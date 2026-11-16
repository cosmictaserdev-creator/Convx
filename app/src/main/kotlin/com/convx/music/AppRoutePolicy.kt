package com.convx.music

import com.convx.music.ui.screens.Screens

/** Route-based rules for the app chrome surrounding each screen. */
internal object AppRoutePolicy {
    val tabRootRoutes = setOf(
        Screens.Home.route,
        Screens.Library.route,
        Screens.Radio.route,
        Screens.Explore.route,
        Screens.Settings.route,
        Screens.Songs.route,
    )

    private val webViewRoutes = setOf(
        "login",
        "switch_channel",
        "settings/spotify",
        "settings/discord/login",
    )
    private val routesWithoutNavigation = setOf(
        "login",
        "channel_picker",
        "equalizer",
        "wrapped",
        "update",
        "listen_together/chat",
    )
    private val routesWithoutPlayer = setOf(
        "wrapped",
        "update",
        "listen_together/chat",
        "login",
        "equalizer",
        "ambient_mode",
    )
    private val routesWithoutBottomSheetPlayer = setOf(
        "wrapped",
        "update",
        "listen_together/chat",
        "ambient_mode",
    )
    private val routesWithoutDownloadState = setOf(
        "login",
        "channel_picker",
        "equalizer",
        "wrapped",
        "update",
        Screens.ListenTogether.route,
        "listen_together/chat",
        "ambient_mode",
    )

    private val routesWithScreenTopHaze = setOf(
        Screens.Search.route,
        "account",
        "channel_picker",
        "recognition",
        "recognition_history",
        "switch_channel",
        "listen_together_from_topbar",
        Screens.ListenTogether.route,
        // These draw their own top blur and buttons; the shell's fallback haze sits above the
        // page and would blur those buttons.
        "stats",
        "history",
    )

    fun isTabRoot(route: String?) = route != null && route in tabRootRoutes
    fun isWebViewRoute(route: String?) = route != null && route in webViewRoutes
    fun showsNavigation(route: String?) = route == null || route !in routesWithoutNavigation
    fun showsPlayer(route: String?) = route == null || route !in routesWithoutPlayer
    fun showsBottomSheetPlayer(route: String?) = route == null || route !in routesWithoutBottomSheetPlayer
    fun needsDownloadState(route: String?) =
        route != null && route !in routesWithoutDownloadState && !route.startsWith("settings")

    fun showsFallbackTopHaze(route: String?) = route != null &&
        route !in tabRootRoutes &&
        route !in routesWithScreenTopHaze &&
        !route.startsWith("settings") &&
        !isWebViewRoute(route) &&
        !route.startsWith("album/") &&
        !route.startsWith("artist/") &&
        !route.startsWith("online_playlist/") &&
        !route.startsWith("local_playlist/") &&
        !route.startsWith("auto_playlist/") &&
        !route.startsWith("cache_playlist/") &&
        !route.startsWith("top_playlist/") &&
        !route.startsWith("browse/") &&
        !route.startsWith("search/")
}
