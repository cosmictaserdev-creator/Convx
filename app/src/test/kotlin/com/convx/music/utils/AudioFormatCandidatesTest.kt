package com.convx.music.utils

import com.convx.music.constants.AudioQuality
import com.music.innertube.models.response.PlayerResponse.StreamingData.Format
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioFormatCandidatesTest {
    private fun format(itag: Int, bitrate: Int, mime: String = "audio/mp4", dub: Boolean? = null) = Format(
        itag = itag, url = null, mimeType = mime, bitrate = bitrate, width = null, height = null,
        contentLength = null, quality = "tiny", fps = null, qualityLabel = null, averageBitrate = null,
        audioQuality = null, approxDurationMs = null, audioSampleRate = null, audioChannels = null,
        loudnessDb = null, lastModified = null, signatureCipher = null, cipher = null,
        audioTrack = dub?.let { Format.AudioTrack(null, null, it) },
    )

    @Test fun explicitOriginalTrackKeepsPriority() {
        val original = format(1, 128000, dub = false)
        val dub = format(2, 256000, dub = true)
        assertTrue(original.isOriginal)
        assertEquals(listOf(original, dub), rankedAudioFormats(listOf(dub, original), AudioQuality.HIGH, false, false))
    }

    @Test fun retainsAlternateFormatsForUrlFallback() {
        val low = format(1, 64000)
        val high = format(2, 128000)
        assertEquals(listOf(high, low), rankedAudioFormats(listOf(low, high), AudioQuality.HIGH, false, false))
        assertEquals(listOf(low, high), rankedAudioFormats(listOf(low, high), AudioQuality.AUTO, true, false))
    }

    @Test fun rejectsVideoEvenWithoutDimensions() {
        val video = format(3, 256000, "video/mp4")
        assertFalse(video.isAudio)
        assertTrue(rankedAudioFormats(listOf(video), AudioQuality.HIGH, false, false).isEmpty())
    }

    @Test fun aacPreferenceFiltersOpusAndKeepsDubOnlyResponsesPlayable() {
        val aac = format(1, 128000, dub = true)
        val opus = format(2, 160000, "audio/webm")
        assertEquals(listOf(aac), rankedAudioFormats(listOf(opus, aac), AudioQuality.HIGH, false, true))
    }
}
