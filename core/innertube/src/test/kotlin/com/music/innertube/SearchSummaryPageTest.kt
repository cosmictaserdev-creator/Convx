package com.music.innertube

import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.MusicResponsiveListItemRenderer
import com.music.innertube.models.SongItem
import com.music.innertube.pages.SearchSummary
import com.music.innertube.pages.SearchSummaryPage
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class SearchSummaryPageTest {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Test
    fun `filters empty summaries and preserves the page when disabled`() {
        val page = SearchSummaryPage(
            summaries = listOf(
                SearchSummary("Songs", listOf(song("clean"), song("explicit", explicit = true))),
                SearchSummary("Explicit", listOf(song("only-explicit", explicit = true))),
            ),
        )

        val filtered = page.filterExplicit(enabled = true)

        assertEquals(listOf("Songs"), filtered.summaries.map { it.title })
        assertEquals(listOf("clean"), filtered.summaries.single().items.map { it.id })
        assertSame(page, page.filterExplicit(enabled = false))
    }

    @Test
    fun `video and shorts filters use the same empty-summary handling`() {
        val page = SearchSummaryPage(
            summaries = listOf(
                SearchSummary("Videos", listOf(song("video", musicVideoType = "MUSIC_VIDEO_TYPE_OFFICIAL_SOURCE_MUSIC"))),
                SearchSummary("Shorts", listOf(playlist("SSshort"))),
                SearchSummary("Keep", listOf(song("audio"), playlist("PLplaylist"))),
            ),
        )

        val withoutVideos = page.filterVideoSongs(disableVideos = true)
        val withoutShorts = page.filterYoutubeShorts(enabled = true)

        assertEquals(listOf("Shorts", "Keep"), withoutVideos.summaries.map { it.title })
        assertEquals(listOf("Videos", "Keep"), withoutShorts.summaries.map { it.title })
    }

    @Test
    fun `responsive song mapping retains artist and duration`() {
        val separator = '\u2022'
        val renderer = json.decodeFromString<MusicResponsiveListItemRenderer>("""{
            "playlistItemData":{"videoId":"track"},
            "flexColumns":[
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[{"text":"Title"}]}}},
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[
                    {"text":"Song"},{"text":" $separator "},{"text":"Artist"},{"text":" $separator "},{"text":"3:21"}
                ]}}}
            ],
            "thumbnail":{"musicThumbnailRenderer":{"thumbnail":{"thumbnails":[{"url":"https://example.org/art.jpg"}]}}}
        }""")

        val item = SearchSummaryPage.fromMusicResponsiveListItemRenderer(renderer) as SongItem

        assertEquals(listOf("Artist"), item.artists.map { it.name })
        assertEquals(201, item.duration)
    }

    @Test
    fun `duration-only subtitle is never shown as an artist`() {
        val renderer = json.decodeFromString<MusicResponsiveListItemRenderer>("""{
            "playlistItemData":{"videoId":"track"},
            "flexColumns":[
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[{"text":"Title"}]}}},
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[{"text":"3:44"}]}}}
            ],
            "thumbnail":{"musicThumbnailRenderer":{"thumbnail":{"thumbnails":[{"url":"https://example.org/art.jpg"}]}}}
        }""")
        val item = SearchSummaryPage.fromMusicResponsiveListItemRenderer(renderer) as SongItem
        assertEquals(emptyList<String>(), item.artists.map { it.name })
        assertEquals(224, item.duration)
    }

    private fun song(
        id: String,
        explicit: Boolean = false,
        musicVideoType: String? = null,
    ) = SongItem(
        id = id,
        title = id,
        artists = emptyList(),
        thumbnail = "",
        explicit = explicit,
        musicVideoType = musicVideoType,
    )

    private fun playlist(id: String) = PlaylistItem(
        id = id,
        title = id,
        author = null,
        songCountText = null,
        thumbnail = null,
        playEndpoint = null,
        shuffleEndpoint = null,
        radioEndpoint = null,
    )
}
