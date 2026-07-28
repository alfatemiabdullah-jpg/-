package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProgress
import com.example.data.model.WordItem
import com.example.ui.MainViewModel
import com.example.ui.components.AudioSpeakButton
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    progress: UserProgress,
    words: List<WordItem>,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val wordOfTheDay = remember(words) {
        words.firstOrNull { it.english == "Opportunity" } ?: words.firstOrNull()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_english_banner_1785238016589),
                        contentDescription = "English Master Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "مرحباً بك في English Master 👋",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تحدَّ نفسك اليوم وطوِّر لغتك الإنجليزية بخطوات بسيطة",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Stats Row (Streak, Words, Score)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "يوم متواصل",
                    value = "${progress.streakDays} 🔥",
                    icon = Icons.Filled.LocalFireDepartment,
                    iconTint = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "كلمة متعلمة",
                    value = "${progress.wordsLearnedCount}",
                    icon = Icons.Filled.School,
                    iconTint = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "مجموع النقاط",
                    value = "${progress.totalScore} XP",
                    icon = Icons.Filled.Star,
                    iconTint = IndigoPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Daily Goal Progress
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الهدف اليومي (6 من ${progress.dailyGoal} كلمات)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "60%",
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = IndigoPrimary,
                        trackColor = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Word of the Day Card
        if (wordOfTheDay != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("word_of_day_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "كلمة اليوم (Word of the Day)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AudioSpeakButton(
                                textToSpeak = wordOfTheDay.english,
                                onSpeak = { viewModel.speakText(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = wordOfTheDay.english,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = wordOfTheDay.phonetic,
                            fontSize = 13.sp,
                            color = IndigoSecondary
                        )
                        Text(
                            text = wordOfTheDay.arabic,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "\"${wordOfTheDay.exampleEn}\"",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = wordOfTheDay.exampleAr,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Main Navigation Hub
        item {
            Text(
                text = "الأقسام والأنشطة الرئيسية",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Grid Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard(
                        title = "قاموس الكلمات",
                        subtitle = "بطاقات تفاعلية ومستويات",
                        icon = Icons.Filled.MenuBook,
                        badgeText = "A1-B2",
                        color = IndigoPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTo("VOCABULARY") }
                    )
                    FeatureCard(
                        title = "المعلم الذكي AI",
                        subtitle = "محادثة إنجليزية مباشرة",
                        icon = Icons.Filled.RecordVoiceOver,
                        badgeText = "ذكاء اصطناعي",
                        color = EmeraldSuccess,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTo("AI_TUTOR") }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard(
                        title = "القواعد والعبارات",
                        subtitle = "شرح مبسط وأمثلة",
                        icon = Icons.Filled.Translate,
                        badgeText = "شامل",
                        color = AmberAccent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTo("GRAMMAR") }
                    )
                    FeatureCard(
                        title = "الاختبارات والتحدي",
                        subtitle = "اختبر مستواك الآن",
                        icon = Icons.Filled.Quiz,
                        badgeText = "تفاعلي",
                        color = Color(0xFFEC4899),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTo("QUIZ") }
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
