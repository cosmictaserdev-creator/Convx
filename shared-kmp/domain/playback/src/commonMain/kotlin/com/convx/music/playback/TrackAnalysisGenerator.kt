/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import com.convx.music.playback.dj.BeatTracker
import com.convx.music.playback.dj.KeyDetector
import com.convx.music.playback.smart.EnergySample
import com.convx.music.playback.smart.TrackAnalysis
import kotlin.math.abs

/**
 * Builds portable track features from decoded mono PCM. Callers feed audio in
 * bounded chunks off the UI thread, then call [finish] once decoding ends.
 * Missing or unreliable tempo evidence returns `null`, so callers never cache
 * fabricated analysis.
 */
class TrackAnalysisGenerator(
    private val trackId: String,
    private val sampleRate: Int,
) {
    private val beatTracker: BeatTracker
    private val keyDetector: KeyDetector
    private val maximumFrames = sampleRate.toLong() * MAX_ANALYSIS_SECONDS
    private val maximumKeyFrames = sampleRate.toLong() * MAX_KEY_ANALYSIS_SECONDS

    private var acceptedFrames = 0L
    private var keyFrames = 0L
    private var wasTruncated = false
    private var finished = false

    init {
        require(trackId.isNotBlank()) { "trackId must not be blank" }
        require(sampleRate in MIN_SAMPLE_RATE..MAX_SAMPLE_RATE) {
            "sampleRate must be between $MIN_SAMPLE_RATE and $MAX_SAMPLE_RATE Hz"
        }
        beatTracker = BeatTracker(sampleRate)
        keyDetector = KeyDetector(sampleRate)
    }

    val analyzedFrames: Long get() = acceptedFrames

    /**
     * Adds a mono float PCM chunk. Non-finite samples are treated as silence
     * and finite samples are clamped to the normalized PCM range.
     */
    fun acceptMonoSamples(samples: FloatArray, offset: Int = 0, length: Int = samples.size - offset) {
        check(!finished) { "analysis is already finished" }
        require(offset >= 0 && length >= 0 && offset <= samples.size - length) {
            "sample range is outside the input buffer"
        }

        val end = offset + length
        for (index in offset until end) {
            if (acceptedFrames >= maximumFrames) {
                wasTruncated = true
                return
            }

            val input = samples[index]
            val mono = if (input.isFinite()) input.coerceIn(-1f, 1f) else 0f
            beatTracker.accept(mono)
            if (keyFrames < maximumKeyFrames) {
                keyDetector.accept(mono)
                keyFrames++
            }
            acceptedFrames++
        }
    }

    /**
     * Completes analysis. `expectedDurationSeconds`, when available from the
     * container, must agree with decoded audio within codec/container padding.
     */
    fun finish(expectedDurationSeconds: Double? = null): TrackAnalysis? {
        check(!finished) { "analysis is already finished" }
        finished = true
        if (wasTruncated) return null

        val decodedDuration = acceptedFrames.toDouble() / sampleRate
        if (decodedDuration < MIN_ANALYSIS_SECONDS) return null

        val expectedDuration = expectedDurationSeconds?.takeIf {
            it.isFinite() && it > 0.0 && it <= MAX_ANALYSIS_SECONDS.toDouble()
        }
        if (expectedDurationSeconds != null && expectedDuration == null) return null
        if (expectedDuration != null && abs(expectedDuration - decodedDuration) > MAX_DURATION_DISCREPANCY_SECONDS) {
            return null
        }

        val grid = beatTracker.estimate()
        if (!grid.bpm.isFinite() || grid.bpm <= 0f) return null

        val fullEnergy = beatTracker.energyProfile()
        if (fullEnergy.isEmpty()) return null
        val lowEnergy = beatTracker.lowEnergyProfile()
        val duration = expectedDuration ?: decodedDuration
        val detectedKey = keyDetector.estimate().takeIf { it.pitchClass in 0..11 }

        return TrackAnalysis(
            status = TrackAnalysis.STATUS_READY,
            trackId = trackId,
            duration = duration,
            bpm = grid.bpm.toDouble(),
            beatInterval = 60.0 / grid.bpm,
            beatConfidence = grid.confidence.toDouble().coerceIn(0.0, 1.0),
            firstBeat = (grid.anchorBeatMs / 1_000.0).takeIf { it.isFinite() && it in 0.0..duration } ?: 0.0,
            key = detectedKey?.name.orEmpty(),
            keyConfidence = detectedKey?.confidence?.toDouble()?.coerceIn(0.0, 1.0) ?: 0.0,
            energyCurve = fullEnergy.toEnergyCurve(),
            lowEnergyCurve = lowEnergy.toEnergyCurve(),
            // Vocal detection and bar-one alignment have no evidence in these DSP features.
            vocalActivityMask = emptyList(),
            vocalProbability = 0.0,
        )
    }

    private fun ByteArray.toEnergyCurve(): List<EnergySample> =
        buildList(size) {
            for (index in 0 until this@toEnergyCurve.size) {
                add(EnergySample(time = index.toDouble(), energy = (this@toEnergyCurve[index].toInt() and 0xff) / 255.0))
            }
        }

    private companion object {
        const val MIN_SAMPLE_RATE = 8_000
        const val MAX_SAMPLE_RATE = 192_000
        const val MIN_ANALYSIS_SECONDS = 12.0
        const val MAX_ANALYSIS_SECONDS = 15 * 60L
        const val MAX_KEY_ANALYSIS_SECONDS = 90L
        const val MAX_DURATION_DISCREPANCY_SECONDS = 2.0
    }
}
