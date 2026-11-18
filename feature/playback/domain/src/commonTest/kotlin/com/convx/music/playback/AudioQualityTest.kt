package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AudioQualityTest {
    @Test
    fun opusKeepsCodecAndBitrate() {
        val format = PlaybackStreamFormat("audio/webm; codecs=\"opus\"", bitrate = 131_000)
        assertEquals("Opus 131 kbps", format.quality?.label)
        assertEquals("Opus · 131 kbps", format.detail)
    }

    @Test
    fun losslessSplitsAtFortyEightKilohertz() {
        assertEquals("Lossless", PlaybackStreamFormat("audio/flac", sampleRate = 44_100).quality?.label)
        assertEquals("Hi-Res Lossless", PlaybackStreamFormat("audio/flac", sampleRate = 96_000).quality?.label)
        assertTrue(PlaybackStreamFormat("audio/flac").quality!!.lossless)
    }

    @Test
    fun unknownFormatsHaveNoBadge() {
        assertNull(PlaybackStreamFormat("application/octet-stream").quality)
        assertNull(audioQualityLabel(null, null, null, null))
    }

    @Test
    fun aacAndNonPositiveBitrate() {
        assertEquals("AAC 128 kbps", PlaybackStreamFormat("audio/mp4; codecs=\"mp4a.40.2\"", bitrate = 128_000).quality?.label)
        assertEquals("AAC", PlaybackStreamFormat("audio/mp4; codecs=\"mp4a.40.2\"", bitrate = 0).quality?.label)
    }
}
