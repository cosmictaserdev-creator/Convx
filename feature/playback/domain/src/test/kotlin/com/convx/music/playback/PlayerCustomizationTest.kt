package com.convx.music.playback

import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PlayerCustomizationTest {
    @Test fun malformedLayoutCannotHideControlsOrEscapeAssetDirectory() {
        val player = PlayerCustomization(order = listOf(PlayerSection.CONTROLS, PlayerSection.CONTROLS),
            hidden = PlayerSection.entries.toSet(), diy = DiyLayout(stickers = listOf(
                DiySticker("escape", DiyStickerKind.IMAGE, "../private.png"),
                DiySticker("valid", DiyStickerKind.EMOJI, "✨", portrait = DiyTransform(Float.NaN, Float.POSITIVE_INFINITY, -4f, 999f)),
                DiySticker("valid", DiyStickerKind.EMOJI, "duplicate"))),
            icons = mapOf("PLAY" to PlayerIconOverride("C:secret.png"))).normalized()
        assertEquals(PlayerSection.entries.size, player.order.size)
        assertEquals(PlayerSection.CONTROLS, player.order.first())
        assertFalse(PlayerSection.CONTROLS in player.hidden)
        assertEquals(listOf("valid"), player.diy.stickers.map { it.id })
        assertEquals(DiyTransform(.5f, .5f, .03f, 360f), player.diy.stickers.single().portrait)
        assertTrue(player.icons.isEmpty())
    }

    @Test fun androidStickerWireFormatKeepsBothOrientations() {
        val layout = Json.decodeFromString<DiyLayout>("""{"version":1,"stickers":[{"id":"one","kind":"EMOJI","source":"✨","flipH":true,"corner":0.2,"portrait":{"x":0.1},"landscape":{"x":0.9}}]}""")
        assertEquals(.1f, layout.stickers.single().portrait.x)
        assertEquals(.9f, layout.stickers.single().landscape.x)
        assertTrue(layout.stickers.single().flipHorizontal)
        assertEquals(layout, Json.decodeFromString<DiyLayout>(Json.encodeToString(layout)))
    }
}
