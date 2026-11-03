package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.components.GbwColorMatrix
import com.example.ui.components.rememberGbwGlowColors
import kotlinx.coroutines.delay

data class GenreTile(
    val name: String,
    val subtitle: String,
    val heightDp: Int
)

@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    allTracks: List<Track>,
    playlists: List<Playlist>,
    isSearchingApi: Boolean,
    monoCovers: Boolean = true,
    onTrackSelect: (Track) -> Unit,
    onPlaylistSelect: (Playlist) -> Unit,
    onTrackMenuClick: (Track) -> Unit,
    onVoiceSearchClick: () -> Unit
) {
    val gbwGlow = rememberGbwGlowColors()

    // Typewriter Suggestion Cycling
    val suggestions = remember {
        listOf(
            "What do you want to drift to?",
            "Search 'Rainy night lo-fi'...",
            "Search 'Deep focus jazz'...",
            "Search 'Synthwave highway'...",
            "Search 'Ambient sleep drone'...",
            "Search YouTube songs or creators..."
        )
    }

    var suggestionIdx by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3400)
            suggestionIdx = (suggestionIdx + 1) % suggestions.size
        }
    }

    val genres = remember {
        listOf(
            GenreTile("Lo-fi Beats", "Gentle tape noise & dusty keys", 110),
            GenreTile("Night Drive", "Dark synth rhythms & city lights", 140),
            GenreTile("Ambient Space", "Infinite reverberations & drones", 120),
            GenreTile("Deep Rain", "Storms, water drips & warmth", 95),
            GenreTile("Acoustic Calm", "Fingerpicked guitars & folk", 130),
            GenreTile("Chillhop", "Mellow kicks & soulful samples", 105),
            GenreTile("Classical Drift", "Minimalist piano & strings", 135),
            GenreTile("Electronic Flow", "Hypnotic pulses & modular synthesis", 115)
        )
    }

    val isSearching = searchQuery.trim().isNotBlank()

    val filteredTracks = if (isSearching) {
        allTracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.genre.contains(searchQuery, ignoreCase = true)
        }
    } else emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Large Centered Search Field with Animated Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = suggestions[suggestionIdx],
                        color = Color(0xFF777777),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(onClick = onVoiceSearchClick) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedContainerColor = Color(0xFF141416),
                    unfocusedContainerColor = Color(0xFF141416),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Brush.horizontalGradient(gbwGlow), RoundedCornerShape(20.dp))
                    .testTag("search_input_field")
            )
        }

        if (isSearching) {
            // Live Search Results in Glass Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                if (isSearchingApi) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Searching online archives...", color = Color(0xFF888888), fontSize = 12.sp)
                        }
                    }
                }

                if (filteredTracks.isEmpty() && !isSearchingApi) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No tracks found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Try another keyword or paste a YouTube link", color = Color(0xFF777777), fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(filteredTracks) { track ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onTrackSelect(track) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF202022))
                                ) {
                                    AsyncImage(
                                        model = track.coverUrl,
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        colorFilter = if (monoCovers) GbwColorMatrix.MonochromaticFilter else null,
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${track.artist} • ${track.album}", color = Color(0xFF888888), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                IconButton(onClick = { onTrackMenuClick(track) }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Masonry Mood & Genre Discovery Tiles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "EXPLORE DRIFT GENRES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(genres) { genre ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(genre.heightDp.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSearchQueryChange(genre.name.substringBefore(" ")) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141416)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1FFFFFFF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = genre.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = genre.subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF888888),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
