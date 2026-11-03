package com.example.api

import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object MusicApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun searchTracks(query: String, limit: Int = 25): List<Track> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val url = "https://itunes.apple.com/search?term=$encoded&entity=song&limit=$limit"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "LarpifyMusicApp/1.0")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val jsonBody = response.body?.string() ?: return@withContext emptyList()
            parseTracksFromJson(jsonBody)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchTrending(genreTerm: String = "top music", limit: Int = 25): List<Track> = withContext(Dispatchers.IO) {
        searchTracks(genreTerm, limit)
    }

    suspend fun fetchByGenre(genre: String, limit: Int = 15): List<Track> = withContext(Dispatchers.IO) {
        searchTracks(genre, limit)
    }

    private fun parseTracksFromJson(jsonString: String): List<Track> {
        val tracks = mutableListOf<Track>()
        try {
            val json = JSONObject(jsonString)
            val results = json.optJSONArray("results") ?: return emptyList()

            val vibrantColors = listOf(
                0xFF00E676,
                0xFF1DB954,
                0xFF00F0FF,
                0xFFFF2A85,
                0xFFF43F5E,
                0xFFFF6B35,
                0xFFD4AF37,
                0xFF1DE9B6,
                0xFF9C27B0,
                0xFF38BDF8
            )

            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                val trackId = item.optLong("trackId", 0L)
                val trackName = item.optString("trackName", "")
                val artistName = item.optString("artistName", "Unknown Artist")
                val previewUrl = item.optString("previewUrl", "")
                val artworkUrl = item.optString("artworkUrl100", "")
                val collectionName = item.optString("collectionName", "Larpify Stream")
                val primaryGenre = item.optString("primaryGenreName", "Music")
                val durationMs = item.optLong("trackTimeMillis", 180000L)

                if (trackName.isNotBlank() && previewUrl.isNotBlank()) {
                    // High-res cover art by replacing 100x100 with 600x600
                    val highResCover = if (artworkUrl.isNotBlank()) {
                        artworkUrl.replace("100x100bb.jpg", "600x600bb.jpg")
                    } else {
                        "https://picsum.photos/seed/$trackId/600/600"
                    }

                    val colorIdx = (trackId.toInt().coerceAtLeast(0) % vibrantColors.size)
                    val dominantColor = vibrantColors[colorIdx]

                    tracks.add(
                        Track(
                            id = "api_$trackId",
                            title = trackName,
                            artist = artistName,
                            album = collectionName,
                            durationSec = (durationMs / 1000).toInt().coerceAtLeast(30),
                            coverUrl = highResCover,
                            audioUrl = previewUrl,
                            videoUrl = null,
                            isVideo = false,
                            genre = primaryGenre,
                            lyrics = "Now streaming on Larpify\n$trackName by $artistName\nAlbum: $collectionName\nGenre: $primaryGenre\nFree music. Zero ads. Zero chill.",
                            isUserUpload = false,
                            dominantColor = dominantColor
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return tracks
    }
}
