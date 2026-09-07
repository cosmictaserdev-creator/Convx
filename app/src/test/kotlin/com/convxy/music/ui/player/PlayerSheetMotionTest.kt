package com.convxy.music.ui.player

import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The point of this transition is that the axes do NOT move together. A straight lerp
 * would pass any "does it end up in the right place" check, so these pin the asymmetry
 * itself — that is the part a refactor would silently flatten.
 */
class PlayerSheetMotionTest {

    private val mini = Rect(left = 16f, top = 900f, right = 400f, bottom = 964f)
    private val full = Rect(left = 0f, top = 0f, right = 1080f, bottom = 2400f)

    // Deliberately not square, so the aspect-ratio test below can actually fail.
    private val miniArt = Rect(left = 24f, top = 900f, right = 72f, bottom = 964f)
    private val fullArt = Rect(left = 140f, top = 300f, right = 940f, bottom = 1100f)

    @Test
    fun `container endpoints are exact`() {
        val start = sharedContainerRect(mini, full, 0f)
        assertEquals(mini.width, start.width, 0.01f)
        assertEquals(mini.height, start.height, 0.01f)

        val end = sharedContainerRect(mini, full, 1f)
        assertEquals(full.width, end.width, 0.01f)
        assertEquals(full.height, end.height, 0.01f)
    }

    @Test
    fun `container grows tall before it grows wide`() {
        val mid = sharedContainerRect(mini, full, 0.5f)
        val heightFraction = (mid.height - mini.height) / (full.height - mini.height)
        val widthFraction = (mid.width - mini.width) / (full.width - mini.width)
        // easeInCubic(0.5) = 0.125 against a linear 0.5.
        assertEquals(0.5f, heightFraction, 0.01f)
        assertEquals(0.125f, widthFraction, 0.01f)
        assertTrue("width must lag height", widthFraction < heightFraction)
    }

    @Test
    fun `artwork leads horizontally and follows vertically`() {
        val mid = sharedArtworkRect(miniArt, fullArt, 0.5f)
        val xFraction = (mid.center.x - miniArt.center.x) / (fullArt.center.x - miniArt.center.x)
        val yFraction = (mid.center.y - miniArt.center.y) / (fullArt.center.y - miniArt.center.y)
        // easeOutCubic(0.5) = 0.875 horizontally; vertically lerp(0.125, 0.5, 0.4) = 0.275.
        assertEquals(0.875f, xFraction, 0.01f)
        assertEquals(0.275f, yFraction, 0.01f)
        assertTrue("cover should lead horizontally", xFraction > yFraction)
    }

    @Test
    fun `artwork keeps its aspect ratio across the whole flight`() {
        val sourceRatio = miniArt.width / miniArt.height
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { progress ->
            val rect = sharedArtworkRect(miniArt, fullArt, progress)
            assertEquals("ratio at $progress", sourceRatio, rect.width / rect.height, 0.001f)
        }
    }

    @Test
    fun `screen corners only open out once fully expanded`() {
        // Mid-drag the expansion must be ignored, or corners snap square mid-gesture.
        val midDrag = sharedContainerCornerRadius(
            collapsedCornerRadius = 24f,
            expandedCornerRadius = 48f,
            progress = 0.5f,
            screenCornerExpansionProgress = 1f,
        )
        assertEquals(36f, midDrag, 0.01f)

        val settled = sharedContainerCornerRadius(24f, 48f, 1f, 1f)
        assertEquals(0f, settled, 0.01f)

        val settledNotYetExpanded = sharedContainerCornerRadius(24f, 48f, 1f, 0f)
        assertEquals(48f, settledNotYetExpanded, 0.01f)
    }

    @Test
    fun `leaving the expanded rest position does not pop the corners`() {
        // The failure this guards: a hard progress == 1f gate makes the first pixel of a
        // downward drag jump the radius from 0 straight back to the full screen radius.
        val atRest = sharedContainerCornerRadius(24f, 48f, 1f, 1f)
        val justMoved = sharedContainerCornerRadius(24f, 48f, 0.999f, 1f)
        assertTrue(
            "radius jumped ${justMoved - atRest}px on the first pixel of drag",
            justMoved - atRest < 8f,
        )

        // And the fade is finished well before the drag is meaningfully underway, so the
        // body of the gesture still rides the plain transition curve.
        val outsideBand = sharedContainerCornerRadius(24f, 48f, 0.9f, 1f)
        assertEquals(lerpRef(24f, 48f, 0.9f), outsideBand, 0.01f)
    }

    private fun lerpRef(start: Float, stop: Float, fraction: Float) =
        start + (stop - start) * fraction

    @Test
    fun `physical screen corners only apply to a full-screen window`() {
        assertTrue(
            playerWindowUsesPhysicalScreenCorners(
                1080, 2400, 1080, 2400,
                isInMultiWindowMode = false,
                isInPictureInPictureMode = false,
            ),
        )
        assertTrue(
            !playerWindowUsesPhysicalScreenCorners(
                1080, 1200, 1080, 2400,
                isInMultiWindowMode = true,
                isInPictureInPictureMode = false,
            ),
        )
        assertTrue(
            !playerWindowUsesPhysicalScreenCorners(
                1080, 2400, 1080, 2400,
                isInMultiWindowMode = false,
                isInPictureInPictureMode = true,
            ),
        )
    }

    @Test
    fun `progress outside zero to one is clamped, not extrapolated`() {
        assertEquals(
            sharedContainerRect(mini, full, 0f).width,
            sharedContainerRect(mini, full, -3f).width,
            0.01f,
        )
        assertEquals(
            sharedContainerRect(mini, full, 1f).width,
            sharedContainerRect(mini, full, 7f).width,
            0.01f,
        )
    }
}
