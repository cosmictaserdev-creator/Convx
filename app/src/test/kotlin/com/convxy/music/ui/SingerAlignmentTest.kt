package com.convxy.music.ui.component

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.convxy.music.ui.screens.settings.LyricsPosition
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Apple Music duet layout alignment: background centered, group/shared
 * centered, lead vocalists alternate sides (v1 left, v2 right, v3 left, …),
 * no agent falls back to the user preference.
 */
class SingerAlignmentTest {

    private val pref = LyricsPosition.CENTER

    @Test
    fun `background vocals are always centered`() {
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment("v1", true, pref))
        assertEquals(TextAlign.Center, singerTextAlign("v1", true, pref))
    }

    @Test
    fun `first vocalist is left`() {
        assertEquals(Alignment.Start, singerLineAlignment("v1", false, pref))
        assertEquals(TextAlign.Left, singerTextAlign("v1", false, pref))
    }

    @Test
    fun `second vocalist is right`() {
        assertEquals(Alignment.End, singerLineAlignment("v2", false, pref))
        assertEquals(TextAlign.Right, singerTextAlign("v2", false, pref))
    }

    @Test
    fun `third vocalist alternates back to left`() {
        assertEquals(Alignment.Start, singerLineAlignment("v3", false, pref))
        assertEquals(TextAlign.Left, singerTextAlign("v3", false, pref))
    }

    @Test
    fun `fourth vocalist alternates back to right`() {
        assertEquals(Alignment.End, singerLineAlignment("v4", false, pref))
        assertEquals(TextAlign.Right, singerTextAlign("v4", false, pref))
    }

    @Test
    fun `group agent is centered`() {
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment("v1000", false, pref))
        assertEquals(TextAlign.Center, singerTextAlign("v1000", false, pref))
    }

    @Test
    fun `shared composite agents are centered`() {
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment("v1+v2", false, pref))
        assertEquals(TextAlign.Center, singerTextAlign("v1,v2", false, pref))
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment("v1&v2", false, pref))
    }

    @Test
    fun `lines without agent honor user preference`() {
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment(null, false, pref))
        assertEquals(Alignment.Start, singerLineAlignment(null, false, LyricsPosition.LEFT))
        assertEquals(Alignment.End, singerLineAlignment(null, false, LyricsPosition.RIGHT))
        assertEquals(TextAlign.Right, singerTextAlign(null, false, LyricsPosition.RIGHT))
    }

    @Test
    fun `unknown agent ids fall back to preference`() {
        assertEquals(Alignment.CenterHorizontally, singerLineAlignment("duet", false, pref))
        assertEquals(TextAlign.Center, singerTextAlign("main", false, pref))
    }
}
