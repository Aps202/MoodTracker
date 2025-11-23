package com.abc.moodtracker.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.random.Random

data class FloatingMood(
    val emoji: String,
    val x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float
)

@Composable
fun AnimatedMoodBackground(isDarkTheme: Boolean) {
    val moods = listOf("😊", "😌", "😐", "😔", "😰")
    var floatingMoodsState by remember {
        mutableStateOf(
            List(15) {
                FloatingMood(
                    emoji = moods.random(),
                    x = Random.nextFloat() * 1000,
                    y = Random.nextFloat() * 2000,
                    speed = 0.5f + Random.nextFloat() * 1.5f,
                    size = 20f + Random.nextFloat() * 40f,
                    alpha = 0.1f + Random.nextFloat() * 0.3f
                )
            }
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        floatingMoodsState = floatingMoodsState.map { mood ->
            var newY = mood.y - mood.speed
            var newX = mood.x

            // Reset if off screen
            if (newY < -mood.size) {
                newY = size.height + mood.size
                newX = Random.nextFloat() * size.width
            }

            drawIntoCanvas { canvas ->
                val nativePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = mood.size
                    alpha = (mood.alpha * 255).toInt()
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                canvas.nativeCanvas.drawText(
                    mood.emoji,
                    newX,
                    newY,
                    nativePaint
                )
            }

            mood.copy(y = newY, x = newX)
        }
    }
}