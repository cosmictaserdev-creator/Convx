package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.LibraryFilter
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.GridRenderer
import com.music.innertube.models.MusicShelfRenderer
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.getContinuation
import com.music.innertube.models.response.BrowseResponse
import com.music.innertube.pages.HistoryPage
import com.music.innertube.pages.LibraryContinuationPage
import com.music.innertube.pages.LibraryPage
import io.ktor.client.call.body

internal object LibraryMapper {
    suspend fun library(innerTube: InnerTube, browseId: String, tabIndex: Int): Result<LibraryPage> {
        return runCatching {
            val response = innerTube.browse(
                client = WEB_REMIX,
                browseId = browseId,
                setLogin = true
            ).body<BrowseResponse>()

            val tabs = response.contents?.singleColumnBrowseResultsRenderer?.tabs

            val contents = if (tabs != null && tabs.size >= tabIndex) {
                tabs[tabIndex].tabRenderer.content?.sectionListRenderer?.contents?.firstOrNull()
            }
            else {
                null
            }

            when {
                contents?.gridRenderer != null -> {
                    val gridItems = contents.gridRenderer.items
                    val twoRowItems = gridItems.mapNotNull(GridRenderer.Item::musicTwoRowItemRenderer)
                    val parsedItems = twoRowItems.mapNotNull { LibraryPage.fromMusicTwoRowItemRenderer(it) }
                    LibraryPage(
                        items = parsedItems,
                        continuation = contents.gridRenderer.continuations?.getContinuation()
                    )
                }

                else -> { // contents?.musicShelfRenderer != null
                    val shelfContents = contents?.musicShelfRenderer?.contents
                    if (shelfContents == null) {
                        throw IllegalStateException("No content found for browseId=$browseId")
                    }
                    val listItemRenderers = shelfContents.mapNotNull(MusicShelfRenderer.Content::musicResponsiveListItemRenderer)

                    val parsedItems = listItemRenderers.mapNotNull(LibraryPage::fromMusicResponsiveListItemRenderer)
                    LibraryPage(
                        items = parsedItems,
                        continuation = contents.musicShelfRenderer.continuations?.getContinuation()
                    )
                }
            }
        }
    }

    suspend fun libraryContinuation(innerTube: InnerTube, continuation: String) = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val contents = response.continuationContents

        when {
            contents?.gridContinuation != null -> {
                LibraryContinuationPage(
                    items = contents.gridContinuation.items
                        .mapNotNull (GridRenderer.Item::musicTwoRowItemRenderer)
                        .mapNotNull { LibraryPage.fromMusicTwoRowItemRenderer(it) },
                    continuation = contents.gridContinuation.continuations?.getContinuation()
                )
            }

            else -> { // contents?.musicShelfContinuation != null
                LibraryContinuationPage(
                    items = contents?.musicShelfContinuation?.contents!!
                        .mapNotNull (MusicShelfRenderer.Content::musicResponsiveListItemRenderer)
                        .mapNotNull { LibraryPage.fromMusicResponsiveListItemRenderer(it) },
                    continuation = contents.musicShelfContinuation.continuations?.getContinuation()
                )
            }
        }
    }

    suspend fun libraryRecentActivity(innerTube: InnerTube): Result<LibraryPage> = runCatching {
        val continuation = LibraryFilter.FILTER_RECENT_ACTIVITY.value

        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val gridItems = response.continuationContents?.sectionListContinuation?.contents?.firstOrNull()
            ?.gridRenderer?.items
        
        if (gridItems == null) {
            return@runCatching LibraryPage(
                items = emptyList(),
                continuation = null
            )
        }
        
        val items = gridItems.mapNotNull {
            it.musicTwoRowItemRenderer?.let { renderer ->
                LibraryPage.fromMusicTwoRowItemRenderer(renderer)
            }
        }.toMutableList()

        // Fetch the full artist for its play endpoint, but keep the library's square thumbnail.
        items.forEachIndexed { index, item ->
            if (item is ArtistItem) {
                ArtistMapper.artist(innerTube, item.id).getOrNull()?.artist?.let { fetchedArtist ->
                    items[index] = fetchedArtist.copy(thumbnail = item.thumbnail)
                }
            }
        }

        LibraryPage(
            items = items,
            continuation = null
        )
    }

    suspend fun musicHistory(innerTube: InnerTube): Result<HistoryPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            browseId = "FEmusic_history",
            setLogin = true
        ).body<BrowseResponse>()

        HistoryPage(
            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                ?.tabRenderer?.content?.sectionListRenderer?.contents
                ?.mapNotNull {
                    it.musicShelfRenderer?.let { musicShelfRenderer ->
                        HistoryPage.fromMusicShelfRenderer(musicShelfRenderer)
                    }
                }
        )
    }
}
