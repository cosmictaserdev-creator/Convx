/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.playlist

import com.convxy.music.ui.utils.FloatingChromeSpacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.datastore.preferences.core.edit
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.utils.completed
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.LocalSyncUtils
import com.convxy.music.R
import com.convxy.music.constants.DarkModeKey
import com.convxy.music.constants.PendingPlaylistDeletesKey
import com.convxy.music.constants.PlaylistEditLockKey
import com.convxy.music.constants.PlaylistSongSortDescendingKey
import com.convxy.music.constants.PlaylistSongSortType
import com.convxy.music.constants.PlaylistSongSortTypeKey
import com.convxy.music.constants.SwipeToRemoveSongKey
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.PlaylistSong
import com.convxy.music.db.entities.PlaylistSongMap
import com.convxy.music.extensions.move
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.ActionPromptDialog
import com.convxy.music.ui.component.DefaultDialog
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.GlassCircleButton
import com.convxy.music.ui.component.ChromeScrim
import com.convxy.music.ui.component.rememberChromeScrimProgress
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.LocalAppBackdrop
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convxy.music.ui.component.LocalItemPrefs
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.OverlayEditButton
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortHeader
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.menu.CustomThumbnailMenu
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.menu.LocalPlaylistMenu
import com.convxy.music.ui.menu.SelectionSongMenu
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.screens.settings.DarkMode
import com.convxy.music.ui.component.AlbumStyleHeroImage
import com.convxy.music.LocalTabView
import com.convxy.music.ui.utils.rememberHeroZoom
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.HeroCardHeader
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.makeTimeString
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.utils.reportException
import com.convxy.music.viewmodels.LocalPlaylistViewModel
import com.yalantis.ucrop.UCrop
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDateTime
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.cancelDownloads
import com.convxy.music.playback.downloadSongs
import com.convxy.music.playback.removeDownloads

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocalPlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: LocalPlaylistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val playlist by viewModel.playlist.collectAsState()
    val songs by viewModel.playlistSongs.collectAsState()
    val mutableSongs = remember { mutableStateListOf<PlaylistSong>() }
    val playlistLength =
        remember(songs) {
            songs.fastSumBy { it.song.song.duration }
        }
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        PlaylistSongSortTypeKey,
        PlaylistSongSortType.CUSTOM
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(
        PlaylistSongSortDescendingKey,
        true
    )
    var locked by rememberPreference(PlaylistEditLockKey, defaultValue = true)

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
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

    val focusRequester = remember { FocusRequester() }
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
        BackHandler {
            isSearching = false
            query = TextFieldValue()
        }
    } else if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    val downloadUtil = LocalDownloadUtil.current
    var downloadState by remember {
        mutableIntStateOf(Download.STATE_STOPPED)
    }

    val editable: Boolean = playlist?.playlist?.isEditable == true

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
            downloadState =
                if (songs.all { downloads[it.song.id]?.state == Download.STATE_COMPLETED }) {
                    Download.STATE_COMPLETED
                } else if (songs.all {
                        downloads[it.song.id]?.state == Download.STATE_QUEUED ||
                                downloads[it.song.id]?.state == Download.STATE_DOWNLOADING ||
                                downloads[it.song.id]?.state == Download.STATE_COMPLETED
                    }
                ) {
                    Download.STATE_DOWNLOADING
                } else {
                    Download.STATE_STOPPED
                }
        }
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        playlist?.playlist?.let { playlistEntity ->
            var editName by remember {
                mutableStateOf(
                    TextFieldValue(playlistEntity.name, TextRange(playlistEntity.name.length))
                )
            }
            var editDescription by remember {
                mutableStateOf(TextFieldValue(playlistEntity.description ?: ""))
            }
            DefaultDialog(
                onDismiss = { showEditDialog = false },
                buttons = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val name = editName.text.trim().ifEmpty { playlistEntity.name }
                            val description = editDescription.text.trim().ifEmpty { null }
                            database.query {
                                update(
                                    playlistEntity.copy(
                                        name = name,
                                        description = description,
                                        lastUpdateTime = LocalDateTime.now()
                                    )
                                )
                            }
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                playlistEntity.browseId?.let { YouTube.renamePlaylist(it, name) }
                            }
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
                        style = MaterialTheme.typography.headlineSmall,
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
                        playlist?.playlist!!.name
                    ),
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
                        if (!editable) {
                            database.transaction {
                                playlist?.id?.let { clearPlaylist(it) }
                            }
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
    if (showAiModifyDialog && playlist != null) {
        val currentPlaylist = playlist
        AiModifyPlaylistDialog(
            playlistId = currentPlaylist?.playlist?.id ?: return,
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
                        playlist?.playlist!!.name
                    ),
                    style = MaterialTheme.typography.bodyLarge,
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
                        database.query {
                            playlist?.let { delete(it.playlist) }
                        }
                        val browseId = playlist?.playlist?.browseId
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            if (browseId == null) return@launch
                            // Marked pending before the network call: if the delete
                            // fails, or a library sync races it while it's still in
                            // flight, the sync sees this browseId as pending and
                            // won't resurrect the playlist it was just told to remove.
                            context.dataStore.edit {
                                it[PendingPlaylistDeletesKey] = (it[PendingPlaylistDeletesKey] ?: emptySet()) + browseId
                            }
                            YouTube.deletePlaylist(browseId)
                        }
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
                database.transaction {
                    move(viewModel.playlistId, from, to)
                }

                // Sync order with YT Music
                if (viewModel.playlist.value?.playlist?.browseId != null) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        val playlistSongMap = database.playlistSongMaps(viewModel.playlistId, 0)
                        val successorIndex = if (from > to) to else to + 1
                        val successorSetVideoId = playlistSongMap.getOrNull(successorIndex)?.setVideoId

                        playlistSongMap.getOrNull(from)?.setVideoId?.let { setVideoId ->
                            YouTube.moveSongPlaylist(
                                viewModel.playlist.value?.playlist?.browseId!!,
                                setVideoId,
                                successorSetVideoId
                            )
                        }
                    }
                }
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
    val rawHeroUrl = playlist?.thumbnails?.firstOrNull()
    var heroUrl by remember { mutableStateOf(rawHeroUrl) }
    LaunchedEffect(rawHeroUrl) {
        delay(1500)
        heroUrl = rawHeroUrl
    }

    val tint = rememberHeroTint(heroUrl)
    val onTint = com.convxy.music.ui.theme.AppleTokens.onColor(tint)

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val chromeShape = ContinuousRoundedRectangle(percent = 50)
    val chromeContentColor = if (useGlass) glassConfig.textColor else onTint

    // Unattached backdrop (never .layerBackdrop'd): glass chrome sampling it
    // early-returns → translucent frosted surface, no RenderNode self-reference.
    // See ArtistScreen.kt for the full explanation of the cycle this avoids.
    val heroBackdrop = rememberLayerBackdrop()

    // A SECOND, ATTACHED backdrop: .layerBackdrop'd onto the LazyColumn below
    // (see its modifier), which is a *sibling* of the floating chrome row, not
    // an ancestor — the chrome row samples a texture of already-drawn list
    // content, it never captures its own draw pass, so no cycle. This is what
    // lets the chrome buttons show real blurred list/hero content behind them
    // instead of heroBackdrop's flat empty-capture fallback.
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
    val heroZoom = rememberHeroZoom()

    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = songs.map { it.song.song.thumbnailUrl to false },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint),
    ) {
    HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
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
            playlist?.let { playlist ->
                if (playlist.songCount == 0 && playlist.playlist.remoteSongCount == 0) {
                    item(key = "empty_placeholder") {
                        EmptyPlaceholder(
                            icon = R.drawable.music_note,
                            text = stringResource(R.string.playlist_is_empty),
                            modifier = Modifier.animateItem()
                        )
                    }
                } else {
                    if (isSearching) {
                        // No hero header in search mode — reserve the floating chrome's
                        // height so the first row doesn't start under the status bar.
                        item(key = "search_chrome_spacer") { FloatingChromeSpacer() }
                    }
                    if (!isSearching) {
                        item(key = "playlist_header") {
                            LocalPlaylistHeader(
                                playlist = playlist,
                                songs = songs,
                                onShowEditDialog = { showEditDialog = true },
                                onShowRemoveDownloadDialog = { showRemoveDownloadDialog = true },
                                onshowDeletePlaylistDialog = { showDeletePlaylistDialog = true },
                                onModifyWithAi = { showAiModifyDialog = true },
                                onStartSearch = { isSearching = true },
                                snackbarHostState = snackbarHostState,
                                heroScale = heroZoom.scale,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

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
                                onSortTypeChange = onSortTypeChange,
                                onSortDescendingChange = onSortDescendingChange,
                                sortTypeText = { sortType ->
                                    when (sortType) {
                                        PlaylistSongSortType.CUSTOM -> R.string.sort_by_custom
                                        PlaylistSongSortType.CREATE_DATE -> R.string.sort_by_create_date
                                        PlaylistSongSortType.NAME -> R.string.sort_by_name
                                        PlaylistSongSortType.ARTIST -> R.string.sort_by_artist
                                        PlaylistSongSortType.PLAY_TIME -> R.string.sort_by_play_time
                                    }
                                },
                                modifier = Modifier.weight(1f),
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
                                            Icon(
                                                painter = painterResource(R.drawable.lock),
                                                contentDescription = description,
                                            )
                                        } else {
                                            Icon(
                                                painter = painterResource(R.drawable.lock_open),
                                                contentDescription = description,
                                            )
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
                        database.transaction {
                            coroutineScope.launch {
                                playlist?.playlist?.browseId?.let { browseId ->
                                    val setVideoId = getSetVideoId(currentItem.map.songId)
                                    setVideoId?.setVideoId?.let { setVideoIdValue ->
                                        YouTube.removeFromPlaylist(
                                            browseId,
                                            currentItem.map.songId,
                                            setVideoIdValue
                                        )
                                    }
                                }
                            }
                            move(
                                currentItem.map.playlistId,
                                currentItem.map.position,
                                Int.MAX_VALUE
                            )
                            delete(currentItem.map.copy(position = Int.MAX_VALUE))
                        }
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
                                                    playlistSong = song,
                                                    playlistBrowseId = playlist?.playlist?.browseId,
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

                                    if (sortType == PlaylistSongSortType.CUSTOM && !locked && !inSelectMode && !isSearching && editable) {
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier.draggableHandle(),
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.drag_handle),
                                                contentDescription = null,
                                            )
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
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = playlist!!.playlist.name,
                                                    items = songs.map { it.song.toMediaItem() },
                                                    startIndex = songs.indexOfFirst { it.map.id == song.map.id },
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
                PlaylistSongSortType.NAME ->
                    remember(filteredSongs) {
                        buildAlphabetSectionIndex(filteredSongs) { it.song.song.title }
                    }

                PlaylistSongSortType.ARTIST ->
                    remember(filteredSongs) {
                        buildAlphabetSectionIndex(filteredSongs) { entry ->
                            entry.song.artists.firstOrNull()?.name.orEmpty()
                        }
                    }

                else -> null
            },
        )

        // Floating glass chrome over the tinted background, replacing the
        // Material TopAppBar — always visible, no title-bar-on-scroll behavior
        // except the existing "reveal playlist name once scrolled" logic, which
        // is preserved as-is. Select mode and in-place search keep their exact
        // prior behavior, just restyled containers.
        CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
        // Built INSIDE the provider so liquidGlass captures listBackdrop (the
        // LazyColumn's own recorded content, a sibling — see its declaration
        // above), not the root appBackdrop — sampling appBackdrop here is the
        // RenderNode cycle. listBackdrop is attached (unlike heroBackdrop), so
        // this chrome actually shows blurred list/hero content, not just a
        // flat tinted fallback.
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f), chromeShape)
        }
        val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
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

                if (showTopBarTitle) {
                    Text(
                        text = playlist?.playlist?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
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
    playlist: Playlist,
    songs: List<PlaylistSong>,
    onShowEditDialog: () -> Unit,
    onShowRemoveDownloadDialog: () -> Unit,
    onshowDeletePlaylistDialog: () -> Unit,
    onModifyWithAi: () -> Unit,
    onStartSearch: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier,
    heroScale: Float = 1f,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val context = LocalContext.current
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val syncUtils = LocalSyncUtils.current
    val scope = rememberCoroutineScope()

    val playlistLength =
        remember(songs) {
            songs.fastSumBy { it.song.song.duration }
        }

    val downloadUtil = LocalDownloadUtil.current
    // Collected rather than folded into a LaunchedEffect so the current state of each
    // download is also available at click time — the download and cancel actions filter
    // on it, see DownloadActions.
    val downloads by downloadUtil.downloads.collectAsState()
    val downloadState = remember(songs, downloads) {
        when {
            songs.isEmpty() -> Download.STATE_STOPPED
            songs.all { downloads[it.song.id]?.state == Download.STATE_COMPLETED } ->
                Download.STATE_COMPLETED

            songs.all {
                downloads[it.song.id]?.state == Download.STATE_QUEUED ||
                    downloads[it.song.id]?.state == Download.STATE_DOWNLOADING ||
                    downloads[it.song.id]?.state == Download.STATE_COMPLETED
            } -> Download.STATE_DOWNLOADING

            else -> Download.STATE_STOPPED
        }
    }

    val liked = playlist.playlist.bookmarkedAt != null
    val editable: Boolean = playlist.playlist.isEditable

    val overrideThumbnail = remember {mutableStateOf<String?>(null)}
    var isCustomThumbnail: Boolean = playlist.thumbnails.firstOrNull()?.let {
        it.contains("studio_square_thumbnail") || it.contains("content://com.vivimusic.music")
    } ?: false


    val result = remember { mutableStateOf<Uri?>(null) }
    var pendingCropDestUri by remember { mutableStateOf<Uri?>(null) }
    var showEditNoteDialog by remember { mutableStateOf(false) }

    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == android.app.Activity.RESULT_OK) {
            val output = res.data?.let { UCrop.getOutput(it) } ?: pendingCropDestUri
            if (output != null) result.value = output
        }
    }

    val (darkMode, _) = rememberEnumPreference(
        DarkModeKey,
        defaultValue = DarkMode.AUTO
    )

    val cropColor = MaterialTheme.colorScheme
    val darkTheme = darkMode == DarkMode.ON || (darkMode == DarkMode.AUTO && isSystemInDarkTheme())

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { sourceUri ->
            val destFile = java.io.File(context.cacheDir, "playlist_cover_crop_${System.currentTimeMillis()}.jpg")
            val destUri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", destFile)
            pendingCropDestUri = destUri
    
            val options = UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(90)
                setHideBottomControls(true)
                setToolbarTitle(context.getString(R.string.edit_playlist_cover))
                
                setStatusBarLight(!darkTheme)

                setToolbarColor(cropColor.surface.toArgb())
                setToolbarWidgetColor(cropColor.inverseSurface.toArgb())
                setRootViewBackgroundColor(cropColor.surface.toArgb())
                setLogoColor(cropColor.surface.toArgb())
            }

            val intent = UCrop.of(sourceUri, destUri)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .getIntent(context)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cropLauncher.launch(intent)
        }
    }

    LaunchedEffect(result.value) {
        val uri = result.value ?: return@LaunchedEffect
        withContext(Dispatchers.IO) {
            when {
                playlist.playlist.browseId == null -> {
                    overrideThumbnail.value = uri.toString()
                    isCustomThumbnail = true

                    // Update the database with the new thumbnail
                    database.query {
                        update(playlist.playlist.copy(thumbnailUrl = uri.toString()))
                    }
                }

                else -> {
                    val bytes = uriToByteArray(context, uri)
                    YouTube.uploadCustomThumbnailLink(
                        playlist.playlist.browseId,
                        bytes!!
                    ).onSuccess { newThumbnailUrl ->
                        overrideThumbnail.value = newThumbnailUrl
                        isCustomThumbnail = true

                        // Update the database with the new thumbnail URL
                        database.query {
                            update(playlist.playlist.copy(thumbnailUrl = newThumbnailUrl))
                        }
                    }.onFailure {
                        if (it is ClientRequestException) {
                            snackbarHostState.showSnackbar("${it.response.status.value} ${it.response.status.description}")
                        }
                        reportException(it)
                    }
                }
            }
        }
    }

    // See the main LocalPlaylistScreen composable: debounce against the local
    // scan streaming thumbnails in one at a time.
    val rawHeroUrl = playlist.thumbnails.firstOrNull()
    var heroUrl by remember { mutableStateOf(rawHeroUrl) }
    LaunchedEffect(rawHeroUrl) {
        delay(1500)
        heroUrl = rawHeroUrl
    }

    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = songs.map { it.song.song.thumbnailUrl to false },
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
            if (showEditNoteDialog) {
                ActionPromptDialog(
                    title = stringResource(R.string.edit_playlist_cover),
                    onDismiss = { showEditNoteDialog = false },
                    onConfirm = {
                        showEditNoteDialog = false
                        pickLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onCancel = { showEditNoteDialog = false }
                ) {
                    if (playlist.playlist.browseId != null) {
                        Text(
                            text = stringResource(R.string.edit_playlist_cover_note),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Text(
                        text = stringResource(R.string.edit_playlist_cover_note_wait),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Wide layout: bounded artwork card with the name beside it instead of
            // the full-bleed square, which would fill the fold on a tablet.
            val tabView = LocalTabView.current
            val songCount = if (playlist.songCount == 0 && playlist.playlist.remoteSongCount != null) {
                playlist.playlist.remoteSongCount
            } else {
                playlist.songCount
            }
            val playlistInfoText = buildString {
                append(pluralStringResource(R.plurals.n_song, songCount, songCount))
                if (playlistLength > 0) {
                    append(" • ")
                    append(makeTimeString(playlistLength * 1000L))
                }
            }

            if (tabView) {
                HeroCardHeader(
                    artworkUrl = heroUrl,
                    title = {
                        Text(
                            text = playlist.playlist.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = onTint,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    subtitle = playlist.playlist.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onTint.copy(alpha = 0.7f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    actions = {
                        Text(
                            text = playlistInfoText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = onTint.copy(alpha = 0.5f),
                        )
                    },
                )
            } else {

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = if (editable) {
                    Modifier.clickable { showEditNoteDialog = true }
                } else {
                    Modifier
                },
            ) {
                AlbumStyleHeroImage(artworkUrl = heroUrl, heroScale = heroScale)
                if (editable) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { showEditNoteDialog = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = stringResource(R.string.edit_playlist_cover),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Playlist Name
            Text(
                text = playlist.playlist.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = onTint,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            playlist.playlist.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = onTint.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata - Song Count • Duration
            Text(
                text = playlistInfoText,
                style = MaterialTheme.typography.bodyMedium,
                color = onTint.copy(alpha = 0.7f)
            )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons Row — Redesigned for unified circular look
            // These keep the ambient (unattached) heroBackdrop deliberately.
            // Providing the list's own attached backdrop here is a RenderNode
            // cycle: this header is drawn INSIDE Modifier.layerBackdrop(
            // listBackdrop), so glass sampling it captures its own draw pass
            // (native stack overflow in RenderNode::prepareTreeImpl). Only the
            // floating chrome row — a *sibling* of that layer — may use it.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // More Options Button (Left - Circular)
                GlassCircleButton(
                    onClick = {
                        menuState.show {
                            LocalPlaylistMenu(
                                playlist = playlist,
                                songs = songs,
                                context = context,
                                downloadState = downloadState,
                                onEdit = onShowEditDialog,
                                onSync = {
                                    scope.launch(Dispatchers.IO) {
                                        val playlistPage = YouTube.playlist(playlist.playlist.browseId!!)
                                            .completed()
                                            .getOrNull() ?: return@launch
                                        database.transaction {
                                            clearPlaylist(playlist.id)
                                            playlistPage.songs
                                                .map(SongItem::toMediaMetadata)
                                                .onEach(::insert)
                                                .mapIndexed { position, song ->
                                                    PlaylistSongMap(
                                                        songId = song.id,
                                                        playlistId = playlist.id,
                                                        position = position,
                                                        setVideoId = song.setVideoId
                                                    )
                                                }
                                                .forEach(::insert)
                                        }
                                    }
                                    scope.launch(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar(context.getString(R.string.playlist_synced))
                                    }
                                },
                                onDelete = onshowDeletePlaylistDialog,
                                onDownload = {
                                    when (downloadState) {
                                        Download.STATE_COMPLETED -> onShowRemoveDownloadDialog()
                                        // Cancel, not remove: this used to delete every
                                        // song in the playlist, finished ones included.
                                        Download.STATE_DOWNLOADING -> cancelDownloads(
                                            context,
                                            songs.map { it.song.id },
                                            downloads,
                                        )

                                        else -> downloadSongs(
                                            context,
                                            songs.map {
                                                DownloadTarget(it.song.id, it.song.song.title)
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
                    size = 48.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Play Button (Center - Large Circle)
                // A playlist's own id never matches a song's albumId (different
                // id spaces) — that comparison was always false, so this button
                // never showed "pause" and always restarted the queue on tap.
                // Membership of the current song in this playlist's own song
                // list is the same "is this the active context" heuristic
                // AlbumScreen uses (there, albumId happens to match directly).
                val isPlayingThisPlaylist = isPlaying && songs.any { it.song.id == mediaMetadata?.id }
                GlassCircleButton(
                    onClick = {
                        if (isPlayingThisPlaylist) {
                            playerConnection.player.pause()
                        } else if (songs.any { it.song.id == mediaMetadata?.id }) {
                            playerConnection.player.play()
                        } else {
                            playerConnection.playQueue(
                                ListQueue(
                                    title = playlist.playlist.name,
                                    items = songs.map { it.song.toMediaItem() },
                                )
                            )
                        }
                    },
                    size = 72.dp,
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlayingThisPlaylist,
                        size = 32.dp,
                        modifier = Modifier.offset(x = 2.dp),
                    )
                }

                // Shuffle Button (Right - Circular)
                GlassCircleButton(
                    onClick = {
                        playerConnection.playQueue(
                            ListQueue(
                                title = playlist.playlist.name,
                                items = songs.shuffled().map { it.song.toMediaItem() },
                            )
                        )
                    },
                    size = 48.dp,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            val staticDescription = remember(songCount, playlistLength) {
                val name = playlist.playlist.name
                val trackCountText = context.resources.getQuantityString(R.plurals.n_song, songCount, songCount)
                "$name is a custom playlist featuring $trackCountText.${
                    if (playlistLength > 0) " Combined duration is ${makeTimeString(playlistLength * 1000L)}." else ""
                }"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.about_playlist),
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

@Composable
private fun MetadataChip(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
