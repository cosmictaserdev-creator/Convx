/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.convxy.music.R

/**
 * A bottom-bar / side-bar destination.
 *
 * One icon per destination per UI style — deliberately not a selected/unselected
 * pair. iOS ships outline/filled pairs because its tab bar has no selection
 * indicator; this bar has both a glass puck and an accent content colour, so a
 * filled variant would be a third signal saying the same thing. Two artworks per
 * tab also have to be optically aligned against each other or the icon visibly
 * shifts on selection, which is exactly the cheap-looking wobble the pair would
 * have bought us.
 */
@Immutable
sealed class Screens(
    @StringRes val titleId: Int,
    val route: String,
    @DrawableRes val icon: Int,
    // SF Symbols-style lookalike used when the Apple Music UI style is active.
    // Defaults to the classic icon, so a destination only names this when it
    // actually has a distinct iOS drawing.
    @DrawableRes val iosIcon: Int = icon,
) {
    /** The icon to draw for the current UI style. */
    fun icon(appleMusicUi: Boolean): Int = if (appleMusicUi) iosIcon else icon

    object Home : Screens(
        titleId = R.string.home,
        route = "home",
        icon = R.drawable.accord_home,
    )

    object Search : Screens(
        titleId = R.string.search,
        route = "search_input",
        icon = R.drawable.search,
        iosIcon = R.drawable.cosmos_search,
    )

    /**
     * The native YouTube section: search/browse/watch pages rendered by Convxy
     * itself, backed by the same player pipeline as everything else.
     */
    object YouTube : Screens(
        titleId = R.string.youtube,
        route = "youtube_home",
        icon = R.drawable.ic_youtube,
    )

    object ListenTogether : Screens(
        titleId = R.string.together,
        route = "listen_together",
        icon = R.drawable.accord_groups,
    )

    /**
     * Flat, alphabetised list of every song on the device. Only ever a tab in local-only
     * mode -- with YouTube on, "all songs" has no bounded meaning.
     */
    object Songs : Screens(
        titleId = R.string.songs,
        route = "songs",
        icon = R.drawable.music_note,
    )

    object Library : Screens(
        titleId = R.string.filter_library,
        route = "library",
        icon = R.drawable.accord_library,
    )

    object Settings : Screens(
        titleId = R.string.settings,
        route = "settings",
        icon = R.drawable.settings,
    )

    companion object {
        // Listen Together is reachable from the top bar and its own route, never as a
        // tab: as a pager page it sat between Search and Library, so every tab switch
        // that crossed it flashed the screen on the way past.
        val MainScreens = listOf(Home, Search, YouTube, Library)

        /**
         * The bar's contents for the current mode. Local-only mode promotes [Songs] to a
         * tab, sitting after Home; Search is filtered out of the row by the floating bar
         * and drawn as the standalone circle on the right either way, so inserting here
         * puts Songs second in the visible row.
         */
        fun mainScreens(localOnly: Boolean): List<Screens> =
            if (localOnly) listOf(Home, Songs, Search, YouTube, Library) else MainScreens

        /** Every route that counts as a tab root, in either mode. */
        val MainRoutes: Set<String> = (MainScreens + Songs).map { it.route }.toSet()
    }
}
