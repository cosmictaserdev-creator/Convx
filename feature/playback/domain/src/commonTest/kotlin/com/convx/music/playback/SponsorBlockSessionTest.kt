package com.convx.music.playback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SponsorBlockSessionTest {
    private val id = "abcdefghijk"
    private val segments = listOf(SponsorSegment(0, 8_000, SponsorCategory.SPONSOR))
    private val skipper = SponsorBlockSkipper(segments, DefaultSponsorCategories)

    @Test fun pausedAndInvalidPositionsNeverSeek() {
        assertNull(sponsorSkipAction(SponsorPlaybackState(id, 1_000), skipper))
        assertNull(sponsorSkipAction(SponsorPlaybackState(id, -1, isPlaying = true), skipper))
        assertEquals(SponsorSkipAction.Seek(8_000), sponsorSkipAction(SponsorPlaybackState(id, 1_000, isPlaying = true), skipper))
    }

    @Test fun endOfTrackUsesNextOnlyWhenTheAdapterAllowsIt() {
        val state = SponsorPlaybackState(id, 1_000, durationMs = 5_000, isPlaying = true, canSkipNext = true)
        assertEquals(SponsorSkipAction.NextTrack, sponsorSkipAction(state, skipper))
        assertEquals(SponsorSkipAction.Seek(5_000), sponsorSkipAction(state.copy(canSkipNext = false), skipper))
        assertNull(sponsorSkipAction(state.copy(positionMs = 5_000, canSkipNext = false), skipper))
    }

    @Test fun unknownDurationStillUsesTheSegmentEnd() {
        assertEquals(SponsorSkipAction.Seek(8_000), sponsorSkipAction(SponsorPlaybackState(id, isPlaying = true), skipper))
    }

    @Test fun disabledAndNonYoutubeIdentitiesMakeNoLookups() = runTest {
        var state = SponsorPlaybackState(null, isPlaying = true)
        var lookups = 0
        val session = SponsorBlockSession(SponsorSegmentsSource { _, _ -> lookups++; segments })
        backgroundScope.launch { session.run({ state }, { _, _ -> fail("Must not skip") }) }
        runCurrent()
        state = state.copy(videoId = "file:///music/song.flac")
        advanceTimeBy(1_000); runCurrent()
        assertEquals(0, lookups)
    }

    @Test fun aPendingSeekIsNotRepeatedUntilPlaybackLeavesThePosition() = runTest {
        var state = SponsorPlaybackState(id, 1_000, isPlaying = true)
        val actions = mutableListOf<SponsorSkipAction>()
        val session = SponsorBlockSession(SponsorSegmentsSource { _, _ -> segments })
        backgroundScope.launch { session.run({ state }, { _, action -> actions += action }) }
        advanceTimeBy(1_500); runCurrent()
        assertEquals(listOf<SponsorSkipAction>(SponsorSkipAction.Seek(8_000)), actions)
        state = state.copy(positionMs = 10_000)
        advanceTimeBy(500); runCurrent()
        state = state.copy(positionMs = 1_000)
        advanceTimeBy(500); runCurrent()
        assertEquals(2, actions.size)
    }

    @Test fun anOldLookupReturningAfterCancellationCannotSkipTheNewTrack() = runTest {
        var state = SponsorPlaybackState(id, 1_000, isPlaying = true)
        val lookedUp = mutableListOf<String>()
        val actions = mutableListOf<SponsorSkipAction>()
        val session = SponsorBlockSession(SponsorSegmentsSource { video, _ ->
            lookedUp += video
            if (video == id) {
                try { awaitCancellation() } catch (_: CancellationException) { segments }
            } else emptyList()
        })
        backgroundScope.launch { session.run({ state }, { _, action -> actions += action }) }
        runCurrent()
        state = state.copy(videoId = "newtrack123", generation = 1)
        advanceTimeBy(1_000); runCurrent()
        assertEquals(listOf(id, "newtrack123"), lookedUp)
        assertTrue(actions.isEmpty())
    }

    @Test fun failuresNeverStopPlaybackAndAreRetriedOnALaterPlay() = runTest {
        var state = SponsorPlaybackState(id, 1_000, isPlaying = true)
        var lookups = 0
        var actions = 0
        val session = SponsorBlockSession(SponsorSegmentsSource { _, _ ->
            if (++lookups == 1) error("Fixture network failure") else segments
        })
        backgroundScope.launch { session.run({ state }, { _, _ -> actions++ }) }
        advanceTimeBy(1_000); runCurrent()
        assertEquals(0, actions)
        state = state.copy(generation = 1)
        advanceTimeBy(1_000); runCurrent()
        assertEquals(2, lookups)
        assertEquals(1, actions)
    }

    @Test fun disablingDuringALookupCancelsItAndNeverSkips() = runTest {
        var state = SponsorPlaybackState(id, 1_000, isPlaying = true)
        var cancelled = false
        val session = SponsorBlockSession(SponsorSegmentsSource { _, _ ->
            try { awaitCancellation() } finally { cancelled = true }
        })
        backgroundScope.launch { session.run({ state }, { _, _ -> fail("Disabled lookup must not skip") }) }
        runCurrent()
        state = state.copy(videoId = null)
        advanceTimeBy(500); runCurrent()
        assertTrue(cancelled)
    }
}
