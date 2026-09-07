/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.net.ConnectivityManager

import androidx.media3.common.PlaybackException
import com.music.innertube.NewPipeExtractor
import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_CREATOR
import com.convxy.music.utils.BotDetectionMitigator
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_43_32
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_61_48
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
import com.convxy.music.constants.AudioQuality
import com.convxy.music.constants.EnableSaavnStreamingKey
import com.convxy.music.constants.ForceSelectedQualityKey
import com.convxy.music.constants.SaavnFallbackToYouTubeKey
import com.convxy.music.constants.EnableTidalStreamingKey
import com.convxy.music.constants.EnabledModulesKey
import com.convxy.music.constants.ModuleSourcesKey
import com.convxy.music.constants.ModuleSettingsKey
import com.convxy.music.constants.SaavnAudioQuality
import com.convxy.music.constants.SaavnAudioQualityKey
import com.convxy.music.constants.TidalInstanceUrlKey
import com.convxy.music.constants.TidalQuality
import com.convxy.music.constants.TidalQualityKey
import com.convxy.music.utils.tidal.TidalService
import com.music.spine.ModuleManager
import org.json.JSONArray
import com.convxy.music.utils.cipher.CipherDeobfuscator
import com.convxy.music.utils.YTPlayerUtils.MAIN_CLIENT
import com.convxy.music.utils.YTPlayerUtils.STREAM_FALLBACK_CLIENTS
import com.convxy.music.utils.YTPlayerUtils.validateStatus
import com.convxy.music.utils.potoken.PoTokenGenerator
import com.convxy.music.utils.potoken.PoTokenResult
import com.convxy.music.utils.sabr.EjsNTransformSolver
import com.convxy.music.utils.PlaybackLogLevel
import com.convxy.music.utils.PlaybackLogManager
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

object YTPlayerUtils {
    private const val logTag = "YTPlayerUtils"
    private const val TAG = "YTPlayerUtils"

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
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
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val poTokenGenerator = PoTokenGenerator()

    /**
     * Client used for fast, low-latency stream resolution.
     * ANDROID_VR clients don't require PoToken and start instantly.
     * Note: ANDROID_VR has loginSupported=false, so metadata like audioConfig and
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
     * Tried in order when [MAIN_CLIENT] cannot produce a stream.
     *
     * IOS is first, and that ordering is measured rather than assumed. Probing the live
     * API (2026-08-19) against a track YouTube had bot-flagged, every one of ANDROID_VR
     * (both versions), ANDROID_MUSIC, IOS_MUSIC, TVHTML5 and WEB_REMIX came back
     * LOGIN_REQUIRED / "Sign in to confirm you're not a bot" or UNPLAYABLE -- and IOS
     * returned OK with direct URLs. Across five popular tracks IOS answered 5/5.
     *
     * It used to sit ninth. So the common failure -- one bot-flagged track -- cost eight
     * doomed round trips, several of them generating a PoToken first, before reaching the
     * client that works. That is the shape users describe as "playing very late", and when
     * one of those eight stalls it is the shape they describe as "song not playing".
     *
     * ANDROID_VR stays as MAIN_CLIENT: when it is not flagged it is fast and offers more
     * audio formats (4 vs IOS's 2), so it remains the better first choice. This array is
     * purely the recovery path.
     *
     * VISIONOS was added 2026-08-19 ahead of IOS: an unreleased/internal client YouTube
     * hasn't started bot-flagging yet, unlike IOS which the 2026-08-19 probe caught 5/5
     * but user reports since then show failing on bot-flagged tracks too. IOS kept second
     * since it still works when VISIONOS doesn't.
     *
     * IPADOS answered 0/5 in the same probe and is kept only for completeness.
     */
    private val STREAM_FALLBACK_CLIENTS: Array<YouTubeClient> = arrayOf(
        VISIONOS,
        IOS,
        ANDROID_VR_1_61_48,
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
        /** True when the stream is sourced from JioSaavn (not YouTube). */
        val isSaavnStream: Boolean = false,
        /** True when the stream is lossless FLAC sourced from TIDAL (hifi-api). */
        val isTidalStream: Boolean = false,
        /** True when the stream is sourced from an 8spine module. */
        val isSpineStream: Boolean = false,
    )
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
        /** Downloads pass false so the offline copy stays YouTube; playback allows lossless. */
        allowLossless: Boolean = true,
        /** Set when a specific track has already failed lossless playback too many
         *  times (stall/parsing errors) — skips the Spine and Tidal intercepts
         *  entirely and resolves the plain YouTube stream for this call only. */
        forceStandardAudio: Boolean = false,
        /** When true, resolve a muxed (audio+video) stream so the player can show
         *  the song's full YouTube video. Skips the audio-only intercepts. */
        videoMode: Boolean = false,
    ): Result<PlaybackData> {
        // ── JioSaavn intercept ───────────────────────────────────────────────
        // If the user has enabled JioSaavn streaming, try to resolve the stream
        // URL from JioSaavn first. We fall through to YouTube on ANY failure so
        // the user always hears audio.
        if (context != null) {
            // Circuit breaker: skips both the lossless-capable Spine modules and
            // Tidal below, so a user who picked a specific quality actually gets
            // it instead of a module/Tidal silently upgrading to FLAC.
            val forceSelectedQuality = context.dataStore.get(ForceSelectedQualityKey, false)

            // ── 8spine module intercept ─────────────────────────────────────────
            // Try enabled 8spine modules for streaming before other sources.
            // Falls through to TIDAL/Saavn/YouTube on ANY failure.
            if (!forceStandardAudio && !videoMode) {
            Timber.tag(TAG).d("═══ SPINE INTERCEPT START ═══ videoId=$videoId")
            val enabledModulesJson = context.dataStore.get(EnabledModulesKey, "[]")
            val moduleSourcesJson = context.dataStore.get(ModuleSourcesKey, "[]")
            val moduleSettingsJson = context.dataStore.get(ModuleSettingsKey, "{}")
            Timber.tag(TAG).d("  enabledModules: $enabledModulesJson")
            Timber.tag(TAG).d("  moduleSources: $moduleSourcesJson")
            Timber.tag(TAG).d("  moduleSettings: $moduleSettingsJson")
            runCatching {
                // Stored in the user's chosen priority order (drag-reorder in
                // ModuleSourceScreen), lowest index tried first.
                val enabledOrder = runCatching {
                    val arr = JSONArray(enabledModulesJson)
                    (0 until arr.length()).map { arr.getString(it) }
                }.getOrElse { emptyList<String>() }
                val enabledIds = enabledOrder.toSet()

                val sourceUrls = runCatching {
                    val arr = JSONArray(moduleSourcesJson)
                    (0 until arr.length()).map { arr.getString(it) }
                }.getOrElse { emptyList<String>() }

                val allModuleSettings = runCatching {
                    val obj = org.json.JSONObject(moduleSettingsJson)
                    obj.keys().asSequence().associateWith { key ->
                        val inner = obj.optJSONObject(key)
                        if (inner != null) {
                            inner.keys().asSequence().associateWith { k -> inner.optString(k, "") }
                        } else emptyMap()
                    }
                }.getOrElse { emptyMap<String, Map<String, String>>() }

                Timber.tag(TAG).d("  Parsed enabledIds: $enabledIds")
                Timber.tag(TAG).d("  Parsed sourceUrls: $sourceUrls")
                Timber.tag(TAG).d("  Parsed allModuleSettings: $allModuleSettings")

                if (enabledIds.isEmpty() || sourceUrls.isEmpty()) {
                    Timber.tag(TAG).d("  SPINE SKIP: enabledIds=${enabledIds.size} sourceUrls=${sourceUrls.size} — falling through")
                    Timber.tag(TAG).d("═══ SPINE INTERCEPT END (skipped) ═══")
                }

                if (enabledIds.isNotEmpty() && sourceUrls.isNotEmpty()) {
                    Timber.tag(TAG).d("  Resolving YouTube metadata for search query...")

                    val (currentSong, meta) = coroutineScope {
                        val nextDeferred = async {
                            val nextResult = YouTube.next(WatchEndpoint(videoId = videoId)).getOrNull()
                            nextResult?.items?.getOrNull(nextResult.currentIndex ?: 0)
                                ?: nextResult?.items?.firstOrNull()
                        }
                        val metaDeferred = async { playerResponseForMetadata(videoId, playlistId).getOrNull() }
                        nextDeferred.await() to metaDeferred.await()
                    }

                    val title = currentSong?.title ?: meta?.videoDetails?.title.orEmpty()
                    Timber.tag(TAG).d("  Resolved title=\"$title\" from ${if (currentSong != null) "YouTube.next" else "videoDetails"}")
                    if (title.isNotBlank()) {
                        val artistNames: List<String> = if (currentSong?.artists?.isNotEmpty() == true) {
                            currentSong.artists.map { it.name }
                        } else {
                            listOf(
                                meta?.videoDetails?.author.orEmpty()
                                    .replace(Regex("(?i)\\s*-\\s*topic\\b"), "")
                                    .replace(Regex("(?i)\\s*VEVO\\b"), "")
                                    .trim()
                            ).filter { it.isNotBlank() }
                        }
                        var cleanTitle = title
                        for (artist in artistNames) {
                            cleanTitle = cleanTitle.replace(Regex("(?i)^\\s*${Regex.escape(artist)}\\s*[-–—]\\s*"), "")
                        }
                        // Strip YouTube noise that confuses search queries
                        cleanTitle = cleanTitle
                            .replace(Regex("(?i)\\(\\s*official\\s*(video|music\\s*video|audio)?\\s*\\)"), "")
                            .replace(Regex("(?i)\\(\\s*lyrics?\\s*\\)"), "")
                            .replace(Regex("(?i)\\[.*?\\]"), "")
                            .replace(Regex("[\u266A\u266B]"), "")
                            .replace(Regex("\\s+"), " ").trim()
                        val query = "$cleanTitle ${artistNames.joinToString(" ")}"
                            .replace("&", " ").replace(",", " ")
                            .replace(Regex("\\s+"), " ").trim()

                        Timber.tag(TAG).d("  Search query: \"$query\" (artists=$artistNames)")

                        val moduleManager = ModuleManager()
                        for ((sourceIdx, sourceUrl) in sourceUrls.withIndex()) {
                            Timber.tag(TAG).d("  ── Source [${sourceIdx + 1}/${sourceUrls.size}]: $sourceUrl")
                            val modules = moduleManager.fetchIndex(sourceUrl).getOrElse { e ->
                                Timber.tag(TAG).e("  ✗ Failed to fetch index from $sourceUrl: ${e.message}")
                                continue
                            }
                            Timber.tag(TAG).d("  ✓ Fetched ${modules.size} modules from $sourceUrl")
                            // Try in the user's priority order, not the index's own order —
                            // enabledOrder.indexOf is -1 (sorts first) for anything not enabled,
                            // but those are skipped by the `!in enabledIds` check below anyway.
                            val orderedModules = modules.sortedBy { enabledOrder.indexOf(it.id) }
                            for ((modIdx, module) in orderedModules.withIndex()) {
                                if (module.id !in enabledIds) {
                                    Timber.tag(TAG).d("    [${modIdx + 1}] SKIP ${module.id} (not enabled)")
                                    continue
                                }
                                Timber.tag(TAG).d("    [${modIdx + 1}/${modules.size}] TRY ${module.id} — isLossless=${module.isLossless} hasHiRes=${module.hasHiRes} isAtmos=${module.isDolbyAtmos}")
                                val spineResult = runCatching {
                                    val loadBaseUrl = sourceUrl.substringBeforeLast("/")
                                    Timber.tag(TAG).d("      Loading module JS from baseUrl=$loadBaseUrl")
                                    val loaded = moduleManager.loadModule(module) { loadBaseUrl }.getOrElse { e ->
                                        Timber.tag(TAG).e("      ✗ Failed to load module ${module.id}: ${e.message}")
                                        return@runCatching null
                                    }

                                    val moduleSettings = allModuleSettings[module.id] ?: emptyMap()
                                    Timber.tag(TAG).d("      Module settings: $moduleSettings")
                                    Timber.tag(TAG).d("      Calling searchTracks(\"$query\", 5, settings)...")
                                    var searchResult = moduleManager.searchTracks(loaded, query, 5, moduleSettings).getOrElse { e ->
                                        Timber.tag(TAG).e("      ✗ Search failed for module ${module.id}: ${e.message}")
                                        return@runCatching null
                                    }
                                    // Retry with artist-first query if no matches (helps GEOLIER2/Apple fallback)
                                    if (searchResult.tracks.isEmpty() && artistNames.isNotEmpty()) {
                                        val retryQuery = "${artistNames.first()} $cleanTitle"
                                            .replace("&", " ").replace(",", " ")
                                            .replace(Regex("\\s+"), " ").trim()
                                        Timber.tag(TAG).d("      No results, retrying with artist-first: \"$retryQuery\"")
                                        searchResult = moduleManager.searchTracks(loaded, retryQuery, 5, moduleSettings).getOrElse { e ->
                                            Timber.tag(TAG).e("      ✗ Retry search failed: ${e.message}")
                                            return@runCatching null
                                        }
                                    }
                                    // minByOrNull on its own always returns something — even a
                                    // track that matched nothing scores 0 and wins when it is the
                                    // only result. That is how a completely different song ends up
                                    // playing. Require a real title hit and a plausible runtime first.
                                    val matchedTrack = searchResult.tracks.filter { track ->
                                        val t = track.title.lowercase().trim()
                                        val q = cleanTitle.lowercase().trim()
                                        val songDur = currentSong?.duration
                                        val durationOk = if (songDur != null && track.duration != null) {
                                            Math.abs(track.duration - songDur) <= 10
                                        } else {
                                            // No runtime to check against — require an exact
                                            // title match instead of skipping the guard.
                                            t == q
                                        }
                                        (q in t || t in q) && durationOk
                                    }.minByOrNull { track ->
                                        val t = track.title.lowercase().trim()
                                        val a = track.artist.lowercase().trim()
                                        val q = cleanTitle.lowercase().trim()
                                        val artistsLower = artistNames.map { it.lowercase().trim() }
                                        var score = 0
                                        if (q in t || t in q) score -= 10
                                        if (artistsLower.any { it in a || a in it }) score -= 5
                                        val trackDur = track.duration
                                        val songDur = currentSong?.duration
                                        if (trackDur != null && songDur != null) {
                                            val diff = Math.abs(trackDur - songDur)
                                            if (diff < 5) score -= 3
                                        }
                                        score
                                    }
                                    if (matchedTrack == null) {
                                        Timber.tag(TAG).d("      ✗ No tracks found by module ${module.id}")
                                        return@runCatching null
                                    }

                                    Timber.tag(TAG).d("      ✓ Match: \"${matchedTrack.title}\" by \"${matchedTrack.artist}\" id=${matchedTrack.id} quality=${matchedTrack.audioQuality} duration=${matchedTrack.duration}s")

                                    Timber.tag(TAG).d("      Calling getStreamUrl(\"${matchedTrack.id}\", settings)...")
                                    val streamResult = moduleManager.getStreamUrl(loaded, matchedTrack.id, moduleSettings).getOrElse { e ->
                                        Timber.tag(TAG).e("      ✗ Stream URL fetch failed for module ${module.id} track ${matchedTrack.id}: ${e.message}")
                                        return@runCatching null
                                    }
                                    val streamUrl = streamResult.streamUrl?.ifBlank { null }
                                    if (streamUrl == null) {
                                        Timber.tag(TAG).d("      ✗ Empty stream URL from module ${module.id}")
                                        Timber.tag(TAG).d("      Full streamResult: streamUrl=${streamResult.streamUrl} track=${streamResult.track}")
                                        return@runCatching null
                                    }

                                    Timber.tag(TAG).d("      ✓ Stream URL: ${streamUrl.take(150)}...")
                                    Timber.tag(TAG).d("      Track audioQuality: ${streamResult.track?.audioQuality}")
                                    Timber.tag(TAG).d("      Track mimeType: ${streamResult.track?.mimeType}")
                                    Timber.tag(TAG).d("      Track audioModes: ${streamResult.track?.audioModes}")
                                    Timber.tag(TAG).d("      Track bitDepth: ${streamResult.track?.bitDepth}")
                                    Timber.tag(TAG).d("      Track sampleRate: ${streamResult.track?.sampleRate}")

                                    val isAtmosSupported = run {
                                        val list = android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS)
                                        list.codecInfos.any { !it.isEncoder && it.supportedTypes.any { t -> t.contains("eac3", ignoreCase = true) } }
                                    }

                                    val isAtmos = !forceSelectedQuality && isAtmosSupported && (module.isDolbyAtmos ||
                                        streamResult.track?.audioQuality?.uppercase()?.contains("ATMOS") == true ||
                                        streamResult.track?.audioModes?.any { it.uppercase().contains("ATMOS") } == true ||
                                        streamResult.track?.mimeType?.uppercase()?.contains("EAC3") == true)

                                    if (!isAtmosSupported && (streamResult.track?.audioQuality?.uppercase()?.contains("ATMOS") == true)) {
                                        Timber.tag(TAG).w("      ! Device does not support EAC3/Atmos - will attempt fallback to FLAC/High")
                                    }

                                    val isLossless = !isAtmos && !forceSelectedQuality && (
                                        module.isLossless ||
                                        streamResult.track?.audioQuality?.uppercase()?.contains("LOSSLESS") == true ||
                                        streamResult.track?.audioQuality?.uppercase()?.contains("HIRES") == true ||
                                        streamResult.track?.audioQuality?.uppercase()?.contains("FLAC") == true
                                    )

                                    Timber.tag(TAG).d("      isAtmos=$isAtmos (module.isDolbyAtmos=${module.isDolbyAtmos} audioQuality=${streamResult.track?.audioQuality} audioModes=${streamResult.track?.audioModes} mimeType=${streamResult.track?.mimeType})")
                                    Timber.tag(TAG).d("      isLossless=$isLossless (module.isLossless=${module.isLossless})")

                                    val mimeType = when {
                                        isAtmos -> "audio/eac3-joc; codecs=ec-3"
                                        isLossless -> "audio/flac; codecs=\"flac\""
                                        else -> "audio/mpeg; codecs=mp3"
                                    }
                                    val bitrate = when {
                                        isAtmos -> 768_000
                                        isLossless -> 1_411_000
                                        else -> 320_000
                                    }

                                    Timber.tag(TAG).d("      ✓ STREAMING from module ${module.id}: mimeType=$mimeType bitrate=$bitrate itag=${if (isAtmos) 9997 else if (isLossless) 9998 else 9996}")
                                    PlaybackData(
                                        audioConfig      = meta?.playerConfig?.audioConfig,
                                        videoDetails     = meta?.videoDetails,
                                        playbackTracking = meta?.playbackTracking,
                                        format           = PlayerResponse.StreamingData.Format(
                                            itag             = when {
                                                isAtmos -> 9997
                                                isLossless -> 9998
                                                else -> 9996
                                            },
                                            url              = streamUrl,
                                            mimeType         = mimeType,
                                            bitrate          = bitrate,
                                            width            = null,
                                            height           = null,
                                            contentLength    = null,
                                            quality          = when {
                                                isAtmos -> "DOLBY_ATMOS"
                                                isLossless -> "LOSSLESS"
                                                else -> "HIGH"
                                            },
                                            fps              = null,
                                            qualityLabel     = null,
                                            averageBitrate   = null,
                                            audioQuality     = when {
                                                isAtmos -> "DOLBY_ATMOS"
                                                isLossless -> "LOSSLESS"
                                                else -> "HIGH"
                                            },
                                            approxDurationMs = matchedTrack.duration?.let { (it * 1000L).toString() },
                                            audioSampleRate  = when {
                                                isAtmos -> 48000
                                                isLossless -> 44100
                                                else -> null
                                            },
                                            audioChannels    = when {
                                                isAtmos -> 6
                                                isLossless -> 2
                                                else -> null
                                            },
                                            loudnessDb       = null,
                                            lastModified     = null,
                                            signatureCipher  = null,
                                            cipher           = null,
                                            audioTrack       = null,
                                        ),
                                        streamUrl              = streamUrl,
                                        streamExpiresInSeconds = 3600,
                                        isSpineStream          = true,
                                    )
                                }.getOrNull()

                                if (spineResult != null) return Result.success(spineResult)
                            }
                        }
                    }
                }
            }.onFailure { e ->
                Timber.tag(TAG).e(e, "═══ SPINE INTERCEPT ERROR ═══")
            }
            Timber.tag(TAG).d("═══ SPINE INTERCEPT END ═══")
            } // !forceStandardAudio
            // ── End 8spine intercept ───────────────────────────────────────────

            // ── Lossless (TIDAL) intercept ───────────────────────────────────────
            // Opt-in FLAC from a public hifi-api instance. Tried BEFORE JioSaavn so
            // lossless wins. Falls through to Saavn/YouTube on ANY failure.
            if (!videoMode && !forceStandardAudio && !forceSelectedQuality && allowLossless && context.dataStore.get(EnableTidalStreamingKey, false)) {
                Timber.tag(TAG).d("Lossless enabled — trying TIDAL for videoId=$videoId")
                val tidalResult = runCatching {
                    val (currentSong, meta) = coroutineScope {
                        val nextDeferred = async {
                            val nextResult = YouTube.next(WatchEndpoint(videoId = videoId)).getOrNull()
                            nextResult?.items?.getOrNull(nextResult.currentIndex ?: 0)
                                ?: nextResult?.items?.firstOrNull()
                        }
                        val metaDeferred = async { playerResponseForMetadata(videoId, playlistId).getOrNull() }
                        nextDeferred.await() to metaDeferred.await()
                    }

                    val title = currentSong?.title ?: meta?.videoDetails?.title.orEmpty()
                    if (title.isBlank()) return@runCatching null

                    val artistNames: List<String> = if (currentSong?.artists?.isNotEmpty() == true) {
                        currentSong.artists.map { it.name }
                    } else {
                        listOf(
                            meta?.videoDetails?.author.orEmpty()
                                .replace(Regex("(?i)\\s*-\\s*topic\\b"), "")
                                .replace(Regex("(?i)\\s*VEVO\\b"), "")
                                .trim()
                        ).filter { it.isNotBlank() }
                    }
                    val query = "$title ${artistNames.joinToString(" ")}"
                        .replace("&", " ").replace(",", " ")
                        .replace(Regex("\\s+"), " ").trim()

                    val wantedTitle = title.lowercase(java.util.Locale.US)
                    val wantedArtists = artistNames.map { it.lowercase(java.util.Locale.US) }
                    val wantedDuration = currentSong?.duration
                    val customUrl = context.dataStore.get(TidalInstanceUrlKey, "").ifBlank { null }

                    val candidates = TidalService.search(query, customUrl)
                    // Ranked, not just the first list-order match: Tidal's search
                    // order isn't quality order, and the previous first-match-wins
                    // pick could lock onto a lower-quality candidate (or one whose
                    // stream simply fails) ahead of a same-title/artist/duration
                    // candidate that actually serves lossless.
                    val matchingCandidates = candidates.filter { c ->
                        val ct = c.title.lowercase(java.util.Locale.US)
                        val ca = c.artistNames.map { it.lowercase(java.util.Locale.US) }
                        val titleOk = ct.contains(wantedTitle) || wantedTitle.contains(ct)
                        val artistOk = wantedArtists.isEmpty() || wantedArtists.any { w ->
                            ca.any { it.contains(w) || w.contains(it) }
                        }
                        // Substring matching alone lets a short title pull in an
                        // unrelated track; runtime is the cheap sanity check. When
                        // either duration is unknown there's nothing to check
                        // against — require an exact title match instead of
                        // silently skipping the guard, so a generic title can't
                        // slip a wrong track through with no check at all.
                        val durationOk = if (wantedDuration != null && c.duration != null) {
                            kotlin.math.abs(c.duration - wantedDuration) <= 10
                        } else {
                            ct == wantedTitle
                        }
                        titleOk && artistOk && durationOk
                    }.sortedByDescending { tidalQualityRank(it.audioQuality) }

                    if (matchingCandidates.isEmpty()) {
                        Timber.tag(TAG).d("TIDAL: no match for \"$query\" — falling back")
                        return@runCatching null
                    }

                    val quality = runCatching {
                        TidalQuality.valueOf(context.dataStore.get(TidalQualityKey, TidalQuality.LOSSLESS.name))
                    }.getOrDefault(TidalQuality.LOSSLESS)

                    // Try every match, best-quality-first, instead of aborting to
                    // Saavn/YouTube the instant the single first candidate's
                    // stream fetch fails.
                    var best: com.convxy.music.utils.tidal.TidalTrack? = null
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
            // ── End TIDAL intercept ──────────────────────────────────────────────

            val saavnEnabled = !videoMode && context.dataStore.get(EnableSaavnStreamingKey, false)
            if (saavnEnabled) {
                Timber.tag(TAG).d("JioSaavn streaming enabled — trying Saavn for videoId=$videoId")
                val saavnResult = runCatching {
                    // Step 1: fetch YouTube Music next items and player metadata concurrently
                    val (currentSong, meta) = coroutineScope {
                        val nextDeferred = async {
                            val nextResult = YouTube.next(WatchEndpoint(videoId = videoId)).getOrNull()
                            nextResult?.items?.getOrNull(nextResult.currentIndex ?: 0)
                                ?: nextResult?.items?.firstOrNull()
                        }
                        val metaDeferred = async {
                            playerResponseForMetadata(videoId, playlistId).getOrNull()
                        }
                        nextDeferred.await() to metaDeferred.await()
                    }

                    // Prefer the YouTube Music next() title; fall back to videoDetails title
                    val title = currentSong?.title
                        ?: meta?.videoDetails?.title.orEmpty()

                    // Use the proper artist list from SongItem (real artist names).
                    // Fall back to videoDetails.author with "- Topic" stripped.
                    val artistNames: List<String> = if (currentSong?.artists?.isNotEmpty() == true) {
                        currentSong.artists.map { it.name }
                    } else {
                        listOf(
                            meta?.videoDetails?.author.orEmpty()
                                .replace(Regex("(?i)\\s*-\\s*topic\\b"), "")
                                .replace(Regex("(?i)\\s*VEVO\\b"), "")
                                .trim()
                        ).filter { it.isNotBlank() }
                    }
                    val artist = artistNames.joinToString(", ")

                    if (title.isBlank()) return@runCatching null

                    Timber.tag(TAG).d("Saavn: resolved title=\"$title\" artists=$artistNames for videoId=$videoId")

                    val albumName = currentSong?.album?.name.orEmpty()
                    val wantedTitleLower = title.lowercase(java.util.Locale.US)
                    val wantedArtistsLower = artistNames.map { it.lowercase(java.util.Locale.US) }
                    val wantedDurationSec = currentSong?.duration

                    val primaryQuery = if (albumName.isNotBlank()) {
                        "$albumName $title $artist"
                    } else {
                        "$title $artist"
                    }
                    .replace("&", " ")
                    .replace(",", " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()

                    val fallbackQuery = "$title $artist"
                    .replace("&", " ")
                    .replace(",", " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()

                    suspend fun findMatch(searchQuery: String): com.music.jiosaavn.SaavnSong? {
                        if (searchQuery.isBlank()) return null
                        Timber.tag(TAG).d("Saavn: searching with query: \"$searchQuery\"")
                        val songs = SaavnService.searchSongs(searchQuery).getOrNull() ?: return null
                        return songs.firstOrNull { candidate ->
                            val candidateTitleLower = candidate.name.lowercase(java.util.Locale.US)
                            val candidateArtists = candidate.artists.primary.map { it.name.lowercase(java.util.Locale.US) }
                            
                            val titleMatches = candidateTitleLower.contains(wantedTitleLower) || wantedTitleLower.contains(candidateTitleLower)
                            val artistMatches = wantedArtistsLower.isEmpty() || wantedArtistsLower.any { wanted ->
                                candidateArtists.any { candidateArtist ->
                                    candidateArtist.contains(wanted) || wanted.contains(candidateArtist)
                                }
                            }
                            
                            // Title matching is substring-based in both directions, so a
                            // short title matches plenty of unrelated songs. Runtime is
                            // the cheap tiebreak that stops a different track being
                            // served as this one. When either duration is unknown
                            // there's nothing to tiebreak with — require an exact
                            // title match instead of skipping the guard entirely.
                            val candidateDuration = candidate.duration
                            val durationMatches = if (wantedDurationSec != null && candidateDuration != null) {
                                kotlin.math.abs(candidateDuration - wantedDurationSec) <= 10
                            } else {
                                candidateTitleLower == wantedTitleLower
                            }

                            val isMatch = titleMatches && artistMatches && durationMatches
                            if (isMatch) {
                                Timber.tag(TAG).d("Saavn: candidate matched: \"${candidate.name}\" on album \"${candidate.album?.name}\" by ${candidate.artists.primary.joinToString { it.name }}")
                            } else {
                                Timber.tag(TAG).d("Saavn: candidate rejected (name/artist/duration mismatch): \"${candidate.name}\" on album \"${candidate.album?.name}\" by ${candidate.artists.primary.joinToString { it.name }}")
                            }
                            isMatch
                        }
                    }

                    var bestSong = findMatch(primaryQuery)
                    if (bestSong == null && primaryQuery != fallbackQuery) {
                        Timber.tag(TAG).d("Saavn: no match found with primary query, trying fallback: \"$fallbackQuery\"")
                        bestSong = findMatch(fallbackQuery)
                    }

                    if (bestSong == null) {
                        Timber.tag(TAG).d("Saavn: no matching candidate found — falling back to YT")
                        return@runCatching null
                    }

                    Timber.tag(TAG).i("Saavn: matched \"${bestSong.name}\" (id=${bestSong.id}, album=\"${bestSong.album?.name}\")")

                    // Step 4: resolve stream URL at requested quality
                    val qualityKey = context.dataStore.get(SaavnAudioQualityKey, SaavnAudioQuality.QUALITY_320.name)
                    val quality = runCatching { SaavnAudioQuality.valueOf(qualityKey) }
                        .getOrDefault(SaavnAudioQuality.QUALITY_320)

                    // First try to resolve stream URL directly from the search result's downloadUrl list
                    // to avoid an extra details API call (saves 300ms-800ms).
                    var streamUrl = SaavnService.selectBestUrl(bestSong.downloadUrl, quality.toApiValue())
                    if (streamUrl.isNullOrBlank()) {
                        Timber.tag(TAG).d("Saavn: downloadUrl list empty in search results, fetching via getBestStreamUrl for songId=${bestSong.id}")
                        streamUrl = SaavnService.getBestStreamUrl(bestSong.id, quality.toApiValue())
                    } else {
                        Timber.tag(TAG).d("Saavn: resolved stream URL directly from search results: $streamUrl")
                    }

                    if (streamUrl.isNullOrBlank()) {
                        Timber.tag(TAG).d("Saavn: no stream URL for songId=${bestSong.id} — falling back to YT")
                        return@runCatching null
                    }

                    // Optimization: Skip synchronous HTTP HEAD request to get content length during playback.
                    // ExoPlayer parses the content length automatically from HTTP GET response headers on buffer.
                    val contentLength: Long? = null

                    Timber.tag(TAG).i("Saavn: streaming from JioSaavn (quality=${quality.toApiValue()}) for videoId=$videoId")
                    // Return a minimal PlaybackData using the Saavn URL.
                    // Reuse the YouTube metadata already fetched in Step 1 — no second
                    // network call needed. This keeps audioConfig/videoDetails/playbackTracking
                    // intact so history and normalization still work properly.
                    PlaybackData(
                        audioConfig      = meta?.playerConfig?.audioConfig,
                        videoDetails     = meta?.videoDetails,
                        playbackTracking = meta?.playbackTracking,
                        format           = PlayerResponse.StreamingData.Format(
                            itag             = when (quality) {
                                SaavnAudioQuality.QUALITY_320 -> 141
                                SaavnAudioQuality.QUALITY_160 -> 140
                                SaavnAudioQuality.QUALITY_96  -> 139
                            },
                            url              = streamUrl,
                            // JioSaavn delivers AAC-LC audio inside a regular MP4 container
                            // (e.g. https://aac.saavncdn.com/.../{id}_320.mp4)
                            mimeType         = "audio/mp4; codecs=\"mp4a.40.2\"",
                            bitrate          = when (quality) {
                                SaavnAudioQuality.QUALITY_320 -> 320_000
                                SaavnAudioQuality.QUALITY_160 -> 160_000
                                SaavnAudioQuality.QUALITY_96  -> 96_000
                            },
                            width            = null,
                            height           = null,
                            contentLength    = contentLength,
                            quality          = quality.toApiValue(),
                            fps              = null,
                            qualityLabel     = null,
                            averageBitrate   = null,
                            audioQuality     = quality.toApiValue(),
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
        // ── End JioSaavn intercept ───────────────────────────────────────────

        val firstAttempt = resolvePlaybackData(videoId, playlistId, audioQuality, connectivityManager, videoMode = videoMode, context = context)

        // A geo-restricted video fails the same way on a retry — rotating a
        // guest session or re-attempting as-is can't fix a region block, so
        // don't waste the one retry on it either way.
        val isGeoRestricted = BotDetectionMitigator.isGeoError(firstAttempt.exceptionOrNull()?.message)

        if (firstAttempt.isFailure && !isGeoRestricted) {
            // visitorData rides in every request's client context independent of
            // login — cookie/dataSyncId are separate fields — so a flagged
            // visitorData can trigger bot detection on a signed-in session just
            // as it does for a guest. A bare same-identity retry reproduces the
            // exact same rejection; rotating visitorData actually changes
            // something and is what gives a signed-in user a real chance too,
            // without touching their login cookie or dataSyncId.
            val label = if (YouTube.cookie == null) "guest" else "signed-in user"
            Timber.tag(TAG).w("Playback failed for $label. Rotating session and retrying...")
            PlaybackLogManager.log(PlaybackLogLevel.BOT, "Playback failed for $label", "Triggering bot detection mitigation (rotating guest session)")
            BotDetectionMitigator.rotateGuestSession()
            val retryResult = resolvePlaybackData(videoId, playlistId, audioQuality, connectivityManager, videoMode = videoMode, context = context)
            retryResult.onSuccess { BotDetectionMitigator.notifyPlaybackSuccess() }
            return retryResult
        }

        firstAttempt.onSuccess { BotDetectionMitigator.notifyPlaybackSuccess() }
        return firstAttempt
    }

    private suspend fun resolvePlaybackData(
        videoId: String,
        playlistId: String? = null,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
        videoMode: Boolean = false,
        context: android.content.Context? = null,
    ): Result<PlaybackData> = runCatching {
        Timber.tag(logTag).d("Fetching player response for videoId: $videoId, playlistId: $playlistId")
        PlaybackLogManager.log(PlaybackLogLevel.INFO, "Resolving playback data", "Video: $videoId")
        
        // Debug: Log ALL playback attempts
        println("[PLAYBACK_DEBUG] playerResponseForPlayback called: videoId=$videoId, playlistId=$playlistId")
        // Check if this is an uploaded/privately owned track
        val isUploadedTrack = playlistId == "MLPT" || playlistId?.contains("MLPT") == true

        val isLoggedIn = YouTube.cookie != null
        Timber.tag(logTag).d("Session authentication status: ${if (isLoggedIn) "Logged in" else "Not logged in"}")

        // Signature timestamp, fetched ONLY if a client we actually try needs one, and at
        // most once per call.
        //
        // This used to run eagerly on every single playback. Getting it means downloading
        // and parsing YouTube's player JavaScript through NewPipe -- a blocking network
        // round trip plus a JS parse -- in front of every song. And MAIN_CLIENT is
        // ANDROID_VR, which declares useSignatureTimestamp = false: the value was fetched
        // and then handed to a client that does not use one, on the hot path, every time.
        //
        // Two failure modes came out of that. The cost shows up as songs taking seconds to
        // start; and when YouTube changes the player JS shape, the extraction fails on a
        // request that never needed it in the first place.
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

        // Generate PoToken ONLY if MAIN_CLIENT uses it (which it now doesn't since we use ANDROID_VR)
        var poToken: PoTokenResult? = null
        val sessionId = if (isLoggedIn) YouTube.dataSyncId else YouTube.visitorData
        if (MAIN_CLIENT.useWebPoTokens && sessionId != null) {
            Timber.tag(logTag).d("Generating PoToken for MAIN_CLIENT with sessionId")
            try {
                poToken = poTokenGenerator.getWebClientPoToken(videoId, sessionId)
                if (poToken != null) {
                    Timber.tag(logTag).d("PoToken generated successfully")
                }
            } catch (e: Exception) {
                Timber.tag(logTag).e(e, "PoToken generation failed: ${e.message}")
            }
        }

        // Try MAIN_CLIENT (ANDROID_VR) for fast stream resolution and METADATA_CLIENT (WEB_REMIX) for history tracking in parallel
        var (mainPlayerResponse, metadataResponse) = coroutineScope {
            val mainDeferred = async {
                Timber.tag(logTag).d("Attempting to get player response using MAIN_CLIENT: ${MAIN_CLIENT.clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Trying ${MAIN_CLIENT.clientName} (Main)")
                YouTube.player(videoId, playlistId, MAIN_CLIENT, signatureTimestampFor(MAIN_CLIENT), poToken?.playerRequestPoToken).getOrThrow()
            }
            val metaDeferred = async {
                if (isLoggedIn) {
                    Timber.tag(logTag).d("Fetching metadata from METADATA_CLIENT (WEB_REMIX) for authenticated tracking")
                    try {
                        // Only generate PoToken for web client metadata fetch
                        var metaPoToken: PoTokenResult? = null
                        val metaSessionId = YouTube.dataSyncId
                        if (METADATA_CLIENT.useWebPoTokens && metaSessionId != null) {
                            try {
                                metaPoToken = poTokenGenerator.getWebClientPoToken(videoId, metaSessionId)
                            } catch (e: Exception) {
                                Timber.tag(logTag).e(e, "Metadata PoToken generation failed")
                            }
                        }
                        YouTube.player(
                            videoId, playlistId, METADATA_CLIENT,
                            signatureTimestampFor(METADATA_CLIENT), metaPoToken?.playerRequestPoToken
                        ).getOrNull().also { response ->
                            Timber.tag(logTag).d("Metadata response obtained: ${response?.playabilityStatus?.status}")
                        }
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "Failed to fetch metadata from METADATA_CLIENT")
                        null
                    }
                } else {
                    null
                }
            }
            mainDeferred.await() to metaDeferred.await()
        }

        // One line with the whole picture of the main response — this is what
        // the shareable playback log needs to diagnose "works logged out,
        // fails logged in" style reports without a debugger attached.
        PlaybackLogManager.log(
            PlaybackLogLevel.DEBUG,
            "Main(${MAIN_CLIENT.clientName}) status=${mainPlayerResponse.playabilityStatus.status}" +
                " muxed=${mainPlayerResponse.streamingData?.formats?.size ?: 0}" +
                " adaptive=${mainPlayerResponse.streamingData?.adaptiveFormats?.size ?: 0}",
            "YouTube",
        )

        // Debug uploaded track response
        if (isUploadedTrack || playlistId?.contains("MLPT") == true) {
            println("[PLAYBACK_DEBUG] Main player response status: ${mainPlayerResponse.playabilityStatus.status}")
            println("[PLAYBACK_DEBUG] Playability reason: ${mainPlayerResponse.playabilityStatus.reason}")
            println("[PLAYBACK_DEBUG] Video details: title=${mainPlayerResponse.videoDetails?.title}, videoId=${mainPlayerResponse.videoDetails?.videoId}")
            println("[PLAYBACK_DEBUG] Streaming data null? ${mainPlayerResponse.streamingData == null}")
            println("[PLAYBACK_DEBUG] Adaptive formats count: ${mainPlayerResponse.streamingData?.adaptiveFormats?.size ?: 0}")
        }

        var usedAgeRestrictedClient: YouTubeClient? = null
        val wasOriginallyAgeRestricted: Boolean

        // Check if MAIN_CLIENT response indicates age-restricted.
        // NOTE: Do NOT include LOGIN_REQUIRED here — ANDROID_VR returns LOGIN_REQUIRED as a
        // bot-detection / client-not-supported signal, NOT a content age gate. Treating it as
        // age-restricted incorrectly reroutes every bot-flagged request through WEB_CREATOR
        // and causes streaming failures for logged-in users.
        val mainStatus = mainPlayerResponse.playabilityStatus.status
        val isAgeRestrictedFromResponse = mainStatus in listOf(
            "AGE_CHECK_REQUIRED",
            "AGE_VERIFICATION_REQUIRED",
            "CONTENT_CHECK_REQUIRED"
        )
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

        // If we still don't have a valid response, throw
        if (mainPlayerResponse == null) {
            throw Exception("Failed to get player response")
        }

        // Fetch audioConfig and playbackTracking from the metadata client if available (authenticated)
        // Fall back to mainPlayerResponse values if metadata fetch failed or user is not logged in
        val audioConfig = metadataResponse?.playerConfig?.audioConfig ?: mainPlayerResponse.playerConfig?.audioConfig
        val videoDetails = metadataResponse?.videoDetails ?: mainPlayerResponse.videoDetails
        val playbackTracking = metadataResponse?.playbackTracking ?: mainPlayerResponse.playbackTracking
        var format: PlayerResponse.StreamingData.Format? = null
        var streamUrl: String? = null
        var streamExpiresInSeconds: Int? = null
        var streamPlayerResponse: PlayerResponse? = null
        // Video-mode last resort: if NO client serves a muxed stream, play the
        // best audio from the first client that offered one, instead of failing.
        var audioFallbackFormat: PlayerResponse.StreamingData.Format? = null
        var audioFallbackUrl: String? = null
        var audioFallbackExpires: Int? = null
        var audioFallbackResponse: PlayerResponse? = null
        var retryMainPlayerResponse: PlayerResponse? = if (usedAgeRestrictedClient != null) mainPlayerResponse else null

        // Check current status
        val currentStatus = mainPlayerResponse.playabilityStatus.status
        var isAgeRestricted = currentStatus in listOf(
            "AGE_CHECK_REQUIRED",
            "AGE_VERIFICATION_REQUIRED",
            "CONTENT_CHECK_REQUIRED"
        )

        if (isAgeRestricted) {
            Timber.tag(logTag).d("Content is still age-restricted (status: $currentStatus), will try fallback clients")
            Timber.tag(TAG).i("Age-restricted content detected: videoId=$videoId, status=$currentStatus")
        }

        // Check if this is a privately owned track (uploaded song)
        val isPrivateTrack = mainPlayerResponse.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_PRIVATELY_OWNED_TRACK"

        // Where to enter the fallback chain.
        //   private track   -> skip to TVHTML5, which is the one that serves uploads
        //   age-restricted  -> skip MAIN_CLIENT, start at the top of the fallbacks
        //   otherwise       -> -1, meaning try MAIN_CLIENT's own streams first
        //
        // Resolved by identity, not by a literal index. The comment here used to say
        // "index 1 = TVHTML5" while index 1 was actually WEB_REMIX -- the array had been
        // reordered and the magic number silently stopped meaning what it claimed. Any
        // future reordering now moves this with it.
        var startIndex = when {
            isPrivateTrack -> STREAM_FALLBACK_CLIENTS.indexOf(TVHTML5).coerceAtLeast(0)
            isAgeRestricted -> 0
            else -> -1
        }

        // Video mode speed-up: the sequential fallback walk costs one player
        // round-trip per client, and most fallback clients are audio-only — the
        // first muxed-capable client can be several hops in (measured ~5s+).
        // When the main client has no muxed format, probe the likely-muxed
        // fallbacks CONCURRENTLY (one round-trip wall-clock) and enter the
        // regular sequential loop directly at the best one. The loop keeps its
        // own onward fallback if that client's stream later fails validation.
        // Only the four native muxed-capable clients are probed: a wide fan-out
        // (7+) of simultaneous player calls per video switch makes YouTube's
        // bot heuristics throttle the whole session intermittently, which is
        // far worse for load reliability than one extra sequential hop.
        if (videoMode && startIndex == -1 &&
            selectMuxedVideoFormat(mainPlayerResponse.streamingData?.formats) == null
        ) {
            val probeCandidates = listOf(1, 2, 8, 9).filter { it < STREAM_FALLBACK_CLIENTS.size }
            val probeResults: Map<Int, List<PlayerResponse.StreamingData.Format>?> =
                kotlinx.coroutines.coroutineScope {
                    probeCandidates.associateWith { idx ->
                        async {
                            try {
                                withTimeoutOrNull(8_000L) {
                                    YouTube.player(
                                        videoId,
                                        playlistId,
                                        STREAM_FALLBACK_CLIENTS[idx],
                                        signatureTimestamp = null,
                                        poToken = null,
                                    ).getOrNull()?.streamingData?.formats
                                }
                            } catch (e: Exception) {
                                Timber.tag(logTag).d("videoMode probe failed for ${STREAM_FALLBACK_CLIENTS[idx].clientName}: ${e.message}")
                                null
                            }
                        }
                    }.mapValues { it.value.await() }
                }
            val winner = probeResults.entries
                .firstOrNull { (_, formats) -> formats != null && selectMuxedVideoFormat(formats) != null }
                ?.key
            if (winner != null) {
                Timber.tag(logTag).d("videoMode probe: entering chain at ${STREAM_FALLBACK_CLIENTS[winner].clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Video probe → ${STREAM_FALLBACK_CLIENTS[winner].clientName}", "YouTube")
                startIndex = winner
            } else {
                PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Video probe found no muxed client; walking chain", "YouTube")
            }
        }

        for (clientIndex in (startIndex until STREAM_FALLBACK_CLIENTS.size)) {
            // reset for each client
            format = null
            streamUrl = null
            streamExpiresInSeconds = null

            // decide which client to use for streams and load its player response
            val client: YouTubeClient
            if (clientIndex == -1) {
                // try with streams from main client first (use retry response if available)
                client = MAIN_CLIENT
                streamPlayerResponse = retryMainPlayerResponse ?: mainPlayerResponse
                Timber.tag(logTag).d("Trying stream from MAIN_CLIENT: ${client.clientName}")
            } else {
                // after main client use fallback clients
                client = STREAM_FALLBACK_CLIENTS[clientIndex]
                Timber.tag(logTag).d("Trying fallback client ${clientIndex + 1}/${STREAM_FALLBACK_CLIENTS.size}: ${client.clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.DEBUG, "Trying fallback [${clientIndex + 1}/${STREAM_FALLBACK_CLIENTS.size}]", client.clientName)

                if (client.loginRequired && !isLoggedIn && YouTube.cookie == null) {
                    // skip client if it requires login but user is not logged in
                    Timber.tag(logTag).d("Skipping client ${client.clientName} - requires login but user is not logged in")
                    continue
                }

                // Lazily generate PoToken for fallback web clients if we haven't already
                if (client.useWebPoTokens && poToken == null && sessionId != null) {
                    Timber.tag(logTag).d("Lazily generating PoToken for fallback web client: ${client.clientName}")
                    try {
                        poToken = poTokenGenerator.getWebClientPoToken(videoId, sessionId)
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "Lazy PoToken generation failed")
                    }
                }

                Timber.tag(logTag).d("Fetching player response for fallback client: ${client.clientName}")
                // Only pass poToken for clients that support it
                val clientPoToken = if (client.useWebPoTokens) poToken?.playerRequestPoToken else null
                // Skip signature timestamp for age-restricted (faster), use it for normal content
                val clientSigTimestamp =
                    if (wasOriginallyAgeRestricted) null else signatureTimestampFor(client)
                streamPlayerResponse =
                    YouTube.player(videoId, playlistId, client, clientSigTimestamp, clientPoToken).getOrNull()
            }

            // process current client response
            if (streamPlayerResponse?.playabilityStatus?.status == "OK") {
                Timber.tag(logTag).d("Player response status OK for client: ${if (clientIndex == -1) MAIN_CLIENT.clientName else STREAM_FALLBACK_CLIENTS[clientIndex].clientName}")
                PlaybackLogManager.log(PlaybackLogLevel.INFO, "Player response OK", if (clientIndex == -1) MAIN_CLIENT.clientName else STREAM_FALLBACK_CLIENTS[clientIndex].clientName)

                // Check if formats have direct URLs (no signatureCipher needed)
                val hasDirectUrls = streamPlayerResponse.streamingData?.adaptiveFormats
                    ?.any { !it.url.isNullOrEmpty() } == true
                val hasSignatureCipher = streamPlayerResponse.streamingData?.adaptiveFormats
                    ?.any { !it.signatureCipher.isNullOrEmpty() || !it.cipher.isNullOrEmpty() } == true

                Timber.tag(logTag).d("URL check: hasDirectUrls=$hasDirectUrls, hasSignatureCipher=$hasSignatureCipher")

                // Skip NewPipe - use direct URLs or custom cipher in findUrlOrNull
                val responseToUse = streamPlayerResponse

                if (videoMode) {
                    // Full-video playback: muxed or nothing on THIS client. Several
                    // fallback clients are adaptive-only (audio formats, no muxed
                    // video), and accepting their audio here permanently downgraded
                    // video mode to audio-only. Keep walking the chain until a
                    // client that still serves muxed streams is found; the first
                    // client's audio is stashed as a last-resort fallback below.
                    format = selectMuxedVideoFormat(
                        responseToUse.streamingData?.formats,
                        maxHeightCap = context?.let { videoQualityCap(it) } ?: 1080,
                    )
                    if (format == null) {
                        Timber.tag(logTag).d("videoMode: no muxed format on ${client.clientName}, trying next client")
                        if (audioFallbackUrl == null) {
                            findFormat(responseToUse, audioQuality, connectivityManager)
                                ?.let { audioFormat ->
                                    findUrlOrNull(audioFormat, videoId, responseToUse, skipNewPipe = wasOriginallyAgeRestricted)
                                        ?.let { audioUrl ->
                                            audioFallbackFormat = audioFormat
                                            audioFallbackUrl = audioUrl
                                            audioFallbackExpires = responseToUse.streamingData?.expiresInSeconds
                                            audioFallbackResponse = responseToUse
                                        }
                                }
                        }
                        continue
                    }
                } else {
                    format = findFormat(
                        responseToUse,
                        audioQuality,
                        connectivityManager,
                    )
                }

                if (format == null) {
                    Timber.tag(logTag).d("No suitable format found for client: ${if (clientIndex == -1) MAIN_CLIENT.clientName else STREAM_FALLBACK_CLIENTS[clientIndex].clientName}")
                    continue
                }

                Timber.tag(logTag).d("Format found: ${format.mimeType}, bitrate: ${format.bitrate}")

                streamUrl = findUrlOrNull(format, videoId, responseToUse, skipNewPipe = wasOriginallyAgeRestricted)
                if (streamUrl == null) {
                    Timber.tag(logTag).d("Stream URL not found for format")
                    continue
                }

                // Apply n-transform for throttle parameter handling
                val currentClient = if (clientIndex == -1) {
                    usedAgeRestrictedClient ?: MAIN_CLIENT
                } else {
                    STREAM_FALLBACK_CLIENTS[clientIndex]
                }

                // Check if this is a privately owned track
                val isPrivatelyOwnedTrack = streamPlayerResponse.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_PRIVATELY_OWNED_TRACK"

                // Apply n-transform FIRST for web clients (main branch order - critical!)
                if (currentClient.useWebPoTokens) {
                    try {
                        Timber.tag(logTag).d("Applying n-transform to stream URL for ${currentClient.clientName}")
                        val transformed = EjsNTransformSolver.transformNParamInUrl(streamUrl!!)
                        if (transformed != streamUrl) {
                            streamUrl = transformed
                            Timber.tag(logTag).d("N-transform applied successfully")
                        }
                    } catch (e: Exception) {
                        Timber.tag(logTag).e(e, "N-transform failed: ${e.message}")
                    }
                }

                // Apply PoToken SECOND (after n-transform - main branch order)
                // Note: pot token is base64 - do NOT Uri.encode it (breaks validation)
                if (currentClient.useWebPoTokens && poToken?.streamingDataPoToken != null) {
                    Timber.tag(logTag).d("Appending pot= parameter to stream URL")
                    val separator = if ("?" in streamUrl!!) "&" else "?"
                    streamUrl = "${streamUrl}${separator}pot=${poToken.streamingDataPoToken}"
                }

                streamExpiresInSeconds = streamPlayerResponse.streamingData?.expiresInSeconds
                if (streamExpiresInSeconds == null) {
                    // NOT a reason to discard a stream that resolved. `continue` here
                    // threw away a working format + URL over a missing housekeeping
                    // field, walked the rest of the fallback clients, and — when the
                    // field is absent from every response, which is what a YouTube-side
                    // shape change looks like — ended with "Missing stream expire time"
                    // and no playback at all.
                    Timber.tag(logTag).w("Stream expiration time not found — defaulting to ${DEFAULT_STREAM_EXPIRY_SECONDS}s")
                    streamExpiresInSeconds = DEFAULT_STREAM_EXPIRY_SECONDS
                }

                Timber.tag(logTag).d("Stream expires in: $streamExpiresInSeconds seconds")

                // Debug: Log URL host and pot token for debugging
                val urlHost = try { java.net.URL(streamUrl).host } catch (e: Exception) { "unknown" }
                Timber.tag(logTag).d("Stream URL host: $urlHost, pot length: ${poToken?.streamingDataPoToken?.length ?: 0}")

                // Check if this is a privately owned track (uploaded song)
                val isPrivatelyOwned = streamPlayerResponse.videoDetails?.musicVideoType == "MUSIC_VIDEO_TYPE_PRIVATELY_OWNED_TRACK"

                if (clientIndex == STREAM_FALLBACK_CLIENTS.size - 1 || isPrivatelyOwned) {
                    /** skip [validateStatus] for last client or private tracks */
                    if (isPrivatelyOwned) {
                        Timber.tag(logTag).d("Skipping validation for privately owned track: ${currentClient.clientName}")
                        println("[PLAYBACK_DEBUG] Using stream without validation for PRIVATELY_OWNED_TRACK")
                    } else {
                        Timber.tag(logTag).d("Using last fallback client without validation: ${STREAM_FALLBACK_CLIENTS[clientIndex].clientName}")
                    }
                    Timber.tag(TAG).i("Playback: client=${currentClient.clientName}, videoId=$videoId, private=$isPrivatelyOwned")
                    break
                }

                if (validateStatus(streamUrl!!)) {
                    // working stream found
                    Timber.tag(logTag).d("Stream validated successfully with client: ${currentClient.clientName}")
                    PlaybackLogManager.log(PlaybackLogLevel.INFO, "Stream validated", currentClient.clientName)
                    // Log for release builds
                    Timber.tag(TAG).i("Playback: client=${currentClient.clientName}, videoId=$videoId")
                    break
                } else {
                    Timber.tag(logTag).d("Stream validation failed for client: ${currentClient.clientName}")

                    // For web clients: try alternate n-transform and re-validate (Zemer approach)
                    if (currentClient.useWebPoTokens) {
                        var nTransformWorked = false

                        // Try CipherDeobfuscator n-transform
                        try {
                            val nTransformed = CipherDeobfuscator.transformNParamInUrl(streamUrl!!)
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

                        if (nTransformWorked) break
                    }
                }
            } else {
                val status = streamPlayerResponse?.playabilityStatus?.status ?: "Unknown"
                val reason = streamPlayerResponse?.playabilityStatus?.reason ?: "No reason"
                Timber.tag(logTag).d("Player response status not OK: $status, reason: $reason")
                PlaybackLogManager.log(PlaybackLogLevel.WARNING, "Client failed: ${client.clientName}", "$status: $reason")
                
                // Restore original Timber log for Logcat
                Timber.tag(logTag).d("Player response status not OK: ${streamPlayerResponse?.playabilityStatus?.status}, reason: ${streamPlayerResponse?.playabilityStatus?.reason}")
            }
        }

        if (streamPlayerResponse == null) {
            Timber.tag(logTag).e("Bad stream player response - all clients failed")
            if (isUploadedTrack) {
                println("[PLAYBACK_DEBUG] FAILURE: All clients failed for uploaded track videoId=$videoId")
            }
            throw Exception("Bad stream player response")
        }

        if (streamPlayerResponse.playabilityStatus.status != "OK") {
            val errorReason = streamPlayerResponse.playabilityStatus.reason
            Timber.tag(logTag).e("Playability status not OK: $errorReason")
            if (isUploadedTrack) {
                println("[PLAYBACK_DEBUG] FAILURE: Playability not OK for uploaded track - status=${streamPlayerResponse.playabilityStatus.status}, reason=$errorReason")
            }
            throw PlaybackException(
                errorReason,
                null,
                PlaybackException.ERROR_CODE_REMOTE_ERROR
            )
        }

        // Checked before streamExpiresInSeconds: format/streamUrl not being set is
        // WHY streamExpiresInSeconds never got set either (all three are reset to
        // null at the top of every loop iteration, and expiresInSeconds is only
        // assigned after format+streamUrl already succeeded for that client) — the
        // old order reported "Missing stream expire time" for every failure, even
        // ones where the real cause was no usable audio format or a dead stream
        // URL on the last-tried client. Surfacing the real reason here.
        if (format == null && videoMode && audioFallbackUrl != null) {
            // No client could serve a muxed stream (all adaptive-only) — play the
            // stashed audio so playback never dies just because video is missing.
            Timber.tag(logTag).w("videoMode: no muxed stream on any client, using audio fallback")
            format = audioFallbackFormat
            streamUrl = audioFallbackUrl
            streamExpiresInSeconds = audioFallbackExpires
            streamPlayerResponse = audioFallbackResponse
        }

        if (format == null) {
            Timber.tag(logTag).e("Could not find format")
            throw Exception("Could not find format")
        }

        if (streamUrl == null) {
            Timber.tag(logTag).e("Could not find stream url")
            throw Exception("Could not find stream url")
        }

        // format + streamUrl came from a response that DID succeed, so a missing
        // expiry here is the genuinely rare case (a client omitting the field) —
        // default rather than throw away an otherwise-playable stream.
        val expiresInSeconds = streamExpiresInSeconds ?: DEFAULT_STREAM_EXPIRY_SECONDS

        Timber.tag(logTag).d("Successfully obtained playback data with format: ${format.mimeType}, bitrate: ${format.bitrate}")
        if (isUploadedTrack) {
            println("[PLAYBACK_DEBUG] SUCCESS: Got playback data for uploaded track - format=${format.mimeType}, streamUrl=${streamUrl?.take(100)}...")
        }
        PlaybackData(
            audioConfig,
            videoDetails,
            playbackTracking,
            format,
            streamUrl,
            expiresInSeconds,
        )
    }.onFailure { e ->
        Timber.tag(logTag).e(e, "Playback resolution failed")
        PlaybackLogManager.log(PlaybackLogLevel.ERROR, "Playback failed", "${e::class.simpleName}: ${e.message}")
        
        // Restore original println for Logcat
        println("[PLAYBACK_DEBUG] EXCEPTION during playback for videoId=$videoId: ${e::class.simpleName}: ${e.message}")
        e.printStackTrace()
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

    private fun findFormat(
        playerResponse: PlayerResponse,
        audioQuality: AudioQuality,
        connectivityManager: ConnectivityManager,
    ): PlayerResponse.StreamingData.Format? {
        Timber.tag(logTag).d("Finding format with audioQuality: $audioQuality, network metered: ${connectivityManager.isActiveNetworkMetered}")

        val audioFormats = playerResponse.streamingData?.adaptiveFormats
            ?.filter { it.isAudio }
            .orEmpty()
        // Some videos (auto-dubs, certain regional content) only serve non-original
        // audio tracks — requiring isOriginal made those permanently unplayable
        // (format=null -> thrown as a generic Exception -> ExoPlayer buckets it as
        // ERROR_CODE_IO_UNSPECIFIED and retries the same doomed resolve forever).
        val original = audioFormats.filter { it.isOriginal }
        val candidates = original.ifEmpty { audioFormats }

        val format = candidates
            .maxByOrNull {
                it.bitrate * when (audioQuality) {
                    AudioQuality.AUTO -> if (connectivityManager.isActiveNetworkMetered) -1 else 1
                    AudioQuality.HIGH -> 1
                    AudioQuality.LOW -> -1
                } + (if (it.mimeType.startsWith("audio/webm")) 10240 else 0) // prefer opus stream
            }

        if (format != null) {
            Timber.tag(logTag).d("Selected format: ${format.mimeType}, bitrate: ${format.bitrate}")
        } else {
            Timber.tag(logTag).d("No suitable audio format found")
        }

        return format
    }
    /**
     * Checks if the stream url returns a successful status.
     * If this returns true the url is likely to work.
     * If this returns false the url might cause an error during playback.
     */
    private fun validateStatus(url: String): Boolean {
        Timber.tag(logTag).d("Validating stream URL status")
        try {
            val requestBuilder = okhttp3.Request.Builder()
                .head()
                .url(url)
                .header("User-Agent", YouTubeClient.USER_AGENT_WEB)

            // Do NOT add Cookie header — googlevideo.com CDN rejects account cookies with 403.
            // Stream URLs are already authenticated via signed URL parameters.

            val response = httpClient.newCall(requestBuilder.build()).execute()
            val isSuccessful = response.isSuccessful
            Timber.tag(logTag).d("Stream URL validation result: ${if (isSuccessful) "Success" else "Failed"} (${response.code})")
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

        // First check if format already has a URL
        if (!format.url.isNullOrEmpty()) {
            Timber.tag(logTag).d("Using URL from format directly")
            return format.url
        }

        // Try custom cipher deobfuscation for signatureCipher formats
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

        // Skip NewPipe for age-restricted content
        if (skipNewPipe) {
            Timber.tag(logTag).d("Skipping NewPipe methods for age-restricted content")
            return null
        }

        // Try to get URL using NewPipeExtractor signature deobfuscation
        val deobfuscatedUrl = NewPipeExtractor.getStreamUrl(format, videoId)
        if (deobfuscatedUrl != null) {
            Timber.tag(logTag).d("Stream URL obtained via NewPipe deobfuscation")
            return deobfuscatedUrl
        }

        // Fallback: try to get URL from StreamInfo
        Timber.tag(logTag).d("Trying StreamInfo fallback for URL")
        val streamUrls = YouTube.getNewPipeStreamUrls(videoId)
        if (streamUrls.isNotEmpty()) {
            val streamUrl = streamUrls.find { it.first == format.itag }?.second
            if (streamUrl != null) {
                Timber.tag(logTag).d("Stream URL obtained from StreamInfo")
                return streamUrl
            }

            // If exact itag not found, try to find any audio stream
            val audioStream = streamUrls.find { urlPair ->
                playerResponse.streamingData?.adaptiveFormats?.any {
                    it.itag == urlPair.first && it.isAudio
                } == true
            }?.second

            if (audioStream != null) {
                Timber.tag(logTag).d("Audio stream URL obtained from StreamInfo (different itag)")
                return audioStream
            }
        }

        Timber.tag(logTag).e("Failed to get stream URL")
        return null
    }

    fun forceRefreshForVideo(videoId: String) {
        Timber.tag(logTag).d("Force refreshing for videoId: $videoId")
    }
}
