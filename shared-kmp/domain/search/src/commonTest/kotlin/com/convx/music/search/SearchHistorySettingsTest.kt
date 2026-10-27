package com.convx.music.search

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchHistorySettingsTest {
    @Test
    fun recordsNonemptyQueriesWhenHistoryIsEnabled() {
        assertTrue(shouldRecordSearchQuery("convx", SearchHistorySettings()))
    }

    @Test
    fun doesNotRecordQueriesWhenHistoryIsPaused() {
        assertFalse(shouldRecordSearchQuery("convx", SearchHistorySettings(paused = true)))
    }

    @Test
    fun doesNotRecordQueriesBeforeSettingsAreLoaded() {
        assertFalse(shouldRecordSearchQuery("convx", null))
    }

    @Test
    fun doesNotRecordEmptyQueries() {
        assertFalse(shouldRecordSearchQuery("", SearchHistorySettings()))
    }
}
