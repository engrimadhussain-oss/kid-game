package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AbcGameState
import com.example.ui.StickerReward

@Composable
fun StickerAlbumScreen(
    state: AbcGameState,
    stickers: List<StickerReward>,
    onStickerTapped: (StickerReward) -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedCount = state.stickersUnlocked.size
    val totalCount = stickers.size
    val maxStarsNeeded = stickers.maxOfOrNull { it.requiredStars } ?: 50
    val progressFraction = (state.stars.toFloat() / maxStarsNeeded.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Trophy Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🏆", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "My Sticker Collection",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3142)
                        )
                        Text(
                            text = "$unlockedCount of $totalCount Stickers Unlocked",
                            fontSize = 14.sp,
                            color = Color(0xFF6C757D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFFFF9F1C),
                    trackColor = Color(0xFFFFE8D6)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ ${state.stars} Stars",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9F1C)
                    )
                    Text(
                        text = "Next at ${stickers.firstOrNull { state.stars < it.requiredStars }?.requiredStars ?: "Max"} ⭐",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6C757D)
                    )
                }
            }
        }

        // Grid of Stickers
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(stickers) { sticker ->
                val isUnlocked = state.stickersUnlocked.contains(sticker.id)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) Color.White else Color(0xFFF1F3F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isUnlocked) 2.dp else 1.dp,
                            color = if (isUnlocked) Color(0xFFFFD166) else Color(0xFFDEE2E6),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onStickerTapped(sticker) }
                        .testTag("sticker_card_${sticker.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isUnlocked) Color(0xFFFFF9E6) else Color(0xFFE9ECEF)
                                )
                        ) {
                            if (isUnlocked) {
                                Text(
                                    text = sticker.emoji,
                                    fontSize = 36.sp,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked Sticker",
                                    tint = Color(0xFFADB5BD),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Text(
                            text = sticker.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF2D3142) else Color(0xFF868E96),
                            textAlign = TextAlign.Center
                        )

                        if (isUnlocked) {
                            Text(
                                text = sticker.description,
                                fontSize = 12.sp,
                                color = Color(0xFF6C757D),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFD166).copy(alpha = 0.3f),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Stars needed",
                                        tint = Color(0xFFFF9F1C),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${sticker.requiredStars} Stars",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF854D0E)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
