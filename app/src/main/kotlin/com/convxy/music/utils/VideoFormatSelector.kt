/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.Context
import com.convxy.music.constants.VideoQualityCapKey
import com.music.innertube.models.response.PlayerResponse

/**
 * Picks the best progressive (muxed audio+video) format for full-video
 * playback.
 *
 * Muxed deliberately: the whole playback pipeline (cache, resolving data
 * source, single ExoPlayer track selector) works on one URL per song.
 * Adaptive video-only streams would need a second, synchronized audio track
 * — a much larger change for a quality bump muxed already covers.
 *
 * Codec is ranked BEFORE resolution, matching what the battle-tested open
 * source clients (Flow's VideoCodecUtils.playbackCodecRank, NewPipe's
 * ListHelper) converge on: H.264 first — every device with hardware decode
 * handles it, so a software AV1/HEVC fallback can never take the default
 * slot and crash a vendor decoder. VP9 second (near-universal hw support),
 * then everything else. Within a codec: resolution, then fps, then bitrate.
 * Anything above 1080p is skipped (progressive formats never exceed it in
 * practice; the guard keeps a surprise 4K entry from destroying mobile data).
 */
fun selectMuxedVideoFormat(
    formats: List<PlayerResponse.StreamingData.Format>?,
    /** Highest allowed height in px; the user's video-quality cap. */
    maxHeightCap: Int = 1080,
): PlayerResponse.StreamingData.Format? =
    formats.orEmpty()
        .filter { format -> (format.height ?: 0) in 1..maxHeightCap.coerceIn(144, 1080) }
        .maxByOrNull { format ->
            muxedVideoCodecRank(format.mimeType) * 10_000_000 +
                (format.height ?: 0) * 1_000 +
                (format.fps ?: 0) * 10 +
                (format.averageBitrate ?: format.bitrate) / 100_000
        }

/**
 * 0 = best (H.264 / AVC in MP4), 1 = VP9, 2 = anything else. Lower wins.
 * Muxed streams are effectively MP4/H.264 (itag 18/22/37) or WEBM/VP9; some
 * clients omit the codecs parameter entirely — MP4 is treated as H.264,
 * mirroring Flow's codecKeyFromMimeType fallback.
 */
private fun muxedVideoCodecRank(mimeType: String?): Int {
    val mime = mimeType?.lowercase().orEmpty()
    return when {
        "webm" in mime -> 1 // VP9/VP8 container
        "mp4" in mime || "avc" in mime || "h264" in mime || "h.264" in mime -> 0
        else -> 2
    }
}

/**
 * The user's muxed video quality cap (max height in px; 1080 = Auto).
 * Read from DataStore on the IO dispatcher only.
 */
suspend fun videoQualityCap(context: Context): Int =
    context.dataStore.get(VideoQualityCapKey, 1080)
