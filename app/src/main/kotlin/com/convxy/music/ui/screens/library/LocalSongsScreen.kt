/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.LocalSongSortDescendingKey
import com.convxy.music.constants.LocalSongSortTypeKey
import com.convxy.music.constants.SongSortType
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.HideOnScrollFAB
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortOption
import com.convxy.music.ui.component.SortPopupButton
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference

/**
 * Every song on the device, in one flat list.
 *
 * This is the tab local-only mode adds. The library's own Songs screen still exists and
 * still has its filter chips; this one deliberately has none — it is the whole on-device
 * library and nothing else, which is what makes the alphabet rail meaningful here.
 *
 * Sorting happens in memory rather than by re-querying: the local library is already a
 * list in the view model, so a sort change is a comparator over a few thousand rows
 * instead of a database round-trip and a fresh Flow emission.
 */
@Composable
fun LocalSongsScreen(
    navController: NavController,
    viewModel: LocalMusicViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

    // Shares the keys the local Home rows use, so changing the sort in one place is not
    // contradicted by the other.
    val (sortType, onSortTypeChange) = rememberEnumPreference(LocalSongSortTypeKey, SongSortType.NAME)
    val (sortDescending, onSortDescendingChange) = rememberPreference(LocalSongSortDescendingKey, false)

    // The view model hands over name-ascending rows; every other order is derived here.
    val sortedSongs = remember(songs, sortType, sortDescending) {
        val ordered = when (sortType) {
            SongSortType.NAME -> songs
            SongSortType.ARTIST -> songs.sortedBy { song ->
                song.artists.firstOrNull()?.name?.lowercase().orEmpty()
            }

            SongSortType.CREATE_DATE -> songs.sortedBy { it.song.dateModified }
            SongSortType.PLAY_TIME -> songs.sortedBy { it.song.totalPlayTime }
        }
        if (sortDescending) ordered.asReversed() else ordered
    }

    // Entering the tab is the natural moment to pick up files added since last time.
    // force = false, so bouncing to the player and back does not re-sweep MediaStore.
    LaunchedEffect(Unit) { viewModel.scanDevice(context, force = false) }

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            item(key = "title", contentType = "header") {
                LargeScreenTitle(title = stringResource(R.string.songs))
            }

            item(key = "header", contentType = "header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = AppleTokens.Gutter),
                ) {
                    Text(
                        text = pluralStringResource(R.plurals.n_song, songs.size, songs.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    Spacer(Modifier.weight(1f))

                    SortPopupButton(
                        options = LocalSongSortOptions,
                        selected = sortType,
                        descending = sortDescending,
                        onSelectedChange = onSortTypeChange,
                        onDescendingChange = onSortDescendingChange,
                    )
                }
            }

            if (songs.isEmpty()) {
                item(key = "empty", contentType = "header") {
                    Text(
                        text = stringResource(
                            if (isScanning) R.string.scanning_local_files else R.string.no_local_files,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppleTokens.Gutter),
                    )
                }
            }

            itemsIndexed(
                items = sortedSongs,
                key = { _, song -> song.id },
                contentType = { _, _ -> "song" },
            ) { index, song ->
                SongListItem(
                    song = song,
                    isActive = song.id == mediaMetadata?.id,
                    isPlaying = isPlaying,
                    // On-device files carry neither a like state nor a download state.
                    showLikedIcon = false,
                    showDownloadIcon = false,
                    shape = listItemShape(index, sortedSongs.size),
                    trailingContent = {
                        IconButton(
                            onClick = {
                                menuState.show {
                                    SongMenu(
                                        originalSong = song,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.more_vert),
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            if (song.id == mediaMetadata?.id) {
                                playerConnection.togglePlayPause()
                            } else {
                                playerConnection.playQueue(
                                    ListQueue(
                                        title = context.getString(R.string.songs),
                                        items = sortedSongs.map { it.toMediaItem() },
                                        startIndex = index,
                                    ),
                                )
                            }
                        },
                )
            }
        }

        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = sortedSongs.size,
            sectionIndexMap = when (sortType) {
                SongSortType.NAME ->
                    remember(sortedSongs) {
                        buildAlphabetSectionIndex(sortedSongs) { it.title }
                    }

                SongSortType.ARTIST ->
                    remember(sortedSongs) {
                        buildAlphabetSectionIndex(sortedSongs) { song ->
                            song.artists.firstOrNull()?.name.orEmpty()
                        }
                    }

                else -> null
            },
        )

        HideOnScrollFAB(
            visible = sortedSongs.isNotEmpty(),
            lazyListState = lazyListState,
            icon = R.drawable.shuffle,
            onClick = {
                playerConnection.playQueue(
                    ListQueue(
                        title = context.getString(R.string.songs),
                        items = sortedSongs.shuffled().map { it.toMediaItem() },
                    ),
                )
            },
        )
    }
}

private val LocalSongSortOptions = listOf(
    SortOption(SongSortType.NAME, R.string.sort_by_name),
    SortOption(SongSortType.ARTIST, R.string.sort_by_artist),
    SortOption(SongSortType.CREATE_DATE, R.string.sort_by_create_date),
    SortOption(SongSortType.PLAY_TIME, R.string.sort_by_play_time),
)
