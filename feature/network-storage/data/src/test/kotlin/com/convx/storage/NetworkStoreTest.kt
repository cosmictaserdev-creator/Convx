package com.convx.storage

import com.convx.storage.data.StoredNetworkServerStore
import com.convx.storage.domain.*
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.test.*

internal class MemoryNetworkSnapshot : NetworkSnapshotStore {
    var value: String? = null
    var failWrite = false
    var writes = 0
    override suspend fun read() = value
    override suspend fun write(snapshot: String) {
        if (failWrite) throw StorageException(StorageError.CREDENTIALS_UNAVAILABLE)
        value = snapshot
        writes++
    }
}

class NetworkStoreTest {
    private val server = NetworkServer.Smb("nas", "NAS", "nas.example", 445, "Music", "Album", SourceCredentials("user", "secret", "domain"))

    @Test fun sourceCredentialsAndBaseFolderSurviveAReopen() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        StoredNetworkServerStore(snapshot).save(server)
        val restored = StoredNetworkServerStore(snapshot)
        assertEquals(listOf(server), restored.load())
        assertEquals(server, restored.find("nas"))
    }

    @Test fun corruptCredentialsCannotBeOverwrittenByAddingAnotherSource() = runTest {
        val snapshot = MemoryNetworkSnapshot().apply { value = "corrupt protected snapshot" }
        val store = StoredNetworkServerStore(snapshot)
        assertEquals(StorageError.CREDENTIALS_UNAVAILABLE, assertFailsWith<StorageException> { store.save(server) }.error)
        assertEquals("corrupt protected snapshot", snapshot.value)
        assertEquals(0, snapshot.writes)
        assertTrue(store.servers.value.isEmpty())
    }

    @Test fun failedWritesDoNotPublishChangedCredentialsOrRemoval() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        val store = StoredNetworkServerStore(snapshot)
        store.save(server)
        snapshot.failWrite = true
        assertFailsWith<StorageException> { store.save(server.copy(credentials = SourceCredentials("new", "changed"))) }
        assertEquals(server, store.find("nas"))
        assertFailsWith<StorageException> { store.delete("nas") }
        assertEquals(server, store.find("nas"))
    }

    @Test fun concurrentSourceEditsDoNotLoseTheOtherSavedSource() = runTest {
        val snapshot = MemoryNetworkSnapshot()
        val store = StoredNetworkServerStore(snapshot)
        val first = async { store.save(server) }
        val second = async { store.save(server.copy(id = "other", name = "Other")) }
        first.await(); second.await()
        assertEquals(setOf("nas", "other"), StoredNetworkServerStore(snapshot).load().map { it.id }.toSet())
    }
}
