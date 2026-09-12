/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings

import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.app.Activity
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.content.Context
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.content.Intent
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.os.Build
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.core.content.edit
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.bounceClick
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Arrangement
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.height
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.size
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHostState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SwitchDefaults
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableFloatStateOf
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.CanvasSource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.CanvasSourceKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.CanvasThumbnailAnimationKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ChipSortTypeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.CropAlbumArtKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DefaultOpenTabKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DensityScale
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DensityScaleKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DynamicThemeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableSettingsPopupKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableHighRefreshRateKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableLyricsThumbnailPlayPauseKey
import com.nexamusic.app.constants.OneTapFullscreenLyricsKey
import com.nexamusic.app.constants.FullscreenLyricsCollapseTopKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.IosOverscrollKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.GridItemSize
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.GridItemsSizeKey
import com.nexamusic.app.constants.GridCardHeightOverrideKey
import com.nexamusic.app.constants.GridColumnsOverrideKey
import com.nexamusic.app.constants.GridSpacingKey
import com.nexamusic.app.ui.utils.GridCardHeightChoices
import com.nexamusic.app.ui.utils.HomeHeroCardHeightChoices
import com.nexamusic.app.ui.utils.HomeCardCornerRadiusChoices
import com.nexamusic.app.constants.SpeedDialColumnsOverrideKey
import com.nexamusic.app.constants.HomeHeroCardHeightOverrideKey
import com.nexamusic.app.constants.SpeedDialCardHeightOverrideKey
import com.nexamusic.app.constants.HomeCardCornerRadiusOverrideKey
import com.nexamusic.app.constants.HomeGridColumnsOverrideKey
import com.nexamusic.app.constants.HomeHeroCardEnabledKey
import com.nexamusic.app.constants.PureBlackHeroBackgroundKey
import com.nexamusic.app.constants.HideHomeFavoriteIconKey
import com.nexamusic.app.constants.ShowHomeFabKey
import com.nexamusic.app.ui.utils.GridColumnChoices
import com.nexamusic.app.ui.utils.GridSpacingChoices
import androidx.compose.material3.Slider
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.HidePlayerThumbnailKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LibraryFilter
import com.nexamusic.app.constants.LibraryIconsOnlyKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ListenTogetherInTopBarKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsAnimationStyle
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsAnimationStyleKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsStandardBlurKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsTextPositionKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsTextSizeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowCachedPlaylistKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowDownloadedPlaylistKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowLikedPlaylistKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowTopPlaylistKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowUploadedPlaylistKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SlimNavBarKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SwipeSensitivityKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SwipeThumbnailKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SwipeLyricsKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SwipeToRemoveSongKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SwipeToSongKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ThumbnailCornerRadiusKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.UseNewMiniPlayerDesignKey
import com.nexamusic.app.constants.MiniBarTabStyleKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.UseAppleMusicPlayerKey
import com.nexamusic.app.constants.UseNewPlayerDesignKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.ThumbnailCornerRadiusModal
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.DefaultDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.EnumDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.rememberPreference
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import kotlin.math.roundToInt
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsClickKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.AppleMusicLyricsBlurKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsGlowEffectKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsLineSpacingKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsScrollKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowAudioQualityBadgeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.MiniPlayerWaveformKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowCommentButtonKey
import com.nexamusic.app.constants.AppFont
import com.nexamusic.app.constants.SelectedFontKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState,
) {
    val (_, _) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
    val (miniBarTabStyle, onMiniBarTabStyleChange) = rememberPreference(MiniBarTabStyleKey, defaultValue = false)
    val (_, _) = rememberPreference(DynamicThemeKey, defaultValue = true)
    val (useNewPlayerDesign, _) = rememberPreference(UseNewPlayerDesignKey, defaultValue = false)
    // DIY stickers have no equivalent slot in the ported Apple Music Player V17
    // layout, so its entry point is hidden rather than left dangling to a
    // customization that would silently never render.
    val (useAppleMusicPlayer, _) = rememberPreference(UseAppleMusicPlayerKey, defaultValue = false)
    val (showAudioQualityBadge, onShowAudioQualityBadgeChange) = rememberPreference(
        ShowAudioQualityBadgeKey,
        defaultValue = true
    )
    val (miniPlayerWaveform, onMiniPlayerWaveformChange) = rememberPreference(
        MiniPlayerWaveformKey,
        defaultValue = true
    )
    val (selectedFontValue) = rememberPreference(
        SelectedFontKey,
        defaultValue = AppFont.SYSTEM.value
    )

    val (libraryIconsOnly, onLibraryIconsOnlyChange) = rememberPreference(
        LibraryIconsOnlyKey,
        defaultValue = true
    )

    val (enableHighRefreshRate, onEnableHighRefreshRateChange) = rememberPreference(
        EnableHighRefreshRateKey,
        defaultValue = true
    )
    val (iosOverscroll, onIosOverscrollChange) = rememberPreference(
        IosOverscrollKey,
        defaultValue = true
    )
    val (enableSettingsPopup, onEnableSettingsPopupChange) = rememberPreference(
        EnableSettingsPopupKey,
        defaultValue = false
    )

    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(
        HidePlayerThumbnailKey,
        defaultValue = false
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
        defaultValue = LyricsAnimationStyle.VIVIMUSIC_1
    )
    val (lyricsTextSize, onLyricsTextSizeChange) = rememberPreference(LyricsTextSizeKey, defaultValue = 30f)
    val (lyricsLineSpacing, onLyricsLineSpacingChange) = rememberPreference(LyricsLineSpacingKey, defaultValue = 1.3f)
    val (lyricsGlowEffect, onLyricsGlowEffectChange) = rememberPreference(LyricsGlowEffectKey, defaultValue = true)
    val (appleMusicLyricsBlur, onAppleMusicLyricsBlurChange) = rememberPreference(AppleMusicLyricsBlurKey, defaultValue = true)
    val (lyricsStandardBlur, onLyricsStandardBlurChange) = rememberPreference(LyricsStandardBlurKey, defaultValue = false)
    val (swipeLyrics, onSwipeLyricsChange) = rememberPreference(SwipeLyricsKey, defaultValue = false)
    val (enableLyricsThumbnailPlayPause, onEnableLyricsThumbnailPlayPauseChange) = rememberPreference(EnableLyricsThumbnailPlayPauseKey, defaultValue = false)
    val (oneTapFullscreenLyrics, onOneTapFullscreenLyricsChange) = rememberPreference(OneTapFullscreenLyricsKey, defaultValue = false)
    val (fullscreenLyricsCollapseTop, onFullscreenLyricsCollapseTopChange) = rememberPreference(FullscreenLyricsCollapseTopKey, defaultValue = true)

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
    val (gridItemSize, onGridItemSizeChange) = rememberEnumPreference(
        GridItemsSizeKey,
        defaultValue = GridItemSize.SMALL
    )
    val (gridColumnsOverride, onGridColumnsOverrideChange) = rememberPreference(GridColumnsOverrideKey, 0)
    val (gridCardHeightOverride, onGridCardHeightOverrideChange) = rememberPreference(GridCardHeightOverrideKey, 0)
    val (gridSpacing, onGridSpacingChange) = rememberPreference(GridSpacingKey, 16)
    val (speedDialColumnsOverride, onSpeedDialColumnsOverrideChange) = rememberPreference(SpeedDialColumnsOverrideKey, 0)
    val (pureBlackHeroBackground, onPureBlackHeroBackgroundChange) = rememberPreference(PureBlackHeroBackgroundKey, false)
    val (showHomeFab, onShowHomeFabChange) = rememberPreference(ShowHomeFabKey, defaultValue = true)
    val (hideHomeFavoriteIcon, onHideHomeFavoriteIconChange) = rememberPreference(
        HideHomeFavoriteIconKey,
        defaultValue = false
    )
    val (homeHeroCardHeightOverride, onHomeHeroCardHeightOverrideChange) = rememberPreference(HomeHeroCardHeightOverrideKey, 0)
    val (speedDialCardHeightOverride, onSpeedDialCardHeightOverrideChange) = rememberPreference(SpeedDialCardHeightOverrideKey, 0)
    val (homeCardCornerRadiusOverride, onHomeCardCornerRadiusOverrideChange) = rememberPreference(HomeCardCornerRadiusOverrideKey, 0)
    val (homeHeroCardEnabled, onHomeHeroCardEnabledChange) = rememberPreference(HomeHeroCardEnabledKey, true)
    val (homeGridColumnsOverride, onHomeGridColumnsOverrideChange) = rememberPreference(HomeGridColumnsOverrideKey, 0)

    // Density scale preferences
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
        sharedPreferences.edit {
            putFloat("density_scale_factor", newScale)
        }
        showRestartDialog = true
    }

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
    val (showCommentButton, onShowCommentButtonChange) = rememberPreference(
        ShowCommentButtonKey,
        defaultValue = true
    )

    val (defaultChip, onDefaultChipChange) = rememberEnumPreference(
        key = ChipSortTypeKey,
        defaultValue = LibraryFilter.LIBRARY
    )

    var showLyricsPositionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showLyricsAnimationStyleDialog by rememberSaveable {
        mutableStateOf(false)
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

    if (showLyricsAnimationStyleDialog) {
        EnumDialog(
            onDismiss = { showLyricsAnimationStyleDialog = false },
            onSelect = {
                onLyricsAnimationStyleChange(it)
                showLyricsAnimationStyleDialog = false
            },
            title = stringResource(R.string.lyrics_animation_style),
            current = lyricsAnimationStyle,
            values = LyricsAnimationStyle.entries,
            valueText = {
                when (it) {
                    LyricsAnimationStyle.NONE -> stringResource(R.string.none)
                    LyricsAnimationStyle.FADE -> stringResource(R.string.fade)
                    LyricsAnimationStyle.GLOW -> stringResource(R.string.glow)
                    LyricsAnimationStyle.SLIDE -> stringResource(R.string.slide)
                    LyricsAnimationStyle.KARAOKE -> stringResource(R.string.karaoke)
                    LyricsAnimationStyle.APPLE -> stringResource(R.string.apple_music_style)
                    LyricsAnimationStyle.APPLE_V2 -> stringResource(R.string.apple_music_style_letter)
                    LyricsAnimationStyle.VIVIMUSIC_1 -> stringResource(R.string.vivimusic_1)
                    LyricsAnimationStyle.LYRICS_V2 -> stringResource(R.string.lyrics_v2_fluid)
                    LyricsAnimationStyle.METRO_LYRICS -> stringResource(R.string.lyrics_animation_metro)
                }
            }
        )
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

    var showDefaultChipDialog by rememberSaveable { mutableStateOf(false) }
    if (showDefaultChipDialog) {
        EnumDialog(
            onDismiss = { showDefaultChipDialog = false },
            onSelect = {
                onDefaultChipChange(it)
                showDefaultChipDialog = false
            },
            title = stringResource(R.string.default_lib_chips),
            current = defaultChip,
            // ARTISTS omitted: Library no longer has an Artists chip, so offering it
            // here would set a default that resolves to the mixed view anyway.
            values = LibraryFilter.entries.filter { it != LibraryFilter.ARTISTS },
            valueText = {
                when (it) {
                    LibraryFilter.SONGS -> stringResource(R.string.songs)
                    LibraryFilter.ARTISTS -> stringResource(R.string.artists)
                    LibraryFilter.ALBUMS -> stringResource(R.string.albums)
                    LibraryFilter.PLAYLISTS -> stringResource(R.string.playlists)
                    LibraryFilter.LIBRARY -> stringResource(R.string.filter_library)
                }
            }
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
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

    var showGridSizeDialog by rememberSaveable { mutableStateOf(false) }
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

    androidx.compose.foundation.lazy.LazyColumn(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .padding(horizontal = 16.dp),
    ) {
        item(key = "all_appearance_settings") {
            Column {
        Material3SettingsGroup(
            title = stringResource(R.string.theme_colors),
            items = listOfNotNull(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.theme_colors)) },
                    onClick = { navController.navigate("settings/appearance/theme") }
                ),
                Material3SettingsItem(
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.convx_logo),
                    title = { Text(stringResource(R.string.app_icon)) },
                    description = { Text(stringResource(R.string.app_icon_desc)) },
                    onClick = { navController.navigate("settings/appearance/appicon") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.presets)) },
                    description = { Text(stringResource(R.string.presets_desc)) },
                    onClick = { navController.navigate("settings/appearance/presets") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.player_icons)) },
                    description = { Text(stringResource(R.string.player_icons_desc)) },
                    onClick = { navController.navigate("settings/appearance/playericons") }
                ),
                if (!useAppleMusicPlayer) Material3SettingsItem(
                    icon = painterResource(R.drawable.edit),
                    title = { Text(stringResource(R.string.diy)) },
                    description = { Text(stringResource(R.string.diy_desc)) },
                    onClick = { navController.navigate("settings/appearance/diy") }
                ) else null
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.liquid_glass),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.liquid_glass)) },
                    onClick = { navController.navigate("settings/appearance/liquidglass") }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.appearance),
            items = buildList {
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.speed),
                        title = { Text(stringResource(R.string.enable_high_refresh_rate)) },
                        description = { Text(stringResource(R.string.enable_high_refresh_rate_desc)) },
                        trailingContent = {
                            Switch(
                                checked = enableHighRefreshRate,
                                onCheckedChange = onEnableHighRefreshRateChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (enableHighRefreshRate) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onEnableHighRefreshRateChange(!enableHighRefreshRate) }
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.tune),
                        title = { Text(stringResource(R.string.ios_overscroll)) },
                        description = { Text(stringResource(R.string.ios_overscroll_desc)) },
                        trailingContent = {
                            Switch(
                                checked = iosOverscroll,
                                onCheckedChange = onIosOverscrollChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (iosOverscroll) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onIosOverscrollChange(!iosOverscroll) }
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.settings),
                        title = { Text(stringResource(R.string.enable_settings_popup)) },
                        description = { Text(stringResource(R.string.enable_settings_popup_desc)) },
                        trailingContent = {
                            Switch(
                                checked = enableSettingsPopup,
                                onCheckedChange = onEnableSettingsPopupChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (enableSettingsPopup) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onEnableSettingsPopupChange(!enableSettingsPopup) }
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.grid_view),
                        title = { Text(stringResource(R.string.library_icons_only)) },
                        description = { Text(stringResource(R.string.library_icons_only_desc)) },
                        trailingContent = {
                            Switch(
                                checked = libraryIconsOnly,
                                onCheckedChange = onLibraryIconsOnlyChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (libraryIconsOnly) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onLibraryIconsOnlyChange(!libraryIconsOnly) }
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(27.dp))

        val (thumbnailCornerRadius, onThumbnailCornerRadiusChange) = rememberPreference(
            ThumbnailCornerRadiusKey,
            defaultValue = 3f
        )
        
        var showThumbnailCornerRadiusDialog by rememberSaveable { mutableStateOf(false) }

        Material3SettingsGroup(
            title = stringResource(R.string.player),
            items = listOfNotNull(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.gradient),
                    title = { Text(stringResource(R.string.player_theme)) },
                    description = { Text(stringResource(R.string.player_theme_desc)) },
                    onClick = { navController.navigate("settings/appearance/playertheme") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.hide_image),
                    title = { Text(stringResource(R.string.hide_player_thumbnail)) },
                    description = { Text(stringResource(R.string.hide_player_thumbnail_desc)) },
                    trailingContent = {
                        Switch(
                            checked = hidePlayerThumbnail,
                            onCheckedChange = onHidePlayerThumbnailChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (hidePlayerThumbnail) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onHidePlayerThumbnailChange(!hidePlayerThumbnail) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.image),
                    title = { Text(stringResource(R.string.thumbnail_corner_radius)) },
                    description = { Text(stringResource(R.string.thumbnail_corner_radius_desc)) },
                    trailingContent = {
                        Text(
                            text = "${thumbnailCornerRadius.roundToInt()}dp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = { showThumbnailCornerRadiusDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.crop),
                    title = { Text(stringResource(R.string.crop_album_art)) },
                    description = { Text(stringResource(R.string.crop_album_art_desc)) },
                    trailingContent = {
                        Switch(
                            checked = cropAlbumArt,
                            onCheckedChange = onCropAlbumArtChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (cropAlbumArt) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onCropAlbumArtChange(!cropAlbumArt) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.show_audio_quality_badge)) },
                    trailingContent = {
                        Switch(
                            checked = showAudioQualityBadge,
                            onCheckedChange = onShowAudioQualityBadgeChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showAudioQualityBadge) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowAudioQualityBadgeChange(!showAudioQualityBadge) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.mini_player_waveform)) },
                    trailingContent = {
                        Switch(
                            checked = miniPlayerWaveform,
                            onCheckedChange = onMiniPlayerWaveformChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (miniPlayerWaveform) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onMiniPlayerWaveformChange(!miniPlayerWaveform) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.nav_bar),
                    title = { Text(stringResource(R.string.mini_bar_tab_style)) },
                    description = { Text(stringResource(R.string.mini_bar_tab_style_desc)) },
                    trailingContent = {
                        Switch(
                            checked = miniBarTabStyle,
                            onCheckedChange = onMiniBarTabStyleChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (miniBarTabStyle) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onMiniBarTabStyleChange(!miniBarTabStyle) }
                ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.canvas_art),
                        title = { Text(stringResource(R.string.vivimusic_canvas)) },
                    description = {
                        val summary = if (!canvasThumbnailAnimation) {
                            stringResource(R.string.disable)
                        } else {
                            when (canvasSource) {
                                CanvasSource.AUTO -> stringResource(R.string.canvas_source_auto)
                                CanvasSource.ECHO_MUSIC -> stringResource(R.string.canvas_source_echo_music)
                                CanvasSource.APPLE_MUSIC -> stringResource(R.string.canvas_source_apple_music)
                                CanvasSource.VIVIMUSIC -> stringResource(R.string.canvas_source_vivimusic)
                                CanvasSource.TIDAL -> stringResource(R.string.canvas_source_tidal)
                            }
                        }
                        Text(summary)
                    },
                    onClick = { navController.navigate("settings/appearance/canvas") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.chat_msg),
                    title = { Text(stringResource(R.string.show_comment_button)) },
                    description = { Text(stringResource(R.string.show_comment_button_description)) },
                    trailingContent = {
                        Switch(
                            checked = showCommentButton,
                            onCheckedChange = onShowCommentButtonChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showCommentButton) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowCommentButtonChange(!showCommentButton) }
                )
            )
        )

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

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.lyrics),
            items = listOfNotNull(
                Material3SettingsItem(
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_animation_style)) },
                    description = {
                        Text(
                            when (lyricsAnimationStyle) {
                                LyricsAnimationStyle.NONE -> stringResource(R.string.none)
                                LyricsAnimationStyle.FADE -> stringResource(R.string.fade)
                                LyricsAnimationStyle.GLOW -> stringResource(R.string.glow)
                                LyricsAnimationStyle.SLIDE -> stringResource(R.string.slide)
                                LyricsAnimationStyle.KARAOKE -> stringResource(R.string.karaoke)
                                LyricsAnimationStyle.VIVIMUSIC_1 -> stringResource(R.string.vivimusic_1)
                                LyricsAnimationStyle.APPLE -> stringResource(R.string.apple_music_style)
                                LyricsAnimationStyle.APPLE_V2 -> stringResource(R.string.apple_music_style_letter)
                                LyricsAnimationStyle.LYRICS_V2 -> stringResource(R.string.lyrics_v2_fluid)
                                LyricsAnimationStyle.METRO_LYRICS -> stringResource(R.string.lyrics_animation_metro)
                            }
                        )
                    },
                    onClick = { showLyricsAnimationStyleDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_glow_effect)) },
                    description = { Text(stringResource(R.string.lyrics_glow_effect_desc)) },
                    trailingContent = {
                        Switch(
                            checked = lyricsGlowEffect,
                            onCheckedChange = onLyricsGlowEffectChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (lyricsGlowEffect) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onLyricsGlowEffectChange(!lyricsGlowEffect) }
                ),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && lyricsAnimationStyle == LyricsAnimationStyle.VIVIMUSIC_1) {
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.lyrics),
                        title = { Text(stringResource(R.string.apple_music_lyrics_blur)) },
                        description = { Text(stringResource(R.string.apple_music_lyrics_blur_desc)) },
                        trailingContent = {
                            Switch(
                                checked = appleMusicLyricsBlur,
                                onCheckedChange = onAppleMusicLyricsBlurChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (appleMusicLyricsBlur) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onAppleMusicLyricsBlurChange(!appleMusicLyricsBlur) }
                    )
                } else null,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.lyrics),
                        title = { Text(stringResource(R.string.standard_lyrics_blur)) },
                        description = { Text(stringResource(R.string.apple_music_lyrics_blur_desc)) },
                        trailingContent = {
                            Switch(
                                checked = lyricsStandardBlur,
                                onCheckedChange = onLyricsStandardBlurChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (lyricsStandardBlur) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onLyricsStandardBlurChange(!lyricsStandardBlur) }
                    )
                } else null,
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_auto_scroll)) },
                    trailingContent = {
                        Switch(
                            checked = lyricsScroll,
                            onCheckedChange = onLyricsScrollChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (lyricsScroll) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onLyricsScrollChange(!lyricsScroll) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.lyrics_swipe_to_change_song)) },
                    description = { Text(stringResource(R.string.lyrics_swipe_to_change_song_desc)) },
                    trailingContent = {
                        Switch(
                            checked = swipeLyrics,
                            onCheckedChange = onSwipeLyricsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (swipeLyrics) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onSwipeLyricsChange(!swipeLyrics) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.play),
                    title = { Text(stringResource(R.string.lyrics_thumbnail_play_pause)) },
                    description = { Text(stringResource(R.string.lyrics_thumbnail_play_pause_desc)) },
                    trailingContent = {
                        Switch(
                            checked = enableLyricsThumbnailPlayPause,
                            onCheckedChange = onEnableLyricsThumbnailPlayPauseChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableLyricsThumbnailPlayPause) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableLyricsThumbnailPlayPauseChange(!enableLyricsThumbnailPlayPause) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.fullscreen),
                    title = { Text(stringResource(R.string.one_tap_fullscreen_lyrics)) },
                    description = { Text(stringResource(R.string.one_tap_fullscreen_lyrics_desc)) },
                    trailingContent = {
                        Switch(
                            checked = oneTapFullscreenLyrics,
                            onCheckedChange = onOneTapFullscreenLyricsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (oneTapFullscreenLyrics) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onOneTapFullscreenLyricsChange(!oneTapFullscreenLyrics) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.expand_more),
                    title = { Text(stringResource(R.string.fullscreen_lyrics_collapse_top)) },
                    description = { Text(stringResource(R.string.fullscreen_lyrics_collapse_top_desc)) },
                    trailingContent = {
                        Switch(
                            checked = fullscreenLyricsCollapseTop,
                            onCheckedChange = onFullscreenLyricsCollapseTopChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (fullscreenLyricsCollapseTop) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onFullscreenLyricsCollapseTopChange(!fullscreenLyricsCollapseTop) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.misc),
            items = listOf(
                Material3SettingsItem(
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
                Material3SettingsItem(
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
                            }
                        )
                    },
                    onClick = { showDefaultChipDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.swipe_song_to_add)) },
                    trailingContent = {
                        Switch(
                            checked = swipeToSong,
                            onCheckedChange = onSwipeToSongChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (swipeToSong) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onSwipeToSongChange(!swipeToSong) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.swipe),
                    title = { Text(stringResource(R.string.swipe_song_to_remove)) },
                    trailingContent = {
                        Switch(
                            checked = swipeToRemoveSong,
                            onCheckedChange = onSwipeToRemoveSongChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (swipeToRemoveSong) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onSwipeToRemoveSongChange(!swipeToRemoveSong) }
                ),
                Material3SettingsItem(
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.display_density)) },
                    description = {
                        Text(DensityScale.fromValue(densityScale).label)
                    },
                    onClick = { showDensityScaleDialog = true }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.grid_and_cards),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.pure_black_hero_background)) },
                    description = { Text(stringResource(R.string.pure_black_hero_background_desc)) },
                    trailingContent = {
                        Switch(
                            checked = pureBlackHeroBackground,
                            onCheckedChange = onPureBlackHeroBackgroundChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (pureBlackHeroBackground) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onPureBlackHeroBackgroundChange(!pureBlackHeroBackground) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.mic),
                    title = { Text(stringResource(R.string.show_home_fab)) },
                    description = { Text(stringResource(R.string.show_home_fab_desc)) },
                    trailingContent = {
                        Switch(
                            checked = showHomeFab,
                            onCheckedChange = onShowHomeFabChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showHomeFab) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowHomeFabChange(!showHomeFab) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.favorite_border),
                    title = { Text(stringResource(R.string.hide_home_favorite_icon)) },
                    description = { Text(stringResource(R.string.hide_home_favorite_icon_desc)) },
                    trailingContent = {
                        Switch(
                            checked = hideHomeFavoriteIcon,
                            onCheckedChange = onHideHomeFavoriteIconChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (hideHomeFavoriteIcon) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onHideHomeFavoriteIconChange(!hideHomeFavoriteIcon) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.grid_columns)) },
                    description = {
                        Column {
                            Text(
                                text = if (gridColumnsOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    gridColumnsOverride.toString()
                                }
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.grid_card_height)) },
                    description = {
                        Column {
                            Text(
                                text = if (gridCardHeightOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    "${gridCardHeightOverride}dp"
                                }
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
                Material3SettingsItem(
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.speed_dial_columns)) },
                    description = {
                        val speedDialChoices = remember { listOf(0, 3, 4, 5, 6) }
                        Column {
                            Text(
                                text = if (speedDialColumnsOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    speedDialColumnsOverride.toString()
                                }
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
                Material3SettingsItem(
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.home_grid_columns)) },
                    description = {
                        val homeGridChoices = remember { listOf(0, 2, 3, 4) }
                        Column {
                            Text(
                                text = if (homeGridColumnsOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    homeGridColumnsOverride.toString()
                                }
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.home_hero_card_height)) },
                    description = {
                        Column {
                            Text(
                                text = if (homeHeroCardHeightOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    "${homeHeroCardHeightOverride}dp"
                                }
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.speed_dial_card_height)) },
                    description = {
                        Column {
                            Text(
                                text = if (speedDialCardHeightOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    "${speedDialCardHeightOverride}dp"
                                }
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
                Material3SettingsItem(
                    icon = painterResource(R.drawable.grid_view),
                    title = { Text(stringResource(R.string.home_card_corner_radius)) },
                    description = {
                        Column {
                            Text(
                                text = if (homeCardCornerRadiusOverride == 0) {
                                    stringResource(R.string.auto)
                                } else {
                                    "${homeCardCornerRadiusOverride}dp"
                                }
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

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.auto_playlists),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.favorite),
                    title = { Text(stringResource(R.string.show_liked_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showLikedPlaylist,
                            onCheckedChange = onShowLikedPlaylistChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showLikedPlaylist) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowLikedPlaylistChange(!showLikedPlaylist) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.offline),
                    title = { Text(stringResource(R.string.show_downloaded_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showDownloadedPlaylist,
                            onCheckedChange = onShowDownloadedPlaylistChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showDownloadedPlaylist) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowDownloadedPlaylistChange(!showDownloadedPlaylist) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.trending_up),
                    title = { Text(stringResource(R.string.show_top_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showTopPlaylist,
                            onCheckedChange = onShowTopPlaylistChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showTopPlaylist) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowTopPlaylistChange(!showTopPlaylist) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.cached),
                    title = { Text(stringResource(R.string.show_cached_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showCachedPlaylist,
                            onCheckedChange = onShowCachedPlaylistChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showCachedPlaylist) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowCachedPlaylistChange(!showCachedPlaylist) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.backup),
                    title = { Text(stringResource(R.string.show_uploaded_playlist)) },
                    trailingContent = {
                        Switch(
                            checked = showUploadedPlaylist,
                            onCheckedChange = onShowUploadedPlaylistChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showUploadedPlaylist) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowUploadedPlaylistChange(!showUploadedPlaylist) }
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.appearance)) },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigateUp() },
                onLongClick = { navController.backToMain() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
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
