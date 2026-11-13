package com.convx.music.modules

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackAudioStream
import com.convx.music.playback.PlaybackRequestTrack
import com.music.spine.ModuleSearchResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ModuleAudioStreamResolverTest {
    private val source = "https://source.example/index.json"
    private fun transport() = FixtureTransport().apply {
        respond(source, """{"category:modules":[{"id":"m","name":"Module","download":"main.js"}]}""")
        respond("https://source.example/main.js", "module source")
    }
    private fun fallback(onCall: (PlaybackRequestTrack) -> Unit = {}) = object : AudioStreamResolver {
        override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
            onCall(track)
            return Result.success(PlaybackAudioStream("https://youtube.example/audio", "audio/mp4", track.durationMs, 128_000))
        }
    }

    @Test fun selectedModuleTrackResolvesThroughRestoredSourceAndSavedSettings() = runTest {
        val scripts = FixtureScripts().apply {
            manifest = moduleJson.decodeFromString("""{"settings":{"quality":{"defaultValue":"low"}}}""")
        }
        val store = FixtureStore()
        val repository = ModuleRepository(ModuleClient(transport(), scripts), store)
        repository.addSource(source)
        val key = repository.preferences.value.key(repository.preferences.value.modules.single())
        repository.setEnabled(key, true)
        repository.saveSettings(key, mapOf("quality" to "lossless"))
        val request = ModuleTrack(key, "Module", ModuleSearchResult(
            id = "track/a?b", title = "Song", artist = "Artist", duration = 200, albumCover = "cover",
        )).playbackRequest()
        scripts.answer = """{"streamUrl":"https://cdn.example/audio","track":{"codec":"flac","bitrate":1411}}"""
        val restored = ModuleRepository(ModuleClient(transport(), scripts), store)
        val resolver = ModuleAudioStreamResolver(restored, fallback { error("A module must not resolve through YouTube") })
        val stream = resolver.resolve(request).getOrThrow()
        assertEquals("https://cdn.example/audio", stream.url)
        assertEquals("audio/flac", stream.mimeType)
        assertEquals(1_411_000, stream.bitrate)
        assertEquals(200_000L, stream.durationMs)
        assertEquals("cover", request.artworkUrl)
        assertEquals("\"lossless\"", scripts.calls.single().second[1])
        assertEquals("\"track/a?b\"", scripts.calls.single().second[0])
    }

    @Test fun equalTrackIdsFromDifferentModuleSourcesKeepDifferentProviderIdentities() {
        val track = ModuleSearchResult(id = "same-track", title = "Song")
        val first = ModuleTrack("https://first.example\nm", "First", track).playbackRequest()
        val second = ModuleTrack("https://second.example\nm", "Second", track).playbackRequest()
        assertEquals(first.id, second.id)
        assertNotEquals(first.providerId, second.providerId)
    }

    @Test fun disabledAndRemovedModulesAndMalformedIdentitiesCannotFallBackToAnotherProvider() = runTest {
        val repository = ModuleRepository(ModuleClient(transport(), FixtureScripts()), FixtureStore())
        repository.addSource(source)
        val key = repository.preferences.value.key(repository.preferences.value.modules.single())
        val track = ModuleTrack(key, "Module", ModuleSearchResult(id = "track")).playbackRequest()
        val resolver = ModuleAudioStreamResolver(repository, fallback { error("Unexpected fallback") })
        assertTrue(resolver.resolve(track).isFailure)
        repository.removeSource(source)
        assertTrue(resolver.resolve(track).isFailure)
        assertTrue(resolver.resolve(track.copy(providerId = "spine:%%%")).isFailure)
    }

    @Test fun nonModuleProvidersUseTheirExistingResolverAndCancellationPropagates() = runTest {
        val scripts = FixtureScripts()
        val repository = ModuleRepository(ModuleClient(transport(), scripts), FixtureStore())
        var forwarded: PlaybackRequestTrack? = null
        val resolver = ModuleAudioStreamResolver(repository, fallback { forwarded = it })
        val youtube = PlaybackRequestTrack("youtube_music", "video", "Video", listOf("Artist"))
        assertEquals("https://youtube.example/audio", resolver.resolve(youtube).getOrThrow().url)
        assertEquals(youtube, forwarded)
        repository.addSource(source)
        val key = repository.preferences.value.key(repository.preferences.value.modules.single())
        repository.setEnabled(key, true)
        scripts.failure = CancellationException("cancelled")
        assertFailsWith<CancellationException> {
            resolver.resolve(ModuleTrack(key, "Module", ModuleSearchResult(id = "track")).playbackRequest())
        }
    }

    @Test fun transportMetadataTakesPrecedenceOverContainerMetadata() {
        val stream = moduleJson.decodeFromString<com.music.spine.ModuleStreamResponse>(
            """{"streamUrl":"https://cdn.example/opaque","streamType":"hls","mimeType":"audio/flac"}""",
        )
        assertEquals("application/vnd.apple.mpegurl", stream.playbackMimeType())
    }
}
