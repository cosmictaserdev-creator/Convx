package com.convx.music.playback.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.lyrics.LyricsDocument
import com.convx.music.lyrics.bitchord.LyricAlignment
import com.convx.music.lyrics.bitchord.LyricLine
import kotlinx.coroutines.flow.first
import kotlin.math.floor

/** Shared timed text: real word/syllable sweeps, duet alignment, seek, and interruptible following. */
@Composable
fun PlaybackLyrics(
    stateHolder: LyricsStateHolder,
    positionMs: () -> Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    fontSize: Float = 30f,
    animationsEnabled: Boolean = true,
    onProviderSelected: (String?) -> Unit = stateHolder::selectSource,
    providersEnabled: Boolean = true,
    inactiveBlur: Float = 0f,
    headerAction: @Composable (() -> Unit)? = null,
    offsetMs: Long = 0L,
) {
    val state by stateHolder.state.collectAsState()
    var showSources by remember { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (providersEnabled) LyricsAction(
                state.document?.sourceName ?: "Choose provider",
                { showSources = !showSources },
                textColor,
            )
            else BasicText("Local lyrics", style = TextStyle(color = textColor.copy(alpha = 0.65f), fontSize = 12.sp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LyricsAction(if (state.isLoading) "Searching…" else "Refresh", stateHolder::retry, textColor)
                headerAction?.invoke()
            }
        }
        AnimatedVisibility(showSources && providersEnabled) {
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 240.dp).background(textColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))) {
                item { ProviderRow("Automatic · best available timing", state.selectedSource == null, textColor) {
                    onProviderSelected(null); showSources = false
                } }
                itemsIndexed(stateHolder.sources, key = { _, source -> source.id }) { _, source ->
                    ProviderRow(source.label + if (source.wordSynced) " · word sync" else "", state.selectedSource == source.id, textColor) {
                        onProviderSelected(source.id); showSources = false
                    }
                }
            }
        }
        if (state.document != null) {
            key(state.document) {
                TimedLyrics(
                    document = requireNotNull(state.document),
                    positionMs = positionMs,
                    onSeek = { onSeek(lyricSeekPosition(it, offsetMs)) },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textColor = textColor,
                    fontSize = fontSize,
                    animationsEnabled = animationsEnabled,
                    inactiveBlur = inactiveBlur,
                )
            }
        } else {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                BasicText(
                    when {
                        state.query == null -> "Play a song to see its lyrics."
                        state.isLoading -> "Finding the words…"
                        else -> state.message ?: "Lyrics are unavailable for this song."
                    },
                    style = TextStyle(color = textColor.copy(alpha = 0.6f), fontSize = 16.sp),
                )
            }
        }
    }
}

@Composable
private fun ProviderRow(label: String, selected: Boolean, textColor: Color, onClick: () -> Unit) {
    BasicText(
        (if (selected) "✓  " else "    ") + label,
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick).padding(12.dp),
        style = TextStyle(color = if (selected) Color(0xFFFA586A) else textColor, fontSize = 13.sp),
    )
}

@Composable
private fun LyricsAction(label: String, onClick: () -> Unit, color: Color) {
    BasicText(label, Modifier.clickable(role = Role.Button, onClick = onClick).padding(vertical = 8.dp),
        style = TextStyle(color = color.copy(alpha = 0.65f), fontSize = 12.sp, fontWeight = FontWeight.Medium))
}

@Composable
private fun TimedLyrics(
    document: LyricsDocument,
    positionMs: () -> Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier,
    textColor: Color,
    fontSize: Float,
    animationsEnabled: Boolean,
    inactiveBlur: Float,
) {
    val listState = rememberLazyListState()
    var following by remember { mutableStateOf(true) }
    val dragged by listState.interactionSource.collectIsDraggedAsState()
    val active by remember(document, positionMs) {
        derivedStateOf { document.lines.indexOfLast { it.timeMs <= positionMs() }.coerceAtLeast(0) }
    }
    LaunchedEffect(dragged) { if (dragged) following = false }
    LaunchedEffect(active, following) {
        if (!following || !document.isSynced) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.totalItemsCount }.first { it > active }
        val viewport = listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
        val visible = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == active }
        if (!animationsEnabled || visible == null) listState.scrollToItem(active, -viewport / 3)
        else listState.animateScrollBy(visible.offset - viewport / 3f, tween(550, easing = androidx.compose.animation.core.CubicBezierEasing(0.75f, 0f, 0.25f, 1f)))
    }
    Column(modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) if (awaitPointerEvent(PointerEventPass.Initial).type == PointerEventType.Scroll) following = false
                }
            },
            contentPadding = PaddingValues(top = 80.dp, bottom = 160.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            itemsIndexed(document.lines, key = { index, line -> "$index:${line.timeMs}" }) { index, line ->
                TimedLine(line, positionMs, index == active || !document.isSynced, textColor, fontSize, animationsEnabled, inactiveBlur,
                    Modifier.fillMaxWidth().clickable(enabled = document.isSynced, role = Role.Button) {
                        onSeek(line.timeMs.coerceAtLeast(0L)); following = true
                    })
                line.background?.let { background ->
                    TimedLine(background, positionMs, index == active, textColor, fontSize * 0.65f, animationsEnabled, inactiveBlur, Modifier.fillMaxWidth().padding(top = 8.dp))
                }
            }
        }
        if (!following && document.isSynced) LyricsAction("Resume following", { following = true }, textColor)
    }
}

@Composable
private fun TimedLine(
    line: LyricLine,
    positionMs: () -> Long,
    active: Boolean,
    textColor: Color,
    fontSize: Float,
    animationsEnabled: Boolean,
    inactiveBlur: Float,
    modifier: Modifier,
) {
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val highlightPath = remember { Path() }
    val emphasis by animateFloatAsState(if (active) 1f else 0.35f, if (animationsEnabled) spring(stiffness = 260f) else tween(0))
    val scale by animateFloatAsState(if (active) 1f else 0.97f, if (animationsEnabled) spring(stiffness = 260f) else tween(0))
    BasicText(
        line.text,
        modifier.graphicsLayer { alpha = emphasis; scaleX = scale; scaleY = scale }
            .then(if (!active && inactiveBlur > 0f) Modifier.blur(inactiveBlur.dp) else Modifier)
            .drawWithContent {
                drawContent()
                val measured = layout
                if (active && measured != null && line.words.isNotEmpty()) {
                    val now = positionMs()
                    highlightPath.reset()
                    line.words.forEachIndexed { index, word ->
                        val span = line.wordSpans.getOrNull(index) ?: return@forEachIndexed
                        val count = word.charsSungAt(now).coerceIn(0f, word.text.length.toFloat())
                        val whole = floor(count).toInt()
                        for (character in 0 until minOf(whole + 1, word.text.length)) {
                            val fraction = if (character < whole) 1f else count - whole
                            val offset = span.first + character
                            if (fraction <= 0f || offset !in line.text.indices) continue
                            val bounds = measured.getBoundingBox(offset)
                            val rtl = measured.getBidiRunDirection(offset) == androidx.compose.ui.text.style.ResolvedTextDirection.Rtl
                            highlightPath.addRect(Rect(
                                if (rtl) bounds.right - bounds.width * fraction else bounds.left,
                                bounds.top,
                                if (rtl) bounds.right else bounds.left + bounds.width * fraction,
                                bounds.bottom,
                            ))
                        }
                    }
                    clipPath(highlightPath) { drawText(measured, color = textColor) }
                }
            },
        style = TextStyle(
            color = textColor.copy(alpha = if (active && line.words.isNotEmpty()) 0.38f else 1f),
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.32f).sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (line.alignment == LyricAlignment.End) androidx.compose.ui.text.style.TextAlign.End else androidx.compose.ui.text.style.TextAlign.Start,
        ),
        onTextLayout = { layout = it },
    )
}
