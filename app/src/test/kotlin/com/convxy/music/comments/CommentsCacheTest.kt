package com.convxy.music.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TTL, LRU and serialisation, against an in-memory storage fake rather than a device filesystem.
 *
 * The clock is injected, so "expires after twelve hours" is asserted by moving the clock instead of
 * by sleeping — which is the only way a TTL test can be both honest and fast.
 */
class CommentsCacheTest {

    private class FakeStorage : CommentCacheStorage {
        val entries = LinkedHashMap<String, CommentsCache.Entry>()
        var reads = 0
        var writes = 0
        var deletes = 0

        override fun read(key: String): CommentsCache.Entry? {
            reads++
            return entries[key]
        }

        override fun write(key: String, entry: CommentsCache.Entry) {
            writes++
            entries[key] = entry
        }

        override fun delete(key: String) {
            deletes++
            entries.remove(key)
        }
        var deleteAlls = 0

        override fun deleteAll() {
            deleteAlls++
            entries.clear()
        }
    }

    private var now = 1_700_000_000_000L
    private lateinit var storage: FakeStorage

    private fun cache(memoryEntries: Int = CommentsCache.DEFAULT_MEMORY_ENTRIES): CommentsCache {
        storage = FakeStorage()
        return CommentsCache(storage, nowMillis = { now }, memoryEntries = memoryEntries)
    }

    private fun ref(id: String) = CommentTrackRef.of(id, "Title $id", listOf("Artist"), 200)

    private fun comment(id: String, at: Long) = TimestampedComment(
        id = id,
        trackId = "sc-1",
        authorName = "author",
        text = "body $id",
        timestampMs = at,
        sourceName = "SoundCloud",
    )

    @Test
    fun `a written result reads back`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")

        val entry = cache.read(ref("a"))
        assertNotNull(entry)
        assertEquals(CommentsCache.KIND_FOUND, entry!!.kind)
        assertEquals("SoundCloud", entry.sourceName)
        assertEquals(1, entry.comments.size)
        assertEquals("c1", entry.comments[0].id)
    }

    @Test
    fun `an empty answer is stored as its own kind`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.Found(emptyList()), "SoundCloud")
        assertEquals(CommentsCache.KIND_EMPTY, cache.read(ref("a"))!!.kind)
    }

    @Test
    fun `no-match is stored so it is not re-searched immediately`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.NoMatchingTrack, "SoundCloud")
        assertEquals(CommentsCache.KIND_NO_MATCH, cache.read(ref("a"))!!.kind)
    }

    @Test
    fun `local setup problems and transient failures are never cached`() {
        // Caching either would freeze a fixable problem in place for twelve hours.
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.NotConfigured, "SoundCloud")
        cache.write(ref("b"), CommentsOutcome.Failed("429"), "SoundCloud")

        assertNull(cache.read(ref("a")))
        assertNull(cache.read(ref("b")))
        assertEquals(0, storage.writes)
    }

    @Test
    fun `found entries expire after twelve hours and are evicted from storage`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")

        now += CommentsCache.TTL_FOUND_MS
        assertNotNull("still fresh at exactly the TTL", cache.read(ref("a")))

        now += 1
        assertNull(cache.read(ref("a")))
        assertTrue(storage.entries.isEmpty())
    }

    @Test
    fun `negative results expire after an hour, not twelve`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.NoMatchingTrack, "SoundCloud")

        now += CommentsCache.TTL_NEGATIVE_MS - 1
        assertNotNull(cache.read(ref("a")))

        now += 2
        assertNull("a no-match is worth retrying soon", cache.read(ref("a")))
    }

    @Test
    fun `invalidate drops one track and leaves the others alone`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")
        cache.write(ref("b"), CommentsOutcome.Found(listOf(comment("c2", 2_000))), "SoundCloud")

        cache.invalidate(ref("a"))

        assertNull(cache.read(ref("a")))
        assertNotNull(cache.read(ref("b")))
    }

    @Test
    fun `memory answers repeat reads without touching storage`() {
        val cache = cache()
        cache.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")
        assertEquals(1, storage.writes)

        cache.read(ref("a"))
        cache.read(ref("a"))
        assertEquals("both reads should have been served from memory", 0, storage.reads)
    }

    @Test
    fun `storage answers once memory has evicted the entry`() {
        val cache = cache(memoryEntries = 1)
        cache.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")
        cache.write(ref("b"), CommentsOutcome.Found(listOf(comment("c2", 2_000))), "SoundCloud")

        // Writing b pushed a out of a one-entry LRU; a is still on disk and still correct.
        val a = cache.read(ref("a"))
        assertNotNull(a)
        assertEquals("c1", a!!.comments[0].id)
        assertEquals(1, storage.reads)

        // And it is back in memory afterwards.
        cache.read(ref("a"))
        assertEquals(1, storage.reads)
    }

    @Test
    fun `a fresh cache reads what a previous process wrote`() {
        val first = cache()
        first.write(ref("a"), CommentsOutcome.Found(listOf(comment("c1", 1_000))), "SoundCloud")

        // Same storage, empty memory — the cold-start path.
        val second = CommentsCache(storage, nowMillis = { now })
        val entry = second.read(ref("a"))
        assertNotNull(entry)
        assertEquals("c1", entry!!.comments[0].id)
    }

    @Test
    fun `entries survive a serialisation round trip`() {
        val cache = cache()
        val entry = CommentsCache.Entry(
            fetchedAtEpochMs = now,
            sourceName = "SoundCloud",
            kind = CommentsCache.KIND_FOUND,
            comments = listOf(comment("c1", 1_000), comment("c2", 2_000)),
        )

        assertEquals(entry, cache.decode(cache.encode(entry)))
    }

    @Test
    fun `corrupt stored data reads as a miss rather than throwing`() {
        val cache = cache()
        assertNull(cache.decode("not json at all"))
        assertNull(cache.decode(""))
        assertNull(cache.decode("{\"kind\":\"found\"}")) // missing required fields
    }

    @Test
    fun `an entry written by a different build is still readable`() {
        // The kind is a plain string tag rather than an enum on the wire, precisely so a format that
        // grows a new kind does not invalidate every older entry on disk.
        val cache = cache()
        val key = CommentsCache.sanitize(ref("a").cacheKey)
        storage.entries[key] = CommentsCache.Entry(now, "SoundCloud", "kind_from_a_newer_build")

        assertEquals("kind_from_a_newer_build", cache.read(ref("a"))?.kind)
    }

    @Test
    fun `cache keys cannot escape the cache directory`() {
        val dirty = CommentsCache.sanitize("../../etc/passwd")
        assertTrue(dirty, dirty.none { it == '/' || it == '.' || it == '\\' })
        assertEquals("a-b_c1", CommentsCache.sanitize("a-b_c1"))
        assertEquals("________", CommentsCache.sanitize(":://||%%"))
    }
}
