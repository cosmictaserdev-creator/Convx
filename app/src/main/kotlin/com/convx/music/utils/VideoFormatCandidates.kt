package com.convx.music.utils

import com.music.innertube.models.response.PlayerResponse.StreamingData.Format

internal fun rankedVideoFormats(formats: List<Format>, requestedHeight: Int?): List<Format> {
    val target = requestedHeight?.takeIf { it > 0 } ?: 720
    return formats.filter { it.mimeType.startsWith("video/") && (it.height ?: 0) > 0 }
        .sortedWith(compareBy<Format> { if (it.height!! <= target) 0 else 1 }
            .thenBy { if (it.height!! <= target) -it.height!! else it.height!! }
            .thenByDescending { it.mimeType.contains("avc1") }
            .thenBy { it.fps ?: 30 }
            .thenBy { it.bitrate })
}
