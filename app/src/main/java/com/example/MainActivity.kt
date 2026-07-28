package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.EnglishMasterTheme

sealed class NavItem(
    val route: String,
    val titleAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavItem("HOME", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home)
    object Vocabulary : NavItem("VOCABULARY", "الكلمات", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object AiTutor : NavItem("AI_TUTOR", "المعلم الذكي", Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver)
    object Grammar : NavItem("GRAMMAR", "القواعد", Icons.Filled.Translate, Icons.Outlined.Translate)
    object Bookmarks : NavItem("BOOKMARKS", "المفضلة", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnglishMasterTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val viewModel: MainViewModel = viewModel()

    var currentRoute by remember { mutableStateOf("HOME") }

    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()
    val filteredWords by viewModel.filteredWords.collectAsStateWithLifecycle()
    val bookmarkedWords by viewModel.bookmarkedWords.collectAsStateWithLifecycle()
    val learnedWords by viewModel.learnedWords.collectAsStateWithLifecycle()
    val allPhrases by viewModel.allPhrases.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedLevel by viewModel.selectedLevel.collectAsStateWithLifecycle()
    val flashcardIndex by viewModel.currentFlashcardIndex.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val activeChatScenario by viewModel.activeChatScenario.collectAsStateWithLifecycle()

    val quizCurrentIndex by viewModel.currentQuizIndex.collectAsStateWithLifecycle()
    val quizSelectedIndex by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val isQuizCompleted by viewModel.isQuizCompleted.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem.Home,
        NavItem.Vocabulary,
        NavItem.AiTutor,
        NavItem.Grammar,
        NavItem.Bookmarks
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row {
                        Text(
                            text = "English Master",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎓",
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProgress.streakDays}d",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentRoute = item.route },
                        label = {
                            Text(
                                text = item.titleAr,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.titleAr
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "HOME" -> HomeScreen(
                    viewModel = viewModel,
                    progress = userProgress,
                    words = allWords,
                    onNavigateTo = { currentRoute = it }
                )
                "VOCABULARY" -> VocabularyScreen(
                    viewModel = viewModel,
                    words = filteredWords,
                    searchQuery = searchQuery,
                    selectedLevel = selectedLevel,
                    flashcardIndex = flashcardIndex
                )
                "AI_TUTOR" -> AiTutorScreen(
                    viewModel = viewModel,
                    messages = chatMessages,
                    isLoading = isAiLoading,
                    activeScenario = activeChatScenario
                )
                "GRAMMAR" -> GrammarScreen(
                    viewModel = viewModel,
                    grammarTopics = viewModel.grammarTopics,
                    phrases = allPhrases
                )
                "QUIZ" -> QuizScreen(
                    viewModel = viewModel,
                    questions = viewModel.quizQuestions,
                    currentIndex = quizCurrentIndex,
                    selectedIndex = quizSelectedIndex,
                    score = quizScore,
                    isCompleted = isQuizCompleted
                )
                "BOOKMARKS" -> BookmarksScreen(
                    viewModel = viewModel,
                    bookmarkedWords = bookmarkedWords,
                    learnedWords = learnedWords,
                    progress = userProgress
                )
            }
        }
    }
}
