package com.convx.storage

import com.convx.storage.data.*
import com.convx.storage.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class NetworkRepositoryTest {
    private val server = NetworkServer.Smb("nas", "NAS", "nas.example", 445, "Music", "")

    @Test fun artworkReadIsBoundedEvenWhenTheRemoteLengthIsUnknownAndAlwaysCloses() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        val store = StoredNetworkServerStore(snapshot).apply { save(server) }
        val source = Source()
        val repository = RemoteNetworkLibraryRepository(store, source, source, StandardTestDispatcher(testScheduler))
        val uri = NetworkMediaUri.create("nas", "cover.jpg")
        assertEquals(StorageError.LIMIT_REACHED,
            assertFailsWith<StorageException> { repository.readArtwork(uri, 4) }.error)
        assertEquals(1, source.closedFiles)
        assertContentEquals("0123456789".encodeToByteArray(), repository.readArtwork(uri, 10))
        assertEquals(2, source.closedFiles)
    }

    @Test fun savingInvalidatesReadersOnlyAfterTheProtectedWriteSucceeds() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        val store = StoredNetworkServerStore(snapshot)
        val dav = Source()
        val smb = Source()
        val repository = RemoteNetworkLibraryRepository(store, dav, smb, StandardTestDispatcher(testScheduler))
        repository.save(server)
        assertEquals(listOf("nas"), smb.invalidated)
        snapshot.failWrite = true
        assertFailsWith<StorageException> { repository.delete("nas") }
        assertEquals(listOf("nas"), smb.invalidated)
        assertEquals(server, store.find("nas"))
    }

    @Test fun artworkCancellationClosesItsRemoteHandleAndPropagates() = runTest {
        val store = StoredNetworkServerStore(MemoryNetworkSnapshot()).apply { save(server) }
        val source = Source().apply { cancelled = true }
        val repository = RemoteNetworkLibraryRepository(store, source, source, StandardTestDispatcher(testScheduler))
        assertFailsWith<CancellationException> { repository.readArtwork(NetworkMediaUri.create("nas", "cover.jpg"), 100) }
        assertEquals(1, source.closedFiles)
    }

    @Test fun browsingRestoresSourcesBeforeFindingTheSelectedServer() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        StoredNetworkServerStore(snapshot).save(server)
        val source = Source()
        val repository = RemoteNetworkLibraryRepository(StoredNetworkServerStore(snapshot), source, source)
        assertEquals("Album", repository.browse("nas", "Album").single().path)
        assertEquals(StorageError.NOT_FOUND, assertFailsWith<StorageException> { repository.browse("deleted", "") }.error)
    }

    private class Source : NetworkFileSource {
        var closedFiles = 0
        var cancelled = false
        val invalidated = mutableListOf<String>()
        override suspend fun list(server: NetworkServer, folder: String) = listOf(NetworkEntry(folder, "Folder", true))
        override fun open(server: NetworkServer, path: String) = object : RemoteFile {
            override val length = -1L
            val data = "0123456789".encodeToByteArray()
            override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
                if (cancelled) throw CancellationException("Cancelled")
                if (position >= data.size) return -1
                val count = minOf(length, data.size - position.toInt())
                data.copyInto(buffer, offset, position.toInt(), position.toInt() + count)
                return count
            }
            override fun close() { closedFiles++ }
        }
        override fun invalidate(id: String) { invalidated += id }
        override fun close() {}
    }
}
