/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.HomeGridColumnsOverrideKey
import com.convx.music.constants.ShowHomeFabKey
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.HomeImageBackground
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.rememberAppBackgroundColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.theme.AppPage
import com.convx.music.ui.theme.LocalMetadataColor
import com.convx.music.ui.theme.LocalPageMetrics
import com.convx.music.utils.rememberPreference

/**
 * Home for local-only mode: the on-device library under the same chrome as the feed Home.
 *
 * Its own screen rather than branches inside [HomeScreen]: there is no feed to refresh, no sign-in
 * card, mood chips, charts or wrapped card, so none of that machinery is composed here.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocalHomeScreen(
    navController: NavController,
    viewModel: com.convx.music.viewmodels.LocalHomeViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val isPlayingState = playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadataState = playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val localSongs by viewModel.songs.collectAsStateWithLifecycle()
    val localAlbums by viewModel.albums.collectAsStateWithLifecycle()
    val localArtists by viewModel.artists.collectAsStateWithLifecycle()
    val localPlaylists by viewModel.playlists.collectAsStateWithLifecycle()
    val localFolders by viewModel.folders.collectAsStateWithLifecycle()

    val localAudioPermission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    LaunchedEffect(localAudioPermission.granted) {
        if (localAudioPermission.granted) viewModel.refresh()
    }

    val lazyListState = rememberLazyListState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsStateWithLifecycle()
    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            lazyListState.animateScrollToItem(0)
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    val (homeGridColumnsOverride) = rememberPreference(HomeGridColumnsOverrideKey, 0)
    val keepListeningColumns = if (homeGridColumnsOverride > 0) homeGridColumnsOverride else 2

    val itemHandlers = rememberHomeItemHandlers(
        navController, playerConnection, menuState, haptic, scope, mediaMetadataState, isPlayingState,
    )

    CompositionLocalProvider(
        LocalPageMetrics provides AppPage.Metrics,
        LocalMetadataColor provides AppTheme.colorScheme.onSurfaceVariant,
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(rememberAppBackgroundColor(AppTheme.colorScheme.surface)),
            )
            HomeImageBackground(contentLoaded = false)

            val sideInset = LocalPlayerAwareWindowInsets.current
                .asPaddingValues()
                .calculateStartPadding(LocalLayoutDirection.current)

            val deps = HomeSectionDeps(
                context = LocalContext.current,
                randomItem = { null },
                navController = navController,
                playerConnection = playerConnection,
                menuState = menuState,
                haptic = haptic,
                scope = scope,
                sideInset = sideInset,
                localGridItem = itemHandlers.localGridItem,
                onLocalItemClick = itemHandlers.onLocalItemClick,
            )

            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(AppPage.ItemSpacing),
                contentPadding = run {
                    val ins = LocalPlayerAwareWindowInsets.current.asPaddingValues()
                    val dir = LocalLayoutDirection.current
                    PaddingValues(
                        start = ins.calculateStartPadding(dir),
                        top = maxOf(ins.calculateTopPadding(), if (sideInset > 0.dp) 96.dp else 116.dp),
                        end = ins.calculateRightPadding(dir),
                        bottom = ins.calculateBottomPadding(),
                    )
                },
            ) {
                if (!localAudioPermission.granted) {
                    item(key = "local_permission", contentType = "local_access") {
                        com.convx.music.ui.screens.library.apple.LocalAccessCard(
                            blocked = localAudioPermission.blocked,
                            onAllow = localAudioPermission.request,
                        )
                    }
                } else if (localSongs.isEmpty()) {
                    item(key = "local_empty", contentType = "local_access") {
                        com.convx.music.ui.component.LocalMusicEmptyState(onScan = viewModel::refresh)
                    }
                } else localHomeContent(
                    deps = deps,
                    songs = localSongs,
                    albums = localAlbums,
                    artists = localArtists,
                    playlists = localPlaylists,
                    folders = localFolders,
                    columns = keepListeningColumns,
                )

                item(key = "bottom_spacer", contentType = "spacer") {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            val (showHomeFab) = rememberPreference(ShowHomeFabKey, true)
            if (showHomeFab) {
                HideOnScrollFAB(
                    visible = localSongs.isNotEmpty(),
                    lazyListState = lazyListState,
                    icon = R.drawable.shuffle,
                    onClick = {
                        // Nothing to pick a radio from offline: shuffle the library itself.
                        playerConnection.playQueue(
                            ListQueue(items = localSongs.shuffled().map { it.toMediaItem() }, startIndex = 0),
                        )
                    },
                    onRecognitionClick = { navController.navigate("recognition") },
                )
            }
        }
    }
}
