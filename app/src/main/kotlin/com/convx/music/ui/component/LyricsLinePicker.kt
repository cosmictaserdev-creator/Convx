package com.convx.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.BasicAlertDialog
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.component.IosButton
import com.convx.music.ui.ios.component.IosButtonStyle

/**
 * Picks up to [MAX_SHARED_LINES] lines to share, starting on [initial]. Independent of the lyrics
 * style, so sharing works in Flamingo and Fisheye as well as the classic list.
 */
@Composable
fun LyricsLinePicker(
    lines: List<String>,
    initial: Int,
    onDismiss: () -> Unit,
    onShare: (List<String>) -> Unit,
) {
    val picked = remember(lines, initial) {
        mutableStateListOf<Int>().apply { if (initial in lines.indices && lines[initial].isNotBlank()) add(initial) }
    }
    val startIndex = (initial.coerceIn(0, lines.lastIndex.coerceAtLeast(0)) - 2).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(ContinuousRoundedRectangle(20.dp))
                .background(AppTheme.colorScheme.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.share_lyrics),
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colorScheme.onSurface,
            )
            LazyColumn(state = listState, modifier = Modifier.heightIn(max = 360.dp)) {
                itemsIndexed(lines) { index, line ->
                    if (line.isBlank()) return@itemsIndexed
                    val selected = index in picked
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(ContinuousRoundedRectangle(10.dp))
                            .background(if (selected) AppTheme.colorScheme.primary.copy(alpha = 0.16f) else AppTheme.colorScheme.surface)
                            .toggleable(
                                value = selected,
                                enabled = selected || picked.size < MAX_SHARED_LINES,
                                role = Role.Checkbox,
                            ) { checked ->
                                if (!checked) picked.remove(index)
                                else if (index !in picked && picked.size < MAX_SHARED_LINES) picked.add(index)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = line,
                            color = if (selected) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }
            IosButton(
                title = stringResource(R.string.share_lyrics),
                style = IosButtonStyle.BorderedProminent,
                onClick = { onShare(picked.sorted().map { lines[it] }) },
                enabled = picked.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Same limit as the classic list's selection mode, so a card never overflows. */
private const val MAX_SHARED_LINES = 5
