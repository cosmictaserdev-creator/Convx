/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.constants

import androidx.datastore.preferences.core.Preferences

/**
 * Which preferences a preset captures, grouped so a preset can be applied selectively — someone
 * can take a preset's colours without also inheriting its grid layout or its wallpaper.
 *
 * Only *visual* preferences appear here. Accounts, tokens, proxies, sync state, playback
 * behaviour, sort orders and cache sizes are deliberately absent: presets are shared between
 * strangers, and a preset that could rewrite someone's proxy settings or scrobbling credentials
 * would be a credential-theft primitive rather than a theme.
 */
enum class PresetCategory {
    COLORS,
    LAYOUT,
    FONT,
    PLAYER,
    LYRICS,
    GLASS,
    BACKGROUND,

    /** Custom images bound to player controls. Not a preference — see PlayerIconSet. */
    PLAYER_ICONS,

    /** Stickers laid out over the player. Not a preference — see DiyLayout. */
    DIY,
}

object PresetKeys {

    val COLORS: List<Preferences.Key<*>> = listOf(
        DynamicThemeKey,
        SelectedThemeColorKey,
        DarkModeKey,
        PureBlackKey,
        PureBlackMiniPlayerKey,
        PureBlackHeroBackgroundKey,
    )

    val LAYOUT: List<Preferences.Key<*>> = listOf(
        DensityScaleKey,
        CustomDensityScaleKey,
        SlimNavBarKey,
        GridItemsSizeKey,
        GridColumnsOverrideKey,
        GridSpacingKey,
        GridCardHeightOverrideKey,
        SpeedDialColumnsOverrideKey,
        SpeedDialCardHeightOverrideKey,
        HomeHeroCardEnabledKey,
        HomeHeroCardHeightOverrideKey,
        HomeCardCornerRadiusOverrideKey,
        HomeGridColumnsOverrideKey,
        ShowHomeFabKey,
        ForceTabletLayoutKey,
        IosOverscrollKey,
        AppleMusicUiKey,
        MiniBarTabStyleKey,
        LibraryIconsOnlyKey,
        ShowAudioQualityBadgeKey,
    )

    val FONT: List<Preferences.Key<*>> = listOf(
        SelectedFontKey,
        BrandFontEnabledKey,
        CustomFontEnabledKey,
        CustomFontPathKey,
        CustomFontNameKey,
        CustomFontArtistOnlyKey,
    )

    val PLAYER: List<Preferences.Key<*>> = listOf(
        PlayerArtworkStyleKey,
        PlayerStaticColorKey,
        PlayerGradientStopsKey,
        PlayerGradientAngleKey,
        PlayerButtonsStyleKey,
        PlayerBackgroundStyleKey,
        MiniPlayerBackgroundStyleKey,
        PlayerLayoutOrderKey,
        PlayerLayoutHiddenSlotsKey,
        UseNewMiniPlayerDesignKey,
        MiniPlayerOutlineKey,
        MiniPlayerWaveformKey,
        HidePlayerThumbnailKey,
        ThumbnailCornerRadiusKey,
        CropAlbumArtKey,
        SliderStyleKey,
        SquigglySliderKey,
        HideVolumeBarKey,
        CanvasThumbnailAnimationKey,
    )

    val LYRICS: List<Preferences.Key<*>> = listOf(
        LyricsTextPositionKey,
        LyricsGlowEffectKey,
        AppleMusicLyricsBlurKey,
        LyricsStandardBlurKey,
        LyricsAnimationStyleKey,
        LyricsTextSizeKey,
        LyricsLineSpacingKey,
        OneTapFullscreenLyricsKey,
        FullscreenLyricsCollapseTopKey,
    )

    val GLASS: List<Preferences.Key<*>> = listOf(
        LiquidGlassGlobalEnabledKey,
        LiquidGlassAdaptiveContrastKey,
        LiquidGlassTextColorKey,
        LiquidGlassSurfaceTintColorKey,
        LiquidGlassSurfaceOpacityKey,
        LiquidGlassStyleKey,
        LiquidGlassPuckColorKey,
        LiquidGlassPuckOpacityKey,
        LiquidGlassHighlightColorKey,
        LiquidGlassHighlightOpacityKey,
        LiquidGlassVibrancyKey,
        LiquidGlassBlurRadiusKey,
        LiquidGlassLensHeightKey,
        LiquidGlassLensAmountKey,
        LiquidGlassChromaticAberrationKey,
        LiquidGlassDepthEffectKey,
        LiquidGlassPlayerEnabledKey,
        LiquidGlassMiniPlayerEnabledKey,
        LiquidGlassNavBarEnabledKey,
        LiquidGlassSidePanelEnabledKey,
        LiquidGlassSidePanelVibrancyKey,
        LiquidGlassSidePanelBlurRadiusKey,
        LiquidGlassSidePanelLensHeightKey,
        LiquidGlassSidePanelLensAmountKey,
        LiquidGlassSidePanelColorKey,
        LiquidGlassSidePanelSurfaceOpacityKey,
        LiquidGlassSidePanelTextColorKey,
    )

    val BACKGROUND: List<Preferences.Key<*>> = listOf(
        HomeBackgroundEnabledKey,
        HomeBackgroundPathKey,
        HomeBackgroundBlurKey,
        HomeBackgroundDimKey,
        HomeBackgroundAnimateKey,
        HomeBackgroundIsVideoKey,
        LibraryBackgroundModeKey,
    )

    /** Preference-backed categories, in the order the picker lists them. */
    val byCategory: Map<PresetCategory, List<Preferences.Key<*>>> = mapOf(
        PresetCategory.COLORS to COLORS,
        PresetCategory.LAYOUT to LAYOUT,
        PresetCategory.FONT to FONT,
        PresetCategory.PLAYER to PLAYER,
        PresetCategory.LYRICS to LYRICS,
        PresetCategory.GLASS to GLASS,
        PresetCategory.BACKGROUND to BACKGROUND,
    )

    /** Every capturable preference key, flattened. */
    val all: List<Preferences.Key<*>> = byCategory.values.flatten()

    private val byName: Map<String, Preferences.Key<*>> = all.associateBy { it.name }

    /**
     * Resolves a key name read out of a preset file.
     *
     * @return null for any name not on the allowlist — which is what stops a hand-edited or
     *   malicious preset from writing to preferences presets have no business touching.
     */
    fun keyOrNull(name: String): Preferences.Key<*>? = byName[name]

    fun categoryOf(key: Preferences.Key<*>): PresetCategory? =
        byCategory.entries.firstOrNull { key in it.value }?.key

    /**
     * Keys whose value is an absolute path to a file in app storage rather than a plain setting.
     * These need the file itself carried alongside the preset and the path rewritten on apply.
     */
    val fileBackedKeys: Set<String> = setOf(
        HomeBackgroundPathKey.name,
        CustomFontPathKey.name,
    )
}

/**
 * The declared type of every capturable preference, keyed by its DataStore name.
 *
 * Generated from PreferenceKeys.kt. It exists because [Preferences.Key] erases its type at
 * runtime: without this, an imported preset could claim a boolean lives where the app reads an
 * int, and the resulting ClassCastException would fire deep inside an unrelated screen. Applying
 * a value whose type does not match the entry here is refused outright.
 */
enum class PrefType { BOOL, INT, FLOAT, LONG, STRING, STRING_SET }

val presetKeyTypes: Map<String, PrefType> = mapOf(
        "dynamicTheme" to PrefType.BOOL,
        "selectedThemeColor" to PrefType.INT,
        "darkMode" to PrefType.STRING,
        "pureBlack" to PrefType.BOOL,
        "pureBlackMiniPlayer" to PrefType.BOOL,
        "pureBlackHeroBackground" to PrefType.BOOL,
        "density_scale_factor" to PrefType.FLOAT,
        "custom_density_scale_value" to PrefType.FLOAT,
        "slimNavBar" to PrefType.BOOL,
        "gridItemSize" to PrefType.STRING,
        "gridColumnsOverride" to PrefType.INT,
        "gridSpacingDp" to PrefType.INT,
        "gridCardHeightOverrideDp" to PrefType.INT,
        "speedDialColumnsOverride" to PrefType.INT,
        "speedDialCardHeightOverrideDp" to PrefType.INT,
        "homeHeroCardEnabled" to PrefType.BOOL,
        "homeHeroCardHeightOverrideDp" to PrefType.INT,
        "homeCardCornerRadiusOverrideDp" to PrefType.INT,
        "homeGridColumnsOverride" to PrefType.INT,
        "showHomeFab" to PrefType.BOOL,
        "forceTabletLayout" to PrefType.BOOL,
        "iosOverscroll" to PrefType.BOOL,
        "appleMusicUi" to PrefType.BOOL,
        "miniBarTabStyle" to PrefType.BOOL,
        "libraryIconsOnly" to PrefType.BOOL,
        "show_audio_quality_badge" to PrefType.BOOL,
        "selected_font" to PrefType.STRING,
        "brandFontEnabled" to PrefType.BOOL,
        "customFontEnabled" to PrefType.BOOL,
        "customFontPath" to PrefType.STRING,
        "customFontName" to PrefType.STRING,
        "customFontArtistOnly" to PrefType.BOOL,
        "playerArtworkStyle" to PrefType.STRING,
        "playerStaticColor" to PrefType.INT,
        "playerGradientStops" to PrefType.STRING,
        "playerGradientAngle" to PrefType.FLOAT,
        "player_buttons_style" to PrefType.STRING,
        "playerBackgroundStyle" to PrefType.STRING,
        "miniPlayerBackgroundStyle" to PrefType.STRING,
        "playerLayoutOrder" to PrefType.STRING,
        "playerLayoutHiddenSlots" to PrefType.STRING,
        "useNewMiniPlayerDesign" to PrefType.BOOL,
        "miniPlayerOutline" to PrefType.BOOL,
        "mini_player_waveform" to PrefType.BOOL,
        "hidePlayerThumbnail" to PrefType.BOOL,
        "thumbnailCornerRadius" to PrefType.FLOAT,
        "cropAlbumArt" to PrefType.BOOL,
        "sliderStyle" to PrefType.STRING,
        "squigglySlider" to PrefType.BOOL,
        "hideVolumeBar" to PrefType.BOOL,
        "canvasThumbnailAnimation" to PrefType.BOOL,
        "lyricsTextPosition" to PrefType.STRING,
        "lyricsGlowEffect" to PrefType.BOOL,
        "appleMusicLyricsBlur" to PrefType.BOOL,
        "lyricsStandardBlur" to PrefType.BOOL,
        "lyricsAnimationStyle" to PrefType.STRING,
        "lyricsTextSize" to PrefType.FLOAT,
        "lyricsLineSpacing" to PrefType.FLOAT,
        "oneTapFullscreenLyrics" to PrefType.BOOL,
        "fullscreenLyricsCollapseTop" to PrefType.BOOL,
        "liquidGlassGlobalEnabled" to PrefType.BOOL,
        "liquidGlassAdaptiveContrast" to PrefType.BOOL,
        "liquidGlassTextColor" to PrefType.INT,
        "liquidGlassSurfaceTintColor" to PrefType.INT,
        "liquidGlassSurfaceOpacity" to PrefType.FLOAT,
        "liquidGlassStyle" to PrefType.STRING,
        "liquidGlassPuckColor" to PrefType.INT,
        "liquidGlassPuckOpacity" to PrefType.FLOAT,
        "liquidGlassHighlightColor" to PrefType.INT,
        "liquidGlassHighlightOpacity" to PrefType.FLOAT,
        "liquidGlassVibrancy" to PrefType.FLOAT,
        "liquidGlassBlurRadius" to PrefType.FLOAT,
        "liquidGlassLensHeight" to PrefType.FLOAT,
        "liquidGlassLensAmount" to PrefType.FLOAT,
        "liquidGlassChromaticAberration" to PrefType.BOOL,
        "liquidGlassDepthEffect" to PrefType.BOOL,
        "liquidGlassPlayerEnabled" to PrefType.BOOL,
        "liquidGlassMiniPlayerEnabled" to PrefType.BOOL,
        "liquidGlassNavBarEnabled" to PrefType.BOOL,
        "liquidGlassSidePanelEnabled" to PrefType.BOOL,
        "liquidGlassSidePanelVibrancy" to PrefType.FLOAT,
        "liquidGlassSidePanelBlurRadius" to PrefType.FLOAT,
        "liquidGlassSidePanelLensHeight" to PrefType.FLOAT,
        "liquidGlassSidePanelLensAmount" to PrefType.FLOAT,
        "liquidGlassSidePanelColor" to PrefType.INT,
        "liquidGlassSidePanelSurfaceOpacity" to PrefType.FLOAT,
        "liquidGlassSidePanelTextColor" to PrefType.INT,
        "homeBackgroundEnabled" to PrefType.BOOL,
        "homeBackgroundPath" to PrefType.STRING,
        "homeBackgroundBlur" to PrefType.FLOAT,
        "homeBackgroundDim" to PrefType.FLOAT,
        "homeBackgroundAnimate" to PrefType.BOOL,
        "homeBackgroundIsVideo" to PrefType.BOOL,
        "libraryBackgroundMode" to PrefType.STRING,
)
