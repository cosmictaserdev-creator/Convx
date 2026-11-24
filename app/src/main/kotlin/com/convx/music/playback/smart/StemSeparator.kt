package com.convx.music.playback.smart

import android.content.Context
import android.media.MediaDataSource
import android.net.Uri
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.convx.music.playback.AudioCache
import java.io.File

object StemStore {
    private fun directory(context: Context) = File(context.filesDir, "stems")

    private fun base(id: String) = "${id.hashCode().toUInt()}_${id.length}"

    fun stem(context: Context, id: String, name: String) = File(directory(context), "${base(id)}_$name.m4a")

    fun vocals(context: Context, id: String) = stem(context, id, "vocals")

    fun instrumental(context: Context, id: String) = stem(context, id, "instrumental")

    fun has(context: Context, id: String) = vocals(context, id).exists()

    fun totalBytes(context: Context): Long = directory(context).listFiles()?.sumOf { it.length() } ?: 0L

    fun clear(context: Context) {
        directory(context).deleteRecursively()
    }

    fun estimatedBytes(durationSeconds: Int, stems: Int = 2): Long = durationSeconds * stems * 96_000L / 8L

    internal fun prepare(context: Context) = directory(context).apply { mkdirs() }
}

@UnstableApi
object StemSeparator {
    private const val TAG = "ConvxStemSeparator"

    const val MAX_SECONDS = 12 * 60 // the whole track is decoded into memory

    fun separate(
        context: Context,
        id: String,
        uri: Uri,
        durationSeconds: Int,
        quality: StemQuality,
        isCurrent: () -> Boolean,
    ): Boolean {
        if (durationSeconds > MAX_SECONDS) return false
        val appContext = context.applicationContext
        StemStore.prepare(appContext)
        return split(appContext, uri, quality, isCurrent) { StemStore.stem(appContext, id, it) } != null
    }

    /** The stems [quality] produces here: Demucs' four when its model is installed, else two. */
    fun stemNames(context: Context, quality: StemQuality): List<String> =
        if (quality == StemQuality.DEMUCS && DemucsModel.isInstalled(context)) {
            listOf("drums", "bass", "other", "vocals")
        } else {
            listOf("vocals", "instrumental")
        }

    /**
     * Decodes [uri], splits it with [quality] and writes each stem as AAC to `target(name)`.
     * Returns the stem names written, or null on failure or cancel; nothing is left behind then.
     */
    fun split(
        context: Context,
        uri: Uri,
        quality: StemQuality,
        isCurrent: () -> Boolean,
        target: (String) -> File,
    ): List<String>? {
        val appContext = context.applicationContext
        val names = stemNames(appContext, quality)
        val demucs = names.size == 4
        val finals = names.map(target)
        val temps = finals.map { File(it.path + ".part") }
        val writers = arrayOfNulls<AacFileWriter>(names.size)
        var finished = false
        try {
            val source = open(appContext, uri) ?: return null
            val decoded = source.use { AudioDecoder.decodeRegionStereo(it, 0.0, MAX_SECONDS.toDouble(), isCurrent) }
                ?: return null
            // Each separator was trained at one rate; Demucs' is fixed rather than borrowed from the other one.
            val rate = if (demucs) DemucsSeparator.SAMPLE_RATE else VocalSpectrogram.sampleRate
            var left = decoded.first.left
            var right = decoded.first.right
            if (kotlin.math.abs(decoded.first.sampleRate - rate) > 1.0) {
                left = MelSpectrogram.resample(left, decoded.first.sampleRate, rate) ?: return null
                right = MelSpectrogram.resample(right, decoded.first.sampleRate, rate) ?: return null
            }
            if (left.size < rate) return null
            for (i in names.indices) writers[i] = AacFileWriter(temps[i], rate.toInt())

            val ok = if (demucs) {
                DemucsSeparator(appContext).use { separator ->
                    separator.separate(left, right, isCurrent) { stem, l, r, count -> writers[stem]!!.write(l, r, 0, count) }
                }
            } else {
                separateFast(appContext, left, right, writers[0]!!, writers[1]!!, isCurrent)
            }
            if (!ok) return null
            writers.forEach { it!!.finish() }
            writers.fill(null)
            finished = temps.indices.all { temps[it].renameTo(finals[it]) }
            return names.takeIf { finished }
        } catch (error: Throwable) {
            Log.w(TAG, "Stem separation of $uri failed", error)
            return null
        } finally {
            writers.forEach { it?.abort() }
            if (!finished) temps.forEach { it.delete() }
        }
    }

    private fun separateFast(
        context: Context,
        left: FloatArray,
        right: FloatArray,
        vocals: AacFileWriter,
        instrumental: AacFileWriter,
        isCurrent: () -> Boolean,
    ): Boolean {
        val tracker = VocalTracker(context)
        try {
            val restLeft = FloatArray(CHUNK_SAMPLES)
            val restRight = FloatArray(CHUNK_SAMPLES)
            return tracker.separateVocals(left, right, isCurrent) { voiceL, voiceR, start, count ->
                vocals.write(voiceL, voiceR, 0, count)
                for (i in 0 until count) {
                    restLeft[i] = left[start + i] - voiceL[i]
                    restRight[i] = right[start + i] - voiceR[i]
                }
                instrumental.write(restLeft, restRight, 0, count)
            }
        } finally {
            tracker.release()
        }
    }

    private fun open(context: Context, uri: Uri): MediaDataSource? {
        if (uri.scheme == "content" || uri.scheme == "file") return UriMediaDataSource.open(context, uri)
        val rendition = AudioCache.renditionsOf(uri).filter { it.isComplete }.maxByOrNull { it.contentLength } ?: return null
        return AudioCache.renditionDataSource(uri, rendition)
    }

    private const val CHUNK_SAMPLES = 1024 * 255 - 6144
}
