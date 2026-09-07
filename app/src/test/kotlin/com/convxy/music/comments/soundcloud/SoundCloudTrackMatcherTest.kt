package com.convxy.music.comments.soundcloud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The honest hard part of the feature: proving a SoundCloud search hit is the same recording Convxy
 * is playing.
 *
 * Getting this wrong is worse than getting nothing — it attaches strangers' reactions to the wrong
 * moment of the wrong song and presents them as real. So these tests are weighted towards the
 * *rejections*: every one of them is a case where the answer has to be "I don't know".
 */
class SoundCloudTrackMatcherTest {

    private fun candidate(
        id: String,
        title: String,
        uploader: String = "",
        durationMs: Long = 250_000L,
        commentable: Boolean? = null,
    ) = SoundCloudTrackCandidate(
        id = id,
        title = title,
        uploaderName = uploader,
        durationMs = durationMs,
        commentable = commentable,
    )

    private fun best(
        candidates: List<SoundCloudTrackCandidate>,
        title: String,
        artists: List<String> = emptyList(),
        durationSeconds: Int = 250,
    ) = SoundCloudTrackMatcher.bestMatch(candidates, title, artists, durationSeconds)

    // ── acceptance ─────────────────────────────────────────────────────────

    @Test
    fun `an exact title with a matching duration is believed`() {
        val match = best(listOf(candidate("1", "Nightcall", "kavinsky")), "Nightcall")
        assertEquals("1", match?.id)
    }

    @Test
    fun `title decorations do not cost the match`() {
        for (title in listOf(
            "Nightcall (Official Video)",
            "Nightcall [Remastered]",
            "Nightcall - Official Audio",
            "Nightcall feat. Someone",
            "NIGHTCALL",
        )) {
            assertNotNull("rejected <$title>", best(listOf(candidate("1", "Nightcall")), title))
        }
    }

    @Test
    fun `an exact title alone is enough even with a different uploader`() {
        // Plenty of SoundCloud uploads credit the uploading account rather than the artist, and for a
        // label release or a repost that account is neither. Rejecting those would reject most real
        // matches.
        val match = best(listOf(candidate("1", "Nightcall", "some-random-reposter")), "Nightcall", listOf("Kavinsky"))
        assertNotNull(match)
    }

    @Test
    fun `the closest duration wins a tie on score`() {
        val match = best(
            listOf(
                candidate("far", "Nightcall (Official Video)", durationMs = 259_000L),
                candidate("near", "Nightcall", durationMs = 250_500L),
            ),
            "Nightcall",
        )
        assertEquals("near", match?.id)
    }

    @Test
    fun `a near-miss title still matches when the uploader agrees`() {
        val match = best(
            listOf(candidate("1", "Nightcall", "Daft Punk")),
            title = "Nightcal",
            artists = listOf("Daft Punk"),
        )
        assertNotNull("a 0.93 title plus an exact artist should clear the bar", match)
    }

    @Test
    fun `an unknown duration skips the duration gate`() {
        // Local files without a length tag must still be able to find their comments.
        val match = best(listOf(candidate("1", "Nightcall", durationMs = 999_999L)), "Nightcall", durationSeconds = 0)
        assertNotNull(match)
    }

    @Test
    fun `a candidate that omits its duration is not gated on it`() {
        assertTrue(SoundCloudTrackMatcher.durationCompatible(0L, 250_000L))
    }

    // ── rejection ──────────────────────────────────────────────────────────

    @Test
    fun `a different song is rejected`() {
        assertNull(best(listOf(candidate("1", "Completely Different Track", "someone")), "Nightcall"))
    }

    @Test
    fun `a near-miss title with an unrelated uploader is rejected`() {
        // Two different artists covering the same song have entirely different comment timelines.
        assertNull(
            best(
                listOf(candidate("1", "Nightcall", "Kavinsky")),
                title = "Nightcal",
                artists = listOf("Daft Punk"),
            ),
        )
    }

    @Test
    fun `a different length is a different recording however perfect the strings look`() {
        // A radio edit and an extended mix of the same title have genuinely different comment
        // timelines, so the duration is a gate rather than a score contribution.
        assertNull(best(listOf(candidate("1", "Nightcall", durationMs = 300_000L)), "Nightcall"))
    }

    @Test
    fun `the duration window is the larger of an absolute or a relative tolerance`() {
        // 8s absolute floor.
        assertTrue(SoundCloudTrackMatcher.durationCompatible(257_000L, 250_000L))
        assertTrue(!SoundCloudTrackMatcher.durationCompatible(262_000L, 250_000L))
        // 4% relative, which is wider than 8s on a ten-minute track.
        assertTrue(SoundCloudTrackMatcher.durationCompatible(620_000L, 600_000L))
        assertTrue(!SoundCloudTrackMatcher.durationCompatible(630_000L, 600_000L))
    }

    @Test
    fun `no candidates and no title both yield nothing`() {
        assertNull(best(emptyList(), "Nightcall"))
        assertNull(best(listOf(candidate("1", "Nightcall")), ""))
        assertNull(best(listOf(candidate("1", "Nightcall")), "   "))
    }

    @Test
    fun `nothing matching at all means no match, never a guess`() {
        val candidates = listOf(
            candidate("1", "Other Song", "other"),
            candidate("2", "Nightcall Remix Edit", "someone", durationMs = 400_000L),
        )
        assertNull(best(candidates, "Nightcall", listOf("Kavinsky")))
    }

    // ── scoring internals ──────────────────────────────────────────────────

    @Test
    fun `similarity is one for equal strings and zero for disjoint ones`() {
        assertEquals(1.0, SoundCloudTrackMatcher.similarity("nightcall", "nightcall").toDouble(), 0.0001)
        assertEquals(0.0, SoundCloudTrackMatcher.similarity("abc", "xyz").toDouble(), 0.0001)
        assertEquals(0.0, SoundCloudTrackMatcher.similarity("", "abc").toDouble(), 0.0001)
    }

    @Test
    fun `similarity is forgiving of a dropped character but not of a rewrite`() {
        val close = SoundCloudTrackMatcher.similarity("nightcall", "nightcal")
        assertTrue("$close should clear the bar", close >= SoundCloudTrackMatcher.MIN_TITLE_SIMILARITY)
        val far = SoundCloudTrackMatcher.similarity("nightcall", "daydream")
        assertTrue("$far should not", far < SoundCloudTrackMatcher.MIN_TITLE_SIMILARITY)
    }

    @Test
    fun `title normalisation drops decorations and noise words but keeps the name`() {
        assertEquals("song", SoundCloudTrackMatcher.normalizeTitle("The Song (Official Video)"))
        assertEquals("song", SoundCloudTrackMatcher.normalizeTitle("Song - Official Audio"))
        assertEquals("song", SoundCloudTrackMatcher.normalizeTitle("Song [Remastered 2024]"))
        assertEquals("song", SoundCloudTrackMatcher.normalizeTitle("Song feat. Someone"))
        // Artists keep their noise words, because "The Weeknd" is a name and not a decoration.
        assertEquals("the weeknd", SoundCloudTrackMatcher.normalizeArtist("The Weeknd"))
    }

    @Test
    fun `tokenising splits on punctuation so acronyms and hyphenated names still match`() {
        assertEquals(listOf("ac", "dc"), SoundCloudTrackMatcher.tokens("AC/DC"))
        assertEquals(listOf("g", "dragon"), SoundCloudTrackMatcher.tokens("G-Dragon"))
    }

    @Test
    fun `uploader overlap is Jaccard over word tokens`() {
        assertEquals(1.0, SoundCloudTrackMatcher.tokenOverlap(setOf("a", "b"), setOf("a", "b")).toDouble(), 0.0001)
        assertEquals(1.0 / 3.0, SoundCloudTrackMatcher.tokenOverlap(setOf("a", "b"), setOf("b", "c")).toDouble(), 0.0001)
        assertEquals(0.0, SoundCloudTrackMatcher.tokenOverlap(emptySet(), setOf("a")).toDouble(), 0.0001)
    }

    @Test
    fun `a disqualified candidate scores null rather than a low number`() {
        val wanted = SoundCloudTrackMatcher.normalizeTitle("Nightcall")
        assertNull(
            SoundCloudTrackMatcher.score(wanted, emptySet(), candidate("1", "Other Song"), 250_000L),
        )
        assertNotNull(SoundCloudTrackMatcher.score(wanted, emptySet(), candidate("1", "Nightcall"), 250_000L))
    }

    // ── query building ─────────────────────────────────────────────────────

    @Test
    fun `the search query puts the primary artist before a cleaned title`() {
        assertEquals(
            "Kavinsky Nightcall",
            SoundCloudTrackMatcher.searchQuery("Nightcall (Official Video)", listOf("Kavinsky")),
        )
        assertEquals("Nightcall", SoundCloudTrackMatcher.searchQuery("Nightcall", emptyList()))
        assertEquals("", SoundCloudTrackMatcher.searchQuery("  ", listOf("  ")))
    }
}
