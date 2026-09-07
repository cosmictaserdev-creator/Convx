/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import com.convxy.music.ui.component.GlassSwitchCompat as Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.LocalExcludedFoldersKey
import com.convxy.music.ui.screens.library.LocalMusicViewModel
import com.convxy.music.utils.LocalFolderIndex
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.decodeExcludedFolders
import com.convxy.music.utils.encodeExcludedFolders
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.launch

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
    val viewModel: LocalMusicViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val isScanning by viewModel.isScanning.collectAsState()

    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(audioPermission) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.scanDevice(context)
    }

    var folders by remember { mutableStateOf<List<LocalFolderIndex.Folder>>(emptyList()) }
    // Keyed on isScanning so the list refills itself when a scan finishes, instead of
    // showing the pre-scan folder set until the screen is reopened.
    LaunchedEffect(isScanning, hasPermission) {
        if (hasPermission && !isScanning) folders = LocalFolderIndex.load(context)
    }

    val (excludedRaw, _) = rememberPreference(LocalExcludedFoldersKey, defaultValue = "")
    val excluded = remember(excludedRaw) { decodeExcludedFolders(excludedRaw) }

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
            context.dataStore.edit { it[LocalExcludedFoldersKey] = encodeExcludedFolders(next) }
            viewModel.scanDevice(context)
        }
    }

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { treeUriToScannerPath(it) }?.let { setExcluded(it, true) }
    }

    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        Text(
            text = stringResource(R.string.excluded_folders_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        if (!hasPermission) {
            // Was a bare "scan from the Local screen first" line, which is a dead end on
            // a fresh install: the folder list is empty precisely because permission was
            // never granted, and nothing here offered to ask for it.
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.excluded_folders_permission),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(audioPermission) }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = { folderPicker.launch(null) },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
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
                    Icon(
                        painter = painterResource(R.drawable.refresh),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.scan_device))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (folders.isEmpty() && !isScanning) {
            Text(
                text = stringResource(R.string.excluded_folders_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
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
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
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
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
        }
        // Given its own callback rather than left as a decoration on a clickable row:
        // the switch is the obvious tap target, and it used to swallow the touch and
        // do nothing (see GlassSwitch).
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
