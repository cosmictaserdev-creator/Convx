package com.convx.music.utils

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ListItemShapeTest {
    @Test
    fun shapesFollowRowPositionAndAreReused() {
        val single = listItemShape(0, 1)
        val first = listItemShape(0, 3)
        val last = listItemShape(2, 3)
        assertSame(RectangleShape, listItemShape(1, 3))
        assertNotEquals(single, first)
        assertNotEquals(first, last)
        assertSame(first, listItemShape(0, 2))
        assertSame(last, listItemShape(1, 2))
        assertNotEquals(first, listItemShape(0, 2, 12.dp))
    }
}
