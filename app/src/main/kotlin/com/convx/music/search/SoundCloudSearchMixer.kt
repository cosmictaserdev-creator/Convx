package com.convx.music.search

import com.music.innertube.models.SongItem
import java.util.Locale

private val titleWhitespace = Regex("\\s+")

private fun String.normalizedSearchText() = trim().lowercase(Locale.ROOT).replace(titleWhitespace, " ")

private fun SongItem.searchIdentity() =
    title.normalizedSearchText() to artists.map { it.name.normalizedSearchText() }.sorted()

internal fun mixSoundCloud(youtube: List<SongItem>, soundCloud: List<SongItem>): List<SongItem> {
    val youtubeIdentities = youtube.map { it.searchIdentity() }.toSet()
    val candidates = soundCloud.distinctBy { it.id }
        .filter { it.searchIdentity() !in youtubeIdentities }
        .take(5)
    if (youtube.size < 3) return youtube + candidates.take(2)
    return buildList {
        var sourceIndex = 0
        youtube.forEachIndexed { index, song ->
            add(song)
            if ((index + 1) % 3 == 0 && sourceIndex < candidates.size) {
                add(candidates[sourceIndex++])
            }
        }
    }
}
