package com.convx.storage.data

import com.convx.storage.domain.*
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.util.EnumSet
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

/** The SMBJ boundary; readers own their file handles and carry absolute positions. */
internal interface SmbFileSession : java.io.Closeable {
    val connected: Boolean
    fun list(path: String): List<NetworkEntry>
    fun open(path: String): RemoteFile
}

class SmbFileSource internal constructor(private val connect: (NetworkServer.Smb) -> SmbFileSession) : NetworkFileSource {
    constructor() : this(::SmbjFileSession)
    private val sessions = ConcurrentHashMap<String, Session>()

    override suspend fun list(server: NetworkServer, folder: String): List<NetworkEntry> = withContext(Dispatchers.IO) {
        coroutineContext.ensureActive()
        val source = server as? NetworkServer.Smb ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        val relative = RemotePath.normalize(folder)
        withSession(source) { connection, _ -> connection.list(fullPath(source, relative)) }.mapNotNull { entry ->
            coroutineContext.ensureActive()
            if (entry.name == "." || entry.name == "..") null
            else entry.copy(path = RemotePath.child(relative, entry.name))
        }.sortedWith(compareBy<NetworkEntry> { !it.directory }.thenBy { it.name.lowercase() })
    }

    override fun open(server: NetworkServer, path: String): RemoteFile {
        val source = server as? NetworkServer.Smb ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        return withSession(source) { connection, owner ->
            val file = connection.open(fullPath(source, path))
            object : RemoteFile {
                override val length = file.length
                override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int = try {
                    file.read(position, buffer, offset, length)
                } catch (failure: Exception) {
                    invalidate(source.id, owner)
                    throw failure
                }
                override fun close() { file.close() }
            }
        }
    }

    override fun invalidate(id: String) { sessions.remove(id)?.close() }
    override fun close() { sessions.values.forEach { it.close() }; sessions.clear() }

    private fun session(server: NetworkServer.Smb): Session = requireNotNull(sessions.compute(server.id) { _, existing ->
        if (existing != null && !existing.closed.get() && existing.settings == server && existing.share.connected) existing
        else {
            existing?.close()
            Session(server, connect(server))
        }
    })

    // BitChord's retry-on-transport-failure policy, adapted for multiple saved servers
    // and concurrent player/read-ahead handles. See NOTICE.
    private fun <T> withSession(server: NetworkServer.Smb, action: (SmbFileSession, Session) -> T): T {
        repeat(2) { attempt ->
            var owner: Session? = null
            try {
                owner = session(server)
                return action(owner.share, owner)
            } catch (failure: StorageException) {
                throw failure // Configuration/authentication errors are not transport retries.
            } catch (failure: IOException) {
                owner?.let { invalidate(server.id, it) }
                if (attempt == 1) throw failure
            }
        }
        error("SMB retry did not complete")
    }

    private fun invalidate(id: String, expected: Session) {
        // An old file's failure must not evict a replacement connection.
        if (sessions.remove(id, expected)) expected.close()
    }
    private fun fullPath(server: NetworkServer.Smb, relative: String): String =
        listOf(server.folder, RemotePath.normalize(relative)).filter(String::isNotEmpty).joinToString("/")

    private class Session(val settings: NetworkServer.Smb, val share: SmbFileSession) : java.io.Closeable {
        val closed = AtomicBoolean(false)
        override fun close() {
            if (closed.compareAndSet(false, true)) runCatching { share.close() }
        }
    }
}

private class SmbjFileSession(server: NetworkServer.Smb) : SmbFileSession {
    private val client = SMBClient(SmbConfig.builder()
        .withTimeout(20, TimeUnit.SECONDS)
        .withReadTimeout(30, TimeUnit.SECONDS)
        .withWriteTimeout(30, TimeUnit.SECONDS)
        .withTransactTimeout(30, TimeUnit.SECONDS)
        .withSoTimeout(30, TimeUnit.SECONDS).build())
    private val connection: Connection
    private val disk: DiskShare
    override val connected: Boolean get() = connection.isConnected

    init {
        try {
            connection = client.connect(server.host, server.port)
            val credentials = server.credentials
            val authentication = if (credentials.username.isBlank() && credentials.password.isBlank()) AuthenticationContext.guest()
                else AuthenticationContext(credentials.username, credentials.password.toCharArray(), credentials.domain)
            disk = connection.authenticate(authentication).connectShare(server.share) as? DiskShare
                ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        } catch (error: Exception) {
            runCatching { client.close() }
            throw error
        }
    }

    override fun list(path: String): List<NetworkEntry> = disk.list(path.replace('/', '\\')).map { info ->
        NetworkEntry(info.fileName, info.fileName,
            info.fileAttributes and FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value != 0L, info.endOfFile)
    }

    override fun open(path: String): RemoteFile {
        val handle = disk.openFile(path.replace('/', '\\'),
            EnumSet.of(AccessMask.FILE_READ_DATA, AccessMask.FILE_READ_ATTRIBUTES),
            null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null)
        try {
            val size = handle.length
            return object : RemoteFile {
                override val length = size
                override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int = handle.read(buffer, position, offset, length)
                override fun close() { handle.close() }
            }
        } catch (error: Exception) { handle.close(); throw error }
    }

    override fun close() {
        runCatching { disk.close() }
        runCatching { connection.close() }
        runCatching { client.close() }
    }
}
