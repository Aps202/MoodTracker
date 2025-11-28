package com.abc.moodtracker.ui

import androidx.compose.ui.graphics.Color

// Mood color mapping for normal state
fun getMoodColor(mood: String, isDarkTheme: Boolean, currentTheme: AppTheme): Color {
    return when {
        mood.contains("Happy", ignoreCase = true) -> Color(0xFF4CAF50)
        mood.contains("Calm", ignoreCase = true) -> Color(0xFF2196F3)
        mood.contains("Neutral", ignoreCase = true) -> Color(0xFF9E9E9E)
        mood.contains("Sad", ignoreCase = true) -> Color(0xFF607D8B)
        mood.contains("Anxious", ignoreCase = true) -> Color(0xFFFF9800)
        else -> Color(0xFF6A1B9A)
    }
}

// Card background color for normal state
fun getMoodCardColor(mood: String, isDarkTheme: Boolean, currentTheme: AppTheme): Color {
    return getMoodColor(mood, isDarkTheme, currentTheme).copy(alpha = 0.2f)
}

// Text color for normal state
fun getMoodTextColor(mood: String, isDarkTheme: Boolean, currentTheme: AppTheme): Color {
    return when {
        mood.contains("Happy", ignoreCase = true) -> Color(0xFF2E7D32)
        mood.contains("Calm", ignoreCase = true) -> Color(0xFF1565C0)
        mood.contains("Neutral", ignoreCase = true) -> Color(0xFF424242)
        mood.contains("Sad", ignoreCase = true) -> Color(0xFF37474F)
        mood.contains("Anxious", ignoreCase = true) -> Color(0xFFEF6C00)
        else -> Color(0xFF4A148C)
    }
}

// Vibrant colors for SELECTED state
fun getSelectedMoodColor(mood: String, isDarkTheme: Boolean, currentTheme: AppTheme): Color {
    return when {
        mood.contains("Happy", ignoreCase = true) -> Color(0xFF4CAF50) // Bright Green
        mood.contains("Calm", ignoreCase = true) -> Color(0xFF2196F3) // Bright Blue
        mood.contains("Neutral", ignoreCase = true) -> Color(0xFF9C27B0) // Vibrant Purple
        mood.contains("Sad", ignoreCase = true) -> Color(0xFF607D8B) // Blue Grey
        mood.contains("Anxious", ignoreCase = true) -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF6A1B9A) // Default Purple
    }
}

// Text color for SELECTED state (always white for better contrast)
fun getSelectedMoodTextColor(mood: String, isDarkTheme: Boolean, currentTheme: AppTheme): Color {
    return Color.White
}

// Mood emoji mapping
fun getMoodEmoji(mood: String): Pair<String, String> {
    return when {
        mood.contains("Happy", ignoreCase = true) -> "😊" to "Happy"
        mood.contains("Calm", ignoreCase = true) -> "😌" to "Calm"
        mood.contains("Neutral", ignoreCase = true) -> "😐" to "Neutral"
        mood.contains("Sad", ignoreCase = true) -> "😔" to "Sad"
        mood.contains("Anxious", ignoreCase = true) -> "😰" to "Anxious"
        else -> "❓" to "Unknown"
    }
}

// Mood description
fun getMoodDescription(mood: String): String {
    return when {
        mood.contains("Happy", ignoreCase = true) -> "Feeling great!"
        mood.contains("Calm", ignoreCase = true) -> "Peaceful & relaxed"
        mood.contains("Neutral", ignoreCase = true) -> "Steady as ever"
        mood.contains("Sad", ignoreCase = true) -> "Need some comfort"
        mood.contains("Anxious", ignoreCase = true) -> "Feeling worried"
        else -> "How are you feeling?"
    }
}