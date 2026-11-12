package com.convx.music.canvas

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.*
import org.junit.Test

class AppleAlbumMatchingTest {
    @Test fun aMatchingEditionWithoutArtworkDoesNotHideAnotherMatchingEdition() {
        val items = Json.parseToJsonElement("""[
            {"name":"Album","artistName":"Artist"},
            {"name":"Album","artistName":"Artist","artwork":{"url":"https://example.com/{w}x{h}.jpg","width":1000,"height":1000}}
        ]""").jsonArray.map { it.jsonObject }
        assertNotNull(AppleMusicCatalog.selectAlbumArtwork(items, "Album", "Artist"))
        assertNull(AppleMusicCatalog.selectAlbumArtwork(items, "Album", "Another Artist"))
    }
}
