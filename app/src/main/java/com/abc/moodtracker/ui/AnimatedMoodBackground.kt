package com.abc.moodtracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import kotlinx.coroutines.delay

data class FloatingEmoji(
    val emoji: String,
    val x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val swayAmount: Float,
    var swayDirection: Float
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun AnimatedMoodBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val emojis = listOf("😊", "😌", "😐", "😔", "😰", "🌟", "💫", "✨")
    val textMeasurer = rememberTextMeasurer()

    var floatingEmojis by remember { mutableStateOf(emptyList<FloatingEmoji>()) }

    // Initialize emojis
    LaunchedEffect(Unit) {
        floatingEmojis = List(BackgroundConfig.emojiCount) {
            FloatingEmoji(
                emoji = emojis.random(),
                x = Random.nextFloat() * 1000f,
                y = Random.nextFloat() * 2000f,
                speed = 0.5f + Random.nextFloat() * 1.2f,
                size = 20f + Random.nextFloat() * 28f,
                alpha = 0.08f + Random.nextFloat() * 0.15f,
                swayAmount = 0.3f + Random.nextFloat() * 1.5f,
                swayDirection = if (Random.nextBoolean()) 1f else -1f
            )
        }
    }

    // Animation loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(20) // ~50 FPS for better performance
            floatingEmojis = floatingEmojis.map { emoji ->
                var newY = emoji.y - emoji.speed
                var newX = emoji.x + (emoji.swayDirection * emoji.swayAmount * 0.08f)
                var newSwayDirection = emoji.swayDirection

                // Change sway direction randomly
                if (Random.nextFloat() < 0.008f) {
                    newSwayDirection *= -1f
                }

                // Reset if off screen
                if (newY < -emoji.size) {
                    newY = 2000f
                    newX = Random.nextFloat() * 1000f
                }

                // Keep within horizontal bounds
                if (newX < -30f || newX > 1030f) {
                    newSwayDirection *= -1f
                    newX = newX.coerceIn(-30f, 1030f)
                }

                emoji.copy(
                    y = newY,
                    x = newX,
                    swayDirection = newSwayDirection
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            floatingEmojis.forEach { floatingEmoji ->
                val textStyle = TextStyle(
                    fontSize = floatingEmoji.size.sp,
                    color = if (isDarkTheme) {
                        Color.White.copy(alpha = floatingEmoji.alpha)
                    } else {
                        Color.Black.copy(alpha = floatingEmoji.alpha * 0.7f)
                    }
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = floatingEmoji.emoji,
                    style = textStyle,
                    topLeft = Offset(floatingEmoji.x - floatingEmoji.size / 2, floatingEmoji.y - floatingEmoji.size / 2)
                )
            }
        }
    }
}

// Background configuration object - FIXED: Removed duplicate function names
object BackgroundConfig {
    var enabled = true
    var emojiCount = 12
    var speedMultiplier = 1.0f

    fun toggleEnabled() {
        enabled = !enabled
    }

    fun updateEmojiCount(count: Int) {
        emojiCount = count.coerceIn(5, 30)
    }

    fun updateSpeedMultiplier(multiplier: Float) {
        speedMultiplier = multiplier.coerceIn(0.1f, 3.0f)
    }
}