package com.music.innertube.soundcloud

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.MediaFormat
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.ContentAvailability
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamType

class SoundCloudStreamTest {
    private fun audio(bitrate: Int, method: DeliveryMethod) = AudioStream.Builder()
        .setId("$bitrate-$method")
        .setContent("https://audio.example/$bitrate-$method", true)
        .setMediaFormat(MediaFormat.MP3)
        .setAverageBitrate(bitrate)
        .setDeliveryMethod(method)
        .build()

    private fun info(vararg streams: AudioStream) = StreamInfo(
        ServiceList.SoundCloud.serviceId, "https://soundcloud.com/artist/track",
        "https://soundcloud.com/artist/track", StreamType.AUDIO_STREAM, "id", "Track title", 0,
    ).apply { audioStreams = streams.toList() }

    @Test fun prefersBestProgressiveAndPreservesMetadata() {
        val result = info(
            audio(320, DeliveryMethod.HLS),
            audio(64, DeliveryMethod.PROGRESSIVE_HTTP),
            audio(128, DeliveryMethod.PROGRESSIVE_HTTP),
        ).apply {
            uploaderName = "Uploader"
            duration = 180
            thumbnails = listOf(
                Image("small", 50, 50, Image.ResolutionLevel.LOW),
                Image("large", 500, 500, Image.ResolutionLevel.HIGH),
            )
        }.toSoundCloudStream()
        assertFalse(result.isHls)
        assertEquals(128, result.bitrateKbps)
        assertEquals("Track title", result.title)
        assertEquals("Uploader", result.uploader)
        assertEquals("large", result.thumbnailUrl)
        assertEquals(180L, result.durationSeconds)
        assertEquals("audio/mpeg", result.mimeType)
    }

    @Test fun fallsBackToBestHls() {
        val result = info(audio(64, DeliveryMethod.HLS), audio(128, DeliveryMethod.HLS)).toSoundCloudStream()
        assertTrue(result.isHls)
        assertEquals(128, result.bitrateKbps)
        assertNull(result.uploader)
        assertNull(result.thumbnailUrl)
    }

    @Test fun noStreamIsAnExplicitFailure() {
        assertFailsWith<IllegalStateException> { info().toSoundCloudStream() }
    }

    @Test fun paidStreamIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            info(audio(128, DeliveryMethod.PROGRESSIVE_HTTP)).apply {
                contentAvailability = ContentAvailability.PAID
            }.toSoundCloudStream()
        }
    }
}
