package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.SegmentedControl

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.catalog.*
import com.convx.music.catalog.presentation.CatalogLibraryActionState
import com.convx.music.catalog.presentation.CatalogScreenState
import com.convx.music.catalog.presentation.RemoteArtwork
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun DesktopCatalogPage(
    destination: DesktopDestination,
    state: CatalogScreenState,
    tracks: List<DesktopTrack>,
    playingId: String?,
    isPlaying: Boolean,
    signedIn: Boolean,
    library: CatalogLibraryActionState,
    onTrackSelected: (String) -> Unit,
    onEntrySelected: (CatalogEntry) -> Unit,
    onPlaySelection: (List<CatalogEntry.Track>, Boolean) -> Unit,
    onSearchFilterSelected: (CatalogSearchFilter) -> Unit,
    onHomeFilterSelected: (CatalogEntry.Browse?) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onSignIn: () -> Unit,
    onSetSaved: (CatalogId, Boolean, Boolean) -> Unit,
    onAddToPlaylist: (CatalogEntry.Track) -> Unit,
    modifier: Modifier = Modifier,
    showPageHeader: Boolean = true,
    topInset: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val entry = state.currentEntry
    val detail: DesktopDetailArtworkStateHolder = koinInject()
    val artwork by detail.state.collectAsState()
    LaunchedEffect(entry?.id, signedIn) { detail.load(entry, signedIn) }
    DisposableEffect(detail) { onDispose(detail::close) }
    DesktopArtistTheme(entry is CatalogEntry.Artist, artwork) {
    val pageSettings: com.convx.music.playback.presentation.PlaybackSettingsStateHolder = koinInject()
    val preferences by pageSettings.state.collectAsState()
    val palette = desktopPalette()
    val isSearch = destination == DesktopDestination.SEARCH && entry == null
    val isHome = destination == DesktopDestination.HOME && entry == null
    val libraryPage = destination in listOf(DesktopDestination.LIBRARY, DesktopDestination.SONGS, DesktopDestination.LIKED,
        DesktopDestination.ALBUMS, DesktopDestination.ARTISTS, DesktopDestination.PLAYLISTS, DesktopDestination.UPLOADS, DesktopDestination.HISTORY)
    val accountRequired = libraryPage && !signedIn && entry == null
    var alphabetical by remember(destination, entry?.id) { mutableStateOf(false) }
    var collectionQuery by remember(entry?.id) { mutableStateOf("") }
    val visibleTracks = remember(tracks, collectionQuery) { tracks.filter {
        collectionQuery.isBlank() || "${it.snapshot.title} ${it.snapshot.artists.joinToString()} ${it.album}".contains(collectionQuery, ignoreCase = true)
    } }

    LaunchedEffect(destination, entry?.id, state.searchFilter, state.selectedHomeFilter) { list.scrollToItem(0) }
    LaunchedEffect(state.continuationToken, state.isLoading, state.isLoadingMore, state.continuationError) {
        if (state.continuationToken == null || state.isLoading || state.isLoadingMore || state.continuationError != null) return@LaunchedEffect
        snapshotFlow {
            val info = list.layoutInfo
            info.totalItemsCount > 0 && (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 3
        }.first { it }
        onLoadMore()
    }

    BoxWithConstraints(modifier.background(palette.background)) {
        val gutter = if (maxWidth < 900.dp) 25.dp else 40.dp
        val showTableColumns = maxWidth >= 880.dp
        val categoryColumns = ((maxWidth - 80.dp) / 220.dp).toInt().coerceAtLeast(2)
        val categoryWidth = (maxWidth - 80.dp - 20.dp * (categoryColumns - 1)) / categoryColumns
        val heroHeight = if (artwork.artist?.hero == null) (maxHeight * .55f).coerceIn(380.dp, 480.dp) else (maxHeight * .72f).coerceIn(460.dp, 680.dp)
        LazyColumn(
            state = list,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = gutter, end = gutter, top = topInset + if (entry is CatalogEntry.Artist) 0.dp else 12.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (entry is CatalogEntry.Playlist) item(key = "collection-search") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    SearchField(collectionQuery, { collectionQuery = it }, Modifier.widthIn(max = 206.dp).padding(bottom = 6.dp), placeholder = "Find in Playlist")
                }
            }
            if (showPageHeader || entry != null) item(key = "header") {
                if (entry != null) DesktopTabletDetailHeader(entry, tracks, artwork, heroHeight,
                    isPlaying && tracks.any { it.request.id == playingId }, signedIn, onPlaySelection,
                    onFavorite = { detail.setFollowing(!artwork.following) },
                    onAbout = { scope.launch { list.animateScrollToItem((list.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)) } }, onSignIn = onSignIn, contentGutter = gutter)
                else Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(destination.title, fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.7).sp)
                    Spacer(Modifier.weight(1f))
                    if (libraryPage && signedIn) CatalogTextButton(if (alphabetical) "A–Z" else "Recent") { alphabetical = !alphabetical }
                }
            }
            if (isSearch && showPageHeader) item(key = "search-filters") {
                CatalogFilterRow(CatalogSearchFilter.entries.map { it to when (it) {
                    CatalogSearchFilter.ALL -> "All"
                    CatalogSearchFilter.TRACKS -> "Songs"
                    CatalogSearchFilter.ALBUMS -> "Albums"
                    CatalogSearchFilter.ARTISTS -> "Artists"
                    CatalogSearchFilter.PLAYLISTS -> "Playlists"
                } }, state.searchFilter, onSearchFilterSelected)
            }
            if (isHome && preferences.appearance.showHomeFilters && state.homeFilters.isNotEmpty()) item(key = "home-filters") {
                CatalogFilterRow(listOf<Pair<CatalogEntry.Browse?, String>>(null to "All") + state.homeFilters.map { it to it.title },
                    state.selectedHomeFilter, onHomeFilterSelected)
            }
            if (accountRequired) item(key = "account") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Your music, all together.", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Text("Sign in to see your ${destination.title.lowercase()}.", color = palette.muted, fontSize = 14.sp)
                    CatalogActionButton("Sign in", onSignIn)
                }
            }
            if (state.isLoading && state.sections.isEmpty()) item(key = "loading") { DesktopPageSkeleton() }
            if (!accountRequired) state.message?.let { message -> item(key = "error") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(message, color = palette.muted, fontSize = 14.sp)
                    CatalogTextButton("Try again", onRetry)
                }
            } }
            if (!state.isLoading && !accountRequired && state.sections.isEmpty() && state.message == null) item(key = "empty") {
                Text(when {
                    isSearch && state.searchQuery.isBlank() -> "Search songs, artists, albums, and playlists."
                    isSearch -> "No results for “${state.searchQuery}”."
                    else -> "Your music will appear here."
                }, color = palette.muted, fontSize = 15.sp)
            }

            val detailTracks = entry is CatalogEntry.Album || entry is CatalogEntry.Playlist
            if (detailTracks) {
                if (tracks.isNotEmpty()) item(key = "table-heading") {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Song", Modifier.weight(1f), color = palette.muted, fontSize = 12.sp)
                        if (showTableColumns) {
                            Text("Artist", Modifier.width(180.dp), color = palette.muted, fontSize = 12.sp)
                            Text("Album", Modifier.width(180.dp), color = palette.muted, fontSize = 12.sp)
                        }
                        Text("Time", Modifier.width(86.dp), color = palette.muted, fontSize = 12.sp)
                    }
                }
                itemsIndexed(if (alphabetical) visibleTracks.sortedBy { it.snapshot.title.lowercase() } else visibleTracks,
                    key = { index, track -> "detail:$index:${track.request.id}" }) { index, track ->
                    CatalogSongRow(track.catalogTrack, track, playingId, isPlaying, library, signedIn,
                        onTrackSelected, onSignIn, onSetSaved, onAddToPlaylist, index + 1, showColumns = showTableColumns)
                }
                state.sections.filter { section -> section.entries.any { it !is CatalogEntry.Track } }.forEachIndexed { index, section ->
                    item(key = "related:$index:${section.id}") {
                        CatalogShelf(section, tracks, playingId, isPlaying, library, signedIn, onTrackSelected,
                            onEntrySelected, onSignIn, onSetSaved, onAddToPlaylist, false)
                    }
                }
            } else if (!accountRequired) {
                state.sections.forEachIndexed { sectionIndex, section ->
                    val entries = if (alphabetical) section.entries.sortedBy { it.title.lowercase() } else section.entries
                    if (entries.isEmpty()) return@forEachIndexed
                    val songSection = entries.all { it is CatalogEntry.Track }
                    val table = songSection && (isSearch || destination in listOf(DesktopDestination.SONGS, DesktopDestination.LIKED, DesktopDestination.UPLOADS, DesktopDestination.HISTORY))
                    if (isSearch && state.searchQuery.isBlank() && entries.all { it is CatalogEntry.Browse }) {
                        item(key = "categories-heading:$section.id") {
                            Text(section.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 26.dp, bottom = 14.dp))
                        }
                        itemsIndexed(entries.chunked(categoryColumns), key = { row, _ -> "categories:${section.id}:$row" }) { _, row ->
                            Row(Modifier.padding(bottom = 18.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                row.forEach { category ->
                                    val colors = listOf(0xFFFF375F, 0xFFAF52DE, 0xFF9255A8, 0xFF00A98F, 0xFFE85939, 0xFF997300)
                                    val color = Color(colors[(category.title.hashCode() and Int.MAX_VALUE) % colors.size])
                                    Box(Modifier.width(categoryWidth).height(categoryWidth * .56f).clip(RoundedCornerShape(9.dp))
                                        .background(color).desktopInteractive { onEntrySelected(category) }) {
                                        RemoteArtwork(category.artworkUrls.firstOrNull(), Modifier.fillMaxSize())
                                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .15f)))))
                                        Text(category.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), maxLines = 2)
                                    }
                                }
                            }
                        }
                    } else if (table) {
                        item(key = "title:$sectionIndex:${section.id}") { Box(Modifier.padding(top = 28.dp, bottom = 12.dp)) { CatalogShelfHeading(section, onEntrySelected) } }
                        itemsIndexed(entries.filterIsInstance<CatalogEntry.Track>(), key = { index, song -> "song:$sectionIndex:$index:${song.id.value}" }) { index, song ->
                            CatalogSongRow(song, tracks.firstOrNull { it.catalogTrack.id == song.id }, playingId, isPlaying,
                                library, signedIn, onTrackSelected, onSignIn, onSetSaved, onAddToPlaylist, if (entry != null) index + 1 else null)
                        }
                    } else item(key = "shelf:$sectionIndex:${section.id}") {
                        CatalogShelf(section.copy(entries = entries), tracks, playingId, isPlaying, library, signedIn,
                            onTrackSelected, onEntrySelected, onSignIn, onSetSaved, onAddToPlaylist,
                            featured = isHome && section.id.startsWith("home-extra-0-"),
                            songsPerColumn = if (entry is CatalogEntry.Artist) 3 else 4,
                            editorial = destination == DesktopDestination.EXPLORE && sectionIndex == 0)
                    }
                }
            }
            if (state.isLoadingMore) item(key = "loading-more") { Text("Loading more…", color = palette.muted, fontSize = 13.sp) }
            state.continuationError?.let { message -> item(key = "more-error") {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(message, color = palette.muted, fontSize = 13.sp)
                    CatalogTextButton("Try again", onLoadMore)
                }
            } }
            (entry as? CatalogEntry.Artist)?.description?.takeIf { it.isNotBlank() }?.let { bio -> item(key = "about") {
                Column(Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("About ${entry.title}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(bio, color = palette.muted, fontSize = 15.sp, lineHeight = 23.sp)
                }
            } }
        }
        VerticalScrollbar(rememberScrollbarAdapter(list), Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 8.dp),
            style = ScrollbarStyle(16.dp, 6.dp, RoundedCornerShape(3.dp), 120,
                palette.foreground.copy(alpha = 0.16f), palette.foreground.copy(alpha = 0.34f)))
    }
    }
}

@Composable
private fun CatalogShelfHeading(section: CatalogSection, onEntry: (CatalogEntry) -> Unit, size: androidx.compose.ui.unit.TextUnit = 17.sp) {
    val palette = desktopPalette()
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            section.subtitle?.let { Text(it, color = palette.muted, fontSize = 11.sp) }
            Text(section.title, fontSize = size, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp)
        }
        section.more?.let { more -> CatalogTextButton("See all", { onEntry(more) }) }
    }
}

@Composable
private fun CatalogShelf(
    section: CatalogSection, tracks: List<DesktopTrack>, playingId: String?, isPlaying: Boolean,
    library: CatalogLibraryActionState, signedIn: Boolean, onTrack: (String) -> Unit, onEntry: (CatalogEntry) -> Unit,
    onSignIn: () -> Unit, onSetSaved: (CatalogId, Boolean, Boolean) -> Unit, onPlaylist: (CatalogEntry.Track) -> Unit,
    featured: Boolean, songsPerColumn: Int = 4, editorial: Boolean = false,
) {
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val songs = !featured && section.entries.all { it is CatalogEntry.Track && !it.isVideo }
    val groups = remember(section.entries, songs) { section.entries.chunked(if (songs) songsPerColumn else 1) }
    BoxWithConstraints {
    val adaptiveWidth = if (editorial) minOf(440.dp, (maxWidth - 20.dp) / 2.5f)
        else if (featured) minOf(254.dp, (maxWidth - 32.dp) / 3.2f)
        else minOf(212.dp, (maxWidth - 48.dp) / 4.1f)
    val headingSize = if (maxWidth < 900.dp) 14.sp else 17.sp
    Column(Modifier.padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { CatalogShelfHeading(section, onEntry, headingSize) }
            if (list.canScrollBackward || list.canScrollForward) Row(Modifier.padding(start = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShelfArrow("‹", "Previous items", list.canScrollBackward) { scope.launch { list.animateScrollToItem((list.firstVisibleItemIndex - 3).coerceAtLeast(0)) } }
                ShelfArrow("›", "Next items", list.canScrollForward) { scope.launch { list.animateScrollToItem((list.firstVisibleItemIndex + 3).coerceAtMost(groups.lastIndex)) } }
            }
        }
        LazyRow(state = list, horizontalArrangement = Arrangement.spacedBy(if (songs) 22.dp else 18.dp)) {
            itemsIndexed(groups, key = { index, items -> "$index:${items.first().id.value}" }) { _, group ->
                if (songs) Column(Modifier.width(332.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    group.filterIsInstance<CatalogEntry.Track>().forEach { song ->
                        CatalogSongRow(song, tracks.firstOrNull { it.catalogTrack.id == song.id }, playingId, isPlaying,
                            library, signedIn, onTrack, onSignIn, onSetSaved, onPlaylist, compact = true)
                    }
                } else {
                    val item = group.first()
                    CatalogArtworkCard(item, featured, playingId == item.id.value && isPlaying, editorial, adaptiveWidth) {
                        if (item is CatalogEntry.Track) onTrack(item.id.value) else onEntry(item)
                    }
                }
            }
        }
    }
}
}

@Composable
private fun CatalogArtworkCard(entry: CatalogEntry, featured: Boolean, playing: Boolean, editorial: Boolean = false, adaptiveWidth: androidx.compose.ui.unit.Dp? = null, onClick: () -> Unit) {
    val palette = desktopPalette()
    val artist = entry is CatalogEntry.Artist
    val video = entry is CatalogEntry.Track && entry.isVideo
    val width = if (artist) 118.dp else adaptiveWidth ?: if (editorial) 360.dp else if (featured) 254.dp else if (video) 300.dp else 212.dp
    val shape = if (artist) CircleShape else RoundedCornerShape(8.dp)
    val subtitle = when (entry) {
        is CatalogEntry.Track -> entry.artists.joinToString { it.name }
        is CatalogEntry.Album -> entry.artists.joinToString { it.name }
        is CatalogEntry.Artist -> entry.subtitle.orEmpty()
        is CatalogEntry.Playlist -> entry.description.orEmpty()
        is CatalogEntry.Browse -> ""
    }
    Column(Modifier.width(width).desktopInteractive(scale = .985f, onClick = onClick), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        if (editorial) {
            Text(entry.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = palette.muted, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(Modifier.fillMaxWidth().height(if (editorial || video) width * .5625f else if (featured) width * 1.333f else width)
            .clip(shape).background(palette.surface)) {
            RemoteArtwork(entry.artworkUrls.firstOrNull(), Modifier.fillMaxSize())
            if (featured) {
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.78f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(entry.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (playing) Box(Modifier.align(Alignment.BottomEnd).padding(8.dp).background(palette.accent, CircleShape).padding(8.dp)) {
                Text("Ⅱ", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (!featured && !editorial) {
            Text(entry.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis,
                textAlign = if (artist) TextAlign.Center else TextAlign.Start, modifier = Modifier.fillMaxWidth())
            if (subtitle.isNotBlank()) Text(subtitle, color = palette.muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                textAlign = if (artist) TextAlign.Center else TextAlign.Start, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun CatalogSongRow(
    song: CatalogEntry.Track, track: DesktopTrack?, playingId: String?, isPlaying: Boolean,
    library: CatalogLibraryActionState, signedIn: Boolean, onTrack: (String) -> Unit, onSignIn: () -> Unit,
    onSetSaved: (CatalogId, Boolean, Boolean) -> Unit, onPlaylist: (CatalogEntry.Track) -> Unit,
    number: Int? = null, compact: Boolean = false, showColumns: Boolean = false,
) {
    val palette = desktopPalette()
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val active = song.id.value == playingId
    val highlight by animateColorAsState(if (active) palette.foreground.copy(alpha = .08f) else Color.Transparent,
        tween(if (LocalDesktopReducedMotion.current) 0 else 120), label = "Song selection")
    Row(Modifier.fillMaxWidth().height(54.dp).drawBehind { drawLine(palette.separator, Offset(0f, size.height), Offset(size.width, size.height), .5.dp.toPx()) }
        .background(highlight).desktopInteractive(scale = 1f, hoverColor = palette.foreground.copy(alpha = .03f)) { onTrack(song.id.value) }.padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (number != null && !compact) Text(number.toString(), color = palette.muted, fontSize = 12.sp, modifier = Modifier.width(24.dp))
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(5.dp)).background(palette.surface)) {
            RemoteArtwork(song.artworkUrls.firstOrNull(), Modifier.fillMaxSize())
            if (active && isPlaying) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Text("Ⅱ", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(song.title, color = if (active) palette.accent else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!showColumns) Text(song.artists.joinToString { it.name }.ifBlank { song.album?.title.orEmpty() }, color = palette.muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (showColumns) {
            Text(song.artists.joinToString { it.name }, Modifier.width(180.dp), color = palette.muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.album?.title.orEmpty(), Modifier.width(180.dp), color = palette.muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (!compact) {
            Text(song.durationMillis?.let { "${it / 60000}:${((it / 1000) % 60).toString().padStart(2, '0')}" } ?: "—", color = palette.muted, fontSize = 12.sp, modifier = Modifier.width(42.dp))
            track?.let { DesktopTrackLibraryAction(it, library, signedIn, onSignIn, onSetSaved, onPlaylist) }
        }
    }
}

@Composable
private fun <T> CatalogFilterRow(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    SegmentedControl(
        options = options.map { it.second }, selectedIndex = options.indexOfFirst { it.first == selected }.coerceAtLeast(0),
        onSelectedIndexChange = { onSelect(options[it].first) }, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun ShelfArrow(label: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    val palette = desktopPalette()
    Box(Modifier.size(28.dp).clip(CircleShape).clickable(enabled = enabled, role = Role.Button, onClickLabel = description, onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = palette.foreground.copy(alpha = if (enabled) 0.8f else 0.2f), fontSize = 27.sp)
    }
}

@Composable
private fun CatalogTextButton(label: String, onClick: () -> Unit) {
    PushButton(text = label, onClick = onClick,
        style = PushButtonStyle.Borderless)
}

@Composable
private fun CatalogActionButton(label: String, onClick: () -> Unit, secondary: Boolean = false) {
    PushButton(text = label, onClick = onClick,
        style = if (secondary) PushButtonStyle.Secondary
            else PushButtonStyle.Default)
}
