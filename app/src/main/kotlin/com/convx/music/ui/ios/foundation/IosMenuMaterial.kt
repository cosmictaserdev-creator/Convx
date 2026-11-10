/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import com.convx.music.ui.utils.additiveChrome
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.backdrop.drawBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.colorControls
import com.convx.music.ui.component.backdrop.effects.lens
import com.convx.music.ui.component.backdrop.isRenderEffectSupported
import com.convx.music.ui.component.backdrop.shadow.Shadow
import com.convx.music.ui.component.backdrop.highlight.Highlight
import com.convx.music.ui.component.backdrop.highlight.HighlightStyle
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.ui.theme.isHueless
import com.convx.music.ui.component.elasticStretchLayerBlock

/**
 * Colors for the iOS-style menu panel. Burn and dodge blends flatten to solid colors
 * when composited over the panel's uniform frost.
 */
@Immutable
data class IosMenuColors(
    /** Laid over the blurred backdrop. */
    val fill: Color,
    val label: Color,
    val secondaryLabel: Color,
    val separator: Color,
    /** The rounded fill under the entry the finger is on (iOS 27). */
    val highlight: Color,
)

object IosMenuMaterial {
    /** Opaque charcoal used when a menu surface cannot show the page through it. */
    val DarkPanelFill = Color(0xFF1C1C1E)

    /** Menu panel corner radius. */
    val CornerRadius = 32.dp

    /** Smoothed panel silhouette. */
    val PanelShape = com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(
        CornerRadius,
        continuity = com.convx.music.ui.component.shapes.continuities.G2Continuity(
            profile = com.convx.music.ui.component.shapes.continuities.G2ContinuityProfile.figmaSmoothing(0.6),
        ),
    )
    val Width = 238.dp

    /** Separator hairline inset from the panel edges (iOS 27 clip: ~16pt). */
    val SeparatorInset = 16.dp

    /** Frost only — no lens, no rim: a haze-style blur, not refracting liquid glass. */
    val BlurRadius = 8.dp
    /** iOS 27 menus carry the page's hue through, noticeably more saturated than it. */
    const val Saturation = 1.8f

    /** Shadow below the menu panel. */
    val PanelShadow = Shadow(radius = 40.dp, offset = DpOffset(0.dp, 8.dp), color = Color.Black.copy(alpha = 0.12f))

    // Flat equivalents of the light theme's burn and dodge blends over the frost.
    private val Light = IosMenuColors(
        fill = Color(0xFFF5F5F5).copy(alpha = 0.72f),
        label = Color(0xFF1A1A1A),
        secondaryLabel = Color(0xFF1A1A1A).copy(alpha = 0.5f),
        separator = Color.Black.copy(alpha = 0.10f),
        highlight = Color.Black.copy(alpha = 0.07f),
    )

    // Dim gray labels are composited by menuVibrancy, letting the blurred background
    // tint them while keeping the text bright enough to read.
    private val Dark = IosMenuColors(
        fill = DarkPanelFill.copy(alpha = 0.82f),
        label = Color(0xFFF8F8FA),
        secondaryLabel = Color.White.copy(alpha = 0.68f),
        separator = Color.White.copy(alpha = 0.06f),
        highlight = Color.White.copy(alpha = 0.09f),
    )

    /**
     * Long-press menus ([LocalMenuLongPress]): their own glass settings, independent of the app's
     * glass config -- 8dp blur, lens at 25% of [LENS_MAX_DP] (48dp), 68% fill, full-res backdrop.
     */
    val LongPressBlurRadius = 8.dp
    val LongPressLensHeight = 12.dp
    val LongPressLensAmount = 12.dp
    const val LongPressFillAlpha = 0.80f

    /** Liquid variant: lens and blur under a translucent fill. */
    val LiquidBlurRadius = 8.dp
    val LiquidLensHeight = 8.dp
    val LiquidLensAmount = 12.dp

    // Keep the blurred artwork visible through the dark fill; plain labels stay readable.
    private val LiquidDark = IosMenuColors(
        fill = DarkPanelFill.copy(alpha = 0.82f),
        label = Color.White,
        secondaryLabel = Color.White.copy(alpha = 0.70f),
        separator = Color.White.copy(alpha = 0.12f),
        highlight = Color.White.copy(alpha = 0.13f),
    )
    private val LiquidLight = IosMenuColors(
        fill = Color.White.copy(alpha = 0.55f),
        label = Color(0xFF1A1A1A),
        secondaryLabel = Color(0xFF1A1A1A).copy(alpha = 0.5f),
        separator = Color.Black.copy(alpha = 0.10f),
        highlight = Color.Black.copy(alpha = 0.07f),
    )

    val colors: IosMenuColors
        @Composable get() {
            val dark = IosTheme.colors.isDark
            return if (LocalMenuLiquid.current) {
                val base = if (dark) LiquidDark else LiquidLight
                if (LocalMenuLongPress.current) base.copy(fill = base.fill.copy(alpha = LongPressFillAlpha)) else base
            } else {
                if (dark) Dark else Light
            }
        }

    /** The glass edge: brighter along the top-leading side, fading round to the bottom. */
    fun rimBrush(size: Size, dark: Boolean): Brush {
        val a = if (dark) 0.18f else 0.30f
        return Brush.linearGradient(
            0f to Color.White.copy(alpha = a),
            0.45f to Color.White.copy(alpha = a * 0.15f),
            1f to Color.White.copy(alpha = a * 0.55f),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
    }
}

/** Opacity of the glass rim on every menu. */
private const val MenuRimAlpha = 0.20f

/** How far a menu leans toward the finger, relative to a glass button's lean. */
private const val MenuStretch = 2.6f

/** True for menus drawn with the liquid material instead of the frost; every menu is liquid glass by default. */
val LocalMenuLiquid = compositionLocalOf { true }

/** True for the player-menu glass (nav-bar glass, own settings); the default for every menu. */
val LocalMenuLongPress = compositionLocalOf { true }

/**
 * In dark mode, menu labels and highlights blend into the panel so its hue shows through.
 * Light mode keeps plain dark labels.
 */
@Composable
fun Modifier.menuVibrancy(): Modifier = additiveChrome(IosTheme.colors.isDark && !LocalMenuLiquid.current)

/**
 * Menu material: a blurred, saturated [backdrop], theme fill, and panel shadow.
 * [stretch] optionally adds elastic pull and glow without a liquid-glass lens.
 *
 * Works inside popups and sheets: the backdrop is the app root's recorded layer, not a
 * same-window haze source.
 */
@Composable
fun Modifier.iosMenuSurface(
    shape: Shape,
    stretch: InteractiveHighlight? = null,
    backdrop: Backdrop = currentIosBackdrop(),
    /**
     * False on a surface that is itself opaque (a menu sheet): sampling the page behind
     * would punch a window through the sheet, so the fill is laid on flat instead.
     */
    frosted: Boolean = true,
): Modifier {
    val fill = IosMenuMaterial.colors.fill
    val frostBase = if (IosTheme.colors.isDark) Color(0xFF2C2C2E) else Color(0xFFEDEDED)
    // A menu is ~5x a button's size: the button-tuned lean would barely move it.
    val withStretch = if (stretch != null) graphicsLayer(elasticStretchLayerBlock(stretch, magnitude = MenuStretch)) else this
    val surface = if (!frosted) {
        withStretch.shadow(20.dp, shape, ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.12f))
            .clip(shape)
            .background(fill)
    } else if (!isRenderEffectSupported()) {
        // No RenderEffect: the fill alone, opaque enough to read over anything.
        val opaque = if (IosTheme.colors.isDark) IosMenuMaterial.DarkPanelFill else Color(0xFFF2F2F2)
        withStretch.clip(shape).background(opaque)
    } else if (LocalMenuLiquid.current && LocalMenuLongPress.current) {
        // Freeze the backdrop while open; the transition scales the recorded layer.
        val density = LocalDensity.current
        val dark = IosTheme.colors.isDark
        // Keep a restrained highlight around the menu edge.
        val rimColor = com.convx.music.ui.theme.GlassTintTokens.pluckColor(dark, Color.Unspecified)
        val rimAlpha = MenuRimAlpha
        // User-set backdrop resolution (settings slider): pixel-sized params are pre-multiplied
        // by it so the look is the same at any scale.
        val resolution by com.convx.music.utils.rememberPreference(
            com.convx.music.constants.MenuBackdropResolutionKey, defaultValue = 1f,
        )
        val scale = resolution.coerceIn(0.3f, 1f)
        val blurPx = with(density) { IosMenuMaterial.LongPressBlurRadius.toPx() } * scale
        val lensHeightPx = with(density) { IosMenuMaterial.LongPressLensHeight.toPx() } * scale
        val lensAmountPx = with(density) { IosMenuMaterial.LongPressLensAmount.toPx() } * scale
        withStretch.drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                colorControls(saturation = IosMenuMaterial.Saturation)
                blur(blurPx)
                lens(refractionHeight = lensHeightPx, refractionAmount = lensAmountPx)
            },
            backdropScale = scale,
            frozen = { true },
            highlight = {
                Highlight(
                    width = com.convx.music.ui.component.EdgeHighlightWidth,
                    style = HighlightStyle.Default(
                        color = rimColor.copy(alpha = rimAlpha),
                        angle = com.convx.music.ui.component.HighlightAngleFrozen,
                    ),
                )
            },
            shadow = { IosMenuMaterial.PanelShadow },
            onDrawBehind = { drawOutline(shape.createOutline(size, layoutDirection, this), frostBase) },
            onDrawSurface = { drawRect(fill) },
        )
    } else if (LocalMenuLiquid.current) {
        // Very low-res sample (blur ~3px there): the blur hides it, and the lens only has
        // to bend a blurred image. The panel's own clip keeps the silhouette crisp.
        val density = LocalDensity.current
        val blurFullPx = with(density) { IosMenuMaterial.LiquidBlurRadius.toPx() }
        val scale = (3f / blurFullPx).coerceIn(0.05f, 0.25f)
        withStretch.drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                colorControls(saturation = IosMenuMaterial.Saturation)
                blur(blurFullPx * scale)
                lens(
                    refractionHeight = IosMenuMaterial.LiquidLensHeight.toPx() * scale,
                    refractionAmount = IosMenuMaterial.LiquidLensAmount.toPx() * scale,
                )
            },
            backdropScale = scale,
            highlight = null,
            shadow = { IosMenuMaterial.PanelShadow },
            onDrawBehind = { drawOutline(shape.createOutline(size, layoutDirection, this), frostBase) },
            onDrawSurface = { drawRect(fill) },
        )
    } else {
        // Sampled at the scale where the blur radius is <= 6px: Skia then blurs directly
        // instead of running its downsample cascade over a full-res, near screen-tall panel
        // every frame of the open animation (measured 15-25ms RenderThread frames). The
        // 24dp frost hides the upscale, as in TopFadeBlur.
        val blurFullPx = with(LocalDensity.current) { IosMenuMaterial.BlurRadius.toPx() }
        val scale = (6f / blurFullPx).coerceIn(0.05f, 0.25f)
        withStretch.drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                colorControls(saturation = IosMenuMaterial.Saturation)
                blur(blurFullPx * scale)
            },
            backdropScale = scale,
            highlight = null,
            shadow = { IosMenuMaterial.PanelShadow },
        // Draw the frost while the backdrop snapshot initializes to prevent a transparent first frame.
            onDrawBehind = { drawOutline(shape.createOutline(size, layoutDirection, this), frostBase) },
            onDrawSurface = { drawRect(fill) },
        )
    }
    val dark = IosTheme.colors.isDark
    val rimWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val navRim = LocalMenuLiquid.current && LocalMenuLongPress.current
    val rimmed = if (navRim) surface else surface.drawWithContent {
        drawContent()
        drawOutline(
            outline = shape.createOutline(size, layoutDirection, this),
            brush = IosMenuMaterial.rimBrush(size, dark),
            style = Stroke(width = rimWidth),
        )
    }
    return if (stretch != null) rimmed.then(stretch.modifier).then(stretch.gestureModifier) else rimmed
}
