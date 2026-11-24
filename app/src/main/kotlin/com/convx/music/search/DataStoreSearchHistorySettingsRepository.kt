package com.convx.music.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.convx.music.constants.PauseSearchHistoryKey
import com.convx.music.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreSearchHistorySettingsRepository(
    private val context: Context,
) : SearchHistorySettingsRepository {
    override fun observe(): Flow<SearchHistorySettings?> = context.dataStore.data
        .map { preferences ->
            SearchHistorySettings(paused = preferences[PauseSearchHistoryKey] ?: false)
        }
        .distinctUntilChanged()

    override suspend fun setPaused(paused: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PauseSearchHistoryKey] = paused
        }
    }
}
