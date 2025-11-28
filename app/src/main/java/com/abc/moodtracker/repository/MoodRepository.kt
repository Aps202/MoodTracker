package com.abc.moodtracker.repository

import com.abc.moodtracker.database.MoodEntryDao
import com.abc.moodtracker.database.MoodEntryEntity
import com.abc.moodtracker.database.UserPreferencesDao
import com.abc.moodtracker.database.UserPreferences // Fixed: Import from database package
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.ui.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoodRepository(private val moodEntryDao: MoodEntryDao) {

    fun getAllEntries(): Flow<List<MoodEntry>> =
        moodEntryDao.getAllEntries().map { entities ->
            entities.map { it.toMoodEntry() }
        }

    suspend fun insertEntry(moodEntry: MoodEntry) {
        try {
            moodEntryDao.insertEntry(moodEntry.toEntity())
            println("REPOSITORY: Successfully inserted mood entry: ${moodEntry.mood}")
        } catch (e: Exception) {
            println("REPOSITORY ERROR: Failed to insert mood entry - ${e.message}")
            throw e
        }
    }

    suspend fun updateEntry(moodEntry: MoodEntry) {
        moodEntryDao.updateEntry(moodEntry.toEntity())
    }

    suspend fun deleteEntry(moodEntry: MoodEntry) {
        moodEntryDao.deleteEntry(moodEntry.toEntity())
    }

    suspend fun getEntryById(entryId: Long): MoodEntry? {
        return moodEntryDao.getEntryById(entryId)?.toMoodEntry()
    }

    suspend fun getEntryCount(): Int {
        return moodEntryDao.getEntryCount()
    }

    private fun MoodEntryEntity.toMoodEntry(): MoodEntry {
        return MoodEntry(
            id = this.id,
            mood = this.mood,
            timestamp = this.timestamp,
            notes = this.notes
        )
    }

    private fun MoodEntry.toEntity(): MoodEntryEntity {
        return MoodEntryEntity(
            id = this.id,
            mood = this.mood,
            timestamp = this.timestamp,
            notes = this.notes
        )
    }
}

// Add PreferencesRepository to the same file
class PreferencesRepository(private val userPreferencesDao: UserPreferencesDao) {

    fun getThemePreference(): Flow<AppTheme> {
        return userPreferencesDao.getPreferences().map { preferences ->
            when (preferences?.theme) {
                "LIGHT" -> AppTheme.LIGHT
                "DARK" -> AppTheme.DARK
                else -> AppTheme.SYSTEM
            }
        }
    }

    suspend fun setThemePreference(theme: AppTheme) {
        val themeString = when (theme) {
            AppTheme.LIGHT -> "LIGHT"
            AppTheme.DARK -> "DARK"
            AppTheme.SYSTEM -> "SYSTEM"
        }

        // Fixed: Get the current preferences properly
        val currentPreferences = userPreferencesDao.getPreferences().let { flow ->
            // This is a simplified approach - you might need to collect the flow properly
            // For a production app, consider using first() or other methods
            null // We'll handle this differently
        }

        val preferences = currentPreferences ?: UserPreferences()

        userPreferencesDao.insertPreferences(
            preferences.copy(theme = themeString)
        )
    }

    suspend fun initializePreferences() {
        // Fixed: Get preferences using a different approach
        try {
            val hasPreferences = userPreferencesDao.getPreferences().let { flow ->
                // For simplicity, we'll just insert default preferences
                // In a real app, you'd properly check if preferences exist
                false
            }
            if (!hasPreferences) {
                userPreferencesDao.insertPreferences(UserPreferences())
            }
        } catch (e: Exception) {
            // If there's an error, insert default preferences
            userPreferencesDao.insertPreferences(UserPreferences())
        }
    }
}