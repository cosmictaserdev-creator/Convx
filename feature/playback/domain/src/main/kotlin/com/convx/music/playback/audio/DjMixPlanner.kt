/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.audio

import com.convx.music.playback.dj.BeatGrid
import com.convx.music.playback.dj.MusicalKey
import kotlin.math.abs
import kotlin.math.pow

enum class DjMixTier {
    FULL_DJ,
    SMART_CROSSFADE,
    PLAIN_CROSSFADE,
}

data class DjMixPlan(
    val tier: DjMixTier,
    val incomingSpeedAdjustment: Float = 1f,
    val incomingPitchShiftSemitones: Int = 0,
) {
    val incomingPitchAdjustment: Float
        get() = if (incomingPitchShiftSemitones == 0) {
            1f
        } else {
            2.0.pow(incomingPitchShiftSemitones / 12.0).toFloat()
        }
}

object DjMixPlanner {
    private const val MIN_CONFIDENCE_FOR_SMART = 0.20f
    private const val MIN_CONFIDENCE_FOR_FULL = 0.45f

    private const val MAX_FULL_STRETCH_PERCENT = 0.06f

    private const val MAX_SMART_STRETCH_PERCENT = 0.06f

    private val OCTAVE_FACTORS = floatArrayOf(0.5f, 1f, 2f)

    fun plan(
        outgoing: BeatGrid?,
        incoming: BeatGrid?,
        outgoingKey: MusicalKey? = null,
        incomingKey: MusicalKey? = null,
    ): DjMixPlan {
        if (outgoing == null || incoming == null || outgoing.bpm <= 0f || incoming.bpm <= 0f) {
            return DjMixPlan(DjMixTier.PLAIN_CROSSFADE)
        }

        val minConfidence = minOf(outgoing.confidence, incoming.confidence).coerceAtLeast(0f)
        if (minConfidence < MIN_CONFIDENCE_FOR_SMART) return DjMixPlan(DjMixTier.PLAIN_CROSSFADE)

        var bestSpeed = 1f
        var bestStretch = Float.MAX_VALUE
        for (factor in OCTAVE_FACTORS) {
            val speed = outgoing.bpm / (incoming.bpm * factor)
            val stretch = abs(speed - 1f)
            if (stretch < bestStretch) {
                bestStretch = stretch
                bestSpeed = speed
            }
        }

        val harmonic = MusicalKey.compatible(outgoingKey, incomingKey)

        return when {
            minConfidence >= MIN_CONFIDENCE_FOR_FULL &&
                bestStretch <= MAX_FULL_STRETCH_PERCENT &&
                harmonic ->
                DjMixPlan(DjMixTier.FULL_DJ, bestSpeed)

            bestStretch <= MAX_SMART_STRETCH_PERCENT ->
                DjMixPlan(DjMixTier.SMART_CROSSFADE, bestSpeed)

            else -> DjMixPlan(DjMixTier.PLAIN_CROSSFADE)
        }
    }
}
