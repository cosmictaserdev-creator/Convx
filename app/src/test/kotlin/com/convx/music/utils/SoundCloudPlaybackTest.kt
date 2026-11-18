package com.convx.music.utils

import com.music.innertube.soundcloud.SoundCloudStream
import org.junit.Assert.*
import org.junit.Test

class SoundCloudPlaybackTest {
    private fun stream(hls: Boolean = false) = SoundCloudStream(
        "https://audio.example/track", hls, "audio/mpeg", 192, 180,
        "Track title", "Uploader", "https://art.example/cover",
    )

    @Test fun progressiveMappingPreservesMetadataAndHasNoRemoteTracking() {
        val data = stream().toPlaybackData("sc:artist/track")
        assertEquals(SOUND_CLOUD_FORMAT_ITAG, data.format.itag)
        assertFalse(data.isHlsStream)
        assertEquals(192000, data.format.bitrate)
        assertEquals("audio/mpeg; codecs=\"mp3\"", data.format.mimeType)
        assertEquals("180000", data.format.approxDurationMs)
        assertEquals(600, data.streamExpiresInSeconds)
        assertEquals("Track title", data.videoDetails!!.title)
        assertEquals("Uploader", data.videoDetails!!.author)
        assertEquals("180", data.videoDetails!!.lengthSeconds)
        assertEquals("https://art.example/cover", data.videoDetails!!.thumbnail.thumbnails.single().url)
        assertNull(data.playbackTracking)
        assertNull(data.audioConfig)
        assertNull(data.format.loudnessDb)
    }

    @Test fun hlsMappingEnablesAdaptivePlayback() {
        assertTrue(stream(true).toPlaybackData("sc:artist/track").isHlsStream)
    }

    @Test fun absentBitrateAndMimeHavePlayableDefaults() {
        val data = stream().copy(mimeType = null, bitrateKbps = null).toPlaybackData("sc:artist/track")
        assertEquals(128000, data.format.bitrate)
        assertEquals("audio/mpeg; codecs=\"mp3\"", data.format.mimeType)
    }

    @Test fun opusAndAacCodecsArePreserved() {
        assertEquals("audio/ogg; codecs=\"opus\"", stream().copy(mimeType = "audio/ogg").toPlaybackData("sc:artist/track").format.mimeType)
        assertEquals("audio/mp4; codecs=\"mp4a.40.2\"", stream().copy(mimeType = "audio/mp4").toPlaybackData("sc:artist/track").format.mimeType)
    }

    @Test fun missingMetadataStillProvidesVideoDetails() {
        val data = stream().copy(title = null, uploader = null, thumbnailUrl = null, durationSeconds = -1).toPlaybackData("sc:artist/track")
        assertNotNull(data.videoDetails)
        assertEquals("sc:artist/track", data.videoDetails!!.videoId)
        assertEquals("0", data.videoDetails!!.lengthSeconds)
        assertTrue(data.videoDetails!!.thumbnail.thumbnails.isEmpty())
        assertNull(data.format.approxDurationMs)
    }
}
