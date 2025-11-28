package com.abc.moodtracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val mood: String,
    val timestamp: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
    val notes: String = ""
)

// Add UserPreferences Entity to the same file
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: Int = 1, // Single row for preferences
    val theme: String = "SYSTEM" // "LIGHT", "DARK", "SYSTEM"
)