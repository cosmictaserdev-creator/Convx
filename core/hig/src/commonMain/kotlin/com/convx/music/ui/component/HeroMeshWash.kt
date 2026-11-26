/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp

/** Draws the color wash beneath hero artwork using an already resolved palette. */
@Composable
fun SharedHeroMeshWash(
    wash: Color,
    accent: Color,
    screenBackground: Color,
    modifier: Modifier = Modifier,
    washFraction: Float = 0.5f,
) {
    Canvas(modifier) {
        drawRect(
            Brush.verticalGradient(
                0f to wash,
                washFraction to wash,
                washFraction + (1f - washFraction) * 0.45f to lerp(wash, screenBackground, 0.20f),
                washFraction + (1f - washFraction) * 0.80f to lerp(wash, screenBackground, 0.65f),
                1f to screenBackground,
            ),
        )
        heroWashBlob(accent.copy(alpha = 0.16f), Offset(0.16f, washFraction + 0.10f), 0.80f)
        heroWashBlob(wash.copy(alpha = 0.26f), Offset(0.90f, washFraction + 0.32f), 0.95f)
    }
}

private fun DrawScope.heroWashBlob(color: Color, at: Offset, radiusFraction: Float) {
    val center = Offset(at.x * size.width, at.y * size.height)
    val radius = size.width * radiusFraction
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color, color.copy(alpha = 0f)),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}
