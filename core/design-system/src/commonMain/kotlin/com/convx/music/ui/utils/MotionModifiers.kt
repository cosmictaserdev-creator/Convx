/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.math.sign

/** Current pull and visual recovery for a hero list's rubber-band movement. */
@Stable
class HeroZoom internal constructor(
    internal val rawPull: MutableFloatState,
    internal val returnPull: MutableFloatState,
    /** Viewport height in pixels. */
    internal val viewport: MutableFloatState,
    val maxPull: Float,
    val enabled: Boolean,
) {
    /** List translation in pixels. */
    val offset: Float
        get() = if (enabled) {
            rubberBand(rawPull.floatValue + returnPull.floatValue, viewport.floatValue)
        } else 0f

    /** Only pulling past the top grows the artwork. */
    val scale: Float
        get() = if (enabled) 1f + (offset / maxPull).coerceIn(0f, 1f) * 0.18f else 1f
}

@Composable
fun rememberHeroZoom(enabled: Boolean, maxPull: Dp = 220.dp): HeroZoom {
    val rawPull = remember { mutableFloatStateOf(0f) }
    val returnPull = remember { mutableFloatStateOf(0f) }
    val viewport = remember { mutableFloatStateOf(0f) }
    val maxPullPx = with(LocalDensity.current) { maxPull.toPx() }
    return remember(rawPull, returnPull, viewport, maxPullPx, enabled) {
        HeroZoom(rawPull, returnPull, viewport, maxPullPx, enabled)
    }
}

/** [heroPullZoom] owns both edges when enabled; otherwise use the ambient effect. */
@Composable
fun HeroZoom.listOverscroll(): OverscrollEffect? =
    if (enabled) null else rememberOverscrollEffect()

/** Moves the list with the pull and drives [HeroZoom.scale]; pulling does not refresh. */
fun Modifier.heroPullZoom(
    zoom: HeroZoom,
): Modifier = composed {
    if (!zoom.enabled) return@composed this

    val rawPull = zoom.rawPull
    val viewport = zoom.viewport
    val scope = rememberCoroutineScope()
    val connection = remember(zoom, scope) { HeroPullConnection(rawPull, zoom.returnPull, scope) }
    DisposableEffect(connection) {
        onDispose { connection.dispose() }
    }
    this
        .onSizeChanged { viewport.floatValue = it.height.toFloat() }
        .graphicsLayer { translationY = zoom.offset }
        .pointerInput(connection) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                connection.pauseReturnForTouch()
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
                connection.finishTouch()
            }
        }
        .nestedScroll(connection)
}

private class HeroPullConnection(
    private val rawPull: MutableFloatState,
    private val returnPull: MutableFloatState,
    private val scope: CoroutineScope,
) : NestedScrollConnection {
    private var gestureGeneration = 0L
    private var releasedGesture = 0L
    private var releasedFling: Job? = null
    private var reboundGeneration = 0L
    private var reboundJob: Job? = null
    private var disposed = false

    fun pauseReturnForTouch() {
        gestureGeneration++
        releasedFling = null
        cancelRebound()
        releaseActivePull()
    }

    fun finishTouch() {
        // Release always springs back, even when no fling is dispatched after the touch.
        if (rawPull.floatValue != 0f) startRebound(0f) else resumeReturnIfNeeded()
    }

    fun dispose() {
        disposed = true
        gestureGeneration++
        releasedFling = null
        cancelRebound()
        rawPull.floatValue = 0f
        returnPull.floatValue = 0f
    }

    private fun releaseActivePull() {
        returnPull.floatValue += rawPull.floatValue
        rawPull.floatValue = 0f
    }

    private fun resumeReturnIfNeeded() {
        if (reboundJob == null && returnPull.floatValue != 0f) startRebound(0f)
    }

    private fun cancelRebound() {
        reboundGeneration++
        reboundJob?.cancel()
        reboundJob = null
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source == NestedScrollSource.UserInput) {
            gestureGeneration++
            resumeReturnIfNeeded()
        }
        val current = rawPull.floatValue
        if (current != 0f && available.y != 0f && sign(available.y) != sign(current)) {
            val target = current + available.y
            val settled = if (sign(target) != sign(current)) 0f else target
            rawPull.floatValue = settled
            return Offset(0f, settled - current)
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (available.y == 0f || source != NestedScrollSource.UserInput) return Offset.Zero
        // Top edge only: a bottom pull dragged the whole list and left it stuck.
        if (available.y < 0f && rawPull.floatValue <= 0f) return Offset.Zero
        rawPull.floatValue += available.y
        return Offset(0f, available.y)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        releasedGesture = gestureGeneration
        releasedFling = currentCoroutineContext()[Job]
        if (rawPull.floatValue == 0f) return Velocity.Zero
        startRebound(available.y)
        return Velocity(0f, available.y)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        // A cancelled fling can finish after the user has touched down again.
        if (disposed || releasedFling !== currentCoroutineContext()[Job] ||
            releasedGesture != gestureGeneration
        ) return Velocity.Zero
        releasedFling = null
        if (available.y == 0f) return Velocity.Zero
        startRebound(available.y)
        return Velocity(0f, available.y)
    }

    private fun startRebound(leftoverVelocity: Float) {
        if (disposed) return
        cancelRebound()
        releaseActivePull()
        val rebound = reboundGeneration
        reboundJob = scope.launch {
            try {
                val seedVelocity = bounceSeedVelocity(leftoverVelocity)
                animate(
                    initialValue = returnPull.floatValue,
                    targetValue = 0f,
                    initialVelocity = seedVelocity,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = bounceStiffnessFor(seedVelocity),
                    ),
                ) { value, _ -> returnPull.floatValue = value }
            } finally {
                if (rebound == reboundGeneration) {
                    returnPull.floatValue = 0f
                    reboundJob = null
                }
            }
        }
    }
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
