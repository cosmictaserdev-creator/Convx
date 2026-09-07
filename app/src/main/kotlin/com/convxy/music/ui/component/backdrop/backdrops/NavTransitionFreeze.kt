/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component.backdrop.backdrops

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.flow.collectLatest

// Covers Motion.PushMillis (260), the longest NavHost transition in MainActivity,
// plus a buffer for frame-delivery slop. Must not be shorter than the transition:
// thawing mid-slide puts the whole-tree re-record back exactly where it hurts. Also
// must not be padded past that need: this window is how long the glass backdrop
// behind chrome visibly shows stale content during a full-screen push (see
// Motion.PushMillis) -- it was 450ms and that staleness was clearly visible.
private const val NavTransitionFreezeWindowNs = 330_000_000L

/**
 * Time-boxed freeze for the app-level [layerBackdrop] across a screen-to-screen
 * navigation transition. [BackdropFreeze] only reacts to scroll gestures — a plain
 * nav transition (slide + fade between two screens) is neither a scroll nor a
 * fling, so it never froze, and the backdrop re-recorded every frame of every
 * navigation for the transition's whole duration (the same per-frame cost already
 * measured and fixed for scroll).
 *
 * Elapsed-time based rather than tied to a real "transition running" callback:
 * Navigation-Compose's `AnimatedContent` doesn't expose one at the `NavHost` call
 * site, but the transition duration here is a known fixed constant, so a timer is
 * sufficient rather than a fallback.
 */
class NavTransitionFreeze {
    private val startedAtNs = longArrayOf(0L)

    val frozen: () -> Boolean = {
        val started = startedAtNs[0]
        started != 0L && System.nanoTime() - started < NavTransitionFreezeWindowNs
    }

    fun markTransitionStarted() {
        startedAtNs[0] = System.nanoTime()
    }
}

/** Marks a transition started every time [currentRoute] changes. */
@Composable
fun rememberNavTransitionFreeze(currentRoute: String?): NavTransitionFreeze {
    val freeze = remember { NavTransitionFreeze() }
    LaunchedEffect(currentRoute) {
        freeze.markTransitionStarted()
    }
    return freeze
}

/**
 * Same freeze, driven by a [PagerState] settling instead of a route change.
 *
 * The main tabs are pages of one HorizontalPager, not NavHost destinations, so a tab
 * switch changes no route -- [rememberNavTransitionFreeze] never fired for it. Nor did
 * [BackdropFreeze]: `animateScrollToPage` mutates the pager state directly and does not
 * dispatch nested scroll the way a gesture or fling does, so the scroll-driven freeze
 * saw nothing either. The app backdrop therefore re-recorded the entire tree on every
 * frame of every tab switch -- the same per-frame cost already measured and fixed for
 * scroll (47ms of layer.record vs 1.9ms), on the single most-used interaction in the app.
 *
 * Re-marks every frame while the pager is in motion rather than once at the start,
 * because a tab animation outlasts the fixed freeze window and would otherwise thaw
 * mid-slide. [isScrollInProgress] is read in a coroutine, never inside `frozen` --
 * a snapshot read there runs in the draw phase and re-invalidates every frame forever.
 */
@Composable
fun rememberPagerTransitionFreeze(pagerState: PagerState): NavTransitionFreeze {
    val freeze = remember { NavTransitionFreeze() }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .collectLatest { scrolling ->
                while (scrolling) {
                    freeze.markTransitionStarted()
                    withFrameNanos { }
                }
            }
    }
    return freeze
}
