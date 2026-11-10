package com.music.innertube

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackAudioStream
import com.convx.music.playback.PlaybackRequestTrack
import com.music.innertube.models.SongItem
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_CREATOR
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_43_32
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_61_48
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_65_10
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_NO_AUTH
import com.music.innertube.models.YouTubeClient.Companion.IPADOS
import com.music.innertube.models.YouTubeClient.Companion.IOS
import com.music.innertube.models.YouTubeClient.Companion.MOBILE
import com.music.innertube.models.YouTubeClient.Companion.TVHTML5
import com.music.innertube.models.YouTubeClient.Companion.TVHTML5_SIMPLY_EMBEDDED_PLAYER
import com.music.innertube.models.YouTubeClient.Companion.VISIONOS
import com.music.innertube.models.YouTubeClient.Companion.WEB
import com.music.innertube.models.YouTubeClient.Companion.WEB_CREATOR
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.music.innertube.models.response.PlayerResponse
import kotlinx.coroutines.CancellationException

/** Prefers range-readable streams and retains a progressive fallback for engines that can stream without seeking. */
class InnertubeAudioStreamResolver(
    private val repository: InnertubeRepository,
    private val webPoTokenProvider: InnertubeWebPoTokenProvider = NoOpInnertubeWebPoTokenProvider,
) : AudioStreamResolver {
    override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
        val expectedTrackDurationMs = track.durationMs
        val direct = resolveDirect(track.id, expectedTrackDurationMs)
        val directStream = direct.getOrNull()
        val directFailure = direct.exceptionOrNull()
        val shouldFindFullTrack =
            (expectedTrackDurationMs == null && directStream?.durationMs?.let { it in 1L..SHORT_TRACK_THRESHOLD_MS } == true) ||
                directFailure?.message?.contains("audio previews shorter", ignoreCase = true) == true
        if (!shouldFindFullTrack) return direct

        val title = track.title.trim()
        val artist = track.artists.firstOrNull(String::isNotBlank)?.trim().orEmpty()
        if (title.isEmpty() || artist.isEmpty()) return direct

        try {
            val search = repository.searchSummary("$title $artist")
            val songs = search.getOrElse { error ->
                if (error is CancellationException) throw error
                return direct
            }.summaries.flatMap { it.items }.filterIsInstance<SongItem>()
            val shortDurationMs = directStream?.durationMs
            val alternatives = songs.asSequence()
                .filter { it.id.isNotBlank() }
                .filter { it.matchesTrackMetadata(title, track.artists) }
                .mapNotNull { song ->
                    val candidateDurationMs = song.duration?.takeIf { it > 0 }?.toLong()?.times(MILLIS_PER_SECOND)
                        ?: return@mapNotNull null
                    if (expectedTrackDurationMs?.let { !candidateDurationMs.matchesExpectedDuration(it) } == true) {
                        return@mapNotNull null
                    }
                    if (expectedTrackDurationMs == null && shortDurationMs != null &&
                        candidateDurationMs <= shortDurationMs + ALTERNATE_TRACK_MIN_GAIN_MS
                    ) {
                        return@mapNotNull null
                    }
                    song to candidateDurationMs
                }
                .sortedBy { (_, candidateDurationMs) ->
                    expectedTrackDurationMs?.let { kotlin.math.abs(candidateDurationMs - it) } ?: candidateDurationMs
                }
                .take(MAX_ALTERNATE_TRACKS)
                .toList()

            for ((song, candidateDurationMs) in alternatives) {
                val alternative = resolveDirect(song.id, candidateDurationMs)
                if (alternative.isSuccess) return alternative
                val error = alternative.exceptionOrNull()
                if (error is CancellationException) throw error
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            // Keep the directly resolved result usable when search is unavailable.
        }
        return direct
    }

    private suspend fun resolveDirect(trackId: String, durationMs: Long?): Result<PlaybackAudioStream> {
        try {
            val clients = listOf(
                ANDROID_VR_1_43_32,
                VISIONOS,
                IOS,
                ANDROID_VR_1_61_48,
                ANDROID_VR_1_65_10,
                WEB_REMIX,
                TVHTML5_SIMPLY_EMBEDDED_PLAYER,
                TVHTML5,
                ANDROID_CREATOR,
                IPADOS,
                ANDROID_VR_NO_AUTH,
                MOBILE,
                WEB,
                WEB_CREATOR,
            )
            val unresolvedPlayers = mutableListOf<UnresolvedPlayer>()
            var progressiveFallback: PlaybackAudioStream? = null
            var shorterPreviewFound = false
            var webPoTokenAttempted = false
            var webPoTokens: InnertubeWebPoTokens? = null

            for (client in clients) {
                if (client.loginRequired && repository.cookie.isNullOrBlank()) continue
                if (client.useWebPoTokens && !webPoTokenAttempted) {
                    webPoTokenAttempted = true
                    val visitorData = repository.visitorData?.takeIf(String::isNotBlank)
                        ?: repository.refreshVisitorData().fold(
                            onSuccess = { it },
                            onFailure = { error ->
                                if (error is CancellationException) throw error
                                null
                            },
                        )
                    if (visitorData != null) {
                        webPoTokens = try {
                            webPoTokenProvider.generate(trackId, visitorData)
                        } catch (cancellation: CancellationException) {
                            throw cancellation
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
                if (client.useWebPoTokens && webPoTokens == null) continue
                val signatureTimestamp = if (client.useSignatureTimestamp) {
                    val timestampResult = PlatformStreamResolver.signatureTimestamp(trackId)
                    val timestampError = timestampResult.exceptionOrNull()
                    if (timestampError is CancellationException) throw timestampError
                    timestampResult.getOrNull() ?: continue
                } else {
                    null
                }
                val playerResult = repository.player(
                    videoId = trackId,
                    client = client,
                    signatureTimestamp = signatureTimestamp,
                    poToken = if (client.useWebPoTokens) webPoTokens?.playerRequest else null,
                )
                if (playerResult.isFailure) {
                    val error = playerResult.exceptionOrNull()
                    if (error is CancellationException) throw error
                    continue
                }

                val player = playerResult.getOrThrow()
                if (player.playabilityStatus.status != "OK") continue
                val candidateDurationMs = player.trackDurationMs(durationMs)
                val expectedDurationMs = durationMs?.takeIf { it > 0L } ?: candidateDurationMs
                val streamingDataPoToken = if (client.useWebPoTokens) webPoTokens?.streamingData else null
                val candidates = player.audioCandidates(trackId, candidateDurationMs, streamingDataPoToken = streamingDataPoToken)
                if (candidates.isEmpty()) {
                    if (player.hasAudioFormats()) {
                        unresolvedPlayers += UnresolvedPlayer(player, candidateDurationMs, streamingDataPoToken)
                    }
                    continue
                }

                for (candidate in candidates) {
                    if (!candidate.matchesTrackDuration(expectedDurationMs)) {
                        shorterPreviewFound = true
                        continue
                    }
                    when (repository.probeAudioStream(candidate.stream.url, candidate.contentLength)) {
                        AudioStreamProbeResult.RANGE_READABLE -> return Result.success(candidate.stream)
                        AudioStreamProbeResult.PROGRESSIVE_READABLE -> if (progressiveFallback == null) progressiveFallback = candidate.stream
                        AudioStreamProbeResult.REFUSED, AudioStreamProbeResult.UNAVAILABLE -> Unit
                    }
                }
                unresolvedPlayers += UnresolvedPlayer(player, candidateDurationMs, streamingDataPoToken)
            }

            if (unresolvedPlayers.isNotEmpty()) {
                val newPipeStreams = PlatformStreamResolver.newPipePlayer(trackId).toMap()
                for ((player, candidateDurationMs, streamingDataPoToken) in unresolvedPlayers) {
                    val expectedDurationMs = durationMs?.takeIf { it > 0L } ?: candidateDurationMs
                    for (candidate in player.audioCandidates(trackId, candidateDurationMs, newPipeStreams, streamingDataPoToken)) {
                        if (!candidate.matchesTrackDuration(expectedDurationMs)) {
                            shorterPreviewFound = true
                            continue
                        }
                        when (repository.probeAudioStream(candidate.stream.url, candidate.contentLength)) {
                            AudioStreamProbeResult.RANGE_READABLE -> return Result.success(candidate.stream)
                            AudioStreamProbeResult.PROGRESSIVE_READABLE -> if (progressiveFallback == null) progressiveFallback = candidate.stream
                            AudioStreamProbeResult.REFUSED, AudioStreamProbeResult.UNAVAILABLE -> Unit
                        }
                    }
                }
            }

            progressiveFallback?.let { return Result.success(it) }

            if (shorterPreviewFound) {
                return Result.failure(
                    IllegalStateException("YouTube only returned audio previews shorter than the selected track."),
                )
            }

            return Result.failure(
                IllegalStateException("YouTube did not provide a playable audio stream for $trackId"),
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            return Result.failure(error)
        }
    }

    private suspend fun PlayerResponse.audioCandidates(
        videoId: String,
        durationMs: Long?,
        newPipeStreams: Map<Int, String> = emptyMap(),
        streamingDataPoToken: String? = null,
    ): List<AudioCandidate> {
        val data = streamingData ?: return emptyList()
        val candidates = mutableListOf<AudioCandidate>()
        for (format in data.adaptiveFormats + data.formats.orEmpty()) {
            if (!format.isAudio) continue
            val directUrl = format.url
            val requiresPlatformResolution =
                !format.signatureCipher.isNullOrBlank() ||
                    !format.cipher.isNullOrBlank() ||
                    directUrl?.let(THROTTLING_PARAMETER_PATTERN::containsMatchIn) == true
            val platformUrl = if (requiresPlatformResolution) {
                PlatformStreamResolver.streamUrl(format, videoId)
            } else {
                null
            }
            val urls = buildList {
                directUrl?.let(::add)
                platformUrl?.let(::add)
                newPipeStreams[format.itag]?.let(::add)
            }.distinct()
            val reportedDurationMs = format.approxDurationMs?.toLongOrNull()?.takeIf { it > 0L }
            for (candidateUrl in urls) {
                val url = if (streamingDataPoToken == null) {
                    candidateUrl
                } else {
                    candidateUrl.withStreamingDataPoToken(streamingDataPoToken)
                }
                if (!url.startsWith("https://") && !url.startsWith("http://")) continue
                candidates += AudioCandidate(
                    stream = PlaybackAudioStream(
                        url = url,
                        mimeType = format.mimeType,
                        durationMs = reportedDurationMs ?: durationMs,
                        bitrate = format.averageBitrate ?: format.bitrate,
                    ),
                    contentLength = format.contentLength,
                    reportedDurationMs = reportedDurationMs,
                )
            }
        }
        return candidates.sortedWith(
            compareByDescending<AudioCandidate> { it.stream.mimeType.startsWith("audio/mp4", ignoreCase = true) }
                .thenByDescending { it.stream.bitrate },
        )
    }

    private fun PlayerResponse.hasAudioFormats(): Boolean = streamingData?.let { data ->
        (data.adaptiveFormats + data.formats.orEmpty()).any { it.isAudio }
    } == true

    private fun PlayerResponse.trackDurationMs(fallbackDurationMs: Long?): Long? =
        fallbackDurationMs?.takeIf { it > 0L }
            ?: videoDetails?.lengthSeconds?.toLongOrNull()?.takeIf { it > 0L }?.times(1_000L)

    private data class AudioCandidate(
        val stream: PlaybackAudioStream,
        val contentLength: Long?,
        val reportedDurationMs: Long?,
    ) {
        fun matchesTrackDuration(trackDurationMs: Long?): Boolean {
            val expected = trackDurationMs?.takeIf { it > 0L } ?: return true
            val actual = reportedDurationMs ?: estimatedDurationFromContentLength() ?: return true
            val allowedShortfall = (expected / 10L).coerceAtLeast(MIN_DURATION_TOLERANCE_MS)
            return actual >= expected - allowedShortfall
        }

        private fun estimatedDurationFromContentLength(): Long? {
            val length = contentLength?.takeIf { it > 0L } ?: return null
            val bitrate = stream.bitrate.takeIf { it > 0 }?.toLong() ?: return null
            return length * BITS_PER_BYTE * MILLIS_PER_SECOND / bitrate
        }
    }

    private data class UnresolvedPlayer(
        val player: PlayerResponse,
        val durationMs: Long?,
        val streamingDataPoToken: String?,
    )

    private fun String.withStreamingDataPoToken(token: String): String =
        if (contains("pot=")) this else "$this${if ('?' in this) '&' else '?'}pot=$token"

    private fun SongItem.matchesTrackMetadata(title: String, artists: List<String>): Boolean {
        val expectedTitle = normalizeTrackText(title)
        val candidateTitle = normalizeTrackText(this.title)
        if (expectedTitle.isEmpty() || candidateTitle.isEmpty() ||
            !(candidateTitle.contains(expectedTitle) || expectedTitle.contains(candidateTitle))
        ) {
            return false
        }
        val expectedArtists = artists.map(::normalizeTrackText).filter(String::isNotEmpty)
        if (expectedArtists.isEmpty()) return false
        return this.artists.any { candidate ->
            val candidateArtist = normalizeTrackText(candidate.name)
            expectedArtists.any { expectedArtist ->
                candidateArtist == expectedArtist ||
                    candidateArtist.contains(expectedArtist) ||
                    expectedArtist.contains(candidateArtist)
            }
        }
    }

    private fun normalizeTrackText(value: String): String =
        value.lowercase().filter(Char::isLetterOrDigit)

    private fun Long.matchesExpectedDuration(expectedDurationMs: Long): Boolean {
        val allowedShortfall = (expectedDurationMs / 10L).coerceAtLeast(MIN_DURATION_TOLERANCE_MS)
        return this >= expectedDurationMs - allowedShortfall
    }

    private companion object {
        const val BITS_PER_BYTE = 8L
        const val MILLIS_PER_SECOND = 1_000L
        const val MIN_DURATION_TOLERANCE_MS = 10_000L
        const val SHORT_TRACK_THRESHOLD_MS = 90_000L
        const val ALTERNATE_TRACK_MIN_GAIN_MS = 20_000L
        const val MAX_ALTERNATE_TRACKS = 4
        val THROTTLING_PARAMETER_PATTERN = Regex("[?&]n=")
    }
}
