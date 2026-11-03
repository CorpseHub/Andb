package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AudioVisualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    accentGlow: Color = Color.White
) {
    val transition = rememberInfiniteTransition(label = "freq_anim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "freq_phase"
    )

    val gbwColors = rememberGbwGlowColors()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            val barWidth = (w / barCount) * 0.75f
            val spacing = (w / barCount) * 0.25f

            // Draw Frequency Bars
            for (i in 0 until barCount) {
                // Compute harmonic frequency magnitude
                val normalizedIdx = i.toFloat() / barCount
                // Bass boost curve in first quarter
                val bassWeight = if (i < 8) 1.4f - (i * 0.08f) else 0.8f

                val rawMagnitude = if (isPlaying) {
                    val s1 = sin((phase * 2.5f + i * 0.45f).toDouble()).toFloat()
                    val s2 = sin((phase * 4.0f + i * 0.9f).toDouble()).toFloat() * 0.5f
                    val combined = abs(s1 + s2) / 1.5f
                    (combined * bassWeight).coerceIn(0.08f, 0.96f)
                } else {
                    0.05f // Resting state
                }

                val barHeight = h * rawMagnitude
                val x = i * (barWidth + spacing) + spacing * 0.5f
                val y = h - barHeight

                // Draw Bar with monochrome silver-to-white gradient
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFDDDDDD),
                            Color(0xFF666666).copy(alpha = 0.5f)
                        ),
                        startY = y,
                        endY = h
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth * 0.5f, barWidth * 0.5f)
                )

                // Peak Dot
                if (isPlaying && rawMagnitude > 0.4f) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = barWidth * 0.45f,
                        center = Offset(x + barWidth * 0.5f, (y - 4f).coerceAtLeast(2f))
                    )
                }
            }

            // Smooth audio waveform line overlay
            val wavePath = Path()
            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing) + barWidth * 0.5f
                val waveFactor = if (isPlaying) {
                    (sin((phase * 3f + i * 0.35f).toDouble()).toFloat() * 0.5f + 0.5f)
                } else 0.1f
                val y = h * (1f - waveFactor * 0.7f)
                if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
            }

            drawPath(
                path = wavePath,
                brush = Brush.horizontalGradient(gbwColors),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
