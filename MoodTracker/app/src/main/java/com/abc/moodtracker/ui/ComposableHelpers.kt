package com.abc.moodtracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Extract emoji and text from mood string
fun extractMoodComponents(mood: String): Pair<String, String> {
    val parts = mood.split(" ")
    return if (parts.size >= 2) {
        Pair(parts.last(), parts.dropLast(1).joinToString(" "))
    } else {
        Pair("", mood)
    }
}

// Get text color based on mood for better visual consistency
fun getMoodTextColor(mood: String): Color {
    return when {
        mood.contains("Happy") -> Color(0xFF2E7D32)
        mood.contains("Calm") -> Color(0xFF1565C0)
        mood.contains("Neutral") -> Color(0xFF424242)
        mood.contains("Sad") -> Color(0xFF6A1B9A)
        mood.contains("Anxious") -> Color(0xFFEF6C00)
        else -> Color(0xFF000000)
    }
}

// Get card background color for each mood
@Composable
fun getMoodCardColor(mood: String): Color {
    return when {
        mood.contains("Happy") -> if (isDarkTheme()) Color(0xFF1B5E20) else Color(0xFFE8F5E8)
        mood.contains("Calm") -> if (isDarkTheme()) Color(0xFF0D47A1) else Color(0xFFE3F2FD)
        mood.contains("Neutral") -> if (isDarkTheme()) Color(0xFF424242) else Color(0xFFF5F5F5)
        mood.contains("Sad") -> if (isDarkTheme()) Color(0xFF4A148C) else Color(0xFFF3E5F5)
        mood.contains("Anxious") -> if (isDarkTheme()) Color(0xFFE65100) else Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.surface
    }
}

// Helper function to check if dark theme is active
@Composable
fun isDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.background == androidx.compose.material3.darkColorScheme().background
}

// Get color for chart bars
@Composable
fun getMoodChartColor(mood: String): Color {
    return when {
        mood.contains("Happy") -> Color(0xFF4CAF50)
        mood.contains("Calm") -> Color(0xFF2196F3)
        mood.contains("Neutral") -> Color(0xFF9E9E9E)
        mood.contains("Sad") -> Color(0xFF673AB7)
        mood.contains("Anxious") -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
}

// Helper function to extract emoji from mood text
fun extractMoodComponentsByText(moodText: String): Pair<String, String> {
    return when (moodText) {
        "Happy" -> Pair("😊", "Happy")
        "Calm" -> Pair("🙂", "Calm")
        "Neutral" -> Pair("😐", "Neutral")
        "Sad" -> Pair("😟", "Sad")
        "Anxious" -> Pair("😬", "Anxious")
        else -> Pair("", moodText)
    }
}