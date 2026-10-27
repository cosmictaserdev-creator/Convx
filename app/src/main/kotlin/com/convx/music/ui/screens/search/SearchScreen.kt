/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.search

import com.convx.music.utils.openSearchResults

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
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
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.role
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.ios.foundation.iosGlass
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.utils.YouTubeUrlParser
import com.convx.music.LocalIsPlayerExpanded
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.SearchSource
import com.convx.music.search.SearchHistoryCommands
import com.convx.music.search.SearchHistorySettingsRepository
import com.convx.music.search.shouldRecordSearchQuery
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.youtubeBrowseRoute
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.viewmodels.MoodAndGenresViewModel
import com.convx.music.viewmodels.ExploreViewModel
import com.convx.music.ui.screens.search.suggestions.SuggestionsTabContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridItemSize
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.HomeImageBackground
import com.convx.music.ui.component.rememberAppBackgroundColor
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent

import com.convx.music.ui.component.LocalNavSearchState
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.catalog.components.LiquidBottomTab
import com.convx.music.ui.component.backdrop.catalog.components.LiquidBottomTabs
import androidx.compose.ui.text.font.FontWeight
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.TopFadeBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.offset
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    pureBlack: Boolean,
    accountImageUrl: String? = null,
    accountName: String = "",
    onAccountClick: () -> Unit = { navController.navigate(com.convx.music.ui.screens.Screens.Settings.route) },
) {
    val searchHistoryCommands: SearchHistoryCommands = koinInject()
    val searchHistorySettingsRepository: SearchHistorySettingsRepository = koinInject()
    val searchHistorySettingsFlow = remember(searchHistorySettingsRepository) {
        searchHistorySettingsRepository.observe()
    }
    val searchHistorySettings by searchHistorySettingsFlow
        .collectAsStateWithLifecycle(initialValue = null)
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isPlayerExpanded = LocalIsPlayerExpanded.current
    val playerConnection = LocalPlayerConnection.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Read the shared nav-bar query so results update as the user types.
    val navSearch = LocalNavSearchState.current

    val localOnly = com.convx.music.LocalLocalOnlyMode.current

    val onSearchFromSuggestion: (String) -> Unit = remember(
        searchHistorySettings,
        searchHistoryCommands,
        coroutineScope,
        focusManager,
        navController,
        playerConnection,
    ) {
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
                    null -> {
                        navController.openSearchResults(searchQuery)
                    }
                }
                if (shouldRecordSearchQuery(searchQuery, searchHistorySettings)) {
                    coroutineScope.launch(Dispatchers.IO) {
                        searchHistoryCommands.record(searchQuery)
                    }
                }
            }
        }
    }

    // Keep the browse page and results page on the same system background.
    val colors = com.convx.music.ui.ios.foundation.IosTheme.colors
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val tabletLayout = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600
    // The phone reference reserves 62 points above its title; retain larger cutouts.
    val titleTop = if (tabletLayout) statusTop + 10.dp else maxOf(statusTop, 62.dp)
    val bottomPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()
    val sideInset = LocalPlayerAwareWindowInsets.current
        .asPaddingValues()
        .calculateStartPadding(LocalLayoutDirection.current)
    val searching = navSearch.keyboardActive || navSearch.query.text.isNotEmpty() || localOnly

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.systemBackground),
    ) {
        if (!searching) {
            // Frosted strip under the status bar once tiles scroll up into it.
            val browseHaze = remember { HazeState() }
            Box(Modifier.fillMaxSize().hazeSource(browseHaze)) {
                AppleSearchBrowse(
                    navController = navController,
                    contentPadding = PaddingValues(start = sideInset, top = titleTop, bottom = bottomPadding + 16.dp),
                    header = { AppleSearchHeader(accountImageUrl, accountName, onAccountClick,
                        horizontalInset = 0.dp, bottomInset = 2.dp) },
                )
            }
            TopFadeBlur(
                hazeState = browseHaze,
                pageColor = colors.systemBackground,
                modifier = Modifier.align(Alignment.TopCenter),
                heightOverride = titleTop + 16.dp,
            )
        } else {
            val pageColor = colors.systemBackground
            val searchBackdrop = com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop(
                onDraw = remember(pageColor) { { drawRect(pageColor); drawContent() } },
            )
            val focusedTop = if (tabletLayout) statusTop + 10.dp else maxOf(statusTop, 58.dp)
            Column(Modifier.fillMaxSize().padding(top = focusedTop)) {
                if (navSearch.canToggleSource) {
                    SourceSwitch(
                        source = navSearch.searchSource,
                        onSelect = { if (it != navSearch.searchSource) navSearch.onToggleSource() },
                        backdrop = searchBackdrop,
                        modifier = Modifier.padding(start = sideInset + 16.dp, end = 16.dp),
                    )
                }
                Box(Modifier.weight(1f)
                    .layerBackdrop(searchBackdrop)) {
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
                            contentColor = colors.label,
                            referenceHistory = true,
                        )
                    }
                }
            }
        }
    }

    // Keep the keyboard closed on resume; the navigation bar handles search focus.
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
    viewModel: MoodAndGenresViewModel = koinViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val moodAndGenresList by viewModel.moodAndGenres.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        moodAndGenresList?.forEach { section ->
            item {
                NavigationTitle(title = section.title)
            }
            val rows = section.items.chunked(2)
            items(rows, key = { row -> row.joinToString("|") { it.endpoint.browseId } }) { row ->
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
                                    navController.navigate(youtubeBrowseRoute(item.endpoint.browseId, item.endpoint.params))
                                }
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = AppTheme.typography.labelLarge,
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
    viewModel: ExploreViewModel = koinViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current
    val mediaMetadata by (playerConnection?.mediaMetadata?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) })
    val isPlaying by (playerConnection?.isEffectivelyPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) })
    val coroutineScope = rememberCoroutineScope()
    
    val explorePage by viewModel.explorePage.collectAsStateWithLifecycle()
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
                                navController.navigate(albumRoute(album.id, album.thumbnail))
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


/** Search toolbar with a title and a 44dp profile button. */
@Composable
private fun AppleSearchHeader(
    accountImageUrl: String?,
    accountName: String,
    onAccountClick: () -> Unit,
    horizontalInset: androidx.compose.ui.unit.Dp = 16.dp,
    bottomInset: androidx.compose.ui.unit.Dp = 10.dp,
) {
    val profileDescription = stringResource(R.string.account)
    Row(
        modifier = Modifier.padding(start = horizontalInset, end = horizontalInset, bottom = bottomInset)
            .fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = stringResource(R.string.search),
            style = com.convx.music.ui.screens.library.apple.SfStyle(34.sp, 41.sp, FontWeight.Bold, 0.4.sp),
            color = com.convx.music.ui.ios.foundation.IosTheme.colors.label,
            maxLines = 1,
            modifier = Modifier.weight(1f).height(41.dp),
        )
        Box(
            Modifier.size(44.dp).clip(CircleShape)
                .iosRowPress(onClick = onAccountClick)
                .semantics { contentDescription = profileDescription },
        ) {
            com.convx.music.ui.component.AccountAvatar(accountImageUrl, accountName)
        }
    }
}

/**
 * Apple's segmented control under the field while searching: "YouTube Music" (Apple
 * Music) and "Your Library" — the catalogue, or what is on this device and saved here.
 */
@Composable
private fun SourceSwitch(
    source: SearchSource,
    onSelect: (SearchSource) -> Unit,
    backdrop: com.convx.music.ui.component.backdrop.Backdrop,
    modifier: Modifier = Modifier,
) {
    val colors = com.convx.music.ui.ios.foundation.IosTheme.colors
    val thumb = if (colors.isDark) Color(0xFF1C1C1C) else Color.White
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .iosGlass(shape = CircleShape,
                spec = com.convx.music.ui.ios.foundation.IosGlassSpec.Control,
                interactive = true, backdrop = backdrop,
                wash = if (colors.isDark) Color.Black.copy(alpha = 0.75f) else Color.Transparent),
    ) {
        val thumbWidth = (maxWidth - 20.dp) / 2 + 12.dp
        val selectionOffset = animateDpAsState(
            if (source == SearchSource.ONLINE) 4.dp else maxWidth - thumbWidth - 4.dp,
            animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.86f, stiffness = 400f),
            label = "searchSourceSelection",
        )
        Box(Modifier.align(Alignment.CenterStart).width(thumbWidth).height(42.dp)
            .graphicsLayer { translationX = selectionOffset.value.toPx() }
            .clip(CircleShape).background(thumb))
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        listOf(
            SearchSource.ONLINE to stringResource(R.string.apple_search_catalog),
            SearchSource.LOCAL to stringResource(R.string.apple_search_library),
        ).forEach { (value, label) ->
            val selected = value == source
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .iosRowPress(onClick = { onSelect(value) })
                    .semantics { this.selected = selected; role = androidx.compose.ui.semantics.Role.Tab },
            ) {
                Text(
                    text = label,
                    style = com.convx.music.ui.screens.library.apple.SfStyle(
                        14.sp, 18.sp, FontWeight.SemiBold, (-0.1).sp,
                    ),
                    color = colors.label,
                    maxLines = 1,
                )
            }
        }
        }
    }
}
