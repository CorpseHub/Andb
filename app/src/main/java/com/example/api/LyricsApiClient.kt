package com.example.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

data class LyricLine(
    val timeSec: Int,
    val text: String
)

data class LyricsResult(
    val plainLyrics: String,
    val syncedLines: List<LyricLine> = emptyList(),
    val source: String = "Lrclib Free Public API"
)

object LyricsApiClient {

    private val cache = ConcurrentHashMap<String, LyricsResult>()
    private val TIME_TAG_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)")

    suspend fun fetchLyrics(artist: String, title: String, fallbackText: String): LyricsResult = withContext(Dispatchers.IO) {
        val cacheKey = "${artist.lowercase()}_${title.lowercase()}"
        cache[cacheKey]?.let { return@withContext it }

        try {
            val encodedArtist = URLEncoder.encode(artist.trim(), "UTF-8")
            val encodedTitle = URLEncoder.encode(title.trim(), "UTF-8")
            val endpoint = "https://lrclib.net/api/get?artist_name=$encodedArtist&track_name=$encodedTitle"

            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "DriftMoods-App/1.0 (Android; Free Open Audio)")
            }

            if (connection.responseCode == 200) {
                val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                val obj = JSONObject(jsonStr)
                val syncedLyricsStr = obj.optString("syncedLyrics", "")
                val plainLyricsStr = obj.optString("plainLyrics", "")

                val lines = if (syncedLyricsStr.isNotBlank()) {
                    parseSyncedLyrics(syncedLyricsStr)
                } else emptyList()

                val result = LyricsResult(
                    plainLyrics = if (plainLyricsStr.isNotBlank()) plainLyricsStr else syncedLyricsStr,
                    syncedLines = lines,
                    source = "LRCLIB Public Open Lyrics"
                )
                cache[cacheKey] = result
                return@withContext result
            } else if (connection.responseCode == 404) {
                // Try search fallback
                val searchUrl = "https://lrclib.net/api/search?q=" + URLEncoder.encode("$artist $title", "UTF-8")
                val searchConn = (URL(searchUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 4000
                    readTimeout = 4000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "DriftMoods-App/1.0 (Android; Free Open Audio)")
                }
                if (searchConn.responseCode == 200) {
                    val searchJson = searchConn.inputStream.bufferedReader().use { it.readText() }
                    val array = JSONArray(searchJson)
                    if (array.length() > 0) {
                        val first = array.getJSONObject(0)
                        val synced = first.optString("syncedLyrics", "")
                        val plain = first.optString("plainLyrics", "")
                        val lines = if (synced.isNotBlank()) parseSyncedLyrics(synced) else emptyList()
                        val res = LyricsResult(
                            plainLyrics = if (plain.isNotBlank()) plain else synced,
                            syncedLines = lines,
                            source = "LRCLIB Open Search"
                        )
                        cache[cacheKey] = res
                        return@withContext res
                    }
                }
            }
        } catch (_: Exception) {
            // Network fallback
        }

        // Generate synthetic synced preview for local/catalog songs
        val syntheticLines = generateSyntheticSyncedLines(fallbackText)
        val fallbackResult = LyricsResult(
            plainLyrics = fallbackText,
            syncedLines = syntheticLines,
            source = "DriftMoods Curated Lyrics"
        )
        cache[cacheKey] = fallbackResult
        fallbackResult
    }

    private fun parseSyncedLyrics(lrc: String): List<LyricLine> {
        val list = mutableListOf<LyricLine>()
        lrc.lines().forEach { line ->
            val matcher = TIME_TAG_PATTERN.matcher(line.trim())
            if (matcher.matches()) {
                val min = matcher.group(1)?.toIntOrNull() ?: 0
                val sec = matcher.group(2)?.toIntOrNull() ?: 0
                val text = matcher.group(4)?.trim() ?: ""
                val totalSec = min * 60 + sec
                if (text.isNotBlank()) {
                    list.add(LyricLine(timeSec = totalSec, text = text))
                }
            }
        }
        return list.sortedBy { it.timeSec }
    }

    private fun generateSyntheticSyncedLines(text: String): List<LyricLine> {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return listOf(
                LyricLine(0, "DriftMoods • Immersion Flow"),
                LyricLine(15, "Free music. Zero ads. Drift on."),
                LyricLine(35, "Feel the sound waves drift around you.")
            )
        }
        val step = 12
        return lines.mapIndexed { idx, str ->
            LyricLine(timeSec = idx * step, text = str)
        }
    }
}
