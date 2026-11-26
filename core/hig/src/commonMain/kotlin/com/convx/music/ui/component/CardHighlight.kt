package com.convx.music.ui.component

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The one-pixel edge every card carries so artwork keeps its outline against a page of
 * the same colour. A plain border: no offscreen layer, so it costs nothing while scrolling.
 */
fun Modifier.cardHighlight(shape: Shape, color: Color): Modifier = border(0.5.dp, color, shape)

/** Edge tint for [cardHighlight]: a light rim on dark pages, a dark hairline on light ones. */
fun cardHighlightColor(dark: Boolean): Color =
    if (dark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
