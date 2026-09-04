package com.example.ui.screens

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlphabetRepository
import com.example.ui.AbcGameState
import com.example.ui.components.ConfettiCelebration

data class LineStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float = 28f
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LetterTraceScreen(
    state: AbcGameState,
    onSelectLetter: (Char) -> Unit,
    onColorSelected: (Long) -> Unit,
    onCompleted: () -> Unit,
    onDismissCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLetter = state.traceLetter
    val item = AlphabetRepository.getByLetter(currentLetter) ?: AlphabetRepository.alphabetList.first()
    val strokes = remember(currentLetter) { mutableStateListOf<LineStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val activeColor = Color(state.traceColorHex)

    val crayonColors = listOf(
        0xFFFF5964, // Coral Red
        0xFFFF9F1C, // Orange
        0xFFFFD166, // Yellow
        0xFF06D6A0, // Green
        0xFF118AB2, // Blue
        0xFF8338EC, // Purple
        0xFFFF70A6  // Pink
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with letter navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentIndex = AlphabetRepository.alphabetList.indexOfFirst { it.letter == currentLetter }
                IconButton(
                    onClick = {
                        val prevIdx = if (currentIndex <= 0) AlphabetRepository.alphabetList.size - 1 else currentIndex - 1
                        onSelectLetter(AlphabetRepository.alphabetList[prevIdx].letter)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("trace_prev_letter")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Letter",
                        tint = activeColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Trace Letter $currentLetter ${currentLetter.lowercaseChar()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3142)
                    )
                    Text(
                        text = "${item.emoji} for ${item.word}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6C757D)
                    )
                }

                IconButton(
                    onClick = {
                        val nextIdx = (currentIndex + 1) % AlphabetRepository.alphabetList.size
                        onSelectLetter(AlphabetRepository.alphabetList[nextIdx].letter)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("trace_next_letter")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Letter",
                        tint = activeColor
                    )
                }
            }

            // Quick letter selector row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AlphabetRepository.alphabetList.forEach { alpha ->
                    val isSelected = alpha.letter == currentLetter
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) Color(alpha.colorHex) else Color(alpha.colorHex).copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onSelectLetter(alpha.letter) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = alpha.letter.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(alpha.colorHex)
                            )
                        }
                    }
                }
            }

            // Drawing Canvas Card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(3.dp, activeColor.copy(alpha = 0.4f), RoundedCornerShape(26.dp))
                    .testTag("tracing_canvas_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInteropFilter { motionEvent ->
                            when (motionEvent.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    currentPoints = listOf(Offset(motionEvent.x, motionEvent.y))
                                    true
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    currentPoints = currentPoints + Offset(motionEvent.x, motionEvent.y)
                                    true
                                }
                                MotionEvent.ACTION_UP -> {
                                    if (currentPoints.isNotEmpty()) {
                                        strokes.add(LineStroke(currentPoints, activeColor))
                                        currentPoints = emptyList()
                                    }
                                    true
                                }
                                else -> false
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw background guideline letter
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(45, 0, 0, 0)
                            textSize = canvasHeight * 0.65f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        // Center vertically: baseline is height/2 - (ascent + descent)/2
                        val yPos = (canvasHeight / 2f) - ((paint.descent() + paint.ascent()) / 2f)
                        drawContext.canvas.nativeCanvas.drawText(
                            "$currentLetter",
                            canvasWidth / 2f,
                            yPos,
                            paint
                        )

                        // Draw completed strokes
                        strokes.forEach { stroke ->
                            if (stroke.points.size > 1) {
                                val path = Path().apply {
                                    moveTo(stroke.points[0].x, stroke.points[0].y)
                                    for (i in 1 until stroke.points.size) {
                                        lineTo(stroke.points[i].x, stroke.points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = stroke.color,
                                    style = Stroke(
                                        width = stroke.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Draw current active stroke
                        if (currentPoints.size > 1) {
                            val path = Path().apply {
                                moveTo(currentPoints[0].x, currentPoints[0].y)
                                for (i in 1 until currentPoints.size) {
                                    lineTo(currentPoints[i].x, currentPoints[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = activeColor,
                                style = Stroke(
                                    width = 28f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Tools: Crayon Color Palette & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                crayonColors.forEach { colorHex ->
                    val isSelected = colorHex == state.traceColorHex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(if (isSelected) 42.dp else 34.dp)
                            .clip(CircleShape)
                            .background(Color(colorHex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF2D3142) else Color.White,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorHex) }
                            .testTag("crayon_color_$colorHex")
                    )
                }
            }

            // Action Buttons: Clear & Done / Finished
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { strokes.clear() },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("trace_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Canvas",
                        tint = Color(0xFF6C757D)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Clear", color = Color(0xFF6C757D), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onCompleted() },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("trace_done_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done Tracing",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "I Did It!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Celebration banner and confetti on complete
        if (state.traceShowCelebration) {
            ConfettiCelebration(modifier = Modifier.fillMaxSize())

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .clickable { onDismissCelebration() }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌟 Fantastic! 🌟", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9F1C))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You traced letter $currentLetter beautifully! +1 Star ⭐",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3142)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onDismissCelebration,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9F1C))
                    ) {
                        Text("Keep Going!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
