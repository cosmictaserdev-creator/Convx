package com.music.innertube

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit

/** Shared HTTP settings for YouTube API clients. */
object NetworkConfig {

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val WRITE_TIMEOUT_SECONDS = 60L
    private const val REQUEST_TIMEOUT_MILLIS = 60000L
    
    private const val CACHE_SIZE_BYTES = 50L * 1024L * 1024L // 50 MiB
    
    @OptIn(ExperimentalSerializationApi::class)
    fun createOptimizedHttpClient(
        cacheDir: File? = null,
        enableCache: Boolean = true
    ): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
                isLenient = true
            })
        }

        install(ContentEncoding) {
            gzip(0.9F)
            deflate(0.8F)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_SECONDS * 1000
            socketTimeoutMillis = READ_TIMEOUT_SECONDS * 1000
        }

        engine {
            config {
                connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                
                retryOnConnectionFailure(true)
                
                if (enableCache) {
                    val cacheDirectory = cacheDir ?: File(System.getProperty("java.io.tmpdir"), "vivimusic_http_cache")
                    cache(okhttp3.Cache(cacheDirectory, CACHE_SIZE_BYTES))
                }
            }
        }
    }
    
    fun createYouTubeMusicClient(cacheDir: File? = null): HttpClient =
        createOptimizedHttpClient(cacheDir)

    fun getAdaptiveTimeouts(networkQuality: NetworkQuality): TimeoutConfig =
        when (networkQuality) {
            NetworkQuality.EXCELLENT -> TimeoutConfig(
                connectTimeout = 10000L,
                readTimeout = 30000L,
                requestTimeout = 45000L
            )
            NetworkQuality.GOOD -> TimeoutConfig(
                connectTimeout = 20000L,
                readTimeout = 45000L,
                requestTimeout = 60000L
            )
            NetworkQuality.POOR -> TimeoutConfig(
                connectTimeout = 30000L,
                readTimeout = 60000L,
                requestTimeout = 90000L
            )
            NetworkQuality.UNKNOWN -> TimeoutConfig(
                connectTimeout = CONNECT_TIMEOUT_SECONDS * 1000,
                readTimeout = READ_TIMEOUT_SECONDS * 1000,
                requestTimeout = REQUEST_TIMEOUT_MILLIS
            )
        }
    
    enum class NetworkQuality {
        EXCELLENT, GOOD, POOR, UNKNOWN
    }
    
    data class TimeoutConfig(
        val connectTimeout: Long,
        val readTimeout: Long,
        val requestTimeout: Long
    )
}
