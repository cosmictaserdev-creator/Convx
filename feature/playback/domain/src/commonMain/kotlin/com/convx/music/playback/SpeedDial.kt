package com.convx.music.playback

import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** A song, album, artist or playlist pinned to Home's Speed Dial, with enough of it to show it again. */
@Serializable
data class SpeedDialPin(
    val kind: String,
    val providerId: String,
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val artworkUrl: String? = null,
) {
    val key: String get() = "$kind:$providerId:$id"
}

@Serializable
data class SpeedDialSnapshot(val pins: List<SpeedDialPin> = emptyList()) {
    fun isPinned(key: String) = pins.any { it.key == key }

    /** Pins [pin] last, or removes it when it is already pinned. */
    fun toggled(pin: SpeedDialPin): SpeedDialSnapshot =
        if (isPinned(pin.key)) copy(pins = pins.filterNot { it.key == pin.key })
        else copy(pins = (pins + pin).takeLast(MAX_PINS))

    fun toJson(): String = JSON.encodeToString(this)

    companion object {
        const val MAX_PINS = 24
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        /** A snapshot from stored text; empty, corrupt or foreign text gives an empty snapshot. */
        fun parse(text: String?): SpeedDialSnapshot {
            if (text.isNullOrBlank()) return SpeedDialSnapshot()
            return try {
                JSON.decodeFromString(serializer(), text)
            } catch (_: SerializationException) {
                SpeedDialSnapshot()
            } catch (_: IllegalArgumentException) {
                SpeedDialSnapshot()
            }
        }
    }
}
