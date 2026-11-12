package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.canvas.CanvasProvider
import com.convx.music.canvas.CanvasProviderPreferences
import com.convx.music.constants.CanvasDisabledProvidersKey
import com.convx.music.constants.CanvasFirstAvailableKey
import com.convx.music.constants.CanvasPriorityWaitSecondsKey
import com.convx.music.constants.CanvasProviderOrderKey
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.ModernSwitch
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberPreference
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@Composable
internal fun CanvasProviderControls(enabled: Boolean, automatic: Boolean) {
    val (order, setOrder) = rememberPreference(CanvasProviderOrderKey, "")
    val (disabled, setDisabled) = rememberPreference(CanvasDisabledProvidersKey, "")
    val (firstAvailable, setFirstAvailable) = rememberPreference(CanvasFirstAvailableKey, false)
    val (waitSeconds, setWaitSeconds) = rememberPreference(CanvasPriorityWaitSecondsKey, 2)
    val preferences = CanvasProviderPreferences.fromStored(order, disabled, firstAvailable, waitSeconds)
    val rows = remember { mutableStateListOf<CanvasProvider>().apply { addAll(preferences.order) } }
    val listState = rememberLazyListState()
    var changed by remember { mutableStateOf(false) }
    val reorderable = rememberReorderableLazyListState(lazyListState = listState) { from, to ->
        rows.add(to.index, rows.removeAt(from.index))
        changed = true
    }
    LaunchedEffect(order, reorderable.isAnyItemDragging) {
        if (!reorderable.isAnyItemDragging) {
            if (changed) {
                setOrder(rows.joinToString(",") { it.name })
                changed = false
            } else if (rows.toList() != preferences.order) {
                rows.clear()
                rows.addAll(preferences.order)
            }
        }
    }
    val haptic = LocalHapticFeedback.current
    Text(stringResource(R.string.canvas_provider_priority), style = AppTheme.typography.titleMedium)
    Text(
        stringResource(R.string.canvas_provider_priority_desc),
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
    )
    LazyColumn(
        state = listState,
        userScrollEnabled = false,
        modifier = Modifier.fillMaxWidth().height((CanvasProvider.entries.size * 64).dp),
    ) {
        items(rows, key = { it.name }) { provider ->
            ReorderableItem(reorderable, key = provider.name) {
                val label = stringResource(provider.labelRes())
                Row(
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(label, modifier = Modifier.weight(1f), style = AppTheme.typography.bodyLarge)
                    ModernSwitch(
                        checked = provider !in preferences.disabled,
                        onCheckedChange = { checked ->
                            val next = if (checked) preferences.disabled - provider else preferences.disabled + provider
                            setDisabled(next.joinToString(",") { it.name })
                        },
                        enabled = enabled,
                    )
                    Icon(
                        painter = painterResource(R.drawable.drag_handle),
                        contentDescription = stringResource(R.string.canvas_reorder_provider, label),
                        tint = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.draggableHandle(
                            enabled = enabled && automatic,
                            onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                        ),
                    )
                }
            }
        }
    }
    IosSettingsGroup(
        items = listOf(
            IosSettingsItem(
                title = { Text(stringResource(R.string.canvas_first_available)) },
                description = { Text(stringResource(R.string.canvas_first_available_desc)) },
                enabled = enabled && automatic,
                trailingContent = { ModernSwitch(checked = firstAvailable, onCheckedChange = setFirstAvailable, enabled = enabled && automatic) },
                onClick = { setFirstAvailable(!firstAvailable) },
            ),
        ),
    )
    if (!firstAvailable) {
        var value by remember(waitSeconds) { mutableFloatStateOf(waitSeconds.coerceIn(0, 10).toFloat()) }
        Text(stringResource(R.string.canvas_priority_wait, value.roundToInt()), style = AppTheme.typography.bodyMedium)
        Slider(
            value = value,
            onValueChange = { value = it },
            onValueChangeFinished = { setWaitSeconds(value.roundToInt()) },
            valueRange = 0f..10f,
            steps = 9,
            enabled = enabled && automatic,
        )
    }
}

private fun CanvasProvider.labelRes(): Int = when (this) {
    CanvasProvider.SPOTIFY -> R.string.canvas_provider_spotify
    CanvasProvider.APPLE_MUSIC -> R.string.canvas_provider_apple
    CanvasProvider.TIDAL -> R.string.canvas_provider_tidal
    CanvasProvider.CONVX -> R.string.canvas_provider_convx
}
