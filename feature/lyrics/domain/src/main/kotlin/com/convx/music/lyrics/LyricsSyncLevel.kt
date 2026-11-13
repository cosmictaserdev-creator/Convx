package com.convx.music.lyrics

/** Capabilities come from real timestamps, never from a provider's advertised name. */
enum class LyricsSyncLevel(val label: String) {
    PLAIN("Plain lyrics"), LINE("Line synced"), WORD("Word by word");

    companion object {
        private val lineStamp = Regex("""^\[(\d{1,3}):(\d{2})\.(\d{2,3})]""")
        private val wordStamp = Regex("""<(\d{1,3}):(\d{2})\.(\d{2,3})(?:\|(\d{1,3}):(\d{2})\.(\d{2,3}))?>([^<]+)""")
        private fun MatchResult.time(offset: Int = 1): Long =
            groupValues[offset].toLong() * 60_000 + groupValues[offset + 1].toLong() * 1000 +
                groupValues[offset + 2].padEnd(3, '0').toLong()

        fun detect(lyrics: String): LyricsSyncLevel {
            val lineTimes = hashSetOf<Long>()
            for (raw in lyrics.lineSequence()) {
                val line = raw.trim()
                lineStamp.find(line)?.let { lineTimes.add(it.time()) }
                val words = wordStamp.findAll(line).filter { word ->
                    word.groupValues[7].isNotBlank() &&
                        (word.groupValues[4].isBlank() || word.time(4) > word.time())
                }.map { it.time() }.toList()
                if (words.size > 1 && words.distinct().size > 1 && words.zipWithNext().all { it.first <= it.second }) return WORD
                // Older Convx providers store word runs on a separate <word:start:end> row.
                if (line.startsWith('<') && line.endsWith('>') && !line.contains("</")) {
                    val starts = line.removeSurrounding("<", ">").split('|').mapNotNull { run ->
                        val pieces = run.split(':')
                        if (pieces.size != 3 || pieces[0].isBlank()) return@mapNotNull null
                        val start = pieces[1].toDoubleOrNull() ?: return@mapNotNull null
                        val end = pieces[2].toDoubleOrNull() ?: return@mapNotNull null
                        start.takeIf { it >= 0 && end > it && it.isFinite() && end.isFinite() }
                    }
                    if (starts.size > 1 && starts.distinct().size > 1 && starts.zipWithNext().all { it.first <= it.second }) return WORD
                }
            }
            return if (lineTimes.size > 1 || lineTimes.any { it > 0 }) LINE else PLAIN
        }
    }
}

data class LyricsUpgradeCandidate(val provider: String, val priority: Int, val lyrics: String, val sync: LyricsSyncLevel)

/** Monotonic quality upgrades; user priority breaks ties within one timing level. */
class LyricsUpgradePolicy {
    var best: LyricsUpgradeCandidate? = null
        private set

    fun consider(provider: String, priority: Int, lyrics: String): LyricsUpgradeCandidate? {
        if (lyrics.isBlank()) return null
        val candidate = LyricsUpgradeCandidate(provider, priority, lyrics, LyricsSyncLevel.detect(lyrics))
        val current = best
        if (current != null && (candidate.sync < current.sync ||
                (candidate.sync == current.sync && candidate.priority >= current.priority))) return null
        best = candidate
        return candidate
    }
}
