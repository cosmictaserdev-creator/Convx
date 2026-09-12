/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nexamusic.app.R
import com.nexamusic.app.constants.BrandFontEnabledKey
import com.nexamusic.app.utils.rememberPreference

/** The product wordmark, always set in caps. */
const val BrandName = "CONVX"

/** Chillax — the display face used for the wordmark and artist names. */
val ChillaxFontFamily = FontFamily(
    Font(R.font.chillax_light, FontWeight.Light),
    Font(R.font.chillax_regular, FontWeight.Normal),
    Font(R.font.chillax_medium, FontWeight.Medium),
    Font(R.font.chillax_semibold, FontWeight.SemiBold),
    Font(R.font.chillax_bold, FontWeight.Bold),
)

/**
 * Chillax while the display-font preference is on, otherwise null so the caller
 * falls through to whatever its text style already specifies. Returning null
 * rather than the app typeface keeps the off state exactly as it was.
 */
@Composable
fun rememberBrandFontFamily(): FontFamily? {
    val enabled by rememberPreference(BrandFontEnabledKey, defaultValue = true)
    return if (enabled) ChillaxFontFamily else null
}
