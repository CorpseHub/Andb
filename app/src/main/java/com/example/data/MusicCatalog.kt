package com.example.data

import com.example.model.Playlist
import com.example.model.Track

object MusicCatalog {

    val SampleTracks = listOf(
        Track(
            id = "track_1",
            title = "Midnight Horizon",
            artist = "Larpify Collective",
            album = "Neon Drift",
            durationSec = 214,
            coverUrl = "https://picsum.photos/seed/larpify1/600/600",
            audioUrl = "https://archive.org/download/free-lofi-beats-to-relax-to/01%20Coffee%20Shop%20Vibes.mp3",
            genre = "Lo-fi",
            lyrics = "Soft neon rain falling on empty streets\nDrifting with the rhythm, gentle ambient beats\nNo commercials, no noise, just you and the sound\nIn Larpify's nocturnal playground.",
            dominantColor = 0xFF00E676
        ),
        Track(
            id = "track_2",
            title = "Cybernetic Pulse",
            artist = "Synthia Void",
            album = "Sector 07",
            durationSec = 188,
            coverUrl = "https://picsum.photos/seed/larpify2/600/600",
            audioUrl = "https://archive.org/download/electronic-synthwave-royalty-free/retro_pulse.mp3",
            genre = "Electronic",
            lyrics = "Synthesizers humming in the midnight air\nElectric frequencies beyond compare\nZero chill, pure adrenaline speed\nLarpify is everything you need.",
            dominantColor = 0xFFF43F5E
        ),
        Track(
            id = "track_3",
            title = "Cloud Rap Anthems",
            artist = "Aether Flow",
            album = "Skyline Dreams",
            durationSec = 175,
            coverUrl = "https://picsum.photos/seed/larpify3/600/600",
            audioUrl = "https://archive.org/download/hip-hop-instrumentals-public-domain/track3_chillhop.mp3",
            genre = "Hip Hop",
            lyrics = "Bass kicks echo through the open sky\nSmooth 808s when the mood is high\nNo interrupts, no sponsor ad queue\nStraight vibes crafted just for you.",
            dominantColor = 0xFF38BDF8
        ),
        Track(
            id = "track_4",
            title = "Coffee & Cassettes",
            artist = "Mellow Sunday",
            album = "Vintage Warmth",
            durationSec = 205,
            coverUrl = "https://picsum.photos/seed/larpify4/600/600",
            audioUrl = "https://archive.org/download/lofi-study-beats-archive/study_session_01.mp3",
            genre = "Lo-fi",
            lyrics = "Warm vinyl crackle in the morning tea\nLost in thoughts, completely free\nPages turning to a quiet song\nThis is where we all belong.",
            dominantColor = 0xFFE6A368
        ),
        Track(
            id = "track_5",
            title = "Velocity Drive",
            artist = "Neon Overdrive",
            album = "Outrun 1984",
            durationSec = 230,
            coverUrl = "https://picsum.photos/seed/larpify5/600/600",
            audioUrl = "https://archive.org/download/synthwave-drive-free/high_speed_synth.mp3",
            genre = "Electronic",
            lyrics = "Dashboard glowing in gradient red\nChasing the horizon miles ahead\nTurbo engines and analog heat\nCruising down the electric street.",
            dominantColor = 0xFFFF6B35
        ),
        Track(
            id = "track_6",
            title = "Acoustic Ember",
            artist = "Cedar & Pine",
            album = "Woodland Echoes",
            durationSec = 192,
            coverUrl = "https://picsum.photos/seed/larpify6/600/600",
            audioUrl = "https://archive.org/download/acoustic-folk-free-audio/ember_fire.mp3",
            genre = "Rock",
            lyrics = "Fingers brushing across wooden strings\nStories that the cold mountain brings\nCampfire fading into the blue dawn\nAs the melodic warmth lingers on.",
            dominantColor = 0xFFD4AF37
        ),
        Track(
            id = "track_7",
            title = "Deep Ocean Serenade",
            artist = "Subsurface",
            album = "Bioluminescence",
            durationSec = 245,
            coverUrl = "https://picsum.photos/seed/larpify7/600/600",
            audioUrl = "https://archive.org/download/ambient-relaxation-archive/deep_ocean_ambience.mp3",
            genre = "Chill",
            lyrics = "Drifting beneath the sapphire tide\nWhere gentle currents quietly glide\nWeightless dreams in glowing deep\nA calm hypnotic oceanic sleep.",
            dominantColor = 0xFF1DE9B6
        ),
        Track(
            id = "track_8",
            title = "Tech & Future Beats (Video)",
            artist = "Studio Larpify",
            album = "Visual Podcasts Ep. 12",
            durationSec = 260,
            coverUrl = "https://picsum.photos/seed/larpify8/600/600",
            audioUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            isVideo = true,
            genre = "Podcasts",
            lyrics = "[Video Podcast Episode]\nWelcome to Larpify Studio Sessions.\nToday we explore modern digital audio streaming, zero-ad architectures, and creative freedom.",
            dominantColor = 0xFF10B981
        ),
        Track(
            id = "track_9",
            title = "Lo-Fi Animation Stream (Video)",
            artist = "Tokyo Chillroom",
            album = "Animated Lo-Fi Visuals",
            durationSec = 310,
            coverUrl = "https://picsum.photos/seed/larpify9/600/600",
            audioUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            isVideo = true,
            genre = "Podcasts",
            lyrics = "[Animated Music Video]\nTokyo rain and cozy desktop animations streaming in high fidelity.",
            dominantColor = 0xFFFF2A85
        ),
        Track(
            id = "track_10",
            title = "Golden Hour Reverie",
            artist = "Solar Drift",
            album = "Sunset Memoirs",
            durationSec = 208,
            coverUrl = "https://picsum.photos/seed/larpify10/600/600",
            audioUrl = "https://archive.org/download/free-lofi-beats-to-relax-to/01%20Coffee%20Shop%20Vibes.mp3",
            genre = "Chill",
            lyrics = "Warm golden sun sinking low\nLetting all the tension and worries go\nJust melody and pure peace.",
            dominantColor = 0xFFFFB300
        ),
        Track(
            id = "track_11",
            title = "808 Thunder",
            artist = "Metro Flow",
            album = "Trap Odyssey",
            durationSec = 165,
            coverUrl = "https://picsum.photos/seed/larpify11/600/600",
            audioUrl = "https://archive.org/download/hip-hop-instrumentals-public-domain/track3_chillhop.mp3",
            genre = "Hip Hop",
            lyrics = "Sub-bass shaking the concrete floor\nHip hop groove we were waiting for.",
            dominantColor = 0xFF9C27B0
        ),
        Track(
            id = "track_12",
            title = "Neon Highway Racer",
            artist = "Turbo 84",
            album = "Grid Runner",
            durationSec = 222,
            coverUrl = "https://picsum.photos/seed/larpify12/600/600",
            audioUrl = "https://archive.org/download/electronic-synthwave-royalty-free/retro_pulse.mp3",
            genre = "Electronic",
            lyrics = "Speeding through a digital paradise\nRoll the electric dice.",
            dominantColor = 0xFF00E5FF
        ),
        Track(
            id = "track_13",
            title = "Coffeehouse Whispers",
            artist = "Rainy Day Jazz",
            album = "Midnight Cafe",
            durationSec = 195,
            coverUrl = "https://picsum.photos/seed/larpify13/600/600",
            audioUrl = "https://archive.org/download/lofi-study-beats-archive/study_session_01.mp3",
            genre = "Lo-fi",
            lyrics = "Acoustic chords and warm espresso aroma\nPeaceful afternoon study aroma.",
            dominantColor = 0xFF8D6E63
        ),
        Track(
            id = "track_14",
            title = "Electric Aurora (Video)",
            artist = "Visual Soundscapes",
            album = "Bioluminescent Dreams",
            durationSec = 280,
            coverUrl = "https://picsum.photos/seed/larpify14/600/600",
            audioUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            isVideo = true,
            genre = "Podcasts",
            lyrics = "[Visual Video Stream]\nElectric fire and northern lights in stunning high definition.",
            dominantColor = 0xFF00E676
        )
    )

    val DefaultPlaylists = listOf(
        Playlist(
            id = "playlist_liked",
            title = "Liked Songs",
            description = "Your favorite tracks on Larpify",
            coverUrl = "https://picsum.photos/seed/heartcover/600/600",
            isPinned = true,
            trackIds = listOf("track_1", "track_2", "track_4"),
            ownerName = "You",
            isSystem = true
        ),
        Playlist(
            id = "playlist_chill_mix",
            title = "Chill Mix",
            description = "Mellow lo-fi and relaxing ambient soundscapes",
            coverUrl = "https://picsum.photos/seed/chillmix/600/600",
            trackIds = listOf("track_1", "track_4", "track_7"),
            ownerName = "Larpify Mixes",
            isSystem = true
        ),
        Playlist(
            id = "playlist_electronic_mix",
            title = "Electronic Mix",
            description = "High energy synthwave, cyberpunk beats & electro",
            coverUrl = "https://picsum.photos/seed/elecmix/600/600",
            trackIds = listOf("track_2", "track_5"),
            ownerName = "Larpify Mixes",
            isSystem = true
        ),
        Playlist(
            id = "playlist_hiphop_mix",
            title = "Hip Hop Mix",
            description = "Cloud rap, booming 808s and urban flows",
            coverUrl = "https://picsum.photos/seed/hiphopmix/600/600",
            trackIds = listOf("track_3"),
            ownerName = "Larpify Mixes",
            isSystem = true
        ),
        Playlist(
            id = "playlist_video_vault",
            title = "Video Podcasts & Shows",
            description = "Full visual audio episodes and music videos",
            coverUrl = "https://picsum.photos/seed/videovault/600/600",
            trackIds = listOf("track_8", "track_9"),
            ownerName = "Larpify Studio",
            isSystem = true
        )
    )

    val SearchCategories = listOf(
        CategoryItem("Music", 0xFFE91E63),
        CategoryItem("Podcasts", 0xFF009688),
        CategoryItem("Live Events", 0xFF673AB7),
        CategoryItem("Made For You", 0xFF1E88E5),
        CategoryItem("New Releases", 0xFFFF5722),
        CategoryItem("Charts", 0xFF8E24AA),
        CategoryItem("Hip Hop", 0xFFF57C00),
        CategoryItem("Lo-fi", 0xFF43A047),
        CategoryItem("Electronic", 0xFF00ACC1),
        CategoryItem("Rock", 0xFFD81B60),
        CategoryItem("Chill", 0xFF3949AB),
        CategoryItem("Mood", 0xFF5E35B1)
    )

    val DiscoverCards = listOf(
        DiscoverItem("#cloud rap", "Aether Flow - Skyline Dreams", "https://picsum.photos/seed/disc1/400/700", "track_3"),
        DiscoverItem("#lofi", "Mellow Sunday - Cassettes", "https://picsum.photos/seed/disc2/400/700", "track_4"),
        DiscoverItem("#rave", "Synthia Void - Cyber Pulse", "https://picsum.photos/seed/disc3/400/700", "track_2"),
        DiscoverItem("#gym", "Neon Overdrive - Velocity", "https://picsum.photos/seed/disc4/400/700", "track_5"),
        DiscoverItem("#late night", "Larpify Collective - Midnight", "https://picsum.photos/seed/disc5/400/700", "track_1")
    )

    val PopularArtists = listOf(
        ArtistItem("Larpify Collective", "1.4M monthly listeners", "https://picsum.photos/seed/art1/300/300"),
        ArtistItem("Synthia Void", "980K monthly listeners", "https://picsum.photos/seed/art2/300/300"),
        ArtistItem("Aether Flow", "820K monthly listeners", "https://picsum.photos/seed/art3/300/300"),
        ArtistItem("Tokyo Chillroom", "650K monthly listeners", "https://picsum.photos/seed/art4/300/300"),
        ArtistItem("Cedar & Pine", "420K monthly listeners", "https://picsum.photos/seed/art5/300/300")
    )
}

data class CategoryItem(val name: String, val colorHex: Long)
data class DiscoverItem(val tag: String, val title: String, val imageUrl: String, val trackId: String)
data class ArtistItem(val name: String, val monthlyListeners: String, val imageUrl: String)
