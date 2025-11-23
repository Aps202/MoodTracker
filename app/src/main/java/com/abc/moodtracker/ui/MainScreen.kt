package com.abc.moodtracker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abc.moodtracker.viewmodel.MoodViewModel
import com.abc.moodtracker.viewmodel.MoodViewModelFactory

// Add these imports
import androidx.compose.foundation.background

@Composable
fun MainScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWeeklyReport: () -> Unit,
    viewModelFactory: MoodViewModelFactory,
    currentTheme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)

    // Check internet connection state
    val connectionState by rememberConnectionState()

    // Show loading/error states
    if (viewModel.isLoading.value) {
        LoadingProgressBar(isLoading = true, isDarkTheme = isDarkTheme)
    } else if (!connectionState.isConnected) {
        ErrorDialog(
            message = "No internet connection. Some features may be limited.",
            onDismiss = { /* We'll keep showing this until connection returns */ },
            isDarkTheme = isDarkTheme
        )
    }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainContentScreen(
                viewModel = viewModel,
                onNavigateToDetail = { entryId ->
                    navController.navigate("detail/$entryId")
                },
                onNavigateToWeeklyReport = {
                    navController.navigate("weekly")
                },
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged,
                isDarkTheme = isDarkTheme
            )
        }
        composable("detail/{entryId}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull() ?: 0L
            DetailScreen(
                entryId = entryId,
                onNavigateBack = { navController.popBackStack() },
                viewModelFactory = viewModelFactory,
                isDarkTheme = isDarkTheme
            )
        }
        composable("weekly") {
            WeeklyReportScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModelFactory = viewModelFactory,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)  // Added both experimental APIs
@Composable
fun MainContentScreen(
    viewModel: MoodViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWeeklyReport: () -> Unit,
    currentTheme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit,
    isDarkTheme: Boolean
) {
    val moodPrediction = remember(viewModel.moodEntries.value) {
        predictNextMood(viewModel.moodEntries.value.map { it.mood })
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
    val pagerState = rememberPagerState(pageCount = { 2 })

    // Animated floating moods background
    AnimatedMoodBackground(isDarkTheme = isDarkTheme)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MoodTracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToWeeklyReport) {
                        Icon(Icons.Default.BarChart, "Weekly Report")
                    }

                    var showThemeDropdown by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showThemeDropdown = true }) {
                            Icon(Icons.Default.Settings, "Theme Settings")
                        }

                        if (showThemeDropdown) {
                            DropdownMenu(
                                expanded = showThemeDropdown,
                                onDismissRequest = { showThemeDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                            Text("Light Theme")
                                        }
                                    },
                                    onClick = {
                                        onThemeChanged(AppTheme.LIGHT)
                                        showThemeDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                            Text("Dark Theme")
                                        }
                                    },
                                    onClick = {
                                        onThemeChanged(AppTheme.DARK)
                                        showThemeDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                            Text("System Default")
                                        }
                                    },
                                    onClick = {
                                        onThemeChanged(AppTheme.SYSTEM)
                                        showThemeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.addMoodEntry(moodPrediction.predictedMood)
                },
                icon = { Icon(Icons.Default.Add, "Quick Add") },
                text = { Text("Add ${moodPrediction.predictedMood}") },
                containerColor = getMoodColor(moodPrediction.predictedMood, isDarkTheme)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            EnhancedThemeIndicator(currentTheme = currentTheme, isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> MoodTrackerContent(
                        viewModel = viewModel,
                        moodPrediction = moodPrediction,
                        onNavigateToDetail = onNavigateToDetail,
                        isDarkTheme = isDarkTheme
                    )
                    1 -> WeeklyPreview(viewModel = viewModel, isDarkTheme = isDarkTheme)
                }
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { index ->
                    val color = if (pagerState.currentPage == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color)  // Now this will work
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedThemeIndicator(currentTheme: AppTheme, isDarkTheme: Boolean) {
    val (icon, text, color) = when (currentTheme) {
        AppTheme.LIGHT -> Triple(Icons.Default.LightMode, "Light Mode", Color(0xFFFFB74D))
        AppTheme.DARK -> Triple(Icons.Default.DarkMode, "Dark Mode", Color(0xFF7986CB))
        AppTheme.SYSTEM -> Triple(Icons.Default.Settings, "System Theme", Color(0xFF81C784))
        else -> Triple(Icons.Default.Settings, "Unknown", Color.Gray)
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF2D2D2D) else color.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = "Current: $text",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Main content that was probably in your original MainScreen
@Composable
fun MoodTrackerContent(
    viewModel: MoodViewModel,
    moodPrediction: MoodPrediction,
    onNavigateToDetail: (Long) -> Unit,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PredictionCard(moodPrediction, isDarkTheme)
        Spacer(modifier = Modifier.height(16.dp))
        EnhancedMoodSelectorSection(viewModel, isDarkTheme)
        Spacer(modifier = Modifier.height(24.dp))
        EnhancedMoodHistorySection(viewModel, onNavigateToDetail, isDarkTheme)
    }
}

@Composable
private fun PredictionCard(moodPrediction: MoodPrediction, isDarkTheme: Boolean) {
    val surfaceVariantColor = if (isDarkTheme) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariantColor = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = surfaceVariantColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Prediction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Next mood likely: ${moodPrediction.predictedMood}",
                style = MaterialTheme.typography.bodyMedium,
                color = onSurfaceVariantColor
            )
        }
    }
}

@Composable
private fun EnhancedMoodSelectorSection(
    viewModel: MoodViewModel,
    isDarkTheme: Boolean
) {
    val onSurfaceColor = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = onSurfaceColor
        )

        // Use your existing MoodSelector component
        com.abc.moodtracker.ui.components.MoodSelector(
            availableMoods = viewModel.availableMoods,
            onMoodSelected = { mood -> viewModel.addMoodEntry(mood) },
            modifier = Modifier.fillMaxWidth(),
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
private fun EnhancedMoodHistorySection(
    viewModel: MoodViewModel,
    onNavigateToDetail: (Long) -> Unit,
    isDarkTheme: Boolean
) {
    val onSurfaceColor = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Recent Moods",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = onSurfaceColor
        )

        if (viewModel.moodEntries.value.isEmpty()) {
            EmptyState(isDarkTheme = isDarkTheme)
        } else {
            // Use your existing MoodHistory component
            com.abc.moodtracker.ui.components.MoodHistory(
                moodEntries = viewModel.moodEntries.value.take(10),
                onNavigateToDetail = onNavigateToDetail,
                onDeleteEntry = { entry -> viewModel.deleteMoodEntry(entry) },
                modifier = Modifier.fillMaxWidth(),
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun EmptyState(isDarkTheme: Boolean) {
    val onSurfaceColor = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "No moods logged yet",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = onSurfaceColor
        )
        Text(
            text = "How are you feeling today? Select a mood above to get started!",
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariantColor
        )
    }
}

@Composable
fun WeeklyPreview(viewModel: MoodViewModel, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface
    val onSurfaceColor = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)  // Now this will work
    ) {
        Text(
            text = "Weekly Overview",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            color = onSurfaceColor
        )

        val stats = calculateWeeklyStats(viewModel.moodEntries.value)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = surfaceColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatItemFixed("Total", stats.total.toString(), isDarkTheme)
                    StatItemFixed("Happy", stats.happyCount.toString(), isDarkTheme)
                    StatItemFixed("Avg", stats.averageMood, isDarkTheme)
                }
            }
        }
    }
}

@Composable
private fun StatItemFixed(label: String, value: String, isDarkTheme: Boolean) {
    val onSurfaceColor = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.fillMaxWidth(0.33f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = onSurfaceVariantColor
        )
    }
}