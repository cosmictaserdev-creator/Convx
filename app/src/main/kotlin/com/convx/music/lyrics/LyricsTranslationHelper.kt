/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.lyrics

import android.content.Context
import com.convx.music.api.DeepLService
import com.convx.music.api.MistralService
import com.convx.music.api.OpenRouterService
import com.convx.music.api.OpenRouterStreamingService
import com.convx.music.constants.LanguageCodeToName
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.LyricsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

object LyricsTranslationHelper {
    private val _status = MutableStateFlow<TranslationStatus>(TranslationStatus.Idle)
    val status: StateFlow<TranslationStatus> = _status.asStateFlow()

    private val _hasActiveTranslations = MutableStateFlow(false)
    val hasActiveTranslations: StateFlow<Boolean> = _hasActiveTranslations.asStateFlow()

    private val _manualTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
    )
    val manualTrigger: SharedFlow<Unit> = _manualTrigger.asSharedFlow()

    private val _clearTranslationsTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
    )
    val clearTranslationsTrigger: SharedFlow<Unit> = _clearTranslationsTrigger.asSharedFlow()

    private val _translationSaved = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
    )
    val translationSaved: SharedFlow<Unit> = _translationSaved.asSharedFlow()

    private var translationJob: Job? = null
    private var isCompositionActive = true

    private val translationCache = mutableMapOf<String, List<String>>()

    private fun getCacheKey(lyricsText: String, mode: String, language: String): String =
        "${lyricsText.hashCode()}_${mode}_$language"

    /** Reads complete string values from a partially received JSON array. */
    private fun parsePartialTranslations(content: String, maxCount: Int): List<String> {
        val arrayStart = content.indexOf('[')
        if (arrayStart == -1) return emptyList()

        val translations = mutableListOf<String>()
        var index = arrayStart + 1
        var insideString = false
        var escapeNextChar = false
        val currentTranslation = StringBuilder()

        while (index < content.length && translations.size < maxCount) {
            val char = content[index]

            when {
                escapeNextChar -> {
                    currentTranslation.append(char)
                    escapeNextChar = false
                }
                char == '\\' && insideString -> {
                    currentTranslation.append(char)
                    escapeNextChar = true
                }
                char == '"' -> {
                    if (insideString) {
                        translations.add(currentTranslation.toString())
                        currentTranslation.clear()
                        insideString = false
                    } else {
                        insideString = true
                    }
                }
                insideString -> currentTranslation.append(char)
                char == ']' -> break
            }
            index++
        }

        return translations
    }

    fun getCachedTranslations(lyrics: List<LyricsEntry>, mode: String, language: String): List<String>? {
        val lyricsText = lyrics.filter { it.text.isNotBlank() }.joinToString("\n") { it.text }
        val key = getCacheKey(lyricsText, mode, language)
        return translationCache[key]
    }

    fun applyCachedTranslations(lyrics: List<LyricsEntry>, mode: String, language: String): Boolean {
        val nonEmptyLyrics = lyrics.filter { it.text.isNotBlank() }
        val lyricsText = nonEmptyLyrics.joinToString("\n") { it.text }
        val key = getCacheKey(lyricsText, mode, language)
        val cached = translationCache[key] ?: return false

        if (cached.size < nonEmptyLyrics.size) return false

        nonEmptyLyrics.forEachIndexed { index, lyric ->
            lyric.translatedTextFlow.value = cached[index]
        }
        return true
    }

    fun triggerManualTranslation() {
        _manualTrigger.tryEmit(Unit)
    }

    fun triggerClearTranslations() {
        _hasActiveTranslations.value = false
        _clearTranslationsTrigger.tryEmit(Unit)
    }

    fun hasTranslations(lyricsEntity: LyricsEntity?): Boolean = !lyricsEntity?.translatedLyrics.isNullOrBlank()

    fun clearTranslations(lyricsEntity: LyricsEntity): LyricsEntity =
        lyricsEntity.copy(
            translatedLyrics = "",
            translationLanguage = "",
            translationMode = "",
        )

    fun resetStatus() {
        _status.value = TranslationStatus.Idle
    }

    fun clearCache() {
        translationCache.clear()
    }

    fun setCompositionActive(active: Boolean) {
        isCompositionActive = active
    }

    fun cancelTranslation() {
        isCompositionActive = false
        translationJob?.cancel()
        translationJob = null
    }

    fun loadTranslationsFromDatabase(
        lyrics: List<LyricsEntry>,
        lyricsEntity: LyricsEntity?,
        targetLanguage: String,
        mode: String,
    ) {
        // Remove stale translations before applying a matching database result.
        lyrics.forEach { it.translatedTextFlow.value = null }

        if (lyricsEntity?.translatedLyrics.isNullOrBlank()) {
            _hasActiveTranslations.value = false
            return
        }
        if (lyricsEntity.translationLanguage != targetLanguage) {
            _hasActiveTranslations.value = false
            return
        }
        if (lyricsEntity.translationMode != mode) {
            _hasActiveTranslations.value = false
            return
        }

        val translatedLines = lyricsEntity.translatedLyrics.lines()
        val nonEmptyEntries = lyrics.mapIndexedNotNull { index, entry ->
            if (entry.text.isNotBlank()) index to entry else null
        }

        nonEmptyEntries.forEachIndexed { idx, (originalIndex, _) ->
            if (idx < translatedLines.size) {
                lyrics[originalIndex].translatedTextFlow.value = translatedLines[idx]
            }
        }

        // Cache database-loaded translations so later requests can reuse them.
        val lyricsText = lyrics.filter { it.text.isNotBlank() }.joinToString("\n") { it.text }
        val cacheKey = getCacheKey(lyricsText, mode, targetLanguage)
        translationCache[cacheKey] = translatedLines
        _hasActiveTranslations.value = true
    }

    fun translateLyrics(
        lyrics: List<LyricsEntry>,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        mode: String,
        scope: CoroutineScope,
        context: Context,
        provider: String = com.convx.music.ai.AiDefaults.PROVIDER,
        deeplApiKey: String = "",
        deeplFormality: String = "default",
        useStreaming: Boolean = true,
        songId: String = "",
        database: MusicDatabase? = null,
    ) {
        translationJob?.cancel()
        _status.value = TranslationStatus.Translating

        lyrics.forEach { it.translatedTextFlow.value = null }

        translationJob = scope.launch(Dispatchers.IO) {
            try {
                val effectiveApiKey = if (provider == "DeepL") deeplApiKey else apiKey
                if (effectiveApiKey.isBlank()) {
                    _status.value = TranslationStatus.Error(context.getString(com.convx.music.R.string.ai_error_api_key_required))
                    return@launch
                }

                if (lyrics.isEmpty()) {
                    _status.value = TranslationStatus.Error(context.getString(com.convx.music.R.string.ai_error_no_lyrics))
                    return@launch
                }

                val nonEmptyEntries = lyrics.mapIndexedNotNull { index, entry ->
                    if (entry.text.isNotBlank()) index to entry else null
                }

                if (nonEmptyEntries.isEmpty()) {
                    _status.value = TranslationStatus.Error(context.getString(com.convx.music.R.string.ai_error_lyrics_empty))
                    return@launch
                }

                val fullText = nonEmptyEntries.joinToString("\n") { it.second.text }

                val cacheKey = getCacheKey(fullText, mode, targetLanguage)
                val cachedTranslations = translationCache[cacheKey]
                if (cachedTranslations != null && restoreCachedTranslations(
                        lyrics = lyrics,
                        nonEmptyEntries = nonEmptyEntries,
                        translations = cachedTranslations,
                        targetLanguage = targetLanguage,
                        mode = mode,
                        songId = songId,
                        database = database,
                    )
                ) return@launch

                if (targetLanguage.isBlank()) {
                    _status.value = TranslationStatus.Error(context.getString(com.convx.music.R.string.ai_error_language_required))
                    return@launch
                }

                val fullLanguageName = LanguageCodeToName[targetLanguage]
                    ?: try {
                        Locale.forLanguageTag(targetLanguage).displayLanguage.takeIf { it.isNotBlank() && it != targetLanguage }
                    } catch (e: Exception) { null }
                    ?: targetLanguage

                val result = translateWithProvider(
                    text = fullText,
                    targetLanguage = targetLanguage,
                    fullLanguageName = fullLanguageName,
                    apiKey = apiKey,
                    deeplApiKey = deeplApiKey,
                    deeplFormality = deeplFormality,
                    baseUrl = baseUrl,
                    model = model,
                    mode = mode,
                    provider = provider,
                    useStreaming = useStreaming,
                    expectedLineCount = nonEmptyEntries.size,
                ) { partialTranslations ->
                    partialTranslations.forEachIndexed { index, translation ->
                        if (index < nonEmptyEntries.size && translation.isNotBlank()) {
                            val originalIndex = nonEmptyEntries[index].first
                            lyrics[originalIndex].translatedTextFlow.value = translation
                        }
                    }
                    _status.value = TranslationStatus.Translating
                }

                result.onSuccess { translatedLines ->
                    if (!isCompositionActive) {
                        return@onSuccess
                    }

                    translationCache[cacheKey] = translatedLines

                    if (songId.isNotBlank() && database != null) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val currentLyrics = database.lyrics(songId).first()
                                if (currentLyrics != null) {
                                    database.query {
                                        upsert(
                                            currentLyrics.copy(
                                                translatedLyrics = translatedLines.joinToString("\n"),
                                                translationLanguage = targetLanguage,
                                                translationMode = mode,
                                            ),
                                        )
                                    }
                                    _translationSaved.tryEmit(Unit)
                                }
                            } catch (e: Exception) {
                                timber.log.Timber.e(e, "Failed to save translated lyrics to database")
                            }
                        }
                    }

                    translatedLines.take(nonEmptyEntries.size).forEachIndexed { index, translation ->
                        val originalIndex = nonEmptyEntries[index].first
                        lyrics[originalIndex].translatedTextFlow.value = translation
                    }
                    _hasActiveTranslations.value = true
                    _status.value = TranslationStatus.Success

                    delay(3000)
                    if (_status.value is TranslationStatus.Success && isCompositionActive) {
                        _status.value = TranslationStatus.Idle
                    }
                }.onFailure { error ->
                    if (!isCompositionActive) {
                        return@onFailure
                    }

                    val errorMessage = error.message ?: context.getString(com.convx.music.R.string.ai_error_unknown)
                    _status.value = TranslationStatus.Error(errorMessage)
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException && isCompositionActive) {
                    val errorMessage = e.message ?: context.getString(com.convx.music.R.string.ai_error_translation_failed)
                    _status.value = TranslationStatus.Error(errorMessage)
                }
            }
        }
    }

    private suspend fun restoreCachedTranslations(
        lyrics: List<LyricsEntry>,
        nonEmptyEntries: List<Pair<Int, LyricsEntry>>,
        translations: List<String>,
        targetLanguage: String,
        mode: String,
        songId: String,
        database: MusicDatabase?,
    ): Boolean {
        if (translations.size < nonEmptyEntries.size) return false

        nonEmptyEntries.forEachIndexed { index, (originalIndex, _) ->
            lyrics[originalIndex].translatedTextFlow.value = translations[index]
        }
        _hasActiveTranslations.value = true
        _status.value = TranslationStatus.Success

        // Keep the database from replacing a cached result with a stale empty entity.
        if (songId.isNotBlank() && database != null) {
            try {
                val currentLyrics = database.lyrics(songId).first()
                if (currentLyrics != null && currentLyrics.translatedLyrics.isNullOrBlank()) {
                    database.query {
                        upsert(
                            currentLyrics.copy(
                                translatedLyrics = translations.joinToString("\n"),
                                translationLanguage = targetLanguage,
                                translationMode = mode,
                            ),
                        )
                    }
                    _translationSaved.tryEmit(Unit)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to persist cached translations to database")
            }
        }

        delay(3000)
        if (_status.value is TranslationStatus.Success && isCompositionActive) {
            _status.value = TranslationStatus.Idle
        }
        return true
    }

    private suspend fun translateWithProvider(
        text: String,
        targetLanguage: String,
        fullLanguageName: String,
        apiKey: String,
        deeplApiKey: String,
        deeplFormality: String,
        baseUrl: String,
        model: String,
        mode: String,
        provider: String,
        useStreaming: Boolean,
        expectedLineCount: Int,
        onPartialTranslations: (List<String>) -> Unit,
    ): Result<List<String>> {
        return when {
            provider == "DeepL" -> {
                Timber.d("Using DeepL for translation")
                DeepLService.translate(
                    text = text,
                    targetLanguage = targetLanguage,
                    apiKey = deeplApiKey,
                    formality = deeplFormality,
                )
            }

            provider == "Mistral" -> {
                Timber.d("Using Mistral for translation")
                MistralService.translate(
                    text = text,
                    targetLanguage = fullLanguageName,
                    apiKey = apiKey,
                    model = model,
                    mode = mode,
                )
            }

            !useStreaming || provider == "Custom" -> {
                Timber.d("Using non-streaming for translation")
                OpenRouterService.translate(
                    text = text,
                    targetLanguage = fullLanguageName,
                    apiKey = apiKey,
                    baseUrl = baseUrl,
                    model = model,
                    mode = mode,
                )
            }

            else -> streamTranslation(
                text = text,
                targetLanguage = fullLanguageName,
                apiKey = apiKey,
                baseUrl = baseUrl,
                model = model,
                mode = mode,
                provider = provider,
                expectedLineCount = expectedLineCount,
                onPartialTranslations = onPartialTranslations,
            )
        }
    }

    private suspend fun streamTranslation(
        text: String,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        mode: String,
        provider: String,
        expectedLineCount: Int,
        onPartialTranslations: (List<String>) -> Unit,
    ): Result<List<String>> {
        Timber.d("Using streaming for translation with provider: $provider")
        var translatedLines: List<String>? = null
        var errorMessage: String? = null
        val contentAccumulator = StringBuilder()

        OpenRouterStreamingService.streamTranslation(
            text = text,
            targetLanguage = targetLanguage,
            apiKey = apiKey,
            baseUrl = baseUrl,
            model = model,
            mode = mode,
        ).collect { chunk ->
            Timber.v("Received streaming chunk: $chunk")
            when (chunk) {
                is OpenRouterStreamingService.StreamChunk.Content -> {
                    contentAccumulator.append(chunk.text)
                    val partialResult = parsePartialTranslations(contentAccumulator.toString(), expectedLineCount)
                    if (partialResult.isNotEmpty()) onPartialTranslations(partialResult)
                }
                is OpenRouterStreamingService.StreamChunk.Complete -> {
                    Timber.d("Streaming complete with ${chunk.translatedLines.size} lines")
                    translatedLines = chunk.translatedLines
                }
                is OpenRouterStreamingService.StreamChunk.Error -> {
                    Timber.e("Streaming error: ${chunk.message}")
                    errorMessage = chunk.message
                }
            }
        }

        Timber.d("Streaming collection complete. hasError=${errorMessage != null}, translatedLines=${translatedLines?.size}")
        return when {
            errorMessage != null -> Result.failure(Exception(errorMessage))
            translatedLines != null -> Result.success(translatedLines)
            else -> Result.failure(Exception("No translation received"))
        }
    }

    sealed class TranslationStatus {
        data object Idle : TranslationStatus()
        data object Translating : TranslationStatus()
        data object Success : TranslationStatus()
        data class Error(val message: String) : TranslationStatus()
    }
}
