package com.convx.music.playback

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrackAnalysisGeneratorTest {

    @Test
    fun extractsTempoAndBoundedEnergyCurvesFromChunkedPcm() {
        val samples = kickTrack(durationSeconds = 24, bpm = 120.0)
        samples[1_000] = Float.NaN
        samples[2_000] = Float.POSITIVE_INFINITY
        val generator = TrackAnalysisGenerator(trackId = "track-120", sampleRate = SAMPLE_RATE)

        var offset = 0
        while (offset < samples.size) {
            val length = minOf(CHUNK_SIZE, samples.size - offset)
            generator.acceptMonoSamples(samples, offset, length)
            offset += length
        }

        val analysis = assertNotNull(generator.finish(expectedDurationSeconds = 24.0))
        assertTrue(analysis.isUsable)
        assertEquals("track-120", analysis.trackId)
        assertEquals(24.0, analysis.duration, 0.01)
        assertEquals(120.0, analysis.bpm, 2.0)
        assertTrue(analysis.beatConfidence >= 0.2)
        assertTrue(analysis.energyCurve.isNotEmpty())
        assertEquals(analysis.energyCurve.size, analysis.lowEnergyCurve.size)
        assertTrue(analysis.energyCurve.all { it.time.isFinite() && it.energy.isFinite() })
        assertTrue(analysis.lowEnergyCurve.all { it.time.isFinite() && it.energy.isFinite() })
        assertEquals(0.0, analysis.vocalProbability)
        assertTrue(analysis.vocalActivityMask.isEmpty())
        assertTrue(analysis.downbeats.isEmpty())
        assertTrue(analysis.phraseBoundaries.isEmpty())
    }

    @Test
    fun silenceAndShortInputDoNotProduceReadyAnalysis() {
        val short = TrackAnalysisGenerator(trackId = "short", sampleRate = SAMPLE_RATE)
        short.acceptMonoSamples(FloatArray(SAMPLE_RATE))
        assertNull(short.finish())

        val silence = TrackAnalysisGenerator(trackId = "silence", sampleRate = SAMPLE_RATE)
        silence.acceptMonoSamples(FloatArray(SAMPLE_RATE * 16))
        assertNull(silence.finish())
    }

    @Test
    fun rejectsDecodedDurationThatDoesNotMatchTheContainer() {
        val samples = kickTrack(durationSeconds = 24, bpm = 120.0)
        val generator = TrackAnalysisGenerator(trackId = "mismatch", sampleRate = SAMPLE_RATE)
        generator.acceptMonoSamples(samples)

        assertNull(generator.finish(expectedDurationSeconds = 31.0))
    }

    @Test
    fun validatesTrackIdentitySampleRateAndChunkBounds() {
        assertFailsWith<IllegalArgumentException> { TrackAnalysisGenerator(" ", SAMPLE_RATE) }
        assertFailsWith<IllegalArgumentException> { TrackAnalysisGenerator("track", 0) }

        val generator = TrackAnalysisGenerator("track", SAMPLE_RATE)
        assertFailsWith<IllegalArgumentException> {
            generator.acceptMonoSamples(floatArrayOf(0f, 0f), offset = 1, length = 2)
        }
        assertFalse(generator.analyzedFrames > 0L)
    }

    private fun kickTrack(durationSeconds: Int, bpm: Double): FloatArray {
        val samples = FloatArray(SAMPLE_RATE * durationSeconds)
        val beatFrames = (SAMPLE_RATE * 60.0 / bpm).roundToInt()
        val pulseFrames = SAMPLE_RATE / 10
        var start = 0
        while (start < samples.size) {
            for (frame in 0 until pulseFrames) {
                val index = start + frame
                if (index >= samples.size) break
                val seconds = frame.toDouble() / SAMPLE_RATE
                val envelope = exp(-seconds / 0.045)
                samples[index] += (0.8 * envelope * sin(2.0 * PI * 60.0 * seconds)).toFloat()
            }
            start += beatFrames
        }
        return samples
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val CHUNK_SIZE = 8_192
    }
}
