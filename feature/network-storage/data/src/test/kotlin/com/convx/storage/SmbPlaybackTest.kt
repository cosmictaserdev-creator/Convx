package com.convx.storage

import com.convx.storage.data.SmbFileSession
import com.convx.storage.data.SmbFileSource
import com.convx.storage.domain.NetworkEntry
import com.convx.storage.domain.NetworkServer
import com.convx.storage.domain.RemoteFile
import com.convx.storage.domain.SourceCredentials
import com.convx.storage.domain.StorageError
import com.convx.storage.domain.StorageException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class SmbPlaybackTest {
    private val server = NetworkServer.Smb("nas", "NAS", "nas.example", 445, "Music", "Library")

    @Test fun reconnectsAStalePooledSessionBeforeRetryingASeekOpen() {
        val first = FakeSession()
        val second = FakeSession()
        var connections = 0
        SmbFileSource { if (connections++ == 0) first else second }.use { source ->
            source.open(server, "Album/song.flac").close()
            first.openFailure = IOException("Connection dropped while TCP still appears connected")
            source.open(server, "Album/song.flac").use { file ->
                val bytes = ByteArray(3)
                assertEquals(3, file.read(7, bytes, 0, bytes.size))
                assertEquals("789", bytes.toString(Charsets.UTF_8))
            }
            assertEquals(2, connections)
            assertTrue(first.closed)
        }
    }

    @Test fun reconnectsForBrowsingAndRetainsTheConfiguredBaseFolder() = runBlocking {
        var connections = 0
        val first = FakeSession().apply { listFailure = IOException("Stale tree connection") }
        val second = FakeSession()
        SmbFileSource { if (connections++ == 0) first else second }.use { source ->
            val entries = source.list(server, "Album")
            assertEquals("Album/song.flac", entries.single().path)
            assertEquals(listOf("Library/Album"), second.listed)
            assertEquals(2, connections)
            assertTrue(first.closed)
        }
    }

    @Test fun authenticationAndConfigurationFailuresDoNotRepeatTheLogin() {
        var connections = 0
        val denied = StorageException(StorageError.AUTHENTICATION)
        val session = FakeSession().apply { openFailure = denied }
        SmbFileSource { connections++; session }.use { source ->
            try {
                source.open(server, "song.flac")
                fail("Expected authentication failure")
            } catch (failure: StorageException) {
                assertSame(denied, failure)
            }
            assertEquals(1, connections)
        }
    }

    @Test fun stopsAfterOneReconnectAndDiscardsBothFailedSessions() {
        val connections = mutableListOf<FakeSession>()
        SmbFileSource {
            FakeSession().apply { openFailure = IOException("Disconnected"); connections += this }
        }.use { source ->
            try { source.open(server, "song.flac"); fail("Expected disconnected transport") }
            catch (_: IOException) { }
            assertEquals(2, connections.size)
            assertTrue(connections.all { it.closed })
        }
    }

    @Test fun independentReadersUseAbsoluteOffsetsAndCloseTheirOwnHandles() {
        val session = FakeSession()
        SmbFileSource { session }.use { source ->
            val first = source.open(server, "Album/song.flac")
            source.open(server, "Album/song.flac").use { second ->
                val a = ByteArray(2)
                val b = ByteArray(2)
                first.use {
                    assertEquals(2, it.read(2, a, 0, 2))
                    assertEquals("23", a.toString(Charsets.UTF_8))
                    assertEquals(2, second.read(7, b, 0, 2))
                    assertEquals("78", b.toString(Charsets.UTF_8))
                }
                assertFalse(session.closed)
                assertEquals(1, second.read(9, b, 0, 2))
                assertEquals(-1, second.read(10, b, 0, 2))
            }
            assertEquals(listOf("Library/Album/song.flac", "Library/Album/song.flac"), session.opened)
        }
    }

    @Test fun changedCredentialsReplaceOnlyThatServersConnection() {
        val connections = mutableListOf<Pair<NetworkServer.Smb, FakeSession>>()
        SmbFileSource { settings -> FakeSession().also { connections += settings to it } }.use { source ->
            source.open(server, "song.flac").close()
            val other = server.copy(id = "other", credentials = SourceCredentials("other-user", "other-password"))
            source.open(other, "song.flac").close()
            val changed = server.copy(credentials = SourceCredentials("user", "changed-password", "DOMAIN"))
            source.open(changed, "song.flac").close()
            assertEquals(3, connections.size)
            assertTrue(connections[0].second.closed)
            assertFalse(connections[1].second.closed)
            assertEquals(changed.credentials, connections[2].first.credentials)
        }
    }

    @Test fun anOldReadersFailureCannotInvalidateANewerConnection() {
        val first = FakeSession()
        val second = FakeSession()
        var connections = 0
        SmbFileSource { if (connections++ == 0) first else second }.use { source ->
            val oldReader = source.open(server, "song.flac")
            source.invalidate(server.id)
            source.open(server, "song.flac").close()
            try { oldReader.read(0, ByteArray(1), 0, 1); fail("The old connection is closed") }
            catch (_: IllegalStateException) { }
            finally { oldReader.close() }
            source.open(server, "song.flac").close()
            assertEquals(2, connections)
            assertFalse(second.closed)
        }
    }

    private class FakeSession : SmbFileSession {
        var closed = false
        var openFailure: Exception? = null
        var listFailure: Exception? = null
        val opened = mutableListOf<String>()
        val listed = mutableListOf<String>()
        override val connected get() = !closed
        override fun list(path: String): List<NetworkEntry> {
            listFailure?.let { throw it }
            listed += path
            return listOf(NetworkEntry("song.flac", "song.flac", false, 10))
        }
        override fun open(path: String): RemoteFile {
            openFailure?.let { throw it }
            opened += path
            return object : RemoteFile {
                val data = "0123456789".toByteArray()
                var fileClosed = false
                override val length = data.size.toLong()
                override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
                    check(!fileClosed && !closed)
                    if (length == 0) return 0
                    if (position >= data.size) return -1
                    val count = minOf(length, data.size - position.toInt())
                    data.copyInto(buffer, offset, position.toInt(), position.toInt() + count)
                    return count
                }
                override fun close() { fileClosed = true }
            }
        }
        override fun close() { closed = true }
    }
}
