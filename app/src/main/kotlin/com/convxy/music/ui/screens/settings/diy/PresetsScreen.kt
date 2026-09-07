/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings.diy

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.convxy.music.R
import com.convxy.music.constants.PresetCategory
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.player.customize.DiyOrientation
import com.convxy.music.ui.player.customize.DiyPlayerMockup
import com.convxy.music.ui.player.customize.rememberDiyLayout
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.preset.PresetStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The user's saved looks: a grid of thumbnails, each one a complete visual snapshot that can be
 * applied, renamed, shared as a file, or deleted.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PresetsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var presets by remember { mutableStateOf(emptyList<PresetStore.Meta>()) }
    var showSaveSheet by remember { mutableStateOf(false) }
    var pendingApply by remember { mutableStateOf<PresetStore.Meta?>(null) }
    var pendingMenu by remember { mutableStateOf<PresetStore.Meta?>(null) }
    var pendingRename by remember { mutableStateOf<PresetStore.Meta?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch { presets = withContext(Dispatchers.IO) { PresetStore.list(context) } }
    }
    LaunchedEffect(Unit) { refresh() }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val result = withContext(Dispatchers.IO) { PresetStore.import(context, uri) }
            message = when (result) {
                is PresetStore.ImportResult.Ok -> context.getString(R.string.preset_imported)
                is PresetStore.ImportResult.Failed -> context.getString(
                    when (result.reason) {
                        PresetStore.ImportResult.Reason.TOO_LARGE -> R.string.preset_import_too_large
                        PresetStore.ImportResult.Reason.UNSUPPORTED_VERSION ->
                            R.string.preset_import_newer_version
                        else -> R.string.preset_import_failed
                    },
                )
            }
            refresh()
        }
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = { message = null },
            title = { Text(stringResource(R.string.presets)) },
            text = { Text(text) },
            confirmButton = {
                TextButton(onClick = { message = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )
    }

    if (showSaveSheet) {
        SavePresetDialog(
            onDismiss = { showSaveSheet = false },
            onSave = { name, categories, thumbnail ->
                showSaveSheet = false
                scope.launch {
                    withContext(Dispatchers.IO) {
                        PresetStore.capture(context, name, categories, thumbnail)
                    }
                    refresh()
                }
            },
        )
    }

    pendingApply?.let { meta ->
        ApplyPresetDialog(
            meta = meta,
            onDismiss = { pendingApply = null },
            onApply = { categories ->
                pendingApply = null
                scope.launch {
                    withContext(Dispatchers.IO) {
                        PresetStore.apply(context, meta.id, categories)
                    }
                    message = context.getString(R.string.preset_applied)
                }
            },
        )
    }

    pendingRename?.let { meta ->
        var name by remember(meta.id) { mutableStateOf(meta.name) }
        AlertDialog(
            onDismissRequest = { pendingRename = null },
            title = { Text(stringResource(R.string.preset_rename)) },
            text = {
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = name.trim().take(60)
                    pendingRename = null
                    if (trimmed.isNotEmpty()) {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                PresetStore.rename(context, meta.id, trimmed)
                            }
                            refresh()
                        }
                    }
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRename = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    pendingMenu?.let { meta ->
        AlertDialog(
            onDismissRequest = { pendingMenu = null },
            title = { Text(meta.name) },
            text = {
                Column {
                    TextButton(onClick = {
                        pendingMenu = null
                        pendingRename = meta
                    }) { Text(stringResource(R.string.preset_rename)) }
                    TextButton(onClick = {
                        pendingMenu = null
                        scope.launch { sharePreset(context, meta) }
                    }) { Text(stringResource(R.string.preset_share)) }
                    TextButton(onClick = {
                        pendingMenu = null
                        scope.launch {
                            withContext(Dispatchers.IO) { PresetStore.delete(context, meta.id) }
                            refresh()
                        }
                    }) {
                        Text(
                            stringResource(R.string.preset_delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { pendingMenu = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = appTopBarWindowInsets(),
                title = { Text(stringResource(R.string.presets)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        onLongClick = {},
                    ) {
                        Icon(painterResource(R.drawable.restore), contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSaveSheet = true },
                icon = { Icon(painterResource(R.drawable.palette), contentDescription = null) },
                text = { Text(stringResource(R.string.preset_save_current)) },
            )
        },
    ) { padding ->
        if (presets.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.preset_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(presets, key = { it.id }) { meta ->
                PresetCard(
                    meta = meta,
                    onClick = { pendingApply = meta },
                    onLongClick = { pendingMenu = meta },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetCard(
    meta: PresetStore.Meta,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val context = LocalContext.current
    val thumbnail = remember(meta.id) { PresetStore.thumbnailFile(context, meta.id) }
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnail != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(thumbnail).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    painterResource(R.drawable.palette),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = meta.name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
        )
    }
}

/**
 * Names the preset, picks what goes in it, and captures the thumbnail.
 *
 * The mockup shown here *is* the thumbnail: it is drawn into a graphics layer and read back on
 * save, so the card in the grid is exactly the preview the user approved rather than a separate
 * render that might drift from it.
 */
@Composable
private fun SavePresetDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, categories: Set<PresetCategory>, thumbnail: Bitmap?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf(PresetCategory.entries.toSet()) }
    val layout = rememberDiyLayout()
    val graphicsLayer = rememberGraphicsLayer()

    // The preview is captured as soon as it has been drawn once, not when OK is pressed. Doing it
    // at press time raced the dialog's teardown and failed silently, which is how presets ended up
    // with no thumbnail at all.
    var drawn by remember { mutableStateOf(false) }
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }
    var captureFailed by remember { mutableStateOf(false) }

    LaunchedEffect(drawn, layout) {
        if (!drawn) return@LaunchedEffect
        val captured = runCatching { graphicsLayer.toImageBitmap().asAndroidBitmap() }
        thumbnail = captured.getOrNull()
        captureFailed = captured.isFailure
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.preset_save_current)) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(180.dp)
                        .aspectRatio(9f / 19.5f)
                        .clip(RoundedCornerShape(14.dp))
                        .drawWithContent {
                            graphicsLayer.record { this@drawWithContent.drawContent() }
                            drawLayer(graphicsLayer)
                            if (!drawn) drawn = true
                        },
                ) {
                    DiyPlayerMockup(
                        orientation = DiyOrientation.PORTRAIT,
                        modifier = Modifier.fillMaxSize(),
                        layout = layout,
                    )
                }
                if (captureFailed) {
                    Text(
                        text = stringResource(R.string.preset_thumbnail_failed),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.preset_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                CategoryChecklist(
                    categories = categories,
                    onChange = { categories = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && categories.isNotEmpty(),
                onClick = { onSave(name.trim().take(60), categories, thumbnail) },
            ) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}

@Composable
private fun ApplyPresetDialog(
    meta: PresetStore.Meta,
    onDismiss: () -> Unit,
    onApply: (Set<PresetCategory>) -> Unit,
) {
    // Only offer what this preset actually contains; ticking a category it never recorded would
    // promise a change that cannot happen.
    var selected by remember(meta.id) { mutableStateOf(meta.categories) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(meta.name) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    stringResource(R.string.preset_apply_desc),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(8.dp))
                CategoryChecklist(
                    categories = selected,
                    available = meta.categories,
                    onChange = { selected = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = selected.isNotEmpty(),
                onClick = { onApply(selected) },
            ) { Text(stringResource(R.string.preset_apply)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}

@Composable
private fun CategoryChecklist(
    categories: Set<PresetCategory>,
    available: Set<PresetCategory> = PresetCategory.entries.toSet(),
    onChange: (Set<PresetCategory>) -> Unit,
) {
    Column {
        PresetCategory.entries.filter { it in available }.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = category in categories,
                    onCheckedChange = { checked ->
                        onChange(if (checked) categories + category else categories - category)
                    },
                )
                Text(
                    text = stringResource(presetCategoryLabel(category)),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun presetCategoryLabel(category: PresetCategory): Int = when (category) {
    PresetCategory.COLORS -> R.string.preset_category_colors
    PresetCategory.LAYOUT -> R.string.preset_category_layout
    PresetCategory.FONT -> R.string.preset_category_font
    PresetCategory.PLAYER -> R.string.preset_category_player
    PresetCategory.LYRICS -> R.string.preset_category_lyrics
    PresetCategory.GLASS -> R.string.preset_category_glass
    PresetCategory.BACKGROUND -> R.string.preset_category_background
    PresetCategory.PLAYER_ICONS -> R.string.preset_category_player_icons
    PresetCategory.DIY -> R.string.preset_category_diy
}

private suspend fun sharePreset(context: android.content.Context, meta: PresetStore.Meta) {
    val file = withContext(Dispatchers.IO) { PresetStore.export(context, meta) } ?: return
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(intent, context.getString(R.string.preset_share)),
    )
}
