package com.convx.storage

import com.convx.storage.domain.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class NetworkImportTest {
    private fun entry(path: String, size: Long = 10) = NetworkEntry(path, path.substringAfterLast('/'), false, size)

    @Test fun importedTracksWithoutArtUseAnOnDemandEmbeddedCover() = runTest {
        val track = NetworkMedia.track("nas", entry("Album/song.flac"), emptyList())
        val writer = RecordingWriter()
        NetworkLibraryImporter(FakeRepository(NetworkLibrary(listOf(track))), { FakeFile },
            { _, _ -> null }, { _, _ -> null }, writer).import("nas")
        val art = writer.songs.single().artworkUri!!
        assertEquals(track.id, NetworkEmbeddedArtworkUri.mediaId(art))
        assertEquals(art, writer.albums.single().artworkUri)
        assertNull(NetworkEmbeddedArtworkUri.mediaId("convx-network-art://nas/../secret"))
    }

    @Test fun rescanRefreshesArtistForExistingSongEvenWhenArtworkIsPresent() = runTest {
        val track = NetworkTrack("existing", "Old title", "Wrong artist", "Album", null, 50)
        val writer = RecordingWriter(mapOf(track.id to SongArtworkState.PRESENT))
        val importer = NetworkLibraryImporter(
            FakeRepository(NetworkLibrary(listOf(track))), { FakeFile },
            { _, _ -> NetworkTags(title = "Real title", artist = "Real artist") },
            { _, _ -> null }, writer,
        )

        importer.import("nas")

        assertEquals("Real artist", writer.metadata[track.id]?.artist)
        assertEquals("Real title", writer.metadata[track.id]?.title)
    }

    @Test fun rescanRefreshesMetadataEvenWhenNoArtworkCanBeFound() = runTest {
        val track = NetworkTrack("existing", "Old title", "Wrong artist", "Album", null, 50)
        val writer = RecordingWriter(mapOf(track.id to SongArtworkState.MISSING))
        NetworkLibraryImporter(
            FakeRepository(NetworkLibrary(listOf(track))), { FakeFile },
            { _, _ -> NetworkTags(artist = "Real artist") }, { _, _ -> null }, writer,
        ).import("nas")

        assertEquals("Real artist", writer.metadata[track.id]?.artist)
        assertTrue(writer.updated.isEmpty())
    }

    @Test fun discFoldersBecomeDiscsOfTheParentAlbum() {
        val track = NetworkMedia.track("nas", entry("Band/Album/CD2/05 - Song.flac"), emptyList())
        assertEquals("Album", track.album)
        assertEquals(2, track.discNumber)
        assertEquals(5, track.trackNumber)
        assertNull(track.artist)
    }

    @Test fun albumsWithTheSameNameByDifferentArtistsStayApart() {
        val a = NetworkTrack("a", "One", "A", "Greatest Hits", null, 1)
        val b = NetworkTrack("b", "One", "B", "Greatest Hits", null, 1)
        val (_, albums) = NetworkLibraryImporter.group(listOf(a, b)) { null }
        assertEquals(2, albums.map { it.id }.toSet().size)
    }

    @Test fun tracksAreOrderedByDiscThenTrackAndCoverIsShared() {
        val tracks = listOf(
            NetworkTrack("d2t1", "Z", "A", "Alb", "art://2", 1, discNumber = 2, trackNumber = 1),
            NetworkTrack("d1t2", "Y", "A", "Alb", null, 1, discNumber = 1, trackNumber = 2),
            NetworkTrack("d1t1", "X", "A", "Alb", null, 1, discNumber = 1, trackNumber = 1),
        )
        val (songs, albums) = NetworkLibraryImporter.group(tracks) { it.artwork }
        assertEquals(listOf("d1t1", "d1t2", "d2t1"), songs.sortedBy { it.index }.map { it.track.id })
        assertTrue(songs.all { it.artworkUri == "art://2" })
        assertEquals(3, albums.single().songCount)
    }

    @Test fun importReadsTagsOnceAndEmbeddedPictureBeatsFolderArt() = runTest {
        val file = entry("Alb/song.mp3", 50)
        val repository = FakeRepository(NetworkLibrary(listOf(NetworkMedia.track("nas", file, emptyList()))))
        val writer = RecordingWriter()
        val cached = mutableMapOf<String, ByteArray>()
        val importer = NetworkLibraryImporter(
            repository,
            { FakeFile },
            { _, _ -> NetworkTags(title = "Real", artist = "Band", album = "Real Album", trackNumber = 3, picture = byteArrayOf(1)) },
            { key, bytes -> cached[key] = bytes; "file://$key" },
            writer,
        )
        val result = importer.import("nas")
        assertEquals(1, result.added)
        val song = writer.songs.single()
        assertEquals("Real", song.track.title)
        assertEquals("Real Album", writer.albums.single().title)
        assertEquals(3, song.track.trackNumber)
        assertTrue(song.artworkUri!!.startsWith("file://"))
        assertContentEquals(byteArrayOf(1), cached.values.single())
    }

    @Test fun songsAlreadyHoldingArtAreSkippedAndMissingArtIsRefreshed() = runTest {
        val library = NetworkLibrary(listOf(
            NetworkTrack("have", "H", null, null, null, 5),
            NetworkTrack("lack", "L", null, null, null, 5),
        ))
        val writer = RecordingWriter(mapOf("have" to SongArtworkState.PRESENT, "lack" to SongArtworkState.MISSING))
        val importer = NetworkLibraryImporter(
            FakeRepository(library), { FakeFile }, { _, _ -> NetworkTags(picture = byteArrayOf(9)) }, { key, _ -> "file://$key" }, writer,
        )
        val result = importer.import("nas")
        assertEquals(NetworkImportResult(added = 0, skipped = 1, refreshed = 1, truncated = false), result)
        assertEquals(listOf("lack"), writer.updated)
    }

    @Test fun embeddedArtworkFromAnyTrackBeatsFolderArtwork() = runTest {
        val tracks = listOf(
            NetworkTrack("first", "First", "Band", "Album", "convx-network://nas/cover.jpg", 1),
            NetworkTrack("second", "Second", "Band", "Album", "convx-network://nas/cover.jpg", 1),
        )
        var tagReads = 0
        var artworkReads = 0
        val repository = object : NetworkLibraryRepository by FakeRepository(NetworkLibrary(tracks)) {
            override suspend fun readArtwork(uri: String, maxBytes: Int): ByteArray {
                artworkReads++
                return byteArrayOf(2)
            }
        }
        val writer = RecordingWriter()
        var cached: ByteArray? = null
        val importer = NetworkLibraryImporter(repository, { FakeFile }, { _, _ ->
            if (tagReads++ == 0) null else NetworkTags(picture = byteArrayOf(1))
        }, { _, bytes -> cached = bytes; "file://cover" }, writer, parallelism = 2)

        importer.import("nas")

        assertContentEquals(byteArrayOf(1), cached)
        assertEquals(0, artworkReads)
        assertTrue(writer.songs.all { it.artworkUri == "file://cover" })
    }

    @Test fun largeImportsAreWrittenInBatchesThatKeepEveryAlbumWhole() = runTest {
        val tracks = (1..700).map { NetworkTrack("id$it", "T$it", "A", "Album${it / 10}", null, 1, trackNumber = it % 10) }
        val writer = RecordingWriter()
        val importer = NetworkLibraryImporter(
            FakeRepository(NetworkLibrary(tracks)), { FakeFile }, { _, _ -> null }, { _, _ -> null }, writer,
        )
        assertEquals(700, importer.import("nas").added)
        assertTrue(writer.calls.size > 1)
        writer.calls.forEach { (songs, albums) ->
            assertEquals(songs.mapNotNull { it.albumId }.toSet(), albums.map { it.id }.toSet())
        }
        assertEquals(700, writer.calls.sumOf { it.first.size })
    }

    @Test fun aFolderWithoutArtIsNotRetriedForEveryTrack() = runTest {
        var reads = 0
        val tracks = (1..20).map { NetworkTrack("id$it", "T$it", "A", "Bare", "convx-network://nas/cover.jpg", 1) }
        val repository = object : NetworkLibraryRepository by FakeRepository(NetworkLibrary(tracks)) {
            override suspend fun readArtwork(uri: String, maxBytes: Int): ByteArray { reads++; error("unreadable") }
        }
        NetworkLibraryImporter(repository, { FakeFile }, { _, _ -> null }, { _, _ -> null }, RecordingWriter(), parallelism = 1).import("nas")
        assertEquals(3, reads)
    }

    private object FakeFile : RemoteFile {
        override val length = 1L
        override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int) = -1
        override fun close() {}
    }

    private class FakeRepository(private val library: NetworkLibrary) : NetworkLibraryRepository {
        override val servers get() = throw UnsupportedOperationException()
        override suspend fun loadServers() = emptyList<NetworkServer>()
        override suspend fun save(server: NetworkServer) {}
        override suspend fun delete(id: String) {}
        override suspend fun test(server: NetworkServer) {}
        override suspend fun browse(id: String, folder: String) = emptyList<NetworkEntry>()
        override suspend fun scan(id: String, maxTracks: Int) = library
        override suspend fun readArtwork(uri: String, maxBytes: Int) = byteArrayOf(7)
    }

    private class RecordingWriter(private val states: Map<String, SongArtworkState> = emptyMap()) : NetworkLibraryWriter {
        override fun embeddedArtwork(songId: String): String? = NetworkEmbeddedArtworkUri.create(songId)
        val songs = mutableListOf<ImportedSong>()
        val albums = mutableListOf<ImportedAlbum>()
        val updated = mutableListOf<String>()
        val metadata = mutableMapOf<String, NetworkTags>()
        val calls = mutableListOf<Pair<List<ImportedSong>, List<ImportedAlbum>>>()
        override suspend fun artworkState(songId: String) = states[songId] ?: SongArtworkState.NEW
        override suspend fun write(songs: List<ImportedSong>, albums: List<ImportedAlbum>) {
            calls += songs to albums
            this.songs += songs
            this.albums += albums
        }
        override suspend fun updateArtwork(songId: String, artworkUri: String) { updated += songId }
        override suspend fun updateMetadata(songId: String, tags: NetworkTags) { metadata[songId] = tags }
    }
}
