package com.convx.music.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LyricsSyncLevelCommonTest {
    @Test
    fun detectsPlainLineAndWordTimingFromProductionClassifier() {
        assertEquals(LyricsSyncLevel.PLAIN, LyricsSyncLevel.detect("Unstamped lyrics"))
        assertEquals(LyricsSyncLevel.LINE, LyricsSyncLevel.detect("[00:01.00]A line"))
        assertEquals(
            LyricsSyncLevel.WORD,
            LyricsSyncLevel.detect("[00:01.00]<00:01.00|00:01.50>Hello <00:01.80|00:02.30>world"),
        )
    }

    @Test
    fun productionUpgradePolicyKeepsBestTimingAndPriority() {
        val policy = LyricsUpgradePolicy()

        assertNotNull(policy.consider("plain", priority = 0, lyrics = "Hello"))
        assertNotNull(
            policy.consider(
                "word",
                priority = 5,
                lyrics = "<00:01.00|00:01.50>Hello <00:01.80|00:02.30>world",
            ),
        )
        assertNull(policy.consider("late-plain", priority = 0, lyrics = "A line"))
        assertNull(
            policy.consider(
                "lower-priority",
                priority = 9,
                lyrics = "<00:01.00|00:01.50>Hello <00:01.80|00:02.30>world",
            ),
        )
    }
}
