package com.abc.moodtracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abc.moodtracker.ui.AppTheme
import com.abc.moodtracker.ui.getMoodCardColor
import com.abc.moodtracker.ui.getMoodTextColor
import com.abc.moodtracker.ui.getMoodEmoji
import com.abc.moodtracker.ui.getMoodDescription
import com.abc.moodtracker.ui.getSelectedMoodColor
import com.abc.moodtracker.ui.getSelectedMoodTextColor

@Composable
fun MoodSelector(
    availableMoods: List<String>,
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    currentTheme: AppTheme = AppTheme.SYSTEM
) {
    var recentlySelected by remember { mutableStateOf<String?>(null) }

    // Reset recently selected after a short delay
    LaunchedEffect(recentlySelected) {
        if (recentlySelected != null) {
            kotlinx.coroutines.delay(300) // Show feedback for 0.3 seconds
            recentlySelected = null
        }
    }

    Column(modifier = modifier) {
        // Mood grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First row: Happy, Calm, Neutral
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                availableMoods.take(3).forEach { mood ->
                    HoverMoodCard(
                        mood = mood,
                        isSelected = mood == selectedMood,
                        isRecentlySelected = mood == recentlySelected,
                        onClick = {
                            println("MOOD_SELECTOR: Card clicked - $mood")
                            onMoodSelected(mood)
                            recentlySelected = mood
                        },
                        modifier = Modifier.weight(1f),
                        isDarkTheme = isDarkTheme,
                        currentTheme = currentTheme
                    )
                }
            }

            // Second row: Sad, Anxious
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                availableMoods.drop(3).take(2).forEach { mood ->
                    HoverMoodCard(
                        mood = mood,
                        isSelected = mood == selectedMood,
                        isRecentlySelected = mood == recentlySelected,
                        onClick = {
                            println("MOOD_SELECTOR: Card clicked - $mood")
                            onMoodSelected(mood)
                            recentlySelected = mood
                        },
                        modifier = Modifier.weight(1f),
                        isDarkTheme = isDarkTheme,
                        currentTheme = currentTheme
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoverMoodCard(
    mood: String,
    isSelected: Boolean,
    isRecentlySelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    currentTheme: AppTheme
) {
    val (emoji, text) = getMoodEmoji(mood)
    val description = getMoodDescription(mood)

    // Animations for selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else if (isRecentlySelected) 0.95f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "mood_card_scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 16f else if (isRecentlySelected) 8f else 4f,
        animationSpec = tween(durationMillis = 300),
        label = "mood_card_elevation"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) -2f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "mood_card_rotation"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
                shadowElevation = elevation
            },
        shape = if (isSelected) CircleShape else MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                // Special colors for selected state
                getSelectedMoodColor(mood, isDarkTheme, currentTheme)
            } else {
                getMoodCardColor(mood, isDarkTheme, currentTheme)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated emoji for selected state
            Text(
                text = if (isSelected) "✨$emoji✨" else emoji,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) {
                    getSelectedMoodTextColor(mood, isDarkTheme, currentTheme)
                } else {
                    getMoodTextColor(mood, isDarkTheme, currentTheme)
                },
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) {
                    getSelectedMoodTextColor(mood, isDarkTheme, currentTheme).copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ThemeSwitcher and other components remain the same...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSwitcher(
    currentTheme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showThemeDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Theme Settings",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Choose Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeOption(
                        title = "Light Mode",
                        description = "Use light theme",
                        icon = Icons.Default.LightMode,
                        isSelected = currentTheme == AppTheme.LIGHT,
                        onClick = {
                            onThemeChanged(AppTheme.LIGHT)
                            showThemeDialog = false
                        }
                    )

                    ThemeOption(
                        title = "Dark Mode",
                        description = "Use dark theme",
                        icon = Icons.Default.DarkMode,
                        isSelected = currentTheme == AppTheme.DARK,
                        onClick = {
                            onThemeChanged(AppTheme.DARK)
                            showThemeDialog = false
                        }
                    )

                    ThemeOption(
                        title = "System Default",
                        description = "Follow system theme",
                        icon = Icons.Default.Settings,
                        isSelected = currentTheme == AppTheme.SYSTEM,
                        onClick = {
                            onThemeChanged(AppTheme.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeOption(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}