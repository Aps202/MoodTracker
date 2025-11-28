package com.abc.moodtracker.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.viewmodel.MoodViewModel
import com.abc.moodtracker.viewmodel.MoodViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    entryId: Long?,
    onNavigateBack: () -> Unit,
    viewModelFactory: MoodViewModelFactory,
    isDarkTheme: Boolean,
    currentTheme: AppTheme = AppTheme.SYSTEM
) {
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    var currentEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var notes by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Neutral") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(entryId) {
        isLoading = true
        if (entryId != null) {
            currentEntry = viewModel.moodEntries.value.find { it.id == entryId }

            if (currentEntry == null) {
                viewModel.getEntryById(entryId)?.let { entry ->
                    currentEntry = entry
                }
            }

            currentEntry?.let { entry ->
                notes = entry.notes
                selectedMood = entry.mood
            }
        } else {
            currentEntry = null
            notes = ""
            selectedMood = "Neutral"
        }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Add animated background
        AnimatedMoodBackground(
            isDarkTheme = isDarkTheme,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (currentEntry != null) "Edit Mood" else "New Mood",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (currentEntry != null) {
                                        val updatedEntry = currentEntry!!.copy(
                                            mood = selectedMood,
                                            notes = notes
                                        )
                                        viewModel.updateMoodEntry(updatedEntry)
                                    } else {
                                        viewModel.addMoodEntry(selectedMood, notes)
                                    }
                                    onNavigateBack()
                                }
                            },
                            enabled = selectedMood.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save"
                            )
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    currentEntry?.let { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Original Entry",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Created: ${entry.timestamp}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Mood Selection Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Select Mood",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Mood grid
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // First row: Happy, Calm, Neutral
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    viewModel.availableMoods.take(3).forEach { mood ->
                                        MoodSelectionCard(
                                            mood = mood,
                                            isSelected = mood == selectedMood,
                                            onClick = { selectedMood = mood },
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
                                    viewModel.availableMoods.drop(3).take(2).forEach { mood ->
                                        MoodSelectionCard(
                                            mood = mood,
                                            isSelected = mood == selectedMood,
                                            onClick = { selectedMood = mood },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Journal Notes",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            BasicTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        innerTextField()
                                        if (notes.isEmpty()) {
                                            Text(
                                                text = "How are you feeling? Add some notes...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (currentEntry != null) {
                                    val updatedEntry = currentEntry!!.copy(
                                        mood = selectedMood,
                                        notes = notes
                                    )
                                    viewModel.updateMoodEntry(updatedEntry)
                                } else {
                                    viewModel.addMoodEntry(selectedMood, notes)
                                }
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (currentEntry != null) "Save Changes" else "Save Mood Entry",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodSelectionCard(
    mood: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    currentTheme: AppTheme
) {
    val (emoji, text) = getMoodEmoji(mood)

    // Animation for selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "detail_mood_card_scale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .height(80.dp)
            .scale(scale),
        shape = if (isSelected) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                getSelectedMoodColor(mood, isDarkTheme, currentTheme)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSelected) "✨$emoji✨" else emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else getMoodTextColor(mood, isDarkTheme, currentTheme),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}