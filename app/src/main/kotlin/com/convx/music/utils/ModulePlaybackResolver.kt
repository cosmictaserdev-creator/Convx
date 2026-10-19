package com.convx.music.utils

import android.media.MediaCodecList
import com.convx.music.constants.AudioQuality
import com.music.spine.ModuleManager
import com.music.spine.ModuleSearchResult
import com.music.spine.ModuleStreamResponse
import com.music.spine.SpineModule
import com.music.innertube.models.response.PlayerResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.net.URI
import java.text.Normalizer
import java.util.Locale

/** Bounded concurrent module lookup; preferences decide between sources that answer in time. */
internal object ModulePlaybackResolver {
    data class Match(
        val module: SpineModule,
        val track: ModuleSearchResult,
        val stream: ModuleStreamResponse,
        val mimeType: String,
        val lossless: Boolean,
        val atmos: Boolean,
        val priority: Int,
    )

    private data class LookupRequest(
        val enabledOrder: List<String>,
        val settings: Map<String, Map<String, String>>,
        val title: String,
        val artists: List<String>,
        val duration: Int?,
        val query: String,
        val audioQuality: AudioQuality,
        val allowLossless: Boolean,
        val forceSelectedQuality: Boolean,
    )

    private val supportsAtmos by lazy {
        runCatching {
            MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.any { codec ->
                !codec.isEncoder && codec.supportedTypes.any { it.contains("eac3", true) }
            }
        }.getOrDefault(false)
    }
    private val gate = Semaphore(3)

    suspend fun resolve(
        sourceUrls: List<String>,
        enabledOrder: List<String>,
        settings: Map<String, Map<String, String>>,
        title: String,
        artists: List<String>,
        duration: Int?,
        query: String,
        audioQuality: AudioQuality,
        allowLossless: Boolean,
        forceSelectedQuality: Boolean,
    ): Match? = withTimeoutOrNull(15_000L) {
        supervisorScope {
            val request = LookupRequest(
                enabledOrder = enabledOrder,
                settings = settings,
                title = title,
                artists = artists,
                duration = duration,
                query = query,
                audioQuality = audioQuality,
                allowLossless = allowLossless,
                forceSelectedQuality = forceSelectedQuality,
            )
            val deadline = System.nanoTime() / 1_000_000 + 15_000L
            val answers = Channel<Match>(Channel.UNLIMITED)
            val jobs = sourceUrls.distinct().map { source ->
                launch(Dispatchers.IO) {
                    val modules = ModuleManager.shared.fetchIndex(source).getOrNull().orEmpty()
                        .filter { it.id in enabledOrder }
                        .sortedBy { enabledOrder.indexOf(it.id) }
                    supervisorScope {
                        modules.forEach { module ->
                            launch {
                                try {
                                    val match = gate.withPermit { findModuleMatch(module, source, request) }
                                    if (match != null) answers.send(match)
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Timber.tag("ModulePlayback").w("Module %s failed: %s", module.id, e.message)
                                }
                            }
                        }
                    }
                }
            }
            val completion = launch { jobs.joinAll(); answers.close() }
            try {
                val first = answers.receiveCatching().getOrNull() ?: return@supervisorScope null
                if (first.priority == 0) return@supervisorScope first
                val candidates = mutableListOf(first)
                val grace = minOf(400L, deadline - System.nanoTime() / 1_000_000 - 20L)
                if (grace > 0) withTimeoutOrNull(grace) {
                    for (answer in answers) {
                        candidates.add(answer)
                        if (answer.priority == 0) break
                    }
                }
                candidates.minByOrNull { it.priority }
            } finally {
                jobs.forEach { it.cancel() }
                completion.cancel()
                answers.cancel()
            }
        }
    }

    private suspend fun findModuleMatch(
        module: SpineModule,
        source: String,
        request: LookupRequest,
    ): Match? {
        val loaded = ModuleManager.shared.loadModule(module) { source.substringBeforeLast('/') }
            .getOrThrow()
        val moduleSettings = request.settings[module.id]
        val configured = loaded.manifest.defaults + moduleSettings.orEmpty().filterValues { it.isNotBlank() }
        val selected = selectedQuality(
            audioQuality = request.audioQuality,
            forceSelectedQuality = request.forceSelectedQuality,
            allowLossless = request.allowLossless,
            moduleSettings = moduleSettings,
            configuredSettings = configured,
        )
        val wanted = qualityForDevice(loaded, compatibleQuality(loaded, selected), supportsAtmos)
        fun settingsFor(tier: String) = configured + mapOf(
            "quality" to tier,
            "dolbyAtmos" to (wantsAtmos(tier) && supportsAtmos && !request.forceSelectedQuality).toString(),
        )

        val candidates = findCandidates(
            loaded = loaded,
            query = request.query,
            title = request.title,
            artists = request.artists,
            duration = request.duration,
            settings = settingsFor(wanted),
        )
        val tiers = (listOf(wanted) +
            (if (wantsAtmos(wanted)) listOf(stereoQuality(loaded)) else emptyList()) +
            (if (wanted != "LOW") listOf("HIGH") else emptyList())).distinct()

        for (track in candidates) {
            for (tier in tiers) {
                val stream = track.streamUrl?.takeIf(::validUrl)?.let {
                    ModuleStreamResponse(streamUrl = it, track = com.music.spine.ModuleStreamTrack(
                        id = track.id, format = track.format, audioQuality = track.audioQuality,
                    ))
                } ?: ModuleManager.shared.getStreamUrl(
                    loaded, track.id, settingsFor(tier), hostTier(tier),
                ).getOrNull() ?: continue
                val url = stream.streamUrl?.takeIf(::validUrl) ?: continue
                val mime = mimeType(stream, track, url)
                val atmos = isAtmos(stream, mime)
                if (atmos && (!supportsAtmos || request.forceSelectedQuality)) continue

                val lossless = mime.contains("flac", true) || mime.contains("alac", true)
                if (lossless && (!request.allowLossless || request.forceSelectedQuality)) continue

                return Match(
                    module = module,
                    track = track,
                    stream = stream,
                    mimeType = mime,
                    lossless = lossless,
                    atmos = atmos,
                    priority = request.enabledOrder.indexOf(module.id),
                )
            }
        }
        return null
    }

    private fun selectedQuality(
        audioQuality: AudioQuality,
        forceSelectedQuality: Boolean,
        allowLossless: Boolean,
        moduleSettings: Map<String, String>?,
        configuredSettings: Map<String, String>,
    ): String {
        if (forceSelectedQuality || !allowLossless) {
            return if (audioQuality == AudioQuality.LOW) "LOW" else "HIGH"
        }

        if (!moduleSettings?.get("quality").isNullOrBlank()) {
            return configuredSettings.getValue("quality")
        }
        if (audioQuality == AudioQuality.LOW) return "LOW"

        return configuredSettings["quality"]
            ?.takeIf { it.isNotBlank() }
            ?: "LOSSLESS"
    }

    private suspend fun findCandidates(
        loaded: ModuleManager.LoadedModule,
        query: String,
        title: String,
        artists: List<String>,
        duration: Int?,
        settings: Map<String, String>,
    ): List<ModuleSearchResult> {
        var tracks = ModuleManager.shared.searchTracks(loaded, query, 8, settings)
            .getOrThrow().tracks
        if (tracks.isEmpty() && artists.isNotEmpty()) {
            tracks = ModuleManager.shared.searchTracks(
                loaded,
                "${artists.first()} $title",
                8,
                settings,
            ).getOrThrow().tracks
        }

        return tracks
            .filter { matches(it, title, artists, duration) }
            .sortedBy { kotlin.math.abs(it.duration - (duration ?: it.duration)) }
            .take(2)
    }

    private fun matches(track: ModuleSearchResult, title: String, artists: List<String>, duration: Int?): Boolean {
        val requested = normalize(title)
        if (requested.isEmpty() || normalize(track.title) != requested) return false
        if (artists.isNotEmpty() && artists.none { artist ->
            val identity = normalize(artist)
            identity.isNotEmpty() && (" " + normalize(track.artist) + " ").contains(" $identity ")
        }) return false
        return duration == null || duration <= 0 || track.duration <= 0 || kotlin.math.abs(track.duration - duration) <= 10
    }

    private fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFKD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)
        .replace(Regex("\\s*[\\[(](?:feat\\.?|ft\\.?|featuring)\\s+[^\\])]*[\\])]"), "")
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ").trim()

    private fun validUrl(value: String): Boolean = runCatching {
        val uri = URI(value)
        uri.scheme in setOf("https", "http") && !uri.host.isNullOrBlank() &&
            !uri.path.orEmpty().endsWith("/undefined") && !uri.path.orEmpty().endsWith("/null")
    }.getOrDefault(false)

    internal fun mimeType(stream: ModuleStreamResponse, track: ModuleSearchResult, url: String): String {
        stream.track?.mimeType?.takeIf { it.startsWith("audio/", true) }?.let { return it }
        stream.mimeType?.takeIf { it.startsWith("audio/", true) }?.let { return it }
        val resolved = listOfNotNull(stream.track?.format, stream.track?.codec, stream.track?.audioQuality)
        val description = (if (resolved.any { it.isNotBlank() }) resolved else listOf(track.audioQuality, track.format))
            .plus(URI(url).path)
            .joinToString(" ").uppercase(Locale.ROOT)
        return when {
            "EAC3" in description || "EC-3" in description || "ATMOS" in description -> "audio/mp4; codecs=\"ec-3\""
            "ALAC" in description -> "audio/mp4; codecs=\"alac\""
            "FLAC" in description || "LOSSLESS" in description || "HIRES" in description -> "audio/flac"
            "OPUS" in description -> "audio/ogg; codecs=\"opus\""
            "AAC" in description || ".M4A" in description -> "audio/mp4"
            else -> "audio/mpeg"
        }
    }

    internal fun isAtmos(stream: ModuleStreamResponse, mime: String): Boolean =
        listOf(mime, stream.track?.codec, stream.track?.audioQuality).any {
            it?.let { value -> value.contains("EAC3", true) || value.contains("EC-3", true) || value.contains("ATMOS", true) } == true
        } || stream.track?.audioModes.orEmpty().any { it.contains("ATMOS", true) }

    private fun wantsAtmos(quality: String) = quality.uppercase(Locale.ROOT).let {
        it == "MAX" || it.contains("DOLBY") || it.contains("ATMOS") || it.contains("EAC3")
    }

    internal fun hostTier(quality: String) = when (quality.uppercase(Locale.ROOT)) {
        "LOW" -> "LOW"
        "HIGH" -> "HIGH"
        else -> "LOSSLESS"
    }

    private fun stereoQuality(loaded: ModuleManager.LoadedModule) = compatibleQuality(loaded, "HIRES")

    internal fun qualityForDevice(loaded: ModuleManager.LoadedModule, quality: String, supportsAtmos: Boolean) =
        if (wantsAtmos(quality) && !supportsAtmos) stereoQuality(loaded) else compatibleQuality(loaded, quality)

    private fun compatibleQuality(loaded: ModuleManager.LoadedModule, quality: String): String {
        val options = loaded.manifest.settings["quality"]?.options?.map { it.value }.orEmpty()
        if (quality in options || options.isEmpty()) return quality
        return when (quality.uppercase(Locale.ROOT)) {
            "HIRES", "HI_RES" -> listOf("HI_RES", "HIRES")
            "LOSSLESS", "CD" -> listOf("CD", "LOSSLESS")
            "EAC3_JOC", "MAX", "DOLBY_ATMOS" -> listOf("HI_RES_DOLBY", "MAX", "EAC3_JOC")
            else -> listOf(quality)
        }.firstOrNull { it in options } ?: quality
    }

    fun Match.playbackData(meta: PlayerResponse?): YTPlayerUtils.PlaybackData {
        val url = requireNotNull(stream.streamUrl)
        val tier = when { atmos -> "DOLBY_ATMOS"; lossless -> "LOSSLESS"; else -> stream.track?.audioQuality ?: "HIGH" }
        val rate = stream.track?.sampleRate?.let { if (it < 1000) (it * 1000).toInt() else it.toInt() }
        val bitrate = stream.track?.bitrate?.let { if (it < 10_000) it * 1000 else it }
            ?: when { atmos -> 768_000; lossless -> 1_411_000; else -> 320_000 }
        return YTPlayerUtils.PlaybackData(
            audioConfig = meta?.playerConfig?.audioConfig,
            videoDetails = meta?.videoDetails,
            playbackTracking = meta?.playbackTracking,
            format = PlayerResponse.StreamingData.Format(
                itag = when { atmos -> 9997; lossless -> 9998; else -> 9996 },
                url = url,
                mimeType = mimeType,
                bitrate = bitrate,
                width = null,
                height = null,
                contentLength = null,
                quality = tier,
                fps = null,
                qualityLabel = null,
                averageBitrate = null,
                audioQuality = tier,
                approxDurationMs = track.duration.takeIf { it > 0 }?.let { (it * 1000L).toString() },
                audioSampleRate = rate,
                audioChannels = null,
                loudnessDb = null,
                lastModified = null,
                signatureCipher = null,
                cipher = null,
                audioTrack = null,
            ),
            streamUrl = url,
            // The module contract supplies no expiry. Keep service caching conservative.
            streamExpiresInSeconds = stream.expiresAt?.let {
                (it - System.currentTimeMillis() / 1000 - 10).coerceIn(0L, 30L).toInt()
            } ?: 30,
            isSpineStream = true,
            isDashStream = stream.streamType.equals("dash", true) || URI(url).path.orEmpty().endsWith(".mpd", true),
            isHlsStream = stream.streamType.equals("hls", true) || stream.mimeType.orEmpty().contains("mpegurl", true) ||
                URI(url).path.orEmpty().endsWith(".m3u8", true),
        )
    }
}
