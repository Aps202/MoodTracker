package com.abc.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.abc.moodtracker.database.MoodTrackerDatabase
import com.abc.moodtracker.repository.MoodRepository
import com.abc.moodtracker.repository.PreferencesRepository
import com.abc.moodtracker.theme.MoodTrackerTheme
import com.abc.moodtracker.ui.MainScreen
import com.abc.moodtracker.viewmodel.MoodViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val database = MoodTrackerDatabase.getDatabase(context)
            val moodRepository = MoodRepository(database.moodEntryDao())
            val preferencesRepository = PreferencesRepository(database.userPreferencesDao())
            val viewModelFactory = MoodViewModelFactory(moodRepository, preferencesRepository)

            // Get the current theme from ViewModel
            val viewModel: com.abc.moodtracker.viewmodel.MoodViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = viewModelFactory
            )
            val currentTheme by viewModel.currentTheme.collectAsState()

            val isSystemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (currentTheme) {
                com.abc.moodtracker.ui.AppTheme.LIGHT -> false
                com.abc.moodtracker.ui.AppTheme.DARK -> true
                com.abc.moodtracker.ui.AppTheme.SYSTEM -> isSystemInDarkTheme
            }

            MoodTrackerTheme(
                darkTheme = isDarkTheme,
                currentTheme = currentTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        viewModelFactory = viewModelFactory,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}