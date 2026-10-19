package com.music.spine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.net.URI

/** Adapts Eclipse HTTP addons to the app's Spine module interface. */
object EclipseAddon {
    const val TYPE = "ECLIPSE"

    /** True when [body] is an Eclipse manifest rather than a Spine module index. */
    fun isManifest(json: Json, body: String): Boolean = runCatching {
        val obj = json.decodeFromString(JsonObject.serializer(), body)
        "id" in obj && obj.keys.none { it.startsWith("category:") }
    }.getOrDefault(false)

    /** Removes `/manifest.json` while preserving any token path segment. */
    fun baseUrl(manifestUrl: String): String =
        manifestUrl.substringBefore('?').removeSuffix("/").removeSuffix("/manifest.json").removeSuffix("/")

    /** HTTPS only, except loopback/emulator hosts for addon development (per the spec). */
    fun isAllowedUrl(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        return uri.scheme == "https" ||
            (uri.scheme == "http" && uri.host in setOf("localhost", "127.0.0.1", "10.0.2.2"))
    }

    fun toModule(json: Json, manifestUrl: String, body: String): SpineModule {
        val m = json.decodeFromString(Manifest.serializer(), body)
        require(m.id.isNotBlank()) { "That URL did not return an addon manifest" }
        require(m.resources.isEmpty() || m.resources.any { it.equals("search", true) }) { "This addon does not support track search" }
        return SpineModule(
            id = m.id,
            name = m.name.ifBlank { m.id },
            version = m.version,
            description = m.description,
            type = TYPE,
            download = baseUrl(manifestUrl),
            icon = m.icon,
            declaredTags = m.types,
        )
    }

    fun moduleManifest(json: Json, body: String): ModuleManifest {
        val manifest = json.decodeFromString(Manifest.serializer(), body)
        return ModuleManifest(settings = manifest.settings.filter { it.key.isNotBlank() }.associate { setting ->
            setting.key to ModuleSetting(setting.type, setting.label.ifBlank { setting.key }, setting.description,
                setting.options.map { ModuleSettingOption(it.label, it.value?.contentOrNull.orEmpty()) }, setting.default)
        })
    }

    /** Addon defaults and selected quality travel on both search and stream requests. */
    fun parameters(manifest: ModuleManifest, settings: Map<String, String>, quality: String = settings["quality"].orEmpty()): Map<String, String> {
        val result = (manifest.defaults + settings).filterKeys { it != "dolbyAtmos" }.toMutableMap()
        if (quality.isNotBlank()) {
            val options = manifest.settings["quality"]?.options.orEmpty().map { it.value }
            val hints = when (quality.uppercase()) {
                "LOSSLESS", "HI_RES_LOSSLESS", "HIRES" -> listOf("lossless", "flac", "hires", "high", "best")
                "LOW" -> listOf("low", "normal", "96", "128")
                else -> listOf("high", "320", "best", "normal")
            }
            result["quality"] = options.firstOrNull { it.equals(quality, true) }
                ?: hints.firstNotNullOfOrNull { hint -> options.firstOrNull { it.contains(hint, true) } }
                ?: options.firstOrNull() ?: quality
        }
        if (settings["dolbyAtmos"] == "true") result.putIfAbsent("atmos", "auto")
        return result
    }

    private fun withParameters(url: String, settings: Map<String, String>): String =
        if (settings.isEmpty()) url else url + (if ('?' in url) "&" else "?") + settings.entries.joinToString("&") {
            java.net.URLEncoder.encode(it.key, "UTF-8") + "=" + java.net.URLEncoder.encode(it.value, "UTF-8")
        }

    fun searchUrl(base: String, query: String, settings: Map<String, String> = emptyMap()): String =
        withParameters("$base/search?q=" + java.net.URLEncoder.encode(query, "UTF-8").replace("+", "%20"), settings)

    fun streamUrl(base: String, id: String, settings: Map<String, String> = emptyMap()): String =
        withParameters("$base/stream/" + java.net.URLEncoder.encode(id, "UTF-8").replace("+", "%20"), settings)

    fun parseSearch(json: Json, body: String, limit: Int): ModuleSearchResponse {
        val r = json.decodeFromString(Search.serializer(), body)
        val tracks = r.tracks.take(limit).map {
            ModuleSearchResult(
                id = it.id,
                title = it.title,
                artist = it.artist,
                album = it.album.orEmpty(),
                albumCover = it.artworkURL ?: it.albumArtworkURL,
                duration = it.duration?.toInt() ?: 0,
                format = it.format.orEmpty(),
                audioQuality = it.audioQuality,
                streamUrl = it.streamURL,
            )
        }
        return ModuleSearchResponse(tracks = tracks, total = tracks.size)
    }

    fun parseStream(json: Json, id: String, body: String): ModuleStreamResponse {
        val s = json.decodeFromString(Stream.serializer(), body)
        val encrypted = (s.encrypted as? JsonPrimitive)?.contentOrNull.orEmpty()
        require(encrypted.isBlank() || encrypted.lowercase() in setOf("false", "none", "0")) { "The addon returned an encrypted stream" }
        require(!s.url.isNullOrBlank()) { s.error ?: "The addon returned no playable audio" }
        return ModuleStreamResponse(
            streamUrl = s.url,
            mimeType = s.mimeType,
            track = ModuleStreamTrack(
                id = id,
                audioQuality = s.quality ?: s.streamQuality ?: s.audioQuality.orEmpty(),
                format = s.container ?: s.containerFormat ?: s.format,
                codec = s.codec ?: s.fileCodec,
                bitDepth = s.bitDepth?.toInt(),
                sampleRate = s.sampleRate,
                audioModes = s.audioModes + listOfNotNull(s.audioMode),
            ),
            streamType = listOfNotNull(s.manifest, s.mediaType, s.format).firstNotNullOfOrNull {
                when (it.lowercase()) { "dash", "mpd", "application/dash+xml" -> "dash"; "hls", "m3u8", "application/vnd.apple.mpegurl" -> "hls"; else -> null }
            },
            expiresAt = s.expiresAt,
        )
    }

    @Serializable
    internal data class Manifest(
        val id: String,
        val name: String = "",
        val version: String = "",
        val description: String = "",
        val icon: String? = null,
        val resources: List<String> = emptyList(),
        val types: List<String> = emptyList(),
        val settings: List<Setting> = emptyList(),
    )

    @Serializable internal data class Setting(val key: String = "", val type: String = "text", val label: String = "", val description: String = "", val default: JsonElement? = null, val options: List<Option> = emptyList())
    @Serializable internal data class Option(val label: String = "", val value: JsonPrimitive? = null)

    @Serializable
    internal data class Search(val tracks: List<Track> = emptyList())

    @Serializable
    internal data class Track(
        val id: String,
        val title: String = "",
        val artist: String = "",
        val album: String? = null,
        val duration: Double? = null,
        @SerialName("artworkURL") val artworkURL: String? = null,
        val isrc: String? = null,
        val format: String? = null,
        val albumArtworkURL: String? = null,
        val streamURL: String? = null,
        val audioQuality: String = "",
    )

    @Serializable
    internal data class Stream(
        val url: String? = null,
        val format: String? = null,
        val quality: String? = null,
        val streamQuality: String? = null,
        val audioQuality: String? = null,
        val expiresAt: Long? = null,
        val codec: String? = null,
        val fileCodec: String? = null,
        val container: String? = null,
        val containerFormat: String? = null,
        val manifest: String? = null,
        val mediaType: String? = null,
        val mimeType: String? = null,
        val encrypted: JsonElement? = null,
        val error: String? = null,
        val audioMode: String? = null,
        val audioModes: List<String> = emptyList(),
        val sampleRate: Double? = null,
        val bitDepth: Double? = null,
    )
}
