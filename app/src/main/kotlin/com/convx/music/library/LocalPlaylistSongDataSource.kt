package com.convx.music.library

import androidx.compose.runtime.Immutable
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.Song
import kotlinx.coroutines.flow.Flow

/** Android row source for playlist screens that need platform Song objects for playback. */
interface LocalPlaylistSongDataSource {
    fun observeSongs(playlistId: String): Flow<List<LocalPlaylistSong>>

    suspend fun loadSongs(playlistId: String): List<LocalPlaylistSong>
}

@Immutable
data class LocalPlaylistSong(
    val track: PlaylistTrack,
    val map: PlaylistSongMap,
    val song: Song,
)
