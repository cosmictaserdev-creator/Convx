package com.convx.music.playback

import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Everything Release Radar remembers between looks round the listener's artists. */
@Serializable
data class ReleaseRadarSnapshot(
    val checkedArtistIds: Set<String> = emptySet(),
    val lastRunEpochMs: Long = 0,
    val playlistWeek: Long? = null,
    val releases: List<TrackedRelease> = emptyList(),
) {
    fun knownIds(artistId: String): Set<String> = releases.filter { it.artistId == artistId }.map { it.id }.toSet()

    fun isFirstLook(artistId: String) = artistId !in checkedArtistIds

    fun withChecked(artistId: String) = copy(checkedArtistIds = checkedArtistIds + artistId)

    /** Adds releases not already held; a release held already keeps its first-seen state. */
    fun withReleases(found: List<TrackedRelease>): ReleaseRadarSnapshot {
        val held = releases.mapTo(HashSet()) { it.id }
        val added = found.filter { held.add(it.id) }
        return if (added.isEmpty()) this else copy(releases = prune(releases + added))
    }

    fun withAnnounced(ids: Set<String>) =
        if (ids.isEmpty()) this else copy(releases = releases.map { if (it.id in ids) it.copy(announced = true) else it })

    fun withRun(nowEpochMs: Long) = copy(lastRunEpochMs = nowEpochMs)

    fun isDue(nowEpochMs: Long, everyDays: Int = 3) =
        lastRunEpochMs <= 0 || nowEpochMs - lastRunEpochMs > everyDays * DAY_MS

    /** Releases worth showing as new, newest first. Back catalogue found on a first look is never among them. */
    fun recent(todayEpochDay: Long, days: Int = 90): List<TrackedRelease> = releases
        .filter { !it.baseline && ReleaseRadarRules.isRecent(it.releaseEpochDay, it.firstSeenEpochMs / DAY_MS, todayEpochDay, days) }
        .sortedByDescending { it.releaseEpochDay ?: (it.firstSeenEpochMs / DAY_MS) }

    fun toJson(): String = JSON.encodeToString(this)

    private fun prune(all: List<TrackedRelease>): List<TrackedRelease> =
        if (all.size <= MAX_RELEASES) all else all.sortedByDescending { it.firstSeenEpochMs }.take(MAX_RELEASES)

    companion object {
        private const val DAY_MS = 86_400_000L
        private const val MAX_RELEASES = 3_000
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        /** A snapshot from stored text; text that is empty, corrupt or from another version gives an empty snapshot. */
        fun parse(text: String?): ReleaseRadarSnapshot {
            if (text.isNullOrBlank()) return ReleaseRadarSnapshot()
            return try {
                JSON.decodeFromString(serializer(), text)
            } catch (_: SerializationException) {
                ReleaseRadarSnapshot()
            } catch (_: IllegalArgumentException) {
                ReleaseRadarSnapshot()
            }
        }
    }
}
