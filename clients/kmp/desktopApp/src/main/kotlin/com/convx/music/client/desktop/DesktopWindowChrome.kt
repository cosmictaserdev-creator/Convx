package com.convx.music.client.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.EventQueue
import java.awt.Frame
import java.awt.MouseInfo
import java.awt.Rectangle
import java.awt.Window

internal fun Modifier.desktopWindowDrag(window: Window?): Modifier {
    if (window == null) return this
    return pointerInput(window) {
        var start = window.location
        var cursor = MouseInfo.getPointerInfo().location
        detectDragGestures(onDragStart = { start = window.location; cursor = MouseInfo.getPointerInfo().location }) { change, _ ->
            change.consume()
            val point = MouseInfo.getPointerInfo().location
            val x = start.x + point.x - cursor.x
            val y = start.y + point.y - cursor.y
            EventQueue.invokeLater { if (window.isDisplayable) window.setLocation(x, y) }
        }
    }
}

@Composable
internal fun DesktopWindowControls(window: Window, onClose: () -> Unit) {
    Row(Modifier.height(36.dp)) {
        WindowButton("−", "Minimize") { (window as? Frame)?.extendedState = Frame.ICONIFIED }
        WindowButton("□", "Maximize or restore") {
            (window as? Frame)?.let { it.extendedState = if (it.extendedState and Frame.MAXIMIZED_BOTH != 0) Frame.NORMAL else Frame.MAXIMIZED_BOTH }
        }
        WindowButton("×", "Close", onClose)
    }
}

@Composable
internal fun DesktopWindowBar(window: Window, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val palette = desktopPalette()
    Row(modifier.fillMaxWidth().height(36.dp).pointerInput(window) {
        var start = window.location
        var cursor = MouseInfo.getPointerInfo().location
        detectDragGestures(onDragStart = { start = window.location; cursor = MouseInfo.getPointerInfo().location }) { change, _ ->
            change.consume()
            val point = MouseInfo.getPointerInfo().location
            val x = start.x + point.x - cursor.x
            val y = start.y + point.y - cursor.y
            EventQueue.invokeLater { if (window.isDisplayable) window.setLocation(x, y) }
        }
    }, verticalAlignment = Alignment.CenterVertically) {
        BasicText("Convx", Modifier.padding(start = 20.dp), style = TextStyle(fontFamily = DesktopWebFont, color = palette.muted, fontSize = 11.sp))
        Spacer(Modifier.weight(1f))
        WindowButton("−", "Minimize") { (window as? Frame)?.extendedState = Frame.ICONIFIED }
        WindowButton("□", "Maximize or restore") {
            (window as? Frame)?.let { it.extendedState = if (it.extendedState and Frame.MAXIMIZED_BOTH != 0) Frame.NORMAL else Frame.MAXIMIZED_BOTH }
        }
        WindowButton("×", "Close", onClose)
    }
}

@Composable
private fun WindowButton(text: String, description: String, onClick: () -> Unit) {
    val palette = desktopPalette()
    Box(Modifier.width(46.dp).fillMaxHeight().clickable(role = Role.Button, onClickLabel = description, onClick = onClick), contentAlignment = Alignment.Center) {
        BasicText(text, style = TextStyle(color = palette.foreground, fontSize = 18.sp))
    }
}

@Composable
internal fun BoxScope.DesktopWindowResizeEdges(window: Window) {
    val alignments = listOf(Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd, Alignment.CenterStart,
        Alignment.CenterEnd, Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd)
    alignments.forEachIndexed { edge, alignment ->
        val corner = edge in listOf(0, 2, 5, 7)
        val horizontal = edge == 1 || edge == 6
        val dimensions = when {
            corner -> Modifier.size(10.dp)
            horizontal -> Modifier.fillMaxWidth().height(5.dp)
            else -> Modifier.width(5.dp).fillMaxHeight()
        }
        Box(Modifier.align(alignment).then(dimensions).pointerInput(window, edge) {
            var original = Rectangle()
            var cursor = MouseInfo.getPointerInfo().location
            detectDragGestures(onDragStart = { original = window.bounds; cursor = MouseInfo.getPointerInfo().location }) { change, _ ->
                change.consume()
                val point = MouseInfo.getPointerInfo().location
                val dx = point.x - cursor.x
                val dy = point.y - cursor.y
                val west = edge in listOf(0, 3, 5)
                val east = edge in listOf(2, 4, 7)
                val north = edge in listOf(0, 1, 2)
                val south = edge in listOf(5, 6, 7)
                val width = (original.width + if (east) dx else if (west) -dx else 0).coerceAtLeast(840)
                val height = (original.height + if (south) dy else if (north) -dy else 0).coerceAtLeast(620)
                val x = if (west) original.x + original.width - width else original.x
                val y = if (north) original.y + original.height - height else original.y
                EventQueue.invokeLater { if (window.isDisplayable) window.setBounds(x, y, width, height) }
            }
        })
    }
}
