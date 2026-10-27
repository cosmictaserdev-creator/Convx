package com.convx.music.playback

import kotlin.random.Random

/** Builds playback order indices without depending on a player implementation. */
object ShuffleOrderPolicy {

    fun create(
        currentIndex: Int,
        totalCount: Int,
        originalQueueSize: Int,
        shufflePlaylistFirst: Boolean,
        random: Random = Random.Default,
    ): IntArray {
        if (totalCount == 0) return intArrayOf()

        return if (shufflePlaylistFirst && originalQueueSize in 1 until totalCount) {
            createPlaylistFirstOrder(currentIndex, totalCount, originalQueueSize, random)
        } else {
            createCurrentFirstOrder(currentIndex, totalCount, random)
        }
    }

    private fun createPlaylistFirstOrder(
        currentIndex: Int,
        totalCount: Int,
        originalQueueSize: Int,
        random: Random,
    ): IntArray {
        val originalIndices = (0 until originalQueueSize)
            .filterTo(mutableListOf()) { it != currentIndex }
        val addedIndices = (originalQueueSize until totalCount)
            .filterTo(mutableListOf()) { it != currentIndex }

        originalIndices.shuffle(random)
        addedIndices.shuffle(random)

        val shuffledIndices = IntArray(totalCount)
        var nextIndex = 0
        shuffledIndices[nextIndex++] = currentIndex
        originalIndices.forEach { shuffledIndices[nextIndex++] = it }
        addedIndices.forEach { shuffledIndices[nextIndex++] = it }
        return shuffledIndices
    }

    private fun createCurrentFirstOrder(
        currentIndex: Int,
        totalCount: Int,
        random: Random,
    ): IntArray {
        val shuffledIndices = IntArray(totalCount) { it }
        shuffledIndices.shuffle(random)

        val currentIndexInOrder = shuffledIndices.indexOf(currentIndex)
        if (currentIndexInOrder != -1) {
            val firstIndex = shuffledIndices[0]
            shuffledIndices[0] = shuffledIndices[currentIndexInOrder]
            shuffledIndices[currentIndexInOrder] = firstIndex
        }
        return shuffledIndices
    }
}
