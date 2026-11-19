package com.convx.music.playback

import android.content.Context
import android.util.LruCache
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.SpotifyCanvasLookup
import java.io.IOException
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

internal data class CanvasDecision(val source: CanvasSource, val artwork: CanvasArtwork) {
    val primaryUrl get() = secureCanvasUrl(artwork.animated)
    val fallbackUrl get() = secureCanvasUrl(artwork.videoUrl)?.takeIf { it != primaryUrl }
}

internal fun secureCanvasUrl(url: String?): String? = url?.trim()?.toHttpUrlOrNull()
    ?.takeIf { it.isHttps && it.username.isEmpty() && it.password.isEmpty() }?.toString()

internal class CanvasResolver(private val context: Context) {
    private data class CacheKey(val mediaId: String, val source: CanvasSource)
    private data class CacheEntry(val decision: CanvasDecision, val expiresAt: Long)

    private val cache = LruCache<CacheKey, CacheEntry>(32)

    suspend fun resolve(metadata: MediaMetadata, source: CanvasSource): CanvasDecision? = withContext(Dispatchers.IO) {
        val title = metadata.title
        val artist = metadata.artists.firstOrNull()?.name.orEmpty()
        if (title.isBlank() || artist.isBlank()) return@withContext null
        val album = metadata.album?.title.orEmpty()
        val storefront = Locale.getDefault().country.lowercase(Locale.ROOT).takeIf { it.length == 2 } ?: "us"
        for (provider in if (source == CanvasSource.AUTO) autoOrder else listOf(source)) {
            val key = CacheKey(metadata.id, provider)
            val now = android.os.SystemClock.elapsedRealtime()
            cache.get(key)?.takeIf { it.expiresAt > now }?.let { return@withContext it.decision }
            val artwork = try {
                withTimeoutOrNull(4_000L) {
                    when (provider) {
                        CanvasSource.SPOTIFY -> SpotifyCanvasLookup.getBySongArtist(context, title, artist)
                        CanvasSource.APPLE_MUSIC -> AppleMusicCanvasProvider.getBySongArtist(title, artist, album, storefront)
                        CanvasSource.TIDAL -> TidalCanvasProvider.getBySongArtist(title, artist, album)
                        CanvasSource.CONVX -> ConvxCanvasProvider.getBySongArtist(title, artist)
                        CanvasSource.AUTO -> null
                    }
                }
            } catch (_: IOException) {
                null
            } ?: continue
            val decision = CanvasDecision(provider, artwork)
            if (decision.primaryUrl == null && decision.fallbackUrl == null) continue
            cache.put(key, CacheEntry(decision, android.os.SystemClock.elapsedRealtime() + 300_000L))
            return@withContext decision
        }
        null
    }

    fun invalidate(mediaId: String) {
        CanvasSource.entries.forEach { cache.remove(CacheKey(mediaId, it)) }
    }

    private companion object {
        val autoOrder = listOf(CanvasSource.SPOTIFY, CanvasSource.APPLE_MUSIC, CanvasSource.TIDAL, CanvasSource.CONVX)
    }
}
