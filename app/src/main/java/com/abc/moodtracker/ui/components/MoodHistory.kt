package com.abc.moodtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.ui.AppTheme
import com.abc.moodtracker.ui.getMoodTextColor
import com.abc.moodtracker.ui.getMoodEmoji
import androidx.compose.foundation.clickable

@Composable
fun MoodHistory(
    moodEntries: List<MoodEntry>,
    onNavigateToDetail: (Long) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    currentTheme: AppTheme = AppTheme.SYSTEM
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (moodEntries.isEmpty()) {
            EmptyHistoryState(isDarkTheme = isDarkTheme)
        } else {
            LazyColumn {
                items(moodEntries) { entry ->
                    EnhancedMoodEntryItem(
                        moodEntry = entry,
                        onDelete = { onDeleteEntry(entry) },
                        onClick = { onNavigateToDetail(entry.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        isDarkTheme = isDarkTheme,
                        currentTheme = currentTheme
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedMoodEntryItem(
    moodEntry: MoodEntry,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    currentTheme: AppTheme = AppTheme.SYSTEM
) {
    val surfaceVariantColor = if (isDarkTheme) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariantColor = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceVariantColor
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
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (emoji, text) = getMoodEmoji(moodEntry.mood)
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = getMoodTextColor(moodEntry.mood, isDarkTheme, currentTheme)
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = moodEntry.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor
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

@Composable
private fun EmptyHistoryState(isDarkTheme: Boolean) {
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
            color = onSurfaceVariantColor,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}