/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.api

import com.convx.music.ai.AiDefaults
import com.convx.music.ai.AiRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import timber.log.Timber

object OpenRouterStreamingService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun streamTranslation(
        text: String,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        mode: String
    ): Flow<StreamChunk> = flow {
        if (text.isBlank()) {
            emit(StreamChunk.Error("Input text is empty"))
            return@flow
        }

        val lineCount = text.lines().size
        
        Timber.d("Starting streaming translation for $lineCount lines")

        try {
            val request = buildRequest(text, targetLanguage, apiKey, baseUrl, model, mode, lineCount)
            client.newCall(request).execute().use { response ->
                Timber.d("Got streaming response: ${response.code}")

                if (!response.isSuccessful) {
                    emit(StreamChunk.Error("Translation failed: ${responseError(response)}"))
                    return@flow
                }

                val reader = BufferedReader(InputStreamReader(response.body.byteStream()))
                val content = readStream(reader, lineCount)
                if (!content.sawDoneMarker && content.text.isNotEmpty()) {
                    Timber.w("Stream ended without [DONE] marker, attempting to parse content")
                    emitTranslationResult(content.text, lineCount)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Streaming error")
            emit(StreamChunk.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    private fun buildRequest(
        text: String,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        mode: String,
        lineCount: Int,
    ): Request {
        val systemPrompt = """You are a precise lyrics translation assistant. Your output must ALWAYS be a valid JSON array of strings.

CRITICAL RULES:
1. Output ONLY a JSON array: ["line1", "line2", "line3"]
2. NO explanations, NO questions, NO additional text
3. Each input line maps to exactly one output line
4. Preserve empty lines as empty strings ""
5. Return EXACTLY $lineCount items in the array
6. If uncertain, provide best approximation but maintain line count"""

        val userPrompt = when (mode) {
            "Transcribed" -> """Transcribe/transliterate the following $lineCount lines phonetically into $targetLanguage script.

CRITICAL REQUIREMENTS:
- Convert the SOUND/PRONUNCIATION of the original text into $targetLanguage script
- DO NOT translate the meaning - only represent how the original words SOUND
- Use the native script of $targetLanguage
- Preserve the original pronunciation as closely as possible
- Keep punctuation and formatting

Input ($lineCount lines):
$text

Output MUST be a JSON array with EXACTLY $lineCount strings."""

            else -> """Translate the following $lineCount lines to $targetLanguage.

IMPORTANT:
- Provide natural, accurate translation
- Maintain poetic flow and meaning
- Keep punctuation appropriate for target language
- Preserve line-by-line structure exactly

Input ($lineCount lines):
$text

Output MUST be a JSON array with EXACTLY $lineCount strings."""
        }

        return AiRequests.chat(
            baseUrl = baseUrl.ifBlank { AiDefaults.BASE_URL },
            apiKey = apiKey,
            model = model,
            system = systemPrompt,
            user = userPrompt,
            // Reasoning models such as Groq's gpt-oss think before any text arrives.
            maxTokens = maxOf(lineCount * 100, 4096),
            temperature = 0.3,
            stream = true,
        )
    }

    private suspend fun FlowCollector<StreamChunk>.readStream(
        reader: BufferedReader,
        expectedLineCount: Int,
    ): StreamContent {
        val content = StringBuilder()
        var chunkCount = 0

        while (true) {
            val line = reader.readLine() ?: break
            if (!line.startsWith("data: ")) continue

            val data = line.substring(6)
            // Anthropic reports a failure that happens mid-stream as an error event.
            if (data.contains("\"type\":\"error\"")) {
                emit(StreamChunk.Error(AiRequests.errorMessage(data, 0, "stream error")))
                return StreamContent("", sawDoneMarker = true)
            }
            if (AiRequests.isStreamEnd(data)) {
                Timber.d("Streaming complete, received $chunkCount chunks")
                val fullContent = content.toString()
                emitTranslationResult(fullContent, expectedLineCount, logDetails = true)
                return StreamContent(fullContent, sawDoneMarker = true)
            }

            val chunk = AiRequests.streamText(data) ?: continue
            content.append(chunk)
            chunkCount++
            emit(StreamChunk.Content(chunk))
        }

        return StreamContent(content.toString(), sawDoneMarker = false)
    }

    private suspend fun FlowCollector<StreamChunk>.emitTranslationResult(
        content: String,
        expectedLineCount: Int,
        logDetails: Boolean = false,
    ) {
        if (logDetails) Timber.d("Full content length: ${content.length}")

        parseTranslationContent(content, expectedLineCount)
            .onSuccess { translatedLines ->
                if (logDetails) Timber.d("Successfully parsed ${translatedLines.size} lines")
                emit(StreamChunk.Complete(translatedLines))
            }
            .onFailure { error ->
                if (logDetails) Timber.e(error, "Failed to parse translation")
                emit(StreamChunk.Error(error.message ?: "Parsing failed"))
            }
    }

    private fun responseError(response: okhttp3.Response): String =
        AiRequests.errorMessage(runCatching { response.body.string() }.getOrDefault(""), response.code, response.message)

    private data class StreamContent(val text: String, val sawDoneMarker: Boolean)

    private fun parseTranslationContent(content: String, expectedLineCount: Int): Result<List<String>> {
        val cleanedContent = content.replace("```json", "").replace("```", "").trim()
        val extractedArray = extractJsonArray(cleanedContent)

        val translatedLines = parseJsonArray(content.trim())
            ?: parseJsonArray(cleanedContent)
            ?: extractedArray?.let(::parseJsonArray)
            ?: extractedArray?.let { parsePlainLines(cleanedContent) }
            ?: return Result.failure(Exception("Failed to parse translation"))

        return Result.success(fitLineCount(translatedLines, expectedLineCount))
    }

    sealed class StreamChunk {
        data class Content(val text: String) : StreamChunk()
        data class Complete(val translatedLines: List<String>) : StreamChunk()
        data class Error(val message: String) : StreamChunk()
    }
}
