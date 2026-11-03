package com.example.model

import androidx.compose.ui.graphics.Color

enum class TrackSource {
    FILE, CATALOG, YOUTUBE
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "DriftMoods Originals",
    val durationSec: Int = 180,
    val coverUrl: String,
    val audioUrl: String,
    val videoUrl: String? = null,
    val isVideo: Boolean = false,
    val genre: String = "Lo-fi",
    val lyrics: String = "DriftMoods • Immersion Flow\nFree music. Zero ads. Drift on.",
    val isUserUpload: Boolean = false,
    val dominantColor: Long = 0xFFFFFFFF,
    val source: TrackSource = TrackSource.CATALOG,
    val youtubeVideoId: String? = null,
    val originalUrl: String? = null,
    val licenseInfo: String? = "Creative Commons / Legal Stream"
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String = "Curated on DriftMoods",
    val coverUrl: String,
    val isPinned: Boolean = false,
    val trackIds: List<String> = emptyList(),
    val ownerName: String = "DriftMoods Listener",
    val isSystem: Boolean = false
)

enum class RepeatMode {
    OFF, ALL, ONE
}

enum class NavigationTab {
    HOME, SEARCH, LIBRARY, THEMES, SETTINGS
}

enum class SoundPreset {
    NORMAL, BASS_BOOST
}

enum class CoverStyle {
    VINYL, SQUARE, ROUND, POLAROID
}

enum class DockStyle {
    FLOATING, COMPACT, ALWAYS_LABELED
}

enum class PlayerStyle {
    PILL, BAR, CARD
}

enum class BackgroundEffect {
    AURORA_GREY, SMOKE, STARFIELD, RAIN_ON_GLASS, GRID_PULSE, FILM_GRAIN, STATIC_NOISE, LIQUID_METAL, SNOWFALL, PURE_BLACK
}

data class SnowConfig(
    val enabled: Boolean = true,
    val density: Int = 45, // particle count
    val speed: Float = 1.0f,
    val windDirection: Float = 0.4f, // -1.0 to 1.0
    val flakeSize: Float = 3.5f,
    val brightness: Float = 0.85f,
    val blur: Boolean = true,
    val onlyHomeAndNowPlaying: Boolean = false
)

data class PlaybackSettings(
    val soundPreset: SoundPreset = SoundPreset.NORMAL,
    val crossfadeSec: Int = 0,
    val gaplessPlayback: Boolean = true,
    val autoplaySimilar: Boolean = true,
    val audioQuality: String = "High", // "Normal", "High", "Hi-Fi Lossless"
    val dataSaver: Boolean = false,
    val reduceMotion: Boolean = false,
    val batterySaver: Boolean = false,
    val monoCovers: Boolean = true, // Monochrome album covers on by default
    val volumePercent: Int = 50,
    val coverStyle: CoverStyle = CoverStyle.VINYL,
    val dockStyle: DockStyle = DockStyle.FLOATING,
    val playerStyle: PlayerStyle = PlayerStyle.PILL,
    val motionIntensity: Float = 1.0f, // 0f off, 0.5f calm, 1.0f normal, 1.5f wild
    val snowConfig: SnowConfig = SnowConfig()
)

data class UserSession(
    val isGuest: Boolean = true,
    val username: String = "Guest Drifter",
    val email: String = "drifter@driftmoods.app",
    val guestStartTimeMs: Long = System.currentTimeMillis(),
    val adsServedCount: Int = 0,
    val passkeys: List<String> = listOf("Biometric Android Key", "DriftMoods Security Key"),
    val twoFactorEnabled: Boolean = false,
    val isPrivateProfile: Boolean = false
)

data class ThemeConfig(
    val id: String,
    val name: String,
    val description: String,
    val background: Color = Color(0xFF0D0D0D),
    val surface: Color = Color(0xFF161616),
    val surfaceVariant: Color = Color(0xFF222222),
    val primary: Color = Color(0xFFFFFFFF),
    val secondary: Color = Color(0xFFB0B0B0),
    val onBackground: Color = Color(0xFFEEEEEE),
    val onSurface: Color = Color(0xFFFFFFFF),
    val accentGlow: Color = Color(0xFFE5E5E5),
    val cardCornerRadius: Int = 14,
    val backgroundEffect: BackgroundEffect = BackgroundEffect.AURORA_GREY,
    val glassBlurAlpha: Float = 0.65f,
    val isCustom: Boolean = false
)
