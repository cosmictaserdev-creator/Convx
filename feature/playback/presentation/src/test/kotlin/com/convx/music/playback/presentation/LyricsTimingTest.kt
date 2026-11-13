package com.convx.music.playback.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

class LyricsTimingTest {
    @Test fun advancingLyricsStillSeeksToTheAudibleLine() {
        assertEquals(11_500L, lyricDisplayPosition(10_000L, 1_500L))
        assertEquals(10_000L, lyricSeekPosition(11_500L, 1_500L))
    }

    @Test fun delayingLyricsStillSeeksToTheAudibleLine() {
        assertEquals(8_500L, lyricDisplayPosition(10_000L, -1_500L))
        assertEquals(10_000L, lyricSeekPosition(8_500L, -1_500L))
    }

    @Test fun clockAndSeekNeverGoBeforeTheBeginning() {
        assertEquals(0L, lyricDisplayPosition(500L, -1_500L))
        assertEquals(0L, lyricSeekPosition(500L, 1_500L))
    }
}
