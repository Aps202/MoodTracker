package com.abc.moodtracker.repository

import com.abc.moodtracker.database.MoodEntryDao
import com.abc.moodtracker.database.MoodEntryEntity
import com.abc.moodtracker.data.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoodRepository(private val moodEntryDao: MoodEntryDao) {

    val allEntries: Flow<List<MoodEntry>> =
        moodEntryDao.getAllEntries().map { entities ->
            entities.map { it.toMoodEntry() }
        }

    suspend fun insertEntry(moodEntry: MoodEntry) {
        moodEntryDao.insertEntry(moodEntry.toEntity())
    }

    suspend fun updateEntry(moodEntry: MoodEntry) {
        moodEntryDao.updateEntry(moodEntry.toEntity())
    }

    suspend fun deleteEntry(moodEntry: MoodEntry) {
        moodEntryDao.deleteEntry(moodEntry.toEntity())
    }

    suspend fun getEntryCount(): Int {
        return moodEntryDao.getEntryCount()
    }
}

// Extension function to convert from domain model to entity
fun MoodEntry.toEntity(): MoodEntryEntity {
    return MoodEntryEntity(
        id = id,
        mood = mood,
        timestamp = timestamp,
        notes = notes
    )
}