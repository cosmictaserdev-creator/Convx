package com.music.spotify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpotifyHomeFeedTest {
    @Test
    fun parsesPlaylistAndArtworkFromHomeShelf() {
        val shelves = SpotifyHomeFeed.parse(
            """
            {
              "data": {"home": {"sectionContainer": {"sections": {"items": [
                {
                  "uri": "spotify:section:mixes",
                  "data": {"title": {"transformedLabel": "Made for you"}},
                  "sectionItems": {"items": [{
                    "uri": "spotify:playlist:mix123",
                    "content": {"data": {
                      "__typename": "Playlist",
                      "name": "Daily Mix 1",
                      "images": {"items": [{"sources": [
                        {"url": "https://img.example/small.jpg", "width": 64},
                        {"url": "https://img.example/large.jpg", "width": 640}
                      ]}]}
                    }}
                  }]}
                }
              ]}}}}
            }
            """.trimIndent(),
        )

        assertEquals(1, shelves.size)
        assertEquals("Made for you", shelves.single().title)
        assertEquals("Daily Mix 1", shelves.single().items.single().title)
        assertEquals("mix123", shelves.single().items.single().playlistId)
        assertEquals("https://img.example/large.jpg", shelves.single().items.single().artworkUrl)
    }

    @Test
    fun combinesSingleItemBaselineShelvesAndSkipsUnknownItems() {
        val shelves = SpotifyHomeFeed.parse(
            """
            {
              "data": {"home": {"sectionContainer": {"sections": {"items": [
                {
                  "data": {"__typename": "HomeFeedBaselineSectionData", "title": {"transformedLabel": "For you"}},
                  "sectionItems": {"items": [{
                    "uri": "spotify:playlist:first",
                    "content": {"data": {"__typename": "Playlist", "name": "First"}}
                  }]}
                },
                {
                  "data": {"__typename": "HomeFeedBaselineSectionData", "title": {"transformedLabel": "For you"}},
                  "sectionItems": {"items": [{
                    "uri": "spotify:playlist:second",
                    "content": {"data": {"__typename": "Playlist", "name": "Second"}}
                  }]}
                },
                {
                  "data": {"title": {"transformedLabel": "Podcasts"}},
                  "sectionItems": {"items": [{
                    "uri": "spotify:show:unavailable",
                    "content": {"data": {"__typename": "Podcast", "name": "Skip"}}
                  }]}
                }
              ]}}}}
            }
            """.trimIndent(),
        )

        assertEquals(1, shelves.size)
        assertEquals("For you", shelves.single().title)
        assertEquals(listOf("First", "Second"), shelves.single().items.map { it.title })
        assertTrue(shelves.single().items.all { it.playlistId != null })
    }
}
