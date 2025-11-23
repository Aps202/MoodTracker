package com.abc.moodtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abc.moodtracker.ui.getMoodCardColor
import com.abc.moodtracker.ui.getMoodCardHoverColor
import com.abc.moodtracker.ui.getMoodTextColor
import com.abc.moodtracker.ui.getMoodEmoji

// Add these imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background

@Composable
fun MoodSelector(
    availableMoods: List<String>,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    Column(modifier = modifier) {
        // REMOVE this Text component - it's duplicated in MainScreen
        // Text(
        //     text = "How are you feeling?",
        //     style = MaterialTheme.typography.titleMedium,
        //     modifier = Modifier.padding(bottom = 16.dp),
        //     color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
        // )

        // Beautiful grid layout with interactive mood cards
        EnhancedMoodGrid(
            moods = availableMoods,
            onMoodSelected = onMoodSelected,
            modifier = Modifier.fillMaxWidth(),
            isDarkTheme = isDarkTheme
        )
    }
}

// Enhanced Mood Grid with interactive cards
@Composable
fun EnhancedMoodGrid(
    moods: List<String>,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row: Happy, Calm, Neutral
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            moods.take(3).forEach { mood ->
                FixedSizeMoodCard(
                    mood = mood,
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.weight(1f),
                    isDarkTheme = isDarkTheme
                )
            }
        }

        // Second row: Sad, Anxious (centered)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            moods.drop(3).take(2).forEach { mood ->
                FixedSizeMoodCard(
                    mood = mood,
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.weight(1f),
                    isDarkTheme = isDarkTheme
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

// Fixed Size Mood Card with consistent round rectangle shape and hover effects
@Composable
fun FixedSizeMoodCard(
    mood: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressedState = interactionSource.collectIsPressedAsState()
    val isPressed = pressedState.value

    val (emoji, text) = getMoodEmoji(mood)

    // Get colors based on theme and pressed state
    val cardColor = if (isPressed) {
        getMoodCardHoverColor(mood, isDarkTheme)
    } else {
        getMoodCardColor(mood, isDarkTheme)
    }

    val textColor = if (isPressed) {
        Color.White // White text when pressed for better contrast
    } else {
        getMoodTextColor(mood, isDarkTheme)
    }

    // Always use round rectangle shape, but change corner radius on hover
    val cardShape = if (isPressed) {
        RoundedCornerShape(50) // Circular/oval shape when pressed/hovered
    } else {
        RoundedCornerShape(16.dp) // Round rectangle shape normally
    }

    Card(
        modifier = modifier
            .height(100.dp), // Fixed height for consistent size
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = if (isPressed) {
            CardDefaults.cardElevation(defaultElevation = 8.dp) // Higher elevation when pressed
        } else {
            CardDefaults.cardElevation(defaultElevation = 4.dp)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null // Remove default ripple
                ) { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isPressed) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}