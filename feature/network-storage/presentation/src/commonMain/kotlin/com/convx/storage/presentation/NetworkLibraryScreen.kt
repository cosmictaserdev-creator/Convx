package com.convx.storage.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.storage.domain.*

@Composable
fun NetworkLibraryScreen(
    repository: NetworkLibraryRepository,
    onPlay: (PlaybackRequestTrack) -> Unit,
    modifier: Modifier = Modifier,
    embedded: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val holder = remember(repository, scope) { NetworkLibraryStateHolder(repository, scope) }
    val state by holder.state.collectAsState()
    DisposableEffect(holder) { onDispose { holder.close() } }
    LaunchedEffect(holder) { holder.load() }
    var protocol by remember { mutableStateOf(NetworkProtocol.WEBDAV) }
    var editing by remember { mutableStateOf<NetworkServer?>(null) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("") }
    var editorVisible by remember { mutableStateOf(false) }
    fun edit(server: NetworkServer?) {
        editing = server
        protocol = server?.protocol ?: NetworkProtocol.WEBDAV
        name = server?.name.orEmpty(); address = server?.address.orEmpty()
        username = server?.credentials?.username.orEmpty()
        password = server?.credentials?.password.orEmpty()
        domain = server?.credentials?.domain.orEmpty()
        editorVisible = true
    }
    val content = if (embedded) modifier.fillMaxWidth() else modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing).verticalScroll(rememberScrollState())
    Column(content.background(IosTheme.colors.systemGroupedBackground).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Network music", bold = true)
        onBack?.let { Button("Back to music", onClick = it) }
        Text("Connect to an SMB share or a WebDAV music folder.")
        state.message?.let { Text(it) }
        if (!state.ready && !state.busy) Button("Load saved sources", onClick = holder::load)
        if (state.busy) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Working...")
            Button("Cancel", onClick = holder::cancel)
        }
        Button("Add source", state.ready && !state.busy) { edit(null) }
        if (editorVisible) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NetworkProtocol.entries.forEach { type -> Button((if (protocol == type) "Selected: " else "") + type.name) { protocol = type } }
            }
            Field("Source name", name) { name = it }
            Field(if (protocol == NetworkProtocol.SMB) "SMB server and share" else "WebDAV folder URL", address) { address = it }
            Field("Username", username) { username = it }
            Field("Password", password, secret = true) { password = it }
            if (protocol == NetworkProtocol.SMB) Field("Domain (optional)", domain) { domain = it }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button("Save source", !state.busy) {
                    holder.save(protocol, name, address, SourceCredentials(username, password, domain), editing?.id) {
                        editorVisible = false; editing = null; password = ""
                    }
                }
                Button("Cancel edit", !state.busy) { editorVisible = false; editing = null; password = "" }
            }
        }
        state.sources.forEach { source ->
            Column(Modifier.fillMaxWidth().background(IosTheme.colors.secondarySystemGroupedBackground, RoundedCornerShape(12.dp)).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(source.name, bold = true)
                Text(source.protocol.name)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button("Browse", !state.busy) { holder.browse(source.id) }
                    Button("Scan music", !state.busy) { holder.scan(source.id) }
                    Button("Test", !state.busy) { holder.test(source) }
                    Button("Edit", !state.busy) { edit(source) }
                    Button("Remove", !state.busy) { holder.remove(source.id) }
                }
            }
        }
        state.selectedId?.let { id ->
            Text(state.sources.firstOrNull { it.id == id }?.name.orEmpty() + " / " + state.folder, bold = true)
            if (state.folder.isNotEmpty()) Button("Parent folder", !state.busy) { holder.browse(id, RemotePath.parent(state.folder)) }
            state.entries.filter { it.directory }.forEach { folder ->
                Button(folder.name, !state.busy) { holder.browse(id, folder.path) }
            }
            if (!state.busy && state.tracks.isEmpty()) Text("No music files in this view.")
            if (state.truncated) Text("The scan reached its limit. Browse individual folders to find more music.")
            state.tracks.forEach { track ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) { Text(track.title); track.artist?.let { Text(it) } }
                    Button("Play", !state.busy) { onPlay(track.playbackRequest()) }
                }
            }
        }
    }
}

@Composable private fun Text(value: String, bold: Boolean = false) {
    BasicText(value, style = TextStyle(color = IosTheme.colors.label, fontSize = 15.sp,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal))
}
@Composable private fun Button(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    BasicText(label, Modifier.background(IosTheme.colors.accent.copy(alpha = if (enabled) .15f else .05f), RoundedCornerShape(8.dp))
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(12.dp),
        style = TextStyle(color = if (enabled) IosTheme.colors.accent else IosTheme.colors.tertiaryLabel))
}
@Composable private fun Field(label: String, value: String, secret: Boolean = false, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label)
        BasicTextField(value, onValueChange, Modifier.fillMaxWidth().semantics { contentDescription = label }
            .border(1.dp, IosTheme.colors.separator, RoundedCornerShape(8.dp)).padding(12.dp), singleLine = true,
            textStyle = TextStyle(color = IosTheme.colors.label, fontSize = 16.sp), cursorBrush = SolidColor(IosTheme.colors.accent),
            visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None)
    }
}
