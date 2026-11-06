package com.convx.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.drawText
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.animation.ValueAnimator
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.composables.sfsymbols.SfSymbol
import com.convx.music.constants.FisheyeRadiusKey
import com.convx.music.constants.FisheyeTextSizeKey
import com.convx.music.constants.FisheyeZoomKey
import com.convx.music.utils.rememberPreference
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.ui.component.flamingo.CharGrowth
import com.convx.music.ui.component.flamingo.HeldWordGrowth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * Word-by-word kinetic lyrics. Each line is cut into rows of at most three
 * words; a held word stands alone, huge, and now and then one word in a phrase is blown up. Rows
 * fill the screen top to bottom with the one being sung in the middle, under a real barrel lens
 * (Android 13+) that bends the whole stack. Every row has
 * its own size, tilt, offset and highlight style, and symbols and backing vocals slide in from the
 * side. All of it is drawn from a generator seeded by the song's lyrics: the same song always
 * plays out the same way, and no two songs look alike.
 */
@Composable
private fun FisheyeStack(
    lines: List<LyricsEntry>,
    fontFamily: FontFamily,
    positionMs: () -> Long,
    highlight: Color,
    /** Seeks to a word the reader tapped; null when seeking isn't allowed. */
    onSeek: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
    animationsEnabled: Boolean = true,
    /** How strongly the middle is magnified, 0 (flat) to ~0.6. */
    lensStrength: Float = FISHEYE_LENS_STRENGTH,
    /** The lens' reach as a share of the pane's long side. */
    lensRadius: Float = FISHEYE_LENS_RADIUS,
    /** Multiplies the type size, on top of the pane-width fit. */
    textScale: Float = 1f,
) {
    val score = remember(lines) { composeFisheyeScore(lines) }
    val rows = score.rows
    if (rows.isEmpty()) return
    val clock = remember { mutableLongStateOf(0L) }
    val currentPosition by androidx.compose.runtime.rememberUpdatedState(positionMs)
    LaunchedEffect(animationsEnabled) {
        while (true) {
            if (animationsEnabled) withFrameNanos { clock.longValue = currentPosition() }
            else { clock.longValue = currentPosition(); delay(100) }
        }
    }
    val current by remember(rows) { derivedStateOf { rows.currentIndex(clock.longValue) } }
    val scroll = remember(rows) { Animatable(current.toFloat()) }
    LaunchedEffect(current, animationsEnabled) {
        if (animationsEnabled) scroll.animateTo(current.toFloat(), spring(dampingRatio = 1f, stiffness = 380f))
        else scroll.snapTo(current.toFloat())
    }

    // Dragging browses the rows; released, it snaps to one and returns to the song after a pause.
    val browse = remember(rows) { Animatable(0f) }
    var dragging by remember { mutableStateOf(false) }
    var browseEpoch by remember { mutableIntStateOf(0) }
    val latestCurrent by androidx.compose.runtime.rememberUpdatedState(current)
    LaunchedEffect(dragging, current, browseEpoch, animationsEnabled) {
        if (dragging || browse.value == 0f) return@LaunchedEffect
        if (animationsEnabled) browse.animateTo(kotlin.math.round(browse.value), spring(dampingRatio = 1f, stiffness = 500f))
        else browse.snapTo(kotlin.math.round(browse.value))
        delay(BROWSE_RESUME_MS)
        if (animationsEnabled) browse.animateTo(0f, spring(dampingRatio = 1f, stiffness = 120f)) else browse.snapTo(0f)
    }
    val scope = rememberCoroutineScope()
    val stepPx = with(LocalDensity.current) { 64.dp.toPx() }
    val focus by remember(rows) {
        derivedStateOf { (current + kotlin.math.round(browse.value).toInt()).coerceIn(0, rows.lastIndex) }
    }
    val backing by remember(score) { derivedStateOf { score.backingAt(clock.longValue) } }

    var size by remember { mutableStateOf(IntSize.Zero) }
    val lens = remember(size, lensStrength, lensRadius) { if (size == IntSize.Zero) null else fisheyeLens(size, lensStrength, lensRadius) }
    // Type is sized from the pane's width so rows sit inside the viewport on any screen.
    val fit = with(LocalDensity.current) { (size.width.toDp().value / FIT_REFERENCE_DP).coerceIn(0.55f, 1f) * textScale }
    // The drag is reported up as a scroll so the player can hide its controls as it would for a list.
    val scrollUp = remember { NestedScrollDispatcher() }
    val noScrollUp = remember { object : NestedScrollConnection {} }
    Box(
        modifier
            .onSizeChanged { size = it }
            .nestedScroll(noScrollUp, scrollUp)
            .pointerInput(rows) {
                detectVerticalDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                    onVerticalDrag = { change, dy ->
                        change.consume()
                        scrollUp.dispatchPostScroll(Offset(0f, dy), Offset.Zero, NestedScrollSource.UserInput)
                        val upper = (rows.lastIndex - latestCurrent).toFloat()
                        val lower = (-latestCurrent).toFloat()
                        scope.launch { browse.snapTo((browse.value - dy / stepPx).coerceIn(lower, upper)) }
                    },
                )
            }
            .pointerInput(rows) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == androidx.compose.ui.input.pointer.PointerEventType.Scroll) {
                            val dy = event.changes.sumOf { it.scrollDelta.y.toDouble() }.toFloat()
                            if (dy != 0f) {
                                event.changes.forEach { it.consume() }
                                scope.launch {
                                    browse.snapTo((browse.value + dy / stepPx).coerceIn(-latestCurrent.toFloat(), (rows.lastIndex - latestCurrent).toFloat()))
                                    browseEpoch++
                                }
                            }
                        }
                    }
                }
            }
            .graphicsLayer {
                val seconds = clock.longValue / 1000f
                rotationX = (if (animationsEnabled) DRIFT_X_DEGREES else 0f) * sin(seconds * 2f * kotlin.math.PI.toFloat() / DRIFT_X_PERIOD_S)
                rotationY = (if (animationsEnabled) DRIFT_Y_DEGREES else 0f) * sin(seconds * 2f * kotlin.math.PI.toFloat() / DRIFT_Y_PERIOD_S)
                cameraDistance = 14f * density
                renderEffect = lens
            },
        contentAlignment = Alignment.Center,
    ) {
        val first = (focus - ROWS_ABOVE).coerceAtLeast(0)
        val last = (focus + ROWS_BELOW).coerceAtMost(rows.lastIndex)
        val window = (first..last).toList()
        StackLayout(firstIndex = first, fitShare = ROW_FIT_BASE * (1f - lensStrength), position = { scroll.value + browse.value }) {
            for (index in window) {
                val row = rows[index]
                RowView(
                    row = row,
                    rowState = when {
                        index < current -> RowState.PAST
                        index == current -> RowState.CURRENT
                        else -> RowState.NEXT
                    },
                    clock = { clock.longValue },
                    distance = { index - (scroll.value + browse.value) },
                    fontFamily = fontFamily,
                    highlight = highlight,
                    onSeek = onSeek,
                    animationsEnabled = animationsEnabled,
                    fit = fit,
                )
            }
        }
        // Backing vocals hang under the stack and slide in from their side while they are sung.
        for (phrase in score.backing) {
            AnimatedVisibility(
                visible = backing === phrase,
                enter = if (animationsEnabled) fadeIn() + slideInHorizontally { it * phrase.side } else fadeIn(androidx.compose.animation.core.tween(0)),
                exit = if (animationsEnabled) fadeOut() + slideOutHorizontally { it * phrase.side } else fadeOut(androidx.compose.animation.core.tween(0)),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
            ) {
                FisheyeText(
                    text = phrase.text,
                    fontFamily = fontFamily,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = highlight.copy(alpha = 0.75f),
                    maxLines = 2,
                )
            }
        }
    }
}

/** Stacks rows top to bottom and slides the stack so the fractional row [position] sits mid-height. */
@Composable
private fun StackLayout(firstIndex: Int, fitShare: Float, position: () -> Float, content: @Composable () -> Unit) {
    Layout(content = content, modifier = Modifier.fillMaxWidth()) { measurables, constraints ->
        val width = constraints.maxWidth
        val placeables = measurables.map { it.measure(Constraints(maxHeight = constraints.maxHeight)) }
        // A row wider than the lens' clear middle is shrunk to fit rather than cut: the lens magnifies
        // the centre, and rows drift sideways, so the row must fit well inside the pane before it.
        val fitWidth = width * fitShare
        val scales = FloatArray(placeables.size) { (fitWidth / placeables[it].width.coerceAtLeast(1)).coerceAtMost(1f) }
        val gap = 6.dp.toPx()
        val tops = FloatArray(placeables.size)
        var y = 0f
        placeables.forEachIndexed { i, p -> tops[i] = y; y += p.height * scales[i] + gap }
        val centres = FloatArray(placeables.size) { tops[it] + placeables[it].height * scales[it] / 2f }
        val local = (position() - firstIndex).coerceIn(0f, (placeables.size - 1).coerceAtLeast(0).toFloat())
        val below = floor(local).toInt().coerceIn(0, (centres.size - 1).coerceAtLeast(0))
        val above = (below + 1).coerceAtMost(centres.size - 1)
        val focusY = if (centres.isEmpty()) 0f else centres[below] + (centres[above] - centres[below]) * (local - below)
        val height = constraints.maxHeight
        layout(width, height) {
            placeables.forEachIndexed { i, p ->
                val s = scales[i]
                val top = height / 2f - focusY + tops[i] - p.height * (1f - s) / 2f
                p.placeWithLayer((width - p.width) / 2, top.roundToInt()) {
                    scaleX = s
                    scaleY = s
                }
            }
        }
    }
}

private enum class RowState { PAST, CURRENT, NEXT }

@Composable
private fun RowView(
    row: StackRow,
    rowState: RowState,
    clock: () -> Long,
    distance: () -> Float,
    fontFamily: FontFamily,
    highlight: Color,
    onSeek: ((Long) -> Unit)?,
    animationsEnabled: Boolean,
    fit: Float,
) {
    // Icons arrive when their row is reached and then stay with it.
    val reached = rowState != RowState.NEXT
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer {
            val depth = abs(distance())
            // Further rows are smaller, dimmer and less tilted; the lens does the bending.
            val scale = (1f - 0.1f * depth + 0.006f * depth * depth).coerceIn(0.5f, 1f)
            scaleX = scale
            scaleY = scale
            alpha = (1f - 0.1f * depth).coerceIn(0.3f, 1f) * if (distance() > 0.5f) 0.6f else 1f
            rotationZ = row.tilt * (1f - depth.coerceAtMost(1f) * 0.6f)
            translationX = row.shift * size.width
        },
    ) {
        val symbol = row.sticker
        if (symbol != null && row.iconSide == 0) StickerSlide(reached, 0, symbol, highlight, animationsEnabled)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            if (symbol != null && row.iconSide < 0) StickerSlide(reached, -1, symbol, highlight, animationsEnabled)
            for (word in row.words) {
                WordView(word, row, rowState, clock, fontFamily, highlight, onSeek, animationsEnabled, fit)
            }
            if (symbol != null && row.iconSide > 0) StickerSlide(reached, 1, symbol, highlight, animationsEnabled)
        }
    }
}

/** A symbol that flies in from its side (or rises in the middle) once its row is reached. */
@Composable
private fun StickerSlide(visible: Boolean, side: Int, symbol: SfSymbol, tint: Color, animationsEnabled: Boolean) {
    // Starts hidden even for a row that is already reached, so the icon always arrives with its pop.
    val shown = remember { androidx.compose.animation.core.MutableTransitionState(!animationsEnabled && visible) }
    shown.targetState = visible
    val pop = androidx.compose.animation.core.spring<Float>(dampingRatio = 0.45f, stiffness = 380f)
    AnimatedVisibility(
        visibleState = shown,
        enter = if (!animationsEnabled) fadeIn(androidx.compose.animation.core.tween(0)) else
            fadeIn(androidx.compose.animation.core.tween(180)) + androidx.compose.animation.scaleIn(pop, initialScale = 0.2f) +
                if (side == 0) androidx.compose.animation.slideInVertically { it / 2 } else slideInHorizontally { it * side },
        exit = if (!animationsEnabled) fadeOut(androidx.compose.animation.core.tween(0)) else
            fadeOut(androidx.compose.animation.core.tween(160)) + androidx.compose.animation.scaleOut(targetScale = 0.4f),
    ) {
        SymbolIcon(symbol = symbol, contentDescription = null, size = 34.dp, tint = tint)
    }
}

@Composable
private fun WordView(
    word: StackWord,
    row: StackRow,
    rowState: RowState,
    clock: () -> Long,
    fontFamily: FontFamily,
    highlight: Color,
    onSeek: ((Long) -> Unit)?,
    animationsEnabled: Boolean,
    fit: Float,
) {
    val style = row.style
    fun progress() = if (animationsEnabled) word.progress(clock()) else if (clock() >= word.startMs) 1f else 0f
    val block = if (style == HighlightStyle.WHITE_SQUARE) Color.White else highlight
    // Ink on a block follows the block's brightness: a white accent (Apple Music and Gradient
    // backgrounds) under white text was white on white.
    val sungInk = if (style.drawsBlock && block.luminance() > 0.6f) Color(0xFF111111) else Color.White
    val color = when (rowState) {
            RowState.NEXT -> highlight.copy(alpha = 0.55f)
            RowState.PAST -> if (style == HighlightStyle.INK) Color.White.copy(alpha = 0.85f) else highlight.copy(alpha = 0.9f)
            // Read per frame through the clock: the word turns as it is sung.
            RowState.CURRENT -> {
                val sung = progress() > 0f
                if (!sung) highlight.copy(alpha = 0.7f)
                else if (style == HighlightStyle.INK) lerp(highlight, Color.White, progress())
                else sungInk
            }
        }
    Box(
        modifier = Modifier
            .then(if (onSeek != null) Modifier.clickable { onSeek(word.startMs) } else Modifier)
            .drawBehind {
                if (rowState == RowState.NEXT) return@drawBehind
                val fill = if (rowState == RowState.CURRENT) progress() else 1f
                if (fill <= 0f) return@drawBehind
                val pad = 3.dp.toPx()
                val faded = rowState == RowState.PAST
                when (style) {
                    HighlightStyle.BLOCK, HighlightStyle.WHITE_SQUARE -> drawRoundRect(
                        color = block.copy(alpha = if (faded) 0.22f else 0.95f),
                        topLeft = Offset(-pad, size.height * 0.1f),
                        size = Size((size.width + pad * 2) * fill, size.height * 0.82f),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                    HighlightStyle.UNDERLINE -> drawRoundRect(
                        color = highlight.copy(alpha = if (faded) 0.3f else 1f),
                        topLeft = Offset(0f, size.height * 0.9f),
                        size = Size(size.width * fill, 4.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                    )
                    HighlightStyle.INK -> Unit
                }
            }
            .padding(horizontal = 4.dp),
    ) {
        val growth = word.growth.takeIf { animationsEnabled }
        if (growth == null && rowState == RowState.CURRENT) {
            // The colour sweeps across the word as it is sung and the word lifts a little with it,
            // the way Convx Lyrics reads, without its glow.
            SweepText(
                text = word.text,
                fontFamily = fontFamily,
                fontSize = (word.sizeSp * fit).sp,
                sung = if (style == HighlightStyle.INK) Color.White else sungInk,
                unsung = highlight.copy(alpha = 0.7f),
                progress = { progress() },
                lift = animationsEnabled,
            )
        } else if (growth == null) {
            FisheyeText(text = word.text, fontFamily = fontFamily, fontSize = (word.sizeSp * fit).sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        } else Row {
            // Held-note growth, letter by letter.
            val letter = remember { CharGrowth() }
            word.text.forEachIndexed { i, char ->
                FisheyeText(
                    text = char.toString(),
                    fontFamily = fontFamily,
                    fontSize = (word.sizeSp * fit).sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                    modifier = Modifier.graphicsLayer {
                        growth.sample(i, clock().toFloat(), letter)
                        val scale = 1f + (letter.scale - 1f) * GROWTH_GAIN
                        scaleX = scale
                        scaleY = scale
                        val em = (word.sizeSp * fit).sp.toPx()
                        translationX = letter.shift * em * GROWTH_GAIN
                        translationY = -letter.rise * em * LETTER_RISE_EM
                    },
                )
            }
        }
    }
}

/** A word whose colour sweeps from [unsung] to [sung] with [progress], softly edged, lifting as it goes. */
@Composable
private fun SweepText(
    text: String,
    fontFamily: FontFamily,
    fontSize: androidx.compose.ui.unit.TextUnit,
    sung: Color,
    unsung: Color,
    progress: () -> Float,
    lift: Boolean,
) {
    val measurer = androidx.compose.ui.text.rememberTextMeasurer()
    val style = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontSize = fontSize, fontWeight = FontWeight.Bold)
    val layout = remember(text, style) { measurer.measure(text, style, maxLines = 1, softWrap = false) }
    val density = LocalDensity.current
    Box(
        Modifier
            .size(with(density) { layout.size.width.toDp() }, with(density) { layout.size.height.toDp() })
            .graphicsLayer {
                if (lift) {
                    val p = progress().coerceIn(0f, 1f)
                    translationY = -fontSize.toPx() * SWEEP_LIFT_EM * (p * p * (3f - 2f * p))
                }
            }
            .drawBehind {
                val p = progress().coerceIn(0f, 1f)
                when {
                    p <= 0f -> drawText(layout, color = unsung)
                    p >= 1f -> drawText(layout, color = sung)
                    else -> {
                        val edge = p * (1f + SWEEP_SPREAD)
                        val brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            0f to sung,
                            (edge - SWEEP_SPREAD).coerceIn(0f, 1f) to sung,
                            edge.coerceIn(0f, 1f) to unsung,
                            1f to unsung,
                            startX = 0f,
                            endX = size.width,
                        )
                        drawText(layout, brush = brush)
                    }
                }
            },
    )
}

private const val SWEEP_SPREAD = 0.3f
private const val SWEEP_LIFT_EM = 0.07f

internal enum class HighlightStyle(val drawsBlock: Boolean) {
    BLOCK(true), WHITE_SQUARE(true), UNDERLINE(false), INK(false),
}

internal class StackWord(val text: String, val startMs: Long, val endMs: Long, var sizeSp: Float = 0f, val piece: Boolean = false) {
    val growth = (endMs - startMs).toFloat().let { held ->
        if (HeldWordGrowth.canGrow(text, held)) HeldWordGrowth(startMs.toFloat(), startMs + held, text.length) else null
    }

    fun progress(nowMs: Long): Float =
        ((nowMs - startMs).toFloat() / (endMs - startMs).coerceAtLeast(1L)).coerceIn(0f, 1f)

    /** A word too long for one row is cut into even pieces, each sung over its share of the word's time. */
    fun pieces(): List<StackWord> {
        if (text.length <= MAX_WORD_CHARS) return listOf(this)
        val count = (text.length + MAX_WORD_CHARS - 1) / MAX_WORD_CHARS
        val size = (text.length + count - 1) / count
        return (0 until count).map { i ->
            val from = i * size
            val to = minOf(text.length, from + size)
            StackWord(
                text.substring(from, to),
                startMs + (endMs - startMs) * from / text.length,
                startMs + (endMs - startMs) * to / text.length,
                piece = true,
            )
        }
    }
}

internal class StackRow(
    val words: List<StackWord>,
    val style: HighlightStyle,
    val tilt: Float,
    /** Sideways offset, as a share of the row's width. */
    val shift: Float,
    val sticker: SfSymbol?,
    /** -1 left, 1 right, 0 centred above the row. */
    val iconSide: Int,
) {
    val startMs = words.first().startMs
}

internal class BackingPhrase(val text: String, val startMs: Long, val endMs: Long, val side: Int)

internal class Score(val rows: List<StackRow>, val backing: List<BackingPhrase>) {
    fun backingAt(nowMs: Long): BackingPhrase? =
        backing.firstOrNull { nowMs >= it.startMs - BACKING_LEAD_MS && nowMs <= it.endMs + BACKING_HOLD_MS }
}

/**
 * Cuts the lyrics into rows and decides every random choice, seeded by the lyrics themselves:
 * stable for a song across opens and seeks, different between songs.
 */
internal fun composeFisheyeScore(lines: List<LyricsEntry>): Score {
    val random = Random(lines.joinToString("\n") { it.text }.hashCode())
    val rows = mutableListOf<StackRow>()
    val backing = mutableListOf<BackingPhrase>()
    for (line in lines.sortedBy { it.time }) {
        val words = line.words.orEmpty().mapNotNull { word ->
            word.text.trim().takeIf { it.isNotEmpty() && word.startTime.isFinite() && word.endTime.isFinite() && word.endTime >= word.startTime }?.let {
                StackWord(it, (word.startTime * 1000).toLong(), (word.endTime * 1000).toLong())
            }
        }
        if (line.isBackground) {
            val start = words.firstOrNull()?.startMs ?: line.time
            backing += BackingPhrase(line.text.trim(), start, words.lastOrNull()?.endMs ?: (start + 2_000), if (random.nextBoolean()) 1 else -1)
            continue
        }
        val displayWords = words.ifEmpty {
            if (line.text.isBlank()) emptyList() else line.text.trim().split(Regex("\\s+"))
                .map { StackWord(it, line.time, line.time) }
        }.flatMap { it.pieces() }
        var i = 0
        while (i < displayWords.size) {
            val held = displayWords[i].piece || displayWords[i].endMs - displayWords[i].startMs >= HELD_WORD_MS
            val count = if (held) 1 else pickRowLength(random)
            val end = minOf(i + count, displayWords.size)
            val nextHeld = (i + 1 until end).firstOrNull { displayWords[it].piece || displayWords[it].endMs - displayWords[it].startMs >= HELD_WORD_MS }
            val group = displayWords.subList(i, nextHeld ?: end)
            i += group.size
            val single = group.size == 1
            val base = if (single) random.nextInt(48, 64).toFloat() else random.nextInt(26, 34).toFloat()
            group.forEach { it.sizeSp = base }
            // Now and then one word in a phrase is blown up.
            if (!single && random.nextInt(BIG_WORD_ONE_IN) == 0) group[random.nextInt(group.size)].sizeSp = base * 1.8f
            val sticker = group.firstNotNullOfOrNull { LyricStickers.symbolFor(it.text) }
            rows += StackRow(
                words = group,
                style = HighlightStyle.entries[random.nextInt(HighlightStyle.entries.size)],
                tilt = (random.nextFloat() - 0.5f) * 8f,
                shift = (random.nextFloat() - 0.5f) * ROW_SHIFT_RANGE,
                sticker = sticker,
                iconSide = if (single && random.nextInt(3) == 0) 0 else if (random.nextBoolean()) 1 else -1,
            )
        }
    }
    return Score(rows, backing)
}

/** Mostly two- and three-word phrases, with the odd word alone. */
private fun pickRowLength(random: Random): Int = when (random.nextInt(10)) {
    0 -> 1
    1, 2, 3, 4, 5 -> 2
    else -> 3
}

/** The last row whose first word has started by [nowMs]. */
private fun List<StackRow>.currentIndex(nowMs: Long): Int {
    var low = 0
    var high = lastIndex
    var found = 0
    while (low <= high) {
        val mid = (low + high) ushr 1
        if (this[mid].startMs <= nowMs) {
            found = mid
            low = mid + 1
        } else high = mid - 1
    }
    return found
}

/**
 * A true barrel lens over the whole stack: each pixel samples nearer the centre the closer to it it
 * is, so the middle bulges toward the viewer and straight rows bend toward the rim. Android 13+.
 */
private fun fisheyeLens(size: IntSize, strength: Float, radius: Float): androidx.compose.ui.graphics.RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || size.width <= 0 || size.height <= 0) return null
    val shader = RuntimeShader(FISHEYE_AGSL).apply {
        setFloatUniform("size", size.width.toFloat(), size.height.toFloat())
        setFloatUniform("strength", strength)
        setFloatUniform("radiusScale", radius)
    }
    return android.graphics.RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
}

private const val FISHEYE_AGSL = """
    uniform shader content;
    uniform float2 size;
    uniform float strength;
    uniform float radiusScale;
    half4 main(float2 p) {
        float2 c = size * 0.5;
        // Sized past the long side so the rim never shows and the whole stack curves.
        float radius = max(size.x, size.y) * radiusScale;
        float2 d = (p - c) / radius;
        float r = min(length(d), 1.0);
        // Barrel: f(r) = r * (1 - k * (1 - r^2)). The quadratic keeps the magnified middle wide
        // and even instead of a tight bulge, then bends rows smoothly toward the rim.
        float mapped = r * (1.0 - strength * (1.0 - r * r));
        float2 src = c + (r > 0.0 ? d * (mapped / r) : float2(0.0)) * radius;
        return content.eval(src);
    }
"""

private const val ROWS_ABOVE = 9
private const val ROWS_BELOW = 9
private const val HELD_WORD_MS = 900L
private const val BIG_WORD_ONE_IN = 6
/** Words here are far bigger than in Convx Lyrics, so the held-note swell is pushed further. */
private const val GROWTH_GAIN = 2f
private const val LETTER_RISE_EM = 0.06f
private const val BACKING_LEAD_MS = 250L
private const val BACKING_HOLD_MS = 400L
/** Idle time after browsing before the stack follows the song again. */
private const val BROWSE_RESUME_MS = 3_000L
/** Gentler than a full barrel: the middle is magnified ~1.5x, so text stays sharp and inside the pane. */
const val FISHEYE_LENS_STRENGTH = 0.34f
const val FISHEYE_LENS_RADIUS = 0.7f
/** The pane width, in dp, at which the type is drawn at its composed size. */
private const val FIT_REFERENCE_DP = 420f
/** Share of the pane width a row may fill before the lens magnifies it. */
private const val ROW_FIT_BASE = 0.82f
/** Longest word a row holds whole; longer ones are cut into pieces on rows of their own. */
private const val MAX_WORD_CHARS = 10
/** Total sideways drift of a row as a share of the pane, kept inside what the fit leaves free. */
private const val ROW_SHIFT_RANGE = 0.06f
private const val DRIFT_X_DEGREES = 5f
private const val DRIFT_Y_DEGREES = 8f
private const val DRIFT_X_PERIOD_S = 9f
private const val DRIFT_Y_PERIOD_S = 13f

@Composable
private fun FisheyeText(
    text: String,
    fontFamily: FontFamily,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    androidx.compose.foundation.text.BasicText(text, modifier,
        style = androidx.compose.ui.text.TextStyle(color = color, fontFamily = fontFamily, fontSize = fontSize,
            fontWeight = fontWeight, fontStyle = fontStyle), maxLines = maxLines)
}

/** Word-timed lyrics as a stack of kinetic rows under a barrel lens. */
@Composable
fun FisheyeLyrics(
    lines: List<LyricsEntry>,
    fontFamily: FontFamily,
    positionMs: () -> Long,
    highlight: Color,
    onSeek: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val (zoom) = rememberPreference(FisheyeZoomKey, FISHEYE_LENS_STRENGTH)
    val (radius) = rememberPreference(FisheyeRadiusKey, FISHEYE_LENS_RADIUS)
    val (textScale) = rememberPreference(FisheyeTextSizeKey, 1f)
    FisheyeStack(
        lines, fontFamily, positionMs, highlight, onSeek, modifier,
        animationsEnabled = ValueAnimator.areAnimatorsEnabled(),
        lensStrength = zoom, lensRadius = radius, textScale = textScale,
    )
}
