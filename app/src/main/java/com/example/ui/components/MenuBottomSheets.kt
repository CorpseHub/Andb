package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Playlist
import com.example.model.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongMenuBottomSheet(
    track: Track,
    isLiked: Boolean,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onAddToQueue: (Track) -> Unit,
    onPlayNext: (Track) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddToPlaylist: (String, String) -> Unit,
    onShare: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPlaylistPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .testTag("song_menu_sheet")
        ) {
            // Header: Cover + Track details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = track.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = track.artist,
                        color = Color(0xFFB3B3B3),
                        fontSize = 13.sp
                    )
                }
            }

            if (!showPlaylistPicker) {
                MenuItem(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    title = if (isLiked) "Unlike" else "Like",
                    onClick = {
                        onToggleLike(track.id)
                        onDismiss()
                    }
                )

                MenuItem(
                    icon = Icons.Default.Add,
                    title = "Add to playlist",
                    onClick = { showPlaylistPicker = true }
                )

                MenuItem(
                    icon = Icons.Default.Queue,
                    title = "Add to queue",
                    onClick = {
                        onAddToQueue(track)
                        onDismiss()
                    }
                )

                MenuItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Play next",
                    onClick = {
                        onPlayNext(track)
                        onDismiss()
                    }
                )

                MenuItem(
                    icon = Icons.Default.Share,
                    title = "Share",
                    onClick = {
                        onShare()
                        onDismiss()
                    }
                )
            } else {
                Text(
                    text = "Select Playlist",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                playlists.filter { !it.isSystem }.forEach { playlist ->
                    MenuItem(
                        icon = Icons.Default.Queue,
                        title = playlist.title,
                        onClick = {
                            onAddToPlaylist(playlist.id, track.id)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMenuBottomSheet(
    playlist: Playlist,
    onDismiss: () -> Unit,
    onAddAllSongs: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .testTag("playlist_menu_sheet")
        ) {
            if (!showDeleteConfirm) {
                Text(
                    text = playlist.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MenuItem(
                    icon = Icons.Default.PlaylistAdd,
                    title = "Add all songs to this playlist",
                    onClick = {
                        onAddAllSongs()
                        onDismiss()
                    }
                )

                MenuItem(
                    icon = Icons.Default.Share,
                    title = "Share playlist",
                    onClick = {
                        onShare()
                        onDismiss()
                    }
                )

                if (!playlist.isSystem) {
                    MenuItem(
                        icon = Icons.Default.Delete,
                        title = "Delete playlist",
                        titleColor = Color(0xFFFF5252),
                        onClick = { showDeleteConfirm = true }
                    )
                }
            } else {
                Text(
                    text = "Delete this playlist?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This will remove \"${playlist.title}\" from your library.",
                    color = Color(0xFFB3B3B3),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { showDeleteConfirm = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282828)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        modifier = Modifier.weight(1f).testTag("confirm_delete_playlist_button")
                    ) {
                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = titleColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
