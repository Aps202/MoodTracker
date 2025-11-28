package com.abc.moodtracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoodSelector(
    availableMoods: List<String>,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "How are you feeling?",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            availableMoods.forEach { mood ->
                Button(
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Text(text = mood)
                }
            }
        }
    }
}