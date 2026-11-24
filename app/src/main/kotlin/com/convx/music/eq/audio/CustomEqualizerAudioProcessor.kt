package com.convx.music.eq.audio

import com.convx.music.playback.EqualizerProfileParser
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convx.music.eq.data.ParametricEQ
import com.convx.music.eq.data.ParametricEQBand
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow
import kotlin.math.tanh

/**
 * Custom audio processor for ExoPlayer that applies parametric EQ using biquad filters
 * Uses ParametricEQ format from AutoEQ project
 */
@UnstableApi
@SuppressWarnings("Deprecated")
class CustomEqualizerAudioProcessor : AudioProcessor {

    private var sampleRate = 0
    private var channelCount = 0
    private var encoding = C.ENCODING_INVALID
    private var pendingFormat = AudioProcessor.AudioFormat.NOT_SET
    @Volatile private var equalizerEnabled = false

    /** Bit Perfect bypass preserves the saved profile. */
    @Volatile
    var forceBypass = false

    private var processingBuffer: ByteBuffer = EMPTY_BUFFER
    private var outputBuffer: ByteBuffer = EMPTY_BUFFER
    private var inputEnded = false

    private var filters: List<BiquadFilter> = emptyList()
    private var preampGain: Double = 1.0  // Linear preamp gain multiplier
    private var pendingProfile: ParametricEQ? = null

    companion object {
        private const val TAG = "CustomEqualizerAudioProcessor"
        private val EMPTY_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    /**
     * Apply an EQ profile
     */
    @Synchronized
    fun applyProfile(parametricEQ: ParametricEQ) {
        require(EqualizerProfileParser.validate(parametricEQ).isEmpty()) { "Invalid EQ profile" }
        // Retain the profile so a new sample rate rebuilds every coefficient.
        pendingProfile = parametricEQ
        if (sampleRate <= 0 || encoding != C.ENCODING_PCM_16BIT || channelCount !in 1..2) {
            Timber.tag(TAG)
                .d("Audio processor not configured yet. Storing profile as pending with ${parametricEQ.bands.size} bands")
            return
        }

        preampGain = 10.0.pow(parametricEQ.preamp / 20.0)

        createFilters(parametricEQ.bands)
        equalizerEnabled = true

        filters.forEach { it.reset() }

        Timber.tag(TAG)
            .d("Applied EQ profile with ${filters.size} bands and ${parametricEQ.preamp} dB preamp")
    }

    /**
     * Disable the equalizer
     */
    @Synchronized
    fun disable() {
        equalizerEnabled = false
        filters = emptyList()
        preampGain = 1.0
        pendingProfile = null
        Timber.tag(TAG).d("Equalizer disabled")
    }

    fun isEnabled(): Boolean = equalizerEnabled

    /** Builds enabled bands below Nyquist using PK, LSC, or HSC filters. */
    private fun createFilters(bands: List<ParametricEQBand>) {
        if (sampleRate == 0) {
            Timber.tag(TAG).w("Cannot create filters: sample rate not set")
            return
        }

        // Filter out disabled bands and frequencies above Nyquist limit
        filters = bands
            .filter { it.enabled && it.gain != 0.0 && it.frequency < sampleRate / 2.0 }
            .map { band ->
                BiquadFilter(
                    sampleRate = sampleRate,
                    frequency = band.frequency,
                    gain = band.gain,
                    q = band.q,
                    filterType = band.filterType
                )
            }

        Timber.tag(TAG)
            .d("Created ${filters.size} biquad filters from ${bands.size} bands (PK/LSC/HSC)")
    }

    @Synchronized
    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        pendingFormat = inputAudioFormat
        return inputAudioFormat
    }

    private fun applyFormat() {
        sampleRate = pendingFormat.sampleRate
        channelCount = pendingFormat.channelCount
        encoding = pendingFormat.encoding
        if (!supports(pendingFormat)) {
            filters = emptyList()
            return
        }
        pendingProfile?.let { profile ->
            preampGain = 10.0.pow(profile.preamp / 20.0)
            createFilters(profile.bands)
            equalizerEnabled = true
        }
    }

    private fun supports(format: AudioProcessor.AudioFormat): Boolean =
        format.encoding == C.ENCODING_PCM_16BIT && format.sampleRate > 0 && format.channelCount in 1..2

    override fun isActive(): Boolean = supports(pendingFormat) && !forceBypass

    @Synchronized
    override fun queueInput(inputBuffer: ByteBuffer) {
        inputBuffer.order(ByteOrder.nativeOrder())
        val inputSize = inputBuffer.remaining()
        if (inputSize == 0) return
        if (forceBypass || !equalizerEnabled || channelCount !in 1..2 ||
            encoding != C.ENCODING_PCM_16BIT || (filters.isEmpty() && preampGain == 1.0)
        ) {
            outputBuffer = inputBuffer.slice().order(ByteOrder.nativeOrder())
            inputBuffer.position(inputBuffer.limit())
            return
        }
        val output = replaceOutputBuffer(inputSize)

        processAudioBuffer16Bit(inputBuffer, output)
        output.flip()
    }

    private fun replaceOutputBuffer(size: Int): ByteBuffer {
        if (processingBuffer.capacity() < size) {
            processingBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
        } else {
            processingBuffer.clear()
        }
        outputBuffer = processingBuffer
        return outputBuffer
    }

    /** Softens peaks above 0.9 without changing quieter samples. */
    private fun softClip(x: Double): Double {
        val threshold = 0.9
        return when {
            x > threshold -> threshold + (1 - threshold) * tanh((x - threshold) / (1 - threshold))
            x < -threshold -> -(threshold + (1 - threshold) * tanh((-x - threshold) / (1 - threshold)))
            else -> x
        }
    }

    private fun toPcm16Sample(sample: Double): Short =
        (softClip(sample) * 32768.0).coerceIn(-32768.0, 32767.0).toInt().toShort()

    private fun processAudioBuffer16Bit(input: ByteBuffer, output: ByteBuffer) {
        val frames = input.remaining() / (Short.SIZE_BYTES * channelCount)
        repeat(frames) {
            var left = input.short / 32768.0
            var right = if (channelCount == 2) input.short / 32768.0 else 0.0
            for (index in filters.indices) {
                val filter = filters[index]
                left = filter.processSample(left)
                if (channelCount == 2) right = filter.processRightSample(right)
            }
            output.putShort(toPcm16Sample(left * preampGain))
            if (channelCount == 2) output.putShort(toPcm16Sample(right * preampGain))
        }
        output.put(input)
    }

    override fun getOutput(): ByteBuffer {
        // queueInput flips this buffer before exposing it here.
        val buffer = outputBuffer
        outputBuffer = EMPTY_BUFFER
        return buffer
    }

    override fun isEnded(): Boolean {
        return inputEnded && outputBuffer.remaining() == 0
    }

    @Deprecated("Deprecated in Java")
    @Synchronized
    override fun flush() {
        outputBuffer = EMPTY_BUFFER
        inputEnded = false
        applyFormat()

        filters.forEach { it.reset() }
    }

    @Synchronized
    override fun reset() {
        pendingFormat = AudioProcessor.AudioFormat.NOT_SET
        @Suppress("DEPRECATION")
        flush()
        processingBuffer = EMPTY_BUFFER
        sampleRate = 0
        channelCount = 0
        encoding = C.ENCODING_INVALID
        filters.forEach { it.reset() }
    }

    override fun queueEndOfStream() {
        inputEnded = true
    }
}
