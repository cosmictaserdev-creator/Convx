package com.convxy.music.playback.dj

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class KeyDetectorTest {

    @Test
    fun `C major is detected from its own triad`() {
        val key = analyze(chordProgression(C_MAJOR_TRIADS))

        assertEquals("C", MusicalKey.PITCH_NAMES[key.pitchClass])
        assertFalse(key.isMinor)
        assertTrue("confidence ${key.confidence}", key.confidence >= MusicalKey.MIN_USABLE_CONFIDENCE)
    }

    @Test
    fun `A minor is detected from its own triad`() {
        val key = analyze(chordProgression(A_MINOR_TRIADS))

        assertEquals("A", MusicalKey.PITCH_NAMES[key.pitchClass])
        assertTrue(key.isMinor)
    }

    @Test
    fun `camelot codes match the published wheel`() {
        assertEquals("8B", MusicalKey(0, isMinor = false, confidence = 1f).camelot)   // C major
        assertEquals("8A", MusicalKey(9, isMinor = true, confidence = 1f).camelot)    // A minor
        assertEquals("9B", MusicalKey(7, isMinor = false, confidence = 1f).camelot)   // G major
        assertEquals("1B", MusicalKey(11, isMinor = false, confidence = 1f).camelot)  // B major
        assertEquals("7B", MusicalKey(5, isMinor = false, confidence = 1f).camelot)   // F major
        assertEquals("9A", MusicalKey(4, isMinor = true, confidence = 1f).camelot)    // E minor
    }

    @Test
    fun `relative major and minor are compatible, a tritone apart is not`() {
        val cMajor = MusicalKey(0, isMinor = false, confidence = 1f)     // 8B
        val aMinor = MusicalKey(9, isMinor = true, confidence = 1f)      // 8A
        val gMajor = MusicalKey(7, isMinor = false, confidence = 1f)     // 9B
        val fSharpMajor = MusicalKey(6, isMinor = false, confidence = 1f) // 2B

        assertTrue(MusicalKey.compatible(cMajor, aMinor))
        assertTrue(MusicalKey.compatible(cMajor, gMajor))
        assertFalse(MusicalKey.compatible(cMajor, fSharpMajor))
    }

    @Test
    fun `an unconfident key never blocks a mix`() {
        val cMajor = MusicalKey(0, isMinor = false, confidence = 1f)
        val unsure = MusicalKey(6, isMinor = false, confidence = 0.1f)

        assertTrue(MusicalKey.compatible(cMajor, unsure))
        assertTrue(MusicalKey.compatible(cMajor, null))
    }

    @Test
    fun `a semitone shift is offered only when it actually resolves the clash`() {
        val gMajor = MusicalKey(7, isMinor = false, confidence = 1f)      // 9B
        val gSharpMajor = MusicalKey(8, isMinor = false, confidence = 1f) // 4B — clashes

        // Shifting G# down a semitone lands on G major, the same key.
        assertEquals(-1, MusicalKey.matchingShift(gMajor, gSharpMajor))
        // Already compatible: no shift.
        assertEquals(0, MusicalKey.matchingShift(gMajor, gMajor))
    }

    // --- fixtures -----------------------------------------------------------

    private fun analyze(samples: FloatArray): MusicalKey {
        val detector = KeyDetector(SAMPLE_RATE)
        for (sample in samples) detector.accept(sample)
        return detector.estimate()
    }

    /** Semitones above C3 for each chord tone, one chord per bar. */
    private fun chordProgression(chords: Array<IntArray>): FloatArray {
        val samples = FloatArray(SAMPLE_RATE * DURATION_MS / 1000)
        val barSamples = SAMPLE_RATE * 2
        var offset = 0
        var chordIndex = 0
        while (offset < samples.size) {
            val chord = chords[chordIndex % chords.size]
            for (semitone in chord) {
                // Fundamental plus a quieter octave, so the chroma sees the same
                // pitch class in more than one of its three octaves.
                addTone(samples, offset, barSamples, semitone, 0.30)
                addTone(samples, offset, barSamples, semitone + 12, 0.15)
            }
            offset += barSamples
            chordIndex++
        }
        return samples
    }

    private fun addTone(samples: FloatArray, offset: Int, length: Int, semitone: Int, amplitude: Double) {
        val frequency = C3_HZ * Math.pow(2.0, semitone / 12.0)
        for (i in 0 until length) {
            val index = offset + i
            if (index >= samples.size) return
            val t = i.toDouble() / SAMPLE_RATE
            samples[index] += (sin(2 * PI * frequency * t) * amplitude).toFloat()
        }
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val DURATION_MS = 32_000
        const val C3_HZ = 130.81

        // I - vi - IV - V in C: C, Am, F, G
        val C_MAJOR_TRIADS = arrayOf(
            intArrayOf(0, 4, 7),    // C  E  G
            intArrayOf(9, 12, 16),  // A  C  E
            intArrayOf(5, 9, 12),   // F  A  C
            intArrayOf(7, 11, 14),  // G  B  D
        )

        // i - iv - V - i in A minor: Am, Dm, E, Am.
        //
        // Deliberately NOT the Am-F-C-G loop: that has exactly the same pitch
        // classes as the C major progression above, so no chroma-based detector
        // could tell them apart and the test would be asserting the impossible.
        // The dominant E major carries G#, foreign to C major, which is what
        // actually distinguishes a minor key from its relative major.
        val A_MINOR_TRIADS = arrayOf(
            intArrayOf(9, 12, 16),  // A  C  E
            intArrayOf(2, 5, 9),    // D  F  A
            intArrayOf(4, 8, 11),   // E  G# B
            intArrayOf(9, 12, 16),  // A  C  E
        )
    }
}
