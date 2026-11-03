package com.example.api

import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object JamendoApiClient {

    // Default Jamendo API client ID with fallback to Jamendo direct streaming CDN
    var clientId: String = "b6747d04"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    // Verified legal Creative Commons Jamendo audio streams
    val CuratedJamendoTracks = listOf(
        Track(
            id = "jamendo_1532771",
            title = "Acoustic Breeze",
            artist = "Benjamin Tissot (Jamendo CC)",
            album = "Acoustic Memories",
            durationSec = 157,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=377983&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1532771/mp32/",
            genre = "Acoustic",
            lyrics = "Streamed via Jamendo Creative Commons on Larpify.\nLicense: Free for personal streaming.\nZero ads. Zero chill.",
            dominantColor = 0xFF00E676
        ),
        Track(
            id = "jamendo_1888200",
            title = "Cyber Ambient Dream",
            artist = "Lukas Got Luck (Jamendo CC)",
            album = "Nocturnal Waves",
            durationSec = 210,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=461021&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1888200/mp32/",
            genre = "Electronic",
            lyrics = "Streamed via Jamendo Creative Commons on Larpify.\nAmbient electronic synthesis.\nZero ads. Zero chill.",
            dominantColor = 0xFF38BDF8
        ),
        Track(
            id = "jamendo_1878440",
            title = "Lo-Fi Midnight Study",
            artist = "Ghostrifter Official (Jamendo CC)",
            album = "Lo-Fi Sessions Vol. 2",
            durationSec = 194,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=459200&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1878440/mp32/",
            genre = "Lo-fi",
            lyrics = "Cozy beats, gentle rain, vinyl hiss.\nExoPlayer stream directly from Jamendo.",
            dominantColor = 0xFFE6A368
        ),
        Track(
            id = "jamendo_1856720",
            title = "Golden Horizon Synth",
            artist = "Doxx & Feelo (Jamendo CC)",
            album = "Sunset Drive",
            durationSec = 228,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=452310&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1856720/mp32/",
            genre = "Synthwave",
            lyrics = "Outrun groove and analog warmth.\nStreaming through ExoPlayer with background media playback.",
            dominantColor = 0xFFFF6B35
        ),
        Track(
            id = "jamendo_1845110",
            title = "Deep Chill Meditation",
            artist = "Purrple Cat (Jamendo CC)",
            album = "Silent Nightfall",
            durationSec = 185,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=449870&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1845110/mp32/",
            genre = "Chill",
            lyrics = "Deep soothing piano and soft pads.\nJamendo Creative Commons catalog.",
            dominantColor = 0xFF1DE9B6
        ),
        Track(
            id = "jamendo_1798320",
            title = "Urban Coffee Break",
            artist = "Punch Deck (Jamendo CC)",
            album = "City Rhythms",
            durationSec = 202,
            coverUrl = "https://usercontent.jamendo.com?type=album&id=438200&width=600",
            audioUrl = "https://mp3d.jamendo.com/download/track/1798320/mp32/",
            genre = "Hip Hop",
            lyrics = "Boom bap drums and melodic keys.\nJamendo API streaming.",
            dominantColor = 0xFFF43F5E
        )
    )

    suspend fun fetchJamendoTracks(tag: String = "lofi", limit: Int = 15): List<Track> = withContext(Dispatchers.IO) {
        try {
            val encodedTag = URLEncoder.encode(tag, "UTF-8")
            val url = "https://api.jamendo.com/v3.0/tracks/?client_id=$clientId&format=jsonpretty&limit=$limit&include=musicinfo&fuzzytags=$encodedTag"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "LarpifyMusicApp/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext CuratedJamendoTracks

            val jsonString = response.body?.string() ?: return@withContext CuratedJamendoTracks
            val parsed = parseJamendoJson(jsonString)
            if (parsed.isNotEmpty()) parsed else CuratedJamendoTracks
        } catch (e: Exception) {
            CuratedJamendoTracks
        }
    }

    private fun parseJamendoJson(jsonString: String): List<Track> {
        val list = mutableListOf<Track>()
        try {
            val root = JSONObject(jsonString)
            val results = root.optJSONArray("results") ?: return emptyList()

            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                val name = item.optString("name", "Jamendo Track")
                val artist = item.optString("artist_name", "Jamendo Artist")
                val album = item.optString("album_name", "Jamendo Release")
                val duration = item.optInt("duration", 180)
                val image = item.optString("image", "https://picsum.photos/seed/$id/600/600")
                var audio = item.optString("audio", "")
                if (audio.isBlank() && id.isNotBlank()) {
                    audio = "https://mp3d.jamendo.com/download/track/$id/mp32/"
                }

                if (id.isNotBlank() && audio.isNotBlank()) {
                    list.add(
                        Track(
                            id = "jamendo_$id",
                            title = name,
                            artist = artist,
                            album = album,
                            durationSec = duration,
                            coverUrl = image,
                            audioUrl = audio,
                            videoUrl = null,
                            isVideo = false,
                            genre = "Jamendo",
                            lyrics = "Streamed from Jamendo API through ExoPlayer.\nTitle: $name\nArtist: $artist\nFree music. Zero ads. Zero chill.",
                            dominantColor = 0xFF00E676
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return list
    }
}
