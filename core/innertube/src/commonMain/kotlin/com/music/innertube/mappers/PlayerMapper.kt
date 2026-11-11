package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.MusicResponsiveListItemRenderer
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig.Companion.MUSIC_VIDEO_TYPE_ATV
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.WEB
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.YTItem
import com.music.innertube.models.getContinuation
import com.music.innertube.models.response.BrowseResponse
import com.music.innertube.models.response.GetQueueResponse
import com.music.innertube.models.response.GetTranscriptResponse
import com.music.innertube.models.response.NextResponse
import com.music.innertube.models.response.PlayerResponse
import com.music.innertube.pages.NextPage
import com.music.innertube.pages.NextResult
import com.music.innertube.pages.RelatedPage
import io.ktor.client.call.body
import kotlin.random.Random

internal object PlayerMapper {
    suspend fun player(
        innerTube: InnerTube,
        videoId: String,
        playlistId: String?,
        client: YouTubeClient,
        signatureTimestamp: Int?,
        poToken: String?,
    ): Result<PlayerResponse> = runCatching {
        innerTube.player(client, videoId, playlistId, signatureTimestamp, poToken).body<PlayerResponse>()
    }

    suspend fun registerPlayback(innerTube: InnerTube, playlistId: String?, playbackTracking: String) = runCatching {
        val cpn = (1..16).map {
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"[Random.Default.nextInt(
                0,
                64
            )]
        }.joinToString("")

        val playbackUrl = playbackTracking.replace(
            "https://s.youtube.com",
            "https://music.youtube.com",
        )

        innerTube.registerPlayback(
            url = playbackUrl,
            playlistId = playlistId,
            cpn = cpn
        )
    }

    suspend fun next(innerTube: InnerTube, endpoint: WatchEndpoint, continuation: String?): Result<NextResult> = runCatching {
        val response = innerTube.next(
            WEB_REMIX,
            endpoint.videoId,
            endpoint.playlistId,
            endpoint.playlistSetVideoId,
            endpoint.index,
            endpoint.params,
            continuation).body<NextResponse>()
        val tabs = response.contents.singleColumnMusicWatchNextResultsRenderer
            ?.tabbedRenderer?.watchNextTabbedResultsRenderer?.tabs
        val firstTab = tabs?.get(0)
        val playlistPanelRenderer = response.continuationContents?.playlistPanelContinuation
            ?: firstTab?.tabRenderer?.content?.musicQueueRenderer?.content?.playlistPanelRenderer!!
        val title = firstTab?.tabRenderer?.content?.musicQueueRenderer
            ?.header?.musicQueueHeaderRenderer?.subtitle?.runs?.firstOrNull()?.text
        val lyricsEndpoint = tabs?.getOrNull(1)?.tabRenderer?.endpoint?.browseEndpoint
        val relatedEndpoint = tabs?.getOrNull(2)?.tabRenderer?.endpoint?.browseEndpoint
        val items = playlistPanelRenderer.contents.mapNotNull { content ->
            content.playlistPanelVideoRenderer
                ?.let(NextPage::fromPlaylistPanelVideoRenderer)
                ?.let { it to content.playlistPanelVideoRenderer.selected }
        }
        val songs = items.map { it.first }
        val currentIndex = items.indexOfFirst { it.second }.takeIf { it != -1 }

        // load automix items
        playlistPanelRenderer.contents.lastOrNull()?.automixPreviewVideoRenderer?.content?.automixPlaylistVideoRenderer?.navigationEndpoint?.watchPlaylistEndpoint?.let { watchPlaylistEndpoint ->
            return@runCatching next(innerTube, watchPlaylistEndpoint, null).getOrThrow().let { result ->
                result.copy(
                    title = title,
                    items = songs + result.items,
                    lyricsEndpoint = lyricsEndpoint,
                    relatedEndpoint = relatedEndpoint,
                    currentIndex = currentIndex,
                    endpoint = watchPlaylistEndpoint
                )
            }
        }
        NextResult(
            title = title,
            items = songs,
            currentIndex = currentIndex,
            lyricsEndpoint = lyricsEndpoint,
            relatedEndpoint = relatedEndpoint,
            continuation = playlistPanelRenderer.continuations?.getContinuation(),
            endpoint = endpoint
        )
    }

    suspend fun lyrics(innerTube: InnerTube, endpoint: BrowseEndpoint): Result<String?> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId, endpoint.params).body<BrowseResponse>()
        response.contents?.sectionListRenderer?.contents
            ?.firstOrNull { it.musicDescriptionShelfRenderer != null }
            ?.musicDescriptionShelfRenderer?.description?.runs
            ?.joinToString(separator = "") { it.text }
    }

    suspend fun related(innerTube: InnerTube, endpoint: BrowseEndpoint): Result<RelatedPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId).body<BrowseResponse>()
        val songs = mutableListOf<SongItem>()
        val albums = mutableListOf<AlbumItem>()
        val artists = mutableListOf<ArtistItem>()
        val playlists = mutableListOf<PlaylistItem>()

        fun addItem(item: YTItem, renderer: MusicResponsiveListItemRenderer?) {
            when (item) {
                is SongItem -> {
                    val isAudioTrack = renderer?.overlay
                        ?.musicItemThumbnailOverlayRenderer?.content
                        ?.musicPlayButtonRenderer?.playNavigationEndpoint
                        ?.watchEndpoint?.watchEndpointMusicSupportedConfigs
                        ?.watchEndpointMusicConfig?.musicVideoType == MUSIC_VIDEO_TYPE_ATV
                    if (isAudioTrack) {
                        songs.add(item)
                    }
                }
                is AlbumItem -> albums.add(item)
                is ArtistItem -> artists.add(item)
                is PlaylistItem -> playlists.add(item)
            }
        }

        response.contents?.sectionListRenderer?.contents?.forEach { sectionContent ->
            sectionContent.musicCarouselShelfRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                    ?: content.musicTwoRowItemRenderer?.let(RelatedPage.Companion::fromMusicTwoRowItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }

            sectionContent.musicShelfRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }

            sectionContent.itemSectionRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }
        }
        RelatedPage(songs, albums, artists, playlists)
    }

    suspend fun queue(innerTube: InnerTube, videoIds: List<String>?, playlistId: String?): Result<List<SongItem>> = runCatching {
        if (videoIds != null) {
            require(videoIds.size <= MAX_GET_QUEUE_SIZE) { "Queue request exceeds the $MAX_GET_QUEUE_SIZE video limit" }
        }
        innerTube.getQueue(WEB_REMIX, videoIds, playlistId).body<GetQueueResponse>().queueDatas
            .mapNotNull {
                it.content.playlistPanelVideoRenderer?.let { renderer ->
                    NextPage.fromPlaylistPanelVideoRenderer(renderer)
                }
            }
    }

    suspend fun transcript(innerTube: InnerTube, videoId: String): Result<String> = runCatching {
        val response = innerTube.getTranscript(WEB, videoId).body<GetTranscriptResponse>()
        response.actions?.firstOrNull()?.updateEngagementPanelAction?.content?.transcriptRenderer?.body?.transcriptBodyRenderer?.cueGroups?.joinToString(separator = "\n") { group ->
            val time = group.transcriptCueGroupRenderer.cues[0].transcriptCueRenderer.startOffsetMs
            val text = group.transcriptCueGroupRenderer.cues[0].transcriptCueRenderer.cue.simpleText
                .trim('♪')
                .trim(' ')
            "[${(time / 60000).toString().padStart(2, '0')}:${((time / 1000) % 60).toString().padStart(2, '0')}.${(time % 1000).toString().padStart(3, '0')}]$text"
        }!!
    }

    private const val MAX_GET_QUEUE_SIZE = 1000
}
