/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.utils.sideBarInset
import com.convx.music.ui.utils.plusStart
import com.convx.music.ui.utils.bleedStart
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.innertube.models.YTItem
import com.convx.music.ui.screens.library.apple.AppleSubpage
import com.convx.music.ui.screens.library.apple.SfStyle
import com.convx.music.ui.screens.library.apple.YtPopTile
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.utils.rememberGridColumns
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridThumbnailHeight
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.HeroBackground
import com.convx.music.ui.utils.rememberHeroZoom
import com.convx.music.ui.utils.heroPullZoom
import com.convx.music.ui.utils.listOverscroll
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.LargeScreenTitle
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.YouTubeGridItem
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.rememberHeroSource
import com.convx.music.ui.component.rememberHeroTopBlur
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.menu.YouTubeAlbumMenu
import com.convx.music.ui.menu.YouTubeArtistMenu
import com.convx.music.ui.menu.YouTubePlaylistMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.HeroTintedContent
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.utils.combinedBounceClick
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.viewmodels.YouTubeBrowseViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
/**
 * A Search category (Apple Music's "Spatial Audio"-style page): the plain page with a
 * glass "‹ Search" back pill and the category's large title collapsing into the bar;
 * the first shelf as large editorial cards (eyebrow, title, line, wide artwork), then
 * every other shelf as its own titled row of 154dp covers. Every card pops Apple's menu
 * on hold.
 */
@Composable
fun YouTubeBrowseScreen(
    navController: NavController,
    viewModel: YouTubeBrowseViewModel = koinViewModel(),
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()
    val browseResult by viewModel.result.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasError by viewModel.hasError.collectAsStateWithLifecycle()

    fun open(item: YTItem) {
        when (item) {
            is SongItem -> playerConnection.playQueue(
                YouTubeQueue(com.music.innertube.models.WatchEndpoint(videoId = item.id), item.toMediaMetadata()),
            )
            is AlbumItem -> navController.navigate(albumRoute(item.id, item.thumbnail))
            is ArtistItem -> navController.navigate(artistRoute(item.id, item.thumbnail))
            is PlaylistItem -> navController.navigate(onlinePlaylistRoute(item.id, item.title, item.thumbnail))
        }
    }

    fun sheet(item: YTItem) {
        menuState.show {
            when (item) {
                is SongItem -> YouTubeSongMenu(song = item, navController = navController, onDismiss = menuState::dismiss)
                is AlbumItem -> YouTubeAlbumMenu(albumItem = item, navController = navController, onDismiss = menuState::dismiss)
                is ArtistItem -> YouTubeArtistMenu(artist = item, onDismiss = menuState::dismiss)
                is PlaylistItem -> YouTubePlaylistMenu(playlist = item, coroutineScope = coroutineScope, onDismiss = menuState::dismiss)
            }
        }
    }

    // A radio mix can sit in two shelves at once; each shelf keeps its own copy, but
    // one shelf never lists an item twice (LazyRow keys).
    val shelves = browseResult?.items.orEmpty()
        .map { it.title to it.items.distinctBy { item -> item.id } }
        .filter { it.second.isNotEmpty() }

    AppleSubpage(
        title = browseResult?.title.orEmpty(),
        onBack = { navController.navigateUp() },
        backLabel = stringResource(R.string.search),
    ) {
        if (shelves.isEmpty()) {
            item(key = "browse_status", contentType = "status") {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        isLoading -> CircularWavyProgressIndicator()
                        hasError -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.home_category_load_failed), color = IosTheme.colors.label)
                            TextButton(onClick = viewModel::refresh) { Text(stringResource(R.string.retry)) }
                        }
                        else -> Text(stringResource(R.string.no_results_found), color = IosTheme.colors.secondaryLabel)
                    }
                }
            }
        }
        shelves.forEachIndexed { index, (title, items) ->
            if (index == 0) {
                item(key = "hero", contentType = "hero") {
                    val sideInset = sideBarInset()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp).plusStart(sideInset),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp).bleedStart(sideInset),
                    ) {
                        items(items, key = { "hero_" + it.id }, contentType = { "hero_card" }) { item ->
                            YtPopTile(
                                item = item,
                                navController = navController,
                                player = playerConnection,
                                subtitle = item.browseSubtitle(),
                                onClick = { open(item) },
                                onMore = { sheet(item) },
                            ) { EditorialCard(item) }
                        }
                    }
                }
            } else {
                item(key = "title_$index", contentType = "shelf_title") {
                    Text(
                        text = title.orEmpty(),
                        style = SfStyle(22.sp, 28.sp, FontWeight.Bold, (-0.26).sp),
                        color = IosTheme.colors.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 10.dp),
                    )
                }
                item(key = "shelf_$index", contentType = "shelf") {
                    val sideInset = sideBarInset()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp).plusStart(sideInset),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.bleedStart(sideInset),
                    ) {
                        items(items, key = { "s${index}_" + it.id }, contentType = { "shelf_card" }) { item ->
                            YtPopTile(
                                item = item,
                                navController = navController,
                                player = playerConnection,
                                subtitle = item.browseSubtitle(),
                                onClick = { open(item) },
                                onMore = { sheet(item) },
                            ) { ShelfCard(item) }
                        }
                    }
                }
            }
        }
    }
}

internal fun YTItem.browseSubtitle(): String = when (this) {
    is SongItem -> artists.joinToString { it.name }
    is AlbumItem -> artists?.joinToString { it.name }.orEmpty()
    is PlaylistItem -> author?.name ?: songCountText.orEmpty()
    is ArtistItem -> ""
}

@Composable
private fun YTItem.eyebrow(): String = stringResource(
    when (this) {
        is SongItem -> R.string.apple_kind_song
        is AlbumItem -> R.string.apple_kind_album
        is PlaylistItem -> R.string.apple_kind_playlist
        is ArtistItem -> R.string.apple_kind_artist
    },
).uppercase()

/**
 * Apple's editorial card: small grey caps eyebrow, the title, a secondary line, then
 * the artwork wide (3:2, 10dp corners) — 88% of the page wide so the next one peeks.
 */
@Composable
internal fun EditorialCard(item: YTItem) {
    val width = (LocalConfiguration.current.screenWidthDp * 0.88f).dp.coerceAtMost(360.dp)
    val colors = IosTheme.colors
    Column(Modifier.width(width)) {
        Text(item.eyebrow(), style = SfStyle(11.sp, 13.sp, FontWeight.SemiBold, 0.06.sp), color = colors.secondaryLabel, maxLines = 1)
        Text(
            item.title,
            style = SfStyle(20.sp, 25.sp, tracking = (-0.45).sp),
            color = colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            item.browseSubtitle(),
            style = SfStyle(20.sp, 25.sp, tracking = (-0.45).sp),
            color = colors.secondaryLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        AsyncImage(
            model = item.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clip(ContinuousRoundedRectangle(10.dp))
                .background(colors.secondarySystemFill),
        )
    }
}

/** A 154dp cover card with rounded corners and title and subtitle text. */
@Composable
internal fun ShelfCard(item: YTItem) {
    val colors = IosTheme.colors
    val shape = if (item is ArtistItem) androidx.compose.foundation.shape.CircleShape else ContinuousRoundedRectangle(10.dp)
    Column(Modifier.width(154.dp)) {
        AsyncImage(
            model = item.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(154.dp)
                .clip(shape)
                .background(colors.secondarySystemFill),
        )
        Spacer(Modifier.height(6.dp))
        Text(item.title, style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp), color = colors.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(item.browseSubtitle(), style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp), color = colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
