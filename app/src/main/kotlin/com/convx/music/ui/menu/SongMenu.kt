/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.DownloadRingIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
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
import com.convx.music.ui.ios.compat.ListItem
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import org.koin.androidx.compose.koinViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.YouTube
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.LocalSyncUtils
import com.convx.music.R
import com.convx.music.constants.ListItemHeight
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.Event
import com.convx.music.db.entities.SpeedDialItem
import com.convx.music.library.PlaylistCommands
import com.convx.music.library.PlaylistEntryReference
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.ExoDownloadService
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.TextFieldDialog
import android.widget.Toast
import com.convx.music.utils.ExternalTagEditor
import com.convx.music.utils.listItemShape
import com.convx.music.ui.utils.ShowMediaInfo
import com.convx.music.viewmodels.CachePlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun SongMenu(
    originalSong: Song,
    event: Event? = null,
    navController: NavController,
    playlistEntry: PlaylistEntryReference? = null,
    playlistBrowseId: String? = null,
    onDismiss: () -> Unit,
    isFromCache: Boolean = false,
) {
    val context = LocalContext.current
    val ringtoneViewModel = com.convx.music.LocalRingtoneViewModel.current
    val database = LocalDatabase.current.get()
    val playlistCommands = koinInject<PlaylistCommands>()
    val playerConnection = LocalPlayerConnection.current ?: return
    val songState = database.song(originalSong.id).collectAsState(initial = originalSong)
    val song = songState.value ?: originalSong
    val downloadUtil = LocalDownloadUtil.current.get()
    val download by downloadUtil.getDownload(originalSong.id)
        .collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    val syncUtils = LocalSyncUtils.current.get()
    val listenTogetherManager = LocalListenTogetherManager.current
    val scope = rememberCoroutineScope()
    var refetchIconDegree by remember { mutableFloatStateOf(0f) }

    val cacheViewModel = koinViewModel<CachePlaylistViewModel>()

    val rotationAnimation by animateFloatAsState(
        targetValue = refetchIconDegree,
        animationSpec = tween(durationMillis = 800),
        label = "",
    )

    val isPinned by database.speedDialDao.isPinned(song.id).collectAsState(initial = false)

    val orderedArtists by produceState(initialValue = emptyList<ArtistEntity>(), song) {
        withContext(Dispatchers.IO) {
            val artistMaps = database.songArtistMap(song.id).sortedBy { it.position }
            val sorted = artistMaps.mapNotNull { map ->
                song.artists.firstOrNull { it.id == map.artistId }
            }
            value = sorted
        }
    }

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val TextFieldValueSaver: Saver<TextFieldValue, *> = Saver(
        save = { it.text },
        restore = { text -> TextFieldValue(text, TextRange(text.length)) }
    )

    var titleField by rememberSaveable(stateSaver = TextFieldValueSaver) {
        mutableStateOf(TextFieldValue(song.song.title))
    }

    var artistField by rememberSaveable(stateSaver = TextFieldValueSaver) {
        mutableStateOf(TextFieldValue(song.artists.firstOrNull()?.name.orEmpty()))
    }

    var showCoverSearch by rememberSaveable { mutableStateOf(false) }
    var showSplitNotice by rememberSaveable { mutableStateOf(false) }
    val (stemQuality) = com.convx.music.utils.rememberEnumPreference(com.convx.music.constants.StemQualityKey, com.convx.music.playback.smart.StemQuality.FAST)
    val stemCount = if (stemQuality == com.convx.music.playback.smart.StemQuality.DEMUCS && com.convx.music.playback.smart.DemucsModel.isInstalled(context)) 4 else 2
    if (showSplitNotice) {
        com.convx.music.ui.component.StemNoticeDialog(
            bytes = com.convx.music.playback.smart.StemStore.estimatedBytes(song.song.duration, stemCount),
            onDismiss = { showSplitNotice = false },
            onConfirm = {
                showSplitNotice = false
                com.convx.music.playback.dj.LibraryAnalysis.analyze(
                    context,
                    listOf(com.convx.music.playback.dj.LibraryAnalysis.Target(song.id, song.song.duration, song.song.title, stems = true)),
                )
                onDismiss()
            },
        )
    }
    if (showCoverSearch) {
        com.convx.music.ui.component.CoverSearchDialog(
            initialQuery = listOf(song.song.title, song.artists.firstOrNull()?.name).filterNotNull().joinToString(" "),
            onDismiss = { showCoverSearch = false },
            apply = { url -> com.convx.music.utils.SongBundle.applySongCover(context, database, song.id, url) },
        )
    }

    if (showEditDialog) {
        TextFieldDialog(
            icon = {
                SymbolIcon(symbol = sf("pencil"), contentDescription = null)
            },
            title = {
                Text(text = stringResource(R.string.edit_song))
            },
            textFields = listOf(
                stringResource(R.string.song_title) to titleField,
                stringResource(R.string.artist_name) to artistField
            ),
            onTextFieldsChange = { index, newValue ->
                if (index == 0) titleField = newValue
                else artistField = newValue
            },
            onDoneMultiple = { values ->
                val newTitle = values[0]
                val newArtist = values[1]

                coroutineScope.launch {
                    database.query {
                        update(song.song.copy(title = newTitle))
                        val artist = song.artists.firstOrNull()
                        if (artist != null) {
                            update(artist.copy(name = newArtist))
                        }
                    }

                    showEditDialog = false
                    onDismiss()
                }
            },
            onDismiss = { showEditDialog = false }
        )
    }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showErrorPlaylistAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    AddToPlaylistDialog(
        isVisible = showChoosePlaylistDialog,
        onGetSong = { playlist ->
            coroutineScope.launch(Dispatchers.IO) {
                playlist.playlist.browseId?.let { browseId ->
                    YouTube.addToPlaylist(browseId, song.id)
                }
            }
            listOf(song.id)
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
                    headlineContent = { Text(text = stringResource(R.string.already_in_playlist)) },
                    leadingContent = {
                        Image(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppTheme.colorScheme.onBackground),
                            modifier = Modifier.size(ListThumbnailSize),
                        )
                    },
                    modifier = Modifier.clickable { showErrorPlaylistAddDialog = false },
                )
            }

            items(listOf(song)) { song ->
                SongListItem(song = song)
            }
        }
    }

    var showSelectArtistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showSelectArtistDialog) {
        ListDialog(
            onDismiss = { showSelectArtistDialog = false },
        ) {
            items(
                items = song.artists.distinctBy { it.id },
                key = { it.id },
            ) { artist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
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
                            modifier = Modifier
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }

    SongListItem(
        song = song,
        badges = {},
        shape = listItemShape(0, 2),
        trailingContent = {
            IconButton(
                onClick = {
                    val s = song.song.toggleLike()
                    database.query {
                        update(s)
                    }
                    syncUtils.likeSong(s)
                },
            ) {
                SymbolIcon(symbol = if (song.song.liked) sf("heart.fill") else sf("heart"), contentDescription = null, tint = if (song.song.liked) AppTheme.colorScheme.error else LocalContentColor.current)
            }
        },
    )

    HorizontalDivider()

    Spacer(modifier = Modifier.height(12.dp))

    val bottomSheetPageState = LocalBottomSheetPageState.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    // A file on the device has no YouTube id: share links, radio, download and refetch would all target a video that doesn't exist.
    val isLocal = song.song.isLocal

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
                    NewAction(
                        icon = { Icon(painterResource(R.drawable.sparks), contentDescription = null, modifier = Modifier.size(28.dp)) },
                        text = stringResource(R.string.song_insight_title),
                        onClick = {
                            onDismiss()
                            navController.navigate("song_insight/${android.net.Uri.encode(song.id)}")
                        },
                    ),
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("pencil"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.edit),
                        onClick = { showEditDialog = true }
                    ),
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("photo"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.search_cover),
                        onClick = { showCoverSearch = true }
                    ),
                    if (isLocal || song.song.isDownloaded) NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("waveform"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.analyze_song),
                        onClick = {
                            com.convx.music.playback.dj.LibraryAnalysis.analyze(
                                context,
                                listOf(com.convx.music.playback.dj.LibraryAnalysis.Target(song.id, song.song.duration, song.song.title)),
                            )
                            onDismiss()
                        }
                    ) else null,
                    if ((isLocal || song.song.isDownloaded) && song.song.duration in 1..com.convx.music.playback.smart.StemSeparator.MAX_SECONDS) NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("waveform.path"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.split_stems),
                        onClick = { showSplitNotice = true }
                    ) else null,
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("text.badge.plus"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.add_to_an_playlist),
                        onClick = { showChoosePlaylistDialog = true }
                    ),
                    if (isLocal) null else NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.share),
                        onClick = {
                            onDismiss()
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${song.id}")
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.sparks),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = AppTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        text = stringResource(R.string.ai_dj_mix_from_song),
                        onClick = {
                            onDismiss()
                            navController.navigate("ai_dj_set?seedSongId=${song.id}")
                        }
                    ),
                    if (isLocal) null else NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.share_smart_link),
                        onClick = {
                            onDismiss()
                            // Odesli/song.link cross-platform link. Deterministic YouTube form
                            // (song.link/y/<id>) resolves to Spotify/Apple/etc. — no API call.
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://song.link/y/${song.id}")
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }
                    )
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)
            )
        }
        item {
            IosMenuGroup(
                items = listOfNotNull(
                    if (listenTogetherManager != null && listenTogetherManager.isInRoom && !listenTogetherManager.isHost) {
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.suggest_to_host)) },
                            icon = {
                                SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                            },
                            onClick = {
                                val durationMs = if (song.song.duration > 0) song.song.duration.toLong() * 1000 else 180000L
                                val trackInfo = com.convx.music.listentogether.TrackInfo(
                                    id = song.id,
                                    title = song.song.title,
                                    artist = orderedArtists.joinToString(", ") { it.name },
                                    album = song.song.albumName,
                                    duration = durationMs,
                                    thumbnail = song.thumbnailUrl
                                )
                                listenTogetherManager.suggestTrack(trackInfo)
                                onDismiss()
                            }
                        )
                    } else null,
                    if (!isGuest && !isLocal) {
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.start_radio)) },
                            description = { Text(text = stringResource(R.string.start_radio_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("dot.radiowaves.left.and.right"), contentDescription = null)
                            },
                            onClick = {
                                onDismiss()
                                playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                            }
                        )
                    } else null,
                    if (!isGuest) {
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.play_next)) },
                            description = { Text(text = stringResource(R.string.play_next_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("text.line.first.and.arrowtriangle.forward"), contentDescription = null)
                            },
                            onClick = {
                                onDismiss()
                                playerConnection.playNext(song.toMediaItem())
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
                                playerConnection.addToQueue(song.toMediaItem())
                            }
                        )
                    } else null
                )
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            IosMenuGroup(
                items = buildList {
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
                                        database.speedDialDao.delete(song.id)
                                    } else {
                                        database.speedDialDao.insert(
                                            SpeedDialItem(
                                                id = song.id,
                                                title = song.song.title,
                                                subtitle = song.artists.joinToString(", ") { it.name },
                                                thumbnailUrl = song.song.thumbnailUrl,
                                                type = "SONG",
                                                explicit = song.song.explicit
                                            )
                                        )
                                    }
                                }
                                onDismiss()
                            }
                        )
                    )
                    add(
                        IosMenuItemData(
                            title = {
                                Text(
                                    text = stringResource(
                                        if (song.song.inLibrary == null) R.string.add_to_library
                                        else R.string.remove_from_library
                                    )
                                )
                            },
                            description = { Text(text = stringResource(R.string.add_to_library_desc)) },
                            icon = {
                                SymbolIcon(symbol = 
                                        if (song.song.inLibrary == null) sf("text.badge.plus")
                                        else sf("text.badge.checkmark")
                                    , contentDescription = null)
                            },
                            onClick = {
                                val currentSong = song.song
                                val isInLibrary = currentSong.inLibrary != null
                                val token =
                                    if (isInLibrary) currentSong.libraryRemoveToken else currentSong.libraryAddToken

                                token?.let {
                                    coroutineScope.launch {
                                        YouTube.feedback(listOf(it))
                                    }
                                }

                                database.query {
                                    update(song.song.toggleLibrary())
                                }
                            }
                        )
                    )
                    if (event != null) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.remove_from_history)) },
                                icon = {
                                    SymbolIcon(symbol = sf("trash"), contentDescription = null)
                                },
                                onClick = {
                                    onDismiss()
                                    database.query {
                                        delete(event)
                                    }
                                }
                            )
                        )
                    }
                    if (playlistEntry != null) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.remove_from_playlist)) },
                                icon = {
                                    SymbolIcon(symbol = sf("trash"), contentDescription = null)
                                },
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val removal = playlistCommands.removePlaylistSong(playlistEntry)
                                        val browseId = playlistBrowseId ?: return@launch
                                        if (removal.removed) {
                                            removal.setVideoId?.let { setVideoId ->
                                                YouTube.removeFromPlaylist(browseId, playlistEntry.songId, setVideoId)
                                            }
                                        }
                                    }
                                    onDismiss()
                                }
                            )
                        )
                    }
                    if (isFromCache) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.remove_from_cache)) },
                                icon = {
                                    SymbolIcon(symbol = sf("trash"), contentDescription = null)
                                },
                                onClick = {
                                    onDismiss()
                                    cacheViewModel.removeSongFromCache(song.id)
                                }
                            )
                        )
                    }
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.set_as_ringtone)) },
                            icon = {
                                SymbolIcon(symbol = sf("bell"), contentDescription = null)
                            },
                            onClick = {
                                ringtoneViewModel.showTrimmer(
                                    song.id,
                                    song.song.title,
                                    song.artists.joinToString { it.name },
                                    song.song.duration
                                )
                                onDismiss()
                            }
                        )
                    )
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        if (!isLocal) item {
            IosMenuGroup(
                items = listOfNotNull(
                    when (download?.state) {
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
                                    com.convx.music.playback.removeDownloads(context, listOf(song.id))
                                }
                            )
                        }
                        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.downloading)) },
                                icon = {
                                    DownloadRingIcon(download)
                                },
                                onClick = {
                                    DownloadService.sendRemoveDownload(
                                        context,
                                        ExoDownloadService::class.java,
                                        song.id,
                                        false,
                                    )
                                }
                            )
                        }
                        // Show failed downloads separately and let the user retry.
                        Download.STATE_FAILED -> {
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.download_failed)) },
                                icon = {
                                    SymbolIcon(symbol = sf("exclamationmark.circle"), contentDescription = null, tint = AppTheme.colorScheme.error)
                                },
                                onClick = {
                                    val downloadRequest =
                                        DownloadRequest
                                            .Builder(song.id, song.id.toUri())
                                            .setCustomCacheKey(song.id)
                                            .setData(song.song.title.toByteArray())
                                            .build()
                                    DownloadService.sendAddDownload(
                                        context,
                                        ExoDownloadService::class.java,
                                        downloadRequest,
                                        false,
                                    )
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
                                    val downloadRequest =
                                        DownloadRequest
                                            .Builder(song.id, song.id.toUri())
                                            .setCustomCacheKey(song.id)
                                            .setData(song.song.title.toByteArray())
                                            .build()
                                    DownloadService.sendAddDownload(
                                        context,
                                        ExoDownloadService::class.java,
                                        downloadRequest,
                                        false,
                                    )
                                }
                            )
                        }
                    },
                    if (download?.state == Download.STATE_COMPLETED) {
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.upgrade_download_quality)) },
                            description = { Text(text = stringResource(R.string.upgrade_download_quality_desc)) },
                            icon = { SymbolIcon(symbol = sf("arrow.up.circle"), contentDescription = null) },
                            onClick = {
                                downloadUtil.upgradeQuality(song.id)
                                onDismiss()
                            }
                        )
                    } else null,
                )
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            IosMenuGroup(
                items = buildList {
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.view_artist)) },
                            description = { Text(text = song.artists.joinToString { it.name }) },
                            icon = {
                                SymbolIcon(symbol = sf("music.mic"), contentDescription = null)
                            },
                            onClick = {
                                if (song.artists.size == 1) {
                                    navController.navigate("artist/${song.artists[0].id}")
                                    onDismiss()
                                } else {
                                    showSelectArtistDialog = true
                                }
                            }
                        )
                    )
                    val albumId = song.song.albumId?.takeIf { it.isNotBlank() } ?: song.album?.id?.takeIf { it.isNotBlank() }
                    val albumName = song.song.albumName ?: song.album?.title
                    if (!albumId.isNullOrBlank()) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.view_album)) },
                                description = albumName?.let { name ->
                                    { Text(text = name) }
                                },
                                icon = {
                                    SymbolIcon(symbol = sf("square.stack"), contentDescription = null)
                                },
                                onClick = {
                                    onDismiss()
                                    navController.navigate("album/$albumId")
                                }
                            )
                        )
                    }
                    if (!isLocal) add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.refetch)) },
                            description = { Text(text = stringResource(R.string.refetch_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null, modifier = Modifier.graphicsLayer(rotationZ = rotationAnimation))
                            },
                            onClick = {
                                refetchIconDegree -= 360
                                scope.launch(Dispatchers.IO) {
                                    YouTube.queue(listOf(song.id)).onSuccess {
                                        val newSong = it.firstOrNull()
                                        if (newSong != null) {
                                            database.transaction {
                                                update(song, newSong.toMediaMetadata())
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    )
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.details)) },
                            description = { Text(text = stringResource(R.string.details_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("info.circle"), contentDescription = null)
                            },
                            onClick = {
                                onDismiss()
                                bottomSheetPageState.showFloating {
                                    ShowMediaInfo(song.id)
                                }
                            }
                        )
                    )
                    // Local files only: a song's id is its MediaStore content URI, which
                    // is the one thing an external editor can actually be handed. There
                    // is nothing on disk to edit for a YouTube track.
                    if (song.song.isLocal) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.edit_tags)) },
                                description = {
                                    Text(text = stringResource(R.string.edit_tags_desc))
                                },
                                icon = {
                                    SymbolIcon(symbol = sf("pencil"), contentDescription = null)
                                },
                                onClick = {
                                    onDismiss()
                                    val opened = ExternalTagEditor.launch(
                                        context = context,
                                        contentUri = song.id,
                                        mimeType = null,
                                    )
                                    if (!opened) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.edit_tags_no_app),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                }
                            )
                        )
                    }
                }
            )
        }
    }
}
