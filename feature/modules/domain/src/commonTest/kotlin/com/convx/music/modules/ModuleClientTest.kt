package com.convx.music.modules

import com.music.spine.EclipseAddon
import com.music.spine.ModuleManifest
import com.music.spine.SpineModule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FixtureTransport : ModuleTransport {
    val requests = mutableListOf<String>()
    val responses = mutableMapOf<String, ModuleHttpResponse>()
    override suspend fun request(url: String, method: String, headers: Map<String, String>, body: String?): ModuleHttpResponse {
        requests += url
        return responses[url] ?: ModuleHttpResponse(404, "")
    }
    fun respond(url: String, body: String) { responses[url] = ModuleHttpResponse(200, body) }
}

internal class FixtureScripts : ModuleScriptRuntime {
    var manifest = ModuleManifest()
    var answer = """{"tracks":[{"id":"track","title":"Song"}],"total":1}"""
    val calls = mutableListOf<Pair<String, List<String>>>()
    var failure: Exception? = null
    var responseFor: ((String, List<String>) -> String)? = null
    override suspend fun manifest(source: String, baseUrl: String): ModuleManifest = manifest
    override suspend fun call(source: String, baseUrl: String, export: String, arguments: List<String>): String {
        failure?.let { throw it }
        calls += export to arguments
        return responseFor?.invoke(export, arguments) ?: answer
    }
}

class ModuleClientTest {
    @Test fun addonLandingPagesAndRedirectedIndexesUseTheirResolvedBase() = runTest {
        val transport = FixtureTransport().apply {
            respond("https://addon.example/token", "<html>Add-on landing page</html>")
            respond("https://addon.example/token/manifest.json", """{"id":"addon"}""")
            responses["https://source.example/index.json"] = ModuleHttpResponse(302, "", mapOf("location" to "/catalog/index.json"))
            respond("https://source.example/catalog/index.json", """{"category:modules":[{"id":"m","name":"M","download":"main.js"}]}""")
        }
        val client = ModuleClient(transport, FixtureScripts())
        assertEquals("https://addon.example/token", client.fetchSource("https://addon.example/token").single().download)
        assertEquals("https://source.example/catalog/main.js", client.fetchSource("https://source.example/index.json").single().download)
    }

    @Test fun resolvesRelativeIndexDownloadsAndFiltersNonMusicCategories() = runTest {
        val transport = FixtureTransport().apply {
            respond("https://source.example/catalog/index.json", """{
                "category:modules":[{"id":"m","name":"Module","download":"../scripts/main.js","icon":"images/logo.png"}],
                "category:artworks":[{"id":"art","name":"Art"}],
                "category:testing":[{"id":"test","name":"Test"}]
            }""")
        }
        val module = ModuleClient(transport, FixtureScripts()).fetchSource("https://source.example/catalog/index.json").single()
        assertEquals("https://source.example/scripts/main.js", module.download)
        assertEquals("https://source.example/catalog/images/logo.png", module.icon)
        assertEquals("https://source.example/catalog/index.json", module.sourceUrl)
    }

    @Test fun addonSearchAndStreamPreserveTokenUnicodeAndSettings() = runTest {
        val transport = FixtureTransport().apply {
            respond("https://addon.example/token/manifest.json", """{"id":"addon","settings":[
                {"key":"country","default":"IN"},
                {"key":"quality","default":"low","options":[{"value":"low"},{"value":"flac"}]}
            ]}""")
            respond("https://addon.example/token/search?q=caf%C3%A9%20%26%20music&country=IN&quality=flac",
                """{"tracks":[{"id":"a/b?c","title":"Song","duration":201.5}]}""")
            respond("https://addon.example/token/stream/a%2Fb%3Fc?country=IN&quality=flac",
                """{"url":"https://cdn.example/audio","fileCodec":"flac","bitDepth":24,"manifest":"hls"}""")
        }
        val client = ModuleClient(transport, FixtureScripts())
        val module = client.fetchSource("https://addon.example/token/manifest.json").single()
        val loaded = client.load(module)
        val settings = mapOf("quality" to "LOSSLESS")
        val track = client.search(loaded, "café & music", settings).tracks.single()
        assertEquals(201, track.duration)
        val stream = client.stream(loaded, track.id, settings)
        assertEquals("flac", stream.track?.codec)
        assertEquals("hls", stream.streamType)
    }

    @Test fun moduleCallsUsePerSettingValuesAndResolveStreamsAfresh() = runTest {
        val transport = FixtureTransport().apply { respond("https://source.example/main.js", "module source") }
        val scripts = FixtureScripts().apply {
            manifest = moduleJson.decodeFromString("""{"noStreamCache":true,"settings":{"country":{"defaultValue":"IN"}}}""")
        }
        val client = ModuleClient(transport, scripts)
        val loaded = client.load(SpineModule("m", "Module", download = "https://source.example/main.js"))
        client.search(loaded, "query", mapOf("quality" to "LOSSLESS"))
        val context = moduleJson.parseToJsonElement(scripts.calls.single().second[2]).jsonObject.getValue("settings").jsonObject
        assertEquals("IN", context.getValue("country").jsonObject.getValue("value").jsonPrimitive.content)
        assertEquals("LOSSLESS", context.getValue("quality").jsonObject.getValue("value").jsonPrimitive.content)
        scripts.answer = """{"streamUrl":"https://cdn.example/first"}"""
        assertEquals("https://cdn.example/first", client.stream(loaded, "track", emptyMap()).streamUrl)
        scripts.answer = """{"streamUrl":"https://cdn.example/second"}"""
        assertEquals("https://cdn.example/second", client.stream(loaded, "track", emptyMap()).streamUrl)
    }

    @Test fun invalidUrlsDowngradesAndEncryptedStreamsAreRejected() = runTest {
        for (url in listOf("file:///tmp/source", "http://remote.example/index.json", "https://user:pass@source.example/index.json", "https://", "source.example")) {
            assertFalse(EclipseAddon.isAllowedUrl(url), url)
        }
        assertTrue(EclipseAddon.isAllowedUrl("http://localhost:7000/manifest.json"))
        val transport = FixtureTransport().apply {
            responses["https://source.example/redirect"] = ModuleHttpResponse(302, "", mapOf("location" to "http://remote.example/index.json"))
        }
        assertFailsWith<IllegalArgumentException> { ModuleClient(transport, FixtureScripts()).fetchSource("https://source.example/redirect") }
        assertEquals(1, transport.requests.size)
        assertFailsWith<IllegalArgumentException> {
            EclipseAddon.parseStream(moduleJson, "track", """{"url":"https://cdn.example/audio","encrypted":true}""")
        }
    }

    @Test fun rateLimitsDoNotTriggerManifestFallbackAndCancellationPropagates() = runTest {
        val transport = FixtureTransport().apply {
            responses["https://source.example/addon"] = ModuleHttpResponse(429, "", mapOf("retry-after" to "300"))
        }
        val scripts = FixtureScripts()
        val client = ModuleClient(transport, scripts)
        assertFailsWith<ModuleHttpException> { client.fetchSource("https://source.example/addon") }
        assertFailsWith<IllegalArgumentException> { client.fetchSource("https://source.example/addon") }
        assertEquals(1, transport.requests.size)
        scripts.failure = CancellationException("cancelled")
        assertFailsWith<CancellationException> {
            client.search(LoadedModule(SpineModule("m", "Module", download = "https://source.example/script"), ModuleManifest()), "q", emptyMap())
        }
    }
}
