/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player.customize

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.convxy.music.R
import com.convxy.music.constants.PlayerIconsKey
import androidx.datastore.preferences.core.Preferences
import com.convxy.music.constants.V2PlayerIconsKey
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import com.convxy.music.utils.rememberPreference
import org.json.JSONObject
import java.io.File

/**
 * A player control whose glyph the user can replace with their own image.
 *
 * Only controls that are actually drawn somewhere get a slot — an entry here without a render
 * site would be a setting that silently does nothing.
 */
enum class PlayerIconSlot(
    @DrawableRes val fallback: Int,
    @StringRes val labelRes: Int,
    val isV2: Boolean = false,
) {
    PLAY(R.drawable.play_applemusic, R.string.player_icon_play),
    PAUSE(R.drawable.pause_applemusic, R.string.player_icon_pause),
    PREVIOUS(R.drawable.skip_previous_legacy, R.string.player_icon_previous),
    NEXT(R.drawable.fast_forward, R.string.player_icon_next),
    REPLAY(R.drawable.replay, R.string.player_icon_replay),
    LIKE(R.drawable.favorite_border, R.string.player_icon_like),
    LIKED(R.drawable.favorite, R.string.player_icon_liked),
    MORE(R.drawable.more_horiz, R.string.player_icon_more),
    SEEK_THUMB(R.drawable.play_applemusic, R.string.player_icon_seek_thumb),

    // V2 Apple Music Player slots
    V2_PLAY(R.drawable.play_applemusic, R.string.v2_player_icon_play, isV2 = true),
    V2_PAUSE(R.drawable.pause_applemusic, R.string.v2_player_icon_pause, isV2 = true),
    V2_PREVIOUS(R.drawable.apple_skip_previous, R.string.v2_player_icon_previous, isV2 = true),
    V2_NEXT(R.drawable.apple_skip_next, R.string.v2_player_icon_next, isV2 = true),
    V2_LIKE(R.drawable.favorite_border, R.string.v2_player_icon_like, isV2 = true),
    V2_LIKED(R.drawable.favorite, R.string.v2_player_icon_liked, isV2 = true),
    V2_MORE(R.drawable.more_horiz, R.string.v2_player_icon_more, isV2 = true),
    V2_LYRICS(R.drawable.apple_lyrics, R.string.v2_player_icon_lyrics, isV2 = true),
    V2_QUEUE(R.drawable.apple_queue, R.string.v2_player_icon_queue, isV2 = true),
    V2_VOLUME_DOWN(R.drawable.speaker_apple, R.string.v2_player_icon_volume, isV2 = true),
    ;

    /** Slider thumbs are decorative-only and hidden unless the user supplies an image. */
    val hiddenWhenUnset: Boolean get() = this == SEEK_THUMB
}

/**
 * One slot's override: the file backing it, and whether it should be recoloured to match the
 * theme. Tinting suits flat silhouettes and destroys photographs, so it is per-slot and off by
 * default — a user who picks a photo of a flower sees the flower, not an accent-coloured blob.
 */
data class PlayerIconOverride(val fileName: String, val tint: Boolean)

/** The full set of overrides, persisted as one JSON blob so a slot change is a single write. */
@JvmInline
value class PlayerIconSet(val overrides: Map<PlayerIconSlot, PlayerIconOverride>) {

    fun with(slot: PlayerIconSlot, override: PlayerIconOverride?): PlayerIconSet =
        PlayerIconSet(
            if (override == null) overrides - slot else overrides + (slot to override)
        )

    fun toJson(): String = JSONObject().apply {
        overrides.forEach { (slot, value) ->
            put(
                slot.name,
                JSONObject().put("file", value.fileName).put("tint", value.tint),
            )
        }
    }.toString()

    companion object {
        val EMPTY = PlayerIconSet(emptyMap())

        fun fromJson(json: String): PlayerIconSet = runCatching {
            val obj = JSONObject(json)
            val map = buildMap {
                PlayerIconSlot.entries.forEach { slot ->
                    val entry = obj.optJSONObject(slot.name) ?: return@forEach
                    val file = entry.optString("file").takeIf { it.isNotEmpty() } ?: return@forEach
                    put(slot, PlayerIconOverride(file, entry.optBoolean("tint", false)))
                }
            }
            PlayerIconSet(map)
        }.getOrDefault(EMPTY)
    }
}

object PlayerIconStore {

    /** Where imported glyphs live. One directory, one file per slot. */
    fun dir(context: Context): File =
        File(context.filesDir, "player_icons").apply { mkdirs() }

    fun v2Dir(context: Context): File =
        File(context.filesDir, "v2_player_icons").apply { mkdirs() }

    fun fileFor(context: Context, override: PlayerIconOverride, isV2: Boolean = false): File =
        File(if (isV2) v2Dir(context) else dir(context), override.fileName)

    /** [key] selects which control set to read — the classic player's or the V2 player's. */
    fun load(
        context: Context,
        key: Preferences.Key<String> = PlayerIconsKey,
    ): PlayerIconSet = PlayerIconSet.fromJson(context.dataStore.get(key, "{}"))

    /**
     * Drops files no slot references any more. Called after a slot is cleared or overwritten so
     * a user who tries ten different play buttons is not left with ten orphaned images.
     */
    fun pruneOrphans(context: Context, set: PlayerIconSet, isV2: Boolean = false) {
        // [isV2] is not optional in practice: the two players keep their glyphs in
        // separate directories, and pruning one directory against the other's set
        // deletes every icon the user picked for the other player.
        val live = set.overrides.values.mapTo(mutableSetOf()) { it.fileName }
        val target = if (isV2) v2Dir(context) else dir(context)
        target.listFiles()?.forEach { if (it.name !in live) it.delete() }
    }
}

/**
 * Resolves the painter for [slot]: the user's image when one is set and still on disk, otherwise
 * the built-in glyph.
 *
 * A missing file falls back silently rather than erroring — the override can outlive its file
 * (restore from backup, storage cleaner, failed preset import), and a player that refuses to draw
 * a play button in that situation is worse than one that quietly uses the stock glyph.
 */
@Composable
fun rememberPlayerIcon(slot: PlayerIconSlot): PlayerIconPainter {
    val context = LocalContext.current
    val prefKey = if (slot.isV2) V2PlayerIconsKey else PlayerIconsKey
    val (json) = rememberPreference(prefKey, defaultValue = "{}")
    val set = remember(json) { PlayerIconSet.fromJson(json) }
    val override = set.overrides[slot]

    val file = remember(override) {
        override?.let { PlayerIconStore.fileFor(context, it, slot.isV2) }?.takeIf { it.isFile }
    }

    if (file == null) {
        return PlayerIconPainter(
            painter = painterResource(slot.fallback),
            isCustom = false,
            tint = true,
        )
    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(file)
            // Re-picking an image writes to this same path (see PlayerIconStore), so the
            // default path-derived cache key can't tell a fresh pick from the deleted one —
            // key on the file's mtime too, or the stale bitmap keeps getting served.
            .memoryCacheKey("player_icon_${file.path}_${file.lastModified()}")
            .diskCacheKey("player_icon_${file.path}_${file.lastModified()}")
            .build(),
    )
    return PlayerIconPainter(painter = painter, isCustom = true, tint = override?.tint ?: false)
}

/**
 * Draws a player control's glyph, honouring any user override for [slot].
 *
 * This is the single call site the player uses, so a slot gains custom-image support everywhere
 * it appears at once — the mini player, the full player, and the mockup all route through here.
 *
 * @param slot null for controls with no override (a mute button shown only to Listen Together
 *   guests, say), which draws [fallback] as an ordinary tinted glyph.
 */
@Composable
fun PlayerGlyph(
    slot: PlayerIconSlot?,
    @DrawableRes fallback: Int,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val resolved = if (slot != null) rememberPlayerIcon(slot) else null
    val painter = resolved?.painter ?: painterResource(fallback)
    val filter = when {
        resolved == null -> ColorFilter.tint(tint)
        else -> resolved.colorFilterFor(tint)
    }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        colorFilter = filter,
        modifier = modifier,
    )
}

/**
 * @param tint true when the caller should apply its own content colour. Built-in glyphs always
 *   want it; custom images only when the user asked for it.
 */
data class PlayerIconPainter(
    val painter: Painter,
    val isCustom: Boolean,
    val tint: Boolean,
) {
    /** Null means "draw the image's own colours". */
    fun colorFilterFor(color: androidx.compose.ui.graphics.Color): ColorFilter? =
        if (tint) ColorFilter.tint(color) else null
}
