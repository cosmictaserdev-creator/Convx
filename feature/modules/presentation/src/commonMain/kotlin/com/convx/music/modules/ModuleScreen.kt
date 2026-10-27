package com.convx.music.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.playback.PlaybackRequestTrack
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.spine.ModuleManifest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun ModulesScreen(
    repository: ModuleRepository,
    onPlay: (PlaybackRequestTrack) -> Unit,
    modifier: Modifier = Modifier,
    embedded: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    val prefs by repository.preferences.collectAsState()
    val scope = rememberCoroutineScope()
    var ready by remember { mutableStateOf(false) }
    var restoring by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var source by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var search by remember { mutableStateOf<ModuleSearch?>(null) }
    var editingKey by remember { mutableStateOf<String?>(null) }
    var manifest by remember { mutableStateOf<ModuleManifest?>(null) }
    var values by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    fun action(block: suspend () -> Unit) {
        if (busy || !ready) return
        busy = true
        error = null
        scope.launch {
            try { block() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { error = failure.message ?: "The request failed. Try again." }
            finally { busy = false }
        }
    }

    suspend fun restoreSavedModules() {
        restoring = true
        error = null
        try { repository.restore(); ready = true }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { error = "Could not read saved modules. Your saved data has been preserved." }
        finally { restoring = false }
    }
    LaunchedEffect(repository) { restoreSavedModules() }
    // Remove stale search results and stream links after settings, priority or source changes.
    LaunchedEffect(prefs) { search = null }

    val contentModifier = if (embedded) modifier.fillMaxWidth() else modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing).verticalScroll(rememberScrollState())
    Column(contentModifier.background(if (embedded) Color.Transparent else IosTheme.colors.systemGroupedBackground).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ModuleText("Modules & add-ons", style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold))
        onBack?.let { Action("Back to music", onClick = it) }
        ModuleText("Add a source, then enable the modules you want to use.")
        error?.let { ModuleText(it, style = TextStyle(color = IosTheme.colors.systemRed)) }
        if (busy || restoring) ModuleText(if (busy) "Working…" else "Loading saved modules…")
        if (!ready && !restoring) Action("Retry loading", !busy) { scope.launch { restoreSavedModules() } }
        Field("Module index or add-on manifest URL", source, { source = it })
        Action("Add source", ready && !busy && source.isNotBlank()) {
            action { repository.addSource(source); source = "" }
        }
        prefs.sources.forEach { url ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Token-bearing URLs remain in the editor/store, not in source labels.
                ModuleText(runCatching { io.ktor.http.Url(url).host }.getOrDefault("Source"))
                ModuleText(prefs.modules.filter { it.sourceUrl == url }.joinToString { it.name })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Action("Refresh", !busy) { action { repository.refreshSource(url) } }
                    Action("Remove", !busy) { action { repository.removeSource(url); editingKey = null; manifest = null } }
                }
            }
        }
        val enabled = prefs.enabled.mapNotNull(prefs::module)
        val disabled = prefs.modules.filter { prefs.key(it) !in prefs.enabled }
        (enabled + disabled).forEach { module ->
            val key = prefs.key(module)
            val priority = prefs.enabled.indexOf(key)
            Column(Modifier.fillMaxWidth().background(IosTheme.colors.secondarySystemGroupedBackground, RoundedCornerShape(12.dp)).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModuleText(module.name + if (priority >= 0) " · Priority ${priority + 1}" else "",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp))
                ModuleText(listOf(module.type, module.version).filter(String::isNotBlank).joinToString(" · "))
                if (module.description.isNotBlank()) ModuleText(module.description)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Action(if (priority >= 0) "Disable" else "Enable", !busy) { action { repository.setEnabled(key, priority < 0) } }
                    Action("Settings", !busy) {
                        action {
                            val loaded = repository.manifest(key)
                            values = loaded.defaults + prefs.settings[key].orEmpty()
                            manifest = loaded
                            editingKey = key
                        }
                    }
                }
                if (priority >= 0) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Action("Move up", !busy && priority > 0) { action { repository.move(key, -1) } }
                    Action("Move down", !busy && priority < enabled.lastIndex) { action { repository.move(key, 1) } }
                }
                if (editingKey == key) {
                    val settings = manifest?.settings.orEmpty()
                    if (settings.isEmpty()) ModuleText("This module has no settings.")
                    settings.forEach { (name, setting) ->
                        val label = setting.label.ifBlank { name }
                        if (setting.description.isNotBlank()) ModuleText(setting.description)
                        when {
                            setting.options.isNotEmpty() -> {
                                ModuleText(label)
                                setting.options.forEach { option ->
                                    Action((if (values[name] == option.value) "✓ " else "") + option.label.ifBlank { option.value }, !busy) {
                                        values = values + (name to option.value)
                                    }
                                }
                            }
                            setting.type.lowercase() in setOf("boolean", "switch", "toggle") -> Action(
                                "$label: ${values[name] == "true"}", !busy,
                            ) { values = values + (name to (values[name] != "true").toString()) }
                            else -> Field(label, values[name].orEmpty(), { values = values + (name to it) },
                                setting.type.lowercase() in setOf("password", "secret"))
                        }
                    }
                    Action("Save settings", !busy) { action { repository.saveSettings(key, values); editingKey = null; manifest = null } }
                }
            }
        }
        Field("Search tracks", query, { query = it })
        Action("Search enabled modules", !busy && prefs.enabled.isNotEmpty() && query.isNotBlank()) {
            action { search = repository.search(query) }
        }
        search?.let { result ->
            if (result.tracks.isEmpty()) ModuleText("No tracks found.")
            result.failures.forEach { (key, message) -> ModuleText("${prefs.module(key)?.name ?: "Source"}: $message") }
            result.tracks.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ModuleText("${item.track.title} — ${item.track.artist}")
                    ModuleText(item.moduleName)
                    Action("Play", !busy) { onPlay(item.playbackRequest()) }
                }
            }
        }
    }
}

@Composable
private fun Action(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    ModuleText(label, Modifier.background(if (enabled) IosTheme.colors.accent.copy(alpha = 0.14f) else IosTheme.colors.systemFill, RoundedCornerShape(8.dp))
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(horizontal = 12.dp, vertical = 12.dp),
        style = TextStyle(color = if (enabled) IosTheme.colors.accent else IosTheme.colors.tertiaryLabel))
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit, secret: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ModuleText(label)
        BasicTextField(value, onValueChange, Modifier.fillMaxWidth().semantics { contentDescription = label }
            .background(IosTheme.colors.secondarySystemGroupedBackground, RoundedCornerShape(8.dp))
            .border(1.dp, IosTheme.colors.separator, RoundedCornerShape(8.dp)).padding(12.dp), singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = IosTheme.colors.label),
            cursorBrush = SolidColor(IosTheme.colors.accent),
            visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None)
    }
}

@Composable
private fun ModuleText(text: String, modifier: Modifier = Modifier, style: TextStyle = TextStyle.Default) {
    BasicText(text, modifier, style = if (style.color == Color.Unspecified) style.copy(color = IosTheme.colors.label) else style)
}
