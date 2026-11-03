package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundEffect
import com.example.model.CoverStyle
import com.example.model.DockStyle
import com.example.model.PlaybackSettings
import com.example.model.PlayerStyle
import com.example.model.SnowConfig
import com.example.model.ThemeConfig
import com.example.theme.ThemeManager

@Composable
fun ThemesScreen(
    activeTheme: ThemeConfig,
    playbackSettings: PlaybackSettings,
    onSelectTheme: (ThemeConfig) -> Unit,
    onUpdatePlaybackSettings: (PlaybackSettings) -> Unit,
    onShowToast: (String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf("Presets") } // "Presets", "Builder", "Snow"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("themes_studio_screen")
    ) {
        // Studio Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
            Text(
                text = "Drift Studio",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Full aesthetic customization • Monochromatic GBW engine",
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )
        }

        // Sub-tabs row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Presets", "Custom Builder", "Snow Settings").forEach { tab ->
                val isSel = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSel) Color.White else Color(0xFF1E1E20))
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSel) Color.Black else Color(0xFFAAAAAA),
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0x1AFFFFFF), thickness = 1.dp)

        when (activeSubTab) {
            "Presets" -> PresetsSubView(
                activeTheme = activeTheme,
                onSelectTheme = onSelectTheme,
                onShowToast = onShowToast
            )
            "Custom Builder" -> BuilderSubView(
                activeTheme = activeTheme,
                settings = playbackSettings,
                onUpdateSettings = onUpdatePlaybackSettings,
                onShowToast = onShowToast
            )
            "Snow Settings" -> SnowSettingsSubView(
                snow = playbackSettings.snowConfig,
                onUpdateSnow = { updated ->
                    onUpdatePlaybackSettings(playbackSettings.copy(snowConfig = updated))
                },
                onShowToast = onShowToast
            )
        }
    }
}

@Composable
private fun PresetsSubView(
    activeTheme: ThemeConfig,
    onSelectTheme: (ThemeConfig) -> Unit,
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text("12 MONOCHROME PRESETS", color = Color(0xFF888888), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(ThemeManager.AllThemes) { theme ->
            val isSelected = theme.id == activeTheme.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onSelectTheme(theme)
                        onShowToast("Applied preset: ${theme.name}")
                    }
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color.White else Color(0x1AFFFFFF),
                        shape = RoundedCornerShape(14.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = theme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.background)
                            .border(1.dp, Color(0x33FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(theme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = theme.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = theme.description,
                            color = Color(0xFF888888),
                            fontSize = 11.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

@Composable
private fun BuilderSubView(
    activeTheme: ThemeConfig,
    settings: PlaybackSettings,
    onUpdateSettings: (PlaybackSettings) -> Unit,
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Section: Cover Style
        item {
            Text("COVER ART STYLE", color = Color(0xFF888888), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    listOf(
                        CoverStyle.VINYL to "Vinyl Disc (Spinning grooves)",
                        CoverStyle.SQUARE to "Square Sleeve (Minimal)",
                        CoverStyle.ROUND to "Circular Disc",
                        CoverStyle.POLAROID to "Polaroid Frame"
                    ).forEach { (style, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings(settings.copy(coverStyle = style))
                                    onShowToast("Cover Style: ${style.name}")
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.coverStyle == style,
                                onClick = { onUpdateSettings(settings.copy(coverStyle = style)) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Monochrome Cover Filter Toggle
        item {
            Text("ALBUM ART MONOCHROME", color = Color(0xFF888888), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mono Covers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Render all album artwork in greyscale for a pure aesthetic", color = Color(0xFF888888), fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.monoCovers,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(monoCovers = it))
                            onShowToast(if (it) "Mono Covers: On" else "Mono Covers: Off")
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color.White)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Motion Intensity
        item {
            Text("MOTION INTENSITY", color = Color(0xFF888888), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Slider(
                        value = settings.motionIntensity,
                        onValueChange = { onUpdateSettings(settings.copy(motionIntensity = it)) },
                        valueRange = 0f..2f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )
                    val label = when {
                        settings.motionIntensity <= 0.1f -> "Off"
                        settings.motionIntensity <= 0.6f -> "Calm"
                        settings.motionIntensity <= 1.2f -> "Normal"
                        else -> "Wild"
                    }
                    Text("Current: $label", color = Color(0xFF888888), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Actions (Randomize, Reset, Export)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val randomTheme = ThemeManager.AllThemes.random()
                        onShowToast("Randomized to ${randomTheme.name}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222224), contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Randomize", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onUpdateSettings(PlaybackSettings())
                        onShowToast("Reset Studio to default")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222224), contentColor = Color(0xFF888888)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

@Composable
private fun SnowSettingsSubView(
    snow: SnowConfig,
    onUpdateSnow: (SnowConfig) -> Unit,
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text("ANIMATED SNOWFALL ENGINE", color = Color(0xFF888888), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Snowfall", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Particle system with 3 parallax layers & beat-reactivity", color = Color(0xFF888888), fontSize = 11.sp)
                        }
                        Switch(
                            checked = snow.enabled,
                            onCheckedChange = {
                                onUpdateSnow(snow.copy(enabled = it))
                                onShowToast(if (it) "Snow enabled" else "Snow disabled")
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color.White)
                        )
                    }

                    HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 10.dp))

                    Text("Density (${snow.density} particles)", color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = snow.density.toFloat(),
                        onValueChange = { onUpdateSnow(snow.copy(density = it.toInt())) },
                        valueRange = 15f..80f,
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Falling Speed (${String.format("%.1fx", snow.speed)})", color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = snow.speed,
                        onValueChange = { onUpdateSnow(snow.copy(speed = it)) },
                        valueRange = 0.4f..2.5f,
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Wind Direction (${if (snow.windDirection > 0) "East" else "West"})", color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = snow.windDirection,
                        onValueChange = { onUpdateSnow(snow.copy(windDirection = it)) },
                        valueRange = -1.0f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )
                }
            }
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}
