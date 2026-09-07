/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphabetScrollBarTest {

    private val ascending = listOf("Abba", "Beck", "Cake", "Zappa")
    private val descending = ascending.reversed()

    @Test
    fun `section keys bucket digits letters and everything else`() {
        assertEquals("0", alphabetSectionKey("2Pac"))
        assertEquals("A", alphabetSectionKey("Abba"))
        assertEquals("A", alphabetSectionKey("  abba"))
        assertEquals("#", alphabetSectionKey("Ólafur"))
        assertEquals("#", alphabetSectionKey(""))
    }

    @Test
    fun `index map points at the first item of each section`() {
        val map = buildAlphabetSectionIndex(ascending) { it }
        assertEquals(mapOf("A" to 0, "B" to 1, "C" to 2, "Z" to 3), map)
    }

    @Test
    fun `empty section falls forward to the next populated one`() {
        val map = buildAlphabetSectionIndex(ascending) { it }
        // No "D" in the library, so "D" lands on the next section that exists: Z.
        assertEquals(3, findAlphabetTargetIndex("D", map))
    }

    @Test
    fun `section past the end falls back to the last populated section`() {
        val map = buildAlphabetSectionIndex(listOf("Abba", "Beck")) { it }
        // Scrubbing to the bottom of the rail must reach the bottom of the list, not 0.
        assertEquals(1, findAlphabetTargetIndex("#", map))
    }

    @Test
    fun `descending sort is detected from the index map`() {
        assertFalse(sectionIndexIsDescending(buildAlphabetSectionIndex(ascending) { it }))
        assertTrue(sectionIndexIsDescending(buildAlphabetSectionIndex(descending) { it }))
    }

    @Test
    fun `a single populated section is not descending`() {
        val map = buildAlphabetSectionIndex(listOf("Abba", "Air")) { it }
        assertFalse(sectionIndexIsDescending(map))
    }

    @Test
    fun `proportional stops span the whole list and stay in range`() {
        val stops = buildProportionalSectionIndex(itemCount = 100, stops = 27)
        assertEquals(27, stops.size)
        assertEquals(0, stops.getValue("0"))
        assertEquals(99, stops.getValue("26"))
        assertTrue(stops.values.all { it in 0..99 })
        // Monotonic: dragging down the rail never scrolls the list back up.
        val ordered = (0 until 27).map { stops.getValue(it.toString()) }
        assertEquals(ordered.sorted(), ordered)
    }

    @Test
    fun `proportional stops survive lists shorter than the stop count`() {
        val stops = buildProportionalSectionIndex(itemCount = 3, stops = 27)
        assertTrue(stops.values.all { it in 0..2 })
        assertEquals(0, stops.getValue("0"))
        assertEquals(2, stops.getValue("26"))
    }

    @Test
    fun `an empty list produces no stops`() {
        assertTrue(buildProportionalSectionIndex(itemCount = 0, stops = 27).isEmpty())
    }
}
