/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.DisableScreenshotKey
import com.convx.music.history.ListenHistoryRepository
import com.convx.music.history.ListenHistorySettingsRepository
import com.convx.music.history.ListenHistorySettingsState
import com.convx.music.search.SearchHistoryCommands
import com.convx.music.search.SearchHistorySettingsRepository
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val scope = rememberCoroutineScope()
    val searchHistoryCommands: SearchHistoryCommands = koinInject()
    val searchHistorySettingsRepository: SearchHistorySettingsRepository = koinInject()
    val searchHistorySettingsFlow = remember(searchHistorySettingsRepository) {
        searchHistorySettingsRepository.observe()
    }
    val searchHistorySettings by searchHistorySettingsFlow
        .collectAsStateWithLifecycle(initialValue = null)
    val listenHistoryRepository: ListenHistoryRepository = koinInject()
    val listenHistorySettingsRepository: ListenHistorySettingsRepository = koinInject()
    val listenHistorySettingsFlow = remember(listenHistorySettingsRepository) {
        listenHistorySettingsRepository.observe()
    }
    val listenHistorySettings by listenHistorySettingsFlow.collectAsStateWithLifecycle(
        initialValue = ListenHistorySettingsState.Loading,
    )
    val pauseListenHistory =
        (listenHistorySettings as? ListenHistorySettingsState.Ready)?.settings?.paused == true
    val onPauseListenHistoryChange: (Boolean) -> Unit = { paused ->
        scope.launch { listenHistorySettingsRepository.setPaused(paused) }
    }
    val pauseSearchHistory = searchHistorySettings?.paused ?: false
    val onPauseSearchHistoryChange: (Boolean) -> Unit = { paused ->
        scope.launch { searchHistorySettingsRepository.setPaused(paused) }
    }
    val (disableScreenshot, onDisableScreenshotChange) = rememberPreference(
        key = DisableScreenshotKey,
        defaultValue = false
    )

    var showClearListenHistoryDialog by remember {
        mutableStateOf(false)
    }

    if (showClearListenHistoryDialog) {
        DefaultDialog(
            onDismiss = { showClearListenHistoryDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.clear_listen_history_confirm),
                    style = AppTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
            },
            buttons = {
                TextButton(
                    onClick = { showClearListenHistoryDialog = false },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                TextButton(
                    onClick = {
                        showClearListenHistoryDialog = false
                        scope.launch { listenHistoryRepository.clear() }
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    var showClearSearchHistoryDialog by remember {
        mutableStateOf(false)
    }

    if (showClearSearchHistoryDialog) {
        DefaultDialog(
            onDismiss = { showClearSearchHistoryDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.clear_search_history_confirm),
                    style = AppTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
            },
            buttons = {
                TextButton(
                    onClick = { showClearSearchHistoryDialog = false },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                TextButton(
                    onClick = {
                        showClearSearchHistoryDialog = false
                        scope.launch { searchHistoryCommands.clear() }
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.privacy),
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {

        IosSettingsGroup(
            title = stringResource(R.string.listen_history),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.history),
                    title = { Text(stringResource(R.string.pause_listen_history)) },
                    trailingContent = {
                        Switch(
                            checked = pauseListenHistory,
                            onCheckedChange = onPauseListenHistoryChange
                        )
                    },
                    onClick = { onPauseListenHistoryChange(!pauseListenHistory) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.delete_history),
                    title = { Text(stringResource(R.string.clear_listen_history)) },
                    onClick = { showClearListenHistoryDialog = true }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.search_history),
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.search_off),
                    title = { Text(stringResource(R.string.pause_search_history)) },
                    trailingContent = {
                        Switch(
                            checked = pauseSearchHistory,
                            onCheckedChange = onPauseSearchHistoryChange
                        )
                    },
                    onClick = { onPauseSearchHistoryChange(!pauseSearchHistory) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.clear_all),
                    title = { Text(stringResource(R.string.clear_search_history)) },
                    onClick = { showClearSearchHistoryDialog = true }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "Screen security",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.screenshot),
                    title = { Text(stringResource(R.string.disable_screenshot)) },
                    description = { Text(stringResource(R.string.disable_screenshot_desc)) },
                    trailingContent = {
                        Switch(
                            checked = disableScreenshot,
                            onCheckedChange = onDisableScreenshotChange
                        )
                    },
                    onClick = { onDisableScreenshotChange(!disableScreenshot) }
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
