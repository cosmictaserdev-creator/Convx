package com.convx.music.library

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.convx.music.constants.PendingPlaylistDeletesKey
import com.convx.music.utils.dataStore
import kotlinx.coroutines.flow.first

class DataStorePendingPlaylistDeletionRepository(
    private val context: Context,
) : PendingPlaylistDeletionRepository {
    override suspend fun pendingBrowseIds(): Set<String> =
        context.dataStore.data.first()[PendingPlaylistDeletesKey] ?: emptySet()

    override suspend fun recordPendingDelete(browseId: String) {
        context.dataStore.edit { preferences ->
            preferences[PendingPlaylistDeletesKey] =
                (preferences[PendingPlaylistDeletesKey] ?: emptySet()) + browseId
        }
    }

    override suspend fun removePendingBrowseIds(browseIds: Set<String>) {
        if (browseIds.isEmpty()) return
        context.dataStore.edit { preferences ->
            preferences[PendingPlaylistDeletesKey] =
                (preferences[PendingPlaylistDeletesKey] ?: emptySet()) - browseIds
        }
    }
}
