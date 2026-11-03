/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import androidx.room.withTransaction
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.storage.domain.NetworkLibraryRepository
import com.convx.storage.domain.NetworkTrack
import java.time.LocalDateTime
import timber.log.Timber

/**
 * Puts the audio found on an SMB or WebDAV source into the library as local songs.
 * A song's id is its `convx-network://` URI, which the player routes back through the
 * network data source, so nothing is copied to the phone.
 */
object NetworkLibraryImporter {

    data class Result(val added: Int, val skipped: Int, val truncated: Boolean)

    suspend fun import(
        database: MusicDatabase,
        repository: NetworkLibraryRepository,
        serverId: String,
    ): Result {
        val library = repository.scan(serverId)
        var added = 0
        var skipped = 0
        library.tracks.forEach { track ->
            if (insert(database, track)) added++ else skipped++
        }
        return Result(added, skipped, library.truncated)
    }

    suspend fun insert(database: MusicDatabase, track: NetworkTrack): Boolean {
        if (database.getSongById(track.id) != null) return false
        val artistNames = ArtistSplitter.split(track.artist).ifEmpty { listOf("Unknown Artist") }
        val albumName = track.album.orEmpty()
        val artistId = "network_artist_${artistName.hashCode()}"
        val albumId = "network_album_${albumName.hashCode()}"
        val now = LocalDateTime.now()
        return try {
            database.withTransaction {
                upsert(
                    SongEntity(
                        id = track.id,
                        title = track.title,
                        thumbnailUrl = track.artwork,
                        albumId = albumId.takeIf { albumName.isNotBlank() },
                        albumName = albumName.takeIf { it.isNotBlank() },
                        isLocal = true,
                        inLibrary = now,
                    ),
                )
                artistNames.forEachIndexed { index, name ->
                    val artistId = "network_artist_${name.hashCode()}"
                    insert(ArtistEntity(id = artistId, name = name, isLocal = true))
                    insert(SongArtistMap(songId = track.id, artistId = artistId, position = index))
                }
                if (albumName.isNotBlank()) {
                    insert(
                        AlbumEntity(
                            id = albumId,
                            title = albumName,
                            thumbnailUrl = track.artwork,
                            songCount = 1,
                            duration = 0,
                            isLocal = true,
                            inLibrary = now,
                        ),
                    )
                    insert(SongAlbumMap(songId = track.id, albumId = albumId, index = 0))
                }
            }
            true
        } catch (e: Exception) {
            Timber.tag("NetworkImport").w(e, "Failed to insert ${track.title}")
            false
        }
    }
}
