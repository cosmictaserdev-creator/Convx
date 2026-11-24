@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.convx.music.core.updater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.convx.music.R
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.LocalPooledGlass
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.glassUsesDarkAppearance
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.foundation.LocalIosBackdrop
import com.convx.music.ui.ios.foundation.IosMenuMaterial
import com.convx.music.viewmodels.UpdateViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults

@Composable
fun FloatingUpdaterHost(visible: Boolean, backdrop: Backdrop, onDetails: () -> Unit) {
    // Defer feature construction, disk reads and WorkManager until after the first rendered frame.
    var mounted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { awaitFrame(); delay(5_000); mounted = true }
    if (mounted) FloatingUpdater(visible, backdrop, onDetails)
}

@Composable
private fun FloatingUpdater(visible: Boolean, backdrop: Backdrop, onDetails: () -> Unit, viewModel: UpdateViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (visible) LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.check() }
    // Re-check snooze policy on return to Home without creating a polling timer.
    LaunchedEffect(visible) { if (visible) viewModel.check() }
    AnimatedVisibility(visible = visible && state.enabled && state.floatingVisible && state.release != null,
        enter = fadeIn(tween(180)), exit = fadeOut(tween(120))) {
        CompositionLocalProvider(LocalAppBackdrop provides backdrop, LocalIosBackdrop provides backdrop, LocalPooledGlass provides null) {
            BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                val panelHeight = (maxHeight - 140.dp).coerceAtLeast(100.dp).coerceAtMost(480.dp)
                val topPadding = if (maxHeight < 400.dp) 8.dp else 64.dp
                Column(Modifier.align(Alignment.TopCenter).padding(top = topPadding, start = 12.dp, end = 12.dp)
                    .widthIn(max = 520.dp).fillMaxWidth().heightIn(max = panelHeight)
                    .liquidGlass(config = LocalGlassEffectConfig.current.let { config ->
                        if (glassUsesDarkAppearance()) config.copy(surfaceTintColor = IosMenuMaterial.DarkPanelFill, surfaceOpacity = 0.82f, blurRadius = 8f)
                        else config.copy(blurRadius = 8f)
                    },
                        shape = ContinuousRoundedRectangle(28.dp), highlightAlpha = 0.20f)
                    .verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        state.release?.let { release ->
                            if (release.imageUrl != null) {
                                AsyncImage(model = release.imageUrl, contentDescription = null,
                                    modifier = Modifier.padding(end = 12.dp).size(64.dp).clip(ContinuousRoundedRectangle(16.dp)),
                                    contentScale = ContentScale.Crop)
                            }
                            Column(Modifier.weight(1f)) { UpdateSummary(release, compact = true) }
                        }
                        CupertinoButton(onClick = viewModel::dismiss, colors = CupertinoButtonDefaults.plainButtonColors(), modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
                            SymbolIcon(symbol = sf("xmark"), contentDescription = stringResource(R.string.dismiss), size = 18.dp)
                        }
                    }
                    UpdateStatus(state)
                    UpdateActions(state, viewModel::download, viewModel::cancel)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CupertinoButton(onClick = viewModel::snooze, colors = CupertinoButtonDefaults.plainButtonColors()) { Text(stringResource(R.string.updater_remind_later)) }
                        CupertinoButton(onClick = onDetails, colors = CupertinoButtonDefaults.plainButtonColors()) { Text(stringResource(R.string.whats_new)) }
                        state.release?.let { ReleaseLink(it) }
                    }
                }
            }
        }
    }
}
