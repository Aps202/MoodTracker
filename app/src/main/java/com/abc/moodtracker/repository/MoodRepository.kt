package com.abc.moodtracker.repository

import com.abc.moodtracker.database.MoodEntryDao
import com.abc.moodtracker.database.MoodEntryEntity
import com.abc.moodtracker.data.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoodRepository(private val moodEntryDao: MoodEntryDao) {

    fun getAllEntries(): Flow<List<MoodEntry>> =
        moodEntryDao.getAllEntries().map { entities ->
            entities.map { entity ->
                MoodEntry(
                    id = entity.id,
                    mood = entity.mood,
                    timestamp = entity.timestamp,
                    notes = entity.notes
                )
            }
        }

    suspend fun insertEntry(moodEntry: MoodEntry) {
        moodEntryDao.insertEntry(
            MoodEntryEntity(
                id = moodEntry.id,
                mood = moodEntry.mood,
                timestamp = moodEntry.timestamp,
                notes = moodEntry.notes
            )
        )
    }

    suspend fun updateEntry(moodEntry: MoodEntry) {
        moodEntryDao.updateEntry(
            MoodEntryEntity(
                id = moodEntry.id,
                mood = moodEntry.mood,
                timestamp = moodEntry.timestamp,
                notes = moodEntry.notes
            )
        )
    }

    suspend fun deleteEntry(moodEntry: MoodEntry) {
        moodEntryDao.deleteEntry(
            MoodEntryEntity(
                id = moodEntry.id,
                mood = moodEntry.mood,
                timestamp = moodEntry.timestamp,
                notes = moodEntry.notes
            )
        )
    }

    suspend fun getEntryCount(): Int {
        return moodEntryDao.getEntryCount()
    }
}