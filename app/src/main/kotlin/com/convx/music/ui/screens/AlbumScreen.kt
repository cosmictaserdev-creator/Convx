/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.utils.sharedTitleTarget

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.valentinilk.shimmer.shimmer
import com.convx.music.ui.utils.Motion
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.Checkbox
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.ripple
import com.convx.music.ui.ios.compat.Surface
import androidx.compose.foundation.shape.CircleShape
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.core.net.toUri
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.Color
import com.convx.music.constants.AppBarHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastForEachReversed
import org.koin.androidx.compose.koinViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.ui.utils.resize
import com.convx.music.LocalDatabase
import com.convx.music.LocalTabView
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.composables.sfsymbols.SfSymbols
import androidx.compose.ui.res.painterResource
import com.composables.sfsymbols.monochrome.SFECircle
import com.composables.sfsymbols.monochrome.SFXmark
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFSquareStack
import com.composables.sfsymbols.monochrome.SFStar
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.AlbumCanvasEnabledKey
import com.convx.music.db.entities.Album
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.ExoDownloadService
import com.convx.music.playback.combinedDownloadState
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.LocalAlbumRadio
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.AlbumGradient
import com.convx.music.ui.component.ExpandableText
import com.convx.music.ui.component.ContrastCircleButton
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HeroCardHeader
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.LinkSegment
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.resolvedTextColor
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.isGlassAllowed
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.ui.component.asSoftIconTint
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.SelectionSongMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.pendingSharedArtworkThumbFor
import com.convx.music.ui.component.SongRowSkeleton
import com.convx.music.ui.component.ShimmerLine
import com.convx.music.ui.component.ThumbThenFullHero
import com.convx.music.ui.component.SeededShimmerHero
import com.convx.music.ui.component.GlassPillRow
import com.convx.music.ui.component.pillGlassBackground
import com.convx.music.ui.component.backExitModifier
import com.convx.music.ui.component.rememberDetailChromeEntrance
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.utils.fadingEdge
import com.convx.music.ui.player.CanvasArtworkPlayer
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.component.rememberHeroTopBlur
import com.convx.music.utils.listItemShape
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberPreference
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import com.convx.music.viewmodels.AlbumViewModel
import com.convx.music.ui.component.HeroMergeBand
import com.convx.music.ui.component.HeroMergeBandHeight
import com.convx.music.ui.component.HeroScrollBlur
import com.convx.music.ui.component.HeroTopBlur
import com.music.innertube.models.SongItem
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: AlbumViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current.get()
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return

    val scope = rememberCoroutineScope()

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val playlistId by viewModel.playlistId.collectAsState()
    val albumWithSongs by viewModel.albumWithSongs.collectAsState()
    val artistSignatureUrl by viewModel.artistSignatureUrl.collectAsState()
    val artistBundledLogo by viewModel.artistBundledLogo.collectAsState()
    val otherVersions by viewModel.otherVersions.collectAsState()
    val releasesForYou by viewModel.releasesForYou.collectAsState()
    val description by viewModel.description.collectAsState()
    val descriptionRuns by viewModel.descriptionRuns.collectAsState()
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)
    val dataSaverEnabled by rememberPreference(key = DataSaverEnabledKey, defaultValue = false)
    val hideVideoSongsPref by rememberPreference(key = HideVideoSongsKey, defaultValue = false)
    val hideVideoSongs = if (dataSaverEnabled) true else hideVideoSongsPref
    val albumCanvasEnabledPref by rememberPreference(key = AlbumCanvasEnabledKey, defaultValue = false)
    val albumCanvasEnabled = if (dataSaverEnabled) false else albumCanvasEnabledPref

    val albumCanvas = rememberAlbumCanvas(
        albumTitle = albumWithSongs?.album?.title,
        artistName = albumWithSongs?.artists?.joinToString { it.name }?.takeIf { it.isNotEmpty() },
        firstSongTitle = albumWithSongs?.songs?.firstOrNull()?.song?.title
    )
    val canvasArtwork = albumCanvas.artwork

    val filteredSongs = remember(albumWithSongs, hideExplicit, hideVideoSongs) {
        var songs = albumWithSongs?.songs ?: emptyList()
        if (hideExplicit) {
            songs = songs.filter { !it.song.explicit }
        }
        if (hideVideoSongs) {
            songs = songs.filter { !it.song.isVideo }
        }
        songs
    }

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }
    if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }
    LaunchedEffect(filteredSongs) {
        selection.fastForEachReversed { songId ->
            if (filteredSongs.find { it.id == songId } == null) {
                selection.remove(songId)
            }
        }
    }

    val downloadUtil = LocalDownloadUtil.current.get()
    var showRemoveDownloadDialog by rememberSaveable { mutableStateOf(false) }
    if (showRemoveDownloadDialog) {
        com.convx.music.ui.component.ActionPromptDialog(
            title = stringResource(R.string.remove_download),
            onDismiss = { showRemoveDownloadDialog = false },
            onCancel = { showRemoveDownloadDialog = false },
            onConfirm = {
                showRemoveDownloadDialog = false
                com.convx.music.playback.removeDownloads(context, filteredSongs.map { it.id })
            },
        ) { Text(stringResource(R.string.remove_download)) }
    }
    var downloadState by remember {
        mutableIntStateOf(Download.STATE_STOPPED)
    }

    LaunchedEffect(albumWithSongs) {
        val songs = albumWithSongs?.songs
        if (songs.isNullOrEmpty()) return@LaunchedEffect
        downloadUtil.downloads.collect { downloads ->
            downloadState = combinedDownloadState(songs, downloads) { it.id }
        }
    }

    val hasExplicitContent = remember(albumWithSongs) {
        albumWithSongs?.album?.explicit == true
    }

    val lazyListState = rememberLazyListState()

    // Per-screen Haze source: the artwork head + its tint plane together become the
    // backdrop that HeroMergeBand/HeroTopBlur sample through, so the hero visibly
    // dissolves into the page and under the status bar. Self-contained per screen —
    // no global threading through the composition tree.
    val hazeState = remember { HazeState() }

    val transparentAppBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset < 100
        }
    }

    // How far scrolled past the hero art, 0..1 — same progressive dark scrim
    // treatment as the Artist screen's floating chrome.
    val chromeScrimAlpha by remember {
        derivedStateOf {
            val scrolledPx = if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                300f
            }
            (scrolledPx / 300f).coerceIn(0f, 1f)
        }
    }

    // Scroll progress driving the heroart progressive blur + the top blur strip:
    // hoisted so the header artwork and the floating chrome share one value.
    val heroTopBlurProgress = rememberHeroTopBlur(lazyListState)
    val chrome = com.convx.music.ui.component.rememberDetailChromeEntrance(iconCount = 2)

    val instantThumbUrl = viewModel.thumbArg
        ?: pendingSharedArtworkThumbFor(viewModel.albumId)
        ?: albumWithSongs?.album?.thumbnailUrl
    val bgUrl = instantThumbUrl ?: albumWithSongs?.album?.thumbnailUrl

    // Apple Music colours, unified with the artist page: Apple's server-computed bgColor
    // through pageBackground (99% path); unmatched albums fall back to the on-device
    // estimateDirect of that bgColor. Flat white text — the foot wash does the contrast work.
    val appleAlbumState = com.convx.music.utils.rememberAppleAlbum(
        albumWithSongs?.album?.title,
        albumWithSongs?.artists?.firstOrNull()?.name,
    )
    val appleAlbumLoading = appleAlbumState.loading
    val appleAlbum = appleAlbumState.art
    val albumArtistState = com.convx.music.utils.rememberAppleArtist(
        albumWithSongs?.artists?.firstOrNull()?.name.orEmpty(),
    )
    // Only estimate from YouTube's art once Apple has settled without a match.
    val estimatedArtworkBg = com.convx.music.utils.rememberEstimatedArtworkBg(
        bgUrl.takeIf { !appleAlbumLoading && appleAlbum == null },
    )
    val tint by androidx.compose.animation.animateColorAsState(
        com.convx.music.ui.theme.AppleColors.color(
            com.convx.music.ui.theme.AppleColors.pageBackground(
                appleAlbum?.bgColor ?: estimatedArtworkBg ?: 0x1C1C1E,
            ),
        ),
        label = "albumPage",
    )
    com.convx.music.PublishAmbientBackgroundColor(tint)
    val onTint = androidx.compose.ui.graphics.Color.White

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.resolvedTextColor() else AppTheme.colorScheme.onSurface

    // The unattached backdrop avoids a RenderNode cycle in the floating chrome.
    val heroBackdrop = rememberLayerBackdrop()

    // The chrome samples this attached sibling backdrop, not its own draw pass.
    // Include the page tint so the blurred surface matches the visible page.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()
    val heroZoom = rememberHeroZoom()

    // Blend the artwork tint into the page background below the hero to hide the color seam.
    val screenBackground = AppTheme.colorScheme.background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint)
    ) {
    HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
    // Capture through a plain Box; LazyColumn's recycled item layers do not flatten reliably.
    Box(modifier = Modifier
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
        val albumWithSongs = albumWithSongs
        if (albumWithSongs != null && albumWithSongs.songs.isNotEmpty()) {
             item(key = "album_header", contentType = "header") {
                val title = albumWithSongs.album.title
                val artistName = albumWithSongs.artists.joinToString { it.name }
                val albumInfo = buildString {
                    albumWithSongs.album.year?.let { append(it).append(" \u00b7 ") }
                    append(pluralStringResource(R.plurals.n_song, filteredSongs.size, filteredSongs.size))
                    val duration = filteredSongs.sumOf { it.song.duration }
                    if (duration > 0) append(" \u00b7 ").append(makeTimeString(duration * 1000L))
                }
                com.convx.music.ui.component.CollectionDetailHeader(
                    title = title,
                    subtitle = title,
                    artwork = appleAlbum?.sized(1200) ?: instantThumbUrl ?: albumWithSongs.album.thumbnailUrl,
                    metadata = albumInfo,
                    description = description,
                    descriptionRuns = descriptionRuns?.map { LinkSegment(it.text, it.navigationEndpoint?.urlEndpoint?.url) },
                    foreground = onTint,
                    playing = isPlaying && filteredSongs.any { it.id == mediaMetadata?.id },
                    enabled = filteredSongs.isNotEmpty(),
                    onPlay = {
                        if (filteredSongs.any { it.id == mediaMetadata?.id }) playerConnection.togglePlayPause()
                        else {
                            playerConnection.service.getAutomix(playlistId)
                            playerConnection.playQueue(LocalAlbumRadio(albumWithSongs))
                        }
                    },
                    onShuffle = {
                        playerConnection.service.getAutomix(playlistId)
                        playerConnection.playQueue(LocalAlbumRadio(albumWithSongs.copy(songs = albumWithSongs.songs.shuffled())))
                    },
                    rightSymbol = if (downloadState == Download.STATE_COMPLETED) "checkmark" else "arrow.down",
                    onRightAction = {
                        when (downloadState) {
                            Download.STATE_COMPLETED -> showRemoveDownloadDialog = true
                            Download.STATE_DOWNLOADING -> com.convx.music.playback.cancelDownloads(context, filteredSongs.map { it.id }, downloadUtil.downloads.value)
                            else -> com.convx.music.playback.downloadSongs(context,
                                filteredSongs.map { com.convx.music.playback.DownloadTarget(it.id, it.song.title, it.artists.firstOrNull()?.name, it.song.duration) }, downloadUtil.downloads.value)
                        }
                    },
                    titleContent = {
                        Box(Modifier.fillMaxWidth().height(75.dp).padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
                            if (!albumArtistState.loading || artistBundledLogo != null || artistSignatureUrl != null) {
                                com.convx.music.ui.screens.artist.ArtistWordmark(
                                    logo = albumArtistState.artist?.logo,
                                    name = artistName,
                                    signatureUrl = artistSignatureUrl,
                                    bundledLogo = artistBundledLogo,
                                )
                            }
                        }
                    },
                    artworkOverlay = {
                        if (albumCanvasEnabled && canvasArtwork != null) {
                            CanvasArtworkPlayer(primaryUrl = canvasArtwork.animated, fallbackUrl = canvasArtwork.videoUrl,
                                isPlaying = !com.convx.music.ui.utils.artworkTransitionRunning(),
                                onExhausted = albumCanvas.onError, onReady = albumCanvas.onReady,
                                modifier = Modifier.fillMaxSize())
                        }
                    },
                )
            }

            if (filteredSongs.isNotEmpty()) {


                itemsIndexed(
                    items = filteredSongs,
                    key = { _, song -> song.id },
                    contentType = { _, _ -> "song_row" },
                ) { index, song ->
                    val onCheckedChange: (Boolean) -> Unit = {
                        if (it) {
                            selection.add(song.id)
                        } else {
                            selection.remove(song.id)
                        }
                    }

                    SongListItem(
                        song = song,
                        albumIndex = index + 1,
                        subtitleColor = onTint.copy(alpha = 0.78f),
                        subtitleAdditiveChrome = false,
                        leadingDivider = index == 0,
                        thumbnailStartInset = 0.dp,
                        isActive = song.id == mediaMetadata?.id,
                        isPlaying = isPlaying,
                        showInLibraryIcon = true,
                        shape = listItemShape(index, filteredSongs.size),
                        flat = true,
                        trailingContent = {
                            if (inSelectMode) {
                                Checkbox(
                                    checked = song.id in selection,
                                    onCheckedChange = onCheckedChange
                                )
                            } else {
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
                                ) {
                                    SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                                }
                            }
                        },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .combinedBounceClick(
                                onClick = {
                                    if (inSelectMode) {
                                        onCheckedChange(song.id !in selection)
                                    } else if (song.id == mediaMetadata?.id) {
                                        playerConnection.togglePlayPause()
                                    } else {
                                        playerConnection.service.getAutomix(playlistId)
                                        playerConnection.playQueue(
                                            LocalAlbumRadio(albumWithSongs, startIndex = index),
                                        )
                                    }
                                },
                                onLongClick = {
                                    if (!inSelectMode) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        inSelectMode = true
                                        onCheckedChange(true)
                                    }
                                },
                            ),
                    )
                }
            }

            if (otherVersions.isNotEmpty()) {
                item(key = "other_versions_title") {
                    NavigationTitle(
                        title = stringResource(R.string.other_versions),
                        modifier = Modifier.animateItem()
                    )
                }
                item(key = "other_versions_list") {
                    LazyRow(
                        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues(),
                    ) {
                        items(
                            items = otherVersions.distinctBy { it.id },
                            key = { it.id },
                        ) { item ->
                            YouTubeGridItem(
                                item = item,
                                isActive = mediaMetadata?.album?.id == item.id,
                                isPlaying = isPlaying,
                                coroutineScope = scope,
                                modifier =
                                Modifier
                                    .combinedBounceClick(
                                        onClick = { navController.navigate(albumRoute(item.id, item.thumbnail)) },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                YouTubeAlbumMenu(
                                                    albumItem = item,
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
                }
            }

            if (releasesForYou.isNotEmpty()) {
                item(key = "releases_for_you_title") {
                    NavigationTitle(
                        title = stringResource(R.string.releases_for_you),
                        modifier = Modifier.animateItem()
                    )
                }
                item(key = "releases_for_you_list") {
                    LazyRow(
                        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues(),
                    ) {
                        items(
                            items = releasesForYou.distinctBy { it.id },
                            key = { it.id },
                        ) { item ->
                            YouTubeGridItem(
                                item = item,
                                isActive = mediaMetadata?.album?.id == item.id,
                                isPlaying = isPlaying,
                                coroutineScope = scope,
                                modifier =
                                Modifier
                                    .combinedBounceClick(
                                        onClick = { navController.navigate(albumRoute(item.id, item.thumbnail)) },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                YouTubeAlbumMenu(
                                                    albumItem = item,
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
                }
            }

            item(key = "bottom_spacer") {
                Spacer(Modifier.height(50.dp))
            }
        } else {
            // Loading mirrors loaded: same hero size/offset/foot-fade/merge band on
            // the seeded bg, skeleton lines standing where title/artist/actions/
            // about/songs will land — so arrival swaps content, never layout.
            item(key = "shimmer_header", contentType = "header") {
                com.convx.music.ui.component.CollectionDetailHeader(
                    title = com.convx.music.ui.utils.selectedArtworkTitle().orEmpty(),
                    artwork = instantThumbUrl,
                    metadata = "",
                    foreground = onTint,
                    playing = false,
                    enabled = false,
                    onPlay = {},
                    onShuffle = {},
                    titleContent = { Box(Modifier.fillMaxWidth().height(75.dp), contentAlignment = Alignment.Center) { ShimmerLine(.55f, 34.dp) } },
                )
            }
            item(key = "shimmer_songs_title") {
                Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                    ShimmerLine(fraction = 0.25f, height = 20.dp)
                }
            }
            items(8) { index ->
                Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    SongRowSkeleton(index = index)
                }
            }
        }
    }
    }

        // Float navigation and selection actions over the hero; the scrim darkens as the list scrolls.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        // Draw the blur before the chrome row so it stays behind the buttons.
        com.convx.music.ui.component.TopFadeBlur(
            hazeState = hazeState,
            pageColor = tint,
            modifier = Modifier.align(Alignment.TopCenter),
        )
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
            val ambientColor = LocalAmbientBackgroundColor.current.value
            val pillIconTint = if (useGlass && ambientColor.isSpecified) ambientColor.asSoftIconTint() else chromeContentColor
            if (inSelectMode) {
                GlassCircleButton(onClick = onExitSelectionMode) {
                    SymbolIcon(symbol = sf("SFXmark"), contentDescription = null)
                }

                Text(
                    text = pluralStringResource(R.plurals.n_selected, selection.size, selection.size),
                    style = AppTheme.typography.titleMedium,
                    color = chromeContentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                GlassPillRow(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(chromeShape)
                        .then(pillGlassBackground(chromeShape))
                        .padding(horizontal = 8.dp),
                ) {
                    Checkbox(
                        checked = selection.size == filteredSongs.size && selection.isNotEmpty(),
                        onCheckedChange = {
                            if (selection.size == filteredSongs.size) {
                                selection.clear()
                            } else {
                                selection.clear()
                                selection.addAll(filteredSongs.map { it.id })
                            }
                        }
                    )
                    IconButton(
                        enabled = selection.isNotEmpty(),
                        onClick = {
                            menuState.show {
                                SelectionSongMenu(
                                    songSelection = selection.mapNotNull { songId ->
                                        filteredSongs.find { it.id == songId }
                                    },
                                    onDismiss = menuState::dismiss,
                                    clearAction = onExitSelectionMode
                                )
                            }
                        }
                    ) {
                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier.additiveChrome(useGlass && ambientColor.isSpecified), tint = pillIconTint)
                    }
                }
            } else {
                GlassCircleButton(
                    onClick = { navController.navigateUp() },
                    onLongClick = { navController.backToMain() },
                    modifier = with(chrome) { Modifier.backEnter().then(backExitModifier()) },
                ) {
                    SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                }

                Spacer(Modifier.weight(1f))

                albumWithSongs?.let { albumWithSongs ->
                    GlassPillRow(
                        modifier = with(chrome) { Modifier.containerEnter() }
                            .height(48.dp)
                            .clip(chromeShape)
                            .then(pillGlassBackground(chromeShape))
                            .padding(horizontal = 8.dp),
                    ) {
                        IconButton(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "https://music.youtube.com/playlist?list=${albumWithSongs.album.playlistId}"
                                    )
                                }
                                context.startActivity(
                                    android.content.Intent.createChooser(
                                        intent,
                                        null
                                    )
                                )
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = stringResource(R.string.share), size = 20.dp, modifier = with(chrome) {
                                    Modifier.iconEnter(0)
                                        
                                        .additiveChrome(useGlass && ambientColor.isSpecified)
                                }, tint = pillIconTint)
                        }

                        IconButton(
                            onClick = {
                                menuState.show {
                                    AlbumMenu(
                                        originalAlbum = Album(
                                            albumWithSongs.album,
                                            albumWithSongs.artists
                                        ),
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = stringResource(R.string.more_options), size = 20.dp, modifier = with(chrome) {
                                    Modifier.iconEnter(1)
                                        
                                        .additiveChrome(useGlass && ambientColor.isSpecified)
                                }, tint = pillIconTint)
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
