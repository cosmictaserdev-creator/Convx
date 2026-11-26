package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HlsPrefetchPlanTest {
    @Test fun openingFractionIncludesTheWholeBoundarySegment() {
        assertEquals(listOf(0, 1), hlsPrefetchSegmentIndices(listOf(0, 4, 8), 13, 3))
        assertEquals(listOf(0, 1, 2), hlsPrefetchSegmentIndices(listOf(0, 4, 8), 12, 1))
        assertEquals(emptyList(), hlsPrefetchSegmentIndices(listOf(0), -1, 3))
    }

    @Test fun choosesKnownBitrateAndFallsBackToPlaylistOrder() {
        assertEquals(2, hlsPrefetchVariantIndex(listOf(64, -1, 192), preferLow = false))
        assertEquals(0, hlsPrefetchVariantIndex(listOf(64, -1, 192), preferLow = true))
        assertEquals(0, hlsPrefetchVariantIndex(listOf(-1, -1), preferLow = false))
        assertNull(hlsPrefetchVariantIndex(emptyList(), preferLow = false))
    }
}
