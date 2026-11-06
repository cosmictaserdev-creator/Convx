package com.convx.music.jam

import org.junit.Assert.assertEquals
import org.junit.Test

class JamQueueDiffTest {
    private fun diff(party: String, local: String) =
        JamQueueDiff.between(party.map(Char::toString), local.map(Char::toString))

    @Test
    fun sameListNeedsNothing() = assertEquals(QueueChange.None, diff("abc", "abc"))

    @Test
    fun emptyingTheQueueClearsIt() = assertEquals(QueueChange.Clear, diff("abc", ""))

    @Test
    fun songsAddedAtTheEndAreQueued() = assertEquals(QueueChange.Add(2, 4, playNext = false), diff("ab", "abcd"))

    @Test
    fun songsAddedAtTheFrontPlayNext() = assertEquals(QueueChange.Add(0, 2, playNext = true), diff("ab", "cdab"))

    @Test
    fun oneSongRemoved() = assertEquals(QueueChange.Remove(1, "b"), diff("abc", "ac"))

    @Test
    fun lastSongRemoved() = assertEquals(QueueChange.Remove(2, "c"), diff("abc", "ab"))

    @Test
    fun songMovedDownAndUp() {
        assertEquals(QueueChange.Move(0, 2, "a"), diff("abc", "bca"))
        assertEquals(QueueChange.Move(2, 0, "c"), diff("abc", "cab"))
    }

    @Test
    fun repeatedSongsFallBackToTheWholeList() {
        assertEquals(QueueChange.Replace, diff("aba", "ba"))
        assertEquals(QueueChange.Replace, diff("abc", "xyz"))
    }
}
