/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player.customize

import android.content.Context
import com.convxy.music.constants.DiyLayoutKey
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Hard ceiling on stickers per layout. Ten is the product limit, not a technical one. */
const val DIY_MAX_STICKERS = 10

/** Total bytes of sticker imagery a single layout may hold. */
const val DIY_MAX_ASSET_BYTES = 8L * 1024 * 1024

enum class DiyStickerKind { IMAGE, EMOJI }

/** Which way up the player is. Each orientation keeps its own placement for the same sticker. */
enum class DiyOrientation { PORTRAIT, LANDSCAPE }

/**
 * Where one sticker sits, in units of the player's own bounds rather than pixels — so a layout
 * made on a phone lands in the same *relative* spot on a tablet instead of drifting off-screen.
 *
 * @param x centre, 0..1 across the player's width
 * @param y centre, 0..1 down the player's height
 * @param scale fraction of the player's smaller edge the sticker's longest side spans
 * @param rotation degrees, clockwise
 */
data class DiyTransform(
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val scale: Float = 0.25f,
    val rotation: Float = 0f,
) {
    fun toJson(): JSONObject = JSONObject()
        .put("x", x.toDouble())
        .put("y", y.toDouble())
        .put("scale", scale.toDouble())
        .put("rotation", rotation.toDouble())

    companion object {
        fun fromJson(obj: JSONObject?): DiyTransform = if (obj == null) DiyTransform() else
            DiyTransform(
                x = obj.optDouble("x", 0.5).toFloat().coerceIn(-0.5f, 1.5f),
                y = obj.optDouble("y", 0.5).toFloat().coerceIn(-0.5f, 1.5f),
                scale = obj.optDouble("scale", 0.25).toFloat().coerceIn(MIN_SCALE, MAX_SCALE),
                rotation = obj.optDouble("rotation", 0.0).toFloat(),
            )

        const val MIN_SCALE = 0.03f
        const val MAX_SCALE = 2.0f
    }
}

/**
 * One placed item. Appearance ([opacity], [flipHorizontal], [cornerRadius], [shadow], [z]) is
 * shared between orientations; only the placement differs, because a user who recolours a
 * sticker in portrait means it in landscape too.
 *
 * @param source for [DiyStickerKind.IMAGE] the file name inside the sticker directory; for
 *   [DiyStickerKind.EMOJI] the emoji itself, kept as text so it scales without ever pixelating.
 * @param z draw order. Negative sits behind the album artwork, positive in front of it. Nothing
 *   ever draws behind the Canvas video or in front of the transport controls.
 */
data class DiySticker(
    val id: String,
    val kind: DiyStickerKind,
    val source: String,
    val z: Int = 1,
    val opacity: Float = 1f,
    val flipHorizontal: Boolean = false,
    val cornerRadius: Float = 0f,
    val shadow: Boolean = false,
    val portrait: DiyTransform = DiyTransform(),
    val landscape: DiyTransform = DiyTransform(),
) {
    fun transformFor(orientation: DiyOrientation): DiyTransform =
        if (orientation == DiyOrientation.PORTRAIT) portrait else landscape

    fun withTransform(orientation: DiyOrientation, transform: DiyTransform): DiySticker =
        if (orientation == DiyOrientation.PORTRAIT) copy(portrait = transform)
        else copy(landscape = transform)

    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("kind", kind.name)
        .put("source", source)
        .put("z", z)
        .put("opacity", opacity.toDouble())
        .put("flipH", flipHorizontal)
        .put("corner", cornerRadius.toDouble())
        .put("shadow", shadow)
        .put("portrait", portrait.toJson())
        .put("landscape", landscape.toJson())

    companion object {
        fun fromJson(obj: JSONObject): DiySticker? {
            val id = obj.optString("id").takeIf { it.isNotEmpty() } ?: return null
            val source = obj.optString("source").takeIf { it.isNotEmpty() } ?: return null
            val kind = runCatching { DiyStickerKind.valueOf(obj.optString("kind")) }
                .getOrNull() ?: return null
            // A file name out of an imported preset must not be able to point anywhere but the
            // sticker directory itself.
            if (kind == DiyStickerKind.IMAGE && !isSafeFileName(source)) return null
            return DiySticker(
                id = id,
                kind = kind,
                source = source,
                z = obj.optInt("z", 1).coerceIn(-MAX_Z, MAX_Z),
                opacity = obj.optDouble("opacity", 1.0).toFloat().coerceIn(0.05f, 1f),
                flipHorizontal = obj.optBoolean("flipH", false),
                cornerRadius = obj.optDouble("corner", 0.0).toFloat().coerceIn(0f, 0.5f),
                shadow = obj.optBoolean("shadow", false),
                portrait = DiyTransform.fromJson(obj.optJSONObject("portrait")),
                landscape = DiyTransform.fromJson(obj.optJSONObject("landscape")),
            )
        }

        const val MAX_Z = 20

        fun isSafeFileName(name: String): Boolean =
            name.isNotEmpty() &&
                !name.contains('/') &&
                !name.contains('\\') &&
                !name.contains("..") &&
                name.length <= 128
    }
}

/** The whole sticker arrangement for the player. */
data class DiyLayout(val stickers: List<DiySticker> = emptyList()) {

    val isEmpty: Boolean get() = stickers.isEmpty()

    fun toJson(): String = JSONObject()
        .put("version", 1)
        .put("stickers", JSONArray().apply { stickers.forEach { put(it.toJson()) } })
        .toString()

    companion object {
        val EMPTY = DiyLayout()

        fun fromJson(json: String): DiyLayout = runCatching {
            val arr = JSONObject(json).optJSONArray("stickers") ?: return EMPTY
            val out = ArrayList<DiySticker>(arr.length())
            for (i in 0 until arr.length()) {
                if (out.size >= DIY_MAX_STICKERS) break
                arr.optJSONObject(i)?.let(DiySticker::fromJson)?.let(out::add)
            }
            DiyLayout(out)
        }.getOrDefault(EMPTY)
    }
}

object DiyStore {

    fun stickerDir(context: Context): File =
        File(context.filesDir, "diy_stickers").apply { mkdirs() }

    fun stickerFile(context: Context, sticker: DiySticker): File? =
        if (sticker.kind == DiyStickerKind.IMAGE) File(stickerDir(context), sticker.source) else null

    fun load(context: Context): DiyLayout =
        DiyLayout.fromJson(context.dataStore.get(DiyLayoutKey, "{}"))

    /** Bytes currently held by sticker images, for the editor's budget readout. */
    fun assetBytes(context: Context): Long =
        stickerDir(context).listFiles()?.sumOf { it.length() } ?: 0L

    /** Deletes sticker files no longer referenced by [layout]. */
    fun pruneOrphans(context: Context, layout: DiyLayout) {
        val live = layout.stickers
            .filter { it.kind == DiyStickerKind.IMAGE }
            .mapTo(mutableSetOf()) { it.source }
        stickerDir(context).listFiles()?.forEach { if (it.name !in live) it.delete() }
    }
}
