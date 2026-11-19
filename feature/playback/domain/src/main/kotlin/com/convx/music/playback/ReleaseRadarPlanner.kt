package com.convx.music.playback

import com.convx.music.playback.ReleaseRadarRules.Kind
import kotlinx.serialization.Serializable

data class SeenRelease(val id: String, val title: String, val kind: Kind, val year: Int, val thumbnailUrl: String? = null)

@Serializable
data class TrackedRelease(
    val id: String,
    val artistId: String,
    val artistName: String,
    val title: String,
    val kind: Kind,
    val year: Int,
    val releaseEpochDay: Long?,
    val firstSeenEpochMs: Long,
    /** Part of an artist's back catalogue found on a first look: kept, never shown as new. */
    val baseline: Boolean,
    val announced: Boolean,
    val thumbnailUrl: String? = null,
)

data class Announcement(val title: String, val text: String)

data class RadarTrack(val songId: String, val plays: Long)

data class RadarRelease(val release: TrackedRelease, val tracks: List<RadarTrack>)

/** Release Radar's decisions apart from fetching: what is new, how it is announced, and what the weekly playlist holds. */
object ReleaseRadarPlanner {
    const val MAX_PLAYLIST_SONGS = 40
    private const val ALBUM_HIGHLIGHTS = 3
    private const val STILL_NEWS_DAYS = 30

    /**
     * Turns the releases on an artist's page that were not seen before into tracked ones. A first look at an artist
     * keeps its back catalogue quietly, unless a release came out within the last month; everything found on a first
     * look is shown but never announced.
     */
    fun classify(
        artistId: String,
        artistName: String,
        seen: List<SeenRelease>,
        knownIds: Set<String>,
        firstLook: Boolean,
        deezerDates: Map<String, String>,
        todayEpochDay: Long,
        nowEpochMs: Long,
    ): List<TrackedRelease> = seen
        .filter { it.id !in knownIds }
        .distinctBy { it.id }
        .map { release ->
            val date = DeezerDates.dateFor(deezerDates, release.title)
                ?.takeIf { release.year == 0 || it.startsWith(release.year.toString()) }
            val epochDay = date?.let(ReleaseRadarRules::epochDayOf)
            val recentlyOut = epochDay != null && ReleaseRadarRules.isRecent(epochDay, epochDay, todayEpochDay, STILL_NEWS_DAYS)
            TrackedRelease(
                id = release.id,
                artistId = artistId,
                artistName = artistName,
                title = release.title,
                kind = release.kind,
                year = release.year,
                releaseEpochDay = epochDay,
                firstSeenEpochMs = nowEpochMs,
                baseline = firstLook && !recentlyOut,
                announced = firstLook,
                thumbnailUrl = release.thumbnailUrl,
            )
        }

    /** One notification for releases by starred artists that were not announced yet; null when there is nothing to say. */
    fun announcement(unannounced: List<TrackedRelease>, starredArtistIds: Set<String>): Announcement? {
        val worth = unannounced.filter { !it.announced && !it.baseline && it.artistId in starredArtistIds }
        val names = worth.map { it.artistName.trim() }.filter { it.isNotEmpty() }.distinct()
        if (names.isEmpty()) return null
        val title = when (names.size) {
            1 -> "New from ${names[0]}"
            2 -> "New from ${names[0]} and ${names[1]}"
            else -> "New from ${names[0]}, ${names[1]} and ${names.size - 2} more"
        }
        val text = worth.take(4).joinToString(" · ") { "${it.title} (${it.kind.label.lowercase()})" }
        return Announcement(title, text)
    }

    /**
     * Song ids for the weekly playlist: every song of new singles and EPs, the [ALBUM_HIGHLIGHTS] most played of new
     * albums, no repeats, at most [limit]. Releases are taken in the order given.
     */
    fun playlistSongs(releases: List<RadarRelease>, limit: Int = MAX_PLAYLIST_SONGS): List<String> {
        val songs = LinkedHashSet<String>()
        for ((release, tracks) in releases) {
            if (songs.size >= limit) break
            val picked = if (release.kind == Kind.ALBUM) tracks.sortedByDescending { it.plays }.take(ALBUM_HIGHLIGHTS) else tracks
            for (track in picked) if (songs.size < limit) songs += track.songId
        }
        return songs.toList()
    }

    /** The Friday on or before [epochDay], where the playlist's week starts (most music comes out on Fridays). */
    fun weekStart(epochDay: Long): Long {
        val mondayBased = (epochDay + 3).mod(7L)
        return epochDay - (mondayBased - FRIDAY).mod(7L)
    }

    private const val FRIDAY = 4L
}

/** An artist's release dates from Deezer, looked up by exact then loose title. */
object DeezerDates {
    /** [albums] are (title, release date). Editions sharing a loose title keep the earliest date; invalid dates are dropped. */
    fun index(albums: List<Pair<String, String>>): Map<String, String> {
        val dates = HashMap<String, String>()
        for ((title, date) in albums) {
            if (ReleaseRadarRules.epochDayOf(date) == null) continue
            dates.merge("=" + exact(title), date) { old, new -> minOf(old, new) }
            val loose = ReleaseRadarRules.normalize(title)
            if (loose.isNotEmpty()) dates.merge(loose, date) { old, new -> minOf(old, new) }
        }
        return dates
    }

    fun dateFor(dates: Map<String, String>, title: String): String? =
        dates["=" + exact(title)] ?: dates[ReleaseRadarRules.normalize(title)]

    private fun exact(title: String) = title.lowercase().filter(Char::isLetterOrDigit)
}
