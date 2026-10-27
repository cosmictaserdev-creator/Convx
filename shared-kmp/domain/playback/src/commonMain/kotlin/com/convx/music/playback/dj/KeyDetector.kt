/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

data class MusicalKey(
    val pitchClass: Int,
    val isMinor: Boolean,
    val confidence: Float,
) {
    val camelotNumber: Int
        get() {
            val relativeMajor = if (isMinor) (pitchClass + 3) % 12 else pitchClass
            return ((relativeMajor * 7 + 7) % 12) + 1
        }

    val camelot: String get() = "$camelotNumber${if (isMinor) "A" else "B"}"

    val name: String get() = "${PITCH_NAMES[pitchClass]}${if (isMinor) "m" else ""}"

    companion object {
        val PITCH_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        fun compatible(a: MusicalKey?, b: MusicalKey?, minConfidence: Float = MIN_USABLE_CONFIDENCE): Boolean {
            if (a == null || b == null) return true
            if (a.confidence < minConfidence || b.confidence < minConfidence) return true

            if (a.camelotNumber == b.camelotNumber) return true
            val distance = positiveMod(a.camelotNumber - b.camelotNumber, 12)
            return (distance == 1 || distance == 11) && a.isMinor == b.isMinor
        }

        fun matchingShift(outgoing: MusicalKey?, incoming: MusicalKey?): Int {
            if (outgoing == null || incoming == null) return 0
            if (outgoing.confidence < MIN_USABLE_CONFIDENCE || incoming.confidence < MIN_USABLE_CONFIDENCE) return 0
            if (compatible(outgoing, incoming)) return 0

            for (shift in intArrayOf(1, -1)) {
                val shifted = incoming.copy(pitchClass = positiveMod(incoming.pitchClass + shift, 12))
                if (compatible(outgoing, shifted)) return shift
            }
            return 0
        }

        const val MIN_USABLE_CONFIDENCE = 0.55f
        val NONE = MusicalKey(-1, false, 0f)
    }
}

class KeyDetector(inputSampleRate: Int) {

    private val decimation = (inputSampleRate / TARGET_SAMPLE_RATE).coerceAtLeast(1)
    private val workingRate = inputSampleRate / decimation

    private val antiAlias = Biquad().apply {
        setLowPass(inputSampleRate, ANTI_ALIAS_HZ)
    }
    private var decimationCounter = 0

    private val block = DoubleArray(BLOCK_SIZE)
    private val windowedBlock = DoubleArray(BLOCK_SIZE)
    private val coefficients = DoubleArray(PITCH_CLASSES * OCTAVES) { bin ->
        val frequency = LOWEST_HZ * 2.0.pow(bin / PITCH_CLASSES + (bin % PITCH_CLASSES) / 12.0)
        if (frequency < workingRate / 2.0) 2.0 * cos(2.0 * PI * frequency / workingRate) else Double.NaN
    }
    private var blockFill = 0

    private val chroma = DoubleArray(12)
    private var blocksAnalyzed = 0

    private val window = DoubleArray(BLOCK_SIZE) { i ->
        0.5 - 0.5 * cos(2.0 * PI * i / (BLOCK_SIZE - 1))
    }

    val hasEnoughData: Boolean get() = blocksAnalyzed >= MIN_BLOCKS

    fun accept(sample: Float) {
        val filtered = antiAlias.process(sample.toDouble())
        if (++decimationCounter < decimation) return
        decimationCounter = 0

        block[blockFill++] = filtered
        if (blockFill < BLOCK_SIZE) return

        analyzeBlock()
        blockFill = 0
    }

    private fun analyzeBlock() {
        for (index in block.indices) windowedBlock[index] = block[index] * window[index]
        for (bin in 0 until PITCH_CLASSES * OCTAVES) {
            val coefficient = coefficients[bin]
            if (coefficient.isNaN()) continue
            chroma[bin % PITCH_CLASSES] += goertzelMagnitude(coefficient)
        }
        blocksAnalyzed++
    }

    private fun goertzelMagnitude(coefficient: Double): Double {
        var s1 = 0.0
        var s2 = 0.0
        for (i in 0 until BLOCK_SIZE) {
            val s = windowedBlock[i] + coefficient * s1 - s2
            s2 = s1
            s1 = s
        }
        val power = s1 * s1 + s2 * s2 - coefficient * s1 * s2
        return if (power > 0.0) sqrt(power) else 0.0
    }

    fun estimate(): MusicalKey {
        if (!hasEnoughData) return MusicalKey.NONE
        val total = chroma.sum()
        if (total <= 0.0) return MusicalKey.NONE

        var best = MusicalKey.NONE
        var bestScore = -2.0
        var runnerUp = -2.0

        for (minor in booleanArrayOf(false, true)) {
            val profile = if (minor) MINOR_PROFILE else MAJOR_PROFILE
            for (tonic in 0 until PITCH_CLASSES) {
                val score = correlate(chroma, profile, tonic)
                if (score > bestScore) {
                    runnerUp = bestScore
                    bestScore = score
                    best = MusicalKey(tonic, minor, 0f)
                } else if (score > runnerUp) {
                    runnerUp = score
                }
            }
        }

        if (bestScore <= 0.0) return MusicalKey.NONE
        val margin = ((bestScore - runnerUp) / bestScore).coerceIn(0.0, 1.0)
        val confidence = (bestScore * (MARGIN_FLOOR + (1.0 - MARGIN_FLOOR) * margin * MARGIN_GAIN))
            .coerceIn(0.0, 1.0)
        return best.copy(confidence = confidence.toFloat())
    }

    fun reset() {
        antiAlias.reset()
        decimationCounter = 0
        blockFill = 0
        blocksAnalyzed = 0
        chroma.fill(0.0)
    }

    private fun correlate(values: DoubleArray, profile: DoubleArray, tonic: Int): Double {
        var meanValues = 0.0
        var meanProfile = 0.0
        for (i in 0 until PITCH_CLASSES) {
            meanValues += values[i]
            meanProfile += profile[i]
        }
        meanValues /= PITCH_CLASSES
        meanProfile /= PITCH_CLASSES

        var covariance = 0.0
        var varianceValues = 0.0
        var varianceProfile = 0.0
        for (i in 0 until PITCH_CLASSES) {
            val dv = values[(i + tonic) % PITCH_CLASSES] - meanValues
            val dp = profile[i] - meanProfile
            covariance += dv * dp
            varianceValues += dv * dv
            varianceProfile += dp * dp
        }
        val denominator = sqrt(varianceValues * varianceProfile)
        return if (denominator <= 0.0) 0.0 else covariance / denominator
    }

    companion object {
        private const val PITCH_CLASSES = 12
        private const val OCTAVES = 3

        private const val LOWEST_HZ = 130.81

        private const val TARGET_SAMPLE_RATE = 4410
        private const val ANTI_ALIAS_HZ = 1400f
        private const val BLOCK_SIZE = 2048
        private const val MIN_BLOCKS = 20

        private const val MARGIN_FLOOR = 0.4
        private const val MARGIN_GAIN = 4.0

        private val MAJOR_PROFILE = doubleArrayOf(
            6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88,
        )
        private val MINOR_PROFILE = doubleArrayOf(
            6.33, 2.68, 3.52, 5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17,
        )
    }
}

private fun positiveMod(value: Int, divisor: Int): Int = ((value % divisor) + divisor) % divisor
