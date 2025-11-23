package com.abc.moodtracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.viewmodel.MoodViewModel
import com.abc.moodtracker.viewmodel.MoodViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// Add this import
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    onNavigateBack: () -> Unit,
    viewModelFactory: MoodViewModelFactory,
    isDarkTheme: Boolean
) {
    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background

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
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            if (viewModel.isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                WeeklyStatsSection(viewModel.moodEntries.value, isDarkTheme)
                MoodFrequencyChart(viewModel.moodEntries.value, isDarkTheme)
                MoodDistributionSection(viewModel.moodEntries.value, isDarkTheme)
            }
        }
    }
}

@Composable
fun WeeklyStatsSection(entries: List<MoodEntry>, isDarkTheme: Boolean) {
    val weeklyData = calculateWeeklyMoodData(entries)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats in a grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Total", weeklyData.totalEntries.toString(), isDarkTheme)
                StatItem("Trend", weeklyData.moodTrend, isDarkTheme)
                StatItem("Avg/Day", "%.1f".format(weeklyData.averageMood), isDarkTheme)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, isDarkTheme: Boolean) {
    Column(
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
            color = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MoodFrequencyChart(entries: List<MoodEntry>, isDarkTheme: Boolean) {
    val weeklyData = calculateWeeklyMoodData(entries)
    val moodFrequency = calculateMoodFrequency(entries)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Frequency (Last 7 Days)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (moodFrequency.isNotEmpty()) {
                // VERTICAL BARS - Changed from horizontal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    moodFrequency.entries.sortedByDescending { it.value }.forEach { (mood, frequency) ->
                        VerticalMoodBar(
                            mood = mood,
                            frequency = frequency,
                            maxFrequency = moodFrequency.values.maxOrNull() ?: 1,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Add labels below bars
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moodFrequency.entries.sortedByDescending { it.value }.forEach { (mood, _) ->
                        val (emoji, moodText) = getMoodEmoji(mood)
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Text(
                    text = "No mood data available for chart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VerticalMoodBar(mood: String, frequency: Int, maxFrequency: Int, isDarkTheme: Boolean) {
    val barColor = getMoodColor(mood, isDarkTheme)
    val barHeight = if (maxFrequency > 0) (frequency.toFloat() / maxFrequency.toFloat()) * 0.8f else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(200.dp)
    ) {
        // Frequency label above bar
        Text(
            text = frequency.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Vertical bar
        Box(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight(barHeight)
                .background(
                    color = barColor,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            // Percentage text inside bar (if there's enough space)
            if (barHeight > 0.3f) {
                Text(
                    text = "${(barHeight * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MoodDistributionSection(entries: List<MoodEntry>, isDarkTheme: Boolean) {
    val weeklyData = calculateWeeklyMoodData(entries)
    val entriesPerDay = weeklyData.entriesPerDay

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Entries Per Day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (entriesPerDay.isNotEmpty()) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val maxEntries = entriesPerDay.values.maxOrNull() ?: 1

                // VERTICAL BARS - Changed from horizontal
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bars container
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEach { day ->
                            val count = entriesPerDay[day] ?: 0
                            VerticalDayBar(day, count, maxEntries, isDarkTheme)
                        }
                    }

                    // Day labels
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        days.forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No data available for daily chart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VerticalDayBar(day: String, count: Int, maxCount: Int, isDarkTheme: Boolean) {
    val barColor = MaterialTheme.colorScheme.secondary
    val barHeight = if (maxCount > 0) (count.toFloat() / maxCount.toFloat()) * 0.8f else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(180.dp)
    ) {
        // Count label above bar
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Vertical bar
        Box(
            modifier = Modifier
                .width(25.dp)
                .fillMaxHeight(barHeight)
                .background(
                    color = barColor,
                    shape = MaterialTheme.shapes.small
                )
        )
    }
}

// Calculate mood frequency for the bar chart
fun calculateMoodFrequency(entries: List<MoodEntry>): Map<String, Int> {
    val weekAgo = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -7)
    }.time

    val weeklyEntries = entries.filter { entry ->
        try {
            val entryDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).parse(entry.timestamp)
            entryDate != null && entryDate.time >= weekAgo.time
        } catch (e: Exception) {
            false
        }
    }

    return weeklyEntries.groupingBy { it.mood }.eachCount()
}

// Enhanced weekly data calculation
fun calculateWeeklyMoodData(entries: List<MoodEntry>): WeeklyMoodData {
    if (entries.isEmpty()) {
        return WeeklyMoodData(
            averageMood = 0.0,
            moodTrend = "No data",
            entriesPerDay = emptyMap(),
            mostFrequentMood = "No data",
            moodDistribution = emptyMap(),
            totalEntries = 0
        )
    }

    // Get last 7 days
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val last7Days = mutableListOf<String>()

    // Generate last 7 days correctly
    for (i in 6 downTo 0) {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.add(Calendar.DAY_OF_YEAR, -i)
        last7Days.add(dateFormat.format(tempCalendar.time))
    }

    // Calculate entries per day
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val entriesPerDayMap = mutableMapOf<String, Int>()

    // Initialize all days with 0
    last7Days.forEach { day ->
        entriesPerDayMap[day] = 0
    }

    // Count entries for each day (last 7 days)
    val weekAgo = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -7)
    }.time

    entries.forEach { entry ->
        try {
            val entryDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).parse(entry.timestamp)
            if (entryDate != null && entryDate.time >= weekAgo.time) {
                val dayName = dayFormat.format(entryDate)
                entriesPerDayMap[dayName] = entriesPerDayMap.getOrDefault(dayName, 0) + 1
            }
        } catch (e: Exception) {
            // Skip entries with invalid dates
        }
    }

    // Calculate mood distribution
    val weeklyEntries = entries.filter { entry ->
        try {
            val entryDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).parse(entry.timestamp)
            entryDate != null && entryDate.time >= weekAgo.time
        } catch (e: Exception) {
            false
        }
    }

    val moodDistribution = weeklyEntries.groupingBy { it.mood }.eachCount()

    // Find most frequent mood
    val mostFrequentMood = moodDistribution.maxByOrNull { it.value }?.key ?: "No data"

    // Determine trend
    val trendValues = entriesPerDayMap.values.toList()
    val trend = if (trendValues.size >= 2) {
        val lastTwo = trendValues.takeLast(2)
        if (lastTwo[1] > lastTwo[0]) "📈 Improving"
        else if (lastTwo[1] < lastTwo[0]) "📉 Declining"
        else "➡️ Stable"
    } else {
        "➡️ Stable"
    }

    return WeeklyMoodData(
        averageMood = if (weeklyEntries.isNotEmpty()) weeklyEntries.size / 7.0 else 0.0,
        moodTrend = trend,
        entriesPerDay = entriesPerDayMap,
        mostFrequentMood = mostFrequentMood,
        moodDistribution = moodDistribution,
        totalEntries = weeklyEntries.size
    )
}

data class WeeklyMoodData(
    val averageMood: Double,
    val moodTrend: String,
    val entriesPerDay: Map<String, Int>,
    val mostFrequentMood: String,
    val moodDistribution: Map<String, Int>,
    val totalEntries: Int
)