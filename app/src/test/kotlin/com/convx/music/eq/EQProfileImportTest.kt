package com.convx.music.eq

import com.convx.music.eq.data.EQProfileImport
import org.junit.Assert.assertEquals
import org.junit.Test

class EQProfileImportTest {
    @Test fun importsPresetWithUtf8Bom() {
        val input = "\uFEFFPreamp: -5.2 dB\nFilter 1: ON PK Fc 1000 Hz Gain -2.5 dB Q 1.41"
        val profile = EQProfileImport.read(input.byteInputStream())
        assertEquals(-5.2, profile.preamp, 0.001)
        assertEquals(1, profile.bands.size)
        assertEquals(-2.5, profile.bands.single().gain, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnrecognizedText() { EQProfileImport.read("not a preset".byteInputStream()) }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidFilter() {
        EQProfileImport.read("Filter 1: ON PK Fc 1000 Hz Gain 200 dB Q 1.41".byteInputStream())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOversizedFiles() { EQProfileImport.read(ByteArray(65537).inputStream()) }
}
