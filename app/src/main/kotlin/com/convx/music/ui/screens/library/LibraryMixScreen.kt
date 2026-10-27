/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.SegmentedButton
import com.convx.music.ui.ios.compat.SingleChoiceSegmentedButtonRow
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFListBullet
import com.composables.sfsymbols.monochrome.SFSquareGrid2x2
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.AlbumViewTypeKey
import com.convx.music.constants.CONTENT_TYPE_HEADER
import com.convx.music.constants.CONTENT_TYPE_PLAYLIST
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.constants.LibraryBackgroundMode
import com.convx.music.constants.LibraryBackgroundModeKey
import com.convx.music.constants.LibraryIconsOnlyKey
import com.convx.music.constants.LibraryViewType
import com.convx.music.constants.MixSortDescendingKey
import com.convx.music.constants.MixSortType
import com.convx.music.constants.MixSortTypeKey
import com.convx.music.constants.ShowCachedPlaylistKey
import com.convx.music.constants.ShowDownloadedPlaylistKey
import com.convx.music.constants.ShowLikedPlaylistKey
import com.convx.music.constants.ShowLocalPlaylistKey
import com.convx.music.constants.ShowTopPlaylistKey
import com.convx.music.constants.ShowUploadedPlaylistKey
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.extensions.reversed
import com.convx.music.ui.component.AlbumGridItem
import com.convx.music.ui.component.AlbumListItem
import com.convx.music.ui.component.ArtistGridItem
import com.convx.music.ui.component.ArtistListItem
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.PlaylistGridItem
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.HideOnScrollFAB
import com.convx.music.ui.component.hasCustomHomeBackground
import com.convx.music.ui.component.rememberAppBackgroundColor
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.SortHeader
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.menu.AlbumMenu
import com.convx.music.ui.menu.ArtistMenu
import com.convx.music.ui.menu.PlaylistMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.localPlaylistRoute
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LibraryMixViewModel
import java.text.Collator
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID
import androidx.compose.ui.platform.LocalLocale
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryMixScreen(
    navController: NavController,
    filterContent: @Composable () -> Unit,
    viewModel: LibraryMixViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()

    var viewType by rememberEnumPreference(AlbumViewTypeKey, LibraryViewType.GRID)
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        MixSortTypeKey,
        MixSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(MixSortDescendingKey, true)
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
    
    val (libraryIconsOnly) = rememberPreference(LibraryIconsOnlyKey, defaultValue = true)

    val topSize by viewModel.topValue.collectAsState(initial = 50)
    
    val likedThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.LIKED_PLAYLIST_ID}"), "")
    val downloadThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.DOWNLOADED_PLAYLIST_ID}"), "")
    val topThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.TOP_PLAYLIST_ID}"), "")
    val cacheThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.CACHED_PLAYLIST_ID}"), "")
    val uploadedThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.UPLOADED_PLAYLIST_ID}"), "")
    val localThumbnail by rememberPreference(stringPreferencesKey("thumbnail_${PlaylistEntity.LOCAL_PLAYLIST_ID}"), "")

    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    // Pinned playlists get their own row up top -- same pattern as
    // LibraryPlaylistsScreen's. Without this, pinning a playlist from the mixed
    // (default) Library tab changed the database but nothing visible here, since
    // this screen's merged album+playlist list has no idea pinnedAt exists.
    val pinnedPlaylists = remember(playlists) {
        playlists.distinctBy { it.id }
            .filter { it.playlist.pinnedAt != null }
            .sortedByDescending { it.playlist.pinnedAt }
    }

    val libraryBackgroundMode by rememberEnumPreference(LibraryBackgroundModeKey, LibraryBackgroundMode.PLAIN)

    // LibraryBackgroundMode only applies when no custom image is set. Without this
    // the blurred-artwork hero and its scrim kept rendering under the user's own
    // background, so the same picture looked right on Home and wrong here.
    val customBackground = hasCustomHomeBackground()
    val heroArtworkVisible = !customBackground

    val heroUrl = if (libraryBackgroundMode == LibraryBackgroundMode.THUMBNAIL_BLUR && heroArtworkVisible) {
        (albums + artists + playlists).firstOrNull()?.let {
            when (it) {
                is Album -> it.album.thumbnailUrl
                is Artist -> it.artist.thumbnailUrl
                is Playlist -> it.thumbnails.firstOrNull()
                else -> null
            }
        }
    } else {
        null
    }
    val heroSource = rememberHeroSource(staticArt = heroUrl)
    // Shared resolver, so Library falls back the same way Home and Search do instead of
    // resolving the same preference by hand — its own fallback was colorScheme.primary,
    // a saturated accent, where every other screen falls back to a neutral surface.
    val tint = if (libraryBackgroundMode == LibraryBackgroundMode.THEME) {
        rememberAppBackgroundColor(AppTheme.colorScheme.surface)
    } else {
        rememberHeroTint(heroUrl)
    }
    val onTint = AppleTokens.onColor(tint)
    val heroBackdrop = rememberLayerBackdrop()

    // These six are constant for the life of the screen, but were rebuilt on every
    // recomposition and passed straight into keyed lazy items — a new instance each
    // time defeats skipping, so all six rows recomposed on any state change here.
    val likedName = stringResource(R.string.liked)
    val offlineName = stringResource(R.string.offline)
    val myTopName = stringResource(R.string.my_top)
    val cachedName = stringResource(R.string.cached_playlist)
    val uploadedName = stringResource(R.string.uploaded_playlist)
    val localName = stringResource(R.string.filter_local)

    fun fixedPlaylist(id: String, name: String) =
        Playlist(
            playlist = PlaylistEntity(id = id, name = name),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val likedPlaylist = remember(likedName) {
        fixedPlaylist(PlaylistEntity.LIKED_PLAYLIST_ID, likedName)
    }
    val downloadPlaylist = remember(offlineName) {
        fixedPlaylist(PlaylistEntity.DOWNLOADED_PLAYLIST_ID, offlineName)
    }
    val topPlaylist = remember(myTopName, topSize) {
        fixedPlaylist(PlaylistEntity.TOP_PLAYLIST_ID, "$myTopName $topSize")
    }
    val cachePlaylist = remember(cachedName) {
        fixedPlaylist(PlaylistEntity.CACHED_PLAYLIST_ID, cachedName)
    }
    val uploadedPlaylist = remember(uploadedName) {
        fixedPlaylist(PlaylistEntity.UPLOADED_PLAYLIST_ID, uploadedName)
    }
    val localPlaylist = remember(localName) {
        fixedPlaylist(PlaylistEntity.LOCAL_PLAYLIST_ID, localName)
    }

    // Every auto playlist except Local is built from YouTube state (likes, YT
    // downloads, listening history, uploads), so local-only mode leaves just the one.
    val localOnly = com.convx.music.LocalLocalOnlyMode.current
    val (showLikedPref) = rememberPreference(ShowLikedPlaylistKey, true)
    val (showDownloadedPref) = rememberPreference(ShowDownloadedPlaylistKey, true)
    val (showTopPref) = rememberPreference(ShowTopPlaylistKey, true)
    val (showCachedPref) = rememberPreference(ShowCachedPlaylistKey, true)
    val (showUploadedPref) = rememberPreference(ShowUploadedPlaylistKey, true)
    val (showLocal) = rememberPreference(ShowLocalPlaylistKey, true)
    val showLiked = showLikedPref && !localOnly
    val showDownloaded = showDownloadedPref && !localOnly
    val showTop = showTopPref && !localOnly
    val showCached = showCachedPref && !localOnly
    val showUploaded = showUploadedPref && !localOnly

    val platformLocale = LocalLocale.current.platformLocale
    val allItems =
        remember(albums, playlists, sortType, sortDescending, platformLocale) {
            var items = albums + playlists
            val collator = Collator.getInstance(platformLocale)
            collator.strength = Collator.PRIMARY
            items =
                when (sortType) {
                    MixSortType.CREATE_DATE ->
                        items.sortedBy { item ->
                            when (item) {
                                is Album -> item.album.bookmarkedAt
                                is Artist -> item.artist.bookmarkedAt
                                is Playlist -> item.playlist.createdAt
                                else -> LocalDateTime.now()
                            }
                        }

                    MixSortType.NAME ->
                        items.sortedWith(
                            compareBy(collator) { item ->
                                when (item) {
                                    is Album -> item.album.title
                                    is Artist -> item.artist.name
                                    is Playlist -> item.playlist.name
                                    else -> ""
                                }
                            },
                        )

                    MixSortType.LAST_UPDATED ->
                        items.sortedBy { item ->
                            when (item) {
                                is Album -> item.album.lastUpdateTime
                                is Artist -> item.artist.lastUpdateTime
                                is Playlist -> item.playlist.lastUpdateTime
                                else -> LocalDateTime.now()
                            }
                        }
                }.reversed(sortDescending)
            items
        }

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    var showCreatePlaylistDialog by rememberSaveable { mutableStateOf(false) }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onPlaylistCreated = { playlistId ->
                showCreatePlaylistDialog = false
                navController.navigate("local_playlist/$playlistId")
            }
        )
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsStateWithLifecycle()

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            when (viewType) {
                LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
                LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
            }
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    val headerContent = @Composable {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppleTokens.Gutter),
        ) {
            SortHeader(
                sortType = sortType,
                sortDescending = sortDescending,
                onSortTypeChange = onSortTypeChange,
                onSortDescendingChange = onSortDescendingChange,
                sortTypeText = { sortType ->
                    when (sortType) {
                        MixSortType.CREATE_DATE -> R.string.sort_by_create_date
                        MixSortType.LAST_UPDATED -> R.string.sort_by_last_updated
                        MixSortType.NAME -> R.string.sort_by_name
                    }
                },
            )

            Spacer(Modifier.weight(1f))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            ) {
                LibraryViewType.entries.forEach { type ->
                    SegmentedButton(
                        selected = viewType == type,
                        onClick = { viewType = type },
                        icon = {
                            SymbolIcon(bounceOnEnter = true, symbol = when (type) {
                                    LibraryViewType.LIST -> sf("SFListBullet")
                                    LibraryViewType.GRID -> sf("SFSquareGrid2x2")
                                }, contentDescription = null)
                        },
                    )
                }
            }
        }
    }

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()

    HeroBackground(
        // HeroBackground fills its whole box with this tint, opaquely. LibraryScreen draws
        // HomeImageBackground BEHIND this composable, so an opaque tint here hid the user's
        // wallpaper completely — suppressing only the hero artwork (heroArtworkVisible) was
        // never enough, the flat colour underneath still covered it.
        tint = if (customBackground) Color.Transparent else tint,
        heroSource = heroSource,
        showDefaultIcon = libraryBackgroundMode != LibraryBackgroundMode.PLAIN && heroArtworkVisible,
        blurArtwork = libraryBackgroundMode == LibraryBackgroundMode.THUMBNAIL_BLUR && heroArtworkVisible,
        fullBlur = true,
        modifier = Modifier.fillMaxSize(),
    ) {
      if (libraryBackgroundMode == LibraryBackgroundMode.THUMBNAIL_BLUR && heroArtworkVisible) {
          // Blurred thumbnail alone isn't dark/flat enough for text on top to
          // stay readable — a uniform scrim, not just the bottom gradient.
          Box(
              modifier = Modifier
                  .matchParentSize()
                  .background(Color.Black.copy(alpha = 0.35f))
          )
      }
      HeroTintedContent(tint = tint, backdrop = heroBackdrop) {
        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            indicator = {
                // Only while visible — see HomeScreen: the M3 LoadingIndicator
                // animates forever once composed and pins the app at full frame
                // rate at idle.
                if (isRefreshing || pullRefreshState.distanceFraction > 0f) {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }
            }
        ) {
            when (viewType) {
                LibraryViewType.LIST ->
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier,
                        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                    ) {
                        item(
                            key = "filter",
                            contentType = CONTENT_TYPE_HEADER,
                        ) {
                            filterContent()
                        }

                        item(
                            key = "header",
                            contentType = CONTENT_TYPE_HEADER,
                        ) {
                            headerContent()
                        }

                        if (pinnedPlaylists.isNotEmpty()) {
                            item(key = "pinned_header", contentType = CONTENT_TYPE_HEADER) {
                                Text(
                                    text = stringResource(R.string.pinned),
                                    style = AppTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(horizontal = AppleTokens.Gutter, vertical = 8.dp)
                                        .animateItem(),
                                )
                            }
                            item(key = "pinned_row", contentType = "pinned_row") {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = AppleTokens.Gutter),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem(),
                                ) {
                                    items(pinnedPlaylists, key = { "pinned_${it.id}" }) { playlist ->
                                        PlaylistGridItem(
                                            playlist = playlist,
                                            fillMaxWidth = false,
                                            modifier = Modifier
                                                .width(150.dp)
                                                .animateItem()
                                                .combinedBounceClick(
                                                    onClick = { navController.navigate("local_playlist/${playlist.id}") },
                                                    onLongClick = {
                                                        menuState.show {
                                                            PlaylistMenu(
                                                                playlist = playlist,
                                                                coroutineScope = coroutineScope,
                                                                onDismiss = menuState::dismiss,
                                                            )
                                                        }
                                                    },
                                                ),
                                        )
                                    }
                                }
                            }
                        }

                        if (showLiked) {
                            item(
                                key = "likedPlaylist",
                                contentType = { CONTENT_TYPE_PLAYLIST },
                            ) {
                                PlaylistListItem(
                                    playlist = likedPlaylist,
                                    autoPlaylist = true,
                                    sharedArtworkId = "liked",
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    thumbnailOverrideUrl = likedThumbnail.takeIf { it.isNotBlank() },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("auto_playlist/liked")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = likedPlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                                )
                            }
                        }

                        if (showDownloaded) {
                            item(
                                key = "downloadedPlaylist",
                                contentType = { CONTENT_TYPE_PLAYLIST },
                            ) {
                                PlaylistListItem(
                                    playlist = downloadPlaylist,
                                    autoPlaylist = true,
                                    sharedArtworkId = "downloaded",
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    thumbnailOverrideUrl = downloadThumbnail.takeIf { it.isNotBlank() },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("auto_playlist/downloaded")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = downloadPlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                                )
                            }
                        }

                        if (showTop) {
                            item(
                                key = "TopPlaylist",
                                contentType = { CONTENT_TYPE_PLAYLIST },
                            ) {
                                PlaylistListItem(
                                    playlist = topPlaylist,
                                    autoPlaylist = true,
                                    sharedArtworkId = topSize.toString(),
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    thumbnailOverrideUrl = topThumbnail.takeIf { it.isNotBlank() },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("top_playlist/$topSize")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = topPlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                                )
                            }
                        }

                        if (showCached) {
                            item(
                                key = "cachePlaylist",
                                contentType = { CONTENT_TYPE_PLAYLIST },
                            ) {
                                PlaylistListItem(
                                    playlist = cachePlaylist,
                                    autoPlaylist = true,
                                    sharedArtworkId = "cached",
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    thumbnailOverrideUrl = cacheThumbnail.takeIf { it.isNotBlank() },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("cache_playlist/cached")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = cachePlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                            )
                        }
                    }

                    if (showUploaded) {
                        item(
                            key = "uploadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistListItem(
                                playlist = uploadedPlaylist,
                                autoPlaylist = true,
                                sharedArtworkId = "uploaded",
                                flat = true,
                                showIconOnly = true,
                                thumbnailOverrideUrl = uploadedThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("auto_playlist/uploaded")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = uploadedPlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                            )
                        }
                    }

                    if (showLocal) {
                        item(
                            key = "localPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistListItem(
                                playlist = localPlaylist,
                                autoPlaylist = true,
                                sharedArtworkId = "local",
                                flat = true,
                                showIconOnly = true,
                                thumbnailOverrideUrl = localThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("auto_playlist/local")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = localPlaylist,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                        autoPlaylist = true
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem(),
                            )
                        }
                    }

                    items(
                        items = allItems.distinctBy { it.id },
                        key = { it.id },
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) { item ->
                        when (item) {
                            is Playlist -> {
                                PlaylistListItem(
                                    playlist = item,
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            SymbolIcon(bounceOnEnter = true, symbol = sf("ellipsis"), contentDescription = null)
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(localPlaylistRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Artist -> {
                                ArtistListItem(
                                    artist = item,
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            SymbolIcon(bounceOnEnter = true, symbol = sf("ellipsis"), contentDescription = null)
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(artistRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Album -> {
                                AlbumListItem(
                                    album = item,
                                    isActive = item.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    flat = true,
                                    showIconOnly = libraryIconsOnly,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            SymbolIcon(bounceOnEnter = true, symbol = sf("ellipsis"), contentDescription = null)
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(albumRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            else -> {}
                        }
                    }
                }

            LibraryViewType.GRID ->
                LazyVerticalGrid(
                    state = lazyGridState,
                    modifier = Modifier,
                    columns = rememberGridColumns(),
                    contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                ) {
                    item(
                        key = "filter",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        filterContent()
                    }

                    item(
                        key = "header",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        headerContent()
                    }
                    if (pinnedPlaylists.isNotEmpty()) {
                        item(
                            key = "pinned_header",
                            span = { GridItemSpan(maxLineSpan) },
                            contentType = CONTENT_TYPE_HEADER,
                        ) {
                            Text(
                                text = stringResource(R.string.pinned),
                                style = AppTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = AppleTokens.Gutter, vertical = 8.dp)
                                    .animateItem(),
                            )
                        }
                        item(
                            key = "pinned_row",
                            span = { GridItemSpan(maxLineSpan) },
                            contentType = "pinned_row",
                        ) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = AppleTokens.Gutter),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem(),
                            ) {
                                items(pinnedPlaylists, key = { "pinned_${it.id}" }) { playlist ->
                                    PlaylistGridItem(
                                        playlist = playlist,
                                        fillMaxWidth = false,
                                        modifier = Modifier
                                            .width(150.dp)
                                            .animateItem()
                                            .combinedBounceClick(
                                                onClick = { navController.navigate("local_playlist/${playlist.id}") },
                                                onLongClick = {
                                                    menuState.show {
                                                        PlaylistMenu(
                                                            playlist = playlist,
                                                            coroutineScope = coroutineScope,
                                                            onDismiss = menuState::dismiss,
                                                        )
                                                    }
                                                },
                                            ),
                                    )
                                }
                            }
                        }
                    }
                    if (showLiked) {
                        item(
                            key = "likedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = likedPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = "liked",
                                showIconOnly = true,
                                thumbnailOverrideUrl = likedThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("auto_playlist/liked")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = likedPlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (showDownloaded) {
                        item(
                            key = "downloadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = downloadPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = "downloaded",
                                showIconOnly = true,
                                thumbnailOverrideUrl = downloadThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("auto_playlist/downloaded")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = downloadPlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (showTop) {
                        item(
                            key = "TopPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = topPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = topSize.toString(),
                                showIconOnly = true,
                                thumbnailOverrideUrl = topThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("top_playlist/$topSize")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = topPlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (showCached) {
                        item(
                            key = "cachePlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = cachePlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = "cached",
                                showIconOnly = true,
                                thumbnailOverrideUrl = cacheThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("cache_playlist/cached")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = cachePlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (showUploaded) {
                        item(
                            key = "uploadedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = uploadedPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = "uploaded",
                                showIconOnly = true,
                                thumbnailOverrideUrl = uploadedThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("auto_playlist/uploaded")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = uploadedPlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    if (showLocal) {
                        item(
                            key = "localPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = localPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
                                sharedArtworkId = "local",
                                showIconOnly = true,
                                thumbnailOverrideUrl = localThumbnail.takeIf { it.isNotBlank() },
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .combinedBounceClick(
                                        onClick = {
                                            navController.navigate("auto_playlist/local")
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                PlaylistMenu(
                                                    playlist = localPlaylist,
                                                    coroutineScope = coroutineScope,
                                                    onDismiss = menuState::dismiss,
                                                    autoPlaylist = true
                                                )
                                            }
                                        }
                                    )
                                    .animateItem(),
                            )
                        }
                    }

                    items(
                        items = allItems.distinctBy { it.id },
                        key = { it.id },
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) { item ->
                        when (item) {
                            is Playlist -> {
                                PlaylistGridItem(
                                    playlist = item,
                                    fillMaxWidth = true,
                                    showIconOnly = libraryIconsOnly,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(localPlaylistRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Artist -> {
                                ArtistGridItem(
                                    artist = item,
                                    fillMaxWidth = true,
                                    showIconOnly = libraryIconsOnly,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(artistRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Album -> {
                                AlbumGridItem(
                                    album = item,
                                    isActive = item.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    coroutineScope = coroutineScope,
                                    fillMaxWidth = true,
                                    showIconOnly = libraryIconsOnly,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate(albumRoute(item.id, item.thumbnailUrl))
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }

            when (viewType) {
                LibraryViewType.LIST -> HideOnScrollFAB(
                    lazyListState = lazyListState,
                    icon = R.drawable.add,
                    onClick = { showCreatePlaylistDialog = true },
                )
                LibraryViewType.GRID -> HideOnScrollFAB(
                    lazyListState = lazyGridState,
                    icon = R.drawable.add,
                    onClick = { showCreatePlaylistDialog = true },
                )
            }
        }
      }
    }
}
