/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibilityScope
import com.convx.music.ui.utils.Motion
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.convx.music.constants.DarkModeKey
import com.convx.music.constants.PureBlackKey
import com.convx.music.ui.screens.settings.DarkMode
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.ui.utils.LocalNavAnimatedVisibilityScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.graphicsLayer
import com.convx.music.ui.utils.LocalMorphArtworkId
import com.convx.music.ui.utils.isMorphRoute
import com.convx.music.ui.utils.morphArtworkId
import com.convx.music.ui.screens.settings.HomeFeedOrderScreen
import com.convx.music.ui.screens.artist.ArtistAlbumsScreen
import com.convx.music.ui.screens.artist.ArtistItemsScreen
import com.convx.music.ui.screens.artist.ArtistScreen
import com.convx.music.ui.screens.artist.ArtistSongsScreen
import com.convx.music.ui.screens.equalizer.EqScreen
import com.convx.music.ui.screens.library.AiDjSetScreen
import com.convx.music.ui.screens.library.LocalFolderScreen
import com.convx.music.ui.screens.library.LocalMusicScreen
import com.convx.music.ui.screens.library.LibraryScreen
import com.convx.music.ui.screens.library.LocalSongsScreen
import com.convx.music.ui.screens.settings.SettingsScreen
import com.convx.music.ui.screens.playlist.AutoPlaylistScreen
import com.convx.music.ui.screens.playlist.CachePlaylistScreen
import com.convx.music.ui.screens.playlist.LocalPlaylistScreen
import com.convx.music.ui.screens.playlist.OnlinePlaylistScreen
import com.convx.music.ui.screens.playlist.TopPlaylistScreen
import com.convx.music.ui.screens.search.OnlineSearchResult
import com.convx.music.ui.screens.search.SearchScreen
import com.convx.music.ui.screens.settings.AboutScreen
import com.convx.music.ui.screens.settings.AppearanceSettings
import com.convx.music.ui.screens.settings.CanvasSelection
import com.convx.music.ui.screens.settings.AppIconScreen
import com.convx.music.ui.screens.settings.diy.DiyEditorScreen
import com.convx.music.ui.screens.settings.diy.PlayerIconsScreen
import com.convx.music.ui.screens.settings.diy.PresetsScreen
import com.convx.music.ui.screens.settings.FontSelectionScreen
import com.convx.music.ui.screens.settings.GlassEffectSettings
import com.convx.music.ui.screens.settings.PlayerThemeScreen
import com.convx.music.ui.screens.settings.BackupAndRestore
import com.convx.music.ui.screens.settings.CrashLogsSettingsScreen
import com.convx.music.ui.screens.settings.SpotifyScreen
import com.convx.music.viewmodels.SpotifyImportViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.convx.music.ui.screens.settings.ContentSettings
import com.convx.music.ui.screens.settings.DiscordLoginScreen
import com.convx.music.ui.screens.settings.PlayerSettings
import com.convx.music.ui.screens.settings.PlayerAdvancedSettings
import com.convx.music.ui.screens.settings.AppearanceAdvancedSettings
import com.convx.music.ui.screens.settings.ContentAdvancedSettings
import com.convx.music.ui.screens.settings.JioSettings
import com.convx.music.ui.screens.settings.LocalFoldersSettingsScreen
import com.convx.music.ui.screens.settings.ScanMusicScreen
import com.convx.music.ui.screens.settings.SongTransitionsScreen
import com.convx.music.ui.screens.settings.PrivacySettings
import com.convx.music.ui.screens.settings.RomanizationSettings
import com.convx.music.ui.screens.settings.AccountSettingsScreen
import com.convx.music.ui.screens.settings.StorageSettings
import com.convx.music.ui.screens.settings.ThemeScreen
import com.convx.music.ui.screens.settings.AiSettings
import com.convx.music.ui.screens.settings.integrations.DiscordSettings
import com.convx.music.ui.screens.settings.integrations.IntegrationScreen
import com.convx.music.ui.screens.settings.integrations.LastFMSettings
import com.convx.music.ui.screens.settings.integrations.ListenTogetherSettings
import com.convx.music.ui.screens.ambient.AmbientModeScreen
import com.convx.music.ui.screens.settings.AmbientModeSettings
import com.convx.music.ui.screens.recognition.RecognitionScreen
import com.convx.music.ui.screens.recognition.RecognitionHistoryScreen
import com.convx.music.ui.screens.settings.ModuleSourceScreen
import com.convx.music.ui.screens.settings.ModuleDetailScreen
import com.convx.music.ui.screens.settings.UpdateSettings
import com.convx.music.ui.screens.wrapped.WrappedScreen
import com.convx.music.core.updater.UpdateScreen
import com.convx.music.core.changelog.ChangelogScreen
import com.convx.music.core.commitscreen.CommitScreen
import com.convx.music.ui.screens.equalizer.axion.AxionEqScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState,
    accountImageUrl: String? = null,
    accountName: String = "",
    onAccountClick: () -> Unit = { navController.navigate(Screens.Settings.route) },
) {
    sharedComposable(Screens.Home.route) {
        HomeRoute(navController = navController, snackbarHostState = snackbarHostState)
    }

    composable(Screens.Search.route) {
        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }
        SearchScreen(
            navController = navController,
            pureBlack = pureBlack,
            accountImageUrl = accountImageUrl,
            accountName = accountName,
            onAccountClick = onAccountClick,
        )
    }

    // Only ever reachable as a tab while local-only mode is on (see Screens.mainScreens),
    // but the route is always registered: a saved back stack entry from a session that
    // had the mode on must still resolve after it is turned off.
    sharedComposable(Screens.Songs.route) {
        LocalSongsScreen(navController)
    }

    sharedComposable(Screens.Library.route) {
        LibraryScreen(navController)
    }

    sharedComposable(Screens.Explore.route) {
        ExploreScreen(navController = navController)
    }

    sharedComposable(Screens.Radio.route) {
        RadioScreen(navController = navController)
    }

    composable(
        route = "radio_station/{seedId}?title={title}&artist={artist}&thumbnail={thumbnail}",
        arguments = listOf(
            navArgument("seedId") { type = NavType.StringType },
            navArgument("title") { type = NavType.StringType; nullable = true; defaultValue = null },
            navArgument("artist") { type = NavType.StringType; nullable = true; defaultValue = null },
            navArgument("thumbnail") { type = NavType.StringType; nullable = true; defaultValue = null },
        ),
    ) { entry ->
        RadioStationScreen(
            navController = navController,
            seedId = entry.arguments?.getString("seedId").orEmpty(),
            seedTitle = entry.arguments?.getString("title").orEmpty(),
            seedArtist = entry.arguments?.getString("artist").orEmpty(),
            seedThumbnail = entry.arguments?.getString("thumbnail").orEmpty(),
        )
    }

    sharedComposable(Screens.Settings.route) {
        SettingsScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "listen_together_from_topbar",
    ) {
        com.convx.music.ui.screens.JamScreen(navController)
    }

    sharedComposable("listen_together/chat") {
        CommentTogetherScreen(navController)
    }

    sharedComposable("history") {
        HistoryScreen(navController)
    }

    sharedComposable("stats") {
        StatsScreen(navController)
    }

    sharedComposable("mood_and_genres") {
        MoodAndGenresScreen(navController, scrollBehavior)
    }

    sharedComposable("account") {
        AccountScreen(navController, scrollBehavior)
    }

    sharedComposable("new_release") {
        NewReleaseScreen(navController, scrollBehavior)
    }

    sharedComposable("charts_screen") {
        ChartsScreen(navController)
    }

    sharedComposable(
        route = "browse/{browseId}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
            }
        )
    ) {
        BrowseScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("browseId")
        )
    }

    composable(
        route = "search/{query}",
        arguments = listOf(
            navArgument("query") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            fadeIn(Motion.push()) + slideInHorizontally(Motion.push()) { it / 3 }
        },
        exitTransition = {
            if (targetState.destination.route?.startsWith("search/") == true) {
                fadeOut(Motion.push())
            } else {
                fadeOut(Motion.push(), targetAlpha = Motion.PushDimAlpha) + slideOutHorizontally(Motion.push()) { Motion.parallaxOffset(it) }
            }
        },
        popEnterTransition = {
            if (initialState.destination.route?.startsWith("search/") == true) {
                fadeIn(Motion.push())
            } else {
                fadeIn(Motion.push()) + slideInHorizontally(Motion.push()) { Motion.parallaxOffset(-it) }
            }
        },
        popExitTransition = {
            fadeOut(Motion.appearExit())
        },
    ) {
        com.convx.music.ui.theme.ForcedDarkScope { OnlineSearchResult(navController) }
    }

    sharedComposable(
        route = "album/{albumId}?thumb={thumb}&morphId={morphId}",
        arguments = listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
            navArgument("thumb") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("morphId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "artist/{artistId}?thumb={thumb}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("thumb") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        ArtistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "artist/{artistId}/songs",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "artist/{artistId}/albums",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            }
        )
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "artist/{artistId}/items?browseId={browseId}&params={params}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "online_playlist/{playlistId}?title={title}&thumbnail={thumbnail}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
            navArgument("title") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("thumbnail") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "local_playlist/{playlistId}?thumb={thumb}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
            navArgument("thumb") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "local_music",
    ) {
        LocalMusicScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "local_folder/{path}",
        arguments = listOf(
            navArgument("path") {
                type = NavType.StringType
            },
        ),
    ) {
        LocalFolderScreen(navController, it.arguments?.getString("path").orEmpty())
    }

    sharedComposable("local_folders") {
        com.convx.music.ui.screens.library.LocalFoldersScreen(navController)
    }

    sharedComposable(
        route = "auto_playlist/{playlist}?thumb={thumb}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
            navArgument("thumb") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "cache_playlist/{playlist}?thumb={thumb}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
            navArgument("thumb") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "top_playlist/{top}",
        arguments = listOf(
            navArgument("top") {
                type = NavType.StringType
            },
        ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        YouTubeBrowseScreen(navController)
    }

    sharedComposable("settings/update") {
       UpdateSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/account") {
        AccountSettingsScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance") {
        AppearanceSettings(navController, scrollBehavior, activity, snackbarHostState)
    }

    sharedComposable("settings/appearance/advanced") {
        AppearanceAdvancedSettings(navController, scrollBehavior, activity)
    }

    sharedComposable("settings/home_feed_order") {
        HomeFeedOrderScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/theme") {
        ThemeScreen(navController)
    }

    sharedComposable("settings/appearance/font") {
        FontSelectionScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/appicon") {
        AppIconScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/presets") {
        PresetsScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/playericons") {
        PlayerIconsScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/diy") {
        DiyEditorScreen(navController)
    }

    sharedComposable("settings/appearance/canvas") {
        CanvasSelection(navController, scrollBehavior)
    }

    sharedComposable("settings/ambient") {
        AmbientModeSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/playertheme") {
        PlayerThemeScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/liquidglass") {
        GlassEffectSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/liquidglass/advanced") {
        GlassEffectSettings(navController, scrollBehavior, advanced = true)
    }

    sharedComposable("settings/content") {
        ContentSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/content/advanced") {
        ContentAdvancedSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/lyrics") {
        com.convx.music.ui.screens.settings.LyricsSettingsScreen(navController)
    }

    com.convx.music.ui.screens.settings.SettingsHubs.all.forEach { hub ->
        sharedComposable(hub.route) {
            com.convx.music.ui.screens.settings.SettingsHubScreen(navController, hub)
        }
    }

    sharedComposable("settings/network") {
        ContentAdvancedSettings(navController, scrollBehavior, networkOnly = true)
    }

    sharedComposable("settings/content/romanization") {
        RomanizationSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/ai") {
        AiSettings(navController, scrollBehavior)
    }
    
    sharedComposable("settings/player") {
        PlayerSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/player/advanced") {
        PlayerAdvancedSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/player/jio") {
        JioSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/scan_music") {
        ScanMusicScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/song_transitions") {
        SongTransitionsScreen(navController)
    }

    sharedComposable("settings/player/local_folders") {
        LocalFoldersSettingsScreen(navController, scrollBehavior)
    }

    sharedComposable("listen_together/party") {
        com.convx.music.ui.screens.JamScreen(navController)
    }

    sharedComposable(
        route = "network_browse/{serverId}",
        arguments = listOf(navArgument("serverId") { type = NavType.StringType }),
    ) {
        com.convx.music.ui.screens.library.NetworkBrowserScreen(navController, it.arguments?.getString("serverId").orEmpty())
    }

    sharedComposable("settings/network_storage") {
        com.convx.music.ui.screens.settings.NetworkStorageSettingsScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/storage") {
        StorageSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/equalizer") {
        AxionEqScreen(onBackClick = { navController.navigateUp() })
    }
    sharedComposable(
        route = "song_insight/{songId}",
        arguments = listOf(navArgument("songId") { type = NavType.StringType }),
    ) {
        com.convx.music.ui.screens.library.SongInsightScreen(navController)
    }

    sharedComposable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }

    sharedComposable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }

    sharedComposable("settings/help/crash_logs") {
        CrashLogsSettingsScreen(navController)
    }

    sharedComposable("settings/spotify") {
        SpotifyScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "ai_dj_set?seedSongId={seedSongId}&concert={concert}",
        arguments = listOf(
            navArgument("seedSongId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("concert") {
                type = NavType.BoolType
                defaultValue = false
            },
        ),
    ) {
        AiDjSetScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("seedSongId"),
            it.arguments?.getBoolean("concert") ?: false,
        )
    }



    sharedComposable("settings/integrations") {
        IntegrationScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/modules") {
        ModuleSourceScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "settings/modules/{moduleId}",
        arguments = listOf(
            navArgument("moduleId") {
                type = NavType.StringType
            },
        ),
    ) {
        ModuleDetailScreen(navController, scrollBehavior, it.arguments?.getString("moduleId") ?: "")
    }

    sharedComposable("settings/integrations/discord") {
        DiscordSettings(navController, scrollBehavior, snackbarHostState)
    }

    sharedComposable("settings/integrations/lastfm") {
        LastFMSettings(navController, scrollBehavior)
    }

    sharedComposable(route = "settings/integrations/listen_together") {
        ListenTogetherSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/discord/login") {
        DiscordLoginScreen(navController)
    }

    sharedComposable("settings/about") {
        AboutScreen(navController, scrollBehavior)
    }

    sharedComposable("update") {
        UpdateScreen(navController)
    }

    sharedComposable("login") {
        LoginScreen(navController)
    }

    sharedComposable("channel_picker") {
        ChannelPickerScreen(navController)
    }

    sharedComposable("switch_channel") {
        SwitchChannelScreen(navController)
    }

    sharedComposable("wrapped") {
        WrappedScreen(navController)
    }

    sharedComposable("ambient_mode") {
        AmbientModeScreen(navController)
    }

    dialog("equalizer") {
        EqScreen(navController = navController)
    }

    sharedComposable("recognition") {
        RecognitionScreen(navController)
    }

    sharedComposable("recognition_history") {
        RecognitionHistoryScreen(navController)
    }
    sharedComposable("settings/changelog") {
        ChangelogScreen(navController,scrollBehavior)
    }
    sharedComposable("settings/commits") {
        CommitScreen(navController, scrollBehavior)
    }
}

/**
 * A destination that may open from a tile: reference-style "one shot" transition.
 *
 * Detail routes (album, playlist, artist...) slide in from the right while the tapped
 * tile's cover flies into the page's hero (`Modifier.sharedArtworkTarget`), and on pop
 * the page slides a quarter-width back out and fades while the cover flies home. The
 * screen behind holds still and dims, so the tile the cover returns to never moves.
 *
 * Non-detail destinations (tabs, settings...) keep the plain cross-fade: a slide on a
 * tab switch would read as drilling in when it is not.
 *
 * Publishes the destination's [AnimatedVisibilityScope] and its artwork id so the hero
 * can claim its half of the shared element without any per-screen plumbing.
 */
private fun NavGraphBuilder.sharedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
): Unit {
    val detail = isMorphRoute(route)
    val collection = route.startsWith("album/") || route.contains("playlist/")
    val darkRoute = com.convx.music.ui.theme.isForcedDarkRoute(route)
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            if (collection) {
                if (com.convx.music.ui.utils.isLibraryArtworkMorph()) fadeIn(tween(PageEnterMillis))
                else slideInHorizontally(Motion.collectionPush()) { it }
            } else if (detail) {
                slideInHorizontally(tween(PageEnterMillis, easing = FastOutSlowInEasing)) { it } +
                    fadeIn(tween(PageEnterMillis, easing = FastOutSlowInEasing))
            } else {
                fadeIn(tween(Motion.PushMillis))
            }
        },
        exitTransition = {
            val nextCollection = targetState.destination.route?.let { it.startsWith("album/") || it.contains("playlist/") } == true
            if (nextCollection) fadeOut(tween(PageExitMillis), targetAlpha = if (com.convx.music.ui.utils.isLibraryArtworkMorph()) 0f else .999f)
            else fadeOut(tween(PageExitMillis), targetAlpha = 0.7f)
        },
        popEnterTransition = {
            val leavingCollection = initialState.destination.route?.let { it.startsWith("album/") || it.contains("playlist/") } == true
            if (leavingCollection) fadeIn(tween(PageEnterMillis), initialAlpha = if (com.convx.music.ui.utils.isLibraryArtworkMorph()) 0f else .999f)
            else fadeIn(tween(PageEnterMillis), initialAlpha = 0.7f)
        },
        popExitTransition = {
            if (collection) {
                if (com.convx.music.ui.utils.isLibraryArtworkMorph()) fadeOut(tween(PageExitMillis))
                else slideOutHorizontally(Motion.collectionPush()) { it }
            } else if (detail) {
                // Tween, not spring: predictive back scrubs this by gesture progress.
                slideOutHorizontally(tween(PageExitMillis, easing = FastOutSlowInEasing)) { it / 4 } +
                    fadeOut(tween(PageExitMillis, easing = FastOutSlowInEasing))
            } else {
                fadeOut(Motion.appearExit())
            }
        },
    ) { entry ->
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this,
            LocalMorphArtworkId provides entry.morphArtworkId(),
        ) {
            if (collection) {
                val corner = com.convx.music.ui.player.rememberScreenCornerRadius()
                val shape = remember(corner) { com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(corner) }
                val pageTransition = transition
                androidx.compose.foundation.layout.Box(
                    Modifier.fillMaxSize().graphicsLayer {
                        clip = pageTransition.currentState != pageTransition.targetState
                        this.shape = shape
                    },
                ) { com.convx.music.ui.theme.ForcedDarkScope(darkRoute) { content(entry) } }
            } else com.convx.music.ui.theme.ForcedDarkScope(darkRoute) { content(entry) }
        }
    }
}

private const val PageEnterMillis = 400
private const val PageExitMillis = 400
