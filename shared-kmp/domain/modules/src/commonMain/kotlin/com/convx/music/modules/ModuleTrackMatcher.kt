package com.convx.music.modules

import com.convx.music.playback.PlaybackRequestTrack
import com.music.spine.ModuleSearchResult
import kotlin.math.abs

/** Only substitute a catalog track when title, artist identity and duration agree. */
internal fun ModuleSearchResult.matches(request: PlaybackRequestTrack): Boolean {
    val title = normalizedIdentity(request.title)
    if (title.isEmpty() || normalizedIdentity(this.title) != title) return false
    val candidateArtist = " " + normalizedIdentity(artist) + " "
    if (request.artists.isNotEmpty() && request.artists.none {
        val identity = normalizedIdentity(it)
        identity.isNotEmpty() && candidateArtist.contains(" $identity ")
    }) return false
    val expectedSeconds = request.durationMs?.takeIf { it > 0 }?.div(1_000L)
    return expectedSeconds == null || duration <= 0 || abs(duration.toLong() - expectedSeconds) <= 10
}

private fun normalizedIdentity(value: String): String = moduleUnicodeCompatibilityDecomposition(value)
    .filterNot { it.category in setOf(CharCategory.NON_SPACING_MARK, CharCategory.COMBINING_SPACING_MARK, CharCategory.ENCLOSING_MARK) }
    .lowercase()
    .replace(Regex("""\s*[\[(](?:feat\.?|ft\.?|featuring)\s+[^\])]*[\])]"""), "")
    .map { if (it.isLetterOrDigit()) it else ' ' }.joinToString("")
    .split(' ').filter(String::isNotBlank).joinToString(" ")
