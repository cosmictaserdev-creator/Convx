/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * 
 * Ported and adapted from reference-dev (LyricsV2.kt)
 */

package com.convx.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.wordsWithSpacing
import androidx.compose.runtime.remember
import com.convx.music.lyrics.WordTimestamp
import kotlin.math.PI
import kotlin.math.sin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyricsLineV2(
    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,
    entry: LyricsEntry,
    isActive: Boolean,
    isPast: Boolean,
    effectivePlaybackPosition: Long,
    expressiveAccent: Color,
    inactiveAlpha: Float,
    baseFontSize: Float,
    lineHeight: Float,
    showTranslated: Boolean,
    agentAlignment: Alignment.Horizontal,
    agentTextAlign: TextAlign,
) {
    val arrangement = when (agentTextAlign) {
        TextAlign.Center -> Arrangement.Center
        TextAlign.Right -> Arrangement.End
        else -> Arrangement.Start
    }

    val words = remember(entry.words, entry.text) { entry.wordsWithSpacing() }

    if (words.isNotEmpty()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = arrangement,
            verticalArrangement = Arrangement.spacedBy(
                // Use a capped spacing for internal wrapping to prevent "sentence break-off"
                with(LocalDensity.current) { (baseFontSize * ( (lineHeight / baseFontSize).coerceAtMost(1.3f) - 1f)).sp.toDp() }
            )
        ) {
            words.forEachIndexed { wordIndex, word ->
                Box(Modifier.padding(end = with(LocalDensity.current) {
                    (baseFontSize * 0.25f * word.text.takeLastWhile { it.isWhitespace() }.length).sp.toDp()
                })) {
                AnimatedWordV2(
                    fontFamily = fontFamily,
                    word = word.copy(text = word.text.trimEnd()),
                    isLineActive = isActive,
                    isLinePast = isPast,
                    effectivePlaybackPosition = effectivePlaybackPosition,
                    expressiveAccent = expressiveAccent,
                    inactiveAlpha = inactiveAlpha,
                    fontSize = if (entry.isBackground) baseFontSize * 0.85f else baseFontSize,
                    lineHeight = lineHeight.coerceAtMost(baseFontSize * 1.3f),
                    isBackground = entry.isBackground
                )
                }
            }
        }
    } else {
        // Fallback for lines without word timings (e.g. standard LRC without word sync)
        Text(
                    fontFamily = fontFamily,
            text = entry.text,
            style = AppTheme.typography.headlineMedium.copy(
                fontSize = if (entry.isBackground) (baseFontSize * 0.85f).sp else baseFontSize.sp,
                fontWeight = FontWeight.Bold,
                // Keep tracking proportional to the lyric size.
                letterSpacing = ((if (entry.isBackground) baseFontSize * 0.85f else baseFontSize) * 0.012f).sp,
                fontStyle = if (entry.isBackground) FontStyle.Italic else FontStyle.Normal,
                lineHeight = lineHeight.coerceAtMost(baseFontSize * 1.3f).sp,
            ),
            color = expressiveAccent.copy(alpha = if (isActive) 1f else inactiveAlpha),
            textAlign = agentTextAlign,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    // Translated text support
    if (showTranslated) {
        val translatedText by entry.translatedTextFlow.collectAsStateWithLifecycle()
        translatedText?.let { translated ->
            Text(
                    fontFamily = fontFamily,
                text = translated,
                fontSize = (baseFontSize * 0.7f).sp,
                color = expressiveAccent.copy(alpha = if (isActive) 0.8f else inactiveAlpha * 0.8f),
                textAlign = agentTextAlign,
                // Slightly tighten translated text.
                fontWeight = FontWeight.Bold,
                letterSpacing = (baseFontSize * 0.7f * -0.012f).sp,
                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                lineHeight = (baseFontSize * 0.7f * (lineHeight / baseFontSize).coerceAtMost(1.3f)).sp
            )
        }
    }
}

@Composable
fun AnimatedWordV2(
    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,
    word: WordTimestamp,
    isLineActive: Boolean,
    isLinePast: Boolean,
    effectivePlaybackPosition: Long,
    expressiveAccent: Color,
    inactiveAlpha: Float,
    fontSize: Float,
    lineHeight: Float,
    isBackground: Boolean,
) {
    val wordStartMs = (word.startTime * 1000).toLong()
    val wordEndMs = (word.endTime * 1000).toLong()
    val wordDuration = (wordEndMs - wordStartMs).coerceAtLeast(1L)

    val isWordComplete = isLinePast || effectivePlaybackPosition >= wordEndMs
    val isWordActive = isLineActive && effectivePlaybackPosition in wordStartMs until wordEndMs

    val progress = when {
        isWordComplete -> 1f
        !isLineActive || effectivePlaybackPosition <= wordStartMs -> 0f
        else -> ((effectivePlaybackPosition - wordStartMs).toFloat() / wordDuration).coerceIn(0f, 1f)
    }

    // Bounce and Float animation
    val sinProgress = sin(progress * PI).toFloat()
    val wordScale = 1f + (0.015f * sinProgress)
    
    val targetFloat = if (isWordActive) -4f * sinProgress else 0f
    val floatOffset by animateFloatAsState(
        targetValue = targetFloat,
        animationSpec = tween(
            durationMillis = if (isWordActive) 50 else 350,
            easing = FastOutSlowInEasing
        ),
        label = "WordFloatOffset"
    )

    // Glow intensity
    val glowProgress = (progress * 2f).coerceAtMost(1f)
    val glowAlpha = if (isWordActive) glowProgress * 0.45f else 0f
    val glowRadius = if (isWordActive) glowProgress * 12f else 0f

    val density = LocalDensity.current
    val fontWeight = FontWeight.Bold

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = floatOffset * density.density
                scaleX = wordScale
                scaleY = wordScale
            }
    ) {
        // Layer 1: Base text (always dimmed)
        Text(
                    fontFamily = fontFamily,
            text = word.text,
            style = AppTheme.typography.headlineMedium.copy(
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                letterSpacing = (fontSize * 0.012f).sp,
                fontStyle = if (isBackground) FontStyle.Italic else FontStyle.Normal,
                lineHeight = lineHeight.sp,
            ),
            color = expressiveAccent.copy(alpha = if (isBackground) inactiveAlpha * 0.7f else inactiveAlpha),
        )

        // Layer 2: Filled overlay with liquid sweep mask + glow
        if (isWordComplete || isWordActive) {
            Text(
                    fontFamily = fontFamily,
                text = word.text,
                style = AppTheme.typography.headlineMedium.copy(
                    fontSize = fontSize.sp,
                    fontWeight = fontWeight,
                    letterSpacing = (fontSize * 0.012f).sp,
                    fontStyle = if (isBackground) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = lineHeight.sp,
                    shadow = if (glowAlpha > 0f) {
                        Shadow(
                            color = expressiveAccent.copy(alpha = glowAlpha),
                            offset = Offset.Zero,
                            blurRadius = glowRadius.coerceAtLeast(1f),
                        )
                    } else null,
                ),
                color = expressiveAccent.copy(alpha = if (isBackground) 0.75f else 1f),
                modifier = if (isWordActive) {
                    Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            val edgeWidth = 8.dp.toPx()
                            val center = (size.width + edgeWidth * 2) * progress - edgeWidth
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Black, Color.Transparent),
                                    startX = center - edgeWidth,
                                    endX = center + edgeWidth,
                                ),
                                blendMode = BlendMode.DstIn,
                            )
                        }
                } else Modifier
            )
        }
    }
}
