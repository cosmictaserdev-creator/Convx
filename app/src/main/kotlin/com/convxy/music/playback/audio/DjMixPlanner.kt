/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.audio

import com.convxy.music.playback.dj.BeatGrid
import com.convxy.music.playback.dj.MusicalKey
import kotlin.math.abs

/** Locked 3-tier fallback from the grilling session: never force a treatment
 *  the beat data can't support — a bad auto-mix sounds worse than a plain
 *  crossfade, and that's exactly what tier 3 protects against. */
enum class DjMixTier {
    /** Confident grid on both sides, tempos close enough to stretch without
     *  it sounding wrong — full treatment: EQ sweep + delay + tempo. */
    FULL_DJ,
    /** Beat detected but not confident/compatible enough for the full
     *  treatment — Apple Automix-style: small tempo nudge only. */
    SMART_CROSSFADE,
    /** No usable beat on one or both sides (spoken word, ambient, detection
     *  failed) — today's plain volume crossfade, unchanged. */
    PLAIN_CROSSFADE,
}

data class DjMixPlan(
    val tier: DjMixTier,
    /** Playback speed to apply to the incoming track, 1f = no change.
     *  Only meaningful for FULL_DJ/SMART_CROSSFADE. */
    val incomingSpeedAdjustment: Float = 1f,
    /** Semitones to pitch-shift the incoming track by so the two tracks share a
     *  compatible key. Always -1, 0 or +1; a larger shift is more audible than
     *  the clash it would fix. */
    val incomingPitchShiftSemitones: Int = 0,
) {
    /** Pitch multiplier for `PlaybackParameters(speed, pitch)`. Sonic shifts
     *  pitch independently of speed, so this rides along with the tempo lock. */
    val incomingPitchAdjustment: Float
        get() = if (incomingPitchShiftSemitones == 0) {
            1f
        } else {
            Math.pow(2.0, incomingPitchShiftSemitones / 12.0).toFloat()
        }
}

object DjMixPlanner {
    // Confidence here is a normalized autocorrelation gated by transient
    // density (see BeatTracker), so these are meaningful fractions, not the
    // vote-share of the old histogram — which topped out near 0.5 on a perfect
    // metronome and so could never clear its own 0.55 FULL threshold.
    // Calibrated against the fixtures in BeatTrackerTest; clean percussive
    // material lands at 0.95+, sparse patterns around 0.3-0.5, non-rhythmic
    // material at 0.
    private const val MIN_CONFIDENCE_FOR_SMART = 0.20f
    private const val MIN_CONFIDENCE_FOR_FULL = 0.45f

    /** Stretch tolerance for the full treatment — beyond this the tempo
     *  correction would be audible/unnatural rather than seamless. */
    private const val MAX_FULL_STRETCH_PERCENT = 0.08f

    /** Smart crossfade allows a slightly narrower nudge since there's no EQ/
     *  effects camouflage riding along with it. */
    private const val MAX_SMART_STRETCH_PERCENT = 0.06f

    /** Half-time and double-time readings of the same track are both correct —
     *  a beat tracker cannot tell 160 BPM with a backbeat from 80 BPM, and
     *  neither can a listener. Every beat of the half-time grid is still a real
     *  beat, so treat the octave as free: a track detected at 64 mixes into one
     *  detected at 128 at speed 1.0, not at speed 0.5. Without this the octave
     *  ambiguity alone knocks most real pairs down to a plain crossfade. */
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

        // A semitone of pitch shift can rescue a near-miss; anything further
        // apart is left alone and demoted instead.
        val shift = MusicalKey.matchingShift(outgoingKey, incomingKey)
        val harmonic = shift != 0 ||
            MusicalKey.compatible(outgoingKey, incomingKey)

        return when {
            minConfidence >= MIN_CONFIDENCE_FOR_FULL &&
                bestStretch <= MAX_FULL_STRETCH_PERCENT &&
                harmonic ->
                DjMixPlan(DjMixTier.FULL_DJ, bestSpeed, shift)

            // Beats line up but the keys fight: drop to the tier with no EQ or
            // effects holding the two together, so a dissonant overlap is brief
            // rather than sustained under a long filtered blend.
            bestStretch <= MAX_SMART_STRETCH_PERCENT ->
                DjMixPlan(DjMixTier.SMART_CROSSFADE, bestSpeed, shift)

            else -> DjMixPlan(DjMixTier.PLAIN_CROSSFADE)
        }
    }
}
