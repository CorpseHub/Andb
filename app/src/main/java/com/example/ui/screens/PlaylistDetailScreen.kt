package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.components.EqualizerAnimation

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    allTracks: List<Track>,
    currentPlayingTrackId: String?,
    isPlaying: Boolean,
    onBack: () -> Unit,
    onPlayAll: (List<Track>) -> Unit,
    onShuffleAll: (List<Track>) -> Unit,
    onTrackSelect: (Track) -> Unit,
    onTrackMenuClick: (Track) -> Unit,
    onPlaylistMenuClick: (Playlist) -> Unit,
    onAddAllSongs: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val playlistTracks = allTracks.filter { playlist.trackIds.contains(it.id) }
    val totalDurationSec = playlistTracks.sumOf { it.durationSec }
    val durationText = "${playlistTracks.size} songs • ${totalDurationSec / 60} min"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("playlist_detail_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header with gradient cover & Back button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF333333),
                                Color(0xFF1E1E1E),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .testTag("playlist_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        AsyncImage(
                            model = playlist.coverUrl,
                            contentDescription = playlist.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = playlist.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = playlist.description,
                        color = Color(0xFFB3B3B3),
                        fontSize = 13.sp
                    )

                    Text(
                        text = "By ${playlist.ownerName} • $durationText",
                        color = Color(0xFFB3B3B3),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Action Buttons Row: Download, Shuffle, 3-dot, Green Play
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onShowToast("Saved to offline cache") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Download",
                            tint = Color(0xFFB3B3B3),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = { onShuffleAll(playlistTracks) }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = Color(0xFFB3B3B3),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = onAddAllSongs) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "Add All Songs",
                            tint = Color(0xFFB3B3B3),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = { onPlaylistMenuClick(playlist) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Playlist Menu",
                            tint = Color(0xFFB3B3B3),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Big Green Play Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayAll(playlistTracks) }
                        .testTag("playlist_play_all_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play All",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Track items
        if (playlistTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This playlist is empty. Add songs to get started!",
                        color = Color(0xFFB3B3B3),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            itemsIndexed(playlistTracks) { index, track ->
                val isCurrent = track.id == currentPlayingTrackId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrackSelect(track) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else Color(0xFF757575),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(28.dp)
                    )

                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = track.title,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artist,
                            color = Color(0xFFB3B3B3),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isCurrent && isPlaying) {
                        EqualizerAnimation(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    IconButton(onClick = { onTrackMenuClick(track) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Track Menu",
                            tint = Color(0xFFB3B3B3)
                        )
                    }
                }
            }
        }
    }
}
