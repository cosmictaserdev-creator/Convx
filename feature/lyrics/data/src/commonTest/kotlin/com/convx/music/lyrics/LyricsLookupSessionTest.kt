package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.BiniLyrics
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LyricsLookupSessionTest {
    private val query = LyricsQuery("video", "Song (Official Video)", "Artist - Topic", 180_000)

    @Test fun disabledIdentificationProviderReceivesNoRequest() = runTest {
        var calls = 0
        val factory = LyricsLookupSessionFactory(backgroundScope, identify = { calls++; error("Must not run") })
        assertNull(factory.create(query, setOf("LrcRed", "YouLyPlus")).recordingId)
        assertEquals(0, calls)
    }

    @Test fun identifiedRecordingIsSharedAndReusedOnlyForTheSameQuery() = runTest {
        val requests = mutableListOf<LyricsQuery>()
        val factory = LyricsLookupSessionFactory(backgroundScope, identify = {
            requests += it
            BiniLyrics.Hit(isrc = "USABC2600001", lyricsUrl = "https://lrc.red/s/USABC2600001.ttml")
        })
        val first = factory.create(query, setOf("BiniLyrics", "LrcRed"))
        val second = factory.create(query, setOf("LrcRed"))
        assertEquals("USABC2600001", first.recordingId)
        assertEquals(first.recordingId, second.recordingId)
        assertEquals(1, requests.size)
        assertEquals("Song", requests.single().title)
        assertEquals("Artist", requests.single().artist)
        factory.create(query.copy(id = "different-video", durationMs = 200_000), setOf("BiniLyrics"))
        assertEquals(2, requests.size)
    }

    @Test fun aSlowIdentifierTimesOutWithoutBlockingFallbackProviders() = runTest {
        val factory = LyricsLookupSessionFactory(backgroundScope, identify = { delay(10_000); null })
        assertNull(factory.create(query, setOf("BiniLyrics")).recordingId)
        assertEquals(2_500L, currentTime)
    }

    @Test fun parentCancellationIsNotConvertedIntoAProviderMiss() = runTest {
        var cancelled = false
        val factory = LyricsLookupSessionFactory(backgroundScope, identify = {
            try { awaitCancellation() } finally { cancelled = true }
        })
        val request = launch { factory.create(query, setOf("BiniLyrics")); fail("Cancelled lookup returned") }
        runCurrent()
        request.cancelAndJoin()
        assertTrue(cancelled)
    }

    @Test fun providersShareOneDocumentAndCancellingOneWaiterKeepsTheOthersDownload() = runTest {
        var downloads = 0
        val factory = LyricsLookupSessionFactory(backgroundScope, download = {
            downloads++
            delay(100)
            "document"
        })
        val session = factory.create(query, emptySet())
        val first = async { session.document("https://example.test/song.ttml") }
        val second = async { session.document("https://example.test/song.ttml") }
        runCurrent()
        first.cancelAndJoin()
        assertEquals("document", second.await())
        assertEquals("document", session.document("https://example.test/song.ttml"))
        assertEquals(1, downloads)
    }

    @Test fun failedDocumentIsSharedWithinOneLookupAndRetriedInANewLookup() = runTest {
        var downloads = 0
        val factory = LyricsLookupSessionFactory(backgroundScope, download = { downloads++; null })
        val session = factory.create(query, emptySet())
        assertNull(session.document("https://example.test/missing.ttml"))
        assertNull(session.document("https://example.test/missing.ttml"))
        assertNull(factory.create(query, emptySet()).document("https://example.test/missing.ttml"))
        assertEquals(2, downloads)
    }
}
