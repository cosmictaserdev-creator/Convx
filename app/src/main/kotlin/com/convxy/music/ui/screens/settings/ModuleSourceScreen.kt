/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.EnabledModulesKey
import com.convxy.music.constants.FetchedModulesKey
import com.convxy.music.constants.ModuleSourcesKey
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.component.ModernSwitch
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import com.music.spine.ModuleManager
import com.music.spine.SpineModule
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray

private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSourceScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val scope = rememberCoroutineScope()
    val moduleManager = remember { ModuleManager() }

    val (sourcesJson, onSourcesJsonChange) = rememberPreference(ModuleSourcesKey, defaultValue = "[]")
    val (enabledJson, onEnabledJsonChange) = rememberPreference(EnabledModulesKey, defaultValue = "[]")

    val sourceUrls = remember(sourcesJson) {
        runCatching {
            val arr = JSONArray(sourcesJson)
            (0 until arr.length()).map { arr.getString(it) }
        }.getOrElse { emptyList() }
    }

    // Stored in the user's chosen priority order (drag-reorder below) — this is what
    // YTPlayerUtils reads to decide which module to try first at playback time.
    val enabledOrder = remember(enabledJson) {
        runCatching {
            val arr = JSONArray(enabledJson)
            (0 until arr.length()).map { arr.getString(it) }
        }.getOrElse { emptyList() }
    }
    val enabledIds = remember(enabledOrder) { enabledOrder.toSet() }

    var showAddDialog by remember { mutableStateOf(false) }
    val (fetchedModulesJson, onFetchedModulesJsonChange) = rememberPreference(FetchedModulesKey, defaultValue = "[]")
    val fetchedModules = remember(fetchedModulesJson) {
        runCatching {
            json.decodeFromString<List<SpineModule>>(fetchedModulesJson)
        }.getOrElse { emptyList() }
    }
    val availableModules = remember(fetchedModules, enabledIds) {
        fetchedModules.filter { it.id !in enabledIds }
    }
    var isLoading by remember { mutableStateOf(false) }
    var loadingSource by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun saveSources(urls: List<String>) {
        val arr = JSONArray()
        urls.forEach { arr.put(it) }
        onSourcesJsonChange(arr.toString())
    }

    fun saveEnabledOrder(order: List<String>) {
        val arr = JSONArray()
        order.forEach { arr.put(it) }
        onEnabledJsonChange(arr.toString())
    }

    fun toggleModule(moduleId: String) {
        // Disabling drops it from the priority list; enabling appends it to the end
        // (lowest priority until the user drags it up) rather than rebuilding from
        // a Set, which would throw away any order the user already set.
        saveEnabledOrder(
            if (moduleId in enabledOrder) enabledOrder - moduleId else enabledOrder + moduleId
        )
    }

    fun removeSource(url: String) {
        saveSources(sourceUrls.filter { it != url })
    }

    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
            )
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
                )
        ) {
            item {
                Spacer(
                    Modifier.windowInsetsPadding(
                        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
                    )
                )
                Text(
                    text = stringResource(R.string.module_sources),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            item {
                Text(
                    text = "SOURCES",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(AppleTokens.CardCorner))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    sourceUrls.forEachIndexed { index, url ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 64.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 0.5.dp,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = url.substringAfter("://").take(40),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            IconButton(
                                onClick = { removeSource(url) },
                                onLongClick = { removeSource(url) },
                            ) {
                                Icon(
                                    painterResource(R.drawable.close),
                                    contentDescription = stringResource(R.string.remove),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(R.drawable.add),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.add_source),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (sourceUrls.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                val allModules = mutableListOf<SpineModule>()
                                for (url in sourceUrls) {
                                    loadingSource = url
                                    moduleManager.fetchIndex(url).onSuccess { allModules.addAll(it) }
                                }
                                onFetchedModulesJsonChange(
                                    json.encodeToString(
                                            kotlinx.serialization.builtins.ListSerializer(SpineModule.serializer()),
                                            allModules.distinctBy { it.id }
                                        )
                                )
                                isLoading = false
                                loadingSource = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = !isLoading,
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Fetching from ${loadingSource?.substringAfter("://")?.take(30) ?: "..."}")
                        } else {
                            Text(stringResource(R.string.fetch_modules))
                        }
                    }
                }
            }

            if (enabledOrder.isNotEmpty()) {
                item {
                    Text(
                        text = "ENABLED — DRAG TO SET PRIORITY",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    val modulesById = remember(fetchedModules) { fetchedModules.associateBy { it.id } }
                    EnabledModulesPriorityList(
                        enabledOrder = enabledOrder,
                        modulesById = modulesById,
                        onReorder = ::saveEnabledOrder,
                        onDisable = ::toggleModule,
                        onClick = { moduleId -> navController.navigate("settings/modules/$moduleId") },
                    )
                }
            }

            if (availableModules.isNotEmpty()) {
                item {
                    Text(
                        text = "AVAILABLE MODULES",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(availableModules, key = { it.id }) { module ->
                    ModuleItem(
                        module = module,
                        isEnabled = false,
                        onToggle = { toggleModule(module.id) },
                        onClick = {
                            navController.navigate("settings/modules/${module.id}")
                        },
                    )
                }
            }

            if (fetchedModules.isEmpty() && !isLoading && sourceUrls.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_modules_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        var urlInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.add_module_source)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.add_module_source_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        singleLine = true,
                        label = { Text(stringResource(R.string.source_url)) },
                        placeholder = { Text("https://example.com/index.json") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (urlInput.isNotBlank()) {
                            saveSources(sourceUrls + urlInput.trim())
                            showAddDialog = false
                        }
                    },
                    enabled = urlInput.isNotBlank(),
                ) {
                    Text(stringResource(R.string.add_to_an_playlist))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.module_sources)) },
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
        },
    )
}

/**
 * Enabled modules only, in the exact order YTPlayerUtils tries them at playback time —
 * drag the handle to reorder. Fixed row height keeps the drag math (which row a given
 * offset lands on) simple and exact, rather than measuring variable-height rows.
 */
@Composable
private fun EnabledModulesPriorityList(
    enabledOrder: List<String>,
    modulesById: Map<String, SpineModule>,
    onReorder: (List<String>) -> Unit,
    onDisable: (String) -> Unit,
    onClick: (String) -> Unit,
) {
    val density = LocalDensity.current
    val rowHeight = 64.dp
    val rowHeightPx = with(density) { rowHeight.toPx() }

    // Local copy so a drag can reorder live, frame by frame, without waiting for the
    // DataStore write + recomposition round trip on every row swap. Committed back to
    // the caller (and so to the preference) once, on drag end.
    var order by remember(enabledOrder) { mutableStateOf(enabledOrder) }
    var draggingId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(AppleTokens.CardCorner))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        order.forEachIndexed { index, moduleId ->
            val module = modulesById[moduleId] ?: return@forEachIndexed
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 64.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
            }
            val isDragging = draggingId == moduleId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = if (isDragging) dragOffset else 0f
                    }
                    .clickable { onClick(moduleId) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = module.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(
                    onClick = { onDisable(moduleId) },
                    onLongClick = { onDisable(moduleId) },
                ) {
                    Icon(
                        painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.drag_handle),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .pointerInput(moduleId) {
                            detectDragGestures(
                                onDragStart = {
                                    draggingId = moduleId
                                    dragOffset = 0f
                                },
                                onDragEnd = {
                                    draggingId = null
                                    dragOffset = 0f
                                    onReorder(order)
                                },
                                onDragCancel = {
                                    draggingId = null
                                    dragOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount.y
                                    val current = order.indexOf(moduleId)
                                    if (dragOffset > rowHeightPx / 2 && current < order.size - 1) {
                                        order = order.toMutableList().apply {
                                            val swapped = this[current + 1]
                                            this[current + 1] = this[current]
                                            this[current] = swapped
                                        }
                                        dragOffset -= rowHeightPx
                                    } else if (dragOffset < -rowHeightPx / 2 && current > 0) {
                                        order = order.toMutableList().apply {
                                            val swapped = this[current - 1]
                                            this[current - 1] = this[current]
                                            this[current] = swapped
                                        }
                                        dragOffset += rowHeightPx
                                    }
                                },
                            )
                        },
                )
            }
        }
    }
}

@Composable
private fun ModuleItem(
    module: SpineModule,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(AppleTokens.CardCorner))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (module.isLossless) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "LOSSLESS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${module.author} v${module.version}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (module.description.isNotBlank()) {
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (module.tags.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    module.tags.take(4).forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }

        ModernSwitch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
        )
    }
}
