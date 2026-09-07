/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.library

import androidx.compose.foundation.lazy.rememberLazyListState
import com.convxy.music.ui.component.ListScrollRail
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.theme.rememberGlobalAccentColors
import com.convxy.music.utils.LocalFolderIndex
import com.convxy.music.utils.listItemShape
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
    val database = LocalDatabase.current
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

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
    ) {
        item(key = "title") {
            Text(
                text = path.substringAfterLast('/'),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
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
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(items = songs.map { it.toMediaItem() }, startIndex = 0),
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = onAccentColor,
                        ),
                    ) { Text(stringResource(R.string.play)) }
                    Button(
                        onClick = {
                            playerConnection.playQueue(
                                ListQueue(items = songs.shuffled().map { it.toMediaItem() }, startIndex = 0),
                            )
                        },
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
                        Icon(painter = painterResource(R.drawable.more_vert), contentDescription = null)
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
