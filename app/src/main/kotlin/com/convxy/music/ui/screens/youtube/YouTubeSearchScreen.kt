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
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.menu.YouTubeVideoMenu
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.viewmodels.YouTubeSearchUiState
import com.convxy.music.viewmodels.YouTubeSearchViewModel
import com.music.innertube.models.WebSearchFilter
import com.music.innertube.utils.YouTubeUrlParser
import kotlinx.coroutines.FlowPreview

/**
 * Native YouTube search: live suggestions, recent queries, result tabs
 * (Videos / Channels / Playlists) and infinite scrolling. Pasted links are
 * recognised and routed straight to the right page.
 */
@OptIn(FlowPreview::class)
@Composable
fun YouTubeSearchScreen(
    navController: NavController,
    viewModel: YouTubeSearchViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val uiState by viewModel.uiState.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    val focusRequester = remember { FocusRequester() }
    var queryValue by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(TextFieldValue(uiState.query))
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // Keep the text field in sync when a search is committed programmatically.
    LaunchedEffect(uiState.query) {
        if (queryValue.text != uiState.query) {
            queryValue = TextFieldValue(uiState.query, selection = androidx.compose.ui.text.TextRange(uiState.query.length))
        }
    }

    val lazyListState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val state = uiState
            if (state.page?.continuation == null || state.isLoading || state.isLoadingMore) return@derivedStateOf false
            val total = lazyListState.layoutInfo.totalItemsCount
            val last = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 8
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadMore() }

    fun submit(raw: String) {
        val query = raw.trim()
        if (query.isEmpty()) return
        // A pasted YouTube link jumps straight to the content.
        when (val parsed = YouTubeUrlParser.parse(query)) {
            is YouTubeUrlParser.ParsedUrl.Video -> {
                keyboardController?.hide()
                navController.navigate("youtube_watch/${parsed.id}")
                return
            }
            is YouTubeUrlParser.ParsedUrl.Channel -> {
                keyboardController?.hide()
                navController.navigate("youtube_channel/${parsed.id}")
                return
            }
            is YouTubeUrlParser.ParsedUrl.Playlist -> {
                keyboardController?.hide()
                navController.navigate("youtube_playlist/${parsed.id}")
                return
            }
            else -> Unit
        }
        keyboardController?.hide()
        viewModel.search(query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Search field row.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            GlassButton(onClick = { navController.navigateUp() }) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back",
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = queryValue,
                    onValueChange = {
                        queryValue = it
                        viewModel.onQueryChanged(it.text)
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submit(queryValue.text) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box {
                            if (queryValue.text.isEmpty()) {
                                Text(
                                    text = "Search YouTube",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
            if (queryValue.text.isNotEmpty()) {
                IconButton(onClick = {
                    queryValue = TextFieldValue("")
                    viewModel.onQueryChanged("")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Clear",
                    )
                }
            }
        }

        // Result tabs (only meaningful once a search exists).
        if (uiState.query.isNotBlank()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                listOf(
                    "All" to WebSearchFilter.NONE,
                    "Videos" to WebSearchFilter.VIDEOS,
                    "Channels" to WebSearchFilter.CHANNELS,
                    "Playlists" to WebSearchFilter.PLAYLISTS,
                ).forEach { (label, filter) ->
                    val selected = uiState.filter == filter
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainer
                            )
                            .clickable { viewModel.setFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
        }

        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                .asPaddingValues(),
            modifier = Modifier.fillMaxSize(),
        ) {
            val state = uiState
            when {
                // ── No query yet: recents + live suggestions ─────────────────
                state.query.isBlank() -> {
                    if (searchHistory.isNotEmpty()) {
                        item(key = "history_title") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    text = "Recent searches",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "Clear",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { viewModel.clearSearchHistory() },
                                )
                            }
                        }
                        items(searchHistory, key = { "qh_${it.id}" }) { entry ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(onClick = { submit(entry.query) })
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.history),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = entry.query,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                )
                                IconButton(onClick = { viewModel.deleteSearchHistoryEntry(entry.id) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = "Remove ${entry.query}",
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                    if (suggestions.isNotEmpty()) {
                        item(key = "sugg_title") {
                            Text(
                                text = "Suggestions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                        items(suggestions, key = { "sg_$it" }) { suggestion ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(onClick = { submit(suggestion) })
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 12.dp),
                                )
                            }
                        }
                    }
                }

                // ── Loading ──────────────────────────────────────────────────
                state.isLoading -> {
                    item(key = "loading") {
                        YouTubeVideoListSkeleton()
                    }
                }

                // ── Error ────────────────────────────────────────────────────
                state.error != null -> {
                    item(key = "error") {
                        YouTubeErrorState(
                            message = state.error.orEmpty(),
                            onRetry = { viewModel.search(state.query) },
                        )
                    }
                }

                // ── Results ──────────────────────────────────────────────────
                else -> {
                    val page = state.page
                    if (page == null || (page.videos.isEmpty() && page.channels.isEmpty() && page.playlists.isEmpty())) {
                        item(key = "empty") {
                            YouTubeEmptyState(message = "No results for \"${state.query}\"")
                        }
                    } else {
                        if (page.videos.isNotEmpty()) {
                            item(key = "videos_title") { SectionHeader("Videos") }
                            items(page.videos, key = { "v_${it.id}" }) { video ->
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
                        if (page.channels.isNotEmpty()) {
                            item(key = "channels_title") { SectionHeader("Channels") }
                            items(page.channels, key = { "c_${it.id}" }) { channel ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    YouTubeChannelRow(
                                        channel = channel,
                                        onClick = { navController.navigate("youtube_channel/${channel.id}") },
                                    )
                                }
                            }
                        }
                        if (page.playlists.isNotEmpty()) {
                            item(key = "playlists_title") { SectionHeader("Playlists") }
                            items(page.playlists, key = { "p_${it.id}" }) { playlist ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    YouTubePlaylistCard(
                                        playlist = playlist,
                                        onClick = { navController.navigate("youtube_playlist/${playlist.id}") },
                                    )
                                }
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
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
