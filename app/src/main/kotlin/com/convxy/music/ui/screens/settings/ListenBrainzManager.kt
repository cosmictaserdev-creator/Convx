/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

object ListenBrainzManager {
    private val logTag = "ListenBrainzManager"
    private val started = AtomicBoolean(false)
    private var scope: CoroutineScope? = null
    private var job: Job? = null
    private var lifecycleObserver: Any? = null
    private val httpClient = OkHttpClient()

    private val _lastSubmitTime = MutableStateFlow<Long?>(null)
    val lastSubmitTimeFlow = _lastSubmitTime.asStateFlow()

    suspend fun submitPlayingNow(context: Context, token: String, title: String, artistNames: String, releaseName: String, durationMs: Long, positionMs: Long): Boolean {
        if (token.isBlank()) return false
        if (title.isBlank()) return false
        return withContext(Dispatchers.IO) {
            try {
                val releasePart = if (releaseName.isBlank()) "" else "\"release_name\":\"${escapeJson(releaseName)}\","
                val trackMetadata = "{\"track_metadata\":{\"artist_name\":\"${escapeJson(
                    artistNames,
                )}\",\"track_name\":\"${escapeJson(
                    title,
                )}\",${releasePart}\"additional_info\":{\"duration_ms\":$durationMs,\"position_ms\":$positionMs,\"submission_client\":\"Convxy Music\",\"submission_client_version\":\"1.5\"}}}"
                val listensJson = "[$trackMetadata]"
                val bodyJson = "{\"listen_type\":\"playing_now\",\"payload\":$listensJson}"
                Timber.tag(logTag).d("submitPlayingNow JSON: %s", bodyJson)
                val mediaType = "application/json".toMediaType()
                val body = bodyJson.toRequestBody(mediaType)
                val request =
                    Request
                        .Builder()
                        .url("https://api.listenbrainz.org/1/submit-listens")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Token $token")
                        .addHeader("User-Agent", "ConvxyMusic/1.5")
                        .build()

                httpClient.newCall(request).execute().use { resp ->
                    val success = resp.isSuccessful
                    if (success) {
                        _lastSubmitTime.value = System.currentTimeMillis()
                        Timber.tag(logTag).d("playing_now submitted for %s", title)
                    } else {
                        val respBody =
                            try {
                                resp.body?.string() ?: ""
                            } catch (e: Exception) {
                                "<unable to read>"
                            }
                        Timber.tag(logTag).w("playing_now submit failed: %s - %s", resp.code, respBody)
                    }
                    success
                }
            } catch (ex: Exception) {
                Timber.tag(logTag).e(ex, "submitPlayingNow failed")
                false
            }
        }
    }

    suspend fun submitFinished(
        context: Context,
        token: String,
        title: String,
        artistNames: String,
        releaseName: String,
        durationMs: Long,
        startMs: Long,
        endMs: Long,
    ): Boolean {
        if (token.isBlank()) return false
        if (title.isBlank()) return false
        return withContext(Dispatchers.IO) {
            try {
                val releasePart = if (releaseName.isBlank()) "" else "\"release_name\":\"${escapeJson(releaseName)}\","
                var listenedAtStart = (startMs / 1000L)
                val MIN_LISTEN_TS = 1033430400L
                if (listenedAtStart < MIN_LISTEN_TS) {
                    Timber.tag(logTag).w("listened_at %s looks too small, replacing with current epoch seconds", listenedAtStart)
                    listenedAtStart = System.currentTimeMillis() / 1000L
                }
                val trackMetadataSingle = "{\"listened_at\":$listenedAtStart,\"track_metadata\":{\"artist_name\":\"${escapeJson(
                    artistNames,
                )}\",\"track_name\":\"${escapeJson(
                    title,
                )}\",${releasePart}\"additional_info\":{\"duration_ms\":$durationMs,\"start_ms\":$startMs,\"end_ms\":$endMs,\"submission_client\":\"Convxy Music\",\"submission_client_version\":\"1.5\"}}}"
                val listensJson = "[$trackMetadataSingle]"
                val bodyJson = "{\"listen_type\":\"single\",\"payload\":$listensJson}"
                Timber.tag(logTag).d("submitFinished JSON: %s", bodyJson)
                val mediaType = "application/json".toMediaType()
                val body = bodyJson.toRequestBody(mediaType)
                val request =
                    Request
                        .Builder()
                        .url("https://api.listenbrainz.org/1/submit-listens")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Token $token")
                        .addHeader("User-Agent", "ConvxyMusic/1.5")
                        .build()

                httpClient.newCall(request).execute().use { resp ->
                    val success = resp.isSuccessful
                    if (success) {
                        _lastSubmitTime.value = System.currentTimeMillis()
                        Timber.tag(logTag).d("finished listen submitted for %s", title)
                    } else {
                        val respBody =
                            try {
                                resp.body?.string() ?: ""
                            } catch (e: Exception) {
                                "<unable to read>"
                            }
                        Timber.tag(logTag).w("finished listen submit failed: %s - %s", resp.code, respBody)
                    }
                    success
                }
            } catch (ex: Exception) {
                Timber.tag(logTag).e(ex, "submitFinished failed")
                false
            }
        }
    }

    private fun escapeJson(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

    fun isRunning(): Boolean = started.get()
}
