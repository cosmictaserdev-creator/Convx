/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * REST and WebSocket client for the party server.
 */

package com.convx.music.jam

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** A refusal from the party server, or a transport failure turned into one. */
class JamException(val code: String, message: String) : IOException(message)

class JamClient(
    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .build(),
) {
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false }

    private val jsonType = "application/json".toMediaType()

    /** A free-tier server sleeps when idle; this wakes it before the user commits to a name and a code. */
    suspend fun warm(baseUrl: String) {
        withContext(Dispatchers.IO) {
            try { execute(Request.Builder().url("${base(baseUrl)}/api/time").build()) } catch (_: IOException) { }
        }
    }

    suspend fun create(baseUrl: String, request: JoinRequest): PartyMembership =
        post("${base(baseUrl)}/api/parties", json.encodeToString(request))

    suspend fun join(baseUrl: String, code: String, request: JoinRequest): PartyMembership =
        post("${base(baseUrl)}/api/parties/${code.trim()}/join", json.encodeToString(request))

    /** Who is in a party, before committing a device slot. Null when the code is unknown or unreachable. */
    suspend fun preview(baseUrl: String, code: String): PartyPreview? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url("${base(baseUrl)}/api/parties/${code.trim()}/preview").build()
        try {
            val result = execute(request)
            if (result.status in 200..299) json.decodeFromString<PartyPreview>(result.body) else null
        } catch (e: IOException) {
            null
        } catch (e: SerializationException) {
            null
        }
    }

    /** Parties and listeners on the server right now; null when it cannot be reached. Doubles as a health check. */
    suspend fun stats(baseUrl: String): PartyStats? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url("${base(baseUrl)}/").build()
        try {
            val result = execute(request)
            if (result.status in 200..299) json.decodeFromString<PartyStats>(result.body) else null
        } catch (e: IOException) {
            null
        } catch (e: SerializationException) {
            null
        }
    }

    suspend fun leave(baseUrl: String, code: String, token: String) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("${base(baseUrl)}/api/parties/$code/leave")
                .header("Authorization", "Bearer $token")
                .post("{}".toRequestBody(jsonType))
                .build()
            try { execute(request) } catch (_: IOException) { }
        }
    }

    /** Opens the party socket; the token travels in the handshake header, never the URL. */
    fun open(baseUrl: String, code: String, token: String, listener: WebSocketListener): WebSocket {
        val wsUrl = base(baseUrl).replaceFirst("http", "ws") + "/ws/parties/$code"
        val request = Request.Builder().url(wsUrl).header("Authorization", "Bearer $token").build()
        return http.newWebSocket(request, listener)
    }

    fun send(socket: WebSocket, frame: JsonObject): Boolean = socket.send(frame.toString())

    private suspend fun post(url: String, body: String): PartyMembership = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).post(body.toRequestBody(jsonType)).build()
        val response = try {
            execute(request)
        } catch (e: IOException) {
            throw JamException("unreachable", "Could not reach the party server")
        }
        response.let {
            val text = it.body
            if (it.status !in 200..299) {
                val error = runCatching { json.decodeFromString<ApiError>(text) }.getOrNull()
                throw JamException(error?.code ?: "http_${it.status}", error?.message?.ifBlank { null } ?: describe(it.status))
            }
            try {
                json.decodeFromString<PartyMembership>(text)
            } catch (_: SerializationException) {
                throw JamException("invalid_response", "The party server returned an invalid response")
            }
        }
    }

    private data class HttpResult(val status: Int, val body: String)

    /** Cancellation closes the underlying call, including a response body still being read. */
    private suspend fun execute(request: Request): HttpResult = suspendCancellableCoroutine { continuation ->
        val call = http.newCall(request)
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = response.use { HttpResult(it.code, it.body?.string().orEmpty()) }
                    if (continuation.isActive) continuation.resume(result)
                } catch (e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }
            }
        })
    }

    private fun describe(status: Int) = when (status) {
        404 -> "No party with that code"
        409 -> "That party is full"
        422 -> "Name and device are required"
        429 -> "Too many requests, try again in a minute"
        else -> "The party server answered $status"
    }

    private fun base(url: String): String = url.trim().trimEnd('/')
}
