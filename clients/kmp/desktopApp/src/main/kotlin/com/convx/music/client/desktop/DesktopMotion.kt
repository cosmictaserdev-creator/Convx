package com.convx.music.client.desktop

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent

internal val LocalDesktopReducedMotion = staticCompositionLocalOf { false }

/** One interaction source keeps press feedback, hover and click cancellation in sync. */
@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.desktopInteractive(
    enabled: Boolean = true,
    scale: Float = .975f,
    hoverColor: Color = Color.Transparent,
    onContextMenu: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val reduced = LocalDesktopReducedMotion.current
    val size by animateFloatAsState(if (pressed && enabled && !reduced) scale else 1f,
        if (reduced) tween(0) else spring(dampingRatio = 1f, stiffness = 900f), label = "Desktop press")
    val fill by animateColorAsState(if (hovered && enabled) hoverColor else Color.Transparent,
        tween(if (reduced) 0 else 120), label = "Desktop hover")
    graphicsLayer { scaleX = size; scaleY = size }
        .background(fill).hoverable(interaction, enabled)
        .then(if (onContextMenu == null) Modifier
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
        else Modifier.onPointerEvent(PointerEventType.Press) { event ->
            if (enabled && event.buttons.isSecondaryPressed) {
                onContextMenu()
                event.changes.forEach { it.consume() }
            }
        }.combinedClickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button,
            onLongClick = onContextMenu, onLongClickLabel = "Show options", onClick = onClick))
}
