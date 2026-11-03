package com.example

import com.example.api.YouTubeHelper
import com.example.model.NavigationTab
import com.example.model.Track
import com.example.model.TrackSource
import com.example.theme.ThemeManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testTrackModel() {
        val track = Track(
            id = "test_1",
            title = "Test Song",
            artist = "Larpify Artist",
            album = "Test Album",
            durationSec = 200,
            coverUrl = "https://example.com/cover.jpg",
            audioUrl = "https://example.com/audio.mp3"
        )
        assertEquals("Test Song", track.title)
        assertEquals("Larpify Artist", track.artist)
        assertEquals(200, track.durationSec)
        assertEquals(TrackSource.CATALOG, track.source)
    }

    @Test
    fun testThemesAvailable() {
        assertEquals(10, ThemeManager.AllThemes.size)
        val midnight = ThemeManager.AllThemes.find { it.id == "midnight_neon" }
        assertNotNull(midnight)
        assertEquals("Midnight Neon", midnight?.name)
    }

    @Test
    fun testNavigationTabs() {
        assertEquals(5, NavigationTab.values().size)
        assertTrue(NavigationTab.values().contains(NavigationTab.SETTINGS))
        assertTrue(NavigationTab.values().contains(NavigationTab.HOME))
        assertTrue(NavigationTab.values().contains(NavigationTab.SEARCH))
        assertTrue(NavigationTab.values().contains(NavigationTab.LIBRARY))
        assertTrue(NavigationTab.values().contains(NavigationTab.THEMES))
    }

    @Test
    fun testYouTubeHelperUrlParsing() {
        val v1 = YouTubeHelper.extractVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertEquals("dQw4w9WgXcQ", v1)

        val v2 = YouTubeHelper.extractVideoId("https://youtu.be/kJQP7kiw5Fk")
        assertEquals("kJQP7kiw5Fk", v2)

        val v3 = YouTubeHelper.extractVideoId("https://www.youtube.com/shorts/fJ9rUzIMcZQ")
        assertEquals("fJ9rUzIMcZQ", v3)

        val playlistId = YouTubeHelper.extractPlaylistId("https://www.youtube.com/playlist?list=PLrAXtmErZgOdP_8GztsuKi9nvOGgyTXr_")
        assertEquals("PLrAXtmErZgOdP_8GztsuKi9nvOGgyTXr_", playlistId)
    }
}
