package com.music.innertube.soundcloud

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SoundCloudIdsTest {
    @Test fun roundTrip() {
        val id = SoundCloudIds.toId("https://soundcloud.com/artist/track")
        assertEquals("sc:artist/track", id)
        assertEquals("https://soundcloud.com/artist/track", SoundCloudIds.toUrl(id!!))
        assertTrue(SoundCloudIds.isSoundCloudId(id))
    }

    @Test fun dropsQueryFragmentAndTrailingSlash() {
        assertEquals("sc:artist/track", SoundCloudIds.toId("https://soundcloud.com/artist/track/?si=token#t=10"))
    }

    @Test fun rejectsOtherHostsAndCredentials() {
        listOf("https://example.com/artist/track", "https://soundcloud.com.evil.test/artist/track", "https://soundcloud.com@evil.test/artist/track", "https://user@soundcloud.com/artist/track").forEach {
            assertNull(SoundCloudIds.toId(it))
        }
    }

    @Test fun rejectsEmptyAndNonTrackPaths() {
        listOf("", "garbage", "https://soundcloud.com/artist", "https://soundcloud.com/artist/sets/list", "https://soundcloud.com/artist/../", "https://soundcloud.com/charts/top").forEach {
            assertNull(SoundCloudIds.toId(it))
        }
        listOf("", "sc:", "sc:artist", "sc:artist/track?x=y", "sc:../track", "youtube:artist/track").forEach {
            assertNull(SoundCloudIds.toUrl(it))
            assertFalse(SoundCloudIds.isSoundCloudId(it))
        }
    }
}
