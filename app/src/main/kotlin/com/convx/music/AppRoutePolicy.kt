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
        "mood_and_genres",
        "new_release",
        "charts_screen",
        "local_folders",
        "local_music",
        "history",
        "stats",
        "listen_together/party",
        "listen_together/chat",
        "ai_dj_set",
        "update",
        "wrapped",
        "ambient_mode",
    )
    private val routesWithScreenTopHazePrefixes = setOf(
        "radio_station/",
        "youtube_browse/",
        "local_folder/",
        "network_browse/",
        "song_insight/",
    )

    fun isTabRoot(route: String?) = route != null && route in tabRootRoutes
    fun isWebViewRoute(route: String?) = route != null && route in webViewRoutes
    fun showsNavigation(route: String?) = route == null || route !in routesWithoutNavigation
    fun showsPlayer(route: String?) = route == null || route !in routesWithoutPlayer
    fun showsBottomSheetPlayer(route: String?) = route == null || route !in routesWithoutBottomSheetPlayer
    fun needsDownloadState(route: String?) =
        route != null && route !in routesWithoutDownloadState && !route.startsWith("settings")

    fun showsFallbackTopHaze(route: String?): Boolean {
        val path = route?.substringBefore('?') ?: return false
        return path !in tabRootRoutes &&
            path !in routesWithScreenTopHaze &&
            routesWithScreenTopHazePrefixes.none { path.startsWith(it) } &&
            !path.startsWith("settings") &&
            !isWebViewRoute(path) &&
            !path.startsWith("album/") &&
            !path.startsWith("artist/") &&
            !path.startsWith("online_playlist/") &&
            !path.startsWith("local_playlist/") &&
            !path.startsWith("auto_playlist/") &&
            !path.startsWith("cache_playlist/") &&
            !path.startsWith("top_playlist/") &&
            !path.startsWith("browse/") &&
            !path.startsWith("search/")
    }
}
