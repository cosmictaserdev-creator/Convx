/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.convx.music.R
import com.convx.music.ai.AiPlaylistModifier
import com.convx.music.db.MusicDatabase
import com.convx.music.library.LocalPlaylistSong
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AiModifyPlaylistDialog(
    playlistId: String,
    currentSongs: List<LocalPlaylistSong>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val database = koinInject<MusicDatabase>()
    val coroutineScope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var logs by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            if (!isProcessing) {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = AppTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.modify_with_ai),
                    style = AppTheme.typography.headlineSmall,
                    color = AppTheme.colorScheme.onSurface
                )

                if (!isProcessing) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text(stringResource(R.string.modify_with_ai_desc)) },
                        placeholder = { Text(stringResource(R.string.ai_modify_prompt_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = true,
                        minLines = 3
                    )

                    if (logs.isNotEmpty()) {
                        Text(text = "Logs:", style = AppTheme.typography.bodyMedium)
                        val scrollState = rememberScrollState()
                        Text(
                            text = logs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 120.dp)
                                .verticalScroll(scrollState),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(android.R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    isProcessing = true
                                    logs = "Starting modification...\n"
                                    coroutineScope.launch {
                                        AiPlaylistModifier.modifyPlaylist(
                                            database = database,
                                            context = context,
                                            playlistId = playlistId,
                                            currentSongs = currentSongs,
                                            userPrompt = prompt,
                                            onLog = { newLog ->
                                                logs = newLog + "\n" + logs
                                            }
                                        )
                                        isProcessing = false
                                        onDismiss()
                                    }
                                }
                            },
                            enabled = prompt.isNotBlank()
                        ) {
                            Text("Start")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AppTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        val latestLog = logs.split("\n").firstOrNull { it.isNotBlank() } ?: "Processing..."
                        Text(
                            text = latestLog,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
