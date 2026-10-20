/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:Suppress("DEPRECATION")

package com.convx.music.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.content.BroadcastReceiver
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.database.SQLException
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.media.audiofx.LoudnessEnhancer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.SystemClock

import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_POSITION_DISCONTINUITY
import com.convx.music.playback.smart.TrackAnalyzer
import com.convx.music.playback.smart.TrackAnalysis
import com.convx.music.data.settings.AppSettings
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Timeline
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.mkv.MatroskaExtractor
import androidx.media3.extractor.mp4.FragmentedMp4Extractor
import androidx.media3.extractor.mp4.Mp4Extractor
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.lastfm.LastFM
import com.convx.music.MainActivity
import com.convx.music.R
import com.convx.music.lyrics.LyricsSyncLevel
import com.convx.music.lyrics.LyricsWithProvider
import com.convx.music.lyrics.LyricsProviderRegistry
import com.convx.music.lyrics.LyricsProviderSettings
import com.convx.music.constants.LyricsProviderOrderKey
import com.convx.music.constants.AudioNormalizationKey
import com.convx.music.constants.AudioOffload
import com.convx.music.constants.AudioQualityKey
import com.convx.music.constants.AutoDownloadOnLikeKey
import com.convx.music.constants.EnableTidalStreamingKey
import com.convx.music.constants.EnabledModulesKey
import com.convx.music.constants.ModuleSettingsKey
import com.convx.music.constants.ModuleSourcesKey
import com.convx.music.constants.ForceSelectedQualityKey
import com.convx.music.constants.EnableSaavnStreamingKey
import com.convx.music.constants.SaavnAudioQualityKey
import com.convx.music.constants.SaavnFallbackToYouTubeKey
import com.convx.music.constants.TidalQualityKey
import com.convx.music.constants.TidalInstanceUrlKey
import com.convx.music.constants.AutoLoadMoreKey
import com.convx.music.constants.AutoSkipNextOnErrorKey
import com.convx.music.constants.AutoDjMixingEnabledKey
import com.convx.music.constants.CreativeTransitionsEnabledKey
import com.convx.music.constants.DjReverbTypeKey
import com.convx.music.constants.DjDelayTypeKey
import com.convx.music.constants.CrossfadeDurationKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.DisableLoadMoreWhenRepeatAllKey
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.convx.music.constants.DiscordActivityNameKey
import com.convx.music.constants.DiscordActivityTypeKey
import com.convx.music.constants.DiscordAdvancedModeKey
import com.convx.music.constants.DiscordButton1TextKey
import com.convx.music.constants.DiscordButton1VisibleKey
import com.convx.music.constants.DiscordButton2TextKey
import com.convx.music.constants.DiscordButton2VisibleKey
import com.convx.music.constants.DiscordStatusKey
import com.convx.music.constants.DiscordTokenKey
import com.convx.music.constants.DiscordUseDetailsKey
import com.convx.music.constants.EnableDiscordRPCKey
import com.convx.music.constants.EnableLastFMScrobblingKey
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.ListenBrainzEnabledKey
import com.convx.music.constants.ListenBrainzTokenKey
import com.convx.music.constants.HistoryDuration
import com.convx.music.constants.LastFMUseNowPlaying
import com.convx.music.constants.MediaSessionConstants.CommandToggleLike
import com.convx.music.constants.MediaSessionConstants.CommandToggleRepeatMode
import com.convx.music.constants.MediaSessionConstants.CommandToggleShuffle
import com.convx.music.constants.MediaSessionConstants.CommandToggleStartRadio
import com.convx.music.constants.PauseListenHistoryKey
import com.convx.music.constants.PauseOnMute
import com.convx.music.constants.PersistentQueueKey
import com.convx.music.constants.PersistentShuffleAcrossQueuesKey
import com.convx.music.constants.PlayerVolumeKey
import com.convx.music.constants.RememberShuffleAndRepeatKey
import com.convx.music.constants.RepeatModeKey
import com.convx.music.constants.ResumeOnBluetoothConnectKey
import com.convx.music.constants.ScrobbleDelayPercentKey
import com.convx.music.constants.ScrobbleDelaySecondsKey
import com.convx.music.constants.ScrobbleMinSongDurationKey
import com.convx.music.constants.ShuffleModeKey
import com.convx.music.constants.ShufflePlaylistFirstKey
import com.convx.music.constants.PreventDuplicateTracksInQueueKey
import com.convx.music.constants.SimilarContent
import com.convx.music.constants.SkipSilenceInstantKey
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.SpatialAudioKey
import com.convx.music.constants.VocalSuppressionKey
import com.convx.music.constants.SkipSilenceKey
import com.convx.music.constants.IpVersionKey
import com.music.innertube.models.IpVersion
import okhttp3.Dns
import java.net.InetAddress
import java.net.Inet4Address
import java.net.Inet6Address
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Event
import com.convx.music.db.entities.FormatEntity
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.db.entities.RelatedSongMap
import com.convx.music.db.entities.Song
import com.convx.music.di.DownloadCache
import com.convx.music.di.PlayerCache
import com.convx.music.eq.EqualizerService
import com.convx.music.playback.audio.BitPerfectAudio
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import com.convx.music.eq.audio.CustomEqualizerAudioProcessor
import com.convx.music.eq.data.EQProfileRepository
import com.convx.music.extensions.SilentHandler
import com.convx.music.extensions.collect
import com.convx.music.extensions.collectLatest
import com.convx.music.extensions.currentMetadata
import com.convx.music.extensions.findNextMediaItemById
import com.convx.music.extensions.mediaItems
import com.convx.music.extensions.metadata
import com.convx.music.extensions.setOffloadEnabled
import com.convx.music.extensions.toEnum
import com.convx.music.extensions.toMediaItem
import com.convx.music.extensions.toPersistQueue
import com.convx.music.extensions.toQueue
import com.convx.music.lyrics.LyricsHelper
import com.convx.music.models.MediaMetadata
import com.convx.music.models.PersistedPlaybackSnapshot
import com.convx.music.models.PersistPlayerState
import com.convx.music.models.PersistQueue
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.audio.TrackAnalyzerAudioProcessor
import com.convx.music.playback.dj.BeatGrid
import com.convx.music.playback.dj.DjEngine
import com.convx.music.playback.audio.DelayAudioProcessor
import com.convx.music.playback.audio.DjFilterAudioProcessor
import com.convx.music.playback.audio.DjTailAudioProcessor
import com.convx.music.playback.audio.DjAmbienceAudioProcessor
import com.convx.music.playback.audio.ReverbType
import com.convx.music.playback.audio.DelayType
import com.convx.music.playback.audio.DjMixPlan
import com.convx.music.playback.audio.DjMixTier
import com.convx.music.playback.audio.LosslessStallWatchdogAudioProcessor
import com.convx.music.playback.audio.SilenceDetectorAudioProcessor
import com.convx.music.playback.audio.SpatialAudioProcessor
import com.convx.music.playback.audio.VocalSuppressionAudioProcessor
import com.convx.music.playback.queues.EmptyQueue
import com.convx.music.playback.queues.Queue
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.playback.queues.filterExplicit
import com.convx.music.playback.queues.filterVideoSongs
import com.convx.music.utils.CoilBitmapLoader
import com.convx.music.utils.DiscordRPC
import com.convx.music.utils.NetworkConnectivityObserver
import com.convx.music.utils.ScrobbleManager
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.YTPlayerUtils
import com.convx.music.constants.StopMusicOnTaskClearKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import com.convx.music.widget.ConvxWidgetManager
import com.convx.music.widget.LyricsWidgetReceiver
import com.convx.music.widget.MusicWidgetReceiver
import com.convx.music.widget.NowPlayingWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import android.os.PowerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.ObjectInputStream
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

/** True when [key] is cached from [position] to its known end (a finished download). */
internal fun androidx.media3.datasource.cache.Cache.isCachedToEnd(key: String, position: Long): Boolean {
    val length = androidx.media3.datasource.cache.ContentMetadata.getContentLength(getContentMetadata(key))
    return length > 0 && position in 0 until length && isCached(key, position, length - position)
}

/** Readable span of [key] from [position] -- see [cachedSpan]. */
internal fun androidx.media3.datasource.cache.Cache.cachedSpanFrom(key: String, position: Long, max: Long): Long =
    cachedSpan(
        cached = getCachedLength(key, position, max),
        position = position,
        contentLength = androidx.media3.datasource.cache.ContentMetadata.getContentLength(getContentMetadata(key)),
        max = max,
    )

/** Returns the cached byte range usable without a network URL. */
internal fun cachedSpan(cached: Long, position: Long, contentLength: Long, max: Long): Long = when {
    cached <= 0 -> 0
    cached >= max -> max
    contentLength > 0 && position + cached >= contentLength -> cached
    else -> 0
}

/** Routing order for [MusicService.currentOutputKey]: first connected one wins. 26 = TYPE_BLE_HEADSET (API 31). */
private val OutputPriority = listOf(
    26,
    android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
    android.media.AudioDeviceInfo.TYPE_USB_HEADSET,
    android.media.AudioDeviceInfo.TYPE_USB_DEVICE,
    android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
    android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET,
)
private const val INSTANT_SILENCE_SKIP_STEP_MS = 15_000L
private const val DEFAULT_CROSSFADE_SECONDS = 5f
private const val INSTANT_SILENCE_SKIP_SETTLE_MS = 350L

// Throttle repeated writes triggered by frequent playback-stat reports.
private const val STATS_WRITE_INTERVAL_MS = 20_000L

/** How far ahead in play order the queue must run dry before the next page is fetched. */
private const val LOAD_MORE_LOOKAHEAD = 5

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class MusicService :
    MediaLibraryService(),
    Player.Listener,
    PlaybackStatsListener.Callback {
    @Inject
    lateinit var database: MusicDatabase

    @Inject
    lateinit var lyricsHelper: LyricsHelper

    @Inject
    lateinit var networkRepository: com.convx.storage.data.NetworkMusicRepository

    @Inject
    lateinit var syncUtils: dagger.Lazy<SyncUtils>

    @Inject
    lateinit var downloadUtil: dagger.Lazy<DownloadUtil>

    @Inject
    lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback

    @Inject
    lateinit var equalizerService: EqualizerService

    @Inject
    lateinit var eqProfileRepository: EQProfileRepository

    @Inject
    lateinit var widgetManager: ConvxWidgetManager

    private lateinit var audioManager: AudioManager
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wasPlayingBeforeAudioFocusLoss = false
    private var hasAudioFocus = false
    private var pendingFocusStart = false
    private var isPausingForAudioFocus = false
    private var audioFocusRequestOutstanding = false
    private var focusResumeJob: Job? = null
    private var wasPlayingBeforeVolumeMute = false
    private var isPausedByVolumeMute = false
    private val MIN_AUDIBLE_VOLUME = 0.05f
    private val VOLUME_WATCHDOG_MS = 2_000L
    var preferredDeviceId: Int? = null
        private set

    private var cachedSkipSilence = false
    private var cachedSkipSilenceInstant = false
    private var cachedBitPerfect = false
    private var cachedOffloadEnabled = false
    private var cachedSpatialAudio = false
    private var cachedVocalSuppression = 0f

    private var crossfade: CrossfadeController? = null
    private var trackAnalyzer: TrackAnalyzer? = null

    private var sparePlayer: ExoPlayer? = null
    // Keep sibling service tasks alive if one coroutine fails.
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Serialize persistence writes and keep file I/O off the main thread.
    private val persistenceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    // Last database write time per media item, used to throttle stats updates.
    private val lastStatsWriteAt = HashMap<String, Long>()

    private val binder = MusicBinder()

    /** App UI connections via [MusicBinder]; see [scheduleIdleStop]. */
    private var uiClients = 0
    private var idleStopJob: Job? = null

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    private lateinit var connectivityManager: ConnectivityManager
    lateinit var connectivityObserver: NetworkConnectivityObserver
    val waitingForNetworkConnection = MutableStateFlow(false)
    private val isNetworkConnected = MutableStateFlow(false)

    private lateinit var audioQuality: com.convx.music.constants.AudioQuality
    private lateinit var ipVersion: IpVersion

    private var currentQueue: Queue = EmptyQueue
    private var queueRequestVersion = 0L
    var queueTitle: String? = null

    val currentMediaMetadata = MutableStateFlow<com.convx.music.models.MediaMetadata?>(null)
    private val currentSong =
        currentMediaMetadata
            .flatMapLatest { mediaMetadata ->
                database.song(mediaMetadata?.id)
            }.stateIn(scope, SharingStarted.Lazily, null)
    private val currentFormat =
        currentMediaMetadata.flatMapLatest { mediaMetadata ->
            database.format(mediaMetadata?.id)
        }

    lateinit var playerVolume: MutableStateFlow<Float>
    val isMuted = MutableStateFlow(false)

    fun toggleMute() {
        val newMutedState = !isMuted.value
        isMuted.value = newMutedState
        player.volume = if (newMutedState) 0f else playerVolume.value
    }

    fun setMuted(muted: Boolean) {
        isMuted.value = muted
        player.volume = if (muted) 0f else playerVolume.value
    }

    fun setPreferredAudioDevice(deviceId: Int?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val deviceInfo = devices.find { it.id == deviceId }
            player.setPreferredAudioDevice(deviceInfo)
            preferredDeviceId = deviceId
        }
    }

    /** Re-resolves the active track when its video/song presentation changes. */
    fun setVideoPlaybackMode(mediaId: String, enabled: Boolean) {
        if (!playerInitialized.value || player.currentMediaItem?.mediaId != mediaId) return
        val changed = if (enabled) videoModeMediaIds.add(mediaId) else videoModeMediaIds.remove(mediaId)
        if (!changed) return

        errorRecovery.performAggressiveCacheClear(mediaId)

        val index = player.currentMediaItemIndex
        val position = player.currentPosition
        val wasPlaying = player.playWhenReady
        player.stop()
        player.seekTo(index, position)
        player.prepare()
        player.playWhenReady = wasPlaying
    }
    lateinit var sleepTimer: SleepTimer

    @Inject
    @PlayerCache
    lateinit var playerCache: SimpleCache

    @Inject
    @DownloadCache
    lateinit var downloadCache: SimpleCache

    lateinit var player: ExoPlayer
        private set
    private var duckVolumeRestoreJob: Job? = null

    private lateinit var mediaSession: MediaLibrarySession

    private val playerInitialized = MutableStateFlow(false)
    val isPlayerReady: kotlinx.coroutines.flow.StateFlow<Boolean> = playerInitialized.asStateFlow()

    private val _playerFlow = MutableStateFlow<ExoPlayer?>(null)
    val playerFlow = _playerFlow.asStateFlow()

    // Lets the UI match its artwork fade to the audio fade; zero selects the default.
    private val _crossfadeDurationMs = MutableStateFlow(0L)
    val crossfadeDurationMs = _crossfadeDurationMs.asStateFlow()

    private val playerSilenceProcessors = HashMap<Player, SilenceDetectorAudioProcessor>()
    private val playerEqualizers = HashMap<Player, CustomEqualizerAudioProcessor>()
    private val playerStallWatchdogs = HashMap<Player, LosslessStallWatchdogAudioProcessor>()
    private val playerSpatialProcessors = HashMap<Player, SpatialAudioProcessor>()
    private val playerVocalSuppressionProcessors = HashMap<Player, VocalSuppressionAudioProcessor>()

    private val instantSilenceSkipEnabled = MutableStateFlow(false)

    private var isAudioEffectSessionOpened = false
    private var loudnessEnhancer: LoudnessEnhancer? = null

    private var discordRpc: DiscordRPC? = null
    private var lastPlaybackSpeed = 1.0f
    private var discordUpdateJob: kotlinx.coroutines.Job? = null

    private var scrobbleManager: ScrobbleManager? = null

    private var listenBrainzEnabled = false
    private var listenBrainzToken = ""
    private var listenBrainzCurrentMediaId: String? = null
    private var listenBrainzCurrentStartTs: Long = 0L

    val automixItems = MutableStateFlow<List<MediaItem>>(emptyList())

    // Separates user-queued items from items added automatically.
    private var originalQueueSize: Int = 0

    private var consecutivePlaybackErr = 0
    private var retryJob: Job? = null
    private var retryCount = 0
    private var silenceSkipJob: Job? = null

    // Shared by the resolver and IO work; errors and quality changes can invalidate it.
    private val songUrlCache = java.util.concurrent.ConcurrentHashMap<String, Pair<String, Long>>()

    // Media IDs whose next resolution must fetch a fresh stream URL.
    private val bypassCacheForQualityChange = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    private var currentMediaIdRetryCount = mutableMapOf<String, Int>()
    private val MAX_RETRY_PER_SONG = 3
    private val RETRY_DELAY_MS = 1000L

    // Limits stall recovery to tracks resolved through a lossless provider.
    private val losslessStreamMediaIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    // Tracks that should fall back to standard audio after repeated lossless failures.
    private val forceStandardAudioMediaIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
    private val forceAacAudioMediaIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
    /** Tracks whose matching video is showing use YouTube audio for picture sync. */
    private val videoModeMediaIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    // Silent stall recoveries are separate from playback exception retries.
    private val losslessStallCount = mutableMapOf<String, Int>()
    private val MAX_LOSSLESS_STALLS_BEFORE_FALLBACK = 2
    // Owns DJ analysis and transitions in a scope independent of service restarts.
    private val djEngine: DjEngine by lazy {
        DjEngine(
            database = database,
            createProbePlayer = { createExoPlayer(PlayerRole.ANALYSIS) },
            releaseProbePlayer = ::releasePlaybackPlayer,
        )
    }

    /** BPM / key / tier readout for the UI, owned by the engine. */
    val djState get() = djEngine.state

    private val recentlyFailedSongs = mutableSetOf<String>()
    private var failedSongsClearJob: Job? = null

    // Google Cast support
    var castConnectionHandler: CastConnectionHandler? = null
        private set

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    if (!player.isPlaying) {
                        scope.launch(Dispatchers.IO) {
                            discordRpc?.closeRPC()
                        }
                    }
                }
                Intent.ACTION_SCREEN_ON -> {
                    if (player.isPlaying) {
                        scope.launch {
                            currentSong.value?.let { song ->
                                updateDiscordRPC(song)
                            }
                        }
                    }
                }
            }
        }
    }

    private val audioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            super.onAudioDevicesAdded(addedDevices)
            val hasBluetooth = addedDevices?.any {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            } == true

            if (hasBluetooth) {
                if (dataStore.get(ResumeOnBluetoothConnectKey, false)) {
                    if (player.playbackState == Player.STATE_READY && !player.isPlaying) {
                        player.play()
                    }
                }
            }
            onOutputChanged()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            onOutputChanged()
        }
    }

    /** Per-output EQ: restore the profile last used on whatever is now playing audio. */
    private fun onOutputChanged() {
        val key = currentOutputKey()
        scope.launch { eqProfileRepository.switchOutput(key) }
    }

    /** Identifies the active output device, preferring external devices to the speaker. */
    private fun currentOutputKey(): String {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val routed = OutputPriority.firstNotNullOfOrNull { type -> outputs.firstOrNull { it.type == type } }
        return routed?.let { "${it.type}:${it.productName}" } ?: "speaker"
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true

        // Handle foreground promotion refusal through Media3's supported callback.
        // Qualify the listener because this service also implements Player.Listener.
        setListener(object : MediaSessionService.Listener {
            override fun onForegroundServiceStartNotAllowedException() {
                Timber.tag(TAG).w("Foreground promotion refused by platform — staying unpromoted")
            }
        })

        playerInitialized.value = false

        startForegroundNotification()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { NOTIFICATION_ID },
                CHANNEL_ID,
                R.string.music_player
            )
                .apply {
                    setSmallIcon(R.drawable.convx_notification)
                },
        )
        player = createExoPlayer()
        player.addListener(this@MusicService)

        AudioCache.init(playerCache, downloadCache)
        AudioCache.prefetchHead = streamResolver::prefetchAnalysisHead
        AudioCache.analyzerUpstream = audioSourceBuilder.youtubeUpstreamFactory()

        trackAnalyzer = TrackAnalyzer(this, AudioCache)
        djEngine.smartGrid = { id ->
            trackAnalyzer?.analysisFor(id)?.takeIf { it.isUsable }?.let {
                BeatGrid(it.bpm.toFloat(), (it.firstBeat * 1000).toFloat(), it.beatConfidence.toFloat())
            }
        }
        crossfade = CrossfadeController(
            scope = scope,
            active = { player },
            standby = { sparePlayer ?: createExoPlayer(PlayerRole.STANDBY).also { sparePlayer = it } },
            onHandoff = ::adoptPlayer,
            analysisFor = { trackAnalyzer?.analysisFor(it.mediaId) ?: TrackAnalysis() },
            requestAnalysis = { mediaItem, durationMs ->
                // Device files are analysed in place; streamed songs look up the cache by the `v` parameter.
                val id = mediaItem.mediaId
                val uri = if (id.startsWith("content://") || id.startsWith("file://")) id.toUri()
                else "https://music.youtube.com/watch?v=$id".toUri()
                trackAnalyzer?.request(mediaItem.mediaId, uri, durationMs / 1000.0)
            },
            djEngine = djEngine,
            warmUpcoming = streamResolver::prefetchUpcoming,
        )
        crossfade?.start()

        // Keep the transition-path pref snapshot warm (see cached* fields).
        scope.launch {
            dataStore.data.collect { prefs ->
                cachedRepeatMode = prefs[RepeatModeKey] ?: REPEAT_MODE_OFF
                cachedAutoLoadMore = prefs[AutoLoadMoreKey] ?: true
                cachedDisableLoadMoreWhenRepeatAll = prefs[DisableLoadMoreWhenRepeatAllKey] ?: false
                cachedPersistentQueue = prefs[PersistentQueueKey] ?: true
            }
        }
        sleepTimer = SleepTimer(scope, player)
        player.addListener(sleepTimer)

        playerInitialized.value = true
        Timber.tag(TAG).d("Player successfully initialized")

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioResources.setupAudioFocusRequest()

        initializeMediaSession()

        connectivityManager = getSystemService()!!
        connectivityObserver = NetworkConnectivityObserver(this)

        val screenStateFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, screenStateFilter)

        audioManager.registerAudioDeviceCallback(audioDeviceCallback, null)
        onOutputChanged()

        audioQuality = dataStore.get(AudioQualityKey).toEnum(com.convx.music.constants.AudioQuality.AUTO)
        ipVersion = dataStore.get(IpVersionKey).toEnum(IpVersion.AUTO)
        // A saved volume of ~0 would come back silent on every launch with no on-screen way to tell
        // (the system volume does not touch this one), so only an audible value is restored.
        playerVolume = MutableStateFlow(
            dataStore.get(PlayerVolumeKey, 1f).coerceIn(0f, 1f).takeIf { it >= MIN_AUDIBLE_VOLUME } ?: 1f,
        )

        // Defer Cast initialization until after startup; it can block the main thread.
        scope.launch {
            delay(CastInitDelayMs)
            initializeCast()
        }

        scope.launch {
            eqProfileRepository.activeProfile.collect { profile ->
                if (profile != null) {
                    equalizerService.applyProfile(profile)
                } else {
                    equalizerService.disable()
                }
            }
        }

        scope.launch {
            connectivityObserver.networkStatus.collect { isConnected ->
                isNetworkConnected.value = isConnected
                if (player.isPlaying) audioResources.acquireWifiLock() else audioResources.releaseWifiLock()
                if (isConnected && waitingForNetworkConnection.value) {
                    errorRecovery.triggerRetry()
                }
                if (isConnected && discordRpc != null && player.isPlaying) {
                    val mediaId = player.currentMetadata?.id
                    if (mediaId != null) {
                        database.song(mediaId).first()?.let { song ->
                            updateDiscordRPC(song)
                        }
                    }
                }
            }
        }

        observePlaybackPreferenceChanges()

        observePlaybackUiAndLyrics()

        observeAudioProcessingPreferences()

        observeDiscordPreferences()

        observeScrobblingPreferences()

        observeTransitionPreferences()

        restorePersistentPlaybackState()

        startQueuePersistence()
    }

    private fun startQueuePersistence() {
        scope.launch {
            while (isActive) {
                delay(if (player.isPlaying) 10.seconds else 30.seconds)
                if (cachedPersistentQueue) queueRepository.saveQueueToDisk()
            }
        }
    }

    /**
     * Playing but silent with the system volume up means the player's own volume was left near zero
     * (an interrupted fade, a synced host volume, a stale duck). Put it back to what the user chose.
     */
    private fun startVolumeWatchdog() {
        scope.launch {
            while (isActive) {
                delay(VOLUME_WATCHDOG_MS)
                if (player.isPlaying && !isMuted.value && crossfade?.isTransitioning() != true &&
                    duckVolumeRestoreJob == null && player.volume < MIN_AUDIBLE_VOLUME &&
                    playerVolume.value >= MIN_AUDIBLE_VOLUME
                ) {
                    Timber.tag(TAG).w("Player volume stuck at ${player.volume}; restoring ${playerVolume.value}")
                    player.volume = playerVolume.value
                }
            }
        }
    }

    private fun observePlaybackUiAndLyrics() {
        startVolumeWatchdog()
        combine(playerVolume, isMuted) { volume, muted ->
            if (muted) 0f else volume
        }.collectLatest(scope) {
            player.volume = it
        }

        playerVolume.debounce(1000).collect(scope) { volume ->
            dataStore.edit { settings ->
                settings[PlayerVolumeKey] = volume
            }
        }

        currentSong.debounce(1000).collect(scope) { song ->
            notificationUpdater.updateNotification()
            notificationUpdater.updateWidgetUI(player.isPlaying)
        }

        var previousPolicy: String? = null
        combine(
            currentMediaMetadata.distinctUntilChangedBy { it?.id },
            dataStore.data.map { prefs ->
                val policy = prefs[LyricsProviderOrderKey].orEmpty() + "|" +
                    LyricsProviderRegistry.providerIds.joinToString(",") { id ->
                        "$id:${prefs[LyricsProviderSettings.enabledKey(id)] ?: true}"
                    }
                (prefs[DataSaverEnabledKey] != true) to policy
            }.distinctUntilChanged(),
        ) { metadata, policy -> metadata to policy }.collectLatest(scope) { (metadata, settings) ->
            val (showLyrics, policy) = settings
            val changed = previousPolicy != null && previousPolicy != policy
            previousPolicy = policy
            if (!showLyrics || metadata == null) return@collectLatest
            val existing = database.lyrics(metadata.id).first()
            if (existing != null) {
                val provider = LyricsProviderRegistry.getProviderForStoredName(existing.provider)
                // Embedded or manually edited lyrics keep their own source.
                if (provider == null && existing.provider != "Unknown") return@collectLatest
                if (!changed && provider != null && LyricsProviderSettings.isEnabled(this, provider) &&
                    LyricsSyncLevel.detect(existing.lyrics) == LyricsSyncLevel.WORD) return@collectLatest
            }
            var lastWritten = existing
            var manuallyChanged = false
            suspend fun persistUpgrade(found: LyricsWithProvider) {
                if (manuallyChanged) return
                database.withTransaction {
                    if (lyrics(metadata.id).first() != lastWritten) {
                        manuallyChanged = true
                    } else {
                        val next = LyricsEntity(metadata.id, found.lyrics, found.provider)
                        if (lastWritten != next) upsert(next)
                        lastWritten = next
                    }
                }
            }
            val initial = existing?.let { LyricsWithProvider(it.lyrics, it.provider) }
            val found = lyricsHelper.getLyrics(metadata, initial, ::persistUpgrade)
            if (found.lyrics != LyricsEntity.LYRICS_NOT_FOUND || lastWritten == null || changed) persistUpgrade(found)

        }
    }

    private fun observeAudioProcessingPreferences() {
        dataStore.data
            .map { (it[SkipSilenceKey] ?: false) to (it[SkipSilenceInstantKey] ?: false) }
            .distinctUntilChanged()
            .collectLatest(scope) { (skipSilence, instantSkip) ->
                cachedSkipSilence = skipSilence
                cachedSkipSilenceInstant = instantSkip

                player.skipSilenceEnabled = skipSilence
                sparePlayer?.skipSilenceEnabled = skipSilence

                val enableInstant = skipSilence && instantSkip
                instantSilenceSkipEnabled.value = enableInstant

                playerSilenceProcessors.values.forEach { processor ->
                    processor.instantModeEnabled = enableInstant
                    if (!enableInstant) {
                        processor.resetTracking()
                    }
                }

                if (!enableInstant) {
                    silenceSkipJob?.cancel()
                }
            }

        dataStore.data
            .map { it[BitPerfectKey] ?: false }
            .distinctUntilChanged()
            .collectLatest(scope) { bitPerfect ->
                cachedBitPerfect = bitPerfect
                equalizerService.setBitPerfectBypass(bitPerfect)
                // Spatial widening rewrites every sample, so it bypasses under Bit
                // Perfect for the same reason the EQ does — the toggle's own value is
                // left alone and restores when Bit Perfect goes back off.
                audioEffects.applySpatialAudio()
                audioEffects.applyVocalSuppression()

                if (bitPerfect) {
                    player.skipSilenceEnabled = false
                    sparePlayer?.skipSilenceEnabled = false
                } else {
                    player.skipSilenceEnabled = cachedSkipSilence
                    sparePlayer?.skipSilenceEnabled = cachedSkipSilence
                    BitPerfectAudio.clearBitPerfectMixer(this@MusicService)
                }
            }

        dataStore.data
            .map { it[SpatialAudioKey] ?: false }
            .distinctUntilChanged()
            .collectLatest(scope) { spatial ->
                cachedSpatialAudio = spatial
                audioEffects.applySpatialAudio()
            }

        dataStore.data
            .map { it[VocalSuppressionKey] ?: 0f }
            .distinctUntilChanged()
            .collectLatest(scope) { amount ->
                cachedVocalSuppression = amount.coerceIn(0f, 1f)
                audioEffects.applyVocalSuppression()
            }

        combine(
            currentFormat,
            dataStore.data
                .map { it[AudioNormalizationKey] ?: true }
                .distinctUntilChanged(),
        ) { format, normalizeAudio ->
            format to normalizeAudio
        }.collectLatest(scope) {
            audioEffects.setupLoudnessEnhancer()
        }

        dataStore.data.map { preferences ->
            val transitionsEnabled = (preferences[CrossfadeEnabledKey] ?: false) &&
                !(preferences[BitPerfectKey] ?: false)
            (preferences[AudioOffload] ?: false) && !transitionsEnabled
        }
            .distinctUntilChanged()
            .collectLatest(scope) { useOffload ->
                cachedOffloadEnabled = useOffload
                player.setOffloadEnabled(useOffload)
                sparePlayer?.setOffloadEnabled(useOffload)
            }
    }

    private fun observeDiscordPreferences() {
        dataStore.data
            .map { it[DiscordTokenKey] to (it[EnableDiscordRPCKey] ?: true) }
            .debounce(300)
            .distinctUntilChanged()
            .collect(scope) { (key, enabled) ->
                if (discordRpc?.isRpcRunning() == true) {
                    discordRpc?.closeRPC()
                }
                discordRpc = null
                if (key != null && enabled) {
                    discordRpc = DiscordRPC(this, key)
                    if (player.playbackState == Player.STATE_READY && player.playWhenReady) {
                        currentSong.value?.let {
                            updateDiscordRPC(it, true)
                        }
                    }
                }
            }

        dataStore.data
            .map {
                listOf(
                    it[DiscordUseDetailsKey],
                    it[DiscordAdvancedModeKey],
                    it[DiscordStatusKey],
                    it[DiscordButton1TextKey],
                    it[DiscordButton1VisibleKey],
                    it[DiscordButton2TextKey],
                    it[DiscordButton2VisibleKey],
                    it[DiscordActivityTypeKey],
                    it[DiscordActivityNameKey]
                )
            }
            .debounce(300)
            .distinctUntilChanged()
            .collect(scope) {
                if (player.playbackState == Player.STATE_READY) {
                    currentSong.value?.let { song ->
                        updateDiscordRPC(song, true)
                    }
                }
            }
    }

    private fun observeScrobblingPreferences() {
        dataStore.data
            .map { it[EnableLastFMScrobblingKey] ?: false }
            .debounce(300)
            .distinctUntilChanged()
            .collect(scope) { enabled ->
                if (enabled && scrobbleManager == null) {
                    val delayPercent = dataStore.get(ScrobbleDelayPercentKey, LastFM.DEFAULT_SCROBBLE_DELAY_PERCENT)
                    val minSongDuration = dataStore.get(ScrobbleMinSongDurationKey, LastFM.DEFAULT_SCROBBLE_MIN_SONG_DURATION)
                    val delaySeconds = dataStore.get(ScrobbleDelaySecondsKey, LastFM.DEFAULT_SCROBBLE_DELAY_SECONDS)
                    scrobbleManager = ScrobbleManager(
                        scope,
                        minSongDuration = minSongDuration,
                        scrobbleDelayPercent = delayPercent,
                        scrobbleDelaySeconds = delaySeconds
                    )
                    scrobbleManager?.useNowPlaying = dataStore.get(LastFMUseNowPlaying, false)
                } else if (!enabled && scrobbleManager != null) {
                    scrobbleManager?.destroy()
                    scrobbleManager = null
                }
            }

        dataStore.data
            .map { it[LastFMUseNowPlaying] ?: false }
            .distinctUntilChanged()
            .collectLatest(scope) {
                scrobbleManager?.useNowPlaying = it
            }

        dataStore.data
            .map {
                val listenBrainz = it[ListenBrainzEnabledKey] ?: false
                val dataSaver = it[DataSaverEnabledKey] ?: false
                if (dataSaver) false else listenBrainz
            }
            .distinctUntilChanged()
            .collect(scope) { listenBrainzEnabled = it }

        dataStore.data
            .map { it[ListenBrainzTokenKey] ?: "" }
            .distinctUntilChanged()
            .collect(scope) { listenBrainzToken = it }

        dataStore.data
            .map { prefs ->
                Triple(
                    prefs[ScrobbleDelayPercentKey] ?: LastFM.DEFAULT_SCROBBLE_DELAY_PERCENT,
                    prefs[ScrobbleMinSongDurationKey] ?: LastFM.DEFAULT_SCROBBLE_MIN_SONG_DURATION,
                    prefs[ScrobbleDelaySecondsKey] ?: LastFM.DEFAULT_SCROBBLE_DELAY_SECONDS
                )
            }
            .distinctUntilChanged()
            .collect(scope) { (delayPercent, minSongDuration, delaySeconds) ->
                scrobbleManager?.let {
                    it.scrobbleDelayPercent = delayPercent
                    it.minSongDuration = minSongDuration
                    it.scrobbleDelaySeconds = delaySeconds
                }
            }
    }

    private data class TransitionPreferences(
        val enabled: Boolean,
        val durationSeconds: Float,
        val autoMix: Boolean,
        val creative: Boolean,
        val reverb: ReverbType,
        val delay: DelayType,
    )

    private fun observeTransitionPreferences() {
        dataStore.data.map { prefs ->
            val enabled = (prefs[CrossfadeEnabledKey] ?: false) && !(prefs[BitPerfectKey] ?: false)
            val autoMix = enabled && (prefs[AutoDjMixingEnabledKey] ?: false)
            val seconds = (prefs[CrossfadeDurationKey] ?: DEFAULT_CROSSFADE_SECONDS)
                .takeIf { it.isFinite() } ?: DEFAULT_CROSSFADE_SECONDS
            TransitionPreferences(
                enabled = enabled,
                durationSeconds = seconds.coerceIn(1f, 12f),
                autoMix = autoMix,
                creative = autoMix && (prefs[CreativeTransitionsEnabledKey] ?: false),
                reverb = prefs[DjReverbTypeKey].toEnum(ReverbType.AUTO),
                delay = prefs[DjDelayTypeKey].toEnum(DelayType.AUTO),
            )
        }.distinctUntilChanged().collect(scope) { preferences ->
            AppSettings.crossfadeSeconds.floatValue = if (preferences.enabled) preferences.durationSeconds else 0f
            AppSettings.smartFadeEnabled.value = preferences.autoMix
            if (!preferences.autoMix) trackAnalyzer?.cancelPending()
            djEngine.enabled = preferences.autoMix
            djEngine.creativeEnabled = preferences.creative
            djEngine.reverbType = preferences.reverb
            djEngine.delayType = preferences.delay
            djEngine.refreshState(player.currentMediaItem?.mediaId)
        }
    }

    private fun restorePersistentPlaybackState() {
        if (!dataStore.get(PersistentQueueKey, true)) return

        persistenceScope.launch {
            val snapshot = readPersistedPlaybackSnapshot() ?: return@launch
            scope.launch {
                snapshot.automix?.let { savedAutomix ->
                    automixItems.value = savedAutomix.items.map { it.toMediaItem() }
                }

                snapshot.playerState?.let { savedState ->
                    // A muted or ducked snapshot contains the effective player volume.
                    if (savedState.volume > 0f) playerVolume.value = savedState.volume
                }

                val savedQueue = snapshot.queue ?: return@launch
                playerInitialized.first { it }
                if (isActive) {
                    val restoredQueue = withContext(Dispatchers.IO) {
                        savedQueue.toQueue(database)
                    }
                    playQueue(queue = restoredQueue, playWhenReady = false)
                }
            }
        }
    }

    private fun readPersistedPlaybackSnapshot(): PersistedPlaybackSnapshot? {
        val store = PlaybackStateStore(filesDir.resolve(PERSISTENT_PLAYBACK_SNAPSHOT_FILE))
        val currentSnapshot = runCatching { store.read() }
            .onFailure { error ->
                Timber.tag(TAG).w(error, "Failed to read persisted playback snapshot")
                store.delete()
            }
            .getOrNull()
        if (currentSnapshot != null) return currentSnapshot

        val legacySnapshot = PersistedPlaybackSnapshot(
            queue = readLegacyPlaybackFile(PERSISTENT_QUEUE_FILE, PersistQueue::class.java),
            automix = readLegacyPlaybackFile(PERSISTENT_AUTOMIX_FILE, PersistQueue::class.java),
            playerState = readLegacyPlaybackFile(PERSISTENT_PLAYER_STATE_FILE, PersistPlayerState::class.java),
        )
        if (
            legacySnapshot.queue == null &&
            legacySnapshot.automix == null &&
            legacySnapshot.playerState == null
        ) return null

        runCatching {
            store.write(legacySnapshot)
            deleteLegacyPlaybackFiles()
        }.onFailure { error ->
            Timber.tag(TAG).w(error, "Failed to migrate saved playback state")
        }
        return legacySnapshot
    }

    private fun <T : Any> readLegacyPlaybackFile(fileName: String, type: Class<T>): T? {
        val file = filesDir.resolve(fileName)
        if (!file.exists()) return null

        return runCatching {
            file.inputStream().use { input ->
                ObjectInputStream(input).use { it.readObject().let(type::cast) }
            }
        }.onFailure { error ->
            Timber.tag(TAG).w(error, "Failed to read legacy playback file $fileName")
            file.delete()
        }.getOrNull()
    }

    private fun deleteLegacyPlaybackFiles() {
        filesDir.resolve(PERSISTENT_QUEUE_FILE).delete()
        filesDir.resolve(PERSISTENT_AUTOMIX_FILE).delete()
        filesDir.resolve(PERSISTENT_PLAYER_STATE_FILE).delete()
    }

    private fun observePlaybackPreferenceChanges() {
        var isFirstQualityEmit = true
        scope.launch {
            dataStore.data
                .map { prefs ->
                    if (prefs[DataSaverEnabledKey] ?: false) {
                        com.convx.music.constants.AudioQuality.LOW
                    } else {
                        prefs[AudioQualityKey]?.let { value ->
                            com.convx.music.constants.AudioQuality.entries.find { it.name == value }
                        } ?: com.convx.music.constants.AudioQuality.AUTO
                    }
                }
                .distinctUntilChanged()
                .collect { newQuality ->
                    val oldQuality = audioQuality
                    audioQuality = newQuality

                    // Skip reload on first emit (app startup)
                    if (isFirstQualityEmit) {
                        isFirstQualityEmit = false
                        Timber.tag("MusicService").i("QUALITY INIT: $newQuality")
                        return@collect
                    }

                    Timber.tag("MusicService").i("QUALITY CHANGED: $oldQuality -> $newQuality")

                    val mediaId = player.currentMediaItem?.mediaId ?: return@collect
                    val currentPosition = player.currentPosition
                    val wasPlaying = player.isPlaying
                    val currentIndex = player.currentMediaItemIndex

                    Timber.tag("MusicService").i("RELOADING STREAM: $mediaId at position ${currentPosition}ms")

                    songUrlCache.remove(mediaId)

                    // Clear old-format bytes before reloading at the new quality.
                    withContext(Dispatchers.IO) {
                        try {
                            playerCache.removeResource(mediaId)
                            downloadCache.removeResource(mediaId)
                            Timber.tag("MusicService").d("Cleared player and download cache for $mediaId")
                        } catch (e: Exception) {
                            Timber.tag("MusicService").e(e, "Failed to clear cache for $mediaId")
                        }
                    }

                    bypassCacheForQualityChange.add(mediaId)
                    Timber.tag("MusicService").d("Set bypass cache flag for $mediaId")

                    player.stop()
                    player.seekTo(currentIndex, currentPosition)
                    player.prepare()
                    if (wasPlaying) {
                        player.play()
                    }
                }
        }

        // Re-resolve the queue when a provider or its settings change. Preparing
        // an existing source leaves its resolved URL and buffered codec intact.
        scope.launch {
            var initial = true
            dataStore.data.map(::streamSourcePreferences).distinctUntilChanged().collect {
                if (initial) {
                    initial = false
                    return@collect
                }
                songUrlCache.clear()
                forceStandardAudioMediaIds.clear()
                forceAacAudioMediaIds.clear()
                val mediaId = player.currentMediaItem?.mediaId ?: return@collect
                if (mediaId.startsWith("content://") || mediaId.startsWith("file://")) return@collect
                crossfade?.onSkipRequested()
                val position = player.currentPosition
                val index = player.currentMediaItemIndex
                val playWhenReady = player.playWhenReady
                val items = (0 until player.mediaItemCount).map(player::getMediaItemAt)
                sparePlayer?.stop()
                sparePlayer?.clearMediaItems()
                player.stop()
                player.setMediaItems(items, index, position)
                player.prepare()
                player.playWhenReady = playWhenReady
            }
        }

        scope.launch {
            dataStore.data
                .map { it[IpVersionKey]?.toEnum(IpVersion.AUTO) ?: IpVersion.AUTO }
                .distinctUntilChanged()
                .collect { newIpVersion ->
                    val oldIpVersion = ipVersion
                    ipVersion = newIpVersion

                    if (isFirstQualityEmit) return@collect

                    Timber.tag("MusicService").i("IP VERSION CHANGED: $oldIpVersion -> $newIpVersion")

                    // Reload player to apply new DNS filter
                    val mediaId = player.currentMediaItem?.mediaId ?: return@collect
                    val currentPosition = player.currentPosition
                    val currentIndex = player.currentMediaItemIndex
                    val wasPlaying = player.isPlaying

                    songUrlCache.remove(mediaId)

                    player.stop()
                    player.seekTo(currentIndex, currentPosition)
                    player.prepare()
                    if (wasPlaying) {
                        player.play()
                    }
                }
        }
    }

    private fun startForegroundNotification() {
        try {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.music_player),
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.music_player))
                .setContentText("")
                .setSmallIcon(R.drawable.convx_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to create foreground notification")
            reportException(e)
        }
    }

    private fun initializeMediaSession() {
        mediaLibrarySessionCallback.apply {
            toggleLike = ::toggleLike
            toggleStartRadio = ::toggleStartRadio
            toggleLibrary = ::toggleLibrary
        }
        mediaSession =
            MediaLibrarySession
                .Builder(this, player, mediaLibrarySessionCallback)
                .setSessionActivity(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                ).setBitmapLoader(CoilBitmapLoader(this, scope))
                .build()
        player.repeatMode = dataStore.get(RepeatModeKey, REPEAT_MODE_OFF)

        if (dataStore.get(RememberShuffleAndRepeatKey, true)) {
            player.shuffleModeEnabled = dataStore.get(ShuffleModeKey, false)
        }

        val sessionToken = SessionToken(this, ComponentName(this, MusicService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({ controllerFuture.get() }, MoreExecutors.directExecutor())
    }

    private enum class PlayerRole { SESSION, STANDBY, ANALYSIS }

    private fun createExoPlayer(role: PlayerRole = PlayerRole.SESSION): ExoPlayer {
        val analysisOnly = role == PlayerRole.ANALYSIS
        var createdPlayer: ExoPlayer? = null
        val eqProcessor = CustomEqualizerAudioProcessor()
        if (!analysisOnly) equalizerService.addAudioProcessor(eqProcessor)

        val silenceProcessor = SilenceDetectorAudioProcessor(
            minSilenceDurationUs = 3_000_000L,
            silenceThreshold = 128,
            onLongSilence = {
                if (playerInitialized.value && createdPlayer === this@MusicService.player) {
                    losslessStallGuard.handleLongSilenceDetected()
                }
            }
        )

        val stallWatchdog = LosslessStallWatchdogAudioProcessor(
            onStallDetected = {
                if (playerInitialized.value && createdPlayer === this@MusicService.player) {
                    losslessStallGuard.handleLosslessStallDetected()
                }
            }
        )

        lateinit var bpmAnalyzerRef: TrackAnalyzerAudioProcessor
        val bpmAnalyzer = TrackAnalyzerAudioProcessor(
            onAnalysisReady = { djEngine.onAnalysisReady(bpmAnalyzerRef) },
            analysisEnabled = { djEngine.enabled },
        )
        bpmAnalyzerRef = bpmAnalyzer

        val djFilter = DjFilterAudioProcessor()
        val djTail = DjTailAudioProcessor()
        val djDelay = DelayAudioProcessor()
        val djAmbience = DjAmbienceAudioProcessor()
        val spatialProcessor = SpatialAudioProcessor()
        val vocalSuppressionProcessor = VocalSuppressionAudioProcessor()
        // Use cached preferences; reading DataStore here could stall player creation during crossfade.
        silenceProcessor.instantModeEnabled = cachedSkipSilence && cachedSkipSilenceInstant
        spatialProcessor.enabled = cachedSpatialAudio && !cachedBitPerfect
        vocalSuppressionProcessor.amount = if (cachedBitPerfect) 0f else cachedVocalSuppression

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(streamResolver.createMediaSourceFactory())
            .setRenderersFactory(
                streamResolver.createRenderersFactory(
                    eqProcessor, silenceProcessor, stallWatchdog, bpmAnalyzer, spatialProcessor, vocalSuppressionProcessor,
                    djFilter, djTail, djDelay, djAmbience, analysisOnly,
                )
            )
            .setLoadControl(
                androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        50_000,   // Min buffer: 50s
                        50_000,   // Max buffer: 50s
                        750,      // Buffer for playback: 750ms — start audio as soon as we have it
                        2_000     // Buffer for playback after re-buffer: 2s
                    )
                    .build()
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                false,
            )
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .setDeviceVolumeControlEnabled(true)
            .build()

        createdPlayer = player
        if (!analysisOnly) {
            playerSilenceProcessors[player] = silenceProcessor
            playerEqualizers[player] = eqProcessor
            playerStallWatchdogs[player] = stallWatchdog
            playerSpatialProcessors[player] = spatialProcessor
            playerVocalSuppressionProcessors[player] = vocalSuppressionProcessor
        }
        djEngine.registerPlayer(player, bpmAnalyzer, djFilter, djTail, djDelay, djAmbience)

        player.setOffloadEnabled(cachedOffloadEnabled && !analysisOnly)
        player.skipSilenceEnabled = cachedSkipSilence && !cachedBitPerfect
        if (!analysisOnly) player.addAnalyticsListener(crashSafe(PlaybackStatsListener(false, this@MusicService)))

        if (role == PlayerRole.SESSION) _playerFlow.value = player
        return player
    }

    private fun releasePlaybackPlayer(target: ExoPlayer) {
        djEngine.unregisterPlayer(target)
        playerEqualizers.remove(target)?.let(equalizerService::removeAudioProcessor)
        playerSilenceProcessors.remove(target)
        playerStallWatchdogs.remove(target)
        playerSpatialProcessors.remove(target)
        playerVocalSuppressionProcessors.remove(target)
        target.release()
    }

    /** Manages audio focus and the Wi-Fi lock used during playback. */
    inner class AudioResourceCoordinator {

        fun setupAudioFocusRequest() {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener { focusChange ->
                    handleAudioFocusChange(focusChange)
                }
                .setAcceptsDelayedFocusGain(true)
                .build()
        }

        fun handleAudioFocusChange(focusChange: Int) {
            when (focusChange) {

                AudioManager.AUDIOFOCUS_GAIN,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                    hasAudioFocus = true
                    duckVolumeRestoreJob?.cancel()
                    duckVolumeRestoreJob = null
                    player.volume = if (isMuted.value) 0f else playerVolume.value
                    resumeAfterFocusGain()
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    hasAudioFocus = false
                    pendingFocusStart = false
                    wasPlayingBeforeAudioFocusLoss = false
                    pauseForAudioFocus()
                    abandonAudioFocus()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    hasAudioFocus = false
                    wasPlayingBeforeAudioFocusLoss = player.playWhenReady
                    pauseForAudioFocus()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    hasAudioFocus = false
                    if (player.isPlaying) {
                        player.volume = if (isMuted.value) 0f else (playerVolume.value * 0.2f)
                        // Safety net: some devices deliver CAN_DUCK and never follow
                        // up with AUDIOFOCUS_GAIN, leaving volume at 20% indefinitely.
                        duckVolumeRestoreJob?.cancel()
                        duckVolumeRestoreJob = scope.launch {
                            delay(DUCK_RESTORE_MS)
                            if (isActive && player.isPlaying) {
                                player.volume = if (isMuted.value) 0f else playerVolume.value
                                Timber.tag(TAG).w("Restoring volume after unreleased audio duck")
                            }
                            duckVolumeRestoreJob = null
                        }
                    }
                }

                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                    hasAudioFocus = true
                    duckVolumeRestoreJob?.cancel()
                    duckVolumeRestoreJob = null
                    player.volume = if (isMuted.value) 0f else playerVolume.value
                }
            }
        }

        fun requestAudioFocus(): FocusRequestResult {
            if (hasAudioFocus) return FocusRequestResult.GRANTED

            audioFocusRequest?.let { request ->
                val result = audioManager.requestAudioFocus(request)
                return when (result) {
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                        hasAudioFocus = true
                        audioFocusRequestOutstanding = true
                        FocusRequestResult.GRANTED
                    }

                    AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                        hasAudioFocus = false
                        audioFocusRequestOutstanding = true
                        FocusRequestResult.DELAYED
                    }

                    else -> {
                        hasAudioFocus = false
                        audioFocusRequestOutstanding = false
                        FocusRequestResult.DENIED
                    }
                }
            }
            return FocusRequestResult.DENIED
        }

        fun abandonAudioFocus() {
            if (audioFocusRequestOutstanding) {
                audioFocusRequest?.let(audioManager::abandonAudioFocusRequest)
            }
            audioFocusRequestOutstanding = false
            hasAudioFocus = false
            pendingFocusStart = false
        }

        fun cancelDelayedFocusStart() {
            pendingFocusStart = false
            if (!hasAudioFocus) abandonAudioFocus()
        }

        private fun resumeAfterFocusGain() {
            val shouldResume = pendingFocusStart || wasPlayingBeforeAudioFocusLoss
            if (!shouldResume) return

            focusResumeJob?.cancel()
            focusResumeJob = scope.launch {
                delay(300)
                if (
                    hasAudioFocus &&
                    (pendingFocusStart || wasPlayingBeforeAudioFocusLoss) &&
                    !player.playWhenReady &&
                    castConnectionHandler?.isCasting?.value != true
                ) {
                    pendingFocusStart = false
                    wasPlayingBeforeAudioFocusLoss = false
                    player.play()
                }
            }
        }

        fun pauseForAudioFocus() {
            focusResumeJob?.cancel()
            if (!player.playWhenReady) return

            isPausingForAudioFocus = true
            try {
                player.pause()
            } finally {
                isPausingForAudioFocus = false
            }
        }

        fun acquireWifiLock() {
            val metadata = player.currentMetadata
            val downloaded = metadata?.id?.let { downloadUtil.get().downloads.value[it]?.state } ==
                androidx.media3.exoplayer.offline.Download.STATE_COMPLETED
            val wifiConnected = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            if (!wifiConnected || metadata?.isLocalFile == true || downloaded) {
                releaseWifiLock()
                return
            }
            if (wifiLock == null) {
                val wifiManager = applicationContext.getSystemService(android.net.wifi.WifiManager::class.java)
                wifiLock = wifiManager?.createWifiLock(
                    android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                    "convx_music:wifi_lock"
                )
            }
            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
            }
        }

        fun releaseWifiLock() {
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
            }
        }
    }

    private val audioResources = AudioResourceCoordinator()

    enum class FocusRequestResult {
        GRANTED,
        DELAYED,
        DENIED,
    }

    fun hasAudioFocusForPlayback(): Boolean = hasAudioFocus

    /** Handles playback retry, skip, stop, and cache recovery. */
    inner class PlaybackErrorRecoveryHandler {

        fun waitOnNetworkError() {
            if (retryCount >= MAX_RETRY_COUNT) {
                Timber.tag(TAG).w("Max retry count ($MAX_RETRY_COUNT) reached, stopping playback")
                stopOnError()
                retryCount = 0
                return
            }

            waitingForNetworkConnection.value = true

            // Avoid leaving the service waiting for network with no active retry job.
            if (retryJob?.isActive == true) return

            retryJob?.cancel()
            retryJob = scope.launch {
                // Exponential backoff: 3s, 6s, 12s, 24s... max 30s
                val delayMs = minOf(3000L * (1 shl retryCount), 30000L)
                Timber.tag(TAG).d("Waiting ${delayMs}ms before retry attempt ${retryCount + 1}/$MAX_RETRY_COUNT")
                delay(delayMs)

                if (!isActive) return@launch
                retryCount++
                if (isNetworkConnected.value) {
                    triggerRetry()
                } else {
                    // Keep waiting until the connection returns.
                    waitOnNetworkError()
                }
            }
        }

        fun triggerRetry() {
            waitingForNetworkConnection.value = false
            retryJob?.cancel()

            if (player.currentMediaItem != null) {
                // Seeking in place forces ExoPlayer to re-resolve the data source and get a fresh URL.
                if (retryCount > 3) {
                    Timber.tag(TAG).d("Retry count > 3, attempting to refresh stream URL")
                    val currentPosition = player.currentPosition
                    player.seekTo(player.currentMediaItemIndex, currentPosition)
                }
                // No play() here: let it auto-resume via playWhenReady instead of stealing audio focus mid-retry.
                player.prepare()
            }
        }

        fun skipOnError() {
            // Stop retrying after repeated errors and wait for user input.
            consecutivePlaybackErr += 2
            val nextWindowIndex = player.nextMediaItemIndex

            if (consecutivePlaybackErr <= MAX_CONSECUTIVE_ERR && nextWindowIndex != C.INDEX_UNSET) {
                player.seekTo(nextWindowIndex, C.TIME_UNSET)
                player.prepare()
                if (castConnectionHandler?.isCasting?.value != true) {
                    player.play()
                }
                return
            }

            player.pause()
            consecutivePlaybackErr = 0
        }

        fun stopOnError() {
            player.pause()
            // Stop the service so Media3 removes the stale playback notification.
            stopSelf()
            currentMediaMetadata.value = null
            notificationUpdater.updateWidgetUI(false)
        }

        fun performAggressiveCacheClear(mediaId: String) {
            Timber.tag(TAG).d("Performing aggressive cache clear for $mediaId")

            // Clear every source variant; lossless streams use a suffixed cache key.
            val sourceKeys = (playerCache.keys + songUrlCache.keys).filter {
                it == mediaId ||
                    it == "$mediaId#aac" ||
                    it == "$mediaId#flac" ||
                    it.startsWith("$mediaId#source-")
            }.toSet() + mediaId
            sourceKeys.forEach(songUrlCache::remove)

            for (key in sourceKeys) {
                try {
                    playerCache.removeResource(key)
                    Timber.tag(TAG).d("Cleared player cache for $key")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to clear player cache for $key")
                }
            }

            try {
                YTPlayerUtils.forceRefreshForVideo(mediaId)
                Timber.tag(TAG).d("Cleared decryption caches for $mediaId")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to clear decryption caches for $mediaId")
            }
        }

        fun hasExceededRetryLimit(mediaId: String): Boolean {
            val currentRetries = currentMediaIdRetryCount[mediaId] ?: 0
            return currentRetries >= MAX_RETRY_PER_SONG
        }

        fun incrementRetryCount(mediaId: String) {
            val currentRetries = currentMediaIdRetryCount[mediaId] ?: 0
            currentMediaIdRetryCount[mediaId] = currentRetries + 1
            Timber.tag(TAG).d("Retry count for $mediaId: ${currentRetries + 1}/$MAX_RETRY_PER_SONG")
        }

        /** Called on successful playback to let a song retry again later if it fails again. */
        fun resetRetryCount(mediaId: String) {
            currentMediaIdRetryCount.remove(mediaId)
            recentlyFailedSongs.remove(mediaId)
        }

        fun markSongAsFailed(mediaId: String) {
            recentlyFailedSongs.add(mediaId)
            currentMediaIdRetryCount.remove(mediaId)

            // Schedule cleanup of failed songs list after 5 minutes
            failedSongsClearJob?.cancel()
            failedSongsClearJob = scope.launch {
                delay(5 * 60 * 1000L)
                recentlyFailedSongs.clear()
                Timber.tag(TAG).d("Cleared recently failed songs list")
            }
        }

        private fun retryStream(
            mediaId: String,
            reason: String,
            fromStart: Boolean = false,
            clearCaches: Boolean = true,
            bypassQualityCache: Boolean = false,
        ) {
            incrementRetryCount(mediaId)
            retryJob?.cancel()
            retryJob = scope.launch {
                if (clearCaches) performAggressiveCacheClear(mediaId)
                if (bypassQualityCache) bypassCacheForQualityChange.add(mediaId)
                delay(RETRY_DELAY_MS)

                val position = if (fromStart) 0L else player.currentPosition
                player.seekTo(player.currentMediaItemIndex, position)
                player.prepare()
                Timber.tag(TAG).d("Retrying playback for $mediaId after $reason")
            }
        }

        fun handleParsingError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            // Unsupported lossless formats will not recover by retrying the same URL.
            if (losslessStreamMediaIds.contains(mediaId)) {
                losslessStallGuard.triggerLosslessFallback(mediaId)
                return
            }

            retryStream(
                mediaId = mediaId,
                reason = "parsing error (from position 0)",
                fromStart = true,
                bypassQualityCache = true,
            )
        }

        fun handleAudioRendererError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            incrementRetryCount(mediaId)

            retryJob?.cancel()
            retryJob = scope.launch {
                try {
                    // Pause immediately to stop the renderer, then wait longer than a normal
                    // retry (3s instead of 1s) so it doesn't keep failing in a loop.
                    player.pause()
                    Timber.tag(TAG).d("Paused playback due to AudioTrack error")
                    delay(RETRY_DELAY_MS * 3)

                    if (!playerInitialized.value) {
                        Timber.tag(TAG).w("Player no longer initialized, aborting AudioTrack recovery")
                        return@launch
                    }

                    val currentIndex = player.currentMediaItemIndex
                    if (currentIndex != C.INDEX_UNSET) {
                        // Seek to current position to force a clean audio renderer reinit
                        val currentPosition = player.currentPosition
                        player.seekTo(currentIndex, currentPosition)
                        player.prepare()

                        Timber.tag(TAG).d("Retrying playback for $mediaId after AudioTrack error")

                        if (wasPlayingBeforeAudioFocusLoss) {
                            delay(500)
                            if (hasAudioFocus && playerInitialized.value) {
                                if (castConnectionHandler?.isCasting?.value != true) {
                                    player.play()
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).w("Invalid media item index during AudioTrack recovery")
                        handleFinalFailure()
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error during AudioTrack error recovery")
                    handleFinalFailure()
                }
            }
        }

        /** Samsung's resource manager reclaims the FLAC hardware decoder mid-playback. */
        fun handleDecoderReclaimError(mediaId: String?) {
            if (mediaId == null) { handleFinalFailure(); return }
            incrementRetryCount(mediaId)
            retryJob?.cancel()
            retryJob = scope.launch {
                Timber.tag(TAG).d("Decoder reclaim: pausing, waiting 2s, re-prepare for $mediaId")
                player.pause()
                delay(2000)
                if (!playerInitialized.value) return@launch
                val idx = player.currentMediaItemIndex
                val pos = player.currentPosition
                player.seekTo(idx, pos)
                player.prepare()
                delay(500)
                if (hasAudioFocus && playerInitialized.value) {
                    if (castConnectionHandler?.isCasting?.value != true) {
                        player.play()
                    }
                }
            }
        }

        fun handleRangeNotSatisfiableError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            retryStream(mediaId, reason = "416 error (from position 0)", fromStart = true)
        }

        fun handlePageReloadError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            Timber.tag(TAG).d("Handling page reload error for $mediaId")
            retryStream(mediaId, reason = "page reload error")
        }

        fun handleExpiredUrlError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            incrementRetryCount(mediaId)

            songUrlCache.remove(mediaId)
            Timber.tag(TAG).d("Cleared cached URL for $mediaId")

            try {
                YTPlayerUtils.forceRefreshForVideo(mediaId)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to clear decryption caches")
            }

            retryStream(mediaId, reason = "403 error", clearCaches = false)
        }

        fun handleGenericIOError(mediaId: String?) {
            if (mediaId == null) {
                handleFinalFailure()
                return
            }

            retryStream(mediaId, reason = "generic IO error")
        }

        fun handleFinalFailure() {
            if (dataStore.get(AutoSkipNextOnErrorKey, false)) {
                Timber.tag(TAG).d("All recovery attempts exhausted, auto-skipping to next track")
                skipOnError()
            } else {
                Timber.tag(TAG).d("All recovery attempts exhausted, stopping playback")
                stopOnError()
            }
        }
    }

    private val errorRecovery = PlaybackErrorRecoveryHandler()

    private fun downloadKey(mediaId: String) = downloadUtil.get().cacheKeyFor(mediaId)

    private val songRecoveryInFlight = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    /**
     * Seeking re-opens the data source for every probe, and a lossless file without a seek table can
     * mean dozens of probes. Recovering the song's metadata once per session is enough; doing it per
     * open queued a database read and a main-thread hop for each probe.
     */
    private val recoveredSongIds: MutableSet<String> = java.util.concurrent.ConcurrentHashMap.newKeySet()

    private fun recoverSongOnce(mediaId: String) {
        if (recoveredSongIds.add(mediaId)) scope.launch(Dispatchers.IO) { recoverSong(mediaId) }
    }

    private suspend fun recoverSong(
        mediaId: String,
        playbackData: YTPlayerUtils.PlaybackData? = null
    ) {
        if (!songRecoveryInFlight.add(mediaId)) return
        try {
            val song = database.song(mediaId).first()
            val mediaMetadata = withContext(Dispatchers.Main) {
                player.findNextMediaItemById(mediaId)?.metadata
            } ?: return
            val duration = song?.song?.duration?.takeIf { it != -1 }
                ?: mediaMetadata.duration.takeIf { it != -1 }
                ?: (playbackData?.videoDetails ?: YTPlayerUtils.playerResponseForMetadata(mediaId)
                    .getOrNull()?.videoDetails)?.lengthSeconds?.toInt()
                ?: -1
            database.query {
                if (song == null) insert(mediaMetadata.copy(duration = duration))
                else {
                    var updatedSong = song.song
                    if (song.song.duration == -1) {
                        updatedSong = updatedSong.copy(duration = duration)
                    }
                    if (song.song.isVideo != mediaMetadata.isVideoSong) {
                        updatedSong = updatedSong.copy(isVideo = mediaMetadata.isVideoSong)
                    }
                    if (song.song.isPodcastEpisode != mediaMetadata.isPodcastEpisode) {
                        updatedSong = updatedSong.copy(isPodcastEpisode = mediaMetadata.isPodcastEpisode)
                    }
                    if (updatedSong != song.song) {
                        update(updatedSong)
                    }
                }
            }
            if (!database.hasRelatedSongs(mediaId)) {
                val relatedEndpoint =
                    YouTube.next(WatchEndpoint(videoId = mediaId)).getOrNull()?.relatedEndpoint
                        ?: return
                val relatedPage = YouTube.related(relatedEndpoint).getOrNull() ?: return
                database.query {
                    relatedPage.songs
                        .map(SongItem::toMediaMetadata)
                        .onEach(::insert)
                        .map {
                            RelatedSongMap(
                                songId = mediaId,
                                relatedSongId = it.id
                            )
                        }
                        .forEach(::insert)
                }
            }
        } finally {
            songRecoveryInFlight.remove(mediaId)
        }
    }

    fun playQueue(
        queue: Queue,
        playWhenReady: Boolean = true,
    ) {
        val requestVersion = ++queueRequestVersion
        if (!playerInitialized.value) {
            Timber.tag(TAG).w("playQueue called before player initialization, queuing request")
            scope.launch {
                playerInitialized.first { it }
                if (requestVersion == queueRequestVersion) {
                    playQueue(queue, playWhenReady)
                }
            }
            return
        }

        currentQueue = queue
        queueTitle = null
        val persistShuffleAcrossQueues = dataStore.get(PersistentShuffleAcrossQueuesKey, false)
        if (!persistShuffleAcrossQueues) {
            player.shuffleModeEnabled = false
        }
        originalQueueSize = 0
        queue.preloadItem?.let { preloadItem ->
            player.setMediaItem(preloadItem.toMediaItem())
            player.prepare()
            player.playWhenReady = playWhenReady
        }
        scope.launch(SilentHandler) {
            val initialStatus =
                withContext(Dispatchers.IO) {
                    queue.getFilteredInitialStatus()
                }
            if (requestVersion != queueRequestVersion) return@launch
            if (queue.preloadItem != null && player.playbackState == STATE_IDLE) return@launch
            if (initialStatus.title != null) {
                queueTitle = initialStatus.title
            }
            if (initialStatus.items.isEmpty()) return@launch
            // Record the original playlist boundary for shuffle-playlist-first.
            originalQueueSize = initialStatus.items.size
            if (queue.preloadItem != null) {
                player.addMediaItems(
                    0,
                    initialStatus.items.subList(0, initialStatus.mediaItemIndex)
                )
                player.addMediaItems(
                    initialStatus.items.subList(
                        initialStatus.mediaItemIndex + 1,
                        initialStatus.items.size
                    )
                )
            } else {
                player.setMediaItems(
                    initialStatus.items,
                    initialStatus.mediaItemIndex.coerceAtLeast(0),
                    initialStatus.position,
                )
                player.prepare()
                player.playWhenReady = playWhenReady
            }

            // Prefetch current and next stream URLs off the loader thread.
            streamResolver.prefetchUpcoming()

            if (player.shuffleModeEnabled) {
                val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
                applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
            }
        }
    }

    private suspend fun Queue.getFilteredInitialStatus(): Queue.Status = getInitialStatus()
        .filterExplicit(dataStore.get(HideExplicitKey, false))
        .filterVideoSongs(
            dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false),
        )

    /** Appends [queue] after the current items without changing the playhead. */
    fun enqueueQueue(queue: Queue) {
        scope.launch(SilentHandler) {
            val status = withContext(Dispatchers.IO) { queue.getFilteredInitialStatus() }
            if (status.items.isEmpty()) return@launch
            // Start with the selected item, then wrap to the earlier entries.
            val start = status.mediaItemIndex.coerceIn(0, status.items.lastIndex)
            addToQueue(status.items.drop(start) + status.items.take(start))
        }
    }

    fun startRadioSeamlessly() {
        if (!playerInitialized.value) {
            Timber.tag(TAG).w("startRadioSeamlessly called before player initialization")
            return
        }

        val currentMediaMetadata = player.currentMetadata ?: return

        val currentIndex = player.currentMediaItemIndex
        val currentMediaId = currentMediaMetadata.id
        val requestVersion = queueRequestVersion

        scope.launch(SilentHandler) {
            val radioQueue = YouTubeQueue(endpoint = WatchEndpoint(videoId = currentMediaId))

            try {
                val initialStatus = withContext(Dispatchers.IO) {
                    radioQueue.getInitialStatus()
                        .filterExplicit(dataStore.get(HideExplicitKey, false))
                        .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))
                }
                if (
                    requestVersion != queueRequestVersion ||
                    player.currentMediaItem?.mediaId != currentMediaId ||
                    player.currentMediaItemIndex != currentIndex
                ) return@launch

                if (initialStatus.title != null) {
                    queueTitle = initialStatus.title
                }

                val radioItems = initialStatus.items.filterNot { it.mediaId == currentMediaId }
                replaceUpcomingItems(currentIndex, radioItems)

                currentQueue = radioQueue
            } catch (_: Exception) {
                if (requestVersion == queueRequestVersion) {
                    loadRelatedRadio(currentMediaId, currentIndex, requestVersion)
                }
            }
        }
    }

    private suspend fun loadRelatedRadio(
        mediaId: String,
        currentIndex: Int,
        requestVersion: Long,
    ) {
        try {
            val relatedEndpoint = withContext(Dispatchers.IO) {
                YouTube.next(WatchEndpoint(videoId = mediaId)).getOrNull()?.relatedEndpoint
            } ?: return
            val relatedPage = withContext(Dispatchers.IO) {
                YouTube.related(relatedEndpoint).getOrNull()
            } ?: return
            val hideVideos = dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false)
            val radioItems = relatedPage.songs
                .filter { it.id != mediaId }
                .map { it.toMediaItem() }
                .filterExplicit(dataStore.get(HideExplicitKey, false))
                .filterVideoSongs(hideVideos)
            if (
                requestVersion != queueRequestVersion ||
                player.currentMediaItem?.mediaId != mediaId ||
                player.currentMediaItemIndex != currentIndex
            ) return
            replaceUpcomingItems(currentIndex, radioItems)
        } catch (_: Exception) {
            // Radio recommendations are optional; keep the current queue on failure.
        }
    }

    private fun replaceUpcomingItems(currentIndex: Int, items: List<MediaItem>) {
        if (items.isEmpty()) return

        val queueEnd = player.mediaItemCount
        if (queueEnd > currentIndex + 1) {
            player.removeMediaItems(currentIndex + 1, queueEnd)
        }
        player.addMediaItems(currentIndex + 1, items)

        if (player.shuffleModeEnabled) {
            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
        }
    }

    fun getAutomixAlbum(albumId: String) {
        scope.launch(SilentHandler) {
            YouTube
                .album(albumId)
                .onSuccess {
                    getAutomix(it.album.playlistId)
                }
        }
    }

    fun getAutomix(playlistId: String) {
        if (!dataStore.get(SimilarContent, true)) return
        if (dataStore.get(DisableLoadMoreWhenRepeatAllKey, false) && player.repeatMode == REPEAT_MODE_ALL) return

        scope.launch(SilentHandler) {
            try {
                loadAutomixItems(playlistId)
            } catch (_: Exception) {
                // Recommendations are optional; keep playback running on failure.
            }
        }
    }

    private suspend fun loadAutomixItems(playlistId: String) {
        val firstResult = YouTube.next(WatchEndpoint(playlistId = playlistId)).getOrNull()
        if (firstResult == null) {
            loadAutomixFromRadio()
            return
        }

        val secondResult = YouTube.next(
            WatchEndpoint(playlistId = firstResult.endpoint.playlistId),
        ).getOrNull()

        when {
            secondResult != null -> automixItems.value = secondResult.items.map { it.toMediaItem() }
            firstResult.items.isNotEmpty() -> automixItems.value = firstResult.items.map { it.toMediaItem() }
        }
    }

    private suspend fun loadAutomixFromRadio() {
        val currentSong = player.currentMetadata ?: return
        val radioResult = YouTube.next(WatchEndpoint(videoId = currentSong.id)).getOrNull()

        if (radioResult != null) {
            val items = radioResult.items
                .filter { it.id != currentSong.id }
                .map { it.toMediaItem() }
            if (items.isNotEmpty()) automixItems.value = items
            return
        }

        val relatedEndpoint = YouTube.next(WatchEndpoint(videoId = currentSong.id))
            .getOrNull()
            ?.relatedEndpoint ?: return
        val relatedPage = YouTube.related(relatedEndpoint).getOrNull() ?: return
        val items = relatedPage.songs
            .filter { it.id != currentSong.id }
            .map { it.toMediaItem() }
        if (items.isNotEmpty()) automixItems.value = items
    }

    fun addToQueueAutomix(
        item: MediaItem,
        position: Int,
    ) {
        removeAutomixSuggestion(position)
        addToQueue(listOf(item))
    }

    fun playNextAutomix(
        item: MediaItem,
        position: Int,
    ) {
        removeAutomixSuggestion(position)
        playNext(listOf(item))
    }

    private fun removeAutomixSuggestion(position: Int) {
        automixItems.value = automixItems.value.toMutableList().apply {
            removeAt(position)
        }
    }

    fun clearAutomix() {
        automixItems.value = emptyList()
    }

    fun playNext(items: List<MediaItem>) {
        if (player.mediaItemCount == 0 || player.playbackState == STATE_IDLE) {
            player.setMediaItems(items)
            player.prepare()
            // Don't start local playback if casting
            if (castConnectionHandler?.isCasting?.value != true) {
                player.play()
            }
            return
        }

        removeDuplicateQueueItems(items)

        val insertIndex = player.currentMediaItemIndex + 1
        val shuffleEnabled = player.shuffleModeEnabled

        player.addMediaItems(insertIndex, items)
        player.prepare()

        if (shuffleEnabled) {
            rebuildShuffleOrderAfterPlayNext(insertIndex, items.size)
        }
    }

    private fun rebuildShuffleOrderAfterPlayNext(insertIndex: Int, insertedCount: Int) {
        val timeline = player.currentTimeline
        if (timeline.isEmpty) return

        val windowCount = timeline.windowCount
        val currentIndex = player.currentMediaItemIndex
        val insertedIndices = (insertIndex until insertIndex + insertedCount).toSet()

        val itemsAfterCurrent = mutableListOf<Int>()
        var nextIndex = currentIndex
        while (true) {
            nextIndex = timeline.getNextWindowIndex(
                nextIndex,
                Player.REPEAT_MODE_OFF,
                /*shuffleModeEnabled=*/true,
            )
            if (nextIndex == C.INDEX_UNSET) break
            if (nextIndex != currentIndex) itemsAfterCurrent.add(nextIndex)
        }

        val itemsBeforeCurrent = mutableListOf<Int>()
        var previousIndex = currentIndex
        while (true) {
            previousIndex = timeline.getPreviousWindowIndex(
                previousIndex,
                Player.REPEAT_MODE_OFF,
                /*shuffleModeEnabled=*/true,
            )
            if (previousIndex == C.INDEX_UNSET) break
            if (previousIndex != currentIndex) itemsBeforeCurrent.add(previousIndex)
        }
        itemsBeforeCurrent.reverse()

        val existingOrder = (itemsBeforeCurrent + itemsAfterCurrent)
            .filter { it != currentIndex && it !in insertedIndices }
        val insertedOrder = (insertIndex until insertIndex + insertedCount).toList()
        val finalOrder = IntArray(windowCount)
        val placed = BooleanArray(windowCount)
        var position = 0

        finalOrder[position++] = currentIndex
        placed[currentIndex] = true
        insertedOrder.forEach { index ->
            if (index in 0 until windowCount) {
                finalOrder[position++] = index
                placed[index] = true
            }
        }
        existingOrder.forEach { index ->
            if (position < windowCount) {
                finalOrder[position++] = index
                placed[index] = true
            }
        }

        // Keep the membership check constant-time while filling a valid permutation.
        for (index in 0 until windowCount) {
            if (position == windowCount) break
            if (!placed[index]) {
                finalOrder[position++] = index
            }
        }

        player.setShuffleOrder(DefaultShuffleOrder(finalOrder, System.currentTimeMillis()))
    }

    fun addToQueue(items: List<MediaItem>) {
        removeDuplicateQueueItems(items)

        player.addMediaItems(items)
        if (player.shuffleModeEnabled) {
            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
        }
        player.prepare()
    }

    private fun removeDuplicateQueueItems(items: List<MediaItem>) {
        if (!dataStore.get(PreventDuplicateTracksInQueueKey, false)) return

        val itemIds = items.mapTo(mutableSetOf()) { it.mediaId }
        var currentIndex = player.currentMediaItemIndex

        for (index in player.mediaItemCount - 1 downTo 0) {
            if (index == currentIndex) continue
            if (player.getMediaItemAt(index).mediaId !in itemIds) continue

            player.removeMediaItem(index)
            // Removing an earlier item shifts the active item's index.
            if (index < currentIndex) currentIndex--
        }
    }

    /** Returns the database row for the player's current track, inserting it if needed. */
    private suspend fun liveCurrentSong(): Song? {
        val metadata = player.currentMetadata ?: return null
        // Read from the player directly; currentSong may be uncollected or one track behind.
        database.song(metadata.id).first()?.let { return it }
        // Await insertion because database.query returns before the write completes.
        database.withTransaction { insert(metadata) }
        return database.song(metadata.id).first()
    }

    fun toggleLibrary() {
        scope.launch {
            val songToToggle = liveCurrentSong()
            songToToggle?.let {
                val isInLibrary = it.song.inLibrary != null
                val token = if (isInLibrary) it.song.libraryRemoveToken else it.song.libraryAddToken

                // Call YouTube API with feedback token if available
                token?.let { feedbackToken ->
                    YouTube.feedback(listOf(feedbackToken))
                }

                database.query {
                    update(it.song.toggleLibrary())
                }
                currentMediaMetadata.value = player.currentMetadata
            }
        }
    }

    fun toggleLike() {
        scope.launch {
            val songToToggle = liveCurrentSong()
            songToToggle?.let {
                val song = it.song.toggleLike()
                database.query {
                    update(song)
                    syncUtils.get().likeSong(song)
                }
                if (song.liked && dataStore.get(AutoDownloadOnLikeKey, false)) {
                    downloadUtil.get().onSongLiked(song)
                }
                currentMediaMetadata.value = player.currentMetadata
            }
        }
    }

    fun toggleStartRadio() {
        startRadioSeamlessly()
    }

    /** Loudness normalization + spatial audio + the system audio-effect-session broadcast, grouped as one effects chain. */
    inner class AudioEffectsChain {

        fun applySpatialAudio() {
            val on = cachedSpatialAudio && !cachedBitPerfect
            playerSpatialProcessors.values.forEach { it.enabled = on }
        }

        fun applyVocalSuppression() {
            val amount = if (cachedBitPerfect) 0f else cachedVocalSuppression
            playerVocalSuppressionProcessors.values.forEach { it.amount = amount }
        }

        fun setupLoudnessEnhancer() {
            val audioSessionId = player.audioSessionId

            if (audioSessionId == C.AUDIO_SESSION_ID_UNSET || audioSessionId <= 0) {
                Timber.tag(TAG).w("setupLoudnessEnhancer: invalid audioSessionId ($audioSessionId), cannot create effect yet")
                return
            }

            if (loudnessEnhancer == null) {
                try {
                    loudnessEnhancer = LoudnessEnhancer(audioSessionId)
                    Timber.tag(TAG).d("LoudnessEnhancer created for sessionId=$audioSessionId")
                } catch (e: Exception) {
                    reportException(e)
                    loudnessEnhancer = null
                    return
                }
            }

            scope.launch {
                try {
                    val currentMediaId = withContext(Dispatchers.Main) {
                        player.currentMediaItem?.mediaId
                    }

                    val normalizeAudio = withContext(Dispatchers.IO) {
                        dataStore.data.map { it[AudioNormalizationKey] ?: true }.first()
                    }

                    if (normalizeAudio && currentMediaId != null) {
                        val format = withContext(Dispatchers.IO) {
                            database.format(currentMediaId).first()
                        }

                        Timber.tag(TAG).d("Audio normalization enabled: $normalizeAudio")
                        Timber.tag(TAG).d("Format loudnessDb: ${format?.loudnessDb}, perceptualLoudnessDb: ${format?.perceptualLoudnessDb}")

                        val loudness = format?.loudnessDb ?: format?.perceptualLoudnessDb

                        withContext(Dispatchers.Main) {
                            if (loudness != null) {
                                val loudnessDb = loudness.toFloat()
                                val targetGain = (-loudnessDb * 100).toInt()
                                val clampedGain = targetGain.coerceIn(MIN_GAIN_MB, MAX_GAIN_MB)

                                Timber.tag(TAG).d("Calculated raw normalization gain: $targetGain mB (from loudness: $loudnessDb)")

                                try {
                                    loudnessEnhancer?.setTargetGain(clampedGain)
                                    loudnessEnhancer?.enabled = true
                                    Timber.tag(TAG).i("LoudnessEnhancer gain applied: $clampedGain mB")
                                } catch (e: Exception) {
                                    Timber.tag(TAG).e(e, "Failed to apply loudness enhancement")
                                    reportException(e)
                                    releaseLoudnessEnhancer()
                                }
                            } else {
                                loudnessEnhancer?.enabled = false
                                Timber.tag(TAG).w("Normalization enabled but no loudness data available - no normalization applied")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            loudnessEnhancer?.enabled = false
                            Timber.tag(TAG).d("setupLoudnessEnhancer: normalization disabled or mediaId unavailable")
                        }
                    }
                } catch (e: Exception) {
                    reportException(e)
                    releaseLoudnessEnhancer()
                }
            }
        }

        fun releaseLoudnessEnhancer() {
            try {
                loudnessEnhancer?.release()
                Timber.tag(TAG).d("LoudnessEnhancer released")
            } catch (e: Exception) {
                reportException(e)
                Timber.tag(TAG).e(e, "Error releasing LoudnessEnhancer: ${e.message}")
            } finally {
                loudnessEnhancer = null
            }
        }

        fun openAudioEffectSession() {
            if (isAudioEffectSessionOpened) return
            isAudioEffectSessionOpened = true
            setupLoudnessEnhancer()
            sendBroadcast(
                Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION).apply {
                    putExtra(AudioEffect.EXTRA_AUDIO_SESSION, player.audioSessionId)
                    putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                    putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                },
            )
        }

        fun closeAudioEffectSession() {
            if (!isAudioEffectSessionOpened) return
            isAudioEffectSessionOpened = false
            releaseLoudnessEnhancer()
            sendBroadcast(
                Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION).apply {
                    putExtra(AudioEffect.EXTRA_AUDIO_SESSION, player.audioSessionId)
                    putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                },
            )
        }
    }

    private val audioEffects = AudioEffectsChain()

    private var previousMediaItemIndex = C.INDEX_UNSET

    // Player callbacks use this snapshot instead of blocking Main on DataStore reads.
    @Volatile private var cachedRepeatMode = REPEAT_MODE_OFF
    @Volatile private var cachedAutoLoadMore = true
    @Volatile private var cachedDisableLoadMoreWhenRepeatAll = false
    @Volatile private var cachedPersistentQueue = true

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        // A failed transition may need standard audio once. Let a later visit to
        // that track try the preferred lossless source again without restarting.
        forceStandardAudioMediaIds.filter { it != mediaItem?.mediaId }.forEach { departedId ->
            forceStandardAudioMediaIds.remove(departedId)
            bypassCacheForQualityChange.add(departedId)
        }
        // Video mode belongs to one queue item. If playback leaves that item,
        // discard its standard-audio variant so returning to it as a song can
        // resolve the preferred lossless source again.
        val departedVideoIds = videoModeMediaIds.filter { it != mediaItem?.mediaId }
        departedVideoIds.forEach(videoModeMediaIds::remove)
        if (departedVideoIds.isNotEmpty()) {
            scope.launch(Dispatchers.IO) {
                departedVideoIds.forEach(errorRecovery::performAggressiveCacheClear)
            }
        }
        Timber.tag(TAG).d(
            "SKIP_DEBUG onMediaItemTransition: reason=$reason (0=REPEAT,1=AUTO,2=SEEK,3=PLAYLIST_CHANGED) " +
                "newIndex=${player.currentMediaItemIndex} newId=${mediaItem?.mediaId} " +
                "prevTrackedIndex=$previousMediaItemIndex mediaItemCount=${player.mediaItemCount} " +
                "repeatMode=${player.repeatMode} shuffle=${player.shuffleModeEnabled}"
        )
        // Force Repeat One if the player ignored it and auto-advanced
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            val repeatMode = cachedRepeatMode
            if (repeatMode == REPEAT_MODE_ONE &&
                previousMediaItemIndex != C.INDEX_UNSET &&
                previousMediaItemIndex != player.currentMediaItemIndex) {

                Timber.tag(TAG).d("SKIP_DEBUG onMediaItemTransition: forcing repeat-one seekTo($previousMediaItemIndex)")
                player.seekTo(previousMediaItemIndex, 0)
            }
        }
        previousMediaItemIndex = player.currentMediaItemIndex
        // Normal track changes use the default artwork dissolve; DJ handoffs bypass this listener.
        _crossfadeDurationMs.value = 0L
        if (player.isPlaying) audioResources.acquireWifiLock()
        streamResolver.prefetchUpcoming()

        playerStallWatchdogs[player]?.let { watchdog ->
            watchdog.armed = mediaItem?.mediaId?.let { losslessStreamMediaIds.contains(it) } == true
            watchdog.resetForNewTrack()
        }
        djEngine.onTrackChanged(player, mediaItem?.mediaId)

        lastPlaybackSpeed = -1.0f // force update song

        audioEffects.setupLoudnessEnhancer()

        discordUpdateJob?.cancel()

        updateScrobblingForCurrentTrack()

        syncCastForMediaItem(mediaItem)

        loadMoreQueueIfNeeded(reason)

        if (cachedPersistentQueue) {
            queueRepository.saveQueueToDisk()
        }
    }

    private fun updateScrobblingForCurrentTrack() {
        scrobbleManager?.onSongStop()
        scrobbleCoordinator.checkAndSubmitListenBrainzFinished()

        if (!player.playWhenReady || player.playbackState != Player.STATE_READY) return

        scrobbleManager?.onSongStart(player.currentMetadata, duration = player.duration)
        val mediaId = player.currentMediaItem?.mediaId ?: return

        if (listenBrainzCurrentMediaId != mediaId) {
            listenBrainzCurrentMediaId = mediaId
            listenBrainzCurrentStartTs = System.currentTimeMillis()
        }
        scrobbleCoordinator.checkAndSubmitListenBrainzPlayingNow(mediaId)
    }

    private fun syncCastForMediaItem(mediaItem: MediaItem?) {
        val castHandler = castConnectionHandler ?: return
        if (!castHandler.isCasting.value || castHandler.isSyncingFromCast) return

        val metadata = mediaItem?.metadata ?: return

        // Reuse the Cast queue entry to avoid reloading media and refreshing the widget.
        val navigated = castHandler.navigateToMediaIfInQueue(metadata.id)
        if (!navigated) castHandler.loadMedia(metadata)
    }

    private fun loadMoreQueueIfNeeded(reason: Int) {
        val shouldLoadMore = cachedAutoLoadMore &&
            reason != Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT &&
            remainingInPlayOrder(LOAD_MORE_LOOKAHEAD) <= LOAD_MORE_LOOKAHEAD &&
            currentQueue.hasNextPage() &&
            !(cachedDisableLoadMoreWhenRepeatAll && player.repeatMode == REPEAT_MODE_ALL)
        if (!shouldLoadMore) return

        scope.launch(SilentHandler) {
            val mediaItems = withContext(Dispatchers.IO) {
                currentQueue.nextPage()
                    .filterExplicit(dataStore.get(HideExplicitKey, false))
                    .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))
            }
            if (player.playbackState != STATE_IDLE && mediaItems.isNotEmpty()) {
                // Appending preserves ExoPlayer's shuffle order; rebuilding it moves the current item and loses played entries.
                player.addMediaItems(mediaItems)
            }
        }
    }

    override fun onPlaybackStateChanged(
        @Player.State playbackState: Int,
    ) {
        // Self-healing re-arm: the DataSourceResolver may populate
        // losslessStreamMediaIds slightly after onMediaItemTransition already
        // checked it (first play with no preload buffer). By STATE_READY the
        // resolver has definitely run for the current item.
        if (playbackState == Player.STATE_READY) {
            player.currentMediaItem?.mediaId?.let { mediaId ->
                playerStallWatchdogs[player]?.armed = losslessStreamMediaIds.contains(mediaId)
            }
        }

        if (playbackState == Player.STATE_ENDED) {
            val repeatMode = cachedRepeatMode
            if (repeatMode == REPEAT_MODE_ALL && player.mediaItemCount > 0) {
                player.seekTo(0, 0)
                player.prepare()
                player.play()
            }
        }

        if (cachedPersistentQueue && !isSilenceSkipping) {
            queueRepository.saveQueueToDisk()
        }

        if (playbackState == Player.STATE_READY) {
            consecutivePlaybackErr = 0
            retryCount = 0
            waitingForNetworkConnection.value = false
            retryJob?.cancel()

            player.currentMediaItem?.mediaId?.let { mediaId ->
                errorRecovery.resetRetryCount(mediaId)
                Timber.tag(TAG).d("Playback successful for $mediaId, reset retry count")
            }

            // Bit Perfect: re-request the hardware mixer for every track, since a
            // sample-rate change needs a fresh negotiation with AudioFlinger.
            if (cachedBitPerfect) {
                player.audioFormat?.let { format ->
                    BitPerfectAudio.requestBitPerfectMixer(this, format.sampleRate, format.pcmEncoding)
                }
            }
        }

        if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
            scrobbleManager?.onSongStop()
            scrobbleCoordinator.checkAndSubmitListenBrainzFinished()
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        // Safety net: if local player tries to start while casting, immediately pause it
        if (playWhenReady && castConnectionHandler?.isCasting?.value == true) {
            player.pause()
            return
        }

        if (playWhenReady) {
            when (audioResources.requestAudioFocus()) {
                FocusRequestResult.GRANTED -> {
                    pendingFocusStart = false
                    wasPlayingBeforeAudioFocusLoss = false
                }

                FocusRequestResult.DELAYED -> {
                    pendingFocusStart = true
                    audioResources.pauseForAudioFocus()
                }

                FocusRequestResult.DENIED -> {
                    pendingFocusStart = false
                    wasPlayingBeforeAudioFocusLoss = false
                    audioResources.pauseForAudioFocus()
                }
            }
        } else if (
            reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST &&
            !isPausingForAudioFocus
        ) {
            wasPlayingBeforeAudioFocusLoss = false
            audioResources.cancelDelayedFocusStart()
        }

        if (playWhenReady && !player.playWhenReady) return

        if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
            if (playWhenReady) {
                isPausedByVolumeMute = false
            }

            if (!playWhenReady && !isPausedByVolumeMute) {
                wasPlayingBeforeVolumeMute = false
            }
        }

        if (playWhenReady) {
            audioEffects.setupLoudnessEnhancer()
        }
        scheduleIdleStop(playWhenReady)
    }

    /** Stops an unbound paused service after [IdleStopMs]; the persisted queue restores on reopen. */
    private fun scheduleIdleStop(playWhenReady: Boolean) {
        idleStopJob?.cancel()
        if (playWhenReady) return
        idleStopJob = scope.launch {
            delay(IdleStopMs)
            val idle = !player.playWhenReady && uiClients == 0 &&
                !wasPlayingBeforeAudioFocusLoss &&
                castConnectionHandler?.isCasting?.value != true
            if (idle) {
                Timber.tag(TAG).d("Paused and unbound for ${IdleStopMs / 60_000}m, stopping service")
                if (dataStore.get(PersistentQueueKey, true)) queueRepository.saveQueueToDisk()
                pauseAllPlayersAndStopSelf()
            }
        }
    }

    override fun onEvents(
        player: Player,
        events: Player.Events,
    ) {
        if (events.containsAny(
                Player.EVENT_PLAYBACK_STATE_CHANGED,
                Player.EVENT_PLAY_WHEN_READY_CHANGED
            )
        ) {
            val isBufferingOrReady =
                player.playbackState == Player.STATE_BUFFERING || player.playbackState == Player.STATE_READY
            if (isBufferingOrReady && player.playWhenReady && hasAudioFocus) {
                audioEffects.openAudioEffectSession()
            } else {
                audioEffects.closeAudioEffectSession()
            }
        }
        if (events.containsAny(EVENT_TIMELINE_CHANGED, EVENT_POSITION_DISCONTINUITY)) {
            currentMediaMetadata.value = player.currentMetadata
        }

        if (events.containsAny(Player.EVENT_IS_PLAYING_CHANGED)) {
            notificationUpdater.updateWidgetUI(player.isPlaying)
            notificationUpdater.restartWidgetProgressTicker()
            if (player.isPlaying) {
                audioResources.acquireWifiLock()
            } else {
                audioResources.releaseWifiLock()
            }
            if (!player.isPlaying && !events.containsAny(Player.EVENT_POSITION_DISCONTINUITY, Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                scope.launch {
                    discordRpc?.close()
                }
            }
        }

        if (events.containsAny(Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_IS_PLAYING_CHANGED) && player.isPlaying) {
            val mediaId = player.currentMetadata?.id
            if (mediaId != null) {
                scope.launch {
                    database.song(mediaId).first()?.let { song ->
                        updateDiscordRPC(song)
                    }
                }
            }
        }

        if (events.containsAny(Player.EVENT_IS_PLAYING_CHANGED)) {
            scrobbleManager?.onPlayerStateChanged(player.isPlaying, player.currentMetadata, duration = player.duration)

            if (player.isPlaying) {
                player.currentMediaItem?.mediaId?.let { mediaId ->
                    if (listenBrainzCurrentMediaId != mediaId) {
                        scrobbleCoordinator.checkAndSubmitListenBrainzFinished()
                        listenBrainzCurrentMediaId = mediaId
                        listenBrainzCurrentStartTs = System.currentTimeMillis()
                    }
                    scrobbleCoordinator.checkAndSubmitListenBrainzPlayingNow(mediaId)
                }
            }
        }

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        notificationUpdater.updateNotification()
        if (shuffleModeEnabled) {
            if (player.mediaItemCount == 0) return

            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            val currentIndex = player.currentMediaItemIndex
            val totalCount = player.mediaItemCount

            applyShuffleOrder(currentIndex, totalCount, shufflePlaylistFirst)
        }

        if (dataStore.get(RememberShuffleAndRepeatKey, true)) {
            scope.launch {
                dataStore.edit { settings ->
                    settings[ShuffleModeKey] = shuffleModeEnabled
                }
            }
        }

        if (dataStore.get(PersistentQueueKey, true)) {
            queueRepository.saveQueueToDisk()
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        notificationUpdater.updateNotification()
        scope.launch {
            dataStore.edit { settings ->
                settings[RepeatModeKey] = repeatMode
            }
        }

        if (dataStore.get(PersistentQueueKey, true)) {
            queueRepository.saveQueueToDisk()
        }
    }

    /** Counts upcoming items in playback order, stopping once there are more than [limit]. */
    private fun remainingInPlayOrder(limit: Int): Int {
        val timeline = player.currentTimeline
        if (timeline.isEmpty) return 0
        val shuffled = player.shuffleModeEnabled
        var index = player.currentMediaItemIndex
        var remaining = 0
        while (remaining <= limit) {
            index = timeline.getNextWindowIndex(index, Player.REPEAT_MODE_OFF, shuffled)
            if (index == C.INDEX_UNSET) break
            remaining++
        }
        return remaining
    }

    /** Keeps the current item first and can shuffle original and added items separately. */
    private fun applyShuffleOrder(
        currentIndex: Int,
        totalCount: Int,
        shufflePlaylistFirst: Boolean
    ) {
        if (totalCount == 0) return

        val indices = if (shufflePlaylistFirst && originalQueueSize in 1 until totalCount) {
            createPlaylistFirstShuffleOrder(currentIndex, totalCount)
        } else {
            createShuffleOrderWithCurrentFirst(currentIndex, totalCount)
        }
        player.setShuffleOrder(DefaultShuffleOrder(indices, System.currentTimeMillis()))
    }

    private fun createPlaylistFirstShuffleOrder(currentIndex: Int, totalCount: Int): IntArray {
        val originalIndices = (0 until originalQueueSize)
            .filterTo(mutableListOf()) { it != currentIndex }
        val addedIndices = (originalQueueSize until totalCount)
            .filterTo(mutableListOf()) { it != currentIndex }

        originalIndices.shuffle()
        addedIndices.shuffle()

        val shuffledIndices = IntArray(totalCount)
        var nextIndex = 0
        shuffledIndices[nextIndex++] = currentIndex

        originalIndices.forEach { shuffledIndices[nextIndex++] = it }
        addedIndices.forEach { shuffledIndices[nextIndex++] = it }
        return shuffledIndices
    }

    private fun createShuffleOrderWithCurrentFirst(currentIndex: Int, totalCount: Int): IntArray {
        val shuffledIndices = IntArray(totalCount) { it }
        shuffledIndices.shuffle()

        val currentIndexInOrder = shuffledIndices.indexOf(currentIndex)
        if (currentIndexInOrder != -1) {
            val firstIndex = shuffledIndices[0]
            shuffledIndices[0] = shuffledIndices[currentIndexInOrder]
            shuffledIndices[currentIndexInOrder] = firstIndex
        }
        return shuffledIndices
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        super.onPlaybackParametersChanged(playbackParameters)

        // Offloaded AudioTrack may ignore or reject non-default speed and pitch.
        val defaultParameters = playbackParameters.speed == 1f && playbackParameters.pitch == 1f
        player.setOffloadEnabled(cachedOffloadEnabled && defaultParameters)

        if (playbackParameters.speed != lastPlaybackSpeed) {
            lastPlaybackSpeed = playbackParameters.speed
            discordUpdateJob?.cancel()

            discordUpdateJob = scope.launch {
                delay(1000)
                if (player.playWhenReady && player.playbackState == Player.STATE_READY) {
                    currentSong.value?.let { song ->
                        updateDiscordRPC(song)
                    }
                }
            }
        }
    }

    /** Add HTTP, network, retry, source, and cause details to playback error logs. */
    private fun logPlayerErrorDetail(error: PlaybackException, mediaId: String?) {
        val httpCode = PlaybackErrorClassifier.getHttpResponseCode(error)?.toString() ?: "-"
        val transport = runCatching {
            val net = connectivityManager.activeNetwork
            if (net == null) "none"
            else {
                val caps = connectivityManager.getNetworkCapabilities(net)
                when {
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WIFI"
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "CELLULAR"
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "ETHERNET"
                    else -> "OTHER"
                }
            }
        }.getOrDefault("unknown")
        val source = player.currentMediaItem?.localConfiguration?.uri?.let { uri ->
            "${uri.host ?: "?"}(${uri.path?.length ?: 0}p,${uri.query?.length ?: 0}q)"
        } ?: "?"
        val retries = mediaId?.let { currentMediaIdRetryCount[it] ?: 0 } ?: 0

        Timber.tag(TAG).w(
            "Player error detail | mediaId=$mediaId | errorCode=${error.errorCode} | http=$httpCode | " +
                "net=${if (isNetworkConnected.value) "connected" else "disconnected"}/$transport | " +
                "retries=$retries | source=$source | state=${player.playbackState}"
        )
        var cause = error.cause
        var depth = 0
        while (cause != null && depth < 6) {
            Timber.tag(TAG).w("Player error cause #$depth: ${cause::class.java.simpleName}: ${cause.message}")
            cause = cause.cause
            depth++
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

        // Ignore delayed errors after the player has been released.
        if (!playerInitialized.value) {
            Timber.tag(TAG).e(error, "Player error occurred but player not initialized")
            return
        }

        val mediaId = player.currentMediaItem?.mediaId
        logPlayerErrorDetail(error, mediaId)
        Timber.tag(TAG).w(error, "Player error occurred for $mediaId: errorCode=${error.errorCode}, message=${error.message}")
        reportException(error)

        if (recoverCodecFailure(error, mediaId)) return

        if (mediaId != null && errorRecovery.hasExceededRetryLimit(mediaId)) {
            Timber.tag(TAG).w("Song $mediaId has exceeded retry limit, skipping")
            errorRecovery.markSongAsFailed(mediaId)
            errorRecovery.handleFinalFailure()
            return
        }

        if (mediaId != null) {
            errorRecovery.performAggressiveCacheClear(mediaId)
        }

        val errorType = PlaybackErrorClassifier.classify(error, isNetworkConnected.value)
        if (handleClassifiedError(errorType, error, mediaId)) return

        // For IO_UNSPECIFIED and IO_BAD_HTTP_STATUS, try recovery first
        if (error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
            Timber.tag(TAG).d("IO error detected (${error.errorCode}), attempting recovery")
            errorRecovery.handleGenericIOError(mediaId)
            return
        }

        if (dataStore.get(AutoSkipNextOnErrorKey, false)) {
            Timber.tag(TAG).d("Auto-skipping to next track due to unrecoverable error")
            errorRecovery.skipOnError()
        } else {
            Timber.tag(TAG).d("Stopping playback due to unrecoverable error")
            errorRecovery.stopOnError()
        }
    }

    private fun handleClassifiedError(
        errorType: PlaybackErrorType,
        error: PlaybackException,
        mediaId: String?,
    ): Boolean {
        when (errorType) {
            PlaybackErrorType.DecoderReclaim -> {
                Timber.tag(TAG).d("FLAC decoder reclaim detected, retrying with fresh codec")
                errorRecovery.handleDecoderReclaimError(mediaId)
            }
            PlaybackErrorType.AudioRenderer -> {
                Timber.tag(TAG).d("AudioTrack error detected (${error.errorCode}), performing safe recovery")
                errorRecovery.handleAudioRendererError(mediaId)
            }
            PlaybackErrorType.RangeNotSatisfiable -> {
                Timber.tag(TAG).d("Range Not Satisfiable (416) detected, performing strict recovery")
                errorRecovery.handleRangeNotSatisfiableError(mediaId)
            }
            PlaybackErrorType.PageReload -> {
                Timber.tag(TAG).d("Page reload error detected, performing strict recovery")
                errorRecovery.handlePageReloadError(mediaId)
            }
            PlaybackErrorType.ExpiredUrl -> {
                Timber.tag(TAG).d("Expired URL (403) detected, refreshing stream URL")
                errorRecovery.handleExpiredUrlError(mediaId)
            }
            PlaybackErrorType.Parsing -> {
                Timber.tag(TAG).d("Parsing error detected (${error.errorCode}), attempting recovery")
                errorRecovery.handleParsingError(mediaId)
            }
            PlaybackErrorType.Network -> handleNetworkPlaybackError()
            PlaybackErrorType.Other -> return false
        }
        return true
    }

    private fun handleNetworkPlaybackError() {
        // Skip to downloaded tracks offline instead of waiting on an unavailable stream.
        if (!isNetworkConnected.value) {
            val nextOfflineIndex = streamResolver.nextOfflinePlayableIndex()
            if (nextOfflineIndex != null) {
                Timber.tag(TAG).d("Offline: skipping to downloaded item $nextOfflineIndex")
                player.seekTo(nextOfflineIndex, 0L)
                player.prepare()
                player.play()
                return
            }
        }

        Timber.tag(TAG).d("Network-related error detected, waiting for connection")
        errorRecovery.waitOnNetworkError()
    }

    private fun recoverCodecFailure(error: PlaybackException, mediaId: String?): Boolean {
        mediaId ?: return false

        val failedFormat = (error as? androidx.media3.exoplayer.ExoPlaybackException)?.rendererFormat
            ?: player.audioFormat
        val errorCode = error.errorCode
        val isDecoderFailure = errorCode in DECODER_FAILURE_CODES
        val isLosslessAudioFailure = errorCode in LOSSLESS_AUDIO_FAILURE_CODES
        val isLocalMedia = mediaId.startsWith("content://") || mediaId.startsWith("file://") ||
            mediaId.startsWith(com.convx.storage.domain.NetworkMediaUri.Scheme + "://")
        val isOpus = failedFormat?.sampleMimeType == androidx.media3.common.MimeTypes.AUDIO_OPUS ||
            failedFormat?.codecs?.contains("opus", ignoreCase = true) == true
        val canRetryOpusAsAac = !isLocalMedia &&
            !downloadCache.isCached(downloadKey(mediaId), 0, 1) &&
            isOpus &&
            isDecoderFailure

        // Retry an online Opus decoder failure once using AAC. Local files and
        // downloads keep their original audio; Media3 can still use another device decoder.
        if (canRetryOpusAsAac && forceAacAudioMediaIds.add(mediaId)) {
            forceStandardAudioMediaIds.add(mediaId)
            errorRecovery.performAggressiveCacheClear(mediaId)
            val index = player.currentMediaItemIndex
            val position = player.currentPosition
            player.currentMediaItem?.let { player.replaceMediaItem(index, it) }
            player.seekTo(index, position)
            player.prepare()
            return true
        }

        // A module codec that cannot initialize needs ordinary audio before retrying its URL.
        if (losslessStreamMediaIds.contains(mediaId) &&
            isLosslessAudioFailure && !PlaybackErrorClassifier.isDecoderReclaimError(error)
        ) {
            losslessStallGuard.triggerLosslessFallback(mediaId)
            return true
        }

        return false
    }

    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
        super.onDeviceVolumeChanged(volume, muted)
        val pauseOnMute = dataStore.get(PauseOnMute, false)

        if ((volume == 0 || muted) && pauseOnMute) {
            if (player.isPlaying) {
                wasPlayingBeforeVolumeMute = true
                isPausedByVolumeMute = true
                player.pause()
            }
        } else if (volume > 0 && !muted && pauseOnMute) {
            if (wasPlayingBeforeVolumeMute && !player.isPlaying && castConnectionHandler?.isCasting?.value != true) {
                wasPlayingBeforeVolumeMute = false
                isPausedByVolumeMute = false
                player.play()
            }
        }
    }

    /** Builds the OkHttp/cache layer shared by playback and the track analyzer (see AudioCache.analyzerUpstream). */
    inner class AudioSourceBuilder {

        fun youtubeUpstreamFactory(): DefaultDataSource.Factory {
            val ytProxy = YouTube.proxy
            val ytProxyAuth = YouTube.proxyAuth
            return DefaultDataSource.Factory(
                this@MusicService,
                ChunkedDataSource.Factory(OkHttpDataSource.Factory(
                    OkHttpClient
                        .Builder()
                        .dns(object : Dns {
                            override fun lookup(hostname: String): List<InetAddress> {
                                val addresses = Dns.SYSTEM.lookup(hostname)
                                return when (this@MusicService.ipVersion) {
                                    IpVersion.IPV4 -> addresses.filter { it is Inet4Address }.ifEmpty { addresses }
                                    IpVersion.IPV6 -> addresses.filter { it is Inet6Address }.ifEmpty { addresses }
                                    IpVersion.AUTO -> addresses
                                }
                            }
                        })
                        .proxySelector(object : java.net.ProxySelector() {
                            override fun select(uri: java.net.URI?): List<java.net.Proxy> {
                                if (ytProxy == null) return listOf(java.net.Proxy.NO_PROXY)
                                val host = uri?.host ?: return listOf(ytProxy)
                                return if (host.contains("googlevideo") || host.contains("youtube")) {
                                    listOf(ytProxy)
                                } else {
                                    listOf(java.net.Proxy.NO_PROXY)
                                }
                            }
                            override fun connectFailed(uri: java.net.URI?, sa: java.net.SocketAddress?, ioe: java.io.IOException?) {}
                        })
                        .proxyAuthenticator { _, response ->
                            ytProxyAuth?.let { auth ->
                                response.request.newBuilder()
                                    .header("Proxy-Authorization", auth)
                                    .build()
                            } ?: response.request
                        }
                        .build(),
                )),
            )
        }

        fun createCacheDataSource(): CacheDataSource.Factory {
            return CacheDataSource
                .Factory()
                .setCache(downloadCache)
                .setUpstreamDataSourceFactory(
                    CacheDataSource
                        .Factory()
                        .setCache(playerCache)
                        // SMB / WebDAV songs (convx-network://) are read by the network source;
                        // every other URI falls through to the normal upstream.
                        .setUpstreamDataSourceFactory(networkRepository.dataSourceFactory(youtubeUpstreamFactory())),
                ).setCacheWriteDataSinkFactory(null)
                .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }

    private val audioSourceBuilder = AudioSourceBuilder()

    // Flag to prevent queue saving during silence skip operations
    private var isSilenceSkipping = false
    private var stallRecoveryJob: Job? = null

    /** Recovers from long silence skips and stalled lossless streams. */
    inner class LosslessStallGuard {

        fun handleLongSilenceDetected() {
            if (!instantSilenceSkipEnabled.value) return
            if (silenceSkipJob?.isActive == true) return

            silenceSkipJob = scope.launch {
                // Debounce so short fades or transitions do not trigger a jump.
                delay(200)
                performInstantSilenceSkip()
            }
        }

        private suspend fun performInstantSilenceSkip() {
            val duration = player.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: return
            if (duration <= INSTANT_SILENCE_SKIP_STEP_MS) return

            isSilenceSkipping = true
            try {
                var hops = 0
                val silenceProcessor = playerSilenceProcessors[player] ?: return
                while (coroutineContext.isActive && instantSilenceSkipEnabled.value && silenceProcessor.isCurrentlySilent()) {
                    val current = player.currentPosition
                    val target = (current + INSTANT_SILENCE_SKIP_STEP_MS).coerceAtMost(duration - 500)

                    if (target <= current) break

                    // Reset silence tracking before seeking to prevent immediate re-trigger
                    silenceProcessor.resetTracking()
                    player.seekTo(target)
                    hops++

                    if (hops >= 60 || target >= duration - 1000) break

                    delay(INSTANT_SILENCE_SKIP_SETTLE_MS)
                }
                if (hops > 0) {
                    Timber.tag(TAG).d("Silence skip: jumped $hops times")
                }
            } finally {
                isSilenceSkipping = false
            }
        }

        /** Lossless/Atmos stream stopped producing audio while position kept
         *  advancing — no PlaybackException, so onPlayerError never fires for this.
         *  Fired from the audio renderer thread; hop to [scope] (Main) before
         *  touching the player. */
        fun handleLosslessStallDetected() {
            if (stallRecoveryJob?.isActive == true) return
            stallRecoveryJob = scope.launch {
                val mediaId = player.currentMediaItem?.mediaId ?: return@launch
                val count = (losslessStallCount[mediaId] ?: 0) + 1
                losslessStallCount[mediaId] = count
                Timber.tag(TAG).w("Lossless stall detected for $mediaId (attempt $count)")

                if (count > MAX_LOSSLESS_STALLS_BEFORE_FALLBACK) {
                    triggerLosslessFallback(mediaId)
                    return@launch
                }

                player.pause()
                delay(150)
                if (!playerInitialized.value) return@launch
                val idx = player.currentMediaItemIndex
                val pos = (player.currentPosition - 1500L).coerceAtLeast(0L)
                player.seekTo(idx, pos)
                player.prepare()
                delay(300)
                if (hasAudioFocus && playerInitialized.value) {
                    if (castConnectionHandler?.isCasting?.value != true) player.play()
                }
            }
        }

        /** Retries the current position with standard audio after repeated lossless failures. */
        fun triggerLosslessFallback(mediaId: String) {
            Timber.tag(TAG).w("Falling back to standard audio for $mediaId after repeated lossless failures")

            forceStandardAudioMediaIds.add(mediaId)
            bypassCacheForQualityChange.add(mediaId)
            // Remove cached lossless bytes so the standard decoder receives the new format.
            errorRecovery.performAggressiveCacheClear(mediaId)
            losslessStreamMediaIds.remove(mediaId)
            losslessStallCount.remove(mediaId)
            playerStallWatchdogs[player]?.armed = false

            val idx = player.currentMediaItemIndex
            val pos = player.currentPosition
            // Replace the wrapper too: an HLS child already holds the old codec
            // and URL, so preparing that child again cannot apply the fallback.
            player.currentMediaItem?.let { player.replaceMediaItem(idx, it) }
            player.seekTo(idx, pos)
            player.prepare()

            Toast.makeText(
                this@MusicService,
                getString(R.string.lossless_fallback_to_standard_audio),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val losslessStallGuard = LosslessStallGuard()

    private fun updateDiscordRPC(song: Song, showFeedback: Boolean = false) {
        val useDetails = dataStore.get(DiscordUseDetailsKey, false)
        val advancedMode = dataStore.get(DiscordAdvancedModeKey, false)

        val status = if (advancedMode) dataStore.get(DiscordStatusKey, "online") else "online"
        val b1Text = if (advancedMode) dataStore.get(DiscordButton1TextKey, "") else ""
        val b1Visible = if (advancedMode) dataStore.get(DiscordButton1VisibleKey, true) else true
        val b2Text = if (advancedMode) dataStore.get(DiscordButton2TextKey, "") else ""
        val b2Visible = if (advancedMode) dataStore.get(DiscordButton2VisibleKey, true) else true
        val activityType = if (advancedMode) dataStore.get(DiscordActivityTypeKey, "listening") else "listening"
        val activityName = if (advancedMode) dataStore.get(DiscordActivityNameKey, "") else ""

        discordUpdateJob?.cancel()
        discordUpdateJob = scope.launch {
            discordRpc?.updateSong(
                song,
                player.currentPosition,
                player.playbackParameters.speed,
                useDetails,
                status,
                b1Text,
                b1Visible,
                b2Text,
                b2Visible,
                activityType,
                activityName
            )?.onFailure {
                // Rate limited or error
                if (showFeedback) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@MusicService, "Discord RPC update failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /** Provider preferences that require resolving the queue again. */
    private fun streamSourcePreferences(prefs: androidx.datastore.preferences.core.Preferences): List<String> = listOf(
        prefs[EnabledModulesKey] ?: "[]",
        prefs[ModuleSourcesKey] ?: "[]",
        prefs[ModuleSettingsKey] ?: "{}",
        (prefs[EnableSaavnStreamingKey] ?: false).toString(),
        prefs[SaavnAudioQualityKey].orEmpty(),
        (prefs[SaavnFallbackToYouTubeKey] ?: true).toString(),
        (prefs[EnableTidalStreamingKey] ?: false).toString(),
        prefs[TidalQualityKey].orEmpty(),
        prefs[TidalInstanceUrlKey].orEmpty(),
        (prefs[ForceSelectedQualityKey] ?: false).toString(),
    )

    private data class ResolvedStream(
        val uri: android.net.Uri,
        val directFile: Boolean,
        val hls: Boolean = false,
    )

    /** Resolves stream URLs, builds data sources, and prefetches upcoming tracks. */
    inner class StreamResolver {

        /** Reads DataStore synchronously; call from a loader or IO thread. */
        fun effectiveCacheKey(mediaId: String): String {
            val prefs = runBlocking { dataStore.data.first() }
            val enabledModules = prefs[EnabledModulesKey] ?: "[]"
            val hasModules = runCatching { org.json.JSONArray(enabledModules).length() > 0 }.getOrDefault(false)
            val fallbackSuffix = if (forceAacAudioMediaIds.contains(mediaId)) "#aac" else ""
            val videoMode = mediaId in videoModeMediaIds
            val forceStandard = mediaId in forceStandardAudioMediaIds
            if (!hasModules && prefs[EnableTidalStreamingKey] != true && prefs[EnableSaavnStreamingKey] != true &&
                !videoMode && !forceStandard
            ) return "$mediaId$fallbackSuffix"
            val fingerprint = (streamSourcePreferences(prefs) + listOf(
                prefs[AudioQualityKey].orEmpty(), (prefs[DataSaverEnabledKey] ?: false).toString(),
                videoMode.toString(), forceStandard.toString(),
            )).joinToString("") { "${it.length}:$it" }
            val digest = java.security.MessageDigest.getInstance("SHA-256").digest(fingerprint.toByteArray())
                .joinToString("") { "%02x".format(it) }
            return "$mediaId#source-$digest$fallbackSuffix"
        }

        private val hlsStreamKeys = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        /** First later queue item playable with no network: a finished download, or a fully cached stream. */
        fun nextOfflinePlayableIndex(): Int? {
            val count = player.mediaItemCount
            var i = player.currentMediaItemIndex
            repeat(count - 1) {
                i = if (i + 1 < count) i + 1 else if (player.repeatMode == Player.REPEAT_MODE_ALL) 0 else return null
                val id = player.getMediaItemAt(i).mediaId
                if (id.startsWith("content://") || id.startsWith("file://") || id.startsWith("/storage/")) return i
                if (downloadCache.isCachedToEnd(downloadKey(id), 0) || playerCache.isCachedToEnd(effectiveCacheKey(id), 0)) return i
            }
            return null
        }

        /** Resolves a playback URL and updates format, cache, and database state. */
        private suspend fun fetchResolvedStream(mediaId: String, effKey: String): ResolvedStream {
            Timber.tag("MusicService").i("FETCHING STREAM: $mediaId | quality=$audioQuality")
            // Fail immediately offline; stream resolution requires a network connection.
            if (connectivityManager.activeNetwork == null) {
                throw PlaybackException(
                    getString(R.string.error_no_internet),
                    null,
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                )
            }
            val fetchStart = SystemClock.elapsedRealtime()
            val playbackData = try {
                YTPlayerUtils.playerResponseForPlayback(
                    mediaId,
                    audioQuality = audioQuality,
                    connectivityManager = connectivityManager,
                    context = this@MusicService,
                    forceStandardAudio = mediaId in forceStandardAudioMediaIds || mediaId in videoModeMediaIds,
                    preferAac = forceAacAudioMediaIds.contains(mediaId),
                ).getOrElse { throwable ->
                    throw playbackResolutionException(throwable)
                }
            } finally {
                Timber.tag(TAG).i("Stream fetch for $mediaId took ${SystemClock.elapsedRealtime() - fetchStart}ms")
            }

            if (effKey != effectiveCacheKey(mediaId)) throw java.io.IOException("Playback source changed during resolution")

            val format = playbackData.format
            val loudnessDb = playbackData.audioConfig?.loudnessDb
            val perceptualLoudnessDb = playbackData.audioConfig?.perceptualLoudnessDb

            Timber.tag(TAG).d("Storing format for $mediaId with loudnessDb: $loudnessDb, perceptualLoudnessDb: $perceptualLoudnessDb")
            if (loudnessDb == null && perceptualLoudnessDb == null) {
                Timber.tag(TAG).w("No loudness data available from YouTube for video: $mediaId")
            }

            database.query {
                upsert(
                    FormatEntity(
                        id = mediaId,
                        itag = format.itag,
                        mimeType = format.mimeType.split(";")[0],
                        codecs = format.mimeType.split("codecs=").getOrNull(1)?.removeSurrounding("\"") ?: "mp3",
                        bitrate = format.bitrate,
                        sampleRate = format.audioSampleRate,
                        contentLength = format.contentLength ?: 0L,
                        loudnessDb = loudnessDb,
                        perceptualLoudnessDb = perceptualLoudnessDb,
                        playbackUrl = playbackData.playbackTracking?.videostatsPlaybackUrl?.baseUrl
                    )
                )
            }
            scope.launch(Dispatchers.IO) { recoverSong(mediaId, playbackData) }

            // Clear bypass flag now that we've fetched fresh stream
            if (bypassCacheForQualityChange.remove(mediaId)) {
                Timber.tag(TAG).d("Cleared bypass cache flag for $mediaId after fresh fetch")
            }

            val streamUrl = playbackData.streamUrl
            if (playbackData.isHlsStream) hlsStreamKeys.add(effKey) else hlsStreamKeys.remove(effKey)
            Timber.tag("SpineDebug").d("DataSourceResolver: resolved mediaId=$mediaId isSpineStream=${playbackData.isSpineStream} isTidalStream=${playbackData.isTidalStream} hls=${playbackData.isHlsStream}")

            if (playbackData.isTidalStream || playbackData.isSpineStream) {
                losslessStreamMediaIds.add(mediaId)
            } else {
                losslessStreamMediaIds.remove(mediaId)
            }

            songUrlCache[effKey] =
                streamUrl to System.currentTimeMillis() + (playbackData.streamExpiresInSeconds * 1000L)

            return ResolvedStream(
                uri = streamUrl.toUri(),
                directFile = playbackData.isTidalStream || playbackData.isSpineStream,
                hls = playbackData.isHlsStream,
            )
        }

        private fun playbackResolutionException(cause: Throwable): PlaybackException = when (cause) {
            is PlaybackException -> cause
            is java.net.ConnectException, is java.net.UnknownHostException -> PlaybackException(
                getString(R.string.error_no_internet),
                cause,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            )
            is java.net.SocketTimeoutException -> PlaybackException(
                getString(R.string.error_timeout),
                cause,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
            )
            else -> PlaybackException(
                getString(R.string.error_unknown),
                cause,
                PlaybackException.ERROR_CODE_REMOTE_ERROR,
            )
        }

        private val prefetchInFlight = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        fun prefetchStreamUrl(mediaId: String) {
            if (mediaId.startsWith("content://") ||
                mediaId.startsWith("file://") ||
                mediaId.startsWith("/storage/")
            ) return
            if (bypassCacheForQualityChange.contains(mediaId)) return
            scope.launch(Dispatchers.IO) {
                val effKey = effectiveCacheKey(mediaId)
                if (playerCache.isCached(effKey, 0, CHUNK_LENGTH)) return@launch
                if (downloadCache.isCached(downloadKey(mediaId), 0, 1)) return@launch
                if (songUrlCache[effKey]?.takeIf { it.second > System.currentTimeMillis() } != null) return@launch
                if (!prefetchInFlight.add(effKey)) return@launch
                try {
                    runCatching { fetchResolvedStream(mediaId, effKey) }
                } finally {
                    prefetchInFlight.remove(effKey)
                }
            }
        }

        /** Pre-resolves the current and next queue items (player order). */
        fun prefetchUpcoming() {
            if (player.mediaItemCount == 0) return
            val index = player.currentMediaItemIndex.coerceAtLeast(0)
            for (i in index..minOf(index + 1, player.mediaItemCount - 1)) {
                runCatching { player.getMediaItemAt(i).mediaId }
                    .getOrNull()?.takeIf { it.isNotEmpty() }?.let(::prefetchStreamUrl)
            }
        }

        private val headPrefetchInFlight = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        /** Caches a bounded opening chunk so [TrackAnalyzer] can inspect an upcoming track early. */
        fun prefetchAnalysisHead(uri: Uri) {
            val mediaId = uri.getQueryParameter("v") ?: return
            if (mediaId.startsWith("content://") ||
                mediaId.startsWith("file://") ||
                mediaId.startsWith("/storage/")
            ) return
            if (playerCache.isCached(mediaId, 0, HEAD_PREFETCH_BYTES)) return
            if (!headPrefetchInFlight.add(mediaId)) return
            scope.launch(Dispatchers.IO) {
                try {
                    val effKey = effectiveCacheKey(mediaId)
                    val cached = songUrlCache[effKey]?.takeIf { it.second > System.currentTimeMillis() }
                    val resolved = cached?.let {
                        ResolvedStream(it.first.toUri(), directFile = false,
                            hls = effKey in hlsStreamKeys || it.first.substringBefore('?').endsWith(".m3u8", true))
                    } ?: fetchResolvedStream(mediaId, effKey)
                    if (resolved.hls) {
                        kotlinx.coroutines.withTimeout(45_000L) {
                            HlsAnalysisHead.prefetch(mediaId, resolved.uri, playerCache, audioSourceBuilder.createCacheDataSource())
                        }
                        return@launch
                    }
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setKey(mediaId)
                        .setLength(HEAD_PREFETCH_BYTES)
                        .build()
                    val source = createDataSourceFactory().createDataSource()
                    try {
                        source.open(dataSpec)
                        val buffer = ByteArray(64 * 1024)
                        // Reads populate the cache; the bytes are not needed here.
                        while (source.read(buffer, 0, buffer.size) != C.RESULT_END_OF_INPUT) {
                        }
                    } finally {
                        runCatching { source.close() }
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).d("Head prefetch failed for $mediaId: ${e.message}")
                } finally {
                    headPrefetchInFlight.remove(mediaId)
                }
            }
        }

        fun createDataSourceFactory(): DataSource.Factory {
            return ResolvingDataSource.Factory(audioSourceBuilder.createCacheDataSource()) { dataSpec ->
                val mediaId = dataSpec.key ?: error("No media id")

                // DefaultDataSource handles local URIs without YouTube resolution.
                if (mediaId.startsWith("content://") ||
                    mediaId.startsWith("file://") ||
                    mediaId.startsWith("/storage/") ||
                    mediaId.startsWith(com.convx.storage.domain.NetworkMediaUri.Scheme + "://")
                ) {
                    return@Factory dataSpec
                }

                // Downloads stay under mediaId. Answer them before effectiveCacheKey, which blocks on a
                // DataStore read: every seek reopens this resolver, so that read made seeks lag.
                // A finished quality upgrade is stored under its own key and wins over the older copy.
                val owner = downloadKey(mediaId)
                val upgradeKey = owner + UPGRADE_SUFFIX
                if (downloadCache.isCachedToEnd(upgradeKey, dataSpec.position)) {
                    return@Factory dataSpec.buildUpon().setKey(upgradeKey).build()
                }
                if (!bypassCacheForQualityChange.contains(mediaId) &&
                    downloadCache.isCachedToEnd(owner, dataSpec.position)
                ) {
                    recoverSongOnce(mediaId)
                    return@Factory if (owner == mediaId) dataSpec else dataSpec.buildUpon().setKey(owner).build()
                }

                // Streaming formats use separate cache keys.
                val effKey = effectiveCacheKey(mediaId)
                Timber.tag("SpineDebug").d("DataSourceResolver: mediaId=$mediaId effKey=$effKey")
                val spec = if (effKey == mediaId) dataSpec else dataSpec.buildUpon().setKey(effKey).build()

                val shouldBypassCache = bypassCacheForQualityChange.contains(mediaId)

                if (!shouldBypassCache) {
                    // Downloads use mediaId and are served offline only when the requested range is cached.
                    if (downloadCache.isCachedToEnd(mediaId, dataSpec.position)) {
                        recoverSongOnce(mediaId)
                        return@Factory dataSpec
                    }

                    // Limit reads to cached bytes so later requests return through this resolver.
                    val cachedSpan = playerCache.cachedSpanFrom(effKey, dataSpec.position, CHUNK_LENGTH)
                    if (cachedSpan > 0) {
                        recoverSongOnce(mediaId)
                        return@Factory spec.subrange(0, cachedSpan)
                    }

                    songUrlCache[effKey]?.takeIf { it.second > System.currentTimeMillis() }?.let {
                        recoverSongOnce(mediaId)
                        return@Factory spec.withUri(it.first.toUri())
                    }
                } else {
                    Timber.tag("MusicService").i("BYPASSING CACHE for $mediaId due to quality change")
                }

                // This synchronous API may block; prefetch usually resolves the URL first.
                val resolved = runBlocking(Dispatchers.IO) { fetchResolvedStream(mediaId, effKey) }

                // Proxies for direct files may return incorrect Content-Range headers; request the remainder at once.
                if (resolved.directFile) {
                    return@Factory spec.withUri(resolved.uri)
                }
                return@Factory spec.withUri(resolved.uri).subrange(dataSpec.uriPositionOffset, CHUNK_LENGTH)
            }
        }

        fun createMediaSourceFactory(): androidx.media3.exoplayer.source.MediaSource.Factory {
            val progressive = DefaultMediaSourceFactory(
                createDataSourceFactory(),
                // Constant-bitrate seeking gives an MP3 without a Xing/VBR header a duration and a
                // working seek bar straight away. Index seeking (the old flag here) leaves the
                // duration unknown until the whole file has been read, which hid the scrubber.
                androidx.media3.extractor.DefaultExtractorsFactory()
                    .setConstantBitrateSeekingEnabled(true)
            )
            // HLS has its own manifests, init sections and segments. Their URLs must
            // bypass the video-id resolver and keep separate cache keys.
            val adaptive = DefaultMediaSourceFactory(audioSourceBuilder.createCacheDataSource())
            return object : androidx.media3.exoplayer.source.MediaSource.Factory by progressive {
                override fun createMediaSource(mediaItem: MediaItem): androidx.media3.exoplayer.source.MediaSource =
                    ResolvingMediaSource(mediaItem) {
                        val mediaId = mediaItem.mediaId
                        if (mediaId.startsWith("content://") || mediaId.startsWith("file://") ||
                            mediaId.startsWith("/storage/")) return@ResolvingMediaSource progressive.createMediaSource(mediaItem)
                        val key = effectiveCacheKey(mediaId)
                        if (!bypassCacheForQualityChange.contains(mediaId) &&
                            (downloadCache.isCached(downloadKey(mediaId), 0, 1) || playerCache.isCached(key, 0, CHUNK_LENGTH))) {
                            return@ResolvingMediaSource progressive.createMediaSource(mediaItem)
                        }
                        val cached = songUrlCache[key]?.takeIf { it.second > System.currentTimeMillis() }
                            ?.takeUnless { bypassCacheForQualityChange.contains(mediaId) }
                        val resolved = cached?.let {
                            ResolvedStream(it.first.toUri(), directFile = false,
                                hls = key in hlsStreamKeys || it.first.substringBefore('?').endsWith(".m3u8", true))
                        } ?: fetchResolvedStream(mediaId, key)
                        if (resolved.hls) {
                            adaptive.createMediaSource(mediaItem.buildUpon().setUri(resolved.uri)
                                .setCustomCacheKey(null).setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8).build())
                        } else {
                            progressive.createMediaSource(mediaItem)
                        }
                    }
            }
        }

        fun createRenderersFactory(
            eqProcessor: CustomEqualizerAudioProcessor,
            silenceProcessor: SilenceDetectorAudioProcessor,
            stallWatchdog: LosslessStallWatchdogAudioProcessor,
            bpmAnalyzer: TrackAnalyzerAudioProcessor,
            spatialProcessor: SpatialAudioProcessor,
            vocalSuppressionProcessor: VocalSuppressionAudioProcessor,
            djFilter: DjFilterAudioProcessor,
            djTail: DjTailAudioProcessor,
            djDelay: DelayAudioProcessor,
            djAmbience: DjAmbienceAudioProcessor,
            analysisOnly: Boolean,
        ) =
            object : NextRenderersFactory(this@MusicService) {
                override fun buildAudioSink(
                    context: Context,
                    enableFloatOutput: Boolean,
                    enableAudioTrackPlaybackParams: Boolean,
                ) = DefaultAudioSink
                    .Builder(this@MusicService)
                    // The effects chain consumes PCM16, including decoded hi-res.
                    .setEnableFloatOutput(false)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    .setAudioProcessorChain(
                        DefaultAudioSink.DefaultAudioProcessorChain(
                            if (analysisOnly) arrayOf(bpmAnalyzer) else arrayOf(
                                eqProcessor,
                                silenceProcessor,
                                stallWatchdog,
                                bpmAnalyzer,
                                // After the analyzer so BPM/key detection still sees the
                                // untouched mix, before the DJ chain so a transition's own
                                // low end isn't fed back in by the widener's crossfeed.
                                spatialProcessor,
                                vocalSuppressionProcessor,
                                djFilter,
                                djTail,
                                djDelay,
                                djAmbience,
                            ),
                            SilenceSkippingAudioProcessor(2_000_000, 20_000, 256),
                            SonicAudioProcessor(),
                        ),
                    ).build().let { sink ->
                        com.convx.music.playback.audio.PcmProcessingAudioSink(sink) { cachedOffloadEnabled && !analysisOnly }
                    }
            }.apply {
                // Platform MediaCodec has no ALAC decoder on most Android devices (no AOSP
                // one exists at all). ON tries the platform decoder first per format and
                // only falls back to NextRenderersFactory's bundled FfmpegAudioRenderer —
                // this doesn't change decode for any format a device already handles.
                setExtensionRendererMode(androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                setEnableDecoderFallback(true)
            }
    }

    private val streamResolver = StreamResolver()

    // PlaybackStatsListener throws a message-less IllegalArgumentException when an event
    // carries an older timestamp than its tracker state; a stats glitch must not kill playback.
    private fun crashSafe(inner: AnalyticsListener): AnalyticsListener =
        java.lang.reflect.Proxy.newProxyInstance(
            AnalyticsListener::class.java.classLoader,
            arrayOf(AnalyticsListener::class.java),
        ) { _, method, args ->
            try {
                method.invoke(inner, *(args ?: emptyArray()))
            } catch (e: java.lang.reflect.InvocationTargetException) {
                if (e.cause !is IllegalArgumentException) throw e.cause ?: e
                reportException(e.cause!!)
                null
            }
        } as AnalyticsListener


    override fun onPlaybackStatsReady(
        eventTime: AnalyticsListener.EventTime,
        playbackStats: PlaybackStats,
    ) {
        val mediaItem = eventTime.timeline.getWindow(eventTime.windowIndex, Timeline.Window()).mediaItem
        val historyDurationMs = dataStore[HistoryDuration]?.times(1000f) ?: 30000f

        if (playbackStats.totalPlayTimeMs >= historyDurationMs &&
            !dataStore.get(PauseListenHistoryKey, false)
        ) {
            // Coalesce stats writes: ExoPlayer reports every ~3s during playback, so
            // without this a single listen inserts a history row and invalidates the
            // events/stats flows every few seconds. Writing at most once per song per
            // window keeps short listens recorded while stopping the storm.
            val now = System.currentTimeMillis()
            if (now - (lastStatsWriteAt[mediaItem.mediaId] ?: 0L) >= STATS_WRITE_INTERVAL_MS) {
                lastStatsWriteAt[mediaItem.mediaId] = now
                database.query {
                    incrementTotalPlayTime(mediaItem.mediaId, playbackStats.totalPlayTimeMs)
                    try {
                        insert(
                            Event(
                                songId = mediaItem.mediaId,
                                timestamp = LocalDateTime.now(),
                                playTime = playbackStats.totalPlayTimeMs,
                            ),
                        )
                    } catch (_: SQLException) {
                        // A track can be removed between playback and event insertion.
                    }
                }
            }
        }

        if (playbackStats.totalPlayTimeMs >= historyDurationMs) {
            CoroutineScope(Dispatchers.IO).launch {
                val playbackUrl = database.format(mediaItem.mediaId).first()?.playbackUrl
                    ?: YTPlayerUtils.playerResponseForMetadata(mediaItem.mediaId, null)
                        .getOrNull()?.playbackTracking?.videostatsPlaybackUrl?.baseUrl
                playbackUrl?.let {
                    YouTube.registerPlayback(null, playbackUrl)
                        .onFailure {
                            reportException(it)
                        }
                }
            }
        }
    }

    /** Queue/automix/player-state persistence to disk, used for crash/kill recovery. */
    inner class QueuePersistenceRepository {
        private var lastWrittenSnapshot: PersistedPlaybackSnapshot? = null

        // ExoPlayer state must be read on its application thread (main), so only the
        // file IO goes to the worker; the single worker serializes saves so a newer
        // snapshot can't be overwritten by an older one.
        fun saveQueueToDisk() {
            val queue = currentQueue
            val title = queueTitle
            val items = player.mediaItems.mapNotNull { it.metadata }
            if (items.isEmpty()) return
            val mediaItemIndex = player.currentMediaItemIndex
            val position = player.currentPosition
            val playWhenReady = player.playWhenReady
            val repeatMode = player.repeatMode
            val shuffleModeEnabled = player.shuffleModeEnabled
            val volume = playerVolume.value
            val playbackState = player.playbackState
            val automixItems = automixItems.value.mapNotNull { it.metadata }
            val snapshot = PersistedPlaybackSnapshot(
                queue = queue.toPersistQueue(title, items, mediaItemIndex, position),
                automix = PersistQueue(
                    title = "automix",
                    items = automixItems,
                    mediaItemIndex = 0,
                    position = 0,
                ),
                playerState = PersistPlayerState(
                    playWhenReady = playWhenReady,
                    repeatMode = repeatMode,
                    shuffleModeEnabled = shuffleModeEnabled,
                    volume = volume,
                    currentPosition = position,
                    currentMediaItemIndex = mediaItemIndex,
                    playbackState = playbackState,
                ),
            )
            persistenceScope.launch {
                persistQueueToDisk(snapshot)
            }
        }

        private fun persistQueueToDisk(snapshot: PersistedPlaybackSnapshot) {
            if (snapshot == lastWrittenSnapshot) return
            try {
                PlaybackStateStore(filesDir.resolve(PERSISTENT_PLAYBACK_SNAPSHOT_FILE)).write(snapshot)
                lastWrittenSnapshot = snapshot
                deleteLegacyPlaybackFiles()
                Timber.tag(TAG).d("Playback state saved successfully")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to save playback state")
                reportException(e)
            }
        }
    }

    private val queueRepository = QueuePersistenceRepository()

    override fun onDestroy() {
        isRunning = false

        try {
            unregisterReceiver(screenStateReceiver)
        } catch (_: Exception) {
            // The receiver may already be unregistered during service teardown.
        }
        audioManager.unregisterAudioDeviceCallback(audioDeviceCallback)
        castConnectionHandler?.release()
        if (dataStore.get(PersistentQueueKey, true)) {
            queueRepository.saveQueueToDisk()
        }
        if (discordRpc?.isRpcRunning() == true) {
            discordRpc?.closeRPC()
        }
        discordRpc = null
        connectivityObserver.unregister()
        audioResources.releaseWifiLock()
        audioResources.abandonAudioFocus()
        audioEffects.releaseLoudnessEnhancer()
        djEngine.release()
        crossfade?.release()
        trackAnalyzer?.release()
        sparePlayer?.let(::releasePlaybackPlayer)
        sparePlayer = null
        mediaSession.release()
        mediaLibrarySessionCallback.release()
        player.removeListener(this)
        player.removeListener(sleepTimer)
        releasePlaybackPlayer(player)
        discordUpdateJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = super.onBind(intent) ?: binder.also { uiClients++ }

    override fun onUnbind(intent: Intent?): Boolean {
        // Media3's own actions (session / browser interfaces) are not the app UI.
        if (intent?.action == null) uiClients = (uiClients - 1).coerceAtLeast(0)
        return super.onUnbind(intent)
    }

    /** Stops playback when the app task is removed, if the user enabled that setting. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (dataStore.get(StopMusicOnTaskClearKey, false)) {
            Timber.tag(TAG).d("Task removed with stop-on-task-clear enabled, stopping playback")
            player.stop()
            // Release the session so its notification also disappears.
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    /** Catches platform refusal when Media3 starts foreground service from a notification callback. */
    override fun startForegroundService(service: Intent): ComponentName? =
        try {
            super.startForegroundService(service)
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Foreground service start refused by platform")
            null
        }

    // Media3 calls final Service.startForeground asynchronously; CrashHandler handles its fallback.

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MusicWidgetReceiver.ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
                notificationUpdater.updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_LIKE -> {
                toggleLike()
            }
            MusicWidgetReceiver.ACTION_NEXT -> {
                player.seekToNext()
                notificationUpdater.updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_PREVIOUS -> {
                player.seekToPrevious()
                notificationUpdater.updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_UPDATE_WIDGET -> {
                notificationUpdater.updateWidgetUI(player.isPlaying)
                // A widget was just placed or resized; pick up the new widget set.
                notificationUpdater.restartWidgetProgressTicker()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Repaints widget progress only while playing, with the screen on, and when a
     * widget that displays progress is placed. Lyrics widgets keep the fast rate
     * so their highlighted line follows playback.
     */
    private var widgetProgressJob: Job? = null

    /** Media-session custom layout + home-screen widget state — the "what the OS shows" surface. */
    inner class PlaybackNotificationUpdater {

        fun updateNotification() {
            mediaSession.setCustomLayout(
                listOf(
                    CommandButton
                        .Builder()
                        .setDisplayName(
                            getString(
                                if (currentSong.value?.song?.liked ==
                                    true
                                ) {
                                    R.string.action_remove_like
                                } else {
                                    R.string.action_like
                                },
                            ),
                        )
                        .setIconResId(if (currentSong.value?.song?.liked == true) R.drawable.ic_heart else R.drawable.ic_heart_outline)
                        .setSessionCommand(CommandToggleLike)
                        .setEnabled(currentSong.value != null)
                        .build(),
                    CommandButton
                        .Builder()
                        .setDisplayName(
                            getString(
                                when (player.repeatMode) {
                                    REPEAT_MODE_OFF -> R.string.repeat_mode_off
                                    REPEAT_MODE_ONE -> R.string.repeat_mode_one
                                    REPEAT_MODE_ALL -> R.string.repeat_mode_all
                                    else -> throw IllegalStateException()
                                },
                            ),
                        ).setIconResId(
                            when (player.repeatMode) {
                                REPEAT_MODE_OFF -> R.drawable.repeat
                                REPEAT_MODE_ONE -> R.drawable.repeat_one_on
                                REPEAT_MODE_ALL -> R.drawable.repeat_on
                                else -> throw IllegalStateException()
                            },
                        ).setSessionCommand(CommandToggleRepeatMode)
                        .build(),
                    CommandButton
                        .Builder()
                        .setDisplayName(getString(if (player.shuffleModeEnabled) R.string.action_shuffle_off else R.string.action_shuffle_on))
                        .setIconResId(if (player.shuffleModeEnabled) R.drawable.shuffle_on else R.drawable.shuffle)
                        .setSessionCommand(CommandToggleShuffle)
                        .build(),
                    CommandButton.Builder()
                        .setDisplayName(getString(R.string.start_radio))
                        .setIconResId(R.drawable.radio)
                        .setSessionCommand(CommandToggleStartRadio)
                        .setEnabled(currentSong.value != null)
                        .build(),
                ),
            )
        }

        fun restartWidgetProgressTicker() {
            widgetProgressJob?.cancel()
            widgetProgressJob = null
            if (!player.isPlaying) return

            val appWidgetManager = AppWidgetManager.getInstance(this@MusicService)
            fun placed(receiver: Class<*>) =
                appWidgetManager.getAppWidgetIds(ComponentName(this@MusicService, receiver)).isNotEmpty()
            val hasLyricsWidget = placed(LyricsWidgetReceiver::class.java)
            val hasProgressWidget = hasLyricsWidget ||
                placed(NowPlayingWidgetReceiver::class.java) ||
                placed(MusicWidgetReceiver::class.java)
            if (!hasProgressWidget) return
            val interval = if (hasLyricsWidget) WIDGET_LYRICS_INTERVAL_MS else WIDGET_PROGRESS_INTERVAL_MS

            widgetProgressJob = scope.launch {
                val powerManager = getSystemService(PowerManager::class.java)
                while (isActive) {
                    delay(interval)
                    if (powerManager?.isInteractive != false) {
                        updateWidgetUI(player.isPlaying)
                    }
                }
            }
        }

        fun updateWidgetUI(isPlaying: Boolean) {
            scope.launch {
                try {
                    val songData = currentSong.value
                    val song = songData?.song
                    val songTitle = song?.title ?: getString(R.string.no_song_playing)
                    val artistName = songData?.artists?.joinToString(", ") { it.name } ?: getString(R.string.tap_to_open)
                    val isLiked = songData?.song?.liked == true

                    widgetManager.updateWidgets(
                        title = songTitle,
                        artist = artistName,
                        artworkUri = song?.thumbnailUrl,
                        isPlaying = isPlaying,
                        isLiked = isLiked,
                        duration = if (player.duration != C.TIME_UNSET) player.duration else 0,
                        currentPosition = player.currentPosition,
                        mediaId = song?.id,
                    )
                } catch (e: Exception) {
                    reportException(e)
                }
            }
        }
    }

    private val notificationUpdater = PlaybackNotificationUpdater()

    private fun shareSong() {
        val songData = currentSong.value
        val songId = songData?.song?.id ?: return

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, com.convx.music.utils.ShareUtils.songUrl(songId))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /** Fetches a playback URL for Cast. */
    suspend fun getStreamUrl(mediaId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val playbackData = YTPlayerUtils.playerResponseForPlayback(
                    videoId = mediaId,
                    audioQuality = audioQuality,
                    connectivityManager = connectivityManager,
                    context = this@MusicService,
                ).getOrNull()
                playbackData?.streamUrl
            } catch (e: Exception) {
                timber.log.Timber.e(e, "Failed to get stream URL for Cast")
                null
            }
        }
    }

    private fun initializeCast() {
        if (dataStore.get(com.convx.music.constants.EnableGoogleCastKey, true)) {
            try {
                castConnectionHandler = CastConnectionHandler(this, scope, this)
                castConnectionHandler?.initialize()
                timber.log.Timber.d("Google Cast initialized")
            } catch (e: Exception) {
                timber.log.Timber.e(e, "Failed to initialize Google Cast")
            }
        }
    }


    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        Timber.tag(TAG).d(
            "SKIP_DEBUG onPositionDiscontinuity: reason=$reason (0=REPEAT,1=SEEK,2=SEEK_ADJUSTMENT," +
                "3=SKIP,4=REMOVE,5=INTERNAL) oldIndex=${oldPosition.mediaItemIndex} " +
                "oldId=${oldPosition.mediaItem?.mediaId} newIndex=${newPosition.mediaItemIndex} " +
                "newId=${newPosition.mediaItem?.mediaId}"
        )
    }

    /**
     * Called by [CrossfadeController] when the incoming track becomes audible.
     * Swaps the session player, audio focus, listeners, and notification onto
     * the incoming player and releases the old one.
     */
    private fun adoptPlayer(outgoing: ExoPlayer, incoming: ExoPlayer, fadeMs: Long) {
        outgoing.removeListener(this@MusicService)
        outgoing.removeListener(sleepTimer)

        incoming.addListener(this@MusicService)
        incoming.addListener(sleepTimer)
        sleepTimer.player = incoming

        player = incoming
        sparePlayer = outgoing
        _crossfadeDurationMs.value = fadeMs
        _playerFlow.value = player

        try {
            (mediaSession as MediaSession).player = player
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Failed to swap player in MediaSession")
        }

        if (incoming.shuffleModeEnabled) {
            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            applyShuffleOrder(incoming.currentMediaItemIndex, incoming.mediaItemCount, shufflePlaylistFirst)
        }

        sleepTimer.notifySongTransition()
        if (player.isPlaying) audioResources.acquireWifiLock()
    }

    private data class ScrobbleTrack(
        val title: String,
        val artistNames: String,
        val releaseName: String,
        val durationMs: Long,
    )

    private suspend fun scrobbleTrack(mediaId: String): ScrobbleTrack? {
        val metadata = player.mediaItems.find { it.mediaId == mediaId }?.metadata
        val song = if (metadata == null) database.song(mediaId).first() else null
        val title = metadata?.title ?: song?.song?.title ?: return null

        return ScrobbleTrack(
            title = title,
            artistNames = metadata?.artists?.joinToString(" & ") { it.name }
                ?: song?.artists?.joinToString(" & ").orEmpty(),
            releaseName = metadata?.album?.title ?: song?.album?.title.orEmpty(),
            durationMs = metadata?.duration?.takeIf { it != -1 }?.times(1000L)
                ?: song?.song?.duration?.takeIf { it != -1 }?.times(1000L)
                ?: 0L,
        )
    }

    /** Sends ListenBrainz "now playing" and finished listen events. */
    inner class ScrobbleCoordinator {

        fun updateListenBrainz(
            title: String,
            artistNames: String,
            releaseName: String,
            durationMs: Long,
            isFinished: Boolean,
            startMs: Long = 0,
            endMs: Long = 0,
            positionMs: Long = 0,
        ) {
            val cleanToken = listenBrainzToken.trim()
            if (!listenBrainzEnabled || cleanToken.isBlank()) return
            scope.launch {
                if (isFinished) {
                    com.convx.music.ui.screens.settings.ListenBrainzManager.submitFinished(
                        context = this@MusicService,
                        token = cleanToken,
                        title = title,
                        artistNames = artistNames,
                        releaseName = releaseName,
                        durationMs = durationMs,
                        startMs = startMs,
                        endMs = endMs
                    )
                } else {
                    com.convx.music.ui.screens.settings.ListenBrainzManager.submitPlayingNow(
                        context = this@MusicService,
                        token = cleanToken,
                        title = title,
                        artistNames = artistNames,
                        releaseName = releaseName,
                        durationMs = durationMs,
                        positionMs = positionMs
                    )
                }
            }
        }

        fun checkAndSubmitListenBrainzFinished() {
            listenBrainzCurrentMediaId?.let { mediaId ->
                val startTs = listenBrainzCurrentStartTs
                if (startTs > 0) {
                    scope.launch {
                        val track = scrobbleTrack(mediaId) ?: return@launch
                        updateListenBrainz(
                            title = track.title,
                            artistNames = track.artistNames,
                            releaseName = track.releaseName,
                            durationMs = track.durationMs,
                            isFinished = true,
                            startMs = startTs,
                            endMs = System.currentTimeMillis(),
                        )
                    }
                }
            }
            listenBrainzCurrentStartTs = 0L
            listenBrainzCurrentMediaId = null
        }

        fun checkAndSubmitListenBrainzPlayingNow(mediaId: String) {
            scope.launch {
                val track = scrobbleTrack(mediaId) ?: return@launch
                updateListenBrainz(
                    title = track.title,
                    artistNames = track.artistNames,
                    releaseName = track.releaseName,
                    durationMs = track.durationMs,
                    isFinished = false,
                )
            }
        }
    }

    private val scrobbleCoordinator = ScrobbleCoordinator()

    companion object {
        /** Widget progress repaint cadence while playing with the screen on. */
        private const val WIDGET_PROGRESS_INTERVAL_MS = 1000L
        private const val WIDGET_LYRICS_INTERVAL_MS = 200L
        private val DECODER_FAILURE_CODES = setOf(
            PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES,
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FAILED,
        )
        private val LOSSLESS_AUDIO_FAILURE_CODES = DECODER_FAILURE_CODES + setOf(
            PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED,
            PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED,
        )
        /** Paused-and-backgrounded time before the service stops itself; matches Media3's FGS timeout. */
        private const val IdleStopMs = 10 * 60_000L
        private const val CastInitDelayMs = 3_000L
        const val ROOT = "root"
        const val SONG = "song"
        const val ARTIST = "artist"
        const val ALBUM = "album"
        const val PLAYLIST = "playlist"
        const val YOUTUBE_PLAYLIST = "youtube_playlist"
        const val SEARCH = "search"
        const val SHUFFLE_ACTION = "__shuffle__"

        const val CHANNEL_ID = "music_channel_01"
        const val NOTIFICATION_ID = 888
        const val ERROR_CODE_NO_STREAM = 1000001
        private const val CHUNK_LENGTH = 10 * 1024 * 1024L // 10MB chunk instead of 512KB to prevent cutoffs
        // ~35s at a typical 160kbps stream — comfortably past TrackAnalyzer's
        // MIN_HEAD_SECONDS (12s) and close to its full head window (~30s), so
        // one prefetch usually satisfies the head pass without a second round.
        private const val HEAD_PREFETCH_BYTES = 700 * 1024L
        const val PERSISTENT_QUEUE_FILE = "persistent_queue.data"
        const val PERSISTENT_AUTOMIX_FILE = "persistent_automix.data"
        const val PERSISTENT_PLAYER_STATE_FILE = "persistent_player_state.data"
        const val PERSISTENT_PLAYBACK_SNAPSHOT_FILE = "persistent_playback_snapshot.data"
        const val MAX_CONSECUTIVE_ERR = 5
        const val MAX_RETRY_COUNT = 10
        // Constants for audio normalization
        private const val MAX_GAIN_MB = 300 // Maximum gain in millibels (3 dB)
        private const val MIN_GAIN_MB = -1500 // Minimum gain in millibels (-15 dB)

        private const val TAG = "MusicService"

        /** Ceiling on how long an audio duck is allowed to hold the volume at
         *  20% before it is force-restored (some devices never send the gain). */
        private const val DUCK_RESTORE_MS = 10_000L

        @Volatile
        var isRunning = false
            private set
    }
}
