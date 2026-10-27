package com.convx.music.modules

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

internal expect fun moduleHttpClient(): HttpClient

internal fun io.ktor.client.HttpClientConfig<*>.configureModuleHttp() {
    followRedirects = false
    expectSuccess = false
    install(HttpTimeout) {
        requestTimeoutMillis = 20_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 15_000
    }
}
