package com.convx.music.client.desktop

import com.convx.music.lyrics.LyricsQuery
import com.convx.music.lyrics.SharedLyricsSource
import com.convx.music.lyrics.bitchord.EnhancedLrc
import com.convx.music.lyrics.bitchord.LyricLine
import java.io.File

internal class DesktopLocalLyricsSource(private val library: DesktopMediaLibrary) : SharedLyricsSource {
    override val id = ID
    override val label = "Local lyrics"
    override val wordSynced = true
    override suspend fun lyrics(query: LyricsQuery): List<LyricLine>? {
        library.awaitReady()
        val track = library.state.value.index.tracks.firstOrNull { it.id == query.id || it.request().id == query.id } ?: return null
        val audio = File(track.filePath)
        val sidecar = listOf(File(audio.parentFile, audio.nameWithoutExtension + ".lrc"), File(audio.path + ".lrc")).firstOrNull { it.isFile && it.length() <= 1_048_576 }
        val text = sidecar?.readText() ?: track.lyricsText ?: return null
        return EnhancedLrc.parse(text).ifEmpty { text.lines().filter(String::isNotBlank).map { LyricLine(timeMs = 0, text = it) } }
    }
    companion object { const val ID = "local-files" }
}
