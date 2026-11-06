@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.convx.music.core.updater

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.convx.music.BuildConfig
import com.convx.music.R
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.utils.MenuItemChrome
import com.convx.music.ui.utils.additiveChrome
import com.convx.music.viewmodels.UpdateViewModel
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults

@Composable
fun UpdateScreen(navController: NavHostController, viewModel: UpdateViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.check(manual = true) }
    Column(Modifier.fillMaxSize().background(AppTheme.colorScheme.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            CupertinoButton(onClick = { navController.navigateUp() }, colors = CupertinoButtonDefaults.plainButtonColors()) {
                SymbolIcon(symbol = sf("chevron.left"), contentDescription = stringResource(R.string.back_content_desc), size = 20.dp)
            }
            Text(stringResource(R.string.app_updates_title), fontWeight = FontWeight.Bold, style = AppTheme.typography.titleLarge)
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val release = state.release
            if (release != null && maxWidth >= 840.dp) {
                Row(Modifier.fillMaxSize().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 16.dp)) { UpdateSummary(release); ReleaseLink(release) }
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 16.dp)) { UpdateChangelog(release) }
                }
            } else {
                Column(Modifier.widthIn(max = 680.dp).fillMaxWidth().align(Alignment.TopCenter).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    if (release != null) { UpdateSummary(release); UpdateChangelog(release); ReleaseLink(release) }
                    else {
                        SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 48.dp)
                        Text(stringResource(if (!state.enabled) R.string.updater_channel_disabled else if (state.phase == UpdatePhase.CHECKING) R.string.checking_for_updates else if (state.error != null) R.string.cant_check_updates else R.string.on_latest_version), style = AppTheme.typography.headlineSmall)
                        Text(stringResource(R.string.current_version_v, BuildConfig.VERSION_NAME), color = AppTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Column(Modifier.widthIn(max = 680.dp).fillMaxWidth().align(Alignment.CenterHorizontally).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UpdateStatus(state)
            if (state.release != null) {
                UpdateActions(state, onDownload = viewModel::download, onCancel = viewModel::cancel)
                Text(stringResource(R.string.updater_install_notice), style = AppTheme.typography.bodySmall, color = AppTheme.colorScheme.onSurfaceVariant)
            } else CupertinoButton(onClick = { viewModel.check(manual = true) }, enabled = state.enabled && state.phase != UpdatePhase.CHECKING, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.check_for_update))
            }
        }
    }
}

@Composable
internal fun UpdateSummary(release: UpdateRelease, compact: Boolean = false) {
    val shape = ContinuousRoundedRectangle(20.dp)
    if (!compact) {
        if (release.imageUrl != null) AsyncImage(model = release.imageUrl, contentDescription = null,
            modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp).aspectRatio(16f / 9f).clip(shape), contentScale = ContentScale.Fit)
        else Box(Modifier.fillMaxWidth().height(100.dp).clip(shape).background(AppTheme.colorScheme.surfaceContainerHigh), contentAlignment = Alignment.Center) {
            SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 48.dp)
        }
        Spacer(Modifier.height(16.dp))
    }
    val dark = AppTheme.colorScheme.surface.luminance() < 0.5f
    Text(stringResource(R.string.update_to_v, release.version), fontWeight = FontWeight.Bold,
        style = if (compact) AppTheme.typography.titleLarge else AppTheme.typography.headlineSmall,
        modifier = Modifier.semantics { heading() }.additiveChrome(compact && dark, strength = MenuItemChrome))
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.updater_release_details, release.publishedAt.take(10), "%.1f".format(release.asset.size / 1048576.0)),
        style = AppTheme.typography.bodySmall, color = AppTheme.colorScheme.onSurfaceVariant)
    if (release.description.isNotBlank()) {
        Spacer(Modifier.height(12.dp))
        Text(if (compact) release.description.lineSequence().firstOrNull { it.isNotBlank() }.orEmpty().take(220) else release.description,
            style = AppTheme.typography.bodyMedium, maxLines = if (compact) 3 else Int.MAX_VALUE)
    }
}

@Composable
private fun UpdateChangelog(release: UpdateRelease) {
    for (section in release.changelog) {
        Text(section.title, style = AppTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).semantics { heading() })
        section.items.forEach { item -> Text("• $item", modifier = Modifier.padding(bottom = 8.dp), style = AppTheme.typography.bodyMedium) }
    }
}

@Composable
internal fun ReleaseLink(release: UpdateRelease) {
    val context = LocalContext.current
    if (release.releaseUrl.isNotBlank()) {
        CupertinoButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.releaseUrl))) },
            colors = CupertinoButtonDefaults.plainButtonColors()) {
            Text(stringResource(R.string.updater_view_release))
        }
    }
}

@Composable
internal fun UpdateStatus(state: UpdateUiState) {
    state.error?.let { Text(stringResource(it.messageResource()), color = AppTheme.colorScheme.error, style = AppTheme.typography.bodyMedium) }
    val label = when (state.phase) {
        UpdatePhase.QUEUED -> stringResource(R.string.updater_waiting)
        UpdatePhase.DOWNLOADING -> state.progress?.let { stringResource(R.string.downloading_percent, (it * 100).toInt()) } ?: stringResource(R.string.downloading_update)
        UpdatePhase.VERIFYING -> stringResource(R.string.updater_verifying)
        UpdatePhase.INSTALLING -> stringResource(R.string.updater_installing)
        UpdatePhase.AWAITING_APPROVAL -> stringResource(R.string.updater_approval)
        UpdatePhase.READY -> stringResource(R.string.update_ready)
        UpdatePhase.INSTALLED -> stringResource(R.string.updater_installed)
        else -> null
    }
    if (label != null) Text(label, style = AppTheme.typography.bodyMedium)
    if (state.phase in setOf(UpdatePhase.DOWNLOADING, UpdatePhase.VERIFYING, UpdatePhase.QUEUED)) {
        val progress = state.progress?.coerceIn(0f, 1f)
        Box(Modifier.fillMaxWidth().height(5.dp).clip(ContinuousRoundedRectangle(3.dp)).background(AppTheme.colorScheme.surfaceContainerHigh)
            .semantics { progressBarRangeInfo = progress?.let { ProgressBarRangeInfo(it, 0f..1f) } ?: ProgressBarRangeInfo.Indeterminate }) {
            if (progress != null) Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(AppTheme.colorScheme.primary))
        }
    }
}

@Composable
internal fun UpdateActions(state: UpdateUiState, onDownload: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val busy = UpdatePolicy.isTransfer(state.phase)
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CupertinoButton(onClick = {
            if (state.phase == UpdatePhase.READY) context.startActivity(Intent(context, UpdateInstallActivity::class.java)) else onDownload()
        }, enabled = state.enabled && !busy && state.phase != UpdatePhase.CHECKING && state.phase != UpdatePhase.INSTALLED) {
            Text(stringResource(if (state.phase == UpdatePhase.READY) R.string.install else R.string.update_available))
        }
        if (state.phase in setOf(UpdatePhase.QUEUED, UpdatePhase.DOWNLOADING, UpdatePhase.VERIFYING)) {
            CupertinoButton(onClick = onCancel, colors = CupertinoButtonDefaults.plainButtonColors()) { Text(stringResource(R.string.cancel)) }
        }
    }
}

private fun UpdateFailure.messageResource(): Int = when (this) {
    UpdateFailure.NETWORK -> R.string.check_internet_connection
    UpdateFailure.RATE_LIMIT -> R.string.updater_rate_limit
    UpdateFailure.STORAGE -> R.string.updater_storage
    UpdateFailure.VERIFICATION -> R.string.updater_verification_failed
    UpdateFailure.INCOMPATIBLE -> R.string.updater_incompatible
    UpdateFailure.INSTALL_BLOCKED -> R.string.updater_blocked
    UpdateFailure.INSTALL_CANCELLED -> R.string.updater_cancelled
    UpdateFailure.INSTALL_FAILED -> R.string.updater_install_failed
    UpdateFailure.UNAVAILABLE -> R.string.updater_unavailable
    UpdateFailure.METADATA -> R.string.updater_metadata_failed
}
