/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.search

import com.convxy.music.ui.utils.appTopBarWindowInsets
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_ALBUM
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_ARTIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_COMMUNITY_PLAYLIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_FEATURED_PLAYLIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_SONG
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_VIDEO
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.MiniPlayerBottomSpacing
import com.convxy.music.constants.MiniPlayerHeight
import com.convxy.music.constants.NavigationBarHeight
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.ChipsRow
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.LocalNavSearchState
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.YouTubeListItem
import com.convxy.music.ui.component.HomeImageBackground
import com.convxy.music.ui.component.hasCustomHomeBackground
import com.convxy.music.ui.component.rememberAppBackgroundColor
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.text.font.FontWeight
import com.convxy.music.ui.component.shimmer.ListItemPlaceHolder
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.menu.YouTubeAlbumMenu
import com.convxy.music.ui.menu.YouTubeArtistMenu
import com.convxy.music.ui.menu.YouTubePlaylistMenu
import com.convxy.music.ui.menu.YouTubeSongMenu
import com.convxy.music.utils.listItemShape
import com.convxy.music.viewmodels.OnlineSearchViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.utils.rememberHeroZoom

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnlineSearchResult(
    navController: NavController,
    viewModel: OnlineSearchViewModel = hiltViewModel(),
    pureBlack: Boolean = false
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

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

    val searchFilter by viewModel.filter.collectAsState()
    val searchSummary = viewModel.summaryPage
    val itemsPage by remember(searchFilter) {
        derivedStateOf {
            searchFilter?.value?.let {
                viewModel.viewStateMap[it]
            }
        }
    }
    
    // Suggestion states


    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "loading" }
        }.collect { shouldLoadMore ->
            if (!shouldLoadMore) return@collect
            viewModel.loadMore()
        }
    }

    val ytItemContent: @Composable LazyItemScope.(YTItem, Int, Int) -> Unit = { item: YTItem, index: Int, size: Int ->
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
            isActive =
            when (item) {
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
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                    )
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
                                    playerConnection.playQueue(
                                        YouTubeQueue(
                                            WatchEndpoint(videoId = item.id),
                                            item.toMediaMetadata()
                                        )
                                    )
                                }
                            }

                            is AlbumItem -> navController.navigate("album/${item.id}")
                            is ArtistItem -> navController.navigate("artist/${item.id}")
                            is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
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
    val hintTint = rememberAppBackgroundColor(AppleTokens.BgElevated)
    val hintOnTint = AppleTokens.onColor(hintTint)

    val heroBackdrop = rememberLayerBackdrop()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint)
    ) {
        if (hasCustomHomeBackground()) {
            // Matches Home/Library: the user's own picture, not this screen's
            // auto-generated hero blur, once a custom background is set.
            HomeImageBackground(withGradient = true)
        } else {
            // Blurred hero artwork background
            if (heroUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(heroUrl)
                        .size(100, 100)
                        .allowHardware(false)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(150.dp)
                )
            }
            // Dark overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            // Primary-color wash at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.55f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                        )
                    )
            )
        }

      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(appTopBarWindowInsets())
        ) {
            // The search bar used to live here — now it's part of the floating nav
            // bar itself (NavBarSearchInputBar in FloatingNavBar.kt), which stays
            // mounted across this whole screen. See PLAN_search_navbar.md.

            // Main content area
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // No opaque wrapper, no Material chip fills — just the row over
                    // the screen's own blurred hero backdrop, tinted to match.
                    ChipsRow(
                        chips = listOf(
                            null to stringResource(R.string.filter_all),
                            FILTER_SONG to stringResource(R.string.filter_songs),
                            FILTER_VIDEO to stringResource(R.string.filter_videos),
                            FILTER_ALBUM to stringResource(R.string.filter_albums),
                            FILTER_ARTIST to stringResource(R.string.filter_artists),
                            FILTER_COMMUNITY_PLAYLIST to stringResource(R.string.filter_community_playlists),
                            FILTER_FEATURED_PLAYLIST to stringResource(R.string.filter_featured_playlists),
                        ),
                        currentValue = searchFilter,
                        onValueUpdate = {
                            if (viewModel.filter.value != it) {
                                viewModel.filter.value = it
                            }
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        },
                        containerColor = Color.Transparent,
                        selectedContainerColor = onTint.copy(alpha = 0.2f),
                        labelColor = onTint.copy(alpha = 0.8f),
                        selectedLabelColor = onTint,
                        modifier = Modifier.fillMaxWidth()
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
                                    ytItemContent(item, index, summary.items.size)
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
                                ytItemContent(item, index, distinctItems.size)
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

                        if (searchFilter == null && searchSummary == null || searchFilter != null && itemsPage == null) {
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
                    // OnlineSearchScreen renders transparent by design (it overlays
                    // straight onto a screen's own hero blur on SearchScreen) — but
                    // here it overlays on top of the results list, not empty space,
                    // so it needs its own opaque backing or the list bleeds through.
                    // Tinted from the app's home background image (like the initial
                    // SearchScreen), not from this results page's search-result hero.
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

