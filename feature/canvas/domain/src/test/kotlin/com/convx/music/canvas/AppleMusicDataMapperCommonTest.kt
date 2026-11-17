package com.convx.music.canvas

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppleMusicDataMapperCommonTest {
    @Test
    fun parsesArtworkDimensionsAndColors() {
        val element = Json.parseToJsonElement(
            """{"url":"https://example.test/{w}x{h}bb.jpg","width":600,"height":300,"bgColor":"#9097a8","textColor1":"141214"}""",
        )

        assertEquals(
            AppleArtwork(
                url = "https://example.test/{w}x{h}bb.jpg",
                width = 600,
                height = 300,
                bgColor = 0x9097a8,
                textColor1 = 0x141214,
            ),
            AppleMusicDataMapper.artwork(element),
        )
        assertNull(AppleMusicDataMapper.artwork(Json.parseToJsonElement("""{"width":600}""")))
    }

    @Test
    fun findsKeyColorRecursivelyAndRejectsNonColorFields() {
        assertEquals(
            0x0198c7,
            AppleMusicDataMapper.findKeyColor(Json.parseToJsonElement("""{"a":{"b":[{"keyColor":"0198c7"}]}}""")),
        )
        assertNull(AppleMusicDataMapper.findKeyColor(Json.parseToJsonElement("""{"bgColor":"ffffff"}""")))
        assertNull(AppleMusicDataMapper.hexColor("not-a-color"))
    }
}
