/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.constants.AppBackgroundColorKey
import com.convx.music.constants.AppTextColorKey
import com.convx.music.utils.rememberPreference

/**
 * Semantic iOS-style colors for content and Liquid Glass surfaces.
 *
 * Use these roles instead of hard-coded colors so light/dark themes stay
 * consistent. User-selected background and text colors override the defaults
 * when set; clearing them restores the system palette.
 */
object IosDesignSystem {
    /** Screen canvas. */
    val IosColors.canvas: Color get() = systemGroupedBackground

    /** Card/group on the canvas. */
    val IosColors.card: Color get() = secondarySystemGroupedBackground

    /** Nested group inside a card. */
    val IosColors.nested: Color get() = tertiarySystemGroupedBackground

    /** Plain-screen background (non-grouped screens). */
    val IosColors.plainBackground: Color get() = systemBackground

    /** Card on a plain screen. */
    val IosColors.plainCard: Color get() = secondarySystemBackground

    /** Primary text — safety/critical copy always uses this. */
    val IosColors.primaryText: Color get() = label

    /** Secondary text — metadata, subtitles. */
    val IosColors.secondaryText: Color get() = secondaryLabel

    /** Tertiary text — captions, footnotes. */
    val IosColors.tertiaryText: Color get() = tertiaryLabel

    /** Hairline dividers. */
    val IosColors.hairline: Color get() = separator
}

/**
 * Default background: semantic grouped card, unless the user picked an
 * Advanced override ([AppBackgroundColorKey] != 0). 0 = system.
 */
@Composable
fun rememberDesignBackground(fallback: Color? = null): Color {
    val (overrideInt) = rememberPreference(AppBackgroundColorKey, defaultValue = 0)
    val semantic = IosTheme.colors.secondarySystemGroupedBackground
    return when {
        overrideInt != 0 -> Color(overrideInt)
        fallback != null && fallback.isSpecified -> fallback
        else -> semantic
    }
}

/**
 * Default text: semantic label, unless the user picked an Advanced override
 * ([AppTextColorKey] != 0). 0 = system.
 */
@Composable
fun rememberDesignText(): Color {
    val (overrideInt) = rememberPreference(AppTextColorKey, defaultValue = 0)
    return if (overrideInt != 0) Color(overrideInt) else IosTheme.colors.label
}

/**
 * Chrome content color: monochromatic label by default (dark over light
 * content, light over dark). Accent only when [emphasize] (single primary
 * action / status) — never tint multiple chrome controls.
 */
@Composable
@ReadOnlyComposable
fun chromeContent(emphasize: Boolean = false): Color =
    if (emphasize) IosTheme.colors.accent else IosTheme.colors.label
