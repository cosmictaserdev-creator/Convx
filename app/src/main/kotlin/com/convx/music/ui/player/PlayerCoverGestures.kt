/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

internal enum class CoverDragAxis { HORIZONTAL, VERTICAL }

private const val HORIZONTAL_DOMINANCE = 1.4f

internal sealed interface CoverDragState {
    data object Idle : CoverDragState
    data class Dragging(val axis: CoverDragAxis) : CoverDragState
    data object Settling : CoverDragState
}

/** Tracks displacement independently of pointer dispatch and animation. */
internal class CoverDragGesture(
    touchSlop: Float,
    private val horizontal: Boolean,
    private val vertical: Boolean,
) {
    var state: CoverDragState = CoverDragState.Idle
        private set
    var position: Offset = Offset.Zero
        private set
    var displacement: Float = 0f
        private set
    var delta: Float = 0f
        private set

    private val slopSquared = touchSlop * touchSlop

    fun move(change: Offset) {
        if (state == CoverDragState.Settling) return
        val previous = position
        position += change
        val insideOrigin = position.getDistanceSquared() <= slopSquared

        val dragging = when (val current = state) {
            CoverDragState.Idle -> {
                if (insideOrigin) return
                val axis = if (abs(position.x) > abs(position.y) * HORIZONTAL_DOMINANCE) {
                    CoverDragAxis.HORIZONTAL
                } else {
                    CoverDragAxis.VERTICAL
                }
                val enabled = if (axis == CoverDragAxis.HORIZONTAL) horizontal else vertical
                if (!enabled) {
                    settle()
                    return
                }
                CoverDragState.Dragging(axis).also { state = it }
            }
            is CoverDragState.Dragging -> {
                if (current.axis == CoverDragAxis.HORIZONTAL && (insideOrigin || crossesOrigin(previous, change))) {
                    settle()
                    return
                }
                current
            }
            CoverDragState.Settling -> return
        }

        val nextDisplacement = position.onAxis(dragging.axis)
        delta = nextDisplacement - displacement
        displacement = nextDisplacement
    }

    private fun crossesOrigin(previous: Offset, change: Offset): Boolean {
        val distanceSquared = change.getDistanceSquared()
        if (distanceSquared == 0f) return false
        val nearest = -(previous.x * change.x + previous.y * change.y) / distanceSquared
        if (nearest <= 0f || nearest >= 1f) return false
        return (previous + change * nearest).getDistanceSquared() <= slopSquared
    }

    /** The same pointer sequence cannot regain ownership after settling. */
    fun settle(): CoverDragAxis? {
        val axis = (state as? CoverDragState.Dragging)?.axis
        state = CoverDragState.Settling
        return axis
    }
}

/**
 * Give descendants the original down and all pre-slop movement. Claim the first eligible
 * drag at Initial so the movement that owns the cover also cancels a descendant tap.
 * Deltas use the locked axis in physical pixels. Positive values go right or down.
 */
internal fun Modifier.playerCoverGestures(
    horizontal: Boolean,
    vertical: Boolean,
    onStart: (CoverDragAxis) -> Unit,
    onMove: (CoverDragAxis, delta: Float, displacement: Float) -> Unit,
    onEnd: (CoverDragAxis, displacement: Float, velocity: Float) -> Unit,
    onCancel: (CoverDragAxis) -> Unit,
): Modifier = composed {
    val start = rememberUpdatedState(onStart)
    val move = rememberUpdatedState(onMove)
    val end = rememberUpdatedState(onEnd)
    val cancel = rememberUpdatedState(onCancel)

    pointerInput(horizontal, vertical) {
        if (!horizontal && !vertical) return@pointerInput
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (currentEvent.changes.count { it.pressed } > 1) return@awaitEachGesture
            val gesture = CoverDragGesture(viewConfiguration.touchSlop, horizontal, vertical)
            val velocity = VelocityTracker()
            velocity.addPosition(down.uptimeMillis, Offset.Zero)

            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.isConsumed || event.changes.any { it.id != down.id && it.pressed }) break

                    val before = gesture.state
                    if (!change.pressed && before == CoverDragState.Idle) break
                    gesture.move(change.positionChangeIgnoreConsumed())
                    velocity.addPosition(change.uptimeMillis, gesture.position)

                    when (val after = gesture.state) {
                        CoverDragState.Idle -> Unit
                        CoverDragState.Settling -> {
                            if (before is CoverDragState.Dragging) {
                                change.consume()
                                cancel.value(before.axis)
                            }
                            break
                        }
                        is CoverDragState.Dragging -> {
                            change.consume()
                            if (before == CoverDragState.Idle) start.value(after.axis)
                            if (gesture.delta != 0f) move.value(after.axis, gesture.delta, gesture.displacement)
                            if (!change.pressed) {
                                gesture.settle()
                                val releaseVelocity = velocity.calculateVelocity()
                                val axisVelocity = if (after.axis == CoverDragAxis.HORIZONTAL) releaseVelocity.x else releaseVelocity.y
                                end.value(after.axis, gesture.displacement, axisVelocity)
                                break
                            }
                        }
                    }
                }
            } finally {
                gesture.settle()?.let { cancel.value(it) }
            }
        }
    }
}

private fun Offset.onAxis(axis: CoverDragAxis): Float =
    if (axis == CoverDragAxis.HORIZONTAL) x else y
