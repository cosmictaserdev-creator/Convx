package com.convx.music.network

import android.app.Activity
import android.app.Instrumentation
import android.os.Bundle
import com.convx.music.BuildConfig
import com.music.spine.ModuleManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import com.music.spine.ModuleStreamResponse

/** Opt-in live checks. URLs arrive as runner arguments and are never included in the report. */
class ModuleCompatibilityInstrumentation : Instrumentation() {
    private var indexUrl: String? = null
    private var addonUrl: String? = null
    private var decodeMedia = false
    private var requestedTiers = emptySet<String>()
    private val report = StringBuilder()
    private var failures = 0

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        indexUrl = arguments?.getString("indexUrl")
        addonUrl = arguments?.getString("addonUrl")
        decodeMedia = arguments?.getString("decode") == "true"
        requestedTiers = arguments?.getString("qualityTiers")?.split(',')?.toSet().orEmpty()
        start()
    }

    override fun onStart() {
        ModuleManager.userAgent = "Convx/${BuildConfig.VERSION_NAME}"
        val manager = ModuleManager()
        runBlocking {
            for ((label, url) in listOf("module" to indexUrl, "addon" to addonUrl)) {
                if (url.isNullOrBlank()) continue
                try {
                    withTimeout(120_000L) {
                        val descriptors = manager.fetchIndex(url, refresh = true).getOrThrow()
                        check(descriptors.isNotEmpty())
                        record("$label: index OK, modules=${descriptors.size}")
                        for (descriptor in descriptors.take(3)) {
                            val loaded = manager.loadModule(descriptor).getOrThrow()
                            val options = loaded.manifest.settings["quality"]?.options.orEmpty().map { it.value }
                            record("$label: load OK, id=${descriptor.id}, version=${descriptor.version}, qualityOptions=$options")
                            val tracks = manager.searchTracks(loaded, "Daft Punk Get Lucky", 10).getOrThrow().tracks
                            check(tracks.isNotEmpty())
                            record("$label: search OK, tracks=${tracks.size}")
                            val selected = tracks.firstOrNull {
                                it.artist.contains("Daft Punk", true) && it.title.contains("Get Lucky", true)
                            } ?: tracks.first()
                            record("$label: selected title=${selected.title}, artist=${selected.artist}")
                            val tiers = options.filter { it in setOf("HIGH", "CD", "HI_RES", "HI_RES_DOLBY") }
                                .filter { requestedTiers.isEmpty() || it in requestedTiers }
                                .ifEmpty { listOf(loaded.manifest.defaults["quality"].orEmpty()) }
                            for (quality in tiers) {
                                try {
                                    val settings = loaded.manifest.defaults + if (quality.isBlank()) emptyMap() else mapOf("quality" to quality)
                                    val stream = manager.getStreamUrl(loaded, selected.id, settings, quality).getOrThrow()
                                    check(!stream.streamUrl.isNullOrBlank())
                                    record("$label: resolve OK, tier=${quality.ifBlank { "default" }}, codec=${stream.track?.codec}, format=${stream.track?.format}, type=${stream.streamType}")
                                    probe(label, quality, requireNotNull(stream.streamUrl))
                                    if (decodeMedia) decode(label, quality, stream)
                                } catch (error: Exception) {
                                    failures++
                                    record("$label: tier=${quality.ifBlank { "default" }} FAILED (${error.javaClass.simpleName})")
                                }
                            }
                        }
                    }
                } catch (error: Exception) {
                    failures++
                    record("$label: FAILED (${error.javaClass.simpleName})")
                }
            }
        }
        manager.unloadAll()
        finish(if (failures == 0) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            Bundle().apply { putString("stream", report.toString()) })
    }

    private fun probe(label: String, tier: String, url: String) {
        val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS).callTimeout(20, TimeUnit.SECONDS).build()
        val request = Request.Builder().url(url).header("Range", "bytes=0-4095")
            .header("User-Agent", ModuleManager.userAgent).build()
        var manifest = false
        client.newCall(request).execute().use { response ->
            record("$label: tier=${tier.ifBlank { "default" }} media HTTP ${response.code}, type=${response.header("Content-Type")?.substringBefore(';')}")
            check(response.isSuccessful)
            val input = requireNotNull(response.body).byteStream()
            val bytes = ByteArray(32)
            check(input.read(bytes) > 0)
            val prefix = bytes.toString(Charsets.UTF_8).trimStart()
            manifest = prefix.startsWith("#EXTM3U") || prefix.startsWith("<?xml") || prefix.startsWith("<MPD")
        }
        if (manifest) {
            record("$label: tier=${tier.ifBlank { "default" }} manifest readable; media-segment seeking needs decoder validation")
            client.connectionPool.evictAll()
            client.dispatcher.executorService.shutdown()
            return
        }
        client.newCall(request.newBuilder().header("Range", "bytes=65536-69631").build()).execute().use { response ->
            record("$label: tier=${tier.ifBlank { "default" }} seek HTTP ${response.code}, range=${response.header("Content-Range")}")
            check(response.isSuccessful)
        }
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdown()
    }

    private fun record(message: String) {
        report.appendLine(message)
        sendStatus(1, Bundle().apply { putString("stream", message + "\n") })
    }

    private fun decode(label: String, tier: String, stream: ModuleStreamResponse) {
        val prepared = CountDownLatch(1)
        val sought = CountDownLatch(1)
        val failure = AtomicReference<PlaybackException?>()
        var seeking = false
        var player: ExoPlayer? = null
        val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
        try {
            runOnMainSync {
                val renderers = NextRenderersFactory(targetContext)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                    .setEnableDecoderFallback(true)
                player = ExoPlayer.Builder(targetContext, renderers)
                    .setMediaSourceFactory(DefaultMediaSourceFactory(targetContext).setDataSourceFactory(
                        DefaultDataSource.Factory(targetContext, OkHttpDataSource.Factory(client).setUserAgent(ModuleManager.userAgent)),
                    )).build().apply {
                        volume = 0f
                        addListener(object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                failure.set(error); prepared.countDown(); sought.countDown()
                            }
                            override fun onEvents(source: Player, events: Player.Events) {
                                if (source.playbackState == Player.STATE_READY && source.isPlaying && audioFormat != null) {
                                    if (seeking && source.currentPosition >= 19_000) sought.countDown()
                                    else prepared.countDown()
                                }
                            }
                        })
                        val mime = when (stream.streamType) {
                            "hls" -> MimeTypes.APPLICATION_M3U8
                            "dash" -> MimeTypes.APPLICATION_MPD
                            else -> stream.mimeType
                        }
                        setMediaItem(MediaItem.Builder().setUri(requireNotNull(stream.streamUrl)).setMimeType(mime).build())
                        prepare()
                        playWhenReady = true
                    }
            }
            check(prepared.await(30, TimeUnit.SECONDS)) { "Decoder preparation timed out" }
            check(failure.get() == null) { failure.get()?.errorCodeName.orEmpty() }
            runOnMainSync {
                val format = player?.audioFormat
                record("$label: tier=${tier.ifBlank { "default" }} DECODING ${format?.sampleMimeType}, rate=${format?.sampleRate}, channels=${format?.channelCount}")
                seeking = true
                player?.seekTo(20_000)
            }
            check(sought.await(30, TimeUnit.SECONDS)) { "Playback did not recover after seeking" }
            check(failure.get() == null) { failure.get()?.errorCodeName.orEmpty() }
            record("$label: tier=${tier.ifBlank { "default" }} decoder seek OK")
        } finally {
            runOnMainSync { player?.release() }
            client.connectionPool.evictAll()
            client.dispatcher.executorService.shutdown()
        }
    }
}
