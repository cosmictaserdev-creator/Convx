/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.artist

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.unit.Dp
import com.convx.music.ui.theme.AppleColors
import com.convx.music.ui.component.GlassPillRow
import com.convx.music.ui.component.StretchPillIcon
import com.convx.music.ui.component.pillGlassBackground
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.resolvedTextColor
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.plusStart

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.graphics.luminance
import com.convx.music.ui.component.BlurredBackdropImage
import com.convx.music.ui.component.bakeRamp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.ripple
import com.convx.music.ui.ios.compat.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFHeart
import com.composables.sfsymbols.monochrome.SFHeartFill
import com.composables.sfsymbols.monochrome.SFInfoCircle
import com.composables.sfsymbols.monochrome.SFLink
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFPlus
import com.composables.sfsymbols.monochrome.SFStar
import com.composables.sfsymbols.monochrome.SFStarFill
import com.composables.sfsymbols.monochrome.SFInfo
import com.composables.sfsymbols.monochrome.SFChevronRight
import com.convx.music.LocalTabView
import androidx.compose.material.icons.Icons
import com.convx.music.ui.component.ArtistHeroImage
import com.convx.music.ui.component.TopFadeBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import coil3.compose.AsyncImage
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.ui.utils.sharedArtworkSource
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.convx.music.LocalDatabase
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.AppBarHeight
import com.convx.music.constants.HideExplicitKey
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import com.convx.music.ui.component.AlbumGridItem
import com.convx.music.ui.component.ExpandableText
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.LinkSegment
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.component.YouTubeListItem
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.component.shimmer.ButtonPlaceholder
import com.convx.music.ui.component.shimmer.ListItemPlaceHolder
import com.convx.music.ui.component.shimmer.ShimmerHost
import com.convx.music.ui.component.shimmer.TextPlaceholder
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.LocalIosColors
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.LocalAccentTextColor
import com.convx.music.ui.theme.LocalMetadataColor
import com.convx.music.ui.theme.rememberBrandFontFamily
import com.convx.music.ui.theme.rememberCustomArtistFontFamily
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.fadingEdge
import com.convx.music.ui.utils.resize
import com.convx.music.utils.listItemShape
import com.convx.music.constants.PureBlackHeroBackgroundKey
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.ArtistViewModel
import com.valentinilk.shimmer.shimmer
import com.convx.music.artistvideo.ArtistVideo
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.ShowArtistBackgroundVideoKey
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.convx.music.ui.component.floatingtabbar.gooey
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Apple Music artist page type: white, with the secondary tiers as plain alpha steps. */
private val ArtistTextTiers = listOf(
    Color.White,
    Color.White.copy(alpha = 0.6f),
    Color.White.copy(alpha = 0.35f),
    Color.White.copy(alpha = 0.24f),
)

private fun artistItemsRoute(artistId: String, browseId: String, params: String?): String = buildString {
    append("artist/")
    append(android.net.Uri.encode(artistId))
    append("/items?browseId=")
    append(android.net.Uri.encode(browseId))
    params?.takeIf { it.isNotBlank() }?.let {
        append("&params=")
        append(android.net.Uri.encode(it))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArtistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val artistPage = viewModel.artistPage
    val libraryArtist by viewModel.libraryArtist.collectAsStateWithLifecycle()
    val librarySongs by viewModel.librarySongs.collectAsStateWithLifecycle()
    val libraryAlbums by viewModel.libraryAlbums.collectAsStateWithLifecycle()
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)
    val dataSaverEnabled by rememberPreference(key = DataSaverEnabledKey, defaultValue = false)
    val showArtistBackgroundVideoPref by rememberPreference(key = ShowArtistBackgroundVideoKey, defaultValue = true)
    val showArtistBackgroundVideo = if (dataSaverEnabled) false else showArtistBackgroundVideoPref
    val inTabView = LocalTabView.current
    val featuredRelease = remember(artistPage) { artistPage?.sections?.featuredRelease() }
    val firstSongShelf = remember(artistPage) {
        artistPage?.sections?.firstOrNull { (it.items.firstOrNull() as? SongItem)?.album != null }?.title
    }

    val lazyListState = rememberLazyListState()

    // Per-screen Haze source: the list content the post-hero TopFadeBlur
    // samples once scrolled past the photo.
    val hazeState = remember { HazeState() }
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocal by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current

    val systemBarsTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val headerOffset = with(density) {
        -(systemBarsTopPadding + AppBarHeight).roundToPx()
    }

    // Drives the back/share scrim as the hero scrolls away.
    val chromeScrimThresholdPx = with(density) { 220.dp.toPx() }
    // Pause motion video when the hero scrolls offscreen.
    val heroVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 } }
    val chromeScrimAlpha by remember {
        derivedStateOf {
            val scrolledPx = if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                chromeScrimThresholdPx
            }
            (scrolledPx / chromeScrimThresholdPx).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(libraryArtist) {
        showLocal = libraryArtist?.artist?.isLocal == true
    }

    // Seed the hero with the fastest available artwork while page data loads.
    val artistThumbnail = artistPage?.artist?.thumbnail ?: libraryArtist?.artist?.thumbnailUrl
    val instantArtistThumb = com.convx.music.ui.component.detailSeed(
        viewModel.thumbArg,
        com.convx.music.ui.utils.pendingSharedArtworkThumbFor(viewModel.artistId),
        artistThumbnail,
    )
    val artistChrome = com.convx.music.ui.component.rememberDetailChromeEntrance(iconCount = 2)

    // Avoid attaching a backdrop inside the root NavHost layer; that would capture itself.
    // This unattached backdrop keeps hero chrome translucent without live refraction.
    val heroBackdrop = rememberLayerBackdrop()

    // The floating chrome samples this sibling list backdrop, so it does not capture itself.
    val heroZoom = rememberHeroZoom()

    // Prefer Apple's artwork color, then estimate it from the displayed image.
    val appleArtistState = com.convx.music.utils.rememberAppleArtist(
        artistPage?.artist?.title ?: libraryArtist?.artist?.name,
    )
    val appleLoading = appleArtistState.loading
    val appleArtist = appleArtistState.artist
    val estimatedArtworkBg = com.convx.music.utils.rememberEstimatedArtworkBg(
        (appleArtist?.hero?.sized(256) ?: instantArtistThumb ?: artistThumbnail)
            .takeIf { !appleLoading && appleArtist?.hero?.bgColor == null },
    )
    // Keep the video player outside the lazy header so scrolling does not recreate it.
    val motionUrl = appleArtist?.motionVideoUrl?.takeIf { showArtistBackgroundVideo }
    val motionPlayer = motionUrl?.let { com.convx.music.artistvideo.rememberArtistVideoPlayer(it) }
    if (motionPlayer != null) {
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        val resumed by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
        LaunchedEffect(motionPlayer, heroVisible, resumed) {
            motionPlayer.active = heroVisible &&
                resumed.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)
        }
    }
    val artworkBgRgb = appleArtist?.hero?.bgColor ?: estimatedArtworkBg ?: 0x1C1C1E
    val pageBgRgb = AppleColors.pageBackground(artworkBgRgb)
    val tint by androidx.compose.animation.animateColorAsState(AppleColors.color(pageBgRgb), label = "artistPage")
    val onTint = Color.White
    val textTiers = ArtistTextTiers
    val chromeTextColor = textTiers[1]

    // Include the page tint in the captured backdrop so blur matches the visible page.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()

    // A flat fill of `tint` reads as a hard-edged color mismatch once the hero
    // art ends — the eased foot wash inside ArtistHeroImage settles the join
    // before the list begins instead.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(tint)
    ) {
    val contentWidth = maxWidth
    val wideArtistHeader = maxWidth > maxHeight && maxWidth >= 600.dp
    // Tab view: a full-bleed photo that takes most of the first screen, like Apple Music on iPad.
    val fullBleedHeroHeight = (maxHeight * 0.72f).coerceIn(380.dp, 820.dp)
    // Only Apple supplies wide art; a YouTube-only artist gets the square hero instead of a cropped wide one.
    val wideHero = inTabView && (appleLoading || appleArtist?.hero != null)
    val sideBySide = wideHero && wideArtistHeader
    val heroSide = when {
        wideHero -> fullBleedHeroHeight / com.convx.music.ui.component.ArtistHeroExtent
        wideArtistHeader -> (maxWidth * 0.42f).coerceAtMost(360.dp)
        else -> maxWidth
    }
    HeroTintedContent(tint = tint, backdrop = heroBackdrop, darkChrome = true) {
      // HeroTintedContent only sees the flat tint, so provide colors derived from the mesh
      // for Compose text, metadata, accents, and iOS components.
      val baseIosColors = IosTheme.colors
      val meshIosColors = remember(baseIosColors, textTiers, tint) {
          baseIosColors.copy(
              isDark = true,
              systemBackground = tint,
              secondarySystemBackground = tint,
              tertiarySystemBackground = tint,
              systemGroupedBackground = tint,
              secondarySystemGroupedBackground = tint,
              tertiarySystemGroupedBackground = tint,
              label = textTiers[0],
              secondaryLabel = textTiers[1],
              tertiaryLabel = textTiers[2],
              separator = textTiers[0].copy(alpha = 0.18f),
              opaqueSeparator = textTiers[0].copy(alpha = 0.24f),
          )
      }
      CompositionLocalProvider(
          LocalAccentTextColor provides textTiers[0],
          LocalContentColor provides textTiers[0],
          // The engine's secondary tier, not its tertiary: the artist name under a
          // song title is the one piece of row metadata people actually read, and the
          // 3.0:1 tier is a step too far back for it on a bright mesh.
          LocalMetadataColor provides textTiers[1],
          LocalIosColors provides meshIosColors,
      ) {
        val sideInset = LocalPlayerAwareWindowInsets.current
            .asPaddingValues()
            .calculateStartPadding(LocalLayoutDirection.current)

        // Capture through a plain Box; LazyColumn's recycled item layers do not flatten reliably.
        Box(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(backdropFreeze.connection)

            // Cache list content so sibling redraws do not re-record the backdrop.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            .graphicsLayer()
            .hazeSource(hazeState)) {
        LazyColumn(
            state = lazyListState,
            overscrollEffect = heroZoom.listOverscroll(),
            modifier = Modifier.heroPullZoom(heroZoom),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            if (artistPage == null && !showLocal) {
                item(key = "shimmer") {
                    Column(
                        modifier = Modifier
                            .offset {
                                IntOffset(x = 0, y = headerOffset)
                            }
                    ) {
                        // Hero sits outside the shimmer sweep: the seeded thumb
                        // mirrors the loaded photo statically, and no grey
                        // shimmer block ever covers the artwork area.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .shimmer()
                                    .background(Color.White.copy(alpha = 0.08f)),
                            )
                        }
                    ShimmerHost {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            TextPlaceholder(
                                height = 36.dp,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(bottom = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ButtonPlaceholder(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(52.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ButtonPlaceholder(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(52.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .shimmer()
                                            .background(
                                                AppTheme.colorScheme.onSurface,
                                                RoundedCornerShape(26.dp)
                                            )
                                    )
                                }
                            }
                        }
                        repeat(6) {
                            ListItemPlaceHolder()
                        }
                    }
                    }
                }
            } else {
                item(key = "header") {
                    val thumbnail = artistPage?.artist?.thumbnail ?: libraryArtist?.artist?.thumbnailUrl
                    val artistName = artistPage?.artist?.title ?: libraryArtist?.artist?.name
                    val displayArtistName = artistName?.titlecaseWords() ?: "Unknown"
                    LaunchedEffect(displayArtistName) {
                        viewModel.fetchArtistSignature(displayArtistName)
                    }
                    val tabView = wideHero
                    val artistId = artistPage?.artist?.id
                    val queueTitle by playerConnection.queueTitle.collectAsStateWithLifecycle()
                    // Queue metadata can omit the artist ID, so also compare artist names.
                    val currentArtists = mediaMetadata?.artists.orEmpty()
                    val metadataMatchesArtist = currentArtists.any { artist ->
                        (artistId != null && artist.id == artistId) ||
                            (artistName != null && artist.name.equals(artistName, ignoreCase = true))
                    }
                    val currentIsThisArtist = metadataMatchesArtist ||
                        (artistName != null && queueTitle == artistName)
                    val isPlayingThisArtist = isPlaying && currentIsThisArtist
                    val isSubscribed = libraryArtist?.artist?.bookmarkedAt != null
                    val featured = featuredRelease

                    val onPlay: () -> Unit = {
                        when {
                            isPlayingThisArtist -> playerConnection.player.pause()
                            currentIsThisArtist -> playerConnection.play()
                            showLocal -> {
                                val songs = if (hideExplicit) {
                                    librarySongs.filterNot { it.song.explicit }
                                } else {
                                    librarySongs
                                }
                                if (songs.isNotEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = artistName ?: "Artist",
                                            items = songs.map { it.toMediaItem() },
                                        ),
                                    )
                                }
                            }
                            else -> {
                                val songs = artistPage?.sections
                                    ?.find { it.items.firstOrNull() is SongItem }
                                    ?.items?.filterIsInstance<SongItem>()
                                if (!songs.isNullOrEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = artistName ?: "Artist",
                                            items = songs.map { it.toMediaItem() },
                                        ),
                                    )
                                } else {
                                    artistPage?.artist?.radioEndpoint?.let {
                                        playerConnection.playQueue(YouTubeQueue(it))
                                    }
                                }
                            }
                        }
                    }
                    val onSubscribe: () -> Unit = {
                        database.transaction {
                            val artist = libraryArtist?.artist
                            if (artist != null) {
                                update(artist.toggleLike())
                            } else {
                                artistPage?.artist?.let {
                                    insert(
                                        ArtistEntity(
                                            id = it.id,
                                            name = it.title,
                                            channelId = it.channelId,
                                            thumbnailUrl = it.thumbnail,
                                        ).toggleLike(),
                                    )
                                }
                            }
                        }
                    }

                    // Hero scroll blur: fully climbed after ~90% of the photo has scrolled away.
                    // Read by the hero in its draw phase, so it is not quantised.
                    val heroVideoFrame = remember(motionPlayer) { androidx.compose.runtime.mutableIntStateOf(0) }
                    val heroBlurRangePx = with(density) { heroSide.toPx() * 0.9f }
                    val heroScrollBlur by remember(lazyListState, heroBlurRangePx) {
                        derivedStateOf {
                            val raw = if (lazyListState.firstVisibleItemIndex == 0) {
                                lazyListState.firstVisibleItemScrollOffset / heroBlurRangePx
                            } else {
                                1f
                            }
                            raw.coerceIn(0f, 1f)
                        }
                    }

                    if (tabView) {
                        val topCover = with(density) { (-headerOffset).toDp() }
                        Box(modifier = Modifier.fillMaxWidth().height(fullBleedHeroHeight)) {
                            com.convx.music.ui.component.ArtistWideHero(
                                artworkUrl = appleArtist?.hero?.sized(1600) ?: thumbnail ?: instantArtistThumb,
                                pageColor = tint,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(fullBleedHeroHeight + topCover)
                                    .offset { IntOffset(x = 0, y = headerOffset) },
                                overlay = {
                                    if (motionPlayer != null) {
                                        ArtistVideo(player = motionPlayer, modifier = Modifier.fillMaxSize(),
                                            onFrame = { heroVideoFrame.intValue++ }, onClick = {})
                                    }
                                },
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                    modifier = Modifier.height(ArtistWordmarkHeight).padding(horizontal = 20.dp),
                                ) {
                                    if (!appleLoading || viewModel.artistBundledLogo != null || viewModel.artistSignatureUrl != null) ArtistWordmark(
                                        logo = appleArtist?.logo,
                                        signatureUrl = viewModel.artistSignatureUrl,
                                        name = displayArtistName,
                                        bundledLogo = viewModel.artistBundledLogo,
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                ArtistActionRow(
                                    keyColor = appleArtist?.keyColor,
                                    pageBgRgb = pageBgRgb,
                                    isPlaying = isPlayingThisArtist,
                                    isSubscribed = isSubscribed,
                                    onInfo = {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(
                                                (lazyListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0),
                                            )
                                        }
                                    },
                                    onPlay = onPlay,
                                    onSubscribe = onSubscribe,
                                )
                            }
                        }
                        if (featured != null && !sideBySide) {
                            Spacer(modifier = Modifier.height(15.dp))
                            FeaturedReleaseCard(
                                album = featured.second,
                                caption = featured.second.year?.toString() ?: featured.first,
                                pageBgRgb = pageBgRgb,
                                onClick = {
                                    navController.navigate(albumRoute(featured.second.id, featured.second.thumbnail))
                                },
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                    } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Square hero: YouTube's art immediately, Apple's own artist image
                        // crossfades over it the moment it resolves -- never blocks on the
                        // lookup when a fallback is already known.
                        val fallbackArtworkUrl = thumbnail ?: instantArtistThumb
                        ArtistHeroImage(
                            artworkUrl = fallbackArtworkUrl,
                            appleArtworkUrl = appleArtist?.hero?.sized(1200),
                            pageColor = tint,
                            heroScale = { heroZoom.scale },
                            loading = appleLoading && fallbackArtworkUrl.isNullOrBlank(),
                            scrollBlur = { 0f },
                            videoFrame = { heroVideoFrame.intValue },
                            modifier = Modifier.width(heroSide).offset { IntOffset(x = 0, y = headerOffset) },
                            overlay = {
                                if (motionPlayer != null) {
                                    ArtistVideo(player = motionPlayer, modifier = Modifier.fillMaxSize(),
                                        onFrame = { heroVideoFrame.intValue++ }, onClick = {})
                                }
                            },
                        )

                            // Offset this content so the artwork remains visible above the lazy item.
                        val itemTopOnScreen = with(density) { (-headerOffset).toDp() }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = if (wideArtistHeader) heroSide else 0.dp)
                                .padding(
                                    top = if (wideArtistHeader) {
                                        12.dp
                                    } else {
                                        (heroSide - ArtistWordmarkFootInset - ArtistWordmarkHeight - itemTopOnScreen)
                                            .coerceAtLeast(0.dp)
                                    },
                                ),
                        ) {
                            // Use the live hero blur so it follows the motion video.
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Box(
                                        contentAlignment = Alignment.BottomCenter,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(ArtistWordmarkHeight)
                                            .padding(horizontal = 20.dp),
                                    ) {
                                        // No name while Apple settles, so a logo never replaces text mid-view.
                                        if (!appleLoading || viewModel.artistBundledLogo != null || viewModel.artistSignatureUrl != null) ArtistWordmark(
                                            logo = appleArtist?.logo,
                                            signatureUrl = viewModel.artistSignatureUrl,
                                            name = displayArtistName,
                                            bundledLogo = viewModel.artistBundledLogo,
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(18.dp))

                                    ArtistActionRow(
                                        keyColor = appleArtist?.keyColor,
                                        pageBgRgb = pageBgRgb,
                                        isPlaying = isPlayingThisArtist,
                                        isSubscribed = isSubscribed,
                                        onInfo = {
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(
                                                    (lazyListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0),
                                                )
                                            }
                                        },
                                        onPlay = onPlay,
                                        onSubscribe = onSubscribe,
                                    )
                                }
                            }

                            if (featured != null) {
                                Spacer(modifier = Modifier.height(15.dp))
                                FeaturedReleaseCard(
                                    album = featured.second,
                                    caption = featured.second.year?.toString() ?: featured.first,
                                    pageBgRgb = pageBgRgb,
                                    onClick = {
                                        navController.navigate(albumRoute(featured.second.id, featured.second.thumbnail))
                                    },
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    }
                }


                if (showLocal) {
                    if (librarySongs.isNotEmpty()) {
                        item(key = "local_songs_title") {
                            ArtistSectionHeader(
                                title = stringResource(R.string.songs),
                                modifier = Modifier.animateItem().artistTitleChrome(),
                                onClick = {
                                    navController.navigate("artist/${viewModel.artistId}/songs")
                                }
                            )
                        }

                        val filteredLibrarySongs = if (hideExplicit) {
                            librarySongs.filter { !it.song.explicit }
                        } else {
                            librarySongs
                        }
                        itemsIndexed(
                            items = filteredLibrarySongs,
                            key = { index, item -> "local_song_${item.id}_$index" }
                        ) { index, song ->
                            SongListItem(
                                song = song,
                                showInLibraryIcon = true,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                shape = listItemShape(index, filteredLibrarySongs.size),
                                flat = true,
                                secondaryAdditiveChrome = true,
                                // Align thumbnails with section titles and inset only the row separators.
                                thumbnailStartInset = 0.dp,
                                leadingDivider = index == 0,
                                trailingDividerInset = if (index == filteredLibrarySongs.lastIndex) {
                                    null
                                } else {
                                    ArtistInterRowDividerInset
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                        modifier = Modifier.additiveChrome(),
                                    ) {
                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, size = 22.dp, tint = Color.White.copy(alpha = 0.6f))
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            if (song.id == mediaMetadata?.id) {
                                                playerConnection.togglePlayPause()
                                            } else {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = libraryArtist?.artist?.name ?: "Unknown Artist",
                                                        items = filteredLibrarySongs.map { it.toMediaItem() },
                                                        startIndex = index
                                                    )
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (libraryAlbums.isNotEmpty()) {
                        item(key = "local_albums_title") {
                            ArtistSectionHeader(
                                title = stringResource(R.string.albums),
                                modifier = Modifier.animateItem().artistTitleChrome(),
                                onClick = {
                                    navController.navigate("artist/${viewModel.artistId}/albums")
                                }
                            )
                        }

                        item(key = "local_albums_list") {
                            val filteredLibraryAlbums = if (hideExplicit) {
                                libraryAlbums.filter { !it.album.explicit }
                            } else {
                                libraryAlbums
                            }
                            LazyRow(
                                contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues().plusStart(sideInset),
                                modifier = Modifier.bleedStart(sideInset),
                            ) {
                                itemsIndexed(
                                    // indexOf(it) in the key lambda was a linear scan per
                                    // item; the index is already supplied here.
                                    items = filteredLibraryAlbums,
                                    key = { index, it -> "local_album_${it.id}_$index" }
                                ) { _, album ->
                                    AlbumGridItem(
                                        album = album,
                                        isActive = mediaMetadata?.album?.id == album.id,
                                        isPlaying = isPlaying,
                                        coroutineScope = coroutineScope,
                                        modifier = Modifier
                                            .combinedBounceClick(
                                                onClick = {
                                                    navController.navigate(albumRoute(album.id, album.thumbnailUrl))
                                                },
                                                onLongClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    menuState.show {
                                                        AlbumMenu(
                                                            originalAlbum = album,
                                                            navController = navController,
                                                            onDismiss = menuState::dismiss
                                                        )
                                                    }
                                                }
                                            )
                                            .animateItem()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    artistPage?.sections?.fastForEach { section ->
                        val featuredBeside = sideBySide && featuredRelease != null && section.title == firstSongShelf
                        if (section.items.isNotEmpty() && !section.isFeaturedLatest() && !featuredBeside) {
                            item(key = "section_${section.title}") {
                                // Use a consistent header for each artist section.
                                val isSongShelf = (section.items.firstOrNull() as? SongItem)?.album != null
                                ArtistSectionHeader(
                                    title = when (section.title) {
                                        "Songs" -> "Top Songs"
                                        "Popular" -> "Popular"
                                        else -> section.title
                                    },
                                    compact = isSongShelf,
                                    modifier = Modifier.animateItem().artistTitleChrome(),
                                    onClick = section.moreEndpoint?.let {
                                        {
                                            navController.navigate(
                                                artistItemsRoute(viewModel.artistId, it.browseId, it.params),
                                            )
                                        }
                                    },
                                )
                            }
                        }

                        if (section.isFeaturedLatest()) {
                            // Drawn as the featured card in the header.
                        } else if ((section.items.firstOrNull() as? SongItem)?.album != null) {
                            // Page songs horizontally and keep part of the next page visible.
                            item(key = "section_songs_${section.title}") {
                                val pages = remember(section) {
                                    section.items.distinctBy { it.id }.filterIsInstance<SongItem>().chunked(4)
                                }
                                val pagerState = rememberLazyListState()
                                val pageWidth = if (featuredBeside) {
                                    ((contentWidth - FeaturedColumnWidth) / 2.2f).coerceIn(300.dp, 420.dp)
                                } else {
                                    contentWidth - if (pages.size > 1) 26.dp else 0.dp
                                }
                                val songsRow: @Composable () -> Unit = {
                                LazyRow(
                                    state = pagerState,
                                    flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(pagerState),
                                    modifier = if (featuredBeside) Modifier else Modifier.animateItem(),
                                ) {
                                    items(pages.size, key = { "songs_page_$it" }) { pageIndex ->
                                        val page = pages[pageIndex]
                                        Column(Modifier.width(pageWidth)) {
                                            page.forEachIndexed { index, song ->
                                            YouTubeListItem(
                                                item = song,
                                                subtitle = song.album?.name,
                                                isActive = mediaMetadata?.id == song.id,
                                                isPlaying = isPlaying,
                                                shape = RoundedCornerShape(18.dp),
                                                flat = false,
                                                secondaryAdditiveChrome = true,
                                                // Align thumbnails with section titles and inset only the row separators.
                                                thumbnailStartInset = 0.dp,
                                                leadingDivider = index == 0,
                                                trailingDividerInset = if (index == page.lastIndex) {
                                                    null
                                                } else {
                                                    ArtistInterRowDividerInset
                                                },
                                                trailingContent = {
                                                    IconButton(
                                                        onClick = {
                                                            menuState.show {
                                                                YouTubeSongMenu(
                                                                    song = song,
                                                                    navController = navController,
                                                                    onDismiss = menuState::dismiss,
                                                                )
                                                            }
                                                        },
                                                        modifier = Modifier.additiveChrome(),
                                                    ) {
                                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, size = 22.dp, tint = Color.White.copy(alpha = 0.6f))
                                                    }
                                                },
                                                modifier = Modifier
                                                    .combinedBounceClick(
                                                        onClick = {
                                                            if (song.id == mediaMetadata?.id) {
                                                                playerConnection.togglePlayPause()
                                                            } else {
                                                                playerConnection.playQueue(
                                                                    YouTubeQueue(
                                                                        WatchEndpoint(videoId = song.id),
                                                                        song.toMediaMetadata()
                                                                    ),
                                                                )
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                YouTubeSongMenu(
                                                                    song = song,
                                                                    navController = navController,
                                                                    onDismiss = menuState::dismiss,
                                                                )
                                                            }
                                                        },
                                                    ),
                                            )
                                            }
                                        }
                                    }
                                }
                                }
                                if (featuredBeside) {
                                    val release = featuredRelease!!
                                    Row(Modifier.fillMaxWidth().animateItem()) {
                                        Column(Modifier.width(FeaturedColumnWidth)) {
                                            ArtistSectionHeader(
                                                title = stringResource(R.string.latest_release),
                                                compact = true,
                                                modifier = Modifier.artistTitleChrome(),
                                            )
                                            FeaturedReleaseCard(
                                                album = release.second,
                                                caption = release.second.year?.toString() ?: release.first,
                                                pageBgRgb = pageBgRgb,
                                                onClick = {
                                                    navController.navigate(albumRoute(release.second.id, release.second.thumbnail))
                                                },
                                                modifier = Modifier.padding(horizontal = 20.dp),
                                            )
                                        }
                                        Column(Modifier.weight(1f)) {
                                            ArtistSectionHeader(
                                                title = "Top Songs",
                                                compact = true,
                                                modifier = Modifier.artistTitleChrome(),
                                                onClick = section.moreEndpoint?.let {
                                                    {
                                                        navController.navigate(
                                                            artistItemsRoute(viewModel.artistId, it.browseId, it.params),
                                                        )
                                                    }
                                                },
                                            )
                                            songsRow()
                                        }
                                    }
                                } else {
                                    songsRow()
                                }
                            }
                        } else if (section.items.firstOrNull() is ArtistItem) {
                            items(
                                items = section.items.distinctBy { it.id }.filterIsInstance<ArtistItem>().take(5),
                                key = { "related_${section.title}_${it.id}" },
                            ) { artist ->
                                RelatedArtistRow(
                                    artist = artist,
                                    onClick = { navController.navigate(artistRoute(artist.id, artist.thumbnail)) },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        menuState.show {
                                            YouTubeArtistMenu(artist = artist, onDismiss = menuState::dismiss)
                                        }
                                    },
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        } else {
                            item(key = "section_list_${section.title}") {
                                val isVideoShelf = section.items.firstOrNull() is SongItem
                                val itemPrefs = com.convx.music.ui.component.LocalItemPrefs.current
                                CompositionLocalProvider(
                                    com.convx.music.ui.component.LocalItemPrefs provides itemPrefs.copy(
                                        gridCardHeightOverrideDp = if (isVideoShelf) 87 else 154,
                                    ),
                                ) {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp).plusStart(sideInset),
                                    modifier = Modifier.bleedStart(sideInset),
                                ) {
                                    items(
                                        items = section.items.distinctBy { it.id },
                                        key = { "youtube_album_${it.id}" },
                                    ) { item ->
                                        YouTubeGridItem(
                                            item = item,
                                            isActive = when (item) {
                                                is SongItem -> mediaMetadata?.id == item.id
                                                is AlbumItem -> mediaMetadata?.album?.id == item.id
                                                else -> false
                                            },
                                            isPlaying = isPlaying,
                                            coroutineScope = coroutineScope,
                                            thumbnailRatio = if (isVideoShelf) 16f / 9f else 1f,
                                            modifier = Modifier
                                                .combinedBounceClick(
                                                    onClick = {
                                                        when (item) {
                                                            is SongItem ->
                                                                playerConnection.playQueue(
                                                                    YouTubeQueue(
                                                                        WatchEndpoint(videoId = item.id),
                                                                        item.toMediaMetadata()
                                                                    ),
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
                                                )
                                                .animateItem(),
                                        )
                                    }
                                }
                                }
                            }
                        }
                    }
                    // The info button scrolls to this biography section.
                    val bio = artistPage?.description
                    val bioRuns = artistPage?.descriptionRuns
                    if (!bio.isNullOrEmpty() || !bioRuns.isNullOrEmpty()) {
                        item(key = "biography") {
                            Column(Modifier.animateItem()) {
                                ArtistSectionHeader(title = "Biography")
                                CompositionLocalProvider(
                                    LocalContentColor provides textTiers[1],
                                ) {
                                    ExpandableText(
                                        text = bio.orEmpty(),
                                        runs = bioRuns?.map {
                                            LinkSegment(text = it.text, url = it.navigationEndpoint?.urlEndpoint?.url)
                                        },
                                        collapsedMaxLines = 6,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        val showLocalFab = librarySongs.isNotEmpty() && libraryArtist?.artist?.isLocal != true
        
        // Library/Local Toggle FAB
        HideOnScrollFAB(
            visible = showLocalFab,
            lazyListState = lazyListState,
            icon = if (showLocal) R.drawable.language else R.drawable.library_music,
            onClick = {
                showLocal = showLocal.not()
                if (!showLocal && artistPage == null) viewModel.fetchArtistsFromYTM()
            },
            hazeState = hazeState,
        )
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )

        // Keep the glass buttons readable over both the hero and scrolled content.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            // The nav fade follows the app theme, not the (always dark)
            // artwork tint: a light fade behind the chrome in light mode.
            val navScrimBase =
                if (AppTheme.colorScheme.surface.luminance() <= 0.5f) Color.Black
                else Color.White
            val navScrim = remember(navScrimBase) {
                Brush.verticalGradient(
                    0f to navScrimBase.copy(alpha = 0.55f),
                    0.35f to navScrimBase.copy(alpha = 0.4f),
                    0.7f to navScrimBase.copy(alpha = 0.12f),
                    1f to Color.Transparent,
                )
            }
            // Alpha read in the draw phase: scrolling redraws this strip, never recomposes.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppBarHeight * 2.5f)
                    .drawBehind { drawRect(navScrim, alpha = chromeScrimAlpha) }
            )

            // Draw the sampled haze above the navigation scrim so it remains
            // visible over the hero photo as well as the scrolled list.
            TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(appTopBarWindowInsets())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
            GlassCircleButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
                modifier = with(artistChrome) {
                    Modifier.backEnter().then(com.convx.music.ui.component.backExitModifier())
                },
            ) {
                SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
            }

            GlassPillRow(
                modifier = with(artistChrome) { Modifier.containerEnter() }
                    .height(44.dp)
                    .then(pillGlassBackground(ArtistCapsuleShape))
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides LocalGlassEffectConfig.current.resolvedTextColor(),
                ) {
                    StretchPillIcon(
                        entrance = artistChrome,
                        index = 0,
                        modifier = Modifier.clickable {
                            viewModel.artistPage?.artist?.let { artist ->
                                menuState.show {
                                    YouTubeArtistMenu(artist = artist, onDismiss = menuState::dismiss)
                                }
                            }
                        },
                    ) {
                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, size = 24.dp)
                    }
                    StretchPillIcon(
                        entrance = artistChrome,
                        index = 1,
                        modifier = Modifier.clickable {
                            viewModel.artistPage?.artist?.shareLink?.let { link ->
                                val send = android.content.Intent(android.content.Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(android.content.Intent.EXTRA_TEXT, link)
                                context.startActivity(android.content.Intent.createChooser(send, null))
                            }
                        },
                    ) {
                        SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 22.dp)
                    }
                }
            }
            }
        }
        }
      }
      }
    }
}

/** Width of the "Latest Release" column beside the top songs in tab view. */
private val FeaturedColumnWidth = 340.dp

/** Bottom-aligned artist logo or name. */
private val ArtistWordmarkHeight = 75.dp
private val ArtistWordmarkMaxWidth = 286.dp

/** Gap between the wordmark box and the hero foot. */
private val ArtistWordmarkFootInset = 4.dp

private val ArtistCapsuleShape = com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(percent = 50)

private fun com.music.innertube.pages.ArtistSection.isFeaturedLatest(): Boolean =
    title.contains("Latest", ignoreCase = true) && items.firstOrNull() is AlbumItem

/**
 * (section title, album) of the release the header card features: YouTube's "Latest release"
 * when the page has one, otherwise the first album/single shelf's lead item — iOS always shows
 * the card, and plenty of YouTube artist pages carry no "Latest" shelf.
 */
private fun List<com.music.innertube.pages.ArtistSection>.featuredRelease(): Pair<String, AlbumItem>? =
    (firstOrNull { it.isFeaturedLatest() } ?: firstOrNull { it.items.firstOrNull() is AlbumItem })
        ?.let { it.title to it.items.first() as AlbumItem }

/** Apple Music logo first, then a signature image, then the artist name. */
@Composable
internal fun ArtistWordmark(
    logo: com.convx.music.canvas.AppleArtwork?,
    name: String,
    signatureUrl: String? = null,
    bundledLogo: java.io.File? = null,
) {
    var logoFailed by remember(logo) { mutableStateOf(false) }
    var bundledLogoFailed by remember(bundledLogo) { mutableStateOf(false) }
    var signatureFailed by remember(signatureUrl) { mutableStateOf(false) }
    if (logo != null && !logoFailed) {
        AsyncImage(
            model = logo.sized(com.convx.music.utils.AppleArt.logoWidthPx(LocalContext.current), png = true),
            contentDescription = name,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            onError = { logoFailed = true },
            modifier = Modifier
                .widthIn(max = ArtistWordmarkMaxWidth)
                .fillMaxSize(),
        )
    } else if (bundledLogo != null && !bundledLogoFailed) {
        AsyncImage(
            model = bundledLogo,
            contentDescription = name,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            onError = { bundledLogoFailed = true },
            modifier = Modifier
                .widthIn(max = ArtistWordmarkMaxWidth)
                .fillMaxSize(),
        )
    } else if (!signatureUrl.isNullOrBlank() && !signatureFailed) {
        AsyncImage(
            model = signatureUrl,
            contentDescription = "$name signature",
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            onError = { signatureFailed = true },
            modifier = Modifier
                .widthIn(max = ArtistWordmarkMaxWidth)
                .fillMaxSize(),
        )
    } else {
        Text(
            text = name,
            color = Color.White,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = rememberCustomArtistFontFamily(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Info / play / favourite: 50, 73, 50 with 24 between. Play is filled with the artist's key
 * colour (white without one) and its glyph is the page colour pulled 30% to black; the side
 * buttons are an 8% white wash with the key colour as their glyph.
 */
@Composable
private fun ArtistActionRow(
    keyColor: Int?,
    pageBgRgb: Int,
    isPlaying: Boolean,
    isSubscribed: Boolean,
    onInfo: () -> Unit,
    onPlay: () -> Unit,
    onSubscribe: () -> Unit,
) {
    val accent = AppleColors.color(AppleColors.playFill(keyColor))
    val controlFill = Color.White.copy(alpha = AppleColors.CONTROL_FILL_ALPHA)
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ArtistControlCircle(size = 50.dp, fill = controlFill, onClick = onInfo) {
            SymbolIcon(symbol = sf("SFInfo"), contentDescription = stringResource(R.string.about), size = 22.dp, tint = accent)
        }
        ArtistControlCircle(size = 73.dp, fill = accent, onClick = onPlay) {
            AnimatedPlayPauseIcon(
                isPlaying = isPlaying,
                tint = AppleColors.color(AppleColors.playGlyph(pageBgRgb)),
                size = 36.dp,
            )
        }
        ArtistControlCircle(size = 50.dp, fill = controlFill, onClick = onSubscribe) {
            SymbolIcon(symbol = if (isSubscribed) sf("SFStarFill") else sf("SFStar"), contentDescription = stringResource(R.string.favorite), size = 24.dp, tint = accent)
        }
    }
}

@Composable
private fun ArtistControlCircle(
    size: Dp,
    fill: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(fill)
            .bounceClick(onClick = onClick),
    ) {
        content()
    }
}

/**
 * Featured release card: 114 tall, 22 corners, 12 padding, 90 artwork with 10 corners, caption
 * and subtitle 15pt at 60% white around a 17pt semibold title, and a 32pt add button. The fill
 * is the measured white wash for this page colour ([AppleColors.cardFillAlpha]).
 */
@Composable
private fun FeaturedReleaseCard(
    album: AlbumItem,
    caption: String,
    pageBgRgb: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(22.dp)
    com.convx.music.ui.utils.ArtworkSourceHost(modifier) { sourceModifier ->
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = sourceModifier
            .fillMaxWidth()
            .height(114.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = AppleColors.cardFillAlpha(pageBgRgb)))
            .border(0.33.dp, Color.White.copy(alpha = 0.10f), shape)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        AsyncImage(
            model = album.thumbnail.resize(300, 300),
            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
                com.convx.music.ui.utils.rememberArtworkSourceOwner(), album.thumbnail,
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .sharedArtworkSource(album.id, album.thumbnail)
                .clip(RoundedCornerShape(10.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = caption,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = album.title,
                color = Color.White,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            album.description?.takeIf { it.isNotBlank() }?.let { subtitle ->
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    }
}

/** "Top Songs ›": 22pt bold on the 20pt gutter, chevron only when the section opens. */
@Composable
private fun ArtistSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    /** Smaller heading used above the song list. */
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(start = 20.dp, end = 20.dp, top = if (compact) 8.dp else 24.dp, bottom = if (compact) 14.dp else 16.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = if (compact) 20.sp else 22.sp,
            lineHeight = if (compact) 25.sp else 28.sp,
            letterSpacing = if (compact) (-0.45).sp else (-0.26).sp,
            fontWeight = if (compact) FontWeight.SemiBold else FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (onClick != null) {
            Spacer(modifier = Modifier.width(4.dp))
            SymbolIcon(symbol = sf("SFChevronRight"), contentDescription = null, size = 16.dp, tint = Color.White.copy(alpha = 0.6f))
        }
    }
}

/** One related artist row with artwork, name, and navigation affordance. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RelatedArtistRow(
    artist: ArtistItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 20.dp),
    ) {
        AsyncImage(
            model = artist.thumbnail?.resize(168, 168),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = artist.title,
            color = Color.White,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.43).sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        SymbolIcon(symbol = sf("SFChevronRight"), contentDescription = null, size = 14.dp, tint = Color.White.copy(alpha = 0.35f))
    }
}

/** Capitalizes each word's first letter while preserving the remaining casing. */
private fun String.titlecaseWords(): String =
    split(' ').joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercaseChar() }
    }

/** Secondary text on this page: white added onto the mesh, player-style. */
private const val ArtistChromeAlpha = 0.45f

/** Titles only get a hint: brightest line, backdrop glow. */
private const val ArtistTitleChromeAlpha = 0.85f

/**
 * Inter-row rules stop just inside the thumbnails; the section's outer rules
 * (first row's leading, last row's trailing) stay full width with padding.
 */
private val ArtistInterRowDividerInset = 28.dp

// Draw modifiers only, no layout nodes. Alpha after additiveChrome, never before.
private fun Modifier.artistChrome() =
    additiveChrome().graphicsLayer { alpha = ArtistChromeAlpha }

private fun Modifier.artistTitleChrome() =
    additiveChrome().graphicsLayer { alpha = ArtistTitleChromeAlpha }
