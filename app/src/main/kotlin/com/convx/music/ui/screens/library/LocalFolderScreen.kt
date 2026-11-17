/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.lazy.rememberLazyListState
import com.convx.music.ui.component.ScreenScaffold
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.component.ListScrollRail
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ButtonDefaults
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.composables.sfsymbols.SfSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.LocalDatabase
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.component.SeededShimmerHero
import com.convx.music.ui.menu.SongMenu
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.theme.rememberGlobalAccentColors
import com.convx.music.utils.LocalFolderIndex
import com.convx.music.utils.listItemShape
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.text.font.FontWeight

/**
 * Every scanned song that lives in one on-device folder.
 *
 * The folder membership comes from MediaStore (the song table has no path
 * column); the rows themselves come from Room by id, so downloads, likes and
 * menus behave exactly as they do everywhere else.
 */
@Composable
fun LocalFolderScreen(
    navController: NavController,
    path: String,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val (accentColor, onAccentColor) = rememberGlobalAccentColors()

    val songs by produceState(initialValue = emptyList<Song>(), path) {
        val ids = LocalFolderIndex.songIdsFor(context, path)
        if (ids.isEmpty()) {
            value = emptyList()
            return@produceState
        }
        // MediaStore's order is the folder's order; Room's IN query is not, so
        // restore it rather than showing an arbitrary shuffle.
        val order = ids.withIndex().associate { (index, id) -> id to index }
        database.songsByIds(ids).collect { rows ->
            value = rows.sortedBy { order[it.id] ?: Int.MAX_VALUE }
        }
    }

    val lazyListState = rememberLazyListState()

    fun playFolder(shuffle: Boolean) {
        val queueSongs = if (shuffle) songs.shuffled() else songs
        playerConnection.playQueue(
            ListQueue(items = queueSongs.map { it.toMediaItem() }, startIndex = 0),
        )
    }

    val titleProgress = rememberTitleCollapseProgressState(lazyListState, titleItemKey = "title")
    ScreenScaffold(
        title = path.substringAfterLast('/'),
        titleAlpha = 0f,
        titleAlphaProvider = { titleProgress.value },
        onBack = { navController.navigateUp() },
    ) { chrome ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().then(chrome.contentModifier()),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            if (songs.isNotEmpty()) {
                item(key = "folder_artwork") {
                    SeededShimmerHero(
                        thumbUrl = songs.firstOrNull()?.thumbnailUrl,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
            }

            item(key = "title") {
                Text(
                    text = path.substringAfterLast('/'),
                    style = AppTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            if (songs.isNotEmpty()) {
                item(key = "actions") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = { playFolder(shuffle = false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = onAccentColor,
                            ),
                        ) { Text(stringResource(R.string.play)) }
                        Button(
                            onClick = { playFolder(shuffle = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = onAccentColor,
                            ),
                        ) { Text(stringResource(R.string.shuffle)) }
                    }
                }
            }

            itemsIndexed(
                items = songs,
                key = { _, song -> song.id },
            ) { index, song ->
                SongListItem(
                    song = song,
                    isActive = song.id == mediaMetadata?.id,
                    isPlaying = isPlaying,
                    showLikedIcon = false,
                    showDownloadIcon = false,
                    shape = listItemShape(index, songs.size),
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
                                playerConnection.playQueue(
                                    ListQueue(items = songs.map { it.toMediaItem() }, startIndex = index),
                                )
                            }
                        },
                )
            }
        }

        // The folder keeps MediaStore's own order, which is not alphabetical, so the rail
        // is a proportional thumb.
        ListScrollRail(
            lazyListState = lazyListState,
            itemCount = songs.size,
            sectionIndexMap = null,
        )
    }
}
