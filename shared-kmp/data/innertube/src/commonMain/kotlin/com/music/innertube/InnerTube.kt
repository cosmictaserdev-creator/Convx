package com.music.innertube

import com.music.innertube.models.Context
import com.music.innertube.models.IpVersion
import com.music.innertube.models.MediaInfo
import com.music.innertube.models.ReturnYouTubeDislikeResponse
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeLocale
import com.music.innertube.models.body.*
import com.music.innertube.models.response.NextResponse
import com.music.innertube.utils.parseCookieString
import com.music.innertube.utils.sha1
import com.music.innertube.utils.sessionAuthorization
import kotlin.io.encoding.ExperimentalEncodingApi
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import kotlin.time.Clock
import kotlin.io.encoding.Base64

private const val ACCOUNT_REQUEST_TIMEOUT_MS = 12_000L
private const val ACCOUNT_CONNECT_TIMEOUT_MS = 5_000L
private const val ACCOUNT_SOCKET_TIMEOUT_MS = 8_000L

@OptIn(ExperimentalEncodingApi::class)
class InnerTube {
    private val rateLimitMutex = Mutex()
    private var rateLimitUntilMillis = 0L

    var locale = YouTubeLocale(
        gl = "US",
        hl = "en-US",
    )
    var visitorData: String? = null
    var dataSyncId: String? = null
        set(value) {
            // API requests need the leading channel id; DATASYNC_ID may include a second id after "||".
            field = value?.takeIf { it.isNotBlank() && it != "null" }?.substringBefore("||")
        }
    var cookie: String? = null
        set(value) {
            field = value
            cookieMap = if (value == null) emptyMap() else parseCookieString(value)
        }
    private var cookieMap = emptyMap<String, String>()

    var proxy: InnertubeProxySettings? = null
        set(value) {
            field = value
            recreateClient()
        }

    var proxyAuth: String? = null
        set(value) {
            field = value
            recreateClient()
        }

    var ipVersion: IpVersion = IpVersion.AUTO
        set(value) {
            field = value
            recreateClient()
        }

    var useLoginForBrowse: Boolean = false

    private var httpClient = createClient()

    private fun recreateClient() {
        httpClient.close()
        httpClient = createClient()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createClient() = HttpClient(
        createInnertubeHttpClientEngine(
            InnertubeNetworkSettings(
                proxy = proxy,
                proxyAuth = proxyAuth,
                ipVersion = ipVersion,
            ),
        ),
    ) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
            })
        }

        install(ContentEncoding) {
            gzip(0.9F)
            deflate(0.8F)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 60000
        }

        defaultRequest {
            url(YouTubeClient.API_URL_YOUTUBE_MUSIC)
        }
    }

    private fun HttpRequestBuilder.ytClient(client: YouTubeClient, setLogin: Boolean = false) {
        contentType(ContentType.Application.Json)
        headers {
            append("X-Goog-Api-Format-Version", "1")
            append("X-YouTube-Client-Name", client.clientId /* Not a typo. The Client-Name header does contain the client id. */)
            append("X-YouTube-Client-Version", client.clientVersion)
            append("X-Origin", YouTubeClient.ORIGIN_YOUTUBE_MUSIC)
            append("Referer", YouTubeClient.REFERER_YOUTUBE_MUSIC)
            visitorData?.let { append("X-Goog-Visitor-Id", it) }
            if (setLogin && client.loginSupported) {
                cookie?.let { cookie ->
                    append("cookie", cookie)
                    val currentTime = Clock.System.now().toEpochMilliseconds() / 1000
                    sessionAuthorization(cookieMap, currentTime, YouTubeClient.ORIGIN_YOUTUBE_MUSIC)
                        ?.let { append("Authorization", it) }
                }
            }
        }
        userAgent(client.userAgent)
        parameter("prettyPrint", false)
    }

    suspend fun probeAudioStream(url: String, contentLength: Long?): AudioStreamProbeResult =
        probeAudioStream(httpClient, url, contentLength)

    /** Retries transient failures, but not timeouts, which would exceed the caller's time budget. */
    private suspend fun <T> withRetry(
        maxAttempts: Int = 3,
        initialDelay: Long = 500L,
        factor: Double = 2.0,
        maxCooldownWaitMillis: Long = RetryPolicy.MAX_INLINE_WAIT_MS,
        block: suspend () -> T,
    ): T {
        var currentDelay = initialDelay
        var attempt = 0
        while (true) {
            val cooldownMs = rateLimitMutex.withLock {
                rateLimitUntilMillis - Clock.System.now().toEpochMilliseconds()
            }
            if (cooldownMs > maxCooldownWaitMillis) {
                throw InnerTubeRateLimitException(cooldownMs)
            }
            if (cooldownMs > 0) {
                delay(cooldownMs + 1)
                continue
            }
            try {
                return block()
            } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
                throw e
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                attempt++
                if (attempt >= maxAttempts) throw e
                delay(RetryPolicy.jitter(currentDelay))
                currentDelay = (currentDelay * factor).toLong()
            } catch (e: io.ktor.client.plugins.ResponseException) {
                val status = e.response.status.value
                attempt++
                val retryAfterMs = RetryPolicy.retryAfterMillis(e.response.headers[HttpHeaders.RetryAfter])
                if (status == 429) {
                    // All read calls share the cooldown, including when this call has no attempts left.
                    val now = Clock.System.now().toEpochMilliseconds()
                    val waitMs = (retryAfterMs ?: 5_000L).coerceAtMost(Long.MAX_VALUE - now)
                    val until = now + waitMs
                    rateLimitMutex.withLock {
                        rateLimitUntilMillis = maxOf(rateLimitUntilMillis, until)
                    }
                }
                if ((status != 429 && status !in 500..599) || attempt >= maxAttempts) throw e
                if (retryAfterMs != null && retryAfterMs > RetryPolicy.MAX_INLINE_WAIT_MS) throw e
                delay(maxOf(retryAfterMs ?: 0L, RetryPolicy.jitter(currentDelay)))
                currentDelay = (currentDelay * factor).toLong()
            }
        }
    }

    suspend fun search(
        client: YouTubeClient,
        query: String? = null,
        params: String? = null,
        continuation: String? = null,
    ) = withRetry {
        httpClient.post("search") {
            ytClient(client, setLogin = useLoginForBrowse)
            setBody(
                SearchBody(
                    context = client.toContext(
                        locale,
                        visitorData,
                        if (useLoginForBrowse) dataSyncId else null
                    ),
                    query = query,
                    params = params
                )
            )
            parameter("continuation", continuation)
            parameter("ctoken", continuation)
        }
    }

    suspend fun player(
        client: YouTubeClient,
        videoId: String,
        playlistId: String?,
        signatureTimestamp: Int?,
        poToken: String? = null,
    ) = withRetry {
        httpClient.post("player") {
            ytClient(client, setLogin = true)
            // Keep playback startup from waiting for the shared 60-second timeout.
            timeout { requestTimeoutMillis = 6_000 }
            setBody(
                PlayerBody(
                    context = client.toContext(locale, visitorData, dataSyncId).let {
                        if (client.isEmbedded) {
                            it.copy(
                                thirdParty = Context.ThirdParty(
                                    embedUrl = "https://www.youtube.com/watch?v=${videoId}"
                                )
                            )
                        } else it
                    },
                    videoId = videoId,
                    playlistId = playlistId,
                    playbackContext = if (client.useSignatureTimestamp && signatureTimestamp != null) {
                        PlayerBody.PlaybackContext(
                            PlayerBody.PlaybackContext.ContentPlaybackContext(
                                signatureTimestamp
                            )
                        )
                    } else null,
                    serviceIntegrityDimensions = if (client.useWebPoTokens && poToken != null) {
                        PlayerBody.ServiceIntegrityDimensions(poToken)
                    } else null,
                )
            )
        }
    }

    suspend fun registerPlayback(
        url: String,
        cpn: String,
        playlistId: String?,
        client: YouTubeClient = YouTubeClient.WEB_REMIX,
    ) = run {
        httpClient.get(url) {
            ytClient(client, true)
            parameter("ver", "2")
            parameter("c", client.clientName)
            parameter("cpn", cpn)

            if (playlistId != null) {
                parameter("list", playlistId)
                parameter("referrer", "https://music.youtube.com/playlist?list=$playlistId")
            }
        }
    }

    suspend fun browse(
        client: YouTubeClient,
        browseId: String? = null,
        params: String? = null,
        continuation: String? = null,
        setLogin: Boolean = false,
    ) = withRetry {
        httpClient.post("browse") {
            ytClient(client, setLogin = setLogin || useLoginForBrowse)
            setBody(
                BrowseBody(
                    context = client.toContext(
                        locale,
                        visitorData,
                        if (setLogin || useLoginForBrowse) dataSyncId else null
                    ),
                    browseId = browseId,
                    params = params,
                    continuation = continuation
                )
            )
        }
    }

    suspend fun next(
        client: YouTubeClient,
        videoId: String?,
        playlistId: String?,
        playlistSetVideoId: String?,
        index: Int?,
        params: String?,
        continuation: String? = null,
    ) = withRetry {
        httpClient.post("next") {
            ytClient(client, setLogin = true)
            setBody(
                NextBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    videoId = videoId,
                    playlistId = playlistId,
                    playlistSetVideoId = playlistSetVideoId,
                    index = index,
                    params = params,
                    continuation = continuation
                )
            )
        }
    }

    suspend fun feedback(
        client: YouTubeClient,
        tokens: List<String>
    ) = run {
        httpClient.post("feedback") {
            ytClient(client, setLogin = true)
            setBody(
                FeedbackBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    feedbackTokens = tokens
                )
            )
        }
    }

    suspend fun getSearchSuggestions(
        client: YouTubeClient,
        input: String,
    ) = withRetry {
        httpClient.post("music/get_search_suggestions") {
            ytClient(client)
            setBody(
                GetSearchSuggestionsBody(
                    context = client.toContext(locale, visitorData, null),
                    input = input
                )
            )
        }
    }

    suspend fun getQueue(
        client: YouTubeClient,
        videoIds: List<String>?,
        playlistId: String?,
    ) = withRetry {
        httpClient.post("music/get_queue") {
            ytClient(client)
            setBody(
                GetQueueBody(
                    context = client.toContext(locale, visitorData, null),
                    videoIds = videoIds,
                    playlistId = playlistId
                )
            )
        }
    }

    suspend fun getTranscript(
        client: YouTubeClient,
        videoId: String,
    ) = withRetry {
        httpClient.post("https://music.youtube.com/youtubei/v1/get_transcript") {
            parameter("key", "AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX3")
            headers {
                append("Content-Type", "application/json")
            }
            setBody(
                GetTranscriptBody(
                    context = client.toContext(locale, null, null),
                    params = Base64.Default.encode(
                        "\n${11.toChar()}$videoId".encodeToByteArray()
                    )
                )
            )
        }
    }

    suspend fun getSwJsData() = withRetry { httpClient.get("https://music.youtube.com/sw.js_data") }

    suspend fun accountMenu(client: YouTubeClient) = withRetry {
        httpClient.post("account/account_menu") {
            ytClient(client, setLogin = true)
            timeout {
                requestTimeoutMillis = ACCOUNT_REQUEST_TIMEOUT_MS
                connectTimeoutMillis = ACCOUNT_CONNECT_TIMEOUT_MS
                socketTimeoutMillis = ACCOUNT_SOCKET_TIMEOUT_MS
            }
            setBody(AccountMenuBody(client.toContext(locale, visitorData, dataSyncId)))
        }
    }

    /**
     * Lists every YouTube channel tied to the currently-set [cookie] (a Google
     * account can have several, and this is the only InnerTube call that
     * enumerates them without opening YouTube's own UI in a WebView).
     */
    suspend fun getAccountSwitcherEndpoint(client: YouTubeClient) = withRetry(
        maxAttempts = 1,
        maxCooldownWaitMillis = 0L,
    ) {
        httpClient.get("https://music.youtube.com/getAccountSwitcherEndpoint") {
            ytClient(client, setLogin = true)
            timeout {
                requestTimeoutMillis = ACCOUNT_REQUEST_TIMEOUT_MS
                connectTimeoutMillis = ACCOUNT_CONNECT_TIMEOUT_MS
                socketTimeoutMillis = ACCOUNT_SOCKET_TIMEOUT_MS
            }
        }
    }

    // Avoid automatic retries: a failed response may follow a successful mutation.
    suspend fun likeVideo(
        client: YouTubeClient,
        videoId: String,
    ) = run {
        httpClient.post("like/like") {
            ytClient(client, setLogin = true)
            setBody(
                LikeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    target = LikeBody.Target.VideoTarget(videoId)
                )
            )
        }
    }

    suspend fun unlikeVideo(
        client: YouTubeClient,
        videoId: String,
    ) = run {
        httpClient.post("like/removelike") {
            ytClient(client, setLogin = true)
            setBody(
                LikeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    target = LikeBody.Target.VideoTarget(videoId)
                )
            )
        }
    }

    suspend fun subscribeChannel(
        client: YouTubeClient,
        channelId: String,
    ) = run {
        httpClient.post("subscription/subscribe") {
            ytClient(client, setLogin = true)
            setBody(
                SubscribeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    channelIds = listOf(channelId)
                )
            )
        }
    }

    suspend fun unsubscribeChannel(
        client: YouTubeClient,
        channelId: String,
    ) = run {
        httpClient.post("subscription/unsubscribe") {
            ytClient(client, setLogin = true)
            setBody(
                SubscribeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    channelIds = listOf(channelId)
                )
            )
        }
    }

    suspend fun likePlaylist(
        client: YouTubeClient,
        playlistId: String,
    ) = run {
        httpClient.post("like/like") {
            ytClient(client, setLogin = true)
            setBody(
                LikeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    target = LikeBody.Target.PlaylistTarget(playlistId)
                )
            )
        }
    }

    suspend fun unlikePlaylist(
        client: YouTubeClient,
        playlistId: String,
    ) = run {
        httpClient.post("like/removelike") {
            ytClient(client, setLogin = true)
            setBody(
                LikeBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    target = LikeBody.Target.PlaylistTarget(playlistId)
                )
            )
        }
    }

    // Avoid automatic retries: a failed response may follow a successful mutation.
    suspend fun addToPlaylist(
        client: YouTubeClient,
        playlistId: String,
        videoId: String,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId.removePrefix("VL"),
                    actions = listOf(
                        Action.AddVideoAction(addedVideoId = videoId)
                    )
                )
            )
        }
    }

    suspend fun addPlaylistToPlaylist(
        client: YouTubeClient,
        playlistId: String,
        addPlaylistId: String,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId.removePrefix("VL"),
                    actions = listOf(
                        Action.AddPlaylistAction(addedFullListId = addPlaylistId)
                    )
                )
            )
        }
    }

    suspend fun removeFromPlaylist(
        client: YouTubeClient,
        playlistId: String,
        videoId: String,
        setVideoId: String,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId.removePrefix("VL"),
                    actions = listOf(
                        Action.RemoveVideoAction(
                            removedVideoId = videoId,
                            setVideoId = setVideoId,
                        )
                    )
                )
            )
        }
    }

    suspend fun moveSongPlaylist(
        client: YouTubeClient,
        playlistId: String,
        setVideoId: String,
        successorSetVideoId: String?,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId,
                    actions = listOf(
                        Action.MoveVideoAction(
                            movedSetVideoIdSuccessor = successorSetVideoId,
                            setVideoId = setVideoId,
                        )
                    )
                )
            )
        }
    }

    // Avoid automatic retries: a failed response may follow a successful mutation.
    suspend fun createPlaylist(
        client: YouTubeClient,
        title: String,
    ) = run {
        httpClient.post("playlist/create") {
            ytClient(client, true)
            setBody(
                CreatePlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    title = title
                )
            )
        }
    }

    // Avoid automatic retries: a failed response may follow a successful mutation.
    suspend fun renamePlaylist(
        client: YouTubeClient,
        playlistId: String,
        name: String,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId,
                    actions = listOf(
                        Action.RenamePlaylistAction(
                            playlistName = name
                        )
                    )
                )
            )
        }
    }
    
    suspend fun getUploadCustomThumbnailLink(
        client: YouTubeClient,
        contentLength: Int
    ) = withRetry {
        httpClient.post("https://music.youtube.com/playlist_image_upload/playlist_custom_thumbnail") {
            ytClient(client, setLogin = true)
            headers {
                append("X-Goog-Upload-Command", "start")
                append("X-Goog-Upload-Protocol", "resumable")
                append("X-Goog-Upload-Header-Content-Length", contentLength.toString())
            }
        }
    }

    suspend fun uploadCustomThumbnail(
        client: YouTubeClient,
        uploadId: String,
        image: ByteArray,
    ) = run {
        httpClient.post("https://music.youtube.com/playlist_image_upload/playlist_custom_thumbnail") {
            ytClient(client, setLogin = true)
            parameter("upload_id", uploadId)
            parameter("upload_protocol", "resumable")
            headers {
                append("X-Goog-Upload-Command", "upload, finalize")
                append("X-Goog-Upload-Offset", "0")
            }
            setBody(image)
        }
    }

    suspend fun setThumbnailPlaylist(
        client: YouTubeClient,
        playlistId: String,
        blobId: String,
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId,
                    actions = listOf(
                        Action.SetCustomThumbnailAction(
                            addedCustomThumbnail = Action.SetCustomThumbnailAction.AddedCustomThumbnail(
                                playlistScottyEncryptedBlobId = blobId
                            )
                        )
                    )
                )
            )
        }
    }

    suspend fun removeThumbnailPlaylist(
        client: YouTubeClient,
        playlistId: String
    ) = run {
        httpClient.post("browse/edit_playlist") {
            ytClient(client, setLogin = true)
            setBody(
                EditPlaylistBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId,
                    actions = listOf(
                        Action.RemoveCustomThumbnailAction()
                    )
                )
            )
        }
    }

    // Avoid automatic retries: a failed response may follow a successful mutation.
    suspend fun deletePlaylist(
        client: YouTubeClient,
        playlistId: String,
    ) = run {
        httpClient.post("playlist/delete") {
            ytClient(client, setLogin = true)
            setBody(
                PlaylistDeleteBody(
                    context = client.toContext(locale, visitorData, dataSyncId),
                    playlistId = playlistId
                )
            )
        }
    }

    private suspend fun returnYouTubeDislike(videoId: String) = withRetry {
        httpClient.get("https://returnyoutubedislikeapi.com/Votes?videoId=$videoId") {
            contentType(ContentType.Application.Json)
        }
    }


    suspend fun getMediaInfo(videoId: String): Result<MediaInfo> =
        runCatching {
            val response = next(client = YouTubeClient.WEB, videoId, null, null, null, null, null).body<NextResponse>()

            val baseForInfo =
                response.contents.twoColumnWatchNextResults
                    ?.results
                    ?.results
                    ?.content
                    ?.find {
                        it?.videoSecondaryInfoRenderer != null
                    }?.videoSecondaryInfoRenderer

            val baseForTitle =
                response.contents.twoColumnWatchNextResults
                    ?.results
                    ?.results
                    ?.content
                    ?.find {
                        it?.videoPrimaryInfoRenderer != null
                    }?.videoPrimaryInfoRenderer

            val returnYouTubeDislikeResponse =
                returnYouTubeDislike(videoId).body<ReturnYouTubeDislikeResponse>()

            return@runCatching MediaInfo(
                videoId = videoId,
                title = baseForTitle
                    ?.title
                    ?.runs
                    ?.firstOrNull()
                    ?.text,
                author = baseForInfo
                    ?.owner
                    ?.videoOwnerRenderer
                    ?.title
                    ?.runs
                    ?.firstOrNull()
                    ?.text,
                authorId =
                    baseForInfo
                        ?.owner
                        ?.videoOwnerRenderer
                        ?.navigationEndpoint
                        ?.browseEndpoint
                        ?.browseId,
                authorThumbnail =
                    baseForInfo
                        ?.owner
                        ?.videoOwnerRenderer
                        ?.thumbnail
                        ?.thumbnails
                        ?.find {
                            it.height == 48
                        }?.url
                        ?.replace("s48", "s960"),
                description = baseForInfo?.attributedDescription?.content,
                subscribers =
                    baseForInfo
                        ?.owner
                        ?.videoOwnerRenderer
                        ?.subscriberCountText
                        ?.simpleText?.split(" ")?.firstOrNull(),
                uploadDate = baseForTitle?.dateText?.simpleText,
                viewCount = returnYouTubeDislikeResponse.viewCount,
                like = returnYouTubeDislikeResponse.likes,
                dislike = returnYouTubeDislikeResponse.dislikes,
            )

        }


}
