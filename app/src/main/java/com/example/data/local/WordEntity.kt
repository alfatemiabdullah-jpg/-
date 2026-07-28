package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.WordItem

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val english: String,
    val arabic: String,
    val phonetic: String,
    val category: String,
    val exampleEn: String,
    val exampleAr: String,
    val isBookmarked: Boolean = false,
    val isLearned: Boolean = false,
    val level: String = "A1"
)

fun WordEntity.toDomainModel(): WordItem = WordItem(
    id = id,
    english = english,
    arabic = arabic,
    phonetic = phonetic,
    category = category,
    exampleEn = exampleEn,
    exampleAr = exampleAr,
    isBookmarked = isBookmarked,
    isLearned = isLearned,
    level = level
)

fun WordItem.toEntity(): WordEntity = WordEntity(
    id = id,
    english = english,
    arabic = arabic,
    phonetic = phonetic,
    category = category,
    exampleEn = exampleEn,
    exampleAr = exampleAr,
    isBookmarked = isBookmarked,
    isLearned = isLearned,
    level = level
)
