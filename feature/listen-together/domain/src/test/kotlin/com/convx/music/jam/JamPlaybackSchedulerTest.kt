package com.convx.music.jam

import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

class JamPlaybackSchedulerTest {
    @Test
    fun replacingATrackWhileBufferingCancelsItsStart() = runTest {
        var now = 0L
        var current = true
        val scheduler = JamPlaybackScheduler({ now }) { elapsed -> now += elapsed; current = false }
        assertFalse(scheduler.awaitReady({ current }, { false }))
    }

    @Test
    fun bufferingHasABoundedDeadline() = runTest {
        var now = 0L
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertFalse(scheduler.awaitReady({ true }, { false }, timeoutMs = 250))
        assertEquals(250L, now)
    }

    @Test
    fun slowResolutionCatchesUpToTheCurrentTimeline() = runTest {
        var now = 0L
        val playback = PartyPlayback(isPlaying = true, anchorMs = 350)
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertTrue(scheduler.awaitReady({ true }, { now >= 2_000 }))
        assertTrue(scheduler.awaitStart({ true }) { JamSync.startDelayMs(playback, now) })
        assertEquals(1_650L, JamSync.positionAt(playback, now))
    }

    @Test
    fun aPreparedTrackNeverStartsBeforeItsDeadline() = runTest {
        var now = 0L
        val scheduler = JamPlaybackScheduler({ now }) { now += it }
        assertTrue(scheduler.awaitStart({ true }) { 750L - now })
        assertEquals(750L, now)
    }

    @Test
    fun leavingWhileWaitingForScheduledStartCancelsIt() = runTest {
        var now = 0L
        var current = true
        val scheduler = JamPlaybackScheduler({ now }) { now += it; current = false }
        assertFalse(scheduler.awaitStart({ current }) { 750L - now })
        assertEquals(250L, now)
    }
}
