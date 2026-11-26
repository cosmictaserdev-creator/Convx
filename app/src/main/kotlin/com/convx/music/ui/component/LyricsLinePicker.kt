package com.convx.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.BasicAlertDialog
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.Card
import com.convx.music.ui.ios.compat.Text

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
    val picked = remember(lines) {
        mutableStateListOf<Int>().apply { if (initial in lines.indices && lines[initial].isNotBlank()) add(initial) }
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (initial - 2).coerceAtLeast(0))
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            shape = AppTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = AppTheme.colorScheme.surface),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) AppTheme.colorScheme.primary.copy(alpha = 0.16f) else AppTheme.colorScheme.surface)
                                .clickable {
                                    when {
                                        selected -> picked.remove(index)
                                        picked.size < MAX_SHARED_LINES -> picked.add(index)
                                    }
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
                Button(
                    onClick = { onShare(picked.sorted().map { lines[it] }) },
                    enabled = picked.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.share_lyrics)) }
            }
        }
    }
}

/** Same limit as the classic list's selection mode, so a card never overflows. */
private const val MAX_SHARED_LINES = 5
