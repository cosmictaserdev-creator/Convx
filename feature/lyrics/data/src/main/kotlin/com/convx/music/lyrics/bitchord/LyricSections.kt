/** Adapted from BitChord (kushagrasinghx/BitChord), GPL-3.0. See NOTICE. */
package com.convx.music.lyrics.bitchord

/**
 * Whether a plain lyric line is one of Genius's section markers — `[Chorus]`,
 * `[Verse 2: Artist]` — rather than words to sing.
 */
fun isGeniusSectionHeader(text: String): Boolean {
    val trimmed = text.trim()
    return trimmed.startsWith("[") && trimmed.endsWith("]") && trimmed.length in 3..60
}
