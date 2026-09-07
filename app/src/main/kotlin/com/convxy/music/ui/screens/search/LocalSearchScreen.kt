/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.search

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.CONTENT_TYPE_LIST
import com.convxy.music.db.entities.Album
import com.convxy.music.db.entities.Artist
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.playback.queues.ListQueue
import com.convxy.music.ui.component.AlbumListItem
import com.convxy.music.ui.component.ArtistListItem
import com.convxy.music.ui.component.ChipsRow
import com.convxy.music.ui.component.EmptyPlaceholder
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.HomeImageBackground
import com.convxy.music.ui.component.rememberAppBackgroundColor
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.font.FontWeight
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.PlaylistListItem
import com.convxy.music.ui.component.SongListItem
import com.convxy.music.ui.menu.SongMenu
import com.convxy.music.utils.listItemShape
import com.convxy.music.viewmodels.LocalFilter
import com.convxy.music.viewmodels.LocalSearchViewModel
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalSearchScreen(
    query: String,
    navController: NavController,
    onDismiss: () -> Unit,
    isFromCache: Boolean = false,
    pureBlack: Boolean,
    viewModel: LocalSearchViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val searchFilter by viewModel.filter.collectAsState()
    val result by viewModel.result.collectAsState()

    LaunchedEffect(query) {
        viewModel.query.value = query
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val lazyListState = rememberLazyListState()

    val heroUrl = result.map.values.flatten().firstOrNull()?.let {
        when (it) {
            is Song -> it.song.thumbnailUrl
            is Album -> it.album.thumbnailUrl
            is Artist -> it.artist.thumbnailUrl
            is Playlist -> it.thumbnails.firstOrNull()
            else -> null
        }
    }
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = result.map[LocalFilter.SONG]?.filterIsInstance<Song>()?.map { it.song.thumbnailUrl to false } ?: emptyList()
    )
    // Was hardcoded Color.Black regardless of the Theme settings color/pureBlack
    // choice — the top bar and result background never matched what the rest of
    // the app (online SearchScreen included) actually painted. Same token online
    // search uses, with pureBlack forcing flat black same as every other
    // hero-tinted screen already does (see rememberHeroTint's PureBlack override).
    val tint = if (pureBlack) Color.Black else rememberAppBackgroundColor(AppleTokens.BgElevated)
    val onTint = AppleTokens.onColor(tint)

    val glassConfig = LocalGlassEffectConfig.current
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val heroBackdrop = rememberLayerBackdrop()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tint),
    ) {
      // Matches Home/Library/OnlineSearchResult: the user's own picture/color
      // background, drawn unconditionally like SearchScreen.kt — HomeImageBackground
      // already no-ops when no custom image is set.
      HomeImageBackground(withGradient = true)
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        val chromeShape = ContinuousRoundedRectangle(percent = 50)
        val chromeBackgroundModifier = if (useGlass) {
            Modifier.liquidGlass(config = glassConfig, shape = chromeShape, highlightAlpha = 0.3f)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }

        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Bottom)
                .asPaddingValues(),
            modifier = Modifier
                .fillMaxSize()
                .let { base ->
                    if (isLandscape) {
                        base.windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                        )
                    } else base
                }
        ) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tint.copy(alpha = 0.8f))
                ) {
                    ChipsRow(
                        chips = listOf(
                            LocalFilter.ALL to stringResource(R.string.filter_all),
                            LocalFilter.SONG to stringResource(R.string.filter_songs),
                            LocalFilter.ALBUM to stringResource(R.string.filter_albums),
                            LocalFilter.ARTIST to stringResource(R.string.filter_artists),
                            LocalFilter.PLAYLIST to stringResource(R.string.filter_playlists),
                        ),
                        currentValue = searchFilter,
                        onValueUpdate = { viewModel.filter.value = it },
                    )
                }
            }

            result.map.forEach { (filter, items) ->
                // Once per section instead of once per row — see the itemsIndexed below.
                val distinctItems = items.distinctBy { it.id }
                if (result.filter == LocalFilter.ALL) {
                    item(key = filter) {
                        NavigationTitle(
                            title = stringResource(
                                when (filter) {
                                    LocalFilter.SONG -> R.string.filter_songs
                                    LocalFilter.ALBUM -> R.string.filter_albums
                                    LocalFilter.ARTIST -> R.string.filter_artists
                                    LocalFilter.PLAYLIST -> R.string.filter_playlists
                                    LocalFilter.ALL -> error("")
                                }
                            ),
                            color = onTint,
                            onClick = { viewModel.filter.value = filter },
                        )
                    }
                }

            // itemsIndexed so listItemShape can use the index directly. It used to call
            // items.indexOfFirst { ... } inside every row — a linear scan per row, i.e.
            // O(n²) per frame — and re-derive the distinct list on each recomposition.
            itemsIndexed(
                items = distinctItems,
                key = { _, it -> it.id },
                contentType = { _, _ -> CONTENT_TYPE_LIST },
            ) { itemIndex, item ->
                when (item) {
                    is Song -> SongListItem(
                        song = item,
                        showInLibraryIcon = true,
                        isActive = item.id == mediaMetadata?.id,
                        isPlaying = isPlaying,
                        flat = true,
                        shape = listItemShape(itemIndex, distinctItems.size),
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    menuState.show {
                                        SongMenu(
                                            originalSong = item,
                                            navController = navController,
                                            onDismiss = {
                                                onDismiss()
                                                menuState.dismiss()
                                            },
                                            isFromCache = isFromCache
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert),
                                    contentDescription = null,
                                )
                            }
                        },
                        modifier = Modifier
                            .combinedBounceClick(
                                onClick = {
                                    if (item.id == mediaMetadata?.id) {
                                        playerConnection.togglePlayPause()
                                    } else {
                                        val songs = result.map
                                            .getOrDefault(LocalFilter.SONG, emptyList())
                                            .filterIsInstance<Song>()
                                            .map { it.toMediaItem() }
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = context.getString(R.string.queue_searched_songs),
                                                items = songs,
                                                startIndex = songs.indexOfFirst { it.mediaId == item.id },
                                            )
                                        )
                                    }
                                },
                                onLongClick = {
                                    menuState.show {
                                        SongMenu(
                                            originalSong = item,
                                            navController = navController,
                                            onDismiss = {
                                                onDismiss()
                                                menuState.dismiss()
                                            },
                                            isFromCache = isFromCache
                                        )
                                    }
                                }
                            )
                            .animateItem(),
                    )

                    is Album -> AlbumListItem(
                        album = item,
                        isActive = item.id == mediaMetadata?.album?.id,
                        isPlaying = isPlaying,
                        flat = true,
                        modifier = Modifier
                            .bounceClick {
                                onDismiss()
                                navController.navigate("album/${item.id}")
                            }
                            .animateItem(),
                    )

                    is Artist -> ArtistListItem(
                        artist = item,
                        flat = true,
                        modifier = Modifier
                            .bounceClick {
                                onDismiss()
                                navController.navigate("artist/${item.id}")
                            }
                            .animateItem(),
                    )

                    is Playlist -> PlaylistListItem(
                        playlist = item,
                        flat = true,
                        modifier = Modifier
                            .bounceClick {
                                onDismiss()
                                navController.navigate("local_playlist/${item.id}")
                            }
                            .animateItem(),
                    )
                }
            }
            }

            if (result.query.isNotEmpty() && result.map.isEmpty()) {
                item(key = "no_result") {
                    EmptyPlaceholder(
                        icon = R.drawable.search,
                        text = stringResource(R.string.no_results_found),
                        modifier = Modifier.padding(top = 100.dp)
                    )
                }
            }
            item {
                Spacer(Modifier.height(100.dp))
            }
        }
      }
    }
}
