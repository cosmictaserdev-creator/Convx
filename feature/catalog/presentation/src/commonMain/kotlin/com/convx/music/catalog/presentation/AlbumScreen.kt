package com.convx.music.catalog.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.ui.ios.component.IosText
import com.convx.music.ui.ios.foundation.IosTheme

/**
 * One album page for every client. [onPlay] gets the queue to start (album order, or shuffled)
 * and the index to begin at; the host decides what playing means.
 */
@Composable
fun AlbumScreen(
    holder: AlbumStateHolder,
    onPlay: (queue: List<PlaybackRequestTrack>, startIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    onOpenAlbum: (CatalogEntry.Album) -> Unit = {},
    onBack: (() -> Unit)? = null,
    trackActions: @Composable RowScope.(CatalogEntry.Track) -> Unit = {},
    downloadsAvailable: Boolean = false,
    selectionActions: ((List<CatalogEntry.Track>) -> Unit)? = null,
) {
    val state by holder.state.collectAsState()
    DisposableEffect(holder) { onDispose { holder.close() } }
    LaunchedEffect(holder) { holder.load() }
    val playable = remember(state.visibleTracks) { state.visibleTracks.map { it.track }.filter { it.isPlayable } }
    Column(modifier.fillMaxSize()) {
        onBack?.let { Pill("Back", primary = false, modifier = Modifier.padding(16.dp), onClick = it) }
        if (state.confirmRemoveDownload) {
            Dialog(onDismissRequest = holder::dismissRemoveDownload) {
                Column(
                    Modifier.clip(RoundedCornerShape(14.dp)).background(IosTheme.colors.secondarySystemGroupedBackground).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    IosText("Remove downloads for this album?", color = IosTheme.colors.label)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Pill("Cancel", primary = false, onClick = holder::dismissRemoveDownload)
                        Pill("Remove", primary = true, onClick = holder::confirmRemoveDownload)
                    }
                }
            }
        }
        when {
            state.showError -> AlbumError(state.error, onRetry = holder::load)
            state.album == null -> IosText("Loading…", color = IosTheme.colors.secondaryLabel, modifier = Modifier.padding(24.dp))
            else -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = "header") { AlbumHeader(state.album!!, state) }
                item(key = "actions") {
                    if (state.selecting) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            IosText("${state.selectedIds.size} selected", color = IosTheme.colors.label, modifier = Modifier.weight(1f))
                            Pill(if (state.allVisibleSelected) "Select none" else "Select all", primary = false, onClick = holder::toggleSelectAll)
                            selectionActions?.let { actions ->
                                Pill("More", primary = false, enabled = state.selectedIds.isNotEmpty()) { actions(state.selectedTracks) }
                            }
                            Pill("Done", primary = true, onClick = holder::clearSelection)
                        }
                    } else {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Pill("Play", primary = true, enabled = playable.isNotEmpty()) { onPlay(playable.map { it.toRequest() }, 0) }
                            Pill("Shuffle", primary = false, enabled = playable.isNotEmpty()) { onPlay(playable.shuffled().map { it.toRequest() }, 0) }
                            if (downloadsAvailable) {
                                Pill(state.download.label(), primary = false, enabled = playable.isNotEmpty(), onClick = holder::onDownloadClick)
                            }
                        }
                    }
                }
                item(key = "tools") { AlbumTools(state, holder) }
                if (state.visibleTracks.isEmpty()) {
                    item(key = "none") { IosText("No songs match.", color = IosTheme.colors.secondaryLabel, modifier = Modifier.padding(16.dp)) }
                }
                items(state.listItems, key = { item ->
                    when (item) {
                        is AlbumListItem.DiscHeader -> "disc-${item.disc}"
                        is AlbumListItem.Track -> "${item.row.number}-${item.row.track.id.value}"
                    }
                }) { item ->
                    when (item) {
                        is AlbumListItem.DiscHeader ->
                            IosText("Disc ${item.disc}", color = IosTheme.colors.secondaryLabel, modifier = Modifier.padding(16.dp, 14.dp, 16.dp, 4.dp))
                        is AlbumListItem.Track -> {
                            val row = item.row
                            val index = playable.indexOf(row.track)
                            val selected = if (state.selecting) row.track.id.value in state.selectedIds else null
                            TrackRow(
                                row,
                                enabled = index >= 0 || state.selecting,
                                selected = selected,
                                actions = trackActions,
                                onLongClick = { if (!state.selecting) holder.startSelection(row.track.id.value) },
                            ) {
                                if (state.selecting) holder.toggleSelected(row.track.id.value) else onPlay(playable.map { it.toRequest() }, index)
                            }
                        }
                    }
                }
                if (state.related.isNotEmpty()) {
                    item(key = "related-title") {
                        IosText("Other versions", color = IosTheme.colors.label, modifier = Modifier.padding(16.dp, 20.dp, 16.dp, 6.dp))
                    }
                    items(state.related, key = { "related-${it.id.value}" }) { album ->
                        Column(Modifier.fillMaxWidth().clickable { onOpenAlbum(album) }.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            IosText(album.title, color = IosTheme.colors.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            IosText(
                                listOfNotNull(album.artists.joinToString { it.name }.ifBlank { null }, album.year).joinToString(" · "),
                                color = IosTheme.colors.secondaryLabel,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumHeader(album: CatalogEntry.Album, state: AlbumScreenState) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(220.dp).clip(RoundedCornerShape(14.dp)).background(IosTheme.colors.tertiarySystemFill)) {
            RemoteArtwork(album.artworkUrls.firstOrNull(), Modifier.fillMaxSize())
        }
        IosText(album.title, color = IosTheme.colors.label, maxLines = 2, overflow = TextOverflow.Ellipsis)
        val artists = album.artists.joinToString { it.name }
        if (artists.isNotBlank()) IosText(artists, color = IosTheme.colors.accent, maxLines = 1, overflow = TextOverflow.Ellipsis)
        IosText(
            listOfNotNull(album.year, "${state.tracks.size} songs", formatAlbumDuration(state.totalDurationMillis)).joinToString(" · "),
            color = IosTheme.colors.secondaryLabel,
        )
    }
}

@Composable
private fun AlbumTools(state: AlbumScreenState, holder: AlbumStateHolder) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicTextField(
            value = state.query,
            onValueChange = holder::setQuery,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search this album" }
                .border(1.dp, IosTheme.colors.separator, RoundedCornerShape(8.dp)).padding(10.dp),
            singleLine = true,
            textStyle = TextStyle(color = IosTheme.colors.label, fontSize = 15.sp),
            cursorBrush = SolidColor(IosTheme.colors.accent),
            decorationBox = { inner ->
                if (state.query.isEmpty()) IosText("Search this album", color = IosTheme.colors.placeholderText)
                inner()
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill(if (state.hideExplicit) "Explicit hidden" else "Hide explicit", primary = state.hideExplicit) { holder.setHideExplicit(!state.hideExplicit) }
            Pill(if (state.hideVideos) "Videos hidden" else "Hide videos", primary = state.hideVideos) { holder.setHideVideos(!state.hideVideos) }
            Pill("Sort: " + state.sort.label(), primary = false) { holder.setSort(state.sort.next()) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrackRow(
    row: AlbumTrackRow,
    enabled: Boolean,
    selected: Boolean?,
    actions: @Composable RowScope.(CatalogEntry.Track) -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val track = row.track
    val color = if (enabled) IosTheme.colors.label else IosTheme.colors.tertiaryLabel
    Row(
        Modifier.fillMaxWidth()
            .background(if (selected == true) IosTheme.colors.accent.copy(alpha = .12f) else androidx.compose.ui.graphics.Color.Transparent)
            .combinedClickable(enabled = enabled, role = Role.Button, onLongClick = onLongClick, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IosText(
            if (selected == null) row.number.toString() else if (selected) "●" else "○",
            color = if (selected == true) IosTheme.colors.accent else IosTheme.colors.tertiaryLabel,
            modifier = Modifier.width(26.dp),
        )
        Column(Modifier.weight(1f)) {
            IosText(track.title, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val detail = listOfNotNull(
                "Explicit".takeIf { track.isExplicit },
                track.artists.joinToString { it.name }.ifBlank { null },
            ).joinToString(" · ")
            if (detail.isNotBlank()) IosText(detail, color = IosTheme.colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        track.durationMillis?.let { IosText(formatTrackDuration(it), color = IosTheme.colors.secondaryLabel) }
        if (selected == null) actions(track)
    }
}

@Composable
private fun AlbumError(error: CatalogError?, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IosText(error.message(), color = IosTheme.colors.label)
        Pill("Retry", primary = true, onClick = onRetry)
    }
}

@Composable
private fun Pill(label: String, primary: Boolean, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    val accent = IosTheme.colors.accent
    IosText(
        label,
        color = when {
            !enabled -> IosTheme.colors.tertiaryLabel
            primary -> IosTheme.colors.systemBackground
            else -> accent
        },
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (primary && enabled) accent else accent.copy(alpha = if (enabled) .15f else .05f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    )
}

private fun CatalogEntry.Track.toRequest() = PlaybackRequestTrack(
    providerId = id.providerId,
    id = id.value,
    title = title,
    artists = artists.map { it.name },
    durationMs = durationMillis,
    artworkUrl = artworkUrls.firstOrNull(),
)

private fun AlbumSort.label() = when (this) {
    AlbumSort.ALBUM_ORDER -> "Album"
    AlbumSort.TITLE -> "Title"
    AlbumSort.DURATION -> "Length"
}

private fun AlbumDownloadState.label() = when (this) {
    AlbumDownloadState.NONE -> "Download"
    AlbumDownloadState.DOWNLOADING -> "Cancel download"
    AlbumDownloadState.DOWNLOADED -> "Downloaded"
}

private fun AlbumSort.next() = AlbumSort.entries[(ordinal + 1) % AlbumSort.entries.size]

private fun CatalogError?.message() = when (this) {
    CatalogError.Offline -> "You're offline. Check your connection and try again."
    CatalogError.NotFound -> "This album could not be found."
    is CatalogError.RateLimited -> "Too many requests. Try again in a moment."
    CatalogError.Unauthorized -> "Sign in to open this album."
    else -> "This album could not be loaded."
}

/** `3:05` for a track, `1:02:03` past an hour. */
internal fun formatTrackDuration(millis: Long): String {
    val total = millis / 1_000
    val seconds = (total % 60).toString().padStart(2, '0')
    val minutes = total / 60 % 60
    val hours = total / 3_600
    return if (hours > 0) "$hours:${minutes.toString().padStart(2, '0')}:$seconds" else "$minutes:$seconds"
}

/** `42 min` or `1 hr 5 min` for the album total; null when no track has a length. */
internal fun formatAlbumDuration(millis: Long): String? {
    val minutes = millis / 60_000
    if (minutes <= 0) return null
    return if (minutes >= 60) "${minutes / 60} hr ${minutes % 60} min" else "$minutes min"
}
