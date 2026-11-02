/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.playback.haptics

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val TARGET_RATE = 4000.0
private const val FRAMES_PER_SECOND = 100.0
private const val MAX_SECONDS = 1200L
private const val MINIMUM_GAP_MS = 150
private const val MINIMUM_SCORE = 0.5

/** Threshold multiple a hit must clear at [sensitivity] (0 to 1); 0.5 keeps the tuned value. */
internal fun hapticScoreFor(sensitivity: Float): Double {
    val value = sensitivity.coerceIn(0f, 1f).toDouble()
    return if (value < 0.5) 1.6 - 1.2 * value else 1.5 - value
}

/** One pulse per detected hit for a whole song, stored compactly. */
class HapticTrack(
    /** Pulse starts in milliseconds, ascending. */
    val times: IntArray,
    /** 0..255 */
    val intensity: IntArray,
    /** 0..255, low is a bass thud and high is a crisp click. */
    val sharpness: IntArray,
    /** How many times each candidate clears the tuned threshold; null once selected. */
    private val scores: FloatArray? = null,
) {
    val size: Int get() = times.size

    /** The pulses that clear [sensitivity]. Candidates are kept so no re-decode is needed. */
    fun select(sensitivity: Float): HapticTrack {
        val scores = scores ?: return this
        val required = hapticScoreFor(sensitivity)
        val kept = ArrayList<Int>()
        var last = -MINIMUM_GAP_MS
        for (n in times.indices) {
            if (scores[n] <= required || times[n] - last < MINIMUM_GAP_MS) continue
            kept.add(n)
            last = times[n]
        }
        return HapticTrack(
            IntArray(kept.size) { times[kept[it]] },
            IntArray(kept.size) { intensity[kept[it]] },
            IntArray(kept.size) { sharpness[kept[it]] },
        )
    }

    /** The first pulse at or after [milliseconds]. */
    fun indexAt(milliseconds: Long): Int {
        var low = 0
        var high = times.size
        while (low < high) {
            val middle = (low + high) ushr 1
            if (times[middle] < milliseconds) low = middle + 1 else high = middle
        }
        return low
    }
}

private class DoubleList {
    var data = DoubleArray(1024)
    var size = 0

    fun add(value: Double) {
        if (size == data.size) data = data.copyOf(size * 2)
        data[size++] = value
    }

    fun toArray(): DoubleArray = data.copyOf(size)
}

/**
 * Streams mono samples in and keeps one bass and one treble level per 10 ms frame.
 * Samples are averaged down to about 4 kHz first, so a 20 minute song stays small.
 */
internal class BandEnergyCollector(sampleRate: Int) {
    private val decimation = max(1, (sampleRate / TARGET_RATE).roundToInt())
    private val effectiveRate = sampleRate.toDouble() / decimation
    private val hop = max(1, (effectiveRate / FRAMES_PER_SECOND).roundToInt())
    val frameMs: Double = hop * decimation * 1000.0 / sampleRate

    private val lowAlpha = exp(-2 * PI * 150 / effectiveRate)
    private val highAlpha = exp(-2 * PI * 400 / effectiveRate)
    private var low1 = 0.0
    private var low2 = 0.0
    private var smooth1 = 0.0
    private var smooth2 = 0.0

    private var decimationSum = 0.0
    private var decimationCount = 0
    private var lowSum = 0.0
    private var highSum = 0.0
    private var inFrame = 0
    private val low = DoubleList()
    private val high = DoubleList()

    fun push(sample: Float) {
        decimationSum += sample
        if (++decimationCount == decimation) {
            process(decimationSum / decimation)
            decimationSum = 0.0
            decimationCount = 0
        }
    }

    private fun process(x: Double) {
        low1 = (1 - lowAlpha) * x + lowAlpha * low1
        low2 = (1 - lowAlpha) * low1 + lowAlpha * low2
        // Two one-pole high-passes keep a kick's fundamental out of the treble band.
        smooth1 = (1 - highAlpha) * x + highAlpha * smooth1
        val high1 = x - smooth1
        smooth2 = (1 - highAlpha) * high1 + highAlpha * smooth2
        val high2 = high1 - smooth2
        lowSum += low2 * low2
        highSum += high2 * high2
        if (++inFrame == hop) {
            low.add(sqrt(lowSum / hop))
            high.add(sqrt(highSum / hop))
            lowSum = 0.0
            highSum = 0.0
            inFrame = 0
        }
    }

    fun finish(): HapticTrack = findHits(low.toArray(), high.toArray(), frameMs)
}

private fun percentile90(values: DoubleArray): Double {
    val sorted = values.copyOf()
    sorted.sort()
    return sorted[min(sorted.size - 1, (sorted.size * 0.9).toInt())]
}

/**
 * Finds hits from per-frame bass and treble levels. Rises in either band are onsets, kick and
 * bass hits weigh more, and an adaptive threshold follows the song's own density so busy and
 * sparse passages both give a readable pattern instead of a constant buzz.
 */
internal fun findHits(low: DoubleArray, high: DoubleArray, frameMs: Double): HapticTrack {
    val frames = low.size
    val empty = HapticTrack(IntArray(0), IntArray(0), IntArray(0), FloatArray(0))
    if (frames < 8) return empty

    val loudness = DoubleArray(frames) { low[it] + high[it] }
    val gate = max(0.002, percentile90(loudness) * 0.1)
    // Rises are measured against each band's typical loud level, not in log terms: every quiet
    // sound starting is a large log rise, but only loud hits such as kicks should be pulses.
    val lowReference = max(1e-4, percentile90(low))
    val highReference = max(1e-4, percentile90(high))

    val onset = DoubleArray(frames)
    for (i in 2 until frames) {
        onset[i] = max(0.0, low[i] - low[i - 2]) / lowReference +
            max(0.0, high[i] - high[i - 2]) / highReference * 0.3
    }
    val prefix = DoubleArray(frames + 1)
    for (i in 0 until frames) prefix[i + 1] = prefix[i] + onset[i]

    val radius = 40 // about 0.4 s of context for the threshold
    val peak = 3 // a hit must be the largest rise within about 30 ms
    val found = ArrayList<Int>()
    val excess = ArrayList<Double>()
    val score = ArrayList<Float>()
    for (i in 2 until frames) {
        if (loudness[i] < gate) continue
        val value = onset[i]
        val from = max(0, i - radius)
        val to = min(frames, i + radius + 1)
        val threshold = max(0.15, (prefix[to] - prefix[from]) / (to - from) * 2.4)
        if (value <= threshold * MINIMUM_SCORE) continue
        var largest = true
        for (j in max(0, i - peak)..min(frames - 1, i + peak)) {
            if (onset[j] > value) {
                largest = false
                break
            }
        }
        if (!largest) continue
        found.add(i)
        excess.add(value - threshold)
        score.add((value / threshold).toFloat())
    }
    if (found.isEmpty()) return empty

    // Scale accents against the hits the tuned threshold keeps, so the default setting feels
    // consistent and the extra hits from higher sensitivity stay soft.
    val ranked = excess.filter { it > 0 }.sorted()
    val reference = if (ranked.isEmpty()) {
        1e-6
    } else {
        max(1e-6, ranked[min(ranked.size - 1, (ranked.size * 0.95).toInt())])
    }
    val times = IntArray(found.size)
    val intensity = IntArray(found.size)
    val sharpness = IntArray(found.size)
    for (n in found.indices) {
        val i = found[n]
        val lowRise = max(0.0, low[i] - low[i - 2])
        val highRise = max(0.0, high[i] - high[i - 2])
        val bass = lowRise / (lowRise + highRise + 1e-9)
        val strength = sqrt((excess[n] / reference).coerceIn(0.0, 1.0))
        // The rise is seen about two frames after the attack begins, so start there.
        times[n] = (max(0, i - 2) * frameMs).toInt()
        intensity[n] = (255 * (0.3 + 0.7 * strength) * (0.65 + 0.35 * bass)).roundToInt().coerceIn(1, 255)
        sharpness[n] = (255 * (1 - bass)).roundToInt().coerceIn(0, 255)
    }
    return HapticTrack(times, intensity, sharpness, score.toFloatArray())
}

/**
 * Decodes a song on this device once and returns its pulses, or null when it cannot be read.
 * Cancelling the calling coroutine stops the decode.
 */
suspend fun analyzeHaptics(context: Context, uri: Uri): HapticTrack? = withContext(Dispatchers.Default) {
    val extractor = MediaExtractor()
    var codec: MediaCodec? = null
    try {
        extractor.setDataSource(context, uri, null)
        var trackIndex = -1
        var inputFormat: MediaFormat? = null
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("audio/")) {
                trackIndex = i
                inputFormat = format
                break
            }
        }
        val format = inputFormat ?: return@withContext null
        val mime = format.getString(MediaFormat.KEY_MIME) ?: return@withContext null
        extractor.selectTrack(trackIndex)

        val decoder = MediaCodec.createDecoderByType(mime)
        codec = decoder
        decoder.configure(format, null, null, 0)
        decoder.start()

        val info = MediaCodec.BufferInfo()
        val limitUs = MAX_SECONDS * 1_000_000L
        var collector: BandEnergyCollector? = null
        var channels = 2
        var floatPcm = false
        var inputDone = false
        var outputDone = false
        var idle = 0

        fun readOutputFormat() {
            val out = decoder.outputFormat
            channels = if (out.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                max(1, out.getInteger(MediaFormat.KEY_CHANNEL_COUNT))
            } else {
                2
            }
            floatPcm = out.containsKey(MediaFormat.KEY_PCM_ENCODING) &&
                out.getInteger(MediaFormat.KEY_PCM_ENCODING) == AudioFormat.ENCODING_PCM_FLOAT
            val rate = if (out.containsKey(MediaFormat.KEY_SAMPLE_RATE)) out.getInteger(MediaFormat.KEY_SAMPLE_RATE) else 44100
            collector = BandEnergyCollector(rate)
        }

        while (!outputDone) {
            ensureActive()
            if (!inputDone) {
                val inputIndex = decoder.dequeueInputBuffer(5_000)
                if (inputIndex >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputIndex)
                    val size = if (inputBuffer != null) extractor.readSampleData(inputBuffer, 0) else -1
                    if (size < 0 || extractor.sampleTime > limitUs) {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        inputDone = true
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, size, extractor.sampleTime, 0)
                        extractor.advance()
                    }
                }
            }
            val outputIndex = decoder.dequeueOutputBuffer(info, 10_000)
            if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                readOutputFormat()
            } else if (outputIndex >= 0) {
                idle = 0
                val buffer = decoder.getOutputBuffer(outputIndex)
                if (buffer != null && info.size > 0) {
                    if (collector == null) readOutputFormat()
                    val sink = collector
                    if (sink != null) {
                        buffer.position(info.offset)
                        buffer.limit(info.offset + info.size)
                        buffer.order(ByteOrder.LITTLE_ENDIAN)
                        if (floatPcm) {
                            val samples = FloatArray(info.size / 4)
                            buffer.asFloatBuffer().get(samples)
                            var i = 0
                            while (i + channels <= samples.size) {
                                var sum = 0f
                                for (c in 0 until channels) sum += samples[i + c]
                                sink.push(sum / channels)
                                i += channels
                            }
                        } else {
                            val samples = ShortArray(info.size / 2)
                            buffer.asShortBuffer().get(samples)
                            var i = 0
                            while (i + channels <= samples.size) {
                                var sum = 0
                                for (c in 0 until channels) sum += samples[i + c]
                                sink.push(sum / (channels * 32768f))
                                i += channels
                            }
                        }
                    }
                }
                decoder.releaseOutputBuffer(outputIndex, false)
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) outputDone = true
                if (info.presentationTimeUs > limitUs) outputDone = true
            } else if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER && inputDone) {
                // The decoder has been told the stream ended; give up if it never answers.
                if (++idle > 500) outputDone = true
            }
        }
        collector?.finish()
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    } finally {
        try {
            codec?.stop()
        } catch (_: Exception) {
        }
        try {
            codec?.release()
        } catch (_: Exception) {
        }
        extractor.release()
    }
}
