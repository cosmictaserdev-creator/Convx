/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.youtube

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.menu.YouTubeVideoMenu
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.makeTimeString
import com.convxy.music.viewmodels.YouTubeChannelUiState
import com.convxy.music.viewmodels.YouTubeChannelViewModel
import com.convxy.music.viewmodels.YouTubePlaylistUiState
import com.convxy.music.viewmodels.YouTubePlaylistViewModel
import com.music.innertube.models.WebVideo
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Channel
// ─────────────────────────────────────────────────────────────────────────────

/** Native channel page: header, tabs, and the selected tab's content. */
@Composable
fun YouTubeChannelScreen(
    navController: NavController,
    viewModel: YouTubeChannelViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val state = uiState as? YouTubeChannelUiState.Ready ?: return@derivedStateOf false
            val content = state.tabContent[state.selectedTabIndex] ?: return@derivedStateOf false
            if (content.continuation == null || content.isLoading || content.error != null) return@derivedStateOf false
            val total = lazyListState.layoutInfo.totalItemsCount
            val last = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 6
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadMore() }

    LazyColumn(
        state = lazyListState,
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "topbar") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                GlassButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
            }
        }

        when (val state = uiState) {
            is YouTubeChannelUiState.Loading -> item(key = "loading") {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    ShimmerHost {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(0.5f)
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                        )
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .clip(ThumbnailRoundedShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                            )
                        }
                    }
                }
            }

            is YouTubeChannelUiState.Error -> item(key = "error") {
                YouTubeErrorState(
                    message = state.message,
                    onRetry = viewModel::refresh,
                )
            }

            is YouTubeChannelUiState.Ready -> {
                val channel = state.page.channel
                item(key = "header") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        AsyncImage(
                            model = channel.avatarUrl?.resize(320, 320),
                            contentDescription = channel.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                        )
                        Text(
                            text = channel.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                        val meta = listOfNotNull(channel.subscriberText, channel.videoCountText)
                            .joinToString(" • ")
                        if (meta.isNotEmpty()) {
                            Text(
                                text = meta,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        channel.description?.takeIf { it.isNotBlank() }?.let { description ->
                            com.convxy.music.ui.component.ExpandableText(
                                text = description,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }

                // Tabs.
                if (state.page.tabs.size > 1) {
                    item(key = "tabs") {
                        LazyRow(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            itemsIndexed(state.page.tabs) { index, tab ->
                                val selected = state.selectedTabIndex == index
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        .combinedBounceClick(onClick = { viewModel.selectTab(index) })
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                }

                val content = state.tabContent[state.selectedTabIndex]
                when {
                    // Channels that expose no tabs at all (rare) would otherwise
                    // sit on the skeleton forever.
                    state.page.tabs.isEmpty() && content == null -> item(key = "tab_empty") {
                        YouTubeEmptyState(message = "Nothing here yet")
                    }

                    content == null || content.isLoading -> item(key = "tab_loading") {
                        YouTubeVideoListSkeleton()
                    }

                    content.error != null -> item(key = "tab_error") {
                        YouTubeErrorState(
                            message = content.error,
                            onRetry = { viewModel.selectTab(state.selectedTabIndex) },
                        )
                    }

                    else -> {
                        if (content.shorts.isNotEmpty()) {
                            item(key = "shorts_title") { NavigationTitle(title = "Shorts") }
                            item(key = "shorts_row") {
                                LazyRow(
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    items(content.shorts, key = { "ch_short_${it.id}" }) { short ->
                                        YouTubeShortsCard(
                                            video = short,
                                            onClick = { navController.navigateYouTubeWatch(short) },
                                        )
                                    }
                                }
                            }
                        }
                        if (content.videos.isNotEmpty()) {
                            items(content.videos, key = { "ch_v_${it.id}" }) { video ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    YouTubeVideoRow(
                                        video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
                                        isActive = mediaMetadata?.id == video.id,
                                        isPlaying = isPlaying,
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
                        }
                        if (content.playlists.isNotEmpty()) {
                            item(key = "pl_title") { NavigationTitle(title = "Playlists") }
                            items(content.playlists, key = { "ch_p_${it.id}" }) { playlist ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    YouTubePlaylistCard(
                                        playlist = playlist,
                                        onClick = { navController.navigate("youtube_playlist/${playlist.id}") },
                                    )
                                }
                            }
                        }
                        if (content.videos.isEmpty() && content.shorts.isEmpty() && content.playlists.isEmpty()) {
                            item(key = "tab_empty") {
                                YouTubeEmptyState(message = "Nothing here yet")
                            }
                        }
                    }
                }
            }
        }

        item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Playlist
// ─────────────────────────────────────────────────────────────────────────────

/** Native playlist page: header with play/shuffle, items, infinite scroll. */
@Composable
fun YouTubePlaylistScreen(
    navController: NavController,
    viewModel: YouTubePlaylistViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val state = uiState as? YouTubePlaylistUiState.Ready ?: return@derivedStateOf false
            if (state.page.continuation == null || state.isLoadingMore) return@derivedStateOf false
            val total = lazyListState.layoutInfo.totalItemsCount
            val last = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 6
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadMore() }

    LazyColumn(
        state = lazyListState,
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "topbar") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                GlassButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
            }
        }

        when (val state = uiState) {
            is YouTubePlaylistUiState.Loading -> item(key = "loading") {
                YouTubeVideoListSkeleton()
            }

            is YouTubePlaylistUiState.Error -> item(key = "error") {
                YouTubeErrorState(
                    message = state.message,
                    onRetry = viewModel::refresh,
                )
            }

            is YouTubePlaylistUiState.Ready -> {
                val playlist = state.page.playlist
                item(key = "header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
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
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp),
                        ) {
                            Text(
                                text = playlist.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = listOfNotNull(playlist.ownerName, playlist.itemCountText)
                                    .joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                if (state.page.videos.isNotEmpty()) {
                    item(key = "actions") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .combinedBounceClick(
                                        onClick = {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = playlist.title,
                                                    items = state.page.videos.map { it.toMediaMetadata().toMediaItem() },
                                                )
                                            )
                                            navController.navigateUp()
                                        }
                                    )
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.play),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = "Play all",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(start = 6.dp),
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .combinedBounceClick(
                                        onClick = {
                                            playerConnection.addToQueue(
                                                state.page.videos.map { it.toMediaMetadata().toMediaItem() }
                                            )
                                        }
                                    )
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.queue_music),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = "Queue",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(start = 6.dp),
                                    )
                                }
                            }
                        }
                    }

                    // Index in the key: YouTube playlists may legally contain the
                    // same video twice; a bare id key would crash LazyColumn.
                    itemsIndexed(state.page.videos, key = { index, video -> "pl_${index}_${video.id}" }) { index, video ->
                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            YouTubeVideoRow(
                                video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
                                isActive = mediaMetadata?.id == video.id,
                                isPlaying = isPlaying,
                                onClick = {
                                    // Play from here: items up to this one first, like YouTube.
                                    val fromHere = state.page.videos.drop(index)
                                    playerConnection.playQueue(
                                        ListQueue(
                                            title = playlist.title,
                                            items = fromHere.map { it.toMediaMetadata().toMediaItem() },
                                        )
                                    )
                                },
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
                        item(key = "loading_more") {
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
                } else {
                    item(key = "empty") {
                        YouTubeEmptyState(message = "This playlist has no videos")
                    }
                }
            }
        }

        item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// History & Saved
// ─────────────────────────────────────────────────────────────────────────────

/** Local YouTube watch history with per-item delete and clear-all. */
@Composable
fun YouTubeHistoryScreen(
    navController: NavController,
) {
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val history by database.youTubeDao.watchHistory().collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "topbar") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                GlassButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Watch history",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
                if (history.isNotEmpty()) {
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.combinedBounceClick(
                            onClick = {
                                scope.launch { database.youTubeDao.clearWatchHistory() }
                            }
                        ),
                    )
                }
            }
        }

        if (history.isEmpty()) {
            item(key = "empty") {
                EmptyPlaceholder(
                    icon = R.drawable.history,
                    text = "Videos you watch will show up here",
                )
            }
        } else {
            items(history, key = { "h_${it.videoId}" }) { entry ->
                val video = entry.toWebVideo()
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    YouTubeVideoRow(
                        video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
                        progress = entry.takeIf { it.progressFraction > 0.01f },
                        onClick = {
                            navController.navigateYouTubeWatch(
                                video,
                                positionMs = if (entry.isResumable) entry.positionSeconds * 1000L else 0,
                            )
                        },
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
                        trailingContent = {
                            androidx.compose.material3.IconButton(onClick = {
                                scope.launch { database.youTubeDao.deleteWatchHistoryEntry(entry.videoId) }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Remove ${entry.title} from history",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                    )
                }
            }
        }
        item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
    }
}

/** Saved (bookmarked) YouTube videos, purely local metadata. */
@Composable
fun YouTubeSavedScreen(
    navController: NavController,
) {
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val saved by database.youTubeDao.savedVideos().collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "topbar") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                GlassButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
                Text(
                    text = "Saved",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }

        if (saved.isEmpty()) {
            item(key = "empty") {
                EmptyPlaceholder(
                    icon = R.drawable.bookmark_star_library,
                    text = "Videos you save will show up here",
                )
            }
        } else {
            items(saved, key = { "s_${it.videoId}" }) { savedVideo ->
                val video = WebVideo(
                    id = savedVideo.videoId,
                    title = savedVideo.title,
                    channelId = savedVideo.channelId,
                    channelName = savedVideo.channelName,
                    thumbnail = savedVideo.thumbnailUrl,
                    durationSeconds = savedVideo.durationSeconds.takeIf { it > 0 },
                    viewsText = null,
                    publishedText = null,
                )
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    YouTubeVideoRow(
                        video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
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
                        trailingContent = {
                            androidx.compose.material3.IconButton(onClick = {
                                scope.launch { database.youTubeDao.unsaveVideo(savedVideo.videoId) }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Remove ${savedVideo.title} from saved",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                    )
                }
            }
        }
        item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
    }
}
