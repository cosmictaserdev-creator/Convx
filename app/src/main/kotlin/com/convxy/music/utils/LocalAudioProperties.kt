/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.utils

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * What a local file actually is, read from the file itself.
 *
 * Nothing in the database records this: only the YouTube playback and download paths
 * write a `FormatEntity`, so an on-device file has no codec, sample rate or bitrate
 * stored anywhere. Read on demand rather than scanned into a column — it needs no schema
 * change, and it stays correct if the file is replaced under the same MediaStore id.
 */
data class LocalAudioProperties(
    val mimeType: String?,
    val sampleRateHz: Int?,
    val channelCount: Int?,
    val bitrateBps: Int?,
    /** Only present for formats that declare it; lossy codecs generally do not. */
    val bitsPerSample: Int?,
) {
    /** True when the container is one of the formats that carries the original samples. */
    val isLossless: Boolean
        get() = mimeType?.substringAfterLast('/')?.lowercase() in LOSSLESS_SUBTYPES

    private companion object {
        val LOSSLESS_SUBTYPES = setOf("flac", "alac", "raw", "wav", "x-wav")
    }
}

/**
 * Reads the first audio track's format.
 *
 * [MediaExtractor] rather than `MediaMetadataRetriever`: the retriever exposes a bitrate
 * and a mime type but no sample rate or channel count, which are the two numbers that
 * actually distinguish a lossless file from a transcode.
 *
 * Returns null rather than throwing for anything unreadable — a missing properties row is
 * a far better outcome than a crash on a malformed file, and files arrive here from the
 * user's own storage where nothing is guaranteed.
 */
suspend fun readLocalAudioProperties(
    context: Context,
    contentUri: String,
): LocalAudioProperties? = withContext(Dispatchers.IO) {
    val extractor = MediaExtractor()
    try {
        extractor.setDataSource(context, contentUri.toUri(), null)
        for (track in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(track)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (!mime.startsWith("audio/")) continue
            return@withContext LocalAudioProperties(
                mimeType = mime,
                sampleRateHz = format.intOrNull(MediaFormat.KEY_SAMPLE_RATE),
                channelCount = format.intOrNull(MediaFormat.KEY_CHANNEL_COUNT),
                bitrateBps = format.intOrNull(MediaFormat.KEY_BIT_RATE),
                bitsPerSample = format.intOrNull("bits-per-sample"),
            )
        }
        null
    } catch (_: Exception) {
        // setDataSource throws IOException on an unreadable file and IllegalArgument on a
        // malformed one; getTrackFormat can throw on a container it half-parsed.
        null
    } finally {
        runCatching { extractor.release() }
    }
}

/** `getInteger` throws when a key is absent, and most of these keys are optional. */
private fun MediaFormat.intOrNull(key: String): Int? =
    if (containsKey(key)) runCatching { getInteger(key) }.getOrNull() else null
