/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * The Saavn catalogue path is pure mapping: the network lives in SaavnService and
 * the UI reuses the existing rows, so the mapping and the id round-trip are the
 * only parts a JVM test can pin down without a device.
 */

package com.convx.music.api

import com.music.innertube.models.SongItem
import com.music.jiosaavn.RawArtistMap
import com.music.jiosaavn.RawArtistMapItem
import com.music.jiosaavn.RawMoreInfo
import com.music.jiosaavn.RawSongItem
import com.music.jiosaavn.SAAVN_SONG_ID_PREFIX
import com.music.jiosaavn.isSaavnSongId
import com.music.jiosaavn.saavnRawSongId
import com.music.jiosaavn.saavnThumbnail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaavnCatalogueTest {

    private fun song(
        id: String = "3IoDK8qI",
        title: String = "Kesariya",
        image: String = "https://c.saavncdn.com/471/Kesariya-Hindi-2022-50x50.jpg",
        album: String = "Brahmastra",
        albumId: String = "1142502",
        duration: String = "268",
        artists: List<Pair<String, String>> = listOf("1" to "Arijit Singh", "2" to "Pritam"),
    ) = RawSongItem(
        id = id,
        title = title,
        image = image,
        moreInfo = RawMoreInfo(
            album_id = albumId,
            album = album,
            encryptedMediaUrl = "encrypted",
            duration = duration,
            has320 = "true",
            artistMap = RawArtistMap(
                primaryArtists = artists.map { RawArtistMapItem(id = it.first, name = it.second) },
            ),
        ),
    )

    @Test
    fun `maps a Saavn song onto a Convx song item`() {
        val item = song().toSongItem()
        requireNotNull(item)
        assertEquals(SAAVN_SONG_ID_PREFIX + "3IoDK8qI", item.id)
        assertEquals("Kesariya", item.title)
        assertEquals(listOf("Arijit Singh", "Pritam"), item.artists.map { it.name })
        assertEquals("Brahmastra", item.album?.name)
        assertEquals("1142502", item.album?.id)
        assertEquals(268, item.duration)
        assertFalse(item.explicit)
    }

    @Test
    fun `asks the CDN for the 500px cover instead of the 50px one`() {
        val item = song().toSongItem()
        requireNotNull(item)
        assertTrue(item.thumbnail.contains("500x500"))
        assertFalse(item.thumbnail.contains("50x50"))
    }

    @Test
    fun `leaves a cover url without a known size untouched`() {
        val url = "https://example.test/cover.jpg"
        assertEquals(url, saavnThumbnail(url))
        assertEquals("", saavnThumbnail(""))
    }

    @Test
    fun `decodes the html entities Saavn sends`() {
        val item = song(
            title = "Tum Hi Ho &quot;Reprise&quot;",
            album = "Aashiqui &amp; 2",
            artists = listOf("1" to "Mithoon &amp; Jeet Gannguli"),
        ).toSongItem()
        requireNotNull(item)
        assertEquals("Tum Hi Ho \"Reprise\"", item.title)
        assertEquals("Aashiqui & 2", item.album?.name)
        assertEquals("Mithoon & Jeet Gannguli", item.artists.single().name)
    }

    @Test
    fun `drops entries the catalogue returned without an id or title`() {
        assertNull(song(id = "").toSongItem())
        assertNull(song(title = "").toSongItem())
        assertNull(song(title = "   ").toSongItem())
    }

    @Test
    fun `a duration that is not a number becomes unknown rather than zero`() {
        assertNull(song(duration = "").toSongItem()?.duration)
        assertNull(song(duration = "2:68").toSongItem()?.duration)
        assertEquals(0, song(duration = "0").toSongItem()?.duration)
    }

    @Test
    fun `a blank album is dropped instead of rendered empty`() {
        assertNull(song(album = "").toSongItem()?.album)
    }

    @Test
    fun `a song with no primary artists keeps an empty artist list`() {
        val item = song(artists = emptyList()).toSongItem()
        requireNotNull(item)
        assertTrue(item.artists.isEmpty())
    }

    @Test
    fun `saavn media ids round-trip and youtube ids are left alone`() {
        val mediaId = SAAVN_SONG_ID_PREFIX + "3IoDK8qI"
        assertTrue(isSaavnSongId(mediaId))
        assertEquals("3IoDK8qI", saavnRawSongId(mediaId))

        val youTubeId = "dQw4w9WgXcQ"
        assertFalse(isSaavnSongId(youTubeId))
        assertEquals(youTubeId, saavnRawSongId(youTubeId))
    }

    @Test
    fun `the queue sees a Saavn item as an ordinary song item`() {
        val item: SongItem? = song().toSongItem()
        requireNotNull(item)
        // searchResultQueue filters siblings with filterInstance<SongItem>(), so the
        // mapped item has to be exactly that type.
        assertEquals(1, listOf(item).filterIsInstance<SongItem>().size)
    }
}
