package com.convx.storage.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/** Tags read from the file itself; every field is null when the file has none. */
class NetworkTags(
    val title: String? = null,
    val artist: String? = null,
    val albumArtist: String? = null,
    val album: String? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val durationSeconds: Int? = null,
    val picture: ByteArray? = null,
)

fun interface NetworkTagReader {
    /** [size] comes from the listing: a WebDAV file only learns its length after the first read. */
    suspend fun read(file: RemoteFile, size: Long): NetworkTags?
}

fun interface ArtworkCache {
    /** Stores [bytes] under [key] and returns a URI the image loader can open, or null if it could not. */
    suspend fun put(key: String, bytes: ByteArray): String?
}

enum class SongArtworkState { NEW, MISSING, PRESENT }

data class ImportedSong(val track: NetworkTrack, val albumId: String?, val index: Int, val artworkUri: String?)

data class ImportedAlbum(
    val id: String,
    val title: String,
    val artist: String?,
    val year: Int?,
    val songCount: Int,
    val durationSeconds: Int,
    val artworkUri: String?,
)

interface NetworkLibraryWriter {
    suspend fun artworkState(songId: String): SongArtworkState
    suspend fun write(songs: List<ImportedSong>, albums: List<ImportedAlbum>)
    suspend fun updateArtwork(songId: String, artworkUri: String)
    suspend fun updateMetadata(songId: String, tags: NetworkTags) {}
    fun embeddedArtwork(songId: String): String? = null
}

data class NetworkImportResult(val added: Int, val skipped: Int, val refreshed: Int, val truncated: Boolean)

/** Same import on every client: only the tag reader, artwork cache and writer are platform code. */
class NetworkLibraryImporter(
    private val repository: NetworkLibraryRepository,
    private val access: NetworkMediaAccess,
    private val tags: NetworkTagReader,
    private val cache: ArtworkCache,
    private val writer: NetworkLibraryWriter,
    private val parallelism: Int = 4,
) {
    suspend fun import(serverId: String, onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }): NetworkImportResult {
        val library = repository.scan(serverId)
        val states = library.tracks.associateWith { writer.artworkState(it.id) }
        val work = states.filterValues { it != SongArtworkState.PRESENT }.keys.toList()
        val lock = Mutex()
        val embeddedCovers = HashMap<String, String>()
        val gate = Semaphore(parallelism)
        var done = 0
        // Existing entries with artwork may still contain old filename-derived metadata.
        coroutineScope {
            states.filterValues { it == SongArtworkState.PRESENT }.keys.map { track ->
                async {
                    gate.withPermit {
                        readTags(track)?.let { writer.updateMetadata(track.id, it) }
                        lock.withLock { onProgress(++done, library.tracks.size) }
                    }
                }
            }.awaitAll()
        }
        val enriched = coroutineScope {
            work.map { track ->
                async {
                    gate.withPermit {
                        val read = readTags(track)
                        if (read != null && states[track] == SongArtworkState.MISSING) {
                            writer.updateMetadata(track.id, read)
                        }
                        val merged = track.withTags(read)
                        lock.withLock {
                            val key = coverKey(merged)
                            if (key !in embeddedCovers && read?.picture != null) {
                                cache.put(key, read.picture)?.let { embeddedCovers[key] = it }
                            }
                            onProgress(++done, library.tracks.size)
                        }
                        merged
                    }
                }
            }.awaitAll()
        }
        val covers = coroutineScope { enriched.groupBy(::coverKey).map { (key, members) ->
            async {
                gate.withPermit {
                    val embeddedUri = embeddedCovers[key]
                    val folderUri = if (embeddedUri == null) {
                        members.asSequence().filter { it.artwork != null }
                            .take(MaxCoverMisses).firstNotNullOfOrNull { coverFor(key, it) }
                    } else null
                    key to (embeddedUri ?: folderUri)
                }
            }
        }.awaitAll().toMap() }
        val tagged = enriched.zip(work).associate { (merged, original) -> original.id to merged }
        val fresh = work.filter { states[it] == SongArtworkState.NEW }.map { tagged.getValue(it.id) }
        val (songs, albums) = group(fresh) { covers[coverKey(it)] ?: it.artwork }
        val songsWithFallback = songs.map { it.copy(artworkUri = it.artworkUri ?: writer.embeddedArtwork(it.track.id)) }
        val firstArtByAlbum = songsWithFallback.filter { it.albumId != null }.groupBy { it.albumId }
            .mapValues { (_, members) -> members.firstNotNullOfOrNull { it.artworkUri } }
        val albumsWithFallback = albums.map { album ->
            album.copy(artworkUri = album.artworkUri ?: firstArtByAlbum[album.id])
        }
        writeInBatches(songsWithFallback, albumsWithFallback)
        var refreshed = 0
        work.filter { states[it] == SongArtworkState.MISSING }.forEach { original ->
            val merged = tagged.getValue(original.id)
            val uri = covers[coverKey(merged)] ?: merged.artwork ?: writer.embeddedArtwork(original.id) ?: return@forEach
            writer.updateArtwork(original.id, uri)
            refreshed++
        }
        return NetworkImportResult(fresh.size, library.tracks.size - work.size, refreshed, library.truncated)
    }

    /** One transaction per few hundred songs; an album's songs and its row always travel together. */
    private suspend fun writeInBatches(songs: List<ImportedSong>, albums: List<ImportedAlbum>) {
        val songsByAlbum = songs.groupBy { it.albumId }
        val batch = ArrayList<ImportedSong>()
        val batchAlbums = ArrayList<ImportedAlbum>()
        suspend fun flush() {
            if (batch.isEmpty()) return
            writer.write(batch.toList(), batchAlbums.toList())
            batch.clear()
            batchAlbums.clear()
        }
        for (album in albums) {
            songsByAlbum[album.id]?.let { batch += it }
            batchAlbums += album
            if (batch.size >= BatchSize) flush()
        }
        for (single in songsByAlbum[null].orEmpty()) {
            batch += single
            if (batch.size >= BatchSize) flush()
        }
        flush()
    }

    private suspend fun readTags(track: NetworkTrack): NetworkTags? = try {
        access.open(track.id).use { tags.read(it, track.size) }
    } catch (error: kotlinx.coroutines.CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }

    /** Embedded picture first, the folder's cover image otherwise. */
    private suspend fun coverFor(folder: String, track: NetworkTrack): String? {
        val bytes = track.artwork?.let {
            try { repository.readArtwork(it) } catch (error: kotlinx.coroutines.CancellationException) { throw error } catch (_: Exception) { null }
        }
        return bytes?.let { cache.put(folder, it) }
    }

    private fun coverKey(track: NetworkTrack): String =
        albumKey(track) ?: NetworkMediaUri.parse(track.id)?.let { "${it.serverId}/${RemotePath.parent(it.path)}" } ?: track.id

    internal companion object {
        const val BatchSize = 300
        const val MaxCoverMisses = 3

        fun albumKey(track: NetworkTrack): String? {
            val album = track.album?.takeIf(String::isNotBlank) ?: return null
            val artist = track.albumArtist ?: track.artist.orEmpty()
            return "${artist.lowercase()}|${album.lowercase()}"
        }

        fun albumId(key: String): String = "network_album_${key.hashCode().toUInt().toString(16)}_${key.length}"

        fun group(tracks: List<NetworkTrack>, artwork: (NetworkTrack) -> String?): Pair<List<ImportedSong>, List<ImportedAlbum>> {
            val byAlbum = tracks.groupBy { albumKey(it) }
            val songs = ArrayList<ImportedSong>(tracks.size)
            val albums = ArrayList<ImportedAlbum>()
            for ((key, members) in byAlbum) {
                if (key == null) {
                    members.forEach { songs += ImportedSong(it, null, 0, artwork(it)) }
                    continue
                }
                val id = albumId(key)
                val ordered = members.sortedWith(
                    compareBy<NetworkTrack>({ it.discNumber ?: 1 }, { it.trackNumber ?: Int.MAX_VALUE }, { it.title.lowercase() }),
                )
                val cover = ordered.firstNotNullOfOrNull(artwork)
                ordered.forEachIndexed { index, track -> songs += ImportedSong(track, id, index, cover) }
                val first = ordered.first()
                albums += ImportedAlbum(
                    id = id,
                    title = first.album.orEmpty(),
                    artist = first.albumArtist ?: first.artist,
                    year = ordered.firstNotNullOfOrNull { it.year },
                    songCount = ordered.size,
                    durationSeconds = ordered.sumOf { it.durationSeconds.coerceAtLeast(0) },
                    artworkUri = cover,
                )
            }
            return songs to albums
        }
    }
}

/** Stable album identity shared by imports and Android metadata refresh. */
fun networkAlbumId(album: String, artist: String): String {
    val key = "${artist.lowercase()}|${album.lowercase()}"
    return "network_album_${key.hashCode().toUInt().toString(16)}_${key.length}"
}

/** Adds files picked in the folder browser: filename metadata only, no network reads. */
suspend fun NetworkLibraryWriter.addTracks(tracks: List<NetworkTrack>) {
    val fresh = tracks.filter { artworkState(it.id) == SongArtworkState.NEW }
    val (songs, albums) = NetworkLibraryImporter.group(fresh) { it.artwork ?: embeddedArtwork(it.id) }
    if (songs.isNotEmpty()) write(songs, albums)
}

internal fun NetworkTrack.withTags(tags: NetworkTags?): NetworkTrack =
    if (tags == null) this else copy(
        title = tags.title ?: title,
        artist = tags.artist ?: artist,
        album = tags.album ?: album,
        albumArtist = tags.albumArtist ?: albumArtist,
        trackNumber = tags.trackNumber ?: trackNumber,
        discNumber = tags.discNumber ?: discNumber,
        year = tags.year ?: year,
        durationSeconds = tags.durationSeconds ?: durationSeconds,
    )
