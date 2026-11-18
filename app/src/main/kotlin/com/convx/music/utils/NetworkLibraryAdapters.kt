/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.convx.music.catalog.AlbumDiscIndex
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumArtistMap
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.storage.domain.ArtworkCache
import com.convx.storage.domain.ImportedAlbum
import com.convx.storage.domain.ImportedSong
import com.convx.storage.domain.NetworkEmbeddedArtworkUri
import com.convx.storage.domain.NetworkLibraryWriter
import com.convx.storage.domain.NetworkMediaUri
import com.convx.storage.domain.NetworkTags
import com.convx.storage.domain.SongArtworkState
import com.convx.storage.domain.networkAlbumId
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/** Keeps cover art in app storage (not cache) so a cleared cache never leaves songs pointing at missing files. */
class AndroidArtworkCache(context: Context) : ArtworkCache {
    private val directory = File(context.filesDir, "network-art")

    override suspend fun put(key: String, bytes: ByteArray): String? = withContext(Dispatchers.IO) {
        if (bytes.isEmpty() || bytes.size > MaxInputBytes) return@withContext null
        val file = File(directory, "${key.hashCode().toUInt().toString(16)}_${key.length}.jpg")
        try {
            if (file.length() == 0L) {
                directory.mkdirs()
                val partial = File.createTempFile(file.name, ".tmp", directory)
                try {
                    val bitmap = decodeScaled(bytes) ?: return@withContext null
                    val written = try {
                        partial.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                    } finally {
                        bitmap.recycle()
                    }
                    coroutineContext.ensureActive()
                    if (!written || (!partial.renameTo(file) && file.length() == 0L)) return@withContext null
                } finally {
                    partial.delete()
                }
            }
            Uri.fromFile(file).toString()
        } catch (_: IOException) {
            null
        }
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
        const val MaxInputBytes = 16 * 1024 * 1024
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
            val fresh = songs.filter { getSongById(it.track.id) == null }
            val byAlbum = fresh.groupBy(ImportedSong::albumId)
            fresh.forEach { imported ->
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
                val members = byAlbum[entry.id].orEmpty()
                if (members.isEmpty()) return@forEach
                val duration = members.sumOf { it.track.durationSeconds.coerceAtLeast(0) }
                val existing = album(entry.id).first()?.album
                val offset = existing?.songCount ?: 0
                if (existing == null) {
                    insert(
                        AlbumEntity(
                            id = entry.id,
                            title = entry.title,
                            year = entry.year,
                            thumbnailUrl = entry.artworkUri,
                            songCount = members.size,
                            duration = duration,
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
                            songCount = existing.songCount + members.size,
                            duration = existing.duration + duration,
                            thumbnailUrl = existing.thumbnailUrl ?: entry.artworkUri,
                        ),
                    )
                }
                members.forEach { member ->
                    val position = member.track.trackNumber?.minus(1) ?: (offset + member.index)
                    insert(SongAlbumMap(member.track.id, entry.id, albumIndex(member.track.discNumber, position)))
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
            val albumName = tags.album?.takeIf { it.isNotBlank() } ?: song.albumName
            val albumArtist = tags.albumArtist?.takeIf { it.isNotBlank() } ?: artist
            val albumId = if (albumName != null && albumArtist != null) networkAlbumId(albumName, albumArtist) else song.albumId
            update(song.copy(
                title = title ?: song.title,
                duration = tags.durationSeconds?.takeIf { it > 0 } ?: song.duration,
                year = tags.year ?: song.year,
                albumId = albumId,
                albumName = albumName,
            ))
            if (artist != null) {
                val id = artistId(artist)
                insert(ArtistEntity(id = id, name = artist, isLocal = true))
                songArtistMap(songId).filter { it.artistId.startsWith("network_artist_") }.forEach(::delete)
                insert(SongArtistMap(songId = songId, artistId = id, position = 0))
            }
            val mappings = songAlbumMaps(songId)
            val previous = mappings.firstOrNull { it.albumId == song.albumId }
            if (albumName != null && albumArtist != null && albumId != null) {
                val existing = album(albumId).first()?.album
                if (existing == null) {
                    insert(AlbumEntity(id = albumId, title = albumName, year = tags.year,
                        thumbnailUrl = song.thumbnailUrl, songCount = 0, duration = 0,
                        isLocal = true, inLibrary = song.inLibrary ?: LocalDateTime.now()))
                }
                val artistId = artistId(albumArtist)
                insert(ArtistEntity(id = artistId, name = albumArtist, isLocal = true))
                insert(AlbumArtistMap(albumId = albumId, artistId = artistId, order = 0))
                mappings.filter { it.albumId != albumId && it.albumId.startsWith("network_album_") }.forEach(::delete)
            }
            if (albumId != null && albumId.startsWith("network_album_")) {
                val disc = tags.discNumber ?: previous?.let { AlbumDiscIndex.discOf(it.index) }
                val position = tags.trackNumber?.minus(1) ?: previous?.index?.rem(AlbumDiscIndex.DISC_STRIDE) ?: 0
                upsert(SongAlbumMap(songId, albumId, albumIndex(disc, position)))
            }
            if (song.albumId != albumId || tags.durationSeconds != null) {
                for (id in listOfNotNull(song.albumId, albumId).distinct().filter { it.startsWith("network_album_") }) {
                    val row = album(id).first()?.album ?: continue
                    val members = albumSongs(id).first()
                    update(row.copy(songCount = members.size,
                        duration = members.sumOf { it.song.duration.coerceAtLeast(0) },
                        inLibrary = if (members.isEmpty()) null else row.inLibrary ?: LocalDateTime.now()))
                }
            }
        }
    }

    private fun albumIndex(disc: Int?, position: Int): Int = AlbumDiscIndex.encode(
        disc?.coerceIn(1, Int.MAX_VALUE / AlbumDiscIndex.DISC_STRIDE),
        position.coerceIn(0, AlbumDiscIndex.DISC_STRIDE - 1),
    )

    private fun artistId(name: String) = "network_artist_${name.hashCode()}"
}
