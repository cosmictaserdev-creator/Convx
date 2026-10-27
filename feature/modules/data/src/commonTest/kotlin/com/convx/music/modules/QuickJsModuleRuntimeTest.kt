package com.convx.music.modules

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QuickJsModuleRuntimeTest {
    @Test fun evaluatesBothSourceFormsAndAwaitsNativeFetch() = runTest {
        val transport = ModuleTransport { url, _, _, _ ->
            assertEquals("https://source.example/api/search", url)
            ModuleHttpResponse(200, """{"tracks":[{"id":"one","title":"Song"}]}""")
        }
        val runtime = QuickJsModuleRuntime(transport)
        val source = """
            module.exports = {
                settings: {quality: {type: 'selector', defaultValue: 'high', options: [{label: 'High', value: 'high'}]}},
                searchTracks: async function(q, limit, context) {
                    var response = await fetch('api/search');
                    return response.json();
                },
                getTrackStreamUrl: async function(id, quality, context) {
                    return {streamUrl: 'https://cdn.example/' + context.settings.quality.value};
                }
            };
        """.trimIndent()
        for (code in listOf(source, "export const moduleCode = `" + source + "`;")) {
            assertEquals("high", runtime.manifest(code, "https://source.example/").defaults["quality"])
            val context = ModuleClient.context(mapOf("quality" to "high"))
            val result = runtime.call(code, "https://source.example/", "searchTracks", listOf("\"q\"", "50", context))
            assertEquals("one", moduleJson.decodeFromString<com.music.spine.ModuleSearchResponse>(result).tracks.single().id)
            val stream = runtime.call(code, "https://source.example/", "getTrackStreamUrl", listOf("\"one\"", "\"high\"", context))
            assertEquals("https://cdn.example/high", moduleJson.decodeFromString<com.music.spine.ModuleStreamResponse>(stream).streamUrl)
        }
    }

    @Test fun scriptExceptionsFailTheCall() = runTest {
        val runtime = QuickJsModuleRuntime(ModuleTransport { _, _, _, _ -> error("Unexpected fetch") })
        val source = "module.exports = {searchTracks: function(){throw new Error('Provider failed');}, getTrackStreamUrl: function(){return {};}};"
        assertFailsWith<IllegalStateException> { runtime.call(source, "https://source.example/", "searchTracks", emptyList()) }
    }

    @Test fun clearingTimeoutDoesNotHoldTheEvaluationOpenAndAtobAcceptsMissingPadding() = runTest {
        withContext(Dispatchers.Default) {
            val runtime = QuickJsModuleRuntime(ModuleTransport { _, _, _, _ -> error("Unexpected fetch") })
            val source = """
                module.exports = {
                    searchTracks: function() {
                        var timer = setTimeout(function() { throw new Error('Cancelled timer fired'); }, 30000);
                        clearTimeout(timer);
                        return {tracks: [{id: atob('dHJhY2s'), title: 'Song'}]};
                    },
                    getTrackStreamUrl: function() { return {}; }
                };
            """.trimIndent()
            val result = withTimeout(2_000) { runtime.call(source, "https://source.example/", "searchTracks", emptyList()) }
            assertEquals("track", moduleJson.decodeFromString<com.music.spine.ModuleSearchResponse>(result).tracks.single().id)
        }
    }

    @Test fun cancelsAnEvaluationAfterJavaScriptActuallyStarts() = runTest {
        // Real dispatchers prevent runTest's virtual clock cancelling before the VM is entered.
        withContext(Dispatchers.Default) {
            val entered = CompletableDeferred<Unit>()
            val runtime = QuickJsModuleRuntime(ModuleTransport { _, _, _, _ ->
                entered.complete(Unit)
                ModuleHttpResponse(200, "{}")
            })
            val work = async {
                runtime.call("""
                    module.exports = {
                        searchTracks: async function() { await fetch('started'); while(true) {} },
                        getTrackStreamUrl: function() { return {}; }
                    };
                """.trimIndent(), "https://source.example/", "searchTracks", emptyList())
            }
            try {
                withTimeout(10_000) { entered.await() }
                delay(100)
                withTimeout(2_000) { work.cancelAndJoin() }
                assertTrue(work.isCancelled)
            } finally {
                work.cancel()
            }
        }
    }
}
