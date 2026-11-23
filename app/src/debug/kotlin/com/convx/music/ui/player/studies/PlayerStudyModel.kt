/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.studies

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.convx.music.R

/** The order follows the eight source references. Each palette carries across all four views. */
internal enum class PlayerStudy(
    val title: String,
    val direction: String,
    val paper: Long,
    val ink: Long,
    val quiet: Long,
    val panel: Long,
    val accent: Long,
    val onAccent: Long,
    val radius: Int,
    val type: FontFamily = FontFamily.SansSerif,
) {
    Tachometer("Tachometer", "An orange instrument panel with an offset playback gauge.",
        0xFFFF542A, 0xFF191510, 0xFF502419, 0xFFFF7857, 0xFF191510, 0xFFFFE5D4, 5, FontFamily.Monospace),
    Reels("Reels", "Twin tape reels and an olive-green LCD with a waveform readout.",
        0xFFC7D1AC, 0xFF1D291E, 0xFF46543F, 0xFFD5DEBC, 0xFF27362A, 0xFFDCEBC5, 3, FontFamily.Monospace),
    Cutout("Cutout", "A sculpted vermilion shell with pale controls set into the cutouts.",
        0xFFECE6F6, 0xFF281527, 0xFF62465E, 0xFFFFF5FF, 0xFFB32529, 0xFFFFF1E7, 25),
    HiFi("Hi-Fi", "A warm turntable with a brushed tonearm and printed liner notes.",
        0xFFF3EDE1, 0xFF262921, 0xFF54594C, 0xFFE6DECE, 0xFF30463F, 0xFFFDF4E7, 12, FontFamily.Serif),
    Arcade("Arcade", "A violet cartridge screen and a directional pad.",
        0xFF120F1C, 0xFFF5EEFE, 0xFFBAAACD, 0xFF211934, 0xFFD8FC82, 0xFF1B2020, 4, FontFamily.Monospace),
    Pocket("Pocket", "A small sea-glass display above a tactile, oval remote.",
        0xFF0B1717, 0xFFE5FFF6, 0xFFA3C9BD, 0xFF15302E, 0xFFA4E5CE, 0xFF0C241F, 24),
    Halo("Halo", "A bright cobalt dial and a live lyric window in blue glass.",
        0xFF0F1C58, 0xFFF3F5FF, 0xFFBDCCFF, 0xFF233B91, 0xFFC9D7FF, 0xFF0F235A, 25),
    Atlas("Atlas", "A pixel topography with chapter markers you can seek to.",
        0xFF141E45, 0xFFEFF3FF, 0xFFBEC9ED, 0xFF243668, 0xFFE8F889, 0xFF182036, 6),
    ;

    val background get() = Color(paper)
    val foreground get() = Color(ink)
    val secondary get() = Color(quiet)
    val surface get() = Color(panel)
    val signal get() = Color(accent)
    val onSignal get() = Color(onAccent)
    val corner get() = radius.dp
}

internal enum class StudyPage(val label: String, val icon: Int) {
    Player("Player", R.drawable.play),
    Lyrics("Lyrics", R.drawable.lyrics),
    Queue("Queue", R.drawable.queue_music),
    More("More", R.drawable.more_horiz),
}

internal data class StudyTrack(val title: String, val artist: String, val duration: Int)

internal val studyTracks = listOf(
    StudyTrack("Night Signal", "Low Meridian", 228),
    StudyTrack("Blue Hours", "June Static", 204),
    StudyTrack("Glass Gardens", "Mira Vale", 256),
    StudyTrack("Passenger Light", "Low Meridian", 191),
    StudyTrack("Daybreak Relay", "Mira Vale", 218),
)

internal data class StudyLine(val start: Int, val text: String)

private val nightSignal = listOf(
    StudyLine(12, "The last train leaves a light"),
    StudyLine(30, "On every window frame"),
    StudyLine(50, "I let the city fall behind"),
    StudyLine(68, "Follow the light through the static"),
    StudyLine(86, "I’ll meet you on the other side"),
    StudyLine(107, "Keep the receiver open"),
    StudyLine(132, "Till the morning finds us"),
    StudyLine(158, "And the sky comes back in tune"),
)

private val otherLyrics = listOf(
    listOf("Stay until the street goes blue", "I can hear the pavement breathe", "All the windows turning gold", "The quiet makes a home for me"),
    listOf("Light collects in little pools", "Among the panes and climbing vines", "Every leaf is holding still", "The whole room tilts towards the sun"),
    listOf("Leave the passenger light on", "I know the road by every turn", "Your shadow keeps me company", "Until the distant houses wake"),
    listOf("Call me when the sky gets clear", "Send it down the silver wire", "I’ll keep watch above the rooftops", "Until the day begins again"),
)

internal data class StudyChapter(val label: String, val start: Int)

internal val studyChapters = listOf(
    StudyChapter("Arrival", 0),
    StudyChapter("Transmission", 68),
    StudyChapter("Daybreak", 158),
)

@Stable
internal class StudySession(initialPage: StudyPage = StudyPage.Player) {
    var page by mutableStateOf(initialPage)
    var position by mutableFloatStateOf(72f)
        private set
    var playing by mutableStateOf(false)
        private set
    var trackIndex by mutableStateOf(0)
        private set
    var queue by mutableStateOf(studyTracks.indices.drop(1))
        private set
    var favorites by mutableStateOf(setOf<Int>())
        private set
    var shuffle by mutableStateOf(false)
    var repeat by mutableStateOf(false)
    var autoFollow by mutableStateOf(true)
    var details by mutableStateOf(false)
    private val history = mutableListOf<Int>()

    val track get() = studyTracks[trackIndex]
    val fraction get() = position / track.duration
    val saved get() = trackIndex in favorites
    val lines get() = if (trackIndex == 0) nightSignal else otherLyrics[trackIndex - 1].mapIndexed { index, line ->
        StudyLine(16 + index * 34, line)
    }
    val activeLine get() = lines.indexOfLast { position >= it.start }

    fun togglePlay() {
        if (position >= track.duration) seek(0f)
        playing = !playing
    }

    fun seek(seconds: Float) {
        position = seconds.coerceIn(0f, track.duration.toFloat())
    }

    fun advance(seconds: Float) {
        if (!playing) return
        val target = position + seconds
        if (target < track.duration) seek(target)
        else if (repeat) seek(target - track.duration)
        else if (queue.isNotEmpty()) next()
        else {
            seek(track.duration.toFloat())
            playing = false
        }
    }

    fun next() {
        val nextTrack = if (shuffle) queue.randomOrNull() else queue.firstOrNull()
        if (nextTrack != null) playFromQueue(nextTrack, playing)
    }

    fun previous() {
        val previous = if (position < 4f) history.removeLastOrNull() else null
        if (previous != null) {
            queue = listOf(trackIndex) + queue
            trackIndex = previous
        }
        seek(0f)
    }

    fun playFromQueue(index: Int, play: Boolean = true) {
        if (index !in queue) return
        history.add(trackIndex)
        queue = queue.filterNot { it == index }
        trackIndex = index
        seek(0f)
        playing = play
    }

    fun moveInQueue(index: Int, offset: Int) {
        val from = queue.indexOf(index)
        val to = from + offset
        if (from !in queue.indices || to !in queue.indices) return
        queue = queue.toMutableList().apply { add(to, removeAt(from)) }
    }

    fun restoreQueue() {
        queue = studyTracks.indices.filterNot { it == trackIndex }
        history.clear()
    }

    fun toggleFavorite() {
        favorites = if (saved) favorites - trackIndex else favorites + trackIndex
    }
}

internal fun studyTime(seconds: Int): String =
    "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
