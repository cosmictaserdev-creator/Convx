package com.convx.music.lyrics.data

import com.convx.music.lyrics.bitchord.*
import com.convx.music.lyrics.LyricsQuery
import com.convx.music.lyrics.SharedLyricsSource
import com.music.innertube.InnertubeRepository
import com.music.innertube.models.WatchEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Uses the same provider clients and lyric models as the Android app. */
fun desktopLyricsSources(innertube: InnertubeRepository): List<SharedLyricsSource> {
    fun source(id: String, label: String, words: Boolean = true, lookup: suspend (LyricsQuery) -> List<LyricLine>?): SharedLyricsSource =
        object : SharedLyricsSource {
            override val id = id
            override val label = label
            override val wordSynced = words
            override suspend fun lyrics(query: LyricsQuery) = withContext(Dispatchers.IO) {
                lookup(query.copy(title = query.title.forLyricsSearch(), artist = query.artist.artistForLyricsSearch()))
            }
        }
    return listOf(
        source("LrcRed", "lrc.red") { LrcRed.lyrics(it.title, it.artist, it.durationMs) },
        source("BiniLyrics", "BiniLyrics") { BiniLyrics.lyrics(it.title, it.artist, it.durationMs, it.album)?.lines },
        source("BetterLyrics", "Better Lyrics") { BetterLyrics.lyrics(it.title, it.artist, it.durationMs, it.album) },
        source("BetterLyricsPortato", "BetterLyrics Portato") { BetterLyrics.portato(it.title, it.artist, it.durationMs, it.album) },
        source("Paxsenix", "PaxSenix") { PaxSenix.lyrics(it.title, it.artist, it.durationMs, it.album) },
        source("PaxsenixSpotify", "PaxSenix: Spotify", false) { PaxSenix.spotifyLyrics(it.title, it.artist, it.durationMs) },
        source("PaxsenixMusixmatch", "PaxSenix: Musixmatch") { PaxSenix.musixmatchLyrics(it.title, it.artist, it.durationMs) },
        source("YouLyPlus", "LyricsPlus") { LyricsPlus.lyrics(it.title, it.artist, it.durationMs, it.album) },
        source("Unison", "Unison") { Unison.lyrics(it.title, it.artist, it.durationMs, it.album) },
        source("YouTubeSubtitle", "YouTube captions", false) { innertube.transcript(it.id).getOrNull()?.let(ProviderLyrics::parse) },
        source("YouTubeMusic", "YouTube Music", false) {
            val endpoint = innertube.next(WatchEndpoint(videoId = it.id)).getOrNull()?.lyricsEndpoint
            endpoint?.let { lyricsEndpoint -> innertube.lyrics(lyricsEndpoint).getOrNull()?.let(ProviderLyrics::parse) }
        },
        source("Kugou", "KuGou", false) { KuGou.lyrics(it.title, it.artist, it.durationMs, it.album) },
        source("LrcLib", "LRCLIB", false) { LrcLib.lyrics(it.title, it.artist, it.durationMs) },
        source("Musixmatch", "Musixmatch", false) { Musixmatch.lyrics(it.title, it.artist, it.durationMs) },
        source("Genius", "Genius", false) { Genius.lyrics(it.title, it.artist) },
    )
}
