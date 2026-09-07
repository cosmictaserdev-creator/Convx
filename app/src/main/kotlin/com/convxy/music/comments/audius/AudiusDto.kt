/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.audius

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire shapes for Audius's public discovery API (`api.audius.co/v1/...`).
 *
 * Every field is nullable and every collection defaults to empty, because the API is versioned by a
 * community-run set of discovery nodes rather than one server: different nodes can be on different
 * builds, and a field one node omits must not turn a good comment payload into a parse failure.
 * `ignoreUnknownKeys` is set on the decoder — Audius adds fields often, and none of them are ours to
 * model.
 *
 * These are transport types only. Nothing outside this package sees one; [AudiusCommentParser] turns
 * them into [com.convxy.music.comments.TimestampedComment] and drops whatever is unusable, so a
 * partially-populated response degrades to fewer comments rather than to a broken UI.
 */

/** `GET https://api.api.audius.co` — the discovery document listing usable hosts. */
@Serializable
data class AudiusHosts(
    @SerialName("data") val data: List<String> = emptyList(),
)

/** A list envelope. Audius wraps every collection the same way. */
@Serializable
data class AudiusList<T>(
    @SerialName("data") val data: List<T> = emptyList(),
    @SerialName("related") val related: AudiusRelated? = null,
    @SerialName("info") val info: AudiusInfo? = null,
)

/** Pagination hint, present on some list endpoints. Null means there is no further page. */
@Serializable
data class AudiusInfo(
    @SerialName("id") val id: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("total_count") val totalCount: Int? = null,
)

/**
 * Entities referenced by id in the primary `data` array, inlined so one request is enough.
 *
 * This is what makes the comments endpoint self-sufficient: each comment carries only a `user_id`,
 * but the same response's `related.users` carries those users' names and avatars. Without it,
 * resolving 100 commenters would cost 100 more requests.
 */
@Serializable
data class AudiusRelated(
    @SerialName("tracks") val tracks: List<AudiusTrack> = emptyList(),
    @SerialName("users") val users: List<AudiusUser> = emptyList(),
)

/**
 * One comment on a track.
 *
 * [trackTimestampS] is the whole reason Audius is a comment source at all: it is the commenter's
 * playback position in **seconds** when they wrote the comment, captured by the Audius client, not
 * parsed out of their text. `null` means they commented without the player running — a real comment,
 * but not a *timed* one, and therefore not something this feature can pin to a moment. Those are
 * dropped rather than defaulted to 0: a comment shown at 0:00 that was actually about the whole track
 * is a lie about where the reaction belongs.
 */
@Serializable
data class AudiusComment(
    @SerialName("id") val id: String? = null,
    @SerialName("entity_type") val entityType: String? = null,
    @SerialName("entity_id") val entityId: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("message") val message: String? = null,
    /** Playback position in seconds. Seconds — note that SoundCloud's equivalent is milliseconds. */
    @SerialName("track_timestamp_s") val trackTimestampS: Long? = null,
    @SerialName("mentions") val mentions: List<AudiusMention>? = null,
    @SerialName("is_muted") val isMuted: Boolean? = null,
    @SerialName("is_edited") val isEdited: Boolean? = null,
    @SerialName("is_tombstone") val isTombstone: Boolean? = null,
    @SerialName("react_count") val reactCount: Int? = null,
    @SerialName("reply_count") val replyCount: Int? = null,
    @SerialName("parent_comment_id") val parentCommentId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

/** An `@handle` inside a comment body. Present so the text can be shown as written. */
@Serializable
data class AudiusMention(
    @SerialName("user_id") val userId: String? = null,
)

/**
 * A track as the search and single-track endpoints return it.
 *
 * [duration] is **seconds** here (SoundCloud's is milliseconds) — the single easiest unit bug in this
 * feature, so it is converted once, in [AudiusCommentParser.toCandidate], and never again.
 *
 * [isrc] is carried because the payload has it, but nothing matches on it yet: Convxy's
 * [com.convxy.music.models.MediaMetadata] has no ISRC to compare against, so a code on one side only
 * proves nothing. Should Convxy ever carry one, an exact ISRC match would beat any amount of string
 * similarity and [AudiusTrackMatcher] is where that branch would go.
 */
@Serializable
data class AudiusTrack(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("slug") val slug: String? = null,
    /** Length in seconds. */
    @SerialName("duration") val duration: Long? = null,
    @SerialName("permalink") val permalink: String? = null,
    @SerialName("isrc") val isrc: String? = null,
    @SerialName("comment_count") val commentCount: Int? = null,
    @SerialName("comments_disabled") val commentsDisabled: Boolean? = null,
    @SerialName("is_available") val isAvailable: Boolean? = null,
    @SerialName("user_id") val userId: String? = null,
    /** The uploading artist, inlined on track payloads. */
    @SerialName("user") val user: AudiusUser? = null,
)

/** A user, whether inlined on a track or resolved through a comment response's `related.users`. */
@Serializable
data class AudiusUser(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("handle") val handle: String? = null,
    @SerialName("is_verified") val isVerified: Boolean? = null,
    @SerialName("profile_picture") val profilePicture: AudiusArtwork? = null,
)

/**
 * Sized image URLs. The keys contain an `x`, which is not a legal Kotlin identifier start, so each is
 * mapped explicitly by [SerialName].
 */
@Serializable
data class AudiusArtwork(
    @SerialName("150x150") val x150: String? = null,
    @SerialName("480x480") val x480: String? = null,
    @SerialName("1000x1000") val x1000: String? = null,
) {
    /** Smallest usable size. Avatars in a comment row are ~32dp, so 150px is already 4-5x that. */
    val avatarUrl: String?
        get() = x150 ?: x480 ?: x1000
}
