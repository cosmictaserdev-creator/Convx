package com.convx.music.client.androidhost

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convx.music.playback.TrackAnalysisGenerator
import com.convx.music.playback.smart.TrackAnalysis
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Collects decoded PCM without changing the samples sent to the output device. */
@UnstableApi
internal class PlaybackTrackAnalysisAudioProcessor(
    private val scope: CoroutineScope,
    private val onAnalysisReady: (String, TrackAnalysis) -> Unit,
) : AudioProcessor {
    private data class TrackRequest(val identity: String, val durationMs: Long?)

    private data class AnalysisRun(
        val identity: String,
        val durationMs: Long?,
        val format: AudioProcessor.AudioFormat,
        val bytesPerSample: Int,
        val bigEndian: Boolean,
        val generator: TrackAnalysisGenerator,
    ) {
        @Volatile var incomplete: Boolean = false
    }

    private sealed interface AnalysisEvent {
        data class Samples(val run: AnalysisRun, val bytes: ByteArray) : AnalysisEvent
        data class End(val run: AnalysisRun) : AnalysisEvent
    }

    private val analysisEvents = Channel<AnalysisEvent>(capacity = MAX_PENDING_CHUNKS)
    private val analysisWorker = scope.launch(Dispatchers.Default) {
        for (event in analysisEvents) {
            when (event) {
                is AnalysisEvent.Samples -> {
                    if (!event.run.incomplete) analyze(event.run, event.bytes)
                }
                is AnalysisEvent.End -> finish(event.run)
            }
        }
    }

    private var inputFormat = AudioProcessor.AudioFormat.NOT_SET
    private var pendingFormat = AudioProcessor.AudioFormat.NOT_SET
    @Volatile private var trackRequest: TrackRequest? = null
    @Volatile private var run: AnalysisRun? = null
    private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
    private var inputEnded = false
    private var monoScratch = FloatArray(0)

    fun resetForTrack(trackIdentity: String?, durationMs: Long?) {
        trackRequest = trackIdentity?.takeIf { it.isNotBlank() }?.let { TrackRequest(it, durationMs) }
        run = createRun()
    }

    fun release() {
        analysisEvents.close()
        analysisWorker.cancel()
    }

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        pendingFormat = inputAudioFormat
        return if (supports(inputAudioFormat)) inputAudioFormat else AudioProcessor.AudioFormat.NOT_SET
    }

    override fun isActive(): Boolean = supports(pendingFormat)

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        val current = run
        if (current != null) {
            val copy = ByteArray(inputBuffer.remaining())
            inputBuffer.duplicate().get(copy)
            if (!analysisEvents.trySend(AnalysisEvent.Samples(current, copy)).isSuccess) {
                // Analysis must never backpressure decoded audio. A dropped chunk invalidates
                // this run so no partial curve can be mistaken for a complete track.
                current.incomplete = true
            }
        }
        outputBuffer = inputBuffer.slice()
        inputBuffer.position(inputBuffer.limit())
    }

    private fun analyze(current: AnalysisRun, bytes: ByteArray) {
        val frameSize = current.bytesPerSample * current.format.channelCount
        if (frameSize <= 0) return
        val frames = bytes.size / frameSize
        if (frames <= 0) return
        if (monoScratch.size < frames) monoScratch = FloatArray(frames)

        val ordered = ByteBuffer.wrap(bytes).order(
            if (current.bigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN,
        )
        for (frame in 0 until frames) {
            var sum = 0.0f
            val frameStart = frame * frameSize
            for (channel in 0 until current.format.channelCount) {
                sum += readSample(ordered, frameStart + channel * current.bytesPerSample, current)
            }
            monoScratch[frame] = sum / current.format.channelCount
        }
        current.generator.acceptMonoSamples(monoScratch, length = frames)
    }

    private fun readSample(buffer: ByteBuffer, index: Int, current: AnalysisRun): Float = when {
        current.format.encoding == C.ENCODING_PCM_FLOAT -> buffer.getFloat(index)
        current.bytesPerSample == 2 -> buffer.getShort(index) / 32_768.0f
        current.bytesPerSample == 3 -> {
            val first = buffer.get(index).toInt() and 0xff
            val second = buffer.get(index + 1).toInt() and 0xff
            val third = buffer.get(index + 2).toInt() and 0xff
            val raw = if (current.bigEndian) {
                (first shl 16) or (second shl 8) or third
            } else {
                (third shl 16) or (second shl 8) or first
            }
            val signed = if (raw and 0x800000 != 0) raw or -0x1000000 else raw
            signed / 8_388_608.0f
        }
        else -> buffer.getInt(index) / 2_147_483_648.0f
    }

    override fun queueEndOfStream() {
        inputEnded = true
        val completed = run
        run = null
        if (completed != null && !analysisEvents.trySend(AnalysisEvent.End(completed)).isSuccess) {
            completed.incomplete = true
        }
    }

    private fun finish(completed: AnalysisRun) {
        if (completed.incomplete) return
        val expected = completed.durationMs?.takeIf { it > 0L }
        val analysis = completed.generator.finish(expected?.div(1_000.0)) ?: return
        onAnalysisReady(completed.identity, analysis)
    }

    override fun getOutput(): ByteBuffer = outputBuffer.also { outputBuffer = AudioProcessor.EMPTY_BUFFER }

    override fun isEnded(): Boolean = inputEnded && !outputBuffer.hasRemaining()

    @Deprecated("Deprecated in AudioProcessor")
    override fun flush() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
        inputFormat = pendingFormat
        run = createRun()
    }

    @Deprecated("Deprecated in AudioProcessor")
    override fun reset() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
        inputFormat = AudioProcessor.AudioFormat.NOT_SET
        pendingFormat = AudioProcessor.AudioFormat.NOT_SET
        trackRequest = null
        run = null
    }

    private fun createRun(): AnalysisRun? {
        val request = trackRequest ?: return null
        if (!supports(inputFormat)) return null
        return runCatching {
            AnalysisRun(
                identity = request.identity,
                durationMs = request.durationMs,
                format = inputFormat,
                bytesPerSample = bytesPerSample(inputFormat.encoding),
                bigEndian = inputFormat.encoding in BIG_ENDIAN_ENCODINGS,
                generator = TrackAnalysisGenerator(request.identity, inputFormat.sampleRate),
            )
        }.getOrNull()
    }

    private fun supports(format: AudioProcessor.AudioFormat): Boolean =
        format.sampleRate > 0 && format.channelCount > 0 && bytesPerSample(format.encoding) > 0

    private fun bytesPerSample(encoding: Int): Int = when (encoding) {
        C.ENCODING_PCM_16BIT, C.ENCODING_PCM_16BIT_BIG_ENDIAN -> 2
        C.ENCODING_PCM_24BIT, C.ENCODING_PCM_24BIT_BIG_ENDIAN -> 3
        C.ENCODING_PCM_32BIT, C.ENCODING_PCM_32BIT_BIG_ENDIAN, C.ENCODING_PCM_FLOAT -> 4
        else -> 0
    }

    private companion object {
        const val MAX_PENDING_CHUNKS = 32
        val BIG_ENDIAN_ENCODINGS = setOf(
            C.ENCODING_PCM_16BIT_BIG_ENDIAN,
            C.ENCODING_PCM_24BIT_BIG_ENDIAN,
            C.ENCODING_PCM_32BIT_BIG_ENDIAN,
        )
    }
}
