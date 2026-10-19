/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.ModalBottomSheet
import com.convx.music.ui.ios.compat.rememberIosModalSheetState
import com.convx.music.ui.ios.foundation.IosTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.app.Activity
import android.os.Build
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import android.content.Context
import androidx.core.content.edit
import com.convx.music.ui.utils.bounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.layout.layout
import com.convx.music.ui.component.shapes.ContinuousCapsule
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Switch
import androidx.compose.material3.SwitchDefaults
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFGaugeWithNeedle
import com.composables.sfsymbols.monochrome.SFIcloudAndArrowDownFill
import com.composables.sfsymbols.monochrome.SFSliderHorizontal3
import com.composables.sfsymbols.monochrome.SFStar
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.constants.CropAlbumArtKey
import com.convx.music.constants.DefaultOpenTabKey
import com.convx.music.constants.DynamicThemeKey
import com.convx.music.constants.EnableHighRefreshRateKey
import com.convx.music.constants.OneTapFullscreenLyricsKey
import com.convx.music.constants.HomeHeroCardEnabledKey
import com.convx.music.constants.PureBlackHeroBackgroundKey
import com.convx.music.constants.LiquidGlassGlobalEnabledKey
import com.convx.music.constants.AlbumCanvasEnabledKey
import com.convx.music.constants.AppLanguageKey
import com.convx.music.constants.LanguageCodeToName
import com.convx.music.constants.SYSTEM_DEFAULT
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.constants.ListenTogetherInTopBarKey
import com.convx.music.constants.LyricsAnimationStyle
import com.convx.music.constants.LyricsAnimationStyleKey
import com.convx.music.constants.LyricsTextPositionKey
import com.convx.music.constants.LyricsTextSizeKey
import com.convx.music.constants.ShowCachedPlaylistKey
import com.convx.music.constants.ShowDownloadedPlaylistKey
import com.convx.music.constants.ShowLikedPlaylistKey
import com.convx.music.constants.ShowTopPlaylistKey
import com.convx.music.constants.ShowUploadedPlaylistKey
import com.convx.music.constants.SlimNavBarKey
import com.convx.music.constants.SwipeSensitivityKey
import com.convx.music.constants.SwipeThumbnailKey
import com.convx.music.constants.SwipeLyricsKey
import com.convx.music.constants.SwipeToRemoveSongKey
import com.convx.music.constants.SwipeToSongKey
import com.convx.music.constants.UseNewMiniPlayerDesignKey
import com.convx.music.constants.UseNewPlayerDesignKey
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlin.math.roundToInt
import com.convx.music.constants.LyricsClickKey
import com.convx.music.constants.LyricsLineSpacingKey
import com.convx.music.constants.LyricsScrollKey
import com.convx.music.constants.MiniPlayerWaveformKey
import com.convx.music.constants.AppFont
import com.convx.music.constants.SelectedFontKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState,
) {
    val (_, _) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
    val (_, _) = rememberPreference(DynamicThemeKey, defaultValue = true)
    val (useNewPlayerDesign, _) = rememberPreference(UseNewPlayerDesignKey, defaultValue = false)
    val (miniPlayerWaveform, onMiniPlayerWaveformChange) = rememberPreference(
        MiniPlayerWaveformKey,
        defaultValue = false
    )
    val (selectedFontValue) = rememberPreference(
        SelectedFontKey,
        defaultValue = AppFont.PLUS_JAKARTA_SANS.value
    )

    val (enableHighRefreshRate, onEnableHighRefreshRateChange) = rememberPreference(
        EnableHighRefreshRateKey,
        defaultValue = true
    )
    val (cropAlbumArt, onCropAlbumArtChange) = rememberPreference(
        CropAlbumArtKey,
        defaultValue = false
    )
    val (defaultOpenTab, onDefaultOpenTabChange) = rememberEnumPreference(
        DefaultOpenTabKey,
        defaultValue = NavigationTab.HOME
    )
    val (lyricsPosition, onLyricsPositionChange) = rememberEnumPreference(
        LyricsTextPositionKey,
        defaultValue = LyricsPosition.LEFT
    )
    val (lyricsClick, onLyricsClickChange) = rememberPreference(LyricsClickKey, defaultValue = true)
    val (lyricsScroll, onLyricsScrollChange) = rememberPreference(
        LyricsScrollKey,
        defaultValue = true
    )
    val (lyricsAnimationStyle, onLyricsAnimationStyleChange) = rememberEnumPreference(
        LyricsAnimationStyleKey,
        defaultValue = LyricsAnimationStyle.FLAMINGO
    )
    val (lyricsTextSize, onLyricsTextSizeChange) = rememberPreference(LyricsTextSizeKey, defaultValue = 30f)
    val (lyricsLineSpacing, onLyricsLineSpacingChange) = rememberPreference(LyricsLineSpacingKey, defaultValue = 1.3f)
    val (swipeLyrics, onSwipeLyricsChange) = rememberPreference(SwipeLyricsKey, defaultValue = false)
    val (oneTapFullscreenLyrics, onOneTapFullscreenLyricsChange) = rememberPreference(OneTapFullscreenLyricsKey, defaultValue = false)

    val (swipeThumbnail, onSwipeThumbnailChange) = rememberPreference(
        SwipeThumbnailKey,
        defaultValue = true
    )
    val (swipeSensitivity, onSwipeSensitivityChange) = rememberPreference(
        SwipeSensitivityKey,
        defaultValue = 0.73f
    )
    val (canvasThumbnailAnimation, onCanvasThumbnailAnimationChange) = rememberPreference(
        CanvasThumbnailAnimationKey,
        defaultValue = true
    )
    val (canvasSource) = rememberEnumPreference(
        CanvasSourceKey,
        defaultValue = CanvasSource.AUTO
    )
    val (pureBlackHeroBackground, onPureBlackHeroBackgroundChange) = rememberPreference(PureBlackHeroBackgroundKey, false)
    val (liquidGlassEnabled, onLiquidGlassEnabledChange) = rememberPreference(
        LiquidGlassGlobalEnabledKey,
        defaultValue = true
    )
    // Keep album canvas controls with the other player display settings.
    val (albumCanvasEnabled, onAlbumCanvasEnabledChange) = rememberPreference(
        AlbumCanvasEnabledKey,
        defaultValue = false
    )
    // App language is a general setting, so keep it in the miscellaneous group.
    val (appLanguage, onAppLanguageChange) = rememberPreference(key = AppLanguageKey, defaultValue = SYSTEM_DEFAULT)
    var showAppLanguageDialog by rememberSaveable { mutableStateOf(false) }
    val (homeHeroCardEnabled, onHomeHeroCardEnabledChange) = rememberPreference(HomeHeroCardEnabledKey, true)

    val (listenTogetherInTopBar, onListenTogetherInTopBarChange) = rememberPreference(
        ListenTogetherInTopBarKey,
        defaultValue = true
    )

    val (swipeToSong, onSwipeToSongChange) = rememberPreference(
        SwipeToSongKey,
        defaultValue = false
    )

    val (swipeToRemoveSong, onSwipeToRemoveSongChange) = rememberPreference(
        SwipeToRemoveSongKey,
        defaultValue = false
    )

    val (showLikedPlaylist, onShowLikedPlaylistChange) = rememberPreference(
        ShowLikedPlaylistKey,
        defaultValue = true
    )
    val (showDownloadedPlaylist, onShowDownloadedPlaylistChange) = rememberPreference(
        ShowDownloadedPlaylistKey,
        defaultValue = true
    )
    val (showTopPlaylist, onShowTopPlaylistChange) = rememberPreference(
        ShowTopPlaylistKey,
        defaultValue = true
    )
    val (showCachedPlaylist, onShowCachedPlaylistChange) = rememberPreference(
        ShowCachedPlaylistKey,
        defaultValue = true
    )
    val (showUploadedPlaylist, onShowUploadedPlaylistChange) = rememberPreference(
        ShowUploadedPlaylistKey,
        defaultValue = true
    )
    var showLyricsPositionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showLyricsAnimationStyleDialog by rememberSaveable {
        mutableStateOf(SettingsDeepLink.consumeLyricsSheet())
    }

    var showLyricsTextSizeDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showLyricsLineSpacingDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showLyricsPositionDialog) {
        EnumDialog(
            onDismiss = { showLyricsPositionDialog = false },
            onSelect = {
                onLyricsPositionChange(it)
                showLyricsPositionDialog = false
            },
            title = stringResource(R.string.lyrics_text_position),
            current = lyricsPosition,
            values = LyricsPosition.entries,
            valueText = {
                when (it) {
                    LyricsPosition.LEFT -> stringResource(R.string.left)
                    LyricsPosition.CENTER -> stringResource(R.string.center)
                    LyricsPosition.RIGHT -> stringResource(R.string.right)
                }
            }
        )
    }

    LaunchedEffect(showLyricsAnimationStyleDialog) {
        if (showLyricsAnimationStyleDialog) {
            showLyricsAnimationStyleDialog = false
            SettingsSheets.open = SettingsAction.LyricsStyle
        }
    }

    var showDefaultOpenTabDialog by rememberSaveable { mutableStateOf(false) }
    if (showDefaultOpenTabDialog) {
        EnumDialog(
            onDismiss = { showDefaultOpenTabDialog = false },
            onSelect = {
                onDefaultOpenTabChange(it)
                showDefaultOpenTabDialog = false
            },
            title = stringResource(R.string.default_open_tab),
            current = defaultOpenTab,
            values = NavigationTab.entries,
            valueText = {
                when (it) {
                    NavigationTab.HOME -> stringResource(R.string.home)
                    NavigationTab.SEARCH -> stringResource(R.string.search)
                    NavigationTab.LIBRARY -> stringResource(R.string.filter_library)
                }
            }
        )
    }

    if (showAppLanguageDialog) {
        EnumDialog(
            onDismiss = { showAppLanguageDialog = false },
            onSelect = {
                onAppLanguageChange(it)
                showAppLanguageDialog = false
            },
            title = stringResource(R.string.app_language),
            current = appLanguage,
            values = (listOf(SYSTEM_DEFAULT) + LanguageCodeToName.keys.toList()),
            valueText = {
                LanguageCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
            }
        )
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.appearance),
        pageColor = IosTheme.colors.systemGroupedBackground,
        horizontalPadding = 16.dp,
    ) {
        item(key = "all_appearance_settings") {
            Column {
        IosSettingsGroup(
            title = "Theme & colors",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.theme_colors)) },
                    onClick = { navController.navigate("settings/appearance/theme") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.presets)) },
                    description = { Text(stringResource(R.string.presets_desc)) },
                    onClick = { navController.navigate("settings/appearance/presets") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.edit),
                    title = { Text(stringResource(R.string.diy)) },
                    description = { Text(stringResource(R.string.diy_desc)) },
                    onClick = { navController.navigate("settings/appearance/diy") }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "App identity",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.alphabet_cyrillic),
                    title = { Text(stringResource(R.string.app_font)) },
                    description = {
                        val fontLabel = when (AppFont.fromValue(selectedFontValue)) {
                            AppFont.SYSTEM -> stringResource(R.string.font_system)
                            AppFont.GOOGLE_SANS -> stringResource(R.string.font_google_sans)
                            AppFont.SANS_FLEX -> stringResource(R.string.font_sans_flex)
                            AppFont.OUTFIT -> stringResource(R.string.font_outfit)
                            AppFont.PLUS_JAKARTA_SANS -> stringResource(R.string.font_plus_jakarta_sans)
                        }
                        Text(fontLabel)
                    },
                    onClick = { navController.navigate("settings/appearance/font") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.convx_logo),
                    title = { Text(stringResource(R.string.app_icon)) },
                    description = { Text(stringResource(R.string.app_icon_desc)) },
                    onClick = { navController.navigate("settings/appearance/appicon") }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.liquid_glass),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass)) },
                    description = { Text(stringResource(R.string.liquid_glass_simple_desc)) },
                    trailingContent = {
                        Switch(
                            checked = liquidGlassEnabled,
                            onCheckedChange = onLiquidGlassEnabledChange
                        )
                    },
                    onClick = { onLiquidGlassEnabledChange(!liquidGlassEnabled) }
                ),
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFSliderHorizontal3),
                    title = { Text(stringResource(R.string.advanced)) },
                    description = { Text(stringResource(R.string.advanced_settings_desc)) },
                    onClick = { navController.navigate("settings/appearance/liquidglass") }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "Display",
            items = buildList {
                add(
                    IosSettingsItem(
                        icon = rememberVectorPainter(SfSymbols.Monochrome.SFGaugeWithNeedle),
                        title = { Text(stringResource(R.string.enable_high_refresh_rate)) },
                        description = { Text(stringResource(R.string.enable_high_refresh_rate_desc)) },
                        trailingContent = {
                            Switch(
                                checked = enableHighRefreshRate,
                                onCheckedChange = onEnableHighRefreshRateChange
                            )
                        },
                        onClick = { onEnableHighRefreshRateChange(!enableHighRefreshRate) }
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "Player & canvas",
            items = listOfNotNull(
                IosSettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.player_icons)) },
                    description = { Text(stringResource(R.string.player_icons_desc)) },
                    onClick = { navController.navigate("settings/appearance/playericons") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.gradient),
                    title = { Text(stringResource(R.string.player_theme)) },
                    description = { Text(stringResource(R.string.player_theme_desc)) },
                    onClick = { navController.navigate("settings/appearance/playertheme") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.crop),
                    title = { Text(stringResource(R.string.crop_album_art)) },
                    description = { Text(stringResource(R.string.crop_album_art_desc)) },
                    trailingContent = {
                        Switch(
                            checked = cropAlbumArt,
                            onCheckedChange = onCropAlbumArtChange
                        )
                    },
                    onClick = { onCropAlbumArtChange(!cropAlbumArt) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.mini_player_waveform)) },
                    trailingContent = {
                        Switch(
                            checked = miniPlayerWaveform,
                            onCheckedChange = onMiniPlayerWaveformChange
                        )
                    },
                    onClick = { onMiniPlayerWaveformChange(!miniPlayerWaveform) }
                ),
                    IosSettingsItem(
                        icon = painterResource(R.drawable.canvas_art),
                        title = { Text(stringResource(R.string.convx_canvas)) },
                    description = {
                        val summary = if (!canvasThumbnailAnimation) {
                            stringResource(R.string.disable)
                        } else {
                            when (canvasSource) {
                                CanvasSource.AUTO -> stringResource(R.string.canvas_source_auto)
                                CanvasSource.APPLE_MUSIC -> stringResource(R.string.canvas_source_apple_music)
                                CanvasSource.CONVX -> stringResource(R.string.canvas_source_convx)
                                CanvasSource.TIDAL -> stringResource(R.string.canvas_source_tidal)
                                CanvasSource.SPOTIFY -> stringResource(R.string.canvas_source_spotify)
                            }
                        }
                        Text(summary)
                    },
                    onClick = { navController.navigate("settings/appearance/canvas") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.show_album_canvas)) },
                    description = { Text(stringResource(R.string.show_album_canvas_desc)) },
                    trailingContent = {
                        Switch(
                            checked = albumCanvasEnabled,
                            onCheckedChange = onAlbumCanvasEnabledChange
                        )
                    },
                    onClick = { onAlbumCanvasEnabledChange(!albumCanvasEnabled) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "Lyrics layout",
            items = listOfNotNull(
                IosSettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_text_position)) },
                    description = {
                        Text(
                            when (lyricsPosition) {
                                LyricsPosition.LEFT -> stringResource(R.string.left)
                                LyricsPosition.CENTER -> stringResource(R.string.center)
                                LyricsPosition.RIGHT -> stringResource(R.string.right)
                            }
                        )
                    },
                    onClick = { showLyricsPositionDialog = true }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_animation_style)) },
                    description = {
                        Text(
                            com.convx.music.ui.component.lyricsStyleLabel(lyricsAnimationStyle)
                        )
                    },
                    onClick = { showLyricsAnimationStyleDialog = true }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_auto_scroll)) },
                    trailingContent = {
                        Switch(
                            checked = lyricsScroll,
                            onCheckedChange = onLyricsScrollChange
                        )
                    },
                    onClick = { onLyricsScrollChange(!lyricsScroll) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.lyrics_swipe_to_change_song)) },
                    description = { Text(stringResource(R.string.lyrics_swipe_to_change_song_desc)) },
                    trailingContent = {
                        Switch(
                            checked = swipeLyrics,
                            onCheckedChange = onSwipeLyricsChange
                        )
                    },
                    onClick = { onSwipeLyricsChange(!swipeLyrics) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.fullscreen),
                    title = { Text(stringResource(R.string.one_tap_fullscreen_lyrics)) },
                    description = { Text(stringResource(R.string.one_tap_fullscreen_lyrics_desc)) },
                    trailingContent = {
                        Switch(
                            checked = oneTapFullscreenLyrics,
                            onCheckedChange = onOneTapFullscreenLyricsChange
                        )
                    },
                    onClick = { onOneTapFullscreenLyricsChange(!oneTapFullscreenLyrics) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "More",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.nav_bar),
                    title = { Text(stringResource(R.string.default_open_tab)) },
                    description = {
                        Text(
                            when (defaultOpenTab) {
                                NavigationTab.HOME -> stringResource(R.string.home)
                                NavigationTab.SEARCH -> stringResource(R.string.search)
                                NavigationTab.LIBRARY -> stringResource(R.string.filter_library)
                            }
                        )
                    },
                    onClick = { showDefaultOpenTabDialog = true }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.swipe_song_to_add)) },
                    trailingContent = {
                        Switch(
                            checked = swipeToSong,
                            onCheckedChange = onSwipeToSongChange
                        )
                    },
                    onClick = { onSwipeToSongChange(!swipeToSong) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.swipe_song_to_remove)) },
                    trailingContent = {
                        Switch(
                            checked = swipeToRemoveSong,
                            onCheckedChange = onSwipeToRemoveSongChange
                        )
                    },
                    onClick = { onSwipeToRemoveSongChange(!swipeToRemoveSong) }
                ),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    IosSettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.app_language)) },
                        onClick = {
                            activity.startActivity(
                                Intent(
                                    Settings.ACTION_APP_LOCALE_SETTINGS,
                                    "package:${activity.packageName}".toUri()
                                )
                            )
                        }
                    )
                } else {
                    IosSettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.app_language)) },
                        description = {
                            Text(
                                LanguageCodeToName.getOrElse(appLanguage) { stringResource(R.string.system_default) }
                            )
                        },
                        onClick = { showAppLanguageDialog = true }
                    )
                }
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.grid_and_cards),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.pure_black_hero_background)) },
                    description = { Text(stringResource(R.string.pure_black_hero_background_desc)) },
                    trailingContent = {
                        Switch(
                            checked = pureBlackHeroBackground,
                            onCheckedChange = onPureBlackHeroBackgroundChange
                        )
                    },
                    onClick = { onPureBlackHeroBackgroundChange(!pureBlackHeroBackground) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.home_hero_card)) },
                    description = { Text(stringResource(R.string.home_hero_card_desc)) },
                    trailingContent = {
                        Switch(
                            checked = homeHeroCardEnabled,
                            onCheckedChange = onHomeHeroCardEnabledChange,
                        )
                    },
                    onClick = { onHomeHeroCardEnabledChange(!homeHeroCardEnabled) },
                ),
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.auto_playlists),
            items = listOf(
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFStar),
                    title = { Text(stringResource(R.string.show_liked_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showLikedPlaylist,
                            onCheckedChange = onShowLikedPlaylistChange
                        )
                    },
                    onClick = { onShowLikedPlaylistChange(!showLikedPlaylist) }
                ),
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFIcloudAndArrowDownFill),
                    title = { Text(stringResource(R.string.show_downloaded_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showDownloadedPlaylist,
                            onCheckedChange = onShowDownloadedPlaylistChange
                        )
                    },
                    onClick = { onShowDownloadedPlaylistChange(!showDownloadedPlaylist) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.trending_up),
                    title = { Text(stringResource(R.string.show_top_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showTopPlaylist,
                            onCheckedChange = onShowTopPlaylistChange
                        )
                    },
                    onClick = { onShowTopPlaylistChange(!showTopPlaylist) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.cached),
                    title = { Text(stringResource(R.string.show_cached_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showCachedPlaylist,
                            onCheckedChange = onShowCachedPlaylistChange
                        )
                    },
                    onClick = { onShowCachedPlaylistChange(!showCachedPlaylist) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.backup),
                    title = { Text(stringResource(R.string.show_uploaded_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showUploadedPlaylist,
                            onCheckedChange = onShowUploadedPlaylistChange
                        )
                    },
                    onClick = { onShowUploadedPlaylistChange(!showUploadedPlaylist) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.advanced),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.advanced)) },
                    description = { Text(stringResource(R.string.advanced_settings_desc)) },
                    onClick = { navController.navigate("settings/appearance/advanced") }
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
            }
        }
    
    }
}

enum class DarkMode {
    ON,
    OFF,
    AUTO,
}

enum class NavigationTab {
    HOME,
    SEARCH,
    LIBRARY,
}

enum class LyricsPosition {
    LEFT,
    CENTER,
    RIGHT,
}

enum class PlayerTextAlignment {
    SIDED,
    CENTER,
}

@Composable
fun ColumnScope.LyricsAnimationSheetContent() {
    val (selected, onSelect) = rememberEnumPreference(LyricsAnimationStyleKey, defaultValue = LyricsAnimationStyle.FLAMINGO)
    val (position, onPositionChange) = rememberEnumPreference(LyricsTextPositionKey, defaultValue = LyricsPosition.LEFT)
    val (autoScroll, onAutoScrollChange) = rememberPreference(LyricsScrollKey, defaultValue = true)
    val (swipeToChange, onSwipeToChangeChange) = rememberPreference(SwipeLyricsKey, defaultValue = false)
    val animation = androidx.compose.animation.core.rememberInfiniteTransition(label = "lyrics sample")
    val progress by animation.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.82f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1900),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "sample lyric progress",
    )
    val selectedColor by androidx.compose.animation.animateColorAsState(
        targetValue = IosTheme.tint,
        animationSpec = androidx.compose.animation.core.tween(220),
        label = "lyrics style accent",
    )

    val align = when (position) {
        LyricsPosition.LEFT -> androidx.compose.ui.text.style.TextAlign.Start
        LyricsPosition.CENTER -> androidx.compose.ui.text.style.TextAlign.Center
        LyricsPosition.RIGHT -> androidx.compose.ui.text.style.TextAlign.End
    }
    val fill = selected == LyricsAnimationStyle.FLAMINGO || selected == LyricsAnimationStyle.KARAOKE || selected == LyricsAnimationStyle.ARCHIVE_TUNE ||
        selected == LyricsAnimationStyle.APPLE || selected == LyricsAnimationStyle.APPLE_V2 ||
        selected == LyricsAnimationStyle.BITCHORD

    run {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Tap a style to preview and apply it",
                    style = IosTheme.typography.footnote,
                    color = IosTheme.colors.secondaryLabel,
                )
            }

            // The playing song's own lyrics, through the player's own lyrics view: what is
            // seen here is exactly what the player shows in the chosen style.
            val lyricsConnection = com.convx.music.LocalPlayerConnection.current
            val lyricsTrack by remember(lyricsConnection) {
                lyricsConnection?.mediaMetadata ?: kotlinx.coroutines.flow.MutableStateFlow(null)
            }.collectAsStateWithLifecycle()
            val lyricsPlaying by remember(lyricsConnection) {
                lyricsConnection?.isPlaying ?: kotlinx.coroutines.flow.MutableStateFlow(false)
            }.collectAsStateWithLifecycle()
            // Paused or not, the preview keeps singing: a clock carries on from where the song
            // stopped and loops at its end, so every style can be watched moving.
            var pausedClock by remember { androidx.compose.runtime.mutableLongStateOf(0L) }
            LaunchedEffect(lyricsPlaying, lyricsTrack?.id) {
                if (lyricsPlaying || lyricsConnection == null) return@LaunchedEffect
                val from = lyricsConnection.player.currentPosition.coerceAtLeast(0L)
                val length = ((lyricsTrack?.duration ?: 0) * 1000L).coerceAtLeast(30_000L)
                val startedAt = System.currentTimeMillis()
                while (true) {
                    androidx.compose.runtime.withFrameMillis { }
                    pausedClock = (from + System.currentTimeMillis() - startedAt) % length
                }
            }
            val previewClock: () -> Long = {
                if (lyricsPlaying) lyricsConnection?.player?.currentPosition ?: 0L else pausedClock
            }
            if (lyricsConnection != null && lyricsTrack != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(ContinuousRoundedRectangle(24.dp)),
                ) {
                    com.convx.music.ui.player.NowPlayingBackground(
                        artworkUrl = lyricsTrack?.thumbnailUrl,
                        hero = false,
                        modifier = Modifier.fillMaxSize(),
                    )
                    com.convx.music.ui.player.InlineLyricsView(
                        mediaMetadata = lyricsTrack,
                        showLyrics = true,
                        positionProvider = previewClock,
                    )
                    Text(
                        lyricsTrack?.title.orEmpty(),
                        color = Color.White.copy(alpha = 0.6f),
                        style = IosTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
                    )
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(ContinuousRoundedRectangle(24.dp))
                        .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(selectedColor.copy(alpha = 0.38f).compositeOver(Color(0xFF111217)), Color(0xFF111217)))),
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    ) {
                        Text(
                            "Where the light meets the sea",
                            color = Color.White.copy(alpha = 0.38f),
                            style = IosTheme.typography.subheadline,
                            textAlign = align,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "We find our way back home",
                            style = IosTheme.typography.title3.let {
                                when {
                                    fill -> it.copy(
                                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            0f to selectedColor,
                                            progress to selectedColor,
                                            (progress + 0.001f).coerceAtMost(1f) to Color.White.copy(alpha = 0.4f),
                                            1f to Color.White.copy(alpha = 0.4f),
                                        ),
                                    )
                                    selected == LyricsAnimationStyle.GLOW -> it.copy(
                                        color = selectedColor,
                                        shadow = androidx.compose.ui.graphics.Shadow(selectedColor, blurRadius = 8f + progress * 22f),
                                    )
                                    else -> it.copy(color = selectedColor)
                                }
                            },
                            fontWeight = FontWeight.Bold,
                            textAlign = align,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth().graphicsLayer {
                                when (selected) {
                                    LyricsAnimationStyle.NONE -> Unit
                                    LyricsAnimationStyle.FADE -> alpha = 0.45f + progress * 0.55f
                                    LyricsAnimationStyle.SLIDE -> {
                                        translationX = (1f - progress) * 28.dp.toPx() * if (position == LyricsPosition.RIGHT) -1f else 1f
                                        alpha = 0.5f + progress * 0.5f
                                    }
                                    else -> {
                                        scaleX = 0.94f + progress * 0.06f
                                        scaleY = scaleX
                                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(
                                            when (position) {
                                                LyricsPosition.LEFT -> 0f
                                                LyricsPosition.CENTER -> 0.5f
                                                LyricsPosition.RIGHT -> 1f
                                            },
                                            0.5f,
                                        )
                                    }
                                }
                            },
                        )
                        Text(
                            "Under the open sky",
                            color = Color.White.copy(alpha = 0.38f),
                            style = IosTheme.typography.subheadline,
                            textAlign = align,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Text(
                        "SAMPLE",
                        color = Color.White.copy(alpha = 0.45f),
                        style = IosTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.TopEnd).padding(14.dp),
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                // Bleed to the sheet edge while the first chip still lines up with the 20dp gutter.
                modifier = Modifier.layout { measurable, constraints ->
                    val bleed = 32.dp.roundToPx()
                    val placeable = measurable.measure(constraints.copy(maxWidth = constraints.maxWidth + bleed))
                    layout(constraints.maxWidth, placeable.height) { placeable.place(-bleed / 2, 0) }
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            ) {
                items(LyricsAnimationStyle.entries) { style ->
                    val active = style == selected
                    Text(
                        text = com.convx.music.ui.component.lyricsStyleLabel(style),
                        color = if (active) Color.White else IosTheme.colors.label,
                        style = IosTheme.typography.subheadline,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(ContinuousCapsule())
                            .background(if (active) selectedColor else IosTheme.colors.secondarySystemFill)
                            .clickable { onSelect(style) }
                            .defaultMinSize(minHeight = 40.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .padding(horizontal = 16.dp),
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(ContinuousRoundedRectangle(IosMetrics.GroupedCornerRadius))
                    .background(IosTheme.colors.secondarySystemGroupedBackground),
            ) {
                Row(
                    Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.lyrics_text_position),
                        style = IosTheme.typography.body,
                        color = IosTheme.colors.label,
                        modifier = Modifier.weight(1f),
                    )
                    Row(
                        Modifier
                            .clip(ContinuousCapsule())
                            .background(IosTheme.colors.secondarySystemFill)
                            .padding(3.dp),
                    ) {
                        LyricsPosition.entries.forEach { p ->
                            val active = p == position
                            Text(
                                text = when (p) {
                                    LyricsPosition.LEFT -> stringResource(R.string.left)
                                    LyricsPosition.CENTER -> stringResource(R.string.center)
                                    LyricsPosition.RIGHT -> stringResource(R.string.right)
                                },
                                color = if (active) Color.White else IosTheme.colors.label,
                                style = IosTheme.typography.footnote,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(ContinuousCapsule())
                                    .background(if (active) selectedColor else Color.Transparent)
                                    .clickable { onPositionChange(p) }
                                    .defaultMinSize(minHeight = 34.dp)
                                    .wrapContentHeight(Alignment.CenterVertically)
                                    .padding(horizontal = 12.dp),
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(start = 16.dp), color = IosTheme.colors.separator, thickness = IosMetrics.SeparatorThickness)
                LyricsSheetSwitch(stringResource(R.string.lyrics_auto_scroll), autoScroll, onAutoScrollChange)
                HorizontalDivider(Modifier.padding(start = 16.dp), color = IosTheme.colors.separator, thickness = IosMetrics.SeparatorThickness)
                LyricsSheetSwitch(stringResource(R.string.lyrics_swipe_to_change_song), swipeToChange, onSwipeToChangeChange)
            }
        }
    }
}

@Composable
private fun LyricsSheetSwitch(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onChange(!checked) }.defaultMinSize(minHeight = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = IosTheme.typography.body, color = IosTheme.colors.label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
