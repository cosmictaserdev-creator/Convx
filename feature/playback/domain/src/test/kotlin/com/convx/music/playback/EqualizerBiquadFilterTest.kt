package com.convx.music.playback

import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EqualizerBiquadFilterTest {
    @Test
    fun peakingFilterAppliesConfiguredBoostAndCut() {
        val boostRms = steadyStateRms(gainDb = 12.0)
        val cutRms = steadyStateRms(gainDb = -12.0)

        assertTrue(boostRms > 2.5, "Expected a 1 kHz boost, got RMS=$boostRms")
        assertTrue(cutRms < 0.4, "Expected a 1 kHz cut, got RMS=$cutRms")
    }

    @Test
    fun stereoChannelsKeepIndependentFilterHistory() {
        val filter = EqualizerBiquadFilter(
            sampleRate = SAMPLE_RATE_HZ,
            frequency = TEST_FREQUENCY_HZ.toDouble(),
            gain = 12.0,
        )

        repeat(64) {
            filter.processSample(1.0)
            assertEquals(0.0, filter.processRightSample(0.0))
        }
    }

    @Test
    fun resetRestoresTheInitialImpulseResponse() {
        val filter = EqualizerBiquadFilter(
            sampleRate = SAMPLE_RATE_HZ,
            frequency = TEST_FREQUENCY_HZ.toDouble(),
            gain = 6.0,
        )
        val initial = filter.processSample(1.0)
        repeat(128) { filter.processSample(0.0) }

        filter.reset()

        assertEquals(initial, filter.processSample(1.0), absoluteTolerance = 1e-12)
    }

    @Test
    fun allFilterTypesProduceFiniteSamplesAtSupportedBandCenters() {
        EqualizerFilterType.values().forEach { filterType ->
            val filter = EqualizerBiquadFilter(
                sampleRate = SAMPLE_RATE_HZ,
                frequency = TEST_FREQUENCY_HZ.toDouble(),
                gain = 6.0,
                q = 1.2,
                filterType = filterType,
            )
            repeat(512) { sampleIndex ->
                val sample = sin(2.0 * PI * TEST_FREQUENCY_HZ * sampleIndex / SAMPLE_RATE_HZ)
                assertTrue(filter.processSample(sample).isFinite(), "Non-finite output for $filterType")
            }
        }
    }

    private fun steadyStateRms(gainDb: Double): Double {
        val filter = EqualizerBiquadFilter(
            sampleRate = SAMPLE_RATE_HZ,
            frequency = TEST_FREQUENCY_HZ.toDouble(),
            gain = gainDb,
        )
        var squareSum = 0.0
        var measuredSamples = 0
        repeat(SAMPLE_COUNT) { sampleIndex ->
            val sample = sin(2.0 * PI * TEST_FREQUENCY_HZ * sampleIndex / SAMPLE_RATE_HZ)
            val output = filter.processSample(sample)
            if (sampleIndex >= WARMUP_SAMPLES) {
                squareSum += output * output
                measuredSamples++
            }
        }
        return kotlin.math.sqrt(squareSum / measuredSamples)
    }

    private companion object {
        const val SAMPLE_RATE_HZ = 44_100
        const val TEST_FREQUENCY_HZ = 1_000
        const val SAMPLE_COUNT = 44_100
        const val WARMUP_SAMPLES = 8_820
    }
}
