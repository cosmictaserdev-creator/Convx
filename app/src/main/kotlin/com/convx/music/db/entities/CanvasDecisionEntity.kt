/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persisted per-track canvas decision: which source won, its loop urls, and the colour
 * identity sampled from the clip so a replay never recomputes palette or blur base.
 * The video pixels themselves stay in the player cache under the normal canvas key.
 */
@Entity(tableName = "canvas_decision", indices = [Index("updatedAt")])
data class CanvasDecisionEntity(
    @PrimaryKey val id: String,
    /** The winning [com.convx.music.constants.CanvasSource] name, or [NO_CANVAS] when the ladder missed. */
    val source: String,
    val primaryUrl: String? = null,
    val fallbackUrl: String? = null,
    /** Packed 0xAARRGGBB colours from [com.convx.music.ui.player.CanvasVideoLook], comma-separated. */
    val palette: String? = null,
    /** Small JPEG representative frame used as the blurred background's base bitmap. */
    val lookFrame: ByteArray? = null,
    val updatedAt: Long,
) {
    val isFound: Boolean get() = source != NO_CANVAS

    companion object {
        const val NO_CANVAS = "none"
    }
}
