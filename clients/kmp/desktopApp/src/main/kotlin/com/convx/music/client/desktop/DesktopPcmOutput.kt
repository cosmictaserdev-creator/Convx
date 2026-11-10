package com.convx.music.client.desktop

import com.convx.music.playback.*
import java.io.IOException
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

internal data class DesktopAudioDevice(val id: String, val name: String)

internal interface DesktopPcmOutput : AutoCloseable {
    val format: PcmFormat
    val description: PlaybackAudioOutput
    val queuedFrames: Int
    val playedFrames: Long
    fun write(bytes: ByteArray, offset: Int, length: Int): Int
    fun start()
    fun pause()
    fun flush()
}

internal fun AudioFormat.pcmFormat() = PcmFormat(sampleRate.toInt(), channels, sampleSizeInBits,
    when (encoding) {
        AudioFormat.Encoding.PCM_SIGNED -> PcmEncoding.SIGNED_INTEGER
        AudioFormat.Encoding.PCM_UNSIGNED -> PcmEncoding.UNSIGNED_INTEGER
        AudioFormat.Encoding.PCM_FLOAT -> PcmEncoding.FLOAT
        else -> throw IOException("The decoded file is not uncompressed PCM audio.")
    }, isBigEndian)

internal fun PcmFormat.javaFormat() = AudioFormat(
    when (encoding) {
        PcmEncoding.SIGNED_INTEGER -> AudioFormat.Encoding.PCM_SIGNED
        PcmEncoding.UNSIGNED_INTEGER -> AudioFormat.Encoding.PCM_UNSIGNED
        PcmEncoding.FLOAT -> AudioFormat.Encoding.PCM_FLOAT
    }, sampleRate.toFloat(), bitsPerSample, channels, frameSize, sampleRate.toFloat(), bigEndian)

internal object DesktopPcmOutputs {
    fun devices(): List<DesktopAudioDevice> = if (System.getProperty("os.name").startsWith("Windows", true)) {
        WindowsWasapiOutput.devices()
    } else AudioSystem.getMixerInfo().mapIndexedNotNull { index, info ->
        val mixer = AudioSystem.getMixer(info)
        if (mixer.sourceLineInfo.any { SourceDataLine::class.java.isAssignableFrom(it.lineClass) })
            DesktopAudioDevice("javasound:$index:${info.name}", info.name) else null
    }

    fun open(format: PcmFormat, settings: PlaybackSettings): DesktopPcmOutput {
        if (System.getProperty("os.name").startsWith("Windows", true)) {
            return WindowsWasapiOutput.open(format, settings.bitPerfectEnabled, settings.audioOutputDeviceId)
        }
        check(!settings.bitPerfectEnabled) { "Exclusive audio output is not available on this platform yet. Turn off exclusive output to use the system mixer." }
        return JavaSoundPcmOutput(format, settings.audioOutputDeviceId)
    }
}

private class JavaSoundPcmOutput(override val format: PcmFormat, deviceId: String?) : DesktopPcmOutput {
    private val info = DataLine.Info(SourceDataLine::class.java, format.javaFormat())
    private val mixerInfo = deviceId?.let { id -> AudioSystem.getMixerInfo().withIndex()
        .firstOrNull { "javasound:${it.index}:${it.value.name}" == id }?.value }
    private val line = (mixerInfo?.let { AudioSystem.getMixer(it).getLine(info) }
        ?: AudioSystem.getLine(info)) as SourceDataLine
    private var basePosition = 0L
    private var writtenFrames = 0L
    override val description = PlaybackAudioOutput(format, format, PlaybackAudioOutputMode.SYSTEM_MIXER,
        mixerInfo?.name ?: "System default", processingApplied = false)

    init {
        try {
            line.open(format.javaFormat(), (format.sampleRate / 10).coerceAtLeast(1) * format.frameSize)
            basePosition = line.longFramePosition
        } catch (failure: Exception) { line.close(); throw failure }
    }

    override val playedFrames: Long get() = (line.longFramePosition - basePosition).coerceAtLeast(0)
    override val queuedFrames: Int get() = (writtenFrames - playedFrames).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
    override fun write(bytes: ByteArray, offset: Int, length: Int): Int {
        val frames = minOf(length / format.frameSize, line.available() / format.frameSize)
        if (frames <= 0) return 0
        return line.write(bytes, offset, frames * format.frameSize).also { writtenFrames += it / format.frameSize }
    }
    override fun start() = line.start()
    override fun pause() = line.stop()
    override fun flush() { line.stop(); line.flush(); basePosition = line.longFramePosition; writtenFrames = 0 }
    override fun close() { line.stop(); line.flush(); line.close() }
}
