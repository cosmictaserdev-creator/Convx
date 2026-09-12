/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.nexamusic.app.ui.screens.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nexamusic.app.R
import com.nexamusic.app.constants.HomeSectionOrderKey
import com.nexamusic.app.constants.HomeSectionHiddenKey
import com.nexamusic.app.ui.component.shapes.ContinuousRoundedRectangle
import com.nexamusic.app.ui.theme.AppleTokens
import com.nexamusic.app.utils.rememberPreference
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * One arrangeable row on Home, as the user sees it.
 *
 * [id] must match the corresponding `HomeSection.id` — that string is the contract
 * between this screen and Home's sort, and is what makes a saved arrangement survive a
 * section being renamed in the UI.
 */
private data class HomeFeedRow(val id: String, val labelRes: Int)

/**
 * The sections a user may arrange, in the order Home shows them by default.
 *
 * Only the fixed sections are listed. The API-driven ones (`similar_recommendation_N`,
 * `home_page_section_N`) are deliberately absent: their count and meaning change with
 * every refresh, so a saved position for "similar recommendation 3" would point at a
 * different thing tomorrow. They keep their default placement and settle after anything
 * arranged here.
 */
private val ArrangeableRows = listOf(
    HomeFeedRow("hero", R.string.home_section_hero),
    HomeFeedRow("keep_listening", R.string.recently_played),
    HomeFeedRow("quick_picks", R.string.quick_picks),
    HomeFeedRow("speed_dial", R.string.speed_dial),
    HomeFeedRow("from_the_community", R.string.home_section_community),
    HomeFeedRow("daily_discover", R.string.home_section_daily_discover),
    HomeFeedRow("account_playlists", R.string.home_section_account_playlists),
    HomeFeedRow("forgotten_favorites", R.string.home_section_forgotten_favorites),
    HomeFeedRow("mood_and_genres", R.string.mood_and_genres),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedOrderScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (savedOrder, onSavedOrderChange) = rememberPreference(HomeSectionOrderKey, "")
    val (hiddenIds, onHiddenIdsChange) = rememberPreference(HomeSectionHiddenKey, "")
    // Read straight off the preference rather than mirrored into local state: a switch
    // writes one value and DataStore is the single source of truth for what is hidden.
    val hidden = remember(hiddenIds) {
        hiddenIds.lineSequence().map(String::trim).filter(String::isNotEmpty).toSet()
    }

    // Seeded from the saved arrangement, with anything it does not mention appended in
    // default order — so a section added by a later release shows up here rather than
    // being invisible and unarrangeable.
    val rows = remember {
        mutableStateListOf<HomeFeedRow>().apply {
            val saved = savedOrder.lineSequence().map(String::trim).filter(String::isNotEmpty).toList()
            addAll(saved.mapNotNull { id -> ArrangeableRows.firstOrNull { it.id == id } })
            addAll(ArrangeableRows.filterNot { row -> any { it.id == row.id } })
        }
    }

    val haptic = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    var hasDragged by remember { mutableStateOf(false) }
    val reorderableState = rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
        rows.add(to.index, rows.removeAt(from.index))
        hasDragged = true
    }

    // Persist on drop rather than on every position change: a drag emits a move per row
    // crossed, and writing DataStore on each one would queue a write per frame.
    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging && hasDragged) {
            onSavedOrderChange(rows.joinToString("\n") { it.id })
            hasDragged = false
        }
    }

    // Explainer and reset sit outside the list on purpose. As LazyColumn items they
    // shifted every row's index by one against `rows`, so `from.index`/`to.index` moved
    // the wrong entry -- a drag appeared to grab one row and reorder another.
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.home_feed_order_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = AppleTokens.Gutter,
                end = AppleTokens.Gutter,
                top = AppleTokens.Gutter,
                bottom = AppleTokens.ItemGap,
            ),
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = AppleTokens.Gutter),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(rows, key = { it.id }) { row ->
                ReorderableItem(state = reorderableState, key = row.id) { dragging ->
                    // Lifts off the surface while held, the way a dragged row is expected
                    // to. animateFloatAsState rather than a raw boolean so the lift and
                    // the drop are both animated.
                    val elevation by animateDpAsState(if (dragging) 8.dp else 0.dp, label = "lift")
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ContinuousRoundedRectangle(AppleTokens.Control),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = elevation,
                        shadowElevation = elevation,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val isHidden = row.id in hidden
                            Text(
                                text = stringResource(row.labelRes),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isHidden) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                            )
                            // A hidden section keeps its place in the arrangement, so
                            // switching it back on returns it where the user left it
                            // instead of at the end of the list.
                            Switch(
                                checked = !isHidden,
                                onCheckedChange = { show ->
                                    val next = if (show) hidden - row.id else hidden + row.id
                                    onHiddenIdsChange(next.joinToString("\n"))
                                },
                            )
                            Icon(
                                painter = painterResource(R.drawable.drag_handle),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                // The handle is the drag target, not a button wrapping it:
                                // an IconButton put its own 48dp ripple between the finger
                                // and the handle, which is what made the grab feel late.
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                ),
                            )
                        }
                    }
                }
            }
        }

        TextButton(
            onClick = {
                // Clearing the preference is what restores the default weights, not
                // writing the default order back: an empty value means "no custom
                // arrangement", which also re-enables the shuffle setting if it is on.
                onSavedOrderChange("")
                rows.clear()
                rows.addAll(ArrangeableRows)
            },
            modifier = Modifier.padding(horizontal = AppleTokens.Gutter - 12.dp),
        ) {
            Text(stringResource(R.string.home_feed_order_reset))
        }
    }
}
