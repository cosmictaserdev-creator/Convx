/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.ios.compat.AlertDialog
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.viewmodels.NetworkStorageViewModel
import com.convx.storage.domain.NetworkProtocol
import com.convx.storage.domain.NetworkServer

/** SMB and WebDAV music sources: add a server, test it, and scan its audio into the library. */
@Composable
fun NetworkStorageSettingsScreen(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER") scrollBehavior: TopAppBarScrollBehavior,
) {
    val viewModel: NetworkStorageViewModel = koinViewModel()
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    val busyId by viewModel.busyId.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<NetworkServer?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.network_storage),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 24.dp,
    ) {
        Text(
            text = stringResource(R.string.network_storage_desc),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )

        Button(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
            SymbolIcon(symbol = sf("plus"), contentDescription = null, size = 18.dp)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.network_storage_add))
        }

        status?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, style = AppTheme.typography.bodyMedium, color = AppTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(20.dp))

        if (servers.isEmpty()) {
            Text(
                text = stringResource(R.string.network_storage_empty),
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
                    servers.forEachIndexed { index, server ->
                        ServerRow(
                            server = server,
                            busy = busyId == server.id,
                            enabled = busyId == null,
                            onTest = { viewModel.test(server) },
                            onScan = { viewModel.scan(server) },
                            onDelete = { deleting = server },
                        )
                        if (index != servers.lastIndex) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showAdd) {
        AddServerDialog(
            onDismiss = { showAdd = false },
            onSave = { protocol, name, address, user, password, domain ->
                viewModel.add(protocol, name, address, user, password, domain)
                showAdd = false
            },
        )
    }

    deleting?.let { server ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.network_storage_remove_title, server.name)) },
            text = { Text(stringResource(R.string.network_storage_remove_text)) },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(server); deleting = null }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text(stringResource(android.R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun ServerRow(
    server: NetworkServer,
    busy: Boolean,
    enabled: Boolean,
    onTest: () -> Unit,
    onScan: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(server.name, style = AppTheme.typography.titleSmall)
                Text(
                    text = (if (server.protocol == NetworkProtocol.SMB) "SMB  " else "WebDAV  ") + server.address,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onTest, enabled = enabled) { Text(stringResource(R.string.network_storage_test)) }
            Button(onClick = onScan, enabled = enabled) { Text(stringResource(R.string.network_storage_scan)) }
            TextButton(onClick = onDelete, enabled = enabled) { Text(stringResource(R.string.network_storage_remove)) }
        }
    }
}

@Composable
private fun AddServerDialog(
    onDismiss: () -> Unit,
    onSave: (NetworkProtocol, String, String, String, String, String) -> Unit,
) {
    var protocol by remember { mutableStateOf(NetworkProtocol.SMB) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.network_storage_add)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (protocol == NetworkProtocol.SMB) {
                        Button(onClick = {}) { Text("SMB") }
                        OutlinedButton(onClick = { protocol = NetworkProtocol.WEBDAV }) { Text("WebDAV") }
                    } else {
                        OutlinedButton(onClick = { protocol = NetworkProtocol.SMB }) { Text("SMB") }
                        Button(onClick = {}) { Text("WebDAV") }
                    }
                }
                OutlinedTextField(name, { name = it }, label = { Text(stringResource(R.string.network_storage_name)) }, singleLine = true)
                OutlinedTextField(
                    address, { address = it },
                    label = {
                        Text(
                            stringResource(
                                if (protocol == NetworkProtocol.SMB) R.string.network_storage_address_smb
                                else R.string.network_storage_address_webdav,
                            ),
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                )
                OutlinedTextField(user, { user = it }, label = { Text(stringResource(R.string.network_storage_user)) }, singleLine = true)
                OutlinedTextField(
                    password, { password = it },
                    label = { Text(stringResource(R.string.network_storage_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                if (protocol == NetworkProtocol.SMB) {
                    OutlinedTextField(domain, { domain = it }, label = { Text(stringResource(R.string.network_storage_domain)) }, singleLine = true)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(protocol, name, address, user, password, domain) },
                enabled = name.isNotBlank() && address.isNotBlank(),
            ) { Text(stringResource(R.string.network_storage_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) } },
    )
}
