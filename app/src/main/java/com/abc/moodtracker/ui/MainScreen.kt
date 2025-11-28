package com.abc.moodtracker.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abc.moodtracker.viewmodel.MoodViewModel
import com.abc.moodtracker.viewmodel.MoodViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModelFactory: MoodViewModelFactory,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)

    // Observe theme state
    val currentTheme by viewModel.currentTheme.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Add animated background to the entire main screen
        AnimatedMoodBackground(
            isDarkTheme = isDarkTheme,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
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
                        // Theme switcher
                        com.abc.moodtracker.ui.components.ThemeSwitcher(
                            currentTheme = currentTheme,
                            onThemeChanged = { newTheme ->
                                viewModel.updateTheme(newTheme)
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        // Weekly report button
                        IconButton(onClick = {
                            navController.navigate("weekly")
                        }) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Weekly Report"
                            )
                        }
                    }
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "main",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("main") {
                    InteractiveMainContent(
                        viewModel = viewModel,
                        onNavigateToDetail = { entryId ->
                            navController.navigate("detail/$entryId")
                        },
                        currentTheme = currentTheme,
                        isDarkTheme = isDarkTheme
                    )
                }
                composable("detail/{entryId}") { backStackEntry ->
                    val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull() ?: 0L
                    DetailScreen(
                        entryId = entryId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModelFactory = viewModelFactory,
                        isDarkTheme = isDarkTheme,
                        currentTheme = currentTheme
                    )
                }
                composable("weekly") {
                    WeeklyReportScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModelFactory = viewModelFactory,
                        isDarkTheme = isDarkTheme,
                        currentTheme = currentTheme
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveMainContent(
    viewModel: MoodViewModel,
    onNavigateToDetail: (Long) -> Unit,
    currentTheme: AppTheme,
    isDarkTheme: Boolean
) {
    val moodEntries by viewModel.moodEntries.collectAsState()

    // State for interactive greeting
    var showWelcome by remember { mutableStateOf(true) }
    var currentGreetingIndex by remember { mutableStateOf(0) }
    var pulseAnimation by remember { mutableStateOf(false) }
    var recentlyLoggedMood by remember { mutableStateOf(false) }

    val greetings = listOf(
        "How are you feeling today? 🌟",
        "What's your mood right now? 💫",
        "How's your heart feeling? 💝",
        "Ready to check in? 📝",
        "Let's capture this moment! ✨"
    )

    // Auto-cycle through greetings when welcome is shown
    LaunchedEffect(showWelcome) {
        while (showWelcome) {
            delay(4000) // Change greeting every 4 seconds
            currentGreetingIndex = (currentGreetingIndex + 1) % greetings.size
            pulseAnimation = true
            delay(200)
            pulseAnimation = false
        }
    }

    // Reset welcome after mood logging
    LaunchedEffect(recentlyLoggedMood) {
        if (recentlyLoggedMood) {
            delay(3000) // Wait 3 seconds
            showWelcome = true
            recentlyLoggedMood = false
        }
    }

    // Pulse animation for the greeting
    val greetingAlpha by animateFloatAsState(
        targetValue = if (pulseAnimation) 0.7f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "greeting_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Interactive Greeting Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated greeting text
                Text(
                    text = greetings[currentGreetingIndex],
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(greetingAlpha)
                        .padding(bottom = 8.dp)
                )

                // Subtitle with emoji
                Text(
                    text = "Tap a mood below to get started! 🎯",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                // Quick stats if there are entries
                if (moodEntries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "📊 You've logged ${moodEntries.size} mood${if (moodEntries.size != 1) "s" else ""} so far!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Mood Selector
        com.abc.moodtracker.ui.components.MoodSelector(
            availableMoods = viewModel.availableMoods,
            selectedMood = null,
            onMoodSelected = { mood ->
                println("UI: Mood selector clicked - $mood")
                viewModel.addMoodEntry(mood)
                // Show feedback that mood was logged
                showWelcome = false
                recentlyLoggedMood = true
            },
            modifier = Modifier.fillMaxWidth(),
            isDarkTheme = isDarkTheme,
            currentTheme = currentTheme
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mood History with enhanced header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Enhanced history header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Your Mood Journey",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Entry count badge
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = "${moodEntries.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                com.abc.moodtracker.ui.components.MoodHistory(
                    moodEntries = moodEntries,
                    onNavigateToDetail = onNavigateToDetail,
                    onDeleteEntry = { entry ->
                        println("UI: Delete entry clicked - ${entry.mood}")
                        viewModel.deleteMoodEntry(entry)
                    },
                    modifier = Modifier.fillMaxSize(),
                    isDarkTheme = isDarkTheme,
                    currentTheme = currentTheme
                )
            }
        }

        // Quick tip at the bottom (only shows when there are few entries)
        if (moodEntries.size < 3) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tip",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tip: Log your mood daily to track patterns!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}