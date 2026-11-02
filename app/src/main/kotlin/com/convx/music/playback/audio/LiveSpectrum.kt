/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback.audio

import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The latest loudness per frequency band of whatever is being played, for the live bars.
 *
 * The audio is tapped before it reaches the speaker, so it runs ahead of what is heard. Frames
 * are kept for a few seconds with their time, and the screen asks for the frame from a moment
 * ago so the bars line up with the sound.
 */
object LiveSpectrum {
    const val BANDS = 28
    private const val CAPACITY = 160

    /** True while a screen is showing the bars, so nothing is computed otherwise. */
    @Volatile
    var active = false

    private val lock = Any()
    private val times = LongArray(CAPACITY)
    private val frames = Array(CAPACITY) { FloatArray(BANDS) }
    private var next = 0
    private var count = 0

    internal fun push(uptimeMs: Long, bands: FloatArray) {
        synchronized(lock) {
            System.arraycopy(bands, 0, frames[next], 0, BANDS)
            times[next] = uptimeMs
            next = (next + 1) % CAPACITY
            if (count < CAPACITY) count++
        }
    }

    /** Fills [out] with the bands (0 to 1) as they were [delayMs] ago; zeros when nothing plays. */
    fun sample(delayMs: Long, out: FloatArray) {
        val target = SystemClock.uptimeMillis() - delayMs
        synchronized(lock) {
            var found = -1
            for (k in 1..count) {
                val index = (next - k + CAPACITY) % CAPACITY
                if (times[index] <= target) {
                    found = index
                    break
                }
            }
            if (found < 0 || target - times[found] > 150) {
                out.fill(0f)
            } else {
                System.arraycopy(frames[found], 0, out, 0, BANDS)
            }
        }
    }
}

/** Turns 16-bit PCM into band levels; one per audio processor, used from the audio thread. */
internal class SpectrumTap {
    private val window = FloatArray(SIZE)
    private var fill = 0
    private val re = FloatArray(SIZE)
    private val im = FloatArray(SIZE)
    private val bands = FloatArray(LiveSpectrum.BANDS)
    private val edges = IntArray(LiveSpectrum.BANDS + 1)
    private val tilt = FloatArray(LiveSpectrum.BANDS)
    private var preparedRate = 0

    fun reset() {
        fill = 0
    }

    fun feed(input: ByteBuffer, sampleRate: Int, channels: Int) {
        if (sampleRate != preparedRate) prepare(sampleRate)
        val samples = input.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val frameCount = samples.remaining() / channels
        for (f in 0 until frameCount) {
            var sum = 0
            val base = f * channels
            for (c in 0 until channels) sum += samples.get(base + c)
            window[fill++] = sum / (channels * 32768f)
            if (fill == SIZE) {
                analyze()
                // Half-overlapping windows give a new reading about every 11 ms.
                System.arraycopy(window, SIZE / 2, window, 0, SIZE / 2)
                fill = SIZE / 2
            }
        }
    }

    private fun prepare(sampleRate: Int) {
        preparedRate = sampleRate
        val low = 40.0
        val high = 14000.0
        val count = LiveSpectrum.BANDS
        val frequencies = DoubleArray(count + 1) { low * (high / low).pow(it.toDouble() / count) }
        for (k in 0..count) {
            edges[k] = (frequencies[k] * SIZE / sampleRate).toInt().coerceIn(1, SIZE / 2 - 1)
        }
        for (k in 1..count) {
            if (edges[k] <= edges[k - 1]) edges[k] = minOf(edges[k - 1] + 1, SIZE / 2)
        }
        for (b in 0 until count) {
            val centre = sqrt(frequencies[b] * frequencies[b + 1])
            // Music loses energy towards the treble; a gentle tilt keeps the right side alive.
            tilt[b] = (3.0 * (kotlin.math.ln(centre / 1000.0) / kotlin.math.ln(2.0))).toFloat().coerceAtLeast(-6f)
        }
    }

    private fun analyze() {
        for (i in 0 until SIZE) {
            re[i] = window[i] * HANN[i]
            im[i] = 0f
        }
        fft()
        val scale = 4f / SIZE
        for (b in 0 until LiveSpectrum.BANDS) {
            var peak = 0f
            for (bin in edges[b] until maxOf(edges[b + 1], edges[b] + 1)) {
                val magnitude = sqrt(re[bin] * re[bin] + im[bin] * im[bin]) * scale
                if (magnitude > peak) peak = magnitude
            }
            val db = 20f * log10(peak + 1e-6f) + tilt[b]
            bands[b] = ((db + 70f) / 65f).coerceIn(0f, 1f)
        }
        LiveSpectrum.push(SystemClock.uptimeMillis(), bands)
    }

    private fun fft() {
        for (i in 0 until SIZE) {
            val j = REVERSED[i]
            if (j > i) {
                val tr = re[i]; re[i] = re[j]; re[j] = tr
                val ti = im[i]; im[i] = im[j]; im[j] = ti
            }
        }
        var length = 2
        while (length <= SIZE) {
            val half = length / 2
            val step = SIZE / length
            var start = 0
            while (start < SIZE) {
                for (k in 0 until half) {
                    val wr = COS[k * step]
                    val wi = SIN[k * step]
                    val a = start + k
                    val b = a + half
                    val tr = re[b] * wr - im[b] * wi
                    val ti = re[b] * wi + im[b] * wr
                    re[b] = re[a] - tr
                    im[b] = im[a] - ti
                    re[a] += tr
                    im[a] += ti
                }
                start += length
            }
            length *= 2
        }
    }

    private companion object {
        const val SIZE = 1024
        const val LOG2_SIZE = 10
        val HANN = FloatArray(SIZE) { (0.5 - 0.5 * cos(2 * PI * it / (SIZE - 1))).toFloat() }
        val COS = FloatArray(SIZE / 2) { cos(-2 * PI * it / SIZE).toFloat() }
        val SIN = FloatArray(SIZE / 2) { sin(-2 * PI * it / SIZE).toFloat() }
        val REVERSED = IntArray(SIZE) { Integer.reverse(it) ushr (32 - LOG2_SIZE) }
    }
}
