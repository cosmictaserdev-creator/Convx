/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.dropShadow
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import com.convx.music.ui.theme.isHueless
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.utils.additiveChrome
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

// Kyant0/Capsule's continuous (superellipse) circle instead of CircleShape's
// circular-arc corners — same reasoning as the floating nav bar/search pill:
// smoother, and it's what liquidGlass's lens effect is tuned against.
private val GlassCircleShape = ContinuousRoundedRectangle(percent = 50)

/**
 * A floating circular glass button for Artist, Album, and Playlist screens.
 * Falls back to a translucent circle when glass is disabled or unsupported.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    // Same idea as the nav bar's own rim tint: the caller's current artwork/background
    // colour, already boosted (asRimPop). Unspecified keeps the plain default rim.
    rimTintColor: Color = Color.Unspecified,
    // Photos (account avatar) must not run through additiveChrome: Plus adds
    // white over every pixel and washes the photo out. Icons keep it.
    applyAdditiveChrome: Boolean = true,
    // Draw a flat fill while keeping the button interaction effects.
    forceFlat: Boolean = false,
    // Sample an existing Haze source instead of applying the full glass effect.
    hazeState: HazeState? = null,
    content: @Composable () -> Unit,
) {
    // Non-nav glass: thinner lens per spec Q17 (.35/.40), fixed style.
    val glassConfig = LocalGlassEffectConfig.current.withFixedStyle().forAccessory()
    // These float over content the same way the nav bar does and are meant to
    // read as the same material, so they follow the nav bar's own on/off switch
    // and its effect values rather than only the global one.
    val useGlass = !forceFlat && hazeState == null &&
        glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()

    val ambientColor = com.convx.music.LocalAmbientBackgroundColor.current.value
    // Single limelight: no local rim computation — pass an explicit caller tint
    // through, otherwise Unspecified so liquidGlass derives the same shared
    // ambient rim every other glass surface on this screen uses.
    val isDark = glassUsesDarkAppearance()
    val isHeroContext = (ambientColor.isSpecified && !ambientColor.isHueless()) || rimTintColor.isSpecified
    val highlightAlpha = com.convx.music.ui.theme.GlassTintTokens.pluckOpacity(isDark, isHeroContext)
    val effectiveRimTintColor = if (rimTintColor.isSpecified) rimTintColor else com.convx.music.ui.theme.GlassTintTokens.pluckColor(isDark, ambientColor)

    val fallbackFill = com.convx.music.ui.theme.GlassTintTokens.baseSurfaceColor(isDark).copy(
        alpha = if (isDark) com.convx.music.ui.theme.GlassTintTokens.NavPillFillOpacityDark else com.convx.music.ui.theme.GlassTintTokens.NavPillFillOpacityLight
    )

    val backgroundModifier = if (useGlass) {
        Modifier.liquidGlass(
            config = glassConfig,
            shape = GlassCircleShape,
            highlightAlpha = highlightAlpha,
            rimTintColor = effectiveRimTintColor,
        )
    } else if (!forceFlat && hazeState != null) {
        Modifier
            .clip(GlassCircleShape)
            .hazeEffect(state = hazeState, style = HazeMaterials.thin(fallbackFill)) {
                // Same idea as GlassPoolScale/GLASS_RESOLUTION_SCALE elsewhere: a
                // small circular button hides the upscale completely, so sample far
                // below Haze's own default.
                inputScale = HazeInputScale.Fixed(0.2f)
            }
    } else {
        Modifier
            .clip(GlassCircleShape)
            .background(fallbackFill)
    }
    // Hero exemption Q14: tinted screens force dark chrome even in light mode.
    val effectiveDark = isDark || isHeroContext
    val contentColor = if (useGlass) {
        glassConfig.resolvedTextColor(activeTint = ambientColor)
    } else {
        com.convx.music.ui.theme.GlassTintTokens.glassContentColor(isDark = effectiveDark, activeTint = ambientColor)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val elasticStretch = rememberElasticStretch()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer(elasticStretchLayerBlock(elasticStretch))
            .size(size)
            .chromeShadow(GlassCircleShape)
            .clip(GlassCircleShape)
            .then(backgroundModifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button,
                indication = ripple(bounded = false, radius = size / 2),
                interactionSource = interactionSource,
            )
            .then(elasticStretch.modifier)
            .then(elasticStretch.gestureModifier)
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(modifier = Modifier.additiveChrome(applyAdditiveChrome && useGlass && effectiveDark)) {
                content()
            }
        }
    }
}

/**
 * A flat circular button whose fill and content colour both come from the
 * screen's own artwork tint, not from glass or a fixed Material color — the
 * back/share/favorite buttons on an artwork-tinted screen (Artist, Album)
 * that wants its whole chrome to read as *that record's* color rather than
 * floating glass over it.
 *
 * [onTint] should be the same contrast color the rest of the screen already
 * derives from its tint (e.g. `AppleTokens.onColor(tint)`), so a light-tinted
 * cover gets dark buttons/icons and a dark-tinted cover gets light ones.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContrastCircleButton(
    onTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    filled: Boolean = false,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val elasticStretch = rememberElasticStretch()

    val bg = if (filled) onTint else onTint.copy(alpha = 0.16f)
    val contentColor = if (filled) {
        if (onTint == Color.White) Color.Black else Color.White
    } else {
        onTint
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer(elasticStretchLayerBlock(elasticStretch))
            .size(size)
            .chromeShadow(GlassCircleShape)
            .clip(GlassCircleShape)
            .background(bg)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button,
                indication = ripple(bounded = false, radius = size / 2),
                interactionSource = interactionSource,
            )
            .then(elasticStretch.modifier)
            .then(elasticStretch.gestureModifier)
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}
