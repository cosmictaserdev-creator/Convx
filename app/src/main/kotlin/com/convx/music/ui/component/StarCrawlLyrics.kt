package com.convx.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.primaryLyricLineIndex
import com.convx.music.lyrics.wordsWithSpacing
import kotlin.math.abs

private val CrawlInk = Color.White
private const val UNSUNG_ALPHA = 0.38f
private const val TILT_DEGREES = 52f
private const val CAMERA_DISTANCE_DP = 14f

/** A long instrumental gap must not stall the crawl for its whole length; it settles on the next line this early. */
private const val CRAWL_SPAN_CAP_MS = 6_000L

/** Lyrics laid on a tilted plane that fades away into the distance, like an opening crawl. */
@Composable
fun StarCrawlLyrics(
    lines: List<LyricsEntry>,
    fontFamily: FontFamily,
    fontSize: Float,
    positionMs: () -> Long,
    isSynced: Boolean,
    autoScrollEnabled: Boolean,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val current = remember(lines, isSynced) { mutableIntStateOf(-1) }
    var height by remember { mutableIntStateOf(0) }
    val frameMs = remember { mutableLongStateOf(0L) }

    LaunchedEffect(lines, isSynced) {
        if (!isSynced) return@LaunchedEffect
        while (true) {
            withFrameNanos { }
            val position = positionMs()
            frameMs.longValue = position
            current.intValue = primaryLyricLineIndex(lines, position)
        }
    }
    // The playhead, not the line change, drives the scroll, so the text creeps up a little every frame.
    LaunchedEffect(lines, isSynced, autoScrollEnabled, height) {
        if (!autoScrollEnabled || !isSynced || height == 0) return@LaunchedEffect
        while (true) {
            withFrameNanos { }
            val index = current.intValue
            if (index < 0) continue
            val start = lines[index].time
            val span = (lines.getOrNull(index + 1)?.time ?: start) - start
            val fraction = if (span > 0) (positionMs() - start).toFloat() / span.coerceAtMost(CRAWL_SPAN_CAP_MS) else 0f
            val distance = listState.crawlDistance(index, fraction)
            if (distance == null) listState.followLyricLine(index, animate = false)
            else if (abs(distance) > 0.5f) listState.scrollBy(distance)
        }
    }

    Box(
        modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Black,
                        startY = 0f,
                        endY = size.height,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
            .onSizeChanged { height = it.height },
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = with(density) { (height / 2f).toDp() }),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationX = TILT_DEGREES
                    cameraDistance = CAMERA_DISTANCE_DP * this.density
                    transformOrigin = TransformOrigin(0.5f, 0.75f)
                },
        ) {
            itemsIndexed(lines, key = { index, line -> "$index-${line.time}" }) { index, line ->
                val alpha by animateFloatAsState(
                    targetValue = when {
                        !isSynced || index == current.intValue -> 1f
                        index < current.intValue -> 0.35f
                        else -> 0.6f
                    },
                    animationSpec = tween(400),
                    label = "crawl_line_alpha",
                )
                val isActive = isSynced && index == current.intValue
                val words = remember(line, lines) { if (isSynced) crawlWords(line, lines.getOrNull(index + 1)?.time) else emptyList() }
                val text = remember(line, words) { if (words.isEmpty()) line.text else words.joinToString("") { it.text } }
                var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
                BasicText(
                    text = text,
                    onTextLayout = { layout = it },
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = (fontSize * 1.15f).sp,
                        lineHeight = (fontSize * 1.5f).sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) CrawlInk.copy(alpha = UNSUNG_ALPHA) else CrawlInk,
                        textAlign = TextAlign.Center,
                        lineBreak = LineBreak.Heading,
                        shadow = Shadow(CrawlInk.copy(alpha = 0.5f), blurRadius = if (isActive) 18f else 0f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { this.alpha = alpha }
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            onSeek(line.time.toInt())
                        }
                        .padding(horizontal = 36.dp, vertical = 10.dp)
                        .then(if (isActive) Modifier.litWords(words, { layout }, { frameMs.longValue }) else Modifier),
                )
            }
        }
    }
}

/** One word of a crawl line and the playback window, in ms, in which it is sung. */
internal data class CrawlWord(val text: String, val startMs: Long, val endMs: Long)

/** A line's words with their timing: the real word times when it has them, otherwise spread over the line by length. */
internal fun crawlWords(line: LyricsEntry, nextLineMs: Long?): List<CrawlWord> {
    line.wordsWithSpacing().takeIf { it.isNotEmpty() }?.let { timed ->
        return timed.map { CrawlWord(it.text, (it.startTime * 1000).toLong(), (it.endTime * 1000).toLong()) }
    }
    val tokens = line.text.split(' ').filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return emptyList()
    val span = ((nextLineMs ?: (line.time + DEFAULT_LINE_MS)) - line.time).coerceIn(MIN_LINE_MS, MAX_LINE_MS)
    val weight = tokens.sumOf { it.length + 1 }
    var from = line.time
    return tokens.mapIndexed { i, token ->
        val share = span * (token.length + 1) / weight
        CrawlWord(if (i == tokens.lastIndex) token else "$token ", from, from + share).also { from += share }
    }
}

private const val DEFAULT_LINE_MS = 4_000L
private const val MIN_LINE_MS = 800L
private const val MAX_LINE_MS = 6_000L

/** How far the playhead is through [word], 0 before it starts and 1 once it is sung. */
internal fun wordProgress(word: CrawlWord, positionMs: Long): Float =
    ((positionMs - word.startMs).toFloat() / (word.endMs - word.startMs).coerceAtLeast(1L)).coerceIn(0f, 1f)

/**
 * Brightens each word as the playhead passes through it by drawing the laid-out line again, clipped
 * to the word. Only the draw pass reads the playhead, so the text is never laid out again while it plays.
 */
private fun Modifier.litWords(words: List<CrawlWord>, layout: () -> TextLayoutResult?, positionMs: () -> Long): Modifier =
    drawWithCache {
        val text = layout()
        var offset = 0
        val shapes = if (text == null) emptyList() else words.map { word ->
            text.getPathForRange(offset, (offset + word.text.length).coerceAtMost(text.layoutInput.text.length)).also { offset += word.text.length }
        }
        onDrawWithContent {
            drawContent()
            if (text == null) return@onDrawWithContent
            val now = positionMs()
            words.forEachIndexed { i, word ->
                val progress = wordProgress(word, now)
                // Painting the full ink at alpha p over the unsung ink lands on UNSUNG + (1 - UNSUNG) * p.
                if (progress > 0f) clipPath(shapes[i]) { drawText(text, color = CrawlInk.copy(alpha = progress), shadow = Shadow.None) }
            }
        }
    }
