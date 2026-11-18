package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.BiniLyrics
import com.convx.music.lyrics.bitchord.LyricLine
import com.convx.music.lyrics.bitchord.LrcRed
import com.convx.music.lyrics.bitchord.LyricsPlus
import com.convx.music.lyrics.bitchord.artistForLyricsSearch
import com.convx.music.lyrics.bitchord.forLyricsSearch
import com.convx.music.lyrics.bitchord.lyricsGet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

class LyricsLookupSession internal constructor(
    val query: LyricsQuery,
    val recordingId: String?,
    private val identified: BiniLyrics.Hit?,
    private val scope: CoroutineScope,
    private val download: suspend (String) -> String?,
) {
    private val documents = mutableMapOf<String, Deferred<String?>>()
    private val mutex = Mutex()

    internal suspend fun document(url: String): String? = mutex.withLock {
        documents.getOrPut(url) { scope.async(start = CoroutineStart.LAZY) { download(url) } }
    }.await()

    internal suspend fun lrcRed(): List<LyricLine>? =
        LrcRed.lyrics(query.title, query.artist, query.durationMs, recordingId, ::document)

    internal suspend fun biniLyrics(): List<LyricLine>? = (identified?.let { BiniLyrics.lyricsFor(it, ::document) }
        ?: BiniLyrics.lyrics(query.title, query.artist, query.durationMs, query.album, recordingId, ::document))?.lines

    internal suspend fun lyricsPlus(): List<LyricLine>? =
        LyricsPlus.lyrics(query.title, query.artist, query.durationMs, query.album, recordingId)
}

class LyricsLookupSessionFactory(
    private val scope: CoroutineScope,
    private val identify: suspend (LyricsQuery) -> BiniLyrics.Hit? = {
        BiniLyrics.identify(it.title, it.artist, it.durationMs, it.album)
    },
    private val download: suspend (String) -> String? = ::lyricsGet,
) {
    private val mutex = Mutex()
    private val recordings = linkedMapOf<LyricsQuery, BiniLyrics.Hit>()

    suspend fun create(query: LyricsQuery, enabledProviders: Set<String>): LyricsLookupSession {
        val cleaned = query.copy(title = query.title.forLyricsSearch(), artist = query.artist.artistForLyricsSearch())
        val remembered = mutex.withLock { recordings.remove(cleaned)?.also { recordings[cleaned] = it } }
        val hit = remembered ?: if ("BiniLyrics" in enabledProviders) {
            withTimeoutOrNull(2_500) {
                try { identify(cleaned) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { null }
            }?.also { found ->
                if (!found.isrc.isNullOrBlank()) {
                    mutex.withLock {
                        recordings[cleaned] = found
                        if (recordings.size > 100) recordings.remove(recordings.keys.first())
                    }
                }
            }
        } else null
        return LyricsLookupSession(cleaned, hit?.isrc, hit, scope, download)
    }
}
