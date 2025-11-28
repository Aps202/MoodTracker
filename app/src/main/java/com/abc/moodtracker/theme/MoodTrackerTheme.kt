package com.abc.moodtracker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.abc.moodtracker.ui.AppTheme

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6A1B9A),
    secondary = Color(0xFF9C27B0),
    tertiary = Color(0xFFBA68C8),
    background = Color(0xFFFFFFFF), // White background for Light theme
    surface = Color(0xFFFFFFFF), // White surface for Light theme
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
)

private val SystemLightColorScheme = lightColorScheme(
    primary = Color(0xFF6A1B9A),
    secondary = Color(0xFF9C27B0),
    tertiary = Color(0xFFBA68C8),
    background = Color(0xFFE3F2FD), // Light blue background for System default (light mode)
    surface = Color(0xFFE3F2FD), // Light blue surface for System default (light mode)
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCE93D8),
    tertiary = Color(0xFFE1BEE7),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
)

@Composable
fun MoodTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    currentTheme: AppTheme = AppTheme.SYSTEM,
    content: @Composable () -> Unit
) {
    val shouldUseDarkTheme = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> darkTheme
    }

    val colorScheme = when {
        shouldUseDarkTheme -> DarkColorScheme
        currentTheme == AppTheme.SYSTEM && !darkTheme -> SystemLightColorScheme // System default in light mode
        else -> LightColorScheme // Explicit Light theme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}