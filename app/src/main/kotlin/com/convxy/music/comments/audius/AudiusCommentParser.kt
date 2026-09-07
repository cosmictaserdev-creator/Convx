/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.audius

import com.convxy.music.comments.TimestampedComment
import kotlinx.serialization.json.Json
import java.time.Instant

/**
 * Turns Audius payloads into [TimestampedComment]s, and drops whatever cannot honestly become one.
 *
 * The dropping is the important half. A comment with no `track_timestamp_s` is a real comment about
 * the track as a whole — Audius lets you post without the player running — but this feature pins
 * reactions to moments, and defaulting such a comment to 0:00 would assert it belongs at the start of
 * the song. It does not. Tombstoned (deleted) and muted (moderated) comments are excluded for the same
 * reason a provider excludes them: showing them would be showing something the author or a moderator
 * withdrew.
 *
 * Mirrors [com.convxy.music.comments.soundcloud.SoundCloudCommentParser] deliberately — same two-stage
 * shape (decode to DTOs, then map to the domain type), same "never invent an author" rule, same
 * tolerance for a payload with fields missing. Two providers that parse the same way are two providers
 * whose bugs look the same, which is worth more than either one being individually clever.
 */
object AudiusCommentParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }

    /**
     * One decoded comments page: the comments, the users they reference, and the track they belong to.
     *
     * [usersById] is the reason a page costs one request instead of one per commenter — Audius inlines
     * every referenced user under `related.users`, so attribution and avatars come with the payload.
     * [track] comes from `related.tracks` the same way, and is what supplies the permalink when the
     * caller resolved the track by id rather than by search.
     */
    data class CommentPage(
        val comments: List<AudiusComment>,
        val usersById: Map<String, AudiusUser>,
        val track: AudiusTrack?,
    ) {
        companion object {
            val EMPTY = CommentPage(emptyList(), emptyMap(), null)
        }
    }

    // ── decoding ───────────────────────────────────────────────────────────

    /** The discovery document's host list. Empty on a malformed body, never an exception. */
    fun parseHosts(body: String): List<String> =
        runCatching { json.decodeFromString(AudiusHosts.serializer(), body).data }
            .getOrDefault(emptyList())
            .filter { it.isNotBlank() }

    fun parseTracks(body: String): List<AudiusTrack> = runCatching {
        json.decodeFromString(AudiusList.serializer(AudiusTrack.serializer()), body).data
    }.getOrDefault(emptyList())

    /**
     * A single-track response. Audius returns one track as a bare object rather than a list envelope,
     * so try the envelope first (a search-shaped body still works) and fall back to the object.
     */
    fun parseTrack(body: String): AudiusTrack? =
        parseTracks(body).firstOrNull()
            ?: runCatching { json.decodeFromString(AudiusTrack.serializer(), body) }.getOrNull()

    /**
     * Decodes a comments page and indexes its `related.users` by id in one pass.
     *
     * A malformed body yields [CommentPage.EMPTY] rather than throwing: the caller is on the player's
     * coroutine, and a bad response from one community-run discovery node must cost a "could not load"
     * state, not a crash.
     */
    fun parseCommentPage(body: String): CommentPage = runCatching {
        val page = json.decodeFromString(AudiusList.serializer(AudiusComment.serializer()), body)
        val users = page.related?.users.orEmpty()
            .mapNotNull { user -> user.id?.takeIf { it.isNotBlank() }?.let { it to user } }
            .toMap()
        CommentPage(
            comments = page.data,
            usersById = users,
            track = page.related?.tracks?.firstOrNull(),
        )
    }.getOrDefault(CommentPage.EMPTY)

    // ── DTO → domain ───────────────────────────────────────────────────────

    /**
     * Narrows a track payload to what matching and fetching need, or null when it cannot be used.
     *
     * This is also where Audius's seconds become milliseconds — once, here, so no comparison anywhere
     * else has to remember which unit the provider spoke in.
     */
    fun toCandidate(dto: AudiusTrack): AudiusTrackCandidate? {
        val id = dto.id?.trim().takeUnless { it.isNullOrEmpty() } ?: return null
        val title = dto.title?.trim().takeUnless { it.isNullOrEmpty() } ?: return null
        val user = dto.user
        return AudiusTrackCandidate(
            id = id,
            title = title,
            artistName = user?.name?.trim()?.takeUnless { it.isEmpty() }
                ?: user?.handle?.trim()?.takeUnless { it.isEmpty() }
                ?: "",
            durationMs = (dto.duration ?: 0L).coerceAtLeast(0L) * 1000L,
            permalink = dto.permalink?.trim()?.takeUnless { it.isNullOrEmpty() },
            commentsDisabled = dto.commentsDisabled,
            commentCount = dto.commentCount,
        )
    }

    /**
     * Maps a page of Audius comments onto [TimestampedComment]s.
     *
     * @param trackDurationMs used to reject a timestamp beyond the end of the track. Null when the
     *   length is unknown, in which case that check is skipped rather than rejecting everything.
     */
    fun toComments(
        page: CommentPage,
        trackId: String,
        sourceName: String,
        trackDurationMs: Long?,
        trackPermalink: String? = page.track?.permalink,
    ): List<TimestampedComment> = page.comments.mapNotNull { dto ->
        toComment(
            dto = dto,
            user = dto.userId?.let { page.usersById[it] },
            trackId = trackId,
            sourceName = sourceName,
            trackDurationMs = trackDurationMs,
            trackPermalink = trackPermalink,
        )
    }

    /** One comment, or null when it cannot honestly be shown on a timeline. */
    fun toComment(
        dto: AudiusComment,
        user: AudiusUser?,
        trackId: String,
        sourceName: String,
        trackDurationMs: Long?,
        trackPermalink: String? = null,
    ): TimestampedComment? {
        val id = dto.id?.trim().takeUnless { it.isNullOrEmpty() } ?: return null

        // Withdrawn by the author or by a moderator. Audius keeps the row and flags it; we drop it.
        if (dto.isTombstone == true || dto.isMuted == true) return null

        // Comments can exist on playlists and profiles too. Only a track comment has a playback position
        // that means something against the thing being played.
        val entityType = dto.entityType
        if (entityType != null && entityType != "Track") return null

        val text = dto.message?.trim().takeUnless { it.isNullOrEmpty() } ?: return null

        // The defining field. Null is a genuine "commented without the player running", not a missing
        // value to be defaulted — see the class doc for why 0 would be a lie.
        val timestampS = dto.trackTimestampS ?: return null
        if (timestampS < 0L) return null
        val timestampMs = timestampS * 1000L
        if (trackDurationMs != null && trackDurationMs > 0L && timestampMs > trackDurationMs) return null

        // An unattributable reaction is dropped rather than given a placeholder name: inventing an
        // author for somebody's real words is worse than showing one fewer comment.
        val handle = user?.handle?.trim()?.takeUnless { it.isEmpty() }
        val author = user?.name?.trim()?.takeUnless { it.isEmpty() } ?: handle ?: return null

        return TimestampedComment(
            id = id,
            trackId = trackId,
            authorName = author,
            text = text,
            timestampMs = timestampMs,
            avatarUrl = user?.profilePicture?.avatarUrl?.takeIf { it.isNotBlank() },
            authorUrl = handle?.let { "$AUDIUS_WEB/$it" },
            permalink = trackPermalink?.takeIf { it.isNotBlank() }?.let { "$AUDIUS_WEB$it" },
            createdAtEpochMs = parseCreatedAt(dto.createdAt),
            // Audius exposes both, unlike SoundCloud's official API — so these are real counts here
            // rather than the nulls the SoundCloud parser has to leave.
            likeCount = dto.reactCount,
            replyCount = dto.replyCount,
            sourceName = sourceName,
        )
    }

    /**
     * Audius writes ISO-8601 UTC with a *variable* fractional part — `2026-08-30T21:48:00Z` and
     * `2026-09-01T21:48:54.20994Z` both appear in one response. [Instant.parse] accepts any digit
     * count from zero to nine, so it handles both; the fallback only exists for a node that writes a
     * space instead of `T`.
     *
     * Null on anything unparseable. A missing "posted 3 days ago" is a cosmetic loss; throwing over it
     * would take the whole comment list with it.
     */
    fun parseCreatedAt(raw: String?): Long? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return runCatching { Instant.parse(text).toEpochMilli() }.getOrNull()
            ?: runCatching { Instant.parse(text.replace(' ', 'T')).toEpochMilli() }.getOrNull()
    }

    private const val AUDIUS_WEB = "https://audius.co"
}
