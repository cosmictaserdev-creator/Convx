/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.convx.music.constants.AiProviderKey
import com.convx.music.constants.OpenRouterApiKey
import com.convx.music.constants.OpenRouterBaseUrlKey
import com.convx.music.constants.OpenRouterModelKey
import com.convx.music.utils.dataStore

/** The AI provider every feature starts on until the user picks another. */
object AiDefaults {
    const val PROVIDER = "Groq"
    const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"

    /** Verified on a free-tier Groq key; the llama models 404 there. */
    const val MODEL = "openai/gpt-oss-120b"

    private const val LEGACY_PROVIDER = "OpenRouter"
    private const val LEGACY_BASE_URL = "https://openrouter.ai/api/v1/chat/completions"
    private const val LEGACY_MODEL = "google/gemini-3.1-flash-lite"

    /** Providers that only translate, so the chat features (DJ, playlists, insights) cannot use them. */
    val TRANSLATION_ONLY = setOf("DeepL")

    /**
     * OpenRouter used to be the default, so a key saved without ever picking a provider is an
     * OpenRouter key. Pin those settings before the new default would send it to Groq.
     */
    suspend fun keepLegacyProvider(context: Context) {
        context.dataStore.edit { prefs ->
            if (prefs[AiProviderKey] != null || prefs[OpenRouterApiKey].isNullOrBlank()) return@edit
            prefs[AiProviderKey] = LEGACY_PROVIDER
            if (prefs[OpenRouterBaseUrlKey] == null) prefs[OpenRouterBaseUrlKey] = LEGACY_BASE_URL
            if (prefs[OpenRouterModelKey] == null) prefs[OpenRouterModelKey] = LEGACY_MODEL
        }
    }
}
