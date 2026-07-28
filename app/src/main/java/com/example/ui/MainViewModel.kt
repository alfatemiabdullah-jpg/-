package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EnglishDatabase
import com.example.data.model.*
import com.example.data.remote.GeminiService
import com.example.data.repository.EnglishRepository
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EnglishRepository
    private val ttsHelper: TextToSpeechHelper = TextToSpeechHelper(application)

    val allWords: StateFlow<List<WordItem>>
    val bookmarkedWords: StateFlow<List<WordItem>>
    val learnedWords: StateFlow<List<WordItem>>
    val allPhrases: StateFlow<List<PhraseItem>>
    val userProgress: StateFlow<UserProgress>

    // Search and Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLevel = MutableStateFlow("ALL") // ALL, A1, A2, B1, B2
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Flashcard Index state
    private val _currentFlashcardIndex = MutableStateFlow(0)
    val currentFlashcardIndex: StateFlow<Int> = _currentFlashcardIndex.asStateFlow()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _activeChatScenario = MutableStateFlow("GENERAL") // GENERAL, RESTAURANT, TRAVEL, JOB_INTERVIEW
    val activeChatScenario: StateFlow<String> = _activeChatScenario.asStateFlow()

    // Active Quiz State
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizCompleted = MutableStateFlow(false)
    val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted.asStateFlow()

    val grammarTopics: List<GrammarTopic>
    val quizQuestions: List<QuizQuestion>

    init {
        val database = EnglishDatabase.getInstance(application)
        val geminiService = GeminiService()
        repository = EnglishRepository(database, geminiService)

        grammarTopics = repository.grammarTopics
        quizQuestions = repository.quizQuestionsList

        allWords = repository.allWords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        bookmarkedWords = repository.bookmarkedWords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        learnedWords = repository.learnedWords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allPhrases = repository.allPhrases.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        userProgress = repository.userProgress.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress()
        )

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        // Welcome message for AI
        _chatMessages.value = listOf(
            ChatMessage(
                sender = ChatSender.AI,
                textEn = "Hello! I am Teacher Alex 👨‍🏫. Welcome to your English practice session! How are you today?",
                translationAr = "مرحباً! أنا المعلم ألكس. أهلاً بك في جلسة المحادثة بالإنجليزية! كيف حالك اليوم؟"
            )
        )
    }

    // Filtered words flow
    val filteredWords: StateFlow<List<WordItem>> = combine(
        allWords, searchQuery, selectedLevel, selectedCategory
    ) { words, query, level, category ->
        words.filter { word ->
            val matchesQuery = query.isBlank() ||
                    word.english.contains(query, ignoreCase = true) ||
                    word.arabic.contains(query, ignoreCase = true)
            val matchesLevel = level == "ALL" || word.level.equals(level, ignoreCase = true)
            val matchesCategory = category == "ALL" || word.category.equals(category, ignoreCase = true)
            matchesQuery && matchesLevel && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun speakText(text: String) {
        ttsHelper.speak(text)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedLevel(level: String) {
        _selectedLevel.value = level
        _currentFlashcardIndex.value = 0
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        _currentFlashcardIndex.value = 0
    }

    fun toggleBookmark(word: WordItem) {
        viewModelScope.launch {
            repository.toggleBookmark(word.id, !word.isBookmarked)
        }
    }

    fun toggleLearned(word: WordItem) {
        viewModelScope.launch {
            repository.toggleLearned(word.id, !word.isLearned)
        }
    }

    fun nextFlashcard(totalCount: Int) {
        if (totalCount > 0) {
            _currentFlashcardIndex.value = (_currentFlashcardIndex.value + 1) % totalCount
        }
    }

    fun prevFlashcard(totalCount: Int) {
        if (totalCount > 0) {
            _currentFlashcardIndex.value = if (_currentFlashcardIndex.value - 1 < 0) totalCount - 1 else _currentFlashcardIndex.value - 1
        }
    }

    // AI Chat methods
    fun setChatScenario(scenario: String) {
        _activeChatScenario.value = scenario
        val introText = when (scenario) {
            "RESTAURANT" -> "Welcome to the Bistro! I'm your waiter Alex. What would you like to order today?"
            "TRAVEL" -> "Welcome to the Airport Check-In counter! May I please see your passport?"
            "JOB_INTERVIEW" -> "Welcome to the Job Interview! Could you introduce yourself briefly?"
            else -> "Let's chat freely in English! What topic do you have in mind?"
        }
        val introAr = when (scenario) {
            "RESTAURANT" -> "أهلاً بك في المطعم! أنا نادلك ألكس. ماذا تود أن تطلب اليوم؟"
            "TRAVEL" -> "أهلاً بك في مكتب المطار! هل يمكنني رؤية جواز سفرك؟"
            "JOB_INTERVIEW" -> "أهلاً بك في مقابلة العمل! هل يمكنك تقديم نفسك بإيجاز؟"
            else -> "دعنا نتحدث بحرية بالإنجليزية! ما الموضوع الذي يدور في ذهنك؟"
        }
        _chatMessages.value = listOf(
            ChatMessage(
                sender = ChatSender.AI,
                textEn = introText,
                translationAr = introAr
            )
        )
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            sender = ChatSender.USER,
            textEn = userText
        )

        _chatMessages.update { it + userMsg }
        _isAiLoading.value = true

        viewModelScope.launch {
            val response = repository.sendMessageToAi(
                userText = userText,
                history = _chatMessages.value,
                topic = _activeChatScenario.value
            )
            _chatMessages.update { it + response }
            _isAiLoading.value = false
            ttsHelper.speak(response.textEn)
        }
    }

    // Quiz handling
    fun selectQuizOption(index: Int) {
        _selectedOptionIndex.value = index
    }

    fun submitQuizAnswer() {
        val selected = _selectedOptionIndex.value ?: return
        val currentQ = quizQuestions.getOrNull(_currentQuizIndex.value) ?: return

        if (selected == currentQ.correctAnswerIndex) {
            _quizScore.value += 20
        }

        if (_currentQuizIndex.value + 1 < quizQuestions.size) {
            _currentQuizIndex.value += 1
            _selectedOptionIndex.value = null
        } else {
            _isQuizCompleted.value = true
            viewModelScope.launch {
                repository.recordQuizScore(_quizScore.value)
            }
        }
    }

    fun resetQuiz() {
        _currentQuizIndex.value = 0
        _selectedOptionIndex.value = null
        _quizScore.value = 0
        _isQuizCompleted.value = false
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
