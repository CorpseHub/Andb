package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.NavigationTab
import com.example.model.PlaybackSettings
import com.example.model.Playlist
import com.example.model.SoundPreset
import com.example.model.Track
import com.example.model.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userSession: UserSession,
    guestTimeRemaining: String,
    currentTrack: Track,
    isPlaying: Boolean,
    playbackSettings: PlaybackSettings,
    userUploads: List<Track>,
    playlists: List<Playlist>,
    initialSection: String? = null,
    onNavigateToThemes: () -> Unit,
    onOpenMusicPlayerPanel: () -> Unit,
    onUpdatePlaybackSettings: (PlaybackSettings) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onUploadTrack: (title: String, artist: String, genre: String, isPub: Boolean, isVid: Boolean, uri: String, coverUri: String) -> Unit,
    onDeleteUpload: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    onLogout: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onShowToast: (String) -> Unit,
    onExpandNowPlaying: () -> Unit
) {
    var activeSection by rememberSaveable { mutableStateOf(initialSection) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteAccountSheet by remember { mutableStateOf(false) }

    // If sub-screen is active, handle Back to return to Settings root
    if (activeSection != null) {
        BackHandler {
            activeSection = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        if (activeSection == null) {
            // Main Settings Root
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
            ) {
                // Search Field on top
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search settings...", color = Color(0xFF888888)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF888888))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF888888))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("settings_search_input")
                    )
                }

                // "Now playing" mini-card at top when audio is playing or has track
                if (isPlaying || currentTrack.title.isNotEmpty()) {
                    item {
                        NowPlayingMiniCard(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            onClick = onExpandNowPlaying
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Settings Group Items
                val allSections = listOf(
                    SettingsItem("Account", "Profile, email, passwords, membership", Icons.Default.Person, "account"),
                    SettingsItem("Create", "New playlist, track uploads, manage uploads", Icons.Default.Add, "create"),
                    SettingsItem("Music Player", "Floating music player panel, queue & controls", Icons.Default.Audiotrack, "music_player"),
                    SettingsItem("Sound and Playback", "Presets, crossfade, gapless, quality", Icons.Default.Tune, "sound_playback"),
                    SettingsItem("Appearance", "Themes, glow effects, reduce motion", Icons.Default.Palette, "appearance"),
                    SettingsItem("Security", "Passkeys, 2FA, active device sessions", Icons.Default.Security, "security"),
                    SettingsItem("Privacy and Safety", "Profile visibility, blocking, data controls", Icons.Default.Shield, "privacy_safety"),
                    SettingsItem("About and Legal", "Zero ads counter, licenses, policy, version", Icons.Default.Info, "about_legal")
                )

                val filtered = if (searchQuery.isBlank()) {
                    allSections
                } else {
                    allSections.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true)
                    }
                }

                items(filtered) { item ->
                    SettingsRow(
                        title = item.title,
                        subtitle = item.subtitle,
                        icon = item.icon,
                        onClick = {
                            if (item.id == "music_player") {
                                onOpenMusicPlayerPanel()
                            } else if (item.id == "appearance") {
                                onNavigateToThemes()
                            } else {
                                activeSection = item.id
                            }
                        }
                    )
                }

                // 0 Ads Counter Footer Note
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Larpify • 0 ads served • Free forever",
                            color = Color(0xFF666666),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Sub-Screen View
            Column(modifier = Modifier.fillMaxSize()) {
                // Sub-Screen Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { activeSection = null },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    val title = when (activeSection) {
                        "account" -> "Account"
                        "create" -> "Create"
                        "sound_playback" -> "Sound and Playback"
                        "security" -> "Security"
                        "privacy_safety" -> "Privacy and Safety"
                        "about_legal" -> "About and Legal"
                        else -> "Settings"
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFF282828), thickness = 1.dp)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when (activeSection) {
                        "account" -> AccountSubScreen(
                            userSession = userSession,
                            guestTimeRemaining = guestTimeRemaining,
                            onOpenAuth = onOpenAuthDialog,
                            onLogout = {
                                onLogout()
                                activeSection = null
                            },
                            onShowToast = onShowToast
                        )
                        "create" -> CreateSubScreen(
                            userSession = userSession,
                            userUploads = userUploads,
                            playlists = playlists,
                            onCreatePlaylist = onCreatePlaylist,
                            onUploadTrack = onUploadTrack,
                            onDeleteUpload = onDeleteUpload,
                            onShowToast = onShowToast
                        )
                        "sound_playback" -> SoundPlaybackSubScreen(
                            settings = playbackSettings,
                            onUpdate = onUpdatePlaybackSettings,
                            onShowToast = onShowToast
                        )
                        "security" -> SecuritySubScreen(
                            userSession = userSession,
                            onShowToast = onShowToast
                        )
                        "privacy_safety" -> PrivacySafetySubScreen(
                            userSession = userSession,
                            onRequestDeleteAccount = { showDeleteAccountSheet = true },
                            onShowToast = onShowToast
                        )
                        "about_legal" -> AboutLegalSubScreen(
                            adsServed = userSession.adsServedCount,
                            onShowToast = onShowToast
                        )
                    }
                }
            }
        }
    }

    // Delete Account Confirmation Bottom Sheet
    if (showDeleteAccountSheet) {
        DeleteAccountConfirmSheet(
            onDismiss = { showDeleteAccountSheet = false },
            onConfirm = {
                showDeleteAccountSheet = false
                onLogout()
                activeSection = null
                onShowToast("Account and data deleted")
            }
        )
    }
}

private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val id: String
)

@Composable
private fun NowPlayingMiniCard(
    track: Track,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .testTag("now_playing_mini_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2820))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF282828))
            ) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Now Playing",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    fontSize = 11.sp,
                    color = Color(0xFFB3B3B3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                    contentDescription = "Playing",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF242424)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF888888),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Open",
            tint = Color(0xFF555555),
            modifier = Modifier.size(14.dp)
        )
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 1: Account
// -------------------------------------------------------------------------
@Composable
private fun AccountSubScreen(
    userSession: UserSession,
    guestTimeRemaining: String,
    onOpenAuth: () -> Unit,
    onLogout: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var editName by remember { mutableStateOf(userSession.username) }
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E2E2E))
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userSession.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = userSession.email,
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Chip
                    val statusText = if (userSession.isGuest) "Guest • $guestTimeRemaining left" else "Larpify Free Member"
                    val statusColor = if (userSession.isGuest) Color(0xFFFFB74D) else MaterialTheme.colorScheme.primary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (userSession.isGuest) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onOpenAuth,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign up free", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("ACCOUNT ACTIONS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ActionItem(
                title = "Edit Profile",
                subtitle = "Change display name and avatar",
                icon = Icons.Default.Edit,
                onClick = {
                    isEditing = !isEditing
                }
            )

            if (isEditing) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Button(
                    onClick = {
                        isEditing = false
                        onShowToast("Profile name updated to $editName")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }

            ActionItem(
                title = "Change Password",
                subtitle = "Update security credentials",
                icon = Icons.Default.Lock,
                onClick = {
                    onShowToast("Password reset link sent to ${userSession.email}")
                }
            )

            ActionItem(
                title = "Log Out",
                subtitle = "Sign out from this device",
                icon = Icons.Default.PowerSettingsNew,
                titleColor = Color(0xFFFF5252),
                onClick = onLogout
            )
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 2: Create (Playlists + Upload track + My Uploads)
// -------------------------------------------------------------------------
@Composable
private fun CreateSubScreen(
    userSession: UserSession,
    userUploads: List<Track>,
    playlists: List<Playlist>,
    onCreatePlaylist: (String, String) -> Unit,
    onUploadTrack: (String, String, String, Boolean, Boolean, String, String) -> Unit,
    onDeleteUpload: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var playlistName by remember { mutableStateOf("") }
    var playlistDesc by remember { mutableStateOf("") }

    var uploadTitle by remember { mutableStateOf("") }
    var uploadArtist by remember { mutableStateOf(userSession.username) }
    var uploadGenre by remember { mutableStateOf("Lo-fi") }
    var uploadIsPublic by remember { mutableStateOf(true) }
    var uploadIsVideo by remember { mutableStateOf(false) }
    var ownsRights by remember { mutableStateOf(false) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri
            uploadIsVideo = uri.toString().contains(".mp4", ignoreCase = true) || uri.toString().contains(".webm", ignoreCase = true)
            onShowToast("Selected: ${uri.lastPathSegment ?: "media file"}")
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Section A: New Playlist
        item {
            Text("CREATE NEW PLAYLIST", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        placeholder = { Text("e.g. Midnight Beats") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (playlistName.isBlank()) {
                                onShowToast("Please enter a playlist name")
                                return@Button
                            }
                            onCreatePlaylist(playlistName, playlistDesc)
                            playlistName = ""
                            playlistDesc = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Playlist", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section B: Upload Track Flow
        item {
            Text("UPLOAD TRACK (MAX 100MB)", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (userSession.isGuest) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2C241E))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Upload locked for Guests • Sign up free to upload on Larpify",
                                color = Color(0xFFFFB74D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = {
                            if (userSession.isGuest) {
                                onShowToast("Sign up free to upload on Larpify")
                            } else {
                                filePicker.launch(arrayOf("audio/*", "video/*"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A2A2A),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedMediaUri != null) "File Selected" else "Select Audio/Video File (MP3, WAV, MP4)",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        label = { Text("Track Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uploadArtist,
                        onValueChange = { uploadArtist = it },
                        label = { Text("Artist Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uploadGenre,
                        onValueChange = { uploadGenre = it },
                        label = { Text("Genre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Public / Private
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { uploadIsPublic = true }
                        ) {
                            RadioButton(
                                selected = uploadIsPublic,
                                onClick = { uploadIsPublic = true },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Public", color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { uploadIsPublic = false }
                        ) {
                            RadioButton(
                                selected = !uploadIsPublic,
                                onClick = { uploadIsPublic = false },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Private", color = Color.White, fontSize = 13.sp)
                        }
                    }

                    // Rights Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { ownsRights = !ownsRights }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = ownsRights,
                            onCheckedChange = { ownsRights = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "I own or have rights to stream this music on Larpify",
                            color = Color(0xFFB3B3B3),
                            fontSize = 11.sp
                        )
                    }

                    if (isUploading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Uploading... ${(uploadProgress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (userSession.isGuest) {
                                onShowToast("Sign up free to upload on Larpify")
                                return@Button
                            }
                            if (uploadTitle.isBlank()) {
                                onShowToast("Please enter a track title")
                                return@Button
                            }
                            if (!ownsRights) {
                                onShowToast("Please confirm rights ownership")
                                return@Button
                            }
                            isUploading = true
                            scope.launch {
                                for (i in 1..10) {
                                    delay(80)
                                    uploadProgress = i / 10f
                                }
                                val uriStr = selectedMediaUri?.toString() ?: "android.resource://com.example/raw/sample"
                                val coverStr = "https://picsum.photos/seed/${uploadTitle.hashCode()}/600/600"
                                onUploadTrack(
                                    uploadTitle, uploadArtist, uploadGenre, uploadIsPublic, uploadIsVideo, uriStr, coverStr
                                )
                                isUploading = false
                                uploadTitle = ""
                                selectedMediaUri = null
                                uploadProgress = 0f
                            }
                        },
                        enabled = !isUploading && uploadTitle.isNotBlank() && ownsRights && !userSession.isGuest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload to Larpify", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section C: My Uploads
        item {
            Text("MY UPLOADS (${userUploads.size})", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (userUploads.isEmpty()) {
            item {
                Text(
                    text = "No tracks uploaded yet.",
                    color = Color(0xFF666666),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(userUploads) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF282828))
                    ) {
                        AsyncImage(
                            model = track.coverUrl,
                            contentDescription = track.title,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(track.artist, color = Color(0xFF888888), fontSize = 11.sp)
                    }
                    IconButton(onClick = { onDeleteUpload(track.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 3: Sound and Playback
// -------------------------------------------------------------------------
@Composable
private fun SoundPlaybackSubScreen(
    settings: PlaybackSettings,
    onUpdate: (PlaybackSettings) -> Unit,
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text("EQUALIZER & SOUND PRESET", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onUpdate(settings.copy(soundPreset = SoundPreset.NORMAL))
                                onShowToast("Sound Preset: Normal")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (settings.soundPreset == SoundPreset.NORMAL) MaterialTheme.colorScheme.primary else Color(0xFF2A2A2A),
                                contentColor = if (settings.soundPreset == SoundPreset.NORMAL) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Normal", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onUpdate(settings.copy(soundPreset = SoundPreset.BASS_BOOST))
                                onShowToast("Sound Preset: Bass Boost (+8dB low shelf)")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (settings.soundPreset == SoundPreset.BASS_BOOST) MaterialTheme.colorScheme.primary else Color(0xFF2A2A2A),
                                contentColor = if (settings.soundPreset == SoundPreset.BASS_BOOST) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bass Boost", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("CROSSFADE (${settings.crossfadeSec}s)", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Slider(
                        value = settings.crossfadeSec.toFloat(),
                        onValueChange = { onUpdate(settings.copy(crossfadeSec = it.toInt())) },
                        valueRange = 0f..12f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color(0xFF333333)
                        )
                    )
                    Text("Allows tracks to blend seamlessly from 0 to 12 seconds", color = Color(0xFF888888), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("PLAYBACK OPTIONS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ToggleRow(
                        title = "Gapless Playback",
                        subtitle = "Seamless audio transitions between consecutive songs",
                        isChecked = settings.gaplessPlayback,
                        onCheckedChange = { onUpdate(settings.copy(gaplessPlayback = it)) }
                    )
                    HorizontalDivider(color = Color(0xFF282828), modifier = Modifier.padding(vertical = 8.dp))
                    ToggleRow(
                        title = "Autoplay Similar Music",
                        subtitle = "Keep listening when your queue finishes",
                        isChecked = settings.autoplaySimilar,
                        onCheckedChange = { onUpdate(settings.copy(autoplaySimilar = it)) }
                    )
                    HorizontalDivider(color = Color(0xFF282828), modifier = Modifier.padding(vertical = 8.dp))
                    ToggleRow(
                        title = "Data Saver Mode",
                        subtitle = "Reduce network streaming bitrate when on cellular data",
                        isChecked = settings.dataSaver,
                        onCheckedChange = { onUpdate(settings.copy(dataSaver = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("AUDIO QUALITY", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    listOf("Normal (160kbps)", "High (320kbps)", "Hi-Fi Lossless (FLAC)").forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdate(settings.copy(audioQuality = quality)) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.audioQuality.contains(quality.substringBefore(" ")),
                                onClick = { onUpdate(settings.copy(audioQuality = quality)) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(quality, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 4: Security
// -------------------------------------------------------------------------
@Composable
private fun SecuritySubScreen(
    userSession: UserSession,
    onShowToast: (String) -> Unit
) {
    var passkeys by remember { mutableStateOf(userSession.passkeys.toMutableList()) }
    var twoFactor by remember { mutableStateOf(userSession.twoFactorEnabled) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text("PASSKEYS & BIOMETRICS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    passkeys.forEachIndexed { idx, pk ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(pk, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            IconButton(onClick = {
                                passkeys.removeAt(idx)
                                onShowToast("Passkey removed")
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF888888), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            passkeys.add("Passkey ${passkeys.size + 1} (FIDO2)")
                            onShowToast("New passkey added via Credential Manager")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A), contentColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Passkey", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("TWO-FACTOR AUTHENTICATION", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ToggleRow(
                        title = "Require 2FA on New Devices",
                        subtitle = "Send a secure confirmation prompt before login",
                        isChecked = twoFactor,
                        onCheckedChange = {
                            twoFactor = it
                            onShowToast(if (it) "2FA Enabled" else "2FA Disabled")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("ACTIVE SESSIONS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Current Android Device (Active now)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Android • Larpify Streaming Engine", color = Color(0xFF888888), fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("THIS DEVICE", color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onShowToast("Logged out of 2 other background sessions") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A), contentColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log out of other devices", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 5: Privacy and Safety
// -------------------------------------------------------------------------
@Composable
private fun PrivacySafetySubScreen(
    userSession: UserSession,
    onRequestDeleteAccount: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var isPrivate by remember { mutableStateOf(userSession.isPrivateProfile) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text("PROFILE VISIBILITY", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ToggleRow(
                        title = "Private Profile",
                        subtitle = "Only approved listeners can see your playlists and uploads",
                        isChecked = isPrivate,
                        onCheckedChange = {
                            isPrivate = it
                            onShowToast(if (it) "Profile set to Private" else "Profile is Public")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("DATA & PRIVACY CONTROLS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ActionItem(
                title = "Blocked Users (0)",
                subtitle = "Manage restricted listeners and profiles",
                icon = Icons.Default.Shield,
                onClick = { onShowToast("No users currently blocked") }
            )

            ActionItem(
                title = "Reported Tracks (0)",
                subtitle = "View status of content flags",
                icon = Icons.Default.Info,
                onClick = { onShowToast("No tracks reported") }
            )

            ActionItem(
                title = "Download My Data",
                subtitle = "Export JSON copy of playlists, likes & history",
                icon = Icons.Default.CloudUpload,
                onClick = { onShowToast("Export prepared: larpify_backup.json saved") }
            )

            ActionItem(
                title = "Clear My Data",
                subtitle = "Reset cache, search history, and queue",
                icon = Icons.Default.Clear,
                onClick = { onShowToast("Cache & local search history cleared") }
            )

            ActionItem(
                title = "Delete Account",
                subtitle = "Permanently remove your Larpify account and uploads",
                icon = Icons.Default.Delete,
                titleColor = Color(0xFFFF5252),
                onClick = onRequestDeleteAccount
            )
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Screen 6: About and Legal
// -------------------------------------------------------------------------
@Composable
private fun AboutLegalSubScreen(
    adsServed: Int,
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Tiny 0 ads counter
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("0", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("0 ads served", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Zero ads. Zero interruptions. 100% free music.", color = Color(0xFFB3B3B3), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("LEGAL POLICIES & CREDITS", color = Color(0xFF888888), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ActionItem(
                title = "Privacy Policy",
                subtitle = "How Larpify respects your zero-telemetry privacy",
                icon = Icons.Default.Policy,
                onClick = { onShowToast("Larpify Privacy Policy: Zero user data sold or tracked.") }
            )

            ActionItem(
                title = "Terms of Service",
                subtitle = "Community guidelines and open music agreements",
                icon = Icons.AutoMirrored.Filled.Help,
                onClick = { onShowToast("Terms: Larpify is an open music & stream player.") }
            )

            ActionItem(
                title = "Content Policy & CC Licenses",
                subtitle = "Creative Commons, Jamendo & Open Archive licenses",
                icon = Icons.Default.Info,
                onClick = { onShowToast("Content licensed under CC-BY / CC-SA / Public Domain.") }
            )

            ActionItem(
                title = "Copyright Takedown Form (DMCA)",
                subtitle = "Submit verified takedown notice for copyright holders",
                icon = Icons.Default.Shield,
                onClick = { onShowToast("DMCA notice email: dmca@larpify.app") }
            )

            ActionItem(
                title = "Report a Problem",
                subtitle = "Send crash reports or feedback directly to team",
                icon = Icons.Default.Edit,
                onClick = { onShowToast("Feedback registered. Thank you!") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Larpify for Android", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Version 1.2.0 • Build 2026", color = Color(0xFF666666), fontSize = 12.sp)
                Text("Free music. Zero ads. Zero chill.", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
            }
        }
    }
}

// -------------------------------------------------------------------------
// Helper Composables
// -------------------------------------------------------------------------
@Composable
private fun ActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF222222)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = titleColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color(0xFF888888), fontSize = 11.sp)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF444444), modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Color(0xFF888888), fontSize = 11.sp)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = Color(0xFF333333)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountConfirmSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1414),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Delete Larpify Account?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This action is permanent. All your uploaded tracks, saved playlists, and local listening history will be wiped immediately.",
                fontSize = 13.sp,
                color = Color(0xFFB3B3B3)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Permanent Deletion", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A), contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", fontWeight = FontWeight.Normal)
            }
        }
    }
}
