/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings.integrations

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.LinearProgressIndicator
import com.convx.music.ui.ios.compat.LinearWavyProgressIndicator
import androidx.compose.material3.SnackbarHostState
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedButton
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.Player.STATE_READY
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.DiscordActivityNameKey
import com.convx.music.constants.DiscordActivityTypeKey
import com.convx.music.constants.DiscordAdvancedModeKey
import com.convx.music.constants.DiscordAvatarKey
import com.convx.music.constants.DiscordButton1TextKey
import com.convx.music.constants.DiscordButton1VisibleKey
import com.convx.music.constants.DiscordButton2TextKey
import com.convx.music.constants.DiscordButton2VisibleKey
import com.convx.music.constants.DiscordInfoDismissedKey
import com.convx.music.constants.DiscordNameKey
import com.convx.music.constants.DiscordStatusKey
import com.convx.music.constants.DiscordTokenKey
import com.convx.music.constants.DiscordUseDetailsKey
import com.convx.music.constants.DiscordUsernameKey
import com.convx.music.constants.EnableDiscordRPCKey
import com.convx.music.db.entities.Song
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.InfoLabel
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.TextFieldDialog
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.DiscordRPC
import com.convx.music.utils.SuperProperties
import com.convx.music.utils.dataStore
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberPreference
import com.my.kizzy.rpc.KizzyRPC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.convx.music.ui.screens.settings.SettingsPage
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()

    var position by rememberSaveable(playbackState) {
        mutableLongStateOf(playerConnection.player.currentPosition)
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Preferences
    var discordToken by rememberPreference(DiscordTokenKey, "")
    val loadedToken by remember(context) {
        context.dataStore.data
            .map { it[DiscordTokenKey] ?: "" }
            .distinctUntilChanged()
    }.collectAsStateWithLifecycle(initialValue = null)
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

    LaunchedEffect(loadedToken) {
        val token = loadedToken ?: return@LaunchedEffect
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
                        text = error.orEmpty(),
                        color = AppTheme.colorScheme.error,
                        style = AppTheme.typography.bodySmall,
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

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.discord_integration),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        // Warning Card
        AnimatedVisibility(visible = !infoDismissed) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colorScheme.surfaceContainerHighest,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    SymbolIcon(symbol = sf("exclamationmark.triangle.fill"), contentDescription = null, size = 24.dp, tint = AppTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.discord_information_warning),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurface,
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
                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
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
                            "idle" -> AppTheme.colorScheme.tertiary
                            "dnd" -> AppTheme.colorScheme.error
                            else -> AppTheme.colorScheme.primary
                        }
                        Surface(
                            color = statusColor,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .border(
                                    2.dp,
                                    AppTheme.colorScheme.surfaceContainerHigh,
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
                        style = AppTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(if (isLoggedIn) 1f else 0.5f),
                    )
                    if (discordUsername.isNotEmpty()) {
                        Text(
                            text = "@$discordUsername",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!isLoggedIn) {
                        Text(
                            text = stringResource(R.string.discord_connect_description),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
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
        IosSettingsGroup(
            title = stringResource(R.string.options),
            items = listOf(
                IosSettingsItem(
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
                IosSettingsItem(
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
                IosSettingsItem(
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
                IosSettingsGroup(
                    title = stringResource(R.string.discord_presence),
                    items = listOf(
                        IosSettingsItem(
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
                        IosSettingsItem(
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
                        IosSettingsItem(
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

                IosSettingsGroup(
                    title = stringResource(R.string.discord_buttons),
                    items = listOf(
                        IosSettingsItem(
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
                        IosSettingsItem(
                            title = { Text(stringResource(R.string.discord_button_2)) },
                            description = {
                                Text(button2Text.ifEmpty { "Visit Convx" })
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
                        containerColor = AppTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SymbolIcon(symbol = sf("info.circle"), contentDescription = null, size = 20.dp, tint = AppTheme.colorScheme.onSecondaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.discord_button_text_variables),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSecondaryContainer,
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
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colorScheme.primary,
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
        "playing" -> stringResource(R.string.discord_playing_convx)
        "watching" -> stringResource(R.string.discord_watching_convx)
        "competing" -> stringResource(R.string.discord_competing_convx)
        else -> stringResource(R.string.listening_to_convx)
    }

    Surface(
        color = AppTheme.colorScheme.surfaceContainer,
        shape = AppTheme.shapes.medium,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (activityName.isNotEmpty()) activityName else activityLabel,
                style = AppTheme.typography.labelLarge,
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
                                        AppTheme.colorScheme.onSurface,
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
                                    AppTheme.colorScheme.surfaceContainer,
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
                        color = AppTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = song?.artists?.joinToString { it.name } ?: "Artist",
                        color = AppTheme.colorScheme.secondary,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    song?.album?.title?.let {
                        Text(
                            text = it,
                            color = AppTheme.colorScheme.secondary,
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
                            com.convx.music.utils.ShareUtils.songUrl(song?.id ?: "").toUri()
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
                        button2Text.ifEmpty { "Visit Convx" }, song
                    )
                } else {
                    button2Text.ifEmpty { "Visit Convx" }
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/cosmictaserdev-creator/Convx".toUri()
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
