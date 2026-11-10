/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.screens.library.FolderSymbol
import com.convx.music.viewmodels.NetworkBrowserViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.LocalItem
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.library.PlaylistSummary
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.ListItem
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.PlaylistListItem
import com.convx.music.ui.component.SpotlightCard
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.AppPage
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.plusStart
import com.convx.music.ui.utils.localPlaylistRoute
import com.convx.music.ui.utils.sharedArtworkSource
import com.convx.music.utils.LocalFolderIndex
import com.convx.music.utils.listItemShape
import kotlinx.coroutines.flow.first

/**
 * Home while local-only mode is on: the browse hub a local-first player shows —
 * songs, albums, artists, playlists, folders — sharing this screen's list, chrome
 * and pull-to-refresh rather than living on a route of its own.
 */
@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.localHomeContent(
    deps: HomeSectionDeps,
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<PlaylistSummary>,
    folders: List<LocalFolderIndex.Folder>,
    columns: Int,
) {
    // Build local shelves from the lists already loaded by the view model.
    if (songs.isEmpty()) {
        item(key = "local_empty", contentType = "empty") {
            Text(
                text = stringResource(R.string.no_local_files),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppPage.Gutter),
            )
        }
        networkSourcesSection(deps)
        return
    }

    localSongShelf(
        deps = deps,
        key = "local_recently_added",
        titleRes = R.string.recently_added,
        songs = songs.sortedByDescending { it.song.inLibrary ?: it.song.dateModified }
            .take(LocalSpotlightCount),
    )

    // Only once something has actually been played -- a shelf of zero-play songs is
    // "recently added" again in a different order.
    val mostPlayed = songs.filter { it.song.totalPlayTime > 0 }
        .sortedByDescending { it.song.totalPlayTime }
        .take(LocalSpotlightCount)
    localSongShelf(
        deps = deps,
        key = "local_most_played",
        titleRes = R.string.most_played_songs,
        songs = mostPlayed,
    )

    if (albums.isNotEmpty()) {
        item(key = "local_albums_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.albums),
                modifier = Modifier.animateItem(),
            )
        }
        localItemRows(deps, "local_album", albums.take(LocalShelfRows * columns), columns)
    }

    if (artists.isNotEmpty()) {
        item(key = "local_artists_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.artists),
                modifier = Modifier.animateItem(),
            )
        }
        localItemRows(deps, "local_artist", artists.take(LocalShelfRows * columns), columns)
    }

    if (playlists.isNotEmpty()) {
        item(key = "local_playlists_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.playlists),
                modifier = Modifier.animateItem(),
            )
        }
        val shownPlaylists = playlists.take(LocalListPreview)
        itemsIndexed(
            items = shownPlaylists,
            key = { _, playlist -> "local_playlist_" + playlist.id },
            contentType = { _, _ -> "local_playlist" },
        ) { index, playlist ->
            PlaylistListItem(
                playlist = playlist,
                shape = listItemShape(index, shownPlaylists.size),
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { deps.navController.navigate(localPlaylistRoute(playlist.id, playlist.artworkUrls.firstOrNull())) },
            )
        }
    }

    if (folders.isNotEmpty()) {
        item(key = "local_folders_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.folders),
                modifier = Modifier.animateItem(),
            )
        }
        // Build this lookup once so each folder row can find its thumbnail in O(1).
        val songById = songs.associateBy { it.id }
        val shownFolders = folders.take(LocalListPreview)

        itemsIndexed(
            items = shownFolders,
            key = { _, folder -> "local_folder_" + folder.path },
            contentType = { _, _ -> "local_folder" },
        ) { index, folder ->
            val thumbnailUrl = remember(folder, songById) {
                folder.songIds.firstNotNullOfOrNull { songById[it]?.song?.thumbnailUrl }
            }
            ListItem(
                title = folder.name,
                subtitle = pluralStringResource(
                    R.plurals.n_song,
                    folder.songIds.size,
                    folder.songIds.size,
                ),
                thumbnailContent = {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.thumbnail_fallback),
                        error = painterResource(R.drawable.thumbnail_fallback),
                        modifier = Modifier
                            .size(ListThumbnailSize)
                            .clip(RoundedCornerShape(AppleTokens.Control))
                            .sharedArtworkSource(folder.path, thumbnailUrl),
                    )
                },
                shape = listItemShape(index, shownFolders.size),
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick {
                        deps.navController.navigate("local_folder/" + Uri.encode(folder.path))
                    },
            )
        }
    }

    networkSourcesSection(deps)
}

/** SMB and WebDAV sources: each opens its folder tree, and the last row adds another. */
internal fun LazyListScope.networkSourcesSection(deps: HomeSectionDeps) {
    item(key = "local_network_title", contentType = "section_title") {
        NavigationTitle(
            title = stringResource(R.string.network_storage),
            modifier = Modifier.animateItem(),
        )
    }
    item(key = "local_network_sources", contentType = "network_sources") {
        NetworkSources(deps.navController)
    }
}

@Composable
private fun NetworkSources(navController: NavController, viewModel: NetworkBrowserViewModel = koinViewModel()) {
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxWidth()) {
        servers.forEachIndexed { index, server ->
            ListItem(
                title = server.name,
                subtitle = server.protocol.name,
                thumbnailContent = { SymbolIcon(symbol = sf(FolderSymbol), contentDescription = null, size = 28.dp) },
                shape = listItemShape(index, servers.size + 1),
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick { navController.navigate("network_browse/" + server.id) },
            )
        }
        ListItem(
            title = stringResource(R.string.network_storage_add),
            subtitle = "",
            thumbnailContent = { SymbolIcon(symbol = sf("plus"), contentDescription = null, size = 28.dp) },
            shape = listItemShape(servers.size, servers.size + 1),
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick { navController.navigate("settings/network_storage") },
        )
    }
}

/**
 * One horizontal shelf of songs, drawn with the card the online Home leads with.
 *
 * LazyRow preserves velocity-based fling distance across the shelf.
 */
internal fun LazyListScope.localSongShelf(
    deps: HomeSectionDeps,
    key: String,
    @StringRes titleRes: Int,
    songs: List<Song>,
) {
    if (songs.isEmpty()) return

    item(key = key + "_title", contentType = "section_title") {
        NavigationTitle(
            title = stringResource(titleRes),
            modifier = Modifier.animateItem(),
            onPlayAllClick = {
                deps.playerConnection.playQueue(
                    ListQueue(items = songs.map { it.toMediaItem() }, startIndex = 0),
                )
            },
        )
    }
    item(key = key, contentType = "hero_rail") {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp).plusStart(deps.sideInset),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().animateItem().bleedStart(deps.sideInset),
        ) {
            items(songs, key = { it.id }, contentType = { "figma_hero" }) { song ->
                HomePopMenu(
                    HomePop.Local(song),
                    deps,
                    song.song.thumbnailUrl,
                    song.song.title,
                    song.artists.joinToString { it.name },
                    { deps.onLocalItemClick(song) },
                ) {
                    FigmaTopPickCard(
                        thumbnail = song.song.thumbnailUrl,
                        title = song.song.title,
                        label = song.artists.firstOrNull()?.name,
                        detail = song.album?.title,
                        artworkKey = song.album?.id,
                    )
                }
            }
        }
    }
}

/**
 * Grid rows of [LocalItem]s laid out as rows of the outer list, the same shape
 * (and for the same reason) as the recently-played section above.
 */
internal fun LazyListScope.localItemRows(
    deps: HomeSectionDeps,
    keyPrefix: String,
    items: List<LocalItem>,
    columns: Int,
) {
    items.chunked(columns).forEachIndexed { index, rowItems ->
        item(key = "${keyPrefix}_row_$index", contentType = "local_row") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppPage.Gutter - AppleTokens.ItemGap / 2),
            ) {
                rowItems.forEach { localItem ->
                    Box(modifier = Modifier.weight(1f)) {
                        deps.localGridItem(localItem)
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


/** Cards on local mode's spotlight shelf. A shelf, not the library. */
internal const val LocalSpotlightCount = 15
