/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.screens.library

import androidx.compose.foundation.background
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.composables.sfsymbols.SfSymbols
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
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.LocalSongSortDescendingKey
import com.convx.music.constants.LocalSongSortTypeKey
import com.convx.music.constants.SongSortType
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.TopFadeBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.SortOption
import com.convx.music.ui.component.SortPopupButton
import com.convx.music.ui.component.buildAlphabetSectionIndex
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.bounceClick
import com.convx.music.utils.listItemShape
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference

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

    val folders by viewModel.folders.collectAsStateWithLifecycle()
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
    val audioPermission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    LaunchedEffect(audioPermission.granted) {
        if (audioPermission.granted) viewModel.scanDevice(context, force = false)
    }
    val visibleSongs = if (audioPermission.granted) sortedSongs else emptyList()
    val sectionIndexMap = remember(sortedSongs, sortType) {
        when (sortType) {
            SongSortType.NAME -> buildAlphabetSectionIndex(sortedSongs) { it.title }
            SongSortType.ARTIST -> buildAlphabetSectionIndex(sortedSongs) { song ->
                song.artists.firstOrNull()?.name.orEmpty()
            }

            else -> null
        }
    }

    val lazyListState = rememberLazyListState()
    // Progressive top blur so the list dissolves under the status bar instead
    // of cutting off hard — same technique as the rest of the app's screens.
    val hazeState = remember { HazeState() }
    val pageInsets = LocalPlayerAwareWindowInsets.current.asPaddingValues()
    val sideInset = pageInsets.calculateStartPadding(androidx.compose.ui.platform.LocalLayoutDirection.current)

    // The list reads its order at tap time, not from whatever index a row was composed with, so a sort
    // change can never start the queue from the previous order.
    val currentSongs by androidx.compose.runtime.rememberUpdatedState(sortedSongs)

    // This tab has no page behind it in local mode, so it paints its own light or dark background.
    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colorScheme.background)) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = sideInset,
                top = maxOf(pageInsets.calculateTopPadding(), if (sideInset > 0.dp) 96.dp else 116.dp),
                end = pageInsets.calculateEndPadding(androidx.compose.ui.platform.LocalLayoutDirection.current),
                bottom = pageInsets.calculateBottomPadding(),
            ),
        ) {
            item(key = "header", contentType = "header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = AppleTokens.Gutter),
                ) {
                    Text(
                        text = pluralStringResource(R.plurals.n_song, songs.size, songs.size),
                        style = AppTheme.typography.titleSmall,
                        color = AppTheme.colorScheme.secondary,
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

            if (!audioPermission.granted) {
                item(key = "permission", contentType = "local_access") {
                    com.convx.music.ui.screens.library.apple.LocalAccessCard(
                        blocked = audioPermission.blocked,
                        onAllow = audioPermission.request,
                    )
                }
            } else if (songs.isEmpty() && !isScanning) {
                item(key = "empty", contentType = "local_access") {
                    com.convx.music.ui.component.LocalMusicEmptyState(onScan = { viewModel.scanDevice(context) })
                }
            } else if (songs.isEmpty()) {
                item(key = "empty", contentType = "header") {
                    Text(
                        text = stringResource(
                            if (isScanning) R.string.scanning_local_files else R.string.no_local_files,
                        ),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppleTokens.Gutter),
                    )
                }
            }

            if (audioPermission.granted) {
                item(key = "folders_entry") {
                    LocalMusicFoldersEntry(count = folders.size, navController = navController)
                }
            }
            itemsIndexed(
                items = visibleSongs,
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
                            SymbolIcon(bounceOnEnter = true, symbol = sf("ellipsis"), contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            if (song.id == mediaMetadata?.id) {
                                playerConnection.togglePlayPause()
                            } else {
                                val ordered = currentSongs
                                playerConnection.playQueue(
                                    ListQueue(
                                        title = context.getString(R.string.songs),
                                        items = ordered.map { it.toMediaItem() },
                                        startIndex = ordered.indexOfFirst { it.id == song.id }.coerceAtLeast(0),
                                    ),
                                )
                            }
                        },
                )
            }
        }

        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = visibleSongs.size,
            sectionIndexMap = sectionIndexMap,
        )

        HideOnScrollFAB(
            visible = visibleSongs.isNotEmpty(),
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
            hazeState = hazeState,
        )
    }
}

private val LocalSongSortOptions = listOf(
    SortOption(SongSortType.NAME, R.string.sort_by_name),
    SortOption(SongSortType.ARTIST, R.string.sort_by_artist),
    SortOption(SongSortType.CREATE_DATE, R.string.sort_by_create_date),
    SortOption(SongSortType.PLAY_TIME, R.string.sort_by_play_time),
)
