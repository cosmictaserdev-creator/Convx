package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.Run
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.getContinuation
import com.music.innertube.models.getItems
import com.music.innertube.models.response.BrowseResponse
import com.music.innertube.pages.ArtistItemsContinuationPage
import com.music.innertube.pages.ArtistItemsPage
import com.music.innertube.pages.ArtistPage
import io.ktor.client.call.body

internal object ArtistMapper {
    suspend fun artist(innerTube: InnerTube, browseId: String): Result<ArtistPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId).body<BrowseResponse>()

        fun mapRuns(runs: List<Run>?): List<Run>? = runs?.map { run ->
            Run(
                text = run.text,
                navigationEndpoint = run.navigationEndpoint
            )
        }

        val descriptionRuns = response.contents?.sectionListRenderer?.contents
            ?.firstOrNull { it.musicDescriptionShelfRenderer != null }
            ?.musicDescriptionShelfRenderer?.description?.runs
            ?.let(::mapRuns)
            ?: response.header?.musicImmersiveHeaderRenderer?.description?.runs?.let(::mapRuns)

        ArtistPage(
            artist = ArtistItem(
                id = browseId,
                title = response.header?.musicImmersiveHeaderRenderer?.title?.runs?.firstOrNull()?.text
                    ?: response.header?.musicVisualHeaderRenderer?.title?.runs?.firstOrNull()?.text
                    ?: response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text!!,
                thumbnail = response.header?.musicImmersiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                    ?: response.header?.musicVisualHeaderRenderer?.foregroundThumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                    ?: response.header?.musicDetailHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl(),
                channelId = response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer?.channelId,
                playEndpoint = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                    ?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicShelfRenderer
                    ?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.overlay?.musicItemThumbnailOverlayRenderer
                    ?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint,
                shuffleEndpoint = response.header?.musicImmersiveHeaderRenderer?.playButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint
                    ?: response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer
                        ?.contents?.firstOrNull()?.musicShelfRenderer?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
                radioEndpoint = response.header?.musicImmersiveHeaderRenderer?.startRadioButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint
            ),
            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                ?.tabRenderer?.content?.sectionListRenderer?.contents
                ?.mapNotNull(ArtistPage::fromSectionListRendererContent)!!,
            description = descriptionRuns?.joinToString(separator = "") { it.text },
                subscriberCountText = response.header?.musicImmersiveHeaderRenderer?.subscriptionButton2
                    ?.subscribeButtonRenderer?.subscriberCountWithSubscribeText?.runs?.firstOrNull()?.text
                    ?: response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer
                        ?.longSubscriberCountText?.runs?.firstOrNull()?.text
                    ?: response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer
                        ?.shortSubscriberCountText?.runs?.firstOrNull()?.text,
            monthlyListenerCount = response.header?.musicImmersiveHeaderRenderer?.monthlyListenerCount?.runs?.firstOrNull()?.text,
            descriptionRuns = descriptionRuns
        )
    }

    suspend fun artistItems(innerTube: InnerTube, endpoint: BrowseEndpoint): Result<ArtistItemsPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId, endpoint.params).body<BrowseResponse>()
        val sectionContent = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()
        
        val gridRenderer = sectionContent?.gridRenderer
        val musicCarouselShelfRenderer = sectionContent?.musicCarouselShelfRenderer
        val musicPlaylistShelfRenderer = sectionContent?.musicPlaylistShelfRenderer
        val musicShelfRenderer = sectionContent?.musicShelfRenderer
        
        when {
            gridRenderer != null -> {
                ArtistItemsPage(
                    title = gridRenderer.header?.gridHeaderRenderer?.title?.runs?.firstOrNull()?.text.orEmpty(),
                    items = gridRenderer.items.mapNotNull {
                        it.musicTwoRowItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                        }
                    },
                    continuation = gridRenderer.continuations?.getContinuation()
                )
            }
            musicCarouselShelfRenderer != null -> {
                ArtistItemsPage(
                    title = musicCarouselShelfRenderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text.orEmpty(),
                    items = musicCarouselShelfRenderer.contents.mapNotNull { content ->
                        content.musicTwoRowItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                        } ?: content.musicResponsiveListItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicResponsiveListItemRenderer(renderer)
                        }
                    },
                    continuation = null
                )
            }
            musicShelfRenderer != null -> {
                ArtistItemsPage(
                    title = musicShelfRenderer.title?.runs?.firstOrNull()?.text 
                        ?: response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text 
                        ?: "",
                    items = musicShelfRenderer.contents?.getItems()?.mapNotNull {
                        ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                    } ?: emptyList(),
                    continuation = musicShelfRenderer.continuations?.getContinuation()
                )
            }
            else -> {
                ArtistItemsPage(
                    title = response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text ?: "",
                    items = musicPlaylistShelfRenderer?.contents?.getItems()?.mapNotNull {
                        ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                    } ?: emptyList(),
                    continuation = musicPlaylistShelfRenderer?.contents?.getContinuation()
                )
            }
        }
    }

    suspend fun artistItemsContinuation(innerTube: InnerTube, continuation: String): Result<ArtistItemsContinuationPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, continuation = continuation).body<BrowseResponse>()

        when {
            response.continuationContents?.gridContinuation != null -> {
                val gridContinuation = response.continuationContents.gridContinuation
                val items = gridContinuation.items.mapNotNull {
                    it.musicTwoRowItemRenderer?.let { renderer ->
                        ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                    }
                }
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else gridContinuation.continuations?.getContinuation()
                )
            }

            response.continuationContents?.musicPlaylistShelfContinuation != null -> {
                val musicPlaylistShelfContinuation = response.continuationContents.musicPlaylistShelfContinuation
                val items = musicPlaylistShelfContinuation.contents.getItems().mapNotNull {
                    ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                }
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else musicPlaylistShelfContinuation.continuations?.getContinuation()
                )
            }

            else -> {
                val continuationItems = response.onResponseReceivedActions?.firstOrNull()
                    ?.appendContinuationItemsAction?.continuationItems
                val items = continuationItems?.getItems()?.mapNotNull {
                    ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                } ?: emptyList()
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else continuationItems?.getContinuation()
                )
            }
        }
    }
}