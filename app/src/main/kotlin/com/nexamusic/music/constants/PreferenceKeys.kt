/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.time.LocalDateTime
import java.time.ZoneOffset

import com.music.innertube.models.IpVersion

val IsFirstRunKey = booleanPreferencesKey("isFirstRun")
/** Set once, the first time the app ever runs — gates the donation prompt on "used for
 *  a while" rather than showing it to a brand new install. */
val FirstLaunchTimestampKey = longPreferencesKey("firstLaunchTimestamp")
val DonationPromptLastShownKey = longPreferencesKey("donationPromptLastShown")
val DonationPromptDismissedKey = booleanPreferencesKey("donationPromptDismissed")
val AppIconKey = stringPreferencesKey("appIcon")

/** JSON map of player-control slot -> user-supplied glyph. See ui/player/customize/PlayerIcons.kt. */
/**
 * Long-press menus as a full-screen overlay (true) rather than a bottom sheet (false).
 * One switch for the whole app -- see OverlayMenu, which shares MenuState with
 * BottomSheetMenu so no call site knows which is in use.
 */
val OverlayMenuStyleKey = booleanPreferencesKey("overlayMenuStyle")
val PlayerIconsKey = stringPreferencesKey("playerIcons")
/** JSON map of V2 player-control slot -> user-supplied glyph. Separate from [PlayerIconsKey]. */
val V2PlayerIconsKey = stringPreferencesKey("v2PlayerIcons")

/** JSON sticker arrangement drawn over the player. See ui/player/customize/DiyLayout.kt. */
val DiyLayoutKey = stringPreferencesKey("diyLayout")
val EnableHighRefreshRateKey = booleanPreferencesKey("enableHighRefreshRate")
val DynamicThemeKey = booleanPreferencesKey("dynamicTheme")
val SelectedThemeColorKey = intPreferencesKey("selectedThemeColor")
val DarkModeKey = stringPreferencesKey("darkMode")
val PureBlackKey = booleanPreferencesKey("pureBlack")
val PureBlackMiniPlayerKey = booleanPreferencesKey("pureBlackMiniPlayer")
val MiniPlayerOutlineKey = booleanPreferencesKey("miniPlayerOutline")
val UseAppleMusicPlayerKey = booleanPreferencesKey("useAppleMusicPlayer")
val ShowPlayerThumbnailShadowKey = booleanPreferencesKey("showPlayerThumbnailShadow")
val PlayerThumbnailShadowElevationKey = floatPreferencesKey("playerThumbnailShadowElevation")
val SelectedFontKey = stringPreferencesKey("selected_font")

enum class AppFont(val value: String) {
    SYSTEM("system"),
    GOOGLE_SANS("google_sans"),
    SANS_FLEX("sans_flex"),
    OUTFIT("outfit"),
    PLUS_JAKARTA_SANS("plus_jakarta_sans");

    companion object {
        fun fromValue(value: String): AppFont = entries.find { it.value == value } ?: SYSTEM
    }
}

val DensityScaleKey = floatPreferencesKey("density_scale_factor")
val CustomDensityScaleKey = floatPreferencesKey("custom_density_scale_value")

enum class DensityScale(val value: Float, val label: String) {
    NATIVE(1.0f, "Native (100%)"),
    SLIGHTLY_COMPACT(0.85f, "Slightly Compact (85%)"),
    COMPACT(0.75f, "Compact (75%)"),
    VERY_COMPACT(0.65f, "Very Compact (65%)"),
    ULTRA_COMPACT(0.55f, "Ultra Compact (55%)");

    companion object {
        fun fromValue(value: Float): DensityScale = entries.find { it.value == value } ?: NATIVE
    }
}

val DefaultOpenTabKey = stringPreferencesKey("defaultOpenTab")
val SlimNavBarKey = booleanPreferencesKey("slimNavBar")
val GridItemsSizeKey = stringPreferencesKey("gridItemSize")
/** 0 = auto (GridColumnMinWidth-driven), else a fixed column count for every adaptive grid. */
val GridColumnsOverrideKey = intPreferencesKey("gridColumnsOverride")
/** Spacing between grid tiles, in dp. Drives GridItem's per-tile padding in Items.kt. */
val GridSpacingKey = intPreferencesKey("gridSpacingDp")
/** 0 = use the Big/Small toggle (GridItemsSizeKey), else an explicit card height in dp. */
val GridCardHeightOverrideKey = intPreferencesKey("gridCardHeightOverrideDp")
/** 0 = auto (width-tiered), else a fixed column count for the Home Speed Dial grid. */
val SpeedDialColumnsOverrideKey = intPreferencesKey("speedDialColumnsOverride")
/** Forces a flat black background (no blurred artwork) on Artist/Album/Playlist/Search hero screens. */
val PureBlackHeroBackgroundKey = booleanPreferencesKey("pureBlackHeroBackground")
val SliderStyleKey = stringPreferencesKey("sliderStyle")
val SquigglySliderKey = booleanPreferencesKey("squigglySlider")
val SwipeToSongKey = booleanPreferencesKey("SwipeToSong")
val SwipeToRemoveSongKey = booleanPreferencesKey("SwipeToRemoveSong")
val UseNewPlayerDesignKey= booleanPreferencesKey("useNewPlayerDesign")
val UseNewMiniPlayerDesignKey = booleanPreferencesKey("useNewMiniPlayerDesign")
val ShowUpNextKey = booleanPreferencesKey("showUpNext")
/** Tab view only: caps the expanded player to a phone-like width instead of stretching
 *  it across the wide screen. The mini player stays full-width either way. */
val CompactPlayerInTabViewKey = booleanPreferencesKey("compactPlayerInTabView")
/** Hides the heart/favorite badge on Home's song and album rows specifically —
 *  other screens (Library, search, playlists) are unaffected. */
val HideHomeFavoriteIconKey = booleanPreferencesKey("hideHomeFavoriteIcon")
val HidePlayerThumbnailKey = booleanPreferencesKey("hidePlayerThumbnail")
val ThumbnailCornerRadiusKey = floatPreferencesKey("thumbnailCornerRadius")
val CropAlbumArtKey = booleanPreferencesKey("cropAlbumArt")
val SeekExtraSeconds = booleanPreferencesKey("seekExtraSeconds")
val PauseOnMute = booleanPreferencesKey("pauseOnMute")
val ResumeOnBluetoothConnectKey = booleanPreferencesKey("resumeOnBluetoothConnect")
val KeepScreenOn = booleanPreferencesKey("keepScreenOn")
val DeveloperModeKey = booleanPreferencesKey("developerMode")
val EnableSettingsPopupKey = booleanPreferencesKey("enableSettingsPopup")

enum class SliderStyle {
    DEFAULT,
    WAVY,
    SLIM,
    WAVEFORM
}

const val SYSTEM_DEFAULT = "SYSTEM_DEFAULT"
val AppLanguageKey = stringPreferencesKey("appLanguage")
val ContentLanguageKey = stringPreferencesKey("contentLanguage")
val ContentCountryKey = stringPreferencesKey("contentCountry")
val SuggestionRegionKey = stringPreferencesKey("suggestionRegion")
val EnableKugouKey = booleanPreferencesKey("enableKugou")
val EnableLrcLibKey = booleanPreferencesKey("enableLrclib")
val EnableBetterLyricsKey = booleanPreferencesKey("enableBetterLyrics")
val EnableSimpMusicKey = booleanPreferencesKey("enableSimpMusic")
val EnableYouLyPlusKey = booleanPreferencesKey("enableYouLyPlus")
val EnablePaxsenixKey = booleanPreferencesKey("enablePaxsenix")
val EnableMusixmatchKey = booleanPreferencesKey("enableMusixmatch")
val HideExplicitKey = booleanPreferencesKey("hideExplicit")
val HideVideoSongsKey = booleanPreferencesKey("hideVideoSongs")
val DataSaverEnabledKey = booleanPreferencesKey("dataSaverEnabled")

// Local-only mode: one switch that rewires Home, Library and Search to the
// on-device library. The screens and routes stay exactly where they are — only
// what they read changes — so turning it off restores the normal app instantly.
/**
 * Home's local Albums shelf ordered by release year (true) rather than A-Z.
 *
 * Year comes from the file's own tag via MediaStore; untagged albums sort last.
 */
val LocalAlbumsByYearKey = booleanPreferencesKey("localAlbumsByYear")
val LocalOnlyModeKey = booleanPreferencesKey("localOnlyMode")
/** Folder paths (LocalFolderIndex.Folder.path) excluded from local scanning, joined by "". Empty = nothing excluded. */
val LocalExcludedFoldersKey = stringPreferencesKey("localExcludedFolders") // delimiter-joined paths; see LocalFolderExclusion.kt
val ListenBrainzEnabledKey = booleanPreferencesKey("listenbrainz_enabled")
val ListenBrainzTokenKey = stringPreferencesKey("listenbrainz_token")
val HideYoutubeShortsKey = booleanPreferencesKey("hideYoutubeShorts")
val ShowArtistDescriptionKey = booleanPreferencesKey("showArtistDescription")
val ShowArtistSubscriberCountKey = booleanPreferencesKey("showArtistSubscriberCount")
val ShowMonthlyListenersKey = booleanPreferencesKey("showMonthlyListeners")
val ShowArtistVideoKey = booleanPreferencesKey("showArtistVideo")
val ShowArtistBackgroundVideoKey = booleanPreferencesKey("showArtistBackgroundVideo")
val ProxyEnabledKey = booleanPreferencesKey("proxyEnabled")
val ProxyUrlKey = stringPreferencesKey("proxyUrl")
val ProxyTypeKey = stringPreferencesKey("proxyType")
val ProxyUsernameKey = stringPreferencesKey("proxyUsername")
val ProxyPasswordKey = stringPreferencesKey("proxyPassword")
val YtmSyncKey = booleanPreferencesKey("ytmSync")
val SelectedYtmPlaylistsKey = stringPreferencesKey("selectedYtmPlaylists")

val AudioQualityKey = stringPreferencesKey("audioQuality")
val IpVersionKey = stringPreferencesKey("ipVersion")

enum class AudioQuality {
    AUTO,
    HIGH,
    LOW,
}

val AudioOffload = booleanPreferencesKey("enableOffload")

// JioSaavn streaming
val EnableSaavnStreamingKey = booleanPreferencesKey("enableSaavnStreaming")
val SaavnAudioQualityKey    = stringPreferencesKey("saavnAudioQuality")
/** On (default): a JioSaavn miss (no match, no stream URL) falls through to a normal
 *  YouTube fetch, so playback never just fails. Off: a miss fails the playback attempt
 *  instead, for a user who wants Saavn-or-nothing rather than a silent audio swap. */
val SaavnFallbackToYouTubeKey = booleanPreferencesKey("saavnFallbackToYouTube")
/** Circuit breaker: on forces the quality picked above no matter what — a lossless-capable
 *  Spine module or Tidal will never silently upgrade the stream to FLAC. Off (default) lets
 *  those sources win when enabled, same as before this switch existed. */
val ForceSelectedQualityKey = booleanPreferencesKey("forceSelectedQuality")

// Lossless (TIDAL via hifi-api). Opt-in, off by default. Streaming only for now.
val EnableTidalStreamingKey = booleanPreferencesKey("enableTidalStreaming")
val TidalQualityKey         = stringPreferencesKey("tidalQuality")
val TidalInstanceUrlKey     = stringPreferencesKey("tidalInstanceUrl")

enum class TidalQuality {
    LOSSLESS,   // 16-bit/44.1kHz FLAC — direct URL, ships now
    HI_RES;     // 24-bit — DASH, not wired yet (inert)

    fun toApiValue() = when (this) {
        LOSSLESS -> "LOSSLESS"
        HI_RES   -> "HI_RES_LOSSLESS"
    }

    fun toLabel() = when (this) {
        LOSSLESS -> "Lossless (16-bit FLAC)"
        HI_RES   -> "Hi-Res (24-bit)"
    }
}

enum class SaavnAudioQuality {
    QUALITY_320,
    QUALITY_160,
    QUALITY_96;

    fun toApiValue() = when (this) {
        QUALITY_320 -> "320kbps"
        QUALITY_160 -> "160kbps"
        QUALITY_96  -> "96kbps"
    }

    fun toLabel() = when (this) {
        QUALITY_320 -> "High (320 kbps)"
        QUALITY_160 -> "Medium (160 kbps)"
        QUALITY_96  -> "Low (96 kbps)"
    }
}

val PersistentQueueKey = booleanPreferencesKey("persistentQueue")
val PersistentShuffleAcrossQueuesKey = booleanPreferencesKey("persistentShuffleAcrossQueues")
val RememberShuffleAndRepeatKey = booleanPreferencesKey("rememberShuffleAndRepeat")
val ShuffleModeKey = booleanPreferencesKey("shuffleMode")
val SkipSilenceKey = booleanPreferencesKey("skipSilence")
val SkipSilenceInstantKey = booleanPreferencesKey("skipSilenceInstant")
val AudioNormalizationKey = booleanPreferencesKey("audioNormalization")
val AutoLoadMoreKey = booleanPreferencesKey("autoLoadMore")
val DisableLoadMoreWhenRepeatAllKey = booleanPreferencesKey("disableLoadMoreWhenRepeatAll")
val AutoDownloadOnLikeKey = booleanPreferencesKey("autoDownloadOnLike")
val SimilarContent = booleanPreferencesKey("similarContent")
val AutoSkipNextOnErrorKey = booleanPreferencesKey("autoSkipNextOnError")
val StopMusicOnTaskClearKey = booleanPreferencesKey("stopMusicOnTaskClear")
val ShufflePlaylistFirstKey = booleanPreferencesKey("shufflePlaylistFirst")
val PreventDuplicateTracksInQueueKey = booleanPreferencesKey("preventDuplicateTracksInQueue")
val CrossfadeEnabledKey = booleanPreferencesKey("crossfadeEnabled")
val CrossfadeDurationKey = floatPreferencesKey("crossfadeDuration")
val CrossfadeGaplessKey = booleanPreferencesKey("crossfadeGapless")

val MaxImageCacheSizeKey = intPreferencesKey("maxImageCacheSize")
val MaxSongCacheSizeKey = intPreferencesKey("maxSongCacheSize")

val PauseListenHistoryKey = booleanPreferencesKey("pauseListenHistory")
val PauseSearchHistoryKey = booleanPreferencesKey("pauseSearchHistory")
val DisableScreenshotKey = booleanPreferencesKey("disableScreenshot")

val DiscordTokenKey = stringPreferencesKey("discordToken")
val DiscordInfoDismissedKey = booleanPreferencesKey("discordInfoDismissed")
val DiscordUsernameKey = stringPreferencesKey("discordUsername")
val DiscordNameKey = stringPreferencesKey("discordName")
val EnableDiscordRPCKey = booleanPreferencesKey("discordRPCEnable")
val DiscordUseDetailsKey = booleanPreferencesKey("discordUseDetails")
val DiscordAvatarKey = stringPreferencesKey("discordAvatar")
val DiscordStatusKey = stringPreferencesKey("discordStatus")
val DiscordButton1TextKey = stringPreferencesKey("discordButton1Text")
val DiscordButton1VisibleKey = booleanPreferencesKey("discordButton1Visible")
val DiscordButton2TextKey = stringPreferencesKey("discordButton2Text")
val DiscordButton2VisibleKey = booleanPreferencesKey("discordButton2Visible")
val DiscordActivityTypeKey = stringPreferencesKey("discordActivityType")
val DiscordActivityNameKey = stringPreferencesKey("discordActivityName")
val DiscordAdvancedModeKey = booleanPreferencesKey("discordAdvancedMode")

// Google Cast
val EnableGoogleCastKey = booleanPreferencesKey("enableGoogleCast")

// Listen Together
val ListenTogetherServerUrlKey = stringPreferencesKey("listenTogetherServerUrl")
val ListenTogetherUsernameKey = stringPreferencesKey("listenTogetherUsername")
val EnableListenTogetherKey = booleanPreferencesKey("enableListenTogether")
val ListenTogetherAutoApprovalKey = booleanPreferencesKey("listenTogetherAutoApproval")
/** Host-side: land guests' track suggestions in the queue without asking. */
val ListenTogetherAutoAddSuggestionsKey = booleanPreferencesKey("listenTogetherAutoAddSuggestions")
val ListenTogetherSyncVolumeKey = booleanPreferencesKey("listenTogetherSyncVolume")
val ListenTogetherSmartResyncKey = booleanPreferencesKey("listenTogetherSmartResync")
val ListenTogetherBlockedUsersKey = stringPreferencesKey("listenTogetherBlockedUsers")
val ListenTogetherInTopBarKey = booleanPreferencesKey("listenTogetherInTopBar")
// Session persistence for reconnection
val ListenTogetherSessionTokenKey = stringPreferencesKey("listenTogetherSessionToken")
val ListenTogetherRoomCodeKey = stringPreferencesKey("listenTogetherRoomCode")
val ListenTogetherUserIdKey = stringPreferencesKey("listenTogetherUserId")
val ListenTogetherIsHostKey = booleanPreferencesKey("listenTogetherIsHost")
val ListenTogetherSessionTimestampKey = longPreferencesKey("listenTogetherSessionTimestamp")

val LastFMSessionKey = stringPreferencesKey("lastfmSession")
val LastFMUsernameKey = stringPreferencesKey("lastfmUsername")
val SpotifySessionKey = stringPreferencesKey("spotifySession")
val EnableLastFMScrobblingKey = booleanPreferencesKey("lastfmScrobblingEnable")
val LastFMUseNowPlaying = booleanPreferencesKey("lastfmUseNowPlaying")

val LastFMUseSendLikes = booleanPreferencesKey("lastfmUseSendLikes")

val ScrobbleDelayPercentKey = floatPreferencesKey("scrobbleDelayPercent")
val ScrobbleMinSongDurationKey = intPreferencesKey("scrobbleMinSongDuration")
val ScrobbleDelaySecondsKey = intPreferencesKey("scrobbleDelaySeconds")

val ChipSortTypeKey = stringPreferencesKey("chipSortType")
val SongSortTypeKey = stringPreferencesKey("songSortType")
val SongSortDescendingKey = booleanPreferencesKey("songSortDescending")
val PlaylistSongSortTypeKey = stringPreferencesKey("playlistSongSortType")
val PlaylistSongSortDescendingKey = booleanPreferencesKey("playlistSongSortDescending")
val AutoPlaylistSongSortTypeKey = stringPreferencesKey("autoPlaylistSongSortType")
val AutoPlaylistSongSortDescendingKey = booleanPreferencesKey("autoPlaylistSongSortDescending")
val ArtistSortTypeKey = stringPreferencesKey("artistSortType")
val ArtistSortDescendingKey = booleanPreferencesKey("artistSortDescending")
val AlbumSortTypeKey = stringPreferencesKey("albumSortType")
val AlbumSortDescendingKey = booleanPreferencesKey("albumSortDescending")
val LocalSongSortTypeKey = stringPreferencesKey("localSongSortType")
val LocalSongSortDescendingKey = booleanPreferencesKey("localSongSortDescending")
val PlaylistSortTypeKey = stringPreferencesKey("playlistSortType")
val PlaylistSortDescendingKey = booleanPreferencesKey("playlistSortDescending")
val AddToPlaylistSortTypeKey = stringPreferencesKey("addToPlaylistSortType")
val AddToPlaylistSortDescendingKey = booleanPreferencesKey("addToPlaylistSortDescending")
val ArtistSongSortTypeKey = stringPreferencesKey("artistSongSortType")
val ArtistSongSortDescendingKey = booleanPreferencesKey("artistSongSortDescending")
val MixSortTypeKey = stringPreferencesKey("mixSortType")
val MixSortDescendingKey = booleanPreferencesKey("albumSortDescending")

val SongFilterKey = stringPreferencesKey("songFilter")
val ArtistFilterKey = stringPreferencesKey("artistFilter")
val AlbumFilterKey = stringPreferencesKey("albumFilter")

val LastLikeSongSyncKey = longPreferencesKey("last_like_song_sync")
val LastLibSongSyncKey = longPreferencesKey("last_library_song_sync")
val LastAlbumSyncKey = longPreferencesKey("last_album_sync")
val LastArtistSyncKey = longPreferencesKey("last_artist_sync")
val LastPlaylistSyncKey = longPreferencesKey("last_playlist_sync")
val LastFullSyncKey = longPreferencesKey("last_full_sync")

/** browseIds the user deleted locally whose remote YouTube delete hasn't been
 *  confirmed gone yet — the playlist sync consults this so it doesn't
 *  resurrect a playlist the user just deleted while the remote delete is
 *  still in flight (or retried after a failure), and clears an id once the
 *  remote library listing no longer includes it. */
val PendingPlaylistDeletesKey = stringSetPreferencesKey("pending_playlist_deletes")

// Sync cooldown in seconds (30 minutes)
const val SYNC_COOLDOWN = 30 * 60L

val ArtistViewTypeKey = stringPreferencesKey("artistViewType")
val AlbumViewTypeKey = stringPreferencesKey("albumViewType")
val PlaylistViewTypeKey = stringPreferencesKey("playlistViewType")

val PlaylistEditLockKey = booleanPreferencesKey("playlistEditLock")
val QuickPicksKey = stringPreferencesKey("discover")
val PreferredLyricsProviderKey = stringPreferencesKey("lyricsProvider")
val LyricsProviderOrderKey = stringPreferencesKey("lyricsProviderOrder")
val QueueEditLockKey = booleanPreferencesKey("queueEditLock")
val ShowWrappedCardKey = booleanPreferencesKey("show_wrapped_card")
val WrappedSeenKey = booleanPreferencesKey("wrapped_seen")
val RandomizeHomeOrderKey = booleanPreferencesKey("randomizeHomeOrder")
val AlbumCanvasEnabledKey = booleanPreferencesKey("albumCanvasEnabled")

val ShowLikedPlaylistKey = booleanPreferencesKey("show_liked_playlist")
val ShowDownloadedPlaylistKey = booleanPreferencesKey("show_downloaded_playlist")
val ShowTopPlaylistKey = booleanPreferencesKey("show_top_playlist")
val ShowCachedPlaylistKey = booleanPreferencesKey("show_cached_playlist")
val ShowUploadedPlaylistKey = booleanPreferencesKey("show_uploaded_playlist")
val ShowLocalPlaylistKey = booleanPreferencesKey("show_local_playlist")
val ShowAudioQualityBadgeKey = booleanPreferencesKey("show_audio_quality_badge")
val ShowCommentButtonKey = booleanPreferencesKey("show_comment_button")
val ShowHistoryButtonKey = booleanPreferencesKey("show_history_button")
val ShowStatsButtonKey = booleanPreferencesKey("show_stats_button")
val MiniPlayerWaveformKey = booleanPreferencesKey("mini_player_waveform")
val BrandFontEnabledKey = booleanPreferencesKey("brandFontEnabled")
val LibraryIconsOnlyKey = booleanPreferencesKey("libraryIconsOnly")

enum class LibraryViewType {
    LIST,
    GRID,
    ;

    fun toggle() =
        when (this) {
            LIST -> GRID
            GRID -> LIST
        }
}

enum class SongFilter {
    LIBRARY,
    LIKED,
    DOWNLOADED,
    UPLOADED,
    LOCAL
}

enum class ArtistFilter {
    LIBRARY,
    LIKED,
    LOCAL,
}

enum class AlbumFilter {
    LIBRARY,
    LIKED,
    UPLOADED,
    LOCAL,
}

enum class SongSortType {
    CREATE_DATE,
    NAME,
    ARTIST,
    PLAY_TIME,
}

enum class PlaylistSongSortType {
    CUSTOM,
    CREATE_DATE,
    NAME,
    ARTIST,
    PLAY_TIME,
}

enum class AutoPlaylistSongSortType {
    CREATE_DATE,
    NAME,
    ARTIST,
    PLAY_TIME,
}

enum class ArtistSortType {
    CREATE_DATE,
    NAME,
    SONG_COUNT,
    PLAY_TIME,
}

enum class ArtistSongSortType {
    CREATE_DATE,
    NAME,
    PLAY_TIME,
}

enum class AlbumSortType {
    CREATE_DATE,
    NAME,
    ARTIST,
    YEAR,
    SONG_COUNT,
    LENGTH,
    PLAY_TIME,
}

enum class PlaylistSortType {
    CREATE_DATE,
    NAME,
    SONG_COUNT,
    LAST_UPDATED,
}

enum class MixSortType {
    CREATE_DATE,
    NAME,
    LAST_UPDATED,
}

enum class GridItemSize {
    BIG,
    SMALL,
}

enum class MyTopFilter {
    ALL_TIME,
    DAY,
    WEEK,
    MONTH,
    YEAR,
    ;

    fun toTimeMillis(): Long =
        when (this) {
            DAY ->
                LocalDateTime
                    .now()
                    .minusDays(1)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli()

            WEEK ->
                LocalDateTime
                    .now()
                    .minusWeeks(1)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli()

            MONTH ->
                LocalDateTime
                    .now()
                    .minusMonths(1)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli()

            YEAR ->
                LocalDateTime
                    .now()
                    .minusMonths(12)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli()

            ALL_TIME -> 0
        }
}

enum class QuickPicks {
    QUICK_PICKS,
    LAST_LISTEN,
}

enum class PreferredLyricsProvider {
    LRCLIB,
    KUGOU,
    BETTER_LYRICS,
    SIMPMUSIC,
    YOULYPLUS,
    PAXSENIX,
}

enum class PlayerButtonsStyle {
    DEFAULT,
    PRIMARY,
    TERTIARY
}

enum class PlayerBackgroundStyle {
    DEFAULT,
    GRADIENT,
    BLUR,
    GLOW_ANIMATED,
    APPLE_MUSIC,
    LIVE_MESH,
    STATIC,
    CUSTOM_GRADIENT,
}

/** Shape + motion applied to the player artwork. */
enum class PlayerArtworkStyle {
    CARD,
    VINYL,
    CLOVER,
}

val PlayerArtworkStyleKey = stringPreferencesKey("playerArtworkStyle")
val PlayerStaticColorKey = intPreferencesKey("playerStaticColor")

/** Comma-separated ARGB ints; see ui/theme/PlayerGradient.kt for the codec. */
val PlayerGradientStopsKey = stringPreferencesKey("playerGradientStops")
val PlayerGradientAngleKey = floatPreferencesKey("playerGradientAngle")

val TopSize = stringPreferencesKey("topSize")
val HistoryDuration = floatPreferencesKey("historyDuration")

val PlayerButtonsStyleKey = stringPreferencesKey("player_buttons_style")
val PlayerBackgroundStyleKey = stringPreferencesKey("playerBackgroundStyle")
val MiniPlayerBackgroundStyleKey = stringPreferencesKey("miniPlayerBackgroundStyle")
val ShowLyricsKey = booleanPreferencesKey("showLyrics")
val SwipeLyricsKey = booleanPreferencesKey("swipeLyrics")
val EnableLyricsThumbnailPlayPauseKey = booleanPreferencesKey("enableLyricsThumbnailPlayPause")
val LyricsTextPositionKey = stringPreferencesKey("lyricsTextPosition")
val LyricsClickKey = booleanPreferencesKey("lyricsClick")
val LyricsScrollKey = booleanPreferencesKey("lyricsScrollKey")
val LyricsRomanizeJapaneseKey = booleanPreferencesKey("lyricsRomanizeJapanese")
val LyricsRomanizeKoreanKey = booleanPreferencesKey("lyricsRomanizeKorean")
val LyricsRomanizeChineseKey = booleanPreferencesKey("lyricsRomanizeChinese")
val LyricsRomanizeRussianKey = booleanPreferencesKey("lyricsRomanizeRussian")
val LyricsRomanizeUkrainianKey = booleanPreferencesKey("lyricsRomanizeUkrainian")
val LyricsRomanizeSerbianKey = booleanPreferencesKey("lyricsRomanizeSerbian")
val LyricsRomanizeBulgarianKey = booleanPreferencesKey("lyricsRomanizeBulgarian")
val LyricsRomanizeBelarusianKey = booleanPreferencesKey("lyricsRomanizeBelarusian")
val LyricsRomanizeKyrgyzKey = booleanPreferencesKey("lyricsRomanizeKyrgyz")
val LyricsRomanizeMacedonianKey = booleanPreferencesKey("lyricsRomanizeMacedonian")
val LyricsRomanizeHindiKey = booleanPreferencesKey("lyricsRomanizeHindi")
val LyricsRomanizePunjabiKey = booleanPreferencesKey("lyricsRomanizePunjabi")
val LyricsRomanizeAsMainKey = booleanPreferencesKey("lyricsRomanizeAsMain")
val LyricsRomanizeCyrillicByLineKey = booleanPreferencesKey("lyricsRomanizeCyrillicByLine")
val TranslateLyricsKey = booleanPreferencesKey("translateLyrics")
val OpenRouterApiKey = stringPreferencesKey("openRouterApiKey")
val AiProviderKey = stringPreferencesKey("aiProvider")
val OpenRouterBaseUrlKey = stringPreferencesKey("openRouterBaseUrl")
val OpenRouterModelKey = stringPreferencesKey("openRouterModel")
val TranslateModeKey = stringPreferencesKey("translateMode")
val TranslateLanguageKey = stringPreferencesKey("translateLanguage")
val DeeplApiKey = stringPreferencesKey("deeplApiKey")
val DeeplFormalityKey = stringPreferencesKey("deeplFormality")
val LyricsGlowEffectKey = booleanPreferencesKey("lyricsGlowEffect")
val AppleMusicLyricsBlurKey = booleanPreferencesKey("appleMusicLyricsBlur")
val LyricsStandardBlurKey = booleanPreferencesKey("lyricsStandardBlur")

val LiquidGlassGlobalEnabledKey = booleanPreferencesKey("liquidGlassGlobalEnabled")
val LiquidGlassAdaptiveContrastKey = booleanPreferencesKey("liquidGlassAdaptiveContrast")
val LiquidGlassTextColorKey = intPreferencesKey("liquidGlassTextColor")
val LiquidGlassSurfaceTintColorKey = intPreferencesKey("liquidGlassSurfaceTintColor")
val LiquidGlassSurfaceOpacityKey = floatPreferencesKey("liquidGlassSurfaceOpacity")

/** Specular rim colour as an ARGB int. 0 = default white. */
/** Which [com.nexamusic.app.ui.component.GlassStyle] every glass surface uses. */
val LiquidGlassStyleKey = stringPreferencesKey("liquidGlassStyle")

/** Selection puck wash colour as an ARGB int. 0 = adaptive to the theme. */
val LiquidGlassPuckColorKey = intPreferencesKey("liquidGlassPuckColor")

/** Selection puck wash opacity at rest, 0..1. */
val LiquidGlassPuckOpacityKey = floatPreferencesKey("liquidGlassPuckOpacity")

val LiquidGlassHighlightColorKey = intPreferencesKey("liquidGlassHighlightColor")

/** Specular rim opacity, 0..1. */
val LiquidGlassHighlightOpacityKey = floatPreferencesKey("liquidGlassHighlightOpacity")
val LiquidGlassVibrancyKey = floatPreferencesKey("liquidGlassVibrancy")
val LiquidGlassBlurRadiusKey = floatPreferencesKey("liquidGlassBlurRadius")
val LiquidGlassLensHeightKey = floatPreferencesKey("liquidGlassLensHeight")
val LiquidGlassLensAmountKey = floatPreferencesKey("liquidGlassLensAmount")
val LiquidGlassChromaticAberrationKey = booleanPreferencesKey("liquidGlassChromaticAberration")
val LiquidGlassDepthEffectKey = booleanPreferencesKey("liquidGlassDepthEffect")
val LiquidGlassPlayerEnabledKey = booleanPreferencesKey("liquidGlassPlayerEnabled")
val LiquidGlassMiniPlayerEnabledKey = booleanPreferencesKey("liquidGlassMiniPlayerEnabled")
val LiquidGlassNavBarEnabledKey = booleanPreferencesKey("liquidGlassNavBarEnabled")
val LiquidGlassSidePanelEnabledKey = booleanPreferencesKey("liquidGlassSidePanelEnabled")
val LiquidGlassSidePanelVibrancyKey = floatPreferencesKey("liquidGlassSidePanelVibrancy")
val LiquidGlassSidePanelBlurRadiusKey = floatPreferencesKey("liquidGlassSidePanelBlurRadius")
val LiquidGlassSidePanelLensHeightKey = floatPreferencesKey("liquidGlassSidePanelLensHeight")
val LiquidGlassSidePanelLensAmountKey = floatPreferencesKey("liquidGlassSidePanelLensAmount")
val LiquidGlassSidePanelColorKey = intPreferencesKey("liquidGlassSidePanelColor")
val LiquidGlassSidePanelSurfaceOpacityKey = floatPreferencesKey("liquidGlassSidePanelSurfaceOpacity")
val LiquidGlassSidePanelTextColorKey = intPreferencesKey("liquidGlassSidePanelTextColor")
val SideBarCollapsedKey = booleanPreferencesKey("sideBarCollapsed")
val PlayerLayoutOrderKey = stringPreferencesKey("playerLayoutOrder")
val PlayerLayoutHiddenSlotsKey = stringPreferencesKey("playerLayoutHiddenSlots")
val AutoDjMixingEnabledKey = booleanPreferencesKey("autoDjMixingEnabled")

/** Off by default: Auto-DJ on its own means beatmatching and a transparent mix,
 *  which is what most listeners want. This adds loop rolls, echo tails and
 *  turntable brakes on transitions the engine judges would otherwise expose a
 *  seam. */
val CreativeTransitionsEnabledKey = booleanPreferencesKey("creativeTransitionsEnabled")
/** Off (default) = classic two-tap (open inline lyrics, then a separate tap to go fullscreen). On = one tap opens/closes fullscreen lyrics directly. */
val OneTapFullscreenLyricsKey = booleanPreferencesKey("oneTapFullscreenLyrics")
/** true (default) = player controls collapse upward (shrink toward the top) when entering full-screen lyrics; false = collapse downward. */
val FullscreenLyricsCollapseTopKey = booleanPreferencesKey("fullscreenLyricsCollapseTop")
val HideVolumeBarKey = booleanPreferencesKey("hideVolumeBar")
val AppleMusicUiKey = booleanPreferencesKey("appleMusicUi")
/** false (default) = simple iOS-style mini bar (thumbnail/title/seek bar/play/forward); true = multi-icon tab-style row. */
val MiniBarTabStyleKey = booleanPreferencesKey("miniBarTabStyle")

/** true (default) = Home's floating mic (sound search) + shuffle FAB pair shows; false = hidden. */
val ShowHomeFabKey = booleanPreferencesKey("showHomeFab")

/** 0 (default) = HomeHeroCard's normal 4:3 aspect-ratio height; else explicit dp height. */
val HomeHeroCardHeightOverrideKey = intPreferencesKey("homeHeroCardHeightOverrideDp")
/** 0 (default) = SpeedDialGridItem's normal square (height == tile width); else explicit dp height. */
val SpeedDialCardHeightOverrideKey = intPreferencesKey("speedDialCardHeightOverrideDp")
/** 0 (default) = each Home card's own default corner radius (12dp tiles, 28dp hero); else a shared dp radius applied to both. */
val HomeCardCornerRadiusOverrideKey = intPreferencesKey("homeCardCornerRadiusOverrideDp")

/**
 * The big hero card at the top of Home. Off by default: the vertical grid below
 * it shows the same content without spending a screen height on one track.
 */
val HomeHeroCardEnabledKey = booleanPreferencesKey("homeHeroCardEnabled")

/**
 * User's own Home section order: section ids, most-important first, newline separated.
 *
 * Stored as ids rather than indices so it survives sections being added, removed or
 * renamed between versions — an unknown id is ignored and a section missing from the
 * list falls back to its default weight, which means a release that adds a new section
 * does not scramble everyone's saved arrangement.
 *
 * Empty means "no custom order", which is distinct from "an order that happens to match
 * the default": clearing it re-enables the default weights for good.
 */
/**
 * Newline-separated ids of Home sections the user switched off, same id space as
 * [HomeSectionOrderKey]. Absent means shown, so a section added by a later release
 * is visible by default rather than silently hidden.
 */
val HomeSectionHiddenKey = stringPreferencesKey("homeSectionHidden")
val HomeSectionOrderKey = stringPreferencesKey("homeSectionOrder")

/** Show YouTube's mood/genre filter chips (Energize, Relax, Feel good) in Home's top bar. */
val ShowHomeMoodFiltersKey = booleanPreferencesKey("showHomeMoodFilters")

/** Columns in Home's vertical "Keep Listening" grid. 0 (default) = 2. */
val HomeGridColumnsOverrideKey = intPreferencesKey("homeGridColumnsOverride")

/**
 * Forces the tablet sidebar layout on a device that would otherwise get the
 * bottom bar, so the wide layout can be tried out on a phone.
 */
val ForceTabletLayoutKey = booleanPreferencesKey("forceTabletLayout")

val HomeBackgroundEnabledKey = booleanPreferencesKey("homeBackgroundEnabled")
val HomeBackgroundPathKey = stringPreferencesKey("homeBackgroundPath")
val HomeBackgroundBlurKey = floatPreferencesKey("homeBackgroundBlur")
val HomeBackgroundDimKey = floatPreferencesKey("homeBackgroundDim")
val HomeBackgroundAnimateKey = booleanPreferencesKey("homeBackgroundAnimate")

/** 0.3..1 scale of the device's own screen resolution used when storing a
 *  picked background image. 1 (default) = full screen resolution. */
val HomeBackgroundQualityKey = floatPreferencesKey("homeBackgroundQuality")

/**
 * App-wide background/text color, independent of Liquid Glass — applies to
 * Home's plain background (when no custom image is set) and the shared nav
 * chrome's fallback color when Liquid Glass is off. 0 (default) means
 * "unset": every call site keeps its exact current fallback color, so a user
 * who never touches this setting sees no change at all.
 */
val AppBackgroundColorKey = intPreferencesKey("appBackgroundColor")
val AppTextColorKey = intPreferencesKey("appTextColor")
val LibraryBackgroundModeKey = stringPreferencesKey("libraryBackgroundMode")
/** True when HomeBackgroundPathKey points at a video file instead of an image. */
val HomeBackgroundIsVideoKey = booleanPreferencesKey("homeBackgroundIsVideo")
val IosOverscrollKey = booleanPreferencesKey("iosOverscroll")

val CustomFontEnabledKey = booleanPreferencesKey("customFontEnabled")
val CustomFontPathKey = stringPreferencesKey("customFontPath")
val CustomFontNameKey = stringPreferencesKey("customFontName")
/** When on, the installed custom font renders only on artist names instead of app-wide. */
val CustomFontArtistOnlyKey = booleanPreferencesKey("customFontArtistOnly")

// 8spine Modules
val ModuleSourcesKey = stringPreferencesKey("moduleSources")
val EnabledModulesKey = stringPreferencesKey("enabledModules")
val ModuleSettingsKey = stringPreferencesKey("moduleSettings")
val FetchedModulesKey = stringPreferencesKey("fetchedModules")


val LyricsAnimationStyleKey = stringPreferencesKey("lyricsAnimationStyle")
enum class LyricsAnimationStyle {
    NONE,
    FADE,
    GLOW,
    SLIDE,
    KARAOKE,
    APPLE,
    APPLE_V2,
    VIVIMUSIC_1,
    LYRICS_V2,
    METRO_LYRICS,
}

val LyricsTextSizeKey = floatPreferencesKey("lyricsTextSize")
val LyricsLineSpacingKey = floatPreferencesKey("lyricsLineSpacing")

val PlayerVolumeKey = floatPreferencesKey("playerVolume")
val RepeatModeKey = intPreferencesKey("repeatMode")

val SearchSourceKey = stringPreferencesKey("searchSource")
val SwipeThumbnailKey = booleanPreferencesKey("swipeThumbnail")
val CanvasThumbnailAnimationKey = booleanPreferencesKey("canvasThumbnailAnimation")
val CanvasSourceKey = stringPreferencesKey("canvasSource")

enum class CanvasSource {
    AUTO,
    ECHO_MUSIC,
    APPLE_MUSIC,
    VIVIMUSIC,
    TIDAL,
}
val SwipeSensitivityKey = floatPreferencesKey("swipeSensitivity")

enum class SearchSource {
    LOCAL,
    ONLINE,
    ;

    fun toggle() =
        when (this) {
            LOCAL -> ONLINE
            ONLINE -> LOCAL
        }
}

val VisitorDataKey = stringPreferencesKey("visitorData")
val DataSyncIdKey = stringPreferencesKey("dataSyncId")
val InnerTubeCookieKey = stringPreferencesKey("innerTubeCookie")
val AccountNameKey = stringPreferencesKey("accountName")
val AccountEmailKey = stringPreferencesKey("accountEmail")
val AccountChannelHandleKey = stringPreferencesKey("accountChannelHandle")
val UseLoginForBrowse = booleanPreferencesKey("useLoginForBrowse")

/** JSON array of SavedAccount (see SavedAccount.kt) — every channel seen at login, for instant tap-to-switch. */
val SavedAccountsKey = stringPreferencesKey("savedAccounts")

val LanguageCodeToName =
    mapOf(
        "af" to "Afrikaans",
        "az" to "Azərbaycan",
        "id" to "Bahasa Indonesia",
        "ms" to "Bahasa Malaysia",
        "ca" to "Català",
        "cs" to "Čeština",
        "da" to "Dansk",
        "de" to "Deutsch",
        "et" to "Eesti",
        "en-GB" to "English (UK)",
        "en" to "English (US)",
        "es" to "Español (España)",
        "es-419" to "Español (Latinoamérica)",
        "eu" to "Euskara",
        "fil" to "Filipino",
        "fr" to "Français",
        "fr-CA" to "Français (Canada)",
        "gl" to "Galego",
        "hr" to "Hrvatski",
        "zu" to "IsiZulu",
        "is" to "Íslenska",
        "it" to "Italiano",
        "sw" to "Kiswahili",
        "lt" to "Lietuvių",
        "hu" to "Magyar",
        "nl" to "Nederlands",
        "no" to "Norsk",
        "or" to "Odia",
        "uz" to "O‘zbe",
        "pl" to "Polski",
        "pt-PT" to "Português",
        "pt" to "Português (Brasil)",
        "ro" to "Română",
        "sq" to "Shqip",
        "sk" to "Slovenčina",
        "sl" to "Slovenščina",
        "fi" to "Suomi",
        "sv" to "Svenska",
        "bo" to "Tibetan བོད་སྐད།",
        "vi" to "Tiếng Việt",
        "tr" to "Türkçe",
        "bg" to "Български",
        "ky" to "Кыргызча",
        "kk" to "Қазақ Тілі",
        "mk" to "Македонски",
        "mn" to "Монгол",
        "ru" to "Русский",
        "sr" to "Српски",
        "uk" to "Українська",
        "el" to "Ελληνικά",
        "hy" to "Հայերեն",
        "iw" to "עברית",
        "ur" to "اردو",
        "ar" to "العربية",
        "fa" to "فارسی",
        "ne" to "नेपाली",
        "mr" to "मराठी",
        "hi" to "हिन्दी",
        "bn" to "বাংলা",
        "pa" to "ਪੰਜਾਬੀ",
        "gu" to "ગુજરાતી",
        "ta" to "தமிழ்",
        "te" to "తెలుగు",
        "kn" to "ಕನ್ನಡ",
        "ml" to "മലയാളം",
        "si" to "සිංහල",
        "th" to "ภาษาไทย",
        "lo" to "ລາວ",
        "my" to "ဗမာ",
        "ka" to "ქართული",
        "am" to "አማርኛ",
        "km" to "ខ្មែរ",
        "zh-CN" to "中文 (简体)",
        "zh-TW" to "中文 (繁體)",
        "zh-HK" to "中文 (香港)",
        "ja" to "日本語",
        "ko" to "한국어",
    )

val CountryCodeToName =
    mapOf(
        "DZ" to "Algeria",
        "AR" to "Argentina",
        "AU" to "Australia",
        "AT" to "Austria",
        "AZ" to "Azerbaijan",
        "BH" to "Bahrain",
        "BD" to "Bangladesh",
        "BY" to "Belarus",
        "BE" to "Belgium",
        "BO" to "Bolivia",
        "BA" to "Bosnia and Herzegovina",
        "BR" to "Brazil",
        "BG" to "Bulgaria",
        "KH" to "Cambodia",
        "CA" to "Canada",
        "CL" to "Chile",
        "HK" to "Hong Kong",
        "CO" to "Colombia",
        "CR" to "Costa Rica",
        "HR" to "Croatia",
        "CY" to "Cyprus",
        "CZ" to "Czech Republic",
        "DK" to "Denmark",
        "DO" to "Dominican Republic",
        "EC" to "Ecuador",
        "EG" to "Egypt",
        "SV" to "El Salvador",
        "EE" to "Estonia",
        "FI" to "Finland",
        "FR" to "France",
        "GE" to "Georgia",
        "DE" to "Germany",
        "GH" to "Ghana",
        "GR" to "Greece",
        "GT" to "Guatemala",
        "HN" to "Honduras",
        "HU" to "Hungary",
        "IS" to "Iceland",
        "IN" to "India",
        "ID" to "Indonesia",
        "IQ" to "Iraq",
        "IE" to "Ireland",
        "IL" to "Israel",
        "IT" to "Italy",
        "JM" to "Jamaica",
        "JP" to "Japan",
        "JO" to "Jordan",
        "KZ" to "Kazakhstan",
        "KE" to "Kenya",
        "KR" to "South Korea",
        "KW" to "Kuwait",
        "LA" to "Lao",
        "LV" to "Latvia",
        "LB" to "Lebanon",
        "LY" to "Libya",
        "LI" to "Liechtenstein",
        "LT" to "Lithuania",
        "LU" to "Luxembourg",
        "MK" to "Macedonia",
        "MY" to "Malaysia",
        "MT" to "Malta",
        "MX" to "Mexico",
        "ME" to "Montenegro",
        "MA" to "Morocco",
        "NP" to "Nepal",
        "NL" to "Netherlands",
        "NZ" to "New Zealand",
        "NI" to "Nicaragua",
        "NG" to "Nigeria",
        "NO" to "Norway",
        "OM" to "Oman",
        "PK" to "Pakistan",
        "PA" to "Panama",
        "PG" to "Papua New Guinea",
        "PY" to "Paraguay",
        "PE" to "Peru",
        "PH" to "Philippines",
        "PL" to "Poland",
        "PT" to "Portugal",
        "PR" to "Puerto Rico",
        "QA" to "Qatar",
        "RO" to "Romania",
        "RU" to "Russian Federation",
        "SA" to "Saudi Arabia",
        "SN" to "Senegal",
        "RS" to "Serbia",
        "SG" to "Singapore",
        "SK" to "Slovakia",
        "SI" to "Slovenia",
        "ZA" to "South Africa",
        "ES" to "Spain",
        "LK" to "Sri Lanka",
        "SE" to "Sweden",
        "CH" to "Switzerland",
        "TW" to "Taiwan",
        "TZ" to "Tanzania",
        "TH" to "Thailand",
        "TN" to "Tunisia",
        "TR" to "Turkey",
        "UG" to "Uganda",
        "UA" to "Ukraine",
        "AE" to "United Arab Emirates",
        "GB" to "United Kingdom",
        "US" to "United States",
        "UY" to "Uruguay",
        "VE" to "Venezuela (Bolivarian Republic)",
        "VN" to "Vietnam",
        "YE" to "Yemen",
        "ZW" to "Zimbabwe",
    )

val SuggestionRegionSlugToName =
    mapOf(
        "system" to "System Default",
        "us" to "Global (USA)",
        "in" to "India",
        "gb" to "United Kingdom",
        "ca" to "Canada",
        "au" to "Australia",
        "jp" to "Japan",
        "kr" to "South Korea",
        "de" to "Germany",
        "fr" to "France",
        "br" to "Brazil",
        "mx" to "Mexico",
        "ru" to "Russia",
        "it" to "Italy",
        "es" to "Spain",
        "nl" to "Netherlands",
        "se" to "Sweden",
        "no" to "Norway",
        "dk" to "Denmark",
        "fi" to "Finland",
        "pl" to "Poland",
        "tr" to "Turkey",
        "za" to "South Africa",
        "ng" to "Nigeria",
        "id" to "Indonesia",
        "my" to "Malaysia",
        "ph" to "Philippines",
        "th" to "Thailand",
        "vn" to "Vietnam",
        "tw" to "Taiwan",
        "hk" to "Hong Kong",
        "sg" to "Singapore",
        "ar" to "Argentina",
        "co" to "Colombia",
        "cl" to "Chile",
        "pe" to "Peru",
        "eg" to "Egypt",
        "sa" to "Saudi Arabia",
        "ae" to "United Arab Emirates",
        "il" to "Israel"
    )

