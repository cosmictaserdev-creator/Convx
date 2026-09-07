/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.convxy.music.ui.screens.library.LibraryScreen
import com.convxy.music.ui.screens.settings.SettingsScreen

/**
 * Page order for [MainTabsPager] -- index into this list is the pager page index.
 *
 * Exactly the tab bar's own left-to-right order, so page index and tab index are the
 * same number and neither has to be mapped onto the other. Search is deliberately
 * absent: it is rendered as an overlay above this pager, not as a page. It used to
 * sit at index 1, wedged between Home and Library, even though the bar draws it as
 * the standalone circle on the far RIGHT (see FloatingNavBar's tabScreens filter) --
 * so every tab switch slid past a page the user could not see in the bar, and the
 * puck's index was off by one past Home. Moving it to the end instead would have
 * been worse: opening search from Home would then scroll the pager across two
 * intervening tabs, composing both on the way.
 */
val MainTabsScreens = listOf(
    Screens.Home,
    Screens.Library,
    Screens.Settings,
)

const val MainTabsRoute = "main_tabs"

/**
 * Animate to [page] without ever animating *across* an intervening page.
 *
 * [PagerState.animateScrollToPage] scrolls through everything between here and there,
 * and with `beyondViewportPageCount = 0` each page it passes over is composed from
 * scratch inside the animation's own frames. Home -> Settings therefore paid for
 * building the entire Library screen mid-slide, so how badly a tab switch stuttered
 * depended on which tab it happened to travel over rather than on where it was going.
 *
 * Landing one page short first makes the animated part always exactly one page: the
 * jump is invisible (it happens in a single frame, before any motion starts) and the
 * slide the user actually sees is unchanged.
 */
suspend fun PagerState.slideToPage(page: Int) {
    val from = currentPage
    if (page - from > 1 || from - page > 1) {
        scrollToPage(page + if (page > from) -1 else 1)
    }
    animateScrollToPage(page)
}

/**
 * Hosts Home/Library/Settings as pages of one [HorizontalPager] instead of
 * separate NavHost destinations. Switching between them becomes a
 * pager scroll -- no destination swap, no AnimatedContent transition, no backdrop
 * re-record -- while every screen still receives the exact same navController it
 * always did, so drilling into a detail screen (album, artist, a settings sub-page,
 * search results) from any of them is completely unchanged: it still goes through
 * the outer NavHost exactly as before.
 *
 * beyondViewportPageCount is deliberately left at its default (0) -- an earlier
 * version set it to keep all five pages composed at once, on the theory that the
 * tab bar's "remembers exactly where you left a tab" promise needed it. It didn't:
 * the OLD NavHost-based multi-back-stack never kept multiple tabs' compositions
 * alive either -- saveState/restoreState only preserves navigation-level state
 * (SavedStateHandle, ViewModelStore), while the actual composition is torn down on
 * every pop and rebuilt from rememberSaveable/ViewModel state on return, same as
 * this pager does at 0. Keeping all five alive instead meant Home's carousels,
 * Library's own internal 5-page sub-pager, and everything else were all composing,
 * recomposing, and (for any glass surface) re-recording their backdrop
 * simultaneously all the time regardless of which tab was visible -- measured as
 * severe, continuous lag, not just a slower switch.
 *
 * userScrollEnabled is off: this bar has always been tap-only, and several screens
 * already use horizontal swipe for their own gestures (hero carousels,
 * swipe-to-remove) that a drag-to-switch-tabs pager would fight with.
 * animateScrollToPage still plays the same smooth slide on a tab tap either way.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTabsPager(
    pagerState: PagerState,
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false,
    ) { page ->
        when (MainTabsScreens.getOrNull(page)) {
            Screens.Home -> HomeScreen(navController = navController, snackbarHostState = snackbarHostState)

            Screens.Library -> LibraryScreen(navController)

            Screens.ListenTogether -> ListenTogetherScreen(navController, showTopBar = false)

            Screens.Settings -> SettingsScreen(navController, scrollBehavior)

            else -> Unit
        }
    }
}
