package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordItem
import com.example.ui.MainViewModel
import com.example.ui.components.InteractiveFlashCard
import com.example.ui.components.WordListItemCard
import com.example.ui.theme.IndigoPrimary

@Composable
fun VocabularyScreen(
    viewModel: MainViewModel,
    words: List<WordItem>,
    searchQuery: String,
    selectedLevel: String,
    flashcardIndex: Int,
    modifier: Modifier = Modifier
) {
    var isFlashcardMode by remember { mutableStateOf(false) }

    val levels = listOf("ALL", "A1", "A2", "B1", "B2")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("ابحث عن كلمة بالإنجليزية أو العربية...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vocabulary_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row for Filter Chips & View Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(levels) { level ->
                    val isSelected = selectedLevel == level
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedLevel(level) },
                        label = {
                            Text(
                                text = if (level == "ALL") "الكل" else level,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Mode switch icon button
            IconButton(
                onClick = { isFlashcardMode = !isFlashcardMode },
                modifier = Modifier.testTag("toggle_view_mode")
            ) {
                Icon(
                    imageVector = if (isFlashcardMode) Icons.Filled.List else Icons.Filled.ViewCarousel,
                    contentDescription = "Toggle mode",
                    tint = IndigoPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (words.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FindInPage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لم يتم العثور على كلمات تطابق البحث",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (isFlashcardMode) {
            // Flashcard Carousel Mode
            val currentWord = words.getOrNull(flashcardIndex % words.size) ?: words.first()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "بطاقة ${ (flashcardIndex % words.size) + 1 } من ${words.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                InteractiveFlashCard(
                    word = currentWord,
                    onSpeak = { viewModel.speakText(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { viewModel.prevFlashcard(words.size) },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Previous Card")
                    }

                    Button(
                        onClick = { viewModel.toggleLearned(currentWord) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentWord.isLearned) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (currentWord.isLearned) Icons.Filled.Check else Icons.Filled.School,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (currentWord.isLearned) "تم الحفظ والتعلّم!" else "حدد كمتعلَّمة")
                    }

                    FilledIconButton(
                        onClick = { viewModel.nextFlashcard(words.size) },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Next Card")
                    }
                }
            }
        } else {
            // Standard List View Mode
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(words, key = { it.id }) { word ->
                    WordListItemCard(
                        word = word,
                        onSpeak = { viewModel.speakText(it) },
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onToggleLearned = { viewModel.toggleLearned(it) }
                    )
                }
            }
        }
    }
}
