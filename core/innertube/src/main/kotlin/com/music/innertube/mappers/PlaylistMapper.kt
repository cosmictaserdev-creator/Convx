package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.models.Artist
import com.music.innertube.models.MusicShelfRenderer
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SectionListRenderer
import com.music.innertube.models.YTItem
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.getContinuation
import com.music.innertube.models.getItems
import com.music.innertube.models.response.BrowseResponse
import com.music.innertube.pages.PlaylistContinuationPage
import com.music.innertube.pages.PlaylistPage
import com.music.innertube.pages.RelatedPage
import com.music.innertube.pages.SearchSummaryPage
import io.ktor.client.call.body

internal object PlaylistMapper {
    suspend fun playlist(innerTube: InnerTube, playlistId: String): Result<PlaylistPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            browseId = "VL$playlistId",
            setLogin = true
        ).body<BrowseResponse>()
        val base = response.contents?.twoColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()
        val header = base?.musicResponsiveHeaderRenderer ?: base?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer

        val editable = base?.musicEditablePlaylistDetailHeaderRenderer != null
        val secondarySectionList = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer

        var related = secondarySectionList?.contents?.let { parseRelatedItems(it.drop(1)) }

        if (related.isNullOrEmpty()) {
            secondarySectionList?.continuations?.getContinuation()?.let { continuationToken ->
                val continuationResponse = innerTube.browse(
                    client = WEB_REMIX,
                    continuation = continuationToken,
                    setLogin = true
                ).body<BrowseResponse>()

                continuationResponse.continuationContents?.sectionListContinuation?.contents?.let {
                    val parsed = parseRelatedItems(it)
                    if (parsed.isNotEmpty()) {
                        related = parsed
                    }
                }
            }
        }

        PlaylistPage(
            playlist = PlaylistItem(
                id = playlistId,
                // Auto playlists may omit header fields such as title or thumbnail.
                title = header?.title?.runs?.firstOrNull()?.text ?: "",
                author = header?.straplineTextOne?.runs?.firstOrNull()?.let {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId
                    )
                },
                songCountText = header?.secondSubtitle?.runs?.firstOrNull()?.text,
                thumbnail = header?.thumbnail?.musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()?.url,
                playEndpoint = null,
                shuffleEndpoint = header?.buttons?.lastOrNull()?.menuRenderer?.items?.firstOrNull()?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
                radioEndpoint = header?.buttons?.getOrNull(2)?.menuRenderer?.items?.find {
                    it.menuNavigationItemRenderer?.icon?.iconType == "MIX"
                }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
                isEditable = editable
            ),
            songs = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.contents?.getItems()?.mapNotNull {
                    PlaylistPage.fromMusicResponsiveListItemRenderer(it)
                } ?: emptyList(),
            songsContinuation = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.contents?.getContinuation()
                ?: response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                    ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.continuations?.getContinuation(),
            continuation = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.continuations?.getContinuation(),
            related = related?.ifEmpty { null }
        )
    }

    private fun parseRelatedItems(contents: List<SectionListRenderer.Content>): List<YTItem> {
        return contents.mapNotNull { content ->
            content.musicCarouselShelfRenderer?.let { renderer ->
                renderer.contents.mapNotNull { it.musicTwoRowItemRenderer }.mapNotNull { RelatedPage.fromMusicTwoRowItemRenderer(it) }
            } ?: content.musicShelfRenderer?.let { renderer ->
                renderer.contents?.getItems()?.mapNotNull { SearchSummaryPage.fromMusicResponsiveListItemRenderer(it) }
            }
        }.flatten()
    }

    suspend fun playlistContinuation(innerTube: InnerTube, continuation: String): Result<PlaylistContinuationPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val mainContents: List<MusicShelfRenderer.Content> = response.continuationContents?.sectionListContinuation?.contents
            ?.mapNotNull { content: SectionListRenderer.Content -> content.musicPlaylistShelfRenderer?.contents }
            ?.flatten()
            ?: emptyList()

        val shelfContents: List<MusicShelfRenderer.Content> =
            response.continuationContents?.musicPlaylistShelfContinuation?.contents ?: emptyList()

        val appendedContents: List<MusicShelfRenderer.Content> = response.onResponseReceivedActions
            ?.firstOrNull()
            ?.appendContinuationItemsAction
            ?.continuationItems
            .orEmpty()

        val allContents = mainContents + shelfContents + appendedContents

        val songs = allContents
            .mapNotNull { content: MusicShelfRenderer.Content -> content.musicResponsiveListItemRenderer }
            .mapNotNull { renderer -> PlaylistPage.fromMusicResponsiveListItemRenderer(renderer) }

        val nextContinuation = if (songs.isEmpty()) null else {
            response.continuationContents
                ?.sectionListContinuation
                ?.continuations
                ?.getContinuation()
                ?: response.continuationContents
                    ?.musicPlaylistShelfContinuation
                    ?.continuations
                    ?.getContinuation()
                ?: response.continuationContents
                    ?.musicShelfContinuation
                    ?.continuations
                    ?.getContinuation()
                ?: response.onResponseReceivedActions
                    ?.firstOrNull()
                    ?.appendContinuationItemsAction
                    ?.continuationItems
                    ?.getContinuation()
        }

        PlaylistContinuationPage(
            songs = songs,
            continuation = nextContinuation
        )
    }
}
