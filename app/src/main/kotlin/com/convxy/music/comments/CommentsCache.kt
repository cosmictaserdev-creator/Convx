/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Per-track comment cache: a small in-memory LRU in front of one JSON file per track.
 *
 * Why not Room: `song.db` is at schema version 38 with a hand-maintained migration chain, and
 * comments are re-fetchable, per-track, expiring blobs — the one kind of data where a migration bug
 * costs a user's whole library and the data itself is worth nothing. So this follows the persistence
 * pattern the app already uses for exactly this shape of thing (`EQProfileRepository`'s serialised
 * store, `UpdateStorageUtils`' file dir, the updater's cached release metadata) rather than adding a
 * table. Nothing new is introduced, and nothing that already exists can be broken by it.
 *
 * Files live under `cacheDir`, so the OS can reclaim them under storage pressure and a cleared cache
 * is a correct, non-destructive outcome.
 */
class CommentsCache(
    private val storage: CommentCacheStorage,
    private val nowMillis: () -> Long = System::currentTimeMillis,
    private val memoryEntries: Int = DEFAULT_MEMORY_ENTRIES,
) {

    /** What actually goes on disk. [kind] is a plain tag so the format can evolve without a schema. */
    @Serializable
    data class Entry(
        val fetchedAtEpochMs: Long,
        val sourceName: String,
        val kind: String,
        val comments: List<TimestampedComment> = emptyList(),
    ) {
        fun age(nowMs: Long) = nowMs - fetchedAtEpochMs
    }

    private val memory = object : LinkedHashMap<String, Entry>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>?) =
            size > memoryEntries
    }

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    /**
     * A cached answer that is still within its TTL, or null.
     *
     * Negative results expire much sooner than positive ones: "this track has no comments" is worth
     * believing for hours, but "we could not match this track" is a statement about a search that
     * may simply have been unlucky, and should be retried fairly quickly.
     */
    fun read(track: CommentTrackRef): Entry? {
        val key = keyFor(track)
        val entry = synchronized(memory) { memory[key] } ?: storage.read(key)?.also {
            synchronized(memory) { memory[key] = it }
        } ?: return null

        val ttl = if (entry.kind == KIND_FOUND) TTL_FOUND_MS else TTL_NEGATIVE_MS
        if (entry.age(nowMillis()) > ttl) {
            synchronized(memory) { memory.remove(key) }
            storage.delete(key)
            return null
        }
        return entry
    }

    /** Stores a fetch result. [CommentsOutcome.NotConfigured] and [Failed] are never cached. */
    fun write(track: CommentTrackRef, outcome: CommentsOutcome, sourceName: String) {
        val entry = when (outcome) {
            is CommentsOutcome.Found ->
                if (outcome.comments.isEmpty()) {
                    Entry(nowMillis(), sourceName, KIND_EMPTY)
                } else {
                    Entry(nowMillis(), sourceName, KIND_FOUND, outcome.comments)
                }
            CommentsOutcome.NoMatchingTrack -> Entry(nowMillis(), sourceName, KIND_NO_MATCH)
            // Both of these are statements about the local setup or about a transient failure, not
            // about the track. Caching either would freeze a fixable problem in place.
            CommentsOutcome.NotConfigured, is CommentsOutcome.Failed -> return
        }
        val key = keyFor(track)
        synchronized(memory) { memory[key] = entry }
        storage.write(key, entry)
    }

    /** Drops the cached entry for one track — used by "refresh" after a manual retry. */
    fun invalidate(track: CommentTrackRef) {
        val key = keyFor(track)
        synchronized(memory) { memory.remove(key) }
        storage.delete(key)
    }

    /**
     * Drops every entry. Called when the user reorders or retoggles comment sources.
     *
     * Without it a priority change would look like it did nothing: a track answered by the previous
     * first choice stays cached for up to twelve hours, so the source the user just promoted would be
     * asked about new tracks and silently skipped for everything they had already opened. Invalidating
     * on the settings change is cheaper than stamping every entry with the ordering that produced it,
     * and the cost is a handful of refetches the user has effectively asked for.
     */
    fun clear() {
        synchronized(memory) { memory.clear() }
        storage.deleteAll()
    }

    private fun keyFor(track: CommentTrackRef) = sanitize(track.cacheKey)

    private val serializer get() = Entry.serializer()

    internal fun encode(entry: Entry): String = json.encodeToString(serializer, entry)

    internal fun decode(raw: String): Entry? = runCatching { json.decodeFromString(serializer, raw) }.getOrNull()

    companion object {
        const val KIND_FOUND = "found"
        const val KIND_EMPTY = "empty"
        const val KIND_NO_MATCH = "no_match"

        /** 12h. Comments accumulate slowly; re-fetching on every player open would be wasteful. */
        const val TTL_FOUND_MS = 12L * 60 * 60 * 1000

        /** 1h for "no comments" / "no match" — cheap to re-check, and worth re-checking. */
        const val TTL_NEGATIVE_MS = 60L * 60 * 1000

        const val DEFAULT_MEMORY_ENTRIES = 24

        /** Filenames come from track ids we do not control, so strip anything path-like. */
        fun sanitize(raw: String): String = buildString(raw.length) {
            for (c in raw) append(if (c.isLetterOrDigit() || c == '-' || c == '_') c else '_')
        }
    }
}

/**
 * Where cache entries live. Split out from [CommentsCache] so the TTL/LRU/serialisation logic above
 * is testable against an in-memory fake instead of a device filesystem.
 */
interface CommentCacheStorage {
    fun read(key: String): CommentsCache.Entry?
    fun write(key: String, entry: CommentsCache.Entry)
    fun delete(key: String)

    /** Drops every entry. See [CommentsCache.clear]. */
    fun deleteAll()
}

/** One `<key>.json` per track under [dir]. Corrupt or unreadable entries are treated as misses. */
class FileCommentCacheStorage(dir: File) : CommentCacheStorage {

    private val directory = dir
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    private fun fileFor(key: String) = File(directory, "$key.json")

    override fun read(key: String): CommentsCache.Entry? {
        val file = fileFor(key)
        if (!file.isFile) return null
        return runCatching {
            json.decodeFromString(CommentsCache.Entry.serializer(), file.readText())
        }.getOrNull()
    }

    override fun write(key: String, entry: CommentsCache.Entry) {
        runCatching {
            if (!directory.exists()) directory.mkdirs()
            // Write-then-rename so a process kill mid-write cannot leave a half-written entry that
            // reads back as a cache miss forever after.
            val target = fileFor(key)
            val tmp = File(directory, "$key.tmp")
            tmp.writeText(json.encodeToString(CommentsCache.Entry.serializer(), entry))
            if (target.exists()) target.delete()
            tmp.renameTo(target)
        }
    }

    override fun delete(key: String) {
        runCatching { fileFor(key).takeIf { it.exists() }?.delete() }
    }

    override fun deleteAll() {
        // Includes the `.tmp` files write-then-rename can leave behind if the process was killed
        // mid-write: they are orphans no key will ever name again, so this is the only sweep that
        // would ever collect them.
        runCatching { directory.listFiles()?.forEach { file -> runCatching { file.delete() } } }
    }
}
