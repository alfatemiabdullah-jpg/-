package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PhraseItem

@Entity(tableName = "phrases")
data class PhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val english: String,
    val arabic: String,
    val category: String,
    val phonetic: String = ""
)

fun PhraseEntity.toDomainModel(): PhraseItem = PhraseItem(
    id = id,
    english = english,
    arabic = arabic,
    category = category,
    phonetic = phonetic
)

fun PhraseItem.toEntity(): PhraseEntity = PhraseEntity(
    id = id,
    english = english,
    arabic = arabic,
    category = category,
    phonetic = phonetic
)
