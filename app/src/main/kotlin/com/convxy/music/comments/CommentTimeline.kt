/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import androidx.compose.runtime.Immutable
import com.convxy.music.utils.makeTimeString

/**
 * Pure timeline arithmetic for timestamped comments: cleaning, ordering, "which comment is live
 * right now", and clustering comments into seek-bar markers.
 *
 * Everything here is a plain function over plain data — no Android, no player, no coroutines — so
 * it is the part of the feature that is unit-tested directly. The player UI contributes only the
 * current position; the decision of what that position means belongs here.
 */
object CommentTimeline {

    /**
     * How long a comment stays "current" once playback reaches it, when nothing follows it.
     *
     * Without a cap, a comment at 0:05 on a track whose next comment is at 3:00 would stay
     * highlighted for three minutes, which reads as a stuck UI rather than a live one. When another
     * comment is closer than this, that comment's own timestamp ends the window instead — so a dense
     * passage hands off from one comment to the next with no dead time.
     */
    const val DEFAULT_ACTIVE_WINDOW_MS = 20_000L

    /**
     * Comments this close together are treated as one moment: one seek-bar marker, one highlighted
     * group. SoundCloud stacks them; listing three separate "active" comments 40ms apart would make
     * the highlight flicker between them as the position ticks past.
     */
    const val MERGE_EPSILON_MS = 500L

    /** Hard ceiling on how many comments we keep for one track. */
    const val MAX_COMMENTS = 400

    /** Ceiling on distinct seek-bar markers, so a comment-dense track cannot turn into a barcode. */
    const val MAX_MARKERS = 48

    /** Markers closer than this fraction of the track are merged. */
    const val DEFAULT_MARKER_GAP_FRACTION = 0.012f

    // ── cleaning + ordering ────────────────────────────────────────────────

    /**
     * Turns whatever a provider returned into a list that is safe to render and safe to seek with.
     *
     * Drops comments with no text, no usable timestamp, or a timestamp outside the track; drops
     * duplicate ids; then sorts by timestamp with a stable tiebreak so the order never shifts
     * between recompositions or between a cached read and a fresh fetch.
     *
     * @param durationMs the track length, or null/`<= 0` when unknown — the upper-bound check is then
     *   skipped rather than rejecting everything.
     */
    fun normalize(
        raw: List<TimestampedComment>,
        durationMs: Long?,
    ): List<TimestampedComment> {
        if (raw.isEmpty()) return emptyList()
        val usableDuration = durationMs?.takeIf { it > 0L }
        val seen = HashSet<String>(raw.size)
        val out = ArrayList<TimestampedComment>(raw.size)
        for (comment in raw) {
            if (comment.text.isBlank()) continue
            if (comment.timestampMs < 0L) continue
            if (usableDuration != null && comment.timestampMs > usableDuration) continue
            // Provider ids are unique per provider; prefix with the source so two sources stacked
            // behind one repository can never collide in the dedupe set or in Compose keys.
            val key = comment.sourceName + "#" + comment.id
            if (!seen.add(key)) continue
            out.add(comment)
        }
        out.sortWith(COMPARATOR)
        return if (out.size > MAX_COMMENTS) out.subList(0, MAX_COMMENTS) else out
    }

    /**
     * Timestamp first, then oldest-written first, then id — the last one only so that comments with
     * an identical timestamp AND no creation date still have one deterministic order.
     */
    private val COMPARATOR: Comparator<TimestampedComment> =
        compareBy<TimestampedComment> { it.timestampMs }
            .thenBy { it.createdAtEpochMs ?: Long.MAX_VALUE }
            .thenBy { it.id }

    /**
     * Rejects a malformed provider timestamp instead of letting it reach `seekTo`.
     *
     * Returns null for a missing value, a negative one, or one past the end of the track.
     */
    fun sanitizeTimestampMs(raw: Long?, durationMs: Long?): Long? {
        if (raw == null || raw < 0L) return null
        val usable = durationMs?.takeIf { it > 0L } ?: return raw
        return if (raw <= usable) raw else null
    }

    // ── which comment is live ──────────────────────────────────────────────

    /**
     * A run of comments that all belong to the same moment in the track.
     *
     * [startIndex]..[endIndexInclusive] index into the *normalised* (already sorted) list, so the UI
     * can highlight the whole run and scroll to its head without a second search.
     */
    @Immutable
    data class CommentGroup(
        val startIndex: Int,
        val endIndexInclusive: Int,
        val timestampMs: Long,
    ) {
        val size: Int get() = endIndexInclusive - startIndex + 1
        val representativeIndex: Int get() = startIndex
    }

    /**
     * The group of comments playback is currently inside, or null when the position is in a gap.
     *
     * Binary search over the sorted timestamps — O(log n) per call — because this runs against the
     * player's existing 100ms position poll. Callers wrap it in `derivedStateOf`, so the cost that
     * matters is not the search but the number of *distinct results* it produces: returning the same
     * group for 20 seconds straight means zero recompositions in that time.
     */
    fun activeGroup(
        comments: List<TimestampedComment>,
        positionMs: Long,
        windowMs: Long = DEFAULT_ACTIVE_WINDOW_MS,
        mergeEpsilonMs: Long = MERGE_EPSILON_MS,
    ): CommentGroup? {
        if (comments.isEmpty() || positionMs < 0L) return null

        // Last comment at or before the playhead.
        val at = lastIndexAtOrBefore(comments, positionMs) ?: return null
        val (start, end) = groupAround(comments, at, mergeEpsilonMs)

        val groupTimestamp = comments[start].timestampMs
        // The window closes at whichever comes first: the next comment, or the read-time cap.
        val nextTimestamp = comments.getOrNull(end + 1)?.timestampMs ?: Long.MAX_VALUE
        val closesAt = minOf(groupTimestamp + windowMs, nextTimestamp)
        return if (positionMs < closesAt) CommentGroup(start, end, groupTimestamp) else null
    }

    /** Id of the comment that "represents" the live moment, for cheap equality checks. */
    fun activeCommentId(
        comments: List<TimestampedComment>,
        positionMs: Long,
        windowMs: Long = DEFAULT_ACTIVE_WINDOW_MS,
    ): String? = activeGroup(comments, positionMs, windowMs)?.let { comments[it.representativeIndex].id }

    /**
     * The whole run of comments that [index] belongs to, as an inclusive `startIndex..endIndex` pair.
     *
     * One rule, used by both [activeGroup] and [displayGroups]: consecutive comments merge when they
     * are within [mergeEpsilonMs] *of each other*, chaining through a run. Sharing that rule is the
     * point — it is what guarantees the group the playhead is inside is always exactly one of the
     * groups drawn on screen, so the highlight and the list can never disagree about a boundary.
     */
    private fun groupAround(
        comments: List<TimestampedComment>,
        index: Int,
        mergeEpsilonMs: Long,
    ): Pair<Int, Int> {
        var start = index
        while (start > 0 &&
            comments[start].timestampMs - comments[start - 1].timestampMs <= mergeEpsilonMs
        ) {
            start--
        }
        var end = index
        while (end < comments.lastIndex &&
            comments[end + 1].timestampMs - comments[end].timestampMs <= mergeEpsilonMs
        ) {
            end++
        }
        return start to end
    }

    /**
     * Partitions a normalised list into the groups the sheet renders, in playback order.
     *
     * Comments within [mergeEpsilonMs] of each other are one moment and get one timestamp heading;
     * listing them separately would put three identical `0:41`s in a row and make the highlight
     * flicker between them as the position ticks past. Groups are contiguous ranges of [comments], so
     * a group's contents are `comments.subList(startIndex, endIndexInclusive + 1)`.
     *
     * Requires the list to be sorted, which [normalize] guarantees.
     */
    fun displayGroups(
        comments: List<TimestampedComment>,
        mergeEpsilonMs: Long = MERGE_EPSILON_MS,
    ): List<CommentGroup> {
        if (comments.isEmpty()) return emptyList()
        val out = ArrayList<CommentGroup>()
        var start = 0
        while (start <= comments.lastIndex) {
            val (_, end) = groupAround(comments, start, mergeEpsilonMs)
            out.add(CommentGroup(start, end, comments[start].timestampMs))
            start = end + 1
        }
        return out
    }

    /**
     * Index of the last comment whose timestamp is `<= positionMs`, or null if the playhead is
     * before the first one. Classic upper-bound binary search; the list must be sorted ascending.
     */
    private fun lastIndexAtOrBefore(comments: List<TimestampedComment>, positionMs: Long): Int? {
        var low = 0
        var high = comments.lastIndex
        var answer: Int? = null
        while (low <= high) {
            val mid = (low + high) ushr 1
            if (comments[mid].timestampMs <= positionMs) {
                answer = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return answer
    }

    /**
     * The ms to hand `Player.seekTo` for [comment]: exactly its timestamp, floored at zero.
     *
     * Kept as a function rather than reading the field at the call site so there is one place that
     * decides what "tap a comment" means, and one place to test it.
     */
    fun seekTargetMs(comment: TimestampedComment): Long = comment.timestampMs.coerceAtLeast(0L)

    // ── seek-bar markers ───────────────────────────────────────────────────

    /** One dot on the seek bar: a position, and how many comments are stacked there. */
    @Immutable
    data class CommentMarker(
        val fraction: Float,
        val timestampMs: Long,
        val count: Int,
        val firstIndex: Int,
        val lastIndexInclusive: Int,
    )

    /**
     * Collapses [comments] into at most [maxMarkers] positions spread over [durationMs].
     *
     * A comment-dense track would otherwise paint hundreds of overlapping dots over the seek bar and
     * make it unreadable, so anything within [minGapFraction] of the previous marker joins it and the
     * marker reports a [CommentMarker.count] instead. If that still overflows [maxMarkers] the gap is
     * widened to `duration / maxMarkers` and the clustering is redone — bounded output, no matter how
     * many comments come back.
     */
    fun markers(
        comments: List<TimestampedComment>,
        durationMs: Long?,
        minGapFraction: Float = DEFAULT_MARKER_GAP_FRACTION,
        maxMarkers: Int = MAX_MARKERS,
    ): List<CommentMarker> {
        val duration = durationMs?.takeIf { it > 0L } ?: return emptyList()
        if (comments.isEmpty()) return emptyList()

        val preferred = cluster(comments, duration, (minGapFraction.coerceAtLeast(0f) * duration).toLong())
        if (preferred.size <= maxMarkers) return preferred

        // Still too dense: force the gap so the count can never exceed maxMarkers.
        val forced = cluster(comments, duration, duration / maxMarkers.coerceAtLeast(1))
        return if (forced.size <= maxMarkers) forced else forced.take(maxMarkers)
    }

    private fun cluster(
        comments: List<TimestampedComment>,
        durationMs: Long,
        minGapMs: Long,
    ): List<CommentMarker> {
        val gap = minGapMs.coerceAtLeast(0L)
        val out = ArrayList<CommentMarker>()
        var start = 0
        while (start <= comments.lastIndex) {
            var end = start
            while (end < comments.lastIndex &&
                comments[end + 1].timestampMs - comments[start].timestampMs <= gap
            ) {
                end++
            }
            // Marker sits at the mean of its cluster, so a wide cluster does not read as being
            // pinned to its earliest member.
            var sum = 0L
            for (i in start..end) sum += comments[i].timestampMs
            val mean = sum / (end - start + 1)
            out.add(
                CommentMarker(
                    fraction = (mean.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f),
                    timestampMs = mean,
                    count = end - start + 1,
                    firstIndex = start,
                    lastIndexInclusive = end,
                ),
            )
            start = end + 1
        }
        return out
    }

    /**
     * The marker that contains [group]'s representative comment, for highlighting the live dot.
     *
     * Lookup is by index range rather than by timestamp: after clustering, several groups can share a
     * marker, and a timestamp comparison would pick the wrong one whenever two clusters are adjacent.
     */
    fun markerForGroup(
        markers: List<CommentMarker>,
        group: CommentGroup,
    ): CommentMarker? =
        markers.firstOrNull { group.representativeIndex in it.firstIndex..it.lastIndexInclusive }

    // ── display ────────────────────────────────────────────────────────────

    /**
     * `0:18` / `1:42` / `2:57` — the same formatting the rest of the app uses for durations, reused
     * rather than re-implemented so a comment's timestamp and the seek bar's elapsed label can never
     * disagree about how a moment is written.
     */
    fun formatTimestamp(timestampMs: Long): String = makeTimeString(timestampMs.coerceAtLeast(0L))
}
