package com.convx.music.playback

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** One counted play of a song, with enough of the song to show it again. */
@Serializable
data class HistoryPlay(
    val songId: String,
    val providerId: String,
    val title: String,
    val artists: List<String>,
    val artworkUrl: String? = null,
    val durationMs: Long? = null,
    val atEpochMs: Long,
)

/** Recent counted plays, newest last, bounded so the stored file stays small. */
@Serializable
data class PlayHistorySnapshot(val plays: List<HistoryPlay> = emptyList()) {
    fun with(play: HistoryPlay): PlayHistorySnapshot = copy(plays = (plays + play).takeLast(MAX_PLAYS))

    fun toJson(): String = JSON.encodeToString(this)

    companion object {
        const val MAX_PLAYS = 3_000
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        /** A snapshot from stored text; empty, corrupt or foreign text gives an empty snapshot. */
        fun parse(text: String?): PlayHistorySnapshot {
            if (text.isNullOrBlank()) return PlayHistorySnapshot()
            return try {
                JSON.decodeFromString(serializer(), text)
            } catch (_: SerializationException) {
                PlayHistorySnapshot()
            } catch (_: IllegalArgumentException) {
                PlayHistorySnapshot()
            }
        }
    }
}

/**
 * Decides when listening counts as a play: the song has been playing for [thresholdMs], or half its length
 * when that is shorter. Pauses do not count, and a song that jumps back to its start after counting counts again.
 */
class PlayRecorder(private val thresholdMs: Long = DEFAULT_THRESHOLD_MS) {
    private var trackId: String? = null
    private var lastAtMs = 0L
    private var wasPlaying = false
    private var lastPositionMs = 0L
    private var listenedMs = 0L
    private var counted = false

    /** Feed playback state with the current time; returns true on the call that completes a play. */
    fun onSnapshot(trackId: String?, isPlaying: Boolean, positionMs: Long, durationMs: Long?, nowMs: Long): Boolean {
        if (trackId != this.trackId) {
            this.trackId = trackId
            listenedMs = 0
            counted = false
            lastAtMs = nowMs
            wasPlaying = isPlaying
            lastPositionMs = positionMs
            return false
        }
        if (trackId == null) return false
        val elapsed = (nowMs - lastAtMs).coerceIn(0, MAX_GAP_MS)
        lastAtMs = nowMs
        if (wasPlaying) listenedMs += elapsed
        wasPlaying = isPlaying
        if (counted && positionMs < RESTART_WINDOW_MS && lastPositionMs - positionMs > RESTART_JUMP_MS) {
            counted = false
            listenedMs = 0
        }
        lastPositionMs = positionMs
        if (counted) return false
        val needed = durationMs?.takeIf { it > 0 }?.let { minOf(thresholdMs, it / 2) } ?: thresholdMs
        if (listenedMs < needed) return false
        counted = true
        return true
    }

    private companion object {
        const val DEFAULT_THRESHOLD_MS = 30_000L
        const val MAX_GAP_MS = 600_000L
        const val RESTART_WINDOW_MS = 3_000L
        const val RESTART_JUMP_MS = 10_000L
    }
}

data class DailyMix(val title: String, val subtitle: String, val plays: List<HistoryPlay>)

/** Builds the Daily Mixes shown on Home from counted plays. */
object DailyMixes {
    /** Mixes are named "Daily Mix 1", "Daily Mix 2" and so on, each with the artists it leans on. */
    fun build(history: List<HistoryPlay>, nowEpochMs: Long, mixCount: Int = DailyMixPlanner.MIX_COUNT): List<DailyMix> {
        val latestBySong = HashMap<String, HistoryPlay>()
        for (play in history) latestBySong[play.songId] = play
        val plays = history.mapNotNull { play ->
            play.artists.firstOrNull()?.takeIf { it.isNotBlank() }?.let { MadeForYou.Play(play.songId, it, play.atEpochMs) }
        }
        return DailyMixPlanner.plan(plays, nowEpochMs, mixCount = mixCount).mapIndexed { index, mix ->
            DailyMix(
                title = "Daily Mix ${index + 1}",
                subtitle = MadeForYou.namesLine(mix.artists.take(4)),
                plays = mix.songIds.mapNotNull(latestBySong::get),
            )
        }
    }
}
