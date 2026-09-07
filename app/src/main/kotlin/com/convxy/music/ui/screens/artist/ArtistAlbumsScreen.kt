/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.artist

import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.activity.compose.BackHandler
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.items
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHost
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHostState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateListOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.listSaver
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.toMutableStateList
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.pluralStringResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.rememberGridColumns
import com.convxy.music.constants.CONTENT_TYPE_ALBUM
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.CONTENT_TYPE_HEADER
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.GridItemSize
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.GridItemsSizeKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.GridThumbnailHeight
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.ListScrollRail
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.LibraryAlbumGridItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.viewmodels.ArtistAlbumsViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistAlbumsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistAlbumsViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val artist by viewModel.artist.collectAsState()
    val albums by viewModel.albums.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }
    if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val visibleAlbums = remember(albums) { albums.distinctBy { it.id } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = rememberGridColumns(),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
        ) {
            item(
                key = "header",
                span = { GridItemSpan(maxLineSpan) },
                contentType = CONTENT_TYPE_HEADER
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = pluralStringResource(R.plurals.n_album, albums.size, albums.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            items(
                items = visibleAlbums,
                key = { it.id },
                contentType = { CONTENT_TYPE_ALBUM }
            ) { album ->
                LibraryAlbumGridItem(
                    navController = navController,
                    menuState = menuState,
                    coroutineScope = coroutineScope,
                    album = album,
                    isActive = album.id == mediaMetadata?.album?.id,
                    isPlaying = isPlaying,
                    modifier = Modifier.animateItem()
                )
            }
        }

        // No sort control on this screen -- albums arrive in release order -- so the rail
        // is a proportional thumb rather than letters.
        ListScrollRail(
            lazyGridState = lazyGridState,
            itemCount = visibleAlbums.size,
            sectionIndexMap = null,
        )

        TopAppBar(
            windowInsets = appTopBarWindowInsets(),
            title = { Text(artist?.artist?.name.orEmpty()) },
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )
    }
}
