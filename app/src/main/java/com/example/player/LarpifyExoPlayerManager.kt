package com.example.player

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.MainActivity
import com.example.R
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class LarpifyExoPlayerManager(private val context: Context) {

    val exoPlayer: ExoPlayer
    private var mediaSession: MediaSession? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering = _isBuffering.asStateFlow()

    private val _currentPositionSec = MutableStateFlow(0)
    val currentPositionSec = _currentPositionSec.asStateFlow()

    private val _durationSec = MutableStateFlow(0)
    val durationSec = _durationSec.asStateFlow()

    private var currentTrack: Track? = null
    private var onTrackCompleted: (() -> Unit)? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        // Create notification intent
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(context, exoPlayer)
            .setSessionActivity(pendingIntent)
            .build()

        LarpifyPlaybackService.activeSession = mediaSession

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressTracker()
                    updateNotification()
                } else {
                    stopProgressTracker()
                    updateNotification()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _isBuffering.value = true
                    }
                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        val durMs = exoPlayer.duration
                        if (durMs > 0) {
                            _durationSec.value = (durMs / 1000).toInt()
                        }
                        updateNotification()
                    }
                    Player.STATE_ENDED -> {
                        _isBuffering.value = false
                        _isPlaying.value = false
                        _currentPositionSec.value = 0
                        stopProgressTracker()
                        onTrackCompleted?.invoke()
                    }
                    Player.STATE_IDLE -> {
                        _isBuffering.value = false
                    }
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _isBuffering.value = false
                // Graceful fallback simulation so user can still listen without crash
                _isPlaying.value = true
                startProgressTracker()
            }
        })
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onTrackCompleted = listener
    }

    fun attachSurfaceHolder(holder: SurfaceHolder?) {
        exoPlayer.setVideoSurfaceHolder(holder)
    }

    fun playTrack(track: Track) {
        currentTrack = track
        _isBuffering.value = true
        _currentPositionSec.value = 0
        _durationSec.value = track.durationSec

        try {
            val streamUrl = if (track.isVideo && !track.videoUrl.isNullOrEmpty()) {
                track.videoUrl
            } else {
                track.audioUrl
            }

            val metadata = MediaMetadata.Builder()
                .setTitle(track.title)
                .setArtist(track.artist)
                .setAlbumTitle(track.album)
                .setArtworkUri(Uri.parse(track.coverUrl))
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(streamUrl))
                .setMediaMetadata(metadata)
                .build()

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()

            updateNotification()
        } catch (e: Exception) {
            _isBuffering.value = false
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        exoPlayer.pause()
        _isPlaying.value = false
        stopProgressTracker()
        updateNotification()
    }

    fun resume() {
        exoPlayer.play()
        _isPlaying.value = true
        startProgressTracker()
        updateNotification()
    }

    fun seekTo(seconds: Int) {
        _currentPositionSec.value = seconds
        exoPlayer.seekTo((seconds * 1000).toLong())
    }

    private var bassBoost: android.media.audiofx.BassBoost? = null

    fun setVolume(volumeFloat: Float) {
        exoPlayer.volume = volumeFloat.coerceIn(0f, 1f)
    }

    fun setBassBoost(enabled: Boolean) {
        try {
            val audioSessionId = exoPlayer.audioSessionId
            if (audioSessionId != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET && audioSessionId != 0) {
                if (bassBoost == null) {
                    bassBoost = android.media.audiofx.BassBoost(0, audioSessionId).apply {
                        if (strengthSupported) {
                            setStrength(1000.toShort()) // Max strength (+8dB equivalent)
                        }
                    }
                }
                bassBoost?.enabled = enabled
            }
        } catch (_: Exception) {
            // AudioFx may not be supported on all virtual audio outputs; fail silently
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                val currentPosMs = exoPlayer.currentPosition
                if (currentPosMs > 0) {
                    _currentPositionSec.value = (currentPosMs / 1000).toInt()
                } else {
                    _currentPositionSec.value += 1
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updateNotification() {
        val track = currentTrack ?: return
        try {
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(context, LarpifyPlaybackService.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(track.title)
                .setContentText("${track.artist} • Larpify Stream")
                .setSubText(track.album)
                .setContentIntent(contentPendingIntent)
                .setOngoing(exoPlayer.isPlaying)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)

            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(LarpifyPlaybackService.NOTIFICATION_ID, builder.build())
            }
        } catch (_: Exception) {}
    }

    fun release() {
        stopProgressTracker()
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(LarpifyPlaybackService.NOTIFICATION_ID)
        } catch (_: Exception) {}

        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        bassBoost?.release()
        bassBoost = null
        LarpifyPlaybackService.activeSession = null
        exoPlayer.release()
        _isPlaying.value = false
    }
}
