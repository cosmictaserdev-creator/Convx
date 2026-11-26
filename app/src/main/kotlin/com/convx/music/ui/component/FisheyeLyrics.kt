package com.convx.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.convx.music.lyrics.LyricsEntry

/** Android retains its existing API while sharing the renderer with desktop. */
@Composable
fun FisheyeLyrics(lines: List<LyricsEntry>, fontFamily: FontFamily, positionMs: () -> Long,
    highlight: Color, onSeek: ((Long) -> Unit)?, modifier: Modifier = Modifier) {
    val (zoom) = com.convx.music.utils.rememberPreference(com.convx.music.constants.FisheyeZoomKey, 0.34f)
    val (radius) = com.convx.music.utils.rememberPreference(com.convx.music.constants.FisheyeRadiusKey, 0.7f)
    val (textScale) = com.convx.music.utils.rememberPreference(com.convx.music.constants.FisheyeTextSizeKey, 1f)
    com.convx.music.playback.presentation.FisheyeLyrics(lines, fontFamily, positionMs, highlight, onSeek,
        modifier, animationsEnabled = !rememberReduceEffects(), lensStrength = zoom, lensRadius = radius, textScale = textScale)
}
