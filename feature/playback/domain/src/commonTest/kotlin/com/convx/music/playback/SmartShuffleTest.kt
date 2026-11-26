package com.convx.music.playback

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SmartShuffleTest {
    private fun adjacentRepeats(keys: List<String?>, order: IntArray): Int =
        order.toList().zipWithNext().count { (a, b) -> keys[a] != null && keys[a] == keys[b] }

    @Test
    fun emptyAndSingleQueues() {
        assertContentEquals(intArrayOf(), SmartShuffle.create(emptyList()))
        assertContentEquals(intArrayOf(0), SmartShuffle.create(listOf("a")))
    }

    @Test
    fun everySongPlaysExactlyOnce() {
        val keys = listOf("a", "a", "b", null, "c", "a", null)
        repeat(50) { seed ->
            val order = SmartShuffle.create(keys, random = Random(seed))
            assertEquals(keys.indices.toList(), order.sorted())
        }
    }

    @Test
    fun theCurrentSongPlaysFirst() {
        val keys = listOf("a", "b", "c", "d", "e")
        repeat(20) { seed ->
            assertEquals(3, SmartShuffle.create(keys, currentIndex = 3, random = Random(seed)).first())
        }
    }

    @Test
    fun anOutOfRangeCurrentIndexIsIgnored() {
        val keys = listOf("a", "b", "c")
        assertEquals(keys.indices.toList(), SmartShuffle.create(keys, currentIndex = 9).sorted())
        assertEquals(keys.indices.toList(), SmartShuffle.create(keys, currentIndex = -1).sorted())
    }

    @Test
    fun neverRepeatsAnArtistWhenAnArrangementExists() {
        val shapes = listOf(
            listOf("a", "a", "b"),
            listOf("a", "a", "a", "b", "b"),
            listOf("a", "a", "a", "b", "b", "b"),
            listOf("a", "b", "c", "a", "b", "c", "a"),
            listOf("a", "a", "b", "c", "d", "e", "f"),
        )
        for (keys in shapes) {
            repeat(200) { seed ->
                val order = SmartShuffle.create(keys, random = Random(seed))
                assertEquals(0, adjacentRepeats(keys, order), "keys=$keys seed=$seed order=${order.toList()}")
            }
        }
    }

    @Test
    fun keepsRepeatsToTheUnavoidableMinimumWhenOneArtistDominates() {
        val keys = listOf("a", "a", "a", "a", "b")
        val order = SmartShuffle.create(keys, random = Random(1))
        // Four of five by one artist: the lone other song splits them into two runs, leaving two repeats.
        assertEquals(2, adjacentRepeats(keys, order), "order=${order.toList()}")
    }

    @Test
    fun unknownArtistsNeverCountAsRepeats() {
        val keys = listOf<String?>(null, null, null, null)
        repeat(20) { seed ->
            assertEquals(keys.indices.toList(), SmartShuffle.create(keys, random = Random(seed)).sorted())
        }
    }

    @Test
    fun theSameSeedGivesTheSameOrder() {
        val keys = listOf("a", "b", "c", "a", "b", "c")
        assertContentEquals(
            SmartShuffle.create(keys, random = Random(7)),
            SmartShuffle.create(keys, random = Random(7)),
        )
    }

    @Test
    fun differentSeedsActuallyShuffle() {
        val keys = List(12) { "artist${it % 4}" }
        val orders = (0 until 20).map { SmartShuffle.create(keys, random = Random(it)).toList() }.toSet()
        assertTrue(orders.size > 5, "only ${orders.size} distinct orders")
    }
}
