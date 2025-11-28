package com.abc.moodtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.viewmodel.MoodViewModel
import com.abc.moodtracker.viewmodel.MoodViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWeeklyReport: () -> Unit,
    viewModelFactory: MoodViewModelFactory,
    currentTheme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit
) {
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
    var showThemeDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MoodTracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    // Weekly Report Button
                    IconButton(
                        onClick = onNavigateToWeeklyReport
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Weekly Report",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Theme Selector
                    Box {
                        IconButton(
                            onClick = { showThemeDropdown = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Theme Settings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showThemeDropdown,
                            onDismissRequest = { showThemeDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LightMode,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DarkMode,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
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
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Theme Indicator
            ThemeIndicator(currentTheme = currentTheme)

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced Mood Selector Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Beautiful grid layout with equal sized mood cards
                MoodGrid(
                    moods = viewModel.availableMoods,
                    onMoodSelected = { mood -> viewModel.addMoodEntry(mood) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mood History Section
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Mood History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Empty State
                if (viewModel.moodEntries.value.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn {
                        items(viewModel.moodEntries.value) { entry ->
                            MoodEntryItem(
                                moodEntry = entry,
                                onDelete = { viewModel.deleteMoodEntry(entry) },
                                onClick = { onNavigateToDetail(entry.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Theme Indicator
@Composable
fun ThemeIndicator(currentTheme: AppTheme) {
    val (icon, text, color) = when (currentTheme) {
        AppTheme.LIGHT -> Triple(Icons.Default.LightMode, "Light Mode", Color(0xFFFFB74D))
        AppTheme.DARK -> Triple(Icons.Default.DarkMode, "Dark Mode", Color(0xFF7986CB))
        AppTheme.SYSTEM -> Triple(Icons.Default.Settings, "System Theme", Color(0xFF81C784))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Current: $text",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Beautiful Mood Grid with equal sized cards
@Composable
fun MoodGrid(
    moods: List<String>,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row: Happy, Calm, Neutral
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            moods.take(3).forEach { mood ->
                BeautifulMoodCard(
                    mood = mood,
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Second row: Sad, Anxious
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Add spacing for centering the two items
            Spacer(modifier = Modifier.weight(0.2f))

            moods.drop(3).take(2).forEach { mood ->
                BeautifulMoodCard(
                    mood = mood,
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

// Beautiful Mood Card with equal size and enhanced design
@Composable
fun BeautifulMoodCard(
    mood: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (emoji, text) = extractMoodComponents(mood)
    val cardColor = getMoodCardColor(mood)
    val textColor = getMoodTextColor(mood)

    Box(
        modifier = modifier
            .height(100.dp) // Fixed height for equal sized cards
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Empty State for when no moods are logged
@Composable
fun EmptyState() {
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
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "How are you feeling today? Select a mood above to get started!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Mood Entry Item with theme-aware background, bold mood names, and delete button
@Composable
fun MoodEntryItem(
    moodEntry: MoodEntry,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood emoji and text
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (emoji, text) = extractMoodComponents(moodEntry.mood)
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold // Make mood name bold
                        ),
                        color = getMoodTextColor(moodEntry.mood)
                    )
                    Text(
                        text = moodEntry.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (moodEntry.notes.isNotEmpty()) {
                        Text(
                            text = "📝 Has notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete mood entry",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}