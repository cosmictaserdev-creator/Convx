/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convx.music.constants.IosOverscrollKey
import com.convx.music.utils.rememberPreference

@Composable
fun rememberHeroZoom(maxPull: Dp = 220.dp): HeroZoom {
    val enabled by rememberPreference(IosOverscrollKey, defaultValue = true)
    return rememberHeroZoom(enabled = enabled, maxPull = maxPull)
}