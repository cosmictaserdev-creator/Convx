package com.convx.music.ui.component.flamingo

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

/**
 * Brush for one character of the line being sung. [percent] is how far the sweep has
 * travelled over the character and [spread] is half the width of its soft edge.
 */
internal fun sweepBrush(percent: Float, spread: Float, sung: Color, unsung: Color): Brush {
    if (percent >= 1f + spread) return SolidColor(sung)
    if (percent <= -spread) return SolidColor(unsung)
    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0f to sung,
            (percent - spread).coerceIn(0f, 1f) to sung,
            (percent + spread).coerceIn(0f, 1f) to unsung,
        ),
    )
}
