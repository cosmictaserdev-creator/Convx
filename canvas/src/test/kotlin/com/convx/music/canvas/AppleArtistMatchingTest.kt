package com.convx.music.canvas

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import org.junit.Assert.*
import org.junit.Test

class AppleArtistMatchingTest {
    @Test fun duplicateExactNamesStillResolve() {
        val artists = Json.parseToJsonElement("""[
            {"id":"1","attributes":{"name":"Artist"}},
            {"id":"2","attributes":{"name":"Artist"}}
        ]""").jsonArray
        assertEquals("1", AppleMusicCatalog.selectArtistId("Artist", artists))
    }

    @Test fun topicSuffixAndDiacriticsDoNotHideArtists() {
        val artists = Json.parseToJsonElement("""[{"id":"1","attributes":{"name":"Beyoncé"}}]""").jsonArray
        assertEquals("1", AppleMusicCatalog.selectArtistId("Beyonce - Topic", artists))
    }

    @Test fun similarNamesDoNotBecomeTheWrongArtist() {
        val artists = Json.parseToJsonElement("""[{"id":"1","attributes":{"name":"Drakeo the Ruler"}}]""").jsonArray
        assertNull(AppleMusicCatalog.selectArtistId("Drake", artists))
    }
}
