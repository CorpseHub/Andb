package com.example.ui.components

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.api.LyricLine
import com.example.api.LyricsApiClient
import com.example.model.RepeatMode
import com.example.model.Track
import com.example.model.TrackSource

@Composable
fun NowPlayingSheet(
    track: Track,
    isPlaying: Boolean,
    progressSec: Int,
    durationSec: Int,
    isLiked: Boolean,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    queue: List<Track>,
    queueIndex: Int,
    monoCovers: Boolean = true,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: () -> Unit,
    onCollapse: () -> Unit,
    onPlayTrackFromQueue: (Track) -> Unit,
    onClearQueue: () -> Unit,
    onShareTrack: () -> Unit,
    onAttachSurface: (SurfaceHolder?) -> Unit,
    youTubeController: YouTubePlayerController? = null,
    onYouTubeStateChanged: (Int) -> Unit = {},
    onYouTubeError: (Int) -> Unit = {},
    onYouTubeProgress: (Int, Int) -> Unit = { _, _ -> },
    onOpenUrl: (String) -> Unit = {}
) {
    var showQueueDrawer by remember { mutableStateOf(false) }
    var isUserScrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableFloatStateOf(0f) }

    // Live Synced Lyrics fetched from public free LRCLIB API
    var syncedLines by remember { mutableStateOf<List<LyricLine>>(emptyList()) }
    var plainLyricsText by remember { mutableStateOf(track.lyrics) }
    var lyricsSource by remember { mutableStateOf("DriftMoods Curated") }

    LaunchedEffect(track.id) {
        val result = LyricsApiClient.fetchLyrics(track.artist, track.title, track.lyrics)
        syncedLines = result.syncedLines
        plainLyricsText = result.plainLyrics
        lyricsSource = result.source
    }

    val gbwGlow = rememberGbwGlowColors()
    val scrollState = rememberScrollState()

    val currentSec = if (isUserScrubbing) scrubValue.toInt() else progressSec
    val maxSec = if (durationSec > 0) durationSec else 180

    // Blurred Monochrome Backdrop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF141416),
                        Color(0xFF0C0C0D),
                        Color(0xFF050505)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("now_playing_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.testTag("collapse_now_playing_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM DRIFTMOODS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF888888),
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = track.album,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onShareTrack,
                    modifier = Modifier.testTag("share_track_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Media Artwork: Spinning Vinyl or YouTube WebView
            if (track.source == TrackSource.YOUTUBE && track.youtubeVideoId != null && youTubeController != null) {
                YouTubePlayerView(
                    videoId = track.youtubeVideoId,
                    isPlaying = isPlaying,
                    controller = youTubeController,
                    onStateChanged = onYouTubeStateChanged,
                    onError = onYouTubeError,
                    onProgressUpdate = onYouTubeProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            } else if (track.isVideo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            SurfaceView(ctx).apply {
                                holder.addCallback(object : SurfaceHolder.Callback {
                                    override fun surfaceCreated(holder: SurfaceHolder) {
                                        onAttachSurface(holder)
                                    }
                                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                                        onAttachSurface(null)
                                    }
                                })
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Spinning Vinyl with Animated Dropping Tonearm
                SpinningVinylView(
                    coverUrl = track.coverUrl,
                    isPlaying = isPlaying,
                    monoCovers = monoCovers
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Real-Time Frequency-Based Audio Visualizer Animation
            AudioVisualizerView(
                isPlaying = isPlaying,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title, Artist, & Like
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = track.artist,
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier.testTag("now_playing_like_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Like",
                        tint = if (isLiked) Color.White else Color(0xFF888888),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seek Bar with Glowing Thumb
            Slider(
                value = currentSec.toFloat(),
                onValueChange = {
                    isUserScrubbing = true
                    scrubValue = it
                },
                onValueChangeFinished = {
                    isUserScrubbing = false
                    onSeek(scrubValue.toInt())
                },
                valueRange = 0f..maxSec.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0xFF333333)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("now_playing_seek_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentSec),
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTime(maxSec),
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glass Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) Color.White else Color(0xFF555555),
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = onSkipPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Big Round Play/Pause Glass Button with GBW Glow border
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Brush.horizontalGradient(gbwGlow), CircleShape)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = onToggleRepeat) {
                    val repeatIcon = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
                    Icon(
                        imageVector = repeatIcon,
                        contentDescription = "Repeat",
                        tint = if (repeatMode != RepeatMode.OFF) Color.White else Color(0xFF555555),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Real-Time Synced Lyrics Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161618)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lyrics Preview",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lyricsSource,
                            color = Color(0xFF777777),
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (syncedLines.isNotEmpty()) {
                        // Find current active synced lyric line
                        val currentActiveIdx = syncedLines.indexOfLast { it.timeSec <= currentSec }.coerceAtLeast(0)
                        val previewLines = syncedLines.subList(
                            (currentActiveIdx - 1).coerceAtLeast(0),
                            (currentActiveIdx + 3).coerceAtMost(syncedLines.size)
                        )

                        previewLines.forEach { line ->
                            val isCurrent = line == syncedLines[currentActiveIdx]
                            Text(
                                text = line.text,
                                color = if (isCurrent) Color.White else Color(0xFF666666),
                                fontSize = if (isCurrent) 16.sp else 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    } else {
                        Text(
                            text = plainLyricsText,
                            color = Color(0xFFDDDDDD),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Track Information & Licensing Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Track Information",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Source: ${track.source.name} • ${track.album}",
                        color = Color(0xFF999999),
                        fontSize = 12.sp
                    )
                    if (track.source == TrackSource.YOUTUBE) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "YouTube videos play in YouTube's own player, so YouTube's ads and rules apply.",
                            color = Color(0xFFCCCCCC),
                            fontSize = 11.sp
                        )
                        if (!track.originalUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Open original video on YouTube",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onOpenUrl(track.originalUrl) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "License: ${track.licenseInfo ?: "Creative Commons / Legal Stream"}",
                            color = Color(0xFF777777),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Queue Open Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showQueueDrawer = true }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Up Next (${queue.size} songs)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "View",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Queue Overlay Drawer
        AnimatedVisibility(
            visible = showQueueDrawer,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xF50C0C0D))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Play Queue",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Clear",
                                color = Color(0xFFCCCCCC),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onClearQueue() }
                                    .padding(8.dp)
                            )
                            IconButton(onClick = { showQueueDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(queue) { idx, qTrack ->
                            val isCur = qTrack.id == track.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCur) Color(0x33FFFFFF) else Color.Transparent)
                                    .clickable { onPlayTrackFromQueue(qTrack) }
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF222222))
                                ) {
                                    AsyncImage(
                                        model = qTrack.coverUrl,
                                        contentDescription = qTrack.title,
                                        contentScale = ContentScale.Crop,
                                        colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = qTrack.title,
                                        color = if (isCur) Color.White else Color(0xFFDDDDDD),
                                        fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = qTrack.artist,
                                        color = Color(0xFF777777),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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

private fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format("%d:%02d", m, s)
}
