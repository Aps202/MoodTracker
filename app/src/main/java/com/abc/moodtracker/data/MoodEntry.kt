package com.abc.moodtracker.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodEntry(
    val id: Long = System.currentTimeMillis(),
    val mood: String,
    val timestamp: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
    val notes: String = "",
    val moodValue: Int = getMoodValue(mood)
) {
    companion object {
        fun getMoodValue(mood: String): Int {
            return when {
                mood.contains("Happy") -> 5
                mood.contains("Calm") -> 4
                mood.contains("Neutral") -> 3
                mood.contains("Sad") -> 2
                mood.contains("Anxious") -> 1
                else -> 3
            }
        }
    }
}