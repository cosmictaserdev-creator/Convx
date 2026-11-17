/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.lyrics

import com.music.innertube.YouTube

object YouTubeSubtitleLyricsProvider : LyricsProvider {
    override val id = "YouTubeSubtitle"
    override val displayName = "YouTube Subtitle"
    override val requiresVideoId = true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = YouTube.transcript(id)
}
