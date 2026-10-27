package com.convx.music.playback

import kotlin.random.Random

/** Keeps one shuffle cycle stable so next/previous follow the same queue order. */
class PlaybackQueueNavigator(
    private val random: Random = Random.Default,
) {
    private var shuffleEnabled = false
    private var order = intArrayOf()

    fun replaceQueue(itemCount: Int, currentIndex: Int, shuffleEnabled: Boolean) {
        this.shuffleEnabled = shuffleEnabled
        order = createOrder(itemCount, currentIndex, shuffleEnabled)
    }

    fun setShuffleEnabled(enabled: Boolean, itemCount: Int, currentIndex: Int) {
        replaceQueue(itemCount, currentIndex, enabled)
    }

    fun append(previousCount: Int, addedCount: Int, shuffleEnabled: Boolean) {
        if (addedCount <= 0) return
        if (!shuffleEnabled) {
            replaceQueue(previousCount + addedCount, currentIndex = -1, shuffleEnabled = false)
            return
        }
        if (!this.shuffleEnabled || order.size != previousCount) {
            replaceQueue(previousCount + addedCount, currentIndex = -1, shuffleEnabled = true)
            return
        }

        val additions = (previousCount until previousCount + addedCount).toList().shuffled(random)
        order += additions.toIntArray()
    }

    fun remove(index: Int, newItemCount: Int, shuffleEnabled: Boolean) {
        if (!shuffleEnabled) {
            replaceQueue(newItemCount, currentIndex = -1, shuffleEnabled = false)
            return
        }
        if (!this.shuffleEnabled || order.size != newItemCount + 1) {
            replaceQueue(newItemCount, currentIndex = -1, shuffleEnabled = true)
            return
        }

        order = order.asSequence()
            .filter { it != index }
            .map { if (it > index) it - 1 else it }
            .toList()
            .toIntArray()
    }

    fun move(fromIndex: Int, toIndex: Int, itemCount: Int, shuffleEnabled: Boolean) {
        if (!shuffleEnabled) {
            replaceQueue(itemCount, currentIndex = -1, shuffleEnabled = false)
            return
        }
        if (!this.shuffleEnabled || order.size != itemCount) {
            replaceQueue(itemCount, currentIndex = -1, shuffleEnabled = true)
            return
        }

        val newPositionToOldIndex = moveQueueIndices((0 until itemCount).toList(), fromIndex, toIndex)
        val oldIndexToNewPosition = IntArray(itemCount)
        newPositionToOldIndex.forEachIndexed { newPosition, oldIndex ->
            oldIndexToNewPosition[oldIndex] = newPosition
        }
        order = order.map(oldIndexToNewPosition::get).toIntArray()
    }

    fun truncate(newItemCount: Int, shuffleEnabled: Boolean) {
        if (!shuffleEnabled) {
            replaceQueue(newItemCount, currentIndex = -1, shuffleEnabled = false)
            return
        }
        if (!this.shuffleEnabled || order.size < newItemCount) {
            replaceQueue(newItemCount, currentIndex = -1, shuffleEnabled = true)
            return
        }

        order = order.filter { it in 0 until newItemCount }.toIntArray()
        if (order.size != newItemCount) {
            replaceQueue(newItemCount, currentIndex = -1, shuffleEnabled = true)
        }
    }

    /** Places the first explicitly selected item at the start of a new shuffle cycle. */
    fun trackSelected(index: Int, currentIndex: Int, itemCount: Int, shuffleEnabled: Boolean) {
        if (index !in 0 until itemCount) return
        if (currentIndex !in 0 until itemCount || this.shuffleEnabled != shuffleEnabled || order.size != itemCount) {
            replaceQueue(itemCount, currentIndex = index, shuffleEnabled = shuffleEnabled)
        }
    }

    fun nextIndex(
        currentIndex: Int,
        itemCount: Int,
        forward: Boolean,
        repeatMode: PlaybackRepeatMode,
        shuffleEnabled: Boolean,
    ): Int? {
        if (!shuffleEnabled) {
            return PlaybackQueuePolicy.nextIndex(
                currentIndex = currentIndex,
                itemCount = itemCount,
                forward = forward,
                repeatMode = repeatMode,
                shuffleEnabled = false,
            )
        }
        if (itemCount <= 0) return null
        if (!this.shuffleEnabled || order.size != itemCount) {
            replaceQueue(itemCount, currentIndex, shuffleEnabled = true)
        }

        val currentOrderIndex = order.indexOf(currentIndex)
        val candidateOrderIndex = when {
            currentOrderIndex < 0 && forward -> 0
            currentOrderIndex < 0 && repeatMode == PlaybackRepeatMode.ALL -> order.lastIndex
            currentOrderIndex < 0 -> return null
            else -> currentOrderIndex + if (forward) 1 else -1
        }
        val resolvedOrderIndex = when {
            candidateOrderIndex in order.indices -> candidateOrderIndex
            repeatMode != PlaybackRepeatMode.ALL -> return null
            forward -> 0
            else -> order.lastIndex
        }
        return order.getOrNull(resolvedOrderIndex)
    }

    private fun createOrder(itemCount: Int, currentIndex: Int, shuffleEnabled: Boolean): IntArray {
        if (itemCount <= 0) return intArrayOf()
        if (!shuffleEnabled) return IntArray(itemCount) { it }
        return ShuffleOrderPolicy.create(
            currentIndex = currentIndex,
            totalCount = itemCount,
            originalQueueSize = itemCount,
            shufflePlaylistFirst = false,
            random = random,
        )
    }
}
