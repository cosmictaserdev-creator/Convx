package com.convx.music.lyrics

import kotlin.test.Test
import kotlin.test.assertNotEquals

class LyricsTranslationKeyTest {
    @Test fun distinctLyricsWithTheSameStringHashDoNotShareTranslations() {
        assertNotEquals(LyricsTranslationKey("Aa", "translate", "en"), LyricsTranslationKey("BB", "translate", "en"))
        assertNotEquals(LyricsTranslationKey("Aa", "translate", "en"), LyricsTranslationKey("Aa", "translate", "fr"))
        assertNotEquals(LyricsTranslationKey("Aa", "translate", "en"), LyricsTranslationKey("Aa", "romanize", "en"))
    }
}
