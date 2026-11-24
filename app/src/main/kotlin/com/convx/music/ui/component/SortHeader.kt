/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Sort control for a list header over any enum of sort fields. It is the menu-style
 * [SortPopupButton]; the old Material split-button pill no longer exists anywhere.
 */
@Composable
inline fun <reified T : Enum<T>> SortHeader(
    sortType: T,
    sortDescending: Boolean,
    crossinline onSortTypeChange: (T) -> Unit,
    crossinline onSortDescendingChange: (Boolean) -> Unit,
    crossinline sortTypeText: (T) -> Int,
    modifier: Modifier = Modifier,
    showDescending: Boolean? = true,
    supportsDescending: (T) -> Boolean = { true },
) {
    val options = enumValues<T>().map { SortOption(it, sortTypeText(it)) }
    SortPopupButton(
        options = options,
        selected = sortType,
        descending = sortDescending,
        onSelectedChange = { onSortTypeChange(it) },
        onDescendingChange = { onSortDescendingChange(it) },
        modifier = modifier,
        showDescending = showDescending == true && supportsDescending(sortType),
    )
}
