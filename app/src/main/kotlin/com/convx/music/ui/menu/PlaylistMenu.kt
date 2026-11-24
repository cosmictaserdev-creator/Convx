/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
import com.convx.music.utils.reportException
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.Alignment
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.utils.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.music.utils.rememberPreference
import com.music.innertube.utils.parseCookieString
import androidx.media3.exoplayer.offline.Download
import com.music.innertube.YouTube
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.SpeedDialItem
import com.convx.music.library.ObservePlaylist
import com.convx.music.library.ObservePlaylists
import com.convx.music.library.LocalPlaylistSongDataSource
import com.convx.music.library.LocalPlaylistSong
import com.convx.music.library.PlaylistCommands
import com.convx.music.library.PendingPlaylistDeletionRepository
import com.convx.music.library.PlaylistDetail
import com.convx.music.library.PlaylistOrder
import com.convx.music.library.toPlaylistDetail
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.combinedDownloadState
import com.convx.music.playback.cancelDownloads
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.removeDownloads
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.rememberPlaylistCoverPicker
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.TextFieldDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistMenu(
    playlist: Playlist,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    autoPlaylist: Boolean? = false,
    downloadPlaylist: Boolean? = false,
    songList: List<Song>? = emptyList(),
) {
    PlaylistMenu(
        playlist = playlist.toPlaylistDetail(),
        coroutineScope = coroutineScope,
        onDismiss = onDismiss,
        autoPlaylist = autoPlaylist,
        downloadPlaylist = downloadPlaylist,
        songList = songList,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistMenu(
    playlist: PlaylistDetail,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    autoPlaylist: Boolean? = false,
    downloadPlaylist: Boolean? = false,
    songList: List<Song>? = emptyList(),
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val observePlaylist = koinInject<ObservePlaylist>()
    val observePlaylists = koinInject<ObservePlaylists>()
    val playlistCommands = koinInject<PlaylistCommands>()
    val pendingPlaylistDeletionRepository = koinInject<PendingPlaylistDeletionRepository>()
    val localPlaylistSongDataSource = koinInject<LocalPlaylistSongDataSource>()
    val downloadUtil = LocalDownloadUtil.current.get()
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val observedPlaylist by observePlaylist(playlist.id).collectAsState(initial = playlist)
    val currentPlaylist = observedPlaylist ?: playlist
    var songs by remember {
        mutableStateOf(emptyList<Song>())
    }

    val (innerTubeCookie) = rememberPreference(InnerTubeCookieKey, "")
    val isSignedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    var isSyncing by remember { mutableStateOf(false) }
    var syncedCount by remember { mutableIntStateOf(0) }
    var isSyncComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (autoPlaylist == false) {
            localPlaylistSongDataSource.observeSongs(playlist.id).collect {
                songs = it.map(LocalPlaylistSong::song)
            }
        } else {
            if (songList != null) {
                songs = songList
            }
        }
    }

    val downloads by downloadUtil.downloads.collectAsStateWithLifecycle()
    val downloadState = remember(songs, downloads) {
        combinedDownloadState(songs, downloads) { it.id }
    }

    val editable = currentPlaylist.isEditable

    val launchCoverPicker = rememberPlaylistCoverPicker(
        playlistId = playlist.id,
        browseId = currentPlaylist.browseId,
        isEditable = currentPlaylist.isEditable,
        onCoverSaved = { newUrl ->
            if (autoPlaylist == true) {
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[stringPreferencesKey("thumbnail_${playlist.id}")] = newUrl
                    }
                }
            }
        }
    )

    val isPinned by database.speedDialDao.isPinned(playlist.id).collectAsState(initial = false)

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        TextFieldDialog(
            icon = { SymbolIcon(symbol = sf("pencil"), contentDescription = null) },
            title = { Text(text = stringResource(R.string.edit_playlist)) },
            onDismiss = { showEditDialog = false },
            initialTextFieldValue =
            TextFieldValue(
                currentPlaylist.name,
                TextRange(currentPlaylist.name.length),
            ),
            onDone = { name ->
                onDismiss()
                coroutineScope.launch(Dispatchers.IO) {
                    if (playlistCommands.updatePlaylist(playlist.id, name, currentPlaylist.description)) {
                        currentPlaylist.browseId?.let { YouTube.renamePlaylist(it, name) }
                    }
                }
            },
        )
    }

    var showMergeDialog by remember { mutableStateOf(false) }
    if (showMergeDialog) {
        val targets by observePlaylists(PlaylistOrder.NAME, false)
            .collectAsState(initial = emptyList())
        com.convx.music.ui.component.ListDialog(onDismiss = { showMergeDialog = false }) {
            item { Text(text = stringResource(R.string.merge_playlist_into), modifier = Modifier.padding(16.dp)) }
            targets.filter { it.id != playlist.id && it.isEditable }.forEach { target ->
                item(key = target.id) {
                    Text(
                        text = target.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMergeDialog = false
                                onDismiss()
                                // This playlist's songs are appended to the target; this one is kept.
                                coroutineScope.launch(Dispatchers.IO) {
                                    playlistCommands.mergePlaylist(playlist.id, target.id)
                                }
                            }
                            .padding(16.dp),
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
                        currentPlaylist.name
                    ),
                    style = AppTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
            },
            buttons = {
                TextButton(
                    onClick = {
                        showRemoveDownloadDialog = false
                    },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                TextButton(
                    onClick = {
                        showRemoveDownloadDialog = false
                        removeDownloads(context, songs.map { it.id })
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    var showDeletePlaylistDialog by remember {
        mutableStateOf(false)
    }

    if (showDeletePlaylistDialog) {
        DefaultDialog(
            onDismiss = { showDeletePlaylistDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.delete_playlist_confirm, currentPlaylist.name),
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
                        onDismiss()
                        coroutineScope.launch(Dispatchers.IO) {
                            val beforeDelete = observePlaylist(playlist.id).first() ?: currentPlaylist
                            if (!playlistCommands.deletePlaylist(playlist.id)) return@launch
                            val browseId = beforeDelete.browseId
                            if (browseId == null) return@launch
                            if (beforeDelete.isBookmarked) YouTube.likePlaylist(browseId, false)
                            // Marked pending before the network call: if the delete
                            // fails, or a library sync races it while it's still in
                            // flight, the sync sees this browseId as pending and
                            // won't resurrect the playlist it was just told to remove.
                            pendingPlaylistDeletionRepository.recordPendingDelete(browseId)
                            YouTube.deletePlaylist(browseId)
                        }
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        )
    }

    PlaylistListItem(
        playlist = currentPlaylist,
        trailingContent = {
            if (!currentPlaylist.isEditable) {
                IconButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            playlistCommands.toggleBookmark(playlist.id)?.let { isBookmarked ->
                                currentPlaylist.browseId?.let { YouTube.likePlaylist(it, isBookmarked) }
                            }
                        }
                    }
                ) {
                    SymbolIcon(symbol = if (currentPlaylist.isBookmarked) sf("heart.fill") else sf("heart"), contentDescription = null, tint = if (currentPlaylist.isBookmarked) AppTheme.colorScheme.error else LocalContentColor.current)
                }
            }
        },
    )

    HorizontalDivider()

    Spacer(modifier = Modifier.height(12.dp))

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    LazyColumn(
        contentPadding = PaddingValues(
            start = 0.dp,
            top = 0.dp,
            end = 0.dp,
            bottom = 8.dp + menuBottomInset(),
        ),
    ) {
        item {
            NewActionGrid(
                actions = listOfNotNull(
                    if (!isGuest) {
                        NewAction(
                            icon = {
                                SymbolIcon(symbol = sf("play.fill"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                            },
                            text = stringResource(R.string.play),
                            onClick = {
                                onDismiss()
                                if (songs.isNotEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = currentPlaylist.name,
                                            items = songs.map(Song::toMediaItem)
                                        )
                                    )
                                }
                            }
                        )
                        NewAction(
                            icon = {
                                SymbolIcon(symbol = sf("shuffle"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                            },
                            text = stringResource(R.string.shuffle),
                            onClick = {
                                onDismiss()
                                if (songs.isNotEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = currentPlaylist.name,
                                            items = songs.shuffled().map(Song::toMediaItem)
                                        )
                                    )
                                }
                            }
                        )
                    } else null,
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.share),
                        onClick = {
                            onDismiss()
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, currentPlaylist.shareLink)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }
                    )
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                columns = if (isGuest) 1 else 3
            )
        }

        item {
            IosMenuGroup(
                items = buildList {
                    if (!isGuest) {
                        currentPlaylist.browseId?.let { browseId ->
                            add(
                                IosMenuItemData(
                                    title = { Text(text = stringResource(R.string.start_radio)) },
                                    description = { Text(text = stringResource(R.string.start_radio_desc)) },
                                    icon = {
                                        SymbolIcon(symbol = sf("dot.radiowaves.left.and.right"), contentDescription = null)
                                    },
                                    onClick = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            YouTube.playlist(browseId).getOrNull()?.playlist?.let { playlistItem ->
                                                playlistItem.radioEndpoint?.let { radioEndpoint ->
                                                    withContext(Dispatchers.Main) {
                                                        playerConnection.playQueue(YouTubeQueue(radioEndpoint))
                                                    }
                                                }
                                            }
                                        }
                                        onDismiss()
                                    }
                                )
                            )
                        }
                    }
                    if (!isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.play_next)) },
                                description = { Text(text = stringResource(R.string.play_next_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("text.line.first.and.arrowtriangle.forward"), contentDescription = null)
                                },
                                onClick = {
                                    coroutineScope.launch {
                                        playerConnection.playNext(songs.map { it.toMediaItem() })
                                    }
                                    onDismiss()
                                }
                            )
                        )
                    }
                    if (!isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.add_to_queue)) },
                                description = { Text(text = stringResource(R.string.add_to_queue_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                                },
                                onClick = {
                                    onDismiss()
                                    playerConnection.addToQueue(songs.map { it.toMediaItem() })
                                }
                            )
                        )
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            IosMenuGroup(
                items = buildList {
                    if (editable && autoPlaylist != true && !isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.edit)) },
                                description = { Text(text = stringResource(R.string.edit_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("pencil"), contentDescription = null)
                                },
                                onClick = {
                                    showEditDialog = true
                                }
                            )
                        )
                    }
                    if (autoPlaylist != true && !isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.merge_playlist)) },
                                icon = { SymbolIcon(symbol = sf("rectangle.stack"), contentDescription = null) },
                                onClick = { showMergeDialog = true }
                            )
                        )
                    }
                    // Custom cover works for any real playlist (local, or online via a local
                    // override), so it isn't gated by `editable` like rename is.
                    if (!isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.edit_playlist_cover)) },
                                icon = {
                                    SymbolIcon(symbol = sf("photo"), contentDescription = null)
                                },
                                // Don't dismiss: the pick/crop launchers live in this menu's
                                // composition and must survive until the crop result returns.
                                onClick = { launchCoverPicker() }
                            )
                        )
                        
                        if (autoPlaylist == true) {
                            val thumbnailKey = remember(playlist.id) { stringPreferencesKey("thumbnail_${playlist.id}") }
                            val currentThumbnail by remember(thumbnailKey) { context.dataStore.data.map { it[thumbnailKey] } }.collectAsState(initial = null)
                            
                            if (!currentThumbnail.isNullOrBlank()) {
                                add(
                                    IosMenuItemData(
                                        title = { Text(text = stringResource(R.string.remove_custom_image)) },
                                        icon = {
                                            SymbolIcon(symbol = sf("xmark"), contentDescription = null)
                                        },
                                        onClick = {
                                            coroutineScope.launch {
                                                context.dataStore.edit { settings ->
                                                    settings.remove(thumbnailKey)
                                                }
                                            }
                                            onDismiss()
                                        }
                                    )
                                )
                            }
                        }
                    }
                    add(
                        IosMenuItemData(
                            title = { 
                                Text(
                                    text = if (isPinned) "Unpin from Speed dial" else "Pin to Speed dial" 
                                ) 
                            },
                            icon = {
                                SymbolIcon(symbol = if (isPinned) sf("minus") else sf("plus"), contentDescription = null)
                            },
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    if (isPinned) {
                                        database.speedDialDao.delete(playlist.id)
                                    } else {
                                        database.speedDialDao.insert(
                                            SpeedDialItem(
                                                id = playlist.id,
                                                title = currentPlaylist.name,
                                                subtitle = null,
                                                thumbnailUrl = currentPlaylist.artworkUrls.firstOrNull(),
                                                type = "PLAYLIST"
                                            )
                                        )
                                    }
                                }
                                onDismiss()
                            }
                        )
                    )
                    if (autoPlaylist != true) {
                        val isPinnedToLibrary = currentPlaylist.pinnedAtEpochMillis != null
                        add(
                            IosMenuItemData(
                                title = {
                                    Text(
                                        text = if (isPinnedToLibrary) stringResource(R.string.unpin_from_library) else stringResource(R.string.pin_to_library)
                                    )
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(if (isPinnedToLibrary) R.drawable.remove else R.drawable.ic_push_pin),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        playlistCommands.togglePin(playlist.id)
                                    }
                                    onDismiss()
                                }
                            )
                        )
                    }
                    if (downloadPlaylist != true) {
                        add(
                            when (downloadState) {
                                Download.STATE_COMPLETED -> {
                                    IosMenuItemData(
                                        title = {
                                            Text(
                                                text = stringResource(R.string.remove_download)
                                            )
                                        },
                                        icon = {
                                            SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
                                        },
                                        onClick = {
                                            showRemoveDownloadDialog = true
                                        }
                                    )
                                }
                                Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                                    IosMenuItemData(
                                        title = { Text(text = stringResource(R.string.downloading)) },
                                        icon = {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        },
                                        onClick = {
                                            cancelDownloads(context, songs.map { it.id }, downloads)
                                        }
                                    )
                                }
                                else -> {
                                    IosMenuItemData(
                                        title = { Text(text = stringResource(R.string.action_download)) },
                                        description = { Text(text = stringResource(R.string.download_desc)) },
                                        icon = {
                                            SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
                                        },
                                        onClick = {
                                            downloadSongs(
                                                context,
                                                songs.map { DownloadTarget(it.id, it.song.title, it.artists.firstOrNull()?.name, it.song.duration) },
                                                downloads,
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                    if (autoPlaylist != true && !isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.delete)) },
                                description = { Text(text = stringResource(R.string.delete_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("trash"), contentDescription = null)
                                },
                                onClick = {
                                    showDeletePlaylistDialog = true
                                }
                            )
                        )
                    }
                    val currentBrowseId = currentPlaylist.browseId
                    if (currentBrowseId == null && isSignedIn && !isGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.sync_playlist)) },
                                description = { Text(text = stringResource(R.string.sync_playlist_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null)
                                },
                                onClick = {
                                    isSyncing = true
                                    syncedCount = 0
                                    isSyncComplete = false
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            val browseId = createPlaylistWithRetry(currentPlaylist.name)
                                            playlistCommands.linkPlaylistToRemote(playlist.id, browseId)
                                            var successCount = 0
                                            songs.forEachIndexed { index, song ->
                                                val result = YouTube.addToPlaylist(browseId, song.id)
                                                if (result.isSuccess) {
                                                    successCount++
                                                } else {
                                                    result.exceptionOrNull()?.let(::reportException)
                                                }
                                                withContext(Dispatchers.Main) {
                                                    syncedCount = index + 1
                                                }
                                                kotlinx.coroutines.delay(200)
                                            }
                                            kotlinx.coroutines.withContext(Dispatchers.Main) {
                                                if (successCount == songs.size) {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.sync_completed_success, songs.size),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.sync_completed_partial, successCount, songs.size),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                isSyncComplete = true
                                            }
                                        } catch (e: Exception) {
                                            kotlinx.coroutines.withContext(Dispatchers.Main) {
                                                Toast.makeText(context, e.message ?: "Failed to sync", Toast.LENGTH_SHORT).show()
                                                isSyncing = false
                                            }
                                        }
                                    }
                                }
                            )
                        )
                    }
                    currentPlaylist.shareLink?.let { shareLink ->
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.share)) },
                                description = { Text(text = stringResource(R.string.share_desc)) },
                                icon = {
                                    SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null)
                                },
                                onClick = {
                                    val intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareLink)
                                    }
                                    context.startActivity(Intent.createChooser(intent, null))
                                    onDismiss()
                                }
                            )
                        )
                    }
                }
            )
        }
    }

    if (isSyncing) {
        DefaultDialog(
            onDismiss = {
                if (isSyncComplete) {
                    isSyncing = false
                    isSyncComplete = false
                    onDismiss()
                }
            },
            buttons = {
                if (isSyncComplete) {
                    TextButton(
                        onClick = {
                            isSyncing = false
                            isSyncComplete = false
                            onDismiss()
                        }
                    ) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                if (isSyncComplete) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        CircularWavyProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(80.dp)
                        )
                        SymbolIcon(symbol = sf("checkmark"), contentDescription = null, size = 36.dp, tint = AppTheme.colorScheme.primary)
                    }
                    Text(
                        text = stringResource(R.string.playlist_uploaded),
                        style = AppTheme.typography.bodyLarge
                    )
                } else {
                    val progressValue = if (songs.isEmpty()) 0f else syncedCount.toFloat() / songs.size
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        CircularWavyProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "${(progressValue * 100).toInt()}%",
                            style = AppTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = if (songs.isEmpty()) {
                            stringResource(R.string.please_wait)
                        } else {
                            stringResource(R.string.syncing_progress, syncedCount, songs.size)
                        },
                        style = AppTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

private suspend fun createPlaylistWithRetry(name: String): String {
    var attempts = 0
    while (true) {
        try {
            return YouTube.createPlaylist(name)
        } catch (e: Exception) {
            attempts++
            if (attempts >= 3) throw e
            kotlinx.coroutines.delay(1000)
        }
    }
}
