package com.convx.music.client.desktop

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.presentation.HistorySections
import com.convx.music.catalog.presentation.SpeedDialSections
import com.convx.music.playback.SpeedDialPin
import com.convx.music.playback.data.JsonFileSpeedDial
import com.convx.music.catalog.MusicCatalogRepository
import com.convx.music.playback.Announcement
import com.convx.music.playback.DailyMix
import com.convx.music.playback.DailyMixes
import com.convx.music.playback.HistoryPlay
import com.convx.music.playback.PlayRecorder
import com.convx.music.playback.ReleaseAnnouncer
import com.convx.music.playback.ReleaseRadarSync
import com.convx.music.playback.TrackedRelease
import com.convx.music.playback.WatchedArtist
import com.convx.music.playback.WatchedArtists
import com.convx.music.playback.data.JsonFilePlayHistory
import com.convx.music.playback.data.JsonFileReleaseStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage

internal data class DesktopDiscoveryState(
    val mixes: List<DailyMix> = emptyList(),
    val releases: List<TrackedRelease> = emptyList(),
    val shelves: List<CatalogSection> = emptyList(),
    val pinned: Set<String> = emptySet(),
    /** Fixed for the run, so a shuffled Home keeps its order while the listener is on it. */
    val seed: Long = 0L,
)

/** Records what is played, builds Daily Mixes from it, and looks for new releases by the listener's artists. */
internal class DesktopDiscovery(
    private val controller: DesktopMusicController,
    private val history: JsonFilePlayHistory,
    private val releaseStore: JsonFileReleaseStore,
    private val radar: ReleaseRadarSync,
    private val speedDial: JsonFileSpeedDial,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sessionSeed = clock()

    val state: StateFlow<DesktopDiscoveryState> = combine(history.observe(), releaseStore.observe(), speedDial.observe()) { plays, releases, pins ->
        val now = clock()
        DesktopDiscoveryState(
            mixes = DailyMixes.build(plays.plays, now),
            releases = releases.recent(now / DAY_MS, RELEASE_WINDOW_DAYS).take(MAX_RELEASES),
            shelves = HistorySections.sections(plays.plays, now) + SpeedDialSections.sections(pins),
            pinned = pins.pins.mapTo(HashSet()) { it.key },
            seed = sessionSeed,
        )
    }.flowOn(Dispatchers.Default).stateIn(scope, SharingStarted.Eagerly, DesktopDiscoveryState())

    fun start() {
        scope.launch { recordPlays() }
        scope.launch { watchForReleases() }
    }

    fun close() = scope.cancel()

    fun togglePin(pin: SpeedDialPin) {
        scope.launch { speedDial.toggle(pin) }
    }

    private suspend fun recordPlays() {
        val recorder = PlayRecorder()
        val ticks = flow { while (true) { emit(Unit); delay(TICK_MS) } }
        combine(controller.playbackSnapshot, ticks) { snapshot, _ -> snapshot }.collect { snapshot ->
            val track = snapshot.track
            val counted = recorder.onSnapshot(track?.id, snapshot.isPlaying, snapshot.positionMs, snapshot.durationMs, clock())
            if (counted && track != null) {
                history.record(HistoryPlay(track.id, track.providerId, track.title, track.artists, track.artworkUrl, snapshot.durationMs, clock()))
            }
        }
    }

    private suspend fun watchForReleases() {
        delay(STARTUP_DELAY_MS)
        while (true) {
            if (releaseStore.read().isDue(clock())) {
                try {
                    radar.run(clock())
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // A failed look is tried again at the next check.
                }
            }
            delay(RADAR_CHECK_MS)
        }
    }

    private companion object {
        const val DAY_MS = 86_400_000L
        const val RELEASE_WINDOW_DAYS = 60
        const val MAX_RELEASES = 20
        const val TICK_MS = 5_000L
        const val STARTUP_DELAY_MS = 20_000L
        const val RADAR_CHECK_MS = 6 * 3_600_000L
    }
}

/** The artists the listener follows on YouTube Music; when signed out there are none to watch. */
internal fun desktopWatchedArtists(catalog: MusicCatalogRepository): suspend (Long) -> WatchedArtists = {
    val followed = (catalog.library(CatalogLibraryCollection.ARTISTS) as? CatalogResult.Success)
        ?.value?.entries.orEmpty().filterIsInstance<CatalogEntry.Artist>()
        .map { WatchedArtist(it.id.value, it.title) }
    WatchedArtists(followed, followed.mapTo(HashSet()) { it.id })
}

/** Shows an announcement as a system tray balloon, when the desktop has a tray. */
internal class DesktopTrayAnnouncer : ReleaseAnnouncer {
    override fun announce(announcement: Announcement): Boolean {
        if (!SystemTray.isSupported()) return false
        return try {
            val tray = SystemTray.getSystemTray()
            val icon = TrayIcon(BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "Convx")
            tray.add(icon)
            icon.displayMessage(announcement.title, announcement.text, TrayIcon.MessageType.INFO)
            Thread {
                Thread.sleep(TRAY_VISIBLE_MS)
                tray.remove(icon)
            }.apply { isDaemon = true }.start()
            true
        } catch (_: Exception) {
            false
        }
    }

    private companion object {
        const val TRAY_VISIBLE_MS = 12_000L
    }
}
