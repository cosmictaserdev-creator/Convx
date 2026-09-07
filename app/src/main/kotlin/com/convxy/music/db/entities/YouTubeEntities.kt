/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.db.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local metadata for a watched regular-YouTube video. Metadata-only (title,
 * channel, thumbnail); the stream itself is never stored — this powers the
 * YouTube watch history and "Continue watching" sections.
 */
@Immutable
@Entity(
    tableName = "youtube_watch_history",
    indices = [
        Index("lastWatchedAt"),
    ],
)
data class YouTubeWatchHistoryEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelId: String? = null,
    val channelName: String? = null,
    val thumbnailUrl: String? = null,
    /** Reported duration in seconds; -1 when unknown (live streams etc.). */
    val durationSeconds: Int = -1,
    /** Last playback position in seconds. */
    val positionSeconds: Int = 0,
    val lastWatchedAt: Long = System.currentTimeMillis(),
    /** True once the user reached the end of the video. */
    val completed: Boolean = false,
) {
    /** Meaningful, resumable progress — drives the Continue Watching section. */
    val isResumable: Boolean
        get() = !completed && positionSeconds > 5 &&
            (durationSeconds <= 0 || positionSeconds < durationSeconds - 10)

    /** Fraction of the video watched, for progress bars on thumbnails. */
    val progressFraction: Float
        get() = if (durationSeconds > 0) {
            (positionSeconds.toFloat() / durationSeconds).coerceIn(0f, 1f)
        } else {
            0f
        }
}

/**
 * A bookmarked ("saved") regular-YouTube video. Metadata only — available
 * offline, the video itself still streams.
 */
@Immutable
@Entity(
    tableName = "youtube_saved_video",
    indices = [
        Index("savedAt"),
    ],
)
data class YouTubeSavedVideoEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelId: String? = null,
    val channelName: String? = null,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int = -1,
    val savedAt: Long = System.currentTimeMillis(),
)

/**
 * Recent YouTube search queries, separate from the music search history so the
 * two experiences never bleed into each other's suggestions.
 */
@Immutable
@Entity(
    tableName = "youtube_search_history",
    indices = [
        Index(value = ["query"], unique = true),
    ],
)
data class YouTubeSearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val searchedAt: Long = System.currentTimeMillis(),
)
