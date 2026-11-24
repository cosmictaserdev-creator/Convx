package com.convx.music.client.androidhost

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convx.music.playback.EqualizerBiquadFilter
import com.convx.music.playback.EqualizerFilterType
import com.convx.music.playback.PlaybackSettings
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow
import kotlin.math.tanh

/** Applies the persisted shared parametric profile to PCM16 without blocking the UI thread. */
@UnstableApi
internal class PlaybackEqualizerAudioProcessor : AudioProcessor {
    private data class RequestedSettings(
        val enabled: Boolean = false,
        val profile: PlaybackSettings = PlaybackSettings(),
    )

    @Volatile
    private var requestedSettings = RequestedSettings()

    private var appliedSettings: RequestedSettings? = null
    private var pendingFormat = AudioProcessor.AudioFormat.NOT_SET
    private var inputFormat = AudioProcessor.AudioFormat.NOT_SET
    private var filters: List<EqualizerBiquadFilter> = emptyList()
    private var preampGain = 1.0
    private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
    private var outputScratch: ByteBuffer? = null
    private var inputEnded = false

    fun updateSettings(settings: PlaybackSettings) {
        requestedSettings = RequestedSettings(
            enabled = settings.equalizerEnabled && !settings.bitPerfectEnabled,
            profile = settings.normalized(),
        )
    }

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        pendingFormat = inputAudioFormat
        return if (supports(inputAudioFormat)) inputAudioFormat else AudioProcessor.AudioFormat.NOT_SET
    }

    override fun isActive(): Boolean = supports(pendingFormat)

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        val settings = requestedSettings
        if (settings != appliedSettings) rebuildFilters(settings)

        if (!settings.enabled || (filters.isEmpty() && preampGain == 1.0)) {
            outputBuffer = inputBuffer.slice()
            inputBuffer.position(inputBuffer.limit())
            return
        }

        val input = inputBuffer.slice().order(ByteOrder.nativeOrder())
        val frameSize = inputFormat.channelCount * Short.SIZE_BYTES
        val outputSize = input.remaining()
        val output = outputScratch
            ?.takeIf { it.capacity() >= outputSize }
            ?.apply { clear() }
            ?: ByteBuffer.allocateDirect(outputSize).also { outputScratch = it }
        output.order(ByteOrder.nativeOrder())
        while (input.remaining() >= frameSize) {
            var left = input.short / 32_768.0
            var right = if (inputFormat.channelCount == 2) input.short / 32_768.0 else 0.0
            for (filter in filters) {
                left = filter.processSample(left)
                if (inputFormat.channelCount == 2) right = filter.processRightSample(right)
            }
            output.putShort(toPcm16(left * preampGain))
            if (inputFormat.channelCount == 2) output.putShort(toPcm16(right * preampGain))
        }
        if (input.hasRemaining()) output.put(input)
        output.flip()
        outputBuffer = output
        inputBuffer.position(inputBuffer.limit())
    }

    private fun rebuildFilters(settings: RequestedSettings) {
        appliedSettings = settings
        filters = emptyList()
        preampGain = 1.0
        if (!settings.enabled || !supports(inputFormat)) return

        val sampleRate = inputFormat.sampleRate
        val profile = settings.profile.equalizerProfile
        preampGain = 10.0.pow(profile.preamp / 20.0)
        filters = profile.bands.asSequence()
            .filter { band ->
                band.enabled && band.gain != 0.0 && band.frequency < sampleRate / 2.0
            }
            .map { band ->
                EqualizerBiquadFilter(
                    sampleRate = sampleRate,
                    frequency = band.frequency,
                    gain = band.gain,
                    q = band.q,
                    filterType = when (band.filterType) {
                        com.convx.music.playback.EqualizerFilterType.PK -> EqualizerFilterType.PK
                        com.convx.music.playback.EqualizerFilterType.LSC -> EqualizerFilterType.LSC
                        com.convx.music.playback.EqualizerFilterType.HSC -> EqualizerFilterType.HSC
                        com.convx.music.playback.EqualizerFilterType.LPQ -> EqualizerFilterType.LPQ
                        com.convx.music.playback.EqualizerFilterType.HPQ -> EqualizerFilterType.HPQ
                    },
                )
            }
            .toList()
    }

    private fun toPcm16(sample: Double): Short {
        val softened = when {
            sample > CLIP_THRESHOLD -> CLIP_THRESHOLD + (1.0 - CLIP_THRESHOLD) * tanh(
                (sample - CLIP_THRESHOLD) / (1.0 - CLIP_THRESHOLD),
            )
            sample < -CLIP_THRESHOLD -> -(CLIP_THRESHOLD + (1.0 - CLIP_THRESHOLD) * tanh(
                (-sample - CLIP_THRESHOLD) / (1.0 - CLIP_THRESHOLD),
            ))
            else -> sample
        }
        return (softened * 32_768.0).coerceIn(-32_768.0, 32_767.0).toInt().toShort()
    }

    override fun getOutput(): ByteBuffer = outputBuffer.also {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
    }

    override fun queueEndOfStream() {
        inputEnded = true
    }

    override fun isEnded(): Boolean = inputEnded && !outputBuffer.hasRemaining()

    @Deprecated("Deprecated in AudioProcessor")
    override fun flush() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
        inputFormat = pendingFormat
        rebuildFilters(requestedSettings)
        filters.forEach(EqualizerBiquadFilter::reset)
    }

    @Deprecated("Deprecated in AudioProcessor")
    override fun reset() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
        pendingFormat = AudioProcessor.AudioFormat.NOT_SET
        inputFormat = AudioProcessor.AudioFormat.NOT_SET
        filters = emptyList()
        preampGain = 1.0
        appliedSettings = null
        outputScratch = null
    }

    private fun supports(format: AudioProcessor.AudioFormat): Boolean =
        format.encoding == C.ENCODING_PCM_16BIT &&
            format.sampleRate in MIN_SAMPLE_RATE..MAX_SAMPLE_RATE &&
            format.channelCount in 1..2

    private companion object {
        const val MIN_SAMPLE_RATE = 8_000
        const val MAX_SAMPLE_RATE = 192_000
        const val CLIP_THRESHOLD = 0.9
    }
}
