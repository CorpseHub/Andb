package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavigationTab
import com.example.theme.ThemeManager
import com.example.ui.components.AddYouTubeBottomSheet
import com.example.ui.components.AuthDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.CreateBottomSheet
import com.example.ui.components.LarpifyTopBar
import com.example.ui.components.MiniPlayer
import com.example.ui.components.MusicPlayerPanel
import com.example.ui.components.NowPlayingSheet
import com.example.ui.components.PlaylistMenuBottomSheet
import com.example.ui.components.ProfileBottomSheet
import com.example.ui.components.SongMenuBottomSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThemesScreen

@Composable
fun LarpifyApp(viewModel: LarpifyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Toast watcher
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Hardware back navigation handler
    BackHandler(enabled = uiState.isNowPlayingExpanded || uiState.selectedPlaylistForDetail != null || uiState.currentTab != NavigationTab.HOME) {
        if (uiState.isNowPlayingExpanded) {
            viewModel.setNowPlayingExpanded(false)
        } else if (uiState.selectedPlaylistForDetail != null) {
            viewModel.closePlaylistDetail()
        } else if (uiState.currentTab != NavigationTab.HOME) {
            viewModel.selectTab(NavigationTab.HOME)
        }
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = uiState.activeTheme.primary,
            background = uiState.activeTheme.background,
            surface = uiState.activeTheme.surface,
            surfaceVariant = uiState.activeTheme.surfaceVariant,
            onBackground = uiState.activeTheme.onBackground,
            onSurface = uiState.activeTheme.onSurface
        )
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = uiState.activeTheme.background,
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Top Bar (only when not in playlist detail)
                    if (uiState.selectedPlaylistForDetail == null) {
                        LarpifyTopBar(
                            currentTab = uiState.currentTab,
                            userSession = uiState.userSession,
                            guestTimeRemaining = uiState.guestRemainingTimeText,
                            onAvatarClick = { viewModel.setShowProfileSheet(true) },
                            onActionClick = {
                                if (uiState.currentTab == NavigationTab.SEARCH) {
                                    viewModel.showToast("Voice search listening...")
                                } else {
                                    viewModel.selectTab(NavigationTab.SEARCH)
                                }
                            },
                            onAddClick = {
                                viewModel.openSettingsSection("create")
                            }
                        )
                    }

                    // Active Screen
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.selectedPlaylistForDetail != null) {
                            PlaylistDetailScreen(
                                playlist = uiState.selectedPlaylistForDetail!!,
                                allTracks = viewModel.getAllTracks(),
                                currentPlayingTrackId = uiState.currentTrack.id,
                                isPlaying = uiState.isPlaying,
                                onBack = { viewModel.closePlaylistDetail() },
                                onPlayAll = { tracks ->
                                    if (tracks.isNotEmpty()) viewModel.playTrack(tracks.first(), tracks)
                                },
                                onShuffleAll = { tracks ->
                                    if (tracks.isNotEmpty()) {
                                        val shuffled = tracks.shuffled()
                                        viewModel.playTrack(shuffled.first(), shuffled)
                                    }
                                },
                                onTrackSelect = { track -> viewModel.playTrack(track) },
                                onTrackMenuClick = { track -> viewModel.openSongMenu(track) },
                                onPlaylistMenuClick = { pl -> viewModel.openPlaylistMenu(pl) },
                                onAddAllSongs = { viewModel.addAllSongsToPlaylist(uiState.selectedPlaylistForDetail!!.id) },
                                onShowToast = { msg -> viewModel.showToast(msg) }
                            )
                        } else {
                            when (uiState.currentTab) {
                                NavigationTab.HOME -> {
                                    HomeScreen(
                                        filterChip = uiState.homeFilterChip,
                                        onFilterChipSelect = { viewModel.setHomeFilterChip(it) },
                                        allTracks = viewModel.getAllTracks(),
                                        playlists = uiState.playlists,
                                        userUploads = uiState.userUploads,
                                        onTrackSelect = { track -> viewModel.playTrack(track) },
                                        onPlaylistSelect = { pl -> viewModel.openPlaylistDetail(pl) }
                                    )
                                }
                                NavigationTab.SEARCH -> {
                                    SearchScreen(
                                        searchQuery = uiState.searchQuery,
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                        selectedCategory = uiState.selectedCategory,
                                        onCategorySelect = { viewModel.selectCategory(it) },
                                        allTracks = viewModel.getAllTracks(),
                                        playlists = uiState.playlists,
                                        isSearchingApi = uiState.isSearchingApi,
                                        onTrackSelect = { track -> viewModel.playTrack(track) },
                                        onPlaylistSelect = { pl -> viewModel.openPlaylistDetail(pl) },
                                        onTrackMenuClick = { track -> viewModel.openSongMenu(track) },
                                        onVoiceSearchClick = { viewModel.showToast("Voice search listening...") }
                                    )
                                }
                                NavigationTab.LIBRARY -> {
                                    LibraryScreen(
                                        playlists = uiState.playlists,
                                        userUploads = uiState.userUploads,
                                        likedCount = uiState.likedTrackIds.size,
                                        isGridView = uiState.isLibraryGridView,
                                        onToggleView = { viewModel.toggleLibraryView() },
                                        onPlaylistSelect = { pl -> viewModel.openPlaylistDetail(pl) },
                                        onPlaylistMenuClick = { pl -> viewModel.openPlaylistMenu(pl) },
                                        onCreateClick = { viewModel.openSettingsSection("create") },
                                        onImportDeviceSongs = { uris -> viewModel.importMultipleDeviceSongs(uris) },
                                        onSyncApiSongs = { viewModel.syncMoreFromApi("trending top hits") },
                                        onSyncJamendo = { viewModel.syncJamendoTracks("lofi chill") }
                                    )
                                }
                                NavigationTab.THEMES -> {
                                    ThemesScreen(
                                        activeTheme = uiState.activeTheme,
                                        onSelectTheme = { theme -> viewModel.setTheme(theme) }
                                    )
                                }
                                NavigationTab.SETTINGS -> {
                                    SettingsScreen(
                                        userSession = uiState.userSession,
                                        guestTimeRemaining = uiState.guestRemainingTimeText,
                                        currentTrack = uiState.currentTrack,
                                        isPlaying = uiState.isPlaying,
                                        playbackSettings = uiState.playbackSettings,
                                        userUploads = uiState.userUploads,
                                        playlists = uiState.playlists,
                                        initialSection = uiState.activeSettingsSection,
                                        onNavigateToThemes = { viewModel.selectTab(NavigationTab.THEMES) },
                                        onOpenMusicPlayerPanel = { viewModel.setShowMusicPlayerPanel(true) },
                                        onUpdatePlaybackSettings = { viewModel.updatePlaybackSettings(it) },
                                        onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) },
                                        onUploadTrack = { title, artist, genre, isPub, isVid, uri, cover ->
                                            viewModel.uploadTrack(title, artist, genre, isPub, isVid, uri, cover)
                                        },
                                        onDeleteUpload = { viewModel.deleteUpload(it) },
                                        onLogin = { email, user -> viewModel.login(email, user) },
                                        onLogout = { viewModel.logout() },
                                        onOpenAuthDialog = { viewModel.setShowAuthDialog(true) },
                                        onShowToast = { viewModel.showToast(it) },
                                        onExpandNowPlaying = { viewModel.setNowPlayingExpanded(true) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom floating components: MiniPlayer + BottomNavBar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    MiniPlayer(
                        track = uiState.currentTrack,
                        isPlaying = uiState.isPlaying,
                        progressSec = uiState.currentPositionSec,
                        durationSec = uiState.durationSec,
                        isLiked = uiState.likedTrackIds.contains(uiState.currentTrack.id),
                        onPlayPause = { viewModel.togglePlayPause() },
                        onLikeToggle = { viewModel.toggleLike(uiState.currentTrack.id) },
                        onExpand = { viewModel.setNowPlayingExpanded(true) },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onDevicesClick = { viewModel.setShowMusicPlayerPanel(true) }
                    )

                    BottomNavBar(
                        currentTab = uiState.currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }

                // Full-Screen Now Playing Sheet
                AnimatedVisibility(
                    visible = uiState.isNowPlayingExpanded,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    NowPlayingSheet(
                        track = uiState.currentTrack,
                        isPlaying = uiState.isPlaying,
                        progressSec = uiState.currentPositionSec,
                        durationSec = uiState.durationSec,
                        isLiked = uiState.likedTrackIds.contains(uiState.currentTrack.id),
                        isShuffle = uiState.isShuffle,
                        repeatMode = uiState.repeatMode,
                        queue = uiState.queue,
                        queueIndex = uiState.queueIndex,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSeek = { viewModel.seekTo(it) },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onToggleRepeat = { viewModel.toggleRepeat() },
                        onToggleLike = { viewModel.toggleLike(uiState.currentTrack.id) },
                        onCollapse = { viewModel.setNowPlayingExpanded(false) },
                        onPlayTrackFromQueue = { track -> viewModel.playTrack(track) },
                        onClearQueue = { viewModel.clearQueue() },
                        onShareTrack = { viewModel.showToast("Link copied: https://larpify.app/track/${uiState.currentTrack.id}") },
                        onAttachSurface = { holder -> viewModel.attachVideoSurface(holder) },
                        youTubeController = viewModel.youTubeController,
                        onYouTubeStateChanged = { viewModel.onYouTubeStateChanged(it) },
                        onYouTubeError = { viewModel.onYouTubeError(it) },
                        onYouTubeProgress = { cur, dur -> viewModel.onYouTubeProgress(cur, dur) },
                        onOpenUrl = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                viewModel.showToast("Could not open link")
                            }
                        }
                    )
                }

                // Music Player Panel (Compact dark bottom sheet / floating panel)
                if (uiState.showMusicPlayerPanel) {
                    MusicPlayerPanel(
                        currentTrack = uiState.currentTrack,
                        isPlaying = uiState.isPlaying,
                        progressSec = uiState.currentPositionSec,
                        durationSec = uiState.durationSec,
                        isShuffle = uiState.isShuffle,
                        repeatMode = uiState.repeatMode,
                        soundPreset = uiState.playbackSettings.soundPreset,
                        volumePercent = uiState.playbackSettings.volumePercent,
                        queue = uiState.queue,
                        queueIndex = uiState.queueIndex,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSeek = { viewModel.seekTo(it) },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onToggleRepeat = { viewModel.toggleRepeat() },
                        onSetSoundPreset = { viewModel.setSoundPreset(it) },
                        onSetVolume = { viewModel.setVolume(it) },
                        onPlayTrackFromQueue = { track -> viewModel.playTrack(track) },
                        onRemoveFromQueue = { viewModel.removeFromQueue(it) },
                        onMoveQueueItem = { from, to -> viewModel.moveQueueItem(from, to) },
                        onClearQueue = { viewModel.clearQueue() },
                        onPlayRandom = { viewModel.playRandom() },
                        onAddFiles = { uris -> viewModel.importMultipleDeviceSongs(uris) },
                        onOpenAddYouTube = { viewModel.setShowAddYouTubeSheet(true) },
                        onLoadCommunitySongs = { viewModel.loadCommunitySongs() },
                        onClose = { viewModel.setShowMusicPlayerPanel(false) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                // Add YouTube Link Bottom Sheet
                if (uiState.showAddYouTubeSheet) {
                    AddYouTubeBottomSheet(
                        onDismiss = { viewModel.setShowAddYouTubeSheet(false) },
                        onTracksResolved = { tracks -> viewModel.addYouTubeTracks(tracks) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                // Profile Bottom Sheet
                if (uiState.showProfileSheet) {
                    ProfileBottomSheet(
                        userSession = uiState.userSession,
                        guestTimeRemaining = uiState.guestRemainingTimeText,
                        onDismiss = { viewModel.setShowProfileSheet(false) },
                        onOpenAuth = { viewModel.setShowAuthDialog(true) },
                        onLogout = { viewModel.logout() },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                // Create Bottom Sheet (can also be invoked directly)
                if (uiState.showCreateSheet) {
                    CreateBottomSheet(
                        isGuest = uiState.userSession.isGuest,
                        username = uiState.userSession.username,
                        onDismiss = { viewModel.setShowCreateSheet(false) },
                        onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) },
                        onUploadTrack = { title, artist, genre, isPub, isVid, uri, cover ->
                            viewModel.uploadTrack(title, artist, genre, isPub, isVid, uri, cover)
                        },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                // Song Menu Bottom Sheet
                if (uiState.selectedTrackForMenu != null) {
                    SongMenuBottomSheet(
                        track = uiState.selectedTrackForMenu!!,
                        isLiked = uiState.likedTrackIds.contains(uiState.selectedTrackForMenu!!.id),
                        playlists = uiState.playlists,
                        onDismiss = { viewModel.closeSongMenu() },
                        onAddToQueue = { viewModel.addTrackToQueue(it) },
                        onPlayNext = { viewModel.playNextInQueue(it) },
                        onToggleLike = { viewModel.toggleLike(it) },
                        onAddToPlaylist = { pId, tId -> viewModel.addTrackToPlaylist(pId, tId) },
                        onShare = { viewModel.showToast("Link copied to clipboard") }
                    )
                }

                // Playlist Menu Bottom Sheet
                if (uiState.selectedPlaylistForMenu != null) {
                    PlaylistMenuBottomSheet(
                        playlist = uiState.selectedPlaylistForMenu!!,
                        onDismiss = { viewModel.closePlaylistMenu() },
                        onAddAllSongs = { viewModel.addAllSongsToPlaylist(uiState.selectedPlaylistForMenu!!.id) },
                        onDelete = { viewModel.deletePlaylist(uiState.selectedPlaylistForMenu!!.id) },
                        onShare = { viewModel.showToast("Playlist link copied") }
                    )
                }

                // Auth Dialog (Sign up / Log in)
                if (uiState.showAuthDialog) {
                    AuthDialog(
                        onDismiss = { viewModel.setShowAuthDialog(false) },
                        onLoginSuccess = { email, user -> viewModel.login(email, user) },
                        onContinueAsGuest = { viewModel.showToast("Continuing as Guest (3 days)") },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }
            }
        }
    }
}
