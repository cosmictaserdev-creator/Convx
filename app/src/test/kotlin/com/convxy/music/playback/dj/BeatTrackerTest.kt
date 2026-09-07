package com.convxy.music.playback.dj

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin
import org.junit.Test

/**
 * Synthesized fixtures for the beat tracker. These are the calibration set the
 * thresholds in [BeatTracker] and `DjMixPlanner` were tuned against, so a
 * change to either that breaks the separation between percussive and
 * non-percussive material fails here.
 */
class BeatTrackerTest {

    @Test
    fun `four on the floor gives an accurate tempo, phase and high confidence`() {
        val grid = analyze(kickTrack(bpm = 128.0, firstBeatMs = 200.0))

        assertEquals(128.0, grid.bpm.toDouble(), 2.0)
        assertBeatFallsOn(grid, 200.0, toleranceMs = 30.0)
        // The regression this whole class exists for: the previous estimator's
        // confidence could not exceed ~0.5 even on an ideal metronome, so the
        // FULL_DJ tier was unreachable and Auto-DJ silently did nothing.
        assertTrue("confidence ${grid.confidence} must clear the FULL_DJ bar", grid.confidence >= 0.6f)
    }

    @Test
    fun `offbeat hats do not pull the tempo to double time`() {
        val grid = analyze(kickTrack(bpm = 128.0, firstBeatMs = 200.0, offbeatHats = true))

        assertEquals(128.0, grid.bpm.toDouble(), 2.0)
        assertBeatFallsOn(grid, 200.0, toleranceMs = 30.0)
    }

    @Test
    fun `analysis starting mid-track anchors the grid in absolute track time`() {
        val grid = analyze(kickTrack(bpm = 128.0, firstBeatMs = 0.0), startPositionMs = 30_000L)

        assertEquals(128.0, grid.bpm.toDouble(), 2.0)
        // Beats are at 30000 + n * beat, so the anchor must be too. A grid
        // anchored at position 0 instead would be half a beat out by here.
        assertBeatFallsOn(grid, 30_000.0, toleranceMs = 30.0)
    }

    @Test
    fun `a backbeat resolves to a grid whose beats are all real beats`() {
        val grid = analyze(backbeatTrack(bpm = 160.0))

        // 160 with a kick/snare backbeat is genuinely ambiguous with 80 — a
        // listener cannot tell either. What must hold is that the reported
        // tempo is one of the two, so every beat of the grid lands on a real
        // beat; DjMixPlanner treats the octave as free.
        val bpm = grid.bpm.toDouble()
        assertTrue("bpm $bpm should be 160 or its half", abs(bpm - 160.0) < 3.0 || abs(bpm - 80.0) < 3.0)
        assertTrue("confidence ${grid.confidence}", grid.confidence >= 0.6f)
    }

    @Test
    fun `a phrase boundary snapped down never lands after the point asked for`() {
        // Regression: snapping to the *nearest* phrase could push a transition
        // up to half a phrase later than `duration - crossfade`, leaving less
        // track than the fade needed, so the outgoing song ended partway
        // through the crossfade.
        val grid = BeatGrid(bpm = 128f, anchorBeatMs = 200f, confidence = 0.9f)
        val phraseMs = grid.beatMs * BeatGrid.BEATS_PER_PHRASE

        for (position in longArrayOf(10_000L, 123_456L, 200_000L, 201_000L)) {
            val snapped = grid.phraseAtOrBeforeMs(position)
            assertTrue("$snapped must not exceed $position", snapped <= position)
            assertTrue("$snapped must be within one phrase of $position", position - snapped < phraseMs)
        }
    }

    @Test
    fun `white noise is rejected`() {
        assertTrue(analyze(noiseTrack()).confidence < 0.20f)
    }

    @Test
    fun `a sustained detuned pad is rejected despite a periodic envelope`() {
        // Beating partials make this autocorrelate as well as a real beat.
        // Only the transient-density gate separates them.
        assertTrue(analyze(padTrack()).confidence < 0.20f)
    }

    // --- fixtures -----------------------------------------------------------

    private fun analyze(samples: FloatArray, startPositionMs: Long = 0L): BeatGrid {
        val tracker = BeatTracker(SAMPLE_RATE)
        tracker.beginAt(startPositionMs)
        for (sample in samples) tracker.accept(sample)
        return tracker.estimate()
    }

    /** Asserts some beat of the grid lands on [expectedMs]. The grid has no
     *  notion of "first" beat, so comparing the anchor directly would fail on
     *  a perfectly correct grid anchored a beat later. */
    private fun assertBeatFallsOn(grid: BeatGrid, expectedMs: Double, toleranceMs: Double) {
        val beat = grid.beatMs.toDouble()
        assertTrue("no grid to check", beat > 0.0)
        val beatsAway = ((expectedMs - grid.anchorBeatMs) / beat).roundToInt()
        val nearest = grid.anchorBeatMs + beatsAway * beat
        assertEquals("nearest grid beat to $expectedMs", expectedMs, nearest, toleranceMs)
    }

    private fun kickTrack(bpm: Double, firstBeatMs: Double, offbeatHats: Boolean = false): FloatArray {
        val random = Random(SEED)
        val samples = FloatArray(SAMPLE_RATE * DURATION_MS / 1000)
        val beatMs = 60_000.0 / bpm
        var t = firstBeatMs
        while (t < DURATION_MS) {
            addHit(samples, t, 60.0, 120.0, 0.9, random, tonal = true)
            if (offbeatHats) addHit(samples, t + beatMs / 2, 0.0, 25.0, 0.35, random, tonal = false)
            t += beatMs
        }
        addNoiseFloor(samples, 0.01, random)
        return samples
    }

    /** Kick on 1 and 3, snare on 2 and 4. */
    private fun backbeatTrack(bpm: Double): FloatArray {
        val random = Random(SEED)
        val samples = FloatArray(SAMPLE_RATE * DURATION_MS / 1000)
        val beatMs = 60_000.0 / bpm
        var t = 0.0
        var beatIndex = 0
        while (t < DURATION_MS) {
            if (beatIndex % 2 == 0) {
                addHit(samples, t, 55.0, 110.0, 0.9, random, tonal = true)
            } else {
                addHit(samples, t, 0.0, 70.0, 0.55, random, tonal = false)
            }
            t += beatMs
            beatIndex++
        }
        addNoiseFloor(samples, 0.01, random)
        return samples
    }

    private fun noiseTrack(): FloatArray {
        val samples = FloatArray(SAMPLE_RATE * DURATION_MS / 1000)
        addNoiseFloor(samples, 0.3, Random(SEED))
        return samples
    }

    /** Held detuned chord, slowly swelling. No attacks anywhere. */
    private fun padTrack(): FloatArray {
        val samples = FloatArray(SAMPLE_RATE * DURATION_MS / 1000)
        for (i in samples.indices) {
            val t = i.toDouble() / SAMPLE_RATE
            val voices = sin(2 * PI * 137.3 * t) * 0.30 +
                sin(2 * PI * 206.1 * t) * 0.22 +
                sin(2 * PI * 91.7 * t) * 0.25
            samples[i] = (voices * (0.6 + 0.3 * sin(2 * PI * 0.07 * t))).toFloat()
        }
        addNoiseFloor(samples, 0.01, Random(SEED))
        return samples
    }

    private fun addHit(
        samples: FloatArray,
        atMs: Double,
        frequency: Double,
        decayMs: Double,
        amplitude: Double,
        random: Random,
        tonal: Boolean,
    ) {
        val start = (atMs * SAMPLE_RATE / 1000.0).toInt()
        val length = (decayMs * SAMPLE_RATE / 1000.0).toInt()
        val tau = decayMs / 3000.0
        for (i in 0 until length) {
            val index = start + i
            if (index < 0 || index >= samples.size) continue
            val t = i.toDouble() / SAMPLE_RATE
            val value = if (tonal) sin(2 * PI * frequency * t) else random.nextDouble() * 2 - 1
            samples[index] += (value * exp(-t / tau) * amplitude).toFloat()
        }
    }

    private fun addNoiseFloor(samples: FloatArray, amplitude: Double, random: Random) {
        for (i in samples.indices) samples[i] += ((random.nextDouble() * 2 - 1) * amplitude).toFloat()
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val DURATION_MS = 40_000
        const val SEED = 1234L
    }
}
