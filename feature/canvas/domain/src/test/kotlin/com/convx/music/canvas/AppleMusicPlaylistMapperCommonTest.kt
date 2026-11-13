package com.convx.music.canvas

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AppleMusicPlaylistMapperCommonTest {
    private val link = ApplePlaylistLink("us", "pl.test")
    private val base = "/v1/catalog/us/playlists/pl.test"
    private val detail = Json.parseToJsonElement(
        """{"data":[{"attributes":{"name":"A playlist","artwork":{"url":"https://example.test/{w}x{h}bb.{f}"}}}]}""",
    ).jsonObject

    @Test
    fun mapsAllTrackPagesAndCountsUnsupportedItems() = runTest {
        val pages = mapOf(
            base to detail,
            "$base/tracks?limit=100" to Json.parseToJsonElement(
                """{"data":[{"type":"songs","attributes":{"name":"First","artistName":"Artist","durationInMillis":123000}}],"next":"$base/tracks?offset=100"}""",
            ).jsonObject,
            "$base/tracks?offset=100" to Json.parseToJsonElement(
                """{"data":[{"type":"music-videos","attributes":{"name":"Video"}},{"type":"songs","attributes":{"name":"Second","artistName":"Artist"}}]}""",
            ).jsonObject,
        )

        val playlist = AppleMusicPlaylistMapper.fetch(link) { pages[it] }

        assertEquals("A playlist", playlist.title)
        assertEquals("https://example.test/600x600bb.jpg", playlist.artworkUrl)
        assertEquals(listOf("First", "Second"), playlist.tracks.map { it.title })
        assertEquals(123000, playlist.tracks.first().durationMs)
        assertEquals(1, playlist.unsupportedTracks)
    }

    @Test
    fun rejectsExternalAndCyclicPagesAndDoesNotReturnPartialResults() = runTest {
        for (next in listOf("https://evil.test/tracks", "$base/tracks?limit=100")) {
            assertIllegalState {
                AppleMusicPlaylistMapper.fetch(link) { path ->
                    if (path == base) detail else Json.parseToJsonElement("""{"data":[],"next":"$next"}""").jsonObject
                }
            }
        }

        assertIllegalState {
            AppleMusicPlaylistMapper.fetch(link) { if (it == base) detail else null }
        }
    }

    private suspend fun assertIllegalState(block: suspend () -> Unit) {
        try {
            block()
        } catch (_: IllegalStateException) {
            return
        }
        fail("Expected IllegalStateException")
    }
}
