package com.convx.music.wear

import android.content.Context
import android.os.SystemClock
import com.convx.music.playback.watch.*
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import java.util.UUID

data class WearLinkState(val snapshot: WearSnapshot? = null, val connected: Boolean = false,
    val connecting: Boolean = true, val receivedAtMs: Long = 0)

class WearPhoneLink(context: Context) : DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener, CapabilityClient.OnCapabilityChangedListener {
    private val dataClient = Wearable.getDataClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val capabilities = Wearable.getCapabilityClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val mutable = MutableStateFlow(WearLinkState())
    val state = mutable.asStateFlow()
    private var node: String? = null
    private var listening: Job? = null
    private var syncId: String? = null
    private var confirmedSession: String? = null
    private var lastSync = 0L
    private val reconnectMutex = Mutex()

    fun start() {
        if (listening != null) return
        mutable.update { it.copy(connecting = true) }
        listening = scope.launch(start = CoroutineStart.LAZY) {
            try {
                dataClient.addListener(this@WearPhoneLink).await()
                messageClient.addListener(this@WearPhoneLink).await()
                capabilities.addListener(this@WearPhoneLink, WearProtocol.PHONE_CAPABILITY).await()
                while (isActive) {
                    val now = SystemClock.elapsedRealtime()
                    if (now - mutable.value.receivedAtMs > 12_000) mutable.update { it.copy(connected = false) }
                    if (now - lastSync > 5_000) reconnect()
                    delay(1_000)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(connecting = false, connected = false) } }
        }
        listening?.start()
    }

    fun stop() {
        listening?.cancel()
        listening = null
        dataClient.removeListener(this)
        messageClient.removeListener(this)
        capabilities.removeListener(this, WearProtocol.PHONE_CAPABILITY)
        node = null
        syncId = null
        confirmedSession = null
        lastSync = 0
        mutable.update { it.copy(connected = false, connecting = false) }
    }

    fun retry() { stop(); start() }

    private suspend fun reconnect() = reconnectMutex.withLock {
        try {
            lastSync = SystemClock.elapsedRealtime()
            val phones = capabilities.getCapability(WearProtocol.PHONE_CAPABILITY, CapabilityClient.FILTER_REACHABLE).await().nodes
            val phone = phones.sortedWith(compareByDescending<Node> { it.isNearby }.thenBy { it.id }).firstOrNull()
            if (phone == null) {
                node = null
                confirmedSession = null
                mutable.update { it.copy(connected = false, connecting = false) }
                return@withLock
            }
            if (node != phone.id) {
                node = phone.id
                confirmedSession = null
                mutable.value = WearLinkState(connecting = true)
                val cached = dataClient.dataItems.await()
                try {
                    cached.filter { it.uri.path == WearProtocol.STATE_PATH && it.uri.host == phone.id }.forEach {
                        DataMapItem.fromDataItem(it).dataMap.getByteArray(WearProtocol.STATE_KEY)?.let { bytes ->
                            WearProtocol.decodeState(bytes)?.let { snapshot -> mutable.value = WearLinkState(snapshot, connecting = true) }
                        }
                    }
                } finally { cached.release() }
            }
            val request = WearCommand(requestId = UUID.randomUUID().toString(), action = WearAction.SYNC)
            syncId = request.requestId
            messageClient.sendMessage(phone.id, WearProtocol.COMMAND_PATH,
                WearProtocol.json.encodeToString(request).encodeToByteArray()).await()
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { mutable.update { it.copy(connected = false, connecting = false) } }
    }

    fun send(action: WearAction, positionMs: Long? = null, volume: Float? = null,
        shuffle: Boolean? = null, repeatMode: com.convx.music.playback.PlaybackRepeatMode? = null) {
        val activityJob = listening ?: return
        val current = mutable.value
        val snapshot = current.snapshot ?: return
        val destination = node ?: return
        if (!current.connected || !snapshot.canControl) return
        val command = WearCommand(requestId = UUID.randomUUID().toString(), action = action,
            sessionId = snapshot.sessionId, mediaId = snapshot.mediaId, positionMs = positionMs,
            volume = volume, shuffle = shuffle, repeatMode = repeatMode)
        scope.launch(activityJob) {
            try {
                messageClient.sendMessage(destination, WearProtocol.COMMAND_PATH,
                    WearProtocol.json.encodeToString(command).encodeToByteArray()).await()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(connected = false) } }
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        val activityJob = listening ?: return
        if (event.path != WearProtocol.STATE_PATH) return
        val snapshot = WearProtocol.decodeState(event.data) ?: return
        scope.launch(activityJob) { accept(event.sourceNodeId, snapshot) }
    }

    override fun onDataChanged(events: DataEventBuffer) {
        val activityJob = listening ?: return
        events.filter { it.type == DataEvent.TYPE_CHANGED && it.dataItem.uri.path == WearProtocol.STATE_PATH }.forEach {
            val sender = it.dataItem.uri.host ?: return@forEach
            val bytes = DataMapItem.fromDataItem(it.dataItem).dataMap.getByteArray(WearProtocol.STATE_KEY) ?: return@forEach
            val snapshot = WearProtocol.decodeState(bytes) ?: return@forEach
            scope.launch(activityJob) { accept(sender, snapshot) }
        }
    }

    private fun accept(sender: String, snapshot: WearSnapshot) {
        if (listening == null || sender != node) return
        val confirmed = snapshot.replyTo != null && snapshot.replyTo == syncId
        if (confirmed) confirmedSession = snapshot.sessionId
        if (confirmedSession != snapshot.sessionId) return
        val previous = mutable.value.snapshot
        if (previous?.sessionId == snapshot.sessionId && snapshot.sequence <= previous.sequence) return
        mutable.value = WearLinkState(snapshot, connected = true, connecting = false, receivedAtMs = SystemClock.elapsedRealtime())
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        val activityJob = listening ?: return
        scope.launch(activityJob) { reconnect() }
    }
}
