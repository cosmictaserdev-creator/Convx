package com.music.innertube.pages

import com.music.innertube.models.Album
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.MusicCardShelfRenderer
import com.music.innertube.models.MusicCarouselShelfRenderer
import com.music.innertube.models.MusicShelfRenderer
import com.music.innertube.models.MusicTwoRowItemRenderer
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SectionListRenderer
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.oddElements
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.utils.parseTime

data class HomePage(
    val chips: List<Chip>?,
    val sections: List<Section>,
    val continuation: String? = null,
    val background: String? = null,
) {
    data class Chip(
        val title: String,
        val endpoint: BrowseEndpoint?,
        val deselectEndPoint: BrowseEndpoint?,
    ) {
        companion object {
            fun fromChipCloudChipRenderer(renderer: SectionListRenderer.Header.ChipCloudRenderer.Chip): Chip? {
                return Chip(
                    title = renderer.chipCloudChipRenderer.text?.runs?.firstOrNull()?.text ?: return null,
                    endpoint = renderer.chipCloudChipRenderer.navigationEndpoint.browseEndpoint,
                    deselectEndPoint = renderer.chipCloudChipRenderer.onDeselectedCommand?.browseEndpoint,
                )
            }
        }
    }

    data class Section(
        val title: String,
        val label: String?,
        val thumbnail: String?,
        val endpoint: BrowseEndpoint?,
        val items: List<YTItem>,
    ) {
        companion object {
            fun fromMusicCarouselShelfRenderer(renderer: MusicCarouselShelfRenderer): Section? {
                return Section(
                    title = renderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text ?: return null,
                    label = renderer.header.musicCarouselShelfBasicHeaderRenderer.strapline?.runs?.firstOrNull()?.text,
                    thumbnail = renderer.header.musicCarouselShelfBasicHeaderRenderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl(),
                    endpoint = renderer.header.musicCarouselShelfBasicHeaderRenderer.moreContentButton?.buttonRenderer?.navigationEndpoint?.browseEndpoint,
                    // List-style shelves ("Quick picks") carry responsive list items, not two-row cards.
                    items = renderer.contents.mapNotNull { content ->
                        content.musicTwoRowItemRenderer?.let { fromMusicTwoRowItemRenderer(it) }
                            ?: content.musicResponsiveListItemRenderer?.let { SearchPage.toYTItem(it) }
                            ?: content.musicMultiRowListItemRenderer?.toSongItem()
                    }.ifEmpty {
                        return null
                    }
                )
            }

            // Shelf/card section parsing ported from the reference app (2026, © Rukamori, GPL-3.0).
            fun fromMusicShelfRenderer(renderer: MusicShelfRenderer): Section? {
                val title = renderer.title?.runs?.firstOrNull()?.text ?: return null
                val items = renderer.contents.orEmpty()
                    .mapNotNull { it.musicResponsiveListItemRenderer?.let(SearchPage::toYTItem) }
                    .ifEmpty { return null }
                return Section(
                    title = title,
                    label = null,
                    thumbnail = null,
                    endpoint = renderer.moreContentButton?.buttonRenderer?.navigationEndpoint?.browseEndpoint,
                    items = items,
                )
            }

            fun fromMusicCardShelfRenderer(renderer: MusicCardShelfRenderer): Section? {
                val items = renderer.contents.orEmpty()
                    .mapNotNull { it.musicResponsiveListItemRenderer?.let(SearchPage::toYTItem) as? SongItem }
                    .ifEmpty { return null }
                val title = renderer.header?.musicCardShelfHeaderBasicRenderer?.title?.runs
                    ?.joinToString("") { it.text }?.takeIf(String::isNotBlank)
                    ?: renderer.title.runs?.joinToString("") { it.text }?.takeIf(String::isNotBlank)
                    ?: return null
                return Section(
                    title = title,
                    label = renderer.subtitle.runs?.joinToString("") { it.text }?.takeIf(String::isNotBlank),
                    thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl(),
                    endpoint = renderer.onTap.browseEndpoint,
                    items = items,
                )
            }

            fun fromSectionListContent(content: SectionListRenderer.Content): Section? =
                content.musicCarouselShelfRenderer?.let(::fromMusicCarouselShelfRenderer)
                    ?: content.musicShelfRenderer?.let(::fromMusicShelfRenderer)
                    ?: content.musicCardShelfRenderer?.let(::fromMusicCardShelfRenderer)
                    ?: content.itemSectionRenderer?.contents.orEmpty().firstNotNullOfOrNull { inner ->
                        inner.musicShelfRenderer?.let(::fromMusicShelfRenderer)
                    }

            private fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): YTItem? {
                return when {
                    renderer.isSong -> {
                        val subtitleRuns = renderer.subtitle?.runs?.oddElements() ?: return null
                        SongItem(
                            id = renderer.navigationEndpoint.watchEndpoint?.videoId ?: return null,
                            title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                            artists = subtitleRuns.filter { run ->
                                run.navigationEndpoint?.browseEndpoint?.browseId?.startsWith("UC") == true ||
                                (run.navigationEndpoint?.browseEndpoint != null && 
                                 run.navigationEndpoint.browseEndpoint.browseId.startsWith("MPREb_") != true)
                            }.map { run ->
                                Artist(
                                    name = run.text,
                                    id = run.navigationEndpoint?.browseEndpoint?.browseId
                                )
                            }.ifEmpty {
                                subtitleRuns.firstOrNull()?.let { run -> 
                                    listOf(Artist(name = run.text, id = null)) 
                                } ?: emptyList()
                            },
                            album = subtitleRuns.firstOrNull { 
                                it.navigationEndpoint?.browseEndpoint?.browseId?.startsWith("MPREb_") == true 
                            }?.let {
                                Album(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId ?: return@let null
                                )
                            },
                            duration = subtitleRuns.firstNotNullOfOrNull { it.text.parseTime() },
                            thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl()
                                ?: return null,
                            explicit = renderer.subtitleBadges?.any {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } == true
                        )
                    }
                    renderer.isAlbum -> {
                        AlbumItem(
                            browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null,
                            playlistId = renderer.thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content
                                ?.musicPlayButtonRenderer?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint?.playlistId ?: return null,
                            title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                            artists = renderer.subtitle?.runs?.oddElements()?.drop(1)?.map {
                                Artist(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId
                                )
                            },
                            year = null,
                            thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                            explicit = renderer.subtitleBadges?.find {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } != null
                        )
                    }

                    renderer.isPlaylist -> {
                        PlaylistItem(
                            id = renderer.navigationEndpoint.browseEndpoint?.browseId?.removePrefix("VL") ?: return null,
                            title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                            author = Artist(
                                name = renderer.subtitle?.runs?.firstOrNull()?.text ?: return null,
                                id = null
                            ),
                            songCountText = null,
                            thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                            playEndpoint = renderer.thumbnailOverlay
                                ?.musicItemThumbnailOverlayRenderer?.content
                                ?.musicPlayButtonRenderer?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                            shuffleEndpoint = renderer.menu?.menuRenderer?.items?.find {
                                it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE"
                            }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint ?: return null,
                            radioEndpoint = renderer.menu.menuRenderer.items.find {
                                it.menuNavigationItemRenderer?.icon?.iconType == "MIX"
                            }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint
                        )
                    }

                    renderer.isArtist -> {
                        ArtistItem(
                            id = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null,
                            title = renderer.title.runs?.lastOrNull()?.text ?: return null,
                            thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                            shuffleEndpoint = renderer.menu?.menuRenderer?.items?.find {
                                it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE"
                            }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint ?: return null,
                            radioEndpoint = renderer.menu.menuRenderer.items.find {
                                it.menuNavigationItemRenderer?.icon?.iconType == "MIX"
                            }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint ?: return null,
                        )
                    }

                    else -> null
                }
            }
        }
    }

    fun filterExplicit(enabled: Boolean = true) =
        if (enabled) {
            copy(sections = sections.map {
                it.copy(items = it.items.filterExplicit())
            })
        } else this

    fun filterVideoSongs(disableVideos: Boolean = false) =
        if (disableVideos) {
            copy(sections = sections.map { section ->
                section.copy(items = section.items.filterVideoSongs(true))
            })
        } else this
}
