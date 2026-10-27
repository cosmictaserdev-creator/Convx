package com.music.innertube

import com.music.innertube.models.response.PlayerResponse
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual fun createInnertubeHttpClientEngine(settings: InnertubeNetworkSettings): HttpClientEngine = Darwin.create()

internal actual object PlatformStreamResolver {
    actual suspend fun signatureTimestamp(videoId: String): Result<Int> =
        IosPlayerScriptResolver.signatureTimestamp(videoId)

    actual suspend fun streamUrl(format: PlayerResponse.StreamingData.Format, videoId: String): String? =
        IosPlayerScriptResolver.streamUrl(format, videoId)

    actual suspend fun newPipePlayer(videoId: String): List<Pair<Int, String>> = emptyList()
}
