/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.playlist

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.chromeShadow
import com.convx.music.ui.component.LocalMusicEmptyState
import com.convx.music.ui.component.LocalMusicPermissionPrompt
import com.convx.music.ui.component.rememberLocalAudioPermission
import com.convx.music.ui.utils.appTopBarWindowInsets
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
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ButtonDefaults
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.OutlinedTextField as TextField
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowClockwise
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFXmark
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastSumBy
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.LocalDownloadUtil
import androidx.media3.exoplayer.offline.Download
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.combinedDownloadState
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.cancelDownloads
import com.convx.music.playback.removeDownloads
import com.convx.music.R
import com.convx.music.constants.SongSortDescendingKey
import com.convx.music.constants.SongSortType
import com.convx.music.constants.SongSortTypeKey
import com.convx.music.constants.YtmSyncKey
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.buildAlphabetSectionIndex
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import com.convx.music.ui.component.EmptyPlaceholder
import com.convx.music.ui.component.ExpandableText
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.ChromeScrim
import com.convx.music.ui.component.rememberChromeScrimProgress
import com.convx.music.ui.player.MeshGradientBackground
import com.convx.music.ui.player.dimmed
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.MenuState
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.withFixedStyle
import com.convx.music.ui.component.isGlassAllowed
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.ui.component.asSoftIconTint
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.menu.AutoPlaylistMenu
import com.convx.music.ui.menu.SelectionSongMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.theme.LocalMetadataColor
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.utils.listItemShape
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.AutoPlaylistViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AutoPlaylistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: AutoPlaylistViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    // Pre-sorted + filtered by the sort/hide prefs inside the ViewModel.
    val songs by viewModel.likedSongs.collectAsStateWithLifecycle()

    val (sortType, onSortTypeChange) = rememberEnumPreference(
        SongSortTypeKey,
        SongSortType.CREATE_DATE,
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(SongSortDescendingKey, true)

    // Refresh liked and uploaded playlists when opened.
    val (ytmSync) = rememberPreference(YtmSyncKey, true)
    LaunchedEffect(Unit) {
        if (ytmSync) {
            when (viewModel.playlist) {
                "liked" -> viewModel.syncLikedSongs()
                "podcasts" -> viewModel.syncLikedSongs()
                "uploaded" -> viewModel.syncUploadedSongs()
            }
        }
    }

    // Local music: storage permission + device scan, so the Apple-styled local view
    // can populate/refresh the library itself (no separate plain screen needed).
    val isLocal = viewModel.playlist == "local"
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasAudioPermission by remember { mutableStateOf(false) }
    val audioPermissionState = rememberLocalAudioPermission { granted -> hasAudioPermission = granted }
    LaunchedEffect(isLocal) {
        if (isLocal) {
            hasAudioPermission = context.checkSelfPermission(audioPermission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    val (titleRes, iconRes) = when (viewModel.playlist) {
        "liked" -> R.string.liked to R.drawable.favorite
        "podcasts" -> R.string.filter_podcasts to R.drawable.radio
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
    val exitSearch = {
        focusManager.clearFocus()
        isSearching = false
        query = TextFieldValue()
    }

    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
        }
    }

    if (isSearching) {
        BackHandler(onBack = exitSearch)
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
    // Instant seed: route thumb -> live tile thumb. Paints mesh bg frame one
    // while the song flow (and its first-song hero) loads.
    val instantAutoThumb = com.convx.music.ui.component.detailSeed(
        viewModel.thumbArg,
        com.convx.music.ui.utils.pendingSharedArtworkThumbFor(viewModel.playlist),
        heroUrl,
    )
    val bgAutoUrl = instantAutoThumb ?: heroUrl
    val autoChrome = com.convx.music.ui.component.rememberDetailChromeEntrance(iconCount = 1)
    val appearance = com.convx.music.ui.component.rememberPlaylistAppearance(bgAutoUrl, autoPlaylistSeed(viewModel.playlist))
    val tint = appearance.background
    com.convx.music.PublishAmbientBackgroundColor(tint)
    val onTint = appearance.foreground

    val heroBackdrop = rememberLayerBackdrop()
    val hazeState = remember { HazeState() }

    // Capture the tinted list behind sibling chrome; an opaque capture avoids a doubled image.
    val listBackdrop = rememberLayerBackdrop()
    val backdropFreeze = rememberBackdropFreeze()

    // Progressive top blur: the artwork dissolves under the status bar as it
    // scrolls up (Haze on 13+, plain scrim below).

    val heroZoom = rememberHeroZoom()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint)
    ) {
        HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
            val chromeShape = ContinuousRoundedRectangle(percent = 50)

            Box(modifier = Modifier.fillMaxSize()) {
                // Capture through a plain Box; LazyColumn's recycled item layers do not flatten reliably.
                Box(modifier = Modifier
            .nestedScroll(backdropFreeze.connection)

            // Cache list content so sibling redraws do not re-record the backdrop.
            .graphicsLayer()
            .layerBackdrop(listBackdrop, frozen = backdropFreeze.frozen)
            .graphicsLayer()
            .hazeSource(hazeState)) {
                com.convx.music.ui.component.ArtworkMeshBackground(bgAutoUrl, paused = { lazyListState.isScrollInProgress }, appearance = appearance)
                LazyColumn(
                    state = lazyListState,
                    overscrollEffect = heroZoom.listOverscroll(),
                    modifier = Modifier.heroPullZoom(heroZoom),
                    contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                ) {
                    item(key = "header_title") {
                        Spacer(Modifier.height(0.dp))
                    }

                        item(key = "playlist_header") {
                            AutoPlaylistHeader(
                                onStartSearch = { isSearching = true },
                                    searchValue = query.takeIf { isSearching },
                                    onSearchValueChange = { query = it },
                                    onSearchImeAction = { focusManager.clearFocus() },
                                    searchFocusRequester = focusRequester,
                                    songs = filteredSongs,
                                    titleRes = titleRes,
                                    iconRes = iconRes,
                                    context = context,
                                    menuState = menuState,
                                    modifier = Modifier,
                                )
                        }

                    if (filteredSongs.isEmpty() && !isSearching) {
                        if (isLocal) {
                            // Local music needs storage access first, then a scan: say what is
                            // being asked for instead of an empty-playlist message and a bare button.
                            item(key = "local_prompt") {
                                Box(Modifier.padding(top = 60.dp)) {
                                    when {
                                        !hasAudioPermission -> LocalMusicPermissionPrompt(permission = audioPermissionState)
                                        isScanning -> Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(48.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) { CircularProgressIndicator() }
                                        else -> LocalMusicEmptyState(onScan = { viewModel.scanLocal(context) })
                                    }
                                }
                            }
                        } else {
                            item(key = "empty_placeholder") {
                                EmptyPlaceholder(
                                    icon = iconRes,
                                    text = stringResource(R.string.playlist_is_empty),
                                    modifier = Modifier.padding(top = 100.dp),
                                )
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

                            CompositionLocalProvider(
                                LocalMetadataColor provides if (viewModel.playlist == "liked") {
                                    onTint.copy(alpha = 0.82f)
                                } else {
                                    AppleTokens.Metadata
                                },
                            ) {
                            SongListItem(
                                song = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                showInLibraryIcon = true,
                                flat = true,
                                secondaryAdditiveChrome = onTint == androidx.compose.ui.graphics.Color.White && viewModel.playlist != "liked",
                                subtitleAdditiveChrome = false,
                                subtitleColor = onTint.copy(alpha = 0.78f),
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
                                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
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

                val chromeScrimProgress = rememberChromeScrimProgress(lazyListState)
                // Sample the list's own attached capture (a sibling, so no
                // RenderNode cycle) rather than the ambient unattached
                // heroBackdrop, whose drawBackdrop early-returns and left this
                // chrome flat with no real blur behind it.
                CompositionLocalProvider(LocalAppBackdrop provides listBackdrop) {
            com.convx.music.ui.component.TopFadeBlur(hazeState, tint, Modifier.align(Alignment.TopCenter))
                // Built inside the provider so liquidGlass captures listBackdrop
                // at composition time, not the ambient unattached backdrop.
                // Keep top-bar glass fixed; the global preference controls the nav bar and mini player.
                val glassConfig = LocalGlassEffectConfig.current.withFixedStyle()
                val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
                val chromeBackgroundModifier = if (useGlass) {
                    Modifier.chromeShadow(chromeShape).liquidGlass(config = glassConfig, shape = chromeShape)
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
                            SymbolIcon(symbol = sf("SFXmark"), contentDescription = null)
                        }

                        Text(
                            text = pluralStringResource(R.plurals.n_selected, selection.size, selection.size),
                            style = AppTheme.typography.titleMedium,
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
                            val ambientColor = LocalAmbientBackgroundColor.current.value
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
                                SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier.additiveChrome(useGlass && ambientColor.isSpecified), tint = if (useGlass && ambientColor.isSpecified) ambientColor.asSoftIconTint() else onTint)
                            }
                        }
                    } else {
                        GlassCircleButton(
                            onClick = { if (isSearching) exitSearch() else navController.navigateUp() },
                            onLongClick = { if (isSearching) exitSearch() else navController.backToMain() },
                            modifier = with(autoChrome) {
                                Modifier.backEnter().then(com.convx.music.ui.component.backExitModifier())
                            },
                        ) {
                            SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                        }

                        Text(
                            playlistName,
                            color = onTint,
                            style = AppTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        )

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
SymbolIcon(symbol = sf("SFArrowClockwise"), contentDescription = null)
                                }
                            }

                            Spacer(Modifier.width(8.dp))
                        }

                    }
                }
                }
                }
            }
        }
    }
}

/** Accent per auto playlist, for the mesh behind it while it has no artwork of its own. */
private fun autoPlaylistSeed(kind: String): Int = when (kind) {
    "liked" -> 0xFFFF375F.toInt()
    "downloaded" -> 0xFF0A84FF.toInt()
    "cached" -> 0xFF30D158.toInt()
    "uploaded" -> 0xFFBF5AF2.toInt()
    "local" -> 0xFF64D2FF.toInt()
    else -> 0xFFFF9F0A.toInt()
}

@Composable
private fun AutoPlaylistHeader(
    songs: List<Song>,
    titleRes: Int,
    iconRes: Int,
    context: android.content.Context,
    menuState: MenuState,
    modifier: Modifier = Modifier,
    onStartSearch: () -> Unit = {},
    searchValue: TextFieldValue? = null,
    onSearchValueChange: (TextFieldValue) -> Unit = {},
    onSearchImeAction: () -> Unit = {},
    searchFocusRequester: FocusRequester? = null,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val downloadUtil = LocalDownloadUtil.current.get()
    val downloads by downloadUtil.downloads.collectAsStateWithLifecycle()
    val downloadableSongs = remember(songs) { songs.filterNot { it.song.isLocal } }
    val downloadState = remember(downloadableSongs, downloads) {
        combinedDownloadState(downloadableSongs, downloads) { it.id }
    }
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
val totalLength = remember(songs) { songs.fastSumBy { it.song.duration } }
    val title = context.getString(titleRes)
    val artwork = com.convx.music.ui.utils.pendingSharedArtworkThumbFor(com.convx.music.ui.utils.LocalMorphArtworkId.current)
        ?: songs.firstOrNull()?.thumbnailUrl
    val appearance = com.convx.music.ui.component.rememberPlaylistAppearance(artwork)
    com.convx.music.ui.component.CollectionDetailHeader(
        title = title,
        artwork = artwork,
        metadata = pluralStringResource(R.plurals.n_song, songs.size, songs.size) +
            if (totalLength > 0) " \u00b7 " + makeTimeString(totalLength * 1000L) else "",
        foreground = appearance.foreground,
        playing = isPlaying && songs.any { it.id == mediaMetadata?.id },
        enabled = songs.isNotEmpty(),
        onPlay = {
            if (songs.any { it.id == mediaMetadata?.id }) playerConnection.togglePlayPause()
            else playerConnection.playQueue(ListQueue(title = title, items = songs.map { it.toMediaItem() }))
        },
        onShuffle = { playerConnection.playQueue(ListQueue(title = title, items = songs.shuffled().map { it.toMediaItem() })) },
        onSearch = onStartSearch,
        searchValue = searchValue,
        onSearchValueChange = onSearchValueChange,
        onSearchImeAction = onSearchImeAction,
        searchFocusRequester = searchFocusRequester,
        onRightAction = {
                    menuState.show {
                        AutoPlaylistMenu(
                            downloadState = downloadState,
                            onQueue = {
                                playerConnection.addToQueue(songs.map { it.toMediaItem() })
                            },
                            onDownload = {
                                when (downloadState) {
                                    Download.STATE_COMPLETED -> removeDownloads(context, downloadableSongs.map { it.id })
                                    Download.STATE_DOWNLOADING, Download.STATE_QUEUED ->
                                        cancelDownloads(context, downloadableSongs.map { it.id }, downloads)
                                    else -> downloadSongs(
                                        context,
                                        downloadableSongs.map {
                                            DownloadTarget(it.id, it.song.title, it.artists.firstOrNull()?.name, it.song.duration)
                                        },
                                        downloads,
                                    )
                                }
                            },
                            onDismiss = { menuState.dismiss() },
                        )
                    }
                },
        modifier = modifier.padding(top = com.convx.music.ui.component.topBarContentPadding()),
    )
}
