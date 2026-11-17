package com.music.innertube.catalog

import com.convx.music.catalog.CatalogAlbumPage
import com.convx.music.catalog.CatalogArtist
import com.convx.music.catalog.CatalogArtistPage
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.catalog.CatalogHomePage
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogLibraryPage
import com.convx.music.catalog.CatalogPlaylistPage
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.CatalogSearchFilter
import com.convx.music.catalog.CatalogSearchPage
import com.convx.music.catalog.CatalogTrackPage
import com.convx.music.catalog.MusicCatalogRepository
import com.convx.music.catalog.YOUTUBE_MUSIC_PROVIDER
import com.music.innertube.InnertubeRepository
import com.music.innertube.SearchFilter
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist as InnertubeArtist
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.pages.ArtistPage
import com.music.innertube.pages.ChartsPage
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.LibraryPage
import com.music.innertube.pages.MoodAndGenres
import com.music.innertube.pages.PlaylistPage
import com.music.innertube.pages.SearchResult
import com.music.innertube.pages.SearchSummaryPage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException

class InnertubeMusicCatalogRepository(
    private val innertube: InnertubeRepository,
) : MusicCatalogRepository {
    override suspend fun searchSuggestions(query: String) = innertube.searchSuggestions(query).toCatalogResult {
        com.convx.music.catalog.CatalogSearchSuggestions(
            queries = it.queries.distinct(),
            entries = it.recommendedItems.mapNotNull { entry -> entry.toCatalogEntry() },
        )
    }

    override suspend fun home(continuationToken: String?): CatalogResult<CatalogHomePage> =
        innertube.home(continuation = continuationToken).toCatalogResult { page ->
            CatalogHomePage(
                sections = page.sections.mapIndexedNotNull { index, section ->
                    section.items.mapNotNull { it.toCatalogEntry() }.takeIf { it.isNotEmpty() }?.let { entries ->
                        CatalogSection(
                            id = "home-$index-${section.title.slug()}",
                            title = section.title,
                            entries = entries,
                            subtitle = section.label,
                            more = section.endpoint?.let { endpoint -> CatalogEntry.Browse(
                                CatalogId(YOUTUBE_MUSIC_PROVIDER, endpoint.browseId), section.title, params = endpoint.params,
                            ) },
                        )
                    }
                },
                continuationToken = page.continuation,
                filters = page.chips.orEmpty().mapNotNull { chip -> chip.endpoint?.let { endpoint ->
                    CatalogEntry.Browse(CatalogId(YOUTUBE_MUSIC_PROVIDER, endpoint.browseId), chip.title, params = endpoint.params)
                } },
            )
        }

    override suspend fun explore(): CatalogResult<CatalogHomePage> =
        innertube.explore().toCatalogResult(ExplorePage::toCatalogHomePage)

    override suspend fun related(track: CatalogId): CatalogResult<CatalogHomePage> = runCatching {
        val endpoint = innertube.next(com.music.innertube.models.WatchEndpoint(videoId = track.value))
            .getOrThrow().relatedEndpoint ?: return@runCatching CatalogHomePage(emptyList())
        val page = innertube.related(endpoint).getOrThrow()
        CatalogHomePage(listOf(
            CatalogSection("related-songs", "Discover", page.songs.mapNotNull { it.toCatalogEntry() }),
            CatalogSection("related-albums", "Recommended Albums", page.albums.map { it.toCatalogEntry() }),
            CatalogSection("related-artists", "Artists You Might Like", page.artists.map { it.toCatalogEntry() }),
            CatalogSection("related-playlists", "Recommended Playlists", page.playlists.map { it.toCatalogEntry() }),
        ).filter { it.entries.isNotEmpty() })
    }.toCatalogResult { it }

    override suspend fun charts(continuationToken: String?): CatalogResult<CatalogHomePage> =
        innertube.getChartsPage(continuationToken).toCatalogResult(ChartsPage::toCatalogHomePage)

    override suspend fun browse(id: CatalogId, params: String?): CatalogResult<CatalogHomePage> =
        if (!id.isYouTubeMusicId()) {
            CatalogResult.Failure(CatalogError.NotFound)
        } else {
            innertube.browse(id.value, params).toCatalogResult { page ->
                CatalogHomePage(
                    sections = page.items.mapIndexedNotNull { index, section ->
                        val entries = section.items.mapNotNull { it.toCatalogEntry() }
                        entries.takeIf { it.isNotEmpty() }?.let {
                            CatalogSection(
                                id = "browse-${id.value}-$index",
                                title = section.title ?: "Results",
                                entries = it,
                            )
                        }
                    },
                )
            }
        }

    override suspend fun search(
        query: String,
        filter: CatalogSearchFilter,
        continuationToken: String?,
    ): CatalogResult<CatalogSearchPage> {
        val result = when {
            continuationToken != null -> innertube.searchContinuation(continuationToken)
                .map { it.toCatalogSearchPage(query, filter) }
            filter == CatalogSearchFilter.ALL -> innertube.searchSummary(query)
                .map { it.toCatalogSearchPage(query) }
            else -> innertube.search(query, filter.toInnerTubeFilter())
                .map { it.toCatalogSearchPage(query, filter) }
        }
        return result.toCatalogResult { it }
    }

    override suspend fun playlist(id: CatalogId): CatalogResult<CatalogPlaylistPage> =
        if (!id.isYouTubeMusicId()) {
            CatalogResult.Failure(CatalogError.NotFound)
        } else {
            innertube.playlist(id.value).toCatalogResult(PlaylistPage::toCatalogPage)
        }

    override suspend fun playlistContinuation(
        id: CatalogId,
        continuationToken: String,
    ): CatalogResult<CatalogTrackPage> =
        if (!id.isYouTubeMusicId()) {
            CatalogResult.Failure(CatalogError.NotFound)
        } else {
            innertube.playlistContinuation(continuationToken).toCatalogResult { page ->
                CatalogTrackPage(
                    tracks = page.songs.mapNotNull { it.toCatalogEntry() as? CatalogEntry.Track },
                    continuationToken = page.continuation,
                )
            }
        }

    override suspend fun album(id: CatalogId): CatalogResult<CatalogAlbumPage> =
        if (!id.isYouTubeMusicId()) {
            CatalogResult.Failure(CatalogError.NotFound)
        } else {
            innertube.album(id.value).toCatalogResult { page ->
                CatalogAlbumPage(
                    album = page.album.toCatalogEntry(),
                    tracks = page.songs.mapNotNull { it.toCatalogEntry() as? CatalogEntry.Track },
                    related = page.otherVersions.map { it.toCatalogEntry() },
                )
            }
        }

    override suspend fun artist(id: CatalogId): CatalogResult<CatalogArtistPage> =
        if (!id.isYouTubeMusicId()) {
            CatalogResult.Failure(CatalogError.NotFound)
        } else {
            innertube.artist(id.value).toCatalogResult(ArtistPage::toCatalogPage)
        }

    override suspend fun library(
        collection: CatalogLibraryCollection,
        continuationToken: String?,
    ): CatalogResult<CatalogLibraryPage> {
        if (collection == CatalogLibraryCollection.HISTORY) return innertube.musicHistory().toCatalogResult { page ->
            CatalogLibraryPage(collection, page.sections.orEmpty().flatMap { it.songs }.mapNotNull { it.toCatalogEntry() }.distinctBy { it.id })
        }
        val result = if (continuationToken != null) {
            innertube.libraryContinuation(continuationToken).map { page ->
                LibraryPage(items = page.items, continuation = page.continuation)
            }
        } else {
            when (collection) {
                CatalogLibraryCollection.HISTORY -> error("History is loaded separately")
                CatalogLibraryCollection.RECENT_ACTIVITY -> innertube.libraryRecentActivity()
                CatalogLibraryCollection.PLAYLISTS -> innertube.library("FEmusic_liked_playlists")
                CatalogLibraryCollection.LIKED_TRACKS -> innertube.library("FEmusic_liked_videos")
                CatalogLibraryCollection.LIKED_ALBUMS -> innertube.library("FEmusic_liked_albums")
                CatalogLibraryCollection.ARTISTS -> innertube.library("FEmusic_library_corpus_artists")
                CatalogLibraryCollection.UPLOADED_TRACKS -> innertube.library("FEmusic_library_privately_owned_tracks", tabIndex = 1)
            }
        }
        return result.toCatalogResult { page ->
            CatalogLibraryPage(
                collection = collection,
                entries = page.items.mapNotNull { it.toCatalogEntry() },
                continuationToken = page.continuation,
            )
        }
    }

    private fun CatalogSearchFilter.toInnerTubeFilter(): SearchFilter = when (this) {
        CatalogSearchFilter.ALL -> SearchFilter("")
        CatalogSearchFilter.TRACKS -> SearchFilter.FILTER_SONG
        CatalogSearchFilter.ALBUMS -> SearchFilter.FILTER_ALBUM
        CatalogSearchFilter.ARTISTS -> SearchFilter.FILTER_ARTIST
        CatalogSearchFilter.PLAYLISTS -> SearchFilter.FILTER_FEATURED_PLAYLIST
    }
}

private fun SearchSummaryPage.toCatalogSearchPage(query: String): CatalogSearchPage =
    CatalogSearchPage(
        query = query,
        sections = summaries.mapIndexedNotNull { index, summary ->
            val entries = summary.items.mapNotNull { it.toCatalogEntry() }
            entries.takeIf { it.isNotEmpty() }?.let {
                CatalogSection("search-$index-${summary.title.slug()}", summary.title, it)
            }
        },
    )

private fun ExplorePage.toCatalogHomePage(): CatalogHomePage = CatalogHomePage(
    sections = buildList {
        newReleaseAlbums.mapNotNull { it.toCatalogEntry() }
            .takeIf { it.isNotEmpty() }
            ?.let { add(CatalogSection("explore-new-releases", "New releases", it)) }
        moodAndGenres.map(MoodAndGenres.Item::toCatalogEntry)
            .takeIf { it.isNotEmpty() }
            ?.let { add(CatalogSection("explore-moods-genres", "Moods & genres", it)) }
    },
)

private fun ChartsPage.toCatalogHomePage(): CatalogHomePage = CatalogHomePage(
    sections = sections.mapIndexedNotNull { index, section ->
        val entries = section.items.mapNotNull { it.toCatalogEntry() }
        entries.takeIf { it.isNotEmpty() }?.let {
            CatalogSection("charts-${section.chartType.name.lowercase()}-$index-${section.title.slug()}", section.title, it)
        }
    },
    continuationToken = continuation,
)

private fun SearchResult.toCatalogSearchPage(query: String, filter: CatalogSearchFilter): CatalogSearchPage {
    val entries = items.mapNotNull { it.toCatalogEntry() }
    val sectionTitle = when (filter) {
        CatalogSearchFilter.ALL -> "Results"
        CatalogSearchFilter.TRACKS -> "Songs"
        CatalogSearchFilter.ALBUMS -> "Albums"
        CatalogSearchFilter.ARTISTS -> "Artists"
        CatalogSearchFilter.PLAYLISTS -> "Playlists"
    }
    return CatalogSearchPage(
        query = query,
        sections = entries.takeIf { it.isNotEmpty() }?.let {
            listOf(CatalogSection("search-${sectionTitle.slug()}", sectionTitle, it, continuation))
        }.orEmpty(),
        continuationToken = continuation,
    )
}

private fun PlaylistPage.toCatalogPage(): CatalogPlaylistPage {
    val playlistEntry = playlist.toCatalogEntry() as? CatalogEntry.Playlist
        ?: CatalogEntry.Playlist(
            id = CatalogId(YOUTUBE_MUSIC_PROVIDER, playlist.id),
            title = playlist.title,
            artworkUrls = listOfNotNull(playlist.thumbnail),
            description = null,
            trackCount = playlist.songCountText?.firstNumber(),
            isEditable = playlist.isEditable,
        )
    return CatalogPlaylistPage(
        playlist = playlistEntry,
        tracks = songs.mapNotNull { it.toCatalogEntry() as? CatalogEntry.Track },
        continuationToken = songsContinuation,
        related = related.orEmpty().mapNotNull { it.toCatalogEntry() },
    )
}

private fun ArtistPage.toCatalogPage(): CatalogArtistPage = CatalogArtistPage(
    artist = CatalogEntry.Artist(
        id = CatalogId(YOUTUBE_MUSIC_PROVIDER, artist.id),
        title = artist.title,
        artworkUrls = listOfNotNull(artist.thumbnail),
        subtitle = subscriberCountText ?: monthlyListenerCount,
        description = description,
    ),
    sections = sections.mapIndexedNotNull { index, section ->
        val entries = section.items.mapNotNull { it.toCatalogEntry() }
        entries.takeIf { it.isNotEmpty() }?.let {
            CatalogSection("artist-$index-${section.title.slug()}", section.title, it, more = section.moreEndpoint?.let { endpoint ->
                CatalogEntry.Browse(CatalogId(YOUTUBE_MUSIC_PROVIDER, endpoint.browseId), section.title, params = endpoint.params)
            })
        }
    },
)

private fun YTItem.toCatalogEntry(): CatalogEntry? = when (this) {
    is SongItem -> CatalogEntry.Track(
        id = CatalogId(YOUTUBE_MUSIC_PROVIDER, id),
        title = title,
        artworkUrls = listOf(thumbnail),
        artists = artists.map(InnertubeArtist::toCatalogArtist),
        album = album?.let {
            com.convx.music.catalog.CatalogAlbumReference(
                id = CatalogId(YOUTUBE_MUSIC_PROVIDER, it.id),
                title = it.name,
            )
        },
        durationMillis = duration?.toLong()?.times(1_000L),
        isExplicit = explicit,
        isVideo = isVideoSong,
        isPlayable = id.isNotBlank(),
        isInLibrary = libraryRemoveToken != null,
    )
    is AlbumItem -> toCatalogEntry()
    is ArtistItem -> toCatalogEntry()
    is PlaylistItem -> toCatalogEntry()
}

private fun AlbumItem.toCatalogEntry() = CatalogEntry.Album(
    id = CatalogId(YOUTUBE_MUSIC_PROVIDER, browseId),
    title = title,
    artworkUrls = listOf(thumbnail),
    artists = artists.orEmpty().map(InnertubeArtist::toCatalogArtist),
    year = year?.toString(),
    trackCount = null,
)

private fun ArtistItem.toCatalogEntry() = CatalogEntry.Artist(
    id = CatalogId(YOUTUBE_MUSIC_PROVIDER, id),
    title = title,
    artworkUrls = listOfNotNull(thumbnail),
    subtitle = null,
)

private fun PlaylistItem.toCatalogEntry() = CatalogEntry.Playlist(
    id = CatalogId(YOUTUBE_MUSIC_PROVIDER, id),
    title = title,
    artworkUrls = listOfNotNull(thumbnail),
    description = null,
    trackCount = songCountText?.firstNumber(),
    isEditable = isEditable,
    author = author?.toCatalogArtist(),
)

private fun MoodAndGenres.Item.toCatalogEntry() = CatalogEntry.Browse(
    id = CatalogId(YOUTUBE_MUSIC_PROVIDER, endpoint.browseId),
    title = title,
    params = endpoint.params,
)

private fun InnertubeArtist.toCatalogArtist() = CatalogArtist(
    id = id?.let { CatalogId(YOUTUBE_MUSIC_PROVIDER, it) },
    name = name,
)

private fun <T, R> Result<T>.toCatalogResult(mapper: (T) -> R): CatalogResult<R> = fold(
    onSuccess = { CatalogResult.Success(mapper(it)) },
    onFailure = { error ->
        if (error is CancellationException) throw error
        CatalogResult.Failure(
            when (error) {
                is HttpRequestTimeoutException -> CatalogError.Offline
                is ResponseException -> when (error.response.status.value) {
                    401, 403 -> CatalogError.Unauthorized
                    404 -> CatalogError.NotFound
                    429 -> CatalogError.RateLimited(retryAfterMillis = null)
                    in 500..599 -> CatalogError.Unavailable
                    else -> CatalogError.InvalidResponse
                }
                else -> CatalogError.Unknown
            },
        )
    },
)

private fun CatalogId.isYouTubeMusicId(): Boolean = providerId == YOUTUBE_MUSIC_PROVIDER && value.isNotBlank()

private fun String.slug(): String = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

private fun String.firstNumber(): Int? = Regex("\\d+").find(this)?.value?.toIntOrNull()
