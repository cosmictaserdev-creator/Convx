/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.convxy.music.R

@Composable
fun RingtoneProgressDialog(
    isVisible: Boolean,
    progress: Float,
    statusMessage: String,
    isComplete: Boolean,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = {
            if (isComplete) onDismiss()
        },
        title = {
            Text(
                when {
                    isComplete && isSuccess -> stringResource(R.string.ringtone_success)
                    isComplete -> stringResource(R.string.ringtone_failed)
                    else -> stringResource(R.string.setting_ringtone)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isComplete && !isSuccess) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (!isComplete) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            if (isComplete) {
                Button(
                    onClick = {
                        if (isSuccess) onOpenSettings() else onDismiss()
                    },
                ) {
                    Text(if (isSuccess) stringResource(R.string.open_settings) else stringResource(R.string.close))
                }
            }
        },
        dismissButton = {
            if (isComplete && isSuccess) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    )
}
