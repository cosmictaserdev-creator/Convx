package com.convx.music.library

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.PlaylistSongSortDescendingKey
import com.convx.music.constants.PlaylistSongSortType
import com.convx.music.constants.PlaylistSongSortTypeKey
import com.convx.music.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStorePlaylistDisplayPreferencesRepository(
    private val context: Context,
) : PlaylistDisplayPreferencesRepository {
    override fun observe(): Flow<PlaylistDisplayPreferences> = context.dataStore.data
        .map { preferences ->
            PlaylistDisplayPreferences(
                order = preferences[PlaylistSongSortTypeKey].toPlaylistTrackOrder(),
                descending = preferences[PlaylistSongSortDescendingKey] ?: true,
                hideVideos = (preferences[HideVideoSongsKey] ?: false) ||
                    (preferences[DataSaverEnabledKey] ?: false),
            )
        }
        .distinctUntilChanged()

    override suspend fun setOrder(order: PlaylistTrackOrder) {
        context.dataStore.edit { preferences ->
            preferences[PlaylistSongSortTypeKey] = order.toPreferenceValue()
        }
    }

    override suspend fun setDescending(descending: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PlaylistSongSortDescendingKey] = descending
        }
    }

    private fun String?.toPlaylistTrackOrder(): PlaylistTrackOrder = when (this) {
        PlaylistSongSortType.CUSTOM.name -> PlaylistTrackOrder.CUSTOM
        PlaylistSongSortType.CREATE_DATE.name -> PlaylistTrackOrder.CREATED
        PlaylistSongSortType.NAME.name -> PlaylistTrackOrder.NAME
        PlaylistSongSortType.ARTIST.name -> PlaylistTrackOrder.ARTIST
        PlaylistSongSortType.PLAY_TIME.name -> PlaylistTrackOrder.PLAY_TIME
        else -> PlaylistTrackOrder.CUSTOM
    }

    private fun PlaylistTrackOrder.toPreferenceValue(): String = when (this) {
        PlaylistTrackOrder.CUSTOM -> PlaylistSongSortType.CUSTOM
        PlaylistTrackOrder.CREATED -> PlaylistSongSortType.CREATE_DATE
        PlaylistTrackOrder.NAME -> PlaylistSongSortType.NAME
        PlaylistTrackOrder.ARTIST -> PlaylistSongSortType.ARTIST
        PlaylistTrackOrder.PLAY_TIME -> PlaylistSongSortType.PLAY_TIME
    }.name
}
