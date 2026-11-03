package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Playlist
import com.example.model.Track
import com.example.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class LarpifyRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("larpify_prefs", Context.MODE_PRIVATE)

    private val _playlists = MutableStateFlow<List<Playlist>>(MusicCatalog.DefaultPlaylists)
    val playlists = _playlists.asStateFlow()

    private val _likedTrackIds = MutableStateFlow<Set<String>>(setOf("track_1", "track_2", "track_4"))
    val likedTrackIds = _likedTrackIds.asStateFlow()

    private val _userUploads = MutableStateFlow<List<Track>>(emptyList())
    val userUploads = _userUploads.asStateFlow()

    private val _apiTracks = MutableStateFlow<List<Track>>(emptyList())
    val apiTracks = _apiTracks.asStateFlow()

    fun addApiTracks(tracks: List<Track>) {
        val existingIds = _apiTracks.value.map { it.id }.toSet()
        val fresh = tracks.filter { !existingIds.contains(it.id) }
        if (fresh.isNotEmpty()) {
            _apiTracks.value = _apiTracks.value + fresh
        }
    }

    private val _currentThemeId = MutableStateFlow(prefs.getString("active_theme_id", "midnight_neon") ?: "midnight_neon")
    val currentThemeId = _currentThemeId.asStateFlow()

    private val _session = MutableStateFlow(
        UserSession(
            isGuest = prefs.getBoolean("is_guest", true),
            username = prefs.getString("username", "Guest Listener") ?: "Guest Listener",
            email = prefs.getString("email", "guest@larpify.app") ?: "guest@larpify.app",
            guestStartTimeMs = getOrInitGuestStartTime(),
            adsServedCount = 0
        )
    )
    val session = _session.asStateFlow()

    private fun getOrInitGuestStartTime(): Long {
        val stored = prefs.getLong("guest_start_time", 0L)
        return if (stored == 0L) {
            val now = System.currentTimeMillis()
            prefs.edit().putLong("guest_start_time", now).apply()
            now
        } else {
            stored
        }
    }

    fun getGuestTimeRemainingString(): String {
        val threeDaysMs = 3L * 24 * 60 * 60 * 1000
        val elapsed = System.currentTimeMillis() - _session.value.guestStartTimeMs
        val remaining = threeDaysMs - elapsed
        if (remaining <= 0) return "Guest pass expired"

        val hoursTotal = remaining / (1000 * 60 * 60)
        val days = hoursTotal / 24
        val hours = hoursTotal % 24
        return "${days}d ${hours}h left"
    }

    fun setThemeId(themeId: String) {
        _currentThemeId.value = themeId
        prefs.edit().putString("active_theme_id", themeId).apply()
    }

    fun toggleLike(trackId: String) {
        val current = _likedTrackIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _likedTrackIds.value = current

        // Update Liked Songs system playlist
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == "playlist_liked") {
                pl.copy(trackIds = current.toList())
            } else {
                pl
            }
        }
    }

    fun createPlaylist(name: String, description: String = "", coverUrl: String = ""): Playlist {
        val newPlaylist = Playlist(
            id = "playlist_${UUID.randomUUID()}",
            title = name.ifBlank { "My Larpify Playlist" },
            description = description.ifBlank { "Curated by ${_session.value.username}" },
            coverUrl = coverUrl.ifBlank { "https://picsum.photos/seed/${UUID.randomUUID()}/600/600" },
            trackIds = emptyList(),
            ownerName = _session.value.username,
            isSystem = false
        )
        _playlists.value = listOf(newPlaylist) + _playlists.value
        return newPlaylist
    }

    fun deletePlaylist(playlistId: String) {
        _playlists.value = _playlists.value.filter { it.id != playlistId || it.isSystem }
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId && !pl.trackIds.contains(trackId)) {
                pl.copy(trackIds = pl.trackIds + trackId)
            } else {
                pl
            }
        }
    }

    fun addAllTracksToPlaylist(playlistId: String, trackIds: List<String>) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                val combined = (pl.trackIds + trackIds).distinct()
                pl.copy(trackIds = combined)
            } else {
                pl
            }
        }
    }

    fun autoImportDeviceTracks(newTracks: List<Track>): Int {
        if (newTracks.isEmpty()) return 0
        _userUploads.value = newTracks + _userUploads.value
        return newTracks.size
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                pl.copy(trackIds = pl.trackIds - trackId)
            } else {
                pl
            }
        }
    }

    fun addUpload(
        title: String,
        artist: String,
        genre: String,
        isPublic: Boolean,
        isVideo: Boolean,
        mediaUri: String,
        coverUri: String
    ): Track {
        val newTrack = Track(
            id = "upload_${UUID.randomUUID()}",
            title = title,
            artist = artist.ifBlank { _session.value.username },
            album = "My Uploads",
            durationSec = if (isVideo) 120 else 180,
            coverUrl = coverUri.ifBlank { "https://picsum.photos/seed/${UUID.randomUUID()}/600/600" },
            audioUrl = mediaUri,
            videoUrl = if (isVideo) mediaUri else null,
            isVideo = isVideo,
            genre = genre.ifBlank { if (isVideo) "Podcasts" else "Lo-fi" },
            isUserUpload = true,
            dominantColor = 0xFF00E676
        )
        _userUploads.value = listOf(newTrack) + _userUploads.value
        return newTrack
    }

    fun loginUser(email: String, username: String) {
        _session.value = UserSession(
            isGuest = false,
            username = username.ifBlank { email.substringBefore("@") },
            email = email,
            adsServedCount = 0
        )
        prefs.edit()
            .putBoolean("is_guest", false)
            .putString("username", _session.value.username)
            .putString("email", email)
            .apply()
    }

    fun logout() {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putBoolean("is_guest", true)
            .putString("username", "Guest Listener")
            .putString("email", "guest@larpify.app")
            .putLong("guest_start_time", now)
            .apply()
        _session.value = UserSession(
            isGuest = true,
            username = "Guest Listener",
            email = "guest@larpify.app",
            guestStartTimeMs = now,
            adsServedCount = 0
        )
    }
}
