package com.convx.music.utils

import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArtworkProcessingTest {
    @Test
    fun `overlapping requests reuse one completed artwork treatment`() = runBlocking {
        val cache = AtomicReference<String?>(null)
        val started = CompletableDeferred<Unit>()
        val finish = CompletableDeferred<Unit>()
        var computations = 0
        val results = coroutineScope {
            val requests = List(12) {
                async {
                    ArtworkProcessing.cached("shared", cache::get) {
                        computations++
                        started.complete(Unit)
                        finish.await()
                        "pixels".also(cache::set)
                    }
                }
            }
            started.await()
            yield()
            finish.complete(Unit)
            requests.awaitAll()
        }
        assertEquals(1, computations)
        assertEquals(List(12) { "pixels" }, results)
    }

    @Test
    fun `disposing a card cancels its work and lets the next request retry`() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val first = launch {
            ArtworkProcessing.cached<String>("cancelled", { null }) {
                started.complete(Unit)
                CompletableDeferred<Unit>().await()
                "unreachable"
            }
        }
        started.await()
        first.cancelAndJoin()
        assertEquals("retry", ArtworkProcessing.cached("cancelled", { null }) { "retry" })
    }

    @Test
    fun `an unavailable image does not poison later retries`() = runBlocking {
        assertNull(ArtworkProcessing.cached<String>("missing", { null }) { null })
        assertEquals("loaded", ArtworkProcessing.cached("missing", { null }) { "loaded" })
    }
}
