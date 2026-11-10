/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

/** Unsigned energy bytes: one per second, normalized to a peak of 255. */
object EnergyProfile {

    private const val EDGE_SECONDS = 8

    private const val DROP_FRACTION = 0.6f

    private const val MIN_USABLE_SECONDS = 20

    fun isUsable(profile: ByteArray): Boolean = profile.size >= MIN_USABLE_SECONDS

    fun coversOutro(profile: ByteArray, trackDurationMs: Long): Boolean {
        if (trackDurationMs <= 0L || !isUsable(profile)) return false
        return profile.size * 1000L >= trackDurationMs - COVERAGE_SLACK_MS
    }

    fun outroEnergy(profile: ByteArray): Float = edgeEnergy(profile, fromEnd = true)

    fun introEnergy(profile: ByteArray): Float = edgeEnergy(profile, fromEnd = false)

    fun outroDropSecond(profile: ByteArray): Int {
        if (!isUsable(profile)) return -1
        val reference = referenceLevel(profile)
        if (reference <= 0f) return -1

        val threshold = reference * DROP_FRACTION
        val searchStart = profile.size * 2 / 3
        var lastLoudSecond = profile.lastIndex
        while (lastLoudSecond >= searchStart && level(profile, lastLoudSecond) < reference) lastLoudSecond--

        for (second in maxOf(searchStart, lastLoudSecond + 1) until profile.size) {
            if (movingMean(profile, second, EDGE_SECONDS / 2) >= threshold) continue
            return second
        }
        return -1
    }

    fun introRiseSecond(profile: ByteArray): Int {
        if (!isUsable(profile)) return -1
        val reference = referenceLevel(profile)
        if (reference <= 0f) return -1

        val threshold = reference * DROP_FRACTION
        val searchEnd = profile.size / 3
        if (level(profile, 0) >= threshold) return -1

        for (second in 1 until searchEnd) {
            if (movingMean(profile, second, EDGE_SECONDS / 2) >= threshold) return second
        }
        return -1
    }

    private fun edgeEnergy(profile: ByteArray, fromEnd: Boolean): Float {
        if (!isUsable(profile)) return 0f
        val reference = referenceLevel(profile)
        if (reference <= 0f) return 0f

        val indices = if (fromEnd) {
            (profile.size - EDGE_SECONDS) until profile.size
        } else {
            0 until EDGE_SECONDS
        }
        var sum = 0f
        for (i in indices) sum += level(profile, i)
        return (sum / EDGE_SECONDS / reference).coerceIn(0f, 1f)
    }

    private fun referenceLevel(profile: ByteArray): Float {
        val counts = IntArray(256)
        for (value in profile) counts[value.toInt() and 0xFF]++
        val rank = profile.size * 3 / 4
        var cumulative = 0
        for (value in counts.indices) {
            cumulative += counts[value]
            if (cumulative > rank) return value.toFloat()
        }
        return 0f
    }

    private fun movingMean(profile: ByteArray, center: Int, radius: Int): Float {
        var sum = 0f
        var count = 0
        for (i in (center - radius)..(center + radius)) {
            if (i in profile.indices) {
                sum += level(profile, i)
                count++
            }
        }
        return if (count == 0) 0f else sum / count
    }

    private fun level(profile: ByteArray, index: Int): Float =
        (profile[index].toInt() and 0xFF).toFloat()

    private const val COVERAGE_SLACK_MS = 5_000L
}
