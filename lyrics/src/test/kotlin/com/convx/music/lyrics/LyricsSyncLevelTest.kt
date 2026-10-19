package com.convx.music.lyrics

import org.junit.Assert.*
import org.junit.Test

class LyricsSyncLevelTest {
    private val words = "[00:01.000]<00:01.000|00:01.500>Hello <00:01.800|00:02.300>world"

    @Test fun detectsRealWordTimingAndRejectsInvalidRuns() {
        assertEquals(LyricsSyncLevel.WORD, LyricsSyncLevel.detect(words))
        assertEquals(LyricsSyncLevel.LINE, LyricsSyncLevel.detect("[00:01.000]<00:01.000|00:00.500>Hello <00:01.000|00:00.700>world"))
        assertEquals(LyricsSyncLevel.LINE, LyricsSyncLevel.detect("[00:01.000]<00:01.000|00:03.000>An entire line"))
        assertEquals(LyricsSyncLevel.PLAIN, LyricsSyncLevel.detect("An entire line\nAnother line"))
        assertEquals(LyricsSyncLevel.LINE, LyricsSyncLevel.detect("[00:01.000]First\n[00:02.000]Second"))
        assertEquals(LyricsSyncLevel.WORD, LyricsSyncLevel.detect("[00:01.000]Hello world\n<Hello:1.0:1.5|world:1.8:2.3>"))
    }

    @Test fun upgradesWithoutDowngradingAndHonoursPriorityWithinQuality() {
        val policy = LyricsUpgradePolicy()
        assertNotNull(policy.consider("plain", 0, "Hello world"))
        assertNotNull(policy.consider("lines", 5, "[00:01.000]Hello world"))
        assertNotNull(policy.consider("words", 6, words))
        assertNull(policy.consider("late-plain", 0, "Hello world"))
        assertNull(policy.consider("late-words", 9, words))
        assertNotNull(policy.consider("preferred-words", 1, words))
        assertEquals("preferred-words", policy.best?.provider)
    }
}
