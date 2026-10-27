package com.convx.music.ui.screens.equalizer

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.annotation.SuppressLint
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.media.session.PlaybackState
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.painterResource
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.convx.music.ui.ios.compat.AlertDialog
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.ListItem
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.RadioButton
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFPlus
import com.composables.sfsymbols.monochrome.SFSliderHorizontal3
import com.composables.sfsymbols.monochrome.SFTrash
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.eq.data.SavedEQProfile
import timber.log.Timber

/**
 * EQ Screen - Manage and select EQ profiles
 */
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun EqScreen(
    navController: NavController? = null,
    viewModel: EQViewModel = koinViewModel(),
    playbackState: PlaybackState? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current

    var showError by remember { mutableStateOf<String?>(null) }

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    // File picker for custom EQ import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val contentResolver = context.contentResolver

                // Extract file name from URI
                var fileName = "custom_eq.txt"
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (displayNameIndex >= 0) {
                            val name = cursor.getString(displayNameIndex)
                            if (!name.isNullOrBlank()) {
                                fileName = name
                            }
                        }
                    }
                }

                val inputStream = contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    viewModel.importCustomProfile(
                        fileName = fileName,
                        inputStream = inputStream,
                        onSuccess = {
                            Timber.d("Custom EQ profile imported successfully: $fileName")
                        },
                        onError = { error ->
                            Timber.d("Error: Unable to import Custom EQ profile: $fileName")
                            showError = context.getString(R.string.import_error_title) + ": " + error.message
                        })
                } else {
                    showError = context.getString(R.string.error_file_read)
                }
            } catch (e: Exception) {
                showError = context.getString(R.string.error_file_open, e.message)
            }
        }
    }

    EqScreenContent(
        profiles = state.profiles,
        activeProfileId = state.activeProfileId,
        onProfileSelected = { viewModel.selectProfile(it) },
        onImportCustomEQ = {
            // Launch file picker for .txt files
            filePickerLauncher.launch("text/plain")
        },
        onOpenSystemEqualizer = {
            playerConnection?.let { connection ->
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                    putExtra(
                        AudioEffect.EXTRA_AUDIO_SESSION,
                        connection.player.audioSessionId
                    )
                    putExtra(
                        AudioEffect.EXTRA_PACKAGE_NAME,
                        context.packageName
                    )
                    putExtra(
                        AudioEffect.EXTRA_CONTENT_TYPE,
                        AudioEffect.CONTENT_TYPE_MUSIC
                    )
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    activityResultLauncher.launch(intent)
                }
            }
        },
        onNavigateToAxion = {
            navController?.navigate("settings/equalizer")
        },
        onDeleteProfile = { viewModel.deleteProfile(it) }
    )

    // Error dialog
    if (showError != null) {
        AlertDialog(
            onDismissRequest = { showError = null },
            title = {
                Text(stringResource(R.string.import_error_title))
            },
            text = {
                Text(showError ?: "")
            },
            confirmButton = {
                TextButton(onClick = { showError = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    // Error dialog for apply failure
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(stringResource(R.string.error_title))
            },
            text = {
                Text(stringResource(R.string.error_eq_apply_failed, state.error ?: ""))
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EqScreenContent(
    profiles: List<SavedEQProfile>,
    activeProfileId: String?,
    onProfileSelected: (String?) -> Unit,
    onImportCustomEQ: () -> Unit,
    onOpenSystemEqualizer: () -> Unit,
    onNavigateToAxion: () -> Unit,
    onDeleteProfile: (String) -> Unit
) {
    Surface(
        shape = AppTheme.shapes.extraLarge,
        color = AppTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 600.dp)
            .padding(vertical = 24.dp) // Optional extra padding if desired, but dialog handles it.
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.equalizer_header),
                        style = AppTheme.typography.headlineSmall
                    )
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.profiles_count,
                            count = profiles.size,
                            profiles.size
                        ),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onNavigateToAxion) {
                        SymbolIcon(symbol = sf("SFSliderHorizontal3"), contentDescription = stringResource(R.string.convx_equalizer))
                    }
                    IconButton(onClick = onImportCustomEQ) {
                        SymbolIcon(symbol = sf("SFPlus"), contentDescription = stringResource(R.string.import_profile))
                    }
                    IconButton(onClick = onOpenSystemEqualizer) {
                        SymbolIcon(symbol = sf("SFSliderHorizontal3"), contentDescription = stringResource(R.string.system_equalizer))
                    }
                }
            }

            // Profile list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // "No Equalization" option (always first)
                item {
                    NoEqualizationItem(
                        isSelected = activeProfileId == null,
                        onSelected = { onProfileSelected(null) }
                    )
                }

                // Custom profiles only
                val customProfiles = profiles.filter { it.isCustom }

                if (customProfiles.isNotEmpty()) {
                    items(customProfiles, key = { it.id }) { profile ->
                        EQProfileItem(
                            profile = profile,
                            isSelected = activeProfileId == profile.id,
                            onSelected = { onProfileSelected(profile.id) },
                            onDelete = { onDeleteProfile(profile.id) }
                        )
                    }
                }

                // Empty state
                if (customProfiles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                SymbolIcon(symbol = sf("slider.vertical.3"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.no_profiles),
                                    style = AppTheme.typography.titleMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onImportCustomEQ) {
                                    Text(stringResource(R.string.import_profile))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(onClick = onOpenSystemEqualizer) {
                                    Text(stringResource(R.string.system_equalizer))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoEqualizationItem(
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                stringResource(R.string.eq_disabled),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        leadingContent = {
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )
        },
        modifier = Modifier
            .bounceClick(onClick = onSelected)
            .padding(horizontal = 8.dp) // align with design
    )
}

@Composable
private fun EQProfileItem(
    profile: SavedEQProfile,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(
                text = profile.deviceModel,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        supportingContent = {
            Text(
                pluralStringResource(
                    id = R.plurals.band_count,
                    count = profile.bands.size,
                    profile.bands.size
                )
            )
        },
        leadingContent = {
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )
        },
        trailingContent = {
            IconButton(onClick = { showDeleteDialog = true }) {
                SymbolIcon(symbol = sf("trash"), contentDescription = stringResource(R.string.delete_profile_desc), tint = AppTheme.colorScheme.error)
            }
        },
        modifier = Modifier
            .bounceClick(onClick = onSelected)
            .padding(horizontal = 8.dp)
    )

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_profile_desc)) },
            text = {
                Text(
                    stringResource(R.string.delete_profile_confirmation, profile.name)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}
