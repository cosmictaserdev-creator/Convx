package com.convx.music.ui.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.withFrameNanos
import kotlin.math.abs

internal val LyricScrollSpec = tween<Float>(550, easing = CubicBezierEasing(0.75f, 0f, 0.25f, 1f))

internal fun lyricCenterDistance(start: Int, end: Int, itemOffset: Int, itemSize: Int): Float =
    itemOffset + itemSize / 2f - (start + end) / 2f

/** One owner scrolls each list; new targets cancel the previous animation through LaunchedEffect. */
internal suspend fun LazyListState.followLyricLine(itemIndex: Int, animate: Boolean = true) {
    if (itemIndex !in 0 until layoutInfo.totalItemsCount) return
    var item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
    if (item == null) {
        val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        scrollToItem(itemIndex, -viewport / 2)
        withFrameNanos { }
        item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex } ?: return
    }
    val distance = lyricCenterDistance(layoutInfo.viewportStartOffset, layoutInfo.viewportEndOffset, item.offset, item.size)
    if (abs(distance) <= 1f) return
    if (animate) animateScrollBy(distance, LyricScrollSpec) else scrollBy(distance)
}
