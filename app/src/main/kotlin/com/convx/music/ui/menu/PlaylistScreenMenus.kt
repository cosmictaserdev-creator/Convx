/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.size
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.offline.Download
import com.convx.music.LocalListenTogetherManager
import com.convx.music.R
import com.convx.music.library.PlaylistDetail
import com.convx.music.library.LocalPlaylistSong
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData

/**
 * Menu for Local Playlist Screen
 */
@Composable
fun LocalPlaylistMenu(
    playlist: PlaylistDetail,
    songs: List<LocalPlaylistSong>,
    context: Context,
    downloadState: Int,
    onEdit: () -> Unit,
    onSync: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onModifyWithAi: () -> Unit,
    onQueue: () -> Unit,
    onDismiss: () -> Unit
) {
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost

    val downloadMenuItem = when (downloadState) {
        Download.STATE_COMPLETED -> IosMenuItemData(
            title = { Text(stringResource(R.string.remove_download)) },
            description = { Text(stringResource(R.string.remove_download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> IosMenuItemData(
            title = { Text(stringResource(R.string.downloading)) },
            description = { Text(stringResource(R.string.download_in_progress_desc)) },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        else -> IosMenuItemData(
            title = { Text(stringResource(R.string.action_download)) },
            description = { Text(stringResource(R.string.download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
    }

    val isYouTubePlaylist = playlist.browseId != null

    val menuItems = buildList {
        add(
            IosMenuItemData(
                title = { Text(stringResource(R.string.edit)) },
                description = { Text(stringResource(R.string.edit_playlist)) },
                icon = {
                    SymbolIcon(symbol = sf("pencil"), contentDescription = null)
                },
                onClick = {
                    onEdit()
                    onDismiss()
                }
            )
        )

        // Show sync button only for YouTube playlists
        if (isYouTubePlaylist) {
            add(
                IosMenuItemData(
                    title = { Text(stringResource(R.string.action_sync)) },
                    description = { Text(stringResource(R.string.sync_playlist_desc)) },
                    icon = {
                        SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null)
                    },
                    onClick = {
                        onSync()
                        onDismiss()
                    }
                )
            )
        }

        if (!isGuest) {
            add(
                IosMenuItemData(
                    title = { Text(stringResource(R.string.add_to_queue)) },
                    description = { Text(stringResource(R.string.add_to_queue_desc)) },
                    icon = {
                        SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                    },
                    onClick = {
                        onQueue()
                        onDismiss()
                    }
                )
            )
        }

        add(downloadMenuItem)

        if (playlist.isEditable) {
            add(
                IosMenuItemData(
                    title = { Text(stringResource(R.string.modify_with_ai)) },
                    description = { Text(stringResource(R.string.modify_with_ai_desc)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.sparks),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onModifyWithAi()
                        onDismiss()
                    }
                )
            )
        }

        add(
            IosMenuItemData(
                title = { Text(stringResource(R.string.share)) },
                description = { Text(stringResource(R.string.share_playlist_desc)) },
                icon = {
                    SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null)
                },
                onClick = {
                    val shareText = playlist.browseId?.let { browseId ->
                        "https://music.youtube.com/playlist?list=$browseId"
                    } ?: songs.joinToString("\n") { it.song.song.title }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                    onDismiss()
                }
            )
        )

        add(
            IosMenuItemData(
                title = { Text(stringResource(R.string.delete)) },
                description = { Text(stringResource(R.string.delete_playlist_desc)) },
                icon = {
                    SymbolIcon(symbol = sf("trash"), contentDescription = null)
                },
                onClick = {
                    onDelete()
                    onDismiss()
                }
            )
        )
    }

    IosMenuGroup(items = menuItems)
}

/**
 * Menu for Auto Playlist Screen (Liked Songs, Downloaded Songs, etc.)
 */
@Composable
fun AutoPlaylistMenu(
    downloadState: Int,
    onQueue: () -> Unit,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost

    val downloadMenuItem = when (downloadState) {
        Download.STATE_COMPLETED -> IosMenuItemData(
            title = { Text(stringResource(R.string.remove_download)) },
            description = { Text(stringResource(R.string.remove_download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> IosMenuItemData(
            title = { Text(stringResource(R.string.downloading)) },
            description = { Text(stringResource(R.string.download_in_progress_desc)) },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        else -> IosMenuItemData(
            title = { Text(stringResource(R.string.action_download)) },
            description = { Text(stringResource(R.string.download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
    }

    IosMenuGroup(
        items = listOfNotNull(
            if (!isGuest) {
                IosMenuItemData(
                    title = { Text(stringResource(R.string.add_to_queue)) },
                    description = { Text(stringResource(R.string.add_to_queue_desc)) },
                    icon = {
                        SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                    },
                    onClick = {
                        onQueue()
                        onDismiss()
                    }
                )
            } else null,
            downloadMenuItem
        )
    )
}

/**
 * Menu for Top Playlist Screen
 */
@Composable
fun TopPlaylistMenu(
    downloadState: Int,
    onQueue: () -> Unit,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost

    val downloadMenuItem = when (downloadState) {
        Download.STATE_COMPLETED -> IosMenuItemData(
            title = { Text(stringResource(R.string.remove_download)) },
            description = { Text(stringResource(R.string.remove_download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> IosMenuItemData(
            title = { Text(stringResource(R.string.downloading)) },
            description = { Text(stringResource(R.string.download_in_progress_desc)) },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        else -> IosMenuItemData(
            title = { Text(stringResource(R.string.action_download)) },
            description = { Text(stringResource(R.string.download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
    }

    IosMenuGroup(
        items = listOfNotNull(
            if (!isGuest) {
                IosMenuItemData(
                    title = { Text(stringResource(R.string.add_to_queue)) },
                    description = { Text(stringResource(R.string.add_to_queue_desc)) },
                    icon = {
                        SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                    },
                    onClick = {
                        onQueue()
                        onDismiss()
                    }
                )
            } else null,
            downloadMenuItem
        )
    )
}

/**
 * Menu for Cache Playlist Screen
 */
@Composable
fun CachePlaylistMenu(
    downloadState: Int,
    onQueue: () -> Unit,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    val listenTogetherManager = LocalListenTogetherManager.current
    val isGuest = listenTogetherManager?.isInRoom == true && !listenTogetherManager.isHost

    val downloadMenuItem = when (downloadState) {
        Download.STATE_COMPLETED -> IosMenuItemData(
            title = { Text(stringResource(R.string.remove_download)) },
            description = { Text(stringResource(R.string.remove_download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> IosMenuItemData(
            title = { Text(stringResource(R.string.downloading)) },
            description = { Text(stringResource(R.string.download_in_progress_desc)) },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
        else -> IosMenuItemData(
            title = { Text(stringResource(R.string.action_download)) },
            description = { Text(stringResource(R.string.download_playlist_desc)) },
            icon = {
                SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null)
            },
            onClick = {
                onDownload()
                onDismiss()
            }
        )
    }

    IosMenuGroup(
        items = listOfNotNull(
            if (!isGuest) {
                IosMenuItemData(
                    title = { Text(stringResource(R.string.add_to_queue)) },
                    description = { Text(stringResource(R.string.add_to_queue_desc)) },
                    icon = {
                        SymbolIcon(symbol = sf("music.note.list"), contentDescription = null)
                    },
                    onClick = {
                        onQueue()
                        onDismiss()
                    }
                )
            } else null,
            downloadMenuItem
        )
    )
}
