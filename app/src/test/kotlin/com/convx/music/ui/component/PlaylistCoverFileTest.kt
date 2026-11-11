/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylistCoverFileTest {
    @Test
    fun matchesOnlyTheOwnPlaylistsCoverFiles() {
        assertTrue(isCoverFileOf("LP_abc_1730000000000.jpg", "LP_abc"))
    }

    @Test
    fun aLongerIdThatSharesThePrefixIsNotAMatch() {
        assertFalse(isCoverFileOf("LP_abc_2_1730000000000.jpg", "LP_abc"))
        assertFalse(isCoverFileOf("LP_abcdef_1730000000000.jpg", "LP_abc"))
    }

    @Test
    fun idsWithRegexMetacharactersAreTreatedLiterally() {
        assertTrue(isCoverFileOf("a.b_1.jpg", "a.b"))
        assertFalse(isCoverFileOf("aXb_1.jpg", "a.b"))
    }

    @Test
    fun otherFileShapesAreNotCovers() {
        assertFalse(isCoverFileOf("LP_abc_.jpg", "LP_abc"))
        assertFalse(isCoverFileOf("LP_abc_12.png", "LP_abc"))
        assertFalse(isCoverFileOf("LP_abc_12.jpg.tmp", "LP_abc"))
    }
}
