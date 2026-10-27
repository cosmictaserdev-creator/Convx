package com.music.innertube

import com.music.innertube.models.IpVersion
import com.music.innertube.models.response.PlayerResponse
import io.ktor.client.engine.HttpClientEngine

/** Network configuration that can be carried safely through common code. */
data class InnertubeProxySettings(
    val type: ProxyType,
    val host: String,
    val port: Int,
)

enum class ProxyType { HTTP, SOCKS }

internal data class InnertubeNetworkSettings(
    val proxy: InnertubeProxySettings?,
    val proxyAuth: String?,
    val ipVersion: IpVersion,
)

internal expect fun createInnertubeHttpClientEngine(settings: InnertubeNetworkSettings): HttpClientEngine

/** Platform stream resolvers own JavaScript signature decoding and native media URL handling. */
internal expect object PlatformStreamResolver {
    suspend fun signatureTimestamp(videoId: String): Result<Int>
    suspend fun streamUrl(format: PlayerResponse.StreamingData.Format, videoId: String): String?
    suspend fun newPipePlayer(videoId: String): List<Pair<Int, String>>
}
