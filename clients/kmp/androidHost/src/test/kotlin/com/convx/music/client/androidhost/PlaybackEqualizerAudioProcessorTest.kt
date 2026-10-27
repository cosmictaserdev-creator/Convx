package com.convx.music.client.androidhost

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convx.music.playback.EqualizerBand
import com.convx.music.playback.EqualizerProfile
import com.convx.music.playback.PlaybackSettings
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

@OptIn(UnstableApi::class)
class PlaybackEqualizerAudioProcessorTest {
    @Test
    fun enabledProfileChangesPcmSamples() {
        val samples = oneKilohertzTone()
        val processed = process(
            samples = samples,
            settings = PlaybackSettings(
                equalizerEnabled = true,
                equalizerProfile = EqualizerProfile(
                    preamp = 0.0,
                    bands = listOf(EqualizerBand(frequency = 1_000.0, gain = 12.0)),
                ),
            ),
        )

        assertNotEquals(samples.toList(), processed.toList())
    }

    @Test
    fun bitPerfectModeBypassesEnabledProfile() {
        val samples = oneKilohertzTone()
        val processed = process(
            samples = samples,
            settings = PlaybackSettings(
                bitPerfectEnabled = true,
                equalizerEnabled = true,
                equalizerProfile = EqualizerProfile(
                    preamp = 0.0,
                    bands = listOf(EqualizerBand(frequency = 1_000.0, gain = 12.0)),
                ),
            ),
        )

        assertArrayEquals(samples, processed)
    }

    private fun process(samples: ShortArray, settings: PlaybackSettings): ShortArray {
        val processor = PlaybackEqualizerAudioProcessor()
        processor.configure(FORMAT)
        processor.updateSettings(settings)
        processor.flush()

        val input = ByteBuffer.allocateDirect(samples.size * Short.SIZE_BYTES).order(ByteOrder.nativeOrder())
        samples.forEach { sample -> input.putShort(sample) }
        input.flip()
        processor.queueInput(input)

        val output = processor.output.order(ByteOrder.nativeOrder())
        return ShortArray(output.remaining() / Short.SIZE_BYTES) { output.short }
    }

    private fun oneKilohertzTone(): ShortArray = ShortArray(SAMPLE_COUNT) { index ->
        (sin(2.0 * PI * TEST_FREQUENCY_HZ * index / SAMPLE_RATE_HZ) * AMPLITUDE).toInt().toShort()
    }

    private companion object {
        const val SAMPLE_RATE_HZ = 44_100
        const val TEST_FREQUENCY_HZ = 1_000
        const val SAMPLE_COUNT = 1_024
        const val AMPLITUDE = 10_000.0
        val FORMAT = AudioProcessor.AudioFormat(SAMPLE_RATE_HZ, 1, C.ENCODING_PCM_16BIT)
    }
}
