/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.convx.music.ui.component.LocalSampledChromeDark
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.darkIosColors
import com.convx.music.ui.ios.foundation.iosColorScheme

/** True inside a scope that is always drawn with the dark palette, whatever the app theme. */
val LocalForcedDark = compositionLocalOf { false }

/**
 * The app's glass settings resolved for the dark palette. The root builds it once from the same
 * preferences as the normal config, so a forced-dark scope gets dark tints, puck and rim values,
 * which are stored as fixed colours and cannot be flipped by the scope itself.
 */
val LocalDarkGlassEffectConfig = compositionLocalOf<com.convx.music.ui.component.GlassEffectConfig?> { null }

/**
 * Draws [content] with the dark palette regardless of the app theme: iOS colours, Material
 * colours and glass chrome. Used by album, artist, playlist and player screens and their menus.
 */
@Composable
fun ForcedDarkScope(enabled: Boolean = true, content: @Composable () -> Unit) {
    if (!enabled) {
        content()
        return
    }
    val accent = IosTheme.tint
    val colors = remember(accent) { darkIosColors(accent) }
    val darkGlass = LocalDarkGlassEffectConfig.current
    CompositionLocalProvider(
        LocalForcedDark provides true,
        LocalSampledChromeDark provides true,
        com.convx.music.ui.component.LocalGlassEffectConfig provides
            (darkGlass ?: com.convx.music.ui.component.LocalGlassEffectConfig.current),
    ) {
        IosTheme(darkTheme = true, accent = accent) {
            MaterialTheme(colorScheme = iosColorScheme(colors)) {
                // Icons and text that take their colour from the surrounding content colour
                // (search bar glyphs, menu icons) follow the dark palette too.
                CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides colors.label,
                    com.convx.music.ui.ios.compat.LocalContentColor provides colors.label,
                ) { content() }
            }
        }
    }
}

/** Routes always drawn with the dark palette: collection pages, artist pages and search results. */
fun isForcedDarkRoute(route: String?): Boolean =
    route != null && (route.startsWith("album/") || route.startsWith("artist/") ||
        route.startsWith("search/") || route.contains("playlist/"))
