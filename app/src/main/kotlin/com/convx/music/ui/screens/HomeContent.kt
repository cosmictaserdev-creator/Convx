package com.convx.music.ui.screens

import com.convx.music.ui.component.tabHeaderTopPadding
import com.music.innertube.pages.HomePage
import com.convx.music.viewmodels.HomeUiState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import com.convx.music.catalog.HomeSection
import com.convx.music.ui.ios.component.AppleContextMenu
import com.convx.music.ui.ios.component.IosGlyph
import com.convx.music.ui.ios.component.IosMenuItem
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.screens.library.apple.Cover
import com.convx.music.ui.screens.library.apple.SfStyle
import com.convx.music.ui.screens.library.apple.songMenu
import com.convx.music.ui.menu.AddToPlaylistDialog
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.Intent
import com.composables.sfsymbols.monochrome.SFSquareAndArrowUp
import com.composables.sfsymbols.monochrome.SFTextLineFirstAndArrowtriangleForward
import com.composables.sfsymbols.monochrome.SFTextLineLastAndArrowtriangleForward
import com.composables.sfsymbols.monochrome.SFDotRadiowavesLeftAndRight
import com.composables.sfsymbols.monochrome.SFPlay
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFSquareStack
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFEllipsis
import com.convx.music.ui.utils.sharedArtworkSource
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import com.convx.music.constants.GridCardArtShape
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.LocalItemPrefs
import com.convx.music.ui.component.BlurredBackdropImage
import com.convx.music.ui.component.flipAndMultiply
import com.convx.music.ui.utils.rememberGridSpacing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import com.convx.music.ui.component.ConvxPageHeader
import com.convx.music.ui.component.TabHeaderTop
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ButtonDefaults
import com.convx.music.ui.ios.compat.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.surfaceColorAtElevation
import com.convx.music.ui.ios.compat.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.request.ImageRequest
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.MoodAndGenres
import com.music.innertube.utils.parseCookieString
import com.music.innertube.YouTube
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.HomeSignInCardDismissedKey
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.constants.LocalSongSortDescendingKey
import com.convx.music.constants.HideHomeFavoriteIconKey
import com.convx.music.constants.LocalSongSortTypeKey
import com.convx.music.constants.RandomizeHomeOrderKey
import com.convx.music.constants.SongSortType
import com.convx.music.constants.ShowHomeFabKey
import com.convx.music.ui.component.homeCardRim
import com.convx.music.ui.component.rememberHomeCardShape
import com.convx.music.constants.HomeHeroCardEnabledKey
import com.convx.music.constants.HomeSectionHiddenKey
import com.convx.music.constants.HomeSectionOrderKey
import com.convx.music.constants.ShowHomeMoodFiltersKey
import com.convx.music.constants.ThumbnailCornerRadius
import com.convx.music.db.MusicDatabase
import com.convx.music.ui.component.SpotlightCard
import com.convx.music.ui.component.SpotlightCardArtworkSize
import com.convx.music.ui.component.SpotlightCardInfoHeight
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.plusStart
import com.convx.music.ui.utils.bounceClick
import com.convx.music.utils.LocalFolderIndex
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.localPlaylistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.LocalDatabase
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.models.MediaMetadata
import com.convx.music.models.SimilarRecommendation
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.LocalAlbumRadio
import com.convx.music.playback.queues.YouTubeAlbumRadio
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.R
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFXmark
import com.composables.sfsymbols.monochrome.SFMusicNote
import com.composables.sfsymbols.monochrome.SFPersonCropCircle
import com.convx.music.ui.component.AlbumGridItem
import com.convx.music.ui.component.ListItem
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.ArtistGridItem
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.HomeHeroCard
import com.convx.music.ui.component.HomeImageBackground
import com.convx.music.ui.component.hasCustomHomeBackground
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.MenuState
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.YouTubeGridItem
import com.music.innertube.pages.ChartsPage.ChartType
import com.convx.music.viewmodels.ChartsViewModel
import com.convx.music.viewmodels.NewReleaseViewModel
import com.convx.music.ui.component.rememberAppBackgroundColor
import com.convx.music.ui.component.rememberTitleCollapseProgress
import com.convx.music.ui.component.shimmer.GridItemPlaceHolder
import com.convx.music.ui.component.shimmer.ShimmerHost
import com.convx.music.ui.component.shimmer.TextPlaceholder
import com.convx.music.ui.component.SongGridItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.ArtistMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.AppPage
import com.convx.music.ui.theme.LocalPageMetrics
import com.convx.music.ui.theme.LocalMetadataColor
import com.convx.music.ui.theme.BrandName
import com.convx.music.ui.theme.rememberBrandFontFamily
import com.convx.music.ui.theme.rememberGlobalAccentColors
import com.convx.music.utils.listItemShape
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.CommunityPlaylistItem
import com.convx.music.viewmodels.HomeViewModel
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.convx.music.viewmodels.DailyDiscoverItem

/** Everything {@link HomeContent} draws, in one immutable snapshot built by the route. */
@Immutable
internal class HomeContentState(
    val home: HomeUiState,
    val shownChip: HomePage.Chip?,
    val pendingChip: HomePage.Chip?,
    val sections: List<HomeSection>,
    val topPicks: List<YTItem>,
    val trending: List<YTItem>,
    val newReleaseAlbums: List<YTItem>,
    val quickPicksSongMap: Map<String, Song>,
    val forgottenFavoritesSongMap: Map<String, Song>,
    val isLoggedIn: Boolean,
    val signInCardDismissed: Boolean,
    val wrappedDataReady: Boolean,
    val avatarUrl: String?,
    val prefs: HomePrefs,
)

/** What the content can ask for; the route decides how each one is carried out. */
@Immutable
internal class HomeActions(
    val onRefresh: () -> Unit,
    val onToggleChip: (HomePage.Chip?) -> Unit,
    val onSignIn: () -> Unit,
    val onDismissSignIn: () -> Unit,
    val onOpenWrapped: () -> Unit,
    val onOpenRecognition: () -> Unit,
    val onLuckyPlay: () -> Unit,
)

/**
 * Home's page: pull-to-refresh, the feed list and the shuffle button. It owns no view model,
 * player or navigator, so it renders from [state] alone.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HomeContent(
    state: HomeContentState,
    actions: HomeActions,
    deps: HomeSectionDeps,
    lazylistState: LazyListState,
    mediaMetadataState: State<MediaMetadata?>,
    isPlayingState: State<Boolean>,
    randomizeJob: MutableState<Job?>,
) {
    val homeState = state.home
    val homePage = homeState.homePage
    val selectedChip = homeState.selectedChip
    val pendingChip = state.pendingChip
    val shownChip = state.shownChip
    val isLoading = homeState.isLoading
    val isRefreshing = homeState.isRefreshing
    val allLocalItems = homeState.allLocalItems
    val allYtItems = homeState.allYtItems
    val isLoggedIn = state.isLoggedIn
    val signInCardDismissed = state.signInCardDismissed
    val shouldShowWrappedCard = homeState.showWrappedCard
    val isWrappedDataReady = state.wrappedDataReady
    val homePrefs = state.prefs
    val showMoodFilters = homePrefs.moodFilters
    val homeSections = state.sections
    val topPicks = state.topPicks
    val trending = state.trending
    val newReleaseAlbums = state.newReleaseAlbums
    val quickPicksSongMap = state.quickPicksSongMap
    val forgottenFavoritesSongMap = state.forgottenFavoritesSongMap
    val url = state.avatarUrl
    val topPicksTitle = stringResource(R.string.top_picks_for_you)
    val newReleaseTitle = stringResource(R.string.new_release_albums)
    val trendingTitle = stringResource(R.string.trending)

    val pullRefreshState = rememberPullToRefreshState()
    val filterScrollState = rememberScrollState()
    val feedScroll = remember { mutableFloatStateOf(0f) }
    val feedScrollTracker = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                feedScroll.floatValue = (feedScroll.floatValue - consumed.y).coerceAtLeast(0f)
                return Offset.Zero
            }
        }
    }
    var previousChip by remember { mutableStateOf(shownChip) }
    LaunchedEffect(shownChip) {
        if (shownChip != previousChip) {
            lazylistState.scrollToItem(0)
            feedScroll.floatValue = 0f
            previousChip = shownChip
        }
    }

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = actions.onRefresh,
        indicator = {
            // Compose the animated indicator only while it is visible to avoid idle redraws.
            if (isRefreshing || pullRefreshState.distanceFraction > 0f) {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
                )
            }
        }
    ) {
        // The shared top bar renders the title and avatar; this screen starts below it.
        Box(Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalPageMetrics provides AppPage.Metrics,
            LocalMetadataColor provides AppTheme.colorScheme.onSurfaceVariant,
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                val pageBackground = rememberAppBackgroundColor(AppTheme.colorScheme.surface)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(pageBackground)
                )
                HomeImageBackground(contentLoaded = homePage != null)
                if (!hasCustomHomeBackground()) {
                    HomeChipBackdrop(
                        url = homePage?.background?.takeIf { selectedChip != null && pendingChip == null },
                        fadeTo = pageBackground,
                        scrolledPx = {
                            val atTop = lazylistState.firstVisibleItemIndex == 0 && lazylistState.firstVisibleItemScrollOffset == 0
                            if (atTop) 0f else feedScroll.floatValue
                        },
                    )
                }

                // Space the floating side bar occupies. The vertical list keeps it as
                // content padding so rows stay clear of the panel; the carousels below
                // give it back with bleedStart so their cards run underneath the glass
                // instead of stopping at its edge.
                val sideInset = LocalPlayerAwareWindowInsets.current
                    .asPaddingValues()
                    .calculateStartPadding(LocalLayoutDirection.current)

                LazyColumn(
                    state = lazylistState,
                    modifier = Modifier.nestedScroll(feedScrollTracker),
                    // Sections had nothing but NavigationTitle's own 12dp padding
                    // between them — ran into each other, felt cramped.
                    verticalArrangement = Arrangement.spacedBy(AppPage.ItemSpacing),
                    contentPadding = run {
                        val ins = LocalPlayerAwareWindowInsets.current.asPaddingValues()
                        val dir = LocalLayoutDirection.current
                        PaddingValues(
                            start = ins.calculateStartPadding(dir),
                            top = tabHeaderTopPadding(),
                            end = ins.calculateRightPadding(dir),
                            bottom = ins.calculateBottomPadding(),
                        )
                    },
                ) {
                    item(key = "convx_header", contentType = "convx_header") {
                        ConvxPageHeader(fontFamily = com.convx.music.designsystem.convxLogoFontFamily(), color = AppTheme.colorScheme.onSurface)
                    }

                    // Apple Music's "Transfer music" card, pointed at our one import path:
                    // signing in to YouTube Music. Gone for good once dismissed or signed in.
                    if (
                        selectedChip == null && !isLoggedIn && !signInCardDismissed
                    ) {
                        item(key = "sign_in_card", contentType = "sign_in_card") {
                            HomeSignInCard(
                                onGetStarted = actions.onSignIn,
                                onDismiss = actions.onDismissSignIn,
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    item(key = "home_feed_filters", contentType = "home_feed_filters") {
                        HomeFeedFilterRow(
                            moodChips = if (showMoodFilters) homePage?.chips.orEmpty().filter { it.endpoint?.browseId?.isNotBlank() == true } else emptyList(),
                            selectedChip = shownChip,
                            onSelected = actions.onToggleChip,
                            scrollState = filterScrollState,
                            sideInset = sideInset,
                            modifier = Modifier.animateItem(),
                        )
                    }

                    if (selectedChip == null) {
                        item(key = "wrapped_card", contentType = "wrapped_card") {
                            AnimatedVisibility(visible = shouldShowWrappedCard) {
                                val wrappedCardShape = rememberHomeCardShape()
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .homeCardRim(wrappedCardShape),
                                    colors = CardDefaults.cardColors(
                                        containerColor = LocalContentColor.current.copy(alpha = 0.1f),
                                    ),
                                    shape = wrappedCardShape
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isWrappedDataReady) {
                                            val bbhFont = try {
                                                FontFamily(Font(R.font.bbh_bartle_regular))
                                            } catch (e: Exception) {
                                                FontFamily.Default
                                            }
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.wrapped_ready_title),
                                                    style = AppTheme.typography.headlineLarge.copy(
                                                        fontFamily = bbhFont,
                                                        textAlign = TextAlign.Center
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = stringResource(R.string.wrapped_ready_subtitle),
                                                    style = AppTheme.typography.bodyLarge.copy(
                                                        textAlign = TextAlign.Center
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Button(onClick = {
                                                    actions.onOpenWrapped()
                                                }) {
                                                    Text(stringResource(R.string.open))
                                                }
                                            }
                                        } else {
                                            com.convx.music.ui.component.ShimmerBox(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                                    .height(160.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pendingChip != null) {
                        // The shimmer below stands in for the chip's feed until it arrives.
                    } else if (selectedChip != null) {
                        chipFeedContent(deps, homePage)
                    } else {
                        homeSections.forEach { section ->
                        when (section) {
                        HomeSection.TopPicks -> figmaRail(
                            deps = deps,
                            mediaMetadata = mediaMetadataState,
                            isPlaying = isPlayingState,
                            key = section.id,
                            title = topPicksTitle,
                            route = "charts_screen",
                            items = topPicks.ifEmpty { trending }.ifEmpty { newReleaseAlbums },
                            hero = true,
                        )
                        HomeSection.NewReleases -> figmaRail(
                            deps, mediaMetadataState, isPlayingState, section.id,
                            newReleaseTitle, "new_release",
                            if (topPicks.isNotEmpty() || trending.isNotEmpty()) newReleaseAlbums else emptyList(),
                        )
                        HomeSection.Trending -> figmaRail(
                            deps, mediaMetadataState, isPlayingState, section.id,
                            trendingTitle, "charts_screen",
                            if (topPicks.isNotEmpty()) trending else emptyList(),
                        )
                        else -> homeSectionsContent(
                            sections = listOf(section),
                            deps = deps,
                            ui = homeState,
                            mediaMetadata = mediaMetadataState,
                            isPlaying = isPlayingState,
                            randomizeJob = randomizeJob,
                            quickPicksSongMap = quickPicksSongMap,
                            forgottenFavoritesSongMap = forgottenFavoritesSongMap,
                            url = url,
                        )
                        }
                        }
                    }
                    // Show this infinite shimmer only during a fetch; a continuation can remain available indefinitely.
                    if (isLoading || pendingChip != null) {
                        item(key = "loading_shimmer", contentType = "shimmer") {
                            ShimmerHost(
                                modifier = Modifier.animateItem()
                            ) {
                                repeat(2) {
                                    TextPlaceholder(
                                        height = 36.dp,
                                        modifier = Modifier
                                            .padding(AppPage.Gutter)
                                            .width(250.dp),
                                    )
                                    LazyRow(
                                        modifier = Modifier.bleedStart(sideInset),
                                        contentPadding = PaddingValues(horizontal = AppleTokens.ItemGap / 2)
                                            .plusStart(sideInset),
                                    ) {
                                        items(4) {
                                            GridItemPlaceHolder()
                                        }
                                    }
                                }

                                TextPlaceholder(
                                    height = 36.dp,
                                    modifier = Modifier
                                        .padding(AppPage.Gutter)
                                        .width(250.dp),
                                )
                                repeat(4) {
                                    Row {
                                        repeat(2) {
                                            TextPlaceholder(
                                                height = MoodAndGenresButtonHeight,
                                                shape = RoundedCornerShape(AppleTokens.Control),
                                                modifier = Modifier
                                                    .padding(AppleTokens.ItemGap / 2)
                                                    .width(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item(key = "bottom_spacer", contentType = "spacer") {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                if (homePrefs.fab) {
                    HideOnScrollFAB(
                        visible = allLocalItems.isNotEmpty() || allYtItems.isNotEmpty(),
                        lazyListState = lazylistState,
                        icon = R.drawable.shuffle,
                        onClick = actions.onLuckyPlay,
                        onRecognitionClick = actions.onOpenRecognition,
                    )
                }
            }
        }
        }
    }
}
