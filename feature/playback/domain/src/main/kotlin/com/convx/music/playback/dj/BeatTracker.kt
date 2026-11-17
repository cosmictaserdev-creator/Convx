/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class BeatGrid(
    val bpm: Float,
    /** Absolute track-time anchor, not a phase folded into one beat. */
    val anchorBeatMs: Float,
    val confidence: Float,
) {
    val beatMs: Float get() = if (bpm > 0f) 60_000f / bpm else 0f

    fun beatAtOrAfter(positionMs: Long): Float {
        val beat = beatMs
        if (beat <= 0f) return positionMs.toFloat()
        val beatsAway = ceil((positionMs - anchorBeatMs) / beat.toDouble())
        return anchorBeatMs + (beatsAway * beat).toFloat()
    }

    fun nearestPhraseMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = floor((positionMs - anchorBeatMs) / phrase.toDouble() + 0.5)
        return anchorBeatMs + (phrasesAway * phrase).toFloat()
    }

    fun phraseAtOrBeforeMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = floor((positionMs - anchorBeatMs) / phrase.toDouble())
        return anchorBeatMs + (phrasesAway * phrase).toFloat()
    }

    fun phraseAtOrAfterMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = ceil((positionMs - anchorBeatMs) / phrase.toDouble())
        return anchorBeatMs + (phrasesAway * phrase).toFloat()
    }

    companion object {
        const val BEATS_PER_PHRASE = 16
        val NONE = BeatGrid(0f, 0f, 0f)
    }
}

class BeatTracker(private val sampleRate: Int) {

    private val hopFrames = max(1, sampleRate / 100) // ~10 ms
    private val hopMs = hopFrames * 1000f / sampleRate

    private val lowPassAlpha: Double = run {
        val rc = 1.0 / (2.0 * PI * KICK_BAND_HZ)
        val dt = 1.0 / sampleRate
        dt / (rc + dt)
    }
    private var lowPassState = 0.0

    private var fullAccumulator = 0.0
    private var lowAccumulator = 0.0
    private var framesInHop = 0
    private var previousFullRms = 0.0
    private var previousLowRms = 0.0

    private val fullRms = FloatArray(MAX_ENVELOPE_FRAMES)
    private val lowRms = FloatArray(MAX_ENVELOPE_FRAMES)
    private var envelopeSize = 0

    private var startPositionMs = 0L

    private val secondEnergy = FloatArray(MAX_PROFILE_SECONDS)
    private val secondLowEnergy = FloatArray(MAX_PROFILE_SECONDS)
    private var secondAccumulator = 0.0
    private var secondLowAccumulator = 0.0
    private var hopsInSecond = 0
    private var profileSize = 0

    val isAnalysisComplete: Boolean get() = envelopeSize >= MAX_ENVELOPE_FRAMES

    val analyzedMs: Long get() = (envelopeSize * hopMs).toLong()

    fun beginAt(positionMs: Long) {
        reset()
        startPositionMs = positionMs
    }

    fun accept(sample: Float) {
        if (profileSize >= MAX_PROFILE_SECONDS && envelopeSize >= MAX_ENVELOPE_FRAMES) return

        val value = sample.toDouble()
        fullAccumulator += value * value
        if (profileSize < MAX_PROFILE_SECONDS) {
            lowPassState += lowPassAlpha * (value - lowPassState)
            lowAccumulator += lowPassState * lowPassState
        }
        framesInHop++

        if (framesInHop < hopFrames) return

        val hopFullRms = sqrt(fullAccumulator / framesInHop).toFloat()
        val hopLowRms = if (profileSize < MAX_PROFILE_SECONDS) {
            sqrt(lowAccumulator / framesInHop).toFloat()
        } else {
            0f
        }
        if (envelopeSize < MAX_ENVELOPE_FRAMES) {
            fullRms[envelopeSize] = hopFullRms
            lowRms[envelopeSize] = hopLowRms
            envelopeSize++
        }

        secondAccumulator += hopFullRms
        secondLowAccumulator += hopLowRms
        hopsInSecond++
        if (hopsInSecond >= HOPS_PER_SECOND && profileSize < MAX_PROFILE_SECONDS) {
            secondEnergy[profileSize++] = (secondAccumulator / hopsInSecond).toFloat()
            secondLowEnergy[profileSize - 1] = (secondLowAccumulator / hopsInSecond).toFloat()
            secondAccumulator = 0.0
            secondLowAccumulator = 0.0
            hopsInSecond = 0
        }

        fullAccumulator = 0.0
        lowAccumulator = 0.0
        framesInHop = 0
    }

    fun energyProfile(): ByteArray {
        if (startPositionMs != 0L) return ByteArray(0)
        return normalizedProfile(secondEnergy)
    }

    /** Per-second low-band RMS, normalized to its own peak like [energyProfile]. */
    fun lowEnergyProfile(): ByteArray {
        if (startPositionMs != 0L) return ByteArray(0)
        return normalizedProfile(secondLowEnergy)
    }

    private fun normalizedProfile(profile: FloatArray): ByteArray {
        if (profileSize == 0) return ByteArray(0)
        var peak = 0f
        for (i in 0 until profileSize) if (profile[i] > peak) peak = profile[i]
        if (peak <= 0f) return ByteArray(0)

        return ByteArray(profileSize) { i ->
            ((profile[i] / peak) * 255f).roundToInt().coerceIn(0, 255).toByte()
        }
    }

    fun estimate(): BeatGrid {
        if (envelopeSize < MIN_ENVELOPE_FRAMES) return BeatGrid.NONE

        val envelope = combinedEnvelope()
        val n = envelope.size
        val maxLag = min(MAX_LAG_FRAMES, n / 2)
        if (maxLag <= MIN_LAG_FRAMES) return BeatGrid.NONE

        val mean = envelope.average()
        var zeroLagEnergy = 0.0
        for (value in envelope) {
            val centered = value - mean
            zeroLagEnergy += centered * centered
        }
        zeroLagEnergy /= n
        if (zeroLagEnergy <= 1e-12) return BeatGrid.NONE

        val correlation = DoubleArray(maxLag + 1)
        for (lag in MIN_LAG_FRAMES..maxLag) {
            var sum = 0.0
            for (i in 0 until n - lag) {
                sum += (envelope[i] - mean) * (envelope[i + lag] - mean)
            }
            correlation[lag] = sum / n / zeroLagEnergy
        }

        var bestLag = -1
        var bestScore = Double.NEGATIVE_INFINITY
        for (lag in MIN_LAG_FRAMES..maxLag) {
            var score = correlation[lag]
            if (2 * lag <= maxLag) score += 0.5 * correlation[2 * lag]
            if (4 * lag <= maxLag) score += 0.25 * correlation[4 * lag]
            score *= tempoPreference(lagToBpm(lag.toDouble()))
            if (score > bestScore) {
                bestScore = score
                bestLag = lag
            }
        }
        if (bestLag < 0) return BeatGrid.NONE

        val refinedLag = refinePeak(correlation, bestLag, maxLag)

        var coarseBpm = lagToBpm(refinedLag)
        while (coarseBpm > 0.0 && coarseBpm < MIN_REPORTED_BPM) coarseBpm *= 2.0
        while (coarseBpm > MAX_REPORTED_BPM) coarseBpm /= 2.0
        if (coarseBpm <= 0.0 || !coarseBpm.isFinite()) return BeatGrid.NONE

        val (period, offset) = refineTempoAndPhase(envelope, 60_000.0 / coarseBpm / hopMs)
        val bpm = (60_000.0 / (period * hopMs)).toFloat()
        if (bpm <= 0f || !bpm.isFinite()) return BeatGrid.NONE

        val density = transientDensity()
        val rhythmic = ((density - MIN_TRANSIENT_DENSITY) /
            (FULL_TRANSIENT_DENSITY - MIN_TRANSIENT_DENSITY)).coerceIn(0.0, 1.0)

        return BeatGrid(
            bpm = bpm,
            anchorBeatMs = startPositionMs + offset * hopMs,
            confidence = (correlation[bestLag].coerceIn(0.0, 1.0) * rhythmic).toFloat(),
        )
    }

    fun reset() {
        lowPassState = 0.0
        fullAccumulator = 0.0
        lowAccumulator = 0.0
        framesInHop = 0
        previousFullRms = 0.0
        previousLowRms = 0.0
        envelopeSize = 0
        startPositionMs = 0L
        secondAccumulator = 0.0
        secondLowAccumulator = 0.0
        hopsInSecond = 0
        profileSize = 0
    }

    private fun combinedEnvelope(): FloatArray {
        val n = envelopeSize
        val lowOnsets = onsetStrength(lowRms, n)
        val fullOnsets = onsetStrength(fullRms, n)

        var lowMean = 0.0
        var fullMean = 0.0
        for (i in 0 until n) {
            lowMean += lowOnsets[i]
            fullMean += fullOnsets[i]
        }
        lowMean /= n
        fullMean /= n

        val lowWeight = if (lowMean > 1e-9) LOW_BAND_WEIGHT / lowMean else 0.0
        val fullWeight = if (fullMean > 1e-9) FULL_BAND_WEIGHT / fullMean else 0.0
        if (lowWeight == 0.0 && fullWeight == 0.0) return FloatArray(0)

        return FloatArray(n) { i -> (lowOnsets[i] * lowWeight + fullOnsets[i] * fullWeight).toFloat() }
    }

    private fun onsetStrength(rms: FloatArray, n: Int): FloatArray {
        val onsets = FloatArray(n)
        for (i in 1 until n) {
            onsets[i] = max(0f, rms[i] - rms[i - 1])
        }
        return onsets
    }

    private fun transientDensity(): Double {
        val n = envelopeSize

        val level = FloatArray(n)
        for (i in 0 until n) {
            var sum = 0.0
            var count = 0
            for (k in -SMOOTH_RADIUS..SMOOTH_RADIUS) {
                val j = i + k
                if (j in 0 until n) {
                    sum += fullRms[j]
                    count++
                }
            }
            level[i] = (sum / count).toFloat()
        }

        var meanLevel = 0.0
        for (value in level) meanLevel += value
        meanLevel /= n
        if (meanLevel <= 1e-9) return 0.0

        var hits = 0
        for (i in 1 until n) {
            if ((level[i] - level[i - 1]) / meanLevel > TRANSIENT_JUMP) hits++
        }
        return hits.toDouble() / n
    }

    private fun refineTempoAndPhase(envelope: FloatArray, coarsePeriodFrames: Double): Pair<Double, Int> {
        var bestPeriod = coarsePeriodFrames
        var bestOffset = 0
        var bestScore = -1.0

        for (step in -REFINE_STEPS..REFINE_STEPS) {
            val period = coarsePeriodFrames * (1.0 + step * REFINE_STEP_FRACTION)
            if (period < 1.0) continue
            val offsetRange = max(1, period.roundToInt())
            for (offset in 0 until offsetRange) {
                var energy = 0.0
                var hits = 0
                var position = offset.toDouble()
                while (position < envelope.size) {
                    energy += envelope[position.roundToInt().coerceAtMost(envelope.size - 1)]
                    hits++
                    position += period
                }
                if (hits == 0) continue
                val score = energy / hits
                if (score > bestScore) {
                    bestScore = score
                    bestPeriod = period
                    bestOffset = offset
                }
            }
        }
        return bestPeriod to bestOffset
    }

    private fun refinePeak(correlation: DoubleArray, lag: Int, maxLag: Int): Double {
        if (lag <= MIN_LAG_FRAMES || lag >= maxLag) return lag.toDouble()
        val left = correlation[lag - 1]
        val center = correlation[lag]
        val right = correlation[lag + 1]
        val denominator = left - 2.0 * center + right
        if (abs(denominator) < 1e-12) return lag.toDouble()
        val delta = 0.5 * (left - right) / denominator
        return if (abs(delta) > 1.0) lag.toDouble() else lag + delta
    }

    private fun lagToBpm(lag: Double): Double = 60_000.0 / (lag * hopMs)

    private fun tempoPreference(bpm: Double): Double {
        if (bpm <= 0.0) return 0.0
        val octaves = ln(bpm / PREFERRED_BPM) / LN_2
        return exp(-0.5 * (octaves / TEMPO_PREFERENCE_OCTAVES) * (octaves / TEMPO_PREFERENCE_OCTAVES))
    }

    companion object {
        private const val KICK_BAND_HZ = 200.0
        private const val LOW_BAND_WEIGHT = 0.65
        private const val FULL_BAND_WEIGHT = 0.35

        private const val SMOOTH_RADIUS = 2

        private const val TRANSIENT_JUMP = 0.5

        private const val MIN_TRANSIENT_DENSITY = 0.004
        private const val FULL_TRANSIENT_DENSITY = 0.015

        private const val MIN_LAG_FRAMES = 30
        private const val MAX_LAG_FRAMES = 100

        private const val MIN_ENVELOPE_FRAMES = 1000
        private const val MAX_ENVELOPE_FRAMES = 2500

        private const val HOPS_PER_SECOND = 100

        private const val MAX_PROFILE_SECONDS = 900

        private const val MIN_REPORTED_BPM = 70.0
        private const val MAX_REPORTED_BPM = 190.0

        private const val REFINE_STEPS = 40
        private const val REFINE_STEP_FRACTION = 0.0005

        private const val PREFERRED_BPM = 120.0
        private const val TEMPO_PREFERENCE_OCTAVES = 0.9
        private val LN_2 = ln(2.0)
    }
}
