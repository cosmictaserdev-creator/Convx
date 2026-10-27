package com.convx.music.jam

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JamSyncTest {
    @Test
    fun delayedPreparationCatchesUpInsteadOfStartingAtTheOldPosition() {
        val playback = PartyPlayback(isPlaying = true, positionMs = 0, anchorMs = 10_000)
        assertEquals(0L, JamSync.startDelayMs(playback, 14_000))
        assertEquals(4_000L, JamSync.positionAt(playback, 14_000))
    }

    @Test
    fun staleClockSamplesCannotDrivePlaybackAfterResume() {
        val clock = JamClock(maxAgeMs = 1_000)
        clock.observe(100, 5_110, 120)
        assertTrue(clock.isFresh(500))
        assertFalse(clock.isFresh(1_121))
        clock.observe(2_000, 8_010, 2_020)
        assertEquals(9_000L, clock.serverNow(3_000))
    }

    @Test
    fun oldLowLatencySampleExpiresWhenTheServerClockMappingChanges() {
        val clock = JamClock(maxAgeMs = 1_000)
        clock.observe(100, 5_101, 102)
        clock.observe(2_000, 9_010, 2_020)
        assertEquals(10_000L, clock.serverNow(3_000))
    }

    @Test
    fun invalidAndExtremelyDelayedClockSamplesAreIgnored() {
        val clock = JamClock()
        clock.observe(200, 1_000, 100)
        clock.observe(100, 2_000, 20_000)
        assertFalse(clock.synced)
    }

    @Test
    fun schedulingUsesMonotonicTimeEvenWhenWallClockWouldJump() {
        val clock = JamClock()
        clock.observe(1_000, 1_000_010, 1_020)
        assertEquals(1_000_500L, clock.serverNow(1_500))
        assertEquals(10L, clock.uncertaintyMs())
    }

    @Test
    fun clockTrustsTheSampleWithTheSmallestRoundTrip() {
        val clock = JamClock()
        // Phone is 2 s behind the server. A slow sample (rtt 400) and a fast one (rtt 20).
        clock.observe(clientSentMs = 1_000, serverMs = 3_200, receivedMs = 1_400)
        clock.observe(clientSentMs = 5_000, serverMs = 7_010, receivedMs = 5_020)
        assertEquals(2_000L, clock.offsetMs())
        assertEquals(12_000L, clock.serverNow(10_000))
    }

    @Test
    fun playingPositionAdvancesFromTheAnchor() {
        val playback = PartyPlayback(isPlaying = true, positionMs = 42_000, anchorMs = 100_000)
        assertEquals(42_000L + 300, JamSync.positionAt(playback, serverNow = 100_300))
    }

    @Test
    fun pausedPositionDoesNotMove() {
        val playback = PartyPlayback(isPlaying = false, positionMs = 42_000, anchorMs = 100_000)
        assertEquals(42_000L, JamSync.positionAt(playback, serverNow = 160_000))
    }

    @Test
    fun aResumeScheduledAheadHoldsUntilItsAnchor() {
        val playback = PartyPlayback(isPlaying = true, positionMs = 10_000, anchorMs = 100_350)
        assertEquals(10_000L, JamSync.positionAt(playback, serverNow = 100_000))
        assertEquals(350L, JamSync.startDelayMs(playback, serverNow = 100_000))
        assertEquals(0L, JamSync.startDelayMs(playback, serverNow = 100_400))
    }
}
