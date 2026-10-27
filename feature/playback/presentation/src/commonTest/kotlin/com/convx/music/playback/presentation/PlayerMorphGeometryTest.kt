package com.convx.music.playback.presentation

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerMorphGeometryTest {
    private val mini = Rect(16f, 900f, 400f, 964f)
    private val full = Rect(0f, 0f, 1080f, 2400f)

    @Test
    fun containerInterpolatesEdgesAndClampsInvalidProgress() {
        val middle = PlayerMorphGeometry.containerRect(mini, full, 0.5f)
        assertEquals(0.5f, (middle.left - mini.left) / (full.left - mini.left), 0.001f)
        assertEquals(mini, PlayerMorphGeometry.containerRect(mini, full, Float.NaN))
        assertEquals(full, PlayerMorphGeometry.containerRect(mini, full, Float.POSITIVE_INFINITY))
    }

    @Test
    fun artworkWindowKeepsTheAndroidLeadAndHitsBothEndpoints() {
        val source = Rect(24f, 900f, 88f, 964f)
        val target = Rect(0f, 0f, 1080f, 1560f)

        assertEquals(source, PlayerMorphGeometry.artworkWindow(source, target, 0f))
        assertEquals(target, PlayerMorphGeometry.artworkWindow(source, target, 1f))

        val middle = PlayerMorphGeometry.artworkWindow(source, target, 0.5f)
        val horizontalFraction = (middle.center.x - source.center.x) / (target.center.x - source.center.x)
        val verticalFraction = (middle.center.y - source.center.y) / (target.center.y - source.center.y)
        assertTrue(horizontalFraction > verticalFraction)
    }

    @Test
    fun stagedFadesPreserveThePlayerHandoff() {
        assertEquals(1f, PlayerMorphGeometry.miniContentAlpha(0f), 0.001f)
        assertEquals(0f, PlayerMorphGeometry.miniContentAlpha(PLAYER_MORPH_MINI_FADE_END), 0.001f)
        assertEquals(0f, PlayerMorphGeometry.pageAlpha(PLAYER_MORPH_CONTENT_APPEAR_START), 0.001f)
        assertEquals(1f, PlayerMorphGeometry.pageAlpha(PLAYER_MORPH_CONTENT_APPEAR_END), 0.001f)
        assertEquals(1f, PlayerMorphGeometry.backgroundAlpha(PLAYER_MORPH_LAYER_HANDOFF_PROGRESS), 0.001f)
    }

    @Test
    fun artworkPreservesItsAspectRatioThroughoutTheMorph() {
        val source = Rect(24f, 900f, 72f, 964f)
        val target = Rect(140f, 300f, 940f, 1100f)
        val expectedRatio = source.width / source.height

        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { progress ->
            val bounds = PlayerMorphGeometry.artworkRect(source, target, progress)
            assertEquals(expectedRatio, bounds.width / bounds.height, 0.001f)
        }
    }

    @Test
    fun inputOwnershipFollowsGestureOriginAndLayerReadiness() {
        assertEquals(
            PlayerMorphInputOwner.MINI,
            PlayerMorphGeometry.inputOwner(true, true, true, 0.5f, true),
        )
        assertEquals(
            PlayerMorphInputOwner.FULL,
            PlayerMorphGeometry.inputOwner(false, true, false, 0.5f, true),
        )
        assertEquals(
            PlayerMorphInputOwner.NONE,
            PlayerMorphGeometry.inputOwner(true, false, false, 0.5f, false),
        )
        assertEquals(
            PlayerMorphInputOwner.FULL,
            PlayerMorphGeometry.inputOwner(true, false, false, 1f, false),
        )
    }

    @Test
    fun sheetCornerRadiusSettlesWithoutJumpingWhenTheDragReverses() {
        val resting = PlayerMorphGeometry.cornerRadius(24f, 48f, 1f, 1f)
        val firstDragFrame = PlayerMorphGeometry.cornerRadius(24f, 48f, 0.999f, 1f)

        assertEquals(0f, resting, 0.001f)
        assertTrue(firstDragFrame < 8f)
        assertEquals(36f, PlayerMorphGeometry.cornerRadius(24f, 48f, 0.5f, 1f), 0.001f)
    }
}
