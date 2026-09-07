/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.dp
import com.convxy.music.LocalPlayerAwareWindowInsets

/**
 * Window insets for a screen's TopAppBar so its title/back button clear the
 * floating side panel in tab view.
 *
 * The status bar on top (NOT [LocalPlayerAwareWindowInsets]'s top — that has the
 * app bar's own height baked in, which would push the bar down), plus the
 * horizontal insets, which already include the side panel's reserved start width
 * (0 on phone, so this is a no-op there and matches the Material default), plus
 * the display cutout's start/end insets — in landscape a camera cutout usually
 * sits on one of the horizontal edges, not the top, so `systemBars` alone (which
 * does not include the cutout) let title/action icons render underneath it. One
 * other screen in the app already adds `displayCutout.only(Start + End)` for its
 * own top bar; this shared function — used by nearly every other screen's top
 * bar — didn't, so this brings it in line.
 */
@Composable
fun appTopBarWindowInsets(): WindowInsets =
    WindowInsets.systemBars.only(WindowInsetsSides.Top)
        .add(WindowInsets.displayCutout.only(WindowInsetsSides.Start + WindowInsetsSides.End))
        .add(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal))

/**
 * Height of the floating top chrome, for a list that has no header of its own to sit
 * beneath it.
 *
 * These screens draw their top bar as a floating overlay and give the list no top inset
 * on purpose, so the hero artwork runs edge to edge under the status bar. That only works
 * while the hero is actually there. In search mode the header item is skipped, and the
 * first song row lands at y=0 — under the status bar, behind the search field. Adding
 * this in place of the header keeps the immersive hero and stops the list hiding under
 * the chrome.
 */
@Composable
fun FloatingChromeSpacer(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(appTopBarWindowInsets().only(WindowInsetsSides.Top))
            // The chrome row: a 48dp control plus its 8dp vertical padding.
            .height(64.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBarScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): TopAppBarScrollBehavior =
    AppBarScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll,
    )

@ExperimentalMaterial3Api
class AppBarScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true },
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = true
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y
                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                    if (consumed.y == 0f && available.y > 0f) {
                        // Reset the total content offset to zero when scrolling all the way down.
                        // This will eliminate some float precision inaccuracies.
                        state.contentOffset = 0f
                    }
                }
                // The bar's position is a function of how far the content is from its top,
                // not of the last gesture's direction. Accumulating consumed.y instead
                // (which is what this was) brought the bar back on any upward flick, so it
                // reappeared in the middle of a list over content it had nothing to do with.
                // Tied to contentOffset it is only on screen near the actual top.
                state.heightOffset = state.contentOffset.coerceIn(state.heightOffsetLimit, 0f)
                return Offset.Zero
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun TopAppBarState.resetHeightOffset() {
    if (heightOffset != 0f) {
        animate(
            initialValue = heightOffset,
            targetValue = 0f,
        ) { value, _ ->
            heightOffset = value
        }
    }
    // heightOffset is derived from contentOffset now, so leaving contentOffset where it
    // was would have the next scroll event snap the bar straight back out of view.
    contentOffset = 0f
}
