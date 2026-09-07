package com.convxy.music.ui.screens.library

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.convxy.music.constants.ThumbnailRoundedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.theme.rememberGlobalAccentColors
import com.convxy.music.db.entities.Album
import com.convxy.music.db.entities.Artist
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.menu.SongMenu
import java.time.LocalDateTime

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

    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()

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
                    // "Recently added" means MediaStore's DATE_ADDED, which the scanner
                    // stores in inLibrary — not dateModified, which is the file's mtime.
                    // Copying a music folder onto the device preserves the source files'
                    // mtimes, so sorting on it ordered songs by when they were authored
                    // rather than when they landed on the phone. dateModified stays as a
                    // fallback for rows written before the scanner recorded DATE_ADDED.
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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStoragePermission = granted
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        // Check if already granted
        hasStoragePermission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            else ->
                context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    // Entering the screen refreshes the library instead of leaving it to the Rescan
    // button. force = false so this only fires when the last scan has gone stale —
    // navigating to the player and back must not restart a full MediaStore sweep.
    LaunchedEffect(hasStoragePermission) {
        if (hasStoragePermission) viewModel.scanDevice(context, force = false)
    }

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
    ) {
        // Header
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
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    if (hasStoragePermission) {
                        // Search
                        IconButton(
                            onClick = {
                                showSearch = !showSearch
                                if (!showSearch) searchQuery = ""
                            },
                        ) {
                            Icon(
                                painter = painterResource(if (showSearch) R.drawable.close else R.drawable.search),
                                contentDescription = "Search",
                            )
                        }
                        // Sort
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.list),
                                    contentDescription = "Sort",
                                )
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
                                Icon(
                                    painter = painterResource(R.drawable.refresh),
                                    contentDescription = "Rescan",
                                )
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Grant permission to access your music library",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_AUDIO
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            permissionLauncher.launch(permission)
                        },
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        } else if (songs.isEmpty() && !isScanning) {
            item(key = "empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "No local music found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.scanDevice(context) },
                    ) {
                        Text("Scan Device")
                    }
                }
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedButton(
                        onClick = { viewModel.scanDevice(context) },
                    ) {
                        Text("Rescan", style = MaterialTheme.typography.labelMedium)
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
                        Icon(
                            painter = painterResource(R.drawable.play),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
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
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Shuffle")
                    }
                }
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
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
                        navController.navigate("album/${album.id}")
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
                        navController.navigate("artist/${artist.id}")
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

        // This screen stacks three sections in one column, so the rail scrubs raw lazy
        // indices rather than one block's indices with a header offset. The count is
        // derived from the data plus the fixed rows around it; a proportional rail maps
        // a fraction of the rail to a fraction of the list, so being a row or two out
        // near the very bottom costs nothing, and an over-long index is caught by
        // scrollToItem's own guards.
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SongListItem(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.thumbnailUrl)
                .size(CoilSize(96, 96))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = song.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
        }

        // Duration
        if (song.song.duration > 0) {
            Text(
                text = formatDuration(song.song.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AlbumListItem(
    album: Album,
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
                .data(album.thumbnailUrl)
                .size(CoilSize(112, 112))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = "${album.album.songCount} songs${if (album.album.year != null) " \u00b7 ${album.album.year}" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
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
                .size(48.dp)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.artist.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = "${artist.songCount} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
