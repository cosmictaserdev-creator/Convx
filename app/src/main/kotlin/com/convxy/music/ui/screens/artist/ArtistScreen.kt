/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.artist

import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.bleedStart
import com.convxy.music.ui.utils.plusStart

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ripple
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalTabView
import com.convxy.music.ui.component.HeroCardHeader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.convxy.music.ui.utils.Motion
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.AppBarHeight
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.constants.ShowArtistDescriptionKey
import com.convxy.music.constants.ShowArtistSubscriberCountKey
import com.convxy.music.constants.ShowMonthlyListenersKey
import com.convxy.music.db.entities.ArtistEntity
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.AlbumGridItem
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.HideOnScrollFAB
import com.convxy.music.ui.component.LinkSegment
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.YouTubeGridItem
import com.convxy.music.ui.component.YouTubeListItem
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.component.shimmer.ButtonPlaceholder
import com.convxy.music.ui.component.shimmer.ListItemPlaceHolder
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.component.shimmer.TextPlaceholder
import com.convxy.music.ui.menu.AlbumMenu
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.menu.YouTubeAlbumMenu
import com.convxy.music.ui.menu.YouTubeArtistMenu
import com.convxy.music.ui.menu.YouTubePlaylistMenu
import com.convxy.music.ui.menu.YouTubeSongMenu
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.LocalAccentTextColor
import com.convxy.music.ui.theme.rememberBrandFontFamily
import com.convxy.music.ui.theme.rememberCustomArtistFontFamily
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.utils.fadingEdge
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.listItemShape
import com.convxy.music.constants.PureBlackHeroBackgroundKey
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.ArtistViewModel
import com.valentinilk.shimmer.shimmer
import com.convxy.music.artistvideo.ArtistVideo
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.constants.ShowArtistVideoKey
import com.convxy.music.constants.ShowArtistBackgroundVideoKey
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.convxy.music.canvas.AppleMusicArtistBackgroundProvider
import com.convxy.music.ui.component.floatingtabbar.gooey

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArtistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val artistPage = viewModel.artistPage
    val libraryArtist by viewModel.libraryArtist.collectAsState()
    val librarySongs by viewModel.librarySongs.collectAsState()
    val libraryAlbums by viewModel.libraryAlbums.collectAsState()
    val artistVideoUrl by viewModel.artistVideoUrl.collectAsState()
    val artistVideoSong by viewModel.artistVideoSong.collectAsState()
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)
    val showArtistDescription by rememberPreference(key = ShowArtistDescriptionKey, defaultValue = true)
    val showArtistSubscriberCount by rememberPreference(key = ShowArtistSubscriberCountKey, defaultValue = true)
    val showMonthlyListeners by rememberPreference(key = ShowMonthlyListenersKey, defaultValue = true)
    val dataSaverEnabled by rememberPreference(key = DataSaverEnabledKey, defaultValue = false)
    val showArtistVideoPref by rememberPreference(key = ShowArtistVideoKey, defaultValue = true)
    val showArtistVideo = if (dataSaverEnabled) false else showArtistVideoPref
    val showArtistBackgroundVideoPref by rememberPreference(key = ShowArtistBackgroundVideoKey, defaultValue = true)
    val showArtistBackgroundVideo = if (dataSaverEnabled) false else showArtistBackgroundVideoPref

    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocal by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current

    // Calculate the offset value outside of the offset lambda
    val systemBarsTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val headerOffset = with(density) {
        -(systemBarsTopPadding + AppBarHeight).roundToPx()
    }

    // How far the list has scrolled past the hero art, 0..1 — drives the
    // floating back/share chrome's scrim from transparent (over the art) to
    // a solid dark shade (over regular content), same progressive treatment
    // as the app's other collapsing top bars.
    val chromeScrimThresholdPx = with(density) { 220.dp.toPx() }
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
        // always show local page for local artists. Show local page remote artist when offline
        showLocal = libraryArtist?.artist?.isLocal == true
    }

    // Apple Music style: a single dominant color pulled from the artist's own
    // artwork washes the screen instead of a flat Material surface, and the
    // circular back/share/subscribe chrome is real liquid glass sampling this
    // screen's own content behind it (same Modifier.liquidGlass + LocalAppBackdrop
    // mechanism the floating nav bar puck uses) rather than a flat fallback color.
    val artistThumbnail = artistPage?.artist?.thumbnail ?: libraryArtist?.artist?.thumbnailUrl

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.textColor else MaterialTheme.colorScheme.onSurface

    // Glass chrome (back/share buttons, chips) samples LocalAppBackdrop. The app
    // root's backdrop (MainActivity's Modifier.layerBackdrop(appBackdrop)) captures
    // the WHOLE NavHost — so a glass surface INSIDE this screen sampling it makes
    // the capture include itself: a native RenderNode cycle (stack overflow in
    // prepareTreeImpl). A screen-local layerBackdrop doesn't help either: this
    // screen is itself inside appBackdrop, so its layer is re-recorded and
    // re-drawn within appBackdrop's own draw pass, re-forming the cycle. So we
    // provide an UNATTACHED backdrop (never .layerBackdrop'd onto anything): its
    // drawBackdrop early-returns, drawing no live refraction, but the glass still
    // renders its translucent surface tint + specular highlight — frosted chrome,
    // no self-reference. (True artwork-refracting glass here would need a capture
    // layer rendered OUTSIDE the NavHost.)
    val heroBackdrop = rememberLayerBackdrop()

    // A SECOND, ATTACHED backdrop: .layerBackdrop'd onto the LazyColumn below,
    // which is a *sibling* of the floating chrome row below, not an ancestor —
    // the chrome row samples a texture of already-drawn list content, it never
    // captures its own draw pass, so no cycle (proven working the same way on
    // the playlist/album screens — contrary to the note above, a capture layer
    // outside the NavHost turned out not to be necessary).
    val heroZoom = rememberHeroZoom()

    val tint = rememberHeroTint(artistThumbnail)
    val onTint = com.convxy.music.ui.theme.AppleTokens.onColor(tint)

    // Fills the screen tint into the capture BEFORE the content, exactly like
    // MainActivity's appBackdrop does with its own background. Without it the
    // list records onto a transparent canvas (the tint is painted by the outer
    // Box, outside this capture), so the blurred result is itself part
    // transparent and the sharp content shows straight through it — the glass
    // read as a doubled/ghosted image, or as no glass at all where the list is
    // sparse. This is the difference that made the nav bar look right and these
    // screens look wrong.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()

    // A flat fill of `tint` reads as a hard-edged color mismatch once the hero
    // art (which the tint is extracted from) ends and the plain list content
    // begins. Easing into the screen's real background color over the lower
    // half turns that seam into a deliberate wash instead — same fix as
    // AlbumScreen.kt.
    val screenBackground = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to tint,
                    0.5f to tint,
                    1f to screenBackground,
                )
            )
    ) {
    HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        // Built INSIDE the provider so liquidGlass captures heroBackdrop, not the
        // root appBackdrop — sampling appBackdrop here is the RenderNode cycle.
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }

        val sideInset = LocalPlayerAwareWindowInsets.current
            .asPaddingValues()
            .calculateStartPadding(LocalLayoutDirection.current)

        // Capture from a plain Box wrapping the LazyColumn, not the LazyColumn's
        // own modifier: LazyColumn promotes its items to their own RenderNodes
        // for scroll recycling, which a capture attached directly to it doesn't
        // reliably flatten (images came through, text/icons didn't). A plain
        // Box one level up just sees "a fully-drawn child" and captures all of
        // it, same as it would any other already-rendered composable.
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
        LazyColumn(
            state = lazyListState,
            // No bounce here: the top pull drives the hero zoom instead.
            overscrollEffect = heroZoom.listOverscroll(),
            modifier = Modifier.heroPullZoom(heroZoom),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            if (artistPage == null && !showLocal) {
                item(key = "shimmer") {
                    ShimmerHost (
                        modifier = Modifier
                            .offset {
                                IntOffset(x = 0, y = headerOffset)
                            }
                    ) {
                        // Artist Image Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.1f),
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmer()
                                    .background(MaterialTheme.colorScheme.onSurface)
                                    .fadingEdge(
                                        top = systemBarsTopPadding + AppBarHeight,
                                        bottom = 200.dp,
                                    ),
                            )
                        }
                        // Artist Name and Controls Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Artist Name Placeholder
                            TextPlaceholder(
                                height = 36.dp,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(bottom = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Buttons Row Placeholder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Subscribe Button Placeholder
                                ButtonPlaceholder(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(52.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Right side buttons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Radio Button Placeholder
                                    ButtonPlaceholder(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(52.dp)
                                    )

                                    // Shuffle Button Placeholder
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .shimmer()
                                            .background(
                                                MaterialTheme.colorScheme.onSurface,
                                                RoundedCornerShape(26.dp)
                                            )
                                    )
                                }
                            }
                        }
                        // Songs List Placeholder
                        repeat(6) {
                            ListItemPlaceHolder()
                        }
                    }
                }
            } else {
                item(key = "header") {
                    val thumbnail = artistPage?.artist?.thumbnail ?: libraryArtist?.artist?.thumbnailUrl
                    val artistName = artistPage?.artist?.title ?: libraryArtist?.artist?.name

                    var backgroundVideoUrl by remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(artistName, showArtistBackgroundVideo) {
                        if (artistName != null && showArtistBackgroundVideo) {
                            withContext(Dispatchers.IO) {
                                backgroundVideoUrl = AppleMusicArtistBackgroundProvider.getByArtistName(artistName)
                            }
                        }
                    }

                    Box {
                        // Wide layout: the full-bleed square would eat the whole
                        // fold, so the portrait becomes a bounded card with the
                        // artist name set beside it (see HeroCardHeader).
                        val tabView = LocalTabView.current

                        // Artist Image with offset
                        if (!tabView && (thumbnail != null || backgroundVideoUrl != null)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .offset {
                                        IntOffset(x = 0, y = headerOffset)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = heroZoom.scale
                                            scaleY = heroZoom.scale
                                        }
                                        .fadingEdge(
                                            bottom = 200.dp,
                                        )
                                ) {
                                    if (thumbnail != null) {
                                        val heroRequest = remember(thumbnail) {
                                            ImageRequest.Builder(context)
                                                .data(thumbnail.resize(1200, 1200))
                                                // The one image in the app that has to fade
                                                // in rather than pop -- it sits inside a card
                                                // that is itself still growing out of the
                                                // tile that opened it. See AlbumScreen's own
                                                // hero request for the matching comment.
                                                .crossfade(Motion.MorphEnterMillis)
                                                .build()
                                        }
                                        AsyncImage(
                                            model = heroRequest,
                                            contentDescription = null,
                                            // Target half of the artwork morph -- the tile
                                            // tapped on the previous screen grows into this
                                            // header instead of the two cross-fading.
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                    if (backgroundVideoUrl != null && showArtistBackgroundVideo) {
                                        ArtistVideo(
                                            videoUrl = backgroundVideoUrl!!,
                                            modifier = Modifier.fillMaxSize(),
                                            onClick = { }
                                        )
                                    }
                                }
                            }
                        }

                        // Artist Name and Controls Section - positioned at bottom of image

                        if (tabView) {
                            HeroCardHeader(
                                artworkUrl = thumbnail,
                                circular = true,
                                title = {
                                    Text(
                                        text = artistName?.titlecaseWords() ?: "Unknown",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontFamily = rememberCustomArtistFontFamily() ?: rememberBrandFontFamily(),
                                        fontWeight = FontWeight.SemiBold,
                                        color = LocalAccentTextColor.current,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 42.sp,
                                    )
                                },
                                subtitle = artistPage?.subscriberCountText?.takeIf {
                                    showArtistSubscriberCount
                                }?.let { subscribers ->
                                    {
                                        Text(
                                            text = subscribers,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                        )
                                    }
                                },
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = if (thumbnail != null && !tabView) {
                                        // Position content at the bottom part of the image
                                        // Using screen width to calculate aspect ratio height minus overlap
                                        LocalResources.current.displayMetrics.widthPixels.let { screenWidth ->
                                            with(density) {
                                                ((screenWidth / 1.2f) - 144).toDp()
                                            }
                                        }
                                    } else {
                                        16.dp
                                    }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // The name moves into HeroCardHeader in tab view.
                                if (!tabView) Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {

                                    //artist video
                                    if (showArtistVideo && !(showArtistBackgroundVideo && backgroundVideoUrl != null)) {
                                        artistVideoUrl?.let { videoUrl ->
                                            artistPage?.artist?.radioEndpoint?.let { radioEndpoint ->
                                                Spacer(modifier = Modifier.width(5.dp))
                                                ArtistVideo(
                                                    videoUrl = videoUrl,
                                                    modifier = Modifier
                                                        .width(45.dp)
                                                        .height(45.dp),
                                                    onClick = {
                                                        val watchEndpoint = artistVideoSong?.endpoint
                                                            ?: artistPage?.artist?.radioEndpoint
                                                        watchEndpoint?.let {
                                                            playerConnection.playQueue(YouTubeQueue(it))
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // Artist Name — drop-cap style, first letter bigger than the rest.
                                    val displayArtistName = artistName?.titlecaseWords() ?: "Unknown"
                                    Text(
                                        text = buildAnnotatedString {
                                            if (displayArtistName.isNotEmpty()) {
                                                withStyle(SpanStyle(fontSize = 60.sp)) {
                                                    append(displayArtistName.first().uppercase())
                                                }
                                                append(displayArtistName.drop(1))
                                            } else {
                                                append(displayArtistName)
                                            }
                                        },
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontFamily = rememberCustomArtistFontFamily() ?: rememberBrandFontFamily(),
                                        fontWeight = FontWeight.SemiBold,
                                        // The page's biggest heading: carries the artwork
                                        // tint plainly rather than flat content colour.
                                        color = LocalAccentTextColor.current,
                                        // Two lines: a long name was ellipsed away on one
                                        // ("Metro Boomin" clipped mid-word behind the share
                                        // button) rather than wrapping into the space the
                                        // header already has.
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        fontSize = 44.sp,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }


                                // Buttons Row — Redesigned Play (Large) - Favorite
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Info Button (Left) — sits a touch lower than the
                                    // bigger center Play button, not perfectly centered
                                    // with it, so it reads as "grounded" beside it.
                                    GlassCircleButton(
                                        onClick = { /* Could show description or bio */ },
                                        size = 48.dp,
                                        modifier = Modifier.offset(y = 8.dp),
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.info),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Primary Play Button (Center - Large)
                                    val artistId = artistPage?.artist?.id
                                    Surface(
                                        onClick = {
                                            val isCurrentArtist = artistId != null &&
                                                mediaMetadata?.artists?.any { it.id == artistId } == true
                                            if (isPlaying && isCurrentArtist) {
                                                playerConnection.player.pause()
                                            } else if (isCurrentArtist) {
                                                playerConnection.player.play()
                                            } else {
                                                // Play artist top songs or radio
                                                val songSection = artistPage?.sections?.find { section ->
                                                    (section.items.firstOrNull() as? SongItem)?.album != null
                                                }
                                                val items = songSection?.items?.filterIsInstance<SongItem>()
                                                if (!items.isNullOrEmpty()) {
                                                    playerConnection.playQueue(
                                                        ListQueue(
                                                            title = artistName ?: "Artist",
                                                            items = items.map { it.toMediaItem() }
                                                        )
                                                    )
                                                } else {
                                                    artistPage?.artist?.radioEndpoint?.let {
                                                        playerConnection.playQueue(YouTubeQueue(it))
                                                    }
                                                }
                                            }
                                        },
                                        shape = CircleShape,
                                        color = onTint,
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            AnimatedPlayPauseIcon(
                                                isPlaying = isPlaying && artistId != null &&
                                                    mediaMetadata?.artists?.any { it.id == artistId } == true,
                                                tint = tint,
                                                size = 32.dp,
                                                modifier = Modifier.offset(x = 2.dp)
                                            )
                                        }
                                    }

                                    // Favorite/Subscribe Button (Right)
                                    val isSubscribed = libraryArtist?.artist?.bookmarkedAt != null
                                    GlassCircleButton(
                                        onClick = {
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
                                                            ).toggleLike()
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        size = 48.dp,
                                        modifier = Modifier.offset(y = 8.dp),
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                if (isSubscribed) R.drawable.favorite else R.drawable.favorite_border
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSubscribed) MaterialTheme.colorScheme.error else LocalContentColor.current
                                        )
                                    }
                                }

                                // Subscriber counts and the artist blurb sit BELOW the play
                                // controls: the buttons are what the page is for, and a long
                                // description pushed them off the fold.
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, bottom = 16.dp)
                                ) {
                                    if (showArtistSubscriberCount) {
                                        artistPage?.subscriberCountText?.let { subscribers ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .clip(chromeShape)
                                                    .then(chromeBackgroundModifier)
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.artist_screen),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = chromeContentColor
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${subscribers.split(' ').firstOrNull() ?: ""} ${stringResource(R.string.subscribers)}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = chromeContentColor,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    if (showMonthlyListeners) {
                                        artistPage?.monthlyListenerCount?.let { monthlyListeners ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .clip(chromeShape)
                                                    .then(chromeBackgroundModifier)
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.graphic_eq),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = chromeContentColor
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${monthlyListeners.split(' ').firstOrNull() ?: ""} ${stringResource(R.string.monthly_listeners)}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = chromeContentColor,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                if (!showLocal && showArtistDescription && artistPage != null) {
                                    val description = artistPage?.description
                                    val descriptionRuns = artistPage?.descriptionRuns
                                    
                                    if (!description.isNullOrEmpty() || !descriptionRuns.isNullOrEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.about_artist),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = onTint,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            
                                            androidx.compose.runtime.CompositionLocalProvider(
                                                androidx.compose.material3.LocalContentColor provides onTint,
                                            ) {
                                                ExpandableText(
                                                    text = description.orEmpty(),
                                                    runs = descriptionRuns?.map {
                                                        LinkSegment(
                                                            text = it.text,
                                                            url = it.navigationEndpoint?.urlEndpoint?.url
                                                        )
                                                    },
                                                    collapsedMaxLines = 3
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }


                if (showLocal) {
                    if (librarySongs.isNotEmpty()) {
                        item(key = "local_songs_title") {
                            NavigationTitle(
                                title = stringResource(R.string.songs),
                                modifier = Modifier.animateItem(),
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
                                trailingContent = {
                                    androidx.compose.material3.IconButton(
                                        onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null,
                                        )
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
                                                        items = librarySongs.map { it.toMediaItem() },
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
                            NavigationTitle(
                                title = stringResource(R.string.albums),
                                modifier = Modifier.animateItem(),
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
                                                    navController.navigate("album/${album.id}")
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
                        if (section.items.isNotEmpty()) {
                            item(key = "section_${section.title}") {
                                // Redesigned header to match mockup "Section >" style
                                NavigationTitle(
                                    title = when (section.title) {
                                        "Songs" -> "Top Songs"
                                        "Popular" -> "Popular"
                                        else -> section.title
                                    },
                                    modifier = Modifier.animateItem(),
                                    onClick = section.moreEndpoint?.let {
                                        {
                                            navController.navigate(
                                                "artist/${viewModel.artistId}/items?browseId=${it.browseId}?params=${it.params}",
                                            )
                                        }
                                    },
                                )
                            }
                        }

                        // Check if this is a "Latest Release" style section to render as a card
                        val firstItem = section.items.firstOrNull()
                        if (section.title.contains("Latest", ignoreCase = true) && firstItem is AlbumItem) {
                            item(key = "featured_release") {
                                FeaturedReleaseCard(
                                    album = firstItem,
                                    onTint = onTint,
                                    onClick = { navController.navigate("album/${firstItem.id}") },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).animateItem()
                                )
                            }
                        } else if ((section.items.firstOrNull() as? SongItem)?.album != null) {
                            // Was also recomputed inside every row's listItemShape(...),
                            // i.e. a full distinctBy pass per row.
                            val distinctSongs = section.items.distinctBy { it.id }
                            itemsIndexed(
                                items = distinctSongs,
                                key = { _, it -> "youtube_song_${it.id}" },
                            ) { index, song ->
                                YouTubeListItem(
                                    item = song as SongItem,
                                    isActive = mediaMetadata?.id == song.id,
                                    isPlaying = isPlaying,
                                    shape = listItemShape(index, distinctSongs.size),
                                    flat = true,
                                    trailingContent = {
                                        androidx.compose.material3.IconButton(
                                            onClick = {
                                                menuState.show {
                                                    YouTubeSongMenu(
                                                        song = song,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
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
                                        )
                                        .animateItem(),
                                )
                            }
                        } else {
                            item(key = "section_list_${section.title}") {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp).plusStart(sideInset),
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
                                            thumbnailRatio = 1f, // Use square thumbnails for all items in horizontal scroll
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

                                                            is AlbumItem -> navController.navigate("album/${item.id}")
                                                            is ArtistItem -> navController.navigate("artist/${item.id}")
                                                            is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
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
            }
        )
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )

        // Floating glass back/share buttons over the hero art, replacing the
        // Material TopAppBar — always visible, no title-bar-on-scroll behavior.
        // Backed by a scrim that ramps from transparent to a dark shade as the
        // list scrolls past the hero art, so the buttons stay legible over
        // regular content too.
        // GlassCircleButton below reads LocalAppBackdrop at its own composition
        // point, so wrapping it here (not redeclaring a val above) is enough.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppBarHeight * 2.5f)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.55f * chromeScrimAlpha),
                            0.35f to Color.Black.copy(alpha = 0.4f * chromeScrimAlpha),
                            0.7f to Color.Black.copy(alpha = 0.12f * chromeScrimAlpha),
                            1f to Color.Transparent,
                        )
                    )
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
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }

            GlassCircleButton(
                onClick = {
                    viewModel.artistPage?.artist?.shareLink?.let { link ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Artist Link", link)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.link),
                    contentDescription = null,
                )
            }
            }
        }
        }
      }
    }
}

@Composable
fun FeaturedReleaseCard(
    album: AlbumItem,
    onTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.thumbnail.resize(400, 400),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album.year?.toString() ?: "Latest Release",
                    style = MaterialTheme.typography.labelMedium,
                    color = onTint.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = onTint,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Album",
                    style = MaterialTheme.typography.bodySmall,
                    color = onTint.copy(alpha = 0.5f),
                    maxLines = 1
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null,
                    tint = onTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Uppercases the first letter of each word, leaving the rest of the casing alone.
 *
 * The artist header used to `.lowercase()` the whole name and rely on a drop-cap span
 * to restore the first letter, so "Metro Boomin" rendered as "Metro boomin". Only
 * touching each word's leading character fixes that without flattening names whose
 * casing is deliberate — SZA, A$AP Rocky and MF DOOM all survive unchanged, which a
 * lowercase-then-capitalize pass would have mangled.
 */
private fun String.titlecaseWords(): String =
    split(' ').joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercaseChar() }
    }
