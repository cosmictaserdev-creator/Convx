package com.convx.music.ui.player

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp
import com.convx.music.playback.presentation.PlayerMorphGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The container itself interpolates linearly (the reference app PlayerSheetGeometry) -- the
 * spring driving progress shapes the motion, so the rect adds no second curve.
 * Asymmetry lives in the artwork path and the tracked mini offsets below, and
 * those are what these pin: a refactor must not silently flatten them.
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
    fun `artwork window lands exactly on a non-square hero target`() {
        val squareArt = Rect(24f, 900f, 88f, 964f)
        val hero = Rect(0f, 0f, 1080f, 1560f)
        assertEquals(squareArt, PlayerMorphGeometry.artworkWindow(squareArt, hero, 0f, edgeToEdge = true))
        val end = PlayerMorphGeometry.artworkWindow(squareArt, hero, 1f, edgeToEdge = true)
        assertEquals(hero.left, end.left, 0.01f)
        assertEquals(hero.top, end.top, 0.01f)
        assertEquals(hero.right, end.right, 0.01f)
        assertEquals(hero.bottom, end.bottom, 0.01f)
        val nan = PlayerMorphGeometry.artworkWindow(squareArt, hero, Float.NaN, edgeToEdge = true)
        assertEquals(squareArt, nan)
    }

    @Test
    fun `ambient transition follows the dj crossfade, clamped`() {
        assertEquals(AMBIENT_TRANSITION_DEFAULT_MS, ambientTransitionMillis(0L))
        assertEquals(4_000, ambientTransitionMillis(4_000L))
        assertEquals(8_000, ambientTransitionMillis(30_000L))
        assertEquals(900, ambientTransitionMillis(100L))
    }

    @Test
    fun `container interpolates linearly edge to edge`() {
        val mid = sharedContainerRect(mini, full, 0.5f)
        val heightFraction = (mid.height - mini.height) / (full.height - mini.height)
        val widthFraction = (mid.width - mini.width) / (full.width - mini.width)
        assertEquals(0.5f, heightFraction, 0.01f)
        assertEquals(0.5f, widthFraction, 0.01f)
        assertEquals(lerp(mini.left, full.left, 0.5f), mid.left, 0.01f)
        assertEquals(lerp(mini.top, full.top, 0.5f), mid.top, 0.01f)
    }

    @Test
    fun `card corners resolve to zero exactly at full expansion`() {        val radii = sharedContainerCornerRadii(
            source = SharedContainerCornerRadii.uniform(32f),
            target = SharedContainerCornerRadii.uniform(28f),
            progress = 1f,
        )
        assertEquals(0f, radii.topStart, 0.001f)
        assertEquals(0f, radii.bottomEnd, 0.001f)
        val mid = sharedContainerCornerRadii(
            source = SharedContainerCornerRadii.uniform(32f),
            target = SharedContainerCornerRadii.uniform(28f),
            progress = 0.5f,
        )
        assertEquals(30f, mid.topStart, 0.01f)
    }

    @Test
    fun `mini title stays absolute while controls ride the right edge`() {
        val source = Rect(left = 16f, top = 900f, right = 400f, bottom = 964f)
        val controls = Rect(left = 320f, top = 908f, right = 392f, bottom = 956f)
        // At rest both shifts are zero: the layer already sits at the corner.
        assertEquals(0f, sharedMiniPlayerTitleShift(source, source), 0.01f)
        assertEquals(0f, sharedMiniPlayerControlsShift(source, source, controls), 0.01f)
        val animated = sharedContainerRect(source, full, 0.5f)
        // Title compensates the corner travel exactly: absolute x is restored.
        val titleShift = sharedMiniPlayerTitleShift(source, animated)
        assertEquals(source.left, animated.left + titleShift, 0.01f)
        // Controls right edge lands on the travelling right edge minus inset.
        val sourceInset = source.right - controls.right
        val shift = sharedMiniPlayerControlsShift(source, animated, controls)
        val landedRight = animated.left + titleShift + shift + (controls.right - source.left)
        assertEquals(animated.right - sourceInset, landedRight, 0.01f)
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
    fun `drag owner follows the end the drag started from`() {
        assertEquals(
            PlayerInputOwner.MINI,
            playerSheetInputOwner(
                targetOpen = true,
                isDragging = true,
                dragStartedFromMiniPlayer = true,
                progress = 0.5f,
                sharedLayersReady = true,
            ),
        )
        assertEquals(
            PlayerInputOwner.FULL,
            playerSheetInputOwner(
                targetOpen = false,
                isDragging = true,
                dragStartedFromMiniPlayer = false,
                progress = 0.5f,
                sharedLayersReady = true,
            ),
        )
    }

    @Test
    fun `mid-flight taps own nobody once the mini handed over`() {
        assertEquals(
            PlayerInputOwner.MINI,
            playerSheetInputOwner(
                targetOpen = false,
                isDragging = false,
                dragStartedFromMiniPlayer = false,
                progress = 0.1f,
                sharedLayersReady = false,
            ),
        )
        assertEquals(
            PlayerInputOwner.NONE,
            playerSheetInputOwner(
                targetOpen = true,
                isDragging = false,
                dragStartedFromMiniPlayer = false,
                progress = 0.5f,
                sharedLayersReady = false,
            ),
        )
        assertEquals(
            PlayerInputOwner.FULL,
            playerSheetInputOwner(
                targetOpen = true,
                isDragging = false,
                dragStartedFromMiniPlayer = false,
                progress = 1f,
                sharedLayersReady = false,
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
