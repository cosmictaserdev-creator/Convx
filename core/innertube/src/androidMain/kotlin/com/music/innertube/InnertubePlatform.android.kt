package com.music.innertube

import com.music.innertube.models.IpVersion
import com.music.innertube.models.response.PlayerResponse
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.Protocol
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.Proxy
import java.net.URI
import java.util.concurrent.TimeUnit

internal actual fun createInnertubeHttpClientEngine(settings: InnertubeNetworkSettings): HttpClientEngine =
    OkHttp.create {
        config {
            connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            retryOnConnectionFailure(false)
            dns(object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    val addresses = Dns.SYSTEM.lookup(hostname)
                    return when (settings.ipVersion) {
                        IpVersion.IPV4 -> addresses.filterIsInstance<Inet4Address>().ifEmpty { addresses }
                        IpVersion.IPV6 -> addresses.filterIsInstance<Inet6Address>().ifEmpty { addresses }
                        IpVersion.AUTO -> addresses
                    }
                }
            })
            settings.proxy?.let { proxy ->
                proxy(proxy.toJavaProxy())
            }
            settings.proxyAuth?.let { authorization ->
                proxyAuthenticator { _, response ->
                    response.request.newBuilder()
                        .header("Proxy-Authorization", authorization)
                        .build()
                }
            }
        }
    }

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
