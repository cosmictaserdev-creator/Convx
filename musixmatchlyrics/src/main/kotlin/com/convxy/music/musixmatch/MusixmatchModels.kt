/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.musixmatch

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MxmEnvelope<T>(
    val message: MxmMessage<T>,
)

@Serializable
data class MxmMessage<T>(
    val header: MxmHeader,
    val body: T? = null,
)

@Serializable
data class MxmHeader(
    @SerialName("status_code") val statusCode: Int,
)

@Serializable
data class MxmTokenBody(
    @SerialName("user_token") val userToken: String,
)

@Serializable
data class MxmTrackSearchBody(
    @SerialName("track_list") val trackList: List<MxmTrackWrapper> = emptyList(),
)

@Serializable
data class MxmTrackWrapper(
    val track: MxmTrack,
)

@Serializable
data class MxmTrack(
    @SerialName("track_id") val trackId: Long,
    @SerialName("track_name") val trackName: String,
    @SerialName("artist_name") val artistName: String = "",
    @SerialName("track_length") val trackLength: Int? = null,
    @SerialName("has_subtitles") val hasSubtitles: Int = 0,
)

@Serializable
data class MxmSubtitleBody(
    val subtitle: MxmSubtitle? = null,
)

@Serializable
data class MxmSubtitle(
    @SerialName("subtitle_body") val subtitleBody: String,
)

@Serializable
data class MxmLyricsBody(
    val lyrics: MxmLyrics? = null,
)

@Serializable
data class MxmLyrics(
    @SerialName("lyrics_body") val lyricsBody: String,
)

/** One synced line from a `track.subtitle.get` subtitle_body JSON payload. */
@Serializable
data class MxmSubtitleLine(
    val text: String,
    val time: MxmSubtitleTime,
)

@Serializable
data class MxmSubtitleTime(
    val total: Double,
)
