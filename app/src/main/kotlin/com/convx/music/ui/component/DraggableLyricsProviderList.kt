/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convx.music.R
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class DraggableLyricsProviderItem(
    val id: String,
    val name: String,
    val icon: Painter,
    val enabled: Boolean = true,
)

@Composable
fun DraggableLyricsProviderList(
    items: MutableList<DraggableLyricsProviderItem>,
    onItemsReordered: (List<DraggableLyricsProviderItem>) -> Unit,
    onToggleEnabled: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    var hasDragged by remember { mutableStateOf(false) }
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
    ) { from, to ->
        val fromIndex = items.indexOfFirst { it.id == from.key }
        val toIndex = items.indexOfFirst { it.id == to.key }
        if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
            val movedItem = items.removeAt(fromIndex)
            items.add(toIndex, movedItem)
            hasDragged = true
        }
    }

    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging && hasDragged) {
            onItemsReordered(items.toList())
            hasDragged = false
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
    ) {
        itemsIndexed(
            items,
            key = { _, item -> item.id },
        ) { _, item ->
            ReorderableItem(
                state = reorderableState,
                key = item.id,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = AbsoluteSmoothCornerShape(12.dp, 60),
                    color = AppTheme.colorScheme.surfaceContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.draggableHandle(),
                        ) {
                            SymbolIcon(symbol = sf("line.3.horizontal"), contentDescription = "Reorder ${item.name}", tint = AppTheme.colorScheme.onSurfaceVariant)
                        }

                        Text(
                            text = item.name,
                            style = AppTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )

                        Switch(
                            checked = item.enabled,
                            onCheckedChange = { onToggleEnabled(item.id) },
                        )
                    }
                }
            }
        }
    }
}
