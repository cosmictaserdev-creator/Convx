/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
// Routed to the glass dispatcher: every Slider( in this file is a settings value
// slider, and this is the single place that decides between the liquid glass rail
// and Material's. Call sites are unchanged.
import com.convxy.music.ui.component.GlassSlider as Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.AmbientAutoHideBackButtonEnabledKey
import com.convxy.music.constants.AmbientCanvasAnchorSide
import com.convxy.music.constants.AmbientCanvasAnchorSideKey
import com.convxy.music.constants.AmbientCanvasEdgeFeatherKey
import com.convxy.music.constants.AmbientCanvasFarVeilKey
import com.convxy.music.constants.AmbientCanvasFitMode
import com.convxy.music.constants.AmbientCanvasFitModeKey
import com.convxy.music.constants.AmbientCanvasGradientSpreadKey
import com.convxy.music.constants.AmbientCanvasSideFitEnabledKey
import com.convxy.music.constants.AmbientCanvasSideGradientKey
import com.convxy.music.constants.AmbientCanvasSideWidthKey
import com.convxy.music.constants.AmbientCanvasSourceKey
import com.convxy.music.ui.screens.ambient.AmbientCanvasFitDefaults
import com.convxy.music.ui.screens.ambient.AmbientCanvasFitPreview
import com.convxy.music.constants.AmbientLyricsTextSizeKey
import com.convxy.music.constants.AmbientProgressRingEnabledKey
import com.convxy.music.constants.AmbientPlaybackFeedbackEnabledKey
import com.convxy.music.constants.AmbientSeekHapticsEnabledKey
import com.convxy.music.constants.AmbientSeekTimeEnabledKey
import com.convxy.music.constants.AmbientSwipeNavigationEnabledKey
import com.convxy.music.constants.AmbientTapToPlayPauseEnabledKey
import com.convxy.music.constants.AmbientTrackInfoEnabledKey
import com.convxy.music.constants.AmbientTrackTransitionsEnabledKey
import com.convxy.music.constants.AmbientVideoCanvasBlurKey
import com.convxy.music.constants.AmbientVideoCanvasDimKey
import com.convxy.music.constants.AmbientVideoCanvasEnabledKey
import com.convxy.music.constants.CanvasSource
import com.convxy.music.constants.LyricsTextSizeKey
import com.convxy.music.ui.component.GlassSwitchCompat as Switch
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.Material3SettingsGroup
import com.convxy.music.ui.component.Material3SettingsItem
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientModeSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (videoCanvasEnabled, onVideoCanvasEnabledChange) = rememberPreference(
        AmbientVideoCanvasEnabledKey,
        defaultValue = false,
    )
    val (canvasSource, onCanvasSourceChange) = rememberEnumPreference(
        AmbientCanvasSourceKey,
        defaultValue = CanvasSource.AUTO,
    )
    val (canvasBlur, onCanvasBlurChange) = rememberPreference(
        AmbientVideoCanvasBlurKey,
        defaultValue = 12f,
    )
    val (canvasDim, onCanvasDimChange) = rememberPreference(
        AmbientVideoCanvasDimKey,
        defaultValue = 0.42f,
    )
    val (canvasSideFitEnabled, onCanvasSideFitEnabledChange) = rememberPreference(
        AmbientCanvasSideFitEnabledKey,
        defaultValue = false,
    )
    val (canvasAnchorSide, onCanvasAnchorSideChange) = rememberEnumPreference(
        AmbientCanvasAnchorSideKey,
        defaultValue = AmbientCanvasAnchorSide.AUTO,
    )
    val (canvasFitMode, onCanvasFitModeChange) = rememberEnumPreference(
        AmbientCanvasFitModeKey,
        defaultValue = AmbientCanvasFitMode.FIT,
    )
    val (canvasSideWidth, onCanvasSideWidthChange) = rememberPreference(
        AmbientCanvasSideWidthKey,
        defaultValue = AmbientCanvasFitDefaults.SideWidth,
    )
    val (canvasSideGradient, onCanvasSideGradientChange) = rememberPreference(
        AmbientCanvasSideGradientKey,
        defaultValue = AmbientCanvasFitDefaults.SideGradient,
    )
    val (canvasGradientSpread, onCanvasGradientSpreadChange) = rememberPreference(
        AmbientCanvasGradientSpreadKey,
        defaultValue = AmbientCanvasFitDefaults.GradientSpread,
    )
    val (canvasFarVeil, onCanvasFarVeilChange) = rememberPreference(
        AmbientCanvasFarVeilKey,
        defaultValue = AmbientCanvasFitDefaults.FarVeil,
    )
    val (canvasEdgeFeather, onCanvasEdgeFeatherChange) = rememberPreference(
        AmbientCanvasEdgeFeatherKey,
        defaultValue = AmbientCanvasFitDefaults.EdgeFeather,
    )
    val (globalLyricsTextSize) = rememberPreference(
        LyricsTextSizeKey,
        defaultValue = 30f,
    )
    val (lyricsTextSize, onLyricsTextSizeChange) = rememberPreference(
        AmbientLyricsTextSizeKey,
        defaultValue = globalLyricsTextSize,
    )
    val (progressRingEnabled, onProgressRingEnabledChange) = rememberPreference(
        AmbientProgressRingEnabledKey,
        defaultValue = true,
    )
    val (playbackFeedbackEnabled, onPlaybackFeedbackEnabledChange) = rememberPreference(
        AmbientPlaybackFeedbackEnabledKey,
        defaultValue = true,
    )
    val (seekTimeEnabled, onSeekTimeEnabledChange) = rememberPreference(
        AmbientSeekTimeEnabledKey,
        defaultValue = true,
    )
    val (seekHapticsEnabled, onSeekHapticsEnabledChange) = rememberPreference(
        AmbientSeekHapticsEnabledKey,
        defaultValue = true,
    )
    val (trackInfoEnabled, onTrackInfoEnabledChange) = rememberPreference(
        AmbientTrackInfoEnabledKey,
        defaultValue = true,
    )
    val (tapToPlayPauseEnabled, onTapToPlayPauseEnabledChange) = rememberPreference(
        AmbientTapToPlayPauseEnabledKey,
        defaultValue = true,
    )
    val (swipeNavigationEnabled, onSwipeNavigationEnabledChange) = rememberPreference(
        AmbientSwipeNavigationEnabledKey,
        defaultValue = true,
    )
    val (trackTransitionsEnabled, onTrackTransitionsEnabledChange) = rememberPreference(
        AmbientTrackTransitionsEnabledKey,
        defaultValue = true,
    )
    val (autoHideBackButtonEnabled, onAutoHideBackButtonEnabledChange) = rememberPreference(
        AmbientAutoHideBackButtonEnabledKey,
        defaultValue = true,
    )

    Column(
        modifier = Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        Text(
            text = stringResource(R.string.ambient_mode_settings_desc),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        )

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_video_canvas),
            items = listOf(
                switchItem(
                    icon = R.drawable.canvas_art,
                    title = R.string.ambient_video_canvas,
                    description = R.string.ambient_video_canvas_desc,
                    checked = videoCanvasEnabled,
                    onCheckedChange = onVideoCanvasEnabledChange,
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_canvas_source),
            items = listOf(
                canvasSourceItem(
                    source = CanvasSource.AUTO,
                    canvasSource = canvasSource,
                    enabled = videoCanvasEnabled,
                    onCanvasSourceChange = onCanvasSourceChange,
                    title = R.string.canvas_source_auto,
                    description = R.string.canvas_source_auto_desc,
                ),
                canvasSourceItem(
                    source = CanvasSource.ECHO_MUSIC,
                    canvasSource = canvasSource,
                    enabled = videoCanvasEnabled,
                    onCanvasSourceChange = onCanvasSourceChange,
                    title = R.string.canvas_source_echo_music,
                    description = R.string.canvas_source_echo_music_desc,
                ),
                canvasSourceItem(
                    source = CanvasSource.APPLE_MUSIC,
                    canvasSource = canvasSource,
                    enabled = videoCanvasEnabled,
                    onCanvasSourceChange = onCanvasSourceChange,
                    title = R.string.canvas_source_apple_music,
                    description = R.string.canvas_source_apple_music_desc,
                ),
                canvasSourceItem(
                    source = CanvasSource.VIVIMUSIC,
                    canvasSource = canvasSource,
                    enabled = videoCanvasEnabled,
                    onCanvasSourceChange = onCanvasSourceChange,
                    title = R.string.canvas_source_vivimusic,
                    description = R.string.canvas_source_vivimusic_desc,
                ),
                canvasSourceItem(
                    source = CanvasSource.TIDAL,
                    canvasSource = canvasSource,
                    enabled = videoCanvasEnabled,
                    onCanvasSourceChange = onCanvasSourceChange,
                    title = R.string.canvas_source_tidal,
                    description = R.string.canvas_source_tidal_desc,
                ),
            )
        )

        Spacer(Modifier.height(16.dp))

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_blur),
            description = stringResource(R.string.ambient_canvas_blur_desc),
            valueLabel = stringResource(R.string.ambient_canvas_blur_value, canvasBlur.roundToInt()),
            value = canvasBlur,
            valueRange = 0f..24f,
            steps = 7,
            enabled = videoCanvasEnabled,
            onValueChange = onCanvasBlurChange,
        )

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_dim),
            description = stringResource(R.string.ambient_canvas_dim_desc),
            valueLabel = stringResource(R.string.ambient_canvas_dim_value, (canvasDim * 100).roundToInt()),
            value = canvasDim,
            valueRange = 0f..0.75f,
            steps = 14,
            enabled = videoCanvasEnabled,
            onValueChange = onCanvasDimChange,
        )

        Spacer(Modifier.height(8.dp))

        // Canvas Position & Fit — everything below only changes how the canvas is placed
        // while the option is on, so the whole group is gated on it (and on the canvas
        // itself) rather than silently doing nothing.
        val sideFitEnabled = videoCanvasEnabled && canvasSideFitEnabled

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_canvas_position_fit),
            items = listOf(
                switchItem(
                    icon = R.drawable.crop,
                    title = R.string.ambient_canvas_position_fit,
                    description = R.string.ambient_canvas_position_fit_desc,
                    checked = canvasSideFitEnabled,
                    enabled = videoCanvasEnabled,
                    onCheckedChange = onCanvasSideFitEnabledChange,
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        // Live sample of every control below it. It is built from the values this screen is
        // already holding rather than from what has round-tripped through DataStore, so the
        // panel and the veil move while a slider is still being dragged.
        AmbientCanvasFitPreview(
            videoCanvasEnabled = videoCanvasEnabled,
            positionFitEnabled = canvasSideFitEnabled,
            anchor = canvasAnchorSide,
            fitMode = canvasFitMode,
            sideWidth = canvasSideWidth,
            sideGradient = canvasSideGradient,
            gradientSpread = canvasGradientSpread,
            farVeil = canvasFarVeil,
            edgeFeather = canvasEdgeFeather,
            dim = canvasDim,
        )

        Spacer(Modifier.height(16.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_canvas_anchor_side),
            items = listOf(
                radioItem(
                    title = R.string.ambient_canvas_anchor_auto,
                    description = R.string.ambient_canvas_anchor_auto_desc,
                    selected = canvasAnchorSide == AmbientCanvasAnchorSide.AUTO,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasAnchorSideChange(AmbientCanvasAnchorSide.AUTO) },
                ),
                radioItem(
                    title = R.string.ambient_canvas_anchor_left,
                    description = R.string.ambient_canvas_anchor_left_desc,
                    selected = canvasAnchorSide == AmbientCanvasAnchorSide.LEFT,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasAnchorSideChange(AmbientCanvasAnchorSide.LEFT) },
                ),
                radioItem(
                    title = R.string.ambient_canvas_anchor_right,
                    description = R.string.ambient_canvas_anchor_right_desc,
                    selected = canvasAnchorSide == AmbientCanvasAnchorSide.RIGHT,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasAnchorSideChange(AmbientCanvasAnchorSide.RIGHT) },
                ),
            )
        )

        Spacer(Modifier.height(16.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_canvas_fit_mode),
            items = listOf(
                radioItem(
                    title = R.string.ambient_canvas_fit_fit,
                    description = R.string.ambient_canvas_fit_fit_desc,
                    selected = canvasFitMode == AmbientCanvasFitMode.FIT,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasFitModeChange(AmbientCanvasFitMode.FIT) },
                ),
                radioItem(
                    title = R.string.ambient_canvas_fit_zoom,
                    description = R.string.ambient_canvas_fit_zoom_desc,
                    selected = canvasFitMode == AmbientCanvasFitMode.ZOOM,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasFitModeChange(AmbientCanvasFitMode.ZOOM) },
                ),
                radioItem(
                    title = R.string.ambient_canvas_fit_stretch,
                    description = R.string.ambient_canvas_fit_stretch_desc,
                    selected = canvasFitMode == AmbientCanvasFitMode.STRETCH,
                    enabled = sideFitEnabled,
                    onClick = { onCanvasFitModeChange(AmbientCanvasFitMode.STRETCH) },
                ),
            )
        )

        Spacer(Modifier.height(16.dp))

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_side_width),
            description = stringResource(R.string.ambient_canvas_side_width_desc),
            valueLabel = stringResource(
                R.string.ambient_canvas_percent_value,
                (canvasSideWidth * 100).roundToInt(),
            ),
            value = canvasSideWidth,
            valueRange = AmbientCanvasFitDefaults.SideWidthRange,
            steps = AmbientCanvasFitDefaults.SideWidthSteps,
            enabled = sideFitEnabled,
            onValueChange = onCanvasSideWidthChange,
        )

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_side_gradient),
            description = stringResource(R.string.ambient_canvas_side_gradient_desc),
            valueLabel = stringResource(
                R.string.ambient_canvas_percent_value,
                (canvasSideGradient * 100).roundToInt(),
            ),
            value = canvasSideGradient,
            valueRange = AmbientCanvasFitDefaults.SideGradientRange,
            steps = AmbientCanvasFitDefaults.SideGradientSteps,
            enabled = sideFitEnabled,
            onValueChange = onCanvasSideGradientChange,
        )

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_gradient_spread),
            description = stringResource(R.string.ambient_canvas_gradient_spread_desc),
            valueLabel = stringResource(
                R.string.ambient_canvas_percent_value,
                (canvasGradientSpread * 100).roundToInt(),
            ),
            value = canvasGradientSpread,
            valueRange = AmbientCanvasFitDefaults.GradientSpreadRange,
            steps = AmbientCanvasFitDefaults.GradientSpreadSteps,
            enabled = sideFitEnabled,
            onValueChange = onCanvasGradientSpreadChange,
        )

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_far_veil),
            description = stringResource(R.string.ambient_canvas_far_veil_desc),
            valueLabel = stringResource(
                R.string.ambient_canvas_percent_value,
                (canvasFarVeil * 100).roundToInt(),
            ),
            value = canvasFarVeil,
            valueRange = AmbientCanvasFitDefaults.FarVeilRange,
            steps = AmbientCanvasFitDefaults.FarVeilSteps,
            enabled = sideFitEnabled,
            onValueChange = onCanvasFarVeilChange,
        )

        AmbientSliderSetting(
            title = stringResource(R.string.ambient_canvas_edge_feather),
            description = stringResource(R.string.ambient_canvas_edge_feather_desc),
            valueLabel = stringResource(
                R.string.ambient_canvas_percent_value,
                (canvasEdgeFeather * 100).roundToInt(),
            ),
            value = canvasEdgeFeather,
            valueRange = AmbientCanvasFitDefaults.EdgeFeatherRange,
            steps = AmbientCanvasFitDefaults.EdgeFeatherSteps,
            enabled = sideFitEnabled,
            onValueChange = onCanvasEdgeFeatherChange,
        )

        Spacer(Modifier.height(8.dp))

        AmbientSliderSetting(
            title = stringResource(R.string.lyrics_text_size),
            description = stringResource(R.string.ambient_lyrics_text_size_desc),
            valueLabel = stringResource(R.string.ambient_lyrics_text_size_value, lyricsTextSize.roundToInt()),
            value = lyricsTextSize,
            valueRange = 16f..56f,
            steps = 19,
            enabled = true,
            onValueChange = onLyricsTextSizeChange,
        )

        Spacer(Modifier.height(8.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_display),
            items = listOf(
                switchItem(
                    icon = R.drawable.fullscreen,
                    title = R.string.ambient_progress_ring,
                    description = R.string.ambient_progress_ring_desc,
                    checked = progressRingEnabled,
                    onCheckedChange = onProgressRingEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.play,
                    title = R.string.ambient_playback_feedback,
                    description = R.string.ambient_playback_feedback_desc,
                    checked = playbackFeedbackEnabled,
                    onCheckedChange = onPlaybackFeedbackEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.tune,
                    title = R.string.ambient_seek_time,
                    description = R.string.ambient_seek_time_desc,
                    checked = seekTimeEnabled,
                    enabled = progressRingEnabled,
                    onCheckedChange = onSeekTimeEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.sparks,
                    title = R.string.ambient_seek_haptics,
                    description = R.string.ambient_seek_haptics_desc,
                    checked = seekHapticsEnabled,
                    enabled = progressRingEnabled,
                    onCheckedChange = onSeekHapticsEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.image,
                    title = R.string.ambient_track_info,
                    description = R.string.ambient_track_info_desc,
                    checked = trackInfoEnabled,
                    onCheckedChange = onTrackInfoEnabledChange,
                ),
            )
        )

        Spacer(Modifier.height(16.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.ambient_interactions),
            items = listOf(
                switchItem(
                    icon = R.drawable.play,
                    title = R.string.ambient_tap_play_pause,
                    description = R.string.ambient_tap_play_pause_desc,
                    checked = tapToPlayPauseEnabled,
                    onCheckedChange = onTapToPlayPauseEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.sparks,
                    title = R.string.ambient_swipe_navigation,
                    description = R.string.ambient_swipe_navigation_desc,
                    checked = swipeNavigationEnabled,
                    onCheckedChange = onSwipeNavigationEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.play,
                    title = R.string.ambient_track_transitions,
                    description = R.string.ambient_track_transitions_desc,
                    checked = trackTransitionsEnabled,
                    enabled = swipeNavigationEnabled,
                    onCheckedChange = onTrackTransitionsEnabledChange,
                ),
                switchItem(
                    icon = R.drawable.fullscreen,
                    title = R.string.ambient_auto_hide_back_button,
                    description = R.string.ambient_auto_hide_back_button_desc,
                    checked = autoHideBackButtonEnabled,
                    onCheckedChange = onAutoHideBackButtonEnabledChange,
                ),
            )
        )

        Spacer(Modifier.height(36.dp))
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        scrollBehavior = scrollBehavior,
        title = { Text(stringResource(R.string.ambient_mode)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun switchItem(
    icon: Int,
    title: Int,
    description: Int,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
): Material3SettingsItem = Material3SettingsItem(
    icon = painterResource(icon),
    title = { Text(stringResource(title)) },
    description = { Text(stringResource(description)) },
    enabled = enabled,
    trailingContent = {
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
        )
    },
    onClick = { onCheckedChange(!checked) },
)

@Composable
private fun canvasSourceItem(
    source: CanvasSource,
    canvasSource: CanvasSource,
    enabled: Boolean,
    onCanvasSourceChange: (CanvasSource) -> Unit,
    title: Int,
    description: Int,
): Material3SettingsItem = radioItem(
    title = title,
    description = description,
    selected = canvasSource == source,
    enabled = enabled,
    onClick = { onCanvasSourceChange(source) },
)

/** A single-choice row: the same shape every Ambient Mode picker uses. */
@Composable
private fun radioItem(
    title: Int,
    description: Int,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
): Material3SettingsItem = Material3SettingsItem(
    leadingContent = {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
        )
    },
    title = { Text(stringResource(title)) },
    description = { Text(stringResource(description)) },
    enabled = enabled,
    onClick = onClick,
)

@Composable
private fun AmbientSliderSetting(
    title: String,
    description: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueLabel,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = description,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
    Spacer(Modifier.height(12.dp))
}
