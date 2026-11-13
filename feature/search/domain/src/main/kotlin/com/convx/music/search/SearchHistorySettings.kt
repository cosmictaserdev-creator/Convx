package com.convx.music.search

import kotlinx.coroutines.flow.Flow

data class SearchHistorySettings(
    val paused: Boolean = false,
)

interface SearchHistorySettingsRepository {
    /** Null indicates that the adapter has not made persisted settings available yet. */
    fun observe(): Flow<SearchHistorySettings?>
    suspend fun setPaused(paused: Boolean)
}

fun shouldRecordSearchQuery(query: String, settings: SearchHistorySettings?): Boolean =
    query.isNotEmpty() && settings?.paused == false
