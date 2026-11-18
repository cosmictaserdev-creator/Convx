package com.convx.music.utils

import com.music.innertube.models.Thumbnail
import com.music.innertube.models.Thumbnails
import com.music.innertube.models.response.PlayerResponse
import com.music.innertube.soundcloud.SoundCloudStream

internal const val SOUND_CLOUD_FORMAT_ITAG = 9995

internal fun SoundCloudStream.toPlaybackData(mediaId: String): YTPlayerUtils.PlaybackData {
    val baseMime = mimeType?.takeIf { it.isNotBlank() } ?: "audio/mpeg"
    val mime = if (baseMime.contains("codecs=")) baseMime else {
        val codec = when (baseMime.substringBefore(';').trim()) {
            "audio/ogg", "audio/opus", "audio/webm" -> "opus"
            "audio/mp4", "audio/aac" -> "mp4a.40.2"
            "audio/flac" -> "flac"
            else -> "mp3"
        }
        "$baseMime; codecs=\"$codec\""
    }
    val bitrate = ((bitrateKbps?.takeIf { it > 0 } ?: 128).toLong() * 1000)
        .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    return YTPlayerUtils.PlaybackData(
        audioConfig = null,
        videoDetails = PlayerResponse.VideoDetails(
            videoId = mediaId,
            title = title,
            author = uploader,
            channelId = "",
            lengthSeconds = durationSeconds.coerceAtLeast(0).toString(),
            musicVideoType = null,
            viewCount = null,
            thumbnail = Thumbnails(thumbnailUrl?.let { listOf(Thumbnail(it, null, null)) }.orEmpty()),
        ),
        playbackTracking = null,
        format = PlayerResponse.StreamingData.Format(
            itag = SOUND_CLOUD_FORMAT_ITAG,
            url = url,
            mimeType = mime,
            bitrate = bitrate,
            width = null,
            height = null,
            contentLength = null,
            quality = "AUDIO_QUALITY_MEDIUM",
            fps = null,
            qualityLabel = null,
            averageBitrate = bitrate,
            audioQuality = "AUDIO_QUALITY_MEDIUM",
            approxDurationMs = durationSeconds.takeIf { it >= 0 && it <= Long.MAX_VALUE / 1000 }
                ?.let { (it * 1000).toString() },
            audioSampleRate = null,
            audioChannels = null,
            loudnessDb = null,
            lastModified = null,
            signatureCipher = null,
            cipher = null,
            audioTrack = null,
        ),
        streamUrl = url,
        streamExpiresInSeconds = 600,
        isHlsStream = isHls,
    )
}
