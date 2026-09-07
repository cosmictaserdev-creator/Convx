/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.betterlyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for the Apple TTML vocalist registry: `ttm:agent` metadata parsing and
 * the `[singers:…]` header emitted when TTML is flattened to Convxy's LRC
 * dialect.
 */
class TTMLParserSingerTest {

    private val ttml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <tt xmlns="http://www.w3.org/ns/ttml"
            xmlns:ttm="http://www.w3.org/ns/ttml#metadata"
            xmlns:itunes="http://music.apple.com/lyric-ttml-internal">
          <head>
            <metadata>
              <ttm:agent type="person" xml:id="v1"><ttm:name type="full">Ryan Gosling</ttm:name></ttm:agent>
              <ttm:agent type="person" xml:id="v2"><ttm:name type="full">Emma Stone</ttm:name></ttm:agent>
              <ttm:agent type="group" xml:id="v1000"/>
            </metadata>
          </head>
          <body>
            <div begin="00:00.500" end="00:04.000">
              <p begin="00:00.500" end="00:02.000" ttm:agent="v1">
                <span begin="00:00.500" end="00:01.000">I re</span><span begin="00:01.000" end="00:02.000">member</span>
              </p>
              <p begin="00:02.000" end="00:04.000" ttm:agent="v2">Nothing stays forever</p>
            </div>
          </body>
        </tt>
    """.trimIndent()

    @Test
    fun `parses agent registry with names and types`() {
        val agents = TTMLParser.parseAgents(ttml)

        assertEquals(3, agents.size)
        val v1 = agents.first { it.id == "v1" }
        assertEquals("Ryan Gosling", v1.name)
        assertEquals("person", v1.type)
        val v2 = agents.first { it.id == "v2" }
        assertEquals("Emma Stone", v2.name)
        val group = agents.first { it.id == "v1000" }
        assertEquals("group", group.type)
        assertNull(group.name)
    }

    @Test
    fun `per line agents still parsed`() {
        val lines = TTMLParser.parseTTML(ttml)

        assertTrue(lines.isNotEmpty())
        assertEquals("v1", lines[0].agent)
        assertEquals("v2", lines[1].agent)
        assertEquals("I remember", lines[0].text)
    }

    @Test
    fun `toLRC emits singers header with named agents`() {
        val lines = TTMLParser.parseTTML(ttml)
        val lrc = TTMLParser.toLRC(lines, TTMLParser.parseAgents(ttml))

        assertTrue(lrc.startsWith("[singers:v1=Ryan Gosling|v2=Emma Stone]"))
        assertTrue(lrc.contains("[00:00.50]{agent:v1}I remember"))
        assertTrue(lrc.contains("{agent:v2}Nothing stays forever"))
    }

    @Test
    fun `toLRC without agents emits no header`() {
        val lines = TTMLParser.parseTTML(ttml)
        val lrc = TTMLParser.toLRC(lines)

        assertFalse(lrc.contains("[singers:"))
        assertTrue(lrc.startsWith("[00:00.50]"))
    }

    @Test
    fun `unnamed group agent is excluded from header`() {
        val lrc = TTMLParser.toLRC(TTMLParser.parseTTML(ttml), TTMLParser.parseAgents(ttml))
        assertFalse(lrc.contains("v1000"))
    }

    @Test
    fun `attribute style names are supported`() {
        val ttml2 = """
            <tt xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
              <head><metadata>
                <ttm:agent xml:id="v1" ttm:name="Karaoke Version"/>
              </metadata></head>
              <body/>
            </tt>
        """.trimIndent()

        val agents = TTMLParser.parseAgents(ttml2)

        assertEquals(listOf("Karaoke Version"), agents.mapNotNull { it.name })
    }

    @Test
    fun `malformed ttml returns empty registry`() {
        assertTrue(TTMLParser.parseAgents("not xml at all <broken").isEmpty())
        assertTrue(TTMLParser.parseAgents("").isEmpty())
    }

    @Test
    fun `agent on parent div is inherited by child paragraphs`() {
        val nested = """
            <?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml"
                xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
              <body>
                <div ttm:agent="v2">
                  <p begin="00:01.000" end="00:02.000">Featured first</p>
                  <p begin="00:02.000" end="00:03.000">Still featured</p>
                </div>
                <div ttm:agent="v1">
                  <p begin="00:03.000" end="00:04.000">Primary later</p>
                </div>
              </body>
            </tt>
        """.trimIndent()

        val lines = TTMLParser.parseTTML(nested)
        assertEquals(listOf("v2", "v2", "v1"), lines.map { it.agent })
    }

    @Test
    fun `missing agent on a continuation line keeps the previous singer`() {
        val sparse = """
            <?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml"
                xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
              <body>
                <div>
                  <p begin="00:01.000" ttm:agent="v1">Line one</p>
                  <p begin="00:02.000">Line two continues</p>
                  <p begin="00:03.000" ttm:agent="v2">Other singer</p>
                </div>
              </body>
            </tt>
        """.trimIndent()

        val lines = TTMLParser.parseTTML(sparse)
        assertEquals(listOf("v1", "v1", "v2"), lines.map { it.agent })
    }
}
