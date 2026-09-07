/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.utils.pressWobble

/**
 * A circular liquid glass icon button.
 *
 * The material is [Modifier.liquidGlass], so it follows the user's blur, lens,
 * vibrancy, tint and per-component switches like every other glass surface. The
 * *interaction* is the reference implementation's: a slight growth plus a
 * tanh-damped lean toward the finger, with a soft light that blooms under the
 * touch point and fades as the press releases ([liquidGlassPressLayerBlock] and
 * [InteractiveHighlight]).
 *
 * That replaces the app's previous press feedback for glass buttons, a uniform
 * `pressWobble` scale-down to 0.86 — a Material idiom, and the opposite direction
 * to how a liquid surface responds: glass swells and follows the finger rather
 * than shrinking away from it.
 *
 * Degrades in two steps rather than one. With the glass style set to TRANSPARENT,
 * or with no backdrop recorded behind the button, `liquidGlass` supplies its own
 * translucent tint and the press physics stay. Only when the platform cannot do
 * glass at all (below API 31, or a low-RAM device) does the button fall back to a
 * flat fill and the old wobble.
 *
 * @param containerColor tint laid over the glass. [Color.Unspecified] keeps the
 * global glass tint; a specified colour is what a call site migrating from
 * `FilledIconButton`'s `containerColor` should pass.
 * @param contentColor icon colour. [Color.Unspecified] takes the glass text
 * colour, matching [GlassCircleButton]'s long-standing choice.
 * @param glassComponent which per-component switch in [GlassEffectConfig] gates it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiquidGlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // [Dp.Unspecified] leaves sizing to [modifier], for call sites that size the
    // button through layout — the transport row, where three buttons share a row
    // and the pressed one grows through an animated weight instead of a fixed box.
    size: Dp = 44.dp,
    shape: CornerBasedShape = LiquidGlassCircleShape,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    onLongClick: (() -> Unit)? = null,
    glassComponent: GlassComponent = GlassComponent.PLAYER,
    config: GlassEffectConfig = LocalGlassEffectConfig.current,
    highlightAlpha: Float = 0.3f,
    // Pass one in when something else is already watching the press — the player's
    // transport row animates its button weights off collectIsPressedAsState, and a
    // private interaction source here would leave it deaf.
    interactionSource: MutableInteractionSource? = null,
    // False keeps the light that blooms under the finger but drops the growth and
    // the lean, for call sites with press feedback of their own: growing a button
    // that already grows itself through its layout weight doubles the effect.
    growOnPress: Boolean = true,
    content: @Composable () -> Unit,
) {
    val glassAllowed = isGlassAllowed()
    val glassEnabled = config.isEnabledFor(glassComponent)
    // Drives the press physics and the glow: both need a surface that is actually
    // refracting to be worth anything.
    val liquid = rememberLiquidGlassActive(glassComponent, config)

    val animationScope = rememberCoroutineScope()
    val interaction = remember(animationScope, liquid) {
        if (liquid) InteractiveHighlight(animationScope = animationScope) else null
    }
    val pressLayer = remember(interaction, growOnPress) {
        if (growOnPress) interaction?.let { liquidGlassPressLayerBlock(it) } else null
    }
    val ownInteractionSource = remember { MutableInteractionSource() }
    val resolvedInteractionSource = interactionSource ?: ownInteractionSource

    val resolvedContentColor = when {
        contentColor.isSpecified -> contentColor
        glassAllowed && glassEnabled -> config.textColor
        else -> MaterialTheme.colorScheme.onSurface
    }
    val surface =
        if (glassAllowed) {
            Modifier.liquidGlass(
                config = config,
                shape = shape,
                highlightAlpha = highlightAlpha,
                surfaceTintOverride = containerColor,
            )
        } else {
            Modifier.background(
                if (containerColor.isSpecified) {
                    containerColor
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                }
            )
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(
                when {
                    pressLayer != null -> Modifier.graphicsLayer(pressLayer)
                    // No glass: keep the feedback this button always had.
                    else -> Modifier.pressWobble(resolvedInteractionSource)
                }
            )
            .then(if (size != Dp.Unspecified) Modifier.size(size) else Modifier)
            .clip(shape)
            .then(surface)
            // Glow paints over the glass and under the icon, which is where a
            // light source that follows the finger belongs.
            .then(interaction?.modifier ?: Modifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled,
                role = Role.Button,
                // Unbounded and thumb-sized for a square button. A layout-sized one
                // has no radius to derive, so its ripple clips to its own shape.
                indication =
                    if (size != Dp.Unspecified) ripple(bounded = false, radius = size / 2)
                    else ripple(),
                interactionSource = resolvedInteractionSource,
            )
            // Observes the press without consuming, so the clickable above still
            // sees its tap.
            .then(interaction?.gestureModifier ?: Modifier)
    ) {
        CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
            content()
        }
    }
}

/**
 * A pill-shaped liquid glass button with row content — the reference
 * implementation's `LiquidButton`, rebuilt on this app's config-driven material
 * instead of the library's hardcoded blur and lens values.
 *
 * Same interaction model as [LiquidGlassIconButton]; see there for why.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: CornerBasedShape = RoundedCornerShape(percent = 50),
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    glassComponent: GlassComponent = GlassComponent.PLAYER,
    config: GlassEffectConfig = LocalGlassEffectConfig.current,
    highlightAlpha: Float = 0.3f,
    onLongClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    growOnPress: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val glassAllowed = isGlassAllowed()
    val glassEnabled = config.isEnabledFor(glassComponent)
    val liquid = rememberLiquidGlassActive(glassComponent, config)

    val animationScope = rememberCoroutineScope()
    val interaction = remember(animationScope, liquid) {
        if (liquid) InteractiveHighlight(animationScope = animationScope) else null
    }
    val pressLayer = remember(interaction, growOnPress) {
        if (growOnPress) interaction?.let { liquidGlassPressLayerBlock(it) } else null
    }
    val ownInteractionSource = remember { MutableInteractionSource() }
    val resolvedInteractionSource = interactionSource ?: ownInteractionSource

    val resolvedContentColor = when {
        contentColor.isSpecified -> contentColor
        glassAllowed && glassEnabled -> config.textColor
        else -> MaterialTheme.colorScheme.onSurface
    }
    val surface =
        if (glassAllowed) {
            Modifier.liquidGlass(
                config = config,
                shape = shape,
                highlightAlpha = highlightAlpha,
                surfaceTintOverride = containerColor,
            )
        } else {
            Modifier.background(
                if (containerColor.isSpecified) {
                    containerColor
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                }
            )
        }

    Row(
        modifier = modifier
            .then(
                when {
                    pressLayer != null -> Modifier.graphicsLayer(pressLayer)
                    else -> Modifier.pressWobble(resolvedInteractionSource)
                }
            )
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .then(surface)
            .then(interaction?.modifier ?: Modifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled,
                role = Role.Button,
                indication = ripple(),
                interactionSource = resolvedInteractionSource,
            )
            .then(interaction?.gestureModifier ?: Modifier)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
            content()
        }
    }
}

/**
 * Kyant0/Capsule's continuous (superellipse) circle rather than `CircleShape`'s
 * circular-arc corners — the same reasoning as the floating nav bar and
 * [GlassCircleButton]: smoother, and it is what the lens effect is tuned against.
 */
internal val LiquidGlassCircleShape: CornerBasedShape = ContinuousRoundedRectangle(percent = 50)
