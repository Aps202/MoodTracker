package com.abc.moodtracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Query("SELECT * FROM mood_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<MoodEntryEntity>>

    @Insert
    suspend fun insertEntry(entry: MoodEntryEntity)

    @Update
    suspend fun updateEntry(entry: MoodEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: MoodEntryEntity)

    @Query("DELETE FROM mood_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Long)

    @Query("SELECT COUNT(*) FROM mood_entries")
    suspend fun getEntryCount(): Int
}