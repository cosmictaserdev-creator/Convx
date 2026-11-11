package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EqualizerProfileParserTest {
    @Test
    fun parsesPreampEnabledFiltersAndMetadata() {
        val profile = EqualizerProfileParser.parseText(
            """
            Device: Studio Headphones
            Preamp: -5.2 dB
            Filter 1: ON LSC Fc 105 Hz Gain 8.8 dB Q 0.70
            Filter 2: OFF PK Fc 70 Hz Gain -6.7 dB Q 0.29
            Filter 3: ON HSC Fc 10000 Hz Gain -2 dB Q 0.7
            """.trimIndent(),
        )

        assertEquals(-5.2, profile.preamp, 0.0001)
        assertEquals(listOf(EqualizerFilterType.LSC, EqualizerFilterType.HSC), profile.bands.map { it.filterType })
        assertEquals(105.0, profile.bands[0].frequency, 0.0001)
        assertEquals(0.70, profile.bands[0].q, 0.0001)
        assertEquals("Studio Headphones", profile.metadata["Device"])
        assertTrue(EqualizerProfileParser.validate(profile).isEmpty())
    }

    @Test
    fun rejectsEmptyAndOutOfRangeProfiles() {
        assertTrue(EqualizerProfileParser.validate(EqualizerProfileParser.parseText("Preamp: 0 dB")).isNotEmpty())
        val loud = EqualizerProfileParser.parseText("Filter 1: ON PK Fc 1000 Hz Gain 31 dB Q 1.41")
        assertEquals(1, EqualizerProfileParser.validate(loud).size)
        val badQ = EqualizerProfile(0.0, listOf(EqualizerBand(1000.0, 0.0, q = 0.0)))
        assertEquals(1, EqualizerProfileParser.validate(badQ).size)
    }
}
