package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlphabetItem
import com.example.data.AlphabetRepository
import com.example.ui.AbcGameState
import com.example.ui.LetterCase

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearnAbcScreen(
    state: AbcGameState,
    onSelectLetter: (Int) -> Unit,
    onNextLetter: () -> Unit,
    onPrevLetter: () -> Unit,
    onToggleCase: () -> Unit,
    onPlayPhonics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentItem = AlphabetRepository.alphabetList[state.selectedLetterIndex]
    val itemColor = Color(currentItem.colorHex)
    var isTappedEmoji by remember { mutableStateOf(false) }
    val emojiScale by animateFloatAsState(
        targetValue = if (isTappedEmoji) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { isTappedEmoji = false },
        label = "emojiBounce"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Upper Controls: Case Selector & Quick Jump
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = itemColor.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onToggleCase() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("toggle_case_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (state.letterCase) {
                            LetterCase.BOTH -> "Case: Aa"
                            LetterCase.UPPERCASE -> "Case: A"
                            LetterCase.LOWERCASE -> "Case: a"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = itemColor
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFD166).copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Explored count",
                        tint = Color(0xFFFF9F1C),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.lettersExplored.size} / 26 Learned",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3142)
                    )
                }
            }
        }

        // Main ABC Interactive Flashcard
        AnimatedContent(
            targetState = currentItem,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "flashcard"
        ) { item ->
            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            listOf(itemColor.copy(alpha = 0.6f), itemColor)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("flashcard_${item.letter}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Big Letter Display
                    val letterDisplay = when (state.letterCase) {
                        LetterCase.BOTH -> "${item.letter} ${item.letter.lowercaseChar()}"
                        LetterCase.UPPERCASE -> "${item.letter}"
                        LetterCase.LOWERCASE -> "${item.letter.lowercaseChar()}"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(itemColor.copy(alpha = 0.12f))
                            .padding(horizontal = 28.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = letterDisplay,
                            fontSize = 62.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = itemColor,
                            letterSpacing = 2.sp
                        )
                    }

                    // Interactive Big Emoji with bounce on tap
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .scale(emojiScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(itemColor.copy(alpha = 0.2f), Color.Transparent)
                                )
                            )
                            .clickable {
                                isTappedEmoji = true
                                onPlayPhonics()
                            }
                            .testTag("flashcard_emoji")
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = 80.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Word with first letter emphasized
                    Text(
                        text = item.word,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3142)
                    )

                    // Phonics Sound Button (Speaker)
                    Button(
                        onClick = { onPlayPhonics() },
                        colors = ButtonDefaults.buttonColors(containerColor = itemColor),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(48.dp)
                            .testTag("listen_phonics_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen Phonics",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hear Phonics Sound",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Example Words
                    if (item.exampleWords.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            item.exampleWords.forEach { exWord ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F3F5),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = exWord,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF495057),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Fun Kid Fact
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF9E6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.funFact,
                                fontSize = 13.sp,
                                color = Color(0xFF5D4037),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Navigation Arrows (Prev & Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPrevLetter() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White,
                    contentColor = itemColor
                ),
                modifier = Modifier
                    .size(52.dp)
                    .border(2.dp, itemColor.copy(alpha = 0.4f), CircleShape)
                    .testTag("prev_letter_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Letter",
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Letter ${state.selectedLetterIndex + 1} of 26",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6C757D)
            )

            IconButton(
                onClick = { onNextLetter() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White,
                    contentColor = itemColor
                ),
                modifier = Modifier
                    .size(52.dp)
                    .border(2.dp, itemColor.copy(alpha = 0.4f), CircleShape)
                    .testTag("next_letter_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Letter",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Quick A-Z Alphabet Strip Carousel
        Text(
            text = "Alphabet Strip",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3142),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlphabetRepository.alphabetList.forEachIndexed { index, letterItem ->
                val isSelected = index == state.selectedLetterIndex
                val isExplored = state.lettersExplored.contains(letterItem.letter)
                val badgeColor = Color(letterItem.colorHex)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) badgeColor else badgeColor.copy(alpha = 0.15f)
                        )
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) Color.White else badgeColor.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onSelectLetter(index) }
                        .testTag("alphabet_chip_${letterItem.letter}")
                ) {
                    Text(
                        text = letterItem.letter.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else badgeColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
