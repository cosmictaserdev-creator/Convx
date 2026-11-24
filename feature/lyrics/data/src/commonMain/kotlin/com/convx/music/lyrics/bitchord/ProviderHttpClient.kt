package com.convx.music.lyrics.bitchord

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.encodeURLParameter
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.coroutines.CancellationException

internal val providerHttpClient = HttpClient {
    expectSuccess = false
    install(HttpTimeout) {
        connectTimeoutMillis = 5_000
        socketTimeoutMillis = 8_000
        requestTimeoutMillis = 12_000
    }
}

internal suspend fun providerGet(
    url: String,
    headers: Map<String, String>,
    requestTimeoutMs: Long = 12_000,
    connectTimeoutMs: Long = 5_000,
): String? = try {
    val response = providerHttpClient.get(url) {
        headers.forEach { (name, value) -> header(name, value) }
        timeout {
            requestTimeoutMillis = requestTimeoutMs
            connectTimeoutMillis = connectTimeoutMs
        }
    }
    if (response.status.isSuccess()) response.bodyAsText() else null
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (_: Exception) {
    null
}

internal class HttpUrl(private val url: Url) {
    fun newBuilder() = Builder(URLBuilder(url))

    fun resolve(link: String): HttpUrl? = try {
        HttpUrl(URLBuilder(url).takeFrom(link).build())
    } catch (_: Exception) {
        null
    }

    override fun toString() = url.toString()

    class Builder(private val builder: URLBuilder) {
        fun addQueryParameter(name: String, value: String?) = apply {
            // Spaces as %20, as the previous client sent them; signed providers hash the exact URL.
            if (value != null) builder.encodedParameters.append(name.encodeURLParameter(), value.encodeURLParameter())
        }

        fun build() = HttpUrl(builder.build())
    }
}

internal fun String.toHttpUrl() = HttpUrl(Url(this))
