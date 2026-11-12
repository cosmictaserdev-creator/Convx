/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.data.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class PitchConversionTest {
    @Test
    fun everyDialogSemitoneSurvivesAFloatRoundTrip() {
        for (semitones in -12..12) {
            assertEquals(semitones, pitchToSemitones(semitonesToPitch(semitones)))
        }
    }

    @Test
    fun noTransposeIsExactlyUnityPitch() {
        assertEquals(1f, semitonesToPitch(0), 0f)
        assertEquals(0, pitchToSemitones(1f))
    }

    @Test
    fun anOctaveDoublesOrHalvesThePitch() {
        assertEquals(2f, semitonesToPitch(12), 1e-6f)
        assertEquals(0.5f, semitonesToPitch(-12), 1e-6f)
    }
}
