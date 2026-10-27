package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import org.koin.compose.koinInject
import java.io.File

@Composable
internal fun DesktopStorageSettings(settings: PlaybackSettingsStateHolder) {
    val library: DesktopMediaLibrary = koinInject()
    val downloads: DesktopDownloadManager = koinInject()
    val media by library.state.collectAsState()
    val preferences by settings.state.collectAsState()
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Music and storage", fontSize = 23.sp, fontWeight = FontWeight.Bold)
        ThemeToggle("Local mode", preferences.localMode, settings::setLocalMode)
        Text("Local mode uses your music folders and completed downloads. Streaming and online lyrics lookups stay off.", color = DesktopMuted, fontSize = 13.sp)
        Text("${media.index.tracks.size} songs · ${media.index.tracks.sumOf { it.bytes } / (1024 * 1024)} MB", color = DesktopMuted, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MediaButton("Add music folder") { chooseMediaFolder(library::addFolder) }
            MediaButton("Rescan", enabled = !media.scanning, onClick = library::rescan)
        }
        if (media.scanning) Text("Scanning ${media.scannedFiles} / ${media.totalFiles}", color = DesktopMuted, fontSize = 13.sp)
        media.index.folders.forEach { folder -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(folder, Modifier.weight(1f), fontSize = 13.sp)
            MediaButton("Remove") { library.removeFolder(folder) }
        } }
        Text("Downloads", fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
        val directory = media.index.downloadDirectory ?: File(System.getProperty("user.home"), "Music/Convx/Downloads").path
        Text(directory, color = DesktopMuted, fontSize = 13.sp)
        Text("New downloads use this folder. Existing files keep their current location.", color = DesktopMuted, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MediaButton("Choose folder") { chooseMediaFolder(downloads::setDirectory) }
            MediaButton("Resume all", onClick = downloads::resumeAll)
            MediaButton("Pause all", onClick = downloads::pauseAll)
        }
        media.message?.let { Text(it, color = DesktopMuted, fontSize = 13.sp) }
    }
}
