/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import com.convxy.music.comments.runCatchingSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** A non-2xx or transport-level failure, carrying just enough to explain itself in the UI. */
class SoundCloudApiException(
    message: String,
    val statusCode: Int? = null,
) : Exception(message)

/**
 * Thin, official-API-only client for `api.soundcloud.com`.
 *
 * Scope is deliberately narrow — track search, single-track lookup, and a track's comments — because
 * those three are all timestamped comments need. Nothing here writes, likes or uploads.
 *
 * Authentication is the documented OAuth 2.1 client-credentials grant: `client_id` + `client_secret`
 * are exchanged for a short-lived access token that is then sent as `Authorization: OAuth <token>`.
 * A bare `client_id` query parameter is NOT used; SoundCloud removed support for it in July 2021.
 *
 * What this class deliberately does not do:
 *  - talk to `api-v2.soundcloud.com`, the internal endpoint the web player uses. It needs a
 *    `client_id` scraped out of SoundCloud's own JavaScript bundles, which changes without notice,
 *    is not licensed for third-party use, and would make this feature break silently and often.
 *  - retry aggressively. One re-auth on a 401, then give up. Rate limits are per-credential and
 *    hammering them is how a credential gets revoked.
 *  - throw. Every entry point returns a [Result] so a SoundCloud outage is a message in a bottom
 *    sheet, never an exception on the player's coroutine.
 */
@Singleton
class SoundCloudApi @Inject constructor(
    private val credentials: SoundCloudCredentials,
) {

    /** Cheap, local, no I/O: the gate the repository checks before it spends anything. */
    fun isConfigured(): Boolean =
        credentials.isEnabled && credentials.resolve().isUsable

    // ── endpoints ──────────────────────────────────────────────────────────

    /** `GET /tracks?q=…` — the search used to map a Convxy track onto a SoundCloud one. */
    suspend fun searchTracks(query: String, limit: Int = SEARCH_LIMIT): Result<List<ScTrack>> {
        if (query.isBlank()) return Result.failure(SoundCloudApiException("empty search query"))
        val url = buildString {
            append(BASE_URL).append("/tracks?q=")
            append(urlEncode(query))
            append("&limit=").append(limit.coerceIn(1, MAX_LIMIT))
        }
        return getJson(url).map { SoundCloudCommentParser.parseTracks(it) }
    }

    /**
     * Looks up a track the caller already has a handle on, so no search-and-match is needed.
     *
     * A bare numeric id or a `soundcloud:tracks:…` urn goes to `/tracks/{urn}`; a web permalink goes
     * to `/resolve?url=…`, which is the documented way to turn one into a resource.
     */
    suspend fun track(idOrUrl: String): Result<ScTrack?> {
        if (idOrUrl.isBlank()) return Result.failure(SoundCloudApiException("empty track id"))
        val url = if (idOrUrl.startsWith("http://") || idOrUrl.startsWith("https://")) {
            "$BASE_URL/resolve?url=${urlEncode(idOrUrl)}"
        } else {
            "$BASE_URL/tracks/${urlEncode(idOrUrl)}"
        }
        return getJson(url).map { SoundCloudCommentParser.parseTrack(it) }
    }

    /**
     * `GET /tracks/{id}/comments`, following `next_href` up to [maxPages] pages.
     *
     * Bounded on purpose. A track with ten thousand comments would otherwise page until the client
     * gave up; [com.convxy.music.comments.CommentTimeline.MAX_COMMENTS] caps what is kept anyway, so
     * three pages of 200 is far more than the UI can use and far less than a runaway loop.
     */
    suspend fun comments(trackId: String, maxPages: Int = MAX_COMMENT_PAGES): Result<List<ScComment>> {
        if (trackId.isBlank()) return Result.failure(SoundCloudApiException("empty track id"))
        val out = ArrayList<ScComment>()
        var url: String? = "$BASE_URL/tracks/${urlEncode(trackId)}/comments?limit=$MAX_LIMIT&linked_partitioning=true"
        var pages = 0
        while (url != null && pages < maxPages) {
            val body = getJson(url).getOrElse { return Result.failure(it) }
            out += SoundCloudCommentParser.parseComments(body)
            url = SoundCloudCommentParser.nextHref(body)?.let { absolutize(it) }
            pages++
        }
        return Result.success(out)
    }

    /** Drops any cached token, so the next call re-authenticates. Used after a credential change. */
    fun invalidateToken() {
        token = null
    }

    // ── transport ──────────────────────────────────────────────────────────

    private val client by lazy {
        OkHttpClient.Builder()
            // Short. Comments are an enhancement over a player that must keep working; a slow
            // SoundCloud must cost a "try again" state, not a hang behind the now-playing screen.
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    private suspend fun getJson(url: String, allowReauth: Boolean = true): Result<String> =
        withContext(Dispatchers.IO) {
            val auth = tokenOrNull().getOrElse { return@withContext Result.failure(it) }
            runCatchingSuspend {
                val request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json; charset=utf-8")
                    .header("Authorization", "OAuth $auth")
                    .header("User-Agent", USER_AGENT)
                    .get()
                    .build()
                client.newCall(request).execute().use { response ->
                    val text = response.body?.string().orEmpty()
                    when {
                        response.isSuccessful -> text
                        // Token expired or was revoked mid-session: mint a new one and retry once.
                        response.code == 401 && allowReauth -> {
                            invalidateToken()
                            return@runCatchingSuspend getJson(url, allowReauth = false).getOrThrow()
                        }
                        response.code == 429 ->
                            throw SoundCloudApiException("rate limited by SoundCloud", 429)
                        else ->
                            throw SoundCloudApiException(
                                "SoundCloud returned ${response.code}", response.code
                            )
                    }
                }
            }.onFailure { Timber.tag(TAG).d(it, "SoundCloud request failed: $url") }
        }

    // ── token ──────────────────────────────────────────────────────────────

    private class Token(val value: String, val expiresAtEpochMs: Long)

    @Volatile
    private var token: Token? = null
    private val tokenMutex = Mutex()

    private suspend fun tokenOrNull(): Result<String> {
        val config = credentials.resolve()
        if (!config.isUsable) {
            return Result.failure(SoundCloudApiException("SoundCloud is not configured"))
        }
        // A user-supplied token is used as-is. It cannot be refreshed, so a 401 on it is terminal —
        // which the single-retry limit above already handles.
        config.accessToken?.let { return Result.success(it) }

        token?.let { cached ->
            if (cached.expiresAtEpochMs > System.currentTimeMillis()) return Result.success(cached.value)
        }

        return tokenMutex.withLock {
            // Re-check inside the lock: several tracks can request comments at once and only the
            // first should pay for the exchange.
            token?.let { cached ->
                if (cached.expiresAtEpochMs > System.currentTimeMillis()) return@withLock Result.success(cached.value)
            }
            exchangeToken(config)
        }
    }

    private suspend fun exchangeToken(config: SoundCloudCredentials.Config): Result<String> =
        withContext(Dispatchers.IO) {
            if (!config.canExchangeTokens) {
                return@withContext Result.failure(
                    SoundCloudApiException("SoundCloud client credentials are incomplete")
                )
            }
            val basic = Base64.getEncoder().encodeToString(
                "${config.clientId}:${config.clientSecret}".toByteArray(Charsets.UTF_8)
            )
            // Documented OAuth 2.1 endpoint first …
            val primary = runCatching {
                post(
                    url = TOKEN_URL,
                    basicAuth = basic,
                    form = FormBody.Builder().add("grant_type", "client_credentials").build(),
                )
            }
            // … then the older api.soundcloud.com/oauth2/token path, which the 2021 security notice
            // describes and some registered applications are still issued against.
            val response = primary.getOrNull()?.takeIf { it.accessToken != null }
                ?: runCatching {
                    post(
                        url = LEGACY_TOKEN_URL,
                        basicAuth = null,
                        form = FormBody.Builder()
                            .add("client_id", config.clientId)
                            .add("client_secret", config.clientSecret)
                            .add("grant_type", "client_credentials")
                            .build(),
                    )
                }.getOrNull()

            val accessToken = response?.accessToken
            if (accessToken.isNullOrBlank()) {
                val reason = response?.errorDescription ?: response?.error ?: "token exchange failed"
                return@withContext Result.failure(SoundCloudApiException(reason))
            }
            // Expire a minute early: a token that dies mid-request costs a retry, and the skew removes
            // most of those for free.
            val lifetimeMs = ((response.expiresIn ?: DEFAULT_TOKEN_LIFETIME_S) - TOKEN_SKEW_S) * 1000L
            token = Token(accessToken, System.currentTimeMillis() + lifetimeMs.coerceAtLeast(30_000L))
            Result.success(accessToken)
    }

    private fun post(url: String, basicAuth: String?, form: FormBody): ScTokenResponse? {
        val builder = Request.Builder()
            .url(url)
            .header("Accept", "application/json; charset=utf-8")
            .header("User-Agent", USER_AGENT)
            .post(form)
        if (basicAuth != null) builder.header("Authorization", "Basic $basicAuth")
        return client.newCall(builder.build()).execute().use { response ->
            if (!response.isSuccessful) return@use null
            val text = response.body?.string().orEmpty()
            if (text.isBlank()) null else runCatching { JSON.decodeFromString(ScTokenResponse.serializer(), text) }.getOrNull()
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────

    /** `next_href` is normally absolute; if a deployment ever returns a path, root it at the API. */
    private fun absolutize(href: String): String =
        if (href.startsWith("http://") || href.startsWith("https://")) href else BASE_URL + href

    private fun urlEncode(raw: String) = java.net.URLEncoder.encode(raw, "UTF-8").replace("+", "%20")

    companion object {
        private const val TAG = "SoundCloudApi"
        const val BASE_URL = "https://api.soundcloud.com"
        private const val TOKEN_URL = "https://secure.soundcloud.com/oauth/token"
        private const val LEGACY_TOKEN_URL = "https://api.soundcloud.com/oauth2/token"
        private const val USER_AGENT = "Convxy/1.0 (timestamped comments; official SoundCloud API)"

        private const val SEARCH_LIMIT = 20
        private const val MAX_LIMIT = 200 // the API's documented ceiling
        private const val MAX_COMMENT_PAGES = 3
        private const val DEFAULT_TOKEN_LIFETIME_S = 3600L
        private const val TOKEN_SKEW_S = 60L

        private val JSON = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            coerceInputValues = true
        }
    }
}
