package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Track
import kotlin.math.sin

@Composable
fun MiniPlayer(
    track: Track,
    isPlaying: Boolean,
    progressSec: Int,
    durationSec: Int,
    isLiked: Boolean,
    monoCovers: Boolean = true,
    onPlayPause: () -> Unit,
    onLikeToggle: () -> Unit,
    onExpand: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onDevicesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gbwGlow = rememberGbwGlowColors()

    val infiniteTransition = rememberInfiniteTransition(label = "mini_spin")
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mini_rot"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mini_wave"
    )

    // Floating Glass Pill Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0x66FFFFFF))
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xE6141416))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(gbwGlow),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onExpand() }
            .testTag("mini_player")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spinning Vinyl Cover
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F0F0F))
                        .border(1.dp, Color(0xFF333333), CircleShape)
                        .rotate(if (isPlaying) vinylRotation else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                        modifier = Modifier.matchParentSize()
                    )
                    // Spindle Center
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF000000))
                            .border(1.dp, Color(0xFF666666), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Title & Artist (Marquee feel)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${track.artist} • DriftMoods",
                        color = Color(0xFF999999),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                IconButton(
                    onClick = onDevicesClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = "Music Player Panel",
                        tint = Color(0xFFBBBBBB),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onLikeToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.White else Color(0xFF888888),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Animated Waveform Progress Line along bottom edge
            val progressFraction = if (durationSec > 0) (progressSec.toFloat() / durationSec).coerceIn(0f, 1f) else 0f
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                val w = size.width
                val h = size.height
                val activeWidth = w * progressFraction

                // Inactive track
                drawLine(
                    color = Color(0x33FFFFFF),
                    start = Offset(0f, h * 0.5f),
                    end = Offset(w, h * 0.5f),
                    strokeWidth = h
                )

                // Active waveform-like progress line
                if (activeWidth > 0f) {
                    drawLine(
                        brush = Brush.horizontalGradient(gbwGlow),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(activeWidth, h * 0.5f),
                        strokeWidth = h
                    )
                }
            }
        }
    }
}
