package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.RepeatMode
import com.example.model.SoundPreset
import com.example.model.Track
import com.example.model.TrackSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerPanel(
    currentTrack: Track,
    isPlaying: Boolean,
    progressSec: Int,
    durationSec: Int,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    soundPreset: SoundPreset,
    volumePercent: Int,
    queue: List<Track>,
    queueIndex: Int,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSetSoundPreset: (SoundPreset) -> Unit,
    onSetVolume: (Int) -> Unit,
    onPlayTrackFromQueue: (Track) -> Unit,
    onRemoveFromQueue: (Int) -> Unit,
    onMoveQueueItem: (from: Int, to: Int) -> Unit,
    onClearQueue: () -> Unit,
    onPlayRandom: () -> Unit,
    onAddFiles: (List<Uri>) -> Unit,
    onOpenAddYouTube: () -> Unit,
    onLoadCommunitySongs: () -> Unit,
    onClose: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isMinimized by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAddFiles(uris)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color(0xFF141414),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .testTag("music_player_panel")
        ) {
            // Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Music Player",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${queue.size} songs",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isMinimized = !isMinimized },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isMinimized) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isMinimized) "Expand" else "Minimize",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }

            // Accent Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF00E676),
                                Color.Transparent
                            )
                        )
                    )
            )

            if (!isMinimized) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(horizontal = 16.dp)
                ) {
                    // Now-Playing Card
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        NowPlayingCard(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            isEmpty = queue.isEmpty()
                        )
                    }

                    // Seek Bar
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatSeconds(progressSec),
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp
                            )
                            Text(
                                text = formatSeconds(durationSec),
                                color = Color(0xFFB3B3B3),
                                fontSize = 11.sp
                            )
                        }
                        Slider(
                            value = if (durationSec > 0) progressSec.toFloat() / durationSec else 0f,
                            onValueChange = { frac ->
                                onSeek((frac * durationSec).toInt())
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color(0xFF333333)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                        )
                    }

                    // Playback Controls
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onSkipPrevious) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable { onPlayPause() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            IconButton(onClick = onSkipNext) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Volume Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Volume",
                                tint = Color(0xFFB3B3B3),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "volume $volumePercent%",
                                color = Color(0xFFB3B3B3),
                                fontSize = 12.sp,
                                modifier = Modifier.width(90.dp)
                            )
                            Slider(
                                value = volumePercent.toFloat(),
                                onValueChange = { onSetVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                            )
                        }
                    }

                    // Mode Buttons: shuffle, repeat, bass boost, normal
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeChip(
                                label = "shuffle",
                                icon = Icons.Default.Shuffle,
                                isActive = isShuffle,
                                onClick = onToggleShuffle,
                                modifier = Modifier.weight(1f)
                            )
                            val repeatIcon = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
                            val repeatLabel = when (repeatMode) {
                                RepeatMode.OFF -> "repeat: off"
                                RepeatMode.ALL -> "repeat: all"
                                RepeatMode.ONE -> "repeat: one"
                            }
                            ModeChip(
                                label = repeatLabel,
                                icon = repeatIcon,
                                isActive = repeatMode != RepeatMode.OFF,
                                onClick = onToggleRepeat,
                                modifier = Modifier.weight(1.2f)
                            )
                            val isYouTube = currentTrack.source == TrackSource.YOUTUBE
                            ModeChip(
                                label = "bass boost",
                                icon = Icons.Default.GraphicEq,
                                isActive = soundPreset == SoundPreset.BASS_BOOST && !isYouTube,
                                onClick = {
                                    if (isYouTube) {
                                        onShowToast("Not available for YouTube tracks")
                                    } else {
                                        onSetSoundPreset(SoundPreset.BASS_BOOST)
                                    }
                                },
                                modifier = Modifier.weight(1.1f)
                            )
                            ModeChip(
                                label = "normal",
                                icon = null,
                                isActive = soundPreset == SoundPreset.NORMAL,
                                onClick = { onSetSoundPreset(SoundPreset.NORMAL) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Search Songs Row + Random button
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("search songs...", fontSize = 12.sp, color = Color(0xFF888888)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF888888),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color(0xFF333333),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onPlayRandom,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF242424),
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("random", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Song List
                    val filteredQueue = if (searchQuery.isBlank()) {
                        queue
                    } else {
                        queue.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                            it.artist.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredQueue.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "no songs yet",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "drop audio files here or add a YouTube link",
                                        color = Color(0xFF888888),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onLoadCommunitySongs,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Load Community Songs", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(filteredQueue) { index, track ->
                            val isCurrent = track.id == currentTrack.id
                            SongRow(
                                track = track,
                                isPlaying = isCurrent && isPlaying,
                                isCurrent = isCurrent,
                                canMoveUp = index > 0,
                                canMoveDown = index < filteredQueue.size - 1,
                                onClick = { onPlayTrackFromQueue(track) },
                                onRemove = { onRemoveFromQueue(index) },
                                onMoveUp = { onMoveQueueItem(index, index - 1) },
                                onMoveDown = { onMoveQueueItem(index, index + 1) }
                            )
                        }
                    }

                    // Bottom action buttons
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { filePicker.launch(arrayOf("audio/*", "video/*")) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF222222),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add files", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = onOpenAddYouTube,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B1E1E),
                                    contentColor = Color(0xFFFF5252)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Icon(Icons.Default.Subscriptions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("YouTube link", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onLoadCommunitySongs,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E2D24),
                                    contentColor = Color(0xFF00E676)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Community Songs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = onClearQueue,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF222222),
                                    contentColor = Color(0xFFB3B3B3)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.9f)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NowPlayingCard(
    track: Track,
    isPlaying: Boolean,
    isEmpty: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1F1F1F))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            if (!isEmpty && track.coverUrl.isNotBlank()) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
            if (isPlaying) {
                EqualizerMiniAnimation()
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isEmpty) "no songs yet" else track.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isEmpty) "drop audio files here or add a YouTube link" else track.artist,
                color = Color(0xFFB3B3B3),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SongRow(
    track: Track,
    isPlaying: Boolean,
    isCurrent: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) Color(0xFF232824) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            if (isPlaying) {
                EqualizerMiniAnimation()
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artist,
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Source badge: FILE, CATALOG, YOUTUBE
                val badgeText = when (track.source) {
                    TrackSource.YOUTUBE -> "YOUTUBE"
                    TrackSource.FILE -> "FILE"
                    TrackSource.CATALOG -> "CATALOG"
                }
                val badgeColor = when (track.source) {
                    TrackSource.YOUTUBE -> Color(0xFFFF5252)
                    TrackSource.FILE -> Color(0xFF64B5F6)
                    TrackSource.CATALOG -> MaterialTheme.colorScheme.primary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = formatSeconds(track.durationSec),
            color = Color(0xFF888888),
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        // Reorder arrows
        if (canMoveUp) {
            IconButton(onClick = onMoveUp, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = Color(0xFF888888), modifier = Modifier.size(16.dp))
            }
        }
        if (canMoveDown) {
            IconButton(onClick = onMoveDown, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = Color(0xFF888888), modifier = Modifier.size(16.dp))
            }
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFF777777), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Color(0xFF223326) else Color(0xFF222222))
            .border(
                width = 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFB3B3B3),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.White,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EqualizerMiniAnimation() {
    val transition = rememberInfiniteTransition(label = "eq_trans")
    val h1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), AnimRepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), AnimRepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), AnimRepeatMode.Reverse),
        label = "h3"
    )

    Row(
        modifier = Modifier
            .size(24.dp)
            .background(Color(0x99000000), RoundedCornerShape(4.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(h1)
                .background(Color(0xFF00E676), RoundedCornerShape(1.dp))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(h2)
                .background(Color(0xFF00E676), RoundedCornerShape(1.dp))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(h3)
                .background(Color(0xFF00E676), RoundedCornerShape(1.dp))
        )
    }
}

private fun formatSeconds(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%d:%02d", m, s)
}
