package com.convx.music.playback

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DIY_MAX_STICKERS = 10
const val DIY_MAX_ASSET_BYTES = 8L * 1024 * 1024

@Serializable
enum class PlayerStyle { WIDE, STACKED }

@Serializable
enum class PlayerSection { ALBUM_ART, TRACK_INFO, SEEK_BAR, CONTROLS, ACTION_ROW }

@Serializable
enum class DiyStickerKind { IMAGE, EMOJI }

@Serializable
data class DiyTransform(val x: Float = .5f, val y: Float = .5f, val scale: Float = .25f, val rotation: Float = 0f) {
    fun normalized() = copy(x = x.bounded(-.5f, 1.5f, .5f), y = y.bounded(-.5f, 1.5f, .5f),
        scale = scale.bounded(.03f, 2f, .25f), rotation = rotation.bounded(-360f, 360f, 0f))
}

@Serializable
data class DiySticker(
    val id: String,
    val kind: DiyStickerKind,
    val source: String,
    val z: Int = 1,
    val opacity: Float = 1f,
    @SerialName("flipH") val flipHorizontal: Boolean = false,
    @SerialName("corner") val cornerRadius: Float = 0f,
    val shadow: Boolean = false,
    val portrait: DiyTransform = DiyTransform(),
    val landscape: DiyTransform = DiyTransform(),
) {
    fun transform(wide: Boolean) = if (wide) landscape else portrait
    fun withTransform(wide: Boolean, transform: DiyTransform) =
        if (wide) copy(landscape = transform.normalized()) else copy(portrait = transform.normalized())
    fun normalized() = copy(z = z.coerceIn(-20, 20), opacity = opacity.bounded(.05f, 1f, 1f),
        cornerRadius = cornerRadius.bounded(0f, .5f, 0f), portrait = portrait.normalized(), landscape = landscape.normalized())
}

@Serializable
data class DiyLayout(val version: Int = 1, val stickers: List<DiySticker> = emptyList()) {
    fun normalized() = copy(version = 1, stickers = stickers.asSequence()
        .filter { it.id.isNotBlank() && it.id.length <= 128 && it.source.isNotBlank() }
        .filter { if (it.kind == DiyStickerKind.IMAGE) isSafePlayerAsset(it.source) else it.source.length <= 64 }
        .distinctBy { it.id }.take(DIY_MAX_STICKERS).map { it.normalized() }.toList())
}

@Serializable
data class PlayerIconOverride(@SerialName("file") val fileName: String, val tint: Boolean = false)

@Serializable
data class PlayerCustomization(
    val style: PlayerStyle = PlayerStyle.STACKED,
    val order: List<PlayerSection> = PlayerSection.entries.toList(),
    val hidden: Set<PlayerSection> = emptySet(),
    val diy: DiyLayout = DiyLayout(),
    val icons: Map<String, PlayerIconOverride> = emptyMap(),
) {
    fun normalized() = copy(order = order.distinct() + PlayerSection.entries.filter { it !in order },
        hidden = hidden - PlayerSection.CONTROLS, diy = diy.normalized(),
        icons = icons.filter { (key, value) -> key in PLAYER_ICON_SLOTS && isSafePlayerAsset(value.fileName) })
}

val PLAYER_ICON_SLOTS = setOf("PLAY", "PAUSE", "PREVIOUS", "NEXT", "SHUFFLE", "REPEAT", "QUEUE", "VOLUME", "MUTE")

fun isSafePlayerAsset(name: String) = name.isNotBlank() && name.length <= 128 &&
    name.none { it == '/' || it == '\\' || it == ':' || it.code < 32 } && !name.contains("..") && name != "."

private fun Float.bounded(min: Float, max: Float, fallback: Float) = takeIf(Float::isFinite)?.coerceIn(min, max) ?: fallback
