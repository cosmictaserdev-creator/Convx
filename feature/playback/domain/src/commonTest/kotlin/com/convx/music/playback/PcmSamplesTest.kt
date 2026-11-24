package com.convx.music.playback

import kotlin.test.*

class PcmSamplesTest {
    @Test fun integerPcmRetainsQuietSamplesAndFullScaleAcrossPrecisions() {
        for (bits in listOf(8, 16, 24, 32)) for (bigEndian in listOf(false, true)) {
            val format = PcmFormat(96_000, 2, bits, bigEndian = bigEndian)
            val scale = 1L shl (bits - 1)
            for (value in listOf(-scale, -scale + 1, -257, -1, 0, 1, 127, 257, scale - 1).filter { it in -scale until scale }) {
                val bytes = ByteArray(format.bytesPerSample)
                PcmSamples.write(bytes, 0, value / scale.toDouble(), format)
                assertEquals(value / scale.toDouble(), PcmSamples.read(bytes, 0, format), "PCM$bits, endian=$bigEndian, sample=$value")
            }
        }
    }

    @Test fun floatPcmRetainsSubSixteenBitSamples() {
        for (bits in listOf(32, 64)) {
            val format = PcmFormat(192_000, 1, bits, PcmEncoding.FLOAT)
            val bytes = ByteArray(format.bytesPerSample)
            val value = 1.0 / 1_048_576
            PcmSamples.write(bytes, 0, value, format)
            assertEquals(value, PcmSamples.read(bytes, 0, format))
        }
    }

    @Test fun exclusiveAccessAndUnchangedFormatAreRequiredForBitPerfectState() {
        val source = PcmFormat(96_000, 2, 24)
        val output = PlaybackAudioOutput(source, source, PlaybackAudioOutputMode.EXCLUSIVE, "DAC", false)
        assertTrue(output.bitPerfect)
        assertFalse(output.copy(mode = PlaybackAudioOutputMode.SYSTEM_MIXER).bitPerfect)
        assertFalse(output.copy(processingApplied = true).bitPerfect)
        assertFalse(output.copy(deviceFormat = source.copy(sampleRate = 48_000)).bitPerfect)
        assertFalse(output.copy(deviceFormat = source.copy(bitsPerSample = 16)).bitPerfect)
    }
}
