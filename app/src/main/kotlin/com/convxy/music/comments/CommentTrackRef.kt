/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import androidx.compose.runtime.Immutable
import com.convxy.music.models.MediaMetadata

/**
 * Everything a [CommentsDataSource] is allowed to know about the track it is being asked about.
 *
 * Convxy plays from several catalogues (YouTube/InnerTube, local files, JioSaavn, Spotify imports)
 * and every one of them has its own id space. None of those ids mean anything to an external
 * comments provider, so the ref carries the *human* identifiers — title, artists, duration — which
 * is what a provider can actually match against, plus an optional [externalTrackId] escape hatch
 * for the case where the caller already knows the provider's own id.
 *
 * Deliberately a value type with no player, Context or Media3 dependency: it is the seam that keeps
 * the UI ignorant of where comments come from, and it is what makes the matching logic unit-testable.
 */
@Immutable
data class CommentTrackRef(
    /** Convxy's own id for the track (InnerTube video id, local-file id, …). Used as the cache key. */
    val id: String,
    val title: String,
    val artistNames: List<String>,
    /** Track length in seconds, `-1`/`0` when unknown — matching then skips the duration check. */
    val durationSeconds: Int,
    val albumName: String? = null,
    /**
     * The provider's own track id or permalink, when Convxy happens to have it (a SoundCloud
     * permalink pasted into a queue, say). Lets a source skip search-and-match entirely.
     */
    val externalTrackId: String? = null,
) {
    val hasUsableTitle: Boolean get() = title.isNotBlank()

    /** Cache key. Includes the duration so a re-tagged local file does not serve stale comments. */
    val cacheKey: String get() = "$id|$durationSeconds"

    companion object {
        fun of(
            id: String,
            title: String,
            artistNames: List<String>,
            durationSeconds: Int,
            albumName: String? = null,
            externalTrackId: String? = null,
        ) = CommentTrackRef(
            id = id,
            title = title,
            artistNames = artistNames.map { it.trim() }.filter { it.isNotEmpty() },
            durationSeconds = durationSeconds,
            albumName = albumName,
            externalTrackId = externalTrackId?.trim()?.takeIf { it.isNotEmpty() },
        )
    }
}

/**
 * Narrows the now-playing [MediaMetadata] down to a [CommentTrackRef].
 *
 * `MediaMetadata.duration` is seconds (it is multiplied by 1000 everywhere it is displayed), which
 * is what the matcher wants; the comment timestamps themselves are milliseconds.
 */
fun MediaMetadata.toCommentTrackRef(): CommentTrackRef =
    CommentTrackRef.of(
        id = id,
        title = title,
        artistNames = artists.map { it.name },
        durationSeconds = duration,
        albumName = album?.title,
    )
