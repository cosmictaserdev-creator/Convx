/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.listentogether

import com.google.protobuf.MessageLite
import com.nexamusic.app.listentogether.proto.ListenTogether
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Message format for encoding/decoding
 */
enum class MessageFormat {
    JSON,      // DEPRECATED - will be removed in future versions
    PROTOBUF
}

/**
 * Codec for encoding and decoding messages in different formats
 */
class MessageCodec(
    var format: MessageFormat = MessageFormat.JSON,
    var compressionEnabled: Boolean = false
) {
    companion object {
        private const val TAG = "MessageCodec"
        private const val COMPRESSION_THRESHOLD = 100 // Only compress if > 100 bytes
        
        /**
         * Detect message format by inspecting first byte
         */
        fun detectMessageFormat(data: ByteArray): MessageFormat {
            if (data.isEmpty()) return MessageFormat.JSON
            // JSON messages start with '{'
            if (data[0] == '{'.code.toByte()) return MessageFormat.JSON
            // Protobuf messages have field tags
            return MessageFormat.PROTOBUF
        }
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    /**
     * Encode a message with the codec's format and compression settings
     */
    fun encode(msgType: String, payload: Any?): ByteArray {
        return if (format == MessageFormat.PROTOBUF) {
            encodeProtobuf(msgType, payload)
        } else {
            encodeJson(msgType, payload)
        }
    }
    
    /**
     * Decode a message, automatically detecting format
     */
    fun decode(data: ByteArray): Pair<String, ByteArray> {
        val detectedFormat = detectMessageFormat(data)
        
        return if (detectedFormat == MessageFormat.PROTOBUF) {
            decodeProtobuf(data)
        } else {
            decodeJson(data)
        }
    }
    
    /**
     * Encode message as JSON (DEPRECATED - will be removed in future versions)
     */
    private fun encodeJson(msgType: String, payload: Any?): ByteArray {
        val msg = Message(
            type = msgType,
            payload = if (payload != null) json.encodeToJsonElement(serializer(payload), payload) else null
        )
        
        var data = json.encodeToString(msg).toByteArray()
        
        if (compressionEnabled && data.size > COMPRESSION_THRESHOLD) {
            val compressed = compressData(data)
            if (compressed.size < data.size) {
                data = compressed
            }
        }
        
        return data
    }
    
    /**
     * Decode JSON message (DEPRECATED - will be removed in future versions)
     */
    private fun decodeJson(data: ByteArray): Pair<String, ByteArray> {
        // Try to decompress if it looks compressed (gzip magic bytes)
        val actualData = if (compressionEnabled && data.size > 2 && 
                             data[0] == 0x1f.toByte() && data[1] == 0x8b.toByte()) {
            decompressData(data) ?: data
        } else {
            data
        }
        
        val msg = json.decodeFromString<Message>(actualData.decodeToString())
        val payloadBytes = msg.payload?.toString()?.toByteArray() ?: byteArrayOf()
        
        return Pair(msg.type, payloadBytes)
    }
    
    /**
     * Encode message using Protocol Buffers
     */
    private fun encodeProtobuf(msgType: String, payload: Any?): ByteArray {
        var payloadBytes = byteArrayOf()
        var compressed = false
        
        if (payload != null) {
            val protoMsg = toProtoMessage(payload)
            payloadBytes = protoMsg.toByteArray()
            
            // Compress if enabled and payload is large enough
            if (compressionEnabled && payloadBytes.size > COMPRESSION_THRESHOLD) {
                val compressedBytes = compressData(payloadBytes)
                if (compressedBytes.size < payloadBytes.size) {
                    payloadBytes = compressedBytes
                    compressed = true
                }
            }
        }
        
        val envelope = ListenTogether.Envelope.newBuilder()
            .setType(msgType)
            .setPayload(com.google.protobuf.ByteString.copyFrom(payloadBytes))
            .setCompressed(compressed)
            .build()
        
        return envelope.toByteArray()
    }
    
    /**
     * Decode protobuf message
     */
    private fun decodeProtobuf(data: ByteArray): Pair<String, ByteArray> {
        val envelope = ListenTogether.Envelope.parseFrom(data)
        
        var payloadBytes = envelope.payload.toByteArray()
        
        if (envelope.compressed) {
            payloadBytes = decompressData(payloadBytes) ?: payloadBytes
        }
        
        return Pair(envelope.type, payloadBytes)
    }
    
    /**
     * Compress data using GZIP
     */
    private fun compressData(data: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { gzip ->
            gzip.write(data)
        }
        return outputStream.toByteArray()
    }
    
    /**
     * Decompress GZIP data
     */
    private fun decompressData(data: ByteArray): ByteArray? {
        return try {
            val inputStream = ByteArrayInputStream(data)
            GZIPInputStream(inputStream).use { gzip ->
                gzip.readBytes()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to decompress data")
            null
        }
    }
    
    /**
     * Convert Kotlin objects to protobuf messages
     */
    private fun toProtoMessage(payload: Any): MessageLite {
        return when (payload) {
            is CreateRoomPayload -> ListenTogether.CreateRoomPayload.newBuilder()
                .setUsername(payload.username)
                .build()
            is JoinRoomPayload -> ListenTogether.JoinRoomPayload.newBuilder()
                .setRoomCode(payload.roomCode)
                .setUsername(payload.username)
                .build()
            is ApproveJoinPayload -> ListenTogether.ApproveJoinPayload.newBuilder()
                .setUserId(payload.userId)
                .build()
            is RejectJoinPayload -> ListenTogether.RejectJoinPayload.newBuilder()
                .setUserId(payload.userId)
                .setReason(payload.reason ?: "")
                .build()
            is PlaybackActionPayload -> {
                val builder = ListenTogether.PlaybackActionPayload.newBuilder()
                    .setAction(payload.action)
                    .setPosition(payload.position ?: 0)
                    .setInsertNext(payload.insertNext ?: false)
                    .setVolume(payload.volume ?: 1f)
                    .setServerTime(payload.serverTime ?: 0)
                
                payload.trackId?.let { builder.setTrackId(it) }
                payload.trackInfo?.let { builder.setTrackInfo(trackInfoToProto(it)) }
                payload.queueTitle?.let { builder.setQueueTitle(it) }
                payload.queue?.forEach { track ->
                    builder.addQueue(trackInfoToProto(track))
                }
                
                builder.build()
            }
            is BufferReadyPayload -> ListenTogether.BufferReadyPayload.newBuilder()
                .setTrackId(payload.trackId)
                .build()
            is KickUserPayload -> ListenTogether.KickUserPayload.newBuilder()
                .setUserId(payload.userId)
                .setReason(payload.reason ?: "")
                .build()
            is SuggestTrackPayload -> {
                val builder = ListenTogether.SuggestTrackPayload.newBuilder()
                payload.trackInfo.let { builder.setTrackInfo(trackInfoToProto(it)) }
                builder.build()
            }
            is ApproveSuggestionPayload -> ListenTogether.ApproveSuggestionPayload.newBuilder()
                .setSuggestionId(payload.suggestionId)
                .build()
            is RejectSuggestionPayload -> ListenTogether.RejectSuggestionPayload.newBuilder()
                .setSuggestionId(payload.suggestionId)
                .setReason(payload.reason ?: "")
                .build()
            is ReconnectPayload -> ListenTogether.ReconnectPayload.newBuilder()
                .setSessionToken(payload.sessionToken)
                .build()
            is TransferHostPayload -> ListenTogether.TransferHostPayload.newBuilder()
                .setNewHostId(payload.newHostId)
                .build()
            else -> throw IllegalArgumentException("Unsupported payload type: ${payload::class.simpleName}")
        }
    }
    
    /**
     * Decode protobuf payload to Kotlin objects
     */
    fun decodePayload(msgType: String, payloadBytes: ByteArray, format: MessageFormat): Any? {
        if (payloadBytes.isEmpty()) return null
        
        return if (format == MessageFormat.PROTOBUF) {
            decodeProtobufPayload(msgType, payloadBytes)
        } else {
            decodeJsonPayload(msgType, payloadBytes)
        }
    }
    
    /**
     * Decode JSON payload (DEPRECATED - will be removed in future versions)
     */
    private fun decodeJsonPayload(msgType: String, payloadBytes: ByteArray): Any? {
        val payloadString = payloadBytes.decodeToString()
        
        return when (msgType) {
            MessageTypes.ROOM_CREATED -> json.decodeFromString<RoomCreatedPayload>(payloadString)
            MessageTypes.JOIN_REQUEST -> json.decodeFromString<JoinRequestPayload>(payloadString)
            MessageTypes.JOIN_APPROVED -> json.decodeFromString<JoinApprovedPayload>(payloadString)
            MessageTypes.JOIN_REJECTED -> json.decodeFromString<JoinRejectedPayload>(payloadString)
            MessageTypes.USER_JOINED -> json.decodeFromString<UserJoinedPayload>(payloadString)
            MessageTypes.USER_LEFT -> json.decodeFromString<UserLeftPayload>(payloadString)
            MessageTypes.SYNC_PLAYBACK -> json.decodeFromString<PlaybackActionPayload>(payloadString)
            MessageTypes.BUFFER_WAIT -> json.decodeFromString<BufferWaitPayload>(payloadString)
            MessageTypes.BUFFER_COMPLETE -> json.decodeFromString<BufferCompletePayload>(payloadString)
            MessageTypes.ERROR -> json.decodeFromString<ErrorPayload>(payloadString)
            MessageTypes.HOST_CHANGED -> json.decodeFromString<HostChangedPayload>(payloadString)
            MessageTypes.KICKED -> json.decodeFromString<KickedPayload>(payloadString)
            MessageTypes.SYNC_STATE -> json.decodeFromString<SyncStatePayload>(payloadString)
            MessageTypes.RECONNECTED -> json.decodeFromString<ReconnectedPayload>(payloadString)
            MessageTypes.USER_RECONNECTED -> json.decodeFromString<UserReconnectedPayload>(payloadString)
            MessageTypes.USER_DISCONNECTED -> json.decodeFromString<UserDisconnectedPayload>(payloadString)
            MessageTypes.SUGGESTION_RECEIVED -> json.decodeFromString<SuggestionReceivedPayload>(payloadString)
            MessageTypes.SUGGESTION_APPROVED -> json.decodeFromString<SuggestionApprovedPayload>(payloadString)
            MessageTypes.SUGGESTION_REJECTED -> json.decodeFromString<SuggestionRejectedPayload>(payloadString)
            MessageTypes.CHAT -> json.decodeFromString<ChatMessagePayload>(payloadString)
            MessageTypes.CONTROL_MODE_CHANGED -> json.decodeFromString<ControlModeChangedPayload>(payloadString)
            MessageTypes.ROOM_EXPIRING -> json.decodeFromString<RoomExpiringPayload>(payloadString)
            MessageTypes.ROOM_CLOSED -> json.decodeFromString<RoomClosedPayload>(payloadString)
            else -> null
        }
    }
    
    /**
     * Decode protobuf payload
     */
    private fun decodeProtobufPayload(msgType: String, payloadBytes: ByteArray): Any? {
        return when (msgType) {
            MessageTypes.ROOM_CREATED -> {
                val pb = ListenTogether.RoomCreatedPayload.parseFrom(payloadBytes)
                RoomCreatedPayload(pb.roomCode, pb.userId, pb.sessionToken)
            }
            MessageTypes.JOIN_REQUEST -> {
                val pb = ListenTogether.JoinRequestPayload.parseFrom(payloadBytes)
                JoinRequestPayload(pb.userId, pb.username)
            }
            MessageTypes.JOIN_APPROVED -> {
                val pb = ListenTogether.JoinApprovedPayload.parseFrom(payloadBytes)
                JoinApprovedPayload(
                    pb.roomCode,
                    pb.userId,
                    pb.sessionToken,
                    protoToRoomState(pb.state)
                )
            }
            MessageTypes.JOIN_REJECTED -> {
                val pb = ListenTogether.JoinRejectedPayload.parseFrom(payloadBytes)
                JoinRejectedPayload(pb.reason)
            }
            MessageTypes.USER_JOINED -> {
                val pb = ListenTogether.UserJoinedPayload.parseFrom(payloadBytes)
                UserJoinedPayload(pb.userId, pb.username)
            }
            MessageTypes.USER_LEFT -> {
                val pb = ListenTogether.UserLeftPayload.parseFrom(payloadBytes)
                UserLeftPayload(pb.userId, pb.username)
            }
            MessageTypes.SYNC_PLAYBACK -> {
                val pb = ListenTogether.PlaybackActionPayload.parseFrom(payloadBytes)
                PlaybackActionPayload(
                    action = pb.action,
                    trackId = pb.trackId.let { if (it.isEmpty()) null else it },
                    position = pb.position.let { if (it <= 0) null else it },
                    trackInfo = if (pb.hasTrackInfo()) protoToTrackInfo(pb.trackInfo) else null,
                    insertNext = pb.insertNext,
                    queue = pb.queueList.map { protoToTrackInfo(it) },
                    queueTitle = pb.queueTitle.let { if (it.isEmpty()) null else it },
                    volume = pb.volume.takeIf { it > 0 },
                    serverTime = pb.serverTime.takeIf { it > 0 }
                )
            }
            MessageTypes.BUFFER_WAIT -> {
                val pb = ListenTogether.BufferWaitPayload.parseFrom(payloadBytes)
                BufferWaitPayload(pb.trackId, pb.waitingForList)
            }
            MessageTypes.BUFFER_COMPLETE -> {
                val pb = ListenTogether.BufferCompletePayload.parseFrom(payloadBytes)
                BufferCompletePayload(pb.trackId)
            }
            MessageTypes.ERROR -> {
                val pb = ListenTogether.ErrorPayload.parseFrom(payloadBytes)
                ErrorPayload(pb.code.toString(), pb.message)
            }
            MessageTypes.HOST_CHANGED -> {
                val pb = ListenTogether.HostChangedPayload.parseFrom(payloadBytes)
                HostChangedPayload(pb.newHostId, pb.newHostName)
            }
            MessageTypes.KICKED -> {
                val pb = ListenTogether.KickedPayload.parseFrom(payloadBytes)
                KickedPayload(pb.reason)
            }
            MessageTypes.SYNC_STATE -> {
                val pb = ListenTogether.SyncStatePayload.parseFrom(payloadBytes)
                SyncStatePayload(
                    currentTrack = if (pb.hasCurrentTrack()) protoToTrackInfo(pb.currentTrack) else null,
                    isPlaying = pb.isPlaying,
                    position = pb.position,
                    lastUpdate = pb.lastUpdate,
                    queue = pb.queueList.map { protoToTrackInfo(it) },
                    volume = pb.volume.takeIf { it > 0 }
                )
            }
            MessageTypes.RECONNECTED -> {
                val pb = ListenTogether.ReconnectedPayload.parseFrom(payloadBytes)
                ReconnectedPayload(
                    pb.roomCode,
                    pb.userId,
                    protoToRoomState(pb.state),
                    pb.isHost
                )
            }
            MessageTypes.USER_RECONNECTED -> {
                val pb = ListenTogether.UserReconnectedPayload.parseFrom(payloadBytes)
                UserReconnectedPayload(pb.userId, pb.username)
            }
            MessageTypes.USER_DISCONNECTED -> {
                val pb = ListenTogether.UserDisconnectedPayload.parseFrom(payloadBytes)
                UserDisconnectedPayload(pb.userId, pb.username)
            }
            MessageTypes.SUGGESTION_RECEIVED -> {
                val pb = ListenTogether.SuggestionReceivedPayload.parseFrom(payloadBytes)
                SuggestionReceivedPayload(
                    pb.suggestionId,
                    pb.fromUserId,
                    pb.fromUsername,
                    protoToTrackInfo(pb.trackInfo)
                )
            }
            MessageTypes.SUGGESTION_APPROVED -> {
                val pb = ListenTogether.SuggestionApprovedPayload.parseFrom(payloadBytes)
                SuggestionApprovedPayload(
                    pb.suggestionId,
                    protoToTrackInfo(pb.trackInfo)
                )
            }
            MessageTypes.SUGGESTION_REJECTED -> {
                val pb = ListenTogether.SuggestionRejectedPayload.parseFrom(payloadBytes)
                SuggestionRejectedPayload(pb.suggestionId, pb.reason.let { if (it.isEmpty()) null else it })
            }
            else -> null
        }
    }
    
    // Helper conversion functions
    
    private fun trackInfoToProto(track: TrackInfo): ListenTogether.TrackInfo {
        return ListenTogether.TrackInfo.newBuilder()
            .setId(track.id)
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbum(track.album ?: "")
            .setDuration(track.duration)
            .setThumbnail(track.thumbnail ?: "")
            .setSuggestedBy(track.suggestedBy ?: "")
            .build()
    }
    
    private fun protoToTrackInfo(proto: ListenTogether.TrackInfo): TrackInfo {
        return TrackInfo(
            id = proto.id,
            title = proto.title,
            artist = proto.artist,
            album = proto.album.let { if (it.isEmpty()) null else it },
            duration = proto.duration,
            thumbnail = proto.thumbnail.let { if (it.isEmpty()) null else it },
            suggestedBy = proto.suggestedBy.let { if (it.isEmpty()) null else it }
        )
    }
    
    private fun protoToUserInfo(proto: ListenTogether.UserInfo): UserInfo {
        return UserInfo(
            userId = proto.userId,
            username = proto.username,
            isHost = proto.isHost,
            isConnected = proto.isConnected
        )
    }
    
    private fun protoToRoomState(proto: ListenTogether.RoomState): RoomState {
        return RoomState(
            roomCode = proto.roomCode,
            hostId = proto.hostId,
            users = proto.usersList.map { protoToUserInfo(it) },
            currentTrack = if (proto.hasCurrentTrack()) protoToTrackInfo(proto.currentTrack) else null,
            isPlaying = proto.isPlaying,
            position = proto.position,
            lastUpdate = proto.lastUpdate,
            volume = proto.volume,
            queue = proto.queueList.map { protoToTrackInfo(it) }
        )
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T> serializer(value: T): kotlinx.serialization.KSerializer<T> {
        return when (value) {
            is CreateRoomPayload -> CreateRoomPayload.serializer()
            is JoinRoomPayload -> JoinRoomPayload.serializer()
            is ApproveJoinPayload -> ApproveJoinPayload.serializer()
            is RejectJoinPayload -> RejectJoinPayload.serializer()
            is PlaybackActionPayload -> PlaybackActionPayload.serializer()
            is BufferReadyPayload -> BufferReadyPayload.serializer()
            is KickUserPayload -> KickUserPayload.serializer()
            is SuggestTrackPayload -> SuggestTrackPayload.serializer()
            is ApproveSuggestionPayload -> ApproveSuggestionPayload.serializer()
            is RejectSuggestionPayload -> RejectSuggestionPayload.serializer()
            is ReconnectPayload -> ReconnectPayload.serializer()
            is TransferHostPayload -> TransferHostPayload.serializer()
            is ChatPayload -> ChatPayload.serializer()
            // Outbound v2 verbs. Adding a payload to decodePayload alone is not
            // enough — anything the client SENDS also needs a branch here, or it
            // throws at encode time and the action silently never leaves.
            is SetControlModePayload -> SetControlModePayload.serializer()
            else -> throw IllegalArgumentException("Unknown type: ${value!!::class.simpleName}")
        } as kotlinx.serialization.KSerializer<T>
    }
}
