package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val startX: Float,
    val startY: Float,
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    particleCount: Int = 36
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF5964),
            Color(0xFFFFD166),
            Color(0xFF06D6A0),
            Color(0xFF4CC9F0),
            Color(0xFFFF70A6),
            Color(0xFF8338EC),
            Color(0xFFFF9F1C)
        )
        List(particleCount) {
            Particle(
                startX = 0.5f,
                startY = 0.45f,
                angle = Random.nextDouble(0.0, Math.PI * 2),
                speed = Random.nextFloat() * 450f + 150f,
                radius = Random.nextFloat() * 9f + 6f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value

        particles.forEach { p ->
            val distance = p.speed * t
            val currentX = (p.startX * w) + (cos(p.angle) * distance).toFloat()
            // add subtle gravity downward
            val currentY = (p.startY * h) + (sin(p.angle) * distance).toFloat() + (t * t * 250f)
            val alpha = (1f - t).coerceIn(0f, 1f)

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius * (1f - t * 0.3f),
                center = Offset(currentX, currentY)
            )
        }
    }
}
