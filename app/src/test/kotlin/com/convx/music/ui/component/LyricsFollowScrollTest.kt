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

    @Test fun linesBelowTheActiveOneTrailItAndTheRestMoveAtOnce() {
        assertEquals(0L, rubberDelayMs(itemIndex = 3, anchorIndex = 5))
        assertEquals(0L, rubberDelayMs(itemIndex = 5, anchorIndex = 5))
        assertEquals(80L, rubberDelayMs(itemIndex = 7, anchorIndex = 5))
        assertEquals(400L, rubberDelayMs(itemIndex = 40, anchorIndex = 5))
    }

    @Test fun theSpringStartsAtTheOldPlaceAndSettlesAtTheNewOne() {
        assertEquals(1f, rubberRemaining(-5), 0f)
        assertEquals(1f, rubberRemaining(0), 0f)
        assertEquals(true, rubberRemaining(200_000_000) in 0f..1f)
        assertEquals(0f, rubberRemaining(1_000_000_000), 0f)
    }

    @Test fun linesBelowTheActiveOneTrailTheListAndThenCatchUp() {
        val delay = rubberDelayMs(itemIndex = 7, anchorIndex = 5) * 1_000_000
        assertEquals(0f, rubberLag(300f, elapsedNs = 0, delayNs = delay), 0f)
        assertEquals(0f, rubberLag(300f, elapsedNs = 150_000_000, delayNs = 0), 0f)
        assertEquals(true, rubberLag(300f, elapsedNs = 150_000_000, delayNs = delay) > 0f)
        assertEquals(0f, rubberLag(300f, elapsedNs = 1_500_000_000, delayNs = delay), 0f)
    }
}
