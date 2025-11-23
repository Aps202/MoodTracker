package com.abc.moodtracker.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [MoodEntryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MoodTrackerDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao

    companion object {
        @Volatile
        private var Instance: MoodTrackerDatabase? = null

        fun getDatabase(context: Context): MoodTrackerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MoodTrackerDatabase::class.java,
                    "mood_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}