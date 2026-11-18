/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.runtime.Immutable

/** The costly visual effects as the listener chose them; [effective] applies Performance mode. */
@Immutable
data class VisualEffects(
    val reduceDynamicBlur: Boolean = false,
    val liquidGlass: Boolean = true,
    val albumCanvas: Boolean = false,
    val artistBackgroundVideo: Boolean = true,
    val homeVideoBackground: Boolean = true,
) {
    /** Performance mode draws lighter and changes nothing else; off returns this instance unchanged. */
    fun effective(performanceMode: Boolean): VisualEffects =
        if (!performanceMode) {
            this
        } else {
            copy(
                reduceDynamicBlur = true,
                liquidGlass = false,
                albumCanvas = false,
                artistBackgroundVideo = false,
                homeVideoBackground = false,
            )
        }
}
