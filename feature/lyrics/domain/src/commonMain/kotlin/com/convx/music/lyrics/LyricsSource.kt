package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.LyricLine

data class LyricsQuery(val id: String, val title: String, val artist: String, val durationMs: Long, val album: String? = null)

interface SharedLyricsSource {
    val id: String
    val label: String
    val wordSynced: Boolean
    suspend fun lyrics(query: LyricsQuery): List<LyricLine>?
}

data class LyricsDocument(val sourceId: String, val sourceName: String, val lines: List<LyricLine>) {
    val hasWordTimings: Boolean get() = lines.any { it.words.isNotEmpty() }
    val isSynced: Boolean get() = lines.any { it.timeMs > 0L || it.words.isNotEmpty() }
}
