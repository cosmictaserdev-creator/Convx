/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.utils.rememberGridColumns
import com.convxy.music.constants.AlbumViewTypeKey
import com.convxy.music.constants.CONTENT_TYPE_HEADER
import com.convxy.music.constants.CONTENT_TYPE_PLAYLIST
import com.convxy.music.constants.GridItemSize
import com.convxy.music.constants.GridItemsSizeKey
import com.convxy.music.constants.GridThumbnailHeight
import com.convxy.music.constants.LibraryBackgroundMode
import com.convxy.music.constants.LibraryBackgroundModeKey
import com.convxy.music.constants.LibraryIconsOnlyKey
import com.convxy.music.constants.LibraryViewType
import com.convxy.music.constants.MixSortDescendingKey
import com.convxy.music.constants.MixSortType
import com.convxy.music.constants.MixSortTypeKey
import com.convxy.music.constants.ShowCachedPlaylistKey
import com.convxy.music.constants.ShowDownloadedPlaylistKey
import com.convxy.music.constants.ShowLikedPlaylistKey
import com.convxy.music.constants.ShowLocalPlaylistKey
import com.convxy.music.constants.ShowTopPlaylistKey
import com.convxy.music.constants.ShowUploadedPlaylistKey
import com.convxy.music.db.entities.Album
import com.convxy.music.db.entities.Artist
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.PlaylistEntity
import com.convxy.music.extensions.reversed
import com.convxy.music.ui.component.AlbumGridItem
import com.convxy.music.ui.component.AlbumListItem
import com.convxy.music.ui.component.ArtistGridItem
import com.convxy.music.ui.component.ArtistListItem
import com.convxy.music.ui.component.HeroBackground
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.component.PlaylistGridItem
import com.convxy.music.ui.component.CreatePlaylistDialog
import com.convxy.music.ui.component.HideOnScrollFAB
import com.convxy.music.ui.component.hasCustomHomeBackground
import com.convxy.music.ui.component.rememberAppBackgroundColor
import com.convxy.music.ui.component.PlaylistListItem
import com.convxy.music.ui.component.SortHeader
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.rememberHeroSource
import com.convxy.music.ui.component.rememberHeroTint
import com.convxy.music.ui.menu.AlbumMenu
import com.convxy.music.ui.menu.ArtistMenu
import com.convxy.music.ui.menu.PlaylistMenu
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.HeroTintedContent
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.constants.LocalOnlyModeKey
import com.convxy.music.utils.rememberPreference
import com.convxy.music.viewmodels.LibraryMixViewModel
import java.text.Collator
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryMixScreen(
    navController: NavController,
    filterContent: @Composable () -> Unit,
    viewModel: LibraryMixViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

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

    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    val libraryBackgroundMode by rememberEnumPreference(LibraryBackgroundModeKey, LibraryBackgroundMode.THUMBNAIL_BLUR)

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
        rememberAppBackgroundColor(MaterialTheme.colorScheme.primary)
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
    val (localOnly) = rememberPreference(LocalOnlyModeKey, false)
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
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

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

            FlowRow(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                LibraryViewType.entries.forEachIndexed { index, type ->
                    ToggleButton(
                        checked = viewType == type,
                        onCheckedChange = { viewType = type },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            LibraryViewType.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        modifier = Modifier.semantics { role = Role.RadioButton },
                    ) {
                        Icon(
                            painter = painterResource(
                                when (type) {
                                    LibraryViewType.LIST -> R.drawable.list
                                    LibraryViewType.GRID -> R.drawable.grid_view
                                }
                            ),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }

    val isRefreshing by viewModel.isRefreshing.collectAsState()
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

                        if (showLiked) {
                            item(
                                key = "likedPlaylist",
                                contentType = { CONTENT_TYPE_PLAYLIST },
                            ) {
                                PlaylistListItem(
                                    playlist = likedPlaylist,
                                    autoPlaylist = true,
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
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("local_playlist/${item.id}")
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
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("artist/${item.id}")
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
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedBounceClick(
                                            onClick = {
                                                navController.navigate("album/${item.id}")
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
                    if (showLiked) {
                        item(
                            key = "likedPlaylist",
                            contentType = { CONTENT_TYPE_PLAYLIST },
                        ) {
                            PlaylistGridItem(
                                playlist = likedPlaylist,
                                fillMaxWidth = true,
                                autoPlaylist = true,
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
                                                navController.navigate("local_playlist/${item.id}")
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
                                                navController.navigate("artist/${item.id}")
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
                                                navController.navigate("album/${item.id}")
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
