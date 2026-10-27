package com.convx.music.lyrics

/** Background vocals and simultaneous duet rows must not move the scroll anchor backwards. */
internal fun primaryLyricLineIndex(lines: List<LyricsEntry>, positionMs: Long): Int =
    findPrimaryLyricLineIndex(
        lines = lines,
        positionMs = positionMs,
        timeMs = LyricsEntry::time,
        isBackground = LyricsEntry::isBackground,
        text = LyricsEntry::text,
    )

/** Keeps the source line's spaces and punctuation attached to the timed runs that precede them. */
fun LyricsEntry.wordsWithSpacing(): List<WordTimestamp> {
    val timed = words.orEmpty()
    if (timed.isEmpty()) return emptyList()
    val starts = mutableListOf<Int>()
    var cursor = 0
    for (word in timed) {
        val token = word.text.trim()
        val start = if (token.isEmpty()) -1 else text.indexOf(token, cursor)
        if (start < 0) {
            return timed.mapIndexed { index, run ->
                val next = timed.getOrNull(index + 1)?.text.orEmpty()
                val needsSpace = run.text.lastOrNull()?.let { it.isLetterOrDigit() && !it.isUnspacedScript() } == true &&
                    next.firstOrNull()?.let { it.isLetterOrDigit() && !it.isUnspacedScript() } == true
                run.copy(text = run.text + if (needsSpace) " " else "")
            }
        }
        starts += start
        cursor = start + token.length
    }
    return timed.mapIndexed { index, word ->
        word.copy(text = text.substring(if (index == 0) 0 else starts[index], starts.getOrNull(index + 1) ?: text.length))
    }
}

private fun Char.isUnspacedScript(): Boolean = when (Character.UnicodeScript.of(code)) {
    Character.UnicodeScript.HAN, Character.UnicodeScript.HIRAGANA, Character.UnicodeScript.KATAKANA,
    Character.UnicodeScript.HANGUL, Character.UnicodeScript.THAI, Character.UnicodeScript.LAO,
    Character.UnicodeScript.KHMER -> true
    else -> false
}
