package com.music.innertube

import com.music.innertube.mappers.AlbumMapper
import com.music.innertube.models.response.BrowseResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumMapperTest {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val header = """{"musicResponsiveHeaderRenderer":{
        "buttons":[], "title":{"runs":[{"text":"Test single"}]},
        "subtitle":{"runs":[{"text":"Single"},{"text":"2026"}]},
        "straplineTextOne":{"runs":[{"text":"Artist"}]},
        "thumbnail":{"musicThumbnailRenderer":{"thumbnail":{"thumbnails":[{"url":"https://example.org/art.jpg"}]}}}
    }}"""
    private val song = """{"musicResponsiveListItemRenderer":{
        "playlistItemData":{"videoId":"song-id"},
        "flexColumns":[{"musicResponsiveListItemFlexColumnRenderer":{"text":{"runs":[{"text":"Song title"}]}}}],
        "fixedColumns":[{"musicResponsiveListItemFixedColumnRenderer":{"text":{"runs":[{"text":"3:21"}]}}}]
    }}"""
    private val shelf = """{"musicPlaylistShelfRenderer":{"playlistId":"OLAK-test","contents":[$song]}}"""

    private fun decode(value: String) = json.decodeFromString<BrowseResponse>(value)

    @Test
    fun `two column header can follow other sections without canonical url`() {
        val response = decode("""{"responseContext":{},"contents":{"twoColumnBrowseResultsRenderer":{
            "tabs":[{"tabRenderer":{"content":{"sectionListRenderer":{"contents":[{},$header]}}}}],
            "secondaryContents":{"sectionListRenderer":{"contents":[{},$shelf]}}
        }}}""")
        val page = AlbumMapper.parseAlbum(response, "MPRE-test")
        assertEquals("Test single", page.album.title)
        assertEquals("OLAK-test", page.album.playlistId)
        assertEquals(2026, page.album.year)
        assertEquals("Artist", page.songs.single().artists.single().name)
        assertEquals("Song title", page.songs.single().title)
        assertEquals(201, page.songs.single().duration)
    }

    @Test
    fun `single column releases expose tracks`() {
        val response = decode("""{"responseContext":{},"contents":{"singleColumnBrowseResultsRenderer":{
            "tabs":[{"tabRenderer":{"content":{"sectionListRenderer":{"contents":[$header,$shelf]}}}}]
        }}}""")
        assertEquals("song-id", AlbumMapper.parseAlbum(response, "MPRE-single").songs.single().id)
    }

    @Test
    fun `legacy header reads list query parameter and keeps every artist`() {
        val response = decode("""{"responseContext":{},
            "microformat":{"microformatDataRenderer":{"urlCanonical":"https://music.youtube.com/playlist?list=OLAK-test&feature=share"}},
            "header":{"musicDetailHeaderRenderer":{
                "title":{"runs":[{"text":"Legacy album"}]},"secondSubtitle":{"runs":[]},"thumbnail":{},"menu":{"menuRenderer":{}},
                "subtitle":{"runs":[{"text":"Artist A","navigationEndpoint":{"browseEndpoint":{"browseId":"A"}}},{"text":"Artist B","navigationEndpoint":{"browseEndpoint":{"browseId":"B"}}},{"text":"2025"}]}
            }}
        }""")
        val page = AlbumMapper.parseAlbum(response, "MPRE-legacy")
        assertEquals("OLAK-test", page.album.playlistId)
        assertEquals(listOf("Artist A", "Artist B"), page.album.artists?.map { it.name })
    }

    @Test
    fun `continuations can be delivered as actions or shelf contents`() {
        val token = """{"continuationItemRenderer":{"continuationEndpoint":{"continuationCommand":{"token":"next-page"}}}}"""
        val action = decode("""{"responseContext":{},"onResponseReceivedActions":[{"appendContinuationItemsAction":{"continuationItems":[$song,$token]}}]}""")
        assertEquals("next-page", AlbumMapper.continuation(action))
        assertEquals(2, AlbumMapper.songContents(action).size)
        val shelfResponse = decode("""{"responseContext":{},"continuationContents":{"musicPlaylistShelfContinuation":{"contents":[$song],"continuations":[{"nextContinuationData":{"continuation":"shelf-next"}}]}}}""")
        assertEquals("shelf-next", AlbumMapper.continuation(shelfResponse))
        assertEquals(1, AlbumMapper.songContents(shelfResponse).size)
        val sectionResponse = decode("""{"responseContext":{},"continuationContents":{"sectionListContinuation":{"contents":[$shelf],"continuations":[{"nextContinuationData":{"continuation":"section-next"}}]}}}""")
        assertEquals("section-next", AlbumMapper.continuation(sectionResponse))
        assertEquals(1, AlbumMapper.songContents(sectionResponse).size)
    }
}
