package com.example.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.example.model.BackgroundEffect
import com.example.model.ThemeConfig

object ThemeManager {

    val MonoPulse = ThemeConfig(
        id = "mono_pulse",
        name = "Mono Pulse",
        description = "Deep graphite, liquid mercury lines & pearl white glow (Default)",
        background = Color(0xFF0C0C0C),
        surface = Color(0xFF161616),
        surfaceVariant = Color(0xFF222222),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFCCCCCC),
        onBackground = Color(0xFFEEEEEE),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x99FFFFFF),
        cardCornerRadius = 16,
        backgroundEffect = BackgroundEffect.AURORA_GREY,
        glassBlurAlpha = 0.70f
    )

    val Frost = ThemeConfig(
        id = "frost",
        name = "Frost",
        description = "Translucent frosted glass over subtle cold fog",
        background = Color(0xFF111315),
        surface = Color(0xFF1A1D20),
        surfaceVariant = Color(0xFF262A2E),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFB8C0C8),
        onBackground = Color(0xFFF0F4F8),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x80D0D8E0),
        cardCornerRadius = 20,
        backgroundEffect = BackgroundEffect.SNOWFALL,
        glassBlurAlpha = 0.85f
    )

    val StaticNoise = ThemeConfig(
        id = "static_noise",
        name = "Static Noise",
        description = "Monochrome analog TV grain & charcoal textures",
        background = Color(0xFF0E0E0E),
        surface = Color(0xFF181818),
        surfaceVariant = Color(0xFF262626),
        primary = Color(0xFFE8E8E8),
        secondary = Color(0xFFAAAAAA),
        onBackground = Color(0xFFDDDDDD),
        onSurface = Color(0xFFEEEEEE),
        accentGlow = Color(0x66E8E8E8),
        cardCornerRadius = 10,
        backgroundEffect = BackgroundEffect.STATIC_NOISE,
        glassBlurAlpha = 0.60f
    )

    val ChromeWave = ThemeConfig(
        id = "chrome_wave",
        name = "Chrome Wave",
        description = "Liquid chrome metal highlights and reflections",
        background = Color(0xFF0A0A0B),
        surface = Color(0xFF141416),
        surfaceVariant = Color(0xFF202024),
        primary = Color(0xFFF5F5F7),
        secondary = Color(0xFFB4B4BC),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x88E5E5EB),
        cardCornerRadius = 18,
        backgroundEffect = BackgroundEffect.LIQUID_METAL,
        glassBlurAlpha = 0.75f
    )

    val GhostAurora = ThemeConfig(
        id = "ghost_aurora",
        name = "Ghost Aurora",
        description = "Spectral grey atmospheric light ribbons",
        background = Color(0xFF0A0D0E),
        surface = Color(0xFF131719),
        surfaceVariant = Color(0xFF1E2428),
        primary = Color(0xFFE2E8F0),
        secondary = Color(0xFF94A3B8),
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x77CBD5E1),
        cardCornerRadius = 16,
        backgroundEffect = BackgroundEffect.AURORA_GREY,
        glassBlurAlpha = 0.70f
    )

    val Scanline = ThemeConfig(
        id = "scanline",
        name = "Scanline",
        description = "Subtle monochrome raster CRT grid aesthetic",
        background = Color(0xFF0B0B0C),
        surface = Color(0xFF151517),
        surfaceVariant = Color(0xFF212124),
        primary = Color(0xFFEDEDED),
        secondary = Color(0xFF8E8E93),
        onBackground = Color(0xFFE5E5EA),
        onSurface = Color(0xFFF2F2F7),
        accentGlow = Color(0x66D1D1D6),
        cardCornerRadius = 8,
        backgroundEffect = BackgroundEffect.GRID_PULSE,
        glassBlurAlpha = 0.60f
    )

    val SlateDrift = ThemeConfig(
        id = "slate_drift",
        name = "Slate Drift",
        description = "Matte slate graphite with calm misty undertones",
        background = Color(0xFF121417),
        surface = Color(0xFF1C2024),
        surfaceVariant = Color(0xFF282E34),
        primary = Color(0xFFF1F5F9),
        secondary = Color(0xFF94A3B8),
        onBackground = Color(0xFFE2E8F0),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x6694A3B8),
        cardCornerRadius = 14,
        backgroundEffect = BackgroundEffect.SMOKE,
        glassBlurAlpha = 0.65f
    )

    val InkBleed = ThemeConfig(
        id = "ink_bleed",
        name = "Ink Bleed",
        description = "Deep sumi-e ink pools on textured parchment",
        background = Color(0xFF070707),
        surface = Color(0xFF111111),
        surfaceVariant = Color(0xFF1C1C1C),
        primary = Color(0xFFFAFAFA),
        secondary = Color(0xFFA1A1AA),
        onBackground = Color(0xFFF4F4F5),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x77FAFAFA),
        cardCornerRadius = 12,
        backgroundEffect = BackgroundEffect.RAIN_ON_GLASS,
        glassBlurAlpha = 0.70f
    )

    val Halo = ThemeConfig(
        id = "halo",
        name = "Halo",
        description = "Total eclipse silhouette with brilliant corona glow",
        background = Color(0xFF050505),
        surface = Color(0xFF101010),
        surfaceVariant = Color(0xFF1E1E1E),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFD4D4D8),
        onBackground = Color(0xFFFAFAFA),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0xB3FFFFFF),
        cardCornerRadius = 24,
        backgroundEffect = BackgroundEffect.STARFIELD,
        glassBlurAlpha = 0.80f
    )

    val PaperDark = ThemeConfig(
        id = "paper_dark",
        name = "Paper Dark",
        description = "Editorial charcoal newsprint & fine typography",
        background = Color(0xFF131313),
        surface = Color(0xFF1D1D1D),
        surfaceVariant = Color(0xFF292929),
        primary = Color(0xFFECECEC),
        secondary = Color(0xFFA3A3A3),
        onBackground = Color(0xFFF5F5F5),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x66E5E5E5),
        cardCornerRadius = 6,
        backgroundEffect = BackgroundEffect.FILM_GRAIN,
        glassBlurAlpha = 0.55f
    )

    val Whiteout = ThemeConfig(
        id = "whiteout",
        name = "Whiteout",
        description = "Stark high-contrast bright monochrome silver",
        background = Color(0xFF202022),
        surface = Color(0xFF2C2C30),
        surfaceVariant = Color(0xFF3A3A40),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFE0E0E6),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0xCCFFFFFF),
        cardCornerRadius = 18,
        backgroundEffect = BackgroundEffect.SNOWFALL,
        glassBlurAlpha = 0.85f
    )

    val Blackout = ThemeConfig(
        id = "blackout",
        name = "Blackout",
        description = "Pitch black OLED zero emission with hairline outlines",
        background = Color(0xFF000000),
        surface = Color(0xFF0A0A0A),
        surfaceVariant = Color(0xFF141414),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFF888888),
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFFFFFFF),
        accentGlow = Color(0x55FFFFFF),
        cardCornerRadius = 14,
        backgroundEffect = BackgroundEffect.PURE_BLACK,
        glassBlurAlpha = 0.50f
    )

    val AllThemes = listOf(
        MonoPulse,
        Frost,
        StaticNoise,
        ChromeWave,
        GhostAurora,
        Scanline,
        SlateDrift,
        InkBleed,
        Halo,
        PaperDark,
        Whiteout,
        Blackout
    )

    fun toColorScheme(theme: ThemeConfig): ColorScheme {
        return darkColorScheme(
            background = theme.background,
            surface = theme.surface,
            surfaceVariant = theme.surfaceVariant,
            primary = theme.primary,
            secondary = theme.secondary,
            onBackground = theme.onBackground,
            onSurface = theme.onSurface,
            outline = Color(0x33FFFFFF),
            outlineVariant = Color(0x1AFFFFFF)
        )
    }
}
