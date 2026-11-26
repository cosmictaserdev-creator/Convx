package com.music.innertube.mappers

import com.music.innertube.models.SongItem
import kotlin.math.abs

/**
 * Signed out, an album page can list a track as its music video where one exists, and that video can run
 * longer than the song or carry another recording. The album's own playlist still lists the songs in order, so
 * a listed video is swapped for the playlist's song with the same title that follows the previous match.
 */
internal object AlbumTrackAlignment {
    private const val MAX_DURATION_GAP_SECONDS = 90
    private val BRACKETED = Regex("""\s*[(\[][^)\]]*[)\]]""")

    fun withSongsForVideos(listed: List<SongItem>, own: List<SongItem>): List<SongItem> {
        if (own.isEmpty() || listed.none { it.isVideoSong }) return listed
        var cursor = 0
        return listed.map { track ->
            if (!track.isVideoSong) {
                val at = own.indexOfFirstFrom(cursor) { it.id == track.id }
                if (at >= 0) cursor = at + 1
                return@map track
            }
            val at = own.indexOfFirstFrom(cursor) { !it.isVideoSong && sameTrack(track, it) }
            if (at < 0) return@map track
            cursor = at + 1
            own[at]
        }
    }

    private fun sameTrack(video: SongItem, song: SongItem): Boolean =
        sameTitle(video.title, song.title) && closeInLength(video.duration, song.duration)

    private fun sameTitle(a: String, b: String): Boolean {
        val exact = key(a)
        if (exact.isNotEmpty() && exact == key(b)) return true
        val bareA = key(a.replace(BRACKETED, ""))
        return bareA.isNotEmpty() && bareA == key(b.replace(BRACKETED, ""))
    }

    private fun closeInLength(a: Int?, b: Int?): Boolean = a == null || b == null || abs(a - b) <= MAX_DURATION_GAP_SECONDS

    private fun key(title: String): String =
        title.lowercase().filter(Char::isLetterOrDigit).ifEmpty { title.trim().lowercase() }

    private inline fun <T> List<T>.indexOfFirstFrom(start: Int, predicate: (T) -> Boolean): Int {
        for (i in start.coerceAtLeast(0) until size) if (predicate(this[i])) return i
        return -1
    }
}
