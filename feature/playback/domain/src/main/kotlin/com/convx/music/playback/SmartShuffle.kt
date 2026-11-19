package com.convx.music.playback

import kotlin.random.Random

/** Shuffle that avoids the same artist twice in a row; a null artist key never counts as a repeat. */
object SmartShuffle {

    fun create(artistKeys: List<String?>, currentIndex: Int = -1, random: Random = Random.Default): IntArray {
        if (artistKeys.isEmpty()) return intArrayOf()

        val groups = LinkedHashMap<Any, ArrayDeque<Int>>()
        var unknown = 0
        artistKeys.forEachIndexed { index, key ->
            if (index == currentIndex) return@forEachIndexed
            groups.getOrPut(key ?: UnknownArtist(unknown++)) { ArrayDeque() }.add(index)
        }
        groups.values.forEach { it.shuffle(random) }

        val order = IntArray(artistKeys.size)
        var next = 0
        var last: Any? = null
        if (currentIndex in artistKeys.indices) {
            order[next++] = currentIndex
            last = artistKeys[currentIndex]
        }

        while (next < order.size) {
            val remaining = order.size - next
            val candidates = groups.filter { (key, songs) -> songs.isNotEmpty() && key != last }
            val pool = candidates.ifEmpty { groups.filter { it.value.isNotEmpty() } }
            // An artist with more than half the remaining songs must be placed now or it will clash later.
            val forced = pool.entries.firstOrNull { it.value.size * 2 > remaining }
            val chosen = forced ?: weightedPick(pool.entries.toList(), random)
            order[next++] = chosen.value.removeFirst()
            last = chosen.key
        }
        return order
    }

    private fun weightedPick(
        entries: List<Map.Entry<Any, ArrayDeque<Int>>>,
        random: Random,
    ): Map.Entry<Any, ArrayDeque<Int>> {
        var ticket = random.nextInt(entries.sumOf { it.value.size })
        for (entry in entries) {
            ticket -= entry.value.size
            if (ticket < 0) return entry
        }
        return entries.last()
    }

    private data class UnknownArtist(val id: Int)
}
