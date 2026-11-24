/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.ios.compat.AppTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
// Explicit import wins over the star import above, so the two toggles below
// pick up the glass switch without touching their call sites.
import com.convx.music.ui.ios.compat.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowClockwise
import com.composables.sfsymbols.monochrome.SFKey
import com.composables.sfsymbols.monochrome.SFPencil
import com.composables.sfsymbols.monochrome.SFPlusCircle
import com.composables.sfsymbols.monochrome.SFRectanglePortraitAndArrowRight
import com.composables.sfsymbols.monochrome.SFWrenchAdjustable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.YouTube
import com.music.innertube.utils.parseCookieString
import com.convx.music.constants.*
import com.convx.music.ui.component.*
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.AccountSettingsViewModel
import com.convx.music.viewmodels.HomeViewModel
import com.convx.music.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountSettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val (accountNamePref, _) = rememberPreference(AccountNameKey, "")
    val (accountEmail, _) = rememberPreference(AccountEmailKey, "")
    val (accountChannelHandle, _) = rememberPreference(AccountChannelHandleKey, "")
    val (innerTubeCookie, onInnerTubeCookieChange) = rememberPreference(InnerTubeCookieKey, "")
    val (visitorData, _) = rememberPreference(VisitorDataKey, "")
    val (dataSyncId, _) = rememberPreference(DataSyncIdKey, "")
    val (savedAccountsJson, _) = rememberPreference(SavedAccountsKey, "")
    val savedAccounts = remember(savedAccountsJson) { savedAccountsJson.toSavedAccounts() }

    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    val (useLoginForBrowse, onUseLoginForBrowseChange) = rememberPreference(UseLoginForBrowse, true)
    val (ytmSync, onYtmSyncChange) = rememberPreference(YtmSyncKey, true)

    val (listenBrainzEnabled, onListenBrainzEnabledChange) = rememberPreference(ListenBrainzEnabledKey, false)
    val (listenBrainzToken, onListenBrainzTokenChange) = rememberPreference(ListenBrainzTokenKey, "")
    var showListenBrainzTokenEditor by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = koinViewModel()
    val accountSettingsViewModel: AccountSettingsViewModel = koinViewModel()
    val accountName by homeViewModel.accountName.collectAsStateWithLifecycle()
    val accountImageUrl by homeViewModel.accountImageUrl.collectAsStateWithLifecycle()

    // Keeps the tap-to-switch list alive for sessions that predate the channel
    // picker, or whose lookup failed at login — without this it is only ever
    // written by a fresh login.
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) accountSettingsViewModel.refreshSavedAccounts(context)
    }

    var showToken by remember { mutableStateOf(false) }
    var showTokenEditor by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.account),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
            // Account Profile Section
            IosSettingsGroup(
                title = "YouTube Music",
                items = listOf(
                    IosSettingsItem(
                        icon = if (isLoggedIn && !accountImageUrl.isNullOrBlank()) null else rememberVectorPainter(SfSymbols.Monochrome.SFRectanglePortraitAndArrowRight),
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isLoggedIn && !accountImageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = accountImageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Text(
                                    text = if (isLoggedIn) accountName else stringResource(R.string.login),
                                    color = if (isLoggedIn) com.convx.music.ui.ios.foundation.IosTheme.colors.label else com.convx.music.ui.ios.foundation.IosTheme.tint,
                                    style = com.convx.music.ui.ios.foundation.IosTheme.typography.body.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        },
                        trailingContent = if (isLoggedIn) ({
                            // iOS-style destructive pill: readable on light and dark cards alike.
                            Text(
                                stringResource(R.string.action_logout),
                                color = com.convx.music.ui.ios.foundation.IosTheme.colors.systemRed,
                                style = com.convx.music.ui.ios.foundation.IosTheme.typography.subheadline.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(com.convx.music.ui.ios.foundation.IosTheme.colors.systemRed.copy(alpha = 0.14f))
                                    .clickable { showLogoutDialog = true }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                            )
                        }) else null,
                        onClick = {
                            if (isLoggedIn) navController.navigate("account")
                            else navController.navigate("login")
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoggedIn && savedAccounts.size > 1) {
                IosSettingsGroup(
                    title = stringResource(R.string.saved_accounts_title),
                    items = savedAccounts.map { account ->
                        IosSettingsItem(
                            leadingContent = {
                                AsyncImage(
                                    model = account.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.person),
                                    error = painterResource(R.drawable.person),
                                    modifier = Modifier.size(40.dp).clip(CircleShape),
                                )
                            },
                            title = { Text(account.name) },
                            description = {
                                Text(account.channelHandle ?: stringResource(R.string.saved_accounts_desc))
                            },
                            isHighlighted = account.dataSyncId == dataSyncId,
                            onClick = { accountSettingsViewModel.switchToSavedAccount(context, account) }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoggedIn) {
                IosSettingsGroup(
                    items = listOf(
                        IosSettingsItem(
                            icon = rememberVectorPainter(SfSymbols.Monochrome.SFArrowClockwise),
                            title = { Text(stringResource(R.string.switch_channel_action)) },
                            description = { Text(stringResource(R.string.switch_channel_action_desc)) },
                            onClick = { navController.navigate("switch_channel") }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Token / Advanced Login Section
            IosSettingsGroup(
                title = stringResource(R.string.advanced_login),
                items = listOf(
                    IosSettingsItem(
                        icon = rememberVectorPainter(SfSymbols.Monochrome.SFKey),
                        title = {
                            Text(
                                when {
                                    !isLoggedIn -> stringResource(R.string.advanced_login)
                                    showToken -> stringResource(R.string.token_shown)
                                    else -> stringResource(R.string.token_hidden)
                                }
                            )
                        },
                        onClick = {
                            if (!isLoggedIn) showTokenEditor = true
                            else if (!showToken) showToken = true
                            else showTokenEditor = true
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sync & Content Section
            if (isLoggedIn) {
                IosSettingsGroup(
                    title = stringResource(R.string.settings_section_player_content),
                    items = listOf(
                        IosSettingsItem(
                            icon = rememberVectorPainter(SfSymbols.Monochrome.SFPlusCircle),
                            title = { Text(stringResource(R.string.more_content)) },
                            trailingContent = {
                                Switch(
                                    checked = useLoginForBrowse,
                                    onCheckedChange = {
                                        YouTube.useLoginForBrowse = it
                                        onUseLoginForBrowseChange(it)
                                    }
                                )
                            },
                            onClick = {
                                val newValue = !useLoginForBrowse
                                YouTube.useLoginForBrowse = newValue
                                onUseLoginForBrowseChange(newValue)
                            }
                        ),
                        IosSettingsItem(
                            icon = rememberVectorPainter(SfSymbols.Monochrome.SFArrowClockwise),
                            title = { Text(stringResource(R.string.yt_sync)) },
                            trailingContent = {
                                Switch(
                                    checked = ytmSync,
                                    onCheckedChange = onYtmSyncChange
                                )
                            },
                            onClick = { onYtmSyncChange(!ytmSync) }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ListenBrainz Section
            IosSettingsGroup(
                title = stringResource(R.string.listenbrainz_scrobbling),
                items = listOf(
                    IosSettingsItem(
                        icon = rememberVectorPainter(SfSymbols.Monochrome.SFPencil),
                        title = { Text(stringResource(R.string.listenbrainz_scrobbling)) },
                        description = { Text(stringResource(R.string.listenbrainz_scrobbling_desc)) },
                        trailingContent = {
                            Switch(
                                checked = listenBrainzEnabled,
                                onCheckedChange = onListenBrainzEnabledChange
                            )
                        },
                        onClick = { onListenBrainzEnabledChange(!listenBrainzEnabled) }
                    ),
                    IosSettingsItem(
                        icon = rememberVectorPainter(SfSymbols.Monochrome.SFPencil),
                        title = {
                            Text(
                                if (listenBrainzToken.isBlank()) {
                                    stringResource(R.string.set_listenbrainz_token)
                                } else {
                                    stringResource(R.string.listenbrainz_token_set)
                                }
                            )
                        },
                        onClick = { showListenBrainzTokenEditor = true }
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Links Section
            IosSettingsGroup(
                title = stringResource(R.string.integrations),
                items = buildList {
                    add(
                        IosSettingsItem(
                            icon = rememberVectorPainter(SfSymbols.Monochrome.SFWrenchAdjustable),
                            title = { Text(stringResource(R.string.integrations)) },
                            onClick = { navController.navigate("settings/integrations") }
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))



        if (showTokenEditor) {
            val text = """
                ***INNERTUBE COOKIE*** =$innerTubeCookie
                ***VISITOR DATA*** =$visitorData
                ***DATASYNC ID*** =$dataSyncId
                ***ACCOUNT NAME*** =$accountNamePref
                ***ACCOUNT EMAIL*** =$accountEmail
                ***ACCOUNT CHANNEL HANDLE*** =$accountChannelHandle
            """.trimIndent()

            TextFieldDialog(
                initialTextFieldValue = TextFieldValue(text),
                onDone = { data ->
                    var cookie = ""
                    var visitorDataValue = ""
                    var dataSyncIdValue = ""
                    var accountNameValue = ""
                    var accountEmailValue = ""
                    var accountChannelHandleValue = ""

                    data.split("\n").forEach {
                        when {
                            it.startsWith("***INNERTUBE COOKIE*** =") -> cookie = it.substringAfter("=")
                            it.startsWith("***VISITOR DATA*** =") -> visitorDataValue = it.substringAfter("=")
                            it.startsWith("***DATASYNC ID*** =") -> dataSyncIdValue = it.substringAfter("=")
                            it.startsWith("***ACCOUNT NAME*** =") -> accountNameValue = it.substringAfter("=")
                            it.startsWith("***ACCOUNT EMAIL*** =") -> accountEmailValue = it.substringAfter("=")
                            it.startsWith("***ACCOUNT CHANNEL HANDLE*** =") -> accountChannelHandleValue = it.substringAfter("=")
                        }
                    }
                    accountSettingsViewModel.saveTokenAndRestart(
                        context = context,
                        cookie = cookie,
                        visitorData = visitorDataValue,
                        dataSyncId = dataSyncIdValue,
                        accountName = accountNameValue,
                        accountEmail = accountEmailValue,
                        accountChannelHandle = accountChannelHandleValue,
                    )
                },
                onDismiss = { showTokenEditor = false },
                singleLine = false,
                maxLines = 20,
                isInputValid = { fullText ->
                    val cookieLine = fullText.lines()
                        .find { it.startsWith("***INNERTUBE COOKIE*** =") }
                    val cookieValue = cookieLine?.substringAfter("***INNERTUBE COOKIE*** =")?.trim() ?: ""
                    cookieValue.isNotEmpty() && "SAPISID" in parseCookieString(cookieValue)
                },
                extraContent = {
                    InfoLabel(text = stringResource(R.string.token_adv_login_description))
                }
            )
        }
        if (showListenBrainzTokenEditor) {
            TextFieldDialog(
                initialTextFieldValue = TextFieldValue(listenBrainzToken),
                onDone = { data ->
                    onListenBrainzTokenChange(data)
                    showListenBrainzTokenEditor = false
                },
                onDismiss = { showListenBrainzTokenEditor = false },
                singleLine = true,
                maxLines = 1,
                isInputValid = {
                    it.isNotEmpty()
                },
                extraContent = {
                    InfoLabel(text = stringResource(R.string.listenbrainz_scrobbling_description))
                }
            )
        }
        if (showLogoutDialog) {
            DefaultDialog(
                onDismiss = { showLogoutDialog = false },
                title = { Text(stringResource(R.string.logout_dialog_title)) },
                buttons = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        ToggleButton(
                            checked = false,
                            onCheckedChange = { showLogoutDialog = false },
                            modifier = Modifier.weight(1f),
                            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                        ) {
                            Text(stringResource(android.R.string.cancel))
                        }

                        ToggleButton(
                            checked = false,
                            onCheckedChange = {
                                accountSettingsViewModel.logoutAndClearSyncedContent(context, onInnerTubeCookieChange)
                                showLogoutDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                contentColor = AppTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.logout_clear_data))
                        }

                        // Keep Data button (Primary OK)
                        ToggleButton(
                            checked = true,
                            onCheckedChange = {
                                accountSettingsViewModel.logoutKeepData(context, onInnerTubeCookieChange)
                                showLogoutDialog = false
                                navController.navigateUp()
                            },
                            modifier = Modifier.weight(1f),
                            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                        ) {
                            Text(stringResource(R.string.logout_keep_data))
                        }
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.logout_dialog_message),
                    style = AppTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

    }
}
