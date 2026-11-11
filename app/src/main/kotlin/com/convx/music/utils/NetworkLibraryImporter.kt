/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import androidx.media3.common.util.UnstableApi
import androidx.room.withTransaction
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.storage.domain.NetworkLibraryRepository
import com.convx.storage.domain.NetworkTrack
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import timber.log.Timber

/**
 * Puts the audio found on an SMB or WebDAV source into the library as local songs.
 * A song's id is its `convx-network://` URI, which the player routes back through the
 * network data source, so nothing is copied to the phone.
 *
 * Two passes: the scan files every song straight away from its path (so the library fills
 * in seconds), then the tags embedded in each file replace the guesses from the filename.
 */
object NetworkLibraryImporter {

    data class Result(val added: Int, val skipped: Int, val truncated: Boolean, val tagged: Int = 0)

    /** Enough parallel header reads to keep a LAN busy without starving playback on the shared connection. */
    private const val TAG_READ_PARALLELISM = 3

    /** Songs written per database transaction. */
    private const val WRITE_CHUNK = 100
    private const val UNKNOWN_ARTIST = "Unknown Artist"

    @UnstableApi
    suspend fun import(
        database: MusicDatabase,
        repository: NetworkLibraryRepository,
        serverId: String,
        tagReader: NetworkTagReader? = null,
        artworkDir: File? = null,
        onProgress: (done: Int, total: Int) -> Unit = { _, _ -> },
    ): Result {
        val library = repository.scan(serverId)
        var added = 0
        var skipped = 0
        // One transaction per chunk, not per song: every commit makes each open library screen re-run its query,
        // and thousands of them in a row is what made a large share freeze or crash the app.
        library.tracks.chunked(WRITE_CHUNK).forEach { chunk ->
            database.withTransaction {
                chunk.forEach { track -> if (insert(database, track, refreshExisting = true)) added++ else skipped++ }
            }
        }
        var tagged = 0
        if (tagReader != null && library.tracks.isNotEmpty()) {
            val permits = Semaphore(TAG_READ_PARALLELISM)
            val lock = Any()
            val writer = Mutex()
            val pending = mutableListOf<Pair<NetworkTrack, NetworkTags>>()
            var done = 0

            suspend fun flush(batch: List<Pair<NetworkTrack, NetworkTags>>) {
                if (batch.isEmpty()) return
                writer.withLock {
                    database.withTransaction {
                        batch.forEach { (track, tags) -> if (applyTags(database, track, tags, artworkDir)) tagged++ }
                    }
                }
            }

            onProgress(0, library.tracks.size)
            coroutineScope {
                library.tracks.map { track ->
                    async {
                        val tags = permits.withPermit { tagReader.read(track.id) }
                        val ready = synchronized(lock) {
                            if (tags != null) pending += track to tags
                            done++
                            onProgress(done, library.tracks.size)
                            if (pending.size >= WRITE_CHUNK) pending.toList().also { pending.clear() } else null
                        }
                        if (ready != null) flush(ready)
                    }
                }.forEach { it.await() }
            }
            flush(synchronized(lock) { pending.toList().also { pending.clear() } })
        }
        database.withTransaction {
            pruneEmptyNetworkAlbums()
            pruneEmptyNetworkArtists()
        }
        return Result(added, skipped, library.truncated, tagged)
    }

    /**
     * Files [track] under the artist and album its path implies. With [refreshExisting] (a re-scan) a song that
     * is already in the library is re-filed, keeping its play counts, likes and library date, so entries made by
     * an older, less careful import get repaired. Otherwise an existing song is left alone. True when it was new.
     */
    suspend fun insert(database: MusicDatabase, track: NetworkTrack, refreshExisting: Boolean = false): Boolean {
        val existing = database.getSongById(track.id)?.song
        // Playing a file from the browser must not replace tag-derived details with a guess from its name.
        if (existing != null && !refreshExisting) return false
        return try {
            file(
                database = database,
                track = track,
                title = track.title,
                artistName = track.artist?.takeIf { it.isNotBlank() } ?: UNKNOWN_ARTIST,
                albumName = track.album.orEmpty(),
                albumKey = if (track.album.isNullOrBlank()) null else "folder|${track.folder}",
                trackNumber = 0,
                durationSeconds = null,
                thumbnail = track.artwork,
                existing = existing,
            )
            existing == null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.tag("NetworkImport").w(e, "Failed to insert ${track.title}")
            false
        }
    }

    /** Replaces the filename guesses with what the file's own tags say. True when something was updated. */
    suspend fun applyTags(database: MusicDatabase, track: NetworkTrack, tags: NetworkTags, artworkDir: File?): Boolean {
        return try {
            val artistName = tags.artist ?: tags.albumArtist ?: track.artist?.takeIf { it.isNotBlank() } ?: UNKNOWN_ARTIST
            val albumName = tags.album ?: track.album.orEmpty()
            // A tagged album is identified by who made it, so "Greatest Hits" by two artists stays two albums
            // and disc 1 / disc 2 folders of one album stay one. Untagged albums fall back to their folder.
            val taggedAlbum = tags.album
            val albumKey = when {
                taggedAlbum != null -> "tag|${(tags.albumArtist ?: artistName).lowercase()}|${taggedAlbum.lowercase()}"
                albumName.isNotBlank() -> "folder|${track.folder}"
                else -> null
            }
            val artworkBytes = tags.artwork
            val embedded = if (artworkBytes != null && artworkDir != null) {
                saveArtwork(artworkDir, albumKey ?: track.id, artworkBytes)
            } else {
                null
            }
            file(
                database = database,
                track = track,
                title = tags.title ?: track.title,
                artistName = artistName,
                albumName = albumName,
                albumKey = albumKey,
                trackNumber = tags.trackNumber ?: 0,
                durationSeconds = tags.durationSeconds,
                thumbnail = embedded ?: track.artwork,
                existing = database.getSongById(track.id)?.song,
            )
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.tag("NetworkImport").w(e, "Failed to apply tags to ${track.title}")
            false
        }
    }

    private suspend fun file(
        database: MusicDatabase,
        track: NetworkTrack,
        title: String,
        artistName: String,
        albumName: String,
        albumKey: String?,
        trackNumber: Int,
        durationSeconds: Int?,
        thumbnail: String?,
        existing: SongEntity?,
    ) {
        val artistId = "network_artist_" + shortHash(artistName.lowercase())
        val albumId = albumKey?.let { "network_album_" + shortHash(serverOf(track.id) + "|" + it) }
        val now = LocalDateTime.now()
        database.withTransaction {
            val base = existing ?: SongEntity(id = track.id, title = title, isLocal = true, inLibrary = now)
            upsert(
                base.copy(
                    title = title,
                    thumbnailUrl = thumbnail ?: existing?.thumbnailUrl,
                    albumId = albumId,
                    albumName = albumName.takeIf { it.isNotBlank() },
                    duration = durationSeconds ?: existing?.duration ?: -1,
                    isLocal = true,
                ),
            )
            insert(ArtistEntity(id = artistId, name = artistName, isLocal = true))
            clearSongArtists(track.id)
            insert(SongArtistMap(songId = track.id, artistId = artistId, position = 0))
            clearSongAlbums(track.id)
            if (albumId != null && albumName.isNotBlank()) {
                val created = insert(
                    AlbumEntity(
                        id = albumId,
                        title = albumName,
                        thumbnailUrl = thumbnail,
                        songCount = 1,
                        duration = 0,
                        isLocal = true,
                        inLibrary = now,
                    ),
                )
                // IGNORE keeps the first row, so a later track that found the real cover has to update it.
                if (created == -1L && thumbnail != null) setAlbumThumbnail(albumId, thumbnail)
                insert(SongAlbumMap(songId = track.id, albumId = albumId, index = trackNumber))
            }
        }
    }

    private fun serverOf(trackId: String): String = trackId.substringAfter("://").substringBefore('/')

    private fun saveArtwork(dir: File, key: String, bytes: ByteArray): String? = runCatching {
        dir.mkdirs()
        val file = File(dir, shortHash(key) + ".img")
        if (!file.exists() || file.length() != bytes.size.toLong()) file.writeBytes(bytes)
        file.toURI().toString()
    }.getOrNull()

    private fun shortHash(value: String): String =
        MessageDigest.getInstance("SHA-1").digest(value.toByteArray()).joinToString("") { "%02x".format(it) }.take(16)
}
