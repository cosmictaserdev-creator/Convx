/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.dj

/**
 * Readers for the per-second energy curve stored on `SongAnalysisEntity`.
 *
 * Everything here answers one of two questions: *how does this track end* and
 * *how does this track begin*. Those pick the mix-out point and the transition
 * style — a track that ends at full energy needs something to hide the seam, a
 * track that fades out over 20 s hides it by itself.
 *
 * Values are unsigned bytes packed into a `ByteArray`, one per second, scaled
 * so the loudest second is 255.
 */
object EnergyProfile {

    /** Seconds averaged at each end when characterising intro/outro. */
    private const val EDGE_SECONDS = 8

    /** Fraction of a track's reference level below which it counts as "down". */
    private const val DROP_FRACTION = 0.6f

    /** A profile shorter than this tells us nothing useful. */
    private const val MIN_USABLE_SECONDS = 20

    fun isUsable(profile: ByteArray): Boolean = profile.size >= MIN_USABLE_SECONDS

    /** True when the profile reaches the end of the track, so its last seconds
     *  really are the outro rather than wherever analysis happened to stop. */
    fun coversOutro(profile: ByteArray, trackDurationMs: Long): Boolean {
        if (trackDurationMs <= 0L || !isUsable(profile)) return false
        return profile.size * 1000L >= trackDurationMs - COVERAGE_SLACK_MS
    }

    /**
     * How loud the track's final seconds are relative to its own typical level.
     * ~1 means it ends at full tilt (needs help to mix out of), ~0 means it
     * fades to nothing (mixes out by itself).
     */
    fun outroEnergy(profile: ByteArray): Float = edgeEnergy(profile, fromEnd = true)

    /** How loud the track's opening seconds are. ~1 means it starts on a full
     *  drum groove, ~0 means a quiet or ambient intro. */
    fun introEnergy(profile: ByteArray): Float = edgeEnergy(profile, fromEnd = false)

    /**
     * Second at which the track's energy drops away and stays down, searching
     * the last third only, or -1 if it never does. This is the natural place to
     * start mixing out — the outro or final breakdown — and beats a flat
     * "N seconds before the end" every time.
     */
    fun outroDropSecond(profile: ByteArray): Int {
        if (!isUsable(profile)) return -1
        val reference = referenceLevel(profile)
        if (reference <= 0f) return -1

        val threshold = reference * DROP_FRACTION
        val searchStart = profile.size * 2 / 3

        for (second in searchStart until profile.size) {
            if (movingMean(profile, second, EDGE_SECONDS / 2) >= threshold) continue
            // Only a real drop if it stays down; a single quiet bar in an
            // outro-less track would otherwise read as the end of the song.
            val staysDown = (second until profile.size).all { level(profile, it) < reference }
            if (staysDown) return second
        }
        return -1
    }

    /**
     * Second at which a quiet intro gives way to the track proper, searching
     * the first third only, or -1 if it never was quiet. Used to mix into the
     * groove rather than into 30 s of pad.
     */
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

    /**
     * The track's own "this is what loud means" level: the 75th percentile
     * rather than the peak, so one crash cymbal doesn't define the scale and a
     * track with a loud chorus and quiet verses isn't judged entirely by its
     * chorus.
     */
    private fun referenceLevel(profile: ByteArray): Float {
        val sorted = IntArray(profile.size) { level(profile, it).toInt() }
        sorted.sort()
        return sorted[(sorted.size * 3 / 4).coerceAtMost(sorted.size - 1)].toFloat()
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
