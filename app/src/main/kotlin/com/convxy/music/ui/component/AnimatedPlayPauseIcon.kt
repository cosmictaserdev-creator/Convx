package com.convxy.music.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convxy.music.R

@Composable
fun AnimatedPlayPauseIcon(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = 32.dp,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedContent(
            targetState = isPlaying,
            transitionSpec = {
                ((fadeIn(tween(180, easing = FastOutSlowInEasing)) + scaleIn(tween(180, easing = FastOutSlowInEasing), initialScale = 0.85f)) togetherWith
                        (fadeOut(tween(140, easing = FastOutSlowInEasing)) + scaleOut(tween(140, easing = FastOutSlowInEasing), targetScale = 0.85f)))
                    .using(SizeTransform(clip = false))
            },
            contentKey = { it },
            label = "play_pause",
        ) { playing ->
            val res = if (playing) R.drawable.pause else R.drawable.play
            val iconSize = if (playing) size * 0.88f else size

            Icon(
                painter = painterResource(res),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}
