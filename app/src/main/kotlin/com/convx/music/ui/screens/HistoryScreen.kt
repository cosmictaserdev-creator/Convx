/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import com.convx.music.ui.ios.compat.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.OutlinedTextField as TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.hazeSource
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachReversed
import androidx.activity.compose.LocalActivity
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.music.innertube.utils.parseCookieString
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.composables.sfsymbols.SfSymbols
import androidx.compose.ui.res.painterResource
import com.composables.sfsymbols.monochrome.SFMagnifyingglass
import com.composables.sfsymbols.monochrome.SFXmark
import com.convx.music.constants.HistorySource
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.extensions.metadata
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.ChipsRow
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.ios.compat.IconButton as CupertinoIconButton
import com.convx.music.ui.component.FrostedTopBar
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.YouTubeListItem
import com.convx.music.ui.component.GlassCircleButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.rememberHeroTopBlur
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.convx.music.ui.menu.SelectionMediaMetadataMenu
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.listItemShape
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.DateAgo
import com.convx.music.viewmodels.HistoryViewModel
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collect

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = koinViewModel(viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<Long>, Long>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val focusRequester = remember { FocusRequester() }
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

    val historySource by viewModel.historySource.collectAsStateWithLifecycle()

    val filteredRemoteContent by viewModel.filteredRemoteContent.collectAsStateWithLifecycle()
    val filteredEvents by viewModel.filteredEvents.collectAsStateWithLifecycle()
    val allEvents by viewModel.filteredFlatEvents.collectAsStateWithLifecycle()

    LaunchedEffect(query.text) {
        viewModel.searchQuery.value = query.text
    }

    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

    @SuppressLint("LocalContextGetResourceValueCall")
    fun dateAgoToString(dateAgo: DateAgo): String {
        return when (dateAgo) {
            DateAgo.Today -> context.getString(R.string.today)
            DateAgo.Yesterday -> context.getString(R.string.yesterday)
            DateAgo.ThisWeek -> context.getString(R.string.this_week)
            DateAgo.LastWeek -> context.getString(R.string.last_week)
            is DateAgo.Other -> dateAgo.date.format(DateTimeFormatter.ofPattern("yyyy/MM"))
        }
    }

    LaunchedEffect(allEvents) {
        selection.fastForEachReversed { eventId ->
            if (allEvents.find { it.event.id == eventId } == null) {
                selection.remove(eventId)
            }
        }
    }

    val lazyListState = rememberLazyListState()
    val historyTitleProgress = rememberTitleCollapseProgressState(lazyListState)

    LaunchedEffect(lazyListState, historySource) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index to
                lazyListState.layoutInfo.totalItemsCount
        }.collect { (lastVisible, totalItems) ->
            if (historySource == HistorySource.LOCAL && lastVisible != null && lastVisible >= totalItems - 10) {
                viewModel.loadMoreEvents()
            }
        }
    }

    val heroUrl = remember(filteredRemoteContent, filteredEvents) {
        if (historySource == HistorySource.REMOTE) {
            filteredRemoteContent?.firstOrNull()?.songs?.firstOrNull()?.thumbnail
        } else {
            filteredEvents.values.firstOrNull()?.firstOrNull()?.song?.song?.thumbnailUrl
        }
    }
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = if (historySource == HistorySource.LOCAL) {
            allEvents.map { it.song.song.thumbnailUrl to false }
        } else emptyList()
    )
    val tint = rememberHeroTint(heroUrl)
    val onTint = AppleTokens.onColor(tint)

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val heroBackdrop = rememberLayerBackdrop()

    val heroZoom = rememberHeroZoom()

    HeroBackground(
        tint = tint,
        heroSource = heroSource,
        blurArtwork = true,
        fullBlur = true,
        bottomGradient = true,
        // Progressive top blur: the artwork dissolves under the status bar as it
        // scrolls up (Haze on 13+, plain scrim below).
        topBlurProgress = rememberHeroTopBlur(lazyListState),
        heroScale = heroZoom.scale,
        modifier = Modifier.fillMaxSize(),
    ) {
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val hazeState = remember { dev.chrisbanes.haze.HazeState() }

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                overscrollEffect = heroZoom.listOverscroll(),
                modifier = Modifier.heroPullZoom(heroZoom).hazeSource(hazeState),
                contentPadding = LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    .asPaddingValues(),
            ) {
                item(key = "history_header") {
                    LargeScreenTitle(
                        title = stringResource(R.string.history),
                        color = onTint,
                    )
                }

                item(key = "chips_row") {
                    ChipsRow(
                        chips = if (isLoggedIn) listOf(
                            HistorySource.LOCAL to stringResource(R.string.local_history),
                            HistorySource.REMOTE to stringResource(R.string.remote_history),
                        ) else {
                            listOf(HistorySource.LOCAL to stringResource(R.string.local_history))
                        },
                        currentValue = historySource,
                        onValueUpdate = {
                            viewModel.historySource.value = it
                            if (it == HistorySource.REMOTE){
                                viewModel.fetchRemoteHistory()
                            }
                        }
                    )
                }

                if (historySource == HistorySource.REMOTE && isLoggedIn) {
                    filteredRemoteContent?.forEach { section ->
                        stickyHeader(contentType = "date_header") {
                            NavigationTitle(
                                title = section.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(tint.copy(alpha = 0.8f))
                            )
                        }

                        // Use song IDs, not list positions, so filtering does not recreate rows.
                        // Occurrence counts only distinguish duplicate songs in the same section.
                        val songKeys = buildList(section.songs.size) {
                            val seen = HashMap<String, Int>()
                            section.songs.forEach { song ->
                                val occurrence = seen[song.id] ?: 0
                                seen[song.id] = occurrence + 1
                                add("${section.title}_${song.id}_$occurrence")
                            }
                        }
                        itemsIndexed(
                            items = section.songs,
                            key = { index, _ -> songKeys[index] },
                            contentType = { _, _ -> "history_song" },
                        ) { index, song ->
                            YouTubeListItem(
                                item = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                shape = listItemShape(index, section.songs.size),
                                flat = true,
                                trailingContent = {
                                    CupertinoIconButton(
                                        onClick = {
                                            menuState.show {
                                                YouTubeSongMenu(
                                                    song = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                    onHistoryRemoved = {
                                                        viewModel.fetchRemoteHistory()
                                                    }
                                                )
                                            }
                                        }
                                    ) {
                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
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
                                                    YouTubeQueue.radio(song.toMediaMetadata())
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            menuState.show {
                                                YouTubeSongMenu(
                                                    song = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                    onHistoryRemoved = {
                                                        viewModel.fetchRemoteHistory()
                                                    }
                                                )
                                            }
                                        }
                                    )
                            )
                        }
                    }
                } else {
                    filteredEvents.forEach { (dateAgo, dateEvents) ->
                        stickyHeader(contentType = "date_header") {
                            NavigationTitle(
                                title = dateAgoToString(dateAgo),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(tint.copy(alpha = 0.8f))
                            )
                        }

                        itemsIndexed(
                            items = dateEvents,
                            key = { _, event -> event.event.id },
                            contentType = { _, _ -> "history_event" },
                        ) { index, event ->
                            val onCheckedChange: (Boolean) -> Unit = remember(event.event.id) {
                                { checked ->
                                    if (checked) {
                                        selection.add(event.event.id)
                                    } else {
                                        selection.remove(event.event.id)
                                    }
                                }
                            }

                            SongListItem(
                                song = event.song,
                                isActive = event.song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                showInLibraryIcon = true,
                                showDownloadIcon = false,
                                shape = listItemShape(index, dateEvents.size),
                                flat = true,
                                trailingContent = {
                                    if (inSelectMode) {
                                        Checkbox(
                                            checked = event.event.id in selection,
                                            onCheckedChange = onCheckedChange
                                        )
                                    } else {
                                        CupertinoIconButton(
                                            onClick = {
                                                menuState.show {
                                                    SongMenu(
                                                        originalSong = event.song,
                                                        event = event.event,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss
                                                    )
                                                }
                                            }
                                        ) {
                                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            if (inSelectMode) {
                                                onCheckedChange(event.event.id !in selection)
                                            } else if (event.song.id == mediaMetadata?.id) {
                                                playerConnection.togglePlayPause()
                                            } else {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = dateAgoToString(dateAgo),
                                                        items = dateEvents.map { it.song.toMediaItem() },
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
                            )
                        }
                    }
                }

                item(key = "bottom_spacer_history") {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            HideOnScrollFAB(
                visible = if (historySource == HistorySource.REMOTE) {
                    filteredRemoteContent?.any { it.songs.isNotEmpty() } == true
                } else {
                    allEvents.isNotEmpty()
                },
                lazyListState = lazyListState,
                icon = R.drawable.shuffle,
                onClick = {
                    if (historySource == HistorySource.REMOTE && filteredRemoteContent != null) {
                        val songs = filteredRemoteContent?.flatMap { it.songs } ?: emptyList()
                        if (songs.isNotEmpty()) {
                            playerConnection.playQueue(
                                ListQueue(
                                    title = context.getString(R.string.history),
                                    items = songs.map { it.toMediaItem() }.shuffled()
                                )
                            )
                        }
                    } else {
                        playerConnection.playQueue(
                            ListQueue(
                                title = context.getString(R.string.history),
                                items = allEvents.map { it.song.toMediaItem() }.shuffled()
                            )
                        )
                    }
                }
            )

            com.convx.music.ui.component.TopFadeBlur(
                hazeState = hazeState,
                pageColor = tint,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            // The large title's stand-in once it has scrolled away. Centred, so
            // it sits between the chrome row's edge buttons rather than under
            // them — but the search field takes the centre in those modes.
            if (!inSelectMode && !isSearching) {
                FrostedTopBar(
                    title = stringResource(R.string.history),
                    titleAlpha = 0f,
                    titleAlphaProvider = { historyTitleProgress.value },
                    titleColor = onTint,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
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
                            checked = selection.size == allEvents.size && selection.isNotEmpty(),
                            onCheckedChange = {
                                if (selection.size == allEvents.size) {
                                    selection.clear()
                                } else {
                                    selection.clear()
                                    selection.addAll(allEvents.map { it.event.id })
                                }
                            }
                        )
                        CupertinoIconButton(
                            enabled = selection.isNotEmpty(),
                            onClick = {
                                menuState.show {
                                    SelectionMediaMetadataMenu(
                                        songSelection = selection.mapNotNull { eventId ->
                                            allEvents.find { it.event.id == eventId }?.song?.toMediaItem()?.metadata
                                        },
                                        onDismiss = menuState::dismiss,
                                        clearAction = onExitSelectionMode,
                                        currentItems = emptyList()
                                    )
                                }
                            }
                        ) {
                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                        }
                    }
                } else if (isSearching) {
                    GlassCircleButton(
                        onClick = {
                            isSearching = false
                            query = TextFieldValue()
                        },
                    ) {
                        SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(chromeShape)
                            .background(onTint.copy(alpha = 0.15f), chromeShape)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search),
                                    style = AppTheme.typography.titleMedium,
                                    color = onTint.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            textStyle = AppTheme.typography.titleMedium.copy(color = onTint),
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
                        onClick = {
                            if (isSearching) {
                                isSearching = false
                                query = TextFieldValue()
                            } else {
                                navController.navigateUp()
                            }
                        },
                        onLongClick = {
                            if (!isSearching) {
                                navController.backToMain()
                            }
                        }
                    ) {
                        SymbolIcon(symbol = sf("chevron.left"), contentDescription = null)
                    }

                    Spacer(Modifier.weight(1f))

                    GlassCircleButton(onClick = { isSearching = true }) {
                        SymbolIcon(symbol = sf("SFMagnifyingglass"), contentDescription = null)
                    }
                }
            }
        }
      }
    }
}
