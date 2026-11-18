package com.convx.music.client.desktop

import com.convx.storage.domain.RemoteFile
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFalse

class DesktopNetworkTagReaderTest {
    @Test fun readsRemoteAudioWithoutClosingTheOwnedFile() = runBlocking {
        val format = AudioFormat(44_100f, 16, 1, true, false)
        val bytes = ByteArrayOutputStream().also { output ->
            AudioInputStream(ByteArrayInputStream(ByteArray(176_400)), format, 88_200).use {
                AudioSystem.write(it, AudioFileFormat.Type.WAVE, output)
            }
        }.toByteArray()
        var closed = false
        val file = object : RemoteFile {
            override val length = bytes.size.toLong()
            override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
                if (position >= bytes.size) return -1
                val count = minOf(length, bytes.size - position.toInt())
                bytes.copyInto(buffer, offset, position.toInt(), position.toInt() + count)
                return count
            }
            override fun close() { closed = true }
        }
        val tags = assertNotNull(DesktopNetworkTagReader().read(file, file.length))
        assertEquals(2, tags.durationSeconds)
        assertFalse(closed)
    }
}
