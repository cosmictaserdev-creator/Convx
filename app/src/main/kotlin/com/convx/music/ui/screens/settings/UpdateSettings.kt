package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Switch
import androidx.compose.material3.SwitchDefaults
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.core.component.UpdateInfoDialog
import com.convx.music.ui.utils.backToMain
import com.convx.music.core.updater.getAutoUpdateCheckSetting
import com.convx.music.core.updater.saveAutoUpdateCheckSetting
import com.convx.music.core.updater.getUpdateAvailableState
import com.convx.music.core.updater.saveUpdateAvailableState
import com.convx.music.core.updater.getUpdateNotificationsSetting
import com.convx.music.core.updater.saveUpdateNotificationsSetting
import android.widget.Toast
import androidx.compose.ui.res.pluralStringResource
import com.convx.music.core.updater.getDownloadedApkCount
import com.convx.music.core.updater.clearDownloadedApks
import com.convx.music.core.updater.getBetaUpdatesSetting
import com.convx.music.core.updater.saveBetaUpdatesSetting
import com.convx.music.core.updater.autoClearOldApks
import com.convx.music.viewmodels.UpdateViewModel
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.BuildConfig

/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current
    val updateViewModel: UpdateViewModel = koinViewModel()
    var autoUpdateEnabled by remember { mutableStateOf(getAutoUpdateCheckSetting(context)) }
    var updateNotificationsEnabled by remember { mutableStateOf(getUpdateNotificationsSetting(context)) }
    val alertsPaused = com.convx.music.core.updater.AUTOMATIC_UPDATE_ALERTS_PAUSED
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

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.update_settings_title),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        IosSettingsGroup(
            title = stringResource(R.string.app_updates_title),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.network_update),
                    title = { Text(stringResource(R.string.system_update)) },
                    description = {
                        if (isUpdateAvailable) {
                            Text(
                                text = stringResource(R.string.update_available),
                                color = AppTheme.colorScheme.error
                            )
                        } else {
                            Text(stringResource(R.string.app_update_uptodate))
                        }
                    },
                    onClick = {
                        navController.navigate("update")
                    }
                ),
                IosSettingsItem(
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
                
                IosSettingsItem(
                    icon = painterResource(R.drawable.update),
                    title = { Text(stringResource(R.string.auto_update_check)) },
                    description = { Text(if (alertsPaused) stringResource(R.string.app_updates_paused) else stringResource(R.string.auto_update_check_subtitle)) },
                    trailingContent = {
                        Switch(
                            checked = autoUpdateEnabled,
                            enabled = !alertsPaused,
                            onCheckedChange = { enabled ->
                                autoUpdateEnabled = enabled
                                saveAutoUpdateCheckSetting(context, enabled)
                                if (!enabled) {
                                    saveUpdateAvailableState(context, false)
                                }
                            }
                        )
                    },
                    onClick = {
                        if (!alertsPaused) {
                            autoUpdateEnabled = !autoUpdateEnabled
                            saveAutoUpdateCheckSetting(context, autoUpdateEnabled)
                            if (!autoUpdateEnabled) saveUpdateAvailableState(context, false)
                        }
                    }
                ),

                IosSettingsItem(
                    icon = painterResource(R.drawable.notification),
                    title = { Text(stringResource(R.string.update_notifications)) },
                    description = { Text(if (alertsPaused) stringResource(R.string.app_updates_paused) else stringResource(R.string.update_notifications_subtitle)) },
                    trailingContent = {
                        Switch(
                            checked = updateNotificationsEnabled,
                            enabled = !alertsPaused,
                            onCheckedChange = { enabled ->
                                updateNotificationsEnabled = enabled
                                saveUpdateNotificationsSetting(context, enabled)
                            }
                        )
                    },
                    onClick = {
                        if (!alertsPaused) {
                            updateNotificationsEnabled = !updateNotificationsEnabled
                            saveUpdateNotificationsSetting(context, updateNotificationsEnabled)
                        }
                    }
                ),

                IosSettingsItem(
                    icon = painterResource(R.drawable.biotech),
                    title = { Text(stringResource(R.string.beta_updates)) },
                    description = { Text(stringResource(R.string.beta_updates_subtitle)) },
                    trailingContent = {
                        Switch(
                            checked = betaUpdatesEnabled,
                            onCheckedChange = { enabled ->
                                betaUpdatesEnabled = enabled
                                saveBetaUpdatesSetting(context, enabled)
                            }
                        )
                    },
                    onClick = {
                        betaUpdatesEnabled = !betaUpdatesEnabled
                        saveBetaUpdatesSetting(context, betaUpdatesEnabled)
                    }
                ),

                IosSettingsItem(
                    icon = painterResource(R.drawable.delete),
                    title = { Text(stringResource(R.string.clear_downloaded_updates)) },
                    description = {
                        if (apkCount == 0) {
                            Text(
                                text = stringResource(R.string.clear_downloaded_updates_desc),
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = pluralStringResource(R.plurals.n_apk_found, apkCount, apkCount),
                                color = AppTheme.colorScheme.error
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            onLongClick = {}
                        ) {
                            SymbolIcon(symbol = sf("info.circle"), contentDescription = null, size = 20.dp)
                        }
                    },
                    onClick = {
                        if (apkCount > 0) {
                            if (clearDownloadedApks(context)) {
                                updateViewModel.clearDownloadedUpdate()
                                apkCount = 0
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to delete some files", Toast.LENGTH_SHORT).show()
                                apkCount = getDownloadedApkCount(context)
                            }
                        }
                    }
                )

            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        IosSettingsGroup(
            title = stringResource(R.string.changelog),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.history),
                    title = { Text(stringResource(R.string.changelog)) },
                    description = { Text(stringResource(R.string.view_version_history)) },
                    onClick = { navController.navigate("settings/changelog") }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.commit),
                    title = { Text(stringResource(R.string.commits)) },
                    description = { Text(stringResource(R.string.view_commit_history)) },
                    onClick = { navController.navigate("settings/commits") }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
    
    }
}
