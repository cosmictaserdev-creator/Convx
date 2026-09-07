/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convxy.music.playback.dj.BeatGrid
import com.convxy.music.playback.dj.BeatTracker
import com.convxy.music.playback.dj.KeyDetector
import com.convxy.music.playback.dj.MusicalKey
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Passive, always-transparent (never alters the audio) tap that feeds decoded
 * PCM to [BeatTracker] and [KeyDetector].
 *
 * Deliberately thin: it unpacks samples and nothing else. Everything that can
 * actually be wrong — tempo, phase, confidence — lives in [BeatTracker], which
 * has no Android or media3 dependency and is unit-tested on the JVM.
 *
 * Analyzes [analysisWindowMs] of track audio, reports once via
 * [onAnalysisReady], then goes idle until [resetForTrack] is called for the
 * next one.
 */
@UnstableApi
class TrackAnalyzerAudioProcessor(
    private val onAnalysisReady: () -> Unit,
    /**
     * Live read of whether DJ mode is on. Beat tracking and key detection are a
     * DJ-mode feature, but this processor sits in the chain unconditionally, so
     * without this gate the full DSP ran on every buffer of every track with DJ
     * mode switched off. Read per configure/buffer rather than captured, so
     * toggling the preference takes effect without rebuilding the player.
     */
    private val analysisEnabled: () -> Boolean = { true },
) : AudioProcessor {

    private var channelCount = 0
    private var bytesPerSample = 0
    private var isFloatEncoding = false
    private var tracker: BeatTracker? = null
    private var keyDetector: KeyDetector? = null

    private var outputBuffer: ByteBuffer = EMPTY_BUFFER
    private var inputEnded = false
    private var reported = false
    private var pendingStartPositionMs = 0L

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        channelCount = inputAudioFormat.channelCount
        isFloatEncoding = inputAudioFormat.encoding == C.ENCODING_PCM_FLOAT
        bytesPerSample = when (inputAudioFormat.encoding) {
            C.ENCODING_PCM_16BIT, C.ENCODING_PCM_16BIT_BIG_ENDIAN -> 2
            C.ENCODING_PCM_24BIT, C.ENCODING_PCM_24BIT_BIG_ENDIAN -> 3
            C.ENCODING_PCM_32BIT, C.ENCODING_PCM_32BIT_BIG_ENDIAN, C.ENCODING_PCM_FLOAT -> 4
            else -> 0
        }
        val unsupported = bytesPerSample == 0 || channelCount <= 0 || inputAudioFormat.sampleRate <= 0
        if (unsupported) {
            Timber.tag(TAG).w(
                "No DJ analysis for this stream: encoding=${inputAudioFormat.encoding} " +
                    "channels=${inputAudioFormat.channelCount} rate=${inputAudioFormat.sampleRate}"
            )
        }
        tracker = if (unsupported) {
            null
        } else {
            BeatTracker(inputAudioFormat.sampleRate).also { it.beginAt(pendingStartPositionMs) }
        }
        keyDetector = if (unsupported) null else KeyDetector(inputAudioFormat.sampleRate)
        return inputAudioFormat
    }

    override fun isActive(): Boolean = tracker != null && analysisEnabled()

    /**
     * Energy curve for the track analyzed so far, one byte per second, or empty
     * if there isn't one yet. Read on demand rather than delivered with
     * [onAnalysisReady]: the grid is final after [analysisWindowMs], but the
     * energy profile keeps growing for as long as the track plays, and the
     * useful read is at transition time when it covers the outro.
     */
    val energyProfile: ByteArray get() = tracker?.energyProfile() ?: ByteArray(0)

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) {
            outputBuffer = EMPTY_BUFFER
            return
        }

        // Keeps running after `reported`: the beat grid is done, but the energy
        // profile accumulates for the whole track.
        analyze(inputBuffer)

        val out = replaceOutputBuffer(inputBuffer.remaining())
        out.put(inputBuffer)
        out.flip()
    }

    private fun analyze(buffer: ByteBuffer) {
        if (!analysisEnabled()) return
        val beatTracker = tracker ?: return
        val detector = keyDetector
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val frameSize = bytesPerSample * channelCount
        if (frameSize <= 0) return
        val frameCount = buffer.remaining() / frameSize
        val basePosition = buffer.position()

        repeat(frameCount) { frameIndex ->
            // Mono downmix. The old analyzer took the per-frame peak across
            // channels, which biases the envelope toward whichever channel is
            // momentarily loudest and adds jitter the tempo search has to fight.
            var sum = 0f
            repeat(channelCount) { channelIndex ->
                val sampleIndex = basePosition + (frameIndex * channelCount + channelIndex) * bytesPerSample
                sum += readSample(buffer, sampleIndex)
            }
            val mono = sum / channelCount
            beatTracker.accept(mono)
            if (!reported) detector?.accept(mono)
        }

        if (!reported && beatTracker.isAnalysisComplete) {
            // Only signals. The analysis itself is deliberately not run here —
            // see computeAnalysis().
            reported = true
            onAnalysisReady()
        }
    }

    /**
     * Runs the actual tempo, phase and key analysis.
     *
     * Deliberately NOT called from [queueInput]: the autocorrelation and the
     * tempo/phase search are hundreds of thousands of operations, and a spike
     * that size on the audio thread risks an underrun — more so on the probe
     * player, where buffers arrive six times faster than real time.
     *
     * Safe to call from another thread once [onAnalysisReady] has fired: that
     * only happens when the beat tracker's envelope is full, at which point the
     * arrays this reads have stopped being written to.
     */
    fun computeAnalysis(): Pair<BeatGrid, MusicalKey> =
        (tracker?.estimate() ?: BeatGrid.NONE) to (keyDetector?.estimate() ?: MusicalKey.NONE)

    /** Normalized to roughly [-1, 1] regardless of source encoding. */
    private fun readSample(buffer: ByteBuffer, index: Int): Float = when {
        isFloatEncoding -> buffer.getFloat(index)
        bytesPerSample == 2 -> buffer.getShort(index) / 32768f
        bytesPerSample == 3 -> {
            val b0 = buffer.get(index).toInt() and 0xFF
            val b1 = buffer.get(index + 1).toInt() and 0xFF
            val b2 = buffer.get(index + 2).toInt()
            ((b2 shl 16) or (b1 shl 8) or b0) / 8388608f
        }
        else -> buffer.getInt(index) / 2147483648f
    }

    /**
     * Restart analysis for a new track. [startPositionMs] is the track position
     * the next decoded audio corresponds to — the beat grid's anchor is
     * absolute track time, so a probe that seeks into the middle of a track
     * must say so or every beat time it reports is wrong.
     */
    fun resetForTrack(startPositionMs: Long = 0L) {
        pendingStartPositionMs = startPositionMs
        reported = false
        tracker?.beginAt(startPositionMs)
        keyDetector?.reset()
    }

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
    }

    @Deprecated("Deprecated in AudioProcessor")
    override fun reset() {
        flush()
        channelCount = 0
        bytesPerSample = 0
        tracker = null
        keyDetector = null
        resetForTrack()
    }

    private fun replaceOutputBuffer(size: Int): ByteBuffer {
        if (outputBuffer.capacity() < size) {
            outputBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
        } else {
            outputBuffer.clear()
        }
        return outputBuffer
    }

    companion object {
        private const val TAG = "TrackAnalyzer"

        private val EMPTY_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }
}
