/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.utils.YouTubeUrlParser
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalIsPlayerExpanded
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.utils.rememberGridColumns
import com.convxy.music.constants.PauseSearchHistoryKey
import com.convxy.music.constants.LocalOnlyModeKey
import com.convxy.music.constants.SearchSource
import com.convxy.music.db.entities.SearchHistory
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.MoodAndGenresViewModel
import com.convxy.music.viewmodels.ExploreViewModel
import com.convxy.music.ui.screens.search.suggestions.SuggestionsTabContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.YouTubeGridItem
import com.convxy.music.ui.menu.YouTubeAlbumMenu
import com.convxy.music.constants.GridThumbnailHeight
import com.convxy.music.constants.GridItemsSizeKey
import com.convxy.music.constants.GridItemSize
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.HomeImageBackground
import com.convxy.music.ui.component.rememberAppBackgroundColor
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent

import com.convxy.music.ui.component.LocalNavSearchState
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.backdrop.catalog.components.LiquidBottomTab
import com.convxy.music.ui.component.backdrop.catalog.components.LiquidBottomTabs
import androidx.compose.ui.text.font.FontWeight
import com.convxy.music.ui.component.GlassCircleButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    pureBlack: Boolean
) {
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isPlayerExpanded = LocalIsPlayerExpanded.current
    val playerConnection = LocalPlayerConnection.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // The nav bar owns the actual search text field now (see NavBarSearchInputBar
    // in FloatingNavBar.kt) — this screen just reads the live query to filter results.
    val navSearch = LocalNavSearchState.current
    val pauseSearchHistory by rememberPreference(PauseSearchHistoryKey, defaultValue = false)

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    // The Explore/Suggestions/Albums tabs are all YouTube browse pages.
    val (localOnly) = rememberPreference(LocalOnlyModeKey, false)
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var showSearchContent by remember { mutableStateOf(false) }

    val onSearchFromSuggestion: (String) -> Unit = remember {
        { searchQuery ->
            if (searchQuery.isNotEmpty()) {
                focusManager.clearFocus()
                when (val parsedUrl = YouTubeUrlParser.parse(searchQuery)) {
                    is YouTubeUrlParser.ParsedUrl.Video -> {
                        playerConnection?.playQueue(
                            YouTubeQueue(WatchEndpoint(videoId = parsedUrl.id)),
                        )
                    }
                    is YouTubeUrlParser.ParsedUrl.Artist -> {
                        navController.navigate("artist/${parsedUrl.id}")
                    }
                    // Regular-YouTube channel/playlist links open the native
                    // YouTube section instead of being searched as text.
                    is YouTubeUrlParser.ParsedUrl.Channel -> {
                        navController.navigate("youtube_channel/${parsedUrl.id}")
                    }
                    is YouTubeUrlParser.ParsedUrl.Playlist -> {
                        navController.navigate("youtube_playlist/${parsedUrl.id}")
                    }
                    null -> {
                        navController.navigate("search/${URLEncoder.encode(searchQuery, "UTF-8")}")
                    }
                }
                if (!pauseSearchHistory) {
                    coroutineScope.launch(Dispatchers.IO) {
                        database.query {
                            insert(SearchHistory(query = searchQuery))
                        }
                    }
                }
            }
        }
    }

    // Fixed elevated token, not derived from any specific result's thumbnail —
    // safe to override with the user's own picked theme color.
    val tint = rememberAppBackgroundColor(AppleTokens.BgElevated)
    val onTint = AppleTokens.onColor(tint)
    val heroSource = rememberHeroSource(staticArt = null)
    val heroBackdrop = rememberLayerBackdrop {
        drawRect(tint)
        drawContent()
    }
    val backdropFreeze = rememberBackdropFreeze()

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        showDefaultIcon = false,
        modifier = Modifier.fillMaxSize(),
    ) {
      HomeImageBackground()
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val accent = com.convxy.music.ui.theme.LocalAccentColor.current

        // Space for the floating side bar in tab view (0 otherwise). The hero
        // background stays full-bleed behind the Scaffold; only the foreground —
        // title, search pill, tabs and results — shifts clear of the panel. The
        // result lists inset bottom-only, so nothing double-pads.
        val sideInset = LocalPlayerAwareWindowInsets.current
            .asPaddingValues()
            .calculateStartPadding(LocalLayoutDirection.current)

        Scaffold(
            modifier = Modifier.padding(start = sideInset),
            topBar = {
                Column(
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    LargeScreenTitle(
                        title = stringResource(R.string.search),
                        color = onTint,
                    )

                    AnimatedVisibility(
                        visible = navSearch.query.text.isEmpty() && !localOnly,
                        enter = expandVertically(animationSpec = tween(durationMillis = 245, easing = FastOutSlowInEasing)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 245, easing = FastOutSlowInEasing)) + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Wide floating pill, swipeable with the same drag-puck
                            // ("pluck") feel as the floating nav bar's own tab selector.
                            LiquidBottomTabs(
                                selectedTabIndex = { selectedTabIndex },
                                onTabSelected = { selectedTabIndex = it },
                                backdrop = heroBackdrop,
                                tabsCount = 3,
                                // Blend into this screen's own tint instead of the
                                // component's default near-black glass fill.
                                containerColor = onTint.copy(alpha = 0.12f),
                                height = 44.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp , vertical = 16.dp)
                            ) {
                                LiquidBottomTab(onClick = { selectedTabIndex = 0 }) {
                                    Text(
                                        text = stringResource(R.string.tab_explore),
                                        color = if (selectedTabIndex == 0) accent else onTint.copy(alpha = 0.6f),
                                    )
                                }
                                LiquidBottomTab(onClick = { selectedTabIndex = 1 }) {
                                    Text(
                                        text = stringResource(R.string.tab_Suggestions),
                                        color = if (selectedTabIndex == 1) accent else onTint.copy(alpha = 0.6f),
                                    )
                                }
                                LiquidBottomTab(onClick = { selectedTabIndex = 2 }) {
                                    Text(
                                        text = stringResource(R.string.tab_album),
                                        color = if (selectedTabIndex == 2) accent else onTint.copy(alpha = 0.6f),
                                    )
                                }
                            }
                    }
                }
                }
            },
            // The bottom search pill used to live here — now it's part of the
            // floating nav bar itself (NavBarSearchInputBar in FloatingNavBar.kt),
            // which stays mounted across this whole screen instead of being owned
            // by it. See PLAN_search_navbar.md.
            containerColor = Color.Transparent
        ) { paddingValues ->
            val bottomPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()
            
            Box(
                modifier = Modifier
                    .nestedScroll(backdropFreeze.connection)

                    // OUTER layer, and it must come BEFORE layerBackdrop: the layer has to
                    // enclose the backdrop node, or that node's draw re-runs whenever anything
                    // else in the window redraws. The mini player, the playing indicator and
                    // the position poll are all siblings that tick on their own schedule, and
                    // each tick was re-recording this entire list. MainActivity pairs an outer
                    // and inner layer for exactly this; the screen-local backdrops were left
                    // with only the inner half.
                    .graphicsLayer()
                    .layerBackdrop(heroBackdrop, frozen = backdropFreeze.frozen)
                    // Content becomes ONE cached RenderNode, so the backdrop's
                    // layer.record { drawContent() } records a single
                    // drawRenderNode instead of re-issuing every op below.
                    .graphicsLayer()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                    )
                    .fillMaxSize()
            ) {
                if (navSearch.query.text.isEmpty() && !localOnly) {
                    val tabPadding = PaddingValues(bottom = bottomPadding + 50.dp)
                    when (selectedTabIndex) {
                        0 -> ExploreTabContent(navController = navController, contentPadding = tabPadding)
                        1 -> SuggestionsTabContent(navController = navController, contentPadding = tabPadding)
                        2 -> AlbumsTabContent(navController = navController, contentPadding = tabPadding)
                    }
                } else {
                    when (navSearch.searchSource) {
                        SearchSource.LOCAL -> LocalSearchScreen(
                            query = navSearch.query.text,
                            navController = navController,
                            onDismiss = { },
                            pureBlack = pureBlack
                        )
                        SearchSource.ONLINE -> OnlineSearchScreen(
                            query = navSearch.query.text,
                            onQueryChange = navSearch.onQueryChange,
                            navController = navController,
                            onSearch = { onSearchFromSuggestion(it) },
                            onDismiss = { },
                            pureBlack = pureBlack,
                            contentColor = onTint,
                        )
                    }
                }
            }
        }
      }
    }

    // Handle lifecycle events to manage keyboard visibility. No auto-focus on
    // launch anymore — the nav bar's search bar starts unfocused (first tap
    // navigates here; a second tap on the bar opens the keyboard).
    DisposableEffect(lifecycleOwner, isPlayerExpanded) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isPlayerExpanded) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        navSearch.onCloseKeyboard()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    navSearch.onCloseKeyboard()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (isPlayerExpanded) {
            keyboardController?.hide()
            focusManager.clearFocus()
            navSearch.onCloseKeyboard()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun ExploreTabContent(
    navController: NavController,
    viewModel: MoodAndGenresViewModel = hiltViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val moodAndGenresList by viewModel.moodAndGenres.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        moodAndGenresList?.forEach { section ->
            item {
                NavigationTitle(title = section.title)
            }
            val rows = section.items.chunked(2)
            items(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                ) {
                    row.forEach { item ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .weight(1f)
                                .padding(6.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(LocalContentColor.current.copy(alpha = 0.1f))
                                .bounceClick {
                                    navController.navigate(
                                        "youtube_browse/${item.endpoint.browseId}?params=${item.endpoint.params}"
                                    )
                                }
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    repeat(2 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (moodAndGenresList == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator()
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun AlbumsTabContent(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current
    val mediaMetadata by (playerConnection?.mediaMetadata?.collectAsState() ?: remember { mutableStateOf(null) })
    val isPlaying by (playerConnection?.isEffectivelyPlaying?.collectAsState() ?: remember { mutableStateOf(false) })
    val coroutineScope = rememberCoroutineScope()
    
    val explorePage by viewModel.explorePage.collectAsState()
    val newReleaseAlbums = explorePage?.newReleaseAlbums

    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    if (newReleaseAlbums == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = rememberGridColumns(),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 12.dp,
                end = 12.dp,
                bottom = 12.dp + contentPadding.calculateBottomPadding()
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = newReleaseAlbums.distinctBy { it.id },
                key = { it.id }
            ) { album ->
                YouTubeGridItem(
                    item = album,
                    isActive = mediaMetadata?.album?.id == album.id,
                    isPlaying = isPlaying,
                    coroutineScope = coroutineScope,
                    fillMaxWidth = true,
                    modifier = Modifier
                        .combinedBounceClick(
                            onClick = {
                                navController.navigate("album/${album.id}")
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                    YouTubeAlbumMenu(
                                        albumItem = album,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        )
                )
            }
        }
    }
}

@Composable
fun DynamicSearchPlaceholder(searchSource: SearchSource, style: TextStyle) {
    Text(
        text = stringResource(
            when (searchSource) {
                SearchSource.ONLINE -> R.string.search_yt_music
                SearchSource.LOCAL -> R.string.search_library
            }
        ),
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
