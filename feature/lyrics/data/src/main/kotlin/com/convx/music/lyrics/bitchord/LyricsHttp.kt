/** Adapted from BitChord (kushagrasinghx/BitChord), GPL-3.0. See NOTICE. */
package com.convx.music.lyrics.bitchord

import kotlinx.serialization.json.Json

/**
 * Shared plumbing for the lyric providers.
 *
 * They are all raced against each other by [LyricsRepository], so a provider
 * that hangs holds up the whole lookup. [LYRICS_TIMEOUT_SECONDS] is deliberately
 * far shorter than [Http]'s stream-oriented timeouts: a lyric that arrives
 * after the second chorus is of no use to anyone, and the fallbacks behind it
 * are the better answer.
 */
private const val LYRICS_TIMEOUT_MS = 6_000L
private const val LYRICS_CONNECT_TIMEOUT_MS = 3_000L

// PaxSenix's authenticated routes often have to query an upstream catalogue
// before answering. Keep the fast deadline for the ordinary providers, but
// match PaxSenix's own client timeout here so valid requests are not discarded
// while its backend is still resolving a track.
private const val AUTHENTICATED_TIMEOUT_MS = 15_000L
private const val AUTHENTICATED_CONNECT_TIMEOUT_MS = 10_000L

internal const val LYRICS_AGENT = "Convx (https://github.com/cosmictaserdev-creator/Convx)"

val lyricsJson = Json { ignoreUnknownKeys = true; isLenient = true }

/** Body of a successful GET, or null for any failure at all. */
internal suspend fun lyricsGet(url: String): String? = providerGet(
    url,
    mapOf("User-Agent" to LYRICS_AGENT, "Accept" to "application/json"),
    LYRICS_TIMEOUT_MS,
    LYRICS_CONNECT_TIMEOUT_MS,
)

/** Body of an authenticated provider GET without service-specific browser headers. */
internal suspend fun lyricsGetBearer(url: String, bearer: String): String? {
    if (bearer.isBlank()) return null
    return providerGet(
        url,
        mapOf(
            "User-Agent" to LYRICS_AGENT,
            "Accept" to "application/json, text/plain, */*",
            "Authorization" to "Bearer $bearer",
        ),
        AUTHENTICATED_TIMEOUT_MS,
        AUTHENTICATED_CONNECT_TIMEOUT_MS,
    )
}

/**
 * [lyricsGet], with a bearer token and the headers Apple's own web player
 * sends alongside one — `amp-api.music.apple.com` answers a token with no
 * `Origin` at all the same way it answers a wrong one, with a 403.
 */
internal suspend fun lyricsGetAuthorized(url: String, bearer: String): String? = providerGet(
    url,
    mapOf(
        "User-Agent" to LYRICS_AGENT,
        "Accept" to "application/json",
        "Authorization" to "Bearer $bearer",
        "Origin" to "https://music.apple.com",
        "Referer" to "https://music.apple.com/",
    ),
    LYRICS_TIMEOUT_MS,
    LYRICS_CONNECT_TIMEOUT_MS,
)
