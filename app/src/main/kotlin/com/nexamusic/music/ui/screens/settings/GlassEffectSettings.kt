package com.nexamusic.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.material3.Slider
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.LocalPlayerConnection
import com.nexamusic.app.R
import com.nexamusic.app.constants.HomeBackgroundEnabledKey
import com.nexamusic.app.constants.HomeBackgroundPathKey
import com.nexamusic.app.constants.LiquidGlassChromaticAberrationKey
import com.nexamusic.app.constants.LiquidGlassDepthEffectKey
import com.nexamusic.app.constants.LiquidGlassBlurRadiusKey
import com.nexamusic.app.constants.LiquidGlassLensAmountKey
import com.nexamusic.app.constants.LiquidGlassLensHeightKey
import com.nexamusic.app.constants.LiquidGlassMiniPlayerEnabledKey
import com.nexamusic.app.constants.LiquidGlassNavBarEnabledKey
import com.nexamusic.app.constants.LiquidGlassSidePanelEnabledKey
import com.nexamusic.app.constants.LiquidGlassSidePanelVibrancyKey
import com.nexamusic.app.constants.LiquidGlassSidePanelBlurRadiusKey
import com.nexamusic.app.constants.LiquidGlassSidePanelLensHeightKey
import com.nexamusic.app.constants.LiquidGlassSidePanelLensAmountKey
import com.nexamusic.app.constants.LiquidGlassSidePanelColorKey
import com.nexamusic.app.constants.LiquidGlassSidePanelSurfaceOpacityKey
import com.nexamusic.app.constants.LiquidGlassSidePanelTextColorKey
import com.nexamusic.app.constants.LiquidGlassSurfaceOpacityKey
import com.nexamusic.app.constants.LiquidGlassPuckColorKey
import com.nexamusic.app.constants.LiquidGlassPuckOpacityKey
import com.nexamusic.app.constants.LiquidGlassStyleKey
import com.nexamusic.app.constants.LiquidGlassHighlightColorKey
import com.nexamusic.app.constants.LiquidGlassHighlightOpacityKey
import com.nexamusic.app.constants.LiquidGlassSurfaceTintColorKey
import com.nexamusic.app.constants.LiquidGlassAdaptiveContrastKey
import com.nexamusic.app.constants.LiquidGlassTextColorKey
import com.nexamusic.app.constants.LiquidGlassVibrancyKey
import com.nexamusic.app.models.MediaMetadata
import com.nexamusic.app.ui.component.GlassStyle
import com.nexamusic.app.ui.component.ColorPickerDialog
import com.nexamusic.app.ui.component.DefaultDialog
import com.nexamusic.app.ui.component.GlassEffectConfig
import com.nexamusic.app.ui.component.LocalAppBackdrop
import com.nexamusic.app.ui.component.LocalAppleMusicUi
import com.nexamusic.app.ui.screens.Screens
import com.nexamusic.app.ui.component.backdrop.backdrops.layerBackdrop
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.nexamusic.app.ui.component.isGlassAllowed
import com.nexamusic.app.ui.component.liquidGlass
import com.nexamusic.app.ui.component.shapes.ContinuousRoundedRectangle
import com.nexamusic.app.ui.component.IconButton as AppIconButton
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import kotlin.math.roundToInt

/** Small current-color preview for a color-picker settings row's trailing content. */
@Composable
private fun ColorSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassEffectSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (vibrancy, onVibrancyChange) = rememberPreference(
        LiquidGlassVibrancyKey, defaultValue = 1.2f
    )
    val (blurRadius, onBlurRadiusChange) = rememberPreference(
        LiquidGlassBlurRadiusKey, defaultValue = 2f
    )
    val (lensHeight, onLensHeightChange) = rememberPreference(
        LiquidGlassLensHeightKey, defaultValue = 0.4f
    )
    val (lensAmount, onLensAmountChange) = rememberPreference(
        LiquidGlassLensAmountKey, defaultValue = 0.6f
    )
    val (chromaticAberration, onChromaticAberrationChange) = rememberPreference(
        LiquidGlassChromaticAberrationKey, defaultValue = false
    )
    val (depthEffect, onDepthEffectChange) = rememberPreference(
        LiquidGlassDepthEffectKey, defaultValue = false
    )
    // 0 marks the theme-adaptive default tint (see MainActivity); the picker then
    // shows the color the current theme resolves to.
    // 0, matching MainActivity. This used to default to a literal dark tint here
    // while MainActivity defaulted to 0 (the adaptive sentinel), so on a fresh
    // install the picker showed a fixed dark colour that the app was not actually
    // using — and reading the two files gave opposite answers about the default.
    val (surfaceTintColorInt, onSurfaceTintColorChange) = rememberPreference(
        LiquidGlassSurfaceTintColorKey, defaultValue = 0
    )
    val (glassStyle, onGlassStyleChange) = rememberEnumPreference(
        LiquidGlassStyleKey, defaultValue = GlassStyle.LIQUID
    )
    val (puckColorInt, onPuckColorChange) = rememberPreference(
        LiquidGlassPuckColorKey, defaultValue = 0
    )
    val (puckOpacity, onPuckOpacityChange) = rememberPreference(
        LiquidGlassPuckOpacityKey, defaultValue = 0.8f
    )
    val (highlightColorInt, onHighlightColorChange) = rememberPreference(
        LiquidGlassHighlightColorKey, defaultValue = 0
    )
    val (highlightOpacity, onHighlightOpacityChange) = rememberPreference(
        LiquidGlassHighlightOpacityKey, defaultValue = 0.55f
    )
    val adaptiveTintColor = if (MaterialTheme.colorScheme.surface.luminance() > 0.5f) {
        Color(0xFFFAFAFA)
    } else {
        Color(0xFF4A4A4E)
    }
    val surfaceTintColor = if (surfaceTintColorInt == 0) {
        adaptiveTintColor
    } else {
        Color(surfaceTintColorInt)
    }
    val (surfaceOpacity, onSurfaceOpacityChange) = rememberPreference(
        LiquidGlassSurfaceOpacityKey, defaultValue = 0.5f
    )
    // 0 marks the theme-adaptive default text color (see MainActivity); the picker
    // then shows the color the current theme resolves to.
    val (textColorInt, onTextColorChange) = rememberPreference(
        LiquidGlassTextColorKey, defaultValue = 0
    )
    val (adaptiveContrast, onAdaptiveContrastChange) = rememberPreference(
        LiquidGlassAdaptiveContrastKey, defaultValue = true
    )
    val adaptiveTextColor = if (MaterialTheme.colorScheme.surface.luminance() > 0.5f) {
        Color(0xFF1A1A1A)
    } else {
        Color.White
    }
    val textColor = if (textColorInt == 0) adaptiveTextColor else Color(textColorInt)
    val (miniPlayerEnabled, onMiniPlayerEnabledChange) = rememberPreference(
        LiquidGlassMiniPlayerEnabledKey, defaultValue = true
    )
    val (navBarEnabled, onNavBarEnabledChange) = rememberPreference(
        LiquidGlassNavBarEnabledKey, defaultValue = true
    )
    val (sidePanelEnabled, onSidePanelEnabledChange) = rememberPreference(
        LiquidGlassSidePanelEnabledKey, defaultValue = true
    )
    val (sidePanelVibrancy, onSidePanelVibrancyChange) = rememberPreference(
        LiquidGlassSidePanelVibrancyKey, defaultValue = 1.2f
    )
    val (sidePanelBlurRadius, onSidePanelBlurRadiusChange) = rememberPreference(
        LiquidGlassSidePanelBlurRadiusKey, defaultValue = 2f
    )
    val (sidePanelLensHeight, onSidePanelLensHeightChange) = rememberPreference(
        LiquidGlassSidePanelLensHeightKey, defaultValue = 0.4f
    )
    val (sidePanelLensAmount, onSidePanelLensAmountChange) = rememberPreference(
        LiquidGlassSidePanelLensAmountKey, defaultValue = 0.6f
    )
    val (sidePanelColorInt, onSidePanelColorChange) = rememberPreference(
        LiquidGlassSidePanelColorKey, defaultValue = 0
    )
    val (sidePanelSurfaceOpacity, onSidePanelSurfaceOpacityChange) = rememberPreference(
        LiquidGlassSidePanelSurfaceOpacityKey, defaultValue = 0.5f
    )
    val (sidePanelTextColorInt, onSidePanelTextColorChange) = rememberPreference(
        LiquidGlassSidePanelTextColorKey, defaultValue = 0
    )
    val sidePanelTextColor = if (sidePanelTextColorInt == 0) adaptiveTextColor else Color(sidePanelTextColorInt)
    val sidePanelTintColor = if (sidePanelColorInt == 0) adaptiveTintColor else Color(sidePanelColorInt)

    // Enabling the side panel override starts it as a copy of the current
    // global values (rather than fixed defaults that may already differ from
    // global), so the look doesn't jump and there's an actual starting point
    // to dial independently from. Nav bar / mini bar has no override — it
    // always mirrors the global settings directly.
    val toggleSidePanel: (Boolean) -> Unit = { enabling ->
        if (enabling) {
            onSidePanelVibrancyChange(vibrancy)
            onSidePanelBlurRadiusChange(blurRadius)
            onSidePanelLensHeightChange(lensHeight)
            onSidePanelLensAmountChange(lensAmount)
            onSidePanelColorChange(surfaceTintColorInt)
            onSidePanelSurfaceOpacityChange(surfaceOpacity)
            onSidePanelTextColorChange(textColorInt)
        }
        onSidePanelEnabledChange(enabling)
    }

    var showVibrancyDialog by rememberSaveable { mutableStateOf(false) }
    var showBlurRadiusDialog by rememberSaveable { mutableStateOf(false) }
    var showLensHeightDialog by rememberSaveable { mutableStateOf(false) }
    var showLensAmountDialog by rememberSaveable { mutableStateOf(false) }
    var showSurfaceOpacityDialog by rememberSaveable { mutableStateOf(false) }
    var showSurfaceTintDialog by rememberSaveable { mutableStateOf(false) }
    var showGlassStyleDialog by rememberSaveable { mutableStateOf(false) }
    var showPuckColorDialog by rememberSaveable { mutableStateOf(false) }
    var showPuckOpacityDialog by rememberSaveable { mutableStateOf(false) }
    var showHighlightColorDialog by rememberSaveable { mutableStateOf(false) }
    var showHighlightOpacityDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelVibrancyDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelBlurRadiusDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelLensHeightDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelLensAmountDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelColorDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelSurfaceOpacityDialog by rememberSaveable { mutableStateOf(false) }
    var showSidePanelTextColorDialog by rememberSaveable { mutableStateOf(false) }
    var showTextColorDialog by rememberSaveable { mutableStateOf(false) }

    // Live sample of the dials below. Built from the in-screen values rather than
    // LocalGlassEffectConfig so it updates as the user drags, without waiting for
    // the app-wide config to round-trip through DataStore.
    val previewConfig = GlassEffectConfig(
        vibrancy = vibrancy,
        blurRadius = blurRadius,
        lensHeight = lensHeight,
        lensAmount = lensAmount,
        chromaticAberration = chromaticAberration,
        depthEffect = depthEffect,
        surfaceTintColor = if (surfaceTintColorInt == 0) Color.Unspecified else Color(surfaceTintColorInt),
        surfaceOpacity = surfaceOpacity,
        textColor = textColor,
    )

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        GlassLivePreview(
            config = previewConfig,
            // The settings Column insets everything by 16dp, which left the preview
            // narrower than the bar it is previewing. Bleed most of that back out.
            modifier = Modifier
                .bleedHorizontally(10.dp)
                .padding(top = 8.dp, bottom = 16.dp),
        )

        Material3SettingsGroup(
            title = stringResource(R.string.liquid_glass_effects),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_vibrancy)) },
                    description = { Text(stringResource(R.string.liquid_glass_vibrancy_desc)) },
                    onClick = { showVibrancyDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.sliders),
                    title = { Text(stringResource(R.string.liquid_glass_blur_radius)) },
                    description = { Text(stringResource(R.string.liquid_glass_blur_radius_desc)) },
                    onClick = { showBlurRadiusDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_lens_height)) },
                    onClick = { showLensHeightDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_lens_amount)) },
                    onClick = { showLensAmountDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_chromatic_aberration)) },
                    trailingContent = {
                        Switch(
                            checked = chromaticAberration,
                            onCheckedChange = onChromaticAberrationChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (chromaticAberration) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onChromaticAberrationChange(!chromaticAberration) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_depth_effect)) },
                    trailingContent = {
                        Switch(
                            checked = depthEffect,
                            onCheckedChange = onDepthEffectChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (depthEffect) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onDepthEffectChange(!depthEffect) }
                ),
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.liquid_glass_appearance),
            items = listOfNotNull(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.liquid_glass_surface_tint)) },
                    description = { Text(stringResource(R.string.liquid_glass_surface_tint_desc)) },
                    trailingContent = { ColorSwatch(surfaceTintColor) },
                    onClick = { showSurfaceTintDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_surface_opacity)) },
                    description = { Text(stringResource(R.string.liquid_glass_surface_opacity_desc)) },
                    onClick = { showSurfaceOpacityDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_style)) },
                    description = {
                        Text(
                            stringResource(
                                when (glassStyle) {
                                    GlassStyle.LIQUID -> R.string.liquid_glass_style_liquid
                                    GlassStyle.BLUR -> R.string.liquid_glass_style_blur
                                    GlassStyle.TRANSPARENT -> R.string.liquid_glass_style_transparent
                                }
                            )
                        )
                    },
                    onClick = { showGlassStyleDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.liquid_glass_puck_color)) },
                    description = { Text(stringResource(R.string.liquid_glass_puck_color_desc)) },
                    onClick = { showPuckColorDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_puck_opacity)) },
                    description = { Text(stringResource(R.string.liquid_glass_puck_opacity_desc)) },
                    onClick = { showPuckOpacityDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.liquid_glass_highlight_color)) },
                    description = { Text(stringResource(R.string.liquid_glass_highlight_color_desc)) },
                    onClick = { showHighlightColorDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass_highlight_opacity)) },
                    description = { Text(stringResource(R.string.liquid_glass_highlight_opacity_desc)) },
                    onClick = { showHighlightOpacityDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.contrast),
                    title = { Text(stringResource(R.string.liquid_glass_adaptive_contrast)) },
                    description = { Text(stringResource(R.string.liquid_glass_adaptive_contrast_desc)) },
                    trailingContent = {
                        Switch(
                            checked = adaptiveContrast,
                            onCheckedChange = onAdaptiveContrastChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (adaptiveContrast) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        )
                    },
                    onClick = { onAdaptiveContrastChange(!adaptiveContrast) }
                ),
                // Hidden while adaptive contrast is on: a manual colour would be
                // overridden anyway, so offering it would just look broken.
                if (adaptiveContrast) {
                    null
                } else {
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.liquid_glass_text_color)) },
                        description = { Text(stringResource(R.string.liquid_glass_text_color_desc)) },
                        trailingContent = { ColorSwatch(textColor) },
                        onClick = { showTextColorDialog = true }
                    )
                },
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.liquid_glass_per_component),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.music_note),
                    title = { Text(stringResource(R.string.liquid_glass_mini_player)) },
                    trailingContent = {
                        Switch(
                            checked = miniPlayerEnabled,
                            onCheckedChange = onMiniPlayerEnabledChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (miniPlayerEnabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onMiniPlayerEnabledChange(!miniPlayerEnabled) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.nav_bar),
                    title = { Text(stringResource(R.string.liquid_glass_nav_bar)) },
                    trailingContent = {
                        Switch(
                            checked = navBarEnabled,
                            onCheckedChange = onNavBarEnabledChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (navBarEnabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onNavBarEnabledChange(!navBarEnabled) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.nav_bar),
                    title = { Text(stringResource(R.string.liquid_glass_side_panel)) },
                    description = { Text(stringResource(R.string.liquid_glass_side_panel_desc)) },
                    trailingContent = {
                        Switch(
                            checked = sidePanelEnabled,
                            onCheckedChange = toggleSidePanel,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (sidePanelEnabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { toggleSidePanel(!sidePanelEnabled) }
                ),
            )
        )

        if (sidePanelEnabled) {
            Spacer(modifier = Modifier.height(27.dp))

            Material3SettingsGroup(
                title = stringResource(R.string.liquid_glass_side_panel_overrides),
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.tune),
                        title = { Text(stringResource(R.string.liquid_glass_vibrancy)) },
                        onClick = { showSidePanelVibrancyDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.sliders),
                        title = { Text(stringResource(R.string.liquid_glass_blur_radius)) },
                        onClick = { showSidePanelBlurRadiusDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.tune),
                        title = { Text(stringResource(R.string.liquid_glass_lens_height)) },
                        onClick = { showSidePanelLensHeightDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.tune),
                        title = { Text(stringResource(R.string.liquid_glass_lens_amount)) },
                        onClick = { showSidePanelLensAmountDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.liquid_glass_surface_tint)) },
                        description = { Text(stringResource(R.string.liquid_glass_side_panel_surface_tint_desc)) },
                        trailingContent = { ColorSwatch(sidePanelTintColor) },
                        onClick = { showSidePanelColorDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.tune),
                        title = { Text(stringResource(R.string.liquid_glass_surface_opacity)) },
                        onClick = { showSidePanelSurfaceOpacityDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.liquid_glass_text_color)) },
                        description = { Text(stringResource(R.string.liquid_glass_side_panel_text_color_desc)) },
                        trailingContent = { ColorSwatch(sidePanelTextColor) },
                        onClick = { showSidePanelTextColorDialog = true }
                    ),
                )
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showVibrancyDialog) {
        var tempValue by remember { mutableFloatStateOf(vibrancy) }
        DefaultDialog(
            onDismiss = { tempValue = vibrancy; showVibrancyDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 1f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = vibrancy; showVibrancyDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onVibrancyChange(tempValue); showVibrancyDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_vibrancy), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showBlurRadiusDialog) {
        var tempValue by remember { mutableFloatStateOf(blurRadius) }
        DefaultDialog(
            onDismiss = { tempValue = blurRadius; showBlurRadiusDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 8f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = blurRadius; showBlurRadiusDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onBlurRadiusChange(tempValue); showBlurRadiusDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_blur_radius), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.0f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..100f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showLensHeightDialog) {
        var tempValue by remember { mutableFloatStateOf(lensHeight) }
        DefaultDialog(
            onDismiss = { tempValue = lensHeight; showLensHeightDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.5f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = lensHeight; showLensHeightDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onLensHeightChange(tempValue); showLensHeightDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_lens_height), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                // Range runs past 1.0: 1.0 is the old maximum (LENS_MAX_DP), and the
                // glass could not be pushed any thicker than that no matter how far
                // the slider went. Values above 1 scale beyond it.
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showLensAmountDialog) {
        var tempValue by remember { mutableFloatStateOf(lensAmount) }
        DefaultDialog(
            onDismiss = { tempValue = lensAmount; showLensAmountDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.5f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = lensAmount; showLensAmountDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onLensAmountChange(tempValue); showLensAmountDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_lens_amount), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                // See the lens-height dialog above for why this runs to 2.
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelVibrancyDialog) {
        var tempValue by remember { mutableFloatStateOf(sidePanelVibrancy) }
        DefaultDialog(
            onDismiss = { tempValue = sidePanelVibrancy; showSidePanelVibrancyDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 1.2f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = sidePanelVibrancy; showSidePanelVibrancyDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSidePanelVibrancyChange(tempValue); showSidePanelVibrancyDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_vibrancy), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelBlurRadiusDialog) {
        var tempValue by remember { mutableFloatStateOf(sidePanelBlurRadius) }
        DefaultDialog(
            onDismiss = { tempValue = sidePanelBlurRadius; showSidePanelBlurRadiusDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 2f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = sidePanelBlurRadius; showSidePanelBlurRadiusDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSidePanelBlurRadiusChange(tempValue); showSidePanelBlurRadiusDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_blur_radius), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.0f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..100f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelLensHeightDialog) {
        var tempValue by remember { mutableFloatStateOf(sidePanelLensHeight) }
        DefaultDialog(
            onDismiss = { tempValue = sidePanelLensHeight; showSidePanelLensHeightDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.4f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = sidePanelLensHeight; showSidePanelLensHeightDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSidePanelLensHeightChange(tempValue); showSidePanelLensHeightDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_lens_height), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                // Range runs past 1.0: 1.0 is the old maximum (LENS_MAX_DP), and the
                // glass could not be pushed any thicker than that no matter how far
                // the slider went. Values above 1 scale beyond it.
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelLensAmountDialog) {
        var tempValue by remember { mutableFloatStateOf(sidePanelLensAmount) }
        DefaultDialog(
            onDismiss = { tempValue = sidePanelLensAmount; showSidePanelLensAmountDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.6f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = sidePanelLensAmount; showSidePanelLensAmountDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSidePanelLensAmountChange(tempValue); showSidePanelLensAmountDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_lens_amount), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                // See the lens-height dialog above for why this runs to 2.
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..2f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelSurfaceOpacityDialog) {
        var tempValue by remember { mutableFloatStateOf(sidePanelSurfaceOpacity) }
        DefaultDialog(
            onDismiss = { tempValue = sidePanelSurfaceOpacity; showSidePanelSurfaceOpacityDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.5f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = sidePanelSurfaceOpacity; showSidePanelSurfaceOpacityDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSidePanelSurfaceOpacityChange(tempValue); showSidePanelSurfaceOpacityDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_surface_opacity), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSidePanelColorDialog) {
        ColorPickerDialog(
            initialColor = sidePanelTintColor,
            title = stringResource(R.string.liquid_glass_surface_tint),
            onDismiss = { showSidePanelColorDialog = false },
            onConfirm = { color ->
                onSidePanelColorChange(color.toArgb())
                showSidePanelColorDialog = false
            },
            onReset = {
                onSidePanelColorChange(0)
                showSidePanelColorDialog = false
            },
        )
    }

    if (showSidePanelTextColorDialog) {
        ColorPickerDialog(
            initialColor = sidePanelTextColor,
            title = stringResource(R.string.liquid_glass_text_color),
            onDismiss = { showSidePanelTextColorDialog = false },
            onConfirm = { color ->
                onSidePanelTextColorChange(color.toArgb())
                showSidePanelTextColorDialog = false
            },
            defaultColor = Color.White,
        )
    }

    if (showSurfaceOpacityDialog) {
        var tempValue by remember { mutableFloatStateOf(surfaceOpacity) }
        DefaultDialog(
            onDismiss = { tempValue = surfaceOpacity; showSurfaceOpacityDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.3f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = surfaceOpacity; showSurfaceOpacityDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onSurfaceOpacityChange(tempValue); showSurfaceOpacityDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_surface_opacity), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showGlassStyleDialog) {
        DefaultDialog(
            onDismiss = { showGlassStyleDialog = false },
            buttons = {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { showGlassStyleDialog = false }) { Text(stringResource(android.R.string.cancel)) }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.liquid_glass_style),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                GlassStyle.entries.forEach { style ->
                    val (label, desc) = when (style) {
                        GlassStyle.LIQUID -> R.string.liquid_glass_style_liquid to R.string.liquid_glass_style_liquid_desc
                        GlassStyle.BLUR -> R.string.liquid_glass_style_blur to R.string.liquid_glass_style_blur_desc
                        GlassStyle.TRANSPARENT -> R.string.liquid_glass_style_transparent to R.string.liquid_glass_style_transparent_desc
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onGlassStyleChange(style)
                                showGlassStyleDialog = false
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(label),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (style == glassStyle) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (showPuckColorDialog) {
        ColorPickerDialog(
            initialColor = if (puckColorInt == 0) Color(0xFF1C1B1C) else Color(puckColorInt),
            title = stringResource(R.string.liquid_glass_puck_color),
            onDismiss = { showPuckColorDialog = false },
            onConfirm = { color ->
                onPuckColorChange(color.toArgb())
                showPuckColorDialog = false
            },
            // Reset returns to the theme-adaptive wash, not a fixed colour.
            onReset = {
                onPuckColorChange(0)
                showPuckColorDialog = false
            },
        )
    }

    if (showPuckOpacityDialog) {
        var tempValue by remember { mutableFloatStateOf(puckOpacity) }
        DefaultDialog(
            onDismiss = { tempValue = puckOpacity; showPuckOpacityDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.8f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = puckOpacity; showPuckOpacityDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onPuckOpacityChange(tempValue); showPuckOpacityDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_puck_opacity), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showHighlightColorDialog) {
        ColorPickerDialog(
            initialColor = if (highlightColorInt == 0) Color.White else Color(highlightColorInt),
            title = stringResource(R.string.liquid_glass_highlight_color),
            onDismiss = { showHighlightColorDialog = false },
            onConfirm = { color ->
                onHighlightColorChange(color.toArgb())
                showHighlightColorDialog = false
            },
            onReset = {
                onHighlightColorChange(0)
                showHighlightColorDialog = false
            },
        )
    }

    if (showHighlightOpacityDialog) {
        var tempValue by remember { mutableFloatStateOf(highlightOpacity) }
        DefaultDialog(
            onDismiss = { tempValue = highlightOpacity; showHighlightOpacityDialog = false },
            buttons = {
                TextButton(onClick = { tempValue = 0.55f }) { Text(stringResource(R.string.reset)) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { tempValue = highlightOpacity; showHighlightOpacityDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = { onHighlightOpacityChange(tempValue); showHighlightOpacityDialog = false }) { Text(stringResource(android.R.string.ok)) }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.liquid_glass_highlight_opacity), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "%.2f".format(tempValue), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                Slider(value = tempValue, onValueChange = { tempValue = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showSurfaceTintDialog) {
        ColorPickerDialog(
            initialColor = surfaceTintColor,
            title = stringResource(R.string.liquid_glass_surface_tint),
            onDismiss = { showSurfaceTintDialog = false },
            onConfirm = { color ->
                onSurfaceTintColorChange(color.toArgb())
                showSurfaceTintDialog = false
            },
            // Reset restores the theme-adaptive default rather than a fixed color.
            onReset = {
                onSurfaceTintColorChange(0)
                showSurfaceTintDialog = false
            },
        )
    }

    if (showTextColorDialog) {
        ColorPickerDialog(
            initialColor = textColor,
            title = stringResource(R.string.liquid_glass_text_color),
            onDismiss = { showTextColorDialog = false },
            onConfirm = { color ->
                onTextColorChange(color.toArgb())
                showTextColorDialog = false
            },
            defaultColor = Color.White,
        )
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.liquid_glass_settings)) },
        navigationIcon = {
            AppIconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}

/**
 * Live sample of the current glass settings: a mock nav bar over real content.
 *
 * The glass surfaces sample [previewBackdrop], which captures ONLY the image panel
 * they sit over. That sibling relationship is load-bearing, not stylistic: a glass
 * surface may sample an attached backdrop only when it is NOT inside that backdrop's
 * layer. Sampling the ambient [LocalAppBackdrop] here would make the app-wide capture
 * include these pills, which re-enters RenderNode::prepareTreeImpl and hard-crashes
 * the renderer — the same cycle that used to kill the artist and playlist screens.
 * So the panel is captured on its own and the bar is drawn next to it, never within it.
 */
@Composable
private fun GlassLivePreview(
    config: GlassEffectConfig,
    modifier: Modifier = Modifier,
) {
    val previewBackdrop = rememberLayerBackdrop()
    val pillShape = ContinuousRoundedRectangle(percent = 50)
    val appleMusicUi = LocalAppleMusicUi.current

    // Same source PlayerThemeScreen previews from: whatever is actually playing.
    val playerConnection = LocalPlayerConnection.current
    val mediaMetadata by remember(playerConnection) {
        playerConnection?.mediaMetadata ?: MutableStateFlow<MediaMetadata?>(null)
    }.collectAsState()

    // Failing a playing song, the user's own home background — it is literally what
    // sits behind the real nav bar, so it is the most honest thing to preview over.
    val (homeBackgroundEnabled) = rememberPreference(HomeBackgroundEnabledKey, false)
    val (homeBackgroundPath) = rememberPreference(HomeBackgroundPathKey, "")
    val backgroundModel: Any? = mediaMetadata?.thumbnailUrl
        ?: homeBackgroundPath.takeIf { homeBackgroundEnabled && it.isNotEmpty() }?.let { File(it) }

    // The real bar's tabs, minus search — that one is the standalone circle beside it.
    val previewTabs = remember { Screens.MainScreens.filter { it != Screens.Search }.take(4) }
    val imageScroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(previewBackdrop)
                // Always drawn: it is the fallback when nothing is playing and no
                // custom background is set, and the saturated stops give blur,
                // vibrancy and lens refraction something to visibly act on.
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF2D55),
                            Color(0xFF5856D6),
                            Color(0xFF32ADE6),
                            Color(0xFF34C759),
                        )
                    )
                ),
        ) {
            // Read off BoxWithConstraintsScope here: inside the Row below the
            // implicit receiver is RowScope, which has no maxWidth.
            val panelWidth = maxWidth
            if (backgroundModel != null) {
                // Drag the image under the glass to watch the refraction track it.
                // The backdrop re-records on each scrolled frame and bumps its
                // content version, so the pills above pick the movement up live.
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(imageScroll),
                ) {
                    AsyncImage(
                        model = backgroundModel,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight()
                            // Wider than the panel, so there is something to pan to.
                            .width(panelWidth * 1.8f),
                    )
                }
            }
        }

        CompositionLocalProvider(LocalAppBackdrop provides previewBackdrop) {
            // Built once and applied to both surfaces: a Modifier chain is an
            // immutable description, and each node it is attached to gets its own
            // state. Cannot be a local helper function — liquidGlass is @Composable.
            val previewGlass = if (isGlassAllowed()) {
                Modifier.liquidGlass(config = config, shape = pillShape, highlightAlpha = 0.3f)
            } else {
                // Same fallback every other glass call site uses when the device
                // can't run the RenderEffect chain.
                Modifier.background(Color.White.copy(alpha = config.surfaceOpacity), pillShape)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    // Wider than an intrinsic content-fit pill so the preview reads
                    // like the real bar's footprint, not a cramped tight-wrapped chip.
                    .fillMaxWidth(0.85f)
                    .height(IntrinsicSize.Max),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = previewGlass
                        .weight(1f)
                        .padding(4.dp),
                ) {
                    previewTabs.forEachIndexed { index, screen ->
                        // First tab reads as selected, so the preview shows both the
                        // selected and unselected treatments at once.
                        val selected = index == 0
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        ) {
                            // Icon and label sizes mirror the real FloatingNavBar so
                            // the preview reports the bar's true proportions.
                            Icon(
                                painter = painterResource(
                                    screen.icon(appleMusicUi)
                                ),
                                contentDescription = null,
                                tint = if (selected) config.textColor else Color.White,
                                modifier = Modifier.size(30.dp),
                            )
                            Text(
                                text = stringResource(screen.titleId),
                                color = if (selected) config.textColor else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 10.sp,
                            )
                        }
                    }
                }

                // Mirrors the real standalone search tab, including the height-first
                // aspect ratio — so this preview also shows the bar and the circle
                // agreeing on height.
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .then(previewGlass),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(Screens.Search.icon(appleMusicUi)),
                        contentDescription = null,
                        tint = config.textColor,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }
    }
}

/**
 * Lets a child extend [amount] past its parent's horizontal padding on each side.
 *
 * Compose rejects negative padding, and the settings Column pads every child by
 * 16dp, so widening just this one otherwise means unpicking that padding and
 * re-adding it to every sibling. This measures the child with the extra width and
 * then reports the ORIGINAL width back up, so the parent's layout is untouched.
 */
private fun Modifier.bleedHorizontally(amount: Dp) = this.layout { measurable, constraints ->
    val bleedPx = amount.roundToPx()
    val extra = bleedPx * 2
    // An unbounded parent has no padding to escape, and adding to Infinity overflows.
    if (constraints.maxWidth == Constraints.Infinity) {
        val placeable = measurable.measure(constraints)
        return@layout layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.minWidth + extra,
            maxWidth = constraints.maxWidth + extra,
        )
    )
    layout(placeable.width - extra, placeable.height) {
        placeable.place(-bleedPx, 0)
    }
}
