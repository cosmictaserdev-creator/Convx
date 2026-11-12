package com.convx.music.playback

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

data class WatchedArtist(val id: String, val name: String)

/** The artists to look at, and which of them the listener saved (only those are announced). */
data class WatchedArtists(val artists: List<WatchedArtist>, val bookmarkedIds: Set<String>)

interface ReleaseArtistSource {
    suspend fun watchedArtists(nowEpochMs: Long): WatchedArtists

    /** The releases on the artist's page, or null when the page could not be loaded. */
    suspend fun releases(artistId: String): List<SeenRelease>?
}

interface ReleaseDateSource {
    /** Release dates by title for [artistName], empty when none can be found. */
    suspend fun releaseDates(artistName: String): Map<String, String>
}

interface ReleaseStore {
    suspend fun read(): ReleaseRadarSnapshot

    suspend fun save(snapshot: ReleaseRadarSnapshot)
}

fun interface ReleaseAnnouncer {
    /** Shows [announcement]; returns false when it cannot be shown, for example when notifications are off. */
    fun announce(announcement: Announcement): Boolean
}

/** Looks at the listener's artists for new releases and remembers and announces them; the same on every client. */
class ReleaseRadarSync(
    private val artists: ReleaseArtistSource,
    private val dates: ReleaseDateSource,
    private val store: ReleaseStore,
    private val announcer: ReleaseAnnouncer,
    private val pageGapMs: Long = PAGE_GAP_MS,
) {
    /** Returns how many new releases were found. */
    suspend fun run(nowEpochMs: Long): Int {
        val watched = artists.watchedArtists(nowEpochMs)
        if (watched.artists.isEmpty()) return 0
        val today = nowEpochMs.floorDiv(DAY_MS)
        val thisYear = ReleaseRadarRules.yearOf(today)
        var snapshot = store.read()
        var found = 0
        for ((index, artist) in watched.artists.withIndex()) {
            currentCoroutineContext().ensureActive()
            if (index > 0) delay(pageGapMs)
            val seen = artists.releases(artist.id) ?: continue
            val known = snapshot.knownIds(artist.id)
            val fresh = seen.filter { it.id !in known }
            val rows = if (fresh.isEmpty()) {
                emptyList()
            } else {
                val releaseDates = if (fresh.any { it.year >= thisYear - 1 }) dates.releaseDates(artist.name) else emptyMap()
                ReleaseRadarPlanner.classify(
                    artist.id, artist.name, fresh, known, snapshot.isFirstLook(artist.id), releaseDates, today, nowEpochMs,
                )
            }
            snapshot = snapshot.withReleases(rows).withChecked(artist.id)
            found += rows.count { !it.baseline }
            // A look cut short keeps what it has, so artists already looked at are not given a first look again.
            if (index % FLUSH_EVERY == FLUSH_EVERY - 1) store.save(snapshot)
        }
        store.save(announce(snapshot, watched.bookmarkedIds).withRun(nowEpochMs))
        return found
    }

    private fun announce(snapshot: ReleaseRadarSnapshot, bookmarkedIds: Set<String>): ReleaseRadarSnapshot {
        val pending = snapshot.releases.filter { !it.announced && !it.baseline }
        if (pending.isEmpty()) return snapshot
        ReleaseRadarPlanner.announcement(pending, bookmarkedIds)?.let(announcer::announce)
        return snapshot.withAnnounced(pending.mapTo(HashSet()) { it.id })
    }

    private companion object {
        const val DAY_MS = 86_400_000L
        const val PAGE_GAP_MS = 1_000L
        const val FLUSH_EVERY = 10
    }
}
