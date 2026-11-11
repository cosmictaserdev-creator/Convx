/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class ChunkedDataSourceRetryTest {
    private val retryable = { e: Exception -> e.message == "403" }

    @Test
    fun succeedsWithoutRetryOrPause() {
        val pauses = mutableListOf<Long>()
        val result = retryOnce(retryable, pauses::add) { retry -> "ok$retry" }
        assertEquals("ok0", result)
        assertEquals(emptyList<Long>(), pauses)
    }

    @Test
    fun retriesOnceAfterAPauseWhenRetryable() {
        val pauses = mutableListOf<Long>()
        val result = retryOnce(retryable, pauses::add) { retry ->
            if (retry == 0) throw IOException("403")
            "ok$retry"
        }
        assertEquals("ok1", result)
        assertEquals(listOf(350L), pauses)
    }

    @Test
    fun aSecondFailurePropagatesWithoutAThirdAttempt() {
        var attempts = 0
        assertThrows(IOException::class.java) {
            retryOnce(retryable, {}) { _ ->
                attempts++
                throw IOException("403")
            }
        }
        assertEquals(2, attempts)
    }

    @Test
    fun nonRetryableFailuresPropagateImmediately() {
        var attempts = 0
        val pauses = mutableListOf<Long>()
        assertThrows(IOException::class.java) {
            retryOnce(retryable, pauses::add) { _ ->
                attempts++
                throw IOException("416")
            }
        }
        assertEquals(1, attempts)
        assertEquals(emptyList<Long>(), pauses)
    }

    @Test
    fun requestNumberIsAppendedToAQuery() {
        assertEquals("https://a.googlevideo.com/videoplayback?id=1&rn=3", "https://a.googlevideo.com/videoplayback?id=1".withRequestNumber(3))
    }

    @Test
    fun requestNumberStartsAQueryWhenThereIsNone() {
        assertEquals("https://a.googlevideo.com/videoplayback?rn=1", "https://a.googlevideo.com/videoplayback".withRequestNumber(1))
    }

    @Test
    fun requestNumberReplacesAnExistingOneInEveryPosition() {
        val base = "https://a.googlevideo.com/videoplayback"
        assertEquals("$base?id=1&rn=9", "$base?rn=2&id=1".withRequestNumber(9))
        assertEquals("$base?id=1&c=WEB&rn=9", "$base?id=1&rn=2&c=WEB".withRequestNumber(9))
        assertEquals("$base?id=1&rn=9", "$base?id=1&rn=2".withRequestNumber(9))
        assertEquals("$base?rn=9", "$base?rn=2".withRequestNumber(9))
    }
}
