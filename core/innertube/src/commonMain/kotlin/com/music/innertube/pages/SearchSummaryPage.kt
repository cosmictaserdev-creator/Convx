package com.music.innertube.pages

import com.music.innertube.models.Album
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.MusicCardShelfRenderer
import com.music.innertube.models.MusicResponsiveListItemRenderer
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.Run
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.models.filterYoutubeShorts
import com.music.innertube.models.oddElements
import com.music.innertube.models.splitBySeparator
import com.music.innertube.utils.parseTime

data class SearchSummary(
    val title: String,
    val items: List<YTItem>,
)


data class SearchSummaryPage(
    val summaries: List<SearchSummary>,
) {
    fun filterExplicit(enabled: Boolean) = filterSummaries(enabled) { it.filterExplicit() }

    fun filterVideoSongs(disableVideos: Boolean) = filterSummaries(disableVideos) { it.filterVideoSongs(true) }

    fun filterYoutubeShorts(enabled: Boolean = false) = filterSummaries(enabled) { it.filterYoutubeShorts(true) }

    private fun filterSummaries(
        enabled: Boolean,
        filterItems: (List<YTItem>) -> List<YTItem>,
    ): SearchSummaryPage {
        if (!enabled) return this

        val filteredSummaries = summaries.mapNotNull { summary ->
            val items = filterItems(summary.items)
            if (items.isEmpty()) null else summary.copy(items = items)
        }
        return copy(summaries = filteredSummaries)
    }

    companion object {
        fun fromMusicCardShelfRenderer(renderer: MusicCardShelfRenderer): YTItem? {
            val subtitle = renderer.subtitle.runs?.splitBySeparator()
            return when {
                renderer.onTap.watchEndpoint != null -> cardSongItem(renderer, subtitle, renderer.onTap.watchEndpoint)

                renderer.onTap.browseEndpoint?.isArtistEndpoint == true ->
                    cardArtistItem(renderer, renderer.onTap.browseEndpoint)

                renderer.onTap.browseEndpoint?.isAlbumEndpoint == true ->
                    cardAlbumItem(renderer, subtitle, renderer.onTap.browseEndpoint)

                renderer.onTap.browseEndpoint?.isPlaylistEndpoint == true ->
                    cardPlaylistItem(renderer, renderer.onTap.browseEndpoint)

                else -> null
            }
        }

        private fun cardSongItem(
            renderer: MusicCardShelfRenderer,
            subtitle: List<List<Run>>?,
            endpoint: WatchEndpoint,
        ): SongItem? {
            return SongItem(
                id = endpoint.videoId ?: return null,
                title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                artists = SearchPage.songArtists(subtitle.orEmpty()),
                album = subtitle.orEmpty().flatten().firstOrNull { it.navigationEndpoint?.browseEndpoint?.isAlbumEndpoint == true }?.let { run ->
                    run.navigationEndpoint?.browseEndpoint?.browseId?.let { browseId ->
                        Album(name = run.text, id = browseId)
                    }
                },
                duration = subtitle?.lastOrNull()?.firstOrNull()?.text?.parseTime(),
                musicVideoType = renderer.onTap.musicVideoType,
                thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                explicit = renderer.subtitleBadges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
            )
        }

        private fun cardArtistItem(renderer: MusicCardShelfRenderer, endpoint: BrowseEndpoint): ArtistItem? {
            return ArtistItem(
                id = endpoint.browseId,
                title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                shuffleEndpoint = renderer.buttons
                    .find { it.buttonRenderer.icon?.iconType == "MUSIC_SHUFFLE" }
                    ?.buttonRenderer
                    ?.command
                    ?.watchPlaylistEndpoint ?: return null,
                radioEndpoint = renderer.buttons
                    .find { it.buttonRenderer.icon?.iconType == "MIX" }
                    ?.buttonRenderer
                    ?.command
                    ?.watchPlaylistEndpoint ?: return null,
            )
        }

        private fun cardAlbumItem(
            renderer: MusicCardShelfRenderer,
            subtitle: List<List<Run>>?,
            endpoint: BrowseEndpoint,
        ): AlbumItem? {
            return AlbumItem(
                browseId = endpoint.browseId,
                playlistId = renderer.buttons.firstOrNull()
                    ?.buttonRenderer
                    ?.command
                    ?.anyWatchEndpoint
                    ?.playlistId ?: return null,
                title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                artists = subtitle?.getOrNull(1)?.oddElements()?.map {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId,
                    )
                } ?: return null,
                year = null,
                thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                explicit = renderer.subtitleBadges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
            )
        }

        private fun cardPlaylistItem(renderer: MusicCardShelfRenderer, endpoint: BrowseEndpoint): PlaylistItem? {
            return PlaylistItem(
                id = endpoint.browseId.removePrefix("VL"),
                title = renderer.header?.musicCardShelfHeaderBasicRenderer?.title?.runs
                    ?.joinToString(separator = "") { it.text }
                    ?: return null,
                author = Artist(
                    id = null,
                    name = renderer.subtitle.runs?.joinToString { it.text } ?: return null,
                ),
                songCountText = null,
                thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                playEndpoint = renderer.buttons
                    .find { it.buttonRenderer.icon?.iconType == "PLAY_ARROW" }
                    ?.buttonRenderer
                    ?.command
                    ?.watchPlaylistEndpoint ?: return null,
                shuffleEndpoint = renderer.buttons
                    .find { it.buttonRenderer.icon?.iconType == "MUSIC_SHUFFLE" }
                    ?.buttonRenderer
                    ?.command
                    ?.watchPlaylistEndpoint ?: return null,
                radioEndpoint = null,
            )
        }

        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): YTItem? {
            val secondaryLine =
                renderer.flexColumns
                    .getOrNull(1)
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.splitBySeparator()
                    ?: return null
            val thirdLine =
                renderer.flexColumns
                    .getOrNull(2)
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.splitBySeparator()
                    ?: emptyList()

            return when {
                renderer.isSong -> SearchPage.songItem(renderer, secondaryLine + thirdLine)
                renderer.isArtist -> responsiveArtistItem(renderer)
                renderer.isAlbum -> responsiveAlbumItem(renderer, secondaryLine)
                renderer.isPlaylist -> responsivePlaylistItem(renderer, secondaryLine)
                else -> null
            }
        }

        private fun responsiveArtistItem(renderer: MusicResponsiveListItemRenderer): ArtistItem? {
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

        private fun responsiveAlbumItem(
            renderer: MusicResponsiveListItemRenderer,
            secondaryLine: List<List<Run>>,
        ): AlbumItem? {
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

        private fun responsivePlaylistItem(
            renderer: MusicResponsiveListItemRenderer,
            secondaryLine: List<List<Run>>,
        ): PlaylistItem? {
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
                    secondaryLine.getOrNull(1)?.firstOrNull()?.let {
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
}
