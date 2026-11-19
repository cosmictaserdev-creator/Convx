package com.convx.music.ui.player

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlayerCoverGesturesTest {
    @Test
    fun diagonalCrossesEuclideanSlopAndStaysOnItsFirstAxis() {
        val gesture = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)

        gesture.move(Offset(6f, 7f))
        assertEquals(CoverDragState.Idle, gesture.state)
        gesture.move(Offset(1f, 1f))
        assertEquals(CoverDragState.Dragging(CoverDragAxis.VERTICAL), gesture.state)
        assertEquals(8f, gesture.displacement, 0f)
        assertEquals(8f, gesture.delta, 0f)

        gesture.move(Offset(40f, -4f))
        assertEquals(CoverDragState.Dragging(CoverDragAxis.VERTICAL), gesture.state)
        assertEquals(-4f, gesture.delta, 0f)
        assertEquals(4f, gesture.displacement, 0f)
    }

    @Test
    fun dominantDisabledAxisAbandonsGestureToDescendants() {
        val gesture = CoverDragGesture(touchSlop = 10f, horizontal = false, vertical = true)

        gesture.move(Offset(14f, 4f))
        assertEquals(CoverDragState.Settling, gesture.state)
        assertNull(gesture.settle())

        gesture.move(Offset(0f, 80f))
        assertEquals(CoverDragState.Settling, gesture.state)
    }

    @Test
    fun diagonalCloseKeepsOwnershipThroughAReversalNearTheOrigin() {
        val gesture = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)

        gesture.move(Offset(-9f, 8f))
        assertEquals(CoverDragState.Dragging(CoverDragAxis.VERTICAL), gesture.state)

        gesture.move(Offset(8f, -7f))
        assertEquals(CoverDragState.Dragging(CoverDragAxis.VERTICAL), gesture.state)
        assertEquals(1f, gesture.displacement, 0f)

        gesture.move(Offset(-18f, 65f))
        assertEquals(66f, gesture.displacement, 0f)
        assertEquals(CoverDragAxis.VERTICAL, gesture.settle())
    }

    @Test
    fun reversalToTheOriginCancelsAndCannotBeClaimedAgain() {
        val gesture = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)

        gesture.move(Offset(18f, 2f))
        assertEquals(CoverDragState.Dragging(CoverDragAxis.HORIZONTAL), gesture.state)

        gesture.move(Offset(-12f, 4f))
        assertEquals(CoverDragState.Settling, gesture.state)
        assertNull(gesture.settle())

        gesture.move(Offset(40f, 0f))
        assertEquals(CoverDragState.Settling, gesture.state)
    }

    @Test
    fun fastReversalStillCancelsWhenTheSamplesStraddleTheOrigin() {
        val gesture = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)

        gesture.move(Offset(30f, 4f))
        gesture.move(Offset(-60f, 0f))

        assertEquals(CoverDragState.Settling, gesture.state)
        assertNull(gesture.settle())
    }

    @Test
    fun endingOneDragAllowsTheVeryNextGestureToClaimAnotherAxis() {
        val first = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)
        first.move(Offset(12f, 0f))
        assertEquals(CoverDragAxis.HORIZONTAL, first.settle())
        assertNull(first.settle())

        val next = CoverDragGesture(touchSlop = 10f, horizontal = true, vertical = true)
        next.move(Offset(0f, -13f))
        assertEquals(CoverDragAxis.VERTICAL, next.settle())
        assertEquals(-13f, next.displacement, 0f)
    }
}
