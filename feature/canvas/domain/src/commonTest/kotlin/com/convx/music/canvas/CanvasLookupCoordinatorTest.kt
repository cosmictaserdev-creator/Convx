package com.convx.music.canvas

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CanvasLookupCoordinatorTest {
    @Test
    fun surfacesShareLookupAndOneCanLeave() = runTest {
        var calls = 0
        val response = CompletableDeferred<String?>()
        val resolver = CanvasLookupCoordinator(backgroundScope, { 0L }, 1000, 1000) { _: String ->
            calls++
            response.await()
        }
        val first = async { resolver.resolve("track") }
        val second = async { resolver.resolve("track") }
        runCurrent()
        first.cancel()
        response.complete("canvas")
        assertEquals("canvas", second.await())
        assertEquals("canvas", resolver.resolve("track"))
        assertEquals(1, calls)
    }

    @Test
    fun negativeResultsExpire() = runTest {
        var now = 0L
        var calls = 0
        val resolver = CanvasLookupCoordinator(backgroundScope, { now }, 1000, 10) { _: String ->
            calls++
            null as String?
        }
        assertNull(resolver.resolve("track"))
        assertNull(resolver.resolve("track"))
        assertEquals(1, calls)
        now = 11
        assertNull(resolver.resolve("track"))
        assertEquals(2, calls)
    }

    @Test
    fun compatibleRequestCanReuseAnUnexpiredResult() = runTest {
        var now = 0L
        val resolver = CanvasLookupCoordinator(backgroundScope, { now }, 10, 10) { key: String -> "result:$key" }
        assertEquals("result:track:old-order", resolver.resolve("track:old-order"))
        assertEquals("result:track:old-order", resolver.findCached { key, _ -> key.startsWith("track:") })
        assertNull(resolver.findCached { key, _ -> key.startsWith("another:") })
        now = 11
        assertNull(resolver.findCached { _, _ -> true })
    }
}
