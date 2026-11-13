package com.convx.music.playback

enum class PcmEncoding { SIGNED_INTEGER, UNSIGNED_INTEGER, FLOAT }

data class PcmFormat(
    val sampleRate: Int,
    val channels: Int,
    val bitsPerSample: Int,
    val encoding: PcmEncoding = PcmEncoding.SIGNED_INTEGER,
    val bigEndian: Boolean = false,
) {
    init {
        require(sampleRate in 1..768_000)
        require(channels in 1..32)
        require(bitsPerSample in setOf(8, 16, 24, 32, 64))
        require(encoding != PcmEncoding.FLOAT || bitsPerSample == 32 || bitsPerSample == 64)
        require(encoding != PcmEncoding.UNSIGNED_INTEGER || bitsPerSample == 8)
        require(encoding != PcmEncoding.SIGNED_INTEGER || bitsPerSample <= 32)
    }

    val bytesPerSample: Int get() = bitsPerSample / 8
    val frameSize: Int get() = channels * bytesPerSample
}

enum class PlaybackAudioOutputMode { SYSTEM_MIXER, EXCLUSIVE }

data class PlaybackAudioOutput(
    val sourceFormat: PcmFormat,
    val deviceFormat: PcmFormat,
    val mode: PlaybackAudioOutputMode,
    val deviceName: String,
    val processingApplied: Boolean,
) {
    val bitPerfect: Boolean get() = mode == PlaybackAudioOutputMode.EXCLUSIVE &&
        !processingApplied && sourceFormat == deviceFormat
}

/** Converts PCM for analysis or explicitly enabled processing; bypass playback keeps the original bytes. */
object PcmSamples {
    fun read(bytes: ByteArray, offset: Int, format: PcmFormat): Double {
        val raw = readBits(bytes, offset, format.bytesPerSample, format.bigEndian)
        return when (format.encoding) {
            PcmEncoding.FLOAT -> if (format.bitsPerSample == 32) Float.fromBits(raw.toInt()).toDouble() else Double.fromBits(raw)
            PcmEncoding.UNSIGNED_INTEGER -> ((raw and 255) - 128) / 128.0
            PcmEncoding.SIGNED_INTEGER -> {
                val shift = 64 - format.bitsPerSample
                val signed = (raw shl shift) shr shift
                signed / (1L shl (format.bitsPerSample - 1)).toDouble()
            }
        }
    }

    fun write(bytes: ByteArray, offset: Int, value: Double, format: PcmFormat) {
        val finite = value.takeIf(Double::isFinite) ?: 0.0
        val raw = when (format.encoding) {
            PcmEncoding.FLOAT -> if (format.bitsPerSample == 32) finite.toFloat().toRawBits().toLong() else finite.toRawBits()
            PcmEncoding.UNSIGNED_INTEGER -> ((finite.coerceIn(-1.0, 1.0) * 128).toLong() + 128).coerceIn(0, 255)
            PcmEncoding.SIGNED_INTEGER -> {
                val scale = 1L shl (format.bitsPerSample - 1)
                (finite.coerceIn(-1.0, 1.0) * scale).toLong().coerceIn(-scale, scale - 1)
            }
        }
        repeat(format.bytesPerSample) { index ->
            val shift = (if (format.bigEndian) format.bytesPerSample - index - 1 else index) * 8
            bytes[offset + index] = (raw ushr shift).toByte()
        }
    }

    private fun readBits(bytes: ByteArray, offset: Int, width: Int, bigEndian: Boolean): Long {
        var result = 0L
        repeat(width) { index ->
            val shift = (if (bigEndian) width - index - 1 else index) * 8
            result = result or ((bytes[offset + index].toLong() and 255) shl shift)
        }
        return result
    }
}
