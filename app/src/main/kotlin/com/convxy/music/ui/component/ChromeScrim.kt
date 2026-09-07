/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.convxy.music.constants.AppBarHeight

/**
 * How far a hero-header list has scrolled past its art, 0..1 — feeds
 * [ChromeScrim]'s intensity. Shared by Artist/Album/Playlist screens so the
 * floating back/share button row stays legible over regular content, not
 * just the hero art it starts over.
 */
@Composable
fun rememberChromeScrimProgress(lazyListState: LazyListState, thresholdPx: Float = 300f): Float {
    val progress by remember {
        derivedStateOf {
            val scrolledPx = if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                thresholdPx
            }
            (scrolledPx / thresholdPx).coerceIn(0f, 1f)
        }
    }
    return progress
}

/**
 * Progressive dark scrim behind a hero screen's floating back/share chrome —
 * transparent at the very top (over the hero art), fading to a dark shade
 * once scrolled past it. A true fade (transparent at the bottom edge), not a
 * flat rectangle: the bottom stop must stay [Color.Transparent] or the box's
 * own bounds show up as a visible line.
 */
@Composable
fun ChromeScrim(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppBarHeight * 2.5f)
            .background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.55f * progress),
                    0.35f to Color.Black.copy(alpha = 0.4f * progress),
                    0.7f to Color.Black.copy(alpha = 0.12f * progress),
                    1f to Color.Transparent,
                )
            )
    )
}
