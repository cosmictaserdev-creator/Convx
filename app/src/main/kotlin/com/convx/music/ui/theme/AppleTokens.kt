package com.convx.music.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.designsystem.ConvxDesignTokens
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Apple Music design tokens — single source of truth for colors, shapes, motion, and contrast helpers.
 */
object AppleTokens {
    val AccentRed = ConvxDesignTokens.Colors.AccentRed

    // Surfaces — Apple-style soft dark greys, not pure black (pure black is an OLED opt-in via PureBlack).
    val Bg = ConvxDesignTokens.Colors.Background
    val BgElevated = ConvxDesignTokens.Colors.BackgroundElevated
    val Card = ConvxDesignTokens.Colors.Card
    val CardSecondary = ConvxDesignTokens.Colors.CardSecondary

    /**
     * Hairline rule derived from the current content color, so it stays visible on light and
     * artwork-tinted surfaces.
     */
    val divider: Color
        @Composable get() = LocalContentColor.current.copy(alpha = 0.12f)

    /** Metadata text color on artwork-tinted screens. */
    val Metadata: Color
        @Composable get() = LocalMetadataColor.current

    /** Horizontal screen gutter. The only gutter. */
    val Gutter = ConvxDesignTokens.Metrics.ContentGutter
    /** Gap between siblings in a grid or list. */
    val ItemGap = ConvxDesignTokens.Metrics.ItemGap
    /** Gap between a section and the next section's header. */
    val SectionGap = ConvxDesignTokens.Metrics.SectionGap
    /** Gap between stacked text lines inside one item. */
    val TextGap = ConvxDesignTokens.Metrics.TextGap

    // Type scale — design-pinned sizes, not Material scale: tile-grid alignment depends on title/subtitle matching exactly.

    // Each size carries its own line height — Compose's derived leading lands taller than the design at every step.

    /** Screen title ("Listen Now"). */
    val TitleLarge = ConvxDesignTokens.Type.ScreenTitle
    val TitleLargeLineHeight = ConvxDesignTokens.Type.ScreenTitleLineHeight
    /** Section header ("Recently Played"). */
    val SectionHeader = ConvxDesignTokens.Type.SectionHeader
    val SectionHeaderLineHeight = ConvxDesignTokens.Type.SectionHeaderLineHeight
    /** Tile title and list-row primary text. */
    val ItemTitle = ConvxDesignTokens.Type.ItemTitle
    val ItemTitleLineHeight = ConvxDesignTokens.Type.ItemTitleLineHeight
    /** Tile subtitle, row metadata. */
    val ItemSubtitle = ConvxDesignTokens.Type.ItemSubtitle
    val ItemSubtitleLineHeight = ConvxDesignTokens.Type.ItemSubtitleLineHeight
    /** Speed dial captions. */
    val Caption = ConvxDesignTokens.Type.Caption
    val CaptionLineHeight = ConvxDesignTokens.Type.CaptionLineHeight

    // Shapes — four corners, no more. All continuous (squircle) curvature — see [AppShapes].
    val Artwork = ConvxDesignTokens.Metrics.ArtworkCorner
    val Control = 8.dp
    val CardCorner = ConvxDesignTokens.Metrics.CardCorner
    val CardCornerLarge = ConvxDesignTokens.Metrics.LargeCardCorner

    // Motion — modelled on SwiftUI `.smooth` (0.5s natural period, zero bounce).
    // Compose takes stiffness where SwiftUI takes a period: stiffness = (2*pi / period)^2.
    object Motion {
        /** (2*pi / 0.5s)^2. The period behind every spec below. */
        const val Stiffness = 158f

        /** SwiftUI `.smooth` — the default for essentially everything. */
        fun <T> standard() = spring<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Stiffness)

        /** SwiftUI `.snappy` / `bounce: 0.15` — selection, toggles, emphasis. */
        fun <T> emphasis() = spring<T>(dampingRatio = 0.85f, stiffness = Stiffness)

        /** `bounce: 0.24` — hero and entrance transitions only. */
        fun <T> expressive() = spring<T>(dampingRatio = 0.76f, stiffness = Stiffness)

        /** SwiftUI `.easeInOut(duration: 0.2)` — cross-fades, where a spring has nothing to overshoot. */
        fun <T> fade() = tween<T>(durationMillis = 200)
    }

    // Adaptive contrast helpers

    /**
     * Primary text/icon color for content sitting on [bg] — typically a screen's
     * own hero tint, extracted from its artwork.
     *
     * Keeps [bg]'s hue and pushes lightness to a readable extreme, rather than
     * dropping to flat white/near-black. The text then belongs to the screen's
     * color instead of being pasted on top of it. Saturation is capped low: this
     * is body copy, so it should read as white/black that happens to be warm or
     * cool, not as colored text.
     */
    fun onColor(bg: Color): Color = bg.shiftedForContrast(saturation = 0.16f, lightness = 0.96f to 0.10f)

    /**
     * Secondary text on [bg]: same hue, but the tint is allowed to show. Use for
     * subtitles, counts and metadata — the role that reads as washed-out grey
     * when it comes from Material's neutral palette.
     */
    fun onColorSecondary(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.42f, lightness = 0.76f to 0.34f)

    /**
     * Headings on [bg] — artist names, section titles, the top-bar title. Carries
     * the most tint of the three: a heading is short and large, so it can be
     * clearly the screen's colour without costing legibility the way body copy
     * would.
     */
    fun onColorHeading(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.55f, lightness = 0.88f to 0.18f)

    /**
     * @param lightness (on-dark, on-light) target lightness.
     */
    /**
     * Luminance where black and white text have equal WCAG contrast (~0.179). Above it black wins;
     * the old 0.5 split gave mid-tone backgrounds white text at about 2.5:1.
     */
    const val LightContentMaxLuminance = 0.179f

    /** True when light content reads better than dark content on this background. */
    fun prefersLightContent(bg: Color): Boolean = bg.luminance() <= LightContentMaxLuminance

    fun contrastRatio(a: Color, b: Color): Float {
        val l1 = maxOf(a.luminance(), b.luminance())
        val l2 = minOf(a.luminance(), b.luminance())
        return (l1 + 0.05f) / (l2 + 0.05f)
    }

    private const val MinTextContrast = 4.5f

    private fun Color.shiftedForContrast(saturation: Float, lightness: Pair<Float, Float>): Color {
        val hsl = FloatArray(3)
        androidx.core.graphics.ColorUtils.colorToHSL(toArgb(), hsl)
        val onDark = prefersLightContent(this)
        val target = if (onDark) lightness.first else lightness.second
        // No artwork (the tint is still black) or a monochrome cover means there
        // is no hue to carry. For primary text, nudging lightness off the extreme
        // would only mute it to a grey, so hand back plain white/near-black.
        // Secondary text is meant to sit back, so it keeps its target.
        if (hsl[1] < 0.08f) {
            hsl[1] = 0f
            hsl[2] = if (target > 0.9f) 1f else if (target < 0.12f) 0.04f else target
        } else {
            hsl[1] = minOf(hsl[1], saturation)
            hsl[2] = target
        }
        var result = Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
        // Secondary and heading targets sit back from the extreme; walk toward it until the text passes.
        var steps = 0
        while (contrastRatio(result, this) < MinTextContrast && steps++ < 12) {
            hsl[2] = (hsl[2] + if (onDark) 0.04f else -0.04f).coerceIn(0f, 1f)
            result = Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
        }
        return result
    }
}

/**
 * Backing local for [AppleTokens.Metadata]. Defaults to iOS `systemGray` — the value
 * that token has always been — so only a screen that explicitly provides something
 * else sees any change.
 */
val LocalMetadataColor = compositionLocalOf { Color(0xFF8E8E93) }

/**
 * The M3 shape scale, bound to [AppleTokens]' four corners so that anything
 * reaching for `MaterialTheme.shapes` lands on the same vocabulary as code that
 * names the token directly.
 */
val AppShapes = Shapes(
    extraSmall = ContinuousRoundedRectangle(4.dp),
    small = ContinuousRoundedRectangle(AppleTokens.Artwork),
    medium = ContinuousRoundedRectangle(AppleTokens.Control),
    large = ContinuousRoundedRectangle(AppleTokens.CardCorner),
    extraLarge = ContinuousRoundedRectangle(AppleTokens.CardCornerLarge),
)
