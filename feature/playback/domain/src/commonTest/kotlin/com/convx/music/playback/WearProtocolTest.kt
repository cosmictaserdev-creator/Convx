package com.convx.music.playback

import com.convx.music.playback.watch.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlin.test.*

class WearProtocolTest {
    private val player = WatchPlayer()
    private var volume = .5f
    private val router = WearCommandRouter("session", player) { volume = it }
    private fun command(action: WearAction, id: String = "request") =
        WearCommand(requestId = id, action = action, sessionId = "session", mediaId = "track")

    @Test fun rejectsMalformedOversizedAndUnknownProtocolCommands() {
        assertNull(WearProtocol.decodeCommand("garbage".encodeToByteArray()))
        assertNull(WearProtocol.decodeCommand(ByteArray(WearProtocol.MAX_COMMAND_BYTES + 1)))
        assertNull(WearProtocol.decodeCommand(WearProtocol.json.encodeToString(command(WearAction.PLAY).copy(version = 9)).encodeToByteArray()))
        assertNull(WearProtocol.decodeCommand(WearProtocol.json.encodeToString(command(WearAction.PLAY).copy(requestId = "")).encodeToByteArray()))
        assertNull(WearProtocol.decodeState("{}".encodeToByteArray()))
    }

    @Test fun delayedCommandsCannotControlAnotherSessionOrTrack() {
        assertFalse(router.receive("watch", command(WearAction.PAUSE).copy(sessionId = "old"), true, true))
        assertFalse(router.receive("watch", command(WearAction.PAUSE).copy(mediaId = "old-track"), true, true))
        player.playbackSnapshot.value = PlaybackSnapshot.EMPTY
        assertFalse(router.receive("watch", command(WearAction.PLAY), true, true))
        assertTrue(player.events.isEmpty())
    }

    @Test fun duplicatePacketsAreIdempotentAndDistinctWatchesHaveIndependentRequests() {
        assertTrue(router.receive("watch", command(WearAction.NEXT), true, true))
        assertFalse(router.receive("watch", command(WearAction.NEXT), true, true))
        assertTrue(router.receive("other-watch", command(WearAction.NEXT), true, true))
        assertEquals(listOf("next", "next"), player.events)
    }

    @Test fun synchronizationIsAllowedWhileRoomControlsAndVolumeAreLocked() {
        assertTrue(router.receive("watch", command(WearAction.SYNC).copy(sessionId = null), false, false))
        assertFalse(router.receive("watch", command(WearAction.PLAY), false, true))
        assertFalse(router.receive("watch", command(WearAction.VOLUME).copy(volume = 1f), true, false))
        assertEquals(.5f, volume)
        assertTrue(player.events.isEmpty())
    }

    @Test fun seekAndVolumeRespectBoundsAndMissingValuesDoNothing() {
        assertFalse(router.receive("watch", command(WearAction.SEEK), true, true))
        assertTrue(router.receive("watch", command(WearAction.SEEK, "seek").copy(positionMs = Long.MAX_VALUE), true, true))
        assertEquals(listOf("seek:180000"), player.events)
        assertFalse(router.receive("watch", command(WearAction.VOLUME).copy(volume = Float.NaN), true, true))
        assertTrue(router.receive("watch", command(WearAction.VOLUME, "volume").copy(volume = 4f), true, true))
        assertEquals(1f, volume)
        player.playbackSnapshot.value = player.playbackSnapshot.value.copy(durationMs = null, canSkipNext = false)
        assertFalse(router.receive("watch", command(WearAction.SEEK, "seek-unknown").copy(positionMs = 1), true, true))
        assertFalse(router.receive("watch", command(WearAction.NEXT), true, true))
    }

    @Test fun snapshotRoundTripPreservesUnicodeWordTimingAndOffsets() {
        val snapshot = WearSnapshot(sessionId = "session", sequence = 2, mediaId = "track", lyricsOffsetMs = 500,
            title = "नमस्ते 世界", lyrics = listOf(WearLyricLine(1_000, "Hello world", listOf(WearWord(1_000, 1_450, "Hello"), WearWord(1_500, 2_000, "world")))))
        assertEquals(snapshot, WearProtocol.decodeState(WearProtocol.encodeState(snapshot)))
        assertEquals(-1, snapshot.activeLyric(0))
        assertEquals(0, snapshot.activeLyric(500))
    }

    @Test fun oversizedLyricsStayInsideTheTransportBudget() {
        val snapshot = WearSnapshot(sessionId = "session", sequence = 1, title = "x".repeat(4_000),
            lyrics = List(100) { WearLyricLine(it * 1_000L, "歌".repeat(4_000),
                List(150) { WearWord(0, 1, "word".repeat(100)) }) })
        val bytes = WearProtocol.encodeState(snapshot)
        assertTrue(bytes.size <= WearProtocol.MAX_STATE_BYTES)
        val decoded = assertNotNull(WearProtocol.decodeState(bytes))
        assertEquals(256, decoded.title.length)
        assertTrue(decoded.lyrics.size < snapshot.lyrics.size)
    }

    @Test fun playheadInterpolationStopsAtDurationAndCannotRunAwayAfterDisconnect() {
        val playing = WearSnapshot(sessionId = "session", sequence = 1, positionMs = 1_000, durationMs = 10_000, isPlaying = true, speed = 2f)
        assertEquals(3_000, playing.positionAfter(1_000))
        assertEquals(10_000, playing.positionAfter(Long.MAX_VALUE))
        assertEquals(1_000, playing.positionAfter(-5_000))
        assertEquals(1_000, playing.copy(isPlaying = false).positionAfter(5_000))
        assertEquals(1_000, playing.copy(speed = Float.NaN).positionAfter(5_000))
    }

    private class WatchPlayer : PlaybackController {
        override val playbackSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY.copy(
            track = PlaybackTrackSnapshot("track", "Song", listOf("Artist")), durationMs = 180_000,
            canSkipNext = true, canSkipPrevious = true))
        override val queueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
        val events = mutableListOf<String>()
        override fun play() { events += "play" }
        override fun pause() { events += "pause" }
        override fun togglePlayPause() { events += "toggle" }
        override fun seekTo(positionMs: Long) { events += "seek:$positionMs" }
        override fun requestSkip(forward: Boolean) { events += if (forward) "next" else "previous" }
        override fun setRepeatMode(mode: PlaybackRepeatMode) { events += "repeat:$mode" }
        override fun setShuffleEnabled(enabled: Boolean) { events += "shuffle:$enabled" }
        override fun playQueueItem(index: Int) = Unit
        override fun moveQueueItem(fromIndex: Int, toIndex: Int) = Unit
        override fun removeQueueItem(index: Int) = Unit
        override fun removeQueueItemsAfterCurrent() = Unit
    }
}
