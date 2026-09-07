/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Multi-singer lyric metadata model.
 *
 * Apple Music-style duet/shared-vocal support rides on top of the existing
 * [LyricsEntry.agent] field: every line may reference a voice id ("v1", "v2",
 * "v1000", …) declared once per song in a singer registry, analogous to the
 * `ttm:agent` registry in Apple's TTML delivery format.
 */

package com.convxy.music.lyrics

/**
 * A single vocalist (or vocal group) referenced by lyric lines.
 *
 * @param id the stable voice id used by [LyricsEntry.agent] (e.g. "v1", "v2", "v1000").
 * @param name the human readable performer name when the source provides one
 * (Apple TTML `ttm:name`), otherwise `null` and the UI may fall back to
 * inferring a name from the track's artist list.
 * @param isGroup true when the agent represents shared vocals performed by
 * multiple vocalists (Apple TTML `type="group"`, conventionally "v1000").
 */
data class SingerInfo(
    val id: String,
    val name: String?,
    val isGroup: Boolean = false,
)

/**
 * Result of parsing a complete lyric document, pairing the timed lines with the
 * singer registry extracted from its metadata header.
 */
data class ParsedLyrics(
    val entries: List<LyricsEntry>,
    val singers: Map<String, SingerInfo>,
) {
    /**
     * True when the lyrics actually switch between at least two distinct lead
     * voices (background lines and unnamed agents included in the count, as a
     * v1/v2 duet without names is still a duet). Songs with zero or one voice
     * must render exactly like the pre-multi-singer implementation.
     */
    val hasMultipleSingers: Boolean by lazy {
        entries.asSequence()
            .filter { !it.isBackground }
            .mapNotNull { it.agent }
            .map { primaryAgentId(it) }
            .distinct()
            .count() >= 2
    }
}

/**
 * Returns the canonical id of the (possibly composite) [agent] string.
 *
 * Supported agent forms:
 *  - `"v1"`, `"v2"`, … (single voice, the common case)
 *  - `"v1000"` (Apple convention for a vocal group / shared line)
 *  - `"v1+v2"`, `"v1,v2"`, `"v1&v2"` (explicit shared line: both leads sing)
 *
 * Composite forms normalize to the group id so a shared line is treated as one
 * (group) voice rather than flipping between its members.
 */
fun primaryAgentId(agent: String): String {
    val trimmed = agent.trim()
    if (SHARED_AGENT_REGEX.matches(trimmed)) return GROUP_AGENT_ID
    return trimmed.substringBefore('+').substringBefore(',').substringBefore('&').trim()
}

/** True when the agent string marks shared vocals (group or composite ids). */
fun isSharedVocals(agent: String?): Boolean {
    if (agent == null) return false
    val trimmed = agent.trim()
    return trimmed == GROUP_AGENT_ID || SHARED_AGENT_REGEX.matches(trimmed)
}

/** Agents recognized as "shared vocals" (`v1+v2`, `v1,v2`, `v1&v2`). */
private val SHARED_AGENT_REGEX = Regex("v\\d+\\s*[+,&]\\s*v\\d+(\\s*[+,&]\\s*v\\d+)*")

/** Apple convention for the all-vocalists group agent. */
const val GROUP_AGENT_ID = "v1000"

/**
 * True when the line at [index] starts a new singer section, i.e. the previous
 * lead line (skipping background vocals and blank/head entries) belongs to a
 * different vocalist. Used to show the "who is singing" badge on section
 * starts only, mirroring the requested layout:
 *
 * ```
 * 🎤 Artist A
 * I remember all the things...
 * ```
 */
fun isSingerSectionStart(lines: List<LyricsEntry>, index: Int): Boolean {
    val entry = lines.getOrNull(index) ?: return false
    val agent = entry.agent ?: return false
    if (entry.isBackground) return false
    val currentId = primaryAgentId(agent)
    for (i in index - 1 downTo 0) {
        val previous = lines[i]
        if (previous.isBackground || previous.text.isBlank()) continue
        val previousAgent = previous.agent ?: return true
        return primaryAgentId(previousAgent) != currentId
    }
    return true
}

