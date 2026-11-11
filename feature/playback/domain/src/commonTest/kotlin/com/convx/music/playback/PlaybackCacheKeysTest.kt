package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaybackCacheKeysTest {
    @Test
    fun distinguishesAudioFromCanvas() {
        assertTrue(isPlaybackAudioCacheKey("song"))
        assertTrue(isPlaybackAudioCacheKey("song#aac"))
        assertTrue(isPlaybackAudioCacheKey("song#hq"))
        assertTrue(isPlaybackAudioCacheKey("song#source-hash"))
        assertTrue(isPlaybackAudioCacheKey("song#source-hash#aac"))
        assertFalse(isPlaybackAudioCacheKey("song#canvas"))
        assertFalse(isPlaybackAudioCacheKey("song#source-hash#canvas"))
    }

    @Test
    fun matchesOnlyTheRequestedSong() {
        assertTrue(cacheKeyBelongsToSong("song", "song"))
        assertTrue(cacheKeyBelongsToSong("song#source-hash", "song"))
        assertFalse(cacheKeyBelongsToSong("song2#source-hash", "song"))
    }

    @Test
    fun analysisAcceptsAudioAndItsDerivedHeadButNeverCanvas() {
        assertTrue(isAnalysisAudioCacheKey("song", "song"))
        assertTrue(isAnalysisAudioCacheKey("song#source-a", "song"))
        assertTrue(isAnalysisAudioCacheKey("song#hls-analysis", "song"))
        assertTrue(isAnalysisAudioCacheKey("song#jam-yt#aac", "song"))
        assertFalse(isAnalysisAudioCacheKey("song#canvas-v2:https://example.test/video", "song"))
        assertFalse(isAnalysisAudioCacheKey("song2#hls-analysis", "song"))
    }

    @Test
    fun offlineFallbackUsesOnlyCompleteAudioForTheRequestedSong() {
        val keys = listOf("song#canvas", "song#source-a", "song#source-b", "song2#source-a")
        assertEquals("song#source-b", chooseCompleteAudioCacheKey("song", "song#source-c", keys) { it == "song#source-b" })
        assertEquals("song#source-a", chooseCompleteAudioCacheKey("song", "song#source-a", keys) { it.endsWith("source-a") })
        assertNull(chooseCompleteAudioCacheKey("song", "song#source-c", keys) { it == "song#canvas" })
    }
}
