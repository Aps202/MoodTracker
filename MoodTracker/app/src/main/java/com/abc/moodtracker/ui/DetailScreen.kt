package com.abc.moodtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun DetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    viewModelFactory: MoodViewModelFactory
) {
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
    val moodEntry = viewModel.moodEntries.value.find { it.id == entryId }
    var currentEntry by remember { mutableStateOf(moodEntry) }
    var notes by remember { mutableStateOf(moodEntry?.notes ?: "") }
    var selectedMood by remember { mutableStateOf(moodEntry?.mood ?: "Neutral 😐") }

    // Update local state when moodEntry changes
    LaunchedEffect(moodEntry) {
        currentEntry = moodEntry
        notes = moodEntry?.notes ?: ""
        selectedMood = moodEntry?.mood ?: "Neutral 😐"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mood Details",
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
                            currentEntry?.let { entry ->
                                val updatedEntry = entry.copy(
                                    mood = selectedMood,
                                    notes = notes
                                )
                                viewModel.updateMoodEntry(updatedEntry)
                            }
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (currentEntry == null) {
                // Entry not found
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Entry not found",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            } else {
                // Mood Selection Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Change Mood",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.availableMoods.forEach { mood ->
                                Button(
                                    onClick = { selectedMood = mood },
                                    modifier = Modifier.weight(1f),
                                    colors = if (mood == selectedMood) {
                                        // Highlight selected mood
                                        androidx.compose.material3.ButtonDefaults.buttonColors(
                                            containerColor = getMoodButtonColor(mood),
                                            contentColor = Color.White
                                        )
                                    } else {
                                        // Default for unselected
                                        androidx.compose.material3.ButtonDefaults.buttonColors()
                                    }
                                ) {
                                    Text(
                                        text = mood.split(" ").last(), // Just the emoji
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Mood Display
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (emoji, text) = extractMoodComponents(selectedMood)
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Current Mood: $text",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = getMoodTextColor(selectedMood)
                            )
                            Text(
                                text = currentEntry?.timestamp ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Section
                Card(
                    modifier = Modifier.fillMaxWidth()
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
                                Column {
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
                        currentEntry?.let { entry ->
                            val updatedEntry = entry.copy(
                                mood = selectedMood,
                                notes = notes
                            )
                            viewModel.updateMoodEntry(updatedEntry)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

// Helper function for mood button colors
@Composable
fun getMoodButtonColor(mood: String): Color {
    return when {
        mood.contains("Happy") -> Color(0xFF4CAF50)
        mood.contains("Calm") -> Color(0xFF2196F3)
        mood.contains("Neutral") -> Color(0xFF9E9E9E)
        mood.contains("Sad") -> Color(0xFF673AB7)
        mood.contains("Anxious") -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
}