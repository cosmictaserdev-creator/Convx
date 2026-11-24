package com.convx.storage

import com.convx.storage.playback.ReadWindow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NetworkReadWindowTest {
    @Test fun seekBoundsReadsToTheRemainingFileAndRequestedRange() {
        val window = ReadWindow(7, 50, 10)
        assertEquals(3L, window.remaining)
        assertEquals(2, window.readLength(2))
        window.advance(2)
        assertEquals(9L, window.position)
        assertEquals(1, window.readLength(8))
        window.advance(1)
        assertEquals(0, window.readLength(8))
    }

    @Test fun seekingExactlyToEofNeedsNoRemoteRead() {
        assertEquals(0, ReadWindow(10, -1, 10).readLength(64))
    }

    @Test fun seekingBeyondEofFailsBeforeReading() {
        assertFailsWith<IllegalArgumentException> { ReadWindow(11, -1, 10) }
    }

    @Test fun veryLargeRangeDoesNotOverflowTheFilePosition() {
        val window = ReadWindow(Long.MAX_VALUE - 10, Long.MAX_VALUE, Long.MAX_VALUE)
        assertEquals(10L, window.remaining)
        window.advance(10)
        assertEquals(Long.MAX_VALUE, window.position)
    }
}
