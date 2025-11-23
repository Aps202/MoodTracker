package com.abc.moodtracker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MoodTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            MaterialTheme.colorScheme.copy(
                background = androidx.compose.ui.graphics.Color(0xFF121212),
                surface = androidx.compose.ui.graphics.Color(0xFF121212),
                onBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                onSurface = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
            )
        } else {
            MaterialTheme.colorScheme
        },
        content = content
    )
}