package com.convx.music.client.desktop

import com.convx.music.playback.*
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

/** One audio thread owns PCM readers, device interfaces, processing state, and deck handoffs. */
internal class DesktopStreamPlayer(
    private val onReady: (trackId: String, durationMs: Long?) -> Unit,
    private val onProgress: (trackId: String, positionMs: Long, durationMs: Long?) -> Unit,
    private val onStalled: (trackId: String) -> Unit,
    private val onPlaying: (trackId: String) -> Unit,
    private val onEnded: (trackId: String) -> Unit,
    private val onError: (trackId: String, message: String) -> Unit,
    private val onOutputChanged: (PlaybackAudioOutput?) -> Unit = {},
    private val releaseOwnedFile: (Path) -> Unit = { Files.deleteIfExists(it); Unit },
    private val openOutput: (PcmFormat, PlaybackSettings) -> DesktopPcmOutput = DesktopPcmOutputs::open,
) {
    private val commands = LinkedBlockingQueue<() -> Unit>()
    private val commandLock = Any()
    private val closing = AtomicBoolean(false)
    private var running = true
    private var active: DesktopPcmFile? = null
    private var prepared: DesktopPcmFile? = null
    private var outgoing: DesktopPcmFile? = null
    private var output: DesktopPcmOutput? = null
    private var playing = false
    private var reachedEnd = false
    private var settings = PlaybackSettings()
    private var volume = 1.0
    private var fadeFrames = 0L
    private var fadePosition = 0L
    private var pending = ByteArray(0)
    private var pendingOffset = 0
    private var pendingSize = 0
    private var samples = DoubleArray(0)
    private var outgoingSamples = DoubleArray(0)
    private var filters: List<List<EqualizerBiquadFilter>> = emptyList()
    private var equalizerKey: Pair<PcmFormat, EqualizerProfile>? = null
    private var lastOutput: PlaybackAudioOutput? = null
    private var lastProgressAt = 0L
    private var needsNativeFormat = false
    private var lastWriteAt = 0L
    private var stallReported = false
    private val worker = Thread(::run, "convx-pcm-output").apply { isDaemon = true; start() }

    fun setVolume(value: Float) = enqueue { if (!settings.bitPerfectEnabled) volume = value.coerceIn(0f, 1f).toDouble() }

    fun updatePlaybackSettings(value: PlaybackSettings) = enqueue {
        val next = value.normalized()
        val reconfigure = settings.bitPerfectEnabled != next.bitPerfectEnabled || settings.audioOutputDeviceId != next.audioOutputDeviceId
        if (settings.volume != next.volume) volume = next.volume.toDouble()
        settings = next
        if (reconfigure) {
            finishFade()
            prepared?.close(); prepared = null
            active?.let { deck ->
                val position = audiblePosition(deck)
                closeOutput()
                deck.rate = 1.0
                deck.seek(position)
                configureOutput(deck)
                if (playing) output?.start()
            }
        }
        publishOutput()
    }

    fun play(trackId: String, sourceUri: String, ownedAudioFile: Path? = null, initialPositionMs: Long = 0L,
             shouldStartPlayback: () -> Boolean) {
        enqueue(rejected = { ownedAudioFile?.let(releaseOwnedFile) }) {
            closeDecks()
            val deck = try { DesktopPcmFile(trackId, File(URI(sourceUri)), ownedAudioFile, releaseOwnedFile) }
            catch (failure: Exception) { ownedAudioFile?.let(releaseOwnedFile); onError(trackId, failure.message ?: "Couldn't open the decoded audio."); return@enqueue }
            active = deck
            deck.seek(initialPositionMs)
            configureOutput(deck)
            playing = shouldStartPlayback()
            reachedEnd = false
            onReady(trackId, deck.durationMs)
            if (playing) { output!!.start(); onPlaying(trackId) }
        }
    }

    fun prepare(trackId: String, sourceUri: String, ownedAudioFile: Path? = null, initialPositionMs: Long = 0L, playbackRate: Double = 1.0) {
        enqueue(rejected = { ownedAudioFile?.let(releaseOwnedFile) }) {
            prepared?.close(); prepared = null
            if (settings.bitPerfectEnabled) { ownedAudioFile?.let(releaseOwnedFile); return@enqueue }
            try {
                val deck = DesktopPcmFile(trackId, File(URI(sourceUri)), ownedAudioFile, releaseOwnedFile)
                prepared = deck
                deck.seek(initialPositionMs)
                deck.rate = playbackRate.coerceIn(.9, 1.1)
            } catch (_: Exception) {
                if (prepared == null) ownedAudioFile?.let(releaseOwnedFile)
                else prepared?.close()
                prepared = null
            }
        }
    }

    fun crossfadeToPrepared(outgoingTrackId: String, incomingTrackId: String, durationMs: Long, onHandoff: () -> Boolean) = enqueue {
        val previous = active ?: return@enqueue
        val incoming = prepared ?: return@enqueue
        val sink = output ?: return@enqueue
        if (settings.bitPerfectEnabled || outgoing != null || previous.trackId != outgoingTrackId || incoming.trackId != incomingTrackId) return@enqueue
        if (!onHandoff()) return@enqueue
        outgoing = previous
        active = incoming
        prepared = null
        fadeFrames = (durationMs.coerceAtLeast(1) * sink.format.sampleRate / 1_000).coerceAtLeast(1)
        fadePosition = 0
        reachedEnd = false
        publishOutput()
    }

    fun clearPrepared() = enqueue { prepared?.close(); prepared = null }
    fun play() = enqueue {
        active?.let { playing = true; output?.start(); onPlaying(it.trackId) }
    }
    fun pause() = enqueue { playing = false; output?.pause(); publishProgress(force = true) }
    fun seekTo(positionMs: Long) = enqueue { seek(positionMs, resume = playing) }
    fun replay() = enqueue { seek(0, resume = true) }

    fun close(onClosed: () -> Unit = {}) {
        synchronized(commandLock) {
            if (!closing.compareAndSet(false, true)) return
            commands.offer {
                try { closeDecks() } finally { running = false; onClosed() }
            }
        }
    }

    private fun enqueue(rejected: () -> Unit = {}, action: () -> Unit) {
        synchronized(commandLock) {
            if (closing.get()) { rejected(); return }
            commands.offer(action)
        }
    }

    private fun run() {
        while (running) {
            try {
                val command = commands.poll(if (playing) 2 else 25, TimeUnit.MILLISECONDS)
                command?.invoke()
                if (!running) break
                if (playing && active != null) tick()
            } catch (failure: Exception) { fail(failure.message ?: "The audio output stopped unexpectedly.") }
            catch (_: LinkageError) { fail("The native audio output isn't available on this platform.") }
        }
    }

    private fun fail(message: String) {
        val trackId = active?.trackId
        playing = false
        closeDecks()
        trackId?.let { onError(it, message) }
    }

    private fun configureOutput(deck: DesktopPcmFile) {
        closeOutput()
        output = openOutput(deck.format, settings)
        val format = output!!.format
        pending = ByteArray((format.sampleRate / 100).coerceAtLeast(1) * format.frameSize)
        samples = DoubleArray(pending.size / format.bytesPerSample)
        outgoingSamples = DoubleArray(samples.size)
        equalizerKey = null
        needsNativeFormat = false
        lastWriteAt = System.nanoTime()
        stallReported = false
        publishOutput()
    }

    private fun tick() {
        val deck = active ?: return
        val sink = output ?: return
        if (pendingOffset < pendingSize) {
            val written = sink.write(pending, pendingOffset, pendingSize - pendingOffset)
            pendingOffset += written
            if (written > 0) {
                lastWriteAt = System.nanoTime()
                if (stallReported) onPlaying(deck.trackId)
                stallReported = false
            }
            else if (!stallReported && System.nanoTime() - lastWriteAt > 3_000_000_000L) { stallReported = true; onStalled(deck.trackId) }
        } else if (needsNativeFormat) {
            if (sink.queuedFrames == 0) {
                configureOutput(deck)
                output!!.start()
            }
        } else if (reachedEnd) {
            if (sink.queuedFrames == 0) {
                playing = false
                sink.pause()
                onProgress(deck.trackId, deck.durationMs, deck.durationMs)
                onEnded(deck.trackId)
            }
        } else {
            pendingOffset = 0
            pendingSize = readChunk(deck, sink.format)
            if (pendingSize == 0) reachedEnd = true
        }
        publishProgress()
    }

    private fun readChunk(deck: DesktopPcmFile, format: PcmFormat): Int {
        val maxFrames = pending.size / format.frameSize
        val equalizer = settings.equalizerEnabled && !settings.bitPerfectEnabled
        val gain = if (settings.bitPerfectEnabled) 1.0 else volume
        val previous = outgoing
        val direct = previous == null && deck.rate == 1.0 && deck.format == format
        if (direct && !equalizer && gain == 1.0) return deck.readDirect(pending, maxFrames)
        val frames: Int
        if (direct) {
            val read = deck.readDirect(pending, maxFrames)
            frames = read / format.frameSize
            repeat(frames * format.channels) { index -> samples[index] = PcmSamples.read(pending, index * format.bytesPerSample, format) }
        } else frames = deck.readProcessed(samples, maxFrames, format)
        if (previous != null) {
            val previousFrames = previous.readProcessed(outgoingSamples, frames, format)
            for (frame in 0 until frames) {
                val progress = ((fadePosition + frame).toFloat() / fadeFrames).coerceIn(0f, 1f)
                val incomingGain = CrossfadeEnvelope.incomingGain(progress)
                val outgoingGain = CrossfadeEnvelope.outgoingGain(progress)
                for (channel in 0 until format.channels) {
                    val index = frame * format.channels + channel
                    samples[index] = samples[index] * incomingGain + if (frame < previousFrames) outgoingSamples[index] * outgoingGain else 0.0
                }
            }
            fadePosition += frames
            if (fadePosition >= fadeFrames) finishFade()
        }
        if (equalizer) prepareEqualizer(format)
        val preamp = if (equalizer) 10.0.pow(settings.equalizerProfile.preamp / 20) else 1.0
        for (frame in 0 until frames) for (channel in 0 until format.channels) {
            val index = frame * format.channels + channel
            var sample = samples[index]
            if (equalizer) for (filter in filters[channel]) sample = filter.processSample(sample)
            PcmSamples.write(pending, index * format.bytesPerSample, sample * preamp * gain, format)
        }
        return frames * format.frameSize
    }

    private fun prepareEqualizer(format: PcmFormat) {
        val key = format to settings.equalizerProfile
        if (equalizerKey == key) return
        equalizerKey = key
        filters = List(format.channels) {
            settings.equalizerProfile.bands.filter { it.enabled && it.frequency < format.sampleRate / 2.0 }.map { band ->
                EqualizerBiquadFilter(format.sampleRate, band.frequency, band.gain, band.q, band.filterType)
            }
        }
    }

    private fun finishFade() {
        outgoing?.close(); outgoing = null
        active?.let { deck ->
            deck.rate = 1.0
            needsNativeFormat = output?.format != deck.format
        }
        publishOutput()
    }

    private fun audiblePosition(deck: DesktopPcmFile): Long {
        val sink = output ?: return (deck.positionFrames * 1_000 / deck.format.sampleRate).toLong()
        val waiting = sink.queuedFrames.toDouble() * deck.format.sampleRate / sink.format.sampleRate * deck.rate +
            (pendingSize - pendingOffset).coerceAtLeast(0).toDouble() / sink.format.frameSize * deck.format.sampleRate / sink.format.sampleRate * deck.rate
        return ((deck.positionFrames - waiting).coerceAtLeast(0.0) * 1_000 / deck.format.sampleRate).toLong().coerceAtMost(deck.durationMs)
    }

    private fun publishProgress(force: Boolean = false) {
        val deck = active ?: return
        val now = System.nanoTime()
        if (!force && now - lastProgressAt < 100_000_000L) return
        lastProgressAt = now
        onProgress(deck.trackId, audiblePosition(deck), deck.durationMs)
        publishOutput()
    }

    private fun publishOutput() {
        val sink = output
        val deck = active
        val value = if (sink == null || deck == null) null else sink.description.copy(sourceFormat = deck.format,
            processingApplied = !settings.bitPerfectEnabled && (settings.equalizerEnabled || volume != 1.0 || outgoing != null || deck.rate != 1.0 || deck.format != sink.format))
        if (value != lastOutput) { lastOutput = value; onOutputChanged(value) }
    }

    private fun seek(positionMs: Long, resume: Boolean) {
        val deck = active ?: return
        val position = positionMs.coerceIn(0, deck.durationMs)
        finishFade()
        output?.flush()
        pendingOffset = 0; pendingSize = 0
        deck.seek(position)
        equalizerKey = null
        if (needsNativeFormat) configureOutput(deck)
        playing = resume
        reachedEnd = false
        if (resume) { output?.start(); onPlaying(deck.trackId) }
        publishProgress(force = true)
    }

    private fun closeOutput() {
        val previous = output
        output = null
        pendingOffset = 0; pendingSize = 0
        previous?.close()
    }

    private fun closeDecks() {
        runCatching { closeOutput() }
        listOfNotNull(active, prepared, outgoing).distinct().forEach { runCatching { it.close() } }
        active = null; prepared = null; outgoing = null
        playing = false; reachedEnd = false; needsNativeFormat = false
        publishOutput()
    }
}
