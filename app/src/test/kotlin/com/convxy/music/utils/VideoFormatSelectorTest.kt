/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import com.music.innertube.models.response.PlayerResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Full-video playback picks one progressive (muxed audio+video) format per
 * song. These tests pin the ranking: resolution first, then fps, then
 * bitrate; audio-only and oversized entries ignored; empty input safe.
 */
class VideoFormatSelectorTest {

    private fun format(
        itag: Int,
        height: Int? = null,
        fps: Int? = null,
        bitrate: Int = 0,
        averageBitrate: Int? = null,
    ) = PlayerResponse.StreamingData.Format(
        itag = itag,
        url = "https://example.googlevideo.com/$itag",
        mimeType = if (height != null) "video/mp4; codecs=\"avc1.64001f, mp4a.40.2\"" else "audio/mp4; codecs=\"mp4a.40.2\"",
        bitrate = bitrate,
        width = height?.let { it * 16 / 9 },
        height = height,
        contentLength = 1_000_000L,
        quality = height?.let { "${it}p" } ?: "tiny",
        fps = fps,
        qualityLabel = height?.let { "${it}p" },
        averageBitrate = averageBitrate,
        audioQuality = if (height == null) "AUDIO_QUALITY_MEDIUM" else null,
        approxDurationMs = "240000",
        audioSampleRate = if (height == null) 44100 else null,
        audioChannels = if (height == null) 2 else null,
        loudnessDb = null,
        lastModified = null,
        signatureCipher = null,
        cipher = null,
        audioTrack = null,
    )

    @Test
    fun `highest resolution muxed format wins`() {
        val picked = selectMuxedVideoFormat(
            listOf(
                format(itag = 18, height = 360, fps = 30, bitrate = 500_000),
                format(itag = 22, height = 720, fps = 30, bitrate = 1_200_000),
                format(itag = 34, height = 480, fps = 30, bitrate = 800_000),
            ),
        )
        assertEquals(22, picked?.itag)
    }

    @Test
    fun `same resolution higher fps wins`() {
        val picked = selectMuxedVideoFormat(
            listOf(
                format(itag = 18, height = 360, fps = 30),
                format(itag = 396, height = 360, fps = 60),
            ),
        )
        assertEquals(396, picked?.itag)
    }

    @Test
    fun `audio only formats are never picked`() {
        val picked = selectMuxedVideoFormat(
            listOf(
                format(itag = 140, height = null, bitrate = 130_000),
                format(itag = 251, height = null, bitrate = 160_000),
            ),
        )
        assertNull(picked)
    }

    @Test
    fun `formats above 1080p are skipped for data safety`() {
        val picked = selectMuxedVideoFormat(
            listOf(
                format(itag = 9999, height = 2160, fps = 30, bitrate = 8_000_000),
                format(itag = 18, height = 360, fps = 30, bitrate = 500_000),
            ),
        )
        assertEquals(18, picked?.itag)
    }

    @Test
    fun `null and empty format lists fall back to no video`() {
        assertNull(selectMuxedVideoFormat(null))
        assertNull(selectMuxedVideoFormat(emptyList()))
    }

    @Test
    fun `null height entries are treated as audio and ignored`() {
        val picked = selectMuxedVideoFormat(
            listOf(format(itag = 17, height = null, bitrate = 100_000)),
        )
        assertNull(picked)
    }
}
