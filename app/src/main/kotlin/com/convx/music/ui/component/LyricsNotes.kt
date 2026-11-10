package com.convx.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.R
import com.convx.music.ui.utils.Motion
import kotlinx.coroutines.delay

@Composable
internal fun LyricsEmptyNote(fontFamily: FontFamily, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        BasicText(
            text = stringResource(R.string.lyrics_escaped),
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.alpha(0.6f),
        )
    }
}

@Composable
internal fun LyricsEndNote(fontFamily: FontFamily, color: Color, modifier: Modifier = Modifier) {
    BasicText(
        text = stringResource(R.string.lyrics_the_end),
        style = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            textAlign = TextAlign.Center,
        ),
        modifier = modifier.fillMaxWidth().padding(vertical = 32.dp).alpha(0.5f),
    )
}

private const val ARTIST_INTRO_MS = 4500L

@Composable
internal fun LyricsArtistIntro(name: String, positionMs: () -> Long, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(name) {
        while (true) {
            visible = positionMs() < ARTIST_INTRO_MS
            delay(100)
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(700, easing = Motion.LyricEasing)) +
            scaleIn(tween(900, easing = Motion.LyricEasing), initialScale = 0.86f),
        exit = fadeOut(tween(900)) + scaleOut(tween(900, easing = Motion.LyricEasing), targetScale = 1.08f),
        modifier = modifier,
    ) {
        BasicText(
            text = name,
            style = TextStyle(
                fontSize = 44.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}
