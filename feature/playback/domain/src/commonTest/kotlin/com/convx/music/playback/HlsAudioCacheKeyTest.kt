package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class HlsAudioCacheKeyTest {
    @Test fun retainsRenditionIdentityAcrossCredentialRotation() {
        val first = hlsAudioCacheKey("song", "https://example.test/a.m3u8?quality=hi&token=old")
        assertEquals(first, hlsAudioCacheKey("song", "https://example.test/a.m3u8?token=new&quality=hi"))
        assertNotEquals(first, hlsAudioCacheKey("song", "https://example.test/a.m3u8?quality=lo"))
        assertTrue(cacheKeyBelongsToSong(first, "song"))
    }
}
