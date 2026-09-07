/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.menu

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.Alignment
import com.convxy.music.constants.InnerTubeCookieKey
import com.convxy.music.constants.PendingPlaylistDeletesKey
import com.convxy.music.utils.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convxy.music.utils.rememberPreference
import com.music.innertube.utils.parseCookieString
import androidx.media3.exoplayer.offline.Download
import com.music.innertube.YouTube
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.SpeedDialItem
import com.convxy.music.db.entities.PlaylistSong
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.cancelDownloads
import com.convxy.music.playback.downloadSongs
import com.convxy.music.playback.removeDownloads
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.ui.component.DefaultDialog
import com.convxy.music.ui.component.Material3MenuGroup
import com.convxy.music.ui.component.Material3MenuItemData
import com.convxy.music.ui.component.rememberPlaylistCoverPicker
import com.convxy.music.ui.component.NewAction
import com.convxy.music.ui.component.NewActionGrid
import com.convxy.music.ui.component.PlaylistListItem
import com.convxy.music.ui.component.TextFieldDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

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
    val context = LocalContext.current
    val database = LocalDatabase.current
    val downloadUtil = LocalDownloadUtil.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val dbPlaylist by database.playlist(playlist.id).collectAsState(initial = playlist)
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
            database.playlistSongs(playlist.id).collect {
                songs = it.map(PlaylistSong::song)
            }
        } else {
            if (songList != null) {
                songs = songList
            }
        }
    }

    // Collected rather than folded into a LaunchedEffect so the current state of each
    // download is also available at click time — the download and cancel actions filter
    // on it, see DownloadActions.
    val downloads by downloadUtil.downloads.collectAsState()
    val downloadState = remember(songs, downloads) {
        when {
            songs.isEmpty() -> Download.STATE_STOPPED
            songs.all { downloads[it.id]?.state == Download.STATE_COMPLETED } ->
                Download.STATE_COMPLETED

            songs.all {
                downloads[it.id]?.state == Download.STATE_QUEUED ||
                    downloads[it.id]?.state == Download.STATE_DOWNLOADING ||
                    downloads[it.id]?.state == Download.STATE_COMPLETED
            } -> Download.STATE_DOWNLOADING

            else -> Download.STATE_STOPPED
        }
    }

    val editable: Boolean = playlist.playlist.isEditable == true

    val launchCoverPicker = rememberPlaylistCoverPicker(
        playlist = playlist,
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
            icon = { Icon(painter = painterResource(R.drawable.edit), contentDescription = null) },
            title = { Text(text = stringResource(R.string.edit_playlist)) },
            onDismiss = { showEditDialog = false },
            initialTextFieldValue =
            TextFieldValue(
                playlist.playlist.name,
                TextRange(playlist.playlist.name.length),
            ),
            onDone = { name ->
                onDismiss()
                database.query {
                    update(
                        playlist.playlist.copy(
                            name = name,
                            lastUpdateTime = LocalDateTime.now()
                        )
                    )
                }
                coroutineScope.launch(Dispatchers.IO) {
                    playlist.playlist.browseId?.let { YouTube.renamePlaylist(it, name) }
                }
            },
        )
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
                        playlist.playlist.name
                    ),
                    style = MaterialTheme.typography.bodyLarge,
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
                    text = stringResource(R.string.delete_playlist_confirm, playlist.playlist.name),
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
                        onDismiss()
                        database.transaction {
                            // First toggle the like using the same logic as the like button
                            if (playlist.playlist.bookmarkedAt != null) {
                                // Using the same toggleLike() method that's used in the like button
                                update(playlist.playlist.toggleLike())
                            }
                            // Then delete the playlist
                            delete(playlist.playlist)
                        }

                        val browseId = playlist.playlist.browseId
                        coroutineScope.launch(Dispatchers.IO) {
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
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        )
    }

    PlaylistListItem(
        playlist = playlist,
        trailingContent = {
            if (playlist.playlist.isEditable != true) {
                IconButton(
                    onClick = {
                        database.query {
                            dbPlaylist?.playlist?.toggleLike()?.let { update(it) }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(if (dbPlaylist?.playlist?.bookmarkedAt != null) R.drawable.favorite else R.drawable.favorite_border),
                        tint = if (dbPlaylist?.playlist?.bookmarkedAt != null) MaterialTheme.colorScheme.error else LocalContentColor.current,
                        contentDescription = null
                    )
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
            bottom = 8.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
        ),
    ) {
        item {
            NewActionGrid(
                actions = listOfNotNull(
                    if (!isGuest) {
                        NewAction(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.play),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            text = stringResource(R.string.play),
                            onClick = {
                                onDismiss()
                                if (songs.isNotEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = playlist.playlist.name,
                                            items = songs.map(Song::toMediaItem)
                                        )
                                    )
                                }
                            }
                        )
                        NewAction(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.shuffle),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            text = stringResource(R.string.shuffle),
                            onClick = {
                                onDismiss()
                                if (songs.isNotEmpty()) {
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = playlist.playlist.name,
                                            items = songs.shuffled().map(Song::toMediaItem)
                                        )
                                    )
                                }
                            }
                        )
                    } else null,
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.share),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        text = stringResource(R.string.share),
                        onClick = {
                            onDismiss()
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/playlist?list=${dbPlaylist?.playlist?.browseId}")
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
            Material3MenuGroup(
                items = buildList {
                    if (!isGuest) {
                        playlist.playlist.browseId?.let { browseId ->
                            add(
                                Material3MenuItemData(
                                    title = { Text(text = stringResource(R.string.start_radio)) },
                                    description = { Text(text = stringResource(R.string.start_radio_desc)) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.radio),
                                            contentDescription = null,
                                        )
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
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.play_next)) },
                                description = { Text(text = stringResource(R.string.play_next_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.playlist_play),
                                        contentDescription = null,
                                    )
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
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.add_to_queue)) },
                                description = { Text(text = stringResource(R.string.add_to_queue_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.queue_music),
                                        contentDescription = null,
                                    )
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
            Material3MenuGroup(
                items = buildList {
                    if (editable && autoPlaylist != true && !isGuest) {
                        add(
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.edit)) },
                                description = { Text(text = stringResource(R.string.edit_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.edit),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    showEditDialog = true
                                }
                            )
                        )
                    }
                    // Custom cover works for any real playlist (local, or online via a local
                    // override), so it isn't gated by `editable` like rename is.
                    if (!isGuest) {
                        add(
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.edit_playlist_cover)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.image),
                                        contentDescription = null,
                                    )
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
                                    Material3MenuItemData(
                                        title = { Text(text = stringResource(R.string.remove_custom_image)) },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.close),
                                                contentDescription = null,
                                            )
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
                        Material3MenuItemData(
                            title = { 
                                Text(
                                    text = if (isPinned) "Unpin from Speed dial" else "Pin to Speed dial" 
                                ) 
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(if (isPinned) R.drawable.remove else R.drawable.add),
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    if (isPinned) {
                                        database.speedDialDao.delete(playlist.id)
                                    } else {
                                        database.speedDialDao.insert(
                                            SpeedDialItem(
                                                id = playlist.id,
                                                title = playlist.playlist.name,
                                                subtitle = null,
                                                thumbnailUrl = playlist.thumbnails.firstOrNull(),
                                                type = "PLAYLIST"
                                            )
                                        )
                                    }
                                }
                                onDismiss()
                            }
                        )
                    )
                    if (downloadPlaylist != true) {
                        add(
                            when (downloadState) {
                                Download.STATE_COMPLETED -> {
                                    Material3MenuItemData(
                                        title = {
                                            Text(
                                                text = stringResource(R.string.remove_download)
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.offline),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            showRemoveDownloadDialog = true
                                        }
                                    )
                                }
                                Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                                    Material3MenuItemData(
                                        title = { Text(text = stringResource(R.string.downloading)) },
                                        icon = {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        },
                                        onClick = {
                                            // Cancel, not remove: this used to open the
                                            // remove-download dialog, which deleted every
                                            // song in the playlist including the ones that
                                            // had already finished.
                                            cancelDownloads(context, songs.map { it.id }, downloads)
                                        }
                                    )
                                }
                                else -> {
                                    Material3MenuItemData(
                                        title = { Text(text = stringResource(R.string.action_download)) },
                                        description = { Text(text = stringResource(R.string.download_desc)) },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.download),
                                                contentDescription = null,
                                            )
                                        },
                                        onClick = {
                                            downloadSongs(
                                                context,
                                                songs.map { DownloadTarget(it.id, it.song.title) },
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
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.delete)) },
                                description = { Text(text = stringResource(R.string.delete_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.delete),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    showDeletePlaylistDialog = true
                                }
                            )
                        )
                    }
                    val currentBrowseId = dbPlaylist?.playlist?.browseId ?: playlist.playlist.browseId
                    if (currentBrowseId == null && isSignedIn && !isGuest) {
                        add(
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.sync_playlist)) },
                                description = { Text(text = stringResource(R.string.sync_playlist_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.sync),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    isSyncing = true
                                    syncedCount = 0
                                    isSyncComplete = false
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            var browseId: String? = null
                                            var attempt = 0
                                            val maxAttempts = 3
                                            while (attempt < maxAttempts) {
                                                try {
                                                    browseId = YouTube.createPlaylist(playlist.playlist.name)
                                                    if (browseId != null) break
                                                } catch (e: Exception) {
                                                    attempt++
                                                    if (attempt >= maxAttempts) throw e
                                                    kotlinx.coroutines.delay(1000)
                                                }
                                            }
                                            if (browseId != null) {
                                                database.query {
                                                    update(playlist.playlist.copy(browseId = browseId))
                                                }
                                                var successCount = 0
                                                songs.forEachIndexed { index, song ->
                                                    val result = YouTube.addToPlaylist(browseId, song.id)
                                                    if (result.isSuccess) {
                                                        successCount++
                                                    } else {
                                                        result.exceptionOrNull()?.printStackTrace()
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
                    playlist.playlist.shareLink?.let { shareLink ->
                        add(
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.share)) },
                                description = { Text(text = stringResource(R.string.share_desc)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.share),
                                        contentDescription = null,
                                    )
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
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = stringResource(R.string.playlist_uploaded),
                        style = MaterialTheme.typography.bodyLarge
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
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = if (songs.isEmpty()) {
                            stringResource(R.string.please_wait)
                        } else {
                            stringResource(R.string.syncing_progress, syncedCount, songs.size)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
