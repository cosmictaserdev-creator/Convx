/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.playlist

import com.convx.music.ui.utils.sharedTitleTarget

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.chromeShadow
import com.convx.music.ui.utils.FloatingChromeSpacer
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.KeyboardOptions
import com.convx.music.ui.ios.compat.Checkbox
import com.convx.music.ui.ios.compat.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.FilledIconToggleButton
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.PlainTooltip
import com.convx.music.ui.ios.compat.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.OutlinedTextField as TextField
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFLine3Horizontal
import com.composables.sfsymbols.monochrome.SFLock
import com.composables.sfsymbols.monochrome.SFLockOpen
import com.composables.sfsymbols.monochrome.SFSquareAndPencil
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFXmark
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import com.convx.music.ui.component.buildAlphabetSectionIndex
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import org.koin.androidx.compose.koinViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.PlaylistEditLockKey
import com.convx.music.constants.SwipeToRemoveSongKey
import com.convx.music.library.LocalPlaylistSong
import com.convx.music.library.PlaylistEntryReference
import com.convx.music.library.PlaylistDetail
import com.convx.music.library.PlaylistTrackOrder
import com.convx.music.extensions.move
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.ActionPromptDialog
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.EmptyPlaceholder
import com.convx.music.ui.component.ContrastCircleButton
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.ChromeScrim
import com.convx.music.ui.component.rememberChromeScrimProgress
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.resolvedTextColor
import com.convx.music.ui.component.withFixedStyle
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.LocalItemPrefs
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.OverlayEditButton
import com.convx.music.ui.component.rememberPlaylistCoverPicker
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.ui.component.isGlassAllowed
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.ui.component.asSoftIconTint
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.menu.CustomThumbnailMenu
import com.convx.music.ui.component.ExpandableText
import com.convx.music.ui.menu.LocalPlaylistMenu
import com.convx.music.ui.menu.SelectionSongMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.component.AlbumStyleHeroImage
import com.convx.music.ui.player.MeshGradientBackground
import com.convx.music.ui.player.dimmed
import com.convx.music.ui.component.HeroTopBlur
import com.convx.music.ui.component.ZeroScrollProgress
import com.convx.music.ui.component.rememberHeroTopBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.LocalTabView
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.HeroCardHeader
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.listItemShape
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LocalPlaylistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.combinedDownloadState
import com.convx.music.playback.cancelDownloads
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.removeDownloads
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** A stable accent per playlist, so a cover-less one still gets its own colourful mesh. */
private fun localPlaylistSeed(id: String): Int {
    val accents = intArrayOf(0xFFFF375F.toInt(), 0xFF0A84FF.toInt(), 0xFFFF9F0A.toInt(), 0xFF30D158.toInt(), 0xFFBF5AF2.toInt(), 0xFF64D2FF.toInt())
    return accents[(id.hashCode() and Int.MAX_VALUE) % accents.size]
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocalPlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: LocalPlaylistViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val playlistDetail by viewModel.playlistDetail.collectAsStateWithLifecycle()
    val playlistDisplayPreferences by viewModel.playlistDisplayPreferences.collectAsStateWithLifecycle()
    val songs by viewModel.playlistSongs.collectAsStateWithLifecycle()
    val sortType = playlistDisplayPreferences.order
    val sortDescending = playlistDisplayPreferences.descending
    val mutableSongs = remember { mutableStateListOf<LocalPlaylistSong>() }
    val playlistLength =
        remember(songs) {
            songs.fastSumBy { it.song.song.duration }
        }
    var locked by rememberPreference(PlaylistEditLockKey, defaultValue = true)

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val exitSearch = {
        focusManager.clearFocus()
        isSearching = false
        query = TextFieldValue()
    }

    val filteredSongs =
        remember(songs, query) {
            if (query.text.isEmpty()) {
                songs
            } else {
                songs.filter { song ->
                    song.song.song.title
                        .contains(query.text, ignoreCase = true) ||
                            song.song.artists
                                .fastAny { it.name.contains(query.text, ignoreCase = true) }
                }
            }
        }

    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
        }
    }

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<Int>, Int>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }

    if (isSearching) {
        BackHandler(onBack = exitSearch)
    } else if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    val downloadUtil = LocalDownloadUtil.current.get()
    var downloadState by remember {
        mutableIntStateOf(Download.STATE_STOPPED)
    }

    val editable = playlistDetail?.isEditable == true

    LaunchedEffect(songs) {
        selection.fastForEachReversed { mapId ->
            if (songs.find { it.map.id == mapId } == null) {
                selection.remove(Integer.valueOf(mapId))
            }
        }
    }

    LaunchedEffect(songs) {
        mutableSongs.apply {
            clear()
            addAll(songs)
        }
        if (songs.isEmpty()) return@LaunchedEffect
        downloadUtil.downloads.collect { downloads ->
            downloadState = combinedDownloadState(songs, downloads) { it.song.id }
        }
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        playlistDetail?.let { detail ->
            var editName by remember {
                mutableStateOf(
                    TextFieldValue(detail.name, TextRange(detail.name.length))
                )
            }
            var editDescription by remember {
                mutableStateOf(TextFieldValue(detail.description ?: ""))
            }
            DefaultDialog(
                onDismiss = { showEditDialog = false },
                buttons = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val name = editName.text.trim().ifEmpty { detail.name }
                            val description = editDescription.text.trim().ifEmpty { null }
                            viewModel.updatePlaylist(name, description)
                            showEditDialog = false
                        },
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = stringResource(R.string.edit_playlist),
                        style = AppTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(stringResource(R.string.playlist_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text(stringResource(R.string.playlist_description)) },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    )
                }
            }
        }
    }

    var showRemoveDownloadDialog by remember {
        mutableStateOf(false)
    }

    if (showRemoveDownloadDialog) {
        DefaultDialog(
            onDismiss = { showRemoveDownloadDialog = false },
            content = {
                Text(
                    text = stringResource(
                        R.string.remove_download_playlist_confirm,
                        playlistDetail?.name.orEmpty()
                    ),
                    style = AppTheme.typography.bodyLarge,
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
                        if (!editable) {
                            viewModel.clearPlaylistSongs()
                        }
                        removeDownloads(context, songs.map { it.song.id })
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    var showDeletePlaylistDialog by remember {
        mutableStateOf(false)
    }
    var showAiModifyDialog by remember {
        mutableStateOf(false)
    }
    val currentPlaylist = playlistDetail
    if (showAiModifyDialog && currentPlaylist != null) {
        AiModifyPlaylistDialog(
            playlistId = currentPlaylist.id,
            currentSongs = songs,
            onDismiss = { showAiModifyDialog = false }
        )
    }
    if (showDeletePlaylistDialog) {
        DefaultDialog(
            onDismiss = { showDeletePlaylistDialog = false },
            content = {
                Text(
                    text = stringResource(
                        R.string.delete_playlist_confirm,
                        playlistDetail?.name.orEmpty()
                    ),
                    style = AppTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            },
            buttons = {
                TextButton(
                    onClick = {
                        showDeletePlaylistDialog = false
                    }
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }
                TextButton(
                    onClick = {
                        showDeletePlaylistDialog = false
                        viewModel.deletePlaylist()
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        )
    }

    val headerItems = 2
    val lazyListState = rememberLazyListState()
    var dragInfo by remember {
        mutableStateOf<Pair<Int, Int>?>(null)
    }
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        scrollThresholdPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
    ) { from, to ->
        if (to.index >= headerItems && from.index >= headerItems) {
            val currentDragInfo = dragInfo
            dragInfo = if (currentDragInfo == null) {
                (from.index - headerItems) to (to.index - headerItems)
            } else {
                currentDragInfo.first to (to.index - headerItems)
            }

            mutableSongs.move(from.index - headerItems, to.index - headerItems)
        }
    }

    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging) {
            // Cleared up front: if the DB transaction below throws, or this
            // effect is cancelled/restarted before reaching the end, leaving
            // dragInfo set would make the NEXT drag's onMove silently reuse
            // this stale "from" index — read as the reorder getting stuck.
            val info = dragInfo
            dragInfo = null
            info?.let { (from, to) ->
                viewModel.movePlaylistSong(from, to)
            }
        }
    }

    val showTopBarTitle by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    // The local library scan streams thumbnails in as songs are found — this
    // playlist's own thumbnail list changes repeatedly while it's running, so
    // grabbing "the first one" live chases every intermediate result. Debounce:
    // only commit to a hero thumbnail once it's held still for a bit.
    val rawHeroUrl = playlistDetail?.artworkUrls?.firstOrNull()
    var heroUrl by remember { mutableStateOf(rawHeroUrl) }
    LaunchedEffect(rawHeroUrl) {
        delay(1500)
        heroUrl = rawHeroUrl
    }
    // Instant seed: route thumb -> live tile thumb. Paints mesh bg frame one
    // while the playlist row (and its debounced hero) loads.
    val instantLocalThumb = com.convx.music.ui.component.detailSeed(
        viewModel.thumbArg,
        com.convx.music.ui.utils.pendingSharedArtworkThumbFor(viewModel.playlistId),
        null,
    )
    val bgHeroUrl = instantLocalThumb ?: heroUrl
    val localChrome = com.convx.music.ui.component.rememberDetailChromeEntrance(iconCount = 1)

    val appearance = com.convx.music.ui.component.rememberPlaylistAppearance(bgHeroUrl, localPlaylistSeed(viewModel.playlistId))
    val tint = appearance.background
    com.convx.music.PublishAmbientBackgroundColor(tint)
    val onTint = appearance.foreground

    // Keep top-bar glass fixed; the global preference controls the nav bar and mini player.
    val glassConfig = LocalGlassEffectConfig.current.withFixedStyle()
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.resolvedTextColor() else onTint

    // Keep the hero capture unattached so glass chrome cannot sample itself.
    val heroBackdrop = rememberLayerBackdrop()

    // This sibling capture lets floating chrome blur the drawn list. Include the
    // tint in the capture so sparse areas do not show through as a transparent blur.
    val listBackdrop = rememberLayerBackdrop()
    val backdropFreeze = rememberBackdropFreeze()
    val heroZoom = rememberHeroZoom()

    // Shared by the hero merge and top blur layers.
    val hazeState = remember { HazeState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint),
    ) {
    HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.chromeShadow(chromeShape).liquidGlass(config = glassConfig, shape = chromeShape)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }
        // Capture through a plain Box; LazyColumn's recycled item layers do not flatten reliably.
        Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // Cache list content so sibling redraws do not re-record the backdrop.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            .graphicsLayer()
            .hazeSource(hazeState)) {
                com.convx.music.ui.component.ArtworkMeshBackground(bgHeroUrl, paused = { lazyListState.isScrollInProgress }, appearance = appearance)
        LazyColumn(
            state = lazyListState,
            overscrollEffect = heroZoom.listOverscroll(),
            modifier = Modifier.heroPullZoom(heroZoom),
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                .union(WindowInsets.ime)
                .asPaddingValues(),
        ) {
            playlistDetail?.let { detail ->
                item(key = "playlist_header") {
                    LocalPlaylistHeader(
                        detail = detail,
                        songs = songs,
                        onShowEditDialog = { showEditDialog = true },
                        onShowRemoveDownloadDialog = { showRemoveDownloadDialog = true },
                        onshowDeletePlaylistDialog = { showDeletePlaylistDialog = true },
                        onSyncPlaylist = viewModel::refresh,
                        onModifyWithAi = { showAiModifyDialog = true },
                        onStartSearch = { isSearching = true },
                        snackbarHostState = snackbarHostState,
                        heroScale = heroZoom.scale,
                        hazeState = hazeState,
                        scrollBlurProgress = rememberHeroTopBlur(lazyListState),
                        searchValue = query.takeIf { isSearching },
                        onSearchValueChange = { query = it },
                        onSearchImeAction = { focusManager.clearFocus() },
                        searchFocusRequester = focusRequester,
                        modifier = Modifier.animateItem(),
                    )
                }
                if (detail.songCount == 0 && detail.remoteSongCount == 0) {
                    item(key = "empty_playlist_message") {
                        Text(stringResource(R.string.playlist_is_empty), color = onTint.copy(alpha = .48f),
                            modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center)
                    }
                } else {
                    item(key = "controls_row") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .animateItem(),
                        ) {
                            SortHeader(
                                sortType = sortType,
                                sortDescending = sortDescending,
                                onSortTypeChange = viewModel::setPlaylistTrackOrder,
                                onSortDescendingChange = viewModel::setPlaylistTrackDescending,
                                sortTypeText = { sortType ->
                                    when (sortType) {
                                        PlaylistTrackOrder.CUSTOM -> R.string.sort_by_custom
                                        PlaylistTrackOrder.CREATED -> R.string.sort_by_create_date
                                        PlaylistTrackOrder.NAME -> R.string.sort_by_name
                                        PlaylistTrackOrder.ARTIST -> R.string.sort_by_artist
                                        PlaylistTrackOrder.PLAY_TIME -> R.string.sort_by_play_time
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                supportsDescending = { it != PlaylistTrackOrder.CUSTOM },
                            )
                            if (editable) {
                                val description = if (locked) "Unlock playlist" else "Lock playlist"
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                    tooltip = { PlainTooltip { Text(description) } },
                                    state = rememberTooltipState(),
                                ) {
                                    FilledIconToggleButton(
                                        checked = locked,
                                        onCheckedChange = { locked = it },
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                    ) {
                                        if (locked) {
                                            SymbolIcon(symbol = sf("SFLock"), contentDescription = description)
                                        } else {
                                            SymbolIcon(symbol = sf("SFLockOpen"), contentDescription = description)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            itemsIndexed(
                items = if (isSearching) filteredSongs else mutableSongs,
                key = { _, song -> song.map.id },
                contentType = { _, _ -> "song_row" },
            ) { index, song ->
                ReorderableItem(
                    state = reorderableState,
                    key = song.map.id,
                ) {
                    val currentItem by rememberUpdatedState(song)

                    fun deleteFromPlaylist() {
                        viewModel.removePlaylistSong(
                            entryId = currentItem.map.id,
                            songId = currentItem.map.songId,
                            position = currentItem.map.position,
                            setVideoId = currentItem.track.setVideoId,
                        )
                    }

                    val swipeRemoveEnabled = LocalItemPrefs.current.swipeToRemoveSong
                    val dismissBoxState =
                        rememberSwipeToDismissBoxState(
                            positionalThreshold = { totalDistance -> totalDistance }
                        )
                    var processedDismiss by remember { mutableStateOf(false) }
                    LaunchedEffect(dismissBoxState.currentValue) {
                        val dv = dismissBoxState.currentValue
                        if (swipeRemoveEnabled && !processedDismiss && (
                                dv == SwipeToDismissBoxValue.StartToEnd ||
                                dv == SwipeToDismissBoxValue.EndToStart
                            )
                        ) {
                            processedDismiss = true
                            deleteFromPlaylist()
                        }
                        if (dv == SwipeToDismissBoxValue.Settled) {
                            processedDismiss = false
                        }
                    }

                    val onCheckedChange: (Boolean) -> Unit = {
                        if (it) {
                            selection.add(song.map.id)
                        } else {
                            selection.remove(Integer.valueOf(song.map.id))
                        }
                    }

                    val content: @Composable () -> Unit = {
                        SongListItem(
                            song = song.song,
                            isActive = song.song.id == mediaMetadata?.id,
                            isPlaying = isPlaying,
                            showInLibraryIcon = true,
                            shape = listItemShape(
                                index = index,
                                count = if (isSearching) filteredSongs.size else mutableSongs.size
                            ),
                            flat = true,
                            secondaryAdditiveChrome = onTint == androidx.compose.ui.graphics.Color.White,
                            subtitleAdditiveChrome = false,
                            subtitleColor = onTint.copy(alpha = 0.78f),
                            trailingContent = {
                                if (inSelectMode) {
                                    Checkbox(
                                        checked = selection.contains(song.map.id),
                                        onCheckedChange = onCheckedChange
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song.song,
                                                    playlistEntry = PlaylistEntryReference(
                                                        playlistId = viewModel.playlistId,
                                                        id = song.track.entryId,
                                                        songId = song.track.songId,
                                                        position = song.track.position,
                                                        setVideoId = song.track.setVideoId,
                                                    ),
                                                    playlistBrowseId = playlistDetail?.browseId,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                    ) {
                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                                    }

                                    if (sortType == PlaylistTrackOrder.CUSTOM && !locked && !inSelectMode && !isSearching && editable) {
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier.draggableHandle(),
                                        ) {
                                            SymbolIcon(symbol = sf("SFLine3Horizontal"), contentDescription = null)
                                        }
                                    }
                                }
                            },
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .combinedBounceClick(
                                    onClick = {
                                        if (inSelectMode) {
                                            onCheckedChange(!selection.contains(song.map.id))
                                        } else if (song.song.id == mediaMetadata?.id) {
                                            playerConnection.togglePlayPause()
                                        } else {
                                            playlistDetail?.let { currentPlaylist ->
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = currentPlaylist.name,
                                                        items = mutableSongs.map { it.song.toMediaItem() },
                                                        startIndex = mutableSongs.indexOfFirst { it.map.id == song.map.id },
                                                    ),
                                                )
                                            }
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

                    if (locked || inSelectMode || !swipeRemoveEnabled) {
                        Box(modifier = Modifier.animateItem()) {
                            content()
                        }
                    } else {
                        SwipeToDismissBox(
                            state = dismissBoxState,
                            backgroundContent = {},
                            modifier = Modifier.animateItem()
                        ) {
                            content()
                        }
                    }
                }
            }
            item(key = "bottom_spacer") {
                Spacer(Modifier.height(50.dp))
            }
        }
        }

        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = filteredSongs.size,
            sectionIndexMap = when (sortType) {
                PlaylistTrackOrder.NAME ->
                    remember(filteredSongs) {
                        buildAlphabetSectionIndex(filteredSongs) { it.song.song.title }
                    }

                PlaylistTrackOrder.ARTIST ->
                    remember(filteredSongs) {
                        buildAlphabetSectionIndex(filteredSongs) { entry ->
                            entry.song.artists.firstOrNull()?.name.orEmpty()
                        }
                    }

                else -> null
            },
        )

        // Keep the current title reveal, selection, and in-place search behavior.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        // The sibling chrome samples the attached list backdrop without capturing its own draw pass.
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.chromeShadow(chromeShape).liquidGlass(config = glassConfig, shape = chromeShape)
        } else {
            Modifier.background(AppTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f), chromeShape)
        }
        val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
        // Draw the blur before the chrome row so it stays behind the buttons and scrim.
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
                    SymbolIcon(symbol = sf("SFXmark"), contentDescription = null)
                }

                Text(
                    text = pluralStringResource(R.plurals.n_selected, selection.size, selection.size),
                    style = AppTheme.typography.titleMedium,
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
                        checked = selection.size == songs.size && selection.isNotEmpty(),
                        onCheckedChange = {
                            if (selection.size == songs.size) {
                                selection.clear()
                            } else {
                                selection.clear()
                                selection.addAll(songs.map { it.map.id })
                            }
                        }
                    )
                    val ambientColor = LocalAmbientBackgroundColor.current.value
                    IconButton(
                        enabled = selection.isNotEmpty(),
                        onClick = {
                            menuState.show {
                                SelectionSongMenu(
                                    songSelection = selection.mapNotNull { mapId ->
                                        songs.find { it.map.id == mapId }?.song
                                    },
                                    songPosition = selection.mapNotNull { mapId ->
                                        songs.find { it.map.id == mapId }?.map
                                    },
                                    onDismiss = menuState::dismiss,
                                    clearAction = onExitSelectionMode
                                )
                            }
                        }
                    ) {
                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier.additiveChrome(useGlass && ambientColor.isSpecified), tint = if (useGlass && ambientColor.isSpecified) ambientColor.asSoftIconTint() else chromeContentColor)
                    }
                }
            } else {
                GlassCircleButton(
                    onClick = { if (isSearching) exitSearch() else navController.navigateUp() },
                    onLongClick = { if (isSearching) exitSearch() else navController.backToMain() },
                    modifier = with(localChrome) {
                        Modifier.backEnter().then(com.convx.music.ui.component.backExitModifier())
                    },
                ) {
                    SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                }

                if (showTopBarTitle) {
                    Text(
                        text = playlistDetail?.name.orEmpty(),
                        style = AppTheme.typography.titleMedium,
                        color = chromeContentColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }

            }
        }
        }
    }
    }

    SnackbarHost(
        hostState = snackbarHostState,
            modifier =
            Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.union(WindowInsets.ime))
                .align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun LocalPlaylistHeader(
    detail: PlaylistDetail,
    songs: List<LocalPlaylistSong>,
    onShowEditDialog: () -> Unit,
    onShowRemoveDownloadDialog: () -> Unit,
    onshowDeletePlaylistDialog: () -> Unit,
    onSyncPlaylist: () -> Unit,
    onModifyWithAi: () -> Unit,
    onStartSearch: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier,
    heroScale: Float = 1f,
    hazeState: HazeState? = null,
    scrollBlurProgress: State<Float> = ZeroScrollProgress,
    searchValue: TextFieldValue? = null,
    onSearchValueChange: (TextFieldValue) -> Unit = {},
    onSearchImeAction: () -> Unit = {},
    searchFocusRequester: FocusRequester? = null,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val scope = rememberCoroutineScope()
    val downloadUtil = LocalDownloadUtil.current.get()
    val downloads by downloadUtil.downloads.collectAsStateWithLifecycle()
    val downloadState = remember(songs, downloads) { combinedDownloadState(songs, downloads) { it.song.id } }
    val launchCoverPicker = rememberPlaylistCoverPicker(
        playlistId = detail.id,
        browseId = detail.browseId,
        isEditable = detail.isEditable,
        onError = { message -> scope.launch { snackbarHostState.showSnackbar(message) } },
    )
    val artwork = com.convx.music.ui.utils.pendingSharedArtworkThumbFor(detail.id) ?: detail.artworkUrls.firstOrNull()
    val appearance = com.convx.music.ui.component.rememberPlaylistAppearance(artwork)
    val duration = remember(songs) { songs.fastSumBy { it.song.song.duration } }
    val count = if (detail.songCount == 0) detail.remoteSongCount ?: 0 else detail.songCount
    com.convx.music.ui.component.CollectionDetailHeader(
        title = detail.name,
        artwork = artwork,
        metadata = pluralStringResource(R.plurals.n_song, count, count) +
            if (duration > 0) " \u00b7 " + makeTimeString(duration * 1000L) else "",
        description = detail.description,
        foreground = appearance.foreground,
        playing = isPlaying && songs.any { it.song.id == mediaMetadata?.id },
        enabled = songs.isNotEmpty(),
        onPlay = {
            if (songs.any { it.song.id == mediaMetadata?.id }) playerConnection.togglePlayPause()
            else playerConnection.playQueue(ListQueue(title = detail.name, items = songs.map { it.song.toMediaItem() }))
        },
        onShuffle = { playerConnection.playQueue(ListQueue(title = detail.name, items = songs.shuffled().map { it.song.toMediaItem() })) },
        onSearch = onStartSearch,
        searchValue = searchValue,
        onSearchValueChange = onSearchValueChange,
        onSearchImeAction = onSearchImeAction,
        searchFocusRequester = searchFocusRequester,
        onRightAction = {
                        menuState.show {
                            LocalPlaylistMenu(
                                playlist = detail,
                                songs = songs,
                                context = context,
                                downloadState = downloadState,
                                onEdit = onShowEditDialog,
                                onSync = {
                                    if (detail.browseId != null) {
                                        onSyncPlaylist()
                                        scope.launch {
                                            snackbarHostState.showSnackbar(context.getString(R.string.playlist_synced))
                                        }
                                    }
                                },
                                onDelete = onshowDeletePlaylistDialog,
                                onDownload = {
                                    when (downloadState) {
                                        Download.STATE_COMPLETED -> onShowRemoveDownloadDialog()
                                        Download.STATE_DOWNLOADING -> cancelDownloads(
                                            context,
                                            songs.map { it.song.id },
                                            downloads,
                                        )

                                        else -> downloadSongs(
                                            context,
                                            songs.map {
                                                DownloadTarget(
                                                    it.song.id,
                                                    it.song.song.title,
                                                    it.song.artists.firstOrNull()?.name,
                                                    it.song.song.duration,
                                                )
                                            },
                                            downloads,
                                        )
                                    }
                                },
                                onQueue = {
                                    playerConnection.addToQueue(
                                        items = songs.map { it.song.toMediaItem() }
                                    )
                                },
                                onModifyWithAi = onModifyWithAi,
                                onDismiss = { menuState.dismiss() }
                            )
                        }
                    },
        onEditCover = launchCoverPicker.takeIf { detail.isEditable },
        modifier = modifier.padding(top = com.convx.music.ui.component.topBarContentPadding()),
    )
}

@Composable
private fun MetadataChip(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = AppTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    } catch (_: SecurityException) {
        null
    }
}
