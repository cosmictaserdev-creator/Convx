package com.convx.music.ui.component

import android.animation.ValueAnimator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first

internal val LyricScrollSpec = tween<Float>(550, easing = CubicBezierEasing(0.75f, 0f, 0.25f, 1f))

private const val RUBBER_STEP_MS = 40
private const val RUBBER_MAX_STEPS = 10
private const val NANOS_PER_MS = 1_000_000L

/** Spring with damping 0.8 and stiffness 130; past [RUBBER_SETTLE_NS] it is under a quarter pixel from rest. */
private const val RUBBER_DAMPING = 0.8
private val RUBBER_OMEGA = sqrt(130.0)
private val RUBBER_DAMPED_OMEGA = RUBBER_OMEGA * sqrt(1 - RUBBER_DAMPING * RUBBER_DAMPING)
private const val RUBBER_SETTLE_NS = 1_000_000_000L
private const val RUBBER_DONE_NS = RUBBER_SETTLE_NS + RUBBER_STEP_MS * RUBBER_MAX_STEPS * NANOS_PER_MS

/** Longer scrolls than this fraction of the viewport are a jump, not a nudge, and glide instead. */
private const val RUBBER_MAX_VIEWPORT = 0.9f

internal fun lyricCenterDistance(start: Int, end: Int, itemOffset: Int, itemSize: Int): Float =
    itemOffset + itemSize / 2f - (start + end) / 2f

/** Lines at or above the active one move at once; each line below it waits a little longer. */
internal fun rubberDelayMs(itemIndex: Int, anchorIndex: Int): Long =
    (itemIndex - anchorIndex).coerceIn(0, RUBBER_MAX_STEPS) * RUBBER_STEP_MS.toLong()

/** Fraction of a scroll still ahead [elapsedNs] after it started: 1 before the start, 0 once settled. */
internal fun rubberRemaining(elapsedNs: Long): Float {
    if (elapsedNs <= 0) return 1f
    if (elapsedNs >= RUBBER_SETTLE_NS) return 0f
    val t = elapsedNs / 1e9
    val decay = RUBBER_DAMPING * RUBBER_OMEGA
    return (exp(-decay * t) * (cos(RUBBER_DAMPED_OMEGA * t) + decay / RUBBER_DAMPED_OMEGA * sin(RUBBER_DAMPED_OMEGA * t))).toFloat()
}

/**
 * How far below its laid-out place a line is drawn [elapsedNs] into a scroll of [distance]: the list
 * has scrolled to where the line would be [delayNs] ago, so it trails the list, then catches up.
 */
internal fun rubberLag(distance: Float, elapsedNs: Long, delayNs: Long): Float =
    distance * (rubberRemaining(elapsedNs - delayNs) - rubberRemaining(elapsedNs))

/**
 * Makes a lyric scroll feel like lines tied together by a rubber string: the list springs to the new
 * line and every line below the active one trails it a beat later. One clock drives the scroll and
 * every line's lag, so the list and the lines always agree on the same frame and nothing pops in or
 * out at the edges. Overlapping scrolls add up, so a new line mid-scroll keeps the motion smooth.
 */
internal class LyricRubber {
    private class Pulse(val distance: Float, val startNs: Long, val anchor: Int)

    private val pulses = ArrayList<Pulse>()
    private var fired by mutableIntStateOf(0)
    private var nowNs by mutableLongStateOf(0L)

    /** Scroll already applied for the live pulses. */
    private var applied = 0f

    fun fire(distance: Float, anchorIndex: Int) {
        val pending = pulses.sumOf { it.distance.toDouble() }.toFloat() - applied
        pulses += Pulse(distance - pending, System.nanoTime(), anchorIndex)
        fired++
    }

    /** Read from a graphics layer, so only the layer redraws while the rubber moves. */
    fun lag(itemIndex: Int): Float {
        val now = nowNs
        var sum = 0f
        for (pulse in pulses) {
            sum += rubberLag(pulse.distance, now - pulse.startNs, rubberDelayMs(itemIndex, pulse.anchor) * NANOS_PER_MS)
        }
        return sum
    }

    suspend fun drive(state: LazyListState) {
        while (true) {
            snapshotFlow { fired }.first { pulses.isNotEmpty() }
            try {
                state.scroll {
                    while (pulses.isNotEmpty()) {
                        withFrameNanos { frame ->
                            pulses.removeAll { pulse ->
                                (frame - pulse.startNs >= RUBBER_DONE_NS).also { if (it) applied -= pulse.distance }
                            }
                            var target = 0f
                            for (pulse in pulses) target += pulse.distance * (1f - rubberRemaining(frame - pulse.startNs))
                            scrollBy(target - applied)
                            applied = target
                            nowNs = frame
                        }
                    }
                }
            } catch (e: CancellationException) {
                // A drag or a jump took the list over: drop the rubber and wait for the next line.
                currentCoroutineContext().ensureActive()
            }
            pulses.clear()
            applied = 0f
            nowNs = System.nanoTime()
        }
    }
}

@Composable
internal fun rememberLyricRubber(state: LazyListState): LyricRubber {
    val rubber = remember(state) { LyricRubber() }
    LaunchedEffect(rubber) { rubber.drive(state) }
    return rubber
}

/** Draws one lyric line where [LyricRubber] says it trails the list. */
internal fun Modifier.lyricRubber(rubber: LyricRubber, itemIndex: Int): Modifier =
    graphicsLayer { translationY = rubber.lag(itemIndex) }

/** One owner scrolls each list; new targets cancel the previous animation through LaunchedEffect. */
internal suspend fun LazyListState.followLyricLine(itemIndex: Int, animate: Boolean = true, rubber: LyricRubber? = null) {
    if (itemIndex !in 0 until layoutInfo.totalItemsCount) return
    val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
    var item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
    if (item == null) {
        scrollToItem(itemIndex, -viewport / 2)
        withFrameNanos { }
        item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex } ?: return
    }
    val distance = lyricCenterDistance(layoutInfo.viewportStartOffset, layoutInfo.viewportEndOffset, item.offset, item.size)
    if (abs(distance) <= 1f) return
    when {
        !animate -> scrollBy(distance)
        rubber != null && abs(distance) <= viewport * RUBBER_MAX_VIEWPORT && ValueAnimator.areAnimatorsEnabled() ->
            rubber.fire(distance, itemIndex)
        else -> animateScrollBy(distance, LyricScrollSpec)
    }
}

/**
 * Where the list has to move so the playhead sits mid-screen: halfway through a line's time the
 * point halfway to the next line is centred, so the text creeps up continuously instead of stepping.
 * Null while either line is off screen. [fraction] is how far the playhead is between the two lines.
 */
internal fun LazyListState.crawlDistance(itemIndex: Int, fraction: Float): Float? {
    val info = layoutInfo
    val from = info.visibleItemsInfo.firstOrNull { it.index == itemIndex } ?: return null
    val start = info.viewportStartOffset
    val end = info.viewportEndOffset
    val here = lyricCenterDistance(start, end, from.offset, from.size)
    val next = info.visibleItemsInfo.firstOrNull { it.index == itemIndex + 1 } ?: return here
    return here + fraction.coerceIn(0f, 1f) * (lyricCenterDistance(start, end, next.offset, next.size) - here)
}
