package com.music.innertube.mappers

import com.music.innertube.InnerTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.MusicShelfRenderer
import com.music.innertube.models.SectionListRenderer
import com.music.innertube.models.SongItem
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.getContinuation
import com.music.innertube.models.getItems
import com.music.innertube.models.oddElements
import com.music.innertube.models.response.BrowseResponse
import com.music.innertube.pages.AlbumPage
import com.music.innertube.pages.NewReleaseAlbumPage
import io.ktor.client.call.body
import io.ktor.http.Url
import kotlinx.coroutines.CancellationException

internal object AlbumMapper {
    internal fun sections(response: BrowseResponse): List<SectionListRenderer.Content> = buildList {
        response.contents?.twoColumnBrowseResultsRenderer?.tabs.orEmpty().forEach {
            addAll(it?.tabRenderer?.content?.sectionListRenderer?.contents.orEmpty())
        }
        response.contents?.singleColumnBrowseResultsRenderer?.tabs.orEmpty().forEach {
            addAll(it.tabRenderer.content?.sectionListRenderer?.contents.orEmpty())
        }
        addAll(response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer?.contents.orEmpty())
        addAll(response.contents?.sectionListRenderer?.contents.orEmpty())
        addAll(response.continuationContents?.sectionListContinuation?.contents.orEmpty())
    }

    internal fun parseAlbum(response: BrowseResponse, browseId: String): AlbumPage {
        val sections = sections(response)
        val responsive = sections.firstNotNullOfOrNull { it.musicResponsiveHeaderRenderer }
            ?: response.header?.musicResponsiveHeaderRenderer
            ?: response.header?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer
        val detail = response.header?.musicDetailHeaderRenderer
            ?: response.header?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicDetailHeaderRenderer
        val playlistId = sections.firstNotNullOfOrNull { it.musicPlaylistShelfRenderer?.playlistId }
            ?: responsive?.buttons?.firstNotNullOfOrNull {
                it.musicPlayButtonRenderer?.playNavigationEndpoint?.watchPlaylistEndpoint?.playlistId
                    ?: it.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint?.playlistId
            }
            ?: detail?.menu?.menuRenderer?.topLevelButtons?.firstNotNullOfOrNull {
                it.buttonRenderer?.navigationEndpoint?.watchPlaylistEndpoint?.playlistId
            }
            ?: response.microformat?.microformatDataRenderer?.urlCanonical?.let { url ->
                runCatching { Url(url).parameters["list"] }.getOrNull()
            }
            ?: ""
        val descriptionRuns = sections.firstNotNullOfOrNull { it.musicDescriptionShelfRenderer?.description?.runs }
            ?: responsive?.description?.runs ?: detail?.description?.runs
        val artistRuns = responsive?.straplineTextOne?.runs
            ?: detail?.subtitle?.runs?.filter { it.navigationEndpoint?.browseEndpoint != null }
        val artists = (if (responsive?.straplineTextOne != null) artistRuns?.oddElements() else artistRuns)
            ?.map { Artist(it.text, it.navigationEndpoint?.browseEndpoint?.browseId) }.orEmpty()
        val album = AlbumItem(
            browseId = browseId,
            playlistId = playlistId,
            title = requireNotNull((responsive?.title ?: detail?.title)?.runs?.firstOrNull()?.text) { "Album title missing: $browseId" },
            artists = artists,
            year = (responsive?.subtitle ?: detail?.subtitle)?.runs?.lastOrNull()?.text?.toIntOrNull(),
            thumbnail = responsive?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                ?: detail?.thumbnail?.croppedSquareThumbnailRenderer?.getThumbnailUrl()
                ?: response.background?.musicThumbnailRenderer?.getThumbnailUrl().orEmpty(),
            explicit = false,
            description = descriptionRuns?.joinToString("") { it.text },
        )
        fun recommendations(keyword: String) = sections
            .filter { section -> section.musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs
                ?.any { it.text.contains(keyword, ignoreCase = true) } == true }
            .flatMap { it.musicCarouselShelfRenderer?.contents.orEmpty() }
            .mapNotNull { it.musicTwoRowItemRenderer }
            .mapNotNull(NewReleaseAlbumPage::fromMusicTwoRowItemRenderer)
        return AlbumPage(
            album = album,
            songs = songContents(response).getItems().mapNotNull { AlbumPage.getSong(it, album) },
            otherVersions = recommendations("versions"),
            releasesForYou = (recommendations("releases") + recommendations("more from")).distinctBy { it.id },
            description = album.description,
            descriptionRuns = descriptionRuns,
        )
    }

    internal fun songContents(response: BrowseResponse): List<MusicShelfRenderer.Content> =
        response.continuationContents?.musicPlaylistShelfContinuation?.contents
            ?: response.continuationContents?.musicShelfContinuation?.contents
            ?: response.onResponseReceivedActions?.flatMap { it.appendContinuationItemsAction?.continuationItems.orEmpty() }
                ?.takeIf { it.isNotEmpty() }
            ?: sections(response).flatMap { section ->
                section.musicPlaylistShelfRenderer?.contents ?: section.musicShelfRenderer?.contents.orEmpty()
            }

    internal fun continuation(response: BrowseResponse): String? =
        songContents(response).getContinuation()
            ?: response.continuationContents?.musicPlaylistShelfContinuation?.continuations?.getContinuation()
            ?: response.continuationContents?.musicShelfContinuation?.continuations?.getContinuation()
            ?: response.continuationContents?.sectionListContinuation?.continuations?.getContinuation()
            ?: sections(response).firstNotNullOfOrNull {
                it.musicPlaylistShelfRenderer?.continuations?.getContinuation()
                    ?: it.musicShelfRenderer?.continuations?.getContinuation()
            }

    suspend fun album(innerTube: InnerTube, browseId: String, withSongs: Boolean): Result<AlbumPage> = catching {
        val response = innerTube.browse(WEB_REMIX, browseId, setLogin = true).body<BrowseResponse>()
        val page = parseAlbum(response, browseId)
        val songs = when {
            !withSongs -> emptyList()
            page.songs.isNotEmpty() -> collectSongs(innerTube, response, page.album)
            page.album.playlistId.isNotBlank() -> albumSongs(innerTube, page.album.playlistId, page.album).getOrThrow()
            else -> error("Album tracks missing: $browseId")
        }
        page.copy(songs = songs)
    }

    suspend fun albumSongs(innerTube: InnerTube, playlistId: String, album: AlbumItem?): Result<List<SongItem>> = catching {
        val response = innerTube.browse(WEB_REMIX, "VL${playlistId.removePrefix("VL")}").body<BrowseResponse>()
        collectSongs(innerTube, response, album)
    }

    private suspend fun collectSongs(innerTube: InnerTube, response: BrowseResponse, album: AlbumItem?): List<SongItem> {
        val songs = songContents(response).getItems().mapNotNull { AlbumPage.getSong(it, album) }.toMutableList()
        var next = continuation(response)
        val seen = mutableSetOf<String>()
        while (next != null && seen.size < 50 && seen.add(next)) {
            val continued = innerTube.browse(client = WEB_REMIX, continuation = next).body<BrowseResponse>()
            songs += songContents(continued).getItems().mapNotNull { AlbumPage.getSong(it, album) }
            next = continuation(continued)
        }
        return songs.distinctBy { it.id }
    }

    private suspend fun <T> catching(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
}
