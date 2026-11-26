package com.convx.music.data.settings

import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

internal fun semitonesToPitch(semitones: Int): Float = 2.0.pow(semitones / 12.0).toFloat()
internal fun pitchToSemitones(pitch: Float): Int =
    if (pitch.isFinite() && pitch > 0f) (12 * log2(pitch.toDouble())).roundToInt() else 0
