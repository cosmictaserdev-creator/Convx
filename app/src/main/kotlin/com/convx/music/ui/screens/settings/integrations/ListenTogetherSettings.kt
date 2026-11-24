/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings.integrations

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.widget.Toast
import androidx.compose.foundation.background
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Switch
import androidx.compose.material3.SwitchDefaults
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.ListenTogetherAutoAddSuggestionsKey
import com.convx.music.constants.ListenTogetherAutoApprovalKey
import com.convx.music.constants.ListenTogetherServerUrlKey
import com.convx.music.constants.ListenTogetherSmartResyncKey
import com.convx.music.constants.ListenTogetherSyncVolumeKey
import com.convx.music.constants.ListenTogetherUsernameKey
import com.convx.music.listentogether.ListenTogetherEvent
import com.convx.music.listentogether.ListenTogetherServer
import com.convx.music.listentogether.ListenTogetherServers
import com.convx.music.listentogether.LogEntry
import com.convx.music.listentogether.LogLevel
import com.convx.music.listentogether.RoomRole
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IntegrationCard
import com.convx.music.ui.component.IntegrationCardItem
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.ListenTogetherViewModel
import kotlinx.coroutines.flow.collectLatest
import com.convx.music.ui.screens.settings.SettingsPage
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenTogetherSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ListenTogetherViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    val role by viewModel.role.collectAsStateWithLifecycle()
    val pendingJoinRequests by viewModel.pendingJoinRequests.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val blockedUsernames by viewModel.blockedUsernames.collectAsStateWithLifecycle()
    
    val servers = remember { ListenTogetherServers.servers }
    var serverUrl by rememberPreference(ListenTogetherServerUrlKey, ListenTogetherServers.defaultServerUrl)
    var username by rememberPreference(ListenTogetherUsernameKey, "")
    var autoApproval by rememberPreference(ListenTogetherAutoApprovalKey, false)
    var autoAddSuggestions by rememberPreference(ListenTogetherAutoAddSuggestionsKey, false)
    var syncHostVolume by rememberPreference(ListenTogetherSyncVolumeKey, true)
    var smartResync by rememberPreference(ListenTogetherSmartResyncKey, true)
    
    var showServerUrlDialog by rememberSaveable { mutableStateOf(false) }
    var showUsernameDialog by rememberSaveable { mutableStateOf(false) }
    var showCreateRoomDialog by rememberSaveable { mutableStateOf(false) }
    var showJoinRoomDialog by rememberSaveable { mutableStateOf(false) }
    var showLogsDialog by rememberSaveable { mutableStateOf(false) }
    var showBlockedUsersDialog by rememberSaveable { mutableStateOf(false) }
    var roomCodeInput by rememberSaveable { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ListenTogetherEvent.RoomCreated -> {
                    // Room created toast is shown globally by the client
                }
                is ListenTogetherEvent.JoinApproved -> {
                    Toast.makeText(context, "Joined room: ${event.roomCode}", Toast.LENGTH_SHORT).show()
                }
                is ListenTogetherEvent.JoinRejected -> {
                    Toast.makeText(context, "Join rejected: ${event.reason}", Toast.LENGTH_SHORT).show()
                }
                is ListenTogetherEvent.JoinRequestReceived -> {
                    Toast.makeText(context, "${event.username} wants to join", Toast.LENGTH_SHORT).show()
                }
                is ListenTogetherEvent.Kicked -> {
                    Toast.makeText(context, "Kicked: ${event.reason}", Toast.LENGTH_SHORT).show()
                }
                is ListenTogetherEvent.ConnectionError -> {
                    Toast.makeText(context, "Connection error: ${event.error}", Toast.LENGTH_SHORT).show()
                }
                is ListenTogetherEvent.ServerError -> {
                    Toast.makeText(context, "Error: ${event.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    // Dialogs
    if (showServerUrlDialog) {
        ServerChooserDialog(
            servers = servers,
            currentUrl = serverUrl,
            onSelect = { server ->
                serverUrl = server.url
                showServerUrlDialog = false
            },
            onUseCustom = { customUrl ->
                serverUrl = customUrl
                showServerUrlDialog = false
            },
            onDismiss = { showServerUrlDialog = false }
        )
    }
    
    if (showUsernameDialog) {
        var tempUsername by rememberSaveable(showUsernameDialog) { mutableStateOf(username) }

        DefaultDialog(
            onDismiss = { showUsernameDialog = false },
            icon = { SymbolIcon(symbol = sf("person.fill"), contentDescription = null) },
            title = { Text(stringResource(R.string.listen_together_username)) },
            buttons = {
                TextButton(onClick = { username = ""; showUsernameDialog = false }) {
                    Text(stringResource(R.string.reset))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { username = tempUsername.trim(); showUsernameDialog = false }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        ) {
            OutlinedTextField(
                value = tempUsername,
                onValueChange = { tempUsername = it },
                label = { Text(stringResource(R.string.listen_together_username)) },
                leadingIcon = {
                    SymbolIcon(symbol = sf("person.fill"), contentDescription = null)
                },
                trailingIcon = {
                    if (tempUsername.isNotBlank()) {
                        IconButton(onClick = { tempUsername = "" }, onLongClick = {}) {
                            SymbolIcon(symbol = sf("xmark"), contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    if (showCreateRoomDialog) {
        var createUsername by rememberSaveable(showCreateRoomDialog) { mutableStateOf(username) }

        DefaultDialog(
            onDismiss = { showCreateRoomDialog = false },
            icon = { SymbolIcon(symbol = sf("plus"), contentDescription = null) },
            title = { Text(stringResource(R.string.listen_together_create_room)) },
            buttons = {
                TextButton(onClick = { showCreateRoomDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val finalUsername = createUsername.trim()
                        if (finalUsername.isNotBlank()) {
                            username = finalUsername
                            viewModel.createRoom(finalUsername)
                            showCreateRoomDialog = false
                        } else {
                            Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = createUsername.trim().isNotBlank()
                ) {
                    Text(stringResource(R.string.create))
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.listen_together_create_room_desc),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = createUsername,
                    onValueChange = { createUsername = it },
                    label = { Text(stringResource(R.string.listen_together_username)) },
                    leadingIcon = {
                        SymbolIcon(symbol = sf("person.fill"), contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    
    if (showJoinRoomDialog) {
        var joinUsername by rememberSaveable(showJoinRoomDialog) { mutableStateOf(username) }

        DefaultDialog(
            onDismiss = { showJoinRoomDialog = false },
            icon = { SymbolIcon(symbol = sf("person.badge.plus"), contentDescription = null) },
            title = { Text(stringResource(R.string.listen_together_join_room)) },
            buttons = {
                TextButton(onClick = { showJoinRoomDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val finalUsername = joinUsername.trim()
                        if (finalUsername.isNotBlank() && roomCodeInput.length == 8) {
                            username = finalUsername
                            viewModel.joinRoom(roomCodeInput, finalUsername)
                            showJoinRoomDialog = false
                            roomCodeInput = ""
                        } else {
                            Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = joinUsername.trim().isNotBlank() && roomCodeInput.length == 8
                ) {
                    Text(stringResource(R.string.join))
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = joinUsername,
                    onValueChange = { joinUsername = it },
                    label = { Text(stringResource(R.string.listen_together_username)) },
                    leadingIcon = {
                        SymbolIcon(symbol = sf("person.fill"), contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = roomCodeInput,
                    onValueChange = { roomCodeInput = it.uppercase().filter { c -> c.isLetterOrDigit() }.take(8) },
                    label = { Text(stringResource(R.string.listen_together_room_code)) },
                    leadingIcon = {
                        SymbolIcon(symbol = sf("key.fill"), contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    
    if (showLogsDialog) {
        LogsDialog(
            logs = logs,
            onClear = { viewModel.clearLogs() },
            onDismiss = { showLogsDialog = false }
        )
    }

    if (showBlockedUsersDialog) {
        BlockedUsersDialog(
            blockedUsernames = blockedUsernames,
            onUnblock = { viewModel.unblockUser(it) },
            onDismiss = { showBlockedUsersDialog = false }
        )
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.listen_together),
        pageColor = AppTheme.colorScheme.background,
    ) {
        val selectedServer = remember(serverUrl) { ListenTogetherServers.findByUrl(serverUrl) }
        
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            IntegrationCard(
                title = stringResource(R.string.settings),
                items = listOf(
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.person),
                        title = { Text(stringResource(R.string.listen_together_blocked_users)) },
                        description = {
                            Text(
                                if (blockedUsernames.isNotEmpty()) 
                                    stringResource(R.string.listen_together_blocked_users_count, blockedUsernames.size)
                                else 
                                    stringResource(R.string.listen_together_no_blocked_users)
                            )
                        },
                        onClick = if (blockedUsernames.isNotEmpty()) {
                            { showBlockedUsersDialog = true }
                        } else null
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.cloud),
                        title = { Text(stringResource(R.string.listen_together_server_url)) },
                        description = {
                            Text(
                                selectedServer?.let { server ->
                                    "${server.name} - ${server.location}"
                                } ?: serverUrl,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { showServerUrlDialog = true }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.person),
                        title = { Text(stringResource(R.string.listen_together_username)) },
                        description = {
                            Text(username.ifEmpty { stringResource(R.string.not_set) })
                        },
                        onClick = if (roomState == null) {
                            { showUsernameDialog = true }
                        } else {
                            { Toast.makeText(context, context.getString(R.string.listen_together_cannot_edit_username_in_room), Toast.LENGTH_SHORT).show() }
                        }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.done),
                        title = { Text(stringResource(R.string.listen_together_auto_approval)) },
                        description = {
                            Text(stringResource(R.string.listen_together_auto_approval_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = autoApproval,
                                onCheckedChange = { autoApproval = it },
                                // Only disable for guests in a room (hosts can always change)
                                enabled = roomState == null || role != RoomRole.GUEST
                            )
                        },
                        // Allow clicking to see disabled state, but only change if enabled
                        onClick = { if (roomState == null || role != RoomRole.GUEST) autoApproval = !autoApproval }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.playlist_add),
                        title = { Text(stringResource(R.string.listen_together_auto_add)) },
                        description = {
                            Text(stringResource(R.string.listen_together_auto_add_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = autoAddSuggestions,
                                onCheckedChange = { autoAddSuggestions = it },
                                enabled = roomState == null || role != RoomRole.GUEST
                            )
                        },
                        onClick = {
                            if (roomState == null || role != RoomRole.GUEST) {
                                autoAddSuggestions = !autoAddSuggestions
                            }
                        }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.volume_up),
                        title = { Text(stringResource(R.string.listen_together_sync_volume)) },
                        description = {
                            Text(stringResource(R.string.listen_together_sync_volume_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = syncHostVolume,
                                onCheckedChange = { syncHostVolume = it }
                            )
                        },
                        onClick = { syncHostVolume = !syncHostVolume }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.automation_slow_connecttion),
                        title = { Text(stringResource(R.string.listen_together_smart_resync)) },
                        description = {
                            Text(stringResource(R.string.listen_together_smart_resync_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = smartResync,
                                onCheckedChange = { smartResync = it }
                            )
                        },
                        onClick = { smartResync = !smartResync }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.bug_report),
                        title = { Text(stringResource(R.string.listen_together_view_logs)) },
                        description = {
                            Text(stringResource(R.string.listen_together_view_logs_desc))
                        },
                        onClick = { showLogsDialog = true }
                    )
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    
    }
}

@Composable
fun LogsDialog(
    logs: List<LogEntry>,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }
    
    val context = LocalContext.current

    DefaultDialog(
        onDismiss = onDismiss,
        icon = { SymbolIcon(symbol = sf("ladybug"), contentDescription = null) },
        title = { Text(stringResource(R.string.listen_together_logs)) },
        buttons = {
            TextButton(
                onClick = {
                    val textToCopy = logs.joinToString("\n") { log ->
                        buildString {
                            append(log.timestamp)
                            append(" [")
                            append(log.level.name)
                            append("] ")
                            append(log.message)
                            log.details?.let { d -> append(" -- $d") }
                        }
                    }
                    val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("ListenTogetherLogs", textToCopy)
                    cm.setPrimaryClip(clip)
                    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                },
                enabled = logs.isNotEmpty()
            ) {
                Text(stringResource(R.string.copy))
            }
            TextButton(onClick = onClear) {
                Text(stringResource(R.string.clear))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.listen_together_no_logs),
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(logs) { log ->
                        LogEntryItem(log)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerChooserDialog(
    servers: List<ListenTogetherServer>,
    currentUrl: String,
    onSelect: (ListenTogetherServer) -> Unit,
    onUseCustom: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customUrl by rememberSaveable(currentUrl) { mutableStateOf(currentUrl) }
    val trimmedCustomUrl = customUrl.trim()

    DefaultDialog(
        onDismiss = onDismiss,
        icon = { SymbolIcon(symbol = sf("cloud.fill"), contentDescription = null) },
        title = { Text(stringResource(R.string.listen_together_choose_server)) },
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            servers.forEach { server ->
                val isSelected = server.url == currentUrl
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick { onSelect(server) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            AppTheme.colorScheme.primaryContainer
                        } else {
                            AppTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = server.name,
                                style = AppTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${server.location} - ${server.operator}",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = server.url,
                                style = AppTheme.typography.labelSmall,
                                color = AppTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (isSelected) {
                            SymbolIcon(symbol = sf("checkmark"), contentDescription = null, tint = AppTheme.colorScheme.primary)
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = stringResource(R.string.listen_together_custom_server),
                style = AppTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = customUrl,
                onValueChange = { customUrl = it },
                label = { Text(stringResource(R.string.listen_together_server_url)) },
                leadingIcon = {
                    SymbolIcon(symbol = sf("link"), contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onUseCustom(trimmedCustomUrl) },
                enabled = trimmedCustomUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.listen_together_use_custom_server))
            }
        }
    }
}

@Composable
fun LogEntryItem(log: LogEntry) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = log.timestamp,
                style = AppTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (log.level) {
                    LogLevel.ERROR -> AppTheme.colorScheme.errorContainer
                    LogLevel.WARNING -> Color(0xFFFFF3CD)
                    LogLevel.DEBUG -> AppTheme.colorScheme.surfaceVariant
                    LogLevel.INFO -> AppTheme.colorScheme.primaryContainer
                }
            ) {
                Text(
                    text = log.level.name,
                    style = AppTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    color = when (log.level) {
                        LogLevel.ERROR -> AppTheme.colorScheme.onErrorContainer
                        LogLevel.WARNING -> Color(0xFF856404)
                        LogLevel.DEBUG -> AppTheme.colorScheme.onSurfaceVariant
                        LogLevel.INFO -> AppTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }

        Text(
            text = log.message,
            style = AppTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace
        )
        log.details?.let { details ->
            Text(
                text = details,
                style = AppTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BlockedUsersDialog(
    blockedUsernames: Set<String>,
    onUnblock: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()

    DefaultDialog(
        onDismiss = onDismiss,
        icon = { SymbolIcon(symbol = sf("person.fill"), contentDescription = null) },
        title = { Text(stringResource(R.string.listen_together_blocked_users)) },
        buttons = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            if (blockedUsernames.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.listen_together_no_blocked_users),
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(blockedUsernames.toList(), key = { it }) { username ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SymbolIcon(symbol = sf("person.fill"), contentDescription = null, size = 20.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = username,
                                    style = AppTheme.typography.bodyMedium
                                )
                            }
                            TextButton(
                                onClick = { onUnblock(username) }
                            ) {
                                Text(stringResource(R.string.unblock))
                            }
                        }
                    }
                }
            }
        }
    }
}

