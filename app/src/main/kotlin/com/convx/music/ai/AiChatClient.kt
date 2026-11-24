/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import android.content.Context
import com.convx.music.constants.AiProviderKey
import com.convx.music.constants.OpenRouterApiKey
import com.convx.music.constants.OpenRouterBaseUrlKey
import com.convx.music.constants.OpenRouterModelKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException
import okhttp3.OkHttpClient

/** Sends chat-completions requests using the configured provider URL, model, and API key. */
object AiChatClient {
    private val client = OkHttpClient.Builder().callTimeout(120, TimeUnit.SECONDS).build()

    /** Returns the assistant response, or null if the request fails. */
    suspend fun complete(
        context: Context,
        systemPrompt: String,
        userPrompt: String,
        onLog: suspend (String) -> Unit,
    ): String? = withContext(Dispatchers.IO) {
        val provider = context.dataStore.get(AiProviderKey, AiDefaults.PROVIDER)
        if (provider == "Puter") {
            onLog("Puter is not implemented yet.")
            return@withContext null
        }
        if (provider in AiDefaults.TRANSLATION_ONLY) {
            onLog("$provider only translates lyrics. Pick a chat provider such as Groq in Settings → AI.")
            return@withContext null
        }

        val apiKey = context.dataStore.get(OpenRouterApiKey, "")
        val baseUrl = context.dataStore.get(OpenRouterBaseUrlKey, AiDefaults.BASE_URL)
        val model = context.dataStore.get(OpenRouterModelKey, AiDefaults.MODEL)
        onLog("Using $provider / $model")
        if (baseUrl.isBlank()) {
            onLog("No address is set for $provider. Set the base URL in Settings → AI.")
            return@withContext null
        }

        if (apiKey.isEmpty()) {
            onLog("API Key is missing. Please set it in Settings.")
            return@withContext null
        }

        try {
            val request = AiRequests.chat(baseUrl, apiKey, model, systemPrompt, userPrompt)
            client.newCall(request).awaitResponse().use { response ->
                val responseString = response.body.string()
                if (!response.isSuccessful) {
                    // The provider's own message says what to fix: a wrong model, a bad key, a rate limit.
                    onLog("AI request failed: ${AiRequests.errorMessage(responseString, response.code, response.message)}")
                    return@withContext null
                }
                currentCoroutineContext().ensureActive()
                AiRequests.text(responseString) ?: run {
                    onLog("$provider returned no answer.")
                    null
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: Exception) {
            onLog("Network error: ${e.message}")
            null
        }
    }

    private suspend fun Call.awaitResponse(): Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        enqueue(object : Callback {
            override fun onFailure(call: Call, error: IOException) {
                if (continuation.isActive) continuation.resumeWithException(error)
            }
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response) { _, value, _ -> value.close() }
            }
        })
    }
}
