package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.SmallDialog as NucleusDialog
import dev.nucleusframework.macoscompose.components.TextField as NucleusTextField

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.convx.music.catalog.presentation.RemoteArtwork
import com.convx.music.library.PlaylistSummary
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject
import java.awt.EventQueue
import java.awt.KeyboardFocusManager
import java.io.File
import javax.swing.JFileChooser

@Composable
internal fun DesktopLocalLibraryScreen(holder: DesktopLocalLibraryStateHolder, modifier: Modifier = Modifier, showSearchHeader: Boolean = true, topInset: androidx.compose.ui.unit.Dp = 0.dp) {
    val palette = desktopPalette()
    val page by holder.state.collectAsState()
    val media: DesktopMediaLibrary = koinInject()
    val downloads: DesktopDownloadManager = koinInject()
    val player: DesktopMusicController = koinInject()
    val now by remember(player) { player.playbackSnapshot.map { DesktopLocalNow(it.track?.id, it.isPlaying) }.distinctUntilChanged() }.collectAsState(DesktopLocalNow())
    var playlistName by remember { mutableStateOf<String?>(null) }
    var renaming by remember { mutableStateOf(false) }
    var addSong by remember { mutableStateOf<DesktopLibraryTrack?>(null) }
    var deletingPlaylist by remember { mutableStateOf(false) }
    var deletingDownload by remember { mutableStateOf<DesktopDownload?>(null) }
    val collection = (page.route as? DesktopLocalRoute.Collection)?.destination
    val playlistId = (page.route as? DesktopLocalRoute.Playlist)?.id
    val detail = page.route !is DesktopLocalRoute.Collection
    fun play(index: Int, shuffle: Boolean = false) {
        val songs = page.songs.map { it.track.request() }
        if (songs.isEmpty()) return
        if (shuffle) {
            player.setShuffleEnabled(true)
            player.playQueue(songs, songs.indices.random())
        }
        else player.playQueue(songs, index.coerceIn(songs.indices))
    }
    val reduced = LocalDesktopReducedMotion.current
    androidx.compose.animation.AnimatedContent(targetState = page, contentKey = { it.route },
        transitionSpec = {
            val direction = if (targetState.canGoBack) 1 else -1
            (androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(if (reduced) 0 else 180)) +
                androidx.compose.animation.slideInHorizontally(androidx.compose.animation.core.tween(if (reduced) 0 else 220)) { if (reduced) 0 else direction * 24 })
                .togetherWith(androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(if (reduced) 0 else 100))).using(null)
        }, modifier = modifier, label = "Local library navigation") { page ->
    val list = rememberLazyListState()
    val collection = (page.route as? DesktopLocalRoute.Collection)?.destination
    val detail = page.route !is DesktopLocalRoute.Collection
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = ((maxWidth - 80.dp) / 230.dp).toInt().coerceAtLeast(1)
        LazyColumn(state = list, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(40.dp, topInset + 24.dp, 40.dp, 112.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (showSearchHeader && !detail) item(key = "header") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(page.title, modifier = Modifier.weight(1f), fontSize = 32.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (collection == DesktopDestination.PLAYLISTS) MediaButton("New playlist") { playlistName = ""; renaming = false }
                    if (collection == DesktopDestination.LOCAL_MUSIC || collection == DesktopDestination.HOME) MediaButton("Add music folder") { chooseMediaFolder(media::addFolder) }
                    if (collection == DesktopDestination.DOWNLOADS) MediaButton("Choose folder") { chooseMediaFolder(downloads::setDirectory) }
                }
            }
            if (showSearchHeader && collection != DesktopDestination.DOWNLOADS) item(key = "search") {
                SearchField(page.query, holder::search, Modifier.fillMaxWidth())
            }
            if (detail) item(key = "detail") {
                Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 30.dp), horizontalArrangement = Arrangement.spacedBy(34.dp), verticalAlignment = Alignment.CenterVertically) {
                    LocalCover(page.songs.firstNotNullOfOrNull { it.track.artworkPath }, Modifier.size(240.dp), circle = page.route is DesktopLocalRoute.Artist)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(page.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Text("${page.songs.size} songs", color = palette.muted, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MediaButton("Play", filled = true) { play(0) }
                            MediaButton("Shuffle") { play(0, true) }
                        }
                        if (playlistId != null) Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MediaButton("Rename") { playlistName = page.title; renaming = true }
                            MediaButton("Delete") { deletingPlaylist = true }
                        }
                    }
                }
            }
            if (page.media.scanning) item(key = "scan") {
                Text("Scanning music · ${page.media.scannedFiles} / ${page.media.totalFiles}", color = palette.muted, fontSize = 13.sp)
            }
            (page.message ?: page.media.message)?.let { message -> item(key = "message") { Text(message, color = palette.muted, fontSize = 13.sp) } }
            if (collection == DesktopDestination.LOCAL_MUSIC) {
                item(key = "folders-title") { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Music folders", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    MediaButton("Rescan", enabled = !page.media.scanning, onClick = media::rescan)
                } }
                itemsIndexed(page.media.index.folders, key = { _, folder -> "folder:$folder" }) { _, folder ->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .desktopInteractive(scale = 1f, hoverColor = palette.foreground.copy(alpha = .03f)) { holder.openFolder(folder) }
                        .padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DesktopGlyph(DesktopGlyphKind.FOLDER, palette.muted, 22.dp)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(File(folder).name.ifBlank { folder }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(folder, color = palette.muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        MediaButton("Remove") { media.removeFolder(folder) }
                    }
                }
                item(key = "tracks-title") { Text("Songs on this device", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
            }
            if (collection == DesktopDestination.DOWNLOADS) {
                item(key = "download-actions") { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MediaButton("Resume all", onClick = downloads::resumeAll)
                    MediaButton("Pause all", onClick = downloads::pauseAll)
                    val folder = page.media.index.downloadDirectory ?: File(System.getProperty("user.home"), "Music/Convx/Downloads").path
                    Text(folder, color = palette.muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                } }
                if (page.media.index.downloads.isEmpty()) item(key = "empty-downloads") {
                    Text("Choose Download from a song?s menu to listen offline.", color = palette.muted, fontSize = 15.sp)
                }
                itemsIndexed(page.media.index.downloads, key = { _, item -> DesktopDownloadManager.key(item.track) }) { _, download ->
                    DownloadRow(download, { downloads.pause(DesktopDownloadManager.key(download.track)) },
                        { downloads.resume(DesktopDownloadManager.key(download.track)) }, { deletingDownload = download },
                        { player.playTrack(download.track) })
                }
            } else if (collection == DesktopDestination.HOME && page.query.isBlank()) {
                val tracks = page.media.index.tracks
                val shelves = listOf("Recently Added" to tracks.sortedByDescending { it.addedAt }.take(20),
                    "Listen Again" to tracks.filter { it.lastPlayedAt != null }.sortedByDescending { it.lastPlayedAt }.take(20),
                    "Your Favorites" to tracks.filter { it.favorite }.take(20),
                    "Most Played" to tracks.filter { it.playCount > 0 }.sortedByDescending { it.playCount }.take(20))
                shelves.filter { it.second.isNotEmpty() }.forEach { (title, songs) -> item(key = "shelf:$title") {
                    Column(Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                            itemsIndexed(songs, key = { _, track -> track.id }) { index, track ->
                                LocalArtworkCard(track.title, track.artists.joinToString(), track.artworkPath) { player.playQueue(songs.map { it.request() }, index) }
                            }
                        }
                    }
                } }
                if (tracks.isEmpty()) item(key = "empty-home") { EmptyMusicLibrary(media) }
            } else if (collection in listOf(DesktopDestination.ALBUMS, DesktopDestination.ARTISTS)) {
                item(key = "groups") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        itemsIndexed(page.groups, key = { _, group -> group.key }) { _, group -> LocalArtworkCard(group.title, group.subtitle, group.artwork,
                            circle = collection == DesktopDestination.ARTISTS) {
                            if (collection == DesktopDestination.ARTISTS) holder.openArtist(group.key) else holder.openAlbum(group.key)
                        } }
                    }
                }
                if (page.groups.isEmpty()) item(key = "empty-groups") { EmptyMusicLibrary(media) }
            } else if (collection == DesktopDestination.PLAYLISTS) {
                item(key = "playlists") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        itemsIndexed(page.playlists, key = { _, playlist -> playlist.id }) { _, playlist -> LocalArtworkCard(playlist.name, "${playlist.songCount} songs", playlist.artworkUrls.firstOrNull()) { holder.openPlaylist(playlist.id) } }
                    }
                }
                if (page.playlists.isEmpty()) item(key = "empty-playlists") { Text("Create a playlist, then add songs from your local library.", color = palette.muted, fontSize = 15.sp) }
            } else {
                if (page.songs.isEmpty()) item(key = "empty-songs") {
                    if (detail) Text("No songs here yet. Add songs from your local library.", color = palette.muted, fontSize = 14.sp) else EmptyMusicLibrary(media)
                }
                itemsIndexed(page.songs, key = { index, song -> "${song.playlistEntry?.id ?: song.track.id}:$index" }) { index, song ->
                    LocalSongRow(song, index, now, { play(index) }, { media.setFavorite(song.track.id, !song.track.favorite) }, { addSong = song.track },
                        onRemove = song.playlistEntry?.let { { holder.removeSong(it) } },
                        onMoveUp = if (playlistId != null && page.query.isBlank() && index > 0) ({ holder.moveSong(playlistId, song.position, page.songs[index - 1].position) }) else null,
                        onMoveDown = if (playlistId != null && page.query.isBlank() && index < page.songs.lastIndex) ({ holder.moveSong(playlistId, song.position, page.songs[index + 1].position) }) else null)
                }
            }
        }
        VerticalScrollbar(rememberScrollbarAdapter(list), Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 8.dp),
            style = ScrollbarStyle(16.dp, 6.dp, RoundedCornerShape(3.dp), 120, palette.foreground.copy(alpha = 0.16f), palette.foreground.copy(alpha = 0.34f)))
    }
    }
    playlistName?.let { initial -> MediaTextDialog(if (renaming) "Rename playlist" else "New playlist", initial, { playlistName = null }) { name ->
        if (renaming && playlistId != null) holder.renamePlaylist(playlistId, name) else holder.createPlaylist(name)
        playlistName = null
    } }
    addSong?.let { song -> LocalPlaylistPicker(page.playlists, song.title, { addSong = null }) { id -> holder.addToPlaylist(id, song.id); addSong = null } }
    if (deletingPlaylist && playlistId != null) MediaConfirmDialog("Delete ${page.title}?", "This removes the playlist. Your music files stay on disk.", { deletingPlaylist = false }) {
        holder.deletePlaylist(playlistId); deletingPlaylist = false
    }
    deletingDownload?.let { download -> MediaConfirmDialog("Delete download?", "The offline copy of ${download.track.title} will be removed from disk.", { deletingDownload = null }) {
        downloads.remove(DesktopDownloadManager.key(download.track), deleteFile = true); deletingDownload = null
    } }
}

private data class DesktopLocalNow(val id: String? = null, val playing: Boolean = false)

@Composable
private fun EmptyMusicLibrary(media: DesktopMediaLibrary) {
    Column(Modifier.padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Bring your music with you.", fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Add a music folder or download songs for offline listening.", color = DesktopMuted, fontSize = 15.sp)
        MediaButton("Add music folder", filled = true) { chooseMediaFolder(media::addFolder) }
    }
}

@Composable
private fun LocalSongRow(song: DesktopLocalSong, index: Int, now: DesktopLocalNow, onPlay: () -> Unit, onFavorite: () -> Unit, onPlaylist: () -> Unit,
    onRemove: (() -> Unit)?, onMoveUp: (() -> Unit)?, onMoveDown: (() -> Unit)?) {
    val palette = desktopPalette()
    val track = song.track
    val selected = track.request().id == now.id
    val player: DesktopMusicController = koinInject()
    var menu by remember(track.id) { mutableStateOf(false) }
    Box {
    Row(Modifier.fillMaxWidth().height(54.dp).drawBehind { drawLine(palette.separator, Offset(0f, size.height), Offset(size.width, size.height), .5.dp.toPx()) }.clip(RoundedCornerShape(6.dp)).background(if (selected) palette.foreground.copy(alpha = 0.06f) else Color.Transparent)
        .desktopInteractive(scale = 1f, hoverColor = palette.foreground.copy(alpha = .03f), onContextMenu = { menu = true }, onClick = onPlay).padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text((index + 1).toString(), color = palette.muted, fontSize = 12.sp, modifier = Modifier.width(25.dp))
        LocalCover(track.artworkPath, Modifier.size(40.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(track.title, color = if (selected) palette.accent else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artists.joinToString().ifBlank { "Unknown Artist" }, color = palette.muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(track.durationMs?.let { "${it / 60000}:${((it / 1000) % 60).toString().padStart(2, '0')}" } ?: "—", color = palette.muted, fontSize = 12.sp, modifier = Modifier.width(40.dp))
        PlayerGlyphButton(if (track.favorite) DesktopGlyphKind.FAVORITED else DesktopGlyphKind.FAVORITE, "Favorite song", track.favorite, 30, 16, onClick = onFavorite)
        PlayerGlyphButton(DesktopGlyphKind.ADD_TO_PLAYLIST, "Add to playlist", size = 30, icon = 17, onClick = onPlaylist)
        PlayerGlyphButton(DesktopGlyphKind.MORE, "Options for ${track.title}", size = 28, icon = 16, onClick = { menu = true })
        if (onRemove != null) {
            PlayerGlyphButton(DesktopGlyphKind.MOVE_UP, "Move up", size = 28, icon = 16, enabled = onMoveUp != null, onClick = onMoveUp ?: {})
            PlayerGlyphButton(DesktopGlyphKind.MOVE_DOWN, "Move down", size = 28, icon = 16, enabled = onMoveDown != null, onClick = onMoveDown ?: {})
            PlayerGlyphButton(DesktopGlyphKind.REMOVE, "Remove from playlist", size = 28, icon = 16, onClick = onRemove)
        }
    }
    dev.nucleusframework.macoscompose.components.DropdownMenu(menu, { menu = false }) {
        dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { menu = false; onPlay() }) { Text("Play") }
        dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { menu = false; player.appendQueue(listOf(track.request())) }) { Text("Add to Queue") }
        dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { menu = false; onFavorite() }) { Text(if (track.favorite) "Remove from Favourites" else "Add to Favourites") }
        dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { menu = false; onPlaylist() }) { Text("Add to Playlist…") }
        if (onRemove != null) dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { menu = false; onRemove() }) { Text("Remove from Playlist") }
    }
    }
}

@Composable
private fun LocalArtworkCard(title: String, subtitle: String, artwork: String?, circle: Boolean = false, onClick: () -> Unit) {
    Column(Modifier.width(212.dp).desktopInteractive(scale = .985f, onClick = onClick), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LocalCover(artwork, Modifier.size(212.dp), circle)
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(subtitle, color = DesktopMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun LocalCover(path: String?, modifier: Modifier, circle: Boolean = false) {
    val palette = desktopPalette()
    val url = path?.let { if (it.startsWith("http:") || it.startsWith("https:") || it.startsWith("file:")) it else File(it).toURI().toString() }
    Box(modifier.clip(if (circle) CircleShape else RoundedCornerShape(12.dp)).background(palette.surface), contentAlignment = Alignment.Center) {
        DesktopGlyph(DesktopGlyphKind.MUSIC_NOTE, palette.muted.copy(alpha = 0.35f), 24.dp)
        RemoteArtwork(url, Modifier.fillMaxSize())
    }
}

@Composable
private fun DownloadRow(download: DesktopDownload, onPause: () -> Unit, onResume: () -> Unit, onDelete: () -> Unit, onPlay: () -> Unit) {
    val palette = desktopPalette()
    val progress by androidx.compose.animation.core.animateFloatAsState(
        (download.receivedBytes.toFloat() / (download.totalBytes ?: Long.MAX_VALUE).coerceAtLeast(1)).coerceIn(0f, 1f),
        androidx.compose.animation.core.tween(if (LocalDesktopReducedMotion.current) 0 else 240), label = "Download progress")
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp).background(palette.surface, RoundedCornerShape(10.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LocalCover(download.track.artworkUrl, Modifier.size(48.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(download.track.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(download.message ?: "${download.status.name.lowercase().replaceFirstChar { it.titlecase() }} · ${download.receivedBytes / (1024 * 1024)} MB" +
                    (download.totalBytes?.let { " / ${it / (1024 * 1024)} MB" } ?: ""), color = palette.muted, fontSize = 12.sp)
            }
            when (download.status) {
                DesktopDownloadStatus.COMPLETE -> MediaButton("Play", onClick = onPlay)
                DesktopDownloadStatus.DOWNLOADING, DesktopDownloadStatus.QUEUED -> MediaButton("Pause", onClick = onPause)
                else -> MediaButton("Resume", onClick = onResume)
            }
            MediaButton("Delete", onClick = onDelete)
        }
        if (download.status != DesktopDownloadStatus.COMPLETE) Box(Modifier.fillMaxWidth().height(3.dp).background(palette.foreground.copy(alpha = 0.08f), CircleShape)) {
            Box(Modifier.fillMaxWidth(progress)
                .fillMaxHeight().background(palette.accent, CircleShape))
        }
    }
}

@Composable
internal fun MediaButton(label: String, enabled: Boolean = true, filled: Boolean = false, onClick: () -> Unit) {
    PushButton(
        text = label.replace("?  ", ""), onClick = onClick, enabled = enabled,
        style = if (filled) PushButtonStyle.Default
            else PushButtonStyle.Neutral,
    )
}

@Composable
internal fun MediaTextDialog(title: String, initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var value by remember(initial) { mutableStateOf(initial) }
    NucleusDialog(
        visible = true, onDismissRequest = onDismiss, title = title, confirmText = "Save",
        onConfirm = { if (value.isNotBlank()) onSave(value.trim()) },
    ) {
        NucleusTextField(value, { value = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Playlist name") })
    }
}

@Composable
private fun LocalPlaylistPicker(playlists: List<PlaylistSummary>, song: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val palette = desktopPalette()
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.width(400.dp).heightIn(max = 500.dp).background(palette.surface, RoundedCornerShape(18.dp)).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Add to playlist", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text(song, color = palette.muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (playlists.isEmpty()) Text("Create a local playlist from the Playlists page first.", color = palette.muted, fontSize = 14.sp)
            LazyColumn(Modifier.weight(1f, fill = false)) { itemsIndexed(playlists, key = { _, item -> item.id }) { _, item ->
                Text(item.name, Modifier.fillMaxWidth().clickable { onSelect(item.id) }.padding(vertical = 12.dp), fontSize = 15.sp)
            } }
            MediaButton("Cancel", onClick = onDismiss)
        }
    }
}

@Composable
private fun MediaConfirmDialog(title: String, message: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    NucleusDialog(
        visible = true, onDismissRequest = onDismiss, title = title, confirmText = "Cancel", onConfirm = onDismiss,
        cancelText = null, destructiveText = "Delete", onDestructive = onConfirm,
    ) { Text(message, fontSize = 14.sp) }
}

internal fun chooseMediaFolder(onSelected: (File) -> Unit) {
    EventQueue.invokeLater {
        val chooser = object : JFileChooser() {
            override fun createDialog(parent: java.awt.Component?): javax.swing.JDialog = super.createDialog(parent).apply {
                // The Tao window has no AWT owner; keep the picker in front of it.
                if (parent == null) { isAlwaysOnTop = true; setLocationRelativeTo(null) }
            }
        }.apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY; dialogTitle = "Choose music folder" }
        if (chooser.showOpenDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow) == JFileChooser.APPROVE_OPTION) onSelected(chooser.selectedFile)
    }
}
