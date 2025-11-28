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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    onNavigateBack: () -> Unit,
    viewModelFactory: MoodViewModelFactory
) {
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
    val moodEntries = viewModel.moodEntries.value

    // Calculate weekly data
    val weeklyData = remember(moodEntries) {
        calculateWeeklyMoodData(moodEntries)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weekly Report",
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
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Weekly Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Last 7 Days Mood Distribution",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val totalEntries = weeklyData.values.sum()
                    Text(
                        text = "Total entries: $totalEntries",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Chart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Mood Frequency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (weeklyData.values.sum() > 0) {
                        // Bar Chart
                        MoodBarChart(weeklyData = weeklyData)
                    } else {
                        // Empty state for chart
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📊",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "No data for the last 7 days",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add some moods to see your weekly report!",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood Statistics
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mood Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (weeklyData.values.sum() > 0) {
                        weeklyData.forEach { (moodName, count) ->
                            if (count > 0) {
                                MoodStatItem(
                                    moodName = moodName,
                                    count = count,
                                    total = weeklyData.values.sum()
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No mood data available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons - Fixed without weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth(0.5f) // Use fillMaxWidth with fraction instead of weight
                ) {
                    Text("Back to Main")
                }

                Button(
                    onClick = {
                        // Refresh data - data is already reactive through ViewModel
                    },
                    modifier = Modifier.fillMaxWidth(1f), // Use fillMaxWidth with fraction
                    enabled = false // Since data is auto-refreshed
                ) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
fun MoodStatItem(moodName: String, count: Int, total: Int) {
    val percentage = if (total > 0) (count * 100) / total else 0
    val (emoji, text) = extractMoodComponentsByText(moodName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 12.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.4f) // Use fillMaxWidth with fraction
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$count times ($percentage%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Simple progress bar
        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f) // Use fillMaxWidth with fraction
                .height(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE0E0E0)
            )
        ) {
            // Progress fill
            Card(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getMoodChartColor(moodName)
                )
            ) {}
        }
    }
}

@Composable
fun MoodBarChart(weeklyData: Map<String, Int>) {
    val maxCount = weeklyData.values.maxOrNull() ?: 1
    val chartHeight = 200.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 40.dp) // Extra space for labels
    ) {
        // Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEach { (moodName, count) ->
                val (emoji, text) = extractMoodComponentsByText(moodName)
                BarChartColumn(
                    moodName = text,
                    emoji = emoji,
                    count = count,
                    maxCount = maxCount,
                    chartHeight = chartHeight
                )
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyData.keys.forEach { moodName ->
                val (emoji, text) = extractMoodComponentsByText(moodName)
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.2f) // Use fillMaxWidth with fraction
                )
            }
        }
    }
}

@Composable
fun BarChartColumn(
    moodName: String,
    emoji: String,
    count: Int,
    maxCount: Int,
    chartHeight: androidx.compose.ui.unit.Dp
) {
    val barHeight = if (maxCount > 0) {
        (count.toFloat() / maxCount.toFloat()) * chartHeight.value
    } else {
        0f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(0.2f) // Use fillMaxWidth with fraction instead of weight
    ) {
        // Count label
        Text(
            text = if (count > 0) count.toString() else "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bar
        Card(
            modifier = Modifier
                .width(24.dp)
                .height(barHeight.dp),
            colors = CardDefaults.cardColors(
                containerColor = getMoodChartColor(moodName)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {}

        Spacer(modifier = Modifier.height(4.dp))

        // Mood label (emoji)
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Calculate weekly mood data
fun calculateWeeklyMoodData(entries: List<MoodEntry>): Map<String, Int> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -7) // Last 7 days
    val oneWeekAgo = calendar.time

    // Filter entries from last 7 days
    val recentEntries = entries.filter { entry ->
        val entryDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).parse(entry.timestamp)
        entryDate != null && entryDate.after(oneWeekAgo)
    }

    // Count moods
    val moodCounts = mutableMapOf<String, Int>()
    val availableMoods = listOf("Happy 😊", "Calm 🙂", "Neutral 😐", "Sad 😟", "Anxious 😬")

    // Initialize all moods with 0
    availableMoods.forEach { mood ->
        moodCounts[extractMoodComponents(mood).second] = 0
    }

    // Count actual entries
    recentEntries.forEach { entry ->
        val moodText = extractMoodComponents(entry.mood).second
        moodCounts[moodText] = moodCounts.getOrDefault(moodText, 0) + 1
    }

    return moodCounts
}