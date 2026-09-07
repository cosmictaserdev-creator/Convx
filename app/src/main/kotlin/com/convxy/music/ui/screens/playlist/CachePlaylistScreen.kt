/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.playlist

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.constants.SongSortDescendingKey
import com.convxy.music.constants.SongSortType
import com.convxy.music.constants.SongSortTypeKey
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortHeader
import com.convxy.music.ui.menu.CachePlaylistMenu
import com.convxy.music.ui.menu.SelectionSongMenu
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.makeTimeString
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.CachePlaylistViewModel
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
import androidx.compose.material3.LocalContentColor
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.ChromeScrim
import com.convxy.music.ui.component.rememberChromeScrimProgress
import java.time.LocalDateTime
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.downloadSongs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CachePlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: CachePlaylistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val cachedSongs by viewModel.cachedSongs.collectAsState()

    val (sortType, onSortTypeChange) = rememberEnumPreference(
        SongSortTypeKey,
        SongSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(SongSortDescendingKey, true)
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)

    val sortedSongs = remember(cachedSongs, sortType, sortDescending) {
        val sorted = when (sortType) {
            SongSortType.CREATE_DATE -> cachedSongs.sortedBy { it.song.dateDownload ?: LocalDateTime.MIN }
            SongSortType.NAME -> cachedSongs.sortedBy { it.song.title }
            SongSortType.ARTIST -> cachedSongs.sortedBy { song ->
                song.artists.joinToString(separator = "") { it.name }
            }
            SongSortType.PLAY_TIME -> cachedSongs.sortedBy { it.song.totalPlayTime }
        }
        if (sortDescending) sorted.reversed() else sorted
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

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
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

    val filteredSongs = remember(sortedSongs, query) {
        if (query.text.isEmpty()) sortedSongs
        else sortedSongs.filter { song ->
            song.title.contains(query.text, true) ||
                song.artists.any { it.name.contains(query.text, true) }
        }
    }

    LaunchedEffect(filteredSongs) {
        selection.fastForEachReversed { songId ->
            if (filteredSongs.find { it.id == songId } == null) {
                selection.remove(songId)
            }
        }
    }

    val heroUrl = filteredSongs.firstOrNull()?.thumbnailUrl
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = filteredSongs.map { it.thumbnailUrl to false },
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
        // topBlurProgress = rememberHeroTopBlur(lazyListState),
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        
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
                state = lazyListState,
                // No bounce here: the top pull drives the hero zoom instead.
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom),
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
            ) {
                item(key = "header_title") {
                    Column {
                        LargeScreenTitle(
                            title = stringResource(R.string.cached_playlist),
                            color = onTint,
                        )
                    }
                }
                
                if (filteredSongs.isEmpty() && !isSearching) {
                    item(key = "empty_placeholder") {
                        EmptyPlaceholder(
                            icon = R.drawable.music_note,
                            text = stringResource(R.string.playlist_is_empty),
                            modifier = Modifier.padding(top = 100.dp)
                        )
                    }
                }

                if (filteredSongs.isEmpty() && isSearching) {
                    item(key = "no_results") {
                        EmptyPlaceholder(
                            icon = R.drawable.search,
                            text = stringResource(R.string.no_results_found),
                            modifier = Modifier.padding(top = 100.dp)
                        )
                    }
                } else {
                    if (filteredSongs.isNotEmpty() && !isSearching) {
                        item(key = "playlist_header") {
                            CachePlaylistHeader(
                                songs = filteredSongs,
                                context = context,
                                menuState = menuState,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

                    if (filteredSongs.isNotEmpty()) {
                        item(key = "sort_header") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .animateItem(),
                            ) {
                                SortHeader(
                                    sortType = sortType,
                                    sortDescending = sortDescending,
                                    onSortTypeChange = onSortTypeChange,
                                    onSortDescendingChange = onSortDescendingChange,
                                    sortTypeText = { sortType ->
                                        when (sortType) {
                                            SongSortType.CREATE_DATE -> R.string.sort_by_create_date
                                            SongSortType.NAME -> R.string.sort_by_name
                                            SongSortType.ARTIST -> R.string.sort_by_artist
                                            SongSortType.PLAY_TIME -> R.string.sort_by_play_time
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        filteredSongs,
                        key = { _, song -> song.id },
                        contentType = { _, _ -> "song_row" }
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
                            isActive = song.id == mediaMetadata?.id,
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
                                    IconButton(onClick = {
                                        menuState.show {
                                            SongMenu(
                                                originalSong = song,
                                                navController = navController,
                                                onDismiss = menuState::dismiss,
                                                isFromCache = true,
                                            )
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .combinedBounceClick(
                                    onClick = {
                                        if (inSelectMode) {
                                            onCheckedChange(song.id !in selection)
                                        } else if (song.id == mediaMetadata?.id) {
                                            playerConnection.togglePlayPause()
                                        } else {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = "Cache Songs",
                                                    items = cachedSongs.map { it.toMediaItem() },
                                                    startIndex = cachedSongs.indexOfFirst { it.id == song.id }
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
                                .animateItem()
                        )
                    }

                    item(key = "bottom_spacer") {
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
            }

            ListScrollRail(
                lazyListState = lazyListState,
                itemCount = filteredSongs.size,
                sectionIndexMap = when (sortType) {
                    SongSortType.NAME ->
                        remember(filteredSongs) {
                            buildAlphabetSectionIndex(filteredSongs) { it.title }
                        }

                    SongSortType.ARTIST ->
                        remember(filteredSongs) {
                            buildAlphabetSectionIndex(filteredSongs) { song ->
                                song.artists.firstOrNull()?.name.orEmpty()
                            }
                        }

                    else -> null
                },
            )

            // Top bar logic
            val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
            // Sample the list's own attached capture (a sibling, so no
            // RenderNode cycle) rather than the ambient unattached heroBackdrop,
            // whose drawBackdrop early-returns and left this chrome flat.
            CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
            // Built inside the provider so liquidGlass captures listBackdrop at
            // composition time, not the ambient unattached backdrop.
            val chromeBackgroundModifier = if (useGlass) {
                Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
            } else {
                Modifier.background(onTint.copy(alpha = 0.15f), chromeShape)
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
                        text = pluralStringResource(R.plurals.n_selected, selection.size, selection.size),
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
private fun CachePlaylistHeader(
    songs: List<Song>,
    context: android.content.Context,
    menuState: com.convxy.music.ui.component.MenuState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val downloads by com.convxy.music.LocalDownloadUtil.current.downloads.collectAsState()
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val cacheLength = remember(songs) { songs.fastSumBy { it.song.duration ?: 0 } }

    val heroUrl = songs.firstOrNull()?.thumbnailUrl
    val heroSource = com.convxy.music.ui.component.rememberHeroSource(
        staticArt = heroUrl,
        songs = songs.map { it.thumbnailUrl to false },
    )
    val tint = com.convxy.music.ui.component.rememberHeroTint(heroUrl)
    val onTint = com.convxy.music.ui.theme.AppleTokens.onColor(tint)

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
            Spacer(Modifier.height(24.dp))

        // Title with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.cached),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = onTint
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.cached_playlist),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = onTint
            )
        }

        Spacer(Modifier.height(8.dp))

        // Metadata - Song Count • Duration
        Text(
            text = buildString {
                append(pluralStringResource(R.plurals.n_song, songs.size, songs.size))
                if (cacheLength > 0) {
                    append(" • ")
                    append(makeTimeString(cacheLength * 1000L))
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = onTint.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(24.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play Button
            val isThisPlaylistPlaying = isPlaying && mediaMetadata?.let { media ->
                songs.any { it.id == media.id }
            } ?: false

            androidx.compose.material3.Button(
                onClick = {
                    if (isThisPlaylistPlaying) {
                        playerConnection.player.pause()
                    } else {
                        playerConnection.playQueue(
                            ListQueue(
                                title = context.getString(R.string.cached_playlist),
                                items = songs.map { it.toMediaItem() },
                            )
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = LocalContentColor.current,
                    contentColor = tint
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isThisPlaylistPlaying,
                        modifier = Modifier.size(20.dp),
                        tint = tint,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.play),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = tint
                    )
                }
            }

            // Shuffle Button
            androidx.compose.material3.Button(
                onClick = {
                    playerConnection.playQueue(
                        ListQueue(
                            title = context.getString(R.string.cached_playlist),
                            items = songs.shuffled().map { it.toMediaItem() },
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = LocalContentColor.current.copy(alpha = 0.15f),
                    contentColor = LocalContentColor.current
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = LocalContentColor.current
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.shuffle),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = LocalContentColor.current
                    )
                }
            }

            // More Options
            Surface(
                onClick = {
                    menuState.show {
                        CachePlaylistMenu(
                            downloadState = Download.STATE_STOPPED,
                            onQueue = {
                                playerConnection.addToQueue(
                                    songs.map { it.toMediaItem() }
                                )
                            },
                            onDownload = {
                                downloadSongs(
                                    context,
                                    songs.map { DownloadTarget(it.id, it.title) },
                                    downloads,
                                )
                            },
                            onDismiss = { menuState.dismiss() }
                        )
                    }
                },
                shape = androidx.compose.foundation.shape.CircleShape,
                color = LocalContentColor.current.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = LocalContentColor.current
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        val staticDescription = remember(songs.size, cacheLength) {
            val name = context.getString(R.string.cached_playlist)
            val trackCountText = context.resources.getQuantityString(R.plurals.n_song, songs.size, songs.size)
            "$name is your local collection of cached tracks, featuring $trackCountText.${
                if (cacheLength > 0) " Combined duration is ${makeTimeString(cacheLength * 1000L)}." else ""
            } These songs are stored on your device for quick access."
        }

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
                    text = staticDescription,
                    runs = null,
                    collapsedMaxLines = 3
                )
            }
        }
        }
    }
}
