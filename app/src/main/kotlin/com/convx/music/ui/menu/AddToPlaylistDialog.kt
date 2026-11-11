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
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.music.innertube.YouTube
import com.music.innertube.utils.parseCookieString
import com.convx.music.LocalDatabase
import com.convx.music.R
import com.convx.music.constants.AddToPlaylistSortDescendingKey
import com.convx.music.constants.AddToPlaylistSortTypeKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.constants.PlaylistSortType
import com.convx.music.db.entities.Playlist
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.ListItem
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.PlaylistsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddToPlaylistDialog(
    isVisible: Boolean,
    allowSyncing: Boolean = true,
    initialTextFieldValue: String? = null,
    onGetSong: suspend (Playlist) -> List<String>, // list of song ids. Songs should be inserted to database in this function.
    onDismiss: () -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val database = LocalDatabase.current.get()
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
    val (innerTubeCookie) = rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    var showCreatePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showDuplicateDialog by remember {
        mutableStateOf(false)
    }
    var selectedPlaylistId by remember {
        mutableStateOf<String?>(null)
    }
    var songIds by remember {
        mutableStateOf<List<String>?>(null) // list is not saveable
    }
    var duplicates by remember {
        mutableStateOf(emptyList<String>())
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
            onDismiss = onDismiss,
        ) {
            item {
                ListItem(
                    title = stringResource(R.string.create_playlist),
                    thumbnailContent = {
                        Image(
                            painter = painterResource(R.drawable.add),
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
                        selectedPlaylistId = playlist.id
                        coroutineScope.launch(Dispatchers.IO) {
                            val roomPlaylist = database.playlist(playlist.id).firstOrNull() ?: return@launch
                            val playlistSongIds = songIds ?: onGetSong(roomPlaylist).also { songIds = it }
                            duplicates = viewModel.duplicateSongIds(playlist.id, playlistSongIds)
                            if (duplicates.isNotEmpty()) {
                                showDuplicateDialog = true
                            } else {
                                onDismiss()
                                viewModel.addSongs(playlist.id, playlistSongIds)

                                roomPlaylist.playlist.browseId?.let { plist ->
                                    playlistSongIds.forEach {
                                        YouTube.addToPlaylist(plist, it)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            initialTextFieldValue = initialTextFieldValue,
            allowSyncing = allowSyncing,
            onPlaylistCreated = { playlistId ->
                coroutineScope.launch(Dispatchers.IO) {
                    val newPlaylist = database.playlist(playlistId).first() ?: return@launch
                    val ids = songIds ?: onGetSong(newPlaylist)
                    viewModel.addSongs(newPlaylist.id, ids)
                    // A synced playlist is empty on YouTube; without this the next sync drops the songs.
                    newPlaylist.playlist.browseId?.let { remote -> ids.forEach { YouTube.addToPlaylist(remote, it) } }
                    onDismiss()
                }
            }
        )
    }

    // duplicate songs warning
        if (showDuplicateDialog) {
            DefaultDialog(
                title = { Text(stringResource(R.string.duplicates)) },
                buttons = {
                    TextButton(
                        onClick = {
                            showDuplicateDialog = false
                            onDismiss()
                            selectedPlaylistId?.let { playlistId ->
                                songIds?.let { playlistSongIds ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        viewModel.addSongs(
                                            playlistId,
                                            playlistSongIds.filterNot { it in duplicates },
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.skip_duplicates))
                    }

                    TextButton(
                        onClick = {
                            showDuplicateDialog = false
                            onDismiss()
                            selectedPlaylistId?.let { playlistId ->
                                songIds?.let { playlistSongIds ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        viewModel.addSongs(playlistId, playlistSongIds)
                                    }
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.add_anyway))
                    }

                    TextButton(
                        onClick = {
                            showDuplicateDialog = false
                        }
                    ) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                onDismiss = {
                    showDuplicateDialog = false
                }
            ) {
                Text(
                    text = if (duplicates.size == 1) {
                        stringResource(R.string.duplicates_description_single)
                    } else {
                        stringResource(R.string.duplicates_description_multiple, duplicates.size)
                    },
                    textAlign = TextAlign.Start,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
}
