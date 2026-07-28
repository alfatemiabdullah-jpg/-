package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class, PhraseEntity::class, UserProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EnglishDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun phraseDao(): PhraseDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: EnglishDatabase? = null

        fun getInstance(context: Context): EnglishDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnglishDatabase::class.java,
                    "english_master.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
