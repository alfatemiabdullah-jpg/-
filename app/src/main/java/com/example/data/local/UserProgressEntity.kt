package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserProgress

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val streakDays: Int = 3,
    val wordsLearnedCount: Int = 18,
    val quizzesCompleted: Int = 5,
    val totalScore: Int = 240,
    val dailyGoal: Int = 10,
    val todayWordsCount: Int = 6
)

fun UserProgressEntity.toDomainModel(): UserProgress = UserProgress(
    streakDays = streakDays,
    wordsLearnedCount = wordsLearnedCount,
    quizzesCompleted = quizzesCompleted,
    totalScore = totalScore,
    dailyGoal = dailyGoal,
    todayWordsCount = todayWordsCount
)

fun UserProgress.toEntity(): UserProgressEntity = UserProgressEntity(
    id = 1,
    streakDays = streakDays,
    wordsLearnedCount = wordsLearnedCount,
    quizzesCompleted = quizzesCompleted,
    totalScore = totalScore,
    dailyGoal = dailyGoal,
    todayWordsCount = todayWordsCount
)
