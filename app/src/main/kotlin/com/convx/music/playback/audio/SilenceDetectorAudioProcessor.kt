/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

/**
 * Lightweight PCM pass-through processor that detects long stretches of near-silence.
 * When [instantModeEnabled] is true and a silence block longer than [minSilenceDurationUs]
 * is detected, [onLongSilence] is invoked exactly once per silent segment.
 */
@UnstableApi
@Suppress("DEPRECATION")
class SilenceDetectorAudioProcessor(
    private val minSilenceDurationUs: Long = 2_000_000L,
    private val silenceThreshold: Int = 256,
    private val onLongSilence: () -> Unit,
) : AudioProcessor {

    private var sampleRate = 0
    private var channelCount = 0
    private var encoding = C.ENCODING_INVALID
    private var minimumSilentFrames = 0L
    private val spectrumTap = SpectrumTap()

    private var outputBuffer: ByteBuffer = EMPTY_BUFFER
    private var inputEnded = false

    @Volatile
    var instantModeEnabled: Boolean = false

    @Volatile
    private var consecutiveSilentFrames: Long = 0

    @Volatile
    private var inSilence: Boolean = false

    private var notifiedThisSilence = false
    private val resetGeneration = AtomicLong()
    private var trackingGeneration = 0L

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        sampleRate = inputAudioFormat.sampleRate
        channelCount = inputAudioFormat.channelCount
        encoding = inputAudioFormat.encoding
        minimumSilentFrames = kotlin.math.ceil(
            minSilenceDurationUs.toDouble() * sampleRate / 1_000_000.0,
        ).toLong().coerceAtLeast(1L)

        // Bypass if not 16-bit PCM (e.g. 24-bit, float, or passthrough)
        if (encoding != C.ENCODING_PCM_16BIT) {
            sampleRate = 0
            channelCount = 0
            return inputAudioFormat
        }

        return inputAudioFormat
    }

    override fun isActive(): Boolean = encoding == C.ENCODING_PCM_16BIT

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) {
            outputBuffer = EMPTY_BUFFER
            return
        }

        val generation = resetGeneration.get()
        if (trackingGeneration != generation) {
            clearSilenceState()
            trackingGeneration = generation
        }
        if (instantModeEnabled && sampleRate > 0 && channelCount > 0) {
            detectSilence(inputBuffer, generation)
        } else {
            clearSilenceState()
        }

        if (LiveSpectrum.active && encoding == C.ENCODING_PCM_16BIT && sampleRate > 0 && channelCount > 0) {
            spectrumTap.feed(inputBuffer, sampleRate, channelCount)
        }

        outputBuffer = inputBuffer.slice().order(ByteOrder.nativeOrder())
        inputBuffer.position(inputBuffer.limit())
    }

    private fun detectSilence(inputBuffer: ByteBuffer, generation: Long) {
        // Ensure predictable endian access for getShort(index).
        inputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val frameCount = inputBuffer.remaining() / 2 / channelCount
        val basePosition = inputBuffer.position()

        var silentFrames = consecutiveSilentFrames
        var silent = inSilence
        var notified = notifiedThisSilence
        repeat(frameCount) { frameIndex ->
            var framePeak = 0
            repeat(channelCount) { channelIndex ->
                val sampleIndex = basePosition + (frameIndex * channelCount + channelIndex) * 2
                val sampleValue = abs(inputBuffer.getShort(sampleIndex).toInt())
                if (sampleValue > framePeak) {
                    framePeak = sampleValue
                }
            }

            if (framePeak < silenceThreshold) {
                silentFrames++
                if (silentFrames >= minimumSilentFrames) {
                    silent = true
                    if (!notified) {
                        notified = true
                        if (resetGeneration.get() == generation) onLongSilence()
                    }
                }
            } else {
                silentFrames = 0L
                silent = false
                notified = false
            }
        }
        if (resetGeneration.get() == generation) {
            consecutiveSilentFrames = silentFrames
            inSilence = silent
            notifiedThisSilence = notified
        }
    }

    private fun clearSilenceState() {
        consecutiveSilentFrames = 0
        inSilence = false
        notifiedThisSilence = false
    }

    fun resetTracking() {
        resetGeneration.incrementAndGet()
        inSilence = false
    }

    fun isCurrentlySilent(): Boolean = inSilence

    override fun queueEndOfStream() {
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val output = outputBuffer
        outputBuffer = EMPTY_BUFFER
        return output
    }

    override fun isEnded(): Boolean = inputEnded && outputBuffer === EMPTY_BUFFER

    @Deprecated("Deprecated in AudioProcessor")
    override fun flush() {
        outputBuffer = EMPTY_BUFFER
        inputEnded = false
        spectrumTap.reset()
        clearSilenceState()
        trackingGeneration = resetGeneration.get()
    }

    @Deprecated("Deprecated in AudioProcessor")
    override fun reset() {
        flush()
        sampleRate = 0
        channelCount = 0
        encoding = C.ENCODING_INVALID
    }

    companion object {
        private val EMPTY_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }
}
