/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

/**
 * Lets a horizontally scrolling row escape its parent's start padding and run
 * under the floating side bar.
 *
 * The vertical list keeps its start content padding, so rows stay clear of the
 * panel. A carousel inside that list would inherit the same offset and stop dead
 * at the panel's edge — nothing is ever drawn beneath it, so the glass has
 * nothing to blur and the cards look cut off rather than tucked under.
 *
 * This measures the child [inset] wider than the slot and places it that far to
 * the left, while still reporting the slot's original width so the list's own
 * layout is untouched. Pair it with [plusStart] on the carousel's own
 * contentPadding so its FIRST card still starts clear of the panel and only the
 * ones you scroll past travel underneath.
 *
 * Start-edge only: the offset is applied on the physical left, matching the side
 * bar, which is itself pinned to the left rather than to the layout direction.
 */
fun Modifier.bleedStart(inset: Dp): Modifier = layout { measurable, constraints ->
    val extra = inset.roundToPx()
    val widened = if (constraints.hasBoundedWidth) {
        constraints.copy(
            minWidth = constraints.minWidth + extra,
            maxWidth = constraints.maxWidth + extra,
        )
    } else {
        constraints
    }
    val placeable = measurable.measure(widened)
    // Report the ORIGINAL width: the parent must not learn it got wider, or the
    // list starts scrolling horizontally.
    layout((placeable.width - extra).coerceAtLeast(0), placeable.height) {
        placeable.place(-extra, 0)
    }
}

/** [PaddingValues] with [extra] added to the start edge. */
@Composable
fun PaddingValues.plusStart(extra: Dp): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) + extra,
        top = calculateTopPadding(),
        end = calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding(),
    )
}
