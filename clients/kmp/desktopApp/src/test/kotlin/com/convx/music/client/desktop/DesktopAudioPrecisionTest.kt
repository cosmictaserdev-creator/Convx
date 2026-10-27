package com.convx.music.client.desktop

import com.convx.music.playback.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import javax.sound.sampled.*
import kotlin.test.*

class DesktopAudioPrecisionTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun decodingRetainsHighResolutionPcmAndQuietSamples() = runBlocking {
        val formats = listOf(PcmFormat(96_000, 2, 24), PcmFormat(192_000, 2, 32), PcmFormat(48_000, 2, 32, PcmEncoding.FLOAT))
        for (format in formats) {
            val source = temporary.newFile("precision-${format.bitsPerSample}-${format.encoding}.wav")
            val samples = listOf(-.75, 1.0 / 8_388_608, -.000001, .5)
            val data = ByteArray(4_800 * format.frameSize)
            repeat(4_800 * format.channels) { index -> PcmSamples.write(data, index * format.bytesPerSample, samples[index % samples.size], format) }
            AudioInputStream(ByteArrayInputStream(data), format.javaFormat(), 4_800).use { AudioSystem.write(it, AudioFileFormat.Type.WAVE, source) }
            val decoder = DesktopAudioTranscoder()
            try {
                val decoded = decoder.transcode(source.path)
                AudioSystem.getAudioInputStream(decoded.toFile()).use { input ->
                    val result = input.format.pcmFormat()
                    assertEquals(format.sampleRate, result.sampleRate)
                    assertEquals(format.channels, result.channels)
                    assertEquals(format.bitsPerSample, result.bitsPerSample)
                    assertEquals(format.encoding, result.encoding)
                    assertContentEquals(data, input.readAllBytes(), "Decoded samples changed for $format")
                }
            } finally { decoder.close() }
        }
    }
}
