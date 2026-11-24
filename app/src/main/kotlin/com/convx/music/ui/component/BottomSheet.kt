/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.convx.music.ui.player.PlayerMorph
import com.convx.music.ui.player.SCREEN_CORNER_EXPANSION_MILLIS
import com.convx.music.ui.player.recordPlayerLayer
import com.convx.music.ui.player.sharedContainerCornerRadius
import com.convx.music.playback.presentation.PLAYER_MORPH_CONTENT_APPEAR_END
import com.convx.music.playback.presentation.PLAYER_MORPH_CONTENT_APPEAR_START
import com.convx.music.playback.presentation.PLAYER_MORPH_LAYER_HANDOFF_PROGRESS
import com.convx.music.playback.presentation.PLAYER_MORPH_MINI_FADE_END
import com.convx.music.designsystem.MotionTokens
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.convx.music.constants.NavigationBarAnimationSpec
import com.convx.music.ui.utils.nearestProjectedSnap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow

/** Progress where the mini and full-player layers hand off during the morph. */
internal const val PLAYER_LAYER_HANDOFF_PROGRESS = PLAYER_MORPH_LAYER_HANDOFF_PROGRESS

/** Progress where mini-player content is fully faded out. */
internal const val PLAYER_MINI_FADE_END = PLAYER_MORPH_MINI_FADE_END

/** Progress where full-player content starts appearing. */
internal const val PLAYER_CONTENT_APPEAR_START = PLAYER_MORPH_CONTENT_APPEAR_START

/** Progress where full-player content is fully visible. */
internal const val PLAYER_CONTENT_APPEAR_END = PLAYER_MORPH_CONTENT_APPEAR_END

/** Q3 lock: backdrop sampling stays frozen through the morph, going live here so
 *  the first capture after the glass fades in is warm, not a stale frame. */
internal const val PLAYER_GLASS_UNFREEZE_PROGRESS = 0.75f

/** Spring stiffness for opening and closing the player sheet. */
private const val PlayerSheetStiffness = MotionTokens.PlayerSheetStiffness

private const val BackdropFreezeSafetyNs = 900_000_000L

/** Close spring damping when the sheet lands into the mini pill with speed. */
private const val COLLAPSE_LANDING_DAMPING = MotionTokens.PlayerSheetLandingDamping

/** How far (of the collapse travel) a full predictive-back swipe previews. */
private const val PREDICTIVE_BACK_TRAVEL = 0.35f

/** Diagnostic transition slowdown; keep at 1f for normal playback. */
private const val DIAG_SLOWDOWN = 1f

/**
 * Draggable sheet adapted from [ViMusic](https://github.com/vfsfitvnm/ViMusic).
 */
@Composable
fun BottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    background: @Composable (BoxScope.() -> Unit) = { },
    onDismiss: (() -> Unit)? = null,
    collapsedContent: @Composable BoxScope.() -> Unit,
    isExpandable: Boolean = true,
    /** Caps the expanded content to this width, centered, so the controls read as one
     *  phone-shaped panel on a wide screen. [background] stays full-bleed regardless;
     *  [Dp.Unspecified] (default) lets content fill the sheet exactly as before. */
    contentMaxWidth: Dp = Dp.Unspecified,
    /** Radius the sheet rounds to at full size — the player passes the device's physical
     *  screen radius so the corners land on the glass. */
    expandedCornerRadius: Dp = 0.dp,
    /** Corner radius while collapsed. */
    collapsedCornerRadius: Dp = 16.dp,
    /** Drawn as a sibling, NOT nested — the sheet applies its own `translationY` during
     *  drag, and this overlay needs plain, unshifted root/window coordinates. Only the
     *  player passes one, for the mini-to-full artwork morph. */
    overlayContent: (@Composable BoxScope.() -> Unit)? = null,
    /** Hands this sheet's drawing to the shared container morph ([PlayerMorph]): while it
     *  runs, the sheet is recorded and not drawn where it sits, and it stops translating.
     *  Only the player passes this. */
    morphEnabled: Boolean = false,
    /** False when the host drives back itself (predictive-back queue pane). */
    handleBack: Boolean = true,
    /** Keep [content] composed while collapsed once opened — the morph then runs over a built
     *  page, not one composing its first frame. Measured but not placed: nothing draws, takes
     *  input or reports semantics. */
    keepContentComposed: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    var hasBeenShown by remember { mutableStateOf(false) }
    if (!state.isCollapsed) hasBeenShown = true
    // Compose the player shortly after launch so its first opening morph is ready.
    if (keepContentComposed) {
        LaunchedEffect(Unit) {
            // Wait until startup settles, before the user is likely to open the player.
            kotlinx.coroutines.delay(600)
            hasBeenShown = true
        }
    }

    // Square the corners after the sheet reaches full size.
    val cornerExpansion = remember { Animatable(0f) }
    LaunchedEffect(state.isExpanded) {
        cornerExpansion.animateTo(
            targetValue = if (state.isExpanded) 1f else 0f,
            // Avoid overshoot, which can briefly produce an invalid corner radius.
            animationSpec = spring(dampingRatio = 1f, stiffness = 550f),
        )
    }

    // Record background and content separately so the morph can fade them at different times.
    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = if (morphEnabled && PlayerMorph.active) {
                    // Keep the recorded background opaque; the overlay controls its fade.
                    1f
                } else {
                    (1.4f * (state.progress.coerceAtLeast(0.1f) - 0.1f).pow(0.5f)).coerceIn(0f, 1f)
                }
            }
            .fillMaxSize()
            .then(
                if (morphEnabled) {
                    Modifier.recordPlayerLayer(
                        layer = { PlayerMorph.fullBackgroundLayer },
                        drawInPlace = { !PlayerMorph.active },
                        markReady = { PlayerMorph.fullBackgroundFrameReady = true },
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        Box(
            // Always full-bleed — see contentMaxWidth's doc above.
            modifier = Modifier.fillMaxSize(),
            content = background,
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                // The overlay places the recorded sheet, so do not apply its drag offset again.
                translationY = if (morphEnabled && PlayerMorph.active) {
                    0f
                } else {
                    (state.expandedBound - state.value).toPx()
                }
            }
            .pointerInput(state, isExpandable) {
                if (!isExpandable) return@pointerInput
                val velocityTracker = VelocityTracker()

                detectVerticalDragGestures(
                    onDragStart = { state.onDragStart(fromMiniPlayer = false) },
                    onVerticalDrag = { change, dragAmount ->
                        state.dragClockNs[0] = System.nanoTime()
                        velocityTracker.addPointerInputChange(change)
                        state.dispatchRawDelta(dragAmount)
                    },
                    onDragCancel = {
                        state.dragClockNs[0] = 0L
                        velocityTracker.resetTracking()
                        // Settle to the nearest anchor: snapping shut here slammed the player
                        // closed in one frame whenever the system took the pointer.
                        state.performFling(0f, onDismiss = null)
                    },
                    onDragEnd = {
                        state.dragClockNs[0] = 0L
                        val velocity = -velocityTracker.calculateVelocity().y
                        velocityTracker.resetTracking()
                        state.performFling(velocity, onDismiss)
                    }
                )
            }
            .graphicsLayer {
                // Interpolate the corner radius during dragging instead of switching abruptly.
                val cornerRadius = if (morphEnabled && PlayerMorph.active) {
                    // The overlay applies rounding to the drawn bounds.
                    0f
                } else {
                    sharedContainerCornerRadius(
                        collapsedCornerRadius = collapsedCornerRadius.toPx(),
                        expandedCornerRadius = expandedCornerRadius.toPx(),
                        progress = state.progress,
                        screenCornerExpansionProgress = cornerExpansion.value,
                    )
                }
                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
                clip = true
            }
    ) {
        if (handleBack && !state.isCollapsed && !state.isDismissed) {
            // Predictive back previews collapse; release commits and cancellation restores the sheet.
            androidx.activity.compose.PredictiveBackHandler { events ->
                try {
                    events.collect { event -> state.previewBack(event.progress) }
                    state.collapseSoft()
                } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                    state.expandSoft()
                    throw e
                }
            }
        }

        if (!state.isCollapsed || (keepContentComposed && hasBeenShown)) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (state.isCollapsed) {
                        // Keep hidden content placed so its layers stay warm for the opening morph.
                            Modifier.clearAndSetSemantics {}
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (morphEnabled) {
                            Modifier.recordPlayerLayer(
                                layer = { PlayerMorph.fullContentLayer },
                                drawInPlace = { !PlayerMorph.active },
                                markReady = { PlayerMorph.fullContentFrameReady = true },
                            )
                        } else {
                            Modifier
                        },
                    )
                    .graphicsLayer {
                        // Start full-player content after mini content has faded out.
                        // The overlay controls the fade during the morph.
                        alpha = if (morphEnabled && PlayerMorph.active) {
                            1f
                        } else {
                            ((state.progress - PLAYER_LAYER_HANDOFF_PROGRESS) / 0.2f)
                                .coerceIn(0f, 1f)
                        }
                    },
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxHeight()
                        .then(
                            if (contentMaxWidth.isSpecified) {
                                Modifier.widthIn(max = contentMaxWidth)
                            } else {
                                Modifier.fillMaxWidth()
                            }
                        ),
                    content = content,
                )
            }
        }

        if (!state.isExpanded && (onDismiss == null || !state.isDismissed)) {
            Box(
                modifier =
                Modifier
                    .graphicsLayer {
                        // Fully gone exactly where the expanded content starts to arrive.
                        alpha = 1f - (state.progress / PLAYER_LAYER_HANDOFF_PROGRESS)
                            .coerceIn(0f, 1f)
                    }.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (isExpandable) state.expandSoft() },
                    ).fillMaxWidth()
                    .height(state.collapsedBound),
                content = collapsedContent,
            )
        }
    }

    // Keep the overlay outside the translated sheet so its root-relative bounds remain valid.
    if (overlayContent != null) {
        Box(modifier = Modifier.fillMaxSize(), content = overlayContent)
    }
}

@Stable
class BottomSheetState(
    draggableState: DraggableState,
    private val coroutineScope: CoroutineScope,
    private val animatable: Animatable<Dp, AnimationVector1D>,
    private val onAnchorChanged: (Int) -> Unit,
    val collapsedBound: Dp,
    val expandedBound: Dp,
    val dismissedBound: Dp,
    private val density: Density,
) : DraggableState by draggableState {
    // A plain array lets draw-phase glass reads avoid snapshot invalidations.
    internal val dragClockNs = longArrayOf(0L)

    /** True from drag start until the sheet settles. */
    var isDragging by mutableStateOf(false)
        private set

    /** Anchor the sheet is moving toward. */
    var targetOpen by mutableStateOf(false)
        private set

    /** Marks a gesture start and which end owns it; mirrored to [PlayerMorph]. */
    fun onDragStart(fromMiniPlayer: Boolean) {
        isDragging = true
        PlayerMorph.isDragging = true
        PlayerMorph.dragFromMiniPlayer = fromMiniPlayer
    }

    private fun endDragState() {
        isDragging = false
        PlayerMorph.isDragging = false
    }

    private fun publishTarget(open: Boolean) {
        targetOpen = open
        PlayerMorph.targetOpen = open
    }

    /** Pass to a `background` slot's [layerBackdrop] to skip re-recording it
     *  while this sheet is being dragged, mirroring [BackdropFreeze]. */
    val backdropFrozen: () -> Boolean = {
        val started = dragClockNs[0]
        (started != 0L && System.nanoTime() - started < BackdropFreezeSafetyNs) ||
            // Q3 lock: tap-driven open/close runs no drag, so without this the
            // backdrop re-samples every animation frame into the morph recording.
            PlayerMorph.glassFrozen
    }

    val value by animatable.asState()

    val isDismissed by derivedStateOf {
        value <= dismissedBound
    }

    val isCollapsed by derivedStateOf {
        value <= collapsedBound
    }

    val isExpanded by derivedStateOf {
        value >= expandedBound
    }

    val progress by derivedStateOf {
        val totalRange = (expandedBound - collapsedBound).value
        if (totalRange <= 0f) 0f else ((animatable.value - collapsedBound).value / totalRange).coerceIn(0f, 1f)
    }

    /**
     * Set by the player sheet: its collapse lands WITH speed (slightly underdamped, stopped
     * by the collapsed bound) and hands that speed (dp/s, negative = downward) here, so the
     * mini pill can carry it on as an overshoot dip below its slot and settle.
     */
    var onCollapseLanded: ((velocityDpPerSec: Float) -> Unit)? = null

    /**
     * true when a collapse starts, false when it ends WITHOUT landing (interrupted, or
     * already at rest). Lets the landing take over on the very frame the sheet reaches its
     * bound: flagged only from [onCollapseLanded], that frame had nobody drawing the card.
     */
    var onCollapseArmed: ((armed: Boolean) -> Unit)? = null
    private var collapseGeneration = 0

    fun collapse(animationSpec: AnimationSpec<Dp>, initialVelocity: Dp = 0.dp) {
        onAnchorChanged(collapsedAnchor)
        publishTarget(false)
        coroutineScope.launch {
            val generation = ++collapseGeneration
            onCollapseArmed?.invoke(true)
            var landed = false
            try {
                val result = animatable.animateTo(collapsedBound, animationSpec, initialVelocity)
                if (result.endReason == androidx.compose.animation.core.AnimationEndReason.BoundReached) {
                    landed = true
                    onCollapseLanded?.invoke(result.endState.velocity.value)
                }
            } finally {
                // A newer collapse cancelled this one and has already re-armed.
                if (!landed && generation == collapseGeneration) onCollapseArmed?.invoke(false)
            }
        }
    }

    /** Critically damped normally; a touch underdamped when a landing handler wants the
     *  arrival speed (only reachable because the collapsed bound stops the overshoot). */
    private fun collapseSpring() = spring<Dp>(
        dampingRatio = if (onCollapseLanded != null) COLLAPSE_LANDING_DAMPING else Spring.DampingRatioNoBouncy,
        stiffness = PlayerSheetStiffness,
    )

    fun expand(animationSpec: AnimationSpec<Dp>, initialVelocity: Dp = 0.dp) {
        onAnchorChanged(expandedAnchor)
        publishTarget(true)
        coroutineScope.launch {
            animatable.animateTo(expandedBound, animationSpec, initialVelocity)
        }
    }

    /** Settles toward the nearest anchor using the release velocity. */
    private fun collapse(velocityPxPerSec: Float = 0f) {
        collapse(collapseSpring(), initialVelocity = with(density) { velocityPxPerSec.toDp() })
    }

    private fun expand(velocityPxPerSec: Float = 0f) {
        expand(
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = PlayerSheetStiffness,
            ),
            initialVelocity = with(density) { velocityPxPerSec.toDp() },
        )
    }

    fun collapseSoft() {
        // Use the same spring for tap and drag transitions.
        endDragState()
        collapse(collapseSpring())
    }

    fun expandSoft() {
        // Match the opening spring to [collapseSoft].
        endDragState()
        expand(
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = PlayerSheetStiffness,
            ),
        )
    }

    fun dismiss() {
        onAnchorChanged(dismissedAnchor)
        endDragState()
        publishTarget(false)
        coroutineScope.launch {
            animatable.animateTo(dismissedBound)
        }
    }
    
    suspend fun dismissAndWait() {
        onAnchorChanged(dismissedAnchor)
        endDragState()
        publishTarget(false)
        animatable.animateTo(dismissedBound)
    }

    /** Predictive-back preview: [fraction] 0..1 of the gesture moves the sheet up to
     *  [PREDICTIVE_BACK_TRAVEL] of the way down. Eased so the first pixels are gentle. */
    fun previewBack(fraction: Float) {
        val eased = 1f - (1f - fraction.coerceIn(0f, 1f)).pow(2)
        val target = expandedBound - (expandedBound - collapsedBound) * (PREDICTIVE_BACK_TRAVEL * eased)
        coroutineScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
            animatable.snapTo(target)
        }
    }

    fun snapTo(value: Dp) {
        endDragState()
        publishTarget(value >= expandedBound)
        coroutineScope.launch {
            animatable.snapTo(value)
        }
    }

    fun performFling(velocity: Float, onDismiss: (() -> Unit)?) {
        endDragState()
        if (velocity > 250) {
            expand(velocity)
        } else if (velocity < -250) {
            if (value < collapsedBound && onDismiss != null) {
                dismiss()
                onDismiss.invoke()
            } else {
                collapse(velocity)
            }
        } else {
            // Slow release: snap nearest the *projected* endpoint, not the release
            // point, so a gentle toss toward an anchor still lands there. Work in px
            // since velocity is px/s; value/anchors are Dp.
            val currentPx = with(density) { value.toPx() }
            val snapsPx = buildList {
                if (onDismiss != null) add(with(density) { dismissedBound.toPx() })
                add(with(density) { collapsedBound.toPx() })
                add(with(density) { expandedBound.toPx() })
            }
            val pickedPx = nearestProjectedSnap(currentPx, velocity, snapsPx)
            val collapsedPx = with(density) { collapsedBound.toPx() }
            val expandedPx = with(density) { expandedBound.toPx() }
            when {
                onDismiss != null && pickedPx != null && pickedPx < collapsedPx && value < collapsedBound -> {
                    dismiss()
                    onDismiss.invoke()
                }
                pickedPx != null && pickedPx >= expandedPx -> expand()
                else -> collapse()
            }
        }
    }

    // Keep this connection stable across recompositions so drag state survives until onPreFling.
    val preUpPostDownNestedScrollConnection = object : NestedScrollConnection {
            var isTopReached = false

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isExpanded && available.y < 0) {
                    isTopReached = false
                }

                return if (isExpanded && available.y > 0 && isTopReached) {
                    dispatchRawDelta(available.y)
                    available
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (isExpanded && available.y < 0) {
                    isTopReached = true
                }

                return if (isExpanded && available.y > 0) {
                    isTopReached = true
                    dispatchRawDelta(available.y)
                    available
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (isExpanded && available.y < 0 && isTopReached) {
                    coroutineScope.launch {
                        performFling(available.y, null)
                    }

                    available
                } else {
                    Velocity.Zero
                }
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                isTopReached = false
                dragClockNs[0] = 0L
                return Velocity.Zero
            }
        }
}

const val expandedAnchor = 2
const val collapsedAnchor = 1
const val dismissedAnchor = 0

@Composable
fun rememberBottomSheetState(
    dismissedBound: Dp,
    expandedBound: Dp,
    collapsedBound: Dp = dismissedBound,
    initialAnchor: Int = dismissedAnchor,
    // Pane-sized sheets start at rest; screen-edge sheets can reveal on mount.
    animateOnMount: Boolean = true,
): BottomSheetState {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var previousAnchor by rememberSaveable {
        mutableIntStateOf(initialAnchor)
    }

    return remember(dismissedBound, expandedBound, collapsedBound, coroutineScope) {
        val initialValue = when (previousAnchor) {
            expandedAnchor -> expandedBound
            collapsedAnchor -> collapsedBound
            dismissedAnchor -> dismissedBound
            else -> error("Unknown BottomSheet anchor")
        }
        val animatable = Animatable(if (animateOnMount) 0.dp else initialValue, Dp.VectorConverter)

        animatable.updateBounds(dismissedBound.coerceAtMost(expandedBound), null)
        if (animateOnMount) {
            coroutineScope.launch {
                animatable.animateTo(initialValue, NavigationBarAnimationSpec)
            }
        }

        BottomSheetState(
            draggableState = DraggableState { delta ->
                // UNDISPATCHED: the snap lands in this input event, not a dispatch later --
                // the card was trailing the finger by a frame.
                coroutineScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                    // Keep direct drags within the expanded bound. The upper animation
                    // bound stays open so settle springs can overshoot. Rubber-banding here
                    // would need separate raw-drag state; using the clamped value makes the
                    // sheet jitter under the finger.
                    val next = animatable.value - with(density) { delta.toDp() }
                    animatable.snapTo(next.coerceAtMost(expandedBound))
                }
            },
            onAnchorChanged = { previousAnchor = it },
            coroutineScope = coroutineScope,
            animatable = animatable,
            collapsedBound = collapsedBound,
            expandedBound = expandedBound,
            dismissedBound = dismissedBound,
            density = density,
        )
    }
}
