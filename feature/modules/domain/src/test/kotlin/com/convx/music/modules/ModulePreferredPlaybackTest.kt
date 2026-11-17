package com.convx.music.modules

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackAudioStream
import com.convx.music.playback.PlaybackRequestTrack
import com.music.spine.ModuleSearchResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModulePreferredPlaybackTest {
    private val request = PlaybackRequestTrack("youtube_music", "video", "Song", listOf("Artist"), 200_000L)
    private val source = "https://source.example/index.json"
    private fun quality(arguments: List<String>): String =
        moduleJson.parseToJsonElement(arguments[2]).jsonObject.getValue("settings").jsonObject
            .getValue("quality").jsonObject.getValue("value").jsonPrimitive.content

    @Test fun followsPriorityAndFallsBackWhenThePreferredModuleCannotStream() = runTest {
        val transport = FixtureTransport().apply {
            respond(source, """{"category:modules":[
                {"id":"first","name":"First","download":"first.js"},
                {"id":"second","name":"Second","download":"second.js"}
            ]}""")
            respond("https://source.example/first.js", "first")
            respond("https://source.example/second.js", "second")
        }
        val scripts = FixtureScripts().apply {
            manifest = moduleJson.decodeFromString("""{"settings":{"quality":{"defaultValue":"default"}}}""")
            responseFor = { export, arguments ->
                if (export == "searchTracks") """{"tracks":[{"id":"song","title":"Song","artist":"Artist","duration":200}]}"""
                else if (quality(arguments) == "first") """{"streamUrl":null}"""
                else """{"streamUrl":"https://cdn.example/second","mimeType":"audio/flac"}"""
            }
        }
        val repository = ModuleRepository(ModuleClient(transport, scripts), FixtureStore())
        repository.addSource(source)
        val keys = repository.preferences.value.modules.map(repository.preferences.value::key)
        keys.forEachIndexed { index, key ->
            repository.setEnabled(key, true)
            repository.saveSettings(key, mapOf("quality" to if (index == 0) "first" else "second"))
        }
        val resolver = ModuleAudioStreamResolver(repository, object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> = error("Unexpected YouTube fallback")
        })
        assertEquals("https://cdn.example/second", resolver.resolve(request).getOrThrow().url)
        assertEquals(listOf("first", "second"), scripts.calls.filter { it.first == "getTrackStreamUrl" }.map { quality(it.second) })
        scripts.calls.clear()
        repository.move(keys[1], -1)
        assertEquals("https://cdn.example/second", resolver.resolve(request).getOrThrow().url)
        assertEquals(listOf("second"), scripts.calls.filter { it.first == "getTrackStreamUrl" }.map { quality(it.second) })
    }

    @Test fun alternateRecordingsOrProviderOutagesRetainTheOriginalCatalogResolver() = runTest {
        val transport = FixtureTransport().apply {
            respond(source, """{"category:modules":[{"id":"m","name":"Module","download":"main.js"}]}""")
            respond("https://source.example/main.js", "source")
        }
        val scripts = FixtureScripts()
        val repository = ModuleRepository(ModuleClient(transport, scripts), FixtureStore())
        repository.addSource(source)
        val key = repository.preferences.value.key(repository.preferences.value.modules.single())
        repository.setEnabled(key, true)
        val resolver = ModuleAudioStreamResolver(repository, object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack) =
                Result.success(PlaybackAudioStream("https://youtube.example/audio", "audio/mp4", track.durationMs, 128_000))
        })
        scripts.answer = """{"tracks":[{"id":"live","title":"Song (Live)","artist":"Artist","duration":200}]}"""
        assertEquals("https://youtube.example/audio", resolver.resolve(request).getOrThrow().url)
        assertTrue(scripts.calls.all { it.first == "searchTracks" })
        scripts.failure = IllegalStateException("Provider offline")
        assertEquals("https://youtube.example/audio", resolver.resolve(request).getOrThrow().url)
        scripts.failure = CancellationException("cancelled")
        assertFailsWith<CancellationException> { resolver.resolve(request) }
    }

    @Test fun recordingIdentityRequiresTitleArtistAndCompatibleDuration() {
        val exact = ModuleSearchResult(id = "song", title = "SONG", artist = "Artist & Guest", duration = 200)
        assertTrue(exact.matches(request))
        assertTrue(exact.copy(title = "Song (feat. Guest)").matches(request))
        assertTrue(exact.copy(duration = 0).matches(request))
        assertFalse(exact.copy(title = "Song Remix").matches(request))
        assertFalse(exact.copy(artist = "Another Artist").matches(request.copy(artists = listOf("Artist & Guest"))))
        assertFalse(exact.copy(artist = "Artist2").matches(request))
        assertFalse(exact.copy(duration = 211).matches(request))
        assertFalse(exact.matches(request.copy(title = "")))
    }

    @Test fun matchesAndroidUnicodeAccentAndCompatibilityNormalization() {
        val request = request.copy(title = "Café", artists = listOf("Rós"))
        assertTrue(ModuleSearchResult(id = "song", title = "Cafe", artist = "Ros", duration = 200).matches(request))
        assertTrue(ModuleSearchResult(id = "song", title = "Ｃａｆｅ", artist = "Ｒｏｓ", duration = 200).matches(request))
        assertTrue(ModuleSearchResult(id = "song", title = "Cafe\u0301", artist = "Ro\u0301s", duration = 200).matches(request))
    }
}
