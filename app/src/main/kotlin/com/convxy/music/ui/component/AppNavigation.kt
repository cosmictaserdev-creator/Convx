/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.convxy.music.constants.AppBackgroundColorKey
import com.convxy.music.constants.AppTextColorKey
import com.convxy.music.utils.rememberPreference
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.screens.Screens
import com.convxy.music.ui.utils.pressWobble
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Immutable
private data class NavItemState(
    val isSelected: Boolean,
    val iconRes: Int
)

@Stable
internal fun isRouteSelected(currentRoute: String?, screenRoute: String, navigationItems: List<Screens>): Boolean {
    if (currentRoute == null) return false
    if (currentRoute == screenRoute) return true
    return navigationItems.any { it.route == screenRoute } &&
        currentRoute.startsWith("$screenRoute/")
}

/**
 * The nav item whose puck/indicator should be lit.
 *
 * Returns the tab matching [currentRoute], or — while on a non-tab destination
 * (a drilled-in detail, a settings sub-page, etc.) where nothing matches — the
 * last tab that did match, so the indicator holds its place instead of snapping
 * back to the first tab (Home).
 *
 * This drives the VISUAL puck only. For click handling call [isRouteSelected]
 * directly: tapping a held-but-not-current tab must still navigate to it.
 */
@Composable
internal fun rememberStickySelectedRoute(
    currentRoute: String?,
    navigationItems: List<Screens>,
): String? {
    val matched = navigationItems.firstOrNull {
        isRouteSelected(currentRoute, it.route, navigationItems)
    }?.route
    var last by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(matched) { if (matched != null) last = matched }
    return matched ?: last
}

@Composable
fun AppNavigationRail(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    onSearchLongClick: (() -> Unit)? = null
) {
    val containerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val haptics = LocalHapticFeedback.current
    val viewConfiguration = LocalViewConfiguration.current

    NavigationRail(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Spacer(modifier = Modifier.weight(1f))

        navigationItems.forEach { screen ->
            val isSelected = remember(currentRoute, screen.route) {
                isRouteSelected(currentRoute, screen.route, navigationItems)
            }
            val iconRes = screen.icon

            val isSearchItem = screen == Screens.Search && onSearchLongClick != null
            val interactionSource = remember { MutableInteractionSource() }
            // LaunchedEffect(interactionSource) only launches once (interactionSource's
            // identity never changes), so its collectLatest closure would otherwise
            // freeze isSelected at whatever it was on that first launch — every tap
            // afterward re-reads that stale value instead of the current route.
            // rememberUpdatedState keeps it live.
            val currentIsSelected by rememberUpdatedState(isSelected)

            if (isSearchItem) {
                LaunchedEffect(interactionSource) {
                    var isLongClick = false
                    interactionSource.interactions.collectLatest { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> {
                                isLongClick = false
                                delay(viewConfiguration.longPressTimeoutMillis)
                                isLongClick = true
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSearchLongClick.invoke()
                            }
                            is PressInteraction.Release -> {
                                if (!isLongClick) {
                                    onItemClick(screen, currentIsSelected)
                                }
                            }
                            is PressInteraction.Cancel -> {
                                isLongClick = false
                            }
                        }
                    }
                }
            }

            NavigationRailItem(
                selected = isSelected,
                onClick = {
                    if (!isSearchItem) {
                        onItemClick(screen, isSelected)
                    }
                },
                interactionSource = interactionSource,
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = stringResource(screen.titleId),
                        modifier = Modifier.pressWobble(interactionSource)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun AppLandscapeRail(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
) {
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val (appBackgroundColorInt) = rememberPreference(AppBackgroundColorKey, 0)
    val (appTextColorInt) = rememberPreference(AppTextColorKey, 0)

    val backgroundColor = when {
        useGlass -> Color.Transparent
        pureBlack -> Color.Black
        appBackgroundColorInt != 0 -> Color(appBackgroundColorInt)
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val selectedContentColor = com.convxy.music.ui.theme.LocalAccentColor.current
    // Non-glass fell back to hardcoded white, which is invisible on a light
    // theme. glassConfig.textColor is already the adaptive colour (computed from
    // what the surface composites to); onSurface is its non-glass equivalent.
    val unselectedContentColor = when {
        appTextColorInt != 0 -> Color(appTextColorInt)
        useGlass -> glassConfig.textColor
        else -> MaterialTheme.colorScheme.onSurface
    }

    val railModifier = if (useGlass) {
        modifier.liquidGlass(
            config = glassConfig,
            shape = RoundedCornerShape(16.dp),
            highlightAlpha = 0.3f,
        )
    } else {
        modifier.background(backgroundColor, RoundedCornerShape(16.dp))
    }

    Column(
        modifier = railModifier
            .padding(8.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        navigationItems.forEach { screen ->
            val isSelected = remember(currentRoute, screen.route) {
                isRouteSelected(currentRoute, screen.route, navigationItems)
            }
            val iconRes = screen.icon

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onItemClick(screen, isSelected) }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(screen.titleId),
                    tint = if (isSelected) selectedContentColor else unselectedContentColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = stringResource(screen.titleId),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) selectedContentColor else unselectedContentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(Modifier.height(8.dp))
        }
    }
}
