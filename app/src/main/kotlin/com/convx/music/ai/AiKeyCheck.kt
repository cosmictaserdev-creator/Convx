/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/** Tries a key against its provider before it is saved, with the same request the features send. */
object AiKeyCheck {
    sealed interface Result {
        data class Works(val detail: String) : Result
        data class Fails(val reason: String) : Result
    }

    private val client = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()

    suspend fun verify(provider: String, baseUrl: String, model: String, apiKey: String): Result =
        withContext(Dispatchers.IO) {
            val key = apiKey.trim()
            if (key.isEmpty()) return@withContext Result.Fails("The key is empty.")
            runCatching {
                if (provider == "DeepL") deepL(key) else chat(provider, baseUrl, model, key)
            }.getOrElse { Result.Fails("Couldn't reach $provider: ${it.message ?: it.javaClass.simpleName}") }
        }

    private fun chat(provider: String, baseUrl: String, model: String, key: String): Result {
        if (baseUrl.isBlank()) return Result.Fails("Set the base URL first.")
        val started = System.currentTimeMillis()
        val request = AiRequests.chat(baseUrl, key, model, "You check that a connection works.", "Reply with the single word OK.", maxTokens = 256)
        client.newCall(request).execute().use { response ->
            val body = response.body.string()
            val reason = AiRequests.errorMessage(body, response.code, response.message)
            val ms = System.currentTimeMillis() - started
            val name = model.ifBlank { provider }
            return when {
                response.isSuccessful -> Result.Works("$provider answered with $name in $ms ms.")
                // The key was accepted; the account is only out of requests for the moment.
                response.code == 429 -> Result.Works("$provider accepted the key but is rate-limiting it right now: $reason")
                response.code == 401 || response.code == 403 -> Result.Fails("$provider rejected this key. $reason")
                response.code == 404 -> Result.Fails("$provider doesn't offer $name to this key, or the address is wrong. $reason")
                else -> Result.Fails("$provider returned an error. $reason")
            }
        }
    }

    /** DeepL has no chat endpoint; its usage endpoint answers only to a valid key. */
    private fun deepL(key: String): Result {
        val host = if (key.endsWith(":fx")) "https://api-free.deepl.com" else "https://api.deepl.com"
        val request = Request.Builder().url("$host/v2/usage").header("Authorization", "DeepL-Auth-Key $key").build()
        client.newCall(request).execute().use { response ->
            return when {
                response.isSuccessful -> Result.Works("DeepL accepted the key.")
                response.code == 403 -> Result.Fails("DeepL rejected this key. Free keys end in :fx.")
                else -> Result.Fails("DeepL returned HTTP ${response.code}.")
            }
        }
    }
}
