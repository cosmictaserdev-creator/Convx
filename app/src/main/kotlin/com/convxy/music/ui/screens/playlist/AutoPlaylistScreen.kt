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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.SongSortDescendingKey
import com.convxy.music.constants.SongSortType
import com.convxy.music.constants.SongSortTypeKey
import com.convxy.music.constants.YtmSyncKey
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.ChromeScrim
import com.convxy.music.ui.component.rememberChromeScrimProgress
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.MenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortHeader
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.menu.AutoPlaylistMenu
import com.convxy.music.ui.menu.SelectionSongMenu
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.makeTimeString
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.AutoPlaylistViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AutoPlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: AutoPlaylistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    // Pre-sorted + filtered by the sort/hide prefs inside the ViewModel.
    val songs by viewModel.likedSongs.collectAsState()

    val (sortType, onSortTypeChange) = rememberEnumPreference(
        SongSortTypeKey,
        SongSortType.CREATE_DATE,
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(SongSortDescendingKey, true)

    // Pull liked/uploaded songs from YouTube Music on open (regression: the sync-on-open
    // effect was dropped in the card-screen refactor, leaving the Liked screen empty).
    val (ytmSync) = rememberPreference(YtmSyncKey, true)
    LaunchedEffect(Unit) {
        if (ytmSync) {
            when (viewModel.playlist) {
                "liked" -> viewModel.syncLikedSongs()
                "uploaded" -> viewModel.syncUploadedSongs()
            }
        }
    }

    // Local music: storage permission + device scan, so the Apple-styled local view
    // can populate/refresh the library itself (no separate plain screen needed).
    val isLocal = viewModel.playlist == "local"
    val isScanning by viewModel.isScanning.collectAsState()
    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasAudioPermission by remember { mutableStateOf(false) }
    val scanPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
    }
    LaunchedEffect(isLocal) {
        if (isLocal) {
            hasAudioPermission = context.checkSelfPermission(audioPermission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    val (titleRes, iconRes) = when (viewModel.playlist) {
        "liked" -> R.string.liked to R.drawable.favorite
        "downloaded" -> R.string.downloaded to R.drawable.download
        "uploaded" -> R.string.uploaded_playlist to R.drawable.cloud
        "local" -> R.string.local_songs to R.drawable.music_note
        else -> R.string.offline to R.drawable.cached
    }
    val playlistName = stringResource(titleRes)

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() },
        ),
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

    val filteredSongs = remember(songs, query) {
        if (query.text.isEmpty()) {
            songs
        } else {
            songs.filter { song ->
                song.title.contains(query.text, true) ||
                    song.artists.any { it.name.contains(query.text, true) }
            }
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

    val heroBackdrop = rememberLayerBackdrop()

    // Attached capture of the list, sampled by the floating chrome below. The
    // chrome is a *sibling* of the capture, never inside it, so there is no
    // RenderNode self-reference. heroBackdrop alone is unattached — its
    // drawBackdrop early-returns, which is why this chrome rendered flat with
    // no real blur. The tint is filled into the capture BEFORE the content
    // (same as MainActivity's appBackdrop) so the recording is opaque; without
    // it the blurred result is part-transparent and the sharp content shows
    // straight through it as a doubled image.
    val listBackdrop = rememberLayerBackdrop(
        onDraw = remember(tint) {
            val bg = tint
            { drawRect(bg); drawContent() }
        }
    )
    val backdropFreeze = rememberBackdropFreeze()

    // Scroll-linked sharp-top/blurred-bottom split, replaced by a constant full
    // blur (fullBlur = true below) — kept commented instead of deleted.
    // val heroTopBlur by remember {
    //     derivedStateOf {
    //         if (lazyListState.firstVisibleItemIndex > 0) 1f
    //         else (lazyListState.firstVisibleItemScrollOffset / 700f).coerceIn(0f, 1f)
    //     }
    // }

    val heroZoom = rememberHeroZoom()

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        blurArtwork = true,
        fullBlur = true,
        bottomGradient = true,
        // topBlurProgress = heroTopBlur,
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
        HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
            val chromeShape = ContinuousRoundedRectangle(percent = 50)

            Box(modifier = Modifier.fillMaxSize()) {
                // Capture from a plain Box wrapping the LazyColumn, not the
                // LazyColumn's own modifier: LazyColumn promotes its items to
                // their own RenderNodes for scroll recycling, which a capture
                // attached directly to it doesn't reliably flatten.
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
                        Spacer(Modifier.height(40.dp))
                    }

                    if (filteredSongs.isEmpty() && !isSearching) {
                        item(key = "empty_placeholder") {
                            EmptyPlaceholder(
                                icon = iconRes,
                                text = stringResource(R.string.playlist_is_empty),
                                modifier = Modifier.padding(top = 100.dp),
                            )
                        }
                        if (isLocal) {
                            item(key = "local_scan_action") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator()
                                    } else {
                                        Button(
                                            onClick = {
                                                if (hasAudioPermission) viewModel.scanLocal(context)
                                                else scanPermissionLauncher.launch(audioPermission)
                                            },
                                        ) {
                                            Text(
                                                stringResource(
                                                    if (hasAudioPermission) R.string.scan_device
                                                    else R.string.grant_permission
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (filteredSongs.isEmpty() && isSearching) {
                        item(key = "no_results") {
                            EmptyPlaceholder(
                                icon = R.drawable.search,
                                text = stringResource(R.string.no_results_found),
                                modifier = Modifier.padding(top = 100.dp),
                            )
                        }
                    } else {
                        if (filteredSongs.isNotEmpty() && !isSearching) {
                            item(key = "playlist_header") {
                                AutoPlaylistHeader(
                                    songs = filteredSongs,
                                    titleRes = titleRes,
                                    iconRes = iconRes,
                                    context = context,
                                    menuState = menuState,
                                    modifier = Modifier,
                                )
                            }
                        }

                        if (filteredSongs.isNotEmpty()) {
                            item(key = "sort_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(start = 16.dp),
                                ) {
                                    SortHeader(
                                        sortType = sortType,
                                        sortDescending = sortDescending,
                                        onSortTypeChange = onSortTypeChange,
                                        onSortDescendingChange = onSortDescendingChange,
                                        sortTypeText = { type ->
                                            when (type) {
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

                        itemsIndexed(filteredSongs, key = { _, song -> song.id }) { index, song ->
                            val onCheckedChange: (Boolean) -> Unit = {
                                if (it) selection.add(song.id) else selection.remove(song.id)
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
                                            onCheckedChange = onCheckedChange,
                                        )
                                    } else {
                                        IconButton(onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        }) {
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
                                            } else if (song.id == mediaMetadata?.id) {
                                                playerConnection.togglePlayPause()
                                            } else {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = playlistName,
                                                        items = songs.map { it.toMediaItem() },
                                                        startIndex = songs.indexOfFirst { it.id == song.id },
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
                                    ),
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

                // Top bar
                val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
                // Sample the list's own attached capture (a sibling, so no
                // RenderNode cycle) rather than the ambient unattached
                // heroBackdrop, whose drawBackdrop early-returns and left this
                // chrome flat with no real blur behind it.
                CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
                // Built inside the provider so liquidGlass captures listBackdrop
                // at composition time, not the ambient unattached backdrop.
                val glassConfig = LocalGlassEffectConfig.current
                val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
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
                            modifier = Modifier.weight(1f),
                        )

                        Row(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(chromeShape)
                                .background(onTint.copy(alpha = 0.15f), chromeShape)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                                },
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
                                    contentDescription = null,
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
                                contentDescription = null,
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
                                        color = onTint.copy(alpha = 0.6f),
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
                                    .focusRequester(focusRequester),
                            )
                        }
                    } else {
                        GlassCircleButton(
                            onClick = { navController.navigateUp() },
                            onLongClick = { navController.backToMain() },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = null,
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        if (isLocal) {
                            GlassCircleButton(
                                onClick = { if (!isScanning) viewModel.scanLocal(context) },
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = onTint,
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.refresh),
                                        contentDescription = null,
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))
                        }

                        GlassCircleButton(onClick = { isSearching = true }) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = null,
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
private fun AutoPlaylistHeader(
    songs: List<Song>,
    titleRes: Int,
    iconRes: Int,
    context: android.content.Context,
    menuState: MenuState,
    modifier: Modifier = Modifier,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val totalLength = remember(songs) { songs.fastSumBy { it.song.duration } }

    val heroUrl = songs.firstOrNull()?.thumbnailUrl
    val tint = rememberHeroTint(heroUrl)
    val onTint = AppleTokens.onColor(tint)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = onTint,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = onTint,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = buildString {
                append(pluralStringResource(R.plurals.n_song, songs.size, songs.size))
                if (totalLength > 0) {
                    append(" • ")
                    append(makeTimeString(totalLength * 1000L))
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = onTint.copy(alpha = 0.7f),
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isThisPlaylistPlaying = isPlaying && mediaMetadata?.let { media ->
                songs.any { it.id == media.id }
            } ?: false

            Button(
                onClick = {
                    if (isThisPlaylistPlaying) {
                        playerConnection.player.pause()
                    } else {
                        playerConnection.playQueue(
                            ListQueue(
                                title = context.getString(titleRes),
                                items = songs.map { it.toMediaItem() },
                            ),
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
                contentPadding = PaddingValues(vertical = 12.dp),
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
                        color = tint,
                    )
                }
            }

            Button(
                onClick = {
                    playerConnection.playQueue(
                        ListQueue(
                            title = context.getString(titleRes),
                            items = songs.shuffled().map { it.toMediaItem() },
                        ),
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
                contentPadding = PaddingValues(vertical = 12.dp),
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
                        fontWeight = FontWeight.Bold,
                        color = LocalContentColor.current,
                    )
                }
            }

            Surface(
                onClick = {
                    menuState.show {
                        AutoPlaylistMenu(
                            downloadState = androidx.media3.exoplayer.offline.Download.STATE_STOPPED,
                            onQueue = {
                                playerConnection.addToQueue(songs.map { it.toMediaItem() })
                            },
                            onDownload = { },
                            onDismiss = { menuState.dismiss() },
                        )
                    }
                },
                shape = CircleShape,
                color = LocalContentColor.current.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = LocalContentColor.current,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        val staticDescription = remember(songs.size, totalLength) {
            val name = context.getString(titleRes)
            val trackCountText = context.resources.getQuantityString(R.plurals.n_song, songs.size, songs.size)
            "$name features $trackCountText.${
                if (totalLength > 0) " Combined duration is ${makeTimeString(totalLength * 1000L)}." else ""
            }"
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.about_album),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onTint,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            CompositionLocalProvider(LocalContentColor provides onTint) {
                ExpandableText(
                    text = staticDescription,
                    runs = null,
                    collapsedMaxLines = 3,
                )
            }
        }
    }
}
