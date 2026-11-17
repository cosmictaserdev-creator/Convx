package com.music.innertube.soundcloud

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFalse
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.ContentAvailability
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType

class SoundCloudMapperTest {
    private fun track(url: String = "https://soundcloud.com/artist/track") =
        StreamInfoItem(ServiceList.SoundCloud.serviceId, url, "Track title", StreamType.AUDIO_STREAM).apply {
            uploaderName = "Artist name"
            duration = 123
            thumbnails = emptyList()
        }

    @Test fun mapsTrackAndLargestThumbnail() {
        val item = track().apply {
            thumbnails = listOf(
                Image("small", 100, 100, Image.ResolutionLevel.LOW),
                Image("large", 500, 500, Image.ResolutionLevel.HIGH),
            )
        }.toSongItem()!!
        assertEquals("sc:artist/track", item.id)
        assertEquals("Track title", item.title)
        assertEquals("Artist name", item.artists.single().name)
        assertNull(item.artists.single().id)
        assertEquals(123, item.duration)
        assertEquals("large", item.thumbnail)
        assertNull(item.album)
        assertFalse(item.explicit)
    }

    @Test fun missingThumbnailIsEmpty() {
        assertEquals("", track().toSongItem()!!.thumbnail)
    }

    @Test fun unknownAndOversizedDurationIsNull() {
        assertNull(track().apply { duration = -1 }.toSongItem()!!.duration)
        assertNull(track().apply { duration = Long.MAX_VALUE }.toSongItem()!!.duration)
    }

    @Test fun foreignUrlIsSkipped() {
        assertNull(track("https://example.com/artist/track").toSongItem())
    }

    @Test fun paidAndMembershipTracksAreSkipped() {
        assertNull(track().apply { contentAvailability = ContentAvailability.PAID }.toSongItem())
        assertNull(track().apply { contentAvailability = ContentAvailability.MEMBERSHIP }.toSongItem())
    }
}
