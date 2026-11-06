/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.convx.music.LocalDatabase
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.ArtistSongSortType
import com.convx.music.db.entities.SpeedDialItem
import com.convx.music.db.entities.Artist
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.ArtistListItem
import com.convx.music.ui.component.CoverSearchDialog
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.utils.SongBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ArtistMenu(
    originalArtist: Artist,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val playerConnection = LocalPlayerConnection.current ?: return
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost
    val artistState = database.artist(originalArtist.id).collectAsState(initial = originalArtist)
    val artist = artistState.value ?: originalArtist
    val isPinned by database.speedDialDao.isPinned(artist.id).collectAsState(initial = false)
    var showCoverSearch by rememberSaveable { mutableStateOf(false) }
    if (showCoverSearch) {
        CoverSearchDialog(
            initialQuery = artist.artist.name,
            onDismiss = { showCoverSearch = false },
            apply = { url -> SongBundle.applyArtistCover(context, database, artist.id, url) },
        )
    }

    ArtistListItem(
        artist = artist,
        badges = {},
        trailingContent = {},
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
                actions = buildList {
                    if (!isGuest) {
                        if (artist.songCount > 0) {
                            add(
                                NewAction(
                                    icon = {
                                        SymbolIcon(symbol = sf("play.fill"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                    },
                                    text = stringResource(R.string.play),
                                    onClick = {
                                        coroutineScope.launch {
                                            val songs = withContext(Dispatchers.IO) {
                                                database
                                                    .artistSongs(artist.id, ArtistSongSortType.CREATE_DATE, true)
                                                    .first()
                                                    .map { it.toMediaItem() }
                                            }
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = artist.artist.name,
                                                    items = songs,
                                                ),
                                            )
                                        }
                                        onDismiss()
                                    }
                                )
                            )

                            add(
                                NewAction(
                                    icon = {
                                        SymbolIcon(symbol = sf("shuffle"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                    },
                                    text = stringResource(R.string.shuffle),
                                    onClick = {
                                        coroutineScope.launch {
                                            val songs = withContext(Dispatchers.IO) {
                                                database
                                                    .artistSongs(artist.id, ArtistSongSortType.CREATE_DATE, true)
                                                    .first()
                                                    .map { it.toMediaItem() }
                                                    .shuffled()
                                            }
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = artist.artist.name,
                                                    items = songs,
                                                ),
                                            )
                                        }
                                        onDismiss()
                                    }
                                )
                            )
                        }
                    }

                    add(
                        NewAction(
                            icon = {
                                SymbolIcon(symbol = if (isPinned) sf("minus") else sf("plus"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                            },
                            text = if (isPinned) "Unpin" else "Pin",
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    if (isPinned) {
                                        database.speedDialDao.delete(artist.id)
                                    } else {
                                        database.speedDialDao.insert(
                                            SpeedDialItem(
                                                id = artist.id,
                                                title = artist.artist.name,
                                                subtitle = null,
                                                thumbnailUrl = artist.artist.thumbnailUrl,
                                                type = "ARTIST"
                                            )
                                        )
                                    }
                                }
                                onDismiss()
                            }
                        )
                    )

                    if (!artist.artist.isYouTubeArtist) {
                        add(
                            NewAction(
                                icon = {
                                    SymbolIcon(symbol = sf("photo"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                },
                                text = stringResource(R.string.search_cover),
                                onClick = { showCoverSearch = true }
                            )
                        )
                        add(
                            NewAction(
                                icon = {
                                    SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                },
                                text = stringResource(R.string.artist_download_image),
                                onClick = {
                                    coroutineScope.launch {
                                        val saved = SongBundle.downloadArtistImage(context, database, artist.id, artist.artist.name)
                                        Toast.makeText(
                                            context,
                                            if (saved) R.string.artist_image_downloaded else R.string.artist_image_not_found,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                    onDismiss()
                                }
                            )
                        )
                    }

                    if (artist.artist.isYouTubeArtist) {
                        add(
                            NewAction(
                                icon = {
                                    SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                },
                                text = stringResource(R.string.share),
                                onClick = {
                                    onDismiss()
                                val intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "https://music.youtube.com/channel/${artist.id}"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(intent, null))
                                }
                            )
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                columns = if (isGuest) 1 else 3
            )
        }

        item {
            IosMenuGroup(
                items = listOf(
                    IosMenuItemData(
                        title = {
                            Text(text = if (artist.artist.bookmarkedAt != null) stringResource(R.string.subscribed) else stringResource(R.string.subscribe))
                        },
                        icon = {
                            SymbolIcon(symbol = if (artist.artist.bookmarkedAt != null) sf("bell.fill") else sf("bell.badge"), contentDescription = null)
                        },
                        onClick = {
                            database.transaction {
                                update(artist.artist.toggleLike())
                            }
                        }
                    )
                )
            )
        }
    }
}
