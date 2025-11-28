package com.abc.moodtracker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Query("SELECT * FROM mood_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: Long): MoodEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

// Add UserPreferencesDao to the same file
@Dao
interface UserPreferencesDao {

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences)

    @Update
    suspend fun updatePreferences(preferences: UserPreferences)

    @Query("DELETE FROM user_preferences WHERE id = 1")
    suspend fun clearPreferences()
}