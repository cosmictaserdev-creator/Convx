/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Same tanh-damped press/drag-out scale+translate distortion as
 * the floating glass controls, so every small floating glass button — top bar back/share/
 * search, selection-mode exit — gets the same "elastic stretch" liquid feel on
 * press. Cheap: a per-frame graphicsLayer transform, no extra backdrop re-capture.
 */
fun elasticStretchLayerBlock(
    interactiveHighlight: InteractiveHighlight,
    // Bar pills pass 2x: same feel, larger travel so the stretch reads at pill size.
    magnitude: Float = 1f,
): GraphicsLayerScope.() -> Unit = {
    val width = size.width
    val height = size.height

    val progress = interactiveHighlight.pressProgress
    val scale = lerp(1f, 1f + magnitude * 4f.dp.toPx() / size.height, progress)

    val maxOffset = size.minDimension
    val initialDerivative = 0.05f
    val offset = interactiveHighlight.offset
    translationX = magnitude * maxOffset * tanh(initialDerivative * offset.x / maxOffset)
    translationY = magnitude * maxOffset * tanh(initialDerivative * offset.y / maxOffset)

    val maxDragScale = magnitude * 4f.dp.toPx() / size.height
    val offsetAngle = atan2(offset.y, offset.x)
    scaleX = scale +
        maxDragScale * abs(cos(offsetAngle) * offset.x / size.maxDimension) *
        (width / height).fastCoerceAtMost(1f)
    scaleY = scale +
        maxDragScale * abs(sin(offsetAngle) * offset.y / size.maxDimension) *
        (height / width).fastCoerceAtMost(1f)
}

@Composable
fun rememberElasticStretch(): InteractiveHighlight {
    val animationScope = rememberCoroutineScope()
    return remember(animationScope) { InteractiveHighlight(animationScope) }
}


@Composable
fun ElasticPillRow(
    modifier: Modifier = Modifier,
    magnitude: Float = 2f,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    val stretch = rememberElasticStretch()
    Row(
        modifier = Modifier
            .graphicsLayer(elasticStretchLayerBlock(stretch, magnitude))
            .then(modifier)
            .then(stretch.modifier)
            .then(stretch.gestureModifier),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}
