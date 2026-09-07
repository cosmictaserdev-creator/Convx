/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.modulehost.DeclarativeModuleHostViewModel
import com.convx.modulehost.DeclarativeModuleHostLifecycle
import com.convx.modulehost.ModuleState
import com.convx.modulehost.RegisteredModule
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.Material3SettingsGroup
import com.convx.music.ui.component.Material3SettingsItem
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain

/**
 * Generic declarative-module detail page. Everything shown here is rendered
 * from the module's own declared manifest data ([RegisteredModule]), so adding
 * a module never requires Convx-side code — only host conventions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclarativeModuleDetailScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    moduleId: String,
    viewModel: DeclarativeModuleHostViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val errorState by viewModel.error.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val updateOffer by viewModel.updateOffer.collectAsStateWithLifecycle()
    val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
    var confirmRemove by rememberSaveable { mutableStateOf(false) }

    val module = snapshot.modules.firstOrNull { it.manifest.id == moduleId }
    val started = snapshot.lifecycle == DeclarativeModuleHostLifecycle.STARTED

    // The module was removed (or the route is stale): nothing to render.
    LaunchedEffect(module == null) {
        if (module == null && started) navController.navigateUp()
    }
    if (module == null) {
        ModuleDetailScaffold(navController, scrollBehavior) {
            Text(
                text = stringResource(R.string.module_host_missing),
                modifier = Modifier.padding(24.dp),
            )
        }
        return
    }

    val state = module.state
    val toggleable = state == ModuleState.INSTALLED ||
        state == ModuleState.DISABLED ||
        state == ModuleState.ENABLED

    ModuleDetailScaffold(navController, scrollBehavior) {
        // Header card.
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppleTokens.CardCorner))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(module.manifest.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                        Text(
                            stringResource(
                                R.string.module_host_module_state,
                                module.manifest.version,
                                stringResource(state.stringRes()),
                            )
                        )
                    }
                }
                Icon(
                    painterResource(R.drawable.grid_view),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
            if (module.manifest.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    Text(
                        module.manifest.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.module_host_actions),
            items = buildList {
                add(
                    Material3SettingsItem(
                        title = {
                            Text(
                                stringResource(
                                    if (state == ModuleState.ENABLED) {
                                        R.string.module_host_disable_module
                                    } else {
                                        R.string.module_host_enable_module
                                    }
                                )
                            )
                        },
                        description = { Text(stringResource(state.stringRes())) },
                        enabled = started && !busy && toggleable,
                        trailingContent = {
                            Switch(
                                checked = state == ModuleState.ENABLED,
                                enabled = started && !busy && toggleable,
                                onCheckedChange = { on ->
                                    if (on) viewModel.enable(moduleId) else viewModel.disable(moduleId)
                                },
                            )
                        },
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.deployed_app_update),
                        title = { Text(stringResource(R.string.module_host_check_updates)) },
                        description = { Text(stringResource(R.string.module_host_check_updates_hint)) },
                        enabled = started && !busy,
                        onClick = { viewModel.checkForUpdates(moduleId) },
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.delete),
                        title = { Text(stringResource(R.string.module_host_remove)) },
                        description = { Text(stringResource(R.string.module_host_remove)) },
                        enabled = started && !busy,
                        onClick = { confirmRemove = true },
                    )
                )
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.module_host_information),
            items = buildList {
                add(
                    infoItem(
                        stringResource(R.string.module_host_field_id),
                        module.manifest.id,
                    )
                )
                add(
                    infoItem(
                        stringResource(R.string.module_host_field_author),
                        module.manifest.author,
                    )
                )
                add(
                    infoItem(
                        stringResource(R.string.module_host_field_type),
                        module.manifest.type,
                    )
                )
                add(
                    infoItem(
                        stringResource(R.string.module_host_field_compatibility),
                        module.manifest.compatibility.compatibilityText(),
                    )
                )
                add(
                    infoItem(
                        stringResource(R.string.module_host_field_permissions),
                        module.manifest.permissions.joinToString(", "),
                    )
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (confirmRemove) {
        ModuleHostRemoveDialog(
            moduleName = module.manifest.name,
            onConfirm = {
                viewModel.remove(moduleId)
                confirmRemove = false
            },
            onDismiss = { confirmRemove = false },
        )
    }
    ModuleHostErrorDialog(errorState, viewModel::dismissError)
    ModuleHostUpdateOfferDialog(updateOffer, busy, viewModel::applyUpdate, viewModel::dismissUpdateOffer)
    ModuleHostUpdateInfoDialog(updateInfo, viewModel::dismissUpdateInfo)
}

@Composable
private fun infoItem(label: String, value: String): Material3SettingsItem =
    Material3SettingsItem(
        title = { Text(label) },
        description = { Text(value) },
        enabled = false,
    )

private fun com.convx.modulehost.ModuleCompatibility.compatibilityText(): String {
    val minimum = minConvxVersion
    val maximum = maxConvxVersion
    return if (maximum == null) "\u2265 $minimum" else "$minimum \u2013 $maximum"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleDetailScaffold(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top),
            )
        )
        content()
    }
    TopAppBar(
        title = { Text(stringResource(R.string.module_host_module)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        windowInsets = appTopBarWindowInsets(),
        scrollBehavior = scrollBehavior,
    )
}

private fun ModuleState.stringRes(): Int = when (this) {
    ModuleState.DISCOVERED -> R.string.module_state_discovered
    ModuleState.VALIDATED -> R.string.module_state_validated
    ModuleState.STAGED -> R.string.module_state_staged
    ModuleState.INSTALLED -> R.string.module_state_installed
    ModuleState.ENABLED -> R.string.module_state_enabled
    ModuleState.DISABLED -> R.string.module_state_disabled
    ModuleState.INVALID -> R.string.module_state_invalid
    ModuleState.INCOMPATIBLE -> R.string.module_state_incompatible
    ModuleState.FAILED -> R.string.module_state_failed
    ModuleState.ROLLED_BACK -> R.string.module_state_rolled_back
}
