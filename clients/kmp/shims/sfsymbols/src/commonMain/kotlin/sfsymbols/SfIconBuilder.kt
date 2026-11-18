package com.composables.sfsymbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Internal helper to construct an SF Symbol ImageVector efficiently.
 */
internal inline fun sfIcon(
    name: String,
    viewportWidth: Float,
    viewportHeight: Float,
    block: ImageVector.Builder.() -> Unit
): ImageVector {
    // Preserve the symbol's true aspect ratio instead of forcing a 24x24 square,
    // which stretched every non-square SF Symbol (e.g. 29.2x25.9 viewports).
    val maxDimension = maxOf(viewportWidth, viewportHeight)
    val scale = 24f / maxDimension
    return ImageVector.Builder(
        name = name,
        defaultWidth = (viewportWidth * scale).dp,
        defaultHeight = (viewportHeight * scale).dp,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight
    ).apply(block).build()
}

/**
 * Internal helper to add an SVG path with opacity to an ImageVector builder.
 */
internal inline fun ImageVector.Builder.addSfPath(
    pathData: String,
    fillAlpha: Float = 1.0f,
    fillColor: Color = Color.Black
): ImageVector.Builder {
    return addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill = SolidColor(fillColor),
        fillAlpha = fillAlpha
    )
}
