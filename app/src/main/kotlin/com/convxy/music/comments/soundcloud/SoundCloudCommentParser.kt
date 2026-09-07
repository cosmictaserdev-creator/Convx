/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import com.convxy.music.comments.TimestampedComment
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Turns SoundCloud's JSON into [TimestampedComment]s, dropping anything that cannot be trusted.
 *
 * Split out from the HTTP client so that the part with actual judgement in it — what counts as a
 * usable timestamp, what a malformed payload costs — is plain synchronous code with plain inputs and
 * can be unit-tested without a socket.
 *
 * The rule throughout is that a bad *comment* is skipped and a bad *response* yields an empty list.
 * Neither is allowed to throw: the caller converts an empty result into "no comments", which is true,
 * rather than into a crash in the middle of the player.
 */
object SoundCloudCommentParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }

    /** SoundCloud's `created_at` is `yyyy/MM/dd HH:mm:ss Z`-ish; only the UTC wall time is read. */
    private val CREATED_AT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][' ']HH:mm:ss")

    // ── envelope handling ──────────────────────────────────────────────────

    /**
     * Comments from either shape the API returns: a bare array (no `linked_partitioning`) or a
     * `{"collection": […]}` envelope. Unparseable input is an empty list, never an exception.
     */
    fun parseComments(body: String): List<ScComment> =
        decodeList<ScComment>(body)

    /** Search results, same two shapes. */
    fun parseTracks(body: String): List<ScTrack> = decodeList<ScTrack>(body)

    /** A single `GET /tracks/{urn}` object, or null when it is missing/malformed/an error body. */
    fun parseTrack(body: String): ScTrack? = runCatching {
        val text = body.trim()
        if (text.isEmpty() || text.startsWith("[") || text.startsWith("\"")) null
        else json.decodeFromString(ScTrack.serializer(), text)
    }.getOrNull()

    /** `next_href` from a paginated envelope, or null when the body is a bare array / has no more pages. */
    fun nextHref(body: String): String? = runCatching {
        val text = body.trim()
        if (!text.startsWith("{")) return@runCatching null
        json.decodeFromString<ScCollection<ScComment>>(text).nextHref?.takeIf { it.isNotBlank() }
    }.getOrNull()

    private inline fun <reified T> decodeList(body: String): List<T> = runCatching {
        val text = body.trim()
        if (text.isEmpty()) {
            emptyList()
        } else if (text.startsWith("[")) {
            json.decodeFromString<List<T>>(text)
        } else {
            json.decodeFromString<ScCollection<T>>(text).collection
        }
    }.getOrDefault(emptyList())

    // ── DTO -> domain ──────────────────────────────────────────────────────

    /**
     * One wire comment to one [TimestampedComment], or null when it must be dropped.
     *
     * Dropped, in order of how often it actually happens:
     *  - no `timestamp`. SoundCloud allows untimed comments on a track; those are not timestamped
     *    comments and showing them on a seek bar would be a lie about where they belong.
     *  - negative, or past the end of the track. A provider bug or a re-uploaded shorter take;
     *    seeking there would jump the player outside the media item.
     *  - blank body, or no id (ids are the list key and the dedupe key).
     */
    fun toComment(
        dto: ScComment,
        trackId: String,
        sourceName: String,
        trackDurationMs: Long?,
        trackPermalinkUrl: String? = null,
    ): TimestampedComment? {
        val id = dto.id?.takeIf { it > 0L }?.toString() ?: return null
        val body = dto.body?.trim().orEmpty()
        if (body.isEmpty()) return null

        val timestamp = dto.timestamp ?: return null
        if (timestamp < 0L) return null
        val usableDuration = trackDurationMs?.takeIf { it > 0L }
        if (usableDuration != null && timestamp > usableDuration) return null

        val user = dto.user
        val author = user?.username?.trim().takeUnless { it.isNullOrEmpty() }
            ?: user?.permalink?.trim()?.takeUnless { it.isNullOrEmpty() }
            ?: return null // an anonymous reaction cannot be attributed, and inventing a name is worse

        return TimestampedComment(
            id = id,
            trackId = trackId,
            authorName = author,
            text = body,
            timestampMs = timestamp,
            // `user` is still typed ScUser? here: reaching a non-null author proves the user object
            // existed, but it proved it through a ?: chain, which smart-casts nothing.
            avatarUrl = user?.avatarUrl?.takeIf { it.isNotBlank() },
            authorUrl = user?.permalinkUrl?.takeIf { it.isNotBlank() },
            permalink = trackPermalinkUrl?.takeIf { it.isNotBlank() },
            createdAtEpochMs = parseCreatedAt(dto.createdAt),
            // The official API does not expose per-comment like/reply counts. Left null rather than
            // zero so the UI can omit the affordance instead of asserting a false "0 likes".
            likeCount = null,
            replyCount = null,
            sourceName = sourceName,
        )
    }

    /** Wire list to domain list, in one pass, dropping what [toComment] rejects. */
    fun toComments(
        dtos: List<ScComment>,
        trackId: String,
        sourceName: String,
        trackDurationMs: Long?,
        trackPermalinkUrl: String? = null,
    ): List<TimestampedComment> =
        dtos.mapNotNull { toComment(it, trackId, sourceName, trackDurationMs, trackPermalinkUrl) }

    /** A search hit to a match candidate, or null when it has no id (and so cannot be queried). */
    fun toCandidate(dto: ScTrack): SoundCloudTrackCandidate? {
        val id = dto.id?.takeIf { it > 0L }?.toString() ?: return null
        return SoundCloudTrackCandidate(
            id = id,
            title = dto.title.orEmpty(),
            uploaderName = dto.user?.username.orEmpty(),
            durationMs = dto.duration ?: 0L,
            permalinkUrl = dto.permalinkUrl?.takeIf { it.isNotBlank() },
            commentable = dto.commentable,
            commentCount = dto.commentCount,
        )
    }

    /**
     * SoundCloud's `created_at` to epoch millis, or null when it is missing or unparseable.
     *
     * Written by hand rather than with a lenient `SimpleDateFormat`: the value is only ever used to
     * order comments that share a timestamp, so an unparseable one degrades to "no creation date"
     * instead of throwing inside a `parseComments` call that has already succeeded.
     */
    fun parseCreatedAt(raw: String?): Long? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return runCatching {
            // The offset suffix ("+0000" / "Z") is dropped: the formatter is fixed and SoundCloud
            // always reports UTC, so there is nothing to convert.
            val withoutOffset = text
                .removeSuffix("Z")
                .replace(Regex("""[+-]\d{2}:?\d{2}$"""), "")
                .trim()
            LocalDateTime.parse(withoutOffset, CREATED_AT).toInstant(ZoneOffset.UTC).toEpochMilli()
        }.getOrNull()
    }
}
