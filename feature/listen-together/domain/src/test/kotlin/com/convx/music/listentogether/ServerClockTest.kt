package com.convx.music.listentogether

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class ServerClockTest {
    @Test
    fun skewedPhoneClockDoesNotMoveThePosition() {
        // The phone runs 5 s fast. A frame stamped at server 10_000 reaches it 40 ms later.
        val clock = ServerClock()
        clock.observe(serverTime = 10_000, receivedAt = 10_000 + 5_000 + 40)
        // 100 ms after arrival the action is 100 ms old on the server's timeline, not 5.1 s.
        assertEquals(100L, clock.elapsedSince(serverTime = 10_000, now = 10_000 + 5_000 + 140))
    }

    @Test
    fun lateFrameAdvancesTheGuestFurther() {
        val clock = ServerClock()
        clock.observe(20_000, 20_000 + 30)          // fast frame sets the offset
        clock.observe(21_000, 21_000 + 330)         // 300 ms slower than the best
        assertEquals(300L + 20L, clock.elapsedSince(21_000, 21_000 + 350))
    }

    @Test
    fun withoutSamplesItFallsBackToTheDeviceClock() {
        val clock = ServerClock()
        assertNull(clock.offsetMs(1_000))
        assertEquals(250L, clock.elapsedSince(serverTime = 750, now = 1_000))
    }

    @Test
    fun neverNegative() {
        val clock = ServerClock()
        clock.observe(5_000, 5_100)
        assertEquals(0L, clock.elapsedSince(serverTime = 9_000, now = 5_200))
    }
}
