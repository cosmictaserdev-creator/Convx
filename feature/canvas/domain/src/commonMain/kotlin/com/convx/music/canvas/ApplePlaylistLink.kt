package com.convx.music.canvas

/** A validated Apple Music playlist catalog identifier. */
data class ApplePlaylistLink(val storefront: String, val id: String) {
    companion object {
        fun parse(input: String): ApplePlaylistLink? = parse(input, DEFAULT_STOREFRONT)

        fun parse(input: String, defaultStorefront: String): ApplePlaylistLink? = runCatching {
            val value = input.trim()
            if (value.any { it <= ' ' || it == '\u007f' }) return null
            val schemeEnd = value.indexOf("://")
            if (schemeEnd < 0 || !value.substring(0, schemeEnd).equals("https", ignoreCase = true)) return null

            val authorityStart = schemeEnd + 3
            val authorityEnd = value.indexOfFirstFrom(authorityStart) { it == '/' || it == '?' || it == '#' }
                .let { if (it < 0) value.length else it }
            val authority = value.substring(authorityStart, authorityEnd)
            if (!authority.equals("music.apple.com", ignoreCase = true)) return null

            val pathEnd = value.indexOfFirstFrom(authorityEnd) { it == '?' || it == '#' }
                .let { if (it < 0) value.length else it }
            val parts = value.substring(authorityEnd, pathEnd).split('/').filter { it.isNotEmpty() }
            val index = parts.indexOf("playlist")
            if (index !in 0..1 || parts.size <= index + 1) return null
            val storefront = if (index == 1) parts[0] else defaultStorefront
            val id = parts.last()
            if (!storefront.matches(Regex("[a-z]{2}")) || !id.matches(Regex("pl\\.[A-Za-z0-9-]+"))) return null
            ApplePlaylistLink(storefront, id)
        }.getOrNull()
    }
}

private inline fun String.indexOfFirstFrom(startIndex: Int, predicate: (Char) -> Boolean): Int {
    for (index in startIndex until length) if (predicate(this[index])) return index
    return -1
}

private const val DEFAULT_STOREFRONT = "us"

data class ApplePlaylistTrack(val title: String, val artist: String, val durationMs: Int)

data class ApplePlaylist(
    val title: String,
    val tracks: List<ApplePlaylistTrack>,
    val unsupportedTracks: Int,
    val artworkUrl: String? = null,
)

/** Relative pagination links are accepted only for the same playlist's tracks endpoint. */
internal object ApplePlaylistPagination {
    fun validateNextPath(base: String, next: String): String {
        check(next.none { it <= ' ' || it == '\u007f' || it == '\\' }) { "Invalid playlist pagination" }
        var index = 0
        while (index < next.length) {
            if (next[index] == '%') {
                check(index + 2 < next.length && next[index + 1].isHexDigit() && next[index + 2].isHexDigit()) {
                    "Invalid playlist pagination"
                }
                index += 3
            } else {
                index++
            }
        }
        val queryStart = next.indexOf('?')
        val fragmentStart = next.indexOf('#')
        check(fragmentStart < 0) { "Invalid playlist pagination" }
        val pathEnd = if (queryStart < 0) next.length else queryStart
        check(next.substring(0, pathEnd) == "$base/tracks") { "Invalid playlist pagination" }
        check(!next.startsWith("//")) { "Invalid playlist pagination" }
        return next
    }
}

private fun Char.isHexDigit(): Boolean = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
