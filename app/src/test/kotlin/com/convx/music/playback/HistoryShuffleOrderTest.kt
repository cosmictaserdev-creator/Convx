/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import androidx.media3.common.C
import androidx.media3.exoplayer.source.ShuffleOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class HistoryShuffleOrderTest {
    private fun ShuffleOrder.list(): List<Int> =
        generateSequence(firstIndex.takeIf { it != C.INDEX_UNSET }) { getNextIndex(it).takeIf { next -> next != C.INDEX_UNSET } }.toList()

    private var current = 0
    private val start = HistoryShuffleOrder(intArrayOf(2, 0, 4, 1, 3)) { current }

    @Test
    fun aDraggedSongMovesInThePlayOrder() {
        val up = start.placed(3, beside = 4, after = false)
        assertEquals(listOf(2, 0, 3, 4, 1), up.list())
        assertEquals(listOf(2, 0, 3, 1, 4), up.placed(4, beside = 1, after = true).list())
        assertEquals(2, up.getPreviousIndex(0))
        assertEquals(start.list(), start.placed(3, beside = 9, after = true).list())
    }

    @Test
    fun songsAddedAtTheEndGoAfterEverything() {
        val added = start.cloneAndInsert(5, 2)
        assertEquals(listOf(2, 0, 4, 1, 3, 5, 6), added.list())
        assertEquals(2, added.getPreviousIndex(0))
    }

    @Test
    fun playNextComesStraightAfterThePlayingSong() {
        val added = HistoryShuffleOrder(intArrayOf(2, 0, 4, 1, 3, 5, 6)) { current }.cloneAndInsert(1, 1)
        assertEquals(listOf(3, 0, 1, 5, 2, 4, 6, 7), added.list())
    }

    @Test
    fun songsInsertedBeforeThePlayingOneGoAtTheEnd() {
        assertEquals(listOf(3, 1, 5, 2, 4, 0), start.cloneAndInsert(0, 1).list())
    }

    @Test
    fun jumpingToASongMakesItNext() {
        val jumped = start.playingNext(0, 3)
        assertEquals(listOf(2, 0, 3, 4, 1), jumped.list())
        assertEquals(0, jumped.getPreviousIndex(3))
    }

    @Test
    fun removingAndMovingKeepTheOrder() {
        assertEquals(listOf(1, 3, 0, 2), start.cloneAndRemove(0, 1).list())
        assertEquals(listOf(1, 4, 3, 0, 2), start.cloneAndMove(0, 1, 4).list())
    }

    @Test
    fun aNewQueueStartsFromItsFirstSong() {
        val set = start.cloneAndSet(6, 3)
        assertEquals(3, set.firstIndex)
        assertEquals((0 until 6).toSet(), set.list().toSet())
    }

    @Test
    fun insertingNothingChangesNothing() {
        assertSame(start, start.cloneAndInsert(2, 0))
        assertSame(start, start.cloneAndInsert(2, -1))
    }

    @Test
    fun insertingIntoAnEmptyQueueUsesEveryNewIndex() {
        val added = HistoryShuffleOrder(IntArray(0)) { 0 }.cloneAndInsert(0, 4)
        assertEquals(setOf(0, 1, 2, 3), added.list().toSet())
        assertEquals(4, added.length)
    }

    @Test
    fun anInsertionIndexPastTheEndIsTreatedAsTheEnd() {
        assertEquals(listOf(2, 0, 4, 1, 3, 5), start.cloneAndInsert(99, 1).list())
    }

    @Test
    fun removingEverythingOrAnEmptyRangeIsSafe() {
        assertEquals(emptyList<Int>(), start.cloneAndRemove(0, 5).list())
        assertSame(start, start.cloneAndRemove(3, 3))
        assertSame(start, start.cloneAndRemove(4, 2))
        assertEquals(start.list(), start.cloneAndRemove(5, 9).list())
    }

    @Test
    fun movingABlockKeepsEverySongOnce() {
        val moved = start.cloneAndMove(1, 3, 0)
        assertEquals((0 until 5).toSet(), moved.list().toSet())
        assertEquals(5, moved.length)
    }

    @Test
    fun invalidMovesLeaveTheOrderAlone() {
        assertSame(start, start.cloneAndMove(-1, 2, 0))
        assertSame(start, start.cloneAndMove(1, 9, 0))
        assertSame(start, start.cloneAndMove(3, 3, 0))
    }

    @Test
    fun neighbourLookupsOutsideTheOrderReturnUnset() {
        assertEquals(C.INDEX_UNSET, start.getNextIndex(3))
        assertEquals(C.INDEX_UNSET, start.getPreviousIndex(2))
        assertEquals(C.INDEX_UNSET, start.getNextIndex(99))
        assertEquals(C.INDEX_UNSET, start.getPreviousIndex(-1))
    }

    @Test
    fun anEmptyOrderHasNoFirstOrLast() {
        val empty = HistoryShuffleOrder(IntArray(0)) { 0 }
        assertEquals(C.INDEX_UNSET, empty.firstIndex)
        assertEquals(C.INDEX_UNSET, empty.lastIndex)
        assertEquals(0, empty.length)
        assertEquals(0, start.cloneAndClear().length)
    }

    @Test
    fun aNewQueueOfZeroSongsIsEmptyAndAnUnknownFirstStillShuffles() {
        assertEquals(0, start.cloneAndSet(0, 0).length)
        assertEquals((0 until 4).toSet(), start.cloneAndSet(4, 9).list().toSet())
    }
}
