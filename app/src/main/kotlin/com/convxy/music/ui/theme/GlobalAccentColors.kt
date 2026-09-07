/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.convxy.music.constants.AppTextColorKey
import com.convxy.music.utils.rememberPreference

/**
 * (container, content) colors for accent buttons/chips (Play, Shuffle, sort pills, ...)
 * that should follow the user's global button/text color (AppTextColorKey) instead of
 * MaterialTheme.colorScheme.primary/onPrimary when one is set. Content color is picked
 * for contrast against the container via [AppleTokens.onColor], same as the player's own
 * accent-on-color pairing, so a light custom color doesn't get light text on top of it.
 */
@Composable
fun rememberGlobalAccentColors(): Pair<Color, Color> {
    val (appTextColorInt) = rememberPreference(AppTextColorKey, defaultValue = 0)
    return if (appTextColorInt != 0) {
        val accent = Color(appTextColorInt)
        accent to AppleTokens.onColor(accent)
    } else {
        MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }
}
