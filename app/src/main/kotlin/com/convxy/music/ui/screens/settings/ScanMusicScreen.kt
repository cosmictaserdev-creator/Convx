/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.screens.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.component.NavigationTitle
import com.convxy.music.ui.screens.library.LocalMusicViewModel
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.utils.LocalFolderIndex

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
    viewModel: LocalMusicViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()
    val songs by viewModel.songs.collectAsStateWithLifecycle()

    var folders by remember { mutableStateOf<List<LocalFolderIndex.Folder>>(emptyList()) }
    // Reloaded when a scan finishes, not on a timer: folders only change when files do.
    LaunchedEffect(isScanning, songs.size) {
        if (!isScanning) {
            folders = runCatching { LocalFolderIndex.load(context) }.getOrDefault(emptyList())
        }
    }

    LazyColumn(
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxWidth(),
    ) {
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
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                // Persisted on the view model, so it survives leaving and returning to
                // this screen -- the one thing the old silent scan never gave anyone.
                scanResult?.let { result ->
                    Text(
                        text = stringResource(
                            R.string.scan_result_summary,
                            result.totalFound,
                            result.newSongs,
                            result.skippedExisting,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        Text(text = folder.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = folder.path,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = folder.songIds.size.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
