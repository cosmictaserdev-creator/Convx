/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import com.convxy.music.LocalPlayerAwareWindowInsets
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Digits bucket first, then A-Z, then everything non-Latin under a single tail bucket. */
val AlphabetSections: List<String> = listOf("0") + ('A'..'Z').map(Char::toString) + listOf("#")

/**
 * Below this the rail is clutter: a list you can reach the end of in two flings does not
 * need a scrubber, and the rail costs real estate on every row it overlaps.
 */
const val AlphabetScrollBarMinItems = 20

/** Wide enough to be a comfortable thumb target, narrow enough not to cover list text. */
private val RailWidth = 28.dp

/** Rail slot that jumps to index 0 rather than to a section. Drawn above "0". */
private const val ScrollTopSection = "↑"

/**
 * Fast-scrub rail pinned to the trailing edge of a long list: drag a finger down it and
 * the list jumps section by section, with a bubble showing the section under the finger.
 *
 * Two details make it feel like a native fast-scroller rather than a row of small
 * buttons:
 *
 * - The rail divides whatever height it is given by the number of sections, so the first
 *   and last targets stay pinned to the ends and only the spacing between them changes.
 *   Letters shrink to 4sp on a short window rather than the rail scrolling or clipping.
 * - Selection updates on drag position, not on per-letter hit testing, so a fast scrub
 *   never drops a section between two sampled pointer events.
 *
 * [sectionIndexMap] maps a section key to the list index where it starts. Sections with
 * no items are not skipped in the rail — they resolve to the next populated section
 * downward (see [findAlphabetTargetIndex]), which keeps the letters at fixed positions
 * so muscle memory survives a re-sort.
 */
@Composable
fun AlphabetScrollBar(
    sectionIndexMap: Map<String, Int>,
    itemCount: Int,
    isAtTarget: (Int) -> Boolean,
    scrollToItem: suspend (Int) -> Unit,
    modifier: Modifier = Modifier,
    sections: List<String> = AlphabetSections,
    showScrollTop: Boolean = true,
    labelOf: (String) -> String = { it },
    showIndicator: Boolean = true,
) {
    val view = LocalView.current
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val scope = rememberCoroutineScope()

    // The rail draws one extra slot above the letters. It is part of the same scrub
    // surface -- dragging up past "0" reaches the top of the list -- so it has to share
    // the cell geometry rather than sit outside the Column as a separate button.
    val railItems = remember(sections, showScrollTop) {
        if (showScrollTop) listOf(ScrollTopSection) + sections else sections
    }

    var scrollJob by remember { mutableStateOf<Job?>(null) }
    var selectedSection by remember { mutableStateOf<String?>(null) }
    var indicatorSection by remember { mutableStateOf<String?>(null) }
    var indicatorVisible by remember { mutableStateOf(false) }
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }

    val currentItemCount by rememberUpdatedState(itemCount)
    val currentSectionIndexMap by rememberUpdatedState(sectionIndexMap)
    val currentSections by rememberUpdatedState(sections)
    val currentRailItems by rememberUpdatedState(railItems)
    val currentIsAtTarget by rememberUpdatedState(isAtTarget)
    val currentScrollToItem by rememberUpdatedState(scrollToItem)

    fun updateSelection(index: Int) {
        if (index !in currentRailItems.indices || index == lastSelectedIndex) return
        lastSelectedIndex = index
        val section = currentRailItems[index]
        selectedSection = section
        indicatorSection = section
        indicatorVisible = true

        val maxIndex = currentItemCount - 1
        if (maxIndex < 0) return
        val target = if (section == ScrollTopSection) {
            0
        } else {
            findAlphabetTargetIndex(
                section = section,
                sectionIndexMap = currentSectionIndexMap,
                sections = currentSections,
            ).coerceIn(0, maxIndex)
        }

        if (!currentIsAtTarget(target)) {
            scrollJob?.cancel()
            scrollJob = scope.launch {
                try {
                    val latestMax = currentItemCount - 1
                    if (latestMax >= 0) currentScrollToItem(target.coerceAtMost(latestMax))
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: IndexOutOfBoundsException) {
                    // A re-sort can replace the lazy layout between validation and scrolling.
                } catch (_: IllegalArgumentException) {
                    // Lazy layouts reject an index from a concurrently replaced item provider.
                }
            }
        }
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun clearSelection() {
        selectedSection = null
        indicatorVisible = false
        lastSelectedIndex = -1
    }

    BoxWithConstraints(modifier = modifier) {
        if (railItems.isEmpty() || maxHeight <= 0.dp) return@BoxWithConstraints

        // Captured into a local: BoxWithConstraintsScope and RowScope both carry
        // @LayoutScopeMarker, so inside the Row below the outer scope is shadowed and
        // maxHeight stops resolving.
        val railHeight = maxHeight
        val cellSize = railHeight / railItems.size.toFloat()
        val cellHeightPx = with(LocalDensity.current) { cellSize.toPx() }
        val trackAlpha by animateFloatAsState(
            targetValue = if (indicatorVisible) 0.9f else 0f,
            label = "railTrackAlpha",
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(railHeight),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlphabetIndicator(
                section = indicatorSection?.let(labelOf),
                visible = indicatorVisible && showIndicator,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .width(RailWidth)
                    .height(railHeight)
                    // A track, not bare floating letters: it reads as a control, and it
                    // only appears while a scrub is in progress so a resting list stays
                    // clean.
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = trackAlpha,
                        ),
                        shape = ContinuousRoundedRectangle(RailWidth / 2),
                    )
                    // The rail is a scrub surface, not 28 buttons. Announcing every
                    // letter would bury the list itself in the accessibility tree, and
                    // a scrub gesture is not reachable that way regardless.
                    .clearAndSetSemantics {}
                    .pointerInput(railItems, cellHeightPx, sectionIndexMap) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            down.consume()
                            var dragged = false
                            updateSelection((down.position.y / cellHeightPx).toInt())
                            try {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == down.id }
                                        ?: break
                                    if (!change.pressed) break
                                    change.consume()
                                    if (!dragged &&
                                        (change.position - down.position).getDistance() >= touchSlop
                                    ) {
                                        dragged = true
                                    }
                                    if (dragged) {
                                        updateSelection(
                                            (change.position.y / cellHeightPx)
                                                .toInt()
                                                .coerceIn(0, railItems.lastIndex),
                                        )
                                    }
                                }
                            } finally {
                                clearSelection()
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                railItems.forEach { section ->
                    AlphabetCell(
                        section = labelOf(section),
                        selected = selectedSection == section,
                        size = cellSize,
                    )
                }
            }
        }
    }
}

/** Number of scrub stops the proportional rail offers. Matches the alphabet rail's height. */
private const val ProportionalRailStops = 27

/** Every stop draws the same tick; the rail is a position, not a label. */
private const val ProportionalRailTick = "·"

/**
 * The one call site every long list uses.
 *
 * Pass [sectionIndexMap] when the list is sorted alphabetically and the rail shows
 * letters. Pass null for any other sort — the rail stays, but becomes a proportional
 * fast-scroll thumb instead of vanishing, which is the whole point: a scrubber that
 * disappears when you change the sort order is a scrubber nobody can rely on.
 *
 * The proportional mode is the same rail with synthetic sections spaced evenly through
 * the list, so both modes share one gesture, one geometry and one haptic.
 */
@Composable
fun ListScrollRail(
    sectionIndexMap: Map<String, Int>?,
    itemCount: Int,
    isAtTarget: (Int) -> Boolean,
    scrollToItem: suspend (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (itemCount < AlphabetScrollBarMinItems) return

    if (sectionIndexMap != null) {
        // A descending sort runs Z->A down the list, so the rail has to run Z->A down the
        // screen too or the thumb travels opposite to the content. Detected from the map
        // rather than passed in, so no call site can get the two out of step.
        val sections = remember(sectionIndexMap) {
            if (sectionIndexIsDescending(sectionIndexMap)) {
                AlphabetSections.asReversed()
            } else {
                AlphabetSections
            }
        }
        AlphabetScrollBar(
            sectionIndexMap = sectionIndexMap,
            itemCount = itemCount,
            isAtTarget = isAtTarget,
            scrollToItem = scrollToItem,
            modifier = modifier,
            sections = sections,
        )
        return
    }

    val stops = remember { List(ProportionalRailStops) { it.toString() } }
    val proportionalIndex = remember(itemCount) {
        buildProportionalSectionIndex(itemCount, ProportionalRailStops)
    }
    AlphabetScrollBar(
        sectionIndexMap = proportionalIndex,
        itemCount = itemCount,
        isAtTarget = isAtTarget,
        scrollToItem = scrollToItem,
        modifier = modifier,
        sections = stops,
        labelOf = { section -> if (section == ScrollTopSection) section else ProportionalRailTick },
        showIndicator = false,
    )
}

/**
 * The rail, positioned and offset for a [LazyListState] whose scrubbable items are the
 * last ones in the layout.
 *
 * Screens put headers, chips and sort buttons above their list, so rail index 0 is not
 * lazy index 0. Whatever the headers add up to on this pass is exactly the difference
 * between the layout's item count and [itemCount]. It is read inside the lambdas rather
 * than in composition on purpose: `layoutInfo` is snapshot state, and reading it during
 * composition would recompose the whole screen on every scrolled pixel.
 */
@Composable
fun BoxScope.ListScrollRail(
    lazyListState: LazyListState,
    itemCount: Int,
    sectionIndexMap: Map<String, Int>?,
    modifier: Modifier = Modifier,
) {
    val headerOffset = {
        (lazyListState.layoutInfo.totalItemsCount - itemCount).coerceAtLeast(0)
    }
    ListScrollRail(
        sectionIndexMap = sectionIndexMap,
        itemCount = itemCount,
        isAtTarget = { lazyListState.firstVisibleItemIndex == it + headerOffset() },
        // Not animateScrollToItem: a scrub samples many sections per second and each
        // animation would be cancelled by the next, so the list would crawl behind the
        // thumb instead of tracking it.
        scrollToItem = { lazyListState.scrollToItem(it + headerOffset()) },
        modifier = modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
    )
}

/**
 * As above, for the library screens that switch between a list and a grid of the same
 * items. Keeps the rail on screen across the view-type toggle instead of making every
 * such screen branch at its own call site.
 */
@Composable
fun BoxScope.ListScrollRail(
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    isGrid: Boolean,
    itemCount: Int,
    sectionIndexMap: Map<String, Int>?,
    modifier: Modifier = Modifier,
) {
    if (isGrid) {
        ListScrollRail(lazyGridState, itemCount, sectionIndexMap, modifier)
    } else {
        ListScrollRail(lazyListState, itemCount, sectionIndexMap, modifier)
    }
}

/** As above, for grid-backed screens (albums, artists, playlists). */
@Composable
fun BoxScope.ListScrollRail(
    lazyGridState: LazyGridState,
    itemCount: Int,
    sectionIndexMap: Map<String, Int>?,
    modifier: Modifier = Modifier,
) {
    val headerOffset = {
        (lazyGridState.layoutInfo.totalItemsCount - itemCount).coerceAtLeast(0)
    }
    ListScrollRail(
        sectionIndexMap = sectionIndexMap,
        itemCount = itemCount,
        isAtTarget = { lazyGridState.firstVisibleItemIndex == it + headerOffset() },
        scrollToItem = { lazyGridState.scrollToItem(it + headerOffset()) },
        modifier = modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
    )
}

/**
 * True when the sections appear in reverse alphabetical order down the list.
 *
 * Compares the first and last populated sections in alphabet order: if "A" starts later
 * in the list than "Z" does, the list is sorted descending.
 */
internal fun sectionIndexIsDescending(sectionIndexMap: Map<String, Int>): Boolean {
    val populated = AlphabetSections.mapNotNull { section ->
        sectionIndexMap[section]
    }
    if (populated.size < 2) return false
    return populated.first() > populated.last()
}

/** Spread [stops] evenly over [itemCount], keyed the way the rail keys its sections. */
internal fun buildProportionalSectionIndex(itemCount: Int, stops: Int): Map<String, Int> {
    if (itemCount <= 0 || stops <= 0) return emptyMap()
    val lastItem = itemCount - 1
    return (0 until stops).associate { stop ->
        val fraction = if (stops == 1) 0f else stop.toFloat() / (stops - 1)
        stop.toString() to (fraction * lastItem).toInt().coerceIn(0, lastItem)
    }
}

@Composable
private fun AlphabetIndicator(section: String?, visible: Boolean) {
    AnimatedVisibility(
        visible = visible && section != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.92f),
                    shape = ContinuousRoundedRectangle(32.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = section.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun AlphabetCell(section: String, selected: Boolean, size: Dp) {
    // The rail always spans the full available height, so on a short window the cells
    // get small rather than the rail getting shorter. Type steps down to match.
    val fontSize = when {
        size < 8.dp -> 6.sp
        size < 12.dp -> 8.sp
        size < 16.dp -> 10.sp
        else -> 12.sp
    }
    Box(
        // Height from the rail's own division, width from the rail: a tall window would
        // otherwise make each cell wider than the track it sits in.
        modifier = Modifier
            .height(size)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = section,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = fontSize),
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

/**
 * Resolve a rail section to a list index.
 *
 * An empty section falls forward to the next populated one, so tapping "Q" in a library
 * with no Q artists lands on R rather than doing nothing. Past the end it falls back to
 * the last populated section instead of index 0, which is what makes a scrub to the
 * bottom of the rail reach the bottom of the list.
 */
fun findAlphabetTargetIndex(
    section: String,
    sectionIndexMap: Map<String, Int>,
    sections: List<String> = AlphabetSections,
): Int {
    if (sectionIndexMap.isEmpty()) return 0
    sectionIndexMap[section]?.let { return it }

    val requestedIndex = sections.indexOf(section)
    if (requestedIndex < 0) return 0

    sections
        .drop(requestedIndex + 1)
        .firstNotNullOfOrNull(sectionIndexMap::get)
        ?.let { return it }

    return sections.asReversed().firstNotNullOfOrNull(sectionIndexMap::get) ?: 0
}

/** Bucket a display title into one of [AlphabetSections]. */
fun alphabetSectionKey(title: String): String {
    val first = title.trimStart().firstOrNull() ?: return "#"
    return when {
        first.isDigit() -> "0"
        first in 'A'..'Z' -> first.toString()
        first in 'a'..'z' -> first.uppercaseChar().toString()
        else -> "#"
    }
}

/** Build the section -> first-index map a rail needs from an already-sorted list. */
fun <T> buildAlphabetSectionIndex(items: List<T>, titleOf: (T) -> String): Map<String, Int> {
    val map = LinkedHashMap<String, Int>()
    items.forEachIndexed { index, item ->
        map.putIfAbsent(alphabetSectionKey(titleOf(item)), index)
    }
    return map
}
