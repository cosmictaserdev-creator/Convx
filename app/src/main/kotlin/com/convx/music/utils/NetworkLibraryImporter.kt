package com.convx.music.utils

import android.content.Context
import com.convx.music.db.MusicDatabase
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.NetworkTrack
import com.convx.storage.domain.addTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Android storage adapters around the shared import policy. */
object NetworkLibraryImporter {
    data class Result(val added: Int, val skipped: Int, val truncated: Boolean)

    suspend fun import(database: MusicDatabase, repository: NetworkMusicRepository,
        serverId: String, context: Context): Result = withContext(Dispatchers.IO) {
        val result = com.convx.storage.domain.NetworkLibraryImporter(
            repository, repository, AndroidNetworkTagReader(), AndroidArtworkCache(context),
            RoomNetworkLibraryWriter(database),
        ).import(serverId)
        Result(result.added, result.skipped, result.truncated)
    }

    suspend fun insert(database: MusicDatabase, track: NetworkTrack): Boolean = withContext(Dispatchers.IO) {
        if (database.getSongById(track.id) != null) return@withContext false
        RoomNetworkLibraryWriter(database).addTracks(listOf(track))
        true
    }
}
