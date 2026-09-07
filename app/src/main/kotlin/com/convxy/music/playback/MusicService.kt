/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:Suppress("DEPRECATION")

package com.convxy.music.playback

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
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Timeline
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
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
import com.convxy.music.MainActivity
import com.convxy.music.R
import com.convxy.music.constants.AudioNormalizationKey
import com.convxy.music.constants.AudioOffload
import com.convxy.music.constants.AudioQualityKey
import com.convxy.music.constants.EnableTidalStreamingKey
import com.convxy.music.constants.EnabledModulesKey
import com.convxy.music.constants.AutoLoadMoreKey
import com.convxy.music.constants.AutoSkipNextOnErrorKey
import com.convxy.music.constants.CrossfadeDurationKey
import com.convxy.music.constants.CrossfadeEnabledKey
import com.convxy.music.constants.AutoDjMixingEnabledKey
import com.convxy.music.constants.CreativeTransitionsEnabledKey
import com.convxy.music.constants.CrossfadeGaplessKey
import com.convxy.music.constants.DisableLoadMoreWhenRepeatAllKey
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.convxy.music.constants.DiscordActivityNameKey
import com.convxy.music.constants.DiscordActivityTypeKey
import com.convxy.music.constants.DiscordAdvancedModeKey
import com.convxy.music.constants.DiscordButton1TextKey
import com.convxy.music.constants.DiscordButton1VisibleKey
import com.convxy.music.constants.DiscordButton2TextKey
import com.convxy.music.constants.DiscordButton2VisibleKey
import com.convxy.music.constants.DiscordStatusKey
import com.convxy.music.constants.DiscordTokenKey
import com.convxy.music.constants.DiscordUseDetailsKey
import com.convxy.music.constants.EnableDiscordRPCKey
import com.convxy.music.constants.EnableLastFMScrobblingKey
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.constants.HideVideoSongsKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.constants.WatchVideoKey
import com.convxy.music.constants.ListenBrainzEnabledKey
import com.convxy.music.constants.ListenBrainzTokenKey
import com.convxy.music.constants.HistoryDuration
import com.convxy.music.constants.LastFMUseNowPlaying
import com.convxy.music.constants.MediaSessionConstants.CommandToggleLike
import com.convxy.music.constants.MediaSessionConstants.CommandToggleRepeatMode
import com.convxy.music.constants.MediaSessionConstants.CommandToggleShuffle
import com.convxy.music.constants.MediaSessionConstants.CommandToggleStartRadio
import com.convxy.music.constants.PauseListenHistoryKey
import com.convxy.music.constants.PauseOnMute
import com.convxy.music.constants.PersistentQueueKey
import com.convxy.music.constants.PersistentShuffleAcrossQueuesKey
import com.convxy.music.constants.PlayerVolumeKey
import com.convxy.music.constants.RememberShuffleAndRepeatKey
import com.convxy.music.constants.RepeatModeKey
import com.convxy.music.constants.ResumeOnBluetoothConnectKey
import com.convxy.music.constants.ScrobbleDelayPercentKey
import com.convxy.music.constants.ScrobbleDelaySecondsKey
import com.convxy.music.constants.ScrobbleMinSongDurationKey
import com.convxy.music.constants.ShowLyricsKey
import com.convxy.music.constants.ShuffleModeKey
import com.convxy.music.constants.ShufflePlaylistFirstKey
import com.convxy.music.constants.PreventDuplicateTracksInQueueKey
import com.convxy.music.constants.SimilarContent
import com.convxy.music.constants.SkipSilenceInstantKey
import com.convxy.music.constants.SkipSilenceKey
import com.convxy.music.constants.IpVersionKey
import com.music.innertube.models.IpVersion
import okhttp3.Dns
import java.net.InetAddress
import java.net.Inet4Address
import java.net.Inet6Address
import com.convxy.music.db.MusicDatabase
import com.convxy.music.db.entities.Event
import com.convxy.music.db.entities.FormatEntity
import com.convxy.music.db.entities.LyricsEntity
import com.convxy.music.db.entities.RelatedSongMap
import com.convxy.music.db.entities.Song
import com.convxy.music.di.DownloadCache
import com.convxy.music.di.PlayerCache
import com.convxy.music.eq.EqualizerService
import com.convxy.music.eq.audio.CustomEqualizerAudioProcessor
import com.convxy.music.eq.data.EQProfileRepository
import com.convxy.music.extensions.SilentHandler
import com.convxy.music.extensions.collect
import com.convxy.music.extensions.collectLatest
import com.convxy.music.extensions.currentMetadata
import com.convxy.music.extensions.findNextMediaItemById
import com.convxy.music.extensions.mediaItems
import com.convxy.music.extensions.metadata
import com.convxy.music.extensions.setOffloadEnabled
import com.convxy.music.extensions.toEnum
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.extensions.toPersistQueue
import com.convxy.music.extensions.toQueue
import com.convxy.music.lyrics.LyricsHelper
import com.convxy.music.models.MediaMetadata
import com.convxy.music.models.PersistPlayerState
import com.convxy.music.models.PersistQueue
import com.convxy.music.models.toMediaMetadata
import com.convxy.music.playback.audio.TrackAnalyzerAudioProcessor
import com.convxy.music.playback.dj.DjEngine
import com.convxy.music.playback.audio.DelayAudioProcessor
import com.convxy.music.playback.audio.DjFilterAudioProcessor
import com.convxy.music.playback.audio.DjTailAudioProcessor
import com.convxy.music.playback.audio.DjMixPlan
import com.convxy.music.playback.audio.DjMixTier
import com.convxy.music.playback.audio.LosslessStallWatchdogAudioProcessor
import com.convxy.music.playback.audio.SilenceDetectorAudioProcessor
import com.convxy.music.playback.queues.EmptyQueue
import com.convxy.music.playback.queues.Queue
import com.convxy.music.playback.queues.YouTubeQueue
import com.convxy.music.playback.queues.filterExplicit
import com.convxy.music.playback.queues.filterVideoSongs
import com.convxy.music.utils.CoilBitmapLoader
import com.convxy.music.utils.DiscordRPC
import com.convxy.music.utils.NetworkConnectivityObserver
import com.convxy.music.utils.ScrobbleManager
import com.convxy.music.utils.SyncUtils
import com.convxy.music.utils.YTPlayerUtils
import com.convxy.music.constants.StopMusicOnTaskClearKey
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import com.convxy.music.utils.reportException
import com.convxy.music.widget.vivimusicWidgetManager
import com.convxy.music.widget.MusicWidgetReceiver
import com.convxy.music.widget.NowPlayingWidgetReceiver
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
import java.io.ObjectOutputStream
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

private const val INSTANT_SILENCE_SKIP_STEP_MS = 15_000L
private const val INSTANT_SILENCE_SKIP_SETTLE_MS = 350L

// Minimum gap between two DB writes for the same media item from the ~3s
// onPlaybackStatsReady reports.
private const val STATS_WRITE_INTERVAL_MS = 20_000L

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
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback

    @Inject
    lateinit var equalizerService: EqualizerService

    @Inject
    lateinit var eqProfileRepository: EQProfileRepository

    @Inject
    lateinit var widgetManager: vivimusicWidgetManager

    @Inject
    lateinit var listenTogetherManager: com.convxy.music.listentogether.ListenTogetherManager

    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var lastAudioFocusState = AudioManager.AUDIOFOCUS_NONE
    private var wasPlayingBeforeAudioFocusLoss = false
    private var hasAudioFocus = false
    private var reentrantFocusGain = false
    private var wasPlayingBeforeVolumeMute = false
    private var isPausedByVolumeMute = false
    var preferredDeviceId: Int? = null //added for audio device switching
        private set//improvement

    private var crossfadeEnabled = false
    private var crossfadeDuration = 5000f
    private var crossfadeGapless = true
    private var crossfadeTriggerJob: Job? = null

    // Mirrors of settings read at ExoPlayer construction time (initial player +
    // crossfade's secondary player). Kept fresh by the dataStore.data collectors
    // in onCreate so construction never blocks the (Main-dispatcher) service
    // scope on a DataStore read â€” critical on the crossfade path, where a
    // blocking read would stall audio right as the next track needs to start.
    private var cachedSkipSilence = false
    private var cachedSkipSilenceInstant = false
    private var cachedOffloadEnabled = false

    private val secondaryPlayerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Timber.tag(TAG).e(error, "Secondary player error")
            secondaryPlayer?.stop()
            secondaryPlayer?.clearMediaItems()
            secondaryPlayer = null
        }
    }

    // SupervisorJob, not Job: a plain Job cancels every sibling the instant ANY
    // child throws uncaught — one bad scope.launch{} (e.g. a playback resolution
    // failure) was cascading into cancelling every other concurrent coroutine
    // under this scope, including unrelated in-flight track resolutions. That
    // showed up as "Parent job is Cancelling" followed by every fallback client
    // failing in under a millisecond for a completely different track — not a
    // real all-clients-blocked failure, just collateral damage from the crash.
    private var scope = CoroutineScope(Dispatchers.Main) + SupervisorJob()

    // Serialized disk writer for queue/player-state persistence: one worker keeps
    // snapshots from overwriting each other and keeps ObjectOutputStream IO (3
    // files per save) off the main and playback threads.
    private val persistenceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    // Last DB write per media id, to coalesce the ~3s ExoPlayer stats reports.
    private val lastStatsWriteAt = HashMap<String, Long>()

    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    private lateinit var connectivityManager: ConnectivityManager
    lateinit var connectivityObserver: NetworkConnectivityObserver
    val waitingForNetworkConnection = MutableStateFlow(false)
    private val isNetworkConnected = MutableStateFlow(false)

    private lateinit var audioQuality: com.convxy.music.constants.AudioQuality
    private lateinit var ipVersion: IpVersion

    private var currentQueue: Queue = EmptyQueue
    var queueTitle: String? = null

    val currentMediaMetadata = MutableStateFlow<com.convxy.music.models.MediaMetadata?>(null)
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
        // Immediately update player volume to ensure it takes effect
        player.volume = if (newMutedState) 0f else playerVolume.value
    }

    fun setMuted(muted: Boolean) {
        isMuted.value = muted
        // Immediately update player volume to ensure it takes effect
        // This handles cases where the player reference may have changed
        player.volume = if (muted) 0f else playerVolume.value
    }

    fun setPreferredAudioDevice(deviceId: Int?) { // this helps us to change between devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val deviceInfo = devices.find { it.id == deviceId }
            player.setPreferredAudioDevice(deviceInfo)
            preferredDeviceId = deviceId
        }
    }
//

    lateinit var sleepTimer: SleepTimer

    @Inject
    @PlayerCache
    lateinit var playerCache: SimpleCache

    @Inject
    @DownloadCache
    lateinit var downloadCache: SimpleCache

    lateinit var player: ExoPlayer
        private set
    private var secondaryPlayer: ExoPlayer? = null
    private var fadingPlayer: ExoPlayer? = null
    private var isCrossfading = false
    private var crossfadeJob: Job? = null
    private var duckVolumeRestoreJob: Job? = null

    private lateinit var mediaSession: MediaLibrarySession

    // Tracks if player has been properly initilized
    private val playerInitialized = MutableStateFlow(false)
    val isPlayerReady: kotlinx.coroutines.flow.StateFlow<Boolean> = playerInitialized.asStateFlow()

    // Expose active player flow for UI/Connection updates
    private val _playerFlow = MutableStateFlow<ExoPlayer?>(null)
    val playerFlow = _playerFlow.asStateFlow()

    private val playerSilenceProcessors = HashMap<Player, SilenceDetectorAudioProcessor>()
    private val playerStallWatchdogs = HashMap<Player, LosslessStallWatchdogAudioProcessor>()



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

    // Tracks the original queue size to distinguish original items from auto-added ones
    private var originalQueueSize: Int = 0

    private var consecutivePlaybackErr = 0
    private var retryJob: Job? = null
    private var retryCount = 0
    private var silenceSkipJob: Job? = null

    // URL cache for stream URLs - class-level so it can be invalidated on errors
    private val songUrlCache = HashMap<String, Pair<String, Long>>()

    // Flag to bypass cache when quality changes - forces fresh stream fetch
    private val bypassCacheForQualityChange = mutableSetOf<String>()

    // Enhanced error tracking for strict retry management
    private var currentMediaIdRetryCount = mutableMapOf<String, Int>()
    private val MAX_RETRY_PER_SONG = 3
    private val RETRY_DELAY_MS = 1000L

    // Tracks which mediaIds resolved to a Tidal/Spine lossless stream, so the
    // stall watchdog only acts on lossless playback (the dropout bug is
    // lossless-only â€” confirmed, never happens on plain YouTube audio).
    private val losslessStreamMediaIds = mutableSetOf<String>()

    // Set once a track has stalled/parsing-errored too many times on lossless â€”
    // the next resolve for this mediaId skips Spine/Tidal and goes straight to
    // the plain YouTube stream (see forceStandardAudio in YTPlayerUtils).
    private val forceStandardAudioMediaIds = mutableSetOf<String>()

    // Separate from currentMediaIdRetryCount (real PlaybackExceptions): counts
    // silent-stall recoveries, which never throw an exception.
    private val losslessStallCount = mutableMapOf<String, Int>()
    private val MAX_LOSSLESS_STALLS_BEFORE_FALLBACK = 2


    // All DJ analysis, pre-analysis and transition decisions live here; this
    // service only carries out what it decides. Owns its own coroutine scope,
    // so it survives `scope` being replaced on service restart.
    private val djEngine: DjEngine by lazy {
        DjEngine(database) { createExoPlayer(publishAsActive = false) }
    }

    /** BPM / key / tier readout for the UI, owned by the engine. */
    val djState get() = djEngine.state

    // Track failed songs to prevent infinite retry loops
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
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true

        // media3's own interception point for the platform refusing a foreground
        // promotion (SDK 31+, and stricter again on 16). Without a listener set,
        // media3 lets ForegroundServiceStartNotAllowedException propagate out of
        // its posted Handler callback and the process dies — which is the
        // SM-A566B/Android 16 crash. CrashHandler catches it as a last resort,
        // but that is a global uncaught-exception net; this is the supported hook
        // and it keeps the service alive. Nothing to recover here: the OS refused
        // the promotion, so playback simply stays unpromoted until it next starts
        // from the foreground.
        // Qualified: this class also implements Player.Listener, so a bare `Listener`
        // resolves to that inherited one instead of the service's.
        setListener(object : MediaSessionService.Listener {
            override fun onForegroundServiceStartNotAllowedException() {
                Timber.tag(TAG).w("Foreground promotion refused by platform — staying unpromoted")
            }
        })

        // Player rediness reset to false
        playerInitialized.value = false

        // 3. Connect the processor to the service
        // handled in createExoPlayer

        try {
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.music_player),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
            val pending = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.music_player))
                .setContentText("")
                .setSmallIcon(R.drawable.convxy_notification)
                .setContentIntent(pending)
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

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { NOTIFICATION_ID },
                CHANNEL_ID,
                R.string.music_player
            )
                .apply {
                    setSmallIcon(R.drawable.convxy_notification)
                },
        )
        player = createExoPlayer()
        player.addListener(this@MusicService)
        sleepTimer = SleepTimer(scope, player)
        player.addListener(sleepTimer)

        // Mark player as initialized after successful creation
        playerInitialized.value = true
        Timber.tag(TAG).d("Player successfully initialized")

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setupAudioFocusRequest()

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

        // Restore shuffle mode if remember option is enabled
        if (dataStore.get(RememberShuffleAndRepeatKey, true)) {
            player.shuffleModeEnabled = dataStore.get(ShuffleModeKey, false)
        }

        // Keep a connected controller so that notification works
        val sessionToken = SessionToken(this, ComponentName(this, MusicService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({ controllerFuture.get() }, MoreExecutors.directExecutor())

        connectivityManager = getSystemService()!!
        connectivityObserver = NetworkConnectivityObserver(this)

        val screenStateFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, screenStateFilter)

        audioManager.registerAudioDeviceCallback(audioDeviceCallback, null)

        audioQuality = dataStore.get(AudioQualityKey).toEnum(com.convxy.music.constants.AudioQuality.AUTO)
        ipVersion = dataStore.get(IpVersionKey).toEnum(IpVersion.AUTO)
        playerVolume = MutableStateFlow(dataStore.get(PlayerVolumeKey, 1f).coerceIn(0f, 1f))

        // Initialize Google Cast
        initializeCast()

        // 4. Watch for EQ profile changes
        scope.launch {
            eqProfileRepository.activeProfile.collect { profile ->
                if (profile != null) {
                    val result = equalizerService.applyProfile(profile)
                    if (result.isSuccess && player.playbackState == Player.STATE_READY && player.isPlaying) {
                        // Instant update: flush buffers and seek slightly to re-process audio
                        // Small seek to force re-buffer through the new EQ settings
                        // Seek to current position effectively resets the pipeline
                        player.seekTo(player.currentPosition)
                    }
                } else {
                    equalizerService.disable()
                    if (player.playbackState == Player.STATE_READY && player.isPlaying) {
                        player.seekTo(player.currentPosition)
                    }
                }
            }
        }

        scope.launch {
            connectivityObserver.networkStatus.collect { isConnected ->
                isNetworkConnected.value = isConnected
                if (isConnected && waitingForNetworkConnection.value) {
                    triggerRetry()
                }
                // Update Discord RPC when network becomes available
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

        // Watch for audio quality setting changes
        var isFirstQualityEmit = true
        scope.launch {
            dataStore.data
                .map { prefs ->
                    if (prefs[DataSaverEnabledKey] ?: false) {
                        com.convxy.music.constants.AudioQuality.LOW
                    } else {
                        prefs[AudioQualityKey]?.let { value ->
                            com.convxy.music.constants.AudioQuality.entries.find { it.name == value }
                        } ?: com.convxy.music.constants.AudioQuality.AUTO
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

                    // Reload current song with new quality
                    val mediaId = player.currentMediaItem?.mediaId ?: return@collect
                    val currentPosition = player.currentPosition
                    val wasPlaying = player.isPlaying
                    val currentIndex = player.currentMediaItemIndex

                    Timber.tag("MusicService").i("RELOADING STREAM: $mediaId at position ${currentPosition}ms")

                    // Clear cached URL to force fresh fetch
                    songUrlCache.remove(mediaId)

                    // CRITICAL: Clear caches synchronously to prevent format parsing errors
                    runBlocking(Dispatchers.IO) {
                        try {
                            playerCache.removeResource(mediaId)
                            downloadCache.removeResource(mediaId)
                            Timber.tag("MusicService").d("Cleared player and download cache for $mediaId")
                        } catch (e: Exception) {
                            Timber.tag("MusicService").e(e, "Failed to clear cache for $mediaId")
                        }
                    }

                    // Set bypass flag so resolver skips cache checks
                    bypassCacheForQualityChange.add(mediaId)
                    Timber.tag("MusicService").d("Set bypass cache flag for $mediaId")

                    // Reload player at same position
                    player.stop()
                    player.seekTo(currentIndex, currentPosition)
                    player.prepare()
                    if (wasPlaying) {
                        player.play()
                    }
                }
        }

        // Watch for full-video mode changes: reload the current song so the
        // muxed (or plain audio) stream is resolved under its own cache key.
        var isFirstVideoModeEmit = true
        scope.launch {
            dataStore.data
                .map { prefs -> prefs[WatchVideoKey] ?: false }
                .distinctUntilChanged()
                .collect { enabled ->
                    if (isFirstVideoModeEmit) {
                        isFirstVideoModeEmit = false
                        return@collect
                    }
                    val mediaId = player.currentMediaItem?.mediaId ?: return@collect
                    val currentPosition = player.currentPosition
                    val wasPlaying = player.isPlaying
                    val currentIndex = player.currentMediaItemIndex

                    Timber.tag("MusicService").i("VIDEO MODE CHANGED: $enabled — reloading $mediaId at ${currentPosition}ms")
                    songUrlCache.remove(mediaId)
                    songUrlCache.remove("$mediaId#video")
                    runBlocking(Dispatchers.IO) {
                        try {
                            playerCache.removeResource(mediaId)
                            playerCache.removeResource("$mediaId#video")
                        } catch (e: Exception) {
                            Timber.tag("MusicService").e(e, "Failed to clear cache for video mode change")
                        }
                    }
                    bypassCacheForQualityChange.add(mediaId)
                    player.stop()
                    player.seekTo(currentIndex, currentPosition)
                    player.prepare()
                    if (wasPlaying) {
                        player.play()
                    }
                }
        }

        // Watch for IP version changes
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

                    // Clear cached URL
                    songUrlCache.remove(mediaId)

                    // Reload player
                    player.stop()
                    player.seekTo(currentIndex, currentPosition)
                    player.prepare()
                    if (wasPlaying) {
                        player.play()
                    }
                }
        }

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
            updateNotification()
            updateWidgetUI(player.isPlaying)
        }

        combine(
            currentMediaMetadata.distinctUntilChangedBy { it?.id },
            dataStore.data.map { (it[ShowLyricsKey] ?: false) && (it[DataSaverEnabledKey] != true) }.distinctUntilChanged(),
        ) { mediaMetadata, showLyrics ->
            mediaMetadata to showLyrics
        }.collectLatest(scope) { (mediaMetadata, showLyrics) ->
            if (showLyrics && mediaMetadata != null && database.lyrics(mediaMetadata.id)
                    .first() == null
            ) {
                val lyricsWithProvider = lyricsHelper.getLyrics(mediaMetadata)
                database.query {
                    upsert(
                        LyricsEntity(
                            id = mediaMetadata.id,
                            lyrics = lyricsWithProvider.lyrics,
                            provider = lyricsWithProvider.provider,
                        ),
                    )
                }
            }
        }

        dataStore.data
            .map { (it[SkipSilenceKey] ?: false) to (it[SkipSilenceInstantKey] ?: false) }
            .distinctUntilChanged()
            .collectLatest(scope) { (skipSilence, instantSkip) ->
                cachedSkipSilence = skipSilence
                cachedSkipSilenceInstant = instantSkip

                player.skipSilenceEnabled = skipSilence
                secondaryPlayer?.skipSilenceEnabled = skipSilence

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

        combine(
            currentFormat,
            dataStore.data
                .map { it[AudioNormalizationKey] ?: true }
                .distinctUntilChanged(),
        ) { format, normalizeAudio ->
            format to normalizeAudio
        }.collectLatest(scope) { (format, normalizeAudio) -> setupLoudnessEnhancer()}

        combine(
            dataStore.data.map { it[AudioOffload] ?: false },
            dataStore.data.map { it[CrossfadeEnabledKey] ?: false }
        ) { offloadPref, crossfadeEnabled ->
             // Force disable offload if crossfade is enabled to prevent volume ramp issues
             if (crossfadeEnabled) false else offloadPref
        }.distinctUntilChanged()
        .collectLatest(scope) { useOffload ->
             cachedOffloadEnabled = useOffload
             player.setOffloadEnabled(useOffload)
             secondaryPlayer?.setOffloadEnabled(useOffload)
        }

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

        // Watch all Discord customization preferences
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

        combine(
            dataStore.data.map { prefs ->
                Triple(
                    prefs[CrossfadeEnabledKey] ?: false,
                    prefs[CrossfadeDurationKey] ?: 5f,
                    prefs[CrossfadeGaplessKey] ?: true
                )
            },
            listenTogetherManager.roomState
        ) { (enabled, duration, gapless), roomState ->
            // Disable crossfade if user is in a listen together room
            Triple(enabled && roomState == null, duration, gapless)
        }
            .distinctUntilChanged()
            .collect(scope) { (enabled, duration, gapless) ->
                crossfadeEnabled = enabled
                crossfadeDuration = duration * 1000f // Convert to ms
                crossfadeGapless = gapless
            }

        dataStore.data
            .map { it[AutoDjMixingEnabledKey] ?: false }
            .distinctUntilChanged()
            .collect(scope) {
                djEngine.enabled = it
                // The readout hides itself when DJ mixing is off, so the flow
                // has to be republished on the toggle, not only on a transition.
                djEngine.refreshState(player.currentMediaItem?.mediaId)
            }

        dataStore.data
            .map { it[CreativeTransitionsEnabledKey] ?: false }
            .distinctUntilChanged()
            .collect(scope) { djEngine.creativeEnabled = it }

        if (dataStore.get(PersistentQueueKey, true)) {
            val queueFile = filesDir.resolve(PERSISTENT_QUEUE_FILE)
            if (queueFile.exists()) {
                runCatching {
                    queueFile.inputStream().use { fis ->
                        ObjectInputStream(fis).use { oos ->
                            oos.readObject() as PersistQueue
                        }
                    }
                }.onSuccess { queue ->
                    runCatching {
                        // Convert back to proper queue type
                        val restoredQueue = queue.toQueue()
                        // Wait for player initialization before playing
                        scope.launch {
                            playerInitialized.first { it }
                            if (isActive) {
                                playQueue(
                                    queue = restoredQueue,
                                    playWhenReady = false,
                                )
                            }
                        }
                    }.onFailure { error ->
                        Timber.tag(TAG).w(error, "Failed to restore persisted queue, clearing data")
                        clearPersistedQueueFiles()
                    }
                }.onFailure { error ->
                    Timber.tag(TAG).w(error, "Failed to read persisted queue, clearing data")
                    clearPersistedQueueFiles()
                }
            }

            val automixFile = filesDir.resolve(PERSISTENT_AUTOMIX_FILE)
            if (automixFile.exists()) {
                runCatching {
                    automixFile.inputStream().use { fis ->
                        ObjectInputStream(fis).use { oos ->
                            oos.readObject() as PersistQueue
                        }
                    }
                }.onSuccess { queue ->
                    runCatching {
                        automixItems.value = queue.items.map { it.toMediaItem() }
                    }.onFailure { error ->
                        Timber.tag(TAG).w(error, "Failed to restore automix queue, clearing data")
                        clearPersistedQueueFiles()
                    }
                }.onFailure { error ->
                    Timber.tag(TAG).w(error, "Failed to read automix queue, clearing data")
                    clearPersistedQueueFiles()
                }
            }

            // Restore player state
            val playerStateFile = filesDir.resolve(PERSISTENT_PLAYER_STATE_FILE)
            if (playerStateFile.exists()) {
                runCatching {
                    playerStateFile.inputStream().use { fis ->
                        ObjectInputStream(fis).use { oos ->
                            oos.readObject() as PersistPlayerState
                        }
                    }
                }.onSuccess { playerState ->
                    // Restore player settings after queue is loaded
                    scope.launch {
                        delay(1000) // Wait for queue to be loaded
                        // Don't restore repeat/shuffle from playerState as they are already set from DataStore (source of truth)
                        // player.repeatMode = playerState.repeatMode
                        // player.shuffleModeEnabled = playerState.shuffleModeEnabled
                        // A snapshot written while muted, audio-ducked, or
                        // mid-crossfade holds a zero (or near-zero) *effective*
                        // volume. Restoring it overrides the real PlayerVolumeKey
                        // value and boots playback silent until the slider moves.
                        if (playerState.volume > 0f) {
                            playerVolume.value = playerState.volume
                        }

                        // Restore position if it's still valid
                        if (playerState.currentMediaItemIndex < player.mediaItemCount) {
                            player.seekTo(playerState.currentMediaItemIndex, playerState.currentPosition)
                        }
                    }
                }.onFailure { error ->
                    Timber.tag(TAG).w(error, "Failed to read player state, clearing data")
                    clearPersistedQueueFiles()
                }
            }
        }

        // Save queue periodically to prevent queue loss from crash or force kill
        scope.launch {
            while (isActive) {
                delay(30.seconds)
                if (dataStore.get(PersistentQueueKey, true)) {
                    saveQueueToDisk()
                }
            }
        }

        // Save queue more frequently when playing to ensure state is preserved
        scope.launch {
            while (isActive) {
                delay(10.seconds)
                if (dataStore.get(PersistentQueueKey, true) && player.isPlaying) {
                    saveQueueToDisk()
                }
            }
        }
    }

    private fun createExoPlayer(publishAsActive: Boolean = true): ExoPlayer {
        val eqProcessor = CustomEqualizerAudioProcessor()
        equalizerService.addAudioProcessor(eqProcessor)

        val silenceProcessor = SilenceDetectorAudioProcessor(
            minSilenceDurationUs = 3_000_000L, // 3s instead of 2s
            silenceThreshold = 128,           // 128 instead of 256
            onLongSilence = { handleLongSilenceDetected() }
        )

        val stallWatchdog = LosslessStallWatchdogAudioProcessor(
            onStallDetected = { handleLosslessStallDetected() }
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

        // Set initial state from the cached mirrors (kept fresh by the
        // dataStore.data collectors in onCreate) rather than blocking here â€”
        // this runs on the crossfade path too, where a blocking DataStore read
        // would stall audio right as the next track needs to start.
        silenceProcessor.instantModeEnabled = cachedSkipSilence && cachedSkipSilenceInstant

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(createMediaSourceFactory())
            .setRenderersFactory(
                createRenderersFactory(
                    eqProcessor, silenceProcessor, stallWatchdog, bpmAnalyzer, djFilter, djTail, djDelay,
                )
            )
            .setLoadControl(
                androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        50_000,   // Min buffer: 50s
                        50_000,   // Max buffer: 50s
                        750,      // Buffer for playback: 750ms â€” start audio as soon as we have it
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

        playerSilenceProcessors[player] = silenceProcessor
        playerStallWatchdogs[player] = stallWatchdog
        djEngine.registerPlayer(player, bpmAnalyzer, djFilter, djTail, djDelay)

        player.apply {
                setOffloadEnabled(cachedOffloadEnabled)
                skipSilenceEnabled = cachedSkipSilence
                addAnalyticsListener(PlaybackStatsListener(false, this@MusicService))

                // Cleanup handled manually in onDestroy/release
            }
        if (publishAsActive) _playerFlow.value = player
        return player
    }

    private fun setupAudioFocusRequest() {
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

    private fun handleAudioFocusChange(focusChange: Int) {
        when (focusChange) {

            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                hasAudioFocus = true
                duckVolumeRestoreJob?.cancel()
                duckVolumeRestoreJob = null

                if (wasPlayingBeforeAudioFocusLoss && !player.isPlaying && !reentrantFocusGain) {
                    reentrantFocusGain = true
                    scope.launch {
                        delay(300)
                        if (hasAudioFocus && wasPlayingBeforeAudioFocusLoss && !player.isPlaying) {
                            // Don't start local playback if casting
                            if (castConnectionHandler?.isCasting?.value != true) {
                                player.play()
                            }
                            wasPlayingBeforeAudioFocusLoss = false
                        }
                        reentrantFocusGain = false
                    }
                }

                player.volume = if (isMuted.value) 0f else playerVolume.value
                lastAudioFocusState = focusChange
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                hasAudioFocus = false
                wasPlayingBeforeAudioFocusLoss = player.isPlaying
                if (player.isPlaying) {
                    player.pause()
                }
                abandonAudioFocus()
                lastAudioFocusState = focusChange
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                hasAudioFocus = false
                wasPlayingBeforeAudioFocusLoss = player.isPlaying
                if (player.isPlaying) {
                    player.pause()
                }
                lastAudioFocusState = focusChange
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                hasAudioFocus = false
                wasPlayingBeforeAudioFocusLoss = player.isPlaying
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
                lastAudioFocusState = focusChange
            }

            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                hasAudioFocus = true
                duckVolumeRestoreJob?.cancel()
                duckVolumeRestoreJob = null
                player.volume = if (isMuted.value) 0f else playerVolume.value
                lastAudioFocusState = focusChange
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true

        audioFocusRequest?.let { request ->
            val result = audioManager.requestAudioFocus(request)
            hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            return hasAudioFocus
        }
        return false
    }

    private fun abandonAudioFocus() {
        if (hasAudioFocus) {
            audioFocusRequest?.let { request ->
                audioManager.abandonAudioFocusRequest(request)
                hasAudioFocus = false
            }
        }
    }

    private fun clearPersistedQueueFiles() {
        runCatching { filesDir.resolve(PERSISTENT_QUEUE_FILE).delete() }
        runCatching { filesDir.resolve(PERSISTENT_AUTOMIX_FILE).delete() }
        runCatching { filesDir.resolve(PERSISTENT_PLAYER_STATE_FILE).delete() }
    }

    fun hasAudioFocusForPlayback(): Boolean {
        return hasAudioFocus
    }

    private fun waitOnNetworkError() {
        // Check if we've exceeded max retry attempts
        if (retryCount >= MAX_RETRY_COUNT) {
            Timber.tag(TAG).w("Max retry count ($MAX_RETRY_COUNT) reached, stopping playback")
            stopOnError()
            retryCount = 0
            return
        }

        waitingForNetworkConnection.value = true

        // Re-arm instead of early-returning when a retry is already pending: every
        // recovery handler below shares `retryJob` and cancels it, so a racing
        // error can leave `waitingForNetworkConnection` true with no live job.
        // Every later network error would then bail out here and playback would
        // stay dead until the process is restarted.
        if (retryJob?.isActive == true) return

        // Start a retry timer with exponential backoff
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
                // Still offline: keep waiting instead of silently dropping the
                // retry (that was the "mostly fixed after a reboot" stall).
                waitOnNetworkError()
            }
        }
    }

    private fun triggerRetry() {
        waitingForNetworkConnection.value = false
        retryJob?.cancel()

        if (player.currentMediaItem != null) {
            // After 3+ failed retries, try to refresh the stream URL by seeking to current position
            // This forces ExoPlayer to re-resolve the data source and get a fresh URL
            if (retryCount > 3) {
                Timber.tag(TAG).d("Retry count > 3, attempting to refresh stream URL")
                val currentPosition = player.currentPosition
                player.seekTo(player.currentMediaItemIndex, currentPosition)
            }
            player.prepare()
            // Don't call play() here - let the player auto-resume via playWhenReady
            // This avoids stealing audio focus during retry attempts
        }
    }

    private fun skipOnError() {
        /**
         * Auto skip to the next media item on error.
         *
         * To prevent a "runaway diesel engine" scenario, force the user to take action after
         * too many errors come up too quickly. Pause to show player "stopped" state
         */
        consecutivePlaybackErr += 2
        val nextWindowIndex = player.nextMediaItemIndex

        if (consecutivePlaybackErr <= MAX_CONSECUTIVE_ERR && nextWindowIndex != C.INDEX_UNSET) {
            player.seekTo(nextWindowIndex, C.TIME_UNSET)
            player.prepare()
            // Don't start local playback if casting
            if (castConnectionHandler?.isCasting?.value != true) {
                player.play()
            }
            return
        }

        player.pause()
        consecutivePlaybackErr = 0
    }

    private fun stopOnError() {
        player.pause()
        // Drop the foreground service so Media3 dismisses its notification
        // instead of leaving a stale "paused" one that reappears forever after
        // a swipe-away. Playback is dead here and has to be restarted anyway;
        // Media3 re-foregrounds automatically the next time audio starts.
        stopSelf()
        currentMediaMetadata.value = null
        updateWidgetUI(false)
    }

    private fun updateNotification() {
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

    private suspend fun recoverSong(
        mediaId: String,
        playbackData: YTPlayerUtils.PlaybackData? = null
    ) {
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
                // Update isVideo flag if it's different from the current value
                if (song.song.isVideo != mediaMetadata.isVideoSong) {
                    updatedSong = updatedSong.copy(isVideo = mediaMetadata.isVideoSong)
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
    }

    fun playQueue(
        queue: Queue,
        playWhenReady: Boolean = true,
    ) {
        if (!scope.isActive) scope = CoroutineScope(Dispatchers.Main) + SupervisorJob()

        // Safety Check : Ensuring player is initilized
        if (!playerInitialized.value) {
            Timber.tag(TAG).w("playQueue called before player initialization, queuing request")
            scope.launch {
                playerInitialized.first { it }
                playQueue(queue, playWhenReady)
            }
            return
        }

        currentQueue = queue
        queueTitle = null
        val persistShuffleAcrossQueues = dataStore.get(PersistentShuffleAcrossQueuesKey, false)
        val previousShuffleEnabled = player.shuffleModeEnabled
        if (!persistShuffleAcrossQueues) {
            player.shuffleModeEnabled = false
        }
        // Reset original queue size when starting a new queue
        originalQueueSize = 0
        if (queue.preloadItem != null) {
            player.setMediaItem(queue.preloadItem!!.toMediaItem())
            player.prepare()
            player.playWhenReady = playWhenReady
        }
        scope.launch(SilentHandler) {
            val initialStatus =
                withContext(Dispatchers.IO) {
                    queue.getInitialStatus()
                        .filterExplicit(dataStore.get(HideExplicitKey, false))
                        .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))
                }
            if (queue.preloadItem != null && player.playbackState == STATE_IDLE) return@launch
            if (initialStatus.title != null) {
                queueTitle = initialStatus.title
            }
            if (initialStatus.items.isEmpty()) return@launch
            // Track original queue size for shuffle playlist first feature
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
                    if (initialStatus.mediaItemIndex >
                        0
                    ) {
                        initialStatus.mediaItemIndex
                    } else {
                        0
                    },
                    initialStatus.position,
                )
                player.prepare()
                player.playWhenReady = playWhenReady
            }

            // Rebuild shuffle order if shuffle is enabled
            if (player.shuffleModeEnabled) {
                val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
                applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
            }
        }
    }

    fun startRadioSeamlessly() {
        // Safety Check: Ensure Player is initilized
        if (!playerInitialized.value) {
            Timber.tag(TAG).w("startRadioSeamlessly called before player initialization")
            return
        }

        val currentMediaMetadata = player.currentMetadata ?: return

        val currentIndex = player.currentMediaItemIndex
        val currentMediaId = currentMediaMetadata.id

        scope.launch(SilentHandler) {
            // Use simple videoId to let YouTube personalize recommendations
            val radioQueue = YouTubeQueue(
                endpoint = WatchEndpoint(
                    videoId = currentMediaId
                )
            )

            try {
                val initialStatus = withContext(Dispatchers.IO) {
                    radioQueue.getInitialStatus()
                        .filterExplicit(dataStore.get(HideExplicitKey, false))
                        .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))
                }

                if (initialStatus.title != null) {
                    queueTitle = initialStatus.title
                }

                // Filter radio items to exclude current media item
                val radioItems = initialStatus.items.filter { item ->
                    item.mediaId != currentMediaId
                }

                if (radioItems.isNotEmpty()) {
                    val itemCount = player.mediaItemCount

                    if (itemCount > currentIndex + 1) {
                        player.removeMediaItems(currentIndex + 1, itemCount)
                    }

                    player.addMediaItems(currentIndex + 1, radioItems)
                    if (player.shuffleModeEnabled) {
                        val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
                        applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
                    }
                }

                currentQueue = radioQueue
            } catch (e: Exception) {
                // Fallback: try with related endpoint
                try {
                    val nextResult = withContext(Dispatchers.IO) {
                        YouTube.next(WatchEndpoint(videoId = currentMediaId)).getOrNull()
                    }
                    nextResult?.relatedEndpoint?.let { relatedEndpoint ->
                        val relatedPage = withContext(Dispatchers.IO) {
                            YouTube.related(relatedEndpoint).getOrNull()
                        }
                        relatedPage?.songs?.let { songs ->
                            val radioItems = songs
                                .filter { it.id != currentMediaId }
                                .map { it.toMediaItem() }
                                .filterExplicit(dataStore.get(HideExplicitKey, false))
                                .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))

                            if (radioItems.isNotEmpty()) {
                                val itemCount = player.mediaItemCount
                                if (itemCount > currentIndex + 1) {
                                    player.removeMediaItems(currentIndex + 1, itemCount)
                                }
                                player.addMediaItems(currentIndex + 1, radioItems)
                                if (player.shuffleModeEnabled) {
                                    val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
                                    applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Silent fail
                }
            }
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
        if (dataStore.get(SimilarContent, true) &&
            !(dataStore.get(DisableLoadMoreWhenRepeatAllKey, false) && player.repeatMode == REPEAT_MODE_ALL)) {
            scope.launch(SilentHandler) {
                try {
                    // Try primary method
                    YouTube.next(WatchEndpoint(playlistId = playlistId))
                        .onSuccess { firstResult ->
                            YouTube.next(WatchEndpoint(playlistId = firstResult.endpoint.playlistId))
                                .onSuccess { secondResult ->
                                    automixItems.value = secondResult.items.map { song ->
                                        song.toMediaItem()
                                    }
                                }
                                .onFailure {
                                    // Fallback: use first result items
                                    if (firstResult.items.isNotEmpty()) {
                                        automixItems.value = firstResult.items.map { song ->
                                            song.toMediaItem()
                                        }
                                    }
                                }
                        }
                        .onFailure {
                            // Fallback: try with radio format
                            val currentSong = player.currentMetadata
                            if (currentSong != null) {
                                // Use simple videoId for better personalized recommendations
                                YouTube.next(WatchEndpoint(
                                    videoId = currentSong.id
                                )).onSuccess { radioResult ->
                                    val filteredItems = radioResult.items
                                        .filter { it.id != currentSong.id }
                                        .map { it.toMediaItem() }
                                    if (filteredItems.isNotEmpty()) {
                                        automixItems.value = filteredItems
                                    }
                                }.onFailure {
                                    // Final fallback: try related endpoint
                                    YouTube.next(WatchEndpoint(videoId = currentSong.id)).getOrNull()?.relatedEndpoint?.let { relatedEndpoint ->
                                        YouTube.related(relatedEndpoint).onSuccess { relatedPage ->
                                            val relatedItems = relatedPage.songs
                                                .filter { it.id != currentSong.id }
                                                .map { it.toMediaItem() }
                                            if (relatedItems.isNotEmpty()) {
                                                automixItems.value = relatedItems

                                            }
                                        }
                                    }
                                }
                            }
                        }
                } catch (_: Exception) {
                    // Silent fail
                }
            }
        }
    }

    fun addToQueueAutomix(
        item: MediaItem,
        position: Int,
    ) {
        automixItems.value =
            automixItems.value.toMutableList().apply {
                removeAt(position)
            }
        addToQueue(listOf(item))
    }

    fun playNextAutomix(
        item: MediaItem,
        position: Int,
    ) {
        automixItems.value =
            automixItems.value.toMutableList().apply {
                removeAt(position)
            }
        playNext(listOf(item))
    }

    fun clearAutomix() {
        automixItems.value = emptyList()
    }

    fun playNext(items: List<MediaItem>) {
        // If queue is empty or player is idle, play immediately instead
        if (player.mediaItemCount == 0 || player.playbackState == STATE_IDLE) {
            player.setMediaItems(items)
            player.prepare()
            // Don't start local playback if casting
            if (castConnectionHandler?.isCasting?.value != true) {
                player.play()
            }
            return
        }

        // Remove duplicates if enabled
        if (dataStore.get(PreventDuplicateTracksInQueueKey, false)) {
            val itemIds = items.map { it.mediaId }.toSet()
            val indicesToRemove = mutableListOf<Int>()
            val currentIndex = player.currentMediaItemIndex

            for (i in 0 until player.mediaItemCount) {
                if (i != currentIndex && player.getMediaItemAt(i).mediaId in itemIds) {
                    indicesToRemove.add(i)
                }
            }

            // Remove from highest index to lowest to maintain index stability
            indicesToRemove.sortedDescending().forEach { index ->
                player.removeMediaItem(index)
            }
        }

        val insertIndex = player.currentMediaItemIndex + 1
        val shuffleEnabled = player.shuffleModeEnabled

        // Insert items immediately after the current item in the window/index space
        player.addMediaItems(insertIndex, items)
        player.prepare()

        if (shuffleEnabled) {
            // Rebuild shuffle order so that newly inserted items are played next
            val timeline = player.currentTimeline
            if (!timeline.isEmpty) {
                val size = timeline.windowCount
                val currentIndex = player.currentMediaItemIndex

                // Newly inserted indices are a contiguous range [insertIndex, insertIndex + items.size)
                val newIndices = (insertIndex until (insertIndex + items.size)).toSet()

                // Collect existing shuffle traversal order excluding current index
                val orderAfter = mutableListOf<Int>()
                var idx = currentIndex
                while (true) {
                    idx = timeline.getNextWindowIndex(idx, Player.REPEAT_MODE_OFF, /*shuffleModeEnabled=*/true)
                    if (idx == C.INDEX_UNSET) break
                    if (idx != currentIndex) orderAfter.add(idx)
                }

                val prevList = mutableListOf<Int>()
                var pIdx = currentIndex
                while (true) {
                    pIdx = timeline.getPreviousWindowIndex(pIdx, Player.REPEAT_MODE_OFF, /*shuffleModeEnabled=*/true)
                    if (pIdx == C.INDEX_UNSET) break
                    if (pIdx != currentIndex) prevList.add(pIdx)
                }
                prevList.reverse() // preserve original forward order

                val existingOrder = (prevList + orderAfter).filter { it != currentIndex && it !in newIndices }

                // Build new shuffle order: current -> newly inserted (in insertion order) -> rest
                val nextBlock = (insertIndex until (insertIndex + items.size)).toList()
                val finalOrder = IntArray(size)
                // Tracks which window indices have already been placed, so the
                // safety-net fill below is an O(1) lookup per slot instead of an
                // O(n) IntArray.contains scan â€” O(n^2) on a large queue otherwise.
                val placed = BooleanArray(size)
                var pos = 0
                finalOrder[pos++] = currentIndex
                placed[currentIndex] = true
                nextBlock.forEach { if (it in 0 until size) { finalOrder[pos++] = it; placed[it] = true } }
                existingOrder.forEach { if (pos < size) { finalOrder[pos++] = it; placed[it] = true } }

                // Fill any missing indices (safety) to ensure a full permutation
                if (pos < size) {
                    for (i in 0 until size) {
                        if (!placed[i]) {
                            finalOrder[pos++] = i
                            if (pos == size) break
                        }
                    }
                }

                player.setShuffleOrder(DefaultShuffleOrder(finalOrder, System.currentTimeMillis()))
            }
        }
    }

    fun addToQueue(items: List<MediaItem>) {
        // Remove duplicates if enabled
        if (dataStore.get(PreventDuplicateTracksInQueueKey, false)) {
            val itemIds = items.map { it.mediaId }.toSet()
            val indicesToRemove = mutableListOf<Int>()
            val currentIndex = player.currentMediaItemIndex

            for (i in 0 until player.mediaItemCount) {
                if (i != currentIndex && player.getMediaItemAt(i).mediaId in itemIds) {
                    indicesToRemove.add(i)
                }
            }

            // Remove from highest index to lowest to maintain index stability
            indicesToRemove.sortedDescending().forEach { index ->
                player.removeMediaItem(index)
            }
        }

        player.addMediaItems(items)
        if (player.shuffleModeEnabled) {
            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
        }
        player.prepare()
    }

    fun toggleLibrary() {
        scope.launch {
            val songToToggle = currentSong.first()
            songToToggle?.let {
                val isInLibrary = it.song.inLibrary != null
                val token = if (isInLibrary) it.song.libraryRemoveToken else it.song.libraryAddToken

                // Call YouTube API with feedback token if available
                token?.let { feedbackToken ->
                    YouTube.feedback(listOf(feedbackToken))
                }

                // Update local database
                database.query {
                    update(it.song.toggleLibrary())
                }
                currentMediaMetadata.value = player.currentMetadata
            }
        }
    }

    fun toggleLike() {
        scope.launch {
            val songToToggle = currentSong.first()
            songToToggle?.let {
                val song = it.song.toggleLike()
                database.query {
                    update(song)
                    syncUtils.likeSong(song)
                    // Auto-download on like is handled by DownloadUtil, which watches
                    // the `liked` column — this hook only ever fired for the player's
                    // own like button, never for the song/queue/selection menus.
                }
                currentMediaMetadata.value = player.currentMetadata
            }
        }
    }

    fun toggleStartRadio() {
        startRadioSeamlessly()
    }

    private fun setupLoudnessEnhancer() {
        val audioSessionId = player.audioSessionId

        if (audioSessionId == C.AUDIO_SESSION_ID_UNSET || audioSessionId <= 0) {
            Timber.tag(TAG).w("setupLoudnessEnhancer: invalid audioSessionId ($audioSessionId), cannot create effect yet")
            return
        }

        // Create or recreate enhancer if needed
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

                    // Use loudnessDb if available, otherwise fall back to perceptualLoudnessDb
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

    private fun releaseLoudnessEnhancer() {
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

    private fun openAudioEffectSession() {
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

    private fun closeAudioEffectSession() {
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

    private var previousMediaItemIndex = C.INDEX_UNSET

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        Timber.tag(TAG).d(
            "SKIP_DEBUG onMediaItemTransition: reason=$reason (0=REPEAT,1=AUTO,2=SEEK,3=PLAYLIST_CHANGED) " +
                "newIndex=${player.currentMediaItemIndex} newId=${mediaItem?.mediaId} " +
                "prevTrackedIndex=$previousMediaItemIndex mediaItemCount=${player.mediaItemCount} " +
                "repeatMode=${player.repeatMode} shuffle=${player.shuffleModeEnabled}"
        )
        // Force Repeat One if the player ignored it and auto-advanced
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            val repeatMode = runBlocking { dataStore.get(RepeatModeKey, REPEAT_MODE_OFF) }
            if (repeatMode == REPEAT_MODE_ONE &&
                previousMediaItemIndex != C.INDEX_UNSET &&
                previousMediaItemIndex != player.currentMediaItemIndex) {

                Timber.tag(TAG).d("SKIP_DEBUG onMediaItemTransition: forcing repeat-one seekTo($previousMediaItemIndex)")
                player.seekTo(previousMediaItemIndex, 0)
            }
        }
        previousMediaItemIndex = player.currentMediaItemIndex

        playerStallWatchdogs[player]?.let { watchdog ->
            watchdog.armed = mediaItem?.mediaId?.let { losslessStreamMediaIds.contains(it) } == true
            watchdog.resetForNewTrack()
        }
        djEngine.onTrackChanged(player, mediaItem?.mediaId)

        lastPlaybackSpeed = -1.0f // force update song

        setupLoudnessEnhancer()

        discordUpdateJob?.cancel()

        scrobbleManager?.onSongStop()
        checkAndSubmitListenBrainzFinished()
        if (player.playWhenReady && player.playbackState == Player.STATE_READY) {
            scrobbleManager?.onSongStart(player.currentMetadata, duration = player.duration)
            player.currentMediaItem?.mediaId?.let { mediaId ->
                if (listenBrainzCurrentMediaId != mediaId) {
                    listenBrainzCurrentMediaId = mediaId
                    listenBrainzCurrentStartTs = System.currentTimeMillis()
                }
                checkAndSubmitListenBrainzPlayingNow(mediaId)
            }
        }

        // Sync Cast when media changes and Cast is connected
        // Skip if this change was triggered by Cast sync (to prevent loops)
        if (castConnectionHandler?.isCasting?.value == true &&
            castConnectionHandler?.isSyncingFromCast != true &&
            mediaItem != null) {
            val metadata = mediaItem.metadata
            if (metadata != null) {
                // Try to navigate to the item if it's already in Cast queue
                // This avoids a full reload which causes the widget to refresh
                val navigated = castConnectionHandler?.navigateToMediaIfInQueue(metadata.id) ?: false
                if (!navigated) {
                    // Item not in Cast queue, need to reload
                    castConnectionHandler?.loadMedia(metadata)
                }
            }
        }

        // Auto load more songs from queue
        if (dataStore.get(AutoLoadMoreKey, true) &&
            reason != Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT &&
            player.mediaItemCount - player.currentMediaItemIndex <= 5 &&
            currentQueue.hasNextPage() &&
            !(dataStore.get(DisableLoadMoreWhenRepeatAllKey, false) && player.repeatMode == REPEAT_MODE_ALL)
        ) {
            scope.launch(SilentHandler) {
                val mediaItems = withContext(Dispatchers.IO) {
                    currentQueue.nextPage()
                        .filterExplicit(dataStore.get(HideExplicitKey, false))
                        .filterVideoSongs(dataStore.get(HideVideoSongsKey, false) || dataStore.get(DataSaverEnabledKey, false))
                }
                if (player.playbackState != STATE_IDLE && mediaItems.isNotEmpty()) {
                    player.addMediaItems(mediaItems)
                    if (player.shuffleModeEnabled) {
                        val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
                        applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
                    }
                }
            }
        }

        // Save state when media item changes
        if (dataStore.get(PersistentQueueKey, true)) {
            saveQueueToDisk()
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

        // Force Repeat All if the player ignored it and ended playback
        if (playbackState == Player.STATE_ENDED) {
            val repeatMode = runBlocking { dataStore.get(RepeatModeKey, REPEAT_MODE_OFF) }
            if (repeatMode == REPEAT_MODE_ALL && player.mediaItemCount > 0) {
                player.seekTo(0, 0)
                player.prepare()
                player.play()
            }
        }

        // Save state when playback state changes (but not during silence skipping)
        if (dataStore.get(PersistentQueueKey, true) && !isSilenceSkipping) {
            saveQueueToDisk()
        }

        if (playbackState == Player.STATE_READY) {
            consecutivePlaybackErr = 0
            retryCount = 0
            waitingForNetworkConnection.value = false
            retryJob?.cancel()

            // Reset retry count for current song on successful playback
            player.currentMediaItem?.mediaId?.let { mediaId ->
                resetRetryCount(mediaId)
                Timber.tag(TAG).d("Playback successful for $mediaId, reset retry count")
            }
            scheduleCrossfade()
        }

        if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
            scrobbleManager?.onSongStop()
            checkAndSubmitListenBrainzFinished()
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        // Safety net: if local player tries to start while casting, immediately pause it
        if (playWhenReady && castConnectionHandler?.isCasting?.value == true) {
            player.pause()
            return
        }

        if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
            if (playWhenReady) {
                isPausedByVolumeMute = false
            }

            if (!playWhenReady && !isPausedByVolumeMute) {
                wasPlayingBeforeVolumeMute = false
            }
        }

        if (playWhenReady) {
            setupLoudnessEnhancer()
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
            scheduleCrossfade()
            val isBufferingOrReady =
                player.playbackState == Player.STATE_BUFFERING || player.playbackState == Player.STATE_READY
            if (isBufferingOrReady && player.playWhenReady) {
                val focusGranted = requestAudioFocus()
                if (focusGranted) {
                    openAudioEffectSession()
                }
            } else {
                closeAudioEffectSession()
            }
        }
        if (events.containsAny(EVENT_TIMELINE_CHANGED, EVENT_POSITION_DISCONTINUITY)) {
            currentMediaMetadata.value = player.currentMetadata
        }

        // Widget and Discord RPC updates
        if (events.containsAny(Player.EVENT_IS_PLAYING_CHANGED)) {
            updateWidgetUI(player.isPlaying)
            if (player.isPlaying) {
                startWidgetUpdates()
            } else {
                stopWidgetUpdates()
            }
            if (!player.isPlaying && !events.containsAny(Player.EVENT_POSITION_DISCONTINUITY, Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                scope.launch {
                    discordRpc?.close()
                }
            }
        }

        // Update Discord RPC when media item changes or playback starts
        if (events.containsAny(Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_IS_PLAYING_CHANGED) && player.isPlaying) {
            val mediaId = player.currentMetadata?.id
            if (mediaId != null) {
                scope.launch {
                    // Fetch song from database to get full info
                    database.song(mediaId).first()?.let { song ->
                        updateDiscordRPC(song)
                    }
                }
            }
        }

        // Scrobbling
        if (events.containsAny(Player.EVENT_IS_PLAYING_CHANGED)) {
            scrobbleManager?.onPlayerStateChanged(player.isPlaying, player.currentMetadata, duration = player.duration)

            if (player.isPlaying) {
                player.currentMediaItem?.mediaId?.let { mediaId ->
                    if (listenBrainzCurrentMediaId != mediaId) {
                        checkAndSubmitListenBrainzFinished()
                        listenBrainzCurrentMediaId = mediaId
                        listenBrainzCurrentStartTs = System.currentTimeMillis()
                    }
                    checkAndSubmitListenBrainzPlayingNow(mediaId)
                }
            }
        }

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        updateNotification()
        if (shuffleModeEnabled) {
            // If queue is empty, don't shuffle
            if (player.mediaItemCount == 0) return

            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            val currentIndex = player.currentMediaItemIndex
            val totalCount = player.mediaItemCount

            applyShuffleOrder(currentIndex, totalCount, shufflePlaylistFirst)
        }

        // Save shuffle mode to preferences
        if (dataStore.get(RememberShuffleAndRepeatKey, true)) {
            scope.launch {
                dataStore.edit { settings ->
                    settings[ShuffleModeKey] = shuffleModeEnabled
                }
            }
        }

        // Save state when shuffle mode changes
        if (dataStore.get(PersistentQueueKey, true)) {
            saveQueueToDisk()
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        updateNotification()
        scope.launch {
            dataStore.edit { settings ->
                settings[RepeatModeKey] = repeatMode
            }
        }

        // Save state when repeat mode changes
        if (dataStore.get(PersistentQueueKey, true)) {
            saveQueueToDisk()
        }
    }

    /**
     * Applies a new shuffle order to the player, maintaining the current item's position.
     * If `shufflePlaylistFirst` is true, it attempts to shuffle original items separately from added items.
     */
    private fun applyShuffleOrder(
        currentIndex: Int,
        totalCount: Int,
        shufflePlaylistFirst: Boolean
    ) {
        if (totalCount == 0) return

        if (shufflePlaylistFirst && originalQueueSize > 0 && originalQueueSize < totalCount) {
            // Shuffle original items and added items separately
            val originalIndices = (0 until originalQueueSize).filter { it != currentIndex }.toMutableList()
            val addedIndices = (originalQueueSize until totalCount).filter { it != currentIndex }.toMutableList()

            originalIndices.shuffle()
            addedIndices.shuffle()

            val shuffledIndices = IntArray(totalCount)
            var pos = 0
            shuffledIndices[pos++] = currentIndex

            if (currentIndex < originalQueueSize) {
                originalIndices.forEach { shuffledIndices[pos++] = it }
                addedIndices.forEach { shuffledIndices[pos++] = it }
            } else {
                (0 until originalQueueSize).shuffled().forEach { shuffledIndices[pos++] = it }
                addedIndices.forEach { shuffledIndices[pos++] = it }
            }
            player.setShuffleOrder(DefaultShuffleOrder(shuffledIndices, System.currentTimeMillis()))
        } else {
            val shuffledIndices = IntArray(totalCount) { it }
            shuffledIndices.shuffle()
            // Ensure current item is first in the shuffle order
            val currentItemIndexInShuffled = shuffledIndices.indexOf(currentIndex)
            if (currentItemIndexInShuffled != -1) { // Should always be true if totalCount > 0
                val temp = shuffledIndices[0]
                shuffledIndices[0] = shuffledIndices[currentItemIndexInShuffled]
                shuffledIndices[currentItemIndexInShuffled] = temp
            }
            player.setShuffleOrder(DefaultShuffleOrder(shuffledIndices, System.currentTimeMillis()))
        }
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        super.onPlaybackParametersChanged(playbackParameters)

        // Offload can't do speed/pitch: an offloaded AudioTrack either ignores
        // PlaybackParams or rejects them outright depending on the device, so a
        // tempo/transpose change lands on a sink that can't honour it. Same
        // defensive drop the crossfade path already makes, driven by the params.
        val defaultParameters = playbackParameters.speed == 1f && playbackParameters.pitch == 1f
        player.setOffloadEnabled(cachedOffloadEnabled && defaultParameters)

        if (playbackParameters.speed != lastPlaybackSpeed) {
            lastPlaybackSpeed = playbackParameters.speed
            // A pending crossfade was scheduled as a wall-clock delay derived from
            // the OLD speed — re-derive it. Skipped mid-transition, where the DJ
            // engine is driving the parameters itself.
            if (!isCrossfading) scheduleCrossfade()
            discordUpdateJob?.cancel()

            // update scheduling thingy
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

    /**
     * Extracts the HTTP response code from an error's cause chain.
     * Returns null if no HTTP response code is found.
     */
    private fun getHttpResponseCode(error: PlaybackException): Int? {
        var cause: Throwable? = error.cause
        while (cause != null) {
            if (cause is HttpDataSource.InvalidResponseCodeException) {
                return cause.responseCode
            }
            cause = cause.cause
        }
        return null
    }

    /**
     * Enriches the onPlayerError log so a client report of just "IO unspecified
     * (2000)" is diagnosable from logcat alone: HTTP status, network state,
     * retry count, media source, and the full cause chain.
     */
    private fun logPlayerErrorDetail(error: PlaybackException, mediaId: String?) {
        val httpCode = getHttpResponseCode(error)?.toString() ?: "-"
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

    /**
     * Checks if the error is caused by an expired/forbidden URL (HTTP 403).
     * This typically happens when a YouTube stream URL expires.
     */
    private fun isExpiredUrlError(error: PlaybackException): Boolean {
        val responseCode = getHttpResponseCode(error)
        return responseCode == 403
    }

    /**
     * Checks if the error is a Range Not Satisfiable error (HTTP 416).
     * This happens when cached data doesn't match the actual stream size.
     */
    private fun isRangeNotSatisfiableError(error: PlaybackException): Boolean {
        val responseCode = getHttpResponseCode(error)
        return responseCode == 416
    }

    /**
     * Checks if the error is a "page needs to be reloaded" error.
     * This is a YouTube-specific error that requires refreshing the stream.
     */
    private fun isPageReloadError(error: PlaybackException): Boolean {
        val errorMessage = error.message?.lowercase() ?: ""
        val causeMessage = error.cause?.message?.lowercase() ?: ""
        val innerCauseMessage = error.cause?.cause?.message?.lowercase() ?: ""

        val reloadKeywords = listOf(
            "page needs to be reloaded",
            "pagina deve essere ricaricata",
            "la pagina deve essere ricaricata",
            "page must be reloaded",
            "reload",
            "ricaricata"
        )

        return reloadKeywords.any { keyword ->
            errorMessage.contains(keyword) ||
            causeMessage.contains(keyword) ||
            innerCauseMessage.contains(keyword)
        }
    }

    private fun isNetworkRelatedError(error: PlaybackException): Boolean {
        // Don't treat specific errors as network errors - they need special handling
        if (isExpiredUrlError(error) || isRangeNotSatisfiableError(error) || isPageReloadError(error)) {
            return false
        }
        return error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE ||
                error.cause is java.net.ConnectException ||
                error.cause is java.net.UnknownHostException ||
                (error.cause as? PlaybackException)?.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
    }

    /**
     * Checks if the error is caused by AudioTrack write or initialization failures.
     * These errors indicate the audio renderer is in a corrupted/invalid state.
     */
    private fun isAudioRendererError(error: PlaybackException): Boolean {
        return error.errorCode == PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED ||
                error.errorCode == PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED ||
                (error.cause as? PlaybackException)?.errorCode == PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED ||
                (error.cause as? PlaybackException)?.errorCode == PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED
    }

    /**
     * Checks if the error is a parsing error (container or manifest unsupported).
     */
    private fun isParsingError(error: PlaybackException): Boolean {
        return error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED ||
                error.errorCode == PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED ||
                error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED
    }

    /**
     * Samsung's resource manager reclaims the FLAC hardware decoder mid-playback.
     */
    private fun isDecoderReclaimError(error: PlaybackException): Boolean {
        if (error.errorCode != PlaybackException.ERROR_CODE_DECODER_INIT_FAILED) return false
        val msg = error.cause?.message ?: return false
        return msg.contains("reclaim", ignoreCase = true)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

        // Safety check : ensuring player is still initialized
        if (!playerInitialized.value) {
            Timber.tag(TAG).e(error, "Player error occurred but player not initialized")
            return
        }

        val mediaId = player.currentMediaItem?.mediaId
        logPlayerErrorDetail(error, mediaId)
        Timber.tag(TAG).w(error, "Player error occurred for $mediaId: errorCode=${error.errorCode}, message=${error.message}")
        reportException(error)

        // Check if this song has failed too many times
        if (mediaId != null && hasExceededRetryLimit(mediaId)) {
            Timber.tag(TAG).w("Song $mediaId has exceeded retry limit, skipping")
            markSongAsFailed(mediaId)
            handleFinalFailure()
            return
        }

        // Aggressive cache clearing for all playback errors
        if (mediaId != null) {
            performAggressiveCacheClear(mediaId)
        }

        // Handle specific error types with strict strategies
        when {
            isDecoderReclaimError(error) -> {
                Timber.tag(TAG).d("FLAC decoder reclaim detected, retrying with fresh codec")
                handleDecoderReclaimError(mediaId)
                return
            }
            isAudioRendererError(error) -> {
                Timber.tag(TAG).d("AudioTrack error detected (${error.errorCode}), performing safe recovery")
                handleAudioRendererError(mediaId)
                return
            }
            isRangeNotSatisfiableError(error) -> {
                Timber.tag(TAG).d("Range Not Satisfiable (416) detected, performing strict recovery")
                handleRangeNotSatisfiableError(mediaId)
                return
            }
            isPageReloadError(error) -> {
                Timber.tag(TAG).d("Page reload error detected, performing strict recovery")
                handlePageReloadError(mediaId)
                return
            }
            isExpiredUrlError(error) -> {
                Timber.tag(TAG).d("Expired URL (403) detected, refreshing stream URL")
                handleExpiredUrlError(mediaId)
                return
            }
            isParsingError(error) -> {
                Timber.tag(TAG).d("Parsing error detected (${error.errorCode}), attempting recovery")
                handleParsingError(mediaId)
                return
            }

            !isNetworkConnected.value || isNetworkRelatedError(error) -> {
                Timber.tag(TAG).d("Network-related error detected, waiting for connection")
                waitOnNetworkError()
                return
            }
        }

        // For IO_UNSPECIFIED and IO_BAD_HTTP_STATUS, try recovery first
        if (error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
            Timber.tag(TAG).d("IO error detected (${error.errorCode}), attempting recovery")
            handleGenericIOError(mediaId)
            return
        }

        // Final fallback
        if (dataStore.get(AutoSkipNextOnErrorKey, false)) {
            Timber.tag(TAG).d("Auto-skipping to next track due to unrecoverable error")
            skipOnError()
        } else {
            Timber.tag(TAG).d("Stopping playback due to unrecoverable error")
            stopOnError()
        }
    }

    /**
     * Performs aggressive cache clearing for a media item.
     * Clears both player cache and download cache, plus URL cache.
     */
    private fun performAggressiveCacheClear(mediaId: String) {
        Timber.tag(TAG).d("Performing aggressive cache clear for $mediaId")

        // Clear URL cache. Lossless/Spine streams live under the "#flac"
        // namespace (see createDataSourceFactory's effKey), so clearing only the
        // plain id left a half-written FLAC body in place: every later resolve
        // read those bytes back and failed with the same
        // ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED, forever.
        songUrlCache.remove(mediaId)
        songUrlCache.remove("$mediaId#flac")

        // Clear player cache
        for (key in listOf(mediaId, "$mediaId#flac")) {
            try {
                playerCache.removeResource(key)
                Timber.tag(TAG).d("Cleared player cache for $key")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to clear player cache for $key")
            }
        }

        // Clear decryption caches
        try {
            YTPlayerUtils.forceRefreshForVideo(mediaId)
            Timber.tag(TAG).d("Cleared decryption caches for $mediaId")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to clear decryption caches for $mediaId")
        }
    }

    /**
     * Checks if a song has exceeded the retry limit.
     */
    private fun hasExceededRetryLimit(mediaId: String): Boolean {
        val currentRetries = currentMediaIdRetryCount[mediaId] ?: 0
        return currentRetries >= MAX_RETRY_PER_SONG
    }

    /**
     * Increments the retry count for a song.
     */
    private fun incrementRetryCount(mediaId: String) {
        val currentRetries = currentMediaIdRetryCount[mediaId] ?: 0
        currentMediaIdRetryCount[mediaId] = currentRetries + 1
        Timber.tag(TAG).d("Retry count for $mediaId: ${currentRetries + 1}/$MAX_RETRY_PER_SONG")
    }

    /**
     * Resets the retry count for a song (called on successful playback).
     */
    private fun resetRetryCount(mediaId: String) {
        currentMediaIdRetryCount.remove(mediaId)
        recentlyFailedSongs.remove(mediaId)
    }

    /**
     * Marks a song as failed to prevent further retry attempts.
     */
    private fun markSongAsFailed(mediaId: String) {
        recentlyFailedSongs.add(mediaId)
        currentMediaIdRetryCount.remove(mediaId)

        // Schedule cleanup of failed songs list after 5 minutes
        failedSongsClearJob?.cancel()
        failedSongsClearJob = scope.launch {
            delay(5 * 60 * 1000L) // 5 minutes
            recentlyFailedSongs.clear()
            Timber.tag(TAG).d("Cleared recently failed songs list")
        }
    }

    /**
     * Handles parsing errors by clearing caches and retrying.
     */
    private fun handleParsingError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        // A parsing-unsupported error on a lossless stream is a permanent format
        // issue, not a transient one â€” retrying the same Tidal/Spine URL will
        // never succeed. Skip straight to the standard-audio fallback instead
        // of looping retries against the same broken source.
        if (losslessStreamMediaIds.contains(mediaId)) {
            triggerLosslessFallback(mediaId)
            return
        }

        incrementRetryCount(mediaId)

        retryJob?.cancel()
        retryJob = scope.launch {
            // Clear all caches for this song
            performAggressiveCacheClear(mediaId)
            
            // Mark the song to bypass cache on next resolution
            bypassCacheForQualityChange.add(mediaId)

            delay(RETRY_DELAY_MS)

            // Re-prepare the player from start to ensure clean container parsing
            val currentIndex = player.currentMediaItemIndex
            player.seekTo(currentIndex, 0)
            player.prepare()

            Timber.tag(TAG).d("Retrying playback for $mediaId after parsing error (from position 0)")
        }
    }

    /**
     * Handles AudioTrack errors (write failed, init failed) with safe recovery.
     * These errors indicate the audio renderer is corrupted and needs careful reset.
     */
    private fun handleAudioRendererError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        incrementRetryCount(mediaId)

        retryJob?.cancel()
        retryJob = scope.launch {
            try {
                // Pause playback immediately to stop the renderer
                player.pause()
                Timber.tag(TAG).d("Paused playback due to AudioTrack error")

                // Wait longer for audio renderer to settle before retry
                // This prevents the renderer from continuing to fail in a loop
                delay(RETRY_DELAY_MS * 3) // 3 seconds instead of 1 second

                // Check if player is still initialized before attempting recovery
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

                    // Resume playback if it wasn't paused by user
                    if (wasPlayingBeforeAudioFocusLoss) {
                        delay(500) // Brief delay to allow renderer to be ready
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

    /**
     * Handles Samsung FLAC decoder reclaim: pause, wait for codec to free, re-prepare fresh.
     */
    private fun handleDecoderReclaimError(mediaId: String?) {
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

    /**
     * Handles Range Not Satisfiable (416) errors with strict recovery.
     * This error occurs when cached data doesn't match the actual stream size.
     */
    private fun handleRangeNotSatisfiableError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        incrementRetryCount(mediaId)

        retryJob?.cancel()
        retryJob = scope.launch {
            // Clear all caches aggressively
            performAggressiveCacheClear(mediaId)

            // Wait before retry
            delay(RETRY_DELAY_MS)

            // Force re-prepare from position 0 to avoid range issues
            val currentIndex = player.currentMediaItemIndex
            player.seekTo(currentIndex, 0)
            player.prepare()

            Timber.tag(TAG).d("Retrying playback for $mediaId after 416 error (from position 0)")
        }
    }

    /**
     * Handles "page needs to be reloaded" errors with strict recovery.
     * This requires clearing decryption caches and getting fresh stream URLs.
     */
    private fun handlePageReloadError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        incrementRetryCount(mediaId)

        retryJob?.cancel()
        retryJob = scope.launch {
            Timber.tag(TAG).d("Handling page reload error for $mediaId")

            // Clear all caches including decryption caches
            performAggressiveCacheClear(mediaId)

            // Short delay - the cipher JS is cached after the first attempt, 
            // so the second attempt resolves quickly.
            delay(RETRY_DELAY_MS)

            // Re-prepare the player
            val currentPosition = player.currentPosition
            val currentIndex = player.currentMediaItemIndex
            player.seekTo(currentIndex, currentPosition)
            player.prepare()

            Timber.tag(TAG).d("Retrying playback for $mediaId after page reload error")
        }
    }

    /**
     * Handles expired URL (403) errors by clearing caches and retrying.
     */
    private fun handleExpiredUrlError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        incrementRetryCount(mediaId)

        // Clear the cached URL
        songUrlCache.remove(mediaId)
        Timber.tag(TAG).d("Cleared cached URL for $mediaId")

        // Clear decryption caches
        try {
            YTPlayerUtils.forceRefreshForVideo(mediaId)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to clear decryption caches")
        }

        retryJob?.cancel()
        retryJob = scope.launch {
            delay(RETRY_DELAY_MS)

            // Seek to current position to force URL re-resolution
            val currentPosition = player.currentPosition
            val currentIndex = player.currentMediaItemIndex
            player.seekTo(currentIndex, currentPosition)
            player.prepare()

            Timber.tag(TAG).d("Retrying playback for $mediaId after 403 error")
        }
    }

    /**
     * Handles generic IO errors with recovery attempt.
     */
    private fun handleGenericIOError(mediaId: String?) {
        if (mediaId == null) {
            handleFinalFailure()
            return
        }

        incrementRetryCount(mediaId)

        retryJob?.cancel()
        retryJob = scope.launch {
            performAggressiveCacheClear(mediaId)
            delay(RETRY_DELAY_MS)

            val currentPosition = player.currentPosition
            val currentIndex = player.currentMediaItemIndex
            player.seekTo(currentIndex, currentPosition)
            player.prepare()

            Timber.tag(TAG).d("Retrying playback for $mediaId after generic IO error")
        }
    }

    /**
     * Handles final failure when all recovery attempts have been exhausted.
     */
    private fun handleFinalFailure() {
        if (dataStore.get(AutoSkipNextOnErrorKey, false)) {
            Timber.tag(TAG).d("All recovery attempts exhausted, auto-skipping to next track")
            skipOnError()
        } else {
            Timber.tag(TAG).d("All recovery attempts exhausted, stopping playback")
            stopOnError()
        }
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

    private fun createCacheDataSource(): CacheDataSource.Factory {
        val ytProxy = YouTube.proxy
        val ytProxyAuth = YouTube.proxyAuth
        return CacheDataSource
            .Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(
                CacheDataSource
                    .Factory()
                    .setCache(playerCache)
                    .setUpstreamDataSourceFactory(
                        DefaultDataSource.Factory(
                            this,
                            OkHttpDataSource.Factory(
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
                            ),
                        ),
                    ),
            ).setCacheWriteDataSinkFactory(null)
            .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)
    }

    // Flag to prevent queue saving during silence skip operations
    private var isSilenceSkipping = false

    private fun handleLongSilenceDetected() {
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

    private var stallRecoveryJob: Job? = null

    /** Lossless/Atmos stream stopped producing audio while position kept
     *  advancing â€” no PlaybackException, so onPlayerError never fires for this.
     *  Fired from the audio renderer thread; hop to [scope] (Main) before
     *  touching the player. */
    private fun handleLosslessStallDetected() {
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

    /** Gives up on lossless for this track: force the plain YouTube stream on
     *  the next resolve, disarm the watchdog, re-prepare from the current
     *  position, and let the user know. */
    private fun triggerLosslessFallback(mediaId: String) {
        Timber.tag(TAG).w("Falling back to standard audio for $mediaId after repeated lossless failures")

        forceStandardAudioMediaIds.add(mediaId)
        bypassCacheForQualityChange.add(mediaId)
        // bypassCacheForQualityChange only skips the resolver's own isCached
        // early-returns; the CacheDataSource underneath still serves whatever is
        // already stored for this key. Drop the lossless bytes outright or the
        // standard-audio retry parses the leftover FLAC body and fails again.
        performAggressiveCacheClear(mediaId)
        losslessStreamMediaIds.remove(mediaId)
        losslessStallCount.remove(mediaId)
        playerStallWatchdogs[player]?.armed = false

        val idx = player.currentMediaItemIndex
        val pos = player.currentPosition
        player.seekTo(idx, pos)
        player.prepare()

        Toast.makeText(
            this@MusicService,
            getString(R.string.lossless_fallback_to_standard_audio),
            Toast.LENGTH_SHORT
        ).show()
    }

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

    private fun createDataSourceFactory(): DataSource.Factory {
        return ResolvingDataSource.Factory(createCacheDataSource()) { dataSpec ->
            val mediaId = dataSpec.key ?: error("No media id")

            // Local files (content://, file://, /storage/) - skip YouTube resolution
            // DefaultDataSource in the chain handles local URIs natively
            if (mediaId.startsWith("content://") ||
                mediaId.startsWith("file://") ||
                mediaId.startsWith("/storage/")
            ) {
                return@Factory dataSpec
            }

            // Lossless namespaces the whole cache chain so FLAC and Opus/AAC bytes
            // for the same video never collide across a toggle. Streaming only:
            // offline downloads live in the plain (Opus) namespace.
            // Spine streams also use lossless namepacing to avoid cache collisions.
            // The YouTube watch screen also forces video for as long as it drives
            // playback (its session flag), so both the cache key and the resolver
            // below have to agree on the effective mode.
            val losslessOn = dataStore.get(EnableTidalStreamingKey, false)
            val spineEnabled = dataStore.get(EnabledModulesKey, "[]") != "[]"
            val videoMode = dataStore.get(WatchVideoKey, false) ||
                com.convxy.music.utils.YouTubePlaybackState.isActive()
            val effKey = when {
                videoMode -> "$mediaId#video"
                losslessOn || spineEnabled -> "$mediaId#flac"
                else -> mediaId
            }
            Timber.tag("SpineDebug").d("DataSourceResolver: mediaId=$mediaId spineEnabled=$spineEnabled losslessOn=$losslessOn effKey=$effKey")
            val spec = if (effKey == mediaId) dataSpec else dataSpec.buildUpon().setKey(effKey).build()

            // Check if we need to bypass cache for quality change
            val shouldBypassCache = bypassCacheForQualityChange.contains(mediaId)

            if (!shouldBypassCache) {
                // Downloads are always cached under the plain mediaId (see
                // DownloadRequest.setCustomCacheKey(song.id) at every download call
                // site) regardless of the lossless/Spine toggles, so they must be
                // looked up under mediaId â€” not effKey â€” or a downloaded song becomes
                // unplayable the moment either toggle is on, since the lookup misses
                // and falls through to a network re-fetch.
                if (downloadCache.isCached(
                        mediaId,
                        dataSpec.position,
                        if (dataSpec.length >= 0) dataSpec.length else 1
                    )
                ) {
                    scope.launch(Dispatchers.IO) { recoverSong(mediaId) }
                    return@Factory dataSpec
                }

                if (playerCache.isCached(effKey, dataSpec.position, CHUNK_LENGTH)) {
                    scope.launch(Dispatchers.IO) { recoverSong(mediaId) }
                    return@Factory spec
                }

                songUrlCache[effKey]?.takeIf { it.second > System.currentTimeMillis() }?.let {
                    scope.launch(Dispatchers.IO) { recoverSong(mediaId) }
                    return@Factory spec.withUri(it.first.toUri())
                }
            } else {
                Timber.tag("MusicService").i("BYPASSING CACHE for $mediaId due to quality change")
            }

            Timber.tag("MusicService").i("FETCHING STREAM: $mediaId | quality=$audioQuality")
            val fetchStart = SystemClock.elapsedRealtime()
            val playbackData = try {
                runBlocking(Dispatchers.IO) {
                    YTPlayerUtils.playerResponseForPlayback(
                        mediaId,
                        audioQuality = audioQuality,
                        connectivityManager = connectivityManager,
                        context = this@MusicService,
                        forceStandardAudio = forceStandardAudioMediaIds.contains(mediaId),
                        videoMode = videoMode,
                    )
                }.getOrElse { throwable ->
                    when (throwable) {
                        is PlaybackException -> throw throwable

                        is java.net.ConnectException, is java.net.UnknownHostException -> {
                            throw PlaybackException(
                                getString(R.string.error_no_internet),
                                throwable,
                                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
                            )
                        }

                        is java.net.SocketTimeoutException -> {
                            throw PlaybackException(
                                getString(R.string.error_timeout),
                                throwable,
                                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT
                            )
                        }

                        else -> throw PlaybackException(
                            getString(R.string.error_unknown),
                            throwable,
                            PlaybackException.ERROR_CODE_REMOTE_ERROR
                        )
                    }
                }
            } finally {
                Timber.tag(TAG).i("Stream fetch for $mediaId took ${SystemClock.elapsedRealtime() - fetchStart}ms")
            }

            // requireNotNull would escape the data source factory as a raw
            // IllegalArgumentException that ExoPlayer maps to a generic IO error
            // with no recoverable error code. Throw a typed PlaybackException so
            // onPlayerError routes it through handleGenericIOError (cache clear
            // + retry) instead of the generic fallback.
            val nonNullPlayback = playbackData ?: throw PlaybackException(
                getString(R.string.error_unknown),
                IllegalStateException("playerResponseForPlayback returned null for $mediaId"),
                PlaybackException.ERROR_CODE_IO_UNSPECIFIED
            )
            run {
                val format = nonNullPlayback.format
                val loudnessDb = nonNullPlayback.audioConfig?.loudnessDb
                val perceptualLoudnessDb = nonNullPlayback.audioConfig?.perceptualLoudnessDb

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
                            playbackUrl = nonNullPlayback.playbackTracking?.videostatsPlaybackUrl?.baseUrl
                        )
                    )
                }
                scope.launch(Dispatchers.IO) { recoverSong(mediaId, nonNullPlayback) }

                // Clear bypass flag now that we've fetched fresh stream
                if (bypassCacheForQualityChange.remove(mediaId)) {
                    Timber.tag("MusicService").d("Cleared bypass cache flag for $mediaId after fresh fetch")
                }

                val streamUrl = nonNullPlayback.streamUrl
                Timber.tag("SpineDebug").d("DataSourceResolver: resolved mediaId=$mediaId isSpineStream=${nonNullPlayback.isSpineStream} isTidalStream=${nonNullPlayback.isTidalStream} streamUrl=${streamUrl?.take(120)}")

                if (nonNullPlayback.isTidalStream || nonNullPlayback.isSpineStream) {
                    losslessStreamMediaIds.add(mediaId)
                } else {
                    losslessStreamMediaIds.remove(mediaId)
                }

                songUrlCache[effKey] =
                    streamUrl to System.currentTimeMillis() + (nonNullPlayback.streamExpiresInSeconds * 1000L)

                // Tidal/Spine hand back one direct static file URL, not a segmented
                // YouTube-style itag CDN, and their proxies can't be trusted to return
                // correct Content-Range headers on every ranged request. Slicing them
                // into CHUNK_LENGTH windows risks ExoPlayer reading a short response as
                // real end-of-file mid-song (auto-advances to the next track with no
                // error). Fetch the whole remaining file in one open-ended request instead.
                if (nonNullPlayback.isTidalStream || nonNullPlayback.isSpineStream) {
                    return@Factory spec.withUri(streamUrl.toUri())
                }
                return@Factory spec.withUri(streamUrl.toUri()).subrange(dataSpec.uriPositionOffset, CHUNK_LENGTH)
            }
        }
    }

    private fun createMediaSourceFactory() =
        DefaultMediaSourceFactory(
            createDataSourceFactory(),
            androidx.media3.extractor.DefaultExtractorsFactory()
                .setConstantBitrateSeekingEnabled(true)
                .setMp3ExtractorFlags(androidx.media3.extractor.mp3.Mp3Extractor.FLAG_ENABLE_INDEX_SEEKING)
        )

    private fun createRenderersFactory(
        eqProcessor: CustomEqualizerAudioProcessor,
        silenceProcessor: SilenceDetectorAudioProcessor,
        stallWatchdog: LosslessStallWatchdogAudioProcessor,
        bpmAnalyzer: TrackAnalyzerAudioProcessor,
        djFilter: DjFilterAudioProcessor,
        djTail: DjTailAudioProcessor,
        djDelay: DelayAudioProcessor,
    ) =
        object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ) = DefaultAudioSink
                .Builder(this@MusicService)
                .setEnableFloatOutput(enableFloatOutput)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .setAudioProcessorChain(
                    DefaultAudioSink.DefaultAudioProcessorChain(
                        // 2. Inject processor into audio pipeline
                        arrayOf(
                            eqProcessor,
                            silenceProcessor,
                            stallWatchdog,
                            bpmAnalyzer,
                            djFilter,
                            djTail,
                            djDelay,
                        ),
                        SilenceSkippingAudioProcessor(2_000_000, 20_000, 256),
                        SonicAudioProcessor(),
                    ),
                ).build()
        }

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

    // ExoPlayer state must be read on its application thread (main), so only the
    // file IO goes to the worker; the single worker serializes saves so a newer
    // snapshot can't be overwritten by an older one.
    private fun saveQueueToDisk() {
        val queue = currentQueue
        val title = queueTitle
        val items = player.mediaItems.mapNotNull { it.metadata }
        if (items.isEmpty()) return
        val mediaItemIndex = player.currentMediaItemIndex
        val position = player.currentPosition
        val playWhenReady = player.playWhenReady
        val repeatMode = player.repeatMode
        val shuffleModeEnabled = player.shuffleModeEnabled
        val volume = player.volume
        val playbackState = player.playbackState
        val automixItems = automixItems.value.mapNotNull { it.metadata }
        persistenceScope.launch {
            persistQueueToDisk(
                queue = queue,
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                playWhenReady = playWhenReady,
                repeatMode = repeatMode,
                shuffleModeEnabled = shuffleModeEnabled,
                volume = volume,
                playbackState = playbackState,
                automixItems = automixItems,
            )
        }
    }

    private fun persistQueueToDisk(
        queue: Queue,
        title: String?,
        items: List<MediaMetadata>,
        mediaItemIndex: Int,
        position: Long,
        playWhenReady: Boolean,
        repeatMode: Int,
        shuffleModeEnabled: Boolean,
        volume: Float,
        playbackState: Int,
        automixItems: List<MediaMetadata>,
    ) {
        try {
            // Save current queue with proper type information
            val persistQueue = queue.toPersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position
            )

            val persistAutomix =
                PersistQueue(
                    title = "automix",
                    items = automixItems,
                    mediaItemIndex = 0,
                    position = 0,
                )

            // Save player state
            val persistPlayerState = PersistPlayerState(
                playWhenReady = playWhenReady,
                repeatMode = repeatMode,
                shuffleModeEnabled = shuffleModeEnabled,
                volume = volume,
                currentPosition = position,
                currentMediaItemIndex = mediaItemIndex,
                playbackState = playbackState
            )

            runCatching {
                filesDir.resolve(PERSISTENT_QUEUE_FILE).outputStream().use { fos ->
                    ObjectOutputStream(fos).use { oos ->
                        oos.writeObject(persistQueue)
                    }
                }
                Timber.tag(TAG).d("Queue saved successfully")
            }.onFailure {
                Timber.tag(TAG).e(it, "Failed to save queue")
                reportException(it)
            }

            runCatching {
            filesDir.resolve(PERSISTENT_AUTOMIX_FILE).outputStream().use { fos ->
                ObjectOutputStream(fos).use { oos ->
                        oos.writeObject(persistAutomix)
                    }
                }
                Timber.tag(TAG).d("Automix saved successfully")
            }.onFailure {
                Timber.tag(TAG).e(it, "Failed to save automix")
                reportException(it)
            }

            runCatching {
                filesDir.resolve(PERSISTENT_PLAYER_STATE_FILE).outputStream().use { fos ->
                    ObjectOutputStream(fos).use { oos ->
                        oos.writeObject(persistPlayerState)
                    }
                }
                Timber.tag(TAG).d("Player state saved successfully")
            }.onFailure {
                Timber.tag(TAG).e(it, "Failed to save player state")
                reportException(it)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during queue save operation")
            reportException(e)
        }
    }

    override fun onDestroy() {
        isRunning = false

        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: Exception) {
            // Ignore
        }
        audioManager.unregisterAudioDeviceCallback(audioDeviceCallback)
        castConnectionHandler?.release()
        if (dataStore.get(PersistentQueueKey, true)) {
            saveQueueToDisk()
        }
        if (discordRpc?.isRpcRunning() == true) {
            discordRpc?.closeRPC()
        }
        discordRpc = null
        connectivityObserver.unregister()
        abandonAudioFocus()
        releaseLoudnessEnhancer()
        djEngine.release()
        mediaSession.release()
        player.removeListener(this)
        player.removeListener(sleepTimer)
        playerSilenceProcessors.remove(player)
        // Note: equalizerService audio processors are cleared in equalizerService.release() if needed,
        // or we can't easily reference the specific processor created in createExoPlayer here without storing it.
        // But since we are destroying the service, it's fine.
        player.release()
        discordUpdateJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = super.onBind(intent) ?: binder

    /**
     * The user swiped the app out of recents.
     *
     * This is where "stop music on task clear" has to live. It used to be handled in
     * MainActivity.onDestroy() behind an `isFinishing` check, and neither half of that
     * holds for a task swipe: the activity is destroyed by the system rather than
     * finishing, so `isFinishing` is false, and onDestroy is not guaranteed to run at all.
     * The setting therefore only ever fired when someone backed out of the app
     * deliberately -- never on the gesture it is named for.
     *
     * onTaskRemoved is the callback Android delivers precisely for this, and the service
     * is the thing still holding playback, so it is also the thing that can stop it.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (dataStore.get(StopMusicOnTaskClearKey, false)) {
            Timber.tag(TAG).d("Task removed with stop-on-task-clear enabled, stopping playback")
            player.stop()
            // stopSelf as well as stop(): a paused-but-alive session leaves the
            // notification sitting in the shade after the app is gone, which reads as the
            // setting not having worked even though the audio did stop.
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    /**
     * media3 promotes the service to foreground from its notification path, including from
     * an async bitmap-load callback (MediaNotificationManager.onNotificationUpdated). If the
     * app is backgrounded and no FGS start is allowed at that moment, the platform throws
     * ForegroundServiceStartNotAllowedException and the process dies.
     *
     * The throw happens inside ContextImpl, on THIS service used as the Context, so it is
     * catchable here â€” an override around onUpdateNotification is not, because the call is
     * posted and no longer on that frame. Nothing to recover: the OS refused the promotion,
     * so the notification simply stays unpromoted until playback next starts in foreground.
     */
    override fun startForegroundService(service: Intent): ComponentName? =
        try {
            super.startForegroundService(service)
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Foreground service start refused by platform")
            null
        }

    // Service.startForeground(id, notification[, type]) is `final` on the
    // platform Service class — cannot be overridden here the way
    // startForegroundService (a Context method) above can. media3's
    // MediaNotificationManager calls it directly and asynchronously (posted
    // to a Handler, not on a frame this class controls), so the
    // ForegroundServiceStartNotAllowedException it can throw is instead
    // caught as a last resort in CrashHandler.kt's uncaught-exception
    // handler — the only remaining interception point for this specific
    // platform call path. See CrashHandler.kt for the actual recovery.

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MusicWidgetReceiver.ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
                updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_LIKE -> {
                toggleLike()
            }
            MusicWidgetReceiver.ACTION_NEXT -> {
                player.seekToNext()
                updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_PREVIOUS -> {
                player.seekToPrevious()
                updateWidgetUI(player.isPlaying)
            }
            MusicWidgetReceiver.ACTION_UPDATE_WIDGET -> {
                updateWidgetUI(player.isPlaying)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Updates all app widgets with current playback state
     */
    /**
     * Repaints widget progress once a second, and only when that is actually worth
     * doing. Gated three ways deliberately: stop when paused (position isn't
     * moving), stop when the screen is off (nobody can see it), and stop when no
     * Now Playing widget is placed (nothing to repaint). Without those gates this
     * is a permanent once-a-second wakeup for no visible benefit.
     */
    private var widgetProgressJob: Job? = null

    private fun restartWidgetProgressTicker() {
        widgetProgressJob?.cancel()
        if (!player.isPlaying) return

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val hasProgressWidget = appWidgetManager
            .getAppWidgetIds(ComponentName(this, NowPlayingWidgetReceiver::class.java))
            .isNotEmpty()
        if (!hasProgressWidget) return

        widgetProgressJob = scope.launch {
            val powerManager = getSystemService(PowerManager::class.java)
            while (isActive) {
                delay(WIDGET_PROGRESS_INTERVAL_MS)
                if (powerManager?.isInteractive != false) {
                    updateWidgetUI(player.isPlaying)
                }
            }
        }
    }

    private fun updateWidgetUI(isPlaying: Boolean) {
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
                // Widget not added to home screen or other error
            }
        }
    }

    /**
     * Re-resolves the current stream after a per-item video-mode change — the
     * YouTube watch screen registering (or dropping) an override in
     * [com.convxy.music.utils.YouTubePlaybackState]. Same dance as the global
     * WatchVideoKey collector: drop the cached URL/bytes for both cache
     * namespaces, then restart the current item at its position so the data
     * source resolves a muxed (or audio-only) stream under the fresh key.
     */
    fun reloadCurrentStreamForVideoMode(mediaId: String) {
        if (!playerInitialized.value) return
        if (player.currentMediaItem?.mediaId != mediaId) return
        val currentPosition = player.currentPosition
        val wasPlaying = player.isPlaying
        val currentIndex = player.currentMediaItemIndex

        Timber.tag("MusicService").i("VIDEO OVERRIDE CHANGED — reloading $mediaId at ${currentPosition}ms")
        songUrlCache.remove(mediaId)
        songUrlCache.remove("$mediaId#video")
        scope.launch {
            // Cache removal must not run on the caller (main) thread: removing a
            // resource can delete hundreds of MB and block on the cache lock the
            // loader thread holds while writing the stream — a guaranteed ANR at
            // exactly the moment a video stream starts caching.
            withContext(Dispatchers.IO) {
                try {
                    playerCache.removeResource(mediaId)
                    playerCache.removeResource("$mediaId#video")
                } catch (e: Exception) {
                    Timber.tag("MusicService").e(e, "Failed to clear cache for video mode change")
                }
            }
            bypassCacheForQualityChange.add(mediaId)
            player.stop()
            player.seekTo(currentIndex, currentPosition)
            player.prepare()
            if (wasPlaying) {
                player.play()
            }
        }
    }

    private var widgetUpdateJob: Job? = null

    private fun startWidgetUpdates() {
        widgetUpdateJob?.cancel()
        widgetUpdateJob = scope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    updateWidgetUI(true)
                }
                delay(200)
            }
        }
    }

    private fun stopWidgetUpdates() {
        widgetUpdateJob?.cancel()
        widgetUpdateJob = null
    }

    private fun shareSong() {
        val songData = currentSong.value
        val songId = songData?.song?.id ?: return

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=$songId")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * Get the stream URL for a given media ID.
     * This is used for Google Cast to send the audio URL to Chromecast.
     */
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

    /**
     * Initialize Google Cast support
     */
    private fun initializeCast() {
        if (dataStore.get(com.convxy.music.constants.EnableGoogleCastKey, true)) {
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
                "newId=${newPosition.mediaItem?.mediaId} crossfadeEnabled=$crossfadeEnabled"
        )
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            scheduleCrossfade()
        }
    }

    private fun scheduleCrossfade() {
        crossfadeTriggerJob?.cancel()
        crossfadeTriggerJob = null
        djEngine.releaseProbe()
        if (!crossfadeEnabled || player.duration == C.TIME_UNSET || player.duration <= crossfadeDuration) return
        if (crossfadeGapless && isNextItemGapless()) return
        if (!player.hasNextMediaItem() && player.repeatMode != REPEAT_MODE_ONE) return

        val targetMediaId = player.currentMediaItem?.mediaId
        // With DJ mixing on this is snapped to a phrase boundary, and pulled to
        // an outro drop where the energy profile shows one. Otherwise it is the
        // plain `duration - crossfadeDuration` it has always been.
        val triggerTime = djEngine.transitionPointMs(
            targetMediaId,
            player.duration,
            crossfadeDuration.toLong(),
        )
        val mediaDelayMs = triggerTime - player.currentPosition
        if (mediaDelayMs <= 0) return
        // delay() counts wall-clock, triggerTime is media time: at 0.5x tempo the
        // trigger is twice as far away in real seconds, at 2x it is half. Unscaled,
        // a user who touched the tempo slider got crossfades firing mid-song or
        // after the track had already ended.
        val speed = player.playbackParameters.speed.takeIf { it > 0f } ?: 1f
        val delayMs = (mediaDelayMs / speed).toLong()

        if (djEngine.enabled) {
            val targetIndex = if (player.repeatMode == REPEAT_MODE_ONE) {
                player.currentMediaItemIndex
            } else {
                player.nextMediaItemIndex
            }
            if (targetIndex != C.INDEX_UNSET) {
                djEngine.hydrate(upcomingMediaIds(targetIndex))
                val incomingItem = player.getMediaItemAt(targetIndex)
                djEngine.schedulePreAnalysis(
                    item = incomingItem,
                    afterMs = delayMs - DjEngine.PROBE_LEAD_MS,
                    guardMediaId = targetMediaId,
                    currentMediaId = { player.currentMediaItem?.mediaId },
                )
            }
        }

        crossfadeTriggerJob = scope.launch {
            delay(delayMs)
            if (isActive && player.isPlaying && player.currentMediaItem?.mediaId == targetMediaId && !sleepTimer.pauseWhenSongEnd) {
                // Sanity re-check before actually firing: player.duration read
                // right after a seek/media-item transition (where this job is
                // scheduled from) isn't always settled yet, so delayMs can end
                // up far too short â€” this fired way earlier than the real
                // trigger point would, crossfading back into "next" (often the
                // previous track on a short/looping queue) mid-song instead of
                // near its end. Re-derive from live state; if we're genuinely
                // not close to the end yet, reschedule fresh instead of firing.
                val liveTriggerTime = djEngine.transitionPointMs(
                    targetMediaId,
                    player.duration,
                    crossfadeDuration.toLong(),
                )
                val prematureToleranceMs = 1500L
                if (player.duration != C.TIME_UNSET &&
                    player.currentPosition < liveTriggerTime - prematureToleranceMs
                ) {
                    scheduleCrossfade()
                } else {
                    startCrossfade()
                }
            }
        }
    }

    /** Media ids of the next few queue entries, for analysis hydration. */
    private fun upcomingMediaIds(fromIndex: Int): List<String> {
        val ids = mutableListOf<String>()
        player.currentMediaItem?.mediaId?.let(ids::add)
        var index = fromIndex
        while (index != C.INDEX_UNSET && ids.size <= DJ_HYDRATE_LOOKAHEAD) {
            ids.add(player.getMediaItemAt(index).mediaId)
            index = if (index + 1 < player.mediaItemCount) index + 1 else C.INDEX_UNSET
        }
        return ids
    }

    private fun isNextItemGapless(): Boolean {
        val current = player.currentMediaItem?.mediaMetadata ?: return false
        val nextIndex = player.nextMediaItemIndex
        if (nextIndex == C.INDEX_UNSET) return false
        val next = player.getMediaItemAt(nextIndex).mediaMetadata
        return current.albumTitle != null && current.albumTitle == next.albumTitle
    }

    private fun startCrossfade() {
        if (isCrossfading) return
        djEngine.releaseProbe()

        // Preserve player state before creating the secondary player. The live
        // ExoPlayer instance is always the authoritative, already-in-memory
        // source of truth for these â€” reading DataStore here would just be a
        // blocking round-trip to re-derive what `player` already holds, right
        // as the next track needs to start.
        val savedRepeatMode = player.repeatMode
        val savedShuffleEnabled = player.shuffleModeEnabled

        // For repeat-one, crossfade back into the same track
        val targetIndex = if (savedRepeatMode == REPEAT_MODE_ONE) {
            player.currentMediaItemIndex
        } else {
            player.nextMediaItemIndex
        }
        if (targetIndex == C.INDEX_UNSET) return

        val outgoingMediaId = player.currentMediaItem?.mediaId
        val incomingMediaId = player.getMediaItemAt(targetIndex).mediaId
        // Flush the outgoing track's now-complete energy profile before the
        // player is handed over â€” this is the only moment its outro exists.
        outgoingMediaId?.let { djEngine.captureEnergyProfile(player, it) }
        val djMixPlan = djEngine.planTransition(
            outgoingMediaId,
            incomingMediaId,
            outgoingDurationMs = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L,
        )

        secondaryPlayer = createExoPlayer()
        val secPlayer = secondaryPlayer!!
        secPlayer.addListener(secondaryPlayerListener)

        djMixPlan?.let { plan ->
            if (plan.tier != DjMixTier.PLAIN_CROSSFADE) {
                // Pitch rides alongside speed: Sonic shifts them independently,
                // so a semitone of key correction does not disturb the tempo lock.
                secPlayer.playbackParameters =
                    PlaybackParameters(plan.incomingSpeedAdjustment, plan.incomingPitchAdjustment)
                Timber.tag(TAG).d(
                    "DJ mix: ${plan.tier} tempo=${plan.incomingSpeedAdjustment} " +
                        "pitch=${plan.incomingPitchShiftSemitones} semitones"
                )
            }
        }

        val itemCount = player.mediaItemCount
        val items = mutableListOf<MediaItem>()
        // Copy entire queue history + future
        for (i in 0 until itemCount) {
            items.add(player.getMediaItemAt(i))
        }

        secPlayer.setMediaItems(items)
        // Seek to target track (next track, or current track for repeat-one).
        // With DJ mixing on, start at the incoming track's first phrase
        // boundary instead of 0 so the two overlap groove-on-groove rather
        // than groove-on-silence.
        val incomingStartMs = if (djMixPlan != null && djMixPlan.tier != DjMixTier.PLAIN_CROSSFADE) {
            djEngine.incomingStartMs(incomingMediaId)
        } else {
            0L
        }
        Timber.tag("DjEngine").d(
            "CROSSFADE start | outgoing=${outgoingMediaId} at ${player.currentPosition}ms/" +
                "${player.duration}ms | incoming=${incomingMediaId} seek=${incomingStartMs}ms | " +
                "fade=${crossfadeDuration.toLong()}ms"
        )
        secPlayer.seekTo(targetIndex, incomingStartMs)
        secPlayer.volume = 0f

        // Copy repeat and shuffle state to the new player
        secPlayer.repeatMode = savedRepeatMode
        secPlayer.shuffleModeEnabled = savedShuffleEnabled

        secPlayer.prepare()
        djEngine.armIncomingFilter(secPlayer)
        // Creative effects act on the track that is leaving; the incoming one
        // arrives clean.
        djEngine.startTailEffect(player, crossfadeDuration.toLong())
        startIncomingOnBeat(secPlayer, djMixPlan, outgoingMediaId)

        performCrossfadeSwap()

        // Rebuild shuffle order on the new primary player if shuffle was active
        if (savedShuffleEnabled) {
            val shufflePlaylistFirst = dataStore.get(ShufflePlaylistFirstKey, false)
            applyShuffleOrder(player.currentMediaItemIndex, player.mediaItemCount, shufflePlaylistFirst)
        }
    }

    /**
     * Releases the incoming player exactly on one of the outgoing track's
     * beats, which is the difference between two tracks playing at the same
     * tempo and two tracks actually beatmatched.
     *
     * The wait is at most one beat. The volume ramp in performCrossfadeSwap()
     * already parks on `while (!player.isPlaying)`, so the fade-in does not
     * start running against silence while we wait.
     */
    private fun startIncomingOnBeat(secPlayer: ExoPlayer, plan: DjMixPlan?, outgoingMediaId: String?) {
        if (plan == null || plan.tier == DjMixTier.PLAIN_CROSSFADE) {
            secPlayer.playWhenReady = true
            return
        }

        val grid = djEngine.analysisFor(outgoingMediaId)?.grid
        val position = player.currentPosition
        val waitMs = grid?.takeIf { it.bpm > 0f }
            ?.let { (it.beatAtOrAfter(position) - position).toLong() }
            ?: 0L

        // ponytail: ExoPlayer has no sample-accurate start, so alignment lands
        // within roughly a buffer (~20 ms) of the beat, and tempo lock alone
        // carries the rest of the transition. If it audibly drifts, micro-adjust
        // speed mid-fade from the measured position error instead.
        if (waitMs !in 1..MAX_BEAT_ALIGN_WAIT_MS) {
            Timber.tag("DjEngine").d("START incoming immediately (no usable beat align, waitMs=$waitMs)")
            secPlayer.playWhenReady = true
            return
        }

        Timber.tag("DjEngine").d("START incoming in ${waitMs}ms, on the next outgoing beat")
        scope.launch {
            delay(waitMs)
            runCatching { secPlayer.playWhenReady = true }
        }
    }

    /**
     * Unwinds the transition's tempo lock gradually instead of snapping.
     *
     * This used to be a single `PlaybackParameters(1f, 1f)` at the end of the
     * fade, so a 6 % tempo match dropped 6 % instantly the moment the crossfade
     * finished â€” clearly audible, and the exact opposite of what the tempo
     * match was for. A DJ eases the pitch fader back; so does this.
     */
    private fun glideSpeedBackToNormal() {
        val target = player
        val parameters = runCatching { target.playbackParameters }.getOrNull() ?: return
        val startSpeed = parameters.speed
        val startPitch = parameters.pitch
        if (startSpeed in 0.999f..1.001f && startPitch in 0.999f..1.001f) return

        // Below a couple of percent the correction is inaudible, so gliding it
        // buys nothing and costs a burst of playbackParameters writes right at
        // the end of the transition â€” each one reaching the playback thread and
        // re-firing listeners, which is audible as a hitch exactly where the mix
        // should be settling. Snap instead.
        if (startSpeed in 0.98f..1.02f && startPitch in 0.999f..1.001f) {
            runCatching { target.playbackParameters = PlaybackParameters(1f, 1f) }
            return
        }

        scope.launch {
            val steps = TEMPO_GLIDE_STEPS
            for (i in 1..steps) {
                val progress = i / steps.toFloat()
                // Pitch unwinds with the tempo. The key correction existed for
                // the overlap; leaving a track permanently a semitone off for
                // the rest of its runtime is a bigger alteration than the clash
                // it was fixing, and gliding it back is itself a pitch bend a
                // DJ would recognise.
                runCatching {
                    target.playbackParameters = PlaybackParameters(
                        startSpeed + (1f - startSpeed) * progress,
                        startPitch + (1f - startPitch) * progress,
                    )
                }
                delay(TEMPO_GLIDE_MS / steps)
            }
            runCatching { target.playbackParameters = PlaybackParameters(1f, 1f) }
        }
    }

    private fun performCrossfadeSwap() {
        isCrossfading = true
        val nextPlayer = secondaryPlayer ?: return
        val currentPlayer = player

        fadingPlayer = currentPlayer
        player = nextPlayer
        _playerFlow.value = player
        secondaryPlayer = null

        fadingPlayer?.removeListener(this)
        fadingPlayer?.removeListener(sleepTimer)

        // Widget progress ticking follows play/pause, so it never runs while paused.
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                restartWidgetProgressTicker()
            }
        })

        // Add listener to sync play/pause state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isCrossfading && fadingPlayer != null) {
                    if (isPlaying) {
                        fadingPlayer?.play()
                    } else {
                        fadingPlayer?.pause()
                    }
                } else {
                    player.removeListener(this)
                }
            }
        })

        nextPlayer.removeListener(secondaryPlayerListener)
        nextPlayer.addListener(this)
        nextPlayer.addListener(sleepTimer)

        sleepTimer.player = player

        try {
            (mediaSession as MediaSession).player = player
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Failed to swap player in MediaSession")
        }

        crossfadeJob = scope.launch {
            val duration = crossfadeDuration.toLong()
            val steps = 20
            val stepTime = duration / steps
            // Use the user's intended volume, not the fading player's current
            // one: a fadingPlayer snapshot taken while audio-ducked or already
            // mid-fade is near zero, and bootstrapping the new primary player
            // off it would leave every crossfade permanently quieter (or silent).
            val startVolume = if (isMuted.value) 0f else playerVolume.value.coerceIn(0f, 1f)

            try {
                for (i in 0..steps) {
                    if (!isActive) break
                    // Pause volume ramp if player is paused
                    while (!player.isPlaying && isActive) {
                        delay(100)
                    }

                    val progress = i / steps.toFloat()
                    // Equal-power (constant sum of squares) rather than the old
                    // quadratic pair, which dipped in perceived loudness through
                    // the middle of every crossfade.
                    val fadeIn = sin(progress * (PI / 2.0)).toFloat()
                    val fadeOut = cos(progress * (PI / 2.0)).toFloat()

                    try {
                        player.volume = startVolume * fadeIn
                        fadingPlayer?.volume = startVolume * fadeOut
                        // Bass swap runs on the same clock as the volume fade.
                        djEngine.driveFilterSweep(fadingPlayer, player, progress)
                    } catch (e: Exception) { break }

                    delay(stepTime)
                }
            } finally {
                // Unwind the ramp even if the job is cancelled mid-fade (a pause
                // parks the loop and the service scope can tear down around it):
                // otherwise the now-primary player keeps the last ramp value â€”
                // near zero at the fade-out end â€” for the rest of the track.
                // Read volume/mute live here, not the captured startVolume: if the
                // user unmuted or raised volume mid-fade, restoring the stale
                // snapshot would silently undo that action.
                runCatching { fadingPlayer?.volume = 0f }
                runCatching { player.volume = if (isMuted.value) 0f else playerVolume.value.coerceIn(0f, 1f) }
                runCatching { cleanupCrossfade() }
            }
        }
    }

    private fun cleanupCrossfade() {
        // Stop the effects before reopening the filters, not after: a running
        // loop-filter ramp would otherwise re-close the high-pass on a player
        // that is now the primary one.
        djEngine.stopTailEffects(fadingPlayer, player)
        djEngine.openFilters(player, fadingPlayer)
        fadingPlayer?.let { djEngine.unregisterPlayer(it) }
        fadingPlayer?.stop()
        fadingPlayer?.clearMediaItems()
        fadingPlayer?.release()
        fadingPlayer = null
        isCrossfading = false
        sleepTimer.notifySongTransition()

        // Tempo correction (if any) was only meant for the transition itself â€”
        // the now-primary player (former secPlayer) returns to normal speed
        // rather than staying tempo-shifted for the rest of the track.
        if (djEngine.activePlanNeedsSpeedReset) {
            glideSpeedBackToNormal()
        }
        djEngine.clearActivePlan()
    }

    private fun updateListenBrainz(title: String, artistNames: String, releaseName: String, durationMs: Long, isFinished: Boolean, startMs: Long = 0, endMs: Long = 0, positionMs: Long = 0) {
        val cleanToken = listenBrainzToken.trim()
        if (!listenBrainzEnabled || cleanToken.isBlank()) return
        scope.launch {
            if (isFinished) {
                com.convxy.music.ui.screens.settings.ListenBrainzManager.submitFinished(
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
                com.convxy.music.ui.screens.settings.ListenBrainzManager.submitPlayingNow(
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

    private fun checkAndSubmitListenBrainzFinished() {
        listenBrainzCurrentMediaId?.let { mediaId ->
            val startTs = listenBrainzCurrentStartTs
            if (startTs > 0) {
                scope.launch {
                    val mediaMetadata = player.mediaItems.find { it.mediaId == mediaId }?.metadata
                    val dbSong = if (mediaMetadata == null) database.song(mediaId).first() else null

                    val title = mediaMetadata?.title ?: dbSong?.song?.title ?: return@launch
                    val artistNames = mediaMetadata?.artists?.joinToString(" & ") { it.name }
                        ?: dbSong?.artists?.joinToString(" & ") { it.name } ?: ""
                    val releaseName = mediaMetadata?.album?.title ?: dbSong?.album?.title ?: ""
                    val durationMs = mediaMetadata?.duration?.takeIf { it != -1 }?.times(1000L)
                        ?: dbSong?.song?.duration?.takeIf { it != -1 }?.times(1000L) ?: 0L

                    updateListenBrainz(title, artistNames, releaseName, durationMs, isFinished = true, startMs = startTs, endMs = System.currentTimeMillis())
                }
            }
        }
        listenBrainzCurrentStartTs = 0L
        listenBrainzCurrentMediaId = null
    }

    private fun checkAndSubmitListenBrainzPlayingNow(mediaId: String) {
        scope.launch {
            val mediaMetadata = player.mediaItems.find { it.mediaId == mediaId }?.metadata
            val dbSong = if (mediaMetadata == null) database.song(mediaId).first() else null

            val title = mediaMetadata?.title ?: dbSong?.song?.title ?: return@launch
            val artistNames = mediaMetadata?.artists?.joinToString(" & ") { it.name }
                ?: dbSong?.artists?.joinToString(" & ") { it.name } ?: ""
            val releaseName = mediaMetadata?.album?.title ?: dbSong?.album?.title ?: ""
            val durationMs = mediaMetadata?.duration?.takeIf { it != -1 }?.times(1000L)
                ?: dbSong?.song?.duration?.takeIf { it != -1 }?.times(1000L) ?: 0L

            updateListenBrainz(title, artistNames, releaseName, durationMs, isFinished = false)
        }
    }

    companion object {
        /** Widget progress repaint cadence while playing with the screen on. */
        private const val WIDGET_PROGRESS_INTERVAL_MS = 1000L
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
        const val PERSISTENT_QUEUE_FILE = "persistent_queue.data"
        const val PERSISTENT_AUTOMIX_FILE = "persistent_automix.data"
        const val PERSISTENT_PLAYER_STATE_FILE = "persistent_player_state.data"
        const val MAX_CONSECUTIVE_ERR = 5
        const val MAX_RETRY_COUNT = 10
        // Constants for audio normalization
        private const val MAX_GAIN_MB = 300 // Maximum gain in millibels (3 dB)
        private const val MIN_GAIN_MB = -1500 // Minimum gain in millibels (-15 dB)

        private const val TAG = "MusicService"

        /** Queue entries ahead of the current one to hydrate analysis for. */
        private const val DJ_HYDRATE_LOOKAHEAD = 3

        /** Longest we will hold the incoming track back to land on a beat â€”
         *  one beat at 60 BPM. Anything longer means the grid is wrong. */
        private const val MAX_BEAT_ALIGN_WAIT_MS = 1_000L

        /** Roughly 8-16 beats of tempo glide after a DJ transition. */
        private const val TEMPO_GLIDE_MS = 6_000L
        private const val TEMPO_GLIDE_STEPS = 12

        /** Ceiling on how long an audio duck is allowed to hold the volume at
         *  20% before it is force-restored (some devices never send the gain). */
        private const val DUCK_RESTORE_MS = 10_000L

        @Volatile
        var isRunning = false
            private set
    }
}
