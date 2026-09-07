/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Player slider color configuration for consistent styling across all slider types
 *
 * This object provides standardized color schemes for Default, Squiggly, and Slim sliders
 * used in the music player interface, ensuring visual consistency and proper contrast.
 */
object PlayerSliderColors {

    /**
     * Standard slider colors for all slider types. The unloaded/inactive track is always a
     * faded version of [activeColor] itself — not a separate background-dependent color — so
     * the two halves of the seek bar read as one color at two strengths, whatever [activeColor]
     * is (including the user's global text/button color when they've set one).
     *
     * @param activeColor Color for active (loaded) track, ticks, and thumb
     * @return SliderColors configuration
     */
    @Composable
    fun getSliderColors(activeColor: Color): SliderColors {
        val inactiveTrackColor = activeColor.copy(alpha = 0.3f)

        return SliderDefaults.colors(
            activeTrackColor = activeColor,
            activeTickColor = activeColor,
            thumbColor = activeColor,
            inactiveTrackColor = inactiveTrackColor,
            disabledActiveTrackColor = activeColor,
            disabledInactiveTrackColor = inactiveTrackColor,
            disabledThumbColor = activeColor
        )
    }
}

