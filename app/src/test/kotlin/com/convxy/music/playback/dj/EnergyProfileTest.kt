package com.convxy.music.playback.dj

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnergyProfileTest {

    @Test
    fun `a track that ends at full energy reports a loud outro and no drop`() {
        val profile = flat(180, level = 200)

        assertTrue(EnergyProfile.outroEnergy(profile) > 0.9f)
        assertEquals(-1, EnergyProfile.outroDropSecond(profile))
    }

    @Test
    fun `a track that fades out reports a quiet outro and finds the drop`() {
        // 150 s at full, then 30 s of outro decaying to silence.
        val profile = ByteArray(180) { second ->
            if (second < 150) 200.toByte() else (200 - (second - 150) * 6).coerceAtLeast(0).toByte()
        }

        assertTrue(EnergyProfile.outroEnergy(profile) < 0.3f)
        val drop = EnergyProfile.outroDropSecond(profile)
        assertTrue("drop at $drop should be in the outro", drop in 150..175)
    }

    @Test
    fun `an early breakdown is not mistaken for the outro`() {
        // Quiet bar at 60 s, but the track comes back and ends loud.
        val profile = ByteArray(180) { second -> (if (second in 60..68) 40 else 200).toByte() }

        assertEquals(-1, EnergyProfile.outroDropSecond(profile))
    }

    @Test
    fun `a quiet intro is detected and located`() {
        // 24 s of pad, then the track kicks in.
        val profile = ByteArray(180) { second -> (if (second < 24) 30 else 200).toByte() }

        assertTrue(EnergyProfile.introEnergy(profile) < 0.3f)
        val rise = EnergyProfile.introRiseSecond(profile)
        assertTrue("rise at $rise should be near 24", rise in 20..30)
    }

    @Test
    fun `a track that starts on the groove reports no intro rise`() {
        val profile = flat(180, level = 200)

        assertTrue(EnergyProfile.introEnergy(profile) > 0.9f)
        assertEquals(-1, EnergyProfile.introRiseSecond(profile))
    }

    @Test
    fun `a profile that stops short of the track does not claim to cover the outro`() {
        val partial = flat(40, level = 200)

        assertFalse(EnergyProfile.coversOutro(partial, trackDurationMs = 180_000L))
        assertTrue(EnergyProfile.coversOutro(flat(180, 200), trackDurationMs = 180_000L))
    }

    @Test
    fun `too short a profile is refused rather than guessed at`() {
        val tiny = flat(5, level = 200)

        assertFalse(EnergyProfile.isUsable(tiny))
        assertEquals(0f, EnergyProfile.outroEnergy(tiny), 0.0001f)
        assertEquals(-1, EnergyProfile.outroDropSecond(tiny))
    }

    private fun flat(seconds: Int, level: Int) = ByteArray(seconds) { level.toByte() }
}
