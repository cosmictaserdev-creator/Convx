/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * REST and WebSocket client for the party server.
 */

package com.convx.music.jam

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.TimeUnit

/** A refusal from the party server, or a transport failure turned into one. */
class JamException(val code: String, message: String) : IOException(message)

class JamClient(
    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // the socket stays open
        .pingInterval(25, TimeUnit.SECONDS)
        .build(),
) {
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false }

    private val jsonType = "application/json".toMediaType()

    /** A free-tier server sleeps when idle; this wakes it before the user commits to a name and a code. */
    suspend fun warm(baseUrl: String) {
        withContext(Dispatchers.IO) {
            runCatching { http.newCall(Request.Builder().url("${base(baseUrl)}/api/time").build()).execute().close() }
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
            http.newCall(request).execute().use {
                if (it.isSuccessful) json.decodeFromString<PartyPreview>(it.body?.string().orEmpty()) else null
            }
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
            http.newCall(request).execute().use {
                if (it.isSuccessful) json.decodeFromString<PartyStats>(it.body?.string().orEmpty()) else null
            }
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
            runCatching { http.newCall(request).execute().close() }
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
            http.newCall(request).execute()
        } catch (e: IOException) {
            throw JamException("unreachable", "Could not reach the party server")
        }
        response.use {
            val text = it.body?.string().orEmpty()
            if (!it.isSuccessful) {
                val error = runCatching { json.decodeFromString<ApiError>(text) }.getOrNull()
                throw JamException(error?.code ?: "http_${it.code}", error?.message?.ifBlank { null } ?: describe(it.code))
            }
            json.decodeFromString<PartyMembership>(text)
        }
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
