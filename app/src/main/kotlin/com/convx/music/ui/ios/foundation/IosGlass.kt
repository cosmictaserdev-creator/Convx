/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.ui.theme.isHueless
import androidx.compose.ui.util.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.backdrop.drawBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.colorControls
import com.convx.music.ui.component.backdrop.effects.lens
import com.convx.music.ui.component.backdrop.highlight.Highlight
import com.convx.music.ui.component.backdrop.isRenderEffectSupported
import com.convx.music.ui.component.backdrop.shadow.Shadow
import com.convx.music.ui.ios.foundation.IosTheme.colors

/**
 * The two liquid-glass materials iOS 26 exposes: `.glass` (clear, lets the backdrop
 * read through) and `.glassProminent` (tinted, used for the primary action).
 *
 * SwiftUI calls the same distinction `GlassButtonStyle` vs `GlassProminentButtonStyle`.
 */
enum class IosGlassStyle { Regular, Prominent }

/**
 * The tuning of one glass surface. Defaults follow the values Apple's own chrome
 * reads at: a small blur, a tall shallow lens, and a bright specular rim.
 */
@Immutable
data class IosGlassSpec(
    val blurRadius: Dp = 2.dp,
    val refractionHeight: Dp = 12.dp,
    val refractionAmount: Dp = 24.dp,
    val saturation: Float = 1.5f,
    val chromaticAberration: Boolean = false,
    val depthEffect: Boolean = false,
    val highlight: Highlight? = Highlight.Default,
    val shadow: Shadow? = Shadow.Default,
) {
    companion object {
        /** Chrome that floats over content: tab bars, toolbars, menus. */
        val Chrome = IosGlassSpec()

        /** Controls the finger touches — a deeper lens so the press reads. */
        val Control = IosGlassSpec(refractionHeight = 16.dp, refractionAmount = 28.dp)

        /** Large surfaces (sheets), where a heavy blur would cost too much. */
        val Sheet = IosGlassSpec(
            blurRadius = 8.dp,
            refractionHeight = 20.dp,
            refractionAmount = 32.dp,
            shadow = Shadow(radius = 32.dp),
        )
    }
}

/**
 * Draws a liquid-glass surface behind this node, refracting [backdrop].
 *
 * Falls back to a plain translucent fill wherever `RenderEffect` is unavailable
 * (below API 31, or an emulator without the shader path), so callers never have to
 * branch on device capability themselves.
 *
 * @param interactive when true the surface stretches and lifts toward the finger,
 *   the way iOS 26 glass buttons do.
 * @param tint hue pushed into the glass — `.glassEffect(.regular.tint(_:))`.
 */
@Composable
fun Modifier.iosGlass(
    shape: Shape,
    style: IosGlassStyle = IosGlassStyle.Regular,
    spec: IosGlassSpec = IosGlassSpec.Chrome,
    tint: Color = Color.Unspecified,
    interactive: Boolean = false,
    backdrop: Backdrop = currentIosBackdrop(),
    wash: Color = Color.Transparent,
): Modifier {
    val colors = IosTheme.colors
    val isDark = colors.isDark
    val baseSurfaceColor = com.convx.music.ui.theme.GlassTintTokens.glassBaseColor(isDark)
    val baseOpacity = if (isDark) {
        com.convx.music.ui.theme.GlassTintTokens.BaseSurfaceOpacityDark
    } else {
        com.convx.music.ui.theme.GlassTintTokens.BaseSurfaceOpacityLight
    }

    val isHeroContext = tint.isSpecified && !tint.isHueless()
    val activeTint = if (isHeroContext) tint else com.convx.music.ui.theme.GlassTintTokens.neutralTintColor(isDark)
    val tintOpacity = if (isHeroContext) com.convx.music.ui.theme.GlassTintTokens.heroTintOpacity(isDark) else 1f
    // Neutral tint carries its own spec alpha; copy(alpha=1) would go opaque.
    val effectiveTintAlpha = (activeTint.alpha * tintOpacity).coerceIn(0f, 1f)

    if (!isRenderEffectSupported()) {
        if (style == IosGlassStyle.Prominent) {
            val prominent = if (tint.isSpecified) tint else colors.accent
            return clip(shape).background(prominent.copy(alpha = 0.82f)).background(wash)
        }
        return clip(shape)
            .background((baseSurfaceColor.copy(alpha = baseOpacity)))
            .background(activeTint.copy(alpha = effectiveTintAlpha))
            .background(wash)
    }

    val scope = rememberCoroutineScope()
    val highlight = remember(scope) { InteractiveHighlight(animationScope = scope) }

    return this
        .drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                colorControls(saturation = spec.saturation)
                blur(spec.blurRadius.toPx())
                lens(
                    refractionHeight = spec.refractionHeight.toPx(),
                    refractionAmount = spec.refractionAmount.toPx(),
                    depthEffect = spec.depthEffect,
                    chromaticAberration = spec.chromaticAberration,
                )
            },
            highlight = { spec.highlight },
            shadow = { spec.shadow },
            layerBlock = if (interactive) {
                {
                    val press = highlight.pressProgress
                    val lift = lerp(1f, 1f + 4.dp.toPx() / size.height, press)
                    scaleX = lift
                    scaleY = lift
                }
            } else {
                null
            },
            onDrawSurface = {
                when (style) {
                    IosGlassStyle.Prominent -> {
                        if (tint.isSpecified) {
                            drawRect(baseSurfaceColor.copy(alpha = baseOpacity))
                            drawRect(tint, blendMode = BlendMode.Hue)
                            drawRect(tint.copy(alpha = 0.82f))
                        } else {
                            drawRect(colors.accent.copy(alpha = 0.82f))
                        }
                    }

                    IosGlassStyle.Regular -> {
                        drawRect(baseSurfaceColor.copy(alpha = baseOpacity))
                        drawRect(activeTint.copy(alpha = effectiveTintAlpha))
                    }
                }
                if (wash.alpha > 0f) drawRect(wash)
            },
        )
        .then(if (interactive) highlight.modifier.then(highlight.gestureModifier) else Modifier)
}

/** The label colour that stays legible on a glass surface of this [style]. */
@Composable
fun iosGlassContentColor(style: IosGlassStyle, tint: Color = Color.Unspecified): Color {
    val colors = IosTheme.colors
    return when (style) {
        IosGlassStyle.Prominent -> Color.White
        IosGlassStyle.Regular -> com.convx.music.ui.theme.GlassTintTokens.glassContentColor(
            isDark = colors.isDark,
            activeTint = tint,
            isSelected = false,
        )
    }
}
