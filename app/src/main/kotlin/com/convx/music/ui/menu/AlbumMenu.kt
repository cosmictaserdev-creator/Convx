/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.offline.Download.STATE_COMPLETED
import androidx.media3.exoplayer.offline.Download.STATE_DOWNLOADING
import androidx.media3.exoplayer.offline.Download.STATE_QUEUED
import androidx.media3.exoplayer.offline.Download.STATE_STOPPED
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.YouTube
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.ListItemHeight
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.SpeedDialItem
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.combinedDownloadState
import com.convx.music.playback.cancelDownloads
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.removeDownloads
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.AlbumListItem
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.ListItem
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.ui.component.SongListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@SuppressLint("MutableCollectionMutableState")
@Composable
fun AlbumMenu(
    originalAlbum: Album,
    navController: NavController,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val downloadUtil = LocalDownloadUtil.current.get()
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val scope = rememberCoroutineScope()
    val libraryAlbum by database.album(originalAlbum.id).collectAsState(initial = originalAlbum)
    val album = libraryAlbum ?: originalAlbum
    var songs by remember {
        mutableStateOf(emptyList<Song>())
    }
    var showSplitNotice by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    var showCoverSearch by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    if (showCoverSearch) {
        com.convx.music.ui.component.CoverSearchDialog(
            initialQuery = "${album.album.title} ${album.artists.firstOrNull()?.name.orEmpty()}".trim(),
            onDismiss = { showCoverSearch = false },
            apply = { url -> com.convx.music.utils.SongBundle.applyAlbumCover(context, database, album.id, url) },
        )
    }
    val (stemQuality) = com.convx.music.utils.rememberEnumPreference(com.convx.music.constants.StemQualityKey, com.convx.music.playback.smart.StemQuality.FAST)
    val stemCount = if (stemQuality == com.convx.music.playback.smart.StemQuality.DEMUCS && com.convx.music.playback.smart.DemucsModel.isInstalled(context)) 4 else 2
    if (showSplitNotice) {
        val eligible = songs.filter { (it.song.isLocal || it.song.isDownloaded) && it.song.duration in 1..com.convx.music.playback.smart.StemSeparator.MAX_SECONDS }
        com.convx.music.ui.component.StemNoticeDialog(
            bytes = eligible.sumOf { com.convx.music.playback.smart.StemStore.estimatedBytes(it.song.duration, stemCount) },
            onDismiss = { showSplitNotice = false },
            onConfirm = {
                showSplitNotice = false
                com.convx.music.playback.dj.LibraryAnalysis.analyze(
                    context,
                    eligible.map { com.convx.music.playback.dj.LibraryAnalysis.Target(it.id, it.song.duration, it.song.title, stems = true) },
                )
                onDismiss()
            },
        )
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        database.albumSongs(album.id).collect {
            songs = it
        }
    }

    val downloads by downloadUtil.downloads.collectAsStateWithLifecycle()
    val downloadState = remember(songs, downloads) {
        combinedDownloadState(songs, downloads) { it.id }
    }

    var refetchIconDegree by remember { mutableFloatStateOf(0f) }

    val rotationAnimation by animateFloatAsState(
        targetValue = refetchIconDegree,
        animationSpec = tween(durationMillis = 800),
        label = "",
    )

    val isPinned by database.speedDialDao.isPinned(album.id).collectAsState(initial = false)

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showSelectArtistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showErrorPlaylistAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val notAddedList by remember {
        mutableStateOf(mutableListOf<Song>())
    }

    AddToPlaylistDialog(
        isVisible = showChoosePlaylistDialog,
        onGetSong = { playlist ->
            coroutineScope.launch(Dispatchers.IO) {
                playlist.playlist.browseId?.let { playlistId ->
                    album.album.playlistId?.let { addPlaylistId ->
                        YouTube.addPlaylistToPlaylist(playlistId, addPlaylistId)
                    }
                }
            }
            songs.map { it.id }
        },
        onDismiss = {
            showChoosePlaylistDialog = false
        },
    )

    if (showErrorPlaylistAddDialog) {
        ListDialog(
            onDismiss = {
                showErrorPlaylistAddDialog = false
                onDismiss()
            },
        ) {
            item {
                ListItem(
                    title = stringResource(R.string.already_in_playlist),
                    thumbnailContent = {
                        Image(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppTheme.colorScheme.onBackground),
                            modifier = Modifier.size(ListThumbnailSize),
                        )
                    },
                    modifier =
                    Modifier
                        .clickable { showErrorPlaylistAddDialog = false },
                )
            }

            items(notAddedList, key = { it.id }) { song ->
                SongListItem(song = song)
            }
        }
    }

    if (showSelectArtistDialog) {
        ListDialog(
            onDismiss = { showSelectArtistDialog = false },
        ) {
            items(
                items = album.artists.distinctBy { it.id },
                key = { it.id },
            ) { artist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                    Modifier
                        .height(ListItemHeight)
                        .clickable {
                            navController.navigate("artist/${artist.id}")
                            showSelectArtistDialog = false
                            onDismiss()
                        }
                        .padding(horizontal = 12.dp),
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = artist.thumbnailUrl,
                            contentDescription = null,
                            modifier =
                            Modifier
                                .size(ListThumbnailSize)
                                .clip(CircleShape),
                        )
                    }
                    Text(
                        text = artist.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier =
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }

    AlbumListItem(
        album = album,
        showLikedIcon = false,
        badges = {},
        trailingContent = {
            IconButton(
                onClick = {
                    database.query {
                        update(album.album.toggleLike())
                    }
                },
            ) {
                SymbolIcon(symbol = if (album.album.bookmarkedAt != null) sf("heart.fill") else sf("heart"), contentDescription = null, tint = if (album.album.bookmarkedAt != null) AppTheme.colorScheme.error else LocalContentColor.current)
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
                                            title = album.album.title,
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
                                    album.album.playlistId?.let { playlistId ->
                                        playerConnection.service.getAutomix(playlistId)
                                    }
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = album.album.title,
                                            items = songs.shuffled().map(Song::toMediaItem)
                                        )
                                    )
                                }
                            }
                        )
                    } else null,
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("photo"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.search_cover),
                        onClick = { showCoverSearch = true }
                    ),
                    if (songs.any { it.song.isLocal || it.song.isDownloaded }) NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("waveform"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.analyze_album),
                        onClick = {
                            com.convx.music.playback.dj.LibraryAnalysis.analyze(
                                context,
                                songs.filter { it.song.isLocal || it.song.isDownloaded }.map {
                                    com.convx.music.playback.dj.LibraryAnalysis.Target(it.id, it.song.duration, it.song.title)
                                },
                            )
                            onDismiss()
                        }
                    ) else null,
                    if (songs.any { (it.song.isLocal || it.song.isDownloaded) && it.song.duration > 0 }) NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("waveform.path"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.split_stems_album),
                        onClick = { showSplitNotice = true }
                    ) else null,
                    if (album.album.isLocal) null else NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.share),
                        onClick = {
                            onDismiss()
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, com.convx.music.utils.ShareUtils.albumUrl(album.album.playlistId ?: album.id))
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
                items = listOfNotNull(
                    if (!isGuest) {
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.play_next)) },
                            description = { Text(text = stringResource(R.string.play_next_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("text.line.first.and.arrowtriangle.forward"), contentDescription = null)
                            },
                            onClick = {
                                onDismiss()
                                playerConnection.playNext(songs.map { it.toMediaItem() })
                            }
                        )
                    } else null,
                    if (!isGuest) {
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
                    } else null,
                    IosMenuItemData(
                        title = { Text(text = stringResource(R.string.add_to_playlist)) },
                        description = { Text(text = stringResource(R.string.add_to_playlist_desc)) },
                        icon = {
                            SymbolIcon(symbol = sf("text.badge.plus"), contentDescription = null)
                        },
                        onClick = {
                            showChoosePlaylistDialog = true
                        }
                    ),
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
                                    database.speedDialDao.delete(album.id)
                                } else {
                                    database.speedDialDao.insert(
                                        SpeedDialItem(
                                            id = album.id,
                                            secondaryId = album.album.playlistId,
                                            title = album.album.title,
                                            subtitle = album.artists.joinToString(", ") { it.name },
                                            thumbnailUrl = album.album.thumbnailUrl,
                                            type = "ALBUM",
                                            explicit = album.album.explicit
                                        )
                                    )
                                }
                            }
                            onDismiss()
                        }
                    )
                )
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // A local album is already on the device; there is nothing to download.
        if (!album.album.isLocal) item {
            IosMenuGroup(
                items = listOf(
                    when (downloadState) {
                        STATE_COMPLETED -> {
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
                                    removeDownloads(context, songs.map { it.id })
                                }
                            )
                        }
                        STATE_QUEUED, STATE_DOWNLOADING -> {
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
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            IosMenuGroup(
                items = listOfNotNull(
                    IosMenuItemData(
                        title = { Text(text = stringResource(R.string.view_artist)) },
                        description = { Text(text = album.artists.joinToString { it.name }) },
                        icon = {
                            SymbolIcon(symbol = sf("music.mic"), contentDescription = null)
                        },
                        onClick = {
                            if (album.artists.size == 1) {
                                navController.navigate("artist/${album.artists[0].id}")
                                onDismiss()
                            } else {
                                showSelectArtistDialog = true
                            }
                        }
                    ),
                    if (album.album.isLocal) null else IosMenuItemData(
                        title = { Text(text = stringResource(R.string.refetch)) },
                        description = { Text(text = stringResource(R.string.refetch_desc)) },
                        icon = {
                            SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null, modifier = Modifier.graphicsLayer(rotationZ = rotationAnimation))
                        },
                        onClick = {
                            refetchIconDegree -= 360
                            scope.launch(Dispatchers.IO) {
                                YouTube.album(album.id).onSuccess {
                                    database.transaction {
                                        update(album.album, it, album.artists)
                                    }
                                }
                            }
                        }
                    )
                )
            )
        }
    }
}
