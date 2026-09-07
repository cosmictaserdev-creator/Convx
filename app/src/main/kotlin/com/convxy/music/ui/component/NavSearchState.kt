/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import com.convxy.music.constants.SearchSource

/**
 * Search input state shared between [com.convxy.music.ui.component.AppFloatingNavBar]
 * (which now owns the actual search text field) and the search_input/search/{query}
 * screens (which just read the query to filter/display results). Hoisted to
 * MainActivity, provided via [LocalNavSearchState] — the nav bar renders outside
 * both screens in the composition tree and must show live text before/regardless
 * of which one is mounted.
 */
@Immutable
data class NavSearchState(
    // Whether the nav bar should render its search-mode chrome. Owned by
    // MainActivity rather than derived purely from the current route inside
    // the nav bar, so entering/exiting search can hold this true/false for a
    // beat before the actual navigation lands — the shrink/expand animation
    // plays out first, then the route changes.
    val visualActive: Boolean = false,
    val keyboardActive: Boolean = false,
    val query: TextFieldValue = TextFieldValue(),
    val onQueryChange: (TextFieldValue) -> Unit = {},
    val onSubmit: (String) -> Unit = {},
    val searchSource: SearchSource = SearchSource.ONLINE,
    val onToggleSource: () -> Unit = {},
    // False in local-only mode: the source is pinned to LOCAL, so the toggle
    // would be a button that changes nothing.
    val canToggleSource: Boolean = true,
    // Tap the search circle in normal mode -> plays the shrink-to-pill
    // animation, then navigates to search_input once it's done.
    val onTapSearchIcon: () -> Unit = {},
    // Tap on the pre-keyboard search bar (search-expanded/search-inline) -> request focus.
    val onTapBar: () -> Unit = {},
    // Back arrow / shrunk-icon / system back (any exit path) -> plays the
    // expand-back animation, then navigateUp once it's done.
    val onExit: () -> Unit = {},
    // System back gesture while the keyboard is open -> just close the keyboard,
    // drop back to search-expanded, without navigating away.
    val onCloseKeyboard: () -> Unit = {},
    val focusRequester: FocusRequester = FocusRequester(),
)

val LocalNavSearchState = staticCompositionLocalOf { NavSearchState() }
