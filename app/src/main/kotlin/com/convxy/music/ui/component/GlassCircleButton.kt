/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A floating circular liquid-glass button — the back/share/favourite buttons on the
 * Artist/Album/Playlist detail screens and the utility buttons around the player.
 *
 * Now a thin wrapper over [LiquidGlassIconButton]: same signature, so its ~80 call
 * sites are untouched, but two things change underneath.
 *
 * The press feedback is the liquid one — the surface grows slightly and leans
 * toward the finger through a damped curve, with a soft light blooming under the
 * touch point — instead of the uniform scale-down to 0.86 this used to do. That
 * wobble is a Material idiom and the opposite direction to how a liquid surface
 * responds: glass swells and follows the finger rather than shrinking away from it.
 *
 * The surface also picks up the backdrop guard, so glass is never asked to refract
 * nothing when one of these is composed outside a recorded backdrop.
 *
 * These float over content the same way the nav bar does and are meant to read as
 * the same material, so they follow the nav bar's own on/off switch and its effect
 * values rather than only the global one. Falls back to a translucent flat circle
 * when glass is disabled or unsupported, as before.
 */
@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    content: @Composable () -> Unit,
) {
    LiquidGlassIconButton(
        onClick = onClick,
        modifier = modifier,
        onLongClick = onLongClick,
        size = size,
        glassComponent = GlassComponent.NAV_BAR,
        highlightAlpha = 0.3f,
        content = content,
    )
}
