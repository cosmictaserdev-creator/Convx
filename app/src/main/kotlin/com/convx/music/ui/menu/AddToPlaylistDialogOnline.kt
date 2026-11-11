/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import com.convx.music.utils.listItemShape
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.convx.music.LocalDatabase
import com.convx.music.LocalSyncUtils
import com.convx.music.R
import com.convx.music.constants.AddToPlaylistSortDescendingKey
import com.convx.music.constants.AddToPlaylistSortTypeKey
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.constants.PlaylistSortType
import com.convx.music.db.entities.Song
import com.convx.music.models.toMediaMetadata
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.ListItem
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.utils.reportException
import com.convx.music.viewmodels.PlaylistsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddToPlaylistDialogOnline(
    isVisible: Boolean,
    allowSyncing: Boolean = true,
    initialTextFieldValue: String? = null,
    songs: SnapshotStateList<Song>, // list of song ids. Songs should be inserted to database in this function.
    onDismiss: () -> Unit,
    onProgressStart: (Boolean) -> Unit,
    onPercentageChange: (Int) -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val database = LocalDatabase.current.get()
    val syncUtils = LocalSyncUtils.current.get()
    val coroutineScope = rememberCoroutineScope()
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        AddToPlaylistSortTypeKey,
        PlaylistSortType.NAME
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(
        AddToPlaylistSortDescendingKey,
        false
    )
    val playlists by viewModel.allPlaylists.collectAsStateWithLifecycle()

    var showCreatePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val filteredPlaylists = remember(playlists, searchQuery) {
        if (searchQuery.isBlank()) {
            playlists
        } else {
            playlists.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (isVisible) {
        val totalItemsCount = 1 + filteredPlaylists.size
        ListDialog(
            onDismiss = onDismiss
        ) {
            item {
                ListItem(
                    title = stringResource(R.string.create_playlist),
                    thumbnailContent = {
                        Image(
                            painter = painterResource(id = R.drawable.playlist_add),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppTheme.colorScheme.onBackground),
                            modifier = Modifier.size(ListThumbnailSize)
                        )
                    },
                    shape = listItemShape(index = 0, count = totalItemsCount),
                    modifier = Modifier.clickable {
                        showCreatePlaylistDialog = true
                    }
                )
            }

            if (playlists.isNotEmpty()) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                text = stringResource(R.string.search),
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            ) 
                        },
                        leadingIcon = {
                            SymbolIcon(symbol = sf("magnifyingglass"), contentDescription = null, tint = AppTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    SymbolIcon(symbol = sf("xmark"), contentDescription = null, tint = AppTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        shape = ContinuousRoundedRectangle(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colorScheme.primary,
                            unfocusedBorderColor = AppTheme.colorScheme.outlineVariant,
                            focusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            cursorColor = AppTheme.colorScheme.primary,
                        ),
                        textStyle = AppTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            if (playlists.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp),
                    ) {
                        SortHeader(
                            sortType = sortType,
                            sortDescending = sortDescending,
                            onSortTypeChange = onSortTypeChange,
                            onSortDescendingChange = onSortDescendingChange,
                            sortTypeText = { sortType ->
                                when (sortType) {
                                    PlaylistSortType.CREATE_DATE -> R.string.sort_by_create_date
                                    PlaylistSortType.NAME -> R.string.sort_by_name
                                    PlaylistSortType.SONG_COUNT -> R.string.sort_by_song_count
                                    PlaylistSortType.LAST_UPDATED -> R.string.sort_by_last_updated
                                }
                            },
                        )
                    }
                }
            }

            if (filteredPlaylists.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_results_found),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            itemsIndexed(filteredPlaylists) { index, playlist ->
                PlaylistListItem(
                    playlist = playlist,
                    shape = listItemShape(index = index + 1, count = totalItemsCount),
                    modifier = Modifier.clickable {
                        coroutineScope.launch(Dispatchers.IO) {
                            onDismiss()
                            val songsTot = songs.count().toDouble()
                            var  songsIdx = 0.toDouble()
                            onProgressStart(true)
                            songs.reversed().forEach{
                                    song ->
                                var allArtists = ""
                                song.artists.forEach {
                                        artist ->
                                    allArtists += " ${URLDecoder.decode(artist.name, StandardCharsets.UTF_8.toString())}"
                                }
                                val query = "${song.title} - $allArtists"

                                coroutineScope.launch {
                                    try {
                                        YouTube.search(query, SearchFilter.FILTER_SONG)
                                            .onSuccess { result ->
                                                val items = result.items.distinctBy { it.id }
                                                if (items.isNotEmpty()) {
                                                    // Use this coroutine's result directly; a shared map
                                                    // allowed concurrent searches to overwrite each other.
                                                    val firstSong = items.firstOrNull() as? SongItem
                                                    if (firstSong != null) {
                                                        val firstSongMedia = firstSong.toMediaMetadata()
                                                        val ids = listOf(firstSong.id)
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                database.insert(firstSongMedia)
                                                            } catch (e: Exception) {
                                                                Timber.tag("Exception inserting song in database:")
                                                                    .e(e.toString())
                                                            }
                                                            viewModel.addSongs(playlist.id, ids)
                                                            syncUtils.uploadPlaylistToYouTube(playlist.id, ids)
                                                        }
                                                    }
                                                }
                                                songsIdx += 1
                                            }
                                            .onFailure {
                                                reportException(it)
                                                songsIdx += 1
                                            }

                                        if (songsIdx.toInt() == songsTot.toInt() - 1) {
                                            onProgressStart(false)
                                        }
                                        onPercentageChange(((songsIdx / songsTot) * 100).toInt())

                                    } catch (e: Exception){
                                        Timber.tag("ERROR").v(e.toString())
                                    }

                                }

                            }

                        }
                    }
                )
            }

            item {
                ListItem(
                    modifier = Modifier.clickable {
                        coroutineScope.launch(Dispatchers.IO) {
                            onDismiss()
                            val songsTot = songs.count().toDouble()
                            var  songsIdx = 0.toDouble()
                            onProgressStart(true)
                            songs.reversed().forEach{
                                    song ->
                                var allArtists = ""
                                song.artists.forEach {
                                        artist ->
                                    allArtists += " ${URLDecoder.decode(artist.name, StandardCharsets.UTF_8.toString())}"
                                }
                                val query = "${song.title} - $allArtists"

                                coroutineScope.launch {
                                    try {
                                        YouTube.search(query, SearchFilter.FILTER_SONG)
                                            .onSuccess { result ->
                                                val items = result.items.distinctBy { it.id }
                                                if (items.isNotEmpty()) {
                                                    // Same shared-map race as the add-to-playlist branch
                                                    // above — read this coroutine's own result directly.
                                                    val firstSong = items.firstOrNull() as? SongItem
                                                    if (firstSong != null) {
                                                        val firstSongMedia = firstSong.toMediaMetadata()
                                                        val firstSongEnt = firstSong.toMediaMetadata().toSongEntity()
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                database.insert(firstSongMedia)
                                                                database.query {
                                                                    update(firstSongEnt.toggleLike())
                                                                }
                                                            } catch (e: Exception) {
                                                                Timber.tag("Exception inserting song in database:")
                                                                    .e(e.toString())
                                                            }
                                                        }
                                                    }
                                                }
                                                songsIdx += 1
                                            }
                                            .onFailure {
                                                reportException(it)
                                                songsIdx += 1
                                            }

                                        if (songsIdx.toInt() == songsTot.toInt() - 1) {
                                            onProgressStart(false)
                                        }
                                        onPercentageChange(((songsIdx / songsTot) * 100).toInt())

                                    } catch (e: Exception){
                                        Timber.tag("ERROR").v(e.toString())
                                    }

                                }

                            }

                        }
                    },
                    title = stringResource(R.string.liked_songs),
                    thumbnailContent = {
                        Image(
                            painter = painterResource(id = R.drawable.favorite),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(AppTheme.colorScheme.onBackground),
                        )
                    },
                    trailingContent = {}
                )
            }

            item {
                Text(
                    text = stringResource(R.string.playlist_add_local_to_synced_note),
                    fontSize = TextUnit(12F, TextUnitType.Sp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            initialTextFieldValue = initialTextFieldValue,
            allowSyncing = allowSyncing
        )
    }

}
