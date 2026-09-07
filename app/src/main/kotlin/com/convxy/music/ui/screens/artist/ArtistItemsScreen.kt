/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.artist

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.utils.rememberGridColumns
import com.convxy.music.constants.GridItemSize
import com.convxy.music.constants.GridItemsSizeKey
import com.convxy.music.constants.GridThumbnailHeight
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.HeroSource
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.YouTubeGridItem
import com.convxy.music.ui.component.YouTubeListItem
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.shimmer.GridItemPlaceHolder
import com.convxy.music.ui.component.shimmer.ListItemPlaceHolder
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.menu.YouTubeAlbumMenu
import com.convxy.music.ui.menu.YouTubeArtistMenu
import com.convxy.music.ui.menu.YouTubePlaylistMenu
import com.convxy.music.ui.menu.YouTubeSongMenu
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.viewmodels.ArtistItemsViewModel
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistItemsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistItemsViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    val title by viewModel.title.collectAsState()
    val itemsPage by viewModel.itemsPage.collectAsState()

    // Same hero treatment as MoodAndGenresScreen: this used to be a bare
    // Material TopAppBar over a plain surface, which is why the app's glass
    // stopped at the edge of this screen.
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
        // Was recomputed once for `items =` and then AGAIN inside every item's
        // listItemShape(...) call — a full distinctBy pass per row, i.e. O(n²)
        // plus a list allocation each time, on every frame of a scroll. Hoisted out
        // of the captured Box too, so the scroll rail can read its count without
        // being drawn into the backdrop layer.
        val distinctItems = remember(itemsPage) {
            itemsPage?.items.orEmpty().distinctBy { it.id }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            // Capture from a plain Box wrapping the lists, not the lists
            // themselves: they promote items to their own RenderNodes for
            // recycling, which a capture attached directly doesn't flatten.
            Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // OUTER layer, and it must come BEFORE layerBackdrop: the layer has to
            // enclose the backdrop node, or that node's draw re-runs whenever anything
            // else in the window redraws. The mini player, the playing indicator and
            // the position poll are all siblings that tick on their own schedule, and
            // each tick was re-recording this entire list. MainActivity pairs an outer
            // and inner layer for exactly this; the screen-local backdrops were left
            // with only the inner half.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            // Content becomes ONE cached RenderNode, so the backdrop's
            // layer.record { drawContent() } records a single drawRenderNode
            // instead of re-issuing every op in the list.
            .graphicsLayer()) {
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
                        // No bounce here: the top pull drives the hero zoom instead.
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
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null,
                                        )
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

                                            is AlbumItem -> navController.navigate("album/${item.id}")
                                            is ArtistItem -> navController.navigate("artist/${item.id}")
                                            is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
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
                        // No bounce here: the top pull drives the hero zoom instead.
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

                                                is AlbumItem -> navController.navigate("album/${item.id}")
                                                is ArtistItem -> navController.navigate("artist/${item.id}")
                                                is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
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
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
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

