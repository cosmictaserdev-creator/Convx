package com.convx.music.ui.component

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.convx.music.constants.ReduceDynamicBlurKey
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.playback.presentation.FisheyeLyrics as FisheyeRenderer
import com.convx.music.utils.rememberPreference

@Composable
fun FisheyeLyrics(
    lines: List<LyricsEntry>,
    fontFamily: FontFamily,
    positionMs: () -> Long,
    highlight: Color,
    onSeek: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val (reduceEffects) = rememberPreference(ReduceDynamicBlurKey, false)
    FisheyeRenderer(
        lines = lines,
        fontFamily = fontFamily,
        positionMs = positionMs,
        highlight = highlight,
        onSeek = onSeek,
        modifier = modifier,
        animationsEnabled = !reduceEffects && ValueAnimator.areAnimatorsEnabled(),
    )
}
