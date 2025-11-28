package com.abc.moodtracker.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodEntry(
    val id: Long = System.currentTimeMillis(),
    val mood: String,
    val timestamp: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
    val notes: String = "" // Add notes field
)