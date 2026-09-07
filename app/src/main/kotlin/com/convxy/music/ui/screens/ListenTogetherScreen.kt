/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.platform.LocalLayoutDirection
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convxy.music.ui.component.DefaultDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.AppBarHeight
import com.convxy.music.constants.ListenTogetherInTopBarKey
import com.convxy.music.constants.ListenTogetherUsernameKey
import com.convxy.music.listentogether.ConnectionState
import com.convxy.music.listentogether.JoinRequestPayload
import com.convxy.music.listentogether.ListenTogetherEvent
import com.convxy.music.listentogether.MAX_ROOM_CODE_LENGTH
import com.convxy.music.listentogether.MIN_ROOM_CODE_LENGTH
import com.convxy.music.listentogether.SuggestionReceivedPayload
import com.convxy.music.listentogether.UserInfo
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListenTogetherScreen(
    navController: NavController,
    showTopBar: Boolean = false
) {
    val context = LocalContext.current
    val listenTogetherManager = LocalListenTogetherManager.current
    val windowInsets = LocalPlayerAwareWindowInsets.current

    if (listenTogetherManager == null) {
        NotConfiguredContent()
        return
    }

    val connectionState by listenTogetherManager.connectionState.collectAsState()
    val roomState by listenTogetherManager.roomState.collectAsState()
    val userId by listenTogetherManager.userId.collectAsState()
    val pendingJoinRequests by listenTogetherManager.pendingJoinRequests.collectAsState()
    val pendingSuggestions by listenTogetherManager.pendingSuggestions.collectAsState()
    val controlMode by listenTogetherManager.controlMode.collectAsState()
    val roomExpiresAt by listenTogetherManager.roomExpiresAt.collectAsState()
    val roomExpiring by listenTogetherManager.roomExpiring.collectAsState()
    val extensionsLeft by listenTogetherManager.extensionsLeft.collectAsState()

    val (listenTogetherInTopBar) = rememberPreference(ListenTogetherInTopBarKey, defaultValue = true)
    val shouldShowTopBar = showTopBar || listenTogetherInTopBar
    
    var savedUsername by rememberPreference(ListenTogetherUsernameKey, "")
    var roomCodeInput by rememberSaveable { mutableStateOf("") }
    var usernameInput by rememberSaveable { mutableStateOf(savedUsername) }

    var isCreatingRoom by rememberSaveable { mutableStateOf(false) }
    var isJoiningRoom by rememberSaveable { mutableStateOf(false) }
    var joinErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedUserForMenu by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedUsername by rememberSaveable { mutableStateOf<String?>(null) }

    val waitingForApprovalText = stringResource(R.string.waiting_for_approval)
    val invalidRoomCodeText = stringResource(R.string.invalid_room_code)
    val joinRequestDeniedText = stringResource(R.string.join_request_denied)

    LaunchedEffect(savedUsername) {
        if (usernameInput.isBlank() && savedUsername.isNotBlank()) {
            usernameInput = savedUsername
        }
    }

    LaunchedEffect(listenTogetherManager) {
        listenTogetherManager.events.collect { event ->
            when (event) {
                is ListenTogetherEvent.JoinRejected -> {
                    val reason = event.reason
                    joinErrorMessage = when {
                        reason.isNullOrBlank() -> joinRequestDeniedText
                        reason.contains("invalid", ignoreCase = true) -> invalidRoomCodeText
                        else -> "$joinRequestDeniedText: $reason"
                    }
                    isJoiningRoom = false
                    isCreatingRoom = false
                }
                is ListenTogetherEvent.JoinApproved -> {
                    isJoiningRoom = false
                    joinErrorMessage = null
                }
                is ListenTogetherEvent.RoomCreated -> {
                    isCreatingRoom = false
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("ListenTogetherRoom", event.roomCode)
                    clipboard.setPrimaryClip(clip)
                }
                else -> {}
            }
        }
    }

    val isInRoom = listenTogetherManager.isInRoom
    val isHost = roomState?.hostId == userId

    // User action menu dialog
    if (selectedUserForMenu != null && selectedUsername != null) {
        UserActionDialog(
            username = selectedUsername ?: "",
            onKick = {
                selectedUserForMenu?.let {
                    listenTogetherManager.kickUser(it, "Removed by host")
                }
                selectedUserForMenu = null
                selectedUsername = null
            },
            onPermanentKick = {
                selectedUserForMenu?.let { userId ->
                    selectedUsername?.let { username ->
                        listenTogetherManager.blockUser(username)
                        listenTogetherManager.kickUser(userId, R.string.user_blocked_by_host.toString())
                    }
                }
                selectedUserForMenu = null
                selectedUsername = null
            },
            onTransferOwnership = {
                selectedUserForMenu?.let {
                    listenTogetherManager.transferHost(it)
                }
                selectedUserForMenu = null
                selectedUsername = null
            },
            onDismiss = {
                selectedUserForMenu = null
                selectedUsername = null
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop = backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()
    
    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            lazyListState.animateScrollToItem(0)
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        contentPadding = PaddingValues(
            // Include the side-panel inset so content clears the floating side bar
            // in tab view instead of sitting under it.
            start = windowInsets.asPaddingValues().calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = windowInsets.asPaddingValues().calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            top = windowInsets.asPaddingValues().calculateTopPadding() + 16.dp,
            bottom = windowInsets.asPaddingValues().calculateBottomPadding() + 16.dp + AppBarHeight
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            HeaderSection(isInRoom = isInRoom)
        }

        // Connection status card
        item {
            ConnectionStatusCard(
                connectionState = connectionState,
                onConnect = { listenTogetherManager.connect() },
                onDisconnect = { listenTogetherManager.disconnect() },
                onReconnect = { listenTogetherManager.forceReconnect() }
            )
        }

        if (connectionState == ConnectionState.CONNECTED && !isInRoom) {
            item {
                Text(
                    text = stringResource(R.string.listen_together_background_disconnect_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (isInRoom) {
            // Room status card
            roomState?.let { room ->
                item {
                    RoomStatusCard(
                        roomCode = room.roomCode,
                        isHost = isHost,
                        context = context,
                        navController = navController
                    )
                }

                // Session countdown. Rooms are a shared, temporary resource —
                // people should see it coming rather than be cut off.
                if (roomExpiresAt > 0L) {
                    item {
                        SessionCountdownCard(
                            expiresAt = roomExpiresAt,
                            expiring = roomExpiring,
                            isHost = isHost,
                            extensionsLeft = extensionsLeft,
                            onExtend = { listenTogetherManager.extendSession() }
                        )
                    }
                }

                // Who may drive playback. Host-only control; the server enforces it.
                if (isHost) {
                    item {
                        ControlModeCard(
                            controlMode = controlMode,
                            onChange = { listenTogetherManager.setControlMode(it) }
                        )
                    }
                }

                // Connected users
                val connectedUsers = room.users.filter { it.isConnected }
                val currentUserIdValue = userId ?: ""
                item {
                    ConnectedUsersSection(
                        users = connectedUsers,
                        isHost = isHost,
                        currentUserId = currentUserIdValue,
                        onUserClick = { clickedUserId, username ->
                            if (isHost && clickedUserId != currentUserIdValue) {
                                selectedUserForMenu = clickedUserId
                                selectedUsername = username
                            }
                        }
                    )
                }

                // Pending join requests (host only)
                if (isHost && pendingJoinRequests.isNotEmpty()) {
                    item {
                        PendingJoinRequestsSection(
                            requests = pendingJoinRequests,
                            onApprove = { listenTogetherManager.approveJoin(it) },
                            onReject = { listenTogetherManager.rejectJoin(it, "Rejected by host") }
                        )
                    }
                }

                // Pending suggestions (host only)
                if (isHost && pendingSuggestions.isNotEmpty()) {
                    item {
                        PendingSuggestionsSection(
                            suggestions = pendingSuggestions,
                            onApprove = { listenTogetherManager.approveSuggestion(it) },
                            onReject = { listenTogetherManager.rejectSuggestion(it, "Rejected by host") }
                        )
                    }
                }

                // Leave room. A solid error-red bar made the least likely action
                // the loudest thing on the screen — red text on the surface is
                // still unmistakably destructive without shouting.
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ContinuousRoundedRectangle(percent = 50))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .bounceClick(onClick = { listenTogetherManager.leaveRoom() })
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(9.dp))
                        Text(
                            text = stringResource(R.string.leave_room),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        } else {
            // Join/Create room section
            item {
                JoinCreateRoomSection(
                    usernameInput = usernameInput,
                    onUsernameChange = { usernameInput = it },
                    roomCodeInput = roomCodeInput,
                    onRoomCodeChange = { roomCodeInput = it },
                    savedUsername = savedUsername,
                    isJoiningRoom = isJoiningRoom,
                    joinErrorMessage = joinErrorMessage,
                    waitingForApprovalText = waitingForApprovalText,
                    bringIntoViewRequester = bringIntoViewRequester,
                    onCreateRoom = {
                        val username = usernameInput.takeIf { it.isNotBlank() } ?: savedUsername
                        val finalUsername = username.trim()
                        if (finalUsername.isNotBlank()) {
                            savedUsername = finalUsername
                            Toast.makeText(context, R.string.creating_room, Toast.LENGTH_SHORT).show()
                            isCreatingRoom = true
                            isJoiningRoom = false
                            joinErrorMessage = null
                            // createRoom now allocates the code and opens the
                            // socket against that room itself — a bare connect()
                            // has no room to dial and would be refused.
                            listenTogetherManager.createRoom(finalUsername)
                        } else {
                            Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onJoinRoom = {
                        val username = usernameInput.takeIf { it.isNotBlank() } ?: savedUsername
                        val finalUsername = username.trim()
                        if (finalUsername.isNotBlank()) {
                            savedUsername = finalUsername
                            Toast.makeText(
                                context,
                                context.getString(R.string.joining_room, roomCodeInput),
                                Toast.LENGTH_SHORT
                            ).show()
                            isJoiningRoom = true
                            isCreatingRoom = false
                            joinErrorMessage = null
                            listenTogetherManager.joinRoom(roomCodeInput, finalUsername)
                        } else {
                            Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFieldFocused = {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                )
            }
        }

        // Settings link
        item {
            SettingsLinkCard(
                onClick = { navController.navigate("settings/integrations/listen_together") }
            )
        }
    }

    if (shouldShowTopBar) {
        TopAppBar(
            title = { Text(stringResource(R.string.together)) },
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
private fun NotConfiguredContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.group),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.listen_together),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.listen_together_not_configured),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeaderSection(isInRoom: Boolean = false) {
    if (isInRoom) return

    // The 80dp icon medallion is gone. A screen titled "Listen together" does
    // not need a picture of two people next to the words "Listen together" —
    // that space belongs to the type.
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.listen_together),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.listen_together_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConnectionStatusCard(
    connectionState: ConnectionState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onReconnect: () -> Unit
) {
    // Connection is plumbing, not content. It used to be a full-width coloured
    // panel with two buttons, which made the least interesting thing on the
    // screen the loudest. Now it is a status line, and it only offers an action
    // when something is actually wrong.
    val broken = connectionState == ConnectionState.ERROR ||
        connectionState == ConnectionState.DISCONNECTED
    val busy = connectionState == ConnectionState.CONNECTING ||
        connectionState == ConnectionState.RECONNECTING

    val tone = when (connectionState) {
        ConnectionState.CONNECTED -> MaterialTheme.colorScheme.primary
        ConnectionState.CONNECTING, ConnectionState.RECONNECTING -> MaterialTheme.colorScheme.tertiary
        ConnectionState.ERROR -> MaterialTheme.colorScheme.error
        ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(11.dp),
                strokeWidth = 2.dp,
                color = tone
            )
        } else {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(tone)
            )
        }
        Spacer(Modifier.width(9.dp))
        Text(
            text = when (connectionState) {
                ConnectionState.CONNECTED -> stringResource(R.string.listen_together_connected)
                ConnectionState.CONNECTING -> stringResource(R.string.listen_together_connecting)
                ConnectionState.RECONNECTING -> stringResource(R.string.listen_together_reconnecting)
                ConnectionState.ERROR -> stringResource(R.string.listen_together_error)
                ConnectionState.DISCONNECTED -> stringResource(R.string.listen_together_disconnected)
            },
            style = MaterialTheme.typography.bodySmall,
            color = tone,
            modifier = Modifier.weight(1f)
        )
        if (broken) {
            Text(
                text = stringResource(R.string.connect),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .bounceClick(onClick = onConnect)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun RoomStatusCard(
    roomCode: String,
    isHost: Boolean,
    context: Context,
    navController: NavController
) {
    val accent = MaterialTheme.colorScheme.primary
    val copiedText = stringResource(R.string.copied_to_clipboard)

    fun copy(label: String, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText(label, value))
        Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.30f), accent.copy(alpha = 0.08f))
                )
            )
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = if (isHost) {
                    stringResource(R.string.listen_together_you_are_host)
                } else {
                    stringResource(R.string.listen_together_you_are_guest)
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(16.dp))

        // One tile per character. A room code gets read aloud and typed by hand,
        // so it is spaced like a code rather than run together as a word.
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.combinedBounceClick(
                onClick = { copy("Room Code", roomCode) },
                onLongClick = { copy("Room Code", roomCode) }
            )
        ) {
            roomCode.forEach { ch ->
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ch.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.listen_together_tap_code_to_copy),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RoomAction(
                icon = R.drawable.chat_msg,
                label = stringResource(R.string.comments),
                onClick = { navController.navigate("listen_together/chat") }
            )
            RoomAction(
                icon = R.drawable.content_copy,
                label = stringResource(R.string.copy_code),
                onClick = { copy("Room Code", roomCode) }
            )
            if (isHost) {
                RoomAction(
                    icon = R.drawable.link,
                    label = stringResource(R.string.copy_link),
                    onClick = {
                        // Deep link into the app, not a hardcoded web host. The
                        // previous link pointed at the old Render server, which
                        // is not even where the room lives any more.
                        copy("Listen Together Link", "vivimusic://listen?code=$roomCode")
                    }
                )
            }
        }
    }
}

/** Square glass-ish action tile used by the room header. */
@Composable
private fun RoomAction(icon: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f))
            .bounceClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(19.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConnectedUsersSection(
    users: List<UserInfo>,
    isHost: Boolean,
    currentUserId: String,
    onUserClick: (String, String) -> Unit
) {
    // Deliberately not a Card. The old screen was a stack of six outlined
    // containers, which is what made it read as a settings page rather than a
    // room. Sections are separated by space and type weight instead.
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.listen_together_in_the_room))
        Spacer(Modifier.height(14.dp))

        // Faces first. The people ARE the content of this screen, so they get
        // the size, and their names drop to a metadata line underneath.
        Row(
            horizontalArrangement = Arrangement.spacedBy((-10).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            users.take(8).forEach { user ->
                MemberAvatar(
                    username = user.username,
                    isHost = user.userId == currentUserId && isHost ||
                        user.isHost,
                    isSelf = user.userId == currentUserId,
                    onClick = { onUserClick(user.userId, user.username) }
                )
            }
            if (users.size > 8) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${users.size - 8}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = users.joinToString(", ") { it.username },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (isHost && users.size > 1) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.listen_together_tap_member_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/** Small, quiet, and consistent — the only thing separating one section from
 *  the next now that the containers are gone. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Colours are derived from the name, so a person keeps the same colour every
 *  session and across everyone's device — recognisable rather than decorative. */
private val MemberColors = listOf(
    Color(0xFFFF2D55), Color(0xFF5856D6), Color(0xFF32ADE6),
    Color(0xFF34C759), Color(0xFFFF9F0A), Color(0xFFAF52DE),
    Color(0xFF64D2FF), Color(0xFFFF6482),
)

private fun colorFor(username: String): Color =
    MemberColors[(username.hashCode().let { if (it < 0) -it else it }) % MemberColors.size]

@Composable
private fun MemberAvatar(
    username: String,
    isHost: Boolean,
    isSelf: Boolean,
    onClick: () -> Unit
) {
    val tint = remember(username) { colorFor(username) }
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(46.dp)
                // The ring is a background colour, not a border: it reads as the
                // avatar sitting on the surface rather than being outlined, which
                // is what lets them overlap cleanly.
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .padding(2.dp)
                .clip(CircleShape)
                .background(tint)
                .bounceClick(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        if (isHost) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.star),
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PendingJoinRequestsSection(
    requests: List<JoinRequestPayload>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    // Same flat treatment as the member row — these are the same people, one
    // step earlier. Giving them a container made a queue of two names look more
    // important than the room itself.
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.listen_together_join_requests))
        Spacer(Modifier.height(12.dp))

        requests.forEach { request ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                MemberAvatar(
                    username = request.username,
                    isHost = false,
                    isSelf = false,
                    onClick = {}
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = request.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                RowAction(
                    icon = R.drawable.close,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    description = stringResource(R.string.reject),
                    onClick = { onReject(request.userId) }
                )
                Spacer(Modifier.width(6.dp))
                RowAction(
                    icon = R.drawable.check,
                    tint = MaterialTheme.colorScheme.primary,
                    description = stringResource(R.string.approve),
                    onClick = { onApprove(request.userId) }
                )
            }
        }
    }
}

/** Circular icon button sized to sit level with a 46dp avatar row. */
@Composable
private fun RowAction(
    icon: Int,
    tint: Color,
    description: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun PendingSuggestionsSection(
    suggestions: List<SuggestionReceivedPayload>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.pending_suggestions))
        Spacer(Modifier.height(12.dp))

        suggestions.forEach { suggestion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                // Square artwork slot, not a circle: this row is about a track,
                // and the member rows above use circles for people. The shape
                // carries the distinction so the eye does not have to read.
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue_music),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = suggestion.trackInfo.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.listen_together_suggested_by,
                            suggestion.fromUsername
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                RowAction(
                    icon = R.drawable.close,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    description = stringResource(R.string.reject),
                    onClick = { onReject(suggestion.suggestionId) }
                )
                Spacer(Modifier.width(6.dp))
                RowAction(
                    icon = R.drawable.check,
                    tint = MaterialTheme.colorScheme.primary,
                    description = stringResource(R.string.approve),
                    onClick = { onApprove(suggestion.suggestionId) }
                )
            }
        }
    }
}

@Composable
private fun JoinCreateRoomSection(
    usernameInput: String,
    onUsernameChange: (String) -> Unit,
    roomCodeInput: String,
    onRoomCodeChange: (String) -> Unit,
    savedUsername: String,
    isJoiningRoom: Boolean,
    joinErrorMessage: String?,
    waitingForApprovalText: String,
    bringIntoViewRequester: BringIntoViewRequester,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit,
    onFieldFocused: () -> Unit = {}
) {
    val canAct = usernameInput.trim().isNotBlank() || savedUsername.isNotBlank()
    val hasCode = roomCodeInput.trim().length >= MIN_ROOM_CODE_LENGTH

    // No container. Starting a room is the whole job of this screen, so it gets
    // the page rather than a panel sitting on the page.
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.listen_together_your_name))
        Spacer(Modifier.height(10.dp))

        EntryField(
            value = usernameInput,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.enter_username),
            onFieldFocused = onFieldFocused,
            trailing = {
                if (usernameInput.isNotBlank()) {
                    RowAction(
                        icon = R.drawable.close,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        description = null,
                        onClick = { onUsernameChange("") }
                    )
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        // Primary action, full width, unmissable. One clear thing to do.
        PrimaryAction(
            label = stringResource(R.string.create_room),
            enabled = canAct,
            onClick = onCreateRoom
        )

        Spacer(Modifier.height(28.dp))
        DividerWithLabel(stringResource(R.string.listen_together_or_join))
        Spacer(Modifier.height(20.dp))

        EntryField(
            value = roomCodeInput,
            onValueChange = { onRoomCodeChange(it.uppercase().filter { c -> c.isLetterOrDigit() }.take(MAX_ROOM_CODE_LENGTH)) },
            placeholder = stringResource(R.string.enter_room_code),
            onFieldFocused = onFieldFocused,
            // The code is data, so it is set like data: wide tracking that makes a
            // mistyped character obvious before you submit it.
            letterSpacing = 4.sp,
            bold = true,
            modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
        )

        Spacer(Modifier.height(14.dp))

        SecondaryAction(
            label = if (isJoiningRoom) waitingForApprovalText else stringResource(R.string.join_room),
            enabled = canAct && hasCode && !isJoiningRoom,
            loading = isJoiningRoom,
            onClick = onJoinRoom
        )

        if (joinErrorMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = joinErrorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Flat filled field, no outline, matching the rest of the screen. */
@Composable
private fun EntryField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onFieldFocused: () -> Unit,
    modifier: Modifier = Modifier,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    bold: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                letterSpacing = letterSpacing,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            letterSpacing = letterSpacing,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        ),
        trailingIcon = trailing,
        singleLine = true,
        shape = ContinuousRoundedRectangle(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) onFieldFocused() }
    )
}

@Composable
private fun PrimaryAction(label: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) MaterialTheme.colorScheme.primary
             else MaterialTheme.colorScheme.surfaceContainerHigh
    val fg = if (enabled) MaterialTheme.colorScheme.onPrimary
             else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(percent = 50))
            .background(bg)
            .then(if (enabled) Modifier.bounceClick(onClick = onClick) else Modifier)
            .padding(vertical = 17.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

@Composable
private fun SecondaryAction(
    label: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val fg = if (enabled || loading) MaterialTheme.colorScheme.onSurface
             else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ContinuousRoundedRectangle(percent = 50))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(if (enabled) Modifier.bounceClick(onClick = onClick) else Modifier)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(15.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = fg
        )
    }
}

@Composable
private fun DividerWithLabel(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f))
        )
    }
}

@Composable
private fun SettingsLinkCard(onClick: () -> Unit) {
    // A row, not a card. It is navigation to somewhere else, so it should sit
    // quieter than anything describing the room itself.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .bounceClick(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.settings),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun UserActionDialog(
    username: String,
    onKick: () -> Unit,
    onPermanentKick: () -> Unit,
    onTransferOwnership: () -> Unit,
    onDismiss: () -> Unit
) {
    DefaultDialog(
        onDismiss = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.group),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.manage_user),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Kick button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onClick = onKick),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.kick_user),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(R.string.kick_user_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Permanently kick button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onClick = onPermanentKick),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.permanently_kick_user),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.permanently_kick_user_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Transfer ownership button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onClick = onTransferOwnership),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.crown),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.transfer_ownership),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.transfer_ownership_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Countdown to the room closing.
 *
 * Rooms expire because they are a shared free resource and an abandoned one
 * would otherwise hold its server state forever. Showing the clock is the
 * difference between "the session ended" and "the app broke".
 */
@Composable
private fun SessionCountdownCard(
    expiresAt: Long,
    expiring: Boolean,
    isHost: Boolean,
    extensionsLeft: Int,
    onExtend: () -> Unit
) {
    // Recomputed once a second rather than derived from a frame clock, so an
    // idle room is not redrawing at display rate just to tick a label.
    var remaining by remember(expiresAt) { mutableStateOf(expiresAt - System.currentTimeMillis()) }
    LaunchedEffect(expiresAt) {
        while (true) {
            remaining = expiresAt - System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    val totalSeconds = (remaining / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val pretty = if (hours > 0) {
        String.format(java.util.Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    // A metadata line, not a container. Only the last ten minutes earn colour —
    // an always-red panel trains people to ignore it, and then it cannot warn
    // them about anything.
    val tone = if (expiring) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.timer),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = tone
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(
                if (expiring) R.string.listen_together_session_ending_soon
                else R.string.listen_together_session_ends,
                pretty
            ),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (expiring) FontWeight.SemiBold else FontWeight.Normal,
            color = tone,
            modifier = Modifier.weight(1f)
        )
        if (isHost && extensionsLeft > 0) {
            Text(
                text = stringResource(R.string.listen_together_extend),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .bounceClick(onClick = onExtend)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Owner-only switch between owner-driven and free-for-all playback.
 *
 * This only reflects the room's setting — the server decides whether a given
 * action is accepted, so a client that lies about its mode gets refused rather
 * than obeyed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlModeCard(
    controlMode: String,
    onChange: (String) -> Unit
) {
    val everyone = controlMode == com.convxy.music.listentogether.ControlModes.EVERYONE
    // Container dropped to match the rest of the room view; the segmented
    // control is already a strong enough shape to hold this section on its own.
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.listen_together_control_mode))
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(
                if (everyone) R.string.listen_together_control_everyone_desc
                else R.string.listen_together_control_owner_desc
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        run {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !everyone,
                    onClick = { onChange(com.convxy.music.listentogether.ControlModes.OWNER) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text(stringResource(R.string.listen_together_control_owner)) }
                SegmentedButton(
                    selected = everyone,
                    onClick = { onChange(com.convxy.music.listentogether.ControlModes.EVERYONE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text(stringResource(R.string.listen_together_control_everyone)) }
            }
        }
    }
}
