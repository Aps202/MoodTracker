package com.abc.moodtracker.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import com.abc.moodtracker.data.MoodEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import java.util.*

// Remove the AppTheme enum from here since it's in the theme package

// Connection State
data class ConnectionState(
    val isConnected: Boolean = true,
    val isChecking: Boolean = false
)

@Composable
fun rememberConnectionState(): State<ConnectionState> {
    val connectionState = remember { mutableStateOf(ConnectionState()) }

    LaunchedEffect(Unit) {
        flow {
            while (true) {
                val isConnected = true // Replace with actual connection check
                emit(isConnected)
                delay(5000)
            }
        }.collect { isConnected ->
            connectionState.value = ConnectionState(
                isConnected = isConnected,
                isChecking = false
            )
        }
    }
    return connectionState
}

// Error Dialog
@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit, isDarkTheme: Boolean) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Connection Issue")
        },
        text = {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
    )
}

// Loading Progress
@Composable
fun LoadingProgressBar(isLoading: Boolean = true, isDarkTheme: Boolean) {
    if (isLoading) {
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = androidx.compose.ui.Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                Text(
                    "Loading your moods...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Mood Prediction
data class MoodPrediction(
    val predictedMood: String,
    val confidence: Float,
    val trend: String
)

fun predictNextMood(moods: List<String>): MoodPrediction {
    if (moods.isEmpty()) {
        return MoodPrediction("Neutral 😐", 0.5f, "stable")
    }
    val lastMood = moods.last()
    return MoodPrediction(lastMood, 0.7f, "stable")
}

// ============================================================================
// MOOD COLOR SYSTEM - 5 COLORS FOR EACH THEME
// ============================================================================

// Light Theme Colors (5 colors)
val lightHappyColor = Color(0xFF4CAF50)      // Green
val lightCalmColor = Color(0xFF2196F3)       // Blue
val lightNeutralColor = Color(0xFF9E9E9E)    // Gray
val lightSadColor = Color(0xFF607D8B)        // Blue Gray
val lightAnxiousColor = Color(0xFFFF9800)    // Orange

val lightHappyBg = Color(0xFFE8F5E8)         // Light Green
val lightCalmBg = Color(0xFFE3F2FD)          // Light Blue
val lightNeutralBg = Color(0xFFF5F5F5)       // Light Gray
val lightSadBg = Color(0xFFECEFF1)           // Light Blue Gray
val lightAnxiousBg = Color(0xFFFFF3E0)       // Light Orange

// Dark Theme Colors (5 colors)
val darkHappyColor = Color(0xFF66BB6A)       // Light Green
val darkCalmColor = Color(0xFF42A5F5)        // Light Blue
val darkNeutralColor = Color(0xFFBDBDBD)     // Light Gray
val darkSadColor = Color(0xFF78909C)         // Light Blue Gray
val darkAnxiousColor = Color(0xFFFFA726)     // Light Orange

val darkHappyBg = Color(0xFF1B5E20)          // Dark Green
val darkCalmBg = Color(0xFF0D47A1)           // Dark Blue
val darkNeutralBg = Color(0xFF424242)        // Dark Gray
val darkSadBg = Color(0xFF37474F)            // Dark Blue Gray
val darkAnxiousBg = Color(0xFFE65100)        // Dark Orange

// System Default Colors (Material You inspired - 5 colors)
val systemHappyColor = Color(0xFF4CAF50)     // Green
val systemCalmColor = Color(0xFF2196F3)      // Blue
val systemNeutralColor = Color(0xFF9E9E9E)   // Gray
val systemSadColor = Color(0xFF673AB7)       // Deep Purple
val systemAnxiousColor = Color(0xFFFF5722)   // Deep Orange

val systemHappyBg = Color(0xFFE8F5E8)        // Light Green
val systemCalmBg = Color(0xFFE3F2FD)         // Light Blue
val systemNeutralBg = Color(0xFFF5F5F5)      // Light Gray
val systemSadBg = Color(0xFFEDE7F6)          // Light Purple
val systemAnxiousBg = Color(0xFFFBE9E7)      // Light Orange

// Primary Color Functions
@Composable
fun getMoodColor(mood: String, isDarkTheme: Boolean): Color {
    return when {
        mood.contains("Happy") -> if (isDarkTheme) darkHappyColor else systemHappyColor
        mood.contains("Calm") -> if (isDarkTheme) darkCalmColor else systemCalmColor
        mood.contains("Neutral") -> if (isDarkTheme) darkNeutralColor else systemNeutralColor
        mood.contains("Sad") -> if (isDarkTheme) darkSadColor else systemSadColor
        mood.contains("Anxious") -> if (isDarkTheme) darkAnxiousColor else systemAnxiousColor
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun getMoodCardColor(mood: String, isDarkTheme: Boolean): Color {
    return when {
        mood.contains("Happy") -> if (isDarkTheme) darkHappyBg else systemHappyBg
        mood.contains("Calm") -> if (isDarkTheme) darkCalmBg else systemCalmBg
        mood.contains("Neutral") -> if (isDarkTheme) darkNeutralBg else systemNeutralBg
        mood.contains("Sad") -> if (isDarkTheme) darkSadBg else systemSadBg
        mood.contains("Anxious") -> if (isDarkTheme) darkAnxiousBg else systemAnxiousBg
        else -> if (isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
fun getMoodTextColor(mood: String, isDarkTheme: Boolean): Color {
    return when {
        mood.contains("Happy") -> if (isDarkTheme) darkHappyColor else systemHappyColor
        mood.contains("Calm") -> if (isDarkTheme) darkCalmColor else systemCalmColor
        mood.contains("Neutral") -> if (isDarkTheme) darkNeutralColor else systemNeutralColor
        mood.contains("Sad") -> if (isDarkTheme) darkSadColor else systemSadColor
        mood.contains("Anxious") -> if (isDarkTheme) darkAnxiousColor else systemAnxiousColor
        else -> if (isDarkTheme) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun getMoodCardHoverColor(mood: String, isDarkTheme: Boolean): Color {
    return when {
        mood.contains("Happy") -> if (isDarkTheme) Color(0xFF388E3C) else Color(0xFF2E7D32)
        mood.contains("Calm") -> if (isDarkTheme) Color(0xFF1565C0) else Color(0xFF0D47A1)
        mood.contains("Neutral") -> if (isDarkTheme) Color(0xFF616161) else Color(0xFF424242)
        mood.contains("Sad") -> if (isDarkTheme) Color(0xFF455A64) else Color(0xFF37474F)
        mood.contains("Anxious") -> if (isDarkTheme) Color(0xFFF57C00) else Color(0xFFEF6C00)
        else -> MaterialTheme.colorScheme.primary
    }
}

// Mood emoji extraction
fun getMoodEmoji(mood: String): Pair<String, String> {
    return when {
        mood.contains("Happy") -> "😊" to "Happy"
        mood.contains("Calm") -> "😌" to "Calm"
        mood.contains("Neutral") -> "😐" to "Neutral"
        mood.contains("Sad") -> "😔" to "Sad"
        mood.contains("Anxious") -> "😰" to "Anxious"
        else -> "❓" to mood
    }
}

// Weekly Stats
data class WeeklyStats(val total: Int, val happyCount: Int, val averageMood: String)

fun calculateWeeklyStats(entries: List<MoodEntry>): WeeklyStats {
    val recentEntries = entries.takeLast(7)
    val happyCount = recentEntries.count { it.mood.contains("Happy") }
    val averageMood = if (recentEntries.isNotEmpty()) {
        "%.1f".format(recentEntries.size / 7.0)
    } else {
        "0.0"
    }
    return WeeklyStats(
        total = recentEntries.size,
        happyCount = happyCount,
        averageMood = averageMood
    )
}
// Add these functions to MoodUtils.kt

@Composable
fun getMoodDetailButtonColor(mood: String, isDarkTheme: Boolean): Color {
    return getMoodColor(mood, isDarkTheme)
}

fun extractMoodComponents(mood: String): Pair<String, String> {
    return getMoodEmoji(mood)
}