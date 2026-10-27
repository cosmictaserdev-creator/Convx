/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import com.convx.music.ui.ios.compat.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.WatchEndpoint
import com.convx.music.R
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.library.PlaylistCardData
import com.convx.music.library.PlaylistDetail
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.localPlaylistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.ArtistMenu
import com.convx.music.ui.menu.PlaylistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LibraryArtistListItem(
    navController: NavController,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    artist: Artist,
    modifier: Modifier = Modifier,
    showIconOnly: Boolean = true,
) = ArtistListItem(
    artist = artist,
    showIconOnly = showIconOnly,
    trailingContent = {
        androidx.compose.material3.IconButton(
            onClick = {
                menuState.show {
                    ArtistMenu(
                        originalArtist = artist,
                        coroutineScope = coroutineScope,
                        onDismiss = menuState::dismiss
                    )
                }
            }
        ) {
            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
        }
    },
    modifier = modifier
        .fillMaxWidth()
        .clickable {
            navController.navigate(artistRoute(artist.id, artist.thumbnailUrl))
        }
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryArtistGridItem(
    navController: NavController,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    artist: Artist,
    modifier: Modifier = Modifier,
    showIconOnly: Boolean = true,
) = ArtistGridItem(
    artist = artist,
    fillMaxWidth = true,
    showIconOnly = showIconOnly,
    modifier = modifier
        .fillMaxWidth()
        .combinedClickable(
            onClick = {
                navController.navigate(artistRoute(artist.id, artist.thumbnailUrl))
            },
            onLongClick = {
                menuState.show {
                    ArtistMenu(
                        originalArtist = artist,
                        coroutineScope = coroutineScope,
                        onDismiss = menuState::dismiss
                    )
                }
            }
        )
)

@Composable
fun LibraryAlbumListItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    menuState: MenuState,
    album: Album,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    showIconOnly: Boolean = true,
) = AlbumListItem(
    album = album,
    isActive = isActive,
    isPlaying = isPlaying,
    showIconOnly = showIconOnly,
    trailingContent = {
        androidx.compose.material3.IconButton(
            onClick = {
                menuState.show {
                    AlbumMenu(
                        originalAlbum = album,
                        navController = navController,
                        onDismiss = menuState::dismiss
                    )
                }
            }
        ) {
            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
        }
    },
    modifier = modifier
        .fillMaxWidth()
        .clickable {
            navController.navigate(albumRoute(album.id, album.thumbnailUrl))
        }
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryAlbumGridItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    album: Album,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    showIconOnly: Boolean = true,
) = AlbumGridItem(
    album = album,
    isActive = isActive,
    isPlaying = isPlaying,
    coroutineScope = coroutineScope,
    fillMaxWidth = true,
    showIconOnly = showIconOnly,
    modifier = modifier
        .fillMaxWidth()
        .combinedClickable(
            onClick = {
                navController.navigate(albumRoute(album.id, album.thumbnailUrl))
            },
            onLongClick = {
                menuState.show {
                    AlbumMenu(
                        originalAlbum = album,
                        navController = navController,
                        onDismiss = menuState::dismiss
                    )
                }
            }
        )
)

@Composable
fun LibraryPlaylistListItem(
    navController: NavController,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    playlist: PlaylistCardData,
    loadPlaylist: suspend (String) -> PlaylistDetail?,
    modifier: Modifier = Modifier,
    showIconOnly: Boolean = true,
) {
    val openMenu = rememberLibraryPlaylistMenuAction(playlist.id, menuState, coroutineScope, loadPlaylist)
    val isRemoteOnly = !playlist.isEditable && playlist.songCount == 0 && playlist.remoteSongCount != 0

    PlaylistListItem(
        playlist = playlist,
        showIconOnly = showIconOnly,
        sharedArtworkId = if (isRemoteOnly) playlist.browseId else playlist.id,
        trailingContent = {
            androidx.compose.material3.IconButton(onClick = openMenu) {
                SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isRemoteOnly) {
                    navController.navigate(
                        onlinePlaylistRoute(
                            playlist.browseId.orEmpty(),
                            playlist.name,
                            playlist.sourceThumbnailUrl,
                        ),
                    )
                } else {
                    navController.navigate(localPlaylistRoute(playlist.id, null))
                }
            },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryPlaylistGridItem(
    navController: NavController,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    playlist: PlaylistCardData,
    loadPlaylist: suspend (String) -> PlaylistDetail?,
    modifier: Modifier = Modifier,
    showIconOnly: Boolean = true,
) {
    val openMenu = rememberLibraryPlaylistMenuAction(playlist.id, menuState, coroutineScope, loadPlaylist)
    val isRemoteOnly = !playlist.isEditable && playlist.songCount == 0 && playlist.remoteSongCount != 0

    PlaylistGridItem(
        playlist = playlist,
        fillMaxWidth = true,
        showIconOnly = showIconOnly,
        sharedArtworkId = if (isRemoteOnly) playlist.browseId else playlist.id,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isRemoteOnly) {
                        navController.navigate(
                            onlinePlaylistRoute(
                                playlist.browseId.orEmpty(),
                                playlist.name,
                                playlist.sourceThumbnailUrl,
                            ),
                        )
                    } else {
                        navController.navigate(localPlaylistRoute(playlist.id, null))
                    }
                },
                onLongClick = openMenu,
            ),
    )
}

@Composable
internal fun rememberLibraryPlaylistMenuAction(
    playlistId: String,
    menuState: MenuState,
    coroutineScope: CoroutineScope,
    loadPlaylist: suspend (String) -> PlaylistDetail?,
): () -> Unit {
    return androidx.compose.runtime.remember(playlistId, menuState, coroutineScope, loadPlaylist) {
        {
            coroutineScope.launch {
                val playlist = loadPlaylist(playlistId) ?: return@launch
                menuState.show { LibraryPlaylistMenuContent(playlist, coroutineScope, menuState) }
            }
        }
    }
}

@Composable
internal fun LibraryPlaylistMenuContent(
    playlist: PlaylistDetail,
    coroutineScope: CoroutineScope,
    menuState: MenuState,
) {
    if (playlist.isEditable || playlist.songCount != 0) {
        PlaylistMenu(
            playlist = playlist,
            coroutineScope = coroutineScope,
            onDismiss = menuState::dismiss,
        )
    } else {
        playlist.browseId?.let { browseId ->
            YouTubePlaylistMenu(
                playlist = PlaylistItem(
                    id = browseId,
                    title = playlist.name,
                    author = null,
                    songCountText = null,
                    thumbnail = playlist.artworkUrls.getOrNull(0) ?: "",
                    playEndpoint = WatchEndpoint(
                        playlistId = browseId,
                        params = playlist.playEndpointParams,
                    ),
                    shuffleEndpoint = WatchEndpoint(
                        playlistId = browseId,
                        params = playlist.shuffleEndpointParams,
                    ),
                    radioEndpoint = WatchEndpoint(
                        playlistId = "RDAMPL$browseId",
                        params = playlist.radioEndpointParams,
                    ),
                    isEditable = false,
                ),
                coroutineScope = coroutineScope,
                onDismiss = menuState::dismiss,
            )
        }
    }
}
