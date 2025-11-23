package com.abc.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.abc.moodtracker.database.MoodTrackerDatabase
import com.abc.moodtracker.repository.MoodRepository
import com.abc.moodtracker.ui.AppTheme
import com.abc.moodtracker.ui.MainScreen
import com.abc.moodtracker.theme.MoodTrackerTheme  // Fixed import
import com.abc.moodtracker.viewmodel.MoodViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val database = MoodTrackerDatabase.getDatabase(context)
            val moodRepository = MoodRepository(database.moodEntryDao())
            val viewModelFactory = MoodViewModelFactory(moodRepository)

            var currentTheme by remember { mutableStateOf(AppTheme.SYSTEM) }
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (currentTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme
                else -> isSystemInDarkTheme
            }

            MoodTrackerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onNavigateToDetail = { /* handled in MainScreen */ },
                        onNavigateToWeeklyReport = { /* handled in MainScreen */ },
                        viewModelFactory = viewModelFactory,
                        currentTheme = currentTheme,
                        onThemeChanged = { newTheme -> currentTheme = newTheme },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}