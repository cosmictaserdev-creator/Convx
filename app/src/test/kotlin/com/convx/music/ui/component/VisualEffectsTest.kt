/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualEffectsTest {
    private val heavy = VisualEffects(
        reduceDynamicBlur = false,
        liquidGlass = true,
        albumCanvas = true,
        artistBackgroundVideo = true,
        homeVideoBackground = true,
    )

    @Test
    fun offChangesNothing() {
        assertSame(heavy, heavy.effective(performanceMode = false))
    }

    @Test
    fun onDrawsLighter() {
        val light = heavy.effective(performanceMode = true)
        assertTrue(light.reduceDynamicBlur)
        assertFalse(light.liquidGlass || light.albumCanvas || light.artistBackgroundVideo || light.homeVideoBackground)
    }

    @Test
    fun onIsIdempotent() {
        val once = heavy.effective(true)
        assertEquals(once, once.effective(true))
    }

    @Test
    fun aListenerWhoAlreadyReducedBlurKeepsItWhenPerformanceModeIsOff() {
        val reduced = heavy.copy(reduceDynamicBlur = true)
        assertTrue(reduced.effective(false).reduceDynamicBlur)
    }

    @Test
    fun turningPerformanceModeBackOffRestoresTheListenersChoices() {
        val chosen = heavy.copy(albumCanvas = false)
        chosen.effective(true)
        assertEquals(chosen, chosen.effective(false))
    }

    @Test
    fun defaultsMatchTheStoredPreferenceDefaults() {
        val defaults = VisualEffects()
        assertFalse(defaults.reduceDynamicBlur)
        assertTrue(defaults.liquidGlass)
        assertFalse(defaults.albumCanvas)
        assertTrue(defaults.artistBackgroundVideo)
    }
}
