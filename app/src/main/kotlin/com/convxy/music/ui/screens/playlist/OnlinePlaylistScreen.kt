/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.playlist

import com.convxy.music.ui.utils.FloatingChromeSpacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEachReversed
import androidx.media3.exoplayer.offline.Download
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.WatchEndpoint
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.PlaylistEntity
import com.convxy.music.db.entities.PlaylistSongMap
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.YouTubePlaylistQueue
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.ChromeScrim
import com.convxy.music.ui.component.rememberChromeScrimProgress
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.backdrop.backdrops.LayerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.YouTubeGridItem
import com.convxy.music.ui.component.YouTubeListItem
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.HeroCardHeader
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.AlbumStyleHeroImage
import com.convxy.music.LocalTabView
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.theme.rememberArtworkTint
import com.convxy.music.ui.menu.YouTubeAlbumMenu
import com.convxy.music.ui.menu.YouTubeArtistMenu
import com.convxy.music.ui.menu.YouTubePlaylistMenu
import com.convxy.music.ui.menu.YouTubeSelectionSongMenu
import com.convxy.music.ui.menu.YouTubeSongMenu
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.makeTimeString
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.OnlinePlaylistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.compose.material3.CircularProgressIndicator
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.OnlineBlur
import com.convxy.music.constants.AppBarHeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.cancelDownloads
import com.convxy.music.playback.downloadSongs
import com.convxy.music.playback.removeDownloads

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnlinePlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: OnlinePlaylistViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val playlist by viewModel.playlist.collectAsState()
    val songs by viewModel.playlistSongs.collectAsState()
    val dbPlaylist by viewModel.dbPlaylist.collectAsState()
    val relatedItems by viewModel.relatedItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()

    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)

    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    val downloadUtil = LocalDownloadUtil.current
    val context = LocalContext.current
    // Collected rather than folded into a LaunchedEffect so the current state of each
    // download is also available at click time — the download and cancel actions filter
    // on it, see DownloadActions.
    val downloads by downloadUtil.downloads.collectAsState()

    val downloadState = remember(songs, downloads) {
        // Bound to a local first: `songs` is a delegated val, which blocks the smart cast
        // the null check below would otherwise give us.
        val current = songs
        when {
            current.isNullOrEmpty() -> Download.STATE_STOPPED
            current.all { downloads[it.id]?.state == Download.STATE_COMPLETED } ->
                Download.STATE_COMPLETED

            current.all {
                downloads[it.id]?.state == Download.STATE_QUEUED ||
                    downloads[it.id]?.state == Download.STATE_DOWNLOADING ||
                    downloads[it.id]?.state == Download.STATE_COMPLETED
            } -> Download.STATE_DOWNLOADING

            else -> Download.STATE_STOPPED
        }
    }

    val filteredSongs = remember(songs, query) {
        if (query.text.isEmpty()) songs.mapIndexed { i, s -> i to s }
        else songs.mapIndexed { i, s -> i to s }.filter {
            it.second.title.contains(query.text, true) ||
                    it.second.artists.fastAny { a -> a.name.contains(query.text, true) }
        }
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

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isSearching) { if (isSearching) focusRequester.requestFocus() }

    LaunchedEffect(filteredSongs) {
        selection.fastForEachReversed { songId ->
            if (filteredSongs.find { it.second.id == songId } == null) {
                selection.remove(songId)
            }
        }
    }

    val transparentAppBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset < 150
        }
    }

    if (isSearching) {
        BackHandler {
            isSearching = false
            query = TextFieldValue()
        }
    } else if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    // Apple Music style: a single dominant color pulled from the playlist's own
    // artwork washes the screen, and the circular back/search/select chrome is
    // real liquid glass sampling this screen's own content behind it — same
    // treatment as Artist/Album.
    val artworkColors = rememberArtworkTint(playlist?.thumbnail)
    val screenBackground = MaterialTheme.colorScheme.background
    val tintColor by animateColorAsState(
        targetValue = artworkColors.getOrNull(0)?.copy(alpha = 0.55f) ?: Color.Transparent,
        animationSpec = tween(durationMillis = 800),
        label = "OnlinePlaylistScreenTint",
    )

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.textColor else MaterialTheme.colorScheme.onSurface

    // Unattached backdrop (never .layerBackdrop'd): glass chrome sampling it
    // early-returns → translucent frosted surface, no RenderNode self-reference.
    // See ArtistScreen.kt for the full explanation of the cycle this avoids.
    val heroBackdrop = rememberLayerBackdrop()

    // A SECOND, ATTACHED backdrop: .layerBackdrop'd onto the LazyColumn below,
    // which is a *sibling* of the floating chrome row, not an ancestor — the
    // chrome row samples a texture of already-drawn list content, it never
    // captures its own draw pass, so no cycle. This is what lets the chrome
    // buttons show real blurred list/hero content instead of heroBackdrop's
    // flat empty-capture fallback.
    val heroZoom = rememberHeroZoom()

    val heroSource = rememberHeroSource (
        staticArt = playlist?.thumbnail,
        songs = songs.map { it.thumbnail to false },
    )
    val tint = rememberHeroTint(playlist?.thumbnail)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        // Built INSIDE the provider so liquidGlass captures heroBackdrop, not the
        // root appBackdrop — sampling appBackdrop here is the RenderNode cycle.
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }
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
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                .union(WindowInsets.ime)
                .asPaddingValues(),
        ) {
            if (playlist == null || songs.isEmpty()) {
                if (isLoading) {
                    item(key = "loading_placeholder") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                playlist?.let { playlist ->
                    if (isSearching) {
                        // No hero header in search mode — reserve the floating chrome's
                        // height so the first row doesn't start under the status bar.
                        item(key = "search_chrome_spacer", contentType = "spacer") { FloatingChromeSpacer() }
                    }
                    if (!isSearching) {
                        item(key = "playlist_header", contentType = "header") {
                            OnlinePlaylistHeader(
                                playlist = playlist,
                                songs = songs,
                                dbPlaylist = dbPlaylist,
                                downloadState = downloadState,
                                navController = navController,
                                coroutineScope = coroutineScope,
                                continuation = viewModel.continuation,
                                heroBackdrop = heroBackdrop,
                                heroScale = heroZoom.scale,
                            )
                        }
                    }

                    itemsIndexed(
                        filteredSongs,
                        key = { _, (_, item) -> item.id },
                        contentType = { _, _ -> "song_row" }
                    ) { index, (_, songItem) ->
                        val onCheckedChange: (Boolean) -> Unit = {
                            if (it) {
                                selection.add(songItem.id)
                            } else {
                                selection.remove(songItem.id)
                            }
                        }

                        YouTubeListItem(
                            item = songItem,
                            isActive = mediaMetadata?.id == songItem.id,
                            isPlaying = isPlaying,
                            isSelected = inSelectMode && songItem.id in selection,
                            shape = listItemShape(index, filteredSongs.size),
                            flat = true,
                            modifier = Modifier
                                .combinedBounceClick(
                                    enabled = !hideExplicit || !songItem.explicit,
                                    onClick = {
                                        if (inSelectMode) {
                                            onCheckedChange(songItem.id !in selection)
                                        } else if (songItem.id == mediaMetadata?.id) {
                                            playerConnection.togglePlayPause()
                                        } else {
                                            playerConnection.playQueue(
                                                YouTubePlaylistQueue(
                                                    playlistId = playlist.id,
                                                    playlistTitle = playlist.title,
                                                    initialSongs = filteredSongs.map { it.second },
                                                    initialContinuation = viewModel.continuation,
                                                    startIndex = index
                                                )
                                            )
                                        }
                                    },
                                    onLongClick = {
                                        if (!inSelectMode) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            inSelectMode = true
                                            onCheckedChange(true)
                                        }
                                    }
                                )
                                .animateItem(),
                            trailingContent = {
                                if (inSelectMode) {
                                    Checkbox(
                                        checked = songItem.id in selection,
                                        onCheckedChange = onCheckedChange
                                    )
                                } else {
                                    IconButton(onClick = {
                                        menuState.show {
                                            YouTubeSongMenu(songItem, navController, menuState::dismiss)
                                        }
                                    }) {
                                        Icon(painterResource(R.drawable.more_vert), null)
                                    }
                                }
                            }
                        )
                    }

                    if (isLoadingMore) {
                        item(key = "loading_more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ContainedLoadingIndicator()
                            }
                        }
                    }

                    if (relatedItems.isNotEmpty() && !isSearching) {
                        item(key = "related_title") {
                            NavigationTitle(
                                title = stringResource(R.string.you_might_also_like),
                                modifier = Modifier.animateItem()
                            )
                        }

                        item(key = "related_items") {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                            ) {
                                items(relatedItems, key = { it.id }) { item ->
                                    YouTubeGridItem(
                                        item = item,
                                        modifier = Modifier
                                            .width(160.dp)
                                            .combinedBounceClick(
                                                onClick = {
                                                    when (item) {
                                                        is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                                                        is AlbumItem -> navController.navigate("album/${item.browseId}")
                                                        is ArtistItem -> navController.navigate("artist/${item.id}")
                                                        is SongItem -> playerConnection.playQueue(
                                                            YouTubeQueue(WatchEndpoint(videoId = item.id))
                                                        )
                                                    }
                                                },
                                                onLongClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    menuState.show {
                                                        when (item) {
                                                            is PlaylistItem -> YouTubePlaylistMenu(
                                                                playlist = item,
                                                                coroutineScope = coroutineScope,
                                                                onDismiss = menuState::dismiss
                                                            )
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
                                                        }
                                                    }
                                                }
                                            )
                                    )
                                }
                            }
                        }
                    }

                    item(key = "bottom_spacer") {
                        Spacer(Modifier.height(50.dp))
                    }
                }
            }
        }
        }

        // A YouTube playlist keeps its own track order, so the rail is a proportional
        // thumb rather than letters.
        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = songs.size,
            sectionIndexMap = null,
        )

        // Floating glass chrome over the tinted background, replacing the
        // Material TopAppBar. Select mode and in-place search keep their exact
        // prior behavior, just restyled containers; the title still only fades
        // in once scrolled past the hero, same as before.
        val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
        // Built INSIDE the provider so liquidGlass captures listBackdrop (the
        // LazyColumn's own recorded content, a sibling — see its declaration
        // above), not the root appBackdrop — sampling appBackdrop here is the
        // RenderNode cycle. listBackdrop is attached (unlike heroBackdrop), so
        // this chrome actually shows blurred list/hero content, not just a
        // flat tinted fallback.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
        ChromeScrim(progress = chromeScrimProgress)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(appTopBarWindowInsets())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (inSelectMode) {
                GlassCircleButton(onClick = onExitSelectionMode) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                }

                Text(
                    text = pluralStringResource(R.plurals.n_song, selection.size, selection.size),
                    style = MaterialTheme.typography.titleMedium,
                    color = chromeContentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(chromeShape)
                        .then(chromeBackgroundModifier)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = selection.size == filteredSongs.size && selection.isNotEmpty(),
                        onCheckedChange = {
                            if (selection.size == filteredSongs.size) {
                                selection.clear()
                            } else {
                                selection.clear()
                                selection.addAll(filteredSongs.map { it.second.id })
                            }
                        }
                    )
                    IconButton(
                        enabled = selection.isNotEmpty(),
                        onClick = {
                            menuState.show {
                                YouTubeSelectionSongMenu(
                                    songSelection = filteredSongs.filter { it.second.id in selection }
                                        .map { it.second },
                                    onDismiss = menuState::dismiss,
                                    clearAction = onExitSelectionMode
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert),
                            contentDescription = null
                        )
                    }
                }
            } else if (isSearching) {
                GlassCircleButton(
                    onClick = {
                        isSearching = false
                        query = TextFieldValue()
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = null
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(chromeShape)
                        .then(chromeBackgroundModifier)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search),
                                style = MaterialTheme.typography.titleMedium,
                                color = chromeContentColor.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = chromeContentColor),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = chromeContentColor,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                }
            } else {
                GlassCircleButton(
                    onClick = { navController.navigateUp() },
                    onLongClick = { navController.backToMain() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = null
                    )
                }

                AnimatedContent(
                    targetState = !transparentAppBar,
                    transitionSpec = {
                        fadeIn().togetherWith(fadeOut())
                    },
                    label = "TopAppBarTitle",
                    modifier = Modifier.weight(1f)
                ) { show ->
                    if (show) {
                        Text(
                            text = playlist?.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = chromeContentColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                GlassCircleButton(onClick = { isSearching = true }) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null
                    )
                }
            }
        }
        }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
      }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OnlinePlaylistHeader(
    playlist: PlaylistItem,
    songs: List<SongItem>,
    dbPlaylist: Playlist?,
    downloadState: Int,
    navController: NavController,
    coroutineScope: CoroutineScope,
    continuation: String?,
    heroBackdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    heroScale: Float = 1f,
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val downloads by LocalDownloadUtil.current.downloads.collectAsState()
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val hasExplicitContent = remember(songs) {
        songs.any { it.explicit }
    }

    // Same glass/tint chrome as the enclosing OnlinePlaylistScreen's floating
    // top bar — recomputed here since this is a separate composable function.
    val artworkColors = rememberArtworkTint(playlist.thumbnail)
    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.textColor else MaterialTheme.colorScheme.onSurface
    val chromeBackgroundModifier = if (useGlass) {
        Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
    } else {
        Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f), chromeShape)
    }

    val heroUrl = playlist.thumbnail
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = songs.map { it.thumbnail to false },
    )
    val tint = rememberHeroTint(heroUrl)
    val onTint = AppleTokens.onColor(tint)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Wide layout: bounded artwork card with the title beside it instead of
            // the full-bleed square, which would fill the fold on a tablet.
            if (LocalTabView.current) {
                HeroCardHeader(
                    artworkUrl = heroUrl,
                    title = {
                        Text(
                            text = playlist.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = onTint,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    subtitle = playlist.author?.name?.let { author ->
                        {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.titleMedium,
                                color = onTint.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                )
            } else {
                AlbumStyleHeroImage(artworkUrl = heroUrl, heroScale = heroScale)

                Spacer(Modifier.height(16.dp))

                // Title
                Text(
                    text = playlist.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = onTint,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

                // Action Buttons Row — Redesigned for unified circular look
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Share Button (Left - Circular)
                    GlassCircleButton(
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, playlist.shareLink)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        size = 48.dp,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.share),
                            contentDescription = stringResource(R.string.share),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play Button (Center - Large Circle)
                    // playlist.id is a YouTube playlist id, never equal to a
                    // song's albumId — that comparison was always false, so this
                    // never showed "pause" and always restarted the queue on tap.
                    // Membership of the current song in this playlist's own list
                    // is the same "is this the active context" heuristic
                    // AlbumScreen uses (there, albumId happens to match directly).
                    val isPlayingThisPlaylist = isPlaying && songs.any { it.id == mediaMetadata?.id }
                    GlassCircleButton(
                        onClick = {
                            if (songs.isNotEmpty()) {
                                if (isPlayingThisPlaylist) {
                                    playerConnection.player.pause()
                                } else if (songs.any { it.id == mediaMetadata?.id }) {
                                    playerConnection.player.play()
                                } else {
                                    playerConnection.playQueue(
                                        YouTubePlaylistQueue(
                                            playlistId = playlist.id,
                                            playlistTitle = playlist.title,
                                            initialSongs = songs,
                                            initialContinuation = continuation
                                        )
                                    )
                                }
                            }
                        },
                        size = 72.dp,
                    ) {
                        AnimatedPlayPauseIcon(
                            isPlaying = isPlayingThisPlaylist,
                            size = 32.dp,
                        )
                    }

                    // Save Button (Right - Circular)
                    val isBookmarked = dbPlaylist?.playlist?.bookmarkedAt != null
                    GlassCircleButton(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                if (dbPlaylist != null) {
                                    database.withTransaction {
                                        val currentPlaylist = dbPlaylist.playlist
                                        update(currentPlaylist, playlist)
                                        update(currentPlaylist.toggleLike())
                                    }
                                } else {
                                    database.withTransaction {
                                        val playlistEntity = PlaylistEntity(
                                            name = playlist.title,
                                            browseId = playlist.id,
                                            thumbnailUrl = playlist.thumbnail,
                                            isEditable = playlist.isEditable,
                                            remoteSongCount = playlist.songCountText?.let {
                                                Regex("""\d+""").find(it)?.value?.toIntOrNull()
                                            },
                                            playEndpointParams = playlist.playEndpoint?.params,
                                            shuffleEndpointParams = playlist.shuffleEndpoint?.params,
                                            radioEndpointParams = playlist.radioEndpoint?.params
                                        ).toggleLike()
                                        insert(playlistEntity)
                                        songs.map { it.toMediaMetadata() }
                                            .onEach { insert(it) }
                                            .mapIndexed { index, song ->
                                                PlaylistSongMap(
                                                    songId = song.id,
                                                    playlistId = playlistEntity.id,
                                                    position = index,
                                                    setVideoId = song.setVideoId
                                                )
                                            }
                                            .forEach { insert(it) }
                                    }
                                }
                            }
                        },
                        size = 48.dp,
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isBookmarked) R.drawable.favorite else R.drawable.favorite_border
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isBookmarked) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }

            Spacer(Modifier.height(24.dp))

            // Explicit Label
            if (hasExplicitContent) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(chromeShape)
                        .then(chromeBackgroundModifier)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.explicit),
                        contentDescription = "Explicit",
                        tint = chromeContentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.explicit),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = chromeContentColor
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // Metadata Badge
            val totalDuration = songs.sumOf { it.duration ?: 0 }
            Text(
                text = buildString {
                    append(pluralStringResource(R.plurals.n_song, songs.size, songs.size))
                    if (totalDuration > 0) {
                        append(" • ")
                        val hours = totalDuration / 3600
                        val minutes = (totalDuration % 3600) / 60
                        if (hours > 0) {
                            append("${hours}h ${minutes}m")
                        } else {
                            append("${minutes}m")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = onTint.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clip(chromeShape)
                    .then(chromeBackgroundModifier)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )

            // Author Name
            playlist.author?.name?.let { authorName ->
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = onTint,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Toggle Buttons Row (Download, Shuffle, More)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Download Toggle
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(chromeShape)
                        .then(chromeBackgroundModifier)
                        .bounceClick(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = {
                                when (downloadState) {
                                    Download.STATE_COMPLETED ->
                                        removeDownloads(context, songs.map { it.id })

                                    // Cancel, not remove: this used to delete every song
                                    // in the playlist, already-finished ones included.
                                    Download.STATE_DOWNLOADING -> cancelDownloads(
                                        context,
                                        songs.map { it.id },
                                        downloads,
                                    )

                                    else -> downloadSongs(
                                        context,
                                        songs.map { DownloadTarget(it.id, it.title) },
                                        downloads,
                                    )
                                }
                            }
                        )
                        .semantics { role = Role.Button }
                ) {
                    when (downloadState) {
                        Download.STATE_COMPLETED -> {
                            Icon(
                                painter = painterResource(R.drawable.offline),
                                contentDescription = stringResource(R.string.saved),
                                modifier = Modifier.size(20.dp),
                                tint = chromeContentColor
                            )
                        }
                        Download.STATE_DOWNLOADING -> {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp),
                                color = chromeContentColor
                            )
                        }
                        else -> {
                            Icon(
                                painter = painterResource(R.drawable.download),
                                contentDescription = stringResource(R.string.action_download),
                                modifier = Modifier.size(20.dp),
                                tint = chromeContentColor
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when (downloadState) {
                            Download.STATE_COMPLETED -> stringResource(R.string.saved)
                            Download.STATE_DOWNLOADING -> stringResource(R.string.saving)
                            else -> stringResource(R.string.save_album)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = chromeContentColor
                    )
                }

                // Shuffle Button
                GlassCircleButton(
                    onClick = {
                        if (songs.isNotEmpty()) {
                            playerConnection.playQueue(
                                YouTubePlaylistQueue(
                                    playlistId = playlist.id,
                                    playlistTitle = playlist.title,
                                    initialSongs = songs.shuffled(),
                                    initialContinuation = continuation
                                )
                            )
                        }
                    },
                    size = 48.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = stringResource(R.string.shuffle_content_desc),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // More Options Button
                GlassCircleButton(
                    onClick = {
                        menuState.show {
                            YouTubePlaylistMenu(
                                playlist = playlist,
                                songs = songs,
                                coroutineScope = coroutineScope,
                                onDismiss = menuState::dismiss,
                            )
                        }
                    },
                    size = 48.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = stringResource(R.string.more_options),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}