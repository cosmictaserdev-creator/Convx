/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.nexamusic.app.ui.utils.Motion
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.nexamusic.app.ui.utils.LocalNavAnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import com.nexamusic.app.ui.utils.morphContainer
import com.nexamusic.app.ui.utils.hasMorphSource
import com.nexamusic.app.ui.utils.morphArtworkId
import com.nexamusic.app.ui.screens.settings.HomeFeedOrderScreen
import com.nexamusic.app.ui.screens.artist.ArtistAlbumsScreen
import com.nexamusic.app.ui.screens.artist.ArtistItemsScreen
import com.nexamusic.app.ui.screens.artist.ArtistScreen
import com.nexamusic.app.ui.screens.artist.ArtistSongsScreen
import com.nexamusic.app.ui.screens.equalizer.EqScreen
import com.nexamusic.app.ui.screens.library.LocalFolderScreen
import com.nexamusic.app.ui.screens.library.LocalMusicScreen
import com.nexamusic.app.ui.screens.library.LibraryScreen
import com.nexamusic.app.ui.screens.library.LocalSongsScreen
import com.nexamusic.app.ui.screens.settings.SettingsScreen
import com.nexamusic.app.ui.screens.playlist.AutoPlaylistScreen
import com.nexamusic.app.ui.screens.playlist.CachePlaylistScreen
import com.nexamusic.app.ui.screens.playlist.LocalPlaylistScreen
import com.nexamusic.app.ui.screens.playlist.OnlinePlaylistScreen
import com.nexamusic.app.ui.screens.playlist.TopPlaylistScreen
import com.nexamusic.app.ui.screens.search.OnlineSearchResult
import com.nexamusic.app.ui.screens.settings.AboutScreen
import com.nexamusic.app.ui.screens.settings.AppearanceSettings
import com.nexamusic.app.ui.screens.settings.CanvasSelection
import com.nexamusic.app.ui.screens.settings.AppIconScreen
import com.nexamusic.app.ui.screens.settings.diy.DiyEditorScreen
import com.nexamusic.app.ui.screens.settings.diy.PlayerIconsScreen
import com.nexamusic.app.ui.screens.settings.diy.PresetsScreen
import com.nexamusic.app.ui.screens.settings.FontSelectionScreen
import com.nexamusic.app.ui.screens.settings.GlassEffectSettings
import com.nexamusic.app.ui.screens.settings.PlayerThemeScreen
import com.nexamusic.app.ui.screens.settings.BackupAndRestore
import com.nexamusic.app.ui.screens.settings.SpotifyScreen
import com.nexamusic.app.viewmodels.SpotifyImportViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexamusic.app.ui.screens.settings.ContentSettings
import com.nexamusic.app.ui.screens.settings.DiscordLoginScreen
import com.nexamusic.app.ui.screens.settings.PlayerSettings
import com.nexamusic.app.ui.screens.settings.JioSettings
import com.nexamusic.app.ui.screens.settings.LocalFoldersSettingsScreen
import com.nexamusic.app.ui.screens.settings.ScanMusicScreen
import com.nexamusic.app.ui.screens.settings.PrivacySettings
import com.nexamusic.app.ui.screens.settings.RomanizationSettings
import com.nexamusic.app.ui.screens.settings.AccountSettingsScreen
import com.nexamusic.app.ui.screens.settings.StorageSettings
import com.nexamusic.app.ui.screens.settings.ThemeScreen
import com.nexamusic.app.ui.screens.settings.AiSettings
import com.nexamusic.app.ui.screens.settings.integrations.DiscordSettings
import com.nexamusic.app.ui.screens.settings.integrations.IntegrationScreen
import com.nexamusic.app.ui.screens.settings.integrations.LastFMSettings
import com.nexamusic.app.ui.screens.settings.integrations.ListenTogetherSettings
import com.nexamusic.app.ui.screens.ambient.AmbientModeScreen
import com.nexamusic.app.ui.screens.recognition.RecognitionScreen
import com.nexamusic.app.ui.screens.recognition.RecognitionHistoryScreen
import com.nexamusic.app.ui.screens.settings.ModuleSourceScreen
import com.nexamusic.app.ui.screens.settings.ModuleDetailScreen
import com.nexamusic.app.ui.screens.settings.UpdateSettings
import com.nexamusic.app.ui.screens.wrapped.WrappedScreen
import com.nexamusic.app.vivimusic.updater.UpdateScreen
import com.nexamusic.app.vivimusic.changelog.ChangelogScreen
import com.nexamusic.app.vivimusic.commitscreen.CommitScreen
import com.nexamusic.app.ui.screens.equalizer.axion.AxionEqScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState,
) {
    // Reverted from the pager (MainTabsPager.kt, still in the tree but unused): the
    // pager itself became the lag source it was meant to fix, so these are real
    // NavHost destinations again -- one AnimatedContent transition per switch (the
    // iOS parallax push set at the NavHost level in MainActivity), multi-back-stack
    // via popUpTo/saveState/restoreState in onNavItemClick, same as everything else
    // in this file.
    sharedComposable(Screens.Home.route) {
        HomeScreen(navController = navController, snackbarHostState = snackbarHostState)
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

    sharedComposable(Screens.Settings.route) {
        SettingsScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "listen_together_from_topbar",
    ) {
        ListenTogetherScreen(navController, showTopBar = true)
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
            fadeIn(tween(250))
        },
        exitTransition = {
            if (targetState.destination.route?.startsWith("search/") == true) {
                fadeOut(tween(200))
            } else {
                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
            }
        },
        popEnterTransition = {
            if (initialState.destination.route?.startsWith("search/") == true) {
                fadeIn(tween(250))
            } else {
                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
            }
        },
        popExitTransition = {
            fadeOut(tween(200))
        },
    ) {
        OnlineSearchResult(navController)
    }

    sharedComposable(
        route = "album/{albumId}",
        arguments = listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
        ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "artist/{artistId}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
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
        route = "artist/{artistId}/items?browseId={browseId}?params={params}",
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
        route = "online_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "local_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
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

    sharedComposable(
        route = "auto_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }

    sharedComposable(
        route = "cache_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
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

    sharedComposable("settings/appearance/playertheme") {
        PlayerThemeScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/appearance/liquidglass") {
        GlassEffectSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/content") {
        ContentSettings(navController, scrollBehavior)
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

    sharedComposable("settings/player/jio") {
        JioSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/scan_music") {
        ScanMusicScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/player/local_folders") {
        LocalFoldersSettingsScreen(navController, scrollBehavior)
    }

    sharedComposable("settings/storage") {
        StorageSettings(navController, scrollBehavior)
    }

    sharedComposable("settings/equalizer") {
        AxionEqScreen(onBackClick = { navController.navigateUp() })
    }

    sharedComposable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }

    sharedComposable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }

    sharedComposable("settings/spotify") {
        SpotifyScreen(navController, scrollBehavior)
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
 * [composable] that also publishes the destination's own [AnimatedVisibilityScope].
 *
 * A shared element needs both ends to name the transition they are part of, and for a
 * NavHost destination that scope only exists inside the entry's content lambda. Rather
 * than repeat a CompositionLocalProvider in every screen, destinations that take part in
 * an artwork morph are declared with this instead of [composable].
 *
 * Applied only where there is actually a shared pair. A destination with no shared element
 * gains nothing from the local, and wrapping all sixty-odd of them would be churn.
 */

/**
 * A destination that grows out of the tile that opened it, and shrinks back into it.
 *
 * The whole screen is one shared element paired with the tapped tile's artwork box, so
 * the detail page reads as that tile opening rather than as a new page arriving over it.
 * See `Modifier.morphContainer` for the mechanics and for what the reference capture
 * actually does.
 *
 * The enter/exit specs here deliberately do almost nothing: the morph IS the transition,
 * and any slide or scale declared at this level would fight the shared element's own
 * interpolation of the same bounds. What is left is the treatment of the screen BEHIND
 * the morph -- it holds still and dims, because the tile the card is flying to and from
 * must not travel out from under it.
 *
 * Falls back to a plain cross-fade when the destination carries no id, or when its tile
 * was never on screen to be nominated (a deep link, a restored back stack): with no
 * source half, `morphContainer` is inert and this is all that is left.
 */
private fun NavGraphBuilder.sharedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable(
    route = route,
    arguments = arguments,
    enterTransition = { fadeIn(tween(1)) },
    exitTransition = { fadeOut(tween(180), targetAlpha = 0.7f) },
    popEnterTransition = { fadeIn(tween(180), initialAlpha = 0.7f) },
    popExitTransition = { fadeOut(tween(1)) },
) { entry ->
    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
        val morphId = entry.morphArtworkId()
        // `morphContainer` no-ops with no id (deep link, restored back stack), and
        // also finds no partner to interpolate from when the id's tile never called
        // `sharedArtworkSource` -- a bespoke card rendering its own AsyncImage instead
        // of going through one of Items.kt's shared composables. Both cases used to
        // fall all the way back to the bare fadeIn(1) above: an instant pop with no
        // motion at all. `animateEnterExit` is the generic, cheap fallback for both:
        // a soft grow-and-fade that costs nothing but a graphicsLayer scale (no
        // shared-element matching, no tile identity needed) -- which is the whole
        // point: every tile-opened screen gets SOME grow-in, not only the ones that
        // were wired into the shared-artwork system.
        //
        // Gated on `!hasMorphSource`, not id == null -- stacking this on TOP of an id
        // that IS driving a real container morph would scale the card twice
        // (morphContainer's own interpolation, then this fallback's 0.94 on top of
        // it), which reads as the card overshooting past its target and snapping back.
        val hasRealMorph = morphId != null && hasMorphSource(morphId)
        Box(
            Modifier
                .morphContainer(morphId, isSource = false)
                .then(
                    if (!hasRealMorph) {
                        Modifier.animateEnterExit(
                            // enter (push, never gesture-driven): spring is fine.
                            enter = fadeIn(Motion.appear()) + scaleIn(Motion.appear(), initialScale = 0.94f),
                            // exit (pop, predictive-back-scrubbed): tween, see appearExit().
                            exit = fadeOut(Motion.appearExit()) + scaleOut(Motion.appearExit(), targetScale = 0.94f),
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            content(entry)
        }
    }
}
