package com.music.spine

import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

/**
 * Sources disagree about whether the list is called "tags" or "labels". Both shapes are
 * live right now, so both are pinned here — the bug this replaces read only "tags", which
 * silently made every module from a labels-only source look like it had no capabilities.
 */
class SpineModuleTagsTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `labels are honoured when a source publishes no tags`() {
        // Shape published by the monochrome source, verbatim.
        val module = json.decodeFromString<SpineModule>(
            """
            {"id":"claudo-tidal","name":"Claudo","author":"Ricky","version":"3.0.0",
             "labels":["FLAC","LOSSLESS","HI-RES","QOBUZ","TIDAL"]}
            """.trimIndent(),
        )
        assertEquals(listOf("FLAC", "LOSSLESS", "HI-RES", "QOBUZ", "TIDAL"), module.tags)
        assertTrue(module.isLossless)
        assertTrue(module.hasHiRes)
    }

    @Test
    fun `tags still win when a source publishes both`() {
        // The vercel meta-index normalises and emits both keys.
        val module = json.decodeFromString<SpineModule>(
            """
            {"id":"x","name":"X","tags":["HI-RES"],"labels":["HI-RES"]}
            """.trimIndent(),
        )
        assertEquals(listOf("HI-RES"), module.tags)
        assertTrue(module.hasHiRes)
    }

    @Test
    fun `a module with neither key claims no capabilities`() {
        val module = json.decodeFromString<SpineModule>("""{"id":"x","name":"X"}""")
        assertTrue(module.tags.isEmpty())
        assertFalse(module.isLossless)
        assertFalse(module.hasHiRes)
        assertFalse(module.isDolbyAtmos)
    }

    @Test
    fun `dolby atmos is detected from either key`() {
        val fromLabels = json.decodeFromString<SpineModule>(
            """{"id":"a","name":"A","labels":["DOBLY ATMOS","TIDAL"]}""",
        )
        assertTrue(fromLabels.isDolbyAtmos)

        val fromTags = json.decodeFromString<SpineModule>(
            """{"id":"b","name":"B","tags":["DOLBY","QOBUZ"]}""",
        )
        assertTrue(fromTags.isDolbyAtmos)
    }
}
