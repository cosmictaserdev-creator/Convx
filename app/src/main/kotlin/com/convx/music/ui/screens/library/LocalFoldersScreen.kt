package com.convx.music.ui.screens.library

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.ui.component.*
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.LocalFolderIndex
import androidx.compose.ui.platform.LocalContext

@Composable
fun LocalMusicFolderRow(folder: LocalFolderIndex.Folder, navController: NavController) {
    Row(
        Modifier.fillMaxWidth().clickable { navController.navigate("local_folder/" + Uri.encode(folder.path)) }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SymbolIcon(symbol = sf("folder.fill"), contentDescription = null, size = 28.dp, tint = AppTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(folder.name, style = AppTheme.typography.bodyLarge)
            Text(folder.path, style = AppTheme.typography.bodySmall, color = AppTheme.colorScheme.onSurfaceVariant)
        }
        Text(folder.songIds.size.toString(), color = AppTheme.colorScheme.onSurfaceVariant)
        SymbolIcon(symbol = sf("chevron.right"), contentDescription = "Open ${folder.name}", size = 16.dp)
    }
}

@Composable
fun LocalMusicFoldersEntry(count: Int?, navController: NavController) {
    Row(
        Modifier.fillMaxWidth().clickable { navController.navigate("local_folders") }
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SymbolIcon(symbol = sf("folder.fill"), contentDescription = null, tint = AppTheme.colorScheme.primary)
        Text("Folders", style = AppTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (count != null) Text(count.toString(), color = AppTheme.colorScheme.onSurfaceVariant)
        SymbolIcon(symbol = sf("chevron.right"), contentDescription = "Browse local folders", size = 16.dp)
    }
}

@Composable
fun LocalFoldersScreen(navController: NavController, viewModel: LocalMusicViewModel = koinViewModel()) {
    val context = LocalContext.current
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val scanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val permission = rememberLocalAudioPermission { }
    LaunchedEffect(permission.granted) { if (permission.granted) viewModel.scanDevice(context, force = false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()) {
        item {
            Text("Folders", style = AppTheme.typography.headlineLarge, modifier = Modifier.padding(24.dp))
        }
        if (!permission.granted) {
            item { LocalMusicPermissionPrompt(permission) }
        } else {
            items(folders, key = { it.path }) { LocalMusicFolderRow(it, navController) }
            if (folders.isEmpty()) item {
                Text(if (scanning) "Scanning device…" else "No music folders found", modifier = Modifier.padding(24.dp))
            }
        }
    }
}
