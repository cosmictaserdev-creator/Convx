/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Ported from BitChord (kushagrasinghx/BitChord), GPL-3.0. See NOTICE.
 */
package com.convx.music.ui.component.bitchord
import com.convx.music.lyrics.bitchord.LyricLine

/** Keep every unfinished vocal visible, including overlaps spanning more than two rows. */
internal fun activeLyricRows(lines: List<LyricLine>, positionMs: Long): List<Int> {
    val latest = lines.indexOfLast { it.timeMs <= positionMs }
    if (latest < 0) return emptyList()
    return (0..latest).filter { index ->
        val line = lines[index]
        index == latest || (!line.isGap &&
            (line.hasKnownEnd || line.background?.hasKnownEnd == true) &&
            line.timeMs <= positionMs && positionMs < line.endMs)
    }
}
