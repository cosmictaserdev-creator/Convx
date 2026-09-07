/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.menu

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.models.WebVideo
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.ListThumbnailSize
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.db.entities.YouTubeSavedVideoEntity
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.YouTubeVideoQueue
import com.convxy.music.ui.component.Material3MenuGroup
import com.convxy.music.ui.component.Material3MenuItemData
import com.convxy.music.ui.component.NewAction
import com.convxy.music.ui.component.NewActionGrid
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.makeTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Overflow menu for a regular-YouTube video. Only local actions (queue, save,
 * history) plus unauthenticated sharing — no fake like/subscribe.
 */
@Composable
fun YouTubeVideoMenu(
    video: WebVideo,
    navController: NavController,
    onDismiss: () -> Unit,
    onPlay: (() -> Unit)? = null,
    showChannel: Boolean = true,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()

    val isSaved by database.youTubeDao.isVideoSaved(video.id).collectAsState(initial = false)
    val historyEntry by database.youTubeDao.watchHistoryEntry(video.id).collectAsState(initial = null)

    val watchUrl = video.watchUrl

    ListItem(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        headlineContent = {
            Text(
                text = video.title,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            val duration = video.durationSeconds?.let { makeTimeString(it * 1000L) }
            Text(
                text = listOfNotNull(video.channelName, duration).joinToString(" • "),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape),
            ) {
                AsyncImage(
                    model = video.thumbnail?.resize(320, 180),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ListThumbnailSize),
                )
            }
        },
    )

    HorizontalDivider()

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
        contentPadding = PaddingValues(0.dp),
    ) {
        item {
            NewActionGrid(
                actions = listOf(
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_widget_play),
                                contentDescription = null,
                            )
                        },
                        text = "Play",
                        onClick = {
                            onDismiss()
                            onPlay?.invoke() ?: run {
                                playerConnection.playQueue(YouTubeVideoQueue(video))
                            }
                        },
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.playlist_play),
                                contentDescription = null,
                            )
                        },
                        text = "Play next",
                        onClick = {
                            onDismiss()
                            playerConnection.playNext(video.toMediaMetadata().toMediaItem())
                        },
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.queue_music),
                                contentDescription = null,
                            )
                        },
                        text = "Add to queue",
                        onClick = {
                            onDismiss()
                            playerConnection.addToQueue(video.toMediaMetadata().toMediaItem())
                        },
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(if (isSaved) R.drawable.bookmark_star_library else R.drawable.library_add),
                                contentDescription = null,
                            )
                        },
                        text = if (isSaved) "Unsave" else "Save",
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                if (isSaved) {
                                    database.youTubeDao.unsaveVideo(video.id)
                                } else {
                                    database.youTubeDao.saveVideo(
                                        YouTubeSavedVideoEntity(
                                            videoId = video.id,
                                            title = video.title,
                                            channelId = video.channelId,
                                            channelName = video.channelName,
                                            thumbnailUrl = video.thumbnail,
                                            durationSeconds = video.durationSeconds ?: -1,
                                        )
                                    )
                                }
                            }
                            onDismiss()
                        },
                    ),
                    if (historyEntry != null) {
                        NewAction(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.delete_history),
                                    contentDescription = null,
                                )
                            },
                            text = "Clear progress",
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    database.youTubeDao.deleteWatchHistoryEntry(video.id)
                                }
                                onDismiss()
                            },
                        )
                    } else {
                        NewAction(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = null,
                                )
                            },
                            text = "Mark watched",
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    database.youTubeDao.upsertWatchHistory(
                                        (historyEntry ?: com.convxy.music.db.entities.YouTubeWatchHistoryEntity(
                                            videoId = video.id,
                                            title = video.title,
                                            channelId = video.channelId,
                                            channelName = video.channelName,
                                            thumbnailUrl = video.thumbnail,
                                            durationSeconds = video.durationSeconds ?: -1,
                                        )).copy(
                                            positionSeconds = (video.durationSeconds ?: 0),
                                            completed = true,
                                            lastWatchedAt = System.currentTimeMillis(),
                                        )
                                    )
                                }
                                onDismiss()
                            },
                        )
                    },
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.link),
                                contentDescription = null,
                            )
                        },
                        text = "Copy link",
                        onClick = {
                            onDismiss()
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            clipboard.setPrimaryClip(
                                android.content.ClipData.newPlainText("YouTube link", watchUrl)
                            )
                            android.widget.Toast.makeText(context, "Link copied", android.widget.Toast.LENGTH_SHORT).show()
                        },
                    ),
                ),
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Material3MenuGroup(
                items = buildList {
                    if (showChannel && !video.channelId.isNullOrBlank()) {
                        add(
                            Material3MenuItemData(
                                title = { Text(text = "Open channel") },
                                description = { Text(text = video.channelName.orEmpty()) },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.artist),
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    onDismiss()
                                    navController.navigate("youtube_channel/${video.channelId}")
                                },
                            )
                        )
                    }
                    add(
                        Material3MenuItemData(
                            title = { Text(text = stringResource(R.string.share)) },
                            description = { Text(text = watchUrl) },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                onDismiss()
                                val intent = Intent(Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(Intent.EXTRA_TEXT, watchUrl)
                                context.startActivity(Intent.createChooser(intent, null))
                            },
                        )
                    )
                },
            )
        }
    }
}
