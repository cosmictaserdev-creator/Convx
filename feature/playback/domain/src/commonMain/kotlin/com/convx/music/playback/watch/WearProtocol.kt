package com.convx.music.playback.watch

import com.convx.music.playback.PlaybackController
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WearProtocol {
    const val VERSION = 1
    const val PHONE_CAPABILITY = "convx_phone"
    const val WATCH_CAPABILITY = "convx_watch"
    const val STATE_PATH = "/convx/watch/state"
    const val COMMAND_PATH = "/convx/watch/command"
    const val STATE_KEY = "state"
    const val MAX_STATE_BYTES = 56 * 1024
    const val MAX_COMMAND_BYTES = 4 * 1024
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun decodeCommand(bytes: ByteArray): WearCommand? = try {
        if (bytes.size > MAX_COMMAND_BYTES) null else json.decodeFromString<WearCommand>(bytes.decodeToString())
            .takeIf { it.version == VERSION && it.requestId.length in 1..128 }
    } catch (_: IllegalArgumentException) { null }

    fun decodeState(bytes: ByteArray): WearSnapshot? = try {
        if (bytes.size > MAX_STATE_BYTES) null else json.decodeFromString<WearSnapshot>(bytes.decodeToString())
            .takeIf { it.version == VERSION && it.sessionId.isNotBlank() && it.sequence >= 0 }
    } catch (_: IllegalArgumentException) { null }

    fun encodeState(snapshot: WearSnapshot): ByteArray {
        var bounded = snapshot.copy(sessionId = snapshot.sessionId.take(128), replyTo = snapshot.replyTo?.take(128),
            mediaId = snapshot.mediaId?.take(2_048), title = snapshot.title.take(256), artist = snapshot.artist.take(512),
            lyricsProvider = snapshot.lyricsProvider?.take(128),
            lyrics = snapshot.lyrics.take(15).map { it.copy(text = it.text.take(1_024),
                words = it.words.take(128).map { word -> word.copy(text = word.text.take(256)) }) })
        var encoded = json.encodeToString(bounded).encodeToByteArray()
        while (encoded.size > MAX_STATE_BYTES && bounded.lyrics.isNotEmpty()) {
            bounded = bounded.copy(lyrics = bounded.lyrics.dropLast(1))
            encoded = json.encodeToString(bounded).encodeToByteArray()
        }
        require(encoded.size <= MAX_STATE_BYTES)
        return encoded
    }
}

@Serializable
enum class WearAction { SYNC, PLAY, PAUSE, NEXT, PREVIOUS, SEEK, VOLUME, SHUFFLE, REPEAT }

@Serializable
data class WearCommand(
    val version: Int = WearProtocol.VERSION,
    val requestId: String,
    val action: WearAction,
    val sessionId: String? = null,
    val mediaId: String? = null,
    val positionMs: Long? = null,
    val volume: Float? = null,
    val shuffle: Boolean? = null,
    val repeatMode: PlaybackRepeatMode? = null,
)

@Serializable
data class WearWord(val startMs: Long, val endMs: Long, val text: String)

@Serializable
data class WearLyricLine(val timeMs: Long, val text: String, val words: List<WearWord> = emptyList(), val background: Boolean = false)

@Serializable
data class WearSnapshot(
    val version: Int = WearProtocol.VERSION,
    val sessionId: String,
    val sequence: Long,
    val replyTo: String? = null,
    val mediaId: String? = null,
    val title: String = "",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long? = null,
    val speed: Float = 1f,
    val canSkipPrevious: Boolean = false,
    val canSkipNext: Boolean = false,
    val canControl: Boolean = true,
    val canAdjustVolume: Boolean = true,
    val volume: Float = 1f,
    val shuffle: Boolean = false,
    val repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.OFF,
    val lyricsProvider: String? = null,
    val lyricsSynced: Boolean = true,
    val lyricsOffsetMs: Long = 0,
    val lyrics: List<WearLyricLine> = emptyList(),
) {
    fun positionAfter(elapsedMs: Long): Long {
        val advance = if (isPlaying && speed.isFinite()) (elapsedMs.coerceIn(0, 5_000) * speed.coerceIn(0f, 4f)).toLong() else 0
        val ceiling = durationMs?.takeIf { it > 0 } ?: Long.MAX_VALUE
        val base = positionMs.coerceIn(0, ceiling)
        return base + advance.coerceAtMost(ceiling - base)
    }

    fun activeLyric(position: Long): Int = if (lyricsSynced) lyrics.indexOfLast {
        !it.background && it.timeMs <= position + lyricsOffsetMs
    } else -1
}

class WearCommandRouter(
    private val sessionId: String,
    private val playback: PlaybackController,
    private val setVolume: (Float) -> Unit,
) {
    private val handled = linkedSetOf<Pair<String, String>>()

    fun receive(node: String, command: WearCommand, canControl: Boolean, canAdjustVolume: Boolean): Boolean {
        if (command.version != WearProtocol.VERSION || command.requestId.length !in 1..128) return false
        if (command.action == WearAction.SYNC) return true
        val state: PlaybackSnapshot = playback.playbackSnapshot.value
        if (!canControl || command.sessionId != sessionId || command.mediaId != state.track?.id || state.track == null) return false
        val identity = node to command.requestId
        if (identity in handled) return false
        when (command.action) {
            WearAction.PLAY -> playback.play()
            WearAction.PAUSE -> playback.pause()
            WearAction.NEXT -> if (state.canSkipNext) playback.requestSkip(true) else return false
            WearAction.PREVIOUS -> if (state.canSkipPrevious) playback.requestSkip(false) else return false
            WearAction.SEEK -> {
                val duration = state.durationMs?.takeIf { it > 0 } ?: return false
                val target = command.positionMs ?: return false
                playback.seekTo(target.coerceIn(0, duration))
            }
            WearAction.VOLUME -> {
                val volume = command.volume?.takeIf { it.isFinite() } ?: return false
                if (!canAdjustVolume) return false
                setVolume(volume.coerceIn(0f, 1f))
            }
            WearAction.SHUFFLE -> playback.setShuffleEnabled(command.shuffle ?: return false)
            WearAction.REPEAT -> playback.setRepeatMode(command.repeatMode ?: return false)
            WearAction.SYNC -> Unit
        }
        handled += identity
        if (handled.size > 128) handled.remove(handled.first())
        return true
    }
}
