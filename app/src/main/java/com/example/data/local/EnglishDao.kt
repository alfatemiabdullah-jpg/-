package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY id ASC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isBookmarked = 1")
    fun getBookmarkedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isLearned = 1")
    fun getLearnedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE level = :level")
    fun getWordsByLevel(level: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category")
    fun getWordsByCategory(category: String): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("UPDATE words SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, isBookmarked: Boolean)

    @Query("UPDATE words SET isLearned = :isLearned WHERE id = :id")
    suspend fun updateLearned(id: Int, isLearned: Boolean)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int
}

@Dao
interface PhraseDao {
    @Query("SELECT * FROM phrases ORDER BY id ASC")
    fun getAllPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE category = :category")
    fun getPhrasesByCategory(category: String): Flow<List<PhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrases(phrases: List<PhraseEntity>)

    @Query("SELECT COUNT(*) FROM phrases")
    suspend fun getPhraseCount(): Int
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET totalScore = totalScore + :score, quizzesCompleted = quizzesCompleted + 1 WHERE id = 1")
    suspend fun addQuizResult(score: Int)

    @Query("UPDATE user_progress SET wordsLearnedCount = wordsLearnedCount + 1, todayWordsCount = todayWordsCount + 1 WHERE id = 1")
    suspend fun incrementWordsLearned()
}
