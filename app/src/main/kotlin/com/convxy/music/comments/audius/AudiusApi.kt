/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.audius

import com.convxy.music.comments.CommentSource
import com.convxy.music.comments.CommentSourcePreferences
import com.convxy.music.comments.runCatchingSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** A non-2xx or transport-level failure, carrying just enough to explain itself in the UI. */
class AudiusApiException(
    message: String,
    val statusCode: Int? = null,
) : Exception(message)

/**
 * Client for Audius's public discovery API.
 *
 * **No credentials, no registration, no account.** That is the entire reason this source exists and
 * why it is first in the default priority: SoundCloud's timed comments are the cleanest data available
 * but sit behind a registered application, which in practice means behind a paid tier, which means most
 * people never see the feature at all. Audius asks only that a caller identify itself with `app_name`,
 * and hands back comments that already carry the commenter's playback position in `track_timestamp_s`.
 *
 * The API is served by a pool of community-run discovery nodes rather than one endpoint, which shapes
 * two decisions here:
 *
 *  - [resolveHost] fetches `https://api.audius.co` once and caches a host from the list it returns.
 *    That document is the documented entry point, and it is also usable directly as a host, so a failed
 *    discovery falls back to it rather than failing the feature.
 *  - A transport error or a 5xx from a node is treated as *that node* being unhealthy, not as Audius
 *    being down. [getJson] drops the cached host and retries once against a freshly resolved one. One
 *    retry, then it gives up — a flaky node should cost a second, not a loop.
 *
 * Scope is narrow on purpose: track search, single-track lookup, and a track's comments. Nothing here
 * writes, reacts, reposts or streams audio. Convxy reads comments; Audius is not a playback source.
 *
 * Nothing throws out of a public method. Every entry point returns a [Result] so an Audius outage is a
 * message in a bottom sheet and never an exception on the player's coroutine.
 */
@Singleton
class AudiusApi @Inject constructor(
    private val preferences: CommentSourcePreferences,
) {

    /**
     * Cheap, local, no I/O — the gate the repository checks before spending anything.
     *
     * Note what is NOT here: no network probe, no host resolution. The interface contract is that
     * `isConfigured` costs nothing on the hot path, and for a credential-free source the honest answer
     * is simply whether the user left it switched on.
     */
    fun isConfigured(): Boolean =
        preferences.isFeatureEnabled && preferences.isSourceEnabled(CommentSource.AUDIUS)

    // ── endpoints ──────────────────────────────────────────────────────────

    /** `GET /v1/tracks/search?query=…` — how a Convxy track gets mapped onto an Audius one. */
    suspend fun searchTracks(query: String, limit: Int = SEARCH_LIMIT): Result<List<AudiusTrack>> {
        if (query.isBlank()) return Result.failure(AudiusApiException("empty search query"))
        val path = "/v1/tracks/search?query=${urlEncode(query)}&limit=${limit.coerceIn(1, MAX_LIMIT)}"
        return getJson(path).map { AudiusCommentParser.parseTracks(it) }
    }

    /** `GET /v1/tracks/{id}` — for the caller that already holds an Audius track id. */
    suspend fun track(id: String): Result<AudiusTrack?> {
        if (id.isBlank()) return Result.failure(AudiusApiException("empty track id"))
        return getJson("/v1/tracks/${urlEncode(id)}").map { AudiusCommentParser.parseTrack(it) }
    }

    /**
     * `GET /v1/tracks/{id}/comments` as one page, with the commenters inlined by the API under
     * `related.users`.
     *
     * A single bounded request rather than a paging loop: `limit` tops out at 100 here, and
     * [com.convxy.music.comments.CommentTimeline.MAX_COMMENTS] is 400 but the UI cannot usefully render
     * more than a couple of hundred ticks on a seek bar. Paging to exhaustion would spend several
     * requests per track change to fetch comments the marker cap would then discard.
     */
    suspend fun comments(trackId: String): Result<AudiusCommentParser.CommentPage> {
        if (trackId.isBlank()) return Result.failure(AudiusApiException("empty track id"))
        return getJson("/v1/tracks/${urlEncode(trackId)}/comments?limit=$MAX_LIMIT")
            .map { AudiusCommentParser.parseCommentPage(it) }
    }

    /** Drops the cached node so the next call re-resolves. Public so a settings change can force it. */
    fun invalidateHost() {
        host = null
    }

    // ── transport ──────────────────────────────────────────────────────────

    private val client by lazy {
        OkHttpClient.Builder()
            // Short, for the same reason SoundCloud's are: comments are an enhancement over a player
            // that must keep working, so a slow node costs a "try again" state, not a hang behind the
            // now-playing screen.
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    /**
     * Fetches [path], retrying once against a different discovery node when the failure looks like
     * that node's problem rather than Audius's.
     *
     * The retry decision lives here rather than inside the request block so there is exactly one retry
     * path. Doing it inline would mean a 5xx recursing from one place and an [java.io.IOException] from
     * another, and two independent retry sites are how a request ends up being attempted four times.
     */
    private suspend fun getJson(path: String, allowNodeSwitch: Boolean = true): Result<String> {
        val attempt = fetch(path)
        if (allowNodeSwitch && attempt.isNodeProblem()) {
            Timber.tag(TAG).d("Audius node unhealthy, re-resolving: ${attempt.exceptionOrNull()?.message}")
            invalidateHost()
            return getJson(path, allowNodeSwitch = false)
        }
        return attempt
    }

    /**
     * True for the failures that say "this node" rather than "this request": a transport error, or a
     * 5xx. A 404 or a 429 is NOT — those would come back the same from any node, and retrying them
     * would just spend the caller's rate limit twice to reach the same answer.
     */
    private fun Result<String>.isNodeProblem(): Boolean {
        val cause = exceptionOrNull() ?: return false
        return cause is java.io.IOException ||
            (cause is AudiusApiException && cause.statusCode?.let { it in 500..599 } == true)
    }

    private suspend fun fetch(path: String): Result<String> = withContext(Dispatchers.IO) {
        val base = resolveHost().getOrElse { return@withContext Result.failure(it) }
        runCatchingSuspend {
            val request = Request.Builder()
                .url(withAppName(base + path))
                .header("Accept", "application/json; charset=utf-8")
                .header("User-Agent", USER_AGENT)
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                when {
                    response.isSuccessful -> text
                    response.code == 429 ->
                        throw AudiusApiException("rate limited by Audius", 429)
                    response.code == 404 ->
                        throw AudiusApiException("Audius has no such track", 404)
                    else ->
                        throw AudiusApiException("Audius returned ${response.code}", response.code)
                }
            }
        }.onFailure { Timber.tag(TAG).d(it, "Audius request failed: $path") }
    }

    /**
     * A usable host, resolved once per process and cached until [invalidateHost] clears it.
     *
     * `https://api.audius.co` returns the list of live discovery nodes; picking from it rather than
     * always calling the entry point is what spreads load across the network and survives one node
     * going away. If discovery itself fails, the entry point is used directly — it serves the API too,
     * so a broken discovery document must not take the whole feature with it.
     */
    private suspend fun resolveHost(): Result<String> {
        host?.let { return Result.success(it) }
        return withContext(Dispatchers.IO) {
            val discovered = runCatchingSuspend {
                val request = Request.Builder()
                    .url(withAppName(DISCOVERY_URL))
                    .header("Accept", "application/json; charset=utf-8")
                    .header("User-Agent", USER_AGENT)
                    .get()
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        emptyList<String>()
                    } else {
                        AudiusCommentParser.parseHosts(response.body?.string().orEmpty())
                    }
                }
            }.getOrDefault(emptyList())

            // Random pick so a fleet of Convxy installs does not all hammer the first-listed node.
            val chosen = discovered.randomOrNull()
                ?.trimEnd('/')
                ?.takeIf { it.startsWith("http") }
                ?: DISCOVERY_URL
            host = chosen
            Result.success(chosen)
        }
    }

    /** Audius asks callers to identify themselves; it is appended to every request. */
    private fun withAppName(url: String): String =
        url + (if ('?' in url) '&' else '?') + "app_name=$APP_NAME"

    private fun urlEncode(raw: String) =
        java.net.URLEncoder.encode(raw, "UTF-8").replace("+", "%20")

    @Volatile
    private var host: String? = null

    companion object {
        private const val TAG = "AudiusApi"
        private const val DISCOVERY_URL = "https://api.audius.co"
        private const val APP_NAME = "Convxy"
        private const val USER_AGENT = "Convxy/1.0 (timestamped comments; Audius public API)"

        private const val SEARCH_LIMIT = 20
        private const val MAX_LIMIT = 100 // the API's documented ceiling
    }
}
