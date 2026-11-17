/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.lyrics

import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import kotlinx.coroutines.CancellationException

object YouTubeLyricsProvider : LyricsProvider {
    override val id = "YouTubeMusic"
    override val displayName = "YouTube Music"
    override val requiresVideoId = true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = try {
        val nextResult = YouTube.next(WatchEndpoint(videoId = id)).getOrThrow()
        Result.success(
            YouTube.lyrics(
                endpoint = nextResult.lyricsEndpoint ?: throw IllegalStateException("Lyrics endpoint not found"),
            ).getOrThrow() ?: throw IllegalStateException("Lyrics unavailable"),
        )
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
}
