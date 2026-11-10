package com.convx.music.client.desktop

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackAudioStream
import com.convx.music.playback.PlaybackRequestTrack
import kotlinx.coroutines.CancellationException
import java.io.File
import java.net.URI

/** Uses indexed files and completed downloads before contacting the online provider. */
internal class DesktopLocalAudioStreamResolver(
    private val online: AudioStreamResolver,
    private val library: DesktopMediaLibrary,
    private val settings: com.convx.music.playback.PlaybackSettingsRepository,
) : AudioStreamResolver {
    override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
        library.awaitReady()
        val file = library.downloadedFile(track) ?: if (track.providerId == LOCAL_MUSIC_PROVIDER) {
            try { File(URI(track.id)) } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { return Result.failure(error) }
        } else null
        if (file == null) return if (settings.getSettings().localMode)
            Result.failure(java.io.IOException("This song isn't downloaded. Switch off local mode to stream it.")) else online.resolve(track)
        if (!file.isFile) return Result.failure(java.io.FileNotFoundException("This audio file is unavailable. Reconnect its drive or rescan your music folders."))
        val mime = when (file.extension.lowercase()) {
            "m4a", "aac" -> "audio/mp4"
            "flac" -> "audio/flac"
            "wav" -> "audio/wav"
            "ogg", "opus" -> "audio/ogg"
            "webm" -> "audio/webm"
            else -> "audio/mpeg"
        }
        return Result.success(PlaybackAudioStream(file.toURI().toString(), mime, track.durationMs, 0))
    }
}
