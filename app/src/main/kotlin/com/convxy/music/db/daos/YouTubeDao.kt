/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.convxy.music.db.entities.YouTubeSavedVideoEntity
import com.convxy.music.db.entities.YouTubeSearchHistoryEntity
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface YouTubeDao {
    // ── Watch history ────────────────────────────────────────────────────────

    @Query("SELECT * FROM youtube_watch_history ORDER BY lastWatchedAt DESC")
    fun watchHistory(): Flow<List<YouTubeWatchHistoryEntity>>

    @Query(
        "SELECT * FROM youtube_watch_history " +
            "WHERE positionSeconds > 5 AND completed = 0 " +
            "AND (durationSeconds <= 0 OR positionSeconds < durationSeconds - 10) " +
            "ORDER BY lastWatchedAt DESC LIMIT :limit"
    )
    fun continueWatching(limit: Int = 20): Flow<List<YouTubeWatchHistoryEntity>>

    @Query("SELECT * FROM youtube_watch_history ORDER BY lastWatchedAt DESC LIMIT :limit")
    suspend fun recentWatched(limit: Int = 30): List<YouTubeWatchHistoryEntity>

    @Query("SELECT * FROM youtube_watch_history WHERE videoId = :videoId")
    fun watchHistoryEntry(videoId: String): Flow<YouTubeWatchHistoryEntity?>

    @Query("SELECT * FROM youtube_watch_history WHERE videoId = :videoId")
    suspend fun getWatchHistoryEntry(videoId: String): YouTubeWatchHistoryEntity?

    @Upsert
    suspend fun upsertWatchHistory(entry: YouTubeWatchHistoryEntity)

    /** Saves a fresh position, preserving title/channel metadata already stored. */
    @Query(
        "UPDATE youtube_watch_history SET " +
            "positionSeconds = :positionSeconds, " +
            "durationSeconds = CASE WHEN :durationSeconds > 0 THEN :durationSeconds ELSE durationSeconds END, " +
            "completed = :completed, " +
            "lastWatchedAt = :lastWatchedAt " +
            "WHERE videoId = :videoId"
    )
    suspend fun updateWatchProgress(
        videoId: String,
        positionSeconds: Int,
        durationSeconds: Int,
        completed: Boolean,
        lastWatchedAt: Long,
    )

    @Query("DELETE FROM youtube_watch_history WHERE videoId = :videoId")
    suspend fun deleteWatchHistoryEntry(videoId: String)

    @Query("DELETE FROM youtube_watch_history")
    suspend fun clearWatchHistory()

    // ── Saved videos ─────────────────────────────────────────────────────────

    @Query("SELECT * FROM youtube_saved_video ORDER BY savedAt DESC")
    fun savedVideos(): Flow<List<YouTubeSavedVideoEntity>>

    @Query("SELECT EXISTS(SELECT * FROM youtube_saved_video WHERE videoId = :videoId)")
    fun isVideoSaved(videoId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVideo(video: YouTubeSavedVideoEntity)

    @Query("DELETE FROM youtube_saved_video WHERE videoId = :videoId")
    suspend fun unsaveVideo(videoId: String)

    // ── Search history ───────────────────────────────────────────────────────

    @Query("SELECT * FROM youtube_search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun searchHistory(limit: Int = 20): Flow<List<YouTubeSearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchQuery(entry: YouTubeSearchHistoryEntity)

    /** Moves an existing query to the top of the recency order (IGNORE would leave it in place). */
    @Query("UPDATE youtube_search_history SET searchedAt = :searchedAt WHERE query = :query")
    suspend fun touchSearchQuery(query: String, searchedAt: Long)

    @Query("DELETE FROM youtube_search_history WHERE id = :id")
    suspend fun deleteSearchQuery(id: Long)

    @Query("DELETE FROM youtube_search_history")
    suspend fun clearSearchHistory()
}
