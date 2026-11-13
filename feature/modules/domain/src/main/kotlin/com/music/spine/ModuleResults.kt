package com.music.spine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuleSearchResult(
    @SerialName("id") val id: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("artist") val artist: String = "",
    @SerialName("artistId") val artistId: String? = null,
    @SerialName("album") val album: String = "",
    @SerialName("albumId") val albumId: String? = null,
    @SerialName("albumCover") val albumCover: String? = null,
    @SerialName("duration") val duration: Int = 0,
    @SerialName("trackNumber") val trackNumber: Int = 0,
    @SerialName("audioQuality") val audioQuality: String = "",
    @SerialName("format") val format: String = "",
    @SerialName("availableQualities") val availableQualities: List<String> = emptyList(),
    @SerialName("streamUrl") val streamUrl: String? = null,
)

@Serializable
data class ModuleSearchResponse(
    @SerialName("tracks") val tracks: List<ModuleSearchResult> = emptyList(),
    @SerialName("total") val total: Int = 0,
)

@Serializable
data class ModuleStreamResponse(
    @SerialName("streamUrl") val streamUrl: String? = null,
    @SerialName("track") val track: ModuleStreamTrack? = null,
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("streamType") val streamType: String? = null,
    @SerialName("expiresAt") val expiresAt: Long? = null,
)

@Serializable
data class ModuleStreamTrack(
    @SerialName("id") val id: String = "",
    @SerialName("audioQuality") val audioQuality: String = "",
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("format") val format: String? = null,
    @SerialName("codec") val codec: String? = null,
    @SerialName("bitDepth") val bitDepth: Int? = null,
    @SerialName("sampleRate") val sampleRate: Double? = null,
    @SerialName("bitrate") val bitrate: Int? = null,
    @SerialName("audioModes") val audioModes: List<String>? = null,
)
