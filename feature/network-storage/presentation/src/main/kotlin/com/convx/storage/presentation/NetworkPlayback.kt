package com.convx.storage.presentation

import com.convx.music.playback.*
import com.convx.storage.domain.*
import kotlinx.coroutines.CancellationException

const val NETWORK_MUSIC_PROVIDER = "network_music"

fun NetworkTrack.playbackRequest() = PlaybackRequestTrack(
    providerId = NETWORK_MUSIC_PROVIDER, id = id, title = title,
    artists = listOfNotNull(artist), artworkUrl = artwork,
)

fun interface NetworkPlaybackEndpoint {
    suspend fun create(mediaId: String, mimeType: String): String
}

/** Logical source IDs stay in queues; endpoint URLs are created only when playing. */
class NetworkAudioStreamResolver(
    private val library: NetworkLibraryRepository,
    private val endpoint: NetworkPlaybackEndpoint,
    private val fallback: AudioStreamResolver,
) : AudioStreamResolver {
    override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
        val networkId = track.id.startsWith(NetworkMediaUri.Scheme + "://")
        if (track.providerId != NETWORK_MUSIC_PROVIDER && !networkId) return fallback.resolve(track)
        return try {
            val location = NetworkMediaUri.parse(track.id) ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
            if (library.loadServers().none { it.id == location.serverId }) throw StorageException(StorageError.NOT_FOUND)
            val mimeType = networkMimeType(location.path)
            Result.success(PlaybackAudioStream(endpoint.create(track.id, mimeType), mimeType, track.durationMs, 0))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            Result.failure(failure)
        }
    }
}

private fun networkMimeType(path: String): String = when (path.substringAfterLast('.', "").lowercase()) {
    "mp3" -> "audio/mpeg"
    "m4a" -> "audio/mp4"
    "aac" -> "audio/aac"
    "flac" -> "audio/flac"
    "wav" -> "audio/wav"
    "aif", "aiff" -> "audio/aiff"
    "ogg", "opus" -> "audio/ogg"
    "wma" -> "audio/x-ms-wma"
    "webm" -> "audio/webm"
    else -> "application/octet-stream"
}
