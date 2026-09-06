package com.convx.music.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclarativeModuleHostSettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeclarativeModuleHostViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val errorState by viewModel.error.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    var pendingRemove by rememberSaveable { mutableStateOf<String?>(null) }

    val installLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.installPackage(context.contentResolver, it) }
    }

    val started = snapshot.lifecycle == DeclarativeModuleHostLifecycle.STARTED

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
        Material3SettingsGroup(
            title = stringResource(R.string.declarative_modules),
            items = buildList {
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.add),
                        title = { Text(stringResource(R.string.module_host_install)) },
                        description = { Text(stringResource(R.string.module_host_install_hint)) },
                        enabled = started && !busy,
                        onClick = { installLauncher.launch(arrayOf("application/octet-stream")) },
                    )
                )
                add(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.info),
                        title = {
                            Text(
                                stringResource(
                                    if (started) R.string.module_host_ready else R.string.module_host_not_started,
                                )
                            )
                        },
                        description = { Text(stringResource(R.string.module_host_version, snapshot.convxVersion)) },
                        enabled = false,
                    )
                )
            },
        )

        if (snapshot.modules.isNotEmpty()) {
            Spacer(modifier = Modifier.height(27.dp))
            ModuleListCard(
                modules = snapshot.modules,
                onToggle = { module ->
                    when (module.state) {
                        ModuleState.INSTALLED, ModuleState.DISABLED -> viewModel.enable(module.manifest.id)
                        ModuleState.ENABLED -> viewModel.disable(module.manifest.id)
                        else -> Unit
                    }
                },
                onRemove = { pendingRemove = it.manifest.id },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    val removing = pendingRemove?.let { id -> snapshot.modules.firstOrNull { it.manifest.id == id } }
    if (removing != null) {
        AlertDialog(
            onDismissRequest = { pendingRemove = null },
            title = { Text(stringResource(R.string.module_host_remove_confirm_title)) },
            text = { Text(stringResource(R.string.module_host_remove_confirm_body, removing.manifest.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.remove(removing.manifest.id)
                    pendingRemove = null
                }) {
                    Text(stringResource(R.string.module_host_remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemove = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    val error = errorState
    if (error != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            icon = { Icon(painterResource(R.drawable.error), null) },
            title = { Text(stringResource(R.string.module_host_error)) },
            text = {
                Column {
                    Text(stringResource(error.messageRes))
                    if (error.detail.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                            Text(error.detail)
                        }
                    }
                }
            },
        )
    }

    TopAppBar(
        title = { Text(stringResource(R.string.declarative_modules)) },
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

/**
 * Card listing installed modules, visually matching [Material3SettingsGroup].
 *
 * Unlike the shared settings group, only the icon/title/description area is
 * clickable here: the row toggle and the trailing remove button are separate
 * hit targets, so tapping remove never also fires the row's enable/disable.
 */
@Composable
private fun ModuleListCard(
    modules: List<RegisteredModule>,
    onToggle: (RegisteredModule) -> Unit,
    onRemove: (RegisteredModule) -> Unit,
) {
    Text(
        text = stringResource(R.string.module_host_installed_modules),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppleTokens.CardCorner))
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        modules.forEachIndexed { index, module ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
            }
            ModuleRow(
                module = module,
                toggleable = module.state == ModuleState.INSTALLED ||
                    module.state == ModuleState.DISABLED ||
                    module.state == ModuleState.ENABLED,
                onToggle = { onToggle(module) },
                onRemove = { onRemove(module) },
            )
        }
    }
}

@Composable
private fun ModuleRow(
    module: RegisteredModule,
    toggleable: Boolean,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.grid_view),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = toggleable, onClick = onToggle),
        ) {
            Text(module.manifest.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(2.dp))
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                Text(
                    stringResource(
                        R.string.module_host_module_state,
                        module.manifest.version,
                        stringResource(module.state.stringRes()),
                    )
                )
            }
        }
        IconButton(
            onClick = onRemove,
            onLongClick = onRemove,
        ) {
            Icon(
                painterResource(R.drawable.delete),
                contentDescription = stringResource(R.string.module_host_remove),
            )
        }
    }
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
