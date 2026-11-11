@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.convx.music.wear

import android.content.ComponentName
import android.os.SystemClock
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.convx.music.constants.BitPerfectKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.listentogether.ListenTogetherClient
import com.convx.music.listentogether.ListenTogetherPlayerConnectionBridge
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.TimedLyricsParser
import com.convx.music.playback.MusicService
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.watch.*
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.getKoin
import timber.log.Timber
import java.util.UUID

fun startWearBridge(service: MusicService, database: MusicDatabase): AutoCloseable? {
    if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(service) != ConnectionResult.SUCCESS) return null
    return WearPhoneBridge(service, database)
}

internal class WearPhoneBridge(private val service: MusicService, private val database: MusicDatabase) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val connection by lazy { PlayerConnection(service, service.MusicBinder(), database, scope) }
    private val session = UUID.randomUUID().toString()
    private val room by lazy { service.getKoin().get<ListenTogetherClient>() }
    private val roomBridge by lazy { service.getKoin().get<ListenTogetherPlayerConnectionBridge>() }
    private val router by lazy { WearCommandRouter(session, connection) { service.playerVolume.value = it } }
    private val dataClient = Wearable.getDataClient(service)
    private val messages = Wearable.getMessageClient(service)
    private val leases = mutableMapOf<String, Long>()
    private var sequence = 0L
    private var lyrics: List<LyricsEntry> = emptyList()
    private var lyricsMediaId: String? = null
    private var provider: String? = null
    private var lyricsOffset = 0L
    private var offsetMediaId: String? = null
    private var lyricsSynced = false
    private var observing = false
    private val canControl get() = (!room.isInRoom || room.canControlPlayback) && !roomBridge.partyLocked.value
    private val canAdjustVolume get() = !service.dataStore.get(BitPerfectKey, false)

    init { active = this }

    private fun observePlayback() {
        if (observing) return
        observing = true
        connection.shouldBlockPlaybackChanges = { !canControl }
        scope.launch {
            connection.currentLyrics.collectLatest { stored ->
                lyricsMediaId = stored?.id
                provider = stored?.provider
                val parsed = if (stored == null || stored.lyrics == LyricsEntity.LYRICS_NOT_FOUND) emptyList()
                else withContext(Dispatchers.Default) {
                    TimedLyricsParser.parse(stored.lyrics).ifEmpty {
                        stored.lyrics.lineSequence().map(String::trim).filter(String::isNotEmpty)
                            .filterNot { it.startsWith("[") }.map { LyricsEntry(0L, it) }.toList()
                    }
                }
                lyricsSynced = parsed.any { it.time > 0 || !it.words.isNullOrEmpty() }
                lyrics = parsed
            }
        }
        scope.launch { connection.currentSong.collect {
            lyricsOffset = it?.song?.lyricsOffset?.toLong() ?: 0L
            offsetMediaId = it?.song?.id
        } }
        scope.launch {
            while (isActive) {
                leases.entries.removeAll { it.value < SystemClock.elapsedRealtime() }
                if (leases.isNotEmpty()) {
                    try {
                        val request = PutDataMapRequest.create(WearProtocol.STATE_PATH)
                        request.dataMap.putByteArray(WearProtocol.STATE_KEY, WearProtocol.encodeState(snapshot()))
                        dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (error: Exception) { Timber.tag("WearCompanion").w(error, "Watch state unavailable") }
                }
                delay(1_000)
            }
        }
    }

    suspend fun receive(node: String, command: WearCommand) {
        observePlayback()
        leases[node] = SystemClock.elapsedRealtime() + 30_000
        router.receive(node, command, canControl, canAdjustVolume)
        messages.sendMessage(node, WearProtocol.STATE_PATH, WearProtocol.encodeState(snapshot(command.requestId))).await()
    }

    private fun snapshot(replyTo: String? = null): WearSnapshot {
        val state = connection.playbackSnapshot.value
        val available = lyrics.takeIf { lyricsMediaId == state.track?.id }.orEmpty()
        val offset = lyricsOffset.takeIf { offsetMediaId == state.track?.id } ?: 0L
        val activeLine = if (lyricsSynced) available.indexOfLast { !it.isBackground && it.time <= state.positionMs + offset } else -1
        val window = available.drop((activeLine - 3).coerceAtLeast(0)).take(11)
        return WearSnapshot(sessionId = session, sequence = ++sequence, replyTo = replyTo,
            mediaId = state.track?.id, title = state.track?.title.orEmpty(), artist = state.track?.artists?.joinToString().orEmpty(),
            isPlaying = state.isPlaying, positionMs = state.positionMs, durationMs = state.durationMs,
            speed = service.player.playbackParameters.speed,
            canSkipPrevious = state.canSkipPrevious, canSkipNext = state.canSkipNext, canControl = canControl,
            canAdjustVolume = canAdjustVolume, volume = service.playerVolume.value,
            shuffle = state.shuffleEnabled, repeatMode = state.repeatMode,
            lyricsProvider = provider.takeIf { available.isNotEmpty() }, lyricsOffsetMs = offset, lyricsSynced = lyricsSynced,
            lyrics = window.map { line -> WearLyricLine(line.time, line.text,
                line.words.orEmpty().map { WearWord((it.startTime * 1_000).toLong(), (it.endTime * 1_000).toLong(), it.text) }, line.isBackground) })
    }

    override fun close() {
        if (active === this) active = null
        if (observing) connection.dispose()
        scope.cancel()
    }

    companion object { var active: WearPhoneBridge? = null; private set }
}

class WearCommandListener : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path != WearProtocol.COMMAND_PATH) return
        val command = WearProtocol.decodeCommand(event.data) ?: return
        runBlocking {
            try {
                withTimeout(8_000) {
                    val nodes = Wearable.getCapabilityClient(this@WearCommandListener)
                        .getCapability(WearProtocol.WATCH_CAPABILITY, CapabilityClient.FILTER_REACHABLE).await().nodes
                    if (nodes.none { it.id == event.sourceNodeId }) return@withTimeout
                    withContext(Dispatchers.Main) {
                        val controller = MediaController.Builder(this@WearCommandListener,
                            SessionToken(this@WearCommandListener, ComponentName(this@WearCommandListener, MusicService::class.java)))
                            .buildAsync().await()
                        try { WearPhoneBridge.active?.receive(event.sourceNodeId, command) }
                        finally { controller.release() }
                    }
                }
            } catch (cancelled: CancellationException) {
                if (cancelled !is TimeoutCancellationException) throw cancelled
            } catch (error: Exception) { Timber.tag("WearCompanion").w(error, "Watch command unavailable") }
        }
    }
}
