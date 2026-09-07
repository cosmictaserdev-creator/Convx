/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.offline.Download
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.LocalSyncUtils
import com.convxy.music.R
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.DefaultDialog
import com.convxy.music.ui.component.Material3MenuGroup
import com.convxy.music.ui.component.Material3MenuItemData
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import com.convxy.music.playback.DownloadTarget
import com.convxy.music.playback.cancelDownloads
import com.convxy.music.playback.downloadSongs
import com.convxy.music.playback.removeDownloads

@Composable
fun YouTubeSelectionSongMenu(
    songSelection: List<SongItem>,
    onDismiss: () -> Unit,
    clearAction: () -> Unit,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val downloadUtil = LocalDownloadUtil.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()
    val syncUtils = LocalSyncUtils.current

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val listenTogetherManager = com.convxy.music.LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && listenTogetherManager.isHost == false

    var showRemoveDownloadDialog by remember {
        mutableStateOf(false)
    }

    // Check if all songs are liked
    val allLiked by remember(songSelection) {
        mutableStateOf(
            songSelection.isNotEmpty() && songSelection.all { song ->
                // Convert to MediaMetadata to check liked status
                val metadata = song.toMediaMetadata()
                metadata.liked
            }
        )
    }

    // Check if all songs are in library
    val allInLibrary by remember(songSelection) {
        mutableStateOf(
            songSelection.all { song ->
                val metadata = song.toMediaMetadata()
                metadata.inLibrary != null
            }
        )
    }

    // Collected rather than folded into a LaunchedEffect so the current state of each
    // download is also available at click time — the download and cancel actions filter
    // on it, see DownloadActions.
    val downloads by downloadUtil.downloads.collectAsState()
    val downloadState = remember(songSelection, downloads) {
        when {
            songSelection.isEmpty() -> Download.STATE_STOPPED
            songSelection.all { downloads[it.id]?.state == Download.STATE_COMPLETED } ->
                Download.STATE_COMPLETED

            songSelection.all {
                downloads[it.id]?.state == Download.STATE_QUEUED ||
                    downloads[it.id]?.state == Download.STATE_DOWNLOADING ||
                    downloads[it.id]?.state == Download.STATE_COMPLETED
            } -> Download.STATE_DOWNLOADING

            else -> Download.STATE_STOPPED
        }
    }

    AddToPlaylistDialogOnline(
        isVisible = showChoosePlaylistDialog,
        songs = remember { 
            songSelection.map { song ->
                // Convert SongItem to Song entity
                val metadata = song.toMediaMetadata()
                com.convxy.music.db.entities.Song(
                    song = com.convxy.music.db.entities.SongEntity(
                        id = metadata.id,
                        title = metadata.title,
                        duration = metadata.duration,
                        thumbnailUrl = metadata.thumbnailUrl,
                        albumId = metadata.album?.id,
                        albumName = metadata.album?.title,
                        liked = metadata.liked,
                        totalPlayTime = 0,
                        inLibrary = metadata.inLibrary,
                        isLocal = false,
                        libraryAddToken = metadata.libraryAddToken,
                        libraryRemoveToken = metadata.libraryRemoveToken
                    ),
                    artists = metadata.artists.map { artist ->
                        com.convxy.music.db.entities.ArtistEntity(
                            id = artist.id ?: "",
                            name = artist.name
                        )
                    },
                    album = metadata.album?.let { album ->
                        com.convxy.music.db.entities.AlbumEntity(
                            id = album.id,
                            title = album.title,
                            thumbnailUrl = metadata.thumbnailUrl, // Use song's thumbnail as album thumbnail
                            songCount = 0,
                            duration = 0
                        )
                    }
                )
            }.toMutableStateList()
        },
        onProgressStart = { },
        onPercentageChange = { },
        onDismiss = {
            showChoosePlaylistDialog = false
        },
    )

    if (showRemoveDownloadDialog) {
        DefaultDialog(
            onDismiss = { showRemoveDownloadDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.remove_download_playlist_confirm, "selection"),
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
                        removeDownloads(context, songSelection.map { it.id })
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 8.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
        ),
    ) {
        item {
            Material3MenuGroup(
                listOfNotNull(
                    if (!isGuest) Material3MenuItemData(
                        icon = { Icon(painterResource(R.drawable.play), null) },
                        title = { Text(stringResource(R.string.play)) },
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(
                                    title = context.getString(R.string.queue_all_songs),
                                    items = songSelection.map { it.toMediaItem() },
                                ),
                            )
                            clearAction()
                            onDismiss()
                        },
                    ) else null,
                    if (!isGuest) Material3MenuItemData(
                        icon = { Icon(painterResource(R.drawable.shuffle), null) },
                        title = { Text(stringResource(R.string.shuffle)) },
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(
                                    title = context.getString(R.string.queue_all_songs),
                                    items = songSelection.shuffled().map { it.toMediaItem() },
                                ),
                            )
                            clearAction()
                            onDismiss()
                        },
                    ) else null,
                    if (!isGuest) Material3MenuItemData(
                        icon = { Icon(painterResource(R.drawable.queue_music), null) },
                        title = { Text(stringResource(R.string.add_to_queue)) },
                        onClick = {
                            playerConnection.addToQueue(songSelection.map { it.toMediaItem() })
                            clearAction()
                            onDismiss()
                        },
                    ) else null,
                    Material3MenuItemData(
                        icon = { Icon(painterResource(R.drawable.playlist_add), null) },
                        title = { Text(stringResource(R.string.add_to_playlist)) },
                        onClick = {
                            showChoosePlaylistDialog = true
                        },
                    ),
                    Material3MenuItemData(
                        title = {
                            Text(
                                text = stringResource(
                                    if (allInLibrary) R.string.remove_from_library else R.string.add_to_library
                                )
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (allInLibrary) R.drawable.library_add_check else R.drawable.library_add
                                ),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            if (allInLibrary) {
                                database.query {
                                    songSelection.forEach { song ->
                                        inLibrary(song.id, null)
                                    }
                                }
                                coroutineScope.launch {
                                    // Use the new reliable method that fetches fresh tokens
                                    songSelection.forEach { song ->
                                        YouTube.toggleSongLibrary(song.id, false)
                                    }
                                }
                            } else {
                                database.transaction {
                                    songSelection.forEach { song ->
                                        insert(song.toMediaMetadata())
                                        inLibrary(song.id, LocalDateTime.now())
                                    }
                                }
                                coroutineScope.launch {
                                    // Use the new reliable method that fetches fresh tokens
                                    songSelection.filter { song ->
                                        song.toMediaMetadata().inLibrary == null
                                    }.forEach { song ->
                                        YouTube.toggleSongLibrary(song.id, true)
                                    }
                                }
                            }
                            clearAction()
                            onDismiss()
                        }
                    ),
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
                                    // remove-download dialog, which deleted every selected
                                    // song including the already-finished ones.
                                    cancelDownloads(
                                        context,
                                        songSelection.map { it.id },
                                        downloads,
                                    )
                                }
                            )
                        }
                        else -> {
                            Material3MenuItemData(
                                title = { Text(text = stringResource(R.string.action_download)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.download),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    downloadSongs(
                                        context,
                                        songSelection.map { DownloadTarget(it.id, it.title) },
                                        downloads,
                                    )
                                    clearAction()
                                    onDismiss()
                                }
                            )
                        }
                    },
                    Material3MenuItemData(
                        title = {
                            Text(
                                text = stringResource(
                                    if (allLiked) R.string.dislike_all else R.string.like_all
                                )
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (allLiked) R.drawable.favorite else R.drawable.favorite_border
                                ),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            database.transaction {
                                songSelection.forEach { song ->
                                    val metadata = song.toMediaMetadata()
                                    if ((!allLiked && !metadata.liked) || allLiked) {
                                        // Insert the song first if it doesn't exist
                                        insert(metadata)
                                        // Create SongEntity with toggled like status
                                        val songEntity = com.convxy.music.db.entities.SongEntity(
                                            id = metadata.id,
                                            title = metadata.title,
                                            duration = metadata.duration,
                                            thumbnailUrl = metadata.thumbnailUrl,
                                            albumId = metadata.album?.id,
                                            albumName = metadata.album?.title,
                                            liked = !metadata.liked,
                                            totalPlayTime = 0,
                                            inLibrary = metadata.inLibrary,
                                            isLocal = false,
                                            libraryAddToken = metadata.libraryAddToken,
                                            libraryRemoveToken = metadata.libraryRemoveToken
                                        )
                                        update(songEntity)
                                        syncUtils.likeSong(songEntity)
                                    }
                                }
                            }
                            clearAction()
                            onDismiss()
                        }
                    )
                ),
            )
        }
    }
}
