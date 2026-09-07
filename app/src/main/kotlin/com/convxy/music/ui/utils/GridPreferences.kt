/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convxy.music.constants.GridColumnMinWidth
import com.convxy.music.constants.GridColumnsOverrideKey
import com.convxy.music.constants.GridSpacingKey
import com.convxy.music.utils.rememberPreference

/** Discrete steps a user can pick for [GridColumnsOverrideKey] / [SpeedDialColumnsOverrideKey]. 0 = auto. */
val GridColumnChoices = listOf(0, 2, 3, 4, 5, 6)

/** Discrete dp steps a user can pick for [GridSpacingKey]. */
val GridSpacingChoices = listOf(4, 8, 12, 16, 20, 24, 32)

/** Discrete dp steps for [GridCardHeightOverrideKey]. 0 = fall back to the Big/Small toggle. */
val GridCardHeightChoices = listOf(0, 96, 112, 128, 144, 164, 184, 200, 220)

/** Discrete dp steps for HomeHeroCardHeightOverrideKey. 0 = the card's normal 4:3 aspect ratio. */
val HomeHeroCardHeightChoices = listOf(0, 200, 240, 280, 320, 360, 400, 440, 480)

/** Discrete dp steps for HomeCardCornerRadiusOverrideKey. 0 = each card's own default (12dp tiles, 28dp hero). */
val HomeCardCornerRadiusChoices = listOf(0, 8, 12, 16, 20, 24, 28, 36)

/**
 * Column spec for every vertical grid screen (library/browse/search/artist item
 * grids). Auto (0) keeps today's behavior — [GridCells.Adaptive] sized off
 * [GridColumnMinWidth] — a fixed override forces the same column count on every
 * screen width instead.
 */
@Composable
fun rememberGridColumns(): GridCells {
    val (override) = rememberPreference(GridColumnsOverrideKey, 0)
    return if (override > 0) GridCells.Fixed(override) else GridCells.Adaptive(minSize = GridColumnMinWidth)
}

/** Spacing between grid tiles — see GridItem in Items.kt, the single place this applies. */
@Composable
fun rememberGridSpacing(): Dp {
    val (spacingDp) = rememberPreference(GridSpacingKey, 16)
    return spacingDp.dp
}
