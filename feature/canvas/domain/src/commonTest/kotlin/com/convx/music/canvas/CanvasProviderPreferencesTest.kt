package com.convx.music.canvas

import kotlin.test.Test
import kotlin.test.assertEquals

class CanvasProviderPreferencesTest {
    @Test
    fun restoresOrderAndAppendsNewProviders() {
        val preferences = CanvasProviderPreferences.fromStored("TIDAL,TIDAL,unknown,SPOTIFY", "TIDAL", false, 2)
        assertEquals(listOf(CanvasProvider.TIDAL, CanvasProvider.SPOTIFY, CanvasProvider.APPLE_MUSIC, CanvasProvider.CONVX), preferences.order)
        assertEquals(listOf(CanvasProvider.SPOTIFY, CanvasProvider.APPLE_MUSIC, CanvasProvider.CONVX), preferences.enabled)
    }

    @Test
    fun clampsWaitAndSupportsFirstAvailable() {
        val preferences = CanvasProviderPreferences.fromStored("", "", true, 50)
        assertEquals(CanvasSelectionMode.FIRST_AVAILABLE, preferences.mode)
        assertEquals(10_000L, preferences.priorityWaitMillis)
    }
}
