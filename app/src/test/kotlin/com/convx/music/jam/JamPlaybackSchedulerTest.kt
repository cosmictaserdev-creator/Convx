package com.convx.music.jam

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JamPlaybackSchedulerTest {
    @Test
    fun replacingATrackWhileBufferingCancelsItsStart() = runBlocking {
        var now = 0L
        var current = true
        val scheduler = JamPlaybackScheduler({ now }) { elapsed -> now += elapsed; current = false }
        assertFalse(scheduler.awaitReady({ current }, { false }))
    }

    @Test
    fun bufferingHasABoundedDeadline() = runBlocking {
        var now = 0L
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertFalse(scheduler.awaitReady({ true }, { false }, timeoutMs = 250))
        assertEquals(250L, now)
    }

    @Test
    fun slowResolutionCatchesUpToTheCurrentTimeline() = runBlocking {
        var now = 0L
        val playback = PartyPlayback(isPlaying = true, anchorMs = 350)
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertTrue(scheduler.awaitReady({ true }, { now >= 2_000 }))
        assertTrue(scheduler.awaitStart({ true }) { JamSync.startDelayMs(playback, now) })
        assertEquals(1_650L, JamSync.positionAt(playback, now))
    }

    @Test
    fun aPreparedTrackNeverStartsBeforeItsDeadline() = runBlocking {
        var now = 0L
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertTrue(scheduler.awaitStart({ true }) { 750L - now })
        assertEquals(750L, now)
    }

    @Test
    fun leavingWhileWaitingForScheduledStartCancelsIt() = runBlocking {
        var now = 0L
        var current = true
        val scheduler = JamPlaybackScheduler({ now }) { now += it; current = false }
        assertFalse(scheduler.awaitStart({ current }) { 750L - now })
        assertEquals(250L, now)
    }
}
