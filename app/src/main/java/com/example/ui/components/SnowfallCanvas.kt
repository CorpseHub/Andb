package com.example.ui.components

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.model.SnowConfig
import kotlin.math.sin
import kotlin.random.Random

data class SnowFlake(
    var x: Float,
    var y: Float,
    val size: Float,
    val layer: Int, // 0 = Far, 1 = Mid, 2 = Near
    val baseSpeed: Float,
    val swayOffset: Float,
    val baseAlpha: Float
)

@Composable
fun SnowfallCanvas(
    config: SnowConfig,
    beatFactor: Float = 0f, // 0.0 to 1.0 from real-time audio visualizer
    isReducedMotion: Boolean = false,
    isBatterySaver: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!config.enabled || isReducedMotion || isBatterySaver) return

    val particleCount = (config.density * if (isBatterySaver) 0.4f else 1.0f).toInt().coerceIn(15, 80)
    var timeMillis by remember { mutableFloatStateOf(0f) }

    val flakes = remember(particleCount) {
        List(particleCount) {
            val layer = when {
                it % 5 == 0 -> 2 // Near (20%)
                it % 2 == 0 -> 1 // Mid (40%)
                else -> 0        // Far (40%)
            }
            val sizeMultiplier = when (layer) {
                2 -> 1.8f
                1 -> 1.0f
                else -> 0.5f
            }
            SnowFlake(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = (Random.nextFloat() * 2.5f + 2f) * sizeMultiplier * (config.flakeSize / 3.5f),
                layer = layer,
                baseSpeed = (Random.nextFloat() * 0.4f + 0.6f) * (when (layer) { 2 -> 1.3f; 1 -> 1.0f; else -> 0.7f }),
                swayOffset = Random.nextFloat() * 6.28f,
                baseAlpha = when (layer) { 2 -> 0.95f; 1 -> 0.70f; else -> 0.40f } * config.brightness
            )
        }
    }

    LaunchedEffect(Unit) {
        var lastTime = 0L
        while (true) {
            withInfiniteAnimationFrameMillis { frameTime ->
                if (lastTime != 0L) {
                    val dt = (frameTime - lastTime) / 1000f
                    timeMillis += dt
                }
                lastTime = frameTime
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        if (width <= 0 || height <= 0) return@Canvas

        val effectiveSpeed = config.speed * (1.0f + beatFactor * 0.6f)
        val wind = config.windDirection * 0.15f

        flakes.forEach { flake ->
            // Update position
            flake.y += (flake.baseSpeed * 0.003f * effectiveSpeed)
            val sway = sin((timeMillis * 1.5f + flake.swayOffset).toDouble()).toFloat() * 0.0015f
            flake.x += wind * 0.002f + sway

            // Wrap around
            if (flake.y > 1.05f) {
                flake.y = -0.05f
                flake.x = Random.nextFloat()
            }
            if (flake.x > 1.05f) flake.x = -0.05f
            if (flake.x < -0.05f) flake.x = 1.05f

            val px = flake.x * width
            val py = flake.y * height
            val sparkleAlpha = (flake.baseAlpha + beatFactor * 0.25f).coerceIn(0.1f, 1.0f)

            // Draw flake with layer-based glow
            if (flake.layer == 2 && config.blur) {
                // Near flake with soft white aura glow
                drawCircle(
                    color = Color.White.copy(alpha = sparkleAlpha * 0.35f),
                    radius = flake.size * 2.2f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Color.White.copy(alpha = sparkleAlpha),
                    radius = flake.size,
                    center = Offset(px, py)
                )
            } else if (flake.layer == 1) {
                drawCircle(
                    color = Color(0xFFE8E8E8).copy(alpha = sparkleAlpha),
                    radius = flake.size,
                    center = Offset(px, py)
                )
            } else {
                drawCircle(
                    color = Color(0xFFB0B0B0).copy(alpha = sparkleAlpha * 0.7f),
                    radius = flake.size,
                    center = Offset(px, py)
                )
            }
        }

        // Settled frost accumulation along bottom dock area
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.02f + beatFactor * 0.03f),
                    Color.White.copy(alpha = 0.06f)
                ),
                startY = height - 140f,
                endY = height
            ),
            topLeft = Offset(0f, height - 140f),
            size = androidx.compose.ui.geometry.Size(width, 140f)
        )
    }
}
