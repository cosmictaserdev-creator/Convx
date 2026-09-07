/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.library

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.CONTENT_TYPE_HEADER
import com.convxy.music.constants.CONTENT_TYPE_SONG
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.constants.LibraryIconsOnlyKey
import com.convxy.music.constants.SongFilter
import com.convxy.music.constants.SongFilterKey
import com.convxy.music.constants.LocalOnlyModeKey
import com.convxy.music.constants.SongSortDescendingKey
import com.convxy.music.constants.SongSortType
import com.convxy.music.constants.SongSortTypeKey
import com.convxy.music.constants.YtmSyncKey
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.buildAlphabetSectionIndex
import com.convxy.music.ui.component.ChipsRow
import com.convxy.music.ui.component.HideOnScrollFAB
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.component.SortOption
import com.convxy.music.ui.component.SortPopupButton
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.utils.listItemShape
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.LibrarySongsViewModel
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.heroPullZoom
import com.convxy.music.ui.utils.listOverscroll
import com.convxy.music.ui.utils.rememberHeroZoom

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibrarySongsScreen(
    navController: NavController,
    onDeselect: () -> Unit,
    viewModel: LibrarySongsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val (sortType, onSortTypeChange) = rememberEnumPreference(
        SongSortTypeKey,
        SongSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(SongSortDescendingKey, true)

    val (ytmSync) = rememberPreference(YtmSyncKey, true)
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)
    val (libraryIconsOnly) = rememberPreference(LibraryIconsOnlyKey, defaultValue = true)

    val songs by viewModel.allSongs.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()

    var storedFilter by rememberEnumPreference(SongFilterKey, SongFilter.LIKED)
    val (localOnly) = rememberPreference(LocalOnlyModeKey, false)
    // Mirrors what the view model actually queries while local-only mode is on.
    val filter = if (localOnly) SongFilter.LOCAL else storedFilter
    // Pull-to-refresh only: no hero artwork on this screen, so heroZoom.scale
    // goes unread and the modifier contributes just the rubber-band stretch.
    val heroZoom = rememberHeroZoom()


    var hasStoragePermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStoragePermission = granted
    }

    LaunchedEffect(Unit) {
        if (ytmSync) {
            when (filter) {
                SongFilter.LIKED -> viewModel.syncLikedSongs()
                SongFilter.LIBRARY -> viewModel.syncLibrarySongs()
                SongFilter.UPLOADED -> viewModel.syncUploadedSongs()
                else -> return@LaunchedEffect
            }
        }
    }

    LaunchedEffect(filter) {
        if (filter == SongFilter.LOCAL) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            hasStoragePermission = context.checkSelfPermission(permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    val lazyListState = rememberLazyListState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            lazyListState.animateScrollToItem(0)
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    val filteredSongs = remember(songs, hideExplicit) {
        if (hideExplicit) songs.filter { !it.song.explicit } else songs
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = lazyListState,
            overscrollEffect = heroZoom.listOverscroll(),
            modifier = Modifier.heroPullZoom(heroZoom),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            item(
                key = "title",
                contentType = CONTENT_TYPE_HEADER,
            ) {
                LargeScreenTitle(title = stringResource(R.string.songs))
            }

            item(
                key = "filter",
                contentType = CONTENT_TYPE_HEADER,
            ) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    FilterChip(
                        label = { Text(stringResource(R.string.songs)) },
                        selected = true,
                        colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface),
                        onClick = onDeselect,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = ""
                            )
                        },
                    )
                    // Local-only mode pins the filter to LOCAL in the view model, so
                    // the chips would be inert controls promising something else.
                    if (!localOnly) {
                        ChipsRow(
                            chips =
                            listOf(
                                SongFilter.LIKED to stringResource(R.string.filter_liked),
                                SongFilter.LIBRARY to stringResource(R.string.filter_library),
                                SongFilter.UPLOADED to stringResource(R.string.filter_uploaded),
                                SongFilter.DOWNLOADED to stringResource(R.string.filter_downloaded),
                                SongFilter.LOCAL to stringResource(R.string.filter_local),
                            ),
                            currentValue = filter,
                            onValueUpdate = {
                                storedFilter = it
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            if (filter == SongFilter.LOCAL && filteredSongs.isEmpty() && !isScanning) {
                item(
                    key = "local_empty",
                    contentType = CONTENT_TYPE_HEADER,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (!hasStoragePermission) {
                            Text(
                                text = stringResource(R.string.local_audio_permission_needed),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(16.dp))
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
                                Text(stringResource(R.string.enable))
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.no_local_files),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.scanLocalFiles() },
                            ) {
                                Text(stringResource(R.string.scan_local_files))
                            }
                        }
                    }
                }
            }

            if (filter == SongFilter.LOCAL && isScanning) {
                item(
                    key = "local_scanning",
                    contentType = CONTENT_TYPE_HEADER,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.scanning_local_files),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Rescan is available whenever local files are already present —
            // previously the only scan trigger was the empty-state button, so
            // once anything was found there was no way to pick up newly added
            // songs.
            if (filter == SongFilter.LOCAL && hasStoragePermission &&
                filteredSongs.isNotEmpty() && !isScanning
            ) {
                item(
                    key = "local_rescan",
                    contentType = CONTENT_TYPE_HEADER,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        scanResult?.let { result ->
                            Text(
                                text = stringResource(
                                    R.string.local_files_found,
                                    result.totalFound
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { viewModel.scanLocalFiles() }) {
                            Icon(
                                painter = painterResource(R.drawable.refresh),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.scan_local_files))
                        }
                    }
                }
            }

            item(
                key = "header",
                contentType = CONTENT_TYPE_HEADER,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = AppleTokens.Gutter,
                        end = AppleTokens.Gutter - 8.dp,
                    ),
                ) {
                    // The current sort reads from this label rather than from the control,
                    // which is what lets the control itself shrink to an icon.
                    Text(
                        text = stringResource(
                            when (sortType) {
                                SongSortType.CREATE_DATE -> R.string.sort_by_create_date
                                SongSortType.NAME -> R.string.sort_by_name
                                SongSortType.ARTIST -> R.string.sort_by_artist
                                SongSortType.PLAY_TIME -> R.string.sort_by_play_time
                            }
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = pluralStringResource(
                            R.plurals.n_song,
                            filteredSongs.size,
                            filteredSongs.size
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    SortPopupButton(
                        options = songSortOptions,
                        selected = sortType,
                        descending = sortDescending,
                        onSelectedChange = onSortTypeChange,
                        onDescendingChange = onSortDescendingChange,
                    )
                }
            }

            itemsIndexed(
                items = filteredSongs,
                key = { _, item -> item.song.id },
                contentType = { _, _ -> CONTENT_TYPE_SONG },
            ) { index, song ->
                SongListItem(
                    song = song,
                    showInLibraryIcon = true,
                    isActive = song.id == mediaMetadata?.id,
                    isPlaying = isPlaying,
                    showIconOnly = libraryIconsOnly,
                    showLikedIcon = filter != SongFilter.LOCAL,
                    showDownloadIcon = filter != SongFilter.DOWNLOADED && filter != SongFilter.LOCAL,
                    shape = listItemShape(index, filteredSongs.size),
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
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            if (song.id == mediaMetadata?.id) {
                                playerConnection.togglePlayPause()
                            } else {
                                playerConnection.playQueue(
                                    ListQueue(
                                        title = context.getString(R.string.queue_all_songs),
                                        items = filteredSongs.map { it.toMediaItem() },
                                        startIndex = index,
                                    ),
                                )
                            }
                        }
                )
            }
        }

        // Fast-scrub rail. Present at every sort: letters when the list is ordered by
        // name, a proportional thumb otherwise. A scrubber that vanishes when you change
        // the sort order is one nobody can rely on.
        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = filteredSongs.size,
            sectionIndexMap = if (sortType == SongSortType.NAME) {
                remember(filteredSongs) { buildAlphabetSectionIndex(filteredSongs) { it.title } }
            } else {
                null
            },
        )

        HideOnScrollFAB(
            visible = filteredSongs.isNotEmpty(),
            lazyListState = lazyListState,
            icon = R.drawable.shuffle,
            onClick = {
                playerConnection.playQueue(
                    ListQueue(
                        title = context.getString(R.string.queue_all_songs),
                        items = filteredSongs.shuffled().map { it.toMediaItem() },
                    ),
                )
            },
        )
    }
}

private val songSortOptions = listOf(
    SortOption(SongSortType.CREATE_DATE, R.string.sort_by_create_date),
    SortOption(SongSortType.NAME, R.string.sort_by_name),
    SortOption(SongSortType.ARTIST, R.string.sort_by_artist),
    SortOption(SongSortType.PLAY_TIME, R.string.sort_by_play_time),
)
