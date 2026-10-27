package com.convx.storage.presentation

import com.convx.music.playback.*
import com.convx.storage.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.*

internal open class TestNetworkLibrary : NetworkLibraryRepository {
    val server = NetworkServer.Smb("nas", "NAS", "nas.example", 445, "Music", "")
    override val servers = MutableStateFlow<List<NetworkServer>>(listOf(server))
    override suspend fun loadServers() = servers.value
    override suspend fun save(server: NetworkServer) { servers.value = listOf(server) }
    override suspend fun delete(id: String) { servers.value = servers.value.filterNot { it.id == id } }
    override suspend fun test(server: NetworkServer) {}
    override suspend fun browse(id: String, folder: String) = emptyList<NetworkEntry>()
    override suspend fun scan(id: String, maxTracks: Int) = NetworkLibrary(emptyList())
    override suspend fun readArtwork(uri: String, maxBytes: Int) = byteArrayOf()
}

class NetworkPlaybackTest {
    private val library = TestNetworkLibrary()
    private var fallbacks = 0
    private var endpoints = 0
    private val fallback = object : AudioStreamResolver {
        override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
            fallbacks++
            return Result.success(PlaybackAudioStream("https://original.example/audio", "audio/mpeg", null, 0))
        }
    }
    private val resolver = NetworkAudioStreamResolver(library, NetworkPlaybackEndpoint { _, _ ->
        endpoints++; "http://127.0.0.1:1234/network/token"
    }, fallback)
    private val track = NetworkTrack(NetworkMediaUri.create("nas", "Album/song.flac"), "Song", "Artist", "Album", null, 100)

    @Test fun networkTracksResolveFreshEndpointsAndKeepLogicalIdsInTheQueue() = runTest {
        val request = track.playbackRequest()
        val stream = resolver.resolve(request).getOrThrow()
        assertEquals("audio/flac", stream.mimeType)
        assertEquals("http://127.0.0.1:1234/network/token", stream.url)
        assertEquals(track.id, request.id)
        assertEquals(NETWORK_MUSIC_PROVIDER, request.providerId)
        resolver.resolve(request).getOrThrow()
        assertEquals(2, endpoints)
        assertEquals(0, fallbacks)
    }

    @Test fun removedOrMalformedSourcesCannotFallBackToAYouTubeTrack() = runTest {
        library.servers.value = emptyList()
        assertEquals(StorageError.NOT_FOUND, (resolver.resolve(track.playbackRequest()).exceptionOrNull() as StorageException).error)
        val malformed = track.playbackRequest().copy(id = "video-id")
        assertEquals(StorageError.INVALID_CONFIGURATION, (resolver.resolve(malformed).exceptionOrNull() as StorageException).error)
        assertEquals(0, fallbacks)
        assertEquals(0, endpoints)
    }

    @Test fun ordinaryCatalogTracksStillReachTheirOriginalResolver() = runTest {
        resolver.resolve(PlaybackRequestTrack("youtube_music", "video", "Title", listOf("Artist"))).getOrThrow()
        assertEquals(1, fallbacks)
    }

    @Test fun cancellationIsNotConvertedIntoAPlaybackFailureResult() = runTest {
        val cancelled = NetworkAudioStreamResolver(library, NetworkPlaybackEndpoint { _, _ -> throw CancellationException() }, fallback)
        assertFailsWith<CancellationException> { cancelled.resolve(track.playbackRequest()) }
    }
}
