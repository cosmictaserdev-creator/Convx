/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Default stops for the user-defined gradient, used until one is saved. */
val DefaultGradientStops = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFF0B0B0B))

/**
 * Gradient stops are stored as one comma-separated string of ARGB ints, so the
 * number of stops can change without adding a preference key per stop.
 */
fun encodeGradientStops(colors: List<Color>): String =
    colors.joinToString(",") { it.toArgb().toString() }

fun decodeGradientStops(encoded: String): List<Color> {
    if (encoded.isBlank()) return DefaultGradientStops
    val colors = encoded.split(",").mapNotNull { it.trim().toIntOrNull()?.let(::Color) }
    // A gradient needs two stops to be a gradient.
    return if (colors.size >= 2) colors else DefaultGradientStops
}

/**
 * Paints a multi-stop linear gradient tilted by [angleDeg] (0 = left-to-right,
 * 90 = top-to-bottom). Drawn in [drawBehind] because the start/end offsets can
 * only be derived once the size is known.
 */
fun Modifier.tiltedGradient(colors: List<Color>, angleDeg: Float): Modifier = drawBehind {
    if (colors.size < 2) {
        drawRect(colors.firstOrNull() ?: Color.Black)
        return@drawBehind
    }
    val radians = Math.toRadians(angleDeg.toDouble())
    val dx = cos(radians).toFloat()
    val dy = sin(radians).toFloat()
    // Project the box onto the gradient axis so the end stops land at the corners
    // instead of being cut off at an angle.
    val length = abs(size.width * dx) + abs(size.height * dy)
    val center = Offset(size.width / 2f, size.height / 2f)
    drawRect(
        Brush.linearGradient(
            colors = colors,
            start = Offset(center.x - dx * length / 2f, center.y - dy * length / 2f),
            end = Offset(center.x + dx * length / 2f, center.y + dy * length / 2f),
        )
    )
}
