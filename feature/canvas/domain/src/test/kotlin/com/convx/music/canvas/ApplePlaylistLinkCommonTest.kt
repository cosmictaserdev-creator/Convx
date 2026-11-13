package com.convx.music.canvas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ApplePlaylistLinkCommonTest {
    @Test
    fun parsesPlaylistLinksAndUsesTheRequestedDefaultStorefront() {
        assertEquals(
            ApplePlaylistLink("us", "pl.u-abc123"),
            ApplePlaylistLink.parse("https://music.apple.com/us/playlist/my-music/pl.u-abc123?l=en"),
        )
        assertEquals(
            ApplePlaylistLink("in", "pl.abc123"),
            ApplePlaylistLink.parse("https://music.apple.com/in/playlist/pl.abc123"),
        )
        assertEquals(
            ApplePlaylistLink("gb", "pl.abc123"),
            ApplePlaylistLink.parse("https://music.apple.com/playlist/pl.abc123", defaultStorefront = "gb"),
        )
    }

    @Test
    fun rejectsOtherHostsServicesPortsAndMalformedIdentifiers() {
        listOf(
            "https://music.apple.com.evil.test/us/playlist/name/pl.abc",
            "https://music.apple.com@evil.test/us/playlist/name/pl.abc",
            "https://music.apple.com/us/album/name/pl.abc",
            "https://music.apple.com/us/playlist/name/p.abc",
            "https://music.apple.com:444/us/playlist/name/pl.abc",
            "http://music.apple.com/us/playlist/name/pl.abc",
            "https://music.apple.com/us/playlist/name with spaces/pl.abc",
            "invalid",
        ).forEach { assertNull(ApplePlaylistLink.parse(it), it) }
    }

    @Test
    fun paginationMustStayOnThePlaylistTracksPath() {
        val base = "/v1/catalog/us/playlists/pl.test"
        val next = "$base/tracks?offset=100"
        assertEquals(next, ApplePlaylistPagination.validateNextPath(base, next))

        listOf(
            "https://evil.test/tracks",
            "//evil.test/tracks",
            "$base/tracks/other",
            "$base/tracks#fragment",
            "$base/tracks?offset=%zz",
        ).forEach { invalid ->
            assertFailsWith<IllegalStateException> {
                ApplePlaylistPagination.validateNextPath(base, invalid)
            }
        }
    }
}
