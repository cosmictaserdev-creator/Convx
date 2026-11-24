/** Adapted from BitChord (kushagrasinghx/BitChord), GPL-3.0. See NOTICE. */
package com.convx.music.lyrics.bitchord

/** Parses line and word timing from LRC text without performing provider I/O. */
object LrcParser {

    /**
     * `[mm:ss.xx] words`. Metadata tags carry no timestamp and fall out on
     * their own. A stamp with no words marks an instrumental break; those are
     * kept, but only where the silence is long enough to be worth showing -
     * otherwise the line would blink out between two sung phrases. A stamp
     * with nothing after it closes the final line, so it always survives.
     */
    fun parse(lrc: String): List<LyricLine> {
        val all = lrc.lineSequence().mapNotNull { line ->
            val match = STAMP.find(line) ?: return@mapNotNull null
            val (minutes, seconds, fraction) = match.destructured
            // Two digits mean centiseconds, three mean milliseconds.
            val fractionMs = when (fraction.length) {
                2 -> fraction.toLong() * 10
                3 -> fraction.toLong()
                else -> 0L
            }
            val body = line.substring(match.range.last + 1)
            // Detect the RTL marker that was written by [toEnhancedLrc].
            val (text, alignment) = stripAlignmentMarker(body)
            LyricLine(
                timeMs = minutes.toLong() * 60_000 + seconds.toLong() * 1_000 + fractionMs,
                // Stripped rather than rebuilt from the runs below: the spacing
                // and punctuation between two words belong to the line, and
                // re-joining the words with single spaces would quietly rewrite
                // a line that never had them.
                text = text.replace(WORD_STAMP, "").trim(),
                words = parseWordRuns(text),
                alignment = alignment,
            )
        }.sortedBy { it.timeMs }.toList()

        val kept = all.filterIndexed { index, line ->
            if (!line.isGap) return@filterIndexed true
            // A trailing stamp closes off the last line - that's the outro.
            val next = all.getOrNull(index + 1) ?: return@filterIndexed true
            next.timeMs - line.timeMs >= MIN_GAP_MS
        }

        // Nothing stands for the intro - LRC files start at the first sung
        // word - so give the run-up its own break when it's long enough.
        val first = kept.firstOrNull() ?: return kept
        return if (!first.isGap && first.timeMs >= MIN_GAP_MS) {
            listOf(LyricLine(0L, "")) + kept
        } else {
            kept
        }
    }

    /**
     * Strip the alignment marker written by [toEnhancedLrc] and return the
     * clean body along with the recovered alignment.
     *
     * The marker is `<R>` - a word-stamp-looking token that never appears in
     * genuine A2 output (word stamps use `<mm:ss.xx>`, not bare `<R>`). It sits
     * at the very start of the body, before the first real stamp.
     */
    private fun stripAlignmentMarker(body: String): Pair<String, LyricAlignment> {
        if (body.startsWith(ALIGNMENT_MARKER)) {
            return body.substringAfter(ALIGNMENT_MARKER) to LyricAlignment.End
        }
        return body to LyricAlignment.Start
    }

    private const val ALIGNMENT_MARKER = "<R>"

    /**
     * The `<mm:ss.xx>` runs of an "enhanced" A2 line, as words.
     *
     * Each run ends where the next one starts, which is why a line written by
     * [toEnhancedLrc] closes with a bare stamp: that last one names no word, it
     * just states where the previous one stopped. A run with no text is
     * therefore a terminator rather than a word, here and in the files other
     * A2 writers produce.
     *
     * Empty for a plain line, which is what keeps [LyricLine.isWordSynced]
     * honest - a line-synced source stays line-synced through this.
     */
    private fun parseWordRuns(body: String): List<LyricWord> {
        val marks = WORD_STAMP.findAll(body).toList()
        if (marks.isEmpty()) return emptyList()
        val runs = marks.mapIndexed { index, mark ->
            val until = marks.getOrNull(index + 1)?.range?.first ?: body.length
            msOf(mark) to body.substring(mark.range.last + 1, until)
        }
        // The next run's stamp is this one's end - including when that run is
        // the closing terminator, which is the only thing that gives the last
        // word of a line an end at all. A run with no space after it is one
        // syllable of a word that carries on, which is how [toEnhancedLrc]
        // writes a word whose syllables were timed apart.
        return wordsFromRuns(
            runs.mapIndexed { index, (startMs, text) ->
                TimedRun(startMs, runs.getOrNull(index + 1)?.first ?: startMs, text)
            },
        )
    }

    private fun msOf(mark: MatchResult): Long {
        val (minutes, seconds, fraction) = mark.destructured
        val fractionMs = when (fraction.length) {
            2 -> fraction.toLong() * 10
            3 -> fraction.toLong()
            else -> 0L
        }
        return minutes.toLong() * 60_000 + seconds.toLong() * 1_000 + fractionMs
    }

    private val STAMP = Regex("""\[(\d{1,2}):(\d{2})[.:](\d{2,3})]""")
    private val WORD_STAMP = Regex("""<(\d{1,3}):(\d{2})[.:](\d{2,3})>""")
}
