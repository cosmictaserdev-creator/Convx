/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.ui.component.NavigationTitle
import com.convx.music.ui.screens.library.LocalMusicViewModel
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.utils.LocalFolderIndex

/**
 * What the device scan is doing, and what it found.
 *
 * The folder settings screen already lets a user include and exclude directories, but a
 * scan itself was invisible: it ran from Home's pull-to-refresh and from entering the
 * local library, reported nothing while it worked, and left no record of what changed.
 * On a large library that is a long silence with no way to tell a slow scan from a
 * broken one.
 */
@Composable
fun ScanMusicScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: LocalMusicViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    LaunchedEffect(permission.granted) {
        if (permission.granted) viewModel.scanDevice(context, force = false)
    }

    var folders by remember { mutableStateOf<List<LocalFolderIndex.Folder>>(emptyList()) }
    // Reloaded when a scan finishes, not on a timer: folders only change when files do.
    LaunchedEffect(isScanning, songs.size, permission.granted) {
        if (permission.granted && !isScanning) {
            folders = runCatching { LocalFolderIndex.load(context) }.getOrDefault(emptyList())
        } else if (!permission.granted) folders = emptyList()
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.scan_device),
        pageColor = AppTheme.colorScheme.background,
    ) {
        if (!permission.granted) {
            item(key = "audio_access") { com.convx.music.ui.component.LocalMusicPermissionPrompt(permission) }
            return@SettingsLazyPage
        }
        item(key = "status") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppleTokens.Gutter),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp))
                    }
                    Text(
                        text = if (isScanning) {
                            stringResource(R.string.scanning_local_files)
                        } else {
                            pluralStringResource(R.plurals.n_song, songs.size, songs.size)
                        },
                        style = AppTheme.typography.titleMedium,
                    )
                }

                // Keep scan results available when returning to this screen.
                scanResult?.let { result ->
                    Text(
                        text = stringResource(
                            R.string.scan_result_summary,
                            result.totalFound,
                            result.newSongs,
                            result.skippedExisting,
                        ),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Button(
                    onClick = { viewModel.scanDevice(context, force = true) },
                    enabled = !isScanning,
                ) {
                    Text(stringResource(R.string.scan_device))
                }

                Button(
                    onClick = { navController.navigate("settings/player/local_folders") },
                ) {
                    Text(stringResource(R.string.excluded_folders))
                }

                Button(
                    onClick = { navController.navigate("settings/network_storage") },
                ) {
                    Text(stringResource(R.string.network_storage))
                }
            }
        }

        if (folders.isNotEmpty()) {
            item(key = "folders_title") {
                NavigationTitle(title = stringResource(R.string.folders))
            }
            items(folders, key = { it.path }) { folder ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppleTokens.Gutter, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = folder.name, style = AppTheme.typography.bodyLarge)
                        Text(
                            text = folder.path,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = folder.songIds.size.toString(),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
