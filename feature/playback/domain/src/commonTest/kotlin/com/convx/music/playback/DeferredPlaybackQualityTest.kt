package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeferredPlaybackQualityTest {
    @Test fun selectionWaitsForTheNextTrackAndCanBeChangedOrCancelled() {
        val active = DeferredPlaybackQuality("high")
        val pending = active.select("low", hasCurrentTrack = true)
        assertEquals("high", pending.current)
        assertEquals("high", pending.forTrack(isCurrent = true))
        assertEquals("low", pending.forTrack(isCurrent = false))
        assertEquals("low", pending.nextTrack().current)
        assertEquals("medium", pending.select("medium", true).nextTrack().current)
        assertNull(pending.select("high", true).pending)
    }

    @Test fun selectionAppliesImmediatelyWithoutATrack() {
        assertEquals(DeferredPlaybackQuality("low"), DeferredPlaybackQuality("high").select("low", false))
    }

    @Test fun crossfadeKeepsTheOutgoingQualityWhileTheIncomingTrackUsesTheNewChoice() {
        val state = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "old").select("low", true)
        assertEquals("high", state.forMedia("old"))
        assertEquals("low", state.forMedia("next"))
        val handoff = state.handoff("old", "next")
        assertEquals("high", handoff.forMedia("old"))
        assertEquals("low", handoff.forMedia("next"))
        val later = handoff.select("medium", true)
        assertEquals("high", later.forMedia("old"))
        assertEquals("low", later.forMedia("next"))
        assertEquals("medium", later.forMedia("third"))
        assertEquals("medium", later.finishFade("old", "high").forMedia("old"))
    }

    @Test fun changingQualityDuringTheFadeDoesNotChangeEitherPlayingTrack() {
        val handoff = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "old").handoff("old", "next")
            .select("low", true)
        assertEquals("high", handoff.forMedia("old"))
        assertEquals("high", handoff.forMedia("next"))
        assertEquals("low", handoff.forMedia("third"))
    }

    @Test fun overlappingCopiesOfTheSameSongKeepTheirCurrentRendition() {
        val state = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "song").select("low", true)
        val handoff = state.handoff("song", "song")
        assertEquals("high", handoff.forMedia("song"))
        assertEquals("low", handoff.selection.pending)
        assertEquals("low", handoff.forMedia("next"))
    }

    @Test fun clearingPlaybackAppliesThePendingChoiceBeforeAnotherTrackIsPrepared() {
        val state = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "old").select("low", true)
            .transition(null, advance = false)
        assertEquals("low", state.forMedia("next"))
        assertNull(state.selection.pending)
    }

    @Test fun aFadingTrackKeepsItsQualityIfTheCurrentQueueIsCleared() {
        val state = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "old").select("low", true)
            .handoff("old", "next").transition(null, advance = false)
        assertEquals("high", state.forMedia("old"))
        assertEquals("low", state.forMedia("new"))
    }

    @Test fun metadataChangesKeepTheChoicePendingUntilAnotherTrackStarts() {
        val state = TrackPlaybackQuality(DeferredPlaybackQuality("high"), "old").select("low", true)
        val sameTrack = state.transition("old", advance = false)
        assertEquals("high", sameTrack.forMedia("old"))
        assertEquals("low", sameTrack.selection.pending)
        val nextTrack = sameTrack.transition("next", advance = true)
        assertEquals("low", nextTrack.forMedia("next"))
        assertNull(nextTrack.selection.pending)
    }
}
