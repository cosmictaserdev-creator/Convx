/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.constants.CanvasBackdropMode
import com.convx.music.constants.CanvasBackdropModeKey
import com.convx.music.constants.CanvasMode
import com.convx.music.constants.CanvasModeKey
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.RadioButton
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.component.IosCheckmark
import com.convx.music.ui.ios.component.IosText
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.utils.dataStore
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasSelection(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (legacyEnabled) = rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    val (mode) = rememberEnumPreference(CanvasModeKey, if (legacyEnabled) CanvasMode.AUTO else CanvasMode.OFF)
    val (backdropMode, setBackdropMode) = rememberEnumPreference(
        CanvasBackdropModeKey,
        defaultValue = CanvasBackdropMode.EXTENDED_VIDEO,
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val canvasEnabled = mode != CanvasMode.OFF
    val (canvasSource, onCanvasSourceChange) = rememberEnumPreference(
        CanvasSourceKey,
        defaultValue = CanvasSource.AUTO
    )

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.convx_canvas),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 24.dp,
    ) {
        Text(
            text = stringResource(R.string.convx_canvas_desc),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
        )

        IosSettingsGroup(
            title = stringResource(R.string.player_canvas_mode),
            items = CanvasMode.entries.map { option ->
                val title = when (option) {
                    CanvasMode.AUTO -> R.string.player_canvas_mode_auto
                    CanvasMode.ASK -> R.string.player_canvas_mode_ask
                    CanvasMode.OFF -> R.string.player_canvas_mode_off
                }
                canvasSettingOption(title, selected = mode == option) {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[CanvasModeKey] = option.name
                            preferences[CanvasThumbnailAnimationKey] = option != CanvasMode.OFF
                        }
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        IosSettingsGroup(
            title = stringResource(R.string.player_canvas_backdrop),
            items = CanvasBackdropMode.entries.map { option ->
                val title = when (option) {
                    CanvasBackdropMode.EXTENDED_VIDEO -> R.string.player_canvas_backdrop_extended
                    CanvasBackdropMode.COLOUR_ONLY -> R.string.player_canvas_backdrop_colour
                }
                canvasSettingOption(title, selected = backdropMode == option) { setBackdropMode(option) }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        IosSettingsGroup(
            title = stringResource(R.string.canvas_source),
            items = listOf(
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.AUTO,
                            onClick = null,
                            enabled = canvasEnabled
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_auto)) },
                    description = { Text(stringResource(R.string.canvas_source_auto_desc)) },
                    enabled = canvasEnabled,
                    onClick = { onCanvasSourceChange(CanvasSource.AUTO) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.APPLE_MUSIC,
                            onClick = null,
                            enabled = canvasEnabled
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_apple_music)) },
                    description = { Text(stringResource(R.string.canvas_source_apple_music_desc)) },
                    enabled = canvasEnabled,
                    onClick = { onCanvasSourceChange(CanvasSource.APPLE_MUSIC) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.CONVX,
                            onClick = null,
                            enabled = canvasEnabled
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_convx)) },
                    description = { Text(stringResource(R.string.canvas_source_convx_desc)) },
                    enabled = canvasEnabled,
                    onClick = { onCanvasSourceChange(CanvasSource.CONVX) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.TIDAL,
                            onClick = null,
                            enabled = canvasEnabled
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_tidal)) },
                    description = { Text(stringResource(R.string.canvas_source_tidal_desc)) },
                    enabled = canvasEnabled,
                    onClick = { onCanvasSourceChange(CanvasSource.TIDAL) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.SPOTIFY,
                            onClick = null,
                            enabled = canvasEnabled
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_spotify)) },
                    description = { Text(stringResource(R.string.canvas_source_spotify_desc)) },
                    enabled = canvasEnabled,
                    onClick = { onCanvasSourceChange(CanvasSource.SPOTIFY) }
                )
            )
        )
        Spacer(modifier = Modifier.height(36.dp))
    }
}

private fun canvasSettingOption(title: Int, selected: Boolean, onClick: () -> Unit) = IosSettingsItem(
    title = {
        IosText(
            text = stringResource(title),
            modifier = Modifier.semantics { this.selected = selected },
        )
    },
    trailingContent = {
        Box(Modifier.width(24.dp), contentAlignment = Alignment.Center) {
            if (selected) IosCheckmark(color = IosTheme.tint)
        }
    },
    onClick = onClick,
)
