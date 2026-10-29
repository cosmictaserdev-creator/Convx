package com.convx.music.ui.component

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class MenuActionColumnCountTest {
    @Test
    fun `phone sheet wraps to two columns instead of packing three`() {
        // Menu sheet inset leaves about 320dp. Three 88dp cells used to share that
        // and ellipsize every label.
        assertEquals(2, menuActionColumnCount(320.dp, MenuActionMinCellWidth, 0.dp, requestedColumns = 3))
    }

    @Test
    fun `wide sheet still uses the requested three columns`() {
        assertEquals(3, menuActionColumnCount(480.dp, MenuActionMinCellWidth, 0.dp, requestedColumns = 3))
    }

    @Test
    fun `panel gap counts toward the cell so icons do not touch`() {
        assertEquals(2, menuActionColumnCount(270.dp, MenuActionMinCellWidth, 12.dp, requestedColumns = 3))
    }

    @Test
    fun `unbounded width keeps the requested count while the menu is measured`() {
        assertEquals(3, menuActionColumnCount(Dp.Infinity, MenuActionMinCellWidth, 0.dp, 3))
    }
}
