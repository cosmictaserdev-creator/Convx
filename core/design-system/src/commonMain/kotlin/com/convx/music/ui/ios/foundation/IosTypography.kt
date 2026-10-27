/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The eleven iOS text styles, at their default (Large) Dynamic Type size.
 *
 * Hosts supply the selected font family; the shared client uses the same bundled
 * family and weight mapping as the production Android client.
 */
@Immutable
data class IosTypography(
    val largeTitle: TextStyle,
    val title: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val headline: TextStyle,
    val subheadline: TextStyle,
    val body: TextStyle,
    val callout: TextStyle,
    val footnote: TextStyle,
    val caption: TextStyle,
    val caption2: TextStyle,
)

fun iosTypography(fontFamily: FontFamily = FontFamily.Default): IosTypography {
    fun style(size: Int, lineHeight: Int, weight: FontWeight, tracking: Float) = TextStyle(
        fontFamily = fontFamily,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        letterSpacing = tracking.sp,
    )

    // Use stronger weights, tighter large-title tracking, and explicit line height
    // to keep Apple Music-style titles distinct and readable.
    fun display(size: Int, weight: FontWeight, tracking: Float) = TextStyle(
        fontFamily = fontFamily,
        fontSize = size.sp,
        lineHeight = (size * 1.2f).sp,
        fontWeight = weight,
        letterSpacing = tracking.sp,
    )

    return IosTypography(
        largeTitle = display(34, FontWeight.W800, -0.8f),
        title = display(28, FontWeight.W800, -0.7f),
        title2 = display(22, FontWeight.W700, -0.4f),
        title3 = display(20, FontWeight.W700, -0.3f),
        headline = display(16, FontWeight.W600, -0.2f),
        subheadline = style(15, 20, FontWeight.Normal, -0.24f),
        // Neutral body text lets weight and spacing carry the hierarchy.
        body = display(16, FontWeight.W400, 0f),
        callout = display(14, FontWeight.W400, 0f),
        footnote = style(13, 18, FontWeight.Normal, -0.08f),
        caption = style(12, 16, FontWeight.Normal, 0f),
        caption2 = style(11, 13, FontWeight.W600, 0f),
    )
}
