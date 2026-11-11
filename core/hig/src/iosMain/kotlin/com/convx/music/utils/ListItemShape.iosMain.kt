package com.convx.music.utils

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

internal actual fun createListItemShapes(radius: Dp): List<Shape> = roundedListItemShapes(radius)
