/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.ShuffleOrder

/**
 * A shuffle order that keeps what was heard behind the playing song, so Previous goes back to the song
 * played last. Media3's default order slots songs added later at random places, often among songs
 * already heard. Here songs added straight after the playing one ("play next") come next and anything
 * else goes at the end. [order] lists queue indices in play order; [current] is the playing queue index.
 */
@UnstableApi
class HistoryShuffleOrder(private val order: IntArray, private val current: () -> Int) : ShuffleOrder {
    private val positions = IntArray(order.size) { -1 }.also { positions ->
        for ((position, index) in order.withIndex()) if (index in positions.indices) positions[index] = position
    }

    private fun positionOf(index: Int) = positions.getOrElse(index) { -1 }

    override fun getLength() = order.size

    override fun getNextIndex(index: Int): Int {
        val position = positionOf(index)
        return if (position < 0 || position + 1 >= order.size) C.INDEX_UNSET else order[position + 1]
    }

    override fun getPreviousIndex(index: Int): Int {
        val position = positionOf(index)
        return if (position <= 0) C.INDEX_UNSET else order[position - 1]
    }

    override fun getLastIndex() = if (order.isEmpty()) C.INDEX_UNSET else order.last()

    override fun getFirstIndex() = if (order.isEmpty()) C.INDEX_UNSET else order.first()

    override fun cloneAndInsert(insertionIndex: Int, insertionCount: Int): ShuffleOrder {
        if (insertionCount <= 0) return this
        val at = insertionIndex.coerceIn(0, order.size)
        val added = (at until at + insertionCount).toList()
        if (order.isEmpty()) return HistoryShuffleOrder(added.shuffled().toIntArray(), current)
        val shifted = order.map { if (it >= at) it + insertionCount else it }.toMutableList()
        // The playing index still refers to the queue before the insertion.
        val playing = current().takeIf { positionOf(it) >= 0 }
        val before = at - 1
        val place = when {
            playing == null -> shifted.size
            at == playing + 1 -> positionOf(playing) + 1
            at >= order.size -> shifted.size
            before >= 0 && positionOf(before) > positionOf(playing) -> positionOf(before) + 1
            else -> shifted.size
        }
        shifted.addAll(place, added)
        return HistoryShuffleOrder(shifted.toIntArray(), current)
    }

    override fun cloneAndRemove(indexFrom: Int, indexToExclusive: Int): ShuffleOrder {
        val from = indexFrom.coerceAtLeast(0)
        val to = indexToExclusive.coerceAtMost(order.size)
        if (to <= from) return this
        val count = to - from
        val kept = order.filter { it < from || it >= to }.map { if (it >= to) it - count else it }
        return HistoryShuffleOrder(kept.toIntArray(), current)
    }

    /** The order after queue items move; Media3 1.7 moves by remove then insert, so it does not call this itself. */
    fun cloneAndMove(indexFrom: Int, indexToExclusive: Int, newIndexFrom: Int): HistoryShuffleOrder {
        if (indexFrom < 0 || indexToExclusive > order.size || indexToExclusive <= indexFrom) return this
        val queue = order.indices.toMutableList()
        val moved = queue.subList(indexFrom, indexToExclusive).toList()
        queue.removeAll(moved)
        queue.addAll(newIndexFrom.coerceIn(0, queue.size), moved)
        val newIndexOf = IntArray(order.size).also { for ((newIndex, old) in queue.withIndex()) it[old] = newIndex }
        return HistoryShuffleOrder(IntArray(order.size) { newIndexOf[order[it]] }, current)
    }

    fun cloneAndSet(insertionCount: Int, startIndex: Int): HistoryShuffleOrder = shuffled(insertionCount, startIndex, current)

    override fun cloneAndClear(): ShuffleOrder = HistoryShuffleOrder(IntArray(0), current)

    /** [target] moved to straight after [from]: jumping to a song makes it the next one played. */
    fun playingNext(from: Int, target: Int): HistoryShuffleOrder {
        if (positionOf(from) < 0 || positionOf(target) < 0 || from == target) return this
        val list = order.toMutableList()
        list.remove(target)
        list.add(list.indexOf(from) + 1, target)
        return HistoryShuffleOrder(list.toIntArray(), current)
    }

    /** [index] moved to just after (or before) [beside], as when a song is dragged in the shuffled queue. */
    fun placed(index: Int, beside: Int, after: Boolean): HistoryShuffleOrder {
        if (positionOf(index) < 0 || positionOf(beside) < 0 || index == beside) return this
        val list = order.toMutableList()
        list.remove(index)
        list.add(list.indexOf(beside) + if (after) 1 else 0, index)
        return HistoryShuffleOrder(list.toIntArray(), current)
    }

    companion object {
        /** A fresh random order of [count] songs with [first], when it is one of them, at the start. */
        fun shuffled(count: Int, first: Int, current: () -> Int): HistoryShuffleOrder {
            if (count <= 0) return HistoryShuffleOrder(IntArray(0), current)
            val rest = (0 until count).filter { it != first }.shuffled()
            return HistoryShuffleOrder((if (first in 0 until count) listOf(first) + rest else rest).toIntArray(), current)
        }
    }
}
