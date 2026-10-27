package com.convx.music.client.desktop

import com.convx.music.playback.PcmFormat
import com.convx.music.playback.PcmSamples
import java.io.File
import java.io.IOException
import java.nio.file.Path
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.roundToLong

internal class DesktopPcmFile(
    val trackId: String,
    private val file: File,
    private val ownedFile: Path?,
    private val releaseOwnedFile: (Path) -> Unit,
) : AutoCloseable {
    private var input: AudioInputStream = AudioSystem.getAudioInputStream(file)
    val format: PcmFormat = input.format.pcmFormat()
    val totalFrames: Long = input.frameLength.also { require(it > 0) { "The decoded audio has no sample frames." } }
    val durationMs: Long get() = totalFrames * 1_000 / format.sampleRate
    var positionFrames = 0.0
        private set
    var rate = 1.0
    private var closed = false
    private val raw = ByteArray(4_096 * format.frameSize)
    private var rawOffset = 0
    private var rawSize = 0
    private var processing = false
    private var primed = false
    private var fraction = 0.0
    private val current = DoubleArray(format.channels)
    private val next = DoubleArray(format.channels)

    fun seek(positionMs: Long) = seekFrame((positionMs.coerceAtLeast(0) * format.sampleRate / 1_000).coerceAtMost(totalFrames))

    private fun seekFrame(frame: Long) {
        input.close()
        input = AudioSystem.getAudioInputStream(file)
        var remaining = frame * format.frameSize
        while (remaining > 0) {
            val skipped = input.skip(remaining)
            if (skipped <= 0) throw IOException("Couldn't seek within the decoded audio file.")
            remaining -= skipped
        }
        positionFrames = frame.toDouble()
        rawOffset = 0; rawSize = 0; primed = false; fraction = 0.0; processing = false
    }

    fun readDirect(destination: ByteArray, maxFrames: Int): Int {
        if (processing) seekFrame(positionFrames.roundToLong().coerceAtMost(totalFrames))
        val count = minOf(maxFrames.toLong(), totalFrames - positionFrames.toLong()).toInt()
        if (count <= 0) return 0
        val read = input.read(destination, 0, count * format.frameSize)
        if (read < 0) throw IOException("The decoded PCM file ended before its final sample frame.")
        positionFrames += read / format.frameSize
        return read
    }

    /** Conversion is used only for enabled transitions, never the direct/exclusive path. */
    fun readProcessed(destination: DoubleArray, maxFrames: Int, output: PcmFormat): Int {
        if (!processing) { processing = true; primed = false; fraction = 0.0 }
        if (!primed) {
            if (!nextFrame(current)) return 0
            if (!nextFrame(next)) current.copyInto(next)
            primed = true
        }
        val step = format.sampleRate.toDouble() / output.sampleRate * rate
        var frames = 0
        while (frames < maxFrames && positionFrames < totalFrames) {
            for (channel in 0 until output.channels) {
                val sample = if (output.channels == 1 && format.channels > 1) {
                    var sum = 0.0
                    for (source in current.indices) sum += current[source] + (next[source] - current[source]) * fraction
                    sum / format.channels
                } else {
                    val source = if (format.channels == 1) 0 else channel
                    if (source < format.channels) current[source] + (next[source] - current[source]) * fraction else 0.0
                }
                destination[frames * output.channels + channel] = sample
            }
            frames++
            positionFrames = (positionFrames + step).coerceAtMost(totalFrames.toDouble())
            fraction += step
            while (fraction >= 1.0) {
                next.copyInto(current)
                if (!nextFrame(next)) current.copyInto(next)
                fraction -= 1.0
            }
        }
        return frames
    }

    private fun nextFrame(destination: DoubleArray): Boolean {
        if (rawOffset >= rawSize) {
            rawSize = input.read(raw)
            rawOffset = 0
            if (rawSize <= 0) return false
        }
        for (channel in destination.indices) destination[channel] = PcmSamples.read(raw, rawOffset + channel * format.bytesPerSample, format)
        rawOffset += format.frameSize
        return true
    }

    override fun close() {
        if (closed) return
        closed = true
        try { input.close() } finally { ownedFile?.let(releaseOwnedFile) }
    }
}
