/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * One user-authored annotation pinned to a moment inside a track.
 *
 * This is deliberately NOT the same concept as lyrics: [com.convxy.music.db.entities.LyricsEntity]
 * and the `lyrics/` providers carry the song's own synchronised text, whereas a comment is
 * somebody's reaction to a point in it. The two share a playback clock and nothing else, so they
 * live in separate packages and never share a provider.
 *
 * Every timestamp is milliseconds from the start of the track — the same unit Media3 uses, so
 * [timestampMs] can be handed straight to `Player.seekTo` with no conversion.
 *
 * `@Immutable` + all-primitive fields keeps this Compose-stable, which matters: the comments list
 * and the seek-bar markers are both recomposed against a playback position that moves constantly,
 * and an unstable model here would invalidate the whole subtree on every tick.
 */
@Immutable
@Serializable
data class TimestampedComment(
    /** Stable id within [sourceName]. Two comments never share one. */
    val id: String,
    /**
     * The track this comment belongs to, in the *provider's* id space (for SoundCloud, its numeric
     * track id) — not Convxy's. Kept so a cached entry can be validated after the fact.
     */
    val trackId: String,
    val authorName: String,
    val text: String,
    /** Milliseconds from the start of the track. Always `>= 0`. */
    val timestampMs: Long,
    val avatarUrl: String? = null,
    val authorUrl: String? = null,
    /** Deep link back to the comment on the provider, for "view source". Null when unavailable. */
    val permalink: String? = null,
    /** Epoch millis the comment was written, when the provider supplies it. */
    val createdAtEpochMs: Long? = null,
    /** Provider-dependent engagement metadata; null means the provider does not expose it. */
    val likeCount: Int? = null,
    val replyCount: Int? = null,
    /** Which [CommentsDataSource] produced this. Shown in the sheet header, never invented. */
    val sourceName: String,
) {
    /** Position as a 0..1 fraction of a [durationMs]-long track, clamped. */
    fun fractionOf(durationMs: Long): Float =
        if (durationMs <= 0L) 0f else (timestampMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
}
