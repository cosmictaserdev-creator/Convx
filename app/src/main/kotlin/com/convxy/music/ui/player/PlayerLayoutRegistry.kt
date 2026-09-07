/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player

import androidx.annotation.StringRes
import com.convxy.music.R

/** A reorderable/hideable section of the full-screen Now Playing layout. */
enum class PlayerSlot(@StringRes val labelRes: Int, val hideable: Boolean) {
    ALBUM_ART(R.string.player_slot_album_art, hideable = true),
    TRACK_INFO(R.string.player_slot_track_info, hideable = true),
    SEEK_BAR(R.string.player_slot_seek_bar, hideable = true),
    /** No way to play/pause without this one — reorder-only, never hidden. */
    CONTROLS(R.string.player_slot_controls, hideable = false),
    ACTION_ROW(R.string.player_slot_action_row, hideable = true),
}

/**
 * Central registry for the player layout builder.
 * Handles serialization/deserialization of the custom slot order and which
 * slots are hidden, the same pattern as LyricsProviderRegistry's provider order.
 */
object PlayerLayoutRegistry {
    fun getDefaultOrder(): List<PlayerSlot> = listOf(
        PlayerSlot.ALBUM_ART,
        PlayerSlot.TRACK_INFO,
        PlayerSlot.SEEK_BAR,
        PlayerSlot.CONTROLS,
        PlayerSlot.ACTION_ROW,
    )

    fun deserializeOrder(orderString: String): List<PlayerSlot> {
        if (orderString.isBlank()) return getDefaultOrder()
        val parsed = orderString.split(",").mapNotNull { name ->
            runCatching { PlayerSlot.valueOf(name.trim()) }.getOrNull()
        }
        // Any slot missing from a stale/corrupt string still needs to render
        // somewhere — append it rather than silently dropping it.
        val missing = PlayerSlot.entries.filter { it !in parsed }
        return parsed + missing
    }

    fun serializeOrder(slots: List<PlayerSlot>): String = slots.joinToString(",") { it.name }

    fun deserializeHiddenSlots(hiddenString: String): Set<PlayerSlot> {
        if (hiddenString.isBlank()) return emptySet()
        return hiddenString.split(",")
            .mapNotNull { name -> runCatching { PlayerSlot.valueOf(name.trim()) }.getOrNull() }
            .filter { it.hideable }
            .toSet()
    }

    fun serializeHiddenSlots(hidden: Set<PlayerSlot>): String =
        hidden.filter { it.hideable }.joinToString(",") { it.name }
}
