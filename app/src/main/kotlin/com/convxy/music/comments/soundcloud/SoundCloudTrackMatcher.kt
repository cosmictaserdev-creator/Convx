/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import com.convxy.music.comments.TrackMatchScoring

/**
 * A track the SoundCloud search endpoint offered as a possible match.
 *
 * Only the fields matching actually needs — the comments endpoint wants [id], the rest exist to
 * decide whether asking for it is justified.
 */
data class SoundCloudTrackCandidate(
    val id: String,
    val title: String,
    val uploaderName: String,
    /** SoundCloud reports durations in milliseconds, which is why this is not an Int of seconds. */
    val durationMs: Long,
    val permalinkUrl: String? = null,
    /** False when the uploader turned comments off, in which case there is nothing to fetch. */
    val commentable: Boolean? = null,
    val commentCount: Int? = null,
)

/**
 * Decides whether a SoundCloud search result is the same recording Convxy is playing.
 *
 * Convxy's tracks are YouTube/InnerTube ids, local files, JioSaavn or Spotify imports — none of which
 * SoundCloud has ever heard of — so the only way onto its comments is to search by title/artist and
 * then *prove* the hit is the same recording.
 *
 * The scoring itself lives in [TrackMatchScoring], shared with the Audius and YouTube sources so one
 * set of thresholds governs every provider. What stays here is SoundCloud-specific: the candidate
 * type, the fact that its "artist" is the uploading account, and its millisecond durations.
 */
object SoundCloudTrackMatcher {

    /** Re-exported so callers and tests read thresholds off one object rather than two. */
    const val MIN_TITLE_SIMILARITY = TrackMatchScoring.MIN_TITLE_SIMILARITY
    const val ABSOLUTE_DURATION_TOLERANCE_MS = TrackMatchScoring.ABSOLUTE_DURATION_TOLERANCE_MS
    const val RELATIVE_DURATION_TOLERANCE = TrackMatchScoring.RELATIVE_DURATION_TOLERANCE

    /**
     * The best candidate for [title]/[artistNames]/[durationSeconds], or null when nothing clears the
     * bar. Ties break towards the closer duration, then the earlier search rank.
     */
    fun bestMatch(
        candidates: List<SoundCloudTrackCandidate>,
        title: String,
        artistNames: List<String>,
        durationSeconds: Int,
    ): SoundCloudTrackCandidate? = TrackMatchScoring.bestMatch(
        candidates = candidates,
        title = title,
        artistNames = artistNames,
        durationSeconds = durationSeconds,
        titleOf = { it.title },
        artistOf = { it.uploaderName },
        durationMsOf = { it.durationMs },
    )

    /** Combined confidence in 0..1, or null when [candidate] is disqualified outright. */
    fun score(
        normalizedWantedTitle: String,
        normalizedWantedArtistTokens: Set<String>,
        candidate: SoundCloudTrackCandidate,
        wantedDurationMs: Long?,
    ): Float? = TrackMatchScoring.score(
        normalizedWantedTitle = normalizedWantedTitle,
        normalizedWantedArtistTokens = normalizedWantedArtistTokens,
        candidateTitle = candidate.title,
        candidateArtist = candidate.uploaderName,
        candidateDurationMs = candidate.durationMs,
        wantedDurationMs = wantedDurationMs,
    )

    fun durationCompatible(candidateDurationMs: Long, wantedDurationMs: Long?): Boolean =
        TrackMatchScoring.durationCompatible(candidateDurationMs, wantedDurationMs)

    /** The search query to send for a track: primary artist first, then the title. */
    fun searchQuery(title: String, artistNames: List<String>): String =
        TrackMatchScoring.searchQuery(title, artistNames)

    // ── string normalisation ───────────────────────────────────────────────

    fun stripDecorations(raw: String): String = TrackMatchScoring.stripDecorations(raw)

    fun normalizeTitle(raw: String): String = TrackMatchScoring.normalizeTitle(raw)

    fun normalizeArtist(raw: String): String = TrackMatchScoring.normalizeArtist(raw)

    fun tokens(raw: String): List<String> = TrackMatchScoring.tokens(raw)

    fun similarity(a: String, b: String): Float = TrackMatchScoring.similarity(a, b)

    fun tokenOverlap(a: Set<String>, b: Set<String>): Float = TrackMatchScoring.tokenOverlap(a, b)
}
