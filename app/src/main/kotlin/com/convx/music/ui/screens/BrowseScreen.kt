/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens
 
 import androidx.compose.foundation.ExperimentalFoundationApi
 import androidx.compose.foundation.layout.Box
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.padding
 import androidx.compose.ui.Alignment
 
 import androidx.compose.foundation.layout.asPaddingValues
 import androidx.compose.foundation.lazy.grid.GridCells
 import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
 import androidx.compose.foundation.lazy.grid.items
 import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.Icon
 import androidx.compose.material3.Text
 import androidx.compose.material3.TextButton
 import com.convx.music.ui.ios.compat.TopAppBar
 import androidx.compose.material3.TopAppBarScrollBehavior
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.rememberCoroutineScope
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.res.painterResource
 import androidx.compose.ui.res.stringResource
 import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
 import androidx.navigation.NavController
 import com.music.innertube.models.AlbumItem
 import com.music.innertube.models.ArtistItem
 import com.music.innertube.models.PlaylistItem
 import com.music.innertube.models.SongItem
 import com.music.innertube.models.WatchEndpoint
 import com.convx.music.LocalPlayerAwareWindowInsets
 import com.convx.music.LocalPlayerConnection
 import com.convx.music.R
 import com.convx.music.models.toMediaMetadata
 import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.utils.rememberGridColumns
 import com.convx.music.constants.GridItemSize
 import com.convx.music.constants.GridItemsSizeKey
 import com.convx.music.constants.GridThumbnailHeight
 import com.convx.music.ui.component.IconButton
 import com.convx.music.ui.component.LocalMenuState
 import com.convx.music.ui.component.YouTubeGridItem
 import com.convx.music.ui.component.shimmer.GridItemPlaceHolder
 import com.convx.music.ui.component.shimmer.ShimmerHost
 import com.convx.music.ui.menu.YouTubeAlbumMenu
 import com.convx.music.ui.menu.YouTubeArtistMenu
 import com.convx.music.ui.menu.YouTubePlaylistMenu
 import com.convx.music.ui.utils.backToMain
 import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.utils.rememberEnumPreference
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.convx.music.ui.component.ScreenLargeTitle
import com.convx.music.ui.component.ScreenScaffold
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
 import com.convx.music.viewmodels.BrowseViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
 @Composable
 fun BrowseScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    browseId: String?,
    viewModel: BrowseViewModel = koinViewModel(),
) {
     val menuState = LocalMenuState.current
     val playerConnection = LocalPlayerConnection.current ?: return
     val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
     val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
 
     val title by viewModel.title.collectAsStateWithLifecycle()
     val browseItems by viewModel.items.collectAsStateWithLifecycle()
     val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
     val hasError by viewModel.hasError.collectAsStateWithLifecycle()
 
     val coroutineScope = rememberCoroutineScope()
     val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
 
     val gridState = rememberLazyGridState()
     val browseTitleProgress = rememberTitleCollapseProgressState(gridState)

ScreenScaffold(
        title = title ?: "",
        titleAlpha = 0f,
        titleAlphaProvider = { browseTitleProgress.value },
        onBack = navController::navigateUp,
        onBackLongClick = navController::backToMain,
    ) { chrome ->
     LazyVerticalGrid(
         state = gridState,
         columns = rememberGridColumns(),
         modifier = chrome.contentModifier(),
         contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
     ) {
         item(key = "title", span = { GridItemSpan(maxLineSpan) }) {
             ScreenLargeTitle(title ?: "")
         }

         if (!browseItems.isNullOrEmpty()) {
             items(
                 items = browseItems.orEmpty().distinctBy { it.id },
                 key = { it.id }
             ) { item ->
                 YouTubeGridItem(
                     item = item,
                     isPlaying = isPlaying,
                     fillMaxWidth = true,
                     coroutineScope = coroutineScope,
                     modifier = Modifier
                         .combinedBounceClick(
                             onClick = {
                                 when (item) {
                                      is SongItem -> playerConnection.playQueue(
                                          YouTubeQueue(WatchEndpoint(videoId = item.id), item.toMediaMetadata()),
                                      )
                                      is AlbumItem -> navController.navigate(albumRoute(item.id, item.thumbnail))
                                      is PlaylistItem -> navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
                                      is ArtistItem -> navController.navigate(artistRoute(item.id, item.thumbnail))
else -> {
                                    }
                                 }
                             },
                             onLongClick = {
                                 menuState.show {
                                     when (item) {
                                         is AlbumItem ->
                                             YouTubeAlbumMenu(
                                                 albumItem = item,
                                                 navController = navController,
                                                 onDismiss = menuState::dismiss
                                             )
 
                                         is PlaylistItem -> {
                                             YouTubePlaylistMenu(
                                                 playlist = item,
                                                 coroutineScope = coroutineScope,
                                                 onDismiss = menuState::dismiss
                                             )
                                         }
 
                                         is ArtistItem -> {
                                             YouTubeArtistMenu(
                                                 artist = item,
                                                 onDismiss = menuState::dismiss
                                             )
                                         }
 
else -> {
                                        }
                                     }
                                 }
                             }
                         )
                 )
             }
 
         } else if (isLoading) {
                 items(8) {
                     ShimmerHost {
                         GridItemPlaceHolder(fillMaxWidth = true)
                     }
                 }
         } else {
             item(key = "browse_empty", span = { GridItemSpan(maxLineSpan) }) {
                 Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                     if (hasError) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Text(stringResource(R.string.home_category_load_failed))
                             TextButton(onClick = viewModel::retry) { Text(stringResource(R.string.retry)) }
                         }
                     } else {
                         Text(stringResource(R.string.no_results_found))
                     }
                 }
             }
         }
     }
 
     }
 }
