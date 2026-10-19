/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.AppBackgroundColorKey
import com.convx.music.constants.AppTextColorKey
import com.convx.music.constants.ChipSortTypeKey
import com.convx.music.constants.DensityScale
import com.convx.music.constants.DensityScaleKey
import com.convx.music.constants.EnableSettingsPopupKey
import com.convx.music.constants.FullscreenLyricsCollapseTopKey
import com.convx.music.constants.GridCardHeightOverrideKey
import com.convx.music.constants.GridColumnsOverrideKey
import com.convx.music.constants.GridItemSize
import com.convx.music.constants.GridItemsSizeKey
import com.convx.music.constants.GridSpacingKey
import com.convx.music.constants.HideHomeFavoriteIconKey
import com.convx.music.constants.HiddenNavigationRoutesKey
import com.convx.music.constants.HidePlayerThumbnailKey
import com.convx.music.constants.HomeCardCornerRadiusOverrideKey
import com.convx.music.constants.HomeGridColumnsOverrideKey
import com.convx.music.constants.HomeHeroCardHeightOverrideKey
import com.convx.music.constants.IosOverscrollKey
import com.convx.music.constants.LibraryFilter
import com.convx.music.constants.LibraryIconsOnlyKey
import com.convx.music.constants.LocalAlbumsByYearKey
import com.convx.music.constants.EnableLyricsThumbnailPlayPauseKey
import com.convx.music.constants.MiniBarTabStyleKey
import com.convx.music.constants.NavBarMeltTransitionKey
import com.convx.music.constants.PureBlackKey
import com.convx.music.constants.PureBlackMiniPlayerKey
import com.convx.music.constants.ReduceDynamicBlurKey
import com.convx.music.constants.TopBarBlurStyle
import com.convx.music.constants.TopBarBlurStyleKey
import com.convx.music.constants.ShowAudioQualityBadgeKey
import com.convx.music.constants.ShowCommentButtonKey
import com.convx.music.constants.ShowHomeFabKey
import com.convx.music.constants.SpeedDialCardHeightOverrideKey
import com.convx.music.constants.SpeedDialColumnsOverrideKey
import com.convx.music.constants.SheetCornerRadiusKey
import com.convx.music.constants.ThumbnailCornerRadiusKey
import com.convx.music.ui.component.ColorPickerDialog
import com.convx.music.ui.screens.Screens
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.MenuSheetCornerRadius
import com.convx.music.ui.component.SheetCornerRadiusModal
import com.convx.music.ui.component.ThumbnailCornerRadiusModal
import com.convx.music.ui.utils.GridCardHeightChoices
import com.convx.music.ui.utils.GridColumnChoices
import com.convx.music.ui.utils.GridSpacingChoices
import com.convx.music.ui.utils.HomeCardCornerRadiusChoices
import com.convx.music.ui.utils.HomeHeroCardHeightChoices
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlin.math.roundToInt

/**
 * The power-user half of [AppearanceSettings]: fine visual tuning (grid/card sizing,
 * corner radii, niche display toggles) most people never touch, split out so the main
 * Appearance page stays a short, everyday list of things people actually go looking
 * for -- theme, font, icon, player theme -- instead of ~55 items deep.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceAdvancedSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
) {
    // Keep the mini-player setting in sync with the main pure-black setting.
    val (pureBlack, onPureBlackChangeRaw) = rememberPreference(PureBlackKey, defaultValue = false)
    val (_, onPureBlackMiniPlayerChange) = rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)
    val onPureBlackChange: (Boolean) -> Unit = { enabled ->
        onPureBlackChangeRaw(enabled)
        onPureBlackMiniPlayerChange(enabled)
    }

    val (iosOverscroll, onIosOverscrollChange) = rememberPreference(IosOverscrollKey, defaultValue = true)
    val (hiddenNavigationRoutes, onHiddenNavigationRoutesChange) = rememberPreference(HiddenNavigationRoutesKey, "")
    val hiddenTabs = remember(hiddenNavigationRoutes) {
        hiddenNavigationRoutes.lineSequence().map(String::trim).filter(String::isNotEmpty).toSet()
    }
    val (localOnlyMode) = rememberPreference(com.convx.music.constants.LocalOnlyModeKey, false)
    val optionalTabs = Screens.mainScreens(localOnlyMode).filter { it != Screens.Search } +
        if (localOnlyMode) emptyList() else listOf(Screens.Explore)
    val onTabVisibilityChange: (Screens, Boolean) -> Unit = { tab, visible ->
        val updated = if (visible) hiddenTabs - tab.route else hiddenTabs + tab.route
        onHiddenNavigationRoutesChange(updated.sorted().joinToString("\n"))
    }
    val (enableSettingsPopup, onEnableSettingsPopupChange) = rememberPreference(
        EnableSettingsPopupKey,
        defaultValue = false
    )
    val (libraryIconsOnly, onLibraryIconsOnlyChange) = rememberPreference(
        LibraryIconsOnlyKey,
        defaultValue = true
    )

    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(
        HidePlayerThumbnailKey,
        defaultValue = false
    )
    val (thumbnailCornerRadius, onThumbnailCornerRadiusChange) = rememberPreference(
        ThumbnailCornerRadiusKey,
        defaultValue = 3f
    )
    var showThumbnailCornerRadiusDialog by rememberSaveable { mutableStateOf(false) }
    val (sheetCornerRadius, onSheetCornerRadiusChange) = rememberPreference(
        SheetCornerRadiusKey,
        defaultValue = MenuSheetCornerRadius.value
    )
    var showSheetCornerRadiusDialog by rememberSaveable { mutableStateOf(false) }
    val (showAudioQualityBadge, onShowAudioQualityBadgeChange) = rememberPreference(
        ShowAudioQualityBadgeKey,
        defaultValue = true
    )
    val (miniBarTabStyle, onMiniBarTabStyleChange) = rememberPreference(MiniBarTabStyleKey, defaultValue = false)
    val (navBarMeltTransition, onNavBarMeltTransitionChange) = rememberPreference(
        NavBarMeltTransitionKey,
        defaultValue = true
    )
    val (showCommentButton, onShowCommentButtonChange) = rememberPreference(
        ShowCommentButtonKey,
        defaultValue = true
    )

    val (enableLyricsThumbnailPlayPause, onEnableLyricsThumbnailPlayPauseChange) = rememberPreference(
        EnableLyricsThumbnailPlayPauseKey,
        defaultValue = false
    )
    val (fullscreenLyricsCollapseTop, onFullscreenLyricsCollapseTopChange) = rememberPreference(
        FullscreenLyricsCollapseTopKey,
        defaultValue = true
    )

    val (defaultChip, onDefaultChipChange) = rememberEnumPreference(
        key = ChipSortTypeKey,
        defaultValue = LibraryFilter.LIBRARY
    )
    var showDefaultChipDialog by rememberSaveable { mutableStateOf(false) }
    val (gridItemSize, onGridItemSizeChange) = rememberEnumPreference(
        GridItemsSizeKey,
        defaultValue = GridItemSize.SMALL
    )
    var showGridSizeDialog by rememberSaveable { mutableStateOf(false) }

    val context = activity as Context
    val sharedPreferences = remember { context.getSharedPreferences("vivimusic_settings", Context.MODE_PRIVATE) }
    val prefDensityScale = remember(sharedPreferences) {
        sharedPreferences.getFloat("density_scale_factor", 1.0f)
    }
    val (densityScale, setDensityScale) = rememberPreference(DensityScaleKey, defaultValue = prefDensityScale)
    var showRestartDialog by rememberSaveable { mutableStateOf(false) }
    var showDensityScaleDialog by rememberSaveable { mutableStateOf(false) }
    val onDensityScaleChange: (Float) -> Unit = { newScale ->
        setDensityScale(newScale)
        sharedPreferences.edit { putFloat("density_scale_factor", newScale) }
        showRestartDialog = true
    }

    val (reduceDynamicBlur, onReduceDynamicBlurChange) = rememberPreference(ReduceDynamicBlurKey, false)
    val (topBarBlurStyle, onTopBarBlurStyleChange) = rememberEnumPreference(TopBarBlurStyleKey, TopBarBlurStyle.HAZE_PROGRESSIVE)
    var showTopBarBlurDialog by rememberSaveable { mutableStateOf(false) }
    val (showHomeFab, onShowHomeFabChange) = rememberPreference(ShowHomeFabKey, defaultValue = true)
    val (hideHomeFavoriteIcon, onHideHomeFavoriteIconChange) = rememberPreference(
        HideHomeFavoriteIconKey,
        defaultValue = false
    )
    val (gridColumnsOverride, onGridColumnsOverrideChange) = rememberPreference(GridColumnsOverrideKey, 0)
    val (gridCardHeightOverride, onGridCardHeightOverrideChange) = rememberPreference(GridCardHeightOverrideKey, 0)
    val (gridSpacing, onGridSpacingChange) = rememberPreference(GridSpacingKey, 16)
    val (speedDialColumnsOverride, onSpeedDialColumnsOverrideChange) = rememberPreference(SpeedDialColumnsOverrideKey, 0)
    val (homeGridColumnsOverride, onHomeGridColumnsOverrideChange) = rememberPreference(HomeGridColumnsOverrideKey, 0)
    val (homeHeroCardHeightOverride, onHomeHeroCardHeightOverrideChange) = rememberPreference(HomeHeroCardHeightOverrideKey, 0)
    val (speedDialCardHeightOverride, onSpeedDialCardHeightOverrideChange) = rememberPreference(SpeedDialCardHeightOverrideKey, 0)
    val (homeCardCornerRadiusOverride, onHomeCardCornerRadiusOverrideChange) = rememberPreference(HomeCardCornerRadiusOverrideKey, 0)
    // Controls the sort order for the local Albums shelf.
    val (localAlbumsByYear, onLocalAlbumsByYearChange) = rememberPreference(LocalAlbumsByYearKey, defaultValue = true)

    // These colors control the app-wide background and text appearance.
    val (backgroundColorInt, onBackgroundColorChange) = rememberPreference(AppBackgroundColorKey, defaultValue = 0)
    val (textColorInt, onTextColorChange) = rememberPreference(AppTextColorKey, defaultValue = 0)
    val backgroundColor = if (backgroundColorInt == 0) AppTheme.colorScheme.surfaceContainerHigh else Color(backgroundColorInt)
    val appTextColor = if (textColorInt == 0) AppTheme.colorScheme.onSurface else Color(textColorInt)
    var showBackgroundColorPicker by rememberSaveable { mutableStateOf(false) }
    var showAppTextColorPicker by rememberSaveable { mutableStateOf(false) }

    if (showBackgroundColorPicker) {
        ColorPickerDialog(
            initialColor = backgroundColor,
            title = stringResource(R.string.background_color),
            onDismiss = { showBackgroundColorPicker = false },
            onConfirm = {
                onBackgroundColorChange(it.toArgb())
                showBackgroundColorPicker = false
            },
        )
    }

    if (showAppTextColorPicker) {
        ColorPickerDialog(
            initialColor = appTextColor,
            title = stringResource(R.string.text_color),
            onDismiss = { showAppTextColorPicker = false },
            onConfirm = {
                onTextColorChange(it.toArgb())
                showAppTextColorPicker = false
            },
        )
    }

    if (showThumbnailCornerRadiusDialog) {
        ThumbnailCornerRadiusModal(
            initialRadius = thumbnailCornerRadius,
            onDismiss = { showThumbnailCornerRadiusDialog = false },
            onRadiusSelected = { radius ->
                onThumbnailCornerRadiusChange(radius)
                showThumbnailCornerRadiusDialog = false
            }
        )
    }

    if (showSheetCornerRadiusDialog) {
        SheetCornerRadiusModal(
            initialRadius = sheetCornerRadius,
            onDismiss = { showSheetCornerRadiusDialog = false },
            onRadiusSelected = { radius ->
                onSheetCornerRadiusChange(radius)
                showSheetCornerRadiusDialog = false
            }
        )
    }

    if (showTopBarBlurDialog) {
        EnumDialog(
            onDismiss = { showTopBarBlurDialog = false },
            onSelect = {
                onTopBarBlurStyleChange(it)
                showTopBarBlurDialog = false
            },
            title = stringResource(R.string.top_bar_blur_style),
            current = topBarBlurStyle,
            values = TopBarBlurStyle.entries,
            valueText = {
                stringResource(
                    when (it) {
                        TopBarBlurStyle.SINGLE_PASS -> R.string.top_bar_blur_single_pass
                        TopBarBlurStyle.HAZE_PROGRESSIVE -> R.string.top_bar_blur_haze_progressive
                    },
                )
            },
        )
    }

    if (showDefaultChipDialog) {
        EnumDialog(
            onDismiss = { showDefaultChipDialog = false },
            onSelect = {
                onDefaultChipChange(it)
                showDefaultChipDialog = false
            },
            title = stringResource(R.string.default_lib_chips),
            current = defaultChip,
            values = LibraryFilter.entries.filter { it != LibraryFilter.ARTISTS },
            valueText = {
                when (it) {
                    LibraryFilter.SONGS -> stringResource(R.string.songs)
                    LibraryFilter.ARTISTS -> stringResource(R.string.artists)
                    LibraryFilter.ALBUMS -> stringResource(R.string.albums)
                    LibraryFilter.PLAYLISTS -> stringResource(R.string.playlists)
                    LibraryFilter.LIBRARY -> stringResource(R.string.filter_library)
                    LibraryFilter.LOCAL -> stringResource(R.string.filter_local)
                }
            }
        )
    }

    if (showGridSizeDialog) {
        EnumDialog(
            onDismiss = { showGridSizeDialog = false },
            onSelect = {
                onGridItemSizeChange(it)
                showGridSizeDialog = false
            },
            title = stringResource(R.string.grid_cell_size),
            current = gridItemSize,
            values = GridItemSize.entries,
            valueText = {
                when (it) {
                    GridItemSize.BIG -> stringResource(R.string.big)
                    GridItemSize.SMALL -> stringResource(R.string.small)
                }
            }
        )
    }

    if (showDensityScaleDialog) {
        EnumDialog(
            onDismiss = { showDensityScaleDialog = false },
            onSelect = {
                onDensityScaleChange(it.value)
                showDensityScaleDialog = false
            },
            title = stringResource(R.string.display_density),
            current = DensityScale.fromValue(densityScale),
            values = DensityScale.entries,
            valueText = { it.label }
        )
    }

    if (showRestartDialog) {
        DefaultDialog(
            onDismiss = { showRestartDialog = false },
            buttons = {
                TextButton(onClick = { showRestartDialog = false }) { Text(stringResource(android.R.string.cancel)) }
                TextButton(onClick = {
                    showRestartDialog = false
                    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }) { Text(stringResource(R.string.restart)) }
            }
        ) {
            Text(
                text = stringResource(R.string.density_restart_message),
                style = AppTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.advanced),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        item(key = "appearance_advanced_settings_content") {
            Column {
                IosSettingsGroup(
                    title = stringResource(R.string.theme_colors),
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.pure_black)) },
                            description = { Text(stringResource(R.string.pure_black_desc)) },
                            trailingContent = {
                                Switch(checked = pureBlack, onCheckedChange = onPureBlackChange)
                            },
                            onClick = { onPureBlackChange(!pureBlack) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = stringResource(R.string.app_background_text_color),
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.palette),
                            title = { Text(stringResource(R.string.background_color)) },
                            description = { Text(stringResource(R.string.app_background_text_color_desc)) },
                            trailingContent = { AdvancedColorSwatch(backgroundColor) },
                            onClick = { showBackgroundColorPicker = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.palette),
                            title = { Text(stringResource(R.string.text_color)) },
                            trailingContent = { AdvancedColorSwatch(appTextColor) },
                            onClick = { showAppTextColorPicker = true }
                        ),
                        // One click back to the design system: clears both
                        // overrides (0 = system semantic) so light/dark, glass
                        // chrome and materials all follow IosTheme again.
                        IosSettingsItem(
                            icon = painterResource(R.drawable.refresh),
                            title = { Text(stringResource(R.string.use_system_colors)) },
                            onClick = {
                                onBackgroundColorChange(0)
                                onTextColorChange(0)
                            }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Layout & behaviour",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tune),
                            title = { Text(stringResource(R.string.ios_overscroll)) },
                            description = { Text(stringResource(R.string.ios_overscroll_desc)) },
                            trailingContent = {
                                Switch(checked = iosOverscroll, onCheckedChange = onIosOverscrollChange)
                            },
                            onClick = { onIosOverscrollChange(!iosOverscroll) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.settings),
                            title = { Text(stringResource(R.string.enable_settings_popup)) },
                            description = { Text(stringResource(R.string.enable_settings_popup_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = enableSettingsPopup,
                                    onCheckedChange = onEnableSettingsPopupChange
                                )
                            },
                            onClick = { onEnableSettingsPopupChange(!enableSettingsPopup) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.library_icons_only)) },
                            description = { Text(stringResource(R.string.library_icons_only_desc)) },
                            trailingContent = {
                                Switch(checked = libraryIconsOnly, onCheckedChange = onLibraryIconsOnlyChange)
                            },
                            onClick = { onLibraryIconsOnlyChange(!libraryIconsOnly) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Navigation tabs",
                    items = optionalTabs.map { tab ->
                        val visible = tab.route !in hiddenTabs
                        IosSettingsItem(
                            icon = painterResource(tab.icon(false)),
                            title = { Text(stringResource(tab.titleId)) },
                            trailingContent = {
                                Switch(checked = visible, onCheckedChange = { onTabVisibilityChange(tab, it) })
                            },
                            onClick = { onTabVisibilityChange(tab, !visible) },
                        )
                    },
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Player",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.hide_image),
                            title = { Text(stringResource(R.string.hide_player_thumbnail)) },
                            description = { Text(stringResource(R.string.hide_player_thumbnail_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = hidePlayerThumbnail,
                                    onCheckedChange = onHidePlayerThumbnailChange
                                )
                            },
                            onClick = { onHidePlayerThumbnailChange(!hidePlayerThumbnail) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.image),
                            title = { Text(stringResource(R.string.thumbnail_corner_radius)) },
                            description = { Text(stringResource(R.string.thumbnail_corner_radius_desc)) },
                            trailingContent = {
                                Text(
                                    text = "${thumbnailCornerRadius.roundToInt()}dp",
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = { showThumbnailCornerRadiusDialog = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tune),
                            title = { Text(stringResource(R.string.sheet_corner_radius)) },
                            trailingContent = {
                                Text(
                                    text = "${sheetCornerRadius.roundToInt()}dp",
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = { showSheetCornerRadiusDialog = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tune),
                            title = { Text(stringResource(R.string.show_audio_quality_badge)) },
                            trailingContent = {
                                Switch(
                                    checked = showAudioQualityBadge,
                                    onCheckedChange = onShowAudioQualityBadgeChange
                                )
                            },
                            onClick = { onShowAudioQualityBadgeChange(!showAudioQualityBadge) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.nav_bar),
                            title = { Text(stringResource(R.string.mini_bar_tab_style)) },
                            description = { Text(stringResource(R.string.mini_bar_tab_style_desc)) },
                            trailingContent = {
                                Switch(checked = miniBarTabStyle, onCheckedChange = onMiniBarTabStyleChange)
                            },
                            onClick = { onMiniBarTabStyleChange(!miniBarTabStyle) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.nav_bar),
                            title = { Text(stringResource(R.string.nav_bar_melt_transition)) },
                            description = { Text(stringResource(R.string.nav_bar_melt_transition_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = navBarMeltTransition,
                                    onCheckedChange = onNavBarMeltTransitionChange
                                )
                            },
                            onClick = { onNavBarMeltTransitionChange(!navBarMeltTransition) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.chat_msg),
                            title = { Text(stringResource(R.string.show_comment_button)) },
                            description = { Text(stringResource(R.string.show_comment_button_description)) },
                            trailingContent = {
                                Switch(checked = showCommentButton, onCheckedChange = onShowCommentButtonChange)
                            },
                            onClick = { onShowCommentButtonChange(!showCommentButton) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Lyrics effects",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.play),
                            title = { Text(stringResource(R.string.lyrics_thumbnail_play_pause)) },
                            description = { Text(stringResource(R.string.lyrics_thumbnail_play_pause_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = enableLyricsThumbnailPlayPause,
                                    onCheckedChange = onEnableLyricsThumbnailPlayPauseChange
                                )
                            },
                            onClick = { onEnableLyricsThumbnailPlayPauseChange(!enableLyricsThumbnailPlayPause) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.expand_more),
                            title = { Text(stringResource(R.string.fullscreen_lyrics_collapse_top)) },
                            description = { Text(stringResource(R.string.fullscreen_lyrics_collapse_top_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = fullscreenLyricsCollapseTop,
                                    onCheckedChange = onFullscreenLyricsCollapseTopChange
                                )
                            },
                            onClick = { onFullscreenLyricsCollapseTopChange(!fullscreenLyricsCollapseTop) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Other",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tab),
                            title = { Text(stringResource(R.string.default_lib_chips)) },
                            description = {
                                Text(
                                    when (defaultChip) {
                                        LibraryFilter.SONGS -> stringResource(R.string.songs)
                                        LibraryFilter.ARTISTS -> stringResource(R.string.artists)
                                        LibraryFilter.ALBUMS -> stringResource(R.string.albums)
                                        LibraryFilter.PLAYLISTS -> stringResource(R.string.playlists)
                                        LibraryFilter.LIBRARY -> stringResource(R.string.filter_library)
                    LibraryFilter.LOCAL -> stringResource(R.string.filter_local)
                                    }
                                )
                            },
                            onClick = { showDefaultChipDialog = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.grid_cell_size)) },
                            description = {
                                Text(
                                    when (gridItemSize) {
                                        GridItemSize.BIG -> stringResource(R.string.big)
                                        GridItemSize.SMALL -> stringResource(R.string.small)
                                    }
                                )
                            },
                            onClick = { showGridSizeDialog = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.display_density)) },
                            description = { Text(DensityScale.fromValue(densityScale).label) },
                            onClick = { showDensityScaleDialog = true }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.album),
                            title = { Text(stringResource(R.string.local_albums_by_year)) },
                            description = { Text(stringResource(R.string.local_albums_by_year_desc)) },
                            trailingContent = {
                                Switch(checked = localAlbumsByYear, onCheckedChange = onLocalAlbumsByYearChange)
                            },
                            onClick = { onLocalAlbumsByYearChange(!localAlbumsByYear) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = stringResource(R.string.grid_and_cards),
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.remove),
                            title = { Text(stringResource(R.string.reduce_dynamic_blur)) },
                            description = { Text(stringResource(R.string.reduce_dynamic_blur_desc)) },
                            trailingContent = {
                                Switch(checked = reduceDynamicBlur, onCheckedChange = onReduceDynamicBlurChange)
                            },
                            onClick = { onReduceDynamicBlurChange(!reduceDynamicBlur) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.top_bar_blur_style)) },
                            description = {
                                Text(stringResource(
                                    when (topBarBlurStyle) {
                                        TopBarBlurStyle.SINGLE_PASS -> R.string.top_bar_blur_single_pass
                                        TopBarBlurStyle.HAZE_PROGRESSIVE -> R.string.top_bar_blur_haze_progressive
                                    },
                                ))
                            },
                            onClick = { showTopBarBlurDialog = true },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.mic),
                            title = { Text(stringResource(R.string.show_home_fab)) },
                            description = { Text(stringResource(R.string.show_home_fab_desc)) },
                            trailingContent = {
                                Switch(checked = showHomeFab, onCheckedChange = onShowHomeFabChange)
                            },
                            onClick = { onShowHomeFabChange(!showHomeFab) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.favorite_border),
                            title = { Text(stringResource(R.string.hide_home_favorite_icon)) },
                            description = { Text(stringResource(R.string.hide_home_favorite_icon_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = hideHomeFavoriteIcon,
                                    onCheckedChange = onHideHomeFavoriteIconChange
                                )
                            },
                            onClick = { onHideHomeFavoriteIconChange(!hideHomeFavoriteIcon) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.grid_columns)) },
                            description = {
                                Column {
                                    Text(
                                        text = if (gridColumnsOverride == 0) stringResource(R.string.auto)
                                        else gridColumnsOverride.toString()
                                    )
                                    Slider(
                                        value = GridColumnChoices.indexOf(gridColumnsOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onGridColumnsOverrideChange(GridColumnChoices[it.roundToInt()])
                                        },
                                        steps = GridColumnChoices.size - 2,
                                        valueRange = 0f..(GridColumnChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.grid_card_height)) },
                            description = {
                                Column {
                                    Text(
                                        text = if (gridCardHeightOverride == 0) stringResource(R.string.auto)
                                        else "${gridCardHeightOverride}dp"
                                    )
                                    Slider(
                                        value = GridCardHeightChoices.indexOf(gridCardHeightOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onGridCardHeightOverrideChange(GridCardHeightChoices[it.roundToInt()])
                                        },
                                        steps = GridCardHeightChoices.size - 2,
                                        valueRange = 0f..(GridCardHeightChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.grid_spacing)) },
                            description = {
                                Column {
                                    Text(text = "${gridSpacing}dp")
                                    Slider(
                                        value = GridSpacingChoices.indexOf(gridSpacing).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onGridSpacingChange(GridSpacingChoices[it.roundToInt()])
                                        },
                                        steps = GridSpacingChoices.size - 2,
                                        valueRange = 0f..(GridSpacingChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.speed_dial_columns)) },
                            description = {
                                val speedDialChoices = remember { listOf(0, 3, 4, 5, 6) }
                                Column {
                                    Text(
                                        text = if (speedDialColumnsOverride == 0) stringResource(R.string.auto)
                                        else speedDialColumnsOverride.toString()
                                    )
                                    Slider(
                                        value = speedDialChoices.indexOf(speedDialColumnsOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onSpeedDialColumnsOverrideChange(speedDialChoices[it.roundToInt()])
                                        },
                                        steps = speedDialChoices.size - 2,
                                        valueRange = 0f..(speedDialChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.home_grid_columns)) },
                            description = {
                                val homeGridChoices = remember { listOf(0, 2, 3, 4) }
                                Column {
                                    Text(
                                        text = if (homeGridColumnsOverride == 0) stringResource(R.string.auto)
                                        else homeGridColumnsOverride.toString()
                                    )
                                    Slider(
                                        value = homeGridChoices.indexOf(homeGridColumnsOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onHomeGridColumnsOverrideChange(homeGridChoices[it.roundToInt()])
                                        },
                                        steps = homeGridChoices.size - 2,
                                        valueRange = 0f..(homeGridChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.home_hero_card_height)) },
                            description = {
                                Column {
                                    Text(
                                        text = if (homeHeroCardHeightOverride == 0) stringResource(R.string.auto)
                                        else "${homeHeroCardHeightOverride}dp"
                                    )
                                    Slider(
                                        value = HomeHeroCardHeightChoices.indexOf(homeHeroCardHeightOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onHomeHeroCardHeightOverrideChange(HomeHeroCardHeightChoices[it.roundToInt()])
                                        },
                                        steps = HomeHeroCardHeightChoices.size - 2,
                                        valueRange = 0f..(HomeHeroCardHeightChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.speed_dial_card_height)) },
                            description = {
                                Column {
                                    Text(
                                        text = if (speedDialCardHeightOverride == 0) stringResource(R.string.auto)
                                        else "${speedDialCardHeightOverride}dp"
                                    )
                                    Slider(
                                        value = GridCardHeightChoices.indexOf(speedDialCardHeightOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onSpeedDialCardHeightOverrideChange(GridCardHeightChoices[it.roundToInt()])
                                        },
                                        steps = GridCardHeightChoices.size - 2,
                                        valueRange = 0f..(GridCardHeightChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.grid_view),
                            title = { Text(stringResource(R.string.home_card_corner_radius)) },
                            description = {
                                Column {
                                    Text(
                                        text = if (homeCardCornerRadiusOverride == 0) stringResource(R.string.auto)
                                        else "${homeCardCornerRadiusOverride}dp"
                                    )
                                    Slider(
                                        value = HomeCardCornerRadiusChoices.indexOf(homeCardCornerRadiusOverride).coerceAtLeast(0).toFloat(),
                                        onValueChange = {
                                            onHomeCardCornerRadiusOverrideChange(HomeCardCornerRadiusChoices[it.roundToInt()])
                                        },
                                        steps = HomeCardCornerRadiusChoices.size - 2,
                                        valueRange = 0f..(HomeCardCornerRadiusChoices.size - 1).toFloat(),
                                    )
                                }
                            },
                        ),
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    
    }
}

/** Small current-color preview for a color-picker settings row's trailing content. */
@Composable
private fun AdvancedColorSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, AppTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
    )
}
