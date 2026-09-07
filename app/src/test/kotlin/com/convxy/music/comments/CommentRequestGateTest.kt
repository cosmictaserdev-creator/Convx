package com.convxy.music.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Stale-response rejection: the race where the user skips three tracks in two seconds and the
 * responses land in whatever order the network likes.
 *
 * Tested directly rather than through the ViewModel because this is the part of the feature that most
 * needs to be provably correct, and it is only provable if it can be exercised without a dispatcher.
 */
class CommentRequestGateTest {

    @Test
    fun `a result for a superseded track is rejected`() {
        val gate = CommentRequestGate()
        val first = gate.begin("a")
        val second = gate.begin("b")

        assertTrue(gate.isCurrent(second, "b"))
        assertTrue("the older fetch must not be allowed to land", !gate.isCurrent(first, "a"))
        assertEquals("b", gate.currentTrackId)
    }

    @Test
    fun `re-binding to the same track keeps an in-flight token valid`() {
        // Recomposition, re-opening the sheet and configuration changes all re-bind. Each of those
        // bumping the generation would restart work or discard a result that had already arrived.
        val gate = CommentRequestGate()
        val token = gate.begin("a")

        assertEquals(token, gate.begin("a"))
        assertEquals(token, gate.begin("a"))
        assertEquals(1, gate.currentGeneration)
        assertTrue(gate.isCurrent(token, "a"))
    }

    @Test
    fun `force bumps the generation for the same track`() {
        val gate = CommentRequestGate()
        val token = gate.begin("a")
        val forced = gate.begin("a", force = true)

        assertNotEquals(token, forced)
        assertTrue(gate.isCurrent(forced, "a"))
        assertTrue(!gate.isCurrent(token, "a"))
    }

    @Test
    fun `a token presented with the wrong track id is rejected`() {
        // Both halves matter: the generation catches "superseded", the id catches a token reused
        // across a same-track rebind that was subsequently pointed elsewhere.
        val gate = CommentRequestGate()
        val token = gate.begin("a")

        assertTrue(!gate.isCurrent(token, "b"))
        assertTrue(!gate.isCurrent(token, null))
    }

    @Test
    fun `clear rejects everything still in flight`() {
        val gate = CommentRequestGate()
        val token = gate.begin("a")
        gate.clear()

        assertNull(gate.currentTrackId)
        assertTrue(!gate.isCurrent(token, "a"))
    }

    @Test
    fun `returning to a track gets a fresh token`() {
        val gate = CommentRequestGate()
        val first = gate.begin("a")
        gate.begin("b")
        val again = gate.begin("a")

        assertNotEquals(first, again)
        assertTrue(gate.isCurrent(again, "a"))
        assertTrue("a token from the previous visit to this track is stale", !gate.isCurrent(first, "a"))
    }

    @Test
    fun `binding to nothing playing leaves no acceptable token`() {
        val gate = CommentRequestGate()
        gate.begin("a")
        val token = gate.begin(null)

        assertNull(gate.currentTrackId)
        assertTrue(!gate.isCurrent(token, null))
        assertTrue(!gate.isCurrent(token, "a"))
    }

    @Test
    fun `generations only ever move forward`() {
        val gate = CommentRequestGate()
        var previous = gate.currentGeneration
        repeat(20) { index ->
            val now = gate.begin("track-${index % 3}")
            assertTrue("$now followed $previous", now >= previous)
            previous = now
        }
    }
}
