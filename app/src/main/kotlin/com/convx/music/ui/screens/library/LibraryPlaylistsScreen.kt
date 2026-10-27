/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import com.convx.music.ui.ios.compat.DropdownMenu
import com.convx.music.ui.ios.compat.DropdownMenuItem
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.music.LocalDatabase
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.SongSortType
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.localPlaylistRoute
import kotlinx.coroutines.flow.first

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.SegmentedButton
import com.convx.music.ui.ios.compat.SingleChoiceSegmentedButtonRow
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFListBullet
import com.composables.sfsymbols.monochrome.SFSquareGrid2x2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.music.innertube.utils.parseCookieString
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.CONTENT_TYPE_HEADER
import com.convx.music.constants.CONTENT_TYPE_PLAYLIST
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.LibraryIconsOnlyKey
import com.convx.music.constants.LibraryViewType
import com.convx.music.constants.PlaylistSortDescendingKey
import com.convx.music.constants.PlaylistSortType
import com.convx.music.constants.PlaylistSortTypeKey
import com.convx.music.constants.PlaylistViewTypeKey
import com.convx.music.constants.ShowCachedPlaylistKey
import com.convx.music.constants.ShowDownloadedPlaylistKey
import com.convx.music.constants.ShowLikedPlaylistKey
import com.convx.music.constants.ShowTopPlaylistKey
import com.convx.music.constants.ShowUploadedPlaylistKey
import com.convx.music.constants.YtmSyncKey
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.ui.component.buildAlphabetSectionIndex
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.TopFadeBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.LibraryPlaylistGridItem
import com.convx.music.ui.component.LibraryPlaylistListItem
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.PlaylistGridItem
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.rememberLibraryPlaylistMenuAction
import com.convx.music.ui.component.SortHeader
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LibraryPlaylistsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.rememberHeroZoom
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryPlaylistsScreen(
    navController: NavController,
    filterContent: @Composable () -> Unit,
    viewModel: LibraryPlaylistsViewModel = koinViewModel(),
    initialTextFieldValue: String? = null,
    allowSyncing: Boolean = true,
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current

    val coroutineScope = rememberCoroutineScope()

    var viewType by rememberEnumPreference(PlaylistViewTypeKey, LibraryViewType.GRID)
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        PlaylistSortTypeKey,
        PlaylistSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(
        PlaylistSortDescendingKey,
        true
    )
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
    
    val (libraryIconsOnly) = rememberPreference(LibraryIconsOnlyKey, defaultValue = true)

    val playlists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val loadPlaylistForMenu = remember(viewModel) { viewModel::loadPlaylistForMenu }

    val topSize by viewModel.topValue.collectAsState(initial = 50)

    val likedPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.liked)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val downloadPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.offline)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val topPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.my_top) + " $topSize"
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val cachePlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.cached_playlist)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val aiDjMixPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.ai_dj_mix)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )
        
    val uploadedPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.uploaded_playlist)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val (showLiked) = rememberPreference(ShowLikedPlaylistKey, true)
    val (showDownloaded) = rememberPreference(ShowDownloadedPlaylistKey, true)
    val (showTop) = rememberPreference(ShowTopPlaylistKey, true)
    val (showCached) = rememberPreference(ShowCachedPlaylistKey, true)
    val (showUploaded) = rememberPreference(ShowUploadedPlaylistKey, true)

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    // Pull-to-refresh only: no hero artwork on this screen, so heroZoom.scale
    // goes unread and the modifier contributes just the rubber-band stretch.
    val heroZoom = rememberHeroZoom()
    // Progressive top blur so the list dissolves under the status bar instead
    // of cutting off hard — same technique as the rest of the app's screens.
    val hazeState = remember { HazeState() }


    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsStateWithLifecycle()

    val (innerTubeCookie) = rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

    val (ytmSync) = rememberPreference(YtmSyncKey, true)

    LaunchedEffect(Unit) {
        if (ytmSync) {
            withContext(Dispatchers.IO) {
                viewModel.sync()
            }
        }
    }

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            when (viewType) {
                LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
                LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
            }
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    var showCreatePlaylistDialog by rememberSaveable { mutableStateOf(false) }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            initialTextFieldValue = initialTextFieldValue,
            allowSyncing = allowSyncing,
            onPlaylistCreated = { playlistId ->
                showCreatePlaylistDialog = false
                navController.navigate("local_playlist/$playlistId")
            }
        )
    }

    val headerContent = @Composable {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppleTokens.Gutter),
        ) {
            SortHeader(
                sortType = sortType,
                sortDescending = sortDescending,
                onSortTypeChange = onSortTypeChange,
                onSortDescendingChange = onSortDescendingChange,
                sortTypeText = { sortType ->
                    when (sortType) {
                        PlaylistSortType.CREATE_DATE -> R.string.sort_by_create_date
                        PlaylistSortType.NAME -> R.string.sort_by_name
                        PlaylistSortType.SONG_COUNT -> R.string.sort_by_song_count
                        PlaylistSortType.LAST_UPDATED -> R.string.sort_by_last_updated
                    }
                },
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = pluralStringResource(
                    R.plurals.n_playlist,
                    playlists.size,
                    playlists.size
                ),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colorScheme.secondary,
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            ) {
                LibraryViewType.entries.forEach { type ->
                    SegmentedButton(
                        selected = viewType == type,
                        onClick = { viewType = type },
                        icon = {
                            SymbolIcon(bounceOnEnter = true, symbol = when (type) {
                                    LibraryViewType.LIST -> sf("SFListBullet")
                                    LibraryViewType.GRID -> sf("SFSquareGrid2x2")
                                }, contentDescription = null)
                        },
                    )
                }
            }
        }
    }

    // Hoisted: both view types de-duped the same list separately, and the scroll rail
    // needs the resulting count too.
    // Synced (online) playlists lead; ones made on this device follow below a divider.
    val (onlinePlaylists, devicePlaylists) = remember(playlists) {
        playlists.distinctBy { it.id }.partition { it.browseId != null }
    }
    val visiblePlaylists = remember(onlinePlaylists, devicePlaylists) { onlinePlaylists + devicePlaylists }
    val showDivider = onlinePlaylists.isNotEmpty() && devicePlaylists.isNotEmpty()

    // Keep pinned playlists in the regular list; this separate list feeds the pinned row.
    val pinnedPlaylists = remember(visiblePlaylists) {
        visiblePlaylists.filter { it.pinnedAtEpochMillis != null }
            .sortedByDescending { it.pinnedAtEpochMillis }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (viewType) {
            LibraryViewType.LIST -> {
                LazyColumn(
                    state = lazyListState,
                    overscrollEffect = heroZoom.listOverscroll(),
                    modifier = Modifier.heroPullZoom(heroZoom).hazeSource(hazeState),
                    contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                ) {
                    item(
                        key = "title",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        LargeScreenTitle(title = stringResource(R.string.playlists))
                    }

                    item(
                        key = "filter",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        filterContent()
                    }

                    item(
                        key = "header",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        headerContent()
                    }

                    if (pinnedPlaylists.isNotEmpty()) {
                        item(key = "pinned_header", contentType = CONTENT_TYPE_HEADER) {
                            Text(
                                text = stringResource(R.string.pinned),
                                style = AppTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = AppleTokens.Gutter, vertical = 8.dp)
                                    .animateItem(),
                            )
                        }
                        item(key = "pinned_row", contentType = "pinned_row") {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = AppleTokens.Gutter),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem(),
                            ) {
                                items(pinnedPlaylists, key = { "pinned_${it.id}" }) { playlist ->
                                    val openMenu = rememberLibraryPlaylistMenuAction(
                                        playlist.id,
                                        menuState,
                                        coroutineScope,
                                        loadPlaylistForMenu,
                                    )
                                    PlaylistGridItem(
                                        playlist = playlist,
                                        fillMaxWidth = false,
                                        modifier = Modifier
                                            .width(150.dp)
                                            .combinedBounceClick(
                                                onClick = { navController.navigate(localPlaylistRoute(playlist.id, null)) },
                                                onLongClick = openMenu,
                                            ),
                                    )
                                }
                            }
                        }
                    }

                    if (showLiked) {
                        item(
                            key = "likedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = likedPlaylist,
                                sharedArtworkId = "liked",
                                grid = false,
                                onClick = { navController.navigate("auto_playlist/liked") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showDownloaded) {
                        item(
                            key = "downloadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = downloadPlaylist,
                                sharedArtworkId = "downloaded",
                                grid = false,
                                onClick = { navController.navigate("auto_playlist/downloaded") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showTop) {
                        item(
                            key = "TopPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = topPlaylist,
                                sharedArtworkId = topSize.toString(),
                                grid = false,
                                onClick = { navController.navigate("top_playlist/$topSize") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showCached) {
                        item(
                            key = "cachePlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = cachePlaylist,
                                sharedArtworkId = "cached",
                                grid = false,
                                onClick = { navController.navigate("cache_playlist/cached") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                    
                    if (showUploaded) {
                        item(
                            key = "uploadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = uploadedPlaylist,
                                sharedArtworkId = "uploaded",
                                grid = false,
                                onClick = { navController.navigate("auto_playlist/uploaded") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    item(
                        key = "aiDjMixPlaylist",
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) {
                        AutoPlaylistCard(
                            playlist = aiDjMixPlaylist,
                            grid = false,
                            onClick = { navController.navigate("ai_dj_set") },
                            showIconOnly = libraryIconsOnly,
                            modifier = Modifier.animateItem(),
                        )
                    }

                    playlists.let { playlists ->
                        if (playlists.isEmpty()) {
                            item(key = "empty_placeholder") {
                            }
                        }

                        items(
                            items = onlinePlaylists,
                            key = { it.id },
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) { playlist ->
                            LibraryPlaylistListItem(
                                navController = navController,
                                menuState = menuState,
                                coroutineScope = coroutineScope,
                                playlist = playlist,
                                loadPlaylist = loadPlaylistForMenu,
                                modifier = Modifier.animateItem()
                            )
                        }
                        if (showDivider) {
                            item(key = "local_divider", contentType = CONTENT_TYPE_HEADER) { LocalSectionDivider() }
                        }
                        items(
                            items = devicePlaylists,
                            key = { "local_${it.id}" },
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) { playlist ->
                            LibraryPlaylistListItem(
                                navController = navController,
                                menuState = menuState,
                                coroutineScope = coroutineScope,
                                playlist = playlist,
                                loadPlaylist = loadPlaylistForMenu,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }

                HideOnScrollFAB(
                    lazyListState = lazyListState,
                    icon = R.drawable.add,
                    onClick = {
                        showCreatePlaylistDialog = true
                    },
                    hazeState = hazeState,
                )
            }

            LibraryViewType.GRID -> {
                LazyVerticalGrid(
                    state = lazyGridState,
                    overscrollEffect = heroZoom.listOverscroll(),
                    modifier = Modifier.heroPullZoom(heroZoom).hazeSource(hazeState),
                    columns = rememberGridColumns(),
                    contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                ) {
                    item(
                        key = "title",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        LargeScreenTitle(title = stringResource(R.string.playlists))
                    }

                    item(
                        key = "filter",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        filterContent()
                    }

                    item(
                        key = "header",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        headerContent()
                    }

                    if (pinnedPlaylists.isNotEmpty()) {
                        item(
                            key = "pinned_header",
                            span = { GridItemSpan(maxLineSpan) },
                            contentType = CONTENT_TYPE_HEADER,
                        ) {
                            Text(
                                text = stringResource(R.string.pinned),
                                style = AppTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = AppleTokens.Gutter, vertical = 8.dp)
                                    .animateItem(),
                            )
                        }
                        item(
                            key = "pinned_row",
                            span = { GridItemSpan(maxLineSpan) },
                            contentType = "pinned_row",
                        ) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = AppleTokens.Gutter),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem(),
                            ) {
                                items(pinnedPlaylists, key = { "pinned_${it.id}" }) { playlist ->
                                    val openMenu = rememberLibraryPlaylistMenuAction(
                                        playlist.id,
                                        menuState,
                                        coroutineScope,
                                        loadPlaylistForMenu,
                                    )
                                    PlaylistGridItem(
                                        playlist = playlist,
                                        fillMaxWidth = false,
                                        modifier = Modifier
                                            .width(150.dp)
                                            .combinedBounceClick(
                                                onClick = { navController.navigate(localPlaylistRoute(playlist.id, null)) },
                                                onLongClick = openMenu,
                                            ),
                                    )
                                }
                            }
                        }
                    }

                    if (showLiked) {
                        item(
                            key = "likedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = likedPlaylist,
                                sharedArtworkId = "liked",
                                grid = true,
                                onClick = { navController.navigate("auto_playlist/liked") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showDownloaded) {
                        item(
                            key = "downloadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = downloadPlaylist,
                                sharedArtworkId = "downloaded",
                                grid = true,
                                onClick = { navController.navigate("auto_playlist/downloaded") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showTop) {
                        item(
                            key = "TopPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = topPlaylist,
                                sharedArtworkId = topSize.toString(),
                                grid = true,
                                onClick = { navController.navigate("top_playlist/$topSize") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showCached) {
                        item(
                            key = "cachePlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = cachePlaylist,
                                sharedArtworkId = "cached",
                                grid = true,
                                onClick = { navController.navigate("cache_playlist/cached") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    if (showUploaded) {
                        item(
                            key = "uploadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            AutoPlaylistCard(
                                playlist = uploadedPlaylist,
                                sharedArtworkId = "uploaded",
                                grid = true,
                                onClick = { navController.navigate("auto_playlist/uploaded") },
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    item(
                        key = "aiDjMixPlaylist",
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) {
                        AutoPlaylistCard(
                            playlist = aiDjMixPlaylist,
                            grid = true,
                            onClick = { navController.navigate("ai_dj_set") },
                            showIconOnly = libraryIconsOnly,
                            modifier = Modifier.animateItem(),
                        )
                    }

                    playlists.let { playlists ->
                        if (playlists.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                            }
                        }

                        items(
                            items = onlinePlaylists,
                            key = { it.id },
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) { playlist ->
                            LibraryPlaylistGridItem(
                                navController = navController,
                                menuState = menuState,
                                coroutineScope = coroutineScope,
                                playlist = playlist,
                                loadPlaylist = loadPlaylistForMenu,
                                modifier = Modifier.animateItem()
                            )
                        }
                        if (showDivider) {
                            item(
                                key = "local_divider",
                                span = { GridItemSpan(maxLineSpan) },
                                contentType = CONTENT_TYPE_HEADER,
                            ) { LocalSectionDivider() }
                        }
                        items(
                            items = devicePlaylists,
                            key = { "local_${it.id}" },
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) { playlist ->
                            LibraryPlaylistGridItem(
                                navController = navController,
                                menuState = menuState,
                                coroutineScope = coroutineScope,
                                playlist = playlist,
                                loadPlaylist = loadPlaylistForMenu,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }

                HideOnScrollFAB(
                    lazyListState = lazyGridState,
                    icon = R.drawable.add,
                    onClick = {
                        showCreatePlaylistDialog = true
                    },
                    hazeState = hazeState,
                )
            }
        }

        TopFadeBlur(
            hazeState = hazeState,
            pageColor = AppTheme.colorScheme.background,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        ListScrollRail(
            lazyListState = lazyListState,
            lazyGridState = lazyGridState,
            isGrid = viewType == LibraryViewType.GRID,
            itemCount = visiblePlaylists.size,
            sectionIndexMap = if (sortType == PlaylistSortType.NAME) {
                remember(visiblePlaylists) {
                    buildAlphabetSectionIndex(visiblePlaylists) { it.name }
                }
            } else {
                null
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AutoPlaylistCard(
    playlist: Playlist,
    sharedArtworkId: String? = null,
    grid: Boolean,
    onClick: () -> Unit,
    showIconOnly: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var customUri by rememberPreference(stringPreferencesKey("thumbnail_${playlist.id}"), "")

    val override = customUri.takeIf { it.isNotBlank() }

    var menuOpen by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            customUri = uri.toString()
        }
    }

    val clickMod = Modifier
        .fillMaxWidth()
        .combinedBounceClick(
            onClick = onClick,
            onLongClick = { menuOpen = true },
        )

    Box(modifier) {
        if (grid) {
            PlaylistGridItem(
                playlist = playlist,
                fillMaxWidth = true,
                autoPlaylist = true,
                sharedArtworkId = sharedArtworkId,
                showIconOnly = showIconOnly,
                thumbnailOverrideUrl = override,
                modifier = clickMod,
            )
        } else {
            PlaylistListItem(
                playlist = playlist,
                autoPlaylist = true,
                sharedArtworkId = sharedArtworkId,
                showIconOnly = showIconOnly,
                thumbnailOverrideUrl = override,
                modifier = clickMod,
            )
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.change_card_image)) },
                onClick = {
                    menuOpen = false
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            )
            if (override != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.remove_custom_image)) },
                    onClick = {
                        menuOpen = false
                        customUri = ""
                    },
                )
            }
        }
    }
}
