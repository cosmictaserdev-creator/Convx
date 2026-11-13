package com.convx.music.modules

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackAudioStream
import com.convx.music.playback.PlaybackRequestTrack
import com.music.spine.ModuleStreamResponse
import io.ktor.http.Url
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.io.encoding.Base64

private const val MODULE_PROVIDER_PREFIX = "spine:"
fun isModuleProvider(providerId: String): Boolean = providerId.startsWith(MODULE_PROVIDER_PREFIX)

/** Stable provider identity prevents equal track IDs from different sources colliding in a queue. */
fun ModuleTrack.playbackRequest() = PlaybackRequestTrack(
    providerId = MODULE_PROVIDER_PREFIX + Base64.UrlSafe.encode(moduleKey.encodeToByteArray()),
    id = track.id,
    title = track.title,
    artists = listOf(track.artist).filter(String::isNotBlank),
    durationMs = track.duration.takeIf { it > 0 }?.toLong()?.times(1_000L),
    artworkUrl = track.albumCover,
)

/** Resolves module identities through the same repository shown in settings on every client. */
class ModuleAudioStreamResolver(
    private val modules: ModuleRepository,
    private val fallback: AudioStreamResolver,
) : AudioStreamResolver {
    override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
        if (!isModuleProvider(track.providerId)) {
            if (track.providerId == "youtube_music") {
                val preferred = try {
                    withTimeoutOrNull(15_000) { modules.preferredStream(track) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    null
                }
                if (preferred != null) {
                    return Result.success(preferred.stream.playbackAudio(
                        preferred.track.duration.takeIf { it > 0 }?.toLong()?.times(1_000L) ?: track.durationMs,
                    ))
                }
            }
            return fallback.resolve(track)
        }
        return try {
            val key = Base64.UrlSafe.decode(track.providerId.removePrefix(MODULE_PROVIDER_PREFIX)).decodeToString()
            modules.restore()
            val module = modules.preferences.value.module(key) ?: error("This module source has been removed")
            val sourceTrack = ModuleTrack(key, module.name, com.music.spine.ModuleSearchResult(id = track.id))
            val stream = modules.stream(sourceTrack)
            Result.success(stream.playbackAudio(track.durationMs))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            Result.failure(failure)
        }
    }
}

private fun ModuleStreamResponse.playbackAudio(durationMs: Long?) = PlaybackAudioStream(
    url = requireNotNull(streamUrl),
    mimeType = playbackMimeType(),
    durationMs = durationMs,
    bitrate = track?.bitrate?.takeIf { it > 0 }?.let { if (it < 10_000) it * 1_000 else it } ?: 0,
)

internal fun ModuleStreamResponse.playbackMimeType(): String {
    when (streamType?.lowercase()) {
        "hls" -> return "application/vnd.apple.mpegurl"
        "dash" -> return "application/dash+xml"
    }
    mimeType?.takeIf(String::isNotBlank)?.let { return it }
    track?.mimeType?.takeIf(String::isNotBlank)?.let { return it }
    val metadata = listOfNotNull(track?.codec, track?.format, streamUrl?.let { Url(it).encodedPath }).joinToString(" ").lowercase()
    return when {
        "flac" in metadata -> "audio/flac"
        "alac" in metadata -> "audio/mp4; codecs=\"alac\""
        "eac3" in metadata || "ec-3" in metadata -> "audio/mp4; codecs=\"ec-3\""
        "opus" in metadata -> "audio/ogg; codecs=\"opus\""
        "ogg" in metadata -> "audio/ogg"
        "wav" in metadata -> "audio/wav"
        "aac" in metadata || "m4a" in metadata || "mp4" in metadata -> "audio/mp4"
        ".m3u8" in metadata -> "application/vnd.apple.mpegurl"
        ".mpd" in metadata -> "application/dash+xml"
        else -> "audio/mpeg"
    }
}
