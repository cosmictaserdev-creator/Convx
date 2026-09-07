/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.convxy.music.ui.player.PlayerMorph
import com.convxy.music.ui.player.easeOutCubic
import com.convxy.music.ui.player.SCREEN_CORNER_EXPANSION_MILLIS
import com.convxy.music.ui.player.recordPlayerLayer
import com.convxy.music.ui.player.sharedContainerCornerRadius
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.convxy.music.constants.NavigationBarAnimationSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow

/** Mirrors BackdropFreeze.kt's identical constant: a safety net for drags that
 *  never deliver onDragEnd/onDragCancel (e.g. gesture stolen elsewhere). */
/**
 * Progress at which the collapsed and expanded layers hand over.
 *
 * One value for both halves so they cannot drift into overlapping again: the mini player
 * is fully faded out here, and the expanded content starts fading in here.
 */
internal const val PLAYER_LAYER_HANDOFF_PROGRESS = 0.25f

private const val BackdropFreezeSafetyNs = 900_000_000L

/**
 * Bottom Sheet
 * Modified from [ViMusic](https://github.com/vfsfitvnm/ViMusic)
 */
@Composable
fun BottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    background: @Composable (BoxScope.() -> Unit) = { },
    onDismiss: (() -> Unit)? = null,
    collapsedContent: @Composable BoxScope.() -> Unit,
    isExpandable: Boolean = true,
    /** Caps the expanded content to this width, centered horizontally, so the
     *  controls read as one phone-shaped panel on a wide screen instead of
     *  stretching edge to edge. [background] (the full-bleed artwork/wash) is
     *  deliberately NOT capped by this — it stays full-screen regardless, same
     *  as the mobile layout's own artwork treatment; only the controls above it
     *  get centered and width-limited. The collapsed/mini content (a full-width
     *  dock regardless) is unaffected either way.
     *  [Dp.Unspecified] (default) lets content fill the sheet exactly as before. */
    contentMaxWidth: Dp = Dp.Unspecified,
    /** Corner radius the sheet rounds to as it reaches full size. The player passes the
     *  device's physical screen radius so the sheet's corners land on the glass; other
     *  callers can leave it and keep the plain collapsed-to-square curve. */
    expandedCornerRadius: Dp = 0.dp,
    /** Corner radius while collapsed. */
    collapsedCornerRadius: Dp = 16.dp,
    /**
     * Drawn as a sibling of the sheet's own draggable content, NOT nested inside it --
     * the sheet applies its own `translationY` to track the drag, and this overlay needs
     * plain, unshifted root/window coordinates to place things by. Null for every caller
     * that doesn't need one (this is a generic sheet primitive; only the player's own
     * `BottomSheet(...)` call passes one, for the mini-to-full artwork morph).
     */
    overlayContent: (@Composable BoxScope.() -> Unit)? = null,
    /**
     * Hands this sheet's drawing to the shared container morph (see [PlayerMorph]).
     *
     * While the morph is running the sheet is recorded and NOT drawn where it sits, and
     * it stops translating -- the container overlay draws the recording scaled into the
     * rect that grows out of the mini player. Only the player passes this; every other
     * caller keeps the plain slide-up sheet.
     */
    morphEnabled: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current

    // Second stage of the corner treatment: once the sheet has settled at full size, its
    // corners open out to square over a short beat of their own, so the player ends up
    // filling the display edge to edge with the device's own glass doing the rounding.
    // Separate from the sheet's motion on purpose -- the corners resolve after the sheet
    // arrives, not with it.
    val cornerExpansion = remember { Animatable(0f) }
    LaunchedEffect(state.isExpanded) {
        cornerExpansion.animateTo(
            targetValue = if (state.isExpanded) 1f else 0f,
            animationSpec = tween(SCREEN_CORNER_EXPANSION_MILLIS),
        )
    }

    // One wrapper around BOTH halves of the sheet (its full-bleed background and its
    // draggable content) so the morph records the player as it actually looks. Recording
    // only the content would hand the container overlay a player with no artwork wash
    // behind it. Plain fillMaxSize: both children already fill, so this adds a node and
    // changes no layout.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (morphEnabled) {
                    Modifier.recordPlayerLayer(
                        layer = { PlayerMorph.fullLayer },
                        drawInPlace = { !PlayerMorph.active },
                    )
                } else {
                    Modifier
                },
            ),
    ) {
    Box(
        modifier = modifier
            .graphicsLayer {
                // background fades during about 10%-61% progress
                alpha = if (morphEnabled && PlayerMorph.active) {
                    // The full-bleed artwork is the one thing that must NOT arrive with
                    // the rest of the player. The container overlay fades the whole
                    // recording in over the first quarter, which for a full-screen image
                    // reads as it appearing in a single frame; easing it across the WHOLE
                    // flight instead lets the cover and controls land first and the
                    // artwork wash bloom in behind them.
                    easeOutCubic(state.progress)
                } else {
                    (1.4f * (state.progress.coerceAtLeast(0.1f) - 0.1f).pow(0.5f)).coerceIn(0f, 1f)
                }
            }
            .fillMaxSize(),
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
            // Use graphicsLayer for offset to ensure hardware acceleration and 120Hz support
            .graphicsLayer {
                // Pinned at full size while the morph owns the frame: the container
                // overlay places the recording itself, and a sheet still translated down
                // would be recorded mid-slide and then placed a second time.
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
                    onVerticalDrag = { change, dragAmount ->
                        state.dragClockNs[0] = System.nanoTime()
                        velocityTracker.addPointerInputChange(change)
                        state.dispatchRawDelta(dragAmount)
                    },
                    onDragCancel = {
                        state.dragClockNs[0] = 0L
                        velocityTracker.resetTracking()
                        state.snapTo(state.collapsedBound)
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
                // Was `if (!state.isExpanded) 16.dp else 0f` -- a hard switch, so the
                // corners popped square the moment the sheet latched open and popped back
                // on the first pixel of a drag. Both endpoints are unchanged; what is new
                // is that the radius now travels between them.
                val cornerRadius = if (morphEnabled && PlayerMorph.active) {
                    // The container overlay is doing the rounding, on the rect it is
                    // actually drawing. Rounding the recording as well would bake a
                    // second, wrongly-scaled curve into it.
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
        if (!state.isCollapsed && !state.isDismissed) {
            BackHandler(onBack = state::collapseSoft)
        }

        // main content
        if (!state.isCollapsed) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Starts where the mini player has finished leaving, not before
                        // it. The two used to overlap between 0.15 and 0.25, so for that
                        // slice of every open and close both were on screen at partial
                        // opacity and the artwork ghosted against itself.
                        // Same reason as the background above: the overlay owns the
                        // fade while the morph is running.
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

    }

    // Sibling of the translated sheet Box above, not a child of it -- see the
    // parameter doc. Unaffected by the sheet's own drag translationY, so the
    // overlay's own translations (root-relative rects it was handed) land where
    // they mean to instead of being shifted a second time.
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
) : DraggableState by draggableState {
    // Same technique as BackdropFreeze.kt: a plain array, not snapshot state,
    // read during the draw phase by a sampling glass surface's `frozen`
    // provider. A snapshot read there would register a draw dependency and
    // every write would re-invalidate the frame forever (the trap documented
    // at MainActivity.kt:1253 and BackdropFreeze.kt:21-23).
    internal val dragClockNs = longArrayOf(0L)

    /** Pass to a `background` slot's [layerBackdrop] to skip re-recording it
     *  while this sheet is being dragged, mirroring [BackdropFreeze]. */
    val backdropFrozen: () -> Boolean = {
        val started = dragClockNs[0]
        started != 0L && System.nanoTime() - started < BackdropFreezeSafetyNs
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

    fun collapse(animationSpec: AnimationSpec<Dp>) {
        onAnchorChanged(collapsedAnchor)
        coroutineScope.launch {
            animatable.animateTo(collapsedBound, animationSpec)
        }
    }

    fun expand(animationSpec: AnimationSpec<Dp>) {
        onAnchorChanged(expandedAnchor)
        coroutineScope.launch {
            animatable.animateTo(expandedBound, animationSpec)
        }
    }

    private fun collapse() {
        // Apple Music feel: bouncy spring for collapse with tactile overshoot
        collapse(
            spring(
                dampingRatio = 0.68f,
                stiffness = 380f,
            )
        )
    }

    private fun expand() {
        // Apple Music feel: bouncy spring for expand with tactile overshoot
        expand(
            spring(
                dampingRatio = 0.68f,
                stiffness = 380f,
            )
        )
    }

    fun collapseSoft() {
        // Bouncy settling curve: energetic response, fast and fluid on 120Hz panels
        collapse(
            spring(
                dampingRatio = 0.70f,
                stiffness = 360f,
            ),
        )
    }

    fun expandSoft() {
        // Bouncy settling curve: energetic response, fast and fluid on 120Hz panels
        expand(
            spring(
                dampingRatio = 0.70f,
                stiffness = 360f,
            ),
        )
    }

    fun dismiss() {
        onAnchorChanged(dismissedAnchor)
        coroutineScope.launch {
            animatable.animateTo(dismissedBound)
        }
    }
    
    suspend fun dismissAndWait() {
        onAnchorChanged(dismissedAnchor)
        animatable.animateTo(dismissedBound)
    }

    fun snapTo(value: Dp) {
        coroutineScope.launch {
            animatable.snapTo(value)
        }
    }

    fun performFling(velocity: Float, onDismiss: (() -> Unit)?) {
        if (velocity > 250) {
            expand()
        } else if (velocity < -250) {
            if (value < collapsedBound && onDismiss != null) {
                dismiss()
                onDismiss.invoke()
            } else {
                collapse()
            }
        } else {
            val l0 = dismissedBound
            val l1 = (collapsedBound - dismissedBound) / 2
            val l2 = (expandedBound - collapsedBound) / 2
            val l3 = expandedBound

            when (value) {
                in l0..l1 -> {
                    if (onDismiss != null) {
                        dismiss()
                        onDismiss.invoke()
                    } else {
                        collapse()
                    }
                }

                in l1..l2 -> collapse()
                in l2..l3 -> expand()
                else -> Unit
            }
        }
    }

    val preUpPostDownNestedScrollConnection
        get() = object : NestedScrollConnection {
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
): BottomSheetState {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var previousAnchor by rememberSaveable {
        mutableIntStateOf(initialAnchor)
    }
    val animatable = remember {
        Animatable(0.dp, Dp.VectorConverter)
    }

    return remember(dismissedBound, expandedBound, collapsedBound, coroutineScope) {
        val initialValue = when (previousAnchor) {
            expandedAnchor -> expandedBound
            collapsedAnchor -> collapsedBound
            dismissedAnchor -> dismissedBound
            else -> error("Unknown BottomSheet anchor")
        }

        animatable.updateBounds(dismissedBound.coerceAtMost(expandedBound), null)
        coroutineScope.launch {
            animatable.animateTo(initialValue, NavigationBarAnimationSpec)
        }

        BottomSheetState(
            draggableState = DraggableState { delta ->
                coroutineScope.launch {
                    animatable.snapTo(animatable.value - with(density) { delta.toDp() })
                }
            },
            onAnchorChanged = { previousAnchor = it },
            coroutineScope = coroutineScope,
            animatable = animatable,
            collapsedBound = collapsedBound,
            expandedBound = expandedBound,
            dismissedBound = dismissedBound,
        )
    }
}
