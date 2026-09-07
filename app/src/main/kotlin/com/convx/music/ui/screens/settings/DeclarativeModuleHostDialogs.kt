/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.modulehost.ModuleHostError
import com.convx.music.modulehost.UpdateOffer

/** Confirm removing an installed module. Shared by the list and detail screens. */
@Composable
fun ModuleHostRemoveDialog(
    moduleName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.module_host_remove_confirm_title)) },
        text = { Text(stringResource(R.string.module_host_remove_confirm_body, moduleName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.module_host_remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

/** Localized error dialog with the engine detail kept secondary. */
@Composable
fun ModuleHostErrorDialog(
    error: ModuleHostError?,
    onDismiss: () -> Unit,
) {
    if (error == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        icon = { Icon(painterResource(R.drawable.error), null) },
        title = { Text(stringResource(R.string.module_host_error)) },
        text = {
            Column {
                Text(stringResource(error.messageRes))
                if (error.detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                        Text(error.detail)
                    }
                }
            }
        },
    )
}

/** Offer dialog shown when a newer published release is available. */
@Composable
fun ModuleHostUpdateOfferDialog(
    offer: UpdateOffer?,
    busy: Boolean,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (offer == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.module_host_update_available_title)) },
        text = {
            Text(
                stringResource(
                    R.string.module_host_update_available_body,
                    offer.moduleName,
                    offer.release.version,
                    offer.installedVersion,
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onApply, enabled = !busy) {
                Text(stringResource(R.string.module_host_update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !busy) {
                Text(stringResource(R.string.module_host_later))
            }
        },
    )
}

/** Neutral OK-only info dialog (e.g. "up to date", "no compatible release"). */
@Composable
fun ModuleHostUpdateInfoDialog(
    messageRes: Int?,
    onDismiss: () -> Unit,
) {
    if (messageRes == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        title = { Text(stringResource(R.string.module_host_check_updates)) },
        text = { Text(stringResource(messageRes)) },
    )
}
