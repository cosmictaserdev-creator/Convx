package com.convx.music.history

import kotlinx.coroutines.flow.Flow

data class ListenHistorySettings(
    val paused: Boolean = false,
)

sealed interface ListenHistorySettingsState {
    data object Loading : ListenHistorySettingsState
    data class Ready(val settings: ListenHistorySettings) : ListenHistorySettingsState
}

interface ListenHistorySettingsRepository {
    fun observe(): Flow<ListenHistorySettingsState>
    suspend fun setPaused(paused: Boolean)
}

fun shouldRecordListenEvent(
    playTimeMs: Long,
    minimumPlayTimeMs: Float,
    settingsState: ListenHistorySettingsState,
): Boolean = playTimeMs >= minimumPlayTimeMs &&
    (settingsState as? ListenHistorySettingsState.Ready)?.settings?.paused == false
