/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.convxy.music.constants.IosOverscrollKey
import com.convxy.music.utils.rememberPreference
import kotlin.math.sign

/**
 * Everything a hero screen needs for pull-to-zoom, so each one is two lines
 * instead of five: pass [scale] to `HeroBackground(heroScale = …)` and
 * [Modifier.heroPullZoom] to its list.
 *
 * [rawPull] is the distance the FINGER has travelled past the top edge; [offset]
 * is how far the content is actually allowed to move, which is [rubberBand] of
 * that. Keeping the two apart is the whole point: the previous version stored the
 * damped value and damped it *again* on each delta with
 * `resistance = (1 - pull/maxPull) * 0.5`, so the content moved at HALF finger
 * speed from the very first pixel and then hit a hard clamp at maxPull. Starting
 * at half speed is what made the pull feel cheap and disconnected — iOS tracks the
 * finger 1:1 at the start and only stiffens as you go, which is exactly what the
 * rubber band curve does.
 *
 * Both are plain [MutableFloatState]s rather than an
 * [androidx.compose.animation.core.Animatable] mutated via `scope.launch { snapTo(...) }`
 * on every scroll delta — that pattern let a fast drag queue up dozens of
 * coroutines racing each other (same bug class as the app's overscroll had),
 * and the zoom could get stuck mid-scale when one of them applied a stale
 * value after a newer one. Scroll-time updates are now a direct synchronous
 * write; a coroutine (and a real spring) is only used once, on release.
 */
@Stable
class HeroZoom internal constructor(
    internal val rawPull: MutableFloatState,
    /** List height in px, measured by [heroPullZoom]; the curve scales with it. */
    internal val viewport: MutableFloatState,
    val maxPull: Float,
    val enabled: Boolean,
) {
    /**
     * How far the list is translated down, in px. Also what a hero image must
     * grow by to keep covering the top of the screen while the list slides.
     */
    val offset: Float
        get() = if (enabled) rubberBand(rawPull.floatValue, viewport.floatValue) else 0f

    /**
     * Zoom only responds to the TOP pull. The bottom edge shares the same offset
     * (it is the same rubber band, just negative) but must not shrink the hero.
     */
    val scale: Float
        get() = if (enabled) 1f + (offset / maxPull).coerceIn(0f, 1f) * 0.18f else 1f
}

@Composable
fun rememberHeroZoom(maxPull: Dp = 220.dp): HeroZoom {
    val rawPull = remember { mutableFloatStateOf(0f) }
    val viewport = remember { mutableFloatStateOf(0f) }
    val maxPullPx = with(LocalDensity.current) { maxPull.toPx() }
    val enabled by rememberPreference(IosOverscrollKey, defaultValue = true)
    return remember(rawPull, viewport, maxPullPx, enabled) {
        HeroZoom(rawPull, viewport, maxPullPx, enabled)
    }
}

/**
 * What to pass as a hero list's `overscrollEffect`: null while [heroPullZoom] owns
 * BOTH edges (it would otherwise eat the leftover before the zoom connection ever
 * sees it), otherwise the ambient effect, so switching the motion preference off
 * leaves the list with normal overscroll rather than none at all.
 */
@Composable
fun HeroZoom.listOverscroll(): OverscrollEffect? =
    if (enabled) null else rememberOverscrollEffect()

/**
 * Rubber-band overscroll for a hero-header list, on the same [rubberBand] curve as
 * the plain list bounce. The top pull additionally drives the zoom: pass
 * [HeroZoom.scale] to `HeroBackground(heroScale = …)`. The bottom edge is a plain
 * bounce. No-ops when the iOS-motion preference is off.
 *
 * Deliberately gesture-only: the top pull used to double as pull-to-refresh, which
 * meant a plain overscroll could kick off a network reload nobody asked for. The
 * stretch is a stretch now; reloading is the screen's own affair.
 */
fun Modifier.heroPullZoom(
    zoom: HeroZoom,
): Modifier = composed {
    if (!zoom.enabled) return@composed this

    val rawPull = zoom.rawPull
    val viewport = zoom.viewport
    val connection = remember(zoom) {
        object : NestedScrollConnection {
            // Scrolling back toward rest pays down the existing stretch before the
            // list itself gets to scroll, otherwise the content jumps. In raw
            // (finger) space, so the return trip is damped by the same curve as
            // the outbound pull and the content tracks the finger symmetrically.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val curr = rawPull.floatValue
                if (curr != 0f && available.y != 0f && sign(available.y) != sign(curr)) {
                    val target = curr + available.y
                    val settled = if (sign(target) != sign(curr)) 0f else target
                    rawPull.floatValue = settled
                    return Offset(0f, settled - curr)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Both edges: pulling down past the top drives the hero stretch,
                // pushing up past the bottom is a plain bounce. The bottom used to
                // do nothing at all here — heroPullZoom was top-only AND
                // listOverscroll() hands these lists a null effect, so the bottom
                // edge had no bounce of any kind.
                //
                // Drag only. Absorbing (and reporting as consumed) the leftover
                // of a fling keeps the scrollable's decay animation alive for its
                // full duration with the zoom held at max — it reads as the pull
                // sticking, then snapping back late. Let the fling end at the
                // edge instead; onPreFling below does the spring.
                if (available.y == 0f || source != NestedScrollSource.UserInput) return Offset.Zero
                // Accumulate the RAW finger distance; the resistance lives entirely
                // in rubberBand, which starts 1:1 and stiffens as it goes rather
                // than starting at half speed and clamping.
                rawPull.floatValue += available.y
                return available
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (rawPull.floatValue != 0f) {
                    // Critically damped: iOS returns and stops dead, it does not
                    // wobble at the end, and a wobbling hero zoom is the tell.
                    //
                    // Tuning is shared with the plain-list bounce rather than set
                    // here. This used to be Spring.StiffnessMedium (1500), roughly
                    // 2.5x faster than the list bounce's 247 -- so the ~25 hero
                    // screens (album, artist, charts, explore, history, library,
                    // every playlist) snapped back on visibly different timing from
                    // every other scroller in the app. It also seeded the spring
                    // with NO velocity at all while consuming the whole fling, so a
                    // hard flick into the top edge produced no overshoot whatsoever.
                    val seedVelocity = bounceSeedVelocity(available.y)
                    animate(
                        initialValue = rawPull.floatValue,
                        targetValue = 0f,
                        initialVelocity = seedVelocity,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = bounceStiffnessFor(seedVelocity),
                        ),
                    ) { value, _ -> rawPull.floatValue = value }
                    return available
                }
                return Velocity.Zero
            }

            /**
             * The bounce for a flick that reaches an edge under its own momentum.
             *
             * This connection had no `onPostFling` at all, which is why hero screens
             * (artist, album, every playlist, charts, explore, history, library) never
             * overshot: [onPreFling] only springs when a stretch already exists, and a
             * pure fling never builds one — [onPostScroll] ignores everything that is
             * not `UserInput`, precisely so the scrollable's decay ends at the edge
             * instead of being held at full stretch for its whole duration. So the
             * leftover velocity arrived here and was dropped on the floor. Plain lists
             * bounced anyway because the ambient effect owns their fling; these lists
             * hand it a null effect (see [listOverscroll]) and own it themselves.
             *
             * Same spring and same seeding as [onPreFling] and the plain-list bounce,
             * so a flick into an edge settles on identical timing everywhere.
             */
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (available.y == 0f) return Velocity.Zero
                val seedVelocity = bounceSeedVelocity(available.y)
                animate(
                    initialValue = rawPull.floatValue,
                    targetValue = 0f,
                    initialVelocity = seedVelocity,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = bounceStiffnessFor(seedVelocity),
                    ),
                ) { value, _ -> rawPull.floatValue = value }
                return available
            }
        }
    }
    // Translate the WHOLE list down by the pull so the header and the content
    // below move as one unit (iOS stretch), not just the image scaling in place.
    this
        .onSizeChanged { viewport.floatValue = it.height.toFloat() }
        .graphicsLayer { translationY = zoom.offset }
        .nestedScroll(connection)
}

/**
 * [basicMarquee] that only runs while the text is actually on screen.
 *
 * A plain `Modifier.basicMarquee()` starts an infinite animation the moment it is
 * composed and never stops. In a lazy list that means every row Compose keeps
 * around — including the ones scrolled out of view and the ones prefetched ahead
 * of the viewport — drives an animation frame forever, so the whole app redraws
 * every vsync with nothing visible moving. Gating on real visibility keeps the
 * effect where the user can see it and costs one bounds check per layout pass.
 *
 * Visibility is read from the node's bounds in the window rather than a lazy
 * list's item info, so this works in any container: grids, rows, the player, a
 * plain Column.
 */
@Composable
fun Modifier.marqueeWhenVisible(): Modifier {
    var visible by remember { mutableStateOf(false) }
    return this
        .onGloballyPositioned { coordinates ->
            // boundsInWindow() collapses to an empty rect once the node is fully
            // clipped by an ancestor, which is exactly "scrolled out of sight".
            val onScreen = coordinates.isAttached && !coordinates.boundsInWindow().isEmpty
            if (onScreen != visible) visible = onScreen
        }
        .then(if (visible) Modifier.basicMarquee() else Modifier)
}
