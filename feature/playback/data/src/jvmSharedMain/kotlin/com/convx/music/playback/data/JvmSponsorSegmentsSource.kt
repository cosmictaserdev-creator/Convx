package com.convx.music.playback.data

import com.convx.music.playback.SponsorCategory
import com.convx.music.playback.SponsorSegment
import com.convx.music.playback.SponsorSegmentsSource
import com.convx.music.playback.decodeSponsorSegments
import com.convx.music.playback.isSponsorVideoId
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/** Android and desktop share the same bounded, cancellable hash-prefix request and session cache. */
class JvmSponsorSegmentsSource(
    private val client: OkHttpClient = defaultClient,
    private val endpoint: HttpUrl = "https://sponsor.ajay.app/api/skipSegments/".toHttpUrl(),
) : SponsorSegmentsSource {
    private data class Key(val id: String, val categories: Set<SponsorCategory>)
    private val cacheLock = Mutex()
    private val cache = object : LinkedHashMap<Key, List<SponsorSegment>>(32, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Key, List<SponsorSegment>>) = size > 32
    }

    override suspend fun segments(videoId: String, categories: Set<SponsorCategory>): List<SponsorSegment>? {
        if (!isSponsorVideoId(videoId) || categories.isEmpty()) return emptyList()
        val key = Key(videoId, categories.toSet())
        cacheLock.withLock { cache[key]?.let { return it } }
        val prefix = MessageDigest.getInstance("SHA-256").digest(videoId.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }.take(4)
        val url = endpoint.newBuilder().addPathSegment(prefix)
            .addQueryParameter("categories", key.categories.sortedBy { it.ordinal }
                .joinToString(",", "[", "]") { "\"${it.apiName}\"" })
            .addQueryParameter("actionType", "skip").build()
        val fetched = fetch(Request.Builder().url(url).header("User-Agent", "Convx Music").build(), videoId)
            ?: return null
        cacheLock.withLock { cache[key] = fetched }
        return fetched
    }

    private suspend fun fetch(request: Request, videoId: String): List<SponsorSegment>? = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { continuation.resumeWith(Result.success(null)) }
            override fun onResponse(call: Call, response: Response) {
                val result = response.use {
                    if (!continuation.isActive) return@use null
                    when {
                        response.code == 404 -> emptyList()
                        !response.isSuccessful -> null
                        else -> try { readBoundedBody(response)?.let { decodeSponsorSegments(it, videoId) } }
                        catch (_: IOException) { null }
                    }
                }
                continuation.resumeWith(Result.success(result))
            }
        })
    }

    private fun readBoundedBody(response: Response): String? {
        if (response.body.contentLength() > MAX_RESPONSE_BYTES) return null
        val bytes = ByteArrayOutputStream()
        response.body.byteStream().use { stream ->
            val buffer = ByteArray(8_192)
            while (true) {
                val count = stream.read(buffer)
                if (count == -1) break
                if (bytes.size() + count > MAX_RESPONSE_BYTES) return null
                bytes.write(buffer, 0, count)
            }
        }
        return bytes.toString(Charsets.UTF_8.name())
    }

    private companion object {
        const val MAX_RESPONSE_BYTES = 1_048_576
        val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder().callTimeout(8, TimeUnit.SECONDS).build()
        }
    }
}
