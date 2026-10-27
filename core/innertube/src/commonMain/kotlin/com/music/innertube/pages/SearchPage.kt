package com.music.innertube.pages

import com.music.innertube.models.Album
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.MusicResponsiveListItemRenderer
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.Run
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.oddElements
import com.music.innertube.models.splitBySeparator
import com.music.innertube.utils.parseTime

data class SearchResult(
    val items: List<YTItem>,
    val continuation: String? = null,
)

object SearchPage {
    internal fun songArtists(lines: List<List<Run>>): List<Artist> {
        val linked = lines.flatten().filter { it.navigationEndpoint?.browseEndpoint?.isArtistEndpoint == true }
        val fallback = lines.firstOrNull { group ->
            val label = group.firstOrNull()?.text?.trim().orEmpty()
            label.isNotEmpty() && !label.matches(Regex("\\d+:\\d{2}(?::\\d{2})?")) &&
                label !in listOf("Song", "Video", "Single", "Album", "EP") &&
                group.none { it.navigationEndpoint?.browseEndpoint?.isAlbumEndpoint == true }
        }?.oddElements().orEmpty()
        return linked.ifEmpty { fallback }.map { Artist(it.text, it.navigationEndpoint?.browseEndpoint?.browseId) }
    }

    fun toYTItem(renderer: MusicResponsiveListItemRenderer): YTItem? {
        val secondaryLine =
            renderer.flexColumns
                .getOrNull(1)
                ?.musicResponsiveListItemFlexColumnRenderer
                ?.text
                ?.runs
                ?.splitBySeparator()
                ?: return null

        return when {
            renderer.isSong -> songItem(renderer, secondaryLine)
            renderer.isArtist -> artistItem(renderer)
            renderer.isAlbum -> albumItem(renderer, secondaryLine)
            renderer.isPlaylist -> playlistItem(renderer, secondaryLine)
            else -> null
        }
    }

    internal fun songItem(renderer: MusicResponsiveListItemRenderer, secondaryLine: List<List<Run>>): SongItem? {
        val libraryTokens = PageHelper.extractLibraryTokensFromMenuItems(renderer.menu?.menuRenderer?.items)

        return SongItem(
            id = renderer.playlistItemData?.videoId
                ?: renderer.navigationEndpoint?.watchEndpoint?.videoId
                ?: renderer.overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint?.videoId
                ?: return null,
            title =
                renderer.flexColumns
                    .firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.firstOrNull()
                    ?.text ?: return null,
            artists = songArtists(secondaryLine),
            album =
                secondaryLine.flatten().firstOrNull { it.navigationEndpoint?.browseEndpoint?.isAlbumEndpoint == true }?.let { run ->
                    run.navigationEndpoint?.browseEndpoint?.browseId?.let { browseId ->
                        Album(name = run.text, id = browseId)
                    }
                },
            duration =
                secondaryLine
                    .lastOrNull()
                    ?.firstOrNull()
                    ?.text
                    ?.parseTime(),
            musicVideoType = renderer.musicVideoType,
            thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
            explicit =
                renderer.badges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
            endpoint = renderer.navigationEndpoint?.watchEndpoint
                ?: renderer.overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint,
            libraryAddToken = libraryTokens.addToken,
            libraryRemoveToken = libraryTokens.removeToken
        )
    }

    private fun artistItem(renderer: MusicResponsiveListItemRenderer): ArtistItem? {
        return ArtistItem(
            id = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
            title =
                renderer.flexColumns
                    .firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.firstOrNull()
                    ?.text
                    ?: return null,
            thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
            shuffleEndpoint =
                renderer.menu
                    ?.menuRenderer
                    ?.items
                    ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                    ?.menuNavigationItemRenderer
                    ?.navigationEndpoint
                    ?.watchPlaylistEndpoint ?: return null,
            radioEndpoint =
                renderer.menu.menuRenderer.items
                    .find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                    ?.menuNavigationItemRenderer
                    ?.navigationEndpoint
                    ?.watchPlaylistEndpoint ?: return null,
        )
    }

    private fun albumItem(renderer: MusicResponsiveListItemRenderer, secondaryLine: List<List<Run>>): AlbumItem? {
        return AlbumItem(
            browseId = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
            playlistId =
                renderer.overlay
                    ?.musicItemThumbnailOverlayRenderer
                    ?.content
                    ?.musicPlayButtonRenderer
                    ?.playNavigationEndpoint
                    ?.anyWatchEndpoint
                    ?.playlistId
                    ?: return null,
            title =
                renderer.flexColumns
                    .firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.firstOrNull()
                    ?.text ?: return null,
            artists =
                secondaryLine.getOrNull(1)?.oddElements()?.map {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId,
                    )
                } ?: return null,
            year =
                secondaryLine
                    .getOrNull(2)
                    ?.firstOrNull()
                    ?.text
                    ?.toIntOrNull(),
            thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
            explicit =
                renderer.badges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
        )
    }

    private fun playlistItem(renderer: MusicResponsiveListItemRenderer, secondaryLine: List<List<Run>>): PlaylistItem? {
        return PlaylistItem(
            id =
                renderer.navigationEndpoint
                    ?.browseEndpoint
                    ?.browseId
                    ?.removePrefix("VL") ?: return null,
            title =
                renderer.flexColumns
                    .firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.firstOrNull()
                    ?.text ?: return null,
            author =
                secondaryLine.firstOrNull()?.firstOrNull()?.let {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId,
                    )
                } ?: return null,
            songCountText =
                renderer.flexColumns
                    .getOrNull(1)
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.lastOrNull()
                    ?.text ?: return null,
            thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
            playEndpoint =
                renderer.overlay
                    ?.musicItemThumbnailOverlayRenderer
                    ?.content
                    ?.musicPlayButtonRenderer
                    ?.playNavigationEndpoint
                    ?.watchPlaylistEndpoint ?: return null,
            shuffleEndpoint =
                renderer.menu
                    ?.menuRenderer
                    ?.items
                    ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                    ?.menuNavigationItemRenderer
                    ?.navigationEndpoint
                    ?.watchPlaylistEndpoint ?: return null,
            radioEndpoint =
                renderer.menu.menuRenderer.items
                    .find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                    ?.menuNavigationItemRenderer
                    ?.navigationEndpoint
                    ?.watchPlaylistEndpoint ?: return null,
        )
    }
}
