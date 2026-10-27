/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.LocalExcludedFoldersKey
import com.convx.music.ui.screens.library.LocalMusicViewModel
import com.convx.music.utils.LocalFolderIndex
import com.convx.music.utils.dataStore
import com.convx.music.utils.decodeExcludedFolders
import com.convx.music.utils.encodeExcludedFolders
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Which on-device folders local-only mode is allowed to scan. Backed by a single
 * excluded-paths preference (see LocalExcludedFoldersKey) — toggling a folder off
 * triggers an immediate rescan so it actually disappears, not just stops growing.
 *
 * The folder list comes from MediaStore, so it only ever contains folders that
 * currently hold music. A folder can also be excluded by hand with the picker, which
 * is the only way to pre-empt one that has no music in it yet.
 */
@Composable
fun LocalFoldersSettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val viewModel: LocalMusicViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    val hasPermission = permission.granted
    LaunchedEffect(hasPermission) { if (hasPermission) viewModel.scanDevice(context, force = false) }

    var folders by remember { mutableStateOf<List<LocalFolderIndex.Folder>>(emptyList()) }
    // Keyed on isScanning so the list refills itself when a scan finishes, instead of
    // showing the pre-scan folder set until the screen is reopened.
    LaunchedEffect(isScanning, hasPermission) {
        if (hasPermission && !isScanning) folders = LocalFolderIndex.load(context)
    }

    val (excludedRaw, _) = rememberPreference(LocalExcludedFoldersKey, defaultValue = "")
    val excluded = remember(excludedRaw) { decodeExcludedFolders(excludedRaw) }

    suspend fun saveExcludedFolders(paths: Set<String>) {
        context.dataStore.edit { it[LocalExcludedFoldersKey] = encodeExcludedFolders(paths) }
        viewModel.scanDevice(context)
    }

    /** Paths excluded by hand that MediaStore doesn't report — no music in them (yet). */
    val excludedNotListed = remember(excluded, folders) {
        excluded.filter { path -> folders.none { it.path == path } }.sorted()
    }

    val setExcluded: (String, Boolean) -> Unit = { path, exclude ->
        coroutineScope.launch {
            // Write and await before rescanning — the scan reads this same key straight
            // off disk, and racing it against a fire-and-forget write meant the rescan
            // could still see the pre-toggle set and never drop the folder's songs.
            val next = if (exclude) excluded + path else excluded - path
            saveExcludedFolders(next)
        }
    }

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { treeUriToScannerPath(it) }?.let { setExcluded(it, true) }
    }

    // "Only this folder": one pick excludes every listed folder outside it, instead of
    // unticking them one by one. Works for a parent folder too -- MediaStore only lists
    // the leaf folders that hold songs, so the parent itself never shows as a row.
    val onlyFolderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        val keep = uri?.let { treeUriToScannerPath(it) } ?: return@rememberLauncherForActivityResult
        coroutineScope.launch {
            val next = folders.map { it.path }
                .filterNot { it == keep || it.startsWith("$keep/") }
                .toSet()
            saveExcludedFolders(next)
        }
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.excluded_folders),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 24.dp,
    ) {

        Text(
            text = stringResource(R.string.excluded_folders_desc),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        if (!hasPermission) {
            com.convx.music.ui.component.LocalMusicPermissionPrompt(permission)
            Spacer(Modifier.height(24.dp))
            return@SettingsPage
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = { folderPicker.launch(null) },
                modifier = Modifier.weight(1f),
            ) {
                SymbolIcon(symbol = sf("plus"), contentDescription = null, size = 18.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.exclude_folder_add))
            }
            Button(
                onClick = { viewModel.scanDevice(context) },
                enabled = !isScanning,
                modifier = Modifier.weight(1f),
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    SymbolIcon(symbol = sf("arrow.clockwise"), contentDescription = null, size = 18.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.scan_device))
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { onlyFolderPicker.launch(null) },
            enabled = !isScanning && folders.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.scan_only_folder))
        }

        Spacer(Modifier.height(20.dp))

        if (folders.isEmpty() && !isScanning) {
            Text(
                text = stringResource(R.string.excluded_folders_empty),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    folders.forEachIndexed { index, folder ->
                        FolderRow(
                            name = folder.name,
                            detail = pluralSongs(folder.songIds.size),
                            included = folder.path !in excluded,
                            onToggle = { include -> setExcluded(folder.path, !include) },
                        )
                        if (index != folders.lastIndex) {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }

        if (excludedNotListed.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.excluded_folders_custom),
                style = AppTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    excludedNotListed.forEachIndexed { index, path ->
                        FolderRow(
                            name = path.substringAfterLast('/').ifEmpty { path },
                            detail = path,
                            included = false,
                            onToggle = { include -> setExcluded(path, !include) },
                        )
                        if (index != excludedNotListed.lastIndex) {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FolderRow(
    name: String,
    detail: String,
    included: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!included) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
        ) {
            Text(
                text = name,
                style = AppTheme.typography.bodyLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = detail,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
        }
        // Give the switch its own callback so tapping it changes the folder state.
        Switch(checked = included, onCheckedChange = onToggle)
    }
}

@Composable
private fun pluralSongs(count: Int): String =
    if (count == 1) {
        stringResource(R.string.excluded_folders_song_count_one)
    } else {
        stringResource(R.string.excluded_folders_song_count, count)
    }

/**
 * SAF hands back a document id like "primary:Music/Rock". The scanner compares against
 * MediaStore's RELATIVE_PATH on Q+ ("Music/Rock"), and against the DATA directory below
 * that on older releases, so the shape has to match the platform it will be checked on.
 */
private fun treeUriToScannerPath(uri: Uri): String? = runCatching {
    val docId = DocumentsContract.getTreeDocumentId(uri) ?: return null
    val volume = docId.substringBefore(':')
    val relative = docId.substringAfter(':', "").trim('/')
    if (relative.isEmpty()) return null
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> relative
        volume == "primary" -> "/storage/emulated/0/$relative"
        else -> "/storage/$volume/$relative"
    }
}.getOrNull()
