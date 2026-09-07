package com.convxy.music.comments

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The one helper in this feature that exists purely to avoid a coroutine bug.
 *
 * Swallowing [CancellationException] is invisible in normal running and only shows up as a fetch that
 * refuses to stop when the user skips a track — which is the single most common thing a user does
 * while comments are loading. Worth three tests.
 */
class RunCatchingSuspendTest {

    @Test
    fun `a successful block passes its value through`() {
        runBlocking {
            assertEquals(7, runCatchingSuspend { 7 }.getOrNull())
            assertEquals("ok", runCatchingSuspend { "ok" }.getOrNull())
        }
    }

    @Test
    fun `an ordinary exception becomes a failure`() {
        runBlocking {
            val result = runCatchingSuspend<Int> { throw IllegalStateException("boom") }
            assertTrue(result.isFailure)
            assertEquals("boom", result.exceptionOrNull()?.message)
        }
    }

    @Test
    fun `cancellation is rethrown rather than reported as a failure`() {
        runBlocking {
            val outer = runCatching {
                runCatchingSuspend<Int> { throw CancellationException("stop") }
            }
            // The exception has to escape, or the coroutine that cancelled this work never learns
            // that it stopped.
            assertTrue("CancellationException was swallowed", outer.isFailure)
            assertTrue(outer.exceptionOrNull() is CancellationException)
        }
    }

    @Test
    fun `a suspended failure inside the block is still caught`() {
        runBlocking {
            val result = runCatchingSuspend {
                kotlinx.coroutines.delay(1)
                throw IllegalStateException("after suspension")
            }
            assertTrue(result.isFailure)
            assertEquals("after suspension", result.exceptionOrNull()?.message)
        }
    }
}
