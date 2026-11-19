package com.convx.music.playback

import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasSessionTest {
    @Test fun rapidSkipsRejectResultsAndFramesFromEveryEarlierEpoch() {
        val first = CanvasTrack("one", 0)
        val second = CanvasTrack("two", 1)
        val third = CanvasTrack("three", 2)
        var session = CanvasSession(first).resolve(first, enabled = true)
        session = session.resolve(second, enabled = true).resolve(third, enabled = true)
        session = session.resolved(second, found = true).resolved(first, found = true)
        assertEquals(CanvasPhase.Resolving, session.phase)
        session = session.resolved(third, found = true)
        assertEquals(CanvasPhase.Found, session.firstFrame(second).phase)
        assertEquals(CanvasPhase.Showing, session.firstFrame(third).phase)
    }

    @Test fun returningToTheSameMediaIdDoesNotAcceptTheOriginalResult() {
        val original = CanvasTrack("one", 0)
        val repeated = CanvasTrack("one", 2)
        val session = CanvasSession(original).resolve(repeated, enabled = true)
        assertEquals(session, session.resolved(original, found = true))
        assertEquals(CanvasPhase.Thumbnail, session.resolved(repeated, found = false).phase)
    }

    @Test fun disablingRejectsResultsAndFramesForTheCurrentTrack() {
        val track = CanvasTrack("one", 0)
        val disabled = CanvasSession(track).resolve(track, enabled = false)
        assertEquals(disabled, disabled.resolved(track, found = true))
        assertEquals(disabled, disabled.firstFrame(track))
    }
}
