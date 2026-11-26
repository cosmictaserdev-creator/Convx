package com.convx.music.utils

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

private val shapeLock = SynchronizedObject()
private val shapesByRadius = mutableMapOf<Dp, List<Shape>>()

fun listItemShape(index: Int, count: Int, radius: Dp = 24.dp): Shape {
    val position = when {
        count == 1 -> 0
        index == 0 -> 1
        index == count - 1 -> 2
        else -> return RectangleShape
    }
    return synchronized(shapeLock) { shapesByRadius.getOrPut(radius) { createListItemShapes(radius) }[position] }
}

internal expect fun createListItemShapes(radius: Dp): List<Shape>

internal fun roundedListItemShapes(radius: Dp): List<Shape> = listOf(
    RoundedCornerShape(radius),
    RoundedCornerShape(topStart = radius, topEnd = radius, bottomStart = 0.dp, bottomEnd = 0.dp),
    RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = radius, bottomEnd = radius),
)
