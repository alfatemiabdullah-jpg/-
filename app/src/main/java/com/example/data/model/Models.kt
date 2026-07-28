package com.example.data.model

data class WordItem(
    val id: Int = 0,
    val english: String,
    val arabic: String,
    val phonetic: String,
    val category: String,
    val exampleEn: String,
    val exampleAr: String,
    val isBookmarked: Boolean = false,
    val isLearned: Boolean = false,
    val level: String = "A1" // A1, A2, B1, B2
)

data class PhraseItem(
    val id: Int = 0,
    val english: String,
    val arabic: String,
    val category: String,
    val phonetic: String = ""
)

data class GrammarTopic(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val level: String,
    val summaryAr: String,
    val explanationAr: String,
    val examples: List<Pair<String, String>> // English, Arabic
)

data class QuizQuestion(
    val id: Int,
    val questionTextAr: String,
    val questionTextEn: String? = null,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanationAr: String,
    val type: QuizType = QuizType.TRANSLATE_EN_AR,
    val audioText: String? = null
)

enum class QuizType {
    TRANSLATE_EN_AR,
    TRANSLATE_AR_EN,
    FILL_BLANK,
    LISTENING
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: ChatSender,
    val textEn: String,
    val translationAr: String? = null,
    val correction: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatSender {
    USER, AI
}

data class UserProgress(
    val streakDays: Int = 3,
    val wordsLearnedCount: Int = 18,
    val quizzesCompleted: Int = 5,
    val totalScore: Int = 240,
    val dailyGoal: Int = 10,
    val todayWordsCount: Int = 6
)
