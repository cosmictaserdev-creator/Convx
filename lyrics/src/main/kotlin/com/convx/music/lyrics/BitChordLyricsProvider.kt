package com.convx.music.lyrics

import android.content.Context
import com.convx.music.lyrics.bitchord.*
import kotlinx.coroutines.CancellationException
import java.util.Locale

/** BitChord's providers, adapted to Convx's persisted lyric format. */
class BitChordLyricsProvider(
    override val id: String,
    override val displayName: String,
    private val lookup: suspend (String, String, String, Long, String?) -> List<LyricLine>?,
) : LyricsProvider {
    override val requiresVideoId: Boolean get() = id == "SimpMusic"
    override fun initialize(context: Context) {
        PaxSenix.setApiKey(context.getSharedPreferences("lyrics_credentials", Context.MODE_PRIVATE)
            .getString("paxsenix_api_key", "").orEmpty())
    }

    override suspend fun getLyrics(id: String, title: String, artist: String, duration: Int, album: String?): Result<String> = try {
        val lines = lookup(id, title.forLyricsSearch(), artist.artistForLyricsSearch(), duration.toLong() * 1000L, album)
        if (lines.isNullOrEmpty()) Result.failure(NoSuchElementException("No lyrics found"))
        else Result.success(lines.toConvxLyrics())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
}

internal fun List<LyricLine>.toConvxLyrics(): String {
    if (none { it.timeMs > 0 || it.words.isNotEmpty() }) return joinToString("\n") { it.text }
    fun stamp(ms: Long) = String.format(Locale.ROOT, "%02d:%02d.%03d", ms / 60_000, ms / 1000 % 60, ms % 1000)
    fun LyricLine.row(background: Boolean = false): String = buildString {
        append("[${stamp(timeMs.coerceAtLeast(0))}]")
        if (background) append("{bg}")
        if (alignment == LyricAlignment.End) append("{agent:v2}")
        if (words.isEmpty()) append(text) else words.forEachIndexed { index, word ->
            if (index > 0) append(' ')
            append("<${stamp(word.startMs)}|${stamp(word.endMs)}>${word.text}")
        }
    }
    return flatMap { line -> listOfNotNull(line.row(), line.background?.row(true)) }.joinToString("\n")
}

object BitChordLyricsProviders {
    val all: List<LyricsProvider> = listOf(
        BitChordLyricsProvider("LrcRed", "lrc.red") { _, t, a, d, _ -> LrcRed.lyrics(t, a, d) },
        BitChordLyricsProvider("BiniLyrics", "BiniLyrics") { _, t, a, d, al -> BiniLyrics.lyrics(t, a, d, al)?.lines },
        BitChordLyricsProvider("BetterLyrics", "Better Lyrics") { _, t, a, d, al -> BetterLyrics.lyrics(t, a, d, al) },
        BitChordLyricsProvider("BetterLyricsPortato", "BetterLyrics Portato") { _, t, a, d, al -> BetterLyrics.portato(t, a, d, al) },
        BitChordLyricsProvider("Paxsenix", "PaxSenix") { _, t, a, d, al -> PaxSenix.lyrics(t, a, d, al) },
        BitChordLyricsProvider("PaxsenixSpotify", "PaxSenix: Spotify") { _, t, a, d, _ -> PaxSenix.spotifyLyrics(t, a, d) },
        BitChordLyricsProvider("PaxsenixMusixmatch", "PaxSenix: Musixmatch") { _, t, a, d, _ -> PaxSenix.musixmatchLyrics(t, a, d) },
        BitChordLyricsProvider("YouLyPlus", "LyricsPlus") { _, t, a, d, al -> LyricsPlus.lyrics(t, a, d, al) },
        BitChordLyricsProvider("SimpMusic", "SimpMusic") { videoId, _, _, d, _ -> SimpMusicLyrics.lyrics(videoId, d) },
        BitChordLyricsProvider("Unison", "Unison") { _, t, a, d, al -> Unison.lyrics(t, a, d, al) },
        BitChordLyricsProvider("Megalobiz", "Megalobiz") { _, t, a, _, _ -> Megalobiz.lyrics(t, a) },
        BitChordLyricsProvider("Kugou", "KuGou") { _, t, a, d, al -> KuGou.lyrics(t, a, d, al) },
        BitChordLyricsProvider("LrcLib", "LRCLIB") { _, t, a, d, _ -> LrcLib.lyrics(t, a, d) },
        BitChordLyricsProvider("Musixmatch", "Musixmatch") { _, t, a, d, _ -> Musixmatch.lyrics(t, a, d) },
        BitChordLyricsProvider("Genius", "Genius") { _, t, a, _, _ -> Genius.lyrics(t, a) },
    )
}
