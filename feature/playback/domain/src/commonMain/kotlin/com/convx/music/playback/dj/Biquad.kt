/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
        configure(sampleRate, cutoffHz, q, highPass = false)
    }

    fun setHighPass(sampleRate: Int, cutoffHz: Float, q: Double = BUTTERWORTH_Q) {
        configure(sampleRate, cutoffHz, q, highPass = true)
    }

    private fun configure(sampleRate: Int, cutoffHz: Float, q: Double, highPass: Boolean) {
        val clamped = cutoffHz.coerceIn(MIN_CUTOFF_HZ, sampleRate * 0.45f)
        val omega = 2.0 * PI * clamped / sampleRate
        val cosOmega = cos(omega)
        val alpha = sin(omega) / (2.0 * q)
        val a0 = 1.0 + alpha
        val numerator = if (highPass) 1.0 + cosOmega else 1.0 - cosOmega
        b0 = (numerator / 2.0) / a0
        b1 = (if (highPass) -numerator else numerator) / a0
        b2 = b0
        a1 = (-2.0 * cosOmega) / a0
        a2 = (1.0 - alpha) / a0
    }

    fun copyCoefficientsFrom(other: Biquad) {
        b0 = other.b0
        b1 = other.b1
        b2 = other.b2
        a1 = other.a1
        a2 = other.a2
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
        const val BUTTERWORTH_Q = 0.707
        private const val MIN_CUTOFF_HZ = 20f
    }
}
