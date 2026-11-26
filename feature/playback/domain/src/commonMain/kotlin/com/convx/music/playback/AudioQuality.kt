package com.convx.music.playback

/** What the badge says. [lossless] adds the waveform glyph. */
data class AudioQuality(val label: String, val lossless: Boolean)

/**
 * Apple's wording: "Lossless" up to 48 kHz, "Hi-Res Lossless" above it, "Dolby Atmos" for
 * E-AC-3 JOC. Lossy streams keep the codec + bitrate so the badge still says something
 * useful (Apple shows nothing there; YouTube/Opus is the common case here). Null = no
 * format known yet, badge hidden rather than guessing.
 */
fun audioQualityLabel(mime: String?, codecs: String?, bitrate: Int?, sampleRate: Int?): AudioQuality? {
    val normalizedMime = mime.orEmpty().lowercase()
    val normalizedCodecs = codecs.orEmpty().lowercase()
    if (normalizedMime.isEmpty() && normalizedCodecs.isEmpty()) return null

    fun containsFormat(format: String) =
        format in normalizedMime || format in normalizedCodecs

    return when {
        containsFormat("joc") || containsFormat("atmos") ->
            AudioQuality("Dolby Atmos", lossless = false)
        containsFormat("ec-3") || containsFormat("eac3") -> AudioQuality("Dolby Digital Plus", lossless = false)
        containsFormat("flac") || containsFormat("alac") || normalizedMime == "audio/raw" || containsFormat("wav") -> {
            val label = if ((sampleRate ?: 0) > 48_000) "Hi-Res Lossless" else "Lossless"
            AudioQuality(label, lossless = true)
        }
        else -> {
            val codec = when {
                containsFormat("opus") -> "Opus"
                containsFormat("mp4a") || containsFormat("aac") -> "AAC"
                containsFormat("mp3") || containsFormat("mpeg") -> "MP3"
                containsFormat("vorbis") -> "Vorbis"
                else -> return null
            }
            AudioQuality(bitrate?.let { "$codec ${it / 1000} kbps" } ?: codec, lossless = false)
        }
    }
}

/** Card detail line, Apple's style: "24-bit/96 kHz FLAC", "Opus · 151 kbps · 48 kHz". */
fun audioQualityDetail(mime: String?, codecs: String?, bitrate: Int?, sampleRate: Int?): String {
    val audioFormat = (mime.orEmpty() + " " + codecs.orEmpty()).lowercase()
    val khz = sampleRate?.let { if (it % 1000 == 0) "${it / 1000} kHz" else "%.1f kHz".format(it / 1000f) }
    val codec = when {
        "flac" in audioFormat -> "FLAC"
        "alac" in audioFormat -> "ALAC"
        "ec-3" in audioFormat || "eac3" in audioFormat -> "Dolby Digital Plus"
        "opus" in audioFormat -> "Opus"
        "mp4a" in audioFormat || "aac" in audioFormat -> "AAC"
        "mp3" in audioFormat || "mpeg" in audioFormat -> "MP3"
        else -> "Audio"
    }
    return if (codec == "FLAC" || codec == "ALAC") {
        listOfNotNull(khz, codec).joinToString(" ")
    } else {
        listOfNotNull(codec, bitrate?.let { "${it / 1000} kbps" }, khz).joinToString(" · ")
    }
}

/** The audio stream a client is playing, as far as its provider told us. */
data class PlaybackStreamFormat(val mimeType: String, val bitrate: Int? = null, val sampleRate: Int? = null) {
    val quality: AudioQuality? get() = audioQualityLabel(mimeType, null, bitrate?.takeIf { it > 0 }, sampleRate)
    val detail: String get() = audioQualityDetail(mimeType, null, bitrate?.takeIf { it > 0 }, sampleRate)
}
