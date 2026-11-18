package com.convx.music.utils

import android.content.Context
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.constants.SpotifySessionKey
import com.music.spotify.SpotifyAuth
import com.music.spotify.SpotifyCanvas
import com.music.spotify.models.SpotifyInternalToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Spotify Canvas for the logged-in Spotify session (Settings > Spotify). Returns null
 * when not connected or on any failure, so callers can fall through to other sources.
 */
object SpotifyCanvasLookup {
    // Only the cookie fields of the stored SpotifySession are needed here.
    @Serializable
    private data class StoredCookies(val spDc: String = "", val spKey: String? = null)

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var cached: Pair<String, SpotifyInternalToken>? = null

    suspend fun getBySongArtist(context: Context, song: String, artist: String): CanvasArtwork? {
        if (song.isBlank() || artist.isBlank()) return null
        return try {
            for (attempt in 0..1) {
                val token = token(context) ?: return null
                try {
                    return SpotifyCanvas.getBySongArtist(song, artist, token.accessToken, token.clientId)
                        ?.let { CanvasArtwork(name = song, artist = artist, animated = it) }
                } catch (e: SpotifyCanvas.RequestException) {
                    if (e.statusCode != 401 || attempt > 0) throw e
                    mutex.withLock { cached = null }
                }
            }
            null
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Timber.tag("SpotifyCanvas").w("Canvas lookup failed")
            null
        }
    }

    private suspend fun token(context: Context): SpotifyInternalToken? = mutex.withLock {
        val raw = context.dataStore.data.first()[SpotifySessionKey]?.takeIf(String::isNotBlank) ?: return@withLock null
        val cookies = json.decodeFromString<StoredCookies>(raw).takeIf { it.spDc.isNotBlank() } ?: return@withLock null
        cached?.takeIf { (spDc, t) ->
            spDc == cookies.spDc && t.clientId.isNotBlank() &&
                t.accessTokenExpirationTimestampMs > System.currentTimeMillis() + 30_000
        }?.let { return@withLock it.second }
        SpotifyAuth.fetchAccessToken(cookies.spDc, cookies.spKey.orEmpty()).getOrThrow()
            .also { cached = cookies.spDc to it }
    }
}
