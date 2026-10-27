/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Runs a listening party for this device and keeps local playback in step with it.
 */

package com.convx.music.jam

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import com.convx.music.R
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import com.convx.music.extensions.metadata
import com.convx.music.extensions.toMediaItem
import com.convx.music.listentogether.ListenTogetherPlayerConnectionBridge
import com.convx.music.listentogether.JamMediaRecovery
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.ListQueue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

/** What this device knows about the party it is in. */
data class JamSession(
    val code: String,
    val you: PartyMember,
    val members: List<PartyMember> = emptyList(),
    val maxMembers: Int = 5,
    val hostOnlyControl: Boolean = false,
    val playback: PartyPlayback = PartyPlayback(),
    val queue: PartyQueue = PartyQueue(),
    val connected: Boolean = false,
    val localPlayback: JamLocalPlayback = JamLocalPlayback.PREPARING,
    val controlPending: Boolean = false,
) {
    /** The server refuses controls from everybody but the host when [hostOnlyControl] is on. */
    val canControl: Boolean get() = !hostOnlyControl || you.isHost
}

enum class JamLocalPlayback { PREPARING, SCHEDULED, PLAYING, PAUSED, RECOVERING, LOCALLY_PAUSED, UNAVAILABLE }

@Singleton
class JamManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bridge: ListenTogetherPlayerConnectionBridge,
    private val classic: dagger.Lazy<com.convx.music.listentogether.ListenTogetherManager>,
) {
    companion object {
        private const val TAG = "Jam"
        const val DEFAULT_SERVER = "https://api.bitchord.kushagrasingh.in"

        /** Hosts that were the default before; a saved one of these is upgraded rather than honoured. */
        private val RETIRED_SERVERS = setOf("https://bitchord-listen-together.onrender.com")

        /** How exactly a device lands on a new party control: everyone is meant to hit the same instant. */
        private const val ALIGN_MS = 150L

        /** Drift between controls worth an audible seek; below it two decoders are just normally apart. */
        private const val DRIFT_LIMIT_MS = 1_200L

        /** Over-limit readings in a row before correcting, and the rest after one, so a seek's rebuffer cannot loop. */
        private const val DRIFT_STRIKES = 2
        private const val DRIFT_COOLDOWN_MS = 6_000L
        private const val RECONCILE_TICK_MS = 1_000L

        /** Between this device's control and its echo the party still describes the world before it. */
        private const val INTENT_QUIET_MS = 4_500L

        /** Request authoritative state if a control's echo is missing. Never execute it speculatively. */
        private const val DEFERRED_PLAY_TIMEOUT_MS = 1_800L

        /** Below this a position difference is two devices slightly apart, not a seek anyone made. */
        private const val SEEK_REPORT_FLOOR_MS = 1_000L
        private const val PING_MS = 4_000L

        /** The first pings go out quickly so the clock offset is measured before the first state is applied. */
        private const val WARMUP_PINGS = 4
        private const val WARMUP_PING_MS = 400L

        /** A socket that answers nothing for this long is half-open (tunnel, doze); drop it and reconnect. */
        private const val SILENCE_LIMIT_MS = 14_000L
        private const val ACTIVITY_LIMIT = 30
        private val REFUSED = setOf(401, 403, 404)

        /** Frames our own changes cause come back as player events; ignore them for this long. */
        private const val ECHO_WINDOW_MS = 900L
        private const val QUEUE_SEND_DEBOUNCE_MS = 600L
        private const val MAX_UPCOMING = 25

        /** A party code from what was typed or pasted: the code itself, or a link that ends in it. */
        fun codeFrom(text: String): String =
            text.trim().substringBefore('?').substringBefore('#').trimEnd('/').substringAfterLast('/')
                .filter(Char::isLetterOrDigit).uppercase()

        /** Songs already played that travel with the queue, so the party can step back through them. */
        private const val HISTORY_SENT = 10
        private val QUEUE_REFUSALS = setOf("queue_full", "invalid_move", "not_found", "no_upcoming", "no_tracks", "missing_track")
        private const val RECONNECT_GIVE_UP_MS = 60_000L
    }

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private val client = JamClient()
    private val clock = JamClock()
    private val pendingPings = linkedMapOf<Long, Long>()
    private val playbackScheduler = JamPlaybackScheduler(SystemClock::elapsedRealtime)
    private val prefs = context.getSharedPreferences("jam", Context.MODE_PRIVATE)

    private val _session = MutableStateFlow<JamSession?>(null)
    val session: StateFlow<JamSession?> = _session.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    /** The last thing worth telling the user: a refusal, a lost connection, a removal. */
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** A party link opened from outside the app: joined as soon as the Party screen is up. */
    data class Invite(val code: String, val server: String?)

    private val _invite = MutableStateFlow<Invite?>(null)
    val invite: StateFlow<Invite?> = _invite.asStateFlow()

    fun offerInvite(code: String, server: String?) {
        _invite.value = Invite(normalise(code), server?.takeIf { it.startsWith("http") })
    }

    fun dismissMessage() { _message.value = null }

    fun consumeInvite(): Invite? = _invite.value.also { _invite.value = null }

    /** Link that opens this party in Convx. */
    fun inviteLink(code: String): String =
        "convx://party/$code?server=" + java.net.URLEncoder.encode(serverUrl, "UTF-8")

    suspend fun warm() = client.warm(serverUrl)

    var serverUrl: String
        get() = prefs.getString("server", null)?.trim()?.trimEnd('/')
            ?.takeIf { it.isNotBlank() && it !in RETIRED_SERVERS } ?: DEFAULT_SERVER
        set(value) = prefs.edit().putString("server", value.trim().ifBlank { DEFAULT_SERVER }).apply()

    /** The code of the party this device was last in, so it can offer to go back after a restart. */
    var lastCode: String?
        get() = prefs.getString("lastCode", null)
        private set(value) = prefs.edit().putString("lastCode", value).apply()

    /** Selected before joining; alternate recordings cannot be assumed to share a timeline. */
    var consistentSource: Boolean
        get() = prefs.getBoolean("consistentSource", true)
        set(value) { if (_session.value == null) prefs.edit().putBoolean("consistentSource", value).apply() }

    suspend fun preview(code: String): PartyPreview? = client.preview(serverUrl, normalise(code))

    suspend fun stats(): PartyStats? = client.stats(serverUrl)

    private val _activity = MutableStateFlow<List<PartyActivity>>(emptyList())
    val activity: StateFlow<List<PartyActivity>> = _activity.asStateFlow()

    var displayName: String
        get() = prefs.getString("name", null)?.takeIf { it.isNotBlank() } ?: Build.MODEL.orEmpty()
        set(value) = prefs.edit().putString("name", value.trim()).apply()

    private val identity: String
        get() = prefs.getString("id", null) ?: UUID.randomUUID().toString().also { prefs.edit().putString("id", it).apply() }

    private var token: String? = null
    private var socket: WebSocket? = null
    private var activeServerUrl: String? = null
    private var pingJob: Job? = null
    private var reconnectJob: Job? = null
    private var queueSendJob: Job? = null
    private var startJob: Job? = null
    private var leaving = false
    private var avatar: String? = null
    private var rejoining = false
    private var lastHeardAt = 0L
    private var lastSeq = -1L
    private var echoUntil = 0L
    private var reconcileJob: Job? = null
    private var deferredPlayJob: Job? = null
    private var intentQuietUntil = 0L
    private var driftStrikes = 0
    private var driftCooldownUntil = 0L
    private var sessionGeneration = 0L
    private var connectionGeneration = 0L
    private var transitionGeneration = 0L
    private var rejoinJob: Job? = null
    private var pendingControlJob: Job? = null
    private var applyingPlayerState = false
    private var originalPlaybackParameters: PlaybackParameters? = null
    private var speedCorrectionUntil = 0L
    private var preparedQueueIndex = -1
    private var preparedForSession = false

    /** The song this device was told to load, so the reconcile tick does not reload it every second. */
    private var loadingTrackId: String? = null

    /**
     * Silenced by something nobody here pressed: a call, another app taking the audio, unplugged
     * headphones. Following the party would take the audio straight back, so this device follows
     * nothing until its user presses play, and then catches up without moving anyone else.
     */
    private var detached = false

    /**
     * A listener in a host-only party paused their own phone. The party plays on without them;
     * the song still follows, the sound does not. Pressing play rejoins where the party has got to.
     */
    private var locallyPaused = false

    private var listenedPlayer: Player? = null

    // ---- party lifecycle -------------------------------------------------------------------

    suspend fun create(name: String, avatarUrl: String? = null) {
        val server = serverUrl
        enter(server) { client.create(server, request(name, avatarUrl)) }
    }

    suspend fun join(code: String, name: String, avatarUrl: String? = null) {
        val server = serverUrl
        enter(server) { client.join(server, normalise(code), request(name, avatarUrl)) }
    }

    fun leave() {
        val current = _session.value ?: return
        val leavingServer = activeServerUrl ?: serverUrl
        leaving = true
        val leavingToken = token
        teardown()
        endSession()
        scope.launch { if (leavingToken != null) client.leave(leavingServer, current.code, leavingToken) }
    }

    fun setHostOnlyControl(enabled: Boolean) = control("setHostOnlyControl") { put("enabled", enabled) }

    fun setAutoplay(enabled: Boolean) = control("setAutoplay") { put("enabled", enabled) }

    fun setMaxMembers(count: Int) = control("setMaxMembers") { put("maxMembers", count) }

    fun kick(memberId: String) = control("kick") { put("memberId", memberId) }

    private suspend fun enter(server: String, call: suspend () -> PartyMembership) {
        if (_busy.value) return
        _busy.value = true
        _message.value = null
        val enteringGeneration = ++sessionGeneration
        // Being in a classic room as well would run two syncs against one player.
        runCatching { classic.get().takeIf { it.isInRoom }?.leaveRoom() }
        val waking = scope.launch {
            delay(3_000)
            _message.value = "Waking the server, this can take up to 30 seconds…"
        }
        try {
            val membership = call()
            if (sessionGeneration != enteringGeneration) {
                scope.launch { client.leave(server, membership.code, membership.token) }
                return
            }
            teardown()
            leaving = false
            lastSeq = -1
            token = membership.token
            activeServerUrl = server
            lastCode = membership.code
            _activity.value = emptyList()
            val party = membership.party
            _session.value = JamSession(
                code = membership.code,
                you = membership.you,
                members = party.members,
                maxMembers = party.maxMembers,
                hostOnlyControl = party.hostOnlyControl,
                playback = party.playback,
                queue = party.queue,
            )
            bridge.jamConsistentSource = consistentSource
            bridge.setJamActive(true)
            connect(membership.code, membership.token)
            startReconcile()
            applyRemote(party.playback)
        } catch (e: JamException) {
            if (sessionGeneration == enteringGeneration) _message.value = e.message
        } finally {
            waking.cancel()
            if (_session.value != null) _message.value = null
            _busy.value = false
        }
    }

    private fun request(name: String, avatarUrl: String? = null): JoinRequest {
        val shown = name.trim().ifBlank { displayName }.take(80)
        displayName = shown
        avatar = avatarUrl
        return JoinRequest(userId = identity, deviceId = identity, displayName = shown, avatarUrl = avatarUrl?.takeIf { it.startsWith("http") })
    }

    /** Codes are typed by hand: the server normalises case and look-alikes, we only strip spaces. */
    private fun normalise(code: String) = codeFrom(code)

    // ---- socket ----------------------------------------------------------------------------

    private fun connect(code: String, token: String) {
        val generation = ++connectionGeneration
        clock.reset()
        pendingPings.clear()
        socket = client.open(activeServerUrl ?: serverUrl, code, token, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch {
                    if (generation != connectionGeneration || webSocket !== socket || leaving) return@launch
                    lastHeardAt = SystemClock.elapsedRealtime()
                    _session.value = _session.value?.copy(connected = true)
                    startPing(webSocket)
                    webSocket.send("""{"type":"sync"}""")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    if (generation != connectionGeneration || webSocket !== socket || leaving) return@launch
                    runCatching { onFrame(text) }.onFailure { Timber.tag(TAG).w(it, "Bad frame") }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = lost(webSocket)

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch {
                    if (generation != connectionGeneration || webSocket !== socket || leaving) return@launch
                    // A refused handshake means the token is gone (grace period passed or the server restarted);
                    // joining again reclaims the slot if the party still exists.
                    if (webSocket === socket && !leaving && response?.code in REFUSED) rejoin() else lost(webSocket)
                }
            }
        })
    }

    private fun lost(webSocket: WebSocket) {
        scope.launch {
            if (webSocket !== socket || leaving) return@launch
            pingJob?.cancel()
            _session.value = _session.value?.copy(connected = false)
            reconnect()
        }
    }

    private fun rejoin() {
        val session = _session.value ?: return
        if (rejoining) return
        rejoining = true
        val generation = sessionGeneration
        rejoinJob = scope.launch {
            try {
                pingJob?.cancel()
                _session.value = _session.value?.copy(connected = false)
                val membership = client.join(activeServerUrl ?: serverUrl, session.code, request(displayName, avatar))
                if (leaving || _session.value == null || generation != sessionGeneration) return@launch
                token = membership.token
                lastSeq = -1
                update { it.copy(you = membership.you) }
                connect(membership.code, membership.token)
            } catch (e: JamException) {
                if (generation != sessionGeneration) return@launch
                if (e.code == "unreachable") {
                    reconnect()
                } else {
                    _message.value = if (e.code == "no_such_party" || e.code == "http_404") "The party has ended" else e.message
                    dropSession()
                }
            } finally {
                if (generation == sessionGeneration) rejoining = false
            }
        }
    }

    private fun reconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            val started = SystemClock.elapsedRealtime()
            var wait = 1_000L
            while (!leaving && _session.value != null) {
                if (SystemClock.elapsedRealtime() - started > RECONNECT_GIVE_UP_MS) {
                    _message.value = "Lost connection to the party"
                    dropSession(forget = false)
                    return@launch
                }
                delay(Random.nextLong(wait / 2, wait + 1))
                wait = (wait * 2).coerceAtMost(8_000L)
                val session = _session.value ?: return@launch
                val current = token ?: return@launch
                if (rejoining) continue
                ++connectionGeneration
                socket?.cancel()
                connect(session.code, current)
                delay(3_000)
                if (_session.value?.connected == true) return@launch
            }
        }
    }

    private fun startPing(webSocket: WebSocket) {
        pingJob?.cancel()
        pingJob = scope.launch {
            var sent = 0
            while (true) {
                if (SystemClock.elapsedRealtime() - lastHeardAt > SILENCE_LIMIT_MS) {
                    // Cancelling fails the socket, which routes through the usual reconnect.
                    webSocket.cancel()
                    return@launch
                }
                sendClockPing(webSocket)
                if (sent >= WARMUP_PINGS) report(webSocket)
                delay(if (++sent <= WARMUP_PINGS) WARMUP_PING_MS else PING_MS)
            }
        }
    }

    /** Tells the server where this device really is; it only logs it, but it is the first thing to look at when two phones disagree. */
    private fun report(webSocket: WebSocket) {
        val player = listenedPlayer ?: return
        if (player.currentMediaItem == null) return
        webSocket.send("""{"type":"report","positionMs":${player.currentPosition},"isPlaying":${player.isPlaying}}""")
        Timber.tag(TAG).d("Local playback: track=${player.currentMediaItem?.mediaId} state=${_session.value?.localPlayback} clockUncertaintyEstimate=${clock.uncertaintyMs()}ms")
    }

    private fun sendClockPing(webSocket: WebSocket) {
        val stamp = System.currentTimeMillis()
        pendingPings[stamp] = SystemClock.elapsedRealtime()
        while (pendingPings.size > 12) pendingPings.remove(pendingPings.keys.first())
        webSocket.send("""{"type":"ping","clientMs":$stamp}""")
    }

    private fun teardown() {
        ++sessionGeneration
        ++connectionGeneration
        ++transitionGeneration
        rejoinJob?.cancel()
        rejoinJob = null
        rejoining = false
        pendingControlJob?.cancel()
        restorePlaybackSpeed()
        pingJob?.cancel()
        reconnectJob?.cancel()
        queueSendJob?.cancel()
        startJob?.cancel()
        reconcileJob?.cancel()
        deferredPlayJob?.cancel()
        loadingTrackId = null
        detached = false
        locallyPaused = false
        intentQuietUntil = 0L
        socket?.close(1000, null)
        socket = null
        token = null
        activeServerUrl = null
        clock.reset()
        pendingPings.clear()
        preparedQueueIndex = -1
        preparedForSession = false
        bridge.setJamActive(false)
    }

    /** [forget] is false when the party may still exist, so a restart can offer to go back to it. */
    private fun dropSession(forget: Boolean = true) {
        teardown()
        endSession(forget)
    }

    private fun endSession(forget: Boolean = true) {
        _session.value = null
        _activity.value = emptyList()
        if (forget) lastCode = null
    }

    // ---- frames in -------------------------------------------------------------------------

    private fun onFrame(text: String) {
        val frame = client.json.parseToJsonElement(text).jsonObject
        lastHeardAt = SystemClock.elapsedRealtime()
        when (frame["type"]?.jsonPrimitive?.contentOrNull) {
            "activity" -> {
                val entry = client.json.decodeFromJsonElement<PartyActivity>(frame)
                _activity.value = (listOf(entry) + _activity.value).take(ACTIVITY_LIMIT)
            }
            "welcome" -> frame["party"]?.let { party ->
                val snapshot = client.json.decodeFromJsonElement<PartySnapshot>(party)
                frame["you"]?.let { you -> update { it.copy(you = client.json.decodeFromJsonElement(you)) } }
                update {
                    it.copy(
                        members = snapshot.members, maxMembers = snapshot.maxMembers,
                        hostOnlyControl = snapshot.hostOnlyControl, queue = snapshot.queue,
                    )
                }
                onState(snapshot.playback)
            }
            "state" -> frame["playback"]?.let { onState(client.json.decodeFromJsonElement(it)) }
            "queue" -> frame["queue"]?.let { queue ->
                val incoming = client.json.decodeFromJsonElement<PartyQueue>(queue)
                if (incoming.seq < (_session.value?.queue?.seq ?: -1L)) return
                update { it.copy(queue = incoming) }
                applyQueue(incoming)
            }
            "members" -> update { current ->
                current.copy(
                    members = frame["members"]?.let { client.json.decodeFromJsonElement<List<PartyMember>>(it) } ?: current.members,
                    maxMembers = frame["maxMembers"]?.jsonPrimitive?.intOrNull ?: current.maxMembers,
                    hostOnlyControl = frame["hostOnlyControl"]?.jsonPrimitive?.booleanOrNull ?: current.hostOnlyControl,
                ).let { next -> next.copy(you = next.members.firstOrNull { it.memberId == current.you.memberId } ?: current.you) }
            }
            "pong" -> {
                val sent = frame["clientMs"]?.jsonPrimitive?.longOrNull
                val server = frame["serverMs"]?.jsonPrimitive?.longOrNull
                val wasSynced = clock.isFresh(SystemClock.elapsedRealtime())
                val sentMonotonic = sent?.let(pendingPings::remove)
                if (sentMonotonic != null && server != null) {
                    clock.observe(sentMonotonic, server, SystemClock.elapsedRealtime())
                }
                // Until now a playing party was only loaded, not positioned; the first measured clock places it.
                if (!wasSynced && clock.synced) _session.value?.let { applyRemote(it.playback) }
            }
            "error" -> {
                resolvePendingControl()
                val code = frame["error"]?.jsonPrimitive?.contentOrNull
                _message.value = frame["message"]?.jsonPrimitive?.contentOrNull ?: code
                if (code == "host_only") Toast.makeText(context, R.string.jam_host_only_toast, Toast.LENGTH_SHORT).show()
                // A refused queue edit leaves this device ahead of the party; fall back to what the party holds.
                if (code in QUEUE_REFUSALS) _session.value?.let { applyQueue(it.queue) }
            }
            "bye" -> {
                _message.value = if (frame["reason"]?.jsonPrimitive?.contentOrNull == "kicked") "You were removed from the party" else null
                dropSession()
            }
        }
    }

    private fun update(change: (JamSession) -> JamSession) {
        _session.value = _session.value?.let(change)
    }

    private fun onState(playback: PartyPlayback) {
        // Every mutation bumps seq; an older or equal one is a duplicate or a straggler.
        if (playback.seq <= lastSeq) {
            if (playback.seq == lastSeq && _session.value?.controlPending == true) {
                resolvePendingControl()
                applyRemote(playback)
            }
            return
        }
        lastSeq = playback.seq
        resolvePendingControl()
        val before = _session.value?.playback
        update { it.copy(playback = playback) }
        // A queue edit bumps seq too, and must not cost an audible seek.
        val moved = before == null || before.anchorMs != playback.anchorMs || before.positionMs != playback.positionMs ||
            before.isPlaying != playback.isPlaying || before.track?.videoId != playback.track?.videoId
        if (moved) applyRemote(playback)
        val session = _session.value ?: return
        if (playback.queueSeq != session.queue.seq) socket?.send("""{"type":"syncQueue"}""")
    }

    // ---- party -> local player -------------------------------------------------------------

    private fun applyRemote(playback: PartyPlayback) {
        val connection = bridge.playerConnection.value ?: return
        val player = connection.player
        val generation = ++transitionGeneration
        val sessionId = sessionGeneration
        startJob?.cancel()
        queueSendJob?.cancel()
        restorePlaybackSpeed()
        val track = playback.track ?: run {
            withPlayerChange { player.pause() }
            setLocalPlayback(JamLocalPlayback.PAUSED)
            return
        }
        if (detached) return
        if (player.currentMediaItem?.metadata?.isLocalFile == true) return
        if (bridge.jamRecovery.value != JamMediaRecovery.NONE) {
            if (player.currentMediaItem?.mediaId == track.videoId) return
            connection.service.resetJamAudioRecovery()
        }
        driftStrikes = 0
        deferredPlayJob?.cancel()
        val queueIndex = playback.queueIndex
        val needsLoad = !preparedForSession || player.currentMediaItem?.mediaId != track.videoId ||
            (preparedQueueIndex >= 0 && queueIndex >= 0 && preparedQueueIndex != queueIndex)
        if (needsLoad) {
            loadingTrackId = track.videoId
            setLocalPlayback(JamLocalPlayback.PREPARING)
            withPlayerChange { player.pause(); startTrack(connection, track) }
        } else if (player.playbackState == Player.STATE_IDLE && player.playerError == null) {
            withPlayerChange { player.prepare() }
        }
        preparedQueueIndex = queueIndex
        if (!playback.isPlaying || locallyPaused) withPlayerChange { player.pause() }
        startJob = scope.launch {
            val began = SystemClock.elapsedRealtime()
            fun current(): Boolean = generation == transitionGeneration && sessionId == sessionGeneration &&
                bridge.playerConnection.value === connection && _session.value?.playback?.track?.videoId == track.videoId
            val ready = playbackScheduler.awaitReady(
                current = { current() && !detached && bridge.jamRecovery.value == JamMediaRecovery.NONE },
                ready = { player.currentMediaItem?.mediaId == track.videoId && player.playbackState == Player.STATE_READY &&
                    (_session.value?.playback?.isPlaying != true || clock.isFresh(SystemClock.elapsedRealtime())) },
            )
            if (!ready) {
                if (current() && !detached && bridge.jamRecovery.value == JamMediaRecovery.NONE) {
                    bridge.setJamRecovery(JamMediaRecovery.UNAVAILABLE)
                    withPlayerChange { player.pause() }
                    _message.value = "Audio could not be prepared. Retry to rejoin the party."
                }
                return@launch
            }
            if (!current() || detached) return@launch
            var latest = _session.value?.playback ?: return@launch
            val wait = JamSync.startDelayMs(latest, clock.serverNow(SystemClock.elapsedRealtime()))
            if (wait > 0) {
                setLocalPlayback(JamLocalPlayback.SCHEDULED)
                withPlayerChange { player.pause() }
                if (!playbackScheduler.awaitStart(current = { current() && !detached }, startDelayMs = {
                    JamSync.startDelayMs(_session.value?.playback ?: latest, clock.serverNow(SystemClock.elapsedRealtime()))
                })) return@launch
            }
            if (!current() || detached || bridge.jamRecovery.value != JamMediaRecovery.NONE) return@launch
            latest = _session.value?.playback ?: return@launch
            val target = JamSync.positionAt(latest, clock.serverNow(SystemClock.elapsedRealtime()))
                .coerceAtLeast(0L).let { position -> if (player.duration > 0) minOf(position, player.duration) else position }
            withPlayerChange {
                if (abs(player.currentPosition - target) > ALIGN_MS) player.seekTo(target)
                player.playWhenReady = latest.isPlaying && !locallyPaused
            }
            loadingTrackId = null
            setLocalPlayback(if (locallyPaused) JamLocalPlayback.LOCALLY_PAUSED else
                if (latest.isPlaying) JamLocalPlayback.PLAYING else JamLocalPlayback.PAUSED)
            Timber.tag(TAG).d("Prepared ${track.videoId} in ${SystemClock.elapsedRealtime() - began}ms, target=$target")
        }
    }

    private fun startTrack(connection: PlayerConnection, track: PartyTrack) {
        preparedForSession = true
        connection.service.resetJamAudioRecovery()
        val queue = _session.value?.queue
        val inQueue = queue != null && queue.index in queue.items.indices && queue.items[queue.index].videoId == track.videoId
        val items = if (inQueue) queue!!.items.map { toMediaItem(it) } else listOf(toMediaItem(track))
        // The party's own playback must not trip the guest lock that stops the user driving the player.
        connection.allowInternalSync = true
        try {
            connection.playQueue(ListQueue(title = null, items = items, startIndex = if (inQueue) queue!!.index else 0),
                force = true, playWhenReady = false)
        } finally {
            connection.allowInternalSync = false
        }
    }

    private inline fun withPlayerChange(change: () -> Unit) {
        applyingPlayerState = true
        echoUntil = SystemClock.elapsedRealtime() + ECHO_WINDOW_MS
        try { change() } finally { applyingPlayerState = false }
    }

    private fun setLocalPlayback(state: JamLocalPlayback) = update { it.copy(localPlayback = state) }

    private fun restorePlaybackSpeed() {
        originalPlaybackParameters?.let { parameters -> listenedPlayer?.playbackParameters = parameters }
        originalPlaybackParameters = null
        speedCorrectionUntil = 0L
    }

    fun retryAudio() {
        val connection = bridge.playerConnection.value ?: return
        if (_session.value == null) return
        detached = false
        locallyPaused = false
        bridge.setJamRecovery(JamMediaRecovery.NONE)
        connection.service.resetJamAudioRecovery()
        setLocalPlayback(JamLocalPlayback.PREPARING)
        withPlayerChange { connection.player.prepare() }
        _session.value?.let { applyRemote(it.playback) }
    }

    private fun startReconcile() {
        reconcileJob?.cancel()
        reconcileJob = scope.launch {
            while (true) {
                delay(RECONCILE_TICK_MS)
                _session.value?.takeIf { it.connected }?.let { reconcile(it.playback) }
            }
        }
    }

    /**
     * Puts this device back where the party is between controls: a song change it missed, a start
     * that never happened, or real drift. Drift is corrected only after [DRIFT_STRIKES] readings in
     * a row and not again for [DRIFT_COOLDOWN_MS], since on a slow phone the rebuffer a seek costs
     * can be as long as the gap it closes.
     */
    private fun reconcile(playback: PartyPlayback) {
        val now = SystemClock.elapsedRealtime()
        if (speedCorrectionUntil > 0L && now >= speedCorrectionUntil) restorePlaybackSpeed()
        if (now < intentQuietUntil || now < echoUntil || detached || !clock.isFresh(now)) return
        if (bridge.jamRecovery.value != JamMediaRecovery.NONE) return
        if (startJob?.isActive == true || deferredPlayJob?.isActive == true) return
        val player = bridge.playerConnection.value?.player ?: return
        val track = playback.track ?: return
        if (player.currentMediaItem?.metadata?.isLocalFile == true) return
        if (player.currentMediaItem?.mediaId != track.videoId) {
            if (loadingTrackId != track.videoId) applyRemote(playback)
            return
        }
        if (locallyPaused) return
        if (player.playWhenReady != playback.isPlaying) return applyRemote(playback)
        if (!playback.isPlaying || player.playbackState != Player.STATE_READY ||
            player.playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE
        ) {
            driftStrikes = 0
            restorePlaybackSpeed()
            return
        }
        val target = JamSync.positionAt(playback, clock.serverNow(now))
        val error = target - player.currentPosition
        val softLimit = maxOf(250L, clock.uncertaintyMs() * 2)
        if (abs(error) <= softLimit) {
            driftStrikes = 0
            restorePlaybackSpeed()
            return
        }
        if (abs(error) < DRIFT_LIMIT_MS) {
            if (originalPlaybackParameters == null && player.playbackParameters.speed == 1f && now >= driftCooldownUntil) {
                originalPlaybackParameters = player.playbackParameters
                player.playbackParameters = PlaybackParameters(if (error > 0) 1.02f else 0.98f, player.playbackParameters.pitch)
                speedCorrectionUntil = now + 10_000L
                driftCooldownUntil = now + 16_000L
            }
            return
        }
        if (now < driftCooldownUntil || ++driftStrikes < DRIFT_STRIKES) return
        Timber.tag(TAG).i("Correcting ${player.currentPosition - target}ms of drift")
        driftStrikes = 0
        driftCooldownUntil = now + DRIFT_COOLDOWN_MS
        restorePlaybackSpeed()
        withPlayerChange { player.seekTo(target) }
    }

    /**
     * Holds this device's own play until the server's echo, which schedules the start a moment
     * ahead for everyone; starting at the press would leave this phone ahead of the party for good.
     */
    private fun deferPlay() {
        val connection = bridge.playerConnection.value ?: return
        if (!clock.synced) return
        echoUntil = SystemClock.elapsedRealtime() + ECHO_WINDOW_MS
        withPlayerChange { connection.pause() }
        deferredPlayJob?.cancel()
        deferredPlayJob = scope.launch {
            delay(DEFERRED_PLAY_TIMEOUT_MS)
            Timber.tag(TAG).w("Party did not echo play; requesting authoritative state")
            socket?.send("""{"type":"sync"}""")
        }
    }

    /** A queue change that leaves the playing track alone only swaps what comes after it. */
    private fun applyQueue(queue: PartyQueue) {
        val connection = bridge.playerConnection.value ?: return
        val player = connection.player
        if (queue.items.isEmpty() || queue.index !in queue.items.indices) return
        if (player.currentMediaItem?.mediaId != queue.items[queue.index].videoId) return
        val upcoming = queue.items.drop(queue.index + 1).map { toMediaItem(it) }
        val signature = listOf(player.currentMediaItem!!.mediaId) + upcoming.map { it.mediaId }
        if (signature == playlistIds(player).drop(player.currentMediaItemIndex)) return
        echoUntil = SystemClock.elapsedRealtime() + ECHO_WINDOW_MS
        val from = player.currentMediaItemIndex + 1
        if (from < player.mediaItemCount) player.removeMediaItems(from, player.mediaItemCount)
        player.addMediaItems(upcoming)
    }

    // ---- local player -> party -------------------------------------------------------------

    private val playerListener = object : Player.Listener {
        private fun suppressed() = applyingPlayerState || SystemClock.elapsedRealtime() < echoUntil ||
            bridge.jamRecovery.value != JamMediaRecovery.NONE || !canSend()

        override fun onPlaybackStateChanged(playbackState: Int) {
            val session = _session.value ?: return
            if (bridge.jamRecovery.value != JamMediaRecovery.NONE || startJob?.isActive == true) return
            if (playbackState == Player.STATE_BUFFERING && !detached && !locallyPaused) {
                restorePlaybackSpeed()
                setLocalPlayback(JamLocalPlayback.RECOVERING)
            } else if (playbackState == Player.STATE_READY && session.localPlayback == JamLocalPlayback.RECOVERING) {
                applyRemote(session.playback)
            }
        }

        /** A guest's own change while the host holds the controls: undo it and say why. */
        private fun refused(): Boolean {
            if (SystemClock.elapsedRealtime() < echoUntil) return false
            val session = _session.value ?: return false
            if (!session.connected || session.canControl) return false
            applyRemote(session.playback)
            applyQueue(session.queue)
            Toast.makeText(context, R.string.jam_host_only_toast, Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            val session = _session.value ?: return
            val player = listenedPlayer ?: return
            if (applyingPlayerState) return
            if (bridge.jamRecovery.value != JamMediaRecovery.NONE) {
                if (playWhenReady && reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) retryAudio()
                return
            }
            if (!playWhenReady && reason != Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
                // One person taking a call must not stop the music for everyone. See [detached].
                detached = true
                setLocalPlayback(JamLocalPlayback.LOCALLY_PAUSED)
                restorePlaybackSpeed()
                startJob?.cancel()
                deferredPlayJob?.cancel()
                return
            }
            if (SystemClock.elapsedRealtime() < echoUntil) return
            if (startJob?.isActive == true && !playWhenReady) return
            if (playWhenReady && detached) {
                // Coming back is a catch-up, not a control: the gap belongs to this device alone.
                detached = false
                applyRemote(session.playback)
                return
            }
            if (!session.canControl) {
                locallyPaused = !playWhenReady
                if (locallyPaused) setLocalPlayback(JamLocalPlayback.LOCALLY_PAUSED)
                // Rejoins where the party has got to, or pauses again if the party itself is paused.
                if (playWhenReady) applyRemote(session.playback)
                return
            }
            if (!session.connected || player.currentMediaItem?.metadata?.isLocalFile == true) return
            // Already what the party says: an echo of its own change, or a no-op.
            if (playWhenReady == session.playback.isPlaying) return
            control(if (playWhenReady) "play" else "pause") { put("positionMs", player.currentPosition) }
            if (playWhenReady) deferPlay()
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            if (reason != Player.DISCONTINUITY_REASON_SEEK || refused() || suppressed()) return
            if (detached || locallyPaused || listenedPlayer?.currentMediaItem?.metadata?.isLocalFile == true) return
            val playback = _session.value?.playback ?: return
            // Two devices normally sit a little apart; only a real move of the playhead is a seek.
            val partyAt = JamSync.positionAt(playback, clock.serverNow(SystemClock.elapsedRealtime()))
            if (clock.synced && abs(newPosition.positionMs - partyAt) <= SEEK_REPORT_FLOOR_MS) return
            control("seek") { put("positionMs", newPosition.positionMs) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (mediaItem == null) return
            if (loadingTrackId == mediaItem.mediaId && startJob?.isActive == true) return
            // A listener reaching the end of a song first simply waits for the party to move on.
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && _session.value?.canControl == false) {
                withPlayerChange { listenedPlayer?.pause() }
                setLocalPlayback(JamLocalPlayback.SCHEDULED)
                socket?.send("""{"type":"sync"}""")
                return
            }
            if (refused() || suppressed()) return
            val session = _session.value ?: return
            if (session.playback.track?.videoId == mediaItem.mediaId) return
            val track = toTrack(mediaItem) ?: run {
                _message.value = "Files on this phone can't be shared with the party"
                return
            }
            val queue = session.queue
            val id = mediaItem.mediaId
            // A skip the user made onto the neighbouring song is a step through the shared queue, not a new song.
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                if (queue.items.getOrNull(queue.index + 1)?.videoId == id) return control("next") {}
                if (queue.items.getOrNull(queue.index - 1)?.videoId == id) return control("previous") {}
            }
            val inQueue = queue.items.indices.filter { queue.items[it].videoId == id }.minByOrNull { abs(it - queue.index) }
            val playing = listenedPlayer?.playWhenReady ?: true
            control("setTrack") {
                put("track", client.json.encodeToJsonElement(track))
                put("positionMs", 0L)
                put("isPlaying", playing)
                inQueue?.let { put("queueIndex", it) }
            }
            // A song the user picked starts with everyone; one that followed on by itself keeps going.
            if (playing && reason != Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) deferPlay()
        }

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            if (startJob?.isActive == true) return
            if (reason != Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED || refused() || suppressed()) return
            queueSendJob?.cancel()
            queueSendJob = scope.launch {
                delay(QUEUE_SEND_DEBOUNCE_MS)
                sendQueue()
            }
        }
    }

    // Declared after [playerListener] on purpose: the collector below runs immediately on the main
    // thread, and a listener that is still null makes ExoPlayer throw a NullPointerException.
    init {
        scope.launch {
            bridge.jamTimingRevision.drop(1).collect {
                if (_session.value == null) return@collect
                restorePlaybackSpeed()
                driftStrikes = 0
                clock.reset()
                pendingPings.clear()
                socket?.let(::sendClockPing)
            }
        }
        scope.launch {
            var wasRecovering = false
            bridge.jamRecovery.collect { recovery ->
                if (_session.value == null) return@collect
                when (recovery) {
                    JamMediaRecovery.RECOVERING -> {
                        wasRecovering = true
                        ++transitionGeneration
                        startJob?.cancel()
                        restorePlaybackSpeed()
                        setLocalPlayback(JamLocalPlayback.RECOVERING)
                    }
                    JamMediaRecovery.UNAVAILABLE -> {
                        wasRecovering = false
                        ++transitionGeneration
                        startJob?.cancel()
                        restorePlaybackSpeed()
                        setLocalPlayback(JamLocalPlayback.UNAVAILABLE)
                    }
                    JamMediaRecovery.NONE -> if (wasRecovering) {
                        wasRecovering = false
                        _session.value?.let { applyRemote(it.playback) }
                    }
                }
            }
        }
        // The rest of the app locks its player controls off this one flag, whatever kind of room it is.
        scope.launch {
            var before: Boolean? = null
            _session.map { session -> session?.let { !it.canControl } }.distinctUntilChanged().collect { locked ->
                bridge.setPartyLocked(locked == true)
                // A local pause only exists against a lock; once it lifts, the party owns the player again.
                if (locked != true) locallyPaused = false
                val message = when {
                    locked == true -> R.string.jam_host_only_toast
                    locked == false && before == true -> R.string.jam_controls_open_toast
                    else -> null
                }
                message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                before = locked
            }
        }
        scope.launch {
            bridge.playerConnection.collectLatest { connection ->
                listenedPlayer?.removeListener(playerListener)
                restorePlaybackSpeed()
                preparedForSession = false
                listenedPlayer = connection?.player?.also { it.addListener(playerListener) }
                _session.value?.let { applyRemote(it.playback) }
            }
        }
    }

    /** Sends the smallest queue action that makes the party's upcoming songs match this device's. */
    private fun sendQueue() {
        val player = listenedPlayer ?: return
        val session = _session.value ?: return
        if (!canSend() || player.mediaItemCount == 0) return
        val ids = playlistIds(player)
        val current = player.currentMediaItemIndex
        val queue = session.queue
        val partyUpcoming = queue.items.drop(queue.index + 1).map { it.videoId }
        val localUpcoming = ids.drop(current + 1).take(MAX_UPCOMING)
        val sameSong = queue.items.getOrNull(queue.index)?.videoId == ids[current]
        val change = if (sameSong) JamQueueDiff.between(partyUpcoming, localUpcoming) else QueueChange.Replace
        val first = queue.index + 1
        when (change) {
            QueueChange.None -> Unit
            QueueChange.Clear -> control("queueClear") {}
            is QueueChange.Add -> {
                val added = (current + 1 + change.localFrom until current + 1 + change.localTo)
                    .mapNotNull { toTrack(player.getMediaItemAt(it)) }
                if (added.isNotEmpty()) control("queueAdd") {
                    put("tracks", client.json.encodeToJsonElement(added))
                    put("playNext", change.playNext)
                }
            }
            is QueueChange.Remove -> control("queueRemove") { put("videoId", change.videoId) }
            is QueueChange.Move -> control("queueMove") {
                put("fromIndex", first + change.from)
                put("toIndex", first + change.to)
                put("videoId", change.videoId)
            }
            QueueChange.Replace -> {
                val start = maxOf(0, current - HISTORY_SENT)
                val window = (start until minOf(ids.size, current + 1 + MAX_UPCOMING))
                    .mapNotNull { index -> toTrack(player.getMediaItemAt(index))?.let { index to it } }
                val at = window.indexOfFirst { it.first == current }
                if (at >= 0) control("setQueue") {
                    put("queue", client.json.encodeToJsonElement(window.map { it.second }))
                    put("queueIndex", at)
                }
            }
        }
    }

    private fun canSend(): Boolean = _session.value?.let { it.connected && it.canControl } == true

    private fun control(action: String, build: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit) {
        if (_session.value?.connected != true) {
            _message.value = "Reconnecting. Try the control again once connected."
            return
        }
        val webSocket = socket ?: return
        intentQuietUntil = SystemClock.elapsedRealtime() + INTENT_QUIET_MS
        val frame = buildJsonObject {
            put("type", "control")
            put("action", action)
            build()
        }
        if (!client.send(webSocket, frame)) {
            resolvePendingControl()
            _message.value = "Control could not be sent. Reconnecting."
            webSocket.cancel()
            lost(webSocket)
            return
        }
        update { it.copy(controlPending = true) }
        pendingControlJob?.cancel()
        val generation = sessionGeneration
        pendingControlJob = scope.launch {
            delay(INTENT_QUIET_MS)
            if (generation != sessionGeneration) return@launch
            intentQuietUntil = 0L
            socket?.send("""{"type":"sync"}""")
            delay(3_000L)
            if (generation == sessionGeneration && _session.value?.controlPending == true) {
                update { it.copy(controlPending = false) }
                _message.value = "Could not confirm the control. Reconnecting to the party."
                socket?.let { it.cancel(); lost(it) }
            }
        }
    }

    private fun resolvePendingControl() {
        intentQuietUntil = 0L
        pendingControlJob?.cancel()
        pendingControlJob = null
        deferredPlayJob?.cancel()
        update { it.copy(controlPending = false) }
    }

    // ---- conversions -----------------------------------------------------------------------

    private fun playlistIds(player: Player): List<String> = (0 until player.mediaItemCount).map { player.getMediaItemAt(it).mediaId }

    private fun toTrack(item: MediaItem): PartyTrack? {
        val meta = item.metadata ?: return null
        // Files on this phone mean nothing to the other devices.
        if (meta.isLocalFile) return null
        return PartyTrack(
            videoId = meta.id,
            title = meta.title,
            artist = meta.artists.joinToString { it.name },
            thumbnailUrl = meta.thumbnailUrl,
            durationMs = meta.duration.takeIf { it > 0 }?.times(1000L),
        )
    }

    private fun toMediaItem(track: PartyTrack): MediaItem = MediaMetadata(
        id = track.videoId,
        title = track.title,
        artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
        duration = track.durationMs?.div(1000)?.toInt() ?: -1,
        thumbnailUrl = track.thumbnailUrl,
    ).toMediaItem()
}
