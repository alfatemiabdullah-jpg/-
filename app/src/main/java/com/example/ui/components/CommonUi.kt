package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhraseItem
import com.example.data.model.WordItem
import com.example.ui.theme.*

@Composable
fun AudioSpeakButton(
    textToSpeak: String,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    IconButton(
        onClick = { onSpeak(textToSpeak) },
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .testTag("audio_speak_button")
    ) {
        Icon(
            imageVector = Icons.Outlined.VolumeUp,
            contentDescription = "Speak pronunciation",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size((size * 0.55).dp)
        )
    }
}

@Composable
fun LevelChip(level: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (level.uppercase()) {
        "A1" -> LevelA1Color.copy(alpha = 0.15f) to LevelA1Color
        "A2" -> LevelA2Color.copy(alpha = 0.15f) to LevelA2Color
        "B1" -> LevelB1Color.copy(alpha = 0.15f) to LevelB1Color
        "B2" -> LevelB2Color.copy(alpha = 0.15f) to LevelB2Color
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = level,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WordListItemCard(
    word: WordItem,
    onSpeak: (String) -> Unit,
    onToggleBookmark: (WordItem) -> Unit,
    onToggleLearned: (WordItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("word_item_${word.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LevelChip(level = word.level)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = word.english,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AudioSpeakButton(
                        textToSpeak = word.english,
                        onSpeak = onSpeak
                    )
                    IconButton(onClick = { onToggleBookmark(word) }) {
                        Icon(
                            imageVector = if (word.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (word.isBookmarked) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (word.phonetic.isNotBlank()) {
                Text(
                    text = word.phonetic,
                    fontSize = 13.sp,
                    color = IndigoSecondary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Text(
                text = word.arabic,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (word.exampleEn.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = word.exampleEn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = word.exampleAr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { onToggleLearned(word) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (word.isLearned) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = "Mark learned",
                            tint = if (word.isLearned) EmeraldSuccess else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveFlashCard(
    word: WordItem,
    onSpeak: (String) -> Unit,
    onToggleBookmark: (WordItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var rotated by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500),
        label = "card_flip"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (rotated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { rotated = !rotated }
            .testTag("interactive_flashcard")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LevelChip(level = word.level)
                Row {
                    AudioSpeakButton(textToSpeak = word.english, onSpeak = onSpeak)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { onToggleBookmark(word) }) {
                        Icon(
                            imageVector = if (word.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (word.isBookmarked) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Card content
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        // Un-flip content if card is rotated past 90 degrees
                        if (rotation > 90f) {
                            rotationY = 180f
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (rotation <= 90f) {
                    // Front side: English
                    Text(
                        text = word.english,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    if (word.phonetic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = word.phonetic,
                            fontSize = 15.sp,
                            color = IndigoSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "اضغط على البطاقة لرؤية المعنى بالعربية 🔄",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Back side: Arabic & Example
                    Text(
                        text = word.arabic,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = word.exampleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = word.exampleAr,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PhraseCard(
    phrase: PhraseItem,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phrase.english,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (phrase.phonetic.isNotBlank()) {
                    Text(
                        text = "النطق: ${phrase.phonetic}",
                        fontSize = 12.sp,
                        color = IndigoSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = phrase.arabic,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            AudioSpeakButton(textToSpeak = phrase.english, onSpeak = onSpeak)
        }
    }
}
