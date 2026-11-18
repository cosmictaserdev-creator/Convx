package com.convx.music.client.desktop

import com.convx.music.playback.*
import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.util.concurrent.atomic.AtomicInteger
import javax.sound.sampled.*
import kotlin.test.*

/** Exercises the real controller, decoder and PCM seek path without opening a hardware device. */
class DesktopSponsorBlockTest {
    @get:Rule val temporary = TemporaryFolder()
    private val track = PlaybackRequestTrack("youtube_music", "abcdefghijk", "Fixture", emptyList(), 30_000)

    @Test fun anEnabledYoutubeSongSeeksPastItsSponsorThroughTheAudioEngine() = runBlocking {
        val lookedUp = CompletableDeferred<String>()
        withPlayer(PlaybackSettings(sponsorBlockEnabled = true), SponsorSegmentsSource { id, _ ->
            lookedUp.complete(id)
            listOf(SponsorSegment(0, 8_000, SponsorCategory.SPONSOR))
        }) { controller, _ ->
            controller.playTrack(track)
            assertEquals(track.id, withTimeout(5_000) { lookedUp.await() })
            val skipped = withTimeout(5_000) { controller.playbackSnapshot.first { it.positionMs >= 8_000 } }
            assertEquals(track.id, skipped.track?.id)
            assertNull(skipped.errorMessage)
        }
    }

    @Test fun localModeAndOtherProvidersNeverQuerySponsorBlock() = runBlocking {
        for ((settings, request) in listOf(
            PlaybackSettings(sponsorBlockEnabled = true, localMode = true) to track,
            PlaybackSettings(sponsorBlockEnabled = true) to track.copy(providerId = "local"),
            PlaybackSettings(sponsorBlockEnabled = false) to track,
        )) {
            val calls = AtomicInteger()
            withPlayer(settings, SponsorSegmentsSource { _, _ -> calls.incrementAndGet(); emptyList() }) { controller, _ ->
                controller.playTrack(request)
                withTimeout(5_000) { controller.playbackSnapshot.first { it.isPlaying && it.positionMs >= 700 } }
                assertEquals(0, calls.get())
            }
        }
    }

    @Test fun turningTheSettingOffCancelsAnInFlightLookupWhileMusicKeepsPlaying() = runBlocking {
        val requested = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        withPlayer(PlaybackSettings(sponsorBlockEnabled = true), SponsorSegmentsSource { _, _ ->
            requested.complete(Unit)
            try { awaitCancellation() } finally { cancelled.complete(Unit) }
        }) { controller, settings ->
            controller.playTrack(track)
            withTimeout(5_000) { requested.await() }
            settings.updateSettings { it.copy(sponsorBlockEnabled = false) }
            withTimeout(5_000) { cancelled.await() }
            val playing = withTimeout(5_000) { controller.playbackSnapshot.first { it.isPlaying && it.positionMs >= 300 } }
            assertTrue(playing.positionMs < 8_000)
            assertNull(playing.errorMessage)
        }
    }

    @Test fun anOutroSkipHonorsTheAfterThisTrackSleepTimer() = runBlocking {
        val allowLookup = CompletableDeferred<Unit>()
        withPlayer(PlaybackSettings(sponsorBlockEnabled = true), SponsorSegmentsSource { _, _ ->
            allowLookup.await()
            listOf(SponsorSegment(0, 35_000, SponsorCategory.SPONSOR))
        }) { controller, _ ->
            controller.playQueue(listOf(track, track.copy(id = "nextsong123")), 0)
            withTimeout(5_000) { controller.playbackSnapshot.first { it.isPlaying } }
            controller.sleepTimer.start(-1)
            allowLookup.complete(Unit)
            val ended = withTimeout(5_000) { controller.playbackSnapshot.first { it.phase == PlaybackPhase.ENDED } }
            assertEquals(track.id, ended.track?.id)
            assertFalse(ended.isPlaying)
            assertEquals(0, controller.queueSnapshot.value.currentIndex)
            assertFalse(controller.sleepTimer.state.value.isActive)
        }
    }

    private suspend fun withPlayer(
        initial: PlaybackSettings,
        source: SponsorSegmentsSource,
        test: suspend (DesktopPlaybackController, PlaybackSettingsRepository) -> Unit,
    ) {
        val wav = temporary.newFile().apply {
            val frames = 8_000L * 30
            AudioInputStream(ByteArrayInputStream(ByteArray(frames.toInt() * 2)), AudioFormat(8_000f, 16, 1, true, false), frames)
                .use { AudioSystem.write(it, AudioFileFormat.Type.WAVE, this) }
        }
        val settings = object : PlaybackSettingsRepository {
            val value = MutableStateFlow(initial)
            override fun observeSettings() = value
            override suspend fun getSettings() = value.value
            override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings {
                value.value = transform(value.value).normalized()
                return value.value
            }
        }
        val controller = DesktopPlaybackController(
            streamResolver = object : AudioStreamResolver {
                override suspend fun resolve(track: PlaybackRequestTrack) = Result.success(PlaybackAudioStream(wav.toURI().toString(), "audio/wav", 30_000, 128_000))
            },
            playbackSettingsRepository = settings,
            playbackCheckpointRepository = object : PlaybackCheckpointRepository {
                val value = MutableStateFlow(PlaybackCheckpoint())
                override fun observeCheckpoint() = value
                override suspend fun getCheckpoint() = value.value
                override suspend fun saveCheckpoint(checkpoint: PlaybackCheckpoint) { value.value = checkpoint }
            },
            trackAnalysisRepository = object : PlaybackTrackAnalysisRepository {
                override suspend fun get(trackIdentity: String): TrackAnalysis? = null
                override suspend fun save(trackIdentity: String, analysis: TrackAnalysis) = Unit
                override suspend fun remove(trackIdentity: String) = Unit
            },
            sponsorSegmentsSource = source,
            openAudioOutput = { format, _ -> object : DesktopPcmOutput {
                override val format = format
                override val description = PlaybackAudioOutput(format, format, PlaybackAudioOutputMode.SYSTEM_MIXER, "Test output", false)
                override val queuedFrames = 0
                override var playedFrames = 0L
                override fun write(bytes: ByteArray, offset: Int, length: Int): Int {
                    Thread.sleep(8)
                    playedFrames += length / format.frameSize
                    return length
                }
                override fun start() = Unit
                override fun pause() = Unit
                override fun flush() = Unit
                override fun close() = Unit
            } },
        )
        try { test(controller, settings) } finally { controller.close() }
    }
}
