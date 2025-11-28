package com.abc.moodtracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abc.moodtracker.data.MoodEntry

@Composable
fun MoodHistory(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(text = "Mood History")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(moodEntries) { entry ->
                MoodEntryItem(moodEntry = entry)
            }
        }
    }
}