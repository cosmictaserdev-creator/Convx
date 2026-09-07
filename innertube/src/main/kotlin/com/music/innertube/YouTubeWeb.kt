package com.music.innertube

import com.music.innertube.models.WebChannel
import com.music.innertube.models.WebChannelPage
import com.music.innertube.models.WebChannelTab
import com.music.innertube.models.WebChannelTabPage
import com.music.innertube.models.WebFeed
import com.music.innertube.models.WebFeedSection
import com.music.innertube.models.WebPlaylist
import com.music.innertube.models.WebPlaylistPage
import com.music.innertube.models.WebSearchFilter
import com.music.innertube.models.WebSearchPage
import com.music.innertube.models.WebVideo
import com.music.innertube.models.WebWatchPage
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeLocale
import com.music.innertube.models.body.BrowseBody
import com.music.innertube.models.body.NextBody
import com.music.innertube.models.body.SearchBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.music.innertube.utils.parseCookieString
import com.music.innertube.utils.sha1
import timber.log.Timber
import java.net.Proxy
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Regular-YouTube (www.youtube.com WEB client) InnerTube access for Convxy's
 * native YouTube section: search, home feed, watch pages (metadata + related),
 * channels and playlists.
 *
 * Stream/playback resolution deliberately does NOT live here — Convxy's existing
 * [YTPlayerUtils] pipeline (player endpoint, PoTokens, SABR solving, cipher
 * deobfuscation) already handles any videoId, music or not. This object only
 * supplies browse/search/watch metadata and queue sources.
 *
 * Every parser is defensive: YouTube ships several renderer generations at once
 * (videoRenderer, compactVideoRenderer, lockupViewModel, …) and any field can be
 * missing. Parsers return nulls rather than throwing, and unknown shapes are
 * skipped, so a single odd item can never break a whole page.
 */
object YouTubeWeb {
    /** Public, static WEB innertube key (same constant the website itself uses). */
    private const val WEB_API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
    private const val API_URL = "https://www.youtube.com/youtubei/v1/"
    private const val ORIGIN = "https://www.youtube.com"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    var locale: YouTubeLocale = YouTubeLocale(
        gl = Locale.getDefault().country.ifBlank { "US" },
        hl = Locale.getDefault().toLanguageTag().ifBlank { "en" },
    )
    var visitorData: String? = null
    var proxy: Proxy? = null
        set(value) {
            field = value
            httpClient.close()
            httpClient = createClient()
        }
    var proxyAuth: String? = null

    private var httpClient = createClient()

    /**
     * Anonymous native-client tube used as a fallback when the WEB browse
     * responses come back degraded (header-only channel pages, empty tabs) —
     * a bot-wall/consent degradation the WEB client is the most exposed to.
     * The ANDROID_VR client is far more resistant, and its mobile browse
     * shape is parsed by the same parsers (single-column tabs).
     */
    private val nativeTube = InnerTube()

    private suspend fun nativeBrowse(
        browseId: String?,
        params: String?,
        continuation: String?,
    ): Result<JsonElement> = runCatching {
        nativeTube.locale = YouTube.locale
        nativeTube.proxy = YouTube.proxy
        nativeTube.proxyAuth = YouTube.proxyAuth
        nativeTube.browse(
            YouTubeClient.ANDROID_VR_1_43_32,
            browseId = browseId,
            params = params,
            continuation = continuation,
            setLogin = false,
        ).body<JsonElement>()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createClient() = HttpClient(OkHttp) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
        install(ContentEncoding) {
            gzip(0.9f)
            deflate(0.8f)
        }
        engine {
            config {
                connectTimeout(10, TimeUnit.SECONDS)
                readTimeout(20, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)
                // Qualified on purpose: OkHttpConfig also declares `proxy`, and the
                // unqualified name would silently read the (always-null) builder one.
                this@YouTubeWeb.proxy?.let { p ->
                    proxy(p)
                    this@YouTubeWeb.proxyAuth?.let { auth ->
                        proxyAuthenticator { _, response ->
                            response.request.newBuilder()
                                .header("Proxy-Authorization", auth)
                                .build()
                        }
                    }
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 20000
        }
        // Every endpoint below posts relative paths ("search", "browse", "next")
        // — without a base URL Ktor rejects them as invalid request URLs.
        defaultRequest {
            url(API_URL)
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.ytHeaders() {
        contentType(ContentType.Application.Json)
        header("X-YouTube-Client-Name", "1")
        header("X-YouTube-Client-Version", YouTubeClient.WEB.clientVersion)
        header("X-Origin", ORIGIN)
        header("Referer", "$ORIGIN/")
        visitorData?.let { header("X-Goog-Visitor-Id", it) }
        // Session handling. The session visitor id synced from the app is the
        // ACCOUNT one when logged in — sending it with an anonymous cookie is a
        // mismatched session that YouTube degrades (header-only channel pages,
        // empty watch/related payloads), which is exactly the "everything
        // browses fine logged out, nothing works after login" signature.
        // Logged in  -> full browser-grade credentials: account cookie +
        //               SAPISIDHASH authorization for the youtube.com origin
        //               (the same thing youtube.com itself sends).
        // Logged out -> consent cookie only (SOCS), matching an anonymous
        //               browser; without it some regions consent-wall instead
        //               of serving innertube data.
        val cookie = YouTube.cookie
        if (cookie.isNullOrBlank()) {
            header(HttpHeaders.Cookie, "SOCS=CAI")
        } else {
            val merged = if (cookie.contains("SOCS=", ignoreCase = true)) cookie else "SOCS=CAI; $cookie"
            header(HttpHeaders.Cookie, merged)
            val cookies = parseCookieString(merged)
            val sapisid = cookies["SAPISID"] ?: cookies["__Secure-3PAPISID"]
            if (sapisid != null) {
                val currentTime = System.currentTimeMillis() / 1000
                val sapisidHash = sha1("$currentTime $sapisid $ORIGIN")
                header("Authorization", "SAPISIDHASH ${currentTime}_${sapisidHash}")
            }
        }
        header(HttpHeaders.UserAgent, YouTubeClient.USER_AGENT_WEB)
        parameter("key", WEB_API_KEY)
        parameter("prettyPrint", "false")
    }

    /**
     * Diagnostics for the on-device file log: one line per endpoint call with
     * parse counts (or the failure), tag-prefixed so "Playback logs → Share"
     * captures why browse fails on a given device/network.
     */
    private fun <T> logEndpoint(name: String, result: Result<T>, summarize: (T) -> String): Result<T> {
        result
            .onSuccess { Timber.tag("YouTubeWeb").i("%s OK: %s", name, summarize(it)) }
            .onFailure { Timber.tag("YouTubeWeb").e(it, "%s FAILED", name) }
        return result
    }

    /**
     * Visitor-less retry for browse endpoints: a session visitorData rejected
     * by the WEB backend (stale, account-bound, consent-tangled) fails every
     * browse that carries it; one anonymous retry distinguishes that from the
     * content genuinely being unavailable. The field is restored on both paths.
     */
    private suspend fun <T> withVisitorFallback(
        isUsable: (T) -> Boolean,
        block: suspend () -> Result<T>,
    ): Result<T> {
        val first = block()
        if (first.isSuccess && isUsable(first.getOrThrow())) return first
        if (visitorData == null) {
            // Fresh-install bootstrap: browse without a visitor id is exactly
            // what comes back empty on a brand-new session. Suggestions is the
            // cheapest innertube call and every WEB response (including this
            // one) carries responseContext.visitorData — [captureVisitor]
            // picks it up, and the retry usually succeeds.
            Timber.tag("YouTubeWeb").w("no visitorData — bootstrapping via suggestions, then retrying browse")
            searchSuggestions("a")
            val second = block()
            if (second.isSuccess && isUsable(second.getOrThrow())) return second
            return first
        }
        val saved = visitorData ?: return first
        Timber.tag("YouTubeWeb").w(
            "browse unusable (%s) — retrying without visitorData",
            first.exceptionOrNull()?.message ?: "empty result",
        )
        visitorData = null
        return try {
            block()
        } finally {
            visitorData = saved
        }
    }

    /**
     * Bootstrap the session visitor id from any response that carries one.
     * On a fresh install [visitorData] is null, and browse endpoints served
     * without one come back empty (consent/bot wall) — but EVERY WEB response,
     * including plain search/player metadata calls, contains
     * `responseContext.visitorData`. Capturing it from the first successful
     * call is what makes browse work from the very first session instead of
     * "after watching some videos".
     */
    private fun captureVisitor(response: JsonElement) {
        if (visitorData != null) return
        response.path("responseContext")
            .path("visitorData")
            .jsonPrimitiveOrNull?.contentOrNullStrict
            ?.takeIf { it.isNotBlank() }
            ?.let { visitorData = it }
    }

    private fun context() = com.music.innertube.models.Context(
        client = com.music.innertube.models.Context.Client(
            clientName = "WEB",
            clientVersion = YouTubeClient.WEB.clientVersion,
            gl = locale.gl,
            hl = locale.hl,
            visitorData = visitorData,
        ),
        // The website declares the active account identity on every request
        // while logged in; omitting it makes the session half-authenticated.
        user = YouTube.cookie?.takeIf { it.isNotBlank() }
            ?.let { com.music.innertube.models.Context.User(onBehalfOfUser = YouTube.dataSyncId) }
            ?: com.music.innertube.models.Context.User(),
    )

    /** Retry wrapper for transient IO errors and 429/5xx, mirroring [InnerTube.withRetry]. */
    private suspend fun <T> withRetry(
        maxAttempts: Int = 3,
        initialDelay: Long = 500L,
        factor: Double = 2.0,
        block: suspend () -> T,
    ): T {
        var currentDelay = initialDelay
        var attempt = 0
        while (true) {
            try {
                return block()
            } catch (e: java.io.IOException) {
                attempt++
                if (attempt >= maxAttempts || coroutineCancelled(e)) throw e
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong()
            } catch (e: io.ktor.client.plugins.ResponseException) {
                val status = e.response.status.value
                attempt++
                if ((status != 429 && status !in 500..599) || attempt >= maxAttempts) throw e
                val retryAfterMs = e.response.headers["Retry-After"]?.toLongOrNull()?.times(1000L)
                delay(retryAfterMs ?: currentDelay)
                currentDelay = (currentDelay * factor).toLong()
            }
        }
    }

    private fun coroutineCancelled(e: Exception) = e is kotlinx.coroutines.CancellationException

    // ─────────────────────────────────────────────────────────────────────────
    // Endpoints
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun search(query: String, filter: WebSearchFilter = WebSearchFilter.NONE): Result<WebSearchPage> =
        withNetworkResult {
            val response = withRetry {
                httpClient.post("search") {
                    ytHeaders()
                    setBody(
                        SearchBody(
                            context = context(),
                            query = query,
                            params = filter.params,
                        )
                    )
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            parseSearchPage(response, filter)
        }

    suspend fun searchContinuation(continuation: String): Result<WebSearchPage> =
        withNetworkResult {
            val response = withRetry {
                httpClient.post("search") {
                    ytHeaders()
                    parameter("continuation", continuation)
                    parameter("ctoken", continuation)
                    setBody(
                        SearchBody(
                            context = context(),
                            query = null,
                            params = null,
                        )
                    )
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            parseSearchPage(response, WebSearchFilter.NONE)
        }

    /** YouTube search suggestions (client-side autocomplete, no innertube required). */
    suspend fun searchSuggestions(query: String): Result<List<String>> = withNetworkResult {
        val body = withRetry {
            httpClient.get("https://suggestqueries-clients6.youtube.com/complete/search") {
                parameter("client", "firefox")
                parameter("ds", "yt")
                parameter("hl", locale.hl)
                parameter("gl", locale.gl)
                parameter("q", query)
            }.body<String>()
        }
        Result.success(parseSuggestResponse(body))
    }

    /** YouTube home ("what to watch") feed. */
    suspend fun home(continuation: String? = null): Result<WebFeed> = withNetworkResult {
        if (continuation != null) {
            val response = withRetry {
                httpClient.post("browse") {
                    ytHeaders()
                    setBody(BrowseBody(context = context(), browseId = "FEwhat_to_watch", params = null, continuation = continuation))
                    parameter("continuation", continuation)
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            logEndpoint("homeContinuation", parseHomeFeed(response)) { feed ->
                "sections=${feed.sections.size} shorts=${feed.shorts.size} cont=${feed.continuation != null}"
            }
        } else {
            val result = withVisitorFallback(
                isUsable = { feed -> feed.sections.isNotEmpty() || feed.shorts.isNotEmpty() },
            ) {
                val response = withRetry {
                    httpClient.post("browse") {
                        ytHeaders()
                        setBody(BrowseBody(context = context(), browseId = "FEwhat_to_watch", params = null, continuation = null))
                    }.body<JsonElement>().also { captureVisitor(it) }
                }
                parseHomeFeed(response)
            }
            logEndpoint("home", result) { feed ->
                "videos=${feed.sections.sumOf { section -> section.videos.size }} shorts=${feed.shorts.size} cont=${feed.continuation != null}"
            }
        }
    }

    /** Watch page: title/views/date/description/channel + first page of related videos. */
    suspend fun watch(videoId: String): Result<WebWatchPage> = withNetworkResult {
        val response = withRetry {
            httpClient.post("next") {
                ytHeaders()
                setBody(
                    NextBody(
                        context = context(),
                        videoId = videoId,
                        playlistId = null,
                        playlistSetVideoId = null,
                        index = null,
                        params = null,
                        continuation = null,
                    )
                )
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        parseWatchPage(response, videoId)
    }

    /** Related-videos continuation for the watch page: items plus the next token (null when done). */
    suspend fun relatedContinuation(continuation: String): Result<Pair<List<WebVideo>, String?>> = withNetworkResult {
        val response = withRetry {
            httpClient.post("next") {
                ytHeaders()
                parameter("continuation", continuation)
                parameter("ctoken", continuation)
                setBody(
                    NextBody(
                        context = context(),
                        videoId = null,
                        playlistId = null,
                        playlistSetVideoId = null,
                        index = null,
                        params = null,
                        continuation = continuation,
                    )
                )
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        val videos = parseRelatedContinuation(response)
        Result.success(videos to response.findContinuation())
    }

    /**
     * Browse a channel. Accepts a UC… channel id, an @handle, a legacy /c/ or
     * /user/ name, or a full URL; handles are resolved to a UC… browse id when
     * the network call can do so.
     */
    suspend fun channel(idOrHandleOrUrl: String): Result<WebChannelPage> = withNetworkResult {
        val browseId = normalizeChannelId(idOrHandleOrUrl)
        var result = withVisitorFallback(
            isUsable = { page -> page.tabs.isNotEmpty() },
        ) {
            val response = withRetry {
                httpClient.post("browse") {
                    ytHeaders()
                    setBody(BrowseBody(context = context(), browseId = browseId, params = null, continuation = null))
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            parseChannelPage(response, browseId)
        }
        if (result.getOrNull()?.tabs?.isEmpty() != false) {
            // WEB failed outright or served a header-only page (no tabs) —
            // the bot-wall degradation. Retry the same browse through the
            // anonymous native client; if it returns tabs, prefer it.
            val native = nativeBrowse(browseId, null, null).mapCatching { response ->
                parseChannelPage(response, browseId).getOrThrow()
            }
            if (native.getOrNull()?.tabs?.isNotEmpty() == true) {
                Timber.tag("YouTubeWeb").i("channel: WEB page had no tabs — native fallback OK")
                result = native
            } else {
                Timber.tag("YouTubeWeb").w(
                    "channel: WEB page had no tabs and native fallback failed (%s)",
                    native.exceptionOrNull()?.message,
                )
            }
        }
        logEndpoint("channel", result) { page ->
            "id=${page.channel.id} tabs=${page.tabs.map { it.title }} subs=${page.channel.subscriberText}"
        }
        result
    }

    /** One tab (Videos / Shorts / Playlists / Live / Home) of a channel. */
    suspend fun channelTab(browseId: String, params: String): Result<WebChannelTabPage> = withNetworkResult {
        val response = withRetry {
            httpClient.post("browse") {
                ytHeaders()
                setBody(BrowseBody(context = context(), browseId = browseId, params = params, continuation = null))
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        val page = parseChannelTabPage(response)
        val finalPage = if (page.videos.isEmpty() && page.shorts.isEmpty() && page.playlists.isEmpty()) {
            val native = nativeBrowse(browseId, params, null).getOrNull()?.let { parseChannelTabPage(it) }
            if (native != null && (native.videos.isNotEmpty() || native.shorts.isNotEmpty() || native.playlists.isNotEmpty())) {
                Timber.tag("YouTubeWeb").i("channelTab: WEB tab empty — native fallback OK (videos=%d)", native.videos.size)
                native
            } else page
        } else page
        logTab("channelTab", finalPage, response)
    }

    suspend fun channelTabContinuation(continuation: String): Result<WebChannelTabPage> = withNetworkResult {
        val response = withRetry {
            httpClient.post("browse") {
                ytHeaders()
                setBody(BrowseBody(context = context(), browseId = null, params = null, continuation = continuation))
                parameter("continuation", continuation)
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        logTab("channelTabCont", parseChannelTabPage(response), response)
    }

    /** Open a public playlist by id (with or without the "VL" prefix). */
    suspend fun playlist(playlistId: String): Result<WebPlaylistPage> = withNetworkResult {
        val id = playlistId.removePrefix("VL")
        val response = withRetry {
            httpClient.post("browse") {
                ytHeaders()
                setBody(BrowseBody(context = context(), browseId = "VL$id", params = null, continuation = null))
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        parsePlaylistPage(response, id)
    }

    suspend fun playlistContinuation(continuation: String): Result<Pair<List<WebVideo>, String?>> = withNetworkResult {
        val response = withRetry {
            httpClient.post("browse") {
                ytHeaders()
                setBody(BrowseBody(context = context(), browseId = null, params = null, continuation = continuation))
                parameter("continuation", continuation)
            }.body<JsonElement>().also { captureVisitor(it) }
        }
        val videos = parsePlaylistContinuation(response)
        Result.success(videos to response.findContinuation())
    }

    /** Resolves a share URL to the video id it points at, following youtu.be etc. */
    suspend fun resolveVideoId(url: String): String? {
        val fromUrl = parseVideoIdFromUrl(url)
        if (fromUrl != null) return fromUrl
        return runCatching {
            val response = withRetry {
                httpClient.post("navigation/resolve_url") {
                    ytHeaders()
                    setBody(
                        buildJsonObject {
                            put("context", json.encodeToJsonElement(com.music.innertube.models.Context.serializer(), context()))
                            put("url", JsonPrimitive(url))
                        }
                    )
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            response
                .find("videoId")
                ?.jsonPrimitive?.contentOrNullStrict
                ?.takeIf { it.length == 11 }
        }.getOrNull()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Parsers
    // ─────────────────────────────────────────────────────────────────────────

    private fun parseSuggestResponse(body: String): List<String> {
        // client=firefox returns plain JSON: ["query", ["a","b",…], …]
        return runCatching {
            val arr = json.parseToJsonElement(body).jsonArray
            arr.getOrNull(1)?.jsonArray?.mapNotNull { el ->
                (el as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }
            }.orEmpty()
        }.getOrElse { emptyList() }
    }

    private fun parseSearchPage(response: JsonElement, filter: WebSearchFilter): Result<WebSearchPage> {
        val items = response.searchResultItems()
        val videos = items.mapNotNull { parseVideoRenderer(it) ?: parseLockupVideo(it) }
        val shorts = items.mapNotNull { parseShortsLockup(it) ?: parseLockupShort(it) }
        val channels = items.mapNotNull { parseChannelRenderer(it) ?: parseLockupChannel(it) }
        val playlists = items.mapNotNull { parseGridPlaylist(it) ?: parseLockupPlaylist(it) }
        val continuation = response.findContinuation()
        val page = when (filter) {
            WebSearchFilter.CHANNELS -> WebSearchPage(emptyList(), channels, emptyList(), continuation)
            WebSearchFilter.PLAYLISTS -> WebSearchPage(emptyList(), emptyList(), playlists, continuation)
            else -> WebSearchPage(videos, channels, playlists, continuation)
        }
        return Result.success(page)
    }

    /** Flattens every "renderer-ish" element of a search response, whatever the nesting. */
    private fun JsonElement.searchResultItems(): List<JsonElement> {
        val out = mutableListOf<JsonElement>()
        fun walk(el: JsonElement?) {
            when (el) {
                is JsonArray -> el.forEach(::walk)
                is JsonObject -> {
                    el.forEach { (key, value) ->
                        if (key == "videoRenderer" || key == "channelRenderer" ||
                            key == "gridPlaylistRenderer" || key == "lockupViewModel" ||
                            key == "shortsLockupViewModel" || key == "reelItemRenderer"
                        ) {
                            out += value
                        } else {
                            walk(value)
                        }
                    }
                }
                else -> Unit
            }
        }
        walk(this)
        return out
    }

    private fun parseHomeFeed(response: JsonElement): Result<WebFeed> {
        val items = mutableListOf<JsonElement>()
        fun walk(el: JsonElement?) {
            when (el) {
                is JsonArray -> el.forEach(::walk)
                is JsonObject -> {
                    el.forEach { (key, value) ->
                        when (key) {
                            "videoRenderer", "lockupViewModel", "shortsLockupViewModel",
                            "gridVideoRenderer", "reelItemRenderer" -> items += value
                            "richItemRenderer" -> walk(value)
                            "contents" -> walk(value)
                            "content" -> if (value is JsonObject) walk(value)
                            "items" -> walk(value)
                            else -> if (key != "thumbnails" && key != "sources") walk(value)
                        }
                    }
                }
                else -> Unit
            }
        }
        walk(response)

        val videos = items.mapNotNull { parseVideoRenderer(it) ?: parseGridVideoRenderer(it) ?: parseLockupVideo(it) }
        val shorts = items.mapNotNull { parseShortsLockup(it) ?: parseLockupShort(it) }.let { parsed ->
            // Lockup shorts also surface in parseLockupVideo via contentType; keep them exclusive.
            val shortIds = parsed.map { it.id }.toSet()
            parsed + videos.filter { it.isShort }.filter { it.id !in shortIds }
        }
        val longformVideos = videos.filter { !it.isShort }
        val continuation = response.findContinuation()
        return Result.success(
            WebFeed(
                sections = if (longformVideos.isNotEmpty()) {
                    listOf(WebFeedSection(title = "Recommended", videos = longformVideos))
                } else {
                    emptyList()
                },
                shorts = shorts.distinctBy { it.id },
                continuation = continuation,
            )
        )
    }

    private fun parseWatchPage(response: JsonElement, videoId: String): Result<WebWatchPage> {
        val results = response.path("contents")
            .path("twoColumnWatchNextResults")
            .path("results")
            .path("results")
            .path("contents")
            .asJsonArrayOrNull

        var primary: JsonObject? = null
        var secondary: JsonObject? = null
        results?.forEach { el ->
            val obj = el as? JsonObject ?: return@forEach
            if (obj.containsKey("videoPrimaryInfoRenderer") && primary == null) {
                primary = obj["videoPrimaryInfoRenderer"] as? JsonObject
            }
            if (obj.containsKey("videoSecondaryInfoRenderer") && secondary == null) {
                secondary = obj["videoSecondaryInfoRenderer"] as? JsonObject
            }
        }

        val primaryObj = primary
        if (primaryObj == null) {
            // The whole watch payload is missing — most likely an unplayable video.
            return Result.failure(java.io.IOException("Watch page unavailable for $videoId"))
        }

        val title = primaryObj.path("title").firstRunOrText()
        val viewCountText = primaryObj.path("viewCount")
            .path("videoViewCountRenderer")
            .path("viewCount")
            .firstRunOrText()
            ?: primaryObj.path("viewCountText").firstRunOrText()
        val dateText = primaryObj.path("dateText").firstRunOrText()
            ?: primaryObj.path("relativeDateText").firstRunOrText()
        val isLive = (viewCountText?.contains("watching", ignoreCase = true) == true) ||
            (primaryObj.path("viewCount").path("videoViewCountRenderer").path("isLive").jsonPrimitiveOrNull?.content == "true")

        val secondaryObj = secondary
        val owner = secondaryObj
            .path("owner")
            .path("videoOwnerRenderer")
        val channelId = owner.path("navigationEndpoint")
            .path("browseEndpoint")
            .path("browseId")
            .jsonPrimitiveOrNull?.contentOrNullStrict
            ?: secondaryObj.path("owner").path("videoOwnerRenderer").path("title").path("runs")
                .asJsonArrayOrNull?.firstOrNull()
                ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
                ?.jsonPrimitiveOrNull?.contentOrNullStrict
        val channelName = owner.path("title").firstRunOrText()
        val channelAvatar = owner.path("thumbnail").thumbnailsLargest()
        val subscriberText = owner.path("subscriberCountText").firstRunOrText()

        val description = secondaryObj
            .path("attributedDescription")
            .path("content")
            .jsonPrimitiveOrNull?.contentOrNullStrict
            ?: secondaryObj.path("description").firstRunOrText()

        val likeCountText = findLikeCountText(response)

        // Related videos live under secondaryResults.
        val secondaryResults = response.path("contents")
            .path("twoColumnWatchNextResults")
            .path("secondaryResults")
            .path("secondaryResults")
        val relatedItems = mutableListOf<JsonElement>()
        secondaryResults.path("results").asJsonArrayOrNull?.forEach { relatedItems += it }
        secondaryResults.path("contents").asJsonArrayOrNull?.forEach { relatedItems += it }

        val related = relatedItems.mapNotNull {
            parseCompactVideoRenderer(it) ?: parseLockupVideo(it) ?: parseVideoRenderer(it)
        }.filter { it.id != videoId }.distinctBy { it.id }
        val relatedContinuation = secondaryResults.findContinuation()
            ?: relatedItems.lastOrNull()?.findContinuation()

        val lengthSeconds = findLengthSeconds(response, videoId)

        val page = WebWatchPage(
            video = WebVideo(
                id = videoId,
                title = title.orEmpty(),
                channelId = channelId,
                channelName = channelName,
                thumbnail = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
                durationSeconds = lengthSeconds,
                viewsText = viewCountText,
                publishedText = dateText,
                isShort = false,
            ),
            channelAvatarUrl = channelAvatar,
            description = description?.decodeHtmlEntities(),
            likeCountText = likeCountText,
            subscriberText = subscriberText,
            isLive = isLive,
            related = related,
            relatedContinuation = relatedContinuation,
        )
        return Result.success(page)
    }

    private fun findLikeCountText(response: JsonElement): String? {
        // Like counts moved between several renderers over the years; probe the known shapes.
        response.path("contents")
            .path("twoColumnWatchNextResults")
            .path("results")
            .path("results")
            .path("contents")
            .asJsonArrayOrNull
            ?.forEach { el ->
                val likeButton = el.find("likeButtonViewModel") ?: el.find("segmentedLikeDislikeButtonViewModel")
                val title = likeButton
                    ?.find("defaultButtonViewModel")
                    ?.find("buttonViewModel")
                    ?.path("title")
                    ?.firstRunOrText()
                if (!title.isNullOrBlank()) return title
                val accessText = likeButton
                    ?.find("toggleButtonViewModel")
                    ?.find("defaultButtonViewModel")
                    ?.find("buttonViewModel")
                    ?.find("accessibilityText")
                    ?.jsonPrimitiveOrNull?.contentOrNullStrict
                if (!accessText.isNullOrBlank()) {
                    // "like this video along with 12,345 other people" → extract the number phrase
                    Regex("([\\d.,]+[KM]?)\\s+(other people|likes)", RegexOption.IGNORE_CASE)
                        .find(accessText)?.groupValues?.getOrNull(1)?.let { return it }
                }
            }
        return null
    }

    private fun findLengthSeconds(response: JsonElement, videoId: String): Int? {
        // lengthSeconds usually appears in the overlay under the player or in
        // videoPrimaryInfoRenderer's sibling metadata; probe widely, stay cheap.
        val lengthText = response.find("lengthText")
        val fromLengthText = lengthText?.firstRunOrText()?.parseDurationSeconds()
        if (fromLengthText != null) return fromLengthText
        val lengthSeconds = response.find("lengthSeconds")
            ?.jsonPrimitiveOrNull?.contentOrNullStrict?.toIntOrNull()
        if (lengthSeconds != null && lengthSeconds > 0) return lengthSeconds
        return null
    }

    private fun parseRelatedContinuation(response: JsonElement): List<WebVideo> {
        val items = mutableListOf<JsonElement>()
        response.path("onResponseReceivedActions")
            .asJsonArrayOrNull
            ?.forEach { action ->
                action.path("appendContinuationItemsAction")
                    .path("continuationItems")
                    .asJsonArrayOrNull
                    ?.forEach { items += it }
            }
        response.path("continuationContents")
            .path("itemSectionContinuation")
            .path("contents")
            .asJsonArrayOrNull
            ?.forEach { items += it }
        return items.mapNotNull { parseCompactVideoRenderer(it) ?: parseLockupVideo(it) }.distinctBy { it.id }
    }

    /** Tab diagnostics: parsed counts, and — on an empty parse — which renderer keys the payload actually used. */
    private fun logTab(name: String, page: WebChannelTabPage, response: JsonElement): Result<WebChannelTabPage> {
        val result = Result.success(page)
        if (page.videos.isNotEmpty() || page.shorts.isNotEmpty() || page.playlists.isNotEmpty()) {
            Timber.tag("YouTubeWeb").i(
                "%s OK: videos=%d shorts=%d playlists=%d cont=%s",
                name, page.videos.size, page.shorts.size, page.playlists.size, page.continuation != null,
            )
        } else {
            Timber.tag("YouTubeWeb").w(
                "%s EMPTY: rendererKeys=[%s] cont=%s",
                name, response.findRendererKeys(), page.continuation != null,
            )
        }
        return result
    }

    /** Renderer keys present anywhere in the payload — the fingerprint needed to support a new shape. */
    private fun JsonElement.findRendererKeys(): String {
        val skip = setOf("contents", "items", "content", "continuationItems", "tabs", "tabRenderer",
            "thumbnails", "sources", "thumbnail", "title", "runs", "text", "endpoint", "navigationEndpoint")
        val found = linkedSetOf<String>()
        fun walk(el: JsonElement?, depth: Int) {
            if (depth > 14 || found.size > 24) return
            when (el) {
                is JsonArray -> el.forEach { walk(it, depth + 1) }
                is JsonObject -> el.forEach { (key, value) ->
                    if (key.endsWith("Renderer") || key.endsWith("ViewModel") || key.endsWith("Bar")) found += key
                    if (key !in skip) walk(value, depth + 1)
                }
                else -> Unit
            }
        }
        walk(this, 0)
        return found.take(20).joinToString(",")
    }

    private fun parseChannelPage(response: JsonElement, requestedId: String): Result<WebChannelPage> {
        val header = response.path("header")
        val metadata = response.path("metadata")
            .path("channelMetadataRenderer")

        val title = header.path("pageHeaderRenderer").path("pageTitle").jsonPrimitiveOrNull?.contentOrNullStrict
            ?: header.path("c4TabbedHeaderRenderer").path("title").firstRunOrText()
            ?: metadata.path("title").jsonPrimitiveOrNull?.contentOrNullStrict
            ?: requestedId

        val resolvedId = metadata.path("externalId").jsonPrimitiveOrNull?.contentOrNullStrict ?: requestedId
        val avatar = header.path("c4TabbedHeaderRenderer").path("avatar").thumbnailsLargest()
            ?: header.path("pageHeaderRenderer")
                .path("content")
                .path("pageHeaderViewModel")
                .path("image")
                .path("decoratedAvatarViewModel")
                .path("avatar")
                .path("avatarViewModel")
                .path("image")
                .thumbnailsLargest()
            ?: header.find("avatar")?.thumbnailsLargest()

        // Subscriber / video counts sit in metadata rows on the new header and in
        // dedicated fields on the old one.
        var subscriberText: String? = header.path("c4TabbedHeaderRenderer")
            .path("subscriberCountText").firstRunOrText()
        var videoCountText: String? = header.path("c4TabbedHeaderRenderer")
            .path("videosCountText").firstRunOrText()
        if (subscriberText == null || videoCountText == null) {
            val rows = header.path("pageHeaderRenderer")
                .path("content")
                .path("pageHeaderViewModel")
                .path("metadata")
                .path("contentMetadataViewModel")
                .path("metadataRows")
            rows.asJsonArrayOrNull?.forEach { row ->
                row.path("metadataParts").asJsonArrayOrNull?.forEach { part ->
                    val text = part.path("text").path("content").jsonPrimitiveOrNull?.contentOrNullStrict ?: return@forEach
                    if (text.contains("subscriber", ignoreCase = true)) subscriberText = text
                    if (text.contains("video", ignoreCase = true)) videoCountText = text
                }
            }
        }

        val description = metadata.path("description").jsonPrimitiveOrNull?.contentOrNullStrict
            ?: header.path("pageHeaderRenderer")
                .path("content")
                .path("pageHeaderViewModel")
                .path("description")
                .firstRunOrText()
            ?: header.path("c4TabbedHeaderRenderer").path("tagline").firstRunOrText()

        // Handle ( vanity url ), best effort.
        val handle = metadata.path("vanityChannelUrl").jsonPrimitiveOrNull?.contentOrNullStrict
            ?.substringAfterLast("/")?.takeIf { it.startsWith("@") }

        var defaultTabIndex = 0
        var homeContent: WebChannelTabPage? = null
        // Desktop WEB ships twoColumnBrowseResultsRenderer; some payloads
        // (mobile-web shape, A/B tests) use singleColumnBrowseResultsRenderer
        // with the same tabs array. Support both or the channel renders as a
        // bare header with no content.
        val tabs = (response.path("contents")
            .path("twoColumnBrowseResultsRenderer")
            .path("tabs").asJsonArrayOrNull
            ?: response.path("contents")
                .path("singleColumnBrowseResultsRenderer")
                .path("tabs").asJsonArrayOrNull)
            ?.let { tabEls ->
                val parsed = ArrayList<Pair<WebChannelTab, JsonElement?>>(tabEls.size)
                tabEls.forEach { tabEl ->
                    val tab = tabEl.path("tabRenderer").takeIf { it != JsonNull }?.let {
                        if (it is JsonObject && it.containsKey("title")) it else tabEl.path("expandableTabRenderer")
                    } ?: tabEl.path("expandableTabRenderer")
                    val tabTitle = tab.path("title").firstRunOrText() ?: return@forEach
                    val params = tab.path("endpoint")
                        .path("browseEndpoint")
                        .path("params")
                        .jsonPrimitiveOrNull?.contentOrNullStrict
                    parsed += WebChannelTab(title = tabTitle, params = params) to tab.path("content").takeIf { it !is JsonNull }
                }
                // Default landing tab: the first PARAMETRIZED tab (the Videos
                // tab on every channel shape we've seen). Tab 0 is Home and a
                // channel landing there is exactly the "opens but shows no
                // videos" bug. Only fall back to Home when nothing else exists.
                defaultTabIndex = parsed.indexOfFirst { it.first.params != null }.takeIf { it >= 0 } ?: 0
                // The Home tab ships its shelves inline (no params) — parse
                // them once here so selecting it never renders an empty list.
                homeContent = parsed.firstOrNull { it.first.params == null && it.second != null }
                    ?.second?.let { parseChannelTabPage(it) }
                parsed.map { it.first }
            }.orEmpty()

        val page = WebChannelPage(
            channel = WebChannel(
                id = resolvedId,
                title = title,
                avatarUrl = avatar,
                subscriberText = subscriberText,
                videoCountText = videoCountText,
                description = description?.decodeHtmlEntities(),
            ),
            tabs = tabs,
            defaultTabIndex = defaultTabIndex,
            homeContent = homeContent,
        )
        return Result.success(page)
    }

    private fun parseChannelTabPage(response: JsonElement): WebChannelTabPage {
        val items = mutableListOf<JsonElement>()
        fun walk(el: JsonElement?) {
            when (el) {
                is JsonArray -> el.forEach(::walk)
                is JsonObject -> {
                    el.forEach { (key, value) ->
                        when (key) {
                            "videoRenderer", "gridVideoRenderer", "lockupViewModel",
                            "shortsLockupViewModel", "gridPlaylistRenderer", "reelItemRenderer" -> items += value
                            "richItemRenderer", "contents", "items", "content" -> walk(value)
                            else -> if (key != "thumbnails" && key != "sources") walk(value)
                        }
                    }
                }
                else -> Unit
            }
        }
        walk(response)

        val videos = items.mapNotNull { parseVideoRenderer(it) ?: parseGridVideoRenderer(it) ?: parseLockupVideo(it) }
            .filter { !it.isShort }
            .distinctBy { it.id }
        val shorts = items.mapNotNull { parseShortsLockup(it) ?: parseLockupShort(it) ?: parseReelItem(it) }.distinctBy { it.id }
        val playlists = items.mapNotNull { parseGridPlaylist(it) ?: parseLockupPlaylist(it) }
            .distinctBy { it.id }
        return WebChannelTabPage(
            videos = videos,
            shorts = shorts,
            playlists = playlists,
            continuation = response.findContinuation(),
        )
    }

    private fun parsePlaylistPage(response: JsonElement, playlistId: String): Result<WebPlaylistPage> {
        val headerOld = response.path("header").path("playlistHeaderRenderer")
        val headerNew = response.path("header").path("pageHeaderRenderer")

        val title = headerOld.path("title").firstRunOrText()
            ?: headerNew.path("pageTitle").jsonPrimitiveOrNull?.contentOrNullStrict
            ?: response.path("metadata").path("playlistMetadataRenderer").path("title").jsonPrimitiveOrNull?.contentOrNullStrict

        val ownerName = headerOld.path("ownerText").firstRunOrText()
            ?: headerNew.path("content").path("pageHeaderViewModel")
                .path("metadata").path("contentMetadataViewModel")
                .path("metadataRows").asJsonArrayOrNull
                ?.firstOrNull()
                ?.path("metadataParts")?.asJsonArrayOrNull
                ?.firstOrNull()
                ?.path("text")?.path("content")?.jsonPrimitiveOrNull?.contentOrNullStrict
        val ownerId = headerOld.path("ownerText")
            .asJsonArrayOrNull?.firstOrNull()
            ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
            ?.jsonPrimitiveOrNull?.contentOrNullStrict

        val itemCountText = headerOld.path("numVideosText").firstRunOrText()
            ?: headerOld.path("stats").asJsonArrayOrNull
                ?.firstOrNull()?.firstRunOrText()

        val thumbnail = response.path("microformat")
            .path("microformatDataRenderer")
            .path("thumbnail")
            .thumbnailsLargest()
            ?: response.find("playlistHeaderRenderer")?.find("thumbnail")?.thumbnailsLargest()

        val itemElements = mutableListOf<JsonElement>()
        response.path("contents")
            .path("twoColumnBrowseResultsRenderer")
            .path("tabs")
            .asJsonArrayOrNull
            ?.forEach { tab ->
                walkAllRenderers(tab.path("tabRenderer").path("content")) { key, value ->
                    if (key == "playlistVideoRenderer" || key == "playlistPanelVideoRenderer" || key == "lockupViewModel") {
                        itemElements += value
                    }
                }
            }

        val videos = itemElements.mapNotNull {
            parsePlaylistVideoRenderer(it) ?: parseLockupVideo(it)
        }.distinctBy { it.id }

        return Result.success(
            WebPlaylistPage(
                playlist = WebPlaylist(
                    id = playlistId,
                    title = title.orEmpty(),
                    thumbnail = thumbnail,
                    itemCountText = itemCountText,
                    ownerName = ownerName,
                    ownerId = ownerId,
                ),
                videos = videos,
                continuation = response.findContinuation(),
            )
        )
    }

    private fun parsePlaylistContinuation(response: JsonElement): List<WebVideo> {
        val items = mutableListOf<JsonElement>()
        response.path("onResponseReceivedActions")
            .asJsonArrayOrNull
            ?.forEach { action ->
                action.path("appendContinuationItemsAction")
                    .path("continuationItems")
                    .asJsonArrayOrNull
                    ?.forEach { items += it }
            }
        response.path("continuationContents")
            .path("playlistVideoListContinuation")
            .path("contents")
            .asJsonArrayOrNull
            ?.forEach { items += it }
        return items.mapNotNull { parsePlaylistVideoRenderer(it) ?: parseLockupVideo(it) }.distinctBy { it.id }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Individual renderer parsers
    // ─────────────────────────────────────────────────────────────────────────

    private fun parseVideoRenderer(el: JsonElement): WebVideo? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["videoRenderer"] as? JsonObject) ?: obj
        val videoId = renderer.path("videoId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        if (videoId.length != 11) return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        val ownerText = renderer.path("ownerText").asJsonArrayOrNull ?: renderer.path("longBylineText").asJsonArrayOrNull
        val channelName = ownerText?.firstOrNull()?.path("text")?.jsonPrimitiveOrNull?.contentOrNullStrict
            ?: renderer.path("shortBylineText").asJsonArrayOrNull?.firstOrNull()
                ?.path("text")?.jsonPrimitiveOrNull?.contentOrNullStrict
        val channelId = ownerText?.firstOrNull()
            ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
            ?.jsonPrimitiveOrNull?.contentOrNullStrict
        val duration = renderer.path("lengthText").firstRunOrText()?.parseDurationSeconds()
        val badges = renderer.path("badges").asJsonArrayOrNull
            ?.mapNotNull { it.path("metadataBadgeRenderer").path("label").jsonPrimitiveOrNull?.contentOrNullStrict }
            .orEmpty()
        val viewsText = renderer.path("viewCountText").firstRunOrText()
        val publishedText = renderer.path("publishedTimeText").firstRunOrText()
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = channelId,
            channelName = channelName,
            thumbnail = renderer.path("thumbnail").thumbnailsLargest(),
            durationSeconds = duration,
            viewsText = viewsText,
            publishedText = publishedText,
            isShort = false,
            badges = badges,
        )
    }

    private fun parseGridVideoRenderer(el: JsonElement): WebVideo? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["gridVideoRenderer"] as? JsonObject) ?: return null
        val videoId = renderer.path("videoId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = renderer.path("shortBylineText").asJsonArrayOrNull?.firstOrNull()
                ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
                ?.jsonPrimitiveOrNull?.contentOrNullStrict,
            channelName = renderer.path("shortBylineText").asJsonArrayOrNull?.firstOrNull()
                ?.path("text")?.jsonPrimitiveOrNull?.contentOrNullStrict,
            thumbnail = renderer.path("thumbnail").thumbnailsLargest(),
            durationSeconds = renderer.path("lengthText").firstRunOrText()?.parseDurationSeconds(),
            viewsText = renderer.path("viewCountText").firstRunOrText(),
            publishedText = renderer.path("publishedTimeText").firstRunOrText(),
        )
    }

    private fun parseCompactVideoRenderer(el: JsonElement): WebVideo? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["compactVideoRenderer"] as? JsonObject) ?: return null
        val videoId = renderer.path("videoId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        val byline = renderer.path("longBylineText").asJsonArrayOrNull
            ?: renderer.path("shortBylineText").asJsonArrayOrNull
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = byline?.firstOrNull()
                ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
                ?.jsonPrimitiveOrNull?.contentOrNullStrict,
            channelName = byline?.firstOrNull()?.path("text")?.jsonPrimitiveOrNull?.contentOrNullStrict,
            thumbnail = renderer.path("thumbnail").thumbnailsLargest(),
            durationSeconds = renderer.path("lengthText").firstRunOrText()?.parseDurationSeconds(),
            viewsText = renderer.path("viewCountText").firstRunOrText(),
            publishedText = renderer.path("publishedTimeText").firstRunOrText(),
        )
    }

    /**
     * The modern `lockupViewModel` renderer (used by search, home and watch
     * related lists) — covers videos, shorts, playlists and channels by
     * contentType.
     */
    private fun parseLockupVideo(el: JsonElement): WebVideo? {
        val obj = ((el as? JsonObject)?.get("lockupViewModel") as? JsonObject) ?: (el as? JsonObject) ?: return null
        if (!obj.containsKey("contentId")) return null
        val contentType = obj.path("contentType").jsonPrimitiveOrNull?.contentOrNullStrict.orEmpty()
        if (contentType.contains("PLAYLIST", ignoreCase = true)) return null
        if (contentType.contains("CHANNEL", ignoreCase = true)) return null

        val id = obj.path("contentId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val metadata = obj.path("metadata").path("lockupMetadataViewModel")
        val title = metadata.path("title").path("content").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null

        val parts = mutableListOf<String>()
        metadata.path("metadata").path("contentMetadataViewModel")
            .path("metadataRows").asJsonArrayOrNull
            ?.forEach { row ->
                row.path("metadataParts").asJsonArrayOrNull?.forEach { part ->
                    part.path("text").path("content").jsonPrimitiveOrNull?.contentOrNullStrict?.let { parts += it }
                }
            }
        val viewsText = parts.firstOrNull {
            it.contains("view", ignoreCase = true) || it.contains("watching", ignoreCase = true)
        }
        val publishedText = parts.firstOrNull {
            it.contains("ago", ignoreCase = true) || it.contains("Streamed", ignoreCase = true) ||
                it.contains("Premiere", ignoreCase = true)
        }
        val channelName = parts.firstOrNull { it != viewsText && it != publishedText }
        val channelId = metadata.find("browseId")?.jsonPrimitiveOrNull?.contentOrNullStrict
            ?.takeIf { it.startsWith("UC") }

        val duration = obj.findAll("thumbnailBadgeViewModel")
            .mapNotNull { it.path("text").path("content").jsonPrimitiveOrNull?.contentOrNullStrict }
            .firstOrNull { it.contains(':') }
            ?.parseDurationSeconds()

        val thumbnail = obj.path("contentImage")
            .path("thumbnailViewModel")
            .path("image")
            .thumbnailsLargest()
            ?: obj.find("image")?.thumbnailsLargest()

        return WebVideo(
            id = id,
            title = title.decodeHtmlEntities(),
            channelId = channelId,
            channelName = channelName,
            thumbnail = thumbnail,
            durationSeconds = duration,
            viewsText = viewsText,
            publishedText = publishedText,
            // Explicit SHORT content type only. The old `duration <= 61`
            // heuristic reclassified real sub-minute NORMAL videos as shorts,
            // and the channel Videos tab then dropped them from the list.
            isShort = contentType.contains("SHORT", ignoreCase = true),
        )
    }

    private fun parseLockupShort(el: JsonElement): WebVideo? {
        val obj = ((el as? JsonObject)?.get("lockupViewModel") as? JsonObject) ?: (el as? JsonObject) ?: return null
        val contentType = obj.path("contentType").jsonPrimitiveOrNull?.contentOrNullStrict.orEmpty()
        if (!contentType.contains("SHORT", ignoreCase = true)) return null
        return parseLockupVideo(obj)?.copy(isShort = true)
    }

    /** Legacy Shorts-tab renderer (`reelItemRenderer`), still served by some channel shapes. */
    private fun parseReelItem(el: JsonElement): WebVideo? {
        val reel = (el as? JsonObject)?.get("reelItemRenderer") as? JsonObject ?: return null
        val videoId = reel.path("videoId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val title = reel.path("title").firstRunOrText() ?: return null
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = null,
            channelName = null,
            thumbnail = reel.path("thumbnail").thumbnailsLargest(),
            durationSeconds = reel.path("lengthText").firstRunOrText()?.parseDurationSeconds(),
            viewsText = reel.path("viewCountText").firstRunOrText(),
            publishedText = null,
            isShort = true,
        )
    }

    private fun parseShortsLockup(el: JsonElement): WebVideo? {
        val obj = (el as? JsonObject)?.get("shortsLockupViewModel") as? JsonObject ?: (el as? JsonObject)?.takeIf {
            it.containsKey("onTap") && it.containsKey("overlayMetadata")
        } ?: return null
        val videoId = obj.path("onTap")
            .path("innertubeCommand")
            .path("reelWatchEndpoint")
            .path("videoId")
            .jsonPrimitiveOrNull?.contentOrNullStrict
            ?: obj.path("entityId").jsonPrimitiveOrNull?.contentOrNullStrict?.removePrefix("shorts-shelf-item-")
        if (videoId == null || videoId.length != 11) return null
        val title = obj.path("overlayMetadata")
            .path("primaryText")
            .path("content")
            .jsonPrimitiveOrNull?.contentOrNullStrict
            ?: return null
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = null,
            channelName = null,
            thumbnail = obj.path("thumbnailViewModel").path("image").thumbnailsLargest(),
            durationSeconds = null,
            viewsText = obj.path("overlayMetadata").path("secondaryText")
                .path("content").jsonPrimitiveOrNull?.contentOrNullStrict,
            publishedText = null,
            isShort = true,
        )
    }

    private fun parseChannelRenderer(el: JsonElement): WebChannel? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["channelRenderer"] as? JsonObject) ?: return null
        val id = renderer.path("channelId").jsonPrimitiveOrNull?.contentOrNullStrict
            ?: renderer.path("navigationEndpoint").path("browseEndpoint").path("browseId")
                .jsonPrimitiveOrNull?.contentOrNullStrict
            ?: return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        return WebChannel(
            id = id,
            title = title.decodeHtmlEntities(),
            avatarUrl = renderer.path("thumbnail").thumbnailsLargest(),
            subscriberText = renderer.path("subscriberCountText").firstRunOrText(),
            videoCountText = renderer.path("videoCountText").firstRunOrText(),
            description = renderer.path("descriptionSnippet").firstRunOrText()?.decodeHtmlEntities(),
        )
    }

    private fun parseLockupChannel(el: JsonElement): WebChannel? {
        val obj = ((el as? JsonObject)?.get("lockupViewModel") as? JsonObject) ?: (el as? JsonObject) ?: return null
        val contentType = obj.path("contentType").jsonPrimitiveOrNull?.contentOrNullStrict.orEmpty()
        if (!contentType.contains("CHANNEL", ignoreCase = true)) return null
        val id = obj.path("contentId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val metadata = obj.path("metadata").path("lockupMetadataViewModel")
        val title = metadata.path("title").path("content").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val parts = mutableListOf<String>()
        metadata.path("metadata").path("contentMetadataViewModel")
            .path("metadataRows").asJsonArrayOrNull
            ?.forEach { row ->
                row.path("metadataParts").asJsonArrayOrNull?.forEach { part ->
                    part.path("text").path("content").jsonPrimitiveOrNull?.contentOrNullStrict?.let { parts += it }
                }
            }
        return WebChannel(
            id = id,
            title = title.decodeHtmlEntities(),
            avatarUrl = obj.path("contentImage")
                .path("decoratedAvatarViewModel")
                .path("avatar")
                .path("avatarViewModel")
                .path("image")
                .thumbnailsLargest()
                ?: obj.find("image")?.thumbnailsLargest(),
            subscriberText = parts.firstOrNull { it.contains("subscriber", ignoreCase = true) },
            videoCountText = parts.firstOrNull { it.contains("video", ignoreCase = true) },
        )
    }

    private fun parseGridPlaylist(el: JsonElement): WebPlaylist? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["gridPlaylistRenderer"] as? JsonObject) ?: return null
        val id = renderer.path("playlistId").jsonPrimitiveOrNull?.contentOrNullStrict?.removePrefix("VL") ?: return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        return WebPlaylist(
            id = id,
            title = title.decodeHtmlEntities(),
            thumbnail = renderer.path("thumbnail").thumbnailsLargest(),
            itemCountText = renderer.path("videoCountText").firstRunOrText()
                ?: renderer.path("videoCountShortText").firstRunOrText(),
            ownerName = renderer.path("ownerText").firstRunOrText(),
            ownerId = renderer.path("ownerText").asJsonArrayOrNull?.firstOrNull()
                ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
                ?.jsonPrimitiveOrNull?.contentOrNullStrict,
        )
    }

    private fun parseLockupPlaylist(el: JsonElement): WebPlaylist? {
        val obj = ((el as? JsonObject)?.get("lockupViewModel") as? JsonObject) ?: (el as? JsonObject) ?: return null
        val contentType = obj.path("contentType").jsonPrimitiveOrNull?.contentOrNullStrict.orEmpty()
        if (!contentType.contains("PLAYLIST", ignoreCase = true)) return null
        val id = obj.path("contentId").jsonPrimitiveOrNull?.contentOrNullStrict?.removePrefix("VL") ?: return null
        val metadata = obj.path("metadata").path("lockupMetadataViewModel")
        val title = metadata.path("title").path("content").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val parts = mutableListOf<String>()
        metadata.path("metadata").path("contentMetadataViewModel")
            .path("metadataRows").asJsonArrayOrNull
            ?.forEach { row ->
                row.path("metadataParts").asJsonArrayOrNull?.forEach { part ->
                    part.path("text").path("content").jsonPrimitiveOrNull?.contentOrNullStrict?.let { parts += it }
                }
            }
        return WebPlaylist(
            id = id,
            title = title.decodeHtmlEntities(),
            thumbnail = obj.path("contentImage")
                .path("collectionThumbnailViewModel")
                .path("primaryThumbnail")
                .path("thumbnailViewModel")
                .path("image")
                .thumbnailsLargest()
                ?: obj.find("image")?.thumbnailsLargest(),
            itemCountText = parts.firstOrNull { it.contains("video", ignoreCase = true) },
            ownerName = parts.lastOrNull { !it.contains("video", ignoreCase = true) },
        )
    }

    private fun parsePlaylistVideoRenderer(el: JsonElement): WebVideo? {
        val obj = el as? JsonObject ?: return null
        val renderer = (obj["playlistVideoRenderer"] as? JsonObject)
            ?: (obj["playlistPanelVideoRenderer"] as? JsonObject)
            ?: return null
        val videoId = renderer.path("videoId").jsonPrimitiveOrNull?.contentOrNullStrict ?: return null
        val title = renderer.path("title").firstRunOrText() ?: return null
        // Playlist items keep views/date in `videoInfo.runs` ("1.2M views", "3 years ago").
        val info = renderer.path("videoInfo").firstRunOrText()
        var viewsText: String? = null
        var publishedText: String? = null
        if (info != null) {
            if (info.contains("view", ignoreCase = true) || info.contains("watching", ignoreCase = true)) {
                viewsText = info.substringBefore("•").trim()
                publishedText = info.substringAfter("•", "").trim().ifBlank { null }
            } else {
                publishedText = info
            }
        }
        return WebVideo(
            id = videoId,
            title = title.decodeHtmlEntities(),
            channelId = renderer.path("shortBylineText").asJsonArrayOrNull?.firstOrNull()
                ?.path("navigationEndpoint")?.path("browseEndpoint")?.path("browseId")
                ?.jsonPrimitiveOrNull?.contentOrNullStrict,
            channelName = renderer.path("shortBylineText").asJsonArrayOrNull?.firstOrNull()
                ?.path("text")?.jsonPrimitiveOrNull?.contentOrNullStrict,
            thumbnail = renderer.path("thumbnail").thumbnailsLargest(),
            durationSeconds = renderer.path("lengthText").firstRunOrText()?.parseDurationSeconds(),
            viewsText = viewsText,
            publishedText = publishedText,
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Generic JSON helpers (all defensive, never throw on missing shapes)
    // ─────────────────────────────────────────────────────────────────────────

    private val JsonElement?.asJsonArrayOrNull: JsonArray?
        get() = (this as? JsonArray)

    private val JsonElement?.jsonPrimitiveOrNull: JsonPrimitive?
        get() = (this as? JsonPrimitive)

    private val JsonPrimitive.contentOrNullStrict: String?
        get() = if (this is kotlinx.serialization.json.JsonNull) null else content

    private fun JsonElement?.path(vararg keys: String): JsonElement? {
        var current = this
        for (key in keys) {
            current = (current as? JsonObject)?.get(key)
            if (current == null) return null
        }
        return current
    }

    private fun JsonElement?.firstRunOrText(): String? {
        val obj = this as? JsonObject ?: return null
        obj["simpleText"]?.jsonPrimitiveOrNull?.contentOrNullStrict?.let { if (it.isNotBlank()) return it }
        val runs = obj["runs"] as? JsonArray ?: return null
        val text = runs.joinToString("") { run ->
            (run as? JsonObject)?.get("text")?.jsonPrimitiveOrNull?.contentOrNullStrict.orEmpty()
        }
        return text.ifBlank { null }
    }

    /** Picks the biggest thumbnail in a `thumbnails: [{url,width,height}]` structure. */
    private fun JsonElement?.thumbnailsLargest(): String? {
        // Both shapes exist: `thumbnail: {thumbnails: [...]}` (renderers) and
        // `thumbnailViewModel: {image: {sources: [...]}}` (lockupViewModel) —
        // the second one is what watch-next related lists ship today, so a
        // parser that only reads `thumbnails` loses every related thumbnail.
        val arr = ((this as? JsonObject)?.get("thumbnails")
            ?: (this as? JsonObject)?.get("sources")
            ?: this) as? JsonArray ?: return null
        var best: Pair<Int, String>? = null
        arr.forEach { el ->
            val obj = el as? JsonObject ?: return@forEach
            val url = obj["url"]?.jsonPrimitiveOrNull?.contentOrNullStrict ?: return@forEach
            val width = obj["width"]?.jsonPrimitiveOrNull?.contentOrNullStrict?.toIntOrNull() ?: 0
            if (url.isNotBlank() && (best == null || width >= best!!.first)) best = width to url
        }
        // Upgrade small avatars/thumbnails to a sharper variant when it's a ytimg URL.
        return best?.second?.upgradeThumbnailUrl()
    }

    private fun String.upgradeThumbnailUrl(): String {
        if (!contains("ytimg.com")) return this
        return replace(Regex("/(s\\d+|w\\d+-h\\d+)(-[^/]*)?/"), "/s720/")
            .let { if (it == this) replace(Regex("(/vi/[^/]+/)[^./]+"), "$1hq720") else it }
    }

    /** Depth-first search for the first object stored under [key]. */
    private fun JsonElement?.find(key: String): JsonElement? {
        when (this) {
            is JsonObject -> {
                get(key)?.let { return it }
                values.forEach { child -> child.find(key)?.let { return it } }
            }
            is JsonArray -> forEach { child -> child.find(key)?.let { return it } }
            else -> Unit
        }
        return null
    }

    /** Depth-first collection of every object stored under [key]. */
    private fun JsonElement?.findAll(key: String): List<JsonObject> {
        val out = mutableListOf<JsonObject>()
        when (this) {
            is JsonObject -> {
                (get(key) as? JsonObject)?.let { out += it }
                values.forEach { child -> out += child.findAll(key) }
            }
            is JsonArray -> forEach { child -> out += child.findAll(key) }
            else -> Unit
        }
        return out
    }

    /** Visits every (key, object) pair anywhere in the tree. */
    private fun walkAllRenderers(root: JsonElement?, onObject: (String, JsonObject) -> Unit) {
        when (root) {
            is JsonObject -> {
                root.forEach { (key, value) ->
                    if (value is JsonObject) {
                        onObject(key, value)
                    }
                    walkAllRenderers(value, onObject)
                }
            }
            is JsonArray -> root.forEach { walkAllRenderers(it, onObject) }
            else -> Unit
        }
    }

    /** Finds a browse-style continuation token anywhere in the element. */
    private fun JsonElement?.findContinuation(): String? {
        findAll("continuationItemRenderer").forEach { renderer ->
            val token = renderer.path("continuationEndpoint")
                .path("continuationCommand")
                .path("token")
                .jsonPrimitiveOrNull?.contentOrNullStrict
                ?: renderer.path("button")
                    .path("buttonRenderer")
                    .path("navigationEndpoint")
                    .path("continuationCommand")
                    .path("token")
                    .jsonPrimitiveOrNull?.contentOrNullStrict
            if (!token.isNullOrBlank()) return token
        }
        return null
    }

    /** "1:23" / "1:02:03" → seconds; null for live/upcoming/invalid. */
    fun String.parseDurationSeconds(): Int? {
        val clean = trim()
        if (clean.isEmpty() || clean.contains("live", ignoreCase = true) ||
            clean.contains("stream", ignoreCase = true) || clean.contains("upcoming", ignoreCase = true)
        ) {
            return null
        }
        val parts = clean.split(":")
        if (parts.any { it.isBlank() || it.any { c -> !c.isDigit() } }) return null
        return try {
            when (parts.size) {
                1 -> parts[0].toInt()
                2 -> parts[0].toInt() * 60 + parts[1].toInt()
                3 -> parts[0].toInt() * 3600 + parts[1].toInt() * 60 + parts[2].toInt()
                else -> null
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    /** Decodes the handful of HTML entities YouTube bakes into plain-text fields. */
    fun String.decodeHtmlEntities(): String {
        if (!contains('&')) return this
        var result = this
        result = result.replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
        // Numeric entities, decimal and hex.
        result = Regex("&#(\\d+);").replace(result) { m ->
            m.groupValues[1].toIntOrNull()?.toChar()?.toString() ?: m.value
        }
        result = Regex("&#x([0-9a-fA-F]+);").replace(result) { m ->
            m.groupValues[1].toIntOrNull(16)?.toChar()?.toString() ?: m.value
        }
        return result
    }

    /** Accepts UC… id, @handle, legacy name, or a full channel URL. */
    private suspend fun normalizeChannelId(input: String): String {
        val value = input.trim().substringBefore('?').trimEnd('/')
        return when {
            value.startsWith("UC") && value.length >= 20 -> value
            value.startsWith("@") -> resolveHandle(value)
            value.startsWith("http", ignoreCase = true) -> {
                val segment = value.substringAfterLast('/').substringBefore('?')
                when {
                    segment.startsWith("UC") && segment.length >= 20 -> segment
                    segment.startsWith("@") -> resolveHandle(segment)
                    else -> resolveHandle(value.substringAfterLast('/').let { if (it.isBlank()) value else it })
                }
            }
            else -> resolveHandle("@$value")
        }
    }

    private suspend fun resolveHandle(handleOrUrl: String): String {
        val url = when {
            handleOrUrl.startsWith("http", ignoreCase = true) -> handleOrUrl
            handleOrUrl.startsWith("@") -> "$ORIGIN/$handleOrUrl"
            else -> "$ORIGIN/@${handleOrUrl}"
        }
        return runCatching {
            val response = withRetry {
                httpClient.post("navigation/resolve_url") {
                    ytHeaders()
                    setBody(
                        buildJsonObject {
                            put("context", json.encodeToJsonElement(com.music.innertube.models.Context.serializer(), context()))
                            put("url", JsonPrimitive(url))
                        }
                    )
                }.body<JsonElement>().also { captureVisitor(it) }
            }
            response.find("browseId")?.jsonPrimitiveOrNull?.contentOrNullStrict
                ?.takeIf { it.startsWith("UC") }
        }.getOrNull() ?: url.substringAfterLast('/').substringBefore('?')
    }

    private fun parseVideoIdFromUrl(url: String): String? {
        Regex("(?:v=|/shorts/|/embed/|/live/|youtu\\.be/)([A-Za-z0-9_-]{11})").find(url)?.let {
            return it.groupValues[1]
        }
        return null
    }

    /**
     * Runs [block] on the IO dispatcher, letting a throw become a failed [Result].
     * Cancellation is never swallowed, so scope cancellation and stale-request
     * handling keep working through this wrapper.
     */
    private suspend fun <T> withNetworkResult(block: suspend () -> Result<T>): Result<T> =
        try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
