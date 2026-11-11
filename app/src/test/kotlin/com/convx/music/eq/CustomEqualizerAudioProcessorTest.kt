package com.convx.music.eq

import com.convx.music.playback.EqualizerProfileParser
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import com.convx.music.eq.audio.CustomEqualizerAudioProcessor
import com.convx.music.eq.data.ParametricEQ
import com.convx.music.eq.data.ParametricEQBand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CustomEqualizerAudioProcessorTest {
    @Test
    fun `preamp applies even without any filters below nyquist`() {
        val processor = CustomEqualizerAudioProcessor()
        processor.applyProfile(ParametricEQ(-6.020599913, listOf(ParametricEQBand(16000.0, 6.0))))
        processor.configure(AudioProcessor.AudioFormat(22050, 1, C.ENCODING_PCM_16BIT))
        processor.flush()
        processor.queueInput(samples(10000))
        assertEquals(5000, processor.output.order(ByteOrder.nativeOrder()).short.toInt())
    }

    @Test
    fun `flat tuning leaves full scale samples unchanged`() {
        val processor = CustomEqualizerAudioProcessor().apply {
            configure(AudioProcessor.AudioFormat(48000, 2, C.ENCODING_PCM_16BIT))
            applyProfile(ParametricEQ(0.0, listOf(ParametricEQBand(1000.0, 0.0))))
        }
        processor.queueInput(samples(32767, -32768, 30000, -30000))
        assertEquals(samples(32767, -32768, 30000, -30000), processor.output)
    }

    @Test
    fun `profile is rebuilt for sample rate changes`() {
        val profile = ParametricEQ(-6.0, listOf(ParametricEQBand(1000.0, 6.0, 2.0)))
        val reused = CustomEqualizerAudioProcessor().apply {
            applyProfile(profile)
            configure(AudioProcessor.AudioFormat(44100, 1, C.ENCODING_PCM_16BIT))
            flush()
            queueInput(samples(10000, 0, 0, 0))
            output
            flush()
            configure(AudioProcessor.AudioFormat(96000, 1, C.ENCODING_PCM_16BIT))
            flush()
        }
        val fresh = CustomEqualizerAudioProcessor().apply {
            applyProfile(profile)
            configure(AudioProcessor.AudioFormat(96000, 1, C.ENCODING_PCM_16BIT))
            flush()
        }
        reused.queueInput(samples(10000, 0, 0, 0))
        fresh.queueInput(samples(10000, 0, 0, 0))
        assertEquals(fresh.output, reused.output)
    }

    @Test
    fun `bit perfect bypass is exact and restores tuning`() {
        val processor = CustomEqualizerAudioProcessor().apply {
            configure(AudioProcessor.AudioFormat(48000, 1, C.ENCODING_PCM_16BIT))
            flush()
            applyProfile(ParametricEQ(-6.0, listOf(ParametricEQBand(1000.0, 0.0))))
            forceBypass = true
        }
        processor.queueInput(samples(30000, -30000))
        assertEquals(samples(30000, -30000), processor.output)
        processor.forceBypass = false
        processor.queueInput(samples(10000))
        assertTrue(processor.output.order(ByteOrder.nativeOrder()).short.toInt() < 6000)
    }

    @Test
    fun `non finite frequency gain and bandwidth are rejected`() {
        assertTrue(EqualizerProfileParser.validate(ParametricEQ(0.0, listOf(ParametricEQBand(Double.NaN, 0.0)))).isNotEmpty())
        assertTrue(EqualizerProfileParser.validate(ParametricEQ(0.0, listOf(ParametricEQBand(1000.0, Double.POSITIVE_INFINITY)))).isNotEmpty())
        assertTrue(EqualizerProfileParser.validate(ParametricEQ(0.0, listOf(ParametricEQBand(1000.0, 0.0, Double.NaN)))).isNotEmpty())
    }

    private fun samples(vararg values: Int) = ByteBuffer.allocateDirect(values.size * 2).order(ByteOrder.nativeOrder()).apply {
        values.forEach { putShort(it.toShort()) }
        flip()
    }
}
