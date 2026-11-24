package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals

class PlaybackSettingsCommonTest {
    @Test
    fun appearanceNormalizesImportedColorsAndNonFiniteControls() {
        val appearance = AppearanceSettings(accentArgb = 0x00224466, fontScale = Float.NaN,
            glassBlurRadius = Float.POSITIVE_INFINITY, glassOpacity = -10f, sidebarWidth = 900f).normalized()
        assertEquals(0xFF224466, appearance.accentArgb)
        assertEquals(1f, appearance.fontScale)
        assertEquals(16f, appearance.glassBlurRadius)
        assertEquals(0.1f, appearance.glassOpacity)
        assertEquals(320f, appearance.sidebarWidth)
    }
    @Test
    fun volumeAndLyricsPreferencesRejectInvalidRanges() {
        val settings = PlaybackSettings(volume = Float.NaN, lyricsFontSize = Float.POSITIVE_INFINITY, lyricsOffsetMs = Long.MAX_VALUE).normalized()
        assertEquals(1f, settings.volume)
        assertEquals(30f, settings.lyricsFontSize)
        assertEquals(5_000L, settings.lyricsOffsetMs)
        assertEquals(0f, PlaybackSettings(volume = -1f).normalized().volume)
        assertEquals(1f, PlaybackSettings(volume = 4f).normalized().volume)
    }
    @Test
    fun transitionModeRespectsCrossfadeAndBitPerfectSettings() {
        assertEquals(PlaybackTransitionMode.OFF, PlaybackSettings().transitionMode)
        assertEquals(
            PlaybackTransitionMode.CROSSFADE,
            PlaybackSettings(crossfadeEnabled = true).transitionMode,
        )
        assertEquals(
            PlaybackTransitionMode.AUTO_MIX,
            PlaybackSettings(crossfadeEnabled = true, autoMixEnabled = true).transitionMode,
        )
        assertEquals(
            PlaybackTransitionMode.OFF,
            PlaybackSettings(crossfadeEnabled = true, autoMixEnabled = true, bitPerfectEnabled = true).transitionMode,
        )
    }

    @Test
    fun normalizedSettingsClampFadeAndDiscardInvalidEqualizerBands() {
        val settings = PlaybackSettings(
            crossfadeDurationMs = Long.MAX_VALUE,
            equalizerProfile = EqualizerProfile(
                preamp = Double.NaN,
                bands = listOf(
                    EqualizerBand(frequency = 1_000.0, gain = 3.0),
                    EqualizerBand(frequency = Double.NaN, gain = 2.0),
                    EqualizerBand(frequency = 2_000.0, gain = 40.0),
                ),
            ),
        ).normalized()

        assertEquals(PlaybackSettings.MAX_CROSSFADE_DURATION_MS, settings.crossfadeDurationMs)
        assertEquals(0.0, settings.equalizerProfile.preamp)
        assertEquals(listOf(1_000.0), settings.equalizerProfile.bands.map { it.frequency })
    }
}
