package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import dev.nucleusframework.macoscompose.components.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun DesktopAudioOutputDialog(
    settings: PlaybackSettingsStateHolder,
    loadDevices: () -> List<DesktopAudioDevice> = DesktopPcmOutputs::devices,
    onDismiss: () -> Unit,
) {
    val preferences by settings.state.collectAsState()
    val saveError by settings.errorMessage.collectAsState()
    var refresh by remember { mutableIntStateOf(0) }
    var devices by remember { mutableStateOf<List<DesktopAudioDevice>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(refresh) {
        loading = true
        error = null
        try { devices = withContext(Dispatchers.IO) { loadDevices() } }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { error = "Couldn't load audio devices. You can still use the system default." }
        finally { loading = false }
    }
    val exclusiveSupported = remember { System.getProperty("os.name").startsWith("Windows", true) }
    SmallDialog(visible = true, onDismissRequest = onDismiss, title = "Audio output", confirmText = "Done",
        onConfirm = onDismiss, cancelText = null) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Exclusive output", Modifier.weight(1f), fontSize = 14.sp)
            Switch(preferences.bitPerfectEnabled, settings::setBitPerfectEnabled,
                enabled = exclusiveSupported || preferences.bitPerfectEnabled,
                modifier = Modifier.semantics { contentDescription = "Exclusive output" })
        }
        Text(if (exclusiveSupported) "Exclusive output bypasses volume, equalizer and transitions. The device must support the song's format."
            else "Exclusive output is available on Windows. This platform uses the system mixer.", color = DesktopMuted, fontSize = 12.sp)
        Column(Modifier.fillMaxWidth().heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushButton("System default", { settings.setAudioOutputDevice(null) }, selected = preferences.audioOutputDeviceId == null,
                style = PushButtonStyle.Neutral, modifier = Modifier.fillMaxWidth())
            devices.forEach { device ->
                key(device.id) {
                    PushButton(device.name, { settings.setAudioOutputDevice(device.id) }, selected = preferences.audioOutputDeviceId == device.id,
                        style = PushButtonStyle.Neutral, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        if (!loading && error == null && preferences.audioOutputDeviceId != null && devices.none { it.id == preferences.audioOutputDeviceId }) {
            Text("Your selected device is unavailable. Choose another device or the system default.", color = DesktopMuted, fontSize = 12.sp)
        }
        error?.let { Text(it, color = DesktopMuted, fontSize = 12.sp) }
        saveError?.let { Text(it, color = DesktopAccent, fontSize = 12.sp) }
        PushButton(if (loading) "Loading devices…" else "Refresh devices", { refresh++ }, enabled = !loading, style = PushButtonStyle.Neutral)
    }
}
