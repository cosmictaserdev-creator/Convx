package com.convx.music.ui.player

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.constants.CanvasSource
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CanvasSelectionRepository
import com.convx.music.ui.component.TextFieldDialog
import com.convx.music.ui.component.ModernSwitch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
internal fun CanvasSearchDialog(metadata: MediaMetadata, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedCanvas = CanvasPosterSource.controller
    DisposableEffect(sharedCanvas) {
        val token = Any()
        sharedCanvas?.holdForPreview(token)
        onDispose { sharedCanvas?.releasePreviewHold(token) }
    }
    var title by remember { mutableStateOf(TextFieldValue(metadata.title)) }
    var artist by remember { mutableStateOf(TextFieldValue(metadata.artists.firstOrNull()?.name.orEmpty())) }
    var source by remember { mutableStateOf(CanvasSource.SPOTIFY) }
    var rememberChoice by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<CanvasArtwork?>(null) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    TextFieldDialog(
        title = { Text(stringResource(R.string.search_canvas)) },
        textFields = listOf(stringResource(R.string.song_title) to title, stringResource(R.string.artist_name) to artist),
        onTextFieldsChange = { index, value ->
            if (!busy) {
                if (index == 0) title = value else artist = value
                result = null
                failed = false
            }
        },
        isInputValid = { it.isNotBlank() && !busy },
        autoDismiss = false,
        onDismiss = onDismiss,
        onDoneMultiple = { values ->
            val selected = result
            if (!busy) scope.launch {
                busy = true
                try {
                    if (selected != null) {
                        CanvasSelectionRepository.selectManually(context, metadata.id, source, selected, rememberChoice)
                        onDismiss()
                    } else {
                        result = CanvasSelectionRepository.searchManually(context, values[0], values[1], metadata.album?.title, source)
                        failed = result == null
                    }
                } catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { failed = true }
                finally { busy = false }
            }
        },
        extraContent = {
            Row {
                listOf(CanvasSource.SPOTIFY to "Spotify", CanvasSource.APPLE_MUSIC to "Apple Music").forEach { (provider, label) ->
                    TextButton(enabled = !busy, onClick = { source = provider; result = null; failed = false }) {
                        Text(if (source == provider) "✓ $label" else label)
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ModernSwitch(checked = rememberChoice, enabled = !busy, onCheckedChange = { rememberChoice = it })
                Text(stringResource(R.string.remember_canvas_for_song))
            }
            TextButton(enabled = !busy, onClick = {
                scope.launch {
                    busy = true
                    try {
                        CanvasSelectionRepository.clearManualSelection(context, metadata.id)
                        onDismiss()
                    } finally { busy = false }
                }
            }) { Text(stringResource(R.string.canvas_use_automatic)) }
            if (busy) Text(stringResource(R.string.canvas_search_working))
            if (failed) Text(stringResource(R.string.canvas_search_no_result))
            result?.let {
                CanvasArtworkPlayer(
                    primaryUrl = it.preferredAnimationUrl,
                    fallbackUrl = it.videoUrl,
                    isPlaying = true,
                    mediaId = metadata.id,
                    modifier = Modifier.size(160.dp),
                    onExhausted = { result = null; failed = true },
                )
                Text(stringResource(R.string.canvas_search_apply_hint))
            }
        },
    )
}
