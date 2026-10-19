/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import com.convx.music.lyrics.LyricsProviderSettings
import com.convx.music.utils.dataStore
import kotlinx.coroutines.launch
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.convx.music.ui.ios.compat.AlertDialog
import com.convx.music.ui.ios.compat.BasicAlertDialog
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.DropdownMenuItem
import com.convx.music.ui.ios.compat.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.EnableBetterLyricsKey
import com.convx.music.constants.EnableKugouKey
import com.convx.music.constants.EnableLrcLibKey
import com.convx.music.constants.EnableMusixmatchKey
import com.convx.music.constants.EnablePaxsenixKey
import com.convx.music.constants.EnableSimpMusicKey
import com.convx.music.constants.EnableUnisonKey
import com.convx.music.constants.EnableYouLyPlusKey
import com.convx.music.constants.IpVersionKey
import com.convx.music.constants.LyricsProviderOrderKey
import com.convx.music.constants.ProxyEnabledKey
import com.convx.music.constants.ProxyPasswordKey
import com.convx.music.constants.ProxyTypeKey
import com.convx.music.constants.ProxyUrlKey
import com.convx.music.constants.ProxyUsernameKey
import com.convx.music.constants.QuickPicks
import com.convx.music.constants.QuickPicksKey
import com.convx.music.constants.ShowArtistBackgroundVideoKey
import com.convx.music.constants.TopSize
import com.convx.music.lyrics.LyricsProviderRegistry
import com.convx.music.ui.component.DraggableLyricsProviderItem
import com.convx.music.ui.component.DraggableLyricsProviderList
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.component.PlaybackLogsDialog
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.PlaybackLogManager
import com.convx.music.utils.YTPlayerUtils
import com.convx.music.utils.YouTubeClientProbe
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.music.innertube.YouTube
import com.music.innertube.models.IpVersion
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape
import java.net.Proxy
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * The power-user half of [ContentSettings]: artist-page display density, proxy/network
 * config, individual lyrics-provider toggles and their priority order, and debug tooling
 * (playback logs, client probe) -- all things a casual listener never needs, split out so
 * the main Content page stays a short list of things people actually go looking for.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentAdvancedSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    networkOnly: Boolean = false,
) {
    val context = LocalContext.current

    val (showArtistBackgroundVideo, onShowArtistBackgroundVideoChange) = rememberPreference(key = ShowArtistBackgroundVideoKey, defaultValue = true)

    val (proxyEnabled, onProxyEnabledChange) = rememberPreference(key = ProxyEnabledKey, defaultValue = false)
    val (proxyType, onProxyTypeChange) = rememberEnumPreference(key = ProxyTypeKey, defaultValue = Proxy.Type.HTTP)
    val (proxyUrl, onProxyUrlChange) = rememberPreference(key = ProxyUrlKey, defaultValue = "")
    val (proxyUsername, onProxyUsernameChange) = rememberPreference(key = ProxyUsernameKey, defaultValue = "")
    val (proxyPassword, onProxyPasswordChange) = rememberPreference(key = ProxyPasswordKey, defaultValue = "")
    val (ipVersion, onIpVersionChange) = rememberEnumPreference(IpVersionKey, defaultValue = IpVersion.AUTO)

    val (lyricsProviderOrder, onLyricsProviderOrderChange) = rememberPreference(key = LyricsProviderOrderKey, defaultValue = "")

    val (lengthTop, onLengthTopChange) = rememberPreference(key = TopSize, defaultValue = "50")
    val (quickPicks, onQuickPicksChange) = rememberEnumPreference(key = QuickPicksKey, defaultValue = QuickPicks.QUICK_PICKS)

    var showPlaybackLogsDialog by rememberSaveable { mutableStateOf(false) }
    var showClientProbeDialog by rememberSaveable { mutableStateOf(false) }
    var clientProbeOutput by rememberSaveable { mutableStateOf<String?>(null) }
    val playbackLogs by PlaybackLogManager.logs.collectAsStateWithLifecycle()

    var showProxyConfigurationDialog by rememberSaveable { mutableStateOf(false) }

    if (showProxyConfigurationDialog) {
        var expandedDropdown by remember { mutableStateOf(false) }
        var tempProxyUrl by rememberSaveable { mutableStateOf(proxyUrl) }
        var tempProxyUsername by rememberSaveable { mutableStateOf(proxyUsername) }
        var tempProxyPassword by rememberSaveable { mutableStateOf(proxyPassword) }
        var authEnabled by rememberSaveable { mutableStateOf(proxyUsername.isNotBlank() || proxyPassword.isNotBlank()) }

        AlertDialog(
            onDismissRequest = { showProxyConfigurationDialog = false },
            title = {
                Text(stringResource(R.string.config_proxy))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = proxyType.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.proxy_type)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            listOf(Proxy.Type.HTTP, Proxy.Type.SOCKS).forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        onProxyTypeChange(type)
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tempProxyUrl,
                        onValueChange = { tempProxyUrl = it },
                        label = { Text(stringResource(R.string.proxy_url)) },
                        placeholder = { Text("host:port") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.enable_authentication))
                        Switch(
                            checked = authEnabled,
                            onCheckedChange = {
                                authEnabled = it
                                if (!it) {
                                    tempProxyUsername = ""
                                    tempProxyPassword = ""
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = authEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tempProxyUsername,
                                onValueChange = { tempProxyUsername = it },
                                label = { Text(stringResource(R.string.proxy_username)) },
                                placeholder = { Text("username") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = tempProxyPassword,
                                onValueChange = { tempProxyPassword = it },
                                label = { Text(stringResource(R.string.proxy_password)) },
                                placeholder = { Text("password") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onProxyUrlChange(tempProxyUrl)
                        onProxyUsernameChange(if (authEnabled) tempProxyUsername else "")
                        onProxyPasswordChange(if (authEnabled) tempProxyPassword else "")
                        showProxyConfigurationDialog = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showProxyConfigurationDialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    var showIpVersionDialog by rememberSaveable { mutableStateOf(false) }
    if (showIpVersionDialog) {
        EnumDialog(
            onDismiss = { showIpVersionDialog = false },
            onSelect = {
                onIpVersionChange(it)
                showIpVersionDialog = false
            },
            title = stringResource(R.string.network_ip_version),
            current = ipVersion,
            values = IpVersion.entries,
            valueText = {
                when (it) {
                    IpVersion.AUTO -> stringResource(R.string.ip_version_auto)
                    IpVersion.IPV4 -> stringResource(R.string.ip_version_ipv4)
                    IpVersion.IPV6 -> stringResource(R.string.ip_version_ipv6)
                }
            }
        )
    }

    var showProviderPriorityDialog by rememberSaveable { mutableStateOf(false) }

    if (showProviderPriorityDialog) {
        val defaultOrder = LyricsProviderRegistry.getDefaultProviderOrder()
        val providerPreferences by context.dataStore.data.collectAsStateWithLifecycle(initialValue = emptyPreferences())
        val providerScope = androidx.compose.runtime.rememberCoroutineScope()
        val enabledStates = defaultOrder.associateWith { providerPreferences[LyricsProviderSettings.enabledKey(it)] ?: true }

        val savedOrder = LyricsProviderRegistry.deserializeProviderOrder(lyricsProviderOrder)
        val normalizedOrder = savedOrder + defaultOrder.filter { it !in savedOrder }

        val lyricsIcon = painterResource(R.drawable.lyrics)
        val draggableItems = remember { mutableStateListOf<DraggableLyricsProviderItem>() }

        LaunchedEffect(normalizedOrder, enabledStates) {
            draggableItems.clear()
            draggableItems.addAll(
                normalizedOrder.map { name ->
                    DraggableLyricsProviderItem(
                        id = name,
                        name = LyricsProviderRegistry.getDisplayName(name),
                        icon = lyricsIcon,
                        enabled = enabledStates[name] ?: true,
                    )
                }
            )
        }

        val cardShape = AbsoluteSmoothCornerShape(30.dp, 60)
        val blockShape = AbsoluteSmoothCornerShape(22.dp, 60)
        val actionShape = AbsoluteSmoothCornerShape(18.dp, 60)

        BasicAlertDialog(onDismissRequest = { showProviderPriorityDialog = false }) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 360.dp),
                shape = cardShape,
                color = AppTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Surface(
                        shape = blockShape,
                        color = AppTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = AbsoluteSmoothCornerShape(12.dp, 60),
                                    color = AppTheme.colorScheme.secondaryContainer,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        SymbolIcon(symbol = sf("quote.bubble"), contentDescription = null, size = 16.dp, tint = AppTheme.colorScheme.onSecondaryContainer)
                                        Text(
                                            text = stringResource(R.string.lyrics_provider_priority),
                                            style = AppTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colorScheme.onSecondaryContainer,
                                        )
                                    }
                                }
                            }

                            Text(
                                text = stringResource(R.string.lyrics_provider_priority_desc),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Surface(
                        shape = blockShape,
                        color = AppTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                    ) {
                        DraggableLyricsProviderList(
                            items = draggableItems,
                            onItemsReordered = { reordered ->
                                onLyricsProviderOrderChange(
                                    LyricsProviderRegistry.serializeProviderOrder(reordered.map { it.id })
                                )
                            },
                            onToggleEnabled = { id ->
                                providerScope.launch {
                                    context.dataStore.edit { prefs ->
                                        val key = LyricsProviderSettings.enabledKey(id)
                                        prefs[key] = !(prefs[key] ?: true)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        com.convx.music.ui.ios.compat.Button(
                            onClick = { showProviderPriorityDialog = false },
                            shape = actionShape,
                        ) {
                            Text(text = stringResource(R.string.close))
                        }
                    }
                }
            }
        }
    }

    var showQuickPicksDialog by rememberSaveable { mutableStateOf(false) }
    if (showQuickPicksDialog) {
        EnumDialog(
            onDismiss = { showQuickPicksDialog = false },
            onSelect = {
                onQuickPicksChange(it)
                showQuickPicksDialog = false
            },
            title = stringResource(R.string.set_quick_picks),
            current = quickPicks,
            values = QuickPicks.values().toList(),
            valueText = {
                when (it) {
                    QuickPicks.QUICK_PICKS -> stringResource(R.string.quick_picks)
                    QuickPicks.LAST_LISTEN -> stringResource(R.string.last_song_listened)
                }
            }
        )
    }

    var showTopLengthDialog by rememberSaveable { mutableStateOf(false) }
    if (showTopLengthDialog) {
        var tempLength by rememberSaveable { mutableFloatStateOf(lengthTop.toFloat()) }

        AlertDialog(
            onDismissRequest = { showTopLengthDialog = false },
            title = { Text(stringResource(R.string.top_length)) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(tempLength.toInt().toString())
                    Slider(
                        value = tempLength,
                        onValueChange = { tempLength = it },
                        valueRange = 1f..100f,
                        steps = 98
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLengthTopChange(tempLength.toInt().toString())
                        showTopLengthDialog = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        )
    }

    if (showClientProbeDialog) {
        val probeVideoId = LocalPlayerConnection.current?.mediaMetadata?.collectAsStateWithLifecycle()?.value?.id
            ?: FALLBACK_PROBE_VIDEO_ID
        LaunchedEffect(showClientProbeDialog, probeVideoId) {
            val results = YouTubeClientProbe.run(
                videoId = probeVideoId,
                clients = YTPlayerUtils.allStreamClients,
                isLoggedIn = YouTube.cookie != null,
            )
            clientProbeOutput = YouTubeClientProbe.format(probeVideoId, results)
        }
        AlertDialog(
            onDismissRequest = { showClientProbeDialog = false },
            title = { Text(stringResource(R.string.client_probe)) },
            text = {
                val output = clientProbeOutput
                if (output == null) {
                    Text(stringResource(R.string.client_probe_running))
                } else {
                    Text(
                        text = output,
                        style = AppTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                        ),
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = clientProbeOutput != null,
                    onClick = {
                        clientProbeOutput?.let { text ->
                            val clipboard = context.getSystemService(ClipboardManager::class.java)
                            clipboard?.setPrimaryClip(ClipData.newPlainText("Convx client probe", text))
                        }
                        showClientProbeDialog = false
                    },
                ) { Text(stringResource(R.string.copy)) }
            },
            dismissButton = {
                TextButton(onClick = { showClientProbeDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    if (showPlaybackLogsDialog) {
        PlaybackLogsDialog(
            logs = playbackLogs,
            onClear = { PlaybackLogManager.clearLogs() },
            onDismiss = { showPlaybackLogsDialog = false }
        )
    }

    SettingsPage(
        navController = navController,
        title = if (networkOnly) "Network & proxy" else stringResource(R.string.advanced),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        if (!networkOnly) IosSettingsGroup(
            title = stringResource(R.string.artist_page_settings),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.show_artist_background_video)) },
                    description = { Text(stringResource(R.string.show_artist_background_video_desc)) },
                    trailingContent = {
                        Switch(
                            checked = showArtistBackgroundVideo,
                            onCheckedChange = onShowArtistBackgroundVideoChange
                        )
                    },
                    onClick = { onShowArtistBackgroundVideoChange(!showArtistBackgroundVideo) }
                )
            )
        )

        if (!networkOnly) Spacer(modifier = Modifier.height(27.dp))

        if (networkOnly) IosSettingsGroup(
            title = stringResource(R.string.proxy),
            items = buildList {
                add(IosSettingsItem(
                    icon = painterResource(R.drawable.network_node),
                    title = { Text(stringResource(R.string.network_ip_version)) },
                    description = {
                        Text(
                            when (ipVersion) {
                                IpVersion.AUTO -> stringResource(R.string.ip_version_auto)
                                IpVersion.IPV4 -> stringResource(R.string.ip_version_ipv4)
                                IpVersion.IPV6 -> stringResource(R.string.ip_version_ipv6)
                            }
                        )
                    },
                    onClick = { showIpVersionDialog = true }
                ))
                add(
                    IosSettingsItem(
                        icon = painterResource(R.drawable.wifi_proxy),
                        title = { Text(stringResource(R.string.enable_proxy)) },
                        trailingContent = {
                            Switch(checked = proxyEnabled, onCheckedChange = onProxyEnabledChange)
                        },
                        onClick = { onProxyEnabledChange(!proxyEnabled) }
                    )
                )
                if (proxyEnabled) {
                    add(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.settings),
                            title = { Text(stringResource(R.string.config_proxy)) },
                            onClick = { showProxyConfigurationDialog = true }
                        )
                    )
                }
            }
        )

        if (networkOnly) Spacer(modifier = Modifier.height(27.dp))

        if (!networkOnly) IosSettingsGroup(
            title = "Lyrics sources",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_provider_priority)) },
                    description = { Text(stringResource(R.string.lyrics_provider_priority_desc)) },
                    onClick = { showProviderPriorityDialog = true },
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.language_korean_latin),
                    title = { Text(stringResource(R.string.lyrics_romanization)) },
                    onClick = { navController.navigate("settings/content/romanization") }
                )
            )
        )

        if (!networkOnly) Spacer(modifier = Modifier.height(27.dp))

        if (!networkOnly) IosSettingsGroup(
            title = "Home & discovery",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.trending_up),
                    title = { Text(stringResource(R.string.top_length)) },
                    description = { Text(lengthTop) },
                    onClick = { showTopLengthDialog = true }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.home_outlined),
                    title = { Text(stringResource(R.string.set_quick_picks)) },
                    description = {
                        Text(
                            when (quickPicks) {
                                QuickPicks.QUICK_PICKS -> stringResource(R.string.quick_picks)
                                QuickPicks.LAST_LISTEN -> stringResource(R.string.last_song_listened)
                            }
                        )
                    },
                    onClick = { showQuickPicksDialog = true }
                )
            )
        )

        if (!networkOnly) Spacer(modifier = Modifier.height(27.dp))

        if (!networkOnly) IosSettingsGroup(
            title = stringResource(R.string.logs_heading),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.bug_report),
                    title = { Text(stringResource(R.string.playback_logs)) },
                    description = { Text(stringResource(R.string.playback_logs_desc)) },
                    onClick = { showPlaybackLogsDialog = true }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.bug_report),
                    title = { Text(stringResource(R.string.client_probe)) },
                    description = { Text(stringResource(R.string.client_probe_desc)) },
                    onClick = {
                        clientProbeOutput = null
                        showClientProbeDialog = true
                    }
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    
    }
}

/**
 * Probed when nothing is playing. A long-lived, globally available video, so a "which
 * clients still work" answer is about the clients rather than about that one track.
 */
private const val FALLBACK_PROBE_VIDEO_ID = "dQw4w9WgXcQ"
