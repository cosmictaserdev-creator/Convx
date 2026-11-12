package com.convx.music.playback

import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.ReleaseRadarRules.KindAndYear
import com.convx.music.playback.ReleaseRadarRules.epochDayOf
import com.convx.music.playback.ReleaseRadarRules.isRecent
import com.convx.music.playback.ReleaseRadarRules.kindAndYear
import com.convx.music.playback.ReleaseRadarRules.normalize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseRadarRulesTest {
    @Test
    fun readsKindAndYearFromTheSubtitle() {
        assertEquals(KindAndYear(Kind.SINGLE, 2026), kindAndYear("Single • 2026"))
        assertEquals(KindAndYear(Kind.ALBUM, 2024), kindAndYear("2024"))
        assertEquals(KindAndYear(Kind.EP, 2025), kindAndYear("EP · 2025"))
        assertEquals(KindAndYear(Kind.ALBUM, 2023), kindAndYear("Album • 2023"))
    }

    @Test
    fun aSubtitleWithoutAYearGivesYearZeroAndUnknownKindsAreAlbums() {
        assertEquals(KindAndYear(Kind.ALBUM, 0), kindAndYear(""))
        assertEquals(KindAndYear(Kind.ALBUM, 0), kindAndYear("Sencillo"))
        assertEquals(KindAndYear(Kind.SINGLE, 0), kindAndYear("single"))
    }

    @Test
    fun theYearIsFoundNextToOtherNumbersButNotInsideLongerOnes() {
        assertEquals(2026, kindAndYear("Single • 2026 • 5 songs").year)
        assertEquals(2022, kindAndYear("Album • 12 songs • 2022").year)
        assertEquals(0, kindAndYear("Album • 120245 plays").year)
    }

    @Test
    fun titlesMatchAcrossSpellings() {
        assertEquals(normalize("Espresso"), normalize("Espresso EP"))
        assertEquals(normalize("Man’s Best Friend"), normalize("Man's Best Friend (Explicit)"))
        assertEquals(normalize("Bring Your Love"), normalize("Bring Your Love - Single"))
        assertFalse(normalize("Short n' Sweet") == normalize("Sweet"))
    }

    @Test
    fun aTitleThatIsOnlyBracketedKeepsItsLettersSoItsDistinct() {
        assertNotEquals(normalize("(Intro)"), normalize("(Outro)"))
        assertTrue(normalize("(Intro)").isNotEmpty())
    }

    @Test
    fun normalizingEmptyOrPunctuationOnlyTitlesIsEmptyWithoutThrowing() {
        assertEquals("", normalize(""))
        assertEquals("", normalize("!!! ???"))
    }

    @Test
    fun parsesRealCalendarDates() {
        assertEquals(0L, epochDayOf("1970-01-01"))
        assertEquals(-1L, epochDayOf("1969-12-31"))
        assertEquals(11_017L, epochDayOf("2000-03-01"))
        assertEquals(epochDayOf("2024-03-01")!! - 1, epochDayOf("2024-02-29"))
        assertEquals(epochDayOf("2026-03-01")!! - 1, epochDayOf("2026-02-28"))
    }

    @Test
    fun rejectsDatesThatDoNotExistOrAreMalformed() {
        assertNull(epochDayOf("2026-02-29"))
        assertNull(epochDayOf("1900-02-29"))
        assertNull(epochDayOf("2026-13-01"))
        assertNull(epochDayOf("2026-00-10"))
        assertNull(epochDayOf("2026-04-31"))
        assertNull(epochDayOf("2026-4-1"))
        assertNull(epochDayOf("not a date"))
        assertNull(epochDayOf(""))
    }

    @Test
    fun aDateIsJudgedByItsReleaseDateElseWhenItWasFirstSeen() {
        val today = epochDayOf("2026-10-18")!!
        fun recent(date: String?, seenDaysAgo: Long) =
            isRecent(date?.let(::epochDayOf), today - seenDaysAgo, today, 14)
        assertTrue(recent("2026-10-16", 100))
        assertFalse(recent("2026-09-01", 1))
        assertTrue(recent(null, 3))
        assertFalse(recent(null, 30))
        assertTrue(recent("2026-10-23", 0))
    }

    @Test
    fun theWindowEdgesAreInclusiveAndAnnouncementsAreCappedAtAWeek() {
        val today = epochDayOf("2026-10-18")!!
        assertTrue(isRecent(today - 14, 0, today, 14))
        assertFalse(isRecent(today - 15, 0, today, 14))
        assertTrue(isRecent(today + 7, 0, today, 14))
        assertFalse(isRecent(today + 8, 0, today, 14))
        assertFalse(isRecent(today, 0, today, -1))
    }
}
