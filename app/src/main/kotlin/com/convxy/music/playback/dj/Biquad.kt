/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.dj

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Direct-form-1 biquad, low-pass and high-pass only (RBJ cookbook).
 *
 * Separate from `eq/audio/BiquadFilter.kt`, which implements the peaking and
 * shelving types the parametric EQ needs and has no low/high-pass at all, and
 * whose constructor fixes the frequency for the filter's lifetime. Here the
 * cutoff moves while audio flows, so coefficients are recomputed in place and
 * the state variables are deliberately never cleared — that is exactly what
 * stops a moving filter from clicking.
 */
class Biquad {
    private var b0 = 1.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0

    private var x1 = 0.0
    private var x2 = 0.0
    private var y1 = 0.0
    private var y2 = 0.0

    fun setLowPass(sampleRate: Int, cutoffHz: Float, q: Double = BUTTERWORTH_Q) {
        val (cosOmega, alpha, a0) = shared(sampleRate, cutoffHz, q)
        b0 = ((1.0 - cosOmega) / 2.0) / a0
        b1 = (1.0 - cosOmega) / a0
        b2 = b0
        a1 = (-2.0 * cosOmega) / a0
        a2 = (1.0 - alpha) / a0
    }

    fun setHighPass(sampleRate: Int, cutoffHz: Float, q: Double = BUTTERWORTH_Q) {
        val (cosOmega, alpha, a0) = shared(sampleRate, cutoffHz, q)
        b0 = ((1.0 + cosOmega) / 2.0) / a0
        b1 = -(1.0 + cosOmega) / a0
        b2 = b0
        a1 = (-2.0 * cosOmega) / a0
        a2 = (1.0 - alpha) / a0
    }

    private fun shared(sampleRate: Int, cutoffHz: Float, q: Double): Triple<Double, Double, Double> {
        val clamped = cutoffHz.coerceIn(MIN_CUTOFF_HZ, sampleRate * 0.45f)
        val omega = 2.0 * PI * clamped / sampleRate
        val alpha = sin(omega) / (2.0 * q)
        return Triple(cos(omega), alpha, 1.0 + alpha)
    }

    fun process(input: Double): Double {
        val output = b0 * input + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = input
        y2 = y1
        y1 = output
        return output
    }

    fun reset() {
        x1 = 0.0
        x2 = 0.0
        y1 = 0.0
        y2 = 0.0
    }

    companion object {
        /** No resonant peak at the cutoff, which a filter sweeping across a
         *  mix would otherwise make very obvious. */
        const val BUTTERWORTH_Q = 0.707
        private const val MIN_CUTOFF_HZ = 20f
    }
}
