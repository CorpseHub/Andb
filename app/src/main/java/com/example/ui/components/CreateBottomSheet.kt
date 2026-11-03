package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBottomSheet(
    isGuest: Boolean,
    username: String,
    onDismiss: () -> Unit,
    onCreatePlaylist: (name: String, desc: String) -> Unit,
    onUploadTrack: (title: String, artist: String, genre: String, isPublic: Boolean, isVideo: Boolean, uri: String, coverUri: String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentSubView by remember { mutableStateOf("menu") } // "menu", "new_playlist", "upload"

    // Playlist form state
    var playlistName by remember { mutableStateOf("") }
    var playlistDesc by remember { mutableStateOf("") }

    // Upload form state
    var uploadTitle by remember { mutableStateOf("") }
    var uploadArtist by remember { mutableStateOf(username) }
    var uploadGenre by remember { mutableStateOf("Lo-fi") }
    var isPublicVisibility by remember { mutableStateOf(true) }
    var isVideoMedia by remember { mutableStateOf(false) }
    var ownsRights by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .testTag("create_bottom_sheet")
        ) {
            when (currentSubView) {
                "menu" -> {
                    Text(
                        text = "Create on Larpify",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    CreateOptionItem(
                        title = "Playlist",
                        subtitle = "Build a playlist with songs or episodes",
                        icon = Icons.Default.QueueMusic,
                        isLocked = false,
                        testTag = "create_playlist_option",
                        onClick = { currentSubView = "new_playlist" }
                    )

                    CreateOptionItem(
                        title = "Upload Track / Video",
                        subtitle = if (isGuest) "Locked for guests • Sign up to upload" else "Upload MP3, WAV, or MP4 directly",
                        icon = Icons.Default.CloudUpload,
                        isLocked = isGuest,
                        testTag = "upload_track_option",
                        onClick = {
                            if (isGuest) {
                                onShowToast("Sign up free to upload on Larpify")
                            } else {
                                currentSubView = "upload"
                            }
                        }
                    )

                    CreateOptionItem(
                        title = "Folder",
                        subtitle = "Organize your playlists into folders",
                        icon = Icons.Default.CreateNewFolder,
                        isLocked = false,
                        testTag = "create_folder_option",
                        onClick = {
                            onShowToast("Folder created")
                            onDismiss()
                        }
                    )
                }

                "new_playlist" -> {
                    Text(
                        text = "Give your playlist a name",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist name") },
                        placeholder = { Text("My Larpify Playlist #1") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("playlist_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text("Description (optional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("playlist_desc_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { currentSubView = "menu" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Cancel", color = Color(0xFFB3B3B3))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                onCreatePlaylist(
                                    playlistName.ifBlank { "My Larpify Playlist" },
                                    playlistDesc
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("submit_create_playlist_button")
                        ) {
                            Text("Create", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                "upload" -> {
                    Text(
                        text = "Upload to Larpify",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Media Type Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isVideoMedia) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF282828))
                                .clickable { isVideoMedia = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = if (!isVideoMedia) MaterialTheme.colorScheme.primary else Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audio (MP3)", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVideoMedia) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF282828))
                                .clickable { isVideoMedia = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = if (isVideoMedia) MaterialTheme.colorScheme.primary else Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Video (MP4)", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        label = { Text("Track Title *") },
                        placeholder = { Text("e.g. Midnight Beats") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("upload_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uploadArtist,
                        onValueChange = { uploadArtist = it },
                        label = { Text("Artist Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uploadGenre,
                        onValueChange = { uploadGenre = it },
                        label = { Text("Genre (e.g. Lo-fi, Rock, Synthwave)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rights ownership checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { ownsRights = !ownsRights }
                    ) {
                        Checkbox(
                            checked = ownsRights,
                            onCheckedChange = { ownsRights = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("rights_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I own or have the rights to this audio/video.",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    if (isUploading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Uploading to Larpify... ${(uploadProgress * 100).toInt()}%",
                            color = Color(0xFFB3B3B3),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (uploadTitle.isBlank()) {
                                onShowToast("Please enter a title")
                                return@Button
                            }
                            if (!ownsRights) {
                                onShowToast("Please confirm you own or have the rights")
                                return@Button
                            }

                            isUploading = true
                            coroutineScope.launch {
                                for (i in 1..10) {
                                    delay(120)
                                    uploadProgress = i / 10f
                                }
                                val demoMedia = if (isVideoMedia) {
                                    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                                } else {
                                    "https://archive.org/download/free-lofi-beats-to-relax-to/01%20Coffee%20Shop%20Vibes.mp3"
                                }
                                onUploadTrack(
                                    uploadTitle,
                                    uploadArtist,
                                    uploadGenre,
                                    isPublicVisibility,
                                    isVideoMedia,
                                    demoMedia,
                                    "https://picsum.photos/seed/${uploadTitle.hashCode()}/600/600"
                                )
                                isUploading = false
                                onDismiss()
                            }
                        },
                        enabled = !isUploading && uploadTitle.isNotBlank() && ownsRights,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("submit_upload_button"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = if (isUploading) "Uploading..." else "Upload Track",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isLocked: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isLocked) Color(0xFF757575) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isLocked) Color(0xFF757575) else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color(0xFFB3B3B3),
                fontSize = 12.sp
            )
        }

        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
