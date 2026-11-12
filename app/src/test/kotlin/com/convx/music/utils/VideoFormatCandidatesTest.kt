package com.convx.music.utils

import com.music.innertube.models.response.PlayerResponse.StreamingData.Format
import org.junit.Assert.assertEquals
import org.junit.Test

class VideoFormatCandidatesTest {
    private fun format(height: Int?, mime: String = "video/mp4; codecs=avc1", fps: Int = 30) = Format(
        itag = height ?: 0, url = null, mimeType = mime, bitrate = 100_000, width = height?.times(16)?.div(9),
        height = height, contentLength = null, quality = "", fps = fps, qualityLabel = null,
        averageBitrate = null, audioQuality = null, approxDurationMs = null, audioSampleRate = null,
        audioChannels = null, loudnessDb = null, lastModified = null, signatureCipher = null,
        cipher = null, audioTrack = null,
    )

    @Test fun prefersRequestedResolutionAndFallsBackToLowerVideo() {
        val candidates = listOf(format(1080), format(360), format(720), format(null, "audio/mp4"))
        assertEquals(listOf(720, 360, 1080), rankedVideoFormats(candidates, 720).map { it.height })
        assertEquals(listOf(360, 720, 1080), rankedVideoFormats(candidates, 360).map { it.height })
    }

    @Test fun usesNearestHigherResolutionWhenNothingFitsAndAutoPrefers720() {
        val candidates = listOf(format(2160), format(1080), format(720))
        assertEquals(listOf(720, 1080, 2160), rankedVideoFormats(candidates, 360).map { it.height })
        assertEquals(listOf(720, 1080, 2160), rankedVideoFormats(candidates, null).map { it.height })
    }
}
