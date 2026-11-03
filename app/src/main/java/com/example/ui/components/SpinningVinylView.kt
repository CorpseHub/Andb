package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SpinningVinylView(
    coverUrl: String,
    isPlaying: Boolean,
    monoCovers: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rot")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_deg"
    )

    // Tonearm drop angle: 0f when resting off-record, 24f when playing on the groove
    val tonearmAngle by animateFloatAsState(
        targetValue = if (isPlaying) 24f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "tonearm_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer Vinyl Disc with grooves & soft aura glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation = 20.dp, shape = CircleShape, spotColor = Color(0x99FFFFFF))
                .clip(CircleShape)
                .background(Color(0xFF0F0F0F))
                .rotate(if (isPlaying) rotation else 0f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Concentric Vinyl Micro-Grooves
                for (r in 40..radius.toInt() step 6) {
                    drawCircle(
                        color = Color(0xFF242424).copy(alpha = 0.45f),
                        radius = r.toFloat(),
                        center = center,
                        style = Stroke(width = 0.8f)
                    )
                }

                // Vinyl Sheen / Reflection Highlights
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = radius,
                    center = center
                )
            }

            // Center Label with Album Art
            Box(
                modifier = Modifier
                    .fillMaxSize(0.42f)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF333333), CircleShape)
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                    modifier = Modifier.matchParentSize()
                )

                // Spindle Hole
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF000000))
                        .border(1.dp, Color(0xFF666666), CircleShape)
                )
            }
        }

        // Realistic Tonearm Pivot & Needle Drop
        Canvas(
            modifier = Modifier
                .size(width = 110.dp, height = 180.dp)
                .align(Alignment.TopEnd)
        ) {
            val pivot = Offset(size.width - 20f, 25f)

            // Draw Pivot Base
            drawCircle(
                color = Color(0xFF2A2A2A),
                radius = 16f,
                center = pivot
            )
            drawCircle(
                color = Color(0xFFE5E5E5),
                radius = 7f,
                center = pivot
            )

            // Tonearm Arm with dynamic rotation
            val armLength = 135f
            val rad = Math.toRadians((tonearmAngle + 45).toDouble())
            val armEndX = pivot.x - (armLength * kotlin.math.cos(rad)).toFloat()
            val armEndY = pivot.y + (armLength * kotlin.math.sin(rad)).toFloat()

            // Arm rod
            drawLine(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFE5E5E5), Color(0xFF888888))
                ),
                start = pivot,
                end = Offset(armEndX, armEndY),
                strokeWidth = 3.5f
            )

            // Needle Head Cartridge
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(armEndX, armEndY)
            )
        }
    }
}
