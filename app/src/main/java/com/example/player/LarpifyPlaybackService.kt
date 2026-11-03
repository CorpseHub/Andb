package com.example.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.MainActivity

@UnstableApi
class LarpifyPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    companion object {
        const val CHANNEL_ID = "larpify_playback_channel"
        const val NOTIFICATION_ID = 1001
        var activeSession: MediaSession? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        activeSession?.let {
            mediaSession = it
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession ?: activeSession
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Larpify Music Playback"
            val descriptionText = "Controls and information for active audio playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        activeSession = null
        super.onDestroy()
    }
}
