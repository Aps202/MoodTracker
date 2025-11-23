package com.abc.moodtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.abc.moodtracker.data.MoodEntry
import com.abc.moodtracker.repository.MoodRepository

class MoodViewModel(private val repository: MoodRepository) : ViewModel() {

    val availableMoods = listOf("Happy 😊", "Calm 🙂", "Neutral 😐", "Sad 😟", "Anxious 😬")

    private val _moodEntries = mutableStateOf(emptyList<MoodEntry>())
    val moodEntries: State<List<MoodEntry>> = _moodEntries

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                _moodEntries.value = entries
                _isLoading.value = false
                println("DEBUG: Loaded ${entries.size} mood entries") // Debug log
            }
        }
    }

    fun addMoodEntry(mood: String) {
        viewModelScope.launch {
            println("DEBUG: Adding mood entry: $mood") // Debug log
            val newEntry = MoodEntry(mood = mood)
            repository.insertEntry(newEntry)
            println("DEBUG: Mood entry added successfully") // Debug log
        }
    }

    fun updateMoodEntry(updatedEntry: MoodEntry) {
        viewModelScope.launch {
            repository.updateEntry(updatedEntry)
        }
    }

    fun deleteMoodEntry(moodEntry: MoodEntry) {
        viewModelScope.launch {
            repository.deleteEntry(moodEntry)
        }
    }
}

// ViewModel Factory
class MoodViewModelFactory(private val repository: MoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}