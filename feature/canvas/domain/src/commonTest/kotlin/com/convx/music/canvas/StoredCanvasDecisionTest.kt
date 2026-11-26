package com.convx.music.canvas

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class StoredCanvasDecisionTest {
    @Test
    fun roundTripsProviderAndCanvasUrls() {
        val decision = StoredCanvasDecision("SPOTIFY", CanvasArtwork(name = "Track", animated = "https://cdn.example/video.mp4?token=old"))
        val saved = Json.encodeToString(StoredCanvasDecision.serializer(), decision)
        assertEquals(decision, Json.decodeFromString(StoredCanvasDecision.serializer(), saved))
    }
}
