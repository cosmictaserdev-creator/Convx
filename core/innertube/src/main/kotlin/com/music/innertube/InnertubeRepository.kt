package com.music.innertube

import com.music.innertube.models.AccountInfo
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.IpVersion
import com.music.innertube.models.MediaInfo
import com.music.innertube.models.SearchSuggestions
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeLocale
import com.music.innertube.models.comment.CommentRenderer
import com.music.innertube.models.comment.CommentThreadRenderer
import com.music.innertube.models.response.PlayerResponse
import com.music.innertube.pages.AlbumPage
import com.music.innertube.pages.ArtistItemsContinuationPage
import com.music.innertube.pages.ArtistItemsPage
import com.music.innertube.pages.ArtistPage
import com.music.innertube.pages.BrowseResult
import com.music.innertube.pages.ChartsPage
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.HistoryPage
import com.music.innertube.pages.HomePage
import com.music.innertube.pages.LibraryContinuationPage
import com.music.innertube.pages.LibraryPage
import com.music.innertube.pages.MoodAndGenres
import com.music.innertube.pages.NextResult
import com.music.innertube.pages.PlaylistContinuationPage
import com.music.innertube.pages.PlaylistPage
import com.music.innertube.pages.RelatedPage
import com.music.innertube.pages.SearchResult
import com.music.innertube.pages.SearchSummaryPage
import io.ktor.client.statement.HttpResponse

interface InnertubeRepository {
    var locale: YouTubeLocale
    var visitorData: String?
    var dataSyncId: String?
    var cookie: String?
    var proxy: InnertubeProxySettings?
    var proxyAuth: String?
    var useLoginForBrowse: Boolean
    var ipVersion: IpVersion

    suspend fun refreshVisitorData(): Result<String>
    suspend fun probeAudioStream(url: String, contentLength: Long?): AudioStreamProbeResult
    fun clearGuestSession()

    suspend fun searchSuggestions(query: String): Result<SearchSuggestions>
    suspend fun searchSummary(query: String): Result<SearchSummaryPage>
    suspend fun search(query: String, filter: SearchFilter): Result<SearchResult>
    suspend fun searchContinuation(continuation: String): Result<SearchResult>

    suspend fun album(browseId: String, withSongs: Boolean = true): Result<AlbumPage>
    suspend fun albumSongs(playlistId: String, album: AlbumItem? = null): Result<List<SongItem>>

    suspend fun artist(browseId: String): Result<ArtistPage>
    suspend fun artistItems(endpoint: BrowseEndpoint): Result<ArtistItemsPage>
    suspend fun artistItemsContinuation(continuation: String): Result<ArtistItemsContinuationPage>

    suspend fun playlist(playlistId: String): Result<PlaylistPage>
    suspend fun playlistContinuation(continuation: String): Result<PlaylistContinuationPage>

    suspend fun home(continuation: String? = null, params: String? = null, browseId: String = "FEmusic_home"): Result<HomePage>
    suspend fun explore(): Result<ExplorePage>
    suspend fun newReleaseAlbums(): Result<List<AlbumItem>>
    suspend fun moodAndGenres(): Result<List<MoodAndGenres>>
    suspend fun browse(browseId: String, params: String?): Result<BrowseResult>

    suspend fun library(browseId: String, tabIndex: Int = 0): Result<LibraryPage>
    suspend fun libraryContinuation(continuation: String): Result<LibraryContinuationPage>
    suspend fun libraryRecentActivity(): Result<LibraryPage>
    suspend fun getChartsPage(continuation: String? = null): Result<ChartsPage>
    suspend fun musicHistory(): Result<HistoryPage>

    suspend fun likeVideo(videoId: String, like: Boolean): Result<HttpResponse>
    suspend fun likePlaylist(playlistId: String, like: Boolean): Result<HttpResponse>
    suspend fun subscribeChannel(channelId: String, subscribe: Boolean): Result<HttpResponse>
    suspend fun getChannelId(browseId: String): String
    suspend fun addToPlaylist(playlistId: String, videoId: String): Result<HttpResponse>
    suspend fun addPlaylistToPlaylist(playlistId: String, addPlaylistId: String): Result<HttpResponse>
    suspend fun removeFromPlaylist(playlistId: String, videoId: String, setVideoId: String): Result<HttpResponse>
    suspend fun moveSongPlaylist(playlistId: String, setVideoId: String, successorSetVideoId: String?): Result<HttpResponse>
    suspend fun createPlaylist(title: String): String
    suspend fun renamePlaylist(playlistId: String, name: String): Result<HttpResponse>
    suspend fun uploadCustomThumbnailLink(playlistId: String, image: ByteArray): Result<String?>
    suspend fun removeThumbnailPlaylist(playlistId: String): Result<String?>
    suspend fun deletePlaylist(playlistId: String): Result<HttpResponse>

    suspend fun player(videoId: String, playlistId: String? = null, client: YouTubeClient, signatureTimestamp: Int? = null, poToken: String? = null): Result<PlayerResponse>
    suspend fun registerPlayback(playlistId: String? = null, playbackTracking: String): Result<HttpResponse>
    suspend fun next(endpoint: WatchEndpoint, continuation: String? = null): Result<NextResult>
    suspend fun lyrics(endpoint: BrowseEndpoint): Result<String?>
    suspend fun related(endpoint: BrowseEndpoint): Result<RelatedPage>
    suspend fun queue(videoIds: List<String>? = null, playlistId: String? = null): Result<List<SongItem>>
    suspend fun transcript(videoId: String): Result<String>

    suspend fun visitorData(): Result<String>
    suspend fun accountInfo(): Result<AccountInfo>
    suspend fun getAccountChannels(): Result<List<AccountInfo>>
    suspend fun feedback(tokens: List<String>): Result<Boolean>
    suspend fun addSongToLibrary(videoId: String): Result<Boolean>
    suspend fun removeSongFromLibrary(videoId: String): Result<Boolean>
    suspend fun toggleSongLibrary(videoId: String, addToLibrary: Boolean): Result<Boolean>
    suspend fun getMediaInfo(videoId: String): Result<MediaInfo>

    suspend fun comments(videoId: String): Result<Pair<List<CommentThreadRenderer>, String?>>
    suspend fun commentContinuation(continuationToken: String): Result<Pair<List<CommentThreadRenderer>, String?>>
    suspend fun commentReplies(replyToken: String): Result<Pair<List<CommentRenderer>, String?>>
}

data class SearchFilter(val value: String) {
    companion object {
        val FILTER_SONG = SearchFilter("EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D")
        val FILTER_VIDEO = SearchFilter("EgWKAQIQAWoKEAkQChAFEAMQBA%3D%3D")
        val FILTER_ALBUM = SearchFilter("EgWKAQIYAWoKEAkQChAFEAMQBA%3D%3D")
        val FILTER_ARTIST = SearchFilter("EgWKAQIgAWoKEAkQChAFEAMQBA%3D%3D")
        val FILTER_FEATURED_PLAYLIST = SearchFilter("EgeKAQQoADgBagwQDhAKEAMQBRAJEAQ%3D")
        val FILTER_COMMUNITY_PLAYLIST = SearchFilter("EgeKAQQoAEABagoQAxAEEAoQCRAF")
    }
}

data class LibraryFilter(val value: String) {
    companion object {
        val FILTER_RECENT_ACTIVITY = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpYnJhcnlfbGFuZGluZxoQZ2dNR0tnUUlCaEFCb0FZQg%3D%3D")
        val FILTER_RECENTLY_PLAYED = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpYnJhcnlfbGFuZGluZxoQZ2dNR0tnUUlCUkFCb0FZQg%3D%3D")
        val FILTER_PLAYLISTS_ALPHABETICAL = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpa2VkX3BsYXlsaXN0cxoQZ2dNR0tnUUlBUkFBb0FZQg%3D%3D")
        val FILTER_PLAYLISTS_RECENTLY_SAVED = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpa2VkX3BsYXlsaXN0cxoQZ2dNR0tnUUlBQkFCb0FZQg%3D%3D")
    }
}
