package com.example.ui

import android.app.Application
import android.content.Context
import android.view.SurfaceHolder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.JamendoApiClient
import com.example.api.MusicApiClient
import com.example.data.LarpifyRepository
import com.example.data.MusicCatalog
import com.example.model.NavigationTab
import com.example.model.PlaybackSettings
import com.example.model.Playlist
import com.example.model.RepeatMode
import com.example.model.SoundPreset
import com.example.model.ThemeConfig
import com.example.model.Track
import com.example.model.TrackSource
import com.example.model.UserSession
import com.example.player.LarpifyExoPlayerManager
import com.example.theme.ThemeManager
import com.example.ui.components.YouTubePlayerController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LarpifyUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val currentTrack: Track = MusicCatalog.SampleTracks[0],
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionSec: Int = 0,
    val durationSec: Int = 180,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = MusicCatalog.SampleTracks,
    val queueIndex: Int = 0,
    val activeTheme: ThemeConfig = ThemeManager.MidnightNeon,
    val userSession: UserSession = UserSession(),
    val guestRemainingTimeText: String = "3d 0h left",
    val homeFilterChip: String = "All",
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val playlists: List<Playlist> = emptyList(),
    val likedTrackIds: Set<String> = emptySet(),
    val userUploads: List<Track> = emptyList(),
    val apiTracks: List<Track> = emptyList(),
    val isSearchingApi: Boolean = false,
    val apiSearchResults: List<Track> = emptyList(),
    val isNowPlayingExpanded: Boolean = false,
    val showProfileSheet: Boolean = false,
    val showCreateSheet: Boolean = false,
    val showAuthDialog: Boolean = false,
    val showMusicPlayerPanel: Boolean = false,
    val showAddYouTubeSheet: Boolean = false,
    val activeSettingsSection: String? = null,
    val playbackSettings: PlaybackSettings = PlaybackSettings(),
    val selectedPlaylistForDetail: Playlist? = null,
    val selectedTrackForMenu: Track? = null,
    val selectedPlaylistForMenu: Playlist? = null,
    val toastMessage: String? = null,
    val isLibraryGridView: Boolean = false
)

class LarpifyViewModel(application: Application) : AndroidViewModel(application) {

    val repository = LarpifyRepository(application)
    val player = LarpifyExoPlayerManager(application)
    val youTubeController = YouTubePlayerController()

    private val prefs = application.getSharedPreferences("larpify_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        LarpifyUiState(
            playbackSettings = PlaybackSettings(
                volumePercent = prefs.getInt("pref_volume", 50),
                crossfadeSec = prefs.getInt("pref_crossfade", 0),
                gaplessPlayback = prefs.getBoolean("pref_gapless", true),
                autoplaySimilar = prefs.getBoolean("pref_autoplay", true),
                audioQuality = prefs.getString("pref_quality", "High") ?: "High",
                dataSaver = prefs.getBoolean("pref_datasaver", false),
                soundPreset = if (prefs.getString("pref_preset", "NORMAL") == "BASS_BOOST") SoundPreset.BASS_BOOST else SoundPreset.NORMAL
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        // Collect repository state
        viewModelScope.launch {
            repository.playlists.collectLatest { list ->
                _uiState.value = _uiState.value.copy(playlists = list)
            }
        }
        viewModelScope.launch {
            repository.likedTrackIds.collectLatest { likedSet ->
                _uiState.value = _uiState.value.copy(likedTrackIds = likedSet)
            }
        }
        viewModelScope.launch {
            repository.userUploads.collectLatest { uploads ->
                _uiState.value = _uiState.value.copy(userUploads = uploads)
            }
        }
        viewModelScope.launch {
            repository.currentThemeId.collectLatest { themeId ->
                val theme = ThemeManager.AllThemes.find { it.id == themeId } ?: ThemeManager.MidnightNeon
                _uiState.value = _uiState.value.copy(activeTheme = theme)
            }
        }
        viewModelScope.launch {
            repository.session.collectLatest { session ->
                _uiState.value = _uiState.value.copy(
                    userSession = session,
                    guestRemainingTimeText = repository.getGuestTimeRemainingString()
                )
            }
        }

        // Collect player states
        viewModelScope.launch {
            player.isPlaying.collectLatest { playing ->
                if (_uiState.value.currentTrack.source != TrackSource.YOUTUBE) {
                    _uiState.value = _uiState.value.copy(isPlaying = playing)
                }
            }
        }
        viewModelScope.launch {
            player.isBuffering.collectLatest { buffering ->
                _uiState.value = _uiState.value.copy(isBuffering = buffering)
            }
        }
        viewModelScope.launch {
            player.currentPositionSec.collectLatest { pos ->
                if (_uiState.value.currentTrack.source != TrackSource.YOUTUBE) {
                    _uiState.value = _uiState.value.copy(currentPositionSec = pos)
                }
            }
        }
        viewModelScope.launch {
            player.durationSec.collectLatest { dur ->
                if (_uiState.value.currentTrack.source != TrackSource.YOUTUBE && dur > 0) {
                    _uiState.value = _uiState.value.copy(durationSec = dur)
                }
            }
        }

        viewModelScope.launch {
            repository.apiTracks.collectLatest { apiList ->
                _uiState.value = _uiState.value.copy(apiTracks = apiList)
            }
        }

        // Apply saved preset
        if (_uiState.value.playbackSettings.soundPreset == SoundPreset.BASS_BOOST) {
            player.setBassBoost(true)
        }
        player.setVolume(_uiState.value.playbackSettings.volumePercent / 100f)

        // Auto-fetch initial trending songs from Public Free Music API
        viewModelScope.launch {
            try {
                val apiSongs = MusicApiClient.fetchTrending("chill lofi synthwave electronic", 30)
                if (apiSongs.isNotEmpty()) {
                    repository.addApiTracks(apiSongs)
                }
            } catch (_: Exception) {}
        }

        // Auto-fetch Jamendo Creative Commons streaming tracks for ExoPlayer
        viewModelScope.launch {
            try {
                val jamendoSongs = JamendoApiClient.fetchJamendoTracks("lofi chill", 15)
                if (jamendoSongs.isNotEmpty()) {
                    repository.addApiTracks(jamendoSongs)
                }
            } catch (_: Exception) {}
        }

        // Auto skip on completion from ExoPlayer
        player.setOnCompletionListener {
            skipNext()
        }
    }

    private var searchJob: Job? = null

    fun getAllTracks(): List<Track> {
        return _uiState.value.userUploads + _uiState.value.apiTracks + MusicCatalog.SampleTracks
    }

    fun getTrackById(id: String): Track? {
        return getAllTracks().find { it.id == id }
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            selectedPlaylistForDetail = null
        )
    }

    fun openSettingsSection(section: String) {
        _uiState.value = _uiState.value.copy(
            currentTab = NavigationTab.SETTINGS,
            activeSettingsSection = section,
            selectedPlaylistForDetail = null
        )
    }

    fun setHomeFilterChip(chip: String) {
        _uiState.value = _uiState.value.copy(homeFilterChip = chip)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.trim().length >= 2) {
            searchJob = viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearchingApi = true)
                delay(350)
                val onlineResults = MusicApiClient.searchTracks(query, 30)
                _uiState.value = _uiState.value.copy(
                    apiSearchResults = onlineResults,
                    isSearchingApi = false
                )
                if (onlineResults.isNotEmpty()) {
                    repository.addApiTracks(onlineResults)
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(apiSearchResults = emptyList(), isSearchingApi = false)
        }
    }

    fun syncMoreFromApi(genre: String = "top hits") {
        viewModelScope.launch {
            showToast("Syncing songs from Public Music API...")
            val songs = MusicApiClient.searchTracks(genre, 30)
            if (songs.isNotEmpty()) {
                repository.addApiTracks(songs)
                showToast("Added ${songs.size} new songs from Music API!")
            } else {
                showToast("Unable to fetch songs. Please check connection.")
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setTheme(theme: ThemeConfig) {
        repository.setThemeId(theme.id)
        showToast("Applied theme: ${theme.name}")
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        val q = newQueue ?: _uiState.value.queue
        val index = q.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            queue = q,
            queueIndex = index,
            durationSec = track.durationSec,
            currentPositionSec = 0
        )

        if (track.source == TrackSource.YOUTUBE) {
            player.pause()
            _uiState.value = _uiState.value.copy(isPlaying = true)
            track.youtubeVideoId?.let { vid ->
                youTubeController.loadVideo(vid)
                youTubeController.play()
            }
        } else {
            youTubeController.pause()
            player.playTrack(track)
        }
    }

    fun togglePlayPause() {
        if (_uiState.value.currentTrack.source == TrackSource.YOUTUBE) {
            if (_uiState.value.isPlaying) {
                youTubeController.pause()
                _uiState.value = _uiState.value.copy(isPlaying = false)
            } else {
                youTubeController.play()
                _uiState.value = _uiState.value.copy(isPlaying = true)
            }
        } else {
            player.togglePlayPause()
        }
    }

    fun seekTo(seconds: Int) {
        _uiState.value = _uiState.value.copy(currentPositionSec = seconds)
        if (_uiState.value.currentTrack.source == TrackSource.YOUTUBE) {
            youTubeController.seekTo(seconds)
        } else {
            player.seekTo(seconds)
        }
    }

    fun skipNext() {
        val currentQ = _uiState.value.queue
        if (currentQ.isEmpty()) return

        if (_uiState.value.repeatMode == RepeatMode.ONE) {
            seekTo(0)
            if (_uiState.value.currentTrack.source == TrackSource.YOUTUBE) {
                youTubeController.play()
                _uiState.value = _uiState.value.copy(isPlaying = true)
            } else {
                player.resume()
            }
            return
        }

        val nextIndex = if (_uiState.value.isShuffle) {
            (currentQ.indices).random()
        } else {
            (_uiState.value.queueIndex + 1) % currentQ.size
        }

        val nextTrack = currentQ[nextIndex]
        playTrack(nextTrack, currentQ)
    }

    fun skipPrevious() {
        if (_uiState.value.currentPositionSec > 3) {
            seekTo(0)
            return
        }
        val currentQ = _uiState.value.queue
        if (currentQ.isEmpty()) return

        val prevIndex = if (_uiState.value.queueIndex > 0) {
            _uiState.value.queueIndex - 1
        } else {
            currentQ.size - 1
        }
        val prevTrack = currentQ[prevIndex]
        playTrack(prevTrack, currentQ)
    }

    fun toggleShuffle() {
        _uiState.value = _uiState.value.copy(isShuffle = !_uiState.value.isShuffle)
        showToast(if (_uiState.value.isShuffle) "Shuffle On" else "Shuffle Off")
    }

    fun toggleRepeat() {
        val nextMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _uiState.value = _uiState.value.copy(repeatMode = nextMode)
        val text = when (nextMode) {
            RepeatMode.OFF -> "Repeat Off"
            RepeatMode.ALL -> "Repeat All"
            RepeatMode.ONE -> "Repeat One"
        }
        showToast(text)
    }

    fun setSoundPreset(preset: SoundPreset) {
        if (preset == SoundPreset.BASS_BOOST && _uiState.value.currentTrack.source == TrackSource.YOUTUBE) {
            showToast("Not available for YouTube tracks")
            return
        }
        val updated = _uiState.value.playbackSettings.copy(soundPreset = preset)
        _uiState.value = _uiState.value.copy(playbackSettings = updated)
        prefs.edit().putString("pref_preset", preset.name).apply()

        player.setBassBoost(preset == SoundPreset.BASS_BOOST)
        showToast(if (preset == SoundPreset.BASS_BOOST) "Bass Boost Activated (+8dB)" else "Sound Preset: Normal")
    }

    fun setVolume(volumePercent: Int) {
        val clamped = volumePercent.coerceIn(0, 100)
        val updated = _uiState.value.playbackSettings.copy(volumePercent = clamped)
        _uiState.value = _uiState.value.copy(playbackSettings = updated)
        prefs.edit().putInt("pref_volume", clamped).apply()

        player.setVolume(clamped / 100f)
        if (_uiState.value.currentTrack.source == TrackSource.YOUTUBE) {
            youTubeController.setVolume(clamped)
        }
    }

    fun updatePlaybackSettings(settings: PlaybackSettings) {
        _uiState.value = _uiState.value.copy(playbackSettings = settings)
        prefs.edit()
            .putInt("pref_crossfade", settings.crossfadeSec)
            .putBoolean("pref_gapless", settings.gaplessPlayback)
            .putBoolean("pref_autoplay", settings.autoplaySimilar)
            .putString("pref_quality", settings.audioQuality)
            .putBoolean("pref_datasaver", settings.dataSaver)
            .putString("pref_preset", settings.soundPreset.name)
            .apply()
        player.setBassBoost(settings.soundPreset == SoundPreset.BASS_BOOST)
    }

    fun toggleLike(trackId: String) {
        repository.toggleLike(trackId)
        val isLikedNow = !_uiState.value.likedTrackIds.contains(trackId)
        showToast(if (isLikedNow) "Added to Liked Songs" else "Removed from Liked Songs")
    }

    fun toggleLibraryView() {
        _uiState.value = _uiState.value.copy(isLibraryGridView = !_uiState.value.isLibraryGridView)
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(isNowPlayingExpanded = expanded)
    }

    fun setShowProfileSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProfileSheet = show)
    }

    fun setShowCreateSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateSheet = show)
    }

    fun setShowAuthDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAuthDialog = show)
    }

    fun setShowMusicPlayerPanel(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMusicPlayerPanel = show)
    }

    fun setShowAddYouTubeSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddYouTubeSheet = show)
    }

    fun openPlaylistDetail(playlist: Playlist) {
        _uiState.value = _uiState.value.copy(selectedPlaylistForDetail = playlist)
    }

    fun closePlaylistDetail() {
        _uiState.value = _uiState.value.copy(selectedPlaylistForDetail = null)
    }

    fun openSongMenu(track: Track) {
        _uiState.value = _uiState.value.copy(selectedTrackForMenu = track)
    }

    fun closeSongMenu() {
        _uiState.value = _uiState.value.copy(selectedTrackForMenu = null)
    }

    fun openPlaylistMenu(playlist: Playlist) {
        _uiState.value = _uiState.value.copy(selectedPlaylistForMenu = playlist)
    }

    fun closePlaylistMenu() {
        _uiState.value = _uiState.value.copy(selectedPlaylistForMenu = null)
    }

    fun addTrackToQueue(track: Track) {
        val q = _uiState.value.queue.toMutableList()
        q.add(track)
        _uiState.value = _uiState.value.copy(queue = q)
        showToast("Added to queue")
    }

    fun playNextInQueue(track: Track) {
        val q = _uiState.value.queue.toMutableList()
        val insertIndex = (_uiState.value.queueIndex + 1).coerceAtMost(q.size)
        q.add(insertIndex, track)
        _uiState.value = _uiState.value.copy(queue = q)
        showToast("Will play next")
    }

    fun clearQueue() {
        _uiState.value = _uiState.value.copy(queue = listOf(_uiState.value.currentTrack), queueIndex = 0)
        showToast("Queue cleared")
    }

    fun removeFromQueue(index: Int) {
        val q = _uiState.value.queue.toMutableList()
        if (index in q.indices) {
            val removed = q.removeAt(index)
            val newIdx = if (index < _uiState.value.queueIndex) {
                _uiState.value.queueIndex - 1
            } else if (index == _uiState.value.queueIndex) {
                0.coerceAtMost(q.size - 1)
            } else {
                _uiState.value.queueIndex
            }
            _uiState.value = _uiState.value.copy(queue = q, queueIndex = newIdx)
            showToast("Removed ${removed.title}")
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        val q = _uiState.value.queue.toMutableList()
        if (from in q.indices && to in q.indices) {
            val item = q.removeAt(from)
            q.add(to, item)
            val currentId = _uiState.value.currentTrack.id
            val newIndex = q.indexOfFirst { it.id == currentId }.coerceAtLeast(0)
            _uiState.value = _uiState.value.copy(queue = q, queueIndex = newIndex)
        }
    }

    fun playRandom() {
        val q = _uiState.value.queue
        if (q.isNotEmpty()) {
            val randomTrack = q.random()
            playTrack(randomTrack)
        }
    }

    fun addYouTubeTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        val currentQ = _uiState.value.queue.toMutableList()
        currentQ.addAll(tracks)
        _uiState.value = _uiState.value.copy(queue = currentQ)
        repository.addApiTracks(tracks)
        playTrack(tracks.first(), currentQ)
        showToast("Added ${tracks.size} YouTube song(s) to queue!")
    }

    fun loadCommunitySongs() {
        viewModelScope.launch {
            showToast("Loading Community Creative Commons Songs...")
            val songs = JamendoApiClient.fetchJamendoTracks("chill indie electronic", 20)
            if (songs.isNotEmpty()) {
                repository.addApiTracks(songs)
                val currentQ = _uiState.value.queue.toMutableList()
                currentQ.addAll(songs)
                _uiState.value = _uiState.value.copy(queue = currentQ)
                showToast("Added ${songs.size} Creative Commons songs with licenses!")
            } else {
                showToast("Community songs currently ready in catalog.")
            }
        }
    }

    fun createPlaylist(name: String, description: String = "") {
        val newPl = repository.createPlaylist(name, description)
        showToast("Created playlist: ${newPl.title}")
    }

    fun deletePlaylist(playlistId: String) {
        repository.deletePlaylist(playlistId)
        if (_uiState.value.selectedPlaylistForDetail?.id == playlistId) {
            _uiState.value = _uiState.value.copy(selectedPlaylistForDetail = null)
        }
        showToast("Playlist deleted")
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        repository.addTrackToPlaylist(playlistId, trackId)
        showToast("Added to playlist")
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
        showToast("Removed from playlist")
    }

    fun importMultipleDeviceSongs(uris: List<android.net.Uri>) {
        if (uris.isEmpty()) return
        val context = getApplication<Application>()
        val importedTracks = mutableListOf<Track>()

        for (uri in uris) {
            try {
                var title = "Local Song"
                var artist = "Device Audio"
                var duration = 180
                val isVideo = uri.toString().contains(".mp4", ignoreCase = true) ||
                        context.contentResolver.getType(uri)?.startsWith("video") == true

                try {
                    val mmr = android.media.MediaMetadataRetriever()
                    mmr.setDataSource(context, uri)
                    title = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE)
                        ?: uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.')
                        ?: "Local Song"
                    artist = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Device Audio"
                    val durStr = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                    if (durStr != null) {
                        duration = (durStr.toIntOrNull() ?: 180000) / 1000
                    }
                    mmr.release()
                } catch (_: Exception) {
                    title = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "Imported Song"
                }

                importedTracks.add(
                    Track(
                        id = "imported_${java.util.UUID.randomUUID()}",
                        title = title,
                        artist = artist,
                        album = "Device Songs",
                        durationSec = duration,
                        coverUrl = "https://picsum.photos/seed/${title.hashCode()}/600/600",
                        audioUrl = uri.toString(),
                        videoUrl = if (isVideo) uri.toString() else null,
                        isVideo = isVideo,
                        genre = if (isVideo) "Podcasts" else "Local",
                        isUserUpload = true,
                        dominantColor = 0xFF00E676,
                        source = TrackSource.FILE,
                        licenseInfo = "Device Local Media"
                    )
                )
            } catch (_: Exception) {}
        }

        val count = repository.autoImportDeviceTracks(importedTracks)
        val q = _uiState.value.queue.toMutableList()
        q.addAll(importedTracks)
        _uiState.value = _uiState.value.copy(queue = q)
        showToast("Automatically added $count songs to Larpify!")
    }

    fun addAllSongsToPlaylist(playlistId: String) {
        val allIds = getAllTracks().map { it.id }
        repository.addAllTracksToPlaylist(playlistId, allIds)
        showToast("Added all ${allIds.size} songs to playlist!")
    }

    fun uploadTrack(title: String, artist: String, genre: String, isPublic: Boolean, isVideo: Boolean, uri: String, coverUri: String) {
        if (_uiState.value.userSession.isGuest) {
            showToast("Sign up free to upload on Larpify")
            return
        }
        val upload = repository.addUpload(title, artist, genre, isPublic, isVideo, uri, coverUri)
        showToast("Added to Larpify")
        val q = _uiState.value.queue.toMutableList()
        q.add(0, upload)
        _uiState.value = _uiState.value.copy(queue = q)
        playTrack(upload, q)
    }

    fun deleteUpload(trackId: String) {
        val updatedUploads = _uiState.value.userUploads.filter { it.id != trackId }
        val updatedQ = _uiState.value.queue.filter { it.id != trackId }
        _uiState.value = _uiState.value.copy(userUploads = updatedUploads, queue = updatedQ)
        showToast("Upload deleted")
    }

    fun login(email: String, username: String) {
        repository.loginUser(email, username)
        showToast("Welcome back, ${_uiState.value.userSession.username}!")
    }

    fun logout() {
        repository.logout()
        showToast("Logged out")
    }

    fun attachVideoSurface(holder: SurfaceHolder?) {
        player.attachSurfaceHolder(holder)
    }

    fun onYouTubeStateChanged(state: Int) {
        when (state) {
            1 -> _uiState.value = _uiState.value.copy(isPlaying = true)
            2 -> _uiState.value = _uiState.value.copy(isPlaying = false)
            0 -> skipNext() // Auto advance when YouTube track ends
        }
    }

    fun onYouTubeError(errorCode: Int) {
        when (errorCode) {
            2, 5, 100, 101, 150 -> {
                showToast("That video can't be played here")
                skipNext()
            }
        }
    }

    fun onYouTubeProgress(currentSec: Int, durSec: Int) {
        _uiState.value = _uiState.value.copy(
            currentPositionSec = currentSec,
            durationSec = if (durSec > 0) durSec else _uiState.value.durationSec
        )
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun syncJamendoTracks(tag: String = "lofi") {
        viewModelScope.launch {
            showToast("Fetching Jamendo streams for ExoPlayer...")
            val tracks = JamendoApiClient.fetchJamendoTracks(tag, 15)
            if (tracks.isNotEmpty()) {
                repository.addApiTracks(tracks)
                val q = _uiState.value.queue.toMutableList()
                q.addAll(tracks)
                _uiState.value = _uiState.value.copy(queue = q)
                showToast("Added ${tracks.size} Jamendo streams!")
            } else {
                showToast("Could not fetch Jamendo tracks.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
