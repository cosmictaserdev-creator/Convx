/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.convx.music.designsystem.ConvxDesignTokens

/**
 * The iOS system palette, split into the light and dark variants Apple ships.
 *
 * Mirrors the "Color" section of exploreswiftui.com: [standard colors][systemBlue],
 * [semantic colors][label], and the UIKit label / fill / separator / background sets.
 */
@Immutable
data class IosColors(
    val isDark: Boolean,

    // Standard colors
    val systemRed: Color,
    val systemOrange: Color,
    val systemYellow: Color,
    val systemGreen: Color,
    val systemMint: Color,
    val systemTeal: Color,
    val systemCyan: Color,
    val systemBlue: Color,
    val systemIndigo: Color,
    val systemPurple: Color,
    val systemPink: Color,
    val systemBrown: Color,

    // Gray variants
    val systemGray: Color,
    val systemGray2: Color,
    val systemGray3: Color,
    val systemGray4: Color,
    val systemGray5: Color,
    val systemGray6: Color,

    // Label colors
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val quaternaryLabel: Color,
    val placeholderText: Color,

    // Fill colors
    val systemFill: Color,
    val secondarySystemFill: Color,
    val tertiarySystemFill: Color,
    val quaternarySystemFill: Color,

    // Separator colors
    val separator: Color,
    val opaqueSeparator: Color,

    // Background colors
    val systemBackground: Color,
    val secondarySystemBackground: Color,
    val tertiarySystemBackground: Color,

    // Grouped background colors
    val systemGroupedBackground: Color,
    val secondarySystemGroupedBackground: Color,
    val tertiarySystemGroupedBackground: Color,

    /** The default accent applied to controls that do not carry an explicit tint. */
    val accent: Color,
) {
    /** The colour a filled control should draw its label in, over [accent]. */
    val onAccent: Color get() = Color.White
}

fun lightIosColors(accent: Color = ConvxDesignTokens.Colors.AccentRed): IosColors = IosColors(
    isDark = false,
    systemRed = Color(0xFFFF3B30),
    systemOrange = Color(0xFFFF9500),
    systemYellow = Color(0xFFFFCC00),
    systemGreen = Color(0xFF34C759),
    systemMint = Color(0xFF00C7BE),
    systemTeal = Color(0xFF30B0C7),
    systemCyan = Color(0xFF32ADE6),
    systemBlue = Color(0xFF007AFF),
    systemIndigo = Color(0xFF5856D6),
    systemPurple = Color(0xFFAF52DE),
    systemPink = Color(0xFFFF2D55),
    systemBrown = Color(0xFFA2845E),
    systemGray = Color(0xFF8E8E93),
    systemGray2 = Color(0xFFAEAEB2),
    systemGray3 = Color(0xFFC7C7CC),
    systemGray4 = Color(0xFFD1D1D6),
    systemGray5 = Color(0xFFE5E5EA),
    systemGray6 = Color(0xFFF2F2F7),
    label = Color(0xFF000000),
    secondaryLabel = Color(0x993C3C43),
    tertiaryLabel = Color(0x4D3C3C43),
    quaternaryLabel = Color(0x2E3C3C43),
    placeholderText = Color(0x4D3C3C43),
    systemFill = Color(0x33787880),
    secondarySystemFill = Color(0x28787880),
    tertiarySystemFill = Color(0x1E767680),
    quaternarySystemFill = Color(0x14747480),
    separator = Color(0x493C3C43),
    opaqueSeparator = Color(0xFFC6C6C8),
    systemBackground = Color(0xFFFFFFFF),
    secondarySystemBackground = Color(0xFFF2F2F7),
    tertiarySystemBackground = Color(0xFFFFFFFF),
    systemGroupedBackground = Color(0xFFF2F2F7),
    secondarySystemGroupedBackground = Color(0xFFFFFFFF),
    tertiarySystemGroupedBackground = Color(0xFFF2F2F7),
    accent = accent,
)

fun darkIosColors(accent: Color = ConvxDesignTokens.Colors.AccentRed): IosColors = IosColors(
    isDark = true,
    systemRed = Color(0xFFFF453A),
    systemOrange = Color(0xFFFF9F0A),
    systemYellow = Color(0xFFFFD60A),
    systemGreen = Color(0xFF30D158),
    systemMint = Color(0xFF63E6E2),
    systemTeal = Color(0xFF40C8E0),
    systemCyan = Color(0xFF64D2FF),
    systemBlue = Color(0xFF0A84FF),
    systemIndigo = Color(0xFF5E5CE6),
    systemPurple = Color(0xFFBF5AF2),
    systemPink = Color(0xFFFF375F),
    systemBrown = Color(0xFFAC8E68),
    systemGray = Color(0xFF8E8E93),
    systemGray2 = Color(0xFF636366),
    systemGray3 = Color(0xFF48484A),
    systemGray4 = Color(0xFF3A3A3C),
    systemGray5 = Color(0xFF2C2C2E),
    systemGray6 = Color(0xFF1C1C1E),
    label = Color(0xFFFFFFFF),
    // Keep dark secondary labels at 70% opacity for readability.
    secondaryLabel = Color(0xB3EBEBF5),
    tertiaryLabel = Color(0x4DEBEBF5),
    quaternaryLabel = Color(0x2EEBEBF5),
    placeholderText = Color(0x4DEBEBF5),
    systemFill = Color(0x5C787880),
    secondarySystemFill = Color(0x51787880),
    tertiarySystemFill = Color(0x3D767680),
    quaternarySystemFill = Color(0x2E767680),
    separator = Color(0x99545458),
    opaqueSeparator = Color(0xFF38383A),
    // Dark system backgrounds use true black.
    systemBackground = Color(0xFF000000),
    secondarySystemBackground = Color(0xFF1C1C1E),
    tertiarySystemBackground = Color(0xFF2C2C2E),
    systemGroupedBackground = Color(0xFF000000),
    secondarySystemGroupedBackground = Color(0xFF1C1C1E),
    tertiarySystemGroupedBackground = Color(0xFF2C2C2E),
    accent = accent,
)
