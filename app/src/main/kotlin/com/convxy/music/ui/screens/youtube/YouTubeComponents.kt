/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.youtube

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.innertube.models.WebChannel
import com.music.innertube.models.WebPlaylist
import com.music.innertube.models.WebVideo
import com.convxy.music.R
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.makeTimeString
import com.music.innertube.YouTubeWeb

/** Subtitle builder shared by every card: "channel • 1.2M views • 3 days ago". */
private fun youtubeCardSubtitle(video: WebVideo): String =
    listOfNotNull(video.channelName, video.viewsText, video.publishedText)
        .joinToString(separator = " • ")

/**
 * Opens the watch screen for [video], passing its metadata through
 * [com.convxy.music.utils.YouTubePlaybackState.pendingVideo] so playback starts
 * instantly (the watch page then only refines the UI, not the queue start).
 */
fun androidx.navigation.NavController.navigateYouTubeWatch(
    video: WebVideo,
    positionMs: Long = 0,
) {
    com.convxy.music.utils.YouTubePlaybackState.pendingVideo = video
    navigate(
        if (positionMs > 0) "youtube_watch/${video.id}?position=$positionMs"
        else "youtube_watch/${video.id}"
    )
}

/**
 * Round icon button in Convxy's floating-control style (solid surface circle,
 * no ripple). Deliberately does NOT use the liquid-glass [com.convxy.music.ui.component.GlassCircleButton]:
 * the glass modifier records backdrops, and keeping the YouTube screens on the
 * plain path removes the one exotic component from their composition.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Full-width feed card for a regular YouTube video: 16:9 thumbnail with a
 * duration badge and optional watch-progress bar, then title and channel
 * metadata. Matches Convxy's rounded-thumbnail design language.
 */
@Composable
fun YouTubeVideoCard(
    video: WebVideo,
    isPlaying: Boolean = false,
    progress: YouTubeWatchHistoryEntity? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onOverflowClick: (() -> Unit)? = null,
    /** Tapping the channel text opens the channel page (null = inert). */
    onChannelClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedBounceClick(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = video.thumbnail?.resize(960, 540),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
            video.durationSeconds?.let { duration ->
                Text(
                    text = makeTimeString(duration * 1000L),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(
                            Color.Black.copy(alpha = 0.72f),
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                )
            }
            progress?.let { entry ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.Black.copy(alpha = 0.35f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(entry.progressFraction)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 0.dp, end = 0.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (onChannelClick != null && !video.channelName.isNullOrBlank()) {
                            Modifier.clickable(onClick = onChannelClick)
                        } else {
                            Modifier
                        }
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = youtubeCardSubtitle(video),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            onOverflowClick?.let { onOverflow ->
                IconButton(onClick = onOverflow, modifier = Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/**
 * Compact horizontal row (thumbnail left) used by search results, related
 * lists, playlists and history.
 */
@Composable
fun YouTubeVideoRow(
    video: WebVideo,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    progress: YouTubeWatchHistoryEntity? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onOverflowClick: (() -> Unit)? = null,
    /** Replaces the default overflow icon (e.g. history-row delete buttons). */
    trailingContent: (@Composable androidx.compose.foundation.layout.RowScope.() -> Unit)? = null,
    /** Tapping the title/channel text opens the channel page (null = inert). */
    onChannelClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedBounceClick(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .aspectRatio(16f / 9f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = video.thumbnail?.resize(544, 306),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
            if (video.isShort) {
                Text(
                    text = "SHORTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(
                            Color.Black.copy(alpha = 0.72f),
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                )
            } else {
                video.durationSeconds?.let { duration ->
                    Text(
                        text = makeTimeString(duration * 1000L),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .background(
                                Color.Black.copy(alpha = 0.72f),
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    )
                }
            }
            progress?.let { entry ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.Black.copy(alpha = 0.35f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(entry.progressFraction)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (onChannelClick != null && !video.channelName.isNullOrBlank()) {
                        Modifier.clickable(onClick = onChannelClick)
                    } else {
                        Modifier
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isActive && isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = youtubeCardSubtitle(video),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            onOverflowClick?.let { onOverflow ->
                IconButton(onClick = onOverflow, modifier = Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/** Vertical 9:16 tile for the Shorts shelf. */
@Composable
fun YouTubeShortsCard(
    video: WebVideo,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(124.dp)
            .bounceClick(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = video.thumbnail?.resize(360, 640),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Text(
            text = video.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        video.viewsText?.let { views ->
            Text(
                text = views,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Channel row used in search results. */
@Composable
fun YouTubeChannelRow(
    channel: WebChannel,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick)
            .padding(vertical = 10.dp),
    ) {
        AsyncImage(
            model = channel.avatarUrl?.resize(160, 160),
            contentDescription = channel.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = channel.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = listOfNotNull(channel.subscriberText, channel.videoCountText)
                .joinToString(" • ")
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** Playlist tile used in search results and channel playlists tabs. */
@Composable
fun YouTubePlaylistCard(
    playlist: WebPlaylist,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedBounceClick(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .aspectRatio(16f / 9f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = playlist.thumbnail?.resize(544, 306),
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
            playlist.itemCountText?.let { count ->
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(
                            Color.Black.copy(alpha = 0.72f),
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = playlist.ownerName.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Friendly failure state with a retry action; never shows raw errors. */
@Composable
fun YouTubeErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.search_off),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        onRetry?.let { retry ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(onClick = retry)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
fun YouTubeEmptyState(
    modifier: Modifier = Modifier,
    message: String,
) {
    EmptyPlaceholder(
        icon = R.drawable.search_off,
        text = message,
        modifier = modifier,
    )
}

/** Maps network/extractor failures onto short, human-readable reasons. */
internal fun youtubeErrorMessage(error: Throwable): String = when {
    error.message?.contains("Watch page unavailable", ignoreCase = true) == true ->
        "This video isn't available. It may be private, removed or region locked."
    error is java.io.IOException -> "Couldn't reach YouTube. Check your connection and try again."
    error is kotlinx.coroutines.CancellationException -> "Request cancelled"
    else -> "Something went wrong while loading from YouTube."
}

/** Loading skeleton shaped like the feed's video cards. */
@Composable
fun YouTubeVideoListSkeleton(
    modifier: Modifier = Modifier,
    rowCount: Int = 4,
) {
    com.convxy.music.ui.component.shimmer.ShimmerHost(modifier = modifier) {
        repeat(rowCount) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(ThumbnailRoundedShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(0.75f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(0.45f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                )
            }
        }
    }
}
