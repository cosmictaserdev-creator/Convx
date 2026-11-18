package com.convx.music.catalog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AlbumDiscIndexTest {
    @Test fun discSortsFirstThenPositionAndRoundTrips() {
        val d1 = AlbumDiscIndex.encode(1, 9_999)
        val d2 = AlbumDiscIndex.encode(2, 0)
        assertTrue(d1 < d2)
        assertEquals(1, AlbumDiscIndex.discOf(d1))
        assertEquals(2, AlbumDiscIndex.discOf(d2))
        assertEquals(3, AlbumDiscIndex.discOf(AlbumDiscIndex.encode(3, 12)))
    }

    @Test fun unknownDiscIsDiscOneAndPlainIndexesStayDiscOne() {
        assertEquals(7, AlbumDiscIndex.encode(null, 7))
        assertEquals(1, AlbumDiscIndex.discOf(0))
        assertEquals(1, AlbumDiscIndex.discOf(250))
        assertEquals(1, AlbumDiscIndex.discOf(-1))
    }

    @Test fun positionsPastTheStrideAreRejected() {
        assertFailsWith<IllegalArgumentException> { AlbumDiscIndex.encode(1, AlbumDiscIndex.DISC_STRIDE) }
    }
}
