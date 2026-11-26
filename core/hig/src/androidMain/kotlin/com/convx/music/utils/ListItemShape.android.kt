package com.convx.music.utils

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

internal actual fun createListItemShapes(radius: Dp): List<Shape> {
        val smoothness = 60
        return listOf(
            AbsoluteSmoothCornerShape(
                cornerRadiusTL = radius, smoothnessAsPercentTL = smoothness,
                cornerRadiusTR = radius, smoothnessAsPercentTR = smoothness,
                cornerRadiusBL = radius, smoothnessAsPercentBL = smoothness,
                cornerRadiusBR = radius, smoothnessAsPercentBR = smoothness
            ),
            AbsoluteSmoothCornerShape(
                cornerRadiusTL = radius, smoothnessAsPercentTL = smoothness,
                cornerRadiusTR = radius, smoothnessAsPercentTR = smoothness,
                cornerRadiusBL = 0.dp, smoothnessAsPercentBL = 0,
                cornerRadiusBR = 0.dp, smoothnessAsPercentBR = 0
            ),
            AbsoluteSmoothCornerShape(
                cornerRadiusTL = 0.dp, smoothnessAsPercentTL = 0,
                cornerRadiusTR = 0.dp, smoothnessAsPercentTR = 0,
                cornerRadiusBL = radius, smoothnessAsPercentBL = smoothness,
                cornerRadiusBR = radius, smoothnessAsPercentBR = smoothness
            ),
        )
}
