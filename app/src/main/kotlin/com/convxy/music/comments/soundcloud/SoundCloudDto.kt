/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Wire shapes for the OFFICIAL SoundCloud API (https://api.soundcloud.com, spec at
// https://developers.soundcloud.com/docs/api/explorer/open-api).
//
// Every field is nullable with a default. That is not laziness: the API's own docs describe
// per-resource variation, its 4xx bodies reuse some of these names for error payloads, and a single
// unexpected null must cost one dropped comment rather than the whole response. Parsing therefore
// runs with ignoreUnknownKeys + explicitNulls=false and each DTO is converted defensively in
// SoundCloudCommentParser.

/** One entry of `GET /tracks/{track_urn}/comments`. */
@Serializable
data class ScComment(
    @SerialName("id") val id: Long? = null,
    @SerialName("kind") val kind: String? = null,
    @SerialName("body") val body: String? = null,
    /**
     * Milliseconds from the start of the track. SoundCloud's timed-comment field, and the reason
     * this provider is used at all. Absent/null on a comment posted without a timestamp — those are
     * ordinary track comments, not timestamped ones, and are dropped.
     */
    @SerialName("timestamp") val timestamp: Long? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("track_id") val trackId: Long? = null,
    @SerialName("user_id") val userId: Long? = null,
    @SerialName("uri") val uri: String? = null,
    @SerialName("user") val user: ScUser? = null,
)

@Serializable
data class ScUser(
    @SerialName("id") val id: Long? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("permalink") val permalink: String? = null,
    @SerialName("permalink_url") val permalinkUrl: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

/** One entry of `GET /tracks?q=…` (search). */
@Serializable
data class ScTrack(
    @SerialName("id") val id: Long? = null,
    @SerialName("title") val title: String? = null,
    /** Track length in milliseconds. */
    @SerialName("duration") val duration: Long? = null,
    @SerialName("permalink_url") val permalinkUrl: String? = null,
    @SerialName("comment_count") val commentCount: Int? = null,
    /** False when the uploader turned comments off — then there is nothing to fetch. */
    @SerialName("commentable") val commentable: Boolean? = null,
    @SerialName("user") val user: ScUser? = null,
)

/**
 * Envelope for a paginated collection response.
 *
 * With `linked_partitioning=true` the API returns `{"collection": […], "next_href": …}`; without it,
 * a bare JSON array. Both are handled in [SoundCloudCommentParser], which reads the raw text and
 * branches — so this type only ever models the envelope case.
 */
@Serializable
data class ScCollection<T>(
    @SerialName("collection") val collection: List<T> = emptyList(),
    @SerialName("next_href") val nextHref: String? = null,
)

/** `POST /oauth/token` response for the client_credentials grant. */
@Serializable
data class ScTokenResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("scope") val scope: String? = null,
    /** Present on an error response instead of a token. */
    @SerialName("error") val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
)
