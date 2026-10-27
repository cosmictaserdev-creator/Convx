package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogSearchFilter
import com.convx.music.catalog.presentation.CatalogSearchStateHolder
import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.SegmentedControl
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import zone.ien.hig.glass.liquidGlass
import zone.ien.hig.glass.LiquidGlassMaterials

@Composable
internal fun DesktopSearchPage(
    search: CatalogSearchStateHolder,
    filter: CatalogSearchFilter,
    onSubmit: (String) -> Unit,
    onFilter: (CatalogSearchFilter) -> Unit,
    onSuggestedEntry: (CatalogEntry) -> Unit,
    modifier: Modifier = Modifier,
    results: @Composable () -> Unit,
) {
    val state by search.state.collectAsState()
    val palette = desktopPalette()
    val reduced = LocalDesktopReducedMotion.current
    val suggestionsBackdrop = rememberLayerBackdrop()
    BoxWithConstraints(modifier) {
      val suggestionStart = ((maxWidth - 640.dp) / 2).coerceAtLeast(40.dp)
      Column(Modifier.fillMaxSize().layerBackdrop(suggestionsBackdrop).padding(top = 64.dp)) {
        if (state.query.isNotBlank() && state.online && !state.isEditing) Box(Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 8.dp)) {
            SegmentedControl(options = listOf("All", "Songs", "Albums", "Artists", "Playlists"), selectedIndex = filter.ordinal,
                onSelectedIndexChange = { onFilter(CatalogSearchFilter.entries[it]) })
        }
        if (state.query.isBlank() && state.recentQueries.isNotEmpty()) {
            Column(Modifier.padding(horizontal = 40.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Recently Searched", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    PushButton(onClick = search::clearHistory, style = PushButtonStyle.Borderless) { Text("Clear", color = palette.accent, fontSize = 13.sp) }
                }
                state.recentQueries.take(3).forEach { text -> SearchSuggestionRow(text, DesktopGlyphKind.HISTORY, { onSubmit(text) }, { search.removeRecent(text) }) }
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) { results() }
      }
      AnimatedVisibility(state.isEditing && state.query.isNotBlank(),
        enter = fadeIn(tween(if (reduced) 0 else 140)) + scaleIn(tween(if (reduced) 0 else 180), initialScale = if (reduced) 1f else .98f),
        exit = fadeOut(tween(if (reduced) 0 else 100)) + scaleOut(tween(if (reduced) 0 else 120), targetScale = if (reduced) 1f else .98f),
        modifier = Modifier.padding(start = suggestionStart, top = 62.dp, end = 24.dp)) {
            LazyColumn(Modifier.width(390.dp).heightIn(max = 650.dp)
                .shadow(24.dp, RoundedCornerShape(7.dp)).clip(RoundedCornerShape(7.dp))
                .liquidGlass(suggestionsBackdrop, RoundedCornerShape(7.dp), LiquidGlassMaterials.MiniPlayer.copy(
                    tint = palette.surface.copy(alpha = .6f), blurRadius = 24.dp, saturation = 1.8f,
                    refractionAmount = 0.dp, chromaticAberration = false))
                .border(.5.dp, palette.separator, RoundedCornerShape(7.dp)),
                contentPadding = PaddingValues(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = "submit") { SearchSuggestionRow(state.query, DesktopGlyphKind.SEARCH, { onSubmit(state.query) }) }
                val recent = state.recentQueries.filter { it.contains(state.query.trim(), true) }.take(5)
                items(recent, key = { "recent:$it" }) { text -> SearchSuggestionRow(text, DesktopGlyphKind.HISTORY, { onSubmit(text) }, { search.removeRecent(text) }) }
                items(state.suggestions.filterNot { suggestion -> recent.any { it.equals(suggestion, true) } }, key = { "suggestion:$it" }) { text ->
                    SearchSuggestionRow(text, DesktopGlyphKind.SEARCH, { onSubmit(text) })
                }
                if (state.isLoadingSuggestions) item(key = "loading") {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DesktopSkeletonBlock(Modifier.size(24.dp))
                        DesktopSkeletonBlock(Modifier.width(180.dp).height(14.dp))
                    }
                }
                items(state.suggestedEntries, key = { "entry:${it.id}" }) { entry ->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
                        .desktopInteractive(scale = 1f, hoverColor = palette.accent.copy(alpha = .2f)) { onSuggestedEntry(entry) }
                        .padding(horizontal = 6.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        com.convx.music.catalog.presentation.RemoteArtwork(entry.artworkUrls.firstOrNull(), Modifier.size(42.dp)
                            .clip(if (entry is CatalogEntry.Artist) androidx.compose.foundation.shape.CircleShape else RoundedCornerShape(4.dp)))
                        Column(Modifier.weight(1f).padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(entry.title, fontSize = 13.sp, maxLines = 1)
                            Text(when (entry) { is CatalogEntry.Track -> "Song \u00B7 " + entry.artists.joinToString { it.name }; is CatalogEntry.Artist -> "Artist"; is CatalogEntry.Album -> "Album"; is CatalogEntry.Playlist -> "Playlist"; else -> "Browse" }, color = palette.muted, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }
            }
      }
    }
}

@Composable
private fun SearchSuggestionRow(text: String, glyph: DesktopGlyphKind, onClick: () -> Unit, onRemove: (() -> Unit)? = null) {
    val palette = desktopPalette()
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        PushButton(onClick = onClick, style = PushButtonStyle.Borderless, modifier = Modifier.weight(1f)) {
            DesktopGlyph(glyph, palette.muted, 17.dp)
            Text(text, modifier = Modifier.weight(1f).padding(horizontal = 12.dp), fontSize = 15.sp, maxLines = 1)
            DesktopGlyph(DesktopGlyphKind.FORWARD, palette.muted, 12.dp)
        }
        onRemove?.let { PlayerGlyphButton(DesktopGlyphKind.REMOVE, "Remove $text from search history", size = 32, icon = 12, onClick = it) }
    }
}
