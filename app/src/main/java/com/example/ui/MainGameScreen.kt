package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BubblePopScreen
import com.example.ui.screens.LearnAbcScreen
import com.example.ui.screens.LetterTraceScreen
import com.example.ui.screens.PictureMatchScreen
import com.example.ui.screens.StickerAlbumScreen

data class NavTabItem(
    val tab: GameTab,
    val label: String,
    val icon: ImageVector,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameScreen(
    viewModel: AbcGameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val navTabs = listOf(
        NavTabItem(GameTab.LEARN, "Learn", Icons.Default.AutoStories, "📚"),
        NavTabItem(GameTab.BUBBLE_POP, "Pop Game", Icons.Outlined.BubbleChart, "🫧"),
        NavTabItem(GameTab.MATCH, "Match", Icons.Default.Extension, "🧩"),
        NavTabItem(GameTab.TRACE, "Trace", Icons.Default.Brush, "✏️"),
        NavTabItem(GameTab.STICKERS, "Rewards", Icons.Default.MilitaryTech, "🏆")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ABC Kids Game",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = Color(0xFF2D3142)
                        )
                    }
                },
                actions = {
                    // Star Counter Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFD166).copy(alpha = 0.3f),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.soundManager.playStarSound()
                                viewModel.soundManager.speak("You have ${state.stars} golden stars!")
                            }
                            .testTag("stars_counter_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFF9F1C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${state.stars}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF854D0E)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFDF5)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                navTabs.forEach { tabItem ->
                    val isSelected = state.currentTab == tabItem.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tabItem.tab) },
                        icon = {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = tabItem.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tabItem.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF118AB2),
                            selectedTextColor = Color(0xFF118AB2),
                            indicatorColor = Color(0xFFE0F7FA),
                            unselectedIconColor = Color(0xFF8E9AAF),
                            unselectedTextColor = Color(0xFF8E9AAF)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tabItem.tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFFDF5)),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 650.dp)
            ) {
                AnimatedContent(
                    targetState = state.currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabSwitch"
                ) { tab ->
                    when (tab) {
                        GameTab.LEARN -> {
                            LearnAbcScreen(
                                state = state,
                                onSelectLetter = { viewModel.selectLetter(it) },
                                onNextLetter = { viewModel.nextLetter() },
                                onPrevLetter = { viewModel.prevLetter() },
                                onToggleCase = { viewModel.toggleLetterCase() },
                                onPlayPhonics = { viewModel.playCurrentPhonics() }
                            )
                        }

                        GameTab.BUBBLE_POP -> {
                            BubblePopScreen(
                                state = state,
                                onBubbleTapped = { viewModel.onBubbleTapped(it) },
                                onNewRound = { viewModel.startBubbleRound() },
                                onHearPrompt = {
                                    viewModel.soundManager.speak("Pop the letter ${state.bubbleTargetLetter}!")
                                }
                            )
                        }

                        GameTab.MATCH -> {
                            PictureMatchScreen(
                                state = state,
                                onOptionSelected = { viewModel.onMatchOptionTapped(it) },
                                onNewRound = { viewModel.startMatchRound() },
                                onHearPrompt = {
                                    viewModel.soundManager.speak("What starts with the letter ${state.matchTarget.letter}?")
                                }
                            )
                        }

                        GameTab.TRACE -> {
                            LetterTraceScreen(
                                state = state,
                                onSelectLetter = { viewModel.setTraceLetter(it) },
                                onColorSelected = { viewModel.setTraceColor(it) },
                                onCompleted = { viewModel.completeTracing() },
                                onDismissCelebration = { viewModel.dismissTraceCelebration() }
                            )
                        }

                        GameTab.STICKERS -> {
                            StickerAlbumScreen(
                                state = state,
                                stickers = viewModel.allStickers,
                                onStickerTapped = { sticker ->
                                    if (state.stickersUnlocked.contains(sticker.id)) {
                                        viewModel.soundManager.playCelebrationChime()
                                        viewModel.soundManager.speak("${sticker.title}! ${sticker.description}")
                                    } else {
                                        viewModel.soundManager.speak("${sticker.title} is locked. Earn ${sticker.requiredStars} stars to unlock!")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
