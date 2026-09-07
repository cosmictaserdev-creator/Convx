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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.models.WebVideo
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.YouTubeVideoQueue
import com.convxy.music.ui.component.LargeScreenTitle
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.menu.YouTubeVideoMenu
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.makeTimeString
import com.convxy.music.viewmodels.YouTubeHomeViewModel
import com.convxy.music.viewmodels.YouTubeHomeUiState

/**
 * The native YouTube home: local sections first (continue watching, recently
 * watched, saved), then YouTube's own recommendations with endless scrolling
 * and a shorts shelf.
 */
@Composable
fun YouTubeHomeScreen(
    navController: NavController,
    viewModel: YouTubeHomeViewModel = hiltViewModel(),
) {
    timber.log.Timber.tag("YouTubeVideo").i("home screen compose enter")
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val database = LocalDatabase.current
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val recentWatched by viewModel.recentWatched.collectAsState()
    val savedVideos by viewModel.savedVideos.collectAsState()

    val lazyListState = rememberLazyListState()

    // Infinite scroll: kick off the next page shortly before the end.
    val shouldLoadMore by remember {
        derivedStateOf {
            val state = uiState as? YouTubeHomeUiState.Ready ?: return@derivedStateOf false
            if (state.feed.continuation == null || state.isLoadingMore || state.endReached) return@derivedStateOf false
            val total = lazyListState.layoutInfo.totalItemsCount
            val last = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 6
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }


    LazyColumn(
        state = lazyListState,
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "header") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp, end = 16.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LargeScreenTitle(title = "YouTube")
                }
                GlassButton(
                    onClick = { navController.navigate("youtube_history") },
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.history),
                        contentDescription = "Watch history",
                    )
                }
                GlassButton(onClick = { navController.navigate("youtube_search") }) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = stringResource(com.convxy.music.R.string.search),
                    )
                }
            }
        }

        // ── Continue watching (local) ────────────────────────────────────────
        if (continueWatching.isNotEmpty()) {
            item(key = "continue_title") {
                NavigationTitle(
                    title = "Continue watching",
                    onClick = { navController.navigate("youtube_history") },
                )
            }
            item(key = "continue_row") {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(continueWatching, key = { it.videoId }) { entry ->
                        ContinueWatchingCard(
                            entry = entry,
                            onClick = {
                                navController.navigateYouTubeWatch(
                                    entry.toWebVideo(),
                                    positionMs = entry.positionSeconds * 1000L,
                                )
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                    videoMenuFor(
                                        entry = entry,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        // ── Recently watched (local) ─────────────────────────────────────────
        if (recentWatched.isNotEmpty()) {
            item(key = "recent_title") {
                NavigationTitle(
                    title = "Recently watched",
                    onClick = { navController.navigate("youtube_history") },
                )
            }
            item(key = "recent_row") {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(recentWatched.take(20), key = { "recent_${it.videoId}" }) { entry ->
                        RecentWatchCard(
                            entry = entry,
                            onClick = {
                                navController.navigateYouTubeWatch(
                                    entry.toWebVideo(),
                                    positionMs = if (entry.isResumable) entry.positionSeconds * 1000L else 0,
                                )
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                    videoMenuFor(
                                        entry = entry,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        // ── Saved (local) ────────────────────────────────────────────────────
        if (savedVideos.isNotEmpty()) {
            item(key = "saved_title") {
                NavigationTitle(
                    title = "Saved",
                    onClick = { navController.navigate("youtube_saved") },
                )
            }
            item(key = "saved_row") {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(savedVideos.take(20), key = { "saved_${it.videoId}" }) { saved ->
                        RecentWatchCard(
                            entry = YouTubeWatchHistoryEntity(
                                videoId = saved.videoId,
                                title = saved.title,
                                channelId = saved.channelId,
                                channelName = saved.channelName,
                                thumbnailUrl = saved.thumbnailUrl,
                                durationSeconds = saved.durationSeconds,
                                lastWatchedAt = saved.savedAt,
                            ),
                            onClick = { navController.navigateYouTubeWatch(saved.toWebVideo()) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                    YouTubeVideoMenu(
                                        video = saved.toWebVideo(),
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        // ── Feed ─────────────────────────────────────────────────────────────
        when (val state = uiState) {
            is YouTubeHomeUiState.Loading -> {
                item(key = "feed_title") {
                    NavigationTitle(title = "Recommended")
                }
                item(key = "feed_loading") {
                    YouTubeVideoListSkeleton()
                }
            }

            is YouTubeHomeUiState.Error -> {
                item(key = "feed_error") {
                    YouTubeErrorState(
                        message = state.message,
                        onRetry = viewModel::refresh,
                    )
                }
            }

            is YouTubeHomeUiState.Ready -> {
                val feed = state.feed
                if (feed.shorts.isNotEmpty()) {
                    item(key = "shorts_title") {
                        NavigationTitle(title = "Shorts")
                    }
                    item(key = "shorts_row") {
                        LazyRow(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(feed.shorts, key = { "short_${it.id}" }) { short ->
                                com.convxy.music.ui.screens.youtube.YouTubeShortsCard(
                                    video = short,
                                    onClick = { navController.navigateYouTubeWatch(short) },
                                )
                            }
                        }
                    }
                }

                item(key = "feed_title") {
                    NavigationTitle(title = "Recommended")
                }

                val feedVideos = feed.sections.flatMap { it.videos }.distinctBy { it.id }
                items(feedVideos, key = { "feed_${it.id}" }) { video ->
                    Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        YouTubeVideoCard(
                            video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
                            isPlaying = mediaMetadata?.id == video.id && isPlaying,
                            onClick = { navController.navigateYouTubeWatch(video) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                    YouTubeVideoMenu(
                                        video = video,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                            onOverflowClick = {
                                menuState.show {
                                    YouTubeVideoMenu(
                                        video = video,
                                        navController = navController,
                                        onDismiss = menuState::dismiss,
                                    )
                                }
                            },
                        )
                    }
                }

                if (state.isLoadingMore) {
                    item(key = "feed_loading_more") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

/** Builds the shared overflow menu from a history row. */
@Composable
private fun videoMenuFor(
    entry: YouTubeWatchHistoryEntity,
    navController: NavController,
    onDismiss: () -> Unit,
) {
    YouTubeVideoMenu(
        video = entry.toWebVideo(),
        navController = navController,
        onDismiss = onDismiss,
    )
}

internal fun YouTubeWatchHistoryEntity.toWebVideo(): WebVideo = WebVideo(
    id = videoId,
    title = title,
    channelId = channelId,
    channelName = channelName,
    thumbnail = thumbnailUrl,
    durationSeconds = if (durationSeconds > 0) durationSeconds else null,
    viewsText = null,
    publishedText = null,
)

internal fun com.convxy.music.db.entities.YouTubeSavedVideoEntity.toWebVideo(): WebVideo = WebVideo(
    id = videoId,
    title = title,
    channelId = channelId,
    channelName = channelName,
    thumbnail = thumbnailUrl,
    durationSeconds = if (durationSeconds > 0) durationSeconds else null,
    viewsText = null,
    publishedText = null,
)

/** Big "resume" card used by Continue watching: 16:9 thumb + progress + title. */
@Composable
private fun ContinueWatchingCard(
    entry: YouTubeWatchHistoryEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .combinedBounceClick(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = entry.thumbnailUrl?.resize(544, 306),
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.35f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(entry.progressFraction)
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .background(
                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_widget_play),
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Text(
            text = entry.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = buildString {
                append(makeTimeString(entry.positionSeconds * 1000L))
                if (entry.durationSeconds > 0) {
                    append(" / ")
                    append(makeTimeString(entry.durationSeconds * 1000L))
                }
                append(" • ")
                append(entry.channelName.orEmpty())
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Compact square-ish card for Recently watched / Saved rows. */
@Composable
private fun RecentWatchCard(
    entry: YouTubeWatchHistoryEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .combinedBounceClick(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(ThumbnailRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            AsyncImage(
                model = entry.thumbnailUrl?.resize(544, 306),
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
            entry.progressFraction.takeIf { it > 0.01f }?.let { fraction ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.35f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }
        Text(
            text = entry.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
