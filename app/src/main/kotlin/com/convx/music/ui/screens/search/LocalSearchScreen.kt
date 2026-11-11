/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.search

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.chromeShadow
import androidx.compose.ui.res.painterResource
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.localPlaylistRoute

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
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.CONTENT_TYPE_LIST
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.AlbumListItem
import com.convx.music.ui.component.ArtistListItem
import com.convx.music.ui.component.ChipsRow
import com.convx.music.ui.component.EmptyPlaceholder
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.HomeImageBackground
import com.convx.music.ui.component.rememberAppBackgroundColor
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.font.FontWeight
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.withFixedStyle
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.menu.SongMenu
import com.convx.music.utils.listItemShape
import com.convx.music.viewmodels.LocalFilter
import com.convx.music.viewmodels.LocalSearchViewModel
import kotlinx.coroutines.flow.drop
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalSearchScreen(
    query: String,
    navController: NavController,
    onDismiss: () -> Unit,
    isFromCache: Boolean = false,
    pureBlack: Boolean,
    viewModel: LocalSearchViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    val searchFilter by viewModel.filter.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.query.value = query
    }
    val localOnly = com.convx.music.LocalLocalOnlyMode.current
    val audioPermission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    LaunchedEffect(localOnly, audioPermission.granted) {
        if (localOnly && audioPermission.granted) viewModel.scanLocalFiles()
    }
    if (localOnly && !audioPermission.granted) {
        Box(Modifier.fillMaxSize()) {
            com.convx.music.ui.screens.library.apple.LocalAccessCard(
                blocked = audioPermission.blocked,
                onAllow = audioPermission.request,
            )
        }
        return
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val lazyListState = rememberLazyListState()

    val heroUrl = result.map.values.flatten().firstOrNull()?.let {
        when (it) {
            is Song -> it.song.thumbnailUrl
            is Album -> it.album.thumbnailUrl
            is Artist -> it.artist.thumbnailUrl
            is Playlist -> it.thumbnails.firstOrNull()
        }
    }
    val heroSource = rememberHeroSource(
        staticArt = heroUrl,
        songs = result.map[LocalFilter.SONG]?.filterIsInstance<Song>()?.map { it.song.thumbnailUrl to false } ?: emptyList()
    )
    // Use the theme background unless pure-black mode is enabled.
    val tint = if (pureBlack) Color.Black else rememberAppBackgroundColor(AppTheme.colorScheme.background)
    val onTint = AppleTokens.onColor(tint)

    // Keep top-bar glass fixed; the global preference controls the nav bar and mini player.
    val glassConfig = LocalGlassEffectConfig.current.withFixedStyle()
    val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR) && isGlassAllowed()
    val heroBackdrop = rememberLayerBackdrop()

    fun playSearchSongs(selectedSongId: String) {
        val songs = result.map
            .getOrDefault(LocalFilter.SONG, emptyList())
            .filterIsInstance<Song>()
            .map { it.toMediaItem() }
        playerConnection.playQueue(
            ListQueue(
                title = context.getString(R.string.queue_searched_songs),
                items = songs,
                startIndex = songs.indexOfFirst { it.mediaId == selectedSongId },
            )
        )
    }

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
            Modifier.chromeShadow(chromeShape).liquidGlass(config = glassConfig, shape = chromeShape)
        } else {
            Modifier.background(LocalContentColor.current.copy(alpha = 0.15f), chromeShape)
        }

        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
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
                // Same pills as the catalogue results; the page colour behind them so
                // rows scroll under a clean edge.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.convx.music.ui.ios.foundation.IosTheme.colors.systemBackground)
                ) {
                    AppleScopePills(
                        pills = listOf(
                            LocalFilter.ALL to stringResource(R.string.filter_all),
                            LocalFilter.SONG to stringResource(R.string.filter_songs),
                            LocalFilter.ALBUM to stringResource(R.string.filter_albums),
                            LocalFilter.ARTIST to stringResource(R.string.filter_artists),
                            LocalFilter.PLAYLIST to stringResource(R.string.filter_playlists),
                        ),
                        selected = searchFilter,
                        onSelect = { viewModel.filter.value = it },
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

            // Reuse the row index and distinct list instead of rescanning them per item.
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
                                SymbolIcon(symbol = sf("ellipsis"), contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .combinedBounceClick(
                                onClick = {
                                    if (item.id == mediaMetadata?.id) {
                                        playerConnection.togglePlayPause()
                                    } else {
                                        playSearchSongs(item.id)
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
                                navController.navigate(albumRoute(item.id, item.thumbnailUrl))
                            }
                            .animateItem(),
                    )

                    is Artist -> ArtistListItem(
                        artist = item,
                        flat = true,
                        modifier = Modifier
                            .bounceClick {
                                onDismiss()
                                navController.navigate(artistRoute(item.id, item.thumbnailUrl))
                            }
                            .animateItem(),
                    )

                    is Playlist -> PlaylistListItem(
                        playlist = item,
                        flat = true,
                        modifier = Modifier
                            .bounceClick {
                                onDismiss()
                                navController.navigate(localPlaylistRoute(item.id, item.thumbnailUrl))
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
