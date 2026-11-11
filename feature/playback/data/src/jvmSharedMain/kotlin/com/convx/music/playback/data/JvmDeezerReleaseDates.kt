package com.convx.music.playback.data

import com.convx.music.playback.DeezerDates
import com.convx.music.playback.DeezerResponses
import com.convx.music.playback.ReleaseDateSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Exact release dates from Deezer's public API (no key), which artist pages on YouTube Music do not give. */
class JvmDeezerReleaseDates(
    private val client: OkHttpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).build(),
    private val endpoint: HttpUrl = "https://api.deezer.com/".toHttpUrl(),
    private val requestGapMs: Long = REQUEST_GAP_MS,
) : ReleaseDateSource {
    override suspend fun releaseDates(artistName: String): Map<String, String> {
        if (artistName.isBlank()) return emptyMap()
        val search = get(
            endpoint.newBuilder().addPathSegments("search/artist")
                .addQueryParameter("q", artistName).addQueryParameter("limit", "10").build(),
        ) ?: return emptyMap()
        val artistId = DeezerResponses.artistId(search, artistName) ?: return emptyMap()
        delay(requestGapMs)
        val albums = get(
            endpoint.newBuilder().addPathSegments("artist/$artistId/albums").addQueryParameter("limit", "60").build(),
        ) ?: return emptyMap()
        return DeezerDates.index(DeezerResponses.albums(albums))
    }

    private suspend fun get(url: HttpUrl): String? = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(Request.Builder().url(url).build())
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWith(Result.success(null))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.use {
                    when {
                        !it.isSuccessful || it.body == null || (it.body?.contentLength() ?: 0L) > MAX_BODY_BYTES -> null
                        else -> try { it.body?.string()?.takeIf { text -> text.length <= MAX_BODY_BYTES } } catch (_: IOException) { null }
                    }
                }
                continuation.resumeWith(Result.success(body))
            }
        })
    }

    private companion object {
        const val REQUEST_GAP_MS = 250L
        const val MAX_BODY_BYTES = 1_000_000L
    }
}
