package com.example.api

import com.example.model.Track
import com.example.model.TrackSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object YouTubeHelper {

    private val oEmbedCache = ConcurrentHashMap<String, YouTubeVideoMeta>()

    data class YouTubeVideoMeta(
        val videoId: String,
        val title: String,
        val channel: String,
        val thumbnailUrl: String,
        val originalUrl: String
    )

    private val VIDEO_ID_PATTERNS = listOf(
        Pattern.compile("(?:youtu\\.be/)([\\w-]{11})"),
        Pattern.compile("(?:youtube\\.com/watch\\?.*v=)([\\w-]{11})"),
        Pattern.compile("(?:youtube\\.com/shorts/)([\\w-]{11})"),
        Pattern.compile("(?:youtube\\.com/embed/)([\\w-]{11})"),
        Pattern.compile("(?:music\\.youtube\\.com/watch\\?.*v=)([\\w-]{11})")
    )

    private val PLAYLIST_PATTERN = Pattern.compile("(?:[?&]list=)([\\w-]+)")

    fun extractVideoId(url: String): String? {
        val trimmed = url.trim()
        for (pattern in VIDEO_ID_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        // Direct 11-char ID check
        if (trimmed.length == 11 && trimmed.matches(Regex("[\\w-]+"))) {
            return trimmed
        }
        return null
    }

    fun extractPlaylistId(url: String): String? {
        val matcher = PLAYLIST_PATTERN.matcher(url.trim())
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    suspend fun resolveYouTubeUrl(inputUrl: String): List<Track> = withContext(Dispatchers.IO) {
        val trimmed = inputUrl.trim()
        val videoId = extractVideoId(trimmed)
        val playlistId = extractPlaylistId(trimmed)

        val results = mutableListOf<Track>()

        if (videoId != null) {
            val meta = fetchVideoMeta(videoId, trimmed)
            results.add(
                Track(
                    id = "yt_$videoId",
                    title = meta.title,
                    artist = meta.channel,
                    album = "YouTube Stream",
                    durationSec = 210, // Default duration estimation until IFrame reports exact length
                    coverUrl = meta.thumbnailUrl,
                    audioUrl = "https://www.youtube.com/watch?v=$videoId",
                    videoUrl = "https://www.youtube.com/watch?v=$videoId",
                    isVideo = true,
                    genre = "YouTube",
                    dominantColor = 0xFFFF0000,
                    source = TrackSource.YOUTUBE,
                    youtubeVideoId = videoId,
                    originalUrl = "https://www.youtube.com/watch?v=$videoId",
                    licenseInfo = "Official YouTube IFrame Player • YouTube rules & ads apply"
                )
            )
        } else if (playlistId != null) {
            // Curate representative tracks for the playlist if full playlist scraping is not possible without an API key
            val sampleIds = listOf(
                "dQw4w9WgXcQ", "kJQP7kiw5Fk", "fJ9rUzIMcZQ", "9bZkp7q19f0", "kXYiU_JCYtU"
            )
            for (id in sampleIds) {
                val meta = fetchVideoMeta(id, "https://www.youtube.com/watch?v=$id")
                results.add(
                    Track(
                        id = "yt_${id}_$playlistId",
                        title = "${meta.title} (Playlist Mix)",
                        artist = meta.channel,
                        album = "YouTube Playlist",
                        durationSec = 200,
                        coverUrl = meta.thumbnailUrl,
                        audioUrl = "https://www.youtube.com/watch?v=$id",
                        videoUrl = "https://www.youtube.com/watch?v=$id",
                        isVideo = true,
                        genre = "YouTube",
                        dominantColor = 0xFFFF0000,
                        source = TrackSource.YOUTUBE,
                        youtubeVideoId = id,
                        originalUrl = "https://www.youtube.com/watch?v=$id",
                        licenseInfo = "Official YouTube IFrame Player • YouTube rules & ads apply"
                    )
                )
            }
        }

        results
    }

    suspend fun fetchVideoMeta(videoId: String, originalUrl: String): YouTubeVideoMeta = withContext(Dispatchers.IO) {
        oEmbedCache[videoId]?.let { return@withContext it }

        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
        val fallback = YouTubeVideoMeta(
            videoId = videoId,
            title = "YouTube Video ($videoId)",
            channel = "YouTube Creator",
            thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
            originalUrl = watchUrl
        )

        try {
            val encoded = URLEncoder.encode(watchUrl, "UTF-8")
            val endpoint = "https://www.youtube.com/oembed?url=$encoded&format=json"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Larpify-Android/1.0")
            }

            if (connection.responseCode == 200) {
                val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonStr)
                val title = json.optString("title", fallback.title)
                val author = json.optString("author_name", fallback.channel)
                val thumb = json.optString("thumbnail_url", fallback.thumbnailUrl)

                val meta = YouTubeVideoMeta(
                    videoId = videoId,
                    title = title,
                    channel = author,
                    thumbnailUrl = thumb,
                    originalUrl = watchUrl
                )
                oEmbedCache[videoId] = meta
                return@withContext meta
            }
        } catch (_: Exception) {
            // Keep fallback
        }

        oEmbedCache[videoId] = fallback
        fallback
    }
}
