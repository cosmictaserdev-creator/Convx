/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.EnabledModulesKey
import com.convxy.music.constants.ModuleSettingsKey
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.ModernSwitch
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import org.json.JSONArray
import org.json.JSONObject

private val QUALITY_OPTIONS = listOf(
    "Lossless (FLAC 16-bit/44.1kHz)" to "LOSSLESS",
    "Hi-Res (FLAC 24-bit)" to "HIRES",
    "Dolby Atmos (EAC3-JOC)" to "EAC3_JOC",
    "High (AAC 320kbps)" to "HIGH",
    "Low (AAC 96kbps)" to "LOW",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    moduleId: String,
) {
    val (enabledJson, onEnabledJsonChange) = rememberPreference(EnabledModulesKey, defaultValue = "[]")
    val (moduleSettingsJson, onModuleSettingsJsonChange) = rememberPreference(ModuleSettingsKey, defaultValue = "{}")

    val enabledIds = remember(enabledJson) {
        runCatching {
            val arr = JSONArray(enabledJson)
            (0 until arr.length()).map { arr.getString(it) }.toSet()
        }.getOrElse { emptySet<String>() }
    }

    val isEnabled = enabledIds.contains(moduleId)

    fun toggleEnabled() {
        val current = enabledIds.toMutableSet()
        if (current.contains(moduleId)) current.remove(moduleId) else current.add(moduleId)
        val arr = JSONArray()
        current.forEach { arr.put(it) }
        onEnabledJsonChange(arr.toString())
    }

    val moduleSettings = remember(moduleSettingsJson) {
        runCatching {
            val obj = JSONObject(moduleSettingsJson)
            val inner = obj.optJSONObject(moduleId)
            if (inner != null) {
                inner.keys().asSequence().associateWith { inner.optString(it, "") }
            } else emptyMap()
        }.getOrElse { emptyMap<String, String>() }
    }

    fun updateModuleSetting(key: String, value: String) {
        val obj = runCatching { JSONObject(moduleSettingsJson) }.getOrElse { JSONObject() }
        val inner = obj.optJSONObject(moduleId) ?: JSONObject()
        inner.put(key, value)
        obj.put(moduleId, inner)
        onModuleSettingsJsonChange(obj.toString())
    }

    var qualityExpanded by remember { mutableStateOf(false) }
    var tidalQualityExpanded by remember { mutableStateOf(false) }

    val currentQuality = moduleSettings["quality"] ?: ""
    val currentTidalQuality = moduleSettings["tidalQuality"] ?: ""

    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(
                    if (isEnabled) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.enable_module),
                style = MaterialTheme.typography.titleMedium,
                color = if (isEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ModernSwitch(
                checked = isEnabled,
                onCheckedChange = { toggleEnabled() },
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.module_info),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppleTokens.CardCorner))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
        ) {
            ModuleInfoRow(stringResource(R.string.module_id), moduleId)
            ModuleInfoRow(
                stringResource(R.string.module_status),
                if (isEnabled) stringResource(R.string.enabled) else stringResource(R.string.disabled)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.module_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "QUALITY SETTINGS",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppleTokens.CardCorner))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
        ) {
            Text(
                text = "Primary Quality",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = qualityExpanded,
                onExpandedChange = { qualityExpanded = it },
            ) {
                OutlinedTextField(
                    value = QUALITY_OPTIONS.find { it.second == currentQuality }?.first ?: "Default",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = qualityExpanded,
                    onDismissRequest = { qualityExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Default") },
                        onClick = {
                            updateModuleSetting("quality", "")
                            qualityExpanded = false
                        },
                    )
                    QUALITY_OPTIONS.forEach { (label, value) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                updateModuleSetting("quality", value)
                                qualityExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tidal Fallback Quality",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = tidalQualityExpanded,
                onExpandedChange = { tidalQualityExpanded = it },
            ) {
                OutlinedTextField(
                    value = QUALITY_OPTIONS.find { it.second == currentTidalQuality }?.first ?: "Default",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tidalQualityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = tidalQualityExpanded,
                    onDismissRequest = { tidalQualityExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Default") },
                        onClick = {
                            updateModuleSetting("tidalQuality", "")
                            tidalQualityExpanded = false
                        },
                    )
                    QUALITY_OPTIONS.forEach { (label, value) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                updateModuleSetting("tidalQuality", value)
                                tidalQualityExpanded = false
                            },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.module_detail)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun ModuleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
