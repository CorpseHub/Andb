package com.example.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.components.GbwColorMatrix
import com.example.ui.components.rememberGbwGlowColors
import kotlin.math.cos
import kotlin.math.sin

data class MoodOrb(
    val name: String,
    val subtitle: String,
    val icon: String,
    val primaryTone: Color,
    val secondaryTone: Color
)

@Composable
fun HomeScreen(
    filterChip: String,
    onFilterChipSelect: (String) -> Unit,
    allTracks: List<Track>,
    playlists: List<Playlist>,
    userUploads: List<Track>,
    monoCovers: Boolean = true,
    onTrackSelect: (Track) -> Unit,
    onPlaylistSelect: (Playlist) -> Unit
) {
    val gbwGlow = rememberGbwGlowColors()

    val moodOrbs = remember {
        listOf(
            MoodOrb("Focus", "Deep Work Flow", "○", Color(0xFFE5E5E5), Color(0xFF222222)),
            MoodOrb("Rain", "Soft Downpour", "∿", Color(0xFFC0C0C0), Color(0xFF141414)),
            MoodOrb("Night Drive", "Dark Highways", "◈", Color(0xFFFFFFFF), Color(0xFF1C1C1E)),
            MoodOrb("Sleep", "Drift to Peace", "☾", Color(0xFF999999), Color(0xFF0F0F10)),
            MoodOrb("Gym", "Heavy Rhythm", "⚡", Color(0xFFF0F0F0), Color(0xFF2C2C2E)),
            MoodOrb("Chill", "Ambient Calm", "☁", Color(0xFFD6D6D6), Color(0xFF18181A))
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // Tagline & Intro
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "DriftMoods",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Free music. Zero ads. Drift on.",
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Hero Mood Orb Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DRIFT MOODS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(moodOrbs) { orb ->
                        MoodOrbCard(
                            orb = orb,
                            onClick = {
                                val match = allTracks.firstOrNull { it.genre.contains(orb.name, true) || it.title.contains(orb.name, true) }
                                    ?: allTracks.firstOrNull()
                                match?.let(onTrackSelect)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bento Grid: Hero "New Drops" Wide Card with Vinyl Peek
        item {
            Text(
                text = "FEATURED DROP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            if (allTracks.isNotEmpty()) {
                val featured = allTracks.first()
                BentoHeroDropCard(
                    track = featured,
                    monoCovers = monoCovers,
                    gbwGlow = gbwGlow,
                    onClick = { onTrackSelect(featured) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Horizontal Vinyl Crate Shelf (3D Vinyl Peek Effect)
        item {
            Text(
                text = "VINYL CRATES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allTracks.take(8)) { track ->
                    VinylCrateCard(
                        track = track,
                        monoCovers = monoCovers,
                        onClick = { onTrackSelect(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bento 2-Column Asymmetrical Grid: Trending & Curated Playlists
        item {
            Text(
                text = "TRENDING ON DRIFTMOODS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                allTracks.drop(1).take(5).forEach { track ->
                    BentoShelfRow(
                        track = track,
                        monoCovers = monoCovers,
                        onClick = { onTrackSelect(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Jamendo & Community Streams
        item {
            Text(
                text = "COMMUNITY STREAMS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(playlists) { playlist ->
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onPlaylistSelect(playlist) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF202022))
                            ) {
                                AsyncImage(
                                    model = playlist.coverUrl,
                                    contentDescription = playlist.title,
                                    contentScale = ContentScale.Crop,
                                    colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = playlist.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${playlist.trackIds.size} songs",
                                color = Color(0xFF888888),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodOrbCard(
    orb: MoodOrb,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "orb_glow")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(elevation = 12.dp, shape = CircleShape, spotColor = Color(0x66FFFFFF))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color(0xFF444444),
                            Color(0xFF111111)
                        )
                    )
                )
                .border(1.dp, Color(0x44FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = orb.icon,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = orb.name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = orb.subtitle,
            color = Color(0xFF777777),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun BentoHeroDropCard(
    track: Track,
    monoCovers: Boolean,
    gbwGlow: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x40FFFFFF))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(1.dp, Brush.horizontalGradient(gbwGlow), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141416))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sleeve + Vinyl Peek
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                // Vinyl peek circle
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .offset(x = 18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F0F0F))
                        .border(1.dp, Color(0xFF333333), CircleShape)
                )
                // Album Sleeve
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222222))
                ) {
                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEW RELEASE",
                    color = Color(0xFFAAAAAA),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    color = Color(0xFF888888),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun VinylCrateCard(
    track: Track,
    monoCovers: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        // Crate Sleeve with Vinyl Peeking
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Vinyl rim peeking above
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = (-8).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F0F0F))
                    .border(1.dp, Color(0xFF333333), CircleShape)
            )
            // Sleeve
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
            ) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = track.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artist,
            color = Color(0xFF777777),
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BentoShelfRow(
    track: Track,
    monoCovers: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141416))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF222222))
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • ${track.genre}",
                color = Color(0xFF888888),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0x22FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
