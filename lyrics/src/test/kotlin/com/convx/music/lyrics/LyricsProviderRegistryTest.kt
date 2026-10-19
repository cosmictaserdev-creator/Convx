package com.convx.music.lyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LyricsProviderRegistryTest {
    @org.junit.Test
    fun upgradingAnOrderPreservesPriorityAndAddsEveryProviderOnce() {
        val order = LyricsProviderRegistry.completeProviderOrder("Kugou,Kugou,missing,YouTubeMusic")
        assertEquals(listOf("Kugou", "YouTubeMusic"), order.take(2))
        assertEquals(order.size, order.distinct().size)
        assertEquals(LyricsProviderRegistry.providerIds.toSet(), order.toSet())
        assertTrue(order.containsAll(listOf("LrcRed", "BiniLyrics", "BetterLyricsPortato", "Genius", "PaxsenixSpotify", "PaxsenixMusixmatch")))
    }
    @Test
    fun videoProvidersDeclareStableIdsAndRequireVideoIds() {
        assertEquals("YouTubeMusic", YouTubeLyricsProvider.id)
        assertEquals("YouTubeSubtitle", YouTubeSubtitleLyricsProvider.id)
        assertTrue(YouTubeLyricsProvider.requiresVideoId)
        assertTrue(YouTubeSubtitleLyricsProvider.requiresVideoId)
    }

    @Test
    fun storedDisplayNamesResolveToProviderIds() {
        assertSame(YouTubeLyricsProvider, LyricsProviderRegistry.getProviderForStoredName("YouTube Music"))
        assertSame(YouTubeSubtitleLyricsProvider, LyricsProviderRegistry.getProviderForStoredName("YouTube Subtitle"))
        assertEquals("YouTube Music", LyricsProviderRegistry.getDisplayName("YouTubeMusic"))
    }

    @Test
    fun providerOrderKeepsKnownIdsAndDropsUnknownValues() {
        val order = LyricsProviderRegistry.deserializeProviderOrder("Kugou,missing,YouTubeMusic")

        assertEquals(listOf("Kugou", "YouTubeMusic"), order)
        assertEquals("Kugou,YouTubeMusic", LyricsProviderRegistry.serializeProviderOrder(order))
    }
}
