/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.net.ConnectivityManager

import androidx.media3.common.PlaybackException
import com.music.innertube.NewPipeExtractor
import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_CREATOR
import com.convx.music.utils.BotDetectionMitigator
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_43_32
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_61_48
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_65_10
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_NO_AUTH
import com.music.innertube.models.YouTubeClient.Companion.IOS
import com.music.innertube.models.YouTubeClient.Companion.IPADOS
import com.music.innertube.models.YouTubeClient.Companion.MOBILE
import com.music.innertube.models.YouTubeClient.Companion.TVHTML5
import com.music.innertube.models.YouTubeClient.Companion.TVHTML5_SIMPLY_EMBEDDED_PLAYER
import com.music.innertube.models.YouTubeClient.Companion.VISIONOS
import com.music.innertube.models.YouTubeClient.Companion.WEB
import com.music.innertube.models.YouTubeClient.Companion.WEB_CREATOR
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.response.PlayerResponse
import com.convx.music.constants.AudioQuality
import com.convx.music.constants.EnableSaavnStreamingKey
import com.convx.music.constants.ForceSelectedQualityKey
import com.convx.music.constants.SaavnFallbackToYouTubeKey
import com.convx.music.constants.EnableTidalStreamingKey
import com.convx.music.constants.EnabledModulesKey
import com.convx.music.constants.ModuleSourcesKey
import com.convx.music.constants.ModuleSettingsKey
import com.convx.music.constants.SaavnAudioQuality
import com.convx.music.constants.SaavnAudioQualityKey
import com.convx.music.constants.TidalInstanceUrlKey
import com.convx.music.constants.TidalQuality
import com.convx.music.constants.TidalQualityKey
import com.convx.music.utils.tidal.TidalService
import com.convx.music.utils.tidal.TidalTrack
import com.music.spine.ModuleManager
import org.json.JSONArray
import com.convx.music.utils.cipher.CipherDeobfuscator
import com.convx.music.utils.YTPlayerUtils.MAIN_CLIENT
import com.convx.music.utils.YTPlayerUtils.STREAM_FALLBACK_CLIENTS
import com.convx.music.utils.YTPlayerUtils.validateStatus
import com.convx.music.utils.potoken.PoTokenGenerator
import com.convx.music.utils.potoken.PoTokenResult
import com.convx.music.utils.sabr.EjsNTransformSolver
import com.convx.music.utils.PlaybackLogLevel
import com.convx.music.utils.PlaybackLogManager
import com.music.innertube.models.IpVersion
import com.music.innertube.models.WatchEndpoint
import com.music.jiosaavn.SaavnService
import okhttp3.Dns
import okhttp3.OkHttpClient
import timber.log.Timber
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull

/** Higher first. Ranks Tidal search-result candidates so the matcher below tries
 *  the best-quality candidate first instead of Tidal's own (unordered w.r.t.
 *  quality) search result order. */
private fun tidalQualityRank(audioQuality: String?): Int = when (audioQuality?.uppercase(java.util.Locale.US)) {
    "HI_RES_LOSSLESS", "HI_RES" -> 3
    "LOSSLESS" -> 2
    "HIGH" -> 1
    else -> 0
}

private fun isMatchingTidalCandidate(
    candidate: TidalTrack,
    wantedTitle: String,
    wantedArtists: List<String>,
    wantedDuration: Int?,
): Boolean {
    val candidateTitle = candidate.title.lowercase(java.util.Locale.US)
    val candidateArtists = candidate.artistNames.map { it.lowercase(java.util.Locale.US) }
    val titleMatches = candidateTitle.contains(wantedTitle) || wantedTitle.contains(candidateTitle)
    val artistMatches = wantedArtists.isEmpty() || wantedArtists.any { wantedArtist ->
        candidateArtists.any { candidateArtist ->
            candidateArtist.contains(wantedArtist) || wantedArtist.contains(candidateArtist)
        }
    }

    // Require an exact title match when duration is unavailable, avoiding loose substring matches.
    val durationMatches = if (wantedDuration != null && candidate.duration != null) {
        kotlin.math.abs(candidate.duration - wantedDuration) <= 10
    } else {
        candidateTitle == wantedTitle
    }

    return titleMatches && artistMatches && durationMatches
}

private fun isAgeRestrictedStatus(status: String): Boolean =
    status == "AGE_CHECK_REQUIRED" ||
        status == "AGE_VERIFICATION_REQUIRED" ||
        status == "CONTENT_CHECK_REQUIRED"

private val SEARCH_QUERY_WHITESPACE = Regex("\\s+")
private val YOUTUBE_TOPIC_SUFFIX = Regex("(?i)\\s*-\\s*topic\\b")
private val YOUTUBE_VEVO_SUFFIX = Regex("(?i)\\s*VEVO\\b")
private val YOUTUBE_OFFICIAL_TAG = Regex("(?i)\\(\\s*official\\s*(video|music\\s*video|audio)?\\s*\\)")
private val YOUTUBE_LYRICS_TAG = Regex("(?i)\\(\\s*lyrics?\\s*\\)")
private val YOUTUBE_BRACKETED_TAG = Regex("\\[.*?\\]")
private val MUSIC_NOTES = Regex("[\\u266A\\u266B]")

private fun normalizeSearchQuery(query: String): String =
    query.replace("&", " ")
        .replace(",", " ")
        .replace(SEARCH_QUERY_WHITESPACE, " ")
        .trim()

private fun resolveArtists(songArtists: List<String>?, author: String): List<String> {
    if (!songArtists.isNullOrEmpty()) return songArtists
    val fallback = author
        .replace(YOUTUBE_TOPIC_SUFFIX, "")
        .replace(YOUTUBE_VEVO_SUFFIX, "")
        .trim()
    return if (fallback.isBlank()) emptyList() else listOf(fallback)
}

private fun cleanYouTubeTitle(title: String, artists: List<String>): String {
    var cleaned = title
    for (artist in artists) {
        val artistPrefix = Regex("(?i)^\\s*${Regex.escape(artist)}\\s*[-–—]\\s*")
        cleaned = cleaned.replace(artistPrefix, "")
    }
    return cleaned
        .replace(YOUTUBE_OFFICIAL_TAG, "")
        .replace(YOUTUBE_LYRICS_TAG, "")
        .replace(YOUTUBE_BRACKETED_TAG, "")
        .replace(MUSIC_NOTES, "")
        .replace(SEARCH_QUERY_WHITESPACE, " ")
        .trim()
}

private fun parseJsonStringArray(value: String): List<String> = runCatching {
    val array = JSONArray(value)
    (0 until array.length()).map { array.getString(it) }
}.getOrDefault(emptyList())

private fun parseModuleSettings(value: String): Map<String, Map<String, String>> = runCatching {
    val settings = org.json.JSONObject(value)
    settings.keys().asSequence().associateWith { moduleId ->
        val moduleSettings = settings.optJSONObject(moduleId)
        if (moduleSettings == null) {
            emptyMap()
        } else {
            moduleSettings.keys().asSequence().associateWith { key -> moduleSettings.optString(key, "") }
        }
    }
}.getOrDefault(emptyMap())

object YTPlayerUtils {
    data class VideoStream(val mediaId: String, val url: String, val mimeType: String, val width: Int, val height: Int, val contentLength: Long, val videoId: String = mediaId)
    private val resolvedVideoStreams = android.util.LruCache<String, Pair<Long, VideoStream>>(32)
    private const val VIDEO_STREAM_CACHE_MS = 10 * 60 * 1000L

    /** Resolve a video track separately; music playback remains owned by MusicService. */
    suspend fun videoStream(videoId: String): VideoStream? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        if (!videoId.matches(Regex("[A-Za-z0-9_-]{11}"))) return@withContext null
        val now = android.os.SystemClock.elapsedRealtime()
        resolvedVideoStreams.get(videoId)?.let { (cachedAt, stream) ->
            if (now - cachedAt < VIDEO_STREAM_CACHE_MS) return@withContext stream
            resolvedVideoStreams.remove(videoId)
        }
        for (client in listOf(VISIONOS, IOS, ANDROID_VR_1_61_48, ANDROID_VR_1_43_32, MAIN_CLIENT)) {
            kotlinx.coroutines.currentCoroutineContext().ensureActive()
            try {
                val response = YouTube.player(videoId, null, client, null, null).getOrNull() ?: continue
                if (response.playabilityStatus.status != "OK") continue
                if (response.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_ATV") return@withContext null
                val candidates = (response.streamingData?.adaptiveFormats.orEmpty() + response.streamingData?.formats.orEmpty())
                    .filter { it.width != null && it.height != null && it.mimeType.startsWith("video/") }
                    .sortedWith(compareByDescending<PlayerResponse.StreamingData.Format> { it.mimeType.contains("avc1") }
                        .thenByDescending { (it.height ?: 0) <= 1080 }
                        .thenByDescending { (it.fps ?: 30) <= 30 }
                        .thenByDescending { it.height ?: 0 }.thenBy { it.bitrate })
                for (format in candidates.take(3)) {
                    // Never use findUrlOrNull's audio-only fallback for a video surface.
                    val rawUrl = format.url ?: (format.signatureCipher ?: format.cipher)?.let {
                        CipherDeobfuscator.deobfuscateStreamUrl(it, videoId)
                    } ?: continue
                    val url = EjsNTransformSolver.transformNParamInUrl(rawUrl)
                    val probe = okhttp3.Request.Builder().url(url)
                        .header("User-Agent", YouTubeClient.USER_AGENT_WEB)
                        .header("Range", "bytes=0-131071").build()
                    val length = httpClient.newCall(probe).execute().use { result ->
                        val prefix = result.body.bytes()
                        val boxType = if (prefix.size >= 8) String(prefix, 4, 4, Charsets.US_ASCII) else null
                        val mp4 = boxType == "ftyp" || boxType == "styp"
                        val webm = prefix.size >= 4 &&
                            prefix[0] == 0x1a.toByte() && prefix[1] == 0x45.toByte() &&
                            prefix[2] == 0xdf.toByte() && prefix[3] == 0xa3.toByte()
                        val contentRange = result.header("Content-Range")
                        if (result.code == 206 && contentRange?.startsWith("bytes 0-") == true && (mp4 || webm)) {
                            contentRange.substringAfterLast('/')?.toLongOrNull()
                        } else null
                    }?.takeIf { it > 0L } ?: continue
                    Timber.tag("VideoStream").d("Playable video: %s, %sp", client.clientName, format.height)
                    val stream = VideoStream(videoId, url,
                        format.mimeType.substringBefore(';'), format.width!!, format.height!!, length)
                    resolvedVideoStreams.put(videoId, android.os.SystemClock.elapsedRealtime() to stream)
                    return@withContext stream
                }
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Timber.tag("VideoStream").d(error, "Video unavailable from %s", client.clientName)
            }
        }
        null
    }

    private const val logTag = "YTPlayerUtils"
    private const val TAG = "YTPlayerUtils"

    /**
     * Shared by stream resolution and audio downloads so they reuse the same
     * connection context, proxy, DNS rules, and request pool.
     */
    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .dns(object : Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                val addresses = Dns.SYSTEM.lookup(hostname)
                return when (YouTube.ipVersion) {
                    IpVersion.IPV4 -> addresses.filter { it is Inet4Address }.ifEmpty { addresses }
                    IpVersion.IPV6 -> addresses.filter { it is Inet6Address }.ifEmpty { addresses }
                    IpVersion.AUTO -> addresses
                }
            }
        })
        .proxySelector(object : ProxySelector() {
            override fun select(uri: URI?): List<Proxy> = listOfNotNull(YouTube.proxy ?: Proxy.NO_PROXY)
            override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
                Timber.tag(TAG).e(ioe, "Proxy connection failed for URI: $uri")
            }
        })
        .proxyAuthenticator { _, response ->
            YouTube.proxyAuth?.let { auth ->
                response.request.newBuilder()
                    .header("Proxy-Authorization", auth)
                    .build()
            } ?: response.request
        }
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        // 30 s, not 15 s: multi-megabyte audio chunk reads ride this client too
        // (DownloadUtil), and per-read timeouts bite slow networks mid-chunk.
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .dispatcher(okhttp3.Dispatcher().apply { maxRequestsPerHost = 16 })
        .connectionPool(okhttp3.ConnectionPool(16, 5, java.util.concurrent.TimeUnit.MINUTES))
        .build()

    private val poTokenGenerator = PoTokenGenerator()

    /** Detached so a slow metadata fetch outlives the playback call that started it. */
    private val metadataScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)

    /** How long a resolved stream waits for the authenticated metadata before playing without it. */
    private const val MetadataWaitMs = 2_500L

    /**
     * Client used for fast, low-latency stream resolution.
     * ANDROID_VR clients don't require PoToken and start instantly.
     * ANDROID_VR has loginSupported=false, so metadata like audioConfig and
     * playbackTracking must be supplemented from an authenticated client (WEB_REMIX)
     * when the user is logged in.
     */
    private val MAIN_CLIENT: YouTubeClient = ANDROID_VR_1_43_32

    /** Used when a player response omits streamingData.expiresInSeconds — 6h, YouTube's usual value. */
    private const val DEFAULT_STREAM_EXPIRY_SECONDS = 21600

    /**
     * Client used to fetch metadata (audioConfig, playbackTracking) when the user is
     * logged in. This ensures remote YouTube history is correctly updated.
     */
    private val METADATA_CLIENT: YouTubeClient = WEB_REMIX

    /**
     * Fallbacks tried when [MAIN_CLIENT] cannot produce a stream. IOS succeeded
     * on all five bot-flagged probe tracks, while other clients failed. VISIONOS
     * remains first at owner request; if its URL passes HEAD but cannot stream,
     * it costs one extra probe before IOS. IPADOS remains a fallback despite
     * failing all five probe tracks. ANDROID_VR remains primary because it is faster
     * and supports more formats when available.
     */
    private val STREAM_FALLBACK_CLIENTS: Array<YouTubeClient> = arrayOf(
        VISIONOS,
        IOS,
        ANDROID_VR_1_61_48,
        ANDROID_VR_1_65_10,
        WEB_REMIX,
        TVHTML5_SIMPLY_EMBEDDED_PLAYER,  // Try embedded player first for age-restricted content
        TVHTML5,
        ANDROID_CREATOR,
        IPADOS,
        ANDROID_VR_NO_AUTH,
        MOBILE,
        WEB,
        WEB_CREATOR
    )
    /**
     * Every client playback may try, in the order it tries them.
     *
     * Exposed for [YouTubeClientProbe] only. Deliberately derived from the same values the
     * player path uses, so a probe result always describes the chain that actually runs.
     */
    internal val allStreamClients: List<YouTubeClient>
        get() = listOf(MAIN_CLIENT) + STREAM_FALLBACK_CLIENTS

    data class PlaybackData(
        val audioConfig: PlayerResponse.PlayerConfig.AudioConfig?,
        val videoDetails: PlayerResponse.VideoDetails?,
        val playbackTracking: PlayerResponse.PlaybackTracking?,
        val format: PlayerResponse.StreamingData.Format,
        val streamUrl: String,
        val streamExpiresInSeconds: Int,
        val isSaavnStream: Boolean = false,
        val isTidalStream: Boolean = false,
        val isSpineStream: Boolean = false,
        val isDashStream: Boolean = false,
        val isHlsStream: Boolean = false,
    )
    /** Resolves the current song and player metadata concurrently for alternate providers. */
    private suspend fun resolvePlaybackMetadata(videoId: String, playlistId: String?) = coroutineScope {
        val nextDeferred = async {
            val nextResult = YouTube.next(WatchEndpoint(videoId = videoId)).getOrNull()
            nextResult?.items?.getOrNull(nextResult.currentIndex ?: 0)
                ?: nextResult?.items?.firstOrNull()
        }
        val metadataDeferred = async { playerResponseForMetadata(videoId, playlistId).getOrNull() }
        nextDeferred.await() to metadataDeferred.await()
    }

    /**
     * Custom player response intended to use for playback.
     * Stream URLs come from [MAIN_CLIENT] or [STREAM_FALLBACK_CLIENTS] for fast loading.
     * Metadata (audioConfig, playbackTracking) come from [METADATA_CLIENT] (WEB_REMIX)
     * when the user is logged in, to ensure remote history recording works correctly.
     */
    suspend fun playerResponseForPlayback(
        videoId: String,
        playlistId: String? = null,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        context: android.content.Context? = null,
        /** Whether lossless providers may be tried; downloads pass false. */
        allowLossless: Boolean = true,
        /** Bypasses lossless providers after repeated playback failures for this track. */
        forceStandardAudio: Boolean = false,
        requireYouTube: Boolean = false,
        preferAac: Boolean = false,
    ): Result<PlaybackData> {
        if (context != null) {
            // Honor selected quality only when Saavn is enabled.
            val forceSelectedQuality = context.dataStore.get(EnableSaavnStreamingKey, false) &&
                context.dataStore.get(ForceSelectedQualityKey, false)

            if (!forceStandardAudio && !requireYouTube) {
                Timber.tag(TAG).d("═══ SPINE INTERCEPT START ═══ videoId=$videoId")
                val enabledModulesJson = context.dataStore.get(EnabledModulesKey, "[]")
                val moduleSourcesJson = context.dataStore.get(ModuleSourcesKey, "[]")
                val moduleSettingsJson = context.dataStore.get(ModuleSettingsKey, "{}")
                Timber.tag(TAG).d("  enabledModules: $enabledModulesJson")
                Timber.tag(TAG).d("  moduleSources: $moduleSourcesJson")
                runCatching {
                    // Stored in the user's chosen priority order (drag-reorder in
                    // ModuleSourceScreen), lowest index tried first.
                    val enabledOrder = parseJsonStringArray(enabledModulesJson)
                    val enabledIds = enabledOrder.toSet()

                    val sourceUrls = parseJsonStringArray(moduleSourcesJson)
                    val allModuleSettings = parseModuleSettings(moduleSettingsJson)

                    Timber.tag(TAG).d("  Parsed enabledIds: $enabledIds")
                    Timber.tag(TAG).d("  Parsed sourceUrls: $sourceUrls")

                    if (enabledIds.isEmpty() || sourceUrls.isEmpty()) {
                        Timber.tag(TAG).d("  SPINE SKIP: enabledIds=${enabledIds.size} sourceUrls=${sourceUrls.size} — falling through")
                        Timber.tag(TAG).d("═══ SPINE INTERCEPT END (skipped) ═══")
                        return@runCatching null
                    }

                    Timber.tag(TAG).d("  Resolving YouTube metadata for search query...")

                    val metadata = withTimeoutOrNull(5_000L) {
                        resolvePlaybackMetadata(videoId, playlistId)
                    } ?: return@runCatching null
                    val (currentSong, meta) = metadata

                    val title = currentSong?.title ?: meta?.videoDetails?.title.orEmpty()
                    Timber.tag(TAG).d("  Resolved title=\"$title\" from ${if (currentSong != null) "YouTube.next" else "videoDetails"}")
                    if (title.isBlank()) return@runCatching null

                    val artistNames = resolveArtists(
                        currentSong?.artists?.map { it.name },
                        meta?.videoDetails?.author.orEmpty(),
                    )
                    val cleanTitle = cleanYouTubeTitle(title, artistNames)
                    val query = normalizeSearchQuery("$cleanTitle ${artistNames.joinToString(" ")}")

                    Timber.tag(TAG).d("  Search query: \"$query\" (artists=$artistNames)")

                    ModuleManager.cacheDir = context.filesDir.resolve("module-scripts")
                    val match = ModulePlaybackResolver.resolve(
                        sourceUrls = sourceUrls,
                        enabledOrder = enabledOrder,
                        settings = allModuleSettings,
                        title = cleanTitle,
                        artists = artistNames,
                        duration = currentSong?.duration,
                        query = query,
                        audioQuality = audioQuality,
                        allowLossless = allowLossless,
                        forceSelectedQuality = forceSelectedQuality,
                    ) ?: return@runCatching null

                    val playback = with(ModulePlaybackResolver) { match.playbackData(meta) }
                    return Result.success(playback)
                }.onFailure { e ->
                    if (e is kotlinx.coroutines.CancellationException) throw e
                    Timber.tag(TAG).e(e, "═══ SPINE INTERCEPT ERROR ═══")
                }
                Timber.tag(TAG).d("═══ SPINE INTERCEPT END ═══")
            } // !forceStandardAudio

            // Try Tidal lossless before Saavn when enabled.
            if (!forceStandardAudio && !requireYouTube && !forceSelectedQuality && allowLossless && context.dataStore.get(EnableTidalStreamingKey, false)) {
                Timber.tag(TAG).d("Lossless enabled — trying TIDAL for videoId=$videoId")
                val tidalResult = runCatching {
                    val (currentSong, meta) = resolvePlaybackMetadata(videoId, playlistId)

                    val title = currentSong?.title ?: meta?.videoDetails?.title.orEmpty()
                    if (title.isBlank()) return@runCatching null

                    val artistNames = resolveArtists(
                        currentSong?.artists?.map { it.name },
                        meta?.videoDetails?.author.orEmpty(),
                    )
                    val query = normalizeSearchQuery("$title ${artistNames.joinToString(" ")}")

                    val wantedTitle = title.lowercase(java.util.Locale.US)
                    val wantedArtists = artistNames.map { it.lowercase(java.util.Locale.US) }
                    val wantedDuration = currentSong?.duration
                    val customUrl = context.dataStore.get(TidalInstanceUrlKey, "").ifBlank { null }

                    val candidates = TidalService.search(query, customUrl)
                    // Rank matches by audio quality because search order is not quality order.
                    val matchingCandidates = candidates.filter { candidate ->
                        isMatchingTidalCandidate(
                            candidate = candidate,
                            wantedTitle = wantedTitle,
                            wantedArtists = wantedArtists,
                            wantedDuration = wantedDuration,
                        )
                    }.sortedByDescending { tidalQualityRank(it.audioQuality) }

                    if (matchingCandidates.isEmpty()) {
                        Timber.tag(TAG).d("TIDAL: no match for \"$query\" — falling back")
                        return@runCatching null
                    }

                    val quality = runCatching {
                        TidalQuality.valueOf(context.dataStore.get(TidalQualityKey, TidalQuality.LOSSLESS.name))
                    }.getOrDefault(TidalQuality.LOSSLESS)

                    var best: TidalTrack? = null
                    var streamUrl: String? = null
                    for (candidate in matchingCandidates) {
                        val url = TidalService.streamUrl(candidate.id, quality.toApiValue(), customUrl)
                        if (url != null) {
                            best = candidate
                            streamUrl = url
                            break
                        }
                    }
                    if (best == null || streamUrl == null) {
                        Timber.tag(TAG).d("TIDAL: no stream URL among ${matchingCandidates.size} match(es) for \"$query\" — falling back")
                        return@runCatching null
                    }

                    Timber.tag(TAG).i("Tidal: streaming FLAC \"${best.title}\" (id=${best.id}, ${quality.toApiValue()}) for videoId=$videoId")
                    PlaybackData(
                        audioConfig      = meta?.playerConfig?.audioConfig,
                        videoDetails     = meta?.videoDetails,
                        playbackTracking = meta?.playbackTracking,
                        format           = PlayerResponse.StreamingData.Format(
                            itag             = 9999,               // sentinel: lossless FLAC
                            url              = streamUrl,
                            // codecs= segment so the media-info sheet shows "flac", not the "mp3" default
                            mimeType         = "audio/flac; codecs=\"flac\"",
                            bitrate          = 1_411_000,          // nominal 16/44.1 stereo, for the badge
                            width            = null,
                            height           = null,
                            contentLength    = null,
                            quality          = quality.toApiValue(),
                            fps              = null,
                            qualityLabel     = null,
                            averageBitrate   = null,
                            audioQuality     = quality.toApiValue(),
                            approxDurationMs = best.duration?.let { (it * 1000L).toString() },
                            audioSampleRate  = 44100,
                            audioChannels    = 2,
                            loudnessDb       = null,
                            lastModified     = null,
                            signatureCipher  = null,
                            cipher           = null,
                            audioTrack       = null,
                        ),
                        streamUrl              = streamUrl,
                        streamExpiresInSeconds = 3600,
                        isTidalStream          = true,
                    )
                }.getOrNull()

                if (tidalResult != null) return Result.success(tidalResult)
                Timber.tag(TAG).d("TIDAL intercept failed or returned null — trying next source")
            }

            val saavnEnabled = !requireYouTube && context.dataStore.get(EnableSaavnStreamingKey, false)
            if (saavnEnabled && !preferAac) {
                Timber.tag(TAG).d("JioSaavn streaming enabled — trying Saavn for videoId=$videoId")
                val saavnResult = runCatching {
                    val (currentSong, meta) = resolvePlaybackMetadata(videoId, playlistId)

                    // Prefer the YouTube Music next() title; fall back to videoDetails title
                    val title = currentSong?.title
                        ?: meta?.videoDetails?.title.orEmpty()

                    // Use the proper artist list from SongItem (real artist names).
                    // Fall back to videoDetails.author with "- Topic" stripped.
                    val artistNames = resolveArtists(
                        currentSong?.artists?.map { it.name },
                        meta?.videoDetails?.author.orEmpty(),
                    )
                    val artist = artistNames.joinToString(", ")

                    if (title.isBlank()) return@runCatching null

                    Timber.tag(TAG).d("Saavn: resolved title=\"$title\" artists=$artistNames for videoId=$videoId")

                    val albumName = currentSong?.album?.name.orEmpty()
                    val wantedTitleLower = title.lowercase(java.util.Locale.US)
                    val wantedArtistsLower = artistNames.map { it.lowercase(java.util.Locale.US) }
                    val wantedDurationSec = currentSong?.duration

                    val primaryQuery = normalizeSearchQuery(if (albumName.isNotBlank()) {
                        "$albumName $title $artist"
                    } else {
                        "$title $artist"
                    })

                    val fallbackQuery = normalizeSearchQuery("$title $artist")

                    val bestSong = findBestSaavnMatch(
                        primaryQuery = primaryQuery,
                        fallbackQuery = fallbackQuery,
                        wantedTitle = wantedTitleLower,
                        wantedArtists = wantedArtistsLower,
                        wantedDurationSec = wantedDurationSec,
                    )

                    if (bestSong == null) {
                        Timber.tag(TAG).d("Saavn: no matching candidate found — falling back to YT")
                        return@runCatching null
                    }

                    Timber.tag(TAG).i("Saavn: matched \"${bestSong.title}\" (id=${bestSong.id}, album=\"${bestSong.moreInfo.album}\")")

                    // Treat the selected quality as a minimum; Saavn returns the available rendition.
                    val qualityKey = context.dataStore.get(SaavnAudioQualityKey, SaavnAudioQuality.QUALITY_320.name)
                    val minQuality = runCatching { SaavnAudioQuality.valueOf(qualityKey) }
                        .getOrDefault(SaavnAudioQuality.QUALITY_320)
                    val minKbps = when (minQuality) {
                        SaavnAudioQuality.QUALITY_320 -> 320
                        SaavnAudioQuality.QUALITY_160 -> 160
                        SaavnAudioQuality.QUALITY_96  -> 96
                    }

                    val stream = SaavnService.getStreamUrl(bestSong.id)
                    if (stream == null || stream.url.isBlank()) {
                        Timber.tag(TAG).d("Saavn: no stream URL for songId=${bestSong.id} — falling back to YT")
                        return@runCatching null
                    }
                    val streamKbps = stream.kbps
                    if (streamKbps != null && streamKbps < minKbps) {
                        Timber.tag(TAG).d("Saavn: only ${streamKbps}kbps available for songId=${bestSong.id}, below the ${minKbps}kbps floor — falling back to YT")
                        return@runCatching null
                    }
                    val streamUrl = stream.url
                    // Unknown bitrate (no recognisable rung in the CDN URL) is
                    // reported at the requested floor rather than left blank —
                    // best-effort labeling only, doesn't affect playback.
                    val deliveredKbps = stream.kbps ?: minKbps
                    val (itag, qualityLabel) = when {
                        deliveredKbps >= 320 -> 141 to "320kbps"
                        deliveredKbps >= 160 -> 140 to "160kbps"
                        else                 -> 139 to "96kbps"
                    }

                    // Optimization: Skip synchronous HTTP HEAD request to get content length during playback.
                    // ExoPlayer parses the content length automatically from HTTP GET response headers on buffer.
                    val contentLength: Long? = null

                    Timber.tag(TAG).i("Saavn: streaming from JioSaavn (${deliveredKbps}kbps) for videoId=$videoId")
                    // Reuse the initial YouTube metadata to avoid another request and
                    // preserve playback tracking, audio configuration, and video details.
                    PlaybackData(
                        audioConfig      = meta?.playerConfig?.audioConfig,
                        videoDetails     = meta?.videoDetails,
                        playbackTracking = meta?.playbackTracking,
                        format           = PlayerResponse.StreamingData.Format(
                            itag             = itag,
                            url              = streamUrl,
                            // JioSaavn delivers AAC-LC audio inside a regular MP4 container
                            // (e.g. https://aac.saavncdn.com/.../{id}_320.mp4)
                            mimeType         = "audio/mp4; codecs=\"mp4a.40.2\"",
                            bitrate          = deliveredKbps * 1_000,
                            width            = null,
                            height           = null,
                            contentLength    = contentLength,
                            quality          = qualityLabel,
                            fps              = null,
                            qualityLabel     = null,
                            averageBitrate   = null,
                            audioQuality     = qualityLabel,
                            approxDurationMs = null,
                            audioSampleRate  = null,
                            audioChannels    = null,
                            loudnessDb       = null,
                            lastModified     = null,
                            signatureCipher  = null,
                            cipher           = null,
                            audioTrack       = null,
                        ),
                        streamUrl              = streamUrl,
                        streamExpiresInSeconds = 3600,
                        isSaavnStream          = true,   // ← mark as Saavn so downloads skip YT range trick
                    )
                }.getOrNull()

                if (saavnResult != null) {
                    return Result.success(saavnResult)
                }
                if (!context.dataStore.get(SaavnFallbackToYouTubeKey, true)) {
                    Timber.tag(TAG).d("Saavn intercept failed and YouTube fallback is off — failing playback")
                    return Result.failure(IOException("No matching track found on JioSaavn"))
                }
                // Any exception or null → fall through to YouTube below
                Timber.tag(TAG).d("Saavn intercept failed or returned null — falling back to YouTube")
            }
        }

        val firstAttempt = resolvePlaybackData(videoId, playlistId, audioQuality, connectivityManager, preferAac)

        // A geo-restricted video fails the same way on a retry — rotating a
        // guest session or re-attempting as-is can't fix a region block, so
        // don't waste the one retry on it either way.
        val isGeoRestricted = BotDetectionMitigator.isGeoError(firstAttempt.exceptionOrNull()?.message)

        if (firstAttempt.isFailure && !isGeoRestricted) {
            // Refresh guest metadata only for explicit session challenges. A CDN
            // 403, timeout or unavailable track does not establish a broken identity.
            val label = if (YouTube.cookie == null) "guest" else "signed-in user"
            Timber.tag(TAG).w("Playback failed for $label. Retrying resolution...")
            if (BotDetectionMitigator.notifyPlaybackFailure(
                    isLoggedIn = !YouTube.cookie.isNullOrBlank(),
                    errorMessage = firstAttempt.exceptionOrNull()?.message,
                )) {
                BotDetectionMitigator.rotateGuestSession()
            }
            val retryResult = resolvePlaybackData(videoId, playlistId, audioQuality, connectivityManager, preferAac)
            return retryResult
        }

        return firstAttempt
    }

    private fun isMatchingSaavnCandidate(
        candidate: com.music.jiosaavn.RawSongItem,
        wantedTitle: String,
        wantedArtists: List<String>,
        wantedDurationSec: Int?,
    ): Boolean {
        val candidateTitle = candidate.title.lowercase(java.util.Locale.US)
        val candidateArtists = candidate.moreInfo.artistMap.primaryArtists.map {
            it.name.lowercase(java.util.Locale.US)
        }

        val titleMatches = candidateTitle.contains(wantedTitle) || wantedTitle.contains(candidateTitle)
        val artistMatches = wantedArtists.isEmpty() || wantedArtists.any { wantedArtist ->
            candidateArtists.any { candidateArtist ->
                candidateArtist.contains(wantedArtist) || wantedArtist.contains(candidateArtist)
            }
        }

        // Require an exact title match when duration is unavailable, avoiding loose substring matches.
        val candidateDurationSec = candidate.moreInfo.duration.toIntOrNull()
        val durationMatches = if (wantedDurationSec != null && candidateDurationSec != null) {
            kotlin.math.abs(candidateDurationSec - wantedDurationSec) <= 10
        } else {
            candidateTitle == wantedTitle
        }

        return titleMatches && artistMatches && durationMatches
    }

    private suspend fun findBestSaavnMatch(
        primaryQuery: String,
        fallbackQuery: String,
        wantedTitle: String,
        wantedArtists: List<String>,
        wantedDurationSec: Int?,
    ): com.music.jiosaavn.RawSongItem? {
        val primaryMatch = findSaavnMatch(primaryQuery, wantedTitle, wantedArtists, wantedDurationSec)
        if (primaryMatch != null || primaryQuery == fallbackQuery) return primaryMatch

        Timber.tag(TAG).d("Saavn: no match found with primary query, trying fallback: \"$fallbackQuery\"")
        return findSaavnMatch(fallbackQuery, wantedTitle, wantedArtists, wantedDurationSec)
    }

    private suspend fun findSaavnMatch(
        query: String,
        wantedTitle: String,
        wantedArtists: List<String>,
        wantedDurationSec: Int?,
    ): com.music.jiosaavn.RawSongItem? {
        if (query.isBlank()) return null

        Timber.tag(TAG).d("Saavn: searching with query: \"$query\"")
        val songs = SaavnService.searchSongs(query)
        return songs.firstOrNull { candidate ->
            val isMatch = isMatchingSaavnCandidate(
                candidate = candidate,
                wantedTitle = wantedTitle,
                wantedArtists = wantedArtists,
                wantedDurationSec = wantedDurationSec,
            )
            val artists = candidate.moreInfo.artistMap.primaryArtists.joinToString { it.name }
            if (isMatch) {
                Timber.tag(TAG).d("Saavn: candidate matched: \"${candidate.title}\" on album \"${candidate.moreInfo.album}\" by $artists")
            } else {
                Timber.tag(TAG).d("Saavn: candidate rejected (name/artist/duration mismatch): \"${candidate.title}\" on album \"${candidate.moreInfo.album}\" by $artists")
            }
            isMatch
        }
    }

    private suspend fun resolvePlaybackData(
        videoId: String,
        playlistId: String? = null,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        preferAac: Boolean = false,
    ): Result<PlaybackData> = runCatching {
        Timber.tag(logTag).d("Fetching player response for videoId: $videoId, playlistId: $playlistId")
        PlaybackLogManager.log(PlaybackLogLevel.INFO, "Resolving playback data", "Video: $videoId")
        
        val isLoggedIn = YouTube.cookie != null
        Timber.tag(logTag).d("Session authentication status: ${if (isLoggedIn) "Logged in" else "Not logged in"}")

        // Extracting a timestamp downloads and parses player JavaScript, so do it only
        // for clients that require one and at most once per playback attempt.
        var signatureTimestampValue: Int? = null
        var signatureTimestampFetched = false
        suspend fun signatureTimestampFor(client: YouTubeClient): Int? {
            if (!client.useSignatureTimestamp) return null
            if (!signatureTimestampFetched) {
                signatureTimestampValue = getSignatureTimestampOrNull(videoId).timestamp
                signatureTimestampFetched = true
                Timber.tag(logTag).d("Signature timestamp obtained lazily: $signatureTimestampValue")
            }
            return signatureTimestampValue
        }

        var poToken: PoTokenResult? = null
        val sessionId = if (isLoggedIn) YouTube.dataSyncId else YouTube.visitorData
        if (MAIN_CLIENT.useWebPoTokens && sessionId != null) {
            Timber.tag(logTag).d("Generating PoToken for MAIN_CLIENT with sessionId")
            try {
                poToken = poTokenGenerator.getWebClientPoTokenSuspend(videoId, sessionId)
                if (poToken != null) {
                    Timber.tag(logTag).d("PoToken generated successfully")
                }
            } catch (e: Exception) {
                Timber.tag(logTag).e(e, "PoToken generation failed: ${e.message}")
            }
        }

        // Fetch metadata for history and loudness in parallel. A slow WEB_REMIX token must
        // not delay playback; the result is awaited briefly after the stream is ready.
        val metaDeferred = if (isLoggedIn) metadataScope.async {
            Timber.tag(logTag).d("Fetching metadata from METADATA_CLIENT (WEB_REMIX) for authenticated tracking")
            try {
                var metaPoToken: PoTokenResult? = null
                val metaSessionId = YouTube.dataSyncId
                if (METADATA_CLIENT.useWebPoTokens && metaSessionId != null) {
                    try {
                        metaPoToken = poTokenGenerator.getWebClientPoTokenSuspend(videoId, metaSessionId)
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "Metadata PoToken generation failed")
                    }
                }
                val metaTimestamp = if (METADATA_CLIENT.useSignatureTimestamp) getSignatureTimestampOrNull(videoId).timestamp else null
                YouTube.player(videoId, playlistId, METADATA_CLIENT, metaTimestamp, metaPoToken?.playerRequestPoToken)
                    .getOrNull()
                    .also { Timber.tag(logTag).d("Metadata response obtained: ${it?.playabilityStatus?.status}") }
            } catch (e: Exception) {
                Timber.tag(logTag).e(e, "Failed to fetch metadata from METADATA_CLIENT")
                null
            }
        } else null

        Timber.tag(logTag).d("Attempting to get player response using MAIN_CLIENT: ${MAIN_CLIENT.clientName}")
        PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Trying ${MAIN_CLIENT.clientName} (Main)")
        var mainPlayerResponse =
            YouTube.player(videoId, playlistId, MAIN_CLIENT, signatureTimestampFor(MAIN_CLIENT), poToken?.playerRequestPoToken).getOrThrow()

        var usedAgeRestrictedClient: YouTubeClient? = null
        val wasOriginallyAgeRestricted: Boolean

        // ANDROID_VR uses LOGIN_REQUIRED for bot detection, not age gating.
        val mainStatus = mainPlayerResponse.playabilityStatus.status
        val isAgeRestrictedFromResponse = isAgeRestrictedStatus(mainStatus)
        wasOriginallyAgeRestricted = isAgeRestrictedFromResponse

        if (isAgeRestrictedFromResponse && isLoggedIn) {
            // Age-restricted: use WEB_CREATOR directly (no NewPipe needed from here)
            Timber.tag(logTag).d("Age-restricted detected, using WEB_CREATOR")
            Timber.tag(TAG).i("Age-restricted: using WEB_CREATOR for videoId=$videoId")
            val creatorResponse = YouTube.player(videoId, playlistId, WEB_CREATOR, null, null).getOrNull()
            if (creatorResponse?.playabilityStatus?.status == "OK") {
                Timber.tag(logTag).d("WEB_CREATOR works for age-restricted content")
                mainPlayerResponse = creatorResponse
                usedAgeRestrictedClient = WEB_CREATOR
            }
        }

        var format: PlayerResponse.StreamingData.Format? = null
        var streamUrl: String? = null
        var streamExpiresInSeconds: Int? = null
        var streamPlayerResponse: PlayerResponse? = null
        var retryMainPlayerResponse: PlayerResponse? = if (usedAgeRestrictedClient != null) mainPlayerResponse else null

        val currentStatus = mainPlayerResponse.playabilityStatus.status
        val isAgeRestricted = isAgeRestrictedStatus(currentStatus)

        if (isAgeRestricted) {
            Timber.tag(logTag).d("Content is still age-restricted (status: $currentStatus), will try fallback clients")
            Timber.tag(TAG).i("Age-restricted content detected: videoId=$videoId, status=$currentStatus")
        }

        val isPrivateTrack = mainPlayerResponse.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_PRIVATELY_OWNED_TRACK"

        // Uploads start at TVHTML5; age-restricted videos skip the main client.
        // Other tracks try the main client first.
        val startIndex = when {
            isPrivateTrack -> STREAM_FALLBACK_CLIENTS.indexOf(TVHTML5).coerceAtLeast(0)
            isAgeRestricted -> 0
            else -> -1
        }

        clientLoop@ for (clientIndex in (startIndex until STREAM_FALLBACK_CLIENTS.size)) {
            format = null
            streamUrl = null
            streamExpiresInSeconds = null

            val client: YouTubeClient
            if (clientIndex == -1) {
                client = MAIN_CLIENT
                streamPlayerResponse = retryMainPlayerResponse ?: mainPlayerResponse
                Timber.tag(logTag).d("Trying stream from MAIN_CLIENT: ${client.clientName}")
            } else {
                client = STREAM_FALLBACK_CLIENTS[clientIndex]
                Timber.tag(logTag).d("Trying fallback client ${clientIndex + 1}/${STREAM_FALLBACK_CLIENTS.size}: ${client.clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Trying fallback [${clientIndex + 1}/${STREAM_FALLBACK_CLIENTS.size}]", client.clientName)

                if (client.loginRequired && !isLoggedIn && YouTube.cookie == null) {
                    Timber.tag(logTag).d("Skipping client ${client.clientName} - requires login but user is not logged in")
                    continue
                }

                // Lazily generate PoToken for fallback web clients if we haven't already
                if (client.useWebPoTokens && poToken == null && sessionId != null) {
                    Timber.tag(logTag).d("Lazily generating PoToken for fallback web client: ${client.clientName}")
                    try {
                        poToken = poTokenGenerator.getWebClientPoTokenSuspend(videoId, sessionId)
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "Lazy PoToken generation failed")
                    }
                }

                Timber.tag(logTag).d("Fetching player response for fallback client: ${client.clientName}")
                val clientPoToken = if (client.useWebPoTokens) poToken?.playerRequestPoToken else null
                // Age-restricted responses do not need a timestamp for fallback clients.
                val clientSigTimestamp =
                    if (wasOriginallyAgeRestricted) null else signatureTimestampFor(client)
                streamPlayerResponse =
                    YouTube.player(videoId, playlistId, client, clientSigTimestamp, clientPoToken).getOrNull()
            }

            if (streamPlayerResponse?.playabilityStatus?.status == "OK") {
                Timber.tag(logTag).d("Player response status OK for client: ${if (clientIndex == -1) MAIN_CLIENT.clientName else STREAM_FALLBACK_CLIENTS[clientIndex].clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.INFO, "Player response OK", if (clientIndex == -1) MAIN_CLIENT.clientName else STREAM_FALLBACK_CLIENTS[clientIndex].clientName)

                val hasDirectUrls = streamPlayerResponse.streamingData?.adaptiveFormats
                    ?.any { !it.url.isNullOrEmpty() } == true
                val hasSignatureCipher = streamPlayerResponse.streamingData?.adaptiveFormats
                    ?.any { !it.signatureCipher.isNullOrEmpty() || !it.cipher.isNullOrEmpty() } == true

                Timber.tag(logTag).d("URL check: hasDirectUrls=$hasDirectUrls, hasSignatureCipher=$hasSignatureCipher")

                val responseToUse = streamPlayerResponse

                val candidates = rankedAudioFormats(
                    responseToUse.streamingData?.adaptiveFormats.orEmpty(),
                    audioQuality,
                    connectivityManager.isActiveNetworkMetered,
                    preferAac,
                )
                for (candidate in candidates) {
                    kotlinx.coroutines.currentCoroutineContext().ensureActive()
                    format = candidate
                    streamUrl = findUrlOrNull(candidate, videoId, responseToUse, skipNewPipe = wasOriginallyAgeRestricted)
                    if (streamUrl == null) continue

                val currentClient = if (clientIndex == -1) {
                    usedAgeRestrictedClient ?: MAIN_CLIENT
                } else {
                    STREAM_FALLBACK_CLIENTS[clientIndex]
                }

                val isPrivatelyOwnedTrack = streamPlayerResponse.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_PRIVATELY_OWNED_TRACK"

                // Apply n-transform FIRST for web clients (main branch order - critical!)
                if (currentClient.useWebPoTokens) {
                    try {
                        Timber.tag(logTag).d("Applying n-transform to stream URL for ${currentClient.clientName}")
                        val transformed = EjsNTransformSolver.transformNParamInUrl(streamUrl)
                        if (transformed != streamUrl) {
                            streamUrl = transformed
                            Timber.tag(logTag).d("N-transform applied successfully")
                        }
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "N-transform failed: ${e.message}")
                    }
                }

                // Apply PoToken SECOND (after n-transform - main branch order)
                // PoToken is base64; URL-encoding it breaks validation.
                if (currentClient.useWebPoTokens && poToken?.streamingDataPoToken != null) {
                    Timber.tag(logTag).d("Appending pot= parameter to stream URL")
                    val separator = if ("?" in streamUrl) "&" else "?"
                    streamUrl = "${streamUrl}${separator}pot=${poToken.streamingDataPoToken}"
                }

                streamExpiresInSeconds = streamPlayerResponse.streamingData?.expiresInSeconds
                if (streamExpiresInSeconds == null) {
                    // Keep a resolved stream if the response omits its expiry.
                    streamExpiresInSeconds = DEFAULT_STREAM_EXPIRY_SECONDS
                }

                Timber.tag(logTag).d("Stream expires in: $streamExpiresInSeconds seconds")

                val urlHost = try { java.net.URL(streamUrl).host } catch (e: Exception) { "unknown" }
                Timber.tag(logTag).d("Stream URL host: $urlHost, pot length: ${poToken?.streamingDataPoToken?.length ?: 0}")

                if (clientIndex == STREAM_FALLBACK_CLIENTS.size - 1 || isPrivatelyOwnedTrack) {
                    if (isPrivatelyOwnedTrack) {
                        Timber.tag(logTag).d("Skipping validation for privately owned track: ${currentClient.clientName}")
                    } else {
                        Timber.tag(logTag).d("Using last fallback client without validation: ${STREAM_FALLBACK_CLIENTS[clientIndex].clientName}")
                    }
                    Timber.tag(TAG).i("Playback: client=${currentClient.clientName}, videoId=$videoId, private=$isPrivatelyOwnedTrack")
                    break@clientLoop
                }

                if (validateStatus(streamUrl)) {
                    Timber.tag(logTag).d("Stream validated successfully with client: ${currentClient.clientName}")
                    PlaybackLogManager.log(PlaybackLogLevel.INFO, "Stream validated", currentClient.clientName)
                    Timber.tag(TAG).i("Playback: client=${currentClient.clientName}, videoId=$videoId")
                    break@clientLoop
                } else {
                    Timber.tag(logTag).d("Stream validation failed for client: ${currentClient.clientName}")

                    // For web clients: try alternate n-transform and re-validate (Zemer approach)
                    if (currentClient.useWebPoTokens) {
                        var nTransformWorked = false

                        try {
                            val nTransformed = CipherDeobfuscator.transformNParamInUrl(streamUrl)
                            if (nTransformed != streamUrl) {
                                Timber.tag(logTag).d("CipherDeobfuscator n-transform applied, re-validating...")
                                if (validateStatus(nTransformed)) {
                                    Timber.tag(logTag).d("N-transformed URL VALIDATED OK!")
                                    streamUrl = nTransformed
                                    nTransformWorked = true
                                    Timber.tag(TAG).i("Playback: client=${currentClient.clientName}, videoId=$videoId (cipher n-transform)")
                                }
                            }
                        } catch (e: Exception) {
                            Timber.tag(logTag).e(e, "CipherDeobfuscator n-transform error")
                        }

                        if (nTransformWorked) break@clientLoop
                    }
                }
                }
            } else {
                val status = streamPlayerResponse?.playabilityStatus?.status ?: "Unknown"
                val reason = streamPlayerResponse?.playabilityStatus?.reason ?: "No reason"
                Timber.tag(logTag).d("Player response status not OK: $status, reason: $reason")
                PlaybackLogManager.log(PlaybackLogLevel.WARNING, "Client failed: ${client.clientName}", "$status: $reason")
                
            }
        }

        if (streamPlayerResponse == null) {
            Timber.tag(logTag).e("Bad stream player response - all clients failed")
            throw Exception("Bad stream player response")
        }

        if (streamPlayerResponse.playabilityStatus.status != "OK") {
            val errorReason = streamPlayerResponse.playabilityStatus.reason
            Timber.tag(logTag).e("Playability status not OK: $errorReason")
            throw PlaybackException(
                errorReason,
                null,
                PlaybackException.ERROR_CODE_REMOTE_ERROR
            )
        }

        if (format == null) {
            val status = streamPlayerResponse.playabilityStatus.status
            val reason = streamPlayerResponse.playabilityStatus.reason
            val detail = buildString {
                append("No playable audio source was returned")
                if (!status.isNullOrBlank()) append(" (status: $status)")
                if (!reason.isNullOrBlank()) append(": $reason")
            }
            Timber.tag(logTag).e(detail)
            // Report this as a remote playback error so ExoPlayer does not retry it as I/O.
            throw PlaybackException(
                detail,
                null,
                PlaybackException.ERROR_CODE_REMOTE_ERROR,
            )
        }

        if (streamUrl == null) {
            val detail = "A playable audio format was returned, but no stream URL could be resolved"
            Timber.tag(logTag).e("$detail (itag=${format.itag}, mimeType=${format.mimeType})")
            throw PlaybackException(
                detail,
                null,
                PlaybackException.ERROR_CODE_REMOTE_ERROR,
            )
        }
        // Keep a playable stream if the response omitted its expiry.
        val expiresInSeconds = streamExpiresInSeconds ?: DEFAULT_STREAM_EXPIRY_SECONDS

        Timber.tag(logTag).d("Successfully obtained playback data with format: ${format.mimeType}, bitrate: ${format.bitrate}")
        // Prefer authenticated metadata when ready; otherwise use the main response.
        val metadataResponse = metaDeferred?.let { withTimeoutOrNull(MetadataWaitMs) { it.await() } }
        if (metaDeferred != null && metadataResponse == null) {
            Timber.tag(logTag).w("Metadata not ready within ${MetadataWaitMs}ms -- playing without it")
        }
        PlaybackData(
            metadataResponse?.playerConfig?.audioConfig ?: mainPlayerResponse.playerConfig?.audioConfig,
            metadataResponse?.videoDetails ?: mainPlayerResponse.videoDetails,
            metadataResponse?.playbackTracking ?: mainPlayerResponse.playbackTracking,
            format,
            streamUrl,
            expiresInSeconds,
        )
    }.onFailure { e ->
        Timber.tag(logTag).e(e, "Playback resolution failed")
        PlaybackLogManager.log(PlaybackLogLevel.ERROR, "Playback failed", "${e::class.simpleName}: ${e.message}")
    }
    /**
     * Simple player response intended to use for metadata only.
     * Stream URLs of this response might not work so don't use them.
     */
    suspend fun playerResponseForMetadata(
        videoId: String,
        playlistId: String? = null,
    ): Result<PlayerResponse> {
        Timber.tag(logTag).d("Fetching metadata-only player response for videoId: $videoId using MAIN_CLIENT: ${MAIN_CLIENT.clientName}")
        return YouTube.player(videoId, playlistId, client = WEB_REMIX) // ANDROID_VR does not work with history
            .onSuccess { Timber.tag(logTag).d("Successfully fetched metadata") }
            .onFailure { Timber.tag(logTag).e(it, "Failed to fetch metadata") }
    }

    private fun validateStatus(url: String): Boolean {
        Timber.tag(logTag).d("Validating stream URL status")
        try {
            // Use a one-byte ranged GET: YouTube can accept HEAD for a stream it won't serve.
            val requestBuilder = okhttp3.Request.Builder()
                .get()
                .url(url)
                .header("Range", "bytes=0-0")
                .header("User-Agent", YouTubeClient.USER_AGENT_WEB)

            // Do NOT add Cookie header — googlevideo.com CDN rejects account cookies with 403.
            // Stream URLs are already authenticated via signed URL parameters.

            // use {} rather than leaking the (1-byte) body back into the pool unread.
            // 206 is the honoured range; a server that ignores Range answers 200. Both
            // mean bytes are being served, which is the only thing this asks.
            val (isSuccessful, code) = httpClient.newCall(requestBuilder.build()).execute()
                .use { it.isSuccessful to it.code }
            Timber.tag(logTag).d("Stream URL validation result: ${if (isSuccessful) "Success" else "Failed"} ($code)")
            return isSuccessful
        } catch (e: Exception) {
            Timber.tag(logTag).e(e, "Stream URL validation failed with exception")
            reportException(e)
        }
        return false
    }
    data class SignatureTimestampResult(
        val timestamp: Int?,
        val isAgeRestricted: Boolean
    )

    private fun getSignatureTimestampOrNull(videoId: String): SignatureTimestampResult {
        Timber.tag(logTag).d("Getting signature timestamp for videoId: $videoId")
        val result = NewPipeExtractor.getSignatureTimestamp(videoId)
        return result.fold(
            onSuccess = { timestamp ->
                Timber.tag(logTag).d("Signature timestamp obtained: $timestamp")
                SignatureTimestampResult(timestamp, isAgeRestricted = false)
            },
            onFailure = { error ->
                val isAgeRestricted = error.message?.contains("age-restricted", ignoreCase = true) == true ||
                    error.cause?.message?.contains("age-restricted", ignoreCase = true) == true
                if (isAgeRestricted) {
                    Timber.tag(logTag).d("Age-restricted content detected from NewPipe")
                    Timber.tag(TAG).i("Age-restricted detected early via NewPipe: videoId=$videoId")
                } else {
                    Timber.tag(logTag).e(error, "Failed to get signature timestamp")
                    reportException(error)
                }
                SignatureTimestampResult(null, isAgeRestricted)
            }
        )
    }

    private suspend fun findUrlOrNull(
        format: PlayerResponse.StreamingData.Format,
        videoId: String,
        playerResponse: PlayerResponse,
        skipNewPipe: Boolean = false
    ): String? {
        Timber.tag(logTag).d("Finding stream URL for format: ${format.mimeType}, videoId: $videoId, skipNewPipe: $skipNewPipe")

        if (!format.url.isNullOrEmpty()) {
            Timber.tag(logTag).d("Using URL from format directly")
            return format.url
        }

        val signatureCipher = format.signatureCipher ?: format.cipher
        if (!signatureCipher.isNullOrEmpty()) {
            Timber.tag(logTag).d("Format has signatureCipher, using custom deobfuscation")
            val customDeobfuscatedUrl = CipherDeobfuscator.deobfuscateStreamUrl(signatureCipher, videoId)
            if (customDeobfuscatedUrl != null) {
                Timber.tag(logTag).d("Stream URL obtained via custom cipher deobfuscation")
                return customDeobfuscatedUrl
            }
            Timber.tag(logTag).d("Custom cipher deobfuscation failed")
        }

        if (skipNewPipe) {
            Timber.tag(logTag).d("Skipping NewPipe methods for age-restricted content")
            return null
        }

        val deobfuscatedUrl = NewPipeExtractor.getStreamUrl(format, videoId)
        if (deobfuscatedUrl != null) {
            Timber.tag(logTag).d("Stream URL obtained via NewPipe deobfuscation")
            return deobfuscatedUrl
        }

        Timber.tag(logTag).d("Trying StreamInfo fallback for URL")
        val streamUrls = YouTube.getNewPipeStreamUrls(videoId)
        if (streamUrls.isNotEmpty()) {
            val streamUrl = streamUrls.find { it.first == format.itag }?.second
            if (streamUrl != null) {
                Timber.tag(logTag).d("Stream URL obtained from StreamInfo")
                return streamUrl
            }


        }

        Timber.tag(logTag).e("Failed to get stream URL")
        return null
    }

    fun forceRefreshForVideo(videoId: String) {
        Timber.tag(logTag).d("Force refreshing for videoId: $videoId")
    }
}
