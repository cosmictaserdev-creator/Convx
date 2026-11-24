/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import com.convx.music.utils.openSearchResults

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.convx.music.constants.SearchSource
import com.convx.music.constants.SearchSourceKey
import com.convx.music.search.SearchHistoryCommands
import com.convx.music.search.SearchHistorySettingsRepository
import com.convx.music.search.shouldRecordSearchQuery
import com.convx.music.ui.component.NavSearchState
import com.convx.music.ui.screens.Screens
import com.convx.music.utils.rememberEnumPreference
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/** The nav bar's search field state and the enter/exit/submit handlers around it. */
@Stable
class AppSearchController(
    val query: MutableState<TextFieldValue>,
    val keyboardActive: MutableState<Boolean>,
    val storedSource: MutableState<SearchSource>,
    val visualOverride: MutableState<Boolean?>,
    val focusRequester: FocusRequester,
    val searchSource: SearchSource,
    val canToggleSource: Boolean,
    val onSearch: (String) -> Unit,
    val enterSearch: () -> Unit,
    val exitSearch: () -> Unit,
) {
    fun navSearchState(
        inSearchScreen: Boolean,
        inSearchInputScreen: Boolean,
        navController: NavHostController,
    ): NavSearchState = NavSearchState(
        visualActive = visualOverride.value ?: inSearchScreen,
        keyboardActive = keyboardActive.value,
        query = query.value,
        onQueryChange = { query.value = it },
        onSubmit = onSearch,
        searchSource = searchSource,
        onToggleSource = {
            storedSource.value =
                if (storedSource.value == SearchSource.ONLINE) SearchSource.LOCAL else SearchSource.ONLINE
        },
        canToggleSource = canToggleSource,
        onTapSearchIcon = enterSearch,
        onTapBar = {
            if (inSearchScreen && !inSearchInputScreen) {
                // Return to the search input before reopening the keyboard from results.
                navController.popBackStack(Screens.Search.route, inclusive = false)
            }
            keyboardActive.value = true
        },
        onExit = exitSearch,
        onCloseKeyboard = { keyboardActive.value = false },
        focusRequester = focusRequester,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun rememberAppSearchController(
    navController: NavHostController,
    localOnlyMode: Boolean,
    ioScope: CoroutineScope,
): AppSearchController {
    val searchHistoryCommands: SearchHistoryCommands = koinInject()
    val searchHistorySettingsRepository: SearchHistorySettingsRepository = koinInject()
    val searchHistorySettingsFlow = remember(searchHistorySettingsRepository) {
        searchHistorySettingsRepository.observe()
    }
    val searchHistorySettings by searchHistorySettingsFlow
        .collectAsStateWithLifecycle(initialValue = null)
    val coroutineScope = rememberCoroutineScope()
    val queryState = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    // Whether the nav bar's search field currently owns the keyboard — the
    // one thing that isn't route-derived (search_input/search/{query} don't
    // change across the first-tap/second-tap boundary). Reset below whenever
    // navigation leaves both search routes.
    val keyboardActive = rememberSaveable { mutableStateOf(false) }
    var searchKeyboardActive by keyboardActive
    val sourceState = rememberEnumPreference(SearchSourceKey, SearchSource.ONLINE)
    var storedSearchSource by sourceState
    // Keep the online preference so it is restored when local-only mode ends.
    val searchSource = if (localOnlyMode) SearchSource.LOCAL else storedSearchSource
    val searchFocusRequester = remember { FocusRequester() }
    // Hold the target visual state until navigation completes its transition.
    val visualOverride = remember { mutableStateOf<Boolean?>(null) }
    var searchVisualOverride by visualOverride

    val onSearch: (String) -> Unit = remember(
        localOnlyMode,
        navController,
        ioScope,
        searchHistoryCommands,
        searchHistorySettings,
    ) {
        { searchQuery ->
            if (searchQuery.isNotEmpty()) {
                // Local results are already shown live, so submitting only saves history.
                if (!localOnlyMode) navController.openSearchResults(searchQuery) {
                    // launchSingleTop can drop a new query because args share one destination ID.
                    popUpTo("search/{query}") { inclusive = true }
                }

                if (shouldRecordSearchQuery(searchQuery, searchHistorySettings)) {
                    ioScope.launch(Dispatchers.IO) {
                        searchHistoryCommands.record(searchQuery)
                    }
                }
            }
        }
    }

    // Let the nav bar animation finish before navigating to avoid overlapping transitions.
    val enterSearch: () -> Unit = remember(navController, coroutineScope) {
        {
            searchVisualOverride = true
            coroutineScope.launch {
                delay(SearchNavTransitionDelayMs)
                // Search starts from the tab root, even when opened from a detail screen.
                if (!AppRoutePolicy.isTabRoot(navController.currentDestination?.route)) {
                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                }
                navController.navigate(Screens.Search.route) { launchSingleTop = true }
                searchVisualOverride = null
            }
        }
    }
    val exitSearch: () -> Unit = remember(navController, coroutineScope) {
        {
            searchKeyboardActive = false
            searchVisualOverride = false
            coroutineScope.launch {
                delay(SearchNavTransitionDelayMs)
                // Remove the input and results destinations so cancel returns to the prior screen.
                if (!navController.popBackStack(Screens.Search.route, inclusive = true)) {
                    navController.navigateUp()
                }
                searchVisualOverride = null
            }
        }
    }
    return remember(queryState, keyboardActive, sourceState, visualOverride, searchFocusRequester, searchSource, localOnlyMode, onSearch, enterSearch, exitSearch) {
        AppSearchController(
            query = queryState,
            keyboardActive = keyboardActive,
            storedSource = sourceState,
            visualOverride = visualOverride,
            focusRequester = searchFocusRequester,
            searchSource = searchSource,
            canToggleSource = !localOnlyMode,
            onSearch = onSearch,
            enterSearch = enterSearch,
            exitSearch = exitSearch,
        )
    }
}

/** Delay navigation until the nav bar's search animation finishes. */
internal const val SearchNavTransitionDelayMs = 360L
