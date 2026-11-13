package com.music.innertube

import com.music.innertube.mappers.AccountMapper
import com.music.innertube.mappers.AlbumMapper
import com.music.innertube.mappers.ArtistMapper
import com.music.innertube.mappers.ChartsMapper
import com.music.innertube.mappers.HomeMapper
import com.music.innertube.mappers.LibraryMapper
import com.music.innertube.mappers.PlayerMapper
import com.music.innertube.mappers.PlaylistMapper
import com.music.innertube.mappers.SearchMapper
import com.music.innertube.models.AccountInfo
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.MediaInfo
import com.music.innertube.models.SearchSuggestions
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.YouTubeLocale
import com.music.innertube.models.comment.CommentResponse
import com.music.innertube.models.comment.CommentThreadRenderer
import com.music.innertube.models.response.CreatePlaylistResponse
import com.music.innertube.models.response.EditPlaylistResponse
import com.music.innertube.models.response.FeedbackResponse
import com.music.innertube.models.response.ImageUploadResponse
import com.music.innertube.models.response.NextResponse
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
import com.music.innertube.pages.LibraryPage
import com.music.innertube.pages.MoodAndGenres
import com.music.innertube.pages.NextResult
import com.music.innertube.pages.PlaylistContinuationPage
import com.music.innertube.pages.PlaylistPage
import com.music.innertube.pages.RelatedPage
import com.music.innertube.pages.SearchResult
import com.music.innertube.pages.SearchSummaryPage
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/** An isolated InnerTube repository instance for one client session. */
open class YouTubeRepository(
    private val innerTube: InnerTube = InnerTube(),
) : InnertubeRepository {

    override var locale: YouTubeLocale
        get() = innerTube.locale
        set(value) {
            innerTube.locale = value
        }
    override var visitorData: String?
        get() = innerTube.visitorData
        set(value) {
            innerTube.visitorData = value
        }
    override var dataSyncId: String?
        get() = innerTube.dataSyncId
        set(value) {
            innerTube.dataSyncId = value
        }
    override var cookie: String?
        get() = innerTube.cookie
        set(value) {
            innerTube.cookie = value
        }
    override var proxy: InnertubeProxySettings?
        get() = innerTube.proxy
        set(value) {
            innerTube.proxy = value
        }

    override var proxyAuth: String?
        get() = innerTube.proxyAuth
        set(value) {
            innerTube.proxyAuth = value
        }
    override var useLoginForBrowse: Boolean
        get() = innerTube.useLoginForBrowse
        set(value) {
            innerTube.useLoginForBrowse = value
        }

    override var ipVersion: com.music.innertube.models.IpVersion
        get() = innerTube.ipVersion
        set(value) {
            innerTube.ipVersion = value
        }

    override suspend fun refreshVisitorData(): Result<String> = visitorData().onSuccess {
        visitorData = it
    }

    override suspend fun probeAudioStream(url: String, contentLength: Long?): AudioStreamProbeResult =
        innerTube.probeAudioStream(url, contentLength)

    override fun clearGuestSession() {
        visitorData = null
        dataSyncId = null
    }

    override suspend fun searchSuggestions(query: String): Result<SearchSuggestions> =
        SearchMapper.searchSuggestions(innerTube, query)

    override suspend fun searchSummary(query: String): Result<SearchSummaryPage> =
        SearchMapper.searchSummary(innerTube, query)

    override suspend fun search(query: String, filter: SearchFilter): Result<SearchResult> =
        SearchMapper.search(innerTube, query, filter)

    override suspend fun searchContinuation(continuation: String): Result<SearchResult> =
        SearchMapper.searchContinuation(innerTube, continuation)

    override suspend fun album(browseId: String, withSongs: Boolean): Result<AlbumPage> =
        AlbumMapper.album(innerTube, browseId, withSongs)

    override suspend fun albumSongs(playlistId: String, album: AlbumItem?): Result<List<SongItem>> =
        AlbumMapper.albumSongs(innerTube, playlistId, album)

    override suspend fun artist(browseId: String): Result<ArtistPage> =
        ArtistMapper.artist(innerTube, browseId)

    override suspend fun artistItems(endpoint: BrowseEndpoint): Result<ArtistItemsPage> =
        ArtistMapper.artistItems(innerTube, endpoint)

    override suspend fun artistItemsContinuation(continuation: String): Result<ArtistItemsContinuationPage> =
        ArtistMapper.artistItemsContinuation(innerTube, continuation)

    override suspend fun playlist(playlistId: String): Result<PlaylistPage> =
        PlaylistMapper.playlist(innerTube, playlistId)

    override suspend fun playlistContinuation(continuation: String): Result<PlaylistContinuationPage> =
        PlaylistMapper.playlistContinuation(innerTube, continuation)

    override suspend fun home(continuation: String?, params: String?, browseId: String): Result<HomePage> =
        HomeMapper.home(innerTube, continuation, params, browseId)

    override suspend fun explore(): Result<ExplorePage> =
        HomeMapper.explore(innerTube)

    override suspend fun newReleaseAlbums(): Result<List<AlbumItem>> =
        HomeMapper.newReleaseAlbums(innerTube)

    override suspend fun moodAndGenres(): Result<List<MoodAndGenres>> =
        HomeMapper.moodAndGenres(innerTube)

    override suspend fun browse(browseId: String, params: String?): Result<BrowseResult> =
        HomeMapper.browse(innerTube, browseId, params)

    override suspend fun library(browseId: String, tabIndex: Int): Result<LibraryPage> =
        LibraryMapper.library(innerTube, browseId, tabIndex)

    override suspend fun libraryContinuation(continuation: String) =
        LibraryMapper.libraryContinuation(innerTube, continuation)

    override suspend fun libraryRecentActivity(): Result<LibraryPage> =
LibraryMapper.libraryRecentActivity(innerTube)

    override suspend fun getChartsPage(continuation: String?): Result<ChartsPage> =
        ChartsMapper.getChartsPage(innerTube, continuation)

    override suspend fun musicHistory(): Result<HistoryPage> =
        LibraryMapper.musicHistory(innerTube)

    override suspend fun likeVideo(videoId: String, like: Boolean) = runCatching {
        if (like)
            innerTube.likeVideo(WEB_REMIX, videoId)
        else
            innerTube.unlikeVideo(WEB_REMIX, videoId)
    }

    override suspend fun likePlaylist(playlistId: String, like: Boolean) = runCatching {
        if (like)
            innerTube.likePlaylist(WEB_REMIX, playlistId)
        else
            innerTube.unlikePlaylist(WEB_REMIX, playlistId)
    }

    override suspend fun subscribeChannel(channelId: String, subscribe: Boolean) = runCatching {
        if (subscribe)
            innerTube.subscribeChannel(WEB_REMIX, channelId)
        else
            innerTube.unsubscribeChannel(WEB_REMIX, channelId)
    }

    override suspend fun getChannelId(browseId: String): String {
        ArtistMapper.artist(innerTube, browseId).onSuccess {
            return it.artist.channelId ?: ""
        }
        return ""
    }

    override suspend fun addToPlaylist(playlistId: String, videoId: String) = runCatching {
        innerTube.addToPlaylist(WEB_REMIX, playlistId, videoId)
    }

    override suspend fun addPlaylistToPlaylist(playlistId: String, addPlaylistId: String) = runCatching {
        innerTube.addPlaylistToPlaylist(WEB_REMIX, playlistId, addPlaylistId)
    }

    override suspend fun removeFromPlaylist(playlistId: String, videoId: String, setVideoId: String) = runCatching {
        innerTube.removeFromPlaylist(WEB_REMIX, playlistId, videoId, setVideoId)
    }

    override suspend fun moveSongPlaylist(playlistId: String, setVideoId: String, successorSetVideoId: String?) = runCatching {
        innerTube.moveSongPlaylist(WEB_REMIX, playlistId, setVideoId, successorSetVideoId)
    }

    override suspend fun createPlaylist(title: String) =
        innerTube.createPlaylist(WEB_REMIX, title).body<CreatePlaylistResponse>().playlistId

    override suspend fun renamePlaylist(playlistId: String, name: String) = runCatching {
        innerTube.renamePlaylist(WEB_REMIX, playlistId, name)
    }

    override suspend fun uploadCustomThumbnailLink(playlistId: String, image: ByteArray) = runCatching {
        val uploadUrl = innerTube.getUploadCustomThumbnailLink(WEB_REMIX, image.size).headers["x-guploader-uploadid"]
        val blobReq = innerTube.uploadCustomThumbnail(
            WEB_REMIX,
            uploadUrl!!,
            image
        )
        val blobId = Json.decodeFromString<ImageUploadResponse>(blobReq.bodyAsText()).encryptedBlobId
        innerTube.setThumbnailPlaylist(WEB_REMIX, playlistId, blobId).body<EditPlaylistResponse>().newHeader?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
    }

    override suspend fun removeThumbnailPlaylist(playlistId: String) = runCatching {
        innerTube.removeThumbnailPlaylist(WEB_REMIX, playlistId).body<EditPlaylistResponse>().newHeader?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
    }

    override suspend fun deletePlaylist(playlistId: String) = runCatching {
        innerTube.deletePlaylist(WEB_REMIX, playlistId)
    }

    override suspend fun player(videoId: String, playlistId: String?, client: YouTubeClient, signatureTimestamp: Int?, poToken: String?): Result<PlayerResponse> =
        PlayerMapper.player(innerTube, videoId, playlistId, client, signatureTimestamp, poToken)

    override suspend fun registerPlayback(playlistId: String?, playbackTracking: String) =
        PlayerMapper.registerPlayback(innerTube, playlistId, playbackTracking)

    override suspend fun next(endpoint: WatchEndpoint, continuation: String?): Result<NextResult> =
        PlayerMapper.next(innerTube, endpoint, continuation)

    override suspend fun lyrics(endpoint: BrowseEndpoint): Result<String?> =
        PlayerMapper.lyrics(innerTube, endpoint)

    override suspend fun related(endpoint: BrowseEndpoint): Result<RelatedPage> =
        PlayerMapper.related(innerTube, endpoint)

    override suspend fun queue(videoIds: List<String>?, playlistId: String?): Result<List<SongItem>> =
        PlayerMapper.queue(innerTube, videoIds, playlistId)

    override suspend fun transcript(videoId: String): Result<String> =
        PlayerMapper.transcript(innerTube, videoId)

    override suspend fun visitorData(): Result<String> =
        AccountMapper.visitorData(innerTube)

    override suspend fun accountInfo(): Result<AccountInfo> =
        AccountMapper.accountInfo(innerTube)

    override suspend fun getAccountChannels(): Result<List<AccountInfo>> =
        AccountMapper.getAccountChannels(innerTube)

    override suspend fun feedback(tokens: List<String>): Result<Boolean> = runCatching {
        innerTube.feedback(WEB_REMIX, tokens).body<FeedbackResponse>().feedbackResponses.all { it.isProcessed }
    }

    /**
     * Add a song to library by fetching fresh feedback tokens from the next endpoint
     * This is more reliable than using cached tokens which might be stale
     */
    override suspend fun addSongToLibrary(videoId: String): Result<Boolean> = runCatching {
        val nextResult = PlayerMapper.next(innerTube, WatchEndpoint(videoId = videoId), null).getOrThrow()
        val song = nextResult.items.find { it.id == videoId }
            ?: throw Exception("Song not found in next response")
        
        val addToken = song.libraryAddToken
            ?: throw Exception("Add to library token not available")
        
        feedback(listOf(addToken)).getOrThrow()
    }

    /**
     * Remove a song from library by fetching fresh feedback tokens from the next endpoint
     */
    override suspend fun removeSongFromLibrary(videoId: String): Result<Boolean> = runCatching {
        val nextResult = PlayerMapper.next(innerTube, WatchEndpoint(videoId = videoId), null).getOrThrow()
        val song = nextResult.items.find { it.id == videoId }
            ?: throw Exception("Song not found in next response")
        
        val removeToken = song.libraryRemoveToken
            ?: throw Exception("Remove from library token not available")
        
        feedback(listOf(removeToken)).getOrThrow()
    }

    /**
     * Toggle song library status - adds if not in library, removes if in library
     * Uses fresh tokens fetched from the API for reliability
     */
    override suspend fun toggleSongLibrary(videoId: String, addToLibrary: Boolean): Result<Boolean> = runCatching {
        if (addToLibrary) {
            addSongToLibrary(videoId).getOrThrow()
        } else {
            removeSongFromLibrary(videoId).getOrThrow()
        }
    }

    override suspend fun getMediaInfo(videoId: String): Result<MediaInfo> =
        AccountMapper.getMediaInfo(innerTube, videoId)

    suspend fun getNewPipeStreamUrls(videoId: String): List<Pair<Int, String>> {
        return PlatformStreamResolver.newPipePlayer(videoId)
    }

    suspend fun newPipePlayer(
        videoId: String,
        tempRes: PlayerResponse,
    ): PlayerResponse? {
        if (tempRes.playabilityStatus.status != "OK") {
            return null
        }

        val streamsList = getNewPipeStreamUrls(videoId)
        if (streamsList.isEmpty()) return null

        val decodedSigResponse = tempRes.copy(
            streamingData = tempRes.streamingData?.copy(
                formats = tempRes.streamingData.formats?.map { format ->
                    format.copy(
                        url = streamsList.find { it.first == format.itag }?.second ?: format.url,
                    )
                },
                adaptiveFormats = tempRes.streamingData.adaptiveFormats.map { adaptiveFormat ->
                    adaptiveFormat.copy(
                        url = streamsList.find { it.first == adaptiveFormat.itag }?.second ?: adaptiveFormat.url,
                    )
                },
            ),
        )

        val urlList = (
            decodedSigResponse.streamingData?.adaptiveFormats?.mapNotNull { it.url }?.toMutableList() ?: mutableListOf()
        ).apply {
            decodedSigResponse.streamingData?.formats?.mapNotNull { it.url }?.let { addAll(it) }
        }

        return if (urlList.isNotEmpty()) {
            decodedSigResponse
        } else {
            null
        }
    }

    override suspend fun comments(videoId: String): Result<Pair<List<CommentThreadRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, videoId, null, null, null, null, null).body<NextResponse>()
        val token = commentContinuationToken(response, videoId)
        commentContinuation(token).getOrThrow()
    }

    private fun commentContinuationToken(response: NextResponse, videoId: String): String {
        val commentsPanel = response.engagementPanels?.firstOrNull {
            it.engagementPanelSectionListRenderer?.panelIdentifier == "engagement-panel-comments-section"
        }
        val engagementPanelToken = commentsPanel
            ?.engagementPanelSectionListRenderer?.content?.sectionListRenderer?.contents
            ?.mapNotNull { it.itemSectionRenderer }
            ?.flatMap { it.contents.orEmpty() }
            ?.mapNotNull { it?.continuationItemRenderer }
            ?.firstOrNull()?.continuationEndpoint?.continuationCommand?.token

        val contentList = response.contents.twoColumnWatchNextResults?.results?.results?.content
        val webToken = contentList
            ?.mapNotNull { it?.continuationItemRenderer }
            ?.firstOrNull()?.continuationEndpoint?.continuationCommand?.token
            ?: contentList
                ?.mapNotNull { it?.itemSectionRenderer }
                ?.flatMap { it.contents.orEmpty() }
                ?.mapNotNull { it?.continuationItemRenderer }
                ?.firstOrNull()?.continuationEndpoint?.continuationCommand?.token

        // The WEB token supports nested replies; use the engagement-panel token as a fallback.
        return webToken ?: engagementPanelToken
            ?: throw Exception("No comment continuation token found for videoId=$videoId")
    }

    override suspend fun commentContinuation(continuationToken: String): Result<Pair<List<CommentThreadRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, null, null, null, null, null, continuationToken).body<CommentResponse>()
        val endpoints = response.onResponseReceivedEndpoints.orEmpty()
        val continuationItems = endpoints.flatMap { endpoint ->
            endpoint.reloadContinuationItemsCommand?.continuationItems.orEmpty() +
            endpoint.appendContinuationItemsAction?.continuationItems.orEmpty()
        }

        // 1. Extract Legacy comments
        val legacyComments = continuationItems.mapNotNull { it.commentThreadRenderer }
            .filter { it.comment?.commentRenderer != null || it.commentViewModel?.commentViewModel != null }

        // Use comment IDs when available; fall back to a renderer hash when they are missing.
        val legacyCommentsMap = legacyComments.associateBy { thread ->
            thread.comment?.commentRenderer?.commentId 
                ?: thread.commentViewModel?.commentViewModel?.commentId
                ?: "legacy-${thread.hashCode()}"
        }

        // 2. Extract Framework-based comments
        val mutations = response.frameworkUpdates?.entityBatchUpdate?.mutations.orEmpty()
        val toolbarMap = mutations.mapNotNull { it.payload?.engagementToolbarStateEntityPayload }.associateBy { it.key }
        val surfaceMap = mutations.mapNotNull { it.payload?.engagementToolbarSurfaceEntityPayload }.associateBy { it.key }

        val commentsFromFramework = mutations.mapNotNull { mutation ->
            mutation.payload?.commentEntityPayload?.let { payload ->
                val toolbarKey = payload.properties?.toolbarStateKey
                val surfaceKey = payload.properties?.toolbarSurfaceKey
                val toolbarState = toolbarMap[toolbarKey]
                val surface = surfaceMap[surfaceKey]
                val likeCount = payload.toolbar?.likeCountNotliked
                    ?: surface?.toolbar?.likeCountNotliked
                    ?: "0"
                val replyCount = payload.toolbar?.replyCount
                    ?: surface?.toolbar?.replyCount
                    ?: "0"
                    
                val commentId = payload.properties?.commentId ?: "framework-${mutation.hashCode()}"
                val legacyMatch = legacyCommentsMap[commentId]

                CommentThreadRenderer(
                    comment = CommentThreadRenderer.Comment(
                        commentRenderer = com.music.innertube.models.comment.CommentRenderer(
                            authorText = com.music.innertube.models.Runs(
                                runs = listOf(com.music.innertube.models.Run(text = payload.author?.displayName ?: "Unknown", navigationEndpoint = null))
                            ),
                            authorThumbnail = com.music.innertube.models.Thumbnails(
                                thumbnails = listOf(com.music.innertube.models.Thumbnail(url = payload.author?.avatarThumbnailUrl ?: "", width = 0, height = 0))
                            ),
                            contentText = com.music.innertube.models.Runs(
                                runs = listOf(com.music.innertube.models.Run(text = payload.properties?.content?.content ?: "", navigationEndpoint = null))
                            ),
                            publishedTimeText = com.music.innertube.models.Runs(
                                runs = listOf(com.music.innertube.models.Run(text = payload.properties?.publishedTime ?: "", navigationEndpoint = null))
                            ),
                            commentId = commentId,
                            voteCount = com.music.innertube.models.Runs(
                                runs = listOf(com.music.innertube.models.Run(text = likeCount, navigationEndpoint = null))
                            ),
                            voteStatus = when (toolbarState?.likeState) {
                                "TOOLBAR_LIKE_STATE_LIKE" -> "UPVOTE"
                                "TOOLBAR_LIKE_STATE_INDIFFERENT" -> "INDIFFERENT"
                                else -> "INDIFFERENT"
                            },
                            replyCount = replyCount.toIntOrNull() ?: 0
                        )
                    ),
                    replies = legacyMatch?.replies
                )
            }
        }

        // 3. Extract Next Token (Exhaustive Search)
        val nextToken = continuationItems.mapNotNull { item ->
            item.continuationItemRenderer?.continuationEndpoint?.continuationCommand?.token
        }.firstOrNull()
            ?: endpoints.flatMap { endpoint ->
                endpoint.appendContinuationItemsAction?.continuationItems.orEmpty()
                    .mapNotNull { it.continuationItemRenderer?.continuationEndpoint?.continuationCommand?.token }
            }.firstOrNull()
        
        // Merge Legacy and Framework comments
        val frameworkCommentsMap = commentsFromFramework.associateBy { it.comment?.commentRenderer?.commentId }
        val allIds = (legacyCommentsMap.keys + frameworkCommentsMap.keys).filterNotNull().distinct()

        // Framework entries have current fields and may include legacy replies.
        val comments = allIds.mapNotNull { id ->
            frameworkCommentsMap[id] ?: legacyCommentsMap[id]
        }.distinctBy { it.comment?.commentRenderer?.commentId ?: it.hashCode() }
        

        Pair(comments, nextToken)
    }

    override suspend fun commentReplies(replyToken: String): Result<Pair<List<com.music.innertube.models.comment.CommentRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, null, null, null, null, null, replyToken).body<CommentResponse>()
        
        val endpoints = response.onResponseReceivedEndpoints.orEmpty()
        val continuationItems = endpoints.flatMap { endpoint ->
            endpoint.reloadContinuationItemsCommand?.continuationItems.orEmpty() +
            endpoint.appendContinuationItemsAction?.continuationItems.orEmpty()
        }

        // 1. Extract Legacy replies
        val legacyReplies = continuationItems.mapNotNull { it.commentRenderer ?: it.commentThreadRenderer?.comment?.commentRenderer }

        // 2. Extract Framework-based replies
        val mutations = response.frameworkUpdates?.entityBatchUpdate?.mutations.orEmpty()
        
        val toolbarMap = mutations.mapNotNull { it.payload?.engagementToolbarStateEntityPayload }.associateBy { it.key }
        val surfaceMap = mutations.mapNotNull { it.payload?.engagementToolbarSurfaceEntityPayload }.associateBy { it.key }

        val frameworkReplies = mutations.mapNotNull { mutation ->
            mutation.payload?.commentEntityPayload?.let { payload ->
                val toolbarKey = payload.properties?.toolbarStateKey
                val surfaceKey = payload.properties?.toolbarSurfaceKey
                val toolbarState = toolbarMap[toolbarKey]
                val surface = surfaceMap[surfaceKey]
                val likeCount = payload.toolbar?.likeCountNotliked
                    ?: surface?.toolbar?.likeCountNotliked
                    ?: "0"

                com.music.innertube.models.comment.CommentRenderer(
                    authorText = com.music.innertube.models.Runs(
                        runs = listOf(com.music.innertube.models.Run(text = payload.author?.displayName ?: "Unknown", navigationEndpoint = null))
                    ),
                    authorThumbnail = com.music.innertube.models.Thumbnails(
                        thumbnails = listOf(com.music.innertube.models.Thumbnail(url = payload.author?.avatarThumbnailUrl ?: "", width = 0, height = 0))
                    ),
                    contentText = com.music.innertube.models.Runs(
                        runs = listOf(com.music.innertube.models.Run(text = payload.properties?.content?.content ?: "", navigationEndpoint = null))
                    ),
                    publishedTimeText = com.music.innertube.models.Runs(
                        runs = listOf(com.music.innertube.models.Run(text = payload.properties?.publishedTime ?: "", navigationEndpoint = null))
                    ),
                    commentId = payload.properties?.commentId ?: "reply-${mutation.hashCode()}",
                    voteCount = com.music.innertube.models.Runs(
                        runs = listOf(com.music.innertube.models.Run(text = likeCount, navigationEndpoint = null))
                    ),
                    voteStatus = when (toolbarState?.likeState) {
                        "TOOLBAR_LIKE_STATE_LIKE" -> "UPVOTE"
                        "TOOLBAR_LIKE_STATE_INDIFFERENT" -> "INDIFFERENT"
                        else -> "INDIFFERENT"
                    }
                )
            }
        }

        val allRepliesMap = legacyReplies.associateBy { it.commentId }
        val allFrameworkRepliesMap = frameworkReplies.associateBy { it.commentId }
        val allIds = (allRepliesMap.keys + allFrameworkRepliesMap.keys).filterNotNull().distinct()

        val mergedReplies = allIds.mapNotNull { id ->
            val legacy = allRepliesMap[id]
            val modern = allFrameworkRepliesMap[id]

            if (legacy != null && modern != null) {
                legacy.copy(
                    voteCount = modern.voteCount ?: legacy.voteCount,
                    voteStatus = modern.voteStatus ?: legacy.voteStatus,
                    replyCount = modern.replyCount ?: legacy.replyCount
                )
            } else {
                modern ?: legacy
            }
        }.distinctBy { it.commentId ?: it.hashCode() }
        
        // 3. Extract Next Token (Exhaustive Search for Replies)
        val nextToken = continuationItems.mapNotNull { item ->
            item.continuationItemRenderer?.let { renderer ->
                renderer.continuationEndpoint?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.command?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.navigationEndpoint?.continuationCommand?.token
            }
        }.firstOrNull()
            ?: endpoints.mapNotNull { endpoint ->
                endpoint.appendContinuationItemsAction?.continuationItems?.mapNotNull { it.continuationItemRenderer?.continuationEndpoint?.continuationCommand?.token }
            }.flatten().firstOrNull()

        Pair(mergedReplies, nextToken)
    }
}

object YouTube : YouTubeRepository()
