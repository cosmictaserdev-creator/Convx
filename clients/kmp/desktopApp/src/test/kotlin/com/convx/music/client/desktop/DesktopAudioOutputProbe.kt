package com.convx.music.client.desktop

import com.convx.music.playback.*

/** Opt-in hardware probe; ordinary unit tests never acquire a user's audio endpoint. */
fun main(arguments: Array<String>) {
    val exclusive = "--exclusive" in arguments
    val formats = if (exclusive) listOf(PcmFormat(48_000, 2, 16)) else listOf(
        PcmFormat(44_100, 2, 16), PcmFormat(96_000, 2, 24), PcmFormat(192_000, 2, 32),
        PcmFormat(48_000, 2, 32, PcmEncoding.FLOAT))
    println("Available audio endpoints: ${DesktopPcmOutputs.devices().size}")
    for (format in formats) {
        DesktopPcmOutputs.open(format, PlaybackSettings(bitPerfectEnabled = exclusive)).use { output ->
            val bytes = ByteArray(format.sampleRate / 100 * format.frameSize)
            val end = System.nanoTime() + 10_000_000_000L
            var written = 0
            val expected = format.sampleRate / 2 * format.frameSize
            output.start()
            while (written < expected) {
                check(System.nanoTime() < end) { "Audio output made no progress" }
                written += output.write(bytes, 0, minOf(bytes.size, expected - written))
                Thread.sleep(2)
            }
            while (output.queuedFrames > 0) {
                check(System.nanoTime() < end) { "Audio output did not drain" }
                Thread.sleep(2)
            }
            check(output.playedFrames >= format.sampleRate / 2)
            println("${format.sampleRate} Hz / ${format.bitsPerSample} bit / ${format.encoding}: ${output.description.mode}; advanced=${output.playedFrames}; bitPerfect=${output.description.bitPerfect}")
        }
    }
}
