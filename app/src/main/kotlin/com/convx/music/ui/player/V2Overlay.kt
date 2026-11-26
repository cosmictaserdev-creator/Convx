/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.convx.music.ui.component.MenuAnchor
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.ios.foundation.LocalIosBackdrop
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Where V2's floating panels (menus, route popover, quality card) are drawn: a slot at the
 * player's root, in the player's own window, above a backdrop that records the whole
 * player -- artwork included. A `Popup` is a separate window, and sampling a layer from
 * there came out empty, which is why the menus showed a flat black panel.
 */
internal class V2OverlayEntry(val content: @Composable () -> Unit, val dismiss: () -> Unit)

internal val LocalV2Overlay = compositionLocalOf<MutableState<V2OverlayEntry?>?> { null }

/** Renders the slot; place after the content [backdrop] records. */
@Composable
internal fun V2OverlayHost(slot: MutableState<V2OverlayEntry?>, backdrop: Backdrop) {
    val content = slot.value ?: return
    CompositionLocalProvider(LocalIosBackdrop provides backdrop) { content.content() }
}

/** Places a panel of [panel] size in a host of [host] size, given the opening touch (host px). */
internal typealias V2Placement = Density.(touch: Offset, host: IntSize, panel: IntSize) -> IntOffset

/** Places the panel above the tap and aligns its right edge with the tap. */
internal val AboveEnd: V2Placement = { touch, host, panel ->
    val m = 12.dp.toPx()
    val x = (touch.x + 14.dp.toPx() - panel.width).coerceIn(m, (host.width - panel.width - m).coerceAtLeast(m))
    val above = touch.y - 22.dp.toPx() - panel.height
    val y = (if (above >= m) above else touch.y + 22.dp.toPx())
        .coerceIn(m, (host.height - panel.height - m).coerceAtLeast(m))
    IntOffset(x.roundToInt(), y.roundToInt())
}

/** Places the panel above the tap and centers it horizontally. */
internal val AboveCenter: V2Placement = { touch, host, panel ->
    val m = 16.dp.toPx()
    val x = (touch.x - panel.width / 2f).coerceIn(m, (host.width - panel.width - m).coerceAtLeast(m))
    val y = (touch.y - 28.dp.toPx() - panel.height).coerceAtLeast(m)
    IntOffset(x.roundToInt(), y.roundToInt())
}

internal val Centered: V2Placement = { _, host, panel ->
    IntOffset((host.width - panel.width) / 2, (host.height - panel.height) / 2)
}

/**
 * Shows [content] in the player's overlay slot while composed; tap outside or Back dismisses.
 * Outside a V2 player (no slot) it falls back to a centred popup.
 */
@Composable
internal fun V2Floating(
    onDismiss: () -> Unit,
    placement: V2Placement,
    dimAlpha: Float = 0f,
    content: @Composable () -> Unit,
) {
    val slot = LocalV2Overlay.current
    if (slot == null) {
        Popup(alignment = androidx.compose.ui.Alignment.Center, onDismissRequest = onDismiss, properties = PopupProperties(focusable = true)) { content() }
        return
    }
    val touch = remember { MenuAnchor.lastDown }
    val latestDismiss by rememberUpdatedState(onDismiss)
    val latestContent by rememberUpdatedState(content)
    DisposableEffect(slot) {
        val entry = V2OverlayEntry(
            content = { FloatingLayer(touch, { latestDismiss() }, dimAlpha, placement) { latestContent() } },
            dismiss = { latestDismiss() },
        )
        slot.value = entry
        onDispose { if (slot.value === entry) slot.value = null }
    }
}

/**
 * Inside a [V2Floating] panel: closes it with the shrink-back animation. Buttons that end the
 * panel (the quality card's OK) use this so they animate out like a tap outside does.
 */
internal val LocalV2FloatingClose = compositionLocalOf<(() -> Unit)?> { null }

/**
 * Same open/close as the app's overlay menus (OverlayMenu): a small blob at the touch point
 * stretches into the panel, width leading height on two springs; close runs it back, faster.
 */
@Composable
private fun FloatingLayer(
    touchInWindow: Offset,
    onDismiss: () -> Unit,
    dimAlpha: Float,
    placement: V2Placement,
    content: @Composable () -> Unit,
) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    var hostSize by remember { mutableStateOf(IntSize.Zero) }
    var panelSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val scaleX = remember { androidx.compose.animation.core.Animatable(0.12f) }
    val scaleY = remember { androidx.compose.animation.core.Animatable(0.12f) }
    val alpha = remember { androidx.compose.animation.core.Animatable(0f) }
    var closing by remember { mutableStateOf(false) }
    val measured = panelSize != IntSize.Zero && hostSize != IntSize.Zero
    // Opens once the panel has a size, so it grows from the right point.
    androidx.compose.runtime.LaunchedEffect(measured) {
        if (!measured) return@LaunchedEffect
        launch { alpha.animateTo(1f, androidx.compose.animation.core.tween(90)) }
        launch { scaleX.animateTo(1f, androidx.compose.animation.core.spring(dampingRatio = 0.72f, stiffness = 700f)) }
        scaleY.animateTo(1f, androidx.compose.animation.core.spring(dampingRatio = 0.78f, stiffness = 420f))
    }
    val close: () -> Unit = {
        if (!closing) {
            closing = true
            scope.launch {
                launch { scaleX.animateTo(0.12f, androidx.compose.animation.core.spring(dampingRatio = 1f, stiffness = 900f)) }
                launch { scaleY.animateTo(0.12f, androidx.compose.animation.core.spring(dampingRatio = 1f, stiffness = 900f)) }
                alpha.animateTo(0f, androidx.compose.animation.core.tween(140, delayMillis = 40))
                onDismiss()
            }
        }
    }
    BackHandler(onBack = close)
    val touchLocal = { if (touchInWindow.isSpecified) touchInWindow - origin else Offset(hostSize.width / 2f, hostSize.height / 2f) }
    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInWindow(); hostSize = it.size }
            .drawBehind { if (dimAlpha > 0f) drawRect(Color.Black.copy(alpha = dimAlpha * alpha.value)) }
            .pointerInput(Unit) { detectTapGestures { close() } },
    ) {
        Box(
            Modifier
                .offset { density.placement(touchLocal(), hostSize, panelSize) }
                .onSizeChanged { panelSize = it }
                .graphicsLayer {
                    val at = density.placement(touchLocal(), hostSize, panelSize)
                    val touch = touchLocal()
                    transformOrigin = if (panelSize.width > 0 && panelSize.height > 0) {
                        androidx.compose.ui.graphics.TransformOrigin(
                            ((touch.x - at.x) / panelSize.width).coerceIn(0f, 1f),
                            ((touch.y - at.y) / panelSize.height).coerceIn(0f, 1f),
                        )
                    } else androidx.compose.ui.graphics.TransformOrigin.Center
                    this.scaleX = scaleX.value
                    this.scaleY = scaleY.value
                    this.alpha = if (measured) alpha.value else 0f
                }
                // Taps on the panel's empty areas must not reach the dismiss layer.
                .pointerInput(Unit) { detectTapGestures { } },
        ) {
            CompositionLocalProvider(LocalV2FloatingClose provides close) { content() }
        }
    }
}
