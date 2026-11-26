package com.convx.music.playback

import com.convx.music.lyrics.LyricsEntry

data class PlayerLyricsPreview(val mediaId: String?, val lines: List<LyricsEntry>)

internal fun currentPreviewLine(lines: List<LyricsEntry>, positionMs: Long): Int {
    val index = lines.binarySearch { it.time.compareTo(positionMs) }
    return if (index >= 0) index else -index - 2
}
