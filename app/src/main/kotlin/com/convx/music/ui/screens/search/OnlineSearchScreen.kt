/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.search

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.composables.sfsymbols.SfSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.screens.library.apple.SfStyle
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.composables.sfsymbols.monochrome.SFArrowUp
import com.composables.sfsymbols.monochrome.SFClock
import com.composables.sfsymbols.monochrome.SFMagnifyingglass
import com.composables.sfsymbols.monochrome.SFXmark
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.SuggestionItemHeight
import com.convx.music.playback.queues.searchResultQueue
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.YouTubeListItem
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.utils.listItemShape
import com.convx.music.ui.ios.compat.Surface
import com.music.innertube.utils.YouTubeUrlParser
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.viewmodels.OnlineSearchSuggestionViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun OnlineSearchScreen(
    query: String,
    onQueryChange: (TextFieldValue) -> Unit,
    navController: NavController,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    pureBlack: Boolean,
    // Caller's own hero-derived contrast color (AppleTokens.onColor(tint)) — this
    // screen renders transparent over the caller's existing blurred hero backdrop
    // instead of painting its own, so text/icons need to match that same contrast.
    contentColor: Color = AppTheme.colorScheme.onSurface,
    referenceHistory: Boolean = false,
    viewModel: OnlineSearchSuggestionViewModel = koinViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val scope = rememberCoroutineScope()

    val haptic = LocalHapticFeedback.current
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val searchedSongsTitle = stringResource(R.string.queue_searched_songs)

    LaunchedEffect(Unit) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .drop(1)
            .collect {
                keyboardController?.hide()
            }
    }

    LaunchedEffect(query) {
        snapshotFlow { query }.collectLatest {
            if (YouTubeUrlParser.isYouTubeUrl(it)) {
                viewModel.query.value = it
            } else {
                kotlinx.coroutines.delay(300L)
                viewModel.query.value = it
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            .asPaddingValues(),
        modifier = Modifier
            .fillMaxSize()
            // Keep the list transparent so the caller's hero background shows through.
    ) {
        if (referenceHistory && query.isEmpty()) {
            item(key = "history_header", contentType = "header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp,
                        top = 20.dp, bottom = 24.dp).height(26.dp),
                ) {
                    Text(text = stringResource(R.string.recently_searched),
                        style = SfStyle(22.sp, 26.sp, FontWeight.Bold, (-0.22).sp),
                        color = contentColor, modifier = Modifier.weight(1f))
                    if (viewState.history.isNotEmpty()) {
                        Text(text = stringResource(R.string.clear_recent_searches),
                            style = SfStyle(17.sp, 22.sp, tracking = (-0.51).sp),
                            color = Color(0xFFFA233A),
                            modifier = Modifier.iosRowPress(onClick = {
                                viewModel.clearHistory()
                            }))
                    }
                }
            }
        } else if (viewState.history.isNotEmpty()) {
            item(key = "history_header") {
                Text(
                    text = stringResource(R.string.search_history),
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp).animateItem()
                )
            }
        }

        itemsIndexed(viewState.history, key = { _, it -> "history_${it.query}" }) { index, history ->
            SuggestionItem(
                query = history.query,
                online = false,
                referenceHistory = referenceHistory,
                contentColor = contentColor,
                onClick = {
                    onSearch(history.query)
                    onDismiss()
                },
                onDelete = {
                    viewModel.deleteHistory(history)
                },
                onFillTextField = {
                    onQueryChange(TextFieldValue(history.query, TextRange(history.query.length)))
                },
                modifier = Modifier.animateItem(),
            )
        }

        if (viewState.history.isNotEmpty() && viewState.suggestions.isNotEmpty()) {
            item(key = "history_suggestion_spacer") {
                Spacer(modifier = Modifier.height(16.dp).animateItem())
            }
        }

        if (viewState.suggestions.isNotEmpty()) {
            item(key = "suggestions_header") {
                Text(
                    text = stringResource(R.string.suggestions),
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp).animateItem()
                )
            }
        }

        itemsIndexed(viewState.suggestions, key = { _, it -> "suggestion_$it" }) { index, query ->
            SuggestionItem(
                query = query,
                online = true,
                contentColor = contentColor,
                onClick = {
                    onSearch(query)
                    onDismiss()
                },
                onFillTextField = {
                    onQueryChange(TextFieldValue(query, TextRange(query.length)))
                },
                modifier = Modifier.animateItem(),
            )
        }

        if (viewState.suggestions.isNotEmpty()) {
            item(key = "suggestions_bottom_spacer") {
                Spacer(modifier = Modifier.height(16.dp).animateItem())
            }
        }

        if (viewState.items.isNotEmpty()) {
            item(key = "search_divider") {
                Text(
                    text = stringResource(if (viewState.isFromLink) R.string.parsed_from_link else R.string.top_result),
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp).animateItem()
                )
            }
            item(key = "search_divider_spacer") {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        itemsIndexed(viewState.items, key = { _, it -> "item_${it.id}" }) { index, item ->
            YouTubeListItem(
                item = item,
                isActive = when (item) {
                    is SongItem -> mediaMetadata?.id == item.id
                    is AlbumItem -> mediaMetadata?.album?.id == item.id
                    else -> false
                },
                isPlaying = isPlaying,
                flat = true,
                shape = listItemShape(index, viewState.items.size),
                trailingContent = {
                    IconButton(
                        onClick = {
                            menuState.show {
                                when (item) {
                                    is SongItem -> YouTubeSongMenu(
                                        song = item,
                                        navController = navController,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is AlbumItem -> YouTubeAlbumMenu(
                                        albumItem = item,
                                        navController = navController,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is ArtistItem -> YouTubeArtistMenu(
                                        artist = item,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is PlaylistItem -> YouTubePlaylistMenu(
                                        playlist = item,
                                        coroutineScope = scope,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                    }
                },
                modifier = Modifier
                    .combinedBounceClick(
                        onClick = {
                            when (item) {
                                is SongItem -> {
                                    if (item.id == mediaMetadata?.id) {
                                        playerConnection.togglePlayPause()
                                    } else {
                                        // Queue the whole suggestion list, not a one-song
                                        // radio: the other rows the user can see should not
                                        // vanish the moment one of them is tapped.
                                        playerConnection.playQueue(
                                            searchResultQueue(item, viewState.items, searchedSongsTitle)
                                        )
                                        onDismiss()
                                    }
                                }
                                is AlbumItem -> {
                                    navController.navigate(albumRoute(item.id, item.thumbnail))
                                    onDismiss()
                                }
                                is ArtistItem -> {
                                    navController.navigate(artistRoute(item.id, item.thumbnail))
                                    onDismiss()
                                }
                                is PlaylistItem -> {
                                    navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
                                    onDismiss()
                                }
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            menuState.show {
                                when (item) {
                                    is SongItem -> YouTubeSongMenu(
                                        song = item,
                                        navController = navController,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is AlbumItem -> YouTubeAlbumMenu(
                                        albumItem = item,
                                        navController = navController,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is ArtistItem -> YouTubeArtistMenu(
                                        artist = item,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                    is PlaylistItem -> YouTubePlaylistMenu(
                                        playlist = item,
                                        coroutineScope = coroutineScope,
                                        onDismiss = {
                                            menuState.dismiss()
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                        }
                    )
                    .animateItem()
            )
        }
    }
}

@Composable
fun SuggestionItem(
    modifier: Modifier = Modifier,
    query: String,
    online: Boolean,
    referenceHistory: Boolean = false,
    contentColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    onFillTextField: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (referenceHistory && !online) 56.dp else SuggestionItemHeight)
                // Flat — same borderless-row-over-hero-blur look as the actual
                // result rows below (YouTubeListItem, flat = true), not a card.
                .bounceClick(onClick = onClick)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        ) {
            SymbolIcon(symbol = if (online) sf("SFMagnifyingglass") else sf("SFClock"), contentDescription = null, size = 17.dp, modifier = Modifier.padding(horizontal = 16.dp).alpha(0.5f), tint = contentColor)

            Text(
                text = query,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor,
                style = SfStyle(17.sp, 22.sp, tracking = (-0.43).sp),
                modifier = Modifier.weight(1f),
            )

            if (!online) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.alpha(0.5f),
                ) {
                    SymbolIcon(symbol = sf("SFXmark"), contentDescription = null, size = 14.dp, tint = contentColor)
                }
            }

            IconButton(
                onClick = onFillTextField,
                modifier = Modifier.alpha(0.5f),
            ) {
                SymbolIcon(symbol = sf("SFArrowUp"), contentDescription = null, size = 16.dp, tint = contentColor)
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = AppleTokens.divider,
        )
    }
}
