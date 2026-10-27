package com.convx.music.client.desktop

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.test.*

class DesktopFileOpenRequestsTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun fileHandoffHandlesEscapedPathsAndIsConsumedOnce() {
        val file = temporary.newFile("music #1 café.flac")
        val requests = DesktopFileOpenRequests(File(temporary.root, "requests"))
        assertTrue(requests.submit(listOf("--unknown", file.path, file.toURI().toString(), "https://example.com/song.mp3")))
        assertEquals(listOf(listOf(file.canonicalFile)), requests.takePending())
        assertTrue(requests.takePending().isEmpty())
        assertTrue(file.isFile)
        assertFalse(requests.submit(listOf(temporary.newFile("readme.txt").path)))
    }

    @Test fun openedFilesSurviveRescansWithoutImportingTheirEntireFolder() = runBlocking {
        val music = temporary.newFolder("Music")
        val file = File(music, "single track.wav")
        val format = AudioFormat(44_100f, 16, 1, true, false)
        AudioInputStream(ByteArrayInputStream(ByteArray(88_200)), format, 44_100).use { AudioSystem.write(it, AudioFileFormat.Type.WAVE, file) }
        val media = DesktopMediaLibrary(File(temporary.root, "index.json"), File(temporary.root, "art"), RecordingLibraryWriter())
        try {
            val imported = media.importFiles(listOf(file))
            assertEquals(file.canonicalPath, imported.single().filePath)
            assertTrue(media.state.value.index.folders.isEmpty())
            media.rescan()
            withTimeout(20_000) { media.state.first { !it.scanning && it.totalFiles == 1 } }
            assertEquals(imported.single().id, media.state.value.index.tracks.single().id)
            assertTrue(file.isFile)
        } finally { media.close() }
    }
}
