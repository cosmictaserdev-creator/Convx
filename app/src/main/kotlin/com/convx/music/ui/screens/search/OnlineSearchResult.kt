/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.search

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.ui.res.painterResource
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import androidx.compose.ui.graphics.Brush
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import com.music.innertube.SearchFilter.Companion.FILTER_ALBUM
import com.music.innertube.SearchFilter.Companion.FILTER_ARTIST
import com.music.innertube.SearchFilter.Companion.FILTER_COMMUNITY_PLAYLIST
import com.music.innertube.SearchFilter.Companion.FILTER_FEATURED_PLAYLIST
import com.music.innertube.SearchFilter.Companion.FILTER_SONG
import com.music.innertube.SearchFilter.Companion.FILTER_VIDEO
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.MiniPlayerBottomSpacing
import com.convx.music.constants.MiniPlayerHeight
import com.convx.music.constants.NavigationBarHeight
import com.convx.music.playback.queues.searchResultQueue
import com.convx.music.ui.component.ChipsRow
import com.convx.music.ui.component.EmptyPlaceholder
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.LocalNavSearchState
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.YouTubeListItem
import com.convx.music.ui.component.HomeImageBackground
import com.convx.music.ui.component.hasCustomHomeBackground
import com.convx.music.ui.component.rememberAppBackgroundColor
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.text.font.FontWeight
import com.convx.music.ui.component.shimmer.ListItemPlaceHolder
import com.convx.music.ui.component.shimmer.ShimmerHost
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.utils.listItemShape
import com.convx.music.viewmodels.OnlineSearchViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.rememberHeroZoom
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnlineSearchResult(
    navController: NavController,
    viewModel: OnlineSearchViewModel = koinViewModel(),
    pureBlack: Boolean = false
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Pull-to-refresh only: no hero artwork here, so heroZoom.scale goes unread
    // and the modifier contributes just the rubber-band stretch.
    val heroZoom = rememberHeroZoom()

    // The nav bar owns the actual search text field/keyboard now (see
    // NavBarSearchInputBar in FloatingNavBar.kt) — this screen just reads the
    // live query to filter/display results.
    val navSearch = LocalNavSearchState.current

    // Deep-link safety net: if this screen was reached without going through
    // the nav bar's own field first (e.g. a direct navigate to search/{query}
    // from search history elsewhere), seed the shared query from the nav arg.
    val encodedQuery = navController.currentBackStackEntry?.arguments?.getString("query") ?: ""
    val decodedQuery = remember(encodedQuery) {
        try {
            URLDecoder.decode(encodedQuery, "UTF-8")
        } catch (e: Exception) {
            encodedQuery
        }
    }
    LaunchedEffect(decodedQuery) {
        if (navSearch.query.text != decodedQuery) {
            navSearch.onQueryChange(TextFieldValue(decodedQuery, TextRange(decodedQuery.length)))
        }
    }

    val searchFilter by viewModel.filter.collectAsStateWithLifecycle()
    val searchSummary = viewModel.summaryPage
    val itemsPage by remember(searchFilter) {
        derivedStateOf {
            searchFilter?.value?.let {
                viewModel.viewStateMap[it]
            }
        }
    }
    val isLoadingResults = if (searchFilter == null) {
        searchSummary == null
    } else {
        itemsPage == null
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "loading" }
        }.collect { shouldLoadMore ->
            if (!shouldLoadMore) return@collect
            viewModel.loadMore()
        }
    }

    // Sibling results let a song tap queue this result set instead of starting one-song radio.
    val searchedSongsTitle = stringResource(R.string.queue_searched_songs)
    val ytItemContent: @Composable LazyItemScope.(YTItem, Int, List<YTItem>) -> Unit = { item: YTItem, index: Int, siblings: List<YTItem> ->
        val size = siblings.size
        val longClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
        }
        YouTubeListItem(
            item = item,
            isActive = when (item) {
                is SongItem -> mediaMetadata?.id == item.id
                is AlbumItem -> mediaMetadata?.album?.id == item.id
                else -> false
            },
            isPlaying = isPlaying,
            flat = true,
            shape = listItemShape(index, size),
            trailingContent = {
                IconButton(
                    onClick = longClick,
                ) {
                    SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                }
            },
            modifier =
            Modifier
                .combinedBounceClick(
                    onClick = {
                        when (item) {
                            is SongItem -> {
                                if (item.id == mediaMetadata?.id) {
                                    playerConnection.togglePlayPause()
                                } else {
                                    playerConnection.playQueue(searchResultQueue(item, siblings, searchedSongsTitle))
                                }
                            }

                            is AlbumItem -> navController.navigate(albumRoute(item.id, item.thumbnail))
                            is ArtistItem -> navController.navigate(artistRoute(item.id, item.thumbnail))
                            is PlaylistItem -> navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
                        }
                    },
                    onLongClick = longClick,
                )
                .animateItem(),
        )
    }

    val heroUrl = remember(searchSummary, itemsPage) {
        if (searchFilter == null) {
            searchSummary?.summaries?.firstOrNull()?.items?.firstOrNull()?.thumbnail
        } else {
            itemsPage?.items?.firstOrNull()?.thumbnail
        }
    }
    val tint = rememberHeroTint(heroUrl)
    val onTint = AppleTokens.onColor(tint)

    // The suggestions/"hint" overlay below (shown while re-typing on this results
    // page) tints from the app's home background image, not this page's own
    // search-result hero — matching the initial SearchScreen's header tint.
    val hintTint = rememberAppBackgroundColor(AppTheme.colorScheme.background)
    val hintOnTint = AppleTokens.onColor(hintTint)

    val heroBackdrop = rememberLayerBackdrop()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
    ) {
        if (hasCustomHomeBackground()) {
            // Matches Home/Library: the user's own picture, not this screen's
            // auto-generated hero blur, once a custom background is set.
            HomeImageBackground(withGradient = true)
        } else {
            // Blurred hero artwork background with smooth unified tone
            if (heroUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(heroUrl)
                        .size(544, 544)
                        .allowHardware(false)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        // Bake the blur once: HWUI re-runs a RenderEffect whenever the clip
                        // it is drawn under changes, and with glass on this backdrop is
                        // drawn under two (screen + glass capture) every frame.
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .blur(120.dp)
                        .graphicsLayer { alpha = 0.55f }
                )
            }
            // Smooth uniform dark overlay for clean contrast across entire screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )
        }

      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(appTopBarWindowInsets())
        ) {
            // Search input is owned by the persistent floating nav bar.

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // No opaque wrapper, no Material chip fills — just the row over
                    // the screen's own blurred hero backdrop, tinted to match.
                    AppleScopePills(
                        pills = listOf(
                            null to stringResource(R.string.filter_all),
                            FILTER_SONG to stringResource(R.string.filter_songs),
                            FILTER_VIDEO to stringResource(R.string.filter_videos),
                            FILTER_ALBUM to stringResource(R.string.filter_albums),
                            FILTER_ARTIST to stringResource(R.string.filter_artists),
                            FILTER_COMMUNITY_PLAYLIST to stringResource(R.string.filter_community_playlists),
                            FILTER_FEATURED_PLAYLIST to stringResource(R.string.filter_featured_playlists),
                        ),
                        selected = searchFilter,
                        onSelect = {
                            if (viewModel.filter.value != it) {
                                viewModel.filter.value = it
                            }
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        },
                    )

                    LazyColumn(
                        state = lazyListState,
                        overscrollEffect = heroZoom.listOverscroll(),
                        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heroPullZoom(heroZoom)
                    ) {
                        if (searchFilter == null) {
                            searchSummary?.summaries?.forEach { summary ->
                                item {
                                    NavigationTitle(
                                        title = summary.title
                                    )
                                }

                                itemsIndexed(
                                    items = summary.items,
                                    key = { index, item -> "${summary.title}/${item.id}/$index" },
                                    contentType = { _, _ -> "yt_item_row" },
                                ) { index, item ->
                                    ytItemContent(item, index, summary.items)
                                }
                            }

                            if (searchSummary?.summaries?.isEmpty() == true) {
                                item {
                                    EmptyPlaceholder(
                                        icon = R.drawable.search,
                                        text = stringResource(R.string.no_results_found),
                                        modifier = Modifier.padding(top = 100.dp)
                                    )
                                }
                            }
                        } else {
                            // Was recomputed inside every row for the count argument,
                            // i.e. a full distinctBy pass per row.
                            val distinctItems = itemsPage?.items.orEmpty().distinctBy { it.id }
                            itemsIndexed(
                                items = distinctItems,
                                key = { _, it -> "filtered_${it.id}" },
                                contentType = { _, _ -> "yt_item_row" },
                            ) { index, item ->
                                ytItemContent(item, index, distinctItems)
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

                            if (itemsPage?.items?.isEmpty() == true) {
                                item {
                                    EmptyPlaceholder(
                                        icon = R.drawable.search,
                                        text = stringResource(R.string.no_results_found),
                                        modifier = Modifier.padding(top = 100.dp)
                                    )
                                }
                            }
                        }

                        if (isLoadingResults) {
                            item {
                                ShimmerHost {
                                    repeat(8) {
                                        ListItemPlaceHolder()
                                    }
                                }
                            }
                        }

                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(MiniPlayerHeight + MiniPlayerBottomSpacing + NavigationBarHeight + 50.dp))
                        }
                    }
                }
                if (navSearch.keyboardActive) {
                    // This search overlay covers the results list, so give it an opaque background.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(hintTint)
                    ) {
                        HomeImageBackground()
                        OnlineSearchScreen(
                            query = navSearch.query.text,
                            onQueryChange = navSearch.onQueryChange,
                            navController = navController,
                            onSearch = navSearch.onSubmit,
                            onDismiss = navSearch.onCloseKeyboard,
                            pureBlack = pureBlack,
                            contentColor = hintOnTint,
                        )
                    }
                }
            }
        }
      }
    }
}

