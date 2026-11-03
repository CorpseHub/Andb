package com.example

import com.example.data.MusicCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testMusicCatalogHasTracks() {
        assertTrue(MusicCatalog.SampleTracks.isNotEmpty())
        assertTrue(MusicCatalog.DefaultPlaylists.isNotEmpty())
        assertTrue(MusicCatalog.SearchCategories.isNotEmpty())
    }
}
