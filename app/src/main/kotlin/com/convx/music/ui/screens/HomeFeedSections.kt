/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import com.convx.music.ui.screens.search.CategoryTile
import com.convx.music.ui.screens.search.AppleSearchViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFPersonCropCircle
import com.convx.music.R
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.constants.ThumbnailCornerRadius
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.extensions.togglePlayPause
import com.convx.music.models.MediaMetadata
import com.convx.music.models.SimilarRecommendation
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.HomeHeroCard
import com.convx.music.ui.component.LocalAppMotionFrozen
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.component.SongListItem
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.AppPage
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.bleedStart
import com.convx.music.ui.utils.onlinePlaylistRoute
import com.convx.music.ui.utils.sharedArtworkSource
import com.convx.music.ui.utils.youtubeBrowseRoute
import com.convx.music.ui.utils.plusStart
import com.convx.music.utils.listItemShape
import com.convx.music.viewmodels.CommunityPlaylistItem
import com.convx.music.viewmodels.DailyDiscoverItem
import com.music.innertube.YouTube
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.HomePage
import com.music.innertube.pages.MoodAndGenres
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
internal fun LazyListScope.homeSectionsContent(
    sections: List<HomeSection>,
    deps: HomeSectionDeps,
    ui: com.convx.music.viewmodels.HomeUiState,
    mediaMetadata: State<MediaMetadata?>,
    isPlaying: State<Boolean>,
    randomizeJob: MutableState<Job?>,
    quickPicksSongMap: Map<String, Song>,
    forgottenFavoritesSongMap: Map<String, Song>,
    url: String?,
) {
    sections.forEach { section ->
        when (section) {
            HomeSection.Hero -> heroSection(deps, ui.quickPicks)
            HomeSection.SpeedDial -> speedDialSection(
                deps = deps,
                items = ui.speedDialItems,
                isRandomizing = ui.isRandomizing,
                randomizeJob = randomizeJob,
            )
            HomeSection.QuickPicks -> quickPicksSection(deps, ui.quickPicks, mediaMetadata, quickPicksSongMap)
            HomeSection.FromTheCommunity -> communityPlaylistsSection(deps, ui.communityPlaylists)
            HomeSection.DailyDiscover -> dailyDiscoverSection(deps, ui.dailyDiscover)
            HomeSection.ListenAgain -> listenAgainSection(deps, ui.listenAgain)
            HomeSection.TopSongs ->
                topSongsSection(deps, ui.topSongs, mediaMetadata, isPlaying)
            HomeSection.AccountPlaylists -> accountPlaylistsSection(
                deps = deps,
                accountPlaylists = ui.accountPlaylists,
                accountName = ui.accountName,
                url = url,
            )
            HomeSection.ForgottenFavorites -> forgottenFavoritesSection(
                deps, ui.forgottenFavorites, mediaMetadata, forgottenFavoritesSongMap, isPlaying,
            )
            is HomeSection.SimilarRecommendation -> similarRecommendationsSection(
                section, deps, ui.similarRecommendations,
            )
            is HomeSection.HomePageSection -> homePageSection(section, deps, ui.homePage)
            HomeSection.MoodAndGenres -> moodAndGenresSection(deps, ui.explorePage)
        }
    }
}


internal fun LazyListScope.heroSection(
    deps: HomeSectionDeps,
    quickPicks: List<Song>?,
) {
    quickPicks?.firstOrNull()?.let { top ->
        item(key = "hero_card", contentType = "hero_card") {
            val motionFrozen = LocalAppMotionFrozen.current
            val meshPaused = remember(motionFrozen, deps.isScrolling) {
                { motionFrozen.value || deps.isScrolling() }
            }
            val heroSubtitle = remember(top.id) {
                top.artists.joinToString { it.name }
            }
            HomeHeroCard(
                title = top.song.title,
                subtitle = heroSubtitle,
                thumbnailUrl = top.song.thumbnailUrl,
                meshEnabled = true,
                meshPaused = meshPaused,
                onPlay = {
                    deps.playerConnection.playQueue(
                        ListQueue(
                            title = top.song.title,
                            items = quickPicks.orEmpty().map { it.toMediaItem() },
                        )
                    )
                },
                onShuffle = {
                    deps.playerConnection.playQueue(
                        ListQueue(
                            title = top.song.title,
                            items = quickPicks.orEmpty().shuffled().map { it.toMediaItem() },
                        )
                    )
                },
                onClick = {
                    deps.playerConnection.playQueue(
                        ListQueue(
                            title = top.song.title,
                            items = listOf(top.toMediaItem()),
                        )
                    )
                },
                modifier = Modifier
                    .padding(horizontal = AppPage.Gutter)
                    .animateItem(),
            )
        }
    }
}

internal fun LazyListScope.speedDialSection(
    deps: HomeSectionDeps,
    items: List<YTItem>,
    isRandomizing: Boolean,
    randomizeJob: MutableState<Job?>,
) {
    items.takeIf { it.isNotEmpty() }?.let { items ->
        item(key = "speed_dial_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.speed_dial),
                modifier = Modifier.animateItem(),
            )
        }
        // The randomize tile rides at the tail of the shelf rather than in a reserved
        // slot of a paging grid: with one row there is no grid to reserve a cell in,
        // and the tile is a "if nothing here appeals" action, which belongs at the end.
        val entries = items.map { it.toShelfEntry(deps) } + ShelfEntry(
            id = "speed_dial_randomize",
            thumbnailUrl = null,
            title = "Surprise me",
            subtitle = if (isRandomizing) "Picking..." else "Play something at random",
            onClick = {
                if (isRandomizing) {
                    randomizeJob.value?.cancel()
                } else {
                    randomizeJob.value = deps.scope.launch {
                        deps.randomItem()?.let { deps.openYtItem(it) }
                    }
                }
            },
        )
        shelfList(key = "speed_dial_list", deps = deps, entries = entries)
    }
}

internal fun LazyListScope.quickPicksSection(
    deps: HomeSectionDeps,
    quickPicks: List<Song>?,
    mediaMetadata: State<MediaMetadata?>,
    songMap: Map<String, Song>,
) {
    quickPicks?.takeIf { it.isNotEmpty() }?.let { quickPicks ->
        item(key = "quick_picks_title", contentType = "section_title") {
            val quickPicksTitle = stringResource(R.string.quick_picks)
            NavigationTitle(
                title = quickPicksTitle,
                modifier = Modifier.animateItem(),
                onPlayAllClick = {
                    deps.playerConnection.playQueue(
                        ListQueue(
                            title = quickPicksTitle,
                            items = quickPicks.distinctBy { it.id }.map { it.toMediaItem() }
                        )
                    )
                },
            )
        }
        shelfList(
            key = "quick_picks_list",
            deps = deps,
            entries = quickPicks.distinctBy { it.id }
                .map { (songMap[it.id] ?: it).toShelfEntry(deps, mediaMetadata) },
        )
    }
}

internal fun LazyListScope.communityPlaylistsSection(
    deps: HomeSectionDeps,
    communityPlaylists: List<CommunityPlaylistItem>?,
) {
    communityPlaylists?.takeIf { it.isNotEmpty() }?.let { playlists ->
        item(key = "community_playlists_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.from_the_community),
                modifier = Modifier.animateItem(),
            )
        }
        shelfRow(
            key = "community_playlists_content",
            deps = deps,
            entries = playlists.map { item ->
                ShelfEntry(
                    id = item.playlist.id,
                    thumbnailUrl = item.playlist.thumbnail,
                    title = item.playlist.title,
                    subtitle = item.playlist.author?.name ?: item.playlist.songCountText.orEmpty(),
                    sharedArtworkId = item.playlist.id.removePrefix("VL"),
                    onClick = {
                        deps.navController.navigate(
                            onlinePlaylistRoute(
                                item.playlist.id.removePrefix("VL"),
                                item.playlist.title,
                                item.playlist.thumbnail,
                            )
                        )
                    },
                )
            },
            compact = true,
        )
    }
}

internal fun LazyListScope.dailyDiscoverSection(
    deps: HomeSectionDeps,
    dailyDiscover: List<DailyDiscoverItem>?,
) {
    dailyDiscover?.takeIf { it.isNotEmpty() }?.let { discoverList ->
        item(key = "daily_discover_title", contentType = "section_title") {
            val title = stringResource(R.string.your_daily_discover)
            NavigationTitle(
                title = title,
                onPlayAllClick = {
                    val queueItems = discoverList.mapNotNull {
                        (it.recommendation as? SongItem)?.toMediaMetadata()
                    }

                    if (queueItems.isNotEmpty()) {
                        deps.playerConnection.playQueue(
                            ListQueue(
                                title = title,
                                items = queueItems.map { it.toMediaItem() }
                            )
                        )
                    }
                },
            )
        }
        shelfRow(
            key = "daily_discover_content",
            deps = deps,
            // Distinct: the same recommendation can be reached from two different
            // seeds, and a repeated key is a hard crash in a lazy row.
            entries = discoverList.map { it.recommendation }
                .distinctBy { it.id }
                .map { it.toShelfEntry(deps) },
        )
    }
}

internal fun LazyListScope.listenAgainSection(
    deps: HomeSectionDeps,
    sectionData: HomePage.Section?,
) {
    remoteHomePageSection(deps, "listen_again", sectionData, RemoteShelfStyle.Compact)
}

/** Shows up to ten top songs in a compact numbered list. */
internal fun LazyListScope.topSongsSection(
    deps: HomeSectionDeps,
    topSongs: List<Song>?,
    mediaMetadata: State<MediaMetadata?>,
    isPlaying: State<Boolean>,
) {
    topSongs?.distinctBy { it.id }?.take(10)?.takeIf { it.isNotEmpty() }?.let { songs ->
        val title = deps.context.getString(R.string.most_played_songs)
        item(key = "top_songs_title", contentType = "section_title") {
            NavigationTitle(
                title = title,
                modifier = Modifier.animateItem(),
                onPlayAllClick = {
                    deps.playerConnection.playQueue(
                        ListQueue(title = title, items = songs.map { it.toMediaItem() }),
                    )
                },
            )
        }
        item(key = "top_songs_list", contentType = "hero_rail") {
            val queue = remember(songs) { songs.map { it.toMediaItem() } }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp).plusStart(deps.sideInset),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().animateItem().bleedStart(deps.sideInset),
            ) {
                itemsIndexed(songs, key = { _, song -> song.id }, contentType = { _, _ -> "figma_hero" }) { index, song ->
                    val artists = song.artists.joinToString { it.name }
                    val play = {
                        if (song.id == mediaMetadata.value?.id) {
                            deps.playerConnection.togglePlayPause()
                        } else {
                            deps.playerConnection.playQueue(ListQueue(title = title, items = queue, startIndex = index))
                        }
                    }
                    HomePopMenu(HomePop.Local(song), deps, song.song.thumbnailUrl, song.song.title, artists, play) {
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
}

internal fun LazyListScope.accountPlaylistsSection(
    deps: HomeSectionDeps,
    accountPlaylists: List<PlaylistItem>?,
    accountName: String,
    url: String?,
) {
    accountPlaylists?.takeIf { it.isNotEmpty() }?.let { accountPlaylists ->
        item(key = "account_playlists_title", contentType = "section_title") {
            NavigationTitle(
                label = stringResource(R.string.your_youtube_playlists),
                title = accountName,
                thumbnail = {
                    if (url != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .diskCacheKey(url)
                                .crossfade(false)
                                .build(),
                            placeholder = painterResource(id = R.drawable.person),
                            error = painterResource(id = R.drawable.person),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(ListThumbnailSize)
                                .clip(CircleShape)
                        )
                    } else {
                        SymbolIcon(symbol = sf("SFPersonCropCircle"), contentDescription = null, modifier = Modifier.size(ListThumbnailSize))
                    }
                },
                onClick = {
                    deps.navController.navigate("account")
                },
                modifier = Modifier.animateItem(),
            )
        }
        shelfRow(
            key = "account_playlists_list",
            deps = deps,
            entries = accountPlaylists.distinctBy { it.id }.map { it.toShelfEntry(deps) },
            compact = true,
        )
    }
}

internal fun LazyListScope.forgottenFavoritesSection(
    deps: HomeSectionDeps,
    forgottenFavorites: List<Song>?,
    mediaMetadata: State<MediaMetadata?>,
    songMap: Map<String, Song>,
    isPlaying: State<Boolean>,
) {
    forgottenFavorites
        ?.map { songMap[it.id] ?: it }
        ?.distinctBy { it.id }
        ?.take(8)
        ?.takeIf { it.isNotEmpty() }
        ?.let { songs ->
        val title = deps.context.getString(R.string.forgotten_favorites)
        item(key = "forgotten_favorites_title", contentType = "section_title") {
            NavigationTitle(
                title = title,
                modifier = Modifier.animateItem(),
                onPlayAllClick = {
                    deps.playerConnection.playQueue(
                        ListQueue(
                            title = title,
                            items = songs.map { it.toMediaItem() },
                        )
                    )
                },
            )
        }
        homeSongList("forgotten_favorites_list", title, songs, deps, mediaMetadata, isPlaying)
    }
}

private fun LazyListScope.homeSongList(
    key: String,
    queueTitle: String,
    songs: List<Song>,
    deps: HomeSectionDeps,
    mediaMetadata: State<MediaMetadata?>,
    isPlaying: State<Boolean>,
    numbered: Boolean = false,
) {
    val activeSongId = mediaMetadata.value?.id
    val playing = isPlaying.value
    val queue = songs.map { it.toMediaItem() }

    itemsIndexed(
        items = songs,
        key = { _, song -> "$key:${song.id}" },
        contentType = { _, _ -> "home_song_row" },
    ) { index, song ->
        HomePopMenu(
            pop = HomePop.Local(song),
            deps = deps,
            thumbnailUrl = song.song.thumbnailUrl,
            title = song.song.title,
            subtitle = song.artists.joinToString { it.name },
            onClick = {
                if (song.id == activeSongId) {
                    deps.playerConnection.togglePlayPause()
                } else {
                    deps.playerConnection.playQueue(
                        ListQueue(title = queueTitle, items = queue, startIndex = index),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().animateItem(),
        ) {
            SongListItem(
                song = song,
                albumIndex = if (numbered) index + 1 else null,
                isActive = song.id == activeSongId,
                isPlaying = playing,
                isSwipeable = false,
                drawHighlight = false,
                shape = listItemShape(index, songs.size),
                flat = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal fun LazyListScope.similarRecommendationsSection(
    section: HomeSection.SimilarRecommendation,
    deps: HomeSectionDeps,
    similarRecommendations: List<SimilarRecommendation>?,
) {
    val recommendation = similarRecommendations?.getOrNull(section.index)
    recommendation?.let {
        item(key = "similar_to_title_${section.index}", contentType = "section_title") {
            val albumArtworkId = when (val linked = recommendation.title) {
                is Song -> linked.album?.id
                is Album -> linked.id
                else -> null
            }
            val albumArtworkUrl = recommendation.title.thumbnailUrl
            com.convx.music.ui.utils.ArtworkSourceHost(Modifier.animateItem()) { sourceModifier ->
            val artworkOwner = com.convx.music.ui.utils.rememberArtworkSourceOwner()
            NavigationTitle(
                label = stringResource(R.string.similar_to),
                title = recommendation.title.title,
                thumbnail = recommendation.title.thumbnailUrl?.let { thumbnailUrl ->
                    {
                        val shape =
                            if (recommendation.title is Artist) CircleShape else RoundedCornerShape(
                                ThumbnailCornerRadius
                            )
                        AsyncImage(
                            model = thumbnailUrl,
                            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(artworkOwner, thumbnailUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .sharedArtworkSource(albumArtworkId, albumArtworkUrl, artworkOwner)
                                .size(ListThumbnailSize)
                                .clip(shape)
                        )
                    }
                },
                onClick = {
                    when (recommendation.title) {
                        is Song -> recommendation.title.album?.let { album ->
                            deps.navController.navigate(albumRoute(album.id, recommendation.title.thumbnailUrl))
                        }
                        is Album -> deps.navController.navigate(albumRoute(recommendation.title.id, recommendation.title.thumbnailUrl))
                        is Artist -> deps.navController.navigate(artistRoute(recommendation.title.id, recommendation.title.thumbnailUrl))
                        is Playlist -> {}
                    }
                },
                modifier = sourceModifier,
            )
            }
        }
        shelfRow(
            key = "similar_to_list_${section.index}",
            deps = deps,
            entries = recommendation.items.distinctBy { it.id }.map { it.toShelfEntry(deps) },
            compact = true,
        )
    }
}

internal fun LazyListScope.homePageSection(
    section: HomeSection.HomePageSection,
    deps: HomeSectionDeps,
    homePage: HomePage?,
) {
    remoteHomePageSection(
        deps = deps,
        key = "home_section_${section.index}",
        sectionData = homePage?.sections?.getOrNull(section.index),
    )
}

/** A chip's feed as YouTube Music sends it, with shelf layouts varied so the page doesn't read as one repeated row. */
internal fun LazyListScope.chipFeedContent(deps: HomeSectionDeps, homePage: HomePage?) {
    var cardShelves = 0
    homePage?.sections?.forEachIndexed { index, section ->
        val style = when {
            section.isListenAgain(deps) -> RemoteShelfStyle.Compact
            section.items.isVideoShelf() -> RemoteShelfStyle.Video
            section.items.isSongShelf() -> RemoteShelfStyle.List
            index == 0 -> RemoteShelfStyle.Hero
            section.items.all { it is ArtistItem } -> RemoteShelfStyle.Compact
            cardShelves++ % 2 == 0 -> RemoteShelfStyle.Spotlight
            else -> RemoteShelfStyle.Compact
        }
        remoteHomePageSection(deps, "chip_section_$index", section, style)
    }
}

private enum class RemoteShelfStyle { Hero, Spotlight, Compact, List, Video }

/** Listen again always reads as cards, whichever path YouTube sends it through. */
private fun HomePage.Section.isListenAgain(deps: HomeSectionDeps): Boolean {
    val name = title.trim()
    return name.equals("Listen again", ignoreCase = true) ||
        name.equals(deps.context.getString(R.string.keep_listening).trim(), ignoreCase = true)
}

private fun LazyListScope.remoteHomePageSection(
    deps: HomeSectionDeps,
    key: String,
    sectionData: HomePage.Section?,
    style: RemoteShelfStyle? = null,
) {
    sectionData?.let {
        val sectionSongs = sectionData.items.filterIsInstance<SongItem>()

        item(key = "${key}_title", contentType = "section_title") {
            NavigationTitle(
                title = sectionData.title,
                label = sectionData.label,
                thumbnail = sectionData.thumbnail?.let { thumbnailUrl ->
                    {
                        val shape =
                            if (sectionData.endpoint?.isArtistEndpoint == true) CircleShape else RoundedCornerShape(
                                ThumbnailCornerRadius
                            )
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(ListThumbnailSize)
                                .clip(shape)
                        )
                    }
                },
                onClick = sectionData.endpoint?.let { endpoint ->
                    {
                        when {
                            endpoint.browseId == "FEmusic_moods_and_genres" ->
                                deps.navController.navigate("mood_and_genres")
                            endpoint.params != null ->
                                deps.navController.navigate(youtubeBrowseRoute(endpoint.browseId, endpoint.params))
                            else ->
                                deps.navController.navigate("browse/${android.net.Uri.encode(endpoint.browseId)}")
                        }
                    }
                },
                onPlayAllClick = if (sectionSongs.isNotEmpty()) {
                    {
                        deps.playerConnection.playQueue(
                            ListQueue(
                                title = sectionData.title,
                                items = sectionSongs.map { it.toMediaMetadata().toMediaItem() }
                            )
                        )
                    }
                } else null,
                modifier = Modifier.animateItem(),
            )
        }
        val items = sectionData.items.distinctBy { it.id }
        val entries = items.map { it.toShelfEntry(deps) }
        val resolvedStyle = style ?: when {
            sectionData.isListenAgain(deps) -> RemoteShelfStyle.Compact
            items.isVideoShelf() -> RemoteShelfStyle.Video
            items.isSongShelf() -> RemoteShelfStyle.List
            else -> RemoteShelfStyle.Compact
        }
        when (resolvedStyle) {
            RemoteShelfStyle.Video -> shelfVideoRow(key = "${key}_list", deps = deps, items = items)
            RemoteShelfStyle.List -> shelfList(key = "${key}_list", deps = deps, entries = entries)
            RemoteShelfStyle.Compact -> shelfRow(key = "${key}_list", deps = deps, entries = entries, compact = true)
            RemoteShelfStyle.Spotlight -> shelfRow(key = "${key}_list", deps = deps, entries = entries)
            RemoteShelfStyle.Hero -> item(key = "${key}_list", contentType = "hero_rail") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp).plusStart(deps.sideInset),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().animateItem().bleedStart(deps.sideInset),
                ) {
                    items(items, key = { it.id }, contentType = { "figma_hero" }) { item ->
                        HomePopMenu(HomePop.Yt(item), deps, item.thumbnail, item.title, item.shelfSubtitle(), { deps.openYtItem(item) }) {
                            FigmaTopPickCard(item)
                        }
                    }
                }
            }
        }
    }
}

internal fun LazyListScope.moodAndGenresSection(
    deps: HomeSectionDeps,
    explorePage: ExplorePage?,
) {
    // Mood filters are pinned below the Home title; this section is the reorderable tile shelf.

    // Duplicate titles would collide with the lazy grid item keys.
    explorePage?.moodAndGenres?.distinctBy { it.title }?.let { moodAndGenres ->
        item(key = "mood_and_genres_title", contentType = "section_title") {
            NavigationTitle(
                title = stringResource(R.string.mood_and_genres),
                onClick = {
                    deps.navController.navigate("mood_and_genres")
                },
                modifier = Modifier.animateItem(),
            )
        }
        item(key = "mood_and_genres_list", contentType = "mood_grid") {
            val tiles: AppleSearchViewModel = koinViewModel()
            val art by tiles.art.collectAsStateWithLifecycle()
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = AppPage.Gutter).plusStart(deps.sideInset),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoodTileWidth * MoodTileAspect * 2 + 12.dp)
                    .animateItem()
                    .bleedStart(deps.sideInset),
            ) {
                items(
                    items = moodAndGenres.filter { it.endpoint.browseId.isNotBlank() },
                    key = { "mood_${it.endpoint.browseId}_${it.endpoint.params}" },
                    contentType = { "mood_tile" },
                ) { mood ->
                    LaunchedEffect(mood) { tiles.loadArt(mood) }
                    CategoryTile(
                        title = mood.title,
                        color = Color(mood.stripeColor),
                        art = art[mood.endpoint.browseId + "|" + mood.endpoint.params],
                        modifier = Modifier.width(MoodTileWidth),
                    ) {
                        deps.navController.navigate(youtubeBrowseRoute(mood.endpoint.browseId, mood.endpoint.params))
                    }
                }
            }
        }
    }
}

private val MoodTileWidth = 180.dp
private const val MoodTileAspect = 105f / 181f

/** Loads live song rows in one batched query and indexes them by ID. */
@Composable
internal fun rememberSongsById(
    database: MusicDatabase,
    songIds: List<String>,
): Map<String, Song> {
    val flow = remember(songIds) {
        if (songIds.isEmpty()) flowOf(emptyList()) else database.songsByIds(songIds)
    }
    val songs by flow.collectAsStateWithLifecycle(initialValue = emptyList())
    return remember(songs) { songs.associateBy { it.id } }
}
