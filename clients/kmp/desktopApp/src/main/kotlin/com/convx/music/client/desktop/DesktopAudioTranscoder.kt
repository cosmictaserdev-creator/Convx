package com.convx.music.client.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runInterruptible
import com.convx.music.playback.TrackAnalysisGenerator
import com.convx.music.playback.PcmSamples
import com.convx.music.playback.smart.TrackAnalysis
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.ffmpeg.global.avutil.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException
import kotlin.math.roundToInt

internal class IncompleteAudioStreamException(message: String) : IOException(message)

/** Decodes provider and local audio without reducing its PCM precision or sample rate. */
internal class DesktopAudioTranscoder : AutoCloseable {
    private val directory = Files.createTempDirectory("convx-desktop-audio-")
    private val lifecycleLock = Any()
    @Volatile private var closed = false
    private var activeTranscodes = 0
    private var activeAnalysisReads = 0
    private val readersByFile = mutableMapOf<Path, Int>()
    private val deleteAfterRead = mutableSetOf<Path>()

    suspend fun transcode(sourceUrl: String, expectedDurationMs: Long? = null): Path {
        synchronized(lifecycleLock) {
            check(!closed) { "The desktop audio decoder has been closed." }
            activeTranscodes += 1
        }
        try {
            val completedFile = AtomicReference<Path?>()
            try {
                val file = runInterruptible(Dispatchers.IO) {
                    check(!closed) { "The desktop audio decoder has been closed." }
                    val destination = Files.createTempFile(directory, "track-", ".wav")
                    val grabber = FFmpegFrameGrabber(sourceUrl)
                    val recorder = FFmpegFrameRecorder(destination.toFile(), 0)
                    var completed = false
                    try {
                        grabber.setOption("rw_timeout", READ_TIMEOUT_MICROS.toString())
                        grabber.start()
                        check(grabber.sampleRate > 0 && grabber.audioChannels > 0) {
                            "The audio stream has no supported audio track."
                        }
                        recorder.format = "wav"
                        recorder.audioCodecName = pcmCodec(grabber)
                        recorder.sampleRate = grabber.sampleRate
                        recorder.audioChannels = grabber.audioChannels
                        recorder.start()

                        while (true) {
                            if (Thread.currentThread().isInterrupted) throw InterruptedException("Audio decoding was cancelled.")
                            val frame = grabber.grabSamples() ?: break
                            recorder.record(frame)
                        }
                        recorder.stop()
                        check(Files.size(destination) > WAV_HEADER_BYTES) { "The audio stream decoded to an empty file." }
                        validateDecodedDuration(destination, expectedDurationMs)
                        completed = true
                        completedFile.set(destination)
                        destination
                    } finally {
                        runCatching { recorder.stop() }
                        runCatching { recorder.release() }
                        runCatching { grabber.stop() }
                        runCatching { grabber.release() }
                        if (!completed) Files.deleteIfExists(destination)
                    }
                }
                currentCoroutineContext().ensureActive()
                return file
            } catch (error: Throwable) {
                completedFile.getAndSet(null)?.let(::delete)
                throw error
            }
        } finally {
            synchronized(lifecycleLock) {
                activeTranscodes -= 1
                if (closed && activeTranscodes == 0 && activeAnalysisReads == 0) deleteDirectoryContents()
            }
        }
    }

    /** Reads the decoded WAV off the UI thread while holding deletion until analysis completes. */
    suspend fun analyze(
        file: Path,
        trackId: String,
        expectedDurationMs: Long?,
    ): TrackAnalysis? {
        synchronized(lifecycleLock) {
            check(!closed) { "The desktop audio decoder has been closed." }
            require(file.parent == directory) { "Only audio files owned by this decoder can be analyzed." }
            activeAnalysisReads += 1
            readersByFile[file] = (readersByFile[file] ?: 0) + 1
        }
        try {
            return runInterruptible(Dispatchers.IO) {
                analyzeWav(file, trackId, expectedDurationMs)
            }
        } finally {
            synchronized(lifecycleLock) {
                activeAnalysisReads -= 1
                val remainingReaders = (readersByFile[file] ?: 1) - 1
                if (remainingReaders <= 0) {
                    readersByFile.remove(file)
                    if (deleteAfterRead.remove(file)) runCatching { Files.deleteIfExists(file) }
                } else {
                    readersByFile[file] = remainingReaders
                }
                if (closed && activeTranscodes == 0 && activeAnalysisReads == 0) deleteDirectoryContents()
            }
        }
    }

    fun delete(file: Path) {
        synchronized(lifecycleLock) {
            if (file.parent != directory) return
            if ((readersByFile[file] ?: 0) > 0) {
                deleteAfterRead.add(file)
            } else {
                runCatching { Files.deleteIfExists(file) }
            }
        }
    }

    override fun close() {
        synchronized(lifecycleLock) {
            if (closed) return
            closed = true
            if (activeTranscodes == 0 && activeAnalysisReads == 0) deleteDirectoryContents()
        }
    }

    private fun analyzeWav(file: Path, trackId: String, expectedDurationMs: Long?): TrackAnalysis? {
        AudioSystem.getAudioInputStream(file.toFile()).use { input ->
            val format = input.format
            val pcm = format.pcmFormat()
            val sampleRate = format.sampleRate.roundToInt()
            val channels = format.channels
            val frameSize = format.frameSize
            if (sampleRate <= 0 || channels <= 0 || frameSize < pcm.frameSize) return null

            val generator = TrackAnalysisGenerator(trackId, sampleRate)
            val bytes = ByteArray(PCM_READ_FRAMES * frameSize)
            val mono = FloatArray(PCM_READ_FRAMES)
            var carry = 0
            while (true) {
                if (Thread.currentThread().isInterrupted) throw InterruptedException("Track analysis was cancelled.")
                val read = input.read(bytes, carry, bytes.size - carry)
                if (read < 0) break
                if (read == 0) continue
                val available = carry + read
                val frames = available / frameSize
                for (frame in 0 until frames) {
                    var channelSum = 0.0f
                    val frameStart = frame * frameSize
                    for (channel in 0 until channels) {
                        val sampleOffset = frameStart + channel * pcm.bytesPerSample
                        channelSum += PcmSamples.read(bytes, sampleOffset, pcm).toFloat()
                    }
                    mono[frame] = channelSum / channels
                }
                if (frames > 0) generator.acceptMonoSamples(mono, length = frames)
                carry = available - frames * frameSize
                if (carry > 0) bytes.copyInto(bytes, destinationOffset = 0, startIndex = frames * frameSize, endIndex = available)
            }
            val expectedDuration = expectedDurationMs?.takeIf { it > 0L }?.div(1_000.0)
            return generator.finish(expectedDurationSeconds = expectedDuration)
        }
    }

    private fun pcmCodec(grabber: FFmpegFrameGrabber): String {
        val context = grabber.formatContext
        val sourceBits = context?.let { streamContext ->
            (0 until streamContext.nb_streams()).asSequence().map { streamContext.streams(it).codecpar() }
                .firstOrNull { it.codec_type() == AVMEDIA_TYPE_AUDIO }?.let {
                    it.bits_per_raw_sample().takeIf { bits -> bits > 0 } ?: it.bits_per_coded_sample()
                }
        } ?: 0
        return when (grabber.sampleFormat) {
            AV_SAMPLE_FMT_U8, AV_SAMPLE_FMT_U8P -> "pcm_u8"
            AV_SAMPLE_FMT_S16, AV_SAMPLE_FMT_S16P -> "pcm_s16le"
            AV_SAMPLE_FMT_S32, AV_SAMPLE_FMT_S32P -> if (sourceBits in 17..24) "pcm_s24le" else "pcm_s32le"
            AV_SAMPLE_FMT_FLT, AV_SAMPLE_FMT_FLTP -> "pcm_f32le"
            AV_SAMPLE_FMT_DBL, AV_SAMPLE_FMT_DBLP -> "pcm_f64le"
            else -> throw IOException("The decoder returned an unsupported PCM sample format.")
        }
    }

    private fun validateDecodedDuration(file: Path, expectedDurationMs: Long?) {
        val decodedDurationMs = try {
            AudioSystem.getAudioInputStream(file.toFile()).use { audio ->
                val sampleRate = audio.format.frameRate.toDouble()
                val frameCount = audio.frameLength
                if (sampleRate <= 0.0 || frameCount <= 0L) {
                    throw IncompleteAudioStreamException("The decoded audio has no measurable duration.")
                }
                (frameCount * MILLIS_PER_SECOND / sampleRate).toLong()
            }
        } catch (error: UnsupportedAudioFileException) {
            throw IncompleteAudioStreamException("The decoded audio file is incomplete or unreadable.").also {
                it.initCause(error)
            }
        }
        if (decodedDurationMs <= 0L) {
            throw IncompleteAudioStreamException("The decoded audio is empty.")
        }

        val expected = expectedDurationMs?.takeIf { it > 0L } ?: return
        val toleranceMs = (expected / EXPECTED_DURATION_TOLERANCE_DIVISOR)
            .coerceIn(MIN_EXPECTED_DURATION_TOLERANCE_MS, MAX_EXPECTED_DURATION_TOLERANCE_MS)
        val minimumDurationMs = (expected - toleranceMs).coerceAtLeast(1L)
        if (decodedDurationMs < minimumDurationMs) {
            throw IncompleteAudioStreamException(
                "The audio stream ended after ${decodedDurationMs}ms; expected about ${expected}ms.",
            )
        }
    }

    private fun deleteDirectoryContents() {
        runCatching {
            Files.list(directory).use { files ->
                files.forEach { file -> runCatching { Files.deleteIfExists(file) } }
            }
            Files.deleteIfExists(directory)
        }
    }

    private companion object {
        const val READ_TIMEOUT_MICROS = 30_000_000
        const val WAV_HEADER_BYTES = 44L
        const val PCM_READ_FRAMES = 4_096
        const val MILLIS_PER_SECOND = 1_000.0
        const val EXPECTED_DURATION_TOLERANCE_DIVISOR = 20L
        const val MIN_EXPECTED_DURATION_TOLERANCE_MS = 1_000L
        const val MAX_EXPECTED_DURATION_TOLERANCE_MS = 15_000L
    }
}
