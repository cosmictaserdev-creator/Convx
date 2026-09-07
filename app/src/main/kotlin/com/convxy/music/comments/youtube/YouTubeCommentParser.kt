/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.youtube

import com.convxy.music.comments.TimestampedComment
import com.music.innertube.models.Run
import com.music.innertube.models.comment.CommentRenderer
import com.music.innertube.models.comment.CommentThreadRenderer

/**
 * Reads the timestamp out of a YouTube comment, and maps comment renderers onto
 * [TimestampedComment]s.
 *
 * YouTube has no timed-comment field. What it has is a convention strong enough to be a feature of the
 * product: a viewer writes `1:23` or `[1:23]`, and YouTube turns it into a link that seeks the video to
 * that second. So the timestamp is recoverable two ways, and this tries them in order of trust:
 *
 *  1. **Structured.** The linked run carries `watchEndpoint.startTimeSeconds` — YouTube's own parse of
 *     the moment, already in seconds. When it is there it is authoritative, and it is there for exactly
 *     the comments YouTube considers timestamped.
 *  2. **Textual.** A viewer may have written a time YouTube did not link (a typo'd bracket, a plain
 *     `2:45` mid-sentence). Falling back to parsing the text recovers those.
 *
 * Both are best-effort and both can come up empty, which is the common case: the overwhelming majority
 * of YouTube comments are not about a moment at all. Those are dropped, not defaulted to 0:00 — a
 * comment shown at the start of the song that was actually about the whole song is a lie about where
 * the reaction belongs, and it would clutter the seek bar with ticks that mean nothing.
 *
 * This is a genuinely weaker signal than Audius's `track_timestamp_s` or SoundCloud's `timestamp`, and
 * that is why YouTube sits second in the default priority despite having by far the widest coverage:
 * it finds comments for more tracks, but a smaller fraction of what it finds is pinned to a moment.
 *
 * Pure functions over plain data — no client, no Context — because the regexes below are the part of
 * this feature most worth having tests for.
 */
object YouTubeCommentParser {

    /**
     * `h:mm:ss` or `m:ss`.
     *
     * The hour alternative is listed first so `1:02:03` reads as one hour two minutes three seconds
     * rather than matching on its `2:03` tail. Seconds are constrained to `[0-5]\d`, which is what stops
     * a bare ratio like `2:1` or a version number like `1:2` from being read as a time — a real
     * timestamp always writes its seconds with two digits.
     */
    private val TIMESTAMP = Regex("""(?<h>\d{1,2}):(?<m>\d{1,2}):(?<s>[0-5]\d)|(?<m2>\d{1,3}):(?<s2>[0-5]\d)""")

    /**
     * Sanity ceiling on a parsed timestamp. [com.convxy.music.comments.CommentTimeline.normalize]
     * already rejects anything past the end of the track, but the track length is not always known — a
     * local file with no duration tag has none — and without this a stray `999:99`-shaped number in a
     * comment would survive as a marker 16 hours into the song.
     */
    private const val MAX_TIMESTAMP_MS = 12L * 60 * 60 * 1000

    // ── timestamps ─────────────────────────────────────────────────────────

    /**
     * The moment a comment is about, in milliseconds, or null when it is not about a moment.
     *
     * Takes the FIRST timestamp in the comment. A viewer can write several ("1:23 and 3:45 both hit"),
     * and this feature pins one comment to one position, so the first is the one the comment opens
     * with and is therefore the one it is most likely to be about. Splitting one comment into several
     * would duplicate its text across the timeline and collide with itself in the id-keyed dedupe.
     */
    fun timestampMs(runs: List<Run>?): Long? {
        if (runs.isNullOrEmpty()) return null

        // YouTube's own parse of the linked time, which is the more trustworthy of the two signals.
        runs.firstNotNullOfOrNull { run ->
            run.navigationEndpoint?.watchEndpoint?.startTimeSeconds?.takeIf { it >= 0 }
        }?.let { return it.toLong() * 1000L }

        return timestampFromText(runs.joinToString("") { it.text })
    }

    /** The first `h:mm:ss` or `m:ss` in [text], in milliseconds, or null if there is none. */
    fun timestampFromText(text: String): Long? {
        if (text.isEmpty()) return null
        val match = TIMESTAMP.find(text) ?: return null
        val groups = match.groups
        val millis = if (groups["h"] != null) {
            val hours = groups["h"]?.value?.toLongOrNull() ?: return null
            val minutes = groups["m"]?.value?.toLongOrNull() ?: return null
            val seconds = groups["s"]?.value?.toLongOrNull() ?: return null
            ((hours * 60 + minutes) * 60 + seconds) * 1000L
        } else {
            val minutes = groups["m2"]?.value?.toLongOrNull() ?: return null
            val seconds = groups["s2"]?.value?.toLongOrNull() ?: return null
            (minutes * 60 + seconds) * 1000L
        }
        return millis.takeIf { it in 0..MAX_TIMESTAMP_MS }
    }

    // ── counts ─────────────────────────────────────────────────────────────

    /**
     * YouTube writes engagement counts the way it displays them — `5`, `1.2K`, `3,4 M`, or nothing at
     * all when the count is zero — so turning one into an [Int] means reading a human-formatted string.
     *
     * Null when it cannot be read, rather than 0: "no likes" and "likes not shown" are different facts
     * and the model has a way to say the second one.
     */
    fun parseCount(text: String?): Int? {
        val raw = text?.trim().orEmpty()
        if (raw.isEmpty()) return null
        val match = Regex("""(\d+(?:[.,]\d+)?)\s*([KkMm]?)""").find(raw) ?: return null
        val value = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return null
        val scaled = value * when (match.groupValues[2].uppercase()) {
            "K" -> 1_000.0
            "M" -> 1_000_000.0
            else -> 1.0
        }
        return if (scaled >= Int.MAX_VALUE) Int.MAX_VALUE else scaled.toInt()
    }

    // ── track identity ─────────────────────────────────────────────────────

    /**
     * Whether [id] is a YouTube video id rather than some other catalogue's.
     *
     * Convxy's `MediaMetadata.id` is a video id for anything that came from InnerTube and a local
     * identifier for a file on disk, and nothing on the model says which. Rather than guess from
     * metadata this reads the id's shape: video ids are exactly eleven characters of base64url, which
     * no MediaStore row id or library key looks like.
     *
     * The all-digits exclusion is the one judgement call. An eleven-digit string is technically a
     * legal video id, but it is overwhelmingly more likely to be a numeric local id, and a false
     * positive here costs an InnerTube request that finds nothing — while rejecting a genuine video id
     * is roughly a one-in-a-billion event.
     */
    fun looksLikeVideoId(id: String?): Boolean {
        val value = id?.trim().orEmpty()
        if (value.length != VIDEO_ID_LENGTH) return false
        if (!value.all { it.isLetterOrDigit() || it == '_' || it == '-' }) return false
        return !value.all { it.isDigit() }
    }

    private const val VIDEO_ID_LENGTH = 11

    // ── renderer → domain ──────────────────────────────────────────────────

    /**
     * Maps a page of comment threads onto timed comments, dropping every thread that is not about a
     * moment. Only top-level threads are read; replies are counted by [TimestampedComment.replyCount]
     * but not flattened into the timeline, matching how `ui/screens/CommentSheet.kt` already treats
     * them as a separate pane.
     */
    fun toComments(
        threads: List<CommentThreadRenderer>,
        videoId: String,
        sourceName: String,
        trackDurationMs: Long?,
    ): List<TimestampedComment> = threads.mapNotNull { thread ->
        thread.comment?.commentRenderer?.let { renderer ->
            toComment(renderer, videoId, sourceName, trackDurationMs)
        }
    }

    /** One comment, or null when it carries no usable timestamp, no text or no author. */
    fun toComment(
        renderer: CommentRenderer,
        videoId: String,
        sourceName: String,
        trackDurationMs: Long?,
    ): TimestampedComment? {
        val id = renderer.commentId?.trim().takeUnless { it.isNullOrEmpty() } ?: return null

        val runs = renderer.contentText?.runs
        val timestampMs = timestampMs(runs) ?: return null
        if (trackDurationMs != null && trackDurationMs > 0L && timestampMs > trackDurationMs) return null

        val text = runs?.joinToString("") { it.text }?.trim().takeUnless { it.isNullOrEmpty() }
            ?: return null

        // An unattributable reaction is dropped rather than given a placeholder: inventing an author
        // for somebody's real words is worse than showing one fewer comment.
        val author = renderer.authorText?.runs?.firstOrNull()?.text?.trim()
            ?.takeUnless { it.isEmpty() } ?: return null

        val channelId = renderer.authorEndpoint?.browseEndpoint?.browseId
            ?.trim()?.takeUnless { it.isEmpty() }

        return TimestampedComment(
            id = id,
            trackId = videoId,
            authorName = author,
            text = text,
            timestampMs = timestampMs,
            avatarUrl = renderer.authorThumbnail?.thumbnails?.lastOrNull()?.url?.takeIf { it.isNotBlank() },
            authorUrl = channelId?.let { "$YOUTUBE_WEB/channel/$it" },
            permalink = "$YOUTUBE_WEB/watch?v=$videoId&lc=$id",
            // Deliberately null. `publishedTimeText` is a relative string — "3 days ago" — and turning
            // that into an epoch would manufacture a precision the payload does not have. The sheet
            // shows nothing for it rather than showing a wrong date.
            createdAtEpochMs = null,
            likeCount = parseCount(renderer.voteCount?.runs?.firstOrNull()?.text),
            replyCount = renderer.replyCount,
            sourceName = sourceName,
        )
    }

    private const val YOUTUBE_WEB = "https://www.youtube.com"
}
