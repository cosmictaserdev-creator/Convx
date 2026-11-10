/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Builds and reads the two request shapes the AI providers speak: OpenAI-style chat completions
 * (Groq, OpenRouter, OpenAI, Gemini, xAI, Perplexity, Mistral) and Anthropic's Messages API (Claude).
 * Every AI feature goes through here, so a provider works for all of them or none.
 */
object AiRequests {
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private const val ANTHROPIC_VERSION = "2023-06-01"

    /** Anthropic's thinking models count thinking against max_tokens, so the reply needs room after it. */
    private const val ANTHROPIC_MIN_TOKENS = 4096
    private const val ANTHROPIC_DEFAULT_TOKENS = 16000

    /** Models that accept the server-side fallback, which reruns a declined request on another model. */
    private val FALLBACK_MODELS = setOf("claude-opus-5-5", "claude-sonnet-5-5", "claude-opus-5", "claude-fable-5-1")

    fun isAnthropic(baseUrl: String): Boolean = baseUrl.trim().toHttpUrlOrNull()?.host == "api.anthropic.com"

    /** One system and one user message; [maxTokens] null leaves the provider's own limit. */
    fun chat(
        baseUrl: String,
        apiKey: String,
        model: String,
        system: String,
        user: String,
        maxTokens: Int? = null,
        temperature: Double? = null,
        stream: Boolean = false,
    ): Request {
        val builder = Request.Builder().url(baseUrl.trim())
        val body = if (isAnthropic(baseUrl)) {
            builder.header("x-api-key", apiKey.trim()).header("anthropic-version", ANTHROPIC_VERSION)
            val fallback = model in FALLBACK_MODELS
            if (fallback) builder.header("anthropic-beta", "server-side-fallback-2026-07-01")
            buildJsonObject {
                put("model", model)
                put("max_tokens", maxTokens?.coerceAtLeast(ANTHROPIC_MIN_TOKENS) ?: ANTHROPIC_DEFAULT_TOKENS)
                put("system", system)
                put("messages", buildJsonArray { add(message("user", user)) })
                if (stream) put("stream", true)
                // A refusal is retried on a model chosen for its category instead of ending the request.
                if (fallback) put("fallbacks", "default")
                // Sampling parameters are rejected by the Messages API, so temperature is not sent.
            }
        } else {
            if (apiKey.isNotBlank()) builder.header("Authorization", "Bearer ${apiKey.trim()}")
            builder.header("HTTP-Referer", "https://github.com/cosmictaserdev-creator/Convx").header("X-Title", "convx")
            buildJsonObject {
                if (model.isNotBlank()) put("model", model)
                put("messages", buildJsonArray {
                    add(message("system", system))
                    add(message("user", user))
                })
                temperature?.let { put("temperature", it) }
                maxTokens?.let { put("max_tokens", it) }
                if (stream) put("stream", true)
            }
        }
        return builder.post(body.toString().toRequestBody(JSON)).build()
    }

    private fun message(role: String, content: String) = buildJsonObject {
        put("role", role)
        put("content", content)
    }

    /** The reply's text, or null when there is none: an empty answer, or a request Claude declined. */
    fun text(responseBody: String): String? {
        val root = runCatching { json.parseToJsonElement(responseBody).jsonObject }.getOrNull() ?: return null
        val blocks = root["content"] as? JsonArray
        if (blocks != null) {
            if (root.string("stop_reason") == "refusal") return null
            // Thinking and fallback blocks come first; only text blocks are the answer.
            return blocks.mapNotNull { (it as? JsonObject)?.takeIf { b -> b.string("type") == "text" }?.string("text") }
                .joinToString("").takeIf { it.isNotBlank() }
        }
        val choice = (root["choices"] as? JsonArray)?.firstOrNull() as? JsonObject ?: return null
        return (choice["message"] as? JsonObject)?.string("content")?.takeIf { it.isNotBlank() }
    }

    /** Text carried by one server-sent `data:` payload, from either shape; null for anything else. */
    fun streamText(data: String): String? {
        val root = runCatching { json.parseToJsonElement(data).jsonObject }.getOrNull() ?: return null
        if (root.string("type") == "content_block_delta") {
            val delta = root["delta"] as? JsonObject ?: return null
            return delta.takeIf { it.string("type") == "text_delta" }?.string("text")
        }
        val choice = (root["choices"] as? JsonArray)?.firstOrNull() as? JsonObject ?: return null
        return (choice["delta"] as? JsonObject)?.string("content")
    }

    /** OpenAI-style streams end with `[DONE]`; Anthropic's with a `message_stop` event. */
    fun isStreamEnd(data: String): Boolean = data == "[DONE]" ||
        runCatching { json.parseToJsonElement(data).jsonObject.string("type") == "message_stop" }.getOrDefault(false)

    /**
     * The JSON array or object inside a reply. Models asked for bare JSON still sometimes add a
     * code fence, a sentence, or wrap the list in an object, so this takes the outermost
     * brackets of [open]; for an array wrapped in an object, the first array inside it.
     */
    fun jsonPart(reply: String, open: Char): String {
        val close = if (open == '[') ']' else '}'
        val cleaned = reply.replace("```json", "").replace("```", "").trim()
        val start = cleaned.indexOf(open)
        val end = cleaned.lastIndexOf(close)
        return if (start in 0 until end) cleaned.substring(start, end + 1) else cleaned
    }

    /** The provider's own explanation of a failed request; both shapes put it at `error.message`. */
    fun errorMessage(responseBody: String, code: Int, status: String): String =
        runCatching { (json.parseToJsonElement(responseBody).jsonObject["error"] as? JsonObject)?.string("message") }
            .getOrNull()?.takeIf { it.isNotBlank() } ?: "HTTP $code${status.takeIf { it.isNotBlank() }?.let { ": $it" } ?: ""}"

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeUnless { it is JsonNull }?.contentOrNull
}
