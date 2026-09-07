package com.convxy.music.comments

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The repository's decision table: every way a provider can answer, and what the UI is told.
 *
 * The contract that matters most is the last one in the file — *nothing here throws*. A comments
 * outage has to degrade to a message in a bottom sheet, because this code runs alongside a player
 * that must keep playing.
 */
class CommentsRepositoryTest {

    private class FakeStorage : CommentCacheStorage {
        val entries = LinkedHashMap<String, CommentsCache.Entry>()
        override fun read(key: String) = entries[key]
        override fun write(key: String, entry: CommentsCache.Entry) {
            entries[key] = entry
        }

        var deleteAlls = 0

        override fun delete(key: String) {
            entries.remove(key)
        }

        override fun deleteAll() {
            deleteAlls++
            entries.clear()
        }
    }

    private class FakeSource(
        override val source: CommentSource = CommentSource.AUDIUS,
        override val name: String = "SoundCloud",
        private val configured: Boolean = true,
        private val supported: Boolean = true,
        private var result: CommentsOutcome = CommentsOutcome.Found(emptyList()),
        private val throws: Boolean = false,
    ) : CommentsDataSource {
        var fetches = 0
            private set

        override suspend fun isConfigured() = configured

        override fun supports(track: CommentTrackRef) = supported

        override suspend fun fetchComments(track: CommentTrackRef): CommentsOutcome {
            fetches++
            if (throws) throw IllegalStateException("socket closed")
            return result
        }

        fun respondWith(next: CommentsOutcome) {
            result = next
        }
    }

    /**
     * @param order the priority list the repository should use. Defaults to the sources in the order
     *   they were passed, so a test that says `Harness(first, second)` is testing "first wins" without
     *   having to spell the preference out — and a test about ordering itself can override it.
     */
    private class Harness(
        vararg sources: CommentsDataSource,
        order: List<CommentSource>? = null,
    ) {
        val storage = FakeStorage()
        val ranked: List<CommentSource> = order ?: sources.map { it.source }
        val repository = CommentsRepository(
            sources.toList(),
            { ranked },
            CommentsCache(storage, nowMillis = { 0L }),
        )
    }

    private val track = CommentTrackRef.of("yt-id", "Nightcall", listOf("Kavinsky"), 250)

    private fun comment(id: String, at: Long) = TimestampedComment(
        id = id,
        trackId = "sc-1",
        authorName = "author",
        text = "body $id",
        timestampMs = at,
        sourceName = "SoundCloud",
    )

    @Test
    fun `no sources at all means unsupported`() {
        runBlocking {
            val result = Harness().repository.commentsFor(track)
            assertEquals(CommentsStatus.UNSUPPORTED, result.status)
            assertTrue(result.comments.isEmpty())
        }
    }

    @Test
    fun `an unconfigured provider costs no request`() {
        runBlocking {
            val source = FakeSource(configured = false)
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.NOT_CONFIGURED, result.status)
            assertEquals("isConfigured must short-circuit before any I/O", 0, source.fetches)
        }
    }

    @Test
    fun `a track no provider can map is unsupported, not misconfigured`() {
        runBlocking {
            val source = FakeSource(supported = false)
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.UNSUPPORTED, result.status)
            assertEquals(0, source.fetches)
        }
    }

    @Test
    fun `found comments come back normalised`() {
        runBlocking {
            val source = FakeSource(
                result = CommentsOutcome.Found(listOf(comment("late", 5_000), comment("early", 1_000))),
            )
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.LOADED, result.status)
            assertEquals(listOf("early", "late"), result.comments.map { it.id })
            assertEquals("SoundCloud", result.sourceName)
            assertEquals(track.id, result.trackId)
            assertTrue(!result.fromCache)
        }
    }

    @Test
    fun `an empty answer is a real answer, not a failure`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(emptyList()))
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.EMPTY, result.status)
            assertTrue(result.comments.isEmpty())
        }
    }

    @Test
    fun `no matching track is reported distinctly`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.NoMatchingTrack)
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.NO_MATCH, result.status)
        }
    }

    @Test
    fun `a failure carries its reason`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Failed("429 Too Many Requests"))
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.FAILED, result.status)
            assertEquals("429 Too Many Requests", result.message)
        }
    }

    @Test
    fun `a second call is served from cache`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val repository = Harness(source).repository

            repository.commentsFor(track)
            val second = repository.commentsFor(track)

            assertEquals("the cache must save the request", 1, source.fetches)
            assertTrue(second.fromCache)
            assertEquals(CommentsStatus.LOADED, second.status)
            assertEquals(1, second.comments.size)
        }
    }

    @Test
    fun `a cached empty answer is served from cache too`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(emptyList()))
            val repository = Harness(source).repository

            repository.commentsFor(track)
            val second = repository.commentsFor(track)

            assertEquals(1, source.fetches)
            assertTrue(second.fromCache)
            assertEquals(CommentsStatus.EMPTY, second.status)
        }
    }

    @Test
    fun `a cached no-match is served from cache`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.NoMatchingTrack)
            val repository = Harness(source).repository

            repository.commentsFor(track)
            val second = repository.commentsFor(track)

            assertEquals("a no-match is a statement about the track, so it is worth remembering", 1, source.fetches)
            assertTrue(second.fromCache)
            assertEquals(CommentsStatus.NO_MATCH, second.status)
        }
    }

    @Test
    fun `a failure is never cached`() {
        runBlocking {
            val harness = Harness(FakeSource(result = CommentsOutcome.Failed("timeout")))

            harness.repository.commentsFor(track)
            assertTrue("caching a failure would freeze a transient problem in place", harness.storage.entries.isEmpty())

            // And the next call tries again, which is the whole point.
            harness.repository.commentsFor(track)
        }
    }

    @Test
    fun `not-configured is never cached`() {
        runBlocking {
            val harness = Harness(FakeSource(configured = false))
            harness.repository.commentsFor(track)
            assertTrue(harness.storage.entries.isEmpty())
        }
    }

    @Test
    fun `force refresh bypasses and overwrites the cache`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val repository = Harness(source).repository

            repository.commentsFor(track)
            source.respondWith(CommentsOutcome.Found(listOf(comment("c1", 1_000), comment("c2", 2_000))))
            val refreshed = repository.commentsFor(track, forceRefresh = true)

            assertEquals(2, source.fetches)
            assertTrue(!refreshed.fromCache)
            assertEquals(2, refreshed.comments.size)

            // The fresh answer is what the cache now holds.
            val after = repository.commentsFor(track)
            assertTrue(after.fromCache)
            assertEquals(2, after.comments.size)
        }
    }

    @Test
    fun `the first source that answers wins`() {
        runBlocking {
            val first = FakeSource(CommentSource.AUDIUS, name = "first", result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val second = FakeSource(CommentSource.YOUTUBE, name = "second", result = CommentsOutcome.Found(listOf(comment("c2", 2_000))))
            val result = Harness(first, second).repository.commentsFor(track)

            assertEquals("first", result.sourceName)
            assertEquals(listOf("c1"), result.comments.map { it.id })
            assertEquals("a source behind the winner is never asked", 0, second.fetches)
        }
    }

    @Test
    fun `a failing source falls through to the next one`() {
        runBlocking {
            val first = FakeSource(CommentSource.AUDIUS, name = "first", result = CommentsOutcome.Failed("timeout"))
            val second = FakeSource(CommentSource.YOUTUBE, name = "second", result = CommentsOutcome.Found(listOf(comment("c2", 2_000))))
            val result = Harness(first, second).repository.commentsFor(track)

            assertEquals(CommentsStatus.LOADED, result.status)
            assertEquals("second", result.sourceName)
            assertEquals(1, second.fetches)
        }
    }

    @Test
    fun `a source that reports itself unconfigured mid-flight is skipped`() {
        runBlocking {
            // Configured at the gate check, then NotConfigured from the fetch — the race with a settings
            // change. Must not be treated as an answer.
            val first = FakeSource(CommentSource.AUDIUS, name = "first", result = CommentsOutcome.NotConfigured)
            val second = FakeSource(CommentSource.YOUTUBE, name = "second", result = CommentsOutcome.Found(listOf(comment("c2", 2_000))))
            val result = Harness(first, second).repository.commentsFor(track)

            assertEquals("second", result.sourceName)
            assertEquals(CommentsStatus.LOADED, result.status)
        }
    }

    @Test
    fun `a source that throws cannot break the call`() {
        runBlocking {
            val source = FakeSource(throws = true)
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(CommentsStatus.FAILED, result.status)
            assertEquals(1, source.fetches)
        }
    }

    @Test
    fun `an entry written by a newer build is treated as a miss`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val harness = Harness(source)
            // A kind this build does not know about: fall back to fetching rather than guessing.
            harness.storage.entries[CommentsCache.sanitize(track.cacheKey)] =
                CommentsCache.Entry(0L, "SoundCloud", "kind_from_the_future")

            val result = harness.repository.commentsFor(track)

            assertEquals(CommentsStatus.LOADED, result.status)
            assertTrue(!result.fromCache)
            assertEquals(1, source.fetches)
        }
    }

    @Test
    fun `comments out of range for the track are dropped on the way through`() {
        runBlocking {
            // 250s track; a provider that reports a timestamp past the end would make seekTo jump outside
            // the media item.
            val source = FakeSource(
                result = CommentsOutcome.Found(
                    listOf(comment("ok", 10_000), comment("past-the-end", 999_999)),
                ),
            )
            val result = Harness(source).repository.commentsFor(track)

            assertEquals(listOf("ok"), result.comments.map { it.id })
        }
    }

    @Test
    fun `the answer always names the track it is about`() {
        runBlocking {
            for (outcome in listOf(
                CommentsOutcome.Found(listOf(comment("c1", 1_000))),
                CommentsOutcome.Found(emptyList()),
                CommentsOutcome.NoMatchingTrack,
                CommentsOutcome.Failed("x"),
            )) {
                val result = Harness(FakeSource(result = outcome)).repository.commentsFor(track)
                assertEquals("for $outcome", track.id, result.trackId)
                assertNotNull(result.status)
            }
        }
    }

    @Test
    fun `different tracks do not share a cache entry`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val repository = Harness(source).repository

            repository.commentsFor(track)
            val other = repository.commentsFor(CommentTrackRef.of("other-id", "Other", listOf("Artist"), 180))

            assertEquals(2, source.fetches)
            assertEquals("other-id", other.trackId)
            assertNull(other.message)
        }
    }

    // ── source priority ────────────────────────────────────────────────────

    @Test
    fun `the priority list, not the injection order, decides who answers`() {
        runBlocking {
            val audius = FakeSource(CommentSource.AUDIUS, name = "Audius",
                result = CommentsOutcome.Found(listOf(comment("from-audius", 1_000))))
            val youTube = FakeSource(CommentSource.YOUTUBE, name = "YouTube",
                result = CommentsOutcome.Found(listOf(comment("from-youtube", 2_000))))

            val audiusFirst = Harness(audius, youTube).repository.commentsFor(track)
            val youTubeFirst = Harness(
                audius, youTube,
                order = listOf(CommentSource.YOUTUBE, CommentSource.AUDIUS),
            ).repository.commentsFor(track)

            // Same two sources, opposite answers, purely because the user ranked them differently.
            assertEquals("Audius", audiusFirst.sourceName)
            assertEquals(listOf("from-audius"), audiusFirst.comments.map { it.id })
            assertEquals("YouTube", youTubeFirst.sourceName)
            assertEquals(listOf("from-youtube"), youTubeFirst.comments.map { it.id })
        }
    }

    @Test
    fun `a source the user switched off is never asked`() {
        runBlocking {
            val audius = FakeSource(CommentSource.AUDIUS, name = "Audius",
                result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))
            val soundCloud = FakeSource(CommentSource.SOUNDCLOUD, name = "SoundCloud",
                result = CommentsOutcome.Found(listOf(comment("c2", 2_000))))

            // SoundCloud is injected and would answer, but it is not in the priority list.
            val result = Harness(audius, soundCloud, order = listOf(CommentSource.AUDIUS))
                .repository.commentsFor(track)

            assertEquals(0, soundCloud.fetches)
            assertEquals("Audius", result.sourceName)
        }
    }

    @Test
    fun `an empty priority list costs no network at all`() {
        runBlocking {
            val source = FakeSource(result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))

            val result = Harness(source, order = emptyList()).repository.commentsFor(track)

            assertEquals(CommentsStatus.UNSUPPORTED, result.status)
            assertEquals(0, source.fetches)
        }
    }

    @Test
    fun `a priority list naming a source that is not injected is skipped`() {
        runBlocking {
            // A preference written by a newer build, read by an older one. Must not crash and must not
            // strand the sources that do exist.
            val audius = FakeSource(CommentSource.AUDIUS, name = "Audius",
                result = CommentsOutcome.Found(listOf(comment("c1", 1_000))))

            val result = Harness(
                audius,
                order = listOf(CommentSource.SOUNDCLOUD, CommentSource.AUDIUS),
            ).repository.commentsFor(track)

            assertEquals(CommentsStatus.LOADED, result.status)
            assertEquals("Audius", result.sourceName)
        }
    }

    @Test
    fun `a no-match from every source is cached as a no-match`() {
        runBlocking {
            val audius = FakeSource(CommentSource.AUDIUS, result = CommentsOutcome.NoMatchingTrack)
            val youTube = FakeSource(CommentSource.YOUTUBE, result = CommentsOutcome.NoMatchingTrack)
            val repository = Harness(audius, youTube).repository

            val first = repository.commentsFor(track)
            val second = repository.commentsFor(track)

            assertEquals(CommentsStatus.NO_MATCH, first.status)
            assertEquals(CommentsStatus.NO_MATCH, second.status)
            assertTrue(second.fromCache)
            // Both were asked once; the second call is served from the negative cache.
            assertEquals(1, audius.fetches)
            assertEquals(1, youTube.fetches)
        }
    }

    @Test
    fun `a no-match is NOT cached while another source was still failing`() {
        runBlocking {
            // Audius says definitively "not on Audius". YouTube says "I could not reach the network".
            // The honest state is unknown-but-retryable, not a one-hour "nothing exists".
            val audius = FakeSource(CommentSource.AUDIUS, result = CommentsOutcome.NoMatchingTrack)
            val youTube = FakeSource(CommentSource.YOUTUBE, result = CommentsOutcome.Failed("timeout"))
            val repository = Harness(audius, youTube).repository

            val first = repository.commentsFor(track)
            assertEquals(CommentsStatus.FAILED, first.status)

            // The network recovers: the same track now answers, which the cached no-match would have
            // suppressed for an hour.
            youTube.respondWith(CommentsOutcome.Found(listOf(comment("late", 2_000))))
            val second = repository.commentsFor(track)

            assertEquals(CommentsStatus.LOADED, second.status)
            assertEquals(listOf("late"), second.comments.map { it.id })
            assertTrue(!second.fromCache)
        }
    }
}
