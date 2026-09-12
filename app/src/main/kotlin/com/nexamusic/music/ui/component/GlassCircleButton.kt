/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexamusic.app.ui.component.shapes.ContinuousRoundedRectangle
import com.nexamusic.app.ui.utils.pressWobble

// Kyant0/Capsule's continuous (superellipse) circle instead of CircleShape's
// circular-arc corners — same reasoning as the floating nav bar/search pill:
// smoother, and it's what liquidGlass's lens effect is tuned against.
private val GlassCircleShape = ContinuousRoundedRectangle(percent = 50)

/**
 * A floating circular liquid-glass button — the back/share/favorite buttons on
 * the Artist/Album/Playlist detail screens, replacing their Material TopAppBar
 * and flat-colored circular Surface/IconButton. Falls back to a translucent
 * flat circle when glass is disabled or unsupported, same as
 * [com.nexamusic.app.ui.component.FloatingNavBar]'s own useGlass fallback.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    content: @Composable () -> Unit,
) {
    val glassConfig = LocalGlassEffectConfig.current
    // These float over content the same way the nav bar does and are meant to
    // read as the same material, so they follow the nav bar's own on/off switch
    // and its effect values rather than only the global one.
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()

    val backgroundModifier = if (useGlass) {
        Modifier.liquidGlass(
            config = glassConfig,
            shape = GlassCircleShape,
            highlightAlpha = 0.3f,
            // backdropScale is left at liquidGlass's default
            // (glassResolutionScale of the blur radius) so these record their
            // backdrop at the same reduced resolution the nav bar uses instead
            // of full res — the blur hides the upscale.
        )
    } else {
        Modifier
            .clip(GlassCircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f))
    }
    val contentColor = if (useGlass) glassConfig.textColor else MaterialTheme.colorScheme.onSurface

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .pressWobble(interactionSource)
            .size(size)
            .clip(GlassCircleShape)
            .then(backgroundModifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button,
                indication = ripple(bounded = false, radius = size / 2),
                interactionSource = interactionSource,
            )
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}
