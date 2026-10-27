package com.music.innertube

import com.music.innertube.utils.YouTubeUrlParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YouTubeUrlParserTest {
    @Test
    fun parsesSupportedVideoLinks() {
        assertEquals(
            YouTubeUrlParser.ParsedUrl.Video("abcdefghijk"),
            YouTubeUrlParser.parse("https://music.youtube.com/watch?v=abcdefghijk"),
        )
        assertEquals(
            YouTubeUrlParser.ParsedUrl.Video("abcdefghijk"),
            YouTubeUrlParser.parse("https://youtu.be/abcdefghijk"),
        )
        assertEquals(
            YouTubeUrlParser.ParsedUrl.Video("abcdefghijk"),
            YouTubeUrlParser.parse("https://www.youtube.com/shorts/abcdefghijk"),
        )
    }

    @Test
    fun parsesSupportedArtistLinks() {
        assertEquals(
            YouTubeUrlParser.ParsedUrl.Artist("UCabcdefghijk"),
            YouTubeUrlParser.parse("https://music.youtube.com/channel/UCabcdefghijk"),
        )
        assertEquals(
            YouTubeUrlParser.ParsedUrl.Artist("MPREabcdefgh"),
            YouTubeUrlParser.parse("https://music.youtube.com/browse/MPREabcdefgh"),
        )
    }

    @Test
    fun rejectsUnsupportedLinks() {
        assertNull(YouTubeUrlParser.parse("https://example.com/watch?v=abcdefghijk"))
        assertNull(YouTubeUrlParser.parse("https://youtube.com/playlist?list=PLabcdefgh"))
        assertTrue(YouTubeUrlParser.isYouTubeUrl("https://youtu.be/abcdefghijk"))
    }
}
