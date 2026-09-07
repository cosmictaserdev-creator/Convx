package com.convxy.music.ui.component

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformSeekBarTest {
    @Test
    fun `bars are deterministic for a given seed`() {
        assertArrayEquals(waveformBars(42, 28), waveformBars(42, 28), 0f)
    }

    @Test
    fun `different seeds give different shapes`() {
        assertTrue(!waveformBars(1, 28).contentEquals(waveformBars(2, 28)))
    }

    @Test
    fun `heights stay within drawable range and count matches`() {
        val bars = waveformBars(7, 40)
        assertEquals(40, bars.size)
        bars.forEach { assertTrue("$it out of range", it in 0.12f..1f) }
    }
}
