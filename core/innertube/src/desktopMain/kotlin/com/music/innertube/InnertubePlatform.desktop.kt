package com.music.innertube

import com.music.innertube.models.response.PlayerResponse
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal actual fun createInnertubeHttpClientEngine(settings: InnertubeNetworkSettings): HttpClientEngine = CIO.create()

internal actual object PlatformStreamResolver {
    actual suspend fun signatureTimestamp(videoId: String): Result<Int> = withContext(Dispatchers.IO) {
        NewPipeExtractor.getSignatureTimestamp(videoId)
    }

    actual suspend fun streamUrl(format: PlayerResponse.StreamingData.Format, videoId: String): String? =
        withContext(Dispatchers.IO) { NewPipeExtractor.getStreamUrl(format, videoId) }

    actual suspend fun newPipePlayer(videoId: String): List<Pair<Int, String>> = withContext(Dispatchers.IO) {
        NewPipeExtractor.newPipePlayer(videoId)
    }
}
