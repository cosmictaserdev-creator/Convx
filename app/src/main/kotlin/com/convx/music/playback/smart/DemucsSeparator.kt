package com.convx.music.playback.smart

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.convx.music.constants.DemucsAutoDownloadKey
import com.convx.music.constants.DemucsModelUrlKey
import com.convx.music.utils.YTPlayerUtils
import com.convx.music.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.nio.FloatBuffer
import kotlin.math.sqrt

enum class StemQuality { FAST, DEMUCS }

enum class DemucsInstall { OK, DOWNLOAD_FAILED, CORRUPT, INCOMPATIBLE }

/** The optional htdemucs ONNX model: downloaded on request and checked before it is trusted. */
object DemucsModel {
    /**
     * htdemucs with the STFT left outside the graph, which is exactly what [DemucsSeparator] feeds it
     * (sathvik77/htdemucs-core-onnx), pinned to one revision so the file can't change under us.
     * Meta's Demucs weights: CC BY-NC 4.0, non-commercial use only.
     */
    const val DEFAULT_URL =
        "https://huggingface.co/sathvik77/htdemucs-core-onnx/resolve/b07e91c7f37d7d5bff3746d61fb954d8cc129459/htdemucs_core.onnx"
    const val DEFAULT_BYTES = 170_992_246L
    private const val DEFAULT_SHA256 = "ae012b5669b0f24aef142577bdbc3ef0ce7ef2afca1aee94605ad7fac0a4f158"

    private val _progress = MutableStateFlow<Float?>(null)

    /** Download progress 0..1 while a download runs, from the settings screen or automatically; else null. */
    val progress: StateFlow<Float?> = _progress.asStateFlow()

    private val installLock = Mutex()

    private fun file(context: Context) = File(context.filesDir, "demucs/htdemucs.onnx")

    fun isInstalled(context: Context) = file(context).let { it.exists() && it.length() > 0 }

    fun sizeBytes(context: Context) = file(context).takeIf { it.exists() }?.length() ?: 0L

    fun delete(context: Context) {
        file(context).delete()
        File(file(context).path + ".part").delete()
    }

    internal fun path(context: Context): String = file(context).absolutePath

    /**
     * Downloads [url], resuming a partial file left by an interrupted attempt, checks the default
     * model's SHA-256, then runs one segment through it before it is kept.
     */
    suspend fun install(context: Context, url: String = DEFAULT_URL): DemucsInstall = installLock.withLock {
        withContext(Dispatchers.IO) {
            if (isInstalled(context)) return@withContext DemucsInstall.OK
            val target = file(context).also { it.parentFile?.mkdirs() }
            val part = File(target.path + ".part")
            _progress.value = 0f
            try {
                if (!download(url, part)) return@withContext DemucsInstall.DOWNLOAD_FAILED
                if (url == DEFAULT_URL && sha256(part) != DEFAULT_SHA256) {
                    part.delete()
                    return@withContext DemucsInstall.CORRUPT
                }
                if (!part.renameTo(target)) return@withContext DemucsInstall.DOWNLOAD_FAILED
                val compatible = DemucsSeparator(context).use { it.selfCheck() }
                if (compatible) DemucsInstall.OK else {
                    target.delete()
                    DemucsInstall.INCOMPATIBLE
                }
            } finally {
                _progress.value = null
            }
        }
    }

    /** False on a network failure; the partial file is kept so the next attempt resumes. */
    private fun download(url: String, part: File): Boolean = runCatching {
        val have = part.takeIf { it.exists() }?.length() ?: 0L
        val request = Request.Builder().url(url).apply { if (have > 0) header("Range", "bytes=$have-") }.build()
        YTPlayerUtils.httpClient.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "HTTP ${response.code}" }
            // 206 continues the partial file; a 200 means the server ignored the range, so start over.
            val resumed = response.code == 206
            val start = if (resumed) have else 0L
            val total = response.body.contentLength().takeIf { it > 0 }?.plus(start) ?: -1L
            var done = start
            response.body.byteStream().use { input ->
                java.io.FileOutputStream(part, resumed).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        done += read
                        if (total > 0) _progress.value = done.toFloat() / total
                    }
                }
            }
            total <= 0 || done == total
        }
    }.onFailure { Log.w("ConvxDemucs", "Model download failed", it) }.getOrDefault(false)

    private fun sha256(file: File): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(256 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Fetches the model on Wi-Fi or another unmetered network when the user turned automatic
     * download on. Called at startup and when the switch is turned on; an interrupted download
     * resumes from where it stopped.
     */
    suspend fun autoDownloadIfWanted(context: Context, enabled: Boolean? = null) {
        val prefs = context.dataStore.data.first()
        // [enabled] answers for a switch flipped just now, before its preference write has landed.
        if ((enabled ?: prefs[DemucsAutoDownloadKey]) != true || isInstalled(context) || !onUnmeteredNetwork(context)) return
        install(context, prefs[DemucsModelUrlKey]?.takeIf { it.isNotBlank() } ?: DEFAULT_URL)
    }

    private fun onUnmeteredNetwork(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

/**
 * Runs an htdemucs ONNX export that takes the waveform `[1,2,N]` and Demucs' complex-as-channels
 * spectrogram `[1,4,2048,T]` and returns a frequency branch `[1,S,4,2048,T]` plus a time branch
 * `[1,S,2,N]`, sources ordered drums, bass, other, vocals. Anything that does not match is refused.
 */
internal class DemucsSeparator(private val context: Context) : AutoCloseable {
    private class Contract(
        val segment: Int,
        val frames: Int,
        val waveInput: String,
        val specInput: String,
        val freqOutput: Int,
        val timeOutput: Int,
    )

    /** Per-segment working memory, reused across segments. */
    private class Scratch(bins: Int, frames: Int, length: Int) {
        val spec = FloatArray(4 * bins * frames)
        val re = Array(bins) { FloatArray(frames) }
        val im = Array(bins) { FloatArray(frames) }
        val left = FloatArray(length)
        val right = FloatArray(length)
        val branch = FloatArray(length)
    }

    private var options: OrtSession.SessionOptions? = null
    private var session: OrtSession? = null
    private var contract: Contract? = null
    private var scratch: Scratch? = null

    private fun open(): Boolean {
        if (session != null) return true
        if (!DemucsModel.isInstalled(context)) return false
        return runCatching {
            val opts = OrtSession.SessionOptions().apply {
                // Half the cores, 2 to 4: inference is most of a split, and the big cores do it.
                setIntraOpNumThreads((Runtime.getRuntime().availableProcessors() / 2).coerceIn(2, 4))
                setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
                setCPUArenaAllocator(false)
                setMemoryPatternOptimization(false)
            }
            val created = OrtEnvironment.getEnvironment().createSession(DemucsModel.path(context), opts)
            options = opts
            session = created
            contract = readContract(created)
            contract != null
        }.onFailure { Log.w(TAG, "Demucs model could not be opened", it) }.getOrDefault(false)
    }

    private fun readContract(session: OrtSession): Contract? {
        fun shapeOf(info: ai.onnxruntime.NodeInfo) = (info.info as? TensorInfo)?.shape
        val inputs = session.inputInfo.mapValues { shapeOf(it.value) }
        val wave = inputs.entries.firstOrNull { it.value?.size == 3 && it.value!![1] == 2L } ?: return null
        val spec = inputs.entries.firstOrNull { it.value?.size == 4 && it.value!![1] == 4L } ?: return null
        val segment = wave.value!![2].toInt()
        val frames = spec.value!![3].toInt()
        if (segment <= 0 || frames != DemucsStft.frames(segment) || spec.value!![2].toInt() != DemucsStft.BINS) return null

        val names = session.outputNames.toList()
        val outputs = session.outputInfo
        val freq = names.indexOfFirst { shapeOf(outputs.getValue(it))?.size == 5 }
        val time = names.indexOfFirst { shapeOf(outputs.getValue(it))?.size == 4 }
        if (freq < 0 || time < 0) return null
        val freqShape = shapeOf(outputs.getValue(names[freq]))!!
        val timeShape = shapeOf(outputs.getValue(names[time]))!!
        if (freqShape[1] != STEMS.toLong() || timeShape[1] != STEMS.toLong() || timeShape[3].toInt() != segment) return null
        return Contract(segment, frames, wave.key, spec.key, freq, time)
    }

    /** One synthetic segment through the model: refused unless the stems carry about the mix's energy. */
    fun selfCheck(): Boolean {
        if (!open()) return false
        val active = contract ?: return false
        val length = active.segment
        val left = FloatArray(length) { (0.3 * Math.sin(it * 0.03) + 0.2 * Math.sin(it * 0.17)).toFloat() }
        val right = FloatArray(length) { (0.3 * Math.sin(it * 0.031) + 0.2 * Math.sin(it * 0.19)).toFloat() }
        val summed = Array(2) { FloatArray(length) }
        val ok = runSegment(left, right, length) { stem, l, r ->
            for (i in 0 until length) {
                summed[0][i] += l[i]
                summed[1][i] += r[i]
            }
        }
        if (!ok) return false
        fun energy(a: FloatArray, from: Int, to: Int): Double {
            var sum = 0.0
            for (i in from until to) sum += a[i].toDouble() * a[i]
            return sqrt(sum / (to - from))
        }
        val from = EDGE_SKIP
        val to = length - EDGE_SKIP
        val mix = energy(left, from, to) + energy(right, from, to)
        val out = energy(summed[0], from, to) + energy(summed[1], from, to)
        val finite = summed.all { channel -> channel.all { it.isFinite() } }
        return finite && mix > 0 && out / mix in 0.25..4.0
    }

    /** Streams every stem as (stem, left, right, count) pieces in track order. False on failure or cancel. */
    fun separate(
        left: FloatArray,
        right: FloatArray,
        isCurrent: () -> Boolean,
        sink: (Int, FloatArray, FloatArray, Int) -> Unit,
    ): Boolean {
        if (!open()) return false
        val active = contract ?: return false
        val n = active.segment
        val stride = n - n / OVERLAP_DIVISOR
        val total = left.size
        val weight = FloatArray(n) { i -> if (i < n / 2) (i + 1).toFloat() else (n - i).toFloat() }
        val peak = weight.max()
        for (i in weight.indices) weight[i] /= peak

        val acc = Array(STEMS) { Array(2) { FloatArray(n) } }
        val sums = FloatArray(n)
        val chunkLeft = FloatArray(n)
        val chunkRight = FloatArray(n)
        val outLeft = FloatArray(n)
        val outRight = FloatArray(n)

        var start = 0
        while (start < total) {
            if (!isCurrent()) return false
            val valid = minOf(n, total - start)
            chunkLeft.fill(0f)
            chunkRight.fill(0f)
            left.copyInto(chunkLeft, 0, start, start + valid)
            right.copyInto(chunkRight, 0, start, start + valid)
            val ok = runSegment(chunkLeft, chunkRight, n) { stem, l, r ->
                for (i in 0 until n) {
                    acc[stem][0][i] += l[i] * weight[i]
                    acc[stem][1][i] += r[i] * weight[i]
                }
            }
            if (!ok) return false
            for (i in 0 until n) sums[i] += weight[i]

            val last = start + n >= total
            val emit = if (last) valid else stride
            for (stem in 0 until STEMS) {
                for (i in 0 until emit) {
                    val w = sums[i].takeIf { it > 1e-6f } ?: 1f
                    outLeft[i] = acc[stem][0][i] / w
                    outRight[i] = acc[stem][1][i] / w
                }
                sink(stem, outLeft, outRight, emit)
            }
            if (last) break
            for (stem in 0 until STEMS) for (channel in 0..1) {
                val a = acc[stem][channel]
                a.copyInto(a, 0, emit, n)
                a.fill(0f, n - emit, n)
            }
            sums.copyInto(sums, 0, emit, n)
            sums.fill(0f, n - emit, n)
            start += stride
        }
        return true
    }

    /** Runs the model on one segment and hands each stem's waveform to [consume]. */
    private fun runSegment(
        left: FloatArray,
        right: FloatArray,
        length: Int,
        consume: (Int, FloatArray, FloatArray) -> Unit,
    ): Boolean {
        val active = contract ?: return false
        val onnx = session ?: return false
        val environment = OrtEnvironment.getEnvironment()
        val frames = active.frames
        val bins = DemucsStft.BINS
        // ~25 MB of scratch per segment; a song is dozens of segments, so it is allocated once.
        val buffers = scratch ?: Scratch(bins, frames, length).also { scratch = it }
        val spec = buffers.spec
        val re = buffers.re
        val im = buffers.im
        for ((channel, samples) in listOf(left, right).withIndex()) {
            DemucsStft.forward(samples, length, re, im)
            for (bin in 0 until bins) {
                re[bin].copyInto(spec, ((2 * channel) * bins + bin) * frames)
                im[bin].copyInto(spec, ((2 * channel + 1) * bins + bin) * frames)
            }
        }
        val wave = FloatArray(2 * length)
        left.copyInto(wave, 0, 0, length)
        right.copyInto(wave, length, 0, length)

        return runCatching {
            OnnxTensor.createTensor(environment, FloatBuffer.wrap(wave), longArrayOf(1, 2, length.toLong())).use { waveTensor ->
                OnnxTensor.createTensor(environment, FloatBuffer.wrap(spec), longArrayOf(1, 4, bins.toLong(), frames.toLong())).use { specTensor ->
                    onnx.run(mapOf(active.waveInput to waveTensor, active.specInput to specTensor)).use { result ->
                        val freq = (result.get(active.freqOutput) as OnnxTensor).floatBuffer
                        val time = (result.get(active.timeOutput) as OnnxTensor).floatBuffer
                        val stemLeft = buffers.left
                        val stemRight = buffers.right
                        val stemRe = buffers.re
                        val stemIm = buffers.im
                        val branch = buffers.branch
                        for (stem in 0 until STEMS) {
                            for (channel in 0..1) {
                                // Whole rows at a time: element-wise get() on an 11-million-float buffer was the slow part.
                                for (bin in 0 until bins) {
                                    freq.position((((stem * 4) + 2 * channel) * bins + bin) * frames)
                                    freq.get(stemRe[bin], 0, frames)
                                    freq.position((((stem * 4) + 2 * channel + 1) * bins + bin) * frames)
                                    freq.get(stemIm[bin], 0, frames)
                                }
                                val target = if (channel == 0) stemLeft else stemRight
                                DemucsStft.inverse(stemRe, stemIm, length, target)
                                time.position(((stem * 2) + channel) * length)
                                time.get(branch, 0, length)
                                for (i in 0 until length) target[i] += branch[i]
                            }
                            consume(stem, stemLeft, stemRight)
                        }
                    }
                }
            }
            true
        }.onFailure { Log.w(TAG, "Demucs inference failed", it) }.getOrDefault(false)
    }

    override fun close() {
        runCatching { session?.close() }
        runCatching { options?.close() }
        session = null
        options = null
        scratch = null
    }

    companion object {
        private const val TAG = "ConvxDemucs"

        /** htdemucs was trained at 44.1 kHz; any other rate gives it the wrong pitch and tempo. */
        const val SAMPLE_RATE = 44_100.0
        const val STEMS = 4
        const val DRUMS = 0
        const val BASS = 1
        const val OTHER = 2
        const val VOCALS = 3
        private const val OVERLAP_DIVISOR = 4
        private const val EDGE_SKIP = 4096
    }
}
