/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings

import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.content.Intent
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.os.Build
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import android.provider.Settings
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.AnimatedVisibility
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Arrangement
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.height
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.size
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.widthIn
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.AlertDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.BasicAlertDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Button
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Surface
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.DropdownMenuItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExposedDropdownMenuBox
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.OutlinedTextField
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Slider
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.runtime.getValue
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.LocalContext
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.core.net.toUri
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.AppLanguageKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ContentCountryKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ContentLanguageKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SuggestionRegionKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SuggestionRegionSlugToName
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.screens.search.suggestions.SuggestionRegionSheet
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.CountryCodeToName
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableBetterLyricsKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableKugouKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableLrcLibKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableSimpMusicKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnableYouLyPlusKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.EnablePaxsenixKey
import com.nexamusic.app.constants.EnableMusixmatchKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.HideExplicitKey
import com.nexamusic.app.constants.DataSaverEnabledKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.HideVideoSongsKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.HideYoutubeShortsKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.AlbumCanvasEnabledKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LanguageCodeToName
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.LyricsProviderOrderKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ProxyEnabledKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ProxyPasswordKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ProxyTypeKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ProxyUrlKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ProxyUsernameKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.QuickPicks
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.QuickPicksKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.RandomizeHomeOrderKey
import com.nexamusic.app.constants.ShowHomeMoodFiltersKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.SYSTEM_DEFAULT
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowArtistDescriptionKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowArtistSubscriberCountKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowMonthlyListenersKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowArtistVideoKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowArtistBackgroundVideoKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.ShowWrappedCardKey
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.TopSize
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.EnumDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.rememberPreference
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.font.FontWeight
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.music.innertube.models.IpVersion
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.constants.IpVersionKey

import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.lyrics.LyricsProviderRegistry
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.DraggableLyricsProviderItem
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.DraggableLyricsProviderList
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateListOf
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.utils.PlaybackLogManager
import com.nexamusic.app.utils.YTPlayerUtils
import com.nexamusic.app.utils.YouTubeClientProbe
import com.music.innertube.YouTube
import com.nexamusic.app.LocalPlayerConnection
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontFamily
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.component.PlaybackLogsDialog
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import java.net.Proxy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Used only before Android 13
    val (appLanguage, onAppLanguageChange) = rememberPreference(key = AppLanguageKey, defaultValue = SYSTEM_DEFAULT)

    val (contentLanguage, onContentLanguageChange) = rememberPreference(key = ContentLanguageKey, defaultValue = "system")
    val (contentCountry, onContentCountryChange) = rememberPreference(key = ContentCountryKey, defaultValue = "system")
    val (suggestionRegion, onSuggestionRegionChange) = rememberPreference(key = SuggestionRegionKey, defaultValue = "system")
    val (hideExplicit, onHideExplicitChange) = rememberPreference(key = HideExplicitKey, defaultValue = false)
    val (dataSaverEnabled, onDataSaverEnabledChange) = rememberPreference(key = DataSaverEnabledKey, defaultValue = false)
    val (hideVideoSongs, onHideVideoSongsChange) = rememberPreference(key = HideVideoSongsKey, defaultValue = false)

    val (hideYoutubeShorts, onHideYoutubeShortsChange) = rememberPreference(key = HideYoutubeShortsKey, defaultValue = false)
    val (showArtistDescription, onShowArtistDescriptionChange) = rememberPreference(key = ShowArtistDescriptionKey, defaultValue = true)
    val (showArtistSubscriberCount, onShowArtistSubscriberCountChange) = rememberPreference(key = ShowArtistSubscriberCountKey, defaultValue = true)
    val (showMonthlyListeners, onShowMonthlyListenersChange) = rememberPreference(key = ShowMonthlyListenersKey, defaultValue = true)
    val (showArtistVideo, onShowArtistVideoChange) = rememberPreference(key = ShowArtistVideoKey, defaultValue = true)
    val (showArtistBackgroundVideo, onShowArtistBackgroundVideoChange) = rememberPreference(key = ShowArtistBackgroundVideoKey, defaultValue = true)
    val (proxyEnabled, onProxyEnabledChange) = rememberPreference(key = ProxyEnabledKey, defaultValue = false)
    val (proxyType, onProxyTypeChange) = rememberEnumPreference(key = ProxyTypeKey, defaultValue = Proxy.Type.HTTP)
    // Empty, not placeholder text — "host:port"/"username"/"password" used to be
    // the actual stored default, so enabling the Proxy toggle (or just opening
    // the auth section, which pre-enabled itself off these being "non-blank")
    // without editing anything saved those literal strings as the real proxy
    // URL / Basic-Auth credentials. Shown as OutlinedTextField placeholder hints
    // below instead.
    val (proxyUrl, onProxyUrlChange) = rememberPreference(key = ProxyUrlKey, defaultValue = "")
    val (proxyUsername, onProxyUsernameChange) = rememberPreference(key = ProxyUsernameKey, defaultValue = "")
    val (proxyPassword, onProxyPasswordChange) = rememberPreference(key = ProxyPasswordKey, defaultValue = "")
    val (enableKugou, onEnableKugouChange) = rememberPreference(key = EnableKugouKey, defaultValue = true)
    val (enableLrclib, onEnableLrclibChange) = rememberPreference(key = EnableLrcLibKey, defaultValue = true)
    val (enableBetterLyrics, onEnableBetterLyricsChange) = rememberPreference(key = EnableBetterLyricsKey, defaultValue = true)
    val (enableSimpMusic, onEnableSimpMusicChange) = rememberPreference(key = EnableSimpMusicKey, defaultValue = true)
    val (enableYouLyPlus, onEnableYouLyPlusChange) = rememberPreference(key = EnableYouLyPlusKey, defaultValue = true)
    val (enablePaxsenix, onEnablePaxsenixChange) = rememberPreference(key = EnablePaxsenixKey, defaultValue = true)
    val (enableMusixmatch, onEnableMusixmatchChange) = rememberPreference(key = EnableMusixmatchKey, defaultValue = true)
    val (lyricsProviderOrder, onLyricsProviderOrderChange) = rememberPreference(
        key = LyricsProviderOrderKey,
        defaultValue = "",
    )
    val (lengthTop, onLengthTopChange) = rememberPreference(key = TopSize, defaultValue = "50")
    val (quickPicks, onQuickPicksChange) = rememberEnumPreference(key = QuickPicksKey, defaultValue = QuickPicks.QUICK_PICKS)
    val (showWrappedCard, onShowWrappedCardChange) = rememberPreference(key = ShowWrappedCardKey, defaultValue = false)
    val (showMoodFilters, onShowMoodFiltersChange) = rememberPreference(
        key = ShowHomeMoodFiltersKey,
        defaultValue = true,
    )
    val (randomizeHomeOrder, onRandomizeHomeOrderChange) = rememberPreference(
        RandomizeHomeOrderKey,
        defaultValue = true
    )
    val (ipVersion, onIpVersionChange) = rememberEnumPreference(
        IpVersionKey,
        defaultValue = IpVersion.AUTO
    )
    val (albumCanvasEnabled, onAlbumCanvasEnabledChange) = rememberPreference(key = AlbumCanvasEnabledKey, defaultValue = false)

    var showPlaybackLogsDialog by rememberSaveable { mutableStateOf(false) }
    var showClientProbeDialog by rememberSaveable { mutableStateOf(false) }
    // Null while a run is in flight, so the dialog can show progress rather than an
    // empty report that looks like a result.
    var clientProbeOutput by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuggestionSheet by rememberSaveable { mutableStateOf(false) }
    val playbackLogs by PlaybackLogManager.logs.collectAsState()

    var showProxyConfigurationDialog by rememberSaveable {
        mutableStateOf(false)
    }

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

    var showContentLanguageDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showContentLanguageDialog) {
        EnumDialog(
            onDismiss = { showContentLanguageDialog = false },
            onSelect = {
                onContentLanguageChange(it)
                showContentLanguageDialog = false
            },
            title = stringResource(R.string.content_language),
            current = contentLanguage,
            values = (listOf(SYSTEM_DEFAULT) + LanguageCodeToName.keys.toList()),
            valueText = {
                LanguageCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
            }
        )
    }

    var showContentCountryDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showContentCountryDialog) {
        EnumDialog(
            onDismiss = { showContentCountryDialog = false },
            onSelect = {
                onContentCountryChange(it)
                showContentCountryDialog = false
            },
            title = stringResource(R.string.content_country),
            current = contentCountry,
            values = (listOf(SYSTEM_DEFAULT) + CountryCodeToName.keys.toList()),
            valueText = {
                CountryCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
            }
        )
    }

    var showAppLanguageDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showAppLanguageDialog) {
        EnumDialog(
            onDismiss = { showAppLanguageDialog = false },
            onSelect = {
                onAppLanguageChange(it)
                showAppLanguageDialog = false
            },
            title = stringResource(R.string.app_language),
            current = appLanguage,
            values = (listOf(SYSTEM_DEFAULT) + LanguageCodeToName.keys.toList()),
            valueText = {
                LanguageCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
            }
        )
    }

    var showProviderPriorityDialog by rememberSaveable { mutableStateOf(false) }

    if (showProviderPriorityDialog) {
        val defaultOrder = LyricsProviderRegistry.getDefaultProviderOrder()
        // User-toggleable provider names (excludes always-on YouTube providers)
        val userToggleable = setOf("YouLyPlus", "Paxsenix", "Musixmatch", "BetterLyrics", "SimpMusic", "LrcLib", "Kugou")
        val enabledProviders = setOfNotNull(
            "LrcLib".takeIf { enableLrclib },
            "Kugou".takeIf { enableKugou },
            "BetterLyrics".takeIf { enableBetterLyrics },
            "SimpMusic".takeIf { enableSimpMusic },
            "YouLyPlus".takeIf { enableYouLyPlus },
            "Paxsenix".takeIf { enablePaxsenix },
            "Musixmatch".takeIf { enableMusixmatch },
        )

        // Build a normalized order: saved order first (only known providers), then any missing ones
        val savedOrder = LyricsProviderRegistry.deserializeProviderOrder(lyricsProviderOrder)
        val normalizedOrder = savedOrder + defaultOrder.filter { it !in savedOrder }

        val lyricsIcon = painterResource(R.drawable.lyrics)
        val draggableItems = remember { mutableStateListOf<DraggableLyricsProviderItem>() }

        LaunchedEffect(normalizedOrder, enabledProviders) {
            val orderedEnabled = normalizedOrder.filter { it in enabledProviders }
            draggableItems.clear()
            draggableItems.addAll(
                orderedEnabled.map { name ->
                    DraggableLyricsProviderItem(
                        id = name,
                        name = LyricsProviderRegistry.getDisplayName(name),
                        icon = lyricsIcon,
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
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Title block / Header
                    Surface(
                        shape = blockShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
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
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.lyrics),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.lyrics_provider_priority),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        )
                                    }
                                }
                            }

                            Text(
                                text = stringResource(R.string.lyrics_provider_priority_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    // Content block with draggable list
                    Surface(
                        shape = blockShape,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                    ) {
                        DraggableLyricsProviderList(
                            items = draggableItems,
                            onItemsReordered = { reordered ->
                                val enabledOrder = reordered.map { it.id }
                                // Disabled user providers + YouTube providers go to the end
                                val rest = normalizedOrder.filter { it !in enabledProviders }
                                onLyricsProviderOrderChange(
                                    LyricsProviderRegistry.serializeProviderOrder(enabledOrder + rest)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(
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

    var showQuickPicksDialog by rememberSaveable {
        mutableStateOf(false)
    }

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

    var showTopLengthDialog by rememberSaveable {
        mutableStateOf(false)
    }

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

    var showIpVersionDialog by rememberSaveable {
        mutableStateOf(false)
    }

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

    if (showClientProbeDialog) {
        // Probes whatever is playing; falls back to a well-known always-available video
        // so the check still works when nothing has been played yet.
        val probeVideoId = LocalPlayerConnection.current?.mediaMetadata?.collectAsState()?.value?.id
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
                        style = MaterialTheme.typography.bodySmall.copy(
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

    if (showSuggestionSheet) {
        SuggestionRegionSheet(
            currentRegionSlug = suggestionRegion,
            onRegionSelected = { onSuggestionRegionChange(it) },
            onDismiss = { showSuggestionSheet = false }
        )
    }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Material3SettingsGroup(
            title = stringResource(R.string.general),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.language),
                    title = { Text(stringResource(R.string.content_language)) },
                    description = {
                        Text(
                            LanguageCodeToName.getOrElse(contentLanguage) { stringResource(R.string.system_default) }
                        )
                    },
                    onClick = { showContentLanguageDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.location_on),
                    title = { Text(stringResource(R.string.content_country)) },
                    description = {
                        Text(
                            CountryCodeToName.getOrElse(contentCountry) { stringResource(R.string.system_default) }
                        )
                    },
                    onClick = { showContentCountryDialog = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.globe_location_pin),
                    title = { Text("Suggestions Region") },
                    description = {
                        Text(
                            SuggestionRegionSlugToName.getOrElse(suggestionRegion) { "Global Charts" }
                        )
                    },
                    onClick = { showSuggestionSheet = true }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.explicit),
                    title = { Text(stringResource(R.string.hide_explicit)) },
                    trailingContent = {
                        Switch(
                            checked = hideExplicit,
                            onCheckedChange = onHideExplicitChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (hideExplicit) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onHideExplicitChange(!hideExplicit) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.hide_video_songs)) },
                    trailingContent = {
                        Switch(
                            checked = hideVideoSongs,
                            onCheckedChange = onHideVideoSongsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (hideVideoSongs) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onHideVideoSongsChange(!hideVideoSongs) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.offline),
                    title = { Text(stringResource(R.string.data_saver_mode)) },
                    description = { Text(stringResource(R.string.data_saver_mode_desc)) },
                    trailingContent = {
                        Switch(
                            checked = dataSaverEnabled,
                            onCheckedChange = onDataSaverEnabledChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (dataSaverEnabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onDataSaverEnabledChange(!dataSaverEnabled) }
                ),

                Material3SettingsItem(
                    icon = painterResource(R.drawable.hide_image),
                    title = { Text(stringResource(R.string.hide_youtube_shorts)) },
                    trailingContent = {
                        Switch(
                            checked = hideYoutubeShorts,
                            onCheckedChange = onHideYoutubeShortsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (hideYoutubeShorts) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onHideYoutubeShortsChange(!hideYoutubeShorts) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.artist_page_settings),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text(stringResource(R.string.show_artist_description)) },
                    trailingContent = {
                        Switch(
                            checked = showArtistDescription,
                            onCheckedChange = onShowArtistDescriptionChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showArtistDescription) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowArtistDescriptionChange(!showArtistDescription) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.person),
                    title = { Text(stringResource(R.string.show_artist_subscriber_count)) },
                    trailingContent = {
                        Switch(
                            checked = showArtistSubscriberCount,
                            onCheckedChange = onShowArtistSubscriberCountChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showArtistSubscriberCount) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowArtistSubscriberCountChange(!showArtistSubscriberCount) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.person),
                    title = { Text(stringResource(R.string.show_artist_monthly_listeners)) },
                    trailingContent = {
                        Switch(
                            checked = showMonthlyListeners,
                            onCheckedChange = onShowMonthlyListenersChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showMonthlyListeners) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowMonthlyListenersChange(!showMonthlyListeners) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.show_artist_video)) },
                    description = { Text(stringResource(R.string.show_artist_video_desc)) },
                    trailingContent = {
                        Switch(
                            checked = showArtistVideo,
                            onCheckedChange = onShowArtistVideoChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showArtistVideo) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowArtistVideoChange(!showArtistVideo) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.show_artist_background_video)) },
                    description = { Text(stringResource(R.string.show_artist_background_video_desc)) },
                    trailingContent = {
                        Switch(
                            checked = showArtistBackgroundVideo,
                            onCheckedChange = onShowArtistBackgroundVideoChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showArtistBackgroundVideo) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowArtistBackgroundVideoChange(!showArtistBackgroundVideo) }
                )
            )
        )

        Material3SettingsGroup(
            title = stringResource(R.string.album_text),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.slow_motion_video),
                    title = { Text(stringResource(R.string.show_album_canvas)) },
                    description = { Text(stringResource(R.string.show_album_canvas_desc)) },
                    trailingContent = {
                        Switch(
                            checked = albumCanvasEnabled,
                            onCheckedChange = onAlbumCanvasEnabledChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (albumCanvasEnabled) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onAlbumCanvasEnabledChange(!albumCanvasEnabled) }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.app_language),
            items = listOf(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.app_language)) },
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APP_LOCALE_SETTINGS,
                                    "package:${context.packageName}".toUri()
                                )
                            )
                        }
                    )
                } else {
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = { Text(stringResource(R.string.app_language)) },
                        description = {
                            Text(
                                LanguageCodeToName.getOrElse(appLanguage) { stringResource(R.string.system_default) }
                            )
                        },
                        onClick = { showAppLanguageDialog = true }
                    )
                }
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.proxy),
            items = buildList {
                add(Material3SettingsItem(
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
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.wifi_proxy),
                        title = { Text(stringResource(R.string.enable_proxy)) },
                        trailingContent = {
                            Switch(
                                checked = proxyEnabled,
                                onCheckedChange = onProxyEnabledChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (proxyEnabled) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onProxyEnabledChange(!proxyEnabled) }
                    )
                )
                if (proxyEnabled) {
                    add(
                        Material3SettingsItem(
                            icon = painterResource(R.drawable.settings),
                            title = { Text(stringResource(R.string.config_proxy)) },
                            onClick = { showProxyConfigurationDialog = true }
                        )
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.lyrics),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.enable_lrclib)) },
                    trailingContent = {
                        Switch(
                            checked = enableLrclib,
                            onCheckedChange = onEnableLrclibChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableLrclib) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableLrclibChange(!enableLrclib) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.enable_kugou)) },
                    trailingContent = {
                        Switch(
                            checked = enableKugou,
                            onCheckedChange = onEnableKugouChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableKugou) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableKugouChange(!enableKugou) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.enable_better_lyrics)) },
                    description = { Text(stringResource(R.string.enable_better_lyrics_desc)) },
                    trailingContent = {
                        Switch(
                            checked = enableBetterLyrics,
                            onCheckedChange = onEnableBetterLyricsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableBetterLyrics) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableBetterLyricsChange(!enableBetterLyrics) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.enable_simpmusic)) },
                    description = { Text(stringResource(R.string.enable_simpmusic_desc)) },
                    trailingContent = {
                        Switch(
                            checked = enableSimpMusic,
                            onCheckedChange = onEnableSimpMusicChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableSimpMusic) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableSimpMusicChange(!enableSimpMusic) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text("YouLyPlus") },
                    description = { Text("LyricsPlus multi-server provider (YouLy+ extension backend)") },
                    trailingContent = {
                        Switch(
                            checked = enableYouLyPlus,
                            onCheckedChange = onEnableYouLyPlusChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableYouLyPlus) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableYouLyPlusChange(!enableYouLyPlus) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text("PaxSenix") },
                    description = { Text("Apple Music quality synced lyrics with syllable-level timing") },
                    trailingContent = {
                        Switch(
                            checked = enablePaxsenix,
                            onCheckedChange = onEnablePaxsenixChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enablePaxsenix) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnablePaxsenixChange(!enablePaxsenix) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text("Musixmatch") },
                    description = { Text("Widely used synced lyrics catalog") },
                    trailingContent = {
                        Switch(
                            checked = enableMusixmatch,
                            onCheckedChange = onEnableMusixmatchChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableMusixmatch) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableMusixmatchChange(!enableMusixmatch) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text(stringResource(R.string.lyrics_provider_priority)) },
                    description = { Text(stringResource(R.string.lyrics_provider_priority_desc)) },
                    onClick = { showProviderPriorityDialog = true },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.language_korean_latin),
                    title = { Text(stringResource(R.string.lyrics_romanization)) },
                    onClick = { navController.navigate("settings/content/romanization") }
                )
            )
        )
//        Spacer(modifier = Modifier.height(27.dp))
//
//        Material3SettingsGroup(
//            title = "Wrapped",
//            items = listOf(
//                Material3SettingsItem(
//                    icon = painterResource(R.drawable.trending_up),
//                    title = { Text(stringResource(R.string.show_wrapped_card)) },
//                    trailingContent = {
//                        Switch(
//                            checked = showWrappedCard,
//                            onCheckedChange = onShowWrappedCardChange,
//                            thumbContent = {
//                                Icon(
//                                    painter = painterResource(
//                                        id = if (showWrappedCard) R.drawable.check else R.drawable.close
//                                    ),
//                                    contentDescription = null,
//                                    modifier = Modifier.size(SwitchDefaults.IconSize)
//                                )
//                            }
//                        )
//                    },
//                    onClick = { onShowWrappedCardChange(!showWrappedCard) }
//                )
//            )
//        )

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.misc),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.shuffle),
                    title = { Text(stringResource(R.string.randomize_home_order)) },
                    description = { Text(stringResource(R.string.randomize_home_order_desc)) },
                    trailingContent = {
                        Switch(
                            checked = randomizeHomeOrder,
                            onCheckedChange = onRandomizeHomeOrderChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (randomizeHomeOrder) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onRandomizeHomeOrderChange(!randomizeHomeOrder) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.drag_handle),
                    title = { Text(stringResource(R.string.home_feed_order)) },
                    description = { Text(stringResource(R.string.home_feed_order_desc)) },
                    onClick = { navController.navigate("settings/home_feed_order") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.tune),
                    title = { Text(stringResource(R.string.show_mood_filters)) },
                    description = { Text(stringResource(R.string.show_mood_filters_desc)) },
                    trailingContent = {
                        Switch(
                            checked = showMoodFilters,
                            onCheckedChange = onShowMoodFiltersChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (showMoodFilters) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onShowMoodFiltersChange(!showMoodFilters) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.trending_up),
                    title = { Text(stringResource(R.string.top_length)) },
                    description = { Text(lengthTop) },
                    onClick = { showTopLengthDialog = true }
                ),
                Material3SettingsItem(
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

        Spacer(modifier = Modifier.height(27.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.logs_heading),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.bug_report),
                    title = { Text(stringResource(R.string.playback_logs)) },
                    description = { Text(stringResource(R.string.playback_logs_desc)) },
                    onClick = { showPlaybackLogsDialog = true }
                ),
                Material3SettingsItem(
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

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.content)) },
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

/**
 * Probed when nothing is playing. A long-lived, globally available video, so a "which
 * clients still work" answer is about the clients rather than about that one track.
 */
private const val FALLBACK_PROBE_VIDEO_ID = "dQw4w9WgXcQ"
