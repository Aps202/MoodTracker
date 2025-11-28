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
    val notes: String = "" // Add notes field
) {
    fun toMoodEntry(): com.abc.moodtracker.data.MoodEntry {
        return com.abc.moodtracker.data.MoodEntry(
            id = id,
            mood = mood,
            timestamp = timestamp,
            notes = notes
        )
    }
}