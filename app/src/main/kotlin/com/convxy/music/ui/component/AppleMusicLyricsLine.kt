/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Apple Music-style lyrics line renderer.
 *
 * ArchiveTune-inspired "sick lyrics" look, built on top of the existing
 * lyrics pipeline (no parser or provider changes):
 *  - per-word karaoke fill driven by [LyricsEntry.words] (Apple Music TTML
 *    via Paxsenix/BetterLyrics already carries word-level timings),
 *  - spring scale + bold emphasis on the active line,
 *  - past lines fade and optionally blur, upcoming lines stay dimmed,
 *  - lines without word timings degrade gracefully to whole-line styling.
 *
 * Lines with no singer metadata render exactly like single-singer songs:
 * the caller passes the global expressive accent as [textColor].
 */

package com.convxy.music.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convxy.music.constants.AppleMusicLyricsBlurKey
import com.convxy.music.constants.LyricsGlowEffectKey
import com.convxy.music.lyrics.LyricsEntry
import com.convxy.music.ui.screens.settings.LyricsPosition
import com.convxy.music.utils.rememberPreference
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppleMusicLyricsLine(
    entry: LyricsEntry,
    nextEntryTime: Long?,
    effectivePlaybackPosition: Long,
    isSynced: Boolean,
    isActive: Boolean,
    distanceFromCurrent: Int,
    lyricsTextPosition: LyricsPosition,
    textColor: Color,
    textSize: Float,
    lineSpacing: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (appleMusicLyricsBlur) = rememberPreference(AppleMusicLyricsBlurKey, true)
    val (lyricsGlowEffect) = rememberPreference(LyricsGlowEffectKey, true)

    // Distance is only meaningful for synced lyrics; unsynced docs always
    // render at full emphasis so the style doubles as a plain lyrics viewer.
    val past = isSynced && distanceFromCurrent < 0
    val activeAlpha = when {
        !isSynced -> 1f
        isActive -> 1f
        past -> 0.4f
        else -> 0.7f
    }
    val lineAlpha by animateFloatAsState(
        targetValue = activeAlpha,
        animationSpec = tween(durationMillis = 400),
        label = "lineAlpha",
    )
    val lineScale by animateFloatAsState(
        targetValue = if (isActive || !isSynced) 1f else 0.97f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "lineScale",
    )
    val blurRadius = when {
        !appleMusicLyricsBlur || !isSynced || isActive || !past -> 0.dp
        else -> (2 * abs(distanceFromCurrent).coerceAtMost(3)).dp
    }

    val alignment = when (lyricsTextPosition) {
        LyricsPosition.LEFT -> Alignment.CenterStart
        LyricsPosition.CENTER -> Alignment.Center
        LyricsPosition.RIGHT -> Alignment.CenterEnd
    }
    val textAlign = when (lyricsTextPosition) {
        LyricsPosition.LEFT -> TextAlign.Left
        LyricsPosition.CENTER -> TextAlign.Center
        LyricsPosition.RIGHT -> TextAlign.Right
    }

    val fontWeight = if (isActive || !isSynced) FontWeight.Bold else FontWeight.SemiBold
    val dimColor = textColor.copy(alpha = 0.45f)
    val glowShadow = if (lyricsGlowEffect && (isActive || !isSynced)) {
        Shadow(
            color = textColor.copy(alpha = 0.55f),
            offset = Offset.Zero,
            blurRadius = 18f,
        )
    } else {
        null
    }

    // Only the active line recomputes its karaoke fill on position ticks;
    // every other line renders plain text and stays jank-free.
    // Shadow lives on each SpanStyle: Compose drops TextStyle.shadow whenever
    // a span paints with a Brush, which is why the glow used to vanish on
    // word-timed lines.
    val activeAnnotated = if (isActive && isSynced && !entry.words.isNullOrEmpty()) {
        buildWordKaraokeTimed(
            words = entry.words!!,
            positionMs = effectivePlaybackPosition,
            activeColor = textColor,
            dimColor = dimColor,
            glow = glowShadow,
        )
    } else {
        null
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .alpha(lineAlpha)
            .blur(blurRadius)
            .graphicsLayer {
                scaleX = lineScale
                scaleY = lineScale
            }
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 24.dp, vertical = (6 * lineSpacing).dp),
    ) {
        Box(
            contentAlignment = alignment,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (activeAnnotated != null) {
                Text(
                    text = activeAnnotated,
                    fontSize = textSize.sp,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    color = textColor,
                    style = androidx.compose.ui.text.TextStyle(shadow = glowShadow),
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    text = entry.text,
                    fontSize = textSize.sp,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    color = textColor,
                    style = androidx.compose.ui.text.TextStyle(shadow = glowShadow),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/**
 * Live karaoke fill: words already sung render in [activeColor], the word
 * being sung sweeps a horizontal gradient at its progress point, and future
 * words stay dim.
 */
private fun buildWordKaraokeTimed(
    words: List<com.convxy.music.lyrics.WordTimestamp>,
    positionMs: Long,
    activeColor: Color,
    dimColor: Color,
    glow: Shadow?,
) = buildAnnotatedString {
    val positionSec = positionMs / 1000.0
    words.forEachIndexed { index, word ->
        val span = (word.endTime - word.startTime).coerceAtLeast(0.001)
        val progress = ((positionSec - word.startTime) / span).coerceIn(0.0, 1.0)
        val wordGlow = if (glow != null && progress > 0.0) {
            Shadow(
                color = glow.color,
                offset = glow.offset,
                blurRadius = glow.blurRadius * (0.45f + 0.55f * progress.toFloat()),
            )
        } else {
            null
        }
        val spanStyle = when {
            progress >= 1.0 -> SpanStyle(color = activeColor, shadow = wordGlow)
            progress <= 0.0 -> SpanStyle(color = dimColor)
            else -> {
                val p = progress.toFloat()
                val endStop = (p + 0.02f).coerceAtMost(1f)
                SpanStyle(
                    brush = Brush.horizontalGradient(
                        0f to activeColor,
                        p to activeColor,
                        endStop to dimColor,
                        1f to dimColor,
                    ),
                    shadow = wordGlow,
                )
            }
        }
        withStyle(spanStyle) {
            append(word.text)
        }
        if (index < words.size - 1) append(" ")
    }
}
