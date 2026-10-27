package com.convx.music.playback

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Stateful two-channel RBJ biquad used by target audio processors. */
class EqualizerBiquadFilter(
    sampleRate: Int,
    frequency: Double,
    gain: Double,
    q: Double = 1.41,
    filterType: EqualizerFilterType = EqualizerFilterType.PK,
) {
    private var a1 = 0.0
    private var a2 = 0.0
    private var b0 = 0.0
    private var b1 = 0.0
    private var b2 = 0.0

    private var x1Left = 0.0
    private var x2Left = 0.0
    private var y1Left = 0.0
    private var y2Left = 0.0
    private var x1Right = 0.0
    private var x2Right = 0.0
    private var y1Right = 0.0
    private var y2Right = 0.0

    init {
        require(sampleRate > 0)
        require(frequency.isFinite() && frequency > 0.0 && frequency < sampleRate / 2.0)
        require(gain.isFinite())
        require(q.isFinite() && q > 0.0)

        val omega = 2.0 * PI * frequency / sampleRate
        val sinOmega = sin(omega)
        val cosOmega = cos(omega)
        val alpha = sinOmega / (2.0 * q)
        when (filterType) {
            EqualizerFilterType.PK -> {
                val amplitude = 10.0.pow(gain / 40.0)
                setCoefficients(
                    b0 = 1.0 + alpha * amplitude,
                    b1 = -2.0 * cosOmega,
                    b2 = 1.0 - alpha * amplitude,
                    a0 = 1.0 + alpha / amplitude,
                    a1 = -2.0 * cosOmega,
                    a2 = 1.0 - alpha / amplitude,
                )
            }
            EqualizerFilterType.LSC -> setShelfCoefficients(
                amplitude = 10.0.pow(gain / 40.0),
                sinOmega = sinOmega,
                cosOmega = cosOmega,
                isLowShelf = true,
            )
            EqualizerFilterType.HSC -> setShelfCoefficients(
                amplitude = 10.0.pow(gain / 40.0),
                sinOmega = sinOmega,
                cosOmega = cosOmega,
                isLowShelf = false,
            )
            EqualizerFilterType.LPQ -> setCoefficients(
                b0 = (1.0 - cosOmega) / 2.0,
                b1 = 1.0 - cosOmega,
                b2 = (1.0 - cosOmega) / 2.0,
                a0 = 1.0 + alpha,
                a1 = -2.0 * cosOmega,
                a2 = 1.0 - alpha,
            )
            EqualizerFilterType.HPQ -> setCoefficients(
                b0 = (1.0 + cosOmega) / 2.0,
                b1 = -(1.0 + cosOmega),
                b2 = (1.0 + cosOmega) / 2.0,
                a0 = 1.0 + alpha,
                a1 = -2.0 * cosOmega,
                a2 = 1.0 - alpha,
            )
        }
    }

    fun processSample(input: Double): Double {
        val output = b0 * input + b1 * x1Left + b2 * x2Left - a1 * y1Left - a2 * y2Left
        x2Left = x1Left
        x1Left = input
        y2Left = y1Left
        y1Left = output
        return output
    }

    fun processRightSample(input: Double): Double {
        val output = b0 * input + b1 * x1Right + b2 * x2Right - a1 * y1Right - a2 * y2Right
        x2Right = x1Right
        x1Right = input
        y2Right = y1Right
        y1Right = output
        return output
    }

    fun processStereo(inputLeft: Double, inputRight: Double): Pair<Double, Double> =
        processSample(inputLeft) to processRightSample(inputRight)

    fun reset() {
        x1Left = 0.0
        x2Left = 0.0
        y1Left = 0.0
        y2Left = 0.0
        x1Right = 0.0
        x2Right = 0.0
        y1Right = 0.0
        y2Right = 0.0
    }

    private fun setShelfCoefficients(
        amplitude: Double,
        sinOmega: Double,
        cosOmega: Double,
        isLowShelf: Boolean,
    ) {
        val alpha = sinOmega / 2.0 * sqrt(2.0)
        val aPlusOne = amplitude + 1.0
        val aMinusOne = amplitude - 1.0
        val twoRootAlpha = 2.0 * sqrt(amplitude) * alpha
        if (isLowShelf) {
            setCoefficients(
                b0 = amplitude * (aPlusOne - aMinusOne * cosOmega + twoRootAlpha),
                b1 = 2.0 * amplitude * (aMinusOne - aPlusOne * cosOmega),
                b2 = amplitude * (aPlusOne - aMinusOne * cosOmega - twoRootAlpha),
                a0 = aPlusOne + aMinusOne * cosOmega + twoRootAlpha,
                a1 = -2.0 * (aMinusOne + aPlusOne * cosOmega),
                a2 = aPlusOne + aMinusOne * cosOmega - twoRootAlpha,
            )
        } else {
            setCoefficients(
                b0 = amplitude * (aPlusOne + aMinusOne * cosOmega + twoRootAlpha),
                b1 = -2.0 * amplitude * (aMinusOne + aPlusOne * cosOmega),
                b2 = amplitude * (aPlusOne + aMinusOne * cosOmega - twoRootAlpha),
                a0 = aPlusOne - aMinusOne * cosOmega + twoRootAlpha,
                a1 = 2.0 * (aMinusOne - aPlusOne * cosOmega),
                a2 = aPlusOne - aMinusOne * cosOmega - twoRootAlpha,
            )
        }
    }

    private fun setCoefficients(b0: Double, b1: Double, b2: Double, a0: Double, a1: Double, a2: Double) {
        require(a0.isFinite() && a0 != 0.0)
        this.b0 = b0 / a0
        this.b1 = b1 / a0
        this.b2 = b2 / a0
        this.a1 = a1 / a0
        this.a2 = a2 / a0
    }
}
