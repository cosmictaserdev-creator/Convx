/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

/**
 * Lightweight typo-tolerant match: every character of [query] must appear in [target], in
 * order, case-insensitively — gaps allowed. Returns null when [query] isn't a subsequence of
 * [target] at all; otherwise a score where higher means a tighter, more contiguous, earlier
 * match, so callers can `sortedByDescending` a filtered list.
 *
 * Not a database-level fuzzy index (no FTS migration) — just an in-memory fallback for local
 * libraries small enough to fully scan, used when the exact substring search finds nothing.
 */
fun fuzzyScore(query: String, target: String): Int? {
    if (query.isBlank()) return 0
    val q = query.trim().lowercase()
    val t = target.lowercase()

    var score = 0
    var searchFrom = 0
    var consecutiveRun = 0
    for (qc in q) {
        val idx = t.indexOf(qc, searchFrom)
        if (idx == -1) return null
        consecutiveRun = if (idx == searchFrom) consecutiveRun + 1 else 1
        score += consecutiveRun * 2 - (idx - searchFrom)
        if (idx == 0 || t[idx - 1] == ' ') score += 3
        searchFrom = idx + 1
    }
    // Less leftover noise in the target reads as a tighter, more relevant match.
    score -= (t.length - q.length) / 4
    return score
}

/** ponytail: assert-based self-check, run manually — no test framework wired for this module. */
private fun demo() {
    check(fuzzyScore("abc", "xyz") == null)
    check(fuzzyScore("", "anything") == 0)
    check(fuzzyScore("cofee", "coffee shop") != null) // typo'd word still matches
    check(fuzzyScore("taly", "italy") != null) // dropped leading letter still matches
    val exact = fuzzyScore("italy", "italy")!!
    val loose = fuzzyScore("ity", "italy")!!
    check(exact > loose) // tighter/earlier match scores higher than a scattered one
}
