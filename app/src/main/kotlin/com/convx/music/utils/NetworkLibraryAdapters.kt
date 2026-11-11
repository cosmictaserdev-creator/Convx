/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.withTransaction
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumArtistMap
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.music.catalog.AlbumDiscIndex
import com.convx.storage.domain.ArtworkCache
import com.convx.storage.domain.ImportedAlbum
import com.convx.storage.domain.ImportedSong
import com.convx.storage.domain.NetworkLibraryWriter
import com.convx.storage.domain.NetworkMediaUri
import com.convx.storage.domain.NetworkEmbeddedArtworkUri
import com.convx.storage.domain.NetworkTags
import com.convx.storage.domain.networkAlbumId
import com.convx.storage.domain.SongArtworkState
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/** Keeps cover art in app storage (not cache) so a cleared cache never leaves songs pointing at missing files. */
class AndroidArtworkCache(context: Context) : ArtworkCache {
    private val directory = File(context.filesDir, "network-art")

    override suspend fun put(key: String, bytes: ByteArray): String? = withContext(Dispatchers.IO) {
        val file = File(directory, "${key.hashCode().toUInt().toString(16)}_${key.length}.jpg")
        if (file.length() == 0L) {
            val bitmap = decodeScaled(bytes) ?: return@withContext null
            directory.mkdirs()
            // Written aside and renamed, so a second caller never sees a half-written cover.
            val partial = File(directory, "${file.name}.${System.nanoTime()}.tmp")
            val written = try {
                partial.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            } finally {
                bitmap.recycle()
            }
            if (!written || (!partial.renameTo(file) && file.length() == 0L)) {
                partial.delete()
                return@withContext null
            }
            partial.delete()
        }
        Uri.fromFile(file).toString()
    }

    private fun decodeScaled(bytes: ByteArray): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MaxEdge) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    }

    private companion object {
        const val MaxEdge = 1024
    }
}

class RoomNetworkLibraryWriter(private val database: MusicDatabase) : NetworkLibraryWriter {

    override fun embeddedArtwork(songId: String): String? = NetworkEmbeddedArtworkUri.create(songId)

    override suspend fun artworkState(songId: String): SongArtworkState {
        val song = database.getSongById(songId)?.song ?: return SongArtworkState.NEW
        val art = song.thumbnailUrl
        // A `convx-network://` thumbnail has no notification/lock-screen art, so it counts as missing.
        val missingFile = art?.let { Uri.parse(it) }?.takeIf { it.scheme == "file" }
            ?.path?.let { !File(it).isFile } == true
        return if (art.isNullOrBlank() || art.startsWith("${NetworkMediaUri.Scheme}://") || art.startsWith("${NetworkEmbeddedArtworkUri.Scheme}://") || missingFile) SongArtworkState.MISSING else SongArtworkState.PRESENT
    }

    override suspend fun write(songs: List<ImportedSong>, albums: List<ImportedAlbum>) {
        val now = LocalDateTime.now()
        database.withTransaction {
            songs.forEach { imported ->
                val track = imported.track
                val artistName = track.artist?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
                val artistId = artistId(artistName)
                upsert(
                    SongEntity(
                        id = track.id,
                        title = track.title,
                        duration = track.durationSeconds,
                        thumbnailUrl = imported.artworkUri,
                        albumId = imported.albumId,
                        albumName = track.album?.takeIf { imported.albumId != null },
                        year = track.year,
                        isLocal = true,
                        inLibrary = now,
                    ),
                )
                insert(ArtistEntity(id = artistId, name = artistName, isLocal = true))
                insert(SongArtistMap(songId = track.id, artistId = artistId, position = 0))
            }
            albums.forEach { entry ->
                val existing = album(entry.id).first()?.album
                val offset = existing?.songCount ?: 0
                if (existing == null) {
                    insert(
                        AlbumEntity(
                            id = entry.id,
                            title = entry.title,
                            year = entry.year,
                            thumbnailUrl = entry.artworkUri,
                            songCount = entry.songCount,
                            duration = entry.durationSeconds,
                            isLocal = true,
                            inLibrary = now,
                        ),
                    )
                    entry.artist?.takeIf { it.isNotBlank() }?.let { name ->
                        val id = artistId(name)
                        insert(ArtistEntity(id = id, name = name, isLocal = true))
                        insert(AlbumArtistMap(albumId = entry.id, artistId = id, order = 0))
                    }
                } else {
                    update(
                        existing.copy(
                            songCount = existing.songCount + entry.songCount,
                            duration = existing.duration + entry.durationSeconds,
                            thumbnailUrl = existing.thumbnailUrl ?: entry.artworkUri,
                        ),
                    )
                }
                songs.filter { it.albumId == entry.id }.forEach {
                    insert(SongAlbumMap(songId = it.track.id, albumId = entry.id, index = AlbumDiscIndex.encode(it.track.discNumber, (offset + it.index).coerceAtMost(AlbumDiscIndex.DISC_STRIDE - 1))))
                }
            }
        }
    }

    override suspend fun updateArtwork(songId: String, artworkUri: String) {
        database.withTransaction {
            val song = getSongById(songId)?.song ?: return@withTransaction
            update(song.copy(thumbnailUrl = artworkUri))
            val owner = song.albumId?.let { album(it).first()?.album } ?: return@withTransaction
            val current = owner.thumbnailUrl
            if (current.isNullOrBlank() || current.startsWith("${NetworkMediaUri.Scheme}://") || current.startsWith("${NetworkEmbeddedArtworkUri.Scheme}://") ||
                (Uri.parse(current).scheme == "file" && Uri.parse(current).path?.let { !File(it).isFile } == true)) {
                update(owner.copy(thumbnailUrl = artworkUri))
            }
        }
    }

    override suspend fun updateMetadata(songId: String, tags: NetworkTags) {
        database.withTransaction {
            val song = getSongById(songId)?.song ?: return@withTransaction
            val title = tags.title?.takeIf { it.isNotBlank() }
            val artist = tags.artist?.takeIf { it.isNotBlank() }
            val albumName = tags.album?.takeIf { it.isNotBlank() }
            val albumArtist = tags.albumArtist?.takeIf { it.isNotBlank() } ?: artist
            val albumId = if (albumName != null && albumArtist != null) networkAlbumId(albumName, albumArtist) else song.albumId
            update(song.copy(
                title = title ?: song.title,
                duration = tags.durationSeconds?.takeIf { it > 0 } ?: song.duration,
                year = tags.year ?: song.year,
                albumId = albumId,
                albumName = albumName ?: song.albumName,
            ))
            if (artist != null) {
                val id = artistId(artist)
                insert(ArtistEntity(id = id, name = artist, isLocal = true))
                songArtistMap(songId).filter { it.artistId.startsWith("network_artist_") }.forEach(::delete)
                insert(SongArtistMap(songId = songId, artistId = id, position = 0))
            }
            if (albumName != null && albumArtist != null && albumId != null) {
                val oldAlbumId = song.albumId
                val existing = album(albumId).first()?.album
                if (existing == null) {
                    insert(AlbumEntity(id = albumId, title = albumName, year = tags.year,
                        thumbnailUrl = song.thumbnailUrl, songCount = 0, duration = 0,
                        isLocal = true, inLibrary = song.inLibrary ?: LocalDateTime.now()))
                }
                val artistId = artistId(albumArtist)
                insert(ArtistEntity(id = artistId, name = albumArtist, isLocal = true))
                insert(AlbumArtistMap(albumId = albumId, artistId = artistId, order = 0))
                songAlbumMaps(songId).filter { it.albumId != albumId && it.albumId.startsWith("network_album_") }.forEach(::delete)
                val trackIndex = ((tags.trackNumber ?: 1) - 1).coerceIn(0, AlbumDiscIndex.DISC_STRIDE - 1)
                upsert(SongAlbumMap(songId, albumId, AlbumDiscIndex.encode(tags.discNumber, trackIndex)))
                for (id in listOfNotNull(oldAlbumId, albumId).distinct().filter { it.startsWith("network_album_") }) {
                    val row = album(id).first()?.album ?: continue
                    val members = albumSongs(id).first()
                    update(row.copy(songCount = members.size,
                        duration = members.sumOf { it.song.duration.coerceAtLeast(0) },
                        inLibrary = if (members.isEmpty()) null else row.inLibrary ?: LocalDateTime.now()))
                }
            }
        }
    }

    private fun artistId(name: String) = "network_artist_${name.hashCode()}"
}
