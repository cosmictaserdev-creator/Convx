package com.convx.music.catalog

/** Read-only provider catalog used by browse, search, and detail screens. */
interface MusicCatalogRepository {
    suspend fun searchSuggestions(query: String): CatalogResult<CatalogSearchSuggestions> =
        CatalogResult.Success(CatalogSearchSuggestions())

    suspend fun home(continuationToken: String? = null): CatalogResult<CatalogHomePage>

    suspend fun explore(): CatalogResult<CatalogHomePage>
    suspend fun related(track: CatalogId): CatalogResult<CatalogHomePage> = CatalogResult.Failure(CatalogError.Unavailable)

    suspend fun charts(continuationToken: String? = null): CatalogResult<CatalogHomePage>

    suspend fun browse(id: CatalogId, params: String? = null): CatalogResult<CatalogHomePage>

    suspend fun search(
        query: String,
        filter: CatalogSearchFilter = CatalogSearchFilter.ALL,
        continuationToken: String? = null,
    ): CatalogResult<CatalogSearchPage>

    suspend fun playlist(id: CatalogId): CatalogResult<CatalogPlaylistPage>

    suspend fun playlistContinuation(
        id: CatalogId,
        continuationToken: String,
    ): CatalogResult<CatalogTrackPage>

    suspend fun album(id: CatalogId): CatalogResult<CatalogAlbumPage>

    suspend fun artist(id: CatalogId): CatalogResult<CatalogArtistPage>

    suspend fun library(
        collection: CatalogLibraryCollection = CatalogLibraryCollection.RECENT_ACTIVITY,
        continuationToken: String? = null,
    ): CatalogResult<CatalogLibraryPage>
}
