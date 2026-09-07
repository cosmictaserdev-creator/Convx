/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import com.convxy.music.playback.dj.Biquad
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * The DJ filter: one low-pass and one high-pass per channel, with cutoffs that
 * glide toward a target instead of jumping.
 *
 * Separate from `CustomEqualizerAudioProcessor` on purpose, for two reasons.
 * That one calls `filters.forEach { it.reset() }` on every `applyProfile()`, so
 * driving it a few times a second during a transition would zero the filter
 * state and click each time; and it holds the user's own EQ profile, which a
 * transition has no business overwriting.
 *
 * Idle state is fully open (low-pass at Nyquist, high-pass at DC), where this
 * is a straight buffer copy — which is where every player sits when DJ mixing
 * is off.
 */
@UnstableApi
class DjFilterAudioProcessor : BaseAudioProcessor() {

    /** Low-pass cutoff in Hz. [OPEN_LOW_PASS_HZ] disables it. */
    @Volatile
    var lowPassHz: Float = OPEN_LOW_PASS_HZ

    /** High-pass cutoff in Hz. [OPEN_HIGH_PASS_HZ] disables it. */
    @Volatile
    var highPassHz: Float = OPEN_HIGH_PASS_HZ

    private var channels = 0
    private var sampleRate = 0

    private var currentLowPass = OPEN_LOW_PASS_HZ
    private var currentHighPass = OPEN_HIGH_PASS_HZ

    // Cutoffs the biquad coefficients were last built for. Recomputing them is
    // two sin/cos per filter per channel, and at a 128-frame chunk that is
    // thousands of trig calls a second on the audio thread for cutoffs that are
    // usually parked.
    private var appliedLowPass = Float.NaN
    private var appliedHighPass = Float.NaN

    private lateinit var lowPass: Array<Biquad>
    private lateinit var highPass: Array<Biquad>

    /** Snaps cutoffs to their targets without gliding. Call when arming or
     *  disarming outside a transition so the first buffer isn't a sweep from
     *  wherever the last transition left off. */
    fun snapToTargets() {
        currentLowPass = lowPassHz
        currentHighPass = highPassHz
    }

    fun open() {
        lowPassHz = OPEN_LOW_PASS_HZ
        highPassHz = OPEN_HIGH_PASS_HZ
        snapToTargets()
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT ||
            inputAudioFormat.channelCount !in 1..2 ||
            inputAudioFormat.sampleRate <= 0
        ) {
            // Unsupported format: stay inactive, audio passes untouched.
            return AudioProcessor.AudioFormat.NOT_SET
        }
        channels = inputAudioFormat.channelCount
        sampleRate = inputAudioFormat.sampleRate
        lowPass = Array(channels) { Biquad() }
        highPass = Array(channels) { Biquad() }
        // Snap to whatever the targets already are — deliberately NOT open().
        // configure() runs on the playback thread some time after prepare(), so
        // resetting the targets here would silently undo an arm that was set up
        // before the player started, and the incoming track's first buffers
        // would come through full-range instead of bass-cut.
        snapToTargets()
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return
        val output = replaceOutputBuffer(remaining)

        if (isOpen()) {
            // Fully open on both ends and not mid-glide: nothing to compute.
            output.put(inputBuffer)
            output.flip()
            return
        }

        val frameCount = remaining / 2 / channels
        var framesDone = 0
        while (framesDone < frameCount) {
            val chunk = minOf(COEFFICIENT_CHUNK_FRAMES, frameCount - framesDone)
            // Cutoffs glide per chunk rather than per buffer: a whole buffer is
            // ~20 ms, and stepping the coefficients that coarsely during a fast
            // sweep is audible as zipper noise.
            advanceCutoffs(chunk)
            if (!nearlyEqual(currentLowPass, appliedLowPass)) {
                for (channel in 0 until channels) lowPass[channel].setLowPass(sampleRate, currentLowPass)
                appliedLowPass = currentLowPass
            }
            if (!nearlyEqual(currentHighPass, appliedHighPass)) {
                for (channel in 0 until channels) highPass[channel].setHighPass(sampleRate, currentHighPass)
                appliedHighPass = currentHighPass
            }
            repeat(chunk) {
                for (channel in 0 until channels) {
                    var sample = inputBuffer.short.toDouble()
                    if (currentLowPass < OPEN_LOW_PASS_HZ) sample = lowPass[channel].process(sample)
                    if (currentHighPass > OPEN_HIGH_PASS_HZ) sample = highPass[channel].process(sample)
                    output.putShort(sample.coerceIn(MIN_SAMPLE, MAX_SAMPLE).toInt().toShort())
                }
            }
            framesDone += chunk
        }
        output.flip()
    }

    /**
     * Glides in log-frequency, because a filter sweep is heard in octaves — a
     * linear glide from 20 kHz spends most of its time in a range the ear
     * barely notices and then rushes through the part that matters.
     */
    private fun advanceCutoffs(frames: Int) {
        val alpha = 1.0 - exp(-frames.toDouble() / (sampleRate * GLIDE_TIME_CONSTANT_S))
        currentLowPass = glide(currentLowPass, lowPassHz, alpha)
        currentHighPass = glide(currentHighPass, highPassHz, alpha)
    }

    private fun glide(current: Float, target: Float, alpha: Double): Float {
        if (current == target) return target
        val next = exp(ln(current.toDouble()) + (ln(target.toDouble()) - ln(current.toDouble())) * alpha)
        return if (abs(next - target) < target * 0.001) target else next.toFloat()
    }

    private fun nearlyEqual(a: Float, b: Float): Boolean = abs(a - b) < b * 0.001f

    private fun isOpen(): Boolean =
        currentLowPass >= OPEN_LOW_PASS_HZ && currentHighPass <= OPEN_HIGH_PASS_HZ &&
            lowPassHz >= OPEN_LOW_PASS_HZ && highPassHz <= OPEN_HIGH_PASS_HZ

    override fun onFlush() {
        if (::lowPass.isInitialized) lowPass.forEach { it.reset() }
        if (::highPass.isInitialized) highPass.forEach { it.reset() }
    }

    override fun onReset() {
        channels = 0
        sampleRate = 0
        appliedLowPass = Float.NaN
        appliedHighPass = Float.NaN
        lowPassHz = OPEN_LOW_PASS_HZ
        highPassHz = OPEN_HIGH_PASS_HZ
        snapToTargets()
    }

    companion object {
        const val OPEN_LOW_PASS_HZ = 20_000f
        const val OPEN_HIGH_PASS_HZ = 20f

        /** Where a bass swap parks the outgoing low-pass / incoming high-pass. */
        const val BASS_KILL_LOW_PASS_HZ = 220f
        const val BASS_KILL_HIGH_PASS_HZ = 300f

        private const val COEFFICIENT_CHUNK_FRAMES = 128
        private const val GLIDE_TIME_CONSTANT_S = 0.35
        private const val MIN_SAMPLE = -32768.0
        private const val MAX_SAMPLE = 32767.0
    }
}
