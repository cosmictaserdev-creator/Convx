/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.convx.music.ui.ios.compat.AlertDialog
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.EnabledModulesKey
import com.convx.music.constants.FetchedModulesKey
import com.convx.music.constants.ModuleSourcesKey
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.ModernSwitch
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberPreference
import com.convx.music.utils.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CancellationException
import com.music.spine.ModuleManager
import com.music.spine.SpineModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val moduleManager = remember {
        ModuleManager.cacheDir = context.filesDir.resolve("module-scripts")
        ModuleManager.shared
    }
    val sourceStatus = remember { mutableStateMapOf<String, String>() }
    val checkingSources = remember { mutableStateMapOf<String, Boolean>() }

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
        moduleManager.unloadModule(moduleId)
        // Disabling drops it from the priority list; enabling appends it to the end
        // (lowest priority until the user drags it up) rather than rebuilding from
        // a Set, which would throw away any order the user already set.
        saveEnabledOrder(
            if (moduleId in enabledOrder) enabledOrder - moduleId else enabledOrder + moduleId
        )
    }

    fun removeSource(url: String) {
        scope.launch {
            val removed = fetchedModules.filter { it.sourceUrl == url ||
                (it.type == com.music.spine.EclipseAddon.TYPE && it.download == com.music.spine.EclipseAddon.baseUrl(url)) }
                .mapTo(hashSetOf()) { it.id }
            context.dataStore.edit { prefs ->
                prefs[ModuleSourcesKey] = JSONArray(sourceUrls.filter { it != url }).toString()
                prefs[EnabledModulesKey] = JSONArray(enabledOrder.filterNot { it in removed }).toString()
                prefs[FetchedModulesKey] = json.encodeToString(
                    kotlinx.serialization.builtins.ListSerializer(SpineModule.serializer()), fetchedModules.filterNot { it.id in removed })
            }
            moduleManager.unloadAll()
        }
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.module_sources),
        pageColor = AppTheme.colorScheme.background,
    ) {

            item {
                Text(
                    text = "SOURCES",
                    style = AppTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(AppleTokens.CardCorner))
                        .background(AppTheme.colorScheme.surfaceContainer)
                ) {
                    sourceUrls.forEachIndexed { index, url ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 64.dp),
                                color = AppTheme.colorScheme.outlineVariant,
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
                                    style = AppTheme.typography.bodyLarge,
                                    color = AppTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = url,
                                    style = AppTheme.typography.bodySmall,
                                    color = AppTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                sourceStatus[url]?.let { status ->
                                    Text(status, style = AppTheme.typography.bodySmall,
                                        color = AppTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            TextButton(
                                enabled = checkingSources[url] != true,
                                onClick = {
                                    scope.launch {
                                        checkingSources[url] = true
                                        try {
                                            val result = moduleManager.fetchIndex(url, refresh = true)
                                            sourceStatus[url] = result.fold(
                                                onSuccess = {
                                                    if (it.isEmpty()) context.getString(R.string.module_source_empty)
                                                    else context.getString(R.string.module_source_available, it.size)
                                                },
                                                onFailure = { context.getString(R.string.module_check_failed) },
                                            )
                                        } finally {
                                            checkingSources[url] = false
                                        }
                                    }
                                },
                            ) { Text(stringResource(R.string.module_check)) }
                            IconButton(
                                onClick = { removeSource(url) },
                                onLongClick = { removeSource(url) },
                            ) {
                                SymbolIcon(symbol = sf("xmark"), contentDescription = stringResource(R.string.remove), size = 18.dp, tint = AppTheme.colorScheme.error)
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
                        SymbolIcon(symbol = sf("plus"), contentDescription = null, size = 20.dp, tint = AppTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.add_source),
                            style = AppTheme.typography.bodyLarge,
                            color = AppTheme.colorScheme.primary,
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
                                try {
                                    val results = coroutineScope {
                                        sourceUrls.map { url -> async { url to moduleManager.fetchIndex(url, refresh = true) } }.awaitAll()
                                    }
                                    val allModules = results.flatMap { (url, result) ->
                                        sourceStatus[url] = result.fold(
                                            onSuccess = { context.getString(R.string.module_source_available, it.size) },
                                            onFailure = { context.getString(R.string.module_check_failed) },
                                        )
                                        result.getOrNull().orEmpty()
                                    }
                                    if (results.any { it.second.isFailure }) {
                                        errorMessage = context.getString(R.string.module_refresh_partial)
                                    }
                                    val retained = if (results.any { it.second.isFailure }) fetchedModules else emptyList()
                                    onFetchedModulesJsonChange(json.encodeToString(
                                        kotlinx.serialization.builtins.ListSerializer(SpineModule.serializer()),
                                        (allModules + retained).distinctBy { it.id },
                                    ))
                                } finally {
                                    isLoading = false
                                }
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
                            Text(stringResource(R.string.module_refreshing))
                        } else {
                            Text(stringResource(R.string.fetch_modules))
                        }
                    }
                }
            }

            errorMessage?.let { message ->
                item {
                    Text(message, color = AppTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp))
                }
            }

            if (enabledOrder.isNotEmpty()) {
                item {
                    Text(
                        text = "ENABLED — DRAG TO SET PRIORITY",
                        style = AppTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = AppTheme.colorScheme.onSurfaceVariant,
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
                        style = AppTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = AppTheme.colorScheme.onSurfaceVariant,
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
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
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
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    errorMessage?.let { Text(it, color = AppTheme.colorScheme.error) }
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
                            val url = urlInput.trim().trimEnd('/')
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val modules = moduleManager.fetchIndex(url, refresh = true).getOrThrow()
                                    require(modules.isNotEmpty()) { "No addons or modules found at that URL" }
                                    context.dataStore.edit { prefs ->
                                        prefs[ModuleSourcesKey] = JSONArray((sourceUrls + url).distinct()).toString()
                                        prefs[FetchedModulesKey] = json.encodeToString(
                                            kotlinx.serialization.builtins.ListSerializer(SpineModule.serializer()),
                                            (modules + fetchedModules).distinctBy { it.id })
                                        prefs[EnabledModulesKey] = JSONArray((enabledOrder + modules.filterNot { m ->
                                            fetchedModules.any { it.id == m.id }
                                        }.map { it.id }).distinct()).toString()
                                    }
                                    showAddDialog = false
                                } catch (cancelled: CancellationException) {
                                    throw cancelled
                                } catch (error: Exception) {
                                    errorMessage = error.message ?: "Unable to add this source"
                                } finally { isLoading = false }
                            }
                        }
                    },
                    enabled = urlInput.isNotBlank() && !isLoading,
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
            .background(AppTheme.colorScheme.surfaceContainer)
    ) {
        order.forEachIndexed { index, moduleId ->
            val module = modulesById[moduleId] ?: return@forEachIndexed
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 64.dp),
                    color = AppTheme.colorScheme.outlineVariant,
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
                    style = AppTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colorScheme.primary,
                    modifier = Modifier.width(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                ModuleLogo(module)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.name,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = module.author,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(
                    onClick = { onDisable(moduleId) },
                    onLongClick = { onDisable(moduleId) },
                ) {
                    SymbolIcon(symbol = sf("xmark"), contentDescription = stringResource(R.string.remove), size = 18.dp, tint = AppTheme.colorScheme.error)
                }
                SymbolIcon(symbol = sf("line.3.horizontal"), contentDescription = null, size = 24.dp, modifier = Modifier
                        
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
                        }, tint = AppTheme.colorScheme.onSurfaceVariant)
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
            .background(AppTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ModuleLogo(module)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = module.name,
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (module.isLossless) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "LOSSLESS",
                        style = AppTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AppTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${module.author} v${module.version}",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
            if (module.description.isNotBlank()) {
                Text(
                    text = module.description,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
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
                            style = AppTheme.typography.labelSmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
