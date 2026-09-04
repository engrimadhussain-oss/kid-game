package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AbcGameState
import com.example.ui.BubbleItem
import com.example.ui.components.ConfettiCelebration
import kotlin.math.roundToInt

@Composable
fun BubblePopScreen(
    state: AbcGameState,
    onBubbleTapped: (BubbleItem) -> Unit,
    onNewRound: () -> Unit,
    onHearPrompt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleDrift"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instruction Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pop the letter bubble:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF118AB2)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF118AB2),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = " ${state.bubbleTargetLetter} ",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = onHearPrompt,
                            modifier = Modifier.testTag("hear_bubble_prompt")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Hear prompt",
                                tint = Color(0xFF118AB2),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onNewRound,
                    modifier = Modifier.testTag("bubble_new_round_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Letter",
                        tint = Color(0xFF118AB2),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bubble Sky Playfield
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE0F7FA), Color(0xFFFFF9C4))
                    )
                )
                .border(2.dp, Color(0xFF80DEEA), RoundedCornerShape(24.dp))
        ) {
            val fieldWidth = maxWidth
            val fieldHeight = maxHeight

            // Confetti Overlay on Success
            if (state.bubbleSuccessCelebration) {
                ConfettiCelebration(modifier = Modifier.fillMaxSize())
            }

            state.bubbles.forEachIndexed { index, bubble ->
                val isPopped = state.poppedBubbleIds.contains(bubble.id)

                if (!isPopped) {
                    val bubbleColor = Color(bubble.colorHex)
                    // Compute absolute position with gentle sine floating
                    val xPos = (fieldWidth * bubble.initialOffsetX)
                    val yPos = (fieldHeight * bubble.initialOffsetY) +
                            (if (index % 2 == 0) floatOffset else -floatOffset).dp

                    SingleBubble(
                        bubble = bubble,
                        color = bubbleColor,
                        modifier = Modifier
                            .offset {
                                IntOffset(xPos.roundToPx(), yPos.roundToPx())
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onBubbleTapped(bubble)
                            }
                            .testTag("bubble_${bubble.letter}")
                    )
                }
            }

            // Success banner toast
            if (state.bubbleSuccessCelebration) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Popped ${state.bubbleTargetLetter}! +1 Star!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06D6A0)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun SingleBubble(
    bubble: BubbleItem,
    color: Color,
    modifier: Modifier = Modifier
) {
    val size = bubble.sizeDp.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.toPx() / 2f
            val center = Offset(radius, radius)

            // Main translucent bubble
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.55f),
                        color.copy(alpha = 0.85f),
                        color.copy(alpha = 0.95f)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Shiny Bubble Highlight (gloss arc at top-left)
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = radius * 0.28f,
                center = Offset(radius * 0.65f, radius * 0.65f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.45f),
                radius = radius * 0.12f,
                center = Offset(radius * 0.48f, radius * 0.48f)
            )
        }

        // Letter inside bubble
        Text(
            text = bubble.letter.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}
