package com.abc.moodtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.repository.MoodRepository
import com.abc.moodtracker.repository.PreferencesRepository
import com.abc.moodtracker.ui.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val availableMoods = listOf("Happy", "Calm", "Neutral", "Sad", "Anxious")

    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentTheme = MutableStateFlow<AppTheme>(AppTheme.SYSTEM)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    init {
        loadAllEntries()
        loadThemePreference()
        initializePreferences()
    }

    fun addMoodEntry(mood: String, notes: String = "") {
        viewModelScope.launch {
            try {
                // Create new entry
                val newEntry = MoodEntry(
                    id = System.currentTimeMillis(),
                    mood = mood,
                    notes = notes
                )

                // Update UI immediately
                val updatedList = mutableListOf<MoodEntry>()
                updatedList.add(newEntry)
                updatedList.addAll(_moodEntries.value)
                _moodEntries.value = updatedList

                // Save to database in background
                moodRepository.insertEntry(newEntry)

                println("SUCCESS: Mood added - ${newEntry.mood}, Total entries: ${_moodEntries.value.size}")
            } catch (e: Exception) {
                println("ERROR: Failed to add mood - ${e.message}")
                loadAllEntries()
            }
        }
    }

    fun updateMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            try {
                // Update UI immediately
                val updatedList = _moodEntries.value.map {
                    if (it.id == entry.id) entry else it
                }
                _moodEntries.value = updatedList

                // Save to database
                moodRepository.updateEntry(entry)
            } catch (e: Exception) {
                loadAllEntries()
            }
        }
    }

    fun deleteMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            try {
                // Update UI immediately
                val updatedList = _moodEntries.value.filter { it.id != entry.id }
                _moodEntries.value = updatedList

                // Delete from database
                moodRepository.deleteEntry(entry)

                println("SUCCESS: Mood deleted - ${entry.mood}, Remaining entries: ${_moodEntries.value.size}")
            } catch (e: Exception) {
                loadAllEntries()
            }
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            try {
                _currentTheme.value = theme
                preferencesRepository.setThemePreference(theme)
                println("SUCCESS: Theme updated to $theme")
            } catch (e: Exception) {
                println("ERROR: Failed to update theme - ${e.message}")
            }
        }
    }

    suspend fun getEntryById(entryId: Long): MoodEntry? {
        return moodRepository.getEntryById(entryId)
    }

    private fun loadAllEntries() {
        viewModelScope.launch {
            try {
                moodRepository.getAllEntries().collect { entries ->
                    _moodEntries.value = entries
                    _isLoading.value = false
                    println("SUCCESS: Loaded ${entries.size} entries from database")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to load entries - ${e.message}")
                _isLoading.value = false
            }
        }
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            try {
                preferencesRepository.getThemePreference().collect { theme ->
                    _currentTheme.value = theme
                    println("SUCCESS: Loaded theme preference: $theme")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to load theme preference - ${e.message}")
            }
        }
    }

    private fun initializePreferences() {
        viewModelScope.launch {
            try {
                preferencesRepository.initializePreferences()
            } catch (e: Exception) {
                println("ERROR: Failed to initialize preferences - ${e.message}")
            }
        }
    }
}

class MoodViewModelFactory(
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoodViewModel(moodRepository, preferencesRepository) as T
    }
}