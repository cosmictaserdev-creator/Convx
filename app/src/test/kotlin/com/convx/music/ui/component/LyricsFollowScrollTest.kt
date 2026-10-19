package com.convx.music.ui.component

import org.junit.Assert.assertEquals
import org.junit.Test

class LyricsFollowScrollTest {
    @Test fun centersTheWholeLine() {
        assertEquals(0f, lyricCenterDistance(0, 1000, 450, 100), 0f)
    }

    @Test fun accountsForContentPaddingAndWrappedLines() {
        assertEquals(0f, lyricCenterDistance(-500, 500, -100, 200), 0f)
        assertEquals(100f, lyricCenterDistance(-500, 500, 0, 200), 0f)
    }
}
