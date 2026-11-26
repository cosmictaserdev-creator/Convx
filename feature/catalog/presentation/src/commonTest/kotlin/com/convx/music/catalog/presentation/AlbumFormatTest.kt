package com.convx.music.catalog.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AlbumFormatTest {
    @Test fun trackDurationsPadSecondsAndShowHoursOnlyWhenNeeded() {
        assertEquals("0:07", formatTrackDuration(7_000))
        assertEquals("3:05", formatTrackDuration(185_000))
        assertEquals("1:02:03", formatTrackDuration(3_723_000))
    }

    @Test fun albumDurationIsInMinutesThenHours() {
        assertNull(formatAlbumDuration(0))
        assertNull(formatAlbumDuration(30_000))
        assertEquals("42 min", formatAlbumDuration(42 * 60_000L))
        assertEquals("1 hr 5 min", formatAlbumDuration(65 * 60_000L))
    }
}
