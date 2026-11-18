package com.composables.sfsymbols

import androidx.compose.ui.graphics.Color
import kotlin.math.cbrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Apple's "automatic gradient" for a symbol colour, as implemented in the SF Symbols app: two stops, top to bottom.
 *
 * 1. L = CIE lightness (0..1) of the colour.
 * 2. Amount d: 0.20 (L <= 0.25), 0.25 (<= 0.70), 0.34 (<= 0.82), -0.11 (lighter than that).
 * 3. Channels are pushed away from L by up to 20 % (the most that keeps every channel inside 0..1).
 * 4. d > 0 mixes toward white, d < 0 darkens.
 * 5. Colours lighter than 0.82 start with the original colour, others start with the adjusted one.
 */
internal fun sfAutomaticGradient(c: Color): Pair<Color, Color> {
    val l = lightness(c)
    val d = when {
        l <= 0.25f -> 0.2f
        l <= 0.7f -> 0.25f
        l <= 0.82f -> 0.34f
        else -> -0.11f
    }
    val m = min(c.red, min(c.green, c.blue))
    val mx = max(c.red, max(c.green, c.blue))
    val s = min(headroom(m, l), headroom(mx, l))
    fun adjust(v: Float): Float {
        val boosted = (v - l) * s + v
        return boosted + (if (d < 0f) boosted else 1f - boosted) * d
    }
    val other = Color(adjust(c.red).coerceIn(0f, 1f), adjust(c.green).coerceIn(0f, 1f), adjust(c.blue).coerceIn(0f, 1f), c.alpha)
    return if (l > 0.82f) c to other else other to c
}

/** Largest contrast boost (capped at 0.2) that keeps channel [v] inside 0..1 when moved away from [l]. */
private fun headroom(v: Float, l: Float): Float {
    val diff = v - l
    if (diff == 0f) return 0.2f
    val target = if (diff > 0f) 1f - v else -v
    return (target / diff).coerceIn(0f, 0.2f)
}

private fun lightness(c: Color): Float {
    fun lin(v: Float) = if (v <= 0.04045f) v / 12.92f else ((v + 0.055f) / 1.055f).pow(2.4f)
    val y = 0.2126f * lin(c.red) + 0.7152f * lin(c.green) + 0.0722f * lin(c.blue)
    val lStar = if (y > 0.008856f) 116f * cbrt(y) - 16f else 903.3f * y
    return lStar / 100f
}
