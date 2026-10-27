package com.convx.music.history

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListenHistorySettingsTest {
    @Test
    fun recordsAfterMinimumPlayTimeWhenEnabled() {
        assertTrue(
            shouldRecordListenEvent(
                playTimeMs = 30_000,
                minimumPlayTimeMs = 30_000f,
                settingsState = ListenHistorySettingsState.Ready(ListenHistorySettings()),
            ),
        )
    }

    @Test
    fun skipsPausedShortAndNotYetLoadedEvents() {
        assertFalse(
            shouldRecordListenEvent(
                30_000,
                30_000f,
                ListenHistorySettingsState.Ready(ListenHistorySettings(paused = true)),
            ),
        )
        assertFalse(
            shouldRecordListenEvent(
                29_999,
                30_000f,
                ListenHistorySettingsState.Ready(ListenHistorySettings()),
            ),
        )
        assertFalse(
            shouldRecordListenEvent(30_000, 30_000f, ListenHistorySettingsState.Loading),
        )
    }
}
