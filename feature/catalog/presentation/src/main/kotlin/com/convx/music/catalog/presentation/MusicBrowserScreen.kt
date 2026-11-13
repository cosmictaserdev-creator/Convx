package com.convx.music.catalog.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.window.Dialog
import com.convx.music.playback.presentation.PlaybackFullPlayer
import com.convx.music.playback.presentation.PlaybackEqualizerSheet
import com.convx.music.playback.presentation.PlaybackMiniPlayer
import com.convx.music.playback.presentation.PlaybackPlayerMorph
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.YOUTUBE_MUSIC_PROVIDER
import com.convx.music.playback.PlaybackController
import com.convx.music.playback.PlaybackQueueController
import com.convx.music.playback.PlaybackQueueSnapshot
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.PlaybackTransitionMode
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import com.convx.music.designsystem.ConvxDesignTokens
import com.convx.music.ui.ios.component.IosText
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.ProvideIosTheme
import com.convx.music.ui.ios.foundation.iosTypography
import com.convx.music.ui.ios.foundation.darkIosColors
import kotlinx.coroutines.flow.collect
import org.koin.compose.koinInject

@Composable
fun MusicBrowserScreen(
    playbackController: PlaybackController? = null,
    modifier: Modifier = Modifier,
    isSignedIn: Boolean = false,
    isSleepTimerActive: Boolean = false,
    crossfadeAvailable: Boolean = false,
    equalizerAvailable: Boolean = false,
    onSleepTimerRequested: () -> Unit = {},
    onSignInRequested: () -> Unit = {},
) {
    val catalog: CatalogStateHolder = koinInject()
    val libraryActions: CatalogLibraryActionStateHolder = koinInject()
    val playlistActions: CatalogPlaylistActionStateHolder = koinInject()
    val state by catalog.state.collectAsState()
    val libraryActionState by libraryActions.state.collectAsState()
    val playlistActionState by playlistActions.state.collectAsState()
    val playback = playbackController?.playbackSnapshot?.collectAsState()?.value
    val queueSnapshot = playbackController?.queueSnapshot?.collectAsState()?.value ?: PlaybackQueueSnapshot.EMPTY
    val playbackSettingsStateHolder: PlaybackSettingsStateHolder = koinInject()
    val playbackSettings by playbackSettingsStateHolder.state.collectAsState()
    val playbackSettingsError by playbackSettingsStateHolder.errorMessage.collectAsState()
    val playerBackdrop = rememberLayerBackdrop()
    var showQueue by remember { mutableStateOf(false) }
    var showFullPlayer by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }
    DisposableEffect(catalog, libraryActions, playlistActions, playbackSettingsStateHolder) {
        onDispose {
            catalog.close()
            libraryActions.close()
            playlistActions.close()
            playbackSettingsStateHolder.close()
        }
    }
    LaunchedEffect(catalog) { catalog.loadHome() }
    ProvideIosTheme(colors = darkIosColors(), typography = iosTypography()) {
        Box(modifier.fillMaxSize().background(ConvxDesignTokens.Colors.Background).layerBackdrop(playerBackdrop)) {
        CatalogSharedTransitionPage(state = state, modifier = Modifier.fillMaxSize()) { pageState ->
            val listState = rememberLazyListState()
            val queue = remember(pageState.sections) {
                pageState.sections.flatMap { section ->
                    section.entries.filterIsInstance<CatalogEntry.Track>()
                        .filter { it.isPlayable }
                        .map { track ->
                            PlaybackRequestTrack(
                                providerId = track.id.providerId,
                                id = track.id.value,
                                title = track.title,
                                artists = track.artists.map { it.name },
                                durationMs = track.durationMillis,
                                artworkUrl = track.artworkUrls.firstOrNull(),
                            )
                        }
                }
            }
            LaunchedEffect(listState, pageState.continuationToken, pageState.isLoading, pageState.isLoadingMore, pageState.continuationError) {
                val isCurrentPage = pageState.currentEntry?.id == state.currentEntry?.id && pageState.pageTitle == state.pageTitle
                if (!isCurrentPage || pageState.continuationToken == null || pageState.isLoading || pageState.isLoadingMore || pageState.continuationError != null) {
                    return@LaunchedEffect
                }
                snapshotFlow {
                    val layout = listState.layoutInfo
                    layout.visibleItemsInfo.lastOrNull()?.index to layout.totalItemsCount
                }.collect { (lastVisibleIndex, itemCount) ->
                    if (itemCount > 0 && lastVisibleIndex != null && lastVisibleIndex >= itemCount - 5) {
                        catalog.loadMore()
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = if (playback?.track != null) 88.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IosText(pageState.pageTitle, style = IosTheme.typography.title, color = IosTheme.colors.label)
                if (pageState.canGoBack) {
                    IosText("Back", color = IosTheme.colors.secondaryLabel, modifier = Modifier.clickable(onClick = catalog::goBack))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BrowserDestination("Home", pageState.pageTitle == "Listen Now" && !pageState.canGoBack) { catalog.loadHome() }
                BrowserDestination("Library", pageState.pageTitle == "Recent activity" && !pageState.canGoBack) {
                    catalog.loadLibrary(CatalogLibraryCollection.RECENT_ACTIVITY)
                }
                BrowserDestination("Playlists", pageState.pageTitle == "Playlists" && !pageState.canGoBack) {
                    catalog.loadLibrary(CatalogLibraryCollection.PLAYLISTS)
                }
                BrowserDestination("Songs", pageState.pageTitle == "Liked songs" && !pageState.canGoBack) {
                    catalog.loadLibrary(CatalogLibraryCollection.LIKED_TRACKS)
                }
            }
            if (!pageState.canGoBack) {
                BasicTextField(
                    value = pageState.searchQuery,
                    onValueChange = catalog::search,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    decorationBox = { field ->
                        if (pageState.searchQuery.isEmpty()) {
                            IosText("Search songs, artists, albums", color = IosTheme.colors.secondaryLabel)
                        }
                        field()
                    },
                )
            }
            pageState.currentEntry?.takeIf { it.artworkUrls.isNotEmpty() }?.let { entry ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RemoteArtwork(
                        entry.artworkUrls.firstOrNull(),
                        Modifier.sharedCatalogArtwork(entry.id)
                            .size(104.dp)
                            .clip(RoundedCornerShape(18.dp)),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        IosText(
                            entry.title,
                            style = IosTheme.typography.title2,
                            color = IosTheme.colors.label,
                            modifier = Modifier.sharedCatalogTitle(entry.id),
                        )
                        val subtitle = when (entry) {
                            is CatalogEntry.Album -> entry.artists.joinToString { it.name }
                            is CatalogEntry.Artist -> entry.subtitle.orEmpty()
                            is CatalogEntry.Playlist -> entry.description.orEmpty()
                            is CatalogEntry.Browse -> ""
                            is CatalogEntry.Track -> entry.artists.joinToString { it.name }
                        }
                        if (subtitle.isNotBlank()) IosText(subtitle, color = IosTheme.colors.secondaryLabel)
                    }
                }
            }
            when {
                pageState.isLoading -> IosText("Loading music…", color = IosTheme.colors.secondaryLabel)
                pageState.message != null -> IosText(pageState.message.orEmpty(), color = IosTheme.colors.secondaryLabel)
                pageState.sections.isEmpty() -> IosText("No music to show yet.", color = IosTheme.colors.secondaryLabel)
            }
            libraryActionState.message?.let { message ->
                IosText(message, color = IosTheme.colors.secondaryLabel)
            }
            playlistActionState.message?.let { message ->
                IosText(message, color = IosTheme.colors.secondaryLabel)
            }
            LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                pageState.sections.forEach { section ->
                    item(key = section.id) {
                        IosText(section.title, style = IosTheme.typography.title2, color = IosTheme.colors.label)
                    }
                    items(section.entries, key = { "${section.id}:${it.id.value}" }) { entry ->
                        CatalogEntryRow(
                            entry = entry,
                            isSignedIn = isSignedIn,
                            isSaved = (entry as? CatalogEntry.Track)?.let(libraryActions::isSaved) ?: false,
                            isLibraryActionPending = (entry as? CatalogEntry.Track)?.let(libraryActions::isPending) ?: false,
                            onSignInRequested = onSignInRequested,
                            onLibraryAction = { track, isSaved -> libraryActions.setSaved(track, isSaved) },
                            onAddToPlaylist = { track ->
                                if (isSignedIn) playlistActions.openPicker(track) else onSignInRequested()
                            },
                        ) {
                            if (entry is CatalogEntry.Track) {
                                if (entry.isPlayable) {
                                    val queueIndex = queue.indexOfFirst { it.id == entry.id.value }
                                    if (queueIndex >= 0) playbackController?.playQueue(queue, queueIndex)
                                }
                            } else {
                                catalog.openEntry(entry)
                            }
                        }
                    }
                }
                when {
                    pageState.isLoadingMore -> item(key = "catalog-loading-more") {
                        IosText("Loading more...", color = IosTheme.colors.secondaryLabel)
                    }
                    pageState.continuationError != null -> item(key = "catalog-load-more-error") {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IosText(pageState.continuationError.orEmpty(), color = IosTheme.colors.secondaryLabel)
                            IosText("Retry", color = IosTheme.colors.label, modifier = Modifier.clickable {
                                catalog.loadMore()
                            })
                        }
                    }
                }
            }
        }
        if (showQueue && playbackController != null) {
            PlaybackQueueDialog(
                queue = queueSnapshot,
                controller = playbackController,
                onDismiss = { showQueue = false },
            )
        }
        if (playlistActionState.isPickerVisible) {
            PlaylistPickerDialog(
                state = playlistActionState,
                onDismiss = playlistActions::dismissPicker,
                onSelectPlaylist = playlistActions::addToPlaylist,
                onCreatePlaylist = playlistActions::createPlaylistAndAdd,
                onLoadMore = playlistActions::loadMorePlaylists,
                onRetry = playlistActions::retryPlaylists,
            )
        }
        }

        val currentPlayback = playback
        val currentTrack = currentPlayback?.track
        if (currentPlayback != null && currentTrack != null) {
            PlaybackPlayerMorph(
                trackId = currentTrack.id,
                expanded = showFullPlayer,
                onExpand = { showFullPlayer = true },
                onCollapse = { showFullPlayer = false },
                modifier = Modifier.fillMaxSize(),
                compactContent = { sharedElements, onExpand ->
                    PlaybackMiniPlayer(
                        playback = currentPlayback,
                        sharedElements = sharedElements,
                        backdrop = playerBackdrop,
                        onExpand = onExpand,
                        onOpenQueue = { showQueue = true },
                        onTogglePlayPause = playbackController::togglePlayPause,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                },
                expandedContent = { sharedElements, onCollapse ->
                    PlaybackFullPlayer(
                        playback = currentPlayback,
                        controller = playbackController,
                        sharedElements = sharedElements,
                        darkTheme = true,
                        sleepTimerActive = isSleepTimerActive,
                        crossfadeEnabled = playbackSettings.transitionMode == PlaybackTransitionMode.CROSSFADE,
                        crossfadeAvailable = crossfadeAvailable,
                        crossfadeLocked = playbackSettings.bitPerfectEnabled,
                        equalizerAvailable = equalizerAvailable,
                        equalizerEnabled = playbackSettings.equalizerEnabled,
                        equalizerLocked = playbackSettings.bitPerfectEnabled,
                        playbackSettingsError = playbackSettingsError,
                        onToggleCrossfade = {
                            playbackSettingsStateHolder.setCrossfadeEnabled(
                                playbackSettings.transitionMode != PlaybackTransitionMode.CROSSFADE,
                            )
                        },
                        onOpenEqualizer = { showEqualizer = true },
                        onCollapse = onCollapse,
                        onOpenQueue = { showQueue = true },
                        onOpenSleepTimer = onSleepTimerRequested,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }
        if (showEqualizer && equalizerAvailable) {
            PlaybackEqualizerSheet(
                settings = playbackSettings,
                darkTheme = true,
                errorMessage = playbackSettingsError,
                onDismissRequest = { showEqualizer = false },
                onEnabledChange = playbackSettingsStateHolder::setEqualizerEnabled,
                onPresetSelected = playbackSettingsStateHolder::setEqualizerPreset,
                onBandGainChange = playbackSettingsStateHolder::setEqualizerBandGain,
            )
        }
        }
    }
}

@Composable
private fun PlaybackQueueDialog(
    queue: PlaybackQueueSnapshot,
    controller: PlaybackQueueController,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(IosTheme.colors.secondarySystemBackground)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IosText("Up Next", style = IosTheme.typography.title2, color = IosTheme.colors.label)
                IosText("Done", color = IosTheme.colors.accent, modifier = Modifier.clickable(onClick = onDismiss))
            }
            if (queue.entries.isEmpty()) {
                IosText("Your queue is empty.", color = IosTheme.colors.secondaryLabel)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 480.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(queue.entries, key = { index, entry -> "${entry.id}:$index" }) { index, entry ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (index == queue.currentIndex) IosTheme.colors.tertiarySystemFill else IosTheme.colors.secondarySystemBackground)
                                .clickable { controller.playQueueItem(index) }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            IosText(
                                entry.title,
                                color = if (index == queue.currentIndex) IosTheme.colors.accent else IosTheme.colors.label,
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                IosText(entry.artists.joinToString(), color = IosTheme.colors.secondaryLabel)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    IosText(
                                        "↑",
                                        color = IosTheme.colors.label,
                                        modifier = Modifier.clickable(enabled = index > 0) {
                                            controller.moveQueueItem(index, index - 1)
                                        },
                                    )
                                    IosText(
                                        "↓",
                                        color = IosTheme.colors.label,
                                        modifier = Modifier.clickable(enabled = index < queue.entries.lastIndex) {
                                            controller.moveQueueItem(index, index + 1)
                                        },
                                    )
                                    IosText(
                                        "Remove",
                                        color = IosTheme.colors.secondaryLabel,
                                        modifier = Modifier.clickable { controller.removeQueueItem(index) },
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
private fun PlaylistPickerDialog(
    state: CatalogPlaylistActionState,
    onDismiss: () -> Unit,
    onSelectPlaylist: (CatalogEntry.Playlist) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
) {
    var newPlaylistName by remember(state.selectedTrack?.id) { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(IosTheme.colors.secondarySystemBackground)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IosText("Add to playlist", style = IosTheme.typography.title2, color = IosTheme.colors.label)
                IosText("Cancel", color = IosTheme.colors.accent, modifier = Modifier.clickable(onClick = onDismiss))
            }
            state.selectedTrack?.let { track ->
                IosText(track.title, color = IosTheme.colors.secondaryLabel)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosTheme.colors.tertiarySystemFill)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                BasicTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    singleLine = true,
                    enabled = !state.isCreatingPlaylist,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { field ->
                        if (newPlaylistName.isEmpty()) {
                            IosText("New playlist name", color = IosTheme.colors.secondaryLabel)
                        }
                        field()
                    },
                )
            }
            IosText(
                text = if (state.isCreatingPlaylist) "Creating and adding…" else "Create and add",
                color = IosTheme.colors.accent,
                modifier = Modifier.clickable(
                    enabled = !state.isCreatingPlaylist,
                    onClick = { onCreatePlaylist(newPlaylistName) },
                ),
            )
            when {
                state.isLoadingPlaylists -> IosText("Loading playlists…", color = IosTheme.colors.secondaryLabel)
                state.playlistError != null && state.playlists.isEmpty() -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        IosText(state.playlistError, color = IosTheme.colors.secondaryLabel)
                        IosText(
                            "Retry",
                            color = IosTheme.colors.accent,
                            modifier = Modifier.clickable(onClick = onRetry),
                        )
                    }
                }
                state.playlists.isEmpty() -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        IosText(
                            state.playlistError ?: "No editable playlists on this page.",
                            color = IosTheme.colors.secondaryLabel,
                        )
                        when {
                            state.continuationToken != null -> IosText(
                                "Load more playlists",
                                color = IosTheme.colors.accent,
                                modifier = Modifier.clickable(onClick = onLoadMore),
                            )
                            state.playlistError != null -> IosText(
                                "Retry",
                                color = IosTheme.colors.accent,
                                modifier = Modifier.clickable(onClick = onRetry),
                            )
                        }
                    }
                }
                else -> {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 480.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(state.playlists, key = { it.id.value }) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = state.pendingPlaylistIds.isEmpty() && !state.isCreatingPlaylist) {
                                    onSelectPlaylist(playlist)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IosText(playlist.title, color = IosTheme.colors.label)
                            if (playlist.id in state.pendingPlaylistIds) {
                                IosText("Adding…", color = IosTheme.colors.secondaryLabel)
                            }
                        }
                    }
                    if (state.continuationToken != null || state.isLoadingMorePlaylists) {
                        item(key = "playlist-picker-load-more") {
                            IosText(
                                if (state.isLoadingMorePlaylists) "Loading more…" else "Load more playlists",
                                color = IosTheme.colors.accent,
                                modifier = Modifier.clickable(
                                    enabled = !state.isLoadingMorePlaylists,
                                    onClick = onLoadMore,
                                ).padding(12.dp),
                            )
                        }
                    }
                }
                state.playlistError?.let { error ->
                    IosText(error, color = IosTheme.colors.secondaryLabel)
                }
                }
            }
            state.message?.let { message ->
                IosText(message, color = IosTheme.colors.secondaryLabel)
            }
        }
    }
}

@Composable
private fun BrowserDestination(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    IosText(
        text = title,
        color = if (selected) IosTheme.colors.label else IosTheme.colors.secondaryLabel,
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun CatalogEntryRow(
    entry: CatalogEntry,
    isSignedIn: Boolean,
    isSaved: Boolean,
    isLibraryActionPending: Boolean,
    onSignInRequested: () -> Unit,
    onLibraryAction: (CatalogEntry.Track, Boolean) -> Unit,
    onAddToPlaylist: (CatalogEntry.Track) -> Unit,
    onClick: (CatalogEntry) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .sharedCatalogArtwork(entry.id)
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(IosTheme.colors.tertiarySystemFill)
                .clickable { onClick(entry) },
        ) {
            RemoteArtwork(entry.artworkUrls.firstOrNull(), Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier.weight(1f).clickable { onClick(entry) },
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            IosText(
                entry.title,
                color = IosTheme.colors.label,
                modifier = Modifier.sharedCatalogTitle(entry.id),
            )
            val detail = when (entry) {
                is CatalogEntry.Track -> entry.artists.joinToString { it.name }
                is CatalogEntry.Album -> entry.artists.joinToString { it.name }
                is CatalogEntry.Artist -> entry.subtitle.orEmpty()
                is CatalogEntry.Playlist -> entry.description.orEmpty()
                is CatalogEntry.Browse -> ""
            }
            if (detail.isNotBlank()) IosText(detail, color = IosTheme.colors.secondaryLabel)
        }
        (entry as? CatalogEntry.Track)
            ?.takeIf { it.id.providerId == YOUTUBE_MUSIC_PROVIDER }
            ?.let { track ->
            IosText(
                text = when {
                    isLibraryActionPending -> "Updating…"
                    !isSignedIn -> "Sign in"
                    isSaved -> "Remove"
                    else -> "Save"
                },
                color = IosTheme.colors.accent,
                modifier = Modifier.clickable(enabled = !isLibraryActionPending) {
                    if (isSignedIn) onLibraryAction(track, !isSaved) else onSignInRequested()
                },
            )
            IosText(
                text = "Add",
                color = IosTheme.colors.accent,
                modifier = Modifier.clickable {
                    if (isSignedIn) onAddToPlaylist(track) else onSignInRequested()
                },
            )
            }
    }
}
