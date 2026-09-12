package com.nexamusic.app.ui.screens.settings

import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.GlassSwitchCompat as Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.R
import com.nexamusic.app.ui.component.IconButton
import com.nexamusic.app.ui.component.Material3SettingsGroup
import com.nexamusic.app.ui.component.Material3SettingsItem
import com.nexamusic.app.vivimusic.component.UpdateInfoDialog
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.vivimusic.updater.getAutoUpdateCheckSetting
import com.nexamusic.app.vivimusic.updater.saveAutoUpdateCheckSetting
import com.nexamusic.app.vivimusic.updater.getUpdateAvailableState
import com.nexamusic.app.vivimusic.updater.saveUpdateAvailableState
import com.nexamusic.app.vivimusic.updater.getUpdateNotificationsSetting
import com.nexamusic.app.vivimusic.updater.saveUpdateNotificationsSetting
import android.widget.Toast
import androidx.compose.ui.res.pluralStringResource
import com.nexamusic.app.vivimusic.updater.getDownloadedApkCount
import com.nexamusic.app.vivimusic.updater.clearDownloadedApks
import com.nexamusic.app.vivimusic.updater.getBetaUpdatesSetting
import com.nexamusic.app.vivimusic.updater.saveBetaUpdatesSetting
import com.nexamusic.app.vivimusic.updater.autoClearOldApks
import androidx.compose.material3.MaterialTheme
import com.nexamusic.app.BuildConfig

//here b5.0.1 must be used for the beta tag

/**
 * NexaMusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current
    var autoUpdateEnabled by remember { mutableStateOf(getAutoUpdateCheckSetting(context)) }
    var updateNotificationsEnabled by remember { mutableStateOf(getUpdateNotificationsSetting(context)) }
    var betaUpdatesEnabled by remember { mutableStateOf(getBetaUpdatesSetting(context)) }
    val isUpdateAvailable = getUpdateAvailableState(context) && autoUpdateEnabled
    var apkCount by remember { mutableStateOf(getDownloadedApkCount(context)) }
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        autoClearOldApks(context)
        apkCount = getDownloadedApkCount(context)
    }

    if (showInfoDialog) {
        UpdateInfoDialog(onDismiss = { showInfoDialog = false })
    }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Material3SettingsGroup(
            title = stringResource(R.string.app_updates_title),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.network_update),
                    title = { Text(stringResource(R.string.system_update)) },
                    description = {
                        if (isUpdateAvailable) {
                            Text(
                                text = stringResource(R.string.update_available),
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(stringResource(R.string.app_update_uptodate))
                        }
                    },
                    onClick = {
                        val isFoss = !BuildConfig.CAST_AVAILABLE
                        if (isFoss) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nkosanamahungela-dev/NexaMusic-"))
                            context.startActivity(intent)
                        } else {
                            navController.navigate("update")
                        }
                    }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = {
                        Text(stringResource(R.string.version, BuildConfig.VERSION_NAME))
                    },
                    description = {
                        val arch = BuildConfig.ARCHITECTURE
                        val variant = if (BuildConfig.CAST_AVAILABLE) "GMS" else "FOSS"
                        Text("$arch - $variant")
                    }
                ),
                
                Material3SettingsItem(
                    icon = painterResource(R.drawable.update),
                    title = { Text(stringResource(R.string.auto_update_check)) },
                    description = { Text(stringResource(R.string.auto_update_check_subtitle)) },
                    trailingContent = {
                        Switch(
                            checked = autoUpdateEnabled,
                            onCheckedChange = { enabled ->
                                autoUpdateEnabled = enabled
                                saveAutoUpdateCheckSetting(context, enabled)
                                if (!enabled) {
                                    saveUpdateAvailableState(context, false)
                                }
                            },
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
