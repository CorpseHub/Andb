package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.BackgroundEffect
import kotlin.math.cos
import kotlin.math.sin

object GbwColorMatrix {
    val MonochromaticFilter: ColorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply {
            setToSaturation(0f)
        }
    )
}

@Composable
fun rememberGbwGlowColors(): List<Color> {
    val transition = rememberInfiniteTransition(label = "gbw_transition")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gbw_phase"
    )

    val c1 = lerpGbwColor(phase)
    val c2 = lerpGbwColor((phase + 0.33f) % 1f)
    val c3 = lerpGbwColor((phase + 0.66f) % 1f)

    return listOf(c1, c2, c3)
}

@Composable
fun rememberGbwBrush(): Brush {
    val colors = rememberGbwGlowColors()
    return Brush.sweepGradient(colors)
}

private fun lerpGbwColor(fraction: Float): Color {
    // Cycles smoothly: Black (0.0) -> Graphite (0.25) -> Mid Grey (0.5) -> Pure White (0.75) -> Black (1.0)
    return when {
        fraction < 0.25f -> {
            val t = fraction / 0.25f
            Color(0xFF0F0F0F).copy(alpha = 1f - t * 0.2f)
        }
        fraction < 0.50f -> {
            val t = (fraction - 0.25f) / 0.25f
            Color(0xFF444444).copy(alpha = 0.8f + t * 0.2f)
        }
        fraction < 0.75f -> {
            val t = (fraction - 0.50f) / 0.25f
            Color(0xFFE5E5E5).copy(alpha = 1f)
        }
        else -> {
            val t = (fraction - 0.75f) / 0.25f
            Color(0xFF222222).copy(alpha = 1f - t * 0.5f)
        }
    }
}

@Composable
fun GbwAnimatedBackground(
    effect: BackgroundEffect,
    beatFactor: Float = 0f,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "bg_anim")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_progress"
    )

    when (effect) {
        BackgroundEffect.PURE_BLACK -> {
            Box(modifier = modifier.fillMaxSize().background(Color(0xFF000000)))
        }

        BackgroundEffect.AURORA_GREY -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF0C0C0C))) {
                val w = size.width
                val h = size.height
                val waveY = h * 0.4f + sin(progress.toDouble()).toFloat() * 60f * (1f + beatFactor * 0.5f)

                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, waveY + 80f)
                        cubicTo(
                            w * 0.7f, waveY - 40f,
                            w * 0.3f, waveY + 120f,
                            0f, waveY
                        )
                        close()
                    },
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1E22).copy(alpha = 0.6f + beatFactor * 0.2f),
                            Color(0xFF141416).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )

                // Secondary soft light sweep
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.04f + beatFactor * 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f + cos(progress.toDouble()).toFloat() * 100f, h * 0.35f),
                        radius = w * 0.6f
                    ),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.6f
                )
            }
        }

        BackgroundEffect.SMOKE -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF0B0D0E))) {
                val w = size.width
                val h = size.height
                for (i in 0..2) {
                    val angle = progress + i * 2.0f
                    val cx = w * (0.3f + i * 0.2f) + cos(angle.toDouble()).toFloat() * 50f
                    val cy = h * (0.2f + i * 0.25f) + sin(angle.toDouble()).toFloat() * 40f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2C3035).copy(alpha = 0.15f + beatFactor * 0.1f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = w * 0.7f
                        ),
                        center = Offset(cx, cy),
                        radius = w * 0.7f
                    )
                }
            }
        }

        BackgroundEffect.STARFIELD -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF050505))) {
                val w = size.width
                val h = size.height
                for (i in 0..40) {
                    val sx = ((i * 73) % w.toInt()).toFloat()
                    val sy = ((i * 127) % h.toInt()).toFloat()
                    val starAlpha = (0.3f + sin((progress * 2f + i).toDouble()).toFloat() * 0.4f).coerceIn(0.1f, 1.0f)
                    val starSize = if (i % 6 == 0) 2.5f else 1.2f
                    drawCircle(
                        color = Color.White.copy(alpha = starAlpha),
                        radius = starSize,
                        center = Offset(sx, sy)
                    )
                }
            }
        }

        BackgroundEffect.GRID_PULSE -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF0A0A0B))) {
                val w = size.width
                val h = size.height
                val spacing = 48f
                val gridAlpha = 0.04f + sin(progress.toDouble()).toFloat() * 0.02f + beatFactor * 0.04f

                var x = 0f
                while (x < w) {
                    drawLine(
                        color = Color.White.copy(alpha = gridAlpha),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                    x += spacing
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = Color.White.copy(alpha = gridAlpha),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += spacing
                }
            }
        }

        BackgroundEffect.LIQUID_METAL -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF0A0A0B))) {
                val w = size.width
                val h = size.height
                val sweepX = (sin(progress.toDouble()).toFloat() + 1f) * 0.5f * w
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f + beatFactor * 0.08f),
                            Color.Transparent
                        ),
                        startX = sweepX - 180f,
                        endX = sweepX + 180f
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                    strokeWidth = w * 1.5f
                )
            }
        }

        BackgroundEffect.STATIC_NOISE, BackgroundEffect.FILM_GRAIN -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
                val w = size.width
                val h = size.height
                // Simulated fine film grain specks
                for (i in 0..60) {
                    val px = ((i * 97 + progress * 50) % w.toInt()).toFloat()
                    val py = ((i * 151 + progress * 70) % h.toInt()).toFloat()
                    drawCircle(
                        color = Color.White.copy(alpha = 0.035f),
                        radius = 1.2f,
                        center = Offset(px, py)
                    )
                }
            }
        }

        BackgroundEffect.RAIN_ON_GLASS -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF080808))) {
                val w = size.width
                val h = size.height
                for (i in 0..18) {
                    val rx = ((i * 89) % w.toInt()).toFloat()
                    val ry = ((i * 137 + progress * 80) % h.toInt()).toFloat()
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f + beatFactor * 0.05f),
                        start = Offset(rx, ry),
                        end = Offset(rx - 4f, ry + 22f),
                        strokeWidth = 1.5f
                    )
                }
            }
        }

        BackgroundEffect.SNOWFALL -> {
            // Handled via SnowfallCanvas overlay
            Box(modifier = modifier.fillMaxSize().background(Color(0xFF0E1012)))
        }
    }
}
