/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings.integrations

import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.content.Intent
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.AnimatedVisibility
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.animateContentSize
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.border
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Arrangement
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.height
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.only
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.size
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.width
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.CircleShape
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Card
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.CardDefaults
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.LinearProgressIndicator
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.LinearWavyProgressIndicator
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHostState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.OutlinedButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Surface
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.LaunchedEffect
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableLongStateOf
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.draw.alpha
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.draw.clip
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.platform.LocalContext
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.font.FontWeight
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.style.TextAlign
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.style.TextOverflow
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.input.TextFieldValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.sp
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.core.net.toUri
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.media3.common.Player.STATE_READY
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import coil3.compose.AsyncImage
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.LocalPlayerConnection
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordActivityNameKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordActivityTypeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordAdvancedModeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordAvatarKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordButton1TextKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordButton1VisibleKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordButton2TextKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordButton2VisibleKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordInfoDismissedKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordNameKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordStatusKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordTokenKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordUseDetailsKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.DiscordUsernameKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableDiscordRPCKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.db.entities.Song
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.EnumDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.InfoLabel
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.TextFieldDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.DiscordRPC
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.SuperProperties
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.makeTimeString
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.rememberPreference
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.my.kizzy.rpc.KizzyRPC
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.Dispatchers
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.delay
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.isActive
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.launch

private enum class DiscordStatus { ONLINE, IDLE, DND }
private enum class DiscordActivityType { LISTENING, PLAYING, WATCHING, COMPETING }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DiscordSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val song by playerConnection.currentSong.collectAsState(null)
    val playbackState by playerConnection.playbackState.collectAsState()

    var position by rememberSaveable(playbackState) {
        mutableLongStateOf(playerConnection.player.currentPosition)
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Preferences
    var discordToken by rememberPreference(DiscordTokenKey, "")
    var discordUsername by rememberPreference(DiscordUsernameKey, "")
    var discordName by rememberPreference(DiscordNameKey, "")
    var discordAvatar by rememberPreference(DiscordAvatarKey, "")
    var infoDismissed by rememberPreference(DiscordInfoDismissedKey, false)

    val (discordRPC, onDiscordRPCChange) = rememberPreference(EnableDiscordRPCKey, true)
    val (useDetails, onUseDetailsChange) = rememberPreference(DiscordUseDetailsKey, false)
    val (advancedMode, onAdvancedModeChange) = rememberPreference(DiscordAdvancedModeKey, false)

    var discordStatus by rememberPreference(DiscordStatusKey, "online")
    var button1Text by rememberPreference(DiscordButton1TextKey, "")
    var button1Visible by rememberPreference(DiscordButton1VisibleKey, true)
    var button2Text by rememberPreference(DiscordButton2TextKey, "")
    var button2Visible by rememberPreference(DiscordButton2VisibleKey, true)
    var activityType by rememberPreference(DiscordActivityTypeKey, "listening")
    var activityName by rememberPreference(DiscordActivityNameKey, "")

    val isLoggedIn = remember(discordToken) { discordToken.isNotEmpty() }

    var showTokenDialog by rememberSaveable { mutableStateOf(false) }
    var showStatusDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }
    var showButton1TextDialog by rememberSaveable { mutableStateOf(false) }
    var showButton2TextDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityNameDialog by rememberSaveable { mutableStateOf(false) }

    // Map string prefs to enums for dialogs
    val currentStatus = when (discordStatus) {
        "idle" -> DiscordStatus.IDLE
        "dnd" -> DiscordStatus.DND
        else -> DiscordStatus.ONLINE
    }
    val currentActivityType = when (activityType) {
        "playing" -> DiscordActivityType.PLAYING
        "watching" -> DiscordActivityType.WATCHING
        "competing" -> DiscordActivityType.COMPETING
        else -> DiscordActivityType.LISTENING
    }

    // Fetch user info when token changes
    LaunchedEffect(discordToken) {
        val token = discordToken
        if (token.isEmpty()) {
            discordUsername = ""
            discordName = ""
            discordAvatar = ""
            return@LaunchedEffect
        }
        launch(Dispatchers.IO) {
            KizzyRPC.getUserInfo(
                token,
                SuperProperties.userAgent,
                SuperProperties.superPropertiesBase64
            ).onSuccess {
                discordUsername = it.username
                discordName = it.name
                discordAvatar = it.avatar ?: ""
            }.onFailure {
                discordUsername = ""
                discordName = ""
                discordAvatar = ""
            }
        }
    }

    // Update playback position
    LaunchedEffect(playbackState) {
        if (playbackState == STATE_READY) {
            while (isActive) {
                delay(100)
                position = playerConnection.player.currentPosition
            }
        }
    }

    // Dialogs
    if (showTokenDialog) {
        var isVerifying by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }

        TextFieldDialog(
            onDismiss = { showTokenDialog = false },
            icon = { Icon(painterResource(R.drawable.token), null) },
            autoDismiss = false,
            onDone = { token ->
                isVerifying = true
                error = null
                coroutineScope.launch(Dispatchers.IO) {
                    KizzyRPC.getUserInfo(
                        token,
                        SuperProperties.userAgent,
                        SuperProperties.superPropertiesBase64
                    ).onSuccess {
                        discordToken = token
                        showTokenDialog = false
                        snackbarHostState.showSnackbar(context.getString(R.string.login_successful))
                    }.onFailure {
                        error = "Invalid token"
                        isVerifying = false
                    }
                }
            },
            singleLine = true,
            isInputValid = { it.isNotEmpty() },
            extraContent = {
                if (isVerifying) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                InfoLabel(text = stringResource(R.string.token_adv_login_description))
            }
        )
    }

    if (showStatusDialog) {
        EnumDialog(
            onDismiss = { showStatusDialog = false },
            onSelect = { selected ->
                discordStatus = when (selected) {
                    DiscordStatus.IDLE -> "idle"
                    DiscordStatus.DND -> "dnd"
                    DiscordStatus.ONLINE -> "online"
                }
                showStatusDialog = false
            },
            title = stringResource(R.string.discord_status),
            current = currentStatus,
            values = DiscordStatus.entries.toList(),
            valueText = {
                when (it) {
                    DiscordStatus.ONLINE -> stringResource(R.string.discord_status_online)
                    DiscordStatus.IDLE -> stringResource(R.string.discord_status_idle)
                    DiscordStatus.DND -> stringResource(R.string.discord_status_dnd)
                }
            }
        )
    }

    if (showActivityTypeDialog) {
        EnumDialog(
            onDismiss = { showActivityTypeDialog = false },
            onSelect = { selected ->
                activityType = when (selected) {
                    DiscordActivityType.PLAYING -> "playing"
                    DiscordActivityType.WATCHING -> "watching"
                    DiscordActivityType.COMPETING -> "competing"
                    DiscordActivityType.LISTENING -> "listening"
                }
                showActivityTypeDialog = false
            },
            title = stringResource(R.string.discord_activity_type),
            current = currentActivityType,
            values = DiscordActivityType.entries.toList(),
            valueText = {
                when (it) {
                    DiscordActivityType.LISTENING -> stringResource(R.string.discord_activity_listening)
                    DiscordActivityType.PLAYING -> stringResource(R.string.discord_activity_playing)
                    DiscordActivityType.WATCHING -> stringResource(R.string.discord_activity_watching)
                    DiscordActivityType.COMPETING -> stringResource(R.string.discord_activity_competing)
                }
            }
        )
    }

    if (showButton1TextDialog) {
        TextFieldDialog(
            onDismiss = { showButton1TextDialog = false },
            onDone = {
                button1Text = it
                showButton1TextDialog = false
            },
            singleLine = true,
            initialTextFieldValue = TextFieldValue(button1Text),
            extraContent = {
                InfoLabel(text = stringResource(R.string.discord_button_text_variables))
            }
        )
    }

    if (showButton2TextDialog) {
        TextFieldDialog(
            onDismiss = { showButton2TextDialog = false },
            onDone = {
                button2Text = it
                showButton2TextDialog = false
            },
            singleLine = true,
            initialTextFieldValue = TextFieldValue(button2Text),
            extraContent = {
                InfoLabel(text = stringResource(R.string.discord_button_text_variables))
            }
        )
    }

    if (showActivityNameDialog) {
        TextFieldDialog(
            onDismiss = { showActivityNameDialog = false },
            onDone = {
                activityName = it
                showActivityNameDialog = false
            },
            singleLine = true,
            initialTextFieldValue = TextFieldValue(activityName),
            extraContent = {
                InfoLabel(text = stringResource(R.string.discord_activity_name_description))
            }
        )
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        // Warning Card
        AnimatedVisibility(visible = !infoDismissed) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.warning),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.discord_information_warning),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { infoDismissed = true },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                }
            }
        }

        // Profile Card (fully rounded)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp,
                             bottom = if (isLoggedIn) 20.dp else 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar with status dot
                Box(modifier = Modifier.size(56.dp)) {
                    if (isLoggedIn && discordAvatar.isNotEmpty()) {
                        AsyncImage(
                            model = discordAvatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.discord),
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.Center)
                                .alpha(0.4f),
                        )
                    }
                    if (isLoggedIn) {
                        val statusColor = when (discordStatus) {
                            "idle" -> MaterialTheme.colorScheme.tertiary
                            "dnd" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }
                        Surface(
                            color = statusColor,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    CircleShape
                                ),
                            content = {},
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLoggedIn) discordName
                               else stringResource(R.string.not_logged_in),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(if (isLoggedIn) 1f else 0.5f),
                    )
                    if (discordUsername.isNotEmpty()) {
                        Text(
                            text = "@$discordUsername",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!isLoggedIn) {
                        Text(
                            text = stringResource(R.string.discord_connect_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Only show logout inline when logged in
                if (isLoggedIn) {
                    OutlinedButton(onClick = {
                        discordName = ""
                        discordToken = ""
                        discordUsername = ""
                        discordAvatar = ""
                    }) {
                        Text(stringResource(R.string.action_logout))
                    }
                }
            }

            // Login buttons below when not logged in
            if (!isLoggedIn) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("settings/discord/login") },
                    ) {
                        Text(stringResource(R.string.action_login))
                    }
                    OutlinedButton(
                        onClick = { showTokenDialog = true },
                    ) {
                        Icon(
                            painterResource(R.drawable.token),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.advanced_login))
                    }
                }
            }
        }

        // Options section (card-based)
        Material3SettingsGroup(
            title = stringResource(R.string.options),
            items = listOf(
                Material3SettingsItem(
                    title = { Text(stringResource(R.string.enable_discord_rpc)) },
                    trailingContent = {
                        Switch(
                            checked = discordRPC,
                            onCheckedChange = onDiscordRPCChange,
                            enabled = isLoggedIn,
                        )
                    },
                    enabled = isLoggedIn,
                    onClick = { if (isLoggedIn) onDiscordRPCChange(!discordRPC) },
                ),
                Material3SettingsItem(
                    title = { Text(stringResource(R.string.discord_use_details)) },
                    description = {
                        Text(stringResource(R.string.discord_use_details_description))
                    },
                    trailingContent = {
                        Switch(
                            checked = useDetails,
                            onCheckedChange = onUseDetailsChange,
                            enabled = isLoggedIn && discordRPC,
                        )
                    },
                    enabled = isLoggedIn && discordRPC,
                    onClick = {
                        if (isLoggedIn && discordRPC) onUseDetailsChange(!useDetails)
                    },
                ),
                Material3SettingsItem(
                    title = { Text(stringResource(R.string.discord_advanced_mode)) },
                    description = {
                        Text(stringResource(R.string.discord_advanced_mode_description))
                    },
                    trailingContent = {
                        Switch(
                            checked = advancedMode,
                            onCheckedChange = onAdvancedModeChange,
                            enabled = isLoggedIn && discordRPC,
                        )
                    },
                    enabled = isLoggedIn && discordRPC,
                    onClick = {
                        if (isLoggedIn && discordRPC) onAdvancedModeChange(!advancedMode)
                    },
                ),
            ),
        )

        Spacer(Modifier.height(8.dp))

        // Advanced customization section
        AnimatedVisibility(visible = isLoggedIn && discordRPC && advancedMode) {
            Column(modifier = Modifier.animateContentSize()) {
                // Presence settings
                Material3SettingsGroup(
                    title = stringResource(R.string.discord_presence),
                    items = listOf(
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.discord_status)) },
                            description = {
                                Text(
                                    when (currentStatus) {
                                        DiscordStatus.ONLINE ->
                                            stringResource(R.string.discord_status_online)
                                        DiscordStatus.IDLE ->
                                            stringResource(R.string.discord_status_idle)
                                        DiscordStatus.DND ->
                                            stringResource(R.string.discord_status_dnd)
                                    }
                                )
                            },
                            onClick = { showStatusDialog = true },
                        ),
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.discord_activity_type)) },
                            description = {
                                Text(
                                    when (currentActivityType) {
                                        DiscordActivityType.LISTENING ->
                                            stringResource(R.string.discord_activity_listening)
                                        DiscordActivityType.PLAYING ->
                                            stringResource(R.string.discord_activity_playing)
                                        DiscordActivityType.WATCHING ->
                                            stringResource(R.string.discord_activity_watching)
                                        DiscordActivityType.COMPETING ->
                                            stringResource(R.string.discord_activity_competing)
                                    }
                                )
                            },
                            onClick = { showActivityTypeDialog = true },
                        ),
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.discord_activity_name)) },
                            description = {
                                Text(
                                    activityName.ifEmpty {
                                        stringResource(R.string.discord_activity_name_description)
                                    }
                                )
                            },
                            onClick = { showActivityNameDialog = true },
                        ),
                    ),
                )

                Spacer(Modifier.height(8.dp))

                // Button customization
                Material3SettingsGroup(
                    title = stringResource(R.string.discord_buttons),
                    items = listOf(
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.discord_button_1)) },
                            description = {
                                Text(button1Text.ifEmpty { "Listen on YouTube Music" })
                            },
                            trailingContent = {
                                Switch(
                                    checked = button1Visible,
                                    onCheckedChange = { button1Visible = it },
                                )
                            },
                            onClick = { showButton1TextDialog = true },
                        ),
                        Material3SettingsItem(
                            title = { Text(stringResource(R.string.discord_button_2)) },
                            description = {
                                Text(button2Text.ifEmpty { "Visit NexaMusic" })
                            },
                            trailingContent = {
                                Switch(
                                    checked = button2Visible,
                                    onCheckedChange = { button2Visible = it },
                                )
                            },
                            onClick = { showButton2TextDialog = true },
                        ),
                    ),
                )

                // Variable hint
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.info),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.discord_button_text_variables),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        // Preview section
        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.discord_rpc_preview),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
        )

        RichPresence(
            song = song,
            currentPlaybackTimeMillis = position,
            activityType = activityType,
            activityName = activityName,
            button1Text = button1Text,
            button1Visible = button1Visible,
            button2Text = button2Text,
            button2Visible = button2Visible,
        )

        // Bottom padding for mini player
        Spacer(Modifier.height(24.dp))
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.discord_integration)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RichPresence(
    song: Song?,
    currentPlaybackTimeMillis: Long = 0L,
    activityType: String = "listening",
    activityName: String = "",
    button1Text: String = "",
    button1Visible: Boolean = true,
    button2Text: String = "",
    button2Visible: Boolean = true,
) {
    val context = LocalContext.current

    val activityLabel = when (activityType) {
        "playing" -> stringResource(R.string.discord_playing_vivimusic)
        "watching" -> stringResource(R.string.discord_watching_vivimusic)
        "competing" -> stringResource(R.string.discord_competing_vivimusic)
        else -> stringResource(R.string.listening_to_vivimusic)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (activityName.isNotEmpty()) activityName else activityLabel,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                Box(Modifier.size(108.dp)) {
                    AsyncImage(
                        model = song?.song?.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .align(Alignment.TopStart)
                            .run {
                                if (song == null) {
                                    border(
                                        2.dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        RoundedCornerShape(3.dp)
                                    )
                                } else {
                                    this
                                }
                            },
                    )

                    song?.artists?.firstOrNull()?.thumbnailUrl?.let {
                        Box(
                            modifier = Modifier
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    CircleShape
                                )
                                .padding(2.dp)
                                .align(Alignment.BottomEnd),
                        ) {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp),
                ) {
                    Text(
                        text = song?.song?.title ?: "Song Title",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = song?.artists?.joinToString { it.name } ?: "Artist",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    song?.album?.title?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    if (song != null) {
                        SongProgressBar(
                            currentTimeMillis = currentPlaybackTimeMillis,
                            durationMillis = song.song.duration.times(1000L),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (button1Visible) {
                val resolvedButton1 = if (song != null) {
                    DiscordRPC.resolveVariables(
                        button1Text.ifEmpty { "Listen on YouTube Music" }, song
                    )
                } else {
                    button1Text.ifEmpty { "Listen on YouTube Music" }
                }
                OutlinedButton(
                    enabled = song != null,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://music.youtube.com/watch?v=${song?.id}".toUri()
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(resolvedButton1)
                }
            }

            if (button2Visible) {
                val resolvedButton2 = if (song != null) {
                    DiscordRPC.resolveVariables(
                        button2Text.ifEmpty { "Visit NexaMusic" }, song
                    )
                } else {
                    button2Text.ifEmpty { "Visit NexaMusic" }
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/nkosanamahungela-dev/NexaMusic-".toUri()
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(resolvedButton2)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SongProgressBar(currentTimeMillis: Long, durationMillis: Long) {
    val progress = if (durationMillis > 0) currentTimeMillis.toFloat() / durationMillis else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))

        LinearWavyProgressIndicator(
            progress = { progress },
            amplitude = { 1f },
            wavelength = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = makeTimeString(currentTimeMillis),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
            )
            Text(
                text = makeTimeString(durationMillis),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontSize = 12.sp
            )
        }
    }
}
