package com.convx.music.client.desktop

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.presentation.RemoteArtwork
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import org.koin.compose.koinInject
import zone.ien.hig.CupertinoDropdownMenu
import zone.ien.hig.ExperimentalCupertinoApi
import zone.ien.hig.MenuItem
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
internal fun DesktopArtistWordmark(name: String, artwork: DesktopDetailArtwork, color: Color, modifier: Modifier = Modifier) {
    val url = artwork.artist?.logo?.sized(800, png = true) ?: artwork.signatureUrl
    var loaded by remember(url) { mutableStateOf(false) }
    Box(modifier, contentAlignment = Alignment.Center) {
        if (!loaded) Text(name, color = color, fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.5).sp,
            maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        if (url != null) AsyncImage(url, null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit,
            colorFilter = if (artwork.artist?.logo == null) ColorFilter.tint(color) else null,
            onSuccess = { loaded = true }, onError = { loaded = false })
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
internal fun DesktopTabletDetailHeader(
    entry: CatalogEntry,
    tracks: List<DesktopTrack>,
    artwork: DesktopDetailArtwork,
    heroHeight: Dp,
    playing: Boolean,
    signedIn: Boolean,
    onPlay: (List<CatalogEntry.Track>, Boolean) -> Unit,
    onFavorite: () -> Unit,
    onAbout: () -> Unit,
    onSignIn: () -> Unit,
    contentGutter: Dp = 40.dp,
) {
    val palette = desktopPalette()
    val controller: DesktopMusicController = koinInject()
    val downloads: DesktopDownloadManager = koinInject()
    val backdrop = rememberLayerBackdrop()
    var menu by remember { mutableStateOf(false) }
    val play = { onPlay(tracks.map { it.catalogTrack }, false) }
    if (entry is CatalogEntry.Artist) {
        val hero = artwork.artist?.hero
        val playFill = artwork.artist?.keyColor?.let { Color(it or 0xFF000000.toInt()) } ?: Color.White
        Box(Modifier.fillMaxWidth().height(heroHeight).bleedHorizontally(contentGutter).layerBackdrop(backdrop)) {
            if (hero != null) {
                RemoteArtwork(hero.sized(1600), Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize().padding(bottom = 180.dp), contentAlignment = Alignment.Center) {
                    RemoteArtwork(entry.artworkUrls.firstOrNull(), Modifier.size(190.dp).clip(CircleShape).border(.5.dp, palette.separator, CircleShape))
                }
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
                0f to Color.Black.copy(alpha = 0.05f), 0.5f to Color.Transparent, 0.83f to palette.background.copy(alpha = 0.72f), 1f to palette.background)))
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 40.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DesktopArtistWordmark(entry.title, artwork, palette.foreground, Modifier.widthIn(max = 480.dp).fillMaxWidth().height(82.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    TabletCircle(DesktopGlyphKind.INFO, "About ${entry.title}", palette.foreground, palette.foreground.copy(alpha = 0.09f), 36.dp, onAbout)
                    TabletCircle(if (playing) DesktopGlyphKind.PAUSE else DesktopGlyphKind.PLAY,
                        if (playing) "Pause" else "Play", palette.background, playFill, 56.dp, play, enabled = tracks.isNotEmpty())
                    TabletCircle(if (artwork.following) DesktopGlyphKind.FAVORITED else DesktopGlyphKind.FAVORITE,
                        if (artwork.following) "Remove artist from favorites" else "Favorite artist", palette.foreground,
                        palette.foreground.copy(alpha = 0.09f), 36.dp, { if (signedIn) onFavorite() else onSignIn() }, enabled = !artwork.updatingFollow)
                }
                artwork.message?.let { Text(it, color = palette.muted, fontSize = 12.sp) }
            }
        }
        return
    }
    BoxWithConstraints(Modifier.fillMaxWidth().layerBackdrop(backdrop).padding(top = 8.dp, bottom = 34.dp)) {
      val coverSize = minOf(270.dp, maxWidth * .36f)
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(34.dp), verticalAlignment = Alignment.CenterVertically) {
        val image = artwork.album?.sized(1200) ?: entry.artworkUrls.firstOrNull()
        RemoteArtwork(image, Modifier.size(coverSize).shadow(18.dp, RoundedCornerShape(9.dp)).clip(RoundedCornerShape(9.dp)).background(palette.surface))
        Column(Modifier.weight(1f).heightIn(min = coverSize), verticalArrangement = Arrangement.Center) {
        Text(entry.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp,
            letterSpacing = (-.6).sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        val creator = when (entry) {
            is CatalogEntry.Album -> entry.artists.joinToString { it.name }
            is CatalogEntry.Playlist -> entry.author?.name.orEmpty()
            else -> ""
        }
        if (creator.isNotBlank()) Text(creator, color = palette.accent, fontSize = 24.sp, lineHeight = 30.sp,
            maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        val totalMs = tracks.sumOf { it.durationMs }
        val metadata = listOfNotNull((entry as? CatalogEntry.Album)?.year,
            "${tracks.size} songs", totalMs.takeIf { it > 0 }?.let { "${it / 60000} min" }).joinToString(" \u00B7 ")
        Text(metadata, color = palette.muted, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
        (entry as? CatalogEntry.Playlist)?.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = palette.muted, fontSize = 13.sp, lineHeight = 20.sp, maxLines = 3, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 20.dp))
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.padding(top = 17.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            TabletCircle(DesktopGlyphKind.SHUFFLE, "Shuffle", palette.foreground, palette.foreground.copy(alpha = 0.08f), 36.dp,
                { onPlay(tracks.map { it.catalogTrack }, true) }, enabled = tracks.isNotEmpty())
            Row(Modifier.width(130.dp).height(36.dp).clip(RoundedCornerShape(25.dp)).background(palette.foreground)
                .desktopInteractive(enabled = tracks.isNotEmpty(), onClick = play),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                DesktopGlyph(if (playing) DesktopGlyphKind.PAUSE else DesktopGlyphKind.PLAY, palette.background, 19.dp)
                Spacer(Modifier.width(7.dp))
                Text(if (playing) "Pause" else "Play", color = palette.background, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Box {
                TabletCircle(DesktopGlyphKind.MORE, "More options", palette.foreground, palette.foreground.copy(alpha = 0.08f), 36.dp, { menu = true })
                dev.nucleusframework.macoscompose.components.DropdownMenu(menu, { menu = false }) {
                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = {
                        downloads.enqueue(tracks.map { it.request }); menu = false
                    }) { Text("Download") }
                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = {
                        controller.appendQueue(tracks.map { it.request }); menu = false
                    }) { Text("Add to queue") }
                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = {
                        val link = when (entry) {
                            is CatalogEntry.Album -> "https://music.youtube.com/browse/${entry.id.value}"
                            is CatalogEntry.Playlist -> "https://music.youtube.com/playlist?list=${entry.id.value}"
                            else -> "https://music.youtube.com"
                        }
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(link), null)
                        menu = false
                    }) { Text("Copy link") }
                }
            }
        }
        }
      }
    }
}

@Composable
internal fun TabletCircle(kind: DesktopGlyphKind, label: String, tint: Color, fill: Color, size: Dp, onClick: () -> Unit, enabled: Boolean = true) {
    Box(Modifier.size(size).clip(CircleShape).background(fill.copy(alpha = if (enabled) fill.alpha else fill.alpha * .4f))
        .semantics { contentDescription = label }
        .desktopInteractive(enabled = enabled, scale = .92f, hoverColor = tint.copy(alpha = .08f), onClick = onClick),
        contentAlignment = Alignment.Center) {
        DesktopGlyph(kind, tint.copy(alpha = if (enabled) tint.alpha else tint.alpha * .4f), if (size >= 50.dp) 24.dp else 18.dp)
    }
}

private fun Modifier.bleedHorizontally(gutter: Dp) = layout { measurable, constraints ->
    val extra = gutter.roundToPx() * 2
    val child = measurable.measure(constraints.copy(minWidth = constraints.minWidth + extra, maxWidth = constraints.maxWidth + extra))
    layout(child.width - extra, child.height) { child.placeRelative(-extra / 2, 0) }
}
