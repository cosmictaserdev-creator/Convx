package com.convx.music.ui.screens.library

import com.convx.music.ui.utils.sharedArtworkSource
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import com.convx.music.constants.ThumbnailRoundedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.component.LocalMusicEmptyState
import com.convx.music.ui.component.LocalMusicPermissionPrompt
import com.convx.music.ui.component.hasLocalAudioPermission
import com.convx.music.ui.component.rememberLocalAudioPermission
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ButtonDefaults
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.DropdownMenu
import com.convx.music.ui.ios.compat.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Text
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowClockwise
import com.composables.sfsymbols.monochrome.SFListBullet
import com.composables.sfsymbols.monochrome.SFMagnifyingglass
import com.composables.sfsymbols.monochrome.SFPlayFill
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFXmark
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size as CoilSize
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.theme.rememberGlobalAccentColors
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import java.time.LocalDateTime
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalMusicScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: LocalMusicViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val menuState = LocalMenuState.current
    val (accentColor, onAccentColor) = rememberGlobalAccentColors()

    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()

    var hasStoragePermission by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf(LocalSortMode.NAME) }
    var showSortMenu by remember { mutableStateOf(false) }

    val displaySongs = remember(songs, searchQuery, sortMode) {
        songs
            .filter { it.title.contains(searchQuery, ignoreCase = true) }
            .let { list ->
                when (sortMode) {
                    LocalSortMode.NAME -> list.sortedBy { it.title.lowercase() }
                    LocalSortMode.DURATION -> list.sortedByDescending { it.song.duration }
                    // "Recently added" = MediaStore DATE_ADDED stored at scan; dateModified is the file's mtime.
                    LocalSortMode.RECENT -> list.sortedByDescending {
                        it.song.inLibrary ?: it.song.dateModified ?: LocalDateTime.MIN
                    }
                }
            }
    }
    val displayAlbums = remember(albums, searchQuery) {
        albums.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
    val displayArtists = remember(artists, searchQuery) {
        artists.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    val audioPermission = rememberLocalAudioPermission { granted -> hasStoragePermission = granted }

    // Re-read on entry as well as after the dialog: the user may have flipped it in Settings.
    LaunchedEffect(Unit) { hasStoragePermission = hasLocalAudioPermission(context) }

    // Refresh on entry; force=false so returning from the player doesn't re-sweep.
    LaunchedEffect(hasStoragePermission) {
        if (hasStoragePermission) viewModel.scanDevice(context, force = false)
    }

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.filter_local),
                        style = AppTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    if (hasStoragePermission) {
                        IconButton(
                            onClick = {
                                showSearch = !showSearch
                                if (!showSearch) searchQuery = ""
                            },
                        ) {
                            SymbolIcon(bounceOnEnter = true, symbol = if (showSearch) sf("SFXmark") else sf("SFMagnifyingglass"), contentDescription = "Search")
                        }
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                SymbolIcon(bounceOnEnter = true, symbol = sf("SFListBullet"), contentDescription = "Sort")
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Name") },
                                    onClick = { sortMode = LocalSortMode.NAME; showSortMenu = false },
                                )
                                DropdownMenuItem(
                                    text = { Text("Duration") },
                                    onClick = { sortMode = LocalSortMode.DURATION; showSortMenu = false },
                                )
                                DropdownMenuItem(
                                    text = { Text("Recently added") },
                                    onClick = { sortMode = LocalSortMode.RECENT; showSortMenu = false },
                                )
                            }
                        }
                        // Rescan
                        IconButton(
                            onClick = { if (!isScanning) viewModel.scanDevice(context) },
                            enabled = !isScanning,
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                SymbolIcon(bounceOnEnter = true, symbol = sf("SFArrowClockwise"), contentDescription = "Rescan")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Stats
                val totalCount = displaySongs.size + displayAlbums.size + displayArtists.size
                if (totalCount > 0) {
                    Text(
                        text = "${displaySongs.size} songs \u00b7 ${displayAlbums.size} albums \u00b7 ${displayArtists.size} artists",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (showSearch) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search local music") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Scan / Permission section
        if (!hasStoragePermission) {
            item(key = "permission") {
                LocalMusicPermissionPrompt(permission = audioPermission)
            }
        } else if (songs.isEmpty() && !isScanning) {
            item(key = "empty") {
                LocalMusicEmptyState(onScan = { viewModel.scanDevice(context) })
            }
        }

        // Scanning indicator
        if (isScanning) {
            item(key = "scanning") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Scanning device...",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Scan result
        val result = scanResult
        if (result != null && !isScanning) {
            item(key = "scan_result") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Found ${result.newSongs} new songs (${result.totalFound} total)",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedButton(
                        onClick = { viewModel.scanDevice(context) },
                    ) {
                        Text("Rescan", style = AppTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Play All / Shuffle All
        if (displaySongs.isNotEmpty()) {
            item(key = "actions") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(items = displaySongs.map { it.toMediaItem() }, startIndex = 0),
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = onAccentColor,
                        ),
                    ) {
                        SymbolIcon(bounceOnEnter = true, symbol = sf("SFPlayFill"), contentDescription = null, size = 18.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Play All")
                    }
                    Button(
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(items = displaySongs.shuffled().map { it.toMediaItem() }, startIndex = 0),
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = onAccentColor,
                        ),
                    ) {
                        SymbolIcon(bounceOnEnter = true, symbol = sf("SFShuffle"), contentDescription = null, size = 18.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Shuffle")
                    }
                }
            }
        }

        if (hasStoragePermission && folders.isNotEmpty()) {
            item(key = "folders_header") { SectionHeader(title = "Folders", count = folders.size) }
            itemsIndexed(folders.filter { it.path.contains(searchQuery, ignoreCase = true) }, key = { _, folder -> "folder:${folder.path}" }) { _, folder ->
                LocalMusicFolderRow(folder = folder, navController = navController)
            }
        }

        // Songs section
        if (displaySongs.isNotEmpty()) {
            item(key = "songs_header") {
                SectionHeader(
                    title = "Songs",
                    count = displaySongs.size,
                )
            }

            val songRows = displaySongs.take(LocalMusicSongRowCap)
            itemsIndexed(
                items = songRows,
                key = { _, it -> it.localMediaId() },
                contentType = { _, _ -> "song" },
            ) { index, song ->
                SongListItem(
                    song = song,
                    onClick = {
                        playerConnection.playQueue(
                            ListQueue(items = displaySongs.map { it.toMediaItem() }, startIndex = displaySongs.indexOf(song)),
                        )
                    },
                    onLongClick = {
                        menuState.show {
                            SongMenu(
                                originalSong = song,
                                navController = navController,
                                onDismiss = menuState::dismiss,
                            )
                        }
                    },
                )
                if (index != songRows.lastIndex) HorizontalDivider()
            }

            if (displaySongs.size > LocalMusicSongRowCap) {
                item(key = "songs_more") {
                    Text(
                        text = "+ ${displaySongs.size - LocalMusicSongRowCap} more songs",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("library/songs")
                            }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }

        // Albums section
        if (displayAlbums.isNotEmpty()) {
            item(key = "albums_header") {
                SectionHeader(
                    title = "Albums",
                    count = displayAlbums.size,
                )
            }

            itemsIndexed(
                items = displayAlbums,
                key = { _, it -> it.id },
                contentType = { _, _ -> "album" },
            ) { index, album ->
                AlbumListItem(
                    album = album,
                    onClick = {
                        navController.navigate(albumRoute(album.id, album.thumbnailUrl))
                    },
                )
                if (index != displayAlbums.lastIndex) HorizontalDivider()
            }
        }

        // Artists section
        if (displayArtists.isNotEmpty()) {
            item(key = "artists_header") {
                SectionHeader(
                    title = "Artists",
                    count = displayArtists.size,
                )
            }

            itemsIndexed(
                items = displayArtists,
                key = { _, it -> it.id },
                contentType = { _, _ -> "artist" },
            ) { index, artist ->
                ArtistListItem(
                    artist = artist,
                    onClick = {
                        navController.navigate(artistRoute(artist.id, artist.thumbnailUrl))
                    },
                )
                if (index != displayArtists.lastIndex) HorizontalDivider()
            }
        }

        // Bottom spacer
        item(key = "bottom_spacer") {
            Spacer(Modifier.height(80.dp))
        }
    }

        // The rail indexes the combined song, album, and artist list plus its fixed rows.
        val railItemCount = displaySongs.size.coerceAtMost(LocalMusicSongRowCap) +
            displayAlbums.size + displayArtists.size + LocalMusicFixedRows
        ListScrollRail(
            sectionIndexMap = null,
            itemCount = railItemCount,
            isAtTarget = { lazyListState.firstVisibleItemIndex == it },
            scrollToItem = { lazyListState.scrollToItem(it) },
            modifier = Modifier
                .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues())
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "($count)",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SongListItem(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    com.convx.music.ui.utils.ArtworkSourceHost(Modifier) { sourceModifier ->
    val artworkOwner = com.convx.music.ui.utils.rememberArtworkSourceOwner()
    val albumArtworkId = song.album?.id ?: song.song.albumId
    Row(
        modifier = sourceModifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.thumbnailUrl)
                .size(CoilSize(96, 96))
                .build(),
            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(artworkOwner, song.thumbnailUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .sharedArtworkSource(albumArtworkId, song.thumbnailUrl, artworkOwner)
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = AppTheme.typography.bodyLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = song.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
        }

        if (song.song.duration > 0) {
            Text(
                text = formatDuration(song.song.duration),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    }
}

@Composable
private fun AlbumListItem(
    album: Album,
    onClick: () -> Unit,
) {
    com.convx.music.ui.utils.ArtworkSourceHost(Modifier) { sourceModifier ->
    val artworkOwner = com.convx.music.ui.utils.rememberArtworkSourceOwner()
    Row(
        modifier = sourceModifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.thumbnailUrl)
                .size(CoilSize(112, 112))
                .build(),
            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(artworkOwner, album.thumbnailUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .sharedArtworkSource(album.id, album.thumbnailUrl, artworkOwner)
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.title,
                style = AppTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = "${album.album.songCount} songs${if (album.album.year != null) " \u00b7 ${album.album.year}" else ""}",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
    }
}

@Composable
private fun ArtistListItem(
    artist: Artist,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artist.thumbnailUrl)
                .size(CoilSize(96, 96))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .sharedArtworkSource(artist.id, artist.thumbnailUrl)
                .size(48.dp)
                .clip(ThumbnailRoundedShape)
                .background(AppTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.artist.name,
                style = AppTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = "${artist.songCount} songs",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** The song section is capped; the rest spills into the dedicated songs screen. */
private const val LocalMusicSongRowCap = 50

/** Header, search, actions, three section headers, "more songs", bottom spacer. */
private const val LocalMusicFixedRows = 8

private enum class LocalSortMode { NAME, DURATION, RECENT }

private fun Song.localMediaId(): String = id

private fun formatDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
