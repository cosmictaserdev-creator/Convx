package com.convx.music.client.desktop

import androidx.compose.runtime.*
import com.convx.music.playback.PlaybackRequestTrack
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
internal fun DesktopDownloadAction(track: PlaybackRequestTrack?, size: Int = 30, menuItem: Boolean = false, onAction: () -> Unit = {}) {
    val library: DesktopMediaLibrary = koinInject()
    val manager: DesktopDownloadManager = koinInject()
    val status by remember(track?.providerId, track?.id, library) {
        library.state.map { state -> state.index.downloads.firstOrNull { track != null && it.track.id == track.id && it.track.providerId == track.providerId }?.status }.distinctUntilChanged()
    }.collectAsState(null)
    val active = status in listOf(DesktopDownloadStatus.QUEUED, DesktopDownloadStatus.DOWNLOADING)
    val complete = status == DesktopDownloadStatus.COMPLETE
    val label = when { complete -> "Downloaded"; active -> "Pause download"; status != null -> "Resume download"; else -> "Download song" }
    val enabled = track != null && track.providerId != LOCAL_MUSIC_PROVIDER && !complete
    val action: () -> Unit = {
        if (track != null) when {
            active -> manager.pause(DesktopDownloadManager.key(track))
            status != null -> manager.resume(DesktopDownloadManager.key(track))
            else -> manager.enqueue(listOf(track))
        }
        onAction()
    }
    if (menuItem) {
        dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = action, enabled = enabled) {
            Text(label)
        }
    } else {
        PlayerGlyphButton(if (complete) DesktopGlyphKind.SAVED else if (active) DesktopGlyphKind.PAUSE else DesktopGlyphKind.DOWNLOAD,
            label, selected = active || complete, size = size, icon = if (size > 35) 21 else 18,
            enabled = enabled, onClick = action)
    }
}
