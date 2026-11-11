/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * UIKit's rubber band constant. Apple's own value; lower is stiffer.
 */
private const val RubberBandConstant = 0.55f

/**
 * Fallback container height (px) for the one frame before the node has measured.
 */
private const val FallbackContainerPx = 2000f

/** Fraction of unconsumed fling velocity used to seed the return spring. */
internal const val FlingBounceScale = 1f

/** Maximum velocity passed to the return spring. */
internal const val MaxBounceVelocity = 10_000f

/** Stiffness for the standard critically damped return spring. */
internal const val BounceSpringStiffness = 247f

/** Releases above this velocity use the slower return spring. */
internal const val HighVelocityThreshold = 5000f

/** Stiffness used for high-velocity releases. */
internal const val HighVelocityBounceStiffness = 130f

/** Applies the rubber-band curve shared by list overscroll and [heroPullZoom]. */
internal fun rubberBand(rawDistance: Float, containerPx: Float): Float {
    val dim = if (containerPx > 0f) containerPx else FallbackContainerPx
    val d = abs(rawDistance)
    val banded = (1f - 1f / (d * RubberBandConstant / dim + 1f)) * dim / RubberBandConstant
    return banded * sign(rawDistance)
}

/**
 * App-wide rubber-band overscroll. Drag state updates synchronously because
 * [applyToScroll] is not suspend; a coroutine runs only to settle after release.
 */
class IosOverscrollEffect : OverscrollEffect {

    /** Raw overscroll distance for each axis; drawing applies [rubberBand]. */
    private val rawPullX = mutableFloatStateOf(0f)
    private val rawPullY = mutableFloatStateOf(0f)

    private val returnPullX = mutableFloatStateOf(0f)
    private val returnPullY = mutableFloatStateOf(0f)

    /** Measured by [IosOverscrollNode]; the rubber band scales with it, as on iOS. */
    private var containerWidthPx = 0f
    private var containerHeightPx = 0f

    private var gestureGeneration = 0L
    private var reboundGeneration = 0L
    private var reboundJob: Job? = null

    private val drawOffsetX: Float
        get() = rubberBand(rawPullX.floatValue + returnPullX.floatValue, containerWidthPx)

    private val drawOffsetY: Float
        get() = rubberBand(rawPullY.floatValue + returnPullY.floatValue, containerHeightPx)

    // A visual return must not make Scrollable consume the next tap as a drag.
    override val isInProgress: Boolean
        get() = rawPullX.floatValue != 0f || rawPullY.floatValue != 0f

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        if (source == NestedScrollSource.UserInput) {
            gestureGeneration++
            resumeReturnIfNeeded()
        }
        // Remove existing stretch before scrolling the list, using the same raw-distance curve.
        val payDownX = payDown(rawPullX, delta.x)
        val payDownY = payDown(rawPullY, delta.y)

        val remaining = Offset(delta.x - payDownX, delta.y - payDownY)
        val consumedByScroll = performScroll(remaining)
        val leftover = remaining - consumedByScroll

        // Stretch during drags only. Leave fling deltas unconsumed so the scrollable
        // can pass its remaining velocity to [applyToFling].
        var stretchX = 0f
        var stretchY = 0f
        if (source == NestedScrollSource.UserInput) {
            if (leftover.x != 0f) {
                rawPullX.floatValue += leftover.x
                stretchX = leftover.x
            }
            if (leftover.y != 0f) {
                rawPullY.floatValue += leftover.y
                stretchY = leftover.y
            }
        }

        return Offset(
            consumedByScroll.x + payDownX + stretchX,
            consumedByScroll.y + payDownY + stretchY,
        )
    }

    /** Reduces existing stretch and returns the unconsumed delta to the list. */
    private fun payDown(pull: androidx.compose.runtime.MutableFloatState, delta: Float): Float {
        val current = pull.floatValue
        if (current == 0f || delta == 0f || sign(delta) == sign(current)) return 0f
        val target = current + delta
        val settled = if (sign(target) != sign(current)) 0f else target
        pull.floatValue = settled
        return settled - current
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity,
    ) {
        val releasedGesture = gestureGeneration
        val consumed = performFling(velocity)
        // A cancelled fling can finish after a newer gesture has started.
        if (releasedGesture != gestureGeneration || !overscrollNode.isAttached) return
        val leftoverX = velocity.x - consumed.x
        val leftoverY = velocity.y - consumed.y
        if (rawPullX.floatValue == 0f && rawPullY.floatValue == 0f &&
            returnPullX.floatValue == 0f && returnPullY.floatValue == 0f &&
            leftoverX == 0f && leftoverY == 0f
        ) {
            return
        }

        startRebound(leftoverX, leftoverY)
    }

    private fun startRebound(velocityX: Float, velocityY: Float) {
        if (!overscrollNode.isAttached) return
        cancelRebound()
        releaseActivePull()
        val rebound = reboundGeneration
        reboundJob = overscrollNode.coroutineScope.launch {
            try {
                coroutineScope {
                    launch { settleAxis(returnPullX, velocityX) }
                    launch { settleAxis(returnPullY, velocityY) }
                }
            } finally {
                if (rebound == reboundGeneration) {
                    returnPullX.floatValue = 0f
                    returnPullY.floatValue = 0f
                    reboundJob = null
                }
            }
        }
    }

    private fun cancelRebound() {
        reboundGeneration++
        reboundJob?.cancel()
        reboundJob = null
    }

    private fun releaseActivePull() {
        returnPullX.floatValue += rawPullX.floatValue
        returnPullY.floatValue += rawPullY.floatValue
        rawPullX.floatValue = 0f
        rawPullY.floatValue = 0f
    }

    private fun pauseReturnForTouch() {
        gestureGeneration++
        cancelRebound()
        // Preserve the touched row's position until the gesture becomes a drag.
        releaseActivePull()
    }

    private fun resumeReturnIfNeeded() {
        if (reboundJob == null &&
            (returnPullX.floatValue != 0f || returnPullY.floatValue != 0f)
        ) startRebound(0f, 0f)
    }

    private fun finishTouch() {
        if (rawPullX.floatValue == 0f && rawPullY.floatValue == 0f) {
            resumeReturnIfNeeded()
        }
    }

    private fun reset() {
        gestureGeneration++
        cancelRebound()
        rawPullX.floatValue = 0f
        rawPullY.floatValue = 0f
        returnPullX.floatValue = 0f
        returnPullY.floatValue = 0f
    }

    private suspend fun settleAxis(
        pull: androidx.compose.runtime.MutableFloatState,
        leftoverVelocity: Float,
    ) {
        if (pull.floatValue == 0f && leftoverVelocity == 0f) return
        val seedVelocity = bounceSeedVelocity(leftoverVelocity)
        animate(
            initialValue = pull.floatValue,
            targetValue = 0f,
            initialVelocity = seedVelocity,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = bounceStiffnessFor(seedVelocity),
            ),
        ) { value, _ -> pull.floatValue = value }
    }

    private val overscrollNode = IosOverscrollNode(
        offsetX = { drawOffsetX },
        offsetY = { drawOffsetY },
        onMeasured = { width, height ->
            containerWidthPx = width
            containerHeightPx = height
        },
        onTouchDown = ::pauseReturnForTouch,
        onTouchUp = ::finishTouch,
        onReset = ::reset,
    )

    override val node: DelegatableNode = overscrollNode
}

private class IosOverscrollNode(
    private val offsetX: () -> Float,
    private val offsetY: () -> Float,
    private val onMeasured: (width: Float, height: Float) -> Unit,
    private val onTouchDown: () -> Unit,
    private val onTouchUp: () -> Unit,
    private val onReset: () -> Unit,
) : Modifier.Node(), LayoutModifierNode, PointerInputModifierNode {
    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        if (pass != PointerEventPass.Initial) return
        if (
            pointerEvent.changes.none { it.previousPressed } &&
            pointerEvent.changes.any { it.changedToDownIgnoreConsumed() }
        ) {
            onTouchDown()
        } else if (pointerEvent.changes.none { it.pressed } &&
            pointerEvent.changes.any { it.previousPressed }
        ) {
            onTouchUp()
        }
    }

    override fun onCancelPointerInput() = onReset()

    override fun onDetach() = onReset()

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        // Scale each axis against its container dimension.
        onMeasured(
            (if (constraints.hasBoundedWidth) constraints.maxWidth else placeable.width).toFloat(),
            (if (constraints.hasBoundedHeight) constraints.maxHeight else placeable.height).toFloat(),
        )
        return layout(placeable.width, placeable.height) {
            // Read offsets during drawing to avoid relayout on every animation frame.
            placeable.placeWithLayer(0, 0) {
                translationX = offsetX()
                translationY = offsetY()
            }
        }
    }
}

private class IosOverscrollFactory(
    private val density: Density,
    private val scope: CoroutineScope,
) : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = IosOverscrollEffect()

    override fun equals(other: Any?): Boolean =
        other is IosOverscrollFactory && other.density == density && other.scope === scope

    override fun hashCode(): Int = 31 * density.hashCode() + scope.hashCode()
}

/** Factory to hand to `LocalOverscrollFactory` to make the whole app bounce. */
@Composable
fun rememberIosOverscrollFactory(): OverscrollFactory {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return remember(density, scope) { IosOverscrollFactory(density, scope) }
}


/** Clamps fling velocity for the list bounce and hero pull spring. */
internal fun bounceSeedVelocity(leftoverVelocity: Float): Float =
    (leftoverVelocity * FlingBounceScale).coerceIn(-MaxBounceVelocity, MaxBounceVelocity)

/** Selects the return spring based on fling velocity. */
internal fun bounceStiffnessFor(seedVelocity: Float): Float =
    if (abs(seedVelocity) > HighVelocityThreshold) {
        HighVelocityBounceStiffness
    } else {
        BounceSpringStiffness
    }

/** Lets overscroll handle flings that reach an edge; otherwise keeps normal snapping. */
private class EdgeAwareFlingBehavior(
    private val state: ScrollableState,
    private val delegate: FlingBehavior,
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // Negative velocity scrolls forward (content moves toward the start), so the
        // relevant edge is whichever direction this fling is actually heading.
        val atEdge = if (initialVelocity < 0f) {
            !state.canScrollForward
        } else {
            !state.canScrollBackward
        }
        if (atEdge) return initialVelocity
        return with(delegate) { performFling(initialVelocity) }
    }
}

/** [snap], but a fling into an edge is handed to the overscroll bounce instead of eaten. */
@Composable
fun rememberEdgeAwareFlingBehavior(
    state: ScrollableState,
    snap: FlingBehavior,
): FlingBehavior = remember(state, snap) { EdgeAwareFlingBehavior(state, snap) }
