package com.convx.music.client.desktop

import com.convx.music.library.*
import com.convx.music.playback.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import javax.sound.sampled.*
import kotlin.test.*

class DesktopMediaLibraryTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test
    fun scansPersistsAndRemovesFolderWithoutDeletingMusic() = runBlocking {
        val music = temporary.newFolder("Music")
        val audio = File(music, "fixture.wav")
        val format = AudioFormat(44_100f, 16, 1, true, false)
        AudioInputStream(ByteArrayInputStream(ByteArray(88_200)), format, 44_100).use { AudioSystem.write(it, AudioFileFormat.Type.WAVE, audio) }
        val index = File(temporary.root, "index.json")
        val writer = RecordingLibraryWriter()
        val library = DesktopMediaLibrary(index, File(temporary.root, "art"), writer)
        library.addFolder(music).join()
        val loaded = withTimeout(20_000) { library.state.first { !it.scanning && it.index.tracks.size == 1 } }
        val track = loaded.index.tracks.single()
        assertEquals("fixture", track.title)
        assertTrue(requireNotNull(track.durationMs) in 990L..1010L)
        assertEquals(audio.toURI().toString(), track.id)
        assertEquals(1, writer.tracks.size)
        library.setFavorite(track.id, true).join()
        library.close()

        val reopened = DesktopMediaLibrary(index, File(temporary.root, "art"), writer)
        reopened.awaitReady()
        assertTrue(reopened.state.value.index.tracks.single().favorite)
        val offline = FixedPlaybackSettings(PlaybackSettings(localMode = true))
        var onlineCalls = 0
        val resolver = DesktopLocalAudioStreamResolver(object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> { onlineCalls++; return Result.failure(Exception("Network must not be used")) }
        }, reopened, offline)
        val stream = resolver.resolve(track.request()).getOrThrow()
        assertEquals(audio.canonicalFile, File(URI(stream.url)).canonicalFile)
        assertEquals(0, onlineCalls)
        assertTrue(resolver.resolve(PlaybackRequestTrack("youtube_music", "not-downloaded", "Online", emptyList())).isFailure)
        assertEquals(0, onlineCalls)

        val decoder = DesktopAudioTranscoder()
        val decoded = decoder.transcode(audio.path, track.durationMs)
        assertTrue(decoded.toFile().length() > 44)
        decoder.delete(decoded)
        decoder.close()
        assertTrue(audio.isFile, "Decoded temporary audio must never take ownership of the source file")
        reopened.removeFolder(music.canonicalPath).join()
        withTimeout(10_000) { reopened.state.first { !it.scanning && it.index.tracks.isEmpty() } }
        assertTrue(audio.isFile, "Removing a folder only removes library entries")
        reopened.close()
    }
}

internal class RecordingLibraryWriter : MusicLibraryWriter {
    val tracks = mutableListOf<PlaylistSyncTrack>()
    override suspend fun savePlaylist(playlist: PlaylistDetail) = Unit
    override suspend fun createLocalPlaylist(name: String, description: String?) = "playlist"
    override suspend fun saveTracks(tracks: List<PlaylistSyncTrack>) { this.tracks.addAll(tracks) }
}

internal class FixedPlaybackSettings(private var value: PlaybackSettings) : PlaybackSettingsRepository {
    override fun observeSettings() = flowOf(value)
    override suspend fun getSettings() = value
    override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings { value = transform(value); return value }
}
