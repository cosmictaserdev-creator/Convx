package com.convx.music.client.desktop

import com.convx.music.playback.PlaybackRequestTrack
import com.music.innertube.InnertubeAudioStreamResolver
import com.music.innertube.YouTubeRepository
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URI
import java.io.File
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first

/** Explicit local diagnostic; never run by the unit-test task and never prints session data or stream URLs. */
internal object DesktopLiveStreamProbe {
    @JvmStatic fun main(args: Array<String>) = runBlocking {
        require(args.size >= 3) { "Pass a public video ID, title, and artist." }
        val repository = YouTubeRepository()
        val encoded = DesktopSecureSessionStore().read()
        if (encoded != null) {
            val session = Json.parseToJsonElement(encoded).jsonObject
            repository.cookie = session["cookie"]?.jsonPrimitive?.content
            repository.visitorData = session["visitorData"]?.jsonPrimitive?.content
            repository.dataSyncId = session["dataSyncId"]?.jsonPrimitive?.content
        }
        println("Session available: ${repository.cookie != null}")
        val provider = DesktopWebPoTokenProvider { println("Token worker failed: ${it.safeDiagnostic()}") }
        val result = InnertubeAudioStreamResolver(repository, provider).resolve(PlaybackRequestTrack("youtube", args[0], args[1], listOf(args[2]), args.getOrNull(3)?.toLongOrNull(), null))
        result.fold(onSuccess = { stream ->
            println("Resolved: ${stream.mimeType}, duration=${stream.durationMs}, bitrate=${stream.bitrate}")
            val connection = URI(stream.url).toURL().openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000; connection.readTimeout = 10_000
            connection.setRequestProperty("Range", "bytes=0-1023")
            connection.setRequestProperty("User-Agent", com.music.innertube.models.YouTubeClient.USER_AGENT_WEB)
            connection.setRequestProperty("Referer", "https://music.youtube.com/")
            try { println("Audio HTTP ${connection.responseCode}; range=${connection.getHeaderField("Content-Range")?.substringBefore('/')}") }
            finally { connection.disconnect() }
        }, onFailure = { error ->
            println("Resolution failed: ${error::class.simpleName}: ${error.message.orEmpty().safeDiagnostic()}")
        })
        args.getOrNull(4)?.let { folder ->
            val directory = File(folder).apply { mkdirs() }
            val library = DesktopMediaLibrary(File(directory, "probe-index.json"), File(directory, "art"), RecordingLibraryWriter())
            val manager = DesktopDownloadManager(InnertubeAudioStreamResolver(repository, provider), library, File(directory, "audio"))
            try {
                manager.enqueue(listOf(PlaybackRequestTrack("youtube_music", args[0], args[1], listOf(args[2]), args[3].toLong(), null))).join()
                val final = withTimeout(120_000) { library.state.first { it.index.downloads.any { download -> download.status in listOf(DesktopDownloadStatus.COMPLETE, DesktopDownloadStatus.FAILED) } } }
                val download = final.index.downloads.single()
                println("Download ${download.status}, bytes=${download.receivedBytes}, message=${download.message}")
            } finally { manager.close(); library.close() }
        }
    }
}

private fun String.safeDiagnostic() = replace(Regex("https?://\\S+"), "[URL]").take(250)
