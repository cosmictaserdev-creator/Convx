package com.convx.music.client.desktop

import com.convx.music.playback.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.*

class DesktopPreviewPersistenceTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun localArtworkDecodesFromEscapedWindowsFileUrls() = runBlocking {
        val file = File(temporary.root, "album artwork #1 café.png")
        javax.imageio.ImageIO.write(java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", file)
        val context = coil3.PlatformContext.INSTANCE
        val loader = coil3.ImageLoader.Builder(context).components { add(DesktopFileArtworkMapper()) }.build()
        try {
            val result = loader.execute(coil3.request.ImageRequest.Builder(context).data(file.toURI().toString()).build())
            if (result is coil3.request.ErrorResult) throw result.throwable
            val success = assertIs<coil3.request.SuccessResult>(result)
            assertEquals(16, success.image.width)
        } finally { loader.shutdown() }
    }

    @Test fun savedFoldersAndDownloadMetadataSurvivePreviewRestart() = runBlocking {
        val index = File(temporary.root, "media-index.json")
        val art = File(temporary.root, "art")
        val file = temporary.newFile("download.webm").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        val original = PlaybackRequestTrack("youtube_music", "download-id", "Offline song", listOf("Artist"), 120_000)
        val library = DesktopMediaLibrary(index, art, RecordingLibraryWriter())
        try {
            library.updateIndex { it.copy(folders = listOf(temporary.root.path), downloadDirectory = temporary.root.path,
                tracks = listOf(DesktopLibraryTrack(file.toURI().toString(), file.path, original.title, downloadedFrom = original)),
                downloads = listOf(DesktopDownload(original, file.name, DesktopDownloadStatus.COMPLETE, 3, 3, "audio/webm", directory = temporary.root.path))) }
            assertTrue(index.readText().contains("download-id"))
        } finally { library.close() }
        val reopened = DesktopMediaLibrary(index, art, RecordingLibraryWriter())
        try {
            reopened.awaitReady()
            assertEquals(listOf(temporary.root.path), reopened.state.value.index.folders)
            assertEquals(DesktopDownloadStatus.COMPLETE, reopened.state.value.index.downloads.single().status)
            val offline = DesktopLocalAudioStreamResolver(object : AudioStreamResolver {
                override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> = error("Offline playback must not contact a provider")
            }, reopened, FixedPlaybackSettings(PlaybackSettings(localMode = true)))
            assertEquals("audio/webm", offline.resolve(original).getOrThrow().mimeType)
        } finally { reopened.close() }
    }

    @Test fun unavailableFolderProducesVisibleFeedback() = runBlocking {
        val library = DesktopMediaLibrary(File(temporary.root, "index.json"), File(temporary.root, "art"), RecordingLibraryWriter())
        try {
            library.addFolder(File(temporary.root, "missing")).join()
            assertTrue(library.state.value.message.orEmpty().contains("unavailable"))
            assertTrue(library.state.value.index.folders.isEmpty())
        } finally { library.close() }
    }
}
