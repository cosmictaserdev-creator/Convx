/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.audius

import com.convxy.music.comments.TrackMatchScoring

/**
 * A track the Audius search endpoint offered as a possible match.
 *
 * Durations are already in milliseconds by the time one of these exists — Audius reports seconds, and
 * converting once in [AudiusCommentParser.toCandidate] rather than at every comparison is what stops
 * that unit difference from becoming a bug.
 */
data class AudiusTrackCandidate(
    val id: String,
    val title: String,
    /** The uploading artist's display name, or their `@handle` when they never set one. */
    val artistName: String,
    val durationMs: Long,
    /** Track path on audius.co, e.g. `/jayb1rdmusic/washed-out-…`. Used to build comment permalinks. */
    val permalink: String? = null,
    /** False when the artist turned comments off, in which case there is nothing to fetch. */
    val commentsDisabled: Boolean? = null,
    val commentCount: Int? = null,
)

/**
 * Decides whether an Audius search result is the same recording Convxy is playing.
 *
 * Audius is a smaller catalogue than SoundCloud and skews electronic, so most Convxy tracks will not be
 * on it at all and [bestMatch] returning null is the common case, not the failure case. That is fine:
 * the repository simply asks the next source in priority order, which is the entire reason there is a
 * priority order.
 *
 * What must not happen is a confident wrong answer. Attaching real people's timed reactions to the
 * wrong recording shows a comment about a drop at 2:31 on a track where nothing happens at 2:31, and
 * there is no way for the user to tell the difference. So the same conservative bar the SoundCloud
 * matcher uses applies here — shared [TrackMatchScoring], shared thresholds, one place to tune.
 *
 * Audius's "artist" is cleaner than SoundCloud's, by the way: SoundCloud credits whichever account
 * uploaded the file, which for a repost is not the artist. Audius credits the uploader too, but uploads
 * are by the artists themselves far more often, so the artist term carries a little more weight here in
 * practice without being scored any differently.
 */
object AudiusTrackMatcher {

    /**
     * The best candidate for [title]/[artistNames]/[durationSeconds], or null when nothing clears the
     * bar. Ties break towards the closer duration, then the earlier search rank.
     */
    fun bestMatch(
        candidates: List<AudiusTrackCandidate>,
        title: String,
        artistNames: List<String>,
        durationSeconds: Int,
    ): AudiusTrackCandidate? = TrackMatchScoring.bestMatch(
        candidates = candidates,
        title = title,
        artistNames = artistNames,
        durationSeconds = durationSeconds,
        titleOf = { it.title },
        artistOf = { it.artistName },
        durationMsOf = { it.durationMs },
    )

    /** The search query to send for a track: primary artist first, then the title. */
    fun searchQuery(title: String, artistNames: List<String>): String =
        TrackMatchScoring.searchQuery(title, artistNames)

    fun normalizeTitle(raw: String): String = TrackMatchScoring.normalizeTitle(raw)

    fun normalizeArtist(raw: String): String = TrackMatchScoring.normalizeArtist(raw)

    fun durationCompatible(candidateDurationMs: Long, wantedDurationMs: Long?): Boolean =
        TrackMatchScoring.durationCompatible(candidateDurationMs, wantedDurationMs)
}
