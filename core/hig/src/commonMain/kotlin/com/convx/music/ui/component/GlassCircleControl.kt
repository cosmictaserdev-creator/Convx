/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component

import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

fun Modifier.chromeShadow(shape: Shape): Modifier = dropShadow(
    shape,
    Shadow(radius = 24.dp, color = Color.Black, offset = DpOffset(0.dp, 4.dp), alpha = 0.18f),
)

/** Circular chrome interaction with a caller-provided surface and indication. */
@Composable
fun GlassCircleControl(
    onClick: () -> Unit,
    shape: Shape,
    surface: Modifier,
    indication: Indication?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val stretch = rememberElasticStretch()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer(elasticStretchLayerBlock(stretch))
            .size(size)
            .chromeShadow(shape)
            .clip(shape)
            .then(surface)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button,
                indication = indication,
                interactionSource = interactionSource,
            )
            .then(stretch.modifier)
            .then(stretch.gestureModifier),
        content = { content() },
    )
}
