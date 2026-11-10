/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.artist

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.hazeSource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import androidx.compose.ui.res.painterResource
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.HeroSource
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.component.YouTubeListItem
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.shimmer.GridItemPlaceHolder
import com.convx.music.ui.component.shimmer.ListItemPlaceHolder
import com.convx.music.ui.component.shimmer.ShimmerHost
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.utils.listItemShape
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.viewmodels.ArtistItemsViewModel
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistItemsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistItemsViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    val title by viewModel.title.collectAsStateWithLifecycle()
    val itemsPage by viewModel.itemsPage.collectAsStateWithLifecycle()

    // Match the shared glass hero treatment used by other browse screens.
    val tint = Color.Black
    val onTint = AppleTokens.onColor(tint)

    // Unattached: the safe ambient backdrop for anything drawn inside the list.
    val heroBackdrop = rememberLayerBackdrop()

    // Attached to the Box wrapping the lists, and handed only to the floating
    // chrome row, which is that layer's sibling.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()
    val heroZoom = rememberHeroZoom()

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "loading" }
        }.collect { shouldLoadMore ->
            if (!shouldLoadMore) return@collect
            viewModel.loadMore()
        }
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow {
            lazyGridState.layoutInfo.visibleItemsInfo.any { it.key == "loading" }
        }.collect { shouldLoadMore ->
            if (!shouldLoadMore) return@collect
            viewModel.loadMore()
        }
    }

    HeroBackground(
        tint = tint,
        heroSource = HeroSource.Default,
        bottomGradient = true,
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        // Deduplicate once; doing this inside each row would repeat a full scan.
        val distinctItems = remember(itemsPage) {
            itemsPage?.items.orEmpty().distinctBy { it.id }
        }
        val hazeState = remember { dev.chrisbanes.haze.HazeState() }
        Box(modifier = Modifier.fillMaxSize()) {
            // Keep backdrop capture in a plain Box; recycled list item layers do not flatten reliably.
            Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // Cache the list layer so sibling redraws do not recapture the backdrop.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            .graphicsLayer()
            .hazeSource(hazeState)) {
                if (itemsPage == null) {
                    ShimmerHost(
                        modifier = Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current),
                    ) {
                        repeat(8) {
                            ListItemPlaceHolder()
                        }
                    }
                }

                if (itemsPage?.items?.firstOrNull() is SongItem) {
                    LazyColumn(
                        state = lazyListState,
                        overscrollEffect = heroZoom.listOverscroll(),
                        modifier = Modifier.heroPullZoom(heroZoom),
                        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                    ) {
                        itemsIndexed(
                            items = distinctItems,
                            key = { _, it -> it.id },
                        ) { index, item ->
                            YouTubeListItem(
                                item = item,
                                isActive =
                                when (item) {
                                    is SongItem -> mediaMetadata?.id == item.id
                                    is AlbumItem -> mediaMetadata?.album?.id == item.id
                                    else -> false
                                },
                                isPlaying = isPlaying,
                                shape = listItemShape(index, distinctItems.size),
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            menuState.show {
                                                when (item) {
                                                    is SongItem ->
                                                        YouTubeSongMenu(
                                                            song = item,
                                                            navController = navController,
                                                            onDismiss = menuState::dismiss,
                                                        )

                                                    is AlbumItem ->
                                                        YouTubeAlbumMenu(
                                                            albumItem = item,
                                                            navController = navController,
                                                            onDismiss = menuState::dismiss,
                                                        )

                                                    is ArtistItem ->
                                                        YouTubeArtistMenu(
                                                            artist = item,
                                                            onDismiss = menuState::dismiss,
                                                        )

                                                    is PlaylistItem ->
                                                        YouTubePlaylistMenu(
                                                            playlist = item,
                                                            coroutineScope = coroutineScope,
                                                            onDismiss = menuState::dismiss,
                                                        )
                                                }
                                            }
                                        },
                                    ) {
                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                                    }
                                },
                                modifier =
                                Modifier
                                    .bounceClick {
                                        when (item) {
                                            is SongItem -> {
                                                if (item.id == mediaMetadata?.id) {
                                                    playerConnection.togglePlayPause()
                                                } else {
                                                    playerConnection.playQueue(
                                                        YouTubeQueue(
                                                            item.endpoint ?: WatchEndpoint(videoId = item.id),
                                                            item.toMediaMetadata()
                                                        ),
                                                    )
                                                }
                                            }

                                            is AlbumItem -> navController.navigate(albumRoute(item.id, item.thumbnail))
                                            is ArtistItem -> navController.navigate(artistRoute(item.id, item.thumbnail))
                                            is PlaylistItem -> navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
                                        }
                                    },
                            )
                        }

                        if (itemsPage?.continuation != null) {
                            item(key = "loading") {
                                ShimmerHost {
                                    repeat(3) {
                                        ListItemPlaceHolder()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        state = lazyGridState,
                        columns = rememberGridColumns(),
                        overscrollEffect = heroZoom.listOverscroll(),
                        modifier = Modifier.heroPullZoom(heroZoom),
                        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
                    ) {
                        items(
                            items = distinctItems,
                            key = { it.id }
                        ) { item ->
                            YouTubeGridItem(
                                item = item,
                                isActive = when (item) {
                                    is SongItem -> mediaMetadata?.id == item.id
                                    is AlbumItem -> mediaMetadata?.album?.id == item.id
                                    else -> false
                                },
                                isPlaying = isPlaying,
                                fillMaxWidth = true,
                                coroutineScope = coroutineScope,
                                modifier = Modifier
                                    .combinedBounceClick(
                                        onClick = {
                                            when (item) {
                                                is SongItem -> playerConnection.playQueue(
                                                    YouTubeQueue(
                                                        item.endpoint ?: WatchEndpoint(videoId = item.id),
                                                        item.toMediaMetadata()
                                                    )
                                                )

                                                is AlbumItem -> navController.navigate(albumRoute(item.id, item.thumbnail))
                                                is ArtistItem -> navController.navigate(artistRoute(item.id, item.thumbnail))
                                                is PlaylistItem -> navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
                                            }
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                when (item) {
                                                    is SongItem -> YouTubeSongMenu(
                                                        song = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss
                                                    )

                                                    is AlbumItem -> YouTubeAlbumMenu(
                                                        albumItem = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss
                                                    )

                                                    is ArtistItem -> YouTubeArtistMenu(
                                                        artist = item,
                                                        onDismiss = menuState::dismiss
                                                    )

                                                    is PlaylistItem -> YouTubePlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    .animateItem()
                            )
                        }

                        if (itemsPage?.continuation != null) {
                            item(key = "loading") {
                                ShimmerHost(Modifier.animateItem()) {
                                    GridItemPlaceHolder(fillMaxWidth = true)
                                }
                            }
                        }
                    }
                }
            }

            // Artist items arrive in YouTube's own order, so the rail is a
            // proportional thumb rather than letters.
            ListScrollRail(
                lazyListState = lazyListState,
                lazyGridState = lazyGridState,
                isGrid = itemsPage?.items?.firstOrNull() !is SongItem,
                itemCount = distinctItems.size,
                sectionIndexMap = null,
            )

            com.convx.music.ui.component.TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            // `align` is resolved here, in BoxScope, because the provider
            // lambda below is not a BoxScope.
            val chromeRowModifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .windowInsetsPadding(appTopBarWindowInsets())
                .padding(horizontal = 16.dp, vertical = 8.dp)

            // Only the chrome row gets the attached backdrop — it is a sibling
            // of that layer. Handing it to anything inside the layer is a
            // RenderNode cycle (see LocalPlaylistScreen).
            CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
                Row(
                    modifier = chromeRowModifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlassCircleButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = title,
                        style = AppTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = onTint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
      }
    }
}

