package com.music.innertube.models

import kotlinx.serialization.Serializable

/** A podcast episode row: [title] is the episode, [secondTitle] the show. */
@Serializable
data class MusicMultiRowListItemRenderer(
    val title: Runs?,
    val secondTitle: Runs?,
    val thumbnail: ThumbnailRenderer?,
    val onTap: NavigationEndpoint?,
) {
    fun toSongItem(): SongItem? {
        val watch = onTap?.watchEndpoint ?: return null
        val show = secondTitle?.runs?.firstOrNull()
        return SongItem(
            id = watch.videoId ?: return null,
            title = title?.runs?.firstOrNull()?.text ?: return null,
            artists = listOfNotNull(show?.let { Artist(name = it.text, id = it.navigationEndpoint?.browseEndpoint?.browseId) }),
            thumbnail = thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
            endpoint = watch,
            musicVideoType = watch.watchEndpointMusicSupportedConfigs?.watchEndpointMusicConfig?.musicVideoType,
        )
    }
}
