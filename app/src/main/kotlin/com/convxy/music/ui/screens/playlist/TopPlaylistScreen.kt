/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.playlist

import com.convxy.music.ui.utils.FloatingChromeSpacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.utils.rememberPreference
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.MyTopFilter
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.DefaultDialog
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortHeader
import com.convxy.music.ui.menu.SelectionSongMenu
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.menu.TopPlaylistMenu
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.makeTimeString
import com.convxy.music.viewmodels.TopPlaylistViewModel
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.rememberHeroTopBlur
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalContentColor
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.ChromeScrim
import com.convxy.music.ui.component.rememberChromeScrimProgress
import androidx.compose.ui.draw.clip
import java.time.LocalDateTime
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.cancelDownloads
import com.convxy.music.playback.downloadSongs
import com.convxy.music.playback.removeDownloads

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopPlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: TopPlaylistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val maxSize = viewModel.top

    val songs by viewModel.topSongs.collectAsState(null)
    val mutableSongs = remember { mutableStateListOf<Song>() }

    val likeLength = remember(songs) {
        songs?.fastSumBy { it.song.duration } ?: 0
    }

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
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

    val filteredSongs = remember(songs, query) {
        if (query.text.isEmpty()) songs ?: emptyList()
        else songs?.filter { song ->
            song.title.contains(query.text, true) ||
                song.artists.any { it.name.contains(query.text, true) }
        } ?: emptyList()
    }

    LaunchedEffect(filteredSongs) {
        selection.fastForEachReversed { songId ->
            if (filteredSongs.find { it.id == songId } == null) {
                selection.remove(songId)
            }
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

    val sortType by viewModel.topPeriod.collectAsState()
    val name = stringResource(R.string.my_top) + " $maxSize"

    val downloadUtil = LocalDownloadUtil.current
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
            current.all { downloads[it.song.id]?.state == Download.STATE_COMPLETED } ->
                Download.STATE_COMPLETED

            current.all {
                downloads[it.song.id]?.state == Download.STATE_QUEUED ||
                    downloads[it.song.id]?.state == Download.STATE_DOWNLOADING ||
                    downloads[it.song.id]?.state == Download.STATE_COMPLETED
            } -> Download.STATE_DOWNLOADING

            else -> Download.STATE_STOPPED
        }
    }

    LaunchedEffect(songs) {
        mutableSongs.apply {
            clear()
            songs?.let { addAll(it) }
        }
    }

    var showRemoveDownloadDialog by remember { mutableStateOf(false) }

    if (showRemoveDownloadDialog) {
        DefaultDialog(
            onDismiss = { showRemoveDownloadDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.remove_download_playlist_confirm, name),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
            },
            buttons = {
                TextButton(
                    onClick = { showRemoveDownloadDialog = false },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                TextButton(
                    onClick = {
                        showRemoveDownloadDialog = false
                        removeDownloads(context, songs?.map { it.song.id }.orEmpty())
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    val state = rememberLazyListState()

    val heroUrl = songs?.firstOrNull()?.song?.thumbnailUrl
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = songs?.map { it.song.thumbnailUrl to false } ?: emptyList()
    )
    val tint = rememberHeroTint(heroUrl)
    val onTint = AppleTokens.onColor(tint)

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val heroBackdrop = rememberLayerBackdrop()

    // Attached capture of the list, sampled by the floating chrome below. The
    // chrome is a *sibling* of the capture, never inside it, so there is no
    // RenderNode self-reference. heroBackdrop alone is unattached — its
    // drawBackdrop early-returns, which is why this chrome rendered flat with
    // no real blur behind it. The tint is filled into the capture BEFORE the
    // content (same as MainActivity's appBackdrop) so the recording is opaque;
    // without it the blurred result is part-transparent and the sharp content
    // shows straight through it as a doubled image.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()
    val heroZoom = rememberHeroZoom()

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        blurArtwork = true,
        fullBlur = true,
        bottomGradient = true,
        // Scroll-linked sharp-top/blurred-bottom split, replaced by fullBlur above.
        // topBlurProgress = rememberHeroTopBlur(state),
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Capture from a plain Box wrapping the LazyColumn, not the
            // LazyColumn's own modifier: LazyColumn promotes its items to their
            // own RenderNodes for scroll recycling, which a capture attached
            // directly to it doesn't reliably flatten.
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
                state = state,
                // No bounce here: the top pull drives the hero zoom instead.
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom),
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
            ) {
                if (songs != null) {
                    item(key = "header_title") {
                        Column {
                            LargeScreenTitle(
                                title = name,
                                color = onTint,
                            )
                        }
                    }

                    if (songs!!.isEmpty()) {
                        item(key = "empty_placeholder") {
                            EmptyPlaceholder(
                                icon = R.drawable.music_note,
                                text = stringResource(R.string.playlist_is_empty),
                                modifier = Modifier.padding(top = 100.dp)
                            )
                        }
                    } else {
                        if (isSearching) {
                            // No hero header in search mode — reserve the floating
                            // chrome's height so the first row doesn't start under the
                            // status bar.
                            item(key = "search_chrome_spacer") { FloatingChromeSpacer() }
                        }
                        if (!isSearching) {
                            item(key = "playlist_header") {
                                TopPlaylistHeader(
                                    name = name,
                                    songs = songs!!,
                                    likeLength = likeLength,
                                    downloadState = downloadState,
                                    onShowRemoveDownloadDialog = { showRemoveDownloadDialog = true },
                                    menuState = menuState,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        item(key = "songs_header") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                            ) {
                                SortHeader(
                                    sortType = sortType,
                                    sortDescending = false,
                                    onSortTypeChange = {
                                        viewModel.topPeriod.value = it
                                    },
                                    onSortDescendingChange = {},
                                    sortTypeText = { sortType ->
                                        when (sortType) {
                                            MyTopFilter.ALL_TIME -> R.string.all_time
                                            MyTopFilter.DAY -> R.string.past_24_hours
                                            MyTopFilter.WEEK -> R.string.past_week
                                            MyTopFilter.MONTH -> R.string.past_month
                                            MyTopFilter.YEAR -> R.string.past_year
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    showDescending = false,
                                )
                            }
                        }
                    }
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
//                            albumIndex = index + 1,
                            isActive = song.song.id == mediaMetadata?.id,
                            isPlaying = isPlaying,
                            showInLibraryIcon = true,
                            flat = true,
                            shape = listItemShape(index, filteredSongs.size),
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
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedBounceClick(
                                    onClick = {
                                        if (inSelectMode) {
                                            onCheckedChange(song.id !in selection)
                                        } else if (song.song.id == mediaMetadata?.id) {
                                            playerConnection.togglePlayPause()
                                        } else {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = name,
                                                    items = songs!!.map { it.toMediaItem() },
                                                    startIndex = songs!!.indexOfFirst { it.id == song.id }
                                                ),
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
                                )
                                .animateItem()
                        )
                    }
                }
                item(key = "bottom_spacer") {
                    Spacer(Modifier.height(100.dp))
                }
            }
            }


            // Ranked by play count, never alphabetically, so the rail is a
            // proportional thumb rather than letters.
            ListScrollRail(
                lazyListState = state,
                itemCount = filteredSongs.size,
                sectionIndexMap = null,
            )

            // Top bar logic
            val chromeScrimProgress = rememberChromeScrimProgress(state)
            // Sample the list's own attached capture (a sibling, so no
            // RenderNode cycle) rather than the ambient unattached heroBackdrop,
            // whose drawBackdrop early-returns and left this chrome flat.
            CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
            // Shadows the outer chromeBackgroundModifier deliberately: that one
            // was built before this provider, so liquidGlass captured the
            // unattached heroBackdrop at composition time and rendered flat.
            val chromeBackgroundModifier = if (useGlass) {
                Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
            } else {
                Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
            }
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
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = pluralStringResource(R.plurals.n_song, selection.size, selection.size),
                        style = MaterialTheme.typography.titleMedium,
                        color = onTint,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(chromeShape)
                            .background(onTint.copy(alpha = 0.15f), chromeShape)
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
                                    selection.addAll(filteredSongs.map { it.id })
                                }
                            }
                        )
                        IconButton(
                            enabled = selection.isNotEmpty(),
                            onClick = {
                                menuState.show {
                                    SelectionSongMenu(
                                        songSelection = filteredSongs.filter { it.id in selection },
                                        onDismiss = menuState::dismiss,
                                        clearAction = onExitSelectionMode,
                                    )
                                }
                            },
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
                            focusManager.clearFocus()
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
                                    color = onTint.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium.copy(color = onTint),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = onTint,
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

                    Spacer(Modifier.weight(1f))

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
        }
      }
    }
}

@Composable
private fun TopPlaylistHeader(
    name: String,
    songs: List<Song>,
    likeLength: Int,
    downloadState: Int,
    onShowRemoveDownloadDialog: () -> Unit,
    menuState: com.convxy.music.ui.component.MenuState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val context = LocalContext.current
    val downloads by LocalDownloadUtil.current.downloads.collectAsState()
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val heroUrl = songs.firstOrNull()?.thumbnailUrl
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = songs.map { it.thumbnailUrl to false },
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
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(50.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = onTint,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildString {
                    append(pluralStringResource(R.plurals.n_song, songs.size, songs.size))
                    if (likeLength > 0) {
                        append(" • ")
                        append(makeTimeString(likeLength * 1000L))
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = onTint.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isPlaying) {
                            playerConnection.player.pause()
                        } else {
                            playerConnection.playQueue(
                                ListQueue(
                                    title = name,
                                    items = songs.map { it.toMediaItem() },
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalContentColor.current,
                        contentColor = tint,
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnimatedPlayPauseIcon(
                            isPlaying = isPlaying,
                            modifier = Modifier.size(20.dp),
                            tint = tint,
                            size = 20.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.play),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = tint,
                        )
                    }
                }

                Button(
                    onClick = {
                        playerConnection.playQueue(
                            ListQueue(
                                title = name,
                                items = songs.shuffled().map { it.toMediaItem() },
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalContentColor.current.copy(alpha = 0.15f),
                        contentColor = LocalContentColor.current,
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = LocalContentColor.current,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.shuffle),
                            style = MaterialTheme.typography.labelLarge,
                            color = LocalContentColor.current,
                        )
                    }
                }

                GlassCircleButton(
                    onClick = {
                        menuState.show {
                            TopPlaylistMenu(
                                downloadState = downloadState,
                                onQueue = {
                                    playerConnection.addToQueue(songs.map { it.toMediaItem() })
                                },
                                onDownload = {
                                    when (downloadState) {
                                        Download.STATE_COMPLETED -> onShowRemoveDownloadDialog()
                                        // Cancel, not remove: this used to delete every
                                        // song in the list, finished ones included.
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
                                },
                                onDismiss = { menuState.dismiss() }
                            )
                        }
                    },
                    size = 48.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.about_album),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onTint,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides onTint,
                ) {
                    ExpandableText(
                        text = "$name is a playlist featuring your ${songs.size} most played tracks. Keep listening to discover how your top songs evolve over time.",
                        collapsedMaxLines = 3
                    )
                }
            }
        }
    }
}
