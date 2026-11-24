package com.convx.music.library

import kotlinx.coroutines.flow.Flow

data class PlaylistDisplayPreferences(
    val order: PlaylistTrackOrder = PlaylistTrackOrder.CUSTOM,
    val descending: Boolean = true,
    val hideVideos: Boolean = false,
)

/** Platform settings adapters preserve these playlist presentation preferences. */
interface PlaylistDisplayPreferencesRepository {
    fun observe(): Flow<PlaylistDisplayPreferences>
    suspend fun setOrder(order: PlaylistTrackOrder)
    suspend fun setDescending(descending: Boolean)
}
