/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.convx.music.LocalDatabase
import com.convx.music.R
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.db.entities.SongEntity
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.ui.component.LyricsProviderSelector
import com.convx.music.ui.component.LyricsProviderMenu
import com.convx.music.ui.component.TextFieldDialog
import com.convx.music.viewmodels.LyricsMenuViewModel
import com.convx.music.constants.OpenRouterApiKey
import com.convx.music.constants.DeeplApiKey
import com.convx.music.constants.AiProviderKey
import com.convx.music.constants.TranslateLanguageKey
import com.convx.music.constants.TranslateModeKey
import com.convx.music.constants.VocalSuppressionKey
import com.convx.music.constants.OpenRouterBaseUrlKey
import com.convx.music.constants.OpenRouterModelKey
import com.convx.music.constants.DeeplFormalityKey
import com.convx.music.lyrics.LyricsTranslationHelper
import com.convx.music.utils.rememberPreference
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsMenu(
    lyricsProvider: () -> LyricsEntity?,
    songProvider: () -> SongEntity?,
    mediaMetadataProvider: () -> MediaMetadata,
    onDismiss: () -> Unit,
    onShowOffsetDialog: () -> Unit = {},
    viewModel: LyricsMenuViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    
    val openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
    val deeplApiKey by rememberPreference(DeeplApiKey, "")
    val aiProvider by rememberPreference(AiProviderKey, com.convx.music.ai.AiDefaults.PROVIDER)
    val translateLanguage by rememberPreference(TranslateLanguageKey, "en")
    val translateMode by rememberPreference(TranslateModeKey, "Literal")
    val openRouterBaseUrl by rememberPreference(OpenRouterBaseUrlKey, com.convx.music.ai.AiDefaults.BASE_URL)
    val openRouterModel by rememberPreference(OpenRouterModelKey, com.convx.music.ai.AiDefaults.MODEL)
    val deeplFormality by rememberPreference(DeeplFormalityKey, "default")

    val hasApiKey = if (aiProvider == "DeepL") deeplApiKey.isNotBlank() else openRouterApiKey.isNotBlank()

    // Observe the authoritative translation-active state from the singleton; this persists
    // correctly across menu open/close cycles and avoids the lyricsProvider() race condition.
    val hasTranslations by LyricsTranslationHelper.hasActiveTranslations.collectAsStateWithLifecycle()
    val (savedVocalSuppression, saveVocalSuppression) = rememberPreference(VocalSuppressionKey, 0f)
    var vocalSuppression by rememberSaveable(mediaMetadataProvider().id) {
        mutableFloatStateOf(savedVocalSuppression)
    }
    LaunchedEffect(savedVocalSuppression) {
        vocalSuppression = savedVocalSuppression
    }

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        TextFieldDialog(
            onDismiss = { showEditDialog = false },
            icon = { SymbolIcon(symbol = sf("pencil"), contentDescription = null) },
            title = { Text(text = mediaMetadataProvider().title) },
            initialTextFieldValue = TextFieldValue(lyricsProvider()?.lyrics.orEmpty()),
            singleLine = false,
            onDone = {
                database.query {
                    upsert(
                        LyricsEntity(
                            id = mediaMetadataProvider().id,
                            lyrics = it,
                            provider = lyricsProvider()?.provider ?: "Manual",
                        ),
                    )
                }
            },
        )
    }

    var showSearchDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showSearchResultDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showProviderMenu by rememberSaveable(mediaMetadataProvider().id) {
        mutableStateOf(false)
    }

    val searchMediaMetadata =
        remember(showSearchDialog) {
            mediaMetadataProvider()
        }
    val (titleField, onTitleFieldChange) =
        rememberSaveable(showSearchDialog, stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue(
                    text = mediaMetadataProvider().title,
                ),
            )
        }
    val (artistField, onArtistFieldChange) =
        rememberSaveable(showSearchDialog, stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue(
                    text = mediaMetadataProvider().artists.joinToString { it.name },
                ),
            )
        }

    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    if (showSearchDialog) {
        DefaultDialog(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            onDismiss = { showSearchDialog = false },
            icon = {
                SymbolIcon(symbol = sf("magnifyingglass"), contentDescription = null)
            },
            title = { Text(stringResource(R.string.search_lyrics)) },
            buttons = {
                TextButton(
                    onClick = { showSearchDialog = false },
                ) {
                    Text(stringResource(android.R.string.cancel))
                }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        showSearchDialog = false
                        onDismiss()
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_WEB_SEARCH).apply {
                                    putExtra(
                                        SearchManager.QUERY,
                                        "${artistField.text} ${titleField.text} lyrics"
                                    )
                                },
                            )
                        } catch (_: Exception) {
                            // No browser may be available to handle web search.
                        }
                    },
                ) {
                    Text(stringResource(R.string.search_online))
                }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        // Try search regardless of network status indicator
                        // as it might be a false negative
                        viewModel.search(
                            searchMediaMetadata.id,
                            titleField.text,
                            artistField.text,
                            searchMediaMetadata.duration,
                            searchMediaMetadata.album?.title
                        )
                        showSearchResultDialog = true
                        
                        // Show warning only if network is definitely unavailable
                        if (!isNetworkAvailable) {
                            Toast.makeText(context, context.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show()
                        }
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        ) {
            OutlinedTextField(
                value = titleField,
                onValueChange = onTitleFieldChange,
                singleLine = true,
                label = { Text(stringResource(R.string.song_title)) },
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = artistField,
                onValueChange = onArtistFieldChange,
                singleLine = true,
                label = { Text(stringResource(R.string.song_artists)) },
            )
        }
    }

    if (showSearchResultDialog) {
        val results by viewModel.results.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        var expandedItemIndex by rememberSaveable {
            mutableIntStateOf(-1)
        }

        ListDialog(
            onDismiss = { showSearchResultDialog = false },
        ) {
            itemsIndexed(results) { index, result ->
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            viewModel.cancelSearch()
                            database.query {
                                upsert(
                                    LyricsEntity(
                                        id = searchMediaMetadata.id,
                                        lyrics = result.lyrics,
                                        provider = result.providerId,
                                    ),
                                )
                            }
                        }
                        .padding(12.dp)
                        .animateContentSize(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = result.lyrics,
                            style = AppTheme.typography.bodyMedium,
                            maxLines = if (index == expandedItemIndex) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )

                        Surface(
                            color = AppTheme.colorScheme.secondaryContainer,
                            shape = ContinuousRoundedRectangle(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = result.providerName,
                                    style = AppTheme.typography.labelMedium,
                                    color = AppTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                )
                                if (result.lyrics.startsWith("[")) {
                                    SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null, size = 14.dp, modifier = Modifier
                                            .padding(start = 4.dp)
                                            , tint = AppTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            expandedItemIndex = if (expandedItemIndex == index) -1 else index
                        },
                    ) {
                        SymbolIcon(symbol = if (index == expandedItemIndex) sf("chevron.up") else sf("chevron.down"), contentDescription = null)
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CircularWavyProgressIndicator()
                    }
                }
            }

            if (!isLoading && results.isEmpty()) {
                item {
                    Text(
                        text = context.getString(R.string.lyrics_not_found),
                        textAlign = TextAlign.Center,
                        modifier =
                        Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }

    var showRomanization by rememberSaveable { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(songProvider()?.romanizeLyrics ?: true) }

    var lyricsOffset by rememberSaveable { mutableIntStateOf(songProvider()?.lyricsOffset ?: 0) }

    // Refresh the setting when the active song changes.
    LaunchedEffect(songProvider()) {
        isChecked = songProvider()?.romanizeLyrics ?: true
    }

    LaunchedEffect(songProvider()) {
        lyricsOffset = songProvider()?.lyricsOffset ?: 0
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (showProviderMenu) {
        LyricsProviderMenu(
            metadata = mediaMetadataProvider(),
            lyrics = lyricsProvider(),
            onBack = { showProviderMenu = false },
            modifier = Modifier.fillMaxWidth(),
        )
    } else LazyColumn(
        contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 8.dp + menuBottomInset()),
    ) {
        item {
            NewActionGrid(
                actions = listOf(
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("pencil"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.edit),
                        onClick = {
                            showEditDialog = true
                        }
                    ),
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("arrow.triangle.2.circlepath"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.refetch),
                        onClick = {
                            onDismiss()
                            viewModel.refetchLyrics(mediaMetadataProvider(), lyricsProvider())
                        }
                    ),
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("magnifyingglass"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.search),
                        onClick = {
                            showSearchDialog = true
                        }
                    )
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)
            )
        }

        item(key = "lyrics_provider") {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                LyricsProviderSelector(
                    metadata = mediaMetadataProvider(),
                    lyrics = lyricsProvider(),
                    onOpenMenu = { showProviderMenu = true },
                )
            }
        }

        item(key = "vocal_reduction") {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.karaoke_vocal_reduction),
                        style = AppTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${(vocalSuppression * 100).toInt()}%",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Slider(
                    value = vocalSuppression,
                    onValueChange = { vocalSuppression = it },
                    onValueChangeFinished = { saveVocalSuppression(vocalSuppression) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            IosMenuGroup(
                items = buildList {
                    if (hasApiKey) {
                        add(
                            IosMenuItemData(
                                title = { Text(stringResource(R.string.ai_lyrics_translation)) },
                                icon = {
                                    SymbolIcon(symbol = sf("character.bubble"), contentDescription = null)
                                },
                                onClick = {
                                    if (hasTranslations) {
                                        lyricsProvider()?.let { lyrics ->
                                            val clearedLyrics = LyricsTranslationHelper.clearTranslations(lyrics)
                                            database.query {
                                                upsert(clearedLyrics)
                                            }
                                            // Resets hasActiveTranslations and clears in-memory translations
                                            LyricsTranslationHelper.triggerClearTranslations()
                                        }
                                    } else {
                                        LyricsTranslationHelper.triggerManualTranslation()
                                    }
                                },
                                trailingContent = {
                                    Switch(
                                        checked = hasTranslations,
                                        onCheckedChange = { newCheckedState ->
                                            if (newCheckedState) {
                                                // Enable translations – hasActiveTranslations updates when done
                                                LyricsTranslationHelper.triggerManualTranslation()
                                            } else {
                                                // Disable translations – triggerClearTranslations resets hasActiveTranslations
                                                lyricsProvider()?.let { lyrics ->
                                                    val clearedLyrics = LyricsTranslationHelper.clearTranslations(lyrics)
                                                    database.query {
                                                        upsert(clearedLyrics)
                                                    }
                                                    LyricsTranslationHelper.triggerClearTranslations()
                                                }
                                            }
                                        },
                                    )
                                },
                            )
                        )
                    }
                    
                    add(
                        IosMenuItemData(
                            title = { Text(stringResource(R.string.lyrics_offset)) },
                            icon = {
                                SymbolIcon(symbol = sf("forward.fill"), contentDescription = null)
                            },
                            onClick = {
                                onDismiss()
                                onShowOffsetDialog()
                            },
                            trailingContent = {
                                Text(
                                    text = "${if (lyricsOffset >= 0) "+" else ""}${lyricsOffset}ms",
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    )
                    
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.romanize_current_track)) },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.language_korean_latin),
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                isChecked = !isChecked
                                songProvider()?.let { song ->
                                    database.query {
                                        upsert(song.copy(romanizeLyrics = isChecked))
                                    }
                                }
                            },
                            trailingContent = {
                                Switch(
                                    checked = isChecked,
                                    onCheckedChange = { newCheckedState ->
                                        isChecked = newCheckedState
                                        songProvider()?.let { song ->
                                            database.query {
                                                upsert(song.copy(romanizeLyrics = newCheckedState))
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    )
                }
            )
        }
    }
    
}
