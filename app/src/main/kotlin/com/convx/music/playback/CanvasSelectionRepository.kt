package com.convx.music.playback

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.media3.datasource.cache.SimpleCache
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.CachedCanvasSegments
import com.convx.music.canvas.CanvasLookupCoordinator
import com.convx.music.canvas.CanvasProviderPreferences
import com.convx.music.canvas.StoredCanvasDecision
import com.convx.music.canvas.usableManualSelection
import com.convx.music.canvas.manualCanvasSelection
import com.convx.music.canvas.CanvasProvider
import com.convx.music.canvas.canvasCacheKey
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.canvas.selectCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasProviderOrderKey
import com.convx.music.constants.CanvasDisabledProvidersKey
import com.convx.music.constants.CanvasFirstAvailableKey
import com.convx.music.constants.CanvasPriorityWaitSecondsKey
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.utils.SpotifyCanvasLookup
import com.convx.music.utils.dataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import java.util.Locale

/** Android provider adapters for the common lookup coordinator. */
internal object CanvasSelectionRepository {
    private val manualSelections = com.convx.music.canvas.ManualCanvasSelections()
    val selectionRevision: kotlinx.coroutines.flow.StateFlow<Long> = manualSelections.revision
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }
    private val coordinator = CanvasLookupCoordinator<Request, Decision>(
        scope = scope,
        nowMillis = System::currentTimeMillis,
        positiveTtlMillis = 30 * 60 * 1000L,
        negativeTtlMillis = 3 * 24 * 60 * 60 * 1000L,
    ) { request -> lookup(request) }

    suspend fun resolve(
        context: Context,
        mediaId: String,
        title: String,
        artist: String,
        album: String?,
        source: CanvasSource,
    ): CanvasArtwork? {
        if (mediaId.isBlank() || title.isBlank() || artist.isBlank()) return null
        val prefs = context.dataStore.data.first()
        val policy = CanvasProviderPreferences.fromStored(
            order = prefs[CanvasProviderOrderKey].orEmpty(),
            disabled = prefs[CanvasDisabledProvidersKey].orEmpty(),
            firstAvailable = prefs[CanvasFirstAvailableKey] ?: false,
            waitSeconds = prefs[CanvasPriorityWaitSecondsKey] ?: 2,
        )
        val request = Request(context.applicationContext, mediaId, title, artist, album.orEmpty(), source, policy)
        val enabledNames = policy.enabled.mapTo(mutableSetOf()) { it.name }
        return try {
            val manual = manualSelections.resolve(mediaId) { withContext(Dispatchers.IO) {
                context.getSharedPreferences("canvas_manual_selections", Context.MODE_PRIVATE).getString(mediaId, null)?.let {
                    runCatching { json.decodeFromString(StoredCanvasDecision.serializer(), it) }.getOrNull()
                }
            } }
            val manualDecision = manual?.usableManualSelection(policy)?.let { selected ->
                CanvasSource.entries.firstOrNull { it.name == selected.source }?.let { Decision(it, selected.artwork) }
            }
            val cached = coordinator.findCached { previous, decision ->
                previous.mediaId == mediaId &&
                    decision.source.name in enabledNames &&
                    (source == CanvasSource.AUTO || source == decision.source)
            }
            val decision = manualDecision ?: cached ?: coordinator.resolve(request)
            if (decision != null && cached == null && manualDecision == null) {
                withContext(Dispatchers.IO) { runCatching { writeStored(request.context, mediaId, decision) } }
            }
            // Verification can parse playlists, so prepare the offline rendition away from the UI thread.
            decision?.artwork?.preferredAnimationUrl?.takeIf { !it.substringBefore('?').endsWith(".mp4", true) }?.let { url ->
                withContext(Dispatchers.IO) {
                    val koin = GlobalContext.get()
                    val downloads = koin.get<SimpleCache>(named("downloadCache"))
                    val playback = koin.get<SimpleCache>(named("playerCache"))
                    HlsCanvasCache.completedSelection(mediaId, url, downloads)
                        ?: HlsCanvasCache.completedSelection(mediaId, url, playback)
                }
            }
            decision?.artwork
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
    }

    internal suspend fun invalidate(context: Context, mediaId: String) {
        coordinator.invalidateWhere { it.mediaId == mediaId }
        withContext(Dispatchers.IO) {
            context.getSharedPreferences("canvas_decisions", Context.MODE_PRIVATE).edit().remove(mediaId).apply()
        }
    }

    /** Explicit selection is scoped to one song; remembering it never changes global provider order. */
    suspend fun selectManually(context: Context, mediaId: String, source: CanvasSource, artwork: CanvasArtwork, remember: Boolean) {
        require(mediaId.isNotBlank() && source != CanvasSource.AUTO && !artwork.preferredAnimationUrl.isNullOrBlank())
        val decision = manualCanvasSelection(CanvasProvider.valueOf(source.name), artwork)
        manualSelections.select(mediaId, decision) { withContext(Dispatchers.IO) {
            val editor = context.getSharedPreferences("canvas_manual_selections", Context.MODE_PRIVATE).edit()
            if (remember) editor.putString(mediaId, json.encodeToString(StoredCanvasDecision.serializer(), decision))
            else editor.remove(mediaId)
            editor.apply()
        } }
    }

    suspend fun clearManualSelection(context: Context, mediaId: String) {
        manualSelections.clear(mediaId) { withContext(Dispatchers.IO) {
            context.getSharedPreferences("canvas_manual_selections", Context.MODE_PRIVATE).edit().remove(mediaId).apply()
        } }
    }

    /** Explicit searches bypass automatic song matches and never change provider preferences. */
    suspend fun searchManually(context: Context, title: String, artist: String, album: String?, source: CanvasSource): CanvasArtwork? =
        withContext(Dispatchers.IO) {
            require(source != CanvasSource.AUTO)
            if (title.isBlank() || artist.isBlank()) return@withContext null
            val prefs = context.dataStore.data.first()
            val disabled = prefs[CanvasDisabledProvidersKey].orEmpty().split(',').map(String::trim)
            if (source.name in disabled) return@withContext null
            val connectivity = context.getSystemService(ConnectivityManager::class.java)
            val capabilities = connectivity?.activeNetwork?.let { connectivity.getNetworkCapabilities(it) }
            if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) != true) return@withContext null
            val storefront = Locale.getDefault().country.lowercase(Locale.ROOT).takeIf { it.length == 2 } ?: "us"
            lookupProvider(context.applicationContext, title.trim(), artist.trim(), album.orEmpty(), source, storefront)
                ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
        }

    private suspend fun lookupProvider(context: Context, title: String, artist: String, album: String,
        source: CanvasSource, storefront: String): CanvasArtwork? {
        currentCoroutineContext().ensureActive()
        val artwork = when (source) {
        CanvasSource.SPOTIFY -> SpotifyCanvasLookup.getBySongArtist(context, title, artist)
        CanvasSource.APPLE_MUSIC -> AppleMusicCanvasProvider.getBySongArtist(title, artist, album, storefront)
        CanvasSource.TIDAL -> TidalCanvasProvider.getBySongArtist(title, artist, album)
        CanvasSource.CONVX -> ConvxCanvasProvider.getBySongArtist(title, artist)
        CanvasSource.AUTO -> null
        }
        // Some provider adapters turn cancellation into a null result. Do not let
        // a cancelled search publish a fallback decision or persist a stale choice.
        currentCoroutineContext().ensureActive()
        return artwork
    }

    private suspend fun lookup(request: Request): Decision? {
        // The coordinator shares this disk check between all surfaces on a cold start.
        val stored = readStored(request.context, request.mediaId)
        val storedSource = stored?.source?.let { name -> CanvasSource.entries.firstOrNull { it.name == name } }
        if (stored != null && storedSource != null &&
            request.policy.enabled.any { it.name == stored.source } &&
            (request.source == CanvasSource.AUTO || request.source == storedSource)) {
            val local = runCatching { findCompleteLocalUrl(request.context, request.mediaId, stored.artwork) }.getOrNull()
            if (local != null) return Decision(storedSource, stored.artwork.copy(animated = local, videoUrl = local))
        }
        val connectivity = request.context.getSystemService(ConnectivityManager::class.java)
        val network = connectivity?.activeNetwork
        val capabilities = network?.let { connectivity.getNetworkCapabilities(it) }
        if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) != true) {
            throw java.io.IOException("No validated network for canvas lookup")
        }
        val storefront = Locale.getDefault().country.lowercase(Locale.ROOT).takeIf { it.length == 2 } ?: "us"
        val enabled = request.policy.enabled.mapNotNull { provider ->
            CanvasSource.entries.firstOrNull { it.name == provider.name }
        }
        val providers = if (request.source == CanvasSource.AUTO) enabled else enabled.filter { it == request.source }
        return selectCanvasProvider(
            providers = providers.map { source ->
                suspend {
                    val artwork = lookupProvider(request.context, request.title, request.artist, request.album, source, storefront)
                    artwork?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }?.let { Decision(source, it) }
                }
            },
            mode = request.policy.mode,
            priorityWaitMillis = request.policy.priorityWaitMillis,
        )
    }

    private data class Decision(val source: CanvasSource, val artwork: CanvasArtwork)

    private fun readStored(context: Context, mediaId: String): StoredCanvasDecision? = runCatching {
        val encoded = context.getSharedPreferences("canvas_decisions", Context.MODE_PRIVATE).getString(mediaId, null)
        encoded?.let { json.decodeFromString(StoredCanvasDecision.serializer(), it) }
    }.getOrNull()

    private fun writeStored(context: Context, mediaId: String, decision: Decision) {
        val encoded = json.encodeToString(StoredCanvasDecision.serializer(), StoredCanvasDecision(decision.source.name, decision.artwork))
        context.getSharedPreferences("canvas_decisions", Context.MODE_PRIVATE).edit().putString(mediaId, encoded).apply()
    }

    private fun findCompleteLocalUrl(context: Context, mediaId: String, artwork: CanvasArtwork): String? {
        val koin = GlobalContext.get()
        val playerCache = koin.get<SimpleCache>(named("playerCache"))
        val downloadCache = koin.get<SimpleCache>(named("downloadCache"))
        return listOfNotNull(artwork.preferredAnimationUrl, artwork.videoUrl, artwork.animated)
            .distinct()
            .firstOrNull { url ->
                when {
                    url.substringBefore('?').endsWith(".mp4", ignoreCase = true) ->
                        playerCache.isCachedToEnd(canvasCacheKey(mediaId, url), 0) ||
                            downloadCache.isCachedToEnd(canvasCacheKey(mediaId, url), 0)
                    else ->
                        HlsCanvasCache.completedSelection(mediaId, url, downloadCache) != null ||
                            HlsCanvasCache.completedSelection(mediaId, url, playerCache) != null
                }
            }
    }

    internal fun markHlsDownloaded(context: Context, mediaId: String, url: String, cache: SimpleCache) {
        val prefix = "$mediaId#canvas-v2:"
        val entries = cache.keys.asSequence().filter { it.startsWith(prefix) }.associateWith { key ->
            cache.getCachedSpans(key).sumOf { it.length }
        }.filterValues { it > 0 }
        val manifestKey = canvasCacheKey(mediaId, url)
        if (manifestKey !in entries) return
        val record = CachedCanvasSegments(manifestKey, entries)
        val encoded = json.encodeToString(CachedCanvasSegments.serializer(), record)
        context.getSharedPreferences("canvas_hls_downloads", Context.MODE_PRIVATE).edit().putString(mediaId, encoded).apply()
    }

    internal fun clearDownloadedHls(context: Context, mediaId: String) {
        context.getSharedPreferences("canvas_hls_downloads", Context.MODE_PRIVATE).edit().remove(mediaId).apply()
    }

    internal fun hasCompleteHls(context: Context, mediaId: String, url: String, cache: SimpleCache): Boolean {
        val encoded = context.getSharedPreferences("canvas_hls_downloads", Context.MODE_PRIVATE).getString(mediaId, null) ?: return false
        val record = runCatching { json.decodeFromString(CachedCanvasSegments.serializer(), encoded) }.getOrNull() ?: return false
        return record.manifestKey == canvasCacheKey(mediaId, url) && record.entries.isNotEmpty() &&
            record.entries.all { (key, length) -> length > 0 && cache.getCachedSpans(key).sumOf { it.length } >= length }
    }

    /** Checks the manifest and every referenced segment using a source that cannot access the network. */
    internal fun hasCompleteCachedHls(mediaId: String, url: String, cache: SimpleCache): Boolean {
        val key = canvasCacheKey(mediaId, url)
        if (!cache.isCachedToEnd(key, 0)) return false
        return runCatching {
            val source = androidx.media3.datasource.cache.CacheDataSource.Factory()
                .setCache(cache)
                .setCacheKeyFactory(canvasSegmentKeyFactory(mediaId))
                .setUpstreamDataSourceFactory(androidx.media3.datasource.PlaceholderDataSource.FACTORY)
                .setCacheWriteDataSinkFactory(null)
            val item = androidx.media3.common.MediaItem.Builder().setUri(url)
                .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8).build()
            androidx.media3.exoplayer.hls.offline.HlsDownloader(item, source).download(null)
            true
        }.getOrDefault(false)
    }

    private class Request(
        val context: Context,
        val mediaId: String,
        val title: String,
        val artist: String,
        val album: String,
        val source: CanvasSource,
        val policy: CanvasProviderPreferences,
    ) {
        // Reordering should not replace an already selected canvas; disabling a provider must.
        private val enabledKey = policy.enabled.map { it.name }.sorted()
        override fun equals(other: Any?): Boolean =
            other is Request && mediaId == other.mediaId && source == other.source && enabledKey == other.enabledKey

        override fun hashCode(): Int = 31 * (31 * mediaId.hashCode() + source.hashCode()) + enabledKey.hashCode()
    }
}
