/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
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

/**
 * Fraction of unconsumed fling velocity handed to the spring-back.
 *
 * Was 0.4f, which threw away 60% of a flick straight into an edge: the bounce read
 * as a muted nudge where Miuix (and iOS) launch a real overshoot. Miuix's
 * `startSpringAnimation(velocity)` seeds its spring with the FULL leftover velocity,
 * so match that.
 */
internal const val FlingBounceScale = 1f

/**
 * Ceiling on the velocity seeded into the bounce spring.
 *
 * Was 4000f -- below Miuix's own HIGH_VELOCITY_THRESHOLD of 5000, so every fling
 * fast enough to deserve the big bounce was clamped to a smaller one first. Raised
 * clear of that threshold; the spring is critically damped, so a large seed
 * overshoots and settles rather than oscillating.
 */
internal const val MaxBounceVelocity = 10_000f

/**
 * Critically-damped spring stiffness for the release bounce-back, equivalent to
 * a ~0.4s natural period (stiffness = (2*pi/period)^2) -- matches real UIScrollView
 * bounce timing (~300-500ms to settle) far more closely than a generic Compose
 * stiffness preset does. Identical to Miuix's SpringMath.STANDARD_SPRING_PERIOD.
 */
internal const val BounceSpringStiffness = 247f

/**
 * Miuix's SpringMath.HIGH_VELOCITY_THRESHOLD: above this the settle switches to a
 * longer natural period, so a hard flick decelerates over a longer, softer arc
 * instead of snapping back on the same timing as a gentle release. We had no
 * velocity-adaptive period at all, which is why fast flings felt abrupt next to
 * theirs.
 */
internal const val HighVelocityThreshold = 5000f

/**
 * Stiffness for [HighVelocityThreshold]-and-above releases: Miuix's
 * SLOWER_SPRING_PERIOD_FOR_HIGH_VELOCITY of 0.55s -> (2*pi/0.55)^2 ~= 130.
 */
internal const val HighVelocityBounceStiffness = 130f

/**
 * UIScrollView's rubber band curve: `(1 - 1/(d*c/dim + 1)) * dim/c`.
 *
 * [rawDistance] is how far the finger has actually travelled past the edge; the
 * result is how far the content is allowed to move. Near zero it is 1:1 with the
 * finger (iOS feels immediate, never mushy), and it asymptotes toward `dim/c`, so
 * it self-limits instead of needing an arbitrary hard cap that the pull slams
 * into.
 *
 * Shared with [heroPullZoom] so the hero screens' pull and the plain list bounce
 * are literally the same curve.
 */
internal fun rubberBand(rawDistance: Float, containerPx: Float): Float {
    val dim = if (containerPx > 0f) containerPx else FallbackContainerPx
    val d = abs(rawDistance)
    val banded = (1f - 1f / (d * RubberBandConstant / dim + 1f)) * dim / RubberBandConstant
    return banded * sign(rawDistance)
}

/**
 * iOS-style rubber-band overscroll, expressed as an [OverscrollEffect] so it can be
 * installed once via `LocalOverscrollFactory` instead of being applied per scroll
 * container. Every LazyColumn / LazyGrid / scrollable Column under the provider
 * bounces, including ones in screens nobody remembered to update.
 *
 * Replaces Android's stretch/glow edge effect while it is provided.
 *
 * State is the RAW distance dragged past the edge, held in a plain mutable float
 * mutated synchronously in [applyToScroll] — not an
 * [androidx.compose.animation.core.Animatable] — because `applyToScroll` isn't
 * suspend and every scroll delta during a drag used to launch its own
 * fire-and-forget `snapTo` coroutine. Under a fast drag that's dozens of coroutines
 * racing each other per second, and the offset could end up stuck on a stale value
 * from a coroutine that hadn't run yet. A coroutine (and a real spring) is only
 * needed once, on release, to animate back to rest.
 */
class IosOverscrollEffect : OverscrollEffect {

    /**
     * Raw finger distance past the edge on each axis; the drawn offset is [rubberBand]
     * of it.
     *
     * Both axes, independently. This used to track Y only, so every horizontal scroller
     * in the app — Home's carousels, the tab pager, the queue — hit its edge and simply
     * stopped dead while vertical lists bounced. Nothing about the curve differs per
     * axis; only the container dimension it scales against does.
     */
    private val rawPullX = mutableFloatStateOf(0f)
    private val rawPullY = mutableFloatStateOf(0f)

    /** Measured by [IosOverscrollNode]; the rubber band scales with it, as on iOS. */
    private var containerWidthPx = 0f
    private var containerHeightPx = 0f

    private val drawOffsetX: Float
        get() = rubberBand(rawPullX.floatValue, containerWidthPx)

    private val drawOffsetY: Float
        get() = rubberBand(rawPullY.floatValue, containerHeightPx)

    override val isInProgress: Boolean
        get() = rawPullX.floatValue != 0f || rawPullY.floatValue != 0f

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        // Dragging back toward rest pays down the existing stretch before the
        // list itself gets to scroll — otherwise the content jumps. Paying down
        // happens in raw (finger) space, so the return trip is damped by exactly
        // the same curve as the outbound pull, which is what makes the stretch
        // track the finger symmetrically.
        val payDownX = payDown(rawPullX, delta.x)
        val payDownY = payDown(rawPullY, delta.y)

        val remaining = Offset(delta.x - payDownX, delta.y - payDownY)
        val consumedByScroll = performScroll(remaining)
        val leftover = remaining - consumedByScroll

        // Leftover means the list hit an edge, so stretch. Drag only: absorbing
        // leftover during a fling reported the whole delta back as consumed, so
        // the scrollable's decay animation never saw an edge and ran its full
        // duration with the stretch pinned at maximum — the bounce appeared to
        // hang, then finally sprang back once the decay expired. Returning
        // nothing consumed makes the decay cancel at once and hand its remaining
        // velocity to applyToFling, which is where the bounce belongs.
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

    /**
     * Spend [delta] reducing an existing stretch on one axis, returning how much of it
     * was used. Never overshoots past rest: a delta larger than the current stretch
     * settles at zero and hands the remainder back for the list to scroll with.
     */
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
        val consumed = performFling(velocity)
        val leftoverX = velocity.x - consumed.x
        val leftoverY = velocity.y - consumed.y
        if (rawPullX.floatValue == 0f && rawPullY.floatValue == 0f &&
            leftoverX == 0f && leftoverY == 0f
        ) {
            return
        }

        // Critically damped, not springy. iOS snaps back and stops dead; a
        // dampingRatio below 1 wobbles at the end, which is the single thing that
        // most makes a rubber band read as "Android imitating iOS".
        //
        // A new drag beats this: the scrollable cancels this suspend fling (and
        // this animate call with it) as soon as the next pointer-down starts a
        // fresh drag, so there's no coroutine to race against.
        //
        // Velocity the list could not consume (a flick straight into an
        // already-reached edge) seeds the spring, so it overshoots into a real
        // bounce instead of just easing a static stretch back to rest. At rest
        // the rubber band is 1:1, so fling velocity needs no curve conversion.
        //
        // Stiffness: Spring.StiffnessMedium (1500) settles roughly 2.5x FASTER
        // than real UIScrollView bounce timing -- checked against Miuix's own
        // overscroll spring (critically damped too, but tuned to a 0.4s natural
        // period, equivalent stiffness ~247) and against the ~300-500ms a real
        // iOS bounce actually takes. A too-stiff critically-damped spring doesn't
        // wobble, but it does snap back hard and fast, which read as "less
        // smooth" than a slower settle in an otherwise-identical curve.
        // Both axes settle together. A gesture is overwhelmingly one or the other, so in
        // practice one of these is already at rest and returns immediately; running them
        // concurrently is what keeps a genuinely diagonal fling from settling in two
        // visible stages.
        coroutineScope {
            launch { settleAxis(rawPullX, leftoverX) }
            launch { settleAxis(rawPullY, leftoverY) }
        }
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

    override val node: DelegatableNode = IosOverscrollNode(
        offsetX = { drawOffsetX },
        offsetY = { drawOffsetY },
        onMeasured = { width, height ->
            containerWidthPx = width
            containerHeightPx = height
        },
    )
}

private class IosOverscrollNode(
    private val offsetX: () -> Float,
    private val offsetY: () -> Float,
    private val onMeasured: (width: Float, height: Float) -> Unit,
) : Modifier.Node(), LayoutModifierNode {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        // The rubber band is scaled by the scroll container's own size on iOS —
        // a short list resists sooner than a full-screen one. Each axis scales
        // against its own dimension, so a wide carousel and a tall list feel the
        // same rather than one resisting far sooner than the other.
        onMeasured(
            (if (constraints.hasBoundedWidth) constraints.maxWidth else placeable.width).toFloat(),
            (if (constraints.hasBoundedHeight) constraints.maxHeight else placeable.height).toFloat(),
        )
        return layout(placeable.width, placeable.height) {
            // Read the offsets inside the layer block so the bounce animates in the
            // draw phase, without re-laying-out the list every frame.
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


/**
 * Clamp leftover fling velocity into the range the bounce spring is seeded with.
 * Shared by the plain-list [IosOverscrollEffect] and the hero-header pull in
 * `MotionModifiers.heroPullZoom`, which previously used unrelated tuning.
 */
internal fun bounceSeedVelocity(leftoverVelocity: Float): Float =
    (leftoverVelocity * FlingBounceScale).coerceIn(-MaxBounceVelocity, MaxBounceVelocity)

/**
 * Velocity-adaptive natural period, copied from Miuix's `SpringEngine.start`: a hard
 * flick gets the longer 0.55s arc, everything else the standard 0.4s. Without this
 * every release settled on the same timing, which made fast flings read as abrupt
 * next to Miuix's overscroll.
 */
internal fun bounceStiffnessFor(seedVelocity: Float): Float =
    if (abs(seedVelocity) > HighVelocityThreshold) {
        HighVelocityBounceStiffness
    } else {
        BounceSpringStiffness
    }

/**
 * A snapping [FlingBehavior] that steps aside once the list has nothing left to scroll.
 *
 * `rememberSnapFlingBehavior` always reports the whole fling as consumed — it has to,
 * because it animates to a snap position itself. At an edge there is no snap position
 * left to animate to, so it consumes the velocity and stops dead, and
 * [IosOverscrollEffect.applyToFling] is handed `leftover = 0`: nothing to seed the
 * bounce spring with, hence no overshoot. Every snapping carousel in the app (Home's
 * quick picks, Charts, Explore, the player's thumbnail strip) was therefore incapable
 * of bouncing, no matter how hard it was flicked.
 *
 * At an edge this returns the velocity unconsumed so the overscroll effect gets it and
 * bounces; everywhere else it is exactly the snapping behaviour it wraps.
 */
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
