package com.abc.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abc.moodtracker.database.MoodTrackerDatabase
import com.abc.moodtracker.repository.MoodRepository
import com.abc.moodtracker.ui.AppTheme
import com.abc.moodtracker.ui.DetailScreen
import com.abc.moodtracker.ui.MainScreen
import com.abc.moodtracker.ui.WeeklyReportScreen
import com.abc.moodtracker.viewmodel.MoodViewModelFactory

// Color schemes
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF03DAC6),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun MoodTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

@Composable
fun MoodTrackerApp(viewModelFactory: MoodViewModelFactory) {
    var currentTheme by remember { mutableStateOf(AppTheme.SYSTEM) }
    val navController = rememberNavController()

    val darkTheme = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    MoodTrackerTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    MainScreen(
                        onNavigateToDetail = { entryId ->
                            navController.navigate("detail/$entryId")
                        },
                        onNavigateToWeeklyReport = {
                            navController.navigate("weekly-report")
                        },
                        viewModelFactory = viewModelFactory,
                        currentTheme = currentTheme,
                        onThemeChanged = { newTheme ->
                            currentTheme = newTheme
                        }
                    )
                }

                composable("detail/{entryId}") { backStackEntry ->
                    val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull() ?: 0L
                    DetailScreen(
                        entryId = entryId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        viewModelFactory = viewModelFactory
                    )
                }

                composable("weekly-report") {
                    WeeklyReportScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }
}

// Main Activity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val database = MoodTrackerDatabase.getDatabase(this)
            val repository = MoodRepository(database.moodEntryDao())
            val viewModelFactory = MoodViewModelFactory(repository)

            MoodTrackerApp(viewModelFactory)
        }
    }
}