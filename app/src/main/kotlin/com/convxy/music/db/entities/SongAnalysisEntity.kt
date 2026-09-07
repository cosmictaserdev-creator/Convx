/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Cached DJ analysis for one track: beat grid, musical key, and a coarse energy
 * curve. Analyzing a track costs a decode pass, so this exists to make that
 * happen exactly once per track rather than once per session.
 *
 * Deliberately NOT `@Immutable` and not compared by value — [energyProfile] is a
 * `ByteArray`, so the generated `equals`/`hashCode` are identity-based. Nothing
 * compares these rows; they are looked up by id.
 */
@Entity(tableName = "song_analysis")
data class SongAnalysisEntity(
    /** Media id, matching `SongEntity.id` / the player's `mediaId`. */
    @PrimaryKey val id: String,
    val bpm: Float,
    /** Absolute track time of one real beat; the grid is this + n * beat. */
    val anchorBeatMs: Float,
    val bpmConfidence: Float,
    /** Pitch class 0 = C .. 11 = B, or -1 when key detection found nothing. */
    val keyIndex: Int = -1,
    val keyIsMinor: Boolean = false,
    val keyConfidence: Float = 0f,
    /**
     * RMS level per second of track, quantized to one unsigned byte each. ~200
     * bytes for a typical song, stored as a BLOB with no type converter needed.
     *
     * Coarse on purpose: it exists to answer "does this track end at full
     * energy or decay away" and "does it open on drums or on 30 s of pad",
     * which is what picks the transition style and the mix-out point. Eight
     * bits and one-second resolution are plenty for that, and keep the row
     * small enough to load for a whole queue.
     */
    val energyProfile: ByteArray = ByteArray(0),
    val analyzedAt: LocalDateTime = LocalDateTime.now(),
    /**
     * Bump [CURRENT_ANALYSIS_VERSION] whenever the analysis algorithm changes.
     * Rows from an older version are ignored and re-analyzed rather than
     * silently trusted — a beat grid from a worse detector is worse than none,
     * because a confident wrong grid produces an actively bad mix.
     */
    val analysisVersion: Int = CURRENT_ANALYSIS_VERSION,
) {
    companion object {
        const val CURRENT_ANALYSIS_VERSION = 1
    }
}
