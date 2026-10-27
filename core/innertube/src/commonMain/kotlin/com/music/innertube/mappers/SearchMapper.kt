package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.SearchFilter
import com.music.innertube.YouTubeConstants
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SearchSuggestions
import com.music.innertube.models.SongItem
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.getContinuation
import com.music.innertube.models.getItems
import com.music.innertube.models.response.GetSearchSuggestionsResponse
import com.music.innertube.models.response.SearchResponse
import com.music.innertube.pages.SearchPage
import com.music.innertube.pages.SearchResult
import com.music.innertube.pages.SearchSuggestionPage
import com.music.innertube.pages.SearchSummary
import com.music.innertube.pages.SearchSummaryPage
import io.ktor.client.call.body

internal object SearchMapper {
    suspend fun searchSuggestions(innerTube: InnerTube, query: String): Result<SearchSuggestions> = runCatching {
        val response = innerTube.getSearchSuggestions(WEB_REMIX, query).body<GetSearchSuggestionsResponse>()
        SearchSuggestions(
            queries = response.contents?.getOrNull(0)?.searchSuggestionsSectionRenderer?.contents?.mapNotNull { content ->
                content.searchSuggestionRenderer?.suggestion?.runs?.joinToString(separator = "") { it.text }
            }.orEmpty(),
            recommendedItems = response.contents?.getOrNull(1)?.searchSuggestionsSectionRenderer?.contents?.mapNotNull {
                it.musicResponsiveListItemRenderer?.let { renderer ->
                    SearchSuggestionPage.fromMusicResponsiveListItemRenderer(renderer)
                }
            }.orEmpty()
        )
    }

    suspend fun searchSummary(innerTube: InnerTube, query: String): Result<SearchSummaryPage> = runCatching {
        val response = innerTube.search(WEB_REMIX, query).body<SearchResponse>()
        val contents = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents.orEmpty()

        val shelfSummaries = contents.mapNotNull { content ->
            val cardShelf = content.musicCardShelfRenderer
            val musicShelf = content.musicShelfRenderer

            when {
                cardShelf != null -> {
                    val title = cardShelf.header?.musicCardShelfHeaderBasicRenderer?.title
                        ?.runs?.firstOrNull()?.text ?: YouTubeConstants.DEFAULT_TOP_RESULT
                    val items = (
                        listOfNotNull(SearchSummaryPage.fromMusicCardShelfRenderer(cardShelf)) +
                            cardShelf.contents
                                ?.mapNotNull { it.musicResponsiveListItemRenderer }
                                ?.mapNotNull(SearchSummaryPage.Companion::fromMusicResponsiveListItemRenderer)
                                .orEmpty()
                        ).distinctBy { it.id }

                    if (items.isEmpty()) null else SearchSummary(title, items)
                }

                musicShelf != null -> {
                    val title = musicShelf.title?.runs?.firstOrNull()?.text
                        ?: YouTubeConstants.DEFAULT_OTHER_RESULTS
                    val items = musicShelf.contents?.getItems()
                        ?.mapNotNull(SearchSummaryPage.Companion::fromMusicResponsiveListItemRenderer)
                        ?.distinctBy { it.id }
                        .orEmpty()

                    if (items.isEmpty()) null else SearchSummary(title, items)
                }

                else -> null
            }
        }

        val flatItems = contents
            .mapNotNull { it.itemSectionRenderer }
            .flatMap { it.contents.orEmpty() }
            .mapNotNull { it.musicResponsiveListItemRenderer }
            .mapNotNull { SearchSummaryPage.fromMusicResponsiveListItemRenderer(it) }

        val groupedSummaries = mutableListOf<SearchSummary>()

        val flatSongs = flatItems.filterIsInstance<SongItem>().filter { !it.isVideoSong }
        if (flatSongs.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Songs", items = flatSongs))
        }

        val flatVideos = flatItems.filterIsInstance<SongItem>().filter { it.isVideoSong }
        if (flatVideos.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Videos", items = flatVideos))
        }

        val flatAlbums = flatItems.filterIsInstance<AlbumItem>()
        if (flatAlbums.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Albums", items = flatAlbums))
        }

        val flatArtists = flatItems.filterIsInstance<ArtistItem>()
        if (flatArtists.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Artists", items = flatArtists))
        }

        val flatPlaylists = flatItems.filterIsInstance<PlaylistItem>()
        if (flatPlaylists.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Playlists", items = flatPlaylists))
        }

        SearchSummaryPage(
            summaries = shelfSummaries + groupedSummaries
        )
    }

    suspend fun search(innerTube: InnerTube, query: String, filter: SearchFilter): Result<SearchResult> = runCatching {
        val response = innerTube.search(WEB_REMIX, query, filter.value).body<SearchResponse>()
        val musicShelfRenderer = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents
            ?.mapNotNull { it.musicShelfRenderer }
            ?.firstOrNull()
        SearchResult(
            items = musicShelfRenderer?.contents?.getItems()?.mapNotNull {
                SearchPage.toYTItem(it)
            }.orEmpty(),
            continuation = musicShelfRenderer?.continuations?.getContinuation()
        )
    }

    suspend fun searchContinuation(innerTube: InnerTube, continuation: String): Result<SearchResult> = runCatching {
        val response = innerTube.search(WEB_REMIX, continuation = continuation).body<SearchResponse>()
        val items = response.continuationContents?.musicShelfContinuation?.contents
            ?.mapNotNull {
                SearchPage.toYTItem(it.musicResponsiveListItemRenderer)
            } ?: emptyList()
        SearchResult(
            items = items,
            continuation = if (items.isEmpty()) null else response.continuationContents?.musicShelfContinuation?.continuations?.getContinuation()
        )
    }
}
