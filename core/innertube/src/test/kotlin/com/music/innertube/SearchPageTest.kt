package com.music.innertube

import com.music.innertube.models.MusicResponsiveListItemRenderer
import com.music.innertube.models.SongItem
import com.music.innertube.pages.SearchPage
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchPageTest {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private fun song(secondaryRuns: String): SongItem {
        val renderer = json.decodeFromString<MusicResponsiveListItemRenderer>("""{
            "playlistItemData":{"videoId":"track"},
            "flexColumns":[
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[{"text":"Title"}]}}},
                {"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[$secondaryRuns]}}}
            ],
            "thumbnail":{"musicThumbnailRenderer":{"thumbnail":{"thumbnails":[{"url":"https://example.org/art.jpg"}]}}}
        }""")
        return SearchPage.toYTItem(renderer) as SongItem
    }

    @Test
    fun `duration is never displayed as an artist`() {
        val item = song("""{"text":"3:21"}""")
        assertEquals(emptyList<String>(), item.artists.map { it.name })
        assertEquals(201, item.duration)
    }

    @Test
    fun `artist links are found after song type and retain collaborators`() {
        val item = song("""{"text":"Song"},{"text":" • "},
            {"text":"Artist A","navigationEndpoint":{"browseEndpoint":{"browseId":"A","browseEndpointContextSupportedConfigs":{"browseEndpointContextMusicConfig":{"pageType":"MUSIC_PAGE_TYPE_ARTIST"}}}}},
            {"text":" & "},
            {"text":"Artist B","navigationEndpoint":{"browseEndpoint":{"browseId":"B","browseEndpointContextSupportedConfigs":{"browseEndpointContextMusicConfig":{"pageType":"MUSIC_PAGE_TYPE_ARTIST"}}}}},
            {"text":" • "},{"text":"3:21"}""")
        assertEquals(listOf("Artist A", "Artist B"), item.artists.map { it.name })
        assertEquals(201, item.duration)
    }
}
