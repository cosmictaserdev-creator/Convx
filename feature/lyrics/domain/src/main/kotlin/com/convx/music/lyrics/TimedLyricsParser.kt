package com.convx.music.lyrics

/** Parses Convx's standard and rich-sync lyric rows without Android APIs. */
object TimedLyricsParser {
    val LINE_REGEX = "((\\[\\d\\d:\\d\\d\\.\\d{2,3}\\] ?)+)(.+)".toRegex()
    val TIME_REGEX = "\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})\\]".toRegex()

    private val richSyncLineRegex = "\\[(\\d{1,2}):(\\d{2})\\.(\\d{2,3})\\](.+)".toRegex()
    private val richSyncWordRegex =
        Regex("<(\\d{1,2}):(\\d{2})\\.(\\d{2,3})(?:\\|(\\d{1,2}):(\\d{2})\\.(\\d{2,3}))?>\\s*([^<]+)")
    private val richSyncTimestampTagRegex =
        Regex("<\\d{1,2}:\\d{2}\\.\\d{2,3}(?:\\|\\d{1,2}:\\d{2}\\.\\d{2,3})?>\\s*")
    private val agentRegex = "\\{agent:([^}]+)\\}".toRegex()
    private val backgroundRegex = "^\\{bg\\}".toRegex()
    private val hexEntityRegex = "&#x([0-9a-fA-F]+);".toRegex()
    private val decimalEntityRegex = "&#(\\d+);".toRegex()

    fun parse(lyrics: String): List<LyricsEntry> {
        val unescapedLyrics = lyrics
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
        val decodedLyrics = decodeHtmlEntities(unescapedLyrics)
        val lines = decodedLyrics.lineSequence()
            .filter { it.isNotBlank() && !it.trim().startsWith("[offset:") }
            .toList()

        val isRichSync = lines.any { line ->
            richSyncLineRegex.matches(line.trim()) && richSyncWordRegex.containsMatchIn(line)
        }
        return if (isRichSync) parseRichSyncLyrics(lines) else parseStandardLyrics(lines)
    }

    private fun decodeHtmlEntities(text: String): String = text
        .replace(hexEntityRegex) { match ->
            match.groupValues[1].toIntOrNull(16)?.let(::codePointString) ?: match.value
        }
        .replace(decimalEntityRegex) { match ->
            match.groupValues[1].toIntOrNull()?.let(::codePointString) ?: match.value
        }
        .replace("&apos;", "'")
        .replace("&quot;", "\"")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")

    private fun codePointString(codePoint: Int): String? {
        if (codePoint !in 0..0x10FFFF || codePoint in 0xD800..0xDFFF) return null
        if (codePoint <= 0xFFFF) return codePoint.toChar().toString()
        val adjusted = codePoint - 0x10000
        return buildString(capacity = 2) {
            append((0xD800 + (adjusted shr 10)).toChar())
            append((0xDC00 + (adjusted and 0x3FF)).toChar())
        }
    }

    private fun parseRichSyncLyrics(lines: List<String>): List<LyricsEntry> {
        val result = mutableListOf<LyricsEntry>()
        lines.forEachIndexed { index, line ->
            val match = richSyncLineRegex.matchEntire(line.trim()) ?: return@forEachIndexed
            val minutes = match.groupValues[1].toLongOrNull() ?: 0L
            val seconds = match.groupValues[2].toLongOrNull() ?: 0L
            val fractionText = match.groupValues[3]
            val fraction = fractionText.toLongOrNull() ?: 0L
            val fractionMillis = if (fractionText.length == 3) fraction else fraction * 10
            val lineTimeMs = minutes * 60_000L + seconds * 1_000L + fractionMillis

            var content = match.groupValues[4].trimStart()
            val agentMatch = agentRegex.find(content)
            val agent = agentMatch?.groupValues?.get(1)
            if (agentMatch != null) content = content.replaceFirst(agentRegex, "")

            val isBackground = backgroundRegex.containsMatchIn(content)
            if (isBackground) content = content.replaceFirst(backgroundRegex, "")

            val wordTimings = parseRichSyncWords(content, index, lines)
            val plainText = content.replace(richSyncTimestampTagRegex, "").trim()
            if (plainText.isBlank()) return@forEachIndexed

            result += LyricsEntry(
                time = lineTimeMs,
                text = plainText,
                words = wordTimings,
                agent = agent,
                isBackground = isBackground,
            )
        }
        return result.sorted()
    }

    private fun parseRichSyncWords(
        content: String,
        currentIndex: Int,
        allLines: List<String>,
    ): List<WordTimestamp>? {
        val matches = richSyncWordRegex.findAll(content).toList()
        if (matches.isEmpty()) return null

        val timings = mutableListOf<WordTimestamp>()
        matches.forEachIndexed { index, match ->
            val startTime = parseRichSyncTime(match)
            val wordText = match.groupValues[7].trim()
            val declaredEnd = if (match.groupValues[4].isNotEmpty()) parseRichSyncTime(match, 4) else null
            val nextWordStart = if (index < matches.lastIndex) {
                parseRichSyncTime(matches[index + 1])
            } else {
                getNextLineStartTime(currentIndex, allLines)
            }
            val endTime = when {
                declaredEnd != null && nextWordStart != null -> minOf(declaredEnd, nextWordStart)
                declaredEnd != null -> declaredEnd
                nextWordStart != null -> nextWordStart
                else -> startTime + 0.5
            }.coerceAtLeast(startTime + 0.02)

            if (wordText.isNotBlank()) timings += WordTimestamp(wordText, startTime, endTime)
        }
        return timings.takeIf { it.isNotEmpty() }
    }

    private fun parseRichSyncTime(match: MatchResult, groupOffset: Int = 1): Double {
        val minutes = match.groupValues[groupOffset].toLongOrNull() ?: 0L
        val seconds = match.groupValues[groupOffset + 1].toLongOrNull() ?: 0L
        val fractionText = match.groupValues[groupOffset + 2]
        val fraction = fractionText.toLongOrNull() ?: 0L
        val scale = if (fractionText.length == 3) 1_000.0 else 100.0
        return minutes * 60.0 + seconds + fraction / scale
    }

    private fun getNextLineStartTime(currentIndex: Int, allLines: List<String>): Double? {
        val nextLine = allLines.getOrNull(currentIndex + 1)?.trim() ?: return null
        val match = richSyncLineRegex.matchEntire(nextLine) ?: return null
        return parseRichSyncTime(match)
    }

    private fun parseStandardLyrics(lines: List<String>): List<LyricsEntry> {
        val result = mutableListOf<LyricsEntry>()
        for (index in lines.indices) {
            val line = lines[index]
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("<") && trimmedLine.endsWith(">")) continue

            val entries = parseLine(line) ?: continue
            val nextLine = lines.getOrNull(index + 1)?.trim()
            val wordTimings = nextLine
                ?.takeIf { it.startsWith("<") && it.endsWith(">") }
                ?.let { parseWordTimestamps(it.removeSurrounding("<", ">")) }

            if (wordTimings == null) {
                result.addAll(entries)
            } else {
                result.addAll(entries.map { entry ->
                    entry.copy(words = wordTimings)
                })
            }
        }
        return result.sorted()
    }

    private fun parseWordTimestamps(data: String): List<WordTimestamp>? {
        if (data.isBlank()) return null
        return try {
            data.split("|").mapNotNull { wordData ->
                val parts = wordData.split(":")
                if (parts.size == 3) {
                    WordTimestamp(parts[0], parts[1].toDouble(), parts[2].toDouble())
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parseLine(line: String): List<LyricsEntry>? {
        if (line.isEmpty()) return null
        val match = LINE_REGEX.matchEntire(line.trim()) ?: return null
        val times = match.groupValues[1]
        var text = match.groupValues[3]

        val agentMatch = agentRegex.find(text)
        val agent = agentMatch?.groupValues?.get(1)
        if (agentMatch != null) text = text.replaceFirst(agentRegex, "")

        val isBackground = backgroundRegex.containsMatchIn(text)
        if (isBackground) text = text.replaceFirst(backgroundRegex, "")

        return TIME_REGEX.findAll(times).map { timeMatch ->
            val minutes = timeMatch.groupValues[1].toLong()
            val seconds = timeMatch.groupValues[2].toLong()
            val fractionText = timeMatch.groupValues[3]
            val fraction = fractionText.toLong() * if (fractionText.length == 2) 10 else 1
            val time = minutes * 60_000L + seconds * 1_000L + fraction
            LyricsEntry(time, text, agent = agent, isBackground = isBackground)
        }.toList()
    }
}
