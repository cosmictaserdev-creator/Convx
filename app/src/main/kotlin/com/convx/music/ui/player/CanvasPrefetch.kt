package com.convx.music.ui.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.convx.music.LocalDownloadUtil
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CanvasTrack
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.secureCanvasUrl
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

private const val PREFETCH_IDLE_MS = 2_000L
private const val PREFETCH_TIMEOUT_MS = 14_000L
private const val PREFETCH_LOOKUP_TIMEOUT_MS = 1_500L
private const val PREFETCH_MAX_BYTES = 256 * 1024L
private const val PREFETCH_CACHE_MAX_BYTES = 6 * 1024 * 1024L
private const val PREFETCH_CACHE_PREFIX = "warm:"

private val prefetchCacheWriteMutex = Mutex()

private val publicCanvasSources = listOf(CanvasSource.APPLE_MUSIC, CanvasSource.TIDAL, CanvasSource.CONVX)

@Composable
internal fun CanvasPrefetch(
    connection: PlayerConnection,
    upcoming: List<MediaMetadata>,
    interaction: State<Long>,
    isPlaying: Boolean,
    enabled: Boolean,
) {
    val (source) = rememberEnumPreference(CanvasSourceKey, CanvasSource.AUTO)
    val appDataSaver by rememberPreference(DataSaverEnabledKey, false)
    if (!enabled || appDataSaver || source == CanvasSource.SPOTIFY || upcoming.isEmpty()) return

    val context = LocalContext.current.applicationContext
    val cache = LocalDownloadUtil.current.get().downloadCache
    val policy by rememberCanvasPrefetchPolicy(context)
    LaunchedEffect(connection, upcoming, interaction, isPlaying, source, policy, cache) {
        val initialPolicy = policy
        if (initialPolicy.dataSaver || !initialPolicy.validated || (!initialPolicy.interactive && !isPlaying)) {
            return@LaunchedEffect
        }
        val providers = if (source == CanvasSource.AUTO) publicCanvasSources else listOf(source)
        var prefetchedTrack: CanvasTrack? = null
        val prefetchedIds = mutableSetOf<String>()

        combine(connection.canvasTrack, snapshotFlow { interaction.value }, ::Pair)
            .collectLatest { (track, lastInteraction) ->
                if (track.mediaId == null) return@collectLatest
                if (prefetchedTrack != track) {
                    prefetchedTrack = track
                    prefetchedIds.clear()
                }

                if (initialPolicy.interactive) {
                    val ageMs = (SystemClock.uptimeMillis() - lastInteraction).coerceAtLeast(0L)
                    delay((PREFETCH_IDLE_MS - ageMs).coerceAtLeast(0L))
                }

                val targets = upcoming.asSequence()
                    .filter { it.id != track.mediaId }
                    .distinctBy { it.id }
                    .take(3)
                withTimeoutOrNull(PREFETCH_TIMEOUT_MS) {
                    for (metadata in targets) {
                        if (connection.canvasTrack.value != track || policy != initialPolicy) break
                        if (metadata.id in prefetchedIds) continue

                        val decision = withContext(Dispatchers.IO) {
                            providers.firstNotNullOfOrNull { provider ->
                                withTimeoutOrNull(PREFETCH_LOOKUP_TIMEOUT_MS) {
                                    connection.canvasResolver.resolve(metadata, provider)
                                }
                            }
                        }
                        if (connection.canvasTrack.value != track || policy != initialPolicy) break
                        if (initialPolicy.unmetered && !initialPolicy.batterySaver && decision?.source == CanvasSource.TIDAL) {
                            val url = decision.primaryUrl ?: decision.fallbackUrl
                            if (url != null) {
                                prefetchCacheWriteMutex.withLock {
                                    warmPublicCanvas(context, cache, metadata.id, url)
                                }
                            }
                        }
                        prefetchedIds.add(metadata.id)
                    }
                }
            }
    }
}

/** Signed URLs and provider fallbacks must use different cache entries for the same song. */
internal fun canvasPlaybackCacheKey(mediaId: String, url: String): String {
    val canonicalUrl = secureCanvasUrl(url) ?: url
    val hash = MessageDigest.getInstance("SHA-256").digest(canonicalUrl.encodeToByteArray())
    return buildString {
        append(mediaId)
        append("#canvas-")
        for (byte in hash) {
            append("0123456789abcdef"[(byte.toInt() ushr 4) and 15])
            append("0123456789abcdef"[byte.toInt() and 15])
        }
    }
}

internal fun canvasWarmCacheKey(mediaId: String, url: String): String =
    PREFETCH_CACHE_PREFIX + canvasPlaybackCacheKey(mediaId, url)

private data class CanvasPrefetchPolicy(
    val validated: Boolean,
    val unmetered: Boolean,
    val interactive: Boolean,
    val dataSaver: Boolean,
    val batterySaver: Boolean,
)

@Composable
private fun rememberCanvasPrefetchPolicy(context: Context): State<CanvasPrefetchPolicy> {
    val connectivity = remember(context) { context.getSystemService(ConnectivityManager::class.java) }
    val power = remember(context) { context.getSystemService(PowerManager::class.java) }
    val policy = remember(context) { mutableStateOf(readCanvasPrefetchPolicy(connectivity, power)) }
    DisposableEffect(context, connectivity, power) {
        fun refresh() {
            policy.value = readCanvasPrefetchPolicy(connectivity, power)
        }

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = refresh()
            override fun onLost(network: Network) = refresh()
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) = refresh()
        }
        val monitoring = try {
            connectivity?.registerDefaultNetworkCallback(networkCallback, Handler(Looper.getMainLooper()))
            connectivity != null
        } catch (_: SecurityException) {
            false
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = refresh()
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            addAction(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        refresh()
        onDispose {
            if (monitoring) connectivity?.unregisterNetworkCallback(networkCallback)
            context.unregisterReceiver(receiver)
        }
    }
    return policy
}

private fun readCanvasPrefetchPolicy(connectivity: ConnectivityManager?, power: PowerManager?): CanvasPrefetchPolicy {
    val interactive = power?.isInteractive ?: true
    val batterySaver = power?.isPowerSaveMode ?: true
    return try {
        val capabilities = connectivity?.activeNetwork?.let { connectivity.getNetworkCapabilities(it) }
        CanvasPrefetchPolicy(
            validated = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true,
            unmetered = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true,
            interactive = interactive,
            dataSaver = connectivity?.restrictBackgroundStatus != ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED,
            batterySaver = batterySaver,
        )
    } catch (_: SecurityException) {
        CanvasPrefetchPolicy(
            validated = false,
            unmetered = false,
            interactive = interactive,
            dataSaver = true,
            batterySaver = batterySaver,
        )
    }
}

private val prefetchRange = Regex("bytes ([0-9]+)-([0-9]+)/([0-9]+)")
private val prefetchClient = OkHttpClient.Builder()
    .followRedirects(false)
    .followSslRedirects(false)
    .connectTimeout(2, TimeUnit.SECONDS)
    .readTimeout(2, TimeUnit.SECONDS)
    .callTimeout(3, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = chain.request()
        if (!isPublicTidalMp4(request.url.toString())) throw IOException("Invalid canvas prefetch address")
        val response = chain.proceed(request)
        val range = prefetchRange.matchEntire(response.header("Content-Range").orEmpty())
        val start = range?.groupValues?.get(1)?.toLongOrNull()
        val end = range?.groupValues?.get(2)?.toLongOrNull()
        val total = range?.groupValues?.get(3)?.toLongOrNull()
        val requestedRange = request.header("Range")?.removePrefix("bytes=")
        val requestStart = requestedRange?.substringBefore('-')?.toLongOrNull()
        val requestEnd = requestedRange?.substringAfter('-')?.toLongOrNull()
        val responseLength = response.body?.contentLength()
        val encoding = response.header("Content-Encoding")
        val rawResponse = encoding == null || encoding.equals("identity", ignoreCase = true)
        val partial = response.code == 206 && start != null && end != null && total != null &&
            requestEnd != null && start == requestStart && end in start until PREFETCH_MAX_BYTES &&
            end == minOf(total - 1, requestEnd) && responseLength == end - start + 1
        val smallFull = response.code == 200 && requestStart == 0L &&
            responseLength != null && responseLength in 1..PREFETCH_MAX_BYTES
        if (!rawResponse || (!partial && !smallFull)) {
            response.close()
            throw IOException("Canvas prefetch requires a bounded response")
        }
        response
    }
    .build()

private fun isPublicTidalMp4(url: String): Boolean {
    val parsed = url.toHttpUrlOrNull() ?: return false
    return parsed.isHttps && parsed.host == "resources.tidal.com" && parsed.port == 443 &&
        parsed.username.isEmpty() && parsed.password.isEmpty() &&
        parsed.encodedQuery == null && parsed.encodedPath.startsWith("/videos/") &&
        parsed.encodedPath.endsWith(".mp4", ignoreCase = true)
}

private suspend fun warmPublicCanvas(context: Context, cache: Cache, mediaId: String, url: String) = coroutineScope {
    if (!isPublicTidalMp4(url)) return@coroutineScope
    val key = canvasWarmCacheKey(mediaId, url)
    val permanentKey = canvasPlaybackCacheKey(mediaId, url)
    val requestScope = currentCoroutineContext()
    val activeCall = AtomicReference<Call?>()
    val cancelCall = launch(start = CoroutineStart.UNDISPATCHED) {
        try {
            awaitCancellation()
        } finally {
            activeCall.getAndSet(null)?.cancel()
        }
    }

    try {
        runInterruptible(Dispatchers.IO) {
            if (cache.getCachedLength(permanentKey, 0, PREFETCH_MAX_BYTES) > 0L) return@runInterruptible
            if (cache.getCachedLength(key, 0, PREFETCH_MAX_BYTES) > 0L) return@runInterruptible
            requestScope.ensureActive()
            if (!reserveWarmCacheSpace(cache, key)) return@runInterruptible

            val callFactory = Call.Factory { request ->
                val livePolicy = readCanvasPrefetchPolicy(
                    context.getSystemService(ConnectivityManager::class.java),
                    context.getSystemService(PowerManager::class.java),
                )
                if (!livePolicy.validated || !livePolicy.unmetered || livePolicy.dataSaver || livePolicy.batterySaver) {
                    throw IOException("Current network does not allow canvas byte prefetch")
                }
                val call = prefetchClient.newCall(request)
                activeCall.getAndSet(call)?.cancel()
                requestScope.ensureActive()
                call
            }
            val dataSource = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(callFactory))
                .createDataSource()
            try {
                val spec = DataSpec.Builder()
                    .setUri(Uri.parse(url))
                    .setKey(key)
                    .setLength(PREFETCH_MAX_BYTES)
                    .build()
                dataSource.open(spec)
                val buffer = ByteArray(16 * 1024)
                var remaining = PREFETCH_MAX_BYTES.toInt()
                while (remaining > 0) {
                    requestScope.ensureActive()
                    val read = dataSource.read(buffer, 0, minOf(buffer.size, remaining))
                    if (read <= 0) break
                    remaining -= read
                }
            } finally {
                dataSource.close()
            }
        }
    } catch (_: IOException) {
        requestScope.ensureActive()
    } finally {
        cancelCall.cancel()
    }
}

private data class WarmCacheResource(val key: String, val bytes: Long, val lastTouch: Long)

private fun reserveWarmCacheSpace(cache: Cache, requestedKey: String): Boolean {
    val resources = cache.keys.mapNotNull { key ->
        if (!key.startsWith(PREFETCH_CACHE_PREFIX)) return@mapNotNull null
        val spans = cache.getCachedSpans(key)
        if (spans.isEmpty()) return@mapNotNull null
        WarmCacheResource(key, spans.sumOf { it.length }, spans.maxOf { it.lastTouchTimestamp })
    }
    val limitBeforeWrite = PREFETCH_CACHE_MAX_BYTES - PREFETCH_MAX_BYTES
    var retainedBytes = resources.sumOf { it.bytes }
    if (retainedBytes <= limitBeforeWrite) return true

    for (resource in resources.sortedBy { it.lastTouch }) {
        if (resource.key == requestedKey) continue
        cache.removeResource(resource.key)
        retainedBytes -= resource.bytes
        if (retainedBytes <= limitBeforeWrite) return true
    }
    return false
}


internal fun canvasReadCacheKey(cache: androidx.media3.datasource.cache.Cache, mediaId: String, url: String): String {
    val key = canvasPlaybackCacheKey(mediaId, url)
    if (cache.isCached(key, 0L, 1L)) return key
    val warm = canvasWarmCacheKey(mediaId, url)
    return if (cache.isCached(warm, 0L, 1L)) warm else key
}
