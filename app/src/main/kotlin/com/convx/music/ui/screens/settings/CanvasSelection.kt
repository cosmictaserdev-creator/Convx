/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.animateColorAsState
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.RadioButton
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.constants.CanvasMiniPlayerEnabledKey
import com.convx.music.ui.component.ExpressiveIconButton
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.ModernSwitch
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasSelection(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (canvasThumbnailAnimation, onCanvasThumbnailAnimationChange) = rememberPreference(
        CanvasThumbnailAnimationKey,
        defaultValue = true
    )
    val (canvasSource, onCanvasSourceChange) = rememberEnumPreference(
        CanvasSourceKey,
        defaultValue = CanvasSource.AUTO
    )
    val (miniCanvas, onMiniCanvasChange) = rememberPreference(CanvasMiniPlayerEnabledKey, true)
    val (blurQuality, setBlurQuality) = rememberEnumPreference(
        com.convx.music.constants.CanvasBlurQualityKey, com.convx.music.canvas.CanvasBlurQuality.HIGH)

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

        // Large capsule banner for main toggle
        val containerColor by animateColorAsState(
            targetValue = if (canvasThumbnailAnimation) {
                AppTheme.colorScheme.primaryContainer
            } else {
                AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            label = "containerColor"
        )

        val contentColor = if (canvasThumbnailAnimation) {
            AppTheme.colorScheme.onPrimaryContainer
        } else {
            AppTheme.colorScheme.onSurfaceVariant
        }

        Card(
            onClick = { onCanvasThumbnailAnimationChange(!canvasThumbnailAnimation) },
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.use_canvas),
                    style = AppTheme.typography.titleMedium,
                    color = contentColor
                )
                ModernSwitch(
                    checked = canvasThumbnailAnimation,
                    onCheckedChange = onCanvasThumbnailAnimationChange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IosSettingsGroup(items = listOf(
            IosSettingsItem(
                title = { Text(stringResource(R.string.canvas_mini_player)) },
                description = { Text(stringResource(R.string.canvas_mini_player_desc)) },
                enabled = canvasThumbnailAnimation,
                trailingContent = { ModernSwitch(checked = miniCanvas, onCheckedChange = onMiniCanvasChange, enabled = canvasThumbnailAnimation) },
                onClick = { onMiniCanvasChange(!miniCanvas) },
            ),
        ))

        Spacer(modifier = Modifier.height(24.dp))

        IosSettingsGroup(
            title = stringResource(R.string.canvas_source),
            items = listOf(
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.AUTO,
                            onClick = null,
                            enabled = canvasThumbnailAnimation
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_auto)) },
                    description = { Text(stringResource(R.string.canvas_source_auto_desc)) },
                    enabled = canvasThumbnailAnimation,
                    onClick = { onCanvasSourceChange(CanvasSource.AUTO) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.APPLE_MUSIC,
                            onClick = null,
                            enabled = canvasThumbnailAnimation
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_apple_music)) },
                    description = { Text(stringResource(R.string.canvas_source_apple_music_desc)) },
                    enabled = canvasThumbnailAnimation,
                    onClick = { onCanvasSourceChange(CanvasSource.APPLE_MUSIC) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.CONVX,
                            onClick = null,
                            enabled = canvasThumbnailAnimation
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_convx)) },
                    description = { Text(stringResource(R.string.canvas_source_convx_desc)) },
                    enabled = canvasThumbnailAnimation,
                    onClick = { onCanvasSourceChange(CanvasSource.CONVX) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.TIDAL,
                            onClick = null,
                            enabled = canvasThumbnailAnimation
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_tidal)) },
                    description = { Text(stringResource(R.string.canvas_source_tidal_desc)) },
                    enabled = canvasThumbnailAnimation,
                    onClick = { onCanvasSourceChange(CanvasSource.TIDAL) }
                ),
                IosSettingsItem(
                    leadingContent = {
                        RadioButton(
                            selected = canvasSource == CanvasSource.SPOTIFY,
                            onClick = null,
                            enabled = canvasThumbnailAnimation
                        )
                    },
                    title = { Text(stringResource(R.string.canvas_source_spotify)) },
                    description = { Text(stringResource(R.string.canvas_source_spotify_desc)) },
                    enabled = canvasThumbnailAnimation,
                    onClick = { onCanvasSourceChange(CanvasSource.SPOTIFY) }
                )
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        CanvasProviderControls(enabled = canvasThumbnailAnimation, automatic = canvasSource == CanvasSource.AUTO)
        IosSettingsGroup(
            title = stringResource(R.string.canvas_blur_quality),
            items = com.convx.music.canvas.CanvasBlurQuality.entries.map { quality ->
                IosSettingsItem(
                    leadingContent = { RadioButton(selected = blurQuality == quality, onClick = null) },
                    title = { Text(stringResource(when (quality) {
                        com.convx.music.canvas.CanvasBlurQuality.HIGH -> R.string.canvas_blur_high
                        com.convx.music.canvas.CanvasBlurQuality.BALANCED -> R.string.canvas_blur_balanced
                        com.convx.music.canvas.CanvasBlurQuality.LOW -> R.string.canvas_blur_low
                    })) },
                    onClick = { setBlurQuality(quality) },
                )
            },
        )
        Text(
            text = stringResource(R.string.canvas_blur_quality_desc),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
        Spacer(modifier = Modifier.height(36.dp))
    
    }
}
