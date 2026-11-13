package com.convx.music.catalog

/** Provider-qualified identity. The raw provider key stays opaque to shared screens. */
data class CatalogId(
    val providerId: String,
    val value: String,
)

const val YOUTUBE_MUSIC_PROVIDER = "youtube_music"

data class CatalogArtist(
    val id: CatalogId?,
    val name: String,
)

data class CatalogAlbumReference(
    val id: CatalogId?,
    val title: String,
    val artworkUrls: List<String> = emptyList(),
)

sealed interface CatalogEntry {
    val id: CatalogId
    val title: String
    val artworkUrls: List<String>

    data class Track(
        override val id: CatalogId,
        override val title: String,
        override val artworkUrls: List<String>,
        val artists: List<CatalogArtist>,
        val album: CatalogAlbumReference?,
        val durationMillis: Long?,
        val isExplicit: Boolean,
        val isVideo: Boolean,
        val isPlayable: Boolean,
        val isInLibrary: Boolean? = null,
    ) : CatalogEntry

    data class Album(
        override val id: CatalogId,
        override val title: String,
        override val artworkUrls: List<String>,
        val artists: List<CatalogArtist>,
        val year: String?,
        val trackCount: Int?,
    ) : CatalogEntry

    data class Artist(
        override val id: CatalogId,
        override val title: String,
        override val artworkUrls: List<String>,
        val subtitle: String?,
        val description: String? = null,
    ) : CatalogEntry

    data class Playlist(
        override val id: CatalogId,
        override val title: String,
        override val artworkUrls: List<String>,
        val description: String?,
        val trackCount: Int?,
        val isEditable: Boolean,
        val author: CatalogArtist? = null,
    ) : CatalogEntry

    data class Browse(
        override val id: CatalogId,
        override val title: String,
        override val artworkUrls: List<String> = emptyList(),
        val params: String? = null,
    ) : CatalogEntry
}

data class CatalogSection(
    val id: String,
    val title: String,
    val entries: List<CatalogEntry>,
    val continuationToken: String? = null,
    val subtitle: String? = null,
    val more: CatalogEntry.Browse? = null,
)

data class CatalogHomePage(
    val sections: List<CatalogSection>,
    val continuationToken: String? = null,
    val filters: List<CatalogEntry.Browse> = emptyList(),
)

enum class CatalogSearchFilter {
    ALL,
    TRACKS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS,
}

data class CatalogSearchPage(
    val query: String,
    val sections: List<CatalogSection>,
    val continuationToken: String? = null,
)

data class CatalogSearchSuggestions(
    val queries: List<String> = emptyList(),
    val entries: List<CatalogEntry> = emptyList(),
)

data class CatalogPlaylistPage(
    val playlist: CatalogEntry.Playlist,
    val tracks: List<CatalogEntry.Track>,
    val continuationToken: String? = null,
    val related: List<CatalogEntry> = emptyList(),
)

data class CatalogTrackPage(
    val tracks: List<CatalogEntry.Track>,
    val continuationToken: String? = null,
)

enum class CatalogLibraryCollection {
    HISTORY,
    RECENT_ACTIVITY,
    PLAYLISTS,
    LIKED_TRACKS,
    LIKED_ALBUMS,
    ARTISTS,
    UPLOADED_TRACKS,
}

data class CatalogLibraryPage(
    val collection: CatalogLibraryCollection,
    val entries: List<CatalogEntry>,
    val continuationToken: String? = null,
)

data class CatalogAlbumPage(
    val album: CatalogEntry.Album,
    val tracks: List<CatalogEntry.Track>,
    val related: List<CatalogEntry.Album> = emptyList(),
)

data class CatalogArtistPage(
    val artist: CatalogEntry.Artist,
    val sections: List<CatalogSection>,
)

sealed interface CatalogError {
    data object Offline : CatalogError
    data object Unauthorized : CatalogError
    data class RateLimited(val retryAfterMillis: Long?) : CatalogError
    data object NotFound : CatalogError
    data object Unavailable : CatalogError
    data object InvalidResponse : CatalogError
    data object Unknown : CatalogError
}

sealed interface CatalogResult<out T> {
    data class Success<out T>(val value: T) : CatalogResult<T>
    data class Failure(val error: CatalogError) : CatalogResult<Nothing>
}
