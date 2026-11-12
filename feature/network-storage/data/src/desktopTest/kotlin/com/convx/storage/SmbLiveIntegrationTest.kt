package com.convx.storage

import com.convx.storage.data.SmbFileSource
import com.convx.storage.domain.NetworkServer
import com.convx.storage.domain.SourceCredentials
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.TimeUnit

/** Opt-in: CONVX_SMB_LIVE=1 and PYTHONPATH containing impacket. */
class SmbLiveIntegrationTest {
    @get:Rule val temp = TemporaryFolder()

    @Test fun reconnectsAfterRealServerRestartAndReadsFromSeekOffset() = runBlocking {
        assumeTrue(System.getenv("CONVX_SMB_LIVE") == "1")
        temp.newFile("song.flac").writeText("0123456789")
        val port = ServerSocket(0).use { it.localPort }
        val server = NetworkServer.Smb("nas", "NAS", "127.0.0.1", port, "Music", "", SourceCredentials("user", "secret"))
        var process = startServer(port)
        try {
            SmbFileSource().use { source ->
                assertEquals("song.flac", source.list(server, "").single().name)
                source.open(server, "song.flac").use { file ->
                    val bytes = ByteArray(3)
                    assertEquals(3, file.read(2, bytes, 0, 3))
                    assertEquals("234", bytes.toString(Charsets.UTF_8))
                }
                process.destroyForcibly()
                check(process.waitFor(10, TimeUnit.SECONDS))
                process = startServer(port)
                source.open(server, "song.flac").use { file ->
                    val bytes = ByteArray(3)
                    assertEquals(3, file.read(7, bytes, 0, 3))
                    assertEquals("789", bytes.toString(Charsets.UTF_8))
                }
            }
        } finally {
            process.destroyForcibly()
            process.waitFor(10, TimeUnit.SECONDS)
        }
    }

    private fun startServer(port: Int): Process {
        val script = """
import sys
from impacket.smbserver import SimpleSMBServer
from impacket.ntlm import compute_lmhash, compute_nthash
s = SimpleSMBServer(listenAddress='127.0.0.1', listenPort=int(sys.argv[1]))
s.setSMB2Support(True)
s.addShare('Music', sys.argv[2], readOnly='yes')
s.addCredential('user', 0, compute_lmhash('secret').hex(), compute_nthash('secret').hex())
s.start()
""".trimIndent()
        val process = ProcessBuilder("python", "-c", script, port.toString(), temp.root.absolutePath)
            .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start()
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15)
        while (System.nanoTime() < deadline) {
            check(process.isAlive) { "SMB server exited before starting" }
            try { Socket("127.0.0.1", port).close(); return process } catch (_: java.io.IOException) { Thread.sleep(100) }
        }
        process.destroyForcibly()
        error("SMB server did not start")
    }
}
