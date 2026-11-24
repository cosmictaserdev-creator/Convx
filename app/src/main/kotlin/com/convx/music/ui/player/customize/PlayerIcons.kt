/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player.customize

import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.convx.music.R
import com.convx.music.constants.PlayerIconsKey
import androidx.datastore.preferences.core.Preferences
import com.convx.music.constants.V2PlayerIconsKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.rememberPreference
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
    V2_PLAY(R.drawable.flamingo_play, R.string.v2_player_icon_play, isV2 = true),
    V2_PAUSE(R.drawable.flamingo_pause, R.string.v2_player_icon_pause, isV2 = true),
    V2_PREVIOUS(R.drawable.flamingo_rewind, R.string.v2_player_icon_previous, isV2 = true),
    V2_NEXT(R.drawable.flamingo_fforward, R.string.v2_player_icon_next, isV2 = true),
    V2_LIKE(R.drawable.flamingo_favorite, R.string.v2_player_icon_like, isV2 = true),
    V2_LIKED(R.drawable.flamingo_favorited, R.string.v2_player_icon_liked, isV2 = true),
    V2_MORE(R.drawable.flamingo_more, R.string.v2_player_icon_more, isV2 = true),
    V2_LYRICS(R.drawable.flamingo_lyrics, R.string.v2_player_icon_lyrics, isV2 = true),
    V2_QUEUE(R.drawable.flamingo_queue, R.string.v2_player_icon_queue, isV2 = true),
    V2_VOLUME_DOWN(R.drawable.flamingo_volume, R.string.v2_player_icon_volume, isV2 = true),
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
                    val file = entry.optString("file").takeIf(DiySticker.Companion::isSafeFileName) ?: return@forEach
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
    val resolved = if (slot != null) rememberPlayerIcon(slot).takeIf { it.isCustom } else null
    val painter = resolved?.painter ?: painterResource(fallback)
    val isLike = slot == PlayerIconSlot.LIKE || slot == PlayerIconSlot.LIKED
    // The heart eases between its outline and filled looks, and pops when it becomes liked.
    val shownTint = if (isLike) {
        androidx.compose.animation.animateColorAsState(tint, tween(220), label = "heartTint").value
    } else tint
    val filter = when {
        resolved == null -> ColorFilter.tint(shownTint)
        else -> resolved.colorFilterFor(shownTint)
    }
    val pop = remember { androidx.compose.animation.core.Animatable(1f) }
    var settled by remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(slot) {
        if (settled && slot == PlayerIconSlot.LIKED) {
            pop.animateTo(1.4f, tween(90))
            pop.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 380f))
        }
        settled = true
    }
    androidx.compose.animation.Crossfade(
        targetState = painter,
        animationSpec = tween(if (isLike) 180 else 0),
        label = "glyph",
    ) { shown ->
        Image(
            painter = shown,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            colorFilter = filter,
            modifier = if (isLike) modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value } else modifier,
        )
    }
}

/**
 * [PlayerGlyph] variant rendering an SF Symbol ([ImageVector]) as the stock glyph instead of a
 * [androidx.annotation.DrawableRes]. Custom-image overrides for [slot] still win; only the default
 * glyph source differs.
 */
@Composable
fun PlayerGlyph(
    slot: PlayerIconSlot?,
    fallbackVector: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val resolved = if (slot != null) rememberPlayerIcon(slot).takeIf { it.isCustom } else null
    val painter = resolved?.painter ?: rememberVectorPainter(fallbackVector)
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
 * [PlayerGlyph] drawing the stock glyph as a bold hierarchical SF Symbol with the shared chrome.
 * A user's custom image for [slot] still wins.
 */
@Composable
fun PlayerGlyph(
    slot: PlayerIconSlot?,
    fallbackSymbol: com.composables.sfsymbols.SfSymbol,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: androidx.compose.ui.unit.Dp = 22.dp,
    bounce: Boolean = false,
) {
    val resolved = if (slot != null) rememberPlayerIcon(slot).takeIf { it.isCustom } else null
    if (resolved != null) {
        Image(
            painter = resolved.painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            colorFilter = resolved.colorFilterFor(tint),
            modifier = modifier,
        )
    } else {
        // The caller's modifier sizes the slot; the symbol sits centred inside it at [size].
        androidx.compose.foundation.layout.Box(modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
            com.convx.music.ui.component.SymbolIcon(
                symbol = fallbackSymbol,
                contentDescription = contentDescription,
                size = size,
                tint = tint,
                bounceOnTouch = bounce,
                // Black is the DstOut cut-out of an active tab disc: it must stay a plain mask.
                chrome = if (tint == androidx.compose.ui.graphics.Color.Black) 0f else com.convx.music.ui.utils.MenuIconChrome,
            )
        }
    }
}

/**
 * [PlayerGlyph] for the play/pause button, popping between the two states instead of cutting.
 *
 * Flamingo's transport: the outgoing glyph shrinks to a third of its size as it fades, and
 * the incoming one grows out of the same point. Swapping the drawable outright — which both
 * players did — reads as a different button appearing rather than the same button changing
 * its mind, and at this size that is the most-looked-at control on the screen.
 *
 * Still a [PlayerGlyph] underneath, so a user's custom play/pause icons animate too.
 */
@Composable
fun AnimatedPlayerGlyph(
    isPlaying: Boolean,
    playSlot: PlayerIconSlot?,
    pauseSlot: PlayerIconSlot?,
    @DrawableRes playFallback: Int,
    @DrawableRes pauseFallback: Int,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    playDescription: String? = null,
    pauseDescription: String? = null,
    // Flamingo sizes play and pause differently (one dp apart) as an optical correction —
    // the play triangle reads smaller than the pause bars at identical bounding-box size.
    // Null keeps the old fillMaxSize-of-modifier behaviour for callers that don't care.
    playSize: androidx.compose.ui.unit.Dp? = null,
    pauseSize: androidx.compose.ui.unit.Dp? = null,
) {
    AnimatedContent(
        targetState = isPlaying,
        transitionSpec = {
            // Apple Music (designRefrence/GVRecorder_1788117512105~3.mp4): the outgoing glyph is
            // already shrunk under the finger and just goes; the incoming one springs up from
            // ~0.2x with a slight settle while the press disc fades behind it.
            (scaleIn(spring(dampingRatio = 0.75f, stiffness = 500f), initialScale = 0.2f) + fadeIn(tween(120)))
                .togetherWith(
                    scaleOut(tween(100, easing = FastOutSlowInEasing), targetScale = 0.2f) + fadeOut(tween(90)),
                )
                .using(SizeTransform(clip = false))
        },
        contentKey = { it },
        label = "player_play_pause",
        modifier = modifier,
    ) { playing ->
        val explicitSize = if (playing) pauseSize else playSize
        PlayerGlyph(
            slot = if (playing) pauseSlot else playSlot,
            fallback = if (playing) pauseFallback else playFallback,
            tint = tint,
            contentDescription = if (playing) pauseDescription else playDescription,
            modifier = if (explicitSize != null) Modifier.size(explicitSize) else Modifier.fillMaxSize(),
        )
    }
}

/**
 * [AnimatedPlayerGlyph] vector-fallback variant — see the [ImageVector] overload of [PlayerGlyph].
 */
@Composable
fun AnimatedPlayerGlyph(
    isPlaying: Boolean,
    playSlot: PlayerIconSlot?,
    pauseSlot: PlayerIconSlot?,
    playFallbackVector: ImageVector,
    pauseFallbackVector: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    playDescription: String? = null,
    pauseDescription: String? = null,
    playSize: androidx.compose.ui.unit.Dp? = null,
    pauseSize: androidx.compose.ui.unit.Dp? = null,
) {
    AnimatedContent(
        targetState = isPlaying,
        transitionSpec = {
            // Apple Music (designRefrence/GVRecorder_1788117512105~3.mp4): the outgoing glyph is
            // already shrunk under the finger and just goes; the incoming one springs up from
            // ~0.2x with a slight settle while the press disc fades behind it.
            (scaleIn(spring(dampingRatio = 0.75f, stiffness = 500f), initialScale = 0.2f) + fadeIn(tween(120)))
                .togetherWith(
                    scaleOut(tween(100, easing = FastOutSlowInEasing), targetScale = 0.2f) + fadeOut(tween(90)),
                )
                .using(SizeTransform(clip = false))
        },
        contentKey = { it },
        label = "player_play_pause",
        modifier = modifier,
    ) { playing ->
        val explicitSize = if (playing) pauseSize else playSize
        PlayerGlyph(
            slot = if (playing) pauseSlot else playSlot,
            fallbackVector = if (playing) pauseFallbackVector else playFallbackVector,
            tint = tint,
            contentDescription = if (playing) pauseDescription else playDescription,
            modifier = if (explicitSize != null) Modifier.size(explicitSize) else Modifier.fillMaxSize(),
        )
    }
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
