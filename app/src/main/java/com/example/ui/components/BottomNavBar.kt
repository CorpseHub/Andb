package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavigationTab

@Composable
fun BottomNavBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val gbwGlow = rememberGbwGlowColors()

    // Floating Glass Dock container (detached with margins and soft shadow)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x66FFFFFF))
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xE6141416))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(gbwGlow),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingDockTabItem(
                label = "Home",
                icon = Icons.Default.Home,
                isSelected = currentTab == NavigationTab.HOME,
                testTag = "tab_home",
                gbwGlow = gbwGlow,
                onClick = { onTabSelected(NavigationTab.HOME) }
            )
            FloatingDockTabItem(
                label = "Search",
                icon = Icons.Default.Search,
                isSelected = currentTab == NavigationTab.SEARCH,
                testTag = "tab_search",
                gbwGlow = gbwGlow,
                onClick = { onTabSelected(NavigationTab.SEARCH) }
            )
            FloatingDockTabItem(
                label = "Library",
                icon = Icons.Default.ViewAgenda,
                isSelected = currentTab == NavigationTab.LIBRARY,
                testTag = "tab_library",
                gbwGlow = gbwGlow,
                onClick = { onTabSelected(NavigationTab.LIBRARY) }
            )
            FloatingDockTabItem(
                label = "Studio",
                icon = Icons.Default.Palette,
                isSelected = currentTab == NavigationTab.THEMES,
                testTag = "tab_themes",
                gbwGlow = gbwGlow,
                onClick = { onTabSelected(NavigationTab.THEMES) }
            )
            FloatingDockTabItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentTab == NavigationTab.SETTINGS,
                testTag = "tab_settings",
                gbwGlow = gbwGlow,
                onClick = { onTabSelected(NavigationTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun FloatingDockTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    gbwGlow: List<Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF888888),
        label = "tab_icon_color"
    )

    val backgroundAlpha by animateDpAsState(
        targetValue = if (isSelected) 1.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tab_bg"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0x33FFFFFF) else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 14.dp else 10.dp, vertical = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )

            // Animated expansion: active tab shows label horizontally
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Moving GBW light underline under active tab
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Brush.horizontalGradient(gbwGlow))
            )
        }
    }
}
