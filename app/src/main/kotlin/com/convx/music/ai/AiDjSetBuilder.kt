/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import android.content.Context
import androidx.media3.common.MediaItem
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongAnalysisEntity
import com.convx.music.db.entities.SongEntity
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.audio.DjSetOrderer
import com.convx.music.playback.dj.BeatGrid
import com.convx.music.playback.dj.MusicalKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

/** Maximum number of local songs included as AI candidates. */
private const val MAX_LIBRARY_CANDIDATES = 150

/** An AI-selected song and its position in a concert set. */
private data class ChosenSong(val title: String, val artist: String, val role: String)

/** Playback order for roles in a concert set. */
private val CONCERT_ROLES = listOf("opener", "rising", "peak", "closer")

data class AiDjSetResult(
    val playlistName: String,
    val orderedTitles: List<String>,
    val unmatchedTitles: List<String>,
    /** Description of how this set was generated. */
    val method: String,
    /** Song ids in playback order. */
    val orderedIds: List<String>,
    /** Resolved YouTube songs, keyed by id; excludes the local seed song. */
    val songsById: Map<String, SongItem>,
    /** Local library song used as the seed, if any. */
    val seedSong: Song?,
)

/**
 * Builds a set from a prompt or seed song. Prompted and concert sets use AI;
 * a seed-only set uses related songs. Concert mode preserves the AI's role
 * order, while other sets use [DjSetOrderer]. Saving is a separate operation.
 */
object AiDjSetBuilder {

    /** Assumed track length, used to estimate how many songs meet a duration target. */
    private const val ASSUMED_AVERAGE_SONG_SECONDS = 210

    /** Builds a set from a prompt, seed song, or both. Without a prompt, non-concert mode uses related songs directly. */
    suspend fun buildSet(
        context: Context,
        database: MusicDatabase,
        prompt: String?,
        seedSongId: String?,
        songCount: Int,
        /** Target set duration in seconds. When set, this determines the song count. */
        targetDurationSeconds: Long? = null,
        /** Keep the AI's role order for a concert set. */
        concertMode: Boolean = false,
        onLog: suspend (String) -> Unit,
    ): AiDjSetResult? = withContext(Dispatchers.IO) {
        val effectiveSongCount = targetDurationSeconds
            ?.let { (it / ASSUMED_AVERAGE_SONG_SECONDS).toInt().coerceAtLeast(1) }
            ?: songCount
        val trimmedPrompt = prompt?.trim().orEmpty()
        if (trimmedPrompt.isEmpty() && seedSongId.isNullOrBlank()) {
            onLog("Need a vibe or a song to start from.")
            return@withContext null
        }

        onLog(
            "Gathering candidates... (prompt=${if (trimmedPrompt.isEmpty()) "none" else "\"$trimmedPrompt\""}, " +
                "seed=${seedSongId ?: "none"})"
        )
        // The seed is already local; fetch it directly instead of relying on related results.
        val seedSong = if (!seedSongId.isNullOrBlank()) database.song(seedSongId).first() else null
        val related = findSeedRelatedSongs(seedSongId, seedSong, onLog)

        // Concert mode uses the seed artist as its theme when the prompt is blank.
        val conceptLabel = trimmedPrompt.ifBlank {
            seedSong?.artists?.firstOrNull()?.name ?: seedSong?.song?.title.orEmpty()
        }
        val useAi = trimmedPrompt.isNotEmpty() || (concertMode && conceptLabel.isNotBlank())

        if (concertMode && conceptLabel.isNotBlank()) {
            // Search the concert theme directly to give the AI relevant candidates.
            YouTube.search(conceptLabel, SearchFilter.FILTER_SONG).getOrNull()
                ?.items?.filterIsInstance<SongItem>()
                ?.forEach { if (it.id != seedSongId) related.putIfAbsent(it.id, it) }
        }

        val chosenSongs = mutableListOf<ChosenSong>()
        var usedAi = false

        if (useAi) {
            onLog(
                if (concertMode) "Asking AI to design a concert set for \"$conceptLabel\"..."
                else "Asking AI to pick songs for the vibe..."
            )
            val librarySongs = database.allSearchableSongs().first().take(MAX_LIBRARY_CANDIDATES)
            val poolJson = buildCandidatePool(related.values, librarySongs)

            val systemPrompt = buildAiPrompt(conceptLabel, poolJson, effectiveSongCount, concertMode)

            val response = AiChatClient.complete(context, systemPrompt, conceptLabel, onLog)
            chosenSongs += parseAiResponse(response, onLog)
            usedAi = chosenSongs.isNotEmpty()
            if (usedAi) {
                onLog("AI picked: " + chosenSongs.joinToString { "${it.title} — ${it.artist} [${it.role}]" })
            }
        }

        val resolved = LinkedHashMap<String, SongItem>()
        val roleById = mutableMapOf<String, String>()
        val unmatched = mutableListOf<String>()

        if (usedAi && chosenSongs.isNotEmpty()) {
            onLog("Resolving ${chosenSongs.size} songs...")
            val results = resolveChosenSongs(chosenSongs, related.values, onLog)
            results.forEach { (chosen, item) ->
                if (item != null) {
                    resolved[item.id] = item
                    roleById[item.id] = chosen.role
                } else {
                    unmatched += chosen.title
                }
            }
        } else {
            addRelatedSongs(resolved, related.values, targetDurationSeconds, effectiveSongCount)
            onLog("Using ${resolved.size} related songs directly (no vibe given).")
        }

        if (resolved.isEmpty()) {
            onLog("No songs found.")
            return@withContext null
        }

        val method = when {
            concertMode && usedAi -> "AI-designed concert set for \"$conceptLabel\" (opener, rising, peak, closer)"
            usedAi && trimmedPrompt.isNotEmpty() -> "AI-curated from your vibe"
            seedSong != null -> "Related songs for \"${seedSong.song.title}\", no AI involved"
            else -> "Related songs, no AI involved"
        }

        onLog("Ordering for the mix...")
        val orderedIds = orderSongs(
            database = database,
            songIds = resolved.keys,
            roleById = roleById,
            concertMode = concertMode,
            seedSongId = seedSongId,
        )
        val seedTitle = seedSong?.song?.title

        val name = playlistName(
            concertMode = concertMode,
            conceptLabel = conceptLabel,
            prompt = trimmedPrompt,
            seedTitle = seedTitle,
        )

        onLog("Done! Built \"$name\" with ${orderedIds.size} songs.")
        AiDjSetResult(
            playlistName = name,
            orderedTitles = orderedIds.map { id -> resolved[id]?.title ?: seedTitle.takeIf { id == seedSongId } ?: id },
            unmatchedTitles = unmatched,
            method = method,
            orderedIds = orderedIds,
            songsById = resolved,
            seedSong = seedSong,
        )
    }

    private fun addRelatedSongs(
        target: MutableMap<String, SongItem>,
        relatedSongs: Collection<SongItem>,
        targetDurationSeconds: Long?,
        songCount: Int,
    ) {
        if (targetDurationSeconds == null) {
            relatedSongs.take(songCount).forEach { target[it.id] = it }
            return
        }

        var totalSeconds = 0L
        for (song in relatedSongs) {
            if (totalSeconds >= targetDurationSeconds) break
            target[song.id] = song
            totalSeconds += song.duration ?: ASSUMED_AVERAGE_SONG_SECONDS
        }
    }

    private fun playlistName(
        concertMode: Boolean,
        conceptLabel: String,
        prompt: String,
        seedTitle: String?,
    ): String = when {
        concertMode -> "Concert: $conceptLabel"
        prompt.isNotEmpty() && seedTitle != null -> "AI DJ Mix: $seedTitle + $prompt"
        prompt.isNotEmpty() -> "AI DJ Mix: $prompt"
        seedTitle != null -> "AI DJ Mix: $seedTitle"
        else -> "AI DJ Mix"
    }.take(100)

    private suspend fun orderSongs(
        database: MusicDatabase,
        songIds: Collection<String>,
        roleById: Map<String, String>,
        concertMode: Boolean,
        seedSongId: String?,
    ): List<String> {
        val ids = songIds.toMutableList()
        if (seedSongId != null) {
            ids.remove(seedSongId)
            ids.add(0, seedSongId)
        }

        if (concertMode && roleById.isNotEmpty()) {
            return orderConcertSongs(ids, roleById, seedSongId)
        }

        val analysisRows = database.songAnalyses(ids, SongAnalysisEntity.CURRENT_ANALYSIS_VERSION)
        val analysisById = analysisRows.associate { row ->
            row.id to (
                BeatGrid(row.bpm, row.anchorBeatMs, row.bpmConfidence) to
                    MusicalKey(row.keyIndex, row.keyIsMinor, row.keyConfidence)
                )
        }
        return DjSetOrderer.order(ids, analysisById)
    }

    /** Keeps the AI's concert arc and appends songs with unrecognized or missing roles. */
    internal fun orderConcertSongs(
        ids: List<String>,
        roleById: Map<String, String>,
        seedSongId: String?,
    ): List<String> {
        // BPM and key ordering would break the concert arc chosen by the AI.
        val ordered = listOfNotNull(seedSongId) + CONCERT_ROLES.flatMap { role ->
            ids.filter { it != seedSongId && roleById[it] == role }
        }
        val orderedSet = ordered.toHashSet()
        return ordered + ids.filterNot(orderedSet::contains)
    }

    private suspend fun resolveChosenSongs(
        chosenSongs: List<ChosenSong>,
        relatedSongs: Collection<SongItem>,
        onLog: suspend (String) -> Unit,
    ): List<Pair<ChosenSong, SongItem?>> = coroutineScope {
        val semaphore = Semaphore(4)
        val completedCount = AtomicInteger(0)

        // awaitAll preserves the AI's order even when searches finish out of order.
        chosenSongs.map { chosen ->
            async {
                semaphore.withPermit {
                    val relatedMatch = relatedSongs.firstOrNull {
                        it.title.equals(chosen.title, ignoreCase = true)
                    }
                    val song = relatedMatch ?: run {
                        val query = if (chosen.artist.isBlank()) {
                            chosen.title
                        } else {
                            "${chosen.title} ${chosen.artist}"
                        }
                        YouTube.search(query, SearchFilter.FILTER_SONG).getOrNull()
                            ?.items?.filterIsInstance<SongItem>()?.firstOrNull()
                    }

                    val completed = completedCount.incrementAndGet()
                    onLog("Resolving [$completed/${chosenSongs.size}]: ${chosen.title}")
                    chosen to song
                }
            }
        }.awaitAll()
    }

    private suspend fun findSeedRelatedSongs(
        seedSongId: String?,
        seedSong: Song?,
        onLog: suspend (String) -> Unit,
    ): LinkedHashMap<String, SongItem> {
        val related = LinkedHashMap<String, SongItem>()
        if (seedSongId.isNullOrBlank()) return related

        val endpoint = YouTube.next(WatchEndpoint(videoId = seedSongId))
            .getOrNull()
            ?.relatedEndpoint
        if (endpoint != null) {
            val page = YouTube.related(endpoint)
            page.onFailure { onLog("Related songs request failed: ${it.message}") }
            page.getOrNull()?.songs?.forEach { song ->
                if (song.id != seedSongId) related[song.id] = song
            }
        }

        if (related.isEmpty()) {
            // If there are no related results, search for more songs by the seed artist.
            val artist = seedSong?.artists?.firstOrNull()?.name
            if (!artist.isNullOrBlank()) {
                onLog("No related songs for the seed — searching more by $artist instead.")
                YouTube.search(artist, SearchFilter.FILTER_SONG).getOrNull()
                    ?.items?.filterIsInstance<SongItem>()
                    ?.forEach { song ->
                        if (song.id != seedSongId) related[song.id] = song
                    }
            }
        }

        onLog("Found ${related.size} candidates from the seed.")
        return related
    }

    private fun buildAiPrompt(
        conceptLabel: String,
        poolJson: String,
        songCount: Int,
        concertMode: Boolean,
    ): String = if (concertMode) {
                """
                    You are a knowledgeable concert-setlist designer with real,
                    accurate knowledge of "$conceptLabel"'s discography and how
                    their live shows are actually structured. Research this
                    from what you genuinely know — don't guess or pad with
                    generic genre filler.

                    Rules:
                    - Prefer this artist's own real songs (or, if this is a
                      genre/vibe rather than one artist, songs that genuinely
                      fit it) — not merely similar artists or a loose radio mix.
                    - Where it's true to how this artist/genre actually performs
                      live, you may include a well-known remix, mashup, or VIP
                      edit instead of the plain studio version — use judgement,
                      don't force it if their real sets are mostly album cuts.
                    - Include a known walk-on/intro moment if this artist
                      genuinely opens their real shows with one.
                    - The candidates below are a hint, not a limit — use your
                      own knowledge of real songs first, and only fall back to
                      this list if you don't know better ones:
                    $poolJson

                    Design the full running order as an actual concert arc,
                    structured into these parts, in this order:
                    - opener: 1 song that eases the crowd in, or a known live intro
                    - rising: a few songs building energy
                    - peak: the biggest hits, highest energy — most of the set
                    - closer: 1-2 songs that wind the set down, or a known encore/finale

                    List the songs in the exact sequence they should play — the
                    order you output IS the running order used directly, with no
                    reordering afterward, so put real thought into what follows
                    what for a natural energy/tempo flow, not just genre match.

                    Pick exactly $songCount songs total across all four parts.
                    Output ONLY a JSON array of objects, in that exact sequence:
                    [{"title": "...", "artist": "...", "role": "opener|rising|peak|closer"}].
                    Do NOT include markdown formatting, explanations, or any other text.
                """.trimIndent()
            } else {
                """
                    You are a DJ curating a set for the user. They describe a vibe or mood.
                    Candidate songs, prefer these when they fit (title/artist pairs):
                    $poolJson

                    Pick exactly $songCount songs that best match the requested vibe. You may
                    suggest a well-known song not in the candidate list if nothing there fits.
                    Output ONLY a JSON array of objects: [{"title": "...", "artist": "..."}].
                    Do NOT include markdown formatting, explanations, or any other text.
                """.trimIndent()
            }

    private fun buildCandidatePool(relatedSongs: Collection<SongItem>, librarySongs: List<Song>): String =
        JSONArray().apply {
            relatedSongs.forEach { song ->
                put(JSONObject().apply {
                    put("title", song.title)
                    put("artist", song.artists.joinToString { it.name })
                })
            }
            librarySongs.forEach { song ->
                put(JSONObject().apply {
                    put("title", song.song.title)
                    put("artist", song.artists.firstOrNull()?.name.orEmpty())
                })
            }
        }.toString()

    private suspend fun parseAiResponse(
        response: String?,
        onLog: suspend (String) -> Unit,
    ): List<ChosenSong> {
        val chosenSongs = mutableListOf<ChosenSong>()
        if (response == null) return chosenSongs

        try {
            val songs = JSONArray(AiRequests.jsonPart(response, '['))
            for (index in 0 until songs.length()) {
                val song = songs.optJSONObject(index) ?: continue
                val title = song.optString("title")
                if (title.isBlank()) continue

                val role = song.optString("role").lowercase().takeIf { it in CONCERT_ROLES } ?: "peak"
                chosenSongs += ChosenSong(title, song.optString("artist"), role)
            }
        } catch (e: Exception) {
            onLog("Couldn't parse the AI response, falling back to related songs.")
        }
        return chosenSongs
    }

    /** Saves a generated set as a playlist in the library. */

    suspend fun savePlaylist(database: MusicDatabase, result: AiDjSetResult): String = withContext(Dispatchers.IO) {
        val playlistId = PlaylistEntity.generatePlaylistId()
        database.insert(
            PlaylistEntity(
                id = playlistId,
                name = result.playlistName,
                bookmarkedAt = LocalDateTime.now(),
                lastUpdateTime = LocalDateTime.now(),
                thumbnailUrl = result.seedSong?.song?.thumbnailUrl ?: result.songsById.values.firstOrNull()?.thumbnail,
                isEditable = true,
            )
        )
        result.orderedIds.forEachIndexed { index, id ->
            // The seed is already in the database; only insert its playlist mapping.
            result.songsById[id]?.let { item ->
                database.upsert(
                    SongEntity(
                        id = item.id,
                        title = item.title,
                        duration = item.duration ?: 0,
                        thumbnailUrl = item.thumbnail,
                    )
                )
            }
            database.insert(PlaylistSongMap(playlistId = playlistId, songId = id, position = index))
        }
        playlistId
    }
}

/** Converts the ordered set, including its local seed, into playable queue items. */

fun AiDjSetResult.toMediaItems(): List<MediaItem> = orderedIds.mapNotNull { id ->
    songsById[id]?.toMediaItem() ?: seedSong?.takeIf { it.song.id == id }?.toMediaItem()
}
