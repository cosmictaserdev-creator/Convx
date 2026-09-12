/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.constants.AppFont
import com.nexamusic.app.constants.BrandFontEnabledKey
import com.nexamusic.app.constants.CustomFontEnabledKey
import com.nexamusic.app.constants.CustomFontNameKey
import com.nexamusic.app.constants.CustomFontPathKey
import com.nexamusic.app.constants.SelectedFontKey
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.ui.theme.GoogleSansFontFamily
import com.nexamusic.app.ui.theme.OutfitFontFamily
import com.nexamusic.app.ui.theme.PlusJakartaSansFontFamily
import com.nexamusic.app.ui.theme.SansFlexFontFamily
import com.nexamusic.app.ui.theme.copyCustomFont
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSelectionScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (selectedFont, onSelectedFontChange) = rememberPreference(
        SelectedFontKey,
        defaultValue = AppFont.SYSTEM.value
    )

    val (brandFont, onBrandFontChange) = rememberPreference(
        BrandFontEnabledKey,
        defaultValue = true
    )
    val (customFontEnabled, onCustomFontEnabledChange) = rememberPreference(
        CustomFontEnabledKey,
        defaultValue = false
    )
    val (customFontPath, onCustomFontPathChange) = rememberPreference(
        CustomFontPathKey,
        defaultValue = ""
    )
    val (customFontName, onCustomFontNameChange) = rememberPreference(
        CustomFontNameKey,
        defaultValue = ""
    )
    val context = LocalContext.current
    val customFontScope = rememberCoroutineScope()
    val customFontPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val displayName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
        } ?: uri.lastPathSegment.orEmpty()
        customFontScope.launch {
            val newPath = withContext(Dispatchers.IO) { copyCustomFont(context, uri) }
            if (newPath != null) {
                onCustomFontPathChange(newPath)
                onCustomFontNameChange(displayName)
                onCustomFontEnabledChange(true)
            }
        }
    }

    val activeFontFamily = remember(selectedFont) {
        when (AppFont.fromValue(selectedFont)) {
            AppFont.SYSTEM -> FontFamily.Default
            AppFont.GOOGLE_SANS -> GoogleSansFontFamily
            AppFont.SANS_FLEX -> SansFlexFontFamily
            AppFont.OUTFIT -> OutfitFontFamily
            AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = appTopBarWindowInsets(),
                title = { Text(stringResource(R.string.app_font)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Typography Preview Card
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.typography_preview).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.preview_text_quote),
                        fontFamily = activeFontFamily,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Expressive typeface is applied to display, headlines, and titles. Body copy and labels remain in the system font for maximum readability.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options settings group
            Material3SettingsGroup(
                title = stringResource(R.string.font_selection),
                items = listOf(
                    Material3SettingsItem(
                        leadingContent = {
                            RadioButton(
                                selected = selectedFont == AppFont.SYSTEM.value,
                                onClick = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.font_system),
                                fontFamily = FontFamily.Default
                            )
                        },
                        description = {
                            Text(
                                text = stringResource(R.string.font_system_desc),
                                fontFamily = FontFamily.Default
                            )
                        },
                        onClick = { onSelectedFontChange(AppFont.SYSTEM.value) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            RadioButton(
                                selected = selectedFont == AppFont.GOOGLE_SANS.value,
                                onClick = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.font_google_sans),
                                fontFamily = GoogleSansFontFamily
                            )
                        },
                        description = {
                            Text(
                                text = stringResource(R.string.font_google_sans_desc),
                                fontFamily = GoogleSansFontFamily
                            )
                        },
                        onClick = { onSelectedFontChange(AppFont.GOOGLE_SANS.value) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            RadioButton(
                                selected = selectedFont == AppFont.SANS_FLEX.value,
                                onClick = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.font_sans_flex),
                                fontFamily = SansFlexFontFamily
                            )
                        },
                        description = {
                            Text(
                                text = stringResource(R.string.font_sans_flex_desc),
                                fontFamily = SansFlexFontFamily
                            )
                        },
                        onClick = { onSelectedFontChange(AppFont.SANS_FLEX.value) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            RadioButton(
                                selected = selectedFont == AppFont.OUTFIT.value,
                                onClick = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.font_outfit),
                                fontFamily = OutfitFontFamily
                            )
                        },
                        description = {
                            Text(
                                text = stringResource(R.string.font_outfit_desc),
                                fontFamily = OutfitFontFamily
                            )
                        },
                        onClick = { onSelectedFontChange(AppFont.OUTFIT.value) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            RadioButton(
                                selected = selectedFont == AppFont.PLUS_JAKARTA_SANS.value,
                                onClick = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.font_plus_jakarta_sans),
                                fontFamily = PlusJakartaSansFontFamily
                            )
                        },
                        description = {
                            Text(
                                text = stringResource(R.string.font_plus_jakarta_sans_desc),
                                fontFamily = PlusJakartaSansFontFamily
                            )
                        },
                        onClick = { onSelectedFontChange(AppFont.PLUS_JAKARTA_SANS.value) }
                    )
                )
            )
            Spacer(modifier = Modifier.height(27.dp))

            Material3SettingsGroup(
                title = stringResource(R.string.font_additional),
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.brand_font)) },
                        description = { Text(stringResource(R.string.brand_font_desc)) },
                        trailingContent = {
                            Switch(
                                checked = brandFont,
                                onCheckedChange = onBrandFontChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (brandFont) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onBrandFontChange(!brandFont) }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.palette),
                        title = { Text(stringResource(R.string.custom_font)) },
                        description = {
                            Text(
                                if (customFontPath.isEmpty()) stringResource(R.string.custom_font_none)
                                else customFontName.ifEmpty { stringResource(R.string.custom_font_set) }
                            )
                        },
                        trailingContent = {
                            if (customFontPath.isNotEmpty()) {
                                Switch(
                                    checked = customFontEnabled,
                                    onCheckedChange = onCustomFontEnabledChange,
                                    thumbContent = {
                                        Icon(
                                            painter = painterResource(
                                                id = if (customFontEnabled) R.drawable.check else R.drawable.close
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                )
                            }
                        },
                        onClick = { customFontPickerLauncher.launch(arrayOf("*/*")) }
                    )
                )
            )
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
