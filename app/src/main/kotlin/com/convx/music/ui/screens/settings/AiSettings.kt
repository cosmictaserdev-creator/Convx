/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.ai.AiDefaults
import com.convx.music.ai.AiKeyCheck
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.convx.music.constants.AiProviderKey
import com.convx.music.constants.DeeplApiKey
import com.convx.music.constants.DeeplFormalityKey
import com.convx.music.constants.LanguageCodeToName
import com.convx.music.constants.OpenRouterApiKey
import com.convx.music.constants.OpenRouterBaseUrlKey
import com.convx.music.constants.OpenRouterModelKey
import com.convx.music.constants.TranslateLanguageKey
import com.convx.music.constants.TranslateModeKey
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.TextFieldDialog
import com.convx.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var aiProvider by rememberPreference(AiProviderKey, AiDefaults.PROVIDER)
    var openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
    var openRouterBaseUrl by rememberPreference(OpenRouterBaseUrlKey, AiDefaults.BASE_URL)
    var openRouterModel by rememberPreference(OpenRouterModelKey, AiDefaults.MODEL)
    var translateLanguage by rememberPreference(TranslateLanguageKey, "en")
    var translateMode by rememberPreference(TranslateModeKey, "Literal")
    var deeplApiKey by rememberPreference(DeeplApiKey, "")
    var deeplFormality by rememberPreference(DeeplFormalityKey, "default")

    val aiProviders = mapOf(
        "Groq" to AiDefaults.BASE_URL,
        "OpenRouter" to "https://openrouter.ai/api/v1/chat/completions",
        "OpenAI" to "https://api.openai.com/v1/chat/completions",
        "Perplexity" to "https://api.perplexity.ai/chat/completions",
        "Claude" to "https://api.anthropic.com/v1/messages",
        "Gemini" to "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions",
        "XAi" to "https://api.x.ai/v1/chat/completions",
        "Mistral" to "https://api.mistral.ai/v1/chat/completions",
        "DeepL" to "https://api.deepl.com/v2/translate",
        "Custom" to ""
    )

    val providerHelpText = mapOf(
        "Groq" to stringResource(R.string.ai_provider_groq_help),
        "OpenRouter" to stringResource(R.string.ai_provider_openrouter_help),
        "OpenAI" to stringResource(R.string.ai_provider_openai_help),
        "Perplexity" to stringResource(R.string.ai_provider_perplexity_help),
        "Claude" to stringResource(R.string.ai_provider_claude_help),
        "Gemini" to stringResource(R.string.ai_provider_gemini_help),
        "XAi" to stringResource(R.string.ai_provider_xai_help),
        "Mistral" to stringResource(R.string.ai_provider_mistral_help),
        "DeepL" to stringResource(R.string.ai_provider_deepl_help),
        "Custom" to ""
    )

    val modelsByProvider = mapOf(
        "OpenRouter" to listOf(
            "google/gemini-3.1-flash-lite",
            "google/gemini-3.5-flash",
            "x-ai/grok-4.3",
            "deepseek/deepseek-v3.1-terminus:exacto",
            "openai/gpt-4o-mini",
            "google/gemini-2.5-flash-lite"
        ),
        "Groq" to listOf(
            // Free-tier accounts don't get every model Groq's own docs list —
            // llama-3.3-70b-versatile and llama-3.1-8b-instant 404 as
            // "does not exist or you do not have access to it" on a fresh key.
            // gpt-oss verified working (200) against a real free-tier key.
            "openai/gpt-oss-120b",
            "openai/gpt-oss-20b",
        ),
        "OpenAI" to listOf(
            "gpt-4o-mini",
            "gpt-4o",
            "gpt-4-turbo"
        ),
        "Claude" to listOf(
            "claude-opus-5-5",
            "claude-sonnet-5-5",
            "claude-haiku-5-5"
        ),
        "Gemini" to listOf(
            "gemini-2.5-flash-lite",
            "gemini-2.5-flash",
            "gemini-2.5-pro",
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-3.5-flash",
            "gemini-3-flash",
            "gemini-3.1-flash-lite",
            "gemini-3.1-pro"
        ),
        "Perplexity" to listOf(
            "sonar",
            "sonar-pro",
            "sonar-reasoning"
        ),
        "XAi" to listOf(
            "grok-4-1-fast",
            "grok-vision-beta"
        ),
        "Mistral" to listOf(
            "mistral-large-latest",
            "mistral-medium-latest",
            "mistral-small-latest",
            "mistral-tiny-latest"
        ),
        "DeepL" to listOf(),
        "Custom" to listOf()
    )

    val commonModels = modelsByProvider[aiProvider] ?: listOf()

    var showProviderDialog by rememberSaveable { mutableStateOf(false) }
    var showProviderHelpDialog by rememberSaveable { mutableStateOf(false) }
    var showTranslateModeDialog by rememberSaveable { mutableStateOf(false) }
    var showTranslateModeHelpDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    var showDeeplApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    var showDeeplFormalityDialog by rememberSaveable { mutableStateOf(false) }
    var showBaseUrlDialog by rememberSaveable { mutableStateOf(false) }
    var showModelDialog by rememberSaveable { mutableStateOf(false) }
    var showCustomModelInput by rememberSaveable { mutableStateOf(false) }

    // A key is tried against its provider before it is saved; a failed one reopens the box with what was typed.
    val scope = rememberCoroutineScope()
    var keyDraft by remember { mutableStateOf<String?>(null) }
    var keyCheck by remember { mutableStateOf<KeyCheck?>(null) }
    var checkJob by remember { mutableStateOf<Job?>(null) }

    fun saveKey(provider: String, key: String) {
        if (provider == "DeepL") deeplApiKey = key else openRouterApiKey = key
    }

    fun checkKey(provider: String, key: String) {
        keyDraft = null
        if (key.isBlank()) return saveKey(provider, "")
        keyCheck = KeyCheck(provider, key, null)
        checkJob?.cancel()
        checkJob = scope.launch {
            val result = AiKeyCheck.verify(provider, openRouterBaseUrl, openRouterModel, key)
            if (result is AiKeyCheck.Result.Works) saveKey(provider, key)
            keyCheck = KeyCheck(provider, key, result)
        }
    }

    keyCheck?.let { check ->
        val result = check.result
        AlertDialog(
            onDismissRequest = { if (result != null) keyCheck = null },
            icon = {
                SymbolIcon(
                    symbol = sf(
                        when (result) {
                            null -> "key.fill"
                            is AiKeyCheck.Result.Works -> "checkmark"
                            is AiKeyCheck.Result.Fails -> "exclamationmark.circle"
                        }
                    ),
                    contentDescription = null,
                )
            },
            title = {
                Text(
                    stringResource(
                        when (result) {
                            null -> R.string.ai_key_checking
                            is AiKeyCheck.Result.Works -> R.string.ai_key_works
                            is AiKeyCheck.Result.Fails -> R.string.ai_key_failed
                        }
                    )
                )
            },
            text = {
                when (result) {
                    null -> Box(Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is AiKeyCheck.Result.Works -> Text(result.detail)
                    is AiKeyCheck.Result.Fails -> Text(result.reason)
                }
            },
            confirmButton = {
                when (result) {
                    null -> TextButton(onClick = { checkJob?.cancel(); keyCheck = null }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                    is AiKeyCheck.Result.Works -> TextButton(onClick = { keyCheck = null }) {
                        Text(stringResource(android.R.string.ok))
                    }
                    is AiKeyCheck.Result.Fails -> TextButton(onClick = {
                        keyDraft = check.key
                        keyCheck = null
                        if (check.provider == "DeepL") showDeeplApiKeyDialog = true else showApiKeyDialog = true
                    }) {
                        Text(stringResource(R.string.ai_key_edit))
                    }
                }
            },
            dismissButton = if (result is AiKeyCheck.Result.Fails) {
                {
                    TextButton(onClick = { saveKey(check.provider, check.key); keyCheck = null }) {
                        Text(stringResource(R.string.ai_key_save_anyway))
                    }
                }
            } else {
                null
            },
        )
    }

    if (showProviderHelpDialog) {
        AlertDialog(
            onDismissRequest = { showProviderHelpDialog = false },
            confirmButton = {
                TextButton(onClick = { showProviderHelpDialog = false }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            icon = { SymbolIcon(symbol = sf("info.circle"), contentDescription = null) },
            title = { Text(stringResource(R.string.ai_provider_help)) },
            text = { 
                Column {
                    providerHelpText.forEach { (provider, help) ->
                        if (help.isNotEmpty()) {
                            Text(
                                text = "$provider: $help",
                                style = AppTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    if (showTranslateModeHelpDialog) {
        AlertDialog(
            onDismissRequest = { showTranslateModeHelpDialog = false },
            confirmButton = {
                TextButton(onClick = { showTranslateModeHelpDialog = false }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            icon = { SymbolIcon(symbol = sf("info.circle"), contentDescription = null) },
            title = { Text(stringResource(R.string.ai_translation_mode)) },
            text = { 
                Column {
                    Text(
                        text = "${stringResource(R.string.ai_translation_literal)}:",
                        style = AppTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.ai_translation_literal_desc),
                        style = AppTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "${stringResource(R.string.ai_translation_transcribed)}:",
                        style = AppTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.ai_translation_transcribed_desc),
                        style = AppTheme.typography.bodyMedium
                    )
                }
            }
        )
    }

    if (showProviderDialog) {
        EnumDialog(
            onDismiss = { showProviderDialog = false },
            onSelect = {
                aiProvider = it
                if (it != "Custom" && it != "DeepL") {
                    openRouterBaseUrl = aiProviders[it] ?: ""
                } else {
                    openRouterBaseUrl = ""
                }
                val modelsForProvider = modelsByProvider[it] ?: listOf()
                openRouterModel = if (modelsForProvider.isNotEmpty()) {
                    modelsForProvider[0]
                } else {
                    ""
                }
                showProviderDialog = false
            },
            title = stringResource(R.string.ai_provider),
            current = aiProvider,
            values = aiProviders.keys.toList(),
            valueText = { it }
        )
    }

    if (showTranslateModeDialog) {
        EnumDialog(
            onDismiss = { showTranslateModeDialog = false },
            onSelect = {
                translateMode = it
                showTranslateModeDialog = false
            },
            title = stringResource(R.string.ai_translation_mode),
            current = translateMode,
            values = listOf("Literal", "Transcribed"),
            valueText = {
                when (it) {
                    "Literal" -> stringResource(R.string.ai_translation_literal)
                    "Transcribed" -> stringResource(R.string.ai_translation_transcribed)
                    else -> it
                }
            }
        )
    }

    if (showLanguageDialog) {
        EnumDialog(
            onDismiss = { showLanguageDialog = false },
            onSelect = {
                translateLanguage = it
                showLanguageDialog = false
            },
            title = stringResource(R.string.ai_target_language),
            current = translateLanguage,
            values = LanguageCodeToName.keys.sortedBy { LanguageCodeToName[it] },
            valueText = { LanguageCodeToName[it] ?: it }
        )
    }

    if (showApiKeyDialog) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.ai_api_key)) },
            icon = { SymbolIcon(symbol = sf("key.fill"), contentDescription = null) },
            initialTextFieldValue = TextFieldValue(text = keyDraft ?: openRouterApiKey),
            onDone = {
                showApiKeyDialog = false
                checkKey(aiProvider, it.trim())
            },
            onDismiss = { showApiKeyDialog = false; keyDraft = null }
        )
    }

    if (showDeeplApiKeyDialog) {
        TextFieldDialog(
            title = { Text("DeepL ${stringResource(R.string.ai_api_key)}") },
            icon = { SymbolIcon(symbol = sf("key.fill"), contentDescription = null) },
            initialTextFieldValue = TextFieldValue(text = keyDraft ?: deeplApiKey),
            onDone = {
                showDeeplApiKeyDialog = false
                checkKey("DeepL", it.trim())
            },
            onDismiss = { showDeeplApiKeyDialog = false; keyDraft = null }
        )
    }

    if (showDeeplFormalityDialog) {
        EnumDialog(
            onDismiss = { showDeeplFormalityDialog = false },
            onSelect = {
                deeplFormality = it
                showDeeplFormalityDialog = false
            },
            title = stringResource(R.string.ai_deepl_formality),
            current = deeplFormality,
            values = listOf("default", "more", "less"),
            valueText = {
                when (it) {
                    "default" -> stringResource(R.string.ai_deepl_formality_default)
                    "more" -> stringResource(R.string.ai_deepl_formality_more)
                    "less" -> stringResource(R.string.ai_deepl_formality_less)
                    else -> it
                }
            }
        )
    }

    if (showBaseUrlDialog && aiProvider == "Custom") {
        TextFieldDialog(
            title = { Text(stringResource(R.string.ai_base_url)) },
            icon = { SymbolIcon(symbol = sf("link"), contentDescription = null) },
            initialTextFieldValue = TextFieldValue(text = openRouterBaseUrl),
            onDone = {
                openRouterBaseUrl = it
                showBaseUrlDialog = false
            },
            onDismiss = { showBaseUrlDialog = false }
        )
    }

    if (showModelDialog) {
        EnumDialog(
            onDismiss = { showModelDialog = false },
            onSelect = {
                if (it == "custom_input") {
                    showCustomModelInput = true
                    showModelDialog = false
                } else {
                    openRouterModel = it
                    showModelDialog = false
                }
            },
            title = stringResource(R.string.ai_model),
            current = if (openRouterModel in commonModels) openRouterModel else "custom_input",
            values = commonModels + "custom_input",
            valueText = { 
                if (it == "custom_input") "Custom" else it
            }
        )
    }

    if (showCustomModelInput) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.ai_model)) },
            icon = { SymbolIcon(symbol = sf("slider.horizontal.3"), contentDescription = null) },
            initialTextFieldValue = TextFieldValue(text = openRouterModel),
            onDone = {
                openRouterModel = it
                showCustomModelInput = false
            },
            onDismiss = { showCustomModelInput = false }
        )
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.ai_lyrics_translation),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        IosSettingsGroup(
            title = stringResource(R.string.ai_provider),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.explore_outlined),
                    title = { Text(stringResource(R.string.ai_provider)) },
                    description = { Text(aiProvider) },
                    onClick = { showProviderDialog = true },
                    trailingContent = {
                        IconButton(onClick = { showProviderHelpDialog = true }) {
                            SymbolIcon(symbol = sf("info.circle"), contentDescription = stringResource(R.string.ai_provider_help), size = 20.dp)
                        }
                    }
                ),
                if (aiProvider == "Custom") {
                    IosSettingsItem(
                        icon = painterResource(R.drawable.link),
                        title = { Text(stringResource(R.string.ai_base_url)) },
                        description = { Text(openRouterBaseUrl.ifBlank { stringResource(R.string.not_set) }) },
                        onClick = { showBaseUrlDialog = true }
                    )
                } else {
                    null
                }
            ).filterNotNull()
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.ai_setup_guide),
            items = buildList {
                if (aiProvider == "DeepL") {
                    add(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.key),
                            title = { Text("DeepL ${stringResource(R.string.ai_api_key)}") },
                            description = { 
                                Text(
                                    if (deeplApiKey.isNotEmpty()) 
                                        "•".repeat(minOf(deeplApiKey.length, 8))
                                    else 
                                        stringResource(R.string.not_set)
                                )
                            },
                            onClick = { showDeeplApiKeyDialog = true }
                        )
                    )
                    add(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tune),
                            title = { Text(stringResource(R.string.ai_deepl_formality)) },
                            description = { 
                                Text(
                                    when (deeplFormality) {
                                        "default" -> stringResource(R.string.ai_deepl_formality_default)
                                        "more" -> stringResource(R.string.ai_deepl_formality_more)
                                        "less" -> stringResource(R.string.ai_deepl_formality_less)
                                        else -> deeplFormality
                                    }
                                )
                            },
                            onClick = { showDeeplFormalityDialog = true }
                        )
                    )
                } else {
                    add(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.key),
                            title = { Text(stringResource(R.string.ai_api_key)) },
                            description = { 
                                Text(
                                    if (openRouterApiKey.isNotEmpty()) 
                                        "•".repeat(minOf(openRouterApiKey.length, 8))
                                    else 
                                        stringResource(R.string.not_set)
                                )
                            },
                            onClick = { showApiKeyDialog = true }
                        )
                    )
                    if (aiProvider != "Custom") {
                        add(
                            IosSettingsItem(
                                icon = painterResource(R.drawable.discover_tune),
                                title = { Text(stringResource(R.string.ai_model)) },
                                description = { Text(openRouterModel.ifBlank { stringResource(R.string.not_set) }) },
                                onClick = { showModelDialog = true }
                            )
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.ai_translation_mode),
            items = buildList {
                if (aiProvider != "DeepL") {
                    add(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.translate),
                            title = { Text(stringResource(R.string.ai_translation_mode)) },
                            description = {
                                Text(
                                    when (translateMode) {
                                        "Literal" -> stringResource(R.string.ai_translation_literal)
                                        "Transcribed" -> stringResource(R.string.ai_translation_transcribed)
                                        else -> translateMode
                                    }
                                )
                            },
                            onClick = { showTranslateModeDialog = true },
                            trailingContent = {
                                IconButton(onClick = { showTranslateModeHelpDialog = true }) {
                                    SymbolIcon(symbol = sf("info.circle"), contentDescription = null, size = 20.dp)
                                }
                            }
                        )
                    )
                }
                add(
                    IosSettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.ai_target_language)) },
                        description = { Text(LanguageCodeToName[translateLanguage] ?: translateLanguage) },
                        onClick = { showLanguageDialog = true }
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

/** A key being tried; [result] is null while the request is in flight. */
private data class KeyCheck(val provider: String, val key: String, val result: AiKeyCheck.Result?)
