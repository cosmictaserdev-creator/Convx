/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.SegmentedButton
import com.convx.music.ui.ios.compat.SingleChoiceSegmentedButtonRow
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFListBullet
import com.composables.sfsymbols.monochrome.SFSquareGrid2x2
import com.composables.sfsymbols.monochrome.SFXmark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.AlbumFilter
import com.convx.music.constants.AlbumFilterKey
import com.convx.music.constants.AlbumSortDescendingKey
import com.convx.music.constants.AlbumSortType
import com.convx.music.constants.AlbumSortTypeKey
import com.convx.music.constants.AlbumViewTypeKey
import com.convx.music.constants.CONTENT_TYPE_ALBUM
import com.convx.music.constants.CONTENT_TYPE_HEADER
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.LibraryIconsOnlyKey
import com.convx.music.constants.LibraryViewType
import com.convx.music.constants.YtmSyncKey
import com.convx.music.ui.component.buildAlphabetSectionIndex
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.ChipsRow
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.TopFadeBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.EmptyPlaceholder
import com.convx.music.ui.component.LibraryAlbumGridItem
import com.convx.music.ui.component.LibraryAlbumListItem
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.SortHeader
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LibraryAlbumsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.rememberHeroZoom
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryAlbumsScreen(
    navController: NavController,
    onDeselect: () -> Unit,
    viewModel: LibraryAlbumsViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    var viewType by rememberEnumPreference(AlbumViewTypeKey, LibraryViewType.GRID)
    var storedFilter by rememberEnumPreference(AlbumFilterKey, AlbumFilter.LIKED)
    val localOnly = com.convx.music.LocalLocalOnlyMode.current
    // Mirrors what the view model actually queries while local-only mode is on.
    val filter = if (localOnly) AlbumFilter.LOCAL else storedFilter
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        AlbumSortTypeKey,
        AlbumSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(AlbumSortDescendingKey, true)
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
    
    val (libraryIconsOnly) = rememberPreference(LibraryIconsOnlyKey, defaultValue = true)

    val (ytmSync) = rememberPreference(YtmSyncKey, true)
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)

    val filterContent = @Composable {
        Row {
            Spacer(Modifier.width(AppleTokens.Gutter))
            FilterChip(
                label = { Text(stringResource(R.string.albums)) },
                selected = true,
                colors = FilterChipDefaults.filterChipColors(containerColor = AppTheme.colorScheme.surface),
                onClick = onDeselect,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    SymbolIcon(bounceOnEnter = true, symbol = sf("SFXmark"), contentDescription = "")
                },
            )
            if (!localOnly) {
                ChipsRow(
                    chips =
                    listOf(
                        AlbumFilter.LIKED to stringResource(R.string.filter_liked),
                        AlbumFilter.LIBRARY to stringResource(R.string.filter_library),
                        AlbumFilter.UPLOADED to stringResource(R.string.filter_uploaded),
                        AlbumFilter.LOCAL to stringResource(R.string.filter_local),
                    ),
                    currentValue = filter,
                    onValueUpdate = {
                        storedFilter = it
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ytmSync) {
            withContext(Dispatchers.IO) {
                viewModel.sync()
            }
        }
    }

    val albums by viewModel.allAlbums.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    // Pull-to-refresh only: heroZoom.scale goes unread, contributes just the rubber-band stretch.
    val heroZoom = rememberHeroZoom()
    // Top blur so the list dissolves under the status bar.
    val hazeState = remember { HazeState() }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsStateWithLifecycle()

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            when (viewType) {
                LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
                LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
            }
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
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
                        AlbumSortType.CREATE_DATE -> R.string.sort_by_create_date
                        AlbumSortType.NAME -> R.string.sort_by_name
                        AlbumSortType.ARTIST -> R.string.sort_by_artist
                        AlbumSortType.YEAR -> R.string.sort_by_year
                        AlbumSortType.SONG_COUNT -> R.string.sort_by_song_count
                        AlbumSortType.LENGTH -> R.string.sort_by_length
                        AlbumSortType.PLAY_TIME -> R.string.sort_by_play_time
                    }
                },
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = pluralStringResource(R.plurals.n_album, albums.size, albums.size),
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

    // Hoisted: both view types and the scroll rail share this deduped list.
    val localAlbums by viewModel.localAlbums.collectAsStateWithLifecycle()
    val sectioned = !localOnly && filter != AlbumFilter.LOCAL
    // Online albums first; device albums follow below a divider.
    val onlineAlbums = remember(albums, hideExplicit, sectioned) {
        (if (hideExplicit) albums.filter { !it.album.explicit } else albums)
            .filter { !sectioned || !it.album.isLocal }
            .distinctBy { it.id }
    }
    val deviceAlbums = remember(onlineAlbums, localAlbums, sectioned) {
        if (!sectioned) emptyList() else localAlbums.distinctBy { it.id }.filter { local -> onlineAlbums.none { it.id == local.id } }
    }
    val visibleAlbums = remember(onlineAlbums, deviceAlbums) { onlineAlbums + deviceAlbums }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (viewType) {
            LibraryViewType.LIST ->
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
                        LargeScreenTitle(title = stringResource(R.string.albums))
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

                    albums.let { albums ->
                        if (albums.isEmpty()) {
                            item(key = "empty_placeholder") {
                                EmptyPlaceholder(
                                    icon = R.drawable.album,
                                    text = stringResource(R.string.library_album_empty),
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        items(
                            items = onlineAlbums,
                            key = { it.id },
                            contentType = { CONTENT_TYPE_ALBUM },
                        ) { album ->
                            LibraryAlbumListItem(
                                navController = navController,
                                menuState = menuState,
                                album = album,
                                isActive = album.id == mediaMetadata?.album?.id,
                                isPlaying = isPlaying,
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }

                        if (deviceAlbums.isNotEmpty()) {
                            item(key = "local_divider", contentType = CONTENT_TYPE_HEADER) {
                                LocalSectionDivider()
                            }
                            items(
                                items = deviceAlbums,
                                key = { "local_${it.id}" },
                                contentType = { CONTENT_TYPE_ALBUM },
                            ) { album ->
                                LibraryAlbumListItem(
                                    navController = navController,
                                    menuState = menuState,
                                    album = album,
                                    isActive = album.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    showIconOnly = libraryIconsOnly,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }

            LibraryViewType.GRID ->
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
                        LargeScreenTitle(title = stringResource(R.string.albums))
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

                    albums.let { albums ->
                        if (albums.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyPlaceholder(
                                    icon = R.drawable.album,
                                    text = stringResource(R.string.library_album_empty),
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        items(
                            items = onlineAlbums,
                            key = { it.id },
                            contentType = { CONTENT_TYPE_ALBUM },
                        ) { album ->
                            LibraryAlbumGridItem(
                                navController = navController,
                                menuState = menuState,
                                coroutineScope = coroutineScope,
                                album = album,
                                isActive = album.id == mediaMetadata?.album?.id,
                                isPlaying = isPlaying,
                                showIconOnly = libraryIconsOnly,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }

                        if (deviceAlbums.isNotEmpty()) {
                            item(
                                key = "local_divider",
                                span = { GridItemSpan(maxLineSpan) },
                                contentType = CONTENT_TYPE_HEADER,
                            ) {
                                LocalSectionDivider()
                            }
                            items(
                                items = deviceAlbums,
                                key = { "local_${it.id}" },
                                contentType = { CONTENT_TYPE_ALBUM },
                            ) { album ->
                                LibraryAlbumGridItem(
                                    navController = navController,
                                    menuState = menuState,
                                    coroutineScope = coroutineScope,
                                    album = album,
                                    isActive = album.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    showIconOnly = libraryIconsOnly,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
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
            itemCount = visibleAlbums.size,
            sectionIndexMap = when (sortType) {
                AlbumSortType.NAME ->
                    remember(visibleAlbums) {
                        buildAlphabetSectionIndex(visibleAlbums) { it.album.title }
                    }

                AlbumSortType.ARTIST ->
                    remember(visibleAlbums) {
                        buildAlphabetSectionIndex(visibleAlbums) { album ->
                            album.artists.firstOrNull()?.name.orEmpty()
                        }
                    }

                else -> null
            },
        )
    }
}
